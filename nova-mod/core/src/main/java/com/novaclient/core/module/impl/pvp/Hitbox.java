package com.novaclient.core.module.impl.pvp;

import com.novaclient.core.module.Module;
import com.novaclient.core.module.ModuleCategory;
import com.novaclient.core.module.setting.NumberSetting;
import com.novaclient.core.module.setting.ColorSetting;
import com.novaclient.core.module.setting.BooleanSetting;

public class Hitbox extends Module {
    private final NumberSetting expansion = new NumberSetting("Expansion", "Hitbox expansion in blocks", 0.1, 0.0, 1.0, 0.05);
    private final ColorSetting color = new ColorSetting("Color", "Hitbox outline color", 255, 0, 0, 100);
    private final BooleanSetting fill = new BooleanSetting("Fill", "Fill hitbox", true);

    public Hitbox() {
        super("Hitbox", "Expands entity hitboxes for easier aiming", ModuleCategory.PVP, 0);
        addSetting(expansion);
        addSetting(color);
        addSetting(fill);
    }

    public float getExpansion() { return expansion.getFloatValue(); }
}
