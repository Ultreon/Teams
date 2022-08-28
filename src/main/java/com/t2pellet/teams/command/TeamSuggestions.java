package com.t2pellet.teams.command;

import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.t2pellet.teams.core.Team;
import com.t2pellet.teams.core.TeamDB;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.SuggestionProviders;
import net.minecraft.util.ResourceLocation;

import java.util.stream.Stream;

public class TeamSuggestions {

    private TeamSuggestions() {
    }

    static final SuggestionProvider<CommandSource> TEAMS = SuggestionProviders.register(new ResourceLocation("teams"), (context, builder) -> {
        Stream<Team> teams = TeamDB.INSTANCE.getTeams();
        teams.forEach(team -> {
            builder.suggest(team.getName());
        });
        return builder.buildFuture();
    });

}
