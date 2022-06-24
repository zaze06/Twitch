package me.alien.yello.integration.events.mindsweper;

import me.alien.yello.integration.util.Vector2I;

public class Action {
    private boolean PlacingFlag;
    private Vector2I pos;

    public Action(boolean placingFlag, Vector2I pos) {
        PlacingFlag = placingFlag;
        this.pos = pos;
    }

    public boolean isPlacingFlag() {
        return PlacingFlag;
    }

    public Vector2I getPos() {
        return pos;
    }
}
