package com.nukateam.cyberware.api.hud;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.eventbus.api.Event;

import java.util.ArrayList;
import java.util.List;

public class CyberwareHudEvent extends Event {
    private final List<IHudElement> elements = new ArrayList<>();
    private boolean hudjackAvailable;
    private final GuiGraphics guiGraphics;

    public CyberwareHudEvent(GuiGraphics guiGraphics, boolean hudjackAvailable) {
        this.guiGraphics = guiGraphics;
        this.hudjackAvailable = hudjackAvailable;
    }

    public GuiGraphics getGuiGraphics() {
        return guiGraphics;
    }

    public boolean isHudjackAvailable() {
        return hudjackAvailable;
    }

    public void setHudjackAvailable(boolean hudjackAvailable) {
        this.hudjackAvailable = hudjackAvailable;
    }

    public List<IHudElement> getElements() {
        return elements;
    }

    public void addElement(IHudElement element) {
        elements.add(element);
    }
}