package me.miku.main;

import java.util.Random;

public class RandomUtils {
    private static final Random random = new Random();

    public static int random(int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }

    public static int random(int distance) {
        return random(-distance, distance);
    }
}
