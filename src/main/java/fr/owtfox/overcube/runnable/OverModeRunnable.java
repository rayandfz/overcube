package fr.owtfox.overcube.runnable;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OverModeRunnable extends BukkitRunnable {
    private Map<Player, Player> playersMap = new ConcurrentHashMap<>();

    public void pushInspect(Player inspectorPlayer, Player suspectPlayer) {
        playersMap.put(inspectorPlayer, suspectPlayer);
    }

    @Override
    public void run() {
        for (var players : playersMap.entrySet()) {
            Player inspectorPlayer = players.getKey();
            Player suspectedPlayer = players.getValue();

            double maxDistance = 10;
            if (inspectorPlayer.getLocation().distance(suspectedPlayer.getLocation()) > maxDistance) {
                inspectorPlayer.teleport(suspectedPlayer.getLocation());
            }
        }
    }
}