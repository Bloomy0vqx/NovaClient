package com.novaclient.core.module.impl.pvp;

import com.novaclient.core.module.Module;
import com.novaclient.core.module.ModuleCategory;
import com.novaclient.core.module.setting.ModeSetting;

public class OneSevenVisuals extends Module {
    private final ModeSetting mode = new ModeSetting("Mode", "Visual style", "1.7", "1.8", "1.9+");

    public OneSevenVisuals() {
        super("1.7 Visuals", "Recreates 1.7 combat visuals", ModuleCategory.PVP, 0);
        addSetting(mode);
    }
}
