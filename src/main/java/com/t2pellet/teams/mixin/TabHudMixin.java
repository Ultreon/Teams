package com.t2pellet.teams.mixin;

import com.t2pellet.teams.client.TeamsModClient;
import net.minecraft.client.gui.overlay.PlayerTabOverlayGui;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@OnlyIn(Dist.CLIENT)
@Mixin(PlayerTabOverlayGui.class)
public class TabHudMixin {

    @ModifyVariable(method = "render", at = @At("STORE"), ordinal = 9)
    private int onRenderTabList(int p) {
        if (TeamsModClient.compass.isShowing()) {
            float scaledHeight = TeamsModClient.client.getWindow().getGuiScaledHeight();
            return (int) (scaledHeight * 0.01) + 12 + 16;
        }
        return p;
    }

}
