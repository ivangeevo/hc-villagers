package org.ivangeevo.hc_villagers.mixin;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.*;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.village.*;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(VillagerEntity.class)
public abstract class VillagerEntityMixin extends MerchantEntity {

    @Shadow private int foodLevel;

    @Shadow protected abstract boolean lacksFood();

    @Shadow public abstract VillagerData getVillagerData();

    @Shadow @Final private static Set<Item> GATHERABLE_ITEMS;

    @Shadow public abstract boolean isReadyToBreed();

    public VillagerEntityMixin(EntityType<? extends MerchantEntity> entityType, World world) {
        super(entityType, world);
    }

    // BTW makes villagers require diamonds to breed.
    // 24 is the max hunger value for villagers, so we set it to that
    @Unique private static final int DIAMOND_FOOD_VALUE = 24;

    // Makes each added trade offer to only add 1 item in the list instead of the default 2
    @ModifyArg(method = "fillRecipes",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/passive/VillagerEntity;fillRecipesFromPool(Lnet/minecraft/village/TradeOfferList;[Lnet/minecraft/village/TradeOffers$Factory;I)V"),
                    index = 2
    )
    private int modifyShowedRecipesCount(int par3) {
        return 1;
    }

    @Inject(method = "fillRecipes", at = @At("TAIL"))
    private void afterFillRecipes(CallbackInfo ci) {

    }

   //@Inject(method = "fillRecipes", at = @At("HEAD"), cancellable = true)
    private void fillAllOffers(CallbackInfo ci) {
        VillagerData data = this.getVillagerData();
        Int2ObjectMap<TradeOffers.Factory[]> poolMap = TradeOffers.PROFESSION_TO_LEVELED_TRADE.get(data.getProfession());
        if (poolMap == null) return;

        TradeOfferList offers = this.getOffers();
        poolMap.forEach((level, factories) -> {
            // Instead of picking 2 random ones, take every valid factory result.
            for (TradeOffers.Factory factory : factories) {
                TradeOffer offer = factory.create(this, this.random);
                if (offer != null && !offers.contains(offer))
                    offers.add(offer);
            }
        });
        ci.cancel(); // prevents vanilla from running its original logic
    }

    //@Inject(method = "interactMob", at = @At("HEAD"), cancellable = true)
    private void onInteractMob(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        ItemStack handStack = player.getStackInHand(hand);

        if (lacksFood() && handStack.isOf(Items.DIAMOND)) {
            this.foodLevel += DIAMOND_FOOD_VALUE;
            handStack.decrement(1);
            this.produceParticles(ParticleTypes.HAPPY_VILLAGER);
            cir.setReturnValue(ActionResult.SUCCESS);
        }

    }

    //@Inject(method = "getAvailableFood", at = @At("HEAD"), cancellable = true)
    private void customGetAvailableFood(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(DIAMOND_FOOD_VALUE);
    }

    //@Inject(method = "canGather", at = @At("RETURN"), cancellable = true)
    private void canGatherDiamond(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        boolean originalConditions = GATHERABLE_ITEMS.contains(stack.getItem()) || this.getVillagerData().getProfession().gatherableItems().contains(stack.getItem()) && this.getInventory().canInsert(stack);
        cir.setReturnValue(stack.isOf(Items.DIAMOND) || originalConditions);
    }


    @Unique
    private static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> createDiamondFollowTask(float speed) {
        return ImmutableList.of(
                Pair.of(0, new MoveToTargetTask(80, 120)), createFreeFollowTask(),
                Pair.of(5, PlayWithVillagerBabiesTask.create()),
                Pair.of(
                        5, new RandomTask<>(ImmutableMap.of(MemoryModuleType.VISIBLE_VILLAGER_BABIES, MemoryModuleState.VALUE_ABSENT), ImmutableList.of(Pair.of(FindEntityTask.create(EntityType.VILLAGER, 8, MemoryModuleType.INTERACTION_TARGET, speed, 2), 2),
                                Pair.of(FindEntityTask.create(EntityType.CAT, 8, MemoryModuleType.INTERACTION_TARGET, speed, 2), 1),
                                Pair.of(FindWalkTargetTask.create(speed), 1),
                                Pair.of(GoTowardsLookTargetTask.create(speed, 2), 1),
                                Pair.of(new JumpInBedTask(speed), 2),
                                Pair.of(new WaitTask(20, 40), 2))
                        )),
                Pair.of(99, ScheduleActivityTask.create())
        );
    }

    private static Pair<Integer, Task<LivingEntity>> createFreeFollowTask() {
        return Pair.of(5, new RandomTask(ImmutableList.of(Pair.of(LookAtMobTask.create(EntityType.CAT, 8.0f), 8), Pair.of(LookAtMobTask.create(EntityType.VILLAGER, 8.0f), 2), Pair.of(LookAtMobTask.create(EntityType.PLAYER, 8.0f), 2), Pair.of(LookAtMobTask.create(SpawnGroup.CREATURE, 8.0f), 1), Pair.of(LookAtMobTask.create(SpawnGroup.WATER_CREATURE, 8.0f), 1), Pair.of(LookAtMobTask.create(SpawnGroup.AXOLOTLS, 8.0f), 1), Pair.of(LookAtMobTask.create(SpawnGroup.UNDERGROUND_WATER_CREATURE, 8.0f), 1), Pair.of(LookAtMobTask.create(SpawnGroup.WATER_AMBIENT, 8.0f), 1), Pair.of(LookAtMobTask.create(SpawnGroup.MONSTER, 8.0f), 1), Pair.of(new WaitTask(30, 60), 2))));
    }

    @Inject(method = "consumeAvailableFood()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/VillagerEntity;getInventory()Lnet/minecraft/inventory/SimpleInventory;", ordinal = 0), cancellable = true)
    private void injected(CallbackInfo ci) {
        for (int i = 0; i < this.getInventory().size(); ++i) {
            ItemStack itemStack = this.getInventory().getStack(i);
            if (itemStack.isEmpty()) continue;
            for (int k = itemStack.getCount(); k > 0; --k) {
                this.foodLevel += DIAMOND_FOOD_VALUE;
                this.getInventory().removeStack(i, 1);
                if (this.lacksFood()) continue;
                return;
            }
        }

        ci.cancel();
    }

    /**
    @Override
    protected void initGoals() {
        if (isSleeping()) {
            this.goalSelector.remove(new TemptGoal(this, 0.60, (stack -> stack.isOf(Items.DIAMOND)), true));
        } else {
            this.goalSelector.add(1, new TemptGoal(this, 0.60, (stack) -> stack.isOf(Items.DIAMOND), true));
        }
    }
    **/


}
