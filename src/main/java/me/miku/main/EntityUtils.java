package me.miku.main;

import org.bukkit.Chunk;
import org.bukkit.entity.EntityType;

import java.util.Arrays;
import java.util.stream.Collectors;

public class EntityUtils {
    public static int getEntityCount(Chunk chunk, EntityType entityType) {
        return Arrays.stream(chunk.getEntities()).filter(v -> v.getType() == entityType).collect(Collectors.toList()).size();
    }
}
