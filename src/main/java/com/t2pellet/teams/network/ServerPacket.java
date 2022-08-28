package com.t2pellet.teams.network;

import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;

public abstract class ServerPacket extends Packet {

    public ServerPacket(MinecraftServer server, PacketBuffer byteBuf) {
        super(byteBuf);
    }

    protected ServerPacket() {
        super();
    }
}
