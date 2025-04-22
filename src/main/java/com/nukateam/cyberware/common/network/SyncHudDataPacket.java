package com.nukateam.cyberware.common.network;

import com.nukateam.cyberware.api.CyberwareAPI;
import com.nukateam.cyberware.api.ICyberwareUserData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncHudDataPacket {
    private final CompoundTag tagCompound;

    public SyncHudDataPacket(CompoundTag tagCompound) {
        this.tagCompound = tagCompound;
    }

    public static void write(SyncHudDataPacket packet, FriendlyByteBuf buf) {
        buf.writeNbt(packet.tagCompound);
    }

    public static SyncHudDataPacket read(FriendlyByteBuf buf) {
        return new SyncHudDataPacket(buf.readNbt());
    }

    public static void handle(SyncHudDataPacket message, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer player = context.get().getSender();
            if (player != null) {
                ICyberwareUserData cyberwareUserData = CyberwareAPI.getCapabilityOrNull(player);
                if (cyberwareUserData != null) {
                    cyberwareUserData.setHudData(message.tagCompound);
                }
            }
        });
        context.get().setPacketHandled(true);
    }
}