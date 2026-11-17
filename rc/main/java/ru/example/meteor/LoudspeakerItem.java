package ru.example.meteor;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public final class LoudspeakerItem {

    private static NamespacedKey key() {
        return new NamespacedKey(MeteorPlugin.getInstance(), "loudspeaker");
    }

    public static ItemStack create() {
        ItemStack item = new ItemStack(Material.NOTE_BLOCK);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("§bSpeaker"));
        meta.getPersistentDataContainer().set(key(), PersistentDataType.BYTE, (byte) 1);
        item.setItemMeta(meta);
        return item;
    }

    public static boolean isLoudspeaker(ItemStack item) {
        if (item == null || item.getType() != Material.NOTE_BLOCK) return false;
        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.getPersistentDataContainer().has(key(), PersistentDataType.BYTE);
    }
}