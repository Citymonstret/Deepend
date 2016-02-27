package com.minecade.deepend.logging;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;

import java.net.URI;

/**
 * Created 2/27/2016 for Deepend
 *
 * @author Citymonstret
 */
public class LogFactory extends ConfigurationFactory {

    final static String COLOR_STYLES = "{INFO=cyan bold bright, WARN=yellow bold, TRACE=blue, ERROR=red bright bold, DEBUG=magenta bold}";

    static String style(String s, String ss) {
        return "%highlight{" + s + "}{INFO=" + ss + ", WARN=" + ss + ", TRACE=" + ss + ", ERROR=" + ss + ", DEBUG=" + ss + "}";
    }

    static Configuration createConfiguration(final String name, ConfigurationBuilder<BuiltConfiguration> builder) {
        builder.setConfigurationName(name);
        builder.setStatusLevel(Level.ERROR);

        String pattern = "[" + style("%d{HH:mm:ss.SSS}", "white") +
                "][%highlight{%level}" + COLOR_STYLES + "][" +
                style("%logger{36}", "white") + "][" + style("%t", "cyan") + "]: " +
                style("%msg", "bright") + "%n";

        AppenderComponentBuilder appenderComponentBuilder = builder.newAppender("Stdout", "CONSOLE")
                .addAttribute("target", ConsoleAppender.Target.SYSTEM_OUT);
        appenderComponentBuilder.add(builder.newLayout("PatternLayout")
                .addAttribute("pattern", pattern));

        builder.add(appenderComponentBuilder);

        builder.add(builder.newLogger("Deepend", Level.ALL)
                .add(builder.newAppenderRef("Stdout"))
                .addAttribute("additivity", false));

        builder.add(builder.newRootLogger(Level.ALL).add(builder.newAppenderRef("Stdout")));

        return builder.build();
    }

    @Override
    protected String[] getSupportedTypes() {
        return new String[] {"*"};
    }

    @Override
    public Configuration getConfiguration(final String name, final URI configurationLocation) {
        return createConfiguration(name, newConfigurationBuilder());
    }

    @Override
    public Configuration getConfiguration(ConfigurationSource source) {
        return createConfiguration(source.toString(), null);
    }
}
