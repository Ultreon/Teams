package com.t2pellet.teams.client;

import com.t2pellet.teams.client.core.ClientTeam;
import com.t2pellet.teams.client.ui.toast.ToastInvited;
import com.t2pellet.teams.client.ui.toast.ToastRequested;
import com.t2pellet.teams.network.PacketHandler;
import com.t2pellet.teams.network.packets.TeamJoinPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.toasts.IToast;
import net.minecraft.client.gui.toasts.ToastGui;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
public class TeamsKeys {

    public static class TeamsKey {
        @FunctionalInterface
        public interface OnPress {
            void execute(Minecraft client);
        }

        private TeamsKey(String keyName, int keyBind, OnPress action) {
            keyBinding = new KeyBinding(
                    keyName,
                    InputMappings.Type.KEYSYM,
                    keyBind,
                    "key.category.teams"
            );
            onPress = action;
        }

        public void register() {
            ClientRegistry.registerKeyBinding(keyBinding);
        }

        public String getLocalizedName() {
            return keyBinding.getTranslatedKeyMessage().getString();
        }

        final KeyBinding keyBinding;
        final OnPress onPress;
    }

    public static final TeamsKey ACCEPT = new TeamsKey("key.teams.accept", GLFW.GLFW_KEY_RIGHT_BRACKET, client -> {
        ToastGui toastManager = client.getToasts();
        ToastInvited invited = toastManager.getToast(ToastInvited.class, IToast.NO_TOKEN);
        if (invited != null) {
            invited.respond();
            PacketHandler.INSTANCE.sendToServer(new TeamJoinPacket(client.player.getUUID(), invited.team));
        } else {
            ToastRequested requested = toastManager.getToast(ToastRequested.class, IToast.NO_TOKEN);
            if (requested != null) {
                requested.respond();
                PacketHandler.INSTANCE.sendToServer(new TeamJoinPacket(requested.id, ClientTeam.INSTANCE.getName()));
            }
        }
    });

    public static final TeamsKey REJECT = new TeamsKey("key.teams.reject", GLFW.GLFW_KEY_LEFT_BRACKET, client -> {
        ToastGui toastManager = client.getToasts();
        ToastInvited toast = toastManager.getToast(ToastInvited.class, IToast.NO_TOKEN);
        if (toast != null) {
            toast.respond();
        } else {
            ToastRequested requested = toastManager.getToast(ToastRequested.class, IToast.NO_TOKEN);
            if (requested != null) {
                requested.respond();
            }
        }
    });

    public static final TeamsKey TOGGLE_HUD = new TeamsKey("key.teams.toggle_hud", GLFW.GLFW_KEY_B, client -> {
        TeamsModClient.compass.enabled = !TeamsModClient.compass.enabled;
        TeamsModClient.status.enabled = !TeamsModClient.status.enabled;
    });

    static final TeamsKey[] KEYS = {
            ACCEPT,
            REJECT,
            TOGGLE_HUD
    };

}
