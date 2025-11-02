package org.ivangeevo.hc_villagers.trades;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.TradedItem;

public class BTWRTradeOffers extends TradeOffers {

    public static class GuaranteedBuyItemFactory implements Factory {
        private final TradedItem stack;
        private final int maxUses;
        private final int experience;
        private final int price;
        private final float multiplier;

        public GuaranteedBuyItemFactory(ItemConvertible item, int count, int maxUses, int experience) {
            this(item, count, maxUses, experience, 1);
        }

        public GuaranteedBuyItemFactory(ItemConvertible item, int count, int maxUses, int experience, int price) {
            this(new TradedItem(item.asItem(), count), maxUses, experience, price);
        }

        public GuaranteedBuyItemFactory(TradedItem stack, int maxUses, int experience, int price) {
            this.stack = stack;
            this.maxUses = maxUses;
            this.experience = experience;
            this.price = price;
            this.multiplier = 0.05F;
        }

        public TradeOffer create(Entity entity, Random random) {
            return new TradeOffer(this.stack, new ItemStack(Items.EMERALD, this.price), this.maxUses, this.experience, this.multiplier);
        }
    }
}
