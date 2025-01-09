package org.ivangeevo.hcVillagers.mixin;

import net.minecraft.village.TradeOffers;
import net.minecraft.village.TradedItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TradeOffers.BuyItemFactory.class)
public interface BuyItemFactoryAccessor
{

    // Accessor for the 'stack' field
    @Accessor
    TradedItem getStack();

    // Accessor for the 'price' field
    @Accessor
    int getPrice();
}
