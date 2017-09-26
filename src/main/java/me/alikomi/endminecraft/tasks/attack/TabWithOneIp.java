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
    private static byte[] reg;

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
                log("s - v " + packetReceivedEvent.getPacket().toString());
                if (packetReceivedEvent.getPacket() instanceof ServerPluginMessagePacket) {
                    log(((ServerPluginMessagePacket) packetReceivedEvent.getPacket()).getChannel());
                    log(new String(((ServerPluginMessagePacket) packetReceivedEvent.getPacket()).getData()));
                    log(Arrays.toString(((ServerPluginMessagePacket) packetReceivedEvent.getPacket()).getData()));
                    byte[] data = ((ServerPluginMessagePacket) packetReceivedEvent.getPacket()).getData();
                    //SendHello
                    if (((ServerPluginMessagePacket) packetReceivedEvent.getPacket()).getChannel().equals("FML|HS") && data.length > 2 && ((byte) 0) == data[0]) {
                        log("发送通道注册包");
                        mc.getSession().send(new ClientPluginMessagePacket("REGISTER", reg));
                        log("发送hello包");
                        mc.getSession().send(new ClientPluginMessagePacket("FML|HS", new byte[]{0x01, 0x02}));
                        log("发送modlist");
                        Map<String, String> ml = new GetModList(ip,port).get();
                        ByteBuf bf = Unpooled.buffer();
                        DefinedPacket.writeVarInt(2, bf);
                        bf.writeByte(ml.size());
                        log("qwq");
                        ml.forEach((k, v) -> {
                            DefinedPacket.writeString(k, bf);
                            log("MODNAME: " + k);
                            DefinedPacket.writeString(v, bf);
                            log("MOVER: " + v);
                        });

                        mc.getSession().send(new ClientPluginMessagePacket("FML|HS", fb(bf.array())));
                    }
                    //SendACK
                    if (((ServerPluginMessagePacket) packetReceivedEvent.getPacket()).getChannel().equals("FML|HS") && data.length > 1 && ((byte) 2) == data[0]) {
                        log("收到服务器Mod列表，准备发送ACK-2包，准备接受RegistryData数据");
                        mc.getSession().send(new ClientPluginMessagePacket("FML|HS", new byte[]{(byte) -1, 0x02}));
                        mc.getSession().send(new ClientPluginMessagePacket("FML|HS", new byte[]{(byte) -1, 0x03}));
                    }
                    //Read RegistryData
                    if (((ServerPluginMessagePacket) packetReceivedEvent.getPacket()).getChannel().equals("FML|HS") && data.length > 1 && ((byte) 3) == data[0]) {
                        log("收到Registry数据，正在准备发送ACK-3包");
                        mc.getSession().send(new ClientPluginMessagePacket("FML|HS", new byte[]{(byte) -1, 0x03}));
                    }
                    if (((ServerPluginMessagePacket) packetReceivedEvent.getPacket()).getChannel().equals("REGISTER")) {
                        reg = ((ServerPluginMessagePacket) packetReceivedEvent.getPacket()).getData();
                        log("通道注册包： " + new String(((ServerPluginMessagePacket) packetReceivedEvent.getPacket()).getData()));
                        mc.getSession().send(new ClientPluginMessagePacket("REGISTER", reg));
                    }
                }

                if (packetReceivedEvent.getPacket() instanceof ServerJoinGamePacket) {
                    mc.getSession().send(new ClientChatPacket("/register qwnmopzx123 qwnmopzx123"));
                    mc.getSession().send(new ClientChatPacket("/login qwnmopzx123"));
                }
                if (packetReceivedEvent.getPacket() instanceof ServerChatPacket) {
                    log("聊天：" + ((ServerChatPacket) packetReceivedEvent.getPacket()).getMessage().getFullText());
                }
                if (packetReceivedEvent.getPacket() instanceof ServerTabCompletePacket) {
                    log("喵！");
                }
            }

            @Override
            public void packetSent(PacketSentEvent packetSentEvent) {

                log("v - s " + packetSentEvent.getPacket().toString());
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