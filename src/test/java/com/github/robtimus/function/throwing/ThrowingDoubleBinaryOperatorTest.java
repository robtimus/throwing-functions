/*
 * ThrowingDoubleBinaryOperatorTest.java
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
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleSupplier;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

@SuppressWarnings("nls")
class ThrowingDoubleBinaryOperatorTest {

    @Nested
    class OnErrorThrowAsChecked {

        @Test
        void testNullArgument() {
            ThrowingDoubleBinaryOperator<IOException> operator = Spied.throwingDoubleBinaryOperator(Double::max);

            assertThrows(NullPointerException.class, () -> operator.onErrorThrowAsChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingDoubleBinaryOperator<IOException> operator = Spied.throwingDoubleBinaryOperator(Double::max);

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingDoubleBinaryOperator<ExecutionException> throwing = operator.onErrorThrowAsChecked(errorMapper);

            assertEquals(2D, throwing.applyAsDouble(1D, 2D));

            verify(operator).applyAsDouble(1D, 2D);
            verify(operator).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(operator, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingDoubleBinaryOperator<IOException> operator = Spied.throwingDoubleBinaryOperator((d1, d2) -> {
                throw new IOException(Double.toString(d1 + d2));
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingDoubleBinaryOperator<ExecutionException> throwing = operator.onErrorThrowAsChecked(errorMapper);

            ExecutionException thrown = assertThrows(ExecutionException.class, () -> throwing.applyAsDouble(1D, 2D));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("3.0", cause.getMessage());

            verify(operator).applyAsDouble(1D, 2D);
            verify(operator).onErrorThrowAsChecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(operator, errorMapper);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingDoubleBinaryOperator<IOException> operator = Spied.throwingDoubleBinaryOperator((d1, d2) -> throwable.throwUnchecked(d1 + d2));

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingDoubleBinaryOperator<ExecutionException> throwing = operator.onErrorThrowAsChecked(errorMapper);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> throwing.applyAsDouble(1D, 2D));
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
            ThrowingDoubleBinaryOperator<IOException> operator = Spied.throwingDoubleBinaryOperator(Double::max);

            assertThrows(NullPointerException.class, () -> operator.onErrorThrowAsUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingDoubleBinaryOperator<IOException> operator = Spied.throwingDoubleBinaryOperator(Double::max);

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            DoubleBinaryOperator throwing = operator.onErrorThrowAsUnchecked(errorMapper);

            assertEquals(2D, throwing.applyAsDouble(1D, 2D));

            verify(operator).applyAsDouble(1D, 2D);
            verify(operator).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(operator, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingDoubleBinaryOperator<IOException> operator = Spied.throwingDoubleBinaryOperator((d1, d2) -> {
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

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingDoubleBinaryOperator<IOException> operator = Spied.throwingDoubleBinaryOperator((d1, d2) -> throwable.throwUnchecked(d1 + d2));

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            DoubleBinaryOperator throwing = operator.onErrorThrowAsUnchecked(errorMapper);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> throwing.applyAsDouble(1D, 2D));
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
            ThrowingDoubleBinaryOperator<IOException> operator = Spied.throwingDoubleBinaryOperator(Double::max);

            assertThrows(NullPointerException.class, () -> operator.onErrorHandleChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingDoubleBinaryOperator<IOException> operator = Spied.throwingDoubleBinaryOperator(Double::max);

            ThrowingToDoubleFunction<IOException, ExecutionException> errorHandler = Spied.throwingToDoubleFunction(e -> 0D);

            ThrowingDoubleBinaryOperator<ExecutionException> handling = operator.onErrorHandleChecked(errorHandler);

            assertEquals(2D, handling.applyAsDouble(1D, 2D));

            verify(operator).applyAsDouble(1D, 2D);
            verify(operator).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(operator, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException, ExecutionException {
                ThrowingDoubleBinaryOperator<IOException> operator = Spied.throwingDoubleBinaryOperator((d1, d2) -> {
                    throw new IOException(Double.toString(d1 + d2));
                });

                ThrowingToDoubleFunction<IOException, ExecutionException> errorHandler = Spied.throwingToDoubleFunction(e -> 0D);

                ThrowingDoubleBinaryOperator<ExecutionException> handling = operator.onErrorHandleChecked(errorHandler);

                assertEquals(0D, handling.applyAsDouble(1D, 2D));

                verify(operator).applyAsDouble(1D, 2D);
                verify(operator).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsDouble(any());
                verifyNoMoreInteractions(operator, errorHandler);
            }

            @Test
            void testHandlerThrowsChecked() throws IOException, ExecutionException {
                ThrowingDoubleBinaryOperator<IOException> operator = Spied.throwingDoubleBinaryOperator((d1, d2) -> {
                    throw new IOException(Double.toString(d1 + d2));
                });

                ThrowingToDoubleFunction<IOException, ExecutionException> errorHandler = Spied.throwingToDoubleFunction(e -> {
                    throw new ExecutionException(e);
                });

                ThrowingDoubleBinaryOperator<ExecutionException> handling = operator.onErrorHandleChecked(errorHandler);

                ExecutionException thrown = assertThrows(ExecutionException.class, () -> handling.applyAsDouble(1D, 2D));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("3.0", cause.getMessage());

                verify(operator).applyAsDouble(1D, 2D);
                verify(operator).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsDouble(any());
                verifyNoMoreInteractions(operator, errorHandler);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testHandlerThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ExecutionException {
                ThrowingDoubleBinaryOperator<IOException> operator = Spied.throwingDoubleBinaryOperator((d1, d2) -> {
                    throw new IOException(Double.toString(d1 + d2));
                });

                ThrowingToDoubleFunction<IOException, ExecutionException> errorHandler = Spied.throwingToDoubleFunction(throwable::throwUnchecked);

                ThrowingDoubleBinaryOperator<ExecutionException> handling = operator.onErrorHandleChecked(errorHandler);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.applyAsDouble(1D, 2D));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("3.0", cause.getMessage());

                verify(operator).applyAsDouble(1D, 2D);
                verify(operator).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsDouble(any());
                verifyNoMoreInteractions(operator, errorHandler);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingDoubleBinaryOperator<IOException> operator = Spied.throwingDoubleBinaryOperator((d1, d2) -> throwable.throwUnchecked(d1 + d2));

            ThrowingToDoubleFunction<IOException, ExecutionException> errorHandler = Spied.throwingToDoubleFunction(e -> 0D);

            ThrowingDoubleBinaryOperator<ExecutionException> handling = operator.onErrorHandleChecked(errorHandler);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.applyAsDouble(1D, 2D));
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
            ThrowingDoubleBinaryOperator<IOException> operator = Spied.throwingDoubleBinaryOperator(Double::max);

            assertThrows(NullPointerException.class, () -> operator.onErrorHandleUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingDoubleBinaryOperator<IOException> operator = Spied.throwingDoubleBinaryOperator(Double::max);

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
                ThrowingDoubleBinaryOperator<IOException> operator = Spied.throwingDoubleBinaryOperator((d1, d2) -> {
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

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testHandlerThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingDoubleBinaryOperator<IOException> operator = Spied.throwingDoubleBinaryOperator((d1, d2) -> {
                    throw new IOException(Double.toString(d1 + d2));
                });

                ToDoubleFunction<IOException> errorHandler = Spied.toDoubleFunction(throwable::throwUnchecked);

                DoubleBinaryOperator handling = operator.onErrorHandleUnchecked(errorHandler);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.applyAsDouble(1D, 2D));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("3.0", cause.getMessage());

                verify(operator).applyAsDouble(1D, 2D);
                verify(operator).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).applyAsDouble(any());
                verifyNoMoreInteractions(operator, errorHandler);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingDoubleBinaryOperator<IOException> operator = Spied.throwingDoubleBinaryOperator((d1, d2) -> throwable.throwUnchecked(d1 + d2));

            ToDoubleFunction<IOException> errorHandler = Spied.toDoubleFunction(e -> 0D);

            DoubleBinaryOperator handling = operator.onErrorHandleUnchecked(errorHandler);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.applyAsDouble(1D, 2D));
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
            ThrowingDoubleBinaryOperator<IOException> operator = Spied.throwingDoubleBinaryOperator(Double::max);

            assertThrows(NullPointerException.class, () -> operator.onErrorApplyChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingDoubleBinaryOperator<IOException> operator = Spied.throwingDoubleBinaryOperator(Double::max);

            ThrowingDoubleBinaryOperator<ExecutionException> fallback = Spied.throwingDoubleBinaryOperator((d1, d2) -> d1 + d2 + d1 + d2);

            ThrowingDoubleBinaryOperator<ExecutionException> applying = operator.onErrorApplyChecked(fallback);

            assertEquals(2D, applying.applyAsDouble(1D, 2D));

            verify(operator).applyAsDouble(1D, 2D);
            verify(operator).onErrorApplyChecked(fallback);
            verifyNoMoreInteractions(operator, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                ThrowingDoubleBinaryOperator<IOException> operator = Spied.throwingDoubleBinaryOperator((d1, d2) -> {
                    throw new IOException(Double.toString(d1 + d2));
                });

                ThrowingDoubleBinaryOperator<ParseException> fallback = Spied.throwingDoubleBinaryOperator((d1, d2) -> d1 + d2 + d1 + d2);

                ThrowingDoubleBinaryOperator<ParseException> applying = operator.onErrorApplyChecked(fallback);

                assertEquals(6D, applying.applyAsDouble(1D, 2D));

                verify(operator).applyAsDouble(1D, 2D);
                verify(operator).onErrorApplyChecked(fallback);
                verify(fallback).applyAsDouble(1D, 2D);
                verifyNoMoreInteractions(operator, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                ThrowingDoubleBinaryOperator<IOException> operator = Spied.throwingDoubleBinaryOperator((d1, d2) -> {
                    throw new IOException(Double.toString(d1 + d2));
                });

                ThrowingDoubleBinaryOperator<ParseException> fallback = Spied.throwingDoubleBinaryOperator((d1, d2) -> {
                    throw new ParseException(Double.toString(d1 + d2), 0);
                });

                ThrowingDoubleBinaryOperator<ParseException> applying = operator.onErrorApplyChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> applying.applyAsDouble(1D, 2D));
                assertEquals("3.0", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(operator).applyAsDouble(1D, 2D);
                verify(operator).onErrorApplyChecked(fallback);
                verify(fallback).applyAsDouble(1D, 2D);
                verifyNoMoreInteractions(operator, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ParseException {
                ThrowingDoubleBinaryOperator<IOException> operator = Spied.throwingDoubleBinaryOperator((d1, d2) -> {
                    throw new IOException(Double.toString(d1 + d2));
                });

                ThrowingDoubleBinaryOperator<ParseException> fallback = Spied
                        .throwingDoubleBinaryOperator((d1, d2) -> throwable.throwUnchecked(d1 + d2));

                ThrowingDoubleBinaryOperator<ParseException> applying = operator.onErrorApplyChecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.applyAsDouble(1D, 2D));
                assertEquals("3.0", thrown.getMessage());

                verify(operator).applyAsDouble(1D, 2D);
                verify(operator).onErrorApplyChecked(fallback);
                verify(fallback).applyAsDouble(1D, 2D);
                verifyNoMoreInteractions(operator, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingDoubleBinaryOperator<IOException> operator = Spied.throwingDoubleBinaryOperator((d1, d2) -> throwable.throwUnchecked(d1 + d2));

            ThrowingDoubleBinaryOperator<ParseException> fallback = Spied.throwingDoubleBinaryOperator((d1, d2) -> d1 + d2 + d1 + d2);

            ThrowingDoubleBinaryOperator<ParseException> applying = operator.onErrorApplyChecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.applyAsDouble(1D, 2D));
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
            ThrowingDoubleBinaryOperator<IOException> operator = Spied.throwingDoubleBinaryOperator(Double::max);

            assertThrows(NullPointerException.class, () -> operator.onErrorApplyUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingDoubleBinaryOperator<IOException> operator = Spied.throwingDoubleBinaryOperator(Double::max);

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
                ThrowingDoubleBinaryOperator<IOException> operator = Spied.throwingDoubleBinaryOperator((d1, d2) -> {
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

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingDoubleBinaryOperator<IOException> operator = Spied.throwingDoubleBinaryOperator((d1, d2) -> {
                    throw new IOException(Double.toString(d1 + d2));
                });

                DoubleBinaryOperator fallback = Spied.doubleBinaryOperator((d1, d2) -> throwable.throwUnchecked(d1 + d2));

                DoubleBinaryOperator applying = operator.onErrorApplyUnchecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.applyAsDouble(1D, 2D));
                assertEquals("3.0", thrown.getMessage());

                verify(operator).applyAsDouble(1D, 2D);
                verify(operator).onErrorApplyUnchecked(fallback);
                verify(fallback).applyAsDouble(1D, 2D);
                verifyNoMoreInteractions(operator, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingDoubleBinaryOperator<IOException> operator = Spied.throwingDoubleBinaryOperator((d1, d2) -> throwable.throwUnchecked(d1 + d2));

            DoubleBinaryOperator fallback = Spied.doubleBinaryOperator((d1, d2) -> d1 + d2 + d1 + d2);

            DoubleBinaryOperator applying = operator.onErrorApplyUnchecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.applyAsDouble(1D, 2D));
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
            ThrowingDoubleBinaryOperator<IOException> operator = Spied.throwingDoubleBinaryOperator(Double::max);

            assertThrows(NullPointerException.class, () -> operator.onErrorGetChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingDoubleBinaryOperator<IOException> operator = Spied.throwingDoubleBinaryOperator(Double::max);

            ThrowingDoubleSupplier<ExecutionException> fallback = Spied.throwingDoubleSupplier(() -> 0D);

            ThrowingDoubleBinaryOperator<ExecutionException> getting = operator.onErrorGetChecked(fallback);

            assertEquals(2D, getting.applyAsDouble(1D, 2D));

            verify(operator).applyAsDouble(1D, 2D);
            verify(operator).onErrorGetChecked(fallback);
            verifyNoMoreInteractions(operator, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                ThrowingDoubleBinaryOperator<IOException> operator = Spied.throwingDoubleBinaryOperator((d1, d2) -> {
                    throw new IOException(Double.toString(d1 + d2));
                });

                ThrowingDoubleSupplier<ParseException> fallback = Spied.throwingDoubleSupplier(() -> 0D);

                ThrowingDoubleBinaryOperator<ParseException> getting = operator.onErrorGetChecked(fallback);

                assertEquals(0D, getting.applyAsDouble(1D, 2D));

                verify(operator).applyAsDouble(1D, 2D);
                verify(operator).onErrorGetChecked(fallback);
                verify(fallback).getAsDouble();
                verifyNoMoreInteractions(operator, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                ThrowingDoubleBinaryOperator<IOException> operator = Spied.throwingDoubleBinaryOperator((d1, d2) -> {
                    throw new IOException(Double.toString(d1 + d2));
                });

                ThrowingDoubleSupplier<ParseException> fallback = Spied.throwingDoubleSupplier(() -> {
                    throw new ParseException("bar", 0);
                });

                ThrowingDoubleBinaryOperator<ParseException> getting = operator.onErrorGetChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> getting.applyAsDouble(1D, 2D));
                assertEquals("bar", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(operator).applyAsDouble(1D, 2D);
                verify(operator).onErrorGetChecked(fallback);
                verify(fallback).getAsDouble();
                verifyNoMoreInteractions(operator, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ParseException {
                ThrowingDoubleBinaryOperator<IOException> operator = Spied.throwingDoubleBinaryOperator((d1, d2) -> {
                    throw new IOException(Double.toString(d1 + d2));
                });

                ThrowingDoubleSupplier<ParseException> fallback = Spied.throwingDoubleSupplier(() -> throwable.throwUnchecked("bar"));

                ThrowingDoubleBinaryOperator<ParseException> getting = operator.onErrorGetChecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.applyAsDouble(1D, 2D));
                assertEquals("bar", thrown.getMessage());

                verify(operator).applyAsDouble(1D, 2D);
                verify(operator).onErrorGetChecked(fallback);
                verify(fallback).getAsDouble();
                verifyNoMoreInteractions(operator, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingDoubleBinaryOperator<IOException> operator = Spied.throwingDoubleBinaryOperator((d1, d2) -> throwable.throwUnchecked(d1 + d2));

            ThrowingDoubleSupplier<ParseException> fallback = Spied.throwingDoubleSupplier(() -> 0D);

            ThrowingDoubleBinaryOperator<ParseException> getting = operator.onErrorGetChecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.applyAsDouble(1D, 2D));
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
            ThrowingDoubleBinaryOperator<IOException> operator = Spied.throwingDoubleBinaryOperator(Double::max);

            assertThrows(NullPointerException.class, () -> operator.onErrorGetUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingDoubleBinaryOperator<IOException> operator = Spied.throwingDoubleBinaryOperator(Double::max);

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
                ThrowingDoubleBinaryOperator<IOException> operator = Spied.throwingDoubleBinaryOperator((d1, d2) -> {
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

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingDoubleBinaryOperator<IOException> operator = Spied.throwingDoubleBinaryOperator((d1, d2) -> {
                    throw new IOException(Double.toString(d1 + d2));
                });

                DoubleSupplier fallback = Spied.doubleSupplier(() -> throwable.throwUnchecked("bar"));

                DoubleBinaryOperator getting = operator.onErrorGetUnchecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.applyAsDouble(1D, 2D));
                assertEquals("bar", thrown.getMessage());

                verify(operator).applyAsDouble(1D, 2D);
                verify(operator).onErrorGetUnchecked(fallback);
                verify(fallback).getAsDouble();
                verifyNoMoreInteractions(operator, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingDoubleBinaryOperator<IOException> operator = Spied.throwingDoubleBinaryOperator((d1, d2) -> throwable.throwUnchecked(d1 + d2));

            DoubleSupplier fallback = Spied.doubleSupplier(() -> 2D);

            DoubleBinaryOperator getting = operator.onErrorGetUnchecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.applyAsDouble(1D, 2D));
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
            ThrowingDoubleBinaryOperator<IOException> operator = Spied.throwingDoubleBinaryOperator(Double::max);

            DoubleBinaryOperator returning = operator.onErrorReturn(2D);

            assertEquals(2D, returning.applyAsDouble(1D, 2D));

            verify(operator).applyAsDouble(1D, 2D);
            verify(operator).onErrorReturn(2D);
            verifyNoMoreInteractions(operator);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingDoubleBinaryOperator<IOException> operator = Spied.throwingDoubleBinaryOperator((d1, d2) -> {
                throw new IOException(Double.toString(d1 + d2));
            });

            DoubleBinaryOperator returning = operator.onErrorReturn(2D);

            assertEquals(2D, returning.applyAsDouble(1D, 2D));

            verify(operator).applyAsDouble(1D, 2D);
            verify(operator).onErrorReturn(2D);
            verifyNoMoreInteractions(operator);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingDoubleBinaryOperator<IOException> operator = Spied.throwingDoubleBinaryOperator((d1, d2) -> throwable.throwUnchecked(d1 + d2));

            DoubleBinaryOperator returning = operator.onErrorReturn(2D);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> returning.applyAsDouble(1D, 2D));
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
            ThrowingDoubleBinaryOperator<IOException> operator = Spied.throwingDoubleBinaryOperator(Double::max);

            DoubleBinaryOperator unchecked = operator.unchecked();

            assertEquals(2D, unchecked.applyAsDouble(1D, 2D));

            verify(operator).applyAsDouble(1D, 2D);
            verify(operator).unchecked();
            verify(operator).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(operator);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingDoubleBinaryOperator<IOException> operator = Spied.throwingDoubleBinaryOperator((d1, d2) -> {
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

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingDoubleBinaryOperator<IOException> operator = Spied.throwingDoubleBinaryOperator((d1, d2) -> throwable.throwUnchecked(d1 + d2));

            DoubleBinaryOperator unchecked = operator.unchecked();

            Throwable thrown = assertThrows(throwable.throwableType(), () -> unchecked.applyAsDouble(1D, 2D));
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
            assertThrows(NullPointerException.class, () -> ThrowingFunction.of(null));
        }

        @Test
        void testNonNullArgument() throws IOException {
            ThrowingDoubleBinaryOperator<IOException> operator = ThrowingDoubleBinaryOperator.of(Double::max);

            assertEquals(2D, operator.applyAsDouble(1D, 2D));
        }
    }

    @Nested
    class UncheckedFactory {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> ThrowingFunction.unchecked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ThrowingDoubleBinaryOperator<IOException> operator = Spied.throwingDoubleBinaryOperator(Double::max);

            DoubleBinaryOperator unchecked = ThrowingDoubleBinaryOperator.unchecked(operator);

            assertEquals(2D, unchecked.applyAsDouble(1D, 2D));

            verify(operator).unchecked();
            verify(operator).onErrorThrowAsUnchecked(any());
            verify(operator).applyAsDouble(1D, 2D);
            verifyNoMoreInteractions(operator);
        }

        @Test
        void testArgumentThrowsChecked() throws IOException {
            ThrowingDoubleBinaryOperator<IOException> operator = Spied.throwingDoubleBinaryOperator((d1, d2) -> {
                throw new IOException(Double.toString(d1 + d2));
            });

            DoubleBinaryOperator unchecked = ThrowingDoubleBinaryOperator.unchecked(operator);

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.applyAsDouble(1D, 2D));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("3.0", cause.getMessage());

            verify(operator).unchecked();
            verify(operator).onErrorThrowAsUnchecked(any());
            verify(operator).applyAsDouble(1D, 2D);
            verifyNoMoreInteractions(operator);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testArgumentThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingDoubleBinaryOperator<IOException> operator = Spied.throwingDoubleBinaryOperator((d1, d2) -> throwable.throwUnchecked(d1 + d2));

            DoubleBinaryOperator unchecked = ThrowingDoubleBinaryOperator.unchecked(operator);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> unchecked.applyAsDouble(1D, 2D));
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
            assertThrows(NullPointerException.class, () -> ThrowingFunction.checked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ThrowingDoubleBinaryOperator<IOException> operator = ThrowingDoubleBinaryOperator.checked(Double::max);

            assertEquals(2D, operator.applyAsDouble(1D, 2D));
        }

        @Test
        void testArgumentThrowsUnchecked() {
            ThrowingDoubleBinaryOperator<IOException> operator = ThrowingDoubleBinaryOperator.checked((d1, d2) -> {
                throw UncheckedException.withoutStackTrace(Double.toString(d1 + d2), new IOException());
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

            assertThrows(NullPointerException.class, () -> ThrowingDoubleBinaryOperator.checked(null, IOException.class));
            assertThrows(NullPointerException.class, () -> ThrowingDoubleBinaryOperator.checked(operator, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ThrowingDoubleBinaryOperator<IOException> operator = ThrowingDoubleBinaryOperator.checked(Double::max, IOException.class);

            assertEquals(2D, operator.applyAsDouble(1D, 2D));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                ThrowingDoubleBinaryOperator<IOException> operator = ThrowingDoubleBinaryOperator.checked((d1, d2) -> {
                    throw UncheckedException.withoutStackTrace(new IOException(Double.toString(d1 + d2)));
                }, IOException.class);

                IOException thrown = assertThrows(IOException.class, () -> operator.applyAsDouble(1D, 2D));
                assertEquals("3.0", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                ThrowingDoubleBinaryOperator<IOException> operator = ThrowingDoubleBinaryOperator.checked((d1, d2) -> {
                    throw UncheckedException.withoutStackTrace(new FileNotFoundException(Double.toString(d1 + d2)));
                }, IOException.class);

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class, () -> operator.applyAsDouble(1D, 2D));
                assertEquals("3.0", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                ThrowingDoubleBinaryOperator<IOException> operator = ThrowingDoubleBinaryOperator.checked((d1, d2) -> {
                    throw UncheckedException.withoutStackTrace(new ParseException(Double.toString(d1 + d2), 0));
                }, IOException.class);

                UncheckedException thrown = assertThrows(UncheckedException.class, () -> operator.applyAsDouble(1D, 2D));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("3.0", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            ThrowingDoubleBinaryOperator<IOException> operator = ThrowingDoubleBinaryOperator.checked((d1, d2) -> {
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

            assertThrows(NullPointerException.class, () -> ThrowingDoubleBinaryOperator.invokeAndUnwrap(null, 1D, 2D, IOException.class));
            assertThrows(NullPointerException.class, () -> ThrowingDoubleBinaryOperator.invokeAndUnwrap(operator, 1D, 2D, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            DoubleBinaryOperator operator = Double::max;

            assertEquals(2D, ThrowingDoubleBinaryOperator.invokeAndUnwrap(operator, 1D, 2D, IOException.class));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                DoubleBinaryOperator operator = (d1, d2) -> {
                    throw UncheckedException.withoutStackTrace(new IOException(Double.toString(d1 + d2)));
                };

                IOException thrown = assertThrows(IOException.class,
                        () -> ThrowingDoubleBinaryOperator.invokeAndUnwrap(operator, 1D, 2D, IOException.class));
                assertEquals("3.0", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                DoubleBinaryOperator operator = (d1, d2) -> {
                    throw UncheckedException.withoutStackTrace(new FileNotFoundException(Double.toString(d1 + d2)));
                };

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class,
                        () -> ThrowingDoubleBinaryOperator.invokeAndUnwrap(operator, 1D, 2D, IOException.class));
                assertEquals("3.0", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                DoubleBinaryOperator operator = (d1, d2) -> {
                    throw UncheckedException.withoutStackTrace(new ParseException(Double.toString(d1 + d2), 0));
                };

                UncheckedException thrown = assertThrows(UncheckedException.class,
                        () -> ThrowingDoubleBinaryOperator.invokeAndUnwrap(operator, 1D, 2D, IOException.class));
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
                    () -> ThrowingDoubleBinaryOperator.invokeAndUnwrap(operator, 1D, 2D, IOException.class));
            assertEquals("3.0", thrown.getMessage());
        }
    }
}
