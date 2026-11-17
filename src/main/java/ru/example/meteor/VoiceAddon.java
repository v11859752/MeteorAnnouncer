package ru.example.meteor;

import su.plo.voice.api.addon.AddonInitializer;
import su.plo.voice.api.addon.InjectPlasmoVoice;
import su.plo.voice.api.addon.annotation.Addon;
import su.plo.voice.api.server.PlasmoVoiceServer;

@Addon(id = "pv-addon-meteor", name = "MeteorAnnouncer", version = "1.0.0", authors = {"kisiqrt"})
public final class VoiceAddon implements AddonInitializer {

    private static PlasmoVoiceServer voiceServer;

    @InjectPlasmoVoice
    private PlasmoVoiceServer injected;

    @Override
    public void onAddonInitialize() {
        voiceServer = injected;
    }

    public void shutdown() {
    }

    public static PlasmoVoiceServer getVoiceServer() {
        return voiceServer;
    }
}