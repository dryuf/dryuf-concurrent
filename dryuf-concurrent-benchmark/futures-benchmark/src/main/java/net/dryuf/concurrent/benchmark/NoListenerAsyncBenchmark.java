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

import com.google.common.util.concurrent.MoreExecutors;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Warmup;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;

public class NoListenerAsyncBenchmark
{
	public static final int         WARMUP_ITERATIONS = BenchmarkSupport.WARMUP_ITERATIONS;
	public static final int         COUNT = BenchmarkSupport.COUNT;

	@SuppressWarnings("unchecked")
	@Benchmark
	@Warmup(iterations = WARMUP_ITERATIONS)
	@Measurement(iterations = 2, batchSize = 1)
	@Fork(warmups = 1, value = 1)
	public void                     benchmarkJdk() throws Exception
	{
		FutureTask[] array = BenchmarkSupport.populateJdkFutureArray(COUNT);
		BenchmarkSupport.threadedRunFutures(array);
		for (FutureTask<Integer> f: array) {
			f.get();
		}
	}

	@SuppressWarnings("unchecked")
	@Benchmark
	@Warmup(iterations = WARMUP_ITERATIONS)
	@Measurement(iterations = 2, batchSize = 1)
	@Fork(warmups = 1, value = 1)
	public void                     benchmarkLwFuture() throws ExecutionException, InterruptedException
	{
		net.dryuf.concurrent.ListenableFutureTask[] array = BenchmarkSupport.populateLwFutureArray(COUNT);
		BenchmarkSupport.threadedRunFutures(array);
		for (net.dryuf.concurrent.ListenableFutureTask f: array) {
			f.get();
		}
	}

	@SuppressWarnings("unchecked")
	@Benchmark
	@Warmup(iterations = WARMUP_ITERATIONS)
	@Measurement(iterations = 2, batchSize = 1)
	@Fork(warmups = 1, value = 1)
	public void                     benchmarkGuava() throws ExecutionException, InterruptedException
	{
		Executor directExecutor = MoreExecutors.directExecutor();
		com.google.common.util.concurrent.ListenableFutureTask[] array = BenchmarkSupport.populateGuavaFutureArray(COUNT);
		BenchmarkSupport.threadedRunFutures(array);
		for (com.google.common.util.concurrent.ListenableFutureTask f: array) {
			f.get();
		}
	}

	@SuppressWarnings("unchecked")
	@Benchmark
	@Warmup(iterations = WARMUP_ITERATIONS)
	@Measurement(iterations = 2, batchSize = 1)
	@Fork(warmups = 1, value = 1)
	public void                     benchmarkSpring() throws ExecutionException, InterruptedException
	{
		org.springframework.util.concurrent.ListenableFutureTask[] array = BenchmarkSupport.populateSpringFutureArray(COUNT);
		BenchmarkSupport.threadedRunFutures(array);
		for (org.springframework.util.concurrent.ListenableFutureTask f: array) {
			f.get();
		}
	}
}
