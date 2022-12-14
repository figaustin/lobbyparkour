package com.etsuni.lobbyparkour;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

public class BoardManager {

    public Scoreboard parkourBoard() {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard scoreboard = manager.getNewScoreboard();

        Objective obj = scoreboard.registerNewObjective("Parkour", "dummy",
                ChatColor.translateAlternateColorCodes('&', "&e&lParkour"));

        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        Score score = obj.getScore(ChatColor.AQUA + "=-=-=-=-=-=-=-=-=-=-=-=");
        score.setScore(3);
        Score score2 = obj.getScore(ChatColor.GOLD + "Your PB: " + "1:00");
        return scoreboard;
    }
}
