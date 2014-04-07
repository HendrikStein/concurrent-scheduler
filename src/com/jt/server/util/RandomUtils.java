package com.jt.server.util;

import java.util.Random;

/**
 * Utility class to create different kind of random content.
 * 
 * @author Hendrik Stein
 * 
 */
public class RandomUtils {

    private RandomUtils() {
        // Utility class, static access only
    }

    /**
     * Get a random number.
     * 
     * @param min the minimum
     * @param max the maximum
     * @return the random number
     */
    public static int getRandomNumber(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }

}
