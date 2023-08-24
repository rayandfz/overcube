package fr.owtfox.overcube.kearbing;

import com.mongodb.client.MongoIterable;
import fr.owtfox.overcube.database.Database;
import fr.owtfox.overcube.models.User;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class KeArbing {
    private final User users;
    private final Plugin plugin;

    public KeArbing(Database database, Plugin plugin) {
        users = new User(database);
        this.plugin = plugin;
    }

    public Runnable launch() {
        return () -> {
            final MongoIterable<String> playerReport = users.getAllPlayerReport();
            for (String playerName : playerReport) {
                final int overNumber = users.getOverNumber(playerName);
                final int reportNumber = users.getReport(playerName);
                if (overNumber > 5) makeSanction();
                if (reportNumber >= 5) makingOver();
            }
        };
    }

    private void makeSanction() {

    }

    private void makingOver() {
        final MongoIterable<String> overcubeUser = users.getOvercubeUser();
        for (String user : overcubeUser) {
            Player player = plugin.getServer().getPlayer(user);
            if (player != null && player.isOnline()) {
                player.sendMessage("Tu es missioner");
            }
        }
    }
}