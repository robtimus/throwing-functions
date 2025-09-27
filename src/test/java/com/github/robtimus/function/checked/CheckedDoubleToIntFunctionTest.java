/*
 * CheckedDoubleToIntFunctionTest.java
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
import java.util.function.DoubleToIntFunction;
import java.util.function.Function;
import java.util.function.IntSupplier;
import java.util.function.ToIntFunction;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("nls")
class CheckedDoubleToIntFunctionTest {

    @Nested
    class OnErrorThrowAsChecked {

        @Test
        void testNullArgument() {
            CheckedDoubleToIntFunction<IOException> function = Spied.checkedDoubleToIntFunction(d -> (int) d + 1);

            assertThrows(NullPointerException.class, () -> function.onErrorThrowAsChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedDoubleToIntFunction<IOException> function = Spied.checkedDoubleToIntFunction(d -> (int) d + 1);

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedDoubleToIntFunction<ExecutionException> throwing = function.onErrorThrowAsChecked(errorMapper);

            assertEquals(2, throwing.applyAsInt(1D));

            verify(function).applyAsInt(1D);
            verify(function).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedDoubleToIntFunction<IOException> function = Spied.checkedDoubleToIntFunction(d -> {
                throw new IOException(Double.toString(d));
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedDoubleToIntFunction<ExecutionException> throwing = function.onErrorThrowAsChecked(errorMapper);

            ExecutionException thrown = assertThrows(ExecutionException.class, () -> throwing.applyAsInt(1D));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1.0", cause.getMessage());

            verify(function).applyAsInt(1D);
            verify(function).onErrorThrowAsChecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(function, errorMapper);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedDoubleToIntFunction<IOException> function = Spied.checkedDoubleToIntFunction(d -> {
                throw new IllegalStateException(Double.toString(d));
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedDoubleToIntFunction<ExecutionException> throwing = function.onErrorThrowAsChecked(errorMapper);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> throwing.applyAsInt(1D));
            assertEquals("1.0", thrown.getMessage());

            verify(function).applyAsInt(1D);
            verify(function).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }
    }

    @Nested
    class OnErrorThrowAsUnchecked {

        @Test
        void testNullArgument() {
            CheckedDoubleToIntFunction<IOException> function = Spied.checkedDoubleToIntFunction(d -> (int) d + 1);

            assertThrows(NullPointerException.class, () -> function.onErrorThrowAsUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedDoubleToIntFunction<IOException> function = Spied.checkedDoubleToIntFunction(d -> (int) d + 1);

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            DoubleToIntFunction throwing = function.onErrorThrowAsUnchecked(errorMapper);

            assertEquals(2, throwing.applyAsInt(1D));

            verify(function).applyAsInt(1D);
            verify(function).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedDoubleToIntFunction<IOException> function = Spied.checkedDoubleToIntFunction(d -> {
                throw new IOException(Double.toString(d));
            });

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            DoubleToIntFunction throwing = function.onErrorThrowAsUnchecked(errorMapper);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> throwing.applyAsInt(1D));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1.0", cause.getMessage());

            verify(function).applyAsInt(1D);
            verify(function).onErrorThrowAsUnchecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(function, errorMapper);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedDoubleToIntFunction<IOException> function = Spied.checkedDoubleToIntFunction(d -> {
                throw new IllegalArgumentException(Double.toString(d));
            });

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            DoubleToIntFunction throwing = function.onErrorThrowAsUnchecked(errorMapper);

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> throwing.applyAsInt(1D));
            assertEquals("1.0", thrown.getMessage());

            verify(function).applyAsInt(1D);
            verify(function).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }
    }

    @Nested
    class OnErrorHandleChecked {

        @Test
        void testNullArgument() {
            CheckedDoubleToIntFunction<IOException> function = Spied.checkedDoubleToIntFunction(d -> (int) d + 1);

            assertThrows(NullPointerException.class, () -> function.onErrorHandleChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedDoubleToIntFunction<IOException> function = Spied.checkedDoubleToIntFunction(d -> (int) d + 1);

            CheckedToIntFunction<IOException, ExecutionException> errorHandler = Spied.checkedToIntFunction(e -> 0);

            CheckedDoubleToIntFunction<ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

            assertEquals(2, handling.applyAsInt(1D));

            verify(function).applyAsInt(1D);
            verify(function).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException, ExecutionException {
                CheckedDoubleToIntFunction<IOException> function = Spied.checkedDoubleToIntFunction(d -> {
                    throw new IOException(Double.toString(d));
                });

                CheckedToIntFunction<IOException, ExecutionException> errorHandler = Spied.checkedToIntFunction(e -> 0);

                CheckedDoubleToIntFunction<ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

                assertEquals(0, handling.applyAsInt(1D));

                verify(function).applyAsInt(1D);
                verify(function).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsInt(any());
                verifyNoMoreInteractions(function, errorHandler);
            }

            @Test
            void testHandlerThrowsChecked() throws IOException, ExecutionException {
                CheckedDoubleToIntFunction<IOException> function = Spied.checkedDoubleToIntFunction(d -> {
                    throw new IOException(Double.toString(d));
                });

                CheckedToIntFunction<IOException, ExecutionException> errorHandler = Spied.checkedToIntFunction(e -> {
                    throw new ExecutionException(e);
                });

                CheckedDoubleToIntFunction<ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

                ExecutionException thrown = assertThrows(ExecutionException.class, () -> handling.applyAsInt(1D));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("1.0", cause.getMessage());

                verify(function).applyAsInt(1D);
                verify(function).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsInt(any());
                verifyNoMoreInteractions(function, errorHandler);
            }

            @Test
            void testHandlerThrowsUnchecked() throws IOException, ExecutionException {
                CheckedDoubleToIntFunction<IOException> function = Spied.checkedDoubleToIntFunction(d -> {
                    throw new IOException(Double.toString(d));
                });

                CheckedToIntFunction<IOException, ExecutionException> errorHandler = Spied.checkedToIntFunction(e -> {
                    throw new IllegalStateException(e);
                });

                CheckedDoubleToIntFunction<ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.applyAsInt(1D));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("1.0", cause.getMessage());

                verify(function).applyAsInt(1D);
                verify(function).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsInt(any());
                verifyNoMoreInteractions(function, errorHandler);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedDoubleToIntFunction<IOException> function = Spied.checkedDoubleToIntFunction(d -> {
                throw new IllegalStateException(Double.toString(d));
            });

            CheckedToIntFunction<IOException, ExecutionException> errorHandler = Spied.checkedToIntFunction(e -> 0);

            CheckedDoubleToIntFunction<ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.applyAsInt(1D));
            assertEquals("1.0", thrown.getMessage());

            verify(function).applyAsInt(1D);
            verify(function).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }
    }

    @Nested
    class OnErrorHandleUnchecked {

        @Test
        void testNullArgument() {
            CheckedDoubleToIntFunction<IOException> function = Spied.checkedDoubleToIntFunction(d -> (int) d + 1);

            assertThrows(NullPointerException.class, () -> function.onErrorHandleUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedDoubleToIntFunction<IOException> function = Spied.checkedDoubleToIntFunction(d -> (int) d + 1);

            ToIntFunction<IOException> errorHandler = Spied.toIntFunction(e -> 0);

            DoubleToIntFunction handling = function.onErrorHandleUnchecked(errorHandler);

            assertEquals(2, handling.applyAsInt(1D));

            verify(function).applyAsInt(1D);
            verify(function).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException {
                CheckedDoubleToIntFunction<IOException> function = Spied.checkedDoubleToIntFunction(d -> {
                    throw new IOException(Double.toString(d));
                });

                ToIntFunction<IOException> errorHandler = Spied.toIntFunction(e -> 0);

                DoubleToIntFunction handling = function.onErrorHandleUnchecked(errorHandler);

                assertEquals(0, handling.applyAsInt(1D));

                verify(function).applyAsInt(1D);
                verify(function).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).applyAsInt(any());
                verifyNoMoreInteractions(function, errorHandler);
            }

            @Test
            void testHandlerThrowsUnchecked() throws IOException {
                CheckedDoubleToIntFunction<IOException> function = Spied.checkedDoubleToIntFunction(d -> {
                    throw new IOException(Double.toString(d));
                });

                ToIntFunction<IOException> errorHandler = Spied.toIntFunction(e -> {
                    throw new IllegalStateException(e);
                });

                DoubleToIntFunction handling = function.onErrorHandleUnchecked(errorHandler);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.applyAsInt(1D));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("1.0", cause.getMessage());

                verify(function).applyAsInt(1D);
                verify(function).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).applyAsInt(any());
                verifyNoMoreInteractions(function, errorHandler);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedDoubleToIntFunction<IOException> function = Spied.checkedDoubleToIntFunction(d -> {
                throw new IllegalStateException(Double.toString(d));
            });

            ToIntFunction<IOException> errorHandler = Spied.toIntFunction(e -> 0);

            DoubleToIntFunction handling = function.onErrorHandleUnchecked(errorHandler);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.applyAsInt(1D));
            assertEquals("1.0", thrown.getMessage());

            verify(function).applyAsInt(1D);
            verify(function).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }
    }

    @Nested
    class OnErrorApplyChecked {

        @Test
        void testNullArgument() {
            CheckedDoubleToIntFunction<IOException> function = Spied.checkedDoubleToIntFunction(d -> (int) d + 1);

            assertThrows(NullPointerException.class, () -> function.onErrorApplyChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedDoubleToIntFunction<IOException> function = Spied.checkedDoubleToIntFunction(d -> (int) d + 1);

            CheckedDoubleToIntFunction<ExecutionException> fallback = Spied.checkedDoubleToIntFunction(d -> (int) d * 3);

            CheckedDoubleToIntFunction<ExecutionException> applying = function.onErrorApplyChecked(fallback);

            assertEquals(2, applying.applyAsInt(1D));

            verify(function).applyAsInt(1D);
            verify(function).onErrorApplyChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                CheckedDoubleToIntFunction<IOException> function = Spied.checkedDoubleToIntFunction(d -> {
                    throw new IOException(Double.toString(d));
                });

                CheckedDoubleToIntFunction<ParseException> fallback = Spied.checkedDoubleToIntFunction(d -> (int) d * 3);

                CheckedDoubleToIntFunction<ParseException> applying = function.onErrorApplyChecked(fallback);

                assertEquals(3, applying.applyAsInt(1D));

                verify(function).applyAsInt(1D);
                verify(function).onErrorApplyChecked(fallback);
                verify(fallback).applyAsInt(1D);
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                CheckedDoubleToIntFunction<IOException> function = Spied.checkedDoubleToIntFunction(d -> {
                    throw new IOException(Double.toString(d));
                });

                CheckedDoubleToIntFunction<ParseException> fallback = Spied.checkedDoubleToIntFunction(d -> {
                    throw new ParseException(Double.toString(d), 0);
                });

                CheckedDoubleToIntFunction<ParseException> applying = function.onErrorApplyChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> applying.applyAsInt(1D));
                assertEquals("1.0", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(function).applyAsInt(1D);
                verify(function).onErrorApplyChecked(fallback);
                verify(fallback).applyAsInt(1D);
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsUnchecked() throws IOException, ParseException {
                CheckedDoubleToIntFunction<IOException> function = Spied.checkedDoubleToIntFunction(d -> {
                    throw new IOException(Double.toString(d));
                });

                CheckedDoubleToIntFunction<ParseException> fallback = Spied.checkedDoubleToIntFunction(d -> {
                    throw new IllegalStateException(Double.toString(d));
                });

                CheckedDoubleToIntFunction<ParseException> applying = function.onErrorApplyChecked(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> applying.applyAsInt(1D));
                assertEquals("1.0", thrown.getMessage());

                verify(function).applyAsInt(1D);
                verify(function).onErrorApplyChecked(fallback);
                verify(fallback).applyAsInt(1D);
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedDoubleToIntFunction<IOException> function = Spied.checkedDoubleToIntFunction(d -> {
                throw new IllegalStateException(Double.toString(d));
            });

            CheckedDoubleToIntFunction<ParseException> fallback = Spied.checkedDoubleToIntFunction(d -> (int) d * 3);

            CheckedDoubleToIntFunction<ParseException> applying = function.onErrorApplyChecked(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> applying.applyAsInt(1D));
            assertEquals("1.0", thrown.getMessage());

            verify(function).applyAsInt(1D);
            verify(function).onErrorApplyChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorApplyUnchecked {

        @Test
        void testNullArgument() {
            CheckedDoubleToIntFunction<IOException> function = Spied.checkedDoubleToIntFunction(d -> (int) d + 1);

            assertThrows(NullPointerException.class, () -> function.onErrorApplyUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedDoubleToIntFunction<IOException> function = Spied.checkedDoubleToIntFunction(d -> (int) d + 1);

            DoubleToIntFunction fallback = Spied.doubleToIntFunction(d -> (int) d * 3);

            DoubleToIntFunction applying = function.onErrorApplyUnchecked(fallback);

            assertEquals(2, applying.applyAsInt(1D));

            verify(function).applyAsInt(1D);
            verify(function).onErrorApplyUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                CheckedDoubleToIntFunction<IOException> function = Spied.checkedDoubleToIntFunction(d -> {
                    throw new IOException(Double.toString(d));
                });

                DoubleToIntFunction fallback = Spied.doubleToIntFunction(d -> (int) d * 3);

                DoubleToIntFunction applying = function.onErrorApplyUnchecked(fallback);

                assertEquals(3, applying.applyAsInt(1D));

                verify(function).applyAsInt(1D);
                verify(function).onErrorApplyUnchecked(fallback);
                verify(fallback).applyAsInt(1D);
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsUnchecked() throws IOException {
                CheckedDoubleToIntFunction<IOException> function = Spied.checkedDoubleToIntFunction(d -> {
                    throw new IOException(Double.toString(d));
                });

                DoubleToIntFunction fallback = Spied.doubleToIntFunction(d -> {
                    throw new IllegalStateException(Double.toString(d));
                });

                DoubleToIntFunction applying = function.onErrorApplyUnchecked(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> applying.applyAsInt(1D));
                assertEquals("1.0", thrown.getMessage());

                verify(function).applyAsInt(1D);
                verify(function).onErrorApplyUnchecked(fallback);
                verify(fallback).applyAsInt(1D);
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedDoubleToIntFunction<IOException> function = Spied.checkedDoubleToIntFunction(d -> {
                throw new IllegalStateException(Double.toString(d));
            });

            DoubleToIntFunction fallback = Spied.doubleToIntFunction(d -> (int) d * 3);

            DoubleToIntFunction applying = function.onErrorApplyUnchecked(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> applying.applyAsInt(1D));
            assertEquals("1.0", thrown.getMessage());

            verify(function).applyAsInt(1D);
            verify(function).onErrorApplyUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorGetChecked {

        @Test
        void testNullArgument() {
            CheckedDoubleToIntFunction<IOException> function = Spied.checkedDoubleToIntFunction(d -> (int) d + 1);

            assertThrows(NullPointerException.class, () -> function.onErrorGetChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedDoubleToIntFunction<IOException> function = Spied.checkedDoubleToIntFunction(d -> (int) d + 1);

            CheckedIntSupplier<ExecutionException> fallback = Spied.checkedIntSupplier(() -> 0);

            CheckedDoubleToIntFunction<ExecutionException> getting = function.onErrorGetChecked(fallback);

            assertEquals(2, getting.applyAsInt(1D));

            verify(function).applyAsInt(1D);
            verify(function).onErrorGetChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                CheckedDoubleToIntFunction<IOException> function = Spied.checkedDoubleToIntFunction(d -> {
                    throw new IOException(Double.toString(d));
                });

                CheckedIntSupplier<ParseException> fallback = Spied.checkedIntSupplier(() -> 0);

                CheckedDoubleToIntFunction<ParseException> getting = function.onErrorGetChecked(fallback);

                assertEquals(0, getting.applyAsInt(1D));

                verify(function).applyAsInt(1D);
                verify(function).onErrorGetChecked(fallback);
                verify(fallback).getAsInt();
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                CheckedDoubleToIntFunction<IOException> function = Spied.checkedDoubleToIntFunction(d -> {
                    throw new IOException(Double.toString(d));
                });

                CheckedIntSupplier<ParseException> fallback = Spied.checkedIntSupplier(() -> {
                    throw new ParseException("bar", 0);
                });

                CheckedDoubleToIntFunction<ParseException> getting = function.onErrorGetChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> getting.applyAsInt(1D));
                assertEquals("bar", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(function).applyAsInt(1D);
                verify(function).onErrorGetChecked(fallback);
                verify(fallback).getAsInt();
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsUnchecked() throws IOException, ParseException {
                CheckedDoubleToIntFunction<IOException> function = Spied.checkedDoubleToIntFunction(d -> {
                    throw new IOException(Double.toString(d));
                });

                CheckedIntSupplier<ParseException> fallback = Spied.checkedIntSupplier(() -> {
                    throw new IllegalStateException("bar");
                });

                CheckedDoubleToIntFunction<ParseException> getting = function.onErrorGetChecked(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> getting.applyAsInt(1D));
                assertEquals("bar", thrown.getMessage());

                verify(function).applyAsInt(1D);
                verify(function).onErrorGetChecked(fallback);
                verify(fallback).getAsInt();
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedDoubleToIntFunction<IOException> function = Spied.checkedDoubleToIntFunction(d -> {
                throw new IllegalStateException(Double.toString(d));
            });

            CheckedIntSupplier<ParseException> fallback = Spied.checkedIntSupplier(() -> 0);

            CheckedDoubleToIntFunction<ParseException> getting = function.onErrorGetChecked(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> getting.applyAsInt(1D));
            assertEquals("1.0", thrown.getMessage());

            verify(function).applyAsInt(1D);
            verify(function).onErrorGetChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorGetUnchecked {

        @Test
        void testNullArgument() {
            CheckedDoubleToIntFunction<IOException> function = Spied.checkedDoubleToIntFunction(d -> (int) d + 1);

            assertThrows(NullPointerException.class, () -> function.onErrorGetUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedDoubleToIntFunction<IOException> function = Spied.checkedDoubleToIntFunction(d -> (int) d + 1);

            IntSupplier fallback = Spied.intSupplier(() -> 0);

            DoubleToIntFunction getting = function.onErrorGetUnchecked(fallback);

            assertEquals(2, getting.applyAsInt(1D));

            verify(function).applyAsInt(1D);
            verify(function).onErrorGetUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                CheckedDoubleToIntFunction<IOException> function = Spied.checkedDoubleToIntFunction(d -> {
                    throw new IOException(Double.toString(d));
                });

                IntSupplier fallback = Spied.intSupplier(() -> 0);

                DoubleToIntFunction getting = function.onErrorGetUnchecked(fallback);

                assertEquals(0, getting.applyAsInt(1D));

                verify(function).applyAsInt(1D);
                verify(function).onErrorGetUnchecked(fallback);
                verify(fallback).getAsInt();
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsUnchecked() throws IOException {
                CheckedDoubleToIntFunction<IOException> function = Spied.checkedDoubleToIntFunction(d -> {
                    throw new IOException(Double.toString(d));
                });

                IntSupplier fallback = Spied.intSupplier(() -> {
                    throw new IllegalStateException("bar");
                });

                DoubleToIntFunction getting = function.onErrorGetUnchecked(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> getting.applyAsInt(1D));
                assertEquals("bar", thrown.getMessage());

                verify(function).applyAsInt(1D);
                verify(function).onErrorGetUnchecked(fallback);
                verify(fallback).getAsInt();
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedDoubleToIntFunction<IOException> function = Spied.checkedDoubleToIntFunction(d -> {
                throw new IllegalStateException(Double.toString(d));
            });

            IntSupplier fallback = Spied.intSupplier(() -> 0);

            DoubleToIntFunction getting = function.onErrorGetUnchecked(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> getting.applyAsInt(1D));
            assertEquals("1.0", thrown.getMessage());

            verify(function).applyAsInt(1D);
            verify(function).onErrorGetUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorReturn {

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedDoubleToIntFunction<IOException> function = Spied.checkedDoubleToIntFunction(d -> (int) d + 1);

            DoubleToIntFunction returning = function.onErrorReturn(0);

            assertEquals(2, returning.applyAsInt(1D));

            verify(function).applyAsInt(1D);
            verify(function).onErrorReturn(0);
            verifyNoMoreInteractions(function);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedDoubleToIntFunction<IOException> function = Spied.checkedDoubleToIntFunction(d -> {
                throw new IOException(Double.toString(d));
            });

            DoubleToIntFunction returning = function.onErrorReturn(0);

            assertEquals(0, returning.applyAsInt(1D));

            verify(function).applyAsInt(1D);
            verify(function).onErrorReturn(0);
            verifyNoMoreInteractions(function);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedDoubleToIntFunction<IOException> function = Spied.checkedDoubleToIntFunction(d -> {
                throw new IllegalStateException(Double.toString(d));
            });

            DoubleToIntFunction returning = function.onErrorReturn(0);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> returning.applyAsInt(1D));
            assertEquals("1.0", thrown.getMessage());

            verify(function).applyAsInt(1D);
            verify(function).onErrorReturn(0);
            verifyNoMoreInteractions(function);
        }
    }

    @Nested
    class Unchecked {

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedDoubleToIntFunction<IOException> function = Spied.checkedDoubleToIntFunction(d -> (int) d + 1);

            DoubleToIntFunction unchecked = function.unchecked();

            assertEquals(2, unchecked.applyAsInt(1D));

            verify(function).applyAsInt(1D);
            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(function);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedDoubleToIntFunction<IOException> function = Spied.checkedDoubleToIntFunction(d -> {
                throw new IOException(Double.toString(d));
            });

            DoubleToIntFunction unchecked = function.unchecked();

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.applyAsInt(1D));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1.0", cause.getMessage());

            verify(function).applyAsInt(1D);
            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(function);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedDoubleToIntFunction<IOException> function = Spied.checkedDoubleToIntFunction(d -> {
                throw new IllegalArgumentException(Double.toString(d));
            });

            DoubleToIntFunction unchecked = function.unchecked();

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> unchecked.applyAsInt(1D));
            assertEquals("1.0", thrown.getMessage());

            verify(function).applyAsInt(1D);
            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(function);
        }
    }

    @Nested
    class Of {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> CheckedDoubleToIntFunction.of(null));
        }

        @Test
        void testNonNullArgument() throws IOException {
            CheckedDoubleToIntFunction<IOException> function = CheckedDoubleToIntFunction.of(d -> (int) d + 1);

            assertEquals(2, function.applyAsInt(1D));
        }
    }

    @Nested
    class UncheckedFactory {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> CheckedDoubleToIntFunction.unchecked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedDoubleToIntFunction<IOException> function = Spied.checkedDoubleToIntFunction(d -> (int) d + 1);

            DoubleToIntFunction unchecked = CheckedDoubleToIntFunction.unchecked(function);

            assertEquals(2, unchecked.applyAsInt(1D));

            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verify(function).applyAsInt(1D);
            verifyNoMoreInteractions(function);
        }

        @Test
        void testArgumentThrowsChecked() throws IOException {
            CheckedDoubleToIntFunction<IOException> function = Spied.checkedDoubleToIntFunction(d -> {
                throw new IOException(Double.toString(d));
            });

            DoubleToIntFunction unchecked = CheckedDoubleToIntFunction.unchecked(function);

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.applyAsInt(1D));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1.0", cause.getMessage());

            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verify(function).applyAsInt(1D);
            verifyNoMoreInteractions(function);
        }

        @Test
        void testArgumentThrowsUnchecked() throws IOException {
            CheckedDoubleToIntFunction<IOException> function = Spied.checkedDoubleToIntFunction(d -> {
                throw new IllegalArgumentException(Double.toString(d));
            });

            DoubleToIntFunction unchecked = CheckedDoubleToIntFunction.unchecked(function);

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> unchecked.applyAsInt(1D));
            assertEquals("1.0", thrown.getMessage());

            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verify(function).applyAsInt(1D);
            verifyNoMoreInteractions(function);
        }
    }

    @Nested
    class CheckedWrapper {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> CheckedDoubleToIntFunction.checked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedDoubleToIntFunction<IOException> function = CheckedDoubleToIntFunction.checked(d -> (int) d + 1);

            assertEquals(2, function.applyAsInt(1D));
        }

        @Test
        void testArgumentThrowsUnchecked() {
            CheckedDoubleToIntFunction<IOException> function = CheckedDoubleToIntFunction.checked(d -> {
                throw new UncheckedException(Double.toString(d), new IOException());
            });

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> function.applyAsInt(1D));
            assertEquals("1.0", thrown.getMessage());
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertNull(cause.getMessage());
        }
    }

    @Nested
    class CheckedUnwrapper {

        @Test
        void testNullArguments() {
            DoubleToIntFunction function = d -> (int) d + 1;

            assertThrows(NullPointerException.class, () -> CheckedDoubleToIntFunction.checked(null, IOException.class));
            assertThrows(NullPointerException.class, () -> CheckedDoubleToIntFunction.checked(function, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedDoubleToIntFunction<IOException> function = CheckedDoubleToIntFunction.checked(d -> (int) d + 1, IOException.class);

            assertEquals(2, function.applyAsInt(1D));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                CheckedDoubleToIntFunction<IOException> function = CheckedDoubleToIntFunction.checked(d -> {
                    throw new UncheckedException(new IOException(Double.toString(d)));
                }, IOException.class);

                IOException thrown = assertThrows(IOException.class, () -> function.applyAsInt(1D));
                assertEquals("1.0", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                CheckedDoubleToIntFunction<IOException> function = CheckedDoubleToIntFunction.checked(d -> {
                    throw new UncheckedException(new FileNotFoundException(Double.toString(d)));
                }, IOException.class);

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class, () -> function.applyAsInt(1D));
                assertEquals("1.0", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                CheckedDoubleToIntFunction<IOException> function = CheckedDoubleToIntFunction.checked(d -> {
                    throw new UncheckedException(new ParseException(Double.toString(d), 0));
                }, IOException.class);

                UncheckedException thrown = assertThrows(UncheckedException.class, () -> function.applyAsInt(1D));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("1.0", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            CheckedDoubleToIntFunction<IOException> function = CheckedDoubleToIntFunction.checked(d -> {
                throw new IllegalStateException(Double.toString(d));
            }, IOException.class);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> function.applyAsInt(1D));
            assertEquals("1.0", thrown.getMessage());
        }
    }

    @Nested
    class InvokeAndUnwrap {

        @Test
        void testNullArguments() {
            DoubleToIntFunction function = d -> String.valueOf(Double.toString(d)).length();

            assertThrows(NullPointerException.class, () -> CheckedDoubleToIntFunction.invokeAndUnwrap(null, 1D, IOException.class));
            assertThrows(NullPointerException.class, () -> CheckedDoubleToIntFunction.invokeAndUnwrap(function, 1D, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            DoubleToIntFunction function = d -> (int) d + 1;

            assertEquals(2, CheckedDoubleToIntFunction.invokeAndUnwrap(function, 1D, IOException.class));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                DoubleToIntFunction function = d -> {
                    throw new UncheckedException(new IOException(Double.toString(d)));
                };

                IOException thrown = assertThrows(IOException.class,
                        () -> CheckedDoubleToIntFunction.invokeAndUnwrap(function, 1D, IOException.class));
                assertEquals("1.0", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                DoubleToIntFunction function = d -> {
                    throw new UncheckedException(new FileNotFoundException(Double.toString(d)));
                };

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class,
                        () -> CheckedDoubleToIntFunction.invokeAndUnwrap(function, 1D, IOException.class));
                assertEquals("1.0", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                DoubleToIntFunction function = d -> {
                    throw new UncheckedException(new ParseException(Double.toString(d), 0));
                };

                UncheckedException thrown = assertThrows(UncheckedException.class,
                        () -> CheckedDoubleToIntFunction.invokeAndUnwrap(function, 1D, IOException.class));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("1.0", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            DoubleToIntFunction function = d -> {
                throw new IllegalStateException(Double.toString(d));
            };

            IllegalStateException thrown = assertThrows(IllegalStateException.class,
                    () -> CheckedDoubleToIntFunction.invokeAndUnwrap(function, 1D, IOException.class));
            assertEquals("1.0", thrown.getMessage());
        }
    }
}
