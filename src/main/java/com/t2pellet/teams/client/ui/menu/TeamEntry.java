package com.t2pellet.teams.client.ui.menu;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.t2pellet.teams.TeamsMod;
import com.t2pellet.teams.client.ui.toast.ToastRequest;
import com.t2pellet.teams.network.PacketHandler;
import com.t2pellet.teams.network.packets.TeamRequestPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.IRenderable;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.util.ResourceLocation;

public class TeamEntry extends AbstractGui implements IRenderable, IGuiEventListener {

    static final int WIDTH = 244;
    static final int HEIGHT = 24;
    private static final ResourceLocation TEXTURE = new ResourceLocation(TeamsMod.MODID, "textures/gui/screen_background.png");

    public final ImageButton joinButton;
    private Minecraft client;
    private String team;
    private int x;
    private int y;

    public TeamEntry(String team, int x, int y) {
        this.client = Minecraft.getInstance();
        this.team = team;
        this.x = x;
        this.y = y;
        this.joinButton = new ImageButton(x + WIDTH - 24, y + 8, 8, 8, 24, 190, 8, TEXTURE, button -> {
            PacketHandler.INSTANCE.sendToServer(new TeamRequestPacket(team, client.player.getUUID()));
            client.getToasts().addToast(new ToastRequest(team));
            client.setScreen(null);
        });
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        RenderSystem.enableDepthTest();
        // Background
        renderBackground(matrices);
        // Name
        client.font.draw(matrices, team, x + 8, y + 12 - (int) (client.font.lineHeight / 2), 0xff000000);
        // Buttons
        joinButton.render(matrices, mouseX, mouseY, delta);
    }

    private void renderBackground(MatrixStack matrices) {
//        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.enableTexture();
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getInstance().getTextureManager().bind(TEXTURE);
        blit(matrices, x, y, 0, 166, WIDTH, HEIGHT);
    }
}
