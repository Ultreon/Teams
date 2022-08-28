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

public class TeamKickPacket extends ServerPacket {

    private static final String TEAM_KEY = "teamName";
    private static final String SENDER_KEY = "senderId";
    private static final String KICKED_KEY = "kickedId";

    public TeamKickPacket(String team, UUID sender, UUID playerToKick) {
        tag.putString(TEAM_KEY, team);
        tag.putUUID(SENDER_KEY, sender);
        tag.putUUID(KICKED_KEY, playerToKick);
    }

    public TeamKickPacket(MinecraftServer server, PacketBuffer byteBuf) {
        super(server, byteBuf);
    }

    @Override
    public void execute(Supplier<NetworkEvent.Context> context) {
        Team team = TeamDB.INSTANCE.getTeam(tag.getString(TEAM_KEY));
        ServerPlayerEntity sender = TeamsMod.getServer().getPlayerList().getPlayer(tag.getUUID(SENDER_KEY));
        if (sender != null && team.playerHasPermissions(sender)) {
            ServerPlayerEntity kicked = TeamsMod.getServer().getPlayerList().getPlayer(tag.getUUID(KICKED_KEY));
            try {
                TeamDB.INSTANCE.removePlayerFromTeam(kicked);
            } catch (Team.TeamException ex) {
                TeamsMod.LOGGER.error(ex.getMessage());
            }
        } else {
            TeamsMod.LOGGER.error("Received packet to kick player, but the sender did not have permissions");
        }
    }
}
