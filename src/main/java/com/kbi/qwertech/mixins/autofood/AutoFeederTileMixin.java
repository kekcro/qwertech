package com.kbi.qwertech.mixins.autofood;

import gregtech.items.MultiItemFood;
import mods.immibis.autofood.AutoFeederTile;
import mods.immibis.core.TileBasicInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(AutoFeederTile.class)
public abstract class AutoFeederTileMixin extends TileBasicInventory {
    @Shadow(remap = false) private int timer;

    @Shadow(remap = false)
    public abstract int getRange();

    @Shadow(remap = false) @Final public static int RESCAN_INTERVAL;
    @Shadow(remap = false) @Final public static int SCAN_INTERVAL;

    public AutoFeederTileMixin(int size, String invname) {
        super(size, invname);
    }

    @Inject(method = "func_145845_h", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;func_77973_b()Lnet/minecraft/item/Item;", ordinal = 0, remap = false), remap = false, cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    public void acceptMetaFoods(CallbackInfo ctx, ItemStack stack) {
        if (stack != null && stack.getItem() instanceof MultiItemFood) {
            ctx.cancel();

            MultiItemFood food = (MultiItemFood) stack.getItem();
            this.timer = SCAN_INTERVAL;
            double range = (double) this.getRange() + 0.5D;
            double x = (double) this.xCoord + 0.5D;
            double y = (double) this.yCoord + 0.5D;
            double z = (double) this.zCoord + 0.5D;
            List<EntityPlayer> players = this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getBoundingBox(x - range, y - range, z - range, x + range, y + range, z + range));

            for (EntityPlayer p : players) {
                if (p.getFoodStats().needFood()) {
                    food.onEaten(stack, this.worldObj, p);
                    this.timer = RESCAN_INTERVAL;
                    if (stack.stackSize <= 0) {
                        break;
                    }
                }
            }

            if (stack.stackSize <= 0) {
                this.inv.contents[0] = null;
            }
        }
    }
}
