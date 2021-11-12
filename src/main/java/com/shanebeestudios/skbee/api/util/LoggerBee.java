package com.shanebeestudios.skbee.api.util;

import java.util.logging.Logger;

/**
 * A Logger wrapper
 */
public class LoggerBee extends Logger {

    protected LoggerBee(String name, String resourceBundleName) {
        super(name, resourceBundleName);
    }

    /** Get an instance of LoggerBee
     * @return new instance of LoggerBee
     */
    public static LoggerBee getLogger() {
        return new LoggerBee("", null);
    }

    @Override
    public void info(String msg) {
        String prefix = msg.replace("[NBTAPI]", "&7[&bNBT&3API&7]");
        Util.log(prefix);
    }

}
