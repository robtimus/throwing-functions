/*
 * ThrowingIntConsumer.java
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
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntConsumer;

/**
 * Represents an operation that accepts a single {@code int}-valued argument and returns no result.
 * This is a checked-exception throwing equivalent of {@link IntConsumer}.
 *
 * @param <X> The type of checked exception that can be thrown.
 */
@FunctionalInterface
@SuppressWarnings("squid:S1181") // Error needs to be caught separately (and re-thrown) to not let it be caught as throwable
public interface ThrowingIntConsumer<X extends Throwable> {

    /**
     * Performs this operation on the given argument.
     *
     * @param value The input argument.
     * @throws X If an error occurs.
     */
    void accept(int value) throws X;

    /**
     * Returns a composed {@code ThrowingIntConsumer} that performs, in sequence, this operation followed by the {@code after} operation.
     * If performing either operation throws an exception, it is relayed to the caller of the composed operation.
     * If performing this operation throws an exception, the {@code after} operation will not be performed.
     *
     * @param after The operation to perform after this operation.
     * @return A composed {@code ThrowingIntConsumer} that performs in sequence this operation followed by the {@code after} operation.
     * @throws NullPointerException If {@code after} is {@code null}.
     */
    default ThrowingIntConsumer<X> andThen(ThrowingIntConsumer<? extends X> after) {
        Objects.requireNonNull(after);
        return t -> {
            accept(t);
            after.accept(t);
        };
    }

