package com.nukateam.cyberware.common.item;

import net.minecraft.init.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.core.NonNullList;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import com.nukateam.cyberware.Cyberware;
import com.nukateam.cyberware.api.item.IDeconstructable;
import com.nukateam.cyberware.common.CyberwareContent2;
import com.nukateam.cyberware.common.misc.NNLUtil;

public class ItemSwordCyberware extends ItemSword implements IDeconstructable {

    public ItemSwordCyberware(String name, ToolMaterial material) {
        super(material);

        setRegistryName(name);
        ForgeRegistries.ITEMS.register(this);
        setTranslationKey(Cyberware.MODID + "." + name);

        setCreativeTab(Cyberware.creativeTab);

        CyberwareContent2.items.add(this);
    }

    @Override
    public boolean canDestroy(ItemStack stack) {
        return true;
    }

    @Override
    public NonNullList<ItemStack> getComponents(ItemStack stack) {
        return NNLUtil.fromArray(new ItemStack[]
                {
                        new ItemStack(Items.IRON_INGOT, 2, 0),
                        new ItemStack(CyberwareContent2.component, 1, 2),
                        new ItemStack(CyberwareContent2.component, 1, 4)
                });
    }

}
