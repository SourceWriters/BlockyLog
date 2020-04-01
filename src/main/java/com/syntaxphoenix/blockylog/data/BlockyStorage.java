package com.syntaxphoenix.blockylog.data;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.bukkit.World;

import com.syntaxphoenix.blockylog.BlockyLog;
import com.syntaxphoenix.syntaxapi.utils.cache.CacheList;
import com.syntaxphoenix.syntaxapi.utils.java.Database;

public class BlockyStorage {

	private final CacheList<BlockyData> cache;
	private final ExecutorService connect = Executors.newSingleThreadExecutor();
	private final ExecutorService database = Executors.newCachedThreadPool();
	private final ExecutorService service = Executors.newCachedThreadPool();
	private final File file;
	private Connection connection;

	public BlockyStorage(World world) {
		cache = new CacheList<>(600, 300, 20000);
		file = new File(world.getWorldFolder(), "blockyData.sqlite");
		connect();
	}

	/*
	 * 
	 */

	private void connect() {
		Future<?> future = connect.submit(() -> {
			try {
				connection = Database.connectSQLite(file);
				connection.prepareStatement(
						"CREATE TABLE IF NOT EXISTS PlayerBlocks (x INTEGER NOT NULL, y INTEGER NOT NULL, z INTEGER NOT NULL, player TEXT, UNIQUE(x, y, z))")
						.executeUpdate();
			} catch (SQLException | IOException | ClassNotFoundException e) {
				BlockyLog.print(e);
			}
		});
		try {
			future.get(1, TimeUnit.MINUTES);
		} catch (Throwable e) {
			BlockyLog.print(e);
		}
	}

	public Connection connection() {
		try {
			if (connection == null || connection.isClosed() || connection.isReadOnly())
				connect();
		} catch (SQLException e) {
			BlockyLog.print(e);
		}
		return connection;
	}

	private Future<?> save(BlockyData data) {
		return database.submit(() -> {
			BlockyLog.print(data.toString());
			try {
				PreparedStatement statement;
				if (data.getPlayerId() == null) {
					statement = connection()
							.prepareStatement("DELETE FROM PlayerBlocks WHERE x = ? AND y = ? AND z = ?");
				} else {
					statement = connection().prepareStatement("REPLACE INTO PlayerBlocks VALUES (?, ?, ?, ?)");
					statement.setString(4, data.getPlayerId().toString());
				}
				statement.setInt(1, data.getX());
				statement.setInt(2, data.getY());
				statement.setInt(3, data.getZ());
				BlockyLog.print("Status: " + statement.executeUpdate());
			} catch (SQLException e) {
				BlockyLog.print(e);
			}
		});
	}

	public Future<?> close() {
		return Executors.newSingleThreadExecutor().submit(() -> {
			cache.clear();
			service.shutdown();
			try {
				service.awaitTermination(2, TimeUnit.MINUTES);
			} catch (InterruptedException e) {
				BlockyLog.print(e);
			}
			database.shutdown();
			try {
				database.awaitTermination(8, TimeUnit.MINUTES);
			} catch (InterruptedException e) {
				BlockyLog.print(e);
			}
			connect.shutdown();
			try {
				connect.awaitTermination(1, TimeUnit.MINUTES);
			} catch (InterruptedException e) {
				BlockyLog.print(e);
			}
			try {
				connection.close();
			} catch (SQLException e) {
				BlockyLog.print(e);
			}
		});
	}

	/*
	 * 
	 */

	public void setData(BlockyData data) {
		setData(data, true);
	}

	public void setData(BlockyData data, boolean updateDatabase) {
		if (updateDatabase)
			save(data);
		BlockyData cached = cache.get(data);
		if (cached != null) {
			if (cached.equalsExact(data))
				return;
			cache.remove(cached);
		}
		cache.add(data);
	}

	public BlockyData getData(int x, int y, int z) {
		Optional<BlockyData> data = getDataFromCache(x, y, z);
		if (data.isPresent()) {
			return data.get();
		} else {
			try {
				loadDataFromFile(x, y, z).get();
			} catch (InterruptedException | ExecutionException e) {
				return new BlockyData(x, y, z, null);
			}
			return (data = getDataFromCache(x, y, z)).isPresent() ? data.get() : new BlockyData(x, y, z, null);
		}
	}

	public Optional<BlockyData> getDataFromCache(int x, int y, int z) {
		return cache.getListCopy().stream().filter(data -> data.hasCoords(x, y, z)).findFirst();
	}

	public Future<?> loadDataFromFile(int x, int y, int z) {
		return service.submit(() -> {
			BlockyData output = null;
			try {
				PreparedStatement prepare = connection()
						.prepareStatement("SELECT * FROM PlayerBlocks WHERE x = ? AND y = ? AND z = ?");
				prepare.setInt(1, x);
				prepare.setInt(2, y);
				prepare.setInt(3, z);

				ResultSet result = prepare.executeQuery();
				while (result.next()) {
					output = new BlockyData(x, y, z, UUID.fromString(result.getString("player")));
				}
			} catch (Throwable throwable) {
				BlockyLog.print(throwable);
			}
			if (output != null)
				setData(output, false);
		});
	}

}
