package net.roboterh.injector.cli;

import net.roboterh.injector.servers.HTTPServer;
import net.roboterh.injector.servers.LdapServer;
import org.apache.commons.cli.*;
import org.apache.log4j.Category;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.concurrent.ExecutionException;

public class CliServerStart {
    // logger
    private static final Logger logger = LogManager.getLogger(CliServerStart.class);

    // default configuration
    public static String addr = "0.0.0.0";
    public static int ldapPort = 1389;
    public static int httpPort = 3456;

    // optional args
    public static String gadget = "";

    public static void run(String[] args) throws Exception {
        // parse argument
        DefaultParser parser = new DefaultParser();
        CommandLine cmd = null;
        Options options = null;

        try {
            options = cmdlineOptions();
            // parse
            cmd = parser.parse(options, args);
        } catch (Exception e) {
            logger.error("Cmdlines parse failed...");
            System.exit(1);
        }

        if (cmd.hasOption("help")) {
            printBasicUsage(options);
            return;
        }
        if (cmd.hasOption("address")) {
            addr = cmd.getOptionValue("address");
        }
        if (cmd.hasOption("ldapPort")) {
            ldapPort = Integer.parseInt(cmd.getOptionValue("ldapPort"));
        }
        if (cmd.hasOption("httpPort")) {
            httpPort = Integer.parseInt(cmd.getOptionValue("httpPort"));
        }
        if (cmd.hasOption("gadget")) {
            gadget = cmd.getOptionValue("gadget");
        }

        try {
            LdapServer ldapServer = new LdapServer(addr, ldapPort);
            HTTPServer httpServer = new HTTPServer(addr, httpPort);
            // start the ldap and http servers
            try {
                // ldap server
                ldapServer.startServer();
            } catch (Exception ex) {
                logger.error(String.format("Start ldap service wrong: %s", ex.getMessage()));
            }
            try {
                // http server
                httpServer.startServer();
            } catch (Exception ex) {
                logger.error(String.format("Start http service wrong: %s", ex.getMessage()));
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
        }

        logger.info("Cli service started...\r\nplease use the same LDAP Search Link as the GUI-version!!");
    }

    public static Options cmdlineOptions() {
        Options options = new Options();
        Option help = new Option("h", "help", false, "Show the help info.");
        options.addOption(help);
        Option address = new Option("A", "address", true, "The address of server.");
        options.addOption(address);
        Option ldapPort = new Option("LP", "ldapPort", true, "The port of ldap server.");
        options.addOption(ldapPort);
        Option httpPort = new Option("HP", "httpPort", true, "The port of http server.");
        options.addOption(httpPort);
        Option gadget = new Option("g", "gadget", true, "The gadget of jndi injection.");
        options.addOption(gadget);

        return options;
    }
    public static void printBasicUsage(Options options) {
        System.out.println();
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp("Learn more about JNDI-injector", options);
        System.out.println();
    }
}


