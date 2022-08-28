package com.t2pellet.teams.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;

public abstract class ClientPacket extends Packet {

    public ClientPacket(Minecraft client, PacketBuffer byteBuf) {
        super(byteBuf);
    }

    protected ClientPacket() {
        super();
    }
}
