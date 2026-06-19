package com.novaclient.core.module;

import com.novaclient.core.event.EventBus;
import com.novaclient.core.event.GameTickEvent;
import com.novaclient.core.event.EventListener;
import com.novaclient.core.event.Render2DEvent;
import com.novaclient.core.event.Render3DEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ModuleManager {
    private static final ModuleManager INSTANCE = new ModuleManager();
    private final List<Module> modules = new ArrayList<>();

    public static ModuleManager getInstance() {
        return INSTANCE;
    }

    public void init() {
        registerModules();
        EventBus.getInstance().register(this);
    }

    private void registerModules() {
        // PvP
        add(new com.novaclient.core.module.impl.pvp.OneSevenVisuals());
        add(new com.novaclient.core.module.impl.pvp.ArmorStatus());
        add(new com.novaclient.core.module.impl.pvp.PotionEffects());
        add(new com.novaclient.core.module.impl.pvp.ToggleSneak());
        add(new com.novaclient.core.module.impl.pvp.ToggleSprint());
        add(new com.novaclient.core.module.impl.pvp.Keystrokes());
        add(new com.novaclient.core.module.impl.pvp.CPSDisplay());
        add(new com.novaclient.core.module.impl.pvp.ReachDisplay());
        add(new com.novaclient.core.module.impl.pvp.ComboCounter());
        add(new com.novaclient.core.module.impl.pvp.Hitbox());
        add(new com.novaclient.core.module.impl.pvp.HitColor());
        add(new com.novaclient.core.module.impl.pvp.TNTCountdown());
        add(new com.novaclient.core.module.impl.pvp.PvPInfo());

        // HUD
        add(new com.novaclient.core.module.impl.hud.Coordinates());
        add(new com.novaclient.core.module.impl.hud.DirectionHUD());
        add(new com.novaclient.core.module.impl.hud.FPSDisplay());
        add(new com.novaclient.core.module.impl.hud.PingDisplay());
        add(new com.novaclient.core.module.impl.hud.ClockDisplay());
        add(new com.novaclient.core.module.impl.hud.DayCounter());
        add(new com.novaclient.core.module.impl.hud.MemoryUsage());
        add(new com.novaclient.core.module.impl.hud.ServerAddress());
        add(new com.novaclient.core.module.impl.hud.Speedometer());
        add(new com.novaclient.core.module.impl.hud.Playtime());

        // Visual
        add(new com.novaclient.core.module.impl.visual.Freelook());
        add(new com.novaclient.core.module.impl.visual.Zoom());
        add(new com.novaclient.core.module.impl.visual.MotionBlur());
        add(new com.novaclient.core.module.impl.visual.CrosshairEditor());
        add(new com.novaclient.core.module.impl.visual.ItemPhysics());
        add(new com.novaclient.core.module.impl.visual.GlintColorizer());
        add(new com.novaclient.core.module.impl.visual.BlockOutline());
        add(new com.novaclient.core.module.impl.visual.WeatherChanger());
        add(new com.novaclient.core.module.impl.visual.TimeChanger());
        add(new com.novaclient.core.module.impl.visual.ParticleChanger());
        add(new com.novaclient.core.module.impl.visual.FogCustomizer());
        add(new com.novaclient.core.module.impl.visual.ThreeDSkins());
        add(new com.novaclient.core.module.impl.visual.TwoDItems());
        add(new com.novaclient.core.module.impl.visual.ShinyPots());

        // Utility
        add(new com.novaclient.core.module.impl.utility.Waypoints());
        add(new com.novaclient.core.module.impl.utility.Saturation());
        add(new com.novaclient.core.module.impl.utility.ScrollableTooltips());
        add(new com.novaclient.core.module.impl.utility.PackOrganizer());
        add(new com.novaclient.core.module.impl.utility.ScreenshotUploader());
        add(new com.novaclient.core.module.impl.utility.AutoTextHotkeys());
        add(new com.novaclient.core.module.impl.utility.BetterSounds());
        add(new com.novaclient.core.module.impl.utility.ChunkBorders());
        add(new com.novaclient.core.module.impl.utility.ItemCounter());
        add(new com.novaclient.core.module.impl.utility.NickHider());
        add(new com.novaclient.core.module.impl.utility.ReplaySystem());

        // Server
        add(new com.novaclient.core.module.impl.server.HypixelMods());
        add(new com.novaclient.core.module.impl.server.BedwarsUtilities());
        add(new com.novaclient.core.module.impl.server.Quickplay());
        add(new com.novaclient.core.module.impl.server.SkyblockAddons());
        add(new com.novaclient.core.module.impl.server.WynncraftSupport());
    }

    private void add(Module module) {
        modules.add(module);
    }

    public List<Module> getModules() {
        return modules;
    }

    public List<Module> getModulesByCategory(ModuleCategory category) {
        return modules.stream()
            .filter(m -> m.getCategory() == category)
            .collect(Collectors.toList());
    }

    public Optional<Module> getModule(String name) {
        return modules.stream()
            .filter(m -> m.getName().equalsIgnoreCase(name))
            .findFirst();
    }

    public List<Module> getEnabledModules() {
        return modules.stream()
            .filter(Module::isEnabled)
            .collect(Collectors.toList());
    }

    public List<Module> search(String query) {
        String lower = query.toLowerCase();
        return modules.stream()
            .filter(m -> m.getName().toLowerCase().contains(lower)
                || m.getDescription().toLowerCase().contains(lower))
            .collect(Collectors.toList());
    }

    @EventListener
    public void onTick(GameTickEvent event) {
        for (Module module : modules) {
            if (module.isEnabled()) {
                module.onTick();
            }
        }
    }

    @EventListener
    public void onRender2D(Render2DEvent event) {
        for (Module module : modules) {
            if (module.isEnabled()) {
                module.onRender2D(event.getPartialTicks());
            }
        }
    }

    @EventListener
    public void onRender3D(Render3DEvent event) {
        for (Module module : modules) {
            if (module.isEnabled()) {
                module.onRender3D(event.getPartialTicks());
            }
        }
    }
}
