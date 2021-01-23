package com.syntaxphoenix.blockylog.data;

import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;

public final class BlockyData {

    private final int x, y, z;

    private final String world;
    private final UUID player;

    public BlockyData(String world, UUID player, int x, int y, int z) {
        this.world = world;
        this.player = player;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public BlockyData(World world, UUID player, int x, int y, int z) {
        this(world.getName(), player, x, y, z);
    }

    public BlockyData(World world, OfflinePlayer player, int x, int y, int z) {
        this(world.getName(), player.getUniqueId(), x, y, z);
    }

    public BlockyData(UUID player, int x, int y, int z) {
        this((String) null, player, x, y, z);
    }

    public BlockyData(OfflinePlayer player, int x, int y, int z) {
        this((String) null, player.getUniqueId(), x, y, z);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public UUID getPlayerId() {
        return player;
    }

    public Optional<Player> getPlayer() {
        return Optional.of(Bukkit.getPlayer(player));
    }

    public Optional<OfflinePlayer> getOfflinePlayer() {
        return Optional.of(Bukkit.getOfflinePlayer(player));
    }

    public String getWorldName() {
        return world;
    }

    public Optional<World> getWorld() {
        return Optional.of(Bukkit.getWorld(world));
    }

    public boolean hasWorld() {
        return world != null;
    }

    public boolean hasPlayer() {
        return player != null;
    }

}
