/*
 * CheckedToIntBiFunction.java
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
public interface CheckedToIntBiFunction<T, U, X extends Exception> {

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
    default <E extends Exception> CheckedToIntBiFunction<T, U, E> onErrorThrowAsChecked(Function<? super X, ? extends E> errorMapper) {
        Objects.requireNonNull(errorMapper);
        return (t, u) -> {
            try {
                return applyAsInt(t, u);
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                // This cast is safe, because only RuntimeException (handled above) and X can be thrown
                @SuppressWarnings("unchecked")
                X x = (X) e;
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
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                // This cast is safe, because only RuntimeException (handled above) and X can be thrown
                @SuppressWarnings("unchecked")
                X x = (X) e;
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
    default <E extends Exception> CheckedToIntBiFunction<T, U, E> onErrorHandleChecked(CheckedToIntFunction<? super X, ? extends E> errorHandler) {
        Objects.requireNonNull(errorHandler);
        return (t, u) -> {
            try {
                return applyAsInt(t, u);
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                // This cast is safe, because only RuntimeException (handled above) and X can be thrown
                @SuppressWarnings("unchecked")
                X x = (X) e;
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
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                // This cast is safe, because only RuntimeException (handled above) and X can be thrown
                @SuppressWarnings("unchecked")
                X x = (X) e;
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
    default <E extends Exception> CheckedToIntBiFunction<T, U, E> onErrorApplyChecked(
            CheckedToIntBiFunction<? super T, ? super U, ? extends E> fallback) {

        Objects.requireNonNull(fallback);
        return (t, u) -> {
            try {
                return applyAsInt(t, u);
            } catch (RuntimeException e) {
                throw e;
            } catch (@SuppressWarnings("unused") Exception e) {
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
            } catch (RuntimeException e) {
                throw e;
            } catch (@SuppressWarnings("unused") Exception e) {
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
    default <E extends Exception> CheckedToIntBiFunction<T, U, E> onErrorGetChecked(CheckedIntSupplier<? extends E> fallback) {
        Objects.requireNonNull(fallback);
        return (t, u) -> {
            try {
                return applyAsInt(t, u);
            } catch (RuntimeException e) {
                throw e;
            } catch (@SuppressWarnings("unused") Exception e) {
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
            } catch (RuntimeException e) {
                throw e;
            } catch (@SuppressWarnings("unused") Exception e) {
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
            } catch (RuntimeException e) {
                throw e;
            } catch (@SuppressWarnings("unused") Exception e) {
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
     * Factory method for turning {@code CheckedToIntBiFunction}-shaped lambdas into {@code CheckedToIntBiFunctions}.
     *
     * @param <T> The type of the first argument to the function.
     * @param <U> The type of the second argument to the function.
     * @param <X> The type of checked exception that can be thrown.
     * @param function The lambda to return as {@code CheckedToIntBiFunction}.
     * @return The given lambda as a {@code CheckedToIntBiFunction}.
     * @throws NullPointerException If {@code function} is {@code null}.
     */
    static <T, U, X extends Exception> CheckedToIntBiFunction<T, U, X> of(CheckedToIntBiFunction<T, U, X> function) {
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
    static <T, U> ToIntBiFunction<T, U> unchecked(CheckedToIntBiFunction<? super T, ? super U, ?> function) {
        Objects.requireNonNull(function);
        return (ToIntBiFunction<T, U>) function.unchecked();
    }

    /**
     * Returns a function that applies the {@code function} function to its input. Any unchecked exception thrown by the {@code function} function is
     * relayed to the caller. This method allows existing {@link ToIntBiFunction} instances to be used where {@code CheckedToIntBiFunction} is
     * expected.
     *
     * @param <T> The type of the first argument to the function.
     * @param <U> The type of the second argument to the function.
     * @param <X> The type of checked exception that can be thrown.
     * @param function The function to apply when the returned function is applied.
     * @return A function that wraps any checked exception in an {@link UncheckedException}.
     * @throws NullPointerException If {@code function} is {@code null}.
     */
    static <T, U, X extends Exception> CheckedToIntBiFunction<T, U, X> checked(ToIntBiFunction<? super T, ? super U> function) {
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
    static <T, U, X extends Exception> CheckedToIntBiFunction<T, U, X> checked(ToIntBiFunction<? super T, ? super U> function, Class<X> errorType) {
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
    static <T, U, X extends Exception> int invokeAndUnwrap(ToIntBiFunction<? super T, ? super U> function, T input1, U input2, Class<X> errorType)
            throws X {

        Objects.requireNonNull(errorType);
        try {
            return function.applyAsInt(input1, input2);
        } catch (UncheckedException e) {
            Exception cause = e.getCause();
            if (errorType.isInstance(cause)) {
                throw errorType.cast(cause);
            }
            throw e;
        }
    }
}
