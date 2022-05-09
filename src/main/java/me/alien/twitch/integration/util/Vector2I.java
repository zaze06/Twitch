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

    public Vector2I setX(int x) {
        this.x = x;
        return this;
    }

    public Vector2I setY(int y) {
        this.y = y;
        return this;
    }

    public Vector2I setZ(int z) {
        this.z = z;
        return this;
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

    public Vector2I add(int x, int y, int z){
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    public Vector2I set(int x, int y, int z){
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public Vector2I clone(){
        return new Vector2I(x,y,z);
    }
}
