package org.ivangeevo.hc_villagers.mixin;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Objects;

@Mixin(MerchantEntity.class)
public abstract class MerchantEntityMixin {

    //@Inject(method = "trade", at = @At("HEAD"), cancellable = true)
    private void beforeTrade(TradeOffer offer, CallbackInfo ci) {
        MerchantEntity merchant = (MerchantEntity)(Object)this;

        if (merchant instanceof VillagerEntity villager) {
            // Determine if this offer is allowed to grant XP
            if (!isHighestTierOffer(villager, offer)) {
                ci.cancel();
            }
        }

    }

    @Unique
    private boolean isHighestTierOffer(VillagerEntity villager, TradeOffer offer) {
        TradeOfferList offers = villager.getOffers();
        if (offers.isEmpty()) return false;

        // The "last offers" in the list are the newest tier (added on last level-up)
        int villagerLevel = villager.getVillagerData().getLevel();

        // Find how many offers belong to this level
        Int2ObjectMap<TradeOffers.Factory[]> map = TradeOffers.PROFESSION_TO_LEVELED_TRADE
                .get(villager.getVillagerData().getProfession());
        if (map == null) return false;

        TradeOffers.Factory[] currentLevelFactories = map.get(villagerLevel);
        if (currentLevelFactories == null) return false;

        int levelOfferCount = currentLevelFactories.length;

        // Usually the last N offers correspond to current level
        int startIndex = Math.max(0, offers.size() - levelOfferCount);
        List<TradeOffer> lastLevelOffers = offers.subList(startIndex, offers.size());

        return lastLevelOffers.contains(offer);
    }

    // Replaces a offer from a merchant's offers after usage with another one from the list.
    @Inject(method = "trade", at = @At("TAIL"))
    private void afterTrade(TradeOffer offer, CallbackInfo ci) {
        if (!((MerchantEntity)(Object)this instanceof VillagerEntity villager)) return;
        TradeOfferList offers = villager.getOffers();
        int index = offers.indexOf(offer);
        if (index == -1) return;

        VillagerData data = villager.getVillagerData();
        int villagerLevel = data.getLevel();
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

        // Find the level that this offer index falls into
        int cumulative = 0;
        int offerLevel = 1;
        for (int level = 1; level <= 5; level++) {
            TradeOffers.Factory[] factories1 = profMap.get(level);
            if (factories == null) continue;
            cumulative += factories.length;
            if (index < cumulative) {
                offerLevel = level;
                break;
            }
        }

        if (replacement != null
                && replacement.getFirstBuyItem() != null
                || Objects.requireNonNull(replacement).getSecondBuyItem().isPresent()
        ) {
            if (offerLevel == villagerLevel) {
                // Not current tier — no XP
                offers.set(index, replacement);
            }

        }

    }

}
