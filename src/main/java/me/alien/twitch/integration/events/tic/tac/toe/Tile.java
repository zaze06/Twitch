package me.alien.twitch.integration.events.tic.tac.toe;


import me.alien.twitch.integration.util.Vector2I;

public class Tile {
    Vector2I pos;
    int value = 0;

    public Tile(Vector2I pos) {
        this.pos = pos;
    }

    public Tile(int x, int y) {
        pos = new Vector2I(x, y);
    }

    public Vector2I getPos() {
        return pos;
    }

    public int getX(){
        return pos.getX();
    }

    public int getY(){
        return pos.getY();
    }

    public String getIcon(){
        String[] tmp = new String[]{"-", "X", "O"};
        return tmp[value];
    }
}
