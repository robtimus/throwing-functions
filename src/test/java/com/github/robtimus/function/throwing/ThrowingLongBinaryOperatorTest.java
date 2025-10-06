/*
 * ThrowingLongBinaryOperatorTest.java
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.function.LongBinaryOperator;
import java.util.function.LongSupplier;
import java.util.function.ToLongFunction;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

@SuppressWarnings("nls")
class ThrowingLongBinaryOperatorTest {

    @Nested
    class OnErrorThrowAsChecked {

        @Test
        void testNullArgument() {
            ThrowingLongBinaryOperator<IOException> operator = Spied.throwingLongBinaryOperator(Long::max);

            assertThrows(NullPointerException.class, () -> operator.onErrorThrowAsChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingLongBinaryOperator<IOException> operator = Spied.throwingLongBinaryOperator(Long::max);

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingLongBinaryOperator<ExecutionException> throwing = operator.onErrorThrowAsChecked(errorMapper);

            assertEquals(2L, throwing.applyAsLong(1L, 2L));

            verify(operator).applyAsLong(1L, 2L);
            verify(operator).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(operator, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingLongBinaryOperator<IOException> operator = Spied.throwingLongBinaryOperator((l1, l2) -> {
                throw new IOException(Long.toString(l1 + l2));
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingLongBinaryOperator<ExecutionException> throwing = operator.onErrorThrowAsChecked(errorMapper);

            ExecutionException thrown = assertThrows(ExecutionException.class, () -> throwing.applyAsLong(1L, 2L));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("3", cause.getMessage());

            verify(operator).applyAsLong(1L, 2L);
            verify(operator).onErrorThrowAsChecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(operator, errorMapper);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingLongBinaryOperator<IOException> operator = Spied.throwingLongBinaryOperator((l1, l2) -> throwable.throwUnchecked(l1 + l2));

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingLongBinaryOperator<ExecutionException> throwing = operator.onErrorThrowAsChecked(errorMapper);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> throwing.applyAsLong(1L, 2L));
            assertEquals("3", thrown.getMessage());

            verify(operator).applyAsLong(1L, 2L);
            verify(operator).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(operator, errorMapper);
        }
    }

    @Nested
    class OnErrorThrowAsUnchecked {

        @Test
        void testNullArgument() {
            ThrowingLongBinaryOperator<IOException> operator = Spied.throwingLongBinaryOperator(Long::max);

            assertThrows(NullPointerException.class, () -> operator.onErrorThrowAsUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingLongBinaryOperator<IOException> operator = Spied.throwingLongBinaryOperator(Long::max);

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            LongBinaryOperator throwing = operator.onErrorThrowAsUnchecked(errorMapper);

            assertEquals(2L, throwing.applyAsLong(1L, 2L));

            verify(operator).applyAsLong(1L, 2L);
            verify(operator).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(operator, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingLongBinaryOperator<IOException> operator = Spied.throwingLongBinaryOperator((l1, l2) -> {
                throw new IOException(Long.toString(l1 + l2));
            });

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            LongBinaryOperator throwing = operator.onErrorThrowAsUnchecked(errorMapper);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> throwing.applyAsLong(1L, 2L));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("3", cause.getMessage());

            verify(operator).applyAsLong(1L, 2L);
            verify(operator).onErrorThrowAsUnchecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(operator, errorMapper);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingLongBinaryOperator<IOException> operator = Spied.throwingLongBinaryOperator((l1, l2) -> throwable.throwUnchecked(l1 + l2));

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            LongBinaryOperator throwing = operator.onErrorThrowAsUnchecked(errorMapper);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> throwing.applyAsLong(1L, 2L));
            assertEquals("3", thrown.getMessage());

            verify(operator).applyAsLong(1L, 2L);
            verify(operator).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(operator, errorMapper);
        }
    }

    @Nested
    class OnErrorHandleChecked {

        @Test
        void testNullArgument() {
            ThrowingLongBinaryOperator<IOException> operator = Spied.throwingLongBinaryOperator(Long::max);

            assertThrows(NullPointerException.class, () -> operator.onErrorHandleChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingLongBinaryOperator<IOException> operator = Spied.throwingLongBinaryOperator(Long::max);

            ThrowingToLongFunction<IOException, ExecutionException> errorHandler = Spied.throwingToLongFunction(e -> 0L);

            ThrowingLongBinaryOperator<ExecutionException> handling = operator.onErrorHandleChecked(errorHandler);

            assertEquals(2L, handling.applyAsLong(1L, 2L));

            verify(operator).applyAsLong(1L, 2L);
            verify(operator).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(operator, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException, ExecutionException {
                ThrowingLongBinaryOperator<IOException> operator = Spied.throwingLongBinaryOperator((l1, l2) -> {
                    throw new IOException(Long.toString(l1 + l2));
                });

                ThrowingToLongFunction<IOException, ExecutionException> errorHandler = Spied.throwingToLongFunction(e -> 0L);

                ThrowingLongBinaryOperator<ExecutionException> handling = operator.onErrorHandleChecked(errorHandler);

                assertEquals(0L, handling.applyAsLong(1L, 2L));

                verify(operator).applyAsLong(1L, 2L);
                verify(operator).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsLong(any());
                verifyNoMoreInteractions(operator, errorHandler);
            }

            @Test
            void testHandlerThrowsChecked() throws IOException, ExecutionException {
                ThrowingLongBinaryOperator<IOException> operator = Spied.throwingLongBinaryOperator((l1, l2) -> {
                    throw new IOException(Long.toString(l1 + l2));
                });

                ThrowingToLongFunction<IOException, ExecutionException> errorHandler = Spied.throwingToLongFunction(e -> {
                    throw new ExecutionException(e);
                });

                ThrowingLongBinaryOperator<ExecutionException> handling = operator.onErrorHandleChecked(errorHandler);

                ExecutionException thrown = assertThrows(ExecutionException.class, () -> handling.applyAsLong(1L, 2L));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("3", cause.getMessage());

                verify(operator).applyAsLong(1L, 2L);
                verify(operator).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsLong(any());
                verifyNoMoreInteractions(operator, errorHandler);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testHandlerThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ExecutionException {
                ThrowingLongBinaryOperator<IOException> operator = Spied.throwingLongBinaryOperator((l1, l2) -> {
                    throw new IOException(Long.toString(l1 + l2));
                });

                ThrowingToLongFunction<IOException, ExecutionException> errorHandler = Spied.throwingToLongFunction(throwable::throwUnchecked);

                ThrowingLongBinaryOperator<ExecutionException> handling = operator.onErrorHandleChecked(errorHandler);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.applyAsLong(1L, 2L));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("3", cause.getMessage());

                verify(operator).applyAsLong(1L, 2L);
                verify(operator).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsLong(any());
                verifyNoMoreInteractions(operator, errorHandler);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingLongBinaryOperator<IOException> operator = Spied.throwingLongBinaryOperator((l1, l2) -> throwable.throwUnchecked(l1 + l2));

            ThrowingToLongFunction<IOException, ExecutionException> errorHandler = Spied.throwingToLongFunction(e -> 0L);

            ThrowingLongBinaryOperator<ExecutionException> handling = operator.onErrorHandleChecked(errorHandler);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.applyAsLong(1L, 2L));
            assertEquals("3", thrown.getMessage());

            verify(operator).applyAsLong(1L, 2L);
            verify(operator).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(operator, errorHandler);
        }
    }

    @Nested
    class OnErrorHandleUnchecked {

        @Test
        void testNullArgument() {
            ThrowingLongBinaryOperator<IOException> operator = Spied.throwingLongBinaryOperator(Long::max);

            assertThrows(NullPointerException.class, () -> operator.onErrorHandleUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingLongBinaryOperator<IOException> operator = Spied.throwingLongBinaryOperator(Long::max);

            ToLongFunction<IOException> errorHandler = Spied.toLongFunction(e -> 0L);

            LongBinaryOperator handling = operator.onErrorHandleUnchecked(errorHandler);

            assertEquals(2L, handling.applyAsLong(1L, 2L));

            verify(operator).applyAsLong(1L, 2L);
            verify(operator).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(operator, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException {
                ThrowingLongBinaryOperator<IOException> operator = Spied.throwingLongBinaryOperator((l1, l2) -> {
                    throw new IOException(Long.toString(l1 + l2));
                });

                ToLongFunction<IOException> errorHandler = Spied.toLongFunction(e -> 0L);

                LongBinaryOperator handling = operator.onErrorHandleUnchecked(errorHandler);

                assertEquals(0L, handling.applyAsLong(1L, 2L));

                verify(operator).applyAsLong(1L, 2L);
                verify(operator).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).applyAsLong(any());
                verifyNoMoreInteractions(operator, errorHandler);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testHandlerThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingLongBinaryOperator<IOException> operator = Spied.throwingLongBinaryOperator((l1, l2) -> {
                    throw new IOException(Long.toString(l1 + l2));
                });

                ToLongFunction<IOException> errorHandler = Spied.toLongFunction(throwable::throwUnchecked);

                LongBinaryOperator handling = operator.onErrorHandleUnchecked(errorHandler);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.applyAsLong(1L, 2L));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("3", cause.getMessage());

                verify(operator).applyAsLong(1L, 2L);
                verify(operator).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).applyAsLong(any());
                verifyNoMoreInteractions(operator, errorHandler);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingLongBinaryOperator<IOException> operator = Spied.throwingLongBinaryOperator((l1, l2) -> throwable.throwUnchecked(l1 + l2));

            ToLongFunction<IOException> errorHandler = Spied.toLongFunction(e -> 0L);

            LongBinaryOperator handling = operator.onErrorHandleUnchecked(errorHandler);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.applyAsLong(1L, 2L));
            assertEquals("3", thrown.getMessage());

            verify(operator).applyAsLong(1L, 2L);
            verify(operator).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(operator, errorHandler);
        }
    }

    @Nested
    class OnErrorApplyChecked {

        @Test
        void testNullArgument() {
            ThrowingLongBinaryOperator<IOException> operator = Spied.throwingLongBinaryOperator(Long::max);

            assertThrows(NullPointerException.class, () -> operator.onErrorApplyChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingLongBinaryOperator<IOException> operator = Spied.throwingLongBinaryOperator(Long::max);

            ThrowingLongBinaryOperator<ExecutionException> fallback = Spied.throwingLongBinaryOperator((l1, l2) -> l1 + l2 + l1 + l2);

            ThrowingLongBinaryOperator<ExecutionException> applying = operator.onErrorApplyChecked(fallback);

            assertEquals(2L, applying.applyAsLong(1L, 2L));

            verify(operator).applyAsLong(1L, 2L);
            verify(operator).onErrorApplyChecked(fallback);
            verifyNoMoreInteractions(operator, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                ThrowingLongBinaryOperator<IOException> operator = Spied.throwingLongBinaryOperator((l1, l2) -> {
                    throw new IOException(Long.toString(l1 + l2));
                });

                ThrowingLongBinaryOperator<ParseException> fallback = Spied.throwingLongBinaryOperator((l1, l2) -> l1 + l2 + l1 + l2);

                ThrowingLongBinaryOperator<ParseException> applying = operator.onErrorApplyChecked(fallback);

                assertEquals(6L, applying.applyAsLong(1L, 2L));

                verify(operator).applyAsLong(1L, 2L);
                verify(operator).onErrorApplyChecked(fallback);
                verify(fallback).applyAsLong(1L, 2L);
                verifyNoMoreInteractions(operator, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                ThrowingLongBinaryOperator<IOException> operator = Spied.throwingLongBinaryOperator((l1, l2) -> {
                    throw new IOException(Long.toString(l1 + l2));
                });

                ThrowingLongBinaryOperator<ParseException> fallback = Spied.throwingLongBinaryOperator((l1, l2) -> {
                    throw new ParseException(Long.toString(l1 + l2), 0);
                });

                ThrowingLongBinaryOperator<ParseException> applying = operator.onErrorApplyChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> applying.applyAsLong(1L, 2L));
                assertEquals("3", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(operator).applyAsLong(1L, 2L);
                verify(operator).onErrorApplyChecked(fallback);
                verify(fallback).applyAsLong(1L, 2L);
                verifyNoMoreInteractions(operator, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ParseException {
                ThrowingLongBinaryOperator<IOException> operator = Spied.throwingLongBinaryOperator((l1, l2) -> {
                    throw new IOException(Long.toString(l1 + l2));
                });

                ThrowingLongBinaryOperator<ParseException> fallback = Spied.throwingLongBinaryOperator((l1, l2) -> throwable.throwUnchecked(l1 + l2));

                ThrowingLongBinaryOperator<ParseException> applying = operator.onErrorApplyChecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.applyAsLong(1L, 2L));
                assertEquals("3", thrown.getMessage());

                verify(operator).applyAsLong(1L, 2L);
                verify(operator).onErrorApplyChecked(fallback);
                verify(fallback).applyAsLong(1L, 2L);
                verifyNoMoreInteractions(operator, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingLongBinaryOperator<IOException> operator = Spied.throwingLongBinaryOperator((l1, l2) -> throwable.throwUnchecked(l1 + l2));

            ThrowingLongBinaryOperator<ParseException> fallback = Spied.throwingLongBinaryOperator((l1, l2) -> l1 + l2 + l1 + l2);

            ThrowingLongBinaryOperator<ParseException> applying = operator.onErrorApplyChecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.applyAsLong(1L, 2L));
            assertEquals("3", thrown.getMessage());

            verify(operator).applyAsLong(1L, 2L);
            verify(operator).onErrorApplyChecked(fallback);
            verifyNoMoreInteractions(operator, fallback);
        }
    }

    @Nested
    class OnErrorApplyUnchecked {

        @Test
        void testNullArgument() {
            ThrowingLongBinaryOperator<IOException> operator = Spied.throwingLongBinaryOperator(Long::max);

            assertThrows(NullPointerException.class, () -> operator.onErrorApplyUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingLongBinaryOperator<IOException> operator = Spied.throwingLongBinaryOperator(Long::max);

            LongBinaryOperator fallback = Spied.longBinaryOperator((l1, l2) -> l1 + l2 + l1 + l2);

            LongBinaryOperator applying = operator.onErrorApplyUnchecked(fallback);

            assertEquals(2L, applying.applyAsLong(1L, 2L));

            verify(operator).applyAsLong(1L, 2L);
            verify(operator).onErrorApplyUnchecked(fallback);
            verifyNoMoreInteractions(operator, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                ThrowingLongBinaryOperator<IOException> operator = Spied.throwingLongBinaryOperator((l1, l2) -> {
                    throw new IOException(Long.toString(l1 + l2));
                });

                LongBinaryOperator fallback = Spied.longBinaryOperator((l1, l2) -> l1 + l2 + l1 + l2);

                LongBinaryOperator applying = operator.onErrorApplyUnchecked(fallback);

                assertEquals(6L, applying.applyAsLong(1L, 2L));

                verify(operator).applyAsLong(1L, 2L);
                verify(operator).onErrorApplyUnchecked(fallback);
                verify(fallback).applyAsLong(1L, 2L);
                verifyNoMoreInteractions(operator, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingLongBinaryOperator<IOException> operator = Spied.throwingLongBinaryOperator((l1, l2) -> {
                    throw new IOException(Long.toString(l1 + l2));
                });

                LongBinaryOperator fallback = Spied.longBinaryOperator((l1, l2) -> throwable.throwUnchecked(l1 + l2));

                LongBinaryOperator applying = operator.onErrorApplyUnchecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.applyAsLong(1L, 2L));
                assertEquals("3", thrown.getMessage());

                verify(operator).applyAsLong(1L, 2L);
                verify(operator).onErrorApplyUnchecked(fallback);
                verify(fallback).applyAsLong(1L, 2L);
                verifyNoMoreInteractions(operator, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingLongBinaryOperator<IOException> operator = Spied.throwingLongBinaryOperator((l1, l2) -> throwable.throwUnchecked(l1 + l2));

            LongBinaryOperator fallback = Spied.longBinaryOperator((l1, l2) -> l1 + l2 + l1 + l2);

            LongBinaryOperator applying = operator.onErrorApplyUnchecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.applyAsLong(1L, 2L));
            assertEquals("3", thrown.getMessage());

            verify(operator).applyAsLong(1L, 2L);
            verify(operator).onErrorApplyUnchecked(fallback);
            verifyNoMoreInteractions(operator, fallback);
        }
    }

    @Nested
    class OnErrorGetChecked {

        @Test
        void testNullArgument() {
            ThrowingLongBinaryOperator<IOException> operator = Spied.throwingLongBinaryOperator(Long::max);

            assertThrows(NullPointerException.class, () -> operator.onErrorGetChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingLongBinaryOperator<IOException> operator = Spied.throwingLongBinaryOperator(Long::max);

            ThrowingLongSupplier<ExecutionException> fallback = Spied.throwingLongSupplier(() -> 0L);

            ThrowingLongBinaryOperator<ExecutionException> getting = operator.onErrorGetChecked(fallback);

            assertEquals(2L, getting.applyAsLong(1L, 2L));

            verify(operator).applyAsLong(1L, 2L);
            verify(operator).onErrorGetChecked(fallback);
            verifyNoMoreInteractions(operator, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                ThrowingLongBinaryOperator<IOException> operator = Spied.throwingLongBinaryOperator((l1, l2) -> {
                    throw new IOException(Long.toString(l1 + l2));
                });

                ThrowingLongSupplier<ParseException> fallback = Spied.throwingLongSupplier(() -> 0L);

                ThrowingLongBinaryOperator<ParseException> getting = operator.onErrorGetChecked(fallback);

                assertEquals(0L, getting.applyAsLong(1L, 2L));

                verify(operator).applyAsLong(1L, 2L);
                verify(operator).onErrorGetChecked(fallback);
                verify(fallback).getAsLong();
                verifyNoMoreInteractions(operator, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                ThrowingLongBinaryOperator<IOException> operator = Spied.throwingLongBinaryOperator((l1, l2) -> {
                    throw new IOException(Long.toString(l1 + l2));
                });

                ThrowingLongSupplier<ParseException> fallback = Spied.throwingLongSupplier(() -> {
                    throw new ParseException("bar", 0);
                });

                ThrowingLongBinaryOperator<ParseException> getting = operator.onErrorGetChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> getting.applyAsLong(1L, 2L));
                assertEquals("bar", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(operator).applyAsLong(1L, 2L);
                verify(operator).onErrorGetChecked(fallback);
                verify(fallback).getAsLong();
                verifyNoMoreInteractions(operator, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ParseException {
                ThrowingLongBinaryOperator<IOException> operator = Spied.throwingLongBinaryOperator((l1, l2) -> {
                    throw new IOException(Long.toString(l1 + l2));
                });

                ThrowingLongSupplier<ParseException> fallback = Spied.throwingLongSupplier(() -> throwable.throwUnchecked("bar"));

                ThrowingLongBinaryOperator<ParseException> getting = operator.onErrorGetChecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.applyAsLong(1L, 2L));
                assertEquals("bar", thrown.getMessage());

                verify(operator).applyAsLong(1L, 2L);
                verify(operator).onErrorGetChecked(fallback);
                verify(fallback).getAsLong();
                verifyNoMoreInteractions(operator, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingLongBinaryOperator<IOException> operator = Spied.throwingLongBinaryOperator((l1, l2) -> throwable.throwUnchecked(l1 + l2));

            ThrowingLongSupplier<ParseException> fallback = Spied.throwingLongSupplier(() -> 0L);

            ThrowingLongBinaryOperator<ParseException> getting = operator.onErrorGetChecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.applyAsLong(1L, 2L));
            assertEquals("3", thrown.getMessage());

            verify(operator).applyAsLong(1L, 2L);
            verify(operator).onErrorGetChecked(fallback);
            verifyNoMoreInteractions(operator, fallback);
        }
    }

    @Nested
    class OnErrorGetUnchecked {

        @Test
        void testNullArgument() {
            ThrowingLongBinaryOperator<IOException> operator = Spied.throwingLongBinaryOperator(Long::max);

            assertThrows(NullPointerException.class, () -> operator.onErrorGetUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingLongBinaryOperator<IOException> operator = Spied.throwingLongBinaryOperator(Long::max);

            LongSupplier fallback = Spied.longSupplier(() -> 2L);

            LongBinaryOperator getting = operator.onErrorGetUnchecked(fallback);

            assertEquals(2L, getting.applyAsLong(1L, 2L));

            verify(operator).applyAsLong(1L, 2L);
            verify(operator).onErrorGetUnchecked(fallback);
            verifyNoMoreInteractions(operator, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                ThrowingLongBinaryOperator<IOException> operator = Spied.throwingLongBinaryOperator((l1, l2) -> {
                    throw new IOException(Long.toString(l1 + l2));
                });

                LongSupplier fallback = Spied.longSupplier(() -> 2L);

                LongBinaryOperator getting = operator.onErrorGetUnchecked(fallback);

                assertEquals(2L, getting.applyAsLong(1L, 2L));

                verify(operator).applyAsLong(1L, 2L);
                verify(operator).onErrorGetUnchecked(fallback);
                verify(fallback).getAsLong();
                verifyNoMoreInteractions(operator, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingLongBinaryOperator<IOException> operator = Spied.throwingLongBinaryOperator((l1, l2) -> {
                    throw new IOException(Long.toString(l1 + l2));
                });

                LongSupplier fallback = Spied.longSupplier(() -> throwable.throwUnchecked("bar"));

                LongBinaryOperator getting = operator.onErrorGetUnchecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.applyAsLong(1L, 2L));
                assertEquals("bar", thrown.getMessage());

                verify(operator).applyAsLong(1L, 2L);
                verify(operator).onErrorGetUnchecked(fallback);
                verify(fallback).getAsLong();
                verifyNoMoreInteractions(operator, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingLongBinaryOperator<IOException> operator = Spied.throwingLongBinaryOperator((l1, l2) -> throwable.throwUnchecked(l1 + l2));

            LongSupplier fallback = Spied.longSupplier(() -> 2L);

            LongBinaryOperator getting = operator.onErrorGetUnchecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.applyAsLong(1L, 2L));
            assertEquals("3", thrown.getMessage());

            verify(operator).applyAsLong(1L, 2L);
            verify(operator).onErrorGetUnchecked(fallback);
            verifyNoMoreInteractions(operator, fallback);
        }
    }

    @Nested
    class OnErrorReturn {

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingLongBinaryOperator<IOException> operator = Spied.throwingLongBinaryOperator(Long::max);

            LongBinaryOperator returning = operator.onErrorReturn(2L);

            assertEquals(2L, returning.applyAsLong(1L, 2L));

            verify(operator).applyAsLong(1L, 2L);
            verify(operator).onErrorReturn(2L);
            verifyNoMoreInteractions(operator);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingLongBinaryOperator<IOException> operator = Spied.throwingLongBinaryOperator((l1, l2) -> {
                throw new IOException(Long.toString(l1 + l2));
            });

            LongBinaryOperator returning = operator.onErrorReturn(2L);

            assertEquals(2L, returning.applyAsLong(1L, 2L));

            verify(operator).applyAsLong(1L, 2L);
            verify(operator).onErrorReturn(2L);
            verifyNoMoreInteractions(operator);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingLongBinaryOperator<IOException> operator = Spied.throwingLongBinaryOperator((l1, l2) -> throwable.throwUnchecked(l1 + l2));

            LongBinaryOperator returning = operator.onErrorReturn(2L);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> returning.applyAsLong(1L, 2L));
            assertEquals("3", thrown.getMessage());

            verify(operator).applyAsLong(1L, 2L);
            verify(operator).onErrorReturn(2L);
            verifyNoMoreInteractions(operator);
        }
    }

    @Nested
    class Unchecked {

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingLongBinaryOperator<IOException> operator = Spied.throwingLongBinaryOperator(Long::max);

            LongBinaryOperator unchecked = operator.unchecked();

            assertEquals(2L, unchecked.applyAsLong(1L, 2L));

            verify(operator).applyAsLong(1L, 2L);
            verify(operator).unchecked();
            verify(operator).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(operator);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingLongBinaryOperator<IOException> operator = Spied.throwingLongBinaryOperator((l1, l2) -> {
                throw new IOException(Long.toString(l1 + l2));
            });

            LongBinaryOperator unchecked = operator.unchecked();

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.applyAsLong(1L, 2L));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("3", cause.getMessage());

            verify(operator).applyAsLong(1L, 2L);
            verify(operator).unchecked();
            verify(operator).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(operator);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingLongBinaryOperator<IOException> operator = Spied.throwingLongBinaryOperator((l1, l2) -> throwable.throwUnchecked(l1 + l2));

            LongBinaryOperator unchecked = operator.unchecked();

            Throwable thrown = assertThrows(throwable.throwableType(), () -> unchecked.applyAsLong(1L, 2L));
            assertEquals("3", thrown.getMessage());

            verify(operator).applyAsLong(1L, 2L);
            verify(operator).unchecked();
            verify(operator).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(operator);
        }
    }

    @Nested
    class Of {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> ThrowingFunction.of(null));
        }

        @Test
        void testNonNullArgument() throws IOException {
            ThrowingLongBinaryOperator<IOException> operator = ThrowingLongBinaryOperator.of(Long::max);

            assertEquals(2L, operator.applyAsLong(1L, 2L));
        }
    }

    @Nested
    class UncheckedFactory {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> ThrowingFunction.unchecked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ThrowingLongBinaryOperator<IOException> operator = Spied.throwingLongBinaryOperator(Long::max);

            LongBinaryOperator unchecked = ThrowingLongBinaryOperator.unchecked(operator);

            assertEquals(2L, unchecked.applyAsLong(1L, 2L));

            verify(operator).unchecked();
            verify(operator).onErrorThrowAsUnchecked(any());
            verify(operator).applyAsLong(1L, 2L);
            verifyNoMoreInteractions(operator);
        }

        @Test
        void testArgumentThrowsChecked() throws IOException {
            ThrowingLongBinaryOperator<IOException> operator = Spied.throwingLongBinaryOperator((l1, l2) -> {
                throw new IOException(Long.toString(l1 + l2));
            });

            LongBinaryOperator unchecked = ThrowingLongBinaryOperator.unchecked(operator);

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.applyAsLong(1L, 2L));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("3", cause.getMessage());

            verify(operator).unchecked();
            verify(operator).onErrorThrowAsUnchecked(any());
            verify(operator).applyAsLong(1L, 2L);
            verifyNoMoreInteractions(operator);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testArgumentThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingLongBinaryOperator<IOException> operator = Spied.throwingLongBinaryOperator((l1, l2) -> throwable.throwUnchecked(l1 + l2));

            LongBinaryOperator unchecked = ThrowingLongBinaryOperator.unchecked(operator);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> unchecked.applyAsLong(1L, 2L));
            assertEquals("3", thrown.getMessage());

            verify(operator).unchecked();
            verify(operator).onErrorThrowAsUnchecked(any());
            verify(operator).applyAsLong(1L, 2L);
            verifyNoMoreInteractions(operator);
        }
    }

    @Nested
    class CheckedWrapper {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> ThrowingFunction.checked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ThrowingLongBinaryOperator<IOException> operator = ThrowingLongBinaryOperator.checked(Long::max);

            assertEquals(2L, operator.applyAsLong(1L, 2L));
        }

        @Test
        void testArgumentThrowsUnchecked() {
            ThrowingLongBinaryOperator<IOException> operator = ThrowingLongBinaryOperator.checked((l1, l2) -> {
                throw UncheckedException.withoutStackTrace(Long.toString(l1 + l2), new IOException());
            });

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> operator.applyAsLong(1L, 2L));
            assertEquals("3", thrown.getMessage());
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertNull(cause.getMessage());
        }
    }

    @Nested
    class CheckedUnwrapper {

        @Test
        void testNullArguments() {
            LongBinaryOperator operator = Long::max;

            assertThrows(NullPointerException.class, () -> ThrowingLongBinaryOperator.checked(null, IOException.class));
            assertThrows(NullPointerException.class, () -> ThrowingLongBinaryOperator.checked(operator, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ThrowingLongBinaryOperator<IOException> operator = ThrowingLongBinaryOperator.checked(Long::max, IOException.class);

            assertEquals(2L, operator.applyAsLong(1L, 2L));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                ThrowingLongBinaryOperator<IOException> operator = ThrowingLongBinaryOperator.checked((l1, l2) -> {
                    throw UncheckedException.withoutStackTrace(new IOException(Long.toString(l1 + l2)));
                }, IOException.class);

                IOException thrown = assertThrows(IOException.class, () -> operator.applyAsLong(1L, 2L));
                assertEquals("3", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                ThrowingLongBinaryOperator<IOException> operator = ThrowingLongBinaryOperator.checked((l1, l2) -> {
                    throw UncheckedException.withoutStackTrace(new FileNotFoundException(Long.toString(l1 + l2)));
                }, IOException.class);

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class, () -> operator.applyAsLong(1L, 2L));
                assertEquals("3", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                ThrowingLongBinaryOperator<IOException> operator = ThrowingLongBinaryOperator.checked((l1, l2) -> {
                    throw UncheckedException.withoutStackTrace(new ParseException(Long.toString(l1 + l2), 0));
                }, IOException.class);

                UncheckedException thrown = assertThrows(UncheckedException.class, () -> operator.applyAsLong(1L, 2L));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("3", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            ThrowingLongBinaryOperator<IOException> operator = ThrowingLongBinaryOperator.checked((l1, l2) -> {
                throw new IllegalStateException(Long.toString(l1 + l2));
            }, IOException.class);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> operator.applyAsLong(1L, 2L));
            assertEquals("3", thrown.getMessage());
        }
    }
}
