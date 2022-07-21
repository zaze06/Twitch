package me.alien.yello.custome.combat;

import me.alien.yello.Main;
import me.alien.yello.enchantments.Enchantments;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.UUID;

import static me.alien.yello.Main.TOOLS;
import static me.alien.yello.Main.combat;
import static me.alien.yello.util.RomanNumber.toRoman;

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
        lore.add(Component.text(""));
        lore.add(MiniMessage.miniMessage().deserialize("<!i><gray>Item rarity"));
        lore.add(finalRarity.getText());

        for(AttributeModifier attribute : itemStack.getType().getDefaultAttributeModifiers(EquipmentSlot.HAND).get(Attribute.GENERIC_ATTACK_DAMAGE)){
            itemMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, attribute);
        }

        switch (finalRarity) {
            case TRASH -> {
                itemMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier(UUID.randomUUID(), "test", -10, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND));
            }
            case COMMON -> {
                itemMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier(UUID.randomUUID(), "test", -6, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND));
            }
            case UNCOMMON -> {
                itemMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier(UUID.randomUUID(), "test", -2, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND));
            }
            case RARE -> {
                itemMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier(UUID.randomUUID(), "test", 3, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND));
                itemMeta.addEnchant(Enchantments.LIFE_STEAL, 1, false);
                lore.add(0, MiniMessage.miniMessage().deserialize("<!i><gray>"+Enchantments.LIFE_STEAL.getName()+" "+toRoman(itemMeta.getEnchantLevel(Enchantments.LIFE_STEAL))));
            }
            case LEGENDARY -> {
                itemMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier(UUID.randomUUID(), "test", 6, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND));
                itemMeta.addEnchant(Enchantments.LIFE_STEAL, 2, false);
                lore.add(0, MiniMessage.miniMessage().deserialize("<!i><gray>"+Enchantments.LIFE_STEAL.getName()+" "+toRoman(itemMeta.getEnchantLevel(Enchantments.LIFE_STEAL))));
            }
            case MYTHICAL -> {
                itemMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier(UUID.randomUUID(), "test", 10, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND));
                itemMeta.addEnchant(Enchantments.LIFE_STEAL, 3, false);
                lore.add(0, MiniMessage.miniMessage().deserialize("<!i><gray>"+Enchantments.LIFE_STEAL.getName()+" "+toRoman(itemMeta.getEnchantLevel(Enchantments.LIFE_STEAL))));
            }
        }

        ArrayList<Component> finalLore = new ArrayList<>(lore);
        if(itemMeta.hasLore()){
            finalLore.addAll(itemMeta.lore());
        }

        itemMeta.lore(finalLore);

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static Rarity getRarity(ItemStack itemStack){
        NamespacedKey key = new NamespacedKey(Main.plugin, "rarity");
        ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta.getPersistentDataContainer().has(key)){
            return Rarity.valueOf(itemMeta.getPersistentDataContainer().get(key, PersistentDataType.STRING));
        }
        return Rarity.NONE;
    }

    private static float getAttackDammage(@NotNull ItemStack itemStack) {
        return TOOLS.getJSONObject("WEAPON").getJSONObject(itemStack.getType().name()).getFloat("attackDamage");
    }
}
