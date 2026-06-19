package com.novaclient.core.module.impl.visual;

import com.novaclient.core.module.Module;
import com.novaclient.core.module.ModuleCategory;
import com.novaclient.core.module.setting.BooleanSetting;

public class TwoDItems extends Module {
    private final BooleanSetting showEnchants = new BooleanSetting("Enchants", "Show enchantments", true);
    private final BooleanSetting showDurability = new BooleanSetting("Durability", "Show durability bar", true);

    public TwoDItems() {
        super("2D Items", "Renders items in 2D style in hand", ModuleCategory.VISUAL, 0);
        addSetting(showEnchants);
        addSetting(showDurability);
    }
}
