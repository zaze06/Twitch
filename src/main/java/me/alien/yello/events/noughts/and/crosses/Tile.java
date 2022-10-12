/*
 * MIT License
 *
 * Copyright (c) 2022. Zacharias Zellén
 */

package me.alien.yello.events.noughts.and.crosses;

import me.alien.yello.util.Vector2I;

public class Tile {
    Vector2I pos;
    int num = 0;

    public Tile(Vector2I pos, int num) {
        this.pos = pos;
        this.num = num;
    }

    public void place(int num){
        this.num = num;
    }

    public Vector2I getPos() {
        return pos;
    }

    public int getNum() {
        return num;
    }
}
