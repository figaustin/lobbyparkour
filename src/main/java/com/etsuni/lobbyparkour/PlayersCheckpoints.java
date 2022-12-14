package com.etsuni.lobbyparkour;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class PlayersCheckpoints {

    private static Map<Player, Integer> checkpoints = new HashMap<>();
    private static PlayersCheckpoints instance = new PlayersCheckpoints();

    public static PlayersCheckpoints getInstance() {
        return instance;
    }

    public Map<Player, Integer> getCheckpoints() {
        return checkpoints;
    }
}
