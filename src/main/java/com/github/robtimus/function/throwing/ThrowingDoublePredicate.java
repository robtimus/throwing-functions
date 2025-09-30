/*
 * ThrowingDoublePredicate.java
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
import java.util.function.BooleanSupplier;
import java.util.function.DoublePredicate;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Represents a predicate (boolean-valued function) of one {@code double}-valued argument.
 * This is a checked-exception throwing equivalent of {@link DoublePredicate}.
 *
 * @param <X> The type of checked exception that can be thrown.
 */
@FunctionalInterface
@SuppressWarnings("squid:S1181") // Error needs to be caught separately (and re-thrown) to not let it be caught as throwable
public interface ThrowingDoublePredicate<X extends Throwable> {

    /**
     * Evaluates this predicate on the given argument.
     *
     * @param value The input argument.
     * @return {@code true} if the input argument matches the predicate, otherwise {@code false}.
     * @throws X If an error occurs.
     */
    boolean test(double value) throws X;

    /**
     * Returns a composed predicate that represents a short-circuiting logical AND of this predicate and another.
     * When evaluating the composed predicate, if this predicate is {@code false}, then the {@code other} predicate is not evaluated.
     * <p>
     * Any exceptions thrown during evaluation of either predicate are relayed to the caller;
     * if evaluation of this predicate throws an exception, the {@code other} predicate will not be evaluated.
     *
     * @param other A predicate that will be logically-ANDed with this predicate.
     * @return A composed predicate that represents the short-circuiting logical AND of this predicate and the {@code other} predicate.
     * @throws NullPointerException If {@code other} is {@code null}.
     */
    default ThrowingDoublePredicate<X> and(ThrowingDoublePredicate<? extends X> other) {
        Objects.requireNonNull(other);
        return t -> test(t) && other.test(t);
    }

    /**
     * Returns a predicate that represents the logical negation of this predicate.
     *
     * @return A predicate that represents the logical negation of this predicate
     */
    default ThrowingDoublePredicate<X> negate() {
        return t -> !test(t);
    }

    /**
     * Returns a composed predicate that represents a short-circuiting logical OR of this predicate and another.
     * When evaluating the composed predicate, if this predicate is {@code true}, then the {@code other} predicate is not evaluated.
     * <p>
     * Any exceptions thrown during evaluation of either predicate are relayed to the caller;
     * if evaluation of this predicate throws an exception, the {@code other} predicate will not be evaluated.
     *
     * @param other A predicate that will be logically-ORed with this predicate
     * @return A composed predicate that represents the short-circuiting logical OR of this predicate and the {@code other} predicate.
     * @throws NullPointerException If {@code other} is {@code null}.
     */
    default ThrowingDoublePredicate<X> or(ThrowingDoublePredicate<? extends X> other) {
        Objects.requireNonNull(other);
        return t -> test(t) || other.test(t);
    }

