package com.nukateam.cyberware.common.network;

import com.nukateam.cyberware.api.CyberwareAPI;
import com.nukateam.cyberware.api.ICyberwareUserData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class TriggerActiveAbilityPacket {
    private final ItemStack stack;

    public TriggerActiveAbilityPacket(ItemStack stack) {
        this.stack = stack;
    }

    public static void write(TriggerActiveAbilityPacket packet, FriendlyByteBuf buf) {
        buf.writeItem(packet.stack);
    }

    public static TriggerActiveAbilityPacket read(FriendlyByteBuf buf) {
        return new TriggerActiveAbilityPacket(buf.readItem());
    }

    public static void handle(TriggerActiveAbilityPacket message, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer player = context.get().getSender();
            if (player != null) {
                ICyberwareUserData cyberwareUserData = CyberwareAPI.getCapabilityOrNull(player);
                if (cyberwareUserData != null) {
                    CyberwareAPI.useActiveItem(player, cyberwareUserData.getCyberware(message.stack));
                }
            }
        });
        context.get().setPacketHandled(true);
    }
}