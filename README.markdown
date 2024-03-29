# Dryuf Concurrent

## Deprecated

Moved to https://github.com/dryuf/dryuf-base/


## Lock free ListenableFuture / Future implementation

The project implements java concurrent (Listenable) Future in very cheap and flexible way.

The performance is for obvious reasons (additional support of listeners) slightly lower than original JDK Future but significantly higher than similar implementations in Guava and Spring.

### Release

```
<dependency>
	<groupId>net.dryuf</groupId>
	<artifactId>dryuf-concurrent</artifactId>
	<version>1.7.1</version>
</dependency>
```

### Flexibility

Additionally it solves several design issues from which suffer Guava and Spring implementations:
- allows several types of listeners, which can just implement Runnable or receive the original Future or receive directly the result to appropriate method
- distinguishes failure and cancellation when invoking listener
- distinguishes not started and running state
- allows delayed cancel notifications, i.e. the notification about cancel can be postponed until the task really exits, this is useful when task occupies some shared resource like network port

### Performance

The performance comparison looks like (measured on my low voltage i7 x86_64):

#### No listener
```
Benchmark                                    Mode  Cnt    Score   Error  Units
NoListenerAsyncBenchmark.benchmarkGuava     thrpt    2  157.510          ops/s
NoListenerAsyncBenchmark.benchmarkJdk       thrpt    2  241.154          ops/s
NoListenerAsyncBenchmark.benchmarkLwFuture  thrpt    2  279.774          ops/s
NoListenerAsyncBenchmark.benchmarkSpring    thrpt    2  117.028          ops/s
```

It's 78% faster than Guava, 139% faster than Spring and 16% faster than JDK.

#### Single listener set prior to run
```
Benchmark                                           Mode  Cnt    Score   Error  Units
SinglePreListenerAsyncBenchmark.benchmarkGuava     thrpt    2   98.639          ops/s
SinglePreListenerAsyncBenchmark.benchmarkJdk       thrpt    2  239.813          ops/s
SinglePreListenerAsyncBenchmark.benchmarkLwFuture  thrpt    2  204.984          ops/s
SinglePreListenerAsyncBenchmark.benchmarkSpring    thrpt    2   82.673          ops/s
```

It's 108% faster than Guava, 148% faster than Spring and 15% slower than JDK (but JDK test runs without  listener).

### Implementations

Currenty there are two implementations that differ in details how to handle atomic operations. The performance difference is about 10% but may change with JRE version:
- feature/atomic-field-updaters - uses AtomicFieldUpdaters to manage the atomic fields, should be faster but with current JIT it's slower
- feature/atomic-type-instances - uses AtomicInteger and AtomicReference instances, although the access is indirect, with current JIT this is faster
- feature/unsafe-volatiles - uses Unsafe instance to read and write volatile members, fastest solution, with NoListener test even outperforms JDK Future


## Lazily built LoadingCache

Class allowing caching results of Function, strongly enhancing performance of ConcurrentHashMap.

Common usage of this class is to build constant program metadata which never change and only grow over time but get fixed at some point (reflection based data commonly used by serializers for example). Implementation propagates the data built in ConcurrentHashMap to regular Map with some delay, increasing its performance. Its performance is similar to ConcurrentHashMap in cold start but gets to HashMap performance after data get fixed.

### Performance

```
Benchmark                                                               Mode  Cnt    Score   Error  Units
LazilyBuiltLoadingCacheBenchmark.coldLazilyBuiltLoadingCacheBenchmark  thrpt    2   31.449          ops/s
LazilyBuiltLoadingCacheBenchmark.directConcurrentBenchmark             thrpt    2   42.302          ops/s
LazilyBuiltLoadingCacheBenchmark.warmLazilyBuiltLoadingCacheBenchmark  thrpt    2  318.348          ops/s
```

Measurement was done in 1M batches on 4-core laptop x86_64 CPU, may be different on server CPU and even more significant on more relaxed CPU architecture (requiring LoadLoad barriers for example). Using this warming cache is about 8 times faster than using ConcurrentHashMap and is only 25% slower during initialization.


## Instance type based Function

Class wrapping instanceof checks into Function interface, delegating the calls according to type of passed argument.

