package me.alien.twitch.integration.util;

public class Time {
    int milliSec;
    int sec;
    int min;
    int hour;

    public Time(int milliSec, int sec, int min, int hour) {
        this.sec = sec;
        this.min = min;
        this.hour = hour;
        this.milliSec = milliSec;
    }

    public Time(int milliSec, int sec, int min) {
        this.milliSec = milliSec;
        this.sec = sec;
        this.min = min;
    }

    public Time(int milliSec, int sec) {
        this.milliSec = milliSec;
        this.sec = sec;
    }

    public Time(int milliSec) {
        this.milliSec = milliSec;
    }

    public Time() {}

    public Time addSec(int sec){
        this.sec += sec;
        return this;
    }

    public Time addMilliSec(int milliSec){
        this.milliSec += milliSec;
        return this;
    }

    public Time addMin(int min){
        this.min += min;
        return this;
    }

    public Time addHour(int hour){
        this.hour += hour;
        return this;
    }

    public Time subtractSec(int sec){
        this.sec -= sec;
        return this;
    }

    public Time subtractMilliSec(int milliSec){
        this.milliSec -= milliSec;
        return this;
    }

    public Time subtractMin(int min){
        this.min -= min;
        return this;
    }

    public Time subtractHour(int hour){
        this.hour -= hour;
        return this;
    }

    public long toMilliSec(){
        return milliSec + toSec()*1000;
    }

    public long toSec(){
        return sec + toMin()*60;
    }

    public long toMin() {
        return min + toHour()*60;
    }

    public long toHour() {
        return hour;
    }
}
