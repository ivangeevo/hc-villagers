package org.ivangeevo.hc_villagers.trading;

/** Implemented on VillagerEntity by {@code VillagerTradingMixin}. */
public interface HCTradingVillager {
    HCTradeState hcVillagers$getTradeState();

    /** Calls vanilla's private {@code VillagerEntity.levelUp()}: level + 1, then {@code fillRecipes()}. */
    void hcVillagers$levelUp();
}
