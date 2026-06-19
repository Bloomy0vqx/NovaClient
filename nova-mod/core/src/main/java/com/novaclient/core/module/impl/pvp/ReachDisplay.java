package com.novaclient.core.module.impl.pvp;

import com.novaclient.core.module.Module;
import com.novaclient.core.module.ModuleCategory;
import com.novaclient.core.module.setting.NumberSetting;

public class ReachDisplay extends Module {
    private final NumberSetting maxReach = new NumberSetting("Max Reach", "Maximum reach distance", 3.5, 1.0, 6.0, 0.1);
    private double currentReach = 0;

    public ReachDisplay() {
        super("Reach Display", "Shows your current reach distance", ModuleCategory.PVP, 0);
        addSetting(maxReach);
    }

    public double getCurrentReach() { return currentReach; }
    public void setCurrentReach(double reach) { this.currentReach = reach; }
}
