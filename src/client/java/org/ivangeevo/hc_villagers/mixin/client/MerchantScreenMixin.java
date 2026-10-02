package org.ivangeevo.hc_villagers.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.ivangeevo.hc_villagers.HCVillagersMod;
import org.ivangeevo.hc_villagers.client.ClientTradeKinds;
import org.ivangeevo.hc_villagers.trading.HCTradeKind;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MerchantScreen.class)
public abstract class MerchantScreenMixin extends HandledScreen<MerchantScreenHandler> {

    /**
     * GUI sprites for the markers.
     * <p>{@code assets/hc_villagers/textures/gui/sprites/trade_plus.png}
     * <p>{@code assets/hc_villagers/textures/gui/sprites/trade_double_plus.png}
     */
    @Unique private static final Identifier PLUS_SPRITE = Identifier.of(HCVillagersMod.MOD_ID, "trade_plus");
    @Unique private static final Identifier DOUBLE_PLUS_SPRITE = Identifier.of(HCVillagersMod.MOD_ID, "trade_double_plus");

    @Unique private static final int VISIBLE_ROWS = 7;
    @Unique private static final int ROW_HEIGHT = 20;
    @Unique private static final int PLUS_WIDTH = 6;
    @Unique private static final int DOUBLE_PLUS_WIDTH = 12;
    @Unique private static final int PLUS_HEIGHT = 6;
    @Unique private static final int SINGLE_PLUS_X = 60;
    @Unique private static final int MARK_Y_OFFSET = 8;
    /**
     * The old text "+" glyph starts one pixel below the text's y; the sprite's cross starts at its top row.
     * This keeps the sprites exactly where the text was. Set to 0 to move them up by one pixel.
     */
    @Unique private static final int SPRITE_Y_ADJUST = 1;

    @Shadow private int selectedIndex;
    @Shadow int indexStartOffset;

    public MerchantScreenMixin(MerchantScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    // Guard against an offer list that is shorter than the selected index / scroll position.
    // Vanilla render() does getRecipes().get(selectedIndex) without a bounds check, so clamp first.
    @Inject(method = "render", at = @At("HEAD"))
    private void clampIndices(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        int size = this.handler.getRecipes().size();
        if (size > 0 && this.selectedIndex >= size) {
            this.selectedIndex = size - 1;
        }
        int maxOffset = Math.max(0, size - VISIBLE_ROWS);
        if (this.indexStartOffset > maxOffset) {
            this.indexStartOffset = maxOffset;
        }
    }

    // Draws "+" / "++" below the arrow of each visible offer row.
    @Inject(method = "render", at = @At("TAIL"))
    private void drawTradeMarkers(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        int size = this.handler.getRecipes().size();
        int first = size > VISIBLE_ROWS ? this.indexStartOffset : 0;

        for (int row = 0; row < VISIBLE_ROWS; row++) {
            int index = first + row;
            if (index >= size) break;

            HCTradeKind kind = ClientTradeKinds.get(this.handler.syncId, index);
            Identifier sprite;
            int width;
            if (kind == HCTradeKind.PLUS) {
                sprite = PLUS_SPRITE;
                width = PLUS_WIDTH;
            } else if (kind == HCTradeKind.LEVEL_UP) {
                sprite = DOUBLE_PLUS_SPRITE;
                width = DOUBLE_PLUS_WIDTH;
            } else {
                continue;
            }

            int itemY = this.y + 19 + row * ROW_HEIGHT;
            int markX = this.x + SINGLE_PLUS_X;
            int markY = itemY + MARK_Y_OFFSET + SPRITE_Y_ADJUST;

            context.getMatrices().push();
            context.getMatrices().translate(0.0F, 0.0F, 300.0F); // above the item icons
            RenderSystem.enableBlend();
            context.drawGuiTexture(sprite, markX, markY, width, PLUS_HEIGHT);
            RenderSystem.disableBlend();
            context.getMatrices().pop();
        }
    }
}