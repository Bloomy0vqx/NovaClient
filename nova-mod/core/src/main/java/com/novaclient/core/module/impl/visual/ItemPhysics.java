package com.novaclient.core.module.impl.visual;

import com.novaclient.core.module.Module;
import com.novaclient.core.module.ModuleCategory;
import com.novaclient.core.module.setting.NumberSetting;
import com.novaclient.core.module.setting.BooleanSetting;

public class ItemPhysics extends Module {
    private final NumberSetting rotationSpeed = new NumberSetting("Rotation", "Rotation speed", 1.0, 0.1, 5.0, 0.1);
    private final NumberSetting floatHeight = new NumberSetting("Float", "Float height", 0.3, 0.0, 1.0, 0.05);
    private final BooleanSetting realLooking = new BooleanSetting("Real Looking", "Realistic item physics", true);

    public ItemPhysics() {
        super("Item Physics", "Realistic dropped item physics", ModuleCategory.VISUAL, 0);
        addSetting(rotationSpeed);
        addSetting(floatHeight);
        addSetting(realLooking);
    }
}
