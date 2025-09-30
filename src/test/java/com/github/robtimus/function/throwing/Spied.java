/*
 * Spied.java
 * Copyright 2025 Rob Spoor
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.robtimus.function.throwing;

import static org.mockito.Mockito.spy;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleFunction;
import java.util.function.DoublePredicate;
import java.util.function.DoubleSupplier;
import java.util.function.DoubleToIntFunction;
import java.util.function.DoubleToLongFunction;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;
import java.util.function.IntBinaryOperator;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.function.IntSupplier;
import java.util.function.IntToDoubleFunction;
import java.util.function.IntToLongFunction;
import java.util.function.IntUnaryOperator;
import java.util.function.LongBinaryOperator;
import java.util.function.LongConsumer;
import java.util.function.LongFunction;
import java.util.function.LongPredicate;
import java.util.function.LongSupplier;
import java.util.function.LongToDoubleFunction;
import java.util.function.LongToIntFunction;
import java.util.function.LongUnaryOperator;
import java.util.function.ObjDoubleConsumer;
import java.util.function.ObjIntConsumer;
import java.util.function.ObjLongConsumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleBiFunction;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntBiFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongBiFunction;
import java.util.function.ToLongFunction;
import java.util.function.UnaryOperator;

final class Spied {

    private Spied() {
    }

    static <T, U, X extends Throwable> ThrowingBiConsumer<T, U, X> throwingBiConsumer(ThrowingBiConsumer<T, U, X> consumer) {
        return spy(new ThrowingBiConsumerWrapper<>(consumer));
    }

    static <T, U, R, X extends Throwable> ThrowingBiFunction<T, U, R, X> throwingBiFunction(ThrowingBiFunction<T, U, R, X> function) {
        return spy(new ThrowingBiFunctionWrapper<>(function));
    }

    static <T, X extends Throwable> ThrowingBinaryOperator<T, X> throwingBinaryOperator(ThrowingBinaryOperator<T, X> operator) {
        return spy(new ThrowingBinaryOperatorWrapper<>(operator));
    }

    static <T, U, X extends Throwable> ThrowingBiPredicate<T, U, X> throwingBiPredicate(ThrowingBiPredicate<T, U, X> predicate) {
        return spy(new ThrowingBiPredicateWrapper<>(predicate));
    }

    static <X extends Throwable> ThrowingBooleanSupplier<X> throwingBooleanSupplier(ThrowingBooleanSupplier<X> supplier) {
        return spy(new ThrowingBooleanSupplierWrapper<>(supplier));
    }

    static <T, X extends Throwable> ThrowingConsumer<T, X> throwingConsumer(ThrowingConsumer<T, X> consumer) {
        return spy(new ThrowingConsumerWrapper<>(consumer));
    }

    static <X extends Throwable> ThrowingDoubleBinaryOperator<X> throwingDoubleBinaryOperator(ThrowingDoubleBinaryOperator<X> operator) {
        return spy(new ThrowingDoubleBinaryOperatorWrapper<>(operator));
    }

    static <X extends Throwable> ThrowingDoubleConsumer<X> throwingDoubleConsumer(ThrowingDoubleConsumer<X> consumer) {
        return spy(new ThrowingDoubleConsumerWrapper<>(consumer));
    }

    static <R, X extends Throwable> ThrowingDoubleFunction<R, X> throwingDoubleFunction(ThrowingDoubleFunction<R, X> function) {
        return spy(new ThrowingDoubleFunctionWrapper<>(function));
    }

    static <X extends Throwable> ThrowingDoublePredicate<X> throwingDoublePredicate(ThrowingDoublePredicate<X> predicate) {
        return spy(new ThrowingDoublePredicateWrapper<>(predicate));
    }

    static <X extends Throwable> ThrowingDoubleSupplier<X> throwingDoubleSupplier(ThrowingDoubleSupplier<X> supplier) {
        return spy(new ThrowingDoubleSupplierWrapper<>(supplier));
    }

    static <X extends Throwable> ThrowingDoubleToIntFunction<X> throwingDoubleToIntFunction(ThrowingDoubleToIntFunction<X> function) {
        return spy(new ThrowingDoubleToIntFunctionWrapper<>(function));
    }

    static <X extends Throwable> ThrowingDoubleToLongFunction<X> throwingDoubleToLongFunction(ThrowingDoubleToLongFunction<X> function) {
        return spy(new ThrowingDoubleToLongFunctionWrapper<>(function));
    }

    static <X extends Throwable> ThrowingDoubleUnaryOperator<X> throwingDoubleUnaryOperator(ThrowingDoubleUnaryOperator<X> operator) {
        return spy(new ThrowingDoubleUnaryOperatorWrapper<>(operator));
    }

    static <T, R, X extends Throwable> ThrowingFunction<T, R, X> throwingFunction(ThrowingFunction<T, R, X> function) {
        return spy(new ThrowingFunctionWrapper<>(function));
    }

    static <X extends Throwable> ThrowingIntBinaryOperator<X> throwingIntBinaryOperator(ThrowingIntBinaryOperator<X> operator) {
        return spy(new ThrowingIntBinaryOperatorWrapper<>(operator));
    }

    static <X extends Throwable> ThrowingIntConsumer<X> throwingIntConsumer(ThrowingIntConsumer<X> consumer) {
        return spy(new ThrowingIntConsumerWrapper<>(consumer));
    }

    static <R, X extends Throwable> ThrowingIntFunction<R, X> throwingIntFunction(ThrowingIntFunction<R, X> function) {
        return spy(new ThrowingIntFunctionWrapper<>(function));
    }

    static <X extends Throwable> ThrowingIntPredicate<X> throwingIntPredicate(ThrowingIntPredicate<X> predicate) {
        return spy(new ThrowingIntPredicateWrapper<>(predicate));
    }

    static <X extends Throwable> ThrowingIntSupplier<X> throwingIntSupplier(ThrowingIntSupplier<X> supplier) {
        return spy(new ThrowingIntSupplierWrapper<>(supplier));
    }

    static <X extends Throwable> ThrowingIntToDoubleFunction<X> throwingIntToDoubleFunction(ThrowingIntToDoubleFunction<X> function) {
        return spy(new ThrowingIntToDoubleFunctionWrapper<>(function));
    }

    static <X extends Throwable> ThrowingIntToLongFunction<X> throwingIntToLongFunction(ThrowingIntToLongFunction<X> function) {
        return spy(new ThrowingIntToLongFunctionWrapper<>(function));
    }

    static <X extends Throwable> ThrowingIntUnaryOperator<X> throwingIntUnaryOperator(ThrowingIntUnaryOperator<X> operator) {
        return spy(new ThrowingIntUnaryOperatorWrapper<>(operator));
    }

    static <X extends Throwable> ThrowingLongBinaryOperator<X> throwingLongBinaryOperator(ThrowingLongBinaryOperator<X> operator) {
        return spy(new ThrowingLongBinaryOperatorWrapper<>(operator));
    }

    static <X extends Throwable> ThrowingLongConsumer<X> throwingLongConsumer(ThrowingLongConsumer<X> consumer) {
        return spy(new ThrowingLongConsumerWrapper<>(consumer));
    }

    static <R, X extends Throwable> ThrowingLongFunction<R, X> throwingLongFunction(ThrowingLongFunction<R, X> function) {
        return spy(new ThrowingLongFunctionWrapper<>(function));
    }

    static <X extends Throwable> ThrowingLongPredicate<X> throwingLongPredicate(ThrowingLongPredicate<X> predicate) {
        return spy(new ThrowingLongPredicateWrapper<>(predicate));
    }

    static <X extends Throwable> ThrowingLongSupplier<X> throwingLongSupplier(ThrowingLongSupplier<X> supplier) {
        return spy(new ThrowingLongSupplierWrapper<>(supplier));
    }

    static <X extends Throwable> ThrowingLongToDoubleFunction<X> throwingLongToDoubleFunction(ThrowingLongToDoubleFunction<X> function) {
        return spy(new ThrowingLongToDoubleFunctionWrapper<>(function));
    }

    static <X extends Throwable> ThrowingLongToIntFunction<X> throwingLongToIntFunction(ThrowingLongToIntFunction<X> function) {
        return spy(new ThrowingLongToIntFunctionWrapper<>(function));
    }

    static <X extends Throwable> ThrowingLongUnaryOperator<X> throwingLongUnaryOperator(ThrowingLongUnaryOperator<X> operator) {
        return spy(new ThrowingLongUnaryOperatorWrapper<>(operator));
    }

    static <T, X extends Throwable> ThrowingObjDoubleConsumer<T, X> throwingObjDoubleConsumer(ThrowingObjDoubleConsumer<T, X> consumer) {
        return spy(new ThrowingObjDoubleConsumerWrapper<>(consumer));
    }

    static <T, X extends Throwable> ThrowingObjIntConsumer<T, X> throwingObjIntConsumer(ThrowingObjIntConsumer<T, X> consumer) {
        return spy(new ThrowingObjIntConsumerWrapper<>(consumer));
    }

    static <T, X extends Throwable> ThrowingObjLongConsumer<T, X> throwingObjLongConsumer(ThrowingObjLongConsumer<T, X> consumer) {
        return spy(new ThrowingObjLongConsumerWrapper<>(consumer));
    }

    static <T, X extends Throwable> ThrowingPredicate<T, X> throwingPredicate(ThrowingPredicate<T, X> predicate) {
        return spy(new ThrowingPredicateWrapper<>(predicate));
    }

    static <X extends Throwable> ThrowingRunnable<X> throwingRunnable(ThrowingRunnable<X> runnable) {
        return spy(new ThrowingRunnableWrapper<>(runnable));
    }

    static <T, X extends Throwable> ThrowingSupplier<T, X> throwingSupplier(ThrowingSupplier<T, X> supplier) {
        return spy(new ThrowingSupplierWrapper<>(supplier));
    }

    static <T, U, X extends Throwable> ThrowingToDoubleBiFunction<T, U, X> throwingToDoubleBiFunction(ThrowingToDoubleBiFunction<T, U, X> function) {
        return spy(new ThrowingToDoubleBiFunctionWrapper<>(function));
    }

    static <T, X extends Throwable> ThrowingToDoubleFunction<T, X> throwingToDoubleFunction(ThrowingToDoubleFunction<T, X> function) {
        return spy(new ThrowingToDoubleFunctionWrapper<>(function));
    }

    static <T, U, X extends Throwable> ThrowingToIntBiFunction<T, U, X> throwingToIntBiFunction(ThrowingToIntBiFunction<T, U, X> function) {
        return spy(new ThrowingToIntBiFunctionWrapper<>(function));
    }

    static <T, X extends Throwable> ThrowingToIntFunction<T, X> throwingToIntFunction(ThrowingToIntFunction<T, X> function) {
        return spy(new ThrowingToIntFunctionWrapper<>(function));
    }

    static <T, U, X extends Throwable> ThrowingToLongBiFunction<T, U, X> throwingToLongBiFunction(ThrowingToLongBiFunction<T, U, X> function) {
        return spy(new ThrowingToLongBiFunctionWrapper<>(function));
    }

    static <T, X extends Throwable> ThrowingToLongFunction<T, X> throwingToLongFunction(ThrowingToLongFunction<T, X> function) {
        return spy(new ThrowingToLongFunctionWrapper<>(function));
    }

    static <T, X extends Throwable> ThrowingUnaryOperator<T, X> throwingUnaryOperator(ThrowingUnaryOperator<T, X> operator) {
        return spy(new ThrowingUnaryOperatorWrapper<>(operator));
    }

    static <T, U> BiConsumer<T, U> biConsumer(BiConsumer<T, U> consumer) {
        return spy(new BiConsumerWrapper<>(consumer));
    }

    static <T, U, R> BiFunction<T, U, R> biFunction(BiFunction<T, U, R> function) {
        return spy(new BiFunctionWrapper<>(function));
    }

    static <T> BinaryOperator<T> binaryOperator(BinaryOperator<T> operator) {
        return spy(new BinaryOperatorWrapper<>(operator));
    }

    static <T, U> BiPredicate<T, U> biPredicate(BiPredicate<T, U> predicate) {
        return spy(new BiPredicateWrapper<>(predicate));
    }

    static BooleanSupplier booleanSupplier(BooleanSupplier supplier) {
        return spy(new BooleanSupplierWrapper(supplier));
    }

    static <T> Consumer<T> consumer(Consumer<T> consumer) {
        return spy(new ConsumerWrapper<>(consumer));
    }

    static DoubleBinaryOperator doubleBinaryOperator(DoubleBinaryOperator operator) {
        return spy(new DoubleBinaryOperatorWrapper(operator));
    }

    static DoubleConsumer doubleConsumer(DoubleConsumer consumer) {
        return spy(new DoubleConsumerWrapper(consumer));
    }

    static <R> DoubleFunction<R> doubleFunction(DoubleFunction<R> function) {
        return spy(new DoubleFunctionWrapper<>(function));
    }

    static DoublePredicate doublePredicate(DoublePredicate predicate) {
        return spy(new DoublePredicateWrapper(predicate));
    }

    static DoubleSupplier doubleSupplier(DoubleSupplier supplier) {
        return spy(new DoubleSupplierWrapper(supplier));
    }

    static DoubleToIntFunction doubleToIntFunction(DoubleToIntFunction function) {
        return spy(new DoubleToIntFunctionWrapper(function));
    }

    static DoubleToLongFunction doubleToLongFunction(DoubleToLongFunction function) {
        return spy(new DoubleToLongFunctionWrapper(function));
    }

    static DoubleUnaryOperator doubleUnaryOperator(DoubleUnaryOperator operator) {
        return spy(new DoubleUnaryOperatorWrapper(operator));
    }

    static <T, R> Function<T, R> function(Function<T, R> function) {
        return spy(new FunctionWrapper<>(function));
    }

    static IntBinaryOperator intBinaryOperator(IntBinaryOperator operator) {
        return spy(new IntBinaryOperatorWrapper(operator));
    }

    static IntConsumer intConsumer(IntConsumer consumer) {
        return spy(new IntConsumerWrapper(consumer));
    }

    static <R> IntFunction<R> intFunction(IntFunction<R> function) {
        return spy(new IntFunctionWrapper<>(function));
    }

    static IntPredicate intPredicate(IntPredicate predicate) {
        return spy(new IntPredicateWrapper(predicate));
    }

    static IntSupplier intSupplier(IntSupplier supplier) {
        return spy(new IntSupplierWrapper(supplier));
    }

    static IntToDoubleFunction intToDoubleFunction(IntToDoubleFunction function) {
        return spy(new IntToDoubleFunctionWrapper(function));
    }

    static IntToLongFunction intToLongFunction(IntToLongFunction function) {
        return spy(new IntToLongFunctionWrapper(function));
    }

    static IntUnaryOperator intUnaryOperator(IntUnaryOperator operator) {
        return spy(new IntUnaryOperatorWrapper(operator));
    }

    static LongBinaryOperator longBinaryOperator(LongBinaryOperator operator) {
        return spy(new LongBinaryOperatorWrapper(operator));
    }

    static LongConsumer longConsumer(LongConsumer consumer) {
        return spy(new LongConsumerWrapper(consumer));
    }

    static <R> LongFunction<R> longFunction(LongFunction<R> function) {
        return spy(new LongFunctionWrapper<>(function));
    }

    static LongPredicate longPredicate(LongPredicate predicate) {
        return spy(new LongPredicateWrapper(predicate));
    }

    static LongSupplier longSupplier(LongSupplier supplier) {
        return spy(new LongSupplierWrapper(supplier));
    }

    static LongToDoubleFunction longToDoubleFunction(LongToDoubleFunction function) {
        return spy(new LongToDoubleFunctionWrapper(function));
    }

    static LongToIntFunction longToIntFunction(LongToIntFunction function) {
        return spy(new LongToIntFunctionWrapper(function));
    }

    static LongUnaryOperator longUnaryOperator(LongUnaryOperator operator) {
        return spy(new LongUnaryOperatorWrapper(operator));
    }

    static <T> ObjDoubleConsumer<T> objDoubleConsumer(ObjDoubleConsumer<T> consumer) {
        return spy(new ObjDoubleConsumerWrapper<>(consumer));
    }

    static <T> ObjIntConsumer<T> objIntConsumer(ObjIntConsumer<T> consumer) {
        return spy(new ObjIntConsumerWrapper<>(consumer));
    }

    static <T> ObjLongConsumer<T> objLongConsumer(ObjLongConsumer<T> consumer) {
        return spy(new ObjLongConsumerWrapper<>(consumer));
    }

    static <T> Predicate<T> predicate(Predicate<T> predicate) {
        return spy(new PredicateWrapper<>(predicate));
    }

    static Runnable runnable(Runnable runnable) {
        return spy(new RunnableWrapper(runnable));
    }

    static <T> Supplier<T> supplier(Supplier<T> supplier) {
        return spy(new SupplierWrapper<>(supplier));
    }

    static <T, U> ToDoubleBiFunction<T, U> toDoubleBiFunction(ToDoubleBiFunction<T, U> function) {
        return spy(new ToDoubleBiFunctionWrapper<>(function));
    }

    static <T> ToDoubleFunction<T> toDoubleFunction(ToDoubleFunction<T> function) {
        return spy(new ToDoubleFunctionWrapper<>(function));
    }

    static <T, U> ToIntBiFunction<T, U> toIntBiFunction(ToIntBiFunction<T, U> function) {
        return spy(new ToIntBiFunctionWrapper<>(function));
    }

    static <T> ToIntFunction<T> toIntFunction(ToIntFunction<T> function) {
        return spy(new ToIntFunctionWrapper<>(function));
    }

    static <T, U> ToLongBiFunction<T, U> toLongBiFunction(ToLongBiFunction<T, U> function) {
        return spy(new ToLongBiFunctionWrapper<>(function));
    }

    static <T> ToLongFunction<T> toLongFunction(ToLongFunction<T> function) {
        return spy(new ToLongFunctionWrapper<>(function));
    }

    static <T> UnaryOperator<T> unaryOperator(UnaryOperator<T> operator) {
        return spy(new UnaryOperatorWrapper<>(operator));
    }

    private static final class ThrowingBiConsumerWrapper<T, U, X extends Throwable> implements ThrowingBiConsumer<T, U, X> {

        private final ThrowingBiConsumer<T, U, X> consumer;

        private ThrowingBiConsumerWrapper(ThrowingBiConsumer<T, U, X> consumer) {
            this.consumer = consumer;
        }

        @Override
        public void accept(T t, U u) throws X {
            consumer.accept(t, u);
        }
    }

    private static final class ThrowingBiFunctionWrapper<T, U, R, X extends Throwable> implements ThrowingBiFunction<T, U, R, X> {

        private final ThrowingBiFunction<T, U, R, X> function;

        private ThrowingBiFunctionWrapper(ThrowingBiFunction<T, U, R, X> function) {
            this.function = function;
        }

        @Override
        public R apply(T t, U u) throws X {
            return function.apply(t, u);
        }
    }

    private static final class ThrowingBinaryOperatorWrapper<T, X extends Throwable> implements ThrowingBinaryOperator<T, X> {

        private final ThrowingBinaryOperator<T, X> operator;

        private ThrowingBinaryOperatorWrapper(ThrowingBinaryOperator<T, X> operator) {
            this.operator = operator;
        }

        @Override
        public T apply(T t, T u) throws X {
            return operator.apply(t, u);
        }
    }

    private static final class ThrowingBiPredicateWrapper<T, U, X extends Throwable> implements ThrowingBiPredicate<T, U, X> {

        private final ThrowingBiPredicate<T, U, X> predicate;

        private ThrowingBiPredicateWrapper(ThrowingBiPredicate<T, U, X> predicate) {
            this.predicate = predicate;
        }

        @Override
        public boolean test(T t, U u) throws X {
            return predicate.test(t, u);
        }
    }

    private static final class ThrowingBooleanSupplierWrapper<X extends Throwable> implements ThrowingBooleanSupplier<X> {

        private final ThrowingBooleanSupplier<X> supplier;

        private ThrowingBooleanSupplierWrapper(ThrowingBooleanSupplier<X> supplier) {
            this.supplier = supplier;
        }

        @Override
        public boolean getAsBoolean() throws X {
            return supplier.getAsBoolean();
        }
    }

    private static final class ThrowingConsumerWrapper<T, X extends Throwable> implements ThrowingConsumer<T, X> {

        private final ThrowingConsumer<T, X> consumer;

        private ThrowingConsumerWrapper(ThrowingConsumer<T, X> consumer) {
            this.consumer = consumer;
        }

        @Override
        public void accept(T t) throws X {
            consumer.accept(t);
        }
    }

    private static final class ThrowingDoubleBinaryOperatorWrapper<X extends Throwable> implements ThrowingDoubleBinaryOperator<X> {

        private final ThrowingDoubleBinaryOperator<X> operator;

        private ThrowingDoubleBinaryOperatorWrapper(ThrowingDoubleBinaryOperator<X> operator) {
            this.operator = operator;
        }

        @Override
        public double applyAsDouble(double t, double u) throws X {
            return operator.applyAsDouble(t, u);
        }
    }

    private static final class ThrowingDoubleConsumerWrapper<X extends Throwable> implements ThrowingDoubleConsumer<X> {

        private final ThrowingDoubleConsumer<X> consumer;

        private ThrowingDoubleConsumerWrapper(ThrowingDoubleConsumer<X> consumer) {
            this.consumer = consumer;
        }

        @Override
        public void accept(double t) throws X {
            consumer.accept(t);
        }
    }

    private static final class ThrowingDoubleFunctionWrapper<R, X extends Throwable> implements ThrowingDoubleFunction<R, X> {

        private final ThrowingDoubleFunction<R, X> function;

        private ThrowingDoubleFunctionWrapper(ThrowingDoubleFunction<R, X> function) {
            this.function = function;
        }

        @Override
        public R apply(double t) throws X {
            return function.apply(t);
        }
    }

    private static final class ThrowingDoublePredicateWrapper<X extends Throwable> implements ThrowingDoublePredicate<X> {

        private final ThrowingDoublePredicate<X> predicate;

        private ThrowingDoublePredicateWrapper(ThrowingDoublePredicate<X> predicate) {
            this.predicate = predicate;
        }

        @Override
        public boolean test(double t) throws X {
            return predicate.test(t);
        }
    }

    private static final class ThrowingDoubleSupplierWrapper<X extends Throwable> implements ThrowingDoubleSupplier<X> {

        private final ThrowingDoubleSupplier<X> supplier;

        private ThrowingDoubleSupplierWrapper(ThrowingDoubleSupplier<X> supplier) {
            this.supplier = supplier;
        }

        @Override
        public double getAsDouble() throws X {
            return supplier.getAsDouble();
        }
    }

    private static final class ThrowingDoubleToIntFunctionWrapper<X extends Throwable> implements ThrowingDoubleToIntFunction<X> {

        private final ThrowingDoubleToIntFunction<X> function;

        private ThrowingDoubleToIntFunctionWrapper(ThrowingDoubleToIntFunction<X> function) {
            this.function = function;
        }

        @Override
        public int applyAsInt(double value) throws X {
            return function.applyAsInt(value);
        }
    }

    private static final class ThrowingDoubleToLongFunctionWrapper<X extends Throwable> implements ThrowingDoubleToLongFunction<X> {

        private final ThrowingDoubleToLongFunction<X> function;

        private ThrowingDoubleToLongFunctionWrapper(ThrowingDoubleToLongFunction<X> function) {
            this.function = function;
        }

        @Override
        public long applyAsLong(double value) throws X {
            return function.applyAsLong(value);
        }
    }

    private static final class ThrowingDoubleUnaryOperatorWrapper<X extends Throwable> implements ThrowingDoubleUnaryOperator<X> {

        private final ThrowingDoubleUnaryOperator<X> operator;

        private ThrowingDoubleUnaryOperatorWrapper(ThrowingDoubleUnaryOperator<X> operator) {
            this.operator = operator;
        }

        @Override
        public double applyAsDouble(double t) throws X {
            return operator.applyAsDouble(t);
        }
    }

    private static final class ThrowingFunctionWrapper<T, R, X extends Throwable> implements ThrowingFunction<T, R, X> {

        private final ThrowingFunction<T, R, X> function;

        private ThrowingFunctionWrapper(ThrowingFunction<T, R, X> function) {
            this.function = function;
        }

        @Override
        public R apply(T t) throws X {
            return function.apply(t);
        }
    }

    private static final class ThrowingIntBinaryOperatorWrapper<X extends Throwable> implements ThrowingIntBinaryOperator<X> {

        private final ThrowingIntBinaryOperator<X> operator;

        private ThrowingIntBinaryOperatorWrapper(ThrowingIntBinaryOperator<X> operator) {
            this.operator = operator;
        }

        @Override
        public int applyAsInt(int t, int u) throws X {
            return operator.applyAsInt(t, u);
        }
    }

    private static final class ThrowingIntConsumerWrapper<X extends Throwable> implements ThrowingIntConsumer<X> {

        private final ThrowingIntConsumer<X> consumer;

        private ThrowingIntConsumerWrapper(ThrowingIntConsumer<X> consumer) {
            this.consumer = consumer;
        }

        @Override
        public void accept(int t) throws X {
            consumer.accept(t);
        }
    }

    private static final class ThrowingIntFunctionWrapper<R, X extends Throwable> implements ThrowingIntFunction<R, X> {

        private final ThrowingIntFunction<R, X> function;

        private ThrowingIntFunctionWrapper(ThrowingIntFunction<R, X> function) {
            this.function = function;
        }

        @Override
        public R apply(int t) throws X {
            return function.apply(t);
        }
    }

    private static final class ThrowingIntPredicateWrapper<X extends Throwable> implements ThrowingIntPredicate<X> {

        private final ThrowingIntPredicate<X> predicate;

        private ThrowingIntPredicateWrapper(ThrowingIntPredicate<X> predicate) {
            this.predicate = predicate;
        }

        @Override
        public boolean test(int t) throws X {
            return predicate.test(t);
        }
    }

    private static final class ThrowingIntSupplierWrapper<X extends Throwable> implements ThrowingIntSupplier<X> {

        private final ThrowingIntSupplier<X> supplier;

        private ThrowingIntSupplierWrapper(ThrowingIntSupplier<X> supplier) {
            this.supplier = supplier;
        }

        @Override
        public int getAsInt() throws X {
            return supplier.getAsInt();
        }
    }

    private static final class ThrowingIntToDoubleFunctionWrapper<X extends Throwable> implements ThrowingIntToDoubleFunction<X> {

        private final ThrowingIntToDoubleFunction<X> function;

        private ThrowingIntToDoubleFunctionWrapper(ThrowingIntToDoubleFunction<X> function) {
            this.function = function;
        }

        @Override
        public double applyAsDouble(int value) throws X {
            return function.applyAsDouble(value);
        }
    }

    private static final class ThrowingIntToLongFunctionWrapper<X extends Throwable> implements ThrowingIntToLongFunction<X> {

        private final ThrowingIntToLongFunction<X> function;

        private ThrowingIntToLongFunctionWrapper(ThrowingIntToLongFunction<X> function) {
            this.function = function;
        }

        @Override
        public long applyAsLong(int value) throws X {
            return function.applyAsLong(value);
        }
    }

    private static final class ThrowingIntUnaryOperatorWrapper<X extends Throwable> implements ThrowingIntUnaryOperator<X> {

        private final ThrowingIntUnaryOperator<X> operator;

        private ThrowingIntUnaryOperatorWrapper(ThrowingIntUnaryOperator<X> operator) {
            this.operator = operator;
        }

        @Override
        public int applyAsInt(int t) throws X {
            return operator.applyAsInt(t);
        }
    }

    private static final class ThrowingLongBinaryOperatorWrapper<X extends Throwable> implements ThrowingLongBinaryOperator<X> {

        private final ThrowingLongBinaryOperator<X> operator;

        private ThrowingLongBinaryOperatorWrapper(ThrowingLongBinaryOperator<X> operator) {
            this.operator = operator;
        }

        @Override
        public long applyAsLong(long t, long u) throws X {
            return operator.applyAsLong(t, u);
        }
    }

    private static final class ThrowingLongConsumerWrapper<X extends Throwable> implements ThrowingLongConsumer<X> {

        private final ThrowingLongConsumer<X> consumer;

        private ThrowingLongConsumerWrapper(ThrowingLongConsumer<X> consumer) {
            this.consumer = consumer;
        }

        @Override
        public void accept(long t) throws X {
            consumer.accept(t);
        }
    }

    private static final class ThrowingLongFunctionWrapper<R, X extends Throwable> implements ThrowingLongFunction<R, X> {

        private final ThrowingLongFunction<R, X> function;

        private ThrowingLongFunctionWrapper(ThrowingLongFunction<R, X> function) {
            this.function = function;
        }

        @Override
        public R apply(long t) throws X {
            return function.apply(t);
        }
    }

    private static final class ThrowingLongPredicateWrapper<X extends Throwable> implements ThrowingLongPredicate<X> {

        private final ThrowingLongPredicate<X> predicate;

        private ThrowingLongPredicateWrapper(ThrowingLongPredicate<X> predicate) {
            this.predicate = predicate;
        }

        @Override
        public boolean test(long t) throws X {
            return predicate.test(t);
        }
    }

    private static final class ThrowingLongSupplierWrapper<X extends Throwable> implements ThrowingLongSupplier<X> {

        private final ThrowingLongSupplier<X> supplier;

        private ThrowingLongSupplierWrapper(ThrowingLongSupplier<X> supplier) {
            this.supplier = supplier;
        }

        @Override
        public long getAsLong() throws X {
            return supplier.getAsLong();
        }
    }

    private static final class ThrowingLongToDoubleFunctionWrapper<X extends Throwable> implements ThrowingLongToDoubleFunction<X> {

        private final ThrowingLongToDoubleFunction<X> function;

        private ThrowingLongToDoubleFunctionWrapper(ThrowingLongToDoubleFunction<X> function) {
            this.function = function;
        }

        @Override
        public double applyAsDouble(long value) throws X {
            return function.applyAsDouble(value);
        }
    }

    private static final class ThrowingLongToIntFunctionWrapper<X extends Throwable> implements ThrowingLongToIntFunction<X> {

        private final ThrowingLongToIntFunction<X> function;

        private ThrowingLongToIntFunctionWrapper(ThrowingLongToIntFunction<X> function) {
            this.function = function;
        }

        @Override
        public int applyAsInt(long value) throws X {
            return function.applyAsInt(value);
        }
    }

    private static final class ThrowingLongUnaryOperatorWrapper<X extends Throwable> implements ThrowingLongUnaryOperator<X> {

        private final ThrowingLongUnaryOperator<X> operator;

        private ThrowingLongUnaryOperatorWrapper(ThrowingLongUnaryOperator<X> operator) {
            this.operator = operator;
        }

        @Override
        public long applyAsLong(long t) throws X {
            return operator.applyAsLong(t);
        }
    }

    private static final class ThrowingObjDoubleConsumerWrapper<T, X extends Throwable> implements ThrowingObjDoubleConsumer<T, X> {

        private final ThrowingObjDoubleConsumer<T, X> consumer;

        private ThrowingObjDoubleConsumerWrapper(ThrowingObjDoubleConsumer<T, X> consumer) {
            this.consumer = consumer;
        }

        @Override
        public void accept(T t, double u) throws X {
            consumer.accept(t, u);
        }
    }

    private static final class ThrowingObjIntConsumerWrapper<T, X extends Throwable> implements ThrowingObjIntConsumer<T, X> {

        private final ThrowingObjIntConsumer<T, X> consumer;

        private ThrowingObjIntConsumerWrapper(ThrowingObjIntConsumer<T, X> consumer) {
            this.consumer = consumer;
        }

        @Override
        public void accept(T t, int u) throws X {
            consumer.accept(t, u);
        }
    }

    private static final class ThrowingObjLongConsumerWrapper<T, X extends Throwable> implements ThrowingObjLongConsumer<T, X> {

        private final ThrowingObjLongConsumer<T, X> consumer;

        private ThrowingObjLongConsumerWrapper(ThrowingObjLongConsumer<T, X> consumer) {
            this.consumer = consumer;
        }

        @Override
        public void accept(T t, long u) throws X {
            consumer.accept(t, u);
        }
    }

    private static final class ThrowingPredicateWrapper<T, X extends Throwable> implements ThrowingPredicate<T, X> {

        private final ThrowingPredicate<T, X> predicate;

        private ThrowingPredicateWrapper(ThrowingPredicate<T, X> predicate) {
            this.predicate = predicate;
        }

        @Override
        public boolean test(T t) throws X {
            return predicate.test(t);
        }
    }

    private static final class ThrowingRunnableWrapper<X extends Throwable> implements ThrowingRunnable<X> {

        private final ThrowingRunnable<X> runnable;

        private ThrowingRunnableWrapper(ThrowingRunnable<X> runnable) {
            this.runnable = runnable;
        }

        @Override
        public void run() throws X {
            runnable.run();
        }
    }

    private static final class ThrowingSupplierWrapper<T, X extends Throwable> implements ThrowingSupplier<T, X> {

        private final ThrowingSupplier<T, X> supplier;

        private ThrowingSupplierWrapper(ThrowingSupplier<T, X> supplier) {
            this.supplier = supplier;
        }

        @Override
        public T get() throws X {
            return supplier.get();
        }
    }

    private static final class ThrowingToDoubleBiFunctionWrapper<T, U, X extends Throwable> implements ThrowingToDoubleBiFunction<T, U, X> {

        private final ThrowingToDoubleBiFunction<T, U, X> function;

        private ThrowingToDoubleBiFunctionWrapper(ThrowingToDoubleBiFunction<T, U, X> function) {
            this.function = function;
        }

        @Override
        public double applyAsDouble(T t, U u) throws X {
            return function.applyAsDouble(t, u);
        }
    }

    private static final class ThrowingToDoubleFunctionWrapper<T, X extends Throwable> implements ThrowingToDoubleFunction<T, X> {

        private final ThrowingToDoubleFunction<T, X> function;

        private ThrowingToDoubleFunctionWrapper(ThrowingToDoubleFunction<T, X> function) {
            this.function = function;
        }

        @Override
        public double applyAsDouble(T value) throws X {
            return function.applyAsDouble(value);
        }
    }

    private static final class ThrowingToIntBiFunctionWrapper<T, U, X extends Throwable> implements ThrowingToIntBiFunction<T, U, X> {

        private final ThrowingToIntBiFunction<T, U, X> function;

        private ThrowingToIntBiFunctionWrapper(ThrowingToIntBiFunction<T, U, X> function) {
            this.function = function;
        }

        @Override
        public int applyAsInt(T t, U u) throws X {
            return function.applyAsInt(t, u);
        }
    }

    private static final class ThrowingToIntFunctionWrapper<T, X extends Throwable> implements ThrowingToIntFunction<T, X> {

        private final ThrowingToIntFunction<T, X> function;

        private ThrowingToIntFunctionWrapper(ThrowingToIntFunction<T, X> function) {
            this.function = function;
        }

        @Override
        public int applyAsInt(T value) throws X {
            return function.applyAsInt(value);
        }
    }

    private static final class ThrowingToLongBiFunctionWrapper<T, U, X extends Throwable> implements ThrowingToLongBiFunction<T, U, X> {

        private final ThrowingToLongBiFunction<T, U, X> function;

        private ThrowingToLongBiFunctionWrapper(ThrowingToLongBiFunction<T, U, X> function) {
            this.function = function;
        }

        @Override
        public long applyAsLong(T t, U u) throws X {
            return function.applyAsLong(t, u);
        }
    }

    private static final class ThrowingToLongFunctionWrapper<T, X extends Throwable> implements ThrowingToLongFunction<T, X> {

        private final ThrowingToLongFunction<T, X> function;

        private ThrowingToLongFunctionWrapper(ThrowingToLongFunction<T, X> function) {
            this.function = function;
        }

        @Override
        public long applyAsLong(T value) throws X {
            return function.applyAsLong(value);
        }
    }

    private static final class ThrowingUnaryOperatorWrapper<T, X extends Throwable> implements ThrowingUnaryOperator<T, X> {

        private final ThrowingUnaryOperator<T, X> operator;

        private ThrowingUnaryOperatorWrapper(ThrowingUnaryOperator<T, X> operator) {
            this.operator = operator;
        }

        @Override
        public T apply(T t) throws X {
            return operator.apply(t);
        }
    }

    private static final class BiConsumerWrapper<T, U> implements BiConsumer<T, U> {

        private final BiConsumer<T, U> consumer;

        private BiConsumerWrapper(BiConsumer<T, U> consumer) {
            this.consumer = consumer;
        }

        @Override
        public void accept(T t, U u) {
            consumer.accept(t, u);
        }
    }

    private static final class BiFunctionWrapper<T, U, R> implements BiFunction<T, U, R> {

        private final BiFunction<T, U, R> function;

        private BiFunctionWrapper(BiFunction<T, U, R> function) {
            this.function = function;
        }

        @Override
        public R apply(T t, U u) {
            return function.apply(t, u);
        }
    }

    private static final class BinaryOperatorWrapper<T> implements BinaryOperator<T> {

        private final BinaryOperator<T> operator;

        private BinaryOperatorWrapper(BinaryOperator<T> operator) {
            this.operator = operator;
        }

        @Override
        public T apply(T t, T u) {
            return operator.apply(t, u);
        }
    }

    private static final class BiPredicateWrapper<T, U> implements BiPredicate<T, U> {

        private final BiPredicate<T, U> predicate;

        private BiPredicateWrapper(BiPredicate<T, U> predicate) {
            this.predicate = predicate;
        }

        @Override
        public boolean test(T t, U u) {
            return predicate.test(t, u);
        }
    }

    private static final class BooleanSupplierWrapper implements BooleanSupplier {

        private final BooleanSupplier supplier;

        private BooleanSupplierWrapper(BooleanSupplier supplier) {
            this.supplier = supplier;
        }

        @Override
        public boolean getAsBoolean() {
            return supplier.getAsBoolean();
        }
    }

    private static final class ConsumerWrapper<T> implements Consumer<T> {

        private final Consumer<T> consumer;

        private ConsumerWrapper(Consumer<T> consumer) {
            this.consumer = consumer;
        }

        @Override
        public void accept(T t) {
            consumer.accept(t);
        }
    }

    private static final class DoubleBinaryOperatorWrapper implements DoubleBinaryOperator {

        private final DoubleBinaryOperator operator;

        private DoubleBinaryOperatorWrapper(DoubleBinaryOperator operator) {
            this.operator = operator;
        }

        @Override
        public double applyAsDouble(double t, double u) {
            return operator.applyAsDouble(t, u);
        }
    }

    private static final class DoubleConsumerWrapper implements DoubleConsumer {

        private final DoubleConsumer consumer;

        private DoubleConsumerWrapper(DoubleConsumer consumer) {
            this.consumer = consumer;
        }

        @Override
        public void accept(double t) {
            consumer.accept(t);
        }
    }

    private static final class DoubleFunctionWrapper<R> implements DoubleFunction<R> {

        private final DoubleFunction<R> function;

        private DoubleFunctionWrapper(DoubleFunction<R> function) {
            this.function = function;
        }

        @Override
        public R apply(double t) {
            return function.apply(t);
        }
    }

    private static final class DoublePredicateWrapper implements DoublePredicate {

        private final DoublePredicate predicate;

        private DoublePredicateWrapper(DoublePredicate predicate) {
            this.predicate = predicate;
        }

        @Override
        public boolean test(double t) {
            return predicate.test(t);
        }
    }

    private static final class DoubleSupplierWrapper implements DoubleSupplier {

        private final DoubleSupplier supplier;

        private DoubleSupplierWrapper(DoubleSupplier supplier) {
            this.supplier = supplier;
        }

        @Override
        public double getAsDouble() {
            return supplier.getAsDouble();
        }
    }

    private static final class DoubleToIntFunctionWrapper implements DoubleToIntFunction {

        private final DoubleToIntFunction function;

        private DoubleToIntFunctionWrapper(DoubleToIntFunction function) {
            this.function = function;
        }

        @Override
        public int applyAsInt(double value) {
            return function.applyAsInt(value);
        }
    }

    private static final class DoubleToLongFunctionWrapper implements DoubleToLongFunction {

        private final DoubleToLongFunction function;

        private DoubleToLongFunctionWrapper(DoubleToLongFunction function) {
            this.function = function;
        }

        @Override
        public long applyAsLong(double value) {
            return function.applyAsLong(value);
        }
    }

    private static final class DoubleUnaryOperatorWrapper implements DoubleUnaryOperator {

        private final DoubleUnaryOperator operator;

        private DoubleUnaryOperatorWrapper(DoubleUnaryOperator operator) {
            this.operator = operator;
        }

        @Override
        public double applyAsDouble(double t) {
            return operator.applyAsDouble(t);
        }
    }

    private static final class FunctionWrapper<T, R> implements Function<T, R> {

        private final Function<T, R> function;

        private FunctionWrapper(Function<T, R> function) {
            this.function = function;
        }

        @Override
        public R apply(T t) {
            return function.apply(t);
        }
    }

    private static final class IntBinaryOperatorWrapper implements IntBinaryOperator {

        private final IntBinaryOperator operator;

        private IntBinaryOperatorWrapper(IntBinaryOperator operator) {
            this.operator = operator;
        }

        @Override
        public int applyAsInt(int t, int u) {
            return operator.applyAsInt(t, u);
        }
    }

    private static final class IntConsumerWrapper implements IntConsumer {

        private final IntConsumer consumer;

        private IntConsumerWrapper(IntConsumer consumer) {
            this.consumer = consumer;
        }

        @Override
        public void accept(int t) {
            consumer.accept(t);
        }
    }

    private static final class IntFunctionWrapper<R> implements IntFunction<R> {

        private final IntFunction<R> function;

        private IntFunctionWrapper(IntFunction<R> function) {
            this.function = function;
        }

        @Override
        public R apply(int t) {
            return function.apply(t);
        }
    }

    private static final class IntPredicateWrapper implements IntPredicate {

        private final IntPredicate predicate;

        private IntPredicateWrapper(IntPredicate predicate) {
            this.predicate = predicate;
        }

        @Override
        public boolean test(int t) {
            return predicate.test(t);
        }
    }

    private static final class IntSupplierWrapper implements IntSupplier {

        private final IntSupplier supplier;

        private IntSupplierWrapper(IntSupplier supplier) {
            this.supplier = supplier;
        }

        @Override
        public int getAsInt() {
            return supplier.getAsInt();
        }
    }

    private static final class IntToDoubleFunctionWrapper implements IntToDoubleFunction {

        private final IntToDoubleFunction function;

        private IntToDoubleFunctionWrapper(IntToDoubleFunction function) {
            this.function = function;
        }

        @Override
        public double applyAsDouble(int value) {
            return function.applyAsDouble(value);
        }
    }

    private static final class IntToLongFunctionWrapper implements IntToLongFunction {

        private final IntToLongFunction function;

        private IntToLongFunctionWrapper(IntToLongFunction function) {
            this.function = function;
        }

        @Override
        public long applyAsLong(int value) {
            return function.applyAsLong(value);
        }
    }

    private static final class IntUnaryOperatorWrapper implements IntUnaryOperator {

        private final IntUnaryOperator operator;

        private IntUnaryOperatorWrapper(IntUnaryOperator operator) {
            this.operator = operator;
        }

        @Override
        public int applyAsInt(int t) {
            return operator.applyAsInt(t);
        }
    }

    private static final class LongBinaryOperatorWrapper implements LongBinaryOperator {

        private final LongBinaryOperator operator;

        private LongBinaryOperatorWrapper(LongBinaryOperator operator) {
            this.operator = operator;
        }

        @Override
        public long applyAsLong(long t, long u) {
            return operator.applyAsLong(t, u);
        }
    }

    private static final class LongConsumerWrapper implements LongConsumer {

        private final LongConsumer consumer;

        private LongConsumerWrapper(LongConsumer consumer) {
            this.consumer = consumer;
        }

        @Override
        public void accept(long t) {
            consumer.accept(t);
        }
    }

    private static final class LongFunctionWrapper<R> implements LongFunction<R> {

        private final LongFunction<R> function;

        private LongFunctionWrapper(LongFunction<R> function) {
            this.function = function;
        }

        @Override
        public R apply(long t) {
            return function.apply(t);
        }
    }

    private static final class LongPredicateWrapper implements LongPredicate {

        private final LongPredicate predicate;

        private LongPredicateWrapper(LongPredicate predicate) {
            this.predicate = predicate;
        }

        @Override
        public boolean test(long t) {
            return predicate.test(t);
        }
    }

    private static final class LongSupplierWrapper implements LongSupplier {

        private final LongSupplier supplier;

        private LongSupplierWrapper(LongSupplier supplier) {
            this.supplier = supplier;
        }

        @Override
        public long getAsLong() {
            return supplier.getAsLong();
        }
    }

    private static final class LongToDoubleFunctionWrapper implements LongToDoubleFunction {

        private final LongToDoubleFunction function;

        private LongToDoubleFunctionWrapper(LongToDoubleFunction function) {
            this.function = function;
        }

        @Override
        public double applyAsDouble(long value) {
            return function.applyAsDouble(value);
        }
    }

    private static final class LongToIntFunctionWrapper implements LongToIntFunction {

        private final LongToIntFunction function;

        private LongToIntFunctionWrapper(LongToIntFunction function) {
            this.function = function;
        }

        @Override
        public int applyAsInt(long value) {
            return function.applyAsInt(value);
        }
    }

    private static final class LongUnaryOperatorWrapper implements LongUnaryOperator {

        private final LongUnaryOperator operator;

        private LongUnaryOperatorWrapper(LongUnaryOperator operator) {
            this.operator = operator;
        }

        @Override
        public long applyAsLong(long t) {
            return operator.applyAsLong(t);
        }
    }

    private static final class ObjDoubleConsumerWrapper<T> implements ObjDoubleConsumer<T> {

        private final ObjDoubleConsumer<T> consumer;

        private ObjDoubleConsumerWrapper(ObjDoubleConsumer<T> consumer) {
            this.consumer = consumer;
        }

        @Override
        public void accept(T t, double u) {
            consumer.accept(t, u);
        }
    }

    private static final class ObjIntConsumerWrapper<T> implements ObjIntConsumer<T> {

        private final ObjIntConsumer<T> consumer;

        private ObjIntConsumerWrapper(ObjIntConsumer<T> consumer) {
            this.consumer = consumer;
        }

        @Override
        public void accept(T t, int u) {
            consumer.accept(t, u);
        }
    }

    private static final class ObjLongConsumerWrapper<T> implements ObjLongConsumer<T> {

        private final ObjLongConsumer<T> consumer;

        private ObjLongConsumerWrapper(ObjLongConsumer<T> consumer) {
            this.consumer = consumer;
        }

        @Override
        public void accept(T t, long u) {
            consumer.accept(t, u);
        }
    }

    private static final class PredicateWrapper<T> implements Predicate<T> {

        private final Predicate<T> predicate;

        private PredicateWrapper(Predicate<T> predicate) {
            this.predicate = predicate;
        }

        @Override
        public boolean test(T t) {
            return predicate.test(t);
        }
    }

    private static final class RunnableWrapper implements Runnable {

        private final Runnable runnable;

        private RunnableWrapper(Runnable runnable) {
            this.runnable = runnable;
        }

        @Override
        public void run() {
            runnable.run();
        }
    }

    private static final class SupplierWrapper<T> implements Supplier<T> {

        private final Supplier<T> supplier;

        private SupplierWrapper(Supplier<T> supplier) {
            this.supplier = supplier;
        }

        @Override
        public T get() {
            return supplier.get();
        }
    }

    private static final class ToDoubleBiFunctionWrapper<T, U> implements ToDoubleBiFunction<T, U> {

        private final ToDoubleBiFunction<T, U> function;

        private ToDoubleBiFunctionWrapper(ToDoubleBiFunction<T, U> function) {
            this.function = function;
        }

        @Override
        public double applyAsDouble(T t, U u) {
            return function.applyAsDouble(t, u);
        }
    }

    private static final class ToDoubleFunctionWrapper<T> implements ToDoubleFunction<T> {

        private final ToDoubleFunction<T> function;

        private ToDoubleFunctionWrapper(ToDoubleFunction<T> function) {
            this.function = function;
        }

        @Override
        public double applyAsDouble(T value) {
            return function.applyAsDouble(value);
        }
    }

    private static final class ToIntBiFunctionWrapper<T, U> implements ToIntBiFunction<T, U> {

        private final ToIntBiFunction<T, U> function;

        private ToIntBiFunctionWrapper(ToIntBiFunction<T, U> function) {
            this.function = function;
        }

        @Override
        public int applyAsInt(T t, U u) {
            return function.applyAsInt(t, u);
        }
    }

    private static final class ToIntFunctionWrapper<T> implements ToIntFunction<T> {

        private final ToIntFunction<T> function;

        private ToIntFunctionWrapper(ToIntFunction<T> function) {
            this.function = function;
        }

        @Override
        public int applyAsInt(T value) {
            return function.applyAsInt(value);
        }
    }

    private static final class ToLongBiFunctionWrapper<T, U> implements ToLongBiFunction<T, U> {

        private final ToLongBiFunction<T, U> function;

        private ToLongBiFunctionWrapper(ToLongBiFunction<T, U> function) {
            this.function = function;
        }

        @Override
        public long applyAsLong(T t, U u) {
            return function.applyAsLong(t, u);
        }
    }

    private static final class ToLongFunctionWrapper<T> implements ToLongFunction<T> {

        private final ToLongFunction<T> function;

        private ToLongFunctionWrapper(ToLongFunction<T> function) {
            this.function = function;
        }

        @Override
        public long applyAsLong(T value) {
            return function.applyAsLong(value);
        }
    }

    private static final class UnaryOperatorWrapper<T> implements UnaryOperator<T> {

        private UnaryOperator<T> operator;

        private UnaryOperatorWrapper(UnaryOperator<T> operator) {
            this.operator = operator;
        }

        @Override
        public T apply(T t) {
            return operator.apply(t);
        }
    }
}
