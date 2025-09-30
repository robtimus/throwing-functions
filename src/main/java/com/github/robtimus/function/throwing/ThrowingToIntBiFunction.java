/*
 * ThrowingToIntBiFunction.java
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
import java.util.function.IntSupplier;
import java.util.function.ToIntBiFunction;
import java.util.function.ToIntFunction;

/**
 * Represents a function that accepts two arguments and produces an {@code int}-valued result.
 * This is a checked-exception throwing equivalent of {@link ToIntBiFunction}.
 *
 * @param <T> The type of the first argument to the function.
 * @param <U> The type of the second argument to the function.
 * @param <X> The type of checked exception that can be thrown.
 */
@FunctionalInterface
@SuppressWarnings("squid:S1181") // Error needs to be caught separately (and re-thrown) to not let it be caught as throwable
public interface ThrowingToIntBiFunction<T, U, X extends Throwable> {

    /**
     * Applies this function to the given arguments.
     *
     * @param t The first function argument.
     * @param u The second function argument.
     * @return The function result.
     * @throws X If an error occurs.
     */
    int applyAsInt(T t, U u) throws X;

    /**
     * Returns a function that applies this function to its input. Any checked exception thrown by this function is transformed using the given error
     * mapper, and the returned function throws the transformation result.
     *
     * @param <E> The type of checked exception to transform to.
     * @param errorMapper The function to use to transform any checked exception thrown by this function.
     * @return A function that transforms any thrown checked exception.
     * @throws NullPointerException If {@code errorMapper} is {@code null}.
     */
    default <E extends Throwable> ThrowingToIntBiFunction<T, U, E> onErrorThrowAsChecked(Function<? super X, ? extends E> errorMapper) {
        Objects.requireNonNull(errorMapper);
        return (t, u) -> {
            try {
                return applyAsInt(t, u);
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
    default <E extends RuntimeException> ToIntBiFunction<T, U> onErrorThrowAsUnchecked(Function<? super X, ? extends E> errorMapper) {
        Objects.requireNonNull(errorMapper);
        return (t, u) -> {
            try {
                return applyAsInt(t, u);
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
    default <E extends Throwable> ThrowingToIntBiFunction<T, U, E> onErrorHandleChecked(ThrowingToIntFunction<? super X, ? extends E> errorHandler) {
        Objects.requireNonNull(errorHandler);
        return (t, u) -> {
            try {
                return applyAsInt(t, u);
            } catch (Error | RuntimeException e) {
                throw e;
            } catch (Throwable throwable) {
                // This cast is safe, because only Error, RuntimeException (both handled above) and X can be thrown
                @SuppressWarnings("unchecked")
                X x = (X) throwable;
                return errorHandler.applyAsInt(x);
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
    default ToIntBiFunction<T, U> onErrorHandleUnchecked(ToIntFunction<? super X> errorHandler) {
        Objects.requireNonNull(errorHandler);
        return (t, u) -> {
            try {
                return applyAsInt(t, u);
            } catch (Error | RuntimeException e) {
                throw e;
            } catch (Throwable throwable) {
                // This cast is safe, because only Error, RuntimeException (both handled above) and X can be thrown
                @SuppressWarnings("unchecked")
                X x = (X) throwable;
                return errorHandler.applyAsInt(x);
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
    default <E extends Throwable> ThrowingToIntBiFunction<T, U, E> onErrorApplyChecked(
            ThrowingToIntBiFunction<? super T, ? super U, ? extends E> fallback) {

        Objects.requireNonNull(fallback);
        return (t, u) -> {
            try {
                return applyAsInt(t, u);
            } catch (Error | RuntimeException e) {
                throw e;
            } catch (@SuppressWarnings("unused") Throwable throwable) {
                return fallback.applyAsInt(t, u);
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
    default ToIntBiFunction<T, U> onErrorApplyUnchecked(ToIntBiFunction<? super T, ? super U> fallback) {
        Objects.requireNonNull(fallback);
        return (t, u) -> {
            try {
                return applyAsInt(t, u);
            } catch (Error | RuntimeException e) {
                throw e;
            } catch (@SuppressWarnings("unused") Throwable throwable) {
                return fallback.applyAsInt(t, u);
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
    default <E extends Throwable> ThrowingToIntBiFunction<T, U, E> onErrorGetChecked(ThrowingIntSupplier<? extends E> fallback) {
        Objects.requireNonNull(fallback);
        return (t, u) -> {
            try {
                return applyAsInt(t, u);
            } catch (Error | RuntimeException e) {
                throw e;
            } catch (@SuppressWarnings("unused") Throwable throwable) {
                return fallback.getAsInt();
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
    default ToIntBiFunction<T, U> onErrorGetUnchecked(IntSupplier fallback) {
        Objects.requireNonNull(fallback);
        return (t, u) -> {
            try {
                return applyAsInt(t, u);
            } catch (Error | RuntimeException e) {
                throw e;
            } catch (@SuppressWarnings("unused") Throwable throwable) {
                return fallback.getAsInt();
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
    default ToIntBiFunction<T, U> onErrorReturn(int fallback) {
        return (t, u) -> {
            try {
                return applyAsInt(t, u);
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
    default ToIntBiFunction<T, U> unchecked() {
        return onErrorThrowAsUnchecked(UncheckedException::new);
    }

    /**
     * Factory method for turning {@code ThrowingToIntBiFunction}-shaped lambdas into {@code ThrowingToIntBiFunctions}.
     *
     * @param <T> The type of the first argument to the function.
     * @param <U> The type of the second argument to the function.
     * @param <X> The type of checked exception that can be thrown.
     * @param function The lambda to return as {@code ThrowingToIntBiFunction}.
     * @return The given lambda as a {@code ThrowingToIntBiFunction}.
     * @throws NullPointerException If {@code function} is {@code null}.
     */
    static <T, U, X extends Throwable> ThrowingToIntBiFunction<T, U, X> of(ThrowingToIntBiFunction<T, U, X> function) {
        Objects.requireNonNull(function);
        return function;
    }

    /**
     * Returns a function that applies the {@code function} function to its input. Any checked exception thrown by the {@code function} function is
     * wrapped in an {@link UncheckedException}.
     *
     * @param <T> The type of the first argument to the function.
     * @param <U> The type of the second argument to the function.
     * @param function The function to apply when the returned function is applied.
     * @return A function that wraps any checked exception in an {@link UncheckedException}.
     * @throws NullPointerException If {@code function} is {@code null}.
     */
    @SuppressWarnings("unchecked")
    static <T, U> ToIntBiFunction<T, U> unchecked(ThrowingToIntBiFunction<? super T, ? super U, ?> function) {
        Objects.requireNonNull(function);
        return (ToIntBiFunction<T, U>) function.unchecked();
    }

    /**
     * Returns a function that applies the {@code function} function to its input. Any unchecked exception thrown by the {@code function} function is
     * relayed to the caller. This method allows existing {@link ToIntBiFunction} instances to be used where {@code ThrowingToIntBiFunction} is
     * expected.
     *
     * @param <T> The type of the first argument to the function.
     * @param <U> The type of the second argument to the function.
     * @param <X> The type of checked exception that can be thrown.
     * @param function The function to apply when the returned function is applied.
     * @return A function that wraps any checked exception in an {@link UncheckedException}.
     * @throws NullPointerException If {@code function} is {@code null}.
     */
    static <T, U, X extends Throwable> ThrowingToIntBiFunction<T, U, X> checked(ToIntBiFunction<? super T, ? super U> function) {
        Objects.requireNonNull(function);
        return function::applyAsInt;
    }

    /**
     * Returns a function that applies the {@code function} function to its input. Any {@link UncheckedException} thrown by the {@code function}
     * function is unwrapped if its cause is an instance of {@code errorType}, otherwise it is relayed to the caller.
     *
     * @param <T> The type of the first argument to the function.
     * @param <U> The type of the second argument to the function.
     * @param <X> The type of checked exception that can be thrown.
     * @param function The function to apply when the returned function is applied.
     * @param errorType The type of checked exception that can be thrown.
     * @return A function that wraps any checked exception in an {@link UncheckedException}.
     * @throws NullPointerException If {@code function} or {@code errorType} is {@code null}.
     */
    static <T, U, X extends Throwable> ThrowingToIntBiFunction<T, U, X> checked(ToIntBiFunction<? super T, ? super U> function, Class<X> errorType) {
        Objects.requireNonNull(function);
        Objects.requireNonNull(errorType);
        return (t, u) -> invokeAndUnwrap(function, t, u, errorType);
    }

    /**
     * Invokes a function, unwrapping any {@link UncheckedException} that is thrown if its cause if an instance of {@code errorType}.
     *
     * @param <T> The type of the first argument to the function.
     * @param <U> The type of the second argument to the function.
     * @param <X> The type of checked exception that can be thrown.
     * @param function The function to invoke.
     * @param input1 The first input to the function.
     * @param input2 The second input to the function.
     * @param errorType The type of checked exception that can be thrown.
     * @return The result of invoking {@code function}.
     * @throws NullPointerException If {@code function} or {@code errorType} is {@code null}.
     * @throws X If {@code function} throws an {@link UncheckedException} that wraps an instance of {@code errorType}.
     */
    static <T, U, X extends Throwable> int invokeAndUnwrap(ToIntBiFunction<? super T, ? super U> function, T input1, U input2, Class<X> errorType)
            throws X {

        Objects.requireNonNull(errorType);
        try {
            return function.applyAsInt(input1, input2);
        } catch (UncheckedException e) {
            Throwable cause = e.getCause();
            if (errorType.isInstance(cause)) {
                throw errorType.cast(cause);
            }
            throw e;
        }
    }
}
