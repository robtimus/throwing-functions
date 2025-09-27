/*
 * CheckedLongSupplierTest.java
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
import java.util.function.LongSupplier;
import java.util.function.ToLongFunction;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("nls")
class CheckedLongSupplierTest {

    @Nested
    class OnErrorThrowAsChecked {

        @Test
        void testNullArgument() {
            CheckedLongSupplier<IOException> supplier = Spied.checkedLongSupplier(() -> 1L);

            assertThrows(NullPointerException.class, () -> supplier.onErrorThrowAsChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedLongSupplier<IOException> supplier = Spied.checkedLongSupplier(() -> 1L);

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedLongSupplier<ExecutionException> throwing = supplier.onErrorThrowAsChecked(errorMapper);

            assertEquals(1L, throwing.getAsLong());

            verify(supplier).getAsLong();
            verify(supplier).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(supplier, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedLongSupplier<IOException> supplier = Spied.checkedLongSupplier(() -> {
                throw new IOException("foo");
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedLongSupplier<ExecutionException> throwing = supplier.onErrorThrowAsChecked(errorMapper);

            ExecutionException thrown = assertThrows(ExecutionException.class, throwing::getAsLong);
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo", cause.getMessage());

            verify(supplier).getAsLong();
            verify(supplier).onErrorThrowAsChecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(supplier, errorMapper);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedLongSupplier<IOException> supplier = Spied.checkedLongSupplier(() -> {
                throw new IllegalStateException("foo");
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedLongSupplier<ExecutionException> throwing = supplier.onErrorThrowAsChecked(errorMapper);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, throwing::getAsLong);
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
            CheckedLongSupplier<IOException> supplier = Spied.checkedLongSupplier(() -> 1L);

            assertThrows(NullPointerException.class, () -> supplier.onErrorThrowAsUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedLongSupplier<IOException> supplier = Spied.checkedLongSupplier(() -> 1L);

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            LongSupplier throwing = supplier.onErrorThrowAsUnchecked(errorMapper);

            assertEquals(1L, throwing.getAsLong());

            verify(supplier).getAsLong();
            verify(supplier).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(supplier, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedLongSupplier<IOException> supplier = Spied.checkedLongSupplier(() -> {
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

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedLongSupplier<IOException> supplier = Spied.checkedLongSupplier(() -> {
                throw new IllegalArgumentException("foo");
            });

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            LongSupplier throwing = supplier.onErrorThrowAsUnchecked(errorMapper);

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, throwing::getAsLong);
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
            CheckedLongSupplier<IOException> supplier = Spied.checkedLongSupplier(() -> 1L);

            assertThrows(NullPointerException.class, () -> supplier.onErrorHandleChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedLongSupplier<IOException> supplier = Spied.checkedLongSupplier(() -> 1L);

            CheckedToLongFunction<IOException, ExecutionException> errorHandler = Spied.checkedToLongFunction(e -> 0L);

            CheckedLongSupplier<ExecutionException> handling = supplier.onErrorHandleChecked(errorHandler);

            assertEquals(1L, handling.getAsLong());

            verify(supplier).getAsLong();
            verify(supplier).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(supplier, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException, ExecutionException {
                CheckedLongSupplier<IOException> supplier = Spied.checkedLongSupplier(() -> {
                    throw new IOException("foo");
                });

                CheckedToLongFunction<IOException, ExecutionException> errorHandler = Spied.checkedToLongFunction(e -> 0L);

                CheckedLongSupplier<ExecutionException> handling = supplier.onErrorHandleChecked(errorHandler);

                assertEquals(0L, handling.getAsLong());

                verify(supplier).getAsLong();
                verify(supplier).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsLong(any());
                verifyNoMoreInteractions(supplier, errorHandler);
            }

            @Test
            void testHandlerThrowsChecked() throws IOException, ExecutionException {
                CheckedLongSupplier<IOException> supplier = Spied.checkedLongSupplier(() -> {
                    throw new IOException("foo");
                });

                CheckedToLongFunction<IOException, ExecutionException> errorHandler = Spied.checkedToLongFunction(e -> {
                    throw new ExecutionException(e);
                });

                CheckedLongSupplier<ExecutionException> handling = supplier.onErrorHandleChecked(errorHandler);

                ExecutionException thrown = assertThrows(ExecutionException.class, handling::getAsLong);
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());

                verify(supplier).getAsLong();
                verify(supplier).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsLong(any());
                verifyNoMoreInteractions(supplier, errorHandler);
            }

            @Test
            void testHandlerThrowsUnchecked() throws IOException, ExecutionException {
                CheckedLongSupplier<IOException> supplier = Spied.checkedLongSupplier(() -> {
                    throw new IOException("foo");
                });

                CheckedToLongFunction<IOException, ExecutionException> errorHandler = Spied.checkedToLongFunction(e -> {
                    throw new IllegalStateException(e);
                });

                CheckedLongSupplier<ExecutionException> handling = supplier.onErrorHandleChecked(errorHandler);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, handling::getAsLong);
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());

                verify(supplier).getAsLong();
                verify(supplier).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsLong(any());
                verifyNoMoreInteractions(supplier, errorHandler);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedLongSupplier<IOException> supplier = Spied.checkedLongSupplier(() -> {
                throw new IllegalStateException("foo");
            });

            CheckedToLongFunction<IOException, ExecutionException> errorHandler = Spied.checkedToLongFunction(e -> 0L);

            CheckedLongSupplier<ExecutionException> handling = supplier.onErrorHandleChecked(errorHandler);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, handling::getAsLong);
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
            CheckedLongSupplier<IOException> supplier = Spied.checkedLongSupplier(() -> 1L);

            assertThrows(NullPointerException.class, () -> supplier.onErrorHandleUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedLongSupplier<IOException> supplier = Spied.checkedLongSupplier(() -> 1L);

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
                CheckedLongSupplier<IOException> supplier = Spied.checkedLongSupplier(() -> {
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

            @Test
            void testHandlerThrowsUnchecked() throws IOException {
                CheckedLongSupplier<IOException> supplier = Spied.checkedLongSupplier(() -> {
                    throw new IOException("foo");
                });

                ToLongFunction<IOException> errorHandler = Spied.toLongFunction(e -> {
                    throw new IllegalStateException(e);
                });

                LongSupplier handling = supplier.onErrorHandleUnchecked(errorHandler);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, handling::getAsLong);
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());

                verify(supplier).getAsLong();
                verify(supplier).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).applyAsLong(any());
                verifyNoMoreInteractions(supplier, errorHandler);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedLongSupplier<IOException> supplier = Spied.checkedLongSupplier(() -> {
                throw new IllegalStateException("foo");
            });

            ToLongFunction<IOException> errorHandler = Spied.toLongFunction(e -> 0L);

            LongSupplier handling = supplier.onErrorHandleUnchecked(errorHandler);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, handling::getAsLong);
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
            CheckedLongSupplier<IOException> supplier = Spied.checkedLongSupplier(() -> 1L);

            assertThrows(NullPointerException.class, () -> supplier.onErrorGetCheckedAsLong(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedLongSupplier<IOException> supplier = Spied.checkedLongSupplier(() -> 1L);

            CheckedLongSupplier<ExecutionException> fallback = Spied.checkedLongSupplier(() -> 0L);

            CheckedLongSupplier<ExecutionException> getting = supplier.onErrorGetCheckedAsLong(fallback);

            assertEquals(1L, getting.getAsLong());

            verify(supplier).getAsLong();
            verify(supplier).onErrorGetCheckedAsLong(fallback);
            verifyNoMoreInteractions(supplier, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                CheckedLongSupplier<IOException> supplier = Spied.checkedLongSupplier(() -> {
                    throw new IOException("foo");
                });

                CheckedLongSupplier<ParseException> fallback = Spied.checkedLongSupplier(() -> 0L);

                CheckedLongSupplier<ParseException> getting = supplier.onErrorGetCheckedAsLong(fallback);

                assertEquals(0L, getting.getAsLong());

                verify(supplier).getAsLong();
                verify(supplier).onErrorGetCheckedAsLong(fallback);
                verify(fallback).getAsLong();
                verifyNoMoreInteractions(supplier, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                CheckedLongSupplier<IOException> supplier = Spied.checkedLongSupplier(() -> {
                    throw new IOException("foo");
                });

                CheckedLongSupplier<ParseException> fallback = Spied.checkedLongSupplier(() -> {
                    throw new ParseException("bar", 0);
                });

                CheckedLongSupplier<ParseException> getting = supplier.onErrorGetCheckedAsLong(fallback);

                ParseException thrown = assertThrows(ParseException.class, getting::getAsLong);
                assertEquals("bar", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(supplier).getAsLong();
                verify(supplier).onErrorGetCheckedAsLong(fallback);
                verify(fallback).getAsLong();
                verifyNoMoreInteractions(supplier, fallback);
            }

            @Test
            void testFallbackThrowsUnchecked() throws IOException, ParseException {
                CheckedLongSupplier<IOException> supplier = Spied.checkedLongSupplier(() -> {
                    throw new IOException("foo");
                });

                CheckedLongSupplier<ParseException> fallback = Spied.checkedLongSupplier(() -> {
                    throw new IllegalStateException("bar");
                });

                CheckedLongSupplier<ParseException> getting = supplier.onErrorGetCheckedAsLong(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, getting::getAsLong);
                assertEquals("bar", thrown.getMessage());

                verify(supplier).getAsLong();
                verify(supplier).onErrorGetCheckedAsLong(fallback);
                verify(fallback).getAsLong();
                verifyNoMoreInteractions(supplier, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedLongSupplier<IOException> supplier = Spied.checkedLongSupplier(() -> {
                throw new IllegalStateException("foo");
            });

            CheckedLongSupplier<ParseException> fallback = Spied.checkedLongSupplier(() -> 0L);

            CheckedLongSupplier<ParseException> getting = supplier.onErrorGetCheckedAsLong(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, getting::getAsLong);
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
            CheckedLongSupplier<IOException> supplier = Spied.checkedLongSupplier(() -> 1L);

            assertThrows(NullPointerException.class, () -> supplier.onErrorGetUncheckedAsLong(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedLongSupplier<IOException> supplier = Spied.checkedLongSupplier(() -> 1L);

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
                CheckedLongSupplier<IOException> supplier = Spied.checkedLongSupplier(() -> {
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

            @Test
            void testFallbackThrowsUnchecked() throws IOException {
                CheckedLongSupplier<IOException> supplier = Spied.checkedLongSupplier(() -> {
                    throw new IOException("foo");
                });

                LongSupplier fallback = Spied.longSupplier(() -> {
                    throw new IllegalStateException("bar");
                });

                LongSupplier getting = supplier.onErrorGetUncheckedAsLong(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, getting::getAsLong);
                assertEquals("bar", thrown.getMessage());

                verify(supplier).getAsLong();
                verify(supplier).onErrorGetUncheckedAsLong(fallback);
                verify(fallback).getAsLong();
                verifyNoMoreInteractions(supplier, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedLongSupplier<IOException> supplier = Spied.checkedLongSupplier(() -> {
                throw new IllegalStateException("foo");
            });

            LongSupplier fallback = Spied.longSupplier(() -> 0L);

            LongSupplier getting = supplier.onErrorGetUncheckedAsLong(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, getting::getAsLong);
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
            CheckedLongSupplier<IOException> supplier = Spied.checkedLongSupplier(() -> 1L);

            LongSupplier returning = supplier.onErrorReturn(0L);

            assertEquals(1L, returning.getAsLong());

            verify(supplier).getAsLong();
            verify(supplier).onErrorReturn(0L);
            verifyNoMoreInteractions(supplier);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedLongSupplier<IOException> supplier = Spied.checkedLongSupplier(() -> {
                throw new IOException("foo");
            });

            LongSupplier returning = supplier.onErrorReturn(0L);

            assertEquals(0L, returning.getAsLong());

            verify(supplier).getAsLong();
            verify(supplier).onErrorReturn(0L);
            verifyNoMoreInteractions(supplier);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedLongSupplier<IOException> supplier = Spied.checkedLongSupplier(() -> {
                throw new IllegalStateException("foo");
            });

            LongSupplier returning = supplier.onErrorReturn(0L);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, returning::getAsLong);
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
            CheckedLongSupplier<IOException> supplier = Spied.checkedLongSupplier(() -> 1L);

            LongSupplier unchecked = supplier.unchecked();

            assertEquals(1L, unchecked.getAsLong());

            verify(supplier).getAsLong();
            verify(supplier).unchecked();
            verify(supplier).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(supplier);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedLongSupplier<IOException> supplier = Spied.checkedLongSupplier(() -> {
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

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedLongSupplier<IOException> supplier = Spied.checkedLongSupplier(() -> {
                throw new IllegalArgumentException("foo");
            });

            LongSupplier unchecked = supplier.unchecked();

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, unchecked::getAsLong);
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
            assertThrows(NullPointerException.class, () -> CheckedLongSupplier.of(null));
        }

        @Test
        void testNonNullArgument() throws IOException {
            CheckedLongSupplier<IOException> supplier = CheckedLongSupplier.of(() -> 1L);

            assertEquals(1L, supplier.getAsLong());
        }
    }

    @Nested
    class UncheckedFactory {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> CheckedLongSupplier.unchecked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedLongSupplier<IOException> supplier = Spied.checkedLongSupplier(() -> 1L);

            LongSupplier unchecked = CheckedLongSupplier.unchecked(supplier);

            assertEquals(1L, unchecked.getAsLong());

            verify(supplier).unchecked();
            verify(supplier).onErrorThrowAsUnchecked(any());
            verify(supplier).getAsLong();
            verifyNoMoreInteractions(supplier);
        }

        @Test
        void testArgumentThrowsChecked() throws IOException {
            CheckedLongSupplier<IOException> supplier = Spied.checkedLongSupplier(() -> {
                throw new IOException("foo");
            });

            LongSupplier unchecked = CheckedLongSupplier.unchecked(supplier);

            UncheckedException thrown = assertThrows(UncheckedException.class, unchecked::getAsLong);
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo", cause.getMessage());

            verify(supplier).unchecked();
            verify(supplier).onErrorThrowAsUnchecked(any());
            verify(supplier).getAsLong();
            verifyNoMoreInteractions(supplier);
        }

        @Test
        void testArgumentThrowsUnchecked() throws IOException {
            CheckedLongSupplier<IOException> supplier = Spied.checkedLongSupplier(() -> {
                throw new IllegalArgumentException("foo");
            });

            LongSupplier unchecked = CheckedLongSupplier.unchecked(supplier);

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, unchecked::getAsLong);
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
            assertThrows(NullPointerException.class, () -> CheckedLongSupplier.checked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedLongSupplier<IOException> supplier = CheckedLongSupplier.checked(() -> 1L);

            assertEquals(1L, supplier.getAsLong());
        }

        @Test
        void testArgumentThrowsUnchecked() {
            CheckedLongSupplier<IOException> supplier = CheckedLongSupplier.checked(() -> {
                throw new UncheckedException("foo", new IOException());
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

            assertThrows(NullPointerException.class, () -> CheckedLongSupplier.checked(null, IOException.class));
            assertThrows(NullPointerException.class, () -> CheckedLongSupplier.checked(supplier, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedLongSupplier<IOException> supplier = CheckedLongSupplier.checked(() -> 1L, IOException.class);

            assertEquals(1L, supplier.getAsLong());
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                CheckedLongSupplier<IOException> supplier = CheckedLongSupplier.checked(() -> {
                    throw new UncheckedException(new IOException("foo"));
                }, IOException.class);

                IOException thrown = assertThrows(IOException.class, supplier::getAsLong);
                assertEquals("foo", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                CheckedLongSupplier<IOException> supplier = CheckedLongSupplier.checked(() -> {
                    throw new UncheckedException(new FileNotFoundException("foo"));
                }, IOException.class);

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class, supplier::getAsLong);
                assertEquals("foo", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                CheckedLongSupplier<IOException> supplier = CheckedLongSupplier.checked(() -> {
                    throw new UncheckedException(new ParseException("foo", 0));
                }, IOException.class);

                UncheckedException thrown = assertThrows(UncheckedException.class, supplier::getAsLong);
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            CheckedLongSupplier<IOException> supplier = CheckedLongSupplier.checked(() -> {
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

            assertThrows(NullPointerException.class, () -> CheckedLongSupplier.invokeAndUnwrap(null, IOException.class));
            assertThrows(NullPointerException.class, () -> CheckedLongSupplier.invokeAndUnwrap(supplier, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            LongSupplier supplier = () -> 1L;

            assertEquals(1L, CheckedLongSupplier.invokeAndUnwrap(supplier, IOException.class));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                LongSupplier supplier = () -> {
                    throw new UncheckedException(new IOException("foo"));
                };

                IOException thrown = assertThrows(IOException.class, () -> CheckedLongSupplier.invokeAndUnwrap(supplier, IOException.class));
                assertEquals("foo", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                LongSupplier supplier = () -> {
                    throw new UncheckedException(new FileNotFoundException("foo"));
                };

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class,
                        () -> CheckedLongSupplier.invokeAndUnwrap(supplier, IOException.class));
                assertEquals("foo", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                LongSupplier supplier = () -> {
                    throw new UncheckedException(new ParseException("foo", 0));
                };

                UncheckedException thrown = assertThrows(UncheckedException.class,
                        () -> CheckedLongSupplier.invokeAndUnwrap(supplier, IOException.class));
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
                    () -> CheckedLongSupplier.invokeAndUnwrap(supplier, IOException.class));
            assertEquals("foo", thrown.getMessage());
        }
    }
}
