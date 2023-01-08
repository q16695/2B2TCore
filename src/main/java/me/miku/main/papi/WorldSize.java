package me.miku.main.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class WorldSize extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "WorldSize";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Mik_u_";
    }

    @Override
    public @NotNull String getVersion() {
        return "Null";
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if(params.equals("player")) {
            float size = FileUtils.sizeOfDirectory(new File(player.getPlayer().getWorld().getWorldFolder().getPath()));
            float s = size / 1024 / 1024 / 1024;
            String st = String.valueOf(s);
            return st.substring(0, 4);
        } else if(params.equals("console")) {
            float size = 0.0F;
            for(World world : Bukkit.getWorlds()) {
                size+=FileUtils.sizeOfDirectory(new File(world.getWorldFolder().getPath()));
            }
            float s = size / 1024 / 1024 / 1024;
            String st = String.valueOf(s);
            return st.substring(0, 4);
        }
        return super.onRequest(player, params);
    }
}
