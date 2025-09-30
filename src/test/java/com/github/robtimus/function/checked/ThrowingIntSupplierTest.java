/*
 * ThrowingIntSupplierTest.java
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
import java.util.function.Function;
import java.util.function.IntSupplier;
import java.util.function.ToIntFunction;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

@SuppressWarnings("nls")
class ThrowingIntSupplierTest {

    @Nested
    class OnErrorThrowAsChecked {

        @Test
        void testNullArgument() {
            ThrowingIntSupplier<IOException> supplier = Spied.checkedIntSupplier(() -> 1);

            assertThrows(NullPointerException.class, () -> supplier.onErrorThrowAsChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingIntSupplier<IOException> supplier = Spied.checkedIntSupplier(() -> 1);

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingIntSupplier<ExecutionException> throwing = supplier.onErrorThrowAsChecked(errorMapper);

            assertEquals(1, throwing.getAsInt());

            verify(supplier).getAsInt();
            verify(supplier).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(supplier, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingIntSupplier<IOException> supplier = Spied.checkedIntSupplier(() -> {
                throw new IOException("foo");
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingIntSupplier<ExecutionException> throwing = supplier.onErrorThrowAsChecked(errorMapper);

            ExecutionException thrown = assertThrows(ExecutionException.class, throwing::getAsInt);
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo", cause.getMessage());

            verify(supplier).getAsInt();
            verify(supplier).onErrorThrowAsChecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(supplier, errorMapper);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingIntSupplier<IOException> supplier = Spied.checkedIntSupplier(() -> throwable.throwUnchecked("foo"));

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingIntSupplier<ExecutionException> throwing = supplier.onErrorThrowAsChecked(errorMapper);

            Throwable thrown = assertThrows(throwable.throwableType(), throwing::getAsInt);
            assertEquals("foo", thrown.getMessage());

            verify(supplier).getAsInt();
            verify(supplier).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(supplier, errorMapper);
        }
    }

    @Nested
    class OnErrorThrowAsUnchecked {

        @Test
        void testNullArgument() {
            ThrowingIntSupplier<IOException> supplier = Spied.checkedIntSupplier(() -> 1);

            assertThrows(NullPointerException.class, () -> supplier.onErrorThrowAsUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingIntSupplier<IOException> supplier = Spied.checkedIntSupplier(() -> 1);

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            IntSupplier throwing = supplier.onErrorThrowAsUnchecked(errorMapper);

            assertEquals(1, throwing.getAsInt());

            verify(supplier).getAsInt();
            verify(supplier).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(supplier, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingIntSupplier<IOException> supplier = Spied.checkedIntSupplier(() -> {
                throw new IOException("foo");
            });

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            IntSupplier throwing = supplier.onErrorThrowAsUnchecked(errorMapper);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, throwing::getAsInt);
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo", cause.getMessage());

            verify(supplier).getAsInt();
            verify(supplier).onErrorThrowAsUnchecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(supplier, errorMapper);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingIntSupplier<IOException> supplier = Spied.checkedIntSupplier(() -> throwable.throwUnchecked("foo"));

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            IntSupplier throwing = supplier.onErrorThrowAsUnchecked(errorMapper);

            Throwable thrown = assertThrows(throwable.throwableType(), throwing::getAsInt);
            assertEquals("foo", thrown.getMessage());

            verify(supplier).getAsInt();
            verify(supplier).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(supplier, errorMapper);
        }
    }

    @Nested
    class OnErrorHandleChecked {

        @Test
        void testNullArgument() {
            ThrowingIntSupplier<IOException> supplier = Spied.checkedIntSupplier(() -> 1);

            assertThrows(NullPointerException.class, () -> supplier.onErrorHandleChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingIntSupplier<IOException> supplier = Spied.checkedIntSupplier(() -> 1);

            ThrowingToIntFunction<IOException, ExecutionException> errorHandler = Spied.checkedToIntFunction(e -> 0);

            ThrowingIntSupplier<ExecutionException> handling = supplier.onErrorHandleChecked(errorHandler);

            assertEquals(1, handling.getAsInt());

            verify(supplier).getAsInt();
            verify(supplier).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(supplier, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException, ExecutionException {
                ThrowingIntSupplier<IOException> supplier = Spied.checkedIntSupplier(() -> {
                    throw new IOException("foo");
                });

                ThrowingToIntFunction<IOException, ExecutionException> errorHandler = Spied.checkedToIntFunction(e -> 0);

                ThrowingIntSupplier<ExecutionException> handling = supplier.onErrorHandleChecked(errorHandler);

                assertEquals(0, handling.getAsInt());

                verify(supplier).getAsInt();
                verify(supplier).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsInt(any());
                verifyNoMoreInteractions(supplier, errorHandler);
            }

            @Test
            void testHandlerThrowsChecked() throws IOException, ExecutionException {
                ThrowingIntSupplier<IOException> supplier = Spied.checkedIntSupplier(() -> {
                    throw new IOException("foo");
                });

                ThrowingToIntFunction<IOException, ExecutionException> errorHandler = Spied.checkedToIntFunction(e -> {
                    throw new ExecutionException(e);
                });

                ThrowingIntSupplier<ExecutionException> handling = supplier.onErrorHandleChecked(errorHandler);

                ExecutionException thrown = assertThrows(ExecutionException.class, handling::getAsInt);
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());

                verify(supplier).getAsInt();
                verify(supplier).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsInt(any());
                verifyNoMoreInteractions(supplier, errorHandler);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testHandlerThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ExecutionException {
                ThrowingIntSupplier<IOException> supplier = Spied.checkedIntSupplier(() -> {
                    throw new IOException("foo");
                });

                ThrowingToIntFunction<IOException, ExecutionException> errorHandler = Spied.checkedToIntFunction(throwable::throwUnchecked);

                ThrowingIntSupplier<ExecutionException> handling = supplier.onErrorHandleChecked(errorHandler);

                Throwable thrown = assertThrows(throwable.throwableType(), handling::getAsInt);
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());

                verify(supplier).getAsInt();
                verify(supplier).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsInt(any());
                verifyNoMoreInteractions(supplier, errorHandler);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingIntSupplier<IOException> supplier = Spied.checkedIntSupplier(() -> throwable.throwUnchecked("foo"));

            ThrowingToIntFunction<IOException, ExecutionException> errorHandler = Spied.checkedToIntFunction(e -> 0);

            ThrowingIntSupplier<ExecutionException> handling = supplier.onErrorHandleChecked(errorHandler);

            Throwable thrown = assertThrows(throwable.throwableType(), handling::getAsInt);
            assertEquals("foo", thrown.getMessage());

            verify(supplier).getAsInt();
            verify(supplier).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(supplier, errorHandler);
        }
    }

    @Nested
    class OnErrorHandleUnchecked {

        @Test
        void testNullArgument() {
            ThrowingIntSupplier<IOException> supplier = Spied.checkedIntSupplier(() -> 1);

            assertThrows(NullPointerException.class, () -> supplier.onErrorHandleUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingIntSupplier<IOException> supplier = Spied.checkedIntSupplier(() -> 1);

            ToIntFunction<IOException> errorHandler = Spied.toIntFunction(e -> 0);

            IntSupplier handling = supplier.onErrorHandleUnchecked(errorHandler);

            assertEquals(1, handling.getAsInt());

            verify(supplier).getAsInt();
            verify(supplier).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(supplier, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException {
                ThrowingIntSupplier<IOException> supplier = Spied.checkedIntSupplier(() -> {
                    throw new IOException("foo");
                });

                ToIntFunction<IOException> errorHandler = Spied.toIntFunction(e -> 0);

                IntSupplier handling = supplier.onErrorHandleUnchecked(errorHandler);

                assertEquals(0, handling.getAsInt());

                verify(supplier).getAsInt();
                verify(supplier).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).applyAsInt(any());
                verifyNoMoreInteractions(supplier, errorHandler);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testHandlerThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingIntSupplier<IOException> supplier = Spied.checkedIntSupplier(() -> {
                    throw new IOException("foo");
                });

                ToIntFunction<IOException> errorHandler = Spied.toIntFunction(throwable::throwUnchecked);

                IntSupplier handling = supplier.onErrorHandleUnchecked(errorHandler);

                Throwable thrown = assertThrows(throwable.throwableType(), handling::getAsInt);
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());

                verify(supplier).getAsInt();
                verify(supplier).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).applyAsInt(any());
                verifyNoMoreInteractions(supplier, errorHandler);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingIntSupplier<IOException> supplier = Spied.checkedIntSupplier(() -> throwable.throwUnchecked("foo"));

            ToIntFunction<IOException> errorHandler = Spied.toIntFunction(e -> 0);

            IntSupplier handling = supplier.onErrorHandleUnchecked(errorHandler);

            Throwable thrown = assertThrows(throwable.throwableType(), handling::getAsInt);
            assertEquals("foo", thrown.getMessage());

            verify(supplier).getAsInt();
            verify(supplier).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(supplier, errorHandler);
        }
    }

    @Nested
    class OnErrorGetCheckedAsInt {

        @Test
        void testNullArgument() {
            ThrowingIntSupplier<IOException> supplier = Spied.checkedIntSupplier(() -> 1);

            assertThrows(NullPointerException.class, () -> supplier.onErrorGetCheckedAsInt(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingIntSupplier<IOException> supplier = Spied.checkedIntSupplier(() -> 1);

            ThrowingIntSupplier<ExecutionException> fallback = Spied.checkedIntSupplier(() -> 0);

            ThrowingIntSupplier<ExecutionException> getting = supplier.onErrorGetCheckedAsInt(fallback);

            assertEquals(1, getting.getAsInt());

            verify(supplier).getAsInt();
            verify(supplier).onErrorGetCheckedAsInt(fallback);
            verifyNoMoreInteractions(supplier, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                ThrowingIntSupplier<IOException> supplier = Spied.checkedIntSupplier(() -> {
                    throw new IOException("foo");
                });

                ThrowingIntSupplier<ParseException> fallback = Spied.checkedIntSupplier(() -> 0);

                ThrowingIntSupplier<ParseException> getting = supplier.onErrorGetCheckedAsInt(fallback);

                assertEquals(0, getting.getAsInt());

                verify(supplier).getAsInt();
                verify(supplier).onErrorGetCheckedAsInt(fallback);
                verify(fallback).getAsInt();
                verifyNoMoreInteractions(supplier, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                ThrowingIntSupplier<IOException> supplier = Spied.checkedIntSupplier(() -> {
                    throw new IOException("foo");
                });

                ThrowingIntSupplier<ParseException> fallback = Spied.checkedIntSupplier(() -> {
                    throw new ParseException("bar", 0);
                });

                ThrowingIntSupplier<ParseException> getting = supplier.onErrorGetCheckedAsInt(fallback);

                ParseException thrown = assertThrows(ParseException.class, getting::getAsInt);
                assertEquals("bar", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(supplier).getAsInt();
                verify(supplier).onErrorGetCheckedAsInt(fallback);
                verify(fallback).getAsInt();
                verifyNoMoreInteractions(supplier, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ParseException {
                ThrowingIntSupplier<IOException> supplier = Spied.checkedIntSupplier(() -> {
                    throw new IOException("foo");
                });

                ThrowingIntSupplier<ParseException> fallback = Spied.checkedIntSupplier(() -> throwable.throwUnchecked("bar"));

                ThrowingIntSupplier<ParseException> getting = supplier.onErrorGetCheckedAsInt(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), getting::getAsInt);
                assertEquals("bar", thrown.getMessage());

                verify(supplier).getAsInt();
                verify(supplier).onErrorGetCheckedAsInt(fallback);
                verify(fallback).getAsInt();
                verifyNoMoreInteractions(supplier, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingIntSupplier<IOException> supplier = Spied.checkedIntSupplier(() -> throwable.throwUnchecked("foo"));

            ThrowingIntSupplier<ParseException> fallback = Spied.checkedIntSupplier(() -> 0);

            ThrowingIntSupplier<ParseException> getting = supplier.onErrorGetCheckedAsInt(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), getting::getAsInt);
            assertEquals("foo", thrown.getMessage());

            verify(supplier).getAsInt();
            verify(supplier).onErrorGetCheckedAsInt(fallback);
            verifyNoMoreInteractions(supplier, fallback);
        }
    }

    @Nested
    class OnErrorGetUncheckedAsInt {

        @Test
        void testNullArgument() {
            ThrowingIntSupplier<IOException> supplier = Spied.checkedIntSupplier(() -> 1);

            assertThrows(NullPointerException.class, () -> supplier.onErrorGetUncheckedAsInt(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingIntSupplier<IOException> supplier = Spied.checkedIntSupplier(() -> 1);

            IntSupplier fallback = Spied.intSupplier(() -> 0);

            IntSupplier getting = supplier.onErrorGetUncheckedAsInt(fallback);

            assertEquals(1, getting.getAsInt());

            verify(supplier).getAsInt();
            verify(supplier).onErrorGetUncheckedAsInt(fallback);
            verifyNoMoreInteractions(supplier, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                ThrowingIntSupplier<IOException> supplier = Spied.checkedIntSupplier(() -> {
                    throw new IOException("foo");
                });

                IntSupplier fallback = Spied.intSupplier(() -> 0);

                IntSupplier getting = supplier.onErrorGetUncheckedAsInt(fallback);

                assertEquals(0, getting.getAsInt());

                verify(supplier).getAsInt();
                verify(supplier).onErrorGetUncheckedAsInt(fallback);
                verify(fallback).getAsInt();
                verifyNoMoreInteractions(supplier, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingIntSupplier<IOException> supplier = Spied.checkedIntSupplier(() -> {
                    throw new IOException("foo");
                });

                IntSupplier fallback = Spied.intSupplier(() -> throwable.throwUnchecked("bar"));

                IntSupplier getting = supplier.onErrorGetUncheckedAsInt(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), getting::getAsInt);
                assertEquals("bar", thrown.getMessage());

                verify(supplier).getAsInt();
                verify(supplier).onErrorGetUncheckedAsInt(fallback);
                verify(fallback).getAsInt();
                verifyNoMoreInteractions(supplier, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingIntSupplier<IOException> supplier = Spied.checkedIntSupplier(() -> throwable.throwUnchecked("foo"));

            IntSupplier fallback = Spied.intSupplier(() -> 0);

            IntSupplier getting = supplier.onErrorGetUncheckedAsInt(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), getting::getAsInt);
            assertEquals("foo", thrown.getMessage());

            verify(supplier).getAsInt();
            verify(supplier).onErrorGetUncheckedAsInt(fallback);
            verifyNoMoreInteractions(supplier, fallback);
        }
    }

    @Nested
    class OnErrorReturn {

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingIntSupplier<IOException> supplier = Spied.checkedIntSupplier(() -> 1);

            IntSupplier returning = supplier.onErrorReturn(0);

            assertEquals(1, returning.getAsInt());

            verify(supplier).getAsInt();
            verify(supplier).onErrorReturn(0);
            verifyNoMoreInteractions(supplier);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingIntSupplier<IOException> supplier = Spied.checkedIntSupplier(() -> {
                throw new IOException("foo");
            });

            IntSupplier returning = supplier.onErrorReturn(0);

            assertEquals(0, returning.getAsInt());

            verify(supplier).getAsInt();
            verify(supplier).onErrorReturn(0);
            verifyNoMoreInteractions(supplier);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingIntSupplier<IOException> supplier = Spied.checkedIntSupplier(() -> throwable.throwUnchecked("foo"));

            IntSupplier returning = supplier.onErrorReturn(0);

            Throwable thrown = assertThrows(throwable.throwableType(), returning::getAsInt);
            assertEquals("foo", thrown.getMessage());

            verify(supplier).getAsInt();
            verify(supplier).onErrorReturn(0);
            verifyNoMoreInteractions(supplier);
        }
    }

    @Nested
    class Unchecked {

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingIntSupplier<IOException> supplier = Spied.checkedIntSupplier(() -> 1);

            IntSupplier unchecked = supplier.unchecked();

            assertEquals(1, unchecked.getAsInt());

            verify(supplier).getAsInt();
            verify(supplier).unchecked();
            verify(supplier).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(supplier);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingIntSupplier<IOException> supplier = Spied.checkedIntSupplier(() -> {
                throw new IOException("foo");
            });

            IntSupplier unchecked = supplier.unchecked();

            UncheckedException thrown = assertThrows(UncheckedException.class, unchecked::getAsInt);
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo", cause.getMessage());

            verify(supplier).getAsInt();
            verify(supplier).unchecked();
            verify(supplier).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(supplier);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingIntSupplier<IOException> supplier = Spied.checkedIntSupplier(() -> throwable.throwUnchecked("foo"));

            IntSupplier unchecked = supplier.unchecked();

            Throwable thrown = assertThrows(throwable.throwableType(), unchecked::getAsInt);
            assertEquals("foo", thrown.getMessage());

            verify(supplier).getAsInt();
            verify(supplier).unchecked();
            verify(supplier).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(supplier);
        }
    }

    @Nested
    class Of {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> ThrowingIntSupplier.of(null));
        }

        @Test
        void testNonNullArgument() throws IOException {
            ThrowingIntSupplier<IOException> supplier = ThrowingIntSupplier.of(() -> 1);

            assertEquals(1, supplier.getAsInt());
        }
    }

    @Nested
    class UncheckedFactory {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> ThrowingIntSupplier.unchecked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ThrowingIntSupplier<IOException> supplier = Spied.checkedIntSupplier(() -> 1);

            IntSupplier unchecked = ThrowingIntSupplier.unchecked(supplier);

            assertEquals(1, unchecked.getAsInt());

            verify(supplier).unchecked();
            verify(supplier).onErrorThrowAsUnchecked(any());
            verify(supplier).getAsInt();
            verifyNoMoreInteractions(supplier);
        }

        @Test
        void testArgumentThrowsChecked() throws IOException {
            ThrowingIntSupplier<IOException> supplier = Spied.checkedIntSupplier(() -> {
                throw new IOException("foo");
            });

            IntSupplier unchecked = ThrowingIntSupplier.unchecked(supplier);

            UncheckedException thrown = assertThrows(UncheckedException.class, unchecked::getAsInt);
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo", cause.getMessage());

            verify(supplier).unchecked();
            verify(supplier).onErrorThrowAsUnchecked(any());
            verify(supplier).getAsInt();
            verifyNoMoreInteractions(supplier);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testArgumentThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingIntSupplier<IOException> supplier = Spied.checkedIntSupplier(() -> throwable.throwUnchecked("foo"));

            IntSupplier unchecked = ThrowingIntSupplier.unchecked(supplier);

            Throwable thrown = assertThrows(throwable.throwableType(), unchecked::getAsInt);
            assertEquals("foo", thrown.getMessage());

            verify(supplier).unchecked();
            verify(supplier).onErrorThrowAsUnchecked(any());
            verify(supplier).getAsInt();
            verifyNoMoreInteractions(supplier);
        }
    }

    @Nested
    class CheckedWrapper {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> ThrowingIntSupplier.checked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ThrowingIntSupplier<IOException> supplier = ThrowingIntSupplier.checked(() -> 1);

            assertEquals(1, supplier.getAsInt());
        }

        @Test
        void testArgumentThrowsUnchecked() {
            ThrowingIntSupplier<IOException> supplier = ThrowingIntSupplier.checked(() -> {
                throw new UncheckedException("foo", new IOException());
            });

            UncheckedException thrown = assertThrows(UncheckedException.class, supplier::getAsInt);
            assertEquals("foo", thrown.getMessage());
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertNull(cause.getMessage());
        }
    }

    @Nested
    class CheckedUnwrapper {

        @Test
        void testNullArguments() {
            IntSupplier supplier = () -> 1;

            assertThrows(NullPointerException.class, () -> ThrowingIntSupplier.checked(null, IOException.class));
            assertThrows(NullPointerException.class, () -> ThrowingIntSupplier.checked(supplier, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ThrowingIntSupplier<IOException> supplier = ThrowingIntSupplier.checked(() -> 1, IOException.class);

            assertEquals(1, supplier.getAsInt());
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                ThrowingIntSupplier<IOException> supplier = ThrowingIntSupplier.checked(() -> {
                    throw new UncheckedException(new IOException("foo"));
                }, IOException.class);

                IOException thrown = assertThrows(IOException.class, supplier::getAsInt);
                assertEquals("foo", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                ThrowingIntSupplier<IOException> supplier = ThrowingIntSupplier.checked(() -> {
                    throw new UncheckedException(new FileNotFoundException("foo"));
                }, IOException.class);

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class, supplier::getAsInt);
                assertEquals("foo", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                ThrowingIntSupplier<IOException> supplier = ThrowingIntSupplier.checked(() -> {
                    throw new UncheckedException(new ParseException("foo", 0));
                }, IOException.class);

                UncheckedException thrown = assertThrows(UncheckedException.class, supplier::getAsInt);
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            ThrowingIntSupplier<IOException> supplier = ThrowingIntSupplier.checked(() -> {
                throw new IllegalStateException("foo");
            }, IOException.class);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, supplier::getAsInt);
            assertEquals("foo", thrown.getMessage());
        }
    }

    @Nested
    class InvokeAndUnwrap {

        @Test
        void testNullArguments() {
            IntSupplier supplier = () -> 1;

            assertThrows(NullPointerException.class, () -> ThrowingIntSupplier.invokeAndUnwrap(null, IOException.class));
            assertThrows(NullPointerException.class, () -> ThrowingIntSupplier.invokeAndUnwrap(supplier, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            IntSupplier supplier = () -> 1;

            assertEquals(1, ThrowingIntSupplier.invokeAndUnwrap(supplier, IOException.class));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                IntSupplier supplier = () -> {
                    throw new UncheckedException(new IOException("foo"));
                };

                IOException thrown = assertThrows(IOException.class, () -> ThrowingIntSupplier.invokeAndUnwrap(supplier, IOException.class));
                assertEquals("foo", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                IntSupplier supplier = () -> {
                    throw new UncheckedException(new FileNotFoundException("foo"));
                };

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class,
                        () -> ThrowingIntSupplier.invokeAndUnwrap(supplier, IOException.class));
                assertEquals("foo", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                IntSupplier supplier = () -> {
                    throw new UncheckedException(new ParseException("foo", 0));
                };

                UncheckedException thrown = assertThrows(UncheckedException.class,
                        () -> ThrowingIntSupplier.invokeAndUnwrap(supplier, IOException.class));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            IntSupplier supplier = () -> {
                throw new IllegalStateException("foo");
            };

            IllegalStateException thrown = assertThrows(IllegalStateException.class,
                    () -> ThrowingIntSupplier.invokeAndUnwrap(supplier, IOException.class));
            assertEquals("foo", thrown.getMessage());
        }
    }
}
