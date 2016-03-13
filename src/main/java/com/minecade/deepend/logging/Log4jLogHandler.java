package com.minecade.deepend.logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.ConfigurationFactory;

/**
 * Created 3/12/2016 for Deepend
 *
 * @author Citymonstret
 */
public class Log4jLogHandler implements LogHandler<Logger, String> {

    // private final java.util.logging.Logger logger;
    private final org.apache.logging.log4j.Logger logger;
    private final Logger logImp;

    public Log4jLogHandler(String loggerName, Logger logger) {
        ConfigurationFactory.setConfigurationFactory(new LogFactory());
        this.logger = LogManager.getLogger(loggerName);
        this.logImp = logger;
    }

    @Override
    public Logger info(String message) {
        logger.info(message);
        return logImp;
    }

    @Override
    public Logger error(String message) {
        logger.error(message);
        return logImp;
    }

    @Override
    public Logger debug(String message) {
        logger.warn(message);
        return logImp;
    }
}
