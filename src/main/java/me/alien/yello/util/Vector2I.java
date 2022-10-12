/*
 * MIT License
 *
 * Copyright (c) 2022. Zacharias Zellén
 */

package me.alien.yello.util;

public class Vector2I {
    int x,y;

    public Vector2I(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void addY(int y){
        this.y += y;
    }

    public void addX(int x){
        this.x += x;
    }

    public double distanceTo(int x1, int y1){
        return (Math.sqrt(Math.pow(x-x1, 2) + Math.pow(y-y1,2)));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vector2I)) return false;

        Vector2I vector2I = (Vector2I) o;

        if (x != vector2I.x) return false;
        return y == vector2I.y;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }

    public boolean isInSide(int x, int y, int x1, int y1) {
        return ((this.x > x && this.x < x1) && (this.y > y && this.y < y1));
    }
}
