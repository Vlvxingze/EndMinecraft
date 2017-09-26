package me.alikomi.endminecraft.utils;

import me.alikomi.endminecraft.Main;
import me.alikomi.endminecraft.tasks.others.GetALiKOMIIp;
import me.alikomi.endminecraft.tasks.others.GetFileIp;
import me.alikomi.endminecraft.tasks.others.GetHttpIp;
import me.alikomi.endminecraft.tasks.others.TestProxy;

import java.io.IOException;
import java.net.Proxy;
import java.util.Map;
import java.util.Scanner;

public class Util {
    public static void log(Object msg) {
        Main.logger.put(msg.toString());
        System.out.println(msg);
    }

    public static void log(Object... msg) {
        for (Object o : msg) {
            Main.logger.put(o.toString());
            System.out.println(o);
        }
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

    public static Map<String, Proxy.Type> getHttpIp(int maxAttack) {
        return GetHttpIp.getHttpIp(maxAttack);
    }

    public static Map<String, Proxy.Type> getFileIp(int maxAttack) throws IOException {
        return GetFileIp.getFileIp(maxAttack);
    }

    public static Map<String, Proxy.Type> getALiKOMIIp(int maxAttack, Scanner sc) throws InterruptedException {
        return GetALiKOMIIp.getALiKOMIIp(maxAttack, sc);
    }

    public static boolean testProxy(String ip, int port, Proxy proxy) {
        try {
            return TestProxy.test(ip, port, proxy);
        } catch (IOException e) {
            e.printStackTrace();
            return true;
        }
    }

    protected static String getHttpReq(String url, String post, String type) {
        return HttpReq.sendPost(url, post, type);
    }
    public static <T> T getCo(String date, T def) {
        if (date.equals("")) {
            return def;
        }
        return (T) date;
    }
    public static int getCo (String date, int def) {
        if (date.equals("")) {
            return def;
        }
        return Integer.parseInt(date);

    }
}
