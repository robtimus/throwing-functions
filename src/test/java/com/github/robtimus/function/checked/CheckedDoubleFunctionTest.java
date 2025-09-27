/*
 * CheckedDoubleFunctionTest.java
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
import java.util.function.DoubleFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("nls")
class CheckedDoubleFunctionTest {

    @Nested
    class OnErrorThrowAsChecked {

        @Test
        void testNullArgument() {
            CheckedDoubleFunction<String, IOException> function = Spied.checkedDoubleFunction(Double::toString);

            assertThrows(NullPointerException.class, () -> function.onErrorThrowAsChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedDoubleFunction<String, IOException> function = Spied.checkedDoubleFunction(Double::toString);

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedDoubleFunction<String, ExecutionException> throwing = function.onErrorThrowAsChecked(errorMapper);

            assertEquals("1.0", throwing.apply(1D));

            verify(function).apply(1D);
            verify(function).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedDoubleFunction<String, IOException> function = Spied.checkedDoubleFunction(d -> {
                throw new IOException(Double.toString(d));
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedDoubleFunction<String, ExecutionException> throwing = function.onErrorThrowAsChecked(errorMapper);

            ExecutionException thrown = assertThrows(ExecutionException.class, () -> throwing.apply(1D));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1.0", cause.getMessage());

            verify(function).apply(1D);
            verify(function).onErrorThrowAsChecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(function, errorMapper);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedDoubleFunction<String, IOException> function = Spied.checkedDoubleFunction(d -> {
                throw new IllegalStateException(Double.toString(d));
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedDoubleFunction<String, ExecutionException> throwing = function.onErrorThrowAsChecked(errorMapper);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> throwing.apply(1D));
            assertEquals("1.0", thrown.getMessage());

            verify(function).apply(1D);
            verify(function).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }
    }

    @Nested
    class OnErrorThrowAsUnchecked {

        @Test
        void testNullArgument() {
            CheckedDoubleFunction<String, IOException> function = Spied.checkedDoubleFunction(Double::toString);

            assertThrows(NullPointerException.class, () -> function.onErrorThrowAsUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedDoubleFunction<String, IOException> function = Spied.checkedDoubleFunction(Double::toString);

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            DoubleFunction<String> throwing = function.onErrorThrowAsUnchecked(errorMapper);

            assertEquals("1.0", throwing.apply(1D));

            verify(function).apply(1D);
            verify(function).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedDoubleFunction<String, IOException> function = Spied.checkedDoubleFunction(d -> {
                throw new IOException(Double.toString(d));
            });

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            DoubleFunction<String> throwing = function.onErrorThrowAsUnchecked(errorMapper);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> throwing.apply(1D));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1.0", cause.getMessage());

            verify(function).apply(1D);
            verify(function).onErrorThrowAsUnchecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(function, errorMapper);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedDoubleFunction<String, IOException> function = Spied.checkedDoubleFunction(d -> {
                throw new IllegalArgumentException(Double.toString(d));
            });

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            DoubleFunction<String> throwing = function.onErrorThrowAsUnchecked(errorMapper);

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> throwing.apply(1D));
            assertEquals("1.0", thrown.getMessage());

            verify(function).apply(1D);
            verify(function).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }
    }

    @Nested
    class OnErrorHandleChecked {

        @Test
        void testNullArgument() {
            CheckedDoubleFunction<String, IOException> function = Spied.checkedDoubleFunction(Double::toString);

            assertThrows(NullPointerException.class, () -> function.onErrorHandleChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedDoubleFunction<String, IOException> function = Spied.checkedDoubleFunction(Double::toString);

            CheckedFunction<IOException, String, ExecutionException> errorHandler = Spied.checkedFunction(Exception::getMessage);

            CheckedDoubleFunction<String, ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

            assertEquals("1.0", handling.apply(1D));

            verify(function).apply(1D);
            verify(function).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException, ExecutionException {
                CheckedDoubleFunction<String, IOException> function = Spied.checkedDoubleFunction(d -> {
                    throw new IOException(Double.toString(d));
                });

                CheckedFunction<IOException, String, ExecutionException> errorHandler = Spied.checkedFunction(Exception::getMessage);

                CheckedDoubleFunction<String, ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

                assertEquals("1.0", handling.apply(1D));

                verify(function).apply(1D);
                verify(function).onErrorHandleChecked(errorHandler);
                verify(errorHandler).apply(any());
                verifyNoMoreInteractions(function, errorHandler);
            }

            @Test
            void testHandlerThrowsChecked() throws IOException, ExecutionException {
                CheckedDoubleFunction<String, IOException> function = Spied.checkedDoubleFunction(d -> {
                    throw new IOException(Double.toString(d));
                });

                CheckedFunction<IOException, String, ExecutionException> errorHandler = Spied.checkedFunction(e -> {
                    throw new ExecutionException(e);
                });

                CheckedDoubleFunction<String, ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

                ExecutionException thrown = assertThrows(ExecutionException.class, () -> handling.apply(1D));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("1.0", cause.getMessage());

                verify(function).apply(1D);
                verify(function).onErrorHandleChecked(errorHandler);
                verify(errorHandler).apply(any());
                verifyNoMoreInteractions(function, errorHandler);
            }

            @Test
            void testHandlerThrowsUnchecked() throws IOException, ExecutionException {
                CheckedDoubleFunction<String, IOException> function = Spied.checkedDoubleFunction(d -> {
                    throw new IOException(Double.toString(d));
                });

                CheckedFunction<IOException, String, ExecutionException> errorHandler = Spied.checkedFunction(e -> {
                    throw new IllegalStateException(e);
                });

                CheckedDoubleFunction<String, ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.apply(1D));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("1.0", cause.getMessage());

                verify(function).apply(1D);
                verify(function).onErrorHandleChecked(errorHandler);
                verify(errorHandler).apply(any());
                verifyNoMoreInteractions(function, errorHandler);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedDoubleFunction<String, IOException> function = Spied.checkedDoubleFunction(d -> {
                throw new IllegalStateException(Double.toString(d));
            });

            CheckedFunction<IOException, String, ExecutionException> errorHandler = Spied.checkedFunction(Exception::getMessage);

            CheckedDoubleFunction<String, ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.apply(1D));
            assertEquals("1.0", thrown.getMessage());

            verify(function).apply(1D);
            verify(function).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }
    }

    @Nested
    class OnErrorHandleUnchecked {

        @Test
        void testNullArgument() {
            CheckedDoubleFunction<String, IOException> function = Spied.checkedDoubleFunction(Double::toString);

            assertThrows(NullPointerException.class, () -> function.onErrorHandleUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedDoubleFunction<String, IOException> function = Spied.checkedDoubleFunction(Double::toString);

            Function<IOException, String> errorHandler = Spied.function(Exception::getMessage);

            DoubleFunction<String> handling = function.onErrorHandleUnchecked(errorHandler);

            assertEquals("1.0", handling.apply(1D));

            verify(function).apply(1D);
            verify(function).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException {
                CheckedDoubleFunction<String, IOException> function = Spied.checkedDoubleFunction(d -> {
                    throw new IOException(Double.toString(d));
                });

                Function<IOException, String> errorHandler = Spied.function(Exception::getMessage);

                DoubleFunction<String> handling = function.onErrorHandleUnchecked(errorHandler);

                assertEquals("1.0", handling.apply(1D));

                verify(function).apply(1D);
                verify(function).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).apply(any());
                verifyNoMoreInteractions(function, errorHandler);
            }

            @Test
            void testHandlerThrowsUnchecked() throws IOException {
                CheckedDoubleFunction<String, IOException> function = Spied.checkedDoubleFunction(d -> {
                    throw new IOException(Double.toString(d));
                });

                Function<IOException, String> errorHandler = Spied.function(e -> {
                    throw new IllegalStateException(e);
                });

                DoubleFunction<String> handling = function.onErrorHandleUnchecked(errorHandler);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.apply(1D));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("1.0", cause.getMessage());

                verify(function).apply(1D);
                verify(function).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).apply(any());
                verifyNoMoreInteractions(function, errorHandler);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedDoubleFunction<String, IOException> function = Spied.checkedDoubleFunction(d -> {
                throw new IllegalStateException(Double.toString(d));
            });

            Function<IOException, String> errorHandler = Spied.function(Exception::getMessage);

            DoubleFunction<String> handling = function.onErrorHandleUnchecked(errorHandler);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.apply(1D));
            assertEquals("1.0", thrown.getMessage());

            verify(function).apply(1D);
            verify(function).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }
    }

    @Nested
    class OnErrorApplyChecked {

        @Test
        void testNullArgument() {
            CheckedDoubleFunction<String, IOException> function = Spied.checkedDoubleFunction(Double::toString);

            assertThrows(NullPointerException.class, () -> function.onErrorApplyChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedDoubleFunction<String, IOException> function = Spied.checkedDoubleFunction(Double::toString);

            CheckedDoubleFunction<String, ExecutionException> fallback = Spied.checkedDoubleFunction(d -> Double.toString(d + d));

            CheckedDoubleFunction<String, ExecutionException> applying = function.onErrorApplyChecked(fallback);

            assertEquals("1.0", applying.apply(1D));

            verify(function).apply(1D);
            verify(function).onErrorApplyChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                CheckedDoubleFunction<String, IOException> function = Spied.checkedDoubleFunction(d -> {
                    throw new IOException(Double.toString(d));
                });

                CheckedDoubleFunction<String, ParseException> fallback = Spied.checkedDoubleFunction(d -> Double.toString(d + d));

                CheckedDoubleFunction<String, ParseException> applying = function.onErrorApplyChecked(fallback);

                assertEquals("2.0", applying.apply(1D));

                verify(function).apply(1D);
                verify(function).onErrorApplyChecked(fallback);
                verify(fallback).apply(1D);
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                CheckedDoubleFunction<String, IOException> function = Spied.checkedDoubleFunction(d -> {
                    throw new IOException(Double.toString(d));
                });

                CheckedDoubleFunction<String, ParseException> fallback = Spied.checkedDoubleFunction(d -> {
                    throw new ParseException(Double.toString(d), 0);
                });

                CheckedDoubleFunction<String, ParseException> applying = function.onErrorApplyChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> applying.apply(1D));
                assertEquals("1.0", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(function).apply(1D);
                verify(function).onErrorApplyChecked(fallback);
                verify(fallback).apply(1D);
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsUnchecked() throws IOException, ParseException {
                CheckedDoubleFunction<String, IOException> function = Spied.checkedDoubleFunction(d -> {
                    throw new IOException(Double.toString(d));
                });

                CheckedDoubleFunction<String, ParseException> fallback = Spied.checkedDoubleFunction(d -> {
                    throw new IllegalStateException(Double.toString(d));
                });

                CheckedDoubleFunction<String, ParseException> applying = function.onErrorApplyChecked(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> applying.apply(1D));
                assertEquals("1.0", thrown.getMessage());

                verify(function).apply(1D);
                verify(function).onErrorApplyChecked(fallback);
                verify(fallback).apply(1D);
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedDoubleFunction<String, IOException> function = Spied.checkedDoubleFunction(d -> {
                throw new IllegalStateException(Double.toString(d));
            });

            CheckedDoubleFunction<String, ParseException> fallback = Spied.checkedDoubleFunction(d -> Double.toString(d + d));

            CheckedDoubleFunction<String, ParseException> applying = function.onErrorApplyChecked(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> applying.apply(1D));
            assertEquals("1.0", thrown.getMessage());

            verify(function).apply(1D);
            verify(function).onErrorApplyChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorApplyUnchecked {

        @Test
        void testNullArgument() {
            CheckedDoubleFunction<String, IOException> function = Spied.checkedDoubleFunction(Double::toString);

            assertThrows(NullPointerException.class, () -> function.onErrorApplyUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedDoubleFunction<String, IOException> function = Spied.checkedDoubleFunction(Double::toString);

            DoubleFunction<String> fallback = Spied.doubleFunction(d -> Double.toString(d + d));

            DoubleFunction<String> applying = function.onErrorApplyUnchecked(fallback);

            assertEquals("1.0", applying.apply(1D));

            verify(function).apply(1D);
            verify(function).onErrorApplyUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                CheckedDoubleFunction<String, IOException> function = Spied.checkedDoubleFunction(d -> {
                    throw new IOException(Double.toString(d));
                });

                DoubleFunction<String> fallback = Spied.doubleFunction(d -> Double.toString(d + d));

                DoubleFunction<String> applying = function.onErrorApplyUnchecked(fallback);

                assertEquals("2.0", applying.apply(1D));

                verify(function).apply(1D);
                verify(function).onErrorApplyUnchecked(fallback);
                verify(fallback).apply(1D);
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsUnchecked() throws IOException {
                CheckedDoubleFunction<String, IOException> function = Spied.checkedDoubleFunction(d -> {
                    throw new IOException(Double.toString(d));
                });

                DoubleFunction<String> fallback = Spied.doubleFunction(d -> {
                    throw new IllegalStateException(Double.toString(d));
                });

                DoubleFunction<String> applying = function.onErrorApplyUnchecked(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> applying.apply(1D));
                assertEquals("1.0", thrown.getMessage());

                verify(function).apply(1D);
                verify(function).onErrorApplyUnchecked(fallback);
                verify(fallback).apply(1D);
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedDoubleFunction<String, IOException> function = Spied.checkedDoubleFunction(d -> {
                throw new IllegalStateException(Double.toString(d));
            });

            DoubleFunction<String> fallback = Spied.doubleFunction(d -> Double.toString(d + d));

            DoubleFunction<String> applying = function.onErrorApplyUnchecked(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> applying.apply(1D));
            assertEquals("1.0", thrown.getMessage());

            verify(function).apply(1D);
            verify(function).onErrorApplyUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorGetChecked {

        @Test
        void testNullArgument() {
            CheckedDoubleFunction<String, IOException> function = Spied.checkedDoubleFunction(Double::toString);

            assertThrows(NullPointerException.class, () -> function.onErrorGetChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedDoubleFunction<String, IOException> function = Spied.checkedDoubleFunction(Double::toString);

            CheckedSupplier<String, ExecutionException> fallback = Spied.checkedSupplier(() -> "bar");

            CheckedDoubleFunction<String, ExecutionException> getting = function.onErrorGetChecked(fallback);

            assertEquals("1.0", getting.apply(1D));

            verify(function).apply(1D);
            verify(function).onErrorGetChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                CheckedDoubleFunction<String, IOException> function = Spied.checkedDoubleFunction(d -> {
                    throw new IOException(Double.toString(d));
                });

                CheckedSupplier<String, ParseException> fallback = Spied.checkedSupplier(() -> "bar");

                CheckedDoubleFunction<String, ParseException> getting = function.onErrorGetChecked(fallback);

                assertEquals("bar", getting.apply(1D));

                verify(function).apply(1D);
                verify(function).onErrorGetChecked(fallback);
                verify(fallback).get();
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                CheckedDoubleFunction<String, IOException> function = Spied.checkedDoubleFunction(d -> {
                    throw new IOException(Double.toString(d));
                });

                CheckedSupplier<String, ParseException> fallback = Spied.checkedSupplier(() -> {
                    throw new ParseException("bar", 0);
                });

                CheckedDoubleFunction<String, ParseException> getting = function.onErrorGetChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> getting.apply(1D));
                assertEquals("bar", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(function).apply(1D);
                verify(function).onErrorGetChecked(fallback);
                verify(fallback).get();
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsUnchecked() throws IOException, ParseException {
                CheckedDoubleFunction<String, IOException> function = Spied.checkedDoubleFunction(d -> {
                    throw new IOException(Double.toString(d));
                });

                CheckedSupplier<String, ParseException> fallback = Spied.checkedSupplier(() -> {
                    throw new IllegalStateException("bar");
                });

                CheckedDoubleFunction<String, ParseException> getting = function.onErrorGetChecked(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> getting.apply(1D));
                assertEquals("bar", thrown.getMessage());

                verify(function).apply(1D);
                verify(function).onErrorGetChecked(fallback);
                verify(fallback).get();
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedDoubleFunction<String, IOException> function = Spied.checkedDoubleFunction(d -> {
                throw new IllegalStateException(Double.toString(d));
            });

            CheckedSupplier<String, ParseException> fallback = Spied.checkedSupplier(() -> "bar");

            CheckedDoubleFunction<String, ParseException> getting = function.onErrorGetChecked(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> getting.apply(1D));
            assertEquals("1.0", thrown.getMessage());

            verify(function).apply(1D);
            verify(function).onErrorGetChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorGetUnchecked {

        @Test
        void testNullArgument() {
            CheckedDoubleFunction<String, IOException> function = Spied.checkedDoubleFunction(Double::toString);

            assertThrows(NullPointerException.class, () -> function.onErrorGetUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedDoubleFunction<String, IOException> function = Spied.checkedDoubleFunction(Double::toString);

            Supplier<String> fallback = Spied.supplier(() -> "bar");

            DoubleFunction<String> getting = function.onErrorGetUnchecked(fallback);

            assertEquals("1.0", getting.apply(1D));

            verify(function).apply(1D);
            verify(function).onErrorGetUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                CheckedDoubleFunction<String, IOException> function = Spied.checkedDoubleFunction(d -> {
                    throw new IOException(Double.toString(d));
                });

                Supplier<String> fallback = Spied.supplier(() -> "bar");

                DoubleFunction<String> getting = function.onErrorGetUnchecked(fallback);

                assertEquals("bar", getting.apply(1D));

                verify(function).apply(1D);
                verify(function).onErrorGetUnchecked(fallback);
                verify(fallback).get();
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsUnchecked() throws IOException {
                CheckedDoubleFunction<String, IOException> function = Spied.checkedDoubleFunction(d -> {
                    throw new IOException(Double.toString(d));
                });

                Supplier<String> fallback = Spied.supplier(() -> {
                    throw new IllegalStateException("bar");
                });

                DoubleFunction<String> getting = function.onErrorGetUnchecked(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> getting.apply(1D));
                assertEquals("bar", thrown.getMessage());

                verify(function).apply(1D);
                verify(function).onErrorGetUnchecked(fallback);
                verify(fallback).get();
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedDoubleFunction<String, IOException> function = Spied.checkedDoubleFunction(d -> {
                throw new IllegalStateException(Double.toString(d));
            });

            Supplier<String> fallback = Spied.supplier(() -> "bar");

            DoubleFunction<String> getting = function.onErrorGetUnchecked(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> getting.apply(1D));
            assertEquals("1.0", thrown.getMessage());

            verify(function).apply(1D);
            verify(function).onErrorGetUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorReturn {

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedDoubleFunction<String, IOException> function = Spied.checkedDoubleFunction(Double::toString);

            DoubleFunction<String> returning = function.onErrorReturn("bar");

            assertEquals("1.0", returning.apply(1D));

            verify(function).apply(1D);
            verify(function).onErrorReturn("bar");
            verifyNoMoreInteractions(function);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedDoubleFunction<String, IOException> function = Spied.checkedDoubleFunction(d -> {
                throw new IOException(Double.toString(d));
            });

            DoubleFunction<String> returning = function.onErrorReturn("bar");

            assertEquals("bar", returning.apply(1D));

            verify(function).apply(1D);
            verify(function).onErrorReturn("bar");
            verifyNoMoreInteractions(function);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedDoubleFunction<String, IOException> function = Spied.checkedDoubleFunction(d -> {
                throw new IllegalStateException(Double.toString(d));
            });

            DoubleFunction<String> returning = function.onErrorReturn("bar");

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> returning.apply(1D));
            assertEquals("1.0", thrown.getMessage());

            verify(function).apply(1D);
            verify(function).onErrorReturn("bar");
            verifyNoMoreInteractions(function);
        }
    }

    @Nested
    class Unchecked {

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedDoubleFunction<String, IOException> function = Spied.checkedDoubleFunction(Double::toString);

            DoubleFunction<String> unchecked = function.unchecked();

            assertEquals("1.0", unchecked.apply(1D));

            verify(function).apply(1D);
            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(function);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedDoubleFunction<String, IOException> function = Spied.checkedDoubleFunction(d -> {
                throw new IOException(Double.toString(d));
            });

            DoubleFunction<String> unchecked = function.unchecked();

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.apply(1D));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1.0", cause.getMessage());

            verify(function).apply(1D);
            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(function);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedDoubleFunction<String, IOException> function = Spied.checkedDoubleFunction(d -> {
                throw new IllegalArgumentException(Double.toString(d));
            });

            DoubleFunction<String> unchecked = function.unchecked();

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> unchecked.apply(1D));
            assertEquals("1.0", thrown.getMessage());

            verify(function).apply(1D);
            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(function);
        }
    }

    @Nested
    class Of {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> CheckedDoubleFunction.of(null));
        }

        @Test
        void testNonNullArgument() throws IOException {
            CheckedDoubleFunction<String, IOException> function = CheckedDoubleFunction.of(Double::toString);

            assertEquals("1.0", function.apply(1D));
        }
    }

    @Nested
    class UncheckedFactory {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> CheckedDoubleFunction.unchecked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedDoubleFunction<String, IOException> function = Spied.checkedDoubleFunction(Double::toString);

            DoubleFunction<String> unchecked = CheckedDoubleFunction.unchecked(function);

            assertEquals("1.0", unchecked.apply(1D));

            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verify(function).apply(1D);
            verifyNoMoreInteractions(function);
        }

        @Test
        void testArgumentThrowsChecked() throws IOException {
            CheckedDoubleFunction<String, IOException> function = Spied.checkedDoubleFunction(d -> {
                throw new IOException(Double.toString(d));
            });

            DoubleFunction<String> unchecked = CheckedDoubleFunction.unchecked(function);

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.apply(1D));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1.0", cause.getMessage());

            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verify(function).apply(1D);
            verifyNoMoreInteractions(function);
        }

        @Test
        void testArgumentThrowsUnchecked() throws IOException {
            CheckedDoubleFunction<String, IOException> function = Spied.checkedDoubleFunction(d -> {
                throw new IllegalArgumentException(Double.toString(d));
            });

            DoubleFunction<String> unchecked = CheckedDoubleFunction.unchecked(function);

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> unchecked.apply(1D));
            assertEquals("1.0", thrown.getMessage());

            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verify(function).apply(1D);
            verifyNoMoreInteractions(function);
        }
    }

    @Nested
    class CheckedWrapper {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> CheckedDoubleFunction.checked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedDoubleFunction<String, IOException> function = CheckedDoubleFunction.checked(Double::toString);

            assertEquals("1.0", function.apply(1D));
        }

        @Test
        void testArgumentThrowsUnchecked() {
            CheckedDoubleFunction<String, IOException> function = CheckedDoubleFunction.checked(d -> {
                throw new UncheckedException(Double.toString(d), new IOException());
            });

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> function.apply(1D));
            assertEquals("1.0", thrown.getMessage());
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertNull(cause.getMessage());
        }
    }

    @Nested
    class CheckedUnwrapper {

        @Test
        void testNullArguments() {
            DoubleFunction<String> function = Double::toString;

            assertThrows(NullPointerException.class, () -> CheckedDoubleFunction.checked(null, IOException.class));
            assertThrows(NullPointerException.class, () -> CheckedDoubleFunction.checked(function, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedDoubleFunction<String, IOException> function = CheckedDoubleFunction.checked(Double::toString, IOException.class);

            assertEquals("1.0", function.apply(1D));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                CheckedDoubleFunction<String, IOException> function = CheckedDoubleFunction.checked(d -> {
                    throw new UncheckedException(new IOException(Double.toString(d)));
                }, IOException.class);

                IOException thrown = assertThrows(IOException.class, () -> function.apply(1D));
                assertEquals("1.0", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                CheckedDoubleFunction<String, IOException> function = CheckedDoubleFunction.checked(d -> {
                    throw new UncheckedException(new FileNotFoundException(Double.toString(d)));
                }, IOException.class);

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class, () -> function.apply(1D));
                assertEquals("1.0", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                CheckedDoubleFunction<String, IOException> function = CheckedDoubleFunction.checked(d -> {
                    throw new UncheckedException(new ParseException(Double.toString(d), 0));
                }, IOException.class);

                UncheckedException thrown = assertThrows(UncheckedException.class, () -> function.apply(1D));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("1.0", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            CheckedDoubleFunction<String, IOException> function = CheckedDoubleFunction.checked(d -> {
                throw new IllegalStateException(Double.toString(d));
            }, IOException.class);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> function.apply(1D));
            assertEquals("1.0", thrown.getMessage());
        }
    }

    @Nested
    class InvokeAndUnwrap {

        @Test
        void testNullArguments() {
            DoubleFunction<String> function = d -> String.valueOf(Double.toString(d)).toUpperCase();

            assertThrows(NullPointerException.class, () -> CheckedDoubleFunction.invokeAndUnwrap(null, 1D, IOException.class));
            assertThrows(NullPointerException.class, () -> CheckedDoubleFunction.invokeAndUnwrap(function, 1D, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            DoubleFunction<String> function = Double::toString;

            assertEquals("1.0", CheckedDoubleFunction.invokeAndUnwrap(function, 1D, IOException.class));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                DoubleFunction<String> function = d -> {
                    throw new UncheckedException(new IOException(Double.toString(d)));
                };

                IOException thrown = assertThrows(IOException.class, () -> CheckedDoubleFunction.invokeAndUnwrap(function, 1D, IOException.class));
                assertEquals("1.0", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                DoubleFunction<String> function = d -> {
                    throw new UncheckedException(new FileNotFoundException(Double.toString(d)));
                };

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class,
                        () -> CheckedDoubleFunction.invokeAndUnwrap(function, 1D, IOException.class));
                assertEquals("1.0", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                DoubleFunction<String> function = d -> {
                    throw new UncheckedException(new ParseException(Double.toString(d), 0));
                };

                UncheckedException thrown = assertThrows(UncheckedException.class,
                        () -> CheckedDoubleFunction.invokeAndUnwrap(function, 1D, IOException.class));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("1.0", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            DoubleFunction<String> function = d -> {
                throw new IllegalStateException(Double.toString(d));
            };

            IllegalStateException thrown = assertThrows(IllegalStateException.class,
                    () -> CheckedDoubleFunction.invokeAndUnwrap(function, 1D, IOException.class));
            assertEquals("1.0", thrown.getMessage());
        }
    }
}
