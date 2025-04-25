package com.nukateam.cyberware.common.item;

import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import com.nukateam.cyberware.api.CyberwareAPI;
import com.nukateam.cyberware.api.ISpecialBattery;
import com.nukateam.cyberware.common.CyberwareContent2;
import com.nukateam.cyberware.common.lib.LibConstants;

public class ItemDenseBattery extends ItemCyberware implements ISpecialBattery {

    public ItemDenseBattery(String name, EnumSlot slot) {
        super(name, slot);
    }

    @Override
    public boolean isIncompatible(ItemStack stack, ItemStack other) {
        return other.getItem() == CyberwareContent2.lowerOrgansUpgrades
                && stack.getItemDamage() == ItemLowerOrgansUpgrade.META_BATTERY;
    }

    @Override
    public int add(ItemStack battery, ItemStack power, int amount, boolean simulate) {
        if (power == ItemStack.EMPTY) {
            int amountToAdd = Math.min(getCapacity(battery) - getStoredEnergy(battery), amount);
            if (!simulate) {
                CompoundTag data = CyberwareAPI.getCyberwareNBT(battery);
                data.setInteger("power", data.getInteger("power") + amountToAdd);
            }
            return amountToAdd;
        }
        return 0;
    }

    @Override
    public int extract(ItemStack battery, int amount, boolean simulate) {
        int amountToSub = Math.min(getStoredEnergy(battery), amount);
        if (!simulate) {
            CompoundTag data = CyberwareAPI.getCyberwareNBT(battery);
            data.setInteger("power", data.getInteger("power") - amountToSub);
        }
        return amountToSub;
    }

    @Override
    public int getStoredEnergy(ItemStack battery) {
        CompoundTag data = CyberwareAPI.getCyberwareNBT(battery);

        if (!data.hasKey("power")) {
            data.setInteger("power", 0);
        }
        return data.getInteger("power");
    }

    @Override
    public int getCapacity(ItemStack battery) {
        return LibConstants.DENSE_BATTERY_CAPACITY;
    }

}
