package com.syntaxphoenix.blockylog;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import com.syntaxphoenix.blockylog.data.BlockyStorage;
import com.syntaxphoenix.blockylog.data.NumericSpace;

import net.md_5.bungee.api.ChatColor;
import net.sourcewriters.minecraft.versiontools.reflection.VersionControl;
import net.sourcewriters.minecraft.versiontools.reflection.data.persistence.DataDistributor;
import net.sourcewriters.minecraft.versiontools.shaded.syntaxapi.random.Keys;
import net.sourcewriters.minecraft.versiontools.shaded.syntaxapi.utils.java.Exceptions;
import net.sourcewriters.minecraft.versiontools.shaded.syntaxapi.utils.key.Namespace;

public class BlockyLog extends JavaPlugin {

    public static final CommandSender CONSOLE = Bukkit.getConsoleSender();
    public static final NumericSpace NAMESPACE = new NumericSpace(Namespace.of("blockylog"));

    private DataDistributor<String> distributor;
    private final ConcurrentHashMap<String, BlockyStorage> storages = new ConcurrentHashMap<>(); // Normally not needed but better for quick
                                                                                                 // access

    /*
     * 
     */

    @Override
    public void onLoad() {
        distributor = VersionControl.get().getDataProvider().createDistributor(new File(""), string -> string, () -> Keys.generateKey(9));
    }

    @Override
    public void onEnable() {
        if (!storages.isEmpty()) {
            return; // Already initialized
        }
        for (World world : Bukkit.getWorlds()) {
            storages.put(world.getName(), new BlockyStorage(distributor.get(world.getName())));
        }
    }

    @Override
    public void onDisable() {
        distributor.shutdown(); // Shutdown and afterwards
        distributor.delete(); // delete all cached data
        storages.clear();
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
