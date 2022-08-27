package com.t2pellet.teams.client.ui.toast;

import com.mojang.blaze3d.systems.RenderSystem;
import com.t2pellet.teams.TeamsMod;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.*;

public abstract class TeamToast implements Toast {

    public final String team;
    private boolean firstDraw = true;
    private long firstDrawTime;

    public TeamToast(String team) {
        this.team = team;
    }

    public abstract String title();

    public abstract String subTitle();

    @Override
    public Visibility draw(MatrixStack matrices, ToastManager manager, long startTime) {
        if (firstDraw) {
            firstDrawTime = startTime;
            firstDraw = false;
        }

//        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        manager.getGame().getTextureManager().bindTexture(TEXTURE);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        manager.drawTexture(matrices, 0, 0, 0, 64, this.getWidth(), this.getHeight());
        manager.getGame().textRenderer.draw(matrices, title(), 22, 7, Color.WHITE.getRGB());
        manager.getGame().textRenderer.draw(matrices, subTitle(), 22, 18, 0xff000000);

        return startTime - firstDrawTime < TeamsMod.getConfig().toastDuration * 1000L && team != null ? Visibility.SHOW : Visibility.HIDE;    }
}
