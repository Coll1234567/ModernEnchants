package me.jishuna.modernenchants.api.exception;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InvalidEnchantmentException extends Exception {
	private static final long serialVersionUID = 4942679715600700625L;
	private final List<String> messages = new ArrayList<>();

	public InvalidEnchantmentException(String message) {
		super(message);
		this.messages.add(message);
	}

	public void addAdditionalInfo(String info) {
		this.messages.add(0, info);
	}

	public void log(Logger logger) {
		for (int i = 0; i < messages.size(); i++) {
			logger.log(Level.WARNING, "{0}{1}", new Object[] { " ".repeat(i * 2), messages.get(i) });
		}
	}
}
