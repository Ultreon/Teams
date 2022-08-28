package com.t2pellet.teams.network.packets;

import com.t2pellet.teams.client.core.ClientTeam;
import com.t2pellet.teams.network.ClientPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class TeamInitPacket extends ClientPacket {

    private static final String NAME_KEY = "teamName";
    private static final String PERMS_KEY = "teamPerms";

    public TeamInitPacket(String name, boolean hasPermissions) {
        tag.putString(NAME_KEY, name);
        tag.putBoolean(PERMS_KEY, hasPermissions);
    }

    public TeamInitPacket(Minecraft client, PacketBuffer byteBuf) {
        super(client, byteBuf);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void execute(Supplier<NetworkEvent.Context> context) {
        ClientTeam.INSTANCE.init(tag.getString(NAME_KEY), tag.getBoolean(PERMS_KEY));
    }
}
