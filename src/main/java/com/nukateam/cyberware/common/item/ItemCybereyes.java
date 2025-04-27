package com.nukateam.cyberware.common.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.init.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.potion.PotionEffect;

import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import com.nukateam.cyberware.api.CyberwareAPI;
import com.nukateam.cyberware.api.CyberwareUpdateEvent;
import com.nukateam.cyberware.api.ICyberwareUserData;
import com.nukateam.cyberware.client.ClientUtils;
import com.nukateam.cyberware.common.handler.EssentialsMissingHandler;
import com.nukateam.cyberware.common.lib.LibConstants;

public class ItemCybereyes extends ItemCyberware {

    private static boolean isBlind;

    public ItemCybereyes(String name, EnumSlot slot) {
        super(name, slot);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public boolean isEssential(ItemStack stack) {
        return true;
    }

    @Override
    public boolean isIncompatible(ItemStack stack, ItemStack other) {
        return CyberwareAPI.getCyberware(other).isEssential(other);
    }

    @SubscribeEvent
    public void handleBlindnessImmunity(CyberwareUpdateEvent event) {
        LivingEntity entityLivingBase = event.getEntityLiving();
        if (!entityLivingBase.isPotionActive(MobEffects.BLINDNESS)) return;

        ICyberwareUserData cyberwareUserData = event.getCyberwareUserData();

        if (cyberwareUserData.isCyberwareInstalled(getCachedStack(0))) {
            entityLivingBase.removePotionEffect(MobEffects.BLINDNESS);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void handleMissingEssentials(CyberwareUpdateEvent event) {
        LivingEntity entityLivingBase = event.getEntityLiving();
        if (entityLivingBase.ticksExisted % 20 != 0) return;

        ICyberwareUserData cyberwareUserData = event.getCyberwareUserData();

        ItemStack itemStackCybereye = cyberwareUserData.getCyberware(getCachedStack(0));
        if (!itemStackCybereye.isEmpty()) {
            boolean isPowered = cyberwareUserData.usePower(itemStackCybereye, getPowerConsumption(itemStackCybereye));
            if (entityLivingBase.world.isRemote
                    && entityLivingBase == Minecraft.getInstance().player) {
                isBlind = !isPowered;
            }
        } else if (entityLivingBase.world.isRemote
                && entityLivingBase == Minecraft.getInstance().player) {
            isBlind = false;
        }

        if (isBlind) {
            entityLivingBase.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 40));
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void overlayPre(RenderGameOverlayEvent.Pre event) {
        if (event.getType() == ElementType.ALL) {
            Player entityPlayer = Minecraft.getInstance().player;

            if (isBlind && !entityPlayer.isCreative()) {
                GlStateManager.pushMatrix();
                GlStateManager.enableBlend();
                GlStateManager.color(1.0F, 1.0F, 1.0F, 0.9F);
                Minecraft.getInstance().getTextureManager().bindTexture(EssentialsMissingHandler.BLACK_PX);
                ClientUtils.drawTexturedModalRect(0, 0, 0, 0, Minecraft.getInstance().displayWidth, Minecraft.getInstance().displayHeight);
                GlStateManager.popMatrix();
            }
        }
    }

    @Override
    public int getPowerConsumption(ItemStack stack) {
        return LibConstants.CYBEREYES_CONSUMPTION;
    }
}
