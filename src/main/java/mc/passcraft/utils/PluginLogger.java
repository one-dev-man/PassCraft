package mc.passcraft.utils;

import org.bukkit.Bukkit;

import java.util.function.Consumer;

public class PluginLogger {

    private String _prefix;

    //

    public PluginLogger() {
        this("");
    }

    public PluginLogger(String prefix) {
        this.setPrefix(prefix);
    }

    //

    public String getPrefix() { return this._prefix; }
    public void setPrefix(String prefix) { this._prefix = AnsiColor.parse(prefix + "§r"); }

    //

    public void info(String info) {
        this.callLog(Bukkit.getLogger()::info, info);
    }

    public void warning(String warning) {
        this.callLog(Bukkit.getLogger()::warning, "§6" + warning);
    }

    public void error(String error) {
        this.callLog(Bukkit.getLogger()::severe, "§4" + error);
    }

    //

    private void callLog(Consumer<String> logMethod, String log) {
        logMethod.accept(this.getPrefix() + " " + AnsiColor.parse(log + "§r"));
    }

}
