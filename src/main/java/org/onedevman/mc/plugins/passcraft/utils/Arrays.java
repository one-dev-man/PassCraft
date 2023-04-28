package org.onedevman.mc.plugins.passcraft.utils;

public class Arrays {

    public static <T> String join(T[] array, String separator) {
        return join(array, separator, 0);
    }

    public static <T> String join(T[] array, String separator, int included_start) {
        return join(array, separator, included_start, array.length);
    }

    public static <T> String join(T[] array, String separator, int included_start, int excluded_end) {
        StringBuilder result = new StringBuilder();

        for(int i = included_start; i < excluded_end-1; ++i)
            result.append(String.valueOf(array[i])).append(separator);

        if(excluded_end > 0)
            result.append(String.valueOf(array[excluded_end - 1]));

        return result.toString();
    }

}
