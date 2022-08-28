package com.t2pellet.teams.client.ui.menu;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class TexturedToggleWidget extends Button {

    private final ResourceLocation texture;
    private final int u;
    private final int v;
    private final int hoveredVOffset;
    private final int textureWidth;
    private final int textureHeight;
    private final ToggleCondition condition;

    public TexturedToggleWidget(int x, int y, int width, int height, int u, int v, ResourceLocation texture, ToggleCondition condition, IPressable pressAction) {
        this(x, y, width, height, u, v, height, texture, 256, 256, condition, pressAction);
    }

    public TexturedToggleWidget(int x, int y, int width, int height, int u, int v, int hoveredVOffset, ResourceLocation texture, ToggleCondition condition, IPressable pressAction) {
        this(x, y, width, height, u, v, hoveredVOffset, texture, 256, 256, condition, pressAction);
    }

    public TexturedToggleWidget(int x, int y, int width, int height, int u, int v, int hoveredVOffset, ResourceLocation texture, int textureWidth, int textureHeight, ToggleCondition condition, IPressable pressAction) {
        this(x, y, width, height, u, v, hoveredVOffset, texture, textureWidth, textureHeight, condition, pressAction, StringTextComponent.EMPTY);
    }

    public TexturedToggleWidget(int x, int y, int width, int height, int u, int v, int hoveredVOffset, ResourceLocation texture, int textureWidth, int textureHeight, ToggleCondition condition, IPressable pressAction, ITextComponent text) {
        this(x, y, width, height, u, v, hoveredVOffset, texture, textureWidth, textureHeight, condition, pressAction, NO_TOOLTIP, text);
    }

    public TexturedToggleWidget(int x, int y, int width, int height, int u, int v, int hoveredVOffset, ResourceLocation texture, int textureWidth, int textureHeight, ToggleCondition condition, IPressable pressAction, ITooltip tooltipSupplier, ITextComponent text) {
        super(x, y, width, height, text, pressAction, tooltipSupplier);
        this.condition = condition;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.u = u;
        this.v = v;
        this.hoveredVOffset = hoveredVOffset;
        this.texture = texture;
    }

    public void renderButton(@NotNull MatrixStack matrices, int mouseX, int mouseY, float delta) {
//        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        Minecraft.getInstance().getTextureManager().bind(this.texture);
        int i = this.v;
        if (this.isHovered()) {
            i += this.hoveredVOffset;
        }
        int j = this.u;
        if (condition.isOn()) {
            j += this.width;
        }

        RenderSystem.enableDepthTest();
        blit(matrices, this.x, this.y, (float)j, (float)i, this.width, this.height, this.textureWidth, this.textureHeight);
        if (this.isHovered) {
            this.renderToolTip(matrices, mouseX, mouseY);
        }
    }

    @FunctionalInterface
    public interface ToggleCondition {
        boolean isOn();
    }
}