    /**
     * Returns an operation that performs this operation on its input. Any checked exception thrown by this operation is transformed using the given
     * error mapper, and the returned operation throws the transformation result.
     *
     * @param <E> The type of checked exception to transform to.
     * @param errorMapper The function to use to transform any checked exception thrown by this operation.
     * @return An operation that transforms any thrown checked exception.
     * @throws NullPointerException If {@code errorMapper} is {@code null}.
     */
    default <E extends Throwable> ThrowingIntConsumer<E> onErrorThrowAsChecked(Function<? super X, ? extends E> errorMapper) {
        Objects.requireNonNull(errorMapper);
        return t -> {
            try {
                accept(t);
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
     * Returns an operation that performs this operation on its input. Any checked exception thrown by this operation is transformed using the given
     * error mapper, and the returned operation throws the transformation result.
     *
     * @param <E> The type of unchecked exception to transform to.
     * @param errorMapper The function to use to transform any checked exception thrown by this operation.
     * @return An operation that transforms any thrown checked exception.
     * @throws NullPointerException If {@code errorMapper} is {@code null}.
     */
    default <E extends RuntimeException> IntConsumer onErrorThrowAsUnchecked(Function<? super X, ? extends E> errorMapper) {
        Objects.requireNonNull(errorMapper);
        return t -> {
            try {
                accept(t);
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
     * Returns an operation that performs this operation on its input. Any checked exception thrown by this operation is handled by the given error
     * handler.
     *
     * @param <E> The type of checked exception that can be thrown by the given error handler.
     * @param errorHandler The operation to perform on any checked exception thrown by this operation.
     * @return An operation that handles any thrown checked exception.
     * @throws NullPointerException If {@code errorHandler} is {@code null}.
     */
    default <E extends Throwable> ThrowingIntConsumer<E> onErrorHandleChecked(ThrowingConsumer<? super X, ? extends E> errorHandler) {
        Objects.requireNonNull(errorHandler);
        return t -> {
            try {
                accept(t);
            } catch (Error | RuntimeException e) {
                throw e;
            } catch (Throwable throwable) {
                // This cast is safe, because only Error, RuntimeException (both handled above) and X can be thrown
                @SuppressWarnings("unchecked")
                X x = (X) throwable;
                errorHandler.accept(x);
            }
        };
    }

    /**
     * Returns an operation that performs this operation on its input. Any checked exception thrown by this operation is handled by the given error
     * handler.
     *
     * @param errorHandler The operation to perform on any checked exception thrown by this operation.
     * @return An operation that handles any thrown checked exception.
     * @throws NullPointerException If {@code errorHandler} is {@code null}.
     */
    default IntConsumer onErrorHandleUnchecked(Consumer<? super X> errorHandler) {
        Objects.requireNonNull(errorHandler);
        return t -> {
            try {
                accept(t);
            } catch (Error | RuntimeException e) {
                throw e;
            } catch (Throwable throwable) {
                // This cast is safe, because only Error, RuntimeException (both handled above) and X can be thrown
                @SuppressWarnings("unchecked")
                X x = (X) throwable;
                errorHandler.accept(x);
            }
        };
    }

    /**
     * Returns an operation that performs this operation on its input. If this operation throws any checked exception, it is discarded and the given
     * fallback operation is invoked.
     *
     * @param <E> The type of checked exception that can be thrown by the given fallback operation.
     * @param fallback The operation to invoke if this operation throws any checked exception.
     * @return An operation that invokes the {@code fallback} operation if this operation throws any checked exception.
     * @throws NullPointerException If {@code fallback} is {@code null}.
     */
    default <E extends Throwable> ThrowingIntConsumer<E> onErrorAcceptChecked(ThrowingIntConsumer<? extends E> fallback) {
        Objects.requireNonNull(fallback);
        return t -> {
            try {
                accept(t);
            } catch (Error | RuntimeException e) {
                throw e;
            } catch (@SuppressWarnings("unused") Throwable throwable) {
                fallback.accept(t);
            }
        };
    }

    /**
     * Returns an operation that performs this operation on its input. If this operation throws any checked exception, it is discarded and the given
     * fallback operation is invoked.
     *
     * @param fallback The operation to invoke if this operation throws any checked exception.
     * @return An operation that invokes the {@code fallback} operation if this operation throws any checked exception.
     * @throws NullPointerException If {@code fallback} is {@code null}.
     */
    default IntConsumer onErrorAcceptUnchecked(IntConsumer fallback) {
        Objects.requireNonNull(fallback);
        return t -> {
            try {
                accept(t);
            } catch (Error | RuntimeException e) {
                throw e;
            } catch (@SuppressWarnings("unused") Throwable throwable) {
                fallback.accept(t);
            }
        };
    }

    /**
     * Returns an operation that performs this operation on its input. Any checked exception thrown by this operation is discarded.
     *
     * @return An operation that discards any thrown checked exception.
     */
    default IntConsumer onErrorDiscard() {
        return t -> {
            try {
                accept(t);
            } catch (Error | RuntimeException e) {
                throw e;
            } catch (@SuppressWarnings("unused") Throwable throwable) {
                // discard
            }
        };
    }

    /**
     * Returns an operation that performs this operation on its input. Any checked exception thrown by this operation is wrapped in an
     * {@link UncheckedException} {@linkplain UncheckedException#withoutStackTrace(Throwable) without a stack trace}.
     *
     * @return An operation that wraps any checked exception in an {@link UncheckedException}.
     */
    default IntConsumer unchecked() {
        return onErrorThrowAsUnchecked(UncheckedException::withoutStackTrace);
    }

    /**
     * Factory method for turning {@code ThrowingIntConsumer}-shaped lambdas into {@code ThrowingIntConsumers}.
     *
     * @param <X> The type of checked exception that can be thrown.
     * @param operation The lambda to return as {@code ThrowingIntConsumer}.
     * @return The given lambda as a {@code ThrowingIntConsumer}.
     * @throws NullPointerException If {@code operation} is {@code null}.
     */
    static <X extends Throwable> ThrowingIntConsumer<X> of(ThrowingIntConsumer<X> operation) {
        Objects.requireNonNull(operation);
        return operation;
    }

    /**
     * Returns an operation that performs the {@code operation} operation to its input. Any checked exception thrown by the {@code operation}
     * operation is wrapped in an {@link UncheckedException}.
     *
     * @param operation The operation to perform when the returned operation is performed.
     * @return An operation that wraps any checked exception in an {@link UncheckedException}.
     * @throws NullPointerException If {@code operation} is {@code null}.
     */
    static IntConsumer unchecked(ThrowingIntConsumer<?> operation) {
        Objects.requireNonNull(operation);
        return operation.unchecked();
    }

    /**
     * Returns an operation that performs the {@code operation} operation to its input. Any checked exception thrown by the {@code operation}
     * operation is relayed to the caller. This method allows existing {@link IntConsumer} instances to be used where {@code ThrowingIntConsumer}
     * is expected.
     *
     * @param <X> The type of checked exception that can be thrown.
     * @param operation The operation to perform when the returned operation is performed.
     * @return An operation that wraps any checked exception in an {@link UncheckedException}.
     * @throws NullPointerException If {@code operation} is {@code null}.
     */
    static <X extends Throwable> ThrowingIntConsumer<X> checked(IntConsumer operation) {
        Objects.requireNonNull(operation);
        return operation::accept;
    }

    /**
     * Returns an operation that performs the {@code operation} operation to its input. Any {@link UncheckedException} thrown by the {@code operation}
     * operation is unwrapped if its cause is an instance of {@code errorType}, otherwise it is relayed to the caller.
     *
     * @param <X> The type of checked exception that can be thrown.
     * @param operation The operation to perform when the returned operation is performed.
     * @param errorType The type of checked exception that can be thrown.
     * @return An operation that wraps any checked exception in an {@link UncheckedException}.
     * @throws NullPointerException If {@code operation} or {@code errorType} is {@code null}.
     */
    static <X extends Throwable> ThrowingIntConsumer<X> checked(IntConsumer operation, Class<X> errorType) {
        Objects.requireNonNull(operation);
        Objects.requireNonNull(errorType);
        return t -> {
            try {
                operation.accept(t);
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
