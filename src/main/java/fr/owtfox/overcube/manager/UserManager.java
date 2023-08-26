package fr.owtfox.overcube.manager;

import fr.owtfox.overcube.models.IUserRepository;
import fr.owtfox.overcube.models.User;
import fr.owtfox.overcube.runnable.OverModeRunnable;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class UserManager {
    private final IUserRepository repository;
    private final Plugin plugin;
    private final HashMap<UUID, List<UUID>> reportUsers = new HashMap<>();
    private final OverModeRunnable overModeRunnable;


    public UserManager(IUserRepository repository, Plugin plugin) {
        this.repository = repository;
        this.plugin = plugin;
        this.overModeRunnable = new OverModeRunnable();
        this.overModeRunnable.runTaskTimer(plugin, 0L, 20L);
    }

    private User findOverCubeUser() {
        Collection<User> overCubeUsers = repository.getOverCubeUsers().join();

        for (User user : overCubeUsers) {
            Player overcubePlayer = Bukkit.getPlayer(user.getUUID());
            if (overcubePlayer != null && overcubePlayer.isOnline() && user.getOverCubeReady()) {
                return user;
            }
        }

        return null;
    }

    private void launchInspector(User spottedUser) {
        User overUser = findOverCubeUser();
        assert overUser != null;
        this.overModeRunnable.pushInspect(Bukkit.getPlayer(overUser.getUUID()), Bukkit.getPlayer(spottedUser.getUUID()));
    }

    public void report(Player commandSender, Player player) {
        final UUID playerUUID = player.getUniqueId();
        final UUID senderUUID = commandSender.getUniqueId();

        final List<UUID> reportedBy = reportUsers.computeIfAbsent(playerUUID, k -> new ArrayList<>());

        if (reportedBy.contains(senderUUID)) {
            commandSender.sendMessage("You have already reported this player.");
            return;
        }

        final User userHasMaxReport = repository.getSingleSpottedUser(playerUUID).join();

        if (userHasMaxReport != null) {
            int overNumber = userHasMaxReport.getOverCubeCount();

            launchInspector(userHasMaxReport);

            return;
        }

        repository.giveReport(playerUUID)
                .thenRun(() -> {
                    reportedBy.add(senderUUID);
                    commandSender.sendMessage("The player has been reported.");
                });

    }

    public void ungrant(CommandSender commandSender, Player player) {
        if (commandSender.isOp())
            repository.setPermission(player.getUniqueId(), false).thenRun(() -> commandSender.sendMessage("The player has lost his privileges"));

        commandSender.sendMessage("You don't have permission for execute this command.");
    }

    public void grant(CommandSender commandSender, Player player) {
        if (commandSender.isOp())
            repository.setPermission(player.getUniqueId(), true).thenRun(() -> commandSender.sendMessage("The player has received the privileges"));

        commandSender.sendMessage("You don't have permission for execute this command.");
    }

    public void ready(Player commandSender) {
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
