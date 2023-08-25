package fr.owtfox.overcube.commands;

import com.jonahseguin.drink.annotation.Command;
import com.jonahseguin.drink.annotation.Sender;
import fr.owtfox.overcube.models.IUserRepository;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.Plugin;

public class UserCommand {
    private final IUserRepository repository;
    private final Plugin plugin;

    public UserCommand(IUserRepository repository, Plugin plugin) {
        this.repository = repository;
        this.plugin = plugin;
    }

    @Command(name = "report", desc = "", usage = "<playerName>")
    public void report(@Sender  CommandSender commandSender, Player player) {
        repository
            .giveReport(player.getUniqueId())
            .thenRun(() -> commandSender.sendMessage("The player has been reported"));
    }

    @Command(name = "ungrant", desc = "", usage = "<playerName>")
    public void ungrant(@Sender  CommandSender commandSender, Player player) {
        if (commandSender.isOp()) {
            repository.setPermission(player.getUniqueId(), false).thenRun(() -> {
                final PermissionAttachment attachment = player.addAttachment(plugin);
                attachment.unsetPermission("overcube.is_over_cube");
                commandSender.sendMessage("The player has lost his privileges");
            });
        }

        commandSender.sendMessage("You don't have permission for execute this command.");
    }

    @Command(name = "grant", desc = "", usage = "<playerName>")
    public void grant(@Sender CommandSender commandSender, Player player) {
        if (commandSender.isOp()) {
            repository.setPermission(player.getUniqueId(), true).thenRun(() -> {
                player.addAttachment(plugin, "overcube.is_over_cube", true);
                commandSender.sendMessage("The player has received the privileges");
            });
        }

        commandSender.sendMessage("You don't have permission for execute this command.");
    }
}