/*
 * UncheckedThrowable.java
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

import static org.junit.jupiter.params.provider.Arguments.arguments;
import java.util.function.Function;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.support.ParameterDeclarations;

final class UncheckedThrowable<X extends Throwable> {

    private final Class<X> throwableType;
    private final Class<? extends Throwable> baseType;
    private final Function<String, X> fromString;
    private final Function<Throwable, X> fromCause;

    private UncheckedThrowable(Class<X> throwableType, Function<String, X> fromString, Function<Throwable, X> fromCause) {
        this.throwableType = throwableType;
        this.baseType = baseType(throwableType);
        this.fromString = fromString;
        this.fromCause = fromCause;
    }

    private static Class<? extends Throwable> baseType(Class<? extends Throwable> type) {
        Class<? extends Throwable> iterator = type;
        while (iterator != Error.class && iterator != RuntimeException.class) {
            iterator = iterator.getSuperclass().asSubclass(Throwable.class);
        }
        return iterator;
    }

    Class<X> throwableType() {
        return throwableType;
    }

    <T> T throwUnchecked(String message) {
        return throwAsUnchecked(fromString.apply(message));
    }

    <T> T throwUnchecked(int value) {
        return throwUnchecked(Integer.toString(value));
    }

    <T> T throwUnchecked(long value) {
        return throwUnchecked(Long.toString(value));
    }

    <T> T throwUnchecked(double value) {
        return throwUnchecked(Double.toString(value));
    }

    <T> T throwUnchecked(Throwable cause) {
        return throwAsUnchecked(fromCause.apply(cause));
    }

    private <T> T throwAsUnchecked(X throwable) {
        if (throwable instanceof Error) {
            throw (Error) throwable;
        }
        throw (RuntimeException) throwable;
    }

    @Override
    public String toString() {
        return baseType.getSimpleName();
    }

    static final class Provider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ParameterDeclarations parameters, ExtensionContext context) throws Exception {
            return Stream.of(
                    arguments(new UncheckedThrowable<>(IllegalStateException.class, IllegalStateException::new, IllegalStateException::new)),
                    arguments(new UncheckedThrowable<>(TestError.class, TestError::new, TestError::new)));
        }
    }

    @SuppressWarnings("serial")
    private static final class TestError extends Error {

        TestError(String message) {
            super(message);
        }

        TestError(Throwable cause) {
            super(cause);
        }
    }
}
