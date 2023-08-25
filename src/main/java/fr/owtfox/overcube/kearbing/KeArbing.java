package fr.owtfox.overcube.kearbing;

import fr.owtfox.overcube.models.IUserRepository;
import fr.owtfox.overcube.models.User;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;

public class KeArbing implements Runnable {
    private final IUserRepository repository;
    private final Plugin plugin;

    public KeArbing(IUserRepository repository, Plugin plugin) {
        this.repository = repository;
        this.plugin = plugin;
    }

    @Override
    public void run() {
        Collection<User> spottedUsers = repository.getSpottedUsers().join();

        for (User user : spottedUsers) {
            int overNumber = user.getOverCubeCount();
            int reportNumber = user.getReportCount();

            if (overNumber > 5)
                makeSanction();

            if (reportNumber >= 5) {
                User overUser = findOverCubeUser();
                if (overUser != null) {
                    launchOverMode(Bukkit.getPlayer(overUser.getUUID()), Bukkit.getPlayer(user.getUUID()), 10);
                }
            }
        }
    }

    public void launchOverMode(Player player1, Player player2, double maxDistance) {
        player1.setGameMode(GameMode.SPECTATOR);
        player1.teleport(player2.getLocation());

        new BukkitRunnable() {
            @Override
            public void run() {
                double distance = player1.getLocation().distance(player2.getLocation());
                if (distance > maxDistance) {
                    adjustPlayerPosition(player1, player2, maxDistance);
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private void adjustPlayerPosition(Player player1, Player player2, double maxDistance) {
        double[] delta = calculateDelta(player1.getLocation(), player2.getLocation(), maxDistance);

        player1.teleport(player1.getLocation().add(delta[0], delta[1], delta[2]));
    }

    private double[] calculateDelta(Location loc1, Location loc2, double maxDistance) {
        double deltaX = loc2.getX() - loc1.getX();
        double deltaY = loc2.getY() - loc1.getY();
        double deltaZ = loc2.getZ() - loc1.getZ();

        double length = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);

        double adjustedDeltaX = deltaX * maxDistance / length;
        double adjustedDeltaY = deltaY * maxDistance / length;
        double adjustedDeltaZ = deltaZ * maxDistance / length;

        return new double[]{adjustedDeltaX, adjustedDeltaY, adjustedDeltaZ};
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

    private void makeSanction() {
        // Implement your sanction logic here
    }
}
