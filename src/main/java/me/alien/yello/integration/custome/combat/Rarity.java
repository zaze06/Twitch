package me.alien.yello.integration.custome.combat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.checkerframework.checker.units.qual.C;

public enum Rarity {
    MYTHICAL(20),
    LEGENDARY(16),
    RARE(10),
    UNCOMMON(6),
    COMMON(4),
    TRASH(0);

    final int minRarity;
    Rarity rarity;

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
        if(this == MYTHICAL) return MiniMessage.miniMessage().deserialize("<!i><obf>---<reset><!i><bold><rainbow>MYTHICAL</rainbow></bold><obf>---<reset>").applyFallbackStyle();
        if(this == LEGENDARY) return MiniMessage.miniMessage().deserialize("<!i><gradient:#ffd300:#00f6ff>LEGENDARY");
        if(this == RARE) return MiniMessage.miniMessage().deserialize("<!i><#f1c232>RARE");
        if(this == UNCOMMON) return MiniMessage.miniMessage().deserialize("<#6aa84f>UNCOMMON");
        if(this == COMMON) return MiniMessage.miniMessage().deserialize("<#134f5c>COMMON");
        if(this == TRASH) return MiniMessage.miniMessage().deserialize("<#5b5b5b>TRASH");
        return null;
    }
}
