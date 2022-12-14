package com.etsuni.lobbyparkour;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayersInRegion {

    private static List<Player> playersList = new ArrayList<>();
    private static PlayersInRegion instance = new PlayersInRegion();

    public static PlayersInRegion getInstance() {
        return instance;
    }

    public List<Player> getList() {
        return playersList;
    }
}