    /**
     * Returns a predicate that evaluates this predicate on its input. Any checked exception thrown by this predicate is transformed using the given
     * error mapper, and the returned predicate throws the transformation result.
     *
     * @param <E> The type of checked exception to transform to.
     * @param errorMapper The function to use to transform any checked exception thrown by this predicate.
     * @return A predicate that transforms any thrown checked exception.
     * @throws NullPointerException If {@code errorMapper} is {@code null}.
     */
    default <E extends Throwable> ThrowingDoublePredicate<E> onErrorThrowAsChecked(Function<? super X, ? extends E> errorMapper) {
        Objects.requireNonNull(errorMapper);
        return t -> {
            try {
                return test(t);
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
     * Returns a predicate that evaluates this predicate on its input. Any checked exception thrown by this predicate is transformed using the given
     * error mapper, and the returned predicate throws the transformation result.
     *
     * @param <E> The type of unchecked exception to transform to.
     * @param errorMapper The function to use to transform any checked exception thrown by this predicate.
     * @return A predicate that transforms any thrown checked exception.
     * @throws NullPointerException If {@code errorMapper} is {@code null}.
     */
    default <E extends RuntimeException> DoublePredicate onErrorThrowAsUnchecked(Function<? super X, ? extends E> errorMapper) {
        Objects.requireNonNull(errorMapper);
        return t -> {
            try {
                return test(t);
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
     * Returns a predicate that evaluates this predicate on its input. Any checked exception thrown by this predicate is transformed using the given
     * error handler, and the returned predicate returns the transformation result.
     *
     * @param <E> The type of checked exception that can be thrown by the given error handler.
     * @param errorHandler The function (as a predicate) to use to transform any checked exception thrown by this predicate.
     * @return A predicate that transforms any thrown checked exception.
     * @throws NullPointerException If {@code errorHandler} is {@code null}.
     */
    default <E extends Throwable> ThrowingDoublePredicate<E> onErrorHandleChecked(ThrowingPredicate<? super X, ? extends E> errorHandler) {
        Objects.requireNonNull(errorHandler);
        return t -> {
            try {
                return test(t);
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
     * Returns a predicate that evaluates this predicate on its input. Any checked exception thrown by this predicate is transformed using the given
     * error handler, and the returned predicate returns the transformation result.
     *
     * @param errorHandler The function (as a predicate) to use to transform any checked exception thrown by this predicate.
     * @return A predicate that transforms any thrown checked exception.
     * @throws NullPointerException If {@code errorHandler} is {@code null}.
     */
    default DoublePredicate onErrorHandleUnchecked(Predicate<? super X> errorHandler) {
        Objects.requireNonNull(errorHandler);
        return t -> {
            try {
                return test(t);
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
     * Returns a predicate that evaluates this predicate on its input. If this predicate throws any checked exception, it is discarded and the given
     * fallback predicate is invoked.
     *
     * @param <E> The type of checked exception that can be thrown by the given fallback predicate.
     * @param fallback The predicate to invoke if this predicate throws any checked exception.
     * @return A predicate that invokes the {@code fallback} predicate if this predicate throws any checked exception.
     * @throws NullPointerException If {@code fallback} is {@code null}.
     */
    default <E extends Throwable> ThrowingDoublePredicate<E> onErrorTestChecked(ThrowingDoublePredicate<? extends E> fallback) {
        Objects.requireNonNull(fallback);
        return t -> {
            try {
                return test(t);
            } catch (Error | RuntimeException e) {
                throw e;
            } catch (@SuppressWarnings("unused") Throwable throwable) {
                return fallback.test(t);
            }
        };
    }

    /**
     * Returns a predicate that evaluates this predicate on its input. If this predicate throws any checked exception, it is discarded and the given
     * fallback predicate is invoked.
     *
     * @param fallback The predicate to invoke if this predicate throws any checked exception.
     * @return A predicate that invokes the {@code fallback} predicate if this predicate throws any checked exception.
     * @throws NullPointerException If {@code fallback} is {@code null}.
     */
    default DoublePredicate onErrorTestUnchecked(DoublePredicate fallback) {
        Objects.requireNonNull(fallback);
        return t -> {
            try {
                return test(t);
            } catch (Error | RuntimeException e) {
                throw e;
            } catch (@SuppressWarnings("unused") Throwable throwable) {
                return fallback.test(t);
            }
        };
    }

    /**
     * Returns a predicate that evaluates this predicate on its input. If this predicate throws any checked exception, it is discarded and the given
     * fallback supplier is invoked.
     *
     * @param <E> The type of checked exception that can be thrown by the given fallback supplier.
     * @param fallback The supplier to produce the value to return if this predicate throws any checked exception.
     * @return A predicate that invokes the {@code fallback} supplier if this predicate throws any checked exception.
     * @throws NullPointerException If {@code fallback} is {@code null}.
     */
    default <E extends Throwable> ThrowingDoublePredicate<E> onErrorGetCheckedAsBoolean(ThrowingBooleanSupplier<? extends E> fallback) {
        Objects.requireNonNull(fallback);
        return t -> {
            try {
                return test(t);
            } catch (Error | RuntimeException e) {
                throw e;
            } catch (@SuppressWarnings("unused") Throwable throwable) {
                return fallback.getAsBoolean();
            }
        };
    }

    /**
     * Returns a predicate that evaluates this predicate on its input. If this predicate throws any checked exception, it is discarded and the given
     * fallback supplier is invoked.
     *
     * @param fallback The supplier to produce the value to return if this predicate throws any checked exception.
     * @return A predicate that invokes the {@code fallback} supplier if this predicate throws any checked exception.
     * @throws NullPointerException If {@code fallback} is {@code null}.
     */
    default DoublePredicate onErrorGetUncheckedAsBoolean(BooleanSupplier fallback) {
        Objects.requireNonNull(fallback);
        return t -> {
            try {
                return test(t);
            } catch (Error | RuntimeException e) {
                throw e;
            } catch (@SuppressWarnings("unused") Throwable throwable) {
                return fallback.getAsBoolean();
            }
        };
    }

    /**
     * Returns a predicate that evaluates this predicate on its input. If this predicate throws any checked exception, it is discarded and the given
     * fallback value is returned.
     *
     * @param fallback The value to return if this predicate throws any checked exception.
     * @return A predicate that returns the {@code fallback} value if this predicate throws any checked exception.
     */
    default DoublePredicate onErrorReturn(boolean fallback) {
        return t -> {
            try {
                return test(t);
            } catch (Error | RuntimeException e) {
                throw e;
            } catch (@SuppressWarnings("unused") Throwable throwable) {
                return fallback;
            }
        };
    }

    /**
     * Returns a predicate that evaluates this predicate on its input. Any checked exception thrown by this predicate is wrapped in an
     * {@link UncheckedException}.
     *
     * @return A predicate that wraps any checked exception in an {@link UncheckedException}.
     */
    default DoublePredicate unchecked() {
        return onErrorThrowAsUnchecked(UncheckedException::new);
    }

    /**
     * Factory method for turning {@code ThrowingDoublePredicate}-shaped lambdas into {@code ThrowingDoublePredicates}.
     *
     * @param <X> The type of checked exception that can be thrown.
     * @param predicate The lambda to return as {@code ThrowingDoublePredicate}.
     * @return The given lambda as a {@code ThrowingDoublePredicate}.
     * @throws NullPointerException If {@code predicate} is {@code null}.
     */
    static <X extends Throwable> ThrowingDoublePredicate<X> of(ThrowingDoublePredicate<X> predicate) {
        Objects.requireNonNull(predicate);
        return predicate;
    }

    /**
     * Returns a predicate that is the negation of the supplied predicate.
     *
     * @param <X> The type of checked exception that can be thrown.
     * @param predicate The predicate to negate.
     * @return A predicate that negates the results of {@code predicate}.
     * @throws NullPointerException If {@code predicate} is {@code null}.
     */
    @SuppressWarnings("unchecked")
    static <X extends Throwable> ThrowingDoublePredicate<X> not(ThrowingDoublePredicate<? extends X> predicate) {
        Objects.requireNonNull(predicate);
        return (ThrowingDoublePredicate<X>) predicate.negate();
    }

    /**
     * Returns a predicate that evaluates the {@code predicate} predicate on its input. Any checked exception thrown by the {@code predicate}
     * predicate is wrapped in an {@link UncheckedException}.
     *
     * @param predicate The predicate to evaluate when the returned predicate is evaluated.
     * @return A predicate that wraps any checked exception in an {@link UncheckedException}.
     * @throws NullPointerException If {@code predicate} is {@code null}.
     */
    static DoublePredicate unchecked(ThrowingDoublePredicate<?> predicate) {
        Objects.requireNonNull(predicate);
        return predicate.unchecked();
    }

    /**
     * Returns a predicate that evaluates the {@code predicate} predicate on its input. Any unchecked exception thrown by the {@code predicate}
     * predicate is relayed to the caller. This method allows existing {@link DoublePredicate} instances to be used where
     * {@code ThrowingDoublePredicate} is expected.
     *
     * @param <X> The type of checked exception that can be thrown.
     * @param predicate The predicate to evaluate when the returned predicate is evaluated.
     * @return A predicate that wraps any checked exception in an {@link UncheckedException}.
     * @throws NullPointerException If {@code predicate} is {@code null}.
     */
    static <X extends Throwable> ThrowingDoublePredicate<X> checked(DoublePredicate predicate) {
        Objects.requireNonNull(predicate);
        return predicate::test;
    }

    /**
     * Returns a predicate that evaluates the {@code predicate} predicate on its input. Any {@link UncheckedException} thrown by the {@code predicate}
     * predicate is unwrapped if its cause is an instance of {@code errorType}, otherwise it is relayed to the caller.
     *
     * @param <X> The type of checked exception that can be thrown.
     * @param predicate The predicate to evaluate when the returned predicate is evaluated.
     * @param errorType The type of checked exception that can be thrown.
     * @return A predicate that wraps any checked exception in an {@link UncheckedException}.
     * @throws NullPointerException If {@code predicate} or {@code errorType} is {@code null}.
     */
    static <X extends Throwable> ThrowingDoublePredicate<X> checked(DoublePredicate predicate, Class<X> errorType) {
        Objects.requireNonNull(predicate);
        Objects.requireNonNull(errorType);
        return t -> invokeAndUnwrap(predicate, t, errorType);
    }

    /**
     * Invokes a predicate, unwrapping any {@link UncheckedException} that is thrown if its cause if an instance of {@code errorType}.
     *
     * @param <X> The type of checked exception that can be thrown.
     * @param predicate The predicate to invoke.
     * @param input The input to the predicate.
     * @param errorType The type of checked exception that can be thrown.
     * @return The result of invoking {@code predicate}.
     * @throws NullPointerException If {@code predicate} or {@code errorType} is {@code null}.
     * @throws X If {@code predicate} throws an {@link UncheckedException} that wraps an instance of {@code errorType}.
     */
    static <X extends Throwable> boolean invokeAndUnwrap(DoublePredicate predicate, double input, Class<X> errorType) throws X {
        Objects.requireNonNull(errorType);
        try {
            return predicate.test(input);
        } catch (UncheckedException e) {
            Throwable cause = e.getCause();
            if (errorType.isInstance(cause)) {
                throw errorType.cast(cause);
            }
            throw e;
        }
    }
}
