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

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;


public class AbstractFutureListenerTest
{
	@Test(timeOut = 1000L)
	public void                     testListenersSet()
	{
		final BlockingQueue<Integer> queue = new LinkedBlockingQueue<Integer>();
		SettableFuture<Void> future = new SettableFuture<Void>();
		future.addListener(null, null, null);
		future.addListener(new Runnable() {
			@Override
			public void run() {
				queue.add(1);
			}
		});
		future.addListener(new FutureNotifier<Future<Void>>() {
			@Override
			public void accept(Future<Void> future) {
				queue.add(2);
			}
		});
		future.addListener(new DefaultFutureListener<Void>() {
			@Override
			public void onSuccess(Void value) {
				queue.add(3);
			}
		});
		future.addListener(new SuccessListener<Void>() {
			@Override
			public void onSuccess(Void value) {
				queue.add(4);
			}
		}, null, null);
		future.addAsyncListener(null, null, null, ListeningExecutors.directExecutor());
		future.addAsyncListener(new Runnable()
		{
			@Override
			public void run()
			{
				queue.add(11);
			}
		}, ListeningExecutors.directExecutor());
		future.addAsyncListener(new FutureNotifier<Future<Void>>()
		{
			@Override
			public void accept(Future<Void> future)
			{
				queue.add(12);
			}
		}, ListeningExecutors.directExecutor());
		future.addAsyncListener(new DefaultFutureListener<Void>()
		{
			@Override
			public void onSuccess(Void value)
			{
				queue.add(13);
			}
		}, ListeningExecutors.directExecutor());
		future.addAsyncListener(new SuccessListener<Void>()
		{
			@Override
			public void onSuccess(Void value)
			{
				queue.add(14);
			}
		}, null, null, ListeningExecutors.directExecutor());
		queue.add(0);
		future.set(null);
		AssertJUnit.assertEquals(0, (int)queue.remove());
		AssertJUnit.assertEquals(1, (int)queue.remove());
		AssertJUnit.assertEquals(2, (int)queue.remove());
		AssertJUnit.assertEquals(3, (int)queue.remove());
		AssertJUnit.assertEquals(4, (int)queue.remove());
		AssertJUnit.assertEquals(11, (int)queue.remove());
		AssertJUnit.assertEquals(12, (int)queue.remove());
		AssertJUnit.assertEquals(13, (int)queue.remove());
		AssertJUnit.assertEquals(14, (int)queue.remove());
		AssertJUnit.assertNull(queue.poll());
	}

	@Test(timeOut = 1000L)
	public void                     testListenersExcepted()
	{
		final BlockingQueue<Integer> queue = new LinkedBlockingQueue<Integer>();
		SettableFuture<Void> future = new SettableFuture<Void>();
		future.addListener(null, null, null);
		future.addListener(new Runnable() {
			@Override
			public void run() {
				queue.add(1);
			}
		});
		future.addListener(new FutureNotifier<Future<Void>>() {
			@Override
			public void accept(Future<Void> future) {
				queue.add(2);
			}
		});
		future.addListener(new DefaultFutureListener<Void>() {
			@Override
			public void onFailure(Throwable ex) {
				queue.add(3);
			}
		});
		future.addListener(null, new FailureListener() {
			@Override
			public void onFailure(Throwable ex) {
				queue.add(4);
			}
		}, null);
		future.addAsyncListener(null, null, null, ListeningExecutors.directExecutor());
		future.addAsyncListener(new Runnable()
		{
			@Override
			public void run()
			{
				queue.add(11);
			}
		}, ListeningExecutors.directExecutor());
		future.addAsyncListener(new FutureNotifier<Future<Void>>()
		{
			@Override
			public void accept(Future<Void> future)
			{
				queue.add(12);
			}
		}, ListeningExecutors.directExecutor());
		future.addAsyncListener(new DefaultFutureListener<Void>()
		{
			@Override
			public void onFailure(Throwable ex)
			{
				queue.add(13);
			}
		}, ListeningExecutors.directExecutor());
		future.addAsyncListener(null, new FailureListener()
		{
			@Override
			public void onFailure(Throwable ex)
			{
				queue.add(14);
			}
		}, null, ListeningExecutors.directExecutor());
		queue.add(0);
		future.setException(new NumberFormatException());
		AssertJUnit.assertEquals(0, (int)queue.remove());
		AssertJUnit.assertEquals(1, (int)queue.remove());
		AssertJUnit.assertEquals(2, (int)queue.remove());
		AssertJUnit.assertEquals(3, (int)queue.remove());
		AssertJUnit.assertEquals(4, (int)queue.remove());
		AssertJUnit.assertEquals(11, (int)queue.remove());
		AssertJUnit.assertEquals(12, (int)queue.remove());
		AssertJUnit.assertEquals(13, (int)queue.remove());
		AssertJUnit.assertEquals(14, (int)queue.remove());
		AssertJUnit.assertNull(queue.poll());
	}

