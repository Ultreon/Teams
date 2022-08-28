package com.t2pellet.teams.network.packets;

import com.t2pellet.teams.TeamsMod;
import com.t2pellet.teams.core.Team;
import com.t2pellet.teams.core.TeamDB;
import com.t2pellet.teams.network.ServerPacket;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class TeamCreatePacket extends ServerPacket {

    private static final String TEAM_KEY = "teamName";
    private static final String PLAYER_KEY = "playerId";

    public TeamCreatePacket(String team, UUID player) {
        tag.putString(TEAM_KEY, team);
        tag.putUUID(PLAYER_KEY, player);
    }

    public TeamCreatePacket(MinecraftServer server, PacketBuffer byteBuf) {
        super(server, byteBuf);
    }

    @Override
    public void execute(Supplier<NetworkEvent.Context> context) {
        ServerPlayerEntity player = TeamsMod.getServer().getPlayerList().getPlayer(tag.getUUID(PLAYER_KEY));
        try {
            TeamDB.INSTANCE.addTeam(tag.getString(TEAM_KEY), player);
        } catch (Team.TeamException e) {
            TeamsMod.LOGGER.error(e.getMessage());
        }
    }
}
