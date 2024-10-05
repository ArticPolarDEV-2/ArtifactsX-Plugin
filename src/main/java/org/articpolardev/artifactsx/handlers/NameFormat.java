package org.articpolardev.artifactsx.handlers;

import net.md_5.bungee.api.ChatColor;

public class NameFormat {
    public static String RelicNameFormat(String hex, String name) {
        return ChatColor.of(hex) + "" + ChatColor.BOLD + ChatColor.UNDERLINE + name;
    }
}
