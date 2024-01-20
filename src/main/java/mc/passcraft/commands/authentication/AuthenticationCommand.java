package mc.passcraft.commands.authentication;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import mc.passcraft.authentication.AuthManager;
import mc.passcraft.authentication.types.AuthResult;
import mc.passcraft.types.AbstractCommand;
import mc.passcraft.types.Formatter;
import mc.passcraft.utils.game.Notify;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class AuthenticationCommand extends AbstractCommand {

    private final TabCompleter completer = (@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) -> {
        List<String> result = new ArrayList<>();

        if(args.length <= 1) {
            result.add("password");
            result.add("discord");
        }

        return result;
    };

    public final AuthManager authmanager;

    //

    public AuthenticationCommand(AuthManager authmanager) {
        this.authmanager = authmanager;
    }

    //

    public TabCompleter completer() {return this.completer; }

    //

    public <CaseType> void notifyByResult(
            Player player,
            AuthResult result,
            Map<AuthResult, CaseType> successCases,
            Map<AuthResult, CaseType> errorCases,
            Formatter<CaseType, String> caseFormatter
    ) throws InvocationTargetException, IllegalAccessException {
        if(successCases.containsKey(result))
            Notify.chatSuccess(player, caseFormatter.format(successCases.get(result)));
        else if(errorCases.containsKey(result))
            Notify.chatError(player, caseFormatter.format(errorCases.get(result)));
    }

}
