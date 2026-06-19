package com.novaclient.core.module.impl.utility;

import com.novaclient.core.module.Module;
import com.novaclient.core.module.ModuleCategory;
import com.novaclient.core.module.setting.ColorSetting;
import com.novaclient.core.module.setting.NumberSetting;
import java.util.ArrayList;
import java.util.List;

public class Waypoints extends Module {
    private final ColorSetting waypointColor = new ColorSetting("Color", "Waypoint color", 255, 80, 80, 255);
    private final NumberSetting renderDistance = new NumberSetting("Distance", "Render distance", 100.0, 10.0, 500.0, 10.0);
    private final List<WaypointData> waypoints = new ArrayList<>();

    public Waypoints() {
        super("Waypoints", "Place waypoints in the world", ModuleCategory.UTILITY, 0);
        addSetting(waypointColor);
        addSetting(renderDistance);
    }

    public void addWaypoint(String name, double x, double y, double z) {
        waypoints.add(new WaypointData(name, x, y, z));
    }

    public void removeWaypoint(String name) {
        waypoints.removeIf(w -> w.name.equals(name));
    }

    public List<WaypointData> getWaypoints() {
        return waypoints;
    }

    public static class WaypointData {
        public final String name;
        public final double x, y, z;

        public WaypointData(String name, double x, double y, double z) {
            this.name = name;
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }
}
