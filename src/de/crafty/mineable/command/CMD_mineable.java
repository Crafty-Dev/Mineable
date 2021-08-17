package de.crafty.mineable.command;

import de.crafty.mineable.main.Mineable;
import de.crafty.mineable.main.References;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CMD_mineable implements TabExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {

        if (cmd.getName().equalsIgnoreCase("mineable")) {
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("reloadConfig")) {
                    File file = new File(Mineable.getInstance().getDataFolder(), "config.yml");
                    if(file.exists()){
                        Mineable.getInstance().reloadConfig();
                        sender.sendMessage(References.PREFIX + "Configuration File has been reloaded");
                    }else {
                        Mineable.getInstance().saveDefaultConfig();
                        sender.sendMessage(References.PREFIX + "Configuration File has been created");
                    }
                }
                if (args[0].equalsIgnoreCase("recreateConfig")) {
                    File file = new File(Mineable.getInstance().getDataFolder(), "config.yml");
                    file.delete();
                    Mineable.getInstance().saveDefaultConfig();
                    sender.sendMessage(References.PREFIX + "Configuration File has been recreated");
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String s, String[] args) {

        List<String> complete = new ArrayList<>();

        if (cmd.getName().equalsIgnoreCase("mineable")) {
            if (args.length == 1) {
                if (args[0].startsWith("rel"))
                    complete.add("reloadConfig");
                else if (args[0].startsWith("rec"))
                    complete.add("recreateConfig");
                else {
                    complete.add("recreateConfig");
                    complete.add("reloadConfig");
                }
            }
        }
        return complete;
    }
}
