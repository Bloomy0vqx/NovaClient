package net.minecraftforge.event;

public class TickEvent {
    public enum Phase { START, END }
    public static class ClientTickEvent extends TickEvent {
        public Phase phase = Phase.END;
    }
}
