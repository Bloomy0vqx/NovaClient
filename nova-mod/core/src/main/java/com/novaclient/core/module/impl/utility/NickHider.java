package com.novaclient.core.module.impl.utility;

import com.novaclient.core.module.Module;
import com.novaclient.core.module.ModuleCategory;
import com.novaclient.core.module.setting.ModeSetting;
import com.novaclient.core.module.setting.NumberSetting;

public class NickHider extends Module {
    private final ModeSetting mode = new ModeSetting("Mode", "Nick mode", "Hide", "Custom");
    private final NumberSetting customName = new NumberSetting("Name", "Custom name index", 0, 0, 10, 1);

    public NickHider() {
        super("Nick Hider", "Hides your real username", ModuleCategory.UTILITY, 0);
        addSetting(mode);
        addSetting(customName);
    }
}
