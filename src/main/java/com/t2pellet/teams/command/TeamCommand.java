package com.t2pellet.teams.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.t2pellet.teams.core.IHasTeam;
import com.t2pellet.teams.core.Team;
import com.t2pellet.teams.core.TeamDB;
import com.t2pellet.teams.network.PacketHandler;
import com.t2pellet.teams.network.packets.toasts.TeamInviteSentPacket;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import static net.minecraft.command.Commands.argument;
import static net.minecraft.command.Commands.literal;

public class TeamCommand {

    private TeamCommand() {
    }

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(literal("teams")
                .then(literal("create")
                        .then(argument("name", StringArgumentType.string())
                                .executes(TeamCommand::createTeam)))
                .then(literal("invite")
                        .then(argument("player", EntityArgument.player())
                                .executes(TeamCommand::invitePlayer)))
                .then(literal("leave")
                        .executes(TeamCommand::leaveTeam))
                .then(literal("kick")
                        .then(argument("player", EntityArgument.player())
                            .requires(source -> source.hasPermission(2))
                            .executes(TeamCommand::kickPlayer)))
                .then(literal("remove")
                        .then(argument("name", StringArgumentType.string())
                            .requires(source -> source.hasPermission(3))
                            .suggests(TeamSuggestions.TEAMS)
                            .executes(TeamCommand::removeTeam)))
                .then(literal("info")
                        .then(argument("name", StringArgumentType.string())
                                .suggests(TeamSuggestions.TEAMS)
                                .executes(TeamCommand::getTeamInfo)))
                .then(literal("list")
                        .executes(TeamCommand::listTeams)));

    }

    private static int createTeam(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
        String name = ctx.getArgument("name", String.class);
        ServerPlayerEntity player = ctx.getSource().getPlayerOrException();
        try {
            TeamDB.INSTANCE.addTeam(name, player);
        } catch (Team.TeamException e) {
            throw new SimpleCommandExceptionType(new LiteralMessage(e.getMessage())).create();
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int invitePlayer(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrException();
        ServerPlayerEntity newPlayer = EntityArgument.getPlayer(ctx, "player");
        Team team = ((IHasTeam) player).getTeam();
        if (team == null) {
            throw new SimpleCommandExceptionType(new TranslationTextComponent("teams.error.notinteam", player.getName().getString())).create();
        }
        try {
            TeamDB.INSTANCE.invitePlayerToTeam(newPlayer, team);
            PacketHandler.INSTANCE.sendTo(new TeamInviteSentPacket(team.getName(), newPlayer.getName().getString()), player);
        } catch (Team.TeamException e) {
            throw new SimpleCommandExceptionType(new LiteralMessage(e.getMessage())).create();
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int leaveTeam(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrException();
        try {
            TeamDB.INSTANCE.removePlayerFromTeam(player);
        } catch (Team.TeamException e) {
            throw new SimpleCommandExceptionType(new LiteralMessage(e.getMessage())).create();
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int kickPlayer(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
        ServerPlayerEntity otherPlayer = EntityArgument.getPlayer(ctx, "player");
        try {
            TeamDB.INSTANCE.removePlayerFromTeam(otherPlayer);
        } catch (Team.TeamException e) {
            throw new SimpleCommandExceptionType(new LiteralMessage(e.getMessage())).create();
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int removeTeam(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
        String name = ctx.getArgument("name", String.class);
        Team team = TeamDB.INSTANCE.getTeam(name);
        if (team == null) {
            throw new SimpleCommandExceptionType(new TranslationTextComponent("teams.error.invalidteam", name)).create();
        }
        TeamDB.INSTANCE.removeTeam(team);
        ctx.getSource().sendSuccess(new TranslationTextComponent("teams.success.remove", name), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int listTeams(CommandContext<CommandSource> ctx) {
        ctx.getSource().sendSuccess(new TranslationTextComponent("teams.success.list"), false);
        TeamDB.INSTANCE.getTeams().forEach(team -> {
            ctx.getSource().sendSuccess(new StringTextComponent(team.getName()), false);
        });
        return Command.SINGLE_SUCCESS;
    }

    private static int getTeamInfo(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
        String name = ctx.getArgument("name", String.class);
        Team team = TeamDB.INSTANCE.getTeam(name);
        if (team == null) {
            throw new SimpleCommandExceptionType(new TranslationTextComponent("teams.error.invalidteam", name)).create();
        }
        ctx.getSource().sendSuccess(new TranslationTextComponent("teams.success.info", name), false);
        team.getOnlinePlayers().forEach(player -> {
            ctx.getSource().sendSuccess(player.getName(), false);
        });
        return Command.SINGLE_SUCCESS;
    }

}
