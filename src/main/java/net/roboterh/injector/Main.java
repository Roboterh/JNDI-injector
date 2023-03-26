package net.roboterh.injector;

import net.roboterh.injector.enums.GadgetEnum;
import net.roboterh.injector.forms.MainForm;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        if (args.length > 0) {
            logger.info("This is a GUI tools for JNDI injection!!\n" +
                    "Now, you could run 'java -jar JNDI-injector' to start it.");
        }
        MainForm.main();
//        for (GadgetEnum gadgetEnum : GadgetEnum.values()) {
//            System.out.println(gadgetEnum);
//        }
    }
}
