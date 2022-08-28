package com.t2pellet.teams.client.ui.menu;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.t2pellet.teams.TeamsMod;
import com.t2pellet.teams.client.core.ClientTeam;
import com.t2pellet.teams.network.PacketHandler;
import com.t2pellet.teams.network.packets.TeamLeavePacket;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@OnlyIn(Dist.CLIENT)
public class TeamsMainScreen extends TeamsScreen {

    static final int WIDTH = 267;
    static final int HEIGHT = 183;
    private static final ResourceLocation TEXTURE = new ResourceLocation(TeamsMod.MODID, "textures/gui/screen_background.png");
    private static final ITextComponent INVITE_TEXT = new TranslationTextComponent("teams.menu.invite");
    private static final ITextComponent LEAVE_TEXT = new TranslationTextComponent("teams.menu.leave");
    private static final ITextComponent GO_BACK_TEXT = new TranslationTextComponent("teams.menu.return");
    private final List<TeammateEntry> entries = new ArrayList<>();

    public TeamsMainScreen(Screen parent) {
        super(parent, new TranslationTextComponent("teams.menu.title"));
    }

    @Override
    protected void init() {
        super.init();
        int yPos = y + 12;
        int xPos = x + (WIDTH - TeammateEntry.WIDTH) / 2;
        // Add player buttons
        for (ClientTeam.Teammate teammate : ClientTeam.INSTANCE.getTeammates()) {
            boolean local = Objects.requireNonNull(client.player).getUUID().equals(teammate.id);
            TeammateEntry entry = new TeammateEntry(this, teammate, xPos, yPos, local);
            addWidget(entry);
            if (entry.getFavButton() != null) {
                addButton(entry.getFavButton());
            }
            if (entry.getKickButton() != null) {
                addButton(entry.getKickButton());
            }
            entries.add(entry);
            yPos += 24;
        }
        // Add menu buttons
        addButton(new Button(this.width / 2  - 125, y + HEIGHT - 30, 80, 20, LEAVE_TEXT, button -> {
            PacketHandler.INSTANCE.sendToServer(new TeamLeavePacket(Objects.requireNonNull(client.player).getUUID()));
            client.setScreen(new TeamsLonelyScreen(parent));
        }));
        addButton(new Button(this.width / 2  - 40, y + HEIGHT - 30, 80, 20, INVITE_TEXT, button -> {
            client.setScreen(new TeamsInviteScreen(this));
        }));
        addButton(new Button(this.width / 2  + 45, y + HEIGHT - 30, 80, 20, GO_BACK_TEXT, button -> {
            client.setScreen(parent);
        }));
    }

    @Override
    public void render(@NotNull MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);

        for (TeammateEntry entry : entries) {
            entry.render(matrices, mouseX, mouseY, delta);
        }
    }

    @Override
    protected int getWidth() {
        return WIDTH;
    }

    @Override
    protected int getHeight() {
        return HEIGHT;
    }

    @Override
    protected ResourceLocation getBackgroundTexture() {
        return TEXTURE;
    }

    @Override
    protected float getBackgroundScale() {
        return 1.1F;
    }

    public void refresh() {
        if (!ClientTeam.INSTANCE.isInTeam()) {
            client.setScreen(parent);
        } else {
            client.setScreen(new TeamsMainScreen(parent));
        }
    }

}
