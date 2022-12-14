package com.etsuni.lobbyparkour;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static com.etsuni.lobbyparkour.LobbyParkour.plugin;

public class Commands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            if(command.getName().equalsIgnoreCase("parkour")) {
                if(args.length > 0) {
                    if(args[0].equalsIgnoreCase("set")) {

                        if(args.length > 1) {

                            if(args[1].equalsIgnoreCase("start")) {
                                Location loc = ((Player) sender).getLocation().getBlock().getLocation();
                                plugin.getCustomConfig().set("parkour.start", loc);
                                plugin.saveCfg();
                            }
                            else if(args[1].equalsIgnoreCase("checkpoint")) {
                                Location loc = ((Player) sender).getLocation().getBlock().getLocation();
                                List<Location> checkpoints = (List<Location>) plugin.getCustomConfig().getList("parkour.checkpoints") == null ?
                                        new ArrayList<>() : (List<Location>) plugin.getCustomConfig().getList("parkour.checkpoints");
                                checkpoints.add(loc);
                                plugin.getCustomConfig().set("parkour.checkpoints", checkpoints);
                                plugin.saveCfg();
                            }
                            else if(args[1].equalsIgnoreCase("end")) {
                                Location loc = ((Player) sender).getLocation().getBlock().getLocation();
                                plugin.getCustomConfig().set("parkour.end", loc);
                                plugin.saveCfg();
                            }
                        }

                    }
                }
            }
        }
        return false;
    }
}
