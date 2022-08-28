package com.t2pellet.teams.events;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.eventbus.api.Event;

public class PlayerUpdateEvents {
    public static class PlayerHealthUpdate extends Event {
        public final ServerPlayerEntity player;
        public final float health;
        public final int hunger;

        public PlayerHealthUpdate(ServerPlayerEntity player, float health, int hunger) {
            this.player = player;
            this.health = health;
            this.hunger = hunger;
        }
    }

    public static class PlayerCopy extends Event {
        public final ServerPlayerEntity oldPlayer;
        public final ServerPlayerEntity newPlayer;

        public PlayerCopy(ServerPlayerEntity oldPlayer, ServerPlayerEntity newPlayer) {
            this.oldPlayer = oldPlayer;
            this.newPlayer = newPlayer;
        }
    }
}
