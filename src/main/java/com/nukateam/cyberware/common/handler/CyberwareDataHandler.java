package com.nukateam.cyberware.common.handler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.core.NonNullList;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import com.nukateam.cyberware.api.CyberwareAPI;
import com.nukateam.cyberware.api.CyberwareUserDataImpl;
import com.nukateam.cyberware.api.ICyberwareUserData;
import com.nukateam.cyberware.api.item.ICyberware;
import com.nukateam.cyberware.api.item.ICyberware.EnumSlot;
import com.nukateam.cyberware.common.CyberwareConfig;
import com.nukateam.cyberware.common.CyberwareContent2;
import com.nukateam.cyberware.common.CyberwareContent2.ZombieItem;
import com.nukateam.cyberware.common.block.entity.BeaconBlockEntity;
import com.nukateam.cyberware.common.entity.EntityCyberZombie;
import com.nukateam.cyberware.common.lib.LibConstants;
import com.nukateam.cyberware.common.network.CyberwarePacketHandler;
import com.nukateam.cyberware.common.network.CyberwareSyncPacket;

public class CyberwareDataHandler {
    public static final CyberwareDataHandler INSTANCE = new CyberwareDataHandler();
    public static final String KEEP_WARE_GAMERULE = "cyberware_keepCyberware";
    public static final String DROP_WARE_GAMERULE = "cyberware_dropCyberware";

