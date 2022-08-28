package com.t2pellet.teams.network.packets;

import com.t2pellet.teams.TeamsMod;
import com.t2pellet.teams.core.Team;
import com.t2pellet.teams.core.TeamDB;
import com.t2pellet.teams.network.PacketHandler;
import com.t2pellet.teams.network.ServerPacket;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

public class TeamRequestPacket extends ServerPacket {

    private static final String TEAM_KEY = "teamName";
    private static final String PLAYER_KEY = "playerId";

    public TeamRequestPacket(String team, UUID player) {
        tag.putString(TEAM_KEY, team);
        tag.putUUID(PLAYER_KEY, player);
    }

    public TeamRequestPacket(MinecraftServer server, PacketBuffer byteBuf) {
        super(server, byteBuf);
    }

    @Override
    public void execute(Supplier<NetworkEvent.Context> context) {
        String name = tag.getString(TEAM_KEY);
        Team team = TeamDB.INSTANCE.getTeam(name);
        if (team == null) {
            throw new IllegalArgumentException("Got request to join team " + name + ", but that team doesn't exist");
        } else {
            // Get first online player in list of seniority
            PlayerList playerManager = TeamsMod.getServer().getPlayerList();
            ServerPlayerEntity seniorPlayer = team.getPlayerUUIDs()
                    .filter(p -> playerManager.getPlayer(p) != null)
                    .map(playerManager::getPlayer)
                    .filter(Objects::nonNull)
                    .findFirst().orElseThrow(NoSuchElementException::new);
            PacketHandler.INSTANCE.sendTo(new TeamRequestedPacket(name, tag.getUUID(PLAYER_KEY)), seniorPlayer);
        }
    }
}
