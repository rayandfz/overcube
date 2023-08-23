package fr.owtfox.overcube;

import com.jonahseguin.drink.CommandService;
import com.jonahseguin.drink.Drink;
import fr.owtfox.overcube.commands.UserCommand;
import fr.owtfox.overcube.database.Database;
import org.bukkit.plugin.java.JavaPlugin;

public final class OverCube extends JavaPlugin {
    private final Database database = new Database();

    @Override
    public void onEnable() {
        database.createCollection();
        CommandService drink = Drink.get(this);
        drink.register(new UserCommand(database, this), "overcube");
        drink.registerCommands();
    }

    @Override
    public void onDisable() {

    }
}
