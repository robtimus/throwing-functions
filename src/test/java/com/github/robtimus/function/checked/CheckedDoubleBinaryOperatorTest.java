/*
 * CheckedDoubleBinaryOperatorTest.java
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
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleSupplier;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("nls")
class CheckedDoubleBinaryOperatorTest {

    @Nested
    class OnErrorThrowAsChecked {

        @Test
        void testNullArgument() {
            CheckedDoubleBinaryOperator<IOException> operator = Spied.checkedDoubleBinaryOperator(Double::max);

            assertThrows(NullPointerException.class, () -> operator.onErrorThrowAsChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedDoubleBinaryOperator<IOException> operator = Spied.checkedDoubleBinaryOperator(Double::max);

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedDoubleBinaryOperator<ExecutionException> throwing = operator.onErrorThrowAsChecked(errorMapper);

            assertEquals(2D, throwing.applyAsDouble(1D, 2D));

            verify(operator).applyAsDouble(1D, 2D);
            verify(operator).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(operator, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedDoubleBinaryOperator<IOException> operator = Spied.checkedDoubleBinaryOperator((d1, d2) -> {
                throw new IOException(Double.toString(d1 + d2));
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedDoubleBinaryOperator<ExecutionException> throwing = operator.onErrorThrowAsChecked(errorMapper);

            ExecutionException thrown = assertThrows(ExecutionException.class, () -> throwing.applyAsDouble(1D, 2D));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("3.0", cause.getMessage());

            verify(operator).applyAsDouble(1D, 2D);
            verify(operator).onErrorThrowAsChecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(operator, errorMapper);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedDoubleBinaryOperator<IOException> operator = Spied.checkedDoubleBinaryOperator((d1, d2) -> {
                throw new IllegalStateException(Double.toString(d1 + d2));
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedDoubleBinaryOperator<ExecutionException> throwing = operator.onErrorThrowAsChecked(errorMapper);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> throwing.applyAsDouble(1D, 2D));
            assertEquals("3.0", thrown.getMessage());

            verify(operator).applyAsDouble(1D, 2D);
            verify(operator).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(operator, errorMapper);
        }
    }

    @Nested
    class OnErrorThrowAsUnchecked {

        @Test
        void testNullArgument() {
            CheckedDoubleBinaryOperator<IOException> operator = Spied.checkedDoubleBinaryOperator(Double::max);

            assertThrows(NullPointerException.class, () -> operator.onErrorThrowAsUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedDoubleBinaryOperator<IOException> operator = Spied.checkedDoubleBinaryOperator(Double::max);

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            DoubleBinaryOperator throwing = operator.onErrorThrowAsUnchecked(errorMapper);

            assertEquals(2D, throwing.applyAsDouble(1D, 2D));

            verify(operator).applyAsDouble(1D, 2D);
            verify(operator).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(operator, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedDoubleBinaryOperator<IOException> operator = Spied.checkedDoubleBinaryOperator((d1, d2) -> {
                throw new IOException(Double.toString(d1 + d2));
            });

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            DoubleBinaryOperator throwing = operator.onErrorThrowAsUnchecked(errorMapper);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> throwing.applyAsDouble(1D, 2D));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("3.0", cause.getMessage());

            verify(operator).applyAsDouble(1D, 2D);
            verify(operator).onErrorThrowAsUnchecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(operator, errorMapper);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedDoubleBinaryOperator<IOException> operator = Spied.checkedDoubleBinaryOperator((d1, d2) -> {
                throw new IllegalArgumentException(Double.toString(d1 + d2));
            });

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            DoubleBinaryOperator throwing = operator.onErrorThrowAsUnchecked(errorMapper);

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> throwing.applyAsDouble(1D, 2D));
            assertEquals("3.0", thrown.getMessage());

            verify(operator).applyAsDouble(1D, 2D);
            verify(operator).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(operator, errorMapper);
        }
    }

    @Nested
    class OnErrorHandleChecked {

        @Test
        void testNullArgument() {
            CheckedDoubleBinaryOperator<IOException> operator = Spied.checkedDoubleBinaryOperator(Double::max);

            assertThrows(NullPointerException.class, () -> operator.onErrorHandleChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedDoubleBinaryOperator<IOException> operator = Spied.checkedDoubleBinaryOperator(Double::max);

            CheckedToDoubleFunction<IOException, ExecutionException> errorHandler = Spied.checkedToDoubleFunction(e -> 0D);

            CheckedDoubleBinaryOperator<ExecutionException> handling = operator.onErrorHandleChecked(errorHandler);

            assertEquals(2D, handling.applyAsDouble(1D, 2D));

            verify(operator).applyAsDouble(1D, 2D);
            verify(operator).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(operator, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException, ExecutionException {
                CheckedDoubleBinaryOperator<IOException> operator = Spied.checkedDoubleBinaryOperator((d1, d2) -> {
                    throw new IOException(Double.toString(d1 + d2));
                });

                CheckedToDoubleFunction<IOException, ExecutionException> errorHandler = Spied.checkedToDoubleFunction(e -> 0D);

                CheckedDoubleBinaryOperator<ExecutionException> handling = operator.onErrorHandleChecked(errorHandler);

                assertEquals(0D, handling.applyAsDouble(1D, 2D));

                verify(operator).applyAsDouble(1D, 2D);
                verify(operator).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsDouble(any());
                verifyNoMoreInteractions(operator, errorHandler);
            }

            @Test
            void testHandlerThrowsChecked() throws IOException, ExecutionException {
                CheckedDoubleBinaryOperator<IOException> operator = Spied.checkedDoubleBinaryOperator((d1, d2) -> {
                    throw new IOException(Double.toString(d1 + d2));
                });

                CheckedToDoubleFunction<IOException, ExecutionException> errorHandler = Spied.checkedToDoubleFunction(e -> {
                    throw new ExecutionException(e);
                });

                CheckedDoubleBinaryOperator<ExecutionException> handling = operator.onErrorHandleChecked(errorHandler);

                ExecutionException thrown = assertThrows(ExecutionException.class, () -> handling.applyAsDouble(1D, 2D));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("3.0", cause.getMessage());

                verify(operator).applyAsDouble(1D, 2D);
                verify(operator).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsDouble(any());
                verifyNoMoreInteractions(operator, errorHandler);
            }

            @Test
            void testHandlerThrowsUnchecked() throws IOException, ExecutionException {
                CheckedDoubleBinaryOperator<IOException> operator = Spied.checkedDoubleBinaryOperator((d1, d2) -> {
                    throw new IOException(Double.toString(d1 + d2));
                });

                CheckedToDoubleFunction<IOException, ExecutionException> errorHandler = Spied.checkedToDoubleFunction(e -> {
                    throw new IllegalStateException(e);
                });

                CheckedDoubleBinaryOperator<ExecutionException> handling = operator.onErrorHandleChecked(errorHandler);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.applyAsDouble(1D, 2D));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("3.0", cause.getMessage());

                verify(operator).applyAsDouble(1D, 2D);
                verify(operator).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsDouble(any());
                verifyNoMoreInteractions(operator, errorHandler);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedDoubleBinaryOperator<IOException> operator = Spied.checkedDoubleBinaryOperator((d1, d2) -> {
                throw new IllegalStateException(Double.toString(d1 + d2));
            });

            CheckedToDoubleFunction<IOException, ExecutionException> errorHandler = Spied.checkedToDoubleFunction(e -> 0D);

            CheckedDoubleBinaryOperator<ExecutionException> handling = operator.onErrorHandleChecked(errorHandler);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.applyAsDouble(1D, 2D));
            assertEquals("3.0", thrown.getMessage());

            verify(operator).applyAsDouble(1D, 2D);
            verify(operator).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(operator, errorHandler);
        }
    }

    @Nested
    class OnErrorHandleUnchecked {

        @Test
        void testNullArgument() {
            CheckedDoubleBinaryOperator<IOException> operator = Spied.checkedDoubleBinaryOperator(Double::max);

            assertThrows(NullPointerException.class, () -> operator.onErrorHandleUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedDoubleBinaryOperator<IOException> operator = Spied.checkedDoubleBinaryOperator(Double::max);

            ToDoubleFunction<IOException> errorHandler = Spied.toDoubleFunction(e -> 0D);

            DoubleBinaryOperator handling = operator.onErrorHandleUnchecked(errorHandler);

            assertEquals(2D, handling.applyAsDouble(1D, 2D));

            verify(operator).applyAsDouble(1D, 2D);
            verify(operator).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(operator, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException {
                CheckedDoubleBinaryOperator<IOException> operator = Spied.checkedDoubleBinaryOperator((d1, d2) -> {
                    throw new IOException(Double.toString(d1 + d2));
                });

                ToDoubleFunction<IOException> errorHandler = Spied.toDoubleFunction(e -> 0D);

                DoubleBinaryOperator handling = operator.onErrorHandleUnchecked(errorHandler);

                assertEquals(0D, handling.applyAsDouble(1D, 2D));

                verify(operator).applyAsDouble(1D, 2D);
                verify(operator).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).applyAsDouble(any());
                verifyNoMoreInteractions(operator, errorHandler);
            }

            @Test
            void testHandlerThrowsUnchecked() throws IOException {
                CheckedDoubleBinaryOperator<IOException> operator = Spied.checkedDoubleBinaryOperator((d1, d2) -> {
                    throw new IOException(Double.toString(d1 + d2));
                });

                ToDoubleFunction<IOException> errorHandler = Spied.toDoubleFunction(e -> {
                    throw new IllegalStateException(e);
                });

                DoubleBinaryOperator handling = operator.onErrorHandleUnchecked(errorHandler);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.applyAsDouble(1D, 2D));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("3.0", cause.getMessage());

                verify(operator).applyAsDouble(1D, 2D);
                verify(operator).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).applyAsDouble(any());
                verifyNoMoreInteractions(operator, errorHandler);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedDoubleBinaryOperator<IOException> operator = Spied.checkedDoubleBinaryOperator((d1, d2) -> {
                throw new IllegalStateException(Double.toString(d1 + d2));
            });

            ToDoubleFunction<IOException> errorHandler = Spied.toDoubleFunction(e -> 0D);

            DoubleBinaryOperator handling = operator.onErrorHandleUnchecked(errorHandler);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.applyAsDouble(1D, 2D));
            assertEquals("3.0", thrown.getMessage());

            verify(operator).applyAsDouble(1D, 2D);
            verify(operator).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(operator, errorHandler);
        }
    }

    @Nested
    class OnErrorApplyChecked {

        @Test
        void testNullArgument() {
            CheckedDoubleBinaryOperator<IOException> operator = Spied.checkedDoubleBinaryOperator(Double::max);

            assertThrows(NullPointerException.class, () -> operator.onErrorApplyChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedDoubleBinaryOperator<IOException> operator = Spied.checkedDoubleBinaryOperator(Double::max);

            CheckedDoubleBinaryOperator<ExecutionException> fallback = Spied.checkedDoubleBinaryOperator((d1, d2) -> d1 + d2 + d1 + d2);

            CheckedDoubleBinaryOperator<ExecutionException> applying = operator.onErrorApplyChecked(fallback);

            assertEquals(2D, applying.applyAsDouble(1D, 2D));

            verify(operator).applyAsDouble(1D, 2D);
            verify(operator).onErrorApplyChecked(fallback);
            verifyNoMoreInteractions(operator, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                CheckedDoubleBinaryOperator<IOException> operator = Spied.checkedDoubleBinaryOperator((d1, d2) -> {
                    throw new IOException(Double.toString(d1 + d2));
                });

                CheckedDoubleBinaryOperator<ParseException> fallback = Spied.checkedDoubleBinaryOperator((d1, d2) -> d1 + d2 + d1 + d2);

                CheckedDoubleBinaryOperator<ParseException> applying = operator.onErrorApplyChecked(fallback);

                assertEquals(6D, applying.applyAsDouble(1D, 2D));

                verify(operator).applyAsDouble(1D, 2D);
                verify(operator).onErrorApplyChecked(fallback);
                verify(fallback).applyAsDouble(1D, 2D);
                verifyNoMoreInteractions(operator, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                CheckedDoubleBinaryOperator<IOException> operator = Spied.checkedDoubleBinaryOperator((d1, d2) -> {
                    throw new IOException(Double.toString(d1 + d2));
                });

                CheckedDoubleBinaryOperator<ParseException> fallback = Spied.checkedDoubleBinaryOperator((d1, d2) -> {
                    throw new ParseException(Double.toString(d1 + d2), 0);
                });

                CheckedDoubleBinaryOperator<ParseException> applying = operator.onErrorApplyChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> applying.applyAsDouble(1D, 2D));
                assertEquals("3.0", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(operator).applyAsDouble(1D, 2D);
                verify(operator).onErrorApplyChecked(fallback);
                verify(fallback).applyAsDouble(1D, 2D);
                verifyNoMoreInteractions(operator, fallback);
            }

            @Test
            void testFallbackThrowsUnchecked() throws IOException, ParseException {
                CheckedDoubleBinaryOperator<IOException> operator = Spied.checkedDoubleBinaryOperator((d1, d2) -> {
                    throw new IOException(Double.toString(d1 + d2));
                });

                CheckedDoubleBinaryOperator<ParseException> fallback = Spied.checkedDoubleBinaryOperator((d1, d2) -> {
                    throw new IllegalStateException(Double.toString(d1 + d2));
                });

                CheckedDoubleBinaryOperator<ParseException> applying = operator.onErrorApplyChecked(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> applying.applyAsDouble(1D, 2D));
                assertEquals("3.0", thrown.getMessage());

                verify(operator).applyAsDouble(1D, 2D);
                verify(operator).onErrorApplyChecked(fallback);
                verify(fallback).applyAsDouble(1D, 2D);
                verifyNoMoreInteractions(operator, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedDoubleBinaryOperator<IOException> operator = Spied.checkedDoubleBinaryOperator((d1, d2) -> {
                throw new IllegalStateException(Double.toString(d1 + d2));
            });

            CheckedDoubleBinaryOperator<ParseException> fallback = Spied.checkedDoubleBinaryOperator((d1, d2) -> d1 + d2 + d1 + d2);

            CheckedDoubleBinaryOperator<ParseException> applying = operator.onErrorApplyChecked(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> applying.applyAsDouble(1D, 2D));
            assertEquals("3.0", thrown.getMessage());

            verify(operator).applyAsDouble(1D, 2D);
            verify(operator).onErrorApplyChecked(fallback);
            verifyNoMoreInteractions(operator, fallback);
        }
    }

    @Nested
    class OnErrorApplyUnchecked {

        @Test
        void testNullArgument() {
            CheckedDoubleBinaryOperator<IOException> operator = Spied.checkedDoubleBinaryOperator(Double::max);

            assertThrows(NullPointerException.class, () -> operator.onErrorApplyUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedDoubleBinaryOperator<IOException> operator = Spied.checkedDoubleBinaryOperator(Double::max);

            DoubleBinaryOperator fallback = Spied.doubleBinaryOperator((d1, d2) -> d1 + d2 + d1 + d2);

            DoubleBinaryOperator applying = operator.onErrorApplyUnchecked(fallback);

            assertEquals(2D, applying.applyAsDouble(1D, 2D));

            verify(operator).applyAsDouble(1D, 2D);
            verify(operator).onErrorApplyUnchecked(fallback);
            verifyNoMoreInteractions(operator, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                CheckedDoubleBinaryOperator<IOException> operator = Spied.checkedDoubleBinaryOperator((d1, d2) -> {
                    throw new IOException(Double.toString(d1 + d2));
                });

                DoubleBinaryOperator fallback = Spied.doubleBinaryOperator((d1, d2) -> d1 + d2 + d1 + d2);

                DoubleBinaryOperator applying = operator.onErrorApplyUnchecked(fallback);

                assertEquals(6D, applying.applyAsDouble(1D, 2D));

                verify(operator).applyAsDouble(1D, 2D);
                verify(operator).onErrorApplyUnchecked(fallback);
                verify(fallback).applyAsDouble(1D, 2D);
                verifyNoMoreInteractions(operator, fallback);
            }

            @Test
            void testFallbackThrowsUnchecked() throws IOException {
                CheckedDoubleBinaryOperator<IOException> operator = Spied.checkedDoubleBinaryOperator((d1, d2) -> {
                    throw new IOException(Double.toString(d1 + d2));
                });

                DoubleBinaryOperator fallback = Spied.doubleBinaryOperator((d1, d2) -> {
                    throw new IllegalStateException(Double.toString(d1 + d2));
                });

                DoubleBinaryOperator applying = operator.onErrorApplyUnchecked(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> applying.applyAsDouble(1D, 2D));
                assertEquals("3.0", thrown.getMessage());

                verify(operator).applyAsDouble(1D, 2D);
                verify(operator).onErrorApplyUnchecked(fallback);
                verify(fallback).applyAsDouble(1D, 2D);
                verifyNoMoreInteractions(operator, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedDoubleBinaryOperator<IOException> operator = Spied.checkedDoubleBinaryOperator((d1, d2) -> {
                throw new IllegalStateException(Double.toString(d1 + d2));
            });

            DoubleBinaryOperator fallback = Spied.doubleBinaryOperator((d1, d2) -> d1 + d2 + d1 + d2);

            DoubleBinaryOperator applying = operator.onErrorApplyUnchecked(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> applying.applyAsDouble(1D, 2D));
            assertEquals("3.0", thrown.getMessage());

            verify(operator).applyAsDouble(1D, 2D);
            verify(operator).onErrorApplyUnchecked(fallback);
            verifyNoMoreInteractions(operator, fallback);
        }
    }

    @Nested
    class OnErrorGetChecked {

        @Test
        void testNullArgument() {
            CheckedDoubleBinaryOperator<IOException> operator = Spied.checkedDoubleBinaryOperator(Double::max);

            assertThrows(NullPointerException.class, () -> operator.onErrorGetChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedDoubleBinaryOperator<IOException> operator = Spied.checkedDoubleBinaryOperator(Double::max);

            CheckedDoubleSupplier<ExecutionException> fallback = Spied.checkedDoubleSupplier(() -> 0D);

            CheckedDoubleBinaryOperator<ExecutionException> getting = operator.onErrorGetChecked(fallback);

            assertEquals(2D, getting.applyAsDouble(1D, 2D));

            verify(operator).applyAsDouble(1D, 2D);
            verify(operator).onErrorGetChecked(fallback);
            verifyNoMoreInteractions(operator, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                CheckedDoubleBinaryOperator<IOException> operator = Spied.checkedDoubleBinaryOperator((d1, d2) -> {
                    throw new IOException(Double.toString(d1 + d2));
                });

                CheckedDoubleSupplier<ParseException> fallback = Spied.checkedDoubleSupplier(() -> 0D);

                CheckedDoubleBinaryOperator<ParseException> getting = operator.onErrorGetChecked(fallback);

                assertEquals(0D, getting.applyAsDouble(1D, 2D));

                verify(operator).applyAsDouble(1D, 2D);
                verify(operator).onErrorGetChecked(fallback);
                verify(fallback).getAsDouble();
                verifyNoMoreInteractions(operator, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                CheckedDoubleBinaryOperator<IOException> operator = Spied.checkedDoubleBinaryOperator((d1, d2) -> {
                    throw new IOException(Double.toString(d1 + d2));
                });

                CheckedDoubleSupplier<ParseException> fallback = Spied.checkedDoubleSupplier(() -> {
                    throw new ParseException("bar", 0);
                });

                CheckedDoubleBinaryOperator<ParseException> getting = operator.onErrorGetChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> getting.applyAsDouble(1D, 2D));
                assertEquals("bar", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(operator).applyAsDouble(1D, 2D);
                verify(operator).onErrorGetChecked(fallback);
                verify(fallback).getAsDouble();
                verifyNoMoreInteractions(operator, fallback);
            }

            @Test
            void testFallbackThrowsUnchecked() throws IOException, ParseException {
                CheckedDoubleBinaryOperator<IOException> operator = Spied.checkedDoubleBinaryOperator((d1, d2) -> {
                    throw new IOException(Double.toString(d1 + d2));
                });

                CheckedDoubleSupplier<ParseException> fallback = Spied.checkedDoubleSupplier(() -> {
                    throw new IllegalStateException("bar");
                });

                CheckedDoubleBinaryOperator<ParseException> getting = operator.onErrorGetChecked(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> getting.applyAsDouble(1D, 2D));
                assertEquals("bar", thrown.getMessage());

                verify(operator).applyAsDouble(1D, 2D);
                verify(operator).onErrorGetChecked(fallback);
                verify(fallback).getAsDouble();
                verifyNoMoreInteractions(operator, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedDoubleBinaryOperator<IOException> operator = Spied.checkedDoubleBinaryOperator((d1, d2) -> {
                throw new IllegalStateException(Double.toString(d1 + d2));
            });

            CheckedDoubleSupplier<ParseException> fallback = Spied.checkedDoubleSupplier(() -> 0D);

            CheckedDoubleBinaryOperator<ParseException> getting = operator.onErrorGetChecked(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> getting.applyAsDouble(1D, 2D));
            assertEquals("3.0", thrown.getMessage());

            verify(operator).applyAsDouble(1D, 2D);
            verify(operator).onErrorGetChecked(fallback);
            verifyNoMoreInteractions(operator, fallback);
        }
    }

    @Nested
    class OnErrorGetUnchecked {

        @Test
        void testNullArgument() {
            CheckedDoubleBinaryOperator<IOException> operator = Spied.checkedDoubleBinaryOperator(Double::max);

            assertThrows(NullPointerException.class, () -> operator.onErrorGetUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedDoubleBinaryOperator<IOException> operator = Spied.checkedDoubleBinaryOperator(Double::max);

            DoubleSupplier fallback = Spied.doubleSupplier(() -> 2D);

            DoubleBinaryOperator getting = operator.onErrorGetUnchecked(fallback);

            assertEquals(2D, getting.applyAsDouble(1D, 2D));

            verify(operator).applyAsDouble(1D, 2D);
            verify(operator).onErrorGetUnchecked(fallback);
            verifyNoMoreInteractions(operator, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                CheckedDoubleBinaryOperator<IOException> operator = Spied.checkedDoubleBinaryOperator((d1, d2) -> {
                    throw new IOException(Double.toString(d1 + d2));
                });

                DoubleSupplier fallback = Spied.doubleSupplier(() -> 2D);

                DoubleBinaryOperator getting = operator.onErrorGetUnchecked(fallback);

                assertEquals(2D, getting.applyAsDouble(1D, 2D));

                verify(operator).applyAsDouble(1D, 2D);
                verify(operator).onErrorGetUnchecked(fallback);
                verify(fallback).getAsDouble();
                verifyNoMoreInteractions(operator, fallback);
            }

            @Test
            void testFallbackThrowsUnchecked() throws IOException {
                CheckedDoubleBinaryOperator<IOException> operator = Spied.checkedDoubleBinaryOperator((d1, d2) -> {
                    throw new IOException(Double.toString(d1 + d2));
                });

                DoubleSupplier fallback = Spied.doubleSupplier(() -> {
                    throw new IllegalStateException("bar");
                });

                DoubleBinaryOperator getting = operator.onErrorGetUnchecked(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> getting.applyAsDouble(1D, 2D));
                assertEquals("bar", thrown.getMessage());

                verify(operator).applyAsDouble(1D, 2D);
                verify(operator).onErrorGetUnchecked(fallback);
                verify(fallback).getAsDouble();
                verifyNoMoreInteractions(operator, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedDoubleBinaryOperator<IOException> operator = Spied.checkedDoubleBinaryOperator((d1, d2) -> {
                throw new IllegalStateException(Double.toString(d1 + d2));
            });

            DoubleSupplier fallback = Spied.doubleSupplier(() -> 2D);

            DoubleBinaryOperator getting = operator.onErrorGetUnchecked(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> getting.applyAsDouble(1D, 2D));
            assertEquals("3.0", thrown.getMessage());

            verify(operator).applyAsDouble(1D, 2D);
            verify(operator).onErrorGetUnchecked(fallback);
            verifyNoMoreInteractions(operator, fallback);
        }
    }

    @Nested
    class OnErrorReturn {

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedDoubleBinaryOperator<IOException> operator = Spied.checkedDoubleBinaryOperator(Double::max);

            DoubleBinaryOperator returning = operator.onErrorReturn(2D);

            assertEquals(2D, returning.applyAsDouble(1D, 2D));

            verify(operator).applyAsDouble(1D, 2D);
            verify(operator).onErrorReturn(2D);
            verifyNoMoreInteractions(operator);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedDoubleBinaryOperator<IOException> operator = Spied.checkedDoubleBinaryOperator((d1, d2) -> {
                throw new IOException(Double.toString(d1 + d2));
            });

            DoubleBinaryOperator returning = operator.onErrorReturn(2D);

            assertEquals(2D, returning.applyAsDouble(1D, 2D));

            verify(operator).applyAsDouble(1D, 2D);
            verify(operator).onErrorReturn(2D);
            verifyNoMoreInteractions(operator);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedDoubleBinaryOperator<IOException> operator = Spied.checkedDoubleBinaryOperator((d1, d2) -> {
                throw new IllegalStateException(Double.toString(d1 + d2));
            });

            DoubleBinaryOperator returning = operator.onErrorReturn(2D);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> returning.applyAsDouble(1D, 2D));
            assertEquals("3.0", thrown.getMessage());

            verify(operator).applyAsDouble(1D, 2D);
            verify(operator).onErrorReturn(2D);
            verifyNoMoreInteractions(operator);
        }
    }

    @Nested
    class Unchecked {

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedDoubleBinaryOperator<IOException> operator = Spied.checkedDoubleBinaryOperator(Double::max);

            DoubleBinaryOperator unchecked = operator.unchecked();

            assertEquals(2D, unchecked.applyAsDouble(1D, 2D));

            verify(operator).applyAsDouble(1D, 2D);
            verify(operator).unchecked();
            verify(operator).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(operator);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedDoubleBinaryOperator<IOException> operator = Spied.checkedDoubleBinaryOperator((d1, d2) -> {
                throw new IOException(Double.toString(d1 + d2));
            });

            DoubleBinaryOperator unchecked = operator.unchecked();

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.applyAsDouble(1D, 2D));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("3.0", cause.getMessage());

            verify(operator).applyAsDouble(1D, 2D);
            verify(operator).unchecked();
            verify(operator).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(operator);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedDoubleBinaryOperator<IOException> operator = Spied.checkedDoubleBinaryOperator((d1, d2) -> {
                throw new IllegalArgumentException(Double.toString(d1 + d2));
            });

            DoubleBinaryOperator unchecked = operator.unchecked();

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> unchecked.applyAsDouble(1D, 2D));
            assertEquals("3.0", thrown.getMessage());

            verify(operator).applyAsDouble(1D, 2D);
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
            CheckedDoubleBinaryOperator<IOException> operator = CheckedDoubleBinaryOperator.of(Double::max);

            assertEquals(2D, operator.applyAsDouble(1D, 2D));
        }
    }

    @Nested
    class UncheckedFactory {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> CheckedFunction.unchecked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedDoubleBinaryOperator<IOException> operator = Spied.checkedDoubleBinaryOperator(Double::max);

            DoubleBinaryOperator unchecked = CheckedDoubleBinaryOperator.unchecked(operator);

            assertEquals(2D, unchecked.applyAsDouble(1D, 2D));

            verify(operator).unchecked();
            verify(operator).onErrorThrowAsUnchecked(any());
            verify(operator).applyAsDouble(1D, 2D);
            verifyNoMoreInteractions(operator);
        }

        @Test
        void testArgumentThrowsChecked() throws IOException {
            CheckedDoubleBinaryOperator<IOException> operator = Spied.checkedDoubleBinaryOperator((d1, d2) -> {
                throw new IOException(Double.toString(d1 + d2));
            });

            DoubleBinaryOperator unchecked = CheckedDoubleBinaryOperator.unchecked(operator);

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.applyAsDouble(1D, 2D));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("3.0", cause.getMessage());

            verify(operator).unchecked();
            verify(operator).onErrorThrowAsUnchecked(any());
            verify(operator).applyAsDouble(1D, 2D);
            verifyNoMoreInteractions(operator);
        }

        @Test
        void testArgumentThrowsUnchecked() throws IOException {
            CheckedDoubleBinaryOperator<IOException> operator = Spied.checkedDoubleBinaryOperator((d1, d2) -> {
                throw new IllegalArgumentException(Double.toString(d1 + d2));
            });

            DoubleBinaryOperator unchecked = CheckedDoubleBinaryOperator.unchecked(operator);

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> unchecked.applyAsDouble(1D, 2D));
            assertEquals("3.0", thrown.getMessage());

            verify(operator).unchecked();
            verify(operator).onErrorThrowAsUnchecked(any());
            verify(operator).applyAsDouble(1D, 2D);
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
            CheckedDoubleBinaryOperator<IOException> operator = CheckedDoubleBinaryOperator.checked(Double::max);

            assertEquals(2D, operator.applyAsDouble(1D, 2D));
        }

        @Test
        void testArgumentThrowsUnchecked() {
            CheckedDoubleBinaryOperator<IOException> operator = CheckedDoubleBinaryOperator.checked((d1, d2) -> {
                throw new UncheckedException(Double.toString(d1 + d2), new IOException());
            });

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> operator.applyAsDouble(1D, 2D));
            assertEquals("3.0", thrown.getMessage());
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertNull(cause.getMessage());
        }
    }

    @Nested
    class CheckedUnwrapper {

        @Test
        void testNullArguments() {
            DoubleBinaryOperator operator = Double::max;

            assertThrows(NullPointerException.class, () -> CheckedDoubleBinaryOperator.checked(null, IOException.class));
            assertThrows(NullPointerException.class, () -> CheckedDoubleBinaryOperator.checked(operator, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedDoubleBinaryOperator<IOException> operator = CheckedDoubleBinaryOperator.checked(Double::max, IOException.class);

            assertEquals(2D, operator.applyAsDouble(1D, 2D));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                CheckedDoubleBinaryOperator<IOException> operator = CheckedDoubleBinaryOperator.checked((d1, d2) -> {
                    throw new UncheckedException(new IOException(Double.toString(d1 + d2)));
                }, IOException.class);

                IOException thrown = assertThrows(IOException.class, () -> operator.applyAsDouble(1D, 2D));
                assertEquals("3.0", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                CheckedDoubleBinaryOperator<IOException> operator = CheckedDoubleBinaryOperator.checked((d1, d2) -> {
                    throw new UncheckedException(new FileNotFoundException(Double.toString(d1 + d2)));
                }, IOException.class);

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class, () -> operator.applyAsDouble(1D, 2D));
                assertEquals("3.0", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                CheckedDoubleBinaryOperator<IOException> operator = CheckedDoubleBinaryOperator.checked((d1, d2) -> {
                    throw new UncheckedException(new ParseException(Double.toString(d1 + d2), 0));
                }, IOException.class);

                UncheckedException thrown = assertThrows(UncheckedException.class, () -> operator.applyAsDouble(1D, 2D));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("3.0", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            CheckedDoubleBinaryOperator<IOException> operator = CheckedDoubleBinaryOperator.checked((d1, d2) -> {
                throw new IllegalStateException(Double.toString(d1 + d2));
            }, IOException.class);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> operator.applyAsDouble(1D, 2D));
            assertEquals("3.0", thrown.getMessage());
        }
    }

    @Nested
    class InvokeAndUnwrap {

        @Test
        void testNullArguments() {
            DoubleBinaryOperator operator = Double::max;

            assertThrows(NullPointerException.class, () -> CheckedDoubleBinaryOperator.invokeAndUnwrap(null, 1D, 2D, IOException.class));
            assertThrows(NullPointerException.class, () -> CheckedDoubleBinaryOperator.invokeAndUnwrap(operator, 1D, 2D, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            DoubleBinaryOperator operator = Double::max;

            assertEquals(2D, CheckedDoubleBinaryOperator.invokeAndUnwrap(operator, 1D, 2D, IOException.class));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                DoubleBinaryOperator operator = (d1, d2) -> {
                    throw new UncheckedException(new IOException(Double.toString(d1 + d2)));
                };

                IOException thrown = assertThrows(IOException.class,
                        () -> CheckedDoubleBinaryOperator.invokeAndUnwrap(operator, 1D, 2D, IOException.class));
                assertEquals("3.0", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                DoubleBinaryOperator operator = (d1, d2) -> {
                    throw new UncheckedException(new FileNotFoundException(Double.toString(d1 + d2)));
                };

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class,
                        () -> CheckedDoubleBinaryOperator.invokeAndUnwrap(operator, 1D, 2D, IOException.class));
                assertEquals("3.0", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                DoubleBinaryOperator operator = (d1, d2) -> {
                    throw new UncheckedException(new ParseException(Double.toString(d1 + d2), 0));
                };

                UncheckedException thrown = assertThrows(UncheckedException.class,
                        () -> CheckedDoubleBinaryOperator.invokeAndUnwrap(operator, 1D, 2D, IOException.class));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("3.0", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            DoubleBinaryOperator operator = (d1, d2) -> {
                throw new IllegalStateException(Double.toString(d1 + d2));
            };

            IllegalStateException thrown = assertThrows(IllegalStateException.class,
                    () -> CheckedDoubleBinaryOperator.invokeAndUnwrap(operator, 1D, 2D, IOException.class));
            assertEquals("3.0", thrown.getMessage());
        }
    }
}
