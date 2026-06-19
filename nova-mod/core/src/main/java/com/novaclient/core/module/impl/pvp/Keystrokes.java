package com.novaclient.core.module.impl.pvp;

import com.novaclient.core.module.Module;
import com.novaclient.core.module.ModuleCategory;
import com.novaclient.core.module.setting.BooleanSetting;
import com.novaclient.core.module.setting.ColorSetting;
import com.novaclient.core.module.setting.NumberSetting;

public class Keystrokes extends Module {
    private final NumberSetting scale = new NumberSetting("Scale", "Display scale", 1.0, 0.5, 3.0, 0.1);
    private final NumberSetting opacity = new NumberSetting("Opacity", "Background opacity", 150, 0, 255, 5);
    private final BooleanSetting showCPS = new BooleanSetting("CPS", "Show clicks per second", true);
    private final BooleanSetting showWASD = new BooleanSetting("WASD", "Show movement keys", true);
    private final ColorSetting keyColor = new ColorSetting("Key Color", "Key background color", 30, 30, 30, 200);
    private final ColorSetting pressedColor = new ColorSetting("Pressed Color", "Pressed key color", 255, 80, 80, 255);
    private final NumberSetting borderRadius = new NumberSetting("Rounded", "Corner radius", 3.0, 0.0, 10.0, 1.0);

    public Keystrokes() {
        super("Keystrokes", "Shows keyboard input on screen", ModuleCategory.PVP, 0);
        addSetting(scale);
        addSetting(opacity);
        addSetting(showCPS);
        addSetting(showWASD);
        addSetting(keyColor);
        addSetting(pressedColor);
        addSetting(borderRadius);
    }

    public float getScale() { return scale.getFloatValue(); }
    public int getOpacity() { return opacity.getIntValue(); }
    public boolean isShowCPS() { return showCPS.isEnabled(); }
    public boolean isShowWASD() { return showWASD.isEnabled(); }
}
