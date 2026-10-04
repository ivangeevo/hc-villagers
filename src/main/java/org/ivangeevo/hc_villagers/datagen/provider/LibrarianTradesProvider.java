package org.ivangeevo.hc_villagers.datagen.provider;

import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.item.Items;
import org.ivangeevo.hc_villagers.datagen.impl.*;

import java.util.function.Consumer;

public class LibrarianTradesProvider extends HCTradeProvider {

    /** Villager type. Replace for easier implementation **/
    private static final String VILLAGER_TYPE = "minecraft:librarian";
    /** The default HC Villager profession progression **/
    private static final ProfessionDefinition HC_PROF_DEFINITION = ProfessionDefinition.of(VILLAGER_TYPE)
            .slots(1, 4, 5, 7, 8).required(5, 7, 10, 15);

    public LibrarianTradesProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    protected void generate(Consumer<ProfessionDefinition> professionConsumer) {
        professionConsumer.accept(
                HC_PROF_DEFINITION
                        .level(1, levelOne())
                        .level(2, levelTwo())
                        .level(3, levelThree())
                        .level(4, levelFour())
                        .level(5, levelFive())
        );
    }

    private LevelDefinition levelOne() {
        return new LevelDefinition()
                .random(
                        Trade.buy("minecraft:paper", 24, 32).sell(Items.EMERALD),
                        Trade.buy("minecraft:ink_sac", 24, 32).sell(Items.EMERALD),
                        Trade.buy("minecraft:feather", 24, 32).sell(Items.EMERALD),
                        Trade.buy("minecraft:name_tag").sell(Items.EMERALD)
                )
                .levelUp(
                        Trade.buy(
                                ItemSpec.of("infernal_enchanting:ancient_manuscript").fallback("minecraft:writable_book"))
                                .sell(Items.EMERALD)
                );
    }

    private LevelDefinition levelTwo() {
        return new LevelDefinition()
                .guaranteedOneOf(
                        Trade.buy(
                                ItemSpec.of("bwt:redstone_eye").count(2).fallback(Items.REDSTONE))
                                .and(Items.EMERALD, 6, 9)
                                .sell(ItemSpec.of("bwt:detector_block").fallback(Items.DISPENSER)),
                        Trade.buy(
                                ItemSpec.of("bwt:redstone_eye").count(4).fallback(Items.REDSTONE))
                                .and(Items.EMERALD, 6, 9)
                                .sell(ItemSpec.of("bwt:buddy_block").fallback(Items.OBSERVER)),
                        Trade.buy(
                                ItemSpec.of(Items.MOSSY_COBBLESTONE).count(6))
                                .and(Items.EMERALD, 6, 9)
                                .sell(ItemSpec.of("bwt:block_dispenser").fallback(Items.STICKY_PISTON))
                )
                .guaranteed(
                        Trade.buy(Items.BOOK)
                                .and(Items.EMERALD, 2, 3)
                                .sell(ItemSpec.of("infernal_enchanting:ancient_manuscript").fallback(Items.WRITABLE_BOOK))
                )
                .random(
                        Trade.buy(Items.REDSTONE, 16, 24).sell(Items.EMERALD),
                        Trade.buy("btwr_ds:redstone_latch", 4, 6).sell(Items.EMERALD),
                        Trade.buy(Items.PISTON).sell(Items.EMERALD),
                        Trade.buy("bwt:turntable").sell(Items.EMERALD)
                )
                .levelUp(
                        Trade.buy(Items.BREWING_STAND).sell(Items.EMERALD, 2)
                );

    }

    private LevelDefinition levelThree() {
        return new LevelDefinition()
                .random(
                        Trade.buy(Items.NETHER_WART, 16, 24).sell(Items.EMERALD),
                        Trade.buy(Items.GLOWSTONE_DUST, 16, 24).sell(Items.EMERALD),
                        Trade.buy(ItemSpec.of("animageddon:nitre").count(48, 64).fallback(Items.GUNPOWDER))
                                .sell(Items.EMERALD),
                        Trade.buy(ItemSpec.of("animageddon:bat_wing").count(8, 12).fallback(Items.PHANTOM_MEMBRANE))
                                .sell(Items.EMERALD),
                        Trade.buy(Items.SPIDER_EYE, 12, 16).sell(Items.EMERALD),
                        Trade.buy(Items.GLASS_BOTTLE, 10, 16).sell(Items.EMERALD)
                )
                .levelUp(
                        Trade.buy(ItemSpec.of("bwt_hct:dormant_soul_forge").fallback(Items.ANVIL))
                                .and(Items.NETHER_STAR)
                                .sell(ItemSpec.of("bwt:soul_forge").fallback(Items.BEACON))
                );
    }

    private LevelDefinition levelFour() {
        return new LevelDefinition()
                .guaranteed(Trade.buy(ItemSpec.of("bwt_hct:dormant_soulforge").fallback(Items.ANVIL))
                        .and(Items.NETHER_STAR)
                        .sell(ItemSpec.of("bwt:soul_forge").fallback(Items.BEACON))
                )
                .random(
                        Trade.buy(ItemSpec.of("animageddon:witch_wart").count(8, 12).fallback(Items.AMETHYST_SHARD))
                                .sell(Items.EMERALD),
                        Trade.buy(ItemSpec.of("animageddon:mysterious_gland").count(12, 16).fallback(Items.SLIME_BALL))
                                .sell(Items.EMERALD),
                        Trade.buy(ItemSpec.of("animageddon:venom_sack").count(8, 12).fallback(Items.PUFFERFISH))
                                .sell(Items.EMERALD),
                        Trade.buy(Items.GHAST_TEAR, 4, 6).sell(Items.EMERALD),
                        Trade.buy(Items.MAGMA_CREAM, 8, 12).sell(Items.EMERALD),
                        Trade.buy(Items.BLAZE_POWDER, 32, 48).sell(Items.EMERALD)
                )
                .levelUp(
                        Trade.buy(ItemSpec.of("btwr:ender_spectacles").fallback(Items.SPYGLASS)).sell(Items.EMERALD, 4)
                );
    }

    private LevelDefinition levelFive() {
        JsonObject tome = new JsonObject();
        tome.addProperty("enchantment", "minecraft:power");

        // Stored level, for the book fallback only
        JsonObject levels = new JsonObject();
        levels.addProperty("minecraft:power", 1);
        JsonObject stored = new JsonObject();
        stored.add("levels", levels);

        ItemSpec powerTomeOrBook = ItemSpec.of("infernal_enchanting:arcane_tome")
                .component("infernal_enchanting:arcane_enchantment", tome)
                .fallback(Items.ENCHANTED_BOOK)
                .fallbackComponent("minecraft:stored_enchantments", stored);

        return new LevelDefinition()
                .guaranteed(
                        Trade.buy(Items.EMERALD, 48, 64).and(Items.PAPER).sell(powerTomeOrBook),
                        Trade.buy(Items.EMERALD, 6, 8).and(Items.ENDER_PEARL).sell(Items.ENDER_EYE)
                )
                .random(
                        Trade.buy(ItemSpec.of("bwt:blood_wood_sapling").count(24, 32).fallback(Items.SNIFFER_EGG))
                                .sell(Items.EMERALD),
                        Trade.buy(ItemSpec.of("bwt:nether_groth").fallback(Items.ANCIENT_DEBRIS))
                                .sell(Items.EMERALD),
                        Trade.buy(ItemSpec.of("btwr_ds:brimstone").count(24, 32).fallback(Items.CRYING_OBSIDIAN))
                                .sell(Items.EMERALD)
                );
    }

}