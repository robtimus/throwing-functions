/*
 * ThrowingIntBinaryOperator.java
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
import java.util.function.IntBinaryOperator;
import java.util.function.IntSupplier;
import java.util.function.ToIntFunction;

/**
 * Represents an operation upon two {@code int}-valued operands and producing an {@code int}-valued result.
 * This is a checked-exception throwing equivalent of {@link IntBinaryOperator}.
 *
 * @param <X> The type of checked exception that can be thrown.
 */
@FunctionalInterface
@SuppressWarnings("squid:S1181") // Error needs to be caught separately (and re-thrown) to not let it be caught as throwable
public interface ThrowingIntBinaryOperator<X extends Throwable> {

    /**
     * Applies this operator to the given operands.
     *
     * @param left The first operand.
     * @param right The second operand.
     * @return The operator result.
     * @throws X If an error occurs.
     */
    int applyAsInt(int left, int right) throws X;

    /**
     * Returns a binary operator that applies this operator to its input. Any checked exception thrown by this operator is transformed using the given
     * error mapper, and the returned operator throws the transformation result.
     *
     * @param <E> The type of checked exception to transform to.
     * @param errorMapper The function to use to transform any checked exception thrown by this operator.
     * @return A binary operator that transforms any thrown checked exception.
     * @throws NullPointerException If {@code errorMapper} is {@code null}.
     */
    default <E extends Throwable> ThrowingIntBinaryOperator<E> onErrorThrowAsChecked(Function<? super X, ? extends E> errorMapper) {
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
     * Returns a binary operator that applies this operator to its input. Any checked exception thrown by this operator is transformed using the given
     * error mapper, and the returned operator throws the transformation result.
     *
     * @param <E> The type of unchecked exception to transform to.
     * @param errorMapper The function to use to transform any checked exception thrown by this operator.
     * @return A binary operator that transforms any thrown checked exception.
     * @throws NullPointerException If {@code errorMapper} is {@code null}.
     */
    default <E extends RuntimeException> IntBinaryOperator onErrorThrowAsUnchecked(Function<? super X, ? extends E> errorMapper) {
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
     * Returns a binary operator that applies this operator to its input. Any checked exception thrown by this operator is transformed using the given
     * error handler, and the returned operator returns the transformation result.
     *
     * @param <E> The type of checked exception that can be thrown by the given error handler.
     * @param errorHandler The function to use to transform any checked exception thrown by this operator.
     * @return A binary operator that transforms any thrown checked exception.
     * @throws NullPointerException If {@code errorHandler} is {@code null}.
     */
    default <E extends Throwable> ThrowingIntBinaryOperator<E> onErrorHandleChecked(ThrowingToIntFunction<? super X, ? extends E> errorHandler) {
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
     * Returns a binary operator that applies this operator to its input. Any checked exception thrown by this operator is transformed using the given
     * error handler, and the returned operator returns the transformation result.
     *
     * @param errorHandler The function to use to transform any checked exception thrown by this operator.
     * @return A binary operator that transforms any thrown checked exception.
     * @throws NullPointerException If {@code errorHandler} is {@code null}.
     */
    default IntBinaryOperator onErrorHandleUnchecked(ToIntFunction<? super X> errorHandler) {
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
     * Returns a binary operator that applies this operator to its input. If this operator throws any checked exception, it is discarded and the given
     * fallback operator is invoked.
     *
     * @param <E> The type of checked exception that can be thrown by the given fallback operator.
     * @param fallback The operator to invoke if this operator throws any checked exception.
     * @return A binary operator that invokes the {@code fallback} operator if this operator throws any checked exception.
     * @throws NullPointerException If {@code fallback} is {@code null}.
     */
    default <E extends Throwable> ThrowingIntBinaryOperator<E> onErrorApplyChecked(ThrowingIntBinaryOperator<? extends E> fallback) {
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
     * Returns a binary operator that applies this operator to its input. If this operator throws any checked exception, it is discarded and the given
     * fallback operator is invoked.
     *
     * @param fallback The operator to invoke if this operator throws any checked exception.
     * @return A binary operator that invokes the {@code fallback} operator if this operator throws any checked exception.
     * @throws NullPointerException If {@code fallback} is {@code null}.
     */
    default IntBinaryOperator onErrorApplyUnchecked(IntBinaryOperator fallback) {
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
     * Returns a binary operator that applies this operator to its input. If this operator throws any checked exception, it is discarded and the given
     * fallback supplier is invoked.
     *
     * @param <E> The type of checked exception that can be thrown by the given fallback supplier.
     * @param fallback The supplier to produce the value to return if this operator throws any checked exception.
     * @return A binary operator that invokes the {@code fallback} supplier if this operator throws any checked exception.
     * @throws NullPointerException If {@code fallback} is {@code null}.
     */
    default <E extends Throwable> ThrowingIntBinaryOperator<E> onErrorGetChecked(ThrowingIntSupplier<? extends E> fallback) {
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
     * Returns a binary operator that applies this operator to its input. If this operator throws any checked exception, it is discarded and the given
     * fallback supplier is invoked.
     *
     * @param fallback The supplier to produce the value to return if this operator throws any checked exception.
     * @return A binary operator that invokes the {@code fallback} supplier if this operator throws any checked exception.
     * @throws NullPointerException If {@code fallback} is {@code null}.
     */
    default IntBinaryOperator onErrorGetUnchecked(IntSupplier fallback) {
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
     * Returns a binary operator that applies this operator to its input. If this operator throws any checked exception, it is discarded and the given
     * fallback value is returned.
     *
     * @param fallback The value to return if this operator throws any checked exception.
     * @return A binary operator that returns the {@code fallback} value if this operator throws any checked exception.
     */
    default IntBinaryOperator onErrorReturn(int fallback) {
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
     * Returns a binary operator that applies this operator to its input. Any checked exception thrown by this operator is wrapped in an
     * {@link UncheckedException} {@linkplain UncheckedException#withoutStackTrace(Throwable) without a stack trace}.
     *
     * @return A binary operator that wraps any checked exception in an {@link UncheckedException}.
     */
    default IntBinaryOperator unchecked() {
        return onErrorThrowAsUnchecked(UncheckedException::withoutStackTrace);
    }

    /**
     * Factory method for turning {@code ThrowingBinaryIntOperator}-shaped lambdas into {@code ThrowingBinaryIntOpreators}.
     *
     * @param <X> The type of checked exception that can be thrown.
     * @param operator The lambda to return as {@code ThrowingIntBinaryOperator}.
     * @return The given lambda as a {@code ThrowingIntBinaryOperator}.
     * @throws NullPointerException If {@code operator} is {@code null}.
     */
    static <X extends Throwable> ThrowingIntBinaryOperator<X> of(ThrowingIntBinaryOperator<X> operator) {
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
    static IntBinaryOperator unchecked(ThrowingIntBinaryOperator<?> operator) {
        Objects.requireNonNull(operator);
        return operator.unchecked();
    }

    /**
     * Returns a binary operator that applies the {@code operator} operator to its input. Any unchecked exception thrown by the {@code operator}
     * operator is relayed to the caller. This method allows existing {@link IntBinaryOperator} instances to be used where
     * {@code ThrowingIntBinaryOperator} is expected.
     *
     * @param <X> The type of checked exception that can be thrown.
     * @param operator The operator to apply when the returned operator is applied.
     * @return A binary operator that wraps any checked exception in an {@link UncheckedException}.
     * @throws NullPointerException If {@code operator} is {@code null}.
     */
    static <X extends Throwable> ThrowingIntBinaryOperator<X> checked(IntBinaryOperator operator) {
        Objects.requireNonNull(operator);
        return operator::applyAsInt;
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
    static <X extends Throwable> ThrowingIntBinaryOperator<X> checked(IntBinaryOperator operator, Class<X> errorType) {
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
    static <X extends Throwable> int invokeAndUnwrap(IntBinaryOperator operator, int input1, int input2, Class<X> errorType) throws X {
        Objects.requireNonNull(errorType);
        try {
            return operator.applyAsInt(input1, input2);
        } catch (UncheckedException e) {
            Throwable cause = e.getCause();
            if (errorType.isInstance(cause)) {
                throw errorType.cast(cause);
            }
            throw e;
        }
    }
}
