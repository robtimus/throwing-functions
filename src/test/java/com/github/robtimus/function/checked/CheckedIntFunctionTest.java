/*
 * CheckedIntFunctionTest.java
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
import java.util.function.IntFunction;
import java.util.function.Supplier;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("nls")
class CheckedIntFunctionTest {

    @Nested
    class OnErrorThrowAsChecked {

        @Test
        void testNullArgument() {
            CheckedIntFunction<String, IOException> function = Spied.checkedIntFunction(Integer::toString);

            assertThrows(NullPointerException.class, () -> function.onErrorThrowAsChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedIntFunction<String, IOException> function = Spied.checkedIntFunction(Integer::toString);

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedIntFunction<String, ExecutionException> throwing = function.onErrorThrowAsChecked(errorMapper);

            assertEquals("1", throwing.apply(1));

            verify(function).apply(1);
            verify(function).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedIntFunction<String, IOException> function = Spied.checkedIntFunction(d -> {
                throw new IOException(Integer.toString(d));
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedIntFunction<String, ExecutionException> throwing = function.onErrorThrowAsChecked(errorMapper);

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
            CheckedIntFunction<String, IOException> function = Spied.checkedIntFunction(d -> {
                throw new IllegalStateException(Integer.toString(d));
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedIntFunction<String, ExecutionException> throwing = function.onErrorThrowAsChecked(errorMapper);

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
            CheckedIntFunction<String, IOException> function = Spied.checkedIntFunction(Integer::toString);

            assertThrows(NullPointerException.class, () -> function.onErrorThrowAsUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedIntFunction<String, IOException> function = Spied.checkedIntFunction(Integer::toString);

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            IntFunction<String> throwing = function.onErrorThrowAsUnchecked(errorMapper);

            assertEquals("1", throwing.apply(1));

            verify(function).apply(1);
            verify(function).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedIntFunction<String, IOException> function = Spied.checkedIntFunction(d -> {
                throw new IOException(Integer.toString(d));
            });

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            IntFunction<String> throwing = function.onErrorThrowAsUnchecked(errorMapper);

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
            CheckedIntFunction<String, IOException> function = Spied.checkedIntFunction(d -> {
                throw new IllegalArgumentException(Integer.toString(d));
            });

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            IntFunction<String> throwing = function.onErrorThrowAsUnchecked(errorMapper);

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
            CheckedIntFunction<String, IOException> function = Spied.checkedIntFunction(Integer::toString);

            assertThrows(NullPointerException.class, () -> function.onErrorHandleChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedIntFunction<String, IOException> function = Spied.checkedIntFunction(Integer::toString);

            CheckedFunction<IOException, String, ExecutionException> errorHandler = Spied.checkedFunction(Exception::getMessage);

            CheckedIntFunction<String, ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

            assertEquals("1", handling.apply(1));

            verify(function).apply(1);
            verify(function).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException, ExecutionException {
                CheckedIntFunction<String, IOException> function = Spied.checkedIntFunction(d -> {
                    throw new IOException(Integer.toString(d));
                });

                CheckedFunction<IOException, String, ExecutionException> errorHandler = Spied.checkedFunction(Exception::getMessage);

                CheckedIntFunction<String, ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

                assertEquals("1", handling.apply(1));

                verify(function).apply(1);
                verify(function).onErrorHandleChecked(errorHandler);
                verify(errorHandler).apply(any());
                verifyNoMoreInteractions(function, errorHandler);
            }

            @Test
            void testHandlerThrowsChecked() throws IOException, ExecutionException {
                CheckedIntFunction<String, IOException> function = Spied.checkedIntFunction(d -> {
                    throw new IOException(Integer.toString(d));
                });

                CheckedFunction<IOException, String, ExecutionException> errorHandler = Spied.checkedFunction(e -> {
                    throw new ExecutionException(e);
                });

                CheckedIntFunction<String, ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

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
                CheckedIntFunction<String, IOException> function = Spied.checkedIntFunction(d -> {
                    throw new IOException(Integer.toString(d));
                });

                CheckedFunction<IOException, String, ExecutionException> errorHandler = Spied.checkedFunction(e -> {
                    throw new IllegalStateException(e);
                });

                CheckedIntFunction<String, ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

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
            CheckedIntFunction<String, IOException> function = Spied.checkedIntFunction(d -> {
                throw new IllegalStateException(Integer.toString(d));
            });

            CheckedFunction<IOException, String, ExecutionException> errorHandler = Spied.checkedFunction(Exception::getMessage);

            CheckedIntFunction<String, ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

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
            CheckedIntFunction<String, IOException> function = Spied.checkedIntFunction(Integer::toString);

            assertThrows(NullPointerException.class, () -> function.onErrorHandleUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedIntFunction<String, IOException> function = Spied.checkedIntFunction(Integer::toString);

            Function<IOException, String> errorHandler = Spied.function(Exception::getMessage);

            IntFunction<String> handling = function.onErrorHandleUnchecked(errorHandler);

            assertEquals("1", handling.apply(1));

            verify(function).apply(1);
            verify(function).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException {
                CheckedIntFunction<String, IOException> function = Spied.checkedIntFunction(d -> {
                    throw new IOException(Integer.toString(d));
                });

                Function<IOException, String> errorHandler = Spied.function(Exception::getMessage);

                IntFunction<String> handling = function.onErrorHandleUnchecked(errorHandler);

                assertEquals("1", handling.apply(1));

                verify(function).apply(1);
                verify(function).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).apply(any());
                verifyNoMoreInteractions(function, errorHandler);
            }

            @Test
            void testHandlerThrowsUnchecked() throws IOException {
                CheckedIntFunction<String, IOException> function = Spied.checkedIntFunction(d -> {
                    throw new IOException(Integer.toString(d));
                });

                Function<IOException, String> errorHandler = Spied.function(e -> {
                    throw new IllegalStateException(e);
                });

                IntFunction<String> handling = function.onErrorHandleUnchecked(errorHandler);

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
            CheckedIntFunction<String, IOException> function = Spied.checkedIntFunction(d -> {
                throw new IllegalStateException(Integer.toString(d));
            });

            Function<IOException, String> errorHandler = Spied.function(Exception::getMessage);

            IntFunction<String> handling = function.onErrorHandleUnchecked(errorHandler);

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
            CheckedIntFunction<String, IOException> function = Spied.checkedIntFunction(Integer::toString);

            assertThrows(NullPointerException.class, () -> function.onErrorApplyChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedIntFunction<String, IOException> function = Spied.checkedIntFunction(Integer::toString);

            CheckedIntFunction<String, ExecutionException> fallback = Spied.checkedIntFunction(d -> Integer.toString(d + d));

            CheckedIntFunction<String, ExecutionException> applying = function.onErrorApplyChecked(fallback);

            assertEquals("1", applying.apply(1));

            verify(function).apply(1);
            verify(function).onErrorApplyChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                CheckedIntFunction<String, IOException> function = Spied.checkedIntFunction(d -> {
                    throw new IOException(Integer.toString(d));
                });

                CheckedIntFunction<String, ParseException> fallback = Spied.checkedIntFunction(d -> Integer.toString(d + d));

                CheckedIntFunction<String, ParseException> applying = function.onErrorApplyChecked(fallback);

                assertEquals("2", applying.apply(1));

                verify(function).apply(1);
                verify(function).onErrorApplyChecked(fallback);
                verify(fallback).apply(1);
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                CheckedIntFunction<String, IOException> function = Spied.checkedIntFunction(d -> {
                    throw new IOException(Integer.toString(d));
                });

                CheckedIntFunction<String, ParseException> fallback = Spied.checkedIntFunction(d -> {
                    throw new ParseException(Integer.toString(d), 0);
                });

                CheckedIntFunction<String, ParseException> applying = function.onErrorApplyChecked(fallback);

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
                CheckedIntFunction<String, IOException> function = Spied.checkedIntFunction(d -> {
                    throw new IOException(Integer.toString(d));
                });

                CheckedIntFunction<String, ParseException> fallback = Spied.checkedIntFunction(d -> {
                    throw new IllegalStateException(Integer.toString(d));
                });

                CheckedIntFunction<String, ParseException> applying = function.onErrorApplyChecked(fallback);

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
            CheckedIntFunction<String, IOException> function = Spied.checkedIntFunction(d -> {
                throw new IllegalStateException(Integer.toString(d));
            });

            CheckedIntFunction<String, ParseException> fallback = Spied.checkedIntFunction(d -> Integer.toString(d + d));

            CheckedIntFunction<String, ParseException> applying = function.onErrorApplyChecked(fallback);

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
            CheckedIntFunction<String, IOException> function = Spied.checkedIntFunction(Integer::toString);

            assertThrows(NullPointerException.class, () -> function.onErrorApplyUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedIntFunction<String, IOException> function = Spied.checkedIntFunction(Integer::toString);

            IntFunction<String> fallback = Spied.intFunction(d -> Integer.toString(d + d));

            IntFunction<String> applying = function.onErrorApplyUnchecked(fallback);

            assertEquals("1", applying.apply(1));

            verify(function).apply(1);
            verify(function).onErrorApplyUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                CheckedIntFunction<String, IOException> function = Spied.checkedIntFunction(d -> {
                    throw new IOException(Integer.toString(d));
                });

                IntFunction<String> fallback = Spied.intFunction(d -> Integer.toString(d + d));

                IntFunction<String> applying = function.onErrorApplyUnchecked(fallback);

                assertEquals("2", applying.apply(1));

                verify(function).apply(1);
                verify(function).onErrorApplyUnchecked(fallback);
                verify(fallback).apply(1);
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsUnchecked() throws IOException {
                CheckedIntFunction<String, IOException> function = Spied.checkedIntFunction(d -> {
                    throw new IOException(Integer.toString(d));
                });

                IntFunction<String> fallback = Spied.intFunction(d -> {
                    throw new IllegalStateException(Integer.toString(d));
                });

                IntFunction<String> applying = function.onErrorApplyUnchecked(fallback);

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
            CheckedIntFunction<String, IOException> function = Spied.checkedIntFunction(d -> {
                throw new IllegalStateException(Integer.toString(d));
            });

            IntFunction<String> fallback = Spied.intFunction(d -> Integer.toString(d + d));

            IntFunction<String> applying = function.onErrorApplyUnchecked(fallback);

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
            CheckedIntFunction<String, IOException> function = Spied.checkedIntFunction(Integer::toString);

            assertThrows(NullPointerException.class, () -> function.onErrorGetChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedIntFunction<String, IOException> function = Spied.checkedIntFunction(Integer::toString);

            CheckedSupplier<String, ExecutionException> fallback = Spied.checkedSupplier(() -> "bar");

            CheckedIntFunction<String, ExecutionException> getting = function.onErrorGetChecked(fallback);

            assertEquals("1", getting.apply(1));

            verify(function).apply(1);
            verify(function).onErrorGetChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                CheckedIntFunction<String, IOException> function = Spied.checkedIntFunction(d -> {
                    throw new IOException(Integer.toString(d));
                });

                CheckedSupplier<String, ParseException> fallback = Spied.checkedSupplier(() -> "bar");

                CheckedIntFunction<String, ParseException> getting = function.onErrorGetChecked(fallback);

                assertEquals("bar", getting.apply(1));

                verify(function).apply(1);
                verify(function).onErrorGetChecked(fallback);
                verify(fallback).get();
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                CheckedIntFunction<String, IOException> function = Spied.checkedIntFunction(d -> {
                    throw new IOException(Integer.toString(d));
                });

                CheckedSupplier<String, ParseException> fallback = Spied.checkedSupplier(() -> {
                    throw new ParseException("bar", 0);
                });

                CheckedIntFunction<String, ParseException> getting = function.onErrorGetChecked(fallback);

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
                CheckedIntFunction<String, IOException> function = Spied.checkedIntFunction(d -> {
                    throw new IOException(Integer.toString(d));
                });

                CheckedSupplier<String, ParseException> fallback = Spied.checkedSupplier(() -> {
                    throw new IllegalStateException("bar");
                });

                CheckedIntFunction<String, ParseException> getting = function.onErrorGetChecked(fallback);

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
            CheckedIntFunction<String, IOException> function = Spied.checkedIntFunction(d -> {
                throw new IllegalStateException(Integer.toString(d));
            });

            CheckedSupplier<String, ParseException> fallback = Spied.checkedSupplier(() -> "bar");

            CheckedIntFunction<String, ParseException> getting = function.onErrorGetChecked(fallback);

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
            CheckedIntFunction<String, IOException> function = Spied.checkedIntFunction(Integer::toString);

            assertThrows(NullPointerException.class, () -> function.onErrorGetUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedIntFunction<String, IOException> function = Spied.checkedIntFunction(Integer::toString);

            Supplier<String> fallback = Spied.supplier(() -> "bar");

            IntFunction<String> getting = function.onErrorGetUnchecked(fallback);

            assertEquals("1", getting.apply(1));

            verify(function).apply(1);
            verify(function).onErrorGetUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                CheckedIntFunction<String, IOException> function = Spied.checkedIntFunction(d -> {
                    throw new IOException(Integer.toString(d));
                });

                Supplier<String> fallback = Spied.supplier(() -> "bar");

                IntFunction<String> getting = function.onErrorGetUnchecked(fallback);

                assertEquals("bar", getting.apply(1));

                verify(function).apply(1);
                verify(function).onErrorGetUnchecked(fallback);
                verify(fallback).get();
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsUnchecked() throws IOException {
                CheckedIntFunction<String, IOException> function = Spied.checkedIntFunction(d -> {
                    throw new IOException(Integer.toString(d));
                });

                Supplier<String> fallback = Spied.supplier(() -> {
                    throw new IllegalStateException("bar");
                });

                IntFunction<String> getting = function.onErrorGetUnchecked(fallback);

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
            CheckedIntFunction<String, IOException> function = Spied.checkedIntFunction(d -> {
                throw new IllegalStateException(Integer.toString(d));
            });

            Supplier<String> fallback = Spied.supplier(() -> "bar");

            IntFunction<String> getting = function.onErrorGetUnchecked(fallback);

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
            CheckedIntFunction<String, IOException> function = Spied.checkedIntFunction(Integer::toString);

            IntFunction<String> returning = function.onErrorReturn("bar");

            assertEquals("1", returning.apply(1));

            verify(function).apply(1);
            verify(function).onErrorReturn("bar");
            verifyNoMoreInteractions(function);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedIntFunction<String, IOException> function = Spied.checkedIntFunction(d -> {
                throw new IOException(Integer.toString(d));
            });

            IntFunction<String> returning = function.onErrorReturn("bar");

            assertEquals("bar", returning.apply(1));

            verify(function).apply(1);
            verify(function).onErrorReturn("bar");
            verifyNoMoreInteractions(function);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedIntFunction<String, IOException> function = Spied.checkedIntFunction(d -> {
                throw new IllegalStateException(Integer.toString(d));
            });

            IntFunction<String> returning = function.onErrorReturn("bar");

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
            CheckedIntFunction<String, IOException> function = Spied.checkedIntFunction(Integer::toString);

            IntFunction<String> unchecked = function.unchecked();

            assertEquals("1", unchecked.apply(1));

            verify(function).apply(1);
            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(function);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedIntFunction<String, IOException> function = Spied.checkedIntFunction(d -> {
                throw new IOException(Integer.toString(d));
            });

            IntFunction<String> unchecked = function.unchecked();

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
            CheckedIntFunction<String, IOException> function = Spied.checkedIntFunction(d -> {
                throw new IllegalArgumentException(Integer.toString(d));
            });

            IntFunction<String> unchecked = function.unchecked();

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
            assertThrows(NullPointerException.class, () -> CheckedIntFunction.of(null));
        }

        @Test
        void testNonNullArgument() throws IOException {
            CheckedIntFunction<String, IOException> function = CheckedIntFunction.of(Integer::toString);

            assertEquals("1", function.apply(1));
        }
    }

    @Nested
    class UncheckedFactory {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> CheckedIntFunction.unchecked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedIntFunction<String, IOException> function = Spied.checkedIntFunction(Integer::toString);

            IntFunction<String> unchecked = CheckedIntFunction.unchecked(function);

            assertEquals("1", unchecked.apply(1));

            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verify(function).apply(1);
            verifyNoMoreInteractions(function);
        }

        @Test
        void testArgumentThrowsChecked() throws IOException {
            CheckedIntFunction<String, IOException> function = Spied.checkedIntFunction(d -> {
                throw new IOException(Integer.toString(d));
            });

            IntFunction<String> unchecked = CheckedIntFunction.unchecked(function);

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
            CheckedIntFunction<String, IOException> function = Spied.checkedIntFunction(d -> {
                throw new IllegalArgumentException(Integer.toString(d));
            });

            IntFunction<String> unchecked = CheckedIntFunction.unchecked(function);

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
            assertThrows(NullPointerException.class, () -> CheckedIntFunction.checked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedIntFunction<String, IOException> function = CheckedIntFunction.checked(Integer::toString);

            assertEquals("1", function.apply(1));
        }

        @Test
        void testArgumentThrowsUnchecked() {
            CheckedIntFunction<String, IOException> function = CheckedIntFunction.checked(d -> {
                throw new UncheckedException(Integer.toString(d), new IOException());
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
            IntFunction<String> function = Integer::toString;

            assertThrows(NullPointerException.class, () -> CheckedIntFunction.checked(null, IOException.class));
            assertThrows(NullPointerException.class, () -> CheckedIntFunction.checked(function, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedIntFunction<String, IOException> function = CheckedIntFunction.checked(Integer::toString, IOException.class);

            assertEquals("1", function.apply(1));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                CheckedIntFunction<String, IOException> function = CheckedIntFunction.checked(d -> {
                    throw new UncheckedException(new IOException(Integer.toString(d)));
                }, IOException.class);

                IOException thrown = assertThrows(IOException.class, () -> function.apply(1));
                assertEquals("1", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                CheckedIntFunction<String, IOException> function = CheckedIntFunction.checked(d -> {
                    throw new UncheckedException(new FileNotFoundException(Integer.toString(d)));
                }, IOException.class);

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class, () -> function.apply(1));
                assertEquals("1", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                CheckedIntFunction<String, IOException> function = CheckedIntFunction.checked(d -> {
                    throw new UncheckedException(new ParseException(Integer.toString(d), 0));
                }, IOException.class);

                UncheckedException thrown = assertThrows(UncheckedException.class, () -> function.apply(1));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("1", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            CheckedIntFunction<String, IOException> function = CheckedIntFunction.checked(d -> {
                throw new IllegalStateException(Integer.toString(d));
            }, IOException.class);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> function.apply(1));
            assertEquals("1", thrown.getMessage());
        }
    }

    @Nested
    class InvokeAndUnwrap {

        @Test
        void testNullArguments() {
            IntFunction<String> function = d -> String.valueOf(Integer.toString(d)).toUpperCase();

            assertThrows(NullPointerException.class, () -> CheckedIntFunction.invokeAndUnwrap(null, 1, IOException.class));
            assertThrows(NullPointerException.class, () -> CheckedIntFunction.invokeAndUnwrap(function, 1, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            IntFunction<String> function = Integer::toString;

            assertEquals("1", CheckedIntFunction.invokeAndUnwrap(function, 1, IOException.class));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                IntFunction<String> function = d -> {
                    throw new UncheckedException(new IOException(Integer.toString(d)));
                };

                IOException thrown = assertThrows(IOException.class, () -> CheckedIntFunction.invokeAndUnwrap(function, 1, IOException.class));
                assertEquals("1", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                IntFunction<String> function = d -> {
                    throw new UncheckedException(new FileNotFoundException(Integer.toString(d)));
                };

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class,
                        () -> CheckedIntFunction.invokeAndUnwrap(function, 1, IOException.class));
                assertEquals("1", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                IntFunction<String> function = d -> {
                    throw new UncheckedException(new ParseException(Integer.toString(d), 0));
                };

                UncheckedException thrown = assertThrows(UncheckedException.class,
                        () -> CheckedIntFunction.invokeAndUnwrap(function, 1, IOException.class));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("1", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            IntFunction<String> function = d -> {
                throw new IllegalStateException(Integer.toString(d));
            };

            IllegalStateException thrown = assertThrows(IllegalStateException.class,
                    () -> CheckedIntFunction.invokeAndUnwrap(function, 1, IOException.class));
            assertEquals("1", thrown.getMessage());
        }
    }
}
