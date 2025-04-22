package com.nukateam.cyberware.common.network;

import com.nukateam.cyberware.api.CyberwareAPI;
import com.nukateam.cyberware.api.ICyberwareUserData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class OpenRadialMenuPacket {
    public OpenRadialMenuPacket() {}

    public static void write(OpenRadialMenuPacket packet, FriendlyByteBuf buf) {}

    public static OpenRadialMenuPacket read(FriendlyByteBuf buf) {
        return new OpenRadialMenuPacket();
    }

    public static void handle(OpenRadialMenuPacket message, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer player = context.get().getSender();
            if (player != null) {
                ICyberwareUserData cyberwareUserData = CyberwareAPI.getCapabilityOrNull(player);
                if (cyberwareUserData != null) {
                    cyberwareUserData.setOpenedRadialMenu(true);
                }
            }
        });
        context.get().setPacketHandled(true);
    }
}