package com.t2pellet.teams.core;

import com.mojang.authlib.GameProfile;
import com.t2pellet.teams.TeamsMod;
import com.t2pellet.teams.config.TeamsConfig;
import com.t2pellet.teams.mixin.AdvancementAccessor;
import com.t2pellet.teams.network.PacketHandler;
import com.t2pellet.teams.network.packets.TeamClearPacket;
import com.t2pellet.teams.network.packets.TeamDataPacket;
import com.t2pellet.teams.network.packets.TeamInitPacket;
import com.t2pellet.teams.network.packets.TeamPlayerDataPacket;
import com.t2pellet.teams.network.packets.toasts.TeamUpdatePacket;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.util.Constants;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Stream;

public class Team extends net.minecraft.scoreboard.Team {

    public final String name;
    private final Set<UUID> players;
    private final Map<UUID, ServerPlayerEntity> onlinePlayers;
    private final Set<Advancement> advancements = new LinkedHashSet<>();
    private ScorePlayerTeam scoreboardTeam;

    @SuppressWarnings("ConstantConditions")
    Team(String name) {
        this.name = name;
        players = new HashSet<>();
        onlinePlayers = new HashMap<>();
        scoreboardTeam = TeamsMod.getScoreboard().getPlayerTeam(name);
        if (scoreboardTeam == null) {
            scoreboardTeam = TeamsMod.getScoreboard().addPlayerTeam(name);
        }
    }

    public UUID getOwner() {
        return players.stream().findFirst().orElseThrow(RuntimeException::new);
    }

    public boolean playerHasPermissions(ServerPlayerEntity player) {
        return getOwner().equals(player.getUUID()) || player.hasPermissions(2);
    }
    public Stream<ServerPlayerEntity> getOnlinePlayers() {
        return onlinePlayers.values().stream();
    }

    public Stream<UUID> getPlayerUUIDs() {
        return players.stream();
    }

    public boolean isEmpty() {
        return players.isEmpty();
    }

    public boolean hasPlayer(ServerPlayerEntity player) {
        return hasPlayer(player.getUUID());
    }

    public boolean hasPlayer(UUID player) {
        return players.contains(player);
    }

    public void addPlayer(ServerPlayerEntity player) {
        addPlayer(player.getUUID());
    }

    public void removePlayer(ServerPlayerEntity player) {
        removePlayer(player.getUUID());
    }

    public void clear() {
        ArrayList<UUID> playersCopy = new ArrayList<>(players);
        playersCopy.forEach(this::removePlayer);
        advancements.clear();
    }

    public void addAdvancement(Advancement advancement) {
        advancements.add(advancement);
    }

    public Set<Advancement> getAdvancements() {
        return advancements;
    }

    void playerOnline(ServerPlayerEntity player, boolean sendPackets) {
        onlinePlayers.put(player.getUUID(), player);
        ((IHasTeam) player).setTeam(this);
        // Packets
        if (sendPackets) {
            PacketHandler.INSTANCE.sendTo(new TeamInitPacket(name, playerHasPermissions(player)), player);
            if (onlinePlayers.size() == 1) {
                ServerPlayerEntity[] players = TeamsMod.getServer().getPlayerList().getPlayers().toArray(new ServerPlayerEntity[]{});
                PacketHandler.INSTANCE.sendTo(new TeamDataPacket(TeamDataPacket.Type.ONLINE, name), players);
            }
            ServerPlayerEntity[] players = getOnlinePlayers().toArray(ServerPlayerEntity[]::new);
            PacketHandler.INSTANCE.sendTo(new TeamPlayerDataPacket(player, TeamPlayerDataPacket.Type.ADD), players);
            for (ServerPlayerEntity teammate : players) {
                PacketHandler.INSTANCE.sendTo(new TeamPlayerDataPacket(teammate, TeamPlayerDataPacket.Type.ADD), player);
            }
        }
        // Advancement Sync
        for (Advancement advancement : getAdvancements()) {
            AdvancementProgress progress = player.getAdvancements().getOrStartProgress(advancement);
            for (String criterion : progress.getRemainingCriteria()) {
                player.getAdvancements().award(advancement, criterion);
            }
        }
    }

