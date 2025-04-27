package com.nukateam.cyberware.common.block.tile;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import com.nukateam.cyberware.common.block.BlockBeaconPost;
import org.jetbrains.annotations.NotNull;
import com.nukateam.cyberware.common.regestry.CyberwareBlockEntities;

public class TileEntityBeaconPost extends BlockEntity {
    public static class TileEntityBeaconPostMaster extends TileEntityBeaconPost {
        public TileEntityBeaconPostMaster(BlockPos pos, BlockState state) {
            super(pos, state);
        }

        @OnlyIn(Dist.CLIENT)
        @Override
        public @NotNull AABB getRenderBoundingBox() {
            return new AABB(
                    worldPosition.getX() - 1, worldPosition.getY(),
                    worldPosition.getZ() - 1,
                    worldPosition.getX() + 2, worldPosition.getY() + 10,
                    worldPosition.getZ() + 2
            );
        }

        @Override
        public void setMasterLoc(BlockPos start) {
            throw new IllegalStateException("NO");
        }
    }

    public BlockPos master = null;
    public boolean destructing = false;

    public TileEntityBeaconPost(BlockPos pos, BlockState state) {
        super(CyberwareBlockEntities.BEACON_POST.get(), pos, state);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public double getViewDistance() {
        return 16384.0D;
    }

    public void setMasterLoc(BlockPos start) {
        this.master = start;
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            setChanged();
        }
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
    }

    public void destruct() {
        if (!destructing && level != null) {
            destructing = true;
            for (int y = 0; y <= 9; y++) {
                for (int x = -1; x <= 1; x++) {
                    for (int z = -1; z <= 1; z++) {
                        if (y > 3 && (x != 0 || z != 0)) {
                            continue;
                        }

                        BlockPos newPos = worldPosition.offset(x, y, z);
                        BlockState state = level.getBlockState(newPos);

                        if (state.getBlock() instanceof BlockBeaconPost && state.getValue(BlockBeaconPost.TRANSFORMED) > 0) {
                            level.getBlockEntity(newPos);
                            level.setBlock(newPos, state.setValue(BlockBeaconPost.TRANSFORMED, 0), Block.UPDATE_ALL);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        if (!(this instanceof TileEntityBeaconPostMaster)) {
            int x = tag.getInt("xx");
            int y = tag.getInt("yy");
            int z = tag.getInt("zz");
            this.master = new BlockPos(x, y, z);
        }
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        CompoundTag tag = pkt.getTag();
        if (tag != null) {
            load(tag);
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);

        if (!(this instanceof TileEntityBeaconPostMaster) && master != null) {
            tag.putInt("xx", master.getX());
            tag.putInt("yy", master.getY());
            tag.putInt("zz", master.getZ());
        }
    }
}