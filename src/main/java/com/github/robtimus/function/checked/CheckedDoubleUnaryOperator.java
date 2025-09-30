/*
 * CheckedDoubleUnaryOperator.java
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
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;

/**
 * Represents an operation on a single {@code double}-valued operand that produces a {@code double}-valued result.
 * This is a checked-exception throwing equivalent of {@link DoubleUnaryOperator}.
 *
 * @param <X> The type of checked exception that can be thrown.
 */
@FunctionalInterface
@SuppressWarnings("squid:S1181") // Error needs to be caught separately (and re-thrown) to not let it be caught as throwable
public interface CheckedDoubleUnaryOperator<X extends Throwable> {

    /**
     * Applies this operator to the given operand.
     *
     * @param operand The operand.
     * @return The operator result.
     * @throws X If an error occurs.
     */
    double applyAsDouble(double operand) throws X;

    /**
     * Returns a unary operator that applies this operator to its input. Any checked exception thrown by this operator is transformed using the given
     * error mapper, and the returned operator throws the transformation result.
     *
     * @param <E> The type of checked exception to transform to.
     * @param errorMapper The function to use to transform any checked exception thrown by this operator.
     * @return A unary operator that transforms any thrown checked exception.
     * @throws NullPointerException If {@code errorMapper} is {@code null}.
     */
    default <E extends Throwable> CheckedDoubleUnaryOperator<E> onErrorThrowAsChecked(Function<? super X, ? extends E> errorMapper) {
        Objects.requireNonNull(errorMapper);
        return t -> {
            try {
                return applyAsDouble(t);
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
     * Returns a unary operator that applies this operator to its input. Any checked exception thrown by this operator is transformed using the given
     * error mapper, and the returned operator throws the transformation result.
     *
     * @param <E> The type of unchecked exception to transform to.
     * @param errorMapper The function to use to transform any checked exception thrown by this operator.
     * @return A unary operator that transforms any thrown checked exception.
     * @throws NullPointerException If {@code errorMapper} is {@code null}.
     */
    default <E extends RuntimeException> DoubleUnaryOperator onErrorThrowAsUnchecked(Function<? super X, ? extends E> errorMapper) {
        Objects.requireNonNull(errorMapper);
        return t -> {
            try {
                return applyAsDouble(t);
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
     * Returns a unary operator that applies this operator to its input. Any checked exception thrown by this operator is transformed using the given
     * error handler, and the returned operator returns the transformation result.
     *
     * @param <E> The type of checked exception that can be thrown by the given error handler.
     * @param errorHandler The function to use to transform any checked exception thrown by this operator.
     * @return A unary operator that transforms any thrown checked exception.
     * @throws NullPointerException If {@code errorHandler} is {@code null}.
     */
    default <E extends Throwable> CheckedDoubleUnaryOperator<E> onErrorHandleChecked(CheckedToDoubleFunction<? super X, ? extends E> errorHandler) {
        Objects.requireNonNull(errorHandler);
        return t -> {
            try {
                return applyAsDouble(t);
            } catch (Error | RuntimeException e) {
                throw e;
            } catch (Throwable throwable) {
                // This cast is safe, because only Error, RuntimeException (both handled above) and X can be thrown
                @SuppressWarnings("unchecked")
                X x = (X) throwable;
                return errorHandler.applyAsDouble(x);
            }
        };
    }

    /**
     * Returns a unary operator that applies this operator to its input. Any checked exception thrown by this operator is transformed using the given
     * error handler, and the returned operator returns the transformation result.
     *
     * @param errorHandler The function to use to transform any checked exception thrown by this operator.
     * @return A unary operator that transforms any thrown checked exception.
     * @throws NullPointerException If {@code errorHandler} is {@code null}.
     */
    default DoubleUnaryOperator onErrorHandleUnchecked(ToDoubleFunction<? super X> errorHandler) {
        Objects.requireNonNull(errorHandler);
        return t -> {
            try {
                return applyAsDouble(t);
            } catch (Error | RuntimeException e) {
                throw e;
            } catch (Throwable throwable) {
                // This cast is safe, because only Error, RuntimeException (both handled above) and X can be thrown
                @SuppressWarnings("unchecked")
                X x = (X) throwable;
                return errorHandler.applyAsDouble(x);
            }
        };
    }

    /**
     * Returns a unary operator that applies this operator to its input. If this operator throws any checked exception, it is discarded and the given
     * fallback operator is invoked.
     *
     * @param <E> The type of checked exception that can be thrown by the given fallback operator.
     * @param fallback The operator to invoke if this operator throws any checked exception.
     * @return A unary operator that invokes the {@code fallback} operator if this operator throws any checked exception.
     * @throws NullPointerException If {@code fallback} is {@code null}.
     */
    default <E extends Throwable> CheckedDoubleUnaryOperator<E> onErrorApplyChecked(CheckedDoubleUnaryOperator<? extends E> fallback) {
        Objects.requireNonNull(fallback);
        return t -> {
            try {
                return applyAsDouble(t);
            } catch (Error | RuntimeException e) {
                throw e;
            } catch (@SuppressWarnings("unused") Throwable throwable) {
                return fallback.applyAsDouble(t);
            }
        };
    }

    /**
     * Returns a unary operator that applies this operator to its input. If this operator throws any checked exception, it is discarded and the given
     * fallback operator is invoked.
     *
     * @param fallback The operator to invoke if this operator throws any checked exception.
     * @return A unary operator that invokes the {@code fallback} operator if this operator throws any checked exception.
     * @throws NullPointerException If {@code fallback} is {@code null}.
     */
    default DoubleUnaryOperator onErrorApplyUnchecked(DoubleUnaryOperator fallback) {
        Objects.requireNonNull(fallback);
        return t -> {
            try {
                return applyAsDouble(t);
            } catch (Error | RuntimeException e) {
                throw e;
            } catch (@SuppressWarnings("unused") Throwable throwable) {
                return fallback.applyAsDouble(t);
            }
        };
    }

    /**
     * Returns a unary operator that applies this operator to its input. If this operator throws any checked exception, it is discarded and the given
     * fallback supplier is invoked.
     *
     * @param <E> The type of checked exception that can be thrown by the given fallback supplier.
     * @param fallback The supplier to produce the value to return if this operator throws any checked exception.
     * @return A unary operator that invokes the {@code fallback} supplier if this operator throws any checked exception.
     * @throws NullPointerException If {@code fallback} is {@code null}.
     */
    default <E extends Throwable> CheckedDoubleUnaryOperator<E> onErrorGetChecked(CheckedDoubleSupplier<? extends E> fallback) {
        Objects.requireNonNull(fallback);
        return t -> {
            try {
                return applyAsDouble(t);
            } catch (Error | RuntimeException e) {
                throw e;
            } catch (@SuppressWarnings("unused") Throwable throwable) {
                return fallback.getAsDouble();
            }
        };
    }

    /**
     * Returns a unary operator that applies this operator to its input. If this operator throws any checked exception, it is discarded and the given
     * fallback supplier is invoked.
     *
     * @param fallback The supplier to produce the value to return if this operator throws any checked exception.
     * @return A unary operator that invokes the {@code fallback} supplier if this operator throws any checked exception.
     * @throws NullPointerException If {@code fallback} is {@code null}.
     */
    default DoubleUnaryOperator onErrorGetUnchecked(DoubleSupplier fallback) {
        Objects.requireNonNull(fallback);
        return t -> {
            try {
                return applyAsDouble(t);
            } catch (Error | RuntimeException e) {
                throw e;
            } catch (@SuppressWarnings("unused") Throwable throwable) {
                return fallback.getAsDouble();
            }
        };
    }

    /**
     * Returns a unary operator that applies this operator to its input. If this operator throws any checked exception, it is discarded and the given
     * fallback value is returned.
     *
     * @param fallback The value to return if this operator throws any checked exception.
     * @return A unary operator that returns the {@code fallback} value if this operator throws any checked exception.
     */
    default DoubleUnaryOperator onErrorReturn(double fallback) {
        return t -> {
            try {
                return applyAsDouble(t);
            } catch (Error | RuntimeException e) {
                throw e;
            } catch (@SuppressWarnings("unused") Throwable throwable) {
                return fallback;
            }
        };
    }

    /**
     * Returns a unary operator that applies this operator to its input. Any checked exception thrown by this operator is wrapped in an
     * {@link UncheckedException}.
     *
     * @return A unary operator that wraps any checked exception in an {@link UncheckedException}.
     */
    default DoubleUnaryOperator unchecked() {
        return onErrorThrowAsUnchecked(UncheckedException::new);
    }

    /**
     * Factory method for turning {@code CheckedDoubleUnaryOperator}-shaped lambdas into {@code CheckedDoubleUnaryOperators}.
     *
     * @param <X> The type of checked exception that can be thrown.
     * @param operator The lambda to return as {@code CheckedDoubleUnaryOperator}.
     * @return The given lambda as a {@code CheckedDoubleUnaryOperator}.
     * @throws NullPointerException If {@code operator} is {@code null}.
     */
    static <X extends Throwable> CheckedDoubleUnaryOperator<X> of(CheckedDoubleUnaryOperator<X> operator) {
        Objects.requireNonNull(operator);
        return operator;
    }

    /**
     * Returns a unary operator that always returns its input argument.
     *
     * @param <X> The type of checked exception that can be thrown.
     * @return A unary operator that always returns its input argument.
     */
    static <X extends Throwable> CheckedDoubleUnaryOperator<X> identity() {
        return t -> t;
    }

    /**
     * Returns a unary operator that applies the {@code operator} operator to its input. Any checked exception thrown by the {@code operator} operator
     * is wrapped in an {@link UncheckedException}.
     *
     * @param operator The operator to apply when the returned operator is applied.
     * @return A unary operator that wraps any checked exception in an {@link UncheckedException}.
     * @throws NullPointerException If {@code operator} is {@code null}.
     */
    static DoubleUnaryOperator unchecked(CheckedDoubleUnaryOperator<?> operator) {
        Objects.requireNonNull(operator);
        return operator.unchecked();
    }

    /**
     * Returns a unary operator that applies the {@code operator} operator to its input. Any unchecked exception thrown by the {@code operator}
     * operator is relayed to the caller. This method allows existing {@link DoubleUnaryOperator} instances to be used where
     * {@code CheckedDoubleUnaryOperator} is expected.
     *
     * @param <X> The type of checked exception that can be thrown.
     * @param operator The operator to apply when the returned operator is applied.
     * @return A unary operator that wraps any checked exception in an {@link UncheckedException}.
     * @throws NullPointerException If {@code operator} is {@code null}.
     */
    static <X extends Throwable> CheckedDoubleUnaryOperator<X> checked(DoubleUnaryOperator operator) {
        Objects.requireNonNull(operator);
        return operator::applyAsDouble;
    }

    /**
     * Returns a unary operator that applies the {@code operator} operator to its input. Any {@link UncheckedException} thrown by the {@code operator}
     * operator is unwrapped if its cause is an instance of {@code errorType}, otherwise it is relayed to the caller.
     *
     * @param <X> The type of checked exception that can be thrown.
     * @param operator The operator to apply when the returned operator is applied.
     * @param errorType The type of checked exception that can be thrown.
     * @return A unary operator that wraps any checked exception in an {@link UncheckedException}.
     * @throws NullPointerException If {@code operator} or {@code errorType} is {@code null}.
     */
    static <X extends Throwable> CheckedDoubleUnaryOperator<X> checked(DoubleUnaryOperator operator, Class<X> errorType) {
        Objects.requireNonNull(operator);
        Objects.requireNonNull(errorType);
        return t -> invokeAndUnwrap(operator, t, errorType);
    }

    /**
     * Invokes a unary operator, unwrapping any {@link UncheckedException} that is thrown if its cause if an instance of {@code errorType}.
     *
     * @param <X> The type of checked exception that can be thrown.
     * @param operator The operator to invoke.
     * @param input The input to the operator.
     * @param errorType The type of checked exception that can be thrown.
     * @return The result of invoking {@code operator}.
     * @throws NullPointerException If {@code operator} or {@code errorType} is {@code null}.
     * @throws X If {@code operator} throws an {@link UncheckedException} that wraps an instance of {@code errorType}.
     */
    static <X extends Throwable> double invokeAndUnwrap(DoubleUnaryOperator operator, double input, Class<X> errorType) throws X {
        Objects.requireNonNull(errorType);
        try {
            return operator.applyAsDouble(input);
        } catch (UncheckedException e) {
            Throwable cause = e.getCause();
            if (errorType.isInstance(cause)) {
                throw errorType.cast(cause);
            }
            throw e;
        }
    }
}
