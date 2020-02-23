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
	
}
