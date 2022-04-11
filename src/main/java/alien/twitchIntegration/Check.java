package alien.twitchIntegration;

import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Door;

public class Check {
    public boolean isDoor(BlockData blockData){
        return (blockData instanceof Door);
    }
}
