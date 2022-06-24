package me.alien.yello.integration.events.mindsweper;

import me.alien.yello.integration.util.Vector2I;

public class Tile {
    private boolean marked;
    private boolean bomb;
    private boolean showen;
    private boolean exploded;
    private int neberingBombs;
    private Vector2I pos;

    public Tile(Vector2I pos) {
        this.pos = pos;
    }

    public int getNeberingBombs() {
        return neberingBombs;
    }

    public boolean isBomb() {
        return bomb;
    }

    public boolean isMarked() {
        return marked;
    }

    public void setMarked(boolean marked) {
        this.marked = marked;
    }

    public void setNeberingBombs(int neberingBombs) {
        this.neberingBombs = neberingBombs;
    }

    public void setBomb(boolean bomb) {
        this.bomb = bomb;
    }

    public Vector2I getPos() {
        return pos;
    }

    public boolean isShowen() {
        return showen;
    }

    public void setShowen(boolean showen) {
        this.showen = showen;
    }

    public boolean isExploded() {
        return exploded;
    }

    public void setExploded(boolean exploded) {
        this.exploded = exploded;
    }
}
