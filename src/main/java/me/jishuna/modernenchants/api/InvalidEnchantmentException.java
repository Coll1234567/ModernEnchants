package me.jishuna.modernenchants.api;

public class InvalidEnchantmentException extends Exception {
	private static final long serialVersionUID = 4942679715600700625L;
	
	public InvalidEnchantmentException(String message) {
		super(message);
	}
}
