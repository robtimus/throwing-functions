/*
 * ThrowingIntToLongFunction.java
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

import java.util.Objects;
import java.util.function.Function;
import java.util.function.IntToLongFunction;
import java.util.function.LongSupplier;
import java.util.function.ToLongFunction;

/**
 * Represents a function that accepts a {@code int}-valued argument and produces a {@code long}-valued result.
 * This is a checked-exception throwing equivalent of {@link IntToLongFunction}.
 *
 * @param <X> The type of checked exception that can be thrown.
 */
@FunctionalInterface
@SuppressWarnings("squid:S1181") // Error needs to be caught separately (and re-thrown) to not let it be caught as throwable
public interface ThrowingIntToLongFunction<X extends Throwable> {

    /**
     * Applies this function to the given argument.
     *
     * @param value The function argument.
     * @return The function result.
     * @throws X If an error occurs.
     */
    long applyAsLong(int value) throws X;

    /**
     * Returns a function that applies this function to its input. Any checked exception thrown by this function is transformed using the given error
     * mapper, and the returned function throws the transformation result.
     *
     * @param <E> The type of checked exception to transform to.
     * @param errorMapper The function to use to transform any checked exception thrown by this function.
     * @return A function that transforms any thrown checked exception.
     * @throws NullPointerException If {@code errorMapper} is {@code null}.
     */
    default <E extends Throwable> ThrowingIntToLongFunction<E> onErrorThrowAsChecked(Function<? super X, ? extends E> errorMapper) {
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
    default <E extends RuntimeException> IntToLongFunction onErrorThrowAsUnchecked(Function<? super X, ? extends E> errorMapper) {
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
    default <E extends Throwable> ThrowingIntToLongFunction<E> onErrorHandleChecked(ThrowingToLongFunction<? super X, ? extends E> errorHandler) {
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
    default IntToLongFunction onErrorHandleUnchecked(ToLongFunction<? super X> errorHandler) {
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
    default <E extends Throwable> ThrowingIntToLongFunction<E> onErrorApplyChecked(ThrowingIntToLongFunction<? extends E> fallback) {
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
    default IntToLongFunction onErrorApplyUnchecked(IntToLongFunction fallback) {
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
    default <E extends Throwable> ThrowingIntToLongFunction<E> onErrorGetChecked(ThrowingLongSupplier<? extends E> fallback) {
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
    default IntToLongFunction onErrorGetUnchecked(LongSupplier fallback) {
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
    default IntToLongFunction onErrorReturn(long fallback) {
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
     * {@link UncheckedException} {@linkplain UncheckedException#withoutStackTrace(Throwable) without a stack trace}.
     *
     * @return A function that wraps any checked exception in an {@link UncheckedException}.
     */
    default IntToLongFunction unchecked() {
        return onErrorThrowAsUnchecked(UncheckedException::withoutStackTrace);
    }

    /**
     * Factory method for turning {@code ThrowingIntToLongFunction}-shaped lambdas into {@code ThrowingIntToLongFunctions}.
     *
     * @param <X> The type of checked exception that can be thrown.
     * @param function The lambda to return as {@code ThrowingIntToLongFunction}.
     * @return The given lambda as a {@code ThrowingIntToLongFunction}.
     * @throws NullPointerException If {@code function} is {@code null}.
     */
    static <X extends Throwable> ThrowingIntToLongFunction<X> of(ThrowingIntToLongFunction<X> function) {
        Objects.requireNonNull(function);
        return function;
    }

    /**
     * Returns a function that applies the {@code function} function to its input. Any checked exception thrown by the {@code function} function is
     * wrapped in an {@link UncheckedException}.
     *
     * @param function The function to apply when the returned function is applied.
     * @return A function that wraps any checked exception in an {@link UncheckedException}.
     * @throws NullPointerException If {@code function} is {@code null}.
     */
    static IntToLongFunction unchecked(ThrowingIntToLongFunction<?> function) {
        Objects.requireNonNull(function);
        return function.unchecked();
    }

    /**
     * Returns a function that applies the {@code function} function to its input. Any unchecked exception thrown by the {@code function} function is
     * relayed to the caller. This method allows existing {@link IntToLongFunction} instances to be used where {@code ThrowingIntToLongFunction} is
     * expected.
     *
     * @param <X> The type of checked exception that can be thrown.
     * @param function The function to apply when the returned function is applied.
     * @return A function that wraps any checked exception in an {@link UncheckedException}.
     * @throws NullPointerException If {@code function} is {@code null}.
     */
    static <X extends Throwable> ThrowingIntToLongFunction<X> checked(IntToLongFunction function) {
        Objects.requireNonNull(function);
        return function::applyAsLong;
    }

    /**
     * Returns a function that applies the {@code function} function to its input. Any {@link UncheckedException} thrown by the {@code function}
     * function is unwrapped if its cause is an instance of {@code errorType}, otherwise it is relayed to the caller.
     *
     * @param <X> The type of checked exception that can be thrown.
     * @param function The function to apply when the returned function is applied.
     * @param errorType The type of checked exception that can be thrown.
     * @return A function that wraps any checked exception in an {@link UncheckedException}.
     * @throws NullPointerException If {@code function} or {@code errorType} is {@code null}.
     */
    static <X extends Throwable> ThrowingIntToLongFunction<X> checked(IntToLongFunction function, Class<X> errorType) {
        Objects.requireNonNull(function);
        Objects.requireNonNull(errorType);
        return t -> invokeAndUnwrap(function, t, errorType);
    }

    /**
     * Invokes a function, unwrapping any {@link UncheckedException} that is thrown if its cause if an instance of {@code errorType}.
     *
     * @param <X> The type of checked exception that can be thrown.
     * @param function The function to invoke.
     * @param input The input to the function.
     * @param errorType The type of checked exception that can be thrown.
     * @return The result of invoking {@code function}.
     * @throws NullPointerException If {@code function} or {@code errorType} is {@code null}.
     * @throws X If {@code function} throws an {@link UncheckedException} that wraps an instance of {@code errorType}.
     */
    static <X extends Throwable> long invokeAndUnwrap(IntToLongFunction function, int input, Class<X> errorType) throws X {
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
