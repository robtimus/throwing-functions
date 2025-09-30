/*
 * ThrowingRunnable.java
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

/**
 * Represents a task that accepts no input and returns no result.
 * This is a checked-exception throwing equivalent of {@link Runnable}.
 *
 * @param <X> The type of checked exception that can be thrown.
 */
@FunctionalInterface
@SuppressWarnings("squid:S1181") // Error needs to be caught separately (and re-thrown) to not let it be caught as throwable
public interface ThrowingRunnable<X extends Throwable> {

    /**
     * Performs this task.
     *
     * @throws X If an error occurs.
     */
    void run() throws X;

    /**
     * Returns a task that performs this task. Any checked exception thrown by this task is transformed using the given error mapper, and the returned
     * task throws the transformation result.
     *
     * @param <E> The type of checked exception to transform to.
     * @param errorMapper The function to use to transform any checked exception thrown by this task.
     * @return A task that transforms any thrown checked exception.
     * @throws NullPointerException If {@code errorMapper} is {@code null}.
     */
    default <E extends Throwable> ThrowingRunnable<E> onErrorThrowAsChecked(Function<? super X, ? extends E> errorMapper) {
        Objects.requireNonNull(errorMapper);
        return () -> {
            try {
                run();
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
     * Returns a task that performs this task. Any checked exception thrown by this task is transformed using the given error mapper, and the returned
     * task throws the transformation result.
     *
     * @param <E> The type of unchecked exception to transform to.
     * @param errorMapper The function to use to transform any checked exception thrown by this task.
     * @return A task that transforms any thrown checked exception.
     * @throws NullPointerException If {@code errorMapper} is {@code null}.
     */
    default <E extends RuntimeException> Runnable onErrorThrowAsUnchecked(Function<? super X, ? extends E> errorMapper) {
        Objects.requireNonNull(errorMapper);
        return () -> {
            try {
                run();
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
     * Returns a task that performs this task. Any checked exception thrown by this task is handled by the given error handler.
     *
     * @param <E> The type of checked exception that can be thrown by the given error handler.
     * @param errorHandler The operation to perform on any checked exception thrown by this task.
     * @return A task that handles any thrown checked exception.
     * @throws NullPointerException If {@code errorHandler} is {@code null}.
     */
    default <E extends Throwable> ThrowingRunnable<E> onErrorHandleChecked(ThrowingConsumer<? super X, ? extends E> errorHandler) {
        Objects.requireNonNull(errorHandler);
        return () -> {
            try {
                run();
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
     * Returns a task that performs this task. Any checked exception thrown by this task is handled by the given error handler.
     *
     * @param errorHandler The operation to perform on any checked exception thrown by this task.
     * @return A task that handles any thrown checked exception.
     * @throws NullPointerException If {@code errorHandler} is {@code null}.
     */
    default Runnable onErrorHandleUnchecked(Consumer<? super X> errorHandler) {
        Objects.requireNonNull(errorHandler);
        return () -> {
            try {
                run();
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
     * Returns a task that performs this task. If this task throws any checked exception, it is discarded and the given fallback task is invoked.
     *
     * @param <E> The type of checked exception that can be thrown by the given fallback task.
     * @param fallback The task to invoke if this task throws any checked exception.
     * @return A task that invokes the {@code fallback} task if this task throws any checked exception.
     * @throws NullPointerException If {@code fallback} is {@code null}.
     */
    default <E extends Throwable> ThrowingRunnable<E> onErrorRunChecked(ThrowingRunnable<? extends E> fallback) {
        Objects.requireNonNull(fallback);
        return () -> {
            try {
                run();
            } catch (Error | RuntimeException e) {
                throw e;
            } catch (@SuppressWarnings("unused") Throwable throwable) {
                fallback.run();
            }
        };
    }

    /**
     * Returns a task that performs this task. If this task throws any checked exception, it is discarded and the given fallback task is invoked.
     *
     * @param fallback The task to invoke if this task throws any checked exception.
     * @return A task that invokes the {@code fallback} task if this task throws any checked exception.
     * @throws NullPointerException If {@code fallback} is {@code null}.
     */
    default Runnable onErrorRunUnchecked(Runnable fallback) {
        Objects.requireNonNull(fallback);
        return () -> {
            try {
                run();
            } catch (Error | RuntimeException e) {
                throw e;
            } catch (@SuppressWarnings("unused") Throwable throwable) {
                fallback.run();
            }
        };
    }

    /**
     * Returns a task that performs this task. Any checked exception thrown by this task is discarded.
     *
     * @return A task that discards any thrown checked exception.
     */
    default Runnable onErrorDiscard() {
        return () -> {
            try {
                run();
            } catch (Error | RuntimeException e) {
                throw e;
            } catch (@SuppressWarnings("unused") Throwable throwable) {
                // discard
            }
        };
    }

    /**
     * Returns a task that performs this task. Any checked exception thrown by this taks is wrapped in an {@link UncheckedException}.
     *
     * @return A task that wraps any checked exception in an {@link UncheckedException}.
     */
    default Runnable unchecked() {
        return onErrorThrowAsUnchecked(UncheckedException::new);
    }

    /**
     * Factory method for turning {@code ThrowingRunnable}-shaped lambdas into {@code ThrowingRunnables}.
     *
     * @param <X> The type of checked exception that can be thrown.
     * @param task The lambda to return as {@code ThrowingRunnable}.
     * @return The given lambda as a {@code ThrowingRunnable}.
     * @throws NullPointerException If {@code task} is {@code null}.
     */
    static <X extends Throwable> ThrowingRunnable<X> of(ThrowingRunnable<X> task) {
        Objects.requireNonNull(task);
        return task;
    }

    /**
     * Returns a task that performs the {@code task} task. Any checked exception thrown by the {@code task} task is wrapped in an
     * {@link UncheckedException}.
     *
     * @param task The task to perform when the returned task is performed.
     * @return A task that wraps any checked exception in an {@link UncheckedException}.
     * @throws NullPointerException If {@code task} is {@code null}.
     */
    static Runnable unchecked(ThrowingRunnable<?> task) {
        Objects.requireNonNull(task);
        return task.unchecked();
    }

    /**
     * Returns a task that performs the {@code task} task. Any checked exception thrown by the {@code task} task is relayed to the caller. This method
     * allows existing {@link Runnable} instances to be used where {@code ThrowingRunnable} is expected.
     *
     * @param <X> The type of checked exception that can be thrown.
     * @param task The task to perform when the returned task is performed.
     * @return A task that wraps any checked exception in an {@link UncheckedException}.
     * @throws NullPointerException If {@code task} is {@code null}.
     */
    static <X extends Throwable> ThrowingRunnable<X> checked(Runnable task) {
        Objects.requireNonNull(task);
        return task::run;
    }

    /**
     * Returns a task that performs the {@code task} task to its input. Any {@link UncheckedException} thrown by the {@code taks} task is unwrapped if
     * its cause is an instance of {@code errorType}, otherwise it is relayed to the caller.
     *
     * @param <X> The type of checked exception that can be thrown.
     * @param task The task to perform when the returned task is performed.
     * @param errorType The type of checked exception that can be thrown.
     * @return A task that wraps any checked exception in an {@link UncheckedException}.
     * @throws NullPointerException If {@code task} or {@code errorType} is {@code null}.
     */
    static <X extends Throwable> ThrowingRunnable<X> checked(Runnable task, Class<X> errorType) {
        Objects.requireNonNull(task);
        Objects.requireNonNull(errorType);
        return () -> invokeAndUnwrap(task, errorType);
    }

    /**
     * Invokes a task, unwrapping any {@link UncheckedException} that is thrown if its cause if an instance of {@code errorType}.
     *
     * @param <X> The type of checked exception that can be thrown.
     * @param task The task to invoke.
     * @param errorType The type of checked exception that can be thrown.
     * @throws NullPointerException If {@code task} or {@code errorType} is {@code null}.
     * @throws X If {@code task} throws an {@link UncheckedException} that wraps an instance of {@code errorType}.
     */
    static <X extends Throwable> void invokeAndUnwrap(Runnable task, Class<X> errorType) throws X {
        Objects.requireNonNull(errorType);
        try {
            task.run();
        } catch (UncheckedException e) {
            Throwable cause = e.getCause();
            if (errorType.isInstance(cause)) {
                throw errorType.cast(cause);
            }
            throw e;
        }
    }
}
