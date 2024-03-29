package net.dryuf.concurrent.function;

import org.testng.annotations.Test;

import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import static org.testng.Assert.expectThrows;


public class ThrowingBiFunctionTest
{
	@Test
	public void sneaky_withException_thrown()
	{
		BiFunction<Integer, Integer, Integer> runnable = ThrowingBiFunction.sneaky((a, b) -> {
			throw new IOException();
		});

		expectThrows(IOException.class, () -> runnable.apply(5, 5));
	}

	@Test
	public void of_withRuntimeException_thrown()
	{
		ThrowingBiFunction<Integer, Integer, Integer, IOException> runnable = ThrowingBiFunction.of((a, b) -> {
			throw new NumberFormatException();
		});

		expectThrows(NumberFormatException.class, () -> runnable.apply(5, 5));
	}
}
