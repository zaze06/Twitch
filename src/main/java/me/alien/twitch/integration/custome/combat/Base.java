package me.alien.twitch.integration.custome.combat;

import io.github.bananapuncher714.nbteditor.NBTEditor;
import me.alien.twitch.integration.Main;
import net.kyori.adventure.text.NBTComponent;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.tags.ItemTagType;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class Base {
    public static ItemStack handle(ItemStack result) {
        int rarity = (int) (Math.random()*20);
        Rarity finalRarity = Rarity.fromRarity(rarity);
        NamespacedKey key = new NamespacedKey(Main.plugin, "rarity");
        ItemMeta itemMeta = result.getItemMeta();
        itemMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, finalRarity.name());
        result.setItemMeta(itemMeta);
        return result;
    }
}
