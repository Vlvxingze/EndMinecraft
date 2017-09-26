package me.alikomi.endminecraft.data;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class InfoData {

    private String ip;
    private int port;
    private String serverVersion;
    private int maxPlayer;
    private int onlinePlayer;
    private String jsonData;
    private JSONObject modinfo;


    public InfoData(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public String toString() {

        StringBuilder stringBuilder = new StringBuilder("");
        stringBuilder.append("ip: ").append(ip);
        stringBuilder.append(System.getProperty("line.separator", "\n"));
        stringBuilder.append("port: ").append(port);
        stringBuilder.append(System.getProperty("line.separator", "\n"));
        stringBuilder.append("serverVersion: ").append(serverVersion);
        stringBuilder.append(System.getProperty("line.separator", "\n"));
        stringBuilder.append("maxPlayer: ").append(maxPlayer);
        stringBuilder.append(System.getProperty("line.separator", "\n"));
        stringBuilder.append("onlinePlayer: ").append(onlinePlayer);
        stringBuilder.append(System.getProperty("line.separator", "\n"));
        stringBuilder.append("jsonData: ").append(jsonData);
        stringBuilder.append(System.getProperty("line.separator", "\n"));

        return stringBuilder.toString();
    }
}
