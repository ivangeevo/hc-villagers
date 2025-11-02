package org.ivangeevo.hc_villagers.mixin;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MerchantEntity.class)
public abstract class MerchantEntityMixin {

    // Replaces a offer from a villager's offers after usage with another one from the list for that level.
    @Inject(method = "trade", at = @At("TAIL"))
    private void afterTrade(TradeOffer offer, CallbackInfo ci) {
        if (!((MerchantEntity)(Object)this instanceof VillagerEntity villager)) return;
        TradeOfferList offers = villager.getOffers();
        int index = offers.indexOf(offer);
        if (index == -1) return;

        VillagerData data = villager.getVillagerData();
        Int2ObjectMap<TradeOffers.Factory[]> profMap =
                TradeOffers.PROFESSION_TO_LEVELED_TRADE.get(data.getProfession());
        if (profMap == null) return;

        TradeOffers.Factory[] factories = profMap.get(data.getLevel());
        if (factories == null || factories.length == 0) return;

        TradeOffer replacement = null;
        Random random = villager.getRandom();
        int attempts = 0;

        while (replacement == null && attempts++ < factories.length * 2) {
            TradeOffers.Factory factory = factories[random.nextInt(factories.length)];
            replacement = factory.create(villager, random);
        }

        if (replacement != null) {
            offers.set(index, replacement);
        }
    }

}
