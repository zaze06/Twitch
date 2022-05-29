package me.alien.twitch.integration.util;

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
}
