package com.nukateam.cyberware.api.item;

import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;

public interface IDeconstructable {
    public boolean canDestroy(ItemStack stack);

    public NonNullList<ItemStack> getComponents(ItemStack stack);
}
