package com.nukateam.cyberware.common.integration.tan;

import com.nukateam.cyberware.common.integration.tan.CyberwareModifier.Type;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;
import com.nukateam.cyberware.api.item.ICyberware.EnumSlot;
import com.nukateam.cyberware.common.CyberwareContent2;
import com.nukateam.cyberware.common.item.ItemCyberware;

import static toughasnails.api.temperature.TemperatureHelper.registerTemperatureModifier;

public class ToughAsNailsIntegration {
    public static final String MOD_ID = "toughasnails";
    public static ItemCyberware sweat;

    public static void preInit() {
        registerTemperatureModifier(new CyberwareModifier(Type.BLUBBER));
        registerTemperatureModifier(new CyberwareModifier(Type.SWEAT));

        sweat = new ItemToughAsNailsUpgrade("tough_as_nails_upgrades",
                new EnumSlot[]{EnumSlot.SKIN, EnumSlot.SKIN},
                new String[]{"sweat", "blubber"});
        sweat.setEssenceCost(7, 14);
        sweat.setWeights(CyberwareContent2.UNCOMMON, CyberwareContent2.UNCOMMON);
        NonNullList<ItemStack> l1 = NonNullList.create();
        NonNullList<ItemStack> l2 = NonNullList.create();
        l1.add(new ItemStack(CyberwareContent2.component, 1, 8));
        l1.add(new ItemStack(CyberwareContent2.component, 2, 7));
        l1.add(new ItemStack(CyberwareContent2.component, 1, 1));
        l2.add(new ItemStack(CyberwareContent2.component, 2, 6));
        l2.add(new ItemStack(CyberwareContent2.component, 1, 7));
        l2.add(new ItemStack(CyberwareContent2.component, 3, 1));
        sweat.setComponents(
                l1, l2
        );
    }
}
