/*
 * ThrowingLongBinaryOperator.java
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
@SuppressWarnings("squid:S1181") // Error needs to be caught separately (and re-thrown) to not let it be caught as throwable
public interface ThrowingLongBinaryOperator<X extends Throwable> {

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
    default <E extends Throwable> ThrowingLongBinaryOperator<E> onErrorThrowAsChecked(Function<? super X, ? extends E> errorMapper) {
        Objects.requireNonNull(errorMapper);
        return (t, u) -> {
            try {
                return applyAsLong(t, u);
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
     * Returns a binary operator that applies this operator to its input. Any checked exception thrown by this operator is transformed using the given
     * error handler, and the returned operator returns the transformation result.
     *
     * @param <E> The type of checked exception that can be thrown by the given error handler.
     * @param errorHandler The function to use to transform any checked exception thrown by this operator.
     * @return A binary operator that transforms any thrown checked exception.
     * @throws NullPointerException If {@code errorHandler} is {@code null}.
     */
    default <E extends Throwable> ThrowingLongBinaryOperator<E> onErrorHandleChecked(ThrowingToLongFunction<? super X, ? extends E> errorHandler) {
        Objects.requireNonNull(errorHandler);
        return (t, u) -> {
            try {
                return applyAsLong(t, u);
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
     * Returns a binary operator that applies this operator to its input. If this operator throws any checked exception, it is discarded and the given
     * fallback operator is invoked.
     *
     * @param <E> The type of checked exception that can be thrown by the given fallback operator.
     * @param fallback The operator to invoke if this operator throws any checked exception.
     * @return A binary operator that invokes the {@code fallback} operator if this operator throws any checked exception.
     * @throws NullPointerException If {@code fallback} is {@code null}.
     */
    default <E extends Throwable> ThrowingLongBinaryOperator<E> onErrorApplyChecked(ThrowingLongBinaryOperator<? extends E> fallback) {
        Objects.requireNonNull(fallback);
        return (t, u) -> {
            try {
                return applyAsLong(t, u);
            } catch (Error | RuntimeException e) {
                throw e;
            } catch (@SuppressWarnings("unused") Throwable throwable) {
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
            } catch (Error | RuntimeException e) {
                throw e;
            } catch (@SuppressWarnings("unused") Throwable throwable) {
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
    default <E extends Throwable> ThrowingLongBinaryOperator<E> onErrorGetChecked(ThrowingLongSupplier<? extends E> fallback) {
        Objects.requireNonNull(fallback);
        return (t, u) -> {
            try {
                return applyAsLong(t, u);
            } catch (Error | RuntimeException e) {
                throw e;
            } catch (@SuppressWarnings("unused") Throwable throwable) {
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
            } catch (Error | RuntimeException e) {
                throw e;
            } catch (@SuppressWarnings("unused") Throwable throwable) {
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
            } catch (Error | RuntimeException e) {
                throw e;
            } catch (@SuppressWarnings("unused") Throwable throwable) {
                return fallback;
            }
        };
    }

    /**
     * Returns a binary operator that applies this operator to its input. Any checked exception thrown by this operator is wrapped in an
     * {@link UncheckedException} {@linkplain UncheckedException#withoutStackTrace(Throwable) without a stack trace}.
     *
     * @return A binary operator that wraps any checked exception in an {@link UncheckedException}.
     */
    default LongBinaryOperator unchecked() {
        return onErrorThrowAsUnchecked(UncheckedException::withoutStackTrace);
    }

    /**
     * Factory method for turning {@code ThrowingLongBinaryOperator}-shaped lambdas into {@code ThrowingLongBinaryOpreators}.
     *
     * @param <X> The type of checked exception that can be thrown.
     * @param operator The lambda to return as {@code ThrowingLongBinaryOperator}.
     * @return The given lambda as a {@code ThrowingLongBinaryOperator}.
     * @throws NullPointerException If {@code operator} is {@code null}.
     */
    static <X extends Throwable> ThrowingLongBinaryOperator<X> of(ThrowingLongBinaryOperator<X> operator) {
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
    static LongBinaryOperator unchecked(ThrowingLongBinaryOperator<?> operator) {
        Objects.requireNonNull(operator);
        return operator.unchecked();
    }

    /**
     * Returns a binary operator that applies the {@code operator} operator to its input. Any unchecked exception thrown by the {@code operator}
     * operator is relayed to the caller. This method allows existing {@link LongBinaryOperator} instances to be used where
     * {@code ThrowingLongBinaryOperator} is expected.
     *
     * @param <X> The type of checked exception that can be thrown.
     * @param operator The operator to apply when the returned operator is applied.
     * @return A binary operator that wraps any checked exception in an {@link UncheckedException}.
     * @throws NullPointerException If {@code operator} is {@code null}.
     */
    static <X extends Throwable> ThrowingLongBinaryOperator<X> checked(LongBinaryOperator operator) {
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
    static <X extends Throwable> ThrowingLongBinaryOperator<X> checked(LongBinaryOperator operator, Class<X> errorType) {
        Objects.requireNonNull(operator);
        Objects.requireNonNull(errorType);
        return (t, u) -> {
            try {
                return operator.applyAsLong(t, u);
            } catch (UncheckedException e) {
                Throwable cause = e.getCause();
                if (errorType.isInstance(cause)) {
                    throw errorType.cast(cause);
                }
                throw e;
            }
        };
    }
}
