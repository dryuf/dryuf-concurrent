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

package net.dryuf.concurrent.benchmark;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;

public class BenchmarkSupport
{
	public static final int         WARMUP_ITERATIONS = 5;
	public static final int         COUNT = 100000;

	@SuppressWarnings("unchecked")
	public static FutureTask<Integer>[] populateJdkFutureArray(int count)
	{
		Callable<Integer> func = () -> { return 0; };
		FutureTask<Integer>[] array = new FutureTask[count];
		for (int i = 0; i < count; ++i) {
			array[i] = new FutureTask<Integer>(func);
		}
		return array;
	}

	@SuppressWarnings("unchecked")
	public static net.dryuf.concurrent.ListenableFutureTask<Integer>[] populateLwFutureArray(int count)
	{
		Callable<Integer> func = () -> { return 0; };
		net.dryuf.concurrent.ListenableFutureTask[] array = new net.dryuf.concurrent.ListenableFutureTask[count];
		for (int i = 0; i < count; ++i) {
			array[i] = new net.dryuf.concurrent.ListenableFutureTask<Integer>(func);
		}
		return array;
	}

	@SuppressWarnings("unchecked")
	public static org.springframework.util.concurrent.ListenableFutureTask<Integer>[] populateSpringFutureArray(int count)
	{
		Callable<Integer> func = () -> { return 0; };
		org.springframework.util.concurrent.ListenableFutureTask[] array = new org.springframework.util.concurrent.ListenableFutureTask[count];
		for (int i = 0; i < count; ++i) {
			array[i] = new org.springframework.util.concurrent.ListenableFutureTask(func);
		}
		return array;
	}

	@SuppressWarnings("unchecked")
	public static com.google.common.util.concurrent.ListenableFutureTask<Integer>[] populateGuavaFutureArray(int count)
	{
		Callable<Integer> func = () -> { return 0; };
		com.google.common.util.concurrent.ListenableFutureTask[] array = new com.google.common.util.concurrent.ListenableFutureTask[count];
		for (int i = 0; i < count; ++i) {
			array[i] = com.google.common.util.concurrent.ListenableFutureTask.create(func);
		}
		return array;
	}

	public static void                     threadedRunFutures(RunnableFuture<Integer>[] array)
	{
		Thread t = new Thread(() -> {
			for (RunnableFuture<Integer> v : array) {
				v.run();
			}
		});
		t.start();
		try {
			t.join();
		}
		catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}
