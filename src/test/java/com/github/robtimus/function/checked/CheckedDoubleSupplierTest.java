/*
 * CheckedDoubleSupplierTest.java
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

@SuppressWarnings("nls")
class CheckedDoubleSupplierTest {

    @Nested
    class OnErrorThrowAsChecked {

        @Test
        void testNullArgument() {
            CheckedDoubleSupplier<IOException> supplier = Spied.checkedDoubleSupplier(() -> 1D);

            assertThrows(NullPointerException.class, () -> supplier.onErrorThrowAsChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedDoubleSupplier<IOException> supplier = Spied.checkedDoubleSupplier(() -> 1D);

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedDoubleSupplier<ExecutionException> throwing = supplier.onErrorThrowAsChecked(errorMapper);

            assertEquals(1D, throwing.getAsDouble());

            verify(supplier).getAsDouble();
            verify(supplier).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(supplier, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedDoubleSupplier<IOException> supplier = Spied.checkedDoubleSupplier(() -> {
                throw new IOException("foo");
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedDoubleSupplier<ExecutionException> throwing = supplier.onErrorThrowAsChecked(errorMapper);

            ExecutionException thrown = assertThrows(ExecutionException.class, throwing::getAsDouble);
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo", cause.getMessage());

            verify(supplier).getAsDouble();
            verify(supplier).onErrorThrowAsChecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(supplier, errorMapper);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedDoubleSupplier<IOException> supplier = Spied.checkedDoubleSupplier(() -> {
                throw new IllegalStateException("foo");
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedDoubleSupplier<ExecutionException> throwing = supplier.onErrorThrowAsChecked(errorMapper);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, throwing::getAsDouble);
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
            CheckedDoubleSupplier<IOException> supplier = Spied.checkedDoubleSupplier(() -> 1D);

            assertThrows(NullPointerException.class, () -> supplier.onErrorThrowAsUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedDoubleSupplier<IOException> supplier = Spied.checkedDoubleSupplier(() -> 1D);

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            DoubleSupplier throwing = supplier.onErrorThrowAsUnchecked(errorMapper);

            assertEquals(1D, throwing.getAsDouble());

            verify(supplier).getAsDouble();
            verify(supplier).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(supplier, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedDoubleSupplier<IOException> supplier = Spied.checkedDoubleSupplier(() -> {
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

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedDoubleSupplier<IOException> supplier = Spied.checkedDoubleSupplier(() -> {
                throw new IllegalArgumentException("foo");
            });

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            DoubleSupplier throwing = supplier.onErrorThrowAsUnchecked(errorMapper);

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, throwing::getAsDouble);
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
            CheckedDoubleSupplier<IOException> supplier = Spied.checkedDoubleSupplier(() -> 1D);

            assertThrows(NullPointerException.class, () -> supplier.onErrorHandleChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedDoubleSupplier<IOException> supplier = Spied.checkedDoubleSupplier(() -> 1D);

            CheckedToDoubleFunction<IOException, ExecutionException> errorHandler = Spied.checkedToDoubleFunction(e -> 0D);

            CheckedDoubleSupplier<ExecutionException> handling = supplier.onErrorHandleChecked(errorHandler);

            assertEquals(1D, handling.getAsDouble());

            verify(supplier).getAsDouble();
            verify(supplier).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(supplier, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException, ExecutionException {
                CheckedDoubleSupplier<IOException> supplier = Spied.checkedDoubleSupplier(() -> {
                    throw new IOException("foo");
                });

                CheckedToDoubleFunction<IOException, ExecutionException> errorHandler = Spied.checkedToDoubleFunction(e -> 0D);

                CheckedDoubleSupplier<ExecutionException> handling = supplier.onErrorHandleChecked(errorHandler);

                assertEquals(0D, handling.getAsDouble());

                verify(supplier).getAsDouble();
                verify(supplier).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsDouble(any());
                verifyNoMoreInteractions(supplier, errorHandler);
            }

            @Test
            void testHandlerThrowsChecked() throws IOException, ExecutionException {
                CheckedDoubleSupplier<IOException> supplier = Spied.checkedDoubleSupplier(() -> {
                    throw new IOException("foo");
                });

                CheckedToDoubleFunction<IOException, ExecutionException> errorHandler = Spied.checkedToDoubleFunction(e -> {
                    throw new ExecutionException(e);
                });

                CheckedDoubleSupplier<ExecutionException> handling = supplier.onErrorHandleChecked(errorHandler);

                ExecutionException thrown = assertThrows(ExecutionException.class, handling::getAsDouble);
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());

                verify(supplier).getAsDouble();
                verify(supplier).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsDouble(any());
                verifyNoMoreInteractions(supplier, errorHandler);
            }

            @Test
            void testHandlerThrowsUnchecked() throws IOException, ExecutionException {
                CheckedDoubleSupplier<IOException> supplier = Spied.checkedDoubleSupplier(() -> {
                    throw new IOException("foo");
                });

                CheckedToDoubleFunction<IOException, ExecutionException> errorHandler = Spied.checkedToDoubleFunction(e -> {
                    throw new IllegalStateException(e);
                });

                CheckedDoubleSupplier<ExecutionException> handling = supplier.onErrorHandleChecked(errorHandler);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, handling::getAsDouble);
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());

                verify(supplier).getAsDouble();
                verify(supplier).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsDouble(any());
                verifyNoMoreInteractions(supplier, errorHandler);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedDoubleSupplier<IOException> supplier = Spied.checkedDoubleSupplier(() -> {
                throw new IllegalStateException("foo");
            });

            CheckedToDoubleFunction<IOException, ExecutionException> errorHandler = Spied.checkedToDoubleFunction(e -> 0D);

            CheckedDoubleSupplier<ExecutionException> handling = supplier.onErrorHandleChecked(errorHandler);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, handling::getAsDouble);
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
            CheckedDoubleSupplier<IOException> supplier = Spied.checkedDoubleSupplier(() -> 1D);

            assertThrows(NullPointerException.class, () -> supplier.onErrorHandleUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedDoubleSupplier<IOException> supplier = Spied.checkedDoubleSupplier(() -> 1D);

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
                CheckedDoubleSupplier<IOException> supplier = Spied.checkedDoubleSupplier(() -> {
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

            @Test
            void testHandlerThrowsUnchecked() throws IOException {
                CheckedDoubleSupplier<IOException> supplier = Spied.checkedDoubleSupplier(() -> {
                    throw new IOException("foo");
                });

                ToDoubleFunction<IOException> errorHandler = Spied.toDoubleFunction(e -> {
                    throw new IllegalStateException(e);
                });

                DoubleSupplier handling = supplier.onErrorHandleUnchecked(errorHandler);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, handling::getAsDouble);
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());

                verify(supplier).getAsDouble();
                verify(supplier).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).applyAsDouble(any());
                verifyNoMoreInteractions(supplier, errorHandler);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedDoubleSupplier<IOException> supplier = Spied.checkedDoubleSupplier(() -> {
                throw new IllegalStateException("foo");
            });

            ToDoubleFunction<IOException> errorHandler = Spied.toDoubleFunction(e -> 0D);

            DoubleSupplier handling = supplier.onErrorHandleUnchecked(errorHandler);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, handling::getAsDouble);
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
            CheckedDoubleSupplier<IOException> supplier = Spied.checkedDoubleSupplier(() -> 1D);

            assertThrows(NullPointerException.class, () -> supplier.onErrorGetCheckedAsDouble(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedDoubleSupplier<IOException> supplier = Spied.checkedDoubleSupplier(() -> 1D);

            CheckedDoubleSupplier<ExecutionException> fallback = Spied.checkedDoubleSupplier(() -> 0D);

            CheckedDoubleSupplier<ExecutionException> getting = supplier.onErrorGetCheckedAsDouble(fallback);

            assertEquals(1D, getting.getAsDouble());

            verify(supplier).getAsDouble();
            verify(supplier).onErrorGetCheckedAsDouble(fallback);
            verifyNoMoreInteractions(supplier, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                CheckedDoubleSupplier<IOException> supplier = Spied.checkedDoubleSupplier(() -> {
                    throw new IOException("foo");
                });

                CheckedDoubleSupplier<ParseException> fallback = Spied.checkedDoubleSupplier(() -> 0D);

                CheckedDoubleSupplier<ParseException> getting = supplier.onErrorGetCheckedAsDouble(fallback);

                assertEquals(0D, getting.getAsDouble());

                verify(supplier).getAsDouble();
                verify(supplier).onErrorGetCheckedAsDouble(fallback);
                verify(fallback).getAsDouble();
                verifyNoMoreInteractions(supplier, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                CheckedDoubleSupplier<IOException> supplier = Spied.checkedDoubleSupplier(() -> {
                    throw new IOException("foo");
                });

                CheckedDoubleSupplier<ParseException> fallback = Spied.checkedDoubleSupplier(() -> {
                    throw new ParseException("bar", 0);
                });

                CheckedDoubleSupplier<ParseException> getting = supplier.onErrorGetCheckedAsDouble(fallback);

                ParseException thrown = assertThrows(ParseException.class, getting::getAsDouble);
                assertEquals("bar", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(supplier).getAsDouble();
                verify(supplier).onErrorGetCheckedAsDouble(fallback);
                verify(fallback).getAsDouble();
                verifyNoMoreInteractions(supplier, fallback);
            }

            @Test
            void testFallbackThrowsUnchecked() throws IOException, ParseException {
                CheckedDoubleSupplier<IOException> supplier = Spied.checkedDoubleSupplier(() -> {
                    throw new IOException("foo");
                });

                CheckedDoubleSupplier<ParseException> fallback = Spied.checkedDoubleSupplier(() -> {
                    throw new IllegalStateException("bar");
                });

                CheckedDoubleSupplier<ParseException> getting = supplier.onErrorGetCheckedAsDouble(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, getting::getAsDouble);
                assertEquals("bar", thrown.getMessage());

                verify(supplier).getAsDouble();
                verify(supplier).onErrorGetCheckedAsDouble(fallback);
                verify(fallback).getAsDouble();
                verifyNoMoreInteractions(supplier, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedDoubleSupplier<IOException> supplier = Spied.checkedDoubleSupplier(() -> {
                throw new IllegalStateException("foo");
            });

            CheckedDoubleSupplier<ParseException> fallback = Spied.checkedDoubleSupplier(() -> 0D);

            CheckedDoubleSupplier<ParseException> getting = supplier.onErrorGetCheckedAsDouble(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, getting::getAsDouble);
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
            CheckedDoubleSupplier<IOException> supplier = Spied.checkedDoubleSupplier(() -> 1D);

            assertThrows(NullPointerException.class, () -> supplier.onErrorGetUncheckedAsDouble(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedDoubleSupplier<IOException> supplier = Spied.checkedDoubleSupplier(() -> 1D);

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
                CheckedDoubleSupplier<IOException> supplier = Spied.checkedDoubleSupplier(() -> {
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

            @Test
            void testFallbackThrowsUnchecked() throws IOException {
                CheckedDoubleSupplier<IOException> supplier = Spied.checkedDoubleSupplier(() -> {
                    throw new IOException("foo");
                });

                DoubleSupplier fallback = Spied.doubleSupplier(() -> {
                    throw new IllegalStateException("bar");
                });

                DoubleSupplier getting = supplier.onErrorGetUncheckedAsDouble(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, getting::getAsDouble);
                assertEquals("bar", thrown.getMessage());

                verify(supplier).getAsDouble();
                verify(supplier).onErrorGetUncheckedAsDouble(fallback);
                verify(fallback).getAsDouble();
                verifyNoMoreInteractions(supplier, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedDoubleSupplier<IOException> supplier = Spied.checkedDoubleSupplier(() -> {
                throw new IllegalStateException("foo");
            });

            DoubleSupplier fallback = Spied.doubleSupplier(() -> 0D);

            DoubleSupplier getting = supplier.onErrorGetUncheckedAsDouble(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, getting::getAsDouble);
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
            CheckedDoubleSupplier<IOException> supplier = Spied.checkedDoubleSupplier(() -> 1D);

            DoubleSupplier returning = supplier.onErrorReturn(0D);

            assertEquals(1D, returning.getAsDouble());

            verify(supplier).getAsDouble();
            verify(supplier).onErrorReturn(0D);
            verifyNoMoreInteractions(supplier);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedDoubleSupplier<IOException> supplier = Spied.checkedDoubleSupplier(() -> {
                throw new IOException("foo");
            });

            DoubleSupplier returning = supplier.onErrorReturn(0D);

            assertEquals(0D, returning.getAsDouble());

            verify(supplier).getAsDouble();
            verify(supplier).onErrorReturn(0D);
            verifyNoMoreInteractions(supplier);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedDoubleSupplier<IOException> supplier = Spied.checkedDoubleSupplier(() -> {
                throw new IllegalStateException("foo");
            });

            DoubleSupplier returning = supplier.onErrorReturn(0D);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, returning::getAsDouble);
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
            CheckedDoubleSupplier<IOException> supplier = Spied.checkedDoubleSupplier(() -> 1D);

            DoubleSupplier unchecked = supplier.unchecked();

            assertEquals(1D, unchecked.getAsDouble());

            verify(supplier).getAsDouble();
            verify(supplier).unchecked();
            verify(supplier).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(supplier);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedDoubleSupplier<IOException> supplier = Spied.checkedDoubleSupplier(() -> {
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

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedDoubleSupplier<IOException> supplier = Spied.checkedDoubleSupplier(() -> {
                throw new IllegalArgumentException("foo");
            });

            DoubleSupplier unchecked = supplier.unchecked();

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, unchecked::getAsDouble);
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
            assertThrows(NullPointerException.class, () -> CheckedDoubleSupplier.of(null));
        }

        @Test
        void testNonNullArgument() throws IOException {
            CheckedDoubleSupplier<IOException> supplier = CheckedDoubleSupplier.of(() -> 1D);

            assertEquals(1D, supplier.getAsDouble());
        }
    }

    @Nested
    class UncheckedFactory {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> CheckedDoubleSupplier.unchecked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedDoubleSupplier<IOException> supplier = Spied.checkedDoubleSupplier(() -> 1D);

            DoubleSupplier unchecked = CheckedDoubleSupplier.unchecked(supplier);

            assertEquals(1D, unchecked.getAsDouble());

            verify(supplier).getAsDouble();
            verifyNoMoreInteractions(supplier);
        }

        @Test
        void testArgumentThrowsChecked() throws IOException {
            CheckedDoubleSupplier<IOException> supplier = Spied.checkedDoubleSupplier(() -> {
                throw new IOException("foo");
            });

            DoubleSupplier unchecked = CheckedDoubleSupplier.unchecked(supplier);

            UncheckedException thrown = assertThrows(UncheckedException.class, unchecked::getAsDouble);
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo", cause.getMessage());

            verify(supplier).getAsDouble();
            verifyNoMoreInteractions(supplier);
        }

        @Test
        void testArgumentThrowsUnchecked() throws IOException {
            CheckedDoubleSupplier<IOException> supplier = Spied.checkedDoubleSupplier(() -> {
                throw new IllegalArgumentException("foo");
            });

            DoubleSupplier unchecked = CheckedDoubleSupplier.unchecked(supplier);

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, unchecked::getAsDouble);
            assertEquals("foo", thrown.getMessage());

            verify(supplier).getAsDouble();
            verifyNoMoreInteractions(supplier);
        }
    }

    @Nested
    class CheckedWrapper {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> CheckedDoubleSupplier.checked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedDoubleSupplier<IOException> supplier = CheckedDoubleSupplier.checked(() -> 1D);

            assertEquals(1D, supplier.getAsDouble());
        }

        @Test
        void testargumentThrowsUnchecked() {
            CheckedDoubleSupplier<IOException> supplier = CheckedDoubleSupplier.checked(() -> {
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

            assertThrows(NullPointerException.class, () -> CheckedDoubleSupplier.checked(null, IOException.class));
            assertThrows(NullPointerException.class, () -> CheckedDoubleSupplier.checked(supplier, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedDoubleSupplier<IOException> supplier = CheckedDoubleSupplier.checked(() -> 1D, IOException.class);

            assertEquals(1D, supplier.getAsDouble());
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                CheckedDoubleSupplier<IOException> supplier = CheckedDoubleSupplier.checked(() -> {
                    throw new UncheckedException(new IOException("foo"));
                }, IOException.class);

                IOException thrown = assertThrows(IOException.class, supplier::getAsDouble);
                assertEquals("foo", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                CheckedDoubleSupplier<IOException> supplier = CheckedDoubleSupplier.checked(() -> {
                    throw new UncheckedException(new FileNotFoundException("foo"));
                }, IOException.class);

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class, supplier::getAsDouble);
                assertEquals("foo", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                CheckedDoubleSupplier<IOException> supplier = CheckedDoubleSupplier.checked(() -> {
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
            CheckedDoubleSupplier<IOException> supplier = CheckedDoubleSupplier.checked(() -> {
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

            assertThrows(NullPointerException.class, () -> CheckedDoubleSupplier.invokeAndUnwrap(null, IOException.class));
            assertThrows(NullPointerException.class, () -> CheckedDoubleSupplier.invokeAndUnwrap(supplier, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            DoubleSupplier supplier = () -> 1D;

            assertEquals(1D, CheckedDoubleSupplier.invokeAndUnwrap(supplier, IOException.class));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                DoubleSupplier supplier = () -> {
                    throw new UncheckedException(new IOException("foo"));
                };

                IOException thrown = assertThrows(IOException.class, () -> CheckedDoubleSupplier.invokeAndUnwrap(supplier, IOException.class));
                assertEquals("foo", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                DoubleSupplier supplier = () -> {
                    throw new UncheckedException(new FileNotFoundException("foo"));
                };

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class,
                        () -> CheckedDoubleSupplier.invokeAndUnwrap(supplier, IOException.class));
                assertEquals("foo", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                DoubleSupplier supplier = () -> {
                    throw new UncheckedException(new ParseException("foo", 0));
                };

                UncheckedException thrown = assertThrows(UncheckedException.class,
                        () -> CheckedDoubleSupplier.invokeAndUnwrap(supplier, IOException.class));
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
                    () -> CheckedDoubleSupplier.invokeAndUnwrap(supplier, IOException.class));
            assertEquals("foo", thrown.getMessage());
        }
    }
}
