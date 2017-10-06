package me.alikomi.endminecraft;

import javassist.*;
import me.alikomi.endminecraft.data.BugData;
import me.alikomi.endminecraft.data.InfoData;
import me.alikomi.endminecraft.log.Loger;
import me.alikomi.endminecraft.tasks.scan.ScanBug;
import me.alikomi.endminecraft.tasks.scan.ScanInfo;
import me.alikomi.endminecraft.utils.Menu;
import me.alikomi.endminecraft.utils.Util;
import org.spacehq.mc.protocol.ProtocolConstants;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.InitialDirContext;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Scanner;

public class Main extends Util {

    public static BugData bugData = null;
    public static InfoData infoData = null;
    public static Loger logger;

    private static String ip;
    public static int port = 25565;

    private static Scanner scanner = new Scanner(System.in);


    public static void main(String[] args) throws InterruptedException, IOException, IllegalAccessException, InstantiationException, NoSuchFieldException, NoSuchMethodException, InvocationTargetException, NotFoundException, CannotCompileException, ClassNotFoundException, NamingException {
        if (!new File("log").exists()) new File("log").mkdir();
        logger = new Loger(new File("log/" + System.currentTimeMillis() + ".log"));
        getInfo();
        scanServer();
        scanBug();
        showMenu();
    }

    private static void init() throws NotFoundException, CannotCompileException, ClassNotFoundException {
        if (ProtocolConstants.GAME_VERSION.equalsIgnoreCase("1.7.7")) {
            ClassPool classPool = new ClassPool(true);
            CtClass ctClass = classPool.get("org.spacehq.packetlib.io.NetInput");
            ctClass.addMethod(CtNewMethod.copy(ctClass.getDeclaredMethod("readVarInt"), "readVarShort", ctClass, null));
            ctClass.toClass();


            ctClass = classPool.get("org.spacehq.packetlib.io.stream.StreamNetInput");
            CtMethod getvars = CtNewMethod.copy(ctClass.getDeclaredMethod("readVarInt"), "readVarShort", ctClass, null);
            getvars.setBody("{\n" +
                    "        int low = readUnsignedShort();\n" +
                    "        int high = 0;\n" +
                    "        if ((low & 0x8000) != 0 ) {\n" +
                    "            low = low & 0x7FFF;\n" +
                    "            high = readUnsignedByte();\n" +
                    "        }\n" +
                    "        return ((high & 0xFF) << 15) | low;\n" +
                    "    }");
            ctClass.addMethod(getvars);
            ctClass.toClass();

            ctClass = classPool.get("org.spacehq.packetlib.tcp.io.ByteBufNetInput");
            CtMethod getvars1 = CtNewMethod.copy(ctClass.getDeclaredMethod("readVarInt"), "readVarShort", ctClass, null);
            getvars1.setBody("{\n" +
                    "        int low = readUnsignedShort();\n" +
                    "        int high = 0;\n" +
                    "        if ((low & 0x8000) != 0 ) {\n" +
                    "            low = low & 0x7FFF;\n" +
                    "            high = readUnsignedByte();\n" +
                    "        }\n" +
                    "        return ((high & 0xFF) << 15) | low;\n" +
                    "    }");
            ctClass.addMethod(getvars1);
            ctClass.toClass();

            ctClass = classPool.get("org.spacehq.mc.protocol.packet.ingame.server.ServerPluginMessagePacket");
            String method_old_name = "read";
            CtMethod method_old = ctClass.getDeclaredMethod(method_old_name);
            String method_new_name = method_old_name + "$impl";
            method_old.setName(method_new_name);
            CtMethod method_new = CtNewMethod.copy(method_old, method_old_name, ctClass, null);
            StringBuilder code = new StringBuilder();
            code.append("{");
            code.append("    this.channel = $1.readString();");
            code.append("    int l;");
            code.append("    if (channel.contains(\"FML\") || channel.contains(\"FORGE\")) {");
            code.append("       l = $1.readVarShort();");
            code.append("    } else {");
            code.append("       l = $1.readShort();");
            code.append("    }");
            code.append("    this.data = $1.readBytes(l);");
            code.append("    System.out.println(\"ch:\"+channel);");
            code.append("}");
            method_new.setBody(code.toString());
            ctClass.addMethod(method_new);
            ctClass.toClass();
            System.out.println("动态替换字节码完成！");
        }
    }

    private static void getInfo() throws NamingException {
        log("欢迎使用EndMinecraft压测程序", "", "官方QQ群： 473516200", "=======================");
        log("请输入ip地址");
        ip = scanner.nextLine();
        if (ip.contains(":")) {
            String[] tmpip = ip.split(":");
            ip = tmpip[0];
            port = Integer.parseInt(tmpip[1]);
        } else {
            log("请输入端口(25565)");
            port = getCo(scanner.nextLine(), 25565);
        }
        Hashtable hashtable = new Hashtable();
        hashtable.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
        hashtable.put("java.naming.provider.url", "dns:");
        try {
            Attribute qwqre = (new InitialDirContext(hashtable)).getAttributes((new StringBuilder()).append("_Minecraft._tcp.").append(ip).toString(), new String[]{
                    "SRV"
            }).get("srv");
            if (qwqre != null) {
                String[] re = qwqre.get().toString().split(" ", 4);
                log("检测到SRV记录，自动跳转到SRV记录");
                ip = re[3];
                log("ip: " + ip);
                port = Integer.parseInt(re[2]);
                log("port: " + port);
            }
        } catch (Exception e) {

        }
        infoData = new InfoData(ip, port);
    }

    private static void scanServer() {
        log("正在探测服务器信息，请稍后");
        ScanInfo.ScanMotdInfo(ip, port);
    }

    private static void scanBug() {
        log("是否开始进服前漏洞探测y/n");
        if ("y".equalsIgnoreCase(scanner.nextLine())) {
            bugData = new BugData(ip, port);
            ScanBug.scanMOTD(ip, port);
            ScanBug.scanTAB(ip, port);
            log("漏洞检测结果： ", bugData.toString());
        }
    }

    private static void showMenu() throws IOException, InterruptedException {
        Menu menu = new Menu(scanner, ip, port);
        while (true) {
            log("请输入攻击方式：", "1 : MOTD攻击", "2 : 分布式假人攻击(集群压测)", "3 : 单ip，TAB压测", "4 : 分布式Forge协议假人攻击(集群压测)");
            log("========================");
            switch (getCo(scanner.nextLine(), 2)) {
                case 1: {
                    menu._1();
                    break;
                }
                case 2: {
                    menu._2();
                    break;
                }
                case 3: {
                    menu._3();
                    break;
                }
                case 4: {
                    menu._4();
                    break;
                }
                default: {
                    log("您的选择有误，请重新选择");
                    break;
                }
            }
        }
    }
}