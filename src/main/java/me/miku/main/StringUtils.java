package me.miku.main;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StringUtils {
    public static String format(String string) {
        String rtn = string;
        if(rtn.contains("%ShulkderDupe%")) {
            rtn = rtn.replaceAll("%ShulkderDupe%", String.valueOf(A2b2tCore.shulkerDupe));
        }
        if(rtn.contains("%AChunkMaxPiston%")) {
            rtn = rtn.replaceAll("%AChunkMaxPiston%", String.valueOf(A2b2tCore.AChunkMaxPiston));
        }
        if(rtn.contains("%AChunkMaxRedStone%")) {
            rtn = rtn.replaceAll("%AChunkMaxRedStone%", String.valueOf(A2b2tCore.AChunkMaxRedStone));
        }
        if(rtn.contains("%fzDupeMaxTime%")) {
            rtn = rtn.replaceAll("%fzDupeMaxTime%", String.valueOf(A2b2tCore.fzDupeMaxTime));
        }
        if(rtn.contains("%colorPreFix%")) {
            rtn = rtn.replaceAll("%colorPreFix%", String.valueOf(A2b2tCore.prefix.charAt(0)));
        }
        if(rtn.contains("%Year%")) {
            rtn = rtn.replaceAll("%Year%", String.valueOf(Date.from(Instant.now()).getYear()));
        }
        if(rtn.contains("%Month%")) {
            rtn = rtn.replaceAll("%Month%", String.valueOf(Date.from(Instant.now()).getMonth()));
        }
        if(rtn.contains("%Day%")) {
            rtn = rtn.replaceAll("%Day%", String.valueOf(Date.from(Instant.now()).getDay()));
        }
        if(rtn.contains("%Hour%")) {
            rtn = rtn.replaceAll("%Hour%", String.valueOf(Date.from(Instant.now()).getHours()));
        }
        if(rtn.contains("%Minute%")) {
            rtn = rtn.replaceAll("%Minute%", String.valueOf(Date.from(Instant.now()).getMinutes()));
        }
        if(rtn.contains("%Second%")) {
            rtn = rtn.replaceAll("%Second%", String.valueOf(Date.from(Instant.now()).getSeconds()));
        }
        if(rtn.contains("offline")) {
            rtn = rtn.replaceAll("%offline%", String.valueOf(Bukkit.getServer().getOnlinePlayers().size()));
        }
        return rtn;
    }
    public static int getCharAmount(String source, char chr) {
        int rtn = 0;
        for(char v : source.toCharArray()) {
            if(v == chr) {
                rtn++;
            }
        }
        return rtn;
    }

    public static List<String> getVariables(String string) {
        List<String> rtn = new ArrayList<>();
        String rrr = string;
        for(int i = 0; i < getCharAmount(string, '%') / 2; i++) {
            rtn.add(rrr.substring(rrr.indexOf("%"), rrr.substring(0, rrr.indexOf("%")).length() + rrr.substring(rrr.indexOf("%") + 1).indexOf("%") + 2));
            rrr = rrr.replaceAll(rrr.substring(rrr.indexOf("%"), rrr.substring(0, rrr.indexOf("%")).length() + rrr.substring(rrr.indexOf("%") + 1).indexOf("%") + 2), "");
        }
        return rtn;
    }

    public static String translatePlaceholderAPI(String string, OfflinePlayer player) {
        String rrr = string;
        for(int i = 0; i < getCharAmount(string, '%') / 2; i++) {
            if(rrr.contains("%")) {
                String nn = rrr.substring(rrr.indexOf("%"), rrr.substring(0, rrr.indexOf("%")).length() + rrr.substring(rrr.indexOf("%") + 1).indexOf("%") + 2);
                rrr = rrr.replaceAll(nn, PlaceholderAPI.setPlaceholders(player, nn));
            }
        }
        return rrr;
    }
}
