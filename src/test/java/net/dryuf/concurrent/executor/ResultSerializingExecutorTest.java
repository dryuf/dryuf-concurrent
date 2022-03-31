package net.dryuf.concurrent.executor;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


/**
 * Tests for {@link ResultSerializingExecutor}.
 */
public class ResultSerializingExecutorTest
{
	@Test
	public void testShort() throws InterruptedException
	{
		for (int t = 0; t < 1024; ++t) {
			try (ResultSerializingExecutor rse = new ResultSerializingExecutor()) {
				for (int i = ThreadLocalRandom.current().nextInt(512); --i >= 0; ) {
					rse.submit(this::doLittle);
				}
			}
		}
	}

	@Test
	public void testSerialization() throws InterruptedException
	{
		List<Integer> expected = IntStream.rangeClosed(0, 1023).boxed().collect(Collectors.toList());
		List<Integer> result = new ArrayList<>();
		try (CloseableExecutor executor = new ClosingExecutor(Executors.newCachedThreadPool());
		     ResultSerializingExecutor rse = new ResultSerializingExecutor(executor)) {
			for (int i = 0; i < 1024; ++i) {
				final int i0 = i;
				rse.submit(() -> { Thread.sleep(ThreadLocalRandom.current().nextInt(10)); return i0; })
						.thenAccept(result::add);

			}
		}
		Assert.assertEquals(result, expected);
	}

	private Void doLittle()
	{
		littleVar *= 17;
		return null;
	}

	private int littleVar = 13;
}