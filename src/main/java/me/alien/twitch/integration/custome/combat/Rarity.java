package me.alien.twitch.integration.custome.combat;

public enum Rarity {
    MYTHICAL(20),
    LEGENDARY(16),
    RARE(10),
    UNCOMMON(6),
    COMMON(4),
    TRASH(0);

    final int minRarity;

    Rarity(int minRarity){
        this.minRarity = minRarity;
    }

    public static Rarity fromRarity(int rarity){
        if(rarity >= TRASH.minRarity) return TRASH;
        else if(rarity >= COMMON.minRarity) return COMMON;
        else if(rarity >= UNCOMMON.minRarity) return UNCOMMON;
        else if(rarity >= RARE.minRarity) return RARE;
        else if(rarity >= LEGENDARY.minRarity) return LEGENDARY;
        else if(rarity >= MYTHICAL.minRarity) return MYTHICAL;
        return null;
    }
}
