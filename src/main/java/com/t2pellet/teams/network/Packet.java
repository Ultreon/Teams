package com.t2pellet.teams.network;

import com.t2pellet.teams.TeamsMod;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public abstract class Packet {

    protected CompoundTag tag;

    Packet(PacketByteBuf byteBuf) {
        this.tag = byteBuf.readCompoundTag();
    }

    Packet() {
        tag = new CompoundTag();
    }

    public void encode(PacketByteBuf byteBuf) {
        byteBuf.writeCompoundTag(tag);
    }

    public abstract void execute();


    public static class PacketKey<T extends Packet> {

        private Class<T> clazz;
        private Identifier id;

        public PacketKey(Class<T> clazz, String id) {
            this.clazz = clazz;
            this.id = new Identifier(TeamsMod.MODID, id);
        }

        public Class<T> getClazz() {
            return clazz;
        }

        public Identifier getId() {
            return id;
        }
    }
}
