package com.t2pellet.teams.client.ui.menu;

import com.t2pellet.teams.client.core.ClientTeam;
import com.t2pellet.teams.client.ui.toast.ToastInviteSent;
import com.t2pellet.teams.network.PacketHandler;
import com.t2pellet.teams.network.packets.TeamInvitePacket;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class TeamsInviteScreen extends TeamsInputScreen {

    private static final ITextComponent TITLE_TEXT = new TranslationTextComponent("teams.menu.invite.title");
    private static final ITextComponent INVITE_TEXT = new TranslationTextComponent("teams.menu.invite.text");


    public TeamsInviteScreen(Screen parent) {
        super(parent, TITLE_TEXT);
    }

    @Override
    protected float getBackgroundScale() {
        return 1.0F;
    }

    @Override
    protected ITextComponent getSubmitText() {
        return INVITE_TEXT;
    }

    @Override
    protected void onSubmit(Button widget) {
        PacketHandler.INSTANCE.sendToServer(new TeamInvitePacket(client.player.getUUID(), inputField.getValue()));
        client.getToasts().addToast(new ToastInviteSent(ClientTeam.INSTANCE.getName(), inputField.getValue()));
        client.setScreen(parent);
    }

    @Override
    protected boolean submitCondition() {
        String clientName = client.player.getName().getString();
        return client.getConnection().getOnlinePlayers()
                .stream()
                .anyMatch(entry -> !entry.getProfile().getName().equals(clientName) && entry.getProfile().getName().equals(inputField.getValue()));
    }
}
