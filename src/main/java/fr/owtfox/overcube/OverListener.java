package fr.owtfox.overcube;

import fr.owtfox.overcube.models.IUserRepository;
import fr.owtfox.overcube.models.User;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.UUID;

public class OverListener implements Listener {

    private final IUserRepository repository;

    public OverListener(IUserRepository repository) {
        this.repository = repository;
    }

    @EventHandler
    public void onLogin(AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();
        if(!repository.contains(uuid).join()) repository.addUser(User.empty(uuid));
    }
}
