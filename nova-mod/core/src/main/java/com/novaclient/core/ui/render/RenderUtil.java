package com.novaclient.core.ui.render;

import com.novaclient.core.platform.Platform;
import com.novaclient.core.platform.PlatformAdapter;

/**
 * Rendering utility that delegates to the platform adapter.
 * All draw calls go through Platform.get() so the core remains version-agnostic.
 */
public class RenderUtil {
    public static final int NO_COLOR = 0;
    private static float zLevel;

    public static void setZLevel(float z) { zLevel = z; }
    public static float getZLevel() { return zLevel; }

    private static PlatformAdapter p() {
        return Platform.isInitialized() ? Platform.get() : null;
    }

    public static void drawRect(float left, float top, float right, float bottom, int color) {
        PlatformAdapter pa = p();
        if (pa != null) pa.drawRect(left, top, right, bottom, color);
    }

    public static void drawRect(float left, float top, float right, float bottom) {
        drawRect(left, top, right, bottom, 0xFFFFFFFF);
    }

    public static void drawRoundedRect(float left, float top, float right, float bottom, int color, float radius) {
        drawRoundedRect(left, top, right, bottom, color, radius, true);
    }

    public static void drawRoundedRect(float left, float top, float right, float bottom, int color, float radius, boolean filled) {
        if (color == NO_COLOR) return;
        PlatformAdapter pa = p();
        if (pa == null) return;
        pa.drawRect(left + radius, top, right - radius, bottom, color);
        pa.drawRect(left, top + radius, right, bottom - radius, color);
        pa.drawCircle(left + radius, top + radius, radius, color, 8);
        pa.drawCircle(right - radius, top + radius, radius, color, 8);
        pa.drawCircle(left + radius, bottom - radius, radius, color, 8);
        pa.drawCircle(right - radius, bottom - radius, radius, color, 8);
    }

    public static void drawCircle(float x, float y, float radius, int color, int segments) {
        PlatformAdapter pa = p();
        if (pa != null) pa.drawCircle(x, y, radius, color, segments);
    }

    public static void drawGradientRect(float left, float top, float right, float bottom, int colorTop, int colorBottom) {
        PlatformAdapter pa = p();
        if (pa != null) pa.drawGradientRect(left, top, right, bottom, colorTop, colorBottom);
    }

    public static void drawGradientRectHorizontal(float left, float top, float right, float bottom, int colorLeft, int colorRight) {
        PlatformAdapter pa = p();
        if (pa != null) pa.drawGradientRectH(left, top, right, bottom, colorLeft, colorRight);
    }

    public static void drawOutline(float left, float top, float right, float bottom, float lineWidth, int color) {
        drawRect(left, top, right, top + lineWidth, color);
        drawRect(left, bottom - lineWidth, right, bottom, color);
        drawRect(left, top, left + lineWidth, bottom, color);
        drawRect(right - lineWidth, top, right, bottom, color);
    }

    public static void drawShadow(float left, float top, float right, float bottom, int shadowColor, int blurSize) {
        int c = ColorUtil.withAlpha(shadowColor, 40);
        drawRect(left - blurSize, top - blurSize, right + blurSize, top, c);
        drawRect(left - blurSize, bottom, right + blurSize, bottom + blurSize, c);
        drawRect(left - blurSize, top, left, bottom, c);
        drawRect(right, top, right + blurSize, bottom, c);
    }

    public static void drawString(String text, float x, float y, int color, boolean shadow) {
        PlatformAdapter pa = p();
        if (pa != null) pa.drawString(text, x, y, color, shadow);
    }

    public static void drawString(String text, float x, float y, int color, float scale, boolean shadow) {
        PlatformAdapter pa = p();
        if (pa != null) pa.drawString(text, x, y, color, scale, shadow);
    }

    public static void drawCenteredString(String text, float x, float y, int color, boolean shadow) {
        PlatformAdapter pa = p();
        if (pa != null) pa.drawCenteredString(text, x, y, color, shadow);
    }

    public static int getStringWidth(String text) {
        PlatformAdapter pa = p();
        return pa != null ? pa.getStringWidth(text) : text.length() * 6;
    }

    public static int getStringHeight() {
        PlatformAdapter pa = p();
        return pa != null ? pa.getStringHeight() : 9;
    }

    public static void enableScissor(int x, int y, int width, int height) {
        PlatformAdapter pa = p();
        if (pa != null) pa.enableScissor(x, y, width, height);
    }

    public static void disableScissor() {
        PlatformAdapter pa = p();
        if (pa != null) pa.disableScissor();
    }
}
