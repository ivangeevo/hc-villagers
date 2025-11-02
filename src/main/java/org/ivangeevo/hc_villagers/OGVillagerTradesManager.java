package org.ivangeevo.hc_villagers;

public class OGVillagerTradesManager
{

    public static void register() {
        //modifyFarmerTrades();
        //modifyLibrarianTrades();
    }

    /**
    private static void modifyFarmerTrades() {
        // Registering level 1 trades for Farmer profession
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.FARMER, 1, factories -> {
            // Remove vanilla level 1 trades (matching by item and price)
            factories.removeIf(factory -> isVanillaFarmerTrade(factory, Items.WHEAT, 20));

            // Add new modded trades
            factories.addAll(List.of(ModFarmerTrades.LEVEL_1_TRADES));
        });

        // Registering level 2 trades for Farmer profession
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.FARMER, 2, factories -> {
            factories.removeIf(factory -> isVanillaFarmerTrade(factory, Blocks.PUMPKIN, 6));
            factories.addAll(List.of(ModFarmerTrades.LEVEL_2_TRADES));
        });

        // Registering level 3 trades for Farmer profession
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.FARMER, 3, factories -> {
            factories.removeIf(factory -> isVanillaFarmerTrade(factory, Items.COOKIE, 3));
            factories.addAll(List.of(ModFarmerTrades.LEVEL_3_TRADES));
        });

        // Registering level 4 trades for Farmer profession
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.FARMER, 4, factories -> {
            factories.removeIf(factory -> isVanillaFarmerTrade(factory, Blocks.CAKE, 1));
            factories.addAll(List.of(ModFarmerTrades.LEVEL_4_TRADES));
        });

        // Registering level 5 trades for Farmer profession
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.FARMER, 5, factories -> {
            factories.removeIf(factory -> isVanillaFarmerTrade(factory, Items.GOLDEN_CARROT, 3));
            factories.addAll(List.of(ModFarmerTrades.LEVEL_5_TRADES));
        });
    }

    // Helper method to check if the trade matches the vanilla trade by item, count, and price
    private static boolean isVanillaFarmerTrade(TradeOffers.Factory factory, Item item, int count, int price) {
        if (factory instanceof TradeOffers.BuyItemFactory buyItemFactory) {
            // Cast the factory to our accessor to allow extended access
            BuyItemFactoryAccessor accessor = (BuyItemFactoryAccessor) buyItemFactory;
            // Compare the item and the count from the TradedItem
            boolean itemMatches = accessor.getStack().item() == item && accessor.getStack().count() == count;
            // Compare the price directly
            boolean priceMatches = accessor.getPrice() == price;

            return itemMatches && priceMatches;
        }
        return false;
    }


    private static void modifyLibrarianTrades() {
        // Registering level 1 trades for Librarian profession
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.LIBRARIAN, 1, factories -> {
            factories.removeAll(List.of(VanillaLibrarianTrades.LEVEL_1_TRADES));
            factories.addAll(List.of(ModLibrarianTrades.LEVEL_1_TRADES));
        });

        // Registering level 2 trades for Librarian profession
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.LIBRARIAN, 2, factories -> {
            factories.removeAll(List.of(VanillaLibrarianTrades.LEVEL_1_TRADES));
            factories.addAll(List.of(ModLibrarianTrades.LEVEL_2_TRADES));
        });

        // Registering level 3 trades for Librarian profession
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.LIBRARIAN, 3, factories -> {
            factories.removeAll(List.of(VanillaLibrarianTrades.LEVEL_1_TRADES));
            factories.addAll(List.of(ModLibrarianTrades.LEVEL_3_TRADES));
        });

        // Registering level 4 trades for Librarian profession
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.LIBRARIAN, 4, factories -> {
            factories.removeAll(List.of(VanillaLibrarianTrades.LEVEL_1_TRADES));
            factories.addAll(List.of(ModLibrarianTrades.LEVEL_4_TRADES));
        });

        // Registering level 5 trades for Librarian profession
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.LIBRARIAN, 5, factories -> {
            factories.removeAll(List.of(VanillaLibrarianTrades.LEVEL_1_TRADES));
            factories.addAll(List.of(ModLibrarianTrades.LEVEL_5_TRADES));
        });

    }
     **/

}
