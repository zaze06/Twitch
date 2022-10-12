/*
 * MIT License
 *
 * Copyright (c) 2022. Zacharias Zellén
 *
 * This is not my file nor my code i don't take credit for this
 */

package me.alien.yello.events.tic.tac.toe;

public enum Mark {
    X('X'),
    O('O'),
    BLANK('-');

    private final char mark;

    Mark(char initMark) {
        this.mark = initMark;
    }

    public boolean isMarked() {
        return this != BLANK;
    }

    public char getMark() {
        return this.mark;
    }

    @Override
    public String toString() {
        return String.valueOf(mark);
    }
}
