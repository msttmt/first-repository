package plugin.oremining;

import org.bukkit.Bukkit;

import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        GameStartCommand gameStartCommand = new GameStartCommand(this);
        Bukkit.getPluginManager().registerEvents(gameStartCommand, this);
        getCommand("gameStart").setExecutor(gameStartCommand);
    }
    }