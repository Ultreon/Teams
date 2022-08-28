package com.t2pellet.teams.client.ui.hud;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.t2pellet.teams.TeamsMod;
import com.t2pellet.teams.client.core.ClientTeam;
import com.t2pellet.teams.config.TeamsConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.awt.*;
import java.util.List;
import java.util.Objects;

@OnlyIn(Dist.CLIENT)
public class StatusOverlay extends AbstractGui {

    private static final ResourceLocation ICONS = new ResourceLocation(TeamsMod.MODID, "textures/gui/hudicons.png");

    public boolean enabled = true;
    private final Minecraft client;
    private int offsetY = 0;

    public StatusOverlay() {
        this.client = Minecraft.getInstance();
    }

    public void render(MatrixStack matrices) {
        offsetY = 0;
        RenderSystem.enableBlend();

        List<ClientTeam.Teammate> teammates = ClientTeam.INSTANCE.getTeammates();
        int shown = 0;
        for (int i = 0; i < teammates.size() && shown < 4; ++i) {
            if (Objects.requireNonNull(client.player).getUUID().equals(teammates.get(i).id)) {
                continue;
            }
            renderStatus(matrices, teammates.get(i));
            ++shown;
        }
    }

    private void renderStatus(MatrixStack matrices, ClientTeam.Teammate teammate) {
        if (!TeamsConfig.ENABLE_STATUS_HUD.get() || !enabled) return;

        // Dont render dead players
        if (teammate.getHealth() <= 0) return;
        
        int posX = (int) Math.round(client.getWindow().getGuiScaledWidth() * 0.003);
        int posY = client.getWindow().getGuiScaledHeight() / 4 - 5 + offsetY;

        // Health
        String health = String.valueOf(Math.round(teammate.getHealth()));
        Minecraft.getInstance().getTextureManager().bind(ICONS);
        blit(matrices, posX + 20, posY, 0, 0, 9, 9);
        drawString(matrices, client.font, new StringTextComponent(health), posX + 32, posY, Color.WHITE.getRGB());

        // Hunger
        String hunger = String.valueOf(teammate.getHunger());
        Minecraft.getInstance().getTextureManager().bind(ICONS);
        blit(matrices, posX + 46, posY, 9, 0, 9, 9);
        drawString(matrices, client.font, new StringTextComponent(hunger), posX + 58, posY, Color.WHITE.getRGB());

        // Draw skin
        Minecraft.getInstance().getTextureManager().bind(teammate.skin);
        matrices.pushPose();
        matrices.scale(0.5F, 0.5F, 0.5F);
        blit(matrices, posX + 4, client.getWindow().getGuiScaledHeight() / 2 - 34 + 2 * offsetY, 32, 32, 32, 32);
        matrices.popPose();

        // Draw name
        drawString(matrices, client.font, new StringTextComponent(teammate.name), (int) Math.round(client.getWindow().getGuiScaledWidth() * 0.002) + 20, posY - 15, Color.WHITE.getRGB());

        // Update count & offset
        offsetY += 46;
    }

}
