package configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * @className: GlobalConfiguration
 * @description:
 * @author: hone
 * @create: 2022/8/23 20:43
 */
public class GlobalConfiguration {
    private static final String CONF_FILENAME = "conf.yaml";

    private static final Logger LOG = LoggerFactory.getLogger(GlobalConfiguration.class);

    // the hidden content to be displayed
    public static final String HIDDEN_CONTENT = "******";

    // the keys whose values should be hidden
    private static final String[] SENSITIVE_KEYS = new String[] {"password", "secret", "fs.azure.account.key"};

    public static Configuration loadConfiguration(final String configDir) {
//        if (configDir == null) {
//            throw new IllegalArgumentException("Given configuration directory is null, cannot load configuration");
//        }
        final File confDirFile = new File(configDir);

        // get Flink yaml configuration file
        final File yamlConfigFile = new File(confDirFile, CONF_FILENAME);

        if (!yamlConfigFile.exists()) {
            throw new RuntimeException(
                    "The config file '" + yamlConfigFile +
                            "' (" + confDirFile.getAbsolutePath() + ") does not exist.");
        }
        Configuration configuration = loadYAMLResource(yamlConfigFile);
        return configuration;
    }

    private static Configuration loadYAMLResource(File file) {
        final Configuration config = new Configuration();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))){

            String line;
            int lineNo = 0;
            while ((line = reader.readLine()) != null) {
                lineNo++;
                // 1. check for comments
                String[] comments = line.split("#", 2);
                String conf = comments[0].trim();

                // 2. get key and value
                if (conf.length() > 0) {
                    String[] kv = conf.split(": ", 2);

                    // skip line with no valid key-value pair
                    if (kv.length == 1) {
                        LOG.warn("Error while trying to split key and value in configuration file " + file + ":" + lineNo + ": \"" + line + "\"");
                        continue;
                    }

                    String key = kv[0].trim();
                    String value = kv[1].trim();

                    // sanity check
                    if (key.length() == 0 || value.length() == 0) {
                        LOG.warn("Error after splitting key and value in configuration file " + file + ":" + lineNo + ": \"" + line + "\"");
                        continue;
                    }

                    LOG.info("Loading configuration property: {}, {}", key, isSensitive(key) ? HIDDEN_CONTENT : value);
                    config.setString(key, value);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error parsing YAML configuration.", e);
        }

        return config;
    }


    /**
     * Check whether the key is a hidden key.
     *
     * @param key the config key
     */
    public static boolean isSensitive(String key) {
        final String keyInLower = key.toLowerCase();
        for (String hideKey : SENSITIVE_KEYS) {
            if (keyInLower.length() >= hideKey.length()
                    && keyInLower.contains(hideKey)) {
                return true;
            }
        }
        return false;
    }
}
