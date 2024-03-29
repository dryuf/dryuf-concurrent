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

import net.dryuf.concurrent.function.ThrowingBiFunction;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;


/**
 * {@link ThrowingBiFunction} implementation, delegating the input to caller based on the input class. This is similar to
 * {@link TypeDelegatingFunction} but allows passing parameters, therefore allowing statically defined callback map and
 * using it from multiple instances.  Typically, the first parameter is the {@code this} class, therefore this
 * implementation decides based on the next parameter.
 *
 * The instantiator of this implementation must provide the mapping of input {@link Class} and processing
 * {@link ThrowingBiFunction}. It is enough to pass mapping of just base classes or interfaces, this implementation will
 * determine the best match according to order of mappings in {@code callbacks} parameter (the earlier in the map takes
 * the priority - use insertion order preserving Map).
 *
 * The callback is determined based on the type of the second parameter to function.
 *
 * <pre>
	public static class MyProcessor
	{
		private static final TypeDelegatingBiFunction2&lt;MyProcessor, Input, Result, RuntimeException&gt; processingFunctions =
			TypeDelegatingBiFunction2.&lt;MyProcessor, Input, Result, RuntimeException&gt;callbacksBuilder()
				.add(First.class, MyProcessor::processFirst)
				.add(Second.class, MyProcessor::processSecond)
				.build();

		public Result process(Input input)
		{
			return processingFunctions.apply(this, input);
		}

		// The First can be also FirstImpl implements First
		private Result processFirst(First input)
		{
			return new Result(input.getFirstValue());
		}

		// The Second can be also SecondImpl implements Second
		private Result processSecond(Second input)
		{
			return new Result(input.getSecondValue());
		}
	}
 * </pre>
 *
 * @param <T>
 *      first parameter type
 * @param <U>
 * 	second parameter type
 * @param <R>
 *      type of expected result
 * @param <X>
 *      type of thrown exception
 *
 * @apiNote thread safe
 *
 * @author
 * Copyright 2015-2022 Zbynek Vyskovsky mailto:kvr000@gmail.com http://github.com/kvr000/ https://github.com/dryuf/ https://www.linkedin.com/in/zbynek-vyskovsky/
 */
public class TypeDelegatingBiFunction2<T, U, R, X extends Exception> implements ThrowingBiFunction<T, U, R, X>
{
	private final Function<Class<? extends U>, ThrowingBiFunction<T, ? super U, ? extends R, X>> typedCallbacks;

	/**
	 * Creates new instance of {@link TypeDelegatingBiFunction2}, initialized by list of callbacks.
	 *
	 * @param callbacks
	 * 	Map of (potentially) interfaces or superclasses to callback functions.
	 */
	public TypeDelegatingBiFunction2(
			Map<Class<? extends U>, ThrowingBiFunction<T, ? super U, ? extends R, X>> callbacks
	)
	{
		this((Class<? extends U> clazz) -> {
			for (Map.Entry<Class<? extends U>, ThrowingBiFunction<T, ? super U, ? extends R, X>> callback :
					callbacks.entrySet()) {
				if (callback.getKey().isAssignableFrom(clazz)) {
					return callback.getValue();
				}
			}
			throw new IllegalArgumentException("Class unsupported by this caller: "+clazz);
		});
	}

	/**
	 * Creates new instance of {@link TypeDelegatingBiFunction2}, initialized by callbacks provider.
	 *
	 * @param callbacksProvider
	 * 	callback to provide processing callback based on the input class
	 */
	public TypeDelegatingBiFunction2(
			Function<Class<? extends U>, ThrowingBiFunction<T, ? super U, ? extends R, X>> callbacksProvider
	)
	{
		this.typedCallbacks = new LazilyBuiltLoadingCache<>(callbacksProvider);
	}

	@SuppressWarnings("unchecked")
	@Override
	public R apply(T owner, U input) throws X
	{
		return typedCallbacks.apply((Class<U>) input.getClass())
				.apply(owner, input);
	}

	/**
	 * Creates a new Callbacks builder.
	 *
	 * @param <T>
	 *      type of owner
	 * @param <U>
	 *      type of input
	 * @param <R>
	 *      return type
	 * @param <X>
	 *      exception thrown
	 *
	 * @return
	 * 	callback builder
	 */
	public static <T, U, R, X extends Exception> CallbacksBuilder<T, U, R, X> callbacksBuilder()
	{
		return new CallbacksBuilder<>();
	}

	/**
	 * Callbacks builder temporary holder.
	 *
	 * @param <T>
	 *      type of owner
	 * @param <U>
	 *      type of input
	 * @param <R>
	 *      return type
	 * @param <X>
	 *      exception thrown
	 */
	public static class CallbacksBuilder<T, U, R, X extends Exception>
	{
		/**
		 * Builds a TypeDelegatingOwnerBiFunction from provided callbacks.
		 *
		 * @return
		 * 	new TypeDelegatingOwnerBiFunction based on callbacks.
		 */
		public TypeDelegatingBiFunction2<T, U, R, X> build()
		{
			return new TypeDelegatingBiFunction2<>(callbacks);
		}

		/**
		 * Builds a Map of callbacks mapping.
		 *
		 * @return
		 * 	new Map of callbacks mapping.
		 */
		public Map<Class<? extends U>, ThrowingBiFunction<T, ? super U, ? extends R, X>> buildMap()
		{
			return callbacks;
		}

		/**
		 * Registers new callback mapping.
		 *
		 * @param clazz
		 * 	type of input
		 * @param callback
		 * 	callback to handle the type
		 * @param <I>
		 *      type of input
		 *
		 * @return
		 * 	this builder.
		 */
		@SuppressWarnings("unchecked")
		public <I extends U> CallbacksBuilder<T, U, R, X> add(
				Class<I> clazz,
				ThrowingBiFunction<T, ? super I, ? extends R, ? extends X> callback
		)
		{
			this.callbacks.merge(
					clazz,
					(ThrowingBiFunction<T, ? super U, ? extends R, X>) callback,
					(key, value) -> {
						throw new IllegalArgumentException("Callback already provided for: " + key);
					}
			);
			return this;
		}

		private Map<Class<? extends U>, ThrowingBiFunction<T, ? super U, ? extends R, X>> callbacks
				= new LinkedHashMap<>();
	}
}
