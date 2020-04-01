package com.syntaxphoenix.blockylog.listener;

import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;

import com.syntaxphoenix.blockylog.BlockyApi;
import com.syntaxphoenix.blockylog.data.BlockyData;
import com.syntaxphoenix.blockylog.data.BlockyStorage;

public class BlockListener implements Listener {

	private final BlockyApi api;

	public BlockListener(BlockyApi api) {
		this.api = api;
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onBreak(BlockBreakEvent event) {
		if (event.isCancelled())
			return;
		Location loc = event.getBlock().getLocation();
		api.getStorage(loc.getWorld()).setData(new BlockyData(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), null));
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlace(BlockPlaceEvent event) {
		if (event.isCancelled())
			return;
		Location loc = event.getBlock().getLocation();
		api.getStorage(loc.getWorld()).setData(
				new BlockyData(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), event.getPlayer().getUniqueId()));
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onExtend(BlockPistonExtendEvent event) {
		if (event.isCancelled())
			return;
		List<Block> blocks = event.getBlocks();
		if (blocks.isEmpty())
			return;
		Location loc;
		BlockFace face = event.getDirection();
		for (Block block : blocks) {
			if (block == null)
				continue;
			BlockyStorage storage = api.getStorage((loc = block.getLocation()).getWorld());
			BlockyData data = storage.getData(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
			UUID player;
			if ((player = data.getPlayerId()) != null) {
				storage.setData(new BlockyData(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), null));
				storage.setData(new BlockyData(face.getModX() + loc.getBlockX(), face.getModY() + loc.getBlockY(),
						face.getModZ() + loc.getBlockZ(), player));
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onRetract(BlockPistonRetractEvent event) {
		if (event.isCancelled())
			return;
		List<Block> blocks = event.getBlocks();
		if (blocks.isEmpty())
			return;
		Location loc;
		BlockFace face = event.getDirection();
		for (Block block : blocks) {
			if (block == null)
				continue;
			BlockyStorage storage = api.getStorage((loc = block.getLocation()).getWorld());
			BlockyData data = storage.getData(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
			UUID player;
			if ((player = data.getPlayerId()) != null) {
				storage.setData(new BlockyData(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), null));
				storage.setData(new BlockyData(face.getModX() + loc.getBlockX(), face.getModY() + loc.getBlockY(),
						face.getModZ() + loc.getBlockZ(), player));
			}
		}
	}

}
