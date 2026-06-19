package com.novaclient.core.module.impl.visual;

import com.novaclient.core.module.Module;
import com.novaclient.core.module.ModuleCategory;
import com.novaclient.core.module.setting.ModeSetting;
import com.novaclient.core.module.setting.NumberSetting;

public class TimeChanger extends Module {
    private final ModeSetting timeMode = new ModeSetting("Mode", "Time mode", "Static", "Real Time", "Cycle");
    private final NumberSetting staticTime = new NumberSetting("Time", "Static time value", 6000, 0, 24000, 100);
    private final NumberSetting cycleSpeed = new NumberSetting("Speed", "Cycle speed", 1.0, 0.1, 10.0, 0.1);

    public TimeChanger() {
        super("Time Changer", "Changes time client-side", ModuleCategory.VISUAL, 0);
        addSetting(timeMode);
        addSetting(staticTime);
        addSetting(cycleSpeed);
    }

    public long getTime() { return staticTime.getIntValue(); }
}
