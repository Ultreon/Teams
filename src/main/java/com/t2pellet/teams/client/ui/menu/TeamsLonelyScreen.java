package com.t2pellet.teams.client.ui.menu;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.t2pellet.teams.TeamsMod;
import com.t2pellet.teams.client.core.ClientTeamDB;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TeamsLonelyScreen extends TeamsScreen {

    static final int WIDTH = 256;
    static final int HEIGHT = 166;
    private static final ResourceLocation TEXTURE = new ResourceLocation(TeamsMod.MODID, "textures/gui/screen_background.png");
    private static final ITextComponent CREATE_TEXT = new TranslationTextComponent("teams.menu.create");
    private static final ITextComponent LONELY_TEXT = new TranslationTextComponent("teams.menu.lonely.alone");
    private static final ITextComponent GO_BACK_TEXT = new TranslationTextComponent("teams.menu.return");

    private final List<TeamEntry> entries = new ArrayList<>();

    public TeamsLonelyScreen(Screen parent) {
        super(parent, new TranslationTextComponent("teams.menu.lonely.title"));
    }

    @Override
    protected void init() {
        super.init();
        // Team Entries
        int yPos = y + 12;
        for (String team : ClientTeamDB.INSTANCE.getOnlineTeams()) {
            TeamEntry entry = new TeamEntry(team, this.width / 2 - 122, yPos);
            addWidget(entry);
            addButton(entry.joinButton);
            yPos += 24;
            entries.add(entry);
       }
        // Menu buttons
        addButton(new Button(this.width / 2 - 106, y + HEIGHT - 30, 100, 20, CREATE_TEXT, button -> {
            client.setScreen(new TeamsCreateScreen(this));
        }));
        addButton(new Button(this.width / 2 + 6, y + HEIGHT - 30, 100, 20, GO_BACK_TEXT, button -> {
            client.setScreen(parent);
        }));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);
        if (ClientTeamDB.INSTANCE.getOnlineTeams().isEmpty()) {
            int textWidth = font.width(LONELY_TEXT);
            int textHeight   = font.lineHeight;
            font.draw(matrices, LONELY_TEXT, (int) ((this.width - textWidth) / 2), y + 24 - (int) (textHeight / 2), Color.BLACK.getRGB());
        }
        for (TeamEntry entry : entries) {
            entry.render(matrices, mouseX, mouseY, delta);
        }
    }

    public void refresh() {
        client.setScreen(new TeamsLonelyScreen(parent));
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
        return 1.0F;
    }
}
