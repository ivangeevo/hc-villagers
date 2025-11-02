package org.ivangeevo.hc_villagers.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradedItem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(TradeOffer.class)
public abstract class TradeOfferMixin {

    @Mutable
    @Shadow @Final private int maxUses;

    // Makes each trade have only 1 use so that it's visually blocked in the GUI after trade
    @Inject(method = "<init>(Lnet/minecraft/village/TradedItem;Ljava/util/Optional;Lnet/minecraft/item/ItemStack;IIZIIFI)V", at = @At("TAIL"))
    private void afterInitTradeOffer(TradedItem firstBuyItem, Optional secondBuyItem, ItemStack sellItem, int _uses, int maxUses, boolean rewardingPlayerExperience, int specialPrice, int demandBonus, float priceMultiplier, int merchantExperience, CallbackInfo ci) {
        this.maxUses = 1;
    }
}
