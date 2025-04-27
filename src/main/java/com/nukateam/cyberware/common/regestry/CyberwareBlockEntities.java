package com.nukateam.cyberware.common.regestry;


import com.nukateam.cyberware.Cyberware;
import com.nukateam.cyberware.common.block.tile.TileEntityCharger;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CyberwareBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Cyberware.MODID);

    public static final RegistryObject<BlockEntityType<TileEntityCharger>> CHARGER =
            BLOCK_ENTITIES.register("charger", () ->
                    BlockEntityType.Builder.of(ChargerBlockEntity::new, CyberwareBlocks.CHARGER.get()).build(null));

    public static final RegistryObject<BlockEntityType<BeaconBlockEntity>> BEACON =
            BLOCK_ENTITIES.register("beacon", () ->
                    BlockEntityType.Builder.of(BeaconBlockEntity::new, CyberwareBlocks.BEACON.get()).build(null));
}
