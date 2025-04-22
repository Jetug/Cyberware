package com.nukateam.cyberware.common;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import com.nukateam.cyberware.Cyberware;
import com.nukateam.cyberware.api.CyberwareUserDataImpl;
import com.nukateam.cyberware.api.ICyberwareUserData;
import com.nukateam.cyberware.common.block.tile.TileEntitySurgery;
import com.nukateam.cyberware.common.handler.CyberwareDataHandler;
import com.nukateam.cyberware.common.handler.EssentialsMissingHandler;
import com.nukateam.cyberware.common.handler.GuiHandler;
import com.nukateam.cyberware.common.handler.MiscHandler;
import com.nukateam.cyberware.common.network.CyberwarePacketHandler;

public class CommonProxy {
    public void preInit() {
        CapabilityManager.INSTANCE.register(ICyberwareUserData.class, CyberwareUserDataImpl.STORAGE, CyberwareUserDataImpl.class);
        CyberwareContent.preInit();
        CyberwarePacketHandler.preInit();
    }

    public void init() {
        NetworkRegistry.INSTANCE.registerGuiHandler(Cyberware.INSTANCE, new GuiHandler());
        MinecraftForge.EVENT_BUS.register(CyberwareDataHandler.INSTANCE);
//        MinecraftForge.EVENT_BUS.register(CyberwareConfig.INSTANCE);
        MinecraftForge.EVENT_BUS.register(MiscHandler.INSTANCE);
        MinecraftForge.EVENT_BUS.register(EssentialsMissingHandler.INSTANCE);
    }

    public void postInit() {
        CyberwareConfig.postInit();
        CyberwareContent.postInit();
    }

    public void wrong(TileEntitySurgery tileEntitySurgery) {
        // client side only
    }

    public boolean workingOnPlayer(LivingEntity entityLivingBase) {
        return false;
    }
}
