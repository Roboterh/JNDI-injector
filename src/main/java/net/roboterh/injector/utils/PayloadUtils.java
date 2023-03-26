package net.roboterh.injector.utils;

import cn.hutool.http.HttpUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.Base64;
import java.util.Random;

public class PayloadUtils {
    private Logger logger = LogManager.getLogger(PayloadUtils.class);

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
}
