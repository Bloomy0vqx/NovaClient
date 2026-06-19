package com.novaclient.core.ui.hud.impl;

import com.novaclient.core.ui.hud.HudElement;
import com.novaclient.core.ui.render.RenderUtil;
import com.novaclient.core.ui.render.ColorUtil;

public class PingElement extends HudElement {
    private static final int BG = ColorUtil.withAlpha(0x000000, 120);
    private static final int TEXT = 0xFFFFFFFF;

    public PingElement(float x, float y) {
        super("Ping", x, y);
        this.width = 70;
        this.height = 16;
    }

    @Override
    public String getName() { return "Ping"; }

    @Override
    public void render(float partialTicks) {
        RenderUtil.drawRoundedRect(x, y, x + width, y + height, BG, 3);
        drawText("0ms", x + 4, y + 3, TEXT, 0.8f);
    }

    @Override
    public void update() {}

    private void drawText(String text, float x, float y, int color, float scale) {}
}
