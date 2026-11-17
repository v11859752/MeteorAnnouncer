package ru.example.meteor;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.block.BlockFace;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;

public class LoudspeakerListener implements Listener {

    private final MeteorPlugin plugin;

    public LoudspeakerListener(MeteorPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (!LoudspeakerItem.isLoudspeaker(event.getItemInHand())) return;
        Location loc = event.getBlockPlaced().getLocation();
        plugin.getStorage().add(loc);
        event.getPlayer().sendMessage(Component.text("§aSpeaker is installed"));
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Location loc = event.getBlock().getLocation();
        if (!plugin.getStorage().contains(loc)) return;
        event.setDropItems(false);
        event.getBlock().getWorld().dropItemNaturally(loc, LoudspeakerItem.create());
        plugin.getStorage().remove(loc);
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent event) {
        event.blockList().removeIf(block -> {
            Location loc = block.getLocation();
            if (plugin.getStorage().contains(loc)) {
                block.getWorld().dropItemNaturally(loc, LoudspeakerItem.create());
                plugin.getStorage().remove(loc);
                return true;
            }
            return false;
        });
    }

    @EventHandler
    public void onPistonExtend(BlockPistonExtendEvent event) {
        for (var block : event.getBlocks()) {
            Location loc = block.getLocation();
            if (plugin.getStorage().contains(loc)) {
                plugin.getStorage().remove(loc);
                BlockFace direction = event.getDirection();
                plugin.getStorage().add(loc.clone().add(
                        direction.getModX(), direction.getModY(), direction.getModZ()));
            }
        }
    }

    @EventHandler
    public void onPistonRetract(BlockPistonRetractEvent event) {
        for (var block : event.getBlocks()) {
            Location loc = block.getLocation();
            if (plugin.getStorage().contains(loc)) {
                plugin.getStorage().remove(loc);
                BlockFace direction = event.getDirection().getOppositeFace();
                plugin.getStorage().add(loc.clone().add(
                        direction.getModX(), direction.getModY(), direction.getModZ()));
            }
        }
    }
}
