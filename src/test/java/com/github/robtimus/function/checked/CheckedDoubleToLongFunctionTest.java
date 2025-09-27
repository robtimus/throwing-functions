/*
 * CheckedDoubleToLongFunctionTest.java
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
import java.util.function.DoubleToLongFunction;
import java.util.function.Function;
import java.util.function.LongSupplier;
import java.util.function.ToLongFunction;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("nls")
class CheckedDoubleToLongFunctionTest {

    @Nested
    class OnErrorThrowAsChecked {

        @Test
        void testNullArgument() {
            CheckedDoubleToLongFunction<IOException> function = Spied.checkedDoubleToLongFunction(d -> (long) d + 1);

            assertThrows(NullPointerException.class, () -> function.onErrorThrowAsChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedDoubleToLongFunction<IOException> function = Spied.checkedDoubleToLongFunction(d -> (long) d + 1);

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedDoubleToLongFunction<ExecutionException> throwing = function.onErrorThrowAsChecked(errorMapper);

            assertEquals(2L, throwing.applyAsLong(1D));

            verify(function).applyAsLong(1D);
            verify(function).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedDoubleToLongFunction<IOException> function = Spied.checkedDoubleToLongFunction(d -> {
                throw new IOException(Double.toString(d));
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedDoubleToLongFunction<ExecutionException> throwing = function.onErrorThrowAsChecked(errorMapper);

            ExecutionException thrown = assertThrows(ExecutionException.class, () -> throwing.applyAsLong(1D));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1.0", cause.getMessage());

            verify(function).applyAsLong(1D);
            verify(function).onErrorThrowAsChecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(function, errorMapper);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedDoubleToLongFunction<IOException> function = Spied.checkedDoubleToLongFunction(d -> {
                throw new IllegalStateException(Double.toString(d));
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedDoubleToLongFunction<ExecutionException> throwing = function.onErrorThrowAsChecked(errorMapper);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> throwing.applyAsLong(1D));
            assertEquals("1.0", thrown.getMessage());

            verify(function).applyAsLong(1D);
            verify(function).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }
    }

    @Nested
    class OnErrorThrowAsUnchecked {

        @Test
        void testNullArgument() {
            CheckedDoubleToLongFunction<IOException> function = Spied.checkedDoubleToLongFunction(d -> (long) d + 1);

            assertThrows(NullPointerException.class, () -> function.onErrorThrowAsUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedDoubleToLongFunction<IOException> function = Spied.checkedDoubleToLongFunction(d -> (long) d + 1);

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            DoubleToLongFunction throwing = function.onErrorThrowAsUnchecked(errorMapper);

            assertEquals(2L, throwing.applyAsLong(1D));

            verify(function).applyAsLong(1D);
            verify(function).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedDoubleToLongFunction<IOException> function = Spied.checkedDoubleToLongFunction(d -> {
                throw new IOException(Double.toString(d));
            });

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            DoubleToLongFunction throwing = function.onErrorThrowAsUnchecked(errorMapper);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> throwing.applyAsLong(1D));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1.0", cause.getMessage());

            verify(function).applyAsLong(1D);
            verify(function).onErrorThrowAsUnchecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(function, errorMapper);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedDoubleToLongFunction<IOException> function = Spied.checkedDoubleToLongFunction(d -> {
                throw new IllegalArgumentException(Double.toString(d));
            });

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            DoubleToLongFunction throwing = function.onErrorThrowAsUnchecked(errorMapper);

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> throwing.applyAsLong(1D));
            assertEquals("1.0", thrown.getMessage());

            verify(function).applyAsLong(1D);
            verify(function).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }
    }

    @Nested
    class OnErrorHandleChecked {

        @Test
        void testNullArgument() {
            CheckedDoubleToLongFunction<IOException> function = Spied.checkedDoubleToLongFunction(d -> (long) d + 1);

            assertThrows(NullPointerException.class, () -> function.onErrorHandleChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedDoubleToLongFunction<IOException> function = Spied.checkedDoubleToLongFunction(d -> (long) d + 1);

            CheckedToLongFunction<IOException, ExecutionException> errorHandler = Spied.checkedToLongFunction(e -> 0L);

            CheckedDoubleToLongFunction<ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

            assertEquals(2L, handling.applyAsLong(1D));

            verify(function).applyAsLong(1D);
            verify(function).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException, ExecutionException {
                CheckedDoubleToLongFunction<IOException> function = Spied.checkedDoubleToLongFunction(d -> {
                    throw new IOException(Double.toString(d));
                });

                CheckedToLongFunction<IOException, ExecutionException> errorHandler = Spied.checkedToLongFunction(e -> 0L);

                CheckedDoubleToLongFunction<ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

                assertEquals(0L, handling.applyAsLong(1D));

                verify(function).applyAsLong(1D);
                verify(function).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsLong(any());
                verifyNoMoreInteractions(function, errorHandler);
            }

            @Test
            void testHandlerThrowsChecked() throws IOException, ExecutionException {
                CheckedDoubleToLongFunction<IOException> function = Spied.checkedDoubleToLongFunction(d -> {
                    throw new IOException(Double.toString(d));
                });

                CheckedToLongFunction<IOException, ExecutionException> errorHandler = Spied.checkedToLongFunction(e -> {
                    throw new ExecutionException(e);
                });

                CheckedDoubleToLongFunction<ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

                ExecutionException thrown = assertThrows(ExecutionException.class, () -> handling.applyAsLong(1D));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("1.0", cause.getMessage());

                verify(function).applyAsLong(1D);
                verify(function).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsLong(any());
                verifyNoMoreInteractions(function, errorHandler);
            }

            @Test
            void testHandlerThrowsUnchecked() throws IOException, ExecutionException {
                CheckedDoubleToLongFunction<IOException> function = Spied.checkedDoubleToLongFunction(d -> {
                    throw new IOException(Double.toString(d));
                });

                CheckedToLongFunction<IOException, ExecutionException> errorHandler = Spied.checkedToLongFunction(e -> {
                    throw new IllegalStateException(e);
                });

                CheckedDoubleToLongFunction<ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.applyAsLong(1D));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("1.0", cause.getMessage());

                verify(function).applyAsLong(1D);
                verify(function).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsLong(any());
                verifyNoMoreInteractions(function, errorHandler);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedDoubleToLongFunction<IOException> function = Spied.checkedDoubleToLongFunction(d -> {
                throw new IllegalStateException(Double.toString(d));
            });

            CheckedToLongFunction<IOException, ExecutionException> errorHandler = Spied.checkedToLongFunction(e -> 0L);

            CheckedDoubleToLongFunction<ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.applyAsLong(1D));
            assertEquals("1.0", thrown.getMessage());

            verify(function).applyAsLong(1D);
            verify(function).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }
    }

    @Nested
    class OnErrorHandleUnchecked {

        @Test
        void testNullArgument() {
            CheckedDoubleToLongFunction<IOException> function = Spied.checkedDoubleToLongFunction(d -> (long) d + 1);

            assertThrows(NullPointerException.class, () -> function.onErrorHandleUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedDoubleToLongFunction<IOException> function = Spied.checkedDoubleToLongFunction(d -> (long) d + 1);

            ToLongFunction<IOException> errorHandler = Spied.toLongFunction(e -> 0L);

            DoubleToLongFunction handling = function.onErrorHandleUnchecked(errorHandler);

            assertEquals(2L, handling.applyAsLong(1D));

            verify(function).applyAsLong(1D);
            verify(function).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException {
                CheckedDoubleToLongFunction<IOException> function = Spied.checkedDoubleToLongFunction(d -> {
                    throw new IOException(Double.toString(d));
                });

                ToLongFunction<IOException> errorHandler = Spied.toLongFunction(e -> 0L);

                DoubleToLongFunction handling = function.onErrorHandleUnchecked(errorHandler);

                assertEquals(0L, handling.applyAsLong(1D));

                verify(function).applyAsLong(1D);
                verify(function).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).applyAsLong(any());
                verifyNoMoreInteractions(function, errorHandler);
            }

            @Test
            void testHandlerThrowsUnchecked() throws IOException {
                CheckedDoubleToLongFunction<IOException> function = Spied.checkedDoubleToLongFunction(d -> {
                    throw new IOException(Double.toString(d));
                });

                ToLongFunction<IOException> errorHandler = Spied.toLongFunction(e -> {
                    throw new IllegalStateException(e);
                });

                DoubleToLongFunction handling = function.onErrorHandleUnchecked(errorHandler);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.applyAsLong(1D));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("1.0", cause.getMessage());

                verify(function).applyAsLong(1D);
                verify(function).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).applyAsLong(any());
                verifyNoMoreInteractions(function, errorHandler);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedDoubleToLongFunction<IOException> function = Spied.checkedDoubleToLongFunction(d -> {
                throw new IllegalStateException(Double.toString(d));
            });

            ToLongFunction<IOException> errorHandler = Spied.toLongFunction(e -> 0L);

            DoubleToLongFunction handling = function.onErrorHandleUnchecked(errorHandler);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.applyAsLong(1D));
            assertEquals("1.0", thrown.getMessage());

            verify(function).applyAsLong(1D);
            verify(function).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }
    }

    @Nested
    class OnErrorApplyChecked {

        @Test
        void testNullArgument() {
            CheckedDoubleToLongFunction<IOException> function = Spied.checkedDoubleToLongFunction(d -> (long) d + 1);

            assertThrows(NullPointerException.class, () -> function.onErrorApplyChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedDoubleToLongFunction<IOException> function = Spied.checkedDoubleToLongFunction(d -> (long) d + 1);

            CheckedDoubleToLongFunction<ExecutionException> fallback = Spied.checkedDoubleToLongFunction(d -> (long) d * 3);

            CheckedDoubleToLongFunction<ExecutionException> applying = function.onErrorApplyChecked(fallback);

            assertEquals(2L, applying.applyAsLong(1D));

            verify(function).applyAsLong(1D);
            verify(function).onErrorApplyChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                CheckedDoubleToLongFunction<IOException> function = Spied.checkedDoubleToLongFunction(d -> {
                    throw new IOException(Double.toString(d));
                });

                CheckedDoubleToLongFunction<ParseException> fallback = Spied.checkedDoubleToLongFunction(d -> (long) d * 3);

                CheckedDoubleToLongFunction<ParseException> applying = function.onErrorApplyChecked(fallback);

                assertEquals(3L, applying.applyAsLong(1D));

                verify(function).applyAsLong(1D);
                verify(function).onErrorApplyChecked(fallback);
                verify(fallback).applyAsLong(1D);
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                CheckedDoubleToLongFunction<IOException> function = Spied.checkedDoubleToLongFunction(d -> {
                    throw new IOException(Double.toString(d));
                });

                CheckedDoubleToLongFunction<ParseException> fallback = Spied.checkedDoubleToLongFunction(d -> {
                    throw new ParseException(Double.toString(d), 0);
                });

                CheckedDoubleToLongFunction<ParseException> applying = function.onErrorApplyChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> applying.applyAsLong(1D));
                assertEquals("1.0", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(function).applyAsLong(1D);
                verify(function).onErrorApplyChecked(fallback);
                verify(fallback).applyAsLong(1D);
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsUnchecked() throws IOException, ParseException {
                CheckedDoubleToLongFunction<IOException> function = Spied.checkedDoubleToLongFunction(d -> {
                    throw new IOException(Double.toString(d));
                });

                CheckedDoubleToLongFunction<ParseException> fallback = Spied.checkedDoubleToLongFunction(d -> {
                    throw new IllegalStateException(Double.toString(d));
                });

                CheckedDoubleToLongFunction<ParseException> applying = function.onErrorApplyChecked(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> applying.applyAsLong(1D));
                assertEquals("1.0", thrown.getMessage());

                verify(function).applyAsLong(1D);
                verify(function).onErrorApplyChecked(fallback);
                verify(fallback).applyAsLong(1D);
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedDoubleToLongFunction<IOException> function = Spied.checkedDoubleToLongFunction(d -> {
                throw new IllegalStateException(Double.toString(d));
            });

            CheckedDoubleToLongFunction<ParseException> fallback = Spied.checkedDoubleToLongFunction(d -> (long) d * 3);

            CheckedDoubleToLongFunction<ParseException> applying = function.onErrorApplyChecked(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> applying.applyAsLong(1D));
            assertEquals("1.0", thrown.getMessage());

            verify(function).applyAsLong(1D);
            verify(function).onErrorApplyChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorApplyUnchecked {

        @Test
        void testNullArgument() {
            CheckedDoubleToLongFunction<IOException> function = Spied.checkedDoubleToLongFunction(d -> (long) d + 1);

            assertThrows(NullPointerException.class, () -> function.onErrorApplyUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedDoubleToLongFunction<IOException> function = Spied.checkedDoubleToLongFunction(d -> (long) d + 1);

            DoubleToLongFunction fallback = Spied.doubleToLongFunction(d -> (long) d * 3);

            DoubleToLongFunction applying = function.onErrorApplyUnchecked(fallback);

            assertEquals(2L, applying.applyAsLong(1D));

            verify(function).applyAsLong(1D);
            verify(function).onErrorApplyUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                CheckedDoubleToLongFunction<IOException> function = Spied.checkedDoubleToLongFunction(d -> {
                    throw new IOException(Double.toString(d));
                });

                DoubleToLongFunction fallback = Spied.doubleToLongFunction(d -> (long) d * 3);

                DoubleToLongFunction applying = function.onErrorApplyUnchecked(fallback);

                assertEquals(3L, applying.applyAsLong(1D));

                verify(function).applyAsLong(1D);
                verify(function).onErrorApplyUnchecked(fallback);
                verify(fallback).applyAsLong(1D);
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsUnchecked() throws IOException {
                CheckedDoubleToLongFunction<IOException> function = Spied.checkedDoubleToLongFunction(d -> {
                    throw new IOException(Double.toString(d));
                });

                DoubleToLongFunction fallback = Spied.doubleToLongFunction(d -> {
                    throw new IllegalStateException(Double.toString(d));
                });

                DoubleToLongFunction applying = function.onErrorApplyUnchecked(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> applying.applyAsLong(1D));
                assertEquals("1.0", thrown.getMessage());

                verify(function).applyAsLong(1D);
                verify(function).onErrorApplyUnchecked(fallback);
                verify(fallback).applyAsLong(1D);
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedDoubleToLongFunction<IOException> function = Spied.checkedDoubleToLongFunction(d -> {
                throw new IllegalStateException(Double.toString(d));
            });

            DoubleToLongFunction fallback = Spied.doubleToLongFunction(d -> (long) d * 3);

            DoubleToLongFunction applying = function.onErrorApplyUnchecked(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> applying.applyAsLong(1D));
            assertEquals("1.0", thrown.getMessage());

            verify(function).applyAsLong(1D);
            verify(function).onErrorApplyUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorGetChecked {

        @Test
        void testNullArgument() {
            CheckedDoubleToLongFunction<IOException> function = Spied.checkedDoubleToLongFunction(d -> (long) d + 1);

            assertThrows(NullPointerException.class, () -> function.onErrorGetChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedDoubleToLongFunction<IOException> function = Spied.checkedDoubleToLongFunction(d -> (long) d + 1);

            CheckedLongSupplier<ExecutionException> fallback = Spied.checkedLongSupplier(() -> 0L);

            CheckedDoubleToLongFunction<ExecutionException> getting = function.onErrorGetChecked(fallback);

            assertEquals(2L, getting.applyAsLong(1D));

            verify(function).applyAsLong(1D);
            verify(function).onErrorGetChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                CheckedDoubleToLongFunction<IOException> function = Spied.checkedDoubleToLongFunction(d -> {
                    throw new IOException(Double.toString(d));
                });

                CheckedLongSupplier<ParseException> fallback = Spied.checkedLongSupplier(() -> 0L);

                CheckedDoubleToLongFunction<ParseException> getting = function.onErrorGetChecked(fallback);

                assertEquals(0L, getting.applyAsLong(1D));

                verify(function).applyAsLong(1D);
                verify(function).onErrorGetChecked(fallback);
                verify(fallback).getAsLong();
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                CheckedDoubleToLongFunction<IOException> function = Spied.checkedDoubleToLongFunction(d -> {
                    throw new IOException(Double.toString(d));
                });

                CheckedLongSupplier<ParseException> fallback = Spied.checkedLongSupplier(() -> {
                    throw new ParseException("bar", 0);
                });

                CheckedDoubleToLongFunction<ParseException> getting = function.onErrorGetChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> getting.applyAsLong(1D));
                assertEquals("bar", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(function).applyAsLong(1D);
                verify(function).onErrorGetChecked(fallback);
                verify(fallback).getAsLong();
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsUnchecked() throws IOException, ParseException {
                CheckedDoubleToLongFunction<IOException> function = Spied.checkedDoubleToLongFunction(d -> {
                    throw new IOException(Double.toString(d));
                });

                CheckedLongSupplier<ParseException> fallback = Spied.checkedLongSupplier(() -> {
                    throw new IllegalStateException("bar");
                });

                CheckedDoubleToLongFunction<ParseException> getting = function.onErrorGetChecked(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> getting.applyAsLong(1D));
                assertEquals("bar", thrown.getMessage());

                verify(function).applyAsLong(1D);
                verify(function).onErrorGetChecked(fallback);
                verify(fallback).getAsLong();
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedDoubleToLongFunction<IOException> function = Spied.checkedDoubleToLongFunction(d -> {
                throw new IllegalStateException(Double.toString(d));
            });

            CheckedLongSupplier<ParseException> fallback = Spied.checkedLongSupplier(() -> 0L);

            CheckedDoubleToLongFunction<ParseException> getting = function.onErrorGetChecked(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> getting.applyAsLong(1D));
            assertEquals("1.0", thrown.getMessage());

            verify(function).applyAsLong(1D);
            verify(function).onErrorGetChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorGetUnchecked {

        @Test
        void testNullArgument() {
            CheckedDoubleToLongFunction<IOException> function = Spied.checkedDoubleToLongFunction(d -> (long) d + 1);

            assertThrows(NullPointerException.class, () -> function.onErrorGetUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedDoubleToLongFunction<IOException> function = Spied.checkedDoubleToLongFunction(d -> (long) d + 1);

            LongSupplier fallback = Spied.longSupplier(() -> 0L);

            DoubleToLongFunction getting = function.onErrorGetUnchecked(fallback);

            assertEquals(2L, getting.applyAsLong(1D));

            verify(function).applyAsLong(1D);
            verify(function).onErrorGetUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                CheckedDoubleToLongFunction<IOException> function = Spied.checkedDoubleToLongFunction(d -> {
                    throw new IOException(Double.toString(d));
                });

                LongSupplier fallback = Spied.longSupplier(() -> 0L);

                DoubleToLongFunction getting = function.onErrorGetUnchecked(fallback);

                assertEquals(0L, getting.applyAsLong(1D));

                verify(function).applyAsLong(1D);
                verify(function).onErrorGetUnchecked(fallback);
                verify(fallback).getAsLong();
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsUnchecked() throws IOException {
                CheckedDoubleToLongFunction<IOException> function = Spied.checkedDoubleToLongFunction(d -> {
                    throw new IOException(Double.toString(d));
                });

                LongSupplier fallback = Spied.longSupplier(() -> {
                    throw new IllegalStateException("bar");
                });

                DoubleToLongFunction getting = function.onErrorGetUnchecked(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> getting.applyAsLong(1D));
                assertEquals("bar", thrown.getMessage());

                verify(function).applyAsLong(1D);
                verify(function).onErrorGetUnchecked(fallback);
                verify(fallback).getAsLong();
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedDoubleToLongFunction<IOException> function = Spied.checkedDoubleToLongFunction(d -> {
                throw new IllegalStateException(Double.toString(d));
            });

            LongSupplier fallback = Spied.longSupplier(() -> 0L);

            DoubleToLongFunction getting = function.onErrorGetUnchecked(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> getting.applyAsLong(1D));
            assertEquals("1.0", thrown.getMessage());

            verify(function).applyAsLong(1D);
            verify(function).onErrorGetUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorReturn {

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedDoubleToLongFunction<IOException> function = Spied.checkedDoubleToLongFunction(d -> (long) d + 1);

            DoubleToLongFunction returning = function.onErrorReturn(0L);

            assertEquals(2L, returning.applyAsLong(1D));

            verify(function).applyAsLong(1D);
            verify(function).onErrorReturn(0L);
            verifyNoMoreInteractions(function);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedDoubleToLongFunction<IOException> function = Spied.checkedDoubleToLongFunction(d -> {
                throw new IOException(Double.toString(d));
            });

            DoubleToLongFunction returning = function.onErrorReturn(0L);

            assertEquals(0L, returning.applyAsLong(1D));

            verify(function).applyAsLong(1D);
            verify(function).onErrorReturn(0L);
            verifyNoMoreInteractions(function);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedDoubleToLongFunction<IOException> function = Spied.checkedDoubleToLongFunction(d -> {
                throw new IllegalStateException(Double.toString(d));
            });

            DoubleToLongFunction returning = function.onErrorReturn(0L);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> returning.applyAsLong(1D));
            assertEquals("1.0", thrown.getMessage());

            verify(function).applyAsLong(1D);
            verify(function).onErrorReturn(0L);
            verifyNoMoreInteractions(function);
        }
    }

    @Nested
    class Unchecked {

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedDoubleToLongFunction<IOException> function = Spied.checkedDoubleToLongFunction(d -> (long) d + 1);

            DoubleToLongFunction unchecked = function.unchecked();

            assertEquals(2L, unchecked.applyAsLong(1D));

            verify(function).applyAsLong(1D);
            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(function);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedDoubleToLongFunction<IOException> function = Spied.checkedDoubleToLongFunction(d -> {
                throw new IOException(Double.toString(d));
            });

            DoubleToLongFunction unchecked = function.unchecked();

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.applyAsLong(1D));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1.0", cause.getMessage());

            verify(function).applyAsLong(1D);
            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(function);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedDoubleToLongFunction<IOException> function = Spied.checkedDoubleToLongFunction(d -> {
                throw new IllegalArgumentException(Double.toString(d));
            });

            DoubleToLongFunction unchecked = function.unchecked();

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> unchecked.applyAsLong(1D));
            assertEquals("1.0", thrown.getMessage());

            verify(function).applyAsLong(1D);
            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(function);
        }
    }

    @Nested
    class Of {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> CheckedDoubleToLongFunction.of(null));
        }

        @Test
        void testNonNullArgument() throws IOException {
            CheckedDoubleToLongFunction<IOException> function = CheckedDoubleToLongFunction.of(d -> (long) d + 1);

            assertEquals(2L, function.applyAsLong(1D));
        }
    }

    @Nested
    class UncheckedFactory {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> CheckedDoubleToLongFunction.unchecked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedDoubleToLongFunction<IOException> function = Spied.checkedDoubleToLongFunction(d -> (long) d + 1);

            DoubleToLongFunction unchecked = CheckedDoubleToLongFunction.unchecked(function);

            assertEquals(2L, unchecked.applyAsLong(1D));

            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verify(function).applyAsLong(1D);
            verifyNoMoreInteractions(function);
        }

        @Test
        void testArgumentThrowsChecked() throws IOException {
            CheckedDoubleToLongFunction<IOException> function = Spied.checkedDoubleToLongFunction(d -> {
                throw new IOException(Double.toString(d));
            });

            DoubleToLongFunction unchecked = CheckedDoubleToLongFunction.unchecked(function);

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.applyAsLong(1D));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1.0", cause.getMessage());

            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verify(function).applyAsLong(1D);
            verifyNoMoreInteractions(function);
        }

        @Test
        void testArgumentThrowsUnchecked() throws IOException {
            CheckedDoubleToLongFunction<IOException> function = Spied.checkedDoubleToLongFunction(d -> {
                throw new IllegalArgumentException(Double.toString(d));
            });

            DoubleToLongFunction unchecked = CheckedDoubleToLongFunction.unchecked(function);

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> unchecked.applyAsLong(1D));
            assertEquals("1.0", thrown.getMessage());

            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verify(function).applyAsLong(1D);
            verifyNoMoreInteractions(function);
        }
    }

    @Nested
    class CheckedWrapper {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> CheckedDoubleToLongFunction.checked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedDoubleToLongFunction<IOException> function = CheckedDoubleToLongFunction.checked(d -> (long) d + 1);

            assertEquals(2L, function.applyAsLong(1D));
        }

        @Test
        void testArgumentThrowsUnchecked() {
            CheckedDoubleToLongFunction<IOException> function = CheckedDoubleToLongFunction.checked(d -> {
                throw new UncheckedException(Double.toString(d), new IOException());
            });

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> function.applyAsLong(1D));
            assertEquals("1.0", thrown.getMessage());
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertNull(cause.getMessage());
        }
    }

    @Nested
    class CheckedUnwrapper {

        @Test
        void testNullArguments() {
            DoubleToLongFunction function = d -> (long) d + 1;

            assertThrows(NullPointerException.class, () -> CheckedDoubleToLongFunction.checked(null, IOException.class));
            assertThrows(NullPointerException.class, () -> CheckedDoubleToLongFunction.checked(function, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedDoubleToLongFunction<IOException> function = CheckedDoubleToLongFunction.checked(d -> (long) d + 1, IOException.class);

            assertEquals(2L, function.applyAsLong(1D));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                CheckedDoubleToLongFunction<IOException> function = CheckedDoubleToLongFunction.checked(d -> {
                    throw new UncheckedException(new IOException(Double.toString(d)));
                }, IOException.class);

                IOException thrown = assertThrows(IOException.class, () -> function.applyAsLong(1D));
                assertEquals("1.0", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                CheckedDoubleToLongFunction<IOException> function = CheckedDoubleToLongFunction.checked(d -> {
                    throw new UncheckedException(new FileNotFoundException(Double.toString(d)));
                }, IOException.class);

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class, () -> function.applyAsLong(1D));
                assertEquals("1.0", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                CheckedDoubleToLongFunction<IOException> function = CheckedDoubleToLongFunction.checked(d -> {
                    throw new UncheckedException(new ParseException(Double.toString(d), 0));
                }, IOException.class);

                UncheckedException thrown = assertThrows(UncheckedException.class, () -> function.applyAsLong(1D));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("1.0", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            CheckedDoubleToLongFunction<IOException> function = CheckedDoubleToLongFunction.checked(d -> {
                throw new IllegalStateException(Double.toString(d));
            }, IOException.class);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> function.applyAsLong(1D));
            assertEquals("1.0", thrown.getMessage());
        }
    }

    @Nested
    class InvokeAndUnwrap {

        @Test
        void testNullArguments() {
            DoubleToLongFunction function = d -> String.valueOf(Double.toString(d)).length();

            assertThrows(NullPointerException.class, () -> CheckedDoubleToLongFunction.invokeAndUnwrap(null, 1D, IOException.class));
            assertThrows(NullPointerException.class, () -> CheckedDoubleToLongFunction.invokeAndUnwrap(function, 1D, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            DoubleToLongFunction function = d -> (long) d + 1;

            assertEquals(2L, CheckedDoubleToLongFunction.invokeAndUnwrap(function, 1D, IOException.class));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                DoubleToLongFunction function = d -> {
                    throw new UncheckedException(new IOException(Double.toString(d)));
                };

                IOException thrown = assertThrows(IOException.class,
                        () -> CheckedDoubleToLongFunction.invokeAndUnwrap(function, 1D, IOException.class));
                assertEquals("1.0", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                DoubleToLongFunction function = d -> {
                    throw new UncheckedException(new FileNotFoundException(Double.toString(d)));
                };

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class,
                        () -> CheckedDoubleToLongFunction.invokeAndUnwrap(function, 1D, IOException.class));
                assertEquals("1.0", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                DoubleToLongFunction function = d -> {
                    throw new UncheckedException(new ParseException(Double.toString(d), 0));
                };

                UncheckedException thrown = assertThrows(UncheckedException.class,
                        () -> CheckedDoubleToLongFunction.invokeAndUnwrap(function, 1D, IOException.class));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("1.0", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            DoubleToLongFunction function = d -> {
                throw new IllegalStateException(Double.toString(d));
            };

            IllegalStateException thrown = assertThrows(IllegalStateException.class,
                    () -> CheckedDoubleToLongFunction.invokeAndUnwrap(function, 1D, IOException.class));
            assertEquals("1.0", thrown.getMessage());
        }
    }
}
