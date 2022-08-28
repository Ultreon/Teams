package com.t2pellet.teams.mixin;

import com.mojang.authlib.GameProfile;
import com.t2pellet.teams.core.IHasTeam;
import com.t2pellet.teams.core.Team;
import com.t2pellet.teams.core.TeamDB;
import com.t2pellet.teams.events.PlayerUpdateEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class PlayerMixin extends PlayerEntity implements IHasTeam {
	@Unique
	private Team team;

	@Shadow private float lastSentHealth;
	@Shadow private int lastSentFood;

	public PlayerMixin(World world, BlockPos pos, float yaw, GameProfile profile) {
		super(world, pos, yaw, profile);
	}

	@Override
	public boolean hasTeam() {
		return team != null;
	}

	@Override
	public Team getTeam() {
		return team;
	}

	@Override
	public void setTeam(Team team) {
		this.team = team;
	}

	@Override
	public boolean isTeammate(ServerPlayerEntity other) {
		return team.equals(((IHasTeam) other).getTeam());
	}

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/CompoundNBT;putBoolean(Ljava/lang/String;Z)V"), method = "addAdditionalSaveData")
	private void writeCustomDataToNbt(CompoundNBT nbt, CallbackInfo info) {
		if (team != null) {
			nbt.putString("playerTeam", team.getName());
		}
	}

	@Inject(at = @At(value = "TAIL"), method = "readAdditionalSaveData")
	private void readCustomDataFromNbt(CompoundNBT nbt, CallbackInfo info) {
		if (team == null && nbt.contains("playerTeam")) {
			team = TeamDB.INSTANCE.getTeam(nbt.getString("playerTeam"));
			if (team == null || !team.hasPlayer(getUUID())) {
				team = null;
			}
		}
	}

	@Inject(at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/entity/player/ServerPlayerEntity;getHealth()F", ordinal = 1), method = "doTick")
	private void playerTick(CallbackInfo info) {
		ServerPlayerEntity player = (ServerPlayerEntity) ((Object) this);
		MinecraftForge.EVENT_BUS.post(new PlayerUpdateEvents.PlayerHealthUpdate(player, player.getHealth(), player.getFoodData().getFoodLevel()));
	}

	@Inject(at = @At("TAIL"), method = "restoreFrom")
	private void copyFrom(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo info) {
		MinecraftForge.EVENT_BUS.post(new PlayerUpdateEvents.PlayerCopy(oldPlayer, (ServerPlayerEntity) ((Object) this)));
	}

	@Override
	public boolean isSpectator() {
		return ((ServerPlayerEntity) (Object) this).gameMode.getGameModeForPlayer() == GameType.SPECTATOR;
	}

	@Override
	public boolean isCreative() {
		return ((ServerPlayerEntity) (Object) this).gameMode.getGameModeForPlayer() == GameType.CREATIVE;
	}
}
