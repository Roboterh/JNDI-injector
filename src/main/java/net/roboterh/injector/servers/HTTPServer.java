package net.roboterh.injector.servers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;

import static net.roboterh.injector.servers.HTTPHandler.*;


public class HTTPServer {
    public static void main(String[] args) {
        HTTPServer httpServer = new HTTPServer("127.0.0.1", 8000);
        httpServer.startServer();
    }
    private static final Logger logger = LogManager.getLogger(HTTPServer.class);

    public static String codeBase;
    private String ip;
    private int port;

    public HTTPServer(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public void startServer() {
        // build the http server
        try {
            HttpServer httpServer = HttpServer.create(new InetSocketAddress(ip, port), 0);
            httpServer.createContext("/", new HttpHandler() {
                @Override
                public void handle(HttpExchange httpExchange) throws IOException {
                    logger.info(String.format("Received http request from %s and the URI is %s ...", httpExchange.getRemoteAddress(), httpExchange.getRequestURI()));

                    // obtain different resources
                    String path = httpExchange.getRequestURI().getPath();
                    if (path.endsWith(".class")) {
                        handleClassRequest(httpExchange);
                    } else if (path.endsWith(".jar")) {
                        handleJarRequest(httpExchange);
                    } else if (path.endsWith(".xml")) {
                        handleXmlRequest(httpExchange);
                    } else if (path.endsWith(".groovy")) {
                        handleGroovyRequest(httpExchange);
                    } else if (path.endsWith(".js")) {
                        handleJavaScriptRequest(httpExchange);
                    } else if (path.endsWith(".sql")) {
                        handleSqlRequest(httpExchange);
                    } else if (path.endsWith(".properties")) {
                        handlePropertiesRequest(httpExchange);
                    } else if (path.endsWith(".yaml")) {
                        handleYamlRequest(httpExchange);
                    } else if (path.endsWith(".txt")) {
                        handleTxtRequest(httpExchange);
                    } else {
                        handleOtherRequest(httpExchange);
                    }
                }
            });

            httpServer.setExecutor(null);
            httpServer.start();
            logger.info(String.format("HTTP server has been listening on %d ...", port));
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        // generate codebase
        codeBase = "http://" + getIp() + ":" + getPort() + "/";
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }
}
