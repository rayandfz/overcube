package fr.owtfox.overcube.manager;

import fr.owtfox.overcube.models.IUserRepository;
import fr.owtfox.overcube.models.User;
import fr.owtfox.overcube.runnable.OverModeRunnable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class OverModeManager {
    private final OverModeRunnable overModeRunnable;
    private final IUserRepository repository;

    public OverModeManager(IUserRepository repository, Plugin plugin) {
        this.repository = repository;
        this.overModeRunnable = new OverModeRunnable();
        this.overModeRunnable.runTaskTimer(plugin, 10L, 20L);
    }

    private Optional<User> findOverCubeUser(UUID spottedUser) {
        Collection<User> overCubeUsers = repository.getOverCubeUsers().join();

        for (User overCubeUser : overCubeUsers) {
            Player overcubePlayer = Bukkit.getPlayer(overCubeUser.getUUID());
            if (overcubePlayer != null && overCubeUser.getOverCubeReady()) {
                overcubePlayer.sendMessage("[Overcube] -> You've been called to an inspection, we're going to teleport you in 10 seconds to the player : " + Bukkit.getPlayer(spottedUser).getDisplayName());
                return Optional.of(overCubeUser);
            }
        }

        return Optional.empty();
    }

    public void launch(User spottedUser) {
        Optional<User> overUser = findOverCubeUser(spottedUser.getUUID());

        overUser.ifPresent(user -> {
            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            Runnable task = () -> this.overModeRunnable.pushInspect(Bukkit.getPlayer(user.getUUID()), Bukkit.getPlayer(spottedUser.getUUID()));
            scheduler.schedule(task, 10, TimeUnit.SECONDS);
        });
    }
}
