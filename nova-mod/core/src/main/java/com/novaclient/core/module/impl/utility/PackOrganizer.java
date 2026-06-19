package com.novaclient.core.module.impl.utility;

import com.novaclient.core.module.Module;
import com.novaclient.core.module.ModuleCategory;
import com.novaclient.core.module.setting.ModeSetting;

public class PackOrganizer extends Module {
    private final ModeSetting sortOrder = new ModeSetting("Sort", "Sort order", "A-Z", "Z-A", "Most Used", "Custom");

    public PackOrganizer() {
        super("Pack Organizer", "Organizes resource packs", ModuleCategory.UTILITY, 0);
        addSetting(sortOrder);
    }
}
