package me.jishuna.modernenchants.api;

import java.util.LinkedHashMap;

public class FixedSizeLinkedHashMap<K, V> extends LinkedHashMap<K, V> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -800060867094207435L;
	private final int max_size;

	public FixedSizeLinkedHashMap(int size) {
		this.max_size = size;
	}

	@Override
	protected boolean removeEldestEntry(java.util.Map.Entry<K, V> eldest) {
		return size() > max_size;
	}
}
