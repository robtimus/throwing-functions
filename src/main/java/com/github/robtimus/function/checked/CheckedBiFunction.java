/*
 * CheckedBiFunction.java
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

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Represents a function that accepts two arguments and produces a result.
 * This is a checked-exception throwing equivalent of {@link BiFunction}.
 *
 * @param <T> The type of the first argument to the function.
 * @param <U> The type of the second argument to the function.
 * @param <R> The type of the result of the function.
 * @param <X> The type of checked exception that can be thrown.
 */
@FunctionalInterface
@SuppressWarnings("squid:S1181") // Error needs to be caught separately (and re-thrown) to not let it be caught as throwable
public interface CheckedBiFunction<T, U, R, X extends Throwable> {

    /**
     * Applies this function to the given arguments.
     *
     * @param t The first function argument.
     * @param u The second function argument.
     * @return The function result.
     * @throws X If an error occurs.
     */
    R apply(T t, U u) throws X;

    /**
     * Returns a composed function that first applies this function to its input, and then applies the {@code after} function to the result. If
     * evaluation of either function throws an exception, it is relayed to the caller of the composed function.
     *
     * @param <V> The type of output of the {@code after} function, and of the composed function.
     * @param after The function to apply after this function is applied.
     * @return A composed function that first applies this function and then applies the {@code after} function.
     * @throws NullPointerException If {@code after} is {@code null}.
     */
    default <V> CheckedBiFunction<T, U, V, X> andThen(CheckedFunction<? super R, ? extends V, ? extends X> after) {
        Objects.requireNonNull(after);
        return (t, u) -> after.apply(apply(t, u));
    }

    /**
     * Returns a function that applies this function to its input. Any checked exception thrown by this function is transformed using the given error
     * mapper, and the returned function throws the transformation result.
     *
     * @param <E> The type of checked exception to transform to.
     * @param errorMapper The function to use to transform any checked exception thrown by this function.
     * @return A function that transforms any thrown checked exception.
     * @throws NullPointerException If {@code errorMapper} is {@code null}.
     */
    default <E extends Throwable> CheckedBiFunction<T, U, R, E> onErrorThrowAsChecked(Function<? super X, ? extends E> errorMapper) {
        Objects.requireNonNull(errorMapper);
        return (t, u) -> {
            try {
                return apply(t, u);
            } catch (Error | RuntimeException e) {
                throw e;
            } catch (Throwable throwable) {
                // This cast is safe, because only Error, RuntimeException (both handled above) and X can be thrown
                @SuppressWarnings("unchecked")
                X x = (X) throwable;
                throw errorMapper.apply(x);
            }
        };
    }

    /**
     * Returns a function that applies this function to its input. Any checked exception thrown by this function is transformed using the given error
     * mapper, and the returned function throws the transformation result.
     *
     * @param <E> The type of unchecked exception to transform to.
     * @param errorMapper The function to use to transform any checked exception thrown by this function.
     * @return A function that transforms any thrown checked exception.
     * @throws NullPointerException If {@code errorMapper} is {@code null}.
     */
    default <E extends RuntimeException> BiFunction<T, U, R> onErrorThrowAsUnchecked(Function<? super X, ? extends E> errorMapper) {
        Objects.requireNonNull(errorMapper);
        return (t, u) -> {
            try {
                return apply(t, u);
            } catch (Error | RuntimeException e) {
                throw e;
            } catch (Throwable throwable) {
                // This cast is safe, because only Error, RuntimeException (both handled above) and X can be thrown
                @SuppressWarnings("unchecked")
                X x = (X) throwable;
                throw errorMapper.apply(x);
            }
        };
    }

    /**
     * Returns a function that applies this function to its input. Any checked exception thrown by this function is transformed using the given error
     * handler, and the returned function returns the transformation result.
     *
     * @param <E> The type of checked exception that can be thrown by the given error handler.
     * @param errorHandler The function to use to transform any checked exception thrown by this function.
     * @return A function that transforms any thrown checked exception.
     * @throws NullPointerException If {@code errorHandler} is {@code null}.
     */
    default <E extends Throwable> CheckedBiFunction<T, U, R, E> onErrorHandleChecked(
            CheckedFunction<? super X, ? extends R, ? extends E> errorHandler) {

        Objects.requireNonNull(errorHandler);
        return (t, u) -> {
            try {
                return apply(t, u);
            } catch (Error | RuntimeException e) {
                throw e;
            } catch (Throwable throwable) {
                // This cast is safe, because only Error, RuntimeException (both handled above) and X can be thrown
                @SuppressWarnings("unchecked")
                X x = (X) throwable;
                return errorHandler.apply(x);
            }
        };
    }

