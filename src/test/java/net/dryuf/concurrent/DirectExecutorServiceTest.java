/*
 * Copyright 2015-2022 Zbynek Vyskovsky mailto:kvr000@gmail.com http://github.com/kvr000/ https://github.com/dryuf/ https://www.linkedin.com/in/zbynek-vyskovsky/
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.dryuf.concurrent;

import org.testng.AssertJUnit;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Tests for {@link DirectExecutorService DirectExecutorService} class.
 *
 * @author
 * Copyright 2015-2022 Zbynek Vyskovsky mailto:kvr000@gmail.com http://github.com/kvr000/ https://github.com/dryuf/ https://www.linkedin.com/in/zbynek-vyskovsky/
 */
public class DirectExecutorServiceTest
{
	@Test
	public void                     testExecute() throws InterruptedException
	{
		final AtomicInteger result = new AtomicInteger();
		getExecutor().execute(new Runnable() {
			@Override
			public void run() {
				result.incrementAndGet();
			}
		});
		AssertJUnit.assertEquals(1, result.get());
	}

	@Test
	public void                     testException() throws InterruptedException
	{
		final AtomicInteger result = new AtomicInteger();
		getExecutor().execute(new Runnable() {
			@Override
			public void run() {
				result.incrementAndGet();
				throw new TestingRuntimeException();
			}
		});
		AssertJUnit.assertEquals(1, result.get());
	}

	@Test
	public void			testLifecycle1() throws InterruptedException
	{
		ListeningExecutorService executor = getExecutor();
		AssertJUnit.assertFalse(executor.isTerminated());
		AssertJUnit.assertFalse(executor.isShutdown());
		executor.shutdown();
		executor.shutdownNow();
		AssertJUnit.assertFalse(executor.isTerminated());
		AssertJUnit.assertFalse(executor.isShutdown());
		AssertJUnit.assertTrue(executor.awaitTermination(0, TimeUnit.MILLISECONDS));
	}

	private static ListeningExecutorService getExecutor()
	{
		return ListeningExecutors.directExecutorService();
	}
}
