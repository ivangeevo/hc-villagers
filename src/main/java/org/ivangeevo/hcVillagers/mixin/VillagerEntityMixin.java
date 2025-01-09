package org.ivangeevo.hcVillagers.mixin;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.village.VillagerData;
import net.minecraft.village.VillagerType;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.Set;

@Mixin(VillagerEntity.class)
public abstract class VillagerEntityMixin extends MerchantEntity {

    @Shadow private int foodLevel;

    @Shadow protected abstract boolean lacksFood();

    @Shadow public abstract VillagerData getVillagerData();

    @Shadow @Final private static Set<Item> GATHERABLE_ITEMS;

    public VillagerEntityMixin(EntityType<? extends MerchantEntity> entityType, World world) {
        super(entityType, world);
    }

    @Unique
    private static final Map<Item, Integer> ITEM_FOOD_VALUES = ImmutableMap.of(Items.DIAMOND, 12);


    @Inject(method = "<init>(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/World;Lnet/minecraft/village/VillagerType;)V", at = @At("TAIL"))
    private void onInit(EntityType<? extends VillagerEntity> entityType, World world, VillagerType type, CallbackInfo ci) {
        if (world != null && !world.isClient) {
            this.initGoals();
        }

    }

    @Inject(method = "getAvailableFood", at = @At("HEAD"), cancellable = true)
    private void customGetAvailableFood(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(ITEM_FOOD_VALUES.entrySet().stream().mapToInt(
                item -> this.getInventory().count(item.getKey()) * item.getValue()).sum());
    }

    @Inject(method = "canGather", at = @At("RETURN"), cancellable = true)
    private void canGatherDiamond(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        boolean originalConditions = GATHERABLE_ITEMS.contains(stack.getItem()) || this.getVillagerData().getProfession().gatherableItems().contains(stack.getItem()) && this.getInventory().canInsert(stack);
        cir.setReturnValue(stack.isOf(Items.DIAMOND) || originalConditions);
    }


    @Inject(method = "consumeAvailableFood()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/VillagerEntity;getInventory()Lnet/minecraft/inventory/SimpleInventory;", ordinal = 0), cancellable = true)
    private void injected(CallbackInfo ci) {
        for (int i = 0; i < this.getInventory().size(); ++i) {
            Integer integer;
            ItemStack itemStack = this.getInventory().getStack(i);
            if (itemStack.isEmpty() || (integer = ITEM_FOOD_VALUES.get(itemStack.getItem())) == null) continue;
            for (int k = itemStack.getCount(); k > 0; --k) {
                this.foodLevel += integer;
                this.getInventory().removeStack(i, 1);
                if (this.lacksFood()) continue;
                return;
            }
        }

        ci.cancel();
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new TemptGoal(this, 0.60, (stack) -> stack.isOf(Items.DIAMOND), false));
    }


}
