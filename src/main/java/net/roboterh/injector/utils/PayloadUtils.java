package net.roboterh.injector.utils;

import cn.hutool.http.HttpUtil;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Base64;
import java.util.Random;

public class PayloadUtils {
    private static Logger logger = LogManager.getLogger(PayloadUtils.class);

    // get command from BaseDN
    public static String getCmdFromBase(String baseDN) {
        String cmd = baseDN.substring(baseDN.lastIndexOf("/") + 1);
        if (baseDN.toLowerCase().contains("base64")) {
            // command by base64 encoded
            try {
                byte[] bytes = Base64.getDecoder().decode(cmd);
                return new String(bytes);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return cmd;
    }

    // get ip and port used reverseShell from base
    public static String[] getIpAndPortFromBase(String baseDN) {
        int firstIndex = baseDN.indexOf("/");
        int secondIndex = baseDN.indexOf("/", firstIndex + 1);
        int thirdIndex = baseDN.lastIndexOf("/");
        String ip = baseDN.substring(secondIndex + 1, thirdIndex);
        String port = baseDN.substring(thirdIndex + 1);

        return new String[]{ip, port};
    }

    // generate random strings
    public static String getRandomString() {
        final String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        final int length = 3;
        // build a StringBuilder object
        StringBuilder builder = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            builder.append(characters.charAt(random.nextInt(characters.length())));
        }
        return builder.toString();
    }

    // base64Encode
    public static String base64Encode(byte[] bytes) throws Exception{
        String result;

        try{
            Class clazz = Class.forName("java.util.Base64");
            Method method = clazz.getDeclaredMethod("getEncoder");
            Object obj = method.invoke(null);
            method = obj.getClass().getDeclaredMethod("encodeToString", byte[].class);
            obj = method.invoke(obj, bytes);
            result = (String)obj;
        }catch(ClassNotFoundException e){
            Class clazz = Class.forName("sun.misc.BASE64Encoder");
            Method method = clazz.getMethod("encodeBuffer", byte[].class);
            Object obj = method.invoke(clazz.newInstance(), bytes);
            result = (String)obj;
            result = result.replaceAll("\r|\n|\r\n", "");
        }

        return result;
    }

    // base64Decode
    public static byte[] base64Decode(String str) throws Exception{
        byte[] bytes;

        try{
            Class clazz = Class.forName("java.util.Base64");
            Method method = clazz.getDeclaredMethod("getDecoder");
            Object obj = method.invoke(null);
            method = obj.getClass().getDeclaredMethod("decode", String.class);
            obj = method.invoke(obj, str);
            bytes = (byte[]) obj;
        }catch(ClassNotFoundException e){
            Class clazz = Class.forName("sun.misc.BASE64Decoder");
            Method method = clazz.getMethod("decodeBuffer", String.class);
            Object obj = method.invoke(clazz.newInstance(), str);
            bytes = (byte[]) obj;
        }

        return bytes;
    }

    public static String rebuildClass(Class clazz, String... args) {
        try {
            // todo: 动态修改内存马模板中的路由和参数等
//            // 加载要修改的类
//            ClassPool pool = ClassPool.getDefault();
//            CtClass ctClass = pool.get(clazz.getName());
//            // 找到要修改的静态变量
//            CtField field = ctClass.getDeclaredField("cmdHeader");
//            // 修改变量的值
//            field.set(null, "test");
//            // 将修改后的类写入文件
//            byte[] bytecode = ctClass.toBytecode();
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        return null;
    }



    public static String getClassCode(Class clazz) throws Exception {
        byte[] bytes = getClassBytes(clazz);
        String result = base64Encode(bytes);

        return result;
    }

    public static byte[] getClassBytes(Class clazz) throws Exception {
        String className = clazz.getName();
        String resoucePath = className.replaceAll("\\.", "/") + ".class";
        InputStream in = Util.class.getProtectionDomain().getClassLoader().getResourceAsStream(resoucePath);
        byte[] bytes = new byte[1024];
        ByteArrayOutputStream baous = new ByteArrayOutputStream();
        int len = 0;
        while((len = in.read(bytes)) != -1){
            baous.write(bytes, 0 , len);
        }

        in.close();
        baous.close();

        return baous.toByteArray();
    }

}
