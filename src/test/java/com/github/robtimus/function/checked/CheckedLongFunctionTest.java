/*
 * CheckedLongFunctionTest.java
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
import java.util.function.LongFunction;
import java.util.function.Supplier;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("nls")
class CheckedLongFunctionTest {

    @Nested
    class OnErrorThrowAsChecked {

        @Test
        void testNullArgument() {
            CheckedLongFunction<String, IOException> function = Spied.checkedLongFunction(Long::toString);

            assertThrows(NullPointerException.class, () -> function.onErrorThrowAsChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedLongFunction<String, IOException> function = Spied.checkedLongFunction(Long::toString);

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedLongFunction<String, ExecutionException> throwing = function.onErrorThrowAsChecked(errorMapper);

            assertEquals("1", throwing.apply(1));

            verify(function).apply(1);
            verify(function).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedLongFunction<String, IOException> function = Spied.checkedLongFunction(d -> {
                throw new IOException(Long.toString(d));
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedLongFunction<String, ExecutionException> throwing = function.onErrorThrowAsChecked(errorMapper);

            ExecutionException thrown = assertThrows(ExecutionException.class, () -> throwing.apply(1));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1", cause.getMessage());

            verify(function).apply(1);
            verify(function).onErrorThrowAsChecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(function, errorMapper);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedLongFunction<String, IOException> function = Spied.checkedLongFunction(d -> {
                throw new IllegalStateException(Long.toString(d));
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedLongFunction<String, ExecutionException> throwing = function.onErrorThrowAsChecked(errorMapper);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> throwing.apply(1));
            assertEquals("1", thrown.getMessage());

            verify(function).apply(1);
            verify(function).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }
    }

    @Nested
    class OnErrorThrowAsUnchecked {

        @Test
        void testNullArgument() {
            CheckedLongFunction<String, IOException> function = Spied.checkedLongFunction(Long::toString);

            assertThrows(NullPointerException.class, () -> function.onErrorThrowAsUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedLongFunction<String, IOException> function = Spied.checkedLongFunction(Long::toString);

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            LongFunction<String> throwing = function.onErrorThrowAsUnchecked(errorMapper);

            assertEquals("1", throwing.apply(1));

            verify(function).apply(1);
            verify(function).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedLongFunction<String, IOException> function = Spied.checkedLongFunction(d -> {
                throw new IOException(Long.toString(d));
            });

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            LongFunction<String> throwing = function.onErrorThrowAsUnchecked(errorMapper);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> throwing.apply(1));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1", cause.getMessage());

            verify(function).apply(1);
            verify(function).onErrorThrowAsUnchecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(function, errorMapper);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedLongFunction<String, IOException> function = Spied.checkedLongFunction(d -> {
                throw new IllegalArgumentException(Long.toString(d));
            });

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            LongFunction<String> throwing = function.onErrorThrowAsUnchecked(errorMapper);

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> throwing.apply(1));
            assertEquals("1", thrown.getMessage());

            verify(function).apply(1);
            verify(function).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }
    }

    @Nested
    class OnErrorHandleChecked {

        @Test
        void testNullArgument() {
            CheckedLongFunction<String, IOException> function = Spied.checkedLongFunction(Long::toString);

            assertThrows(NullPointerException.class, () -> function.onErrorHandleChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedLongFunction<String, IOException> function = Spied.checkedLongFunction(Long::toString);

            CheckedFunction<IOException, String, ExecutionException> errorHandler = Spied.checkedFunction(Exception::getMessage);

            CheckedLongFunction<String, ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

            assertEquals("1", handling.apply(1));

            verify(function).apply(1);
            verify(function).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException, ExecutionException {
                CheckedLongFunction<String, IOException> function = Spied.checkedLongFunction(d -> {
                    throw new IOException(Long.toString(d));
                });

                CheckedFunction<IOException, String, ExecutionException> errorHandler = Spied.checkedFunction(Exception::getMessage);

                CheckedLongFunction<String, ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

                assertEquals("1", handling.apply(1));

                verify(function).apply(1);
                verify(function).onErrorHandleChecked(errorHandler);
                verify(errorHandler).apply(any());
                verifyNoMoreInteractions(function, errorHandler);
            }

            @Test
            void testHandlerThrowsChecked() throws IOException, ExecutionException {
                CheckedLongFunction<String, IOException> function = Spied.checkedLongFunction(d -> {
                    throw new IOException(Long.toString(d));
                });

                CheckedFunction<IOException, String, ExecutionException> errorHandler = Spied.checkedFunction(e -> {
                    throw new ExecutionException(e);
                });

                CheckedLongFunction<String, ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

                ExecutionException thrown = assertThrows(ExecutionException.class, () -> handling.apply(1));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("1", cause.getMessage());

                verify(function).apply(1);
                verify(function).onErrorHandleChecked(errorHandler);
                verify(errorHandler).apply(any());
                verifyNoMoreInteractions(function, errorHandler);
            }

            @Test
            void testHandlerThrowsUnchecked() throws IOException, ExecutionException {
                CheckedLongFunction<String, IOException> function = Spied.checkedLongFunction(d -> {
                    throw new IOException(Long.toString(d));
                });

                CheckedFunction<IOException, String, ExecutionException> errorHandler = Spied.checkedFunction(e -> {
                    throw new IllegalStateException(e);
                });

                CheckedLongFunction<String, ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.apply(1));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("1", cause.getMessage());

                verify(function).apply(1);
                verify(function).onErrorHandleChecked(errorHandler);
                verify(errorHandler).apply(any());
                verifyNoMoreInteractions(function, errorHandler);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedLongFunction<String, IOException> function = Spied.checkedLongFunction(d -> {
                throw new IllegalStateException(Long.toString(d));
            });

            CheckedFunction<IOException, String, ExecutionException> errorHandler = Spied.checkedFunction(Exception::getMessage);

            CheckedLongFunction<String, ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.apply(1));
            assertEquals("1", thrown.getMessage());

            verify(function).apply(1);
            verify(function).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }
    }

    @Nested
    class OnErrorHandleUnchecked {

        @Test
        void testNullArgument() {
            CheckedLongFunction<String, IOException> function = Spied.checkedLongFunction(Long::toString);

            assertThrows(NullPointerException.class, () -> function.onErrorHandleUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedLongFunction<String, IOException> function = Spied.checkedLongFunction(Long::toString);

            Function<IOException, String> errorHandler = Spied.function(Exception::getMessage);

            LongFunction<String> handling = function.onErrorHandleUnchecked(errorHandler);

            assertEquals("1", handling.apply(1));

            verify(function).apply(1);
            verify(function).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException {
                CheckedLongFunction<String, IOException> function = Spied.checkedLongFunction(d -> {
                    throw new IOException(Long.toString(d));
                });

                Function<IOException, String> errorHandler = Spied.function(Exception::getMessage);

                LongFunction<String> handling = function.onErrorHandleUnchecked(errorHandler);

                assertEquals("1", handling.apply(1));

                verify(function).apply(1);
                verify(function).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).apply(any());
                verifyNoMoreInteractions(function, errorHandler);
            }

            @Test
            void testHandlerThrowsUnchecked() throws IOException {
                CheckedLongFunction<String, IOException> function = Spied.checkedLongFunction(d -> {
                    throw new IOException(Long.toString(d));
                });

                Function<IOException, String> errorHandler = Spied.function(e -> {
                    throw new IllegalStateException(e);
                });

                LongFunction<String> handling = function.onErrorHandleUnchecked(errorHandler);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.apply(1));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("1", cause.getMessage());

                verify(function).apply(1);
                verify(function).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).apply(any());
                verifyNoMoreInteractions(function, errorHandler);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedLongFunction<String, IOException> function = Spied.checkedLongFunction(d -> {
                throw new IllegalStateException(Long.toString(d));
            });

            Function<IOException, String> errorHandler = Spied.function(Exception::getMessage);

            LongFunction<String> handling = function.onErrorHandleUnchecked(errorHandler);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.apply(1));
            assertEquals("1", thrown.getMessage());

            verify(function).apply(1);
            verify(function).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }
    }

    @Nested
    class OnErrorApplyChecked {

        @Test
        void testNullArgument() {
            CheckedLongFunction<String, IOException> function = Spied.checkedLongFunction(Long::toString);

            assertThrows(NullPointerException.class, () -> function.onErrorApplyChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedLongFunction<String, IOException> function = Spied.checkedLongFunction(Long::toString);

            CheckedLongFunction<String, ExecutionException> fallback = Spied.checkedLongFunction(d -> Long.toString(d + d));

            CheckedLongFunction<String, ExecutionException> applying = function.onErrorApplyChecked(fallback);

            assertEquals("1", applying.apply(1));

            verify(function).apply(1);
            verify(function).onErrorApplyChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                CheckedLongFunction<String, IOException> function = Spied.checkedLongFunction(d -> {
                    throw new IOException(Long.toString(d));
                });

                CheckedLongFunction<String, ParseException> fallback = Spied.checkedLongFunction(d -> Long.toString(d + d));

                CheckedLongFunction<String, ParseException> applying = function.onErrorApplyChecked(fallback);

                assertEquals("2", applying.apply(1));

                verify(function).apply(1);
                verify(function).onErrorApplyChecked(fallback);
                verify(fallback).apply(1);
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                CheckedLongFunction<String, IOException> function = Spied.checkedLongFunction(d -> {
                    throw new IOException(Long.toString(d));
                });

                CheckedLongFunction<String, ParseException> fallback = Spied.checkedLongFunction(d -> {
                    throw new ParseException(Long.toString(d), 0);
                });

                CheckedLongFunction<String, ParseException> applying = function.onErrorApplyChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> applying.apply(1));
                assertEquals("1", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(function).apply(1);
                verify(function).onErrorApplyChecked(fallback);
                verify(fallback).apply(1);
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsUnchecked() throws IOException, ParseException {
                CheckedLongFunction<String, IOException> function = Spied.checkedLongFunction(d -> {
                    throw new IOException(Long.toString(d));
                });

                CheckedLongFunction<String, ParseException> fallback = Spied.checkedLongFunction(d -> {
                    throw new IllegalStateException(Long.toString(d));
                });

                CheckedLongFunction<String, ParseException> applying = function.onErrorApplyChecked(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> applying.apply(1));
                assertEquals("1", thrown.getMessage());

                verify(function).apply(1);
                verify(function).onErrorApplyChecked(fallback);
                verify(fallback).apply(1);
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedLongFunction<String, IOException> function = Spied.checkedLongFunction(d -> {
                throw new IllegalStateException(Long.toString(d));
            });

            CheckedLongFunction<String, ParseException> fallback = Spied.checkedLongFunction(d -> Long.toString(d + d));

            CheckedLongFunction<String, ParseException> applying = function.onErrorApplyChecked(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> applying.apply(1));
            assertEquals("1", thrown.getMessage());

            verify(function).apply(1);
            verify(function).onErrorApplyChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorApplyUnchecked {

        @Test
        void testNullArgument() {
            CheckedLongFunction<String, IOException> function = Spied.checkedLongFunction(Long::toString);

            assertThrows(NullPointerException.class, () -> function.onErrorApplyUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedLongFunction<String, IOException> function = Spied.checkedLongFunction(Long::toString);

            LongFunction<String> fallback = Spied.longFunction(d -> Long.toString(d + d));

            LongFunction<String> applying = function.onErrorApplyUnchecked(fallback);

            assertEquals("1", applying.apply(1));

            verify(function).apply(1);
            verify(function).onErrorApplyUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                CheckedLongFunction<String, IOException> function = Spied.checkedLongFunction(d -> {
                    throw new IOException(Long.toString(d));
                });

                LongFunction<String> fallback = Spied.longFunction(d -> Long.toString(d + d));

                LongFunction<String> applying = function.onErrorApplyUnchecked(fallback);

                assertEquals("2", applying.apply(1));

                verify(function).apply(1);
                verify(function).onErrorApplyUnchecked(fallback);
                verify(fallback).apply(1);
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsUnchecked() throws IOException {
                CheckedLongFunction<String, IOException> function = Spied.checkedLongFunction(d -> {
                    throw new IOException(Long.toString(d));
                });

                LongFunction<String> fallback = Spied.longFunction(d -> {
                    throw new IllegalStateException(Long.toString(d));
                });

                LongFunction<String> applying = function.onErrorApplyUnchecked(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> applying.apply(1));
                assertEquals("1", thrown.getMessage());

                verify(function).apply(1);
                verify(function).onErrorApplyUnchecked(fallback);
                verify(fallback).apply(1);
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedLongFunction<String, IOException> function = Spied.checkedLongFunction(d -> {
                throw new IllegalStateException(Long.toString(d));
            });

            LongFunction<String> fallback = Spied.longFunction(d -> Long.toString(d + d));

            LongFunction<String> applying = function.onErrorApplyUnchecked(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> applying.apply(1));
            assertEquals("1", thrown.getMessage());

            verify(function).apply(1);
            verify(function).onErrorApplyUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorGetChecked {

        @Test
        void testNullArgument() {
            CheckedLongFunction<String, IOException> function = Spied.checkedLongFunction(Long::toString);

            assertThrows(NullPointerException.class, () -> function.onErrorGetChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedLongFunction<String, IOException> function = Spied.checkedLongFunction(Long::toString);

            CheckedSupplier<String, ExecutionException> fallback = Spied.checkedSupplier(() -> "bar");

            CheckedLongFunction<String, ExecutionException> getting = function.onErrorGetChecked(fallback);

            assertEquals("1", getting.apply(1));

            verify(function).apply(1);
            verify(function).onErrorGetChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                CheckedLongFunction<String, IOException> function = Spied.checkedLongFunction(d -> {
                    throw new IOException(Long.toString(d));
                });

                CheckedSupplier<String, ParseException> fallback = Spied.checkedSupplier(() -> "bar");

                CheckedLongFunction<String, ParseException> getting = function.onErrorGetChecked(fallback);

                assertEquals("bar", getting.apply(1));

                verify(function).apply(1);
                verify(function).onErrorGetChecked(fallback);
                verify(fallback).get();
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                CheckedLongFunction<String, IOException> function = Spied.checkedLongFunction(d -> {
                    throw new IOException(Long.toString(d));
                });

                CheckedSupplier<String, ParseException> fallback = Spied.checkedSupplier(() -> {
                    throw new ParseException("bar", 0);
                });

                CheckedLongFunction<String, ParseException> getting = function.onErrorGetChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> getting.apply(1));
                assertEquals("bar", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(function).apply(1);
                verify(function).onErrorGetChecked(fallback);
                verify(fallback).get();
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsUnchecked() throws IOException, ParseException {
                CheckedLongFunction<String, IOException> function = Spied.checkedLongFunction(d -> {
                    throw new IOException(Long.toString(d));
                });

                CheckedSupplier<String, ParseException> fallback = Spied.checkedSupplier(() -> {
                    throw new IllegalStateException("bar");
                });

                CheckedLongFunction<String, ParseException> getting = function.onErrorGetChecked(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> getting.apply(1));
                assertEquals("bar", thrown.getMessage());

                verify(function).apply(1);
                verify(function).onErrorGetChecked(fallback);
                verify(fallback).get();
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedLongFunction<String, IOException> function = Spied.checkedLongFunction(d -> {
                throw new IllegalStateException(Long.toString(d));
            });

            CheckedSupplier<String, ParseException> fallback = Spied.checkedSupplier(() -> "bar");

            CheckedLongFunction<String, ParseException> getting = function.onErrorGetChecked(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> getting.apply(1));
            assertEquals("1", thrown.getMessage());

            verify(function).apply(1);
            verify(function).onErrorGetChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorGetUnchecked {

        @Test
        void testNullArgument() {
            CheckedLongFunction<String, IOException> function = Spied.checkedLongFunction(Long::toString);

            assertThrows(NullPointerException.class, () -> function.onErrorGetUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedLongFunction<String, IOException> function = Spied.checkedLongFunction(Long::toString);

            Supplier<String> fallback = Spied.supplier(() -> "bar");

            LongFunction<String> getting = function.onErrorGetUnchecked(fallback);

            assertEquals("1", getting.apply(1));

            verify(function).apply(1);
            verify(function).onErrorGetUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                CheckedLongFunction<String, IOException> function = Spied.checkedLongFunction(d -> {
                    throw new IOException(Long.toString(d));
                });

                Supplier<String> fallback = Spied.supplier(() -> "bar");

                LongFunction<String> getting = function.onErrorGetUnchecked(fallback);

                assertEquals("bar", getting.apply(1));

                verify(function).apply(1);
                verify(function).onErrorGetUnchecked(fallback);
                verify(fallback).get();
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsUnchecked() throws IOException {
                CheckedLongFunction<String, IOException> function = Spied.checkedLongFunction(d -> {
                    throw new IOException(Long.toString(d));
                });

                Supplier<String> fallback = Spied.supplier(() -> {
                    throw new IllegalStateException("bar");
                });

                LongFunction<String> getting = function.onErrorGetUnchecked(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> getting.apply(1));
                assertEquals("bar", thrown.getMessage());

                verify(function).apply(1);
                verify(function).onErrorGetUnchecked(fallback);
                verify(fallback).get();
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedLongFunction<String, IOException> function = Spied.checkedLongFunction(d -> {
                throw new IllegalStateException(Long.toString(d));
            });

            Supplier<String> fallback = Spied.supplier(() -> "bar");

            LongFunction<String> getting = function.onErrorGetUnchecked(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> getting.apply(1));
            assertEquals("1", thrown.getMessage());

            verify(function).apply(1);
            verify(function).onErrorGetUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorReturn {

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedLongFunction<String, IOException> function = Spied.checkedLongFunction(Long::toString);

            LongFunction<String> returning = function.onErrorReturn("bar");

            assertEquals("1", returning.apply(1));

            verify(function).apply(1);
            verify(function).onErrorReturn("bar");
            verifyNoMoreInteractions(function);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedLongFunction<String, IOException> function = Spied.checkedLongFunction(d -> {
                throw new IOException(Long.toString(d));
            });

            LongFunction<String> returning = function.onErrorReturn("bar");

            assertEquals("bar", returning.apply(1));

            verify(function).apply(1);
            verify(function).onErrorReturn("bar");
            verifyNoMoreInteractions(function);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedLongFunction<String, IOException> function = Spied.checkedLongFunction(d -> {
                throw new IllegalStateException(Long.toString(d));
            });

            LongFunction<String> returning = function.onErrorReturn("bar");

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> returning.apply(1));
            assertEquals("1", thrown.getMessage());

            verify(function).apply(1);
            verify(function).onErrorReturn("bar");
            verifyNoMoreInteractions(function);
        }
    }

    @Nested
    class Unchecked {

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedLongFunction<String, IOException> function = Spied.checkedLongFunction(Long::toString);

            LongFunction<String> unchecked = function.unchecked();

            assertEquals("1", unchecked.apply(1));

            verify(function).apply(1);
            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(function);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedLongFunction<String, IOException> function = Spied.checkedLongFunction(d -> {
                throw new IOException(Long.toString(d));
            });

            LongFunction<String> unchecked = function.unchecked();

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.apply(1));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1", cause.getMessage());

            verify(function).apply(1);
            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(function);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedLongFunction<String, IOException> function = Spied.checkedLongFunction(d -> {
                throw new IllegalArgumentException(Long.toString(d));
            });

            LongFunction<String> unchecked = function.unchecked();

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> unchecked.apply(1));
            assertEquals("1", thrown.getMessage());

            verify(function).apply(1);
            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(function);
        }
    }

    @Nested
    class Of {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> CheckedLongFunction.of(null));
        }

        @Test
        void testNonNullArgument() throws IOException {
            CheckedLongFunction<String, IOException> function = CheckedLongFunction.of(Long::toString);

            assertEquals("1", function.apply(1));
        }
    }

    @Nested
    class UncheckedFactory {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> CheckedLongFunction.unchecked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedLongFunction<String, IOException> function = Spied.checkedLongFunction(Long::toString);

            LongFunction<String> unchecked = CheckedLongFunction.unchecked(function);

            assertEquals("1", unchecked.apply(1));

            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verify(function).apply(1);
            verifyNoMoreInteractions(function);
        }

        @Test
        void testArgumentThrowsChecked() throws IOException {
            CheckedLongFunction<String, IOException> function = Spied.checkedLongFunction(d -> {
                throw new IOException(Long.toString(d));
            });

            LongFunction<String> unchecked = CheckedLongFunction.unchecked(function);

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.apply(1));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1", cause.getMessage());

            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verify(function).apply(1);
            verifyNoMoreInteractions(function);
        }

        @Test
        void testArgumentThrowsUnchecked() throws IOException {
            CheckedLongFunction<String, IOException> function = Spied.checkedLongFunction(d -> {
                throw new IllegalArgumentException(Long.toString(d));
            });

            LongFunction<String> unchecked = CheckedLongFunction.unchecked(function);

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> unchecked.apply(1));
            assertEquals("1", thrown.getMessage());

            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verify(function).apply(1);
            verifyNoMoreInteractions(function);
        }
    }

    @Nested
    class CheckedWrapper {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> CheckedLongFunction.checked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedLongFunction<String, IOException> function = CheckedLongFunction.checked(Long::toString);

            assertEquals("1", function.apply(1));
        }

        @Test
        void testargumentThrowsUnchecked() {
            CheckedLongFunction<String, IOException> function = CheckedLongFunction.checked(d -> {
                throw new UncheckedException(Long.toString(d), new IOException());
            });

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> function.apply(1));
            assertEquals("1", thrown.getMessage());
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertNull(cause.getMessage());
        }
    }

    @Nested
    class CheckedUnwrapper {

        @Test
        void testNullArguments() {
            LongFunction<String> function = Long::toString;

            assertThrows(NullPointerException.class, () -> CheckedLongFunction.checked(null, IOException.class));
            assertThrows(NullPointerException.class, () -> CheckedLongFunction.checked(function, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedLongFunction<String, IOException> function = CheckedLongFunction.checked(Long::toString, IOException.class);

            assertEquals("1", function.apply(1));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                CheckedLongFunction<String, IOException> function = CheckedLongFunction.checked(d -> {
                    throw new UncheckedException(new IOException(Long.toString(d)));
                }, IOException.class);

                IOException thrown = assertThrows(IOException.class, () -> function.apply(1));
                assertEquals("1", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                CheckedLongFunction<String, IOException> function = CheckedLongFunction.checked(d -> {
                    throw new UncheckedException(new FileNotFoundException(Long.toString(d)));
                }, IOException.class);

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class, () -> function.apply(1));
                assertEquals("1", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                CheckedLongFunction<String, IOException> function = CheckedLongFunction.checked(d -> {
                    throw new UncheckedException(new ParseException(Long.toString(d), 0));
                }, IOException.class);

                UncheckedException thrown = assertThrows(UncheckedException.class, () -> function.apply(1));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("1", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            CheckedLongFunction<String, IOException> function = CheckedLongFunction.checked(d -> {
                throw new IllegalStateException(Long.toString(d));
            }, IOException.class);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> function.apply(1));
            assertEquals("1", thrown.getMessage());
        }
    }

    @Nested
    class InvokeAndUnwrap {

        @Test
        void testNullArguments() {
            LongFunction<String> function = d -> String.valueOf(Long.toString(d)).toUpperCase();

            assertThrows(NullPointerException.class, () -> CheckedLongFunction.invokeAndUnwrap(null, 1, IOException.class));
            assertThrows(NullPointerException.class, () -> CheckedLongFunction.invokeAndUnwrap(function, 1, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            LongFunction<String> function = Long::toString;

            assertEquals("1", CheckedLongFunction.invokeAndUnwrap(function, 1, IOException.class));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                LongFunction<String> function = d -> {
                    throw new UncheckedException(new IOException(Long.toString(d)));
                };

                IOException thrown = assertThrows(IOException.class, () -> CheckedLongFunction.invokeAndUnwrap(function, 1, IOException.class));
                assertEquals("1", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                LongFunction<String> function = d -> {
                    throw new UncheckedException(new FileNotFoundException(Long.toString(d)));
                };

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class,
                        () -> CheckedLongFunction.invokeAndUnwrap(function, 1, IOException.class));
                assertEquals("1", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                LongFunction<String> function = d -> {
                    throw new UncheckedException(new ParseException(Long.toString(d), 0));
                };

                UncheckedException thrown = assertThrows(UncheckedException.class,
                        () -> CheckedLongFunction.invokeAndUnwrap(function, 1, IOException.class));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("1", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            LongFunction<String> function = d -> {
                throw new IllegalStateException(Long.toString(d));
            };

            IllegalStateException thrown = assertThrows(IllegalStateException.class,
                    () -> CheckedLongFunction.invokeAndUnwrap(function, 1, IOException.class));
            assertEquals("1", thrown.getMessage());
        }
    }
}
