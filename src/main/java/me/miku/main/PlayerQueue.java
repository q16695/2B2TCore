package me.miku.main;

import org.bukkit.World;
import org.bukkit.entity.Player;

public class PlayerQueue {
    public Player player;
    //second
    public int queueTime;
    //tick
    public int joinServerTick = 0;
    public int position;
    public World normalWorld;

    public PlayerQueue(Player player, int queueTime, int position, World normalWorld) {
        this.player = player;
        this.queueTime = queueTime;
        this.position = position;
        this.normalWorld = normalWorld;
    }
}
