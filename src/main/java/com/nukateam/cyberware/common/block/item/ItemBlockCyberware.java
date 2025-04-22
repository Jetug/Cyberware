package com.nukateam.cyberware.common.block.item;

import java.util.List;

import com.mojang.realmsclient.gui.ChatFormatting;

import com.nukateam.cyberware.api.item.ICyberwareTabItem;
import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemBlock;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

public class ItemBlockCyberware extends ItemBlock implements ICyberwareTabItem {
    private String[] tt;

    public ItemBlockCyberware(Block block) {
        super(block);
    }

    public ItemBlockCyberware(Block block, String... tooltip) {
        super(block);
        this.tt = tooltip;
    }

    @Override
    public EnumCategory getCategory(ItemStack stack) {
        return EnumCategory.BLOCKS;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag advanced) {
        if (this.tt != null) {
            for (String str : tt) {
                tooltip.add(ChatFormatting.GRAY + I18n.format(str));
            }
        }
    }
}
