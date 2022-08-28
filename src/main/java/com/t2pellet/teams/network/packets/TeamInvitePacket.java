package com.t2pellet.teams.network.packets;

import com.t2pellet.teams.TeamsMod;
import com.t2pellet.teams.core.IHasTeam;
import com.t2pellet.teams.core.Team;
import com.t2pellet.teams.core.TeamDB;
import com.t2pellet.teams.network.ServerPacket;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

public class TeamInvitePacket extends ServerPacket {

    private static final String FROM_KEY = "fromId";
    private static final String TO_KEY = "toId";

    public TeamInvitePacket(UUID from, String to) {
        tag.putUUID(FROM_KEY, from);
        tag.putString(TO_KEY, to);
    }

    public TeamInvitePacket(MinecraftServer server, PacketBuffer byteBuf) {
        super(server, byteBuf);
    }

    @Override
    public void execute(Supplier<NetworkEvent.Context> context) {
        UUID from = tag.getUUID(FROM_KEY);
        UUID to = Optional.ofNullable(TeamsMod.getServer().getProfileCache().get(tag.getString(TO_KEY))).orElseThrow(NoSuchElementException::new).getId();

        ServerPlayerEntity fromPlayer = TeamsMod.getServer().getPlayerList().getPlayer(from);
        ServerPlayerEntity toPlayer = TeamsMod.getServer().getPlayerList().getPlayer(to);

        Objects.requireNonNull(fromPlayer, "Origin player doesn't exists.");
        Objects.requireNonNull(toPlayer, "Destination player doesn't exists.");

        Team team = ((IHasTeam) fromPlayer).getTeam();
        if (team == null) {
            TeamsMod.LOGGER.error(fromPlayer.getName().getString() + " tried inviting " + toPlayer.getName().getString() + " but they are not in a team..");
        } else {
            try {
                TeamDB.INSTANCE.invitePlayerToTeam(toPlayer, team);
            } catch (Team.TeamException e) {
                TeamsMod.LOGGER.error(e.getMessage());
            }
        }
    }
}
