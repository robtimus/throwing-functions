/*
 * CheckedToDoubleFunctionTest.java
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
class CheckedToDoubleFunctionTest {

    @Nested
    class OnErrorThrowAsChecked {

        @Test
        void testNullArgument() {
            CheckedToDoubleFunction<String, IOException> function = Spied.checkedToDoubleFunction(String::length);

            assertThrows(NullPointerException.class, () -> function.onErrorThrowAsChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedToDoubleFunction<String, IOException> function = Spied.checkedToDoubleFunction(String::length);

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedToDoubleFunction<String, ExecutionException> throwing = function.onErrorThrowAsChecked(errorMapper);

            assertEquals(3D, throwing.applyAsDouble("foo"));

            verify(function).applyAsDouble("foo");
            verify(function).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedToDoubleFunction<String, IOException> function = Spied.checkedToDoubleFunction(s -> {
                throw new IOException(s);
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedToDoubleFunction<String, ExecutionException> throwing = function.onErrorThrowAsChecked(errorMapper);

            ExecutionException thrown = assertThrows(ExecutionException.class, () -> throwing.applyAsDouble("foo"));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo", cause.getMessage());

            verify(function).applyAsDouble("foo");
            verify(function).onErrorThrowAsChecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(function, errorMapper);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedToDoubleFunction<String, IOException> function = Spied.checkedToDoubleFunction(s -> {
                throw new IllegalStateException(s);
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedToDoubleFunction<String, ExecutionException> throwing = function.onErrorThrowAsChecked(errorMapper);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> throwing.applyAsDouble("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(function).applyAsDouble("foo");
            verify(function).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }
    }

    @Nested
    class OnErrorThrowAsUnchecked {

        @Test
        void testNullArgument() {
            CheckedToDoubleFunction<String, IOException> function = Spied.checkedToDoubleFunction(String::length);

            assertThrows(NullPointerException.class, () -> function.onErrorThrowAsUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedToDoubleFunction<String, IOException> function = Spied.checkedToDoubleFunction(String::length);

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            ToDoubleFunction<String> throwing = function.onErrorThrowAsUnchecked(errorMapper);

            assertEquals(3D, throwing.applyAsDouble("foo"));

            verify(function).applyAsDouble("foo");
            verify(function).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedToDoubleFunction<String, IOException> function = Spied.checkedToDoubleFunction(s -> {
                throw new IOException(s);
            });

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            ToDoubleFunction<String> throwing = function.onErrorThrowAsUnchecked(errorMapper);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> throwing.applyAsDouble("foo"));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo", cause.getMessage());

            verify(function).applyAsDouble("foo");
            verify(function).onErrorThrowAsUnchecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(function, errorMapper);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedToDoubleFunction<String, IOException> function = Spied.checkedToDoubleFunction(s -> {
                throw new IllegalArgumentException(s);
            });

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            ToDoubleFunction<String> throwing = function.onErrorThrowAsUnchecked(errorMapper);

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> throwing.applyAsDouble("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(function).applyAsDouble("foo");
            verify(function).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }
    }

    @Nested
    class OnErrorHandleChecked {

        @Test
        void testNullArgument() {
            CheckedToDoubleFunction<String, IOException> function = Spied.checkedToDoubleFunction(String::length);

            assertThrows(NullPointerException.class, () -> function.onErrorHandleChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedToDoubleFunction<String, IOException> function = Spied.checkedToDoubleFunction(String::length);

            CheckedToDoubleFunction<IOException, ExecutionException> errorHandler = Spied.checkedToDoubleFunction(e -> 0D);

            CheckedToDoubleFunction<String, ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

            assertEquals(3D, handling.applyAsDouble("foo"));

            verify(function).applyAsDouble("foo");
            verify(function).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException, ExecutionException {
                CheckedToDoubleFunction<String, IOException> function = Spied.checkedToDoubleFunction(s -> {
                    throw new IOException(s);
                });

                CheckedToDoubleFunction<IOException, ExecutionException> errorHandler = Spied.checkedToDoubleFunction(e -> 0D);

                CheckedToDoubleFunction<String, ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

                assertEquals(0D, handling.applyAsDouble("foo"));

                verify(function).applyAsDouble("foo");
                verify(function).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsDouble(any());
                verifyNoMoreInteractions(function, errorHandler);
            }

            @Test
            void testHandlerThrowsChecked() throws IOException, ExecutionException {
                CheckedToDoubleFunction<String, IOException> function = Spied.checkedToDoubleFunction(s -> {
                    throw new IOException(s);
                });

                CheckedToDoubleFunction<IOException, ExecutionException> errorHandler = Spied.checkedToDoubleFunction(e -> {
                    throw new ExecutionException(e);
                });

                CheckedToDoubleFunction<String, ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

                ExecutionException thrown = assertThrows(ExecutionException.class, () -> handling.applyAsDouble("foo"));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());

                verify(function).applyAsDouble("foo");
                verify(function).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsDouble(any());
                verifyNoMoreInteractions(function, errorHandler);
            }

            @Test
            void testHandlerThrowsUnchecked() throws IOException, ExecutionException {
                CheckedToDoubleFunction<String, IOException> function = Spied.checkedToDoubleFunction(s -> {
                    throw new IOException(s);
                });

                CheckedToDoubleFunction<IOException, ExecutionException> errorHandler = Spied.checkedToDoubleFunction(e -> {
                    throw new IllegalStateException(e);
                });

                CheckedToDoubleFunction<String, ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.applyAsDouble("foo"));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());

                verify(function).applyAsDouble("foo");
                verify(function).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsDouble(any());
                verifyNoMoreInteractions(function, errorHandler);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedToDoubleFunction<String, IOException> function = Spied.checkedToDoubleFunction(s -> {
                throw new IllegalStateException(s);
            });

            CheckedToDoubleFunction<IOException, ExecutionException> errorHandler = Spied.checkedToDoubleFunction(e -> 0D);

            CheckedToDoubleFunction<String, ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.applyAsDouble("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(function).applyAsDouble("foo");
            verify(function).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }
    }

    @Nested
    class OnErrorHandleUnchecked {

        @Test
        void testNullArgument() {
            CheckedToDoubleFunction<String, IOException> function = Spied.checkedToDoubleFunction(String::length);

            assertThrows(NullPointerException.class, () -> function.onErrorHandleUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedToDoubleFunction<String, IOException> function = Spied.checkedToDoubleFunction(String::length);

            ToDoubleFunction<IOException> errorHandler = Spied.toDoubleFunction(e -> 0D);

            ToDoubleFunction<String> handling = function.onErrorHandleUnchecked(errorHandler);

            assertEquals(3D, handling.applyAsDouble("foo"));

            verify(function).applyAsDouble("foo");
            verify(function).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException {
                CheckedToDoubleFunction<String, IOException> function = Spied.checkedToDoubleFunction(s -> {
                    throw new IOException(s);
                });

                ToDoubleFunction<IOException> errorHandler = Spied.toDoubleFunction(e -> 0D);

                ToDoubleFunction<String> handling = function.onErrorHandleUnchecked(errorHandler);

                assertEquals(0D, handling.applyAsDouble("foo"));

                verify(function).applyAsDouble("foo");
                verify(function).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).applyAsDouble(any());
                verifyNoMoreInteractions(function, errorHandler);
            }

            @Test
            void testHandlerThrowsUnchecked() throws IOException {
                CheckedToDoubleFunction<String, IOException> function = Spied.checkedToDoubleFunction(s -> {
                    throw new IOException(s);
                });

                ToDoubleFunction<IOException> errorHandler = Spied.toDoubleFunction(e -> {
                    throw new IllegalStateException(e);
                });

                ToDoubleFunction<String> handling = function.onErrorHandleUnchecked(errorHandler);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.applyAsDouble("foo"));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());

                verify(function).applyAsDouble("foo");
                verify(function).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).applyAsDouble(any());
                verifyNoMoreInteractions(function, errorHandler);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedToDoubleFunction<String, IOException> function = Spied.checkedToDoubleFunction(s -> {
                throw new IllegalStateException(s);
            });

            ToDoubleFunction<IOException> errorHandler = Spied.toDoubleFunction(e -> 0D);

            ToDoubleFunction<String> handling = function.onErrorHandleUnchecked(errorHandler);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.applyAsDouble("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(function).applyAsDouble("foo");
            verify(function).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }
    }

    @Nested
    class OnErrorApplyChecked {

        @Test
        void testNullArgument() {
            CheckedToDoubleFunction<String, IOException> function = Spied.checkedToDoubleFunction(String::length);

            assertThrows(NullPointerException.class, () -> function.onErrorApplyChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedToDoubleFunction<String, IOException> function = Spied.checkedToDoubleFunction(String::length);

            CheckedToDoubleFunction<String, ExecutionException> fallback = Spied.checkedToDoubleFunction(s -> 2 * s.length());

            CheckedToDoubleFunction<String, ExecutionException> applying = function.onErrorApplyChecked(fallback);

            assertEquals(3D, applying.applyAsDouble("foo"));

            verify(function).applyAsDouble("foo");
            verify(function).onErrorApplyChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                CheckedToDoubleFunction<String, IOException> function = Spied.checkedToDoubleFunction(s -> {
                    throw new IOException(s);
                });

                CheckedToDoubleFunction<String, ParseException> fallback = Spied.checkedToDoubleFunction(s -> 2 * s.length());

                CheckedToDoubleFunction<String, ParseException> applying = function.onErrorApplyChecked(fallback);

                assertEquals(6D, applying.applyAsDouble("foo"));

                verify(function).applyAsDouble("foo");
                verify(function).onErrorApplyChecked(fallback);
                verify(fallback).applyAsDouble("foo");
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                CheckedToDoubleFunction<String, IOException> function = Spied.checkedToDoubleFunction(s -> {
                    throw new IOException(s);
                });

                CheckedToDoubleFunction<String, ParseException> fallback = Spied.checkedToDoubleFunction(s -> {
                    throw new ParseException(s, 0);
                });

                CheckedToDoubleFunction<String, ParseException> applying = function.onErrorApplyChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> applying.applyAsDouble("foo"));
                assertEquals("foo", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(function).applyAsDouble("foo");
                verify(function).onErrorApplyChecked(fallback);
                verify(fallback).applyAsDouble("foo");
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsUnchecked() throws IOException, ParseException {
                CheckedToDoubleFunction<String, IOException> function = Spied.checkedToDoubleFunction(s -> {
                    throw new IOException(s);
                });

                CheckedToDoubleFunction<String, ParseException> fallback = Spied.checkedToDoubleFunction(s -> {
                    throw new IllegalStateException(s);
                });

                CheckedToDoubleFunction<String, ParseException> applying = function.onErrorApplyChecked(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> applying.applyAsDouble("foo"));
                assertEquals("foo", thrown.getMessage());

                verify(function).applyAsDouble("foo");
                verify(function).onErrorApplyChecked(fallback);
                verify(fallback).applyAsDouble("foo");
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedToDoubleFunction<String, IOException> function = Spied.checkedToDoubleFunction(s -> {
                throw new IllegalStateException(s);
            });

            CheckedToDoubleFunction<String, ParseException> fallback = Spied.checkedToDoubleFunction(s -> 2 * s.length());

            CheckedToDoubleFunction<String, ParseException> applying = function.onErrorApplyChecked(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> applying.applyAsDouble("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(function).applyAsDouble("foo");
            verify(function).onErrorApplyChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorApplyUnchecked {

        @Test
        void testNullArgument() {
            CheckedToDoubleFunction<String, IOException> function = Spied.checkedToDoubleFunction(String::length);

            assertThrows(NullPointerException.class, () -> function.onErrorApplyUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedToDoubleFunction<String, IOException> function = Spied.checkedToDoubleFunction(String::length);

            ToDoubleFunction<String> fallback = Spied.toDoubleFunction(s -> 2 * s.length());

            ToDoubleFunction<String> applying = function.onErrorApplyUnchecked(fallback);

            assertEquals(3D, applying.applyAsDouble("foo"));

            verify(function).applyAsDouble("foo");
            verify(function).onErrorApplyUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                CheckedToDoubleFunction<String, IOException> function = Spied.checkedToDoubleFunction(s -> {
                    throw new IOException(s);
                });

                ToDoubleFunction<String> fallback = Spied.toDoubleFunction(s -> 2 * s.length());

                ToDoubleFunction<String> applying = function.onErrorApplyUnchecked(fallback);

                assertEquals(6D, applying.applyAsDouble("foo"));

                verify(function).applyAsDouble("foo");
                verify(function).onErrorApplyUnchecked(fallback);
                verify(fallback).applyAsDouble("foo");
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsUnchecked() throws IOException {
                CheckedToDoubleFunction<String, IOException> function = Spied.checkedToDoubleFunction(s -> {
                    throw new IOException(s);
                });

                ToDoubleFunction<String> fallback = Spied.toDoubleFunction(s -> {
                    throw new IllegalStateException(s);
                });

                ToDoubleFunction<String> applying = function.onErrorApplyUnchecked(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> applying.applyAsDouble("foo"));
                assertEquals("foo", thrown.getMessage());

                verify(function).applyAsDouble("foo");
                verify(function).onErrorApplyUnchecked(fallback);
                verify(fallback).applyAsDouble("foo");
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedToDoubleFunction<String, IOException> function = Spied.checkedToDoubleFunction(s -> {
                throw new IllegalStateException(s);
            });

            ToDoubleFunction<String> fallback = Spied.toDoubleFunction(s -> 2 * s.length());

            ToDoubleFunction<String> applying = function.onErrorApplyUnchecked(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> applying.applyAsDouble("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(function).applyAsDouble("foo");
            verify(function).onErrorApplyUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorGetChecked {

        @Test
        void testNullArgument() {
            CheckedToDoubleFunction<String, IOException> function = Spied.checkedToDoubleFunction(String::length);

            assertThrows(NullPointerException.class, () -> function.onErrorGetChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedToDoubleFunction<String, IOException> function = Spied.checkedToDoubleFunction(String::length);

            CheckedDoubleSupplier<ExecutionException> fallback = Spied.checkedDoubleSupplier(() -> 0D);

            CheckedToDoubleFunction<String, ExecutionException> getting = function.onErrorGetChecked(fallback);

            assertEquals(3D, getting.applyAsDouble("foo"));

            verify(function).applyAsDouble("foo");
            verify(function).onErrorGetChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                CheckedToDoubleFunction<String, IOException> function = Spied.checkedToDoubleFunction(s -> {
                    throw new IOException(s);
                });

                CheckedDoubleSupplier<ParseException> fallback = Spied.checkedDoubleSupplier(() -> 0D);

                CheckedToDoubleFunction<String, ParseException> getting = function.onErrorGetChecked(fallback);

                assertEquals(0D, getting.applyAsDouble("foo"));

                verify(function).applyAsDouble("foo");
                verify(function).onErrorGetChecked(fallback);
                verify(fallback).getAsDouble();
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                CheckedToDoubleFunction<String, IOException> function = Spied.checkedToDoubleFunction(s -> {
                    throw new IOException(s);
                });

                CheckedDoubleSupplier<ParseException> fallback = Spied.checkedDoubleSupplier(() -> {
                    throw new ParseException("bar", 0);
                });

                CheckedToDoubleFunction<String, ParseException> getting = function.onErrorGetChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> getting.applyAsDouble("foo"));
                assertEquals("bar", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(function).applyAsDouble("foo");
                verify(function).onErrorGetChecked(fallback);
                verify(fallback).getAsDouble();
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsUnchecked() throws IOException, ParseException {
                CheckedToDoubleFunction<String, IOException> function = Spied.checkedToDoubleFunction(s -> {
                    throw new IOException(s);
                });

                CheckedDoubleSupplier<ParseException> fallback = Spied.checkedDoubleSupplier(() -> {
                    throw new IllegalStateException("bar");
                });

                CheckedToDoubleFunction<String, ParseException> getting = function.onErrorGetChecked(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> getting.applyAsDouble("foo"));
                assertEquals("bar", thrown.getMessage());

                verify(function).applyAsDouble("foo");
                verify(function).onErrorGetChecked(fallback);
                verify(fallback).getAsDouble();
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedToDoubleFunction<String, IOException> function = Spied.checkedToDoubleFunction(s -> {
                throw new IllegalStateException(s);
            });

            CheckedDoubleSupplier<ParseException> fallback = Spied.checkedDoubleSupplier(() -> 0D);

            CheckedToDoubleFunction<String, ParseException> getting = function.onErrorGetChecked(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> getting.applyAsDouble("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(function).applyAsDouble("foo");
            verify(function).onErrorGetChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorGetUnchecked {

        @Test
        void testNullArgument() {
            CheckedToDoubleFunction<String, IOException> function = Spied.checkedToDoubleFunction(String::length);

            assertThrows(NullPointerException.class, () -> function.onErrorGetUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedToDoubleFunction<String, IOException> function = Spied.checkedToDoubleFunction(String::length);

            DoubleSupplier fallback = Spied.doubleSupplier(() -> 0D);

            ToDoubleFunction<String> getting = function.onErrorGetUnchecked(fallback);

            assertEquals(3D, getting.applyAsDouble("foo"));

            verify(function).applyAsDouble("foo");
            verify(function).onErrorGetUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                CheckedToDoubleFunction<String, IOException> function = Spied.checkedToDoubleFunction(s -> {
                    throw new IOException(s);
                });

                DoubleSupplier fallback = Spied.doubleSupplier(() -> 0D);

                ToDoubleFunction<String> getting = function.onErrorGetUnchecked(fallback);

                assertEquals(0D, getting.applyAsDouble("foo"));

                verify(function).applyAsDouble("foo");
                verify(function).onErrorGetUnchecked(fallback);
                verify(fallback).getAsDouble();
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsUnchecked() throws IOException {
                CheckedToDoubleFunction<String, IOException> function = Spied.checkedToDoubleFunction(s -> {
                    throw new IOException(s);
                });

                DoubleSupplier fallback = Spied.doubleSupplier(() -> {
                    throw new IllegalStateException("bar");
                });

                ToDoubleFunction<String> getting = function.onErrorGetUnchecked(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> getting.applyAsDouble("foo"));
                assertEquals("bar", thrown.getMessage());

                verify(function).applyAsDouble("foo");
                verify(function).onErrorGetUnchecked(fallback);
                verify(fallback).getAsDouble();
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedToDoubleFunction<String, IOException> function = Spied.checkedToDoubleFunction(s -> {
                throw new IllegalStateException(s);
            });

            DoubleSupplier fallback = Spied.doubleSupplier(() -> 0D);

            ToDoubleFunction<String> getting = function.onErrorGetUnchecked(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> getting.applyAsDouble("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(function).applyAsDouble("foo");
            verify(function).onErrorGetUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorReturn {

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedToDoubleFunction<String, IOException> function = Spied.checkedToDoubleFunction(String::length);

            ToDoubleFunction<String> returning = function.onErrorReturn(0D);

            assertEquals(3D, returning.applyAsDouble("foo"));

            verify(function).applyAsDouble("foo");
            verify(function).onErrorReturn(0D);
            verifyNoMoreInteractions(function);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedToDoubleFunction<String, IOException> function = Spied.checkedToDoubleFunction(s -> {
                throw new IOException(s);
            });

            ToDoubleFunction<String> returning = function.onErrorReturn(0D);

            assertEquals(0D, returning.applyAsDouble("foo"));

            verify(function).applyAsDouble("foo");
            verify(function).onErrorReturn(0D);
            verifyNoMoreInteractions(function);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedToDoubleFunction<String, IOException> function = Spied.checkedToDoubleFunction(s -> {
                throw new IllegalStateException(s);
            });

            ToDoubleFunction<String> returning = function.onErrorReturn(0D);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> returning.applyAsDouble("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(function).applyAsDouble("foo");
            verify(function).onErrorReturn(0D);
            verifyNoMoreInteractions(function);
        }
    }

    @Nested
    class Unchecked {

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedToDoubleFunction<String, IOException> function = Spied.checkedToDoubleFunction(String::length);

            ToDoubleFunction<String> unchecked = function.unchecked();

            assertEquals(3D, unchecked.applyAsDouble("foo"));

            verify(function).applyAsDouble("foo");
            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(function);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedToDoubleFunction<String, IOException> function = Spied.checkedToDoubleFunction(s -> {
                throw new IOException(s);
            });

            ToDoubleFunction<String> unchecked = function.unchecked();

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.applyAsDouble("foo"));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo", cause.getMessage());

            verify(function).applyAsDouble("foo");
            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(function);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedToDoubleFunction<String, IOException> function = Spied.checkedToDoubleFunction(s -> {
                throw new IllegalArgumentException(s);
            });

            ToDoubleFunction<String> unchecked = function.unchecked();

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> unchecked.applyAsDouble("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(function).applyAsDouble("foo");
            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(function);
        }
    }

    @Nested
    class Of {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> CheckedToDoubleFunction.of(null));
        }

        @Test
        void testNonNullArgument() throws IOException {
            CheckedToDoubleFunction<String, IOException> function = CheckedToDoubleFunction.of(String::length);

            assertEquals(3D, function.applyAsDouble("foo"));
        }
    }

    @Nested
    class UncheckedFactory {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> CheckedToDoubleFunction.unchecked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedToDoubleFunction<String, IOException> function = Spied.checkedToDoubleFunction(String::length);

            ToDoubleFunction<String> unchecked = CheckedToDoubleFunction.unchecked(function);

            assertEquals(3D, unchecked.applyAsDouble("foo"));

            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verify(function).applyAsDouble("foo");
            verifyNoMoreInteractions(function);
        }

        @Test
        void testArgumentThrowsChecked() throws IOException {
            CheckedToDoubleFunction<String, IOException> function = Spied.checkedToDoubleFunction(s -> {
                throw new IOException(s);
            });

            ToDoubleFunction<String> unchecked = CheckedToDoubleFunction.unchecked(function);

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.applyAsDouble("foo"));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo", cause.getMessage());

            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verify(function).applyAsDouble("foo");
            verifyNoMoreInteractions(function);
        }

        @Test
        void testArgumentThrowsUnchecked() throws IOException {
            CheckedToDoubleFunction<String, IOException> function = Spied.checkedToDoubleFunction(s -> {
                throw new IllegalArgumentException(s);
            });

            ToDoubleFunction<String> unchecked = CheckedToDoubleFunction.unchecked(function);

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> unchecked.applyAsDouble("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verify(function).applyAsDouble("foo");
            verifyNoMoreInteractions(function);
        }
    }

    @Nested
    class CheckedWrapper {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> CheckedToDoubleFunction.checked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedToDoubleFunction<String, IOException> function = CheckedToDoubleFunction.checked(String::length);

            assertEquals(3D, function.applyAsDouble("foo"));
        }

        @Test
        void testArgumentThrowsUnchecked() {
            CheckedToDoubleFunction<String, IOException> function = CheckedToDoubleFunction.checked(s -> {
                throw new UncheckedException(s, new IOException());
            });

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> function.applyAsDouble("foo"));
            assertEquals("foo", thrown.getMessage());
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertNull(cause.getMessage());
        }
    }

    @Nested
    class CheckedUnwrapper {

        @Test
        void testNullArguments() {
            ToDoubleFunction<String> function = String::length;

            assertThrows(NullPointerException.class, () -> CheckedToDoubleFunction.checked(null, IOException.class));
            assertThrows(NullPointerException.class, () -> CheckedToDoubleFunction.checked(function, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedToDoubleFunction<String, IOException> function = CheckedToDoubleFunction.checked(String::length, IOException.class);

            assertEquals(3D, function.applyAsDouble("foo"));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                CheckedToDoubleFunction<String, IOException> function = CheckedToDoubleFunction.checked(s -> {
                    throw new UncheckedException(new IOException(s));
                }, IOException.class);

                IOException thrown = assertThrows(IOException.class, () -> function.applyAsDouble("foo"));
                assertEquals("foo", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                CheckedToDoubleFunction<String, IOException> function = CheckedToDoubleFunction.checked(s -> {
                    throw new UncheckedException(new FileNotFoundException(s));
                }, IOException.class);

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class, () -> function.applyAsDouble("foo"));
                assertEquals("foo", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                CheckedToDoubleFunction<String, IOException> function = CheckedToDoubleFunction.checked(s -> {
                    throw new UncheckedException(new ParseException(s, 0));
                }, IOException.class);

                UncheckedException thrown = assertThrows(UncheckedException.class, () -> function.applyAsDouble("foo"));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            CheckedToDoubleFunction<String, IOException> function = CheckedToDoubleFunction.checked(s -> {
                throw new IllegalStateException(s);
            }, IOException.class);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> function.applyAsDouble("foo"));
            assertEquals("foo", thrown.getMessage());
        }
    }

    @Nested
    class InvokeAndUnwrap {

        @Test
        void testNullArguments() throws IOException {
            ToDoubleFunction<String> function = s -> String.valueOf(s).length();

            assertThrows(NullPointerException.class, () -> CheckedToDoubleFunction.invokeAndUnwrap(null, "foo", IOException.class));
            assertThrows(NullPointerException.class, () -> CheckedToDoubleFunction.invokeAndUnwrap(function, "foo", null));

            assertEquals(4D, CheckedToDoubleFunction.invokeAndUnwrap(function, null, IOException.class));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ToDoubleFunction<String> function = String::length;

            assertEquals(3D, CheckedToDoubleFunction.invokeAndUnwrap(function, "foo", IOException.class));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                ToDoubleFunction<String> function = s -> {
                    throw new UncheckedException(new IOException(s));
                };

                IOException thrown = assertThrows(IOException.class,
                        () -> CheckedToDoubleFunction.invokeAndUnwrap(function, "foo", IOException.class));
                assertEquals("foo", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                ToDoubleFunction<String> function = s -> {
                    throw new UncheckedException(new FileNotFoundException(s));
                };

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class,
                        () -> CheckedToDoubleFunction.invokeAndUnwrap(function, "foo", IOException.class));
                assertEquals("foo", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                ToDoubleFunction<String> function = s -> {
                    throw new UncheckedException(new ParseException(s, 0));
                };

                UncheckedException thrown = assertThrows(UncheckedException.class,
                        () -> CheckedToDoubleFunction.invokeAndUnwrap(function, "foo", IOException.class));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            ToDoubleFunction<String> function = s -> {
                throw new IllegalStateException(s);
            };

            IllegalStateException thrown = assertThrows(IllegalStateException.class,
                    () -> CheckedToDoubleFunction.invokeAndUnwrap(function, "foo", IOException.class));
            assertEquals("foo", thrown.getMessage());
        }
    }
}
