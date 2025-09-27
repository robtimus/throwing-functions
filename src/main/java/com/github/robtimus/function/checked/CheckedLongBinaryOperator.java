/*
 * CheckedLongBinaryOperator.java
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
import java.util.function.LongBinaryOperator;
import java.util.function.LongSupplier;
import java.util.function.ToLongFunction;

/**
 * Represents an operation upon two {@code long}-valued operands and producing a {@code long}-valued result.
 * This is a checked-exception throwing equivalent of {@link LongBinaryOperator}.
 *
 * @param <X> The type of checked exception that can be thrown.
 */
@FunctionalInterface
public interface CheckedLongBinaryOperator<X extends Exception> {

    /**
     * Applies this operator to the given operands.
     *
     * @param left The first operand.
     * @param right The second operand.
     * @return The operator result.
     * @throws X If an error occurs.
     */
    long applyAsLong(long left, long right) throws X;

    /**
     * Returns a binary operator that applies this operator to its input. Any checked exception thrown by this operator is transformed using the given
     * error mapper, and the returned operator throws the transformation result.
     *
     * @param <E> The type of checked exception to transform to.
     * @param errorMapper The function to use to transform any checked exception thrown by this operator.
     * @return A binary operator that transforms any thrown checked exception.
     * @throws NullPointerException If {@code errorMapper} is {@code null}.
     */
    default <E extends Exception> CheckedLongBinaryOperator<E> onErrorThrowAsChecked(Function<? super X, ? extends E> errorMapper) {
        Objects.requireNonNull(errorMapper);
        return (t, u) -> {
            try {
                return applyAsLong(t, u);
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
     * Returns a binary operator that applies this operator to its input. Any checked exception thrown by this operator is transformed using the given
     * error mapper, and the returned operator throws the transformation result.
     *
     * @param <E> The type of unchecked exception to transform to.
     * @param errorMapper The function to use to transform any checked exception thrown by this operator.
     * @return A binary operator that transforms any thrown checked exception.
     * @throws NullPointerException If {@code errorMapper} is {@code null}.
     */
    default <E extends RuntimeException> LongBinaryOperator onErrorThrowAsUnchecked(Function<? super X, ? extends E> errorMapper) {
        Objects.requireNonNull(errorMapper);
        return (t, u) -> {
            try {
                return applyAsLong(t, u);
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
     * Returns a binary operator that applies this operator to its input. Any checked exception thrown by this operator is transformed using the given
     * error handler, and the returned operator returns the transformation result.
     *
     * @param <E> The type of checked exception that can be thrown by the given error handler.
     * @param errorHandler The function to use to transform any checked exception thrown by this operator.
     * @return A binary operator that transforms any thrown checked exception.
     * @throws NullPointerException If {@code errorHandler} is {@code null}.
     */
    default <E extends Exception> CheckedLongBinaryOperator<E> onErrorHandleChecked(CheckedToLongFunction<? super X, ? extends E> errorHandler) {
        Objects.requireNonNull(errorHandler);
        return (t, u) -> {
            try {
                return applyAsLong(t, u);
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                // This cast is safe, because only RuntimeException (handled above) and X can be thrown
                @SuppressWarnings("unchecked")
                X x = (X) e;
                return errorHandler.applyAsLong(x);
            }
        };
    }

    /**
     * Returns a binary operator that applies this operator to its input. Any checked exception thrown by this operator is transformed using the given
     * error handler, and the returned operator returns the transformation result.
     *
     * @param errorHandler The function to use to transform any checked exception thrown by this operator.
     * @return A binary operator that transforms any thrown checked exception.
     * @throws NullPointerException If {@code errorHandler} is {@code null}.
     */
    default LongBinaryOperator onErrorHandleUnchecked(ToLongFunction<? super X> errorHandler) {
        Objects.requireNonNull(errorHandler);
        return (t, u) -> {
            try {
                return applyAsLong(t, u);
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                // This cast is safe, because only RuntimeException (handled above) and X can be thrown
                @SuppressWarnings("unchecked")
                X x = (X) e;
                return errorHandler.applyAsLong(x);
            }
        };
    }

    /**
     * Returns a binary operator that applies this operator to its input. If this operator throws any checked exception, it is discarded and the given
     * fallback operator is invoked.
     *
     * @param <E> The type of checked exception that can be thrown by the given fallback operator.
     * @param fallback The operator to invoke if this operator throws any checked exception.
     * @return A binary operator that invokes the {@code fallback} operator if this operator throws any checked exception.
     * @throws NullPointerException If {@code fallback} is {@code null}.
     */
    default <E extends Exception> CheckedLongBinaryOperator<E> onErrorApplyChecked(CheckedLongBinaryOperator<? extends E> fallback) {
        Objects.requireNonNull(fallback);
        return (t, u) -> {
            try {
                return applyAsLong(t, u);
            } catch (RuntimeException e) {
                throw e;
            } catch (@SuppressWarnings("unused") Exception e) {
                return fallback.applyAsLong(t, u);
            }
        };
    }

    /**
     * Returns a binary operator that applies this operator to its input. If this operator throws any checked exception, it is discarded and the given
     * fallback operator is invoked.
     *
     * @param fallback The operator to invoke if this operator throws any checked exception.
     * @return A binary operator that invokes the {@code fallback} operator if this operator throws any checked exception.
     * @throws NullPointerException If {@code fallback} is {@code null}.
     */
    default LongBinaryOperator onErrorApplyUnchecked(LongBinaryOperator fallback) {
        Objects.requireNonNull(fallback);
        return (t, u) -> {
            try {
                return applyAsLong(t, u);
            } catch (RuntimeException e) {
                throw e;
            } catch (@SuppressWarnings("unused") Exception e) {
                return fallback.applyAsLong(t, u);
            }
        };
    }

    /**
     * Returns a binary operator that applies this operator to its input. If this operator throws any checked exception, it is discarded and the given
     * fallback supplier is invoked.
     *
     * @param <E> The type of checked exception that can be thrown by the given fallback supplier.
     * @param fallback The supplier to produce the value to return if this operator throws any checked exception.
     * @return A binary operator that invokes the {@code fallback} supplier if this operator throws any checked exception.
     * @throws NullPointerException If {@code fallback} is {@code null}.
     */
    default <E extends Exception> CheckedLongBinaryOperator<E> onErrorGetChecked(CheckedLongSupplier<? extends E> fallback) {
        Objects.requireNonNull(fallback);
        return (t, u) -> {
            try {
                return applyAsLong(t, u);
            } catch (RuntimeException e) {
                throw e;
            } catch (@SuppressWarnings("unused") Exception e) {
                return fallback.getAsLong();
            }
        };
    }

    /**
     * Returns a binary operator that applies this operator to its input. If this operator throws any checked exception, it is discarded and the given
     * fallback supplier is invoked.
     *
     * @param fallback The supplier to produce the value to return if this operator throws any checked exception.
     * @return A binary operator that invokes the {@code fallback} supplier if this operator throws any checked exception.
     * @throws NullPointerException If {@code fallback} is {@code null}.
     */
    default LongBinaryOperator onErrorGetUnchecked(LongSupplier fallback) {
        Objects.requireNonNull(fallback);
        return (t, u) -> {
            try {
                return applyAsLong(t, u);
            } catch (RuntimeException e) {
                throw e;
            } catch (@SuppressWarnings("unused") Exception e) {
                return fallback.getAsLong();
            }
        };
    }

    /**
     * Returns a binary operator that applies this operator to its input. If this operator throws any checked exception, it is discarded and the given
     * fallback value is returned.
     *
     * @param fallback The value to return if this operator throws any checked exception.
     * @return A binary operator that returns the {@code fallback} value if this operator throws any checked exception.
     */
    default LongBinaryOperator onErrorReturn(long fallback) {
        return (t, u) -> {
            try {
                return applyAsLong(t, u);
            } catch (RuntimeException e) {
                throw e;
            } catch (@SuppressWarnings("unused") Exception e) {
                return fallback;
            }
        };
    }

    /**
     * Returns a binary operator that applies this operator to its input. Any checked exception thrown by this operator is wrapped in an
     * {@link UncheckedException}.
     *
     * @return A binary operator that wraps any checked exception in an {@link UncheckedException}.
     */
    default LongBinaryOperator unchecked() {
        return onErrorThrowAsUnchecked(UncheckedException::new);
    }

    /**
     * Factory method for turning {@code CheckedBinaryLongOperator}-shaped lambdas into {@code CheckedBinaryLongOpreators}.
     *
     * @param <X> The type of checked exception that can be thrown.
     * @param operator The lambda to return as {@code CheckedLongBinaryOperator}.
     * @return The given lambda as a {@code CheckedLongBinaryOperator}.
     * @throws NullPointerException If {@code operator} is {@code null}.
     */
    static <X extends Exception> CheckedLongBinaryOperator<X> of(CheckedLongBinaryOperator<X> operator) {
        Objects.requireNonNull(operator);
        return operator;
    }

    /**
     * Returns a binary operator that applies the {@code operator} operator to its input. Any checked exception thrown by the {@code operator}
     * operator is wrapped in an {@link UncheckedException}.
     *
     * @param operator The operator to apply when the returned operator is applied.
     * @return A binary operator that wraps any checked exception in an {@link UncheckedException}.
     * @throws NullPointerException If {@code operator} is {@code null}.
     */
    static LongBinaryOperator unchecked(CheckedLongBinaryOperator<?> operator) {
        Objects.requireNonNull(operator);
        return operator.unchecked();
    }

    /**
     * Returns a binary operator that applies the {@code operator} operator to its input. Any unchecked exception thrown by the {@code operator}
     * operator is relayed to the caller. This method allows existing {@link LongBinaryOperator} instances to be used where
     * {@code CheckedLongBinaryOperator} is expected.
     *
     * @param <X> The type of checked exception that can be thrown.
     * @param operator The operator to apply when the returned operator is applied.
     * @return A binary operator that wraps any checked exception in an {@link UncheckedException}.
     * @throws NullPointerException If {@code operator} is {@code null}.
     */
    static <X extends Exception> CheckedLongBinaryOperator<X> checked(LongBinaryOperator operator) {
        Objects.requireNonNull(operator);
        return operator::applyAsLong;
    }

    /**
     * Returns a binary operator that applies the {@code operator} operator to its input. Any {@link UncheckedException} thrown by the
     * {@code operator} operator is unwrapped if its cause is an instance of {@code errorType}, otherwise it is relayed to the caller.
     *
     * @param <X> The type of checked exception that can be thrown.
     * @param operator The operator to apply when the returned operator is applied.
     * @param errorType The type of checked exception that can be thrown.
     * @return A binary operator that wraps any checked exception in an {@link UncheckedException}.
     * @throws NullPointerException If {@code operator} or {@code errorType} is {@code null}.
     */
    static <X extends Exception> CheckedLongBinaryOperator<X> checked(LongBinaryOperator operator, Class<X> errorType) {
        Objects.requireNonNull(operator);
        Objects.requireNonNull(errorType);
        return (t, u) -> invokeAndUnwrap(operator, t, u, errorType);
    }

    /**
     * Invokes a binary operator, unwrapping any {@link UncheckedException} that is thrown if its cause if an instance of {@code errorType}.
     *
     * @param <X> The type of checked exception that can be thrown.
     * @param operator The operator to invoke.
     * @param input1 The first input to the operator.
     * @param input2 The second input to the operator.
     * @param errorType The type of checked exception that can be thrown.
     * @return The result of invoking {@code operator}.
     * @throws NullPointerException If {@code operator} or {@code errorType} is {@code null}.
     * @throws X If {@code operator} throws an {@link UncheckedException} that wraps an instance of {@code errorType}.
     */
    static <X extends Exception> long invokeAndUnwrap(LongBinaryOperator operator, long input1, long input2, Class<X> errorType) throws X {
        Objects.requireNonNull(errorType);
        try {
            return operator.applyAsLong(input1, input2);
        } catch (UncheckedException e) {
            Exception cause = e.getCause();
            if (errorType.isInstance(cause)) {
                throw errorType.cast(cause);
            }
            throw e;
        }
    }
}
