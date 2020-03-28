package com.syntaxphoenix.blockylog;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.bukkit.Location;
import org.bukkit.World;

import com.syntaxphoenix.blockylog.data.BlockyStorage;
import com.syntaxphoenix.blockylog.storage.BlockyData;

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
	
	public boolean isPlayerPlaced(Location location) throws InterruptedException, ExecutionException, TimeoutException {
		return isPlayerPlaced(location, 3, TimeUnit.SECONDS);
	}
	
	public boolean isPlayerPlaced(Location location, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		Optional<BlockyData> data = getStorage(location.getWorld()).getData(location.getBlockX(), location.getBlockY(), location.getBlockZ()).get(timeout, unit);
		return data.isPresent() ? data.get().getPlayerId() != null : false;
	}
	
}
