package com.t2pellet.teams.core;

import com.t2pellet.teams.TeamsMod;
import com.t2pellet.teams.events.AdvancementEvents;
import com.t2pellet.teams.events.PlayerUpdateEvents;
import com.t2pellet.teams.network.PacketHandler;
import com.t2pellet.teams.network.packets.TeamDataPacket;
import com.t2pellet.teams.network.packets.TeamPlayerDataPacket;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TeamsMod.MODID)
public class EventHandlers {

    private EventHandlers() {
    }

    @SubscribeEvent
    public static void playerConnect(PlayerEvent.PlayerLoggedInEvent event) {
        // Mark online
        if (event.getPlayer() instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = ((ServerPlayerEntity) event.getPlayer());
            Team team = TeamDB.INSTANCE.getTeam(player);
            if (team != null) {
                team.playerOnline(player, true);
            }
            // Send packets
            String[] teams = TeamDB.INSTANCE.getTeams().map(t -> t.name).toArray(String[]::new);
            String[] onlineTeams = TeamDB.INSTANCE.getTeams().filter(t -> t.getOnlinePlayers().findAny().isPresent()).map(t -> t.name).toArray(String[]::new);
            PacketHandler.INSTANCE.sendTo(new TeamDataPacket(TeamDataPacket.Type.ADD, teams), player);
            PacketHandler.INSTANCE.sendTo(new TeamDataPacket(TeamDataPacket.Type.ONLINE, onlineTeams), player);
        }
    }

    @SubscribeEvent
    public static void playerDisconnect(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getPlayer() instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
            Team team = TeamDB.INSTANCE.getTeam(player);
            if (team != null) {
                team.playerOffline(player, true);
            }
        }
    }

    @SubscribeEvent
    public static void playerHealthUpdate(PlayerUpdateEvents.PlayerHealthUpdate event) {
        Team team = TeamDB.INSTANCE.getTeam(event.player);
        if (team != null) {
            ServerPlayerEntity[] players = team.getOnlinePlayers().filter(other -> !other.equals(event.player)).toArray(ServerPlayerEntity[]::new);
            PacketHandler.INSTANCE.sendTo(new TeamPlayerDataPacket(event.player, TeamPlayerDataPacket.Type.UPDATE), players);
        }
    }

    @SubscribeEvent
    public static void playerCopy(PlayerUpdateEvents.PlayerCopy event) {
        Team team = TeamDB.INSTANCE.getTeam(event.oldPlayer);
        if (team != null) {
            team.playerOffline(event.oldPlayer, false);
            team.playerOnline(event.newPlayer, false);
        }
    }

    @SubscribeEvent
    public static void playerAdvancement(AdvancementEvents.PlayerAdvancement event) {
        Team team = TeamDB.INSTANCE.getTeam(event.player);
        if (team != null) {
            team.addAdvancement(event.advancement);
        }
    }

}
