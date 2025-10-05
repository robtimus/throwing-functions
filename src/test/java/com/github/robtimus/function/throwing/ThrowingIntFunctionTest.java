/*
 * ThrowingIntFunctionTest.java
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
import java.util.function.IntFunction;
import java.util.function.Supplier;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

@SuppressWarnings("nls")
class ThrowingIntFunctionTest {

    @Nested
    class OnErrorThrowAsChecked {

        @Test
        void testNullArgument() {
            ThrowingIntFunction<String, IOException> function = Spied.throwingIntFunction(Integer::toString);

            assertThrows(NullPointerException.class, () -> function.onErrorThrowAsChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingIntFunction<String, IOException> function = Spied.throwingIntFunction(Integer::toString);

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingIntFunction<String, ExecutionException> throwing = function.onErrorThrowAsChecked(errorMapper);

            assertEquals("1", throwing.apply(1));

            verify(function).apply(1);
            verify(function).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingIntFunction<String, IOException> function = Spied.throwingIntFunction(i -> {
                throw new IOException(Integer.toString(i));
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingIntFunction<String, ExecutionException> throwing = function.onErrorThrowAsChecked(errorMapper);

            ExecutionException thrown = assertThrows(ExecutionException.class, () -> throwing.apply(1));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1", cause.getMessage());

            verify(function).apply(1);
            verify(function).onErrorThrowAsChecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(function, errorMapper);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingIntFunction<String, IOException> function = Spied.throwingIntFunction(throwable::throwUnchecked);

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingIntFunction<String, ExecutionException> throwing = function.onErrorThrowAsChecked(errorMapper);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> throwing.apply(1));
            assertEquals("1", thrown.getMessage());

            verify(function).apply(1);
            verify(function).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }
    }

    @Nested
    class OnErrorThrowAsUnchecked {

        @Test
        void testNullArgument() {
            ThrowingIntFunction<String, IOException> function = Spied.throwingIntFunction(Integer::toString);

            assertThrows(NullPointerException.class, () -> function.onErrorThrowAsUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingIntFunction<String, IOException> function = Spied.throwingIntFunction(Integer::toString);

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            IntFunction<String> throwing = function.onErrorThrowAsUnchecked(errorMapper);

            assertEquals("1", throwing.apply(1));

            verify(function).apply(1);
            verify(function).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingIntFunction<String, IOException> function = Spied.throwingIntFunction(i -> {
                throw new IOException(Integer.toString(i));
            });

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            IntFunction<String> throwing = function.onErrorThrowAsUnchecked(errorMapper);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> throwing.apply(1));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1", cause.getMessage());

            verify(function).apply(1);
            verify(function).onErrorThrowAsUnchecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(function, errorMapper);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingIntFunction<String, IOException> function = Spied.throwingIntFunction(throwable::throwUnchecked);

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            IntFunction<String> throwing = function.onErrorThrowAsUnchecked(errorMapper);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> throwing.apply(1));
            assertEquals("1", thrown.getMessage());

            verify(function).apply(1);
            verify(function).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }
    }

    @Nested
    class OnErrorHandleChecked {

        @Test
        void testNullArgument() {
            ThrowingIntFunction<String, IOException> function = Spied.throwingIntFunction(Integer::toString);

            assertThrows(NullPointerException.class, () -> function.onErrorHandleChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingIntFunction<String, IOException> function = Spied.throwingIntFunction(Integer::toString);

            ThrowingFunction<IOException, String, ExecutionException> errorHandler = Spied.throwingFunction(Exception::getMessage);

            ThrowingIntFunction<String, ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

            assertEquals("1", handling.apply(1));

            verify(function).apply(1);
            verify(function).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException, ExecutionException {
                ThrowingIntFunction<String, IOException> function = Spied.throwingIntFunction(i -> {
                    throw new IOException(Integer.toString(i));
                });

                ThrowingFunction<IOException, String, ExecutionException> errorHandler = Spied.throwingFunction(Exception::getMessage);

                ThrowingIntFunction<String, ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

                assertEquals("1", handling.apply(1));

                verify(function).apply(1);
                verify(function).onErrorHandleChecked(errorHandler);
                verify(errorHandler).apply(any());
                verifyNoMoreInteractions(function, errorHandler);
            }

            @Test
            void testHandlerThrowsChecked() throws IOException, ExecutionException {
                ThrowingIntFunction<String, IOException> function = Spied.throwingIntFunction(i -> {
                    throw new IOException(Integer.toString(i));
                });

                ThrowingFunction<IOException, String, ExecutionException> errorHandler = Spied.throwingFunction(e -> {
                    throw new ExecutionException(e);
                });

                ThrowingIntFunction<String, ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

                ExecutionException thrown = assertThrows(ExecutionException.class, () -> handling.apply(1));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("1", cause.getMessage());

                verify(function).apply(1);
                verify(function).onErrorHandleChecked(errorHandler);
                verify(errorHandler).apply(any());
                verifyNoMoreInteractions(function, errorHandler);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testHandlerThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ExecutionException {
                ThrowingIntFunction<String, IOException> function = Spied.throwingIntFunction(i -> {
                    throw new IOException(Integer.toString(i));
                });

                ThrowingFunction<IOException, String, ExecutionException> errorHandler = Spied.throwingFunction(throwable::throwUnchecked);

                ThrowingIntFunction<String, ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.apply(1));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("1", cause.getMessage());

                verify(function).apply(1);
                verify(function).onErrorHandleChecked(errorHandler);
                verify(errorHandler).apply(any());
                verifyNoMoreInteractions(function, errorHandler);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingIntFunction<String, IOException> function = Spied.throwingIntFunction(throwable::throwUnchecked);

            ThrowingFunction<IOException, String, ExecutionException> errorHandler = Spied.throwingFunction(Exception::getMessage);

            ThrowingIntFunction<String, ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.apply(1));
            assertEquals("1", thrown.getMessage());

            verify(function).apply(1);
            verify(function).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }
    }

    @Nested
    class OnErrorHandleUnchecked {

        @Test
        void testNullArgument() {
            ThrowingIntFunction<String, IOException> function = Spied.throwingIntFunction(Integer::toString);

            assertThrows(NullPointerException.class, () -> function.onErrorHandleUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingIntFunction<String, IOException> function = Spied.throwingIntFunction(Integer::toString);

            Function<IOException, String> errorHandler = Spied.function(Exception::getMessage);

            IntFunction<String> handling = function.onErrorHandleUnchecked(errorHandler);

            assertEquals("1", handling.apply(1));

            verify(function).apply(1);
            verify(function).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException {
                ThrowingIntFunction<String, IOException> function = Spied.throwingIntFunction(i -> {
                    throw new IOException(Integer.toString(i));
                });

                Function<IOException, String> errorHandler = Spied.function(Exception::getMessage);

                IntFunction<String> handling = function.onErrorHandleUnchecked(errorHandler);

                assertEquals("1", handling.apply(1));

                verify(function).apply(1);
                verify(function).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).apply(any());
                verifyNoMoreInteractions(function, errorHandler);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testHandlerThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingIntFunction<String, IOException> function = Spied.throwingIntFunction(i -> {
                    throw new IOException(Integer.toString(i));
                });

                Function<IOException, String> errorHandler = Spied.function(throwable::throwUnchecked);

                IntFunction<String> handling = function.onErrorHandleUnchecked(errorHandler);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.apply(1));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("1", cause.getMessage());

                verify(function).apply(1);
                verify(function).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).apply(any());
                verifyNoMoreInteractions(function, errorHandler);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingIntFunction<String, IOException> function = Spied.throwingIntFunction(throwable::throwUnchecked);

            Function<IOException, String> errorHandler = Spied.function(Exception::getMessage);

            IntFunction<String> handling = function.onErrorHandleUnchecked(errorHandler);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.apply(1));
            assertEquals("1", thrown.getMessage());

            verify(function).apply(1);
            verify(function).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }
    }

    @Nested
    class OnErrorApplyChecked {

        @Test
        void testNullArgument() {
            ThrowingIntFunction<String, IOException> function = Spied.throwingIntFunction(Integer::toString);

            assertThrows(NullPointerException.class, () -> function.onErrorApplyChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingIntFunction<String, IOException> function = Spied.throwingIntFunction(Integer::toString);

            ThrowingIntFunction<String, ExecutionException> fallback = Spied.throwingIntFunction(i -> Integer.toString(i + i));

            ThrowingIntFunction<String, ExecutionException> applying = function.onErrorApplyChecked(fallback);

            assertEquals("1", applying.apply(1));

            verify(function).apply(1);
            verify(function).onErrorApplyChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                ThrowingIntFunction<String, IOException> function = Spied.throwingIntFunction(i -> {
                    throw new IOException(Integer.toString(i));
                });

                ThrowingIntFunction<String, ParseException> fallback = Spied.throwingIntFunction(i -> Integer.toString(i + i));

                ThrowingIntFunction<String, ParseException> applying = function.onErrorApplyChecked(fallback);

                assertEquals("2", applying.apply(1));

                verify(function).apply(1);
                verify(function).onErrorApplyChecked(fallback);
                verify(fallback).apply(1);
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                ThrowingIntFunction<String, IOException> function = Spied.throwingIntFunction(i -> {
                    throw new IOException(Integer.toString(i));
                });

                ThrowingIntFunction<String, ParseException> fallback = Spied.throwingIntFunction(i -> {
                    throw new ParseException(Integer.toString(i), 0);
                });

                ThrowingIntFunction<String, ParseException> applying = function.onErrorApplyChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> applying.apply(1));
                assertEquals("1", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(function).apply(1);
                verify(function).onErrorApplyChecked(fallback);
                verify(fallback).apply(1);
                verifyNoMoreInteractions(function, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ParseException {
                ThrowingIntFunction<String, IOException> function = Spied.throwingIntFunction(i -> {
                    throw new IOException(Integer.toString(i));
                });

                ThrowingIntFunction<String, ParseException> fallback = Spied.throwingIntFunction(throwable::throwUnchecked);

                ThrowingIntFunction<String, ParseException> applying = function.onErrorApplyChecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.apply(1));
                assertEquals("1", thrown.getMessage());

                verify(function).apply(1);
                verify(function).onErrorApplyChecked(fallback);
                verify(fallback).apply(1);
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingIntFunction<String, IOException> function = Spied.throwingIntFunction(throwable::throwUnchecked);

            ThrowingIntFunction<String, ParseException> fallback = Spied.throwingIntFunction(i -> Integer.toString(i + i));

            ThrowingIntFunction<String, ParseException> applying = function.onErrorApplyChecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.apply(1));
            assertEquals("1", thrown.getMessage());

            verify(function).apply(1);
            verify(function).onErrorApplyChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorApplyUnchecked {

        @Test
        void testNullArgument() {
            ThrowingIntFunction<String, IOException> function = Spied.throwingIntFunction(Integer::toString);

            assertThrows(NullPointerException.class, () -> function.onErrorApplyUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingIntFunction<String, IOException> function = Spied.throwingIntFunction(Integer::toString);

            IntFunction<String> fallback = Spied.intFunction(i -> Integer.toString(i + i));

            IntFunction<String> applying = function.onErrorApplyUnchecked(fallback);

            assertEquals("1", applying.apply(1));

            verify(function).apply(1);
            verify(function).onErrorApplyUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                ThrowingIntFunction<String, IOException> function = Spied.throwingIntFunction(i -> {
                    throw new IOException(Integer.toString(i));
                });

                IntFunction<String> fallback = Spied.intFunction(i -> Integer.toString(i + i));

                IntFunction<String> applying = function.onErrorApplyUnchecked(fallback);

                assertEquals("2", applying.apply(1));

                verify(function).apply(1);
                verify(function).onErrorApplyUnchecked(fallback);
                verify(fallback).apply(1);
                verifyNoMoreInteractions(function, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingIntFunction<String, IOException> function = Spied.throwingIntFunction(i -> {
                    throw new IOException(Integer.toString(i));
                });

                IntFunction<String> fallback = Spied.intFunction(throwable::throwUnchecked);

                IntFunction<String> applying = function.onErrorApplyUnchecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.apply(1));
                assertEquals("1", thrown.getMessage());

                verify(function).apply(1);
                verify(function).onErrorApplyUnchecked(fallback);
                verify(fallback).apply(1);
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingIntFunction<String, IOException> function = Spied.throwingIntFunction(throwable::throwUnchecked);

            IntFunction<String> fallback = Spied.intFunction(i -> Integer.toString(i + i));

            IntFunction<String> applying = function.onErrorApplyUnchecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.apply(1));
            assertEquals("1", thrown.getMessage());

            verify(function).apply(1);
            verify(function).onErrorApplyUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorGetChecked {

        @Test
        void testNullArgument() {
            ThrowingIntFunction<String, IOException> function = Spied.throwingIntFunction(Integer::toString);

            assertThrows(NullPointerException.class, () -> function.onErrorGetChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingIntFunction<String, IOException> function = Spied.throwingIntFunction(Integer::toString);

            ThrowingSupplier<String, ExecutionException> fallback = Spied.throwingSupplier(() -> "bar");

            ThrowingIntFunction<String, ExecutionException> getting = function.onErrorGetChecked(fallback);

            assertEquals("1", getting.apply(1));

            verify(function).apply(1);
            verify(function).onErrorGetChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                ThrowingIntFunction<String, IOException> function = Spied.throwingIntFunction(i -> {
                    throw new IOException(Integer.toString(i));
                });

                ThrowingSupplier<String, ParseException> fallback = Spied.throwingSupplier(() -> "bar");

                ThrowingIntFunction<String, ParseException> getting = function.onErrorGetChecked(fallback);

                assertEquals("bar", getting.apply(1));

                verify(function).apply(1);
                verify(function).onErrorGetChecked(fallback);
                verify(fallback).get();
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                ThrowingIntFunction<String, IOException> function = Spied.throwingIntFunction(i -> {
                    throw new IOException(Integer.toString(i));
                });

                ThrowingSupplier<String, ParseException> fallback = Spied.throwingSupplier(() -> {
                    throw new ParseException("bar", 0);
                });

                ThrowingIntFunction<String, ParseException> getting = function.onErrorGetChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> getting.apply(1));
                assertEquals("bar", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(function).apply(1);
                verify(function).onErrorGetChecked(fallback);
                verify(fallback).get();
                verifyNoMoreInteractions(function, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ParseException {
                ThrowingIntFunction<String, IOException> function = Spied.throwingIntFunction(i -> {
                    throw new IOException(Integer.toString(i));
                });

                ThrowingSupplier<String, ParseException> fallback = Spied.throwingSupplier(() -> throwable.throwUnchecked("bar"));

                ThrowingIntFunction<String, ParseException> getting = function.onErrorGetChecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.apply(1));
                assertEquals("bar", thrown.getMessage());

                verify(function).apply(1);
                verify(function).onErrorGetChecked(fallback);
                verify(fallback).get();
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingIntFunction<String, IOException> function = Spied.throwingIntFunction(throwable::throwUnchecked);

            ThrowingSupplier<String, ParseException> fallback = Spied.throwingSupplier(() -> "bar");

            ThrowingIntFunction<String, ParseException> getting = function.onErrorGetChecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.apply(1));
            assertEquals("1", thrown.getMessage());

            verify(function).apply(1);
            verify(function).onErrorGetChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorGetUnchecked {

        @Test
        void testNullArgument() {
            ThrowingIntFunction<String, IOException> function = Spied.throwingIntFunction(Integer::toString);

            assertThrows(NullPointerException.class, () -> function.onErrorGetUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingIntFunction<String, IOException> function = Spied.throwingIntFunction(Integer::toString);

            Supplier<String> fallback = Spied.supplier(() -> "bar");

            IntFunction<String> getting = function.onErrorGetUnchecked(fallback);

            assertEquals("1", getting.apply(1));

            verify(function).apply(1);
            verify(function).onErrorGetUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                ThrowingIntFunction<String, IOException> function = Spied.throwingIntFunction(i -> {
                    throw new IOException(Integer.toString(i));
                });

                Supplier<String> fallback = Spied.supplier(() -> "bar");

                IntFunction<String> getting = function.onErrorGetUnchecked(fallback);

                assertEquals("bar", getting.apply(1));

                verify(function).apply(1);
                verify(function).onErrorGetUnchecked(fallback);
                verify(fallback).get();
                verifyNoMoreInteractions(function, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingIntFunction<String, IOException> function = Spied.throwingIntFunction(i -> {
                    throw new IOException(Integer.toString(i));
                });

                Supplier<String> fallback = Spied.supplier(() -> throwable.throwUnchecked("bar"));

                IntFunction<String> getting = function.onErrorGetUnchecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.apply(1));
                assertEquals("bar", thrown.getMessage());

                verify(function).apply(1);
                verify(function).onErrorGetUnchecked(fallback);
                verify(fallback).get();
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingIntFunction<String, IOException> function = Spied.throwingIntFunction(throwable::throwUnchecked);

            Supplier<String> fallback = Spied.supplier(() -> "bar");

            IntFunction<String> getting = function.onErrorGetUnchecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.apply(1));
            assertEquals("1", thrown.getMessage());

            verify(function).apply(1);
            verify(function).onErrorGetUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorReturn {

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingIntFunction<String, IOException> function = Spied.throwingIntFunction(Integer::toString);

            IntFunction<String> returning = function.onErrorReturn("bar");

            assertEquals("1", returning.apply(1));

            verify(function).apply(1);
            verify(function).onErrorReturn("bar");
            verifyNoMoreInteractions(function);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingIntFunction<String, IOException> function = Spied.throwingIntFunction(i -> {
                throw new IOException(Integer.toString(i));
            });

            IntFunction<String> returning = function.onErrorReturn("bar");

            assertEquals("bar", returning.apply(1));

            verify(function).apply(1);
            verify(function).onErrorReturn("bar");
            verifyNoMoreInteractions(function);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingIntFunction<String, IOException> function = Spied.throwingIntFunction(throwable::throwUnchecked);

            IntFunction<String> returning = function.onErrorReturn("bar");

            Throwable thrown = assertThrows(throwable.throwableType(), () -> returning.apply(1));
            assertEquals("1", thrown.getMessage());

            verify(function).apply(1);
            verify(function).onErrorReturn("bar");
            verifyNoMoreInteractions(function);
        }
    }

    @Nested
    class Unchecked {

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingIntFunction<String, IOException> function = Spied.throwingIntFunction(Integer::toString);

            IntFunction<String> unchecked = function.unchecked();

            assertEquals("1", unchecked.apply(1));

            verify(function).apply(1);
            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(function);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingIntFunction<String, IOException> function = Spied.throwingIntFunction(i -> {
                throw new IOException(Integer.toString(i));
            });

            IntFunction<String> unchecked = function.unchecked();

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.apply(1));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1", cause.getMessage());

            verify(function).apply(1);
            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(function);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingIntFunction<String, IOException> function = Spied.throwingIntFunction(throwable::throwUnchecked);

            IntFunction<String> unchecked = function.unchecked();

            Throwable thrown = assertThrows(throwable.throwableType(), () -> unchecked.apply(1));
            assertEquals("1", thrown.getMessage());

            verify(function).apply(1);
            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(function);
        }
    }

    @Nested
    class Of {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> ThrowingIntFunction.of(null));
        }

        @Test
        void testNonNullArgument() throws IOException {
            ThrowingIntFunction<String, IOException> function = ThrowingIntFunction.of(Integer::toString);

            assertEquals("1", function.apply(1));
        }
    }

    @Nested
    class UncheckedFactory {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> ThrowingIntFunction.unchecked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ThrowingIntFunction<String, IOException> function = Spied.throwingIntFunction(Integer::toString);

            IntFunction<String> unchecked = ThrowingIntFunction.unchecked(function);

            assertEquals("1", unchecked.apply(1));

            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verify(function).apply(1);
            verifyNoMoreInteractions(function);
        }

        @Test
        void testArgumentThrowsChecked() throws IOException {
            ThrowingIntFunction<String, IOException> function = Spied.throwingIntFunction(i -> {
                throw new IOException(Integer.toString(i));
            });

            IntFunction<String> unchecked = ThrowingIntFunction.unchecked(function);

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.apply(1));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1", cause.getMessage());

            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verify(function).apply(1);
            verifyNoMoreInteractions(function);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testArgumentThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingIntFunction<String, IOException> function = Spied.throwingIntFunction(throwable::throwUnchecked);

            IntFunction<String> unchecked = ThrowingIntFunction.unchecked(function);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> unchecked.apply(1));
            assertEquals("1", thrown.getMessage());

            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verify(function).apply(1);
            verifyNoMoreInteractions(function);
        }
    }

    @Nested
    class CheckedWrapper {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> ThrowingIntFunction.checked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ThrowingIntFunction<String, IOException> function = ThrowingIntFunction.checked(Integer::toString);

            assertEquals("1", function.apply(1));
        }

        @Test
        void testArgumentThrowsUnchecked() {
            ThrowingIntFunction<String, IOException> function = ThrowingIntFunction.checked(i -> {
                throw UncheckedException.withoutStackTrace(Integer.toString(i), new IOException());
            });

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> function.apply(1));
            assertEquals("1", thrown.getMessage());
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertNull(cause.getMessage());
        }
    }

    @Nested
    class CheckedUnwrapper {

        @Test
        void testNullArguments() {
            IntFunction<String> function = Integer::toString;

            assertThrows(NullPointerException.class, () -> ThrowingIntFunction.checked(null, IOException.class));
            assertThrows(NullPointerException.class, () -> ThrowingIntFunction.checked(function, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ThrowingIntFunction<String, IOException> function = ThrowingIntFunction.checked(Integer::toString, IOException.class);

            assertEquals("1", function.apply(1));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                ThrowingIntFunction<String, IOException> function = ThrowingIntFunction.checked(i -> {
                    throw UncheckedException.withoutStackTrace(new IOException(Integer.toString(i)));
                }, IOException.class);

                IOException thrown = assertThrows(IOException.class, () -> function.apply(1));
                assertEquals("1", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                ThrowingIntFunction<String, IOException> function = ThrowingIntFunction.checked(i -> {
                    throw UncheckedException.withoutStackTrace(new FileNotFoundException(Integer.toString(i)));
                }, IOException.class);

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class, () -> function.apply(1));
                assertEquals("1", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                ThrowingIntFunction<String, IOException> function = ThrowingIntFunction.checked(i -> {
                    throw UncheckedException.withoutStackTrace(new ParseException(Integer.toString(i), 0));
                }, IOException.class);

                UncheckedException thrown = assertThrows(UncheckedException.class, () -> function.apply(1));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("1", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            ThrowingIntFunction<String, IOException> function = ThrowingIntFunction.checked(i -> {
                throw new IllegalStateException(Integer.toString(i));
            }, IOException.class);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> function.apply(1));
            assertEquals("1", thrown.getMessage());
        }
    }

    @Nested
    class InvokeAndUnwrap {

        @Test
        void testNullArguments() {
            IntFunction<String> function = i -> String.valueOf(Integer.toString(i)).toUpperCase();

            assertThrows(NullPointerException.class, () -> ThrowingIntFunction.invokeAndUnwrap(null, 1, IOException.class));
            assertThrows(NullPointerException.class, () -> ThrowingIntFunction.invokeAndUnwrap(function, 1, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            IntFunction<String> function = Integer::toString;

            assertEquals("1", ThrowingIntFunction.invokeAndUnwrap(function, 1, IOException.class));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                IntFunction<String> function = i -> {
                    throw UncheckedException.withoutStackTrace(new IOException(Integer.toString(i)));
                };

                IOException thrown = assertThrows(IOException.class, () -> ThrowingIntFunction.invokeAndUnwrap(function, 1, IOException.class));
                assertEquals("1", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                IntFunction<String> function = i -> {
                    throw UncheckedException.withoutStackTrace(new FileNotFoundException(Integer.toString(i)));
                };

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class,
                        () -> ThrowingIntFunction.invokeAndUnwrap(function, 1, IOException.class));
                assertEquals("1", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                IntFunction<String> function = i -> {
                    throw UncheckedException.withoutStackTrace(new ParseException(Integer.toString(i), 0));
                };

                UncheckedException thrown = assertThrows(UncheckedException.class,
                        () -> ThrowingIntFunction.invokeAndUnwrap(function, 1, IOException.class));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("1", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            IntFunction<String> function = i -> {
                throw new IllegalStateException(Integer.toString(i));
            };

            IllegalStateException thrown = assertThrows(IllegalStateException.class,
                    () -> ThrowingIntFunction.invokeAndUnwrap(function, 1, IOException.class));
            assertEquals("1", thrown.getMessage());
        }
    }
}
