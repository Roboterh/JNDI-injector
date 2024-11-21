package net.roboterh.injector.servers;

import cn.hutool.core.io.file.FileReader;
import com.sun.net.httpserver.HttpExchange;
import net.roboterh.injector.utils.Cache;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.File;

public class HTTPHandler {
    private static final Logger logger = LogManager.getLogger(HTTPHandler.class);

    public static String cwd = System.getProperty("user.dir");

    public static void handleClassRequest(HttpExchange httpExchange) {
        try {
            // obtain the classname
            String path = httpExchange.getRequestURI().getPath();
            String className = path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("."));

            // obtain bytes from cache if exist
            if (Cache.contains(className)) {
                byte[] bytes = Cache.get(className);
                // send the data
                httpExchange.sendResponseHeaders(200, bytes.length);
                httpExchange.getResponseBody().write(bytes);
            } else {
                // not exist
                logger.info(String.format("Response code is %d ...", 404));
                httpExchange.sendResponseHeaders(404, 0);
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        httpExchange.close();
    }

    public static void handleJarRequest(HttpExchange httpExchange) {
        try {
            // obtain the JarName
            String path = httpExchange.getRequestURI().getPath();
            String jarName = path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("."));

            String filepath = cwd + File.separator + "data" + File.separator + jarName + ".jar";
            // obtain bytes
            if (jarName.equalsIgnoreCase("tomcatSnakeyaml")) {
                // predefined
                FileReader fileReader = new FileReader(filepath, "UTF-8");
                byte[] bytes = null;
                bytes = fileReader.readBytes();
                httpExchange.sendResponseHeaders(200, bytes.length);
                httpExchange.getResponseBody().write(bytes);
            } else {
                // customize
                File file = new File(filepath);
                if (file.exists()) {
                    // file exists
                    byte[] bytes = null;
                    FileReader fileReader = new FileReader(filepath, "UTF-8");
                    bytes = fileReader.readBytes();
                    httpExchange.sendResponseHeaders(200, bytes.length);
                    httpExchange.getResponseBody().write(bytes);
                } else {
                    // not exist
                    logger.info(String.format("The file %s don't exist", filepath));
                    httpExchange.sendResponseHeaders(404, 0);
                }
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        httpExchange.close();
    }

    public static void handleXmlRequest(HttpExchange httpExchange) {
        try {
            // obtain the xmlName
            String path = httpExchange.getRequestURI().getPath();
            String xmlName = path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("."));

            String filepath = cwd + File.separator + "data" + File.separator + xmlName + ".xml";
            // obtain bytes
            if (xmlName.equalsIgnoreCase("XXE")) {
                // predefined
                FileReader fileReader = new FileReader(filepath, "UTF-8");
                byte[] bytes = fileReader.readBytes();
                httpExchange.getResponseHeaders().add("Content-Type","application/xml; charset=utf-8");
                httpExchange.sendResponseHeaders(200, bytes.length + 1);
                httpExchange.getResponseBody().write(bytes);
            } else {
                // customize
                File file = new File(filepath);
                if (file.exists()) {
                    // file exists
                    FileReader fileReader = new FileReader(filepath, "UTF-8");
                    byte[] bytes = fileReader.readBytes();
                    httpExchange.getResponseHeaders().add("Content-Type","application/xml; charset=utf-8");
                    httpExchange.sendResponseHeaders(200, bytes.length + 1);
                    httpExchange.getResponseBody().write(bytes);
                } else {
                    // not exist
                    logger.info(String.format("The file %s don't exist", filepath));
                    httpExchange.sendResponseHeaders(404, 0);
                }
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        httpExchange.close();
    }

    public static void handleGroovyRequest(HttpExchange httpExchange) {
        try {
            // obtain the groovyName
            String path = httpExchange.getRequestURI().getPath();
            String groovyName = path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("."));

            String filepath = cwd + File.separator + "data" + File.separator + groovyName + ".groovy";
            // obtain bytes
            if (groovyName.equalsIgnoreCase("Groovy")) {
                // predefined
                FileReader fileReader = new FileReader(filepath, "UTF-8");
                byte[] bytes = fileReader.readBytes();
                httpExchange.sendResponseHeaders(200, bytes.length + 1);
                httpExchange.getResponseBody().write(bytes);
            } else {
                // customize
                File file = new File(filepath);
                if (file.exists()) {
                    // file exists
                    FileReader fileReader = new FileReader(filepath, "UTF-8");
                    byte[] bytes = fileReader.readBytes();
                    httpExchange.sendResponseHeaders(200, bytes.length + 1);
                    httpExchange.getResponseBody().write(bytes);
                } else {
                    // not exist
                    logger.info(String.format("The file %s don't exist ...", filepath));
                    httpExchange.sendResponseHeaders(404, 0);
                }
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        httpExchange.close();
    }

    public static void handleJavaScriptRequest(HttpExchange httpExchange) {
        try {
            // obtain the jsName
            String path = httpExchange.getRequestURI().getPath();
            String jsName = path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("."));

            String filePath = cwd + File.separator + "data" + File.separator + jsName + ".js";
            if (jsName.equalsIgnoreCase("JavaScript")) {
                // predefined
                FileReader fileReader = new FileReader(filePath, "UTF-8");
                byte[] bytes = fileReader.readBytes();
                httpExchange.sendResponseHeaders(200, bytes.length + 1);
                httpExchange.getResponseBody().write(bytes);
            } else {
                // customize
                File file = new File(filePath);
                if (file.exists()) {
                    // file exists
                    FileReader fileReader = new FileReader(filePath, "UTF-8");
                    byte[] bytes = fileReader.readBytes();
                    httpExchange.sendResponseHeaders(200, bytes.length + 1);
                    httpExchange.getResponseBody().write(bytes);
                } else {
                    // not exist
                    logger.info(String.format("The file %s don't exist ...", filePath));
                    httpExchange.sendResponseHeaders(404, 0);
                }
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        httpExchange.close();
    }

    public static void handleSqlRequest(HttpExchange httpExchange) {
        try {
            // obtain the sqlName
            String path = httpExchange.getRequestURI().getPath();
            String sqlName = path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("."));

            String filePath = cwd + File.separator + "data" + File.separator + sqlName + ".sql";
            if (sqlName.equalsIgnoreCase("SQL")) {
                // predefined
                FileReader fileReader = new FileReader(filePath, "UTF-8");
                byte[] bytes = fileReader.readBytes();
                httpExchange.sendResponseHeaders(200, bytes.length + 1);
                httpExchange.getResponseBody().write(bytes);
            } else {
                // customize
                File file = new File(filePath);
                if (file.exists()) {
                    // file exists
                    FileReader fileReader = new FileReader(filePath, "UTF-8");
                    byte[] bytes = fileReader.readBytes();
                    httpExchange.sendResponseHeaders(200, bytes.length + 1);
                    httpExchange.getResponseBody().write(bytes);
                } else {
                    // not exist
                    logger.info(String.format("The file %s don't exist ...", filePath));
                    httpExchange.sendResponseHeaders(404, 0);
                }
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        httpExchange.close();
    }

    public static void handlePropertiesRequest(HttpExchange httpExchange) {
        try {
            // obtain the properties fileName
            String path = httpExchange.getRequestURI().getPath();
            String PropertiesName = path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("."));

            String filePath = cwd + File.separator + "data" + File.separator + PropertiesName + ".properties";
            if (PropertiesName.equalsIgnoreCase("AttributionCovered")) {
                // predefined
                FileReader fileReader = new FileReader(filePath, "UTF-8");
                byte[] bytes = fileReader.readBytes();
                httpExchange.sendResponseHeaders(200, bytes.length + 1);
                httpExchange.getResponseBody().write(bytes);
            } else {
                // customize
                File file = new File(filePath);
                if (file.exists()) {
                    // file exists
                    FileReader fileReader = new FileReader(filePath, "UTF-8");
                    byte[] bytes = fileReader.readBytes();
                    httpExchange.sendResponseHeaders(200, bytes.length + 1);
                    httpExchange.getResponseBody().write(bytes);
                } else {
                    // not exist
                    logger.info(String.format("The file %s don't exist ...", filePath));
                    httpExchange.sendResponseHeaders(404, 0);
                }
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        httpExchange.close();
    }

    public static void handleYamlRequest(HttpExchange httpExchange) {
        try {
            // obtain the yaml fileName
            String path = httpExchange.getRequestURI().getPath();
            String PropertiesName = path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("."));

            String filePath = cwd + File.separator + "data" + File.separator + PropertiesName + ".yaml";
            if (PropertiesName.equalsIgnoreCase("SnakeYaml")) {
                // predefined
                FileReader fileReader = new FileReader(filePath, "UTF-8");
                byte[] bytes = fileReader.readBytes();
                httpExchange.sendResponseHeaders(200, bytes.length + 1);
                httpExchange.getResponseBody().write(bytes);
            } else {
                // customize
                File file = new File(filePath);
                if (file.exists()) {
                    // file exists
                    FileReader fileReader = new FileReader(filePath, "UTF-8");
                    byte[] bytes = fileReader.readBytes();
                    httpExchange.sendResponseHeaders(200, bytes.length + 1);
                    httpExchange.getResponseBody().write(bytes);
                } else {
                    // not exist
                    logger.info(String.format("The file %s don't exist ...", filePath));
                    httpExchange.sendResponseHeaders(404, 0);
                }
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        httpExchange.close();
    }

    public static void handleTxtRequest(HttpExchange httpExchange) {
        try {
            // obtain the txt fileName
            String path = httpExchange.getRequestURI().getPath();
            String PropertiesName = path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("."));

            String filePath = cwd + File.separator + "data" + File.separator + PropertiesName + ".txt";
            // customize
            File file = new File(filePath);
            if (file.exists()) {
                // file exists
                FileReader fileReader = new FileReader(filePath, "UTF-8");
                byte[] bytes = fileReader.readBytes();
                httpExchange.sendResponseHeaders(200, bytes.length + 1);
                httpExchange.getResponseBody().write(bytes);
            } else {
                // not exist
                logger.info(String.format("The file %s don't exist ...", filePath));
                httpExchange.sendResponseHeaders(404, 0);
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        httpExchange.close();
    }

    public static void handleOtherRequest(HttpExchange httpExchange) {
        try {
            // obtain the fileName
            String path = httpExchange.getRequestURI().getPath();
            String PropertiesName = path.substring(path.lastIndexOf("/") + 1);

            String filePath = cwd + File.separator + "data" + File.separator + PropertiesName;
            // customize
            File file = new File(filePath);
            if (file.exists()) {
                // file exists
                FileReader fileReader = new FileReader(filePath, "UTF-8");
                byte[] bytes = fileReader.readBytes();
                httpExchange.sendResponseHeaders(200, bytes.length + 1);
                httpExchange.getResponseBody().write(bytes);
            } else {
                // not exist
                logger.info(String.format("The file %s don't exist ...", filePath));
                httpExchange.sendResponseHeaders(404, 0);
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        httpExchange.close();
    }
}