Instead of writing many ```instanceof``` checks, it allows specifying the Map of types and the callback to call for the matching type. The implementation is smart enough to figure any subclasses, in the order items coming from the Map. There are two ways of specifying the Map of callback functions, one of them wraps only Function objects, the other wraps into BiFunction objects, allowing specifying the owner object first. The latter allows the callback delegator to be shared among multiple calling instances.


### Usage

```
public Callee
{
	// Note the second parameter to add method is BiFunction, so it is real instance method reference, not static reference
	private static BiFunction<Callee, Object, Object> ownerCaller = new TypeDelegatingOwnerBiFunction<>(TypeDelegatingOwnerBiFunction.<Callee, Object, Object>callbacksBuilder()
			.add(First.class, Callee::callFirst)
			.add(Second.class, Callee::callSecond)
			.add(Third.class, Callee::callThird)
			.add(Fourth.class, Callee::callFourth)
			.add(Fifth.class, Callee::callFifth)
			.build()
	);

	public Object call(Object input)
	{
		ownerCaller.call(this, input);
	}

	private Object callFirst(First input) { ... }
	private Object callSecond(Second input) { ... }

	...
}
```

### Performance

```
Benchmark                                                   Mode  Cnt   Score   Error  Units
TypeDelegatingFunctionBenchmark.directInstanceofBenchmark  thrpt    2  24.092          ops/s
TypeDelegatingFunctionBenchmark.instanceCallerBenchmark    thrpt    2  67.543          ops/s
TypeDelegatingFunctionBenchmark.ownerCallerBenchmark       thrpt    2  67.262          ops/s
```

Done in 1M batches. Apart from convenience and type safety, you can see the callback delegator is about 3 times faster
than instanceof based code (the benchmark is based on 5 different classes passed as argument).


## Custom Executor

### CloseableExecutor

Interface allowing automatically closing Executor in try-with-resources statements, mostly to simplify unit tests.

#### ClosingExecutor and NotClosingExecutor

Implementation of the above, wrapping the existing ExecutorService into another Executor and either shutting down in
`close()` method for ClosingExecutor or not shutting it down for NotClosingExecutor.
Both of them wait until all submitted tasks are processed.

#### UncontrolledCloseableExecutor

CloseableExecutor not closing delegated executor, neither executions of current tasks.  This is simplified version when
instance of CloseableExecutor is required but not any additional control because delegated executor is typically shared.

### ResourceClosingExecutor and ResourceNotClosingExecutor

CloseableExecutor implementations, closing also associated AutoCloseable resource, tying lifecycle of executor together
with another resource.  Mostly useful in tests or benchmarks where lifecycle of items is limited to scope.

### SequencingExecutor

Executor executing tasks in order of submission.  This is useful when tasks are tied to specific resource (such as
connection) but delegating executor is shared.

### ResultSequencingExecutor

Executor executing tasks in parallel but finishing the results sequentially in the order of submission.  This is useful
when the tasks can be parallelized but they write to shared resource at the end.

### CapacityResultSequencingExecutor

Executor running tasks in parallel but finishing the results sequentially in the order of submission.  Additionally, it
controls throughput by given capacity and number of parallel tasks.  Typically, the capacity is constrained by memory or
disk size or number of connections.

### FinishingSequencingExecutor

Executor executing tasks in order of submission.  Once there is no tasks pending, it will additionally call `finisher`
function to review the current state.

### SingleConsumerQueue

Queue for submitting tasks and consuming them from single consumer, guaranteed to be executed in unique instance.

### WorkExecutor, SingleWorkExecutor, BatchWorkExecutor

Executor processing work items instead of executing code.  Items are processed either in separate tasks
(SingleWorkExecutor) or batched into groups to optimize throughput (BatchWorkExecutor).


## Synchronization primitives

### RunSingle

Ensures there is only single activity running at the same time.  Typically used when there is repeated activity and new
one should not be started until previous one finished.

### CountDownRunner

Similar to CountDownLatch but instead of actively waiting, it allows registering callback which is executed once the
object reaches target.


## License

The code is released under version 2.0 of the [Apache License][].

## Stay in Touch

Feel free to contact me at kvr000@gmail.com and http://github.com/kvr000/ and http://github.com/dryuf/ and https://www.linkedin.com/in/zbynek-vyskovsky/

[Apache License]: http://www.apache.org/licenses/LICENSE-2.0

<!--- vim: set tw=120: --->
