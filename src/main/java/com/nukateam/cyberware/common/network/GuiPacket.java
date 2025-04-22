package com.nukateam.cyberware.common.network;

import com.nukateam.cyberware.Cyberware;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class GuiPacket {
    private final int guiId;
    private final BlockPos pos;

    public GuiPacket(int guiId, BlockPos pos) {
        this.guiId = guiId;
        this.pos = pos;
    }

    public static void write(GuiPacket packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.guiId);
        buf.writeBlockPos(packet.pos);
    }

    public static GuiPacket read(FriendlyByteBuf buf) {
        return new GuiPacket(buf.readInt(), buf.readBlockPos());
    }

    public static void handle(GuiPacket message, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer player = context.get().getSender();
            if (player != null) {
                player.openMenu(Cyberware.INSTANCE.getMenuProvider(message.guiId, message.pos));
            }
        });
        context.get().setPacketHandled(true);
    }
}