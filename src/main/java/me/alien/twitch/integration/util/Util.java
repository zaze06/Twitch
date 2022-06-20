package me.alien.twitch.integration.util;

import org.bukkit.Material;

import java.util.Collection;
import java.util.function.Predicate;

public class Util {
    public static <T> T getElement(Collection<T> collection, Predicate<T> predicate){
        for(T t : collection){
            if(predicate.test(t)){
                return t;
            }
        }
        return null;
    }

    public static boolean isTool(Material itemType){
        return (itemType == Material.DIAMOND_SWORD || itemType == Material.GOLDEN_SWORD || itemType == Material.IRON_SWORD || itemType == Material.NETHERITE_SWORD);
    }
}
