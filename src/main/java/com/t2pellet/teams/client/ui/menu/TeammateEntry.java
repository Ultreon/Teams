package com.t2pellet.teams.client.ui.menu;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.t2pellet.teams.TeamsMod;
import com.t2pellet.teams.client.core.ClientTeam;
import com.t2pellet.teams.network.PacketHandler;
import com.t2pellet.teams.network.packets.TeamKickPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.IRenderable;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TeammateEntry extends AbstractGui implements IRenderable, IGuiEventListener {

    static final int WIDTH = 244;
    static final int HEIGHT = 24;
    private static final ResourceLocation TEXTURE = new ResourceLocation(TeamsMod.MODID, "textures/gui/screen_background.png");

    private ImageButton kickButton;
    private TexturedToggleWidget favButton;
    private Minecraft client;
    private TeamsMainScreen parent;
    private ClientTeam.Teammate teammate;
    private int x;
    private int y;

    public TeammateEntry(TeamsMainScreen parent, ClientTeam.Teammate teammate, int x, int y, boolean local) {
        this.client = Minecraft.getInstance();
        this.parent = parent;
        this.teammate = teammate;
        this.x = x;
        this.y = y;
        if (!local) {
            this.favButton = new TexturedToggleWidget(x + WIDTH - 12, y + 8, 8, 8, 0, 190, TEXTURE, () -> {
                return ClientTeam.INSTANCE.isFavourite(teammate);
            }, button -> {
                if (ClientTeam.INSTANCE.isFavourite(teammate)) {
                    ClientTeam.INSTANCE.removeFavourite(teammate);
                } else {
                    ClientTeam.INSTANCE.addFavourite(teammate);
                }
            });
        }
        if (ClientTeam.INSTANCE.hasPermissions()) {
            this.kickButton = new ImageButton(x + WIDTH - 24, y + 8, 8, 8, 16, 190, 8, TEXTURE, button -> {
                PacketHandler.INSTANCE.sendToServer(new TeamKickPacket(ClientTeam.INSTANCE.getName(), client.player.getUUID(), teammate.id));
                ClientTeam.INSTANCE.removePlayer(teammate.id);
            });
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        RenderSystem.enableDepthTest();
        // Background
        renderBackground(matrices);
        // Head
        float scale = 0.5F;
        matrices.pushPose();
        matrices.scale(scale, scale, scale);
        Minecraft.getInstance().getTextureManager().bind(teammate.skin);
        blit(matrices, (int) ((x + 4) / scale), (int) ((y + 4) / scale), 32, 32, 32, 32);
        matrices.popPose();
        // Nameplate
        client.font.draw(matrices, teammate.name, x + 24, y + 12 - (int) (client.font.lineHeight / 2), 0xff000000);
        // Buttons
        if (favButton != null) {
            favButton.render(matrices, mouseX, mouseY, delta);
        }
        if (kickButton != null) {
            kickButton.render(matrices, mouseX, mouseY, delta);
        }
    }

    private void renderBackground(MatrixStack matrices) {
//        RenderSystem.setShader(GameRenderer::getPositionTexShader);
//        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
        Minecraft.getInstance().getTextureManager().bind(TEXTURE);
        blit(matrices, x, y, 0, 166, WIDTH, HEIGHT);
        RenderSystem.enableBlend();
    }

//    @Override
//    public SelectionType getType() {
//        return SelectionType.FOCUSED;
//    }

    public ImageButton getKickButton() {
        return kickButton;
    }

    public TexturedToggleWidget getFavButton() {
        return favButton;
    }
}
