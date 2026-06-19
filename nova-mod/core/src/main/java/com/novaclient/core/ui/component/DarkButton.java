package com.novaclient.core.ui.component;

public class DarkButton extends GuiWidget {
    private String label;
    private Runnable onPress;

    private static final int BG_BORDER = 0xFF000000;
    private static final int BG_HOVER_BORDER = 0xFF000000;
    private static final int BG_NORMAL = 0xFF000000;
    private static final int BG_HOVER = 0xFF222222;
    private static final int HOVER_HIGHLIGHT = 0x22FFFFFF;
    private static final int TEXT_COLOR = 0xFFFFFFFF;

    public DarkButton(int x, int y, int width, int height, String label, Runnable onPress) {
        super(x, y, width, height);
        this.label = label;
        this.onPress = onPress;
    }

    @Override
    public void render(float mouseX, float mouseY, float partialTicks) {
        hovered = isHovered(mouseX, mouseY);
        int borderColor = hovered ? BG_HOVER_BORDER : BG_BORDER;
        int bgColor = hovered ? BG_HOVER : BG_NORMAL;

        drawPixelatedRoundedRect(x, y, width, height, borderColor);
        drawPixelatedRoundedRect(x + 1, y + 1, width - 2, height - 2, bgColor);

        if (hovered) {
            fill(x + 2, y + 1, x + width - 2, y + 2, HOVER_HIGHLIGHT);
        }

        int textX = x + (width - getStringWidth(label)) / 2;
        int textY = y + (height - 8) / 2;
        drawText(label, textX, textY, TEXT_COLOR);
    }

    @Override
    public void mouseClicked(float mouseX, float mouseY, int button) {
        if (button == 0 && isHovered(mouseX, mouseY) && onPress != null) {
            onPress.run();
        }
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
