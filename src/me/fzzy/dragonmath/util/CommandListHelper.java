package me.fzzy.dragonmath.util;

import org.bukkit.entity.Player;

public class CommandListHelper {

    private static String[] commands = {
            "/dungeon save room [dungeonName]"
    };

    public static void displayHelp(int page, Player player) {
        for (String s : commands) {
            player.sendMessage(s);
        }
    }

}
