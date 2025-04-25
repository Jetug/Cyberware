package com.nukateam.cyberware.common.block.tile;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import com.nukateam.cyberware.common.CyberwareContent2;

public class TileEntityComponentBox extends TileEntity {
    public static class ItemStackHandlerComponent extends ItemStackHandler {
        public ItemStackHandlerComponent(int size) {
            super(size);
        }

        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            if (!isItemValidForSlot(slot, stack)) return stack;

            return super.insertItem(slot, stack, simulate);
        }

        public boolean isItemValidForSlot(int slot, @Nonnull ItemStack stack) {
            if (!stack.isEmpty() && stack.getItem() == CyberwareContent2.component) return true;

            return stack.isEmpty();
        }

    }

    public ItemStackHandler slots = new ItemStackHandlerComponent(18);
    public String customName = null;
    public boolean doDrop = true;

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return (T) slots;
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public void readFromNBT(@Nonnull CompoundTag tagCompound) {
        super.readFromNBT(tagCompound);

        slots.deserializeNBT(tagCompound.getCompoundTag("inv"));

        if (tagCompound.hasKey("CustomName")) {
            customName = tagCompound.getString("CustomName");
        }
    }

    @Nonnull
    @Override
    public CompoundTag writeToNBT(@Nonnull CompoundTag tagCompound) {
        tagCompound = super.writeToNBT(tagCompound);

        tagCompound.setTag("inv", slots.serializeNBT());

        if (hasCustomName()) {
            tagCompound.setString("CustomName", customName);
        }

        return tagCompound;
    }

    @Override
    public void onDataPacket(NetworkManager networkManager, SPacketUpdateTileEntity packetUpdateTileEntity) {
        CompoundTag tagCompound = packetUpdateTileEntity.getNbtCompound();
        readFromNBT(tagCompound);
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        CompoundTag tagCompound = new CompoundTag();
        writeToNBT(tagCompound);
        return new SPacketUpdateTileEntity(pos, 0, tagCompound);
    }

    @Nonnull
    @Override
    public CompoundTag getUpdateTag() {
        return writeToNBT(new CompoundTag());
    }

    public boolean isUsableByPlayer(EntityPlayer entityPlayer) {
        return world.getTileEntity(pos) == this
                && entityPlayer.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
    }

    public String getName() {
        return hasCustomName() ? customName : "cyberware.container.component_box";
    }

    public boolean hasCustomName() {
        return customName != null && !customName.isEmpty();
    }

    public void setCustomInventoryName(String name) {
        customName = name;
    }

    @Override
    public ITextComponent getDisplayName() {
        return hasCustomName() ? new TextComponentString(getName()) : new TextComponentTranslation(getName());
    }
}
