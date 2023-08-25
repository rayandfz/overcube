package fr.owtfox.overcube;

import com.jonahseguin.drink.CommandService;
import com.jonahseguin.drink.Drink;
import fr.owtfox.overcube.commands.UserCommand;
import fr.owtfox.overcube.database.SQLUserRepository;
import fr.owtfox.overcube.kearbing.KeArbing;
import fr.owtfox.overcube.models.IUserRepository;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public final class OverCube extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        final FileConfiguration configuration = this.getConfig();

        IUserRepository repository = SQLUserRepository.openPostgres(
                configuration.getString("database.url"),
                configuration.getString("database.name"),
                configuration.getString("database.username"),
                configuration.getString("database.password")
        );

        getServer().getPluginManager().registerEvents(new OverListener(repository), this);

        CommandService drink = Drink.get(this);
        drink.register(new UserCommand(repository, this), "overcube");
        drink.registerCommands();

        BukkitScheduler scheduler = getServer().getScheduler();
        scheduler.runTaskTimer(this, new KeArbing(repository), 0, 20 * 10);
    }
}
