package me.miku.main.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class NewOrNotChunk extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "newOrNotChunk";
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
        if(player.getPlayer().getChunk().isLoaded()) {
            return "OLD";
        } else {
            return "NEW";
        }
    }
}
