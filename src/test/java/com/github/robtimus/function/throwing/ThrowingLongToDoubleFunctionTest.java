/*
 * ThrowingLongToDoubleFunctionTest.java
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
import java.util.function.LongToDoubleFunction;
import java.util.function.ToDoubleFunction;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

@SuppressWarnings("nls")
class ThrowingLongToDoubleFunctionTest {

    @Nested
    class OnErrorThrowAsChecked {

        @Test
        void testNullArgument() {
            ThrowingLongToDoubleFunction<IOException> function = Spied.throwingLongToDoubleFunction(l -> l + 1D);

            assertThrows(NullPointerException.class, () -> function.onErrorThrowAsChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingLongToDoubleFunction<IOException> function = Spied.throwingLongToDoubleFunction(l -> l + 1D);

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingLongToDoubleFunction<ExecutionException> throwing = function.onErrorThrowAsChecked(errorMapper);

            assertEquals(2D, throwing.applyAsDouble(1L));

            verify(function).applyAsDouble(1L);
            verify(function).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingLongToDoubleFunction<IOException> function = Spied.throwingLongToDoubleFunction(l -> {
                throw new IOException(Long.toString(l));
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingLongToDoubleFunction<ExecutionException> throwing = function.onErrorThrowAsChecked(errorMapper);

            ExecutionException thrown = assertThrows(ExecutionException.class, () -> throwing.applyAsDouble(1L));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1", cause.getMessage());

            verify(function).applyAsDouble(1L);
            verify(function).onErrorThrowAsChecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(function, errorMapper);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingLongToDoubleFunction<IOException> function = Spied.throwingLongToDoubleFunction(throwable::throwUnchecked);

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingLongToDoubleFunction<ExecutionException> throwing = function.onErrorThrowAsChecked(errorMapper);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> throwing.applyAsDouble(1L));
            assertEquals("1", thrown.getMessage());

            verify(function).applyAsDouble(1L);
            verify(function).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }
    }

    @Nested
    class OnErrorThrowAsUnchecked {

        @Test
        void testNullArgument() {
            ThrowingLongToDoubleFunction<IOException> function = Spied.throwingLongToDoubleFunction(l -> l + 1D);

            assertThrows(NullPointerException.class, () -> function.onErrorThrowAsUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingLongToDoubleFunction<IOException> function = Spied.throwingLongToDoubleFunction(l -> l + 1D);

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            LongToDoubleFunction throwing = function.onErrorThrowAsUnchecked(errorMapper);

            assertEquals(2D, throwing.applyAsDouble(1L));

            verify(function).applyAsDouble(1L);
            verify(function).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingLongToDoubleFunction<IOException> function = Spied.throwingLongToDoubleFunction(l -> {
                throw new IOException(Long.toString(l));
            });

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            LongToDoubleFunction throwing = function.onErrorThrowAsUnchecked(errorMapper);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> throwing.applyAsDouble(1L));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1", cause.getMessage());

            verify(function).applyAsDouble(1L);
            verify(function).onErrorThrowAsUnchecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(function, errorMapper);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingLongToDoubleFunction<IOException> function = Spied.throwingLongToDoubleFunction(throwable::throwUnchecked);

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            LongToDoubleFunction throwing = function.onErrorThrowAsUnchecked(errorMapper);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> throwing.applyAsDouble(1L));
            assertEquals("1", thrown.getMessage());

            verify(function).applyAsDouble(1L);
            verify(function).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }
    }

    @Nested
    class OnErrorHandleChecked {

        @Test
        void testNullArgument() {
            ThrowingLongToDoubleFunction<IOException> function = Spied.throwingLongToDoubleFunction(l -> l + 1D);

            assertThrows(NullPointerException.class, () -> function.onErrorHandleChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingLongToDoubleFunction<IOException> function = Spied.throwingLongToDoubleFunction(l -> l + 1D);

            ThrowingToDoubleFunction<IOException, ExecutionException> errorHandler = Spied.throwingToDoubleFunction(e -> 0D);

            ThrowingLongToDoubleFunction<ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

            assertEquals(2D, handling.applyAsDouble(1L));

            verify(function).applyAsDouble(1L);
            verify(function).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException, ExecutionException {
                ThrowingLongToDoubleFunction<IOException> function = Spied.throwingLongToDoubleFunction(l -> {
                    throw new IOException(Long.toString(l));
                });

                ThrowingToDoubleFunction<IOException, ExecutionException> errorHandler = Spied.throwingToDoubleFunction(e -> 0D);

                ThrowingLongToDoubleFunction<ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

                assertEquals(0D, handling.applyAsDouble(1L));

                verify(function).applyAsDouble(1L);
                verify(function).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsDouble(any());
                verifyNoMoreInteractions(function, errorHandler);
            }

            @Test
            void testHandlerThrowsChecked() throws IOException, ExecutionException {
                ThrowingLongToDoubleFunction<IOException> function = Spied.throwingLongToDoubleFunction(l -> {
                    throw new IOException(Long.toString(l));
                });

                ThrowingToDoubleFunction<IOException, ExecutionException> errorHandler = Spied.throwingToDoubleFunction(e -> {
                    throw new ExecutionException(e);
                });

                ThrowingLongToDoubleFunction<ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

                ExecutionException thrown = assertThrows(ExecutionException.class, () -> handling.applyAsDouble(1L));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("1", cause.getMessage());

                verify(function).applyAsDouble(1L);
                verify(function).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsDouble(any());
                verifyNoMoreInteractions(function, errorHandler);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testHandlerThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ExecutionException {
                ThrowingLongToDoubleFunction<IOException> function = Spied.throwingLongToDoubleFunction(l -> {
                    throw new IOException(Long.toString(l));
                });

                ThrowingToDoubleFunction<IOException, ExecutionException> errorHandler = Spied.throwingToDoubleFunction(throwable::throwUnchecked);

                ThrowingLongToDoubleFunction<ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.applyAsDouble(1L));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("1", cause.getMessage());

                verify(function).applyAsDouble(1L);
                verify(function).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsDouble(any());
                verifyNoMoreInteractions(function, errorHandler);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingLongToDoubleFunction<IOException> function = Spied.throwingLongToDoubleFunction(throwable::throwUnchecked);

            ThrowingToDoubleFunction<IOException, ExecutionException> errorHandler = Spied.throwingToDoubleFunction(e -> 0D);

            ThrowingLongToDoubleFunction<ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.applyAsDouble(1L));
            assertEquals("1", thrown.getMessage());

            verify(function).applyAsDouble(1L);
            verify(function).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }
    }

    @Nested
    class OnErrorHandleUnchecked {

        @Test
        void testNullArgument() {
            ThrowingLongToDoubleFunction<IOException> function = Spied.throwingLongToDoubleFunction(l -> l + 1D);

            assertThrows(NullPointerException.class, () -> function.onErrorHandleUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingLongToDoubleFunction<IOException> function = Spied.throwingLongToDoubleFunction(l -> l + 1D);

            ToDoubleFunction<IOException> errorHandler = Spied.toDoubleFunction(e -> 0D);

            LongToDoubleFunction handling = function.onErrorHandleUnchecked(errorHandler);

            assertEquals(2D, handling.applyAsDouble(1L));

            verify(function).applyAsDouble(1L);
            verify(function).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException {
                ThrowingLongToDoubleFunction<IOException> function = Spied.throwingLongToDoubleFunction(l -> {
                    throw new IOException(Long.toString(l));
                });

                ToDoubleFunction<IOException> errorHandler = Spied.toDoubleFunction(e -> 0D);

                LongToDoubleFunction handling = function.onErrorHandleUnchecked(errorHandler);

                assertEquals(0D, handling.applyAsDouble(1L));

                verify(function).applyAsDouble(1L);
                verify(function).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).applyAsDouble(any());
                verifyNoMoreInteractions(function, errorHandler);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testHandlerThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingLongToDoubleFunction<IOException> function = Spied.throwingLongToDoubleFunction(l -> {
                    throw new IOException(Long.toString(l));
                });

                ToDoubleFunction<IOException> errorHandler = Spied.toDoubleFunction(throwable::throwUnchecked);

                LongToDoubleFunction handling = function.onErrorHandleUnchecked(errorHandler);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.applyAsDouble(1L));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("1", cause.getMessage());

                verify(function).applyAsDouble(1L);
                verify(function).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).applyAsDouble(any());
                verifyNoMoreInteractions(function, errorHandler);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingLongToDoubleFunction<IOException> function = Spied.throwingLongToDoubleFunction(throwable::throwUnchecked);

            ToDoubleFunction<IOException> errorHandler = Spied.toDoubleFunction(e -> 0D);

            LongToDoubleFunction handling = function.onErrorHandleUnchecked(errorHandler);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.applyAsDouble(1L));
            assertEquals("1", thrown.getMessage());

            verify(function).applyAsDouble(1L);
            verify(function).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }
    }

    @Nested
    class OnErrorApplyChecked {

        @Test
        void testNullArgument() {
            ThrowingLongToDoubleFunction<IOException> function = Spied.throwingLongToDoubleFunction(l -> l + 1D);

            assertThrows(NullPointerException.class, () -> function.onErrorApplyChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingLongToDoubleFunction<IOException> function = Spied.throwingLongToDoubleFunction(l -> l + 1D);

            ThrowingLongToDoubleFunction<ExecutionException> fallback = Spied.throwingLongToDoubleFunction(l -> l * 3D);

            ThrowingLongToDoubleFunction<ExecutionException> applying = function.onErrorApplyChecked(fallback);

            assertEquals(2D, applying.applyAsDouble(1L));

            verify(function).applyAsDouble(1L);
            verify(function).onErrorApplyChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                ThrowingLongToDoubleFunction<IOException> function = Spied.throwingLongToDoubleFunction(l -> {
                    throw new IOException(Long.toString(l));
                });

                ThrowingLongToDoubleFunction<ParseException> fallback = Spied.throwingLongToDoubleFunction(l -> l * 3D);

                ThrowingLongToDoubleFunction<ParseException> applying = function.onErrorApplyChecked(fallback);

                assertEquals(3D, applying.applyAsDouble(1L));

                verify(function).applyAsDouble(1L);
                verify(function).onErrorApplyChecked(fallback);
                verify(fallback).applyAsDouble(1L);
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                ThrowingLongToDoubleFunction<IOException> function = Spied.throwingLongToDoubleFunction(l -> {
                    throw new IOException(Long.toString(l));
                });

                ThrowingLongToDoubleFunction<ParseException> fallback = Spied.throwingLongToDoubleFunction(l -> {
                    throw new ParseException(Long.toString(l), 0);
                });

                ThrowingLongToDoubleFunction<ParseException> applying = function.onErrorApplyChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> applying.applyAsDouble(1L));
                assertEquals("1", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(function).applyAsDouble(1L);
                verify(function).onErrorApplyChecked(fallback);
                verify(fallback).applyAsDouble(1L);
                verifyNoMoreInteractions(function, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ParseException {
                ThrowingLongToDoubleFunction<IOException> function = Spied.throwingLongToDoubleFunction(l -> {
                    throw new IOException(Long.toString(l));
                });

                ThrowingLongToDoubleFunction<ParseException> fallback = Spied.throwingLongToDoubleFunction(throwable::throwUnchecked);

                ThrowingLongToDoubleFunction<ParseException> applying = function.onErrorApplyChecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.applyAsDouble(1L));
                assertEquals("1", thrown.getMessage());

                verify(function).applyAsDouble(1L);
                verify(function).onErrorApplyChecked(fallback);
                verify(fallback).applyAsDouble(1L);
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingLongToDoubleFunction<IOException> function = Spied.throwingLongToDoubleFunction(throwable::throwUnchecked);

            ThrowingLongToDoubleFunction<ParseException> fallback = Spied.throwingLongToDoubleFunction(l -> l * 3D);

            ThrowingLongToDoubleFunction<ParseException> applying = function.onErrorApplyChecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.applyAsDouble(1L));
            assertEquals("1", thrown.getMessage());

            verify(function).applyAsDouble(1L);
            verify(function).onErrorApplyChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorApplyUnchecked {

        @Test
        void testNullArgument() {
            ThrowingLongToDoubleFunction<IOException> function = Spied.throwingLongToDoubleFunction(l -> l + 1D);

            assertThrows(NullPointerException.class, () -> function.onErrorApplyUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingLongToDoubleFunction<IOException> function = Spied.throwingLongToDoubleFunction(l -> l + 1D);

            LongToDoubleFunction fallback = Spied.longToDoubleFunction(l -> l * 3D);

            LongToDoubleFunction applying = function.onErrorApplyUnchecked(fallback);

            assertEquals(2D, applying.applyAsDouble(1L));

            verify(function).applyAsDouble(1L);
            verify(function).onErrorApplyUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                ThrowingLongToDoubleFunction<IOException> function = Spied.throwingLongToDoubleFunction(l -> {
                    throw new IOException(Long.toString(l));
                });

                LongToDoubleFunction fallback = Spied.longToDoubleFunction(l -> l * 3D);

                LongToDoubleFunction applying = function.onErrorApplyUnchecked(fallback);

                assertEquals(3D, applying.applyAsDouble(1L));

                verify(function).applyAsDouble(1L);
                verify(function).onErrorApplyUnchecked(fallback);
                verify(fallback).applyAsDouble(1L);
                verifyNoMoreInteractions(function, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingLongToDoubleFunction<IOException> function = Spied.throwingLongToDoubleFunction(l -> {
                    throw new IOException(Long.toString(l));
                });

                LongToDoubleFunction fallback = Spied.longToDoubleFunction(throwable::throwUnchecked);

                LongToDoubleFunction applying = function.onErrorApplyUnchecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.applyAsDouble(1L));
                assertEquals("1", thrown.getMessage());

                verify(function).applyAsDouble(1L);
                verify(function).onErrorApplyUnchecked(fallback);
                verify(fallback).applyAsDouble(1L);
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingLongToDoubleFunction<IOException> function = Spied.throwingLongToDoubleFunction(throwable::throwUnchecked);

            LongToDoubleFunction fallback = Spied.longToDoubleFunction(l -> l * 3D);

            LongToDoubleFunction applying = function.onErrorApplyUnchecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.applyAsDouble(1L));
            assertEquals("1", thrown.getMessage());

            verify(function).applyAsDouble(1L);
            verify(function).onErrorApplyUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorGetChecked {

        @Test
        void testNullArgument() {
            ThrowingLongToDoubleFunction<IOException> function = Spied.throwingLongToDoubleFunction(l -> l + 1D);

            assertThrows(NullPointerException.class, () -> function.onErrorGetChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingLongToDoubleFunction<IOException> function = Spied.throwingLongToDoubleFunction(l -> l + 1D);

            ThrowingDoubleSupplier<ExecutionException> fallback = Spied.throwingDoubleSupplier(() -> 0D);

            ThrowingLongToDoubleFunction<ExecutionException> getting = function.onErrorGetChecked(fallback);

            assertEquals(2D, getting.applyAsDouble(1L));

            verify(function).applyAsDouble(1L);
            verify(function).onErrorGetChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                ThrowingLongToDoubleFunction<IOException> function = Spied.throwingLongToDoubleFunction(l -> {
                    throw new IOException(Long.toString(l));
                });

                ThrowingDoubleSupplier<ParseException> fallback = Spied.throwingDoubleSupplier(() -> 0D);

                ThrowingLongToDoubleFunction<ParseException> getting = function.onErrorGetChecked(fallback);

                assertEquals(0D, getting.applyAsDouble(1L));

                verify(function).applyAsDouble(1L);
                verify(function).onErrorGetChecked(fallback);
                verify(fallback).getAsDouble();
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                ThrowingLongToDoubleFunction<IOException> function = Spied.throwingLongToDoubleFunction(l -> {
                    throw new IOException(Long.toString(l));
                });

                ThrowingDoubleSupplier<ParseException> fallback = Spied.throwingDoubleSupplier(() -> {
                    throw new ParseException("bar", 0);
                });

                ThrowingLongToDoubleFunction<ParseException> getting = function.onErrorGetChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> getting.applyAsDouble(1L));
                assertEquals("bar", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(function).applyAsDouble(1L);
                verify(function).onErrorGetChecked(fallback);
                verify(fallback).getAsDouble();
                verifyNoMoreInteractions(function, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ParseException {
                ThrowingLongToDoubleFunction<IOException> function = Spied.throwingLongToDoubleFunction(l -> {
                    throw new IOException(Long.toString(l));
                });

                ThrowingDoubleSupplier<ParseException> fallback = Spied.throwingDoubleSupplier(() -> throwable.throwUnchecked("bar"));

                ThrowingLongToDoubleFunction<ParseException> getting = function.onErrorGetChecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.applyAsDouble(1L));
                assertEquals("bar", thrown.getMessage());

                verify(function).applyAsDouble(1L);
                verify(function).onErrorGetChecked(fallback);
                verify(fallback).getAsDouble();
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingLongToDoubleFunction<IOException> function = Spied.throwingLongToDoubleFunction(throwable::throwUnchecked);

            ThrowingDoubleSupplier<ParseException> fallback = Spied.throwingDoubleSupplier(() -> 0D);

            ThrowingLongToDoubleFunction<ParseException> getting = function.onErrorGetChecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.applyAsDouble(1L));
            assertEquals("1", thrown.getMessage());

            verify(function).applyAsDouble(1L);
            verify(function).onErrorGetChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorGetUnchecked {

        @Test
        void testNullArgument() {
            ThrowingLongToDoubleFunction<IOException> function = Spied.throwingLongToDoubleFunction(l -> l + 1D);

            assertThrows(NullPointerException.class, () -> function.onErrorGetUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingLongToDoubleFunction<IOException> function = Spied.throwingLongToDoubleFunction(l -> l + 1D);

            DoubleSupplier fallback = Spied.doubleSupplier(() -> 0D);

            LongToDoubleFunction getting = function.onErrorGetUnchecked(fallback);

            assertEquals(2D, getting.applyAsDouble(1L));

            verify(function).applyAsDouble(1L);
            verify(function).onErrorGetUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                ThrowingLongToDoubleFunction<IOException> function = Spied.throwingLongToDoubleFunction(l -> {
                    throw new IOException(Long.toString(l));
                });

                DoubleSupplier fallback = Spied.doubleSupplier(() -> 0D);

                LongToDoubleFunction getting = function.onErrorGetUnchecked(fallback);

                assertEquals(0D, getting.applyAsDouble(1L));

                verify(function).applyAsDouble(1L);
                verify(function).onErrorGetUnchecked(fallback);
                verify(fallback).getAsDouble();
                verifyNoMoreInteractions(function, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingLongToDoubleFunction<IOException> function = Spied.throwingLongToDoubleFunction(l -> {
                    throw new IOException(Long.toString(l));
                });

                DoubleSupplier fallback = Spied.doubleSupplier(() -> throwable.throwUnchecked("bar"));

                LongToDoubleFunction getting = function.onErrorGetUnchecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.applyAsDouble(1L));
                assertEquals("bar", thrown.getMessage());

                verify(function).applyAsDouble(1L);
                verify(function).onErrorGetUnchecked(fallback);
                verify(fallback).getAsDouble();
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingLongToDoubleFunction<IOException> function = Spied.throwingLongToDoubleFunction(throwable::throwUnchecked);

            DoubleSupplier fallback = Spied.doubleSupplier(() -> 0D);

            LongToDoubleFunction getting = function.onErrorGetUnchecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.applyAsDouble(1L));
            assertEquals("1", thrown.getMessage());

            verify(function).applyAsDouble(1L);
            verify(function).onErrorGetUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorReturn {

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingLongToDoubleFunction<IOException> function = Spied.throwingLongToDoubleFunction(l -> l + 1D);

            LongToDoubleFunction returning = function.onErrorReturn(0D);

            assertEquals(2D, returning.applyAsDouble(1L));

            verify(function).applyAsDouble(1L);
            verify(function).onErrorReturn(0D);
            verifyNoMoreInteractions(function);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingLongToDoubleFunction<IOException> function = Spied.throwingLongToDoubleFunction(l -> {
                throw new IOException(Long.toString(l));
            });

            LongToDoubleFunction returning = function.onErrorReturn(0D);

            assertEquals(0D, returning.applyAsDouble(1L));

            verify(function).applyAsDouble(1L);
            verify(function).onErrorReturn(0D);
            verifyNoMoreInteractions(function);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingLongToDoubleFunction<IOException> function = Spied.throwingLongToDoubleFunction(throwable::throwUnchecked);

            LongToDoubleFunction returning = function.onErrorReturn(0D);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> returning.applyAsDouble(1L));
            assertEquals("1", thrown.getMessage());

            verify(function).applyAsDouble(1L);
            verify(function).onErrorReturn(0D);
            verifyNoMoreInteractions(function);
        }
    }

    @Nested
    class Unchecked {

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingLongToDoubleFunction<IOException> function = Spied.throwingLongToDoubleFunction(l -> l + 1D);

            LongToDoubleFunction unchecked = function.unchecked();

            assertEquals(2D, unchecked.applyAsDouble(1L));

            verify(function).applyAsDouble(1L);
            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(function);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingLongToDoubleFunction<IOException> function = Spied.throwingLongToDoubleFunction(l -> {
                throw new IOException(Long.toString(l));
            });

            LongToDoubleFunction unchecked = function.unchecked();

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.applyAsDouble(1L));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1", cause.getMessage());

            verify(function).applyAsDouble(1L);
            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(function);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingLongToDoubleFunction<IOException> function = Spied.throwingLongToDoubleFunction(throwable::throwUnchecked);

            LongToDoubleFunction unchecked = function.unchecked();

            Throwable thrown = assertThrows(throwable.throwableType(), () -> unchecked.applyAsDouble(1L));
            assertEquals("1", thrown.getMessage());

            verify(function).applyAsDouble(1L);
            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(function);
        }
    }

    @Nested
    class Of {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> ThrowingLongToDoubleFunction.of(null));
        }

        @Test
        void testNonNullArgument() throws IOException {
            ThrowingLongToDoubleFunction<IOException> function = ThrowingLongToDoubleFunction.of(l -> l + 1D);

            assertEquals(2D, function.applyAsDouble(1L));
        }
    }

    @Nested
    class UncheckedFactory {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> ThrowingLongToDoubleFunction.unchecked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ThrowingLongToDoubleFunction<IOException> function = Spied.throwingLongToDoubleFunction(l -> l + 1D);

            LongToDoubleFunction unchecked = ThrowingLongToDoubleFunction.unchecked(function);

            assertEquals(2D, unchecked.applyAsDouble(1L));

            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verify(function).applyAsDouble(1L);
            verifyNoMoreInteractions(function);
        }

        @Test
        void testArgumentThrowsChecked() throws IOException {
            ThrowingLongToDoubleFunction<IOException> function = Spied.throwingLongToDoubleFunction(l -> {
                throw new IOException(Long.toString(l));
            });

            LongToDoubleFunction unchecked = ThrowingLongToDoubleFunction.unchecked(function);

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.applyAsDouble(1L));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1", cause.getMessage());

            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verify(function).applyAsDouble(1L);
            verifyNoMoreInteractions(function);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testArgumentThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingLongToDoubleFunction<IOException> function = Spied.throwingLongToDoubleFunction(throwable::throwUnchecked);

            LongToDoubleFunction unchecked = ThrowingLongToDoubleFunction.unchecked(function);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> unchecked.applyAsDouble(1L));
            assertEquals("1", thrown.getMessage());

            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verify(function).applyAsDouble(1L);
            verifyNoMoreInteractions(function);
        }
    }

    @Nested
    class CheckedWrapper {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> ThrowingLongToDoubleFunction.checked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ThrowingLongToDoubleFunction<IOException> function = ThrowingLongToDoubleFunction.checked(l -> l + 1D);

            assertEquals(2D, function.applyAsDouble(1L));
        }

        @Test
        void testArgumentThrowsUnchecked() {
            ThrowingLongToDoubleFunction<IOException> function = ThrowingLongToDoubleFunction.checked(l -> {
                throw UncheckedException.withoutStackTrace(Long.toString(l), new IOException());
            });

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> function.applyAsDouble(1L));
            assertEquals("1", thrown.getMessage());
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertNull(cause.getMessage());
        }
    }

    @Nested
    class CheckedUnwrapper {

        @Test
        void testNullArguments() {
            LongToDoubleFunction function = l -> l + 1D;

            assertThrows(NullPointerException.class, () -> ThrowingLongToDoubleFunction.checked(null, IOException.class));
            assertThrows(NullPointerException.class, () -> ThrowingLongToDoubleFunction.checked(function, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ThrowingLongToDoubleFunction<IOException> function = ThrowingLongToDoubleFunction.checked(l -> l + 1D, IOException.class);

            assertEquals(2D, function.applyAsDouble(1L));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                ThrowingLongToDoubleFunction<IOException> function = ThrowingLongToDoubleFunction.checked(l -> {
                    throw UncheckedException.withoutStackTrace(new IOException(Long.toString(l)));
                }, IOException.class);

                IOException thrown = assertThrows(IOException.class, () -> function.applyAsDouble(1L));
                assertEquals("1", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                ThrowingLongToDoubleFunction<IOException> function = ThrowingLongToDoubleFunction.checked(l -> {
                    throw UncheckedException.withoutStackTrace(new FileNotFoundException(Long.toString(l)));
                }, IOException.class);

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class, () -> function.applyAsDouble(1L));
                assertEquals("1", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                ThrowingLongToDoubleFunction<IOException> function = ThrowingLongToDoubleFunction.checked(l -> {
                    throw UncheckedException.withoutStackTrace(new ParseException(Long.toString(l), 0));
                }, IOException.class);

                UncheckedException thrown = assertThrows(UncheckedException.class, () -> function.applyAsDouble(1L));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("1", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            ThrowingLongToDoubleFunction<IOException> function = ThrowingLongToDoubleFunction.checked(l -> {
                throw new IllegalStateException(Long.toString(l));
            }, IOException.class);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> function.applyAsDouble(1L));
            assertEquals("1", thrown.getMessage());
        }
    }

    @Nested
    class InvokeAndUnwrap {

        @Test
        void testNullArguments() {
            LongToDoubleFunction function = l -> String.valueOf(Long.toString(l)).length();

            assertThrows(NullPointerException.class, () -> ThrowingLongToDoubleFunction.invokeAndUnwrap(null, 1L, IOException.class));
            assertThrows(NullPointerException.class, () -> ThrowingLongToDoubleFunction.invokeAndUnwrap(function, 1L, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            LongToDoubleFunction function = l -> l + 1D;

            assertEquals(2D, ThrowingLongToDoubleFunction.invokeAndUnwrap(function, 1L, IOException.class));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                LongToDoubleFunction function = l -> {
                    throw UncheckedException.withoutStackTrace(new IOException(Long.toString(l)));
                };

                IOException thrown = assertThrows(IOException.class,
                        () -> ThrowingLongToDoubleFunction.invokeAndUnwrap(function, 1L, IOException.class));
                assertEquals("1", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                LongToDoubleFunction function = l -> {
                    throw UncheckedException.withoutStackTrace(new FileNotFoundException(Long.toString(l)));
                };

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class,
                        () -> ThrowingLongToDoubleFunction.invokeAndUnwrap(function, 1L, IOException.class));
                assertEquals("1", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                LongToDoubleFunction function = l -> {
                    throw UncheckedException.withoutStackTrace(new ParseException(Long.toString(l), 0));
                };

                UncheckedException thrown = assertThrows(UncheckedException.class,
                        () -> ThrowingLongToDoubleFunction.invokeAndUnwrap(function, 1L, IOException.class));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("1", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            LongToDoubleFunction function = l -> {
                throw new IllegalStateException(Long.toString(l));
            };

            IllegalStateException thrown = assertThrows(IllegalStateException.class,
                    () -> ThrowingLongToDoubleFunction.invokeAndUnwrap(function, 1L, IOException.class));
            assertEquals("1", thrown.getMessage());
        }
    }
}
