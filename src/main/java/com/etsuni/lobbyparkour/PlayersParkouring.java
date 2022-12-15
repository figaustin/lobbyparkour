package com.etsuni.lobbyparkour;

import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class PlayersParkouring {

    private Map<Player, Integer> playersCheckpoints = new HashMap<>();
    private Map<Player, LocalDateTime> playersTimes = new HashMap<>();
    private static PlayersParkouring instance = new PlayersParkouring();

    public static PlayersParkouring getInstance() {
        return instance;
    }

    public Map<Player, Integer> getCheckpoints() {
        return playersCheckpoints;
    }

    public Map<Player, LocalDateTime> getPlayersTimes() {
        return playersTimes;
    }
}
