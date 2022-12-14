package com.etsuni.lobbyparkour;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.etsuni.lobbyparkour.LobbyParkour.plugin;

public class Parkour implements Listener {
    
    private Map<Player, Integer> checkpoints;
    private List<Player> playersInRegion;
    private Map<Player, LocalDateTime> playersTime;
    
    public Parkour() {
        checkpoints = new HashMap<>();
        playersInRegion = new ArrayList<>();
        playersTime = new HashMap<>();
    }

    @EventHandler
    public void region(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        //LOOKUP WG MOVE TYPE, MIGHT MAKE THIS FASTER


        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        org.bukkit.World realWorld = Bukkit.getWorld("world");
        World world = BukkitAdapter.adapt(realWorld);
        RegionManager regions = container.get(world);
        ProtectedRegion region;

        if(regions != null) {
            region = regions.getRegion("Parkour");
        } else {
            return;
        }

        Location loc = BukkitAdapter.adapt(player.getLocation());
        RegionQuery query = container.createQuery();
        ApplicableRegionSet set = query.getApplicableRegions(loc);

        for(ProtectedRegion reg : set) {
            if(reg.equals(region)) {
                if(playersInRegion.contains(player)) {
                } else {
                    playersInRegion.add(player);
                    BoardManager bm = new BoardManager();
                    player.setScoreboard(bm.parkourBoard());
                }
                return;
            }
        }
        boolean removed = playersInRegion.remove(player);
        if(removed) {
            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        }

    }

    @EventHandler
    public void start(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        org.bukkit.Location location = player.getLocation().getBlock().getLocation();

        if(playersTime.containsKey(player)) {
            return;
        }

        if(location.equals(plugin.getCustomConfig().getLocation("parkour.start"))) {
            LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
            playersTime.put(player, now);
            player.sendMessage("Start time:" + now);
        }
    }

    @EventHandler
    public void end(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        org.bukkit.Location location = player.getLocation().getBlock().getLocation();

        if(!playersTime.containsKey(player)){
            return;
        }

        if(location.equals(plugin.getCustomConfig().getLocation("parkour.end"))) {
            LocalDateTime start = playersTime.get(player);
            LocalDateTime end = LocalDateTime.now(ZoneOffset.UTC);
            long minutes = ChronoUnit.MINUTES.between(start, end);
            long seconds = ChronoUnit.SECONDS.between(start, end);
            long millis = ChronoUnit.MILLIS.between(start, end);
            player.sendMessage("Your time was: "+minutes+"min "+seconds+"."+millis+"s");
            playersTime.remove(player);
            checkpoints.remove(player);
        }
    }

    @EventHandler
    public void checkpoint(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        org.bukkit.Location location = player.getLocation().getBlock().getLocation();
        if(plugin.getCustomConfig().getList("parkour.checkpoints") == null) {
            return;
        }

        for(Object loc : plugin.getCustomConfig().getList("parkour.checkpoints")) {
            if(location.equals(loc)) {
                if(checkpoints.containsKey(player)) {

                    if(checkpoints.get(player) < plugin.getCustomConfig().getList("parkour.checkpoints").indexOf(loc)) {
                        checkpoints.replace(player,
                                plugin.getCustomConfig().getList("parkour.checkpoints").indexOf(loc));
                        player.sendMessage("New Checkpoint!");
                    }

                } else {
                    checkpoints.put(player, 0);
                    player.sendMessage("New Checkpoint!");
                }
            }
        }
    }
}
