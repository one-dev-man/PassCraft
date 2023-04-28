package org.onedevman.mc.plugins.passcraft.utils;

public class Strings {

    public static int sort(String str1, String str2) {
        int str1_length = str1.length();
        int str2_length = str2.length();

        int str1_score = 0;
        int str2_score = 0;

        for(int i = 0; i < str1_length && i < str2_length && str1_score == str2_score; ++i) {
            str1_score += str1.charAt(i);
            str2_score += str2.charAt(i);
        }

        return str1_score > str2_score ? 1 : 0;
    }

}
