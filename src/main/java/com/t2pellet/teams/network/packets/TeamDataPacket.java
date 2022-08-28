package com.t2pellet.teams.network.packets;

import com.t2pellet.teams.client.core.ClientTeamDB;
import com.t2pellet.teams.network.ClientPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class TeamDataPacket extends ClientPacket {

    private static final String TEAM_KEY = "teamName";
    private static final String TYPE_KEY = "type";

    public enum Type {
        ADD,
        REMOVE,
        ONLINE,
        OFFLINE,
        CLEAR
    }

    public TeamDataPacket(Type type, String... teams) {
        ListNBT nbtList = new ListNBT();
        for (String team : teams) {
            nbtList.add(StringNBT.valueOf(team));
        }
        tag.put(TEAM_KEY, nbtList);
        tag.putString(TYPE_KEY, type.name());
    }

    public TeamDataPacket(Minecraft client, PacketBuffer byteBuf) {
        super(client, byteBuf);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void execute(Supplier<NetworkEvent.Context> context) {
        Type type = Type.valueOf(tag.getString(TYPE_KEY));
        ListNBT nbtList = tag.getList(TEAM_KEY, Constants.NBT.TAG_STRING);
        for (INBT elem : nbtList) {
            String team = elem.getAsString();
            switch (type) {
                case ADD:
                    ClientTeamDB.INSTANCE.addTeam(team);
                    break;
                case REMOVE:
                    ClientTeamDB.INSTANCE.removeTeam(team);
                    break;
                case ONLINE:
                    ClientTeamDB.INSTANCE.teamOnline(team);
                    break;
                case OFFLINE:
                    ClientTeamDB.INSTANCE.teamOffline(team);
                    break;
                case CLEAR:
                    ClientTeamDB.INSTANCE.clear();
                    break;
            }
        }
    }

}
