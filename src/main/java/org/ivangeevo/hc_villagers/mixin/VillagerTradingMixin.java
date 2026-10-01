package org.ivangeevo.hc_villagers.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.VillagerData;
import net.minecraft.world.World;
import org.ivangeevo.hc_villagers.trading.HCTradeLogic;
import org.ivangeevo.hc_villagers.trading.HCTradeState;
import org.ivangeevo.hc_villagers.trading.HCTradingVillager;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * BTW trading hooks.
 * <p> Every hook checks {@link HCTradeLogic#isManaged} first, so professions without an
 * {@code HCTradeTable} (vanilla ones that haven't been reworked, other mods' professions) behave exactly like vanilla.
 */
@Mixin(VillagerEntity.class)
public abstract class VillagerTradingMixin extends MerchantEntity implements HCTradingVillager {

    @Unique private static final String HC_TRADING_NBT = "hc_villagers:trading";

    @Shadow @Nullable private PlayerEntity lastCustomer;
    @Shadow protected abstract void levelUp();
    @Shadow public abstract VillagerData getVillagerData();

    @Unique private final HCTradeState hcVillagers$tradeState = new HCTradeState();

    public VillagerTradingMixin(EntityType<? extends MerchantEntity> entityType, World world) {
        super(entityType, world);
    }

    @Unique
    private VillagerEntity hcVillagers$self() {
        return (VillagerEntity) (Object) this;
    }

    @Override
    public HCTradeState hcVillagers$getTradeState() {
        return hcVillagers$tradeState;
    }

    @Override
    public void hcVillagers$levelUp() {
        this.levelUp();
    }

    // Vanilla never generates trades for managed professions. Runs on first getOffers() and on level-up.
    @Inject(method = "fillRecipes", at = @At("HEAD"), cancellable = true)
    private void hcVillagers$fillRecipes(CallbackInfo ci) {
        if (HCTradeLogic.isManaged(hcVillagers$self())) {
            HCTradeLogic.rebuildAll(hcVillagers$self(), this.getOffers());
            ci.cancel();
        }
    }

    // Replaces vanilla villager XP / auto level-up. Keeps vanilla's player XP orb and gossip behaviour.
    @Inject(method = "afterUsing", at = @At("HEAD"), cancellable = true)
    private void hcVillagers$afterUsing(TradeOffer offer, CallbackInfo ci) {
        if (!HCTradeLogic.isManaged(hcVillagers$self())) return;

        int orbXp = 3 + this.random.nextInt(4);
        this.lastCustomer = this.getCustomer();
        if (HCTradeLogic.onTrade(hcVillagers$self(), offer)) {
            orbXp += 5;
        }
        if (offer.shouldRewardPlayerExperience()) {
            this.getWorld().spawnEntity(new ExperienceOrbEntity(this.getWorld(), this.getX(), this.getY() + 0.5, this.getZ(), orbXp));
        }
        ci.cancel();
    }

    // Applies rerolls / level-up queued by trades once the trading screen is closed.
    // tick() instead of mobTick() so it also works for NoAI villagers.
    @Inject(method = "tick", at = @At("TAIL"))
    private void hcVillagers$tick(CallbackInfo ci) {
        if (!this.getWorld().isClient && HCTradeLogic.isManaged(hcVillagers$self())) {
            HCTradeLogic.tick(hcVillagers$self());
        }
    }

    @Inject(method = "beginTradeWith", at = @At("HEAD"))
    private void hcVillagers$beforeTrade(PlayerEntity customer, CallbackInfo ci) {
        if (HCTradeLogic.isManaged(hcVillagers$self())) {
            HCTradeLogic.prepareForTrading(hcVillagers$self());
        }
    }

    // The screen is open now (sendOffers ran), so the client has a syncId to attach the markers to.
    @Inject(method = "beginTradeWith", at = @At("TAIL"))
    private void hcVillagers$afterTradeOpened(PlayerEntity customer, CallbackInfo ci) {
        if (HCTradeLogic.isManaged(hcVillagers$self())) {
            HCTradeLogic.sendKindsToCustomer(hcVillagers$self());
        }
    }

    // BTW prices are fixed: no gossip or Hero of the Village discounts for managed professions.
    @Inject(method = "prepareOffersFor", at = @At("HEAD"), cancellable = true)
    private void hcVillagers$noDiscounts(PlayerEntity player, CallbackInfo ci) {
        if (HCTradeLogic.isManaged(hcVillagers$self())) ci.cancel();
    }

    // Offers are rerolled on use instead of restocked at the workstation.
    @Inject(method = "shouldRestock", at = @At("HEAD"), cancellable = true)
    private void hcVillagers$noRestock(CallbackInfoReturnable<Boolean> cir) {
        if (HCTradeLogic.isManaged(hcVillagers$self())) cir.setReturnValue(false);
    }

    // Profession changed: vanilla drops the offers, so drop our state too.
    @Inject(method = "setVillagerData", at = @At("HEAD"))
    private void hcVillagers$onVillagerDataChanged(VillagerData data, CallbackInfo ci) {
        if (hcVillagers$tradeState != null && this.getVillagerData().getProfession() != data.getProfession()) {
            hcVillagers$tradeState.reset();
        }
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void hcVillagers$writeTradeState(NbtCompound nbt, CallbackInfo ci) {
        nbt.put(HC_TRADING_NBT, hcVillagers$tradeState.toNbt());
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void hcVillagers$readTradeState(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains(HC_TRADING_NBT, NbtElement.COMPOUND_TYPE)) {
            hcVillagers$tradeState.fromNbt(nbt.getCompound(HC_TRADING_NBT));
        }
    }
}
