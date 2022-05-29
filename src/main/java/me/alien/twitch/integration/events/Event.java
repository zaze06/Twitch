package me.alien.twitch.integration.events;

public interface Event {
    boolean run();
    void addData(Object data);
    void removeData(Object data);
    void removeData(int location);
    Object getData(int location);
    void end();
}
