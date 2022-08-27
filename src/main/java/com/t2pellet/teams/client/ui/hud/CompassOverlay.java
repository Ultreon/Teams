package com.t2pellet.teams.client.ui.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import com.t2pellet.teams.TeamsMod;
import com.t2pellet.teams.client.core.ClientTeam;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Objects;

@Environment(EnvType.CLIENT)
public class CompassOverlay extends DrawableHelper {

    private static final int HUD_WIDTH = 182;
    private static final int HUD_HEIGHT = 5;

    private static final int MIN_DIST = 12;
    private static final int MAX_DIST = 128;
    private static final float MIN_SCALE = 0.2f;
    private static final float MAX_SCALE = 0.4f;
    private static final float MIN_ALPHA = 0.4f;

    public boolean enabled = true;
    private final MinecraftClient client;
    private boolean isShowing = false;

    public CompassOverlay() {
        this.client = MinecraftClient.getInstance();
    }

    public boolean isShowing() {
        return isShowing;
    }

    public void render(MatrixStack matrices) {
        if (!TeamsMod.getConfig().enableCompassHUD || !enabled) {
            isShowing = false;
            return;
        }

        // Render heads
        boolean renderedAnyHead = false;
        float minScale = 1.0F;
        for (ClientTeam.Teammate teammate : ClientTeam.INSTANCE.getTeammates()) {
            if (Objects.requireNonNull(client.player).getUuid().equals(teammate.id)) continue;
            PlayerEntity player = Objects.requireNonNull(client.world).getPlayerByUuid(teammate.id);
            if (player != null) {
                double rotationHead = caculateRotationHead();
                float scaleFactor = calculateScaleFactor(player);
                if (scaleFactor < minScale) minScale = scaleFactor;
                double renderFactor = calculateRenderFactor(player, rotationHead);
                renderHUDHead(matrices, teammate.skin, scaleFactor, renderFactor);
                renderedAnyHead = true;
            }
        }

        // Render bar
        if (ClientTeam.INSTANCE.isInTeam() && !ClientTeam.INSTANCE.isTeamEmpty() && renderedAnyHead) {
            MinecraftClient.getInstance().getTextureManager().bindTexture(GUI_ICONS_TEXTURE);
            int x = (client.getWindow().getScaledWidth() - HUD_WIDTH) / 2;
            int y = 5 + HUD_HEIGHT / 2;
            float alpha = (1 - minScale) * (1 - MIN_ALPHA) + MIN_ALPHA;
            RenderSystem.enableBlend();
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, alpha);
            drawTexture(matrices, x, y, 0, 74, HUD_WIDTH, HUD_HEIGHT);
            RenderSystem.disableBlend();
            isShowing = true;
        } else {
            isShowing = false;
        }
    }

    private double caculateRotationHead() {
        double rotationHead = Objects.requireNonNull(client.player).getHeadYaw() % 360;
        if (rotationHead > 180) {
            rotationHead = rotationHead - 360;
        } else if (rotationHead < -180) {
            rotationHead = 360 + rotationHead;
        }
        return rotationHead;
    }

    private float calculateScaleFactor(PlayerEntity player) {
        double diffPosX = player.getPos().x - Objects.requireNonNull(client.player).getPos().x;
        double diffPosZ = player.getPos().z - client.player.getPos().z;
        double magnitude =  Math.sqrt(diffPosX * diffPosX + diffPosZ * diffPosZ);

        if (magnitude >= MAX_DIST) {
            return 1;
        } else if (magnitude <= MIN_DIST) {
            return 0;
        } else {
            return (float) ((magnitude - MIN_DIST) / (MAX_DIST - MIN_DIST));
        }
    }

    private double calculateRenderFactor(PlayerEntity player, double rotationHead) {
        double diffPosX = player.getPos().x - Objects.requireNonNull(client.player).getPos().x;
        double diffPosZ = player.getPos().z - client.player.getPos().z;
        double magnitude = Math.sqrt(diffPosX * diffPosX + diffPosZ * diffPosZ);
        diffPosX /= magnitude;
        diffPosZ /= magnitude;
        double angle = Math.atan(diffPosZ / diffPosX) * 180 / Math.PI + 90;
        if (diffPosX >= 0) {
            angle -= 180;
        }
        double renderFactor = (angle - rotationHead) / 180;
        if (renderFactor > 1) {
            renderFactor = renderFactor - 2;
        }
        if (renderFactor < -1) {
            renderFactor = 2 + renderFactor;
        }
        return renderFactor;
    }

    private void renderHUDHead(MatrixStack matrices, Identifier skin, float scaleFactor, double renderFactor) {
        MinecraftClient.getInstance().getTextureManager().bindTexture(skin);
        int scaledWidth = client.getWindow().getScaledWidth();
        int x = (int) (scaledWidth / 2 - HUD_WIDTH / 4 + renderFactor * HUD_WIDTH / 2 + 41);
        int y = 5 + HUD_HEIGHT + 4;
        float sizeFactor = scaleFactor * (MAX_SCALE - MIN_SCALE) + MIN_SCALE;
        float alphaFactor = (1 - scaleFactor) * (1 - MIN_ALPHA) + MIN_ALPHA;
        matrices.push();
        RenderSystem.enableBlend();
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, alphaFactor);
        matrices.scale(sizeFactor, sizeFactor, sizeFactor);
        if (1 - Math.abs(renderFactor) < Math.min(alphaFactor, 0.6f)) {
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, (float) (1 - Math.abs(renderFactor)));
            drawTexture(matrices, Math.round(x / sizeFactor), Math.round(y / sizeFactor), 32, 32, 32, 32);
        } else {
            drawTexture(matrices, Math.round(x / sizeFactor), Math.round(y / sizeFactor), 32, 32, 32, 32);
        }
        RenderSystem.disableBlend();
        matrices.pop();
    }



}
