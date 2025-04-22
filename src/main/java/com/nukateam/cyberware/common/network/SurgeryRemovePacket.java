package com.nukateam.cyberware.common.network;

import com.nukateam.cyberware.api.item.ICyberware.EnumSlot;
import com.nukateam.cyberware.common.block.tile.TileEntitySurgery;
import com.nukateam.cyberware.common.lib.LibConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SurgeryRemovePacket {
    private final BlockPos pos;
    private final int slotNumber;
    private final boolean isNull;

    public SurgeryRemovePacket(BlockPos pos, int slotNumber, boolean isNull) {
        this.pos = pos;
        this.slotNumber = slotNumber;
        this.isNull = isNull;
    }

    public static void write(SurgeryRemovePacket packet, FriendlyByteBuf buf) {
        buf.writeBlockPos(packet.pos);
        buf.writeInt(packet.slotNumber);
        buf.writeBoolean(packet.isNull);
    }

    public static SurgeryRemovePacket read(FriendlyByteBuf buf) {
        return new SurgeryRemovePacket(
                buf.readBlockPos(),
                buf.readInt(),
                buf.readBoolean()
        );
    }

    public static void handle(SurgeryRemovePacket message, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer player = context.get().getSender();
            if (player != null) {
                var te = player.level().getBlockEntity(message.pos);
                if (te instanceof TileEntitySurgery surgery) {
                    surgery.discardSlots[message.slotNumber] = message.isNull;
                    EnumSlot slot = EnumSlot.values()[message.slotNumber / LibConstants.WARE_PER_SLOT];
                    int index = message.slotNumber % LibConstants.WARE_PER_SLOT;

                    if (message.isNull) {
                        surgery.disableDependants(surgery.slotsPlayer.getStackInSlot(message.slotNumber), slot, index);
                    } else {
                        surgery.enableDependsOn(surgery.slotsPlayer.getStackInSlot(message.slotNumber), slot, index);
                    }
                    surgery.updateEssential(slot);
                    surgery.updateEssence();
                }
            }
        });
        context.get().setPacketHandled(true);
    }
}