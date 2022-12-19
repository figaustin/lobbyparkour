package com.etsuni.lobbyparkour;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.etsuni.lobbyparkour.LobbyParkour.plugin;

public class BoardManager {

    public Scoreboard parkourBoard(Player player) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard scoreboard = manager.getNewScoreboard();

        Objective obj = scoreboard.registerNewObjective("Parkour", "dummy",
                ChatColor.translateAlternateColorCodes('&', "&e&lParkour"));

        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        Score score = obj.getScore(ChatColor.AQUA + "=-=-=-=-=-=-=-=-=-=-=-=");
        score.setScore(14);
        Score score2 = obj.getScore(getPB(player.getUniqueId().toString()));
        score2.setScore(13);
        Score empty = obj.getScore(" ");
        empty.setScore(12);
        Score best = obj.getScore(ChatColor.GREEN + "Leaderboard:");
        best.setScore(11);

        List<Long> leaderboard = getLeaderboard() == null ? new ArrayList<>(): getLeaderboard();
        leaderboard.sort(Collections.reverseOrder());
        if(!leaderboard.isEmpty()) {
            for(int i = leaderboard.size() - 1; i >= 0; i--) {
                long minutes = (leaderboard.get(i) / 1000) / 60;
                long seconds = (leaderboard.get(i) / 1000) % 60;
                long realMillis = leaderboard.get(i) % 1000;
                Score newScore = obj.getScore(ChatColor.GOLD + "- " + minutes+":" +seconds + "." + realMillis);
                newScore.setScore(i);

            }
        }

        return scoreboard;
    }

    public String getPB(String uuid) {

        List<Long> entries = new ArrayList<>();
        BasicDBObject query = new BasicDBObject();
        query.put("uuid", uuid);
        FindIterable<Document> finds = plugin.getCollection().find(query);

        for(Document document : finds) {
            entries.add(document.getLong("time"));
        }

        Collections.sort(entries);

        long minutes = 0;
        long seconds = 0;
        long realMillis = 0;
        if(entries.size() > 0 && entries.get(0) != null) {
            minutes = ( entries.get(0) / 1000) / 60;
            seconds = ( entries.get(0) / 1000) % 60;
            realMillis = entries.get(0) % 1000;
        }


        return ChatColor.GOLD + "Your PB:    " + minutes+":" +seconds + "." + realMillis;
    }

    public List<Long> getLeaderboard() {
        List<Long> leaderboard = new ArrayList<>();
        List<Long> entries = new ArrayList<>();
        BasicDBObject query = new BasicDBObject();

        query.put("time", 1);
        FindIterable<Document> finds = plugin.getCollection().find();
        if(finds.first() == null) {
            return null;
        }
        for(Document document : finds) {
            entries.add(document.getLong("time"));
        }

        Collections.sort(entries);
        int LEADERBOARD_SIZE = 10;
        for(int i = 0; i <= LEADERBOARD_SIZE - 1; i++) {
            if(entries.size() > i) {
                leaderboard.add(entries.get(i));
            } else {
                break;
            }
        }

        return leaderboard;
    }
}
