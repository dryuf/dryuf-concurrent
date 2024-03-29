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


import java.util.Collections;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;


/**
 * {@link java.util.concurrent.ExecutorService ExecutorService} executing {@link Runnable} directly in this thread.
 *
 * @author
 * Copyright 2015-2022 Zbynek Vyskovsky mailto:kvr000@gmail.com http://github.com/kvr000/ https://github.com/dryuf/ https://www.linkedin.com/in/zbynek-vyskovsky/
 */
public class RejectingExecutorService extends AbstractListeningExecutorService
{
	public void			execute(Runnable runnable)
	{
		throw new RejectedExecutionException(getClass()+" rejects everything.");
	}

	@Override
	public void                     shutdown()
	{
	}

	@Override
	public List<Runnable>           shutdownNow()
	{
		return Collections.<Runnable>emptyList();
	}

	@Override
	public boolean                  isShutdown()
	{
		return true;
	}

	@Override
	public boolean                  isTerminated()
	{
		return true;
	}

	@Override
	public boolean                  awaitTermination(long timeout, TimeUnit unit) throws InterruptedException
	{
		return true;
	}

	/**
	 * Gets instance of {@link RejectingExecutorService}.
	 *
	 * @return
	 * 	single instance of {@link RejectingExecutorService}
	 */
	public static RejectingExecutorService getInstance()
	{
		return instance;
	}

	private static RejectingExecutorService instance = new RejectingExecutorService();

	private static Logger		logger = Logger.getLogger(RejectingExecutorService.class.getName());
}