    @SubscribeEvent
    public void onEntityConstructing(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof LivingEntity entity) {
            entity.getAttributes().addTransientAttributeModifiers(CyberwareAPI.getToleranceAttributeModifiers());
        }
    }

    @SubscribeEvent
    public void worldLoad(LevelEvent.Load event) {
        if (event.getLevel() instanceof Level level) {
            GameRules rules = level.getGameRules();
            if (!rules.hasRule(KEEP_WARE_GAMERULE)) {
                rules.addGameRule(KEEP_WARE_GAMERULE, Boolean.toString(CyberwareConfig.DEFAULT_KEEP), GameRules.BooleanValue.create(CyberwareConfig.DEFAULT_KEEP));
            }
            if (!rules.hasRule(DROP_WARE_GAMERULE)) {
                rules.addGameRule(DROP_WARE_GAMERULE, Boolean.toString(CyberwareConfig.DEFAULT_DROP), GameRules.BooleanValue.create(CyberwareConfig.DEFAULT_DROP));
            }
        }
    }

    @SubscribeEvent
    public void attachCyberwareData(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            event.addCapability(CyberwareUserDataImpl.Provider.NAME, new CyberwareUserDataImpl.Provider());
        }
    }

    @SubscribeEvent
    public void playerDeathEvent(PlayerEvent.Clone event) {
        Player entityPlayerLiving = event.getEntity();
        Player entityPlayerDead = event.getOriginal();
        if (event.isWasDeath()) {
            if (entityPlayerLiving.level().getGameRules().getBoolean(KEEP_WARE_GAMERULE)) {
                ICyberwareUserData cyberwareUserDataDead = CyberwareAPI.getCapabilityOrNull(entityPlayerDead);
                ICyberwareUserData cyberwareUserDataLiving = CyberwareAPI.getCapabilityOrNull(entityPlayerLiving);
                if (cyberwareUserDataDead != null && cyberwareUserDataLiving != null) {
                    cyberwareUserDataLiving.deserializeNBT(cyberwareUserDataDead.serializeNBT());
                }
            }
        } else {
            ICyberwareUserData cyberwareUserDataDead = CyberwareAPI.getCapabilityOrNull(entityPlayerDead);
            ICyberwareUserData cyberwareUserDataLiving = CyberwareAPI.getCapabilityOrNull(entityPlayerLiving);
            if (cyberwareUserDataDead != null && cyberwareUserDataLiving != null) {
                cyberwareUserDataLiving.deserializeNBT(cyberwareUserDataDead.serializeNBT());
            }
        }
    }

    @SubscribeEvent
    public void handleCyberzombieDrops(LivingDropsEvent event) {
        LivingEntity entityLivingBase = event.getEntity();
        if (entityLivingBase instanceof Player && !entityLivingBase.level().isClientSide()) {
            Player entityPlayer = (Player) entityLivingBase;
            if ((entityPlayer.level().getGameRules().getBoolean(DROP_WARE_GAMERULE)
                    && !entityPlayer.level().getGameRules().getBoolean(KEEP_WARE_GAMERULE))
                    || (entityPlayer.level().getGameRules().getBoolean(KEEP_WARE_GAMERULE)
                    && shouldDropWare(event.getSource()))) {
                ICyberwareUserData cyberwareUserData = CyberwareAPI.getCapabilityOrNull(entityPlayer);
                if (cyberwareUserData != null) {
                    for (EnumSlot slot : EnumSlot.values()) {
                        NonNullList<ItemStack> nnlInstalled = cyberwareUserData.getInstalledCyberware(slot);
                        NonNullList<ItemStack> nnlDefaults = NonNullList.create();
                        for (ItemStack itemStackDefault : CyberwareConfig.getStartingItems(EnumSlot.values()[slot.ordinal()])) {
                            nnlDefaults.add(itemStackDefault.copy());
                        }
                        for (ItemStack itemStackInstalled : nnlInstalled) {
                            if (!itemStackInstalled.isEmpty()) {
                                ItemStack itemStackToDrop = itemStackInstalled.copy();
                                boolean found = false;
                                for (ItemStack itemStackDefault : nnlDefaults) {
                                    if (CyberwareAPI.areCyberwareStacksEqual(itemStackDefault, itemStackToDrop)) {
                                        if (itemStackToDrop.getCount() > itemStackDefault.getCount()) {
                                            itemStackToDrop.shrink(itemStackDefault.getCount());
                                        } else {
                                            found = true;
                                        }
                                    }
                                }

                                if (!found
                                        && entityPlayer.level().random.nextFloat() < CyberwareConfig.DROP_CHANCE / 100F) {
                                    ItemEntity itemEntity = new ItemEntity(entityPlayer.level(),
                                            entityPlayer.getX(), entityPlayer.getY(), entityPlayer.getZ(), itemStackToDrop);
                                    event.getDrops().add(itemEntity);
                                }
                            }
                        }
                    }
                    cyberwareUserData.resetWare(entityPlayer);
                }
            }
        }
    }

    private boolean shouldDropWare(DamageSource source) {
        if (source == EssentialsMissingHandler.noessence) return true;
        if (source == EssentialsMissingHandler.heartless) return true;
        if (source == EssentialsMissingHandler.brainless) return true;
        if (source == EssentialsMissingHandler.nomuscles) return true;
        if (source == EssentialsMissingHandler.spineless) return true;

        return false;
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void handleCZSpawn(LivingSpawnEvent.SpecialSpawn event) {
        if (!(event.getEntity() instanceof Zombie)) {
            return;
        }

        Zombie zombie = (Zombie) event.getEntity();

        if (zombie instanceof ZombifiedPiglin) {
            return;
        }

        if (CyberwareConfig.MOBS_ENABLE_CYBER_ZOMBIES
                && !(zombie instanceof EntityCyberZombie)
                && (!CyberwareConfig.MOBS_APPLY_DIMENSION_TO_BEACON
                || isValidDimension(event.getLevel()))) {
            int tier = BeaconBlockEntity.isInRange(zombie.level(), zombie.getX(), zombie.getY(), zombie.getZ());
            if (tier > 0) {
                float chance = tier == 2 ? LibConstants.BEACON_CHANCE
                        : tier == 1 ? LibConstants.BEACON_CHANCE_INTERNAL
                        : LibConstants.LARGE_BEACON_CHANCE;
                if ((event.getLevel().random.nextFloat() < (chance / 100F))) {
                    EntityCyberZombie entityCyberZombie = new EntityCyberZombie(EntityCyberZombie.TYPE, event.getLevel());
                    if (event.getLevel().random.nextFloat() < (LibConstants.BEACON_BRUTE_CHANCE / 100F)) {
                        entityCyberZombie.setBrute();
                    }
                    entityCyberZombie.moveTo(zombie.getX(), zombie.getY(), zombie.getZ(), zombie.getYRot(), zombie.getXRot());
                    entityCyberZombie.finalizeSpawn(event.getLevel(), event.getLevel().getCurrentDifficultyAt(entityCyberZombie.blockPosition()),
                            MobConversionEvent.ConversionReason.NATURAL, null, null);

                    for (EquipmentSlot slot : EquipmentSlot.values()) {
                        if (entityCyberZombie.getItemBySlot(slot).isEmpty()) {
                            entityCyberZombie.setItemSlot(slot, zombie.getItemBySlot(slot));
                        }
                    }
                    event.getLevel().addFreshEntity(entityCyberZombie);
                    zombie.setHealth(0F);
                    zombie.discard();

                    // continue processing to get a chance for clothing
                    zombie = entityCyberZombie;
                }
            }
        }

        if (CyberwareConfig.ENABLE_CLOTHES
                && CyberwareConfig.MOBS_ADD_CLOTHES) {
            if (zombie.getItemBySlot(EquipmentSlot.HEAD).isEmpty()
                    && zombie.level().random.nextFloat() < LibConstants.ZOMBIE_SHADES_CHANCE / 100F) {
                if (zombie.level().random.nextBoolean()) {
                    zombie.setItemSlot(EquipmentSlot.HEAD, new ItemStack(CyberwareContent2.shades));
                } else {
                    zombie.setItemSlot(EquipmentSlot.HEAD, new ItemStack(CyberwareContent2.shades2));
                }

                zombie.setDropChance(EquipmentSlot.HEAD, CyberwareConfig.MOBS_CLOTH_DROP_RARITY / 100F);
            }

            float chestRand = zombie.level().random.nextFloat();

            if (zombie.getItemBySlot(EquipmentSlot.CHEST).isEmpty()
                    && chestRand < LibConstants.ZOMBIE_TRENCH_CHANCE / 100F) {
                ItemStack stack = new ItemStack(CyberwareContent2.trenchCoat);
                int rand = zombie.level().random.nextInt(3);
                if (rand == 0) {
                    CyberwareContent2.trenchCoat.setColor(stack, 0x664028);
                } else if (rand == 1) {
                    CyberwareContent2.trenchCoat.setColor(stack, 0xEAEAEA);
                }

                zombie.setItemSlot(EquipmentSlot.CHEST, stack);
                zombie.setDropChance(EquipmentSlot.CHEST, CyberwareConfig.MOBS_CLOTH_DROP_RARITY / 100F);
            } else if (zombie.getItemBySlot(EquipmentSlot.CHEST).isEmpty()
                    && chestRand - (LibConstants.ZOMBIE_TRENCH_CHANCE / 100F) < LibConstants.ZOMBIE_BIKER_CHANCE / 100F) {
                ItemStack stack = new ItemStack(CyberwareContent2.jacket);
                zombie.setItemSlot(EquipmentSlot.CHEST, stack);
                zombie.setDropChance(EquipmentSlot.CHEST, CyberwareConfig.MOBS_CLOTH_DROP_RARITY / 100F);
            }
        }
    }

    public static void addRandomCyberware(EntityCyberZombie cyberZombie, boolean brute) {
        ICyberwareUserData cyberwareUserData = CyberwareAPI.getCapabilityOrNull(cyberZombie);
        if (cyberwareUserData == null) return;

        NonNullList<NonNullList<ItemStack>> wares = NonNullList.create();

        for (EnumSlot slot : EnumSlot.values()) {
            NonNullList<ItemStack> toAdd = cyberwareUserData.getInstalledCyberware(slot);
            toAdd.removeAll(Collections.singleton(ItemStack.EMPTY));
            wares.add(toAdd);
        }

        // Cyberzombies get all the power
        ItemStack battery = new ItemStack(CyberwareContent2.creativeBattery);
        wares.get(CyberwareContent2.creativeBattery.getSlot(battery).ordinal()).add(battery);

        int numberOfItemsToInstall = WeightedRandom.getRandomItem(cyberZombie.level().random, CyberwareContent2.numItems).num;
        if (brute) {
            numberOfItemsToInstall += LibConstants.MORE_ITEMS_BRUTE;
        }

        List<ItemStack> installed = new ArrayList<>();

        List<ZombieItem> items = new ArrayList<>(CyberwareContent2.zombieItems);
        for (int indexItem = 0; indexItem < numberOfItemsToInstall; indexItem++) {
            int tries = 0;
            ItemStack randomItem;
            ICyberware randomWare;

            do {
                randomItem = WeightedRandom.getRandomItem(cyberZombie.level().random, items).stack.copy();
                randomWare = CyberwareAPI.getCyberware(randomItem);
                randomItem.setCount(randomWare.installedStackSize(randomItem));
                tries++;
            }
            while (contains(wares.get(randomWare.getSlot(randomItem).ordinal()), randomItem) && tries < 10);

            if (tries < 10) {
                NonNullList<NonNullList<ItemStack>> required = randomWare.required(randomItem);
                for (NonNullList<ItemStack> requiredCategory : required) {
                    boolean found = false;
                    for (ItemStack option : requiredCategory) {
                        ICyberware optionWare = CyberwareAPI.getCyberware(option);
                        option.setCount(optionWare.installedStackSize(option));
                        if (contains(wares.get(optionWare.getSlot(option).ordinal()), option)) {
                            found = true;
                            break;
                        }
                    }

                    if (!found) {
                        ItemStack req = requiredCategory.get(cyberZombie.level().random.nextInt(requiredCategory.size())).copy();
                        ICyberware reqWare = CyberwareAPI.getCyberware(req);
                        req.setCount(reqWare.installedStackSize(req));
                        wares.get(reqWare.getSlot(req).ordinal()).add(req);
                        installed.add(req);
                        indexItem++;
                    }
                }
                wares.get(randomWare.getSlot(randomItem).ordinal()).add(randomItem);
                installed.add(randomItem);
            }
        }

        for (EnumSlot slot : EnumSlot.values()) {
            cyberwareUserData.setInstalledCyberware(cyberZombie, slot, wares.get(slot.ordinal()));
        }
        cyberwareUserData.updateCapacity();

        cyberZombie.setHealth(cyberZombie.getMaxHealth());
        cyberZombie.hasRandomWare = true;

        CyberwareAPI.updateData(cyberZombie);
    }

    private static boolean contains(NonNullList<ItemStack> nnlHaystack, ItemStack needle) {
        for (ItemStack check : nnlHaystack) {
            if (!check.isEmpty()
                    && !needle.isEmpty()
                    && check.getItem() == needle.getItem()
                    && check.getDamageValue() == needle.getDamageValue()) {
                return true;
            }
        }
        return false;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPotentialSpawns(@Nonnull LevelEvent.PotentialSpawns event) {
        if (event.getMobCategory() != MobCategory.MONSTER) return;
        if (!CyberwareConfig.MOBS_APPLY_DIMENSION_TO_SPAWNING) return;
        if (isValidDimension(event.getLevel())) return;

        List<MobSpawnSettings.SpawnerData> spawnListEntriesToRemove = new ArrayList<>();
        for (MobSpawnSettings.SpawnerData spawnData : event.getSpawnerDataList()) {
            if (spawnData.type.equals(EntityCyberZombie.TYPE)) {
                spawnListEntriesToRemove.add(spawnData);
            }
        }
        event.getSpawnerDataList().removeAll(spawnListEntriesToRemove);
    }

    public boolean isValidDimension(@Nonnull Level level) {
        boolean isListed = CyberwareConfig.MOBS_DIMENSION_IDS.contains(level.dimensionTypeId());
        return (CyberwareConfig.MOBS_IS_DIMENSION_BLACKLIST && !isListed)
                || (!CyberwareConfig.MOBS_IS_DIMENSION_BLACKLIST && isListed);
    }

    @SubscribeEvent
    public void syncCyberwareData(EntityJoinLevelEvent event) {
        if (!event.getLevel().isClientSide()) {
            Entity entity = event.getEntity();
            if (entity instanceof Player player) {
                ICyberwareUserData cyberwareUserData = CyberwareAPI.getCapabilityOrNull(player);
                if (cyberwareUserData != null) {
                    CompoundTag tagCompound = cyberwareUserData.serializeNBT();
                    CyberwarePacketHandler.sendToClient(new CyberwareSyncPacket(tagCompound, entity.getId()), (ServerPlayer) player);
                }
            }
        }
    }

    @SubscribeEvent
    public void startTrackingEvent(PlayerEvent.StartTracking event) {
        var player = event.getEntity();
        var target = event.getTarget();

        if (!target.level().isClientSide()) {
            var cyberwareUserData = CyberwareAPI.getCapabilityOrNull(target);
            if (cyberwareUserData != null) {
                var tagCompound = cyberwareUserData.serializeNBT();
                CyberwarePacketHandler.sendToClient(new CyberwareSyncPacket(tagCompound, target.getId()), (ServerPlayer) player);
            }
        }
    }
}