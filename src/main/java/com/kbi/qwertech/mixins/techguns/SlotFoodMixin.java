package com.kbi.qwertech.mixins.techguns;

import gregtech.items.MultiItemFood;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import techguns.gui.playerinventory.SlotFood;

@Mixin(SlotFood.class)
public abstract class SlotFoodMixin {

    @Inject(method = "func_75214_a", at = @At("HEAD"), cancellable = true, remap = false)
    public void acceptMetaFoods(ItemStack item, CallbackInfoReturnable<Boolean> ctx) {
        if (item.getItem() instanceof MultiItemFood) ctx.setReturnValue(true);
    }
}
