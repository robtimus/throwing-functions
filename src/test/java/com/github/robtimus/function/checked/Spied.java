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
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;
import java.util.function.IntBinaryOperator;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.function.IntSupplier;
import java.util.function.IntUnaryOperator;
import java.util.function.LongBinaryOperator;
import java.util.function.LongConsumer;
import java.util.function.LongFunction;
import java.util.function.LongPredicate;
import java.util.function.LongSupplier;
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

    static <T, U, X extends Exception> CheckedBiConsumer<T, U, X> checkedBiConsumer(CheckedBiConsumer<T, U, X> consumer) {
        return spy(new CheckedBiConsumerWrapper<>(consumer));
    }

    static <T, U, R, X extends Exception> CheckedBiFunction<T, U, R, X> checkedBiFunction(CheckedBiFunction<T, U, R, X> function) {
        return spy(new CheckedBiFunctionWrapper<>(function));
    }

    static <T, X extends Exception> CheckedBinaryOperator<T, X> checkedBinaryOperator(CheckedBinaryOperator<T, X> operator) {
        return spy(new CheckedBinaryOperatorWrapper<>(operator));
    }

    static <T, U, X extends Exception> CheckedBiPredicate<T, U, X> checkedBiPredicate(CheckedBiPredicate<T, U, X> predicate) {
        return spy(new CheckedBiPredicateWrapper<>(predicate));
    }

    static <X extends Exception> CheckedBooleanSupplier<X> checkedBooleanSupplier(CheckedBooleanSupplier<X> supplier) {
        return spy(new CheckedBooleanSupplierWrapper<>(supplier));
    }

    static <T, X extends Exception> CheckedConsumer<T, X> checkedConsumer(CheckedConsumer<T, X> consumer) {
        return spy(new CheckedConsumerWrapper<>(consumer));
    }

    static <X extends Exception> CheckedDoubleBinaryOperator<X> checkedDoubleBinaryOperator(CheckedDoubleBinaryOperator<X> operator) {
        return spy(new CheckedDoubleBinaryOperatorWrapper<>(operator));
    }

    static <X extends Exception> CheckedDoubleConsumer<X> checkedDoubleConsumer(CheckedDoubleConsumer<X> consumer) {
        return spy(new CheckedDoubleConsumerWrapper<>(consumer));
    }

    static <R, X extends Exception> CheckedDoubleFunction<R, X> checkedDoubleFunction(CheckedDoubleFunction<R, X> function) {
        return spy(new CheckedDoubleFunctionWrapper<>(function));
    }

    static <X extends Exception> CheckedDoublePredicate<X> checkedDoublePredicate(CheckedDoublePredicate<X> predicate) {
        return spy(new CheckedDoublePredicateWrapper<>(predicate));
    }

    static <X extends Exception> CheckedDoubleSupplier<X> checkedDoubleSupplier(CheckedDoubleSupplier<X> supplier) {
        return spy(new CheckedDoubleSupplierWrapper<>(supplier));
    }

    static <X extends Exception> CheckedDoubleUnaryOperator<X> checkedDoubleUnaryOperator(CheckedDoubleUnaryOperator<X> operator) {
        return spy(new CheckedDoubleUnaryOperatorWrapper<>(operator));
    }

    static <T, R, X extends Exception> CheckedFunction<T, R, X> checkedFunction(CheckedFunction<T, R, X> function) {
        return spy(new CheckedFunctionWrapper<>(function));
    }

    static <X extends Exception> CheckedIntBinaryOperator<X> checkedIntBinaryOperator(CheckedIntBinaryOperator<X> operator) {
        return spy(new CheckedIntBinaryOperatorWrapper<>(operator));
    }

    static <X extends Exception> CheckedIntConsumer<X> checkedIntConsumer(CheckedIntConsumer<X> consumer) {
        return spy(new CheckedIntConsumerWrapper<>(consumer));
    }

    static <R, X extends Exception> CheckedIntFunction<R, X> checkedIntFunction(CheckedIntFunction<R, X> function) {
        return spy(new CheckedIntFunctionWrapper<>(function));
    }

    static <X extends Exception> CheckedIntPredicate<X> checkedIntPredicate(CheckedIntPredicate<X> predicate) {
        return spy(new CheckedIntPredicateWrapper<>(predicate));
    }

    static <X extends Exception> CheckedIntSupplier<X> checkedIntSupplier(CheckedIntSupplier<X> supplier) {
        return spy(new CheckedIntSupplierWrapper<>(supplier));
    }

    static <X extends Exception> CheckedIntUnaryOperator<X> checkedIntUnaryOperator(CheckedIntUnaryOperator<X> operator) {
        return spy(new CheckedIntUnaryOperatorWrapper<>(operator));
    }

    static <X extends Exception> CheckedLongBinaryOperator<X> checkedLongBinaryOperator(CheckedLongBinaryOperator<X> operator) {
        return spy(new CheckedLongBinaryOperatorWrapper<>(operator));
    }

    static <X extends Exception> CheckedLongConsumer<X> checkedLongConsumer(CheckedLongConsumer<X> consumer) {
        return spy(new CheckedLongConsumerWrapper<>(consumer));
    }

    static <R, X extends Exception> CheckedLongFunction<R, X> checkedLongFunction(CheckedLongFunction<R, X> function) {
        return spy(new CheckedLongFunctionWrapper<>(function));
    }

    static <X extends Exception> CheckedLongPredicate<X> checkedLongPredicate(CheckedLongPredicate<X> predicate) {
        return spy(new CheckedLongPredicateWrapper<>(predicate));
    }

    static <X extends Exception> CheckedLongSupplier<X> checkedLongSupplier(CheckedLongSupplier<X> supplier) {
        return spy(new CheckedLongSupplierWrapper<>(supplier));
    }

    static <X extends Exception> CheckedLongUnaryOperator<X> checkedLongUnaryOperator(CheckedLongUnaryOperator<X> operator) {
        return spy(new CheckedLongUnaryOperatorWrapper<>(operator));
    }

    static <T, X extends Exception> CheckedObjDoubleConsumer<T, X> checkedObjDoubleConsumer(CheckedObjDoubleConsumer<T, X> consumer) {
        return spy(new CheckedObjDoubleConsumerWrapper<>(consumer));
    }

    static <T, X extends Exception> CheckedObjIntConsumer<T, X> checkedObjIntConsumer(CheckedObjIntConsumer<T, X> consumer) {
        return spy(new CheckedObjIntConsumerWrapper<>(consumer));
    }

    static <T, X extends Exception> CheckedObjLongConsumer<T, X> checkedObjLongConsumer(CheckedObjLongConsumer<T, X> consumer) {
        return spy(new CheckedObjLongConsumerWrapper<>(consumer));
    }

    static <T, X extends Exception> CheckedPredicate<T, X> checkedPredicate(CheckedPredicate<T, X> predicate) {
        return spy(new CheckedPredicateWrapper<>(predicate));
    }

    static <X extends Exception> CheckedRunnable<X> checkedRunnable(CheckedRunnable<X> runnable) {
        return spy(new CheckedRunnableWrapper<>(runnable));
    }

    static <T, X extends Exception> CheckedSupplier<T, X> checkedSupplier(CheckedSupplier<T, X> supplier) {
        return spy(new CheckedSupplierWrapper<>(supplier));
    }

    static <T, U, X extends Exception> CheckedToDoubleBiFunction<T, U, X> checkedToDoubleBiFunction(CheckedToDoubleBiFunction<T, U, X> function) {
        return spy(new CheckedToDoubleBiFunctionWrapper<>(function));
    }

    static <T, X extends Exception> CheckedToDoubleFunction<T, X> checkedToDoubleFunction(CheckedToDoubleFunction<T, X> function) {
        return spy(new CheckedToDoubleFunctionWrapper<>(function));
    }

    static <T, U, X extends Exception> CheckedToIntBiFunction<T, U, X> checkedToIntBiFunction(CheckedToIntBiFunction<T, U, X> function) {
        return spy(new CheckedToIntBiFunctionWrapper<>(function));
    }

    static <T, X extends Exception> CheckedToIntFunction<T, X> checkedToIntFunction(CheckedToIntFunction<T, X> function) {
        return spy(new CheckedToIntFunctionWrapper<>(function));
    }

    static <T, U, X extends Exception> CheckedToLongBiFunction<T, U, X> checkedToLongBiFunction(CheckedToLongBiFunction<T, U, X> function) {
        return spy(new CheckedToLongBiFunctionWrapper<>(function));
    }

    static <T, X extends Exception> CheckedToLongFunction<T, X> checkedToLongFunction(CheckedToLongFunction<T, X> function) {
        return spy(new CheckedToLongFunctionWrapper<>(function));
    }

    static <T, X extends Exception> CheckedUnaryOperator<T, X> checkedUnaryOperator(CheckedUnaryOperator<T, X> operator) {
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

    private static final class CheckedBiConsumerWrapper<T, U, X extends Exception> implements CheckedBiConsumer<T, U, X> {

        private final CheckedBiConsumer<T, U, X> consumer;

        private CheckedBiConsumerWrapper(CheckedBiConsumer<T, U, X> consumer) {
            this.consumer = consumer;
        }

        @Override
        public void accept(T t, U u) throws X {
            consumer.accept(t, u);
        }
    }

    private static final class CheckedBiFunctionWrapper<T, U, R, X extends Exception> implements CheckedBiFunction<T, U, R, X> {

        private final CheckedBiFunction<T, U, R, X> function;

        private CheckedBiFunctionWrapper(CheckedBiFunction<T, U, R, X> function) {
            this.function = function;
        }

        @Override
        public R apply(T t, U u) throws X {
            return function.apply(t, u);
        }
    }

    private static final class CheckedBinaryOperatorWrapper<T, X extends Exception> implements CheckedBinaryOperator<T, X> {

        private final CheckedBinaryOperator<T, X> operator;

        private CheckedBinaryOperatorWrapper(CheckedBinaryOperator<T, X> operator) {
            this.operator = operator;
        }

        @Override
        public T apply(T t, T u) throws X {
            return operator.apply(t, u);
        }
    }

    private static final class CheckedBiPredicateWrapper<T, U, X extends Exception> implements CheckedBiPredicate<T, U, X> {

        private final CheckedBiPredicate<T, U, X> predicate;

        private CheckedBiPredicateWrapper(CheckedBiPredicate<T, U, X> predicate) {
            this.predicate = predicate;
        }

        @Override
        public boolean test(T t, U u) throws X {
            return predicate.test(t, u);
        }
    }

    private static final class CheckedBooleanSupplierWrapper<X extends Exception> implements CheckedBooleanSupplier<X> {

        private final CheckedBooleanSupplier<X> supplier;

        private CheckedBooleanSupplierWrapper(CheckedBooleanSupplier<X> supplier) {
            this.supplier = supplier;
        }

        @Override
        public boolean getAsBoolean() throws X {
            return supplier.getAsBoolean();
        }
    }

    private static final class CheckedConsumerWrapper<T, X extends Exception> implements CheckedConsumer<T, X> {

        private final CheckedConsumer<T, X> consumer;

        private CheckedConsumerWrapper(CheckedConsumer<T, X> consumer) {
            this.consumer = consumer;
        }

        @Override
        public void accept(T t) throws X {
            consumer.accept(t);
        }
    }

    private static final class CheckedDoubleBinaryOperatorWrapper<X extends Exception> implements CheckedDoubleBinaryOperator<X> {

        private final CheckedDoubleBinaryOperator<X> operator;

        private CheckedDoubleBinaryOperatorWrapper(CheckedDoubleBinaryOperator<X> operator) {
            this.operator = operator;
        }

        @Override
        public double applyAsDouble(double t, double u) throws X {
            return operator.applyAsDouble(t, u);
        }
    }

    private static final class CheckedDoubleConsumerWrapper<X extends Exception> implements CheckedDoubleConsumer<X> {

        private final CheckedDoubleConsumer<X> consumer;

        private CheckedDoubleConsumerWrapper(CheckedDoubleConsumer<X> consumer) {
            this.consumer = consumer;
        }

        @Override
        public void accept(double t) throws X {
            consumer.accept(t);
        }
    }

    private static final class CheckedDoubleFunctionWrapper<R, X extends Exception> implements CheckedDoubleFunction<R, X> {

        private final CheckedDoubleFunction<R, X> function;

        private CheckedDoubleFunctionWrapper(CheckedDoubleFunction<R, X> function) {
            this.function = function;
        }

        @Override
        public R apply(double t) throws X {
            return function.apply(t);
        }
    }

    private static final class CheckedDoublePredicateWrapper<X extends Exception> implements CheckedDoublePredicate<X> {

        private final CheckedDoublePredicate<X> predicate;

        private CheckedDoublePredicateWrapper(CheckedDoublePredicate<X> predicate) {
            this.predicate = predicate;
        }

        @Override
        public boolean test(double t) throws X {
            return predicate.test(t);
        }
    }

    private static final class CheckedDoubleSupplierWrapper<X extends Exception> implements CheckedDoubleSupplier<X> {

        private final CheckedDoubleSupplier<X> supplier;

        private CheckedDoubleSupplierWrapper(CheckedDoubleSupplier<X> supplier) {
            this.supplier = supplier;
        }

        @Override
        public double getAsDouble() throws X {
            return supplier.getAsDouble();
        }
    }

    private static final class CheckedDoubleUnaryOperatorWrapper<X extends Exception> implements CheckedDoubleUnaryOperator<X> {

        private final CheckedDoubleUnaryOperator<X> operator;

        private CheckedDoubleUnaryOperatorWrapper(CheckedDoubleUnaryOperator<X> operator) {
            this.operator = operator;
        }

        @Override
        public double applyAsDouble(double t) throws X {
            return operator.applyAsDouble(t);
        }
    }

    private static final class CheckedFunctionWrapper<T, R, X extends Exception> implements CheckedFunction<T, R, X> {

        private final CheckedFunction<T, R, X> function;

        private CheckedFunctionWrapper(CheckedFunction<T, R, X> function) {
            this.function = function;
        }

        @Override
        public R apply(T t) throws X {
            return function.apply(t);
        }
    }

    private static final class CheckedIntBinaryOperatorWrapper<X extends Exception> implements CheckedIntBinaryOperator<X> {

        private final CheckedIntBinaryOperator<X> operator;

        private CheckedIntBinaryOperatorWrapper(CheckedIntBinaryOperator<X> operator) {
            this.operator = operator;
        }

        @Override
        public int applyAsInt(int t, int u) throws X {
            return operator.applyAsInt(t, u);
        }
    }

    private static final class CheckedIntConsumerWrapper<X extends Exception> implements CheckedIntConsumer<X> {

        private final CheckedIntConsumer<X> consumer;

        private CheckedIntConsumerWrapper(CheckedIntConsumer<X> consumer) {
            this.consumer = consumer;
        }

        @Override
        public void accept(int t) throws X {
            consumer.accept(t);
        }
    }

    private static final class CheckedIntFunctionWrapper<R, X extends Exception> implements CheckedIntFunction<R, X> {

        private final CheckedIntFunction<R, X> function;

        private CheckedIntFunctionWrapper(CheckedIntFunction<R, X> function) {
            this.function = function;
        }

        @Override
        public R apply(int t) throws X {
            return function.apply(t);
        }
    }

    private static final class CheckedIntPredicateWrapper<X extends Exception> implements CheckedIntPredicate<X> {

        private final CheckedIntPredicate<X> predicate;

        private CheckedIntPredicateWrapper(CheckedIntPredicate<X> predicate) {
            this.predicate = predicate;
        }

        @Override
        public boolean test(int t) throws X {
            return predicate.test(t);
        }
    }

    private static final class CheckedIntSupplierWrapper<X extends Exception> implements CheckedIntSupplier<X> {

        private final CheckedIntSupplier<X> supplier;

        private CheckedIntSupplierWrapper(CheckedIntSupplier<X> supplier) {
            this.supplier = supplier;
        }

        @Override
        public int getAsInt() throws X {
            return supplier.getAsInt();
        }
    }

    private static final class CheckedIntUnaryOperatorWrapper<X extends Exception> implements CheckedIntUnaryOperator<X> {

        private final CheckedIntUnaryOperator<X> operator;

        private CheckedIntUnaryOperatorWrapper(CheckedIntUnaryOperator<X> operator) {
            this.operator = operator;
        }

        @Override
        public int applyAsInt(int t) throws X {
            return operator.applyAsInt(t);
        }
    }

    private static final class CheckedLongBinaryOperatorWrapper<X extends Exception> implements CheckedLongBinaryOperator<X> {

        private final CheckedLongBinaryOperator<X> operator;

        private CheckedLongBinaryOperatorWrapper(CheckedLongBinaryOperator<X> operator) {
            this.operator = operator;
        }

        @Override
        public long applyAsLong(long t, long u) throws X {
            return operator.applyAsLong(t, u);
        }
    }

    private static final class CheckedLongConsumerWrapper<X extends Exception> implements CheckedLongConsumer<X> {

        private final CheckedLongConsumer<X> consumer;

        private CheckedLongConsumerWrapper(CheckedLongConsumer<X> consumer) {
            this.consumer = consumer;
        }

        @Override
        public void accept(long t) throws X {
            consumer.accept(t);
        }
    }

    private static final class CheckedLongFunctionWrapper<R, X extends Exception> implements CheckedLongFunction<R, X> {

        private final CheckedLongFunction<R, X> function;

        private CheckedLongFunctionWrapper(CheckedLongFunction<R, X> function) {
            this.function = function;
        }

        @Override
        public R apply(long t) throws X {
            return function.apply(t);
        }
    }

    private static final class CheckedLongPredicateWrapper<X extends Exception> implements CheckedLongPredicate<X> {

        private final CheckedLongPredicate<X> predicate;

        private CheckedLongPredicateWrapper(CheckedLongPredicate<X> predicate) {
            this.predicate = predicate;
        }

        @Override
        public boolean test(long t) throws X {
            return predicate.test(t);
        }
    }

    private static final class CheckedLongSupplierWrapper<X extends Exception> implements CheckedLongSupplier<X> {

        private final CheckedLongSupplier<X> supplier;

        private CheckedLongSupplierWrapper(CheckedLongSupplier<X> supplier) {
            this.supplier = supplier;
        }

        @Override
        public long getAsLong() throws X {
            return supplier.getAsLong();
        }
    }

    private static final class CheckedLongUnaryOperatorWrapper<X extends Exception> implements CheckedLongUnaryOperator<X> {

        private final CheckedLongUnaryOperator<X> operator;

        private CheckedLongUnaryOperatorWrapper(CheckedLongUnaryOperator<X> operator) {
            this.operator = operator;
        }

        @Override
        public long applyAsLong(long t) throws X {
            return operator.applyAsLong(t);
        }
    }

    private static final class CheckedObjDoubleConsumerWrapper<T, X extends Exception> implements CheckedObjDoubleConsumer<T, X> {

        private final CheckedObjDoubleConsumer<T, X> consumer;

        private CheckedObjDoubleConsumerWrapper(CheckedObjDoubleConsumer<T, X> consumer) {
            this.consumer = consumer;
        }

        @Override
        public void accept(T t, double u) throws X {
            consumer.accept(t, u);
        }
    }

    private static final class CheckedObjIntConsumerWrapper<T, X extends Exception> implements CheckedObjIntConsumer<T, X> {

        private final CheckedObjIntConsumer<T, X> consumer;

        private CheckedObjIntConsumerWrapper(CheckedObjIntConsumer<T, X> consumer) {
            this.consumer = consumer;
        }

        @Override
        public void accept(T t, int u) throws X {
            consumer.accept(t, u);
        }
    }

    private static final class CheckedObjLongConsumerWrapper<T, X extends Exception> implements CheckedObjLongConsumer<T, X> {

        private final CheckedObjLongConsumer<T, X> consumer;

        private CheckedObjLongConsumerWrapper(CheckedObjLongConsumer<T, X> consumer) {
            this.consumer = consumer;
        }

        @Override
        public void accept(T t, long u) throws X {
            consumer.accept(t, u);
        }
    }

    private static final class CheckedPredicateWrapper<T, X extends Exception> implements CheckedPredicate<T, X> {

        private final CheckedPredicate<T, X> predicate;

        private CheckedPredicateWrapper(CheckedPredicate<T, X> predicate) {
            this.predicate = predicate;
        }

        @Override
        public boolean test(T t) throws X {
            return predicate.test(t);
        }
    }

    private static final class CheckedRunnableWrapper<X extends Exception> implements CheckedRunnable<X> {

        private final CheckedRunnable<X> runnable;

        private CheckedRunnableWrapper(CheckedRunnable<X> runnable) {
            this.runnable = runnable;
        }

        @Override
        public void run() throws X {
            runnable.run();
        }
    }

    private static final class CheckedSupplierWrapper<T, X extends Exception> implements CheckedSupplier<T, X> {

        private final CheckedSupplier<T, X> supplier;

        private CheckedSupplierWrapper(CheckedSupplier<T, X> supplier) {
            this.supplier = supplier;
        }

        @Override
        public T get() throws X {
            return supplier.get();
        }
    }

    private static final class CheckedToDoubleBiFunctionWrapper<T, U, X extends Exception> implements CheckedToDoubleBiFunction<T, U, X> {

        private final CheckedToDoubleBiFunction<T, U, X> function;

        private CheckedToDoubleBiFunctionWrapper(CheckedToDoubleBiFunction<T, U, X> function) {
            this.function = function;
        }

        @Override
        public double applyAsDouble(T t, U u) throws X {
            return function.applyAsDouble(t, u);
        }
    }

    private static final class CheckedToDoubleFunctionWrapper<T, X extends Exception> implements CheckedToDoubleFunction<T, X> {

        private final CheckedToDoubleFunction<T, X> function;

        private CheckedToDoubleFunctionWrapper(CheckedToDoubleFunction<T, X> function) {
            this.function = function;
        }

        @Override
        public double applyAsDouble(T value) throws X {
            return function.applyAsDouble(value);
        }
    }

    private static final class CheckedToIntBiFunctionWrapper<T, U, X extends Exception> implements CheckedToIntBiFunction<T, U, X> {

        private final CheckedToIntBiFunction<T, U, X> function;

        private CheckedToIntBiFunctionWrapper(CheckedToIntBiFunction<T, U, X> function) {
            this.function = function;
        }

        @Override
        public int applyAsInt(T t, U u) throws X {
            return function.applyAsInt(t, u);
        }
    }

    private static final class CheckedToIntFunctionWrapper<T, X extends Exception> implements CheckedToIntFunction<T, X> {

        private final CheckedToIntFunction<T, X> function;

        private CheckedToIntFunctionWrapper(CheckedToIntFunction<T, X> function) {
            this.function = function;
        }

        @Override
        public int applyAsInt(T value) throws X {
            return function.applyAsInt(value);
        }
    }

    private static final class CheckedToLongBiFunctionWrapper<T, U, X extends Exception> implements CheckedToLongBiFunction<T, U, X> {

        private final CheckedToLongBiFunction<T, U, X> function;

        private CheckedToLongBiFunctionWrapper(CheckedToLongBiFunction<T, U, X> function) {
            this.function = function;
        }

        @Override
        public long applyAsLong(T t, U u) throws X {
            return function.applyAsLong(t, u);
        }
    }

    private static final class CheckedToLongFunctionWrapper<T, X extends Exception> implements CheckedToLongFunction<T, X> {

        private final CheckedToLongFunction<T, X> function;

        private CheckedToLongFunctionWrapper(CheckedToLongFunction<T, X> function) {
            this.function = function;
        }

        @Override
        public long applyAsLong(T value) throws X {
            return function.applyAsLong(value);
        }
    }

    private static final class CheckedUnaryOperatorWrapper<T, X extends Exception> implements CheckedUnaryOperator<T, X> {

        private final CheckedUnaryOperator<T, X> operator;

        private CheckedUnaryOperatorWrapper(CheckedUnaryOperator<T, X> operator) {
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
