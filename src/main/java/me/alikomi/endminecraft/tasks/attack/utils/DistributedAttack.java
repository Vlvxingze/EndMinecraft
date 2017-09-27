package me.alikomi.endminecraft.tasks.attack.utils;

import org.spacehq.packetlib.Client;

import java.net.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;

public interface DistributedAttack {
    
    public void setAttack(boolean sattack);

    public void  setConnects(HashMap<String, Client> con);

    public String getIp();

    public int getPort();

    public long getTime();

    public int getSleepTime();

    public Map<String, Proxy.Type> getIps();

    public boolean isTab();

    public boolean isLele();

    public boolean isAttack();

    public HashMap<String, Client> getConnects();

    public Lock  getLock();

    public List<String> getIpsKey();




    public Client start(Proxy.Type tp, String po);

    public void startAttack() throws InterruptedException;
}
