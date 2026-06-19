package com.novaclient.core.module.impl.utility;

import com.novaclient.core.module.Module;
import com.novaclient.core.module.ModuleCategory;
import com.novaclient.core.module.setting.ModeSetting;
import com.novaclient.core.module.setting.BooleanSetting;
import com.novaclient.core.module.setting.NumberSetting;

public class ScreenshotUploader extends Module {
    private final ModeSetting uploadService = new ModeSetting("Service", "Upload service", "Imgur", "Custom");
    private final BooleanSetting autoUpload = new BooleanSetting("Auto Upload", "Auto upload screenshots", false);
    private final NumberSetting delay = new NumberSetting("Delay", "Upload delay in seconds", 2.0, 0.0, 10.0, 1.0);

    public ScreenshotUploader() {
        super("Screenshot Uploader", "Uploads screenshots automatically", ModuleCategory.UTILITY, 0);
        addSetting(uploadService);
        addSetting(autoUpload);
        addSetting(delay);
    }
}
