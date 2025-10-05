/*
 * ThrowingIntToLongFunctionTest.java
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
import java.util.function.IntToLongFunction;
import java.util.function.LongSupplier;
import java.util.function.ToLongFunction;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

@SuppressWarnings("nls")
class ThrowingIntToLongFunctionTest {

    @Nested
    class OnErrorThrowAsChecked {

        @Test
        void testNullArgument() {
            ThrowingIntToLongFunction<IOException> function = Spied.throwingIntToLongFunction(i -> i + 1);

            assertThrows(NullPointerException.class, () -> function.onErrorThrowAsChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingIntToLongFunction<IOException> function = Spied.throwingIntToLongFunction(i -> i + 1);

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingIntToLongFunction<ExecutionException> throwing = function.onErrorThrowAsChecked(errorMapper);

            assertEquals(2L, throwing.applyAsLong(1));

            verify(function).applyAsLong(1);
            verify(function).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingIntToLongFunction<IOException> function = Spied.throwingIntToLongFunction(i -> {
                throw new IOException(Integer.toString(i));
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingIntToLongFunction<ExecutionException> throwing = function.onErrorThrowAsChecked(errorMapper);

            ExecutionException thrown = assertThrows(ExecutionException.class, () -> throwing.applyAsLong(1));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1", cause.getMessage());

            verify(function).applyAsLong(1);
            verify(function).onErrorThrowAsChecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(function, errorMapper);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingIntToLongFunction<IOException> function = Spied.throwingIntToLongFunction(throwable::throwUnchecked);

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingIntToLongFunction<ExecutionException> throwing = function.onErrorThrowAsChecked(errorMapper);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> throwing.applyAsLong(1));
            assertEquals("1", thrown.getMessage());

            verify(function).applyAsLong(1);
            verify(function).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }
    }

    @Nested
    class OnErrorThrowAsUnchecked {

        @Test
        void testNullArgument() {
            ThrowingIntToLongFunction<IOException> function = Spied.throwingIntToLongFunction(i -> i + 1);

            assertThrows(NullPointerException.class, () -> function.onErrorThrowAsUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingIntToLongFunction<IOException> function = Spied.throwingIntToLongFunction(i -> i + 1);

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            IntToLongFunction throwing = function.onErrorThrowAsUnchecked(errorMapper);

            assertEquals(2L, throwing.applyAsLong(1));

            verify(function).applyAsLong(1);
            verify(function).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingIntToLongFunction<IOException> function = Spied.throwingIntToLongFunction(i -> {
                throw new IOException(Integer.toString(i));
            });

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            IntToLongFunction throwing = function.onErrorThrowAsUnchecked(errorMapper);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> throwing.applyAsLong(1));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1", cause.getMessage());

            verify(function).applyAsLong(1);
            verify(function).onErrorThrowAsUnchecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(function, errorMapper);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingIntToLongFunction<IOException> function = Spied.throwingIntToLongFunction(throwable::throwUnchecked);

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            IntToLongFunction throwing = function.onErrorThrowAsUnchecked(errorMapper);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> throwing.applyAsLong(1));
            assertEquals("1", thrown.getMessage());

            verify(function).applyAsLong(1);
            verify(function).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }
    }

    @Nested
    class OnErrorHandleChecked {

        @Test
        void testNullArgument() {
            ThrowingIntToLongFunction<IOException> function = Spied.throwingIntToLongFunction(i -> i + 1);

            assertThrows(NullPointerException.class, () -> function.onErrorHandleChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingIntToLongFunction<IOException> function = Spied.throwingIntToLongFunction(i -> i + 1);

            ThrowingToLongFunction<IOException, ExecutionException> errorHandler = Spied.throwingToLongFunction(e -> 0L);

            ThrowingIntToLongFunction<ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

            assertEquals(2L, handling.applyAsLong(1));

            verify(function).applyAsLong(1);
            verify(function).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException, ExecutionException {
                ThrowingIntToLongFunction<IOException> function = Spied.throwingIntToLongFunction(i -> {
                    throw new IOException(Integer.toString(i));
                });

                ThrowingToLongFunction<IOException, ExecutionException> errorHandler = Spied.throwingToLongFunction(e -> 0L);

                ThrowingIntToLongFunction<ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

                assertEquals(0L, handling.applyAsLong(1));

                verify(function).applyAsLong(1);
                verify(function).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsLong(any());
                verifyNoMoreInteractions(function, errorHandler);
            }

            @Test
            void testHandlerThrowsChecked() throws IOException, ExecutionException {
                ThrowingIntToLongFunction<IOException> function = Spied.throwingIntToLongFunction(i -> {
                    throw new IOException(Integer.toString(i));
                });

                ThrowingToLongFunction<IOException, ExecutionException> errorHandler = Spied.throwingToLongFunction(e -> {
                    throw new ExecutionException(e);
                });

                ThrowingIntToLongFunction<ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

                ExecutionException thrown = assertThrows(ExecutionException.class, () -> handling.applyAsLong(1));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("1", cause.getMessage());

                verify(function).applyAsLong(1);
                verify(function).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsLong(any());
                verifyNoMoreInteractions(function, errorHandler);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testHandlerThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ExecutionException {
                ThrowingIntToLongFunction<IOException> function = Spied.throwingIntToLongFunction(i -> {
                    throw new IOException(Integer.toString(i));
                });

                ThrowingToLongFunction<IOException, ExecutionException> errorHandler = Spied.throwingToLongFunction(throwable::throwUnchecked);

                ThrowingIntToLongFunction<ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.applyAsLong(1));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("1", cause.getMessage());

                verify(function).applyAsLong(1);
                verify(function).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsLong(any());
                verifyNoMoreInteractions(function, errorHandler);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingIntToLongFunction<IOException> function = Spied.throwingIntToLongFunction(throwable::throwUnchecked);

            ThrowingToLongFunction<IOException, ExecutionException> errorHandler = Spied.throwingToLongFunction(e -> 0L);

            ThrowingIntToLongFunction<ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.applyAsLong(1));
            assertEquals("1", thrown.getMessage());

            verify(function).applyAsLong(1);
            verify(function).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }
    }

    @Nested
    class OnErrorHandleUnchecked {

        @Test
        void testNullArgument() {
            ThrowingIntToLongFunction<IOException> function = Spied.throwingIntToLongFunction(i -> i + 1);

            assertThrows(NullPointerException.class, () -> function.onErrorHandleUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingIntToLongFunction<IOException> function = Spied.throwingIntToLongFunction(i -> i + 1);

            ToLongFunction<IOException> errorHandler = Spied.toLongFunction(e -> 0L);

            IntToLongFunction handling = function.onErrorHandleUnchecked(errorHandler);

            assertEquals(2L, handling.applyAsLong(1));

            verify(function).applyAsLong(1);
            verify(function).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException {
                ThrowingIntToLongFunction<IOException> function = Spied.throwingIntToLongFunction(i -> {
                    throw new IOException(Integer.toString(i));
                });

                ToLongFunction<IOException> errorHandler = Spied.toLongFunction(e -> 0L);

                IntToLongFunction handling = function.onErrorHandleUnchecked(errorHandler);

                assertEquals(0L, handling.applyAsLong(1));

                verify(function).applyAsLong(1);
                verify(function).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).applyAsLong(any());
                verifyNoMoreInteractions(function, errorHandler);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testHandlerThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingIntToLongFunction<IOException> function = Spied.throwingIntToLongFunction(i -> {
                    throw new IOException(Integer.toString(i));
                });

                ToLongFunction<IOException> errorHandler = Spied.toLongFunction(throwable::throwUnchecked);

                IntToLongFunction handling = function.onErrorHandleUnchecked(errorHandler);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.applyAsLong(1));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("1", cause.getMessage());

                verify(function).applyAsLong(1);
                verify(function).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).applyAsLong(any());
                verifyNoMoreInteractions(function, errorHandler);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingIntToLongFunction<IOException> function = Spied.throwingIntToLongFunction(throwable::throwUnchecked);

            ToLongFunction<IOException> errorHandler = Spied.toLongFunction(e -> 0L);

            IntToLongFunction handling = function.onErrorHandleUnchecked(errorHandler);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.applyAsLong(1));
            assertEquals("1", thrown.getMessage());

            verify(function).applyAsLong(1);
            verify(function).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }
    }

    @Nested
    class OnErrorApplyChecked {

        @Test
        void testNullArgument() {
            ThrowingIntToLongFunction<IOException> function = Spied.throwingIntToLongFunction(i -> i + 1);

            assertThrows(NullPointerException.class, () -> function.onErrorApplyChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingIntToLongFunction<IOException> function = Spied.throwingIntToLongFunction(i -> i + 1);

            ThrowingIntToLongFunction<ExecutionException> fallback = Spied.throwingIntToLongFunction(i -> i * 3);

            ThrowingIntToLongFunction<ExecutionException> applying = function.onErrorApplyChecked(fallback);

            assertEquals(2L, applying.applyAsLong(1));

            verify(function).applyAsLong(1);
            verify(function).onErrorApplyChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                ThrowingIntToLongFunction<IOException> function = Spied.throwingIntToLongFunction(i -> {
                    throw new IOException(Integer.toString(i));
                });

                ThrowingIntToLongFunction<ParseException> fallback = Spied.throwingIntToLongFunction(i -> i * 3);

                ThrowingIntToLongFunction<ParseException> applying = function.onErrorApplyChecked(fallback);

                assertEquals(3L, applying.applyAsLong(1));

                verify(function).applyAsLong(1);
                verify(function).onErrorApplyChecked(fallback);
                verify(fallback).applyAsLong(1);
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                ThrowingIntToLongFunction<IOException> function = Spied.throwingIntToLongFunction(i -> {
                    throw new IOException(Integer.toString(i));
                });

                ThrowingIntToLongFunction<ParseException> fallback = Spied.throwingIntToLongFunction(i -> {
                    throw new ParseException(Integer.toString(i), 0);
                });

                ThrowingIntToLongFunction<ParseException> applying = function.onErrorApplyChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> applying.applyAsLong(1));
                assertEquals("1", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(function).applyAsLong(1);
                verify(function).onErrorApplyChecked(fallback);
                verify(fallback).applyAsLong(1);
                verifyNoMoreInteractions(function, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ParseException {
                ThrowingIntToLongFunction<IOException> function = Spied.throwingIntToLongFunction(i -> {
                    throw new IOException(Integer.toString(i));
                });

                ThrowingIntToLongFunction<ParseException> fallback = Spied
                        .throwingIntToLongFunction(throwable::throwUnchecked);

                ThrowingIntToLongFunction<ParseException> applying = function.onErrorApplyChecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.applyAsLong(1));
                assertEquals("1", thrown.getMessage());

                verify(function).applyAsLong(1);
                verify(function).onErrorApplyChecked(fallback);
                verify(fallback).applyAsLong(1);
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingIntToLongFunction<IOException> function = Spied.throwingIntToLongFunction(throwable::throwUnchecked);

            ThrowingIntToLongFunction<ParseException> fallback = Spied.throwingIntToLongFunction(i -> i * 3);

            ThrowingIntToLongFunction<ParseException> applying = function.onErrorApplyChecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.applyAsLong(1));
            assertEquals("1", thrown.getMessage());

            verify(function).applyAsLong(1);
            verify(function).onErrorApplyChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorApplyUnchecked {

        @Test
        void testNullArgument() {
            ThrowingIntToLongFunction<IOException> function = Spied.throwingIntToLongFunction(i -> i + 1);

            assertThrows(NullPointerException.class, () -> function.onErrorApplyUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingIntToLongFunction<IOException> function = Spied.throwingIntToLongFunction(i -> i + 1);

            IntToLongFunction fallback = Spied.intToLongFunction(i -> i * 3);

            IntToLongFunction applying = function.onErrorApplyUnchecked(fallback);

            assertEquals(2L, applying.applyAsLong(1));

            verify(function).applyAsLong(1);
            verify(function).onErrorApplyUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                ThrowingIntToLongFunction<IOException> function = Spied.throwingIntToLongFunction(i -> {
                    throw new IOException(Integer.toString(i));
                });

                IntToLongFunction fallback = Spied.intToLongFunction(i -> i * 3);

                IntToLongFunction applying = function.onErrorApplyUnchecked(fallback);

                assertEquals(3L, applying.applyAsLong(1));

                verify(function).applyAsLong(1);
                verify(function).onErrorApplyUnchecked(fallback);
                verify(fallback).applyAsLong(1);
                verifyNoMoreInteractions(function, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingIntToLongFunction<IOException> function = Spied.throwingIntToLongFunction(i -> {
                    throw new IOException(Integer.toString(i));
                });

                IntToLongFunction fallback = Spied.intToLongFunction(throwable::throwUnchecked);

                IntToLongFunction applying = function.onErrorApplyUnchecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.applyAsLong(1));
                assertEquals("1", thrown.getMessage());

                verify(function).applyAsLong(1);
                verify(function).onErrorApplyUnchecked(fallback);
                verify(fallback).applyAsLong(1);
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingIntToLongFunction<IOException> function = Spied.throwingIntToLongFunction(throwable::throwUnchecked);

            IntToLongFunction fallback = Spied.intToLongFunction(i -> i * 3);

            IntToLongFunction applying = function.onErrorApplyUnchecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.applyAsLong(1));
            assertEquals("1", thrown.getMessage());

            verify(function).applyAsLong(1);
            verify(function).onErrorApplyUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorGetChecked {

        @Test
        void testNullArgument() {
            ThrowingIntToLongFunction<IOException> function = Spied.throwingIntToLongFunction(i -> i + 1);

            assertThrows(NullPointerException.class, () -> function.onErrorGetChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingIntToLongFunction<IOException> function = Spied.throwingIntToLongFunction(i -> i + 1);

            ThrowingLongSupplier<ExecutionException> fallback = Spied.throwingLongSupplier(() -> 0L);

            ThrowingIntToLongFunction<ExecutionException> getting = function.onErrorGetChecked(fallback);

            assertEquals(2L, getting.applyAsLong(1));

            verify(function).applyAsLong(1);
            verify(function).onErrorGetChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                ThrowingIntToLongFunction<IOException> function = Spied.throwingIntToLongFunction(i -> {
                    throw new IOException(Integer.toString(i));
                });

                ThrowingLongSupplier<ParseException> fallback = Spied.throwingLongSupplier(() -> 0L);

                ThrowingIntToLongFunction<ParseException> getting = function.onErrorGetChecked(fallback);

                assertEquals(0L, getting.applyAsLong(1));

                verify(function).applyAsLong(1);
                verify(function).onErrorGetChecked(fallback);
                verify(fallback).getAsLong();
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                ThrowingIntToLongFunction<IOException> function = Spied.throwingIntToLongFunction(i -> {
                    throw new IOException(Integer.toString(i));
                });

                ThrowingLongSupplier<ParseException> fallback = Spied.throwingLongSupplier(() -> {
                    throw new ParseException("bar", 0);
                });

                ThrowingIntToLongFunction<ParseException> getting = function.onErrorGetChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> getting.applyAsLong(1));
                assertEquals("bar", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(function).applyAsLong(1);
                verify(function).onErrorGetChecked(fallback);
                verify(fallback).getAsLong();
                verifyNoMoreInteractions(function, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ParseException {
                ThrowingIntToLongFunction<IOException> function = Spied.throwingIntToLongFunction(i -> {
                    throw new IOException(Integer.toString(i));
                });

                ThrowingLongSupplier<ParseException> fallback = Spied.throwingLongSupplier(() -> throwable.throwUnchecked("bar"));

                ThrowingIntToLongFunction<ParseException> getting = function.onErrorGetChecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.applyAsLong(1));
                assertEquals("bar", thrown.getMessage());

                verify(function).applyAsLong(1);
                verify(function).onErrorGetChecked(fallback);
                verify(fallback).getAsLong();
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingIntToLongFunction<IOException> function = Spied.throwingIntToLongFunction(throwable::throwUnchecked);

            ThrowingLongSupplier<ParseException> fallback = Spied.throwingLongSupplier(() -> 0L);

            ThrowingIntToLongFunction<ParseException> getting = function.onErrorGetChecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.applyAsLong(1));
            assertEquals("1", thrown.getMessage());

            verify(function).applyAsLong(1);
            verify(function).onErrorGetChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorGetUnchecked {

        @Test
        void testNullArgument() {
            ThrowingIntToLongFunction<IOException> function = Spied.throwingIntToLongFunction(i -> i + 1);

            assertThrows(NullPointerException.class, () -> function.onErrorGetUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingIntToLongFunction<IOException> function = Spied.throwingIntToLongFunction(i -> i + 1);

            LongSupplier fallback = Spied.longSupplier(() -> 0L);

            IntToLongFunction getting = function.onErrorGetUnchecked(fallback);

            assertEquals(2L, getting.applyAsLong(1));

            verify(function).applyAsLong(1);
            verify(function).onErrorGetUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                ThrowingIntToLongFunction<IOException> function = Spied.throwingIntToLongFunction(i -> {
                    throw new IOException(Integer.toString(i));
                });

                LongSupplier fallback = Spied.longSupplier(() -> 0L);

                IntToLongFunction getting = function.onErrorGetUnchecked(fallback);

                assertEquals(0L, getting.applyAsLong(1));

                verify(function).applyAsLong(1);
                verify(function).onErrorGetUnchecked(fallback);
                verify(fallback).getAsLong();
                verifyNoMoreInteractions(function, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingIntToLongFunction<IOException> function = Spied.throwingIntToLongFunction(i -> {
                    throw new IOException(Integer.toString(i));
                });

                LongSupplier fallback = Spied.longSupplier(() -> throwable.throwUnchecked("bar"));

                IntToLongFunction getting = function.onErrorGetUnchecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.applyAsLong(1));
                assertEquals("bar", thrown.getMessage());

                verify(function).applyAsLong(1);
                verify(function).onErrorGetUnchecked(fallback);
                verify(fallback).getAsLong();
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingIntToLongFunction<IOException> function = Spied.throwingIntToLongFunction(throwable::throwUnchecked);

            LongSupplier fallback = Spied.longSupplier(() -> 0L);

            IntToLongFunction getting = function.onErrorGetUnchecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.applyAsLong(1));
            assertEquals("1", thrown.getMessage());

            verify(function).applyAsLong(1);
            verify(function).onErrorGetUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorReturn {

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingIntToLongFunction<IOException> function = Spied.throwingIntToLongFunction(i -> i + 1);

            IntToLongFunction returning = function.onErrorReturn(0L);

            assertEquals(2L, returning.applyAsLong(1));

            verify(function).applyAsLong(1);
            verify(function).onErrorReturn(0L);
            verifyNoMoreInteractions(function);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingIntToLongFunction<IOException> function = Spied.throwingIntToLongFunction(i -> {
                throw new IOException(Integer.toString(i));
            });

            IntToLongFunction returning = function.onErrorReturn(0L);

            assertEquals(0L, returning.applyAsLong(1));

            verify(function).applyAsLong(1);
            verify(function).onErrorReturn(0L);
            verifyNoMoreInteractions(function);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingIntToLongFunction<IOException> function = Spied.throwingIntToLongFunction(throwable::throwUnchecked);

            IntToLongFunction returning = function.onErrorReturn(0L);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> returning.applyAsLong(1));
            assertEquals("1", thrown.getMessage());

            verify(function).applyAsLong(1);
            verify(function).onErrorReturn(0L);
            verifyNoMoreInteractions(function);
        }
    }

    @Nested
    class Unchecked {

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingIntToLongFunction<IOException> function = Spied.throwingIntToLongFunction(i -> i + 1);

            IntToLongFunction unchecked = function.unchecked();

            assertEquals(2L, unchecked.applyAsLong(1));

            verify(function).applyAsLong(1);
            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(function);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingIntToLongFunction<IOException> function = Spied.throwingIntToLongFunction(i -> {
                throw new IOException(Integer.toString(i));
            });

            IntToLongFunction unchecked = function.unchecked();

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.applyAsLong(1));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1", cause.getMessage());

            verify(function).applyAsLong(1);
            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(function);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingIntToLongFunction<IOException> function = Spied.throwingIntToLongFunction(throwable::throwUnchecked);

            IntToLongFunction unchecked = function.unchecked();

            Throwable thrown = assertThrows(throwable.throwableType(), () -> unchecked.applyAsLong(1));
            assertEquals("1", thrown.getMessage());

            verify(function).applyAsLong(1);
            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(function);
        }
    }

    @Nested
    class Of {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> ThrowingIntToLongFunction.of(null));
        }

        @Test
        void testNonNullArgument() throws IOException {
            ThrowingIntToLongFunction<IOException> function = ThrowingIntToLongFunction.of(i -> i + 1);

            assertEquals(2L, function.applyAsLong(1));
        }
    }

    @Nested
    class UncheckedFactory {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> ThrowingIntToLongFunction.unchecked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ThrowingIntToLongFunction<IOException> function = Spied.throwingIntToLongFunction(i -> i + 1);

            IntToLongFunction unchecked = ThrowingIntToLongFunction.unchecked(function);

            assertEquals(2L, unchecked.applyAsLong(1));

            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verify(function).applyAsLong(1);
            verifyNoMoreInteractions(function);
        }

        @Test
        void testArgumentThrowsChecked() throws IOException {
            ThrowingIntToLongFunction<IOException> function = Spied.throwingIntToLongFunction(i -> {
                throw new IOException(Integer.toString(i));
            });

            IntToLongFunction unchecked = ThrowingIntToLongFunction.unchecked(function);

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.applyAsLong(1));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1", cause.getMessage());

            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verify(function).applyAsLong(1);
            verifyNoMoreInteractions(function);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testArgumentThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingIntToLongFunction<IOException> function = Spied.throwingIntToLongFunction(throwable::throwUnchecked);

            IntToLongFunction unchecked = ThrowingIntToLongFunction.unchecked(function);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> unchecked.applyAsLong(1));
            assertEquals("1", thrown.getMessage());

            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verify(function).applyAsLong(1);
            verifyNoMoreInteractions(function);
        }
    }

    @Nested
    class CheckedWrapper {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> ThrowingIntToLongFunction.checked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ThrowingIntToLongFunction<IOException> function = ThrowingIntToLongFunction.checked(i -> i + 1);

            assertEquals(2L, function.applyAsLong(1));
        }

        @Test
        void testArgumentThrowsUnchecked() {
            ThrowingIntToLongFunction<IOException> function = ThrowingIntToLongFunction.checked(i -> {
                throw UncheckedException.withoutStackTrace(Integer.toString(i), new IOException());
            });

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> function.applyAsLong(1));
            assertEquals("1", thrown.getMessage());
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertNull(cause.getMessage());
        }
    }

    @Nested
    class CheckedUnwrapper {

        @Test
        void testNullArguments() {
            IntToLongFunction function = i -> i + 1;

            assertThrows(NullPointerException.class, () -> ThrowingIntToLongFunction.checked(null, IOException.class));
            assertThrows(NullPointerException.class, () -> ThrowingIntToLongFunction.checked(function, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ThrowingIntToLongFunction<IOException> function = ThrowingIntToLongFunction.checked(i -> i + 1, IOException.class);

            assertEquals(2L, function.applyAsLong(1));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                ThrowingIntToLongFunction<IOException> function = ThrowingIntToLongFunction.checked(i -> {
                    throw UncheckedException.withoutStackTrace(new IOException(Integer.toString(i)));
                }, IOException.class);

                IOException thrown = assertThrows(IOException.class, () -> function.applyAsLong(1));
                assertEquals("1", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                ThrowingIntToLongFunction<IOException> function = ThrowingIntToLongFunction.checked(i -> {
                    throw UncheckedException.withoutStackTrace(new FileNotFoundException(Integer.toString(i)));
                }, IOException.class);

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class, () -> function.applyAsLong(1));
                assertEquals("1", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                ThrowingIntToLongFunction<IOException> function = ThrowingIntToLongFunction.checked(i -> {
                    throw UncheckedException.withoutStackTrace(new ParseException(Integer.toString(i), 0));
                }, IOException.class);

                UncheckedException thrown = assertThrows(UncheckedException.class, () -> function.applyAsLong(1));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("1", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            ThrowingIntToLongFunction<IOException> function = ThrowingIntToLongFunction.checked(i -> {
                throw new IllegalStateException(Integer.toString(i));
            }, IOException.class);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> function.applyAsLong(1));
            assertEquals("1", thrown.getMessage());
        }
    }

    @Nested
    class InvokeAndUnwrap {

        @Test
        void testNullArguments() {
            IntToLongFunction function = i -> String.valueOf(Integer.toString(i)).length();

            assertThrows(NullPointerException.class, () -> ThrowingIntToLongFunction.invokeAndUnwrap(null, 1, IOException.class));
            assertThrows(NullPointerException.class, () -> ThrowingIntToLongFunction.invokeAndUnwrap(function, 1, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            IntToLongFunction function = i -> i + 1;

            assertEquals(2L, ThrowingIntToLongFunction.invokeAndUnwrap(function, 1, IOException.class));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                IntToLongFunction function = i -> {
                    throw UncheckedException.withoutStackTrace(new IOException(Integer.toString(i)));
                };

                IOException thrown = assertThrows(IOException.class,
                        () -> ThrowingIntToLongFunction.invokeAndUnwrap(function, 1, IOException.class));
                assertEquals("1", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                IntToLongFunction function = i -> {
                    throw UncheckedException.withoutStackTrace(new FileNotFoundException(Integer.toString(i)));
                };

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class,
                        () -> ThrowingIntToLongFunction.invokeAndUnwrap(function, 1, IOException.class));
                assertEquals("1", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                IntToLongFunction function = i -> {
                    throw UncheckedException.withoutStackTrace(new ParseException(Integer.toString(i), 0));
                };

                UncheckedException thrown = assertThrows(UncheckedException.class,
                        () -> ThrowingIntToLongFunction.invokeAndUnwrap(function, 1, IOException.class));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("1", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            IntToLongFunction function = i -> {
                throw new IllegalStateException(Integer.toString(i));
            };

            IllegalStateException thrown = assertThrows(IllegalStateException.class,
                    () -> ThrowingIntToLongFunction.invokeAndUnwrap(function, 1, IOException.class));
            assertEquals("1", thrown.getMessage());
        }
    }
}
