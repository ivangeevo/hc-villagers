package org.ivangeevo.hc_villagers.util;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.TradedItem;

public class BuyVariableItemCountFactory implements TradeOffers.Factory {
    private final ItemConvertible item;
    private final int minCount;
    private final int maxCount;
    private final int maxUses;
    private final int experience;
    private final int price;
    private final float multiplier;

    public BuyVariableItemCountFactory(ItemConvertible item, int minCount, int maxCount, int maxUses, int experience) {
        this(item, minCount, maxCount, maxUses, experience, 1);
    }

    public BuyVariableItemCountFactory(ItemConvertible item, int minCount, int maxCount, int maxUses, int experience, int price) {
        this.item = item;
        this.minCount = minCount;
        this.maxCount = maxCount;
        this.maxUses = maxUses;
        this.experience = experience;
        this.price = price;
        this.multiplier = 0.05F;
    }

    @Override
    public TradeOffer create(Entity entity, Random random) {
        // Determine a random count between minCount and maxCount
        int count = random.nextInt(maxCount - minCount + 1) + minCount;

        return new TradeOffer(
                new TradedItem(item.asItem(), count),
                new ItemStack(Items.EMERALD, this.price),
                this.maxUses,
                this.experience,
                this.multiplier
        );
    }
}
