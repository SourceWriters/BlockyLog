package com.syntaxphoenix.blockylog.data;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;

import org.bukkit.World;

import com.syntaxphoenix.blockylog.BlockyLog;
import com.syntaxphoenix.blockylog.storage.BlockyData;
import com.syntaxphoenix.blockylog.utils.FakeFuture;
import com.syntaxphoenix.syntaxapi.utils.cache.CacheList;
import com.syntaxphoenix.syntaxapi.utils.java.Database;

public class BlockyStorage {

	private final CacheList<BlockyData> cache;
	private final ExecutorService service = Executors.newFixedThreadPool(3);
	private final File file;
	private Connection connection;

	public BlockyStorage(World world) {
		cache = new CacheList<>(600, 300, 20000, saveAndClear());
		file = new File(world.getWorldFolder(), "blockyData.sqlite");
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

	private Consumer<ArrayList<BlockyData>> saveAndClear() {
		return delete -> {
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
		};
	}

	public Future<?> saveCache() {
		return service.submit(() -> {
			saveAndClear().accept(cache.getListCopy());
		});
	}

	/*
	 * 
	 */

	public void setData(BlockyData data) {
		BlockyData cached = cache.get(data);
		if (cached != null) {
			if (cached.equalsExact(data))
				return;
			cache.remove(cached);
		}
		cache.add(data);
	}

	public Future<Optional<BlockyData>> getData(int x, int y, int z) {
		Optional<BlockyData> data = getDataFromCache(x, y, z);
		if (data.isPresent()) {
			return new FakeFuture<>(data);
		}
		return getDataFromFile(x, y, z);
	}

	public Optional<BlockyData> getDataFromCache(int x, int y, int z) {
		return cache.getListCopy().stream().filter(data -> data.hasCoords(x, y, z)).findFirst();
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
				if (result.next()) {
					output = new BlockyData(x, y, z, UUID.fromString(result.getString("player")));
				}
			} catch (Throwable throwable) {
				BlockyLog.print(throwable);
			}
			if (output != null)
				cache.add(output);
			return Optional.ofNullable(output);
		});
	}

}
