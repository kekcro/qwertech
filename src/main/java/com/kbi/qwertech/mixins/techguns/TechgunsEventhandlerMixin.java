package com.kbi.qwertech.mixins.techguns;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import techguns.events.TechgunsEventhandler;

@Mixin(TechgunsEventhandler.class)
public abstract class TechgunsEventhandlerMixin {

    @Inject(method = "OnLivingAttack", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/util/DamageSource;getSourceOfDamage()Lnet/minecraft/entity/Entity;"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    public void preventCrash(LivingAttackEvent event, CallbackInfo ctx, EntityLivingBase elb, DamageSource src, Entity attacker) {
        if (attacker == null) ctx.cancel();
    }
}
