package me.alikomi.endminecraft.tasks.attack.forge;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import me.alikomi.endminecraft.Main;
import me.alikomi.endminecraft.utils.Util;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public class GetModList extends Util{
    private String ip;
    private int port;

    public Map<String,String> get() {
        Map<String,String> list;
        list = get(1);
        if (list == null) get(2);
        return list;
    }
    public Map<String,String> get(int id) {
        switch (id) {
            case 1: {
                log("开始get");
                if (Main.infoData.getModinfo() == null ||! Main.infoData.getModinfo().getJSONArray("modList").toString().contains("Forge")) {
                    return null;
                }
                Map<String,String> list = new HashMap<>();
                Main.infoData.getModinfo().getJSONArray("modList").forEach((v) -> {
                    JSONObject jb = (JSONObject) v;
                    list.put(jb.getString("modid"), jb.getString("version"));
                });
                return list;
            }
        }
        return null;
    }
}
