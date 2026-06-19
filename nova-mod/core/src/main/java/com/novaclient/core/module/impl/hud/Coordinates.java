package com.novaclient.core.module.impl.hud;

import com.novaclient.core.module.Module;
import com.novaclient.core.module.ModuleCategory;
import com.novaclient.core.module.setting.BooleanSetting;
import com.novaclient.core.module.setting.ColorSetting;
import com.novaclient.core.module.setting.ModeSetting;

public class Coordinates extends Module {
    private final ModeSetting format = new ModeSetting("Format", "Coordinate format", "XYZ", "X: Y: Z:", "Compact");
    private final BooleanSetting showInChat = new BooleanSetting("Chat", "Show coordinates in chat", true);
    private final BooleanSetting showNether = new BooleanSetting("Nether", "Show nether coordinates", true);
    private final ColorSetting textColor = new ColorSetting("Color", "Text color", 255, 255, 255, 255);
    private final ColorSetting bgColor = new ColorSetting("Background", "Background color", 0, 0, 0, 100);

    public Coordinates() {
        super("Coordinates", "Displays your current coordinates", ModuleCategory.HUD, 0);
        addSetting(format);
        addSetting(showInChat);
        addSetting(showNether);
        addSetting(textColor);
        addSetting(bgColor);
    }
}
