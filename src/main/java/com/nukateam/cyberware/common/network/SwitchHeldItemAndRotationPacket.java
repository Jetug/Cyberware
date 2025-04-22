package com.nukateam.cyberware.common.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.util.Mth;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SwitchHeldItemAndRotationPacket {
    private final int slot;
    private final int entityId;
    private final int attackerId;

    public SwitchHeldItemAndRotationPacket(int slot, int entityId, int attackerId) {
        this.slot = slot;
        this.entityId = entityId;
        this.attackerId = attackerId;
    }

    public static void write(SwitchHeldItemAndRotationPacket packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.entityId);
        buf.writeInt(packet.slot);
        buf.writeInt(packet.attackerId);
    }

    public static SwitchHeldItemAndRotationPacket read(FriendlyByteBuf buf) {
        return new SwitchHeldItemAndRotationPacket(
                buf.readInt(),
                buf.readInt(),
                buf.readInt()
        );
    }

    public static void handle(SwitchHeldItemAndRotationPacket message, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            if (context.get().getDirection().getReceptionSide().isClient()) {
                var level = net.minecraft.client.Minecraft.getInstance().level;
                if (level != null) {
                    Entity targetEntity = level.getEntity(message.entityId);
                    if (targetEntity instanceof net.minecraft.world.entity.player.Player player) {
                        player.getInventory().selected = message.slot;

                        if (message.attackerId != -1) {
                            player.closeContainer();
                            Entity attacker = level.getEntity(message.attackerId);
                            if (attacker != null) {
                                faceEntity(player, attacker);
                            }
                        }
                    }
                }
            }
        });
        context.get().setPacketHandled(true);
    }

    private static void faceEntity(Entity player, Entity entity) {
        double dx = entity.getX() - player.getX();
        double dz = entity.getZ() - player.getZ();
        double dy = entity instanceof LivingEntity living ?
                living.getEyeY() - (player.getY() + player.getEyeHeight()) :
                (entity.getBoundingBox().minY + entity.getBoundingBox().maxY) / 2 - (player.getY() + player.getEyeHeight());

        double dist = Mth.sqrt((float) (dx * dx + dz * dz));
        player.setXRot((float)(-Mth.atan2(dy, dist) * (180D / Math.PI)));
        player.setYRot((float)(Mth.atan2(dz, dx) * (180D / Math.PI)) - 90.0F);
    }
}