package me.alikomi.endminecraft.tasks.attack.forge;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.AllArgsConstructor;
import me.alikomi.endminecraft.tasks.attack.anticheat3_4_3.AntiCheatPack;
import me.alikomi.endminecraft.utils.DefinedPacket;
import me.alikomi.endminecraft.utils.Util;
import org.spacehq.mc.protocol.packet.ingame.client.ClientPluginMessagePacket;
import org.spacehq.mc.protocol.packet.ingame.server.ServerPluginMessagePacket;
import org.spacehq.packetlib.Client;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Map;

@AllArgsConstructor
public class ForgeHandShake extends Util {
    private final Client client;
    private final Map<String, String> modlist;

    private static boolean _3 = false;
    private static byte[] reg;

    public void start(ServerPluginMessagePacket packet) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, NoSuchFieldException {
        log(packet.getChannel());
        byte[] data = packet.getData();
        log(Arrays.toString(data));
        log(new String(data));

        if (packet.getChannel().equalsIgnoreCase("AntiCheat3.4.3")) {
            client.getSession().send(new ClientPluginMessagePacket("AntiCheat3.4.3", AntiCheatPack.acPackmk(AntiCheatPack.jy(packet.getData()))));
        }

        if (!packet.getChannel().contains("FML") && !packet.getChannel().contains("REGISTER")) return;
        //SendHello
        if (packet.getChannel().equals("FML|HS") && data.length > 2 && ((byte) 0) == data[0]) {
            log("SENDHELLO");
            client.getSession().send(new ClientPluginMessagePacket("REGISTER", reg));
            client.getSession().send(new ClientPluginMessagePacket("FML|HS", new byte[]{0x01, 0x02}));
            ByteBuf bf = Unpooled.buffer();
            DefinedPacket.writeVarInt(2, bf);
            bf.writeByte(modlist.size());
            modlist.forEach((k, v) -> {
                DefinedPacket.writeString(k, bf);
                DefinedPacket.writeString(v, bf);
            });

            client.getSession().send(new ClientPluginMessagePacket("FML|HS", fb(bf.array())));
            return;
        }
        //SendACK
        if (packet.getChannel().equals("FML|HS") && data.length > 1 && ((byte) 2) == data[0]) {
            log("SENDACK-2");
            client.getSession().send(new ClientPluginMessagePacket("FML|HS", new byte[]{(byte) -1, 0x02}));
            _3 = true;
            return;
        }
        //Read RegistryData
        if (_3) {
            log("SENDACK-3");
            client.getSession().send(new ClientPluginMessagePacket("FML|HS", new byte[]{(byte) -1, 0x03}));
            _3 = false;
            return;
        }
        if (packet.getChannel().equals("REGISTER")) {
            reg = packet.getData();
        }
    }
}
