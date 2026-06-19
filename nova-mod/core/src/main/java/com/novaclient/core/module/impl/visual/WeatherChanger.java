package com.novaclient.core.module.impl.visual;

import com.novaclient.core.module.Module;
import com.novaclient.core.module.ModuleCategory;
import com.novaclient.core.module.setting.ModeSetting;
import com.novaclient.core.module.setting.NumberSetting;

public class WeatherChanger extends Module {
    private final ModeSetting weather = new ModeSetting("Weather", "Weather type", "Clear", "Rain", "Thunder");
    private final NumberSetting intensity = new NumberSetting("Intensity", "Weather intensity", 1.0, 0.1, 10.0, 0.1);

    public WeatherChanger() {
        super("Weather Changer", "Changes weather client-side", ModuleCategory.VISUAL, 0);
        addSetting(weather);
        addSetting(intensity);
    }

    public String getWeather() { return weather.getMode(); }
    public float getIntensity() { return intensity.getFloatValue(); }
}
