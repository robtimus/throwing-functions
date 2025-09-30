/*
 * CheckedBooleanSupplierTest.java
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.concurrent.ExecutionException;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Predicate;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

@SuppressWarnings("nls")
class CheckedBooleanSupplierTest {

    @Nested
    class OnErrorThrowAsChecked {

        @Test
        void testNullArgument() {
            CheckedBooleanSupplier<IOException> supplier = Spied.checkedBooleanSupplier(() -> true);

            assertThrows(NullPointerException.class, () -> supplier.onErrorThrowAsChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedBooleanSupplier<IOException> supplier = Spied.checkedBooleanSupplier(() -> true);

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedBooleanSupplier<ExecutionException> throwing = supplier.onErrorThrowAsChecked(errorMapper);

            assertTrue(throwing.getAsBoolean());

            verify(supplier).getAsBoolean();
            verify(supplier).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(supplier, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedBooleanSupplier<IOException> supplier = Spied.checkedBooleanSupplier(() -> {
                throw new IOException("foo");
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedBooleanSupplier<ExecutionException> throwing = supplier.onErrorThrowAsChecked(errorMapper);

            ExecutionException thrown = assertThrows(ExecutionException.class, throwing::getAsBoolean);
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo", cause.getMessage());

            verify(supplier).getAsBoolean();
            verify(supplier).onErrorThrowAsChecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(supplier, errorMapper);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            CheckedBooleanSupplier<IOException> supplier = Spied.checkedBooleanSupplier(() -> throwable.throwUnchecked("foo"));

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedBooleanSupplier<ExecutionException> throwing = supplier.onErrorThrowAsChecked(errorMapper);

            Throwable thrown = assertThrows(throwable.throwableType(), throwing::getAsBoolean);
            assertEquals("foo", thrown.getMessage());

            verify(supplier).getAsBoolean();
            verify(supplier).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(supplier, errorMapper);
        }
    }

    @Nested
    class OnErrorThrowAsUnchecked {

        @Test
        void testNullArgument() {
            CheckedBooleanSupplier<IOException> supplier = Spied.checkedBooleanSupplier(() -> true);

            assertThrows(NullPointerException.class, () -> supplier.onErrorThrowAsUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedBooleanSupplier<IOException> supplier = Spied.checkedBooleanSupplier(() -> true);

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            BooleanSupplier throwing = supplier.onErrorThrowAsUnchecked(errorMapper);

            assertTrue(throwing.getAsBoolean());

            verify(supplier).getAsBoolean();
            verify(supplier).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(supplier, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedBooleanSupplier<IOException> supplier = Spied.checkedBooleanSupplier(() -> {
                throw new IOException("foo");
            });

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            BooleanSupplier throwing = supplier.onErrorThrowAsUnchecked(errorMapper);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, throwing::getAsBoolean);
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo", cause.getMessage());

            verify(supplier).getAsBoolean();
            verify(supplier).onErrorThrowAsUnchecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(supplier, errorMapper);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            CheckedBooleanSupplier<IOException> supplier = Spied.checkedBooleanSupplier(() -> throwable.throwUnchecked("foo"));

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            BooleanSupplier throwing = supplier.onErrorThrowAsUnchecked(errorMapper);

            Throwable thrown = assertThrows(throwable.throwableType(), throwing::getAsBoolean);
            assertEquals("foo", thrown.getMessage());

            verify(supplier).getAsBoolean();
            verify(supplier).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(supplier, errorMapper);
        }
    }

    @Nested
    class OnErrorHandleChecked {

        @Test
        void testNullArgument() {
            CheckedBooleanSupplier<IOException> supplier = Spied.checkedBooleanSupplier(() -> true);

            assertThrows(NullPointerException.class, () -> supplier.onErrorHandleChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedBooleanSupplier<IOException> supplier = Spied.checkedBooleanSupplier(() -> true);

            CheckedPredicate<IOException, ExecutionException> errorHandler = Spied.checkedPredicate(e -> e.getMessage() == null);

            CheckedBooleanSupplier<ExecutionException> handling = supplier.onErrorHandleChecked(errorHandler);

            assertTrue(handling.getAsBoolean());

            verify(supplier).getAsBoolean();
            verify(supplier).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(supplier, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException, ExecutionException {
                CheckedBooleanSupplier<IOException> supplier = Spied.checkedBooleanSupplier(() -> {
                    throw new IOException("foo");
                });

                CheckedPredicate<IOException, ExecutionException> errorHandler = Spied.checkedPredicate(e -> e.getMessage() == null);

                CheckedBooleanSupplier<ExecutionException> handling = supplier.onErrorHandleChecked(errorHandler);

                assertFalse(handling.getAsBoolean());

                verify(supplier).getAsBoolean();
                verify(supplier).onErrorHandleChecked(errorHandler);
                verify(errorHandler).test(any());
                verifyNoMoreInteractions(supplier, errorHandler);
            }

            @Test
            void testHandlerThrowsChecked() throws IOException, ExecutionException {
                CheckedBooleanSupplier<IOException> supplier = Spied.checkedBooleanSupplier(() -> {
                    throw new IOException("foo");
                });

                CheckedPredicate<IOException, ExecutionException> errorHandler = Spied.checkedPredicate(e -> {
                    throw new ExecutionException(e);
                });

                CheckedBooleanSupplier<ExecutionException> handling = supplier.onErrorHandleChecked(errorHandler);

                ExecutionException thrown = assertThrows(ExecutionException.class, handling::getAsBoolean);
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());

                verify(supplier).getAsBoolean();
                verify(supplier).onErrorHandleChecked(errorHandler);
                verify(errorHandler).test(any());
                verifyNoMoreInteractions(supplier, errorHandler);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testHandlerThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ExecutionException {
                CheckedBooleanSupplier<IOException> supplier = Spied.checkedBooleanSupplier(() -> {
                    throw new IOException("foo");
                });

                CheckedPredicate<IOException, ExecutionException> errorHandler = Spied.checkedPredicate(throwable::throwUnchecked);

                CheckedBooleanSupplier<ExecutionException> handling = supplier.onErrorHandleChecked(errorHandler);

                Throwable thrown = assertThrows(throwable.throwableType(), handling::getAsBoolean);
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());

                verify(supplier).getAsBoolean();
                verify(supplier).onErrorHandleChecked(errorHandler);
                verify(errorHandler).test(any());
                verifyNoMoreInteractions(supplier, errorHandler);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            CheckedBooleanSupplier<IOException> supplier = Spied.checkedBooleanSupplier(() -> throwable.throwUnchecked("foo"));

            CheckedPredicate<IOException, ExecutionException> errorHandler = Spied.checkedPredicate(e -> e.getMessage() == null);

            CheckedBooleanSupplier<ExecutionException> handling = supplier.onErrorHandleChecked(errorHandler);

            Throwable thrown = assertThrows(throwable.throwableType(), handling::getAsBoolean);
            assertEquals("foo", thrown.getMessage());

            verify(supplier).getAsBoolean();
            verify(supplier).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(supplier, errorHandler);
        }
    }

    @Nested
    class OnErrorHandleUnchecked {

        @Test
        void testNullArgument() {
            CheckedBooleanSupplier<IOException> supplier = Spied.checkedBooleanSupplier(() -> true);

            assertThrows(NullPointerException.class, () -> supplier.onErrorHandleUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedBooleanSupplier<IOException> supplier = Spied.checkedBooleanSupplier(() -> true);

            Predicate<IOException> errorHandler = Spied.predicate(e -> e.getMessage() == null);

            BooleanSupplier handling = supplier.onErrorHandleUnchecked(errorHandler);

            assertTrue(handling.getAsBoolean());

            verify(supplier).getAsBoolean();
            verify(supplier).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(supplier, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException {
                CheckedBooleanSupplier<IOException> supplier = Spied.checkedBooleanSupplier(() -> {
                    throw new IOException("foo");
                });

                Predicate<IOException> errorHandler = Spied.predicate(e -> e.getMessage() == null);

                BooleanSupplier handling = supplier.onErrorHandleUnchecked(errorHandler);

                assertFalse(handling.getAsBoolean());

                verify(supplier).getAsBoolean();
                verify(supplier).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).test(any());
                verifyNoMoreInteractions(supplier, errorHandler);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testHandlerThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                CheckedBooleanSupplier<IOException> supplier = Spied.checkedBooleanSupplier(() -> {
                    throw new IOException("foo");
                });

                Predicate<IOException> errorHandler = Spied.predicate(throwable::throwUnchecked);

                BooleanSupplier handling = supplier.onErrorHandleUnchecked(errorHandler);

                Throwable thrown = assertThrows(throwable.throwableType(), handling::getAsBoolean);
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());

                verify(supplier).getAsBoolean();
                verify(supplier).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).test(any());
                verifyNoMoreInteractions(supplier, errorHandler);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            CheckedBooleanSupplier<IOException> supplier = Spied.checkedBooleanSupplier(() -> throwable.throwUnchecked("foo"));

            Predicate<IOException> errorHandler = Spied.predicate(e -> e.getMessage() == null);

            BooleanSupplier handling = supplier.onErrorHandleUnchecked(errorHandler);

            Throwable thrown = assertThrows(throwable.throwableType(), handling::getAsBoolean);
            assertEquals("foo", thrown.getMessage());

            verify(supplier).getAsBoolean();
            verify(supplier).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(supplier, errorHandler);
        }
    }

    @Nested
    class OnErrorGetCheckedAsBoolean {

        @Test
        void testNullArgument() {
            CheckedBooleanSupplier<IOException> supplier = Spied.checkedBooleanSupplier(() -> true);

            assertThrows(NullPointerException.class, () -> supplier.onErrorGetCheckedAsBoolean(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedBooleanSupplier<IOException> supplier = Spied.checkedBooleanSupplier(() -> true);

            CheckedBooleanSupplier<ExecutionException> fallback = Spied.checkedBooleanSupplier(() -> false);

            CheckedBooleanSupplier<ExecutionException> getting = supplier.onErrorGetCheckedAsBoolean(fallback);

            assertTrue(getting.getAsBoolean());

            verify(supplier).getAsBoolean();
            verify(supplier).onErrorGetCheckedAsBoolean(fallback);
            verifyNoMoreInteractions(supplier, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                CheckedBooleanSupplier<IOException> supplier = Spied.checkedBooleanSupplier(() -> {
                    throw new IOException("foo");
                });

                CheckedBooleanSupplier<ParseException> fallback = Spied.checkedBooleanSupplier(() -> false);

                CheckedBooleanSupplier<ParseException> getting = supplier.onErrorGetCheckedAsBoolean(fallback);

                assertFalse(getting.getAsBoolean());

                verify(supplier).getAsBoolean();
                verify(supplier).onErrorGetCheckedAsBoolean(fallback);
                verify(fallback).getAsBoolean();
                verifyNoMoreInteractions(supplier, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                CheckedBooleanSupplier<IOException> supplier = Spied.checkedBooleanSupplier(() -> {
                    throw new IOException("foo");
                });

                CheckedBooleanSupplier<ParseException> fallback = Spied.checkedBooleanSupplier(() -> {
                    throw new ParseException("bar", 0);
                });

                CheckedBooleanSupplier<ParseException> getting = supplier.onErrorGetCheckedAsBoolean(fallback);

                ParseException thrown = assertThrows(ParseException.class, getting::getAsBoolean);
                assertEquals("bar", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(supplier).getAsBoolean();
                verify(supplier).onErrorGetCheckedAsBoolean(fallback);
                verify(fallback).getAsBoolean();
                verifyNoMoreInteractions(supplier, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ParseException {
                CheckedBooleanSupplier<IOException> supplier = Spied.checkedBooleanSupplier(() -> {
                    throw new IOException("foo");
                });

                CheckedBooleanSupplier<ParseException> fallback = Spied.checkedBooleanSupplier(() -> throwable.throwUnchecked("bar"));

                CheckedBooleanSupplier<ParseException> getting = supplier.onErrorGetCheckedAsBoolean(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), getting::getAsBoolean);
                assertEquals("bar", thrown.getMessage());

                verify(supplier).getAsBoolean();
                verify(supplier).onErrorGetCheckedAsBoolean(fallback);
                verify(fallback).getAsBoolean();
                verifyNoMoreInteractions(supplier, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            CheckedBooleanSupplier<IOException> supplier = Spied.checkedBooleanSupplier(() -> throwable.throwUnchecked("foo"));

            CheckedBooleanSupplier<ParseException> fallback = Spied.checkedBooleanSupplier(() -> false);

            CheckedBooleanSupplier<ParseException> getting = supplier.onErrorGetCheckedAsBoolean(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), getting::getAsBoolean);
            assertEquals("foo", thrown.getMessage());

            verify(supplier).getAsBoolean();
            verify(supplier).onErrorGetCheckedAsBoolean(fallback);
            verifyNoMoreInteractions(supplier, fallback);
        }
    }

    @Nested
    class OnErrorGetUncheckedAsBoolean {

        @Test
        void testNullArgument() {
            CheckedBooleanSupplier<IOException> supplier = Spied.checkedBooleanSupplier(() -> true);

            assertThrows(NullPointerException.class, () -> supplier.onErrorGetUncheckedAsBoolean(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedBooleanSupplier<IOException> supplier = Spied.checkedBooleanSupplier(() -> true);

            BooleanSupplier fallback = Spied.booleanSupplier(() -> false);

            BooleanSupplier getting = supplier.onErrorGetUncheckedAsBoolean(fallback);

            assertTrue(getting.getAsBoolean());

            verify(supplier).getAsBoolean();
            verify(supplier).onErrorGetUncheckedAsBoolean(fallback);
            verifyNoMoreInteractions(supplier, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                CheckedBooleanSupplier<IOException> supplier = Spied.checkedBooleanSupplier(() -> {
                    throw new IOException("foo");
                });

                BooleanSupplier fallback = Spied.booleanSupplier(() -> false);

                BooleanSupplier getting = supplier.onErrorGetUncheckedAsBoolean(fallback);

                assertFalse(getting.getAsBoolean());

                verify(supplier).getAsBoolean();
                verify(supplier).onErrorGetUncheckedAsBoolean(fallback);
                verify(fallback).getAsBoolean();
                verifyNoMoreInteractions(supplier, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                CheckedBooleanSupplier<IOException> supplier = Spied.checkedBooleanSupplier(() -> {
                    throw new IOException("foo");
                });

                BooleanSupplier fallback = Spied.booleanSupplier(() -> throwable.throwUnchecked("bar"));

                BooleanSupplier getting = supplier.onErrorGetUncheckedAsBoolean(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), getting::getAsBoolean);
                assertEquals("bar", thrown.getMessage());

                verify(supplier).getAsBoolean();
                verify(supplier).onErrorGetUncheckedAsBoolean(fallback);
                verify(fallback).getAsBoolean();
                verifyNoMoreInteractions(supplier, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            CheckedBooleanSupplier<IOException> supplier = Spied.checkedBooleanSupplier(() -> throwable.throwUnchecked("foo"));

            BooleanSupplier fallback = Spied.booleanSupplier(() -> false);

            BooleanSupplier getting = supplier.onErrorGetUncheckedAsBoolean(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), getting::getAsBoolean);
            assertEquals("foo", thrown.getMessage());

            verify(supplier).getAsBoolean();
            verify(supplier).onErrorGetUncheckedAsBoolean(fallback);
            verifyNoMoreInteractions(supplier, fallback);
        }
    }

    @Nested
    class OnErrorReturn {

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedBooleanSupplier<IOException> supplier = Spied.checkedBooleanSupplier(() -> true);

            BooleanSupplier returning = supplier.onErrorReturn(false);

            assertTrue(returning.getAsBoolean());

            verify(supplier).getAsBoolean();
            verify(supplier).onErrorReturn(false);
            verifyNoMoreInteractions(supplier);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedBooleanSupplier<IOException> supplier = Spied.checkedBooleanSupplier(() -> {
                throw new IOException("foo");
            });

            BooleanSupplier returning = supplier.onErrorReturn(false);

            assertFalse(returning.getAsBoolean());

            verify(supplier).getAsBoolean();
            verify(supplier).onErrorReturn(false);
            verifyNoMoreInteractions(supplier);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            CheckedBooleanSupplier<IOException> supplier = Spied.checkedBooleanSupplier(() -> throwable.throwUnchecked("foo"));

            BooleanSupplier returning = supplier.onErrorReturn(false);

            Throwable thrown = assertThrows(throwable.throwableType(), returning::getAsBoolean);
            assertEquals("foo", thrown.getMessage());

            verify(supplier).getAsBoolean();
            verify(supplier).onErrorReturn(false);
            verifyNoMoreInteractions(supplier);
        }
    }

    @Nested
    class Unchecked {

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedBooleanSupplier<IOException> supplier = Spied.checkedBooleanSupplier(() -> true);

            BooleanSupplier unchecked = supplier.unchecked();

            assertTrue(unchecked.getAsBoolean());

            verify(supplier).getAsBoolean();
            verify(supplier).unchecked();
            verify(supplier).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(supplier);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedBooleanSupplier<IOException> supplier = Spied.checkedBooleanSupplier(() -> {
                throw new IOException("foo");
            });

            BooleanSupplier unchecked = supplier.unchecked();

            UncheckedException thrown = assertThrows(UncheckedException.class, unchecked::getAsBoolean);
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo", cause.getMessage());

            verify(supplier).getAsBoolean();
            verify(supplier).unchecked();
            verify(supplier).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(supplier);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            CheckedBooleanSupplier<IOException> supplier = Spied.checkedBooleanSupplier(() -> throwable.throwUnchecked("foo"));

            BooleanSupplier unchecked = supplier.unchecked();

            Throwable thrown = assertThrows(throwable.throwableType(), unchecked::getAsBoolean);
            assertEquals("foo", thrown.getMessage());

            verify(supplier).getAsBoolean();
            verify(supplier).unchecked();
            verify(supplier).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(supplier);
        }
    }

    @Nested
    class Of {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> CheckedBooleanSupplier.of(null));
        }

        @Test
        void testNonNullArgument() throws IOException {
            CheckedBooleanSupplier<IOException> supplier = CheckedBooleanSupplier.of(() -> true);

            assertTrue(supplier.getAsBoolean());
        }
    }

    @Nested
    class UncheckedFactory {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> CheckedBooleanSupplier.unchecked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedBooleanSupplier<IOException> supplier = Spied.checkedBooleanSupplier(() -> true);

            BooleanSupplier unchecked = CheckedBooleanSupplier.unchecked(supplier);

            assertTrue(unchecked.getAsBoolean());

            verify(supplier).unchecked();
            verify(supplier).onErrorThrowAsUnchecked(any());
            verify(supplier).getAsBoolean();
            verifyNoMoreInteractions(supplier);
        }

        @Test
        void testArgumentThrowsChecked() throws IOException {
            CheckedBooleanSupplier<IOException> supplier = Spied.checkedBooleanSupplier(() -> {
                throw new IOException("foo");
            });

            BooleanSupplier unchecked = CheckedBooleanSupplier.unchecked(supplier);

            UncheckedException thrown = assertThrows(UncheckedException.class, unchecked::getAsBoolean);
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo", cause.getMessage());

            verify(supplier).unchecked();
            verify(supplier).onErrorThrowAsUnchecked(any());
            verify(supplier).getAsBoolean();
            verifyNoMoreInteractions(supplier);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testArgumentThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            CheckedBooleanSupplier<IOException> supplier = Spied.checkedBooleanSupplier(() -> throwable.throwUnchecked("foo"));

            BooleanSupplier unchecked = CheckedBooleanSupplier.unchecked(supplier);

            Throwable thrown = assertThrows(throwable.throwableType(), unchecked::getAsBoolean);
            assertEquals("foo", thrown.getMessage());

            verify(supplier).unchecked();
            verify(supplier).onErrorThrowAsUnchecked(any());
            verify(supplier).getAsBoolean();
            verifyNoMoreInteractions(supplier);
        }
    }

    @Nested
    class CheckedWrapper {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> CheckedBooleanSupplier.checked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedBooleanSupplier<IOException> supplier = CheckedBooleanSupplier.checked(() -> true);

            assertTrue(supplier.getAsBoolean());
        }

        @Test
        void testArgumentThrowsUnchecked() {
            CheckedBooleanSupplier<IOException> supplier = CheckedBooleanSupplier.checked(() -> {
                throw new UncheckedException("foo", new IOException());
            });

            UncheckedException thrown = assertThrows(UncheckedException.class, supplier::getAsBoolean);
            assertEquals("foo", thrown.getMessage());
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertNull(cause.getMessage());
        }
    }

    @Nested
    class CheckedUnwrapper {

        @Test
        void testNullArguments() {
            BooleanSupplier supplier = () -> true;

            assertThrows(NullPointerException.class, () -> CheckedBooleanSupplier.checked(null, IOException.class));
            assertThrows(NullPointerException.class, () -> CheckedBooleanSupplier.checked(supplier, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedBooleanSupplier<IOException> supplier = CheckedBooleanSupplier.checked(() -> true, IOException.class);

            assertTrue(supplier.getAsBoolean());
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                CheckedBooleanSupplier<IOException> supplier = CheckedBooleanSupplier.checked(() -> {
                    throw new UncheckedException(new IOException("foo"));
                }, IOException.class);

                IOException thrown = assertThrows(IOException.class, supplier::getAsBoolean);
                assertEquals("foo", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                CheckedBooleanSupplier<IOException> supplier = CheckedBooleanSupplier.checked(() -> {
                    throw new UncheckedException(new FileNotFoundException("foo"));
                }, IOException.class);

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class, supplier::getAsBoolean);
                assertEquals("foo", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                CheckedBooleanSupplier<IOException> supplier = CheckedBooleanSupplier.checked(() -> {
                    throw new UncheckedException(new ParseException("foo", 0));
                }, IOException.class);

                UncheckedException thrown = assertThrows(UncheckedException.class, supplier::getAsBoolean);
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            CheckedBooleanSupplier<IOException> supplier = CheckedBooleanSupplier.checked(() -> {
                throw new IllegalStateException("foo");
            }, IOException.class);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, supplier::getAsBoolean);
            assertEquals("foo", thrown.getMessage());
        }
    }

    @Nested
    class InvokeAndUnwrap {

        @Test
        void testNullArguments() {
            BooleanSupplier supplier = () -> true;

            assertThrows(NullPointerException.class, () -> CheckedBooleanSupplier.invokeAndUnwrap(null, IOException.class));
            assertThrows(NullPointerException.class, () -> CheckedBooleanSupplier.invokeAndUnwrap(supplier, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            BooleanSupplier supplier = () -> true;

            assertTrue(CheckedBooleanSupplier.invokeAndUnwrap(supplier, IOException.class));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                BooleanSupplier supplier = () -> {
                    throw new UncheckedException(new IOException("foo"));
                };

                IOException thrown = assertThrows(IOException.class, () -> CheckedBooleanSupplier.invokeAndUnwrap(supplier, IOException.class));
                assertEquals("foo", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                BooleanSupplier supplier = () -> {
                    throw new UncheckedException(new FileNotFoundException("foo"));
                };

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class,
                        () -> CheckedBooleanSupplier.invokeAndUnwrap(supplier, IOException.class));
                assertEquals("foo", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                BooleanSupplier supplier = () -> {
                    throw new UncheckedException(new ParseException("foo", 0));
                };

                UncheckedException thrown = assertThrows(UncheckedException.class,
                        () -> CheckedBooleanSupplier.invokeAndUnwrap(supplier, IOException.class));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            BooleanSupplier supplier = () -> {
                throw new IllegalStateException("foo");
            };

            IllegalStateException thrown = assertThrows(IllegalStateException.class,
                    () -> CheckedBooleanSupplier.invokeAndUnwrap(supplier, IOException.class));
            assertEquals("foo", thrown.getMessage());
        }
    }
}
