package net.dryuf.concurrent.function;

import org.testng.annotations.Test;

import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static org.testng.Assert.expectThrows;


public class ThrowingBiConsumerTest
{
	@Test
	public void sneaky_withException_thrown()
	{
		BiConsumer<Integer, Integer> runnable = ThrowingBiConsumer.sneaky((a, b) -> {
			throw new IOException();
		});

		expectThrows(IOException.class, () -> runnable.accept(5, 5));
	}

	@Test
	public void of_withRuntimeException_thrown()
	{
		ThrowingBiConsumer<Integer, Integer, RuntimeException> runnable = ThrowingBiConsumer.of((a, b) -> {
			throw new NumberFormatException();
		});

		expectThrows(NumberFormatException.class, () -> runnable.accept(5, 5));
	}
}
