package com.novaclient.module;

import java.util.ArrayList;
import java.util.List;

public class ModuleManager {

    private final List<Module> modules = new ArrayList<>();

    public void init() {
        // Combat
        register(new Module("KillAura", Category.COMBAT, "Automatically attacks nearby entities"));
        register(new Module("Reach", Category.COMBAT, "Extends attack reach distance"));
        register(new Module("AutoClicker", Category.COMBAT, "Automatically clicks for you"));
        register(new Module("Velocity", Category.COMBAT, "Reduces knockback received"));
        register(new Module("CriticalHit", Category.COMBAT, "Always lands critical hits"));
        register(new Module("AntiFireball", Category.COMBAT, "Deflects fireballs"));

        // Movement
        register(new Module("Sprint", Category.MOVEMENT, "Auto sprints in all directions"));
        register(new Module("NoFall", Category.MOVEMENT, "Prevents fall damage"));
        register(new Module("Speed", Category.MOVEMENT, "Increases movement speed"));
        register(new Module("Fly", Category.MOVEMENT, "Allows creative flight in survival"));
        register(new Module("Sneak", Category.MOVEMENT, "Permanently sneaks to prevent edge fall"));
        register(new Module("FastLadder", Category.MOVEMENT, "Climb ladders at max speed"));

        // Visual
        register(new Module("ESP", Category.VISUAL, "Draws boxes around entities"));
        register(new Module("Tracers", Category.VISUAL, "Draws lines to entities"));
        register(new Module("FullBright", Category.VISUAL, "Removes all darkness effects"));
        register(new Module("NoWeather", Category.VISUAL, "Hides rain and snow effects"));
        register(new Module("Xray", Category.VISUAL, "Highlights ores through walls"));
        register(new Module("Chams", Category.VISUAL, "Colors player models through walls"));
        register(new Module("AntiBlind", Category.VISUAL, "Removes blindness and darkness effects"));

        // HUD
        register(new Module("FPS", Category.HUD, "Shows current FPS on screen"));
        register(new Module("Coordinates", Category.HUD, "Shows X Y Z coordinates"));
        register(new Module("Armor", Category.HUD, "Displays armor durability"));
        register(new Module("PotionTimer", Category.HUD, "Shows active potion effects"));
        register(new Module("Ping", Category.HUD, "Shows server ping"));
        register(new Module("Direction", Category.HUD, "Shows compass direction"));
        register(new Module("Clock", Category.HUD, "Displays current time"));
        register(new Module("ModList", Category.HUD, "Lists active modules on screen"));
        register(new Module("Reach Display", Category.HUD, "Shows current reach distance"));

        // World
        register(new Module("NoClip", Category.WORLD, "Phase through blocks"));
        register(new Module("AutoTool", Category.WORLD, "Auto-selects best tool for blocks"));
        register(new Module("FastBreak", Category.WORLD, "Breaks blocks faster"));
        register(new Module("ChestESP", Category.WORLD, "Highlights chests and containers"));
        register(new Module("Scaffold", Category.WORLD, "Auto places blocks below you"));

        // Misc
        register(new Module("AntiAFK", Category.MISC, "Prevents AFK kick with subtle movement"));
        register(new Module("AutoRespawn", Category.MISC, "Automatically respawns on death"));
        register(new Module("NameProtect", Category.MISC, "Hides your IGN from own screen"));
        register(new Module("FakeLag", Category.MISC, "Simulates packet lag"));
    }

    private void register(Module m) {
        modules.add(m);
    }

    public List<Module> getModules() { return modules; }

    public List<Module> getByCategory(Category cat) {
        List<Module> result = new ArrayList<>();
        for (Module m : modules) {
            if (m.getCategory() == cat) result.add(m);
        }
        return result;
    }

    public enum Category {
        COMBAT, MOVEMENT, VISUAL, HUD, WORLD, MISC
    }
}
