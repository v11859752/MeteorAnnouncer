package ru.example.meteor;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class AudioLoader {

    private final MeteorPlugin plugin;

    public AudioLoader(MeteorPlugin plugin) {
        this.plugin = plugin;
    }

    public short[] loadSamples(String fileName) {
        File file = new File(plugin.getDataFolder(), "audio/" + fileName);
        if (!file.exists()) {
            plugin.getLogger().warning("file not found: " + file.getAbsolutePath());
            return null;
        }

        try (AudioInputStream originalStream = AudioSystem.getAudioInputStream(file)) {
            AudioFormat targetFormat = new AudioFormat(48000f, 16, 1, true, false);
            AudioInputStream converted = AudioSystem.getAudioInputStream(targetFormat, originalStream);

            byte[] bytes = converted.readAllBytes();
            short[] samples = new short[bytes.length / 2];
            for (int i = 0; i < samples.length; i++) {
                samples[i] = (short) ((bytes[i * 2] & 0xFF) | (bytes[i * 2 + 1] << 8));
            }
            return samples;
        } catch (UnsupportedAudioFileException | IOException e) {
            plugin.getLogger().severe("error file: " + e.getMessage());
            return null;
        }
    }
}