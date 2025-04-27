package com.nukateam.cyberware.client.gui;

import com.nukateam.cyberware.Cyberware;
import com.nukateam.cyberware.api.CyberwareAPI;
import com.nukateam.cyberware.api.ICyberwareUserData;
import com.nukateam.cyberware.common.CyberwareContent2;
import com.nukateam.cyberware.common.item.ItemCyberlimb;
import com.nukateam.cyberware.common.item.ItemHandUpgrade;
import com.nukateam.cyberware.common.network.CyberwarePacketHandler;
import com.nukateam.cyberware.common.network.GuiPacket;
import micdoodle8.mods.galacticraft.api.client.tabs.AbstractTab;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.util.EnumHandSide;

public class InventoryTabFineManipulators extends AbstractTab {

    public InventoryTabFineManipulators() {
        super(0, 0, 0, new ItemStack(CyberwareContent2.handUpgrades, 1, ItemHandUpgrade.META_CRAFT_HANDS));
    }

    @Override
    public void onTabClicked() {
        Minecraft.getInstance().player.openGui(Cyberware.INSTANCE, 1, Minecraft.getInstance().player.world, 0, 0, 0);
        CyberwarePacketHandler.INSTANCE.sendToServer(new GuiPacket(1, 0, 0, 0));
    }

    @Override
    public boolean shouldAddToList() {
        Player entityPlayer = Minecraft.getInstance().player;
        ICyberwareUserData cyberwareUserData = CyberwareAPI.getCapabilityOrNull(entityPlayer);
        if (cyberwareUserData == null) {
            return false;
        }

        boolean hasCyberArm = entityPlayer.getPrimaryHand() == EnumHandSide.RIGHT
                ? (cyberwareUserData.isCyberwareInstalled(CyberwareContent2.cyberlimbs.getCachedStack(ItemCyberlimb.META_RIGHT_CYBER_ARM)))
                : (cyberwareUserData.isCyberwareInstalled(CyberwareContent2.cyberlimbs.getCachedStack(ItemCyberlimb.META_LEFT_CYBER_ARM)));

        return hasCyberArm
                && cyberwareUserData.isCyberwareInstalled(CyberwareContent2.handUpgrades.getCachedStack(ItemHandUpgrade.META_CRAFT_HANDS));
    }
}
