package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Loads framework configuration from src/main/resources/config.properties.
 * System properties (e.g. -Dbrowser=firefox passed by CI or the command line)
 * always take priority over the file, so nothing needs to be hardcoded and
 * nothing needs to be edited to change environments.
 */
public class ConfigReader {

    private static final String CONFIG_PATH = "src/main/resources/config.properties";
    private static Properties properties;

    static {
        try (FileInputStream fis = new FileInputStream(CONFIG_PATH)) {
            properties = new Properties();
            properties.load(fis);
        } catch (IOException e) {
            throw new RuntimeException("Unable to load config.properties at " + CONFIG_PATH, e);
        }
    }

    private ConfigReader() {
        // utility class
    }

    public static String get(String key) {
        // System property (e.g. mvn test -Dbrowser=firefox) wins over the file.
        String sysProp = System.getProperty(key);
        if (sysProp != null && !sysProp.isEmpty()) {
            return sysProp;
        }
        String envVar = System.getenv(key.toUpperCase().replace('.', '_'));
        if (envVar != null && !envVar.isEmpty()) {
            return envVar;
        }
        String value = properties.getProperty(key);
        if (value == null) {
            throw new RuntimeException("Missing key in config.properties: " + key);
        }
        return value;
    }

    public static String get(String key, String defaultValue) {
        try {
            return get(key);
        } catch (RuntimeException e) {
            return defaultValue;
        }
    }

    public static int getInt(String key) {
        return Integer.parseInt(get(key));
    }

    public static boolean getBoolean(String key) {
        return Boolean.parseBoolean(get(key));
    }
}
