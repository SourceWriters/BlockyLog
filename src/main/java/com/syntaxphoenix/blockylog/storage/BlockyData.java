package com.syntaxphoenix.blockylog.storage;

import java.util.UUID;

public class BlockyData {

	private final int x, y, z;
	private UUID player;
	
	public BlockyData(int x, int y, int z, UUID player) {
		this.player = player;
		this.x = x;
		this.y = y;
		this.z = z;
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
	
	public void setPlayerId(UUID player) {
		this.player = player;
	}
	
	@Override
	public boolean equals(Object object) {
		if(object instanceof BlockyData) {
			BlockyData data = (BlockyData) object;
			return data.x == x && data.y == y && data.z == z;
		}
		return false;
	}

	public boolean equalsExact(BlockyData data) {
		if(data.player == null && player == null)
			return equals(data);
		if(data.player == null || player == null)
			return false;
		return equals(data);
	}

	public boolean hasCoords(int x, int y, int z) {
		return this.x == x && this.y == y && this.z == z;
	}
	
}
