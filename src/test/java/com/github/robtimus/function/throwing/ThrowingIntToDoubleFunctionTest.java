/*
 * ThrowingIntToDoubleFunctionTest.java
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
import java.util.function.DoubleSupplier;
import java.util.function.Function;
import java.util.function.IntToDoubleFunction;
import java.util.function.ToDoubleFunction;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

@SuppressWarnings("nls")
class ThrowingIntToDoubleFunctionTest {

    @Nested
    class OnErrorThrowAsChecked {

        @Test
        void testNullArgument() {
            ThrowingIntToDoubleFunction<IOException> function = Spied.throwingIntToDoubleFunction(i -> i + 1);

            assertThrows(NullPointerException.class, () -> function.onErrorThrowAsChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingIntToDoubleFunction<IOException> function = Spied.throwingIntToDoubleFunction(i -> i + 1);

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingIntToDoubleFunction<ExecutionException> throwing = function.onErrorThrowAsChecked(errorMapper);

            assertEquals(2D, throwing.applyAsDouble(1));

            verify(function).applyAsDouble(1);
            verify(function).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingIntToDoubleFunction<IOException> function = Spied.throwingIntToDoubleFunction(i -> {
                throw new IOException(Integer.toString(i));
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingIntToDoubleFunction<ExecutionException> throwing = function.onErrorThrowAsChecked(errorMapper);

            ExecutionException thrown = assertThrows(ExecutionException.class, () -> throwing.applyAsDouble(1));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1", cause.getMessage());

            verify(function).applyAsDouble(1);
            verify(function).onErrorThrowAsChecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(function, errorMapper);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingIntToDoubleFunction<IOException> function = Spied.throwingIntToDoubleFunction(throwable::throwUnchecked);

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingIntToDoubleFunction<ExecutionException> throwing = function.onErrorThrowAsChecked(errorMapper);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> throwing.applyAsDouble(1));
            assertEquals("1", thrown.getMessage());

            verify(function).applyAsDouble(1);
            verify(function).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }
    }

    @Nested
    class OnErrorThrowAsUnchecked {

        @Test
        void testNullArgument() {
            ThrowingIntToDoubleFunction<IOException> function = Spied.throwingIntToDoubleFunction(i -> i + 1);

            assertThrows(NullPointerException.class, () -> function.onErrorThrowAsUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingIntToDoubleFunction<IOException> function = Spied.throwingIntToDoubleFunction(i -> i + 1);

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            IntToDoubleFunction throwing = function.onErrorThrowAsUnchecked(errorMapper);

            assertEquals(2D, throwing.applyAsDouble(1));

            verify(function).applyAsDouble(1);
            verify(function).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingIntToDoubleFunction<IOException> function = Spied.throwingIntToDoubleFunction(i -> {
                throw new IOException(Integer.toString(i));
            });

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            IntToDoubleFunction throwing = function.onErrorThrowAsUnchecked(errorMapper);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> throwing.applyAsDouble(1));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1", cause.getMessage());

            verify(function).applyAsDouble(1);
            verify(function).onErrorThrowAsUnchecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(function, errorMapper);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingIntToDoubleFunction<IOException> function = Spied.throwingIntToDoubleFunction(throwable::throwUnchecked);

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            IntToDoubleFunction throwing = function.onErrorThrowAsUnchecked(errorMapper);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> throwing.applyAsDouble(1));
            assertEquals("1", thrown.getMessage());

            verify(function).applyAsDouble(1);
            verify(function).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }
    }

    @Nested
    class OnErrorHandleChecked {

        @Test
        void testNullArgument() {
            ThrowingIntToDoubleFunction<IOException> function = Spied.throwingIntToDoubleFunction(i -> i + 1);

            assertThrows(NullPointerException.class, () -> function.onErrorHandleChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingIntToDoubleFunction<IOException> function = Spied.throwingIntToDoubleFunction(i -> i + 1);

            ThrowingToDoubleFunction<IOException, ExecutionException> errorHandler = Spied.throwingToDoubleFunction(e -> 0D);

            ThrowingIntToDoubleFunction<ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

            assertEquals(2D, handling.applyAsDouble(1));

            verify(function).applyAsDouble(1);
            verify(function).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException, ExecutionException {
                ThrowingIntToDoubleFunction<IOException> function = Spied.throwingIntToDoubleFunction(i -> {
                    throw new IOException(Integer.toString(i));
                });

                ThrowingToDoubleFunction<IOException, ExecutionException> errorHandler = Spied.throwingToDoubleFunction(e -> 0D);

                ThrowingIntToDoubleFunction<ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

                assertEquals(0D, handling.applyAsDouble(1));

                verify(function).applyAsDouble(1);
                verify(function).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsDouble(any());
                verifyNoMoreInteractions(function, errorHandler);
            }

            @Test
            void testHandlerThrowsChecked() throws IOException, ExecutionException {
                ThrowingIntToDoubleFunction<IOException> function = Spied.throwingIntToDoubleFunction(i -> {
                    throw new IOException(Integer.toString(i));
                });

                ThrowingToDoubleFunction<IOException, ExecutionException> errorHandler = Spied.throwingToDoubleFunction(e -> {
                    throw new ExecutionException(e);
                });

                ThrowingIntToDoubleFunction<ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

                ExecutionException thrown = assertThrows(ExecutionException.class, () -> handling.applyAsDouble(1));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("1", cause.getMessage());

                verify(function).applyAsDouble(1);
                verify(function).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsDouble(any());
                verifyNoMoreInteractions(function, errorHandler);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testHandlerThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ExecutionException {
                ThrowingIntToDoubleFunction<IOException> function = Spied.throwingIntToDoubleFunction(i -> {
                    throw new IOException(Integer.toString(i));
                });

                ThrowingToDoubleFunction<IOException, ExecutionException> errorHandler = Spied.throwingToDoubleFunction(throwable::throwUnchecked);

                ThrowingIntToDoubleFunction<ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.applyAsDouble(1));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("1", cause.getMessage());

                verify(function).applyAsDouble(1);
                verify(function).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsDouble(any());
                verifyNoMoreInteractions(function, errorHandler);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingIntToDoubleFunction<IOException> function = Spied.throwingIntToDoubleFunction(throwable::throwUnchecked);

            ThrowingToDoubleFunction<IOException, ExecutionException> errorHandler = Spied.throwingToDoubleFunction(e -> 0D);

            ThrowingIntToDoubleFunction<ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.applyAsDouble(1));
            assertEquals("1", thrown.getMessage());

            verify(function).applyAsDouble(1);
            verify(function).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }
    }

    @Nested
    class OnErrorHandleUnchecked {

        @Test
        void testNullArgument() {
            ThrowingIntToDoubleFunction<IOException> function = Spied.throwingIntToDoubleFunction(i -> i + 1);

            assertThrows(NullPointerException.class, () -> function.onErrorHandleUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingIntToDoubleFunction<IOException> function = Spied.throwingIntToDoubleFunction(i -> i + 1);

            ToDoubleFunction<IOException> errorHandler = Spied.toDoubleFunction(e -> 0D);

            IntToDoubleFunction handling = function.onErrorHandleUnchecked(errorHandler);

            assertEquals(2D, handling.applyAsDouble(1));

            verify(function).applyAsDouble(1);
            verify(function).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException {
                ThrowingIntToDoubleFunction<IOException> function = Spied.throwingIntToDoubleFunction(i -> {
                    throw new IOException(Integer.toString(i));
                });

                ToDoubleFunction<IOException> errorHandler = Spied.toDoubleFunction(e -> 0D);

                IntToDoubleFunction handling = function.onErrorHandleUnchecked(errorHandler);

                assertEquals(0D, handling.applyAsDouble(1));

                verify(function).applyAsDouble(1);
                verify(function).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).applyAsDouble(any());
                verifyNoMoreInteractions(function, errorHandler);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testHandlerThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingIntToDoubleFunction<IOException> function = Spied.throwingIntToDoubleFunction(i -> {
                    throw new IOException(Integer.toString(i));
                });

                ToDoubleFunction<IOException> errorHandler = Spied.toDoubleFunction(throwable::throwUnchecked);

                IntToDoubleFunction handling = function.onErrorHandleUnchecked(errorHandler);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.applyAsDouble(1));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("1", cause.getMessage());

                verify(function).applyAsDouble(1);
                verify(function).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).applyAsDouble(any());
                verifyNoMoreInteractions(function, errorHandler);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingIntToDoubleFunction<IOException> function = Spied.throwingIntToDoubleFunction(throwable::throwUnchecked);

            ToDoubleFunction<IOException> errorHandler = Spied.toDoubleFunction(e -> 0D);

            IntToDoubleFunction handling = function.onErrorHandleUnchecked(errorHandler);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.applyAsDouble(1));
            assertEquals("1", thrown.getMessage());

            verify(function).applyAsDouble(1);
            verify(function).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }
    }

    @Nested
    class OnErrorApplyChecked {

        @Test
        void testNullArgument() {
            ThrowingIntToDoubleFunction<IOException> function = Spied.throwingIntToDoubleFunction(i -> i + 1);

            assertThrows(NullPointerException.class, () -> function.onErrorApplyChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingIntToDoubleFunction<IOException> function = Spied.throwingIntToDoubleFunction(i -> i + 1);

            ThrowingIntToDoubleFunction<ExecutionException> fallback = Spied.throwingIntToDoubleFunction(i -> i * 3);

            ThrowingIntToDoubleFunction<ExecutionException> applying = function.onErrorApplyChecked(fallback);

            assertEquals(2D, applying.applyAsDouble(1));

            verify(function).applyAsDouble(1);
            verify(function).onErrorApplyChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                ThrowingIntToDoubleFunction<IOException> function = Spied.throwingIntToDoubleFunction(i -> {
                    throw new IOException(Integer.toString(i));
                });

                ThrowingIntToDoubleFunction<ParseException> fallback = Spied.throwingIntToDoubleFunction(i -> i * 3);

                ThrowingIntToDoubleFunction<ParseException> applying = function.onErrorApplyChecked(fallback);

                assertEquals(3D, applying.applyAsDouble(1));

                verify(function).applyAsDouble(1);
                verify(function).onErrorApplyChecked(fallback);
                verify(fallback).applyAsDouble(1);
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                ThrowingIntToDoubleFunction<IOException> function = Spied.throwingIntToDoubleFunction(i -> {
                    throw new IOException(Integer.toString(i));
                });

                ThrowingIntToDoubleFunction<ParseException> fallback = Spied.throwingIntToDoubleFunction(i -> {
                    throw new ParseException(Integer.toString(i), 0);
                });

                ThrowingIntToDoubleFunction<ParseException> applying = function.onErrorApplyChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> applying.applyAsDouble(1));
                assertEquals("1", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(function).applyAsDouble(1);
                verify(function).onErrorApplyChecked(fallback);
                verify(fallback).applyAsDouble(1);
                verifyNoMoreInteractions(function, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ParseException {
                ThrowingIntToDoubleFunction<IOException> function = Spied.throwingIntToDoubleFunction(i -> {
                    throw new IOException(Integer.toString(i));
                });

                ThrowingIntToDoubleFunction<ParseException> fallback = Spied
                        .throwingIntToDoubleFunction(throwable::throwUnchecked);

                ThrowingIntToDoubleFunction<ParseException> applying = function.onErrorApplyChecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.applyAsDouble(1));
                assertEquals("1", thrown.getMessage());

                verify(function).applyAsDouble(1);
                verify(function).onErrorApplyChecked(fallback);
                verify(fallback).applyAsDouble(1);
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingIntToDoubleFunction<IOException> function = Spied.throwingIntToDoubleFunction(throwable::throwUnchecked);

            ThrowingIntToDoubleFunction<ParseException> fallback = Spied.throwingIntToDoubleFunction(i -> i * 3);

            ThrowingIntToDoubleFunction<ParseException> applying = function.onErrorApplyChecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.applyAsDouble(1));
            assertEquals("1", thrown.getMessage());

            verify(function).applyAsDouble(1);
            verify(function).onErrorApplyChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorApplyUnchecked {

        @Test
        void testNullArgument() {
            ThrowingIntToDoubleFunction<IOException> function = Spied.throwingIntToDoubleFunction(i -> i + 1);

            assertThrows(NullPointerException.class, () -> function.onErrorApplyUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingIntToDoubleFunction<IOException> function = Spied.throwingIntToDoubleFunction(i -> i + 1);

            IntToDoubleFunction fallback = Spied.intToDoubleFunction(i -> i * 3);

            IntToDoubleFunction applying = function.onErrorApplyUnchecked(fallback);

            assertEquals(2D, applying.applyAsDouble(1));

            verify(function).applyAsDouble(1);
            verify(function).onErrorApplyUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                ThrowingIntToDoubleFunction<IOException> function = Spied.throwingIntToDoubleFunction(i -> {
                    throw new IOException(Integer.toString(i));
                });

                IntToDoubleFunction fallback = Spied.intToDoubleFunction(i -> i * 3);

                IntToDoubleFunction applying = function.onErrorApplyUnchecked(fallback);

                assertEquals(3D, applying.applyAsDouble(1));

                verify(function).applyAsDouble(1);
                verify(function).onErrorApplyUnchecked(fallback);
                verify(fallback).applyAsDouble(1);
                verifyNoMoreInteractions(function, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingIntToDoubleFunction<IOException> function = Spied.throwingIntToDoubleFunction(i -> {
                    throw new IOException(Integer.toString(i));
                });

                IntToDoubleFunction fallback = Spied.intToDoubleFunction(throwable::throwUnchecked);

                IntToDoubleFunction applying = function.onErrorApplyUnchecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.applyAsDouble(1));
                assertEquals("1", thrown.getMessage());

                verify(function).applyAsDouble(1);
                verify(function).onErrorApplyUnchecked(fallback);
                verify(fallback).applyAsDouble(1);
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingIntToDoubleFunction<IOException> function = Spied.throwingIntToDoubleFunction(throwable::throwUnchecked);

            IntToDoubleFunction fallback = Spied.intToDoubleFunction(i -> i * 3);

            IntToDoubleFunction applying = function.onErrorApplyUnchecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.applyAsDouble(1));
            assertEquals("1", thrown.getMessage());

            verify(function).applyAsDouble(1);
            verify(function).onErrorApplyUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorGetChecked {

        @Test
        void testNullArgument() {
            ThrowingIntToDoubleFunction<IOException> function = Spied.throwingIntToDoubleFunction(i -> i + 1);

            assertThrows(NullPointerException.class, () -> function.onErrorGetChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingIntToDoubleFunction<IOException> function = Spied.throwingIntToDoubleFunction(i -> i + 1);

            ThrowingDoubleSupplier<ExecutionException> fallback = Spied.throwingDoubleSupplier(() -> 0D);

            ThrowingIntToDoubleFunction<ExecutionException> getting = function.onErrorGetChecked(fallback);

            assertEquals(2D, getting.applyAsDouble(1));

            verify(function).applyAsDouble(1);
            verify(function).onErrorGetChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                ThrowingIntToDoubleFunction<IOException> function = Spied.throwingIntToDoubleFunction(i -> {
                    throw new IOException(Integer.toString(i));
                });

                ThrowingDoubleSupplier<ParseException> fallback = Spied.throwingDoubleSupplier(() -> 0D);

                ThrowingIntToDoubleFunction<ParseException> getting = function.onErrorGetChecked(fallback);

                assertEquals(0D, getting.applyAsDouble(1));

                verify(function).applyAsDouble(1);
                verify(function).onErrorGetChecked(fallback);
                verify(fallback).getAsDouble();
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                ThrowingIntToDoubleFunction<IOException> function = Spied.throwingIntToDoubleFunction(i -> {
                    throw new IOException(Integer.toString(i));
                });

                ThrowingDoubleSupplier<ParseException> fallback = Spied.throwingDoubleSupplier(() -> {
                    throw new ParseException("bar", 0);
                });

                ThrowingIntToDoubleFunction<ParseException> getting = function.onErrorGetChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> getting.applyAsDouble(1));
                assertEquals("bar", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(function).applyAsDouble(1);
                verify(function).onErrorGetChecked(fallback);
                verify(fallback).getAsDouble();
                verifyNoMoreInteractions(function, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ParseException {
                ThrowingIntToDoubleFunction<IOException> function = Spied.throwingIntToDoubleFunction(i -> {
                    throw new IOException(Integer.toString(i));
                });

                ThrowingDoubleSupplier<ParseException> fallback = Spied.throwingDoubleSupplier(() -> throwable.throwUnchecked("bar"));

                ThrowingIntToDoubleFunction<ParseException> getting = function.onErrorGetChecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.applyAsDouble(1));
                assertEquals("bar", thrown.getMessage());

                verify(function).applyAsDouble(1);
                verify(function).onErrorGetChecked(fallback);
                verify(fallback).getAsDouble();
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingIntToDoubleFunction<IOException> function = Spied.throwingIntToDoubleFunction(throwable::throwUnchecked);

            ThrowingDoubleSupplier<ParseException> fallback = Spied.throwingDoubleSupplier(() -> 0D);

            ThrowingIntToDoubleFunction<ParseException> getting = function.onErrorGetChecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.applyAsDouble(1));
            assertEquals("1", thrown.getMessage());

            verify(function).applyAsDouble(1);
            verify(function).onErrorGetChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorGetUnchecked {

        @Test
        void testNullArgument() {
            ThrowingIntToDoubleFunction<IOException> function = Spied.throwingIntToDoubleFunction(i -> i + 1);

            assertThrows(NullPointerException.class, () -> function.onErrorGetUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingIntToDoubleFunction<IOException> function = Spied.throwingIntToDoubleFunction(i -> i + 1);

            DoubleSupplier fallback = Spied.doubleSupplier(() -> 0D);

            IntToDoubleFunction getting = function.onErrorGetUnchecked(fallback);

            assertEquals(2D, getting.applyAsDouble(1));

            verify(function).applyAsDouble(1);
            verify(function).onErrorGetUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                ThrowingIntToDoubleFunction<IOException> function = Spied.throwingIntToDoubleFunction(i -> {
                    throw new IOException(Integer.toString(i));
                });

                DoubleSupplier fallback = Spied.doubleSupplier(() -> 0D);

                IntToDoubleFunction getting = function.onErrorGetUnchecked(fallback);

                assertEquals(0D, getting.applyAsDouble(1));

                verify(function).applyAsDouble(1);
                verify(function).onErrorGetUnchecked(fallback);
                verify(fallback).getAsDouble();
                verifyNoMoreInteractions(function, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingIntToDoubleFunction<IOException> function = Spied.throwingIntToDoubleFunction(i -> {
                    throw new IOException(Integer.toString(i));
                });

                DoubleSupplier fallback = Spied.doubleSupplier(() -> throwable.throwUnchecked("bar"));

                IntToDoubleFunction getting = function.onErrorGetUnchecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.applyAsDouble(1));
                assertEquals("bar", thrown.getMessage());

                verify(function).applyAsDouble(1);
                verify(function).onErrorGetUnchecked(fallback);
                verify(fallback).getAsDouble();
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingIntToDoubleFunction<IOException> function = Spied.throwingIntToDoubleFunction(throwable::throwUnchecked);

            DoubleSupplier fallback = Spied.doubleSupplier(() -> 0D);

            IntToDoubleFunction getting = function.onErrorGetUnchecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.applyAsDouble(1));
            assertEquals("1", thrown.getMessage());

            verify(function).applyAsDouble(1);
            verify(function).onErrorGetUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorReturn {

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingIntToDoubleFunction<IOException> function = Spied.throwingIntToDoubleFunction(i -> i + 1);

            IntToDoubleFunction returning = function.onErrorReturn(0D);

            assertEquals(2D, returning.applyAsDouble(1));

            verify(function).applyAsDouble(1);
            verify(function).onErrorReturn(0D);
            verifyNoMoreInteractions(function);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingIntToDoubleFunction<IOException> function = Spied.throwingIntToDoubleFunction(i -> {
                throw new IOException(Integer.toString(i));
            });

            IntToDoubleFunction returning = function.onErrorReturn(0D);

            assertEquals(0D, returning.applyAsDouble(1));

            verify(function).applyAsDouble(1);
            verify(function).onErrorReturn(0D);
            verifyNoMoreInteractions(function);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingIntToDoubleFunction<IOException> function = Spied.throwingIntToDoubleFunction(throwable::throwUnchecked);

            IntToDoubleFunction returning = function.onErrorReturn(0D);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> returning.applyAsDouble(1));
            assertEquals("1", thrown.getMessage());

            verify(function).applyAsDouble(1);
            verify(function).onErrorReturn(0D);
            verifyNoMoreInteractions(function);
        }
    }

    @Nested
    class Unchecked {

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingIntToDoubleFunction<IOException> function = Spied.throwingIntToDoubleFunction(i -> i + 1);

            IntToDoubleFunction unchecked = function.unchecked();

            assertEquals(2D, unchecked.applyAsDouble(1));

            verify(function).applyAsDouble(1);
            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(function);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingIntToDoubleFunction<IOException> function = Spied.throwingIntToDoubleFunction(i -> {
                throw new IOException(Integer.toString(i));
            });

            IntToDoubleFunction unchecked = function.unchecked();

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.applyAsDouble(1));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1", cause.getMessage());

            verify(function).applyAsDouble(1);
            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(function);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingIntToDoubleFunction<IOException> function = Spied.throwingIntToDoubleFunction(throwable::throwUnchecked);

            IntToDoubleFunction unchecked = function.unchecked();

            Throwable thrown = assertThrows(throwable.throwableType(), () -> unchecked.applyAsDouble(1));
            assertEquals("1", thrown.getMessage());

            verify(function).applyAsDouble(1);
            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(function);
        }
    }

    @Nested
    class Of {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> ThrowingIntToDoubleFunction.of(null));
        }

        @Test
        void testNonNullArgument() throws IOException {
            ThrowingIntToDoubleFunction<IOException> function = ThrowingIntToDoubleFunction.of(i -> i + 1);

            assertEquals(2D, function.applyAsDouble(1));
        }
    }

    @Nested
    class UncheckedFactory {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> ThrowingIntToDoubleFunction.unchecked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ThrowingIntToDoubleFunction<IOException> function = Spied.throwingIntToDoubleFunction(i -> i + 1);

            IntToDoubleFunction unchecked = ThrowingIntToDoubleFunction.unchecked(function);

            assertEquals(2D, unchecked.applyAsDouble(1));

            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verify(function).applyAsDouble(1);
            verifyNoMoreInteractions(function);
        }

        @Test
        void testArgumentThrowsChecked() throws IOException {
            ThrowingIntToDoubleFunction<IOException> function = Spied.throwingIntToDoubleFunction(i -> {
                throw new IOException(Integer.toString(i));
            });

            IntToDoubleFunction unchecked = ThrowingIntToDoubleFunction.unchecked(function);

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.applyAsDouble(1));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1", cause.getMessage());

            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verify(function).applyAsDouble(1);
            verifyNoMoreInteractions(function);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testArgumentThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingIntToDoubleFunction<IOException> function = Spied.throwingIntToDoubleFunction(throwable::throwUnchecked);

            IntToDoubleFunction unchecked = ThrowingIntToDoubleFunction.unchecked(function);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> unchecked.applyAsDouble(1));
            assertEquals("1", thrown.getMessage());

            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verify(function).applyAsDouble(1);
            verifyNoMoreInteractions(function);
        }
    }

    @Nested
    class CheckedWrapper {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> ThrowingIntToDoubleFunction.checked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ThrowingIntToDoubleFunction<IOException> function = ThrowingIntToDoubleFunction.checked(i -> i + 1);

            assertEquals(2D, function.applyAsDouble(1));
        }

        @Test
        void testArgumentThrowsUnchecked() {
            ThrowingIntToDoubleFunction<IOException> function = ThrowingIntToDoubleFunction.checked(i -> {
                throw UncheckedException.withoutStackTrace(Integer.toString(i), new IOException());
            });

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> function.applyAsDouble(1));
            assertEquals("1", thrown.getMessage());
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertNull(cause.getMessage());
        }
    }

    @Nested
    class CheckedUnwrapper {

        @Test
        void testNullArguments() {
            IntToDoubleFunction function = i -> i + 1;

            assertThrows(NullPointerException.class, () -> ThrowingIntToDoubleFunction.checked(null, IOException.class));
            assertThrows(NullPointerException.class, () -> ThrowingIntToDoubleFunction.checked(function, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ThrowingIntToDoubleFunction<IOException> function = ThrowingIntToDoubleFunction.checked(i -> i + 1, IOException.class);

            assertEquals(2D, function.applyAsDouble(1));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                ThrowingIntToDoubleFunction<IOException> function = ThrowingIntToDoubleFunction.checked(i -> {
                    throw UncheckedException.withoutStackTrace(new IOException(Integer.toString(i)));
                }, IOException.class);

                IOException thrown = assertThrows(IOException.class, () -> function.applyAsDouble(1));
                assertEquals("1", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                ThrowingIntToDoubleFunction<IOException> function = ThrowingIntToDoubleFunction.checked(i -> {
                    throw UncheckedException.withoutStackTrace(new FileNotFoundException(Integer.toString(i)));
                }, IOException.class);

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class, () -> function.applyAsDouble(1));
                assertEquals("1", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                ThrowingIntToDoubleFunction<IOException> function = ThrowingIntToDoubleFunction.checked(i -> {
                    throw UncheckedException.withoutStackTrace(new ParseException(Integer.toString(i), 0));
                }, IOException.class);

                UncheckedException thrown = assertThrows(UncheckedException.class, () -> function.applyAsDouble(1));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("1", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            ThrowingIntToDoubleFunction<IOException> function = ThrowingIntToDoubleFunction.checked(i -> {
                throw new IllegalStateException(Integer.toString(i));
            }, IOException.class);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> function.applyAsDouble(1));
            assertEquals("1", thrown.getMessage());
        }
    }
}
