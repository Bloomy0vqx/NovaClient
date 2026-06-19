package com.novaclient.core.ui.hud.impl;

import com.novaclient.core.ui.hud.HudElement;
import com.novaclient.core.ui.render.RenderUtil;
import com.novaclient.core.ui.render.ColorUtil;

public class ServerElement extends HudElement {
    private static final int BG = ColorUtil.withAlpha(0x000000, 120);
    private static final int TEXT = 0xFFFFFFFF;

    public ServerElement(float x, float y) {
        super("Server", x, y);
        this.width = 120;
        this.height = 16;
    }

    @Override
    public String getName() { return "Server"; }

    @Override
    public void render(float partialTicks) {
        RenderUtil.drawRoundedRect(x, y, x + width, y + height, BG, 3);
        drawText("Singleplayer", x + 4, y + 3, TEXT, 0.8f);
    }

    @Override
    public void update() {}

    private void drawText(String text, float x, float y, int color, float scale) {}
}
