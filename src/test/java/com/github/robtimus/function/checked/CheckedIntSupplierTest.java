/*
 * CheckedIntSupplierTest.java
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

@SuppressWarnings("nls")
class CheckedIntSupplierTest {

    @Nested
    class OnErrorThrowAsChecked {

        @Test
        void testNullArgument() {
            CheckedIntSupplier<IOException> supplier = Spied.checkedIntSupplier(() -> 1);

            assertThrows(NullPointerException.class, () -> supplier.onErrorThrowAsChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedIntSupplier<IOException> supplier = Spied.checkedIntSupplier(() -> 1);

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedIntSupplier<ExecutionException> throwing = supplier.onErrorThrowAsChecked(errorMapper);

            assertEquals(1, throwing.getAsInt());

            verify(supplier).getAsInt();
            verify(supplier).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(supplier, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedIntSupplier<IOException> supplier = Spied.checkedIntSupplier(() -> {
                throw new IOException("foo");
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedIntSupplier<ExecutionException> throwing = supplier.onErrorThrowAsChecked(errorMapper);

            ExecutionException thrown = assertThrows(ExecutionException.class, throwing::getAsInt);
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo", cause.getMessage());

            verify(supplier).getAsInt();
            verify(supplier).onErrorThrowAsChecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(supplier, errorMapper);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedIntSupplier<IOException> supplier = Spied.checkedIntSupplier(() -> {
                throw new IllegalStateException("foo");
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedIntSupplier<ExecutionException> throwing = supplier.onErrorThrowAsChecked(errorMapper);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, throwing::getAsInt);
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
            CheckedIntSupplier<IOException> supplier = Spied.checkedIntSupplier(() -> 1);

            assertThrows(NullPointerException.class, () -> supplier.onErrorThrowAsUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedIntSupplier<IOException> supplier = Spied.checkedIntSupplier(() -> 1);

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            IntSupplier throwing = supplier.onErrorThrowAsUnchecked(errorMapper);

            assertEquals(1, throwing.getAsInt());

            verify(supplier).getAsInt();
            verify(supplier).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(supplier, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedIntSupplier<IOException> supplier = Spied.checkedIntSupplier(() -> {
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

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedIntSupplier<IOException> supplier = Spied.checkedIntSupplier(() -> {
                throw new IllegalArgumentException("foo");
            });

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            IntSupplier throwing = supplier.onErrorThrowAsUnchecked(errorMapper);

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, throwing::getAsInt);
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
            CheckedIntSupplier<IOException> supplier = Spied.checkedIntSupplier(() -> 1);

            assertThrows(NullPointerException.class, () -> supplier.onErrorHandleChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedIntSupplier<IOException> supplier = Spied.checkedIntSupplier(() -> 1);

            CheckedToIntFunction<IOException, ExecutionException> errorHandler = Spied.checkedToIntFunction(e -> 0);

            CheckedIntSupplier<ExecutionException> handling = supplier.onErrorHandleChecked(errorHandler);

            assertEquals(1, handling.getAsInt());

            verify(supplier).getAsInt();
            verify(supplier).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(supplier, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException, ExecutionException {
                CheckedIntSupplier<IOException> supplier = Spied.checkedIntSupplier(() -> {
                    throw new IOException("foo");
                });

                CheckedToIntFunction<IOException, ExecutionException> errorHandler = Spied.checkedToIntFunction(e -> 0);

                CheckedIntSupplier<ExecutionException> handling = supplier.onErrorHandleChecked(errorHandler);

                assertEquals(0, handling.getAsInt());

                verify(supplier).getAsInt();
                verify(supplier).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsInt(any());
                verifyNoMoreInteractions(supplier, errorHandler);
            }

            @Test
            void testHandlerThrowsChecked() throws IOException, ExecutionException {
                CheckedIntSupplier<IOException> supplier = Spied.checkedIntSupplier(() -> {
                    throw new IOException("foo");
                });

                CheckedToIntFunction<IOException, ExecutionException> errorHandler = Spied.checkedToIntFunction(e -> {
                    throw new ExecutionException(e);
                });

                CheckedIntSupplier<ExecutionException> handling = supplier.onErrorHandleChecked(errorHandler);

                ExecutionException thrown = assertThrows(ExecutionException.class, handling::getAsInt);
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());

                verify(supplier).getAsInt();
                verify(supplier).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsInt(any());
                verifyNoMoreInteractions(supplier, errorHandler);
            }

            @Test
            void testHandlerThrowsUnchecked() throws IOException, ExecutionException {
                CheckedIntSupplier<IOException> supplier = Spied.checkedIntSupplier(() -> {
                    throw new IOException("foo");
                });

                CheckedToIntFunction<IOException, ExecutionException> errorHandler = Spied.checkedToIntFunction(e -> {
                    throw new IllegalStateException(e);
                });

                CheckedIntSupplier<ExecutionException> handling = supplier.onErrorHandleChecked(errorHandler);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, handling::getAsInt);
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());

                verify(supplier).getAsInt();
                verify(supplier).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsInt(any());
                verifyNoMoreInteractions(supplier, errorHandler);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedIntSupplier<IOException> supplier = Spied.checkedIntSupplier(() -> {
                throw new IllegalStateException("foo");
            });

            CheckedToIntFunction<IOException, ExecutionException> errorHandler = Spied.checkedToIntFunction(e -> 0);

            CheckedIntSupplier<ExecutionException> handling = supplier.onErrorHandleChecked(errorHandler);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, handling::getAsInt);
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
            CheckedIntSupplier<IOException> supplier = Spied.checkedIntSupplier(() -> 1);

            assertThrows(NullPointerException.class, () -> supplier.onErrorHandleUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedIntSupplier<IOException> supplier = Spied.checkedIntSupplier(() -> 1);

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
                CheckedIntSupplier<IOException> supplier = Spied.checkedIntSupplier(() -> {
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

            @Test
            void testHandlerThrowsUnchecked() throws IOException {
                CheckedIntSupplier<IOException> supplier = Spied.checkedIntSupplier(() -> {
                    throw new IOException("foo");
                });

                ToIntFunction<IOException> errorHandler = Spied.toIntFunction(e -> {
                    throw new IllegalStateException(e);
                });

                IntSupplier handling = supplier.onErrorHandleUnchecked(errorHandler);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, handling::getAsInt);
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());

                verify(supplier).getAsInt();
                verify(supplier).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).applyAsInt(any());
                verifyNoMoreInteractions(supplier, errorHandler);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedIntSupplier<IOException> supplier = Spied.checkedIntSupplier(() -> {
                throw new IllegalStateException("foo");
            });

            ToIntFunction<IOException> errorHandler = Spied.toIntFunction(e -> 0);

            IntSupplier handling = supplier.onErrorHandleUnchecked(errorHandler);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, handling::getAsInt);
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
            CheckedIntSupplier<IOException> supplier = Spied.checkedIntSupplier(() -> 1);

            assertThrows(NullPointerException.class, () -> supplier.onErrorGetCheckedAsInt(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedIntSupplier<IOException> supplier = Spied.checkedIntSupplier(() -> 1);

            CheckedIntSupplier<ExecutionException> fallback = Spied.checkedIntSupplier(() -> 0);

            CheckedIntSupplier<ExecutionException> getting = supplier.onErrorGetCheckedAsInt(fallback);

            assertEquals(1, getting.getAsInt());

            verify(supplier).getAsInt();
            verify(supplier).onErrorGetCheckedAsInt(fallback);
            verifyNoMoreInteractions(supplier, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                CheckedIntSupplier<IOException> supplier = Spied.checkedIntSupplier(() -> {
                    throw new IOException("foo");
                });

                CheckedIntSupplier<ParseException> fallback = Spied.checkedIntSupplier(() -> 0);

                CheckedIntSupplier<ParseException> getting = supplier.onErrorGetCheckedAsInt(fallback);

                assertEquals(0, getting.getAsInt());

                verify(supplier).getAsInt();
                verify(supplier).onErrorGetCheckedAsInt(fallback);
                verify(fallback).getAsInt();
                verifyNoMoreInteractions(supplier, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                CheckedIntSupplier<IOException> supplier = Spied.checkedIntSupplier(() -> {
                    throw new IOException("foo");
                });

                CheckedIntSupplier<ParseException> fallback = Spied.checkedIntSupplier(() -> {
                    throw new ParseException("bar", 0);
                });

                CheckedIntSupplier<ParseException> getting = supplier.onErrorGetCheckedAsInt(fallback);

                ParseException thrown = assertThrows(ParseException.class, getting::getAsInt);
                assertEquals("bar", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(supplier).getAsInt();
                verify(supplier).onErrorGetCheckedAsInt(fallback);
                verify(fallback).getAsInt();
                verifyNoMoreInteractions(supplier, fallback);
            }

            @Test
            void testFallbackThrowsUnchecked() throws IOException, ParseException {
                CheckedIntSupplier<IOException> supplier = Spied.checkedIntSupplier(() -> {
                    throw new IOException("foo");
                });

                CheckedIntSupplier<ParseException> fallback = Spied.checkedIntSupplier(() -> {
                    throw new IllegalStateException("bar");
                });

                CheckedIntSupplier<ParseException> getting = supplier.onErrorGetCheckedAsInt(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, getting::getAsInt);
                assertEquals("bar", thrown.getMessage());

                verify(supplier).getAsInt();
                verify(supplier).onErrorGetCheckedAsInt(fallback);
                verify(fallback).getAsInt();
                verifyNoMoreInteractions(supplier, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedIntSupplier<IOException> supplier = Spied.checkedIntSupplier(() -> {
                throw new IllegalStateException("foo");
            });

            CheckedIntSupplier<ParseException> fallback = Spied.checkedIntSupplier(() -> 0);

            CheckedIntSupplier<ParseException> getting = supplier.onErrorGetCheckedAsInt(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, getting::getAsInt);
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
            CheckedIntSupplier<IOException> supplier = Spied.checkedIntSupplier(() -> 1);

            assertThrows(NullPointerException.class, () -> supplier.onErrorGetUncheckedAsInt(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedIntSupplier<IOException> supplier = Spied.checkedIntSupplier(() -> 1);

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
                CheckedIntSupplier<IOException> supplier = Spied.checkedIntSupplier(() -> {
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

            @Test
            void testFallbackThrowsUnchecked() throws IOException {
                CheckedIntSupplier<IOException> supplier = Spied.checkedIntSupplier(() -> {
                    throw new IOException("foo");
                });

                IntSupplier fallback = Spied.intSupplier(() -> {
                    throw new IllegalStateException("bar");
                });

                IntSupplier getting = supplier.onErrorGetUncheckedAsInt(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, getting::getAsInt);
                assertEquals("bar", thrown.getMessage());

                verify(supplier).getAsInt();
                verify(supplier).onErrorGetUncheckedAsInt(fallback);
                verify(fallback).getAsInt();
                verifyNoMoreInteractions(supplier, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedIntSupplier<IOException> supplier = Spied.checkedIntSupplier(() -> {
                throw new IllegalStateException("foo");
            });

            IntSupplier fallback = Spied.intSupplier(() -> 0);

            IntSupplier getting = supplier.onErrorGetUncheckedAsInt(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, getting::getAsInt);
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
            CheckedIntSupplier<IOException> supplier = Spied.checkedIntSupplier(() -> 1);

            IntSupplier returning = supplier.onErrorReturn(0);

            assertEquals(1, returning.getAsInt());

            verify(supplier).getAsInt();
            verify(supplier).onErrorReturn(0);
            verifyNoMoreInteractions(supplier);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedIntSupplier<IOException> supplier = Spied.checkedIntSupplier(() -> {
                throw new IOException("foo");
            });

            IntSupplier returning = supplier.onErrorReturn(0);

            assertEquals(0, returning.getAsInt());

            verify(supplier).getAsInt();
            verify(supplier).onErrorReturn(0);
            verifyNoMoreInteractions(supplier);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedIntSupplier<IOException> supplier = Spied.checkedIntSupplier(() -> {
                throw new IllegalStateException("foo");
            });

            IntSupplier returning = supplier.onErrorReturn(0);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, returning::getAsInt);
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
            CheckedIntSupplier<IOException> supplier = Spied.checkedIntSupplier(() -> 1);

            IntSupplier unchecked = supplier.unchecked();

            assertEquals(1, unchecked.getAsInt());

            verify(supplier).getAsInt();
            verify(supplier).unchecked();
            verify(supplier).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(supplier);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedIntSupplier<IOException> supplier = Spied.checkedIntSupplier(() -> {
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

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedIntSupplier<IOException> supplier = Spied.checkedIntSupplier(() -> {
                throw new IllegalArgumentException("foo");
            });

            IntSupplier unchecked = supplier.unchecked();

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, unchecked::getAsInt);
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
            assertThrows(NullPointerException.class, () -> CheckedIntSupplier.of(null));
        }

        @Test
        void testNonNullArgument() throws IOException {
            CheckedIntSupplier<IOException> supplier = CheckedIntSupplier.of(() -> 1);

            assertEquals(1, supplier.getAsInt());
        }
    }

    @Nested
    class UncheckedFactory {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> CheckedIntSupplier.unchecked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedIntSupplier<IOException> supplier = Spied.checkedIntSupplier(() -> 1);

            IntSupplier unchecked = CheckedIntSupplier.unchecked(supplier);

            assertEquals(1, unchecked.getAsInt());

            verify(supplier).unchecked();
            verify(supplier).onErrorThrowAsUnchecked(any());
            verify(supplier).getAsInt();
            verifyNoMoreInteractions(supplier);
        }

        @Test
        void testArgumentThrowsChecked() throws IOException {
            CheckedIntSupplier<IOException> supplier = Spied.checkedIntSupplier(() -> {
                throw new IOException("foo");
            });

            IntSupplier unchecked = CheckedIntSupplier.unchecked(supplier);

            UncheckedException thrown = assertThrows(UncheckedException.class, unchecked::getAsInt);
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo", cause.getMessage());

            verify(supplier).unchecked();
            verify(supplier).onErrorThrowAsUnchecked(any());
            verify(supplier).getAsInt();
            verifyNoMoreInteractions(supplier);
        }

        @Test
        void testArgumentThrowsUnchecked() throws IOException {
            CheckedIntSupplier<IOException> supplier = Spied.checkedIntSupplier(() -> {
                throw new IllegalArgumentException("foo");
            });

            IntSupplier unchecked = CheckedIntSupplier.unchecked(supplier);

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, unchecked::getAsInt);
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
            assertThrows(NullPointerException.class, () -> CheckedIntSupplier.checked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedIntSupplier<IOException> supplier = CheckedIntSupplier.checked(() -> 1);

            assertEquals(1, supplier.getAsInt());
        }

        @Test
        void testArgumentThrowsUnchecked() {
            CheckedIntSupplier<IOException> supplier = CheckedIntSupplier.checked(() -> {
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

            assertThrows(NullPointerException.class, () -> CheckedIntSupplier.checked(null, IOException.class));
            assertThrows(NullPointerException.class, () -> CheckedIntSupplier.checked(supplier, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedIntSupplier<IOException> supplier = CheckedIntSupplier.checked(() -> 1, IOException.class);

            assertEquals(1, supplier.getAsInt());
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                CheckedIntSupplier<IOException> supplier = CheckedIntSupplier.checked(() -> {
                    throw new UncheckedException(new IOException("foo"));
                }, IOException.class);

                IOException thrown = assertThrows(IOException.class, supplier::getAsInt);
                assertEquals("foo", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                CheckedIntSupplier<IOException> supplier = CheckedIntSupplier.checked(() -> {
                    throw new UncheckedException(new FileNotFoundException("foo"));
                }, IOException.class);

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class, supplier::getAsInt);
                assertEquals("foo", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                CheckedIntSupplier<IOException> supplier = CheckedIntSupplier.checked(() -> {
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
            CheckedIntSupplier<IOException> supplier = CheckedIntSupplier.checked(() -> {
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

            assertThrows(NullPointerException.class, () -> CheckedIntSupplier.invokeAndUnwrap(null, IOException.class));
            assertThrows(NullPointerException.class, () -> CheckedIntSupplier.invokeAndUnwrap(supplier, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            IntSupplier supplier = () -> 1;

            assertEquals(1, CheckedIntSupplier.invokeAndUnwrap(supplier, IOException.class));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                IntSupplier supplier = () -> {
                    throw new UncheckedException(new IOException("foo"));
                };

                IOException thrown = assertThrows(IOException.class, () -> CheckedIntSupplier.invokeAndUnwrap(supplier, IOException.class));
                assertEquals("foo", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                IntSupplier supplier = () -> {
                    throw new UncheckedException(new FileNotFoundException("foo"));
                };

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class,
                        () -> CheckedIntSupplier.invokeAndUnwrap(supplier, IOException.class));
                assertEquals("foo", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                IntSupplier supplier = () -> {
                    throw new UncheckedException(new ParseException("foo", 0));
                };

                UncheckedException thrown = assertThrows(UncheckedException.class,
                        () -> CheckedIntSupplier.invokeAndUnwrap(supplier, IOException.class));
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
                    () -> CheckedIntSupplier.invokeAndUnwrap(supplier, IOException.class));
            assertEquals("foo", thrown.getMessage());
        }
    }
}
