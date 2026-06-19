package com.novaclient.core.module.impl.pvp;

import com.novaclient.core.module.Module;
import com.novaclient.core.module.ModuleCategory;
import com.novaclient.core.module.setting.NumberSetting;

public class CPSDisplay extends Module {
    private final NumberSetting smoothing = new NumberSetting("Smoothing", "CPS display smoothing", 5.0, 1.0, 20.0, 1.0);
    private int leftCps, rightCps;

    public CPSDisplay() {
        super("CPS", "Displays clicks per second", ModuleCategory.PVP, 0);
        addSetting(smoothing);
    }

    public int getLeftCps() { return leftCps; }
    public int getRightCps() { return rightCps; }
    public void setLeftCps(int cps) { this.leftCps = cps; }
    public void setRightCps(int cps) { this.rightCps = cps; }
}
