package me.jishuna.modernenchants.api.enchantment;

import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.NamespacedKey;

public class EnchanterData {
	
	private long seed;
	private NamespacedKey[] keys = new NamespacedKey[3];
	private int[] levels = new int[3];
	
	public EnchanterData() {
		randomizeSeed();
	}
	
	public void randomizeSeed() {
		this.seed = ThreadLocalRandom.current().nextLong();
	}
	
	public long getSeed() {
		return this.seed;
	}
	
	public NamespacedKey getKey(int slot) {
		return this.keys[slot];
	}
	
	public void setKey(int slot, NamespacedKey key) {
		this.keys[slot] = key;
	}
	
	public int getLevel(int slot) {
		return this.levels[slot];
	}
	
	public void setLevel(int slot, int level) {
		this.levels[slot] = level;
	}

}
