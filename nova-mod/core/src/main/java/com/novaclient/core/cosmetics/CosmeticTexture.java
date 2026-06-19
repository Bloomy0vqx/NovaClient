package com.novaclient.core.cosmetics;

import java.util.List;

public class CosmeticTexture {
    private final String id;
    private final String url;
    private final int width;
    private final int height;
    private final List<AnimationFrame> frames;

    public CosmeticTexture(String id, String url, int width, int height) {
        this.id = id;
        this.url = url;
        this.width = width;
        this.height = height;
        this.frames = null;
    }

    public CosmeticTexture(String id, String url, int width, int height, List<AnimationFrame> frames) {
        this.id = id;
        this.url = url;
        this.width = width;
        this.height = height;
        this.frames = frames;
    }

    public String getId() { return id; }
    public String getUrl() { return url; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public List<AnimationFrame> getFrames() { return frames; }
    public boolean isAnimated() { return frames != null && !frames.isEmpty(); }

    public static class AnimationFrame {
        public final int u;
        public final int v;
        public final int width;
        public final int height;
        public final int duration;

        public AnimationFrame(int u, int v, int width, int height, int duration) {
            this.u = u;
            this.v = v;
            this.width = width;
            this.height = height;
            this.duration = duration;
        }
    }
}
