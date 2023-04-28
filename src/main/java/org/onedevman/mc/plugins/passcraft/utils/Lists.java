package org.onedevman.mc.plugins.passcraft.utils;

import java.util.*;

public class Lists {

    public static <T> List<T> toList(Iterator<T> iterator) {
        List<T> result = new ArrayList<>();

        while(iterator.hasNext())
            result.add(iterator.next());

        return result;
    }

    public static <T> List<T> toList(Collection<T> collection) {
        return toList(collection.iterator());
    }

    public static <T> List<T> toList(Set<T> set) {
        return toList(set.iterator());
    }

    //

    public static <T> List<T> copy(List<T> list) {
        int list_size = list.size();

        List<T> result = new ArrayList<>();

        for(int i = 0; i < list_size; ++i)
            result.add(list.get(i));

        return result;
    }

    //

    public static <T> boolean remove(List<T> list, T item) {
        int list_size = list.size();

        boolean removed = false;
        for(int i = list_size-1; i > -1 && !removed; --i) {
            removed = list.get(i).equals(item);

            if(removed)
                list.remove(i);
        }

        return removed;
    }

    //

    public static <T> boolean isModifiable(List<T> list) {
        return !list.getClass().getName().contains("Unmodifiable");
    }

}
