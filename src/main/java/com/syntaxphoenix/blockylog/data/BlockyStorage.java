package com.syntaxphoenix.blockylog.data;

import static com.syntaxphoenix.blockylog.BlockyLog.NAMESPACE;

import java.util.UUID;

import org.bukkit.Location;

import net.sourcewriters.minecraft.versiontools.reflection.data.persistence.PersistentContainer;

public class BlockyStorage {

    private final PersistentContainer<String> container;

    public BlockyStorage(PersistentContainer<String> container) {
        this.container = container;
    }

    public void set(BlockyData data) {
        if (data.hasPlayer()) {
            container.set(NAMESPACE.create(data.getX(), data.getY(), data.getZ()), data.getPlayerId(), UUIDDataType.TYPE);
            return;
        }
        container.remove(NAMESPACE.create(data.getX(), data.getY(), data.getZ()));
    }

    public BlockyData get(int x, int y, int z) {
        UUID uniqueId = container.get(NAMESPACE.create(x, y, z), UUIDDataType.TYPE);
        return new BlockyData(container.getKey(), uniqueId, x, y, z);
    }

    public BlockyData get(Location location) {
        return location == null ? null : get(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

}
