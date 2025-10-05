/*
 * ThrowingLongSupplierTest.java
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
import java.util.function.LongSupplier;
import java.util.function.ToLongFunction;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

@SuppressWarnings("nls")
class ThrowingLongSupplierTest {

    @Nested
    class OnErrorThrowAsChecked {

        @Test
        void testNullArgument() {
            ThrowingLongSupplier<IOException> supplier = Spied.throwingLongSupplier(() -> 1L);

            assertThrows(NullPointerException.class, () -> supplier.onErrorThrowAsChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingLongSupplier<IOException> supplier = Spied.throwingLongSupplier(() -> 1L);

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingLongSupplier<ExecutionException> throwing = supplier.onErrorThrowAsChecked(errorMapper);

            assertEquals(1L, throwing.getAsLong());

            verify(supplier).getAsLong();
            verify(supplier).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(supplier, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingLongSupplier<IOException> supplier = Spied.throwingLongSupplier(() -> {
                throw new IOException("foo");
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingLongSupplier<ExecutionException> throwing = supplier.onErrorThrowAsChecked(errorMapper);

            ExecutionException thrown = assertThrows(ExecutionException.class, throwing::getAsLong);
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo", cause.getMessage());

            verify(supplier).getAsLong();
            verify(supplier).onErrorThrowAsChecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(supplier, errorMapper);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingLongSupplier<IOException> supplier = Spied.throwingLongSupplier(() -> throwable.throwUnchecked("foo"));

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingLongSupplier<ExecutionException> throwing = supplier.onErrorThrowAsChecked(errorMapper);

            Throwable thrown = assertThrows(throwable.throwableType(), throwing::getAsLong);
            assertEquals("foo", thrown.getMessage());

            verify(supplier).getAsLong();
            verify(supplier).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(supplier, errorMapper);
        }
    }

    @Nested
    class OnErrorThrowAsUnchecked {

        @Test
        void testNullArgument() {
            ThrowingLongSupplier<IOException> supplier = Spied.throwingLongSupplier(() -> 1L);

            assertThrows(NullPointerException.class, () -> supplier.onErrorThrowAsUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingLongSupplier<IOException> supplier = Spied.throwingLongSupplier(() -> 1L);

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            LongSupplier throwing = supplier.onErrorThrowAsUnchecked(errorMapper);

            assertEquals(1L, throwing.getAsLong());

            verify(supplier).getAsLong();
            verify(supplier).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(supplier, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingLongSupplier<IOException> supplier = Spied.throwingLongSupplier(() -> {
                throw new IOException("foo");
            });

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            LongSupplier throwing = supplier.onErrorThrowAsUnchecked(errorMapper);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, throwing::getAsLong);
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo", cause.getMessage());

            verify(supplier).getAsLong();
            verify(supplier).onErrorThrowAsUnchecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(supplier, errorMapper);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingLongSupplier<IOException> supplier = Spied.throwingLongSupplier(() -> throwable.throwUnchecked("foo"));

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            LongSupplier throwing = supplier.onErrorThrowAsUnchecked(errorMapper);

            Throwable thrown = assertThrows(throwable.throwableType(), throwing::getAsLong);
            assertEquals("foo", thrown.getMessage());

            verify(supplier).getAsLong();
            verify(supplier).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(supplier, errorMapper);
        }
    }

    @Nested
    class OnErrorHandleChecked {

        @Test
        void testNullArgument() {
            ThrowingLongSupplier<IOException> supplier = Spied.throwingLongSupplier(() -> 1L);

            assertThrows(NullPointerException.class, () -> supplier.onErrorHandleChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingLongSupplier<IOException> supplier = Spied.throwingLongSupplier(() -> 1L);

            ThrowingToLongFunction<IOException, ExecutionException> errorHandler = Spied.throwingToLongFunction(e -> 0L);

            ThrowingLongSupplier<ExecutionException> handling = supplier.onErrorHandleChecked(errorHandler);

            assertEquals(1L, handling.getAsLong());

            verify(supplier).getAsLong();
            verify(supplier).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(supplier, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException, ExecutionException {
                ThrowingLongSupplier<IOException> supplier = Spied.throwingLongSupplier(() -> {
                    throw new IOException("foo");
                });

                ThrowingToLongFunction<IOException, ExecutionException> errorHandler = Spied.throwingToLongFunction(e -> 0L);

                ThrowingLongSupplier<ExecutionException> handling = supplier.onErrorHandleChecked(errorHandler);

                assertEquals(0L, handling.getAsLong());

                verify(supplier).getAsLong();
                verify(supplier).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsLong(any());
                verifyNoMoreInteractions(supplier, errorHandler);
            }

            @Test
            void testHandlerThrowsChecked() throws IOException, ExecutionException {
                ThrowingLongSupplier<IOException> supplier = Spied.throwingLongSupplier(() -> {
                    throw new IOException("foo");
                });

                ThrowingToLongFunction<IOException, ExecutionException> errorHandler = Spied.throwingToLongFunction(e -> {
                    throw new ExecutionException(e);
                });

                ThrowingLongSupplier<ExecutionException> handling = supplier.onErrorHandleChecked(errorHandler);

                ExecutionException thrown = assertThrows(ExecutionException.class, handling::getAsLong);
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());

                verify(supplier).getAsLong();
                verify(supplier).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsLong(any());
                verifyNoMoreInteractions(supplier, errorHandler);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testHandlerThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ExecutionException {
                ThrowingLongSupplier<IOException> supplier = Spied.throwingLongSupplier(() -> {
                    throw new IOException("foo");
                });

                ThrowingToLongFunction<IOException, ExecutionException> errorHandler = Spied.throwingToLongFunction(throwable::throwUnchecked);

                ThrowingLongSupplier<ExecutionException> handling = supplier.onErrorHandleChecked(errorHandler);

                Throwable thrown = assertThrows(throwable.throwableType(), handling::getAsLong);
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());

                verify(supplier).getAsLong();
                verify(supplier).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsLong(any());
                verifyNoMoreInteractions(supplier, errorHandler);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingLongSupplier<IOException> supplier = Spied.throwingLongSupplier(() -> throwable.throwUnchecked("foo"));

            ThrowingToLongFunction<IOException, ExecutionException> errorHandler = Spied.throwingToLongFunction(e -> 0L);

            ThrowingLongSupplier<ExecutionException> handling = supplier.onErrorHandleChecked(errorHandler);

            Throwable thrown = assertThrows(throwable.throwableType(), handling::getAsLong);
            assertEquals("foo", thrown.getMessage());

            verify(supplier).getAsLong();
            verify(supplier).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(supplier, errorHandler);
        }
    }

    @Nested
    class OnErrorHandleUnchecked {

        @Test
        void testNullArgument() {
            ThrowingLongSupplier<IOException> supplier = Spied.throwingLongSupplier(() -> 1L);

            assertThrows(NullPointerException.class, () -> supplier.onErrorHandleUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingLongSupplier<IOException> supplier = Spied.throwingLongSupplier(() -> 1L);

            ToLongFunction<IOException> errorHandler = Spied.toLongFunction(e -> 0L);

            LongSupplier handling = supplier.onErrorHandleUnchecked(errorHandler);

            assertEquals(1L, handling.getAsLong());

            verify(supplier).getAsLong();
            verify(supplier).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(supplier, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException {
                ThrowingLongSupplier<IOException> supplier = Spied.throwingLongSupplier(() -> {
                    throw new IOException("foo");
                });

                ToLongFunction<IOException> errorHandler = Spied.toLongFunction(e -> 0L);

                LongSupplier handling = supplier.onErrorHandleUnchecked(errorHandler);

                assertEquals(0L, handling.getAsLong());

                verify(supplier).getAsLong();
                verify(supplier).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).applyAsLong(any());
                verifyNoMoreInteractions(supplier, errorHandler);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testHandlerThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingLongSupplier<IOException> supplier = Spied.throwingLongSupplier(() -> {
                    throw new IOException("foo");
                });

                ToLongFunction<IOException> errorHandler = Spied.toLongFunction(throwable::throwUnchecked);

                LongSupplier handling = supplier.onErrorHandleUnchecked(errorHandler);

                Throwable thrown = assertThrows(throwable.throwableType(), handling::getAsLong);
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());

                verify(supplier).getAsLong();
                verify(supplier).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).applyAsLong(any());
                verifyNoMoreInteractions(supplier, errorHandler);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingLongSupplier<IOException> supplier = Spied.throwingLongSupplier(() -> throwable.throwUnchecked("foo"));

            ToLongFunction<IOException> errorHandler = Spied.toLongFunction(e -> 0L);

            LongSupplier handling = supplier.onErrorHandleUnchecked(errorHandler);

            Throwable thrown = assertThrows(throwable.throwableType(), handling::getAsLong);
            assertEquals("foo", thrown.getMessage());

            verify(supplier).getAsLong();
            verify(supplier).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(supplier, errorHandler);
        }
    }

    @Nested
    class OnErrorGetCheckedAsLong {

        @Test
        void testNullArgument() {
            ThrowingLongSupplier<IOException> supplier = Spied.throwingLongSupplier(() -> 1L);

            assertThrows(NullPointerException.class, () -> supplier.onErrorGetCheckedAsLong(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingLongSupplier<IOException> supplier = Spied.throwingLongSupplier(() -> 1L);

            ThrowingLongSupplier<ExecutionException> fallback = Spied.throwingLongSupplier(() -> 0L);

            ThrowingLongSupplier<ExecutionException> getting = supplier.onErrorGetCheckedAsLong(fallback);

            assertEquals(1L, getting.getAsLong());

            verify(supplier).getAsLong();
            verify(supplier).onErrorGetCheckedAsLong(fallback);
            verifyNoMoreInteractions(supplier, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                ThrowingLongSupplier<IOException> supplier = Spied.throwingLongSupplier(() -> {
                    throw new IOException("foo");
                });

                ThrowingLongSupplier<ParseException> fallback = Spied.throwingLongSupplier(() -> 0L);

                ThrowingLongSupplier<ParseException> getting = supplier.onErrorGetCheckedAsLong(fallback);

                assertEquals(0L, getting.getAsLong());

                verify(supplier).getAsLong();
                verify(supplier).onErrorGetCheckedAsLong(fallback);
                verify(fallback).getAsLong();
                verifyNoMoreInteractions(supplier, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                ThrowingLongSupplier<IOException> supplier = Spied.throwingLongSupplier(() -> {
                    throw new IOException("foo");
                });

                ThrowingLongSupplier<ParseException> fallback = Spied.throwingLongSupplier(() -> {
                    throw new ParseException("bar", 0);
                });

                ThrowingLongSupplier<ParseException> getting = supplier.onErrorGetCheckedAsLong(fallback);

                ParseException thrown = assertThrows(ParseException.class, getting::getAsLong);
                assertEquals("bar", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(supplier).getAsLong();
                verify(supplier).onErrorGetCheckedAsLong(fallback);
                verify(fallback).getAsLong();
                verifyNoMoreInteractions(supplier, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ParseException {
                ThrowingLongSupplier<IOException> supplier = Spied.throwingLongSupplier(() -> {
                    throw new IOException("foo");
                });

                ThrowingLongSupplier<ParseException> fallback = Spied.throwingLongSupplier(() -> throwable.throwUnchecked("bar"));

                ThrowingLongSupplier<ParseException> getting = supplier.onErrorGetCheckedAsLong(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), getting::getAsLong);
                assertEquals("bar", thrown.getMessage());

                verify(supplier).getAsLong();
                verify(supplier).onErrorGetCheckedAsLong(fallback);
                verify(fallback).getAsLong();
                verifyNoMoreInteractions(supplier, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingLongSupplier<IOException> supplier = Spied.throwingLongSupplier(() -> throwable.throwUnchecked("foo"));

            ThrowingLongSupplier<ParseException> fallback = Spied.throwingLongSupplier(() -> 0L);

            ThrowingLongSupplier<ParseException> getting = supplier.onErrorGetCheckedAsLong(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), getting::getAsLong);
            assertEquals("foo", thrown.getMessage());

            verify(supplier).getAsLong();
            verify(supplier).onErrorGetCheckedAsLong(fallback);
            verifyNoMoreInteractions(supplier, fallback);
        }
    }

    @Nested
    class OnErrorGetUncheckedAsLong {

        @Test
        void testNullArgument() {
            ThrowingLongSupplier<IOException> supplier = Spied.throwingLongSupplier(() -> 1L);

            assertThrows(NullPointerException.class, () -> supplier.onErrorGetUncheckedAsLong(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingLongSupplier<IOException> supplier = Spied.throwingLongSupplier(() -> 1L);

            LongSupplier fallback = Spied.longSupplier(() -> 0L);

            LongSupplier getting = supplier.onErrorGetUncheckedAsLong(fallback);

            assertEquals(1L, getting.getAsLong());

            verify(supplier).getAsLong();
            verify(supplier).onErrorGetUncheckedAsLong(fallback);
            verifyNoMoreInteractions(supplier, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                ThrowingLongSupplier<IOException> supplier = Spied.throwingLongSupplier(() -> {
                    throw new IOException("foo");
                });

                LongSupplier fallback = Spied.longSupplier(() -> 0L);

                LongSupplier getting = supplier.onErrorGetUncheckedAsLong(fallback);

                assertEquals(0L, getting.getAsLong());

                verify(supplier).getAsLong();
                verify(supplier).onErrorGetUncheckedAsLong(fallback);
                verify(fallback).getAsLong();
                verifyNoMoreInteractions(supplier, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingLongSupplier<IOException> supplier = Spied.throwingLongSupplier(() -> {
                    throw new IOException("foo");
                });

                LongSupplier fallback = Spied.longSupplier(() -> throwable.throwUnchecked("bar"));

                LongSupplier getting = supplier.onErrorGetUncheckedAsLong(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), getting::getAsLong);
                assertEquals("bar", thrown.getMessage());

                verify(supplier).getAsLong();
                verify(supplier).onErrorGetUncheckedAsLong(fallback);
                verify(fallback).getAsLong();
                verifyNoMoreInteractions(supplier, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingLongSupplier<IOException> supplier = Spied.throwingLongSupplier(() -> throwable.throwUnchecked("foo"));

            LongSupplier fallback = Spied.longSupplier(() -> 0L);

            LongSupplier getting = supplier.onErrorGetUncheckedAsLong(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), getting::getAsLong);
            assertEquals("foo", thrown.getMessage());

            verify(supplier).getAsLong();
            verify(supplier).onErrorGetUncheckedAsLong(fallback);
            verifyNoMoreInteractions(supplier, fallback);
        }
    }

    @Nested
    class OnErrorReturn {

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingLongSupplier<IOException> supplier = Spied.throwingLongSupplier(() -> 1L);

            LongSupplier returning = supplier.onErrorReturn(0L);

            assertEquals(1L, returning.getAsLong());

            verify(supplier).getAsLong();
            verify(supplier).onErrorReturn(0L);
            verifyNoMoreInteractions(supplier);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingLongSupplier<IOException> supplier = Spied.throwingLongSupplier(() -> {
                throw new IOException("foo");
            });

            LongSupplier returning = supplier.onErrorReturn(0L);

            assertEquals(0L, returning.getAsLong());

            verify(supplier).getAsLong();
            verify(supplier).onErrorReturn(0L);
            verifyNoMoreInteractions(supplier);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingLongSupplier<IOException> supplier = Spied.throwingLongSupplier(() -> throwable.throwUnchecked("foo"));

            LongSupplier returning = supplier.onErrorReturn(0L);

            Throwable thrown = assertThrows(throwable.throwableType(), returning::getAsLong);
            assertEquals("foo", thrown.getMessage());

            verify(supplier).getAsLong();
            verify(supplier).onErrorReturn(0L);
            verifyNoMoreInteractions(supplier);
        }
    }

    @Nested
    class Unchecked {

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingLongSupplier<IOException> supplier = Spied.throwingLongSupplier(() -> 1L);

            LongSupplier unchecked = supplier.unchecked();

            assertEquals(1L, unchecked.getAsLong());

            verify(supplier).getAsLong();
            verify(supplier).unchecked();
            verify(supplier).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(supplier);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingLongSupplier<IOException> supplier = Spied.throwingLongSupplier(() -> {
                throw new IOException("foo");
            });

            LongSupplier unchecked = supplier.unchecked();

            UncheckedException thrown = assertThrows(UncheckedException.class, unchecked::getAsLong);
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo", cause.getMessage());

            verify(supplier).getAsLong();
            verify(supplier).unchecked();
            verify(supplier).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(supplier);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingLongSupplier<IOException> supplier = Spied.throwingLongSupplier(() -> throwable.throwUnchecked("foo"));

            LongSupplier unchecked = supplier.unchecked();

            Throwable thrown = assertThrows(throwable.throwableType(), unchecked::getAsLong);
            assertEquals("foo", thrown.getMessage());

            verify(supplier).getAsLong();
            verify(supplier).unchecked();
            verify(supplier).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(supplier);
        }
    }

    @Nested
    class Of {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> ThrowingLongSupplier.of(null));
        }

        @Test
        void testNonNullArgument() throws IOException {
            ThrowingLongSupplier<IOException> supplier = ThrowingLongSupplier.of(() -> 1L);

            assertEquals(1L, supplier.getAsLong());
        }
    }

    @Nested
    class UncheckedFactory {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> ThrowingLongSupplier.unchecked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ThrowingLongSupplier<IOException> supplier = Spied.throwingLongSupplier(() -> 1L);

            LongSupplier unchecked = ThrowingLongSupplier.unchecked(supplier);

            assertEquals(1L, unchecked.getAsLong());

            verify(supplier).unchecked();
            verify(supplier).onErrorThrowAsUnchecked(any());
            verify(supplier).getAsLong();
            verifyNoMoreInteractions(supplier);
        }

        @Test
        void testArgumentThrowsChecked() throws IOException {
            ThrowingLongSupplier<IOException> supplier = Spied.throwingLongSupplier(() -> {
                throw new IOException("foo");
            });

            LongSupplier unchecked = ThrowingLongSupplier.unchecked(supplier);

            UncheckedException thrown = assertThrows(UncheckedException.class, unchecked::getAsLong);
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo", cause.getMessage());

            verify(supplier).unchecked();
            verify(supplier).onErrorThrowAsUnchecked(any());
            verify(supplier).getAsLong();
            verifyNoMoreInteractions(supplier);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testArgumentThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingLongSupplier<IOException> supplier = Spied.throwingLongSupplier(() -> throwable.throwUnchecked("foo"));

            LongSupplier unchecked = ThrowingLongSupplier.unchecked(supplier);

            Throwable thrown = assertThrows(throwable.throwableType(), unchecked::getAsLong);
            assertEquals("foo", thrown.getMessage());

            verify(supplier).unchecked();
            verify(supplier).onErrorThrowAsUnchecked(any());
            verify(supplier).getAsLong();
            verifyNoMoreInteractions(supplier);
        }
    }

    @Nested
    class CheckedWrapper {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> ThrowingLongSupplier.checked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ThrowingLongSupplier<IOException> supplier = ThrowingLongSupplier.checked(() -> 1L);

            assertEquals(1L, supplier.getAsLong());
        }

        @Test
        void testArgumentThrowsUnchecked() {
            ThrowingLongSupplier<IOException> supplier = ThrowingLongSupplier.checked(() -> {
                throw UncheckedException.withoutStackTrace("foo", new IOException());
            });

            UncheckedException thrown = assertThrows(UncheckedException.class, supplier::getAsLong);
            assertEquals("foo", thrown.getMessage());
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertNull(cause.getMessage());
        }
    }

    @Nested
    class CheckedUnwrapper {

        @Test
        void testNullArguments() {
            LongSupplier supplier = () -> 1L;

            assertThrows(NullPointerException.class, () -> ThrowingLongSupplier.checked(null, IOException.class));
            assertThrows(NullPointerException.class, () -> ThrowingLongSupplier.checked(supplier, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ThrowingLongSupplier<IOException> supplier = ThrowingLongSupplier.checked(() -> 1L, IOException.class);

            assertEquals(1L, supplier.getAsLong());
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                ThrowingLongSupplier<IOException> supplier = ThrowingLongSupplier.checked(() -> {
                    throw UncheckedException.withoutStackTrace(new IOException("foo"));
                }, IOException.class);

                IOException thrown = assertThrows(IOException.class, supplier::getAsLong);
                assertEquals("foo", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                ThrowingLongSupplier<IOException> supplier = ThrowingLongSupplier.checked(() -> {
                    throw UncheckedException.withoutStackTrace(new FileNotFoundException("foo"));
                }, IOException.class);

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class, supplier::getAsLong);
                assertEquals("foo", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                ThrowingLongSupplier<IOException> supplier = ThrowingLongSupplier.checked(() -> {
                    throw UncheckedException.withoutStackTrace(new ParseException("foo", 0));
                }, IOException.class);

                UncheckedException thrown = assertThrows(UncheckedException.class, supplier::getAsLong);
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            ThrowingLongSupplier<IOException> supplier = ThrowingLongSupplier.checked(() -> {
                throw new IllegalStateException("foo");
            }, IOException.class);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, supplier::getAsLong);
            assertEquals("foo", thrown.getMessage());
        }
    }

    @Nested
    class InvokeAndUnwrap {

        @Test
        void testNullArguments() {
            LongSupplier supplier = () -> 1L;

            assertThrows(NullPointerException.class, () -> ThrowingLongSupplier.invokeAndUnwrap(null, IOException.class));
            assertThrows(NullPointerException.class, () -> ThrowingLongSupplier.invokeAndUnwrap(supplier, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            LongSupplier supplier = () -> 1L;

            assertEquals(1L, ThrowingLongSupplier.invokeAndUnwrap(supplier, IOException.class));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                LongSupplier supplier = () -> {
                    throw UncheckedException.withoutStackTrace(new IOException("foo"));
                };

                IOException thrown = assertThrows(IOException.class, () -> ThrowingLongSupplier.invokeAndUnwrap(supplier, IOException.class));
                assertEquals("foo", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                LongSupplier supplier = () -> {
                    throw UncheckedException.withoutStackTrace(new FileNotFoundException("foo"));
                };

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class,
                        () -> ThrowingLongSupplier.invokeAndUnwrap(supplier, IOException.class));
                assertEquals("foo", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                LongSupplier supplier = () -> {
                    throw UncheckedException.withoutStackTrace(new ParseException("foo", 0));
                };

                UncheckedException thrown = assertThrows(UncheckedException.class,
                        () -> ThrowingLongSupplier.invokeAndUnwrap(supplier, IOException.class));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            LongSupplier supplier = () -> {
                throw new IllegalStateException("foo");
            };

            IllegalStateException thrown = assertThrows(IllegalStateException.class,
                    () -> ThrowingLongSupplier.invokeAndUnwrap(supplier, IOException.class));
            assertEquals("foo", thrown.getMessage());
        }
    }
}
