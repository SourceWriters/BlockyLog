package com.syntaxphoenix.blockylog.data;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.bukkit.World;

import com.syntaxphoenix.blockylog.BlockyLog;
import com.syntaxphoenix.blockylog.storage.BlockyData;
import com.syntaxphoenix.blockylog.utils.FakeFuture;
import com.syntaxphoenix.syntaxapi.utils.java.Database;

public class BlockyStorage {

	private final ExecutorService service = Executors.newFixedThreadPool(3);
	private final ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();
	private final Map<BlockyData, Boolean> cache = Collections.synchronizedMap(new HashMap<BlockyData, Boolean>());
	private final File file;
	private Connection connection;

	public BlockyStorage(World world) {
		file = new File(world.getWorldFolder(), "blockyData.sqlite");
		timer.scheduleAtFixedRate(() -> saveAndClearCache(), 10, 10, TimeUnit.MINUTES);
		service.submit(() -> {
			try {
				connection = Database.connectSQLite(file);
				connection.prepareStatement(
						"CREATE TABLE IF NOT EXISTS PlayerBlocks (x INTEGER NOT NULL, y INTEGER NOT NULL, z INTEGER NOT NULL, player TEXT, UNIQUE(x, y, z))")
						.executeUpdate();
			} catch (SQLException | IOException e) {
				BlockyLog.print(e);
			}
		});
	}

	/*
	 * 
	 */

	private void saveAndClearCache() {
		ArrayList<BlockyData> delete = new ArrayList<>();
		ArrayList<BlockyData> uncache = new ArrayList<>();

		cache.entrySet().stream().forEach(entry -> {
			if (entry.getValue()) {
				delete.add(entry.getKey());
			} else {
				uncache.add(entry.getKey());
			}
		});

		if (!uncache.isEmpty()) {
			for (BlockyData data : uncache) {
				cache.put(data, true);
			}
		}

		if (delete.isEmpty()) {
			return;
		}
		PreparedStatement statement = null;
		try {
			statement = connection.prepareStatement("REPLACE INTO PlayerBlocks VALUES (?, ?, ?, ?)");
		} catch (SQLException e) {
			BlockyLog.print(e);
		}
		if (statement == null) {
			return;
		}

		ArrayList<BlockyData> removeCompletly = new ArrayList<>();
		int current = 0;
		for (BlockyData data : delete) {
			if (cache.get(data)) {
				cache.remove(data);
			}
			if (data.getPlayerId() == null) {
				removeCompletly.add(data);
				continue;
			}
			try {
				statement.setInt(1, data.getX());
				statement.setInt(2, data.getY());
				statement.setInt(3, data.getZ());
				statement.setString(4, data.getPlayerId().toString());

				statement.addBatch();
				current++;

				if (current % 1000 == 0 || current == delete.size()) {
					statement.executeBatch();
				}
			} catch (SQLException e) {
				BlockyLog.print(e);
			}
		}

		if (removeCompletly.isEmpty()) {
			return;
		}
		statement = null;
		try {
			statement = connection.prepareStatement("DELETE FROM PlayerBlocks WHERE x = ? AND y = ? AND z = ?");
		} catch (SQLException e) {
			BlockyLog.print(e);
		}
		if (statement == null) {
			return;
		}
		current = 0;
		for (BlockyData data : removeCompletly) {
			try {
				statement.setInt(1, data.getX());
				statement.setInt(2, data.getY());
				statement.setInt(3, data.getZ());

				statement.addBatch();
				current++;

				if (current % 1000 == 0 || current == removeCompletly.size()) {
					statement.executeBatch();
				}
			} catch (SQLException e) {
				BlockyLog.print(e);
			}
		}

	}

	public Future<?> saveCache() {
		return service.submit(() -> {
			PreparedStatement statement = null;
			try {
				statement = connection.prepareStatement("REPLACE INTO PlayerBlocks VALUES (?, ?, ?, ?)");
			} catch (SQLException e) {
				BlockyLog.print(e);
			}
			if (statement == null) {
				return;
			}

			ArrayList<BlockyData> removeCompletly = new ArrayList<>();
			int current = 0;
			Set<BlockyData> set = cache.keySet();
			for (BlockyData data : set) {
				if (cache.get(data)) {
					cache.remove(data);
				}
				if (data.getPlayerId() == null) {
					removeCompletly.add(data);
					continue;
				}
				try {
					statement.setInt(1, data.getX());
					statement.setInt(2, data.getY());
					statement.setInt(3, data.getZ());
					statement.setString(4, data.getPlayerId().toString());

					statement.addBatch();
					current++;

					if (current % 1000 == 0 || current == set.size()) {
						statement.executeBatch();
					}
				} catch (SQLException e) {
					BlockyLog.print(e);
				}
			}

			if (removeCompletly.isEmpty()) {
				return;
			}
			statement = null;
			try {
				statement = connection.prepareStatement("DELETE FROM PlayerBlocks WHERE x = ? AND y = ? AND z = ?");
			} catch (SQLException e) {
				BlockyLog.print(e);
			}
			if (statement == null) {
				return;
			}
			current = 0;
			for (BlockyData data : removeCompletly) {
				try {
					statement.setInt(1, data.getX());
					statement.setInt(2, data.getY());
					statement.setInt(3, data.getZ());

					statement.addBatch();
					current++;

					if (current % 1000 == 0 || current == removeCompletly.size()) {
						statement.executeBatch();
					}
				} catch (SQLException e) {
					BlockyLog.print(e);
				}
			}
		});
	}

	/*
	 * 
	 */

	public void setData(BlockyData data) {
		cache.put(data, false);
	}

	public Future<Optional<BlockyData>> getData(int x, int y, int z) {
		Optional<BlockyData> data = getDataFromCache(x, y, z);
		if (data.isPresent()) {
			return new FakeFuture<>(data);
		}
		return getDataFromFile(x, y, z);
	}

	public Optional<BlockyData> getDataFromCache(int x, int y, int z) {
		Optional<BlockyData> option = cache.keySet().stream()
				.filter(data -> data.getX() == x && data.getY() == y && data.getZ() == z).findFirst();
		if (option.isPresent()) {
			cache.put(option.get(), false);
		}
		return option;
	}

	public Future<Optional<BlockyData>> getDataFromFile(int x, int y, int z) {
		return service.submit(() -> {
			BlockyData output = null;
			try {
				PreparedStatement prepare = connection
						.prepareStatement("SELECT * FROM PlayerBlocks WHERE x = ? AND y = ? AND z = ?");
				prepare.setInt(1, x);
				prepare.setInt(2, y);
				prepare.setInt(3, z);

				ResultSet result = prepare.executeQuery();
				while (result.next()) {
					output = new BlockyData(x, y, z, UUID.fromString(result.getString("player")));
					break;
				}

			} catch (Throwable throwable) {
				BlockyLog.print(throwable);
			}
			return Optional.ofNullable(output);
		});
	}

}
