package com.nukateam.cyberware.common.network;

import com.nukateam.cyberware.common.block.tile.TileEntityEngineeringTable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ScannerSmashPacket {
    private final BlockPos pos;

    public ScannerSmashPacket(BlockPos pos) {
        this.pos = pos;
    }

    public static void write(ScannerSmashPacket packet, FriendlyByteBuf buf) {
        buf.writeBlockPos(packet.pos);
    }

    public static ScannerSmashPacket read(FriendlyByteBuf buf) {
        return new ScannerSmashPacket(buf.readBlockPos());
    }

    public static void handle(ScannerSmashPacket message, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            if (context.get().getDirection().getReceptionSide().isClient()) {
                var level = net.minecraft.client.Minecraft.getInstance().level;
                if (level != null) {
                    var te = level.getBlockEntity(message.pos);
                    if (te instanceof TileEntityEngineeringTable eng) {
                        eng.smashSounds();
                    }
                }
            }
        });
        context.get().setPacketHandled(true);
    }
}