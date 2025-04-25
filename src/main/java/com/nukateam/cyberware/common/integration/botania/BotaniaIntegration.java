package com.nukateam.cyberware.common.integration.botania;

import com.nukateam.cyberware.api.item.ICyberware;
import com.nukateam.cyberware.common.CyberwareContent2;
import com.nukateam.cyberware.common.item.ItemCyberware;
import com.nukateam.cyberware.common.misc.NNLUtil;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.world.item.ItemStack;

public class BotaniaIntegration {
    public static final String MOD_ID = "botania";
    public static ItemCyberware manaLens;

    public static void preInit() {
        ItemStack stackManaglass = new ItemStack(Blocks.GLASS); // CyberwareContent.getItemStackByRegistryName("botania:managlass", 0);
        ItemStack stackManasteelIngot = new ItemStack(Items.IRON_INGOT); // CyberwareContent.getItemStackByRegistryName("botania:manaresource", 0);

        manaLens = new ItemManaLens("manaseer_lens", ICyberware.EnumSlot.EYES, new String[]{"lens", "link"});
        manaLens.setEssenceCost(1, 1);
        manaLens.setWeights(CyberwareContent2.COMMON, CyberwareContent2.COMMON);
        manaLens.setComponents(NNLUtil.fromArray(new ItemStack[]{stackManaglass,
                        stackManasteelIngot,
                        new ItemStack(CyberwareContent2.component, 1, 6),
                        new ItemStack(CyberwareContent2.component, 1, 7)}),
                NNLUtil.fromArray(new ItemStack[]{stackManaglass,
                        stackManasteelIngot,
                        new ItemStack(CyberwareContent2.component, 1, 6),
                        new ItemStack(CyberwareContent2.component, 1, 5)}));
    }
}
