package me.alien.twitch.integration;

import java.util.ArrayList;
import java.util.Map;

public class Parser {
    public static <K, V extends Comparable<V> >
    Map.Entry<K, V>
    getTopValues(Map<K, V> map, int amount)
    {

        // To store the result
        ArrayList<Pair<K, V>> list = new ArrayList<>();

        // Iterate in the map to find the required entry
        for (Map.Entry<K, V> currentEntry : map.entrySet()) {
            for(int i = 0; i < 10; i++){
                //if(list.get(i).getValue() >)
            }
        }

        // Return the entry with highest value
        return null;//entryWithMaxValue;
    }
}
