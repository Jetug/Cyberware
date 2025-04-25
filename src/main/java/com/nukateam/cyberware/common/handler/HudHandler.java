package com.nukateam.cyberware.common.handler;

import com.mojang.blaze3d.vertex.PoseStack;
import com.nukateam.cyberware.Cyberware;
import com.nukateam.cyberware.api.CyberwareAPI;
import com.nukateam.cyberware.api.ICyberwareUserData;
import com.nukateam.cyberware.api.hud.*;
import com.nukateam.cyberware.api.item.IHudjack;
import com.nukateam.cyberware.client.KeyBinds;
import com.nukateam.cyberware.client.gui.GuiHudConfiguration;
import com.nukateam.cyberware.client.gui.hud.*;
import com.nukateam.cyberware.common.CyberwareConfig;
import com.nukateam.cyberware.common.CyberwareContent2;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class HudHandler {
    public static final HudHandler INSTANCE = new HudHandler();

    private static class NotificationStack<T> extends Stack<T> {
        private final int maxSize;

        public NotificationStack(int size) {
            super();
            this.maxSize = size;
        }

        @Override
        public T push(T object) {
            while (this.size() >= maxSize) {
                this.remove(0);
            }
            return super.push(object);
        }
    }

    public static void addNotification(NotificationInstance notification) {
        notifications.push(notification);
    }

    public static final ResourceLocation HUD_TEXTURE = new ResourceLocation(Cyberware.MOD_ID, "textures/gui/hud.png");
    public static final Stack<NotificationInstance> notifications = new NotificationStack<>(5);

    private static final PowerDisplay powerDisplay = new PowerDisplay();
    private static final MissingPowerDisplay missingPowerDisplay = new MissingPowerDisplay();
    private static final NotificationDisplay notificationDisplay = new NotificationDisplay();

    static {
        notificationDisplay.setHorizontalAnchor(EnumAnchorHorizontal.LEFT);
        notificationDisplay.setVerticalAnchor(EnumAnchorVertical.BOTTOM);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void addHudElements(CyberwareHudEvent event) {
        if (event.isHudjackAvailable()) {
            event.addElement(powerDisplay);
            event.addElement(missingPowerDisplay);
            event.addElement(notificationDisplay);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void saveHudElements(CyberwareHudDataEvent event) {
        event.addElement(powerDisplay);
        event.addElement(missingPowerDisplay);
        event.addElement(notificationDisplay);
    }

    private int cache_tickExisted = 0;
    private float cache_floatingFactor = 0.0F;
    private List<IHudElement> cache_hudElements = new ArrayList<>();
    private boolean cache_isHUDjackAvailable = false;
    private boolean cache_promptToOpenMenu = false;
    private int cache_hudColorHex = 0x00FFFF;

    private int lastTickExisted = 0;
    private double lastVelX = 0;
    private double lastVelY = 0;
    private double lastVelZ = 0;
    private double lastLastVelX = 0;
    private double lastLastVelY = 0;
    private double lastLastVelZ = 0;

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onRender(@Nonnull RenderGuiOverlayEvent.Pre event) {
        if (event.getOverlay() == VanillaGuiOverlay.CHAT_PANEL.type()) {
            drawHUD(event.getGuiGraphics(), event.getPartialTick());
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void drawHUD(GuiGraphics guiGraphics, float partialTick) {
        Minecraft mc = Minecraft.getInstance();
        var player = mc.player;
        if (player == null) return;

        if (player.tickCount != cache_tickExisted) {
            cache_tickExisted = player.tickCount;

            ICyberwareUserData cyberwareUserData = CyberwareAPI.getCapabilityOrNull(player);
            if (cyberwareUserData == null) return;

            cache_floatingFactor = 0.0F;
            boolean isHUDjackAvailable = false;

            List<ItemStack> listHUDjackItems = cyberwareUserData.getHudjackItems();
            for (ItemStack stack : listHUDjackItems) {
                if (((IHudjack) CyberwareAPI.getCyberware(stack)).isActive(stack)) {
                    isHUDjackAvailable = true;
                    if (CyberwareConfig.ENABLE_FLOAT.get()) {
                        if (CyberwareAPI.getCyberware(stack) == CyberwareContent2.EYE_UPGRADES.get()) {
                            cache_floatingFactor = CyberwareConfig.HUDLENS_FLOAT.get().floatValue();
                        } else {
                            cache_floatingFactor = CyberwareConfig.HUDJACK_FLOAT.get().floatValue();
                        }
                    }
                    break;
                }
            }

            CyberwareHudEvent hudEvent = new CyberwareHudEvent(guiGraphics,
                    mc.getWindow().getGuiScaledWidth(),
                    mc.getWindow().getGuiScaledHeight(),
                    isHUDjackAvailable);
            MinecraftForge.EVENT_BUS.post(hudEvent);
            cache_hudElements = hudEvent.getElements();
            cache_isHUDjackAvailable = hudEvent.isHudjackAvailable();
            cache_promptToOpenMenu = cyberwareUserData.getActiveItems().size() > 0
                    && !cyberwareUserData.hasOpenedRadialMenu();
            cache_hudColorHex = cyberwareUserData.getHudColorHex();
        }

        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();

        double accelLastY = lastVelY - lastLastVelY;
        double accelY = player.getDeltaMovement().y - lastVelY;
        double accelPitch = accelLastY + (accelY - accelLastY) * (partialTick + player.tickCount - lastTickExisted) / 2F;

        double pitchCameraMove = cache_floatingFactor * ((player.xRotO + (player.getXRot() - player.xRotO) * partialTick) - player.getXRot());
        double yawCameraMove = cache_floatingFactor * ((player.yRotO + (player.getYRot() - player.yRotO) * partialTick) - player.getYRot();

        poseStack.translate(yawCameraMove, pitchCameraMove + accelPitch * 50F * cache_floatingFactor, 0);

        if (player.tickCount > lastTickExisted + 1) {
            lastTickExisted = player.tickCount;
            lastLastVelX = lastVelX;
            lastLastVelY = lastVelY;
            lastLastVelZ = lastVelZ;
            lastVelX = player.getDeltaMovement().x;
            lastVelY = player.getDeltaMovement().y;
            lastVelZ = player.getDeltaMovement().z;
        }

        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        for (IHudElement hudElement : cache_hudElements) {
            if (hudElement.getHeight() + GuiHudConfiguration.getAbsoluteY(hudElement) <= 3) {
                GuiHudConfiguration.setYFromAbsolute(hudElement, -hudElement.getHeight() + 4);
            }

            if (GuiHudConfiguration.getAbsoluteY(hudElement) >= screenHeight - 3) {
                GuiHudConfiguration.setYFromAbsolute(hudElement, screenHeight - 4);
            }

            if (hudElement.getWidth() + GuiHudConfiguration.getAbsoluteX(hudElement) <= 3) {
                GuiHudConfiguration.setXFromAbsolute(hudElement, -hudElement.getWidth() + 4);
            }

            if (GuiHudConfiguration.getAbsoluteX(hudElement) >= screenWidth - 3) {
                GuiHudConfiguration.setXFromAbsolute(hudElement, screenWidth - 4);
            }

            hudElement.render(guiGraphics, player, cache_isHUDjackAvailable,
                    mc.screen instanceof GuiHudConfiguration, partialTick);
        }

        // Display a prompt to the user to open the radial menu if they haven't yet
        if (cache_promptToOpenMenu) {
            Component textOpenMenu = Component.translatable("cyberware.gui.open_menu",
                    KeyBinds.menu.getTranslatedKeyMessage());
            Font font = mc.font;
            guiGraphics.drawString(font, textOpenMenu,
                    screenWidth - font.width(textOpenMenu) - 5, 5, cache_hudColorHex);
        }

        poseStack.popPose();
    }
}