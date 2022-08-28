package com.t2pellet.teams.mixin;

import com.t2pellet.teams.TeamsMod;
import com.t2pellet.teams.events.AdvancementEvents;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerAdvancements.class)
public class AdvancementMixin {

    @Shadow private ServerPlayerEntity player;

    @Inject(method = "award", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/PlayerAdvancements;ensureVisibility(Lnet/minecraft/advancements/Advancement;)V"))
    public void advancementCompleted(Advancement advancement, String criterionName, CallbackInfoReturnable<Boolean> ci) {
        if (!TeamsMod.getServer().overworld().isClientSide) {
            MinecraftForge.EVENT_BUS.post(new AdvancementEvents.PlayerAdvancement(player, advancement));
        }
    }
}
