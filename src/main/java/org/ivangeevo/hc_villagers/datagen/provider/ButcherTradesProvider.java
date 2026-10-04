package org.ivangeevo.hc_villagers.datagen.provider;

import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.item.Items;
import org.ivangeevo.hc_villagers.datagen.impl.*;

import java.util.function.Consumer;

public class ButcherTradesProvider extends HCTradeProvider {

    public ButcherTradesProvider(FabricDataOutput output) {
        super(output);
    }

    /** Villager type. Replace for easier implementation **/
    private static final String VILLAGER_TYPE = "minecraft:butcher";
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
        ItemSpec ropeOrLadder = ItemSpec.of("bwt:rope").count(16, 24).fallback(Items.LADDER);
        ItemSpec rawChevalOrRabbit = ItemSpec.of("animageddon:cheval").count(2).fallback(Items.RABBIT);
        ItemSpec bwtCauldronOrVanillaOne = ItemSpec.of("bwt:cauldron").fallback(Items.CAULDRON);

        return new LevelDefinition()
                .random(
                        Trade.buy(Items.ARROW, 16, 24).sell(Items.EMERALD),
                        Trade.buy(Items.SHEARS).sell(Items.EMERALD),
                        Trade.buy(Items.FISHING_ROD).sell(Items.EMERALD),
                        Trade.buy(Items.BOW).sell(Items.EMERALD),
                        Trade.buy(ropeOrLadder).sell(Items.EMERALD),
                        Trade.buy(Items.EMERALD).sell(rawChevalOrRabbit),
                        Trade.buy(Items.EMERALD).sell(Items.BEEF, 2),
                        Trade.buy(Items.EMERALD).sell(Items.PORKCHOP, 2),
                        Trade.buy(Items.EMERALD).sell(Items.MUTTON, 3),
                        Trade.buy(Items.EMERALD).sell(Items.COD, 3),
                        Trade.buy(Items.EMERALD).sell(Items.SALMON, 3),
                        Trade.buy(Items.EMERALD).sell(Items.CHICKEN, 3)
                )
                .levelUp(
                        Trade.buy(bwtCauldronOrVanillaOne).sell(Items.EMERALD)
                );
    }

    private LevelDefinition levelTwo() {
        ItemSpec flourOrWheat = ItemSpec.of("bwt:flour").count(16, 24).fallback(Items.WHEAT);
        ItemSpec spruceBarkOrWood = ItemSpec.of("sturdy_trees:bark_spruce").count(48, 64).fallback(Items.SPRUCE_WOOD);
        ItemSpec dungOrRottenFlesh = ItemSpec.of("bwt:dung").count(10, 16).fallback(Items.ROTTEN_FLESH);
        ItemSpec sandwichOrRabbitStew = ItemSpec.of("self_sustainable:sandwich").count(2).fallback(Items.RABBIT_STEW);
        ItemSpec chowderOrMushroomStew = ItemSpec.of("self_sustainable:chowder").count(2).fallback(Items.MUSHROOM_STEW);
        ItemSpec hamAndEggsOrCookedRabbit = ItemSpec.of("self_sustainable:ham_and_eggs").count(2).fallback(Items.COOKED_RABBIT);
        ItemSpec steakPotatoesOrCookedPorkchop = ItemSpec.of("self_sustainable:steak_and_potatoes").count(2).fallback(Items.COOKED_PORKCHOP);
        ItemSpec sawOrStoneCutter = ItemSpec.of("bwt:saw").fallback(Items.STONECUTTER);

        return new LevelDefinition()
                .random(
                        Trade.buy(Items.LEATHER, 4, 6).sell(Items.EMERALD),
                        Trade.buy(flourOrWheat).sell(Items.EMERALD),
                        Trade.buy(spruceBarkOrWood).sell(Items.EMERALD),
                        Trade.buy(dungOrRottenFlesh).sell(Items.EMERALD),
                        Trade.buy(Items.EMERALD).sell(sandwichOrRabbitStew),
                        Trade.buy(Items.EMERALD).sell(chowderOrMushroomStew),
                        Trade.buy(Items.EMERALD).sell(hamAndEggsOrCookedRabbit),
                        Trade.buy(Items.EMERALD).sell(steakPotatoesOrCookedPorkchop)

                )
                .levelUp(
                        Trade.buy(sawOrStoneCutter).sell(Items.EMERALD, 2)
                );
    }

    private LevelDefinition levelThree() {
        ItemSpec cookedLiverOrBeef = ItemSpec.of("btwr:beast_liver_cooked").fallback(Items.COOKED_BEEF);
        ItemSpec cookedWolfchopOrMutton = ItemSpec.of("bwt:wolf_chop").count(2, 3).fallback(Items.COOKED_MUTTON);
        ItemSpec steakDinnerOrCake = ItemSpec.of("self_sustainable:steak_dinner").count(2).fallback(Items.CAKE);
        ItemSpec porkDinnerOrCake = ItemSpec.of("self_sustainable:pork_dinner").count(2).fallback(Items.CAKE);
        ItemSpec wolfDinnerOrCake = ItemSpec.of("self_sustainable:wolf_dinner").count(2).fallback(Items.CAKE);
        ItemSpec chickenSoupOrHoneyBottle = ItemSpec.of("self_sustainable:chicken_soup").count(2).fallback(Items.HONEY_BOTTLE);
        ItemSpec cookedKebabOrSusStew = ItemSpec.of("self_sustainable:cooked_kebab").count(2).fallback(Items.HONEY_BOTTLE);
        ItemSpec tannedLeatherOrRabbitHide = ItemSpec.of("bwt:tanned_leather").count(4, 6).fallback(Items.RABBIT_HIDE);
        ItemSpec breedingHarnessOrLead = ItemSpec.of("bwt:breeding_harness").fallback(Items.LEAD);

        return new LevelDefinition()
                .random(
                        Trade.buy(Items.CARROT, 10, 16).sell(Items.EMERALD),
                        Trade.buy(Items.POTATO, 10, 16).sell(Items.EMERALD),
                        Trade.buy(cookedLiverOrBeef).sell(Items.EMERALD, 2),
                        Trade.buy(Items.SADDLE).sell(Items.EMERALD, 2),
                        Trade.buy(cookedWolfchopOrMutton).sell(Items.EMERALD),
                        Trade.buy(Items.EMERALD).sell(steakDinnerOrCake),
                        Trade.buy(Items.EMERALD).sell(porkDinnerOrCake),
                        Trade.buy(Items.EMERALD).sell(wolfDinnerOrCake),
                        Trade.buy(Items.EMERALD).sell(chickenSoupOrHoneyBottle),
                        Trade.buy(Items.EMERALD).sell(cookedKebabOrSusStew),
                        Trade.buy(Items.EMERALD).sell(tannedLeatherOrRabbitHide)
                )
                .levelUp(
                        Trade.buy(breedingHarnessOrLead).sell(Items.EMERALD, 3)
                );
    }

    private LevelDefinition levelFour() {
        ItemSpec runedSkullOrWitherSkellySkull = ItemSpec.of("btwr_ds:runed_skull").fallback(Items.WITHER_SKELETON_SKULL);
        ItemSpec cookedMysteryMeatOrTurtleScute = ItemSpec.of("btwr:mystery_meat_cooked").count(2, 3).fallback(Items.TURTLE_SCUTE);
        ItemSpec screwOrTripwireHook = ItemSpec.of("bwt:screw").fallback(Items.TRIPWIRE_HOOK);
        ItemSpec compositeBowOrCrossbow = ItemSpec.of("bwt:composite_bow").fallback(Items.CROSSBOW);
        ItemSpec heartyStewOrGoldenCarrot = ItemSpec.of("self_sustainable:hearty_stew").fallback(Items.GOLDEN_CARROT);
        ItemSpec tanLeatherHelmOrNormal = ItemSpec.of("btwr:leather_tanned_helmet").fallback(Items.LEATHER_HELMET);
        ItemSpec tanLeatherChestOrNormal = ItemSpec.of("btwr:leather_tanned_chestplate").fallback(Items.LEATHER_CHESTPLATE);
        ItemSpec tanLeatherLegsOrNormal = ItemSpec.of("btwr:leather_tanned_leggings").fallback(Items.LEATHER_LEGGINGS);
        ItemSpec tanLeatherBootsOrNormal = ItemSpec.of("btwr:leather_tanned_boots").fallback(Items.LEATHER_BOOTS);

        // TODO: Either make a way to set block states based item reference
        //  or make the chopping block 2 separate blocks for clear/bloody
        ItemSpec choppingBlockOrAnvil = ItemSpec.of("bwt_hct:chopping_block").fallback(Items.ANVIL);

        return new LevelDefinition()
                .guaranteed(
                        Trade.buy(Items.EMERALD, 6, 8).and(Items.SKELETON_SKULL).sell(runedSkullOrWitherSkellySkull)
                )
                .random(
                        Trade.buy(cookedMysteryMeatOrTurtleScute).sell(Items.EMERALD),
                        Trade.buy(screwOrTripwireHook).sell(Items.EMERALD, 2, 3),
                        Trade.buy(compositeBowOrCrossbow).sell(Items.EMERALD, 2, 3),
                        Trade.buy(Items.LEAD).sell(Items.EMERALD),
                        Trade.buy(Items.EMERALD).sell(heartyStewOrGoldenCarrot),
                        Trade.buy(Items.EMERALD, 3, 4).sell(tanLeatherHelmOrNormal),
                        Trade.buy(Items.EMERALD, 6, 8).sell(tanLeatherChestOrNormal),
                        Trade.buy(Items.EMERALD, 4, 6).sell(tanLeatherLegsOrNormal),
                        Trade.buy(Items.EMERALD, 2, 3).sell(tanLeatherBootsOrNormal)
                )
                .levelUp(
                        Trade.buy(choppingBlockOrAnvil).sell(Items.EMERALD, 4)
                );

    }

    private LevelDefinition levelFive() {
        ItemSpec dynamiteOrBlazePowder = ItemSpec.of("bwt:dynamite").count(4, 6).fallback(Items.BLAZE_POWDER);
        ItemSpec soapOrSlimeBall = ItemSpec.of("bwt:soap").fallback(Items.SLIME_BALL);
        ItemSpec netheriteBattleAxeOrNormal = ItemSpec.of("bwt:battle_axe").fallback(Items.NETHERITE_AXE);
        ItemSpec companionCubeOrEnderChest = ItemSpec.of("bwt:companion_cube").fallback(Items.ENDER_CHEST);
        ItemSpec broadheadOrSpectralArrow = ItemSpec.of("bwt:broadhead_arrow").count(6, 10).fallback(Items.SPECTRAL_ARROW);

        JsonObject tome = new JsonObject();
        tome.addProperty("enchantment", "minecraft:sharpness");

        // Stored level, for the book fallback only
        JsonObject levels = new JsonObject();
        levels.addProperty("minecraft:sharpness", 1);
        JsonObject stored = new JsonObject();
        stored.add("levels", levels);

        ItemSpec sharpnessTomeOrBook = ItemSpec.of("infernal_enchanting:arcane_tome")
                .component("infernal_enchanting:arcane_enchantment", tome)
                .fallback(Items.ENCHANTED_BOOK)
                .fallbackComponent("minecraft:stored_enchantments", stored);

        return new LevelDefinition()
                .guaranteed(
                        Trade.buy(Items.EMERALD, 48, 64).and(Items.PAPER).sell(sharpnessTomeOrBook)
                )
                .random(
                        Trade.buy(dynamiteOrBlazePowder).sell(Items.EMERALD),
                        Trade.buy(Items.LIGHTNING_ROD).and(soapOrSlimeBall).sell(Items.EMERALD, 3, 5),
                        Trade.buy(netheriteBattleAxeOrNormal).sell(Items.EMERALD, 3, 5),
                        Trade.buy(companionCubeOrEnderChest).sell(Items.EMERALD, 1, 2),
                        Trade.buy(broadheadOrSpectralArrow).sell(Items.EMERALD)
                );
    }
}
