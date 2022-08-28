package com.t2pellet.teams.network.packets.toasts;

import com.t2pellet.teams.client.TeamsModClient;
import com.t2pellet.teams.client.ui.toast.ToastInvited;
import com.t2pellet.teams.core.Team;
import com.t2pellet.teams.network.ClientPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class TeamInvitedPacket extends ClientPacket {

    private static final String TEAM_KEY = "teamName";

    public TeamInvitedPacket(Team team) {
        tag.putString(TEAM_KEY, team.getName());
    }

    public TeamInvitedPacket(Minecraft client, PacketBuffer byteBuf) {
        super(client, byteBuf);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void execute(Supplier<NetworkEvent.Context> context) {
        String team = tag.getString(TEAM_KEY);
        TeamsModClient.client.getToasts().addToast(new ToastInvited(team));
    }
}
