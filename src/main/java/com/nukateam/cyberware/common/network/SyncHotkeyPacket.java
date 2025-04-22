package com.nukateam.cyberware.common.network;

import com.nukateam.cyberware.api.CyberwareAPI;
import com.nukateam.cyberware.api.ICyberwareUserData;
import com.nukateam.cyberware.api.item.HotkeyHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncHotkeyPacket {
    private final int selectedPart;
    private final int key;

    public SyncHotkeyPacket(int selectedPart, int key) {
        this.selectedPart = selectedPart;
        this.key = key;
    }

    public static void write(SyncHotkeyPacket packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.selectedPart);
        buf.writeInt(packet.key);
    }

    public static SyncHotkeyPacket read(FriendlyByteBuf buf) {
        return new SyncHotkeyPacket(buf.readInt(), buf.readInt());
    }

    public static void handle(SyncHotkeyPacket message, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer player = context.get().getSender();
            if (player != null) {
                ICyberwareUserData cyberwareUserData = CyberwareAPI.getCapabilityOrNull(player);
                if (cyberwareUserData != null) {
                    if (message.key == Integer.MAX_VALUE) {
                        HotkeyHelper.removeHotkey(cyberwareUserData, cyberwareUserData.getActiveItems().get(message.selectedPart));
                    } else {
                        HotkeyHelper.removeHotkey(cyberwareUserData, message.key);
                        HotkeyHelper.assignHotkey(cyberwareUserData,
                                cyberwareUserData.getActiveItems().get(message.selectedPart),
                                message.key);
                    }
                }
            }
        });
        context.get().setPacketHandled(true);
    }
}