package io.github.darkkronicle.kronhud.mixins;

import io.github.darkkronicle.kronhud.gui.HudManager;
import io.github.darkkronicle.kronhud.gui.hud.*;
import io.github.darkkronicle.kronhud.gui.hud.vanilla.ActionBarHud;
import io.github.darkkronicle.kronhud.gui.hud.vanilla.CrosshairHud;
import io.github.darkkronicle.kronhud.gui.hud.vanilla.ScoreboardHud;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(InGameHud.class)
public class MixinInGameHud {

    @Shadow
    private int overlayRemaining;

    @Inject(method = "renderStatusEffectOverlay", at = @At("HEAD"), cancellable = true)
    public void renderStatusEffect(MatrixStack matrices, CallbackInfo ci) {
        PotionsHud hud = (PotionsHud) HudManager.getInstance().get(PotionsHud.ID);
        if (hud != null && hud.isEnabled()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
    public void renderCrosshair(MatrixStack matrices, CallbackInfo ci) {
        CrosshairHud hud = (CrosshairHud) HudManager.getInstance().get(CrosshairHud.ID);
        if (hud != null && hud.isEnabled()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderScoreboardSidebar", at = @At("HEAD"), cancellable = true)
    public void renderScoreboard(MatrixStack matrices, ScoreboardObjective objective, CallbackInfo ci) {
        ScoreboardHud hud = (ScoreboardHud) HudManager.getInstance().get(ScoreboardHud.ID);
        if (hud != null && hud.isEnabled()) {
            ci.cancel();
        }
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;drawWithShadow(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/text/Text;FFI)I"))
    public int getActionBar(TextRenderer instance, MatrixStack matrices, Text message, float x, float y, int color) {
        ActionBarHud hud = (ActionBarHud) HudManager.getInstance().get(ActionBarHud.ID);
        if (hud != null && hud.isEnabled()) {
            hud.setActionBar(message, color);
            return 0; // Doesn't matter since return value is not used
        } else {
            return instance.drawWithShadow(matrices, message, x, y, color);
        }
    }

    @Inject(method = "setOverlayMessage", at = @At("TAIL"))
    public void setDuration(Text message, boolean tinted, CallbackInfo ci) {
        ActionBarHud hud = (ActionBarHud) HudManager.getInstance().get(ActionBarHud.ID);
        if (hud != null && hud.isEnabled()) {
            overlayRemaining = hud.timeShown.getValue();
        }
    }


}
