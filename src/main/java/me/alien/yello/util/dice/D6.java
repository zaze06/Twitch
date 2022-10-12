/*
 * MIT License
 *
 * Copyright (c) 2022. Zacharias Zellén
 */

package me.alien.yello.util.dice;

import static me.alien.yello.Main.rand;

public class D6 implements Dice{
    @Override
    public int roll() {
        return rand.nextInt(5)+1;
    }
}
