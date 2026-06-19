package com.novaclient.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.Text;

import java.lang.reflect.Method;

public class RenderHelper {

    private static Method drawTextWithShadow5;
    private static Method drawText6;
    private static Method drawTextWithShadow6;
    private static boolean resolved = false;

    private static void resolve() {
        if (resolved) return;
        resolved = true;

        Class<?> dcClass = DrawContext.class;
        Class<?> trClass = TextRenderer.class;
        Class<?> textClass = Text.class;

        // drawTextWithShadow(TextRenderer, Text, int, int, int)
        try {
            drawTextWithShadow5 = dcClass.getMethod("drawTextWithShadow", trClass, textClass, int.class, int.class, int.class);
        } catch (NoSuchMethodException ignored) {}

        // drawText(TextRenderer, Text, int, int, int, boolean)
        try {
            drawText6 = dcClass.getMethod("drawText", trClass, textClass, int.class, int.class, int.class, boolean.class);
        } catch (NoSuchMethodException ignored) {}

        // drawTextWithShadow(TextRenderer, Text, int, int, int, int) - some versions
        try {
            drawTextWithShadow6 = dcClass.getMethod("drawTextWithShadow", trClass, textClass, int.class, int.class, int.class, int.class);
        } catch (NoSuchMethodException ignored) {}
    }

    public static int drawText(DrawContext ctx, TextRenderer tr, Text text, int x, int y, int color, boolean shadow) {
        resolve();

        // Try drawText(TextRenderer, Text, int, int, int, boolean) first
        if (drawText6 != null) {
            try {
                return (int) drawText6.invoke(ctx, tr, text, x, y, color, shadow);
            } catch (Exception ignored) {}
        }

        // If shadow=true, try drawTextWithShadow(TextRenderer, Text, int, int, int)
        if (shadow && drawTextWithShadow5 != null) {
            try {
                return (int) drawTextWithShadow5.invoke(ctx, tr, text, x, y, color);
            } catch (Exception ignored) {}
        }

        // If shadow=false and drawText6 failed, try drawTextWithShadow anyway
        if (drawTextWithShadow5 != null) {
            try {
                return (int) drawTextWithShadow5.invoke(ctx, tr, text, x, y, color);
            } catch (Exception ignored) {}
        }

        return 0;
    }
}
