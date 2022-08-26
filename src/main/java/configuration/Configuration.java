package configuration;

import java.util.HashMap;

/**
 * @className: Configuration
 * @description: configuration class
 * @author: hone
 * @create: 2022/8/23 21:05
 */
public class Configuration {
    private final HashMap<String, Object> confData;

    /**
     * Creates a new empty configuration.
     */
    public Configuration() {
        this.confData = new HashMap<>();
    }

    /**
     * Adds the given key/value pair to the configuration object.
     *
     * @param key
     *        the key of the key/value pair to be added
     * @param value
     *        the value of the key/value pair to be added
     */
    public void setString(String key, String value) {
        setValueInternal(key, value);
    }

    <T> void setValueInternal(String key, T value) {
        if (key == null) {
            throw new NullPointerException("Key must not be null.");
        }
        if (value == null) {
            throw new NullPointerException("Value must not be null.");
        }

        synchronized (this.confData) {
            this.confData.put(key, value);
        }
    }
}
