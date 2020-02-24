package com.syntaxphoenix.syntaxapi.utils.cache;

public class CachedObject<V> {

	private final V value;
	private long lastAccessed = System.currentTimeMillis();
	
	public CachedObject(V value) {
		this.value = value;
	}
	
	public long getLastAccess() {
		return lastAccessed;
	}
	
	public V getValue() {
		return getValue(true);
	}
	
	final V getValue(boolean update) {
		if(update)
			lastAccessed = System.currentTimeMillis();
		return value;
	}

}
