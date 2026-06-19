package com.novaclient.core.module.impl.pvp;

import com.novaclient.core.module.Module;
import com.novaclient.core.module.ModuleCategory;
import com.novaclient.core.module.setting.BooleanSetting;

public class ToggleSprint extends Module {
    private final BooleanSetting autoDisable = new BooleanSetting("Auto Disable", "Disable when key released", true);
    private boolean isSprinting = false;

    public ToggleSprint() {
        super("Toggle Sprint", "Toggles sprinting instead of holding", ModuleCategory.PVP, 0);
        addSetting(autoDisable);
    }

    public boolean isSprinting() {
        return isSprinting;
    }

    public void setSprinting(boolean sprinting) {
        this.isSprinting = sprinting;
    }
}
