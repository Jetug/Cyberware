package com.nukateam.cyberware.common.item;

import javax.annotation.Nonnull;

import net.minecraft.block.BlockCauldron;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.core.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import com.nukateam.cyberware.Cyberware;
import com.nukateam.cyberware.api.item.IDeconstructable;
import com.nukateam.cyberware.client.ClientUtils;
import com.nukateam.cyberware.common.CyberwareContent2;

public class ItemArmorCyberware extends ItemArmor implements IDeconstructable {

    public ItemArmorCyberware(String name, ArmorMaterial materialIn, int renderIndexIn, EntityEquipmentSlot equipmentSlotIn) {
        super(materialIn, renderIndexIn, equipmentSlotIn);

        setRegistryName(name);
        ForgeRegistries.ITEMS.register(this);
        setTranslationKey(Cyberware.MODID + "." + name);

        setCreativeTab(Cyberware.creativeTab);

        CyberwareContent2.items.add(this);
    }

    @Override
    public boolean canDestroy(ItemStack stack) {
        return true;
    }

    @Override
    public NonNullList<ItemStack> getComponents(ItemStack stack) {
        Item item = stack.getItem();

        NonNullList<ItemStack> nnl = NonNullList.create();
        if (item == CyberwareContent2.trenchCoat) {
            nnl.add(new ItemStack(CyberwareContent2.component, 2, 2));
            nnl.add(new ItemStack(Items.LEATHER, 12, 0));
            nnl.add(new ItemStack(Items.DYE, 1, 0));
        } else if (item == CyberwareContent2.jacket) {
            nnl.add(new ItemStack(CyberwareContent2.component, 1, 2));
            nnl.add(new ItemStack(Items.LEATHER, 8, 0));
            nnl.add(new ItemStack(Items.DYE, 1, 0));
        } else {
            nnl.add(new ItemStack(Blocks.STAINED_GLASS, 4, 15));
            nnl.add(new ItemStack(CyberwareContent2.component, 1, 4));
        }
        return nnl;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public ModelBiped getArmorModel(LivingEntity entityLivingBase, ItemStack itemStack, EntityEquipmentSlot armorSlot, ModelBiped _default) {
        if (!itemStack.isEmpty()
                && itemStack.getItem() == CyberwareContent2.trenchCoat) {
            ClientUtils.modelTrenchCoat.setDefaultModel(_default);
            return ClientUtils.modelTrenchCoat;
        }

        return null;
    }

    @Override
    public boolean hasColor(@Nonnull ItemStack stack) {
        if (getArmorMaterial() != CyberwareContent2.trenchMat) {
            return false;
        }

        CompoundTag tagCompound = stack.getTagCompound();
        return tagCompound != null
                && tagCompound.hasKey("display", 10)
                && tagCompound.getCompoundTag("display").hasKey("color", 3);
    }

    @Override
    public int getColor(@Nonnull ItemStack stack) {
        if (getArmorMaterial() != CyberwareContent2.trenchMat) {
            return 16777215;
        } else {
            CompoundTag tagCompound = stack.getTagCompound();

            if (tagCompound != null) {
                CompoundTag tagCompoundDisplay = tagCompound.getCompoundTag("display");

                if (tagCompoundDisplay.hasKey("color", 3)) {
                    return tagCompoundDisplay.getInteger("color");
                }
            }

            return 0x333333; // 0x664028
        }
    }

    @Override
    public void removeColor(@Nonnull ItemStack stack) {
        if (getArmorMaterial() == CyberwareContent2.trenchMat) {
            CompoundTag tagCompound = stack.getTagCompound();

            if (tagCompound != null) {
                CompoundTag tagCompoundDisplay = tagCompound.getCompoundTag("display");

                if (tagCompoundDisplay.hasKey("color")) {
                    tagCompoundDisplay.removeTag("color");
                }
            }
        }
    }

    public void setColor(ItemStack stack, int color) {
        if (getArmorMaterial() != CyberwareContent2.trenchMat) {
            throw new UnsupportedOperationException("Can\'t dye non-leather!");
        } else {
            CompoundTag tagCompound = stack.getTagCompound();

            if (tagCompound == null) {
                tagCompound = new CompoundTag();
                stack.setTagCompound(tagCompound);
            }

            CompoundTag tagCompoundDisplay = tagCompound.getCompoundTag("display");

            if (!tagCompound.hasKey("display", 10)) {
                tagCompound.setTag("display", tagCompoundDisplay);
            }

            tagCompoundDisplay.setInteger("color", color);
        }
    }

    @Override
    public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> list) {
        if (isInCreativeTab(tab)) {
            if (getArmorMaterial() == CyberwareContent2.trenchMat) {
                super.getSubItems(tab, list);
                ItemStack brown = new ItemStack(this);
                setColor(brown, 0x664028);
                list.add(brown);
                ItemStack white = new ItemStack(this);
                setColor(white, 0xEAEAEA);
                list.add(white);
            } else {
                super.getSubItems(tab, list);
            }
        }
    }

    @Nonnull
    @Override
    public EnumActionResult onItemUse(@Nonnull EntityPlayer entityPlayer, @Nonnull World world, @Nonnull BlockPos blockPos,
                                      @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {
        final IBlockState blockState = world.getBlockState(blockPos);
        final ItemStack itemStack = entityPlayer.getHeldItem(hand);
        if (!world.isRemote
                && blockState.getBlock() instanceof BlockCauldron
                && hasColor(itemStack)) {
            final int waterLevel = blockState.getValue(BlockCauldron.LEVEL);
            if (waterLevel > 0) {
                removeColor(itemStack);
                ((BlockCauldron) blockState.getBlock()).setWaterLevel(world, blockPos, blockState, waterLevel - 1);
                entityPlayer.addStat(StatList.ARMOR_CLEANED);
                return EnumActionResult.SUCCESS;
            }
        }
        return super.onItemUse(entityPlayer, world, blockPos, hand, facing, hitX, hitY, hitZ);
    }
}