package com.nukateam.cyberware.common.item;

import com.nukateam.cyberware.Cyberware;
import com.nukateam.cyberware.common.CyberwareContent2;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class ItemCyberwareBase extends Item {
    private ItemStack[] itemStackCache;

    public ItemCyberwareBase() {
        super(new Properties());
        itemStackCache = new ItemStack[1];
    }

    public ItemStack getCachedStack(int damage) {
        if (damage < 0 || damage >= itemStackCache.length) {
            return ItemStack.EMPTY;
        }

        ItemStack itemStack = itemStackCache[damage];
        if (itemStack != null
                && (itemStack.getItem() != this
                || itemStack.getCount() != 1
                || itemStack.getDamageValue() != damage)) {
            Cyberware.LOGGER.error("Corrupted item stack cache: found {} as {}:{}, expected {}:{}",
                    itemStack, itemStack.getItem(), itemStack.getDamageValue(),
                    this, damage);
            itemStack = null;
        }
        if (itemStack == null) {
            itemStack = new ItemStack(this, 1, damage);
            itemStackCache[damage] = itemStack;
        }
        return itemStack;
    }
}