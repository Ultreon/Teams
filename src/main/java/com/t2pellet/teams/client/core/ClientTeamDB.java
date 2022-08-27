package com.t2pellet.teams.client.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ClientTeamDB {

    public static ClientTeamDB INSTANCE = new ClientTeamDB();

    private Set<String> teams;
    private Set<String> onlineTeams;

    private ClientTeamDB() {
        teams = new HashSet<>();
        onlineTeams = new HashSet<>();
    }

    public List<String> getTeams() {
        return new ArrayList<>(teams);
    }

    public List<String> getOnlineTeams() {
        return new ArrayList<>(onlineTeams);
    }

    public void addTeam(String team) {
        teams.add(team);
    }

    public void removeTeam(String team) {
        teams.remove(team);
        teamOffline(team);
    }

    public boolean containsTeam(String team) {
        return teams.contains(team);
    }

    public void teamOnline(String team) {
        onlineTeams.add(team);
    }

    public void teamOffline(String team) {
        onlineTeams.remove(team);
    }

    public boolean containsOnlineTeam(String team) {
        return onlineTeams.contains(team);
    }

    public void clear() {
        teams.clear();
        onlineTeams.clear();
    }

}
