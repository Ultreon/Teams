package com.t2pellet.teams.network.packets;

import com.t2pellet.teams.client.core.ClientTeam;
import com.t2pellet.teams.network.ClientPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class TeamClearPacket extends ClientPacket {

    public TeamClearPacket() {
    }

    public TeamClearPacket(Minecraft client, PacketBuffer byteBuf) {
        super(client, byteBuf);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void execute(Supplier<NetworkEvent.Context> context) {
        ClientTeam.INSTANCE.reset();
    }
}
