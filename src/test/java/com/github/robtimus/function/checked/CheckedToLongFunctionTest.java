/*
 * CheckedToLongFunctionTest.java
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
class CheckedToLongFunctionTest {

    @Nested
    class OnErrorThrowAsChecked {

        @Test
        void testNullArgument() {
            CheckedToLongFunction<String, IOException> function = Spied.checkedToLongFunction(String::length);

            assertThrows(NullPointerException.class, () -> function.onErrorThrowAsChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedToLongFunction<String, IOException> function = Spied.checkedToLongFunction(String::length);

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedToLongFunction<String, ExecutionException> throwing = function.onErrorThrowAsChecked(errorMapper);

            assertEquals(3L, throwing.applyAsLong("foo"));

            verify(function).applyAsLong("foo");
            verify(function).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedToLongFunction<String, IOException> function = Spied.checkedToLongFunction(s -> {
                throw new IOException(s);
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedToLongFunction<String, ExecutionException> throwing = function.onErrorThrowAsChecked(errorMapper);

            ExecutionException thrown = assertThrows(ExecutionException.class, () -> throwing.applyAsLong("foo"));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo", cause.getMessage());

            verify(function).applyAsLong("foo");
            verify(function).onErrorThrowAsChecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(function, errorMapper);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedToLongFunction<String, IOException> function = Spied.checkedToLongFunction(s -> {
                throw new IllegalStateException(s);
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedToLongFunction<String, ExecutionException> throwing = function.onErrorThrowAsChecked(errorMapper);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> throwing.applyAsLong("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(function).applyAsLong("foo");
            verify(function).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }
    }

    @Nested
    class OnErrorThrowAsUnchecked {

        @Test
        void testNullArgument() {
            CheckedToLongFunction<String, IOException> function = Spied.checkedToLongFunction(String::length);

            assertThrows(NullPointerException.class, () -> function.onErrorThrowAsUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedToLongFunction<String, IOException> function = Spied.checkedToLongFunction(String::length);

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            ToLongFunction<String> throwing = function.onErrorThrowAsUnchecked(errorMapper);

            assertEquals(3L, throwing.applyAsLong("foo"));

            verify(function).applyAsLong("foo");
            verify(function).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedToLongFunction<String, IOException> function = Spied.checkedToLongFunction(s -> {
                throw new IOException(s);
            });

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            ToLongFunction<String> throwing = function.onErrorThrowAsUnchecked(errorMapper);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> throwing.applyAsLong("foo"));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo", cause.getMessage());

            verify(function).applyAsLong("foo");
            verify(function).onErrorThrowAsUnchecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(function, errorMapper);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedToLongFunction<String, IOException> function = Spied.checkedToLongFunction(s -> {
                throw new IllegalArgumentException(s);
            });

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            ToLongFunction<String> throwing = function.onErrorThrowAsUnchecked(errorMapper);

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> throwing.applyAsLong("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(function).applyAsLong("foo");
            verify(function).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }
    }

    @Nested
    class OnErrorHandleChecked {

        @Test
        void testNullArgument() {
            CheckedToLongFunction<String, IOException> function = Spied.checkedToLongFunction(String::length);

            assertThrows(NullPointerException.class, () -> function.onErrorHandleChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedToLongFunction<String, IOException> function = Spied.checkedToLongFunction(String::length);

            CheckedToLongFunction<IOException, ExecutionException> errorHandler = Spied.checkedToLongFunction(e -> 0L);

            CheckedToLongFunction<String, ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

            assertEquals(3L, handling.applyAsLong("foo"));

            verify(function).applyAsLong("foo");
            verify(function).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException, ExecutionException {
                CheckedToLongFunction<String, IOException> function = Spied.checkedToLongFunction(s -> {
                    throw new IOException(s);
                });

                CheckedToLongFunction<IOException, ExecutionException> errorHandler = Spied.checkedToLongFunction(e -> 0L);

                CheckedToLongFunction<String, ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

                assertEquals(0L, handling.applyAsLong("foo"));

                verify(function).applyAsLong("foo");
                verify(function).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsLong(any());
                verifyNoMoreInteractions(function, errorHandler);
            }

            @Test
            void testHandlerThrowsChecked() throws IOException, ExecutionException {
                CheckedToLongFunction<String, IOException> function = Spied.checkedToLongFunction(s -> {
                    throw new IOException(s);
                });

                CheckedToLongFunction<IOException, ExecutionException> errorHandler = Spied.checkedToLongFunction(e -> {
                    throw new ExecutionException(e);
                });

                CheckedToLongFunction<String, ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

                ExecutionException thrown = assertThrows(ExecutionException.class, () -> handling.applyAsLong("foo"));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());

                verify(function).applyAsLong("foo");
                verify(function).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsLong(any());
                verifyNoMoreInteractions(function, errorHandler);
            }

            @Test
            void testHandlerThrowsUnchecked() throws IOException, ExecutionException {
                CheckedToLongFunction<String, IOException> function = Spied.checkedToLongFunction(s -> {
                    throw new IOException(s);
                });

                CheckedToLongFunction<IOException, ExecutionException> errorHandler = Spied.checkedToLongFunction(e -> {
                    throw new IllegalStateException(e);
                });

                CheckedToLongFunction<String, ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.applyAsLong("foo"));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());

                verify(function).applyAsLong("foo");
                verify(function).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsLong(any());
                verifyNoMoreInteractions(function, errorHandler);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedToLongFunction<String, IOException> function = Spied.checkedToLongFunction(s -> {
                throw new IllegalStateException(s);
            });

            CheckedToLongFunction<IOException, ExecutionException> errorHandler = Spied.checkedToLongFunction(e -> 0L);

            CheckedToLongFunction<String, ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.applyAsLong("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(function).applyAsLong("foo");
            verify(function).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }
    }

    @Nested
    class OnErrorHandleUnchecked {

        @Test
        void testNullArgument() {
            CheckedToLongFunction<String, IOException> function = Spied.checkedToLongFunction(String::length);

            assertThrows(NullPointerException.class, () -> function.onErrorHandleUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedToLongFunction<String, IOException> function = Spied.checkedToLongFunction(String::length);

            ToLongFunction<IOException> errorHandler = Spied.toLongFunction(e -> 0L);

            ToLongFunction<String> handling = function.onErrorHandleUnchecked(errorHandler);

            assertEquals(3L, handling.applyAsLong("foo"));

            verify(function).applyAsLong("foo");
            verify(function).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException {
                CheckedToLongFunction<String, IOException> function = Spied.checkedToLongFunction(s -> {
                    throw new IOException(s);
                });

                ToLongFunction<IOException> errorHandler = Spied.toLongFunction(e -> 0L);

                ToLongFunction<String> handling = function.onErrorHandleUnchecked(errorHandler);

                assertEquals(0L, handling.applyAsLong("foo"));

                verify(function).applyAsLong("foo");
                verify(function).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).applyAsLong(any());
                verifyNoMoreInteractions(function, errorHandler);
            }

            @Test
            void testHandlerThrowsUnchecked() throws IOException {
                CheckedToLongFunction<String, IOException> function = Spied.checkedToLongFunction(s -> {
                    throw new IOException(s);
                });

                ToLongFunction<IOException> errorHandler = Spied.toLongFunction(e -> {
                    throw new IllegalStateException(e);
                });

                ToLongFunction<String> handling = function.onErrorHandleUnchecked(errorHandler);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.applyAsLong("foo"));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());

                verify(function).applyAsLong("foo");
                verify(function).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).applyAsLong(any());
                verifyNoMoreInteractions(function, errorHandler);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedToLongFunction<String, IOException> function = Spied.checkedToLongFunction(s -> {
                throw new IllegalStateException(s);
            });

            ToLongFunction<IOException> errorHandler = Spied.toLongFunction(e -> 0L);

            ToLongFunction<String> handling = function.onErrorHandleUnchecked(errorHandler);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.applyAsLong("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(function).applyAsLong("foo");
            verify(function).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }
    }

    @Nested
    class OnErrorApplyChecked {

        @Test
        void testNullArgument() {
            CheckedToLongFunction<String, IOException> function = Spied.checkedToLongFunction(String::length);

            assertThrows(NullPointerException.class, () -> function.onErrorApplyChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedToLongFunction<String, IOException> function = Spied.checkedToLongFunction(String::length);

            CheckedToLongFunction<String, ExecutionException> fallback = Spied.checkedToLongFunction(s -> 2 * s.length());

            CheckedToLongFunction<String, ExecutionException> applying = function.onErrorApplyChecked(fallback);

            assertEquals(3L, applying.applyAsLong("foo"));

            verify(function).applyAsLong("foo");
            verify(function).onErrorApplyChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                CheckedToLongFunction<String, IOException> function = Spied.checkedToLongFunction(s -> {
                    throw new IOException(s);
                });

                CheckedToLongFunction<String, ParseException> fallback = Spied.checkedToLongFunction(s -> 2 * s.length());

                CheckedToLongFunction<String, ParseException> applying = function.onErrorApplyChecked(fallback);

                assertEquals(6L, applying.applyAsLong("foo"));

                verify(function).applyAsLong("foo");
                verify(function).onErrorApplyChecked(fallback);
                verify(fallback).applyAsLong("foo");
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                CheckedToLongFunction<String, IOException> function = Spied.checkedToLongFunction(s -> {
                    throw new IOException(s);
                });

                CheckedToLongFunction<String, ParseException> fallback = Spied.checkedToLongFunction(s -> {
                    throw new ParseException(s, 0);
                });

                CheckedToLongFunction<String, ParseException> applying = function.onErrorApplyChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> applying.applyAsLong("foo"));
                assertEquals("foo", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(function).applyAsLong("foo");
                verify(function).onErrorApplyChecked(fallback);
                verify(fallback).applyAsLong("foo");
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsUnchecked() throws IOException, ParseException {
                CheckedToLongFunction<String, IOException> function = Spied.checkedToLongFunction(s -> {
                    throw new IOException(s);
                });

                CheckedToLongFunction<String, ParseException> fallback = Spied.checkedToLongFunction(s -> {
                    throw new IllegalStateException(s);
                });

                CheckedToLongFunction<String, ParseException> applying = function.onErrorApplyChecked(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> applying.applyAsLong("foo"));
                assertEquals("foo", thrown.getMessage());

                verify(function).applyAsLong("foo");
                verify(function).onErrorApplyChecked(fallback);
                verify(fallback).applyAsLong("foo");
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedToLongFunction<String, IOException> function = Spied.checkedToLongFunction(s -> {
                throw new IllegalStateException(s);
            });

            CheckedToLongFunction<String, ParseException> fallback = Spied.checkedToLongFunction(s -> 2 * s.length());

            CheckedToLongFunction<String, ParseException> applying = function.onErrorApplyChecked(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> applying.applyAsLong("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(function).applyAsLong("foo");
            verify(function).onErrorApplyChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorApplyUnchecked {

        @Test
        void testNullArgument() {
            CheckedToLongFunction<String, IOException> function = Spied.checkedToLongFunction(String::length);

            assertThrows(NullPointerException.class, () -> function.onErrorApplyUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedToLongFunction<String, IOException> function = Spied.checkedToLongFunction(String::length);

            ToLongFunction<String> fallback = Spied.toLongFunction(s -> 2 * s.length());

            ToLongFunction<String> applying = function.onErrorApplyUnchecked(fallback);

            assertEquals(3L, applying.applyAsLong("foo"));

            verify(function).applyAsLong("foo");
            verify(function).onErrorApplyUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                CheckedToLongFunction<String, IOException> function = Spied.checkedToLongFunction(s -> {
                    throw new IOException(s);
                });

                ToLongFunction<String> fallback = Spied.toLongFunction(s -> 2 * s.length());

                ToLongFunction<String> applying = function.onErrorApplyUnchecked(fallback);

                assertEquals(6L, applying.applyAsLong("foo"));

                verify(function).applyAsLong("foo");
                verify(function).onErrorApplyUnchecked(fallback);
                verify(fallback).applyAsLong("foo");
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsUnchecked() throws IOException {
                CheckedToLongFunction<String, IOException> function = Spied.checkedToLongFunction(s -> {
                    throw new IOException(s);
                });

                ToLongFunction<String> fallback = Spied.toLongFunction(s -> {
                    throw new IllegalStateException(s);
                });

                ToLongFunction<String> applying = function.onErrorApplyUnchecked(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> applying.applyAsLong("foo"));
                assertEquals("foo", thrown.getMessage());

                verify(function).applyAsLong("foo");
                verify(function).onErrorApplyUnchecked(fallback);
                verify(fallback).applyAsLong("foo");
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedToLongFunction<String, IOException> function = Spied.checkedToLongFunction(s -> {
                throw new IllegalStateException(s);
            });

            ToLongFunction<String> fallback = Spied.toLongFunction(s -> 2 * s.length());

            ToLongFunction<String> applying = function.onErrorApplyUnchecked(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> applying.applyAsLong("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(function).applyAsLong("foo");
            verify(function).onErrorApplyUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorGetChecked {

        @Test
        void testNullArgument() {
            CheckedToLongFunction<String, IOException> function = Spied.checkedToLongFunction(String::length);

            assertThrows(NullPointerException.class, () -> function.onErrorGetChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedToLongFunction<String, IOException> function = Spied.checkedToLongFunction(String::length);

            CheckedLongSupplier<ExecutionException> fallback = Spied.checkedLongSupplier(() -> 0L);

            CheckedToLongFunction<String, ExecutionException> getting = function.onErrorGetChecked(fallback);

            assertEquals(3L, getting.applyAsLong("foo"));

            verify(function).applyAsLong("foo");
            verify(function).onErrorGetChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                CheckedToLongFunction<String, IOException> function = Spied.checkedToLongFunction(s -> {
                    throw new IOException(s);
                });

                CheckedLongSupplier<ParseException> fallback = Spied.checkedLongSupplier(() -> 0L);

                CheckedToLongFunction<String, ParseException> getting = function.onErrorGetChecked(fallback);

                assertEquals(0L, getting.applyAsLong("foo"));

                verify(function).applyAsLong("foo");
                verify(function).onErrorGetChecked(fallback);
                verify(fallback).getAsLong();
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                CheckedToLongFunction<String, IOException> function = Spied.checkedToLongFunction(s -> {
                    throw new IOException(s);
                });

                CheckedLongSupplier<ParseException> fallback = Spied.checkedLongSupplier(() -> {
                    throw new ParseException("bar", 0);
                });

                CheckedToLongFunction<String, ParseException> getting = function.onErrorGetChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> getting.applyAsLong("foo"));
                assertEquals("bar", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(function).applyAsLong("foo");
                verify(function).onErrorGetChecked(fallback);
                verify(fallback).getAsLong();
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsUnchecked() throws IOException, ParseException {
                CheckedToLongFunction<String, IOException> function = Spied.checkedToLongFunction(s -> {
                    throw new IOException(s);
                });

                CheckedLongSupplier<ParseException> fallback = Spied.checkedLongSupplier(() -> {
                    throw new IllegalStateException("bar");
                });

                CheckedToLongFunction<String, ParseException> getting = function.onErrorGetChecked(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> getting.applyAsLong("foo"));
                assertEquals("bar", thrown.getMessage());

                verify(function).applyAsLong("foo");
                verify(function).onErrorGetChecked(fallback);
                verify(fallback).getAsLong();
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedToLongFunction<String, IOException> function = Spied.checkedToLongFunction(s -> {
                throw new IllegalStateException(s);
            });

            CheckedLongSupplier<ParseException> fallback = Spied.checkedLongSupplier(() -> 0L);

            CheckedToLongFunction<String, ParseException> getting = function.onErrorGetChecked(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> getting.applyAsLong("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(function).applyAsLong("foo");
            verify(function).onErrorGetChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorGetUnchecked {

        @Test
        void testNullArgument() {
            CheckedToLongFunction<String, IOException> function = Spied.checkedToLongFunction(String::length);

            assertThrows(NullPointerException.class, () -> function.onErrorGetUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedToLongFunction<String, IOException> function = Spied.checkedToLongFunction(String::length);

            LongSupplier fallback = Spied.longSupplier(() -> 0L);

            ToLongFunction<String> getting = function.onErrorGetUnchecked(fallback);

            assertEquals(3L, getting.applyAsLong("foo"));

            verify(function).applyAsLong("foo");
            verify(function).onErrorGetUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                CheckedToLongFunction<String, IOException> function = Spied.checkedToLongFunction(s -> {
                    throw new IOException(s);
                });

                LongSupplier fallback = Spied.longSupplier(() -> 0L);

                ToLongFunction<String> getting = function.onErrorGetUnchecked(fallback);

                assertEquals(0L, getting.applyAsLong("foo"));

                verify(function).applyAsLong("foo");
                verify(function).onErrorGetUnchecked(fallback);
                verify(fallback).getAsLong();
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsUnchecked() throws IOException {
                CheckedToLongFunction<String, IOException> function = Spied.checkedToLongFunction(s -> {
                    throw new IOException(s);
                });

                LongSupplier fallback = Spied.longSupplier(() -> {
                    throw new IllegalStateException("bar");
                });

                ToLongFunction<String> getting = function.onErrorGetUnchecked(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> getting.applyAsLong("foo"));
                assertEquals("bar", thrown.getMessage());

                verify(function).applyAsLong("foo");
                verify(function).onErrorGetUnchecked(fallback);
                verify(fallback).getAsLong();
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedToLongFunction<String, IOException> function = Spied.checkedToLongFunction(s -> {
                throw new IllegalStateException(s);
            });

            LongSupplier fallback = Spied.longSupplier(() -> 0L);

            ToLongFunction<String> getting = function.onErrorGetUnchecked(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> getting.applyAsLong("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(function).applyAsLong("foo");
            verify(function).onErrorGetUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorReturn {

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedToLongFunction<String, IOException> function = Spied.checkedToLongFunction(String::length);

            ToLongFunction<String> returning = function.onErrorReturn(0L);

            assertEquals(3L, returning.applyAsLong("foo"));

            verify(function).applyAsLong("foo");
            verify(function).onErrorReturn(0L);
            verifyNoMoreInteractions(function);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedToLongFunction<String, IOException> function = Spied.checkedToLongFunction(s -> {
                throw new IOException(s);
            });

            ToLongFunction<String> returning = function.onErrorReturn(0L);

            assertEquals(0L, returning.applyAsLong("foo"));

            verify(function).applyAsLong("foo");
            verify(function).onErrorReturn(0L);
            verifyNoMoreInteractions(function);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedToLongFunction<String, IOException> function = Spied.checkedToLongFunction(s -> {
                throw new IllegalStateException(s);
            });

            ToLongFunction<String> returning = function.onErrorReturn(0L);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> returning.applyAsLong("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(function).applyAsLong("foo");
            verify(function).onErrorReturn(0L);
            verifyNoMoreInteractions(function);
        }
    }

    @Nested
    class Unchecked {

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedToLongFunction<String, IOException> function = Spied.checkedToLongFunction(String::length);

            ToLongFunction<String> unchecked = function.unchecked();

            assertEquals(3L, unchecked.applyAsLong("foo"));

            verify(function).applyAsLong("foo");
            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(function);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedToLongFunction<String, IOException> function = Spied.checkedToLongFunction(s -> {
                throw new IOException(s);
            });

            ToLongFunction<String> unchecked = function.unchecked();

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.applyAsLong("foo"));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo", cause.getMessage());

            verify(function).applyAsLong("foo");
            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(function);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedToLongFunction<String, IOException> function = Spied.checkedToLongFunction(s -> {
                throw new IllegalArgumentException(s);
            });

            ToLongFunction<String> unchecked = function.unchecked();

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> unchecked.applyAsLong("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(function).applyAsLong("foo");
            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(function);
        }
    }

    @Nested
    class Of {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> CheckedToLongFunction.of(null));
        }

        @Test
        void testNonNullArgument() throws IOException {
            CheckedToLongFunction<String, IOException> function = CheckedToLongFunction.of(String::length);

            assertEquals(3L, function.applyAsLong("foo"));
        }
    }

    @Nested
    class UncheckedFactory {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> CheckedToLongFunction.unchecked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedToLongFunction<String, IOException> function = Spied.checkedToLongFunction(String::length);

            ToLongFunction<String> unchecked = CheckedToLongFunction.unchecked(function);

            assertEquals(3L, unchecked.applyAsLong("foo"));

            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verify(function).applyAsLong("foo");
            verifyNoMoreInteractions(function);
        }

        @Test
        void testArgumentThrowsChecked() throws IOException {
            CheckedToLongFunction<String, IOException> function = Spied.checkedToLongFunction(s -> {
                throw new IOException(s);
            });

            ToLongFunction<String> unchecked = CheckedToLongFunction.unchecked(function);

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.applyAsLong("foo"));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo", cause.getMessage());

            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verify(function).applyAsLong("foo");
            verifyNoMoreInteractions(function);
        }

        @Test
        void testArgumentThrowsUnchecked() throws IOException {
            CheckedToLongFunction<String, IOException> function = Spied.checkedToLongFunction(s -> {
                throw new IllegalArgumentException(s);
            });

            ToLongFunction<String> unchecked = CheckedToLongFunction.unchecked(function);

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> unchecked.applyAsLong("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verify(function).applyAsLong("foo");
            verifyNoMoreInteractions(function);
        }
    }

    @Nested
    class CheckedWrapper {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> CheckedToLongFunction.checked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedToLongFunction<String, IOException> function = CheckedToLongFunction.checked(String::length);

            assertEquals(3L, function.applyAsLong("foo"));
        }

        @Test
        void testArgumentThrowsUnchecked() {
            CheckedToLongFunction<String, IOException> function = CheckedToLongFunction.checked(s -> {
                throw new UncheckedException(s, new IOException());
            });

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> function.applyAsLong("foo"));
            assertEquals("foo", thrown.getMessage());
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertNull(cause.getMessage());
        }
    }

    @Nested
    class CheckedUnwrapper {

        @Test
        void testNullArguments() {
            ToLongFunction<String> function = String::length;

            assertThrows(NullPointerException.class, () -> CheckedToLongFunction.checked(null, IOException.class));
            assertThrows(NullPointerException.class, () -> CheckedToLongFunction.checked(function, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedToLongFunction<String, IOException> function = CheckedToLongFunction.checked(String::length, IOException.class);

            assertEquals(3L, function.applyAsLong("foo"));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                CheckedToLongFunction<String, IOException> function = CheckedToLongFunction.checked(s -> {
                    throw new UncheckedException(new IOException(s));
                }, IOException.class);

                IOException thrown = assertThrows(IOException.class, () -> function.applyAsLong("foo"));
                assertEquals("foo", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                CheckedToLongFunction<String, IOException> function = CheckedToLongFunction.checked(s -> {
                    throw new UncheckedException(new FileNotFoundException(s));
                }, IOException.class);

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class, () -> function.applyAsLong("foo"));
                assertEquals("foo", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                CheckedToLongFunction<String, IOException> function = CheckedToLongFunction.checked(s -> {
                    throw new UncheckedException(new ParseException(s, 0));
                }, IOException.class);

                UncheckedException thrown = assertThrows(UncheckedException.class, () -> function.applyAsLong("foo"));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            CheckedToLongFunction<String, IOException> function = CheckedToLongFunction.checked(s -> {
                throw new IllegalStateException(s);
            }, IOException.class);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> function.applyAsLong("foo"));
            assertEquals("foo", thrown.getMessage());
        }
    }

    @Nested
    class InvokeAndUnwrap {

        @Test
        void testNullArguments() throws IOException {
            ToLongFunction<String> function = s -> String.valueOf(s).length();

            assertThrows(NullPointerException.class, () -> CheckedToLongFunction.invokeAndUnwrap(null, "foo", IOException.class));
            assertThrows(NullPointerException.class, () -> CheckedToLongFunction.invokeAndUnwrap(function, "foo", null));

            assertEquals(4L, CheckedToLongFunction.invokeAndUnwrap(function, null, IOException.class));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ToLongFunction<String> function = String::length;

            assertEquals(3L, CheckedToLongFunction.invokeAndUnwrap(function, "foo", IOException.class));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                ToLongFunction<String> function = s -> {
                    throw new UncheckedException(new IOException(s));
                };

                IOException thrown = assertThrows(IOException.class, () -> CheckedToLongFunction.invokeAndUnwrap(function, "foo", IOException.class));
                assertEquals("foo", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                ToLongFunction<String> function = s -> {
                    throw new UncheckedException(new FileNotFoundException(s));
                };

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class,
                        () -> CheckedToLongFunction.invokeAndUnwrap(function, "foo", IOException.class));
                assertEquals("foo", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                ToLongFunction<String> function = s -> {
                    throw new UncheckedException(new ParseException(s, 0));
                };

                UncheckedException thrown = assertThrows(UncheckedException.class,
                        () -> CheckedToLongFunction.invokeAndUnwrap(function, "foo", IOException.class));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            ToLongFunction<String> function = s -> {
                throw new IllegalStateException(s);
            };

            IllegalStateException thrown = assertThrows(IllegalStateException.class,
                    () -> CheckedToLongFunction.invokeAndUnwrap(function, "foo", IOException.class));
            assertEquals("foo", thrown.getMessage());
        }
    }
}
