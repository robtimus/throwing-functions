/*
 * ThrowingIntBinaryOperatorTest.java
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
import java.util.function.IntBinaryOperator;
import java.util.function.IntSupplier;
import java.util.function.ToIntFunction;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

@SuppressWarnings("nls")
class ThrowingIntBinaryOperatorTest {

    @Nested
    class OnErrorThrowAsChecked {

        @Test
        void testNullArgument() {
            ThrowingIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator(Integer::max);

            assertThrows(NullPointerException.class, () -> operator.onErrorThrowAsChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator(Integer::max);

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingIntBinaryOperator<ExecutionException> throwing = operator.onErrorThrowAsChecked(errorMapper);

            assertEquals(2, throwing.applyAsInt(1, 2));

            verify(operator).applyAsInt(1, 2);
            verify(operator).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(operator, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator((i1, i2) -> {
                throw new IOException(Integer.toString(i1 + i2));
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingIntBinaryOperator<ExecutionException> throwing = operator.onErrorThrowAsChecked(errorMapper);

            ExecutionException thrown = assertThrows(ExecutionException.class, () -> throwing.applyAsInt(1, 2));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("3", cause.getMessage());

            verify(operator).applyAsInt(1, 2);
            verify(operator).onErrorThrowAsChecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(operator, errorMapper);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator((i1, i2) -> throwable.throwUnchecked(i1 + i2));

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingIntBinaryOperator<ExecutionException> throwing = operator.onErrorThrowAsChecked(errorMapper);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> throwing.applyAsInt(1, 2));
            assertEquals("3", thrown.getMessage());

            verify(operator).applyAsInt(1, 2);
            verify(operator).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(operator, errorMapper);
        }
    }

    @Nested
    class OnErrorThrowAsUnchecked {

        @Test
        void testNullArgument() {
            ThrowingIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator(Integer::max);

            assertThrows(NullPointerException.class, () -> operator.onErrorThrowAsUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator(Integer::max);

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            IntBinaryOperator throwing = operator.onErrorThrowAsUnchecked(errorMapper);

            assertEquals(2, throwing.applyAsInt(1, 2));

            verify(operator).applyAsInt(1, 2);
            verify(operator).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(operator, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator((i1, i2) -> {
                throw new IOException(Integer.toString(i1 + i2));
            });

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            IntBinaryOperator throwing = operator.onErrorThrowAsUnchecked(errorMapper);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> throwing.applyAsInt(1, 2));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("3", cause.getMessage());

            verify(operator).applyAsInt(1, 2);
            verify(operator).onErrorThrowAsUnchecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(operator, errorMapper);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator((i1, i2) -> throwable.throwUnchecked(i1 + i2));

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            IntBinaryOperator throwing = operator.onErrorThrowAsUnchecked(errorMapper);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> throwing.applyAsInt(1, 2));
            assertEquals("3", thrown.getMessage());

            verify(operator).applyAsInt(1, 2);
            verify(operator).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(operator, errorMapper);
        }
    }

    @Nested
    class OnErrorHandleChecked {

        @Test
        void testNullArgument() {
            ThrowingIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator(Integer::max);

            assertThrows(NullPointerException.class, () -> operator.onErrorHandleChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator(Integer::max);

            ThrowingToIntFunction<IOException, ExecutionException> errorHandler = Spied.checkedToIntFunction(e -> 0);

            ThrowingIntBinaryOperator<ExecutionException> handling = operator.onErrorHandleChecked(errorHandler);

            assertEquals(2, handling.applyAsInt(1, 2));

            verify(operator).applyAsInt(1, 2);
            verify(operator).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(operator, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException, ExecutionException {
                ThrowingIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator((i1, i2) -> {
                    throw new IOException(Integer.toString(i1 + i2));
                });

                ThrowingToIntFunction<IOException, ExecutionException> errorHandler = Spied.checkedToIntFunction(e -> 0);

                ThrowingIntBinaryOperator<ExecutionException> handling = operator.onErrorHandleChecked(errorHandler);

                assertEquals(0, handling.applyAsInt(1, 2));

                verify(operator).applyAsInt(1, 2);
                verify(operator).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsInt(any());
                verifyNoMoreInteractions(operator, errorHandler);
            }

            @Test
            void testHandlerThrowsChecked() throws IOException, ExecutionException {
                ThrowingIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator((i1, i2) -> {
                    throw new IOException(Integer.toString(i1 + i2));
                });

                ThrowingToIntFunction<IOException, ExecutionException> errorHandler = Spied.checkedToIntFunction(e -> {
                    throw new ExecutionException(e);
                });

                ThrowingIntBinaryOperator<ExecutionException> handling = operator.onErrorHandleChecked(errorHandler);

                ExecutionException thrown = assertThrows(ExecutionException.class, () -> handling.applyAsInt(1, 2));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("3", cause.getMessage());

                verify(operator).applyAsInt(1, 2);
                verify(operator).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsInt(any());
                verifyNoMoreInteractions(operator, errorHandler);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testHandlerThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ExecutionException {
                ThrowingIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator((i1, i2) -> {
                    throw new IOException(Integer.toString(i1 + i2));
                });

                ThrowingToIntFunction<IOException, ExecutionException> errorHandler = Spied.checkedToIntFunction(throwable::throwUnchecked);

                ThrowingIntBinaryOperator<ExecutionException> handling = operator.onErrorHandleChecked(errorHandler);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.applyAsInt(1, 2));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("3", cause.getMessage());

                verify(operator).applyAsInt(1, 2);
                verify(operator).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsInt(any());
                verifyNoMoreInteractions(operator, errorHandler);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator((i1, i2) -> throwable.throwUnchecked(i1 + i2));

            ThrowingToIntFunction<IOException, ExecutionException> errorHandler = Spied.checkedToIntFunction(e -> 0);

            ThrowingIntBinaryOperator<ExecutionException> handling = operator.onErrorHandleChecked(errorHandler);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.applyAsInt(1, 2));
            assertEquals("3", thrown.getMessage());

            verify(operator).applyAsInt(1, 2);
            verify(operator).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(operator, errorHandler);
        }
    }

    @Nested
    class OnErrorHandleUnchecked {

        @Test
        void testNullArgument() {
            ThrowingIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator(Integer::max);

            assertThrows(NullPointerException.class, () -> operator.onErrorHandleUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator(Integer::max);

            ToIntFunction<IOException> errorHandler = Spied.toIntFunction(e -> 0);

            IntBinaryOperator handling = operator.onErrorHandleUnchecked(errorHandler);

            assertEquals(2, handling.applyAsInt(1, 2));

            verify(operator).applyAsInt(1, 2);
            verify(operator).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(operator, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException {
                ThrowingIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator((i1, i2) -> {
                    throw new IOException(Integer.toString(i1 + i2));
                });

                ToIntFunction<IOException> errorHandler = Spied.toIntFunction(e -> 0);

                IntBinaryOperator handling = operator.onErrorHandleUnchecked(errorHandler);

                assertEquals(0, handling.applyAsInt(1, 2));

                verify(operator).applyAsInt(1, 2);
                verify(operator).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).applyAsInt(any());
                verifyNoMoreInteractions(operator, errorHandler);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testHandlerThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator((i1, i2) -> {
                    throw new IOException(Integer.toString(i1 + i2));
                });

                ToIntFunction<IOException> errorHandler = Spied.toIntFunction(throwable::throwUnchecked);

                IntBinaryOperator handling = operator.onErrorHandleUnchecked(errorHandler);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.applyAsInt(1, 2));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("3", cause.getMessage());

                verify(operator).applyAsInt(1, 2);
                verify(operator).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).applyAsInt(any());
                verifyNoMoreInteractions(operator, errorHandler);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator((i1, i2) -> throwable.throwUnchecked(i1 + i2));

            ToIntFunction<IOException> errorHandler = Spied.toIntFunction(e -> 0);

            IntBinaryOperator handling = operator.onErrorHandleUnchecked(errorHandler);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.applyAsInt(1, 2));
            assertEquals("3", thrown.getMessage());

            verify(operator).applyAsInt(1, 2);
            verify(operator).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(operator, errorHandler);
        }
    }

    @Nested
    class OnErrorApplyChecked {

        @Test
        void testNullArgument() {
            ThrowingIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator(Integer::max);

            assertThrows(NullPointerException.class, () -> operator.onErrorApplyChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator(Integer::max);

            ThrowingIntBinaryOperator<ExecutionException> fallback = Spied.checkedIntBinaryOperator((i1, i2) -> i1 + i2 + i1 + i2);

            ThrowingIntBinaryOperator<ExecutionException> applying = operator.onErrorApplyChecked(fallback);

            assertEquals(2, applying.applyAsInt(1, 2));

            verify(operator).applyAsInt(1, 2);
            verify(operator).onErrorApplyChecked(fallback);
            verifyNoMoreInteractions(operator, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                ThrowingIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator((i1, i2) -> {
                    throw new IOException(Integer.toString(i1 + i2));
                });

                ThrowingIntBinaryOperator<ParseException> fallback = Spied.checkedIntBinaryOperator((i1, i2) -> i1 + i2 + i1 + i2);

                ThrowingIntBinaryOperator<ParseException> applying = operator.onErrorApplyChecked(fallback);

                assertEquals(6, applying.applyAsInt(1, 2));

                verify(operator).applyAsInt(1, 2);
                verify(operator).onErrorApplyChecked(fallback);
                verify(fallback).applyAsInt(1, 2);
                verifyNoMoreInteractions(operator, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                ThrowingIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator((i1, i2) -> {
                    throw new IOException(Integer.toString(i1 + i2));
                });

                ThrowingIntBinaryOperator<ParseException> fallback = Spied.checkedIntBinaryOperator((i1, i2) -> {
                    throw new ParseException(Integer.toString(i1 + i2), 0);
                });

                ThrowingIntBinaryOperator<ParseException> applying = operator.onErrorApplyChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> applying.applyAsInt(1, 2));
                assertEquals("3", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(operator).applyAsInt(1, 2);
                verify(operator).onErrorApplyChecked(fallback);
                verify(fallback).applyAsInt(1, 2);
                verifyNoMoreInteractions(operator, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ParseException {
                ThrowingIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator((i1, i2) -> {
                    throw new IOException(Integer.toString(i1 + i2));
                });

                ThrowingIntBinaryOperator<ParseException> fallback = Spied.checkedIntBinaryOperator((i1, i2) -> throwable.throwUnchecked(i1 + i2));

                ThrowingIntBinaryOperator<ParseException> applying = operator.onErrorApplyChecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.applyAsInt(1, 2));
                assertEquals("3", thrown.getMessage());

                verify(operator).applyAsInt(1, 2);
                verify(operator).onErrorApplyChecked(fallback);
                verify(fallback).applyAsInt(1, 2);
                verifyNoMoreInteractions(operator, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator((i1, i2) -> throwable.throwUnchecked(i1 + i2));

            ThrowingIntBinaryOperator<ParseException> fallback = Spied.checkedIntBinaryOperator((i1, i2) -> i1 + i2 + i1 + i2);

            ThrowingIntBinaryOperator<ParseException> applying = operator.onErrorApplyChecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.applyAsInt(1, 2));
            assertEquals("3", thrown.getMessage());

            verify(operator).applyAsInt(1, 2);
            verify(operator).onErrorApplyChecked(fallback);
            verifyNoMoreInteractions(operator, fallback);
        }
    }

    @Nested
    class OnErrorApplyUnchecked {

        @Test
        void testNullArgument() {
            ThrowingIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator(Integer::max);

            assertThrows(NullPointerException.class, () -> operator.onErrorApplyUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator(Integer::max);

            IntBinaryOperator fallback = Spied.intBinaryOperator((i1, i2) -> i1 + i2 + i1 + i2);

            IntBinaryOperator applying = operator.onErrorApplyUnchecked(fallback);

            assertEquals(2, applying.applyAsInt(1, 2));

            verify(operator).applyAsInt(1, 2);
            verify(operator).onErrorApplyUnchecked(fallback);
            verifyNoMoreInteractions(operator, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                ThrowingIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator((i1, i2) -> {
                    throw new IOException(Integer.toString(i1 + i2));
                });

                IntBinaryOperator fallback = Spied.intBinaryOperator((i1, i2) -> i1 + i2 + i1 + i2);

                IntBinaryOperator applying = operator.onErrorApplyUnchecked(fallback);

                assertEquals(6, applying.applyAsInt(1, 2));

                verify(operator).applyAsInt(1, 2);
                verify(operator).onErrorApplyUnchecked(fallback);
                verify(fallback).applyAsInt(1, 2);
                verifyNoMoreInteractions(operator, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator((i1, i2) -> {
                    throw new IOException(Integer.toString(i1 + i2));
                });

                IntBinaryOperator fallback = Spied.intBinaryOperator((i1, i2) -> throwable.throwUnchecked(i1 + i2));

                IntBinaryOperator applying = operator.onErrorApplyUnchecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.applyAsInt(1, 2));
                assertEquals("3", thrown.getMessage());

                verify(operator).applyAsInt(1, 2);
                verify(operator).onErrorApplyUnchecked(fallback);
                verify(fallback).applyAsInt(1, 2);
                verifyNoMoreInteractions(operator, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator((i1, i2) -> throwable.throwUnchecked(i1 + i2));

            IntBinaryOperator fallback = Spied.intBinaryOperator((i1, i2) -> i1 + i2 + i1 + i2);

            IntBinaryOperator applying = operator.onErrorApplyUnchecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.applyAsInt(1, 2));
            assertEquals("3", thrown.getMessage());

            verify(operator).applyAsInt(1, 2);
            verify(operator).onErrorApplyUnchecked(fallback);
            verifyNoMoreInteractions(operator, fallback);
        }
    }

    @Nested
    class OnErrorGetChecked {

        @Test
        void testNullArgument() {
            ThrowingIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator(Integer::max);

            assertThrows(NullPointerException.class, () -> operator.onErrorGetChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator(Integer::max);

            ThrowingIntSupplier<ExecutionException> fallback = Spied.checkedIntSupplier(() -> 0);

            ThrowingIntBinaryOperator<ExecutionException> getting = operator.onErrorGetChecked(fallback);

            assertEquals(2, getting.applyAsInt(1, 2));

            verify(operator).applyAsInt(1, 2);
            verify(operator).onErrorGetChecked(fallback);
            verifyNoMoreInteractions(operator, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                ThrowingIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator((i1, i2) -> {
                    throw new IOException(Integer.toString(i1 + i2));
                });

                ThrowingIntSupplier<ParseException> fallback = Spied.checkedIntSupplier(() -> 0);

                ThrowingIntBinaryOperator<ParseException> getting = operator.onErrorGetChecked(fallback);

                assertEquals(0, getting.applyAsInt(1, 2));

                verify(operator).applyAsInt(1, 2);
                verify(operator).onErrorGetChecked(fallback);
                verify(fallback).getAsInt();
                verifyNoMoreInteractions(operator, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                ThrowingIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator((i1, i2) -> {
                    throw new IOException(Integer.toString(i1 + i2));
                });

                ThrowingIntSupplier<ParseException> fallback = Spied.checkedIntSupplier(() -> {
                    throw new ParseException("bar", 0);
                });

                ThrowingIntBinaryOperator<ParseException> getting = operator.onErrorGetChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> getting.applyAsInt(1, 2));
                assertEquals("bar", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(operator).applyAsInt(1, 2);
                verify(operator).onErrorGetChecked(fallback);
                verify(fallback).getAsInt();
                verifyNoMoreInteractions(operator, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ParseException {
                ThrowingIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator((i1, i2) -> {
                    throw new IOException(Integer.toString(i1 + i2));
                });

                ThrowingIntSupplier<ParseException> fallback = Spied.checkedIntSupplier(() -> throwable.throwUnchecked("bar"));

                ThrowingIntBinaryOperator<ParseException> getting = operator.onErrorGetChecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.applyAsInt(1, 2));
                assertEquals("bar", thrown.getMessage());

                verify(operator).applyAsInt(1, 2);
                verify(operator).onErrorGetChecked(fallback);
                verify(fallback).getAsInt();
                verifyNoMoreInteractions(operator, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator((i1, i2) -> throwable.throwUnchecked(i1 + i2));

            ThrowingIntSupplier<ParseException> fallback = Spied.checkedIntSupplier(() -> 0);

            ThrowingIntBinaryOperator<ParseException> getting = operator.onErrorGetChecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.applyAsInt(1, 2));
            assertEquals("3", thrown.getMessage());

            verify(operator).applyAsInt(1, 2);
            verify(operator).onErrorGetChecked(fallback);
            verifyNoMoreInteractions(operator, fallback);
        }
    }

    @Nested
    class OnErrorGetUnchecked {

        @Test
        void testNullArgument() {
            ThrowingIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator(Integer::max);

            assertThrows(NullPointerException.class, () -> operator.onErrorGetUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator(Integer::max);

            IntSupplier fallback = Spied.intSupplier(() -> 2);

            IntBinaryOperator getting = operator.onErrorGetUnchecked(fallback);

            assertEquals(2, getting.applyAsInt(1, 2));

            verify(operator).applyAsInt(1, 2);
            verify(operator).onErrorGetUnchecked(fallback);
            verifyNoMoreInteractions(operator, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                ThrowingIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator((i1, i2) -> {
                    throw new IOException(Integer.toString(i1 + i2));
                });

                IntSupplier fallback = Spied.intSupplier(() -> 2);

                IntBinaryOperator getting = operator.onErrorGetUnchecked(fallback);

                assertEquals(2, getting.applyAsInt(1, 2));

                verify(operator).applyAsInt(1, 2);
                verify(operator).onErrorGetUnchecked(fallback);
                verify(fallback).getAsInt();
                verifyNoMoreInteractions(operator, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator((i1, i2) -> {
                    throw new IOException(Integer.toString(i1 + i2));
                });

                IntSupplier fallback = Spied.intSupplier(() -> throwable.throwUnchecked("bar"));

                IntBinaryOperator getting = operator.onErrorGetUnchecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.applyAsInt(1, 2));
                assertEquals("bar", thrown.getMessage());

                verify(operator).applyAsInt(1, 2);
                verify(operator).onErrorGetUnchecked(fallback);
                verify(fallback).getAsInt();
                verifyNoMoreInteractions(operator, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator((i1, i2) -> throwable.throwUnchecked(i1 + i2));

            IntSupplier fallback = Spied.intSupplier(() -> 2);

            IntBinaryOperator getting = operator.onErrorGetUnchecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.applyAsInt(1, 2));
            assertEquals("3", thrown.getMessage());

            verify(operator).applyAsInt(1, 2);
            verify(operator).onErrorGetUnchecked(fallback);
            verifyNoMoreInteractions(operator, fallback);
        }
    }

    @Nested
    class OnErrorReturn {

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator(Integer::max);

            IntBinaryOperator returning = operator.onErrorReturn(2);

            assertEquals(2, returning.applyAsInt(1, 2));

            verify(operator).applyAsInt(1, 2);
            verify(operator).onErrorReturn(2);
            verifyNoMoreInteractions(operator);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator((i1, i2) -> {
                throw new IOException(Integer.toString(i1 + i2));
            });

            IntBinaryOperator returning = operator.onErrorReturn(2);

            assertEquals(2, returning.applyAsInt(1, 2));

            verify(operator).applyAsInt(1, 2);
            verify(operator).onErrorReturn(2);
            verifyNoMoreInteractions(operator);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator((i1, i2) -> throwable.throwUnchecked(i1 + i2));

            IntBinaryOperator returning = operator.onErrorReturn(2);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> returning.applyAsInt(1, 2));
            assertEquals("3", thrown.getMessage());

            verify(operator).applyAsInt(1, 2);
            verify(operator).onErrorReturn(2);
            verifyNoMoreInteractions(operator);
        }
    }

    @Nested
    class Unchecked {

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator(Integer::max);

            IntBinaryOperator unchecked = operator.unchecked();

            assertEquals(2, unchecked.applyAsInt(1, 2));

            verify(operator).applyAsInt(1, 2);
            verify(operator).unchecked();
            verify(operator).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(operator);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator((i1, i2) -> {
                throw new IOException(Integer.toString(i1 + i2));
            });

            IntBinaryOperator unchecked = operator.unchecked();

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.applyAsInt(1, 2));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("3", cause.getMessage());

            verify(operator).applyAsInt(1, 2);
            verify(operator).unchecked();
            verify(operator).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(operator);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator((i1, i2) -> throwable.throwUnchecked(i1 + i2));

            IntBinaryOperator unchecked = operator.unchecked();

            Throwable thrown = assertThrows(throwable.throwableType(), () -> unchecked.applyAsInt(1, 2));
            assertEquals("3", thrown.getMessage());

            verify(operator).applyAsInt(1, 2);
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
            ThrowingIntBinaryOperator<IOException> operator = ThrowingIntBinaryOperator.of(Integer::max);

            assertEquals(2, operator.applyAsInt(1, 2));
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
            ThrowingIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator(Integer::max);

            IntBinaryOperator unchecked = ThrowingIntBinaryOperator.unchecked(operator);

            assertEquals(2, unchecked.applyAsInt(1, 2));

            verify(operator).unchecked();
            verify(operator).onErrorThrowAsUnchecked(any());
            verify(operator).applyAsInt(1, 2);
            verifyNoMoreInteractions(operator);
        }

        @Test
        void testArgumentThrowsChecked() throws IOException {
            ThrowingIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator((i1, i2) -> {
                throw new IOException(Integer.toString(i1 + i2));
            });

            IntBinaryOperator unchecked = ThrowingIntBinaryOperator.unchecked(operator);

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.applyAsInt(1, 2));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("3", cause.getMessage());

            verify(operator).unchecked();
            verify(operator).onErrorThrowAsUnchecked(any());
            verify(operator).applyAsInt(1, 2);
            verifyNoMoreInteractions(operator);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testArgumentThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator((i1, i2) -> throwable.throwUnchecked(i1 + i2));

            IntBinaryOperator unchecked = ThrowingIntBinaryOperator.unchecked(operator);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> unchecked.applyAsInt(1, 2));
            assertEquals("3", thrown.getMessage());

            verify(operator).unchecked();
            verify(operator).onErrorThrowAsUnchecked(any());
            verify(operator).applyAsInt(1, 2);
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
            ThrowingIntBinaryOperator<IOException> operator = ThrowingIntBinaryOperator.checked(Integer::max);

            assertEquals(2, operator.applyAsInt(1, 2));
        }

        @Test
        void testArgumentThrowsUnchecked() {
            ThrowingIntBinaryOperator<IOException> operator = ThrowingIntBinaryOperator.checked((i1, i2) -> {
                throw new UncheckedException(Integer.toString(i1 + i2), new IOException());
            });

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> operator.applyAsInt(1, 2));
            assertEquals("3", thrown.getMessage());
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertNull(cause.getMessage());
        }
    }

    @Nested
    class CheckedUnwrapper {

        @Test
        void testNullArguments() {
            IntBinaryOperator operator = Integer::max;

            assertThrows(NullPointerException.class, () -> ThrowingIntBinaryOperator.checked(null, IOException.class));
            assertThrows(NullPointerException.class, () -> ThrowingIntBinaryOperator.checked(operator, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ThrowingIntBinaryOperator<IOException> operator = ThrowingIntBinaryOperator.checked(Integer::max, IOException.class);

            assertEquals(2, operator.applyAsInt(1, 2));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                ThrowingIntBinaryOperator<IOException> operator = ThrowingIntBinaryOperator.checked((i1, i2) -> {
                    throw new UncheckedException(new IOException(Integer.toString(i1 + i2)));
                }, IOException.class);

                IOException thrown = assertThrows(IOException.class, () -> operator.applyAsInt(1, 2));
                assertEquals("3", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                ThrowingIntBinaryOperator<IOException> operator = ThrowingIntBinaryOperator.checked((i1, i2) -> {
                    throw new UncheckedException(new FileNotFoundException(Integer.toString(i1 + i2)));
                }, IOException.class);

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class, () -> operator.applyAsInt(1, 2));
                assertEquals("3", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                ThrowingIntBinaryOperator<IOException> operator = ThrowingIntBinaryOperator.checked((i1, i2) -> {
                    throw new UncheckedException(new ParseException(Integer.toString(i1 + i2), 0));
                }, IOException.class);

                UncheckedException thrown = assertThrows(UncheckedException.class, () -> operator.applyAsInt(1, 2));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("3", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            ThrowingIntBinaryOperator<IOException> operator = ThrowingIntBinaryOperator.checked((i1, i2) -> {
                throw new IllegalStateException(Integer.toString(i1 + i2));
            }, IOException.class);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> operator.applyAsInt(1, 2));
            assertEquals("3", thrown.getMessage());
        }
    }

    @Nested
    class InvokeAndUnwrap {

        @Test
        void testNullArguments() {
            IntBinaryOperator operator = Integer::max;

            assertThrows(NullPointerException.class, () -> ThrowingIntBinaryOperator.invokeAndUnwrap(null, 1, 2, IOException.class));
            assertThrows(NullPointerException.class, () -> ThrowingIntBinaryOperator.invokeAndUnwrap(operator, 1, 2, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            IntBinaryOperator operator = Integer::max;

            assertEquals(2, ThrowingIntBinaryOperator.invokeAndUnwrap(operator, 1, 2, IOException.class));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                IntBinaryOperator operator = (i1, i2) -> {
                    throw new UncheckedException(new IOException(Integer.toString(i1 + i2)));
                };

                IOException thrown = assertThrows(IOException.class,
                        () -> ThrowingIntBinaryOperator.invokeAndUnwrap(operator, 1, 2, IOException.class));
                assertEquals("3", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                IntBinaryOperator operator = (i1, i2) -> {
                    throw new UncheckedException(new FileNotFoundException(Integer.toString(i1 + i2)));
                };

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class,
                        () -> ThrowingIntBinaryOperator.invokeAndUnwrap(operator, 1, 2, IOException.class));
                assertEquals("3", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                IntBinaryOperator operator = (i1, i2) -> {
                    throw new UncheckedException(new ParseException(Integer.toString(i1 + i2), 0));
                };

                UncheckedException thrown = assertThrows(UncheckedException.class,
                        () -> ThrowingIntBinaryOperator.invokeAndUnwrap(operator, 1, 2, IOException.class));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("3", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            IntBinaryOperator operator = (i1, i2) -> {
                throw new IllegalStateException(Integer.toString(i1 + i2));
            };

            IllegalStateException thrown = assertThrows(IllegalStateException.class,
                    () -> ThrowingIntBinaryOperator.invokeAndUnwrap(operator, 1, 2, IOException.class));
            assertEquals("3", thrown.getMessage());
        }
    }
}
