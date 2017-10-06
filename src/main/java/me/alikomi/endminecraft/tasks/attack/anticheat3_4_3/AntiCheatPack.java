package me.alikomi.endminecraft.tasks.attack.anticheat3_4_3;

import me.alikomi.endminecraft.utils.Util;
import net.saralab.anticheat.Class5;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class AntiCheatPack extends Util {
    public static String jy(byte[] pack) throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        String sv = acPackre(pack).replace(("\b"), "").trim();
        Class5 antiCheatMod = new Class5();
        HashSet<String> hashSet = new HashSet<>();
        hashSet.add("AntiCheat.jar");
        Field md5list = antiCheatMod.getClass().getDeclaredField("Field14");
        md5list.setAccessible(true);
        md5list.set(null, hashSet);
        Field A = antiCheatMod.getClass().getDeclaredField("Field12");
        A.setAccessible(true);
        A.set(null, sv);
        Method method = antiCheatMod.getClass().getDeclaredMethod("Method40", Object.class);
        if (!method.isAccessible()) method.setAccessible(true);
        Object jym = method.invoke(antiCheatMod, hashSet);
        if (jym == null) {
            log("NULL！");
        }
        log(jym);
        return (String) jym;
    }

    public static String acPackre(byte[] a) {
        if (a == null || a.length == 0) {
            return null;
        }
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(a);
        try {
            final GZIPInputStream gzipInputStream = new GZIPInputStream(byteArrayInputStream);
            final byte[] array = new byte[256];
            int read;
            while ((read = gzipInputStream.read(array)) >= 0) {
                byteArrayOutputStream.write(array, 0, read);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return byteArrayOutputStream.toString();
    }

    public static byte[] acPackmk(final String a) {
        if (a == null || a.length() == 0) {
            return null;
        }
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ByteArrayOutputStream byteArrayOutputStream2;
        try {
            final GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
            gzipOutputStream.write(a.getBytes("UTF-8"));
            gzipOutputStream.close();
            byteArrayOutputStream2 = byteArrayOutputStream;
        } catch (IOException ex) {
            byteArrayOutputStream2 = byteArrayOutputStream;
            ex.printStackTrace();
        }
        return byteArrayOutputStream2.toByteArray();
    }
}
