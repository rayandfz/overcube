package fr.owtfox.overcube;

import com.jonahseguin.drink.CommandService;
import com.jonahseguin.drink.Drink;
import fr.owtfox.overcube.commands.UserCommand;
import fr.owtfox.overcube.database.Database;
import fr.owtfox.overcube.kearbing.KeArbing;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public final class OverCube extends JavaPlugin {
    private final Database database = new Database();
    private final KeArbing keArbing = new KeArbing(database, this);
    private int taskId = -1;

    @Override
    public void onEnable() {
        database.createCollection();
        CommandService drink = Drink.get(this);
        drink.register(new UserCommand(database, this), "overcube");
        drink.registerCommands();


        BukkitScheduler scheduler = getServer().getScheduler();
        taskId = scheduler.scheduleSyncRepeatingTask(this, keArbing.launch(), 20 * 60 * 15, 20 * 60 * 15);
    }

    @Override
    public void onDisable() {
        if (taskId != -1) {
            getServer().getScheduler().cancelTask(taskId);
        }
    }
}
