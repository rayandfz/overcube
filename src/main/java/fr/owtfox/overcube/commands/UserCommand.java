package fr.owtfox.overcube.commands;

import com.jonahseguin.drink.annotation.Command;
import com.jonahseguin.drink.annotation.Sender;
import fr.owtfox.overcube.models.IUserRepository;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import java.util.HashMap;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserCommand {
    private final IUserRepository repository;
    private final Plugin plugin;
    private HashMap<UUID, List<UUID>> reportUsers = new HashMap<>();

    public UserCommand(IUserRepository repository, Plugin plugin) {
        this.repository = repository;
        this.plugin = plugin;
    }

    @Command(name = "report", desc = "", usage = "<playerName>")
    public void report(@Sender Player commandSender, Player player) {
        UUID playerUUID = player.getUniqueId();
        UUID senderUUID = commandSender.getUniqueId();
        List<UUID> reportedBy = reportUsers.computeIfAbsent(playerUUID, k -> new ArrayList<>());

        if (reportedBy.contains(senderUUID)) {
            commandSender.sendMessage("You have already reported this player.");
            return;
        }

        repository.giveReport(playerUUID)
                .thenRun(() -> {
                    reportedBy.add(senderUUID);
                    commandSender.sendMessage("The player has been reported.");
                });
    }

    @Command(name = "ungrant", desc = "", usage = "<playerName>")
    public void ungrant(@Sender  CommandSender commandSender, Player player) {
        if (commandSender.isOp())
            repository.setPermission(player.getUniqueId(), false).thenRun(() -> commandSender.sendMessage("The player has lost his privileges"));

        commandSender.sendMessage("You don't have permission for execute this command.");
    }

    @Command(name = "grant", desc = "", usage = "<playerName>")
    public void grant(@Sender CommandSender commandSender, Player player) {
        if (commandSender.isOp())
            repository.setPermission(player.getUniqueId(), true).thenRun(() -> commandSender.sendMessage("The player has received the privileges"));

        commandSender.sendMessage("You don't have permission for execute this command.");
    }

    @Command(name = "ready", desc = "", usage = "/ready")
    public void ready(@Sender Player commandSender) {
        final UUID uuid = commandSender.getUniqueId();

        final boolean hasPermission = repository.getPermission(uuid).join();

        if (!hasPermission) {
            commandSender.sendMessage("You don't have permission for this");
            return;
        }

        final boolean overCubeReady = repository.getOverCubeReady(uuid).join();

        repository.setOverCubeReady(uuid, !overCubeReady);

        commandSender.sendMessage("Overcube ready is " + !overCubeReady);
    }
}