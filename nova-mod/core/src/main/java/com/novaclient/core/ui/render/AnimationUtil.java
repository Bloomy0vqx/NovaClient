package com.novaclient.core.ui.render;

public class AnimationUtil {
    public static float animate(float target, float current, float speed) {
        float diff = target - current;
        if (diff < speed && diff > -speed) {
            return target;
        }
        return current + diff * speed;
    }

    public static float easeOut(float t) {
        return t * (2 - t);
    }

    public static float easeIn(float t) {
        return t * t;
    }

    public static float easeInOut(float t) {
        return t < 0.5f ? 2 * t * t : -1 + (4 - 2 * t) * t;
    }

    public static float easeOutCubic(float t) {
        float f = 1 - t;
        return 1 - f * f * f;
    }

    public static float easeOutQuad(float t) {
        return t * (2 - t);
    }

    public static float easeInQuad(float t) {
        return t * t;
    }

    public static float easeOutElastic(float t) {
        if (t == 0 || t == 1) return t;
        float p = 0.3f;
        float s = p / 4;
        return (float) Math.pow(2, -10 * t) * (float) Math.sin((t - s) * (2 * Math.PI) / p) + 1;
    }

    public static float easeOutBack(float t) {
        float f = 1.70158f;
        return 1 + (--t) * t * ((f + 1) * t + f);
    }

    public static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    public static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }
}