	@Test(timeOut = 1000L)
	public void                     testListenersCancelled()
	{
		final BlockingQueue<Integer> queue = new LinkedBlockingQueue<Integer>();
		SettableFuture<Void> future = new SettableFuture<Void>();
		future.addListener(null, null, null);
		future.addListener(new Runnable() {
			@Override
			public void run() {
				queue.add(1);
			}
		});
		future.addListener(new FutureNotifier<Future<Void>>() {
			@Override
			public void accept(Future<Void> future) {
				queue.add(2);
			}
		});
		future.addListener(new DefaultFutureListener<Void>() {
			@Override
			public void onCancelled() {
				queue.add(3);
			}
		});
		future.addListener(null, null, new CancelListener() {
			@Override
			public void onCancelled() {
				queue.add(4);
			}
		});
		future.addAsyncListener(null, null, null, ListeningExecutors.directExecutor());
		future.addAsyncListener(new Runnable()
		{
			@Override
			public void run()
			{
				queue.add(11);
			}
		}, ListeningExecutors.directExecutor());
		future.addAsyncListener(new FutureNotifier<Future<Void>>()
		{
			@Override
			public void accept(Future<Void> future)
			{
				queue.add(12);
			}
		}, ListeningExecutors.directExecutor());
		future.addAsyncListener(new DefaultFutureListener<Void>()
		{
			@Override
			public void onCancelled()
			{
				queue.add(13);
			}
		}, ListeningExecutors.directExecutor());
		future.addAsyncListener(null, null, new CancelListener()
		{
			@Override
			public void onCancelled()
			{
				queue.add(14);
			}
		}, ListeningExecutors.directExecutor());
		queue.add(0);
		future.cancel(true);
		AssertJUnit.assertEquals(0, (int)queue.remove());
		AssertJUnit.assertEquals(1, (int)queue.remove());
		AssertJUnit.assertEquals(2, (int)queue.remove());
		AssertJUnit.assertEquals(3, (int)queue.remove());
		AssertJUnit.assertEquals(4, (int)queue.remove());
		AssertJUnit.assertEquals(11, (int)queue.remove());
		AssertJUnit.assertEquals(12, (int)queue.remove());
		AssertJUnit.assertEquals(13, (int)queue.remove());
		AssertJUnit.assertEquals(14, (int)queue.remove());
		AssertJUnit.assertNull(queue.poll());
	}

	@Test
	public void                     testExceptingListenerSet()
	{
		SettableFuture<Void> future = new SettableFuture<Void>();
		addBadListeners(future);
		future.set(null);
		addBadListeners(future);
	}

	@Test
	public void                     testExceptingListenerExcepted()
	{
		SettableFuture<Void> future = new SettableFuture<Void>();
		addBadListeners(future);
		future.setException(new NumberFormatException());
		addBadListeners(future);
	}

	@Test
	public void                     testExceptingListenerCancelled()
	{
		SettableFuture<Void> future = new SettableFuture<Void>();
		addBadListeners(future);
		future.cancel(true);
		addBadListeners(future);
	}

	private <V> ListenableFuture<V>	addBadListeners(ListenableFuture<V> future)
	{
		future.addListener(new Runnable() {
			@Override
			public void run() {
				throw new TestingRuntimeException();
			}
		});
		future.addListener(new FutureNotifier<ListenableFuture<V>>() {
			@Override
			public void accept(ListenableFuture<V> future) {
				throw new TestingRuntimeException();
			}
		});
		future.addListener(new FutureListener<V>() {
			@Override
			public void onSuccess(V result) {
				throw new TestingRuntimeException();
			}
			@Override
			public void onFailure(Throwable ex) {
				throw new TestingRuntimeException();
			}
			@Override
			public void onCancelled() {
				throw new TestingRuntimeException();
			}
		});
		future.addListener(
				new SuccessListener<V>() {
					@Override
					public void onSuccess(V result) {
						throw new TestingRuntimeException();
					}
				},
				new FailureListener() {
					@Override
					public void onFailure(Throwable ex) {
						throw new TestingRuntimeException();

					}
				},
				new CancelListener() {
					@Override
					public void onCancelled() {
						throw new TestingRuntimeException();
					}
				}
		);
		future.addAsyncListener(new Runnable()
		{
			@Override
			public void run()
			{
				throw new TestingRuntimeException();
			}
		}, ListeningExecutors.rejectingExecutor());
		future.addAsyncListener(new FutureNotifier<ListenableFuture<V>>()
		{
			@Override
			public void accept(ListenableFuture<V> future)
			{
				throw new TestingRuntimeException();
			}
		}, ListeningExecutors.rejectingExecutor());
		future.addAsyncListener(new FutureListener<V>()
		{
			@Override
			public void onSuccess(V result)
			{
				throw new TestingRuntimeException();
			}

			@Override
			public void onFailure(Throwable ex)
			{
				throw new TestingRuntimeException();
			}

			@Override
			public void onCancelled()
			{
				throw new TestingRuntimeException();
			}
		}, ListeningExecutors.rejectingExecutor());
		future.addAsyncListener(
				new SuccessListener<V>()
				{
					@Override
					public void onSuccess(V result)
					{
						throw new TestingRuntimeException();
					}
				},
				new FailureListener()
				{
					@Override
					public void onFailure(Throwable ex)
					{
						throw new TestingRuntimeException();

					}
				},
				new CancelListener()
				{
					@Override
					public void onCancelled()
					{
						throw new TestingRuntimeException();
					}
				},
				ListeningExecutors.rejectingExecutor()
		);
		return future;
	}
}
