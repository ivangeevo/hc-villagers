package org.ivangeevo.hc_villagers.datagen.provider;

import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.item.Items;
import org.ivangeevo.hc_villagers.datagen.impl.*;

import java.util.function.Consumer;

public class FarmerTradesProvider extends HCTradeProvider {

    public FarmerTradesProvider(FabricDataOutput output) {
        super(output);
    }

    /** Villager type. Replace for easier implementation **/
    private static final String VILLAGER_TYPE = "minecraft:farmer";
    /** The default HC Villager profession progression **/
    private static final ProfessionDefinition HC_PROF_DEFINITION = ProfessionDefinition.of(VILLAGER_TYPE)
            .slots(1, 4, 5, 7, 8).required(5, 7, 10, 15);

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
        ItemSpec looseOrNormalDirt = ItemSpec.of("tough_environment:dirt_loose").count(48, 64).fallback(Items.DIRT);
        ItemSpec brownWoolBallOrBlock = ItemSpec.of("self_sustainable:brown_wool").count(16, 24).fallback(Items.BROWN_WOOL);

        return new LevelDefinition()
                .random(
                        Trade.buy(looseOrNormalDirt).sell(Items.EMERALD),
                        Trade.buy(Items.OAK_LOG, 32, 48).sell(Items.EMERALD),
                        Trade.buy(Items.SPRUCE_LOG, 32, 48).sell(Items.EMERALD),
                        Trade.buy(Items.BIRCH_LOG, 32, 48).sell(Items.EMERALD),
                        Trade.buy(Items.JUNGLE_LOG, 32, 48).sell(Items.EMERALD),
                        Trade.buy(brownWoolBallOrBlock).sell(Items.EMERALD),
                        Trade.buy(Items.BONE_MEAL, 32, 64).sell(Items.EMERALD)
                )
                .levelUp(
                        Trade.buy(Items.IRON_HOE).sell(Items.EMERALD)
                );

    }

    private LevelDefinition levelTwo() {
        ItemSpec rootsOrSugarCane = ItemSpec.of("vegehenna:sugar_cane_roots").fallback(Items.SUGAR_CANE);
        ItemSpec hempOrWheatSeeds = ItemSpec.of("bwt:hemp_seeds").count(24, 32).fallback(Items.WHEAT_SEEDS);
        ItemSpec millstoneOrIronPick = ItemSpec.of("bwt:mill_stone").fallback(Items.IRON_PICKAXE);

        return new LevelDefinition()
                .guaranteed(
                        Trade.buy(Items.EMERALD, 2, 3).sell(rootsOrSugarCane)
                )
                .random(
                        Trade.buy(Items.MILK_BUCKET).sell(Items.EMERALD, 1, 2),
                        Trade.buy(hempOrWheatSeeds).sell(Items.EMERALD),
                        Trade.buy(Items.SUGAR, 24, 32).sell(Items.EMERALD),
                        Trade.buy(Items.COCOA_BEANS, 10, 16).sell(Items.EMERALD),
                        Trade.buy(Items.BROWN_MUSHROOM, 10, 16).sell(Items.EMERALD),
                        Trade.buy(Items.GLASS_PANE, 16, 32).sell(Items.EMERALD),
                        Trade.buy(Items.EGG, 8, 12).sell(Items.EMERALD),
                        Trade.buy(Items.EMERALD).sell(Items.WHEAT, 8, 16),
                        Trade.buy(Items.EMERALD).sell(Items.APPLE, 2, 4)
                )
                .levelUp(
                        Trade.buy(millstoneOrIronPick).sell(Items.EMERALD, 2)
                );
    }

    private LevelDefinition levelThree() {
        ItemSpec chocolateOrCookie = ItemSpec.of("vegehenna:chocolate").fallback(Items.COOKIE);
        ItemSpec soapOrSlimeBall = ItemSpec.of("bwt:soap").fallback(Items.SLIME_BALL);
        ItemSpec cookedScrambledEggOrChicken = ItemSpec.of("self_sustainable:egg_scrambled_cooked").count(3).fallback(Items.COOKED_CHICKEN);
        ItemSpec cookedMushroomOmeletteOrStew = ItemSpec.of("self_sustainable:mushroom_omelette_cooked").count(3).fallback(Items.MUSHROOM_STEW);
        ItemSpec waterWheelOrIronBlock = ItemSpec.of("bwt:water_wheel").fallback(Items.IRON_BLOCK);

        return new LevelDefinition()
                .random(
                        Trade.buy(Items.MELON, 8, 12).sell(Items.EMERALD),
                        Trade.buy(Items.PUMPKIN, 8, 12).sell(Items.EMERALD),
                        Trade.buy(chocolateOrCookie).sell(Items.EMERALD),
                        Trade.buy(Items.SHEARS).sell(Items.EMERALD),
                        Trade.buy(Items.FLINT_AND_STEEL).sell(Items.EMERALD),
                        Trade.buy(soapOrSlimeBall).sell(Items.EMERALD, 2),
                        Trade.buy(Items.EMERALD).sell(Items.BREAD, 3),
                        Trade.buy(Items.EMERALD).sell(cookedScrambledEggOrChicken),
                        Trade.buy(Items.EMERALD).sell(cookedMushroomOmeletteOrStew)
                )
                .levelUp(
                        Trade.buy(waterWheelOrIronBlock).sell(Items.EMERALD, 3)
                );
    }

    private LevelDefinition levelFour() {
        ItemSpec cementBucketOrConcrete = ItemSpec.of("btwr:cement_bucket").fallback(Items.GRAY_CONCRETE);
        ItemSpec lightBlockOrRedstoneLamp = ItemSpec.of("bwt:light_block").count(2, 4).fallback(Items.REDSTONE_LAMP);
        ItemSpec stumpRemoverOrTNT = ItemSpec.of("sturdy_trees:stump_remover").count(6, 8).fallback(Items.TNT);
        ItemSpec stakeOrOakFence = ItemSpec.of("btwr:stake").fallback(Items.OAK_FENCE);
        ItemSpec planterOrComposter = ItemSpec.of("bwt:planter").count(16, 24).fallback(Items.COMPOSTER);

        return new LevelDefinition()
                .random(
                        Trade.buy(cementBucketOrConcrete).sell(Items.EMERALD, 2, 4),
                        Trade.buy(lightBlockOrRedstoneLamp).sell(Items.EMERALD),
                        Trade.buy(stumpRemoverOrTNT).sell(Items.EMERALD),
                        Trade.buy(stakeOrOakFence).and(Items.STRING, 16, 32).sell(Items.EMERALD),
                        Trade.buy(Items.EMERALD).sell(Items.PUMPKIN_PIE, 2),
                        Trade.buy(Items.EMERALD).sell(Items.COOKIE, 8, 10),
                        Trade.buy(Items.EMERALD).sell(Items.CAKE)
                )
                .levelUp(
                        Trade.buy(planterOrComposter).sell(Items.EMERALD)
                );

    }

    private LevelDefinition levelFive() {
        JsonObject tome = new JsonObject();
        tome.addProperty("enchantment", "minecraft:looting");

        // Stored level, for the book fallback only
        JsonObject levels = new JsonObject();
        levels.addProperty("minecraft:looting", 1);
        JsonObject stored = new JsonObject();
        stored.add("levels", levels);

        ItemSpec lootingTomeOrBook = ItemSpec.of("infernal_enchanting:arcane_tome")
                .component("infernal_enchanting:arcane_enchantment", tome)
                .fallback(Items.ENCHANTED_BOOK)
                .fallbackComponent("minecraft:stored_enchantments", stored);

        return new LevelDefinition()
                .guaranteed(
                        Trade.buy(Items.EMERALD, 12, 16).sell(Items.MYCELIUM),
                        Trade.buy(Items.EMERALD, 48, 64).and(Items.PAPER).sell(lootingTomeOrBook)
                );
    }
}
