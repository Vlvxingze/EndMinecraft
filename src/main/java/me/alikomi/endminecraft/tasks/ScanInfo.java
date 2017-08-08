package me.alikomi.endminecraft.tasks;

import ch.jamiete.mcping.MinecraftPing;
import ch.jamiete.mcping.MinecraftPingOptions;
import me.alikomi.endminecraft.utils.GetMotdData;
import me.alikomi.endminecraft.utils.Util;


import java.io.IOException;

public class ScanInfo extends Util {
    public static void ScanMotdInfo(String ip, int port) {
        String data = null;
        try {
            data = new MinecraftPing().getPing(new MinecraftPingOptions().setHostname(ip).setPort(port));//获取MOTD的json。
        } catch (IOException e) {
            e.printStackTrace();
        }
        log(data);
        GetMotdData motdData = new GetMotdData(data);
        log("版本： " + motdData.getVersion());
        log("在线人数： " + motdData.getOnlinePlayers());
        log("最大人数： " + motdData.getMaxPlayers());
    }

}