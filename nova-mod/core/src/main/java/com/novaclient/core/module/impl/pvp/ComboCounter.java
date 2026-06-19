package com.novaclient.core.module.impl.pvp;

import com.novaclient.core.module.Module;
import com.novaclient.core.module.ModuleCategory;
import com.novaclient.core.module.setting.NumberSetting;

public class ComboCounter extends Module {
    private final NumberSetting resetTime = new NumberSetting("Reset Time", "Combo reset time in seconds", 2.0, 1.0, 10.0, 0.5);
    private int combo = 0;
    private long lastHitTime = 0;

    public ComboCounter() {
        super("Combo Counter", "Shows current combo count", ModuleCategory.PVP, 0);
        addSetting(resetTime);
    }

    public int getCombo() { return combo; }
    public void setCombo(int combo) { this.combo = combo; }
    public long getLastHitTime() { return lastHitTime; }
    public void setLastHitTime(long time) { this.lastHitTime = time; }
}