    /**
     * Returns a function that applies this function to its input. Any checked exception thrown by this function is transformed using the given error
     * handler, and the returned function returns the transformation result.
     *
     * @param errorHandler The function to use to transform any checked exception thrown by this function.
     * @return A function that transforms any thrown checked exception.
     * @throws NullPointerException If {@code errorHandler} is {@code null}.
     */
    default BiFunction<T, U, R> onErrorHandleUnchecked(Function<? super X, ? extends R> errorHandler) {
        Objects.requireNonNull(errorHandler);
        return (t, u) -> {
            try {
                return apply(t, u);
            } catch (Error | RuntimeException e) {
                throw e;
            } catch (Throwable throwable) {
                // This cast is safe, because only Error, RuntimeException (both handled above) and X can be thrown
                @SuppressWarnings("unchecked")
                X x = (X) throwable;
                return errorHandler.apply(x);
            }
        };
    }

    /**
     * Returns a function that applies this function to its input. If this function throws any checked exception, it is discarded and the given
     * fallback function is invoked.
     *
     * @param <E> The type of checked exception that can be thrown by the given fallback function.
     * @param fallback The function to invoke if this function throws any checked exception.
     * @return A function that invokes the {@code fallback} function if this function throws any checked exception.
     * @throws NullPointerException If {@code fallback} is {@code null}.
     */
    default <E extends Throwable> CheckedBiFunction<T, U, R, E> onErrorApplyChecked(
            CheckedBiFunction<? super T, ? super U, ? extends R, ? extends E> fallback) {

        Objects.requireNonNull(fallback);
        return (t, u) -> {
            try {
                return apply(t, u);
            } catch (Error | RuntimeException e) {
                throw e;
            } catch (@SuppressWarnings("unused") Throwable throwable) {
                return fallback.apply(t, u);
            }
        };
    }

    /**
     * Returns a function that applies this function to its input. If this function throws any checked exception, it is discarded and the given
     * fallback function is invoked.
     *
     * @param fallback The function to invoke if this function throws any checked exception.
     * @return A function that invokes the {@code fallback} function if this function throws any checked exception.
     * @throws NullPointerException If {@code fallback} is {@code null}.
     */
    default BiFunction<T, U, R> onErrorApplyUnchecked(BiFunction<? super T, ? super U, ? extends R> fallback) {
        Objects.requireNonNull(fallback);
        return (t, u) -> {
            try {
                return apply(t, u);
            } catch (Error | RuntimeException e) {
                throw e;
            } catch (@SuppressWarnings("unused") Throwable throwable) {
                return fallback.apply(t, u);
            }
        };
    }

    /**
     * Returns a function that applies this function to its input. If this function throws any checked exception, it is discarded and the given
     * fallback supplier is invoked.
     *
     * @param <E> The type of checked exception that can be thrown by the given fallback supplier.
     * @param fallback The supplier to produce the value to return if this function throws any checked exception.
     * @return A function that invokes the {@code fallback} supplier if this function throws any checked exception.
     * @throws NullPointerException If {@code fallback} is {@code null}.
     */
    default <E extends Throwable> CheckedBiFunction<T, U, R, E> onErrorGetChecked(CheckedSupplier<? extends R, ? extends E> fallback) {
        Objects.requireNonNull(fallback);
        return (t, u) -> {
            try {
                return apply(t, u);
            } catch (Error | RuntimeException e) {
                throw e;
            } catch (@SuppressWarnings("unused") Throwable throwable) {
                return fallback.get();
            }
        };
    }

    /**
     * Returns a function that applies this function to its input. If this function throws any checked exception, it is discarded and the given
     * fallback supplier is invoked.
     *
     * @param fallback The supplier to produce the value to return if this function throws any checked exception.
     * @return A function that invokes the {@code fallback} supplier if this function throws any checked exception.
     * @throws NullPointerException If {@code fallback} is {@code null}.
     */
    default BiFunction<T, U, R> onErrorGetUnchecked(Supplier<? extends R> fallback) {
        Objects.requireNonNull(fallback);
        return (t, u) -> {
            try {
                return apply(t, u);
            } catch (Error | RuntimeException e) {
                throw e;
            } catch (@SuppressWarnings("unused") Throwable throwable) {
                return fallback.get();
            }
        };
    }

    /**
     * Returns a function that applies this function to its input. If this function throws any checked exception, it is discarded and the given
     * fallback value is returned.
     *
     * @param fallback The value to return if this function throws any checked exception.
     * @return A function that returns the {@code fallback} value if this function throws any checked exception.
     */
    default BiFunction<T, U, R> onErrorReturn(R fallback) {
        return (t, u) -> {
            try {
                return apply(t, u);
            } catch (Error | RuntimeException e) {
                throw e;
            } catch (@SuppressWarnings("unused") Throwable throwable) {
                return fallback;
            }
        };
    }

