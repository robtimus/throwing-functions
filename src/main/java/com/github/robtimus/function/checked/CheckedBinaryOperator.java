/*
 * CheckedBinaryOperator.java
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
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Represents an operation upon two operands of the same type, producing a result of the same type as the operands.
 * This is a checked-exception throwing equivalent of {@link BinaryOperator}.
 *
 * @param <T> The type of the operands and result of the operator.
 * @param <X> The type of checked exception that can be thrown.
 */
@FunctionalInterface
@SuppressWarnings("squid:S1181") // Error needs to be caught separately (and re-thrown) to not let it be caught as throwable
public interface CheckedBinaryOperator<T, X extends Throwable> extends CheckedBiFunction<T, T, T, X> {

    @Override
    default <E extends Throwable> CheckedBinaryOperator<T, E> onErrorThrowAsChecked(Function<? super X, ? extends E> errorMapper) {
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

    @Override
    default <E extends RuntimeException> BinaryOperator<T> onErrorThrowAsUnchecked(Function<? super X, ? extends E> errorMapper) {
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

    @Override
    default <E extends Throwable> CheckedBinaryOperator<T, E> onErrorHandleChecked(
            CheckedFunction<? super X, ? extends T, ? extends E> errorHandler) {

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

    @Override
    default BinaryOperator<T> onErrorHandleUnchecked(Function<? super X, ? extends T> errorHandler) {
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

    @Override
    default <E extends Throwable> CheckedBinaryOperator<T, E> onErrorApplyChecked(
            CheckedBiFunction<? super T, ? super T, ? extends T, ? extends E> fallback) {

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

    @Override
    default BinaryOperator<T> onErrorApplyUnchecked(BiFunction<? super T, ? super T, ? extends T> fallback) {
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

    @Override
    default <E extends Throwable> CheckedBinaryOperator<T, E> onErrorGetChecked(CheckedSupplier<? extends T, ? extends E> fallback) {
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

    @Override
    default BinaryOperator<T> onErrorGetUnchecked(Supplier<? extends T> fallback) {
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

    @Override
    default BinaryOperator<T> onErrorReturn(T fallback) {
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

    @Override
    default BinaryOperator<T> unchecked() {
        return onErrorThrowAsUnchecked(UncheckedException::new);
    }

    /**
     * Factory method for turning {@code CheckedBinaryOperator}-shaped lambdas into {@code CheckedBinaryOperators}.
     *
     * @param <T> The type of the operands and result of the operator.
     * @param <X> The type of checked exception that can be thrown.
     * @param operator The lambda to return as {@code CheckedBinaryOperator}.
     * @return The given lambda as a {@code CheckedBinaryOperator}.
     * @throws NullPointerException If {@code operator} is {@code null}.
     */
    static <T, X extends Throwable> CheckedBinaryOperator<T, X> of(CheckedBinaryOperator<T, X> operator) {
        Objects.requireNonNull(operator);
        return operator;
    }

    /**
     * Returns a binary operator that applies the {@code operator} binary operator to its input. Any checked exception thrown by the {@code operator}
     * binary operator is wrapped in an {@link UncheckedException}.
     *
     * @param <T> The type of the operands and result of the operator.
     * @param operator The binary operator to apply when the returned binary operator is applied.
     * @return A binary operator that wraps any checked exception in an {@link UncheckedException}.
     * @throws NullPointerException If {@code operator} is {@code null}.
     */
    static <T> BinaryOperator<T> unchecked(CheckedBinaryOperator<T, ?> operator) {
        Objects.requireNonNull(operator);
        return operator.unchecked();
    }

    /**
     * Returns a binary operator that applies the {@code operator} binary operator to its input. Any unchecked exception thrown by the
     * {@code operator} binary operator is relayed to the caller. This method allows existing {@link BinaryOperator} instances to be used where
     * {@code CheckedBinaryOperator} is expected.
     *
     * @param <T> The type of the operands and result of the operator.
     * @param <X> The type of checked exception that can be thrown.
     * @param operator The binary operator to apply when the returned binary operator is applied.
     * @return A binary operator that wraps any checked exception in an {@link UncheckedException}.
     * @throws NullPointerException If {@code operator} is {@code null}.
     */
    static <T, X extends Throwable> CheckedBinaryOperator<T, X> checked(BinaryOperator<T> operator) {
        Objects.requireNonNull(operator);
        return operator::apply;
    }

    /**
     * Returns a binary operator that applies the {@code operator} binary operator to its input. Any {@link UncheckedException} thrown by the
     * {@code operator} binary operator is unwrapped if its cause is an instance of {@code errorType}, otherwise it is relayed to the caller.
     *
     * @param <T> The type of the operands and result of the operator.
     * @param <X> The type of checked exception that can be thrown.
     * @param operator The binary operator to apply when the returned binary operator is applied.
     * @param errorType The type of checked exception that can be thrown.
     * @return A binary operator that wraps any checked exception in an {@link UncheckedException}.
     * @throws NullPointerException If {@code operator} or {@code errorType} is {@code null}.
     */
    static <T, X extends Throwable> CheckedBinaryOperator<T, X> checked(BinaryOperator<T> operator, Class<X> errorType) {
        Objects.requireNonNull(operator);
        Objects.requireNonNull(errorType);
        return (t, u) -> invokeAndUnwrap(operator, t, u, errorType);
    }

    /**
     * Invokes a binary operator, unwrapping any {@link UncheckedException} that is thrown if its cause if an instance of {@code errorType}.
     *
     * @param <T> The type of the operands and result of the operator.
     * @param <X> The type of checked exception that can be thrown.
     * @param operator The binary operator to invoke.
     * @param input1 The first input to the binary operator.
     * @param input2 The second input to the binary operator.
     * @param errorType The type of checked exception that can be thrown.
     * @return The result of invoking {@code operator}.
     * @throws NullPointerException If {@code operator} or {@code errorType} is {@code null}.
     * @throws X If {@code operator} throws an {@link UncheckedException} that wraps an instance of {@code errorType}.
     */
    static <T, X extends Throwable> T invokeAndUnwrap(BinaryOperator<T> operator, T input1, T input2, Class<X> errorType) throws X {
        Objects.requireNonNull(errorType);
        try {
            return operator.apply(input1, input2);
        } catch (UncheckedException e) {
            Throwable cause = e.getCause();
            if (errorType.isInstance(cause)) {
                throw errorType.cast(cause);
            }
            throw e;
        }
    }
}
