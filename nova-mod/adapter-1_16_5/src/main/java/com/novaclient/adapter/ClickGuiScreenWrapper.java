package com.novaclient.adapter;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.novaclient.core.NovaCore;
import net.minecraft.client.gui.screen.Screen;

public class ClickGuiScreenWrapper extends Screen {
    
    @Override
    protected void init() {
        if (NovaCore.getInstance().getClickGui() != null) {
            NovaCore.getInstance().getClickGui().init(width, height);
        }
    }

    public void func_231160_c_() {
        this.init();
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (NovaCore.getInstance().getClickGui() != null) {
            NovaCore.getInstance().getClickGui().render(mouseX, mouseY, partialTicks);
        }
    }

    public void func_230430_a_(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (NovaCore.getInstance().getClickGui() != null) {
            NovaCore.getInstance().getClickGui().mouseClicked((float)mouseX, (float)mouseY, button);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    public boolean func_231044_a_(double mouseX, double mouseY, int button) {
        return this.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (NovaCore.getInstance().getClickGui() != null) {
            NovaCore.getInstance().getClickGui().mouseReleased((float)mouseX, (float)mouseY, button);
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    public boolean func_231048_c_(double mouseX, double mouseY, int button) {
        return this.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public boolean func_231177_au__() {
        return this.isPauseScreen();
    }

    @Override
    public void onClose() {
        NovaCore.getInstance().setClickGuiOpen(false);
    }

    public void func_231175_as__() {
        this.onClose();
    }
}
