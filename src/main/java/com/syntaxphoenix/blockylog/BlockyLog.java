package com.syntaxphoenix.blockylog;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import com.syntaxphoenix.blockylog.data.BlockyStorage;
import com.syntaxphoenix.blockylog.listener.BlockListener;
import com.syntaxphoenix.syntaxapi.utils.java.Exceptions;

import net.md_5.bungee.api.ChatColor;

public class BlockyLog extends JavaPlugin {
	
	public static final CommandSender CONSOLE = Bukkit.getConsoleSender();
	
	/*
	 * 
	 */
	
	private final HashMap<UUID, BlockyStorage> storages = new HashMap<>();
	
	/*
	 * 
	 */
	
	@Override
	public void onEnable() {
		
		Bukkit.getPluginManager().registerEvents(new BlockListener(BlockyApi.api = new BlockyApi(this)), this);
		
	}
	
	@Override
	public void onDisable() {
		
		if(!storages.isEmpty()) {
			for(BlockyStorage storage : storages.values()) {
				try {
					storage.saveCache().get(2, TimeUnit.MINUTES);
				} catch (InterruptedException | ExecutionException | TimeoutException e) {
					BlockyLog.print(e);
				}
			}
		}
		
	}
	
	/*
	 * 
	 */
	
	public BlockyStorage getOrCreateStorage(World world) {
		UUID worldId = world.getUID();
		if(storages.containsKey(worldId)) {
			return storages.get(worldId);
		} else {
			BlockyStorage storage = new BlockyStorage(world);
			storages.put(worldId, storage);
			return storage;
		}
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
		if(messages == null || messages.length == 0) {
			return;
		}
		for(String message : messages) {
			print(state, color, message);
		}
	}
	
	public static void print(String state, String color, String message) {
		CONSOLE.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&7BlockyLog / " + state + "&8] " + color + message));
	}

}
