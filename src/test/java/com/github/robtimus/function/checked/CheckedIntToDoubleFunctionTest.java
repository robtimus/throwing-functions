/*
 * CheckedIntToDoubleFunctionTest.java
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
import java.util.function.IntToDoubleFunction;
import java.util.function.ToDoubleFunction;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("nls")
class CheckedIntToDoubleFunctionTest {

    @Nested
    class OnErrorThrowAsChecked {

        @Test
        void testNullArgument() {
            CheckedIntToDoubleFunction<IOException> function = Spied.checkedIntToDoubleFunction(i -> i + 1);

            assertThrows(NullPointerException.class, () -> function.onErrorThrowAsChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedIntToDoubleFunction<IOException> function = Spied.checkedIntToDoubleFunction(i -> i + 1);

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedIntToDoubleFunction<ExecutionException> throwing = function.onErrorThrowAsChecked(errorMapper);

            assertEquals(2D, throwing.applyAsDouble(1));

            verify(function).applyAsDouble(1);
            verify(function).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedIntToDoubleFunction<IOException> function = Spied.checkedIntToDoubleFunction(i -> {
                throw new IOException(Integer.toString(i));
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedIntToDoubleFunction<ExecutionException> throwing = function.onErrorThrowAsChecked(errorMapper);

            ExecutionException thrown = assertThrows(ExecutionException.class, () -> throwing.applyAsDouble(1));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1", cause.getMessage());

            verify(function).applyAsDouble(1);
            verify(function).onErrorThrowAsChecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(function, errorMapper);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedIntToDoubleFunction<IOException> function = Spied.checkedIntToDoubleFunction(i -> {
                throw new IllegalStateException(Integer.toString(i));
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedIntToDoubleFunction<ExecutionException> throwing = function.onErrorThrowAsChecked(errorMapper);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> throwing.applyAsDouble(1));
            assertEquals("1", thrown.getMessage());

            verify(function).applyAsDouble(1);
            verify(function).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }
    }

    @Nested
    class OnErrorThrowAsUnchecked {

        @Test
        void testNullArgument() {
            CheckedIntToDoubleFunction<IOException> function = Spied.checkedIntToDoubleFunction(i -> i + 1);

            assertThrows(NullPointerException.class, () -> function.onErrorThrowAsUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedIntToDoubleFunction<IOException> function = Spied.checkedIntToDoubleFunction(i -> i + 1);

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            IntToDoubleFunction throwing = function.onErrorThrowAsUnchecked(errorMapper);

            assertEquals(2D, throwing.applyAsDouble(1));

            verify(function).applyAsDouble(1);
            verify(function).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedIntToDoubleFunction<IOException> function = Spied.checkedIntToDoubleFunction(i -> {
                throw new IOException(Integer.toString(i));
            });

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            IntToDoubleFunction throwing = function.onErrorThrowAsUnchecked(errorMapper);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> throwing.applyAsDouble(1));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1", cause.getMessage());

            verify(function).applyAsDouble(1);
            verify(function).onErrorThrowAsUnchecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(function, errorMapper);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedIntToDoubleFunction<IOException> function = Spied.checkedIntToDoubleFunction(i -> {
                throw new IllegalArgumentException(Integer.toString(i));
            });

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            IntToDoubleFunction throwing = function.onErrorThrowAsUnchecked(errorMapper);

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> throwing.applyAsDouble(1));
            assertEquals("1", thrown.getMessage());

            verify(function).applyAsDouble(1);
            verify(function).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }
    }

    @Nested
    class OnErrorHandleChecked {

        @Test
        void testNullArgument() {
            CheckedIntToDoubleFunction<IOException> function = Spied.checkedIntToDoubleFunction(i -> i + 1);

            assertThrows(NullPointerException.class, () -> function.onErrorHandleChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedIntToDoubleFunction<IOException> function = Spied.checkedIntToDoubleFunction(i -> i + 1);

            CheckedToDoubleFunction<IOException, ExecutionException> errorHandler = Spied.checkedToDoubleFunction(e -> 0D);

            CheckedIntToDoubleFunction<ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

            assertEquals(2D, handling.applyAsDouble(1));

            verify(function).applyAsDouble(1);
            verify(function).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException, ExecutionException {
                CheckedIntToDoubleFunction<IOException> function = Spied.checkedIntToDoubleFunction(i -> {
                    throw new IOException(Integer.toString(i));
                });

                CheckedToDoubleFunction<IOException, ExecutionException> errorHandler = Spied.checkedToDoubleFunction(e -> 0D);

                CheckedIntToDoubleFunction<ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

                assertEquals(0D, handling.applyAsDouble(1));

                verify(function).applyAsDouble(1);
                verify(function).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsDouble(any());
                verifyNoMoreInteractions(function, errorHandler);
            }

            @Test
            void testHandlerThrowsChecked() throws IOException, ExecutionException {
                CheckedIntToDoubleFunction<IOException> function = Spied.checkedIntToDoubleFunction(i -> {
                    throw new IOException(Integer.toString(i));
                });

                CheckedToDoubleFunction<IOException, ExecutionException> errorHandler = Spied.checkedToDoubleFunction(e -> {
                    throw new ExecutionException(e);
                });

                CheckedIntToDoubleFunction<ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

                ExecutionException thrown = assertThrows(ExecutionException.class, () -> handling.applyAsDouble(1));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("1", cause.getMessage());

                verify(function).applyAsDouble(1);
                verify(function).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsDouble(any());
                verifyNoMoreInteractions(function, errorHandler);
            }

            @Test
            void testHandlerThrowsUnchecked() throws IOException, ExecutionException {
                CheckedIntToDoubleFunction<IOException> function = Spied.checkedIntToDoubleFunction(i -> {
                    throw new IOException(Integer.toString(i));
                });

                CheckedToDoubleFunction<IOException, ExecutionException> errorHandler = Spied.checkedToDoubleFunction(e -> {
                    throw new IllegalStateException(e);
                });

                CheckedIntToDoubleFunction<ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.applyAsDouble(1));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("1", cause.getMessage());

                verify(function).applyAsDouble(1);
                verify(function).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsDouble(any());
                verifyNoMoreInteractions(function, errorHandler);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedIntToDoubleFunction<IOException> function = Spied.checkedIntToDoubleFunction(i -> {
                throw new IllegalStateException(Integer.toString(i));
            });

            CheckedToDoubleFunction<IOException, ExecutionException> errorHandler = Spied.checkedToDoubleFunction(e -> 0D);

            CheckedIntToDoubleFunction<ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.applyAsDouble(1));
            assertEquals("1", thrown.getMessage());

            verify(function).applyAsDouble(1);
            verify(function).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }
    }

    @Nested
    class OnErrorHandleUnchecked {

        @Test
        void testNullArgument() {
            CheckedIntToDoubleFunction<IOException> function = Spied.checkedIntToDoubleFunction(i -> i + 1);

            assertThrows(NullPointerException.class, () -> function.onErrorHandleUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedIntToDoubleFunction<IOException> function = Spied.checkedIntToDoubleFunction(i -> i + 1);

            ToDoubleFunction<IOException> errorHandler = Spied.toDoubleFunction(e -> 0D);

            IntToDoubleFunction handling = function.onErrorHandleUnchecked(errorHandler);

            assertEquals(2D, handling.applyAsDouble(1));

            verify(function).applyAsDouble(1);
            verify(function).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException {
                CheckedIntToDoubleFunction<IOException> function = Spied.checkedIntToDoubleFunction(i -> {
                    throw new IOException(Integer.toString(i));
                });

                ToDoubleFunction<IOException> errorHandler = Spied.toDoubleFunction(e -> 0D);

                IntToDoubleFunction handling = function.onErrorHandleUnchecked(errorHandler);

                assertEquals(0D, handling.applyAsDouble(1));

                verify(function).applyAsDouble(1);
                verify(function).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).applyAsDouble(any());
                verifyNoMoreInteractions(function, errorHandler);
            }

            @Test
            void testHandlerThrowsUnchecked() throws IOException {
                CheckedIntToDoubleFunction<IOException> function = Spied.checkedIntToDoubleFunction(i -> {
                    throw new IOException(Integer.toString(i));
                });

                ToDoubleFunction<IOException> errorHandler = Spied.toDoubleFunction(e -> {
                    throw new IllegalStateException(e);
                });

                IntToDoubleFunction handling = function.onErrorHandleUnchecked(errorHandler);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.applyAsDouble(1));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("1", cause.getMessage());

                verify(function).applyAsDouble(1);
                verify(function).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).applyAsDouble(any());
                verifyNoMoreInteractions(function, errorHandler);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedIntToDoubleFunction<IOException> function = Spied.checkedIntToDoubleFunction(i -> {
                throw new IllegalStateException(Integer.toString(i));
            });

            ToDoubleFunction<IOException> errorHandler = Spied.toDoubleFunction(e -> 0D);

            IntToDoubleFunction handling = function.onErrorHandleUnchecked(errorHandler);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.applyAsDouble(1));
            assertEquals("1", thrown.getMessage());

            verify(function).applyAsDouble(1);
            verify(function).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }
    }

    @Nested
    class OnErrorApplyChecked {

        @Test
        void testNullArgument() {
            CheckedIntToDoubleFunction<IOException> function = Spied.checkedIntToDoubleFunction(i -> i + 1);

            assertThrows(NullPointerException.class, () -> function.onErrorApplyChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedIntToDoubleFunction<IOException> function = Spied.checkedIntToDoubleFunction(i -> i + 1);

            CheckedIntToDoubleFunction<ExecutionException> fallback = Spied.checkedIntToDoubleFunction(i -> i * 3);

            CheckedIntToDoubleFunction<ExecutionException> applying = function.onErrorApplyChecked(fallback);

            assertEquals(2D, applying.applyAsDouble(1));

            verify(function).applyAsDouble(1);
            verify(function).onErrorApplyChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                CheckedIntToDoubleFunction<IOException> function = Spied.checkedIntToDoubleFunction(i -> {
                    throw new IOException(Integer.toString(i));
                });

                CheckedIntToDoubleFunction<ParseException> fallback = Spied.checkedIntToDoubleFunction(i -> i * 3);

                CheckedIntToDoubleFunction<ParseException> applying = function.onErrorApplyChecked(fallback);

                assertEquals(3D, applying.applyAsDouble(1));

                verify(function).applyAsDouble(1);
                verify(function).onErrorApplyChecked(fallback);
                verify(fallback).applyAsDouble(1);
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                CheckedIntToDoubleFunction<IOException> function = Spied.checkedIntToDoubleFunction(i -> {
                    throw new IOException(Integer.toString(i));
                });

                CheckedIntToDoubleFunction<ParseException> fallback = Spied.checkedIntToDoubleFunction(i -> {
                    throw new ParseException(Integer.toString(i), 0);
                });

                CheckedIntToDoubleFunction<ParseException> applying = function.onErrorApplyChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> applying.applyAsDouble(1));
                assertEquals("1", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(function).applyAsDouble(1);
                verify(function).onErrorApplyChecked(fallback);
                verify(fallback).applyAsDouble(1);
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsUnchecked() throws IOException, ParseException {
                CheckedIntToDoubleFunction<IOException> function = Spied.checkedIntToDoubleFunction(i -> {
                    throw new IOException(Integer.toString(i));
                });

                CheckedIntToDoubleFunction<ParseException> fallback = Spied.checkedIntToDoubleFunction(i -> {
                    throw new IllegalStateException(Integer.toString(i));
                });

                CheckedIntToDoubleFunction<ParseException> applying = function.onErrorApplyChecked(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> applying.applyAsDouble(1));
                assertEquals("1", thrown.getMessage());

                verify(function).applyAsDouble(1);
                verify(function).onErrorApplyChecked(fallback);
                verify(fallback).applyAsDouble(1);
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedIntToDoubleFunction<IOException> function = Spied.checkedIntToDoubleFunction(i -> {
                throw new IllegalStateException(Integer.toString(i));
            });

            CheckedIntToDoubleFunction<ParseException> fallback = Spied.checkedIntToDoubleFunction(i -> i * 3);

            CheckedIntToDoubleFunction<ParseException> applying = function.onErrorApplyChecked(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> applying.applyAsDouble(1));
            assertEquals("1", thrown.getMessage());

            verify(function).applyAsDouble(1);
            verify(function).onErrorApplyChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorApplyUnchecked {

        @Test
        void testNullArgument() {
            CheckedIntToDoubleFunction<IOException> function = Spied.checkedIntToDoubleFunction(i -> i + 1);

            assertThrows(NullPointerException.class, () -> function.onErrorApplyUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedIntToDoubleFunction<IOException> function = Spied.checkedIntToDoubleFunction(i -> i + 1);

            IntToDoubleFunction fallback = Spied.intToDoubleFunction(i -> i * 3);

            IntToDoubleFunction applying = function.onErrorApplyUnchecked(fallback);

            assertEquals(2D, applying.applyAsDouble(1));

            verify(function).applyAsDouble(1);
            verify(function).onErrorApplyUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                CheckedIntToDoubleFunction<IOException> function = Spied.checkedIntToDoubleFunction(i -> {
                    throw new IOException(Integer.toString(i));
                });

                IntToDoubleFunction fallback = Spied.intToDoubleFunction(i -> i * 3);

                IntToDoubleFunction applying = function.onErrorApplyUnchecked(fallback);

                assertEquals(3D, applying.applyAsDouble(1));

                verify(function).applyAsDouble(1);
                verify(function).onErrorApplyUnchecked(fallback);
                verify(fallback).applyAsDouble(1);
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsUnchecked() throws IOException {
                CheckedIntToDoubleFunction<IOException> function = Spied.checkedIntToDoubleFunction(i -> {
                    throw new IOException(Integer.toString(i));
                });

                IntToDoubleFunction fallback = Spied.intToDoubleFunction(i -> {
                    throw new IllegalStateException(Integer.toString(i));
                });

                IntToDoubleFunction applying = function.onErrorApplyUnchecked(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> applying.applyAsDouble(1));
                assertEquals("1", thrown.getMessage());

                verify(function).applyAsDouble(1);
                verify(function).onErrorApplyUnchecked(fallback);
                verify(fallback).applyAsDouble(1);
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedIntToDoubleFunction<IOException> function = Spied.checkedIntToDoubleFunction(i -> {
                throw new IllegalStateException(Integer.toString(i));
            });

            IntToDoubleFunction fallback = Spied.intToDoubleFunction(i -> i * 3);

            IntToDoubleFunction applying = function.onErrorApplyUnchecked(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> applying.applyAsDouble(1));
            assertEquals("1", thrown.getMessage());

            verify(function).applyAsDouble(1);
            verify(function).onErrorApplyUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorGetChecked {

        @Test
        void testNullArgument() {
            CheckedIntToDoubleFunction<IOException> function = Spied.checkedIntToDoubleFunction(i -> i + 1);

            assertThrows(NullPointerException.class, () -> function.onErrorGetChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedIntToDoubleFunction<IOException> function = Spied.checkedIntToDoubleFunction(i -> i + 1);

            CheckedDoubleSupplier<ExecutionException> fallback = Spied.checkedDoubleSupplier(() -> 0D);

            CheckedIntToDoubleFunction<ExecutionException> getting = function.onErrorGetChecked(fallback);

            assertEquals(2D, getting.applyAsDouble(1));

            verify(function).applyAsDouble(1);
            verify(function).onErrorGetChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                CheckedIntToDoubleFunction<IOException> function = Spied.checkedIntToDoubleFunction(i -> {
                    throw new IOException(Integer.toString(i));
                });

                CheckedDoubleSupplier<ParseException> fallback = Spied.checkedDoubleSupplier(() -> 0D);

                CheckedIntToDoubleFunction<ParseException> getting = function.onErrorGetChecked(fallback);

                assertEquals(0D, getting.applyAsDouble(1));

                verify(function).applyAsDouble(1);
                verify(function).onErrorGetChecked(fallback);
                verify(fallback).getAsDouble();
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                CheckedIntToDoubleFunction<IOException> function = Spied.checkedIntToDoubleFunction(i -> {
                    throw new IOException(Integer.toString(i));
                });

                CheckedDoubleSupplier<ParseException> fallback = Spied.checkedDoubleSupplier(() -> {
                    throw new ParseException("bar", 0);
                });

                CheckedIntToDoubleFunction<ParseException> getting = function.onErrorGetChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> getting.applyAsDouble(1));
                assertEquals("bar", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(function).applyAsDouble(1);
                verify(function).onErrorGetChecked(fallback);
                verify(fallback).getAsDouble();
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsUnchecked() throws IOException, ParseException {
                CheckedIntToDoubleFunction<IOException> function = Spied.checkedIntToDoubleFunction(i -> {
                    throw new IOException(Integer.toString(i));
                });

                CheckedDoubleSupplier<ParseException> fallback = Spied.checkedDoubleSupplier(() -> {
                    throw new IllegalStateException("bar");
                });

                CheckedIntToDoubleFunction<ParseException> getting = function.onErrorGetChecked(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> getting.applyAsDouble(1));
                assertEquals("bar", thrown.getMessage());

                verify(function).applyAsDouble(1);
                verify(function).onErrorGetChecked(fallback);
                verify(fallback).getAsDouble();
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedIntToDoubleFunction<IOException> function = Spied.checkedIntToDoubleFunction(i -> {
                throw new IllegalStateException(Integer.toString(i));
            });

            CheckedDoubleSupplier<ParseException> fallback = Spied.checkedDoubleSupplier(() -> 0D);

            CheckedIntToDoubleFunction<ParseException> getting = function.onErrorGetChecked(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> getting.applyAsDouble(1));
            assertEquals("1", thrown.getMessage());

            verify(function).applyAsDouble(1);
            verify(function).onErrorGetChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorGetUnchecked {

        @Test
        void testNullArgument() {
            CheckedIntToDoubleFunction<IOException> function = Spied.checkedIntToDoubleFunction(i -> i + 1);

            assertThrows(NullPointerException.class, () -> function.onErrorGetUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedIntToDoubleFunction<IOException> function = Spied.checkedIntToDoubleFunction(i -> i + 1);

            DoubleSupplier fallback = Spied.doubleSupplier(() -> 0D);

            IntToDoubleFunction getting = function.onErrorGetUnchecked(fallback);

            assertEquals(2D, getting.applyAsDouble(1));

            verify(function).applyAsDouble(1);
            verify(function).onErrorGetUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                CheckedIntToDoubleFunction<IOException> function = Spied.checkedIntToDoubleFunction(i -> {
                    throw new IOException(Integer.toString(i));
                });

                DoubleSupplier fallback = Spied.doubleSupplier(() -> 0D);

                IntToDoubleFunction getting = function.onErrorGetUnchecked(fallback);

                assertEquals(0D, getting.applyAsDouble(1));

                verify(function).applyAsDouble(1);
                verify(function).onErrorGetUnchecked(fallback);
                verify(fallback).getAsDouble();
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsUnchecked() throws IOException {
                CheckedIntToDoubleFunction<IOException> function = Spied.checkedIntToDoubleFunction(i -> {
                    throw new IOException(Integer.toString(i));
                });

                DoubleSupplier fallback = Spied.doubleSupplier(() -> {
                    throw new IllegalStateException("bar");
                });

                IntToDoubleFunction getting = function.onErrorGetUnchecked(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> getting.applyAsDouble(1));
                assertEquals("bar", thrown.getMessage());

                verify(function).applyAsDouble(1);
                verify(function).onErrorGetUnchecked(fallback);
                verify(fallback).getAsDouble();
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedIntToDoubleFunction<IOException> function = Spied.checkedIntToDoubleFunction(i -> {
                throw new IllegalStateException(Integer.toString(i));
            });

            DoubleSupplier fallback = Spied.doubleSupplier(() -> 0D);

            IntToDoubleFunction getting = function.onErrorGetUnchecked(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> getting.applyAsDouble(1));
            assertEquals("1", thrown.getMessage());

            verify(function).applyAsDouble(1);
            verify(function).onErrorGetUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorReturn {

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedIntToDoubleFunction<IOException> function = Spied.checkedIntToDoubleFunction(i -> i + 1);

            IntToDoubleFunction returning = function.onErrorReturn(0D);

            assertEquals(2D, returning.applyAsDouble(1));

            verify(function).applyAsDouble(1);
            verify(function).onErrorReturn(0D);
            verifyNoMoreInteractions(function);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedIntToDoubleFunction<IOException> function = Spied.checkedIntToDoubleFunction(i -> {
                throw new IOException(Integer.toString(i));
            });

            IntToDoubleFunction returning = function.onErrorReturn(0D);

            assertEquals(0D, returning.applyAsDouble(1));

            verify(function).applyAsDouble(1);
            verify(function).onErrorReturn(0D);
            verifyNoMoreInteractions(function);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedIntToDoubleFunction<IOException> function = Spied.checkedIntToDoubleFunction(i -> {
                throw new IllegalStateException(Integer.toString(i));
            });

            IntToDoubleFunction returning = function.onErrorReturn(0D);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> returning.applyAsDouble(1));
            assertEquals("1", thrown.getMessage());

            verify(function).applyAsDouble(1);
            verify(function).onErrorReturn(0D);
            verifyNoMoreInteractions(function);
        }
    }

    @Nested
    class Unchecked {

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedIntToDoubleFunction<IOException> function = Spied.checkedIntToDoubleFunction(i -> i + 1);

            IntToDoubleFunction unchecked = function.unchecked();

            assertEquals(2D, unchecked.applyAsDouble(1));

            verify(function).applyAsDouble(1);
            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(function);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedIntToDoubleFunction<IOException> function = Spied.checkedIntToDoubleFunction(i -> {
                throw new IOException(Integer.toString(i));
            });

            IntToDoubleFunction unchecked = function.unchecked();

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.applyAsDouble(1));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1", cause.getMessage());

            verify(function).applyAsDouble(1);
            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(function);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedIntToDoubleFunction<IOException> function = Spied.checkedIntToDoubleFunction(i -> {
                throw new IllegalArgumentException(Integer.toString(i));
            });

            IntToDoubleFunction unchecked = function.unchecked();

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> unchecked.applyAsDouble(1));
            assertEquals("1", thrown.getMessage());

            verify(function).applyAsDouble(1);
            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(function);
        }
    }

    @Nested
    class Of {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> CheckedIntToDoubleFunction.of(null));
        }

        @Test
        void testNonNullArgument() throws IOException {
            CheckedIntToDoubleFunction<IOException> function = CheckedIntToDoubleFunction.of(i -> i + 1);

            assertEquals(2D, function.applyAsDouble(1));
        }
    }

    @Nested
    class UncheckedFactory {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> CheckedIntToDoubleFunction.unchecked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedIntToDoubleFunction<IOException> function = Spied.checkedIntToDoubleFunction(i -> i + 1);

            IntToDoubleFunction unchecked = CheckedIntToDoubleFunction.unchecked(function);

            assertEquals(2D, unchecked.applyAsDouble(1));

            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verify(function).applyAsDouble(1);
            verifyNoMoreInteractions(function);
        }

        @Test
        void testArgumentThrowsChecked() throws IOException {
            CheckedIntToDoubleFunction<IOException> function = Spied.checkedIntToDoubleFunction(i -> {
                throw new IOException(Integer.toString(i));
            });

            IntToDoubleFunction unchecked = CheckedIntToDoubleFunction.unchecked(function);

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.applyAsDouble(1));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1", cause.getMessage());

            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verify(function).applyAsDouble(1);
            verifyNoMoreInteractions(function);
        }

        @Test
        void testArgumentThrowsUnchecked() throws IOException {
            CheckedIntToDoubleFunction<IOException> function = Spied.checkedIntToDoubleFunction(i -> {
                throw new IllegalArgumentException(Integer.toString(i));
            });

            IntToDoubleFunction unchecked = CheckedIntToDoubleFunction.unchecked(function);

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> unchecked.applyAsDouble(1));
            assertEquals("1", thrown.getMessage());

            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verify(function).applyAsDouble(1);
            verifyNoMoreInteractions(function);
        }
    }

    @Nested
    class CheckedWrapper {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> CheckedIntToDoubleFunction.checked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedIntToDoubleFunction<IOException> function = CheckedIntToDoubleFunction.checked(i -> i + 1);

            assertEquals(2D, function.applyAsDouble(1));
        }

        @Test
        void testArgumentThrowsUnchecked() {
            CheckedIntToDoubleFunction<IOException> function = CheckedIntToDoubleFunction.checked(i -> {
                throw new UncheckedException(Integer.toString(i), new IOException());
            });

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> function.applyAsDouble(1));
            assertEquals("1", thrown.getMessage());
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertNull(cause.getMessage());
        }
    }

    @Nested
    class CheckedUnwrapper {

        @Test
        void testNullArguments() {
            IntToDoubleFunction function = i -> i + 1;

            assertThrows(NullPointerException.class, () -> CheckedIntToDoubleFunction.checked(null, IOException.class));
            assertThrows(NullPointerException.class, () -> CheckedIntToDoubleFunction.checked(function, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedIntToDoubleFunction<IOException> function = CheckedIntToDoubleFunction.checked(i -> i + 1, IOException.class);

            assertEquals(2D, function.applyAsDouble(1));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                CheckedIntToDoubleFunction<IOException> function = CheckedIntToDoubleFunction.checked(i -> {
                    throw new UncheckedException(new IOException(Integer.toString(i)));
                }, IOException.class);

                IOException thrown = assertThrows(IOException.class, () -> function.applyAsDouble(1));
                assertEquals("1", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                CheckedIntToDoubleFunction<IOException> function = CheckedIntToDoubleFunction.checked(i -> {
                    throw new UncheckedException(new FileNotFoundException(Integer.toString(i)));
                }, IOException.class);

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class, () -> function.applyAsDouble(1));
                assertEquals("1", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                CheckedIntToDoubleFunction<IOException> function = CheckedIntToDoubleFunction.checked(i -> {
                    throw new UncheckedException(new ParseException(Integer.toString(i), 0));
                }, IOException.class);

                UncheckedException thrown = assertThrows(UncheckedException.class, () -> function.applyAsDouble(1));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("1", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            CheckedIntToDoubleFunction<IOException> function = CheckedIntToDoubleFunction.checked(i -> {
                throw new IllegalStateException(Integer.toString(i));
            }, IOException.class);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> function.applyAsDouble(1));
            assertEquals("1", thrown.getMessage());
        }
    }

    @Nested
    class InvokeAndUnwrap {

        @Test
        void testNullArguments() {
            IntToDoubleFunction function = i -> String.valueOf(Integer.toString(i)).length();

            assertThrows(NullPointerException.class, () -> CheckedIntToDoubleFunction.invokeAndUnwrap(null, 1, IOException.class));
            assertThrows(NullPointerException.class, () -> CheckedIntToDoubleFunction.invokeAndUnwrap(function, 1, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            IntToDoubleFunction function = i -> i + 1;

            assertEquals(2D, CheckedIntToDoubleFunction.invokeAndUnwrap(function, 1, IOException.class));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                IntToDoubleFunction function = i -> {
                    throw new UncheckedException(new IOException(Integer.toString(i)));
                };

                IOException thrown = assertThrows(IOException.class,
                        () -> CheckedIntToDoubleFunction.invokeAndUnwrap(function, 1, IOException.class));
                assertEquals("1", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                IntToDoubleFunction function = i -> {
                    throw new UncheckedException(new FileNotFoundException(Integer.toString(i)));
                };

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class,
                        () -> CheckedIntToDoubleFunction.invokeAndUnwrap(function, 1, IOException.class));
                assertEquals("1", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                IntToDoubleFunction function = i -> {
                    throw new UncheckedException(new ParseException(Integer.toString(i), 0));
                };

                UncheckedException thrown = assertThrows(UncheckedException.class,
                        () -> CheckedIntToDoubleFunction.invokeAndUnwrap(function, 1, IOException.class));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("1", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            IntToDoubleFunction function = i -> {
                throw new IllegalStateException(Integer.toString(i));
            };

            IllegalStateException thrown = assertThrows(IllegalStateException.class,
                    () -> CheckedIntToDoubleFunction.invokeAndUnwrap(function, 1, IOException.class));
            assertEquals("1", thrown.getMessage());
        }
    }
}
