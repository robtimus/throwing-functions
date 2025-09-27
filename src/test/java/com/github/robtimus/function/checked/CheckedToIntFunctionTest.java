/*
 * CheckedToIntFunctionTest.java
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
class CheckedToIntFunctionTest {

    @Nested
    class OnErrorThrowAsChecked {

        @Test
        void testNullArgument() {
            CheckedToIntFunction<String, IOException> function = Spied.checkedToIntFunction(String::length);

            assertThrows(NullPointerException.class, () -> function.onErrorThrowAsChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedToIntFunction<String, IOException> function = Spied.checkedToIntFunction(String::length);

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedToIntFunction<String, ExecutionException> throwing = function.onErrorThrowAsChecked(errorMapper);

            assertEquals(3, throwing.applyAsInt("foo"));

            verify(function).applyAsInt("foo");
            verify(function).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedToIntFunction<String, IOException> function = Spied.checkedToIntFunction(s -> {
                throw new IOException(s);
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedToIntFunction<String, ExecutionException> throwing = function.onErrorThrowAsChecked(errorMapper);

            ExecutionException thrown = assertThrows(ExecutionException.class, () -> throwing.applyAsInt("foo"));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo", cause.getMessage());

            verify(function).applyAsInt("foo");
            verify(function).onErrorThrowAsChecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(function, errorMapper);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedToIntFunction<String, IOException> function = Spied.checkedToIntFunction(s -> {
                throw new IllegalStateException(s);
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedToIntFunction<String, ExecutionException> throwing = function.onErrorThrowAsChecked(errorMapper);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> throwing.applyAsInt("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(function).applyAsInt("foo");
            verify(function).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }
    }

    @Nested
    class OnErrorThrowAsUnchecked {

        @Test
        void testNullArgument() {
            CheckedToIntFunction<String, IOException> function = Spied.checkedToIntFunction(String::length);

            assertThrows(NullPointerException.class, () -> function.onErrorThrowAsUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedToIntFunction<String, IOException> function = Spied.checkedToIntFunction(String::length);

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            ToIntFunction<String> throwing = function.onErrorThrowAsUnchecked(errorMapper);

            assertEquals(3, throwing.applyAsInt("foo"));

            verify(function).applyAsInt("foo");
            verify(function).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedToIntFunction<String, IOException> function = Spied.checkedToIntFunction(s -> {
                throw new IOException(s);
            });

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            ToIntFunction<String> throwing = function.onErrorThrowAsUnchecked(errorMapper);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> throwing.applyAsInt("foo"));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo", cause.getMessage());

            verify(function).applyAsInt("foo");
            verify(function).onErrorThrowAsUnchecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(function, errorMapper);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedToIntFunction<String, IOException> function = Spied.checkedToIntFunction(s -> {
                throw new IllegalArgumentException(s);
            });

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            ToIntFunction<String> throwing = function.onErrorThrowAsUnchecked(errorMapper);

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> throwing.applyAsInt("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(function).applyAsInt("foo");
            verify(function).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }
    }

    @Nested
    class OnErrorHandleChecked {

        @Test
        void testNullArgument() {
            CheckedToIntFunction<String, IOException> function = Spied.checkedToIntFunction(String::length);

            assertThrows(NullPointerException.class, () -> function.onErrorHandleChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedToIntFunction<String, IOException> function = Spied.checkedToIntFunction(String::length);

            CheckedToIntFunction<IOException, ExecutionException> errorHandler = Spied.checkedToIntFunction(e -> 0);

            CheckedToIntFunction<String, ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

            assertEquals(3, handling.applyAsInt("foo"));

            verify(function).applyAsInt("foo");
            verify(function).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException, ExecutionException {
                CheckedToIntFunction<String, IOException> function = Spied.checkedToIntFunction(s -> {
                    throw new IOException(s);
                });

                CheckedToIntFunction<IOException, ExecutionException> errorHandler = Spied.checkedToIntFunction(e -> 0);

                CheckedToIntFunction<String, ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

                assertEquals(0, handling.applyAsInt("foo"));

                verify(function).applyAsInt("foo");
                verify(function).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsInt(any());
                verifyNoMoreInteractions(function, errorHandler);
            }

            @Test
            void testHandlerThrowsChecked() throws IOException, ExecutionException {
                CheckedToIntFunction<String, IOException> function = Spied.checkedToIntFunction(s -> {
                    throw new IOException(s);
                });

                CheckedToIntFunction<IOException, ExecutionException> errorHandler = Spied.checkedToIntFunction(e -> {
                    throw new ExecutionException(e);
                });

                CheckedToIntFunction<String, ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

                ExecutionException thrown = assertThrows(ExecutionException.class, () -> handling.applyAsInt("foo"));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());

                verify(function).applyAsInt("foo");
                verify(function).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsInt(any());
                verifyNoMoreInteractions(function, errorHandler);
            }

            @Test
            void testHandlerThrowsUnchecked() throws IOException, ExecutionException {
                CheckedToIntFunction<String, IOException> function = Spied.checkedToIntFunction(s -> {
                    throw new IOException(s);
                });

                CheckedToIntFunction<IOException, ExecutionException> errorHandler = Spied.checkedToIntFunction(e -> {
                    throw new IllegalStateException(e);
                });

                CheckedToIntFunction<String, ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.applyAsInt("foo"));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());

                verify(function).applyAsInt("foo");
                verify(function).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsInt(any());
                verifyNoMoreInteractions(function, errorHandler);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedToIntFunction<String, IOException> function = Spied.checkedToIntFunction(s -> {
                throw new IllegalStateException(s);
            });

            CheckedToIntFunction<IOException, ExecutionException> errorHandler = Spied.checkedToIntFunction(e -> 0);

            CheckedToIntFunction<String, ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.applyAsInt("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(function).applyAsInt("foo");
            verify(function).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }
    }

    @Nested
    class OnErrorHandleUnchecked {

        @Test
        void testNullArgument() {
            CheckedToIntFunction<String, IOException> function = Spied.checkedToIntFunction(String::length);

            assertThrows(NullPointerException.class, () -> function.onErrorHandleUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedToIntFunction<String, IOException> function = Spied.checkedToIntFunction(String::length);

            ToIntFunction<IOException> errorHandler = Spied.toIntFunction(e -> 0);

            ToIntFunction<String> handling = function.onErrorHandleUnchecked(errorHandler);

            assertEquals(3, handling.applyAsInt("foo"));

            verify(function).applyAsInt("foo");
            verify(function).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException {
                CheckedToIntFunction<String, IOException> function = Spied.checkedToIntFunction(s -> {
                    throw new IOException(s);
                });

                ToIntFunction<IOException> errorHandler = Spied.toIntFunction(e -> 0);

                ToIntFunction<String> handling = function.onErrorHandleUnchecked(errorHandler);

                assertEquals(0, handling.applyAsInt("foo"));

                verify(function).applyAsInt("foo");
                verify(function).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).applyAsInt(any());
                verifyNoMoreInteractions(function, errorHandler);
            }

            @Test
            void testHandlerThrowsUnchecked() throws IOException {
                CheckedToIntFunction<String, IOException> function = Spied.checkedToIntFunction(s -> {
                    throw new IOException(s);
                });

                ToIntFunction<IOException> errorHandler = Spied.toIntFunction(e -> {
                    throw new IllegalStateException(e);
                });

                ToIntFunction<String> handling = function.onErrorHandleUnchecked(errorHandler);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.applyAsInt("foo"));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());

                verify(function).applyAsInt("foo");
                verify(function).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).applyAsInt(any());
                verifyNoMoreInteractions(function, errorHandler);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedToIntFunction<String, IOException> function = Spied.checkedToIntFunction(s -> {
                throw new IllegalStateException(s);
            });

            ToIntFunction<IOException> errorHandler = Spied.toIntFunction(e -> 0);

            ToIntFunction<String> handling = function.onErrorHandleUnchecked(errorHandler);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.applyAsInt("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(function).applyAsInt("foo");
            verify(function).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }
    }

    @Nested
    class OnErrorApplyChecked {

        @Test
        void testNullArgument() {
            CheckedToIntFunction<String, IOException> function = Spied.checkedToIntFunction(String::length);

            assertThrows(NullPointerException.class, () -> function.onErrorApplyChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedToIntFunction<String, IOException> function = Spied.checkedToIntFunction(String::length);

            CheckedToIntFunction<String, ExecutionException> fallback = Spied.checkedToIntFunction(s -> 2 * s.length());

            CheckedToIntFunction<String, ExecutionException> applying = function.onErrorApplyChecked(fallback);

            assertEquals(3, applying.applyAsInt("foo"));

            verify(function).applyAsInt("foo");
            verify(function).onErrorApplyChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                CheckedToIntFunction<String, IOException> function = Spied.checkedToIntFunction(s -> {
                    throw new IOException(s);
                });

                CheckedToIntFunction<String, ParseException> fallback = Spied.checkedToIntFunction(s -> 2 * s.length());

                CheckedToIntFunction<String, ParseException> applying = function.onErrorApplyChecked(fallback);

                assertEquals(6, applying.applyAsInt("foo"));

                verify(function).applyAsInt("foo");
                verify(function).onErrorApplyChecked(fallback);
                verify(fallback).applyAsInt("foo");
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                CheckedToIntFunction<String, IOException> function = Spied.checkedToIntFunction(s -> {
                    throw new IOException(s);
                });

                CheckedToIntFunction<String, ParseException> fallback = Spied.checkedToIntFunction(s -> {
                    throw new ParseException(s, 0);
                });

                CheckedToIntFunction<String, ParseException> applying = function.onErrorApplyChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> applying.applyAsInt("foo"));
                assertEquals("foo", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(function).applyAsInt("foo");
                verify(function).onErrorApplyChecked(fallback);
                verify(fallback).applyAsInt("foo");
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsUnchecked() throws IOException, ParseException {
                CheckedToIntFunction<String, IOException> function = Spied.checkedToIntFunction(s -> {
                    throw new IOException(s);
                });

                CheckedToIntFunction<String, ParseException> fallback = Spied.checkedToIntFunction(s -> {
                    throw new IllegalStateException(s);
                });

                CheckedToIntFunction<String, ParseException> applying = function.onErrorApplyChecked(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> applying.applyAsInt("foo"));
                assertEquals("foo", thrown.getMessage());

                verify(function).applyAsInt("foo");
                verify(function).onErrorApplyChecked(fallback);
                verify(fallback).applyAsInt("foo");
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedToIntFunction<String, IOException> function = Spied.checkedToIntFunction(s -> {
                throw new IllegalStateException(s);
            });

            CheckedToIntFunction<String, ParseException> fallback = Spied.checkedToIntFunction(s -> 2 * s.length());

            CheckedToIntFunction<String, ParseException> applying = function.onErrorApplyChecked(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> applying.applyAsInt("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(function).applyAsInt("foo");
            verify(function).onErrorApplyChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorApplyUnchecked {

        @Test
        void testNullArgument() {
            CheckedToIntFunction<String, IOException> function = Spied.checkedToIntFunction(String::length);

            assertThrows(NullPointerException.class, () -> function.onErrorApplyUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedToIntFunction<String, IOException> function = Spied.checkedToIntFunction(String::length);

            ToIntFunction<String> fallback = Spied.toIntFunction(s -> 2 * s.length());

            ToIntFunction<String> applying = function.onErrorApplyUnchecked(fallback);

            assertEquals(3, applying.applyAsInt("foo"));

            verify(function).applyAsInt("foo");
            verify(function).onErrorApplyUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                CheckedToIntFunction<String, IOException> function = Spied.checkedToIntFunction(s -> {
                    throw new IOException(s);
                });

                ToIntFunction<String> fallback = Spied.toIntFunction(s -> 2 * s.length());

                ToIntFunction<String> applying = function.onErrorApplyUnchecked(fallback);

                assertEquals(6, applying.applyAsInt("foo"));

                verify(function).applyAsInt("foo");
                verify(function).onErrorApplyUnchecked(fallback);
                verify(fallback).applyAsInt("foo");
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsUnchecked() throws IOException {
                CheckedToIntFunction<String, IOException> function = Spied.checkedToIntFunction(s -> {
                    throw new IOException(s);
                });

                ToIntFunction<String> fallback = Spied.toIntFunction(s -> {
                    throw new IllegalStateException(s);
                });

                ToIntFunction<String> applying = function.onErrorApplyUnchecked(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> applying.applyAsInt("foo"));
                assertEquals("foo", thrown.getMessage());

                verify(function).applyAsInt("foo");
                verify(function).onErrorApplyUnchecked(fallback);
                verify(fallback).applyAsInt("foo");
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedToIntFunction<String, IOException> function = Spied.checkedToIntFunction(s -> {
                throw new IllegalStateException(s);
            });

            ToIntFunction<String> fallback = Spied.toIntFunction(s -> 2 * s.length());

            ToIntFunction<String> applying = function.onErrorApplyUnchecked(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> applying.applyAsInt("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(function).applyAsInt("foo");
            verify(function).onErrorApplyUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorGetChecked {

        @Test
        void testNullArgument() {
            CheckedToIntFunction<String, IOException> function = Spied.checkedToIntFunction(String::length);

            assertThrows(NullPointerException.class, () -> function.onErrorGetChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedToIntFunction<String, IOException> function = Spied.checkedToIntFunction(String::length);

            CheckedIntSupplier<ExecutionException> fallback = Spied.checkedIntSupplier(() -> 0);

            CheckedToIntFunction<String, ExecutionException> getting = function.onErrorGetChecked(fallback);

            assertEquals(3, getting.applyAsInt("foo"));

            verify(function).applyAsInt("foo");
            verify(function).onErrorGetChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                CheckedToIntFunction<String, IOException> function = Spied.checkedToIntFunction(s -> {
                    throw new IOException(s);
                });

                CheckedIntSupplier<ParseException> fallback = Spied.checkedIntSupplier(() -> 0);

                CheckedToIntFunction<String, ParseException> getting = function.onErrorGetChecked(fallback);

                assertEquals(0, getting.applyAsInt("foo"));

                verify(function).applyAsInt("foo");
                verify(function).onErrorGetChecked(fallback);
                verify(fallback).getAsInt();
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                CheckedToIntFunction<String, IOException> function = Spied.checkedToIntFunction(s -> {
                    throw new IOException(s);
                });

                CheckedIntSupplier<ParseException> fallback = Spied.checkedIntSupplier(() -> {
                    throw new ParseException("bar", 0);
                });

                CheckedToIntFunction<String, ParseException> getting = function.onErrorGetChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> getting.applyAsInt("foo"));
                assertEquals("bar", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(function).applyAsInt("foo");
                verify(function).onErrorGetChecked(fallback);
                verify(fallback).getAsInt();
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsUnchecked() throws IOException, ParseException {
                CheckedToIntFunction<String, IOException> function = Spied.checkedToIntFunction(s -> {
                    throw new IOException(s);
                });

                CheckedIntSupplier<ParseException> fallback = Spied.checkedIntSupplier(() -> {
                    throw new IllegalStateException("bar");
                });

                CheckedToIntFunction<String, ParseException> getting = function.onErrorGetChecked(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> getting.applyAsInt("foo"));
                assertEquals("bar", thrown.getMessage());

                verify(function).applyAsInt("foo");
                verify(function).onErrorGetChecked(fallback);
                verify(fallback).getAsInt();
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedToIntFunction<String, IOException> function = Spied.checkedToIntFunction(s -> {
                throw new IllegalStateException(s);
            });

            CheckedIntSupplier<ParseException> fallback = Spied.checkedIntSupplier(() -> 0);

            CheckedToIntFunction<String, ParseException> getting = function.onErrorGetChecked(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> getting.applyAsInt("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(function).applyAsInt("foo");
            verify(function).onErrorGetChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorGetUnchecked {

        @Test
        void testNullArgument() {
            CheckedToIntFunction<String, IOException> function = Spied.checkedToIntFunction(String::length);

            assertThrows(NullPointerException.class, () -> function.onErrorGetUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedToIntFunction<String, IOException> function = Spied.checkedToIntFunction(String::length);

            IntSupplier fallback = Spied.intSupplier(() -> 0);

            ToIntFunction<String> getting = function.onErrorGetUnchecked(fallback);

            assertEquals(3, getting.applyAsInt("foo"));

            verify(function).applyAsInt("foo");
            verify(function).onErrorGetUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                CheckedToIntFunction<String, IOException> function = Spied.checkedToIntFunction(s -> {
                    throw new IOException(s);
                });

                IntSupplier fallback = Spied.intSupplier(() -> 0);

                ToIntFunction<String> getting = function.onErrorGetUnchecked(fallback);

                assertEquals(0, getting.applyAsInt("foo"));

                verify(function).applyAsInt("foo");
                verify(function).onErrorGetUnchecked(fallback);
                verify(fallback).getAsInt();
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsUnchecked() throws IOException {
                CheckedToIntFunction<String, IOException> function = Spied.checkedToIntFunction(s -> {
                    throw new IOException(s);
                });

                IntSupplier fallback = Spied.intSupplier(() -> {
                    throw new IllegalStateException("bar");
                });

                ToIntFunction<String> getting = function.onErrorGetUnchecked(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> getting.applyAsInt("foo"));
                assertEquals("bar", thrown.getMessage());

                verify(function).applyAsInt("foo");
                verify(function).onErrorGetUnchecked(fallback);
                verify(fallback).getAsInt();
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedToIntFunction<String, IOException> function = Spied.checkedToIntFunction(s -> {
                throw new IllegalStateException(s);
            });

            IntSupplier fallback = Spied.intSupplier(() -> 0);

            ToIntFunction<String> getting = function.onErrorGetUnchecked(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> getting.applyAsInt("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(function).applyAsInt("foo");
            verify(function).onErrorGetUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorReturn {

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedToIntFunction<String, IOException> function = Spied.checkedToIntFunction(String::length);

            ToIntFunction<String> returning = function.onErrorReturn(0);

            assertEquals(3, returning.applyAsInt("foo"));

            verify(function).applyAsInt("foo");
            verify(function).onErrorReturn(0);
            verifyNoMoreInteractions(function);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedToIntFunction<String, IOException> function = Spied.checkedToIntFunction(s -> {
                throw new IOException(s);
            });

            ToIntFunction<String> returning = function.onErrorReturn(0);

            assertEquals(0, returning.applyAsInt("foo"));

            verify(function).applyAsInt("foo");
            verify(function).onErrorReturn(0);
            verifyNoMoreInteractions(function);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedToIntFunction<String, IOException> function = Spied.checkedToIntFunction(s -> {
                throw new IllegalStateException(s);
            });

            ToIntFunction<String> returning = function.onErrorReturn(0);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> returning.applyAsInt("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(function).applyAsInt("foo");
            verify(function).onErrorReturn(0);
            verifyNoMoreInteractions(function);
        }
    }

    @Nested
    class Unchecked {

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedToIntFunction<String, IOException> function = Spied.checkedToIntFunction(String::length);

            ToIntFunction<String> unchecked = function.unchecked();

            assertEquals(3, unchecked.applyAsInt("foo"));

            verify(function).applyAsInt("foo");
            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(function);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedToIntFunction<String, IOException> function = Spied.checkedToIntFunction(s -> {
                throw new IOException(s);
            });

            ToIntFunction<String> unchecked = function.unchecked();

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.applyAsInt("foo"));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo", cause.getMessage());

            verify(function).applyAsInt("foo");
            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(function);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedToIntFunction<String, IOException> function = Spied.checkedToIntFunction(s -> {
                throw new IllegalArgumentException(s);
            });

            ToIntFunction<String> unchecked = function.unchecked();

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> unchecked.applyAsInt("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(function).applyAsInt("foo");
            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(function);
        }
    }

    @Nested
    class Of {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> CheckedToIntFunction.of(null));
        }

        @Test
        void testNonNullArgument() throws IOException {
            CheckedToIntFunction<String, IOException> function = CheckedToIntFunction.of(String::length);

            assertEquals(3, function.applyAsInt("foo"));
        }
    }

    @Nested
    class UncheckedFactory {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> CheckedToIntFunction.unchecked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedToIntFunction<String, IOException> function = Spied.checkedToIntFunction(String::length);

            ToIntFunction<String> unchecked = CheckedToIntFunction.unchecked(function);

            assertEquals(3, unchecked.applyAsInt("foo"));

            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verify(function).applyAsInt("foo");
            verifyNoMoreInteractions(function);
        }

        @Test
        void testArgumentThrowsChecked() throws IOException {
            CheckedToIntFunction<String, IOException> function = Spied.checkedToIntFunction(s -> {
                throw new IOException(s);
            });

            ToIntFunction<String> unchecked = CheckedToIntFunction.unchecked(function);

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.applyAsInt("foo"));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo", cause.getMessage());

            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verify(function).applyAsInt("foo");
            verifyNoMoreInteractions(function);
        }

        @Test
        void testArgumentThrowsUnchecked() throws IOException {
            CheckedToIntFunction<String, IOException> function = Spied.checkedToIntFunction(s -> {
                throw new IllegalArgumentException(s);
            });

            ToIntFunction<String> unchecked = CheckedToIntFunction.unchecked(function);

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> unchecked.applyAsInt("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verify(function).applyAsInt("foo");
            verifyNoMoreInteractions(function);
        }
    }

    @Nested
    class CheckedWrapper {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> CheckedToIntFunction.checked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedToIntFunction<String, IOException> function = CheckedToIntFunction.checked(String::length);

            assertEquals(3, function.applyAsInt("foo"));
        }

        @Test
        void testArgumentThrowsUnchecked() {
            CheckedToIntFunction<String, IOException> function = CheckedToIntFunction.checked(s -> {
                throw new UncheckedException(s, new IOException());
            });

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> function.applyAsInt("foo"));
            assertEquals("foo", thrown.getMessage());
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertNull(cause.getMessage());
        }
    }

    @Nested
    class CheckedUnwrapper {

        @Test
        void testNullArguments() {
            ToIntFunction<String> function = String::length;

            assertThrows(NullPointerException.class, () -> CheckedToIntFunction.checked(null, IOException.class));
            assertThrows(NullPointerException.class, () -> CheckedToIntFunction.checked(function, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedToIntFunction<String, IOException> function = CheckedToIntFunction.checked(String::length, IOException.class);

            assertEquals(3, function.applyAsInt("foo"));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                CheckedToIntFunction<String, IOException> function = CheckedToIntFunction.checked(s -> {
                    throw new UncheckedException(new IOException(s));
                }, IOException.class);

                IOException thrown = assertThrows(IOException.class, () -> function.applyAsInt("foo"));
                assertEquals("foo", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                CheckedToIntFunction<String, IOException> function = CheckedToIntFunction.checked(s -> {
                    throw new UncheckedException(new FileNotFoundException(s));
                }, IOException.class);

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class, () -> function.applyAsInt("foo"));
                assertEquals("foo", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                CheckedToIntFunction<String, IOException> function = CheckedToIntFunction.checked(s -> {
                    throw new UncheckedException(new ParseException(s, 0));
                }, IOException.class);

                UncheckedException thrown = assertThrows(UncheckedException.class, () -> function.applyAsInt("foo"));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            CheckedToIntFunction<String, IOException> function = CheckedToIntFunction.checked(s -> {
                throw new IllegalStateException(s);
            }, IOException.class);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> function.applyAsInt("foo"));
            assertEquals("foo", thrown.getMessage());
        }
    }

    @Nested
    class InvokeAndUnwrap {

        @Test
        void testNullArguments() throws IOException {
            ToIntFunction<String> function = s -> String.valueOf(s).length();

            assertThrows(NullPointerException.class, () -> CheckedToIntFunction.invokeAndUnwrap(null, "foo", IOException.class));
            assertThrows(NullPointerException.class, () -> CheckedToIntFunction.invokeAndUnwrap(function, "foo", null));

            assertEquals(4, CheckedToIntFunction.invokeAndUnwrap(function, null, IOException.class));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ToIntFunction<String> function = String::length;

            assertEquals(3, CheckedToIntFunction.invokeAndUnwrap(function, "foo", IOException.class));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                ToIntFunction<String> function = s -> {
                    throw new UncheckedException(new IOException(s));
                };

                IOException thrown = assertThrows(IOException.class, () -> CheckedToIntFunction.invokeAndUnwrap(function, "foo", IOException.class));
                assertEquals("foo", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                ToIntFunction<String> function = s -> {
                    throw new UncheckedException(new FileNotFoundException(s));
                };

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class,
                        () -> CheckedToIntFunction.invokeAndUnwrap(function, "foo", IOException.class));
                assertEquals("foo", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                ToIntFunction<String> function = s -> {
                    throw new UncheckedException(new ParseException(s, 0));
                };

                UncheckedException thrown = assertThrows(UncheckedException.class,
                        () -> CheckedToIntFunction.invokeAndUnwrap(function, "foo", IOException.class));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            ToIntFunction<String> function = s -> {
                throw new IllegalStateException(s);
            };

            IllegalStateException thrown = assertThrows(IllegalStateException.class,
                    () -> CheckedToIntFunction.invokeAndUnwrap(function, "foo", IOException.class));
            assertEquals("foo", thrown.getMessage());
        }
    }
}
