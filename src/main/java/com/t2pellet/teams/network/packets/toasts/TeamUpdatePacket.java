package com.t2pellet.teams.network.packets.toasts;

import com.t2pellet.teams.client.TeamsModClient;
import com.t2pellet.teams.client.ui.toast.ToastJoin;
import com.t2pellet.teams.client.ui.toast.ToastLeave;
import com.t2pellet.teams.network.ClientPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class TeamUpdatePacket extends ClientPacket {

    public enum Action {
        JOINED,
        LEFT
    }

    private static final String TEAM_KEY = "teamName";
    private static final String PLAYER_KEY = "playerName";
    private static final String ACTION_KEY = "action";
    private static final String LOCAL_KEY = "local";

    public TeamUpdatePacket(String team, String player, Action action, boolean isLocal) {
        tag.putString(TEAM_KEY, team);
        tag.putString(PLAYER_KEY, player);
        tag.putString(ACTION_KEY, action.name());
        tag.putBoolean(LOCAL_KEY, isLocal);
    }

    public TeamUpdatePacket(Minecraft client, PacketBuffer byteBuf) {
        super(client, byteBuf);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void execute(Supplier<NetworkEvent.Context> context) {
        String team = tag.getString(TEAM_KEY);
        String player = tag.getString(PLAYER_KEY);
        Action action = Action.valueOf(tag.getString(ACTION_KEY));
        boolean isLocal = tag.getBoolean(LOCAL_KEY);

        switch (action) {
            case JOINED:
                TeamsModClient.client.getToasts().addToast(new ToastJoin(team, player, isLocal));
                break;
            case LEFT:
                TeamsModClient.client.getToasts().addToast(new ToastLeave(team, player, isLocal));
                break;
        }
    }
}
