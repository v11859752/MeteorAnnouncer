package ru.example.meteor;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import su.plo.voice.api.server.PlasmoVoiceServer;

import java.io.File;

public final class MeteorPlugin extends JavaPlugin {
    private static MeteorPlugin instance;
    private VoiceAddon voiceAddon;
    private LoudspeakerStorage storage;
    private AudioLoader audioLoader;

    @Override
    public void onLoad() {
        instance = this;
        voiceAddon = new VoiceAddon();
        PlasmoVoiceServer.getAddonsLoader().load(voiceAddon);
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        File audioDir = new File(getDataFolder(), "audio");
        if (!audioDir.exists() && audioDir.mkdirs()) {
            getLogger().info("Created audio folder. Put meteor_warning.wav there.");
        }
        storage = new LoudspeakerStorage(this);
        audioLoader = new AudioLoader(this);
        registerLoudspeakerRecipe();
        getServer().getPluginManager().registerEvents(new LoudspeakerListener(this), this);
        if (getCommand("meteor") != null) {
            getCommand("meteor").setExecutor(new MeteorCommand(this));
        } else {
            getLogger().severe("Command 'meteor' is missing from plugin.yml!");
        }
        getLogger().info("MeteorAnnouncer enabled. Voice API ready: " +
                (VoiceAddon.getVoiceServer() != null));
    }

    @Override
    public void onDisable() {
        if (voiceAddon != null) voiceAddon.shutdown();
    }

    private void registerLoudspeakerRecipe() {
        NamespacedKey key = new NamespacedKey(this, "loudspeaker");
        getServer().removeRecipe(key);
        ShapedRecipe recipe = new ShapedRecipe(key, LoudspeakerItem.create());
        recipe.shape("IRI", "RNR", "IRI");
        recipe.setIngredient('I', org.bukkit.Material.IRON_INGOT);
        recipe.setIngredient('R', org.bukkit.Material.REDSTONE);
        recipe.setIngredient('N', org.bukkit.Material.NOTE_BLOCK);
        boolean registered = getServer().addRecipe(recipe);
        getLogger().info("Loudspeaker recipe registration: " + registered);
    }

    public static MeteorPlugin getInstance() { return instance; }
    public LoudspeakerStorage getStorage() { return storage; }
    public AudioLoader getAudioLoader() { return audioLoader; }
}
