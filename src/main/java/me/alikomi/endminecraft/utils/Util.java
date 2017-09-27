package me.alikomi.endminecraft.utils;

import me.alikomi.endminecraft.Main;
import me.alikomi.endminecraft.tasks.others.GetALiKOMIIp;
import me.alikomi.endminecraft.tasks.others.GetFileIp;
import me.alikomi.endminecraft.tasks.others.GetHttpIp;
import me.alikomi.endminecraft.tasks.others.TestProxy;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.util.Map;
import java.util.Random;
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

    public static String getRandomString(int length) {
        String str = "_abcde_fghijk_lmnopqrst_uvw_xyzABCD_EFGHIJKLM_NOPQRSTUVWXY_Z012345_6789_";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; ++i) {
            int number = random.nextInt(72);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    private static byte[] subBytes(byte[] src, int begin, int count) {
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

    static Map<String, Proxy.Type> getHttpIp(int maxAttack) {
        return GetHttpIp.getHttpIp(maxAttack);
    }

    static Map<String, Proxy.Type> getFileIp(int maxAttack) throws IOException {
        return GetFileIp.getFileIp(maxAttack);
    }

    static Map<String, Proxy.Type> getALiKOMIIp(int maxAttack, Scanner sc) throws InterruptedException {
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
    static <T> T getCo(String date, T def) {
        if (date.equals("")) {
            return def;
        }
        return (T) date;
    }
    protected static int getCo(String date, int def) {
        if (date.equals("")) {
            return def;
        }
        return Integer.parseInt(date);
    }

    protected void getMotd(Proxy proxy, String ip, int port) {
        try {
            final Socket socket = new Socket(proxy);
            socket.connect(new InetSocketAddress(ip, port));
            if (socket.isConnected() && !socket.isClosed()) {
                final OutputStream out = socket.getOutputStream();
                out.write(MinecraftPackets.MOTD_HEAD_PACK);
                out.flush();
                out.write(MinecraftPackets.MOTD_GET_PACK);
                out.flush();
                out.close();
            }
            socket.close();
        } catch (final Exception e) {
            log("异常抛出！");
        }
    }
}
