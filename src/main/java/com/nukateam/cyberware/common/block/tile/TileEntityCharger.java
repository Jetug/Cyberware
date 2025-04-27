package com.nukateam.cyberware.common.block.tile;

import javax.annotation.Nonnull;
import java.util.List;

import com.nukateam.cyberware.api.CyberwareAPI;
import com.nukateam.cyberware.api.ICyberwareUserData;
import com.nukateam.cyberware.common.regestry.CyberwareBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nullable;

public class TileEntityCharger extends BlockEntity implements IEnergyStorage {
    private final PowerContainer container = new PowerContainer();
    private boolean last = false;
    private final LazyOptional<IEnergyStorage> energyHandler = LazyOptional.of(() -> this);

    public TileEntityCharger(BlockPos pos, BlockState state) {
        super(CyberwareBlockEntities.CHARGER.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, ChargerBlockEntity charger) {
        if (level.isClientSide()) return;

        List<LivingEntity> entitiesInRange = level.getEntitiesOfClass(LivingEntity.class,
                new AABB(pos.getX(), pos.getY(), pos.getZ(),
                        pos.getX() + 1.0, pos.getY() + 2.5, pos.getZ() + 1.0));

        for (LivingEntity entityInRange : entitiesInRange) {
            ICyberwareUserData cyberwareUserData = CyberwareAPI.getCapabilityOrNull(entityInRange);
            if (cyberwareUserData != null
                    && !cyberwareUserData.isAtCapacity(ItemStack.EMPTY, 20)
                    && (charger.container.getStoredPower() >= CyberwareConfig.TESLA_PER_POWER)) {

                charger.container.takePower(CyberwareConfig.TESLA_PER_POWER, false);
                cyberwareUserData.addPower(20, ItemStack.EMPTY);

                if (entityInRange.tickCount % 5 == 0) {
                    spawnChargingParticles(level, pos);
                }
            }
        }

        boolean hasPower = (charger.container.getStoredPower() >= CyberwareConfig.TESLA_PER_POWER);
        if (hasPower != charger.last) {
            charger.last = hasPower;
            level.sendBlockUpdated(pos, state, state, 2);
        }
    }

    private static void spawnChargingParticles(Level level, BlockPos pos) {
        for (int i = 0; i < 5; i++) {
            double xOffset = (level.random.nextDouble() - 0.5) * 0.1;
            double zOffset = (level.random.nextDouble() - 0.5) * 0.1;
            level.addParticle(ParticleTypes.CLOUD,
                    pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                    xOffset, 0.05, zOffset);
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        container.deserializeNBT(tag.getCompound("power"));
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("power", container.serializeNBT());
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        load(pkt.getTag());
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            return energyHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    // IEnergyStorage implementation
    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return (int) container.givePower(maxReceive, simulate);
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return (int) container.takePower(maxExtract, simulate);
    }

    @Override
    public int getEnergyStored() {
        return (int) container.getStoredPower();
    }

    @Override
    public int getMaxEnergyStored() {
        return (int) container.getCapacity();
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    @Override
    public boolean canReceive() {
        return true;
    }
}
