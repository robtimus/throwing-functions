/*
 * CheckedSupplierTest.java
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
import java.util.function.Supplier;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("nls")
class CheckedSupplierTest {

    @Nested
    class OnErrorThrowAsChecked {

        @Test
        void testNullArgument() {
            CheckedSupplier<String, IOException> supplier = Spied.checkedSupplier(() -> "foo");

            assertThrows(NullPointerException.class, () -> supplier.onErrorThrowAsChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedSupplier<String, IOException> supplier = Spied.checkedSupplier(() -> "foo");

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedSupplier<String, ExecutionException> throwing = supplier.onErrorThrowAsChecked(errorMapper);

            assertEquals("foo", throwing.get());

            verify(supplier).get();
            verify(supplier).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(supplier, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedSupplier<String, IOException> supplier = Spied.checkedSupplier(() -> {
                throw new IOException("foo");
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedSupplier<String, ExecutionException> throwing = supplier.onErrorThrowAsChecked(errorMapper);

            ExecutionException thrown = assertThrows(ExecutionException.class, throwing::get);
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo", cause.getMessage());

            verify(supplier).get();
            verify(supplier).onErrorThrowAsChecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(supplier, errorMapper);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedSupplier<String, IOException> supplier = Spied.checkedSupplier(() -> {
                throw new IllegalStateException("foo");
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedSupplier<String, ExecutionException> throwing = supplier.onErrorThrowAsChecked(errorMapper);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, throwing::get);
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
            CheckedSupplier<String, IOException> supplier = Spied.checkedSupplier(() -> "foo");

            assertThrows(NullPointerException.class, () -> supplier.onErrorThrowAsUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedSupplier<String, IOException> supplier = Spied.checkedSupplier(() -> "foo");

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            Supplier<String> throwing = supplier.onErrorThrowAsUnchecked(errorMapper);

            assertEquals("foo", throwing.get());

            verify(supplier).get();
            verify(supplier).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(supplier, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedSupplier<String, IOException> supplier = Spied.checkedSupplier(() -> {
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

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedSupplier<String, IOException> supplier = Spied.checkedSupplier(() -> {
                throw new IllegalArgumentException("foo");
            });

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            Supplier<String> throwing = supplier.onErrorThrowAsUnchecked(errorMapper);

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, throwing::get);
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
            CheckedSupplier<String, IOException> supplier = Spied.checkedSupplier(() -> "foo");

            assertThrows(NullPointerException.class, () -> supplier.onErrorHandleChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedSupplier<String, IOException> supplier = Spied.checkedSupplier(() -> "foo");

            CheckedFunction<IOException, String, ExecutionException> errorHandler = Spied.checkedFunction(Exception::getMessage);

            CheckedSupplier<String, ExecutionException> handling = supplier.onErrorHandleChecked(errorHandler);

            assertEquals("foo", handling.get());

            verify(supplier).get();
            verify(supplier).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(supplier, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException, ExecutionException {
                CheckedSupplier<String, IOException> supplier = Spied.checkedSupplier(() -> {
                    throw new IOException("foo");
                });

                CheckedFunction<IOException, String, ExecutionException> errorHandler = Spied.checkedFunction(Exception::getMessage);

                CheckedSupplier<String, ExecutionException> handling = supplier.onErrorHandleChecked(errorHandler);

                assertEquals("foo", handling.get());

                verify(supplier).get();
                verify(supplier).onErrorHandleChecked(errorHandler);
                verify(errorHandler).apply(any());
                verifyNoMoreInteractions(supplier, errorHandler);
            }

            @Test
            void testHandlerThrowsChecked() throws IOException, ExecutionException {
                CheckedSupplier<String, IOException> supplier = Spied.checkedSupplier(() -> {
                    throw new IOException("foo");
                });

                CheckedFunction<IOException, String, ExecutionException> errorHandler = Spied.checkedFunction(e -> {
                    throw new ExecutionException(e);
                });

                CheckedSupplier<String, ExecutionException> handling = supplier.onErrorHandleChecked(errorHandler);

                ExecutionException thrown = assertThrows(ExecutionException.class, handling::get);
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());

                verify(supplier).get();
                verify(supplier).onErrorHandleChecked(errorHandler);
                verify(errorHandler).apply(any());
                verifyNoMoreInteractions(supplier, errorHandler);
            }

            @Test
            void testHandlerThrowsUnchecked() throws IOException, ExecutionException {
                CheckedSupplier<String, IOException> supplier = Spied.checkedSupplier(() -> {
                    throw new IOException("foo");
                });

                CheckedFunction<IOException, String, ExecutionException> errorHandler = Spied.checkedFunction(e -> {
                    throw new IllegalStateException(e);
                });

                CheckedSupplier<String, ExecutionException> handling = supplier.onErrorHandleChecked(errorHandler);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, handling::get);
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());

                verify(supplier).get();
                verify(supplier).onErrorHandleChecked(errorHandler);
                verify(errorHandler).apply(any());
                verifyNoMoreInteractions(supplier, errorHandler);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedSupplier<String, IOException> supplier = Spied.checkedSupplier(() -> {
                throw new IllegalStateException("foo");
            });

            CheckedFunction<IOException, String, ExecutionException> errorHandler = Spied.checkedFunction(Exception::getMessage);

            CheckedSupplier<String, ExecutionException> handling = supplier.onErrorHandleChecked(errorHandler);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, handling::get);
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
            CheckedSupplier<String, IOException> supplier = Spied.checkedSupplier(() -> "foo");

            assertThrows(NullPointerException.class, () -> supplier.onErrorHandleUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedSupplier<String, IOException> supplier = Spied.checkedSupplier(() -> "foo");

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
                CheckedSupplier<String, IOException> supplier = Spied.checkedSupplier(() -> {
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

            @Test
            void testHandlerThrowsUnchecked() throws IOException {
                CheckedSupplier<String, IOException> supplier = Spied.checkedSupplier(() -> {
                    throw new IOException("foo");
                });

                Function<IOException, String> errorHandler = Spied.function(e -> {
                    throw new IllegalStateException(e);
                });

                Supplier<String> handling = supplier.onErrorHandleUnchecked(errorHandler);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, handling::get);
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());

                verify(supplier).get();
                verify(supplier).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).apply(any());
                verifyNoMoreInteractions(supplier, errorHandler);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedSupplier<String, IOException> supplier = Spied.checkedSupplier(() -> {
                throw new IllegalStateException("foo");
            });

            Function<IOException, String> errorHandler = Spied.function(Exception::getMessage);

            Supplier<String> handling = supplier.onErrorHandleUnchecked(errorHandler);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, handling::get);
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
            CheckedSupplier<String, IOException> supplier = Spied.checkedSupplier(() -> "foo");

            assertThrows(NullPointerException.class, () -> supplier.onErrorGetChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedSupplier<String, IOException> supplier = Spied.checkedSupplier(() -> "foo");

            CheckedSupplier<String, ExecutionException> fallback = Spied.checkedSupplier(() -> "bar");

            CheckedSupplier<String, ExecutionException> getting = supplier.onErrorGetChecked(fallback);

            assertEquals("foo", getting.get());

            verify(supplier).get();
            verify(supplier).onErrorGetChecked(fallback);
            verifyNoMoreInteractions(supplier, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                CheckedSupplier<String, IOException> supplier = Spied.checkedSupplier(() -> {
                    throw new IOException("foo");
                });

                CheckedSupplier<String, ParseException> fallback = Spied.checkedSupplier(() -> "bar");

                CheckedSupplier<String, ParseException> getting = supplier.onErrorGetChecked(fallback);

                assertEquals("bar", getting.get());

                verify(supplier).get();
                verify(supplier).onErrorGetChecked(fallback);
                verify(fallback).get();
                verifyNoMoreInteractions(supplier, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                CheckedSupplier<String, IOException> supplier = Spied.checkedSupplier(() -> {
                    throw new IOException("foo");
                });

                CheckedSupplier<String, ParseException> fallback = Spied.checkedSupplier(() -> {
                    throw new ParseException("bar", 0);
                });

                CheckedSupplier<String, ParseException> getting = supplier.onErrorGetChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, getting::get);
                assertEquals("bar", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(supplier).get();
                verify(supplier).onErrorGetChecked(fallback);
                verify(fallback).get();
                verifyNoMoreInteractions(supplier, fallback);
            }

            @Test
            void testFallbackThrowsUnchecked() throws IOException, ParseException {
                CheckedSupplier<String, IOException> supplier = Spied.checkedSupplier(() -> {
                    throw new IOException("foo");
                });

                CheckedSupplier<String, ParseException> fallback = Spied.checkedSupplier(() -> {
                    throw new IllegalStateException("bar");
                });

                CheckedSupplier<String, ParseException> getting = supplier.onErrorGetChecked(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, getting::get);
                assertEquals("bar", thrown.getMessage());

                verify(supplier).get();
                verify(supplier).onErrorGetChecked(fallback);
                verify(fallback).get();
                verifyNoMoreInteractions(supplier, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedSupplier<String, IOException> supplier = Spied.checkedSupplier(() -> {
                throw new IllegalStateException("foo");
            });

            CheckedSupplier<String, ParseException> fallback = Spied.checkedSupplier(() -> "bar");

            CheckedSupplier<String, ParseException> getting = supplier.onErrorGetChecked(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, getting::get);
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
            CheckedSupplier<String, IOException> supplier = Spied.checkedSupplier(() -> "foo");

            assertThrows(NullPointerException.class, () -> supplier.onErrorGetUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedSupplier<String, IOException> supplier = Spied.checkedSupplier(() -> "foo");

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
                CheckedSupplier<String, IOException> supplier = Spied.checkedSupplier(() -> {
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

            @Test
            void testFallbackThrowsUnchecked() throws IOException {
                CheckedSupplier<String, IOException> supplier = Spied.checkedSupplier(() -> {
                    throw new IOException("foo");
                });

                Supplier<String> fallback = Spied.supplier(() -> {
                    throw new IllegalStateException("bar");
                });

                Supplier<String> getting = supplier.onErrorGetUnchecked(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, getting::get);
                assertEquals("bar", thrown.getMessage());

                verify(supplier).get();
                verify(supplier).onErrorGetUnchecked(fallback);
                verify(fallback).get();
                verifyNoMoreInteractions(supplier, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedSupplier<String, IOException> supplier = Spied.checkedSupplier(() -> {
                throw new IllegalStateException("foo");
            });

            Supplier<String> fallback = Spied.supplier(() -> "bar");

            Supplier<String> getting = supplier.onErrorGetUnchecked(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, getting::get);
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
            CheckedSupplier<String, IOException> supplier = Spied.checkedSupplier(() -> "foo");

            Supplier<String> returning = supplier.onErrorReturn("bar");

            assertEquals("foo", returning.get());

            verify(supplier).get();
            verify(supplier).onErrorReturn("bar");
            verifyNoMoreInteractions(supplier);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedSupplier<String, IOException> supplier = Spied.checkedSupplier(() -> {
                throw new IOException("foo");
            });

            Supplier<String> returning = supplier.onErrorReturn("bar");

            assertEquals("bar", returning.get());

            verify(supplier).get();
            verify(supplier).onErrorReturn("bar");
            verifyNoMoreInteractions(supplier);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedSupplier<String, IOException> supplier = Spied.checkedSupplier(() -> {
                throw new IllegalStateException("foo");
            });

            Supplier<String> returning = supplier.onErrorReturn("bar");

            IllegalStateException thrown = assertThrows(IllegalStateException.class, returning::get);
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
            CheckedSupplier<String, IOException> supplier = Spied.checkedSupplier(() -> "foo");

            Supplier<String> unchecked = supplier.unchecked();

            assertEquals("foo", unchecked.get());

            verify(supplier).get();
            verify(supplier).unchecked();
            verify(supplier).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(supplier);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedSupplier<String, IOException> supplier = Spied.checkedSupplier(() -> {
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

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedSupplier<String, IOException> supplier = Spied.checkedSupplier(() -> {
                throw new IllegalArgumentException("foo");
            });

            Supplier<String> unchecked = supplier.unchecked();

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, unchecked::get);
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
            assertThrows(NullPointerException.class, () -> CheckedSupplier.of(null));
        }

        @Test
        void testNonNullArgument() throws IOException {
            CheckedSupplier<String, IOException> supplier = CheckedSupplier.of(() -> "foo");

            assertEquals("foo", supplier.get());
        }
    }

    @Nested
    class UncheckedFactory {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> CheckedSupplier.unchecked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedSupplier<String, IOException> supplier = Spied.checkedSupplier(() -> "foo");

            Supplier<String> unchecked = CheckedSupplier.unchecked(supplier);

            assertEquals("foo", unchecked.get());

            verify(supplier).unchecked();
            verify(supplier).onErrorThrowAsUnchecked(any());
            verify(supplier).get();
            verifyNoMoreInteractions(supplier);
        }

        @Test
        void testArgumentThrowsChecked() throws IOException {
            CheckedSupplier<String, IOException> supplier = Spied.checkedSupplier(() -> {
                throw new IOException("foo");
            });

            Supplier<String> unchecked = CheckedSupplier.unchecked(supplier);

            UncheckedException thrown = assertThrows(UncheckedException.class, unchecked::get);
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo", cause.getMessage());

            verify(supplier).unchecked();
            verify(supplier).onErrorThrowAsUnchecked(any());
            verify(supplier).get();
            verifyNoMoreInteractions(supplier);
        }

        @Test
        void testArgumentThrowsUnchecked() throws IOException {
            CheckedSupplier<String, IOException> supplier = Spied.checkedSupplier(() -> {
                throw new IllegalArgumentException("foo");
            });

            Supplier<String> unchecked = CheckedSupplier.unchecked(supplier);

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, unchecked::get);
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
            assertThrows(NullPointerException.class, () -> CheckedSupplier.checked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedSupplier<String, IOException> supplier = CheckedSupplier.checked(() -> "foo");

            assertEquals("foo", supplier.get());
        }

        @Test
        void testArgumentThrowsUnchecked() {
            CheckedSupplier<String, IOException> supplier = CheckedSupplier.checked(() -> {
                throw new UncheckedException("foo", new IOException());
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

            assertThrows(NullPointerException.class, () -> CheckedSupplier.checked(null, IOException.class));
            assertThrows(NullPointerException.class, () -> CheckedSupplier.checked(supplier, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedSupplier<String, IOException> supplier = CheckedSupplier.checked(() -> "foo", IOException.class);

            assertEquals("foo", supplier.get());
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                CheckedSupplier<String, IOException> supplier = CheckedSupplier.checked(() -> {
                    throw new UncheckedException(new IOException("foo"));
                }, IOException.class);

                IOException thrown = assertThrows(IOException.class, supplier::get);
                assertEquals("foo", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                CheckedSupplier<String, IOException> supplier = CheckedSupplier.checked(() -> {
                    throw new UncheckedException(new FileNotFoundException("foo"));
                }, IOException.class);

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class, supplier::get);
                assertEquals("foo", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                CheckedSupplier<String, IOException> supplier = CheckedSupplier.checked(() -> {
                    throw new UncheckedException(new ParseException("foo", 0));
                }, IOException.class);

                UncheckedException thrown = assertThrows(UncheckedException.class, supplier::get);
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            CheckedSupplier<String, IOException> supplier = CheckedSupplier.checked(() -> {
                throw new IllegalStateException("foo");
            }, IOException.class);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, supplier::get);
            assertEquals("foo", thrown.getMessage());
        }
    }

    @Nested
    class InvokeAndUnwrap {

        @Test
        void testNullArguments() {
            Supplier<String> supplier = () -> "foo";

            assertThrows(NullPointerException.class, () -> CheckedSupplier.invokeAndUnwrap(null, IOException.class));
            assertThrows(NullPointerException.class, () -> CheckedSupplier.invokeAndUnwrap(supplier, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            Supplier<String> supplier = () -> "foo";

            assertEquals("foo", CheckedSupplier.invokeAndUnwrap(supplier, IOException.class));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                Supplier<String> supplier = () -> {
                    throw new UncheckedException(new IOException("foo"));
                };

                IOException thrown = assertThrows(IOException.class, () -> CheckedSupplier.invokeAndUnwrap(supplier, IOException.class));
                assertEquals("foo", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                Supplier<String> supplier = () -> {
                    throw new UncheckedException(new FileNotFoundException("foo"));
                };

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class,
                        () -> CheckedSupplier.invokeAndUnwrap(supplier, IOException.class));
                assertEquals("foo", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                Supplier<String> supplier = () -> {
                    throw new UncheckedException(new ParseException("foo", 0));
                };

                UncheckedException thrown = assertThrows(UncheckedException.class,
                        () -> CheckedSupplier.invokeAndUnwrap(supplier, IOException.class));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            Supplier<String> supplier = () -> {
                throw new IllegalStateException("foo");
            };

            IllegalStateException thrown = assertThrows(IllegalStateException.class,
                    () -> CheckedSupplier.invokeAndUnwrap(supplier, IOException.class));
            assertEquals("foo", thrown.getMessage());
        }
    }
}
