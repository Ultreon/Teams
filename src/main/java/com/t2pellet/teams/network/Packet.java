package com.t2pellet.teams.network;

import com.t2pellet.teams.TeamsMod;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public abstract class Packet {

    protected CompoundNBT tag;

    Packet(PacketBuffer byteBuf) {
        this.tag = byteBuf.readNbt();
    }

    Packet() {
        tag = new CompoundNBT();
    }

    public void encode(PacketBuffer byteBuf) {
        byteBuf.writeNbt(tag);
    }

    public abstract void execute(Supplier<NetworkEvent.Context> context);


    public static class PacketKey<T extends Packet> {

        private Class<T> clazz;
        private ResourceLocation id;

        public PacketKey(Class<T> clazz, String id) {
            this.clazz = clazz;
            this.id = new ResourceLocation(TeamsMod.MODID, id);
        }

        public Class<T> getClazz() {
            return clazz;
        }

        public ResourceLocation getId() {
            return id;
        }
    }
}
