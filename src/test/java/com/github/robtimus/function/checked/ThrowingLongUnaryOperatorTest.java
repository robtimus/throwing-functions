/*
 * ThrowingLongUnaryOperatorTest.java
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
import java.util.function.LongSupplier;
import java.util.function.LongUnaryOperator;
import java.util.function.ToLongFunction;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

@SuppressWarnings("nls")
class ThrowingLongUnaryOperatorTest {

    @Nested
    class OnErrorThrowAsChecked {

        @Test
        void testNullArgument() {
            ThrowingLongUnaryOperator<IOException> operator = Spied.checkedLongUnaryOperator(l -> l + 1);

            assertThrows(NullPointerException.class, () -> operator.onErrorThrowAsChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingLongUnaryOperator<IOException> operator = Spied.checkedLongUnaryOperator(l -> l + 1);

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingLongUnaryOperator<ExecutionException> throwing = operator.onErrorThrowAsChecked(errorMapper);

            assertEquals(2L, throwing.applyAsLong(1L));

            verify(operator).applyAsLong(1L);
            verify(operator).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(operator, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingLongUnaryOperator<IOException> operator = Spied.checkedLongUnaryOperator(l -> {
                throw new IOException(Long.toString(l));
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingLongUnaryOperator<ExecutionException> throwing = operator.onErrorThrowAsChecked(errorMapper);

            ExecutionException thrown = assertThrows(ExecutionException.class, () -> throwing.applyAsLong(1L));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1", cause.getMessage());

            verify(operator).applyAsLong(1L);
            verify(operator).onErrorThrowAsChecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(operator, errorMapper);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingLongUnaryOperator<IOException> operator = Spied.checkedLongUnaryOperator(throwable::throwUnchecked);

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingLongUnaryOperator<ExecutionException> throwing = operator.onErrorThrowAsChecked(errorMapper);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> throwing.applyAsLong(1L));
            assertEquals("1", thrown.getMessage());

            verify(operator).applyAsLong(1L);
            verify(operator).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(operator, errorMapper);
        }
    }

    @Nested
    class OnErrorThrowAsUnchecked {

        @Test
        void testNullArgument() {
            ThrowingLongUnaryOperator<IOException> operator = Spied.checkedLongUnaryOperator(l -> l + 1);

            assertThrows(NullPointerException.class, () -> operator.onErrorThrowAsUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingLongUnaryOperator<IOException> operator = Spied.checkedLongUnaryOperator(l -> l + 1);

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            LongUnaryOperator throwing = operator.onErrorThrowAsUnchecked(errorMapper);

            assertEquals(2L, throwing.applyAsLong(1L));

            verify(operator).applyAsLong(1L);
            verify(operator).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(operator, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingLongUnaryOperator<IOException> operator = Spied.checkedLongUnaryOperator(l -> {
                throw new IOException(Long.toString(l));
            });

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            LongUnaryOperator throwing = operator.onErrorThrowAsUnchecked(errorMapper);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> throwing.applyAsLong(1L));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1", cause.getMessage());

            verify(operator).applyAsLong(1L);
            verify(operator).onErrorThrowAsUnchecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(operator, errorMapper);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingLongUnaryOperator<IOException> operator = Spied.checkedLongUnaryOperator(throwable::throwUnchecked);

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            LongUnaryOperator throwing = operator.onErrorThrowAsUnchecked(errorMapper);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> throwing.applyAsLong(1L));
            assertEquals("1", thrown.getMessage());

            verify(operator).applyAsLong(1L);
            verify(operator).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(operator, errorMapper);
        }
    }

    @Nested
    class OnErrorHandleChecked {

        @Test
        void testNullArgument() {
            ThrowingLongUnaryOperator<IOException> operator = Spied.checkedLongUnaryOperator(l -> l + 1);

            assertThrows(NullPointerException.class, () -> operator.onErrorHandleChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingLongUnaryOperator<IOException> operator = Spied.checkedLongUnaryOperator(l -> l + 1);

            ThrowingToLongFunction<IOException, ExecutionException> errorHandler = Spied.checkedToLongFunction(e -> 0L);

            ThrowingLongUnaryOperator<ExecutionException> handling = operator.onErrorHandleChecked(errorHandler);

            assertEquals(2L, handling.applyAsLong(1L));

            verify(operator).applyAsLong(1L);
            verify(operator).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(operator, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException, ExecutionException {
                ThrowingLongUnaryOperator<IOException> operator = Spied.checkedLongUnaryOperator(l -> {
                    throw new IOException(Long.toString(l));
                });

                ThrowingToLongFunction<IOException, ExecutionException> errorHandler = Spied.checkedToLongFunction(e -> 0L);

                ThrowingLongUnaryOperator<ExecutionException> handling = operator.onErrorHandleChecked(errorHandler);

                assertEquals(0L, handling.applyAsLong(1L));

                verify(operator).applyAsLong(1L);
                verify(operator).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsLong(any());
                verifyNoMoreInteractions(operator, errorHandler);
            }

            @Test
            void testHandlerThrowsChecked() throws IOException, ExecutionException {
                ThrowingLongUnaryOperator<IOException> operator = Spied.checkedLongUnaryOperator(l -> {
                    throw new IOException(Long.toString(l));
                });

                ThrowingToLongFunction<IOException, ExecutionException> errorHandler = Spied.checkedToLongFunction(e -> {
                    throw new ExecutionException(e);
                });

                ThrowingLongUnaryOperator<ExecutionException> handling = operator.onErrorHandleChecked(errorHandler);

                ExecutionException thrown = assertThrows(ExecutionException.class, () -> handling.applyAsLong(1L));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("1", cause.getMessage());

                verify(operator).applyAsLong(1L);
                verify(operator).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsLong(any());
                verifyNoMoreInteractions(operator, errorHandler);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testHandlerThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ExecutionException {
                ThrowingLongUnaryOperator<IOException> operator = Spied.checkedLongUnaryOperator(l -> {
                    throw new IOException(Long.toString(l));
                });

                ThrowingToLongFunction<IOException, ExecutionException> errorHandler = Spied.checkedToLongFunction(throwable::throwUnchecked);

                ThrowingLongUnaryOperator<ExecutionException> handling = operator.onErrorHandleChecked(errorHandler);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.applyAsLong(1L));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("1", cause.getMessage());

                verify(operator).applyAsLong(1L);
                verify(operator).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsLong(any());
                verifyNoMoreInteractions(operator, errorHandler);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingLongUnaryOperator<IOException> operator = Spied.checkedLongUnaryOperator(throwable::throwUnchecked);

            ThrowingToLongFunction<IOException, ExecutionException> errorHandler = Spied.checkedToLongFunction(e -> 0L);

            ThrowingLongUnaryOperator<ExecutionException> handling = operator.onErrorHandleChecked(errorHandler);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.applyAsLong(1L));
            assertEquals("1", thrown.getMessage());

            verify(operator).applyAsLong(1L);
            verify(operator).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(operator, errorHandler);
        }
    }

    @Nested
    class OnErrorHandleUnchecked {

        @Test
        void testNullArgument() {
            ThrowingLongUnaryOperator<IOException> operator = Spied.checkedLongUnaryOperator(l -> l + 1);

            assertThrows(NullPointerException.class, () -> operator.onErrorHandleUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingLongUnaryOperator<IOException> operator = Spied.checkedLongUnaryOperator(l -> l + 1);

            ToLongFunction<IOException> errorHandler = Spied.toLongFunction(e -> 0L);

            LongUnaryOperator handling = operator.onErrorHandleUnchecked(errorHandler);

            assertEquals(2L, handling.applyAsLong(1L));

            verify(operator).applyAsLong(1L);
            verify(operator).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(operator, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException {
                ThrowingLongUnaryOperator<IOException> operator = Spied.checkedLongUnaryOperator(l -> {
                    throw new IOException(Long.toString(l));
                });

                ToLongFunction<IOException> errorHandler = Spied.toLongFunction(e -> 0L);

                LongUnaryOperator handling = operator.onErrorHandleUnchecked(errorHandler);

                assertEquals(0L, handling.applyAsLong(1L));

                verify(operator).applyAsLong(1L);
                verify(operator).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).applyAsLong(any());
                verifyNoMoreInteractions(operator, errorHandler);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testHandlerThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingLongUnaryOperator<IOException> operator = Spied.checkedLongUnaryOperator(l -> {
                    throw new IOException(Long.toString(l));
                });

                ToLongFunction<IOException> errorHandler = Spied.toLongFunction(throwable::throwUnchecked);

                LongUnaryOperator handling = operator.onErrorHandleUnchecked(errorHandler);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.applyAsLong(1L));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("1", cause.getMessage());

                verify(operator).applyAsLong(1L);
                verify(operator).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).applyAsLong(any());
                verifyNoMoreInteractions(operator, errorHandler);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingLongUnaryOperator<IOException> operator = Spied.checkedLongUnaryOperator(throwable::throwUnchecked);

            ToLongFunction<IOException> errorHandler = Spied.toLongFunction(e -> 0L);

            LongUnaryOperator handling = operator.onErrorHandleUnchecked(errorHandler);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.applyAsLong(1L));
            assertEquals("1", thrown.getMessage());

            verify(operator).applyAsLong(1L);
            verify(operator).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(operator, errorHandler);
        }
    }

    @Nested
    class OnErrorApplyChecked {

        @Test
        void testNullArgument() {
            ThrowingLongUnaryOperator<IOException> operator = Spied.checkedLongUnaryOperator(l -> l + 1);

            assertThrows(NullPointerException.class, () -> operator.onErrorApplyChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingLongUnaryOperator<IOException> operator = Spied.checkedLongUnaryOperator(l -> l + 1);

            ThrowingLongUnaryOperator<ExecutionException> fallback = Spied.checkedLongUnaryOperator(l -> l + l);

            ThrowingLongUnaryOperator<ExecutionException> applying = operator.onErrorApplyChecked(fallback);

            assertEquals(2L, applying.applyAsLong(1L));

            verify(operator).applyAsLong(1L);
            verify(operator).onErrorApplyChecked(fallback);
            verifyNoMoreInteractions(operator, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                ThrowingLongUnaryOperator<IOException> operator = Spied.checkedLongUnaryOperator(l -> {
                    throw new IOException(Long.toString(l));
                });

                ThrowingLongUnaryOperator<ParseException> fallback = Spied.checkedLongUnaryOperator(l -> l + l);

                ThrowingLongUnaryOperator<ParseException> applying = operator.onErrorApplyChecked(fallback);

                assertEquals(2L, applying.applyAsLong(1L));

                verify(operator).applyAsLong(1L);
                verify(operator).onErrorApplyChecked(fallback);
                verify(fallback).applyAsLong(1L);
                verifyNoMoreInteractions(operator, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                ThrowingLongUnaryOperator<IOException> operator = Spied.checkedLongUnaryOperator(l -> {
                    throw new IOException(Long.toString(l));
                });

                ThrowingLongUnaryOperator<ParseException> fallback = Spied.checkedLongUnaryOperator(l -> {
                    throw new ParseException(Long.toString(l), 0);
                });

                ThrowingLongUnaryOperator<ParseException> applying = operator.onErrorApplyChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> applying.applyAsLong(1L));
                assertEquals("1", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(operator).applyAsLong(1L);
                verify(operator).onErrorApplyChecked(fallback);
                verify(fallback).applyAsLong(1L);
                verifyNoMoreInteractions(operator, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ParseException {
                ThrowingLongUnaryOperator<IOException> operator = Spied.checkedLongUnaryOperator(l -> {
                    throw new IOException(Long.toString(l));
                });

                ThrowingLongUnaryOperator<ParseException> fallback = Spied.checkedLongUnaryOperator(throwable::throwUnchecked);

                ThrowingLongUnaryOperator<ParseException> applying = operator.onErrorApplyChecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.applyAsLong(1L));
                assertEquals("1", thrown.getMessage());

                verify(operator).applyAsLong(1L);
                verify(operator).onErrorApplyChecked(fallback);
                verify(fallback).applyAsLong(1L);
                verifyNoMoreInteractions(operator, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingLongUnaryOperator<IOException> operator = Spied.checkedLongUnaryOperator(throwable::throwUnchecked);

            ThrowingLongUnaryOperator<ParseException> fallback = Spied.checkedLongUnaryOperator(l -> l + l);

            ThrowingLongUnaryOperator<ParseException> applying = operator.onErrorApplyChecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.applyAsLong(1L));
            assertEquals("1", thrown.getMessage());

            verify(operator).applyAsLong(1L);
            verify(operator).onErrorApplyChecked(fallback);
            verifyNoMoreInteractions(operator, fallback);
        }
    }

    @Nested
    class OnErrorApplyUnchecked {

        @Test
        void testNullArgument() {
            ThrowingLongUnaryOperator<IOException> operator = Spied.checkedLongUnaryOperator(l -> l + 1);

            assertThrows(NullPointerException.class, () -> operator.onErrorApplyUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingLongUnaryOperator<IOException> operator = Spied.checkedLongUnaryOperator(l -> l + 1);

            LongUnaryOperator fallback = Spied.longUnaryOperator(l -> l + l);

            LongUnaryOperator applying = operator.onErrorApplyUnchecked(fallback);

            assertEquals(2L, applying.applyAsLong(1L));

            verify(operator).applyAsLong(1L);
            verify(operator).onErrorApplyUnchecked(fallback);
            verifyNoMoreInteractions(operator, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                ThrowingLongUnaryOperator<IOException> operator = Spied.checkedLongUnaryOperator(l -> {
                    throw new IOException(Long.toString(l));
                });

                LongUnaryOperator fallback = Spied.longUnaryOperator(l -> l + l);

                LongUnaryOperator applying = operator.onErrorApplyUnchecked(fallback);

                assertEquals(2L, applying.applyAsLong(1L));

                verify(operator).applyAsLong(1L);
                verify(operator).onErrorApplyUnchecked(fallback);
                verify(fallback).applyAsLong(1L);
                verifyNoMoreInteractions(operator, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingLongUnaryOperator<IOException> operator = Spied.checkedLongUnaryOperator(l -> {
                    throw new IOException(Long.toString(l));
                });

                LongUnaryOperator fallback = Spied.longUnaryOperator(throwable::throwUnchecked);

                LongUnaryOperator applying = operator.onErrorApplyUnchecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.applyAsLong(1L));
                assertEquals("1", thrown.getMessage());

                verify(operator).applyAsLong(1L);
                verify(operator).onErrorApplyUnchecked(fallback);
                verify(fallback).applyAsLong(1L);
                verifyNoMoreInteractions(operator, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingLongUnaryOperator<IOException> operator = Spied.checkedLongUnaryOperator(throwable::throwUnchecked);

            LongUnaryOperator fallback = Spied.longUnaryOperator(l -> l + l);

            LongUnaryOperator applying = operator.onErrorApplyUnchecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.applyAsLong(1L));
            assertEquals("1", thrown.getMessage());

            verify(operator).applyAsLong(1L);
            verify(operator).onErrorApplyUnchecked(fallback);
            verifyNoMoreInteractions(operator, fallback);
        }
    }

    @Nested
    class OnErrorGetChecked {

        @Test
        void testNullArgument() {
            ThrowingLongUnaryOperator<IOException> operator = Spied.checkedLongUnaryOperator(l -> l + 1);

            assertThrows(NullPointerException.class, () -> operator.onErrorGetChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingLongUnaryOperator<IOException> operator = Spied.checkedLongUnaryOperator(l -> l + 1);

            ThrowingLongSupplier<ExecutionException> fallback = Spied.checkedLongSupplier(() -> 0L);

            ThrowingLongUnaryOperator<ExecutionException> getting = operator.onErrorGetChecked(fallback);

            assertEquals(2L, getting.applyAsLong(1L));

            verify(operator).applyAsLong(1L);
            verify(operator).onErrorGetChecked(fallback);
            verifyNoMoreInteractions(operator, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                ThrowingLongUnaryOperator<IOException> operator = Spied.checkedLongUnaryOperator(l -> {
                    throw new IOException(Long.toString(l));
                });

                ThrowingLongSupplier<ParseException> fallback = Spied.checkedLongSupplier(() -> 0L);

                ThrowingLongUnaryOperator<ParseException> getting = operator.onErrorGetChecked(fallback);

                assertEquals(0L, getting.applyAsLong(1L));

                verify(operator).applyAsLong(1L);
                verify(operator).onErrorGetChecked(fallback);
                verify(fallback).getAsLong();
                verifyNoMoreInteractions(operator, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                ThrowingLongUnaryOperator<IOException> operator = Spied.checkedLongUnaryOperator(l -> {
                    throw new IOException(Long.toString(l));
                });

                ThrowingLongSupplier<ParseException> fallback = Spied.checkedLongSupplier(() -> {
                    throw new ParseException("bar", 0);
                });

                ThrowingLongUnaryOperator<ParseException> getting = operator.onErrorGetChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> getting.applyAsLong(1L));
                assertEquals("bar", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(operator).applyAsLong(1L);
                verify(operator).onErrorGetChecked(fallback);
                verify(fallback).getAsLong();
                verifyNoMoreInteractions(operator, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ParseException {
                ThrowingLongUnaryOperator<IOException> operator = Spied.checkedLongUnaryOperator(l -> {
                    throw new IOException(Long.toString(l));
                });

                ThrowingLongSupplier<ParseException> fallback = Spied.checkedLongSupplier(() -> throwable.throwUnchecked("bar"));

                ThrowingLongUnaryOperator<ParseException> getting = operator.onErrorGetChecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.applyAsLong(1L));
                assertEquals("bar", thrown.getMessage());

                verify(operator).applyAsLong(1L);
                verify(operator).onErrorGetChecked(fallback);
                verify(fallback).getAsLong();
                verifyNoMoreInteractions(operator, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingLongUnaryOperator<IOException> operator = Spied.checkedLongUnaryOperator(throwable::throwUnchecked);

            ThrowingLongSupplier<ParseException> fallback = Spied.checkedLongSupplier(() -> 0L);

            ThrowingLongUnaryOperator<ParseException> getting = operator.onErrorGetChecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.applyAsLong(1L));
            assertEquals("1", thrown.getMessage());

            verify(operator).applyAsLong(1L);
            verify(operator).onErrorGetChecked(fallback);
            verifyNoMoreInteractions(operator, fallback);
        }
    }

    @Nested
    class OnErrorGetUnchecked {

        @Test
        void testNullArgument() {
            ThrowingLongUnaryOperator<IOException> operator = Spied.checkedLongUnaryOperator(l -> l + 1);

            assertThrows(NullPointerException.class, () -> operator.onErrorGetUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingLongUnaryOperator<IOException> operator = Spied.checkedLongUnaryOperator(l -> l + 1);

            LongSupplier fallback = Spied.longSupplier(() -> 2L);

            LongUnaryOperator getting = operator.onErrorGetUnchecked(fallback);

            assertEquals(2L, getting.applyAsLong(1L));

            verify(operator).applyAsLong(1L);
            verify(operator).onErrorGetUnchecked(fallback);
            verifyNoMoreInteractions(operator, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                ThrowingLongUnaryOperator<IOException> operator = Spied.checkedLongUnaryOperator(l -> {
                    throw new IOException(Long.toString(l));
                });

                LongSupplier fallback = Spied.longSupplier(() -> 2L);

                LongUnaryOperator getting = operator.onErrorGetUnchecked(fallback);

                assertEquals(2L, getting.applyAsLong(1L));

                verify(operator).applyAsLong(1L);
                verify(operator).onErrorGetUnchecked(fallback);
                verify(fallback).getAsLong();
                verifyNoMoreInteractions(operator, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingLongUnaryOperator<IOException> operator = Spied.checkedLongUnaryOperator(l -> {
                    throw new IOException(Long.toString(l));
                });

                LongSupplier fallback = Spied.longSupplier(() -> throwable.throwUnchecked("bar"));

                LongUnaryOperator getting = operator.onErrorGetUnchecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.applyAsLong(1L));
                assertEquals("bar", thrown.getMessage());

                verify(operator).applyAsLong(1L);
                verify(operator).onErrorGetUnchecked(fallback);
                verify(fallback).getAsLong();
                verifyNoMoreInteractions(operator, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingLongUnaryOperator<IOException> operator = Spied.checkedLongUnaryOperator(throwable::throwUnchecked);

            LongSupplier fallback = Spied.longSupplier(() -> 2L);

            LongUnaryOperator getting = operator.onErrorGetUnchecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.applyAsLong(1L));
            assertEquals("1", thrown.getMessage());

            verify(operator).applyAsLong(1L);
            verify(operator).onErrorGetUnchecked(fallback);
            verifyNoMoreInteractions(operator, fallback);
        }
    }

    @Nested
    class OnErrorReturn {

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingLongUnaryOperator<IOException> operator = Spied.checkedLongUnaryOperator(l -> l + 1);

            LongUnaryOperator returning = operator.onErrorReturn(2L);

            assertEquals(2L, returning.applyAsLong(1L));

            verify(operator).applyAsLong(1L);
            verify(operator).onErrorReturn(2L);
            verifyNoMoreInteractions(operator);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingLongUnaryOperator<IOException> operator = Spied.checkedLongUnaryOperator(l -> {
                throw new IOException(Long.toString(l));
            });

            LongUnaryOperator returning = operator.onErrorReturn(2L);

            assertEquals(2L, returning.applyAsLong(1L));

            verify(operator).applyAsLong(1L);
            verify(operator).onErrorReturn(2L);
            verifyNoMoreInteractions(operator);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingLongUnaryOperator<IOException> operator = Spied.checkedLongUnaryOperator(throwable::throwUnchecked);

            LongUnaryOperator returning = operator.onErrorReturn(2L);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> returning.applyAsLong(1L));
            assertEquals("1", thrown.getMessage());

            verify(operator).applyAsLong(1L);
            verify(operator).onErrorReturn(2L);
            verifyNoMoreInteractions(operator);
        }
    }

    @Nested
    class Unchecked {

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingLongUnaryOperator<IOException> operator = Spied.checkedLongUnaryOperator(l -> l + 1);

            LongUnaryOperator unchecked = operator.unchecked();

            assertEquals(2L, unchecked.applyAsLong(1L));

            verify(operator).applyAsLong(1L);
            verify(operator).unchecked();
            verify(operator).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(operator);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingLongUnaryOperator<IOException> operator = Spied.checkedLongUnaryOperator(l -> {
                throw new IOException(Long.toString(l));
            });

            LongUnaryOperator unchecked = operator.unchecked();

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.applyAsLong(1L));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1", cause.getMessage());

            verify(operator).applyAsLong(1L);
            verify(operator).unchecked();
            verify(operator).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(operator);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingLongUnaryOperator<IOException> operator = Spied.checkedLongUnaryOperator(throwable::throwUnchecked);

            LongUnaryOperator unchecked = operator.unchecked();

            Throwable thrown = assertThrows(throwable.throwableType(), () -> unchecked.applyAsLong(1L));
            assertEquals("1", thrown.getMessage());

            verify(operator).applyAsLong(1L);
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
            ThrowingLongUnaryOperator<IOException> operator = ThrowingLongUnaryOperator.of(l -> l + 1);

            assertEquals(2L, operator.applyAsLong(1L));
        }
    }

    @Test
    void testIdentity() throws IOException {
        ThrowingLongUnaryOperator<IOException> operator = ThrowingLongUnaryOperator.identity();

        assertEquals(1L, operator.applyAsLong(1L));
    }

    @Nested
    class UncheckedFactory {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> ThrowingFunction.unchecked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ThrowingLongUnaryOperator<IOException> operator = Spied.checkedLongUnaryOperator(l -> l + 1);

            LongUnaryOperator unchecked = ThrowingLongUnaryOperator.unchecked(operator);

            assertEquals(2L, unchecked.applyAsLong(1L));

            verify(operator).unchecked();
            verify(operator).onErrorThrowAsUnchecked(any());
            verify(operator).applyAsLong(1L);
            verifyNoMoreInteractions(operator);
        }

        @Test
        void testArgumentThrowsChecked() throws IOException {
            ThrowingLongUnaryOperator<IOException> operator = Spied.checkedLongUnaryOperator(l -> {
                throw new IOException(Long.toString(l));
            });

            LongUnaryOperator unchecked = ThrowingLongUnaryOperator.unchecked(operator);

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.applyAsLong(1L));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1", cause.getMessage());

            verify(operator).unchecked();
            verify(operator).onErrorThrowAsUnchecked(any());
            verify(operator).applyAsLong(1L);
            verifyNoMoreInteractions(operator);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testArgumentThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingLongUnaryOperator<IOException> operator = Spied.checkedLongUnaryOperator(throwable::throwUnchecked);

            LongUnaryOperator unchecked = ThrowingLongUnaryOperator.unchecked(operator);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> unchecked.applyAsLong(1L));
            assertEquals("1", thrown.getMessage());

            verify(operator).unchecked();
            verify(operator).onErrorThrowAsUnchecked(any());
            verify(operator).applyAsLong(1L);
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
            ThrowingLongUnaryOperator<IOException> operator = ThrowingLongUnaryOperator.checked(l -> l + 1);

            assertEquals(2L, operator.applyAsLong(1L));
        }

        @Test
        void testArgumentThrowsUnchecked() {
            ThrowingLongUnaryOperator<IOException> operator = ThrowingLongUnaryOperator.checked(l -> {
                throw new UncheckedException(Long.toString(l), new IOException());
            });

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> operator.applyAsLong(1L));
            assertEquals("1", thrown.getMessage());
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertNull(cause.getMessage());
        }
    }

    @Nested
    class CheckedUnwrapper {

        @Test
        void testNullArguments() {
            LongUnaryOperator operator = l -> l + 1;

            assertThrows(NullPointerException.class, () -> ThrowingLongUnaryOperator.checked(null, IOException.class));
            assertThrows(NullPointerException.class, () -> ThrowingLongUnaryOperator.checked(operator, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ThrowingLongUnaryOperator<IOException> operator = ThrowingLongUnaryOperator.checked(l -> l + 1, IOException.class);

            assertEquals(2L, operator.applyAsLong(1L));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                ThrowingLongUnaryOperator<IOException> operator = ThrowingLongUnaryOperator.checked(l -> {
                    throw new UncheckedException(new IOException(Long.toString(l)));
                }, IOException.class);

                IOException thrown = assertThrows(IOException.class, () -> operator.applyAsLong(1L));
                assertEquals("1", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                ThrowingLongUnaryOperator<IOException> operator = ThrowingLongUnaryOperator.checked(l -> {
                    throw new UncheckedException(new FileNotFoundException(Long.toString(l)));
                }, IOException.class);

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class, () -> operator.applyAsLong(1L));
                assertEquals("1", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                ThrowingLongUnaryOperator<IOException> operator = ThrowingLongUnaryOperator.checked(l -> {
                    throw new UncheckedException(new ParseException(Long.toString(l), 0));
                }, IOException.class);

                UncheckedException thrown = assertThrows(UncheckedException.class, () -> operator.applyAsLong(1L));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("1", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            ThrowingLongUnaryOperator<IOException> operator = ThrowingLongUnaryOperator.checked(l -> {
                throw new IllegalStateException(Long.toString(l));
            }, IOException.class);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> operator.applyAsLong(1L));
            assertEquals("1", thrown.getMessage());
        }
    }

    @Nested
    class InvokeAndUnwrap {

        @Test
        void testNullArguments() {
            LongUnaryOperator operator = l -> l + 1;

            assertThrows(NullPointerException.class, () -> ThrowingLongUnaryOperator.invokeAndUnwrap(null, 1L, IOException.class));
            assertThrows(NullPointerException.class, () -> ThrowingLongUnaryOperator.invokeAndUnwrap(operator, 1L, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            LongUnaryOperator operator = l -> l + 1;

            assertEquals(2L, ThrowingLongUnaryOperator.invokeAndUnwrap(operator, 1L, IOException.class));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                LongUnaryOperator operator = l -> {
                    throw new UncheckedException(new IOException(Long.toString(l)));
                };

                IOException thrown = assertThrows(IOException.class,
                        () -> ThrowingLongUnaryOperator.invokeAndUnwrap(operator, 1L, IOException.class));
                assertEquals("1", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                LongUnaryOperator operator = l -> {
                    throw new UncheckedException(new FileNotFoundException(Long.toString(l)));
                };

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class,
                        () -> ThrowingLongUnaryOperator.invokeAndUnwrap(operator, 1L, IOException.class));
                assertEquals("1", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                LongUnaryOperator operator = l -> {
                    throw new UncheckedException(new ParseException(Long.toString(l), 0));
                };

                UncheckedException thrown = assertThrows(UncheckedException.class,
                        () -> ThrowingLongUnaryOperator.invokeAndUnwrap(operator, 1L, IOException.class));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("1", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            LongUnaryOperator operator = l -> {
                throw new IllegalStateException(Long.toString(l));
            };

            IllegalStateException thrown = assertThrows(IllegalStateException.class,
                    () -> ThrowingLongUnaryOperator.invokeAndUnwrap(operator, 1L, IOException.class));
            assertEquals("1", thrown.getMessage());
        }
    }
}
