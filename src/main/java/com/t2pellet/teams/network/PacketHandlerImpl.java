package com.t2pellet.teams.network;

import com.t2pellet.teams.TeamsMod;
import com.t2pellet.teams.network.packets.LoginPacket;
import io.netty.handler.codec.EncoderException;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.network.FMLHandshakeHandler;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

class PacketHandlerImpl implements PacketHandler {
    private static final String VERSION = "teams-net1";
    private final Map<Class<? extends Packet>, ResourceLocation> idMap;
    private SimpleChannel channel;
    private int id = 0;

    PacketHandlerImpl() {
        idMap = new HashMap<>();
    }

    public final void initialize() {
        channel = NetworkRegistry.ChannelBuilder.named(TeamsMod.res("network"))
                .clientAcceptedVersions(s -> Objects.equals(s, VERSION))
                .serverAcceptedVersions(s -> Objects.equals(s, VERSION))
                .networkProtocolVersion(() -> VERSION)
                .simpleChannel();

        /////////////////////////////////
        //     PACKET REGISTRATION     //
        /////////////////////////////////
        channel.messageBuilder(LoginPacket.Reply.class, id++)
                .loginIndex(LoginPacket::getLoginIndex, LoginPacket::setLoginIndex)
                .decoder(buffer -> new LoginPacket.Reply())
                .encoder((msg, buffer) -> {
                })
                .consumer(FMLHandshakeHandler.indexFirst((hh, msg, ctx) -> msg.handle(ctx)))
                .add();
    }

    @Override
    public <T extends Packet> void registerPacket(ResourceLocation id, Class<T> packetClass) {
        channel.messageBuilder(packetClass, this.id++)
                .decoder(packetBuffer -> {
                    T packet = null;
                    if (FMLEnvironment.dist == Dist.CLIENT) {
                        if (ClientPacket.class.isAssignableFrom(packetClass)) {
                            try {
                                packet = packetClass.getDeclaredConstructor(Minecraft.class, PacketBuffer.class).newInstance(Minecraft.getInstance(), packetBuffer);
                            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                                     InvocationTargetException ex) {
                                throw new EncoderException("Failed to instantiate packet - " + id, ex);
                            }
                            ;
                        }
                    }
                    if (ServerPacket.class.isAssignableFrom(packetClass)) {
                        try {
                            packet = packetClass.getDeclaredConstructor(MinecraftServer.class, PacketBuffer.class).newInstance(ServerLifecycleHooks.getCurrentServer(), packetBuffer);
                        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                                 InvocationTargetException ex) {
                            throw new EncoderException("Failed to instantiate packet - " + id, ex);
                        }
                    }

                    if (packet == null) {
                        throw new EncoderException("Packet not valid for current dist - " + id);
                    }

                    return packet;
                })
                .encoder(Packet::encode)
                .consumer((t, contextSupplier) -> {
                    t.execute(contextSupplier);
                    return true;
                })
                .add();


        idMap.put(packetClass, id);
    }

    @Override
    public void sendToServer(Packet packet) {
        channel.sendToServer(packet);
    }

    @Override
    public void sendTo(Packet packet, ServerPlayerEntity player) {
        channel.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }

    @Override
    public void sendTo(Packet packet, ServerPlayerEntity... players) {
        for (ServerPlayerEntity player : players) sendTo(packet, player);
    }

    @Override
    public void sendInRange(Packet packet, Entity e, float range) {
        sendInArea(packet, e.level, e.blockPosition(), range);
    }

    @Override
    public void sendInArea(Packet packet, World world, BlockPos pos, float range) {
        AxisAlignedBB box = new AxisAlignedBB(pos);
        List<ServerPlayerEntity> nearbyPlayers = world.getEntitiesOfClass(ServerPlayerEntity.class, box.inflate(range), p -> true);
        sendTo(packet, nearbyPlayers.toArray(new ServerPlayerEntity[0]));
    }

}
