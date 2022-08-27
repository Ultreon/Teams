package com.t2pellet.teams.client.core;

import com.t2pellet.teams.TeamsMod;
import com.t2pellet.teams.client.TeamsModClient;
import com.t2pellet.teams.client.ui.menu.TeamsLonelyScreen;
import com.t2pellet.teams.client.ui.menu.TeamsMainScreen;
import com.t2pellet.teams.client.ui.menu.TeamsScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

import java.util.*;
import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
class ClientTeamImpl implements ClientTeam {

    private MinecraftClient client = MinecraftClient.getInstance();
    private Map<UUID, Teammate> teammates = new HashMap<>();
    private Set<UUID> favourites = new HashSet<>();
    private boolean initialized = false;
    private String name = "";
    private boolean hasPerms = false;

    ClientTeamImpl() {
    }

    @Override
    public void init(String name, boolean hasPermissions) {
        if (this.initialized) {
            throw new IllegalArgumentException("Cannot initialize already initialized team. Did you clear it first?");
        }
        this.name = name;
        this.hasPerms = hasPermissions;
        this.initialized = true;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean hasPermissions() {
        return hasPerms;
    }

    @Override
    public boolean isInTeam() {
        return initialized;
    }

    @Override
    public boolean isTeamEmpty() {
        return teammates.size() == 0 || (teammates.size() == 1 && teammates.get(Objects.requireNonNull(TeamsModClient.client.player).getUuid()) != null);
    }

    @Override
    public List<Teammate> getTeammates() {
        return new ArrayList<>(teammates.values());
    }

    public boolean hasPlayer(UUID player) {
        return teammates.containsKey(player);
    }

    @Override
    public void addPlayer(UUID player, String name, Identifier skin, float health, int hunger) {
        teammates.put(player, new Teammate(player, name, skin, health, hunger));
        // Refresh TeamsMainScreen if open
        if (client.currentScreen instanceof TeamsMainScreen) {
            TeamsMainScreen screen = (TeamsMainScreen) client.currentScreen;
            screen.refresh();
        } // Close TeamsScreens if we join a team
        else if (player.equals(Objects.requireNonNull(client.player).getUuid()) && client.currentScreen instanceof TeamsScreen) {
            client.openScreen(null);
        }
    }

    @Override
    public void updatePlayer(UUID player, float health, int hunger) {
        Teammate teammate = teammates.get(player);
        if (teammate != null) {
            teammate.health = health;
            teammate.hunger = hunger;
        } else {
            TeamsMod.LOGGER.warn("Tried updating player with UUID " + player + "but they are not in this clients team");
        }
    }

    @Override
    public void removePlayer(UUID player) {
        teammates.remove(player);
        // Refresh TeamsMainScreen if open, or close it if we were kicked
        if (client.currentScreen instanceof TeamsMainScreen) {
            TeamsMainScreen screen = (TeamsMainScreen) client.currentScreen;
            if (teammates.isEmpty() || player.equals(Objects.requireNonNull(client.player).getUuid())) {
                client.openScreen(screen.parent);
            } else {
                screen.refresh();
            }
        } else if (client.currentScreen instanceof TeamsLonelyScreen) {
            TeamsLonelyScreen screen = (TeamsLonelyScreen) client.currentScreen;
            screen.refresh();
        }
    }

    @Override
    public List<Teammate> getFavourites() {
        return favourites.stream()
                .filter(teammates::containsKey)
                .map(teammates::get)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isFavourite(Teammate player) {
        return favourites.contains(player.id);
    }

    @Override
    public void addFavourite(Teammate player) {
        favourites.add(player.id);
    }

    @Override
    public void removeFavourite(Teammate player) {
        favourites.remove(player.id);
    }

    @Override
    public void reset() {
        teammates.clear();
        name = "";
        hasPerms = false;
        initialized = false;
        // If in TeamsScreen, go to lonely screen
        if (client.currentScreen instanceof TeamsScreen) {
            client.openScreen(new TeamsLonelyScreen(null));
        }
    }

}
