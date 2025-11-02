package org.ivangeevo.hc_villagers.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.TradeOutputSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TradeOutputSlot.class)
public abstract class TradeOutputSlotMixin {

    @Inject(method = "onTakeItem", at = @At("TAIL"))
    private void afterOnTakeItem(PlayerEntity player, ItemStack stack, CallbackInfo ci, @Local(argsOnly = true) ItemStack itemStack) {

    }
}
