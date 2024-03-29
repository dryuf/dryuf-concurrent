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

import java.util.concurrent.Callable;
import java.util.concurrent.RunnableFuture;


/**
 * ListenableFuture implementation which can be used as handling wrapper for actually running the
 * task.
 *
 * @param <V>
 * 	future return type
 *
 * @author
 * Copyright 2015-2022 Zbynek Vyskovsky mailto:kvr000@gmail.com http://github.com/kvr000/ https://github.com/dryuf/ https://www.linkedin.com/in/zbynek-vyskovsky/
 */
public class ListenableFutureTask<V> extends AbstractFuture<V> implements RunnableFuture<V>
{
	/**
	 * Constructs new instance with {@link Runnable} reference and provided {@code result}.
	 *
	 * @param runnable
	 * 	function to run
	 * @param result
	 * 	provided result
	 */
	public                          ListenableFutureTask(final Runnable runnable, final V result)
	{
		this(new Callable<V>()
		{
			@Override
			public V call() throws Exception
			{
				runnable.run();
				return result;
			}
		});
	}

	/**
	 * Constructs new instance with {@link Callable} reference.
	 *
	 * @param callable
	 * 	function to compute the result
	 */
	public                          ListenableFutureTask(final Callable<V> callable)
	{
		super(false);
		this.callable = callable;
	}

	protected void                  interruptTask()
	{
		myThread.interrupt();
	}

	protected boolean		enforcedCancel()
	{
		return false;
	}

	@Override
	public void                     run()
	{
		try {
			myThread = Thread.currentThread();
			if (setRunning()) {
				V result = callable.call();
				if (enforcedCancel())
					setCancelled();
				else
					set(result);
			}
		}
		catch (Throwable ex) {
			if (enforcedCancel())
				setCancelled();
			else
				setException(ex);
			if (ex instanceof Error)
				throw (Error)ex;
		}
	}

	/**
	 * The thread that executes the task.
	 *
	 * Volatile is not needed as this is surrounded with other memory barrier reads/writes.
	 */
	private Thread                  myThread;

	/**
	 * Callable performing the task.
	 */
	private final Callable<V>       callable;
}
