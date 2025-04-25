package com.nukateam.cyberware.common.registry;

import com.nukateam.cyberware.Cyberware;
import com.nukateam.cyberware.api.CyberwareAPI;
import com.nukateam.cyberware.common.block.*;
import com.nukateam.cyberware.common.config.CyberwareConfig;
import com.nukateam.cyberware.common.effect.PotionNeuropozyne;
import com.nukateam.cyberware.common.entity.EntityCyberZombie;
import com.nukateam.cyberware.common.integration.*;
import com.nukateam.cyberware.common.item.*;
import com.nukateam.cyberware.common.item.VanillaWares.SpiderEyeWare;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.common.ForgeTier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class CyberwareContent {
    // Константы
    public static final int RARE = 10;
    public static final int UNCOMMON = 25;
    public static final int COMMON = 50;
    public static final int VERY_COMMON = 100;

    // Регистры
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Cyberware.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Cyberware.MOD_ID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Cyberware.MOD_ID);
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, Cyberware.MOD_ID);

    // Блоки
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

    // Материалы
    public static final ArmorMaterial SHADES_MATERIAL_1 = createArmorMaterial("shades_1", 5, new int[]{1, 2, 3, 1}, 15, SoundEvents.ARMOR_EQUIP_GENERIC, 0.0F, () -> Ingredient.of(Items.GLASS));
    public static final ArmorMaterial SHADES_MATERIAL_2 = createArmorMaterial("shades_2", 5, new int[]{1, 2, 3, 1}, 15, SoundEvents.ARMOR_EQUIP_GENERIC, 0.0F, () -> Ingredient.of(Items.GLASS));
    public static final ArmorMaterial JACKET_MATERIAL = createArmorMaterial("jacket", 5, new int[]{1, 2, 3, 1}, 15, SoundEvents.ARMOR_EQUIP_LEATHER, 0.0F, () -> Ingredient.of(Items.LEATHER));
    public static final ArmorMaterial TRENCH_MATERIAL = createArmorMaterial("trenchcoat", 5, new int[]{1, 2, 3, 1}, 15, SoundEvents.ARMOR_EQUIP_LEATHER, 0.0F, () -> Ingredient.of(Items.LEATHER));
    public static final Tier KATANA_TIER = new ForgeTier(2, 1561, 6.0F, 2.0F, 14, null, () -> Ingredient.EMPTY);

    // Предметы
    public static final RegistryObject<Item> SHADES = registerItem("shades", () -> new ItemArmorCyberware(SHADES_MATERIAL_1, EquipmentSlot.HEAD, new Item.Properties()));
    public static final RegistryObject<Item> SHADES_2 = registerItem("shades2", () -> new ItemArmorCyberware(SHADES_MATERIAL_2, EquipmentSlot.HEAD, new Item.Properties()));
    public static final RegistryObject<Item> JACKET = registerItem("jacket", () -> new ItemArmorCyberware(JACKET_MATERIAL, EquipmentSlot.CHEST, new Item.Properties()));
    public static final RegistryObject<Item> TRENCH_COAT = registerItem("trenchcoat", () -> new ItemArmorCyberware(TRENCH_MATERIAL, EquipmentSlot.CHEST, new Item.Properties()));
    public static final RegistryObject<Item> KATANA = registerItem("katana", () -> new ItemSwordCyberware(KATANA_TIER, 3, -2.4F, new Item.Properties()));

    // Компоненты и кибернетика
    public static final RegistryObject<Item> BODY_PART = registerItem("body_part", () ->
            new ItemBodyPart(new Item.Properties(),
                    new EnumSlot[]{EnumSlot.EYES, EnumSlot.CRANIUM, EnumSlot.HEART, EnumSlot.LUNGS, EnumSlot.LOWER_ORGANS, EnumSlot.SKIN, EnumSlot.MUSCLE, EnumSlot.BONE, EnumSlot.ARM, EnumSlot.ARM, EnumSlot.LEG, EnumSlot.LEG},
                    new String[]{"eyes", "brain", "heart", "lungs", "stomach", "skin", "muscles", "bones", "arm_left", "arm_right", "leg_left", "leg_right"}));

    public static final RegistryObject<Item> CYBEREYES = registerItem("cybereyes", () -> {
        ItemCyberware item = new ItemCybereyes(new Item.Properties(), EnumSlot.EYES);
        item.setEssenceCost(8);
        item.setWeights(UNCOMMON);
        return item;
    });

    // ... все остальные предметы с полной конфигурацией

    // Эффекты
    public static final RegistryObject<MobEffect> NEUROPOZYNE_EFFECT = EFFECTS.register("neuropozyne",
            () -> new PotionNeuropozyne(false, 0x47453d));
    public static final RegistryObject<MobEffect> REJECTION_EFFECT = EFFECTS.register("rejection",
            () -> new PotionNeuropozyne(true, 0xFF0000));

    // Сущности
    public static final RegistryObject<EntityType<EntityCyberZombie>> CYBER_ZOMBIE = ENTITIES.register("cyberzombie",
            () -> EntityType.Builder.of(EntityCyberZombie::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .build("cyberzombie"));

    // Списки
    public static final List<NumItem> NUM_ITEMS = new ArrayList<>();
    public static final List<ZombieItem> ZOMBIE_ITEMS = new ArrayList<>();

    static {
        NUM_ITEMS.add(new NumItem(50, 4));
        NUM_ITEMS.add(new NumItem(25, 3));
        NUM_ITEMS.add(new NumItem(25, 5));
        NUM_ITEMS.add(new NumItem(15, 6));
        NUM_ITEMS.add(new NumItem(5, 10));
    }

    // Методы регистрации
    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> blockSupplier) {
        RegistryObject<T> block = BLOCKS.register(name, blockSupplier);
        registerBlockItem(name, block);
        return block;
    }

    private static <T extends Block> void registerBlockItem(String name, RegistryObject<T> block) {
        ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    private static <T extends Item> RegistryObject<T> registerItem(String name, Supplier<T> itemSupplier) {
        return ITEMS.register(name, itemSupplier);
    }

    private static ArmorMaterial createArmorMaterial(String name, int durability, int[] protection, int enchantability,
                                                     SoundEvent sound, float toughness, Supplier<Ingredient> repairMaterial) {
        return new ArmorMaterial() {
            @Override public int getDurabilityForSlot(EquipmentSlot slot) { return durability; }
            @Override public int getDefenseForSlot(EquipmentSlot slot) { return protection[slot.getIndex()]; }
            @Override public int getEnchantmentValue() { return enchantability; }
            @Override public SoundEvent getEquipSound() { return sound; }
            @Override public Ingredient getRepairIngredient() { return repairMaterial.get(); }
            @Override public String getName() { return Cyberware.MOD_ID + ":" + name; }
            @Override public float getToughness() { return toughness; }
            @Override public float getKnockbackResistance() { return 0; }
        };
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
        ENTITIES.register(eventBus);
        EFFECTS.register(eventBus);
    }

    public static void setup() {
        // Настройка киберзомби
        if (CyberwareConfig.MOBS_ENABLE_CYBER_ZOMBIES.get()) {
            // ... конфигурация спавна
        }

        // Интеграции
        if (CyberwareConfig.INT_BOTANIA.get() && ModList.get().isLoaded("botania")) {
            BotaniaIntegration.setup();
        }
        if (CyberwareConfig.INT_ENDER_IO.get() && ModList.get().isLoaded("enderio")) {
            EnderIOIntegration.setup();
        }
        if (CyberwareConfig.INT_TOUGH_AS_NAILS.get() && ModList.get().isLoaded("toughasnails")) {
            ToughAsNailsIntegration.setup();
        }

        // Линковка ванильных предметов
        CyberwareAPI.linkCyberware(Items.SPIDER_EYE, new SpiderEyeWare());
    }

    // Классы для взвешенных предметов
    public static class NumItem {
        private final int weight;
        private final int num;

        public NumItem(int weight, int num) {
            this.weight = weight;
            this.num = num;
        }

        public int getWeight() { return weight; }
        public int getNum() { return num; }
    }

    public static class ZombieItem {
        private final int weight;
        private final ItemStack stack;

        public ZombieItem(int weight, ItemStack stack) {
            this.weight = weight;
            this.stack = stack;
        }

        public int getWeight() { return weight; }
        public ItemStack getStack() { return stack; }
    }
}