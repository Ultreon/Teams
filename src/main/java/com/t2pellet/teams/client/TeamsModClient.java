package com.t2pellet.teams.client;

import com.t2pellet.teams.TeamsMod;
import com.t2pellet.teams.client.core.ClientTeam;
import com.t2pellet.teams.client.core.ClientTeamDB;
import com.t2pellet.teams.client.ui.hud.CompassOverlay;
import com.t2pellet.teams.client.ui.hud.StatusOverlay;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = TeamsMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class TeamsModClient {

    public static final Minecraft client = Minecraft.getInstance();
    public static final StatusOverlay status = new StatusOverlay();
    public static final CompassOverlay compass = new CompassOverlay();

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.addListener(TeamsModClient::renderGameOverlay);
        MinecraftForge.EVENT_BUS.addListener(TeamsModClient::endClientTick);

        // Register keybinds
        for (TeamsKeys.TeamsKey key : TeamsKeys.KEYS) {
            key.register();
        }
    }

    private static void renderGameOverlay(RenderGameOverlayEvent event) {
        status.render(event.getMatrixStack());
        compass.render(event.getMatrixStack());
    }

    private static void endClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            for (TeamsKeys.TeamsKey key : TeamsKeys.KEYS) {
                if (key.keyBinding.consumeClick()) {
                    key.onPress.execute(client);
                }
            }
        }
    }

    private static void clientDisconnect(ClientPlayerNetworkEvent.LoggedOutEvent event) {
        ClientTeam.INSTANCE.reset();
        ClientTeamDB.INSTANCE.clear();
    }

}
