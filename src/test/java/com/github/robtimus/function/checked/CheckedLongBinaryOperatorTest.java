/*
 * CheckedLongBinaryOperatorTest.java
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
import java.util.function.LongBinaryOperator;
import java.util.function.LongSupplier;
import java.util.function.ToLongFunction;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("nls")
class CheckedLongBinaryOperatorTest {

    @Nested
    class OnErrorThrowAsChecked {

        @Test
        void testNullArgument() {
            CheckedLongBinaryOperator<IOException> operator = Spied.checkedLongBinaryOperator(Long::max);

            assertThrows(NullPointerException.class, () -> operator.onErrorThrowAsChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedLongBinaryOperator<IOException> operator = Spied.checkedLongBinaryOperator(Long::max);

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedLongBinaryOperator<ExecutionException> throwing = operator.onErrorThrowAsChecked(errorMapper);

            assertEquals(2L, throwing.applyAsLong(1L, 2L));

            verify(operator).applyAsLong(1L, 2L);
            verify(operator).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(operator, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedLongBinaryOperator<IOException> operator = Spied.checkedLongBinaryOperator((l1, l2) -> {
                throw new IOException(Long.toString(l1 + l2));
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedLongBinaryOperator<ExecutionException> throwing = operator.onErrorThrowAsChecked(errorMapper);

            ExecutionException thrown = assertThrows(ExecutionException.class, () -> throwing.applyAsLong(1L, 2L));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("3", cause.getMessage());

            verify(operator).applyAsLong(1L, 2L);
            verify(operator).onErrorThrowAsChecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(operator, errorMapper);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedLongBinaryOperator<IOException> operator = Spied.checkedLongBinaryOperator((l1, l2) -> {
                throw new IllegalStateException(Long.toString(l1 + l2));
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedLongBinaryOperator<ExecutionException> throwing = operator.onErrorThrowAsChecked(errorMapper);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> throwing.applyAsLong(1L, 2L));
            assertEquals("3", thrown.getMessage());

            verify(operator).applyAsLong(1L, 2L);
            verify(operator).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(operator, errorMapper);
        }
    }

    @Nested
    class OnErrorThrowAsUnchecked {

        @Test
        void testNullArgument() {
            CheckedLongBinaryOperator<IOException> operator = Spied.checkedLongBinaryOperator(Long::max);

            assertThrows(NullPointerException.class, () -> operator.onErrorThrowAsUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedLongBinaryOperator<IOException> operator = Spied.checkedLongBinaryOperator(Long::max);

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            LongBinaryOperator throwing = operator.onErrorThrowAsUnchecked(errorMapper);

            assertEquals(2L, throwing.applyAsLong(1L, 2L));

            verify(operator).applyAsLong(1L, 2L);
            verify(operator).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(operator, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedLongBinaryOperator<IOException> operator = Spied.checkedLongBinaryOperator((l1, l2) -> {
                throw new IOException(Long.toString(l1 + l2));
            });

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            LongBinaryOperator throwing = operator.onErrorThrowAsUnchecked(errorMapper);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> throwing.applyAsLong(1L, 2L));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("3", cause.getMessage());

            verify(operator).applyAsLong(1L, 2L);
            verify(operator).onErrorThrowAsUnchecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(operator, errorMapper);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedLongBinaryOperator<IOException> operator = Spied.checkedLongBinaryOperator((l1, l2) -> {
                throw new IllegalArgumentException(Long.toString(l1 + l2));
            });

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            LongBinaryOperator throwing = operator.onErrorThrowAsUnchecked(errorMapper);

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> throwing.applyAsLong(1L, 2L));
            assertEquals("3", thrown.getMessage());

            verify(operator).applyAsLong(1L, 2L);
            verify(operator).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(operator, errorMapper);
        }
    }

    @Nested
    class OnErrorHandleChecked {

        @Test
        void testNullArgument() {
            CheckedLongBinaryOperator<IOException> operator = Spied.checkedLongBinaryOperator(Long::max);

            assertThrows(NullPointerException.class, () -> operator.onErrorHandleChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedLongBinaryOperator<IOException> operator = Spied.checkedLongBinaryOperator(Long::max);

            CheckedToLongFunction<IOException, ExecutionException> errorHandler = Spied.checkedToLongFunction(e -> 0L);

            CheckedLongBinaryOperator<ExecutionException> handling = operator.onErrorHandleChecked(errorHandler);

            assertEquals(2L, handling.applyAsLong(1L, 2L));

            verify(operator).applyAsLong(1L, 2L);
            verify(operator).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(operator, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException, ExecutionException {
                CheckedLongBinaryOperator<IOException> operator = Spied.checkedLongBinaryOperator((l1, l2) -> {
                    throw new IOException(Long.toString(l1 + l2));
                });

                CheckedToLongFunction<IOException, ExecutionException> errorHandler = Spied.checkedToLongFunction(e -> 0L);

                CheckedLongBinaryOperator<ExecutionException> handling = operator.onErrorHandleChecked(errorHandler);

                assertEquals(0L, handling.applyAsLong(1L, 2L));

                verify(operator).applyAsLong(1L, 2L);
                verify(operator).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsLong(any());
                verifyNoMoreInteractions(operator, errorHandler);
            }

            @Test
            void testHandlerThrowsChecked() throws IOException, ExecutionException {
                CheckedLongBinaryOperator<IOException> operator = Spied.checkedLongBinaryOperator((l1, l2) -> {
                    throw new IOException(Long.toString(l1 + l2));
                });

                CheckedToLongFunction<IOException, ExecutionException> errorHandler = Spied.checkedToLongFunction(e -> {
                    throw new ExecutionException(e);
                });

                CheckedLongBinaryOperator<ExecutionException> handling = operator.onErrorHandleChecked(errorHandler);

                ExecutionException thrown = assertThrows(ExecutionException.class, () -> handling.applyAsLong(1L, 2L));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("3", cause.getMessage());

                verify(operator).applyAsLong(1L, 2L);
                verify(operator).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsLong(any());
                verifyNoMoreInteractions(operator, errorHandler);
            }

            @Test
            void testHandlerThrowsUnchecked() throws IOException, ExecutionException {
                CheckedLongBinaryOperator<IOException> operator = Spied.checkedLongBinaryOperator((l1, l2) -> {
                    throw new IOException(Long.toString(l1 + l2));
                });

                CheckedToLongFunction<IOException, ExecutionException> errorHandler = Spied.checkedToLongFunction(e -> {
                    throw new IllegalStateException(e);
                });

                CheckedLongBinaryOperator<ExecutionException> handling = operator.onErrorHandleChecked(errorHandler);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.applyAsLong(1L, 2L));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("3", cause.getMessage());

                verify(operator).applyAsLong(1L, 2L);
                verify(operator).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsLong(any());
                verifyNoMoreInteractions(operator, errorHandler);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedLongBinaryOperator<IOException> operator = Spied.checkedLongBinaryOperator((l1, l2) -> {
                throw new IllegalStateException(Long.toString(l1 + l2));
            });

            CheckedToLongFunction<IOException, ExecutionException> errorHandler = Spied.checkedToLongFunction(e -> 0L);

            CheckedLongBinaryOperator<ExecutionException> handling = operator.onErrorHandleChecked(errorHandler);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.applyAsLong(1L, 2L));
            assertEquals("3", thrown.getMessage());

            verify(operator).applyAsLong(1L, 2L);
            verify(operator).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(operator, errorHandler);
        }
    }

    @Nested
    class OnErrorHandleUnchecked {

        @Test
        void testNullArgument() {
            CheckedLongBinaryOperator<IOException> operator = Spied.checkedLongBinaryOperator(Long::max);

            assertThrows(NullPointerException.class, () -> operator.onErrorHandleUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedLongBinaryOperator<IOException> operator = Spied.checkedLongBinaryOperator(Long::max);

            ToLongFunction<IOException> errorHandler = Spied.toLongFunction(e -> 0L);

            LongBinaryOperator handling = operator.onErrorHandleUnchecked(errorHandler);

            assertEquals(2L, handling.applyAsLong(1L, 2L));

            verify(operator).applyAsLong(1L, 2L);
            verify(operator).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(operator, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException {
                CheckedLongBinaryOperator<IOException> operator = Spied.checkedLongBinaryOperator((l1, l2) -> {
                    throw new IOException(Long.toString(l1 + l2));
                });

                ToLongFunction<IOException> errorHandler = Spied.toLongFunction(e -> 0L);

                LongBinaryOperator handling = operator.onErrorHandleUnchecked(errorHandler);

                assertEquals(0L, handling.applyAsLong(1L, 2L));

                verify(operator).applyAsLong(1L, 2L);
                verify(operator).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).applyAsLong(any());
                verifyNoMoreInteractions(operator, errorHandler);
            }

            @Test
            void testHandlerThrowsUnchecked() throws IOException {
                CheckedLongBinaryOperator<IOException> operator = Spied.checkedLongBinaryOperator((l1, l2) -> {
                    throw new IOException(Long.toString(l1 + l2));
                });

                ToLongFunction<IOException> errorHandler = Spied.toLongFunction(e -> {
                    throw new IllegalStateException(e);
                });

                LongBinaryOperator handling = operator.onErrorHandleUnchecked(errorHandler);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.applyAsLong(1L, 2L));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("3", cause.getMessage());

                verify(operator).applyAsLong(1L, 2L);
                verify(operator).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).applyAsLong(any());
                verifyNoMoreInteractions(operator, errorHandler);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedLongBinaryOperator<IOException> operator = Spied.checkedLongBinaryOperator((l1, l2) -> {
                throw new IllegalStateException(Long.toString(l1 + l2));
            });

            ToLongFunction<IOException> errorHandler = Spied.toLongFunction(e -> 0L);

            LongBinaryOperator handling = operator.onErrorHandleUnchecked(errorHandler);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.applyAsLong(1L, 2L));
            assertEquals("3", thrown.getMessage());

            verify(operator).applyAsLong(1L, 2L);
            verify(operator).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(operator, errorHandler);
        }
    }

    @Nested
    class OnErrorApplyChecked {

        @Test
        void testNullArgument() {
            CheckedLongBinaryOperator<IOException> operator = Spied.checkedLongBinaryOperator(Long::max);

            assertThrows(NullPointerException.class, () -> operator.onErrorApplyChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedLongBinaryOperator<IOException> operator = Spied.checkedLongBinaryOperator(Long::max);

            CheckedLongBinaryOperator<ExecutionException> fallback = Spied.checkedLongBinaryOperator((l1, l2) -> l1 + l2 + l1 + l2);

            CheckedLongBinaryOperator<ExecutionException> applying = operator.onErrorApplyChecked(fallback);

            assertEquals(2L, applying.applyAsLong(1L, 2L));

            verify(operator).applyAsLong(1L, 2L);
            verify(operator).onErrorApplyChecked(fallback);
            verifyNoMoreInteractions(operator, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                CheckedLongBinaryOperator<IOException> operator = Spied.checkedLongBinaryOperator((l1, l2) -> {
                    throw new IOException(Long.toString(l1 + l2));
                });

                CheckedLongBinaryOperator<ParseException> fallback = Spied.checkedLongBinaryOperator((l1, l2) -> l1 + l2 + l1 + l2);

                CheckedLongBinaryOperator<ParseException> applying = operator.onErrorApplyChecked(fallback);

                assertEquals(6L, applying.applyAsLong(1L, 2L));

                verify(operator).applyAsLong(1L, 2L);
                verify(operator).onErrorApplyChecked(fallback);
                verify(fallback).applyAsLong(1L, 2L);
                verifyNoMoreInteractions(operator, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                CheckedLongBinaryOperator<IOException> operator = Spied.checkedLongBinaryOperator((l1, l2) -> {
                    throw new IOException(Long.toString(l1 + l2));
                });

                CheckedLongBinaryOperator<ParseException> fallback = Spied.checkedLongBinaryOperator((l1, l2) -> {
                    throw new ParseException(Long.toString(l1 + l2), 0);
                });

                CheckedLongBinaryOperator<ParseException> applying = operator.onErrorApplyChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> applying.applyAsLong(1L, 2L));
                assertEquals("3", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(operator).applyAsLong(1L, 2L);
                verify(operator).onErrorApplyChecked(fallback);
                verify(fallback).applyAsLong(1L, 2L);
                verifyNoMoreInteractions(operator, fallback);
            }

            @Test
            void testFallbackThrowsUnchecked() throws IOException, ParseException {
                CheckedLongBinaryOperator<IOException> operator = Spied.checkedLongBinaryOperator((l1, l2) -> {
                    throw new IOException(Long.toString(l1 + l2));
                });

                CheckedLongBinaryOperator<ParseException> fallback = Spied.checkedLongBinaryOperator((l1, l2) -> {
                    throw new IllegalStateException(Long.toString(l1 + l2));
                });

                CheckedLongBinaryOperator<ParseException> applying = operator.onErrorApplyChecked(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> applying.applyAsLong(1L, 2L));
                assertEquals("3", thrown.getMessage());

                verify(operator).applyAsLong(1L, 2L);
                verify(operator).onErrorApplyChecked(fallback);
                verify(fallback).applyAsLong(1L, 2L);
                verifyNoMoreInteractions(operator, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedLongBinaryOperator<IOException> operator = Spied.checkedLongBinaryOperator((l1, l2) -> {
                throw new IllegalStateException(Long.toString(l1 + l2));
            });

            CheckedLongBinaryOperator<ParseException> fallback = Spied.checkedLongBinaryOperator((l1, l2) -> l1 + l2 + l1 + l2);

            CheckedLongBinaryOperator<ParseException> applying = operator.onErrorApplyChecked(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> applying.applyAsLong(1L, 2L));
            assertEquals("3", thrown.getMessage());

            verify(operator).applyAsLong(1L, 2L);
            verify(operator).onErrorApplyChecked(fallback);
            verifyNoMoreInteractions(operator, fallback);
        }
    }

    @Nested
    class OnErrorApplyUnchecked {

        @Test
        void testNullArgument() {
            CheckedLongBinaryOperator<IOException> operator = Spied.checkedLongBinaryOperator(Long::max);

            assertThrows(NullPointerException.class, () -> operator.onErrorApplyUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedLongBinaryOperator<IOException> operator = Spied.checkedLongBinaryOperator(Long::max);

            LongBinaryOperator fallback = Spied.longBinaryOperator((l1, l2) -> l1 + l2 + l1 + l2);

            LongBinaryOperator applying = operator.onErrorApplyUnchecked(fallback);

            assertEquals(2L, applying.applyAsLong(1L, 2L));

            verify(operator).applyAsLong(1L, 2L);
            verify(operator).onErrorApplyUnchecked(fallback);
            verifyNoMoreInteractions(operator, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                CheckedLongBinaryOperator<IOException> operator = Spied.checkedLongBinaryOperator((l1, l2) -> {
                    throw new IOException(Long.toString(l1 + l2));
                });

                LongBinaryOperator fallback = Spied.longBinaryOperator((l1, l2) -> l1 + l2 + l1 + l2);

                LongBinaryOperator applying = operator.onErrorApplyUnchecked(fallback);

                assertEquals(6L, applying.applyAsLong(1L, 2L));

                verify(operator).applyAsLong(1L, 2L);
                verify(operator).onErrorApplyUnchecked(fallback);
                verify(fallback).applyAsLong(1L, 2L);
                verifyNoMoreInteractions(operator, fallback);
            }

            @Test
            void testFallbackThrowsUnchecked() throws IOException {
                CheckedLongBinaryOperator<IOException> operator = Spied.checkedLongBinaryOperator((l1, l2) -> {
                    throw new IOException(Long.toString(l1 + l2));
                });

                LongBinaryOperator fallback = Spied.longBinaryOperator((l1, l2) -> {
                    throw new IllegalStateException(Long.toString(l1 + l2));
                });

                LongBinaryOperator applying = operator.onErrorApplyUnchecked(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> applying.applyAsLong(1L, 2L));
                assertEquals("3", thrown.getMessage());

                verify(operator).applyAsLong(1L, 2L);
                verify(operator).onErrorApplyUnchecked(fallback);
                verify(fallback).applyAsLong(1L, 2L);
                verifyNoMoreInteractions(operator, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedLongBinaryOperator<IOException> operator = Spied.checkedLongBinaryOperator((l1, l2) -> {
                throw new IllegalStateException(Long.toString(l1 + l2));
            });

            LongBinaryOperator fallback = Spied.longBinaryOperator((l1, l2) -> l1 + l2 + l1 + l2);

            LongBinaryOperator applying = operator.onErrorApplyUnchecked(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> applying.applyAsLong(1L, 2L));
            assertEquals("3", thrown.getMessage());

            verify(operator).applyAsLong(1L, 2L);
            verify(operator).onErrorApplyUnchecked(fallback);
            verifyNoMoreInteractions(operator, fallback);
        }
    }

    @Nested
    class OnErrorGetChecked {

        @Test
        void testNullArgument() {
            CheckedLongBinaryOperator<IOException> operator = Spied.checkedLongBinaryOperator(Long::max);

            assertThrows(NullPointerException.class, () -> operator.onErrorGetChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedLongBinaryOperator<IOException> operator = Spied.checkedLongBinaryOperator(Long::max);

            CheckedLongSupplier<ExecutionException> fallback = Spied.checkedLongSupplier(() -> 0L);

            CheckedLongBinaryOperator<ExecutionException> getting = operator.onErrorGetChecked(fallback);

            assertEquals(2L, getting.applyAsLong(1L, 2L));

            verify(operator).applyAsLong(1L, 2L);
            verify(operator).onErrorGetChecked(fallback);
            verifyNoMoreInteractions(operator, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                CheckedLongBinaryOperator<IOException> operator = Spied.checkedLongBinaryOperator((l1, l2) -> {
                    throw new IOException(Long.toString(l1 + l2));
                });

                CheckedLongSupplier<ParseException> fallback = Spied.checkedLongSupplier(() -> 0L);

                CheckedLongBinaryOperator<ParseException> getting = operator.onErrorGetChecked(fallback);

                assertEquals(0L, getting.applyAsLong(1L, 2L));

                verify(operator).applyAsLong(1L, 2L);
                verify(operator).onErrorGetChecked(fallback);
                verify(fallback).getAsLong();
                verifyNoMoreInteractions(operator, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                CheckedLongBinaryOperator<IOException> operator = Spied.checkedLongBinaryOperator((l1, l2) -> {
                    throw new IOException(Long.toString(l1 + l2));
                });

                CheckedLongSupplier<ParseException> fallback = Spied.checkedLongSupplier(() -> {
                    throw new ParseException("bar", 0);
                });

                CheckedLongBinaryOperator<ParseException> getting = operator.onErrorGetChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> getting.applyAsLong(1L, 2L));
                assertEquals("bar", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(operator).applyAsLong(1L, 2L);
                verify(operator).onErrorGetChecked(fallback);
                verify(fallback).getAsLong();
                verifyNoMoreInteractions(operator, fallback);
            }

            @Test
            void testFallbackThrowsUnchecked() throws IOException, ParseException {
                CheckedLongBinaryOperator<IOException> operator = Spied.checkedLongBinaryOperator((l1, l2) -> {
                    throw new IOException(Long.toString(l1 + l2));
                });

                CheckedLongSupplier<ParseException> fallback = Spied.checkedLongSupplier(() -> {
                    throw new IllegalStateException("bar");
                });

                CheckedLongBinaryOperator<ParseException> getting = operator.onErrorGetChecked(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> getting.applyAsLong(1L, 2L));
                assertEquals("bar", thrown.getMessage());

                verify(operator).applyAsLong(1L, 2L);
                verify(operator).onErrorGetChecked(fallback);
                verify(fallback).getAsLong();
                verifyNoMoreInteractions(operator, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedLongBinaryOperator<IOException> operator = Spied.checkedLongBinaryOperator((l1, l2) -> {
                throw new IllegalStateException(Long.toString(l1 + l2));
            });

            CheckedLongSupplier<ParseException> fallback = Spied.checkedLongSupplier(() -> 0L);

            CheckedLongBinaryOperator<ParseException> getting = operator.onErrorGetChecked(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> getting.applyAsLong(1L, 2L));
            assertEquals("3", thrown.getMessage());

            verify(operator).applyAsLong(1L, 2L);
            verify(operator).onErrorGetChecked(fallback);
            verifyNoMoreInteractions(operator, fallback);
        }
    }

    @Nested
    class OnErrorGetUnchecked {

        @Test
        void testNullArgument() {
            CheckedLongBinaryOperator<IOException> operator = Spied.checkedLongBinaryOperator(Long::max);

            assertThrows(NullPointerException.class, () -> operator.onErrorGetUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedLongBinaryOperator<IOException> operator = Spied.checkedLongBinaryOperator(Long::max);

            LongSupplier fallback = Spied.longSupplier(() -> 2L);

            LongBinaryOperator getting = operator.onErrorGetUnchecked(fallback);

            assertEquals(2L, getting.applyAsLong(1L, 2L));

            verify(operator).applyAsLong(1L, 2L);
            verify(operator).onErrorGetUnchecked(fallback);
            verifyNoMoreInteractions(operator, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                CheckedLongBinaryOperator<IOException> operator = Spied.checkedLongBinaryOperator((l1, l2) -> {
                    throw new IOException(Long.toString(l1 + l2));
                });

                LongSupplier fallback = Spied.longSupplier(() -> 2L);

                LongBinaryOperator getting = operator.onErrorGetUnchecked(fallback);

                assertEquals(2L, getting.applyAsLong(1L, 2L));

                verify(operator).applyAsLong(1L, 2L);
                verify(operator).onErrorGetUnchecked(fallback);
                verify(fallback).getAsLong();
                verifyNoMoreInteractions(operator, fallback);
            }

            @Test
            void testFallbackThrowsUnchecked() throws IOException {
                CheckedLongBinaryOperator<IOException> operator = Spied.checkedLongBinaryOperator((l1, l2) -> {
                    throw new IOException(Long.toString(l1 + l2));
                });

                LongSupplier fallback = Spied.longSupplier(() -> {
                    throw new IllegalStateException("bar");
                });

                LongBinaryOperator getting = operator.onErrorGetUnchecked(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> getting.applyAsLong(1L, 2L));
                assertEquals("bar", thrown.getMessage());

                verify(operator).applyAsLong(1L, 2L);
                verify(operator).onErrorGetUnchecked(fallback);
                verify(fallback).getAsLong();
                verifyNoMoreInteractions(operator, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedLongBinaryOperator<IOException> operator = Spied.checkedLongBinaryOperator((l1, l2) -> {
                throw new IllegalStateException(Long.toString(l1 + l2));
            });

            LongSupplier fallback = Spied.longSupplier(() -> 2L);

            LongBinaryOperator getting = operator.onErrorGetUnchecked(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> getting.applyAsLong(1L, 2L));
            assertEquals("3", thrown.getMessage());

            verify(operator).applyAsLong(1L, 2L);
            verify(operator).onErrorGetUnchecked(fallback);
            verifyNoMoreInteractions(operator, fallback);
        }
    }

    @Nested
    class OnErrorReturn {

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedLongBinaryOperator<IOException> operator = Spied.checkedLongBinaryOperator(Long::max);

            LongBinaryOperator returning = operator.onErrorReturn(2L);

            assertEquals(2L, returning.applyAsLong(1L, 2L));

            verify(operator).applyAsLong(1L, 2L);
            verify(operator).onErrorReturn(2L);
            verifyNoMoreInteractions(operator);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedLongBinaryOperator<IOException> operator = Spied.checkedLongBinaryOperator((l1, l2) -> {
                throw new IOException(Long.toString(l1 + l2));
            });

            LongBinaryOperator returning = operator.onErrorReturn(2L);

            assertEquals(2L, returning.applyAsLong(1L, 2L));

            verify(operator).applyAsLong(1L, 2L);
            verify(operator).onErrorReturn(2L);
            verifyNoMoreInteractions(operator);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedLongBinaryOperator<IOException> operator = Spied.checkedLongBinaryOperator((l1, l2) -> {
                throw new IllegalStateException(Long.toString(l1 + l2));
            });

            LongBinaryOperator returning = operator.onErrorReturn(2L);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> returning.applyAsLong(1L, 2L));
            assertEquals("3", thrown.getMessage());

            verify(operator).applyAsLong(1L, 2L);
            verify(operator).onErrorReturn(2L);
            verifyNoMoreInteractions(operator);
        }
    }

    @Nested
    class Unchecked {

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedLongBinaryOperator<IOException> operator = Spied.checkedLongBinaryOperator(Long::max);

            LongBinaryOperator unchecked = operator.unchecked();

            assertEquals(2L, unchecked.applyAsLong(1L, 2L));

            verify(operator).applyAsLong(1L, 2L);
            verify(operator).unchecked();
            verify(operator).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(operator);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedLongBinaryOperator<IOException> operator = Spied.checkedLongBinaryOperator((l1, l2) -> {
                throw new IOException(Long.toString(l1 + l2));
            });

            LongBinaryOperator unchecked = operator.unchecked();

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.applyAsLong(1L, 2L));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("3", cause.getMessage());

            verify(operator).applyAsLong(1L, 2L);
            verify(operator).unchecked();
            verify(operator).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(operator);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedLongBinaryOperator<IOException> operator = Spied.checkedLongBinaryOperator((l1, l2) -> {
                throw new IllegalArgumentException(Long.toString(l1 + l2));
            });

            LongBinaryOperator unchecked = operator.unchecked();

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> unchecked.applyAsLong(1L, 2L));
            assertEquals("3", thrown.getMessage());

            verify(operator).applyAsLong(1L, 2L);
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
            CheckedLongBinaryOperator<IOException> operator = CheckedLongBinaryOperator.of(Long::max);

            assertEquals(2L, operator.applyAsLong(1L, 2L));
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
            CheckedLongBinaryOperator<IOException> operator = Spied.checkedLongBinaryOperator(Long::max);

            LongBinaryOperator unchecked = CheckedLongBinaryOperator.unchecked(operator);

            assertEquals(2L, unchecked.applyAsLong(1L, 2L));

            verify(operator).applyAsLong(1L, 2L);
            verifyNoMoreInteractions(operator);
        }

        @Test
        void testArgumentThrowsChecked() throws IOException {
            CheckedLongBinaryOperator<IOException> operator = Spied.checkedLongBinaryOperator((l1, l2) -> {
                throw new IOException(Long.toString(l1 + l2));
            });

            LongBinaryOperator unchecked = CheckedLongBinaryOperator.unchecked(operator);

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.applyAsLong(1L, 2L));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("3", cause.getMessage());

            verify(operator).applyAsLong(1L, 2L);
            verifyNoMoreInteractions(operator);
        }

        @Test
        void testArgumentThrowsUnchecked() throws IOException {
            CheckedLongBinaryOperator<IOException> operator = Spied.checkedLongBinaryOperator((l1, l2) -> {
                throw new IllegalArgumentException(Long.toString(l1 + l2));
            });

            LongBinaryOperator unchecked = CheckedLongBinaryOperator.unchecked(operator);

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> unchecked.applyAsLong(1L, 2L));
            assertEquals("3", thrown.getMessage());

            verify(operator).applyAsLong(1L, 2L);
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
            CheckedLongBinaryOperator<IOException> operator = CheckedLongBinaryOperator.checked(Long::max);

            assertEquals(2L, operator.applyAsLong(1L, 2L));
        }

        @Test
        void testargumentThrowsUnchecked() {
            CheckedLongBinaryOperator<IOException> operator = CheckedLongBinaryOperator.checked((l1, l2) -> {
                throw new UncheckedException(Long.toString(l1 + l2), new IOException());
            });

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> operator.applyAsLong(1L, 2L));
            assertEquals("3", thrown.getMessage());
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertNull(cause.getMessage());
        }
    }

    @Nested
    class CheckedUnwrapper {

        @Test
        void testNullArguments() {
            LongBinaryOperator operator = Long::max;

            assertThrows(NullPointerException.class, () -> CheckedLongBinaryOperator.checked(null, IOException.class));
            assertThrows(NullPointerException.class, () -> CheckedLongBinaryOperator.checked(operator, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedLongBinaryOperator<IOException> operator = CheckedLongBinaryOperator.checked(Long::max, IOException.class);

            assertEquals(2L, operator.applyAsLong(1L, 2L));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                CheckedLongBinaryOperator<IOException> operator = CheckedLongBinaryOperator.checked((l1, l2) -> {
                    throw new UncheckedException(new IOException(Long.toString(l1 + l2)));
                }, IOException.class);

                IOException thrown = assertThrows(IOException.class, () -> operator.applyAsLong(1L, 2L));
                assertEquals("3", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                CheckedLongBinaryOperator<IOException> operator = CheckedLongBinaryOperator.checked((l1, l2) -> {
                    throw new UncheckedException(new FileNotFoundException(Long.toString(l1 + l2)));
                }, IOException.class);

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class, () -> operator.applyAsLong(1L, 2L));
                assertEquals("3", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                CheckedLongBinaryOperator<IOException> operator = CheckedLongBinaryOperator.checked((l1, l2) -> {
                    throw new UncheckedException(new ParseException(Long.toString(l1 + l2), 0));
                }, IOException.class);

                UncheckedException thrown = assertThrows(UncheckedException.class, () -> operator.applyAsLong(1L, 2L));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("3", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            CheckedLongBinaryOperator<IOException> operator = CheckedLongBinaryOperator.checked((l1, l2) -> {
                throw new IllegalStateException(Long.toString(l1 + l2));
            }, IOException.class);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> operator.applyAsLong(1L, 2L));
            assertEquals("3", thrown.getMessage());
        }
    }

    @Nested
    class InvokeAndUnwrap {

        @Test
        void testNullArguments() {
            LongBinaryOperator operator = Long::max;

            assertThrows(NullPointerException.class, () -> CheckedLongBinaryOperator.invokeAndUnwrap(null, 1L, 2L, IOException.class));
            assertThrows(NullPointerException.class, () -> CheckedLongBinaryOperator.invokeAndUnwrap(operator, 1L, 2L, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            LongBinaryOperator operator = Long::max;

            assertEquals(2L, CheckedLongBinaryOperator.invokeAndUnwrap(operator, 1L, 2L, IOException.class));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                LongBinaryOperator operator = (l1, l2) -> {
                    throw new UncheckedException(new IOException(Long.toString(l1 + l2)));
                };

                IOException thrown = assertThrows(IOException.class,
                        () -> CheckedLongBinaryOperator.invokeAndUnwrap(operator, 1L, 2L, IOException.class));
                assertEquals("3", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                LongBinaryOperator operator = (l1, l2) -> {
                    throw new UncheckedException(new FileNotFoundException(Long.toString(l1 + l2)));
                };

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class,
                        () -> CheckedLongBinaryOperator.invokeAndUnwrap(operator, 1L, 2L, IOException.class));
                assertEquals("3", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                LongBinaryOperator operator = (l1, l2) -> {
                    throw new UncheckedException(new ParseException(Long.toString(l1 + l2), 0));
                };

                UncheckedException thrown = assertThrows(UncheckedException.class,
                        () -> CheckedLongBinaryOperator.invokeAndUnwrap(operator, 1L, 2L, IOException.class));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("3", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            LongBinaryOperator operator = (l1, l2) -> {
                throw new IllegalStateException(Long.toString(l1 + l2));
            };

            IllegalStateException thrown = assertThrows(IllegalStateException.class,
                    () -> CheckedLongBinaryOperator.invokeAndUnwrap(operator, 1L, 2L, IOException.class));
            assertEquals("3", thrown.getMessage());
        }
    }
}
