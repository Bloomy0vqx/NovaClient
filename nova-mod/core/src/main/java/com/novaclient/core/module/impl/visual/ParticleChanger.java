package com.novaclient.core.module.impl.visual;

import com.novaclient.core.module.Module;
import com.novaclient.core.module.ModuleCategory;
import com.novaclient.core.module.setting.ModeSetting;
import com.novaclient.core.module.setting.NumberSetting;
import com.novaclient.core.module.setting.ColorSetting;

public class ParticleChanger extends Module {
    private final ModeSetting mode = new ModeSetting("Mode", "Particle mode", "Custom", "Remove", "Enhance");
    private final ModeSetting particleType = new ModeSetting("Type", "Particle type", "Flame", "Heart", "Cloud", "Critical", "Enchant");
    private final NumberSetting amount = new NumberSetting("Amount", "Particle amount multiplier", 2.0, 1.0, 10.0, 0.5);
    private final ColorSetting color = new ColorSetting("Color", "Particle color", 255, 100, 255, 255);

    public ParticleChanger() {
        super("Particle Changer", "Customizes particles", ModuleCategory.VISUAL, 0);
        addSetting(mode);
        addSetting(particleType);
        addSetting(amount);
        addSetting(color);
    }
}
