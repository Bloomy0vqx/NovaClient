package com.novaclient.core.module.impl.visual;

import com.novaclient.core.module.Module;
import com.novaclient.core.module.ModuleCategory;
import com.novaclient.core.module.setting.NumberSetting;
import com.novaclient.core.module.setting.BooleanSetting;
import com.novaclient.core.module.setting.ModeSetting;

public class Freelook extends Module {
    private final ModeSetting toggleMode = new ModeSetting("Mode", "Activation mode", "Hold", "Toggle");
    private final NumberSetting speed = new NumberSetting("Speed", "Camera rotation speed", 5.0, 1.0, 20.0, 0.5);
    private final BooleanSetting showPlayer = new BooleanSetting("Show Player", "Show player model while freelooking", true);
    private boolean active = false;

    public Freelook() {
        super("Freelook", "Free camera look without moving", ModuleCategory.VISUAL, 0);
        addSetting(toggleMode);
        addSetting(speed);
        addSetting(showPlayer);
    }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
