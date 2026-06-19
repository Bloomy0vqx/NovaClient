package net.minecraftforge.common;

public class MinecraftForge {
    public static final EventBus EVENT_BUS = new EventBus();

    public static class EventBus {
        public void register(Object target) {}
        public void post(Object event) {}
    }
}
