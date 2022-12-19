package com.etsuni.lobbyparkour;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import static com.etsuni.lobbyparkour.LobbyParkour.plugin;

public class Events implements Listener {

    private List<Player> playersInRegion = new ArrayList<>();

    @EventHandler
    public void startParkour(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location location = player.getLocation().getBlock().getLocation();

        if(plugin.getCustomConfig().get("parkour.end") == null) {
            return;
        }

        if(!location.equals(plugin.getCustomConfig().getLocation("parkour.start"))) {
            return;
        }

        if(location.equals(plugin.getCustomConfig().getLocation("parkour.start")) &&
                !PlayersParkouring.getInstance().getPlayersTimes().containsKey(player)) {
            LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
            Parkour parkour = new Parkour(now, plugin.getCustomConfig().getLocation("parkour.end"),
                    (List<Location>) plugin.getCustomConfig().getList("parkour.checkpoints"));
            parkour.start(player);
        }
    }


    @EventHandler
    public void region(PlayerMoveEvent event) {

        Player player = event.getPlayer();

        com.sk89q.worldedit.util.Location loc = BukkitAdapter.adapt(player.getLocation());
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        ApplicableRegionSet set = query.getApplicableRegions(loc);

        for(ProtectedRegion reg : set) {
            org.bukkit.World realWorld = Bukkit.getWorld(player.getWorld().getName());
            World world = BukkitAdapter.adapt(realWorld);
            RegionManager regions = container.get(world);
            ProtectedRegion region;
            if(regions != null) {
                region = regions.getRegion("parkour");
            } else {
                return;
            }
            if(reg.equals(region)) {
                if(playersInRegion.contains(player)) {
                    return;
                } else {
                    playersInRegion.add(player);
                    BoardManager bm = new BoardManager();
                    player.setScoreboard(bm.parkourBoard(player));
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
    public void onClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();

        if(!action.equals(Action.RIGHT_CLICK_AIR)) {
            return;
        }

        if(player.getInventory().getItemInMainHand().hasItemMeta()
                && player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().contains("Checkpoint")) {
            List<Location> checkpoints = (List<Location>) plugin.getCustomConfig().getList("parkour.checkpoints");
            if(PlayersParkouring.getInstance().getCheckpoints().containsKey(player)) {
                player.teleport(checkpoints.get(PlayersParkouring.getInstance().getCheckpoints().get(player)));
            }

        }
    }

}
