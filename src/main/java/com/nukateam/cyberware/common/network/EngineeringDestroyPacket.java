package com.nukateam.cyberware.common.network;

import com.nukateam.cyberware.common.block.tile.TileEntityEngineeringTable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class EngineeringDestroyPacket {
    private final BlockPos pos;

    public EngineeringDestroyPacket(BlockPos pos) {
        this.pos = pos;
    }

    public static void write(EngineeringDestroyPacket packet, FriendlyByteBuf buf) {
        buf.writeBlockPos(packet.pos);
    }

    public static EngineeringDestroyPacket read(FriendlyByteBuf buf) {
        return new EngineeringDestroyPacket(buf.readBlockPos());
    }

    public static void handle(EngineeringDestroyPacket message, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer player = context.get().getSender();
            if (player != null) {
                var te = player.level().getBlockEntity(message.pos);
                if (te instanceof TileEntityEngineeringTable engineering) {
                    engineering.smash(true);
                }
            }
        });
        context.get().setPacketHandled(true);
    }
}