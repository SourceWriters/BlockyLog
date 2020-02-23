package com.syntaxphoenix.blockylog.listener;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockMultiPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;

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
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onMultiPlace(BlockMultiPlaceEvent event) {
		event.getReplacedBlockStates().forEach(state -> {
			Location loc = state.getLocation();
			api.getStorage(loc.getWorld()).setData(new BlockyData(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), event.getPlayer().getUniqueId()));
		});
	}
	
	/*
	 * 
	 */
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onChange(EntityChangeBlockEvent event) {
		if(event.getEntityType() == EntityType.PLAYER) {
			Location loc = event.getBlock().getLocation();
			api.getStorage(loc.getWorld()).setData(new BlockyData(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), event.getEntity().getUniqueId()));
			return;
		}
		Location loc = event.getBlock().getLocation();
		api.getStorage(loc.getWorld()).setData(new BlockyData(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), null));
	}
	
}
