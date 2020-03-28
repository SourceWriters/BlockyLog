package com.syntaxphoenix.blockylog.listener;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import com.syntaxphoenix.blockylog.BlockyApi;
import com.syntaxphoenix.blockylog.storage.BlockyData;

public class BlockListener implements Listener {
	
	private final BlockyApi api;
	
	public BlockListener(BlockyApi api) {
		this.api = api;
	}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onBreak(BlockBreakEvent event) {
		Location loc = event.getBlock().getLocation();
		api.getStorage(loc.getWorld()).setData(new BlockyData(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), null));
	}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlace(BlockPlaceEvent event) {
		Location loc = event.getBlock().getLocation();
		api.getStorage(loc.getWorld()).setData(new BlockyData(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), event.getPlayer().getUniqueId()));
	}
	
}
