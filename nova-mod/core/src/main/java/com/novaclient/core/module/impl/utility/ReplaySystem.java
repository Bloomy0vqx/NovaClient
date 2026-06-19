package com.novaclient.core.module.impl.utility;

import com.novaclient.core.module.Module;
import com.novaclient.core.module.ModuleCategory;
import com.novaclient.core.module.setting.NumberSetting;
import com.novaclient.core.module.setting.BooleanSetting;
import java.util.ArrayList;
import java.util.List;

public class ReplaySystem extends Module {
    private final NumberSetting recordTime = new NumberSetting("Duration", "Max record duration (seconds)", 300, 30, 600, 30);
    private final BooleanSetting autoRecord = new BooleanSetting("Auto Record", "Auto record on join", false);
    private final BooleanSetting showNametags = new BooleanSetting("Nametags", "Show player nametags", true);
    private boolean recording = false;
    private final List<Object> recordedFrames = new ArrayList<>();

    public ReplaySystem() {
        super("Replay System", "Records and replays gameplay", ModuleCategory.UTILITY, 0);
        addSetting(recordTime);
        addSetting(autoRecord);
        addSetting(showNametags);
    }

    public boolean isRecording() { return recording; }
    public void setRecording(boolean recording) { this.recording = recording; }
}
