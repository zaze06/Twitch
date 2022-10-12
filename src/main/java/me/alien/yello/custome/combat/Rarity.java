/*
 * MIT License
 *
 * Copyright (c) 2022. Zacharias Zellén
 */

package me.alien.yello.custome.combat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public enum Rarity {
    MYTHICAL(20),
    LEGENDARY(18),
    RARE(16),
    UNCOMMON(10),
    COMMON(7),
    TRASH(0),
    NONE(-1);

    final int minRarity;

    Rarity(int minRarity){
        this.minRarity = minRarity;
    }

    public static Rarity fromRarity(int rarity){
        if(rarity >= MYTHICAL.minRarity) return MYTHICAL;
        else if(rarity >= LEGENDARY.minRarity) return LEGENDARY;
        else if(rarity >= RARE.minRarity) return RARE;
        else if(rarity >= UNCOMMON.minRarity) return UNCOMMON;
        else if(rarity >= COMMON.minRarity) return COMMON;
        else if(rarity >= TRASH.minRarity) return TRASH;
        return null;
    }

    public Component getText() {
        if (this == MYTHICAL) return MiniMessage.miniMessage().deserialize("<obf>---<reset><!i><bold><rainbow>MYTHICAL</rainbow></bold><obf>---<reset>").applyFallbackStyle();
        if (this == LEGENDARY) return MiniMessage.miniMessage().deserialize("<gradient:#ffd300:#00f6ff>LEGENDARY");
        if (this == RARE) return MiniMessage.miniMessage().deserialize("<#f1c232>RARE");
        if (this == UNCOMMON) return MiniMessage.miniMessage().deserialize("<!i><#6aa84f>UNCOMMON");
        if (this == COMMON) return MiniMessage.miniMessage().deserialize("<!i><#134f5c>COMMON");
        if (this == TRASH) return MiniMessage.miniMessage().deserialize("<!i><#5b5b5b>TRASH");
        return null;
    }

    public int getMinRarity() {
        return minRarity;
    }
}
