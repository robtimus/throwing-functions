/*
 * ThrowingSupplierTest.java
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
import java.util.function.Supplier;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

@SuppressWarnings("nls")
class ThrowingSupplierTest {

    @Nested
    class OnErrorThrowAsChecked {

        @Test
        void testNullArgument() {
            ThrowingSupplier<String, IOException> supplier = Spied.throwingSupplier(() -> "foo");

            assertThrows(NullPointerException.class, () -> supplier.onErrorThrowAsChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingSupplier<String, IOException> supplier = Spied.throwingSupplier(() -> "foo");

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingSupplier<String, ExecutionException> throwing = supplier.onErrorThrowAsChecked(errorMapper);

            assertEquals("foo", throwing.get());

            verify(supplier).get();
            verify(supplier).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(supplier, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingSupplier<String, IOException> supplier = Spied.throwingSupplier(() -> {
                throw new IOException("foo");
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingSupplier<String, ExecutionException> throwing = supplier.onErrorThrowAsChecked(errorMapper);

            ExecutionException thrown = assertThrows(ExecutionException.class, throwing::get);
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo", cause.getMessage());

            verify(supplier).get();
            verify(supplier).onErrorThrowAsChecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(supplier, errorMapper);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingSupplier<String, IOException> supplier = Spied.throwingSupplier(() -> throwable.throwUnchecked("foo"));

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingSupplier<String, ExecutionException> throwing = supplier.onErrorThrowAsChecked(errorMapper);

            Throwable thrown = assertThrows(throwable.throwableType(), throwing::get);
            assertEquals("foo", thrown.getMessage());

            verify(supplier).get();
            verify(supplier).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(supplier, errorMapper);
        }
    }

    @Nested
    class OnErrorThrowAsUnchecked {

        @Test
        void testNullArgument() {
            ThrowingSupplier<String, IOException> supplier = Spied.throwingSupplier(() -> "foo");

            assertThrows(NullPointerException.class, () -> supplier.onErrorThrowAsUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingSupplier<String, IOException> supplier = Spied.throwingSupplier(() -> "foo");

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            Supplier<String> throwing = supplier.onErrorThrowAsUnchecked(errorMapper);

            assertEquals("foo", throwing.get());

            verify(supplier).get();
            verify(supplier).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(supplier, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingSupplier<String, IOException> supplier = Spied.throwingSupplier(() -> {
                throw new IOException("foo");
            });

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            Supplier<String> throwing = supplier.onErrorThrowAsUnchecked(errorMapper);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, throwing::get);
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo", cause.getMessage());

            verify(supplier).get();
            verify(supplier).onErrorThrowAsUnchecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(supplier, errorMapper);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingSupplier<String, IOException> supplier = Spied.throwingSupplier(() -> throwable.throwUnchecked("foo"));

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            Supplier<String> throwing = supplier.onErrorThrowAsUnchecked(errorMapper);

            Throwable thrown = assertThrows(throwable.throwableType(), throwing::get);
            assertEquals("foo", thrown.getMessage());

            verify(supplier).get();
            verify(supplier).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(supplier, errorMapper);
        }
    }

    @Nested
    class OnErrorHandleChecked {

        @Test
        void testNullArgument() {
            ThrowingSupplier<String, IOException> supplier = Spied.throwingSupplier(() -> "foo");

            assertThrows(NullPointerException.class, () -> supplier.onErrorHandleChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingSupplier<String, IOException> supplier = Spied.throwingSupplier(() -> "foo");

            ThrowingFunction<IOException, String, ExecutionException> errorHandler = Spied.throwingFunction(Exception::getMessage);

            ThrowingSupplier<String, ExecutionException> handling = supplier.onErrorHandleChecked(errorHandler);

            assertEquals("foo", handling.get());

            verify(supplier).get();
            verify(supplier).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(supplier, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException, ExecutionException {
                ThrowingSupplier<String, IOException> supplier = Spied.throwingSupplier(() -> {
                    throw new IOException("foo");
                });

                ThrowingFunction<IOException, String, ExecutionException> errorHandler = Spied.throwingFunction(Exception::getMessage);

                ThrowingSupplier<String, ExecutionException> handling = supplier.onErrorHandleChecked(errorHandler);

                assertEquals("foo", handling.get());

                verify(supplier).get();
                verify(supplier).onErrorHandleChecked(errorHandler);
                verify(errorHandler).apply(any());
                verifyNoMoreInteractions(supplier, errorHandler);
            }

            @Test
            void testHandlerThrowsChecked() throws IOException, ExecutionException {
                ThrowingSupplier<String, IOException> supplier = Spied.throwingSupplier(() -> {
                    throw new IOException("foo");
                });

                ThrowingFunction<IOException, String, ExecutionException> errorHandler = Spied.throwingFunction(e -> {
                    throw new ExecutionException(e);
                });

                ThrowingSupplier<String, ExecutionException> handling = supplier.onErrorHandleChecked(errorHandler);

                ExecutionException thrown = assertThrows(ExecutionException.class, handling::get);
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());

                verify(supplier).get();
                verify(supplier).onErrorHandleChecked(errorHandler);
                verify(errorHandler).apply(any());
                verifyNoMoreInteractions(supplier, errorHandler);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testHandlerThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ExecutionException {
                ThrowingSupplier<String, IOException> supplier = Spied.throwingSupplier(() -> {
                    throw new IOException("foo");
                });

                ThrowingFunction<IOException, String, ExecutionException> errorHandler = Spied.throwingFunction(throwable::throwUnchecked);

                ThrowingSupplier<String, ExecutionException> handling = supplier.onErrorHandleChecked(errorHandler);

                Throwable thrown = assertThrows(throwable.throwableType(), handling::get);
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());

                verify(supplier).get();
                verify(supplier).onErrorHandleChecked(errorHandler);
                verify(errorHandler).apply(any());
                verifyNoMoreInteractions(supplier, errorHandler);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingSupplier<String, IOException> supplier = Spied.throwingSupplier(() -> throwable.throwUnchecked("foo"));

            ThrowingFunction<IOException, String, ExecutionException> errorHandler = Spied.throwingFunction(Exception::getMessage);

            ThrowingSupplier<String, ExecutionException> handling = supplier.onErrorHandleChecked(errorHandler);

            Throwable thrown = assertThrows(throwable.throwableType(), handling::get);
            assertEquals("foo", thrown.getMessage());

            verify(supplier).get();
            verify(supplier).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(supplier, errorHandler);
        }
    }

    @Nested
    class OnErrorHandleUnchecked {

        @Test
        void testNullArgument() {
            ThrowingSupplier<String, IOException> supplier = Spied.throwingSupplier(() -> "foo");

            assertThrows(NullPointerException.class, () -> supplier.onErrorHandleUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingSupplier<String, IOException> supplier = Spied.throwingSupplier(() -> "foo");

            Function<IOException, String> errorHandler = Spied.function(Exception::getMessage);

            Supplier<String> handling = supplier.onErrorHandleUnchecked(errorHandler);

            assertEquals("foo", handling.get());

            verify(supplier).get();
            verify(supplier).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(supplier, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException {
                ThrowingSupplier<String, IOException> supplier = Spied.throwingSupplier(() -> {
                    throw new IOException("foo");
                });

                Function<IOException, String> errorHandler = Spied.function(Exception::getMessage);

                Supplier<String> handling = supplier.onErrorHandleUnchecked(errorHandler);

                assertEquals("foo", handling.get());

                verify(supplier).get();
                verify(supplier).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).apply(any());
                verifyNoMoreInteractions(supplier, errorHandler);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testHandlerThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingSupplier<String, IOException> supplier = Spied.throwingSupplier(() -> {
                    throw new IOException("foo");
                });

                Function<IOException, String> errorHandler = Spied.function(throwable::throwUnchecked);

                Supplier<String> handling = supplier.onErrorHandleUnchecked(errorHandler);

                Throwable thrown = assertThrows(throwable.throwableType(), handling::get);
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());

                verify(supplier).get();
                verify(supplier).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).apply(any());
                verifyNoMoreInteractions(supplier, errorHandler);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingSupplier<String, IOException> supplier = Spied.throwingSupplier(() -> throwable.throwUnchecked("foo"));

            Function<IOException, String> errorHandler = Spied.function(Exception::getMessage);

            Supplier<String> handling = supplier.onErrorHandleUnchecked(errorHandler);

            Throwable thrown = assertThrows(throwable.throwableType(), handling::get);
            assertEquals("foo", thrown.getMessage());

            verify(supplier).get();
            verify(supplier).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(supplier, errorHandler);
        }
    }

    @Nested
    class OnErrorGetChecked {

        @Test
        void testNullArgument() {
            ThrowingSupplier<String, IOException> supplier = Spied.throwingSupplier(() -> "foo");

            assertThrows(NullPointerException.class, () -> supplier.onErrorGetChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingSupplier<String, IOException> supplier = Spied.throwingSupplier(() -> "foo");

            ThrowingSupplier<String, ExecutionException> fallback = Spied.throwingSupplier(() -> "bar");

            ThrowingSupplier<String, ExecutionException> getting = supplier.onErrorGetChecked(fallback);

            assertEquals("foo", getting.get());

            verify(supplier).get();
            verify(supplier).onErrorGetChecked(fallback);
            verifyNoMoreInteractions(supplier, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                ThrowingSupplier<String, IOException> supplier = Spied.throwingSupplier(() -> {
                    throw new IOException("foo");
                });

                ThrowingSupplier<String, ParseException> fallback = Spied.throwingSupplier(() -> "bar");

                ThrowingSupplier<String, ParseException> getting = supplier.onErrorGetChecked(fallback);

                assertEquals("bar", getting.get());

                verify(supplier).get();
                verify(supplier).onErrorGetChecked(fallback);
                verify(fallback).get();
                verifyNoMoreInteractions(supplier, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                ThrowingSupplier<String, IOException> supplier = Spied.throwingSupplier(() -> {
                    throw new IOException("foo");
                });

                ThrowingSupplier<String, ParseException> fallback = Spied.throwingSupplier(() -> {
                    throw new ParseException("bar", 0);
                });

                ThrowingSupplier<String, ParseException> getting = supplier.onErrorGetChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, getting::get);
                assertEquals("bar", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(supplier).get();
                verify(supplier).onErrorGetChecked(fallback);
                verify(fallback).get();
                verifyNoMoreInteractions(supplier, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ParseException {
                ThrowingSupplier<String, IOException> supplier = Spied.throwingSupplier(() -> {
                    throw new IOException("foo");
                });

                ThrowingSupplier<String, ParseException> fallback = Spied.throwingSupplier(() -> throwable.throwUnchecked("bar"));

                ThrowingSupplier<String, ParseException> getting = supplier.onErrorGetChecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), getting::get);
                assertEquals("bar", thrown.getMessage());

                verify(supplier).get();
                verify(supplier).onErrorGetChecked(fallback);
                verify(fallback).get();
                verifyNoMoreInteractions(supplier, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingSupplier<String, IOException> supplier = Spied.throwingSupplier(() -> throwable.throwUnchecked("foo"));

            ThrowingSupplier<String, ParseException> fallback = Spied.throwingSupplier(() -> "bar");

            ThrowingSupplier<String, ParseException> getting = supplier.onErrorGetChecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), getting::get);
            assertEquals("foo", thrown.getMessage());

            verify(supplier).get();
            verify(supplier).onErrorGetChecked(fallback);
            verifyNoMoreInteractions(supplier, fallback);
        }
    }

    @Nested
    class OnErrorGetUnchecked {

        @Test
        void testNullArgument() {
            ThrowingSupplier<String, IOException> supplier = Spied.throwingSupplier(() -> "foo");

            assertThrows(NullPointerException.class, () -> supplier.onErrorGetUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingSupplier<String, IOException> supplier = Spied.throwingSupplier(() -> "foo");

            Supplier<String> fallback = Spied.supplier(() -> "bar");

            Supplier<String> getting = supplier.onErrorGetUnchecked(fallback);

            assertEquals("foo", getting.get());

            verify(supplier).get();
            verify(supplier).onErrorGetUnchecked(fallback);
            verifyNoMoreInteractions(supplier, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                ThrowingSupplier<String, IOException> supplier = Spied.throwingSupplier(() -> {
                    throw new IOException("foo");
                });

                Supplier<String> fallback = Spied.supplier(() -> "bar");

                Supplier<String> getting = supplier.onErrorGetUnchecked(fallback);

                assertEquals("bar", getting.get());

                verify(supplier).get();
                verify(supplier).onErrorGetUnchecked(fallback);
                verify(fallback).get();
                verifyNoMoreInteractions(supplier, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingSupplier<String, IOException> supplier = Spied.throwingSupplier(() -> {
                    throw new IOException("foo");
                });

                Supplier<String> fallback = Spied.supplier(() -> throwable.throwUnchecked("bar"));

                Supplier<String> getting = supplier.onErrorGetUnchecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), getting::get);
                assertEquals("bar", thrown.getMessage());

                verify(supplier).get();
                verify(supplier).onErrorGetUnchecked(fallback);
                verify(fallback).get();
                verifyNoMoreInteractions(supplier, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingSupplier<String, IOException> supplier = Spied.throwingSupplier(() -> throwable.throwUnchecked("foo"));

            Supplier<String> fallback = Spied.supplier(() -> "bar");

            Supplier<String> getting = supplier.onErrorGetUnchecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), getting::get);
            assertEquals("foo", thrown.getMessage());

            verify(supplier).get();
            verify(supplier).onErrorGetUnchecked(fallback);
            verifyNoMoreInteractions(supplier, fallback);
        }
    }

    @Nested
    class OnErrorReturn {

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingSupplier<String, IOException> supplier = Spied.throwingSupplier(() -> "foo");

            Supplier<String> returning = supplier.onErrorReturn("bar");

            assertEquals("foo", returning.get());

            verify(supplier).get();
            verify(supplier).onErrorReturn("bar");
            verifyNoMoreInteractions(supplier);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingSupplier<String, IOException> supplier = Spied.throwingSupplier(() -> {
                throw new IOException("foo");
            });

            Supplier<String> returning = supplier.onErrorReturn("bar");

            assertEquals("bar", returning.get());

            verify(supplier).get();
            verify(supplier).onErrorReturn("bar");
            verifyNoMoreInteractions(supplier);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingSupplier<String, IOException> supplier = Spied.throwingSupplier(() -> throwable.throwUnchecked("foo"));

            Supplier<String> returning = supplier.onErrorReturn("bar");

            Throwable thrown = assertThrows(throwable.throwableType(), returning::get);
            assertEquals("foo", thrown.getMessage());

            verify(supplier).get();
            verify(supplier).onErrorReturn("bar");
            verifyNoMoreInteractions(supplier);
        }
    }

    @Nested
    class Unchecked {

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingSupplier<String, IOException> supplier = Spied.throwingSupplier(() -> "foo");

            Supplier<String> unchecked = supplier.unchecked();

            assertEquals("foo", unchecked.get());

            verify(supplier).get();
            verify(supplier).unchecked();
            verify(supplier).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(supplier);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingSupplier<String, IOException> supplier = Spied.throwingSupplier(() -> {
                throw new IOException("foo");
            });

            Supplier<String> unchecked = supplier.unchecked();

            UncheckedException thrown = assertThrows(UncheckedException.class, unchecked::get);
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo", cause.getMessage());

            verify(supplier).get();
            verify(supplier).unchecked();
            verify(supplier).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(supplier);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingSupplier<String, IOException> supplier = Spied.throwingSupplier(() -> throwable.throwUnchecked("foo"));

            Supplier<String> unchecked = supplier.unchecked();

            Throwable thrown = assertThrows(throwable.throwableType(), unchecked::get);
            assertEquals("foo", thrown.getMessage());

            verify(supplier).get();
            verify(supplier).unchecked();
            verify(supplier).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(supplier);
        }
    }

    @Nested
    class Of {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> ThrowingSupplier.of(null));
        }

        @Test
        void testNonNullArgument() throws IOException {
            ThrowingSupplier<String, IOException> supplier = ThrowingSupplier.of(() -> "foo");

            assertEquals("foo", supplier.get());
        }
    }

    @Nested
    class UncheckedFactory {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> ThrowingSupplier.unchecked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ThrowingSupplier<String, IOException> supplier = Spied.throwingSupplier(() -> "foo");

            Supplier<String> unchecked = ThrowingSupplier.unchecked(supplier);

            assertEquals("foo", unchecked.get());

            verify(supplier).unchecked();
            verify(supplier).onErrorThrowAsUnchecked(any());
            verify(supplier).get();
            verifyNoMoreInteractions(supplier);
        }

        @Test
        void testArgumentThrowsChecked() throws IOException {
            ThrowingSupplier<String, IOException> supplier = Spied.throwingSupplier(() -> {
                throw new IOException("foo");
            });

            Supplier<String> unchecked = ThrowingSupplier.unchecked(supplier);

            UncheckedException thrown = assertThrows(UncheckedException.class, unchecked::get);
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo", cause.getMessage());

            verify(supplier).unchecked();
            verify(supplier).onErrorThrowAsUnchecked(any());
            verify(supplier).get();
            verifyNoMoreInteractions(supplier);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testArgumentThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingSupplier<String, IOException> supplier = Spied.throwingSupplier(() -> throwable.throwUnchecked("foo"));

            Supplier<String> unchecked = ThrowingSupplier.unchecked(supplier);

            Throwable thrown = assertThrows(throwable.throwableType(), unchecked::get);
            assertEquals("foo", thrown.getMessage());

            verify(supplier).unchecked();
            verify(supplier).onErrorThrowAsUnchecked(any());
            verify(supplier).get();
            verifyNoMoreInteractions(supplier);
        }
    }

    @Nested
    class CheckedWrapper {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> ThrowingSupplier.checked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ThrowingSupplier<String, IOException> supplier = ThrowingSupplier.checked(() -> "foo");

            assertEquals("foo", supplier.get());
        }

        @Test
        void testArgumentThrowsUnchecked() {
            ThrowingSupplier<String, IOException> supplier = ThrowingSupplier.checked(() -> {
                throw UncheckedException.withoutStackTrace("foo", new IOException());
            });

            UncheckedException thrown = assertThrows(UncheckedException.class, supplier::get);
            assertEquals("foo", thrown.getMessage());
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertNull(cause.getMessage());
        }
    }

    @Nested
    class CheckedUnwrapper {

        @Test
        void testNullArguments() {
            Supplier<String> supplier = () -> "foo";

            assertThrows(NullPointerException.class, () -> ThrowingSupplier.checked(null, IOException.class));
            assertThrows(NullPointerException.class, () -> ThrowingSupplier.checked(supplier, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ThrowingSupplier<String, IOException> supplier = ThrowingSupplier.checked(() -> "foo", IOException.class);

            assertEquals("foo", supplier.get());
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                ThrowingSupplier<String, IOException> supplier = ThrowingSupplier.checked(() -> {
                    throw UncheckedException.withoutStackTrace(new IOException("foo"));
                }, IOException.class);

                IOException thrown = assertThrows(IOException.class, supplier::get);
                assertEquals("foo", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                ThrowingSupplier<String, IOException> supplier = ThrowingSupplier.checked(() -> {
                    throw UncheckedException.withoutStackTrace(new FileNotFoundException("foo"));
                }, IOException.class);

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class, supplier::get);
                assertEquals("foo", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                ThrowingSupplier<String, IOException> supplier = ThrowingSupplier.checked(() -> {
                    throw UncheckedException.withoutStackTrace(new ParseException("foo", 0));
                }, IOException.class);

                UncheckedException thrown = assertThrows(UncheckedException.class, supplier::get);
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            ThrowingSupplier<String, IOException> supplier = ThrowingSupplier.checked(() -> {
                throw new IllegalStateException("foo");
            }, IOException.class);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, supplier::get);
            assertEquals("foo", thrown.getMessage());
        }
    }
}
