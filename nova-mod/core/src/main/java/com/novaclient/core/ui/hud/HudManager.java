package com.novaclient.core.ui.hud;

import com.novaclient.core.ui.render.RenderUtil;
import com.novaclient.core.ui.render.ColorUtil;
import java.util.ArrayList;
import java.util.List;

public class HudManager {
    private static final HudManager INSTANCE = new HudManager();
    private final List<HudElement> elements = new ArrayList<>();
    private HudElement draggingElement = null;

    public static HudManager getInstance() {
        return INSTANCE;
    }

    public void init() {
        // Register default HUD elements
        addElement(new com.novaclient.core.ui.hud.impl.CoordsElement(5, 5));
        addElement(new com.novaclient.core.ui.hud.impl.FpsElement(5, 20));
        addElement(new com.novaclient.core.ui.hud.impl.PingElement(5, 35));
        addElement(new com.novaclient.core.ui.hud.impl.DirectionElement(5, 50));
        addElement(new com.novaclient.core.ui.hud.impl.ClockElement(5, 65));
        addElement(new com.novaclient.core.ui.hud.impl.MemoryElement(5, 80));
        addElement(new com.novaclient.core.ui.hud.impl.ServerElement(5, 95));
    }

    public void addElement(HudElement element) {
        elements.add(element);
    }

    public void removeElement(HudElement element) {
        elements.remove(element);
    }

    public List<HudElement> getElements() {
        return elements;
    }

    public void renderAll(float partialTicks) {
        for (HudElement element : elements) {
            if (element.isVisible()) {
                element.render(partialTicks);
            }
        }
    }

    public void updateAll() {
        for (HudElement element : elements) {
            element.update();
        }
    }

    public void mouseClicked(float mouseX, float mouseY, int button) {
        if (button == 0) {
            for (int i = elements.size() - 1; i >= 0; i--) {
                HudElement element = elements.get(i);
                if (element.isVisible() && element.isHovered(mouseX, mouseY)) {
                    draggingElement = element;
                    element.startDrag(mouseX, mouseY);
                    return;
                }
            }
        }
    }

    public void mouseMoved(float mouseX, float mouseY) {
        if (draggingElement != null) {
            draggingElement.drag(mouseX, mouseY);
        }
    }

    public void mouseReleased(int button) {
        if (button == 0 && draggingElement != null) {
            draggingElement.stopDrag();
            draggingElement = null;
        }
    }
}
