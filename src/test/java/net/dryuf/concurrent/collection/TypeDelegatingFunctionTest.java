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

package net.dryuf.concurrent.collection;

import lombok.RequiredArgsConstructor;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

import java.util.concurrent.atomic.AtomicInteger;


/**
 * Tests for {@link TypeDelegatingFunction}.
 */
public class TypeDelegatingFunctionTest
{
	@Test(expectedExceptions = IllegalArgumentException.class)
	public void			testUndefined()
	{
		Fixture<Object, Object> fixture =
			new Fixture<>(TypeDelegatingFunction.<Object, Object, RuntimeException>callbacksBuilder().build());
		fixture.call(new Object());
	}

	@Test
	public void			testDefined()
	{
		AtomicInteger firstCount = new AtomicInteger();
		AtomicInteger secondCount = new AtomicInteger();

		Fixture<Object, Object> fixture = new Fixture<>(TypeDelegatingFunction.<Object, Object, RuntimeException>callbacksBuilder()
				.add(FirstImpl.class, (First o) -> firstCount.incrementAndGet())
				.add(SecondImpl.class, (Second o) -> secondCount.incrementAndGet())
				.build()
		);
		fixture.call(new FirstImpl());

		AssertJUnit.assertEquals(1, firstCount.get());
		AssertJUnit.assertEquals(0, secondCount.get());
	}

	@Test
	public void			testDefinedSecond()
	{
		AtomicInteger firstCount = new AtomicInteger();
		AtomicInteger secondCount = new AtomicInteger();

		Fixture<Object, Object> fixture = new Fixture<>(TypeDelegatingFunction.<Object, Object, RuntimeException>callbacksBuilder()
				.add(FirstImpl.class, (First o) -> firstCount.incrementAndGet())
				.add(SecondImpl.class, (Second o) -> secondCount.incrementAndGet())
				.build()
		);
		fixture.call(new SecondImpl());

		AssertJUnit.assertEquals(0, firstCount.get());
		AssertJUnit.assertEquals(1, secondCount.get());
	}

	@Test
	public void			testDerived()
	{
		AtomicInteger firstCount = new AtomicInteger();
		AtomicInteger secondCount = new AtomicInteger();

		Fixture<Object, Object> fixture = new Fixture<>(TypeDelegatingFunction.<Object, Object, RuntimeException>callbacksBuilder()
				.add(First.class, (First o) -> firstCount.incrementAndGet())
				.add(Second.class, (Second o) -> secondCount.incrementAndGet())
				.build()
		);
		fixture.call(new FirstImpl());

		AssertJUnit.assertEquals(1, firstCount.get());
		AssertJUnit.assertEquals(0, secondCount.get());
	}

	@Test
	public void			testDerivedConflicting()
	{
		AtomicInteger firstCount = new AtomicInteger();
		AtomicInteger secondCount = new AtomicInteger();

		Fixture<Object, Object> fixture =
			new Fixture<>(TypeDelegatingFunction.<Object, Object, RuntimeException>callbacksBuilder()
				.add(First.class, (First o) -> firstCount.incrementAndGet())
				.add(Second.class, (Second o) -> secondCount.incrementAndGet())
				.build()
		);
		fixture.call(new BothImpl());

		AssertJUnit.assertEquals(1, firstCount.get());
		AssertJUnit.assertEquals(0, secondCount.get());
	}

	private class Fixture<I, R>
	{
		public 				Fixture(TypeDelegatingFunction<? super I, ? extends R, RuntimeException> callbacks)
		{
			this.callbacks = callbacks;
		}

		public R call(I input)
		{
			return callbacks.apply(input);
		}

		private final TypeDelegatingFunction<? super I, ? extends R, RuntimeException> callbacks;
	}

	private static interface Input
	{
	}

	private static interface First extends Input
	{
		default int getFirstValue()
		{
			return 1;
		}
	}

	private static interface Second extends Input
	{
		default int getSecondValue()
		{
			return 2;
		}
	}

	private static class FirstImpl implements First
	{
	}

	private static class SecondImpl implements Second
	{
	}

	private static class BothImpl implements First, Second
	{
	}

	private static class Additional
	{
	}

	@RequiredArgsConstructor
	private static class Result
	{
		private final int value;
	}

	/**
	 * This serves as javadoc example only:
	 */
	public static class MyProcessor
	{
		private static final TypeDelegatingFunction<Input, Result, RuntimeException> processingFunctions =
			TypeDelegatingFunction.<Input, Result, RuntimeException>callbacksBuilder()
				.add(First.class, MyProcessor::processFirst)
				.add(Second.class, MyProcessor::processSecond)
				.build();

		public Result process(Input input)
		{
			return processingFunctions.apply(input);
		}

		// The First can be also FirstImpl implements First
		private static Result processFirst(First input)
		{
			return new Result(input.getFirstValue());
		}

		// The Second can be also SecondImpl implements Second
		private static Result processSecond(Second input)
		{
			return new Result(input.getSecondValue());
		}
	}
}