    /**
     * Returns a function that applies this function to its input. Any checked exception thrown by this function is wrapped in an
     * {@link UncheckedException}.
     *
     * @return A function that wraps any checked exception in an {@link UncheckedException}.
     */
    default BiFunction<T, U, R> unchecked() {
        return onErrorThrowAsUnchecked(UncheckedException::new);
    }

    /**
     * Factory method for turning {@code CheckedBiFunction}-shaped lambdas into {@code CheckedBiFunctions}.
     *
     * @param <T> The type of the first argument to the function.
     * @param <U> The type of the second argument to the function.
     * @param <R> The type of the result of the function.
     * @param <X> The type of checked exception that can be thrown.
     * @param function The lambda to return as {@code CheckedBiFunction}.
     * @return The given lambda as a {@code CheckedBiFunction}.
     * @throws NullPointerException If {@code function} is {@code null}.
     */
    static <T, U, R, X extends Throwable> CheckedBiFunction<T, U, R, X> of(CheckedBiFunction<T, U, R, X> function) {
        Objects.requireNonNull(function);
        return function;
    }

    /**
     * Returns a function that applies the {@code function} function to its input. Any checked exception thrown by the {@code function} function is
     * wrapped in an {@link UncheckedException}.
     *
     * @param <T> The type of the first argument to the function.
     * @param <U> The type of the second argument to the function.
     * @param <R> The type of the result of the function.
     * @param function The function to apply when the returned function is applied.
     * @return A function that wraps any checked exception in an {@link UncheckedException}.
     * @throws NullPointerException If {@code function} is {@code null}.
     */
    @SuppressWarnings("unchecked")
    static <T, U, R> BiFunction<T, U, R> unchecked(CheckedBiFunction<? super T, ? super U, ? extends R, ?> function) {
        Objects.requireNonNull(function);
        return (BiFunction<T, U, R>) function.unchecked();
    }

    /**
     * Returns a function that applies the {@code function} function to its input. Any unchecked exception thrown by the {@code function} function is
     * relayed to the caller. This method allows existing {@link BiFunction} instances to be used where {@code CheckedBiFunction} is expected.
     *
     * @param <T> The type of the first argument to the function.
     * @param <U> The type of the second argument to the function.
     * @param <R> The type of the result of the function.
     * @param <X> The type of checked exception that can be thrown.
     * @param function The function to apply when the returned function is applied.
     * @return A function that wraps any checked exception in an {@link UncheckedException}.
     * @throws NullPointerException If {@code function} is {@code null}.
     */
    static <T, U, R, X extends Throwable> CheckedBiFunction<T, U, R, X> checked(BiFunction<? super T, ? super U, ? extends R> function) {
        Objects.requireNonNull(function);
        return function::apply;
    }

    /**
     * Returns a function that applies the {@code function} function to its input. Any {@link UncheckedException} thrown by the {@code function}
     * function is unwrapped if its cause is an instance of {@code errorType}, otherwise it is relayed to the caller.
     *
     * @param <T> The type of the first argument to the function.
     * @param <U> The type of the second argument to the function.
     * @param <R> The type of the result of the function.
     * @param <X> The type of checked exception that can be thrown.
     * @param function The function to apply when the returned function is applied.
     * @param errorType The type of checked exception that can be thrown.
     * @return A function that wraps any checked exception in an {@link UncheckedException}.
     * @throws NullPointerException If {@code function} or {@code errorType} is {@code null}.
     */
    static <T, U, R, X extends Throwable> CheckedBiFunction<T, U, R, X> checked(BiFunction<? super T, ? super U, ? extends R> function,
                                                                                Class<X> errorType) {
        Objects.requireNonNull(function);
        Objects.requireNonNull(errorType);
        return (t, u) -> invokeAndUnwrap(function, t, u, errorType);
    }

    /**
     * Invokes a function, unwrapping any {@link UncheckedException} that is thrown if its cause if an instance of {@code errorType}.
     *
     * @param <T> The type of the first argument to the function.
     * @param <U> The type of the second argument to the function.
     * @param <R> The type of the result of the function.
     * @param <X> The type of checked exception that can be thrown.
     * @param function The function to invoke.
     * @param input1 The first input to the function.
     * @param input2 The second input to the function.
     * @param errorType The type of checked exception that can be thrown.
     * @return The result of invoking {@code function}.
     * @throws NullPointerException If {@code function} or {@code errorType} is {@code null}.
     * @throws X If {@code function} throws an {@link UncheckedException} that wraps an instance of {@code errorType}.
     */
    static <T, U, R, X extends Throwable> R invokeAndUnwrap(BiFunction<? super T, ? super U, ? extends R> function, T input1, U input2,
                                                            Class<X> errorType) throws X {
        Objects.requireNonNull(errorType);
        try {
            return function.apply(input1, input2);
        } catch (UncheckedException e) {
            Throwable cause = e.getCause();
            if (errorType.isInstance(cause)) {
                throw errorType.cast(cause);
            }
            throw e;
        }
    }
}
