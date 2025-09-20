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
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

final class Spied {

    private Spied() {
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
}
