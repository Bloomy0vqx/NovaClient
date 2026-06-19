package com.novaclient.core.module.impl.visual;

import com.novaclient.core.module.Module;
import com.novaclient.core.module.ModuleCategory;
import com.novaclient.core.module.setting.ColorSetting;
import com.novaclient.core.module.setting.NumberSetting;
import com.novaclient.core.module.setting.ModeSetting;
import com.novaclient.core.module.setting.BooleanSetting;

public class BlockOutline extends Module {
    private final ColorSetting outlineColor = new ColorSetting("Color", "Outline color", 255, 255, 255, 255);
    private final NumberSetting lineWidth = new NumberSetting("Width", "Outline width", 2.0, 1.0, 5.0, 0.5);
    private final BooleanSetting fill = new BooleanSetting("Fill", "Fill outline", false);
    private final ColorSetting fillColor = new ColorSetting("Fill Color", "Fill color", 255, 255, 255, 50);
    private final ModeSetting mode = new ModeSetting("Mode", "Outline mode", "Outline", "Outline + Fill", "Fill only");

    public BlockOutline() {
        super("Block Outline", "Custom block selection outline", ModuleCategory.VISUAL, 0);
        addSetting(outlineColor);
        addSetting(lineWidth);
        addSetting(fill);
        addSetting(fillColor);
        addSetting(mode);
    }
}
