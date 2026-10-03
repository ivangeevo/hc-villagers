package org.ivangeevo.hc_villagers.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.brain.*;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VillagerEntity.class)
public abstract class VillagerEntityMixin extends MerchantEntity {

    @Shadow private int foodLevel;

    @Shadow public abstract Brain<VillagerEntity> getBrain();
    @Shadow protected abstract void sayNo();

    @Shadow public abstract boolean canGather(ItemStack stack);

    /** Food a villager needs to breed (vanilla: 12). One diamond = exactly one breeding **/
    @Unique private static final int BREEDING_FOOD = 12;
    @Unique private static final int DIAMOND_FOOD_VALUE = 12;
    @Unique private static final double FOLLOW_RANGE = 10.0;
    @Unique private static final float FOLLOW_SPEED = 0.6f;

    public VillagerEntityMixin(EntityType<? extends MerchantEntity> entityType, World world) {
        super(entityType, world);
    }

    // Instead of the vanilla foods for villagers, only diamonds count now
    // This also makes isReadyToBreed() and lacksFood() work off diamonds.
    @Inject(method = "getAvailableFood", at = @At("HEAD"), cancellable = true)
    private void onlyDiamondsAreFood(CallbackInfoReturnable<Integer> cir) {
        int newValue = this.getInventory().count(Items.DIAMOND) * DIAMOND_FOOD_VALUE;
        cir.setReturnValue(newValue);
    }

    // Consume the diamond and set the food value enough to produce a baby
    @Inject(method = "consumeAvailableFood", at = @At("HEAD"), cancellable = true)
    private void consumeDiamonds(CallbackInfo ci) {
        SimpleInventory inventory = this.getInventory();
        while (this.foodLevel < BREEDING_FOOD && inventory.count(Items.DIAMOND) > 0) {
            inventory.removeItem(Items.DIAMOND, 1);
            this.foodLevel += DIAMOND_FOOD_VALUE;
        }
        ci.cancel();
    }

    // Set diamonds to be a valid gatherable item
    @Inject(method = "canGather", at = @At("RETURN"), cancellable = true)
    private void canGatherDiamond(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack.isOf(Items.DIAMOND) ) {
            cir.setReturnValue(!isFed() && this.getInventory().canInsert(stack));
        }
    }

    // Take only one item from a thrown stack
    @Inject(method = "loot", at = @At("HEAD"), cancellable = true)
    private void onLootDiamond(ItemEntity item, CallbackInfo ci) {
        ItemStack stack = item.getStack();

        if (!stack.isOf(Items.DIAMOND)) return;

        if (this.canGather(stack) && this.getInventory().addStack(stack.copyWithCount(1)).isEmpty()) {
            this.triggerItemPickedUpByEntityCriteria(item);
            this.sendPickup(item, 1);
            stack.decrement(1);
            if (stack.isEmpty()) {
                item.discard();
            }
        }
        ci.cancel(); // skip vanilla behavior for diamonds
    }

    // Set giving a diamond to a villager for breeding to only work when shift-clicking on them with it
    @Inject(method = "interactMob", at = @At("HEAD"), cancellable = true)
    private void onShiftInteractMob(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        ItemStack stack = player.getStackInHand(hand);

        if (!player.isSneaking() || !stack.isOf(Items.DIAMOND) || this.isBaby()) return;

        boolean client = this.getWorld().isClient;
        if (isFed()) {
            if (!client) {
                this.sayNo();
            }
            cir.setReturnValue(ActionResult.success(client));
            return;
        }

        if (!client) {
            ItemStack leftover = this.getInventory().addStack(stack.copyWithCount(1));

            if (!leftover.isEmpty()) {
                cir.setReturnValue(ActionResult.FAIL);
                return;
            }

            if (!player.isCreative()) {
                stack.decrement(1);
            }

            this.produceParticles(ParticleTypes.HAPPY_VILLAGER);
        }

        cir.setReturnValue(ActionResult.success(client));
    }

    // Follow a player holding a diamond
    @Inject(method = "mobTick", at = @At("TAIL"))
    private void tryFollowDiamondHolder(CallbackInfo ci) {
        if (this.isSleeping() || this.hasCustomer() || this.getBrain().hasActivity(Activity.PANIC)) return;

        PlayerEntity player = this.getWorld().getClosestPlayer(
                this.getX(), this.getY(), this.getZ(), FOLLOW_RANGE,
                e -> e instanceof PlayerEntity p && !p.isSpectator() && p.isHolding(Items.DIAMOND)
        );

        if (player == null) return;

        this.getBrain().remember(MemoryModuleType.LOOK_TARGET, new EntityLookTarget(player, true));

        if (this.squaredDistanceTo(player) > 9.0) {
            this.getBrain().remember(
                    MemoryModuleType.WALK_TARGET,
                    new WalkTarget(new EntityLookTarget(player, false), FOLLOW_SPEED, 2)
            );
        }
    }

    @Unique
    private boolean isFed() {
        return this.foodLevel + this.getInventory().count(Items.DIAMOND) * DIAMOND_FOOD_VALUE >= BREEDING_FOOD;
    }

}