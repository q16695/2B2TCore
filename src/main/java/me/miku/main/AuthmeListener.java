package me.miku.main;

import fr.xephi.authme.api.v3.AuthMeApi;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;
import java.util.stream.Collectors;

public class AuthmeListener implements Listener {
    @EventHandler
    public void queue1(PlayerJoinEvent event) {
        if(A2b2tCore.EnableQueue) {
            A2b2tCore.queue.put(event.getPlayer().getName(), new PlayerQueue(event.getPlayer(), new Random().nextInt(A2b2tCore.maxQueueSeconds - A2b2tCore.minQueueSeconds + 1) + A2b2tCore.minQueueSeconds, A2b2tCore.queue.size(), event.getPlayer().getWorld()));
            new BukkitRunnable() {
                @Override
                public void run() {
                    if(Bukkit.getServer().getPlayer(event.getPlayer().getName()) == null || !event.getPlayer().getWorld().getName().equals(A2b2tCore.QueueMap)) {
                        cancel();
                        return;
                    }
                    if(Bukkit.getServer().getPlayer(event.getPlayer().getName()) != null || event.getPlayer().getWorld().getName().equals(A2b2tCore.QueueMap)) {
                        if (A2b2tCore.queue.containsKey(event.getPlayer().getName())) {
                            if (A2b2tCore.EnableQueue && !event.getPlayer().hasPermission("2b2tcore.skipQueue")) {
                                for (Player v : Bukkit.getServer().getOnlinePlayers()) {
                                    if (!event.getPlayer().getName().equals(v.getName())) {
                                        event.getPlayer().hidePlayer(v);
                                    }
                                }
                                if (!A2b2tCore.queue.containsKey(event.getPlayer().getName())) {
                                    A2b2tCore.queue.put(event.getPlayer().getName(), new PlayerQueue(event.getPlayer(), new Random().nextInt(A2b2tCore.maxQueueSeconds - A2b2tCore.minQueueSeconds + 1) + A2b2tCore.minQueueSeconds, A2b2tCore.queue.size(), event.getPlayer().getWorld()));
                                } else {
                                    A2b2tCore.queue.get(event.getPlayer().getName()).joinServerTick++;
                                }
                                String positionMessage = A2b2tCore.PositionInQueue;
                                if (positionMessage.contains("%Position%")) {
                                    positionMessage = positionMessage.replaceAll("%Position%", String.valueOf(Math.abs(A2b2tCore.queue.get(event.getPlayer().getName()).position)));
                                }
                                positionMessage = StringUtils.format(positionMessage);
                                event.getPlayer().sendTitle(positionMessage, "", 0, 5, 15);
                            }
                            if (A2b2tCore.queue.get(event.getPlayer().getName()).joinServerTick >= A2b2tCore.queue.get(event.getPlayer().getName()).queueTime * 20 && A2b2tCore.queue.get(event.getPlayer().getName()).position <= 0) {
                                if (AuthMeApi.getInstance().isAuthenticated(event.getPlayer()) && Bukkit.getServer().getPlayer(event.getPlayer().getName()).getWorld().getName().equals(A2b2tCore.QueueMap)) {
                                    Bukkit.getServer().getPlayer(event.getPlayer().getName()).performCommand("2b2tCore:join");
                                }
                            }
                        } else {
                            if (AuthMeApi.getInstance().isAuthenticated(event.getPlayer()) && Bukkit.getServer().getPlayer(event.getPlayer().getName()).getWorld().getName().equals(A2b2tCore.QueueMap)) {
                                Bukkit.getServer().getPlayer(event.getPlayer().getName()).performCommand("2b2tCore:join");
                            }
                        }
                    }
                }
            }.runTaskTimerAsynchronously(A2b2tCore.getPlugin(A2b2tCore.class), 1L, 1L);
            if (Bukkit.getServer().getWorlds().stream().filter(v -> !v.getName().equals(A2b2tCore.QueueMap)).collect(Collectors.toList()).size() > 0) {
                Location location = event.getPlayer().getLocation();
                location.setWorld(Bukkit.getServer().getWorld(A2b2tCore.QueueMap));
                event.getPlayer().teleport(location);
            }
        }
    }
}
