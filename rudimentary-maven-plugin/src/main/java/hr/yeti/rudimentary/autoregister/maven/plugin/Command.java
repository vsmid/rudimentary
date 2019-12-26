package hr.yeti.rudimentary.autoregister.maven.plugin;

public interface Command {

    default boolean isWindowsOS() {
        return System.getProperty("os.name").toLowerCase().contains("windows");
    }

}
