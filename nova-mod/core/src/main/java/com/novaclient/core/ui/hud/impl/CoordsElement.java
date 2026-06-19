package com.novaclient.core.ui.hud.impl;

import com.novaclient.core.ui.hud.HudElement;
import com.novaclient.core.ui.render.RenderUtil;
import com.novaclient.core.ui.render.ColorUtil;

public class CoordsElement extends HudElement {
    private static final int BG = ColorUtil.withAlpha(0x000000, 120);
    private static final int TEXT = 0xFFFFFFFF;

    public CoordsElement(float x, float y) {
        super("Coordinates", x, y);
        this.width = 160;
        this.height = 16;
    }

    @Override
    public String getName() { return "Coordinates"; }

    @Override
    public void render(float partialTicks) {
        RenderUtil.drawRoundedRect(x, y, x + width, y + height, BG, 3);
        drawText("X: 0 Y: 0 Z: 0", x + 4, y + 3, TEXT, 0.8f);
    }

    @Override
    public void update() {}

    private void drawText(String text, float x, float y, int color, float scale) {
        // Adapter provides text rendering
    }
}
