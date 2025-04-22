package com.nukateam.cyberware.common.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class CyberwarePacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    private static int id = 0;

    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation("cyberware", "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void register() {
        //S2C
        INSTANCE.registerMessage(id++, CyberwareSyncPacket.class,
                CyberwareSyncPacket::write,
                CyberwareSyncPacket::read,
                CyberwareSyncPacket::handle);
//
//        INSTANCE.registerMessage(id++, UpdateConfigPacket.class,
//                UpdateConfigPacket::write,
//                UpdateConfigPacket::read,
//                UpdateConfigPacket::handle);

        INSTANCE.registerMessage(id++, DodgePacket.class,
                DodgePacket::write,
                DodgePacket::read,
                DodgePacket::handle);

        INSTANCE.registerMessage(id++, ParticlePacket.class,
                ParticlePacket::write,
                ParticlePacket::read,
                ParticlePacket::handle);

        INSTANCE.registerMessage(id++, ScannerSmashPacket.class,
                ScannerSmashPacket::write,
                ScannerSmashPacket::read,
                ScannerSmashPacket::handle);

        INSTANCE.registerMessage(id++, SwitchHeldItemAndRotationPacket.class,
                SwitchHeldItemAndRotationPacket::write,
                SwitchHeldItemAndRotationPacket::read,
                SwitchHeldItemAndRotationPacket::handle);

        //C2S
        INSTANCE.registerMessage(id++, SurgeryRemovePacket.class,
                SurgeryRemovePacket::write,
                SurgeryRemovePacket::read,
                SurgeryRemovePacket::handle);

        INSTANCE.registerMessage(id++, EngineeringDestroyPacket.class,
                EngineeringDestroyPacket::write,
                EngineeringDestroyPacket::read,
                EngineeringDestroyPacket::handle);

        INSTANCE.registerMessage(id++, EngineeringSwitchArchivePacket.class,
                EngineeringSwitchArchivePacket::write,
                EngineeringSwitchArchivePacket::read,
                EngineeringSwitchArchivePacket::handle);

        INSTANCE.registerMessage(id++, GuiPacket.class,
                GuiPacket::write,
                GuiPacket::read,
                GuiPacket::handle);

        INSTANCE.registerMessage(id++, OpenRadialMenuPacket.class,
                OpenRadialMenuPacket::write,
                OpenRadialMenuPacket::read,
                OpenRadialMenuPacket::handle);

        INSTANCE.registerMessage(id++, SyncHotkeyPacket.class,
                SyncHotkeyPacket::write,
                SyncHotkeyPacket::read,
                SyncHotkeyPacket::handle);

        INSTANCE.registerMessage(id++, TriggerActiveAbilityPacket.class,
                TriggerActiveAbilityPacket::write,
                TriggerActiveAbilityPacket::read,
                TriggerActiveAbilityPacket::handle);

        INSTANCE.registerMessage(id++, SyncHudDataPacket.class,
                SyncHudDataPacket::write,
                SyncHudDataPacket::read,
                SyncHudDataPacket::handle);
    }

    public static void sendToServer(Object packet) {
        INSTANCE.sendToServer(packet);
    }

    public static void sendToClient(Object packet, ServerPlayer player) {
        INSTANCE.sendTo(packet, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }
}