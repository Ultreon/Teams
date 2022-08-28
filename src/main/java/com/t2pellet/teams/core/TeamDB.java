package com.t2pellet.teams.core;

import com.t2pellet.teams.TeamsMod;
import com.t2pellet.teams.network.PacketHandler;
import com.t2pellet.teams.network.packets.TeamDataPacket;
import com.t2pellet.teams.network.packets.toasts.TeamInvitedPacket;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.Constants;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@SuppressWarnings("UnusedReturnValue")
public class TeamDB {

    public static final TeamDB INSTANCE = new TeamDB();
    private static final String TEAMS_KEY = "teams";

    private final Map<String, Team> teams = new HashMap<>();

    private TeamDB() {
    }

    public Stream<Team> getTeams() {
        return teams.values().stream();
    }

    public void addTeam(Team team) throws Team.TeamException {
        if (teams.containsKey(team.getName())) {
            throw new Team.TeamException(new TranslationTextComponent("teams.error.duplicateteam"));
        }
        teams.put(team.getName(), team);
        net.minecraft.entity.player.ServerPlayerEntity[] players = TeamsMod.getServer().getPlayerList().getPlayers().toArray(new ServerPlayerEntity[]{});
        PacketHandler.INSTANCE.sendTo(new TeamDataPacket(TeamDataPacket.Type.ADD, team.name), players);
    }

    public Team addTeam(String name, @org.jetbrains.annotations.Nullable ServerPlayerEntity creator) throws Team.TeamException {
        if (creator != null && ((IHasTeam) creator).hasTeam()) {
            throw new Team.TeamException(new TranslationTextComponent("teams.error.alreadyinteam", creator.getName().getString()));
        }
        Team team = new Team.Builder(name).complete();
        addTeam(team);
        if (creator != null) {
            team.addPlayer(creator);
        }
        ServerPlayerEntity[] players = TeamsMod.getServer().getPlayerList().getPlayers().toArray(new ServerPlayerEntity[]{});
        PacketHandler.INSTANCE.sendTo(new TeamDataPacket(TeamDataPacket.Type.ONLINE, team.name), players);
        return team;
    }

    public void removeTeam(Team team) {
        teams.remove(team.getName());
        TeamsMod.getScoreboard().removePlayerTeam(TeamsMod.getScoreboard().getPlayerTeam(team.getName()));
        team.clear();
        ServerPlayerEntity[] players = TeamsMod.getServer().getPlayerList().getPlayers().toArray(new ServerPlayerEntity[]{});
        PacketHandler.INSTANCE.sendTo(new TeamDataPacket(TeamDataPacket.Type.REMOVE, team.name), players);
    }

    public boolean isEmpty() {
        return teams.isEmpty();
    }

    public boolean hasTeam(String team) {
        return teams.containsKey(team);
    }

    public Team getTeam(ServerPlayerEntity player) {
        return ((IHasTeam) player).getTeam();
    }

    public Team getTeam(String name) {
        return teams.get(name);
    }

    public void invitePlayerToTeam(ServerPlayerEntity player, Team team) throws Team.TeamException {
        if (((IHasTeam) player).hasTeam()) {
            throw new Team.TeamException(new TranslationTextComponent("teams.error.alreadyinteam", player.getName().getString()));
        }
        PacketHandler.INSTANCE.sendTo(new TeamInvitedPacket(team), player);
    }

    public void addPlayerToTeam(ServerPlayerEntity player, Team team) throws Team.TeamException {
        if (((IHasTeam) player).hasTeam()) {
            throw new Team.TeamException(new TranslationTextComponent("teams.error.alreadyinteam", player.getName()));
        }
        team.addPlayer(player);
    }

    public void removePlayerFromTeam(ServerPlayerEntity player) throws Team.TeamException {
        Team playerTeam = ((IHasTeam) player).getTeam();
        if (playerTeam == null) {
            throw new Team.TeamException(new TranslationTextComponent("teams.error.notinteam", player.getName().getString()));
        }
        playerTeam.removePlayer(player);
        if (playerTeam.isEmpty()) {
            removeTeam(playerTeam);
        }
    }

    public void fromNBT(CompoundNBT compound) {
        teams.clear();
        ListNBT list = compound.getList(TEAMS_KEY, Constants.NBT.TAG_COMPOUND);
        for (net.minecraft.nbt.INBT tag : list) {
            try {
                addTeam(Team.fromNBT((CompoundNBT) tag));
            } catch (Team.TeamException ex) {
                TeamsMod.LOGGER.error("Failed to load team from NBT" + ex.getMessage());
            }
        }
    }

    public CompoundNBT toNBT() {
        CompoundNBT compound = new CompoundNBT();
        ListNBT list = new ListNBT();
        for (Team team : teams.values()) {
            list.add(team.toNBT());
        }
        compound.put(TEAMS_KEY, list);
        return compound;
    }
}
