package alien.twitchIntegration;

import org.bukkit.entity.EntityType;

import java.util.ArrayList;

public class Action {
    String id;
    ArrayList<Resualt> resualts = new ArrayList<>();



    class Resualt{
        final Types type;
        final int odds;

        public Resualt(Types type, int odds) {
            this.type = type;
            this.odds = odds;
        }

        public boolean doAction(){
            if(type == Types.SPAWN){

            }
            return false;
        }
    }
    enum Types{
        SPAWN,
        ITEM,
        TELEPORT
    }
}
