package com.t2pellet.teams.events;

import net.minecraft.advancements.Advancement;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.eventbus.api.Event;

public class AdvancementEvents {
    public static class PlayerAdvancement extends Event {
        public final ServerPlayerEntity player;
        public final Advancement advancement;

        public PlayerAdvancement(ServerPlayerEntity player, Advancement advancement) {
            this.player = player;
            this.advancement = advancement;
        }
    }

}
