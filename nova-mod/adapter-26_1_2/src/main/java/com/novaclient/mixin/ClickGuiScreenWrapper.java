package com.novaclient.mixin;

import com.novaclient.adapter.Adapter26_1_2Fabric;
import com.novaclient.core.NovaCore;
import com.novaclient.core.ui.ClickGuiScreen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ClickGuiScreenWrapper extends Screen {

    public ClickGuiScreenWrapper() {
        super(Text.literal("Nova Client Mod Menu"));
    }

    @Override
    protected void init() {
        super.init();
        ClickGuiScreen gui = NovaCore.getInstance().getClickGui();
        if (gui != null) {
            gui.init((float) this.width, (float) this.height);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        Adapter26_1_2Fabric.setCurrentDrawContext(context);

        ClickGuiScreen gui = NovaCore.getInstance().getClickGui();
        if (gui != null && NovaCore.getInstance().isClickGuiOpen()) {
            gui.render((float) mouseX, (float) mouseY, delta);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        ClickGuiScreen gui = NovaCore.getInstance().getClickGui();
        if (gui != null) {
            gui.mouseClicked((float) mouseX, (float) mouseY, button);
        }
        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        ClickGuiScreen gui = NovaCore.getInstance().getClickGui();
        if (gui != null) {
            gui.mouseReleased((float) mouseX, (float) mouseY, button);
        }
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        ClickGuiScreen gui = NovaCore.getInstance().getClickGui();
        if (gui != null) {
            gui.mouseScrolled((float) mouseX, (float) mouseY, (float) verticalAmount);
        }
        return true;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        ClickGuiScreen gui = NovaCore.getInstance().getClickGui();
        if (gui != null) {
            gui.keyTyped(chr, -1);
        }
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        ClickGuiScreen gui = NovaCore.getInstance().getClickGui();
        if (gui != null) {
            gui.keyTyped('\0', keyCode);
        }
        if (keyCode == 256) {
            NovaCore.getInstance().toggleClickGui();
            this.client.setScreen(null);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void close() {
        NovaCore.getInstance().toggleClickGui();
        super.close();
    }
}
