package me.miku.main;

import org.bukkit.Chunk;
import org.bukkit.Material;

public class ChunkUtils {
    public static int checkBlock(Chunk chunk, Material material) {
        int c = 0;
        for(int i = 0; i <= 16; i++) {
            for(int j = 0; j <= 16; j++) {
                for(int a = 0; a <= 256; a++) {
                    if(chunk.getBlock(i, a, j).getType() == material) {
                        c++;
                    }
                }
            }
        }
        return c;
    }
}
