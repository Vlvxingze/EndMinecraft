package me.alikomi.endminecraft.log;

import me.alikomi.endminecraft.Main;

import java.io.*;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Loger {
    File f;
    private StringBuilder sb = new StringBuilder("");
    private Lock sbLock = new ReentrantLock();
    String lineSeparator = System.getProperty("line.separator", "\n");

    public Loger(File f) {
        this.f = f;
        if (! f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        put("时间戳：" + System.currentTimeMillis());
        put("当前系统：" + System.getProperty("os.name").toLowerCase(Locale.US));
        put("当前系统位数：" + System.getProperty("os.arch").toLowerCase(Locale.US));
        put("当前系统版本" + System.getProperty("os.version").toLowerCase(Locale.US));
        write();
    }
    public void put (String log) {
        sbLock.lock();
        sb.append(log);
        sb.append(lineSeparator);
        sbLock.unlock();
    }
    public void write() {
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (!f.exists() || !f.canWrite()) {
                    System.exit(100);
                }

                sbLock.lock();
                final String st = sb.toString();
                sbLock.unlock();
                try {
                    BufferedWriter bw = new BufferedWriter(new FileWriter(f));
                    bw.write(st);
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
