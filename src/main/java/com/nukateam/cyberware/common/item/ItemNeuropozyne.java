package com.nukateam.cyberware.common.item;

import java.util.List;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import com.nukateam.cyberware.Cyberware;
import com.nukateam.cyberware.common.CyberwareContent2;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ItemNeuropozyne extends Item {
    public ItemNeuropozyne(String name) {
        super();

        setRegistryName(name);
        ForgeRegistries.ITEMS.register(this);
        setTranslationKey(Cyberware.MODID + "." + name);

        setCreativeTab(Cyberware.creativeTab);

        setMaxDamage(0);

        CyberwareContent2.items.add(this);
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, Player entityPlayer, @Nonnull EnumHand hand) {
        ItemStack stack = entityPlayer.getHeldItem(hand);

        if (!entityPlayer.capabilities.isCreativeMode) {
            stack.shrink(1);
        }

        entityPlayer.addPotionEffect(new PotionEffect(CyberwareContent2.neuropozyneEffect, 24000, 0, false, false));

        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        String neuropozyne = I18n.format("cyberware.tooltip.neuropozyne");

        tooltip.add(ChatFormatting.BLUE + neuropozyne);
    }
}
