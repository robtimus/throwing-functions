/*
 * ThrowingDoubleToIntFunctionTest.java
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
import java.util.function.DoubleToIntFunction;
import java.util.function.Function;
import java.util.function.IntSupplier;
import java.util.function.ToIntFunction;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

@SuppressWarnings("nls")
class ThrowingDoubleToIntFunctionTest {

    @Nested
    class OnErrorThrowAsChecked {

        @Test
        void testNullArgument() {
            ThrowingDoubleToIntFunction<IOException> function = Spied.throwingDoubleToIntFunction(d -> (int) d + 1);

            assertThrows(NullPointerException.class, () -> function.onErrorThrowAsChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingDoubleToIntFunction<IOException> function = Spied.throwingDoubleToIntFunction(d -> (int) d + 1);

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingDoubleToIntFunction<ExecutionException> throwing = function.onErrorThrowAsChecked(errorMapper);

            assertEquals(2, throwing.applyAsInt(1D));

            verify(function).applyAsInt(1D);
            verify(function).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingDoubleToIntFunction<IOException> function = Spied.throwingDoubleToIntFunction(d -> {
                throw new IOException(Double.toString(d));
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingDoubleToIntFunction<ExecutionException> throwing = function.onErrorThrowAsChecked(errorMapper);

            ExecutionException thrown = assertThrows(ExecutionException.class, () -> throwing.applyAsInt(1D));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1.0", cause.getMessage());

            verify(function).applyAsInt(1D);
            verify(function).onErrorThrowAsChecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(function, errorMapper);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingDoubleToIntFunction<IOException> function = Spied.throwingDoubleToIntFunction(throwable::throwUnchecked);

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingDoubleToIntFunction<ExecutionException> throwing = function.onErrorThrowAsChecked(errorMapper);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> throwing.applyAsInt(1D));
            assertEquals("1.0", thrown.getMessage());

            verify(function).applyAsInt(1D);
            verify(function).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }
    }

    @Nested
    class OnErrorThrowAsUnchecked {

        @Test
        void testNullArgument() {
            ThrowingDoubleToIntFunction<IOException> function = Spied.throwingDoubleToIntFunction(d -> (int) d + 1);

            assertThrows(NullPointerException.class, () -> function.onErrorThrowAsUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingDoubleToIntFunction<IOException> function = Spied.throwingDoubleToIntFunction(d -> (int) d + 1);

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            DoubleToIntFunction throwing = function.onErrorThrowAsUnchecked(errorMapper);

            assertEquals(2, throwing.applyAsInt(1D));

            verify(function).applyAsInt(1D);
            verify(function).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingDoubleToIntFunction<IOException> function = Spied.throwingDoubleToIntFunction(d -> {
                throw new IOException(Double.toString(d));
            });

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            DoubleToIntFunction throwing = function.onErrorThrowAsUnchecked(errorMapper);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> throwing.applyAsInt(1D));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1.0", cause.getMessage());

            verify(function).applyAsInt(1D);
            verify(function).onErrorThrowAsUnchecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(function, errorMapper);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingDoubleToIntFunction<IOException> function = Spied.throwingDoubleToIntFunction(throwable::throwUnchecked);

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            DoubleToIntFunction throwing = function.onErrorThrowAsUnchecked(errorMapper);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> throwing.applyAsInt(1D));
            assertEquals("1.0", thrown.getMessage());

            verify(function).applyAsInt(1D);
            verify(function).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }
    }

    @Nested
    class OnErrorHandleChecked {

        @Test
        void testNullArgument() {
            ThrowingDoubleToIntFunction<IOException> function = Spied.throwingDoubleToIntFunction(d -> (int) d + 1);

            assertThrows(NullPointerException.class, () -> function.onErrorHandleChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingDoubleToIntFunction<IOException> function = Spied.throwingDoubleToIntFunction(d -> (int) d + 1);

            ThrowingToIntFunction<IOException, ExecutionException> errorHandler = Spied.throwingToIntFunction(e -> 0);

            ThrowingDoubleToIntFunction<ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

            assertEquals(2, handling.applyAsInt(1D));

            verify(function).applyAsInt(1D);
            verify(function).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException, ExecutionException {
                ThrowingDoubleToIntFunction<IOException> function = Spied.throwingDoubleToIntFunction(d -> {
                    throw new IOException(Double.toString(d));
                });

                ThrowingToIntFunction<IOException, ExecutionException> errorHandler = Spied.throwingToIntFunction(e -> 0);

                ThrowingDoubleToIntFunction<ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

                assertEquals(0, handling.applyAsInt(1D));

                verify(function).applyAsInt(1D);
                verify(function).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsInt(any());
                verifyNoMoreInteractions(function, errorHandler);
            }

            @Test
            void testHandlerThrowsChecked() throws IOException, ExecutionException {
                ThrowingDoubleToIntFunction<IOException> function = Spied.throwingDoubleToIntFunction(d -> {
                    throw new IOException(Double.toString(d));
                });

                ThrowingToIntFunction<IOException, ExecutionException> errorHandler = Spied.throwingToIntFunction(e -> {
                    throw new ExecutionException(e);
                });

                ThrowingDoubleToIntFunction<ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

                ExecutionException thrown = assertThrows(ExecutionException.class, () -> handling.applyAsInt(1D));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("1.0", cause.getMessage());

                verify(function).applyAsInt(1D);
                verify(function).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsInt(any());
                verifyNoMoreInteractions(function, errorHandler);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testHandlerThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ExecutionException {
                ThrowingDoubleToIntFunction<IOException> function = Spied.throwingDoubleToIntFunction(d -> {
                    throw new IOException(Double.toString(d));
                });

                ThrowingToIntFunction<IOException, ExecutionException> errorHandler = Spied.throwingToIntFunction(throwable::throwUnchecked);

                ThrowingDoubleToIntFunction<ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.applyAsInt(1D));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("1.0", cause.getMessage());

                verify(function).applyAsInt(1D);
                verify(function).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsInt(any());
                verifyNoMoreInteractions(function, errorHandler);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingDoubleToIntFunction<IOException> function = Spied.throwingDoubleToIntFunction(throwable::throwUnchecked);

            ThrowingToIntFunction<IOException, ExecutionException> errorHandler = Spied.throwingToIntFunction(e -> 0);

            ThrowingDoubleToIntFunction<ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.applyAsInt(1D));
            assertEquals("1.0", thrown.getMessage());

            verify(function).applyAsInt(1D);
            verify(function).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }
    }

    @Nested
    class OnErrorHandleUnchecked {

        @Test
        void testNullArgument() {
            ThrowingDoubleToIntFunction<IOException> function = Spied.throwingDoubleToIntFunction(d -> (int) d + 1);

            assertThrows(NullPointerException.class, () -> function.onErrorHandleUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingDoubleToIntFunction<IOException> function = Spied.throwingDoubleToIntFunction(d -> (int) d + 1);

            ToIntFunction<IOException> errorHandler = Spied.toIntFunction(e -> 0);

            DoubleToIntFunction handling = function.onErrorHandleUnchecked(errorHandler);

            assertEquals(2, handling.applyAsInt(1D));

            verify(function).applyAsInt(1D);
            verify(function).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException {
                ThrowingDoubleToIntFunction<IOException> function = Spied.throwingDoubleToIntFunction(d -> {
                    throw new IOException(Double.toString(d));
                });

                ToIntFunction<IOException> errorHandler = Spied.toIntFunction(e -> 0);

                DoubleToIntFunction handling = function.onErrorHandleUnchecked(errorHandler);

                assertEquals(0, handling.applyAsInt(1D));

                verify(function).applyAsInt(1D);
                verify(function).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).applyAsInt(any());
                verifyNoMoreInteractions(function, errorHandler);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testHandlerThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingDoubleToIntFunction<IOException> function = Spied.throwingDoubleToIntFunction(d -> {
                    throw new IOException(Double.toString(d));
                });

                ToIntFunction<IOException> errorHandler = Spied.toIntFunction(throwable::throwUnchecked);

                DoubleToIntFunction handling = function.onErrorHandleUnchecked(errorHandler);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.applyAsInt(1D));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("1.0", cause.getMessage());

                verify(function).applyAsInt(1D);
                verify(function).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).applyAsInt(any());
                verifyNoMoreInteractions(function, errorHandler);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingDoubleToIntFunction<IOException> function = Spied.throwingDoubleToIntFunction(throwable::throwUnchecked);

            ToIntFunction<IOException> errorHandler = Spied.toIntFunction(e -> 0);

            DoubleToIntFunction handling = function.onErrorHandleUnchecked(errorHandler);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.applyAsInt(1D));
            assertEquals("1.0", thrown.getMessage());

            verify(function).applyAsInt(1D);
            verify(function).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }
    }

    @Nested
    class OnErrorApplyChecked {

        @Test
        void testNullArgument() {
            ThrowingDoubleToIntFunction<IOException> function = Spied.throwingDoubleToIntFunction(d -> (int) d + 1);

            assertThrows(NullPointerException.class, () -> function.onErrorApplyChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingDoubleToIntFunction<IOException> function = Spied.throwingDoubleToIntFunction(d -> (int) d + 1);

            ThrowingDoubleToIntFunction<ExecutionException> fallback = Spied.throwingDoubleToIntFunction(d -> (int) d * 3);

            ThrowingDoubleToIntFunction<ExecutionException> applying = function.onErrorApplyChecked(fallback);

            assertEquals(2, applying.applyAsInt(1D));

            verify(function).applyAsInt(1D);
            verify(function).onErrorApplyChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                ThrowingDoubleToIntFunction<IOException> function = Spied.throwingDoubleToIntFunction(d -> {
                    throw new IOException(Double.toString(d));
                });

                ThrowingDoubleToIntFunction<ParseException> fallback = Spied.throwingDoubleToIntFunction(d -> (int) d * 3);

                ThrowingDoubleToIntFunction<ParseException> applying = function.onErrorApplyChecked(fallback);

                assertEquals(3, applying.applyAsInt(1D));

                verify(function).applyAsInt(1D);
                verify(function).onErrorApplyChecked(fallback);
                verify(fallback).applyAsInt(1D);
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                ThrowingDoubleToIntFunction<IOException> function = Spied.throwingDoubleToIntFunction(d -> {
                    throw new IOException(Double.toString(d));
                });

                ThrowingDoubleToIntFunction<ParseException> fallback = Spied.throwingDoubleToIntFunction(d -> {
                    throw new ParseException(Double.toString(d), 0);
                });

                ThrowingDoubleToIntFunction<ParseException> applying = function.onErrorApplyChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> applying.applyAsInt(1D));
                assertEquals("1.0", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(function).applyAsInt(1D);
                verify(function).onErrorApplyChecked(fallback);
                verify(fallback).applyAsInt(1D);
                verifyNoMoreInteractions(function, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ParseException {
                ThrowingDoubleToIntFunction<IOException> function = Spied.throwingDoubleToIntFunction(d -> {
                    throw new IOException(Double.toString(d));
                });

                ThrowingDoubleToIntFunction<ParseException> fallback = Spied
                        .throwingDoubleToIntFunction(throwable::throwUnchecked);

                ThrowingDoubleToIntFunction<ParseException> applying = function.onErrorApplyChecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.applyAsInt(1D));
                assertEquals("1.0", thrown.getMessage());

                verify(function).applyAsInt(1D);
                verify(function).onErrorApplyChecked(fallback);
                verify(fallback).applyAsInt(1D);
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingDoubleToIntFunction<IOException> function = Spied.throwingDoubleToIntFunction(throwable::throwUnchecked);

            ThrowingDoubleToIntFunction<ParseException> fallback = Spied.throwingDoubleToIntFunction(d -> (int) d * 3);

            ThrowingDoubleToIntFunction<ParseException> applying = function.onErrorApplyChecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.applyAsInt(1D));
            assertEquals("1.0", thrown.getMessage());

            verify(function).applyAsInt(1D);
            verify(function).onErrorApplyChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorApplyUnchecked {

        @Test
        void testNullArgument() {
            ThrowingDoubleToIntFunction<IOException> function = Spied.throwingDoubleToIntFunction(d -> (int) d + 1);

            assertThrows(NullPointerException.class, () -> function.onErrorApplyUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingDoubleToIntFunction<IOException> function = Spied.throwingDoubleToIntFunction(d -> (int) d + 1);

            DoubleToIntFunction fallback = Spied.doubleToIntFunction(d -> (int) d * 3);

            DoubleToIntFunction applying = function.onErrorApplyUnchecked(fallback);

            assertEquals(2, applying.applyAsInt(1D));

            verify(function).applyAsInt(1D);
            verify(function).onErrorApplyUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                ThrowingDoubleToIntFunction<IOException> function = Spied.throwingDoubleToIntFunction(d -> {
                    throw new IOException(Double.toString(d));
                });

                DoubleToIntFunction fallback = Spied.doubleToIntFunction(d -> (int) d * 3);

                DoubleToIntFunction applying = function.onErrorApplyUnchecked(fallback);

                assertEquals(3, applying.applyAsInt(1D));

                verify(function).applyAsInt(1D);
                verify(function).onErrorApplyUnchecked(fallback);
                verify(fallback).applyAsInt(1D);
                verifyNoMoreInteractions(function, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingDoubleToIntFunction<IOException> function = Spied.throwingDoubleToIntFunction(d -> {
                    throw new IOException(Double.toString(d));
                });

                DoubleToIntFunction fallback = Spied.doubleToIntFunction(throwable::throwUnchecked);

                DoubleToIntFunction applying = function.onErrorApplyUnchecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.applyAsInt(1D));
                assertEquals("1.0", thrown.getMessage());

                verify(function).applyAsInt(1D);
                verify(function).onErrorApplyUnchecked(fallback);
                verify(fallback).applyAsInt(1D);
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingDoubleToIntFunction<IOException> function = Spied.throwingDoubleToIntFunction(throwable::throwUnchecked);

            DoubleToIntFunction fallback = Spied.doubleToIntFunction(d -> (int) d * 3);

            DoubleToIntFunction applying = function.onErrorApplyUnchecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.applyAsInt(1D));
            assertEquals("1.0", thrown.getMessage());

            verify(function).applyAsInt(1D);
            verify(function).onErrorApplyUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorGetChecked {

        @Test
        void testNullArgument() {
            ThrowingDoubleToIntFunction<IOException> function = Spied.throwingDoubleToIntFunction(d -> (int) d + 1);

            assertThrows(NullPointerException.class, () -> function.onErrorGetChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingDoubleToIntFunction<IOException> function = Spied.throwingDoubleToIntFunction(d -> (int) d + 1);

            ThrowingIntSupplier<ExecutionException> fallback = Spied.throwingIntSupplier(() -> 0);

            ThrowingDoubleToIntFunction<ExecutionException> getting = function.onErrorGetChecked(fallback);

            assertEquals(2, getting.applyAsInt(1D));

            verify(function).applyAsInt(1D);
            verify(function).onErrorGetChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                ThrowingDoubleToIntFunction<IOException> function = Spied.throwingDoubleToIntFunction(d -> {
                    throw new IOException(Double.toString(d));
                });

                ThrowingIntSupplier<ParseException> fallback = Spied.throwingIntSupplier(() -> 0);

                ThrowingDoubleToIntFunction<ParseException> getting = function.onErrorGetChecked(fallback);

                assertEquals(0, getting.applyAsInt(1D));

                verify(function).applyAsInt(1D);
                verify(function).onErrorGetChecked(fallback);
                verify(fallback).getAsInt();
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                ThrowingDoubleToIntFunction<IOException> function = Spied.throwingDoubleToIntFunction(d -> {
                    throw new IOException(Double.toString(d));
                });

                ThrowingIntSupplier<ParseException> fallback = Spied.throwingIntSupplier(() -> {
                    throw new ParseException("bar", 0);
                });

                ThrowingDoubleToIntFunction<ParseException> getting = function.onErrorGetChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> getting.applyAsInt(1D));
                assertEquals("bar", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(function).applyAsInt(1D);
                verify(function).onErrorGetChecked(fallback);
                verify(fallback).getAsInt();
                verifyNoMoreInteractions(function, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ParseException {
                ThrowingDoubleToIntFunction<IOException> function = Spied.throwingDoubleToIntFunction(d -> {
                    throw new IOException(Double.toString(d));
                });

                ThrowingIntSupplier<ParseException> fallback = Spied.throwingIntSupplier(() -> throwable.throwUnchecked("bar"));

                ThrowingDoubleToIntFunction<ParseException> getting = function.onErrorGetChecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.applyAsInt(1D));
                assertEquals("bar", thrown.getMessage());

                verify(function).applyAsInt(1D);
                verify(function).onErrorGetChecked(fallback);
                verify(fallback).getAsInt();
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingDoubleToIntFunction<IOException> function = Spied.throwingDoubleToIntFunction(throwable::throwUnchecked);

            ThrowingIntSupplier<ParseException> fallback = Spied.throwingIntSupplier(() -> 0);

            ThrowingDoubleToIntFunction<ParseException> getting = function.onErrorGetChecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.applyAsInt(1D));
            assertEquals("1.0", thrown.getMessage());

            verify(function).applyAsInt(1D);
            verify(function).onErrorGetChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorGetUnchecked {

        @Test
        void testNullArgument() {
            ThrowingDoubleToIntFunction<IOException> function = Spied.throwingDoubleToIntFunction(d -> (int) d + 1);

            assertThrows(NullPointerException.class, () -> function.onErrorGetUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingDoubleToIntFunction<IOException> function = Spied.throwingDoubleToIntFunction(d -> (int) d + 1);

            IntSupplier fallback = Spied.intSupplier(() -> 0);

            DoubleToIntFunction getting = function.onErrorGetUnchecked(fallback);

            assertEquals(2, getting.applyAsInt(1D));

            verify(function).applyAsInt(1D);
            verify(function).onErrorGetUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                ThrowingDoubleToIntFunction<IOException> function = Spied.throwingDoubleToIntFunction(d -> {
                    throw new IOException(Double.toString(d));
                });

                IntSupplier fallback = Spied.intSupplier(() -> 0);

                DoubleToIntFunction getting = function.onErrorGetUnchecked(fallback);

                assertEquals(0, getting.applyAsInt(1D));

                verify(function).applyAsInt(1D);
                verify(function).onErrorGetUnchecked(fallback);
                verify(fallback).getAsInt();
                verifyNoMoreInteractions(function, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingDoubleToIntFunction<IOException> function = Spied.throwingDoubleToIntFunction(d -> {
                    throw new IOException(Double.toString(d));
                });

                IntSupplier fallback = Spied.intSupplier(() -> throwable.throwUnchecked("bar"));

                DoubleToIntFunction getting = function.onErrorGetUnchecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.applyAsInt(1D));
                assertEquals("bar", thrown.getMessage());

                verify(function).applyAsInt(1D);
                verify(function).onErrorGetUnchecked(fallback);
                verify(fallback).getAsInt();
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingDoubleToIntFunction<IOException> function = Spied.throwingDoubleToIntFunction(throwable::throwUnchecked);

            IntSupplier fallback = Spied.intSupplier(() -> 0);

            DoubleToIntFunction getting = function.onErrorGetUnchecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.applyAsInt(1D));
            assertEquals("1.0", thrown.getMessage());

            verify(function).applyAsInt(1D);
            verify(function).onErrorGetUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorReturn {

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingDoubleToIntFunction<IOException> function = Spied.throwingDoubleToIntFunction(d -> (int) d + 1);

            DoubleToIntFunction returning = function.onErrorReturn(0);

            assertEquals(2, returning.applyAsInt(1D));

            verify(function).applyAsInt(1D);
            verify(function).onErrorReturn(0);
            verifyNoMoreInteractions(function);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingDoubleToIntFunction<IOException> function = Spied.throwingDoubleToIntFunction(d -> {
                throw new IOException(Double.toString(d));
            });

            DoubleToIntFunction returning = function.onErrorReturn(0);

            assertEquals(0, returning.applyAsInt(1D));

            verify(function).applyAsInt(1D);
            verify(function).onErrorReturn(0);
            verifyNoMoreInteractions(function);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingDoubleToIntFunction<IOException> function = Spied.throwingDoubleToIntFunction(throwable::throwUnchecked);

            DoubleToIntFunction returning = function.onErrorReturn(0);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> returning.applyAsInt(1D));
            assertEquals("1.0", thrown.getMessage());

            verify(function).applyAsInt(1D);
            verify(function).onErrorReturn(0);
            verifyNoMoreInteractions(function);
        }
    }

    @Nested
    class Unchecked {

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingDoubleToIntFunction<IOException> function = Spied.throwingDoubleToIntFunction(d -> (int) d + 1);

            DoubleToIntFunction unchecked = function.unchecked();

            assertEquals(2, unchecked.applyAsInt(1D));

            verify(function).applyAsInt(1D);
            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(function);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingDoubleToIntFunction<IOException> function = Spied.throwingDoubleToIntFunction(d -> {
                throw new IOException(Double.toString(d));
            });

            DoubleToIntFunction unchecked = function.unchecked();

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.applyAsInt(1D));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1.0", cause.getMessage());

            verify(function).applyAsInt(1D);
            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(function);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingDoubleToIntFunction<IOException> function = Spied.throwingDoubleToIntFunction(throwable::throwUnchecked);

            DoubleToIntFunction unchecked = function.unchecked();

            Throwable thrown = assertThrows(throwable.throwableType(), () -> unchecked.applyAsInt(1D));
            assertEquals("1.0", thrown.getMessage());

            verify(function).applyAsInt(1D);
            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(function);
        }
    }

    @Nested
    class Of {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> ThrowingDoubleToIntFunction.of(null));
        }

        @Test
        void testNonNullArgument() throws IOException {
            ThrowingDoubleToIntFunction<IOException> function = ThrowingDoubleToIntFunction.of(d -> (int) d + 1);

            assertEquals(2, function.applyAsInt(1D));
        }
    }

    @Nested
    class UncheckedFactory {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> ThrowingDoubleToIntFunction.unchecked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ThrowingDoubleToIntFunction<IOException> function = Spied.throwingDoubleToIntFunction(d -> (int) d + 1);

            DoubleToIntFunction unchecked = ThrowingDoubleToIntFunction.unchecked(function);

            assertEquals(2, unchecked.applyAsInt(1D));

            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verify(function).applyAsInt(1D);
            verifyNoMoreInteractions(function);
        }

        @Test
        void testArgumentThrowsChecked() throws IOException {
            ThrowingDoubleToIntFunction<IOException> function = Spied.throwingDoubleToIntFunction(d -> {
                throw new IOException(Double.toString(d));
            });

            DoubleToIntFunction unchecked = ThrowingDoubleToIntFunction.unchecked(function);

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.applyAsInt(1D));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1.0", cause.getMessage());

            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verify(function).applyAsInt(1D);
            verifyNoMoreInteractions(function);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testArgumentThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingDoubleToIntFunction<IOException> function = Spied.throwingDoubleToIntFunction(throwable::throwUnchecked);

            DoubleToIntFunction unchecked = ThrowingDoubleToIntFunction.unchecked(function);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> unchecked.applyAsInt(1D));
            assertEquals("1.0", thrown.getMessage());

            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verify(function).applyAsInt(1D);
            verifyNoMoreInteractions(function);
        }
    }

    @Nested
    class CheckedWrapper {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> ThrowingDoubleToIntFunction.checked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ThrowingDoubleToIntFunction<IOException> function = ThrowingDoubleToIntFunction.checked(d -> (int) d + 1);

            assertEquals(2, function.applyAsInt(1D));
        }

        @Test
        void testArgumentThrowsUnchecked() {
            ThrowingDoubleToIntFunction<IOException> function = ThrowingDoubleToIntFunction.checked(d -> {
                throw new UncheckedException(Double.toString(d), new IOException());
            });

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> function.applyAsInt(1D));
            assertEquals("1.0", thrown.getMessage());
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertNull(cause.getMessage());
        }
    }

    @Nested
    class CheckedUnwrapper {

        @Test
        void testNullArguments() {
            DoubleToIntFunction function = d -> (int) d + 1;

            assertThrows(NullPointerException.class, () -> ThrowingDoubleToIntFunction.checked(null, IOException.class));
            assertThrows(NullPointerException.class, () -> ThrowingDoubleToIntFunction.checked(function, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ThrowingDoubleToIntFunction<IOException> function = ThrowingDoubleToIntFunction.checked(d -> (int) d + 1, IOException.class);

            assertEquals(2, function.applyAsInt(1D));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                ThrowingDoubleToIntFunction<IOException> function = ThrowingDoubleToIntFunction.checked(d -> {
                    throw new UncheckedException(new IOException(Double.toString(d)));
                }, IOException.class);

                IOException thrown = assertThrows(IOException.class, () -> function.applyAsInt(1D));
                assertEquals("1.0", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                ThrowingDoubleToIntFunction<IOException> function = ThrowingDoubleToIntFunction.checked(d -> {
                    throw new UncheckedException(new FileNotFoundException(Double.toString(d)));
                }, IOException.class);

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class, () -> function.applyAsInt(1D));
                assertEquals("1.0", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                ThrowingDoubleToIntFunction<IOException> function = ThrowingDoubleToIntFunction.checked(d -> {
                    throw new UncheckedException(new ParseException(Double.toString(d), 0));
                }, IOException.class);

                UncheckedException thrown = assertThrows(UncheckedException.class, () -> function.applyAsInt(1D));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("1.0", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            ThrowingDoubleToIntFunction<IOException> function = ThrowingDoubleToIntFunction.checked(d -> {
                throw new IllegalStateException(Double.toString(d));
            }, IOException.class);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> function.applyAsInt(1D));
            assertEquals("1.0", thrown.getMessage());
        }
    }

    @Nested
    class InvokeAndUnwrap {

        @Test
        void testNullArguments() {
            DoubleToIntFunction function = d -> String.valueOf(Double.toString(d)).length();

            assertThrows(NullPointerException.class, () -> ThrowingDoubleToIntFunction.invokeAndUnwrap(null, 1D, IOException.class));
            assertThrows(NullPointerException.class, () -> ThrowingDoubleToIntFunction.invokeAndUnwrap(function, 1D, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            DoubleToIntFunction function = d -> (int) d + 1;

            assertEquals(2, ThrowingDoubleToIntFunction.invokeAndUnwrap(function, 1D, IOException.class));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                DoubleToIntFunction function = d -> {
                    throw new UncheckedException(new IOException(Double.toString(d)));
                };

                IOException thrown = assertThrows(IOException.class,
                        () -> ThrowingDoubleToIntFunction.invokeAndUnwrap(function, 1D, IOException.class));
                assertEquals("1.0", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                DoubleToIntFunction function = d -> {
                    throw new UncheckedException(new FileNotFoundException(Double.toString(d)));
                };

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class,
                        () -> ThrowingDoubleToIntFunction.invokeAndUnwrap(function, 1D, IOException.class));
                assertEquals("1.0", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                DoubleToIntFunction function = d -> {
                    throw new UncheckedException(new ParseException(Double.toString(d), 0));
                };

                UncheckedException thrown = assertThrows(UncheckedException.class,
                        () -> ThrowingDoubleToIntFunction.invokeAndUnwrap(function, 1D, IOException.class));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("1.0", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            DoubleToIntFunction function = d -> {
                throw new IllegalStateException(Double.toString(d));
            };

            IllegalStateException thrown = assertThrows(IllegalStateException.class,
                    () -> ThrowingDoubleToIntFunction.invokeAndUnwrap(function, 1D, IOException.class));
            assertEquals("1.0", thrown.getMessage());
        }
    }
}
