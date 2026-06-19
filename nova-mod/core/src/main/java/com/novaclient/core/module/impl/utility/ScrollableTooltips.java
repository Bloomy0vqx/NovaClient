package com.novaclient.core.module.impl.utility;

import com.novaclient.core.module.Module;
import com.novaclient.core.module.ModuleCategory;
import com.novaclient.core.module.setting.NumberSetting;
import com.novaclient.core.module.setting.BooleanSetting;

public class ScrollableTooltips extends Module {
    private final NumberSetting scrollSpeed = new NumberSetting("Speed", "Scroll speed", 1.0, 0.5, 3.0, 0.1);
    private final BooleanSetting smoothScroll = new BooleanSetting("Smooth", "Smooth scrolling", true);

    public ScrollableTooltips() {
        super("Scrollable Tooltips", "Makes inventory tooltips scrollable", ModuleCategory.UTILITY, 0);
        addSetting(scrollSpeed);
        addSetting(smoothScroll);
    }
}
