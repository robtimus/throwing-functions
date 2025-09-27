/*
 * CheckedIntToLongFunctionTest.java
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
import java.util.function.IntToLongFunction;
import java.util.function.LongSupplier;
import java.util.function.ToLongFunction;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("nls")
class CheckedIntToLongFunctionTest {

    @Nested
    class OnErrorThrowAsChecked {

        @Test
        void testNullArgument() {
            CheckedIntToLongFunction<IOException> function = Spied.checkedIntToLongFunction(i -> i + 1);

            assertThrows(NullPointerException.class, () -> function.onErrorThrowAsChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedIntToLongFunction<IOException> function = Spied.checkedIntToLongFunction(i -> i + 1);

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedIntToLongFunction<ExecutionException> throwing = function.onErrorThrowAsChecked(errorMapper);

            assertEquals(2L, throwing.applyAsLong(1));

            verify(function).applyAsLong(1);
            verify(function).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedIntToLongFunction<IOException> function = Spied.checkedIntToLongFunction(i -> {
                throw new IOException(Integer.toString(i));
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedIntToLongFunction<ExecutionException> throwing = function.onErrorThrowAsChecked(errorMapper);

            ExecutionException thrown = assertThrows(ExecutionException.class, () -> throwing.applyAsLong(1));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1", cause.getMessage());

            verify(function).applyAsLong(1);
            verify(function).onErrorThrowAsChecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(function, errorMapper);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedIntToLongFunction<IOException> function = Spied.checkedIntToLongFunction(i -> {
                throw new IllegalStateException(Integer.toString(i));
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedIntToLongFunction<ExecutionException> throwing = function.onErrorThrowAsChecked(errorMapper);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> throwing.applyAsLong(1));
            assertEquals("1", thrown.getMessage());

            verify(function).applyAsLong(1);
            verify(function).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }
    }

    @Nested
    class OnErrorThrowAsUnchecked {

        @Test
        void testNullArgument() {
            CheckedIntToLongFunction<IOException> function = Spied.checkedIntToLongFunction(i -> i + 1);

            assertThrows(NullPointerException.class, () -> function.onErrorThrowAsUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedIntToLongFunction<IOException> function = Spied.checkedIntToLongFunction(i -> i + 1);

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            IntToLongFunction throwing = function.onErrorThrowAsUnchecked(errorMapper);

            assertEquals(2L, throwing.applyAsLong(1));

            verify(function).applyAsLong(1);
            verify(function).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedIntToLongFunction<IOException> function = Spied.checkedIntToLongFunction(i -> {
                throw new IOException(Integer.toString(i));
            });

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            IntToLongFunction throwing = function.onErrorThrowAsUnchecked(errorMapper);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> throwing.applyAsLong(1));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1", cause.getMessage());

            verify(function).applyAsLong(1);
            verify(function).onErrorThrowAsUnchecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(function, errorMapper);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedIntToLongFunction<IOException> function = Spied.checkedIntToLongFunction(i -> {
                throw new IllegalArgumentException(Integer.toString(i));
            });

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            IntToLongFunction throwing = function.onErrorThrowAsUnchecked(errorMapper);

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> throwing.applyAsLong(1));
            assertEquals("1", thrown.getMessage());

            verify(function).applyAsLong(1);
            verify(function).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }
    }

    @Nested
    class OnErrorHandleChecked {

        @Test
        void testNullArgument() {
            CheckedIntToLongFunction<IOException> function = Spied.checkedIntToLongFunction(i -> i + 1);

            assertThrows(NullPointerException.class, () -> function.onErrorHandleChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedIntToLongFunction<IOException> function = Spied.checkedIntToLongFunction(i -> i + 1);

            CheckedToLongFunction<IOException, ExecutionException> errorHandler = Spied.checkedToLongFunction(e -> 0L);

            CheckedIntToLongFunction<ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

            assertEquals(2L, handling.applyAsLong(1));

            verify(function).applyAsLong(1);
            verify(function).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException, ExecutionException {
                CheckedIntToLongFunction<IOException> function = Spied.checkedIntToLongFunction(i -> {
                    throw new IOException(Integer.toString(i));
                });

                CheckedToLongFunction<IOException, ExecutionException> errorHandler = Spied.checkedToLongFunction(e -> 0L);

                CheckedIntToLongFunction<ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

                assertEquals(0L, handling.applyAsLong(1));

                verify(function).applyAsLong(1);
                verify(function).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsLong(any());
                verifyNoMoreInteractions(function, errorHandler);
            }

            @Test
            void testHandlerThrowsChecked() throws IOException, ExecutionException {
                CheckedIntToLongFunction<IOException> function = Spied.checkedIntToLongFunction(i -> {
                    throw new IOException(Integer.toString(i));
                });

                CheckedToLongFunction<IOException, ExecutionException> errorHandler = Spied.checkedToLongFunction(e -> {
                    throw new ExecutionException(e);
                });

                CheckedIntToLongFunction<ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

                ExecutionException thrown = assertThrows(ExecutionException.class, () -> handling.applyAsLong(1));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("1", cause.getMessage());

                verify(function).applyAsLong(1);
                verify(function).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsLong(any());
                verifyNoMoreInteractions(function, errorHandler);
            }

            @Test
            void testHandlerThrowsUnchecked() throws IOException, ExecutionException {
                CheckedIntToLongFunction<IOException> function = Spied.checkedIntToLongFunction(i -> {
                    throw new IOException(Integer.toString(i));
                });

                CheckedToLongFunction<IOException, ExecutionException> errorHandler = Spied.checkedToLongFunction(e -> {
                    throw new IllegalStateException(e);
                });

                CheckedIntToLongFunction<ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.applyAsLong(1));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("1", cause.getMessage());

                verify(function).applyAsLong(1);
                verify(function).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsLong(any());
                verifyNoMoreInteractions(function, errorHandler);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedIntToLongFunction<IOException> function = Spied.checkedIntToLongFunction(i -> {
                throw new IllegalStateException(Integer.toString(i));
            });

            CheckedToLongFunction<IOException, ExecutionException> errorHandler = Spied.checkedToLongFunction(e -> 0L);

            CheckedIntToLongFunction<ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.applyAsLong(1));
            assertEquals("1", thrown.getMessage());

            verify(function).applyAsLong(1);
            verify(function).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }
    }

    @Nested
    class OnErrorHandleUnchecked {

        @Test
        void testNullArgument() {
            CheckedIntToLongFunction<IOException> function = Spied.checkedIntToLongFunction(i -> i + 1);

            assertThrows(NullPointerException.class, () -> function.onErrorHandleUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedIntToLongFunction<IOException> function = Spied.checkedIntToLongFunction(i -> i + 1);

            ToLongFunction<IOException> errorHandler = Spied.toLongFunction(e -> 0L);

            IntToLongFunction handling = function.onErrorHandleUnchecked(errorHandler);

            assertEquals(2L, handling.applyAsLong(1));

            verify(function).applyAsLong(1);
            verify(function).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException {
                CheckedIntToLongFunction<IOException> function = Spied.checkedIntToLongFunction(i -> {
                    throw new IOException(Integer.toString(i));
                });

                ToLongFunction<IOException> errorHandler = Spied.toLongFunction(e -> 0L);

                IntToLongFunction handling = function.onErrorHandleUnchecked(errorHandler);

                assertEquals(0L, handling.applyAsLong(1));

                verify(function).applyAsLong(1);
                verify(function).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).applyAsLong(any());
                verifyNoMoreInteractions(function, errorHandler);
            }

            @Test
            void testHandlerThrowsUnchecked() throws IOException {
                CheckedIntToLongFunction<IOException> function = Spied.checkedIntToLongFunction(i -> {
                    throw new IOException(Integer.toString(i));
                });

                ToLongFunction<IOException> errorHandler = Spied.toLongFunction(e -> {
                    throw new IllegalStateException(e);
                });

                IntToLongFunction handling = function.onErrorHandleUnchecked(errorHandler);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.applyAsLong(1));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("1", cause.getMessage());

                verify(function).applyAsLong(1);
                verify(function).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).applyAsLong(any());
                verifyNoMoreInteractions(function, errorHandler);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedIntToLongFunction<IOException> function = Spied.checkedIntToLongFunction(i -> {
                throw new IllegalStateException(Integer.toString(i));
            });

            ToLongFunction<IOException> errorHandler = Spied.toLongFunction(e -> 0L);

            IntToLongFunction handling = function.onErrorHandleUnchecked(errorHandler);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.applyAsLong(1));
            assertEquals("1", thrown.getMessage());

            verify(function).applyAsLong(1);
            verify(function).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }
    }

    @Nested
    class OnErrorApplyChecked {

        @Test
        void testNullArgument() {
            CheckedIntToLongFunction<IOException> function = Spied.checkedIntToLongFunction(i -> i + 1);

            assertThrows(NullPointerException.class, () -> function.onErrorApplyChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedIntToLongFunction<IOException> function = Spied.checkedIntToLongFunction(i -> i + 1);

            CheckedIntToLongFunction<ExecutionException> fallback = Spied.checkedIntToLongFunction(i -> i * 3);

            CheckedIntToLongFunction<ExecutionException> applying = function.onErrorApplyChecked(fallback);

            assertEquals(2L, applying.applyAsLong(1));

            verify(function).applyAsLong(1);
            verify(function).onErrorApplyChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                CheckedIntToLongFunction<IOException> function = Spied.checkedIntToLongFunction(i -> {
                    throw new IOException(Integer.toString(i));
                });

                CheckedIntToLongFunction<ParseException> fallback = Spied.checkedIntToLongFunction(i -> i * 3);

                CheckedIntToLongFunction<ParseException> applying = function.onErrorApplyChecked(fallback);

                assertEquals(3L, applying.applyAsLong(1));

                verify(function).applyAsLong(1);
                verify(function).onErrorApplyChecked(fallback);
                verify(fallback).applyAsLong(1);
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                CheckedIntToLongFunction<IOException> function = Spied.checkedIntToLongFunction(i -> {
                    throw new IOException(Integer.toString(i));
                });

                CheckedIntToLongFunction<ParseException> fallback = Spied.checkedIntToLongFunction(i -> {
                    throw new ParseException(Integer.toString(i), 0);
                });

                CheckedIntToLongFunction<ParseException> applying = function.onErrorApplyChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> applying.applyAsLong(1));
                assertEquals("1", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(function).applyAsLong(1);
                verify(function).onErrorApplyChecked(fallback);
                verify(fallback).applyAsLong(1);
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsUnchecked() throws IOException, ParseException {
                CheckedIntToLongFunction<IOException> function = Spied.checkedIntToLongFunction(i -> {
                    throw new IOException(Integer.toString(i));
                });

                CheckedIntToLongFunction<ParseException> fallback = Spied.checkedIntToLongFunction(i -> {
                    throw new IllegalStateException(Integer.toString(i));
                });

                CheckedIntToLongFunction<ParseException> applying = function.onErrorApplyChecked(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> applying.applyAsLong(1));
                assertEquals("1", thrown.getMessage());

                verify(function).applyAsLong(1);
                verify(function).onErrorApplyChecked(fallback);
                verify(fallback).applyAsLong(1);
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedIntToLongFunction<IOException> function = Spied.checkedIntToLongFunction(i -> {
                throw new IllegalStateException(Integer.toString(i));
            });

            CheckedIntToLongFunction<ParseException> fallback = Spied.checkedIntToLongFunction(i -> i * 3);

            CheckedIntToLongFunction<ParseException> applying = function.onErrorApplyChecked(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> applying.applyAsLong(1));
            assertEquals("1", thrown.getMessage());

            verify(function).applyAsLong(1);
            verify(function).onErrorApplyChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorApplyUnchecked {

        @Test
        void testNullArgument() {
            CheckedIntToLongFunction<IOException> function = Spied.checkedIntToLongFunction(i -> i + 1);

            assertThrows(NullPointerException.class, () -> function.onErrorApplyUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedIntToLongFunction<IOException> function = Spied.checkedIntToLongFunction(i -> i + 1);

            IntToLongFunction fallback = Spied.intToLongFunction(i -> i * 3);

            IntToLongFunction applying = function.onErrorApplyUnchecked(fallback);

            assertEquals(2L, applying.applyAsLong(1));

            verify(function).applyAsLong(1);
            verify(function).onErrorApplyUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                CheckedIntToLongFunction<IOException> function = Spied.checkedIntToLongFunction(i -> {
                    throw new IOException(Integer.toString(i));
                });

                IntToLongFunction fallback = Spied.intToLongFunction(i -> i * 3);

                IntToLongFunction applying = function.onErrorApplyUnchecked(fallback);

                assertEquals(3L, applying.applyAsLong(1));

                verify(function).applyAsLong(1);
                verify(function).onErrorApplyUnchecked(fallback);
                verify(fallback).applyAsLong(1);
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsUnchecked() throws IOException {
                CheckedIntToLongFunction<IOException> function = Spied.checkedIntToLongFunction(i -> {
                    throw new IOException(Integer.toString(i));
                });

                IntToLongFunction fallback = Spied.intToLongFunction(i -> {
                    throw new IllegalStateException(Integer.toString(i));
                });

                IntToLongFunction applying = function.onErrorApplyUnchecked(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> applying.applyAsLong(1));
                assertEquals("1", thrown.getMessage());

                verify(function).applyAsLong(1);
                verify(function).onErrorApplyUnchecked(fallback);
                verify(fallback).applyAsLong(1);
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedIntToLongFunction<IOException> function = Spied.checkedIntToLongFunction(i -> {
                throw new IllegalStateException(Integer.toString(i));
            });

            IntToLongFunction fallback = Spied.intToLongFunction(i -> i * 3);

            IntToLongFunction applying = function.onErrorApplyUnchecked(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> applying.applyAsLong(1));
            assertEquals("1", thrown.getMessage());

            verify(function).applyAsLong(1);
            verify(function).onErrorApplyUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorGetChecked {

        @Test
        void testNullArgument() {
            CheckedIntToLongFunction<IOException> function = Spied.checkedIntToLongFunction(i -> i + 1);

            assertThrows(NullPointerException.class, () -> function.onErrorGetChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedIntToLongFunction<IOException> function = Spied.checkedIntToLongFunction(i -> i + 1);

            CheckedLongSupplier<ExecutionException> fallback = Spied.checkedLongSupplier(() -> 0L);

            CheckedIntToLongFunction<ExecutionException> getting = function.onErrorGetChecked(fallback);

            assertEquals(2L, getting.applyAsLong(1));

            verify(function).applyAsLong(1);
            verify(function).onErrorGetChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                CheckedIntToLongFunction<IOException> function = Spied.checkedIntToLongFunction(i -> {
                    throw new IOException(Integer.toString(i));
                });

                CheckedLongSupplier<ParseException> fallback = Spied.checkedLongSupplier(() -> 0L);

                CheckedIntToLongFunction<ParseException> getting = function.onErrorGetChecked(fallback);

                assertEquals(0L, getting.applyAsLong(1));

                verify(function).applyAsLong(1);
                verify(function).onErrorGetChecked(fallback);
                verify(fallback).getAsLong();
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                CheckedIntToLongFunction<IOException> function = Spied.checkedIntToLongFunction(i -> {
                    throw new IOException(Integer.toString(i));
                });

                CheckedLongSupplier<ParseException> fallback = Spied.checkedLongSupplier(() -> {
                    throw new ParseException("bar", 0);
                });

                CheckedIntToLongFunction<ParseException> getting = function.onErrorGetChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> getting.applyAsLong(1));
                assertEquals("bar", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(function).applyAsLong(1);
                verify(function).onErrorGetChecked(fallback);
                verify(fallback).getAsLong();
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsUnchecked() throws IOException, ParseException {
                CheckedIntToLongFunction<IOException> function = Spied.checkedIntToLongFunction(i -> {
                    throw new IOException(Integer.toString(i));
                });

                CheckedLongSupplier<ParseException> fallback = Spied.checkedLongSupplier(() -> {
                    throw new IllegalStateException("bar");
                });

                CheckedIntToLongFunction<ParseException> getting = function.onErrorGetChecked(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> getting.applyAsLong(1));
                assertEquals("bar", thrown.getMessage());

                verify(function).applyAsLong(1);
                verify(function).onErrorGetChecked(fallback);
                verify(fallback).getAsLong();
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedIntToLongFunction<IOException> function = Spied.checkedIntToLongFunction(i -> {
                throw new IllegalStateException(Integer.toString(i));
            });

            CheckedLongSupplier<ParseException> fallback = Spied.checkedLongSupplier(() -> 0L);

            CheckedIntToLongFunction<ParseException> getting = function.onErrorGetChecked(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> getting.applyAsLong(1));
            assertEquals("1", thrown.getMessage());

            verify(function).applyAsLong(1);
            verify(function).onErrorGetChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorGetUnchecked {

        @Test
        void testNullArgument() {
            CheckedIntToLongFunction<IOException> function = Spied.checkedIntToLongFunction(i -> i + 1);

            assertThrows(NullPointerException.class, () -> function.onErrorGetUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedIntToLongFunction<IOException> function = Spied.checkedIntToLongFunction(i -> i + 1);

            LongSupplier fallback = Spied.longSupplier(() -> 0L);

            IntToLongFunction getting = function.onErrorGetUnchecked(fallback);

            assertEquals(2L, getting.applyAsLong(1));

            verify(function).applyAsLong(1);
            verify(function).onErrorGetUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                CheckedIntToLongFunction<IOException> function = Spied.checkedIntToLongFunction(i -> {
                    throw new IOException(Integer.toString(i));
                });

                LongSupplier fallback = Spied.longSupplier(() -> 0L);

                IntToLongFunction getting = function.onErrorGetUnchecked(fallback);

                assertEquals(0L, getting.applyAsLong(1));

                verify(function).applyAsLong(1);
                verify(function).onErrorGetUnchecked(fallback);
                verify(fallback).getAsLong();
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsUnchecked() throws IOException {
                CheckedIntToLongFunction<IOException> function = Spied.checkedIntToLongFunction(i -> {
                    throw new IOException(Integer.toString(i));
                });

                LongSupplier fallback = Spied.longSupplier(() -> {
                    throw new IllegalStateException("bar");
                });

                IntToLongFunction getting = function.onErrorGetUnchecked(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> getting.applyAsLong(1));
                assertEquals("bar", thrown.getMessage());

                verify(function).applyAsLong(1);
                verify(function).onErrorGetUnchecked(fallback);
                verify(fallback).getAsLong();
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedIntToLongFunction<IOException> function = Spied.checkedIntToLongFunction(i -> {
                throw new IllegalStateException(Integer.toString(i));
            });

            LongSupplier fallback = Spied.longSupplier(() -> 0L);

            IntToLongFunction getting = function.onErrorGetUnchecked(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> getting.applyAsLong(1));
            assertEquals("1", thrown.getMessage());

            verify(function).applyAsLong(1);
            verify(function).onErrorGetUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorReturn {

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedIntToLongFunction<IOException> function = Spied.checkedIntToLongFunction(i -> i + 1);

            IntToLongFunction returning = function.onErrorReturn(0L);

            assertEquals(2L, returning.applyAsLong(1));

            verify(function).applyAsLong(1);
            verify(function).onErrorReturn(0L);
            verifyNoMoreInteractions(function);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedIntToLongFunction<IOException> function = Spied.checkedIntToLongFunction(i -> {
                throw new IOException(Integer.toString(i));
            });

            IntToLongFunction returning = function.onErrorReturn(0L);

            assertEquals(0L, returning.applyAsLong(1));

            verify(function).applyAsLong(1);
            verify(function).onErrorReturn(0L);
            verifyNoMoreInteractions(function);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedIntToLongFunction<IOException> function = Spied.checkedIntToLongFunction(i -> {
                throw new IllegalStateException(Integer.toString(i));
            });

            IntToLongFunction returning = function.onErrorReturn(0L);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> returning.applyAsLong(1));
            assertEquals("1", thrown.getMessage());

            verify(function).applyAsLong(1);
            verify(function).onErrorReturn(0L);
            verifyNoMoreInteractions(function);
        }
    }

    @Nested
    class Unchecked {

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedIntToLongFunction<IOException> function = Spied.checkedIntToLongFunction(i -> i + 1);

            IntToLongFunction unchecked = function.unchecked();

            assertEquals(2L, unchecked.applyAsLong(1));

            verify(function).applyAsLong(1);
            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(function);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedIntToLongFunction<IOException> function = Spied.checkedIntToLongFunction(i -> {
                throw new IOException(Integer.toString(i));
            });

            IntToLongFunction unchecked = function.unchecked();

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.applyAsLong(1));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1", cause.getMessage());

            verify(function).applyAsLong(1);
            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(function);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedIntToLongFunction<IOException> function = Spied.checkedIntToLongFunction(i -> {
                throw new IllegalArgumentException(Integer.toString(i));
            });

            IntToLongFunction unchecked = function.unchecked();

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> unchecked.applyAsLong(1));
            assertEquals("1", thrown.getMessage());

            verify(function).applyAsLong(1);
            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(function);
        }
    }

    @Nested
    class Of {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> CheckedIntToLongFunction.of(null));
        }

        @Test
        void testNonNullArgument() throws IOException {
            CheckedIntToLongFunction<IOException> function = CheckedIntToLongFunction.of(i -> i + 1);

            assertEquals(2L, function.applyAsLong(1));
        }
    }

    @Nested
    class UncheckedFactory {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> CheckedIntToLongFunction.unchecked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedIntToLongFunction<IOException> function = Spied.checkedIntToLongFunction(i -> i + 1);

            IntToLongFunction unchecked = CheckedIntToLongFunction.unchecked(function);

            assertEquals(2L, unchecked.applyAsLong(1));

            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verify(function).applyAsLong(1);
            verifyNoMoreInteractions(function);
        }

        @Test
        void testArgumentThrowsChecked() throws IOException {
            CheckedIntToLongFunction<IOException> function = Spied.checkedIntToLongFunction(i -> {
                throw new IOException(Integer.toString(i));
            });

            IntToLongFunction unchecked = CheckedIntToLongFunction.unchecked(function);

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.applyAsLong(1));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1", cause.getMessage());

            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verify(function).applyAsLong(1);
            verifyNoMoreInteractions(function);
        }

        @Test
        void testArgumentThrowsUnchecked() throws IOException {
            CheckedIntToLongFunction<IOException> function = Spied.checkedIntToLongFunction(i -> {
                throw new IllegalArgumentException(Integer.toString(i));
            });

            IntToLongFunction unchecked = CheckedIntToLongFunction.unchecked(function);

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> unchecked.applyAsLong(1));
            assertEquals("1", thrown.getMessage());

            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verify(function).applyAsLong(1);
            verifyNoMoreInteractions(function);
        }
    }

    @Nested
    class CheckedWrapper {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> CheckedIntToLongFunction.checked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedIntToLongFunction<IOException> function = CheckedIntToLongFunction.checked(i -> i + 1);

            assertEquals(2L, function.applyAsLong(1));
        }

        @Test
        void testArgumentThrowsUnchecked() {
            CheckedIntToLongFunction<IOException> function = CheckedIntToLongFunction.checked(i -> {
                throw new UncheckedException(Integer.toString(i), new IOException());
            });

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> function.applyAsLong(1));
            assertEquals("1", thrown.getMessage());
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertNull(cause.getMessage());
        }
    }

    @Nested
    class CheckedUnwrapper {

        @Test
        void testNullArguments() {
            IntToLongFunction function = i -> i + 1;

            assertThrows(NullPointerException.class, () -> CheckedIntToLongFunction.checked(null, IOException.class));
            assertThrows(NullPointerException.class, () -> CheckedIntToLongFunction.checked(function, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedIntToLongFunction<IOException> function = CheckedIntToLongFunction.checked(i -> i + 1, IOException.class);

            assertEquals(2L, function.applyAsLong(1));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                CheckedIntToLongFunction<IOException> function = CheckedIntToLongFunction.checked(i -> {
                    throw new UncheckedException(new IOException(Integer.toString(i)));
                }, IOException.class);

                IOException thrown = assertThrows(IOException.class, () -> function.applyAsLong(1));
                assertEquals("1", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                CheckedIntToLongFunction<IOException> function = CheckedIntToLongFunction.checked(i -> {
                    throw new UncheckedException(new FileNotFoundException(Integer.toString(i)));
                }, IOException.class);

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class, () -> function.applyAsLong(1));
                assertEquals("1", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                CheckedIntToLongFunction<IOException> function = CheckedIntToLongFunction.checked(i -> {
                    throw new UncheckedException(new ParseException(Integer.toString(i), 0));
                }, IOException.class);

                UncheckedException thrown = assertThrows(UncheckedException.class, () -> function.applyAsLong(1));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("1", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            CheckedIntToLongFunction<IOException> function = CheckedIntToLongFunction.checked(i -> {
                throw new IllegalStateException(Integer.toString(i));
            }, IOException.class);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> function.applyAsLong(1));
            assertEquals("1", thrown.getMessage());
        }
    }

    @Nested
    class InvokeAndUnwrap {

        @Test
        void testNullArguments() {
            IntToLongFunction function = i -> String.valueOf(Integer.toString(i)).length();

            assertThrows(NullPointerException.class, () -> CheckedIntToLongFunction.invokeAndUnwrap(null, 1, IOException.class));
            assertThrows(NullPointerException.class, () -> CheckedIntToLongFunction.invokeAndUnwrap(function, 1, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            IntToLongFunction function = i -> i + 1;

            assertEquals(2L, CheckedIntToLongFunction.invokeAndUnwrap(function, 1, IOException.class));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                IntToLongFunction function = i -> {
                    throw new UncheckedException(new IOException(Integer.toString(i)));
                };

                IOException thrown = assertThrows(IOException.class,
                        () -> CheckedIntToLongFunction.invokeAndUnwrap(function, 1, IOException.class));
                assertEquals("1", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                IntToLongFunction function = i -> {
                    throw new UncheckedException(new FileNotFoundException(Integer.toString(i)));
                };

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class,
                        () -> CheckedIntToLongFunction.invokeAndUnwrap(function, 1, IOException.class));
                assertEquals("1", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                IntToLongFunction function = i -> {
                    throw new UncheckedException(new ParseException(Integer.toString(i), 0));
                };

                UncheckedException thrown = assertThrows(UncheckedException.class,
                        () -> CheckedIntToLongFunction.invokeAndUnwrap(function, 1, IOException.class));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("1", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            IntToLongFunction function = i -> {
                throw new IllegalStateException(Integer.toString(i));
            };

            IllegalStateException thrown = assertThrows(IllegalStateException.class,
                    () -> CheckedIntToLongFunction.invokeAndUnwrap(function, 1, IOException.class));
            assertEquals("1", thrown.getMessage());
        }
    }
}
