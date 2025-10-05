/*
 * ThrowingLongFunctionTest.java
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
import java.util.function.LongFunction;
import java.util.function.Supplier;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

@SuppressWarnings("nls")
class ThrowingLongFunctionTest {

    @Nested
    class OnErrorThrowAsChecked {

        @Test
        void testNullArgument() {
            ThrowingLongFunction<String, IOException> function = Spied.throwingLongFunction(Long::toString);

            assertThrows(NullPointerException.class, () -> function.onErrorThrowAsChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingLongFunction<String, IOException> function = Spied.throwingLongFunction(Long::toString);

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingLongFunction<String, ExecutionException> throwing = function.onErrorThrowAsChecked(errorMapper);

            assertEquals("1", throwing.apply(1L));

            verify(function).apply(1L);
            verify(function).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingLongFunction<String, IOException> function = Spied.throwingLongFunction(l -> {
                throw new IOException(Long.toString(l));
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingLongFunction<String, ExecutionException> throwing = function.onErrorThrowAsChecked(errorMapper);

            ExecutionException thrown = assertThrows(ExecutionException.class, () -> throwing.apply(1L));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1", cause.getMessage());

            verify(function).apply(1L);
            verify(function).onErrorThrowAsChecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(function, errorMapper);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingLongFunction<String, IOException> function = Spied.throwingLongFunction(throwable::throwUnchecked);

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingLongFunction<String, ExecutionException> throwing = function.onErrorThrowAsChecked(errorMapper);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> throwing.apply(1L));
            assertEquals("1", thrown.getMessage());

            verify(function).apply(1L);
            verify(function).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }
    }

    @Nested
    class OnErrorThrowAsUnchecked {

        @Test
        void testNullArgument() {
            ThrowingLongFunction<String, IOException> function = Spied.throwingLongFunction(Long::toString);

            assertThrows(NullPointerException.class, () -> function.onErrorThrowAsUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingLongFunction<String, IOException> function = Spied.throwingLongFunction(Long::toString);

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            LongFunction<String> throwing = function.onErrorThrowAsUnchecked(errorMapper);

            assertEquals("1", throwing.apply(1L));

            verify(function).apply(1L);
            verify(function).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingLongFunction<String, IOException> function = Spied.throwingLongFunction(l -> {
                throw new IOException(Long.toString(l));
            });

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            LongFunction<String> throwing = function.onErrorThrowAsUnchecked(errorMapper);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> throwing.apply(1L));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1", cause.getMessage());

            verify(function).apply(1L);
            verify(function).onErrorThrowAsUnchecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(function, errorMapper);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingLongFunction<String, IOException> function = Spied.throwingLongFunction(throwable::throwUnchecked);

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            LongFunction<String> throwing = function.onErrorThrowAsUnchecked(errorMapper);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> throwing.apply(1L));
            assertEquals("1", thrown.getMessage());

            verify(function).apply(1L);
            verify(function).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }
    }

    @Nested
    class OnErrorHandleChecked {

        @Test
        void testNullArgument() {
            ThrowingLongFunction<String, IOException> function = Spied.throwingLongFunction(Long::toString);

            assertThrows(NullPointerException.class, () -> function.onErrorHandleChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingLongFunction<String, IOException> function = Spied.throwingLongFunction(Long::toString);

            ThrowingFunction<IOException, String, ExecutionException> errorHandler = Spied.throwingFunction(Exception::getMessage);

            ThrowingLongFunction<String, ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

            assertEquals("1", handling.apply(1L));

            verify(function).apply(1L);
            verify(function).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException, ExecutionException {
                ThrowingLongFunction<String, IOException> function = Spied.throwingLongFunction(l -> {
                    throw new IOException(Long.toString(l));
                });

                ThrowingFunction<IOException, String, ExecutionException> errorHandler = Spied.throwingFunction(Exception::getMessage);

                ThrowingLongFunction<String, ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

                assertEquals("1", handling.apply(1L));

                verify(function).apply(1L);
                verify(function).onErrorHandleChecked(errorHandler);
                verify(errorHandler).apply(any());
                verifyNoMoreInteractions(function, errorHandler);
            }

            @Test
            void testHandlerThrowsChecked() throws IOException, ExecutionException {
                ThrowingLongFunction<String, IOException> function = Spied.throwingLongFunction(l -> {
                    throw new IOException(Long.toString(l));
                });

                ThrowingFunction<IOException, String, ExecutionException> errorHandler = Spied.throwingFunction(e -> {
                    throw new ExecutionException(e);
                });

                ThrowingLongFunction<String, ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

                ExecutionException thrown = assertThrows(ExecutionException.class, () -> handling.apply(1L));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("1", cause.getMessage());

                verify(function).apply(1L);
                verify(function).onErrorHandleChecked(errorHandler);
                verify(errorHandler).apply(any());
                verifyNoMoreInteractions(function, errorHandler);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testHandlerThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ExecutionException {
                ThrowingLongFunction<String, IOException> function = Spied.throwingLongFunction(l -> {
                    throw new IOException(Long.toString(l));
                });

                ThrowingFunction<IOException, String, ExecutionException> errorHandler = Spied.throwingFunction(throwable::throwUnchecked);

                ThrowingLongFunction<String, ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.apply(1L));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("1", cause.getMessage());

                verify(function).apply(1L);
                verify(function).onErrorHandleChecked(errorHandler);
                verify(errorHandler).apply(any());
                verifyNoMoreInteractions(function, errorHandler);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingLongFunction<String, IOException> function = Spied.throwingLongFunction(throwable::throwUnchecked);

            ThrowingFunction<IOException, String, ExecutionException> errorHandler = Spied.throwingFunction(Exception::getMessage);

            ThrowingLongFunction<String, ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.apply(1L));
            assertEquals("1", thrown.getMessage());

            verify(function).apply(1L);
            verify(function).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }
    }

    @Nested
    class OnErrorHandleUnchecked {

        @Test
        void testNullArgument() {
            ThrowingLongFunction<String, IOException> function = Spied.throwingLongFunction(Long::toString);

            assertThrows(NullPointerException.class, () -> function.onErrorHandleUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingLongFunction<String, IOException> function = Spied.throwingLongFunction(Long::toString);

            Function<IOException, String> errorHandler = Spied.function(Exception::getMessage);

            LongFunction<String> handling = function.onErrorHandleUnchecked(errorHandler);

            assertEquals("1", handling.apply(1L));

            verify(function).apply(1L);
            verify(function).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException {
                ThrowingLongFunction<String, IOException> function = Spied.throwingLongFunction(l -> {
                    throw new IOException(Long.toString(l));
                });

                Function<IOException, String> errorHandler = Spied.function(Exception::getMessage);

                LongFunction<String> handling = function.onErrorHandleUnchecked(errorHandler);

                assertEquals("1", handling.apply(1L));

                verify(function).apply(1L);
                verify(function).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).apply(any());
                verifyNoMoreInteractions(function, errorHandler);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testHandlerThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingLongFunction<String, IOException> function = Spied.throwingLongFunction(l -> {
                    throw new IOException(Long.toString(l));
                });

                Function<IOException, String> errorHandler = Spied.function(throwable::throwUnchecked);

                LongFunction<String> handling = function.onErrorHandleUnchecked(errorHandler);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.apply(1L));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("1", cause.getMessage());

                verify(function).apply(1L);
                verify(function).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).apply(any());
                verifyNoMoreInteractions(function, errorHandler);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingLongFunction<String, IOException> function = Spied.throwingLongFunction(throwable::throwUnchecked);

            Function<IOException, String> errorHandler = Spied.function(Exception::getMessage);

            LongFunction<String> handling = function.onErrorHandleUnchecked(errorHandler);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.apply(1L));
            assertEquals("1", thrown.getMessage());

            verify(function).apply(1L);
            verify(function).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }
    }

    @Nested
    class OnErrorApplyChecked {

        @Test
        void testNullArgument() {
            ThrowingLongFunction<String, IOException> function = Spied.throwingLongFunction(Long::toString);

            assertThrows(NullPointerException.class, () -> function.onErrorApplyChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingLongFunction<String, IOException> function = Spied.throwingLongFunction(Long::toString);

            ThrowingLongFunction<String, ExecutionException> fallback = Spied.throwingLongFunction(l -> Long.toString(l + l));

            ThrowingLongFunction<String, ExecutionException> applying = function.onErrorApplyChecked(fallback);

            assertEquals("1", applying.apply(1L));

            verify(function).apply(1L);
            verify(function).onErrorApplyChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                ThrowingLongFunction<String, IOException> function = Spied.throwingLongFunction(l -> {
                    throw new IOException(Long.toString(l));
                });

                ThrowingLongFunction<String, ParseException> fallback = Spied.throwingLongFunction(l -> Long.toString(l + l));

                ThrowingLongFunction<String, ParseException> applying = function.onErrorApplyChecked(fallback);

                assertEquals("2", applying.apply(1L));

                verify(function).apply(1L);
                verify(function).onErrorApplyChecked(fallback);
                verify(fallback).apply(1L);
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                ThrowingLongFunction<String, IOException> function = Spied.throwingLongFunction(l -> {
                    throw new IOException(Long.toString(l));
                });

                ThrowingLongFunction<String, ParseException> fallback = Spied.throwingLongFunction(l -> {
                    throw new ParseException(Long.toString(l), 0);
                });

                ThrowingLongFunction<String, ParseException> applying = function.onErrorApplyChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> applying.apply(1L));
                assertEquals("1", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(function).apply(1L);
                verify(function).onErrorApplyChecked(fallback);
                verify(fallback).apply(1L);
                verifyNoMoreInteractions(function, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ParseException {
                ThrowingLongFunction<String, IOException> function = Spied.throwingLongFunction(l -> {
                    throw new IOException(Long.toString(l));
                });

                ThrowingLongFunction<String, ParseException> fallback = Spied.throwingLongFunction(throwable::throwUnchecked);

                ThrowingLongFunction<String, ParseException> applying = function.onErrorApplyChecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.apply(1L));
                assertEquals("1", thrown.getMessage());

                verify(function).apply(1L);
                verify(function).onErrorApplyChecked(fallback);
                verify(fallback).apply(1L);
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingLongFunction<String, IOException> function = Spied.throwingLongFunction(throwable::throwUnchecked);

            ThrowingLongFunction<String, ParseException> fallback = Spied.throwingLongFunction(l -> Long.toString(l + l));

            ThrowingLongFunction<String, ParseException> applying = function.onErrorApplyChecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.apply(1L));
            assertEquals("1", thrown.getMessage());

            verify(function).apply(1L);
            verify(function).onErrorApplyChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorApplyUnchecked {

        @Test
        void testNullArgument() {
            ThrowingLongFunction<String, IOException> function = Spied.throwingLongFunction(Long::toString);

            assertThrows(NullPointerException.class, () -> function.onErrorApplyUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingLongFunction<String, IOException> function = Spied.throwingLongFunction(Long::toString);

            LongFunction<String> fallback = Spied.longFunction(l -> Long.toString(l + l));

            LongFunction<String> applying = function.onErrorApplyUnchecked(fallback);

            assertEquals("1", applying.apply(1L));

            verify(function).apply(1L);
            verify(function).onErrorApplyUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                ThrowingLongFunction<String, IOException> function = Spied.throwingLongFunction(l -> {
                    throw new IOException(Long.toString(l));
                });

                LongFunction<String> fallback = Spied.longFunction(l -> Long.toString(l + l));

                LongFunction<String> applying = function.onErrorApplyUnchecked(fallback);

                assertEquals("2", applying.apply(1L));

                verify(function).apply(1L);
                verify(function).onErrorApplyUnchecked(fallback);
                verify(fallback).apply(1L);
                verifyNoMoreInteractions(function, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingLongFunction<String, IOException> function = Spied.throwingLongFunction(l -> {
                    throw new IOException(Long.toString(l));
                });

                LongFunction<String> fallback = Spied.longFunction(throwable::throwUnchecked);

                LongFunction<String> applying = function.onErrorApplyUnchecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.apply(1L));
                assertEquals("1", thrown.getMessage());

                verify(function).apply(1L);
                verify(function).onErrorApplyUnchecked(fallback);
                verify(fallback).apply(1L);
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingLongFunction<String, IOException> function = Spied.throwingLongFunction(throwable::throwUnchecked);

            LongFunction<String> fallback = Spied.longFunction(l -> Long.toString(l + l));

            LongFunction<String> applying = function.onErrorApplyUnchecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.apply(1L));
            assertEquals("1", thrown.getMessage());

            verify(function).apply(1L);
            verify(function).onErrorApplyUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorGetChecked {

        @Test
        void testNullArgument() {
            ThrowingLongFunction<String, IOException> function = Spied.throwingLongFunction(Long::toString);

            assertThrows(NullPointerException.class, () -> function.onErrorGetChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingLongFunction<String, IOException> function = Spied.throwingLongFunction(Long::toString);

            ThrowingSupplier<String, ExecutionException> fallback = Spied.throwingSupplier(() -> "bar");

            ThrowingLongFunction<String, ExecutionException> getting = function.onErrorGetChecked(fallback);

            assertEquals("1", getting.apply(1L));

            verify(function).apply(1L);
            verify(function).onErrorGetChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                ThrowingLongFunction<String, IOException> function = Spied.throwingLongFunction(l -> {
                    throw new IOException(Long.toString(l));
                });

                ThrowingSupplier<String, ParseException> fallback = Spied.throwingSupplier(() -> "bar");

                ThrowingLongFunction<String, ParseException> getting = function.onErrorGetChecked(fallback);

                assertEquals("bar", getting.apply(1L));

                verify(function).apply(1L);
                verify(function).onErrorGetChecked(fallback);
                verify(fallback).get();
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                ThrowingLongFunction<String, IOException> function = Spied.throwingLongFunction(l -> {
                    throw new IOException(Long.toString(l));
                });

                ThrowingSupplier<String, ParseException> fallback = Spied.throwingSupplier(() -> {
                    throw new ParseException("bar", 0);
                });

                ThrowingLongFunction<String, ParseException> getting = function.onErrorGetChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> getting.apply(1L));
                assertEquals("bar", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(function).apply(1L);
                verify(function).onErrorGetChecked(fallback);
                verify(fallback).get();
                verifyNoMoreInteractions(function, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ParseException {
                ThrowingLongFunction<String, IOException> function = Spied.throwingLongFunction(l -> {
                    throw new IOException(Long.toString(l));
                });

                ThrowingSupplier<String, ParseException> fallback = Spied.throwingSupplier(() -> throwable.throwUnchecked("bar"));

                ThrowingLongFunction<String, ParseException> getting = function.onErrorGetChecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.apply(1L));
                assertEquals("bar", thrown.getMessage());

                verify(function).apply(1L);
                verify(function).onErrorGetChecked(fallback);
                verify(fallback).get();
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingLongFunction<String, IOException> function = Spied.throwingLongFunction(throwable::throwUnchecked);

            ThrowingSupplier<String, ParseException> fallback = Spied.throwingSupplier(() -> "bar");

            ThrowingLongFunction<String, ParseException> getting = function.onErrorGetChecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.apply(1L));
            assertEquals("1", thrown.getMessage());

            verify(function).apply(1L);
            verify(function).onErrorGetChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorGetUnchecked {

        @Test
        void testNullArgument() {
            ThrowingLongFunction<String, IOException> function = Spied.throwingLongFunction(Long::toString);

            assertThrows(NullPointerException.class, () -> function.onErrorGetUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingLongFunction<String, IOException> function = Spied.throwingLongFunction(Long::toString);

            Supplier<String> fallback = Spied.supplier(() -> "bar");

            LongFunction<String> getting = function.onErrorGetUnchecked(fallback);

            assertEquals("1", getting.apply(1L));

            verify(function).apply(1L);
            verify(function).onErrorGetUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                ThrowingLongFunction<String, IOException> function = Spied.throwingLongFunction(l -> {
                    throw new IOException(Long.toString(l));
                });

                Supplier<String> fallback = Spied.supplier(() -> "bar");

                LongFunction<String> getting = function.onErrorGetUnchecked(fallback);

                assertEquals("bar", getting.apply(1L));

                verify(function).apply(1L);
                verify(function).onErrorGetUnchecked(fallback);
                verify(fallback).get();
                verifyNoMoreInteractions(function, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingLongFunction<String, IOException> function = Spied.throwingLongFunction(l -> {
                    throw new IOException(Long.toString(l));
                });

                Supplier<String> fallback = Spied.supplier(() -> throwable.throwUnchecked("bar"));

                LongFunction<String> getting = function.onErrorGetUnchecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.apply(1L));
                assertEquals("bar", thrown.getMessage());

                verify(function).apply(1L);
                verify(function).onErrorGetUnchecked(fallback);
                verify(fallback).get();
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingLongFunction<String, IOException> function = Spied.throwingLongFunction(throwable::throwUnchecked);

            Supplier<String> fallback = Spied.supplier(() -> "bar");

            LongFunction<String> getting = function.onErrorGetUnchecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.apply(1L));
            assertEquals("1", thrown.getMessage());

            verify(function).apply(1L);
            verify(function).onErrorGetUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorReturn {

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingLongFunction<String, IOException> function = Spied.throwingLongFunction(Long::toString);

            LongFunction<String> returning = function.onErrorReturn("bar");

            assertEquals("1", returning.apply(1L));

            verify(function).apply(1L);
            verify(function).onErrorReturn("bar");
            verifyNoMoreInteractions(function);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingLongFunction<String, IOException> function = Spied.throwingLongFunction(l -> {
                throw new IOException(Long.toString(l));
            });

            LongFunction<String> returning = function.onErrorReturn("bar");

            assertEquals("bar", returning.apply(1L));

            verify(function).apply(1L);
            verify(function).onErrorReturn("bar");
            verifyNoMoreInteractions(function);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingLongFunction<String, IOException> function = Spied.throwingLongFunction(throwable::throwUnchecked);

            LongFunction<String> returning = function.onErrorReturn("bar");

            Throwable thrown = assertThrows(throwable.throwableType(), () -> returning.apply(1L));
            assertEquals("1", thrown.getMessage());

            verify(function).apply(1L);
            verify(function).onErrorReturn("bar");
            verifyNoMoreInteractions(function);
        }
    }

    @Nested
    class Unchecked {

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingLongFunction<String, IOException> function = Spied.throwingLongFunction(Long::toString);

            LongFunction<String> unchecked = function.unchecked();

            assertEquals("1", unchecked.apply(1L));

            verify(function).apply(1L);
            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(function);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingLongFunction<String, IOException> function = Spied.throwingLongFunction(l -> {
                throw new IOException(Long.toString(l));
            });

            LongFunction<String> unchecked = function.unchecked();

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.apply(1L));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1", cause.getMessage());

            verify(function).apply(1L);
            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(function);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingLongFunction<String, IOException> function = Spied.throwingLongFunction(throwable::throwUnchecked);

            LongFunction<String> unchecked = function.unchecked();

            Throwable thrown = assertThrows(throwable.throwableType(), () -> unchecked.apply(1L));
            assertEquals("1", thrown.getMessage());

            verify(function).apply(1L);
            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(function);
        }
    }

    @Nested
    class Of {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> ThrowingLongFunction.of(null));
        }

        @Test
        void testNonNullArgument() throws IOException {
            ThrowingLongFunction<String, IOException> function = ThrowingLongFunction.of(Long::toString);

            assertEquals("1", function.apply(1L));
        }
    }

    @Nested
    class UncheckedFactory {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> ThrowingLongFunction.unchecked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ThrowingLongFunction<String, IOException> function = Spied.throwingLongFunction(Long::toString);

            LongFunction<String> unchecked = ThrowingLongFunction.unchecked(function);

            assertEquals("1", unchecked.apply(1L));

            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verify(function).apply(1L);
            verifyNoMoreInteractions(function);
        }

        @Test
        void testArgumentThrowsChecked() throws IOException {
            ThrowingLongFunction<String, IOException> function = Spied.throwingLongFunction(l -> {
                throw new IOException(Long.toString(l));
            });

            LongFunction<String> unchecked = ThrowingLongFunction.unchecked(function);

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.apply(1L));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1", cause.getMessage());

            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verify(function).apply(1L);
            verifyNoMoreInteractions(function);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testArgumentThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingLongFunction<String, IOException> function = Spied.throwingLongFunction(throwable::throwUnchecked);

            LongFunction<String> unchecked = ThrowingLongFunction.unchecked(function);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> unchecked.apply(1L));
            assertEquals("1", thrown.getMessage());

            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verify(function).apply(1L);
            verifyNoMoreInteractions(function);
        }
    }

    @Nested
    class CheckedWrapper {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> ThrowingLongFunction.checked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ThrowingLongFunction<String, IOException> function = ThrowingLongFunction.checked(Long::toString);

            assertEquals("1", function.apply(1L));
        }

        @Test
        void testArgumentThrowsUnchecked() {
            ThrowingLongFunction<String, IOException> function = ThrowingLongFunction.checked(l -> {
                throw UncheckedException.withoutStackTrace(Long.toString(l), new IOException());
            });

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> function.apply(1L));
            assertEquals("1", thrown.getMessage());
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertNull(cause.getMessage());
        }
    }

    @Nested
    class CheckedUnwrapper {

        @Test
        void testNullArguments() {
            LongFunction<String> function = Long::toString;

            assertThrows(NullPointerException.class, () -> ThrowingLongFunction.checked(null, IOException.class));
            assertThrows(NullPointerException.class, () -> ThrowingLongFunction.checked(function, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ThrowingLongFunction<String, IOException> function = ThrowingLongFunction.checked(Long::toString, IOException.class);

            assertEquals("1", function.apply(1L));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                ThrowingLongFunction<String, IOException> function = ThrowingLongFunction.checked(l -> {
                    throw UncheckedException.withoutStackTrace(new IOException(Long.toString(l)));
                }, IOException.class);

                IOException thrown = assertThrows(IOException.class, () -> function.apply(1L));
                assertEquals("1", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                ThrowingLongFunction<String, IOException> function = ThrowingLongFunction.checked(l -> {
                    throw UncheckedException.withoutStackTrace(new FileNotFoundException(Long.toString(l)));
                }, IOException.class);

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class, () -> function.apply(1L));
                assertEquals("1", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                ThrowingLongFunction<String, IOException> function = ThrowingLongFunction.checked(l -> {
                    throw UncheckedException.withoutStackTrace(new ParseException(Long.toString(l), 0));
                }, IOException.class);

                UncheckedException thrown = assertThrows(UncheckedException.class, () -> function.apply(1L));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("1", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            ThrowingLongFunction<String, IOException> function = ThrowingLongFunction.checked(l -> {
                throw new IllegalStateException(Long.toString(l));
            }, IOException.class);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> function.apply(1L));
            assertEquals("1", thrown.getMessage());
        }
    }

    @Nested
    class InvokeAndUnwrap {

        @Test
        void testNullArguments() {
            LongFunction<String> function = l -> String.valueOf(Long.toString(l)).toUpperCase();

            assertThrows(NullPointerException.class, () -> ThrowingLongFunction.invokeAndUnwrap(null, 1L, IOException.class));
            assertThrows(NullPointerException.class, () -> ThrowingLongFunction.invokeAndUnwrap(function, 1L, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            LongFunction<String> function = Long::toString;

            assertEquals("1", ThrowingLongFunction.invokeAndUnwrap(function, 1L, IOException.class));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                LongFunction<String> function = l -> {
                    throw UncheckedException.withoutStackTrace(new IOException(Long.toString(l)));
                };

                IOException thrown = assertThrows(IOException.class, () -> ThrowingLongFunction.invokeAndUnwrap(function, 1L, IOException.class));
                assertEquals("1", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                LongFunction<String> function = l -> {
                    throw UncheckedException.withoutStackTrace(new FileNotFoundException(Long.toString(l)));
                };

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class,
                        () -> ThrowingLongFunction.invokeAndUnwrap(function, 1L, IOException.class));
                assertEquals("1", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                LongFunction<String> function = l -> {
                    throw UncheckedException.withoutStackTrace(new ParseException(Long.toString(l), 0));
                };

                UncheckedException thrown = assertThrows(UncheckedException.class,
                        () -> ThrowingLongFunction.invokeAndUnwrap(function, 1L, IOException.class));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("1", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            LongFunction<String> function = l -> {
                throw new IllegalStateException(Long.toString(l));
            };

            IllegalStateException thrown = assertThrows(IllegalStateException.class,
                    () -> ThrowingLongFunction.invokeAndUnwrap(function, 1L, IOException.class));
            assertEquals("1", thrown.getMessage());
        }
    }
}
