package com.novaclient.core.ui.hud.impl;

import com.novaclient.core.ui.hud.HudElement;
import com.novaclient.core.ui.render.RenderUtil;
import com.novaclient.core.ui.render.ColorUtil;

public class DirectionElement extends HudElement {
    private static final int BG = ColorUtil.withAlpha(0x000000, 120);
    private static final int TEXT = 0xFFFFFFFF;

    public DirectionElement(float x, float y) {
        super("Direction", x, y);
        this.width = 120;
        this.height = 16;
    }

    @Override
    public String getName() { return "Direction"; }

    @Override
    public void render(float partialTicks) {
        RenderUtil.drawRoundedRect(x, y, x + width, y + height, BG, 3);
        drawText("Facing: South (180.0)", x + 4, y + 3, TEXT, 0.8f);
    }

    @Override
    public void update() {}

    private void drawText(String text, float x, float y, int color, float scale) {}
}
