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

public class TeamJoinPacket extends ServerPacket {

    private static final String ID_KEY = "playerId";
    private static final String TEAM_KEY = "teamName";

    public TeamJoinPacket(UUID playerId, String team) {
        tag.putUUID(ID_KEY, playerId);
        tag.putString(TEAM_KEY, team);
    }

    public TeamJoinPacket(MinecraftServer server, PacketBuffer byteBuf) {
        super(server, byteBuf);
    }

    @Override
    public void execute(Supplier<NetworkEvent.Context> context) {
        UUID id = tag.getUUID(ID_KEY);
        ServerPlayerEntity player = TeamsMod.getServer().getPlayerList().getPlayer(id);
        String teamName = tag.getString(TEAM_KEY);
        Team team = TeamDB.INSTANCE.getTeam(teamName);
        try {
            TeamDB.INSTANCE.addPlayerToTeam(player, team);
        } catch (Team.TeamException ex) {
            TeamsMod.LOGGER.error("Failed to join team: " + teamName);
            TeamsMod.LOGGER.error(ex.getMessage());
        }
    }
}
