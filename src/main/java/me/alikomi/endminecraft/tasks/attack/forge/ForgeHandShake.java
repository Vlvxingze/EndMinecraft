package me.alikomi.endminecraft.tasks.attack.forge;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.AllArgsConstructor;
import me.alikomi.endminecraft.utils.DefinedPacket;
import me.alikomi.endminecraft.utils.Util;
import org.spacehq.mc.protocol.packet.ingame.client.ClientPluginMessagePacket;
import org.spacehq.mc.protocol.packet.ingame.server.ServerPluginMessagePacket;
import org.spacehq.packetlib.Client;

import java.util.Map;

@AllArgsConstructor
public class ForgeHandShake extends Util {
    private final Client client;
    private final Map<String, String>modlist;

    private static boolean _3 = false;
    private static byte[] reg;

    public void start(ServerPluginMessagePacket packet) {
        byte[] data = packet.getData();
        //SendHello
        if (packet.getChannel().equals("FML|HS") && data.length > 2 && ((byte) 0) == data[0]) {
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
        }
        //SendACK
        if (packet.getChannel().equals("FML|HS") && data.length > 1 && ((byte) 2) == data[0]) {
            client.getSession().send(new ClientPluginMessagePacket("FML|HS", new byte[]{(byte) -1, 0x02}));
            _3 = true;
        }
        //Read RegistryData
        if (_3) {
            client.getSession().send(new ClientPluginMessagePacket("FML|HS", new byte[]{(byte) -1, 0x03}));
            _3 = false;
        }
        if (packet.getChannel().equals("REGISTER")) {
            reg = packet.getData();
        }
    }
}
