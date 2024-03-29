package net.dryuf.concurrent.function;

import lombok.SneakyThrows;


/**
 * Three arguments consumer throwing an exception.
 *
 * @param <T>
 *	parameter type
 * @param <U>
 *	parameter type
 * @param <V>
 *	parameter type
 * @param <X>
 *	thrown exception
 *
 * @author
 * Copyright 2015-2023 Zbynek Vyskovsky mailto:kvr000@gmail.com http://github.com/kvr000/ https://github.com/dryuf/ https://www.linkedin.com/in/zbynek-vyskovsky/
 */
@FunctionalInterface
public interface ThrowingTriConsumer<T, U, V, X extends Exception>
{
	/**
	 * Consumes the value.
	 *
	 * @param p0
	 * 	input parameter
	 * @param p1
	 * 	input parameter
	 * @param p2
	 * 	input parameter
	 *
	 * @throws X
	 * 	in case of error.
	 */
	void accept(T p0, U p1, V p2) throws X;

	/**
	 * Consumes the value.
	 *
	 * @param p0
	 * 	input parameter
	 * @param p1
	 * 	input parameter
	 * @param p2
	 * 	input parameter
	 *
	 * @apiNote throws X
	 * 	in case of error.
	 */
	@SneakyThrows
	default void sneakyAccept(T p0, U p1, V p2)
	{
		accept(p0, p1, p2);
	}

	/**
	 * Converts this into {@link ThrowingTriConsumer}, propagating exceptions silently.
	 *
	 * @return
	 * 	converted {@link ThrowingTriConsumer} object.
	 */
	default ThrowingTriConsumer<T, U, V, RuntimeException> sneakyThrowing()
	{
		return sneakyThrowing(this);
	}

	/**
	 * Converts ThrowingTriConsumer into ThrowingTriConsumer, propagating exceptions silently.
	 *
	 * @param function
	 * 	original function
	 *
	 * @return
	 * 	converted {@link ThrowingTriConsumer} object.
	 *
	 * @param <T>
	 *      type of function parameter
	 * @param <U>
	 *     	type of parameter
	 * @param <V>
	 *     	type of parameter
	 * @param <X>
	 *      potential exception thrown by original function
	 */
	static <T, U, V, X extends Exception> ThrowingTriConsumer<T, U, V, RuntimeException> sneakyThrowing(ThrowingTriConsumer<T, U, V, X> function)
	{
		// Keep this expanded so mock instances still work correctly:
		return new ThrowingTriConsumer<T, U, V, RuntimeException>()
		{
			@Override
			@SneakyThrows
			public void accept(T t, U u, V v)
			{
				function.accept(t, u, v);
			}
		};
	}
}
