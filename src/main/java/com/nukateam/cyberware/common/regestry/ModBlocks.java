package com.nukateam.cyberware.common.regestry;

import com.nukateam.cyberware.Cyberware;
import com.nukateam.cyberware.common.block.*;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Cyberware.MODID);

    public static final RegistryObject<Block> SURGERY = registerBlock("surgery", BlockSurgery::new);
    public static final RegistryObject<Block> SURGERY_CHAMBER = registerBlock("surgery_chamber", BlockSurgeryChamber::new);
    public static final RegistryObject<Block> CHARGER = registerBlock("charger", BlockCharger::new);
    public static final RegistryObject<Block> ENGINEERING = registerBlock("engineering_table", BlockEngineeringTable::new);
    public static final RegistryObject<Block> SCANNER = registerBlock("scanner", BlockScanner::new);
    public static final RegistryObject<Block> BLUEPRINT_ARCHIVE = registerBlock("blueprint_archive", BlockBlueprintArchive::new);
    public static final RegistryObject<Block> COMPONENT_BOX = registerBlock("component_box", BlockComponentBox::new);
    public static final RegistryObject<Block> RADIO = registerBlock("beacon", BlockBeacon::new);
    public static final RegistryObject<Block> RADIO_LARGE = registerBlock("beacon_large", BlockBeaconLarge::new);
    public static final RegistryObject<Block> RADIO_POST = registerBlock("beacon_post", BlockBeaconPost::new);

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        var toReturn = BLOCKS.register(name, block);
        ModItems.ITEMS.register(name, () -> new BlockItem(toReturn.get(), new Item.Properties()));
        return toReturn;
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
