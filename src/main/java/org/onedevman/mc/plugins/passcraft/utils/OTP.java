package org.onedevman.mc.plugins.passcraft.utils;

import java.util.Random;

public class OTP {

    private static final Random RANDOM = new Random();

    private static long NEXT_RANDOM_UPDATE_TIMESTAMP = -1;
    private static final int MAX_RANDOM_UPDATE_DELTA = 25;

    public static String gen(int length) {
        updateRandom();

        int start = 1;

        for(int i = 0; i < length; ++i) start = start*10;

        int otp = RANDOM.nextInt(start-1);

        return String.format("%d", start + otp).substring(1);
    }

    public static void updateRandom() {
        if(System.currentTimeMillis() > NEXT_RANDOM_UPDATE_TIMESTAMP) {
            NEXT_RANDOM_UPDATE_TIMESTAMP = System.currentTimeMillis() + RANDOM.nextInt(MAX_RANDOM_UPDATE_DELTA);
            RANDOM.setSeed(System.nanoTime());
        }
    }

}
