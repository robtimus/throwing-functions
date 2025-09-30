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

package com.github.robtimus.function.checked;

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

    static <T, U, X extends Throwable> ThrowingBiConsumer<T, U, X> checkedBiConsumer(ThrowingBiConsumer<T, U, X> consumer) {
        return spy(new CheckedBiConsumerWrapper<>(consumer));
    }

    static <T, U, R, X extends Throwable> ThrowingBiFunction<T, U, R, X> checkedBiFunction(ThrowingBiFunction<T, U, R, X> function) {
        return spy(new CheckedBiFunctionWrapper<>(function));
    }

    static <T, X extends Throwable> ThrowingBinaryOperator<T, X> checkedBinaryOperator(ThrowingBinaryOperator<T, X> operator) {
        return spy(new CheckedBinaryOperatorWrapper<>(operator));
    }

    static <T, U, X extends Throwable> ThrowingBiPredicate<T, U, X> checkedBiPredicate(ThrowingBiPredicate<T, U, X> predicate) {
        return spy(new CheckedBiPredicateWrapper<>(predicate));
    }

    static <X extends Throwable> ThrowingBooleanSupplier<X> checkedBooleanSupplier(ThrowingBooleanSupplier<X> supplier) {
        return spy(new CheckedBooleanSupplierWrapper<>(supplier));
    }

    static <T, X extends Throwable> ThrowingConsumer<T, X> checkedConsumer(ThrowingConsumer<T, X> consumer) {
        return spy(new CheckedConsumerWrapper<>(consumer));
    }

    static <X extends Throwable> ThrowingDoubleBinaryOperator<X> checkedDoubleBinaryOperator(ThrowingDoubleBinaryOperator<X> operator) {
        return spy(new CheckedDoubleBinaryOperatorWrapper<>(operator));
    }

    static <X extends Throwable> ThrowingDoubleConsumer<X> checkedDoubleConsumer(ThrowingDoubleConsumer<X> consumer) {
        return spy(new CheckedDoubleConsumerWrapper<>(consumer));
    }

    static <R, X extends Throwable> ThrowingDoubleFunction<R, X> checkedDoubleFunction(ThrowingDoubleFunction<R, X> function) {
        return spy(new CheckedDoubleFunctionWrapper<>(function));
    }

    static <X extends Throwable> ThrowingDoublePredicate<X> checkedDoublePredicate(ThrowingDoublePredicate<X> predicate) {
        return spy(new CheckedDoublePredicateWrapper<>(predicate));
    }

    static <X extends Throwable> ThrowingDoubleSupplier<X> checkedDoubleSupplier(ThrowingDoubleSupplier<X> supplier) {
        return spy(new CheckedDoubleSupplierWrapper<>(supplier));
    }

    static <X extends Throwable> ThrowingDoubleToIntFunction<X> checkedDoubleToIntFunction(ThrowingDoubleToIntFunction<X> function) {
        return spy(new CheckedDoubleToIntFunctionWrapper<>(function));
    }

    static <X extends Throwable> ThrowingDoubleToLongFunction<X> checkedDoubleToLongFunction(ThrowingDoubleToLongFunction<X> function) {
        return spy(new CheckedDoubleToLongFunctionWrapper<>(function));
    }

    static <X extends Throwable> ThrowingDoubleUnaryOperator<X> checkedDoubleUnaryOperator(ThrowingDoubleUnaryOperator<X> operator) {
        return spy(new CheckedDoubleUnaryOperatorWrapper<>(operator));
    }

    static <T, R, X extends Throwable> ThrowingFunction<T, R, X> checkedFunction(ThrowingFunction<T, R, X> function) {
        return spy(new CheckedFunctionWrapper<>(function));
    }

    static <X extends Throwable> ThrowingIntBinaryOperator<X> checkedIntBinaryOperator(ThrowingIntBinaryOperator<X> operator) {
        return spy(new CheckedIntBinaryOperatorWrapper<>(operator));
    }

    static <X extends Throwable> ThrowingIntConsumer<X> checkedIntConsumer(ThrowingIntConsumer<X> consumer) {
        return spy(new CheckedIntConsumerWrapper<>(consumer));
    }

    static <R, X extends Throwable> ThrowingIntFunction<R, X> checkedIntFunction(ThrowingIntFunction<R, X> function) {
        return spy(new CheckedIntFunctionWrapper<>(function));
    }

    static <X extends Throwable> ThrowingIntPredicate<X> checkedIntPredicate(ThrowingIntPredicate<X> predicate) {
        return spy(new CheckedIntPredicateWrapper<>(predicate));
    }

    static <X extends Throwable> ThrowingIntSupplier<X> checkedIntSupplier(ThrowingIntSupplier<X> supplier) {
        return spy(new CheckedIntSupplierWrapper<>(supplier));
    }

    static <X extends Throwable> ThrowingIntToDoubleFunction<X> checkedIntToDoubleFunction(ThrowingIntToDoubleFunction<X> function) {
        return spy(new CheckedIntToDoubleFunctionWrapper<>(function));
    }

    static <X extends Throwable> ThrowingIntToLongFunction<X> checkedIntToLongFunction(ThrowingIntToLongFunction<X> function) {
        return spy(new CheckedIntToLongFunctionWrapper<>(function));
    }

    static <X extends Throwable> ThrowingIntUnaryOperator<X> checkedIntUnaryOperator(ThrowingIntUnaryOperator<X> operator) {
        return spy(new CheckedIntUnaryOperatorWrapper<>(operator));
    }

    static <X extends Throwable> ThrowingLongBinaryOperator<X> checkedLongBinaryOperator(ThrowingLongBinaryOperator<X> operator) {
        return spy(new CheckedLongBinaryOperatorWrapper<>(operator));
    }

    static <X extends Throwable> ThrowingLongConsumer<X> checkedLongConsumer(ThrowingLongConsumer<X> consumer) {
        return spy(new CheckedLongConsumerWrapper<>(consumer));
    }

    static <R, X extends Throwable> ThrowingLongFunction<R, X> checkedLongFunction(ThrowingLongFunction<R, X> function) {
        return spy(new CheckedLongFunctionWrapper<>(function));
    }

    static <X extends Throwable> ThrowingLongPredicate<X> checkedLongPredicate(ThrowingLongPredicate<X> predicate) {
        return spy(new CheckedLongPredicateWrapper<>(predicate));
    }

    static <X extends Throwable> ThrowingLongSupplier<X> checkedLongSupplier(ThrowingLongSupplier<X> supplier) {
        return spy(new CheckedLongSupplierWrapper<>(supplier));
    }

    static <X extends Throwable> ThrowingLongToDoubleFunction<X> checkedLongToDoubleFunction(ThrowingLongToDoubleFunction<X> function) {
        return spy(new CheckedLongToDoubleFunctionWrapper<>(function));
    }

    static <X extends Throwable> ThrowingLongToIntFunction<X> checkedLongToIntFunction(ThrowingLongToIntFunction<X> function) {
        return spy(new CheckedLongToIntFunctionWrapper<>(function));
    }

    static <X extends Throwable> ThrowingLongUnaryOperator<X> checkedLongUnaryOperator(ThrowingLongUnaryOperator<X> operator) {
        return spy(new CheckedLongUnaryOperatorWrapper<>(operator));
    }

    static <T, X extends Throwable> ThrowingObjDoubleConsumer<T, X> checkedObjDoubleConsumer(ThrowingObjDoubleConsumer<T, X> consumer) {
        return spy(new CheckedObjDoubleConsumerWrapper<>(consumer));
    }

    static <T, X extends Throwable> ThrowingObjIntConsumer<T, X> checkedObjIntConsumer(ThrowingObjIntConsumer<T, X> consumer) {
        return spy(new CheckedObjIntConsumerWrapper<>(consumer));
    }

    static <T, X extends Throwable> ThrowingObjLongConsumer<T, X> checkedObjLongConsumer(ThrowingObjLongConsumer<T, X> consumer) {
        return spy(new CheckedObjLongConsumerWrapper<>(consumer));
    }

    static <T, X extends Throwable> ThrowingPredicate<T, X> checkedPredicate(ThrowingPredicate<T, X> predicate) {
        return spy(new CheckedPredicateWrapper<>(predicate));
    }

    static <X extends Throwable> ThrowingRunnable<X> checkedRunnable(ThrowingRunnable<X> runnable) {
        return spy(new CheckedRunnableWrapper<>(runnable));
    }

    static <T, X extends Throwable> ThrowingSupplier<T, X> checkedSupplier(ThrowingSupplier<T, X> supplier) {
        return spy(new CheckedSupplierWrapper<>(supplier));
    }

    static <T, U, X extends Throwable> ThrowingToDoubleBiFunction<T, U, X> checkedToDoubleBiFunction(ThrowingToDoubleBiFunction<T, U, X> function) {
        return spy(new CheckedToDoubleBiFunctionWrapper<>(function));
    }

    static <T, X extends Throwable> ThrowingToDoubleFunction<T, X> checkedToDoubleFunction(ThrowingToDoubleFunction<T, X> function) {
        return spy(new CheckedToDoubleFunctionWrapper<>(function));
    }

    static <T, U, X extends Throwable> ThrowingToIntBiFunction<T, U, X> checkedToIntBiFunction(ThrowingToIntBiFunction<T, U, X> function) {
        return spy(new CheckedToIntBiFunctionWrapper<>(function));
    }

    static <T, X extends Throwable> ThrowingToIntFunction<T, X> checkedToIntFunction(ThrowingToIntFunction<T, X> function) {
        return spy(new CheckedToIntFunctionWrapper<>(function));
    }

    static <T, U, X extends Throwable> ThrowingToLongBiFunction<T, U, X> checkedToLongBiFunction(ThrowingToLongBiFunction<T, U, X> function) {
        return spy(new CheckedToLongBiFunctionWrapper<>(function));
    }

    static <T, X extends Throwable> ThrowingToLongFunction<T, X> checkedToLongFunction(ThrowingToLongFunction<T, X> function) {
        return spy(new CheckedToLongFunctionWrapper<>(function));
    }

    static <T, X extends Throwable> ThrowingUnaryOperator<T, X> checkedUnaryOperator(ThrowingUnaryOperator<T, X> operator) {
        return spy(new CheckedUnaryOperatorWrapper<>(operator));
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

    private static final class CheckedBiConsumerWrapper<T, U, X extends Throwable> implements ThrowingBiConsumer<T, U, X> {

        private final ThrowingBiConsumer<T, U, X> consumer;

        private CheckedBiConsumerWrapper(ThrowingBiConsumer<T, U, X> consumer) {
            this.consumer = consumer;
        }

        @Override
        public void accept(T t, U u) throws X {
            consumer.accept(t, u);
        }
    }

    private static final class CheckedBiFunctionWrapper<T, U, R, X extends Throwable> implements ThrowingBiFunction<T, U, R, X> {

        private final ThrowingBiFunction<T, U, R, X> function;

        private CheckedBiFunctionWrapper(ThrowingBiFunction<T, U, R, X> function) {
            this.function = function;
        }

        @Override
        public R apply(T t, U u) throws X {
            return function.apply(t, u);
        }
    }

    private static final class CheckedBinaryOperatorWrapper<T, X extends Throwable> implements ThrowingBinaryOperator<T, X> {

        private final ThrowingBinaryOperator<T, X> operator;

        private CheckedBinaryOperatorWrapper(ThrowingBinaryOperator<T, X> operator) {
            this.operator = operator;
        }

        @Override
        public T apply(T t, T u) throws X {
            return operator.apply(t, u);
        }
    }

    private static final class CheckedBiPredicateWrapper<T, U, X extends Throwable> implements ThrowingBiPredicate<T, U, X> {

        private final ThrowingBiPredicate<T, U, X> predicate;

        private CheckedBiPredicateWrapper(ThrowingBiPredicate<T, U, X> predicate) {
            this.predicate = predicate;
        }

        @Override
        public boolean test(T t, U u) throws X {
            return predicate.test(t, u);
        }
    }

    private static final class CheckedBooleanSupplierWrapper<X extends Throwable> implements ThrowingBooleanSupplier<X> {

        private final ThrowingBooleanSupplier<X> supplier;

        private CheckedBooleanSupplierWrapper(ThrowingBooleanSupplier<X> supplier) {
            this.supplier = supplier;
        }

        @Override
        public boolean getAsBoolean() throws X {
            return supplier.getAsBoolean();
        }
    }

    private static final class CheckedConsumerWrapper<T, X extends Throwable> implements ThrowingConsumer<T, X> {

        private final ThrowingConsumer<T, X> consumer;

        private CheckedConsumerWrapper(ThrowingConsumer<T, X> consumer) {
            this.consumer = consumer;
        }

        @Override
        public void accept(T t) throws X {
            consumer.accept(t);
        }
    }

    private static final class CheckedDoubleBinaryOperatorWrapper<X extends Throwable> implements ThrowingDoubleBinaryOperator<X> {

        private final ThrowingDoubleBinaryOperator<X> operator;

        private CheckedDoubleBinaryOperatorWrapper(ThrowingDoubleBinaryOperator<X> operator) {
            this.operator = operator;
        }

        @Override
        public double applyAsDouble(double t, double u) throws X {
            return operator.applyAsDouble(t, u);
        }
    }

    private static final class CheckedDoubleConsumerWrapper<X extends Throwable> implements ThrowingDoubleConsumer<X> {

        private final ThrowingDoubleConsumer<X> consumer;

        private CheckedDoubleConsumerWrapper(ThrowingDoubleConsumer<X> consumer) {
            this.consumer = consumer;
        }

        @Override
        public void accept(double t) throws X {
            consumer.accept(t);
        }
    }

    private static final class CheckedDoubleFunctionWrapper<R, X extends Throwable> implements ThrowingDoubleFunction<R, X> {

        private final ThrowingDoubleFunction<R, X> function;

        private CheckedDoubleFunctionWrapper(ThrowingDoubleFunction<R, X> function) {
            this.function = function;
        }

        @Override
        public R apply(double t) throws X {
            return function.apply(t);
        }
    }

    private static final class CheckedDoublePredicateWrapper<X extends Throwable> implements ThrowingDoublePredicate<X> {

        private final ThrowingDoublePredicate<X> predicate;

        private CheckedDoublePredicateWrapper(ThrowingDoublePredicate<X> predicate) {
            this.predicate = predicate;
        }

        @Override
        public boolean test(double t) throws X {
            return predicate.test(t);
        }
    }

    private static final class CheckedDoubleSupplierWrapper<X extends Throwable> implements ThrowingDoubleSupplier<X> {

        private final ThrowingDoubleSupplier<X> supplier;

        private CheckedDoubleSupplierWrapper(ThrowingDoubleSupplier<X> supplier) {
            this.supplier = supplier;
        }

        @Override
        public double getAsDouble() throws X {
            return supplier.getAsDouble();
        }
    }

    private static final class CheckedDoubleToIntFunctionWrapper<X extends Throwable> implements ThrowingDoubleToIntFunction<X> {

        private final ThrowingDoubleToIntFunction<X> function;

        private CheckedDoubleToIntFunctionWrapper(ThrowingDoubleToIntFunction<X> function) {
            this.function = function;
        }

        @Override
        public int applyAsInt(double value) throws X {
            return function.applyAsInt(value);
        }
    }

    private static final class CheckedDoubleToLongFunctionWrapper<X extends Throwable> implements ThrowingDoubleToLongFunction<X> {

        private final ThrowingDoubleToLongFunction<X> function;

        private CheckedDoubleToLongFunctionWrapper(ThrowingDoubleToLongFunction<X> function) {
            this.function = function;
        }

        @Override
        public long applyAsLong(double value) throws X {
            return function.applyAsLong(value);
        }
    }

    private static final class CheckedDoubleUnaryOperatorWrapper<X extends Throwable> implements ThrowingDoubleUnaryOperator<X> {

        private final ThrowingDoubleUnaryOperator<X> operator;

        private CheckedDoubleUnaryOperatorWrapper(ThrowingDoubleUnaryOperator<X> operator) {
            this.operator = operator;
        }

        @Override
        public double applyAsDouble(double t) throws X {
            return operator.applyAsDouble(t);
        }
    }

    private static final class CheckedFunctionWrapper<T, R, X extends Throwable> implements ThrowingFunction<T, R, X> {

        private final ThrowingFunction<T, R, X> function;

        private CheckedFunctionWrapper(ThrowingFunction<T, R, X> function) {
            this.function = function;
        }

        @Override
        public R apply(T t) throws X {
            return function.apply(t);
        }
    }

    private static final class CheckedIntBinaryOperatorWrapper<X extends Throwable> implements ThrowingIntBinaryOperator<X> {

        private final ThrowingIntBinaryOperator<X> operator;

        private CheckedIntBinaryOperatorWrapper(ThrowingIntBinaryOperator<X> operator) {
            this.operator = operator;
        }

        @Override
        public int applyAsInt(int t, int u) throws X {
            return operator.applyAsInt(t, u);
        }
    }

    private static final class CheckedIntConsumerWrapper<X extends Throwable> implements ThrowingIntConsumer<X> {

        private final ThrowingIntConsumer<X> consumer;

        private CheckedIntConsumerWrapper(ThrowingIntConsumer<X> consumer) {
            this.consumer = consumer;
        }

        @Override
        public void accept(int t) throws X {
            consumer.accept(t);
        }
    }

    private static final class CheckedIntFunctionWrapper<R, X extends Throwable> implements ThrowingIntFunction<R, X> {

        private final ThrowingIntFunction<R, X> function;

        private CheckedIntFunctionWrapper(ThrowingIntFunction<R, X> function) {
            this.function = function;
        }

        @Override
        public R apply(int t) throws X {
            return function.apply(t);
        }
    }

    private static final class CheckedIntPredicateWrapper<X extends Throwable> implements ThrowingIntPredicate<X> {

        private final ThrowingIntPredicate<X> predicate;

        private CheckedIntPredicateWrapper(ThrowingIntPredicate<X> predicate) {
            this.predicate = predicate;
        }

        @Override
        public boolean test(int t) throws X {
            return predicate.test(t);
        }
    }

    private static final class CheckedIntSupplierWrapper<X extends Throwable> implements ThrowingIntSupplier<X> {

        private final ThrowingIntSupplier<X> supplier;

        private CheckedIntSupplierWrapper(ThrowingIntSupplier<X> supplier) {
            this.supplier = supplier;
        }

        @Override
        public int getAsInt() throws X {
            return supplier.getAsInt();
        }
    }

    private static final class CheckedIntToDoubleFunctionWrapper<X extends Throwable> implements ThrowingIntToDoubleFunction<X> {

        private final ThrowingIntToDoubleFunction<X> function;

        private CheckedIntToDoubleFunctionWrapper(ThrowingIntToDoubleFunction<X> function) {
            this.function = function;
        }

        @Override
        public double applyAsDouble(int value) throws X {
            return function.applyAsDouble(value);
        }
    }

    private static final class CheckedIntToLongFunctionWrapper<X extends Throwable> implements ThrowingIntToLongFunction<X> {

        private final ThrowingIntToLongFunction<X> function;

        private CheckedIntToLongFunctionWrapper(ThrowingIntToLongFunction<X> function) {
            this.function = function;
        }

        @Override
        public long applyAsLong(int value) throws X {
            return function.applyAsLong(value);
        }
    }

    private static final class CheckedIntUnaryOperatorWrapper<X extends Throwable> implements ThrowingIntUnaryOperator<X> {

        private final ThrowingIntUnaryOperator<X> operator;

        private CheckedIntUnaryOperatorWrapper(ThrowingIntUnaryOperator<X> operator) {
            this.operator = operator;
        }

        @Override
        public int applyAsInt(int t) throws X {
            return operator.applyAsInt(t);
        }
    }

    private static final class CheckedLongBinaryOperatorWrapper<X extends Throwable> implements ThrowingLongBinaryOperator<X> {

        private final ThrowingLongBinaryOperator<X> operator;

        private CheckedLongBinaryOperatorWrapper(ThrowingLongBinaryOperator<X> operator) {
            this.operator = operator;
        }

        @Override
        public long applyAsLong(long t, long u) throws X {
            return operator.applyAsLong(t, u);
        }
    }

    private static final class CheckedLongConsumerWrapper<X extends Throwable> implements ThrowingLongConsumer<X> {

        private final ThrowingLongConsumer<X> consumer;

        private CheckedLongConsumerWrapper(ThrowingLongConsumer<X> consumer) {
            this.consumer = consumer;
        }

        @Override
        public void accept(long t) throws X {
            consumer.accept(t);
        }
    }

    private static final class CheckedLongFunctionWrapper<R, X extends Throwable> implements ThrowingLongFunction<R, X> {

        private final ThrowingLongFunction<R, X> function;

        private CheckedLongFunctionWrapper(ThrowingLongFunction<R, X> function) {
            this.function = function;
        }

        @Override
        public R apply(long t) throws X {
            return function.apply(t);
        }
    }

    private static final class CheckedLongPredicateWrapper<X extends Throwable> implements ThrowingLongPredicate<X> {

        private final ThrowingLongPredicate<X> predicate;

        private CheckedLongPredicateWrapper(ThrowingLongPredicate<X> predicate) {
            this.predicate = predicate;
        }

        @Override
        public boolean test(long t) throws X {
            return predicate.test(t);
        }
    }

    private static final class CheckedLongSupplierWrapper<X extends Throwable> implements ThrowingLongSupplier<X> {

        private final ThrowingLongSupplier<X> supplier;

        private CheckedLongSupplierWrapper(ThrowingLongSupplier<X> supplier) {
            this.supplier = supplier;
        }

        @Override
        public long getAsLong() throws X {
            return supplier.getAsLong();
        }
    }

    private static final class CheckedLongToDoubleFunctionWrapper<X extends Throwable> implements ThrowingLongToDoubleFunction<X> {

        private final ThrowingLongToDoubleFunction<X> function;

        private CheckedLongToDoubleFunctionWrapper(ThrowingLongToDoubleFunction<X> function) {
            this.function = function;
        }

        @Override
        public double applyAsDouble(long value) throws X {
            return function.applyAsDouble(value);
        }
    }

    private static final class CheckedLongToIntFunctionWrapper<X extends Throwable> implements ThrowingLongToIntFunction<X> {

        private final ThrowingLongToIntFunction<X> function;

        private CheckedLongToIntFunctionWrapper(ThrowingLongToIntFunction<X> function) {
            this.function = function;
        }

        @Override
        public int applyAsInt(long value) throws X {
            return function.applyAsInt(value);
        }
    }

    private static final class CheckedLongUnaryOperatorWrapper<X extends Throwable> implements ThrowingLongUnaryOperator<X> {

        private final ThrowingLongUnaryOperator<X> operator;

        private CheckedLongUnaryOperatorWrapper(ThrowingLongUnaryOperator<X> operator) {
            this.operator = operator;
        }

        @Override
        public long applyAsLong(long t) throws X {
            return operator.applyAsLong(t);
        }
    }

    private static final class CheckedObjDoubleConsumerWrapper<T, X extends Throwable> implements ThrowingObjDoubleConsumer<T, X> {

        private final ThrowingObjDoubleConsumer<T, X> consumer;

        private CheckedObjDoubleConsumerWrapper(ThrowingObjDoubleConsumer<T, X> consumer) {
            this.consumer = consumer;
        }

        @Override
        public void accept(T t, double u) throws X {
            consumer.accept(t, u);
        }
    }

    private static final class CheckedObjIntConsumerWrapper<T, X extends Throwable> implements ThrowingObjIntConsumer<T, X> {

        private final ThrowingObjIntConsumer<T, X> consumer;

        private CheckedObjIntConsumerWrapper(ThrowingObjIntConsumer<T, X> consumer) {
            this.consumer = consumer;
        }

        @Override
        public void accept(T t, int u) throws X {
            consumer.accept(t, u);
        }
    }

    private static final class CheckedObjLongConsumerWrapper<T, X extends Throwable> implements ThrowingObjLongConsumer<T, X> {

        private final ThrowingObjLongConsumer<T, X> consumer;

        private CheckedObjLongConsumerWrapper(ThrowingObjLongConsumer<T, X> consumer) {
            this.consumer = consumer;
        }

        @Override
        public void accept(T t, long u) throws X {
            consumer.accept(t, u);
        }
    }

    private static final class CheckedPredicateWrapper<T, X extends Throwable> implements ThrowingPredicate<T, X> {

        private final ThrowingPredicate<T, X> predicate;

        private CheckedPredicateWrapper(ThrowingPredicate<T, X> predicate) {
            this.predicate = predicate;
        }

        @Override
        public boolean test(T t) throws X {
            return predicate.test(t);
        }
    }

    private static final class CheckedRunnableWrapper<X extends Throwable> implements ThrowingRunnable<X> {

        private final ThrowingRunnable<X> runnable;

        private CheckedRunnableWrapper(ThrowingRunnable<X> runnable) {
            this.runnable = runnable;
        }

        @Override
        public void run() throws X {
            runnable.run();
        }
    }

    private static final class CheckedSupplierWrapper<T, X extends Throwable> implements ThrowingSupplier<T, X> {

        private final ThrowingSupplier<T, X> supplier;

        private CheckedSupplierWrapper(ThrowingSupplier<T, X> supplier) {
            this.supplier = supplier;
        }

        @Override
        public T get() throws X {
            return supplier.get();
        }
    }

    private static final class CheckedToDoubleBiFunctionWrapper<T, U, X extends Throwable> implements ThrowingToDoubleBiFunction<T, U, X> {

        private final ThrowingToDoubleBiFunction<T, U, X> function;

        private CheckedToDoubleBiFunctionWrapper(ThrowingToDoubleBiFunction<T, U, X> function) {
            this.function = function;
        }

        @Override
        public double applyAsDouble(T t, U u) throws X {
            return function.applyAsDouble(t, u);
        }
    }

    private static final class CheckedToDoubleFunctionWrapper<T, X extends Throwable> implements ThrowingToDoubleFunction<T, X> {

        private final ThrowingToDoubleFunction<T, X> function;

        private CheckedToDoubleFunctionWrapper(ThrowingToDoubleFunction<T, X> function) {
            this.function = function;
        }

        @Override
        public double applyAsDouble(T value) throws X {
            return function.applyAsDouble(value);
        }
    }

    private static final class CheckedToIntBiFunctionWrapper<T, U, X extends Throwable> implements ThrowingToIntBiFunction<T, U, X> {

        private final ThrowingToIntBiFunction<T, U, X> function;

        private CheckedToIntBiFunctionWrapper(ThrowingToIntBiFunction<T, U, X> function) {
            this.function = function;
        }

        @Override
        public int applyAsInt(T t, U u) throws X {
            return function.applyAsInt(t, u);
        }
    }

    private static final class CheckedToIntFunctionWrapper<T, X extends Throwable> implements ThrowingToIntFunction<T, X> {

        private final ThrowingToIntFunction<T, X> function;

        private CheckedToIntFunctionWrapper(ThrowingToIntFunction<T, X> function) {
            this.function = function;
        }

        @Override
        public int applyAsInt(T value) throws X {
            return function.applyAsInt(value);
        }
    }

    private static final class CheckedToLongBiFunctionWrapper<T, U, X extends Throwable> implements ThrowingToLongBiFunction<T, U, X> {

        private final ThrowingToLongBiFunction<T, U, X> function;

        private CheckedToLongBiFunctionWrapper(ThrowingToLongBiFunction<T, U, X> function) {
            this.function = function;
        }

        @Override
        public long applyAsLong(T t, U u) throws X {
            return function.applyAsLong(t, u);
        }
    }

    private static final class CheckedToLongFunctionWrapper<T, X extends Throwable> implements ThrowingToLongFunction<T, X> {

        private final ThrowingToLongFunction<T, X> function;

        private CheckedToLongFunctionWrapper(ThrowingToLongFunction<T, X> function) {
            this.function = function;
        }

        @Override
        public long applyAsLong(T value) throws X {
            return function.applyAsLong(value);
        }
    }

    private static final class CheckedUnaryOperatorWrapper<T, X extends Throwable> implements ThrowingUnaryOperator<T, X> {

        private final ThrowingUnaryOperator<T, X> operator;

        private CheckedUnaryOperatorWrapper(ThrowingUnaryOperator<T, X> operator) {
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
