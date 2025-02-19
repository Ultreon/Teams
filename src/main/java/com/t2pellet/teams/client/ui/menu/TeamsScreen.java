package com.t2pellet.teams.client.ui.menu;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.t2pellet.teams.client.TeamsModClient;
import com.t2pellet.teams.client.core.ClientTeam;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import org.jetbrains.annotations.NotNull;

public abstract class TeamsScreen extends Screen {

    public final Screen parent;
    protected final Minecraft client;
    protected int x;
    protected int y;
    protected boolean inTeam;

    public TeamsScreen(Screen parent, ITextComponent title) {
        super(title);
        client = TeamsModClient.client;
        this.parent = parent;
        inTeam = ClientTeam.INSTANCE.isInTeam();
    }

    @Override
    protected void init() {
        x = (width - getWidth()) / 2;
        y = (height - getHeight()) / 2;
    }

    @Override
    public void render(@NotNull MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
//        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getInstance().getTextureManager().bind(getBackgroundTexture());
        matrices.pushPose();
        matrices.scale(getBackgroundScale(), getBackgroundScale(), getBackgroundScale());
        blit(matrices, (int) (x / getBackgroundScale()), (int) (y / getBackgroundScale()), 0, 0, (int) (getWidth() / getBackgroundScale()), (int) (getHeight() / getBackgroundScale()));
        matrices.popPose();
        super.render(matrices, mouseX, mouseY, delta);
    }

    protected abstract int getWidth();

    protected abstract int getHeight();

    protected abstract ResourceLocation getBackgroundTexture();

    protected abstract float getBackgroundScale();

}
