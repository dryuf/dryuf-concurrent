/*
 * Copyright 2015 Zbynek Vyskovsky mailto:kvr000@gmail.com http://kvr.znj.cz/ http://github.com/kvr000/
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


import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * {@link Executor} which rejects everything.
 *
 * @author
 * 	Zbynek Vyskovsky, mailto:kvr000@gmail.com http://kvr.znj.cz/software/java/ListenableFuture/ http://github.com/kvr000
 */
public class RejectingExecutor implements Executor
{
	public void			execute(Runnable runnable)
	{
		throw new RejectedExecutionException(getClass()+" rejects everything.");
	}

	/**
	 * Gets instance of {@link DirectExecutor}.
	 *
	 * @return
	 * 	single instane of {@link DirectExecutor}
	 */
	public static RejectingExecutor	getInstance()
	{
		return instance;
	}

	private static RejectingExecutor instance = new RejectingExecutor();
}
