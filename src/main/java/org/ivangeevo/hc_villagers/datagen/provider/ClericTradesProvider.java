package org.ivangeevo.hc_villagers.datagen.provider;

import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import org.ivangeevo.hc_villagers.datagen.impl.*;
import org.ivangeevo.hc_villagers.trading.data.TradeEntry;

import java.util.function.Consumer;

public class ClericTradesProvider extends HCTradeProvider {

    /** Level range for enchants that are provided by vanilla trades **/
    private static final int ENCHANT_MIN = 5;
    private static final int ENCHANT_MAX = 19;

    public ClericTradesProvider(FabricDataOutput output) {
        super(output);
    }

    /** Villager type. Replace for easier implementation **/
    private static final String VILLAGER_TYPE = "minecraft:cleric";
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
        ItemSpec hempOrWheat = ItemSpec.of("bwt:hemp").count(16, 24).fallback(Items.WHEAT);

        return new LevelDefinition()
                .random(
                        Trade.buy(hempOrWheat).sell(Items.EMERALD),
                        Trade.buy(Items.RED_MUSHROOM, 10, 16).sell(Items.EMERALD),
                        Trade.buy(Items.CACTUS, 32, 64).sell(Items.EMERALD),
                        Trade.buy(Items.FLINT_AND_STEEL).sell(Items.EMERALD),
                        Trade.buy(Items.PAINTING).sell(Items.EMERALD, 2, 3)
                )
                .levelUp(
                        Trade.buy(Items.ENCHANTING_TABLE).sell(Items.EMERALD, 2)
                );
    }

    private LevelDefinition levelTwo() {
        ItemSpec arcaneVesselOrNetherWartBlock = ItemSpec.of("bwt:arcane_vessel").fallback(Items.NETHER_WART_BLOCK);

        return new LevelDefinition()
                .random(
                        enchanted(Items.IRON_HELMET),
                        enchanted(Items.IRON_CHESTPLATE),
                        enchanted(Items.IRON_LEGGINGS),
                        enchanted(Items.IRON_BOOTS),
                        enchanted(Items.IRON_PICKAXE),
                        enchanted(Items.IRON_AXE),
                        enchanted(Items.IRON_SWORD),
                        enchanted(Items.DIAMOND_HELMET),
                        enchanted(Items.DIAMOND_CHESTPLATE),
                        enchanted(Items.DIAMOND_LEGGINGS),
                        enchanted(Items.DIAMOND_BOOTS),
                        enchanted(Items.DIAMOND_PICKAXE),
                        enchanted(Items.DIAMOND_AXE),
                        enchanted(Items.DIAMOND_SWORD)
                )
                .levelUp(
                        Trade.buy(arcaneVesselOrNetherWartBlock).sell(Items.EMERALD, 2)
                );
    }

    private LevelDefinition levelThree() {
        // Only mob heads if btwr is loaded which has the mob head drops mechanic
        ItemSpec skellyHeadOrBones = ItemSpec.of(Items.SKELETON_SKULL).count(6, 12).requiresMod("btwr").fallback(Items.BONE);
        ItemSpec creeperHeadOrGunpowder = ItemSpec.of(Items.CREEPER_HEAD).count(6, 12).requiresMod("btwr").fallback(Items.GUNPOWDER);
        ItemSpec zombieHeadOrRottenFlesh = ItemSpec.of(Items.ZOMBIE_HEAD).count(6, 12).requiresMod("btwr").fallback(Items.ROTTEN_FLESH);
        ItemSpec fleshBlockOrLeather = ItemSpec.of("animageddon:rotten_flesh_block").count(6, 12).fallback(Items.LEATHER);
        ItemSpec boneBlockOrBoneMeal = ItemSpec.of("animageddon:bone_block").count(6, 12).requiresMod("btwr").fallback(Items.BONE_MEAL);
        ItemSpec infusedOrWitherSkellySkull = ItemSpec.of("bwt_hct:infused_skull").fallback(Items.WITHER_SKELETON_SKULL);
        ItemSpec runedOrSkellySkull = ItemSpec.of("bwt_hct:runed_skull").fallback(Items.SKELETON_SKULL);

        return new LevelDefinition()
                .random(
                        Trade.buy(skellyHeadOrBones).sell(Items.EMERALD),
                        Trade.buy(creeperHeadOrGunpowder).sell(Items.EMERALD),
                        Trade.buy(zombieHeadOrRottenFlesh).sell(Items.EMERALD),
                        Trade.buy(fleshBlockOrLeather).sell(Items.EMERALD),
                        Trade.buy(boneBlockOrBoneMeal).sell(Items.EMERALD)
                )
                .levelUp(
                        Trade.buy(runedOrSkellySkull).and(Items.EMERALD, 6, 8).sell(infusedOrWitherSkellySkull)
                );
    }

    private LevelDefinition levelFour() {
        ItemSpec runedOrSkellySkull = ItemSpec.of("bwt_hct:runed_skull").fallback(Items.SKELETON_SKULL);
        ItemSpec infusedOrWitherSkellySkull = ItemSpec.of("bwt_hct:infused_skull").fallback(Items.WITHER_SKELETON_SKULL);
        ItemSpec soulUrnOrSoulSand = ItemSpec.of("bwt:soul_urn").count(2, 3).fallback(Items.SOUL_SAND).fallbackCount(64);
        ItemSpec canvasOrWhiteWool = ItemSpec.of("bwt:canvas").fallback(Items.WHITE_WOOL).fallbackCount(64);
        ItemSpec infernalEnchanterOrRespawnAnchor = ItemSpec.of("infernal_enchanting:infernal_enchanter").fallback(Items.RESPAWN_ANCHOR);

        return new LevelDefinition()
                .guaranteed(
                        Trade.buy(runedOrSkellySkull).and(Items.EMERALD, 6, 8).sell(infusedOrWitherSkellySkull)
                )
                .random(
                        Trade.buy(soulUrnOrSoulSand).sell(Items.EMERALD),
                        Trade.buy(Items.BLACK_CANDLE, 12, 16).sell(Items.EMERALD),
                        Trade.buy(Items.WHITE_CANDLE, 12, 16).sell(Items.EMERALD),
                        Trade.buy(Items.RED_CANDLE, 12, 16).sell(Items.EMERALD),
                        Trade.buy(Items.YELLOW_CANDLE, 12, 16).sell(Items.EMERALD),
                        Trade.buy(Items.BLUE_CANDLE, 12, 16).sell(Items.EMERALD),
                        Trade.buy(Items.GREEN_CANDLE, 12, 16).sell(Items.EMERALD),
                        Trade.buy(canvasOrWhiteWool).sell(Items.EMERALD, 2, 3)
                        )
                .levelUp(
                        Trade.buy(infernalEnchanterOrRespawnAnchor).sell(Items.EMERALD, 4)
                );
    }

    private LevelDefinition levelFive() {
        JsonObject tome = new JsonObject();
        tome.addProperty("enchantment", "minecraft:fortune");

        // Stored level, for the book fallback only
        JsonObject levels = new JsonObject();
        levels.addProperty("minecraft:fortune", 1);
        JsonObject stored = new JsonObject();
        stored.add("levels", levels);

        ItemSpec fortuneTomeOrBook = ItemSpec.of("infernal_enchanting:arcane_tome")
                .component("infernal_enchanting:arcane_enchantment", tome)
                .fallback(Items.ENCHANTED_BOOK)
                .fallbackComponent("minecraft:stored_enchantments", stored);

        return new LevelDefinition()
                .guaranteed(
                        Trade.buy(Items.EMERALD, 48, 64).and(Items.PAPER).sell(fortuneTomeOrBook)
                );
    }

    /** Creates a sell trade with applied enchantments if an undamaged item is provided in the buy trade **/
    private static TradeEntry enchanted(Item item) {
        return enchanted(item, Items.EMERALD, 2, 4, ENCHANT_MIN, ENCHANT_MAX);
    }

    private static TradeEntry enchanted(Item buyItem, Item sellItem, int sellItemMin, int sellItemMax, int enchMin, int enchMax) {
        ItemSpec enchantedItem = ItemSpec.of(buyItem).component("minecraft:damage", 0).enchant(enchMin, enchMax);
        return Trade.buy(buyItem).and(sellItem, sellItemMin, sellItemMax).sell(enchantedItem);
    }
}
