package com.t2pellet.teams.client.ui.toast;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.t2pellet.teams.config.TeamsConfig;
import net.minecraft.client.gui.toasts.IToast;
import net.minecraft.client.gui.toasts.ToastGui;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public abstract class TeamToast implements IToast {

    public final String team;
    private boolean firstDraw = true;
    private long firstDrawTime;

    public TeamToast(String team) {
        this.team = team;
    }

    public abstract String title();

    public abstract String subTitle();

    @NotNull
    @Override
    public Visibility render(@NotNull MatrixStack matrices, @NotNull ToastGui manager, long startTime) {
        if (firstDraw) {
            firstDrawTime = startTime;
            firstDraw = false;
        }

//        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        manager.getMinecraft().getTextureManager().bind(TEXTURE);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        manager.blit(matrices, 0, 0, 0, 64, this.width(), this.height());
        manager.getMinecraft().font.draw(matrices, title(), 22, 7, Color.WHITE.getRGB());
        manager.getMinecraft().font.draw(matrices, subTitle(), 22, 18, 0xff000000);

        return startTime - firstDrawTime < TeamsConfig.TOAST_DURATION.get() * 1000L && team != null ? Visibility.SHOW : Visibility.HIDE;    }
}
