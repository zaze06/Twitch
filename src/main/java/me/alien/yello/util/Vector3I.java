package me.alien.yello.util;

public class Vector3I {
    int x;
    int y;
    int z;

    public Vector3I(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3I setX(int x) {
        this.x = x;
        return this;
    }

    public Vector3I setY(int y) {
        this.y = y;
        return this;
    }

    public Vector3I setZ(int z) {
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

    public Vector3I add(int x, int y, int z){
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    public Vector3I set(int x, int y, int z){
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public Vector3I clone(){
        return new Vector3I(x,y,z);
    }

    @Override
    public String toString() {
        return "Vector3I{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
