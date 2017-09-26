package me.alikomi.endminecraft.tasks.others;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class GetMotdData {

    private String jsondata;
    private JSONObject main;
    private JSONObject version;
    private JSONObject players;
    private JSONObject modinfo;

    public GetMotdData(String returnjson) {
        this.jsondata = returnjson;
        main = JSON.parseObject(jsondata);
        if (jsondata == null) {
            return;
        }
        version = main.getJSONObject("version");
        players = main.getJSONObject("players");
        if (main.containsKey("modinfo")) {
            modinfo = main.getJSONObject("modinfo");
        }
    }

    public String getVersion() {
        if (jsondata == null) return null;
        return version.getString("name");
    }

    public int getOnlinePlayers() {
        try {
            if (jsondata == null) return 0;
            if (version.getString("name").contains("1.") || players.getInteger("online") != 0) {
                return players.getInteger("online");
            } else {
                String data = version.getString("name");
                //if (data.contains("/") || data.contains("\\") || data.contains(":"))
                if (data.contains("/")) {
                    String on = data.split("/")[0];
                    if (on.contains("§")) {
                        on = on.replaceAll("§.", "");
                    }
                    return Integer.parseInt(on);
                } else if (data.contains("\\")) {
                    String on = data.split("\\")[0];
                    if (on.contains("§")) {
                        on = on.replaceAll("§.", "");
                    }
                    return Integer.parseInt(on);
                } else if (data.contains(":")) {
                    String on = data.split(":")[0];
                    if (on.contains("§")) {
                        on = on.replaceAll("§.", "");
                    }
                    return Integer.parseInt(on);
                } else {
                    return 0;
                }
            }
        } catch (Exception e) {
            return 0;
        }
    }

    public int getMaxPlayers() {
        try {
            if (jsondata == null) return 0;
            if (version.getString("name").contains("1.") || players.getInteger("max") != 0) {
                return players.getInteger("max");
            } else {
                String data = version.getString("name");
                //if (data.contains("/") || data.contains("\\") || data.contains(":"))
                if (data.contains("/")) {
                    String on = data.split("/")[1];
                    if (on.contains("§")) {
                        on = on.replaceAll("§.", "");
                    }
                    return Integer.parseInt(on);
                } else if (data.contains("\\")) {
                    String on = data.split("\\")[1];
                    if (on.contains("§")) {
                        on = on.replaceAll("§.", "");
                    }
                    return Integer.parseInt(on);
                } else if (data.contains(":")) {
                    String on = data.split(":")[1];
                    if (on.contains("§")) {
                        on = on.replaceAll("§.", "");
                    }
                    return Integer.parseInt(on);
                } else {
                    return 0;
                }
            }
        } catch (Exception e) {
            return 0;
        }
    }

    public JSONObject getModinfo() {
        return modinfo;
    }
}
