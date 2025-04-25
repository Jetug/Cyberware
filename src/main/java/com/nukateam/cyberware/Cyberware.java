package com.nukateam.cyberware;

import com.nukateam.cyberware.common.regestry.ModItems;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;

import com.nukateam.cyberware.common.CyberwareConfig;
import com.nukateam.cyberware.common.misc.CommandClearCyberware;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Cyberware.MODID)
public class Cyberware {
    public static final String MODID = "cyberware";
    public static final Logger LOGGER = LogManager.getLogger();
    public static final IEventBus MOD_EVENT_BUS = FMLJavaModLoadingContext.get().getModEventBus();

    public Cyberware() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CyberwareConfig.clientSpec);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, CyberwareConfig.serverSpec);
        ModItems.register(MOD_EVENT_BUS);
//        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CyberwareConfig.SPEC);

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Cyberware common setup");
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        event.getServer().getCommands().getDispatcher().register(CommandClearCyberware.getCommand());
    }
}