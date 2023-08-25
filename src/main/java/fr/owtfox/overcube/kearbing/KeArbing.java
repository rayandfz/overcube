package fr.owtfox.overcube.kearbing;

import fr.owtfox.overcube.models.IUserRepository;
import fr.owtfox.overcube.models.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Collection;

public class KeArbing implements Runnable {
    private final IUserRepository repository;

    public KeArbing(IUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run() {
        Collection<User> spottedUsers = repository.getSpottedUsers().join();

        for(User user : spottedUsers) {
            final int overNumber = user.getOverCubeCount();
            final int reportNumber = user.getReportCount();
            if (overNumber > 5) makeSanction();
            if (reportNumber >= 5) makingOver();
        }
    }

    private void makeSanction() {
    }

    private void makingOver() {
        Collection<User> overCubeUsers = repository.getOverCubeUsers().join();
        for (User user : overCubeUsers) {
            Player player = Bukkit.getPlayer(user.getUUID());
            if (player != null) {
                player.sendMessage("test");
            }
        }
    }
}