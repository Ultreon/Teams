package com.t2pellet.teams;

import com.t2pellet.teams.command.TeamCommand;
import com.t2pellet.teams.config.TeamsConfig;
import com.t2pellet.teams.core.TeamDB;
import com.t2pellet.teams.network.PacketHandler;
import com.t2pellet.teams.network.TeamPackets;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.FolderName;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;

@Mod(TeamsMod.MODID)
public class TeamsMod {

	public static final String MODID = "teams";
	public static final Logger LOGGER = LogManager.getLogger(MODID);
	private static MinecraftServer server;
	private static Scoreboard scoreboard;

	public static MinecraftServer getServer() {
		return server;
	}

	public static Scoreboard getScoreboard() {
		return scoreboard;
	}

	public TeamsMod() {
		MinecraftForge.EVENT_BUS.register(this);

		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);

		TeamsConfig.register(ModLoadingContext.get());
	}

	public static ResourceLocation res(String path) {
		return new ResourceLocation(MODID, path);
	}

	public void commonSetup(FMLCommonSetupEvent event) {
		LOGGER.info("Teams mod init!");

		PacketHandler.register(TeamPackets.class);
	}

	@SubscribeEvent
	public void serverStarted(FMLServerStartedEvent event) {
		// Get server instance
		TeamsMod.server = event.getServer();
		TeamsMod.scoreboard = server.getScoreboard();
		// Load saved teams
		try {
			File saveFile = new File(server.getWorldPath(FolderName.ROOT).toFile(), "teams.dat");
			CompoundNBT element = CompressedStreamTools.read(saveFile);
			if (element != null) {
				TeamDB.INSTANCE.fromNBT(element);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SubscribeEvent
	public void serverStopped(FMLServerStoppedEvent event) {
		// Save teams
		try {
			File saveFile = new File(server.getWorldPath(FolderName.ROOT).toFile(), "teams.dat");
			CompoundNBT element = TeamDB.INSTANCE.toNBT();
			CompressedStreamTools.write(element, saveFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SubscribeEvent
	public void registerCommands(RegisterCommandsEvent event) {
		TeamCommand.register(event.getDispatcher());
	}
}
