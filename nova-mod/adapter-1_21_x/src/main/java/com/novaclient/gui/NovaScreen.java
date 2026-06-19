package com.novaclient.gui;

import com.novaclient.NovaClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class NovaScreen extends Screen {

    private final ClickGui gui;

    public NovaScreen(Text title, ClickGui gui) {
        super(title);
        this.gui = gui;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        gui.render(context, mouseX, mouseY, delta);
        // Don't call super — we draw our own background
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        gui.mouseClicked(mouseX, mouseY, button);
        return true;
    }

    @Override
    public boolean shouldPause() {
        return false; // Game keeps running
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Override
    public void close() {
        super.close();
        NovaClient.getInstance().setGuiOpen(false);
    }
}
