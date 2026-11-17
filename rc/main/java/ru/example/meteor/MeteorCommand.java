package ru.example.meteor;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import su.plo.slib.api.server.position.ServerPos3d;

import java.util.Random;

public class MeteorCommand implements CommandExecutor {
    private final MeteorPlugin plugin;
    private final Random random = new Random();

    public MeteorCommand(MeteorPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("meteorannouncer.admin")) {
            sender.sendMessage("§cYou don't have rights to this command..");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage("§eUsing:");
            sender.sendMessage("§6/meteor start §7— Launch the meteorite");
            sender.sendMessage("§6/meteor give [nickname] §7— issue a speaker");
            return true;
        }

        if (args[0].equalsIgnoreCase("give")) {
            Player target;
            if (args.length >= 2) {
                target = Bukkit.getPlayerExact(args[1]);
                if (target == null) {
                    sender.sendMessage("§cThe player was not found");
                    return true;
                }
            } else if (sender instanceof Player p) {
                target = p;
            } else {
                sender.sendMessage("§cfrom the console: meteor give <nickname>");
                return true;
            }
            ItemStack item = LoudspeakerItem.create();
            target.getInventory().addItem(item).values()
                    .forEach(left -> target.getWorld().dropItemNaturally(target.getLocation(), left));
            sender.sendMessage("§aThe speaker has been issued " + target.getName() + ".");
            return true;
        }

        if (args[0].equalsIgnoreCase("start")) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("§claunching from the console is not possible.");
                return true;
            }
            Location adminLoc = player.getLocation();
            Location target = pickTarget(adminLoc);
            int delaySeconds = Math.max(0, plugin.getConfig().getInt("meteor.fall-delay-seconds", 25));
            int searchRadius = Math.max(0, plugin.getConfig().getInt("meteor.search-radius", 100));

            String audioFile = plugin.getConfig().getString("meteor.audio-file", "meteor_warning.wav");
            short[] samples = plugin.getAudioLoader().loadSamples(audioFile);
            if (samples == null) {
                player.sendMessage("§cNo audio was found. Check plugins/MeteorAnnouncer/audio/" + audioFile);
            } else {
                for (Location speaker : plugin.getStorage().getAll()) {
                    if (speaker.getWorld() == null || !speaker.getWorld().equals(target.getWorld())) continue;
                    if (speaker.distanceSquared(target) > (double) searchRadius * searchRadius) continue;
                    playAudio(speaker, samples);
                }
            }

            new MeteorTask(plugin, target).runTaskTimer(plugin, delaySeconds * 20L, 1L);
            player.sendMessage("§eThe meteorite is launched. Drop point: " +
                    String.format("%.1f, %.1f, %.1f", target.getX(), target.getY(), target.getZ()));
            return true;
        }

        sender.sendMessage("§cUnknown subcommand");
        return true;
    }

    private Location pickTarget(Location center) {
        double angle = random.nextDouble() * Math.PI * 2;
        double radius = Math.max(0, plugin.getConfig().getDouble("meteor.target-radius", 100));
        double distance = random.nextDouble() * radius;
        double x = center.getX() + Math.cos(angle) * distance;
        double z = center.getZ() + Math.sin(angle) * distance;
        int y = center.getWorld().getHighestBlockYAt((int) x, (int) z);
        return new Location(center.getWorld(), x, y, z);
    }

    private void playAudio(Location speaker, short[] samples) {
        var voiceServer = VoiceAddon.getVoiceServer();
        if (voiceServer == null) return;
        var line = voiceServer.getSourceLineManager().getLineByName("proximity").orElse(null);
        if (line == null) return;
        var world = voiceServer.getMinecraftServer().getWorlds().stream()
                .filter(w -> w.getName().equals(speaker.getWorld().getName()))
                .findFirst().orElse(null);
        if (world == null) return;
        var pos = new ServerPos3d(world, speaker.getX(), speaker.getY(), speaker.getZ());
        var source = line.createStaticSource(pos, false);
        var provider = new su.plo.voice.api.server.audio.provider.ArrayAudioFrameProvider(voiceServer, false);
        int listenRadius = Math.max(1, plugin.getConfig().getInt("meteor.listen-radius", 25));
        var sender = source.createAudioSender(provider, (short) listenRadius);
        provider.addSamples(samples);
        sender.start();
        sender.onStop(() -> {
            provider.close();
            source.remove();
        });
    }
}
