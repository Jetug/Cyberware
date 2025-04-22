package com.nukateam.cyberware.common.network;

import com.nukateam.cyberware.api.CyberwareAPI;
import com.nukateam.cyberware.api.ICyberwareUserData;
import com.nukateam.cyberware.api.hud.CyberwareHudDataEvent;
import com.nukateam.cyberware.api.hud.IHudElement;
import com.nukateam.cyberware.client.gui.hud.HudNBTData;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

public class CyberwareSyncPacket {
    private final CompoundTag data;
    private final int entityId;

    public CyberwareSyncPacket(CompoundTag data, int entityId) {
        this.data = data;
        this.entityId = entityId;
    }

    public static void encode(CyberwareSyncPacket packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.entityId);
        buf.writeNbt(packet.data);
    }

    public static CyberwareSyncPacket decode(FriendlyByteBuf buf) {
        return new CyberwareSyncPacket(buf.readNbt(), buf.readInt());
    }

    public static void handle(final CyberwareSyncPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Minecraft minecraft = Minecraft.getInstance();
            Entity targetEntity = minecraft.level.getEntity(packet.entityId);
            ICyberwareUserData cyberwareUserData = CyberwareAPI.getCapabilityOrNull(targetEntity);

            if (cyberwareUserData != null) {
                cyberwareUserData.deserializeNBT(packet.data);

                if (targetEntity == minecraft.player) {
                    CompoundTag tagCompound = cyberwareUserData.getHudData();

                    CyberwareHudDataEvent hudEvent = new CyberwareHudDataEvent();
                    MinecraftForge.EVENT_BUS.post(hudEvent);
                    List<IHudElement> elements = hudEvent.getElements();

                    for (IHudElement element : elements) {
                        if (tagCompound.contains(element.getUniqueName())) {
                            element.load(new HudNBTData(tagCompound.getCompound(element.getUniqueName())));
                        }
                    }
                }
            }
        });
        ctx.setPacketHandled(true);
    }
}