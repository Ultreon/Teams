package com.t2pellet.teams.network.packets;

import com.t2pellet.teams.client.core.ClientTeamDB;
import com.t2pellet.teams.network.ClientPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.PacketByteBuf;

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
        ListTag nbtList = new ListTag();
        for (String team : teams) {
            nbtList.add(StringTag.of(team));
        }
        tag.put(TEAM_KEY, nbtList);
        tag.putString(TYPE_KEY, type.name());
    }

    public TeamDataPacket(MinecraftClient client, PacketByteBuf byteBuf) {
        super(client, byteBuf);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void execute() {
        Type type = Type.valueOf(tag.getString(TYPE_KEY));
        ListTag nbtList = tag.getList(TEAM_KEY, NbtType.STRING);
        for (Tag elem : nbtList) {
            String team = elem.asString();
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
