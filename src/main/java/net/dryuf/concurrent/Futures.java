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

import java.util.Arrays;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;


/**
 * Futures utilities.
 *
 * @author
 * Copyright 2015-2022 Zbynek Vyskovsky mailto:kvr000@gmail.com http://github.com/kvr000/ https://github.com/dryuf/ https://www.linkedin.com/in/zbynek-vyskovsky/
 */
public final class Futures
{
	/**
	 * Private constructor to avoid instantiation.
	 */
	private                         Futures()
	{
	}

	/**
	 * Returns future which already completed with success.
	 *
	 * @param result
	 *      future result
	 *
	 * @param <V>
	 *         future type
	 *
	 * @return
	 *      successfully completed future
	 */
	public static <V> ListenableFuture<V> successFuture(V result)
	{
		SettableFuture<V> future = new SettableFuture<V>();
		future.set(result);
		return future;
	}

	/**
	 * Returns future which already completed with failure.
	 *
	 * @param ex
	 *      exception that caused failure
	 *
	 * @param <V>
	 *      future type
	 *
	 * @return
	 *      future completed with exception {@code ex}.
	 */
	public static <V> ListenableFuture<V> failedFuture(Throwable ex)
	{
		SettableFuture<V> future = new SettableFuture<V>();
		future.setException(ex);
		return future;
	}

	/**
	 * Returns future which was cancelled.
	 *
	 * @param <V>
	 *      future type
	 *
	 * @return
	 *      cancelled future
	 */
	public static <V> ListenableFuture<V> cancelledFuture()
	{
		SettableFuture<V> future = new SettableFuture<V>();
		future.cancel(true);
		return future;
	}

	/**
	 * Returns future that will be completed once all of the dependent futures are completed.
	 *
	 * If any of the futures fail this future will fail immediatelly.
	 *
	 * @param futures
	 *      dependent futures
	 *
	 * @return
	 *      future that will complete once all of {@code futures} complete
	 */
	public static ListenableFuture<Void> allOf(Iterable<ListenableFuture<?>> futures)
	{
		return new AllOfFuture(futures);
	}

	/**
	 * Returns future that will be completed once all of the dependent futures are completed.
	 *
	 * If any of the futures fail this future will fail immediately.
	 *
	 * @param futures
	 *      dependent futures
	 *
	 * @return
	 *      future that will complete once all of {@code futures} complete
	 */
	public static ListenableFuture<Void> allOf(ListenableFuture<?>... futures)
	{
		return new AllOfFuture(Arrays.asList(futures));
	}

	/**
	 * Returns future that will be completed once all of the dependent futures are completed.
	 *
	 * If any of the futures fail this future will fail immediately.
	 *
	 * @param futures
	 *      dependent futures
	 * @param <V>
	 *      type of value
	 *
	 * @return
	 *      future that will complete once all of {@code futures} complete
	 */
	public static <V> ListenableFuture<V> anyOf(Iterable<ListenableFuture<V>> futures)
	{
		return new AnyOfFuture<V>(futures);
	}

	/**
	 * Returns future that will be completed once all of the dependent futures are completed.
	 *
	 * If any of the futures fail this future will fail immediately.
	 *
	 * @param futures
	 *      dependent futures
	 * @param <V>
	 *      type of value
	 *
	 * @return
	 *      future that will complete once all of {@code futures} complete
	 */
	@SafeVarargs
	public static <V> ListenableFuture<V> anyOf(ListenableFuture<V>... futures)
	{
		return new AnyOfFuture<V>(Arrays.asList(futures));
	}

	/**
	 * Cancels all futures in the list.
	 *
	 * @param futures
	 *      futures to be cancelled
	 *
	 * @return
	 *      number of futures actually cancelled
	 */
	public static int               cancelAll(Iterable<? extends Future<?>> futures)
	{
		int count = 0;
		for (Future<?> future: futures) {
			count += future.cancel(true) ? 1 : 0;
		}
		return count;
	}

	private static class AllOfFuture extends AbstractFuture<Void> implements FutureListener<Object>
	{
		@SuppressWarnings("unchecked")
		public                          AllOfFuture(Iterable<ListenableFuture<?>> futures)
		{
			this.futures = futures;
			for (ListenableFuture<?> future: futures) {
				counterUpdater.incrementAndGet(this);
				((ListenableFuture<Object>)future).addListener(this);
			}
			counterUpdater.decrementAndGet(this);
		}

		@Override
		public void                     onSuccess(Object result)
		{
			if (counterUpdater.decrementAndGet(this) == 0)
				set(null);
		}

		@Override
		public void                     onFailure(Throwable ex)
		{
			setException(ex);
			interruptTask();
			counterUpdater.decrementAndGet(this);
		}

		@Override
		public void                     onCancelled()
		{
			cancel(true);
			counterUpdater.decrementAndGet(this);
		}

		@Override
		public void                     interruptTask()
		{
			if (futures != null) {
				for (ListenableFuture<?> future : futures)
					future.cancel(true);
				futures = null;
			}
		}

		private Iterable<ListenableFuture<?>> futures;

		private volatile int            counter = 1;

		private static final AtomicIntegerFieldUpdater<AllOfFuture> counterUpdater = AtomicIntegerFieldUpdater.newUpdater(AllOfFuture.class, "counter");
	}


	private static class AnyOfFuture<V> extends AbstractFuture<V> implements FutureListener<V>
	{
		@SuppressWarnings("unchecked")
		public                          AnyOfFuture(Iterable<ListenableFuture<V>> futures)
		{
			this.futures = futures;
			for (ListenableFuture<V> future: futures) {
				future.addListener(this);
			}
		}

		@Override
		public void                     onSuccess(V result)
		{
			if (set(result)) {
				interruptTask();
			}
		}

		@Override
		public void                     onFailure(Throwable ex)
		{
			if (setException(ex)) {
				interruptTask();
			}
		}

		@Override
		public void                     onCancelled()
		{
		}

		@Override
		public void                     interruptTask()
		{
			if (futures != null) {
				for (ListenableFuture<?> future : futures)
					future.cancel(true);
				futures = null;
			}
		}

		private Iterable<ListenableFuture<V>> futures;
	}
}
