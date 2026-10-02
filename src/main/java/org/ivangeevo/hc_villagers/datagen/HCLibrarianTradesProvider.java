package org.ivangeevo.hc_villagers.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.item.Items;
import org.ivangeevo.hc_villagers.datagen.impl.*;

import java.util.function.Consumer;

public class HCLibrarianTradesProvider extends HCTradeProvider {

    protected HCLibrarianTradesProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    protected void generate(Consumer<ProfessionDefinition> professionConsumer) {
        professionConsumer.accept(
                ProfessionDefinition.of("minecraft:librarian")
                        .slots(1, 4, 5, 7, 8)
                        .required(5, 7, 10, 15)
                        .level(1, levelOne())
                        .level(2, levelTwo())
                        //.level(3, levelThree())
                        //.level(4, levelFour())
                        //.level(5, levelFive())

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
                                .sell(Items.EMERALD));

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
                                .sell(ItemSpec.of("bwt:block_dispenser").fallback(Items.STICKY_PISTON)),
                        Trade.buy(ItemSpec.of("animageddon:bat_wing").count(8, 12).fallback(Items.PHANTOM_MEMBRANE))
                                .sell(Items.EMERALD),
                        Trade.buy(Items.SPIDER_EYE, 12, 16).sell(Items.EMERALD),
                        Trade.buy(Items.GLASS_BOTTLE, 10, 16).sell(Items.EMERALD)
                )
                .levelUp(
                        Trade.buy(ItemSpec.of("bwt_hct:dormant_soulforge").fallback(Items.ANVIL))
                                .and(Items.NETHER_STAR)
                                .sell(ItemSpec.of("bwt:soulforge").fallback(Items.BEACON))
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
}
