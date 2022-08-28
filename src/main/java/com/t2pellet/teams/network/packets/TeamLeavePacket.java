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

public class TeamLeavePacket extends ServerPacket {

    private static final String PLAYER_KEY = "playerId";

    public TeamLeavePacket(UUID player) {
        tag.putUUID(PLAYER_KEY, player);
    }

    public TeamLeavePacket(MinecraftServer server, PacketBuffer byteBuf) {
        super(server, byteBuf);
    }

    @Override
    public void execute(Supplier<NetworkEvent.Context> context) {
        ServerPlayerEntity player = TeamsMod.getServer().getPlayerList().getPlayer(tag.getUUID(PLAYER_KEY));
        try {
            TeamDB.INSTANCE.removePlayerFromTeam(player);
        } catch (Team.TeamException ex) {
            TeamsMod.LOGGER.error(ex.getMessage());
        }
    }
}
