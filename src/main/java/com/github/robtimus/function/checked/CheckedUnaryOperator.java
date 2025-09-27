/*
 * CheckedUnaryOperator.java
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
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * Represents an operation on a single operand that produces a result of the same type as its operand.
 * This is a checked-exception throwing equivalent of {@link UnaryOperator}.
 *
 * @param <T> The type of the operand and result of the operator.
 * @param <X> The type of checked exception that can be thrown.
 */
@FunctionalInterface
public interface CheckedUnaryOperator<T, X extends Exception> extends CheckedFunction<T, T, X> {

    @Override
    default <E extends Exception> CheckedUnaryOperator<T, E> onErrorThrowAsChecked(Function<? super X, ? extends E> errorMapper) {
        Objects.requireNonNull(errorMapper);
        return t -> {
            try {
                return apply(t);
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

    @Override
    default <E extends RuntimeException> UnaryOperator<T> onErrorThrowAsUnchecked(Function<? super X, ? extends E> errorMapper) {
        Objects.requireNonNull(errorMapper);
        return t -> {
            try {
                return apply(t);
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

    @Override
    default <E extends Exception> CheckedUnaryOperator<T, E> onErrorHandleChecked(CheckedFunction<? super X, ? extends T, ? extends E> errorHandler) {
        Objects.requireNonNull(errorHandler);
        return t -> {
            try {
                return apply(t);
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                // This cast is safe, because only RuntimeException (handled above) and X can be thrown
                @SuppressWarnings("unchecked")
                X x = (X) e;
                return errorHandler.apply(x);
            }
        };
    }

    @Override
    default UnaryOperator<T> onErrorHandleUnchecked(Function<? super X, ? extends T> errorHandler) {
        Objects.requireNonNull(errorHandler);
        return t -> {
            try {
                return apply(t);
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                // This cast is safe, because only RuntimeException (handled above) and X can be thrown
                @SuppressWarnings("unchecked")
                X x = (X) e;
                return errorHandler.apply(x);
            }
        };
    }

    @Override
    default <E extends Exception> CheckedUnaryOperator<T, E> onErrorApplyChecked(CheckedFunction<? super T, ? extends T, ? extends E> fallback) {
        Objects.requireNonNull(fallback);
        return t -> {
            try {
                return apply(t);
            } catch (RuntimeException e) {
                throw e;
            } catch (@SuppressWarnings("unused") Exception e) {
                return fallback.apply(t);
            }
        };
    }

    @Override
    default UnaryOperator<T> onErrorApplyUnchecked(Function<? super T, ? extends T> fallback) {
        Objects.requireNonNull(fallback);
        return t -> {
            try {
                return apply(t);
            } catch (RuntimeException e) {
                throw e;
            } catch (@SuppressWarnings("unused") Exception e) {
                return fallback.apply(t);
            }
        };
    }

    @Override
    default <E extends Exception> CheckedUnaryOperator<T, E> onErrorGetChecked(CheckedSupplier<? extends T, ? extends E> fallback) {
        Objects.requireNonNull(fallback);
        return t -> {
            try {
                return apply(t);
            } catch (RuntimeException e) {
                throw e;
            } catch (@SuppressWarnings("unused") Exception e) {
                return fallback.get();
            }
        };
    }

    @Override
    default UnaryOperator<T> onErrorGetUnchecked(Supplier<? extends T> fallback) {
        Objects.requireNonNull(fallback);
        return t -> {
            try {
                return apply(t);
            } catch (RuntimeException e) {
                throw e;
            } catch (@SuppressWarnings("unused") Exception e) {
                return fallback.get();
            }
        };
    }

    @Override
    default UnaryOperator<T> onErrorReturn(T fallback) {
        return t -> {
            try {
                return apply(t);
            } catch (RuntimeException e) {
                throw e;
            } catch (@SuppressWarnings("unused") Exception e) {
                return fallback;
            }
        };
    }

    @Override
    default UnaryOperator<T> unchecked() {
        return onErrorThrowAsUnchecked(UncheckedException::new);
    }

    /**
     * Factory method for turning {@code CheckedUnaryOperator}-shaped lambdas into {@code CheckedUnaryOperators}.
     *
     * @param <T> The type of the operand and result of the operator.
     * @param <X> The type of checked exception that can be thrown.
     * @param operator The lambda to return as {@code CheckedUnaryOperator}.
     * @return The given lambda as a {@code CheckedUnaryOperator}.
     * @throws NullPointerException If {@code operator} is {@code null}.
     */
    static <T, X extends Exception> CheckedUnaryOperator<T, X> of(CheckedUnaryOperator<T, X> operator) {
        Objects.requireNonNull(operator);
        return operator;
    }

    /**
     * Returns a unary operator that always returns its input argument.
     *
     * @param <T> The type of the operand and result of the operator.
     * @param <X> The type of checked exception that can be thrown.
     * @return A unary operator that always returns its input argument.
     */
    static <T, X extends Exception> CheckedUnaryOperator<T, X> identity() {
        return t -> t;
    }

    /**
     * Returns a unary operator that applies the {@code operator} unary operator to its input. Any checked exception thrown by the {@code operator}
     * unary operator is wrapped in an {@link UncheckedException}.
     *
     * @param <T> The type of the operand and result of the operator.
     * @param operator The unary operator to apply when the returned unary operator is applied.
     * @return A unary operator that wraps any checked exception in an {@link UncheckedException}.
     * @throws NullPointerException If {@code operator} is {@code null}.
     */
    static <T> UnaryOperator<T> unchecked(CheckedUnaryOperator<T, ?> operator) {
        Objects.requireNonNull(operator);
        return operator.unchecked();
    }

    /**
     * Returns a unary operator that applies the {@code operator} unary operator to its input. Any unchecked exception thrown by the {@code operator}
     * unary operator is relayed to the caller. This method allows existing {@link UnaryOperator} instances to be used where
     * {@code CheckedUnaryOperator} is expected.
     *
     * @param <T> The type of the operand and result of the operator.
     * @param <X> The type of checked exception that can be thrown.
     * @param operator The unary operator to apply when the returned unary operator is applied.
     * @return A unary operator that wraps any checked exception in an {@link UncheckedException}.
     * @throws NullPointerException If {@code operator} is {@code null}.
     */
    static <T, X extends Exception> CheckedUnaryOperator<T, X> checked(UnaryOperator<T> operator) {
        Objects.requireNonNull(operator);
        return operator::apply;
    }

    /**
     * Returns a unary operator that applies the {@code operator} unary operator to its input. Any {@link UncheckedException} thrown by the
     * {@code operator} unary operator is unwrapped if its cause is an instance of {@code errorType}, otherwise it is relayed to the caller.
     *
     * @param <T> The type of the operand and result of the operator.
     * @param <X> The type of checked exception that can be thrown.
     * @param operator The unary operator to apply when the returned unary operator is applied.
     * @param errorType The type of checked exception that can be thrown.
     * @return A unary operator that wraps any checked exception in an {@link UncheckedException}.
     * @throws NullPointerException If {@code operator} or {@code errorType} is {@code null}.
     */
    static <T, X extends Exception> CheckedUnaryOperator<T, X> checked(UnaryOperator<T> operator, Class<X> errorType) {
        Objects.requireNonNull(operator);
        Objects.requireNonNull(errorType);
        return t -> invokeAndUnwrap(operator, t, errorType);
    }

    /**
     * Invokes a unary operator, unwrapping any {@link UncheckedException} that is thrown if its cause if an instance of {@code errorType}.
     *
     * @param <T> The type of the operand and result of the operator.
     * @param <X> The type of checked exception that can be thrown.
     * @param operator The unary operator to invoke.
     * @param input The input to the unary operator.
     * @param errorType The type of checked exception that can be thrown.
     * @return The result of invoking {@code operator}.
     * @throws NullPointerException If {@code operator} or {@code errorType} is {@code null}.
     * @throws X If {@code operator} throws an {@link UncheckedException} that wraps an instance of {@code errorType}.
     */
    static <T, X extends Exception> T invokeAndUnwrap(UnaryOperator<T> operator, T input, Class<X> errorType) throws X {
        Objects.requireNonNull(errorType);
        try {
            return operator.apply(input);
        } catch (UncheckedException e) {
            Exception cause = e.getCause();
            if (errorType.isInstance(cause)) {
                throw errorType.cast(cause);
            }
            throw e;
        }
    }
}
