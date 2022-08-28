package com.t2pellet.teams.client.ui.menu;

import com.t2pellet.teams.client.core.ClientTeamDB;
import com.t2pellet.teams.network.PacketHandler;
import com.t2pellet.teams.network.packets.TeamCreatePacket;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class TeamsCreateScreen extends TeamsInputScreen {

    private static final ITextComponent CREATE_TITLE = new TranslationTextComponent("teams.menu.create.title");
    private static final ITextComponent CREATE_TEXT = new TranslationTextComponent("teams.menu.create.text");

    public TeamsCreateScreen(Screen parent) {
        super(parent, CREATE_TITLE);
    }

    @Override
    protected ITextComponent getSubmitText() {
        return CREATE_TEXT;
    }

    @Override
    protected void onSubmit(Button widget) {
        client.setScreen(new TeamsMainScreen(null));
        PacketHandler.INSTANCE.sendToServer(new TeamCreatePacket(inputField.getValue(), client.player.getUUID()));
    }

    @Override
    protected boolean submitCondition() {
        return !ClientTeamDB.INSTANCE.containsTeam(inputField.getValue());
    }
}
