package me.jishuna.modernenchants.api;

public interface FailableFunction<T, R, E extends Throwable> {
	public R apply(T input) throws E;
}