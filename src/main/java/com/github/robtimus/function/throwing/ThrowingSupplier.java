/*
 * ThrowingSupplier.java
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
import java.util.function.Supplier;

/**
 * Represents a supplier of results.
 * This is a checked-exception throwing equivalent of {@link Supplier}.
 *
 * @param <T> The type of results supplied by this supplier.
 * @param <X> The type of checked exception that can be thrown.
 */
@FunctionalInterface
@SuppressWarnings("squid:S1181") // Error needs to be caught separately (and re-thrown) to not let it be caught as throwable
public interface ThrowingSupplier<T, X extends Throwable> {

    /**
     * Gets a result.
     *
     * @return A result.
     * @throws X If an error occurs.
     */
    T get() throws X;

    /**
     * Returns a supplier that calls this supplier. Any checked exception thrown by this supplier is transformed using the given error mapper, and
     * the returned supplier throws the transformation result.
     *
     * @param <E> The type of checked exception to transform to.
     * @param errorMapper The function to use to transform any checked exception thrown by this supplier.
     * @return A supplier that transforms any thrown checked exception.
     * @throws NullPointerException If {@code errorMapper} is {@code null}.
     */
    default <E extends Throwable> ThrowingSupplier<T, E> onErrorThrowAsChecked(Function<? super X, ? extends E> errorMapper) {
        Objects.requireNonNull(errorMapper);
        return () -> {
            try {
                return get();
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
     * Returns a supplier that calls this supplier. Any checked exception thrown by this supplier is transformed using the given error mapper, and
     * the returned supplier throws the transformation result.
     *
     * @param <E> The type of unchecked exception to transform to.
     * @param errorMapper The function to use to transform any checked exception thrown by this supplier.
     * @return A supplier that transforms any thrown checked exception.
     * @throws NullPointerException If {@code errorMapper} is {@code null}.
     */
    default <E extends RuntimeException> Supplier<T> onErrorThrowAsUnchecked(Function<? super X, ? extends E> errorMapper) {
        Objects.requireNonNull(errorMapper);
        return () -> {
            try {
                return get();
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
     * Returns a supplier that calls this supplier. Any checked exception thrown by this supplier is transformed using the given error handler, and
     * the returned supplier returns the transformation result.
     *
     * @param <E> The type of checked exception that can be thrown by the given error handler.
     * @param errorHandler The function to use to transform any checked exception thrown by this supplier.
     * @return A supplier that transforms any thrown checked exception.
     * @throws NullPointerException If {@code errorHandler} is {@code null}.
     */
    default <E extends Throwable> ThrowingSupplier<T, E> onErrorHandleChecked(ThrowingFunction<? super X, ? extends T, ? extends E> errorHandler) {
        Objects.requireNonNull(errorHandler);
        return () -> {
            try {
                return get();
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

    /**
     * Returns a supplier that calls this supplier. Any checked exception thrown by this supplier is transformed using the given error handler, and
     * the returned supplier returns the transformation result.
     *
     * @param errorHandler The function to use to transform any checked exception thrown by this supplier.
     * @return A supplier that transforms any thrown checked exception.
     * @throws NullPointerException If {@code errorHandler} is {@code null}.
     */
    default Supplier<T> onErrorHandleUnchecked(Function<? super X, ? extends T> errorHandler) {
        Objects.requireNonNull(errorHandler);
        return () -> {
            try {
                return get();
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

    /**
     * Returns a supplier that calls this supplier. If this supplier throws any checked exception, it is discarded and the given fallback supplier is
     * invoked.
     *
     * @param <E> The type of checked exception that can be thrown by the given fallback supplier.
     * @param fallback The supplier to produce the value to return if this supplier throws any checked exception.
     * @return A supplier that invokes the {@code fallback} supplier if this supplier throws any checked exception.
     * @throws NullPointerException If {@code fallback} is {@code null}.
     */
    default <E extends Throwable> ThrowingSupplier<T, E> onErrorGetChecked(ThrowingSupplier<? extends T, ? extends E> fallback) {
        Objects.requireNonNull(fallback);
        return () -> {
            try {
                return get();
            } catch (Error | RuntimeException e) {
                throw e;
            } catch (@SuppressWarnings("unused") Throwable throwable) {
                return fallback.get();
            }
        };
    }

    /**
     * Returns a supplier that calls this supplier. If this supplier throws any checked exception, it is discarded and the given fallback supplier is
     * invoked.
     *
     * @param fallback The supplier to produce the value to return if this supplier throws any checked exception.
     * @return A supplier that invokes the {@code fallback} supplier if this supplier throws any checked exception.
     * @throws NullPointerException If {@code fallback} is {@code null}.
     */
    default Supplier<T> onErrorGetUnchecked(Supplier<? extends T> fallback) {
        Objects.requireNonNull(fallback);
        return () -> {
            try {
                return get();
            } catch (Error | RuntimeException e) {
                throw e;
            } catch (@SuppressWarnings("unused") Throwable throwable) {
                return fallback.get();
            }
        };
    }

    /**
     * Returns a supplier that calls this supplier. If this supplier throws any checked exception, it is discarded and the given fallback value is
     * returned.
     *
     * @param fallback The value to return if this supplier throws any checked exception.
     * @return A supplier that returns the {@code fallback} value if this supplier throws any checked exception.
     */
    default Supplier<T> onErrorReturn(T fallback) {
        return () -> {
            try {
                return get();
            } catch (Error | RuntimeException e) {
                throw e;
            } catch (@SuppressWarnings("unused") Throwable throwable) {
                return fallback;
            }
        };
    }

    /**
     * Returns a supplier that applies this supplier to its input. Any checked exception thrown by this supplier is wrapped in an
     * {@link UncheckedException} {@linkplain UncheckedException#withoutStackTrace(Throwable) without a stack trace}.
     *
     * @return A supplier that wraps any checked exception in an {@link UncheckedException}.
     */
    default Supplier<T> unchecked() {
        return onErrorThrowAsUnchecked(UncheckedException::withoutStackTrace);
    }

    /**
     * Factory method for turning {@code ThrowingSupplier}-shaped lambdas into {@code ThrowingSuppliers}.
     *
     * @param <T> The type of the results supplied by the supplier.
     * @param <X> The type of checked exception that can be thrown.
     * @param supplier The lambda to return as {@code ThrowingSupplier}.
     * @return The given lambda as a {@code ThrowingSupplier}.
     * @throws NullPointerException If {@code supplier} is {@code null}.
     */
    static <T, X extends Throwable> ThrowingSupplier<T, X> of(ThrowingSupplier<T, X> supplier) {
        Objects.requireNonNull(supplier);
        return supplier;
    }

    /**
     * Returns a supplier that calls the {@code supplier} supplier. Any checked exception thrown by the {@code supplier} supplier is wrapped in an
     * {@link UncheckedException}.
     *
     * @param <T> The type of the results supplied by the supplier.
     * @param supplier The supplier to call when the returned supplier is invoked.
     * @return A supplier that wraps any checked exception in an {@link UncheckedException}.
     * @throws NullPointerException If {@code function} is {@code null}.
     */
    @SuppressWarnings("unchecked")
    static <T> Supplier<T> unchecked(ThrowingSupplier<? extends T, ?> supplier) {
        Objects.requireNonNull(supplier);
        return (Supplier<T>) supplier.unchecked();
    }

    /**
     * Returns a supplier that calls the {@code supplier} supplier. Any unchecked exception thrown by the {@code supplier} supplier is relayed to the
     * caller. This method allows existing {@link Supplier} instances to be used where {@code ThrowingSupplier} is expected.
     *
     * @param <T> The type of the results supplied by the supplier.
     * @param <X> The type of checked exception that can be thrown.
     * @param supplier The supplier to call when the returned function is invoked.
     * @return A supplier that wraps any checked exception in an {@link UncheckedException}.
     * @throws NullPointerException If {@code supplier} is {@code null}.
     */
    static <T, X extends Throwable> ThrowingSupplier<T, X> checked(Supplier<? extends T> supplier) {
        Objects.requireNonNull(supplier);
        return supplier::get;
    }

    /**
     * Returns a supplier that calls the {@code supplier} supplier. Any {@link UncheckedException} thrown by the {@code supplier} supplier is
     * unwrapped if its cause is an instance of {@code errorType}, otherwise it is relayed to the caller.
     *
     * @param <T> The type of the results supplied by the supplier.
     * @param <X> The type of checked exception that can be thrown.
     * @param supplier The supplier to call when the returned supplier is invoked.
     * @param errorType The type of checked exception that can be thrown.
     * @return A supplier that wraps any checked exception in an {@link UncheckedException}.
     * @throws NullPointerException If {@code supplier} or {@code errorType} is {@code null}.
     */
    static <T, X extends Throwable> ThrowingSupplier<T, X> checked(Supplier<? extends T> supplier, Class<X> errorType) {
        Objects.requireNonNull(supplier);
        Objects.requireNonNull(errorType);
        return () -> invokeAndUnwrap(supplier, errorType);
    }

    /**
     * Invokes a supplier, unwrapping any {@link UncheckedException} that is thrown if its cause if an instance of {@code errorType}.
     *
     * @param <T> The type of the results supplied by the supplier.
     * @param <X> The type of checked exception that can be thrown.
     * @param supplier The supplier to invoke.
     * @param errorType The type of checked exception that can be thrown.
     * @return The result of invoking {@code supplier}.
     * @throws NullPointerException If {@code supplier} or {@code errorType} is {@code null}.
     * @throws X If {@code supplier} throws an {@link UncheckedException} that wraps an instance of {@code errorType}.
     */
    static <T, X extends Throwable> T invokeAndUnwrap(Supplier<? extends T> supplier, Class<X> errorType) throws X {
        Objects.requireNonNull(errorType);
        try {
            return supplier.get();
        } catch (UncheckedException e) {
            Throwable cause = e.getCause();
            if (errorType.isInstance(cause)) {
                throw errorType.cast(cause);
            }
            throw e;
        }
    }
}
