/*
 * CheckedToDoubleFunction.java
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
import java.util.function.DoubleSupplier;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;

/**
 * Represents a function that produces an {@code double}-valued result.
 * This is a checked-exception throwing equivalent of {@link ToDoubleFunction}.
 *
 * @param <T> The type of the input to the function.
 * @param <X> The type of checked exception that can be thrown.
 */
@FunctionalInterface
public interface CheckedToDoubleFunction<T, X extends Exception> {

    /**
     * Applies this function to the given argument.
     *
     * @param value The function argument.
     * @return The function result.
     * @throws X If an error occurs.
     */
    double applyAsDouble(T value) throws X;

    /**
     * Returns a function that applies this function to its input. Any checked exception thrown by this function is transformed using the given error
     * mapper, and the returned function throws the transformation result.
     *
     * @param <E> The type of checked exception to transform to.
     * @param errorMapper The function to use to transform any checked exception thrown by this function.
     * @return A function that transforms any thrown checked exception.
     * @throws NullPointerException If {@code errorMapper} is {@code null}.
     */
    default <E extends Exception> CheckedToDoubleFunction<T, E> onErrorThrowAsChecked(Function<? super X, ? extends E> errorMapper) {
        Objects.requireNonNull(errorMapper);
        return t -> {
            try {
                return applyAsDouble(t);
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
    default <E extends RuntimeException> ToDoubleFunction<T> onErrorThrowAsUnchecked(Function<? super X, ? extends E> errorMapper) {
        Objects.requireNonNull(errorMapper);
        return t -> {
            try {
                return applyAsDouble(t);
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
    default <E extends Exception> CheckedToDoubleFunction<T, E> onErrorHandleChecked(CheckedToDoubleFunction<? super X, ? extends E> errorHandler) {
        Objects.requireNonNull(errorHandler);
        return t -> {
            try {
                return applyAsDouble(t);
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                // This cast is safe, because only RuntimeException (handled above) and X can be thrown
                @SuppressWarnings("unchecked")
                X x = (X) e;
                return errorHandler.applyAsDouble(x);
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
    default ToDoubleFunction<T> onErrorHandleUnchecked(ToDoubleFunction<? super X> errorHandler) {
        Objects.requireNonNull(errorHandler);
        return t -> {
            try {
                return applyAsDouble(t);
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                // This cast is safe, because only RuntimeException (handled above) and X can be thrown
                @SuppressWarnings("unchecked")
                X x = (X) e;
                return errorHandler.applyAsDouble(x);
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
    default <E extends Exception> CheckedToDoubleFunction<T, E> onErrorApplyChecked(CheckedToDoubleFunction<? super T, ? extends E> fallback) {
        Objects.requireNonNull(fallback);
        return t -> {
            try {
                return applyAsDouble(t);
            } catch (RuntimeException e) {
                throw e;
            } catch (@SuppressWarnings("unused") Exception e) {
                return fallback.applyAsDouble(t);
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
    default ToDoubleFunction<T> onErrorApplyUnchecked(ToDoubleFunction<? super T> fallback) {
        Objects.requireNonNull(fallback);
        return t -> {
            try {
                return applyAsDouble(t);
            } catch (RuntimeException e) {
                throw e;
            } catch (@SuppressWarnings("unused") Exception e) {
                return fallback.applyAsDouble(t);
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
    default <E extends Exception> CheckedToDoubleFunction<T, E> onErrorGetChecked(CheckedDoubleSupplier<? extends E> fallback) {
        Objects.requireNonNull(fallback);
        return t -> {
            try {
                return applyAsDouble(t);
            } catch (RuntimeException e) {
                throw e;
            } catch (@SuppressWarnings("unused") Exception e) {
                return fallback.getAsDouble();
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
    default ToDoubleFunction<T> onErrorGetUnchecked(DoubleSupplier fallback) {
        Objects.requireNonNull(fallback);
        return t -> {
            try {
                return applyAsDouble(t);
            } catch (RuntimeException e) {
                throw e;
            } catch (@SuppressWarnings("unused") Exception e) {
                return fallback.getAsDouble();
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
    default ToDoubleFunction<T> onErrorReturn(double fallback) {
        return t -> {
            try {
                return applyAsDouble(t);
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
    default ToDoubleFunction<T> unchecked() {
        return onErrorThrowAsUnchecked(UncheckedException::new);
    }

    /**
     * Factory method for turning {@code CheckedToDoubleFunction}-shaped lambdas into {@code CheckedToDoubleFunctions}.
     *
     * @param <T> The type of the input to the function.
     * @param <X> The type of checked exception that can be thrown.
     * @param function The lambda to return as {@code CheckedToDoubleFunction}.
     * @return The given lambda as a {@code CheckedToDoubleFunction}.
     * @throws NullPointerException If {@code function} is {@code null}.
     */
    static <T, X extends Exception> CheckedToDoubleFunction<T, X> of(CheckedToDoubleFunction<T, X> function) {
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
    static <T> ToDoubleFunction<T> unchecked(CheckedToDoubleFunction<? super T, ?> function) {
        Objects.requireNonNull(function);
        return t -> {
            try {
                return function.applyAsDouble(t);
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new UncheckedException(e);
            }
        };
    }

    /**
     * Returns a function that applies the {@code function} function to its input. Any unchecked exception thrown by the {@code function} function is
     * relayed to the caller. This method allows existing {@link ToDoubleFunction} instances to be used where {@code CheckedToDoubleFunction} is
     * expected.
     *
     * @param <T> The type of the input to the function.
     * @param <X> The type of checked exception that can be thrown.
     * @param function The function to apply when the returned function is applied.
     * @return A function that wraps any checked exception in an {@link UncheckedException}.
     * @throws NullPointerException If {@code function} is {@code null}.
     */
    static <T, X extends Exception> CheckedToDoubleFunction<T, X> checked(ToDoubleFunction<? super T> function) {
        Objects.requireNonNull(function);
        return function::applyAsDouble;
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
    static <T, X extends Exception> CheckedToDoubleFunction<T, X> checked(ToDoubleFunction<? super T> function, Class<X> errorType) {
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
    static <T, X extends Exception> double invokeAndUnwrap(ToDoubleFunction<? super T> function, T input, Class<X> errorType) throws X {
        Objects.requireNonNull(errorType);
        try {
            return function.applyAsDouble(input);
        } catch (UncheckedException e) {
            Exception cause = e.getCause();
            if (errorType.isInstance(cause)) {
                throw errorType.cast(cause);
            }
            throw e;
        }
    }
}
