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
}
