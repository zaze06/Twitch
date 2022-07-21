package me.alien.yello.enchantments;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;

import static me.alien.yello.Main.plugin;

public class Enchantments {
    public static final Enchantment LIFE_STEAL = new LifeSteal(new NamespacedKey(plugin, "life_steal"));
}
