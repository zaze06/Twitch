package me.alien.yello.integration.custome.combat;

import me.alien.yello.integration.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.util.RGBLike;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;

import static me.alien.yello.integration.Main.combat;

public class Base {
    /**
     *
     * @param itemStack input ItemStack to add the Rarity too
     * @return input item stack or null if failed
     */
    @Nullable
    public static ItemStack handle(@NotNull ItemStack itemStack) {
        if(!combat) return null;
        int rarity = (int) (Math.random()*20);
        return handle(itemStack, rarity);
    }

    public static ItemStack handle(@NotNull ItemStack itemStack, int rarity){
        Rarity finalRarity = Rarity.fromRarity(rarity);
        if(finalRarity == null) finalRarity = Rarity.COMMON;
        NamespacedKey key = new NamespacedKey(Main.plugin, "rarity");
        ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta.getPersistentDataContainer().has(key)) return null;

        itemMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, finalRarity.name());
        ArrayList<Component> lore = new ArrayList<>();
        lore.add(Component.text("Item Rarity", TextColor.color(new Color(112, 112, 112).getRGB())));
        lore.add(finalRarity.getText());

        ArrayList<Component> finalLore = new ArrayList<>(lore);
        if(itemMeta.hasLore()){
            lore.addAll(itemMeta.lore());
        }

        itemMeta.lore(finalLore);

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
