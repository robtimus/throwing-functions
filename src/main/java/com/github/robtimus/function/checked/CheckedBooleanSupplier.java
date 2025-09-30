/*
 * CheckedBooleanSupplier.java
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
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Represents a supplier of {@code boolean}-valued results.
 * This is a checked-exception throwing equivalent of {@link BooleanSupplier}.
 *
 * @param <X> The type of checked exception that can be thrown.
 */
@FunctionalInterface
@SuppressWarnings("squid:S1181") // Error needs to be caught separately (and re-thrown) to not let it be caught as throwable
public interface CheckedBooleanSupplier<X extends Throwable> {

    /**
     * Gets a result.
     *
     * @return A result.
     * @throws X If an error occurs.
     */
    boolean getAsBoolean() throws X;

    /**
     * Returns a supplier that calls this supplier. Any checked exception thrown by this supplier is transformed using the given error mapper, and
     * the returned supplier throws the transformation result.
     *
     * @param <E> The type of checked exception to transform to.
     * @param errorMapper The function to use to transform any checked exception thrown by this supplier.
     * @return A supplier that transforms any thrown checked exception.
     * @throws NullPointerException If {@code errorMapper} is {@code null}.
     */
    default <E extends Throwable> CheckedBooleanSupplier<E> onErrorThrowAsChecked(Function<? super X, ? extends E> errorMapper) {
        Objects.requireNonNull(errorMapper);
        return () -> {
            try {
                return getAsBoolean();
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
    default <E extends RuntimeException> BooleanSupplier onErrorThrowAsUnchecked(Function<? super X, ? extends E> errorMapper) {
        Objects.requireNonNull(errorMapper);
        return () -> {
            try {
                return getAsBoolean();
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
     * @param errorHandler The function (as a predicate) to use to transform any checked exception thrown by this supplier.
     * @return A supplier that transforms any thrown checked exception.
     * @throws NullPointerException If {@code errorHandler} is {@code null}.
     */
    default <E extends Throwable> CheckedBooleanSupplier<E> onErrorHandleChecked(CheckedPredicate<? super X, ? extends E> errorHandler) {
        Objects.requireNonNull(errorHandler);
        return () -> {
            try {
                return getAsBoolean();
            } catch (Error | RuntimeException e) {
                throw e;
            } catch (Throwable throwable) {
                // This cast is safe, because only Error, RuntimeException (both handled above) and X can be thrown
                @SuppressWarnings("unchecked")
                X x = (X) throwable;
                return errorHandler.test(x);
            }
        };
    }

    /**
     * Returns a supplier that calls this supplier. Any checked exception thrown by this supplier is transformed using the given error handler, and
     * the returned supplier returns the transformation result.
     *
     * @param errorHandler The function (as a predicate) to use to transform any checked exception thrown by this supplier.
     * @return A supplier that transforms any thrown checked exception.
     * @throws NullPointerException If {@code errorHandler} is {@code null}.
     */
    default BooleanSupplier onErrorHandleUnchecked(Predicate<? super X> errorHandler) {
        Objects.requireNonNull(errorHandler);
        return () -> {
            try {
                return getAsBoolean();
            } catch (Error | RuntimeException e) {
                throw e;
            } catch (Throwable throwable) {
                // This cast is safe, because only Error, RuntimeException (both handled above) and X can be thrown
                @SuppressWarnings("unchecked")
                X x = (X) throwable;
                return errorHandler.test(x);
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
    default <E extends Throwable> CheckedBooleanSupplier<E> onErrorGetCheckedAsBoolean(CheckedBooleanSupplier<? extends E> fallback) {
        Objects.requireNonNull(fallback);
        return () -> {
            try {
                return getAsBoolean();
            } catch (Error | RuntimeException e) {
                throw e;
            } catch (@SuppressWarnings("unused") Throwable throwable) {
                return fallback.getAsBoolean();
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
    default BooleanSupplier onErrorGetUncheckedAsBoolean(BooleanSupplier fallback) {
        Objects.requireNonNull(fallback);
        return () -> {
            try {
                return getAsBoolean();
            } catch (Error | RuntimeException e) {
                throw e;
            } catch (@SuppressWarnings("unused") Throwable throwable) {
                return fallback.getAsBoolean();
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
    default BooleanSupplier onErrorReturn(boolean fallback) {
        return () -> {
            try {
                return getAsBoolean();
            } catch (Error | RuntimeException e) {
                throw e;
            } catch (@SuppressWarnings("unused") Throwable throwable) {
                return fallback;
            }
        };
    }

    /**
     * Returns a supplier that applies this supplier to its input. Any checked exception thrown by this supplier is wrapped in an
     * {@link UncheckedException}.
     *
     * @return A supplier that wraps any checked exception in an {@link UncheckedException}.
     */
    default BooleanSupplier unchecked() {
        return onErrorThrowAsUnchecked(UncheckedException::new);
    }

    /**
     * Factory method for turning {@code CheckedBooleanSupplier}-shaped lambdas into {@code CheckedBooleanSuppliers}.
     *
     * @param <X> The type of checked exception that can be thrown.
     * @param supplier The lambda to return as {@code CheckedBooleanSupplier}.
     * @return The given lambda as a {@code CheckedBooleanSupplier}.
     * @throws NullPointerException If {@code supplier} is {@code null}.
     */
    static <X extends Throwable> CheckedBooleanSupplier<X> of(CheckedBooleanSupplier<X> supplier) {
        Objects.requireNonNull(supplier);
        return supplier;
    }

    /**
     * Returns a supplier that calls the {@code supplier} supplier. Any checked exception thrown by the {@code supplier} supplier is wrapped in an
     * {@link UncheckedException}.
     *
     * @param supplier The supplier to call when the returned supplier is invoked.
     * @return A supplier that wraps any checked exception in an {@link UncheckedException}.
     * @throws NullPointerException If {@code function} is {@code null}.
     */
    static BooleanSupplier unchecked(CheckedBooleanSupplier<?> supplier) {
        Objects.requireNonNull(supplier);
        return supplier.unchecked();
    }

    /**
     * Returns a supplier that calls the {@code supplier} supplier. Any unchecked exception thrown by the {@code supplier} supplier is relayed to the
     * caller. This method allows existing {@link BooleanSupplier} instances to be used where {@code CheckedBooleanSupplier} is expected.
     *
     * @param <X> The type of checked exception that can be thrown.
     * @param supplier The supplier to call when the returned function is invoked.
     * @return A supplier that wraps any checked exception in an {@link UncheckedException}.
     * @throws NullPointerException If {@code supplier} is {@code null}.
     */
    static <X extends Throwable> CheckedBooleanSupplier<X> checked(BooleanSupplier supplier) {
        Objects.requireNonNull(supplier);
        return supplier::getAsBoolean;
    }

    /**
     * Returns a supplier that calls the {@code supplier} supplier. Any {@link UncheckedException} thrown by the {@code supplier} supplier is
     * unwrapped if its cause is an instance of {@code errorType}, otherwise it is relayed to the caller.
     *
     * @param <X> The type of checked exception that can be thrown.
     * @param supplier The supplier to call when the returned supplier is invoked.
     * @param errorType The type of checked exception that can be thrown.
     * @return A supplier that wraps any checked exception in an {@link UncheckedException}.
     * @throws NullPointerException If {@code supplier} or {@code errorType} is {@code null}.
     */
    static <X extends Throwable> CheckedBooleanSupplier<X> checked(BooleanSupplier supplier, Class<X> errorType) {
        Objects.requireNonNull(supplier);
        Objects.requireNonNull(errorType);
        return () -> invokeAndUnwrap(supplier, errorType);
    }

    /**
     * Invokes a supplier, unwrapping any {@link UncheckedException} that is thrown if its cause if an instance of {@code errorType}.
     *
     * @param <X> The type of checked exception that can be thrown.
     * @param supplier The supplier to invoke.
     * @param errorType The type of checked exception that can be thrown.
     * @return The result of invoking {@code supplier}.
     * @throws NullPointerException If {@code supplier} or {@code errorType} is {@code null}.
     * @throws X If {@code supplier} throws an {@link UncheckedException} that wraps an instance of {@code errorType}.
     */
    static <X extends Throwable> boolean invokeAndUnwrap(BooleanSupplier supplier, Class<X> errorType) throws X {
        Objects.requireNonNull(errorType);
        try {
            return supplier.getAsBoolean();
        } catch (UncheckedException e) {
            Throwable cause = e.getCause();
            if (errorType.isInstance(cause)) {
                throw errorType.cast(cause);
            }
            throw e;
        }
    }
}
