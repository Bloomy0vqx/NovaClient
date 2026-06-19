package net.minecraftforge.client.event;

public class RenderGameOverlayEvent {
    public float partialTicks;
    public ElementType type;

    public enum ElementType {
        ALL, TEXT, HOTBAR, CROSSHAIRS, CHAT, PLAYER_LIST, DEBUG, POTION_ICONS, SUBTITLES, FPS_GRAPH
    }

    public ElementType getType() { return type; }
    public float getPartialTicks() { return partialTicks; }

    public static class Post extends RenderGameOverlayEvent {
        public Post(float partialTicks) {
            this.partialTicks = partialTicks;
            this.type = ElementType.ALL;
        }
    }
}
