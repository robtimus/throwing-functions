/*
 * ThrowingBiConsumer.java
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
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Represents an operation that accepts two input arguments and returns no result.
 * This is a checked-exception throwing equivalent of {@link BiConsumer}.
 *
 * @param <T> The type of the first argument to the operation.
 * @param <U> The type of the second argument to the operation.
 * @param <X> The type of checked exception that can be thrown.
 */
@FunctionalInterface
@SuppressWarnings("squid:S1181") // Error needs to be caught separately (and re-thrown) to not let it be caught as throwable
public interface ThrowingBiConsumer<T, U, X extends Throwable> {

    /**
     * Performs this operation on the given arguments.
     *
     * @param t The first input argument.
     * @param u The second input argument.
     * @throws X If an error occurs.
     */
    void accept(T t, U u) throws X;

    /**
     * Returns a composed {@code ThrowingBiConsumer} that performs, in sequence, this operation followed by the {@code after} operation.
     * If performing either operation throws an exception, it is relayed to the caller of the composed operation.
     * If performing this operation throws an exception, the {@code after} operation will not be performed.
     *
     * @param after The operation to perform after this operation.
     * @return A composed {@code ThrowingBiConsumer} that performs in sequence this operation followed by the {@code after} operation.
     * @throws NullPointerException If {@code after} is {@code null}.
     */
    default ThrowingBiConsumer<T, U, X> andThen(ThrowingBiConsumer<? super T, ? super U, ? extends X> after) {
        Objects.requireNonNull(after);
        return (t, u) -> {
            accept(t, u);
            after.accept(t, u);
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
    default <E extends Throwable> ThrowingBiConsumer<T, U, E> onErrorThrowAsChecked(Function<? super X, ? extends E> errorMapper) {
        Objects.requireNonNull(errorMapper);
        return (t, u) -> {
            try {
                accept(t, u);
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
    default <E extends RuntimeException> BiConsumer<T, U> onErrorThrowAsUnchecked(Function<? super X, ? extends E> errorMapper) {
        Objects.requireNonNull(errorMapper);
        return (t, u) -> {
            try {
                accept(t, u);
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
    default <E extends Throwable> ThrowingBiConsumer<T, U, E> onErrorHandleChecked(ThrowingConsumer<? super X, ? extends E> errorHandler) {
        Objects.requireNonNull(errorHandler);
        return (t, u) -> {
            try {
                accept(t, u);
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
    default BiConsumer<T, U> onErrorHandleUnchecked(Consumer<? super X> errorHandler) {
        Objects.requireNonNull(errorHandler);
        return (t, u) -> {
            try {
                accept(t, u);
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
    default <E extends Throwable> ThrowingBiConsumer<T, U, E> onErrorAcceptChecked(ThrowingBiConsumer<? super T, ? super U, ? extends E> fallback) {
        Objects.requireNonNull(fallback);
        return (t, u) -> {
            try {
                accept(t, u);
            } catch (Error | RuntimeException e) {
                throw e;
            } catch (@SuppressWarnings("unused") Throwable throwable) {
                fallback.accept(t, u);
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
    default BiConsumer<T, U> onErrorAcceptUnchecked(BiConsumer<? super T, ? super U> fallback) {
        Objects.requireNonNull(fallback);
        return (t, u) -> {
            try {
                accept(t, u);
            } catch (Error | RuntimeException e) {
                throw e;
            } catch (@SuppressWarnings("unused") Throwable throwable) {
                fallback.accept(t, u);
            }
        };
    }

    /**
     * Returns an operation that performs this operation on its input. Any checked exception thrown by this operation is discarded.
     *
     * @return An operation that discards any thrown checked exception.
     */
    default BiConsumer<T, U> onErrorDiscard() {
        return (t, u) -> {
            try {
                accept(t, u);
            } catch (Error | RuntimeException e) {
                throw e;
            } catch (@SuppressWarnings("unused") Throwable throwable) {
                // discard
            }
        };
    }

    /**
     * Returns an operation that performs this operation on its input. Any checked exception thrown by this operation is wrapped in an
     * {@link UncheckedException}.
     *
     * @return An operation that wraps any checked exception in an {@link UncheckedException}.
     */
    default BiConsumer<T, U> unchecked() {
        return onErrorThrowAsUnchecked(UncheckedException::new);
    }

    /**
     * Factory method for turning {@code ThrowingBiConsumer}-shaped lambdas into {@code ThrowingBiConsumers}.
     *
     * @param <T> The type of the first argument to the operation.
     * @param <U> The type of the second argument to the operation.
     * @param <X> The type of checked exception that can be thrown.
     * @param operation The lambda to return as {@code ThrowingBiConsumer}.
     * @return The given lambda as a {@code ThrowingBiConsumer}.
     * @throws NullPointerException If {@code operation} is {@code null}.
     */
    static <T, U, X extends Throwable> ThrowingBiConsumer<T, U, X> of(ThrowingBiConsumer<T, U, X> operation) {
        Objects.requireNonNull(operation);
        return operation;
    }

    /**
     * Returns an operation that performs the {@code operation} operation to its input. Any checked exception thrown by the {@code operation}
     * operation is wrapped in an {@link UncheckedException}.
     *
     * @param <T> The type of the first argument to the operation.
     * @param <U> The type of the second argument to the operation.
     * @param operation The operation to perform when the returned operation is performed.
     * @return An operation that wraps any checked exception in an {@link UncheckedException}.
     * @throws NullPointerException If {@code operation} is {@code null}.
     */
    @SuppressWarnings("unchecked")
    static <T, U> BiConsumer<T, U> unchecked(ThrowingBiConsumer<? super T, ? super U, ?> operation) {
        Objects.requireNonNull(operation);
        return (BiConsumer<T, U>) operation.unchecked();
    }

    /**
     * Returns an operation that performs the {@code operation} operation to its input. Any checked exception thrown by the {@code operation}
     * operation is relayed to the caller. This method allows existing {@link BiConsumer} instances to be used where {@code ThrowingBiConsumer} is
     * expected.
     *
     * @param <T> The type of the first argument to the operation.
     * @param <U> The type of the second argument to the operation.
     * @param <X> The type of checked exception that can be thrown.
     * @param operation The operation to perform when the returned operation is performed.
     * @return An operation that wraps any checked exception in an {@link UncheckedException}.
     * @throws NullPointerException If {@code operation} is {@code null}.
     */
    static <T, U, X extends Throwable> ThrowingBiConsumer<T, U, X> checked(BiConsumer<? super T, ? super U> operation) {
        Objects.requireNonNull(operation);
        return operation::accept;
    }

    /**
     * Returns an operation that performs the {@code operation} operation to its input. Any {@link UncheckedException} thrown by the {@code operation}
     * operation is unwrapped if its cause is an instance of {@code errorType}, otherwise it is relayed to the caller.
     *
     * @param <T> The type of the first argument to the operation.
     * @param <U> The type of the second argument to the operation.
     * @param <X> The type of checked exception that can be thrown.
     * @param operation The operation to perform when the returned operation is performed.
     * @param errorType The type of checked exception that can be thrown.
     * @return An operation that wraps any checked exception in an {@link UncheckedException}.
     * @throws NullPointerException If {@code operation} or {@code errorType} is {@code null}.
     */
    static <T, U, X extends Throwable> ThrowingBiConsumer<T, U, X> checked(BiConsumer<? super T, ? super U> operation, Class<X> errorType) {
        Objects.requireNonNull(operation);
        Objects.requireNonNull(errorType);
        return (t, u) -> invokeAndUnwrap(operation, t, u, errorType);
    }

    /**
     * Invokes an operation, unwrapping any {@link UncheckedException} that is thrown if its cause if an instance of {@code errorType}.
     *
     * @param <T> The type of the first argument to the operation.
     * @param <U> The type of the second argument to the operation.
     * @param <X> The type of checked exception that can be thrown.
     * @param operation The operation to invoke.
     * @param input1 The first input to the operation.
     * @param input2 The second input to the operation.
     * @param errorType The type of checked exception that can be thrown.
     * @throws NullPointerException If {@code operation} or {@code errorType} is {@code null}.
     * @throws X If {@code operation} throws an {@link UncheckedException} that wraps an instance of {@code errorType}.
     */
    static <T, U, X extends Throwable> void invokeAndUnwrap(BiConsumer<? super T, ? super U> operation, T input1, U input2, Class<X> errorType)
            throws X {

        Objects.requireNonNull(errorType);
        try {
            operation.accept(input1, input2);
        } catch (UncheckedException e) {
            Throwable cause = e.getCause();
            if (errorType.isInstance(cause)) {
                throw errorType.cast(cause);
            }
            throw e;
        }
    }
}
