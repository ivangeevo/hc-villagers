package org.ivangeevo.hc_villagers.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.TradeOutputSlot;
import net.minecraft.stat.Stats;
import net.minecraft.village.Merchant;
import net.minecraft.village.MerchantInventory;
import net.minecraft.village.TradeOffer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TradeOutputSlot.class)
public abstract class TradeOutputSlotMixin extends Slot {

    @Shadow @Final private MerchantInventory merchantInventory;

    @Shadow @Final private Merchant merchant;

    public TradeOutputSlotMixin(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    //@Inject(method = "onTakeItem", at = @At("HEAD"))
    private void afterOnTakeItem(PlayerEntity player, ItemStack stack, CallbackInfo ci) {
        this.onCrafted(stack);
        TradeOffer tradeOffer = this.merchantInventory.getTradeOffer();
        if (tradeOffer != null) {
            ItemStack itemStack = this.merchantInventory.getStack(0);
            ItemStack itemStack2 = this.merchantInventory.getStack(1);
            if (tradeOffer.depleteBuyItems(itemStack, itemStack2) || tradeOffer.depleteBuyItems(itemStack2, itemStack)) {
                this.merchant.trade(tradeOffer);
                player.incrementStat(Stats.TRADED_WITH_VILLAGER);
                this.merchantInventory.setStack(0, itemStack);
                this.merchantInventory.setStack(1, itemStack2);
            }

            this.merchant.setExperienceFromServer(this.merchant.getExperience() + tradeOffer.getMerchantExperience());
        }
    }
}
