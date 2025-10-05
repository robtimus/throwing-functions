/*
 * ThrowingDoubleUnaryOperatorTest.java
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

package com.github.robtimus.function.throwing;

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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

@SuppressWarnings("nls")
class ThrowingDoubleUnaryOperatorTest {

    @Nested
    class OnErrorThrowAsChecked {

        @Test
        void testNullArgument() {
            ThrowingDoubleUnaryOperator<IOException> operator = Spied.throwingDoubleUnaryOperator(d -> d + 1);

            assertThrows(NullPointerException.class, () -> operator.onErrorThrowAsChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingDoubleUnaryOperator<IOException> operator = Spied.throwingDoubleUnaryOperator(d -> d + 1);

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingDoubleUnaryOperator<ExecutionException> throwing = operator.onErrorThrowAsChecked(errorMapper);

            assertEquals(2D, throwing.applyAsDouble(1D));

            verify(operator).applyAsDouble(1D);
            verify(operator).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(operator, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingDoubleUnaryOperator<IOException> operator = Spied.throwingDoubleUnaryOperator(d -> {
                throw new IOException(Double.toString(d));
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingDoubleUnaryOperator<ExecutionException> throwing = operator.onErrorThrowAsChecked(errorMapper);

            ExecutionException thrown = assertThrows(ExecutionException.class, () -> throwing.applyAsDouble(1D));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1.0", cause.getMessage());

            verify(operator).applyAsDouble(1D);
            verify(operator).onErrorThrowAsChecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(operator, errorMapper);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingDoubleUnaryOperator<IOException> operator = Spied.throwingDoubleUnaryOperator(throwable::throwUnchecked);

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingDoubleUnaryOperator<ExecutionException> throwing = operator.onErrorThrowAsChecked(errorMapper);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> throwing.applyAsDouble(1D));
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
            ThrowingDoubleUnaryOperator<IOException> operator = Spied.throwingDoubleUnaryOperator(d -> d + 1);

            assertThrows(NullPointerException.class, () -> operator.onErrorThrowAsUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingDoubleUnaryOperator<IOException> operator = Spied.throwingDoubleUnaryOperator(d -> d + 1);

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            DoubleUnaryOperator throwing = operator.onErrorThrowAsUnchecked(errorMapper);

            assertEquals(2D, throwing.applyAsDouble(1D));

            verify(operator).applyAsDouble(1D);
            verify(operator).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(operator, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingDoubleUnaryOperator<IOException> operator = Spied.throwingDoubleUnaryOperator(d -> {
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

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingDoubleUnaryOperator<IOException> operator = Spied.throwingDoubleUnaryOperator(throwable::throwUnchecked);

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            DoubleUnaryOperator throwing = operator.onErrorThrowAsUnchecked(errorMapper);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> throwing.applyAsDouble(1D));
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
            ThrowingDoubleUnaryOperator<IOException> operator = Spied.throwingDoubleUnaryOperator(d -> d + 1);

            assertThrows(NullPointerException.class, () -> operator.onErrorHandleChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingDoubleUnaryOperator<IOException> operator = Spied.throwingDoubleUnaryOperator(d -> d + 1);

            ThrowingToDoubleFunction<IOException, ExecutionException> errorHandler = Spied.throwingToDoubleFunction(e -> 0D);

            ThrowingDoubleUnaryOperator<ExecutionException> handling = operator.onErrorHandleChecked(errorHandler);

            assertEquals(2D, handling.applyAsDouble(1D));

            verify(operator).applyAsDouble(1D);
            verify(operator).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(operator, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException, ExecutionException {
                ThrowingDoubleUnaryOperator<IOException> operator = Spied.throwingDoubleUnaryOperator(d -> {
                    throw new IOException(Double.toString(d));
                });

                ThrowingToDoubleFunction<IOException, ExecutionException> errorHandler = Spied.throwingToDoubleFunction(e -> 0D);

                ThrowingDoubleUnaryOperator<ExecutionException> handling = operator.onErrorHandleChecked(errorHandler);

                assertEquals(0D, handling.applyAsDouble(1D));

                verify(operator).applyAsDouble(1D);
                verify(operator).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsDouble(any());
                verifyNoMoreInteractions(operator, errorHandler);
            }

            @Test
            void testHandlerThrowsChecked() throws IOException, ExecutionException {
                ThrowingDoubleUnaryOperator<IOException> operator = Spied.throwingDoubleUnaryOperator(d -> {
                    throw new IOException(Double.toString(d));
                });

                ThrowingToDoubleFunction<IOException, ExecutionException> errorHandler = Spied.throwingToDoubleFunction(e -> {
                    throw new ExecutionException(e);
                });

                ThrowingDoubleUnaryOperator<ExecutionException> handling = operator.onErrorHandleChecked(errorHandler);

                ExecutionException thrown = assertThrows(ExecutionException.class, () -> handling.applyAsDouble(1D));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("1.0", cause.getMessage());

                verify(operator).applyAsDouble(1D);
                verify(operator).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsDouble(any());
                verifyNoMoreInteractions(operator, errorHandler);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testHandlerThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ExecutionException {
                ThrowingDoubleUnaryOperator<IOException> operator = Spied.throwingDoubleUnaryOperator(d -> {
                    throw new IOException(Double.toString(d));
                });

                ThrowingToDoubleFunction<IOException, ExecutionException> errorHandler = Spied.throwingToDoubleFunction(throwable::throwUnchecked);

                ThrowingDoubleUnaryOperator<ExecutionException> handling = operator.onErrorHandleChecked(errorHandler);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.applyAsDouble(1D));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("1.0", cause.getMessage());

                verify(operator).applyAsDouble(1D);
                verify(operator).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsDouble(any());
                verifyNoMoreInteractions(operator, errorHandler);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingDoubleUnaryOperator<IOException> operator = Spied.throwingDoubleUnaryOperator(throwable::throwUnchecked);

            ThrowingToDoubleFunction<IOException, ExecutionException> errorHandler = Spied.throwingToDoubleFunction(e -> 0D);

            ThrowingDoubleUnaryOperator<ExecutionException> handling = operator.onErrorHandleChecked(errorHandler);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.applyAsDouble(1D));
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
            ThrowingDoubleUnaryOperator<IOException> operator = Spied.throwingDoubleUnaryOperator(d -> d + 1);

            assertThrows(NullPointerException.class, () -> operator.onErrorHandleUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingDoubleUnaryOperator<IOException> operator = Spied.throwingDoubleUnaryOperator(d -> d + 1);

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
                ThrowingDoubleUnaryOperator<IOException> operator = Spied.throwingDoubleUnaryOperator(d -> {
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

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testHandlerThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingDoubleUnaryOperator<IOException> operator = Spied.throwingDoubleUnaryOperator(d -> {
                    throw new IOException(Double.toString(d));
                });

                ToDoubleFunction<IOException> errorHandler = Spied.toDoubleFunction(throwable::throwUnchecked);

                DoubleUnaryOperator handling = operator.onErrorHandleUnchecked(errorHandler);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.applyAsDouble(1D));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("1.0", cause.getMessage());

                verify(operator).applyAsDouble(1D);
                verify(operator).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).applyAsDouble(any());
                verifyNoMoreInteractions(operator, errorHandler);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingDoubleUnaryOperator<IOException> operator = Spied.throwingDoubleUnaryOperator(throwable::throwUnchecked);

            ToDoubleFunction<IOException> errorHandler = Spied.toDoubleFunction(e -> 0D);

            DoubleUnaryOperator handling = operator.onErrorHandleUnchecked(errorHandler);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.applyAsDouble(1D));
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
            ThrowingDoubleUnaryOperator<IOException> operator = Spied.throwingDoubleUnaryOperator(d -> d + 1);

            assertThrows(NullPointerException.class, () -> operator.onErrorApplyChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingDoubleUnaryOperator<IOException> operator = Spied.throwingDoubleUnaryOperator(d -> d + 1);

            ThrowingDoubleUnaryOperator<ExecutionException> fallback = Spied.throwingDoubleUnaryOperator(d -> d + d);

            ThrowingDoubleUnaryOperator<ExecutionException> applying = operator.onErrorApplyChecked(fallback);

            assertEquals(2D, applying.applyAsDouble(1D));

            verify(operator).applyAsDouble(1D);
            verify(operator).onErrorApplyChecked(fallback);
            verifyNoMoreInteractions(operator, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                ThrowingDoubleUnaryOperator<IOException> operator = Spied.throwingDoubleUnaryOperator(d -> {
                    throw new IOException(Double.toString(d));
                });

                ThrowingDoubleUnaryOperator<ParseException> fallback = Spied.throwingDoubleUnaryOperator(d -> d + d);

                ThrowingDoubleUnaryOperator<ParseException> applying = operator.onErrorApplyChecked(fallback);

                assertEquals(2D, applying.applyAsDouble(1D));

                verify(operator).applyAsDouble(1D);
                verify(operator).onErrorApplyChecked(fallback);
                verify(fallback).applyAsDouble(1D);
                verifyNoMoreInteractions(operator, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                ThrowingDoubleUnaryOperator<IOException> operator = Spied.throwingDoubleUnaryOperator(d -> {
                    throw new IOException(Double.toString(d));
                });

                ThrowingDoubleUnaryOperator<ParseException> fallback = Spied.throwingDoubleUnaryOperator(d -> {
                    throw new ParseException(Double.toString(d), 0);
                });

                ThrowingDoubleUnaryOperator<ParseException> applying = operator.onErrorApplyChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> applying.applyAsDouble(1D));
                assertEquals("1.0", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(operator).applyAsDouble(1D);
                verify(operator).onErrorApplyChecked(fallback);
                verify(fallback).applyAsDouble(1D);
                verifyNoMoreInteractions(operator, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ParseException {
                ThrowingDoubleUnaryOperator<IOException> operator = Spied.throwingDoubleUnaryOperator(d -> {
                    throw new IOException(Double.toString(d));
                });

                ThrowingDoubleUnaryOperator<ParseException> fallback = Spied
                        .throwingDoubleUnaryOperator(throwable::throwUnchecked);

                ThrowingDoubleUnaryOperator<ParseException> applying = operator.onErrorApplyChecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.applyAsDouble(1D));
                assertEquals("1.0", thrown.getMessage());

                verify(operator).applyAsDouble(1D);
                verify(operator).onErrorApplyChecked(fallback);
                verify(fallback).applyAsDouble(1D);
                verifyNoMoreInteractions(operator, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingDoubleUnaryOperator<IOException> operator = Spied.throwingDoubleUnaryOperator(throwable::throwUnchecked);

            ThrowingDoubleUnaryOperator<ParseException> fallback = Spied.throwingDoubleUnaryOperator(d -> d + d);

            ThrowingDoubleUnaryOperator<ParseException> applying = operator.onErrorApplyChecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.applyAsDouble(1D));
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
            ThrowingDoubleUnaryOperator<IOException> operator = Spied.throwingDoubleUnaryOperator(d -> d + 1);

            assertThrows(NullPointerException.class, () -> operator.onErrorApplyUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingDoubleUnaryOperator<IOException> operator = Spied.throwingDoubleUnaryOperator(d -> d + 1);

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
                ThrowingDoubleUnaryOperator<IOException> operator = Spied.throwingDoubleUnaryOperator(d -> {
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

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingDoubleUnaryOperator<IOException> operator = Spied.throwingDoubleUnaryOperator(d -> {
                    throw new IOException(Double.toString(d));
                });

                DoubleUnaryOperator fallback = Spied.doubleUnaryOperator(throwable::throwUnchecked);

                DoubleUnaryOperator applying = operator.onErrorApplyUnchecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.applyAsDouble(1D));
                assertEquals("1.0", thrown.getMessage());

                verify(operator).applyAsDouble(1D);
                verify(operator).onErrorApplyUnchecked(fallback);
                verify(fallback).applyAsDouble(1D);
                verifyNoMoreInteractions(operator, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingDoubleUnaryOperator<IOException> operator = Spied.throwingDoubleUnaryOperator(throwable::throwUnchecked);

            DoubleUnaryOperator fallback = Spied.doubleUnaryOperator(d -> d + d);

            DoubleUnaryOperator applying = operator.onErrorApplyUnchecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.applyAsDouble(1D));
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
            ThrowingDoubleUnaryOperator<IOException> operator = Spied.throwingDoubleUnaryOperator(d -> d + 1);

            assertThrows(NullPointerException.class, () -> operator.onErrorGetChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingDoubleUnaryOperator<IOException> operator = Spied.throwingDoubleUnaryOperator(d -> d + 1);

            ThrowingDoubleSupplier<ExecutionException> fallback = Spied.throwingDoubleSupplier(() -> 0D);

            ThrowingDoubleUnaryOperator<ExecutionException> getting = operator.onErrorGetChecked(fallback);

            assertEquals(2D, getting.applyAsDouble(1D));

            verify(operator).applyAsDouble(1D);
            verify(operator).onErrorGetChecked(fallback);
            verifyNoMoreInteractions(operator, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                ThrowingDoubleUnaryOperator<IOException> operator = Spied.throwingDoubleUnaryOperator(d -> {
                    throw new IOException(Double.toString(d));
                });

                ThrowingDoubleSupplier<ParseException> fallback = Spied.throwingDoubleSupplier(() -> 0D);

                ThrowingDoubleUnaryOperator<ParseException> getting = operator.onErrorGetChecked(fallback);

                assertEquals(0D, getting.applyAsDouble(1D));

                verify(operator).applyAsDouble(1D);
                verify(operator).onErrorGetChecked(fallback);
                verify(fallback).getAsDouble();
                verifyNoMoreInteractions(operator, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                ThrowingDoubleUnaryOperator<IOException> operator = Spied.throwingDoubleUnaryOperator(d -> {
                    throw new IOException(Double.toString(d));
                });

                ThrowingDoubleSupplier<ParseException> fallback = Spied.throwingDoubleSupplier(() -> {
                    throw new ParseException("bar", 0);
                });

                ThrowingDoubleUnaryOperator<ParseException> getting = operator.onErrorGetChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> getting.applyAsDouble(1D));
                assertEquals("bar", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(operator).applyAsDouble(1D);
                verify(operator).onErrorGetChecked(fallback);
                verify(fallback).getAsDouble();
                verifyNoMoreInteractions(operator, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ParseException {
                ThrowingDoubleUnaryOperator<IOException> operator = Spied.throwingDoubleUnaryOperator(d -> {
                    throw new IOException(Double.toString(d));
                });

                ThrowingDoubleSupplier<ParseException> fallback = Spied.throwingDoubleSupplier(() -> throwable.throwUnchecked("bar"));

                ThrowingDoubleUnaryOperator<ParseException> getting = operator.onErrorGetChecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.applyAsDouble(1D));
                assertEquals("bar", thrown.getMessage());

                verify(operator).applyAsDouble(1D);
                verify(operator).onErrorGetChecked(fallback);
                verify(fallback).getAsDouble();
                verifyNoMoreInteractions(operator, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingDoubleUnaryOperator<IOException> operator = Spied.throwingDoubleUnaryOperator(throwable::throwUnchecked);

            ThrowingDoubleSupplier<ParseException> fallback = Spied.throwingDoubleSupplier(() -> 0D);

            ThrowingDoubleUnaryOperator<ParseException> getting = operator.onErrorGetChecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.applyAsDouble(1D));
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
            ThrowingDoubleUnaryOperator<IOException> operator = Spied.throwingDoubleUnaryOperator(d -> d + 1);

            assertThrows(NullPointerException.class, () -> operator.onErrorGetUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingDoubleUnaryOperator<IOException> operator = Spied.throwingDoubleUnaryOperator(d -> d + 1);

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
                ThrowingDoubleUnaryOperator<IOException> operator = Spied.throwingDoubleUnaryOperator(d -> {
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

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingDoubleUnaryOperator<IOException> operator = Spied.throwingDoubleUnaryOperator(d -> {
                    throw new IOException(Double.toString(d));
                });

                DoubleSupplier fallback = Spied.doubleSupplier(() -> throwable.throwUnchecked("bar"));

                DoubleUnaryOperator getting = operator.onErrorGetUnchecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.applyAsDouble(1D));
                assertEquals("bar", thrown.getMessage());

                verify(operator).applyAsDouble(1D);
                verify(operator).onErrorGetUnchecked(fallback);
                verify(fallback).getAsDouble();
                verifyNoMoreInteractions(operator, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingDoubleUnaryOperator<IOException> operator = Spied.throwingDoubleUnaryOperator(throwable::throwUnchecked);

            DoubleSupplier fallback = Spied.doubleSupplier(() -> 2D);

            DoubleUnaryOperator getting = operator.onErrorGetUnchecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.applyAsDouble(1D));
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
            ThrowingDoubleUnaryOperator<IOException> operator = Spied.throwingDoubleUnaryOperator(d -> d + 1);

            DoubleUnaryOperator returning = operator.onErrorReturn(2D);

            assertEquals(2D, returning.applyAsDouble(1D));

            verify(operator).applyAsDouble(1D);
            verify(operator).onErrorReturn(2D);
            verifyNoMoreInteractions(operator);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingDoubleUnaryOperator<IOException> operator = Spied.throwingDoubleUnaryOperator(d -> {
                throw new IOException(Double.toString(d));
            });

            DoubleUnaryOperator returning = operator.onErrorReturn(2D);

            assertEquals(2D, returning.applyAsDouble(1D));

            verify(operator).applyAsDouble(1D);
            verify(operator).onErrorReturn(2D);
            verifyNoMoreInteractions(operator);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingDoubleUnaryOperator<IOException> operator = Spied.throwingDoubleUnaryOperator(throwable::throwUnchecked);

            DoubleUnaryOperator returning = operator.onErrorReturn(2D);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> returning.applyAsDouble(1D));
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
            ThrowingDoubleUnaryOperator<IOException> operator = Spied.throwingDoubleUnaryOperator(d -> d + 1);

            DoubleUnaryOperator unchecked = operator.unchecked();

            assertEquals(2D, unchecked.applyAsDouble(1D));

            verify(operator).applyAsDouble(1D);
            verify(operator).unchecked();
            verify(operator).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(operator);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingDoubleUnaryOperator<IOException> operator = Spied.throwingDoubleUnaryOperator(d -> {
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

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingDoubleUnaryOperator<IOException> operator = Spied.throwingDoubleUnaryOperator(throwable::throwUnchecked);

            DoubleUnaryOperator unchecked = operator.unchecked();

            Throwable thrown = assertThrows(throwable.throwableType(), () -> unchecked.applyAsDouble(1D));
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
            assertThrows(NullPointerException.class, () -> ThrowingFunction.of(null));
        }

        @Test
        void testNonNullArgument() throws IOException {
            ThrowingDoubleUnaryOperator<IOException> operator = ThrowingDoubleUnaryOperator.of(d -> d + 1);

            assertEquals(2D, operator.applyAsDouble(1D));
        }
    }

    @Test
    void testIdentity() throws IOException {
        ThrowingDoubleUnaryOperator<IOException> operator = ThrowingDoubleUnaryOperator.identity();

        assertEquals(1D, operator.applyAsDouble(1D));
    }

    @Nested
    class UncheckedFactory {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> ThrowingFunction.unchecked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ThrowingDoubleUnaryOperator<IOException> operator = Spied.throwingDoubleUnaryOperator(d -> d + 1);

            DoubleUnaryOperator unchecked = ThrowingDoubleUnaryOperator.unchecked(operator);

            assertEquals(2D, unchecked.applyAsDouble(1D));

            verify(operator).unchecked();
            verify(operator).onErrorThrowAsUnchecked(any());
            verify(operator).applyAsDouble(1D);
            verifyNoMoreInteractions(operator);
        }

        @Test
        void testArgumentThrowsChecked() throws IOException {
            ThrowingDoubleUnaryOperator<IOException> operator = Spied.throwingDoubleUnaryOperator(d -> {
                throw new IOException(Double.toString(d));
            });

            DoubleUnaryOperator unchecked = ThrowingDoubleUnaryOperator.unchecked(operator);

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.applyAsDouble(1D));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1.0", cause.getMessage());

            verify(operator).unchecked();
            verify(operator).onErrorThrowAsUnchecked(any());
            verify(operator).applyAsDouble(1D);
            verifyNoMoreInteractions(operator);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testArgumentThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingDoubleUnaryOperator<IOException> operator = Spied.throwingDoubleUnaryOperator(throwable::throwUnchecked);

            DoubleUnaryOperator unchecked = ThrowingDoubleUnaryOperator.unchecked(operator);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> unchecked.applyAsDouble(1D));
            assertEquals("1.0", thrown.getMessage());

            verify(operator).unchecked();
            verify(operator).onErrorThrowAsUnchecked(any());
            verify(operator).applyAsDouble(1D);
            verifyNoMoreInteractions(operator);
        }
    }

    @Nested
    class CheckedWrapper {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> ThrowingFunction.checked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ThrowingDoubleUnaryOperator<IOException> operator = ThrowingDoubleUnaryOperator.checked(d -> d + 1);

            assertEquals(2D, operator.applyAsDouble(1D));
        }

        @Test
        void testArgumentThrowsUnchecked() {
            ThrowingDoubleUnaryOperator<IOException> operator = ThrowingDoubleUnaryOperator.checked(d -> {
                throw UncheckedException.withoutStackTrace(Double.toString(d), new IOException());
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

            assertThrows(NullPointerException.class, () -> ThrowingDoubleUnaryOperator.checked(null, IOException.class));
            assertThrows(NullPointerException.class, () -> ThrowingDoubleUnaryOperator.checked(operator, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ThrowingDoubleUnaryOperator<IOException> operator = ThrowingDoubleUnaryOperator.checked(d -> d + 1, IOException.class);

            assertEquals(2D, operator.applyAsDouble(1D));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                ThrowingDoubleUnaryOperator<IOException> operator = ThrowingDoubleUnaryOperator.checked(d -> {
                    throw UncheckedException.withoutStackTrace(new IOException(Double.toString(d)));
                }, IOException.class);

                IOException thrown = assertThrows(IOException.class, () -> operator.applyAsDouble(1D));
                assertEquals("1.0", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                ThrowingDoubleUnaryOperator<IOException> operator = ThrowingDoubleUnaryOperator.checked(d -> {
                    throw UncheckedException.withoutStackTrace(new FileNotFoundException(Double.toString(d)));
                }, IOException.class);

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class, () -> operator.applyAsDouble(1D));
                assertEquals("1.0", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                ThrowingDoubleUnaryOperator<IOException> operator = ThrowingDoubleUnaryOperator.checked(d -> {
                    throw UncheckedException.withoutStackTrace(new ParseException(Double.toString(d), 0));
                }, IOException.class);

                UncheckedException thrown = assertThrows(UncheckedException.class, () -> operator.applyAsDouble(1D));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("1.0", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            ThrowingDoubleUnaryOperator<IOException> operator = ThrowingDoubleUnaryOperator.checked(d -> {
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

            assertThrows(NullPointerException.class, () -> ThrowingDoubleUnaryOperator.invokeAndUnwrap(null, 1D, IOException.class));
            assertThrows(NullPointerException.class, () -> ThrowingDoubleUnaryOperator.invokeAndUnwrap(operator, 1D, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            DoubleUnaryOperator operator = d -> d + 1;

            assertEquals(2D, ThrowingDoubleUnaryOperator.invokeAndUnwrap(operator, 1D, IOException.class));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                DoubleUnaryOperator operator = d -> {
                    throw UncheckedException.withoutStackTrace(new IOException(Double.toString(d)));
                };

                IOException thrown = assertThrows(IOException.class,
                        () -> ThrowingDoubleUnaryOperator.invokeAndUnwrap(operator, 1D, IOException.class));
                assertEquals("1.0", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                DoubleUnaryOperator operator = d -> {
                    throw UncheckedException.withoutStackTrace(new FileNotFoundException(Double.toString(d)));
                };

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class,
                        () -> ThrowingDoubleUnaryOperator.invokeAndUnwrap(operator, 1D, IOException.class));
                assertEquals("1.0", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                DoubleUnaryOperator operator = d -> {
                    throw UncheckedException.withoutStackTrace(new ParseException(Double.toString(d), 0));
                };

                UncheckedException thrown = assertThrows(UncheckedException.class,
                        () -> ThrowingDoubleUnaryOperator.invokeAndUnwrap(operator, 1D, IOException.class));
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
                    () -> ThrowingDoubleUnaryOperator.invokeAndUnwrap(operator, 1D, IOException.class));
            assertEquals("1.0", thrown.getMessage());
        }
    }
}
