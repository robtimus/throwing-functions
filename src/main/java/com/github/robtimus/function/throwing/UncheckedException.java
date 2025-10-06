/*
 * UncheckedException.java
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

/**
 * Wraps a throwable with an unchecked exception.
 *
 * @author Rob Spoor
 */
@SuppressWarnings("serial")
public final class UncheckedException extends RuntimeException {

    private UncheckedException(String message, Throwable cause, boolean includeStackTrace) {
        super(message, cause, true, includeStackTrace);
    }

    /**
     * Creates a new exception with a stack trace of its own.
     *
     * @param cause The throwable to wrap.
     * @return The created exception.
     * @throws NullPointerException If the given throwable is {@code null}.
     */
    public static UncheckedException withStackTrace(Throwable cause) {
        return withStackTrace(cause.toString(), cause);
    }

    /**
     * Creates a new exception with a stack trace of its own.
     *
     * @param message The optional detail message.
     * @param cause The throwable to wrap.
     * @return The created exception.
     * @throws NullPointerException If the given throwable is {@code null}.
     */
    public static UncheckedException withStackTrace(String message, Throwable cause) {
        Objects.requireNonNull(cause);
        return new UncheckedException(message, cause, true);
    }

    /**
     * Creates a new exception without a stack trace of its own. This makes the created exception more lightweight.
     *
     * @param cause The throwable to wrap.
     * @return The created exception.
     * @throws NullPointerException If the given throwable is {@code null}.
     */
    public static UncheckedException withoutStackTrace(Throwable cause) {
        return withoutStackTrace(cause.toString(), cause);
    }

    /**
     * Creates a new exception with a stack trace of its own. This makes the created exception more lightweight.
     *
     * @param message The optional detail message.
     * @param cause The throwable to wrap.
     * @return The created exception.
     * @throws NullPointerException If the given throwable is {@code null}.
     */
    public static UncheckedException withoutStackTrace(String message, Throwable cause) {
        Objects.requireNonNull(cause);
        return new UncheckedException(message, cause, false);
    }

    /**
     * Throws the wrapped throwable as an instance of a specific error type.
     *
     * @param <T> The return type. This allows this method to be used with return statements.
     * @param <X> The type of error to throw.
     * @param errorType The type of error to throw.
     * @return Nothing. This method always thrown an exception.
     * @throws NullPointerException If the given error type is {@code null}.
     * @throws X The wrapped throwable cast to an instance of the given error type.
     * @throws IllegalStateException If the wrapped throwable is not an instance of the given error type.
     */
    public <T, X extends Throwable> T throwCauseAs(Class<X> errorType) throws X {
        Throwable cause = getCause();
        if (errorType.isInstance(cause)) {
            throw errorType.cast(cause);
        }
        throw new IllegalStateException("Unexpected exception thrown: " + cause, cause); //$NON-NLS-1$
    }

    /**
     * Throws the wrapped throwable as an instance of one of a set of specific error types.
     *
     * @param <T> The return type. This allows this method to be used with return statements.
     * @param <X1> The type of the first possible error to throw.
     * @param <X2> The type of the second possible error to throw.
     * @param errorType1 The type of the first possible error to throw.
     * @param errorType2 The type of the second possible error to throw.
     * @return Nothing. This method always thrown an exception.
     * @throws NullPointerException If either given error type is {@code null}.
     * @throws X1 The wrapped throwable cast to an instance of the first given error type.
     * @throws X2 The wrapped throwable cast to an instance of the second given error type.
     * @throws IllegalStateException If the wrapped throwable is not an instance of either given error type.
     */
    public <T, X1 extends Throwable, X2 extends Throwable> T throwCauseAsOneOf(Class<X1> errorType1, Class<X2> errorType2) throws X1, X2 {
        Throwable cause = getCause();
        if (errorType1.isInstance(cause)) {
            throw errorType1.cast(cause);
        }
        if (errorType2.isInstance(cause)) {
            throw errorType2.cast(cause);
        }
        throw new IllegalStateException("Unexpected exception thrown: " + cause, cause); //$NON-NLS-1$
    }

    /**
     * Throws the wrapped throwable as an instance of one of a set of specific error types.
     *
     * @param <T> The return type. This allows this method to be used with return statements.
     * @param <X1> The type of the first possible error to throw.
     * @param <X2> The type of the second possible error to throw.
     * @param <X3> The type of the third possible error to throw.
     * @param errorType1 The type of the first possible error to throw.
     * @param errorType2 The type of the second possible error to throw.
     * @param errorType3 The type of the third possible error to throw.
     * @return Nothing. This method always thrown an exception.
     * @throws NullPointerException If any given error type is {@code null}.
     * @throws X1 The wrapped throwable cast to an instance of the first given error type.
     * @throws X2 The wrapped throwable cast to an instance of the second given error type.
     * @throws X3 The wrapped throwable cast to an instance of the third given error type.
     * @throws IllegalStateException If the wrapped throwable is not an instance of any given error type.
     */
    public <T, X1 extends Throwable, X2 extends Throwable, X3 extends Throwable> T throwCauseAsOneOf(Class<X1> errorType1,
                                                                                                     Class<X2> errorType2,
                                                                                                     Class<X3> errorType3) throws X1, X2, X3 {
        Throwable cause = getCause();
        if (errorType1.isInstance(cause)) {
            throw errorType1.cast(cause);
        }
        if (errorType2.isInstance(cause)) {
            throw errorType2.cast(cause);
        }
        if (errorType3.isInstance(cause)) {
            throw errorType3.cast(cause);
        }
        throw new IllegalStateException("Unexpected exception thrown: " + cause, cause); //$NON-NLS-1$
    }
}
