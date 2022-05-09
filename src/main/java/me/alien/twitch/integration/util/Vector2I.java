package me.alien.twitch.integration.util;

public class Vector2I {
    int x;
    int y;
    int z;

    public Vector2I(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public void add(int x, int y, int z){
        this.x += x;
        this.y += y;
        this.z += z;
    }

    public Vector2I clone(){
        return new Vector2I(x,y,z);
    }
}
