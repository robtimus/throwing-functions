/*
 * CheckedIntBinaryOperatorTest.java
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

@SuppressWarnings("nls")
class CheckedIntBinaryOperatorTest {

    @Nested
    class OnErrorThrowAsChecked {

        @Test
        void testNullArgument() {
            CheckedIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator(Integer::max);

            assertThrows(NullPointerException.class, () -> operator.onErrorThrowAsChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator(Integer::max);

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedIntBinaryOperator<ExecutionException> throwing = operator.onErrorThrowAsChecked(errorMapper);

            assertEquals(2, throwing.applyAsInt(1, 2));

            verify(operator).applyAsInt(1, 2);
            verify(operator).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(operator, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator((i1, i2) -> {
                throw new IOException(Integer.toString(i1 + i2));
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedIntBinaryOperator<ExecutionException> throwing = operator.onErrorThrowAsChecked(errorMapper);

            ExecutionException thrown = assertThrows(ExecutionException.class, () -> throwing.applyAsInt(1, 2));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("3", cause.getMessage());

            verify(operator).applyAsInt(1, 2);
            verify(operator).onErrorThrowAsChecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(operator, errorMapper);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator((i1, i2) -> {
                throw new IllegalStateException(Integer.toString(i1 + i2));
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedIntBinaryOperator<ExecutionException> throwing = operator.onErrorThrowAsChecked(errorMapper);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> throwing.applyAsInt(1, 2));
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
            CheckedIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator(Integer::max);

            assertThrows(NullPointerException.class, () -> operator.onErrorThrowAsUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator(Integer::max);

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            IntBinaryOperator throwing = operator.onErrorThrowAsUnchecked(errorMapper);

            assertEquals(2, throwing.applyAsInt(1, 2));

            verify(operator).applyAsInt(1, 2);
            verify(operator).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(operator, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator((i1, i2) -> {
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

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator((i1, i2) -> {
                throw new IllegalArgumentException(Integer.toString(i1 + i2));
            });

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            IntBinaryOperator throwing = operator.onErrorThrowAsUnchecked(errorMapper);

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> throwing.applyAsInt(1, 2));
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
            CheckedIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator(Integer::max);

            assertThrows(NullPointerException.class, () -> operator.onErrorHandleChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator(Integer::max);

            CheckedToIntFunction<IOException, ExecutionException> errorHandler = Spied.checkedToIntFunction(e -> 0);

            CheckedIntBinaryOperator<ExecutionException> handling = operator.onErrorHandleChecked(errorHandler);

            assertEquals(2, handling.applyAsInt(1, 2));

            verify(operator).applyAsInt(1, 2);
            verify(operator).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(operator, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException, ExecutionException {
                CheckedIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator((i1, i2) -> {
                    throw new IOException(Integer.toString(i1 + i2));
                });

                CheckedToIntFunction<IOException, ExecutionException> errorHandler = Spied.checkedToIntFunction(e -> 0);

                CheckedIntBinaryOperator<ExecutionException> handling = operator.onErrorHandleChecked(errorHandler);

                assertEquals(0, handling.applyAsInt(1, 2));

                verify(operator).applyAsInt(1, 2);
                verify(operator).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsInt(any());
                verifyNoMoreInteractions(operator, errorHandler);
            }

            @Test
            void testHandlerThrowsChecked() throws IOException, ExecutionException {
                CheckedIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator((i1, i2) -> {
                    throw new IOException(Integer.toString(i1 + i2));
                });

                CheckedToIntFunction<IOException, ExecutionException> errorHandler = Spied.checkedToIntFunction(e -> {
                    throw new ExecutionException(e);
                });

                CheckedIntBinaryOperator<ExecutionException> handling = operator.onErrorHandleChecked(errorHandler);

                ExecutionException thrown = assertThrows(ExecutionException.class, () -> handling.applyAsInt(1, 2));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("3", cause.getMessage());

                verify(operator).applyAsInt(1, 2);
                verify(operator).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsInt(any());
                verifyNoMoreInteractions(operator, errorHandler);
            }

            @Test
            void testHandlerThrowsUnchecked() throws IOException, ExecutionException {
                CheckedIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator((i1, i2) -> {
                    throw new IOException(Integer.toString(i1 + i2));
                });

                CheckedToIntFunction<IOException, ExecutionException> errorHandler = Spied.checkedToIntFunction(e -> {
                    throw new IllegalStateException(e);
                });

                CheckedIntBinaryOperator<ExecutionException> handling = operator.onErrorHandleChecked(errorHandler);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.applyAsInt(1, 2));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("3", cause.getMessage());

                verify(operator).applyAsInt(1, 2);
                verify(operator).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsInt(any());
                verifyNoMoreInteractions(operator, errorHandler);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator((i1, i2) -> {
                throw new IllegalStateException(Integer.toString(i1 + i2));
            });

            CheckedToIntFunction<IOException, ExecutionException> errorHandler = Spied.checkedToIntFunction(e -> 0);

            CheckedIntBinaryOperator<ExecutionException> handling = operator.onErrorHandleChecked(errorHandler);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.applyAsInt(1, 2));
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
            CheckedIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator(Integer::max);

            assertThrows(NullPointerException.class, () -> operator.onErrorHandleUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator(Integer::max);

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
                CheckedIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator((i1, i2) -> {
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

            @Test
            void testHandlerThrowsUnchecked() throws IOException {
                CheckedIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator((i1, i2) -> {
                    throw new IOException(Integer.toString(i1 + i2));
                });

                ToIntFunction<IOException> errorHandler = Spied.toIntFunction(e -> {
                    throw new IllegalStateException(e);
                });

                IntBinaryOperator handling = operator.onErrorHandleUnchecked(errorHandler);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.applyAsInt(1, 2));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("3", cause.getMessage());

                verify(operator).applyAsInt(1, 2);
                verify(operator).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).applyAsInt(any());
                verifyNoMoreInteractions(operator, errorHandler);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator((i1, i2) -> {
                throw new IllegalStateException(Integer.toString(i1 + i2));
            });

            ToIntFunction<IOException> errorHandler = Spied.toIntFunction(e -> 0);

            IntBinaryOperator handling = operator.onErrorHandleUnchecked(errorHandler);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.applyAsInt(1, 2));
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
            CheckedIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator(Integer::max);

            assertThrows(NullPointerException.class, () -> operator.onErrorApplyChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator(Integer::max);

            CheckedIntBinaryOperator<ExecutionException> fallback = Spied.checkedIntBinaryOperator((i1, i2) -> i1 + i2 + i1 + i2);

            CheckedIntBinaryOperator<ExecutionException> applying = operator.onErrorApplyChecked(fallback);

            assertEquals(2, applying.applyAsInt(1, 2));

            verify(operator).applyAsInt(1, 2);
            verify(operator).onErrorApplyChecked(fallback);
            verifyNoMoreInteractions(operator, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                CheckedIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator((i1, i2) -> {
                    throw new IOException(Integer.toString(i1 + i2));
                });

                CheckedIntBinaryOperator<ParseException> fallback = Spied.checkedIntBinaryOperator((i1, i2) -> i1 + i2 + i1 + i2);

                CheckedIntBinaryOperator<ParseException> applying = operator.onErrorApplyChecked(fallback);

                assertEquals(6, applying.applyAsInt(1, 2));

                verify(operator).applyAsInt(1, 2);
                verify(operator).onErrorApplyChecked(fallback);
                verify(fallback).applyAsInt(1, 2);
                verifyNoMoreInteractions(operator, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                CheckedIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator((i1, i2) -> {
                    throw new IOException(Integer.toString(i1 + i2));
                });

                CheckedIntBinaryOperator<ParseException> fallback = Spied.checkedIntBinaryOperator((i1, i2) -> {
                    throw new ParseException(Integer.toString(i1 + i2), 0);
                });

                CheckedIntBinaryOperator<ParseException> applying = operator.onErrorApplyChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> applying.applyAsInt(1, 2));
                assertEquals("3", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(operator).applyAsInt(1, 2);
                verify(operator).onErrorApplyChecked(fallback);
                verify(fallback).applyAsInt(1, 2);
                verifyNoMoreInteractions(operator, fallback);
            }

            @Test
            void testFallbackThrowsUnchecked() throws IOException, ParseException {
                CheckedIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator((i1, i2) -> {
                    throw new IOException(Integer.toString(i1 + i2));
                });

                CheckedIntBinaryOperator<ParseException> fallback = Spied.checkedIntBinaryOperator((i1, i2) -> {
                    throw new IllegalStateException(Integer.toString(i1 + i2));
                });

                CheckedIntBinaryOperator<ParseException> applying = operator.onErrorApplyChecked(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> applying.applyAsInt(1, 2));
                assertEquals("3", thrown.getMessage());

                verify(operator).applyAsInt(1, 2);
                verify(operator).onErrorApplyChecked(fallback);
                verify(fallback).applyAsInt(1, 2);
                verifyNoMoreInteractions(operator, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator((i1, i2) -> {
                throw new IllegalStateException(Integer.toString(i1 + i2));
            });

            CheckedIntBinaryOperator<ParseException> fallback = Spied.checkedIntBinaryOperator((i1, i2) -> i1 + i2 + i1 + i2);

            CheckedIntBinaryOperator<ParseException> applying = operator.onErrorApplyChecked(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> applying.applyAsInt(1, 2));
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
            CheckedIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator(Integer::max);

            assertThrows(NullPointerException.class, () -> operator.onErrorApplyUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator(Integer::max);

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
                CheckedIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator((i1, i2) -> {
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

            @Test
            void testFallbackThrowsUnchecked() throws IOException {
                CheckedIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator((i1, i2) -> {
                    throw new IOException(Integer.toString(i1 + i2));
                });

                IntBinaryOperator fallback = Spied.intBinaryOperator((i1, i2) -> {
                    throw new IllegalStateException(Integer.toString(i1 + i2));
                });

                IntBinaryOperator applying = operator.onErrorApplyUnchecked(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> applying.applyAsInt(1, 2));
                assertEquals("3", thrown.getMessage());

                verify(operator).applyAsInt(1, 2);
                verify(operator).onErrorApplyUnchecked(fallback);
                verify(fallback).applyAsInt(1, 2);
                verifyNoMoreInteractions(operator, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator((i1, i2) -> {
                throw new IllegalStateException(Integer.toString(i1 + i2));
            });

            IntBinaryOperator fallback = Spied.intBinaryOperator((i1, i2) -> i1 + i2 + i1 + i2);

            IntBinaryOperator applying = operator.onErrorApplyUnchecked(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> applying.applyAsInt(1, 2));
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
            CheckedIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator(Integer::max);

            assertThrows(NullPointerException.class, () -> operator.onErrorGetChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator(Integer::max);

            CheckedIntSupplier<ExecutionException> fallback = Spied.checkedIntSupplier(() -> 0);

            CheckedIntBinaryOperator<ExecutionException> getting = operator.onErrorGetChecked(fallback);

            assertEquals(2, getting.applyAsInt(1, 2));

            verify(operator).applyAsInt(1, 2);
            verify(operator).onErrorGetChecked(fallback);
            verifyNoMoreInteractions(operator, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                CheckedIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator((i1, i2) -> {
                    throw new IOException(Integer.toString(i1 + i2));
                });

                CheckedIntSupplier<ParseException> fallback = Spied.checkedIntSupplier(() -> 0);

                CheckedIntBinaryOperator<ParseException> getting = operator.onErrorGetChecked(fallback);

                assertEquals(0, getting.applyAsInt(1, 2));

                verify(operator).applyAsInt(1, 2);
                verify(operator).onErrorGetChecked(fallback);
                verify(fallback).getAsInt();
                verifyNoMoreInteractions(operator, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                CheckedIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator((i1, i2) -> {
                    throw new IOException(Integer.toString(i1 + i2));
                });

                CheckedIntSupplier<ParseException> fallback = Spied.checkedIntSupplier(() -> {
                    throw new ParseException("bar", 0);
                });

                CheckedIntBinaryOperator<ParseException> getting = operator.onErrorGetChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> getting.applyAsInt(1, 2));
                assertEquals("bar", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(operator).applyAsInt(1, 2);
                verify(operator).onErrorGetChecked(fallback);
                verify(fallback).getAsInt();
                verifyNoMoreInteractions(operator, fallback);
            }

            @Test
            void testFallbackThrowsUnchecked() throws IOException, ParseException {
                CheckedIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator((i1, i2) -> {
                    throw new IOException(Integer.toString(i1 + i2));
                });

                CheckedIntSupplier<ParseException> fallback = Spied.checkedIntSupplier(() -> {
                    throw new IllegalStateException("bar");
                });

                CheckedIntBinaryOperator<ParseException> getting = operator.onErrorGetChecked(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> getting.applyAsInt(1, 2));
                assertEquals("bar", thrown.getMessage());

                verify(operator).applyAsInt(1, 2);
                verify(operator).onErrorGetChecked(fallback);
                verify(fallback).getAsInt();
                verifyNoMoreInteractions(operator, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator((i1, i2) -> {
                throw new IllegalStateException(Integer.toString(i1 + i2));
            });

            CheckedIntSupplier<ParseException> fallback = Spied.checkedIntSupplier(() -> 0);

            CheckedIntBinaryOperator<ParseException> getting = operator.onErrorGetChecked(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> getting.applyAsInt(1, 2));
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
            CheckedIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator(Integer::max);

            assertThrows(NullPointerException.class, () -> operator.onErrorGetUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator(Integer::max);

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
                CheckedIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator((i1, i2) -> {
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

            @Test
            void testFallbackThrowsUnchecked() throws IOException {
                CheckedIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator((i1, i2) -> {
                    throw new IOException(Integer.toString(i1 + i2));
                });

                IntSupplier fallback = Spied.intSupplier(() -> {
                    throw new IllegalStateException("bar");
                });

                IntBinaryOperator getting = operator.onErrorGetUnchecked(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> getting.applyAsInt(1, 2));
                assertEquals("bar", thrown.getMessage());

                verify(operator).applyAsInt(1, 2);
                verify(operator).onErrorGetUnchecked(fallback);
                verify(fallback).getAsInt();
                verifyNoMoreInteractions(operator, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator((i1, i2) -> {
                throw new IllegalStateException(Integer.toString(i1 + i2));
            });

            IntSupplier fallback = Spied.intSupplier(() -> 2);

            IntBinaryOperator getting = operator.onErrorGetUnchecked(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> getting.applyAsInt(1, 2));
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
            CheckedIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator(Integer::max);

            IntBinaryOperator returning = operator.onErrorReturn(2);

            assertEquals(2, returning.applyAsInt(1, 2));

            verify(operator).applyAsInt(1, 2);
            verify(operator).onErrorReturn(2);
            verifyNoMoreInteractions(operator);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator((i1, i2) -> {
                throw new IOException(Integer.toString(i1 + i2));
            });

            IntBinaryOperator returning = operator.onErrorReturn(2);

            assertEquals(2, returning.applyAsInt(1, 2));

            verify(operator).applyAsInt(1, 2);
            verify(operator).onErrorReturn(2);
            verifyNoMoreInteractions(operator);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator((i1, i2) -> {
                throw new IllegalStateException(Integer.toString(i1 + i2));
            });

            IntBinaryOperator returning = operator.onErrorReturn(2);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> returning.applyAsInt(1, 2));
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
            CheckedIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator(Integer::max);

            IntBinaryOperator unchecked = operator.unchecked();

            assertEquals(2, unchecked.applyAsInt(1, 2));

            verify(operator).applyAsInt(1, 2);
            verify(operator).unchecked();
            verify(operator).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(operator);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator((i1, i2) -> {
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

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator((i1, i2) -> {
                throw new IllegalArgumentException(Integer.toString(i1 + i2));
            });

            IntBinaryOperator unchecked = operator.unchecked();

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> unchecked.applyAsInt(1, 2));
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
            assertThrows(NullPointerException.class, () -> CheckedFunction.of(null));
        }

        @Test
        void testNonNullArgument() throws IOException {
            CheckedIntBinaryOperator<IOException> operator = CheckedIntBinaryOperator.of(Integer::max);

            assertEquals(2, operator.applyAsInt(1, 2));
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
            CheckedIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator(Integer::max);

            IntBinaryOperator unchecked = CheckedIntBinaryOperator.unchecked(operator);

            assertEquals(2, unchecked.applyAsInt(1, 2));

            verify(operator).unchecked();
            verify(operator).onErrorThrowAsUnchecked(any());
            verify(operator).applyAsInt(1, 2);
            verifyNoMoreInteractions(operator);
        }

        @Test
        void testArgumentThrowsChecked() throws IOException {
            CheckedIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator((i1, i2) -> {
                throw new IOException(Integer.toString(i1 + i2));
            });

            IntBinaryOperator unchecked = CheckedIntBinaryOperator.unchecked(operator);

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.applyAsInt(1, 2));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("3", cause.getMessage());

            verify(operator).unchecked();
            verify(operator).onErrorThrowAsUnchecked(any());
            verify(operator).applyAsInt(1, 2);
            verifyNoMoreInteractions(operator);
        }

        @Test
        void testArgumentThrowsUnchecked() throws IOException {
            CheckedIntBinaryOperator<IOException> operator = Spied.checkedIntBinaryOperator((i1, i2) -> {
                throw new IllegalArgumentException(Integer.toString(i1 + i2));
            });

            IntBinaryOperator unchecked = CheckedIntBinaryOperator.unchecked(operator);

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> unchecked.applyAsInt(1, 2));
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
            assertThrows(NullPointerException.class, () -> CheckedFunction.checked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedIntBinaryOperator<IOException> operator = CheckedIntBinaryOperator.checked(Integer::max);

            assertEquals(2, operator.applyAsInt(1, 2));
        }

        @Test
        void testArgumentThrowsUnchecked() {
            CheckedIntBinaryOperator<IOException> operator = CheckedIntBinaryOperator.checked((i1, i2) -> {
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

            assertThrows(NullPointerException.class, () -> CheckedIntBinaryOperator.checked(null, IOException.class));
            assertThrows(NullPointerException.class, () -> CheckedIntBinaryOperator.checked(operator, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedIntBinaryOperator<IOException> operator = CheckedIntBinaryOperator.checked(Integer::max, IOException.class);

            assertEquals(2, operator.applyAsInt(1, 2));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                CheckedIntBinaryOperator<IOException> operator = CheckedIntBinaryOperator.checked((i1, i2) -> {
                    throw new UncheckedException(new IOException(Integer.toString(i1 + i2)));
                }, IOException.class);

                IOException thrown = assertThrows(IOException.class, () -> operator.applyAsInt(1, 2));
                assertEquals("3", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                CheckedIntBinaryOperator<IOException> operator = CheckedIntBinaryOperator.checked((i1, i2) -> {
                    throw new UncheckedException(new FileNotFoundException(Integer.toString(i1 + i2)));
                }, IOException.class);

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class, () -> operator.applyAsInt(1, 2));
                assertEquals("3", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                CheckedIntBinaryOperator<IOException> operator = CheckedIntBinaryOperator.checked((i1, i2) -> {
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
            CheckedIntBinaryOperator<IOException> operator = CheckedIntBinaryOperator.checked((i1, i2) -> {
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

            assertThrows(NullPointerException.class, () -> CheckedIntBinaryOperator.invokeAndUnwrap(null, 1, 2, IOException.class));
            assertThrows(NullPointerException.class, () -> CheckedIntBinaryOperator.invokeAndUnwrap(operator, 1, 2, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            IntBinaryOperator operator = Integer::max;

            assertEquals(2, CheckedIntBinaryOperator.invokeAndUnwrap(operator, 1, 2, IOException.class));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                IntBinaryOperator operator = (i1, i2) -> {
                    throw new UncheckedException(new IOException(Integer.toString(i1 + i2)));
                };

                IOException thrown = assertThrows(IOException.class,
                        () -> CheckedIntBinaryOperator.invokeAndUnwrap(operator, 1, 2, IOException.class));
                assertEquals("3", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                IntBinaryOperator operator = (i1, i2) -> {
                    throw new UncheckedException(new FileNotFoundException(Integer.toString(i1 + i2)));
                };

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class,
                        () -> CheckedIntBinaryOperator.invokeAndUnwrap(operator, 1, 2, IOException.class));
                assertEquals("3", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                IntBinaryOperator operator = (i1, i2) -> {
                    throw new UncheckedException(new ParseException(Integer.toString(i1 + i2), 0));
                };

                UncheckedException thrown = assertThrows(UncheckedException.class,
                        () -> CheckedIntBinaryOperator.invokeAndUnwrap(operator, 1, 2, IOException.class));
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
                    () -> CheckedIntBinaryOperator.invokeAndUnwrap(operator, 1, 2, IOException.class));
            assertEquals("3", thrown.getMessage());
        }
    }
}
