package com.nukateam.cyberware.common.network;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.Random;
import java.util.function.Supplier;

public class DodgePacket {
    private final int entityId;

    public DodgePacket(int entityId) {
        this.entityId = entityId;
    }

    public static void write(DodgePacket packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.entityId);
    }

    public static DodgePacket read(FriendlyByteBuf buf) {
        return new DodgePacket(buf.readInt());
    }

    public static void handle(DodgePacket message, Supplier<NetworkEvent.Context> context) {
        if (context.get().getDirection().getReceptionSide().isClient()) {
            context.get().enqueueWork(() -> {
                Entity targetEntity = Minecraft.getInstance().level.getEntity(message.entityId);
                if (targetEntity != null) {
                    var rand = targetEntity.level().getRandom();
                    for (int i = 0; i < 25; i++) {
                        targetEntity.level().addParticle(ParticleTypes.WHITE_ASH,
                                targetEntity.getX(),
                                targetEntity.getY() + rand.nextFloat() * targetEntity.getBbHeight(),
                                targetEntity.getZ(),
                                (rand.nextFloat() - .5F) * .2F,
                                0,
                                (rand.nextFloat() - .5F) * .2F);
                    }
                    targetEntity.playSound(SoundEvents.FIREWORK_ROCKET_LAUNCH, 1F, 1F);
                }
            });
        }
        context.get().setPacketHandled(true);
    }
}