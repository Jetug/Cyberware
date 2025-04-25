package com.nukateam.cyberware.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.nukateam.cyberware.api.CyberwareAPI;
import com.nukateam.cyberware.api.ICyberwareUserData;
import com.nukateam.cyberware.api.hud.CyberwareHudDataEvent;
import com.nukateam.cyberware.api.hud.CyberwareHudEvent;
import com.nukateam.cyberware.api.hud.IHudElement;
import com.nukateam.cyberware.api.hud.IHudElement.EnumAnchorHorizontal;
import com.nukateam.cyberware.api.hud.IHudElement.EnumAnchorVertical;
import com.nukateam.cyberware.api.item.IHudjack;
import com.nukateam.cyberware.client.ClientUtils;
import com.nukateam.cyberware.client.gui.hud.HudNBTData;
import com.nukateam.cyberware.common.handler.HudHandler;
import com.nukateam.cyberware.common.network.CyberwarePacketHandler;
import com.nukateam.cyberware.common.network.SyncHudDataPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;

import java.util.ArrayList;
import java.util.List;

public class GuiHudConfiguration extends Screen {
    private IHudElement dragging = null;
    private IHudElement hoveredElement = null;
    private int offsetX = 0;
    private int offsetY = 0;
    private boolean clicked = false;
    private static final ResourceLocation HUD_TEXTURE = new ResourceLocation("cyberware", "textures/gui/hud.png");

    protected GuiHudConfiguration() {
        super(Component.empty());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);

        Minecraft mc = Minecraft.getInstance();
        boolean active = false;
        ICyberwareUserData cyberwareUserData = CyberwareAPI.getCapabilityOrNull(mc.player);
        if (cyberwareUserData != null) {
            List<ItemStack> hudjackItems = cyberwareUserData.getHudjackItems();
            for (ItemStack stack : hudjackItems) {
                if (((IHudjack) CyberwareAPI.getCyberware(stack)).isActive(stack)) {
                    active = true;
                    break;
                }
            }
        }

        CyberwareHudEvent hudEvent = new CyberwareHudEvent(guiGraphics, active);
        MinecraftForge.EVENT_BUS.post(hudEvent);
        List<IHudElement> elements = hudEvent.getElements();

        hoveredElement = null;
        for (IHudElement element : elements) {
            if (hoveredElement == null && dragging == null) {
                int elemX = getAbsoluteX(element);
                int elemY = getAbsoluteY(element);

                if (isPointInRegion(elemX, elemY, element.getWidth(), element.getHeight(), mouseX, mouseY)) {
                    hoveredElement = element;
                    offsetX = mouseX - elemX;
                    offsetY = mouseY - elemY;
                }
            }
        }

        for (IHudElement element : elements) {
            drawBox(guiGraphics, element, mouseX, mouseY);
            drawButtons(guiGraphics, element, mouseX, mouseY);
        }

        for (IHudElement element : elements) {
            drawButtonTooltips(guiGraphics, element, mouseX, mouseY);
        }

        if (dragging != null) {
            int moveToX = mouseX - offsetX;
            int moveToY = mouseY - offsetY;

            setXFromAbsolute(dragging, moveToX);
            setYFromAbsolute(dragging, moveToY);

            if (mc.options.keyShift.isDown()) {
                dragging.setX(Math.round(dragging.getX() / 5F) * 5);
                dragging.setY(Math.round(dragging.getY() / 5F) * 5);
            }

            List<Component> l = new ArrayList<>();
            l.add(Component.literal(dragging.getX() + ", " + dragging.getY()));
            guiGraphics.renderTooltip(font, l, mouseX, mouseY);
        } else if (hoveredElement != null) {
            List<Component> l = new ArrayList<>();
            l.add(Component.literal(hoveredElement.getX() + ", " + hoveredElement.getY()));
            guiGraphics.renderTooltip(font, l, mouseX, mouseY);
        }

        clicked = false;
    }

    private void drawBox(GuiGraphics guiGraphics, IHudElement element, int mouseX, int mouseY) {
        int elemX = getAbsoluteX(element) - 1;
        int elemY = getAbsoluteY(element) - 1;

        float[] color = CyberwareAPI.getHUDColor();
        guiGraphics.setColor(color[0], color[1], color[2], 1.0F);

        if (element == dragging || element == hoveredElement) {
            boolean right = element.getHorizontalAnchor() == EnumAnchorHorizontal.RIGHT;
            boolean bottom = element.getVerticalAnchor() == EnumAnchorVertical.BOTTOM;

            int elemPosX = element.getX();
            int pos = right ? getAbsoluteX(element) + element.getWidth() : 0;
            int posY = bottom ? elemY + element.getHeight() + 1 : elemY;
            posY = Math.max(1, posY);
            posY = Math.min(this.height - 2, posY);

            while (elemPosX >= 2) {
                guiGraphics.blit(HUD_TEXTURE, pos, posY, 255, 0, 1, 1);
                pos += 2;
                elemPosX -= 2;
            }
            guiGraphics.blit(HUD_TEXTURE, pos, posY, 255, 0, elemPosX, 1);

            int elemPosY = element.getY();
            pos = bottom ? getAbsoluteY(element) + element.getHeight() : 0;
            int posX = right ? elemX + element.getWidth() + 1 : elemX;
            posX = Math.max(1, posX);
            posX = Math.min(this.width - 2, posX);

            while (elemPosY >= 2) {
                guiGraphics.blit(HUD_TEXTURE, posX, pos, 255, 0, 1, 1);
                pos += 2;
                elemPosY -= 2;
            }
            guiGraphics.blit(HUD_TEXTURE, posX, pos, 255, 0, 1, elemPosY);
        }

        boolean shift = (Minecraft.getInstance().player.tickCount / 4) % 2 == 0;
        int one = shift ? 254 : 255;
        int two = shift ? 255 : 254;

        int width = element.getWidth() + 2;
        int pos = 0;
        while (width >= 2) {
            guiGraphics.blit(HUD_TEXTURE, elemX + pos, elemY, one, 0, 1, 1);
            guiGraphics.blit(HUD_TEXTURE, elemX + pos + 1, elemY, two, 0, 1, 1);

            guiGraphics.blit(HUD_TEXTURE, elemX + pos, elemY + element.getHeight() + 1, one, 0, 1, 1);
            guiGraphics.blit(HUD_TEXTURE, elemX + pos + 1, elemY + element.getHeight() + 1, two, 0, 1, 1);

            pos += 2;
            width -= 2;
        }
        guiGraphics.blit(HUD_TEXTURE, elemX, elemY, one, 0, width, 1);
        guiGraphics.blit(HUD_TEXTURE, elemX, elemY + element.getHeight() + 1, 255, 0, width, 1);

        int height = element.getHeight() + 2;
        pos = 0;
        while (height >= 2) {
            guiGraphics.blit(HUD_TEXTURE, elemX, elemY + pos, one, 0, 1, 1);
            guiGraphics.blit(HUD_TEXTURE, elemX, elemY + pos + 1, two, 0, 1, 1);

            guiGraphics.blit(HUD_TEXTURE, elemX + element.getWidth() + 1, elemY + pos, one, 0, 1, 1);
            guiGraphics.blit(HUD_TEXTURE, elemX + element.getWidth() + 1, elemY + pos + 1, two, 0, 1, 1);

            pos += 2;
            height -= 2;
        }

        guiGraphics.blit(HUD_TEXTURE, elemX, elemY + pos, one, 0, 1, height);
        guiGraphics.blit(HUD_TEXTURE, elemX + element.getWidth() + 1, elemY + pos, two, 0, 1, height);

        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    protected boolean isPointInRegion(int rectX, int rectY, int rectWidth, int rectHeight, int pointX, int pointY) {
        return pointX >= rectX - 1 && pointX < rectX + rectWidth + 1
                && pointY >= rectY - 1 && pointY < rectY + rectHeight + 1;
    }

    private void drawButtonTooltips(GuiGraphics guiGraphics, IHudElement element, int mouseX, int mouseY) {
        int elemX = getAbsoluteX(element) - 1;
        int elemY = getAbsoluteY(element) - 1;

        int buttonsY = (elemY + element.getHeight() + 10 > this.height) ? elemY - 11 : (elemY + element.getHeight() + 4);
        int buttonsX = elemX + 5;

        boolean showHideHover = false;
        boolean hidden = false;
        if (element.canHide()) {
            showHideHover = isPointInRegion(buttonsX, buttonsY, 9, 9, mouseX, mouseY);
            hidden = element.isHidden();
            buttonsX += 11;
        }

        boolean upDownHover = isPointInRegion(buttonsX, buttonsY, 9, 9, mouseX, mouseY);
        boolean down = element.getVerticalAnchor() != EnumAnchorVertical.BOTTOM;
        buttonsX += 11;

        boolean leftRightHover = isPointInRegion(buttonsX, buttonsY, 9, 9, mouseX, mouseY);
        boolean right = element.getHorizontalAnchor() != EnumAnchorHorizontal.RIGHT;
        buttonsX += 11;

        boolean resetHover = isPointInRegion(buttonsX, buttonsY, 9, 9, mouseX, mouseY);

        if (upDownHover) {
            List<Component> l = new ArrayList<>();
            l.add(Component.translatable(down ? "cyberware.gui.stickDown" : "cyberware.gui.stickUp"));
            guiGraphics.renderTooltip(font, l, mouseX, mouseY);

            if (clicked) {
                flipVertical(element);
            }
        }

        if (showHideHover) {
            List<Component> l = new ArrayList<>();
            l.add(Component.translatable(hidden ? "cyberware.gui.show" : "cyberware.gui.hide"));
            guiGraphics.renderTooltip(font, l, mouseX, mouseY);

            if (clicked) {
                element.setHidden(!hidden);
            }
        }

        if (resetHover) {
            List<Component> l = new ArrayList<>();
            l.add(Component.translatable("cyberware.gui.reset_hud"));
            guiGraphics.renderTooltip(font, l, mouseX, mouseY);

            if (clicked) {
                element.reset();
            }
        }

        if (leftRightHover) {
            List<Component> l = new ArrayList<>();
            l.add(Component.translatable(right ? "cyberware.gui.stick_right" : "cyberware.gui.stick_left"));
            guiGraphics.renderTooltip(font, l, mouseX, mouseY);

            if (clicked) {
                flipHorizontal(element);
            }
        }
    }

    private void drawButtons(GuiGraphics guiGraphics, IHudElement element, int mouseX, int mouseY) {
        int elemX = getAbsoluteX(element) - 1;
        int elemY = getAbsoluteY(element) - 1;

        int buttonsY = (elemY + element.getHeight() + 10 > this.height) ? elemY - 11 : (elemY + element.getHeight() + 4);
        int buttonsX = elemX + 5;

        if (element.canHide()) {
            boolean showHideHover = isPointInRegion(buttonsX, buttonsY, 9, 9, mouseX, mouseY);
            boolean hidden = element.isHidden();
            guiGraphics.blit(HUD_TEXTURE, buttonsX, buttonsY, showHideHover ^ hidden ? 125 : 116, 0, 9, 9);
            buttonsX += 11;
        }

        boolean upDownHover = isPointInRegion(buttonsX, buttonsY, 9, 9, mouseX, mouseY);
        boolean down = element.getVerticalAnchor() != EnumAnchorVertical.BOTTOM;
        guiGraphics.blit(HUD_TEXTURE, buttonsX, buttonsY, down ^ upDownHover ? 80 : 89, 0, 9, 9);
        buttonsX += 11;

        boolean leftRightHover = isPointInRegion(buttonsX, buttonsY, 9, 9, mouseX, mouseY);
        boolean right = element.getHorizontalAnchor() != EnumAnchorHorizontal.RIGHT;
        guiGraphics.blit(HUD_TEXTURE, buttonsX, buttonsY, right ^ leftRightHover ? 98 : 107, 0, 9, 9);
        buttonsX += 11;

        boolean resetHover = isPointInRegion(buttonsX, buttonsY, 9, 9, mouseX, mouseY);
        guiGraphics.blit(HUD_TEXTURE, buttonsX, buttonsY, 134, 0, 9, 9);
    }


    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (mouseButton == 0) {
            if (dragging == null) {
                dragging = hoveredElement;
            }
            clicked = true;
        }
        if (mouseButton == 1 && hoveredElement != null) {
            flipVertical(hoveredElement);
        }
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
        if (mouseButton == 0 && dragging != null) {
            dragging = null;
        }
        return super.mouseReleased(mouseX, mouseY, mouseButton);
    }

    public int getAbsoluteX(IHudElement element) {
        if (element.getHorizontalAnchor() == EnumAnchorHorizontal.RIGHT) {
            return this.width - element.getX() - element.getWidth();
        }
        return element.getX();
    }

    public int getAbsoluteY(IHudElement element) {
        if (element.getVerticalAnchor() == EnumAnchorVertical.BOTTOM) {
            return this.height - element.getY() - element.getHeight();
        }
        return element.getY();
    }

    public void setXFromAbsolute(IHudElement element, int x) {
        if (element.getHorizontalAnchor() == EnumAnchorHorizontal.RIGHT) {
            element.setX(this.width - x - element.getWidth());
        } else {
            element.setX(x);
        }
    }

    public void setYFromAbsolute(IHudElement element, int y) {
        if (element.getVerticalAnchor() == EnumAnchorVertical.BOTTOM) {
            element.setY(this.height - y - element.getHeight());
        } else {
            element.setY(y);
        }
    }

    private void flipVertical(IHudElement element) {
        int y = getAbsoluteY(element);
        element.setVerticalAnchor(element.getVerticalAnchor() == EnumAnchorVertical.BOTTOM ?
                EnumAnchorVertical.TOP : EnumAnchorVertical.BOTTOM);
        setYFromAbsolute(element, y);
    }

    private void flipHorizontal(IHudElement element) {
        int x = getAbsoluteX(element);
        element.setHorizontalAnchor(element.getHorizontalAnchor() == EnumAnchorHorizontal.RIGHT ?
                EnumAnchorHorizontal.LEFT : EnumAnchorHorizontal.RIGHT);
        setXFromAbsolute(element, x);
    }

    @Override
    public void tick() {
        if (minecraft != null && minecraft.options != null) {
            if (minecraft.options.keyInventory.isDown()) {
                minecraft.setScreen(null);
            }
        }
        super.tick();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        CompoundTag tagCompound = new CompoundTag();

        CyberwareHudDataEvent hudEvent = new CyberwareHudDataEvent();
        MinecraftForge.EVENT_BUS.post(hudEvent);
        List<IHudElement> elements = hudEvent.getElements();

        for (IHudElement element : elements) {
            HudNBTData elementData = new HudNBTData(new CompoundTag());
            element.save(elementData);
            tagCompound.put(element.getUniqueName(), elementData.getTag());
        }

        ICyberwareUserData cyberwareUserData = CyberwareAPI.getCapabilityOrNull(minecraft.player);
        if (cyberwareUserData != null) {
            cyberwareUserData.setHudData(tagCompound);
        }

        CyberwarePacketHandler.INSTANCE.sendToServer(new SyncHudDataPacket(tagCompound));
        super.onClose();
    }
}