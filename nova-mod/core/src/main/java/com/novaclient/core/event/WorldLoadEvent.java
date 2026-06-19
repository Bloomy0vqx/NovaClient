package com.novaclient.core.event;

public class WorldLoadEvent extends Event {
    private final Object world;

    public WorldLoadEvent(Object world) {
        this.world = world;
    }

    public Object getWorld() {
        return world;
    }
}
