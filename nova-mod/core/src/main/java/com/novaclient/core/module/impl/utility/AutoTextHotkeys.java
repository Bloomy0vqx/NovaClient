package com.novaclient.core.module.impl.utility;

import com.novaclient.core.module.Module;
import com.novaclient.core.module.ModuleCategory;
import com.novaclient.core.module.setting.BooleanSetting;
import java.util.ArrayList;
import java.util.List;

public class AutoTextHotkeys extends Module {
    private final BooleanSetting requireShift = new BooleanSetting("Shift Required", "Require shift to activate", true);
    private final List<TextHotkey> hotkeys = new ArrayList<>();

    public AutoTextHotkeys() {
        super("Auto Text Hotkeys", "Send pre-defined text via hotkeys", ModuleCategory.UTILITY, 0);
        addSetting(requireShift);
    }

    public void addHotkey(String key, String text) {
        hotkeys.add(new TextHotkey(key, text));
    }

    public List<TextHotkey> getHotkeys() {
        return hotkeys;
    }

    public static class TextHotkey {
        public final String key;
        public final String text;

        public TextHotkey(String key, String text) {
            this.key = key;
            this.text = text;
        }
    }
}
