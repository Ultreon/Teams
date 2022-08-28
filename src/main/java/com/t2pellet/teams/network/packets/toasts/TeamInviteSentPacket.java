package com.t2pellet.teams.network.packets.toasts;

import com.t2pellet.teams.client.TeamsModClient;
import com.t2pellet.teams.client.ui.toast.ToastInviteSent;
import com.t2pellet.teams.network.ClientPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class TeamInviteSentPacket extends ClientPacket {

    private static final String TEAM_KEY = "teamName";
    private static final String NAME_KEY = "playerName";

    public TeamInviteSentPacket(String team, String player) {
        tag.putString(TEAM_KEY, team);
        tag.putString(NAME_KEY, player);
    }

    public TeamInviteSentPacket(Minecraft client, PacketBuffer byteBuf) {
        super(client, byteBuf);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void execute(Supplier<NetworkEvent.Context> context) {
        String team = tag.getString(TEAM_KEY);
        String name = tag.getString(NAME_KEY);
        TeamsModClient.client.getToasts().addToast(new ToastInviteSent(team, name));
    }
}
