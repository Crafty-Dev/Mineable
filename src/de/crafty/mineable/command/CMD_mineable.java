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
import java.util.Set;

public class CMD_mineable implements TabExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {

        if (cmd.getName().equalsIgnoreCase("mineable")) {
            Mineable instance = Mineable.getInstance();
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("reloadConfig")) {
                    File file = new File(instance.getDataFolder(), "config.yml");
                    if (file.exists()) {
                        instance.reloadConfig();
                        sender.sendMessage(References.PREFIX + "Configuration File has been reloaded");
                    } else {
                        Mineable.getInstance().saveDefaultConfig();
                        sender.sendMessage(References.PREFIX + "Configuration File has been created");
                    }
                }
                if (args[0].equalsIgnoreCase("recreateConfig")) {
                    File file = new File(instance.getDataFolder(), "config.yml");
                    file.delete();
                    instance.saveDefaultConfig();
                    sender.sendMessage(References.PREFIX + "Configuration File has been recreated");
                }
                return true;
            }
            if (args.length == 2) {
                Set<String> availableBlocks = instance.getConfig().getKeys(true);

                if (args[0].equalsIgnoreCase("enable")) {
                    for (String s1 : availableBlocks) {
                        if (args[1].equalsIgnoreCase(s1)) {
                            instance.getConfig().set(s1, true);
                            instance.saveConfig();
                            instance.reloadConfig();
                            sender.sendMessage(References.PREFIX + "§a" + s1 + " §7is now affected by silk touch");
                            return true;
                        }
                    }
                    sender.sendMessage(References.PREFIX + "§c" + args[1] + " §7is not a registered block");
                }

                if (args[0].equalsIgnoreCase("disable")) {
                    for (String s1 : availableBlocks) {
                        if (args[1].equalsIgnoreCase(s1)) {
                            instance.getConfig().set(s1, false);
                            instance.saveConfig();
                            instance.reloadConfig();
                            sender.sendMessage(References.PREFIX + "§a" + s1 + " §7is no longer affected by silk touch");
                            return true;
                        }
                    }
                    sender.sendMessage(References.PREFIX + "§c" + args[1] + " §7is not a registered block");
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String s, String[] args) {

        List<String> complete = new ArrayList<>();

        List<String> list1 = new ArrayList<>();
        list1.add("reloadConfig");
        list1.add("recreateConfig");
        list1.add("enable");
        list1.add("disable");

        List<String> list2 = new ArrayList<>(Mineable.getInstance().getConfig().getKeys(true));

        if (cmd.getName().equalsIgnoreCase("mineable")) {
            if (args.length == 1) {
                for (String s1 : list1) {
                    if (s1.startsWith(args[0])) {
                        complete.add(s1);
                    }
                }
                if (complete.isEmpty())
                    complete.addAll(list1);
            }
            if (args.length == 2) {
                for (String s1 : list2) {
                    if (s1.startsWith(args[1])) {
                        complete.add(s1);
                    }
                }
                if (complete.isEmpty())
                    complete.addAll(list2);
            }
        }
        return complete;
    }
}
