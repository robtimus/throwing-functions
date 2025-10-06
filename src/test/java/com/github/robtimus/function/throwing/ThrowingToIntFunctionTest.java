/*
 * ThrowingToIntFunctionTest.java
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
import java.util.function.IntSupplier;
import java.util.function.ToIntFunction;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

@SuppressWarnings("nls")
class ThrowingToIntFunctionTest {

    @Nested
    class OnErrorThrowAsChecked {

        @Test
        void testNullArgument() {
            ThrowingToIntFunction<String, IOException> function = Spied.throwingToIntFunction(String::length);

            assertThrows(NullPointerException.class, () -> function.onErrorThrowAsChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingToIntFunction<String, IOException> function = Spied.throwingToIntFunction(String::length);

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingToIntFunction<String, ExecutionException> throwing = function.onErrorThrowAsChecked(errorMapper);

            assertEquals(3, throwing.applyAsInt("foo"));

            verify(function).applyAsInt("foo");
            verify(function).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingToIntFunction<String, IOException> function = Spied.throwingToIntFunction(s -> {
                throw new IOException(s);
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingToIntFunction<String, ExecutionException> throwing = function.onErrorThrowAsChecked(errorMapper);

            ExecutionException thrown = assertThrows(ExecutionException.class, () -> throwing.applyAsInt("foo"));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo", cause.getMessage());

            verify(function).applyAsInt("foo");
            verify(function).onErrorThrowAsChecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(function, errorMapper);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingToIntFunction<String, IOException> function = Spied.throwingToIntFunction(throwable::throwUnchecked);

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingToIntFunction<String, ExecutionException> throwing = function.onErrorThrowAsChecked(errorMapper);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> throwing.applyAsInt("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(function).applyAsInt("foo");
            verify(function).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }
    }

    @Nested
    class OnErrorThrowAsUnchecked {

        @Test
        void testNullArgument() {
            ThrowingToIntFunction<String, IOException> function = Spied.throwingToIntFunction(String::length);

            assertThrows(NullPointerException.class, () -> function.onErrorThrowAsUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingToIntFunction<String, IOException> function = Spied.throwingToIntFunction(String::length);

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            ToIntFunction<String> throwing = function.onErrorThrowAsUnchecked(errorMapper);

            assertEquals(3, throwing.applyAsInt("foo"));

            verify(function).applyAsInt("foo");
            verify(function).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingToIntFunction<String, IOException> function = Spied.throwingToIntFunction(s -> {
                throw new IOException(s);
            });

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            ToIntFunction<String> throwing = function.onErrorThrowAsUnchecked(errorMapper);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> throwing.applyAsInt("foo"));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo", cause.getMessage());

            verify(function).applyAsInt("foo");
            verify(function).onErrorThrowAsUnchecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(function, errorMapper);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingToIntFunction<String, IOException> function = Spied.throwingToIntFunction(throwable::throwUnchecked);

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            ToIntFunction<String> throwing = function.onErrorThrowAsUnchecked(errorMapper);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> throwing.applyAsInt("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(function).applyAsInt("foo");
            verify(function).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }
    }

    @Nested
    class OnErrorHandleChecked {

        @Test
        void testNullArgument() {
            ThrowingToIntFunction<String, IOException> function = Spied.throwingToIntFunction(String::length);

            assertThrows(NullPointerException.class, () -> function.onErrorHandleChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingToIntFunction<String, IOException> function = Spied.throwingToIntFunction(String::length);

            ThrowingToIntFunction<IOException, ExecutionException> errorHandler = Spied.throwingToIntFunction(e -> 0);

            ThrowingToIntFunction<String, ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

            assertEquals(3, handling.applyAsInt("foo"));

            verify(function).applyAsInt("foo");
            verify(function).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException, ExecutionException {
                ThrowingToIntFunction<String, IOException> function = Spied.throwingToIntFunction(s -> {
                    throw new IOException(s);
                });

                ThrowingToIntFunction<IOException, ExecutionException> errorHandler = Spied.throwingToIntFunction(e -> 0);

                ThrowingToIntFunction<String, ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

                assertEquals(0, handling.applyAsInt("foo"));

                verify(function).applyAsInt("foo");
                verify(function).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsInt(any());
                verifyNoMoreInteractions(function, errorHandler);
            }

            @Test
            void testHandlerThrowsChecked() throws IOException, ExecutionException {
                ThrowingToIntFunction<String, IOException> function = Spied.throwingToIntFunction(s -> {
                    throw new IOException(s);
                });

                ThrowingToIntFunction<IOException, ExecutionException> errorHandler = Spied.throwingToIntFunction(e -> {
                    throw new ExecutionException(e);
                });

                ThrowingToIntFunction<String, ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

                ExecutionException thrown = assertThrows(ExecutionException.class, () -> handling.applyAsInt("foo"));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());

                verify(function).applyAsInt("foo");
                verify(function).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsInt(any());
                verifyNoMoreInteractions(function, errorHandler);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testHandlerThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ExecutionException {
                ThrowingToIntFunction<String, IOException> function = Spied.throwingToIntFunction(s -> {
                    throw new IOException(s);
                });

                ThrowingToIntFunction<IOException, ExecutionException> errorHandler = Spied.throwingToIntFunction(throwable::throwUnchecked);

                ThrowingToIntFunction<String, ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.applyAsInt("foo"));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());

                verify(function).applyAsInt("foo");
                verify(function).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsInt(any());
                verifyNoMoreInteractions(function, errorHandler);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingToIntFunction<String, IOException> function = Spied.throwingToIntFunction(throwable::throwUnchecked);

            ThrowingToIntFunction<IOException, ExecutionException> errorHandler = Spied.throwingToIntFunction(e -> 0);

            ThrowingToIntFunction<String, ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.applyAsInt("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(function).applyAsInt("foo");
            verify(function).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }
    }

    @Nested
    class OnErrorHandleUnchecked {

        @Test
        void testNullArgument() {
            ThrowingToIntFunction<String, IOException> function = Spied.throwingToIntFunction(String::length);

            assertThrows(NullPointerException.class, () -> function.onErrorHandleUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingToIntFunction<String, IOException> function = Spied.throwingToIntFunction(String::length);

            ToIntFunction<IOException> errorHandler = Spied.toIntFunction(e -> 0);

            ToIntFunction<String> handling = function.onErrorHandleUnchecked(errorHandler);

            assertEquals(3, handling.applyAsInt("foo"));

            verify(function).applyAsInt("foo");
            verify(function).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException {
                ThrowingToIntFunction<String, IOException> function = Spied.throwingToIntFunction(s -> {
                    throw new IOException(s);
                });

                ToIntFunction<IOException> errorHandler = Spied.toIntFunction(e -> 0);

                ToIntFunction<String> handling = function.onErrorHandleUnchecked(errorHandler);

                assertEquals(0, handling.applyAsInt("foo"));

                verify(function).applyAsInt("foo");
                verify(function).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).applyAsInt(any());
                verifyNoMoreInteractions(function, errorHandler);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testHandlerThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingToIntFunction<String, IOException> function = Spied.throwingToIntFunction(s -> {
                    throw new IOException(s);
                });

                ToIntFunction<IOException> errorHandler = Spied.toIntFunction(throwable::throwUnchecked);

                ToIntFunction<String> handling = function.onErrorHandleUnchecked(errorHandler);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.applyAsInt("foo"));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());

                verify(function).applyAsInt("foo");
                verify(function).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).applyAsInt(any());
                verifyNoMoreInteractions(function, errorHandler);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingToIntFunction<String, IOException> function = Spied.throwingToIntFunction(throwable::throwUnchecked);

            ToIntFunction<IOException> errorHandler = Spied.toIntFunction(e -> 0);

            ToIntFunction<String> handling = function.onErrorHandleUnchecked(errorHandler);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.applyAsInt("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(function).applyAsInt("foo");
            verify(function).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }
    }

    @Nested
    class OnErrorApplyChecked {

        @Test
        void testNullArgument() {
            ThrowingToIntFunction<String, IOException> function = Spied.throwingToIntFunction(String::length);

            assertThrows(NullPointerException.class, () -> function.onErrorApplyChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingToIntFunction<String, IOException> function = Spied.throwingToIntFunction(String::length);

            ThrowingToIntFunction<String, ExecutionException> fallback = Spied.throwingToIntFunction(s -> 2 * s.length());

            ThrowingToIntFunction<String, ExecutionException> applying = function.onErrorApplyChecked(fallback);

            assertEquals(3, applying.applyAsInt("foo"));

            verify(function).applyAsInt("foo");
            verify(function).onErrorApplyChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                ThrowingToIntFunction<String, IOException> function = Spied.throwingToIntFunction(s -> {
                    throw new IOException(s);
                });

                ThrowingToIntFunction<String, ParseException> fallback = Spied.throwingToIntFunction(s -> 2 * s.length());

                ThrowingToIntFunction<String, ParseException> applying = function.onErrorApplyChecked(fallback);

                assertEquals(6, applying.applyAsInt("foo"));

                verify(function).applyAsInt("foo");
                verify(function).onErrorApplyChecked(fallback);
                verify(fallback).applyAsInt("foo");
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                ThrowingToIntFunction<String, IOException> function = Spied.throwingToIntFunction(s -> {
                    throw new IOException(s);
                });

                ThrowingToIntFunction<String, ParseException> fallback = Spied.throwingToIntFunction(s -> {
                    throw new ParseException(s, 0);
                });

                ThrowingToIntFunction<String, ParseException> applying = function.onErrorApplyChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> applying.applyAsInt("foo"));
                assertEquals("foo", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(function).applyAsInt("foo");
                verify(function).onErrorApplyChecked(fallback);
                verify(fallback).applyAsInt("foo");
                verifyNoMoreInteractions(function, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ParseException {
                ThrowingToIntFunction<String, IOException> function = Spied.throwingToIntFunction(s -> {
                    throw new IOException(s);
                });

                ThrowingToIntFunction<String, ParseException> fallback = Spied.throwingToIntFunction(throwable::throwUnchecked);

                ThrowingToIntFunction<String, ParseException> applying = function.onErrorApplyChecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.applyAsInt("foo"));
                assertEquals("foo", thrown.getMessage());

                verify(function).applyAsInt("foo");
                verify(function).onErrorApplyChecked(fallback);
                verify(fallback).applyAsInt("foo");
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingToIntFunction<String, IOException> function = Spied.throwingToIntFunction(throwable::throwUnchecked);

            ThrowingToIntFunction<String, ParseException> fallback = Spied.throwingToIntFunction(s -> 2 * s.length());

            ThrowingToIntFunction<String, ParseException> applying = function.onErrorApplyChecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.applyAsInt("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(function).applyAsInt("foo");
            verify(function).onErrorApplyChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorApplyUnchecked {

        @Test
        void testNullArgument() {
            ThrowingToIntFunction<String, IOException> function = Spied.throwingToIntFunction(String::length);

            assertThrows(NullPointerException.class, () -> function.onErrorApplyUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingToIntFunction<String, IOException> function = Spied.throwingToIntFunction(String::length);

            ToIntFunction<String> fallback = Spied.toIntFunction(s -> 2 * s.length());

            ToIntFunction<String> applying = function.onErrorApplyUnchecked(fallback);

            assertEquals(3, applying.applyAsInt("foo"));

            verify(function).applyAsInt("foo");
            verify(function).onErrorApplyUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                ThrowingToIntFunction<String, IOException> function = Spied.throwingToIntFunction(s -> {
                    throw new IOException(s);
                });

                ToIntFunction<String> fallback = Spied.toIntFunction(s -> 2 * s.length());

                ToIntFunction<String> applying = function.onErrorApplyUnchecked(fallback);

                assertEquals(6, applying.applyAsInt("foo"));

                verify(function).applyAsInt("foo");
                verify(function).onErrorApplyUnchecked(fallback);
                verify(fallback).applyAsInt("foo");
                verifyNoMoreInteractions(function, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingToIntFunction<String, IOException> function = Spied.throwingToIntFunction(s -> {
                    throw new IOException(s);
                });

                ToIntFunction<String> fallback = Spied.toIntFunction(throwable::throwUnchecked);

                ToIntFunction<String> applying = function.onErrorApplyUnchecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.applyAsInt("foo"));
                assertEquals("foo", thrown.getMessage());

                verify(function).applyAsInt("foo");
                verify(function).onErrorApplyUnchecked(fallback);
                verify(fallback).applyAsInt("foo");
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingToIntFunction<String, IOException> function = Spied.throwingToIntFunction(throwable::throwUnchecked);

            ToIntFunction<String> fallback = Spied.toIntFunction(s -> 2 * s.length());

            ToIntFunction<String> applying = function.onErrorApplyUnchecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.applyAsInt("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(function).applyAsInt("foo");
            verify(function).onErrorApplyUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorGetChecked {

        @Test
        void testNullArgument() {
            ThrowingToIntFunction<String, IOException> function = Spied.throwingToIntFunction(String::length);

            assertThrows(NullPointerException.class, () -> function.onErrorGetChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingToIntFunction<String, IOException> function = Spied.throwingToIntFunction(String::length);

            ThrowingIntSupplier<ExecutionException> fallback = Spied.throwingIntSupplier(() -> 0);

            ThrowingToIntFunction<String, ExecutionException> getting = function.onErrorGetChecked(fallback);

            assertEquals(3, getting.applyAsInt("foo"));

            verify(function).applyAsInt("foo");
            verify(function).onErrorGetChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                ThrowingToIntFunction<String, IOException> function = Spied.throwingToIntFunction(s -> {
                    throw new IOException(s);
                });

                ThrowingIntSupplier<ParseException> fallback = Spied.throwingIntSupplier(() -> 0);

                ThrowingToIntFunction<String, ParseException> getting = function.onErrorGetChecked(fallback);

                assertEquals(0, getting.applyAsInt("foo"));

                verify(function).applyAsInt("foo");
                verify(function).onErrorGetChecked(fallback);
                verify(fallback).getAsInt();
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                ThrowingToIntFunction<String, IOException> function = Spied.throwingToIntFunction(s -> {
                    throw new IOException(s);
                });

                ThrowingIntSupplier<ParseException> fallback = Spied.throwingIntSupplier(() -> {
                    throw new ParseException("bar", 0);
                });

                ThrowingToIntFunction<String, ParseException> getting = function.onErrorGetChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> getting.applyAsInt("foo"));
                assertEquals("bar", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(function).applyAsInt("foo");
                verify(function).onErrorGetChecked(fallback);
                verify(fallback).getAsInt();
                verifyNoMoreInteractions(function, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ParseException {
                ThrowingToIntFunction<String, IOException> function = Spied.throwingToIntFunction(s -> {
                    throw new IOException(s);
                });

                ThrowingIntSupplier<ParseException> fallback = Spied.throwingIntSupplier(() -> throwable.throwUnchecked("bar"));

                ThrowingToIntFunction<String, ParseException> getting = function.onErrorGetChecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.applyAsInt("foo"));
                assertEquals("bar", thrown.getMessage());

                verify(function).applyAsInt("foo");
                verify(function).onErrorGetChecked(fallback);
                verify(fallback).getAsInt();
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingToIntFunction<String, IOException> function = Spied.throwingToIntFunction(throwable::throwUnchecked);

            ThrowingIntSupplier<ParseException> fallback = Spied.throwingIntSupplier(() -> 0);

            ThrowingToIntFunction<String, ParseException> getting = function.onErrorGetChecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.applyAsInt("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(function).applyAsInt("foo");
            verify(function).onErrorGetChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorGetUnchecked {

        @Test
        void testNullArgument() {
            ThrowingToIntFunction<String, IOException> function = Spied.throwingToIntFunction(String::length);

            assertThrows(NullPointerException.class, () -> function.onErrorGetUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingToIntFunction<String, IOException> function = Spied.throwingToIntFunction(String::length);

            IntSupplier fallback = Spied.intSupplier(() -> 0);

            ToIntFunction<String> getting = function.onErrorGetUnchecked(fallback);

            assertEquals(3, getting.applyAsInt("foo"));

            verify(function).applyAsInt("foo");
            verify(function).onErrorGetUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                ThrowingToIntFunction<String, IOException> function = Spied.throwingToIntFunction(s -> {
                    throw new IOException(s);
                });

                IntSupplier fallback = Spied.intSupplier(() -> 0);

                ToIntFunction<String> getting = function.onErrorGetUnchecked(fallback);

                assertEquals(0, getting.applyAsInt("foo"));

                verify(function).applyAsInt("foo");
                verify(function).onErrorGetUnchecked(fallback);
                verify(fallback).getAsInt();
                verifyNoMoreInteractions(function, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingToIntFunction<String, IOException> function = Spied.throwingToIntFunction(s -> {
                    throw new IOException(s);
                });

                IntSupplier fallback = Spied.intSupplier(() -> throwable.throwUnchecked("bar"));

                ToIntFunction<String> getting = function.onErrorGetUnchecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.applyAsInt("foo"));
                assertEquals("bar", thrown.getMessage());

                verify(function).applyAsInt("foo");
                verify(function).onErrorGetUnchecked(fallback);
                verify(fallback).getAsInt();
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingToIntFunction<String, IOException> function = Spied.throwingToIntFunction(throwable::throwUnchecked);

            IntSupplier fallback = Spied.intSupplier(() -> 0);

            ToIntFunction<String> getting = function.onErrorGetUnchecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.applyAsInt("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(function).applyAsInt("foo");
            verify(function).onErrorGetUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorReturn {

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingToIntFunction<String, IOException> function = Spied.throwingToIntFunction(String::length);

            ToIntFunction<String> returning = function.onErrorReturn(0);

            assertEquals(3, returning.applyAsInt("foo"));

            verify(function).applyAsInt("foo");
            verify(function).onErrorReturn(0);
            verifyNoMoreInteractions(function);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingToIntFunction<String, IOException> function = Spied.throwingToIntFunction(s -> {
                throw new IOException(s);
            });

            ToIntFunction<String> returning = function.onErrorReturn(0);

            assertEquals(0, returning.applyAsInt("foo"));

            verify(function).applyAsInt("foo");
            verify(function).onErrorReturn(0);
            verifyNoMoreInteractions(function);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingToIntFunction<String, IOException> function = Spied.throwingToIntFunction(throwable::throwUnchecked);

            ToIntFunction<String> returning = function.onErrorReturn(0);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> returning.applyAsInt("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(function).applyAsInt("foo");
            verify(function).onErrorReturn(0);
            verifyNoMoreInteractions(function);
        }
    }

    @Nested
    class Unchecked {

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingToIntFunction<String, IOException> function = Spied.throwingToIntFunction(String::length);

            ToIntFunction<String> unchecked = function.unchecked();

            assertEquals(3, unchecked.applyAsInt("foo"));

            verify(function).applyAsInt("foo");
            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(function);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingToIntFunction<String, IOException> function = Spied.throwingToIntFunction(s -> {
                throw new IOException(s);
            });

            ToIntFunction<String> unchecked = function.unchecked();

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.applyAsInt("foo"));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo", cause.getMessage());

            verify(function).applyAsInt("foo");
            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(function);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingToIntFunction<String, IOException> function = Spied.throwingToIntFunction(throwable::throwUnchecked);

            ToIntFunction<String> unchecked = function.unchecked();

            Throwable thrown = assertThrows(throwable.throwableType(), () -> unchecked.applyAsInt("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(function).applyAsInt("foo");
            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(function);
        }
    }

    @Nested
    class Of {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> ThrowingToIntFunction.of(null));
        }

        @Test
        void testNonNullArgument() throws IOException {
            ThrowingToIntFunction<String, IOException> function = ThrowingToIntFunction.of(String::length);

            assertEquals(3, function.applyAsInt("foo"));
        }
    }

    @Nested
    class UncheckedFactory {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> ThrowingToIntFunction.unchecked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ThrowingToIntFunction<String, IOException> function = Spied.throwingToIntFunction(String::length);

            ToIntFunction<String> unchecked = ThrowingToIntFunction.unchecked(function);

            assertEquals(3, unchecked.applyAsInt("foo"));

            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verify(function).applyAsInt("foo");
            verifyNoMoreInteractions(function);
        }

        @Test
        void testArgumentThrowsChecked() throws IOException {
            ThrowingToIntFunction<String, IOException> function = Spied.throwingToIntFunction(s -> {
                throw new IOException(s);
            });

            ToIntFunction<String> unchecked = ThrowingToIntFunction.unchecked(function);

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.applyAsInt("foo"));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo", cause.getMessage());

            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verify(function).applyAsInt("foo");
            verifyNoMoreInteractions(function);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testArgumentThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingToIntFunction<String, IOException> function = Spied.throwingToIntFunction(throwable::throwUnchecked);

            ToIntFunction<String> unchecked = ThrowingToIntFunction.unchecked(function);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> unchecked.applyAsInt("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verify(function).applyAsInt("foo");
            verifyNoMoreInteractions(function);
        }
    }

    @Nested
    class CheckedWrapper {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> ThrowingToIntFunction.checked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ThrowingToIntFunction<String, IOException> function = ThrowingToIntFunction.checked(String::length);

            assertEquals(3, function.applyAsInt("foo"));
        }

        @Test
        void testArgumentThrowsUnchecked() {
            ThrowingToIntFunction<String, IOException> function = ThrowingToIntFunction.checked(s -> {
                throw UncheckedException.withoutStackTrace(s, new IOException());
            });

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> function.applyAsInt("foo"));
            assertEquals("foo", thrown.getMessage());
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertNull(cause.getMessage());
        }
    }

    @Nested
    class CheckedUnwrapper {

        @Test
        void testNullArguments() {
            ToIntFunction<String> function = String::length;

            assertThrows(NullPointerException.class, () -> ThrowingToIntFunction.checked(null, IOException.class));
            assertThrows(NullPointerException.class, () -> ThrowingToIntFunction.checked(function, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ThrowingToIntFunction<String, IOException> function = ThrowingToIntFunction.checked(String::length, IOException.class);

            assertEquals(3, function.applyAsInt("foo"));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                ThrowingToIntFunction<String, IOException> function = ThrowingToIntFunction.checked(s -> {
                    throw UncheckedException.withoutStackTrace(new IOException(s));
                }, IOException.class);

                IOException thrown = assertThrows(IOException.class, () -> function.applyAsInt("foo"));
                assertEquals("foo", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                ThrowingToIntFunction<String, IOException> function = ThrowingToIntFunction.checked(s -> {
                    throw UncheckedException.withoutStackTrace(new FileNotFoundException(s));
                }, IOException.class);

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class, () -> function.applyAsInt("foo"));
                assertEquals("foo", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                ThrowingToIntFunction<String, IOException> function = ThrowingToIntFunction.checked(s -> {
                    throw UncheckedException.withoutStackTrace(new ParseException(s, 0));
                }, IOException.class);

                UncheckedException thrown = assertThrows(UncheckedException.class, () -> function.applyAsInt("foo"));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            ThrowingToIntFunction<String, IOException> function = ThrowingToIntFunction.checked(s -> {
                throw new IllegalStateException(s);
            }, IOException.class);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> function.applyAsInt("foo"));
            assertEquals("foo", thrown.getMessage());
        }
    }
}
