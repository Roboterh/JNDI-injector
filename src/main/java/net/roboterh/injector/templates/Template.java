package net.roboterh.injector.templates;

public interface Template {
    /**
     * get the className
     * @return className
     */
    String getClassName();

    /**
     * add the cache
     */
    void addCache();

    /**
     * generate the bytecodes
     */
    void generate();

    /**
     * get the bytes
     * @return bytes
     */
    byte[] getBytes();
}
