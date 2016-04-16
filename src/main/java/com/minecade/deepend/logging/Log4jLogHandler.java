package com.minecade.deepend.logging;

import lombok.NonNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.ConfigurationFactory;

/**
 * Created 3/12/2016 for Deepend
 *
 * @author Citymonstret
 */
class Log4jLogHandler implements LogHandler<Logger, String> {

    // private final java.util.logging.Logger logger;
    private final org.apache.logging.log4j.Logger logger;
    private final Logger logImp;

    public Log4jLogHandler(@NonNull final String loggerName, @NonNull final Logger logger) {
        ConfigurationFactory.setConfigurationFactory(new LogFactory());
        this.logger = LogManager.getLogger(loggerName);
        this.logImp = logger;
    }

    @Override
    public Logger info(@NonNull final String message) {
        this.logger.info(message);
        return this.logImp;
    }

    @Override
    public Logger error(@NonNull final String message) {
        this.logger.error(message);
        return this.logImp;
    }

    @Override
    public Logger debug(@NonNull final String message) {
        this.logger.debug(message);
        return this.logImp;
    }
}
