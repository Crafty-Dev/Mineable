package de.crafty.mineable.main;

import de.crafty.mineable.command.CMD_mineable;
import de.crafty.mineable.events.BlockBreakListener;
import de.crafty.mineable.events.BlockPlaceListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Mineable extends JavaPlugin {


    private static Mineable instance;

    @Override
    public void onEnable() {

        instance = this;

        Bukkit.getConsoleSender().sendMessage(References.PREFIX + "Plugin enabled");

        getCommand("mineable").setExecutor(new CMD_mineable());

        PluginManager pm = Bukkit.getPluginManager();

        pm.registerEvents(new BlockBreakListener(), this);
        pm.registerEvents(new BlockPlaceListener(), this);

        this.saveDefaultConfig();

    }


    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(References.PREFIX + "Plugin disabled");
    }

    public static Mineable getInstance() {
        return instance;
    }
}
