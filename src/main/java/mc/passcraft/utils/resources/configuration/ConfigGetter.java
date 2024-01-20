package mc.passcraft.utils.resources.configuration;

public abstract class ConfigGetter {

    protected final ConfigParser configParser;

    public ConfigGetter(ConfigParser configParser) {
        this.configParser = configParser;
    }

}