package fr.owtfox.overcube.commands;

import com.jonahseguin.drink.annotation.Command;
import com.jonahseguin.drink.annotation.Sender;
import fr.owtfox.overcube.manager.UserManager;
import fr.owtfox.overcube.models.IUserRepository;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class UserCommand {
    private final UserManager userManager;

    public UserCommand(IUserRepository repository, Plugin plugin) {
        userManager = new UserManager(repository, plugin);
    }

    @Command(name = "report", desc = "Command to report a player", usage = "<playerName>")
    public void report(@Sender Player commandSender, Player player) {
        userManager.report(commandSender, player);
    }

    @Command(name = "ungrant", desc = "Command to increase a player's privileges (be overcube user)", usage = "<playerName>")
    public void ungrant(@Sender CommandSender commandSender, Player player) {
        userManager.ungrant(commandSender, player);
    }

    @Command(name = "grant", desc = "Remove player privileges", usage = "<playerName>")
    public void grant(@Sender CommandSender commandSender, Player player) {
       userManager.grant(commandSender, player);
    }

    @Command(name = "ready", desc = "Ready for overcube", usage = "/ready")
    public void ready(@Sender Player commandSender) {
        userManager.ready(commandSender);
    }
}