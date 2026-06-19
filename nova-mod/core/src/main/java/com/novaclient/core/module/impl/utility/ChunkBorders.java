package com.novaclient.core.module.impl.utility;

import com.novaclient.core.module.Module;
import com.novaclient.core.module.ModuleCategory;
import com.novaclient.core.module.setting.ColorSetting;
import com.novaclient.core.module.setting.NumberSetting;

public class ChunkBorders extends Module {
    private final ColorSetting borderColor = new ColorSetting("Color", "Border color", 255, 0, 0, 150);
    private final NumberSetting lineWidth = new NumberSetting("Width", "Line width", 2.0, 1.0, 5.0, 0.5);

    public ChunkBorders() {
        super("Chunk Borders", "Shows chunk borders", ModuleCategory.UTILITY, 0);
        addSetting(borderColor);
        addSetting(lineWidth);
    }
}
