/*
 * ThrowingIntUnaryOperatorTest.java
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
import java.util.function.Function;
import java.util.function.IntSupplier;
import java.util.function.IntUnaryOperator;
import java.util.function.ToIntFunction;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

@SuppressWarnings("nls")
class ThrowingIntUnaryOperatorTest {

    @Nested
    class OnErrorThrowAsChecked {

        @Test
        void testNullArgument() {
            ThrowingIntUnaryOperator<IOException> operator = Spied.throwingIntUnaryOperator(i -> i + 1);

            assertThrows(NullPointerException.class, () -> operator.onErrorThrowAsChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingIntUnaryOperator<IOException> operator = Spied.throwingIntUnaryOperator(i -> i + 1);

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingIntUnaryOperator<ExecutionException> throwing = operator.onErrorThrowAsChecked(errorMapper);

            assertEquals(2, throwing.applyAsInt(1));

            verify(operator).applyAsInt(1);
            verify(operator).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(operator, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingIntUnaryOperator<IOException> operator = Spied.throwingIntUnaryOperator(i -> {
                throw new IOException(Integer.toString(i));
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingIntUnaryOperator<ExecutionException> throwing = operator.onErrorThrowAsChecked(errorMapper);

            ExecutionException thrown = assertThrows(ExecutionException.class, () -> throwing.applyAsInt(1));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1", cause.getMessage());

            verify(operator).applyAsInt(1);
            verify(operator).onErrorThrowAsChecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(operator, errorMapper);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingIntUnaryOperator<IOException> operator = Spied.throwingIntUnaryOperator(throwable::throwUnchecked);

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingIntUnaryOperator<ExecutionException> throwing = operator.onErrorThrowAsChecked(errorMapper);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> throwing.applyAsInt(1));
            assertEquals("1", thrown.getMessage());

            verify(operator).applyAsInt(1);
            verify(operator).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(operator, errorMapper);
        }
    }

    @Nested
    class OnErrorThrowAsUnchecked {

        @Test
        void testNullArgument() {
            ThrowingIntUnaryOperator<IOException> operator = Spied.throwingIntUnaryOperator(i -> i + 1);

            assertThrows(NullPointerException.class, () -> operator.onErrorThrowAsUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingIntUnaryOperator<IOException> operator = Spied.throwingIntUnaryOperator(i -> i + 1);

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            IntUnaryOperator throwing = operator.onErrorThrowAsUnchecked(errorMapper);

            assertEquals(2, throwing.applyAsInt(1));

            verify(operator).applyAsInt(1);
            verify(operator).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(operator, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingIntUnaryOperator<IOException> operator = Spied.throwingIntUnaryOperator(i -> {
                throw new IOException(Integer.toString(i));
            });

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            IntUnaryOperator throwing = operator.onErrorThrowAsUnchecked(errorMapper);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> throwing.applyAsInt(1));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1", cause.getMessage());

            verify(operator).applyAsInt(1);
            verify(operator).onErrorThrowAsUnchecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(operator, errorMapper);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingIntUnaryOperator<IOException> operator = Spied.throwingIntUnaryOperator(throwable::throwUnchecked);

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            IntUnaryOperator throwing = operator.onErrorThrowAsUnchecked(errorMapper);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> throwing.applyAsInt(1));
            assertEquals("1", thrown.getMessage());

            verify(operator).applyAsInt(1);
            verify(operator).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(operator, errorMapper);
        }
    }

    @Nested
    class OnErrorHandleChecked {

        @Test
        void testNullArgument() {
            ThrowingIntUnaryOperator<IOException> operator = Spied.throwingIntUnaryOperator(i -> i + 1);

            assertThrows(NullPointerException.class, () -> operator.onErrorHandleChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingIntUnaryOperator<IOException> operator = Spied.throwingIntUnaryOperator(i -> i + 1);

            ThrowingToIntFunction<IOException, ExecutionException> errorHandler = Spied.throwingToIntFunction(e -> 0);

            ThrowingIntUnaryOperator<ExecutionException> handling = operator.onErrorHandleChecked(errorHandler);

            assertEquals(2, handling.applyAsInt(1));

            verify(operator).applyAsInt(1);
            verify(operator).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(operator, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException, ExecutionException {
                ThrowingIntUnaryOperator<IOException> operator = Spied.throwingIntUnaryOperator(i -> {
                    throw new IOException(Integer.toString(i));
                });

                ThrowingToIntFunction<IOException, ExecutionException> errorHandler = Spied.throwingToIntFunction(e -> 0);

                ThrowingIntUnaryOperator<ExecutionException> handling = operator.onErrorHandleChecked(errorHandler);

                assertEquals(0, handling.applyAsInt(1));

                verify(operator).applyAsInt(1);
                verify(operator).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsInt(any());
                verifyNoMoreInteractions(operator, errorHandler);
            }

            @Test
            void testHandlerThrowsChecked() throws IOException, ExecutionException {
                ThrowingIntUnaryOperator<IOException> operator = Spied.throwingIntUnaryOperator(i -> {
                    throw new IOException(Integer.toString(i));
                });

                ThrowingToIntFunction<IOException, ExecutionException> errorHandler = Spied.throwingToIntFunction(e -> {
                    throw new ExecutionException(e);
                });

                ThrowingIntUnaryOperator<ExecutionException> handling = operator.onErrorHandleChecked(errorHandler);

                ExecutionException thrown = assertThrows(ExecutionException.class, () -> handling.applyAsInt(1));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("1", cause.getMessage());

                verify(operator).applyAsInt(1);
                verify(operator).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsInt(any());
                verifyNoMoreInteractions(operator, errorHandler);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testHandlerThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ExecutionException {
                ThrowingIntUnaryOperator<IOException> operator = Spied.throwingIntUnaryOperator(i -> {
                    throw new IOException(Integer.toString(i));
                });

                ThrowingToIntFunction<IOException, ExecutionException> errorHandler = Spied.throwingToIntFunction(throwable::throwUnchecked);

                ThrowingIntUnaryOperator<ExecutionException> handling = operator.onErrorHandleChecked(errorHandler);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.applyAsInt(1));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("1", cause.getMessage());

                verify(operator).applyAsInt(1);
                verify(operator).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsInt(any());
                verifyNoMoreInteractions(operator, errorHandler);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingIntUnaryOperator<IOException> operator = Spied.throwingIntUnaryOperator(throwable::throwUnchecked);

            ThrowingToIntFunction<IOException, ExecutionException> errorHandler = Spied.throwingToIntFunction(e -> 0);

            ThrowingIntUnaryOperator<ExecutionException> handling = operator.onErrorHandleChecked(errorHandler);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.applyAsInt(1));
            assertEquals("1", thrown.getMessage());

            verify(operator).applyAsInt(1);
            verify(operator).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(operator, errorHandler);
        }
    }

    @Nested
    class OnErrorHandleUnchecked {

        @Test
        void testNullArgument() {
            ThrowingIntUnaryOperator<IOException> operator = Spied.throwingIntUnaryOperator(i -> i + 1);

            assertThrows(NullPointerException.class, () -> operator.onErrorHandleUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingIntUnaryOperator<IOException> operator = Spied.throwingIntUnaryOperator(i -> i + 1);

            ToIntFunction<IOException> errorHandler = Spied.toIntFunction(e -> 0);

            IntUnaryOperator handling = operator.onErrorHandleUnchecked(errorHandler);

            assertEquals(2, handling.applyAsInt(1));

            verify(operator).applyAsInt(1);
            verify(operator).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(operator, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException {
                ThrowingIntUnaryOperator<IOException> operator = Spied.throwingIntUnaryOperator(i -> {
                    throw new IOException(Integer.toString(i));
                });

                ToIntFunction<IOException> errorHandler = Spied.toIntFunction(e -> 0);

                IntUnaryOperator handling = operator.onErrorHandleUnchecked(errorHandler);

                assertEquals(0, handling.applyAsInt(1));

                verify(operator).applyAsInt(1);
                verify(operator).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).applyAsInt(any());
                verifyNoMoreInteractions(operator, errorHandler);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testHandlerThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingIntUnaryOperator<IOException> operator = Spied.throwingIntUnaryOperator(i -> {
                    throw new IOException(Integer.toString(i));
                });

                ToIntFunction<IOException> errorHandler = Spied.toIntFunction(throwable::throwUnchecked);

                IntUnaryOperator handling = operator.onErrorHandleUnchecked(errorHandler);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.applyAsInt(1));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("1", cause.getMessage());

                verify(operator).applyAsInt(1);
                verify(operator).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).applyAsInt(any());
                verifyNoMoreInteractions(operator, errorHandler);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingIntUnaryOperator<IOException> operator = Spied.throwingIntUnaryOperator(throwable::throwUnchecked);

            ToIntFunction<IOException> errorHandler = Spied.toIntFunction(e -> 0);

            IntUnaryOperator handling = operator.onErrorHandleUnchecked(errorHandler);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.applyAsInt(1));
            assertEquals("1", thrown.getMessage());

            verify(operator).applyAsInt(1);
            verify(operator).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(operator, errorHandler);
        }
    }

    @Nested
    class OnErrorApplyChecked {

        @Test
        void testNullArgument() {
            ThrowingIntUnaryOperator<IOException> operator = Spied.throwingIntUnaryOperator(i -> i + 1);

            assertThrows(NullPointerException.class, () -> operator.onErrorApplyChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingIntUnaryOperator<IOException> operator = Spied.throwingIntUnaryOperator(i -> i + 1);

            ThrowingIntUnaryOperator<ExecutionException> fallback = Spied.throwingIntUnaryOperator(i -> i + i);

            ThrowingIntUnaryOperator<ExecutionException> applying = operator.onErrorApplyChecked(fallback);

            assertEquals(2, applying.applyAsInt(1));

            verify(operator).applyAsInt(1);
            verify(operator).onErrorApplyChecked(fallback);
            verifyNoMoreInteractions(operator, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                ThrowingIntUnaryOperator<IOException> operator = Spied.throwingIntUnaryOperator(i -> {
                    throw new IOException(Integer.toString(i));
                });

                ThrowingIntUnaryOperator<ParseException> fallback = Spied.throwingIntUnaryOperator(i -> i + i);

                ThrowingIntUnaryOperator<ParseException> applying = operator.onErrorApplyChecked(fallback);

                assertEquals(2, applying.applyAsInt(1));

                verify(operator).applyAsInt(1);
                verify(operator).onErrorApplyChecked(fallback);
                verify(fallback).applyAsInt(1);
                verifyNoMoreInteractions(operator, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                ThrowingIntUnaryOperator<IOException> operator = Spied.throwingIntUnaryOperator(i -> {
                    throw new IOException(Integer.toString(i));
                });

                ThrowingIntUnaryOperator<ParseException> fallback = Spied.throwingIntUnaryOperator(i -> {
                    throw new ParseException(Integer.toString(i), 0);
                });

                ThrowingIntUnaryOperator<ParseException> applying = operator.onErrorApplyChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> applying.applyAsInt(1));
                assertEquals("1", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(operator).applyAsInt(1);
                verify(operator).onErrorApplyChecked(fallback);
                verify(fallback).applyAsInt(1);
                verifyNoMoreInteractions(operator, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ParseException {
                ThrowingIntUnaryOperator<IOException> operator = Spied.throwingIntUnaryOperator(i -> {
                    throw new IOException(Integer.toString(i));
                });

                ThrowingIntUnaryOperator<ParseException> fallback = Spied.throwingIntUnaryOperator(throwable::throwUnchecked);

                ThrowingIntUnaryOperator<ParseException> applying = operator.onErrorApplyChecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.applyAsInt(1));
                assertEquals("1", thrown.getMessage());

                verify(operator).applyAsInt(1);
                verify(operator).onErrorApplyChecked(fallback);
                verify(fallback).applyAsInt(1);
                verifyNoMoreInteractions(operator, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingIntUnaryOperator<IOException> operator = Spied.throwingIntUnaryOperator(throwable::throwUnchecked);

            ThrowingIntUnaryOperator<ParseException> fallback = Spied.throwingIntUnaryOperator(i -> i + i);

            ThrowingIntUnaryOperator<ParseException> applying = operator.onErrorApplyChecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.applyAsInt(1));
            assertEquals("1", thrown.getMessage());

            verify(operator).applyAsInt(1);
            verify(operator).onErrorApplyChecked(fallback);
            verifyNoMoreInteractions(operator, fallback);
        }
    }

    @Nested
    class OnErrorApplyUnchecked {

        @Test
        void testNullArgument() {
            ThrowingIntUnaryOperator<IOException> operator = Spied.throwingIntUnaryOperator(i -> i + 1);

            assertThrows(NullPointerException.class, () -> operator.onErrorApplyUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingIntUnaryOperator<IOException> operator = Spied.throwingIntUnaryOperator(i -> i + 1);

            IntUnaryOperator fallback = Spied.intUnaryOperator(i -> i + i);

            IntUnaryOperator applying = operator.onErrorApplyUnchecked(fallback);

            assertEquals(2, applying.applyAsInt(1));

            verify(operator).applyAsInt(1);
            verify(operator).onErrorApplyUnchecked(fallback);
            verifyNoMoreInteractions(operator, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                ThrowingIntUnaryOperator<IOException> operator = Spied.throwingIntUnaryOperator(i -> {
                    throw new IOException(Integer.toString(i));
                });

                IntUnaryOperator fallback = Spied.intUnaryOperator(i -> i + i);

                IntUnaryOperator applying = operator.onErrorApplyUnchecked(fallback);

                assertEquals(2, applying.applyAsInt(1));

                verify(operator).applyAsInt(1);
                verify(operator).onErrorApplyUnchecked(fallback);
                verify(fallback).applyAsInt(1);
                verifyNoMoreInteractions(operator, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingIntUnaryOperator<IOException> operator = Spied.throwingIntUnaryOperator(i -> {
                    throw new IOException(Integer.toString(i));
                });

                IntUnaryOperator fallback = Spied.intUnaryOperator(throwable::throwUnchecked);

                IntUnaryOperator applying = operator.onErrorApplyUnchecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.applyAsInt(1));
                assertEquals("1", thrown.getMessage());

                verify(operator).applyAsInt(1);
                verify(operator).onErrorApplyUnchecked(fallback);
                verify(fallback).applyAsInt(1);
                verifyNoMoreInteractions(operator, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingIntUnaryOperator<IOException> operator = Spied.throwingIntUnaryOperator(throwable::throwUnchecked);

            IntUnaryOperator fallback = Spied.intUnaryOperator(i -> i + i);

            IntUnaryOperator applying = operator.onErrorApplyUnchecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.applyAsInt(1));
            assertEquals("1", thrown.getMessage());

            verify(operator).applyAsInt(1);
            verify(operator).onErrorApplyUnchecked(fallback);
            verifyNoMoreInteractions(operator, fallback);
        }
    }

    @Nested
    class OnErrorGetChecked {

        @Test
        void testNullArgument() {
            ThrowingIntUnaryOperator<IOException> operator = Spied.throwingIntUnaryOperator(i -> i + 1);

            assertThrows(NullPointerException.class, () -> operator.onErrorGetChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingIntUnaryOperator<IOException> operator = Spied.throwingIntUnaryOperator(i -> i + 1);

            ThrowingIntSupplier<ExecutionException> fallback = Spied.throwingIntSupplier(() -> 0);

            ThrowingIntUnaryOperator<ExecutionException> getting = operator.onErrorGetChecked(fallback);

            assertEquals(2, getting.applyAsInt(1));

            verify(operator).applyAsInt(1);
            verify(operator).onErrorGetChecked(fallback);
            verifyNoMoreInteractions(operator, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                ThrowingIntUnaryOperator<IOException> operator = Spied.throwingIntUnaryOperator(i -> {
                    throw new IOException(Integer.toString(i));
                });

                ThrowingIntSupplier<ParseException> fallback = Spied.throwingIntSupplier(() -> 0);

                ThrowingIntUnaryOperator<ParseException> getting = operator.onErrorGetChecked(fallback);

                assertEquals(0, getting.applyAsInt(1));

                verify(operator).applyAsInt(1);
                verify(operator).onErrorGetChecked(fallback);
                verify(fallback).getAsInt();
                verifyNoMoreInteractions(operator, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                ThrowingIntUnaryOperator<IOException> operator = Spied.throwingIntUnaryOperator(i -> {
                    throw new IOException(Integer.toString(i));
                });

                ThrowingIntSupplier<ParseException> fallback = Spied.throwingIntSupplier(() -> {
                    throw new ParseException("bar", 0);
                });

                ThrowingIntUnaryOperator<ParseException> getting = operator.onErrorGetChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> getting.applyAsInt(1));
                assertEquals("bar", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(operator).applyAsInt(1);
                verify(operator).onErrorGetChecked(fallback);
                verify(fallback).getAsInt();
                verifyNoMoreInteractions(operator, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ParseException {
                ThrowingIntUnaryOperator<IOException> operator = Spied.throwingIntUnaryOperator(i -> {
                    throw new IOException(Integer.toString(i));
                });

                ThrowingIntSupplier<ParseException> fallback = Spied.throwingIntSupplier(() -> throwable.throwUnchecked("bar"));

                ThrowingIntUnaryOperator<ParseException> getting = operator.onErrorGetChecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.applyAsInt(1));
                assertEquals("bar", thrown.getMessage());

                verify(operator).applyAsInt(1);
                verify(operator).onErrorGetChecked(fallback);
                verify(fallback).getAsInt();
                verifyNoMoreInteractions(operator, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingIntUnaryOperator<IOException> operator = Spied.throwingIntUnaryOperator(throwable::throwUnchecked);

            ThrowingIntSupplier<ParseException> fallback = Spied.throwingIntSupplier(() -> 0);

            ThrowingIntUnaryOperator<ParseException> getting = operator.onErrorGetChecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.applyAsInt(1));
            assertEquals("1", thrown.getMessage());

            verify(operator).applyAsInt(1);
            verify(operator).onErrorGetChecked(fallback);
            verifyNoMoreInteractions(operator, fallback);
        }
    }

    @Nested
    class OnErrorGetUnchecked {

        @Test
        void testNullArgument() {
            ThrowingIntUnaryOperator<IOException> operator = Spied.throwingIntUnaryOperator(i -> i + 1);

            assertThrows(NullPointerException.class, () -> operator.onErrorGetUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingIntUnaryOperator<IOException> operator = Spied.throwingIntUnaryOperator(i -> i + 1);

            IntSupplier fallback = Spied.intSupplier(() -> 2);

            IntUnaryOperator getting = operator.onErrorGetUnchecked(fallback);

            assertEquals(2, getting.applyAsInt(1));

            verify(operator).applyAsInt(1);
            verify(operator).onErrorGetUnchecked(fallback);
            verifyNoMoreInteractions(operator, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                ThrowingIntUnaryOperator<IOException> operator = Spied.throwingIntUnaryOperator(i -> {
                    throw new IOException(Integer.toString(i));
                });

                IntSupplier fallback = Spied.intSupplier(() -> 2);

                IntUnaryOperator getting = operator.onErrorGetUnchecked(fallback);

                assertEquals(2, getting.applyAsInt(1));

                verify(operator).applyAsInt(1);
                verify(operator).onErrorGetUnchecked(fallback);
                verify(fallback).getAsInt();
                verifyNoMoreInteractions(operator, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingIntUnaryOperator<IOException> operator = Spied.throwingIntUnaryOperator(i -> {
                    throw new IOException(Integer.toString(i));
                });

                IntSupplier fallback = Spied.intSupplier(() -> throwable.throwUnchecked("bar"));

                IntUnaryOperator getting = operator.onErrorGetUnchecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.applyAsInt(1));
                assertEquals("bar", thrown.getMessage());

                verify(operator).applyAsInt(1);
                verify(operator).onErrorGetUnchecked(fallback);
                verify(fallback).getAsInt();
                verifyNoMoreInteractions(operator, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingIntUnaryOperator<IOException> operator = Spied.throwingIntUnaryOperator(throwable::throwUnchecked);

            IntSupplier fallback = Spied.intSupplier(() -> 2);

            IntUnaryOperator getting = operator.onErrorGetUnchecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.applyAsInt(1));
            assertEquals("1", thrown.getMessage());

            verify(operator).applyAsInt(1);
            verify(operator).onErrorGetUnchecked(fallback);
            verifyNoMoreInteractions(operator, fallback);
        }
    }

    @Nested
    class OnErrorReturn {

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingIntUnaryOperator<IOException> operator = Spied.throwingIntUnaryOperator(i -> i + 1);

            IntUnaryOperator returning = operator.onErrorReturn(2);

            assertEquals(2, returning.applyAsInt(1));

            verify(operator).applyAsInt(1);
            verify(operator).onErrorReturn(2);
            verifyNoMoreInteractions(operator);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingIntUnaryOperator<IOException> operator = Spied.throwingIntUnaryOperator(i -> {
                throw new IOException(Integer.toString(i));
            });

            IntUnaryOperator returning = operator.onErrorReturn(2);

            assertEquals(2, returning.applyAsInt(1));

            verify(operator).applyAsInt(1);
            verify(operator).onErrorReturn(2);
            verifyNoMoreInteractions(operator);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingIntUnaryOperator<IOException> operator = Spied.throwingIntUnaryOperator(throwable::throwUnchecked);

            IntUnaryOperator returning = operator.onErrorReturn(2);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> returning.applyAsInt(1));
            assertEquals("1", thrown.getMessage());

            verify(operator).applyAsInt(1);
            verify(operator).onErrorReturn(2);
            verifyNoMoreInteractions(operator);
        }
    }

    @Nested
    class Unchecked {

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingIntUnaryOperator<IOException> operator = Spied.throwingIntUnaryOperator(i -> i + 1);

            IntUnaryOperator unchecked = operator.unchecked();

            assertEquals(2, unchecked.applyAsInt(1));

            verify(operator).applyAsInt(1);
            verify(operator).unchecked();
            verify(operator).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(operator);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingIntUnaryOperator<IOException> operator = Spied.throwingIntUnaryOperator(i -> {
                throw new IOException(Integer.toString(i));
            });

            IntUnaryOperator unchecked = operator.unchecked();

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.applyAsInt(1));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1", cause.getMessage());

            verify(operator).applyAsInt(1);
            verify(operator).unchecked();
            verify(operator).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(operator);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingIntUnaryOperator<IOException> operator = Spied.throwingIntUnaryOperator(throwable::throwUnchecked);

            IntUnaryOperator unchecked = operator.unchecked();

            Throwable thrown = assertThrows(throwable.throwableType(), () -> unchecked.applyAsInt(1));
            assertEquals("1", thrown.getMessage());

            verify(operator).applyAsInt(1);
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
            ThrowingIntUnaryOperator<IOException> operator = ThrowingIntUnaryOperator.of(i -> i + 1);

            assertEquals(2, operator.applyAsInt(1));
        }
    }

    @Test
    void testIdentity() throws IOException {
        ThrowingIntUnaryOperator<IOException> operator = ThrowingIntUnaryOperator.identity();

        assertEquals(1, operator.applyAsInt(1));
    }

    @Nested
    class UncheckedFactory {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> ThrowingFunction.unchecked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ThrowingIntUnaryOperator<IOException> operator = Spied.throwingIntUnaryOperator(i -> i + 1);

            IntUnaryOperator unchecked = ThrowingIntUnaryOperator.unchecked(operator);

            assertEquals(2, unchecked.applyAsInt(1));

            verify(operator).unchecked();
            verify(operator).onErrorThrowAsUnchecked(any());
            verify(operator).applyAsInt(1);
            verifyNoMoreInteractions(operator);
        }

        @Test
        void testArgumentThrowsChecked() throws IOException {
            ThrowingIntUnaryOperator<IOException> operator = Spied.throwingIntUnaryOperator(i -> {
                throw new IOException(Integer.toString(i));
            });

            IntUnaryOperator unchecked = ThrowingIntUnaryOperator.unchecked(operator);

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.applyAsInt(1));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1", cause.getMessage());

            verify(operator).unchecked();
            verify(operator).onErrorThrowAsUnchecked(any());
            verify(operator).applyAsInt(1);
            verifyNoMoreInteractions(operator);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testArgumentThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingIntUnaryOperator<IOException> operator = Spied.throwingIntUnaryOperator(throwable::throwUnchecked);

            IntUnaryOperator unchecked = ThrowingIntUnaryOperator.unchecked(operator);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> unchecked.applyAsInt(1));
            assertEquals("1", thrown.getMessage());

            verify(operator).unchecked();
            verify(operator).onErrorThrowAsUnchecked(any());
            verify(operator).applyAsInt(1);
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
            ThrowingIntUnaryOperator<IOException> operator = ThrowingIntUnaryOperator.checked(i -> i + 1);

            assertEquals(2, operator.applyAsInt(1));
        }

        @Test
        void testArgumentThrowsUnchecked() {
            ThrowingIntUnaryOperator<IOException> operator = ThrowingIntUnaryOperator.checked(i -> {
                throw new UncheckedException(Integer.toString(i), new IOException());
            });

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> operator.applyAsInt(1));
            assertEquals("1", thrown.getMessage());
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertNull(cause.getMessage());
        }
    }

    @Nested
    class CheckedUnwrapper {

        @Test
        void testNullArguments() {
            IntUnaryOperator operator = i -> i + 1;

            assertThrows(NullPointerException.class, () -> ThrowingIntUnaryOperator.checked(null, IOException.class));
            assertThrows(NullPointerException.class, () -> ThrowingIntUnaryOperator.checked(operator, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ThrowingIntUnaryOperator<IOException> operator = ThrowingIntUnaryOperator.checked(i -> i + 1, IOException.class);

            assertEquals(2, operator.applyAsInt(1));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                ThrowingIntUnaryOperator<IOException> operator = ThrowingIntUnaryOperator.checked(i -> {
                    throw new UncheckedException(new IOException(Integer.toString(i)));
                }, IOException.class);

                IOException thrown = assertThrows(IOException.class, () -> operator.applyAsInt(1));
                assertEquals("1", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                ThrowingIntUnaryOperator<IOException> operator = ThrowingIntUnaryOperator.checked(i -> {
                    throw new UncheckedException(new FileNotFoundException(Integer.toString(i)));
                }, IOException.class);

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class, () -> operator.applyAsInt(1));
                assertEquals("1", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                ThrowingIntUnaryOperator<IOException> operator = ThrowingIntUnaryOperator.checked(i -> {
                    throw new UncheckedException(new ParseException(Integer.toString(i), 0));
                }, IOException.class);

                UncheckedException thrown = assertThrows(UncheckedException.class, () -> operator.applyAsInt(1));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("1", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            ThrowingIntUnaryOperator<IOException> operator = ThrowingIntUnaryOperator.checked(i -> {
                throw new IllegalStateException(Integer.toString(i));
            }, IOException.class);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> operator.applyAsInt(1));
            assertEquals("1", thrown.getMessage());
        }
    }

    @Nested
    class InvokeAndUnwrap {

        @Test
        void testNullArguments() {
            IntUnaryOperator operator = i -> i + 1;

            assertThrows(NullPointerException.class, () -> ThrowingIntUnaryOperator.invokeAndUnwrap(null, 1, IOException.class));
            assertThrows(NullPointerException.class, () -> ThrowingIntUnaryOperator.invokeAndUnwrap(operator, 1, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            IntUnaryOperator operator = i -> i + 1;

            assertEquals(2, ThrowingIntUnaryOperator.invokeAndUnwrap(operator, 1, IOException.class));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                IntUnaryOperator operator = i -> {
                    throw new UncheckedException(new IOException(Integer.toString(i)));
                };

                IOException thrown = assertThrows(IOException.class,
                        () -> ThrowingIntUnaryOperator.invokeAndUnwrap(operator, 1, IOException.class));
                assertEquals("1", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                IntUnaryOperator operator = i -> {
                    throw new UncheckedException(new FileNotFoundException(Integer.toString(i)));
                };

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class,
                        () -> ThrowingIntUnaryOperator.invokeAndUnwrap(operator, 1, IOException.class));
                assertEquals("1", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                IntUnaryOperator operator = i -> {
                    throw new UncheckedException(new ParseException(Integer.toString(i), 0));
                };

                UncheckedException thrown = assertThrows(UncheckedException.class,
                        () -> ThrowingIntUnaryOperator.invokeAndUnwrap(operator, 1, IOException.class));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("1", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            IntUnaryOperator operator = i -> {
                throw new IllegalStateException(Integer.toString(i));
            };

            IllegalStateException thrown = assertThrows(IllegalStateException.class,
                    () -> ThrowingIntUnaryOperator.invokeAndUnwrap(operator, 1, IOException.class));
            assertEquals("1", thrown.getMessage());
        }
    }
}
