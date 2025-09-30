/*
 * ThrowingDoubleSupplierTest.java
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

package com.github.robtimus.function.checked;

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
import java.util.function.ToDoubleFunction;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

@SuppressWarnings("nls")
class ThrowingDoubleSupplierTest {

    @Nested
    class OnErrorThrowAsChecked {

        @Test
        void testNullArgument() {
            ThrowingDoubleSupplier<IOException> supplier = Spied.checkedDoubleSupplier(() -> 1D);

            assertThrows(NullPointerException.class, () -> supplier.onErrorThrowAsChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingDoubleSupplier<IOException> supplier = Spied.checkedDoubleSupplier(() -> 1D);

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingDoubleSupplier<ExecutionException> throwing = supplier.onErrorThrowAsChecked(errorMapper);

            assertEquals(1D, throwing.getAsDouble());

            verify(supplier).getAsDouble();
            verify(supplier).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(supplier, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingDoubleSupplier<IOException> supplier = Spied.checkedDoubleSupplier(() -> {
                throw new IOException("foo");
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingDoubleSupplier<ExecutionException> throwing = supplier.onErrorThrowAsChecked(errorMapper);

            ExecutionException thrown = assertThrows(ExecutionException.class, throwing::getAsDouble);
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo", cause.getMessage());

            verify(supplier).getAsDouble();
            verify(supplier).onErrorThrowAsChecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(supplier, errorMapper);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingDoubleSupplier<IOException> supplier = Spied.checkedDoubleSupplier(() -> throwable.throwUnchecked("foo"));

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingDoubleSupplier<ExecutionException> throwing = supplier.onErrorThrowAsChecked(errorMapper);

            Throwable thrown = assertThrows(throwable.throwableType(), throwing::getAsDouble);
            assertEquals("foo", thrown.getMessage());

            verify(supplier).getAsDouble();
            verify(supplier).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(supplier, errorMapper);
        }
    }

    @Nested
    class OnErrorThrowAsUnchecked {

        @Test
        void testNullArgument() {
            ThrowingDoubleSupplier<IOException> supplier = Spied.checkedDoubleSupplier(() -> 1D);

            assertThrows(NullPointerException.class, () -> supplier.onErrorThrowAsUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingDoubleSupplier<IOException> supplier = Spied.checkedDoubleSupplier(() -> 1D);

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            DoubleSupplier throwing = supplier.onErrorThrowAsUnchecked(errorMapper);

            assertEquals(1D, throwing.getAsDouble());

            verify(supplier).getAsDouble();
            verify(supplier).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(supplier, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingDoubleSupplier<IOException> supplier = Spied.checkedDoubleSupplier(() -> {
                throw new IOException("foo");
            });

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            DoubleSupplier throwing = supplier.onErrorThrowAsUnchecked(errorMapper);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, throwing::getAsDouble);
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo", cause.getMessage());

            verify(supplier).getAsDouble();
            verify(supplier).onErrorThrowAsUnchecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(supplier, errorMapper);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingDoubleSupplier<IOException> supplier = Spied.checkedDoubleSupplier(() -> throwable.throwUnchecked("foo"));

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            DoubleSupplier throwing = supplier.onErrorThrowAsUnchecked(errorMapper);

            Throwable thrown = assertThrows(throwable.throwableType(), throwing::getAsDouble);
            assertEquals("foo", thrown.getMessage());

            verify(supplier).getAsDouble();
            verify(supplier).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(supplier, errorMapper);
        }
    }

    @Nested
    class OnErrorHandleChecked {

        @Test
        void testNullArgument() {
            ThrowingDoubleSupplier<IOException> supplier = Spied.checkedDoubleSupplier(() -> 1D);

            assertThrows(NullPointerException.class, () -> supplier.onErrorHandleChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingDoubleSupplier<IOException> supplier = Spied.checkedDoubleSupplier(() -> 1D);

            ThrowingToDoubleFunction<IOException, ExecutionException> errorHandler = Spied.checkedToDoubleFunction(e -> 0D);

            ThrowingDoubleSupplier<ExecutionException> handling = supplier.onErrorHandleChecked(errorHandler);

            assertEquals(1D, handling.getAsDouble());

            verify(supplier).getAsDouble();
            verify(supplier).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(supplier, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException, ExecutionException {
                ThrowingDoubleSupplier<IOException> supplier = Spied.checkedDoubleSupplier(() -> {
                    throw new IOException("foo");
                });

                ThrowingToDoubleFunction<IOException, ExecutionException> errorHandler = Spied.checkedToDoubleFunction(e -> 0D);

                ThrowingDoubleSupplier<ExecutionException> handling = supplier.onErrorHandleChecked(errorHandler);

                assertEquals(0D, handling.getAsDouble());

                verify(supplier).getAsDouble();
                verify(supplier).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsDouble(any());
                verifyNoMoreInteractions(supplier, errorHandler);
            }

            @Test
            void testHandlerThrowsChecked() throws IOException, ExecutionException {
                ThrowingDoubleSupplier<IOException> supplier = Spied.checkedDoubleSupplier(() -> {
                    throw new IOException("foo");
                });

                ThrowingToDoubleFunction<IOException, ExecutionException> errorHandler = Spied.checkedToDoubleFunction(e -> {
                    throw new ExecutionException(e);
                });

                ThrowingDoubleSupplier<ExecutionException> handling = supplier.onErrorHandleChecked(errorHandler);

                ExecutionException thrown = assertThrows(ExecutionException.class, handling::getAsDouble);
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());

                verify(supplier).getAsDouble();
                verify(supplier).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsDouble(any());
                verifyNoMoreInteractions(supplier, errorHandler);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testHandlerThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ExecutionException {
                ThrowingDoubleSupplier<IOException> supplier = Spied.checkedDoubleSupplier(() -> {
                    throw new IOException("foo");
                });

                ThrowingToDoubleFunction<IOException, ExecutionException> errorHandler = Spied.checkedToDoubleFunction(throwable::throwUnchecked);

                ThrowingDoubleSupplier<ExecutionException> handling = supplier.onErrorHandleChecked(errorHandler);

                Throwable thrown = assertThrows(throwable.throwableType(), handling::getAsDouble);
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());

                verify(supplier).getAsDouble();
                verify(supplier).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsDouble(any());
                verifyNoMoreInteractions(supplier, errorHandler);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingDoubleSupplier<IOException> supplier = Spied.checkedDoubleSupplier(() -> throwable.throwUnchecked("foo"));

            ThrowingToDoubleFunction<IOException, ExecutionException> errorHandler = Spied.checkedToDoubleFunction(e -> 0D);

            ThrowingDoubleSupplier<ExecutionException> handling = supplier.onErrorHandleChecked(errorHandler);

            Throwable thrown = assertThrows(throwable.throwableType(), handling::getAsDouble);
            assertEquals("foo", thrown.getMessage());

            verify(supplier).getAsDouble();
            verify(supplier).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(supplier, errorHandler);
        }
    }

    @Nested
    class OnErrorHandleUnchecked {

        @Test
        void testNullArgument() {
            ThrowingDoubleSupplier<IOException> supplier = Spied.checkedDoubleSupplier(() -> 1D);

            assertThrows(NullPointerException.class, () -> supplier.onErrorHandleUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingDoubleSupplier<IOException> supplier = Spied.checkedDoubleSupplier(() -> 1D);

            ToDoubleFunction<IOException> errorHandler = Spied.toDoubleFunction(e -> 0D);

            DoubleSupplier handling = supplier.onErrorHandleUnchecked(errorHandler);

            assertEquals(1D, handling.getAsDouble());

            verify(supplier).getAsDouble();
            verify(supplier).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(supplier, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException {
                ThrowingDoubleSupplier<IOException> supplier = Spied.checkedDoubleSupplier(() -> {
                    throw new IOException("foo");
                });

                ToDoubleFunction<IOException> errorHandler = Spied.toDoubleFunction(e -> 0D);

                DoubleSupplier handling = supplier.onErrorHandleUnchecked(errorHandler);

                assertEquals(0D, handling.getAsDouble());

                verify(supplier).getAsDouble();
                verify(supplier).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).applyAsDouble(any());
                verifyNoMoreInteractions(supplier, errorHandler);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testHandlerThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingDoubleSupplier<IOException> supplier = Spied.checkedDoubleSupplier(() -> {
                    throw new IOException("foo");
                });

                ToDoubleFunction<IOException> errorHandler = Spied.toDoubleFunction(throwable::throwUnchecked);

                DoubleSupplier handling = supplier.onErrorHandleUnchecked(errorHandler);

                Throwable thrown = assertThrows(throwable.throwableType(), handling::getAsDouble);
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());

                verify(supplier).getAsDouble();
                verify(supplier).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).applyAsDouble(any());
                verifyNoMoreInteractions(supplier, errorHandler);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingDoubleSupplier<IOException> supplier = Spied.checkedDoubleSupplier(() -> throwable.throwUnchecked("foo"));

            ToDoubleFunction<IOException> errorHandler = Spied.toDoubleFunction(e -> 0D);

            DoubleSupplier handling = supplier.onErrorHandleUnchecked(errorHandler);

            Throwable thrown = assertThrows(throwable.throwableType(), handling::getAsDouble);
            assertEquals("foo", thrown.getMessage());

            verify(supplier).getAsDouble();
            verify(supplier).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(supplier, errorHandler);
        }
    }

    @Nested
    class OnErrorGetCheckedAsDouble {

        @Test
        void testNullArgument() {
            ThrowingDoubleSupplier<IOException> supplier = Spied.checkedDoubleSupplier(() -> 1D);

            assertThrows(NullPointerException.class, () -> supplier.onErrorGetCheckedAsDouble(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingDoubleSupplier<IOException> supplier = Spied.checkedDoubleSupplier(() -> 1D);

            ThrowingDoubleSupplier<ExecutionException> fallback = Spied.checkedDoubleSupplier(() -> 0D);

            ThrowingDoubleSupplier<ExecutionException> getting = supplier.onErrorGetCheckedAsDouble(fallback);

            assertEquals(1D, getting.getAsDouble());

            verify(supplier).getAsDouble();
            verify(supplier).onErrorGetCheckedAsDouble(fallback);
            verifyNoMoreInteractions(supplier, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                ThrowingDoubleSupplier<IOException> supplier = Spied.checkedDoubleSupplier(() -> {
                    throw new IOException("foo");
                });

                ThrowingDoubleSupplier<ParseException> fallback = Spied.checkedDoubleSupplier(() -> 0D);

                ThrowingDoubleSupplier<ParseException> getting = supplier.onErrorGetCheckedAsDouble(fallback);

                assertEquals(0D, getting.getAsDouble());

                verify(supplier).getAsDouble();
                verify(supplier).onErrorGetCheckedAsDouble(fallback);
                verify(fallback).getAsDouble();
                verifyNoMoreInteractions(supplier, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                ThrowingDoubleSupplier<IOException> supplier = Spied.checkedDoubleSupplier(() -> {
                    throw new IOException("foo");
                });

                ThrowingDoubleSupplier<ParseException> fallback = Spied.checkedDoubleSupplier(() -> {
                    throw new ParseException("bar", 0);
                });

                ThrowingDoubleSupplier<ParseException> getting = supplier.onErrorGetCheckedAsDouble(fallback);

                ParseException thrown = assertThrows(ParseException.class, getting::getAsDouble);
                assertEquals("bar", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(supplier).getAsDouble();
                verify(supplier).onErrorGetCheckedAsDouble(fallback);
                verify(fallback).getAsDouble();
                verifyNoMoreInteractions(supplier, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ParseException {
                ThrowingDoubleSupplier<IOException> supplier = Spied.checkedDoubleSupplier(() -> {
                    throw new IOException("foo");
                });

                ThrowingDoubleSupplier<ParseException> fallback = Spied.checkedDoubleSupplier(() -> throwable.throwUnchecked("bar"));

                ThrowingDoubleSupplier<ParseException> getting = supplier.onErrorGetCheckedAsDouble(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), getting::getAsDouble);
                assertEquals("bar", thrown.getMessage());

                verify(supplier).getAsDouble();
                verify(supplier).onErrorGetCheckedAsDouble(fallback);
                verify(fallback).getAsDouble();
                verifyNoMoreInteractions(supplier, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingDoubleSupplier<IOException> supplier = Spied.checkedDoubleSupplier(() -> throwable.throwUnchecked("foo"));

            ThrowingDoubleSupplier<ParseException> fallback = Spied.checkedDoubleSupplier(() -> 0D);

            ThrowingDoubleSupplier<ParseException> getting = supplier.onErrorGetCheckedAsDouble(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), getting::getAsDouble);
            assertEquals("foo", thrown.getMessage());

            verify(supplier).getAsDouble();
            verify(supplier).onErrorGetCheckedAsDouble(fallback);
            verifyNoMoreInteractions(supplier, fallback);
        }
    }

    @Nested
    class OnErrorGetUncheckedAsDouble {

        @Test
        void testNullArgument() {
            ThrowingDoubleSupplier<IOException> supplier = Spied.checkedDoubleSupplier(() -> 1D);

            assertThrows(NullPointerException.class, () -> supplier.onErrorGetUncheckedAsDouble(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingDoubleSupplier<IOException> supplier = Spied.checkedDoubleSupplier(() -> 1D);

            DoubleSupplier fallback = Spied.doubleSupplier(() -> 0D);

            DoubleSupplier getting = supplier.onErrorGetUncheckedAsDouble(fallback);

            assertEquals(1D, getting.getAsDouble());

            verify(supplier).getAsDouble();
            verify(supplier).onErrorGetUncheckedAsDouble(fallback);
            verifyNoMoreInteractions(supplier, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                ThrowingDoubleSupplier<IOException> supplier = Spied.checkedDoubleSupplier(() -> {
                    throw new IOException("foo");
                });

                DoubleSupplier fallback = Spied.doubleSupplier(() -> 0D);

                DoubleSupplier getting = supplier.onErrorGetUncheckedAsDouble(fallback);

                assertEquals(0D, getting.getAsDouble());

                verify(supplier).getAsDouble();
                verify(supplier).onErrorGetUncheckedAsDouble(fallback);
                verify(fallback).getAsDouble();
                verifyNoMoreInteractions(supplier, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingDoubleSupplier<IOException> supplier = Spied.checkedDoubleSupplier(() -> {
                    throw new IOException("foo");
                });

                DoubleSupplier fallback = Spied.doubleSupplier(() -> throwable.throwUnchecked("bar"));

                DoubleSupplier getting = supplier.onErrorGetUncheckedAsDouble(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), getting::getAsDouble);
                assertEquals("bar", thrown.getMessage());

                verify(supplier).getAsDouble();
                verify(supplier).onErrorGetUncheckedAsDouble(fallback);
                verify(fallback).getAsDouble();
                verifyNoMoreInteractions(supplier, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingDoubleSupplier<IOException> supplier = Spied.checkedDoubleSupplier(() -> throwable.throwUnchecked("foo"));

            DoubleSupplier fallback = Spied.doubleSupplier(() -> 0D);

            DoubleSupplier getting = supplier.onErrorGetUncheckedAsDouble(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), getting::getAsDouble);
            assertEquals("foo", thrown.getMessage());

            verify(supplier).getAsDouble();
            verify(supplier).onErrorGetUncheckedAsDouble(fallback);
            verifyNoMoreInteractions(supplier, fallback);
        }
    }

    @Nested
    class OnErrorReturn {

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingDoubleSupplier<IOException> supplier = Spied.checkedDoubleSupplier(() -> 1D);

            DoubleSupplier returning = supplier.onErrorReturn(0D);

            assertEquals(1D, returning.getAsDouble());

            verify(supplier).getAsDouble();
            verify(supplier).onErrorReturn(0D);
            verifyNoMoreInteractions(supplier);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingDoubleSupplier<IOException> supplier = Spied.checkedDoubleSupplier(() -> {
                throw new IOException("foo");
            });

            DoubleSupplier returning = supplier.onErrorReturn(0D);

            assertEquals(0D, returning.getAsDouble());

            verify(supplier).getAsDouble();
            verify(supplier).onErrorReturn(0D);
            verifyNoMoreInteractions(supplier);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingDoubleSupplier<IOException> supplier = Spied.checkedDoubleSupplier(() -> throwable.throwUnchecked("foo"));

            DoubleSupplier returning = supplier.onErrorReturn(0D);

            Throwable thrown = assertThrows(throwable.throwableType(), returning::getAsDouble);
            assertEquals("foo", thrown.getMessage());

            verify(supplier).getAsDouble();
            verify(supplier).onErrorReturn(0D);
            verifyNoMoreInteractions(supplier);
        }
    }

    @Nested
    class Unchecked {

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingDoubleSupplier<IOException> supplier = Spied.checkedDoubleSupplier(() -> 1D);

            DoubleSupplier unchecked = supplier.unchecked();

            assertEquals(1D, unchecked.getAsDouble());

            verify(supplier).getAsDouble();
            verify(supplier).unchecked();
            verify(supplier).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(supplier);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingDoubleSupplier<IOException> supplier = Spied.checkedDoubleSupplier(() -> {
                throw new IOException("foo");
            });

            DoubleSupplier unchecked = supplier.unchecked();

            UncheckedException thrown = assertThrows(UncheckedException.class, unchecked::getAsDouble);
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo", cause.getMessage());

            verify(supplier).getAsDouble();
            verify(supplier).unchecked();
            verify(supplier).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(supplier);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingDoubleSupplier<IOException> supplier = Spied.checkedDoubleSupplier(() -> throwable.throwUnchecked("foo"));

            DoubleSupplier unchecked = supplier.unchecked();

            Throwable thrown = assertThrows(throwable.throwableType(), unchecked::getAsDouble);
            assertEquals("foo", thrown.getMessage());

            verify(supplier).getAsDouble();
            verify(supplier).unchecked();
            verify(supplier).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(supplier);
        }
    }

    @Nested
    class Of {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> ThrowingDoubleSupplier.of(null));
        }

        @Test
        void testNonNullArgument() throws IOException {
            ThrowingDoubleSupplier<IOException> supplier = ThrowingDoubleSupplier.of(() -> 1D);

            assertEquals(1D, supplier.getAsDouble());
        }
    }

    @Nested
    class UncheckedFactory {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> ThrowingDoubleSupplier.unchecked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ThrowingDoubleSupplier<IOException> supplier = Spied.checkedDoubleSupplier(() -> 1D);

            DoubleSupplier unchecked = ThrowingDoubleSupplier.unchecked(supplier);

            assertEquals(1D, unchecked.getAsDouble());

            verify(supplier).unchecked();
            verify(supplier).onErrorThrowAsUnchecked(any());
            verify(supplier).getAsDouble();
            verifyNoMoreInteractions(supplier);
        }

        @Test
        void testArgumentThrowsChecked() throws IOException {
            ThrowingDoubleSupplier<IOException> supplier = Spied.checkedDoubleSupplier(() -> {
                throw new IOException("foo");
            });

            DoubleSupplier unchecked = ThrowingDoubleSupplier.unchecked(supplier);

            UncheckedException thrown = assertThrows(UncheckedException.class, unchecked::getAsDouble);
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo", cause.getMessage());

            verify(supplier).unchecked();
            verify(supplier).onErrorThrowAsUnchecked(any());
            verify(supplier).getAsDouble();
            verifyNoMoreInteractions(supplier);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testArgumentThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingDoubleSupplier<IOException> supplier = Spied.checkedDoubleSupplier(() -> throwable.throwUnchecked("foo"));

            DoubleSupplier unchecked = ThrowingDoubleSupplier.unchecked(supplier);

            Throwable thrown = assertThrows(throwable.throwableType(), unchecked::getAsDouble);
            assertEquals("foo", thrown.getMessage());

            verify(supplier).unchecked();
            verify(supplier).onErrorThrowAsUnchecked(any());
            verify(supplier).getAsDouble();
            verifyNoMoreInteractions(supplier);
        }
    }

    @Nested
    class CheckedWrapper {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> ThrowingDoubleSupplier.checked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ThrowingDoubleSupplier<IOException> supplier = ThrowingDoubleSupplier.checked(() -> 1D);

            assertEquals(1D, supplier.getAsDouble());
        }

        @Test
        void testArgumentThrowsUnchecked() {
            ThrowingDoubleSupplier<IOException> supplier = ThrowingDoubleSupplier.checked(() -> {
                throw new UncheckedException("foo", new IOException());
            });

            UncheckedException thrown = assertThrows(UncheckedException.class, supplier::getAsDouble);
            assertEquals("foo", thrown.getMessage());
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertNull(cause.getMessage());
        }
    }

    @Nested
    class CheckedUnwrapper {

        @Test
        void testNullArguments() {
            DoubleSupplier supplier = () -> 1D;

            assertThrows(NullPointerException.class, () -> ThrowingDoubleSupplier.checked(null, IOException.class));
            assertThrows(NullPointerException.class, () -> ThrowingDoubleSupplier.checked(supplier, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ThrowingDoubleSupplier<IOException> supplier = ThrowingDoubleSupplier.checked(() -> 1D, IOException.class);

            assertEquals(1D, supplier.getAsDouble());
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                ThrowingDoubleSupplier<IOException> supplier = ThrowingDoubleSupplier.checked(() -> {
                    throw new UncheckedException(new IOException("foo"));
                }, IOException.class);

                IOException thrown = assertThrows(IOException.class, supplier::getAsDouble);
                assertEquals("foo", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                ThrowingDoubleSupplier<IOException> supplier = ThrowingDoubleSupplier.checked(() -> {
                    throw new UncheckedException(new FileNotFoundException("foo"));
                }, IOException.class);

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class, supplier::getAsDouble);
                assertEquals("foo", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                ThrowingDoubleSupplier<IOException> supplier = ThrowingDoubleSupplier.checked(() -> {
                    throw new UncheckedException(new ParseException("foo", 0));
                }, IOException.class);

                UncheckedException thrown = assertThrows(UncheckedException.class, supplier::getAsDouble);
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            ThrowingDoubleSupplier<IOException> supplier = ThrowingDoubleSupplier.checked(() -> {
                throw new IllegalStateException("foo");
            }, IOException.class);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, supplier::getAsDouble);
            assertEquals("foo", thrown.getMessage());
        }
    }

    @Nested
    class InvokeAndUnwrap {

        @Test
        void testNullArguments() {
            DoubleSupplier supplier = () -> 1D;

            assertThrows(NullPointerException.class, () -> ThrowingDoubleSupplier.invokeAndUnwrap(null, IOException.class));
            assertThrows(NullPointerException.class, () -> ThrowingDoubleSupplier.invokeAndUnwrap(supplier, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            DoubleSupplier supplier = () -> 1D;

            assertEquals(1D, ThrowingDoubleSupplier.invokeAndUnwrap(supplier, IOException.class));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                DoubleSupplier supplier = () -> {
                    throw new UncheckedException(new IOException("foo"));
                };

                IOException thrown = assertThrows(IOException.class, () -> ThrowingDoubleSupplier.invokeAndUnwrap(supplier, IOException.class));
                assertEquals("foo", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                DoubleSupplier supplier = () -> {
                    throw new UncheckedException(new FileNotFoundException("foo"));
                };

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class,
                        () -> ThrowingDoubleSupplier.invokeAndUnwrap(supplier, IOException.class));
                assertEquals("foo", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                DoubleSupplier supplier = () -> {
                    throw new UncheckedException(new ParseException("foo", 0));
                };

                UncheckedException thrown = assertThrows(UncheckedException.class,
                        () -> ThrowingDoubleSupplier.invokeAndUnwrap(supplier, IOException.class));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            DoubleSupplier supplier = () -> {
                throw new IllegalStateException("foo");
            };

            IllegalStateException thrown = assertThrows(IllegalStateException.class,
                    () -> ThrowingDoubleSupplier.invokeAndUnwrap(supplier, IOException.class));
            assertEquals("foo", thrown.getMessage());
        }
    }
}
