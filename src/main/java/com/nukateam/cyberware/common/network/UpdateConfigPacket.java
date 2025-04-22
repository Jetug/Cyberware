package com.nukateam.cyberware.common.network;

import com.nukateam.cyberware.common.CyberwareConfig;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

//public class UpdateConfigPacket {
//    public static void write(UpdateConfigPacket packet, FriendlyByteBuf buf) {
//        buf.writeInt(CyberwareConfig.SERVER.ESSENCE.get());
//        buf.writeInt(CyberwareConfig.SERVER.CRITICAL_ESSENCE.get());
//        buf.writeBoolean(CyberwareConfig.SERVER.SURGERY_CRAFTING.get());
//        buf.writeFloat(CyberwareConfig.SERVER.ENGINEERING_CHANCE.get());
//        buf.writeFloat(CyberwareConfig.SERVER.SCANNER_CHANCE.get());
//        buf.writeFloat(CyberwareConfig.SERVER.SCANNER_CHANCE_ADDL.get());
//        buf.writeInt(CyberwareConfig.SERVER.SCANNER_TIME);
//    }
//
//    public static UpdateConfigPacket read(FriendlyByteBuf buf) {
//        CyberwareConfig.SERVER.ESSENCE = buf.readInt();
//        CyberwareConfig.SERVER.CRITICAL_ESSENCE = buf.readInt();
//        CyberwareConfig.SERVER.SURGERY_CRAFTING = buf.readBoolean();
//        CyberwareConfig.SERVER.ENGINEERING_CHANCE = buf.readFloat();
//        CyberwareConfig.SERVER.SCANNER_CHANCE = buf.readFloat();
//        CyberwareConfig.SERVER.SCANNER_CHANCE_ADDL = buf.readFloat();
//        CyberwareConfig.SERVER.SCANNER_TIME = buf.readInt();
//        return new UpdateConfigPacket();
//    }
//
//    public static void handle(UpdateConfigPacket message, Supplier<NetworkEvent.Context> context) {
//        context.get().setPacketHandled(true);
//    }
//}