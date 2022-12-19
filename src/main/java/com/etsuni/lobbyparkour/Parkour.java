package com.etsuni.lobbyparkour;

import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitScheduler;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
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
        ItemStack item = new ItemStack(Material.GLOWSTONE_DUST);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "Return To Checkpoint (Right Click)");
        item.setItemMeta(meta);
        player.getInventory().addItem(item);

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

                    addTime(player.getUniqueId().toString(), millis);
                    player.getInventory().remove(item);
//                    player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
                    BoardManager bm = new BoardManager();
                    player.setScoreboard(bm.parkourBoard(player));
                    scheduler.cancelTask(task);
                } else if(checkpoints != null && checkpoints.contains(player.getLocation().getBlock().getLocation())) {
                    int index = checkpoints.indexOf(player.getLocation().getBlock().getLocation());
                    if(addCheckPoint(player, index)) {
                        player.sendMessage("Reached checkpoint #" + (index + 1) +"!");
                    }
                }
            }
        },0,0);
    }

    public Boolean addCheckPoint(Player player, int index) {

        if(playerCheckPoint > index) {
            return false;
        }

        if(playerCheckPoint < index) {
            playerCheckPoint = index;
            if(!PlayersParkouring.getInstance().getCheckpoints().containsKey(player)) {
                PlayersParkouring.getInstance().getCheckpoints().put(player, index);
            } else {
                PlayersParkouring.getInstance().getCheckpoints().replace(player, index);
            }

            return true;
        }

        return false;
    }

    public void addTime(String uuid, long time) {

        Document playerTime = new Document("uuid", uuid).append("time", time);
        plugin.getCollection().insertOne(playerTime);
    }

}
