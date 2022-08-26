package cli;

import configuration.ConfigConstants;
import configuration.Configuration;
import configuration.GlobalConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import static util.Preconditions.checkNotNull;

/**
 * @className: CliFrontend
 * @description: 提交程度入口
 * @author: hone
 * @create: 2022/8/23 18:44
 */
public class CliFrontend {
    private static final String CONFIG_DIRECTORY_FALLBACK_1 = "../conf";
    private static final String CONFIG_DIRECTORY_FALLBACK_2 = "conf";

    private static final Logger LOG = LoggerFactory.getLogger(CliFrontend.class);

    private final Configuration configuration;

    private final List<CustomCommandLine> customCommandLines;



    public CliFrontend(
            Configuration configuration,
//            ClusterClientServiceLoader clusterClientServiceLoader,
            List<CustomCommandLine> customCommandLines) {
        this.configuration = checkNotNull(configuration);
        this.customCommandLines = checkNotNull(customCommandLines);
//        this.clusterClientServiceLoader = checkNotNull(clusterClientServiceLoader);

//        FileSystem.initialize(configuration, PluginUtils.createPluginManagerFromRootFolder(configuration));
//
//        this.customCommandLineOptions = new Options();
//
//        for (CustomCommandLine customCommandLine : customCommandLines) {
//            customCommandLine.addGeneralOptions(customCommandLineOptions);
//            customCommandLine.addRunOptions(customCommandLineOptions);
//        }
//
//        this.clientTimeout = configuration.get(ClientOptions.CLIENT_TIMEOUT);
//        this.defaultParallelism = configuration.getInteger(CoreOptions.DEFAULT_PARALLELISM);
    }

    public static void main(String[] args) throws IOException {
        // get the config directory
        final String configurationDirectory = getConfigurationDirectoryFromEnv();
//        System.out.println(configurationDirectory);

        // load the global configuration
        final Configuration configuration = GlobalConfiguration.loadConfiguration(configurationDirectory);

        // load the custom command lines
        final List<CustomCommandLine> customCommandLines = loadCustomCommandLines(
                configuration,
                configurationDirectory);

        CliFrontend cliFrontend = new CliFrontend(configuration, customCommandLines);


    }

    public static String getConfigurationDirectoryFromEnv(){
        String location = System.getenv(ConfigConstants.ENV_CONF_DIR);
        if (location != null) {
            if (new File(location).exists()) {
                return location;
            }
            else {
                throw new RuntimeException("The configuration directory '" + location + "', specified in the '" +
                        ConfigConstants.ENV_CONF_DIR + "' environment variable, does not exist.");
            }
        }
        else if (new File(CONFIG_DIRECTORY_FALLBACK_1).exists()) {
            location = CONFIG_DIRECTORY_FALLBACK_1;
        }
        else if (new File(CONFIG_DIRECTORY_FALLBACK_2).exists()) {
            location = CONFIG_DIRECTORY_FALLBACK_2;
        }
        else {
            throw new RuntimeException("The configuration directory was not specified. " +
                    "Please specify the directory containing the configuration file through the '" +
                    ConfigConstants.ENV_CONF_DIR + "' environment variable.");
        }
        return location;
    }

    public static List<CustomCommandLine> loadCustomCommandLines(Configuration configuration, String configurationDirectory) {
        List<CustomCommandLine> customCommandLines = new ArrayList<>();

        //	Command line interface of the YARN session, with a special initialization here
        //	to prefix all options with y/yarn.
        final String yarnSessionCLI = "cli.YarnSessionCli";
        try {
            customCommandLines.add(
                    loadCustomCommandLine(yarnSessionCLI,
                            configuration,
                            configurationDirectory,
                            "y",
                            "yarn"));
        } catch (NoClassDefFoundError | Exception e) {
            final String errorYarnSessionCLI = "org.apache.flink.yarn.cli.FallbackYarnSessionCli";
            try {
                LOG.info("Loading FallbackYarnSessionCli");
                customCommandLines.add(
                        loadCustomCommandLine(errorYarnSessionCLI, configuration));
            } catch (Exception exception) {
                LOG.warn("Could not load CLI class {}.", yarnSessionCLI, e);
            }
        }
        return customCommandLines;
    }

    /**
     * Loads a class from the classpath of the CustomCommandLine
     * @param className The fully-qualified class name to load.
     * @param params The constructor parameters
     */
    private static CustomCommandLine loadCustomCommandLine(String className, Object... params) throws Exception {

        Class<? extends CustomCommandLine> customCliClass =
                Class.forName(className).asSubclass(CustomCommandLine.class);

        // construct class types from the parameters
        Class<?>[] types = new Class<?>[params.length];
        for (int i = 0; i < params.length; i++) {
            checkNotNull(params[i], "Parameters for custom command-lines may not be null.");
            types[i] = params[i].getClass();
        }

        Constructor<? extends CustomCommandLine> constructor = customCliClass.getConstructor(types);

        return constructor.newInstance(params);
    }
}
