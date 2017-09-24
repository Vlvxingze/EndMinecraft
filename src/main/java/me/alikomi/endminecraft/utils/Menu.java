package me.alikomi.endminecraft.utils;

import me.alikomi.endminecraft.Main;
import me.alikomi.endminecraft.tasks.attack.*;

import java.io.IOException;
import java.net.Proxy;
import java.util.Map;
import java.util.Scanner;

public class Menu extends Util {
    private String ip;
    private Scanner scanner;
    private int port;

    public Menu(Scanner sc, String ip, int port) {
        this.ip = ip;
        this.port = port;
        this.scanner = sc;
    }

    public void _1() {
        log("MOTD攻击选择");
        if (Main.bugData != null) {
            if (Main.bugData.getMotdBug()) {
                log("恭喜！服务器有motdbug，秒蹦他吧");
            } else {
                log("本服务器没有motdbug哦！可能本功能会无效，请选择其他功能吧~");
            }
        }
        log("请输入攻击时间(单位：蛤)(60)");//我就是这么暴力
        int time = getCo(scanner.nextLine(),60)*1000;
        log("请输入线程数(16)");
        int thread = getCo(scanner.nextLine(),16);
        MotdAttack attack = new MotdAttack(ip, port, time, thread);
        attack.startAttack();
    }

    public void _2() throws IOException, InterruptedException {
        log("分布式假人压测选择", "请输入攻击时长！(1000s)");
        long time = getCo(scanner.nextLine(),1000);
        log("请输入最大攻击数(10000)");
        int maxAttack = getCo(scanner.nextLine(),10000);
        log("请输入每次加入服务器间隔(ms)");
        int sleepTime = getCo(scanner.nextLine(),1);
        log("请输入是否开启TAB攻击 y/n，默认开启(y)");
        boolean tab = getCo(scanner.nextLine(),"y").equals("y");
        log("请输入是否开启操死乐乐模式 y/n，默认关闭(n)");
        boolean lele = getCo(scanner.nextLine(),"n").equals("y");
        Map<String, Proxy.Type> ips = getProxy(maxAttack);
        DistributedBotAttack distributedBotAttack = new DistributedBotAttack(ip, port, time * 1000, sleepTime, ips,tab,lele);
        distributedBotAttack.startAttack();
    }

    private Map<String, Proxy.Type> getProxy(int maxAttack) throws IOException, InterruptedException {
        log("请输入代理ip列表获取方式（2）：", "1.通过API获取", "2.通过本地获取", "3.官方获取");
        Map<String, Proxy.Type> ips;
        switch (getCo(scanner.nextLine(),2)) {
            case 1: {
                ips = getHttpIp(maxAttack);
                break;
            }
            case 2: {
                ips = getFileIp(maxAttack);
                break;
            }
            case 3: {
                ips = getALiKOMIIp(maxAttack, scanner);
                break;
            }
            default: {
                ips = getHttpIp(maxAttack);
                break;
            }
        }
        return ips;
    }
}
