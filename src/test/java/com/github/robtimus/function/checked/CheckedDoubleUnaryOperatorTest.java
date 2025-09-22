/*
 * CheckedDoubleUnaryOperatorTest.java
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
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("nls")
class CheckedDoubleUnaryOperatorTest {

    @Nested
    class OnErrorThrowAsChecked {

        @Test
        void testNullArgument() {
            CheckedDoubleUnaryOperator<IOException> operator = Spied.checkedDoubleUnaryOperator(d -> d + 1);

            assertThrows(NullPointerException.class, () -> operator.onErrorThrowAsChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedDoubleUnaryOperator<IOException> operator = Spied.checkedDoubleUnaryOperator(d -> d + 1);

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedDoubleUnaryOperator<ExecutionException> throwing = operator.onErrorThrowAsChecked(errorMapper);

            assertEquals(2D, throwing.applyAsDouble(1D));

            verify(operator).applyAsDouble(1D);
            verify(operator).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(operator, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedDoubleUnaryOperator<IOException> operator = Spied.checkedDoubleUnaryOperator(d -> {
                throw new IOException(Double.toString(d));
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedDoubleUnaryOperator<ExecutionException> throwing = operator.onErrorThrowAsChecked(errorMapper);

            ExecutionException thrown = assertThrows(ExecutionException.class, () -> throwing.applyAsDouble(1D));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1.0", cause.getMessage());

            verify(operator).applyAsDouble(1D);
            verify(operator).onErrorThrowAsChecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(operator, errorMapper);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedDoubleUnaryOperator<IOException> operator = Spied.checkedDoubleUnaryOperator(d -> {
                throw new IllegalStateException(Double.toString(d));
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedDoubleUnaryOperator<ExecutionException> throwing = operator.onErrorThrowAsChecked(errorMapper);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> throwing.applyAsDouble(1D));
            assertEquals("1.0", thrown.getMessage());

            verify(operator).applyAsDouble(1D);
            verify(operator).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(operator, errorMapper);
        }
    }

    @Nested
    class OnErrorThrowAsUnchecked {

        @Test
        void testNullArgument() {
            CheckedDoubleUnaryOperator<IOException> operator = Spied.checkedDoubleUnaryOperator(d -> d + 1);

            assertThrows(NullPointerException.class, () -> operator.onErrorThrowAsUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedDoubleUnaryOperator<IOException> operator = Spied.checkedDoubleUnaryOperator(d -> d + 1);

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            DoubleUnaryOperator throwing = operator.onErrorThrowAsUnchecked(errorMapper);

            assertEquals(2D, throwing.applyAsDouble(1D));

            verify(operator).applyAsDouble(1D);
            verify(operator).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(operator, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedDoubleUnaryOperator<IOException> operator = Spied.checkedDoubleUnaryOperator(d -> {
                throw new IOException(Double.toString(d));
            });

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            DoubleUnaryOperator throwing = operator.onErrorThrowAsUnchecked(errorMapper);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> throwing.applyAsDouble(1D));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1.0", cause.getMessage());

            verify(operator).applyAsDouble(1D);
            verify(operator).onErrorThrowAsUnchecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(operator, errorMapper);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedDoubleUnaryOperator<IOException> operator = Spied.checkedDoubleUnaryOperator(d -> {
                throw new IllegalArgumentException(Double.toString(d));
            });

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            DoubleUnaryOperator throwing = operator.onErrorThrowAsUnchecked(errorMapper);

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> throwing.applyAsDouble(1D));
            assertEquals("1.0", thrown.getMessage());

            verify(operator).applyAsDouble(1D);
            verify(operator).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(operator, errorMapper);
        }
    }

    @Nested
    class OnErrorHandleChecked {

        @Test
        void testNullArgument() {
            CheckedDoubleUnaryOperator<IOException> operator = Spied.checkedDoubleUnaryOperator(d -> d + 1);

            assertThrows(NullPointerException.class, () -> operator.onErrorHandleChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedDoubleUnaryOperator<IOException> operator = Spied.checkedDoubleUnaryOperator(d -> d + 1);

            CheckedToDoubleFunction<IOException, ExecutionException> errorHandler = Spied.checkedToDoubleFunction(e -> 0D);

            CheckedDoubleUnaryOperator<ExecutionException> handling = operator.onErrorHandleChecked(errorHandler);

            assertEquals(2D, handling.applyAsDouble(1D));

            verify(operator).applyAsDouble(1D);
            verify(operator).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(operator, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException, ExecutionException {
                CheckedDoubleUnaryOperator<IOException> operator = Spied.checkedDoubleUnaryOperator(d -> {
                    throw new IOException(Double.toString(d));
                });

                CheckedToDoubleFunction<IOException, ExecutionException> errorHandler = Spied.checkedToDoubleFunction(e -> 0D);

                CheckedDoubleUnaryOperator<ExecutionException> handling = operator.onErrorHandleChecked(errorHandler);

                assertEquals(0D, handling.applyAsDouble(1D));

                verify(operator).applyAsDouble(1D);
                verify(operator).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsDouble(any());
                verifyNoMoreInteractions(operator, errorHandler);
            }

            @Test
            void testHandlerThrowsChecked() throws IOException, ExecutionException {
                CheckedDoubleUnaryOperator<IOException> operator = Spied.checkedDoubleUnaryOperator(d -> {
                    throw new IOException(Double.toString(d));
                });

                CheckedToDoubleFunction<IOException, ExecutionException> errorHandler = Spied.checkedToDoubleFunction(e -> {
                    throw new ExecutionException(e);
                });

                CheckedDoubleUnaryOperator<ExecutionException> handling = operator.onErrorHandleChecked(errorHandler);

                ExecutionException thrown = assertThrows(ExecutionException.class, () -> handling.applyAsDouble(1D));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("1.0", cause.getMessage());

                verify(operator).applyAsDouble(1D);
                verify(operator).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsDouble(any());
                verifyNoMoreInteractions(operator, errorHandler);
            }

            @Test
            void testHandlerThrowsUnchecked() throws IOException, ExecutionException {
                CheckedDoubleUnaryOperator<IOException> operator = Spied.checkedDoubleUnaryOperator(d -> {
                    throw new IOException(Double.toString(d));
                });

                CheckedToDoubleFunction<IOException, ExecutionException> errorHandler = Spied.checkedToDoubleFunction(e -> {
                    throw new IllegalStateException(e);
                });

                CheckedDoubleUnaryOperator<ExecutionException> handling = operator.onErrorHandleChecked(errorHandler);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.applyAsDouble(1D));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("1.0", cause.getMessage());

                verify(operator).applyAsDouble(1D);
                verify(operator).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsDouble(any());
                verifyNoMoreInteractions(operator, errorHandler);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedDoubleUnaryOperator<IOException> operator = Spied.checkedDoubleUnaryOperator(d -> {
                throw new IllegalStateException(Double.toString(d));
            });

            CheckedToDoubleFunction<IOException, ExecutionException> errorHandler = Spied.checkedToDoubleFunction(e -> 0D);

            CheckedDoubleUnaryOperator<ExecutionException> handling = operator.onErrorHandleChecked(errorHandler);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.applyAsDouble(1D));
            assertEquals("1.0", thrown.getMessage());

            verify(operator).applyAsDouble(1D);
            verify(operator).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(operator, errorHandler);
        }
    }

    @Nested
    class OnErrorHandleUnchecked {

        @Test
        void testNullArgument() {
            CheckedDoubleUnaryOperator<IOException> operator = Spied.checkedDoubleUnaryOperator(d -> d + 1);

            assertThrows(NullPointerException.class, () -> operator.onErrorHandleUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedDoubleUnaryOperator<IOException> operator = Spied.checkedDoubleUnaryOperator(d -> d + 1);

            ToDoubleFunction<IOException> errorHandler = Spied.toDoubleFunction(e -> 0D);

            DoubleUnaryOperator handling = operator.onErrorHandleUnchecked(errorHandler);

            assertEquals(2D, handling.applyAsDouble(1D));

            verify(operator).applyAsDouble(1D);
            verify(operator).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(operator, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException {
                CheckedDoubleUnaryOperator<IOException> operator = Spied.checkedDoubleUnaryOperator(d -> {
                    throw new IOException(Double.toString(d));
                });

                ToDoubleFunction<IOException> errorHandler = Spied.toDoubleFunction(e -> 0D);

                DoubleUnaryOperator handling = operator.onErrorHandleUnchecked(errorHandler);

                assertEquals(0D, handling.applyAsDouble(1D));

                verify(operator).applyAsDouble(1D);
                verify(operator).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).applyAsDouble(any());
                verifyNoMoreInteractions(operator, errorHandler);
            }

            @Test
            void testHandlerThrowsUnchecked() throws IOException {
                CheckedDoubleUnaryOperator<IOException> operator = Spied.checkedDoubleUnaryOperator(d -> {
                    throw new IOException(Double.toString(d));
                });

                ToDoubleFunction<IOException> errorHandler = Spied.toDoubleFunction(e -> {
                    throw new IllegalStateException(e);
                });

                DoubleUnaryOperator handling = operator.onErrorHandleUnchecked(errorHandler);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.applyAsDouble(1D));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("1.0", cause.getMessage());

                verify(operator).applyAsDouble(1D);
                verify(operator).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).applyAsDouble(any());
                verifyNoMoreInteractions(operator, errorHandler);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedDoubleUnaryOperator<IOException> operator = Spied.checkedDoubleUnaryOperator(d -> {
                throw new IllegalStateException(Double.toString(d));
            });

            ToDoubleFunction<IOException> errorHandler = Spied.toDoubleFunction(e -> 0D);

            DoubleUnaryOperator handling = operator.onErrorHandleUnchecked(errorHandler);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.applyAsDouble(1D));
            assertEquals("1.0", thrown.getMessage());

            verify(operator).applyAsDouble(1D);
            verify(operator).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(operator, errorHandler);
        }
    }

    @Nested
    class OnErrorApplyChecked {

        @Test
        void testNullArgument() {
            CheckedDoubleUnaryOperator<IOException> operator = Spied.checkedDoubleUnaryOperator(d -> d + 1);

            assertThrows(NullPointerException.class, () -> operator.onErrorApplyChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedDoubleUnaryOperator<IOException> operator = Spied.checkedDoubleUnaryOperator(d -> d + 1);

            CheckedDoubleUnaryOperator<ExecutionException> fallback = Spied.checkedDoubleUnaryOperator(d -> d + d);

            CheckedDoubleUnaryOperator<ExecutionException> applying = operator.onErrorApplyChecked(fallback);

            assertEquals(2D, applying.applyAsDouble(1D));

            verify(operator).applyAsDouble(1D);
            verify(operator).onErrorApplyChecked(fallback);
            verifyNoMoreInteractions(operator, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                CheckedDoubleUnaryOperator<IOException> operator = Spied.checkedDoubleUnaryOperator(d -> {
                    throw new IOException(Double.toString(d));
                });

                CheckedDoubleUnaryOperator<ParseException> fallback = Spied.checkedDoubleUnaryOperator(d -> d + d);

                CheckedDoubleUnaryOperator<ParseException> applying = operator.onErrorApplyChecked(fallback);

                assertEquals(2D, applying.applyAsDouble(1D));

                verify(operator).applyAsDouble(1D);
                verify(operator).onErrorApplyChecked(fallback);
                verify(fallback).applyAsDouble(1D);
                verifyNoMoreInteractions(operator, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                CheckedDoubleUnaryOperator<IOException> operator = Spied.checkedDoubleUnaryOperator(d -> {
                    throw new IOException(Double.toString(d));
                });

                CheckedDoubleUnaryOperator<ParseException> fallback = Spied.checkedDoubleUnaryOperator(d -> {
                    throw new ParseException(Double.toString(d), 0);
                });

                CheckedDoubleUnaryOperator<ParseException> applying = operator.onErrorApplyChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> applying.applyAsDouble(1D));
                assertEquals("1.0", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(operator).applyAsDouble(1D);
                verify(operator).onErrorApplyChecked(fallback);
                verify(fallback).applyAsDouble(1D);
                verifyNoMoreInteractions(operator, fallback);
            }

            @Test
            void testFallbackThrowsUnchecked() throws IOException, ParseException {
                CheckedDoubleUnaryOperator<IOException> operator = Spied.checkedDoubleUnaryOperator(d -> {
                    throw new IOException(Double.toString(d));
                });

                CheckedDoubleUnaryOperator<ParseException> fallback = Spied.checkedDoubleUnaryOperator(d -> {
                    throw new IllegalStateException(Double.toString(d));
                });

                CheckedDoubleUnaryOperator<ParseException> applying = operator.onErrorApplyChecked(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> applying.applyAsDouble(1D));
                assertEquals("1.0", thrown.getMessage());

                verify(operator).applyAsDouble(1D);
                verify(operator).onErrorApplyChecked(fallback);
                verify(fallback).applyAsDouble(1D);
                verifyNoMoreInteractions(operator, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedDoubleUnaryOperator<IOException> operator = Spied.checkedDoubleUnaryOperator(d -> {
                throw new IllegalStateException(Double.toString(d));
            });

            CheckedDoubleUnaryOperator<ParseException> fallback = Spied.checkedDoubleUnaryOperator(d -> d + d);

            CheckedDoubleUnaryOperator<ParseException> applying = operator.onErrorApplyChecked(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> applying.applyAsDouble(1D));
            assertEquals("1.0", thrown.getMessage());

            verify(operator).applyAsDouble(1D);
            verify(operator).onErrorApplyChecked(fallback);
            verifyNoMoreInteractions(operator, fallback);
        }
    }

    @Nested
    class OnErrorApplyUnchecked {

        @Test
        void testNullArgument() {
            CheckedDoubleUnaryOperator<IOException> operator = Spied.checkedDoubleUnaryOperator(d -> d + 1);

            assertThrows(NullPointerException.class, () -> operator.onErrorApplyUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedDoubleUnaryOperator<IOException> operator = Spied.checkedDoubleUnaryOperator(d -> d + 1);

            DoubleUnaryOperator fallback = Spied.doubleUnaryOperator(d -> d + d);

            DoubleUnaryOperator applying = operator.onErrorApplyUnchecked(fallback);

            assertEquals(2D, applying.applyAsDouble(1D));

            verify(operator).applyAsDouble(1D);
            verify(operator).onErrorApplyUnchecked(fallback);
            verifyNoMoreInteractions(operator, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                CheckedDoubleUnaryOperator<IOException> operator = Spied.checkedDoubleUnaryOperator(d -> {
                    throw new IOException(Double.toString(d));
                });

                DoubleUnaryOperator fallback = Spied.doubleUnaryOperator(d -> d + d);

                DoubleUnaryOperator applying = operator.onErrorApplyUnchecked(fallback);

                assertEquals(2D, applying.applyAsDouble(1D));

                verify(operator).applyAsDouble(1D);
                verify(operator).onErrorApplyUnchecked(fallback);
                verify(fallback).applyAsDouble(1D);
                verifyNoMoreInteractions(operator, fallback);
            }

            @Test
            void testFallbackThrowsUnchecked() throws IOException {
                CheckedDoubleUnaryOperator<IOException> operator = Spied.checkedDoubleUnaryOperator(d -> {
                    throw new IOException(Double.toString(d));
                });

                DoubleUnaryOperator fallback = Spied.doubleUnaryOperator(d -> {
                    throw new IllegalStateException(Double.toString(d));
                });

                DoubleUnaryOperator applying = operator.onErrorApplyUnchecked(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> applying.applyAsDouble(1D));
                assertEquals("1.0", thrown.getMessage());

                verify(operator).applyAsDouble(1D);
                verify(operator).onErrorApplyUnchecked(fallback);
                verify(fallback).applyAsDouble(1D);
                verifyNoMoreInteractions(operator, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedDoubleUnaryOperator<IOException> operator = Spied.checkedDoubleUnaryOperator(d -> {
                throw new IllegalStateException(Double.toString(d));
            });

            DoubleUnaryOperator fallback = Spied.doubleUnaryOperator(d -> d + d);

            DoubleUnaryOperator applying = operator.onErrorApplyUnchecked(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> applying.applyAsDouble(1D));
            assertEquals("1.0", thrown.getMessage());

            verify(operator).applyAsDouble(1D);
            verify(operator).onErrorApplyUnchecked(fallback);
            verifyNoMoreInteractions(operator, fallback);
        }
    }

    @Nested
    class OnErrorGetChecked {

        @Test
        void testNullArgument() {
            CheckedDoubleUnaryOperator<IOException> operator = Spied.checkedDoubleUnaryOperator(d -> d + 1);

            assertThrows(NullPointerException.class, () -> operator.onErrorGetChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedDoubleUnaryOperator<IOException> operator = Spied.checkedDoubleUnaryOperator(d -> d + 1);

            CheckedDoubleSupplier<ExecutionException> fallback = Spied.checkedDoubleSupplier(() -> 0D);

            CheckedDoubleUnaryOperator<ExecutionException> getting = operator.onErrorGetChecked(fallback);

            assertEquals(2D, getting.applyAsDouble(1D));

            verify(operator).applyAsDouble(1D);
            verify(operator).onErrorGetChecked(fallback);
            verifyNoMoreInteractions(operator, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                CheckedDoubleUnaryOperator<IOException> operator = Spied.checkedDoubleUnaryOperator(d -> {
                    throw new IOException(Double.toString(d));
                });

                CheckedDoubleSupplier<ParseException> fallback = Spied.checkedDoubleSupplier(() -> 0D);

                CheckedDoubleUnaryOperator<ParseException> getting = operator.onErrorGetChecked(fallback);

                assertEquals(0D, getting.applyAsDouble(1D));

                verify(operator).applyAsDouble(1D);
                verify(operator).onErrorGetChecked(fallback);
                verify(fallback).getAsDouble();
                verifyNoMoreInteractions(operator, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                CheckedDoubleUnaryOperator<IOException> operator = Spied.checkedDoubleUnaryOperator(d -> {
                    throw new IOException(Double.toString(d));
                });

                CheckedDoubleSupplier<ParseException> fallback = Spied.checkedDoubleSupplier(() -> {
                    throw new ParseException("bar", 0);
                });

                CheckedDoubleUnaryOperator<ParseException> getting = operator.onErrorGetChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> getting.applyAsDouble(1D));
                assertEquals("bar", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(operator).applyAsDouble(1D);
                verify(operator).onErrorGetChecked(fallback);
                verify(fallback).getAsDouble();
                verifyNoMoreInteractions(operator, fallback);
            }

            @Test
            void testFallbackThrowsUnchecked() throws IOException, ParseException {
                CheckedDoubleUnaryOperator<IOException> operator = Spied.checkedDoubleUnaryOperator(d -> {
                    throw new IOException(Double.toString(d));
                });

                CheckedDoubleSupplier<ParseException> fallback = Spied.checkedDoubleSupplier(() -> {
                    throw new IllegalStateException("bar");
                });

                CheckedDoubleUnaryOperator<ParseException> getting = operator.onErrorGetChecked(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> getting.applyAsDouble(1D));
                assertEquals("bar", thrown.getMessage());

                verify(operator).applyAsDouble(1D);
                verify(operator).onErrorGetChecked(fallback);
                verify(fallback).getAsDouble();
                verifyNoMoreInteractions(operator, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedDoubleUnaryOperator<IOException> operator = Spied.checkedDoubleUnaryOperator(d -> {
                throw new IllegalStateException(Double.toString(d));
            });

            CheckedDoubleSupplier<ParseException> fallback = Spied.checkedDoubleSupplier(() -> 0D);

            CheckedDoubleUnaryOperator<ParseException> getting = operator.onErrorGetChecked(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> getting.applyAsDouble(1D));
            assertEquals("1.0", thrown.getMessage());

            verify(operator).applyAsDouble(1D);
            verify(operator).onErrorGetChecked(fallback);
            verifyNoMoreInteractions(operator, fallback);
        }
    }

    @Nested
    class OnErrorGetUnchecked {

        @Test
        void testNullArgument() {
            CheckedDoubleUnaryOperator<IOException> operator = Spied.checkedDoubleUnaryOperator(d -> d + 1);

            assertThrows(NullPointerException.class, () -> operator.onErrorGetUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedDoubleUnaryOperator<IOException> operator = Spied.checkedDoubleUnaryOperator(d -> d + 1);

            DoubleSupplier fallback = Spied.doubleSupplier(() -> 2D);

            DoubleUnaryOperator getting = operator.onErrorGetUnchecked(fallback);

            assertEquals(2D, getting.applyAsDouble(1D));

            verify(operator).applyAsDouble(1D);
            verify(operator).onErrorGetUnchecked(fallback);
            verifyNoMoreInteractions(operator, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                CheckedDoubleUnaryOperator<IOException> operator = Spied.checkedDoubleUnaryOperator(d -> {
                    throw new IOException(Double.toString(d));
                });

                DoubleSupplier fallback = Spied.doubleSupplier(() -> 2D);

                DoubleUnaryOperator getting = operator.onErrorGetUnchecked(fallback);

                assertEquals(2D, getting.applyAsDouble(1D));

                verify(operator).applyAsDouble(1D);
                verify(operator).onErrorGetUnchecked(fallback);
                verify(fallback).getAsDouble();
                verifyNoMoreInteractions(operator, fallback);
            }

            @Test
            void testFallbackThrowsUnchecked() throws IOException {
                CheckedDoubleUnaryOperator<IOException> operator = Spied.checkedDoubleUnaryOperator(d -> {
                    throw new IOException(Double.toString(d));
                });

                DoubleSupplier fallback = Spied.doubleSupplier(() -> {
                    throw new IllegalStateException("bar");
                });

                DoubleUnaryOperator getting = operator.onErrorGetUnchecked(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> getting.applyAsDouble(1D));
                assertEquals("bar", thrown.getMessage());

                verify(operator).applyAsDouble(1D);
                verify(operator).onErrorGetUnchecked(fallback);
                verify(fallback).getAsDouble();
                verifyNoMoreInteractions(operator, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedDoubleUnaryOperator<IOException> operator = Spied.checkedDoubleUnaryOperator(d -> {
                throw new IllegalStateException(Double.toString(d));
            });

            DoubleSupplier fallback = Spied.doubleSupplier(() -> 2D);

            DoubleUnaryOperator getting = operator.onErrorGetUnchecked(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> getting.applyAsDouble(1D));
            assertEquals("1.0", thrown.getMessage());

            verify(operator).applyAsDouble(1D);
            verify(operator).onErrorGetUnchecked(fallback);
            verifyNoMoreInteractions(operator, fallback);
        }
    }

    @Nested
    class OnErrorReturn {

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedDoubleUnaryOperator<IOException> operator = Spied.checkedDoubleUnaryOperator(d -> d + 1);

            DoubleUnaryOperator returning = operator.onErrorReturn(2D);

            assertEquals(2D, returning.applyAsDouble(1D));

            verify(operator).applyAsDouble(1D);
            verify(operator).onErrorReturn(2D);
            verifyNoMoreInteractions(operator);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedDoubleUnaryOperator<IOException> operator = Spied.checkedDoubleUnaryOperator(d -> {
                throw new IOException(Double.toString(d));
            });

            DoubleUnaryOperator returning = operator.onErrorReturn(2D);

            assertEquals(2D, returning.applyAsDouble(1D));

            verify(operator).applyAsDouble(1D);
            verify(operator).onErrorReturn(2D);
            verifyNoMoreInteractions(operator);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedDoubleUnaryOperator<IOException> operator = Spied.checkedDoubleUnaryOperator(d -> {
                throw new IllegalStateException(Double.toString(d));
            });

            DoubleUnaryOperator returning = operator.onErrorReturn(2D);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> returning.applyAsDouble(1D));
            assertEquals("1.0", thrown.getMessage());

            verify(operator).applyAsDouble(1D);
            verify(operator).onErrorReturn(2D);
            verifyNoMoreInteractions(operator);
        }
    }

    @Nested
    class Unchecked {

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedDoubleUnaryOperator<IOException> operator = Spied.checkedDoubleUnaryOperator(d -> d + 1);

            DoubleUnaryOperator unchecked = operator.unchecked();

            assertEquals(2D, unchecked.applyAsDouble(1D));

            verify(operator).applyAsDouble(1D);
            verify(operator).unchecked();
            verify(operator).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(operator);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedDoubleUnaryOperator<IOException> operator = Spied.checkedDoubleUnaryOperator(d -> {
                throw new IOException(Double.toString(d));
            });

            DoubleUnaryOperator unchecked = operator.unchecked();

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.applyAsDouble(1D));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1.0", cause.getMessage());

            verify(operator).applyAsDouble(1D);
            verify(operator).unchecked();
            verify(operator).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(operator);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedDoubleUnaryOperator<IOException> operator = Spied.checkedDoubleUnaryOperator(d -> {
                throw new IllegalArgumentException(Double.toString(d));
            });

            DoubleUnaryOperator unchecked = operator.unchecked();

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> unchecked.applyAsDouble(1D));
            assertEquals("1.0", thrown.getMessage());

            verify(operator).applyAsDouble(1D);
            verify(operator).unchecked();
            verify(operator).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(operator);
        }
    }

    @Nested
    class Of {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> CheckedFunction.of(null));
        }

        @Test
        void testNonNullArgument() throws IOException {
            CheckedDoubleUnaryOperator<IOException> operator = CheckedDoubleUnaryOperator.of(d -> d + 1);

            assertEquals(2D, operator.applyAsDouble(1D));
        }
    }

    @Test
    void testIdentity() throws IOException {
        CheckedDoubleUnaryOperator<IOException> operator = CheckedDoubleUnaryOperator.identity();

        assertEquals(1D, operator.applyAsDouble(1D));
    }

    @Nested
    class UncheckedFactory {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> CheckedFunction.unchecked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedDoubleUnaryOperator<IOException> operator = Spied.checkedDoubleUnaryOperator(d -> d + 1);

            DoubleUnaryOperator unchecked = CheckedDoubleUnaryOperator.unchecked(operator);

            assertEquals(2D, unchecked.applyAsDouble(1D));

            verify(operator).applyAsDouble(1D);
            verifyNoMoreInteractions(operator);
        }

        @Test
        void testArgumentThrowsChecked() throws IOException {
            CheckedDoubleUnaryOperator<IOException> operator = Spied.checkedDoubleUnaryOperator(d -> {
                throw new IOException(Double.toString(d));
            });

            DoubleUnaryOperator unchecked = CheckedDoubleUnaryOperator.unchecked(operator);

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.applyAsDouble(1D));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1.0", cause.getMessage());

            verify(operator).applyAsDouble(1D);
            verifyNoMoreInteractions(operator);
        }

        @Test
        void testArgumentThrowsUnchecked() throws IOException {
            CheckedDoubleUnaryOperator<IOException> operator = Spied.checkedDoubleUnaryOperator(d -> {
                throw new IllegalArgumentException(Double.toString(d));
            });

            DoubleUnaryOperator unchecked = CheckedDoubleUnaryOperator.unchecked(operator);

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> unchecked.applyAsDouble(1D));
            assertEquals("1.0", thrown.getMessage());

            verify(operator).applyAsDouble(1D);
            verifyNoMoreInteractions(operator);
        }
    }

    @Nested
    class CheckedWrapper {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> CheckedFunction.checked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedDoubleUnaryOperator<IOException> operator = CheckedDoubleUnaryOperator.checked(d -> d + 1);

            assertEquals(2D, operator.applyAsDouble(1D));
        }

        @Test
        void testargumentThrowsUnchecked() {
            CheckedDoubleUnaryOperator<IOException> operator = CheckedDoubleUnaryOperator.checked(d -> {
                throw new UncheckedException(Double.toString(d), new IOException());
            });

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> operator.applyAsDouble(1D));
            assertEquals("1.0", thrown.getMessage());
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertNull(cause.getMessage());
        }
    }

    @Nested
    class CheckedUnwrapper {

        @Test
        void testNullArguments() {
            DoubleUnaryOperator operator = d -> d + 1;

            assertThrows(NullPointerException.class, () -> CheckedDoubleUnaryOperator.checked(null, IOException.class));
            assertThrows(NullPointerException.class, () -> CheckedDoubleUnaryOperator.checked(operator, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedDoubleUnaryOperator<IOException> operator = CheckedDoubleUnaryOperator.checked(d -> d + 1, IOException.class);

            assertEquals(2D, operator.applyAsDouble(1D));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                CheckedDoubleUnaryOperator<IOException> operator = CheckedDoubleUnaryOperator.checked(d -> {
                    throw new UncheckedException(new IOException(Double.toString(d)));
                }, IOException.class);

                IOException thrown = assertThrows(IOException.class, () -> operator.applyAsDouble(1D));
                assertEquals("1.0", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                CheckedDoubleUnaryOperator<IOException> operator = CheckedDoubleUnaryOperator.checked(d -> {
                    throw new UncheckedException(new FileNotFoundException(Double.toString(d)));
                }, IOException.class);

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class, () -> operator.applyAsDouble(1D));
                assertEquals("1.0", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                CheckedDoubleUnaryOperator<IOException> operator = CheckedDoubleUnaryOperator.checked(d -> {
                    throw new UncheckedException(new ParseException(Double.toString(d), 0));
                }, IOException.class);

                UncheckedException thrown = assertThrows(UncheckedException.class, () -> operator.applyAsDouble(1D));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("1.0", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            CheckedDoubleUnaryOperator<IOException> operator = CheckedDoubleUnaryOperator.checked(d -> {
                throw new IllegalStateException(Double.toString(d));
            }, IOException.class);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> operator.applyAsDouble(1D));
            assertEquals("1.0", thrown.getMessage());
        }
    }

    @Nested
    class InvokeAndUnwrap {

        @Test
        void testNullArguments() {
            DoubleUnaryOperator operator = d -> d + 1;

            assertThrows(NullPointerException.class, () -> CheckedDoubleUnaryOperator.invokeAndUnwrap(null, 1D, IOException.class));
            assertThrows(NullPointerException.class, () -> CheckedDoubleUnaryOperator.invokeAndUnwrap(operator, 1D, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            DoubleUnaryOperator operator = d -> d + 1;

            assertEquals(2D, CheckedDoubleUnaryOperator.invokeAndUnwrap(operator, 1D, IOException.class));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                DoubleUnaryOperator operator = d -> {
                    throw new UncheckedException(new IOException(Double.toString(d)));
                };

                IOException thrown = assertThrows(IOException.class,
                        () -> CheckedDoubleUnaryOperator.invokeAndUnwrap(operator, 1D, IOException.class));
                assertEquals("1.0", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                DoubleUnaryOperator operator = d -> {
                    throw new UncheckedException(new FileNotFoundException(Double.toString(d)));
                };

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class,
                        () -> CheckedDoubleUnaryOperator.invokeAndUnwrap(operator, 1D, IOException.class));
                assertEquals("1.0", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                DoubleUnaryOperator operator = d -> {
                    throw new UncheckedException(new ParseException(Double.toString(d), 0));
                };

                UncheckedException thrown = assertThrows(UncheckedException.class,
                        () -> CheckedDoubleUnaryOperator.invokeAndUnwrap(operator, 1D, IOException.class));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("1.0", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            DoubleUnaryOperator operator = d -> {
                throw new IllegalStateException(Double.toString(d));
            };

            IllegalStateException thrown = assertThrows(IllegalStateException.class,
                    () -> CheckedDoubleUnaryOperator.invokeAndUnwrap(operator, 1D, IOException.class));
            assertEquals("1.0", thrown.getMessage());
        }
    }
}
