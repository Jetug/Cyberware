package com.nukateam.cyberware.common.block;

import javax.annotation.Nonnull;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemBlock;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import com.nukateam.cyberware.Cyberware;
import flaxbeard.cyberware.common.CyberwareConfig;
import com.nukateam.cyberware.common.CyberwareContent2;
import com.nukateam.cyberware.common.block.item.ItemBlockCyberware;
import com.nukateam.cyberware.common.block.tile.TileEntityScanner;

public class BlockScanner extends BlockContainer {

    public BlockScanner() {
        super(Material.IRON);
        setHardness(5.0F);
        setResistance(10.0F);
        setSoundType(SoundType.METAL);

        String name = "scanner";

        setRegistryName(name);
        ForgeRegistries.BLOCKS.register(this);

        ItemBlock itemBlock = new ItemBlockCyberware(this, "cyberware.tooltip.scanner");
        itemBlock.setRegistryName(name);
        ForgeRegistries.ITEMS.register(itemBlock);

        setTranslationKey(Cyberware.MODID + "." + name);

        setCreativeTab(Cyberware.creativeTab);
        GameRegistry.registerTileEntity(TileEntityScanner.class, new ResourceLocation(Cyberware.MODID, name));

        CyberwareContent2.blocks.add(this);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isOpaqueCube(IBlockState blockState) {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isFullCube(IBlockState blockState) {
        return false;
    }

    @Override
    public BlockEntity createNewTileEntity(@Nonnull World world, int metadata) {
        return new TileEntityScanner();
    }

    @SuppressWarnings("deprecation")
    @Nonnull
    @Override
    public EnumBlockRenderType getRenderType(IBlockState blockState) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState blockState, LivingEntity placer, ItemStack stack) {
        if (stack.hasDisplayName()) {
            BlockEntity tileentity = world.getTileEntity(pos);

            if (tileentity instanceof TileEntityScanner) {
                ((TileEntityScanner) tileentity).setCustomInventoryName(stack.getDisplayName());
            }
        }
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState blockState,
                                    Player entityPlayer, EnumHand hand,
                                    EnumFacing side, float hitX, float hitY, float hitZ) {
        BlockEntity tileentity = world.getTileEntity(pos);
        if (tileentity instanceof TileEntityScanner) {
            if (entityPlayer.isCreative()
                    && entityPlayer.isSneaking()) {
                TileEntityScanner scanner = ((TileEntityScanner) tileentity);
                scanner.ticks = CyberwareConfig.SCANNER_TIME - 200;
            }
            entityPlayer.openGui(Cyberware.INSTANCE, 3, world, pos.getX(), pos.getY(), pos.getZ());
        }

        return true;
    }

    @Override
    public void breakBlock(World world, @Nonnull BlockPos pos, @Nonnull IBlockState blockState) {
        BlockEntity tileentity = world.getTileEntity(pos);

        if (tileentity instanceof TileEntityScanner
                && !world.isRemote) {
            TileEntityScanner scanner = (TileEntityScanner) tileentity;

            for (int indexSlot = 0; indexSlot < scanner.slots.getSlots(); indexSlot++) {
                ItemStack stack = scanner.slots.getStackInSlot(indexSlot);
                if (!stack.isEmpty()) {
                    InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack);
                }
            }
        }

        super.breakBlock(world, pos, blockState);
    }

}
