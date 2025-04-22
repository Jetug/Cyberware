package com.nukateam.cyberware.common.network;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ParticlePacket {
    private final int effectId;
    private final double x, y, z;

    public ParticlePacket(int effectId, double x, double y, double z) {
        this.effectId = effectId;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static void write(ParticlePacket packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.effectId);
        buf.writeDouble(packet.x);
        buf.writeDouble(packet.y);
        buf.writeDouble(packet.z);
    }

    public static ParticlePacket read(FriendlyByteBuf buf) {
        return new ParticlePacket(
                buf.readInt(),
                buf.readDouble(),
                buf.readDouble(),
                buf.readDouble()
        );
    }

    public static void handle(ParticlePacket message, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            if (context.get().getDirection().getReceptionSide().isClient()) {
                var level = net.minecraft.client.Minecraft.getInstance().level;
                if (level != null) {
                    var rand = level.random;
                    switch (message.effectId) {
                        case 0 -> {
                            for (int i = 0; i < 5; i++) {
                                level.addParticle(ParticleTypes.HEART,
                                        message.x + rand.nextFloat() - 0.5,
                                        message.y + rand.nextFloat() - 0.5,
                                        message.z + rand.nextFloat() - 0.5,
                                        2.0 * (rand.nextFloat() - 0.5),
                                        0.5,
                                        2.0 * (rand.nextFloat() - 0.5));
                            }
                        }
                        case 1 -> {
                            for (int i = 0; i < 5; i++) {
                                level.addParticle(ParticleTypes.ANGRY_VILLAGER,
                                        message.x + rand.nextFloat() - 0.5,
                                        message.y + rand.nextFloat() - 0.5,
                                        message.z + rand.nextFloat() - 0.5,
                                        2.0 * (rand.nextFloat() - 0.5),
                                        0.5,
                                        2.0 * (rand.nextFloat() - 0.5));
                            }
                        }
                    }
                }
            }
        });
        context.get().setPacketHandled(true);
    }
}