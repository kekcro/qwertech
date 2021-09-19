package com.kbi.qwertech.mixins.techguns;

import cpw.mods.fml.common.gameevent.TickEvent;
import gregapi.item.multiitem.food.IFoodStat;
import gregtech.items.MultiItemFood;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import techguns.TGPackets;
import techguns.Techguns;
import techguns.events.TechgunsTickHandler;
import techguns.extendedproperties.TechgunsExtendedPlayerProperties;
import techguns.items.armors.ITGSpecialSlot;
import techguns.packets.PacketTGExtendedPlayerSync;

@Mixin(TechgunsTickHandler.class)
public abstract class TechgunsTickHandlerMixin {

    @Inject(method = "onPlayerTick", at = @At(value = "INVOKE_ASSIGN", target = "Ltechguns/Techguns;consumeFood([Lnet/minecraft/item/ItemStack;II)Lnet/minecraft/item/ItemStack;"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD, remap = false)
    public void whatTheFuck(TickEvent.PlayerTickEvent event, CallbackInfo ctx, TechgunsExtendedPlayerProperties props, boolean wearingTGArmor,
                            IAttributeInstance instance, IAttributeInstance instance2, IAttributeInstance instance3,
                            float f, boolean bl, float f2, float f3,
                            int needed, ItemStack stack) {
        if (stack != null && stack.getItem() instanceof MultiItemFood) {
            ctx.cancel();
            EntityPlayer player = event.player;
            MultiItemFood food = (MultiItemFood) stack.getItem();
            IFoodStat stats = food.mFoodStats.get((short) food.getDamage(stack));
            int hunger = stats.getFoodLevel(food, stack, player);
            float saturation = stats.getSaturation(food, stack, player);

            food.onEaten(stack, player.worldObj, player);

            int left = hunger - needed;
            if (left > 0) {
                props.foodleft = left;
                props.lastSaturation = saturation;
            } else {
                props.foodleft = 0;
                props.lastSaturation = 0.0F;
            }

            if (!player.worldObj.isRemote) {
                TGPackets.network.sendTo(new PacketTGExtendedPlayerSync(player, props, true), (EntityPlayerMP) player);
            }

            //duplicate code to prevent sudden gaps in logic caused by early cancellation
            ItemStack faceslot = props.TG_inventory.inventory[0];
            if (faceslot != null) {
                ITGSpecialSlot face = (ITGSpecialSlot) faceslot.getItem();
                face.onPlayerTick(faceslot, event, props);
            }

            props.isGliding = false;
            stack = props.TG_inventory.inventory[1];
            if (stack != null) {
                ITGSpecialSlot back = (ITGSpecialSlot) stack.getItem();
                back.onPlayerTick(stack, event, props);
            }

            Techguns.proxy.handlePlayerGliding(props, event.player);
        }
    }
}
