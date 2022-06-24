package me.alien.yello.integration.custome.combat;

import me.alien.yello.integration.Main;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import static me.alien.yello.integration.Main.combat;

public class Base {
    public static ItemStack handle(ItemStack result) {
        if(!combat) return null;
        int rarity = (int) (Math.random()*20);
        Rarity finalRarity = Rarity.fromRarity(rarity);
        if(finalRarity == null) finalRarity = Rarity.COMMON;
        NamespacedKey key = new NamespacedKey(Main.plugin, "rarity");
        ItemMeta itemMeta = result.getItemMeta();
        itemMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, finalRarity.name());
        result.setItemMeta(itemMeta);
        return result;
    }
}
