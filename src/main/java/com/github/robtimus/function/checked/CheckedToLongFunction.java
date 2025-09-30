/*
 * CheckedToLongFunction.java
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
import java.util.function.Function;
import java.util.function.LongSupplier;
import java.util.function.ToLongFunction;

/**
 * Represents a function that produces a {@code long}-valued result.
 * This is a checked-exception throwing equivalent of {@link ToLongFunction}.
 *
 * @param <T> The type of the input to the function.
 * @param <X> The type of checked exception that can be thrown.
 */
@FunctionalInterface
@SuppressWarnings("squid:S1181") // Error needs to be caught separately (and re-thrown) to not let it be caught as throwable
public interface CheckedToLongFunction<T, X extends Throwable> {

    /**
     * Applies this function to the given argument.
     *
     * @param value The function argument.
     * @return The function result.
     * @throws X If an error occurs.
     */
    long applyAsLong(T value) throws X;

    /**
     * Returns a function that applies this function to its input. Any checked exception thrown by this function is transformed using the given error
     * mapper, and the returned function throws the transformation result.
     *
     * @param <E> The type of checked exception to transform to.
     * @param errorMapper The function to use to transform any checked exception thrown by this function.
     * @return A function that transforms any thrown checked exception.
     * @throws NullPointerException If {@code errorMapper} is {@code null}.
     */
    default <E extends Throwable> CheckedToLongFunction<T, E> onErrorThrowAsChecked(Function<? super X, ? extends E> errorMapper) {
        Objects.requireNonNull(errorMapper);
        return t -> {
            try {
                return applyAsLong(t);
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
    default <E extends RuntimeException> ToLongFunction<T> onErrorThrowAsUnchecked(Function<? super X, ? extends E> errorMapper) {
        Objects.requireNonNull(errorMapper);
        return t -> {
            try {
                return applyAsLong(t);
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
    default <E extends Throwable> CheckedToLongFunction<T, E> onErrorHandleChecked(CheckedToLongFunction<? super X, ? extends E> errorHandler) {
        Objects.requireNonNull(errorHandler);
        return t -> {
            try {
                return applyAsLong(t);
            } catch (Error | RuntimeException e) {
                throw e;
            } catch (Throwable throwable) {
                // This cast is safe, because only Error, RuntimeException (both handled above) and X can be thrown
                @SuppressWarnings("unchecked")
                X x = (X) throwable;
                return errorHandler.applyAsLong(x);
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
    default ToLongFunction<T> onErrorHandleUnchecked(ToLongFunction<? super X> errorHandler) {
        Objects.requireNonNull(errorHandler);
        return t -> {
            try {
                return applyAsLong(t);
            } catch (Error | RuntimeException e) {
                throw e;
            } catch (Throwable throwable) {
                // This cast is safe, because only Error, RuntimeException (both handled above) and X can be thrown
                @SuppressWarnings("unchecked")
                X x = (X) throwable;
                return errorHandler.applyAsLong(x);
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
    default <E extends Throwable> CheckedToLongFunction<T, E> onErrorApplyChecked(CheckedToLongFunction<? super T, ? extends E> fallback) {
        Objects.requireNonNull(fallback);
        return t -> {
            try {
                return applyAsLong(t);
            } catch (Error | RuntimeException e) {
                throw e;
            } catch (@SuppressWarnings("unused") Throwable throwable) {
                return fallback.applyAsLong(t);
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
    default ToLongFunction<T> onErrorApplyUnchecked(ToLongFunction<? super T> fallback) {
        Objects.requireNonNull(fallback);
        return t -> {
            try {
                return applyAsLong(t);
            } catch (Error | RuntimeException e) {
                throw e;
            } catch (@SuppressWarnings("unused") Throwable throwable) {
                return fallback.applyAsLong(t);
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
    default <E extends Throwable> CheckedToLongFunction<T, E> onErrorGetChecked(CheckedLongSupplier<? extends E> fallback) {
        Objects.requireNonNull(fallback);
        return t -> {
            try {
                return applyAsLong(t);
            } catch (Error | RuntimeException e) {
                throw e;
            } catch (@SuppressWarnings("unused") Throwable throwable) {
                return fallback.getAsLong();
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
    default ToLongFunction<T> onErrorGetUnchecked(LongSupplier fallback) {
        Objects.requireNonNull(fallback);
        return t -> {
            try {
                return applyAsLong(t);
            } catch (Error | RuntimeException e) {
                throw e;
            } catch (@SuppressWarnings("unused") Throwable throwable) {
                return fallback.getAsLong();
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
    default ToLongFunction<T> onErrorReturn(long fallback) {
        return t -> {
            try {
                return applyAsLong(t);
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
    default ToLongFunction<T> unchecked() {
        return onErrorThrowAsUnchecked(UncheckedException::new);
    }

    /**
     * Factory method for turning {@code CheckedToLongFunction}-shaped lambdas into {@code CheckedToLongFunctions}.
     *
     * @param <T> The type of the input to the function.
     * @param <X> The type of checked exception that can be thrown.
     * @param function The lambda to return as {@code CheckedToLongFunction}.
     * @return The given lambda as a {@code CheckedToLongFunction}.
     * @throws NullPointerException If {@code function} is {@code null}.
     */
    static <T, X extends Throwable> CheckedToLongFunction<T, X> of(CheckedToLongFunction<T, X> function) {
        Objects.requireNonNull(function);
        return function;
    }

    /**
     * Returns a function that applies the {@code function} function to its input. Any checked exception thrown by the {@code function} function is
     * wrapped in an {@link UncheckedException}.
     *
     * @param <T> The type of the input to the function.
     * @param function The function to apply when the returned function is applied.
     * @return A function that wraps any checked exception in an {@link UncheckedException}.
     * @throws NullPointerException If {@code function} is {@code null}.
     */
    @SuppressWarnings("unchecked")
    static <T> ToLongFunction<T> unchecked(CheckedToLongFunction<? super T, ?> function) {
        Objects.requireNonNull(function);
        return (ToLongFunction<T>) function.unchecked();
    }

    /**
     * Returns a function that applies the {@code function} function to its input. Any unchecked exception thrown by the {@code function} function is
     * relayed to the caller. This method allows existing {@link ToLongFunction} instances to be used where {@code CheckedToLongFunction} is expected.
     *
     * @param <T> The type of the input to the function.
     * @param <X> The type of checked exception that can be thrown.
     * @param function The function to apply when the returned function is applied.
     * @return A function that wraps any checked exception in an {@link UncheckedException}.
     * @throws NullPointerException If {@code function} is {@code null}.
     */
    static <T, X extends Throwable> CheckedToLongFunction<T, X> checked(ToLongFunction<? super T> function) {
        Objects.requireNonNull(function);
        return function::applyAsLong;
    }

    /**
     * Returns a function that applies the {@code function} function to its input. Any {@link UncheckedException} thrown by the {@code function}
     * function is unwrapped if its cause is an instance of {@code errorType}, otherwise it is relayed to the caller.
     *
     * @param <T> The type of the input to the function.
     * @param <X> The type of checked exception that can be thrown.
     * @param function The function to apply when the returned function is applied.
     * @param errorType The type of checked exception that can be thrown.
     * @return A function that wraps any checked exception in an {@link UncheckedException}.
     * @throws NullPointerException If {@code function} or {@code errorType} is {@code null}.
     */
    static <T, X extends Throwable> CheckedToLongFunction<T, X> checked(ToLongFunction<? super T> function, Class<X> errorType) {
        Objects.requireNonNull(function);
        Objects.requireNonNull(errorType);
        return t -> invokeAndUnwrap(function, t, errorType);
    }

    /**
     * Invokes a function, unwrapping any {@link UncheckedException} that is thrown if its cause if an instance of {@code errorType}.
     *
     * @param <T> The type of the input to the function.
     * @param <X> The type of checked exception that can be thrown.
     * @param function The function to invoke.
     * @param input The input to the function.
     * @param errorType The type of checked exception that can be thrown.
     * @return The result of invoking {@code function}.
     * @throws NullPointerException If {@code function} or {@code errorType} is {@code null}.
     * @throws X If {@code function} throws an {@link UncheckedException} that wraps an instance of {@code errorType}.
     */
    static <T, X extends Throwable> long invokeAndUnwrap(ToLongFunction<? super T> function, T input, Class<X> errorType) throws X {
        Objects.requireNonNull(errorType);
        try {
            return function.applyAsLong(input);
        } catch (UncheckedException e) {
            Throwable cause = e.getCause();
            if (errorType.isInstance(cause)) {
                throw errorType.cast(cause);
            }
            throw e;
        }
    }
}
