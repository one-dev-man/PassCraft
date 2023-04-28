package org.onedevman.mc.plugins.passcraft.utils;

public class AnsiColor {

    public enum Code {
        DARK_RED    ("§4", "\u001B[31m"),
        RED         ("§c", "\u001B[91m"),
        GOLD        ("§6", "\u001B[33m"),
        YELLOW      ("§e", "\u001B[93m"),
        DARK_GREEN  ("§2", "\u001B[32m"),
        GREEN       ("§a", "\u001B[92m"),
        AQUA        ("§b", "\u001B[96m"),
        DARK_AQUA   ("§3", "\u001B[36m"),
        DARK_BLUE   ("§1", "\u001B[34m"),
        BLUE        ("§9", "\u001B[94m"),
        LIGHT_PURPLE("§d", "\u001B[95m"),
        DARK_PURPLE ("§5", "\u001B[35m"),
        WHITE       ("§f", "\u001B[97m"),
        GRAY        ("§7", "\u001B[37m"),
        DARK_GRAY   ("§8", "\u001B[90m"),
        BLACK       ("§0", "\u001B[30m"),
        RESET       ("§r", "\u001B[0m"),
        BOLD        ("§l", "\u001B[1m"),
        ITALIC      ("§o", "\u001B[3m"),
        UNDERLINED  ("§n", "\u001B[4m");

        public final String regex;
        public final String replacement;

        Code(String regex, String replacement) {
            this.regex = regex;
            this.replacement = replacement;
        }
    }

    //

    public static String parse(String content) {
        String result = content;

        for(Code code : Code.values())
            result = result.replaceAll(code.regex, code.replacement);

        return result;
    }

}
