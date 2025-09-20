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
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
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

    static <T, R, X extends Exception> CheckedFunction<T, R, X> checkedFunction(CheckedFunction<T, R, X> function) {
        return spy(new CheckedFunctionWrapper<>(function));
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

    static <T, R> Function<T, R> function(Function<T, R> function) {
        return spy(new FunctionWrapper<>(function));
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

    static <T> UnaryOperator<T> unaryOperator(UnaryOperator<T> operator) {
        return spy(new UnaryOperatorWrapper<>(operator));
    }

    private static final class CheckedBiConsumerWrapper<T, U, X extends Exception> implements CheckedBiConsumer<T, U, X> {

        private CheckedBiConsumer<T, U, X> consumer;

        private CheckedBiConsumerWrapper(CheckedBiConsumer<T, U, X> consumer) {
            this.consumer = consumer;
        }

        @Override
        public void accept(T t, U u) throws X {
            consumer.accept(t, u);
        }
    }

    private static final class CheckedBiFunctionWrapper<T, U, R, X extends Exception> implements CheckedBiFunction<T, U, R, X> {

        private CheckedBiFunction<T, U, R, X> function;

        private CheckedBiFunctionWrapper(CheckedBiFunction<T, U, R, X> function) {
            this.function = function;
        }

        @Override
        public R apply(T t, U u) throws X {
            return function.apply(t, u);
        }
    }

    private static final class CheckedBinaryOperatorWrapper<T, X extends Exception> implements CheckedBinaryOperator<T, X> {

        private CheckedBinaryOperator<T, X> operator;

        private CheckedBinaryOperatorWrapper(CheckedBinaryOperator<T, X> operator) {
            this.operator = operator;
        }

        @Override
        public T apply(T t, T u) throws X {
            return operator.apply(t, u);
        }
    }

    private static final class CheckedBiPredicateWrapper<T, U, X extends Exception> implements CheckedBiPredicate<T, U, X> {

        private CheckedBiPredicate<T, U, X> predicate;

        private CheckedBiPredicateWrapper(CheckedBiPredicate<T, U, X> predicate) {
            this.predicate = predicate;
        }

        @Override
        public boolean test(T t, U u) throws X {
            return predicate.test(t, u);
        }
    }

    private static final class CheckedBooleanSupplierWrapper<X extends Exception> implements CheckedBooleanSupplier<X> {

        private CheckedBooleanSupplier<X> supplier;

        private CheckedBooleanSupplierWrapper(CheckedBooleanSupplier<X> supplier) {
            this.supplier = supplier;
        }

        @Override
        public boolean getAsBoolean() throws X {
            return supplier.getAsBoolean();
        }
    }

    private static final class CheckedConsumerWrapper<T, X extends Exception> implements CheckedConsumer<T, X> {

        private CheckedConsumer<T, X> consumer;

        private CheckedConsumerWrapper(CheckedConsumer<T, X> consumer) {
            this.consumer = consumer;
        }

        @Override
        public void accept(T t) throws X {
            consumer.accept(t);
        }
    }

    private static final class CheckedFunctionWrapper<T, R, X extends Exception> implements CheckedFunction<T, R, X> {

        private CheckedFunction<T, R, X> function;

        private CheckedFunctionWrapper(CheckedFunction<T, R, X> function) {
            this.function = function;
        }

        @Override
        public R apply(T t) throws X {
            return function.apply(t);
        }
    }

    private static final class CheckedPredicateWrapper<T, X extends Exception> implements CheckedPredicate<T, X> {

        private CheckedPredicate<T, X> predicate;

        private CheckedPredicateWrapper(CheckedPredicate<T, X> predicate) {
            this.predicate = predicate;
        }

        @Override
        public boolean test(T t) throws X {
            return predicate.test(t);
        }
    }

    private static final class CheckedRunnableWrapper<X extends Exception> implements CheckedRunnable<X> {

        private CheckedRunnable<X> runnable;

        private CheckedRunnableWrapper(CheckedRunnable<X> runnable) {
            this.runnable = runnable;
        }

        @Override
        public void run() throws X {
            runnable.run();
        }
    }

    private static final class CheckedSupplierWrapper<T, X extends Exception> implements CheckedSupplier<T, X> {

        private CheckedSupplier<T, X> supplier;

        private CheckedSupplierWrapper(CheckedSupplier<T, X> supplier) {
            this.supplier = supplier;
        }

        @Override
        public T get() throws X {
            return supplier.get();
        }
    }

    private static final class CheckedUnaryOperatorWrapper<T, X extends Exception> implements CheckedUnaryOperator<T, X> {

        private CheckedUnaryOperator<T, X> operator;

        private CheckedUnaryOperatorWrapper(CheckedUnaryOperator<T, X> operator) {
            this.operator = operator;
        }

        @Override
        public T apply(T t) throws X {
            return operator.apply(t);
        }
    }

    private static final class BiConsumerWrapper<T, U> implements BiConsumer<T, U> {

        private BiConsumer<T, U> consumer;

        private BiConsumerWrapper(BiConsumer<T, U> consumer) {
            this.consumer = consumer;
        }

        @Override
        public void accept(T t, U u) {
            consumer.accept(t, u);
        }
    }

    private static final class BiFunctionWrapper<T, U, R> implements BiFunction<T, U, R> {

        private BiFunction<T, U, R> function;

        private BiFunctionWrapper(BiFunction<T, U, R> function) {
            this.function = function;
        }

        @Override
        public R apply(T t, U u) {
            return function.apply(t, u);
        }
    }

    private static final class BinaryOperatorWrapper<T> implements BinaryOperator<T> {

        private BinaryOperator<T> operator;

        private BinaryOperatorWrapper(BinaryOperator<T> operator) {
            this.operator = operator;
        }

        @Override
        public T apply(T t, T u) {
            return operator.apply(t, u);
        }
    }

    private static final class BiPredicateWrapper<T, U> implements BiPredicate<T, U> {

        private BiPredicate<T, U> predicate;

        private BiPredicateWrapper(BiPredicate<T, U> predicate) {
            this.predicate = predicate;
        }

        @Override
        public boolean test(T t, U u) {
            return predicate.test(t, u);
        }
    }

    private static final class BooleanSupplierWrapper implements BooleanSupplier {

        private BooleanSupplier supplier;

        private BooleanSupplierWrapper(BooleanSupplier supplier) {
            this.supplier = supplier;
        }

        @Override
        public boolean getAsBoolean() {
            return supplier.getAsBoolean();
        }
    }

    private static final class ConsumerWrapper<T> implements Consumer<T> {

        private Consumer<T> consumer;

        private ConsumerWrapper(Consumer<T> consumer) {
            this.consumer = consumer;
        }

        @Override
        public void accept(T t) {
            consumer.accept(t);
        }
    }

    private static final class FunctionWrapper<T, R> implements Function<T, R> {

        private Function<T, R> function;

        private FunctionWrapper(Function<T, R> function) {
            this.function = function;
        }

        @Override
        public R apply(T t) {
            return function.apply(t);
        }
    }

    private static final class PredicateWrapper<T> implements Predicate<T> {

        private Predicate<T> predicate;

        private PredicateWrapper(Predicate<T> predicate) {
            this.predicate = predicate;
        }

        @Override
        public boolean test(T t) {
            return predicate.test(t);
        }
    }

    private static final class RunnableWrapper implements Runnable {

        private Runnable runnable;

        private RunnableWrapper(Runnable runnable) {
            this.runnable = runnable;
        }

        @Override
        public void run() {
            runnable.run();
        }
    }

    private static final class SupplierWrapper<T> implements Supplier<T> {

        private Supplier<T> supplier;

        private SupplierWrapper(Supplier<T> supplier) {
            this.supplier = supplier;
        }

        @Override
        public T get() {
            return supplier.get();
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
