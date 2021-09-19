package com.kbi.qwertech.mixins.techguns;

import gregtech.items.MultiItemFood;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import techguns.TGItems;
import techguns.Techguns;

@Mixin(Techguns.class)
public abstract class TechgunsMixin {
    /**
     * @author Yoghurt4C
     * @reason injects don't like loops
     */
    @Overwrite(remap = false)
    public static ItemStack consumeFood(ItemStack[] inv, int startIndex, int endIndex) {
        for (int i = startIndex; i < endIndex; ++i) {
            if (inv[i] != null && (inv[i].getItem() instanceof ItemFood || inv[i].getItem() instanceof MultiItemFood)) {
                ItemStack food = TGItems.newStack(inv[i], 1);
                --inv[i].stackSize;
                if (inv[i].stackSize <= 0) {
                    inv[i] = null;
                }

                return food;
            }
        }

        return null;
    }
}
