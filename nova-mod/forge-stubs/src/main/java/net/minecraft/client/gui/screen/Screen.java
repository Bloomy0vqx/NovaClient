package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;

public class Screen {
    public int width;
    public int height;

    protected void init() {}
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {}
    public void tick() {}
    public void onClose() {}
    public boolean mouseClicked(double mouseX, double mouseY, int button) { return false; }
    public boolean mouseReleased(double mouseX, double mouseY, int button) { return false; }
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) { return false; }
    public boolean isPauseScreen() { return true; }
}
