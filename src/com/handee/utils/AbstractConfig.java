/**
 *
 */
package com.handee.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Properties;

/**
 * @author wang
 */
public abstract class AbstractConfig {
    static {
        System.setProperty("line.separator", "\r\n");
        System.setProperty("file.encoding", "UTF-8");
    }

    protected static final Logger _log = Logger.getLogger(AbstractConfig.class);


    protected AbstractConfig() {
        throw new InternalError();
    }

    private static final HandlerRegistry<String, ConfigLoader> _loaders = new HandlerRegistry<String, ConfigLoader>(true) {
        @Override
        public String standardizeKey(String key) {
            return key.trim().toLowerCase();
        }
    };

    protected static void registerConfig(ConfigLoader loader) {
        _loaders.register(loader.getName(), loader);
    }

    public static void loadConfigs() throws Exception {
        for (ConfigLoader loader : _loaders.getHandlers().values())
            loader.load();
    }

    public static String loadConfig(String name) throws Exception {
        final ConfigLoader loader = _loaders.get(name);

        if (loader == null)
            throw new Exception();

        try {
            loader.load();
            return "'" + loader.getFileName() + "' reloaded!";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public static String getLoaderNames() {
        return StringUtils.join(_loaders.getHandlers().keySet().iterator(), "|");
    }

    protected static abstract class ConfigLoader {
        protected abstract String getName();

        protected String getFileName() {
            return "./conf/" + getName().trim() + ".properties";
        }

        protected void load() throws Exception {
            _log.info("loading '" + getFileName() + "'");

            try {
                loadReader(new BufferedReader(new FileReader(getFileName())));
            } catch (Exception e) {
                _log.fatal("Failed to load '" + getFileName() + "'!", e);

                throw new Exception("Failed to load '" + getFileName() + "'!");
            }
        }

        protected void loadReader(BufferedReader reader) throws Exception {
            loadImpl(new HandeeProperties(reader));
        }

        protected void loadImpl(Properties properties) throws Exception {
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof ConfigLoader && getClass().equals(obj.getClass());
        }
    }
}