    void playerOffline(ServerPlayerEntity player, boolean sendPackets) {
        onlinePlayers.remove(player.getUUID());
        // Packets
        if (sendPackets) {
            if (isEmpty()) {
                ServerPlayerEntity[] players = TeamsMod.getServer().getPlayerList().getPlayers().toArray(new ServerPlayerEntity[]{});
                PacketHandler.INSTANCE.sendTo(new TeamDataPacket(TeamDataPacket.Type.OFFLINE, name), players);
            }
            ServerPlayerEntity[] players = getOnlinePlayers().toArray(ServerPlayerEntity[]::new);
            PacketHandler.INSTANCE.sendTo(new TeamPlayerDataPacket(player, TeamPlayerDataPacket.Type.REMOVE), players);
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void addPlayer(UUID player) {
        players.add(player);
        String playerName = getNameFromUUID(player);
        // Scoreboard
        net.minecraft.scoreboard.Team playerScoreboardTeam = TeamsMod.getScoreboard().getPlayerTeam(playerName);
        if (playerScoreboardTeam == null || !playerScoreboardTeam.isAlliedTo(scoreboardTeam)) {
            TeamsMod.getScoreboard().addPlayerToTeam(playerName, scoreboardTeam);
        }
        ServerPlayerEntity playerEntity = TeamsMod.getServer().getPlayerList().getPlayer(player);
        if (playerEntity != null) {
            // Packets
            PacketHandler.INSTANCE.sendTo(new TeamUpdatePacket(name, playerName, TeamUpdatePacket.Action.JOINED, true), playerEntity);
            PacketHandler.INSTANCE.sendTo(new TeamUpdatePacket(name, playerName, TeamUpdatePacket.Action.JOINED, false), getOnlinePlayers().toArray(ServerPlayerEntity[]::new));
            playerOnline(playerEntity, true);
            // Advancement Sync
            Set<Advancement> advancements = ((AdvancementAccessor) playerEntity.getAdvancements()).getVisibleAdvancements();
            for (Advancement advancement : advancements) {
                if (playerEntity.getAdvancements().getOrStartProgress(advancement).isDone()) {
                    addAdvancement(advancement);
                }
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void removePlayer(UUID player) {
        players.remove(player);
        String playerName = getNameFromUUID(player);
        // Scoreboard
        net.minecraft.scoreboard.Team playerScoreboardTeam = TeamsMod.getScoreboard().getPlayerTeam(playerName);
        if (playerScoreboardTeam != null && playerScoreboardTeam.isAlliedTo(scoreboardTeam)) {
            TeamsMod.getScoreboard().removePlayerFromTeam(playerName, scoreboardTeam);
        }
        // Packets
        ServerPlayerEntity playerEntity = TeamsMod.getServer().getPlayerList().getPlayer(player);
        if (playerEntity != null) {
            playerOffline(playerEntity, true);
            PacketHandler.INSTANCE.sendTo(new TeamClearPacket(), playerEntity);
            PacketHandler.INSTANCE.sendTo(new TeamUpdatePacket(name, playerName, TeamUpdatePacket.Action.LEFT, true), playerEntity);
            PacketHandler.INSTANCE.sendTo(new TeamUpdatePacket(name, playerName, TeamUpdatePacket.Action.LEFT, false), getOnlinePlayers().toArray(ServerPlayerEntity[]::new));
            ((IHasTeam) playerEntity).setTeam(null);
        }
    }

    private String getNameFromUUID(UUID id) {
        return Optional.ofNullable(TeamsMod.getServer().getProfileCache().get(id)).map(GameProfile::getName).orElseThrow(RuntimeException::new);
    }

    @SuppressWarnings("ConstantConditions")
    static Team fromNBT(CompoundNBT compound) {
        Team team = new Team.Builder(compound.getString("name"))
                .setColour(TextFormatting.getByName(compound.getString("colour")))
                .setCollisionRule(CollisionRule.byName(compound.getString("collision")))
                .setDeathMessageVisibilityRule(Visible.byName(compound.getString("deathMessages")))
                .setNameTagVisibilityRule(Visible.byName(compound.getString("nameTags")))
                .setFriendlyFireAllowed(compound.getBoolean("friendlyFire"))
                .setShowFriendlyInvisibles(compound.getBoolean("showInvisible"))
                .complete();

        ListNBT players = compound.getList("players", Constants.NBT.TAG_STRING);
        for (INBT elem : players) {
            team.addPlayer(UUID.fromString(elem.getAsString()));
        }

        ListNBT advancements = compound.getList("advancement", Constants.NBT.TAG_STRING);
        for (INBT adv : advancements) {
            ResourceLocation id = ResourceLocation.tryParse(adv.getAsString());
            team.addAdvancement(TeamsMod.getServer().getAdvancements().getAdvancement(id));
        }

        return team;
    }

    CompoundNBT toNBT() {
        CompoundNBT compound = new CompoundNBT();
        compound.putString("name", name);
        compound.putString("colour", scoreboardTeam.getColor().getName());
        compound.putString("collision", scoreboardTeam.getCollisionRule().name);
        compound.putString("deathMessages", scoreboardTeam.getDeathMessageVisibility().name);
        compound.putString("nameTags", scoreboardTeam.getNameTagVisibility().name);
        compound.putBoolean("friendlyFire", scoreboardTeam.isAllowFriendlyFire());
        compound.putBoolean("showInvisible", scoreboardTeam.canSeeFriendlyInvisibles());

        ListNBT playerList = new ListNBT();
        for (UUID player : players) {
            playerList.add(StringNBT.valueOf(player.toString()));
        }
        compound.put("players", playerList);

        ListNBT advList = new ListNBT();
        for (Advancement advancement : advancements) {
            advList.add(StringNBT.valueOf(advancement.getId().toString()));
        }
        compound.put("advancements", advList);

        return compound;
    }

    @NotNull
    @Override
    public String getName() {
        return name;
    }

    @NotNull
    @Override
    public IFormattableTextComponent getFormattedName(@NotNull ITextComponent name) {
        return scoreboardTeam.getFormattedName(name);
    }

    @Override
    public boolean canSeeFriendlyInvisibles() {
        return scoreboardTeam.canSeeFriendlyInvisibles();
    }

    public void setSeeFriendlyInvisibles(boolean value) {
        scoreboardTeam.setSeeFriendlyInvisibles(value);
    }

    @Override
    public boolean isAllowFriendlyFire() {
        return scoreboardTeam.isAllowFriendlyFire();
    }

    public void setAllowFriendlyFire(boolean value) {
        scoreboardTeam.setAllowFriendlyFire(value);
    }

    @NotNull
    @Override
    public Visible getNameTagVisibility() {
        return scoreboardTeam.getNameTagVisibility();
    }

    public void setNameTagVisibility(Visible value) {
        scoreboardTeam.setNameTagVisibility(value);
    }

    @NotNull
    @Override
    public TextFormatting getColor() {
        return scoreboardTeam.getColor();
    }

    public void setColor(TextFormatting colour) {
        scoreboardTeam.setColor(colour);
    }

    @NotNull
    @Override
    public Collection<String> getPlayers() {
        return scoreboardTeam.getPlayers();
    }

    @NotNull
    @Override
    public Visible getDeathMessageVisibility() {
        return scoreboardTeam.getDeathMessageVisibility();
    }

    public void setDeathMessageVisibility(Visible value) {
        scoreboardTeam.setDeathMessageVisibility(value);
    }

    @NotNull
    @Override
    public CollisionRule getCollisionRule() {
        return scoreboardTeam.getCollisionRule();
    }

    public void setCollisionRule(CollisionRule value) {
        scoreboardTeam.setCollisionRule(value);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Team && Objects.equals(((Team) obj).getName(), this.name);
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }


    public static class TeamException extends Exception {
        public TeamException(TextComponent message) {
            super(message.getString());
        }
    }

    public static class Builder {

        private final String name;
        private boolean showFriendlyInvisibles = TeamsConfig.SHOW_INVISIBLE_TEAMMATES.get();
        private boolean friendlyFireAllowed = TeamsConfig.FRIENDLY_FIRE_ENABLED.get();
        private Visible nameTagVisibilityRule = TeamsConfig.NAME_TAG_VISIBILITY.get();
        private TextFormatting colour = TeamsConfig.COLOUR.get();
        private Visible deathMessageVisibilityRule = TeamsConfig.DEATH_MESSAGE_VISIBILITY.get();
        private CollisionRule collisionRule = TeamsConfig.COLLISION_RULE.get();

        public Builder(String name) {
            this.name = name;
        }

        public Builder setShowFriendlyInvisibles(boolean showFriendlyInvisibles) {
            this.showFriendlyInvisibles = showFriendlyInvisibles;
            return this;
        }

        public Builder setFriendlyFireAllowed(boolean friendlyFireAllowed) {
            this.friendlyFireAllowed = friendlyFireAllowed;
            return this;
        }

        public Builder setNameTagVisibilityRule(Visible nameTagVisibilityRule) {
            this.nameTagVisibilityRule = nameTagVisibilityRule;
            return this;
        }

        public Builder setColour(TextFormatting colour) {
            this.colour = colour;
            return this;
        }

        public Builder setDeathMessageVisibilityRule(Visible deathMessageVisibilityRule) {
            this.deathMessageVisibilityRule = deathMessageVisibilityRule;
            return this;
        }

        public Builder setCollisionRule(CollisionRule collisionRule) {
            this.collisionRule = collisionRule;
            return this;
        }

        public Team complete() {
            Team team = new Team(name);
            team.setSeeFriendlyInvisibles(showFriendlyInvisibles);
            team.setAllowFriendlyFire(friendlyFireAllowed);
            team.setNameTagVisibility(nameTagVisibilityRule);
            team.setColor(colour);
            team.setDeathMessageVisibility(deathMessageVisibilityRule);
            team.setCollisionRule(collisionRule);
            return team;
        }

    }
}
