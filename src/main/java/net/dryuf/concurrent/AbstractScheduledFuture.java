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


import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;


/**
 * Asynchronous {@link java.util.concurrent.Future} that can be finished externally.
 *
 * @param <V>
 *      future result type
 *
 * @author
 * Copyright 2015-2022 Zbynek Vyskovsky mailto:kvr000@gmail.com http://github.com/kvr000/ https://github.com/dryuf/ https://www.linkedin.com/in/zbynek-vyskovsky/
 */
public abstract class AbstractScheduledFuture<V> extends AbstractFuture<V> implements ListenableScheduledFuture<V>
{
	public                          AbstractScheduledFuture()
	{
		super(false);
	}

	@Override
	public abstract long            getDelay(TimeUnit unit);

	@Override
	public int                      compareTo(Delayed o)
	{
		long d0 = getDelay(TimeUnit.MILLISECONDS);
		long d1 = getDelay(TimeUnit.MILLISECONDS);
		return d0 == d1 ? 0 : d0 < d1 ? -1 : 1;
	}
}
