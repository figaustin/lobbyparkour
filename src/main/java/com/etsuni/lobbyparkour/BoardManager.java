package com.etsuni.lobbyparkour;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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
        score.setScore(13);
        Score score2 = obj.getScore(getPB(player.getUniqueId().toString()));
        score2.setScore(12);
        Score empty = obj.getScore(" ");
        empty.setScore(11);
        Score best = obj.getScore(ChatColor.GREEN + "Leaderboard:");
        best.setScore(10);

        List<Long> leaderboard = getLeaderboard();
        for(int i = leaderboard.size() - 1; i >= 0; i--) {
            long minutes = (leaderboard.get(i) / 1000) / 60;
            long seconds = (leaderboard.get(i) / 1000) % 60;
            long realMillis = leaderboard.get(i) % 1000;
            Score newScore = obj.getScore(ChatColor.GOLD + "" + minutes+":" +seconds + "." + realMillis);
            newScore.setScore(i);
        }
        return scoreboard;
    }

    public String getPB(String uuid) {
        Document find = new Document("uuid", uuid);
        FindIterable<Document> finds = plugin.getCollection().find(find);
        if(finds.first() == null) {
            return "";
        }
        long millis = 123456789;

        for(Document document : finds) {
            millis = document.getLong("pb");
        }


        long minutes = (millis / 1000) / 60;
        long seconds = (millis / 1000) % 60;
        long realMillis = millis % 1000;

        return ChatColor.GOLD + "Your PB:    " + minutes+":" +seconds + "." + realMillis;
    }

    public List<Long> getLeaderboard() {
        List<Long> leaderboard = new ArrayList<>();
        Document find = new Document("leaderboardOBJ", "obj");

        MongoCursor<Document> cursor = plugin.getCollection().find(find).iterator();

        try{
            while(cursor.hasNext()) {
                Document str = cursor.next();

                List<Document> list = (List<Document>) str.get("leaderboard");
                for(int i = 0; i < list.size(); i++) {
                    leaderboard.add(list.get(i).getLong("time"));
                }
            }
        } finally {
            cursor.close();
        }


        return leaderboard;
    }
}
