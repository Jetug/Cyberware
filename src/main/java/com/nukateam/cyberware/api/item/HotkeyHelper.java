package com.nukateam.cyberware.api.item;

import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import com.nukateam.cyberware.api.CyberwareAPI;
import com.nukateam.cyberware.api.ICyberwareUserData;

public class HotkeyHelper {
    public static void assignHotkey(ICyberwareUserData cyberwareUserData, ItemStack stack, int key) {
        removeHotkey(cyberwareUserData, stack);

        cyberwareUserData.addHotkey(key, stack);
        CyberwareAPI.getCyberwareNBT(stack).setInteger("hotkey", key);
    }

    public static void removeHotkey(ICyberwareUserData cyberwareUserData, int key) {
        ItemStack stack = cyberwareUserData.getHotkey(key);
        removeHotkey(cyberwareUserData, stack);
    }

    public static void removeHotkey(ICyberwareUserData cyberwareUserData, ItemStack stack) {
        int hotkey = getHotkey(stack);

        if (hotkey != -1) {
            cyberwareUserData.removeHotkey(hotkey);
            CyberwareAPI.getCyberwareNBT(stack).removeTag("hotkey");
        }
    }

    public static int getHotkey(ItemStack stack) {
        if (stack.isEmpty()) return -1;

        CompoundTag tagCompound = CyberwareAPI.getCyberwareNBT(stack);
        if (!tagCompound.hasKey("hotkey")) {
            return -1;
        }

        return tagCompound.getInteger("hotkey");
    }
}
