/*
 * MIT License
 *
 * Copyright (c) 2022. Zacharias Zellén
 */

package me.alien.yello.util.dice;

import static me.alien.yello.Main.rand;

public class D20 implements Dice{
    @Override
    public int roll() {
        return rand.nextInt(19)+1;
    }
}
