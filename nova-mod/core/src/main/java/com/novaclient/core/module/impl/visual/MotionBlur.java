package com.novaclient.core.module.impl.visual;

import com.novaclient.core.module.Module;
import com.novaclient.core.module.ModuleCategory;
import com.novaclient.core.module.setting.NumberSetting;
import com.novaclient.core.module.setting.ModeSetting;

public class MotionBlur extends Module {
    private final NumberSetting blurAmount = new NumberSetting("Amount", "Motion blur intensity", 5.0, 1.0, 20.0, 1.0);
    private final ModeSetting quality = new ModeSetting("Quality", "Blur quality", "Low", "Medium", "High");

    public MotionBlur() {
        super("Motion Blur", "Adds motion blur effect", ModuleCategory.VISUAL, 0);
        addSetting(blurAmount);
        addSetting(quality);
    }

    public int getBlurAmount() { return blurAmount.getIntValue(); }
}
