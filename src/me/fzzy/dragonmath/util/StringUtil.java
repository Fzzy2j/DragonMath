package me.fzzy.dragonmath.util;

public class StringUtil {

    public static String capitalizeFirst(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }

}
