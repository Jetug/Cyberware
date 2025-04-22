package com.nukateam.cyberware.common.network;

import com.nukateam.cyberware.client.gui.ContainerEngineeringTable;
import com.nukateam.cyberware.common.block.tile.TileEntityEngineeringTable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class EngineeringSwitchArchivePacket {
    private final BlockPos pos;
    private final boolean direction;
    private final boolean isComponent;

    public EngineeringSwitchArchivePacket(BlockPos pos, boolean direction, boolean isComponent) {
        this.pos = pos;
        this.direction = direction;
        this.isComponent = isComponent;
    }

    public static void write(EngineeringSwitchArchivePacket packet, FriendlyByteBuf buf) {
        buf.writeBlockPos(packet.pos);
        buf.writeBoolean(packet.direction);
        buf.writeBoolean(packet.isComponent);
    }

    public static EngineeringSwitchArchivePacket read(FriendlyByteBuf buf) {
        return new EngineeringSwitchArchivePacket(
                buf.readBlockPos(),
                buf.readBoolean(),
                buf.readBoolean()
        );
    }

    public static void handle(EngineeringSwitchArchivePacket message, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer player = context.get().getSender();
            if (player != null && player.containerMenu instanceof ContainerEngineeringTable container) {
                if (message.isComponent) {
                    if (message.direction) {
                        container.nextComponentBox();
                    } else {
                        container.prevComponentBox();
                    }
                } else {
                    if (message.direction) {
                        container.nextArchive();
                    } else {
                        container.prevArchive();
                    }
                    TileEntityEngineeringTable te = (TileEntityEngineeringTable) player.level().getBlockEntity(message.pos);
                    if (te != null) {
                        te.lastPlayerArchive.put(player.getStringUUID(), container.archive.getPos());
                    }
                }
            }
        });
        context.get().setPacketHandled(true);
    }
}