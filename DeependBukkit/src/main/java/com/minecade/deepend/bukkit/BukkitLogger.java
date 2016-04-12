package com.minecade.deepend.bukkit;

import com.minecade.deepend.logging.LogHandler;
import com.minecade.deepend.logging.Logger;

/**
 * Created 3/12/2016 for Deepend
 *
 * @author Citymonstret
 */
class BukkitLogger implements LogHandler<Logger, String> {

    private Logger instance;

    @SuppressWarnings("unused")
    public BukkitLogger(String name, Logger instance) {
        this.instance = instance;
    }

    @Override
    public Logger info(String message) {
        DeependBukkit.logger.info(message);
        return instance;
    }

    @Override
    public Logger error(String message) {
        DeependBukkit.logger.severe(message);
        return instance;
    }

    @Override
    public Logger debug(String message) {
        DeependBukkit.logger.warning(message);
        return instance;
    }

}
