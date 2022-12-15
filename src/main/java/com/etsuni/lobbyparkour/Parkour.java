package com.etsuni.lobbyparkour;

import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static com.etsuni.lobbyparkour.LobbyParkour.plugin;

public class Parkour {

    public LocalDateTime startTime;

    public Location end;

    public List<Location> checkpoints;

    public int playerCheckPoint;

    public int task = 0;

    public Parkour(LocalDateTime startTime, Location end, List<Location> checkpoints) {
        this.startTime = startTime;
        this.end = end;
        this.checkpoints = checkpoints;
        this.playerCheckPoint = -1;
    }

    public void start(Player player) {
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        PlayersParkouring.getInstance().getPlayersTimes().put(player, startTime);
        player.sendMessage(ChatColor.GREEN + "Parkour Started!");

        task = scheduler.scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                if(player.getLocation().getBlock().getLocation().equals(end)) {
                    LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
                    long millis = ChronoUnit.MILLIS.between(startTime, now);
                    long minutes = (millis / 1000) / 60;
                    long seconds = (millis / 1000) % 60;
                    long realMillis = millis % 1000;
                    player.sendMessage(ChatColor.GOLD + "Parkour time: " +minutes+":" +seconds + "." + realMillis + "!");
                    PlayersParkouring.getInstance().getPlayersTimes().remove(player);
                    PlayersParkouring.getInstance().getCheckpoints().remove(player);
                    personalBest(millis, player.getUniqueId().toString());
                    scheduler.cancelTask(task);
                } else if(checkpoints.contains(player.getLocation().getBlock().getLocation())) {
                    int index = checkpoints.indexOf(player.getLocation().getBlock().getLocation());
                    if(addCheckPoint(index)) {
                        player.sendMessage("Reached checkpoint #" + (index + 1) +"!");
                    }
                }
            }
        },0,2);
    }

    public Boolean addCheckPoint(int index) {

        if(playerCheckPoint > index) {
            return false;
        }

        if(playerCheckPoint < index) {
            playerCheckPoint = index;
            return true;
        }

        return false;
    }

    public void personalBest(long time, String uuid) {
        playerInDB(uuid);
        Document find = new Document("uuid", uuid);

        if(find.putIfAbsent("pb", time) == null) {
            return;
        }

        if(time < find.getLong("pb")) {
            find.replace("pb", time);
        }
    }

    public void playerInDB(String uuid) {
        Document find = new Document("uuid", uuid);
        if(plugin.getCollection().find(find).first() == null) {
            plugin.getCollection().insertOne(find);
        }
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Location getEnd() {
        return end;
    }

    public void setEnd(Location end) {
        this.end = end;
    }

    public List<Location> getCheckpoints() {
        return checkpoints;
    }

    public void setCheckpoints(List<Location> checkpoints) {
        this.checkpoints = checkpoints;
    }
}
