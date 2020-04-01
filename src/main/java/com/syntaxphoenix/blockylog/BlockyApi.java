package com.syntaxphoenix.blockylog;

import org.bukkit.Location;
import org.bukkit.World;

import com.syntaxphoenix.blockylog.data.BlockyStorage;

public final class BlockyApi {

	protected static BlockyApi api;
	
	public static BlockyApi getApi() {
		return api;
	}
	
	/*
	 * 
	 */
	
	private final BlockyLog blockyLog;
	
	protected BlockyApi(BlockyLog blockyLog) {
		this.blockyLog = blockyLog;
	}
	
	public BlockyStorage getStorage(World world) {
		return blockyLog.getOrCreateStorage(world);
	}
	
	public boolean isPlayerPlaced(Location location) {
		return getStorage(location.getWorld()).getData(location.getBlockX(), location.getBlockY(), location.getBlockZ()).getPlayerId() != null;
	}
	
}
