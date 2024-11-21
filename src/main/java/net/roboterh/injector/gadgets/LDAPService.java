package net.roboterh.injector.gadgets;

import com.unboundid.ldap.listener.interceptor.InMemoryInterceptedSearchResult;

public interface LDAPService {
    /**
     * send result
     * @param result
     * @param baseDN
     */
    void sendResult(InMemoryInterceptedSearchResult result, String baseDN);

    /**
     * add args for payload
     * @param baseDN
     */
    void argsHandler(String baseDN);

    /**
     * generate the particular bytecode and return specific classname
     */
    void generateHandler();
}
