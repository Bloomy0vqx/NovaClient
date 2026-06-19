package com.novaclient.core.event;

public class PacketEvent extends Event {
    private final Object packet;

    public PacketEvent(Object packet) {
        this.packet = packet;
    }

    public Object getPacket() {
        return packet;
    }
}
