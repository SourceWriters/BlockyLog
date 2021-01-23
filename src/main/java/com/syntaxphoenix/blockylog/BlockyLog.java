package com.syntaxphoenix.blockylog;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;
import net.sourcewriters.minecraft.versiontools.shaded.syntaxapi.utils.java.Exceptions;

public class BlockyLog extends JavaPlugin {

    public static final CommandSender CONSOLE = Bukkit.getConsoleSender();

    /*
     * 
     */

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    /*
     * 
     * 
     * 
     */

    public static void print(Throwable throwable) {
        print("&4ERROR", "&c", throwable);
    }

    public static void print(String... messages) {
        print("&8INFO", "&7", messages);
    }

    public static void print(String message) {
        print("&8INFO", "&7", message);
    }

    public static void print(String state, String color, Throwable throwable) {
        print(state, color, Exceptions.stackTraceToStringArray(throwable));
    }

    public static void print(String state, String color, String... messages) {
        if (messages == null || messages.length == 0) {
            return;
        }
        for (String message : messages) {
            print(state, color, message);
        }
    }

    public static void print(String state, String color, String message) {
        CONSOLE.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&7BlockyLog / " + state + "&8] " + color + message));
    }

}
