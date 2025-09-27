/*
 * CheckedBinaryOperatorTest.java
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
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("nls")
class CheckedBinaryOperatorTest {

    @Nested
    class OnErrorThrowAsChecked {

        @Test
        void testNullArgument() {
            CheckedBinaryOperator<String, IOException> operator = Spied.checkedBinaryOperator(String::concat);

            assertThrows(NullPointerException.class, () -> operator.onErrorThrowAsChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedBinaryOperator<String, IOException> operator = Spied.checkedBinaryOperator(String::concat);

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedBinaryOperator<String, ExecutionException> throwing = operator.onErrorThrowAsChecked(errorMapper);

            assertEquals("foobar", throwing.apply("foo", "bar"));

            verify(operator).apply("foo", "bar");
            verify(operator).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(operator, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedBinaryOperator<String, IOException> operator = Spied.checkedBinaryOperator((s1, s2) -> {
                throw new IOException(s1 + s2);
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedBinaryOperator<String, ExecutionException> throwing = operator.onErrorThrowAsChecked(errorMapper);

            ExecutionException thrown = assertThrows(ExecutionException.class, () -> throwing.apply("foo", "bar"));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foobar", cause.getMessage());

            verify(operator).apply("foo", "bar");
            verify(operator).onErrorThrowAsChecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(operator, errorMapper);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedBinaryOperator<String, IOException> operator = Spied.checkedBinaryOperator((s1, s2) -> {
                throw new IllegalStateException(s1 + s2);
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedBinaryOperator<String, ExecutionException> throwing = operator.onErrorThrowAsChecked(errorMapper);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> throwing.apply("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());

            verify(operator).apply("foo", "bar");
            verify(operator).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(operator, errorMapper);
        }
    }

    @Nested
    class OnErrorThrowAsUnchecked {

        @Test
        void testNullArgument() {
            CheckedBinaryOperator<String, IOException> operator = Spied.checkedBinaryOperator(String::concat);

            assertThrows(NullPointerException.class, () -> operator.onErrorThrowAsUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedBinaryOperator<String, IOException> operator = Spied.checkedBinaryOperator(String::concat);

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            BinaryOperator<String> throwing = operator.onErrorThrowAsUnchecked(errorMapper);

            assertEquals("foobar", throwing.apply("foo", "bar"));

            verify(operator).apply("foo", "bar");
            verify(operator).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(operator, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedBinaryOperator<String, IOException> operator = Spied.checkedBinaryOperator((s1, s2) -> {
                throw new IOException(s1 + s2);
            });

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            BinaryOperator<String> throwing = operator.onErrorThrowAsUnchecked(errorMapper);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> throwing.apply("foo", "bar"));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foobar", cause.getMessage());

            verify(operator).apply("foo", "bar");
            verify(operator).onErrorThrowAsUnchecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(operator, errorMapper);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedBinaryOperator<String, IOException> operator = Spied.checkedBinaryOperator((s1, s2) -> {
                throw new IllegalArgumentException(s1 + s2);
            });

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            BinaryOperator<String> throwing = operator.onErrorThrowAsUnchecked(errorMapper);

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> throwing.apply("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());

            verify(operator).apply("foo", "bar");
            verify(operator).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(operator, errorMapper);
        }
    }

    @Nested
    class OnErrorHandleChecked {

        @Test
        void testNullArgument() {
            CheckedBinaryOperator<String, IOException> operator = Spied.checkedBinaryOperator(String::concat);

            assertThrows(NullPointerException.class, () -> operator.onErrorHandleChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedBinaryOperator<String, IOException> operator = Spied.checkedBinaryOperator(String::concat);

            CheckedFunction<IOException, String, ExecutionException> errorHandler = Spied.checkedFunction(Exception::getMessage);

            CheckedBinaryOperator<String, ExecutionException> handling = operator.onErrorHandleChecked(errorHandler);

            assertEquals("foobar", handling.apply("foo", "bar"));

            verify(operator).apply("foo", "bar");
            verify(operator).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(operator, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException, ExecutionException {
                CheckedBinaryOperator<String, IOException> operator = Spied.checkedBinaryOperator((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                CheckedFunction<IOException, String, ExecutionException> errorHandler = Spied.checkedFunction(Exception::getMessage);

                CheckedBinaryOperator<String, ExecutionException> handling = operator.onErrorHandleChecked(errorHandler);

                assertEquals("foobar", handling.apply("foo", "bar"));

                verify(operator).apply("foo", "bar");
                verify(operator).onErrorHandleChecked(errorHandler);
                verify(errorHandler).apply(any());
                verifyNoMoreInteractions(operator, errorHandler);
            }

            @Test
            void testHandlerThrowsChecked() throws IOException, ExecutionException {
                CheckedBinaryOperator<String, IOException> operator = Spied.checkedBinaryOperator((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                CheckedFunction<IOException, String, ExecutionException> errorHandler = Spied.checkedFunction(e -> {
                    throw new ExecutionException(e);
                });

                CheckedBinaryOperator<String, ExecutionException> handling = operator.onErrorHandleChecked(errorHandler);

                ExecutionException thrown = assertThrows(ExecutionException.class, () -> handling.apply("foo", "bar"));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foobar", cause.getMessage());

                verify(operator).apply("foo", "bar");
                verify(operator).onErrorHandleChecked(errorHandler);
                verify(errorHandler).apply(any());
                verifyNoMoreInteractions(operator, errorHandler);
            }

            @Test
            void testHandlerThrowsUnchecked() throws IOException, ExecutionException {
                CheckedBinaryOperator<String, IOException> operator = Spied.checkedBinaryOperator((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                CheckedFunction<IOException, String, ExecutionException> errorHandler = Spied.checkedFunction(e -> {
                    throw new IllegalStateException(e);
                });

                CheckedBinaryOperator<String, ExecutionException> handling = operator.onErrorHandleChecked(errorHandler);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.apply("foo", "bar"));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foobar", cause.getMessage());

                verify(operator).apply("foo", "bar");
                verify(operator).onErrorHandleChecked(errorHandler);
                verify(errorHandler).apply(any());
                verifyNoMoreInteractions(operator, errorHandler);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedBinaryOperator<String, IOException> operator = Spied.checkedBinaryOperator((s1, s2) -> {
                throw new IllegalStateException(s1 + s2);
            });

            CheckedFunction<IOException, String, ExecutionException> errorHandler = Spied.checkedFunction(Exception::getMessage);

            CheckedBinaryOperator<String, ExecutionException> handling = operator.onErrorHandleChecked(errorHandler);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.apply("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());

            verify(operator).apply("foo", "bar");
            verify(operator).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(operator, errorHandler);
        }
    }

    @Nested
    class OnErrorHandleUnchecked {

        @Test
        void testNullArgument() {
            CheckedBinaryOperator<String, IOException> operator = Spied.checkedBinaryOperator(String::concat);

            assertThrows(NullPointerException.class, () -> operator.onErrorHandleUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedBinaryOperator<String, IOException> operator = Spied.checkedBinaryOperator(String::concat);

            Function<IOException, String> errorHandler = Spied.function(Exception::getMessage);

            BinaryOperator<String> handling = operator.onErrorHandleUnchecked(errorHandler);

            assertEquals("foobar", handling.apply("foo", "bar"));

            verify(operator).apply("foo", "bar");
            verify(operator).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(operator, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException {
                CheckedBinaryOperator<String, IOException> operator = Spied.checkedBinaryOperator((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                Function<IOException, String> errorHandler = Spied.function(Exception::getMessage);

                BinaryOperator<String> handling = operator.onErrorHandleUnchecked(errorHandler);

                assertEquals("foobar", handling.apply("foo", "bar"));

                verify(operator).apply("foo", "bar");
                verify(operator).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).apply(any());
                verifyNoMoreInteractions(operator, errorHandler);
            }

            @Test
            void testHandlerThrowsUnchecked() throws IOException {
                CheckedBinaryOperator<String, IOException> operator = Spied.checkedBinaryOperator((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                Function<IOException, String> errorHandler = Spied.function(e -> {
                    throw new IllegalStateException(e);
                });

                BinaryOperator<String> handling = operator.onErrorHandleUnchecked(errorHandler);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.apply("foo", "bar"));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foobar", cause.getMessage());

                verify(operator).apply("foo", "bar");
                verify(operator).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).apply(any());
                verifyNoMoreInteractions(operator, errorHandler);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedBinaryOperator<String, IOException> operator = Spied.checkedBinaryOperator((s1, s2) -> {
                throw new IllegalStateException(s1 + s2);
            });

            Function<IOException, String> errorHandler = Spied.function(Exception::getMessage);

            BinaryOperator<String> handling = operator.onErrorHandleUnchecked(errorHandler);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.apply("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());

            verify(operator).apply("foo", "bar");
            verify(operator).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(operator, errorHandler);
        }
    }

    @Nested
    class OnErrorApplyChecked {

        @Test
        void testNullArgument() {
            CheckedBinaryOperator<String, IOException> operator = Spied.checkedBinaryOperator(String::concat);

            assertThrows(NullPointerException.class, () -> operator.onErrorApplyChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedBinaryOperator<String, IOException> operator = Spied.checkedBinaryOperator(String::concat);

            CheckedBinaryOperator<String, ExecutionException> fallback = Spied.checkedBinaryOperator((s1, s2) -> s1 + s2 + s1 + s2);

            CheckedBinaryOperator<String, ExecutionException> applying = operator.onErrorApplyChecked(fallback);

            assertEquals("foobar", applying.apply("foo", "bar"));

            verify(operator).apply("foo", "bar");
            verify(operator).onErrorApplyChecked(fallback);
            verifyNoMoreInteractions(operator, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                CheckedBinaryOperator<String, IOException> operator = Spied.checkedBinaryOperator((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                CheckedBinaryOperator<String, ParseException> fallback = Spied.checkedBinaryOperator((s1, s2) -> s1 + s2 + s1 + s2);

                CheckedBinaryOperator<String, ParseException> applying = operator.onErrorApplyChecked(fallback);

                assertEquals("foobarfoobar", applying.apply("foo", "bar"));

                verify(operator).apply("foo", "bar");
                verify(operator).onErrorApplyChecked(fallback);
                verify(fallback).apply("foo", "bar");
                verifyNoMoreInteractions(operator, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                CheckedBinaryOperator<String, IOException> operator = Spied.checkedBinaryOperator((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                CheckedBinaryOperator<String, ParseException> fallback = Spied.checkedBinaryOperator((s1, s2) -> {
                    throw new ParseException(s1 + s2, 0);
                });

                CheckedBinaryOperator<String, ParseException> applying = operator.onErrorApplyChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> applying.apply("foo", "bar"));
                assertEquals("foobar", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(operator).apply("foo", "bar");
                verify(operator).onErrorApplyChecked(fallback);
                verify(fallback).apply("foo", "bar");
                verifyNoMoreInteractions(operator, fallback);
            }

            @Test
            void testFallbackThrowsUnchecked() throws IOException, ParseException {
                CheckedBinaryOperator<String, IOException> operator = Spied.checkedBinaryOperator((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                CheckedBinaryOperator<String, ParseException> fallback = Spied.checkedBinaryOperator((s1, s2) -> {
                    throw new IllegalStateException(s1 + s2);
                });

                CheckedBinaryOperator<String, ParseException> applying = operator.onErrorApplyChecked(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> applying.apply("foo", "bar"));
                assertEquals("foobar", thrown.getMessage());

                verify(operator).apply("foo", "bar");
                verify(operator).onErrorApplyChecked(fallback);
                verify(fallback).apply("foo", "bar");
                verifyNoMoreInteractions(operator, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedBinaryOperator<String, IOException> operator = Spied.checkedBinaryOperator((s1, s2) -> {
                throw new IllegalStateException(s1 + s2);
            });

            CheckedBinaryOperator<String, ParseException> fallback = Spied.checkedBinaryOperator((s1, s2) -> s1 + s2 + s1 + s2);

            CheckedBinaryOperator<String, ParseException> applying = operator.onErrorApplyChecked(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> applying.apply("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());

            verify(operator).apply("foo", "bar");
            verify(operator).onErrorApplyChecked(fallback);
            verifyNoMoreInteractions(operator, fallback);
        }
    }

    @Nested
    class OnErrorApplyUnchecked {

        @Test
        void testNullArgument() {
            CheckedBinaryOperator<String, IOException> operator = Spied.checkedBinaryOperator(String::concat);

            assertThrows(NullPointerException.class, () -> operator.onErrorApplyUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedBinaryOperator<String, IOException> operator = Spied.checkedBinaryOperator(String::concat);

            BinaryOperator<String> fallback = Spied.binaryOperator((s1, s2) -> s1 + s2 + s1 + s2);

            BinaryOperator<String> applying = operator.onErrorApplyUnchecked(fallback);

            assertEquals("foobar", applying.apply("foo", "bar"));

            verify(operator).apply("foo", "bar");
            verify(operator).onErrorApplyUnchecked(fallback);
            verifyNoMoreInteractions(operator, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                CheckedBinaryOperator<String, IOException> operator = Spied.checkedBinaryOperator((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                BinaryOperator<String> fallback = Spied.binaryOperator((s1, s2) -> s1 + s2 + s1 + s2);

                BinaryOperator<String> applying = operator.onErrorApplyUnchecked(fallback);

                assertEquals("foobarfoobar", applying.apply("foo", "bar"));

                verify(operator).apply("foo", "bar");
                verify(operator).onErrorApplyUnchecked(fallback);
                verify(fallback).apply("foo", "bar");
                verifyNoMoreInteractions(operator, fallback);
            }

            @Test
            void testFallbackThrowsUnchecked() throws IOException {
                CheckedBinaryOperator<String, IOException> operator = Spied.checkedBinaryOperator((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                BinaryOperator<String> fallback = Spied.binaryOperator((s1, s2) -> {
                    throw new IllegalStateException(s1 + s2);
                });

                BinaryOperator<String> applying = operator.onErrorApplyUnchecked(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> applying.apply("foo", "bar"));
                assertEquals("foobar", thrown.getMessage());

                verify(operator).apply("foo", "bar");
                verify(operator).onErrorApplyUnchecked(fallback);
                verify(fallback).apply("foo", "bar");
                verifyNoMoreInteractions(operator, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedBinaryOperator<String, IOException> operator = Spied.checkedBinaryOperator((s1, s2) -> {
                throw new IllegalStateException(s1 + s2);
            });

            BinaryOperator<String> fallback = Spied.binaryOperator((s1, s2) -> s1 + s2 + s1 + s2);

            BinaryOperator<String> applying = operator.onErrorApplyUnchecked(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> applying.apply("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());

            verify(operator).apply("foo", "bar");
            verify(operator).onErrorApplyUnchecked(fallback);
            verifyNoMoreInteractions(operator, fallback);
        }
    }

    @Nested
    class OnErrorGetChecked {

        @Test
        void testNullArgument() {
            CheckedBinaryOperator<String, IOException> operator = Spied.checkedBinaryOperator(String::concat);

            assertThrows(NullPointerException.class, () -> operator.onErrorGetChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedBinaryOperator<String, IOException> operator = Spied.checkedBinaryOperator(String::concat);

            CheckedSupplier<String, ExecutionException> fallback = Spied.checkedSupplier(() -> "bar");

            CheckedBinaryOperator<String, ExecutionException> getting = operator.onErrorGetChecked(fallback);

            assertEquals("foobar", getting.apply("foo", "bar"));

            verify(operator).apply("foo", "bar");
            verify(operator).onErrorGetChecked(fallback);
            verifyNoMoreInteractions(operator, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                CheckedBinaryOperator<String, IOException> operator = Spied.checkedBinaryOperator((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                CheckedSupplier<String, ParseException> fallback = Spied.checkedSupplier(() -> "bar");

                CheckedBinaryOperator<String, ParseException> getting = operator.onErrorGetChecked(fallback);

                assertEquals("bar", getting.apply("foo", "bar"));

                verify(operator).apply("foo", "bar");
                verify(operator).onErrorGetChecked(fallback);
                verify(fallback).get();
                verifyNoMoreInteractions(operator, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                CheckedBinaryOperator<String, IOException> operator = Spied.checkedBinaryOperator((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                CheckedSupplier<String, ParseException> fallback = Spied.checkedSupplier(() -> {
                    throw new ParseException("bar", 0);
                });

                CheckedBinaryOperator<String, ParseException> getting = operator.onErrorGetChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> getting.apply("foo", "bar"));
                assertEquals("bar", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(operator).apply("foo", "bar");
                verify(operator).onErrorGetChecked(fallback);
                verify(fallback).get();
                verifyNoMoreInteractions(operator, fallback);
            }

            @Test
            void testFallbackThrowsUnchecked() throws IOException, ParseException {
                CheckedBinaryOperator<String, IOException> operator = Spied.checkedBinaryOperator((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                CheckedSupplier<String, ParseException> fallback = Spied.checkedSupplier(() -> {
                    throw new IllegalStateException("bar");
                });

                CheckedBinaryOperator<String, ParseException> getting = operator.onErrorGetChecked(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> getting.apply("foo", "bar"));
                assertEquals("bar", thrown.getMessage());

                verify(operator).apply("foo", "bar");
                verify(operator).onErrorGetChecked(fallback);
                verify(fallback).get();
                verifyNoMoreInteractions(operator, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedBinaryOperator<String, IOException> operator = Spied.checkedBinaryOperator((s1, s2) -> {
                throw new IllegalStateException(s1 + s2);
            });

            CheckedSupplier<String, ParseException> fallback = Spied.checkedSupplier(() -> "bar");

            CheckedBinaryOperator<String, ParseException> getting = operator.onErrorGetChecked(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> getting.apply("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());

            verify(operator).apply("foo", "bar");
            verify(operator).onErrorGetChecked(fallback);
            verifyNoMoreInteractions(operator, fallback);
        }
    }

    @Nested
    class OnErrorGetUnchecked {

        @Test
        void testNullArgument() {
            CheckedBinaryOperator<String, IOException> operator = Spied.checkedBinaryOperator(String::concat);

            assertThrows(NullPointerException.class, () -> operator.onErrorGetUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedBinaryOperator<String, IOException> operator = Spied.checkedBinaryOperator(String::concat);

            Supplier<String> fallback = Spied.supplier(() -> "bar");

            BinaryOperator<String> getting = operator.onErrorGetUnchecked(fallback);

            assertEquals("foobar", getting.apply("foo", "bar"));

            verify(operator).apply("foo", "bar");
            verify(operator).onErrorGetUnchecked(fallback);
            verifyNoMoreInteractions(operator, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                CheckedBinaryOperator<String, IOException> operator = Spied.checkedBinaryOperator((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                Supplier<String> fallback = Spied.supplier(() -> "bar");

                BinaryOperator<String> getting = operator.onErrorGetUnchecked(fallback);

                assertEquals("bar", getting.apply("foo", "bar"));

                verify(operator).apply("foo", "bar");
                verify(operator).onErrorGetUnchecked(fallback);
                verify(fallback).get();
                verifyNoMoreInteractions(operator, fallback);
            }

            @Test
            void testFallbackThrowsUnchecked() throws IOException {
                CheckedBinaryOperator<String, IOException> operator = Spied.checkedBinaryOperator((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                Supplier<String> fallback = Spied.supplier(() -> {
                    throw new IllegalStateException("bar");
                });

                BinaryOperator<String> getting = operator.onErrorGetUnchecked(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> getting.apply("foo", "bar"));
                assertEquals("bar", thrown.getMessage());

                verify(operator).apply("foo", "bar");
                verify(operator).onErrorGetUnchecked(fallback);
                verify(fallback).get();
                verifyNoMoreInteractions(operator, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedBinaryOperator<String, IOException> operator = Spied.checkedBinaryOperator((s1, s2) -> {
                throw new IllegalStateException(s1 + s2);
            });

            Supplier<String> fallback = Spied.supplier(() -> "bar");

            BinaryOperator<String> getting = operator.onErrorGetUnchecked(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> getting.apply("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());

            verify(operator).apply("foo", "bar");
            verify(operator).onErrorGetUnchecked(fallback);
            verifyNoMoreInteractions(operator, fallback);
        }
    }

    @Nested
    class OnErrorReturn {

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedBinaryOperator<String, IOException> operator = Spied.checkedBinaryOperator(String::concat);

            BinaryOperator<String> returning = operator.onErrorReturn("bar");

            assertEquals("foobar", returning.apply("foo", "bar"));

            verify(operator).apply("foo", "bar");
            verify(operator).onErrorReturn("bar");
            verifyNoMoreInteractions(operator);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedBinaryOperator<String, IOException> operator = Spied.checkedBinaryOperator((s1, s2) -> {
                throw new IOException(s1 + s2);
            });

            BinaryOperator<String> returning = operator.onErrorReturn("bar");

            assertEquals("bar", returning.apply("foo", "bar"));

            verify(operator).apply("foo", "bar");
            verify(operator).onErrorReturn("bar");
            verifyNoMoreInteractions(operator);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedBinaryOperator<String, IOException> operator = Spied.checkedBinaryOperator((s1, s2) -> {
                throw new IllegalStateException(s1 + s2);
            });

            BinaryOperator<String> returning = operator.onErrorReturn("bar");

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> returning.apply("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());

            verify(operator).apply("foo", "bar");
            verify(operator).onErrorReturn("bar");
            verifyNoMoreInteractions(operator);
        }
    }

    @Nested
    class Unchecked {

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedBinaryOperator<String, IOException> operator = Spied.checkedBinaryOperator(String::concat);

            BinaryOperator<String> unchecked = operator.unchecked();

            assertEquals("foobar", unchecked.apply("foo", "bar"));

            verify(operator).apply("foo", "bar");
            verify(operator).unchecked();
            verify(operator).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(operator);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedBinaryOperator<String, IOException> operator = Spied.checkedBinaryOperator((s1, s2) -> {
                throw new IOException(s1 + s2);
            });

            BinaryOperator<String> unchecked = operator.unchecked();

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.apply("foo", "bar"));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foobar", cause.getMessage());

            verify(operator).apply("foo", "bar");
            verify(operator).unchecked();
            verify(operator).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(operator);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedBinaryOperator<String, IOException> operator = Spied.checkedBinaryOperator((s1, s2) -> {
                throw new IllegalArgumentException(s1 + s2);
            });

            BinaryOperator<String> unchecked = operator.unchecked();

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> unchecked.apply("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());

            verify(operator).apply("foo", "bar");
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
            CheckedBinaryOperator<String, IOException> operator = CheckedBinaryOperator.of(String::concat);

            assertEquals("foobar", operator.apply("foo", "bar"));
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
            CheckedBinaryOperator<String, IOException> operator = Spied.checkedBinaryOperator(String::concat);

            BinaryOperator<String> unchecked = CheckedBinaryOperator.unchecked(operator);

            assertEquals("foobar", unchecked.apply("foo", "bar"));

            verify(operator).unchecked();
            verify(operator).onErrorThrowAsUnchecked(any());
            verify(operator).apply("foo", "bar");
            verifyNoMoreInteractions(operator);
        }

        @Test
        void testArgumentThrowsChecked() throws IOException {
            CheckedBinaryOperator<String, IOException> operator = Spied.checkedBinaryOperator((s1, s2) -> {
                throw new IOException(s1 + s2);
            });

            BinaryOperator<String> unchecked = CheckedBinaryOperator.unchecked(operator);

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.apply("foo", "bar"));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foobar", cause.getMessage());

            verify(operator).unchecked();
            verify(operator).onErrorThrowAsUnchecked(any());
            verify(operator).apply("foo", "bar");
            verifyNoMoreInteractions(operator);
        }

        @Test
        void testArgumentThrowsUnchecked() throws IOException {
            CheckedBinaryOperator<String, IOException> operator = Spied.checkedBinaryOperator((s1, s2) -> {
                throw new IllegalArgumentException(s1 + s2);
            });

            BinaryOperator<String> unchecked = CheckedBinaryOperator.unchecked(operator);

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> unchecked.apply("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());

            verify(operator).unchecked();
            verify(operator).onErrorThrowAsUnchecked(any());
            verify(operator).apply("foo", "bar");
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
            CheckedBinaryOperator<String, IOException> operator = CheckedBinaryOperator.checked(String::concat);

            assertEquals("foobar", operator.apply("foo", "bar"));
        }

        @Test
        void testargumentThrowsUnchecked() {
            CheckedBinaryOperator<String, IOException> operator = CheckedBinaryOperator.checked((s1, s2) -> {
                throw new UncheckedException(s1 + s2, new IOException());
            });

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> operator.apply("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertNull(cause.getMessage());
        }
    }

    @Nested
    class CheckedUnwrapper {

        @Test
        void testNullArguments() {
            BinaryOperator<String> operator = String::concat;

            assertThrows(NullPointerException.class, () -> CheckedBinaryOperator.checked(null, IOException.class));
            assertThrows(NullPointerException.class, () -> CheckedBinaryOperator.checked(operator, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedBinaryOperator<String, IOException> operator = CheckedBinaryOperator.checked(String::concat, IOException.class);

            assertEquals("foobar", operator.apply("foo", "bar"));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                CheckedBinaryOperator<String, IOException> operator = CheckedBinaryOperator.checked((s1, s2) -> {
                    throw new UncheckedException(new IOException(s1 + s2));
                }, IOException.class);

                IOException thrown = assertThrows(IOException.class, () -> operator.apply("foo", "bar"));
                assertEquals("foobar", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                CheckedBinaryOperator<String, IOException> operator = CheckedBinaryOperator.checked((s1, s2) -> {
                    throw new UncheckedException(new FileNotFoundException(s1 + s2));
                }, IOException.class);

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class, () -> operator.apply("foo", "bar"));
                assertEquals("foobar", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                CheckedBinaryOperator<String, IOException> operator = CheckedBinaryOperator.checked((s1, s2) -> {
                    throw new UncheckedException(new ParseException(s1 + s2, 0));
                }, IOException.class);

                UncheckedException thrown = assertThrows(UncheckedException.class, () -> operator.apply("foo", "bar"));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("foobar", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            CheckedBinaryOperator<String, IOException> operator = CheckedBinaryOperator.checked((s1, s2) -> {
                throw new IllegalStateException(s1 + s2);
            }, IOException.class);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> operator.apply("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());
        }
    }

    @Nested
    class InvokeAndUnwrap {

        @Test
        void testNullArguments() throws IOException {
            BinaryOperator<String> operator = (s1, s2) -> s1 + s2;

            assertThrows(NullPointerException.class, () -> CheckedBinaryOperator.invokeAndUnwrap(null, "foo", "bar", IOException.class));
            assertThrows(NullPointerException.class, () -> CheckedBinaryOperator.invokeAndUnwrap(operator, "foo", "bar", null));

            assertEquals("nullbar", CheckedBinaryOperator.invokeAndUnwrap(operator, null, "bar", IOException.class));
            assertEquals("foonull", CheckedBinaryOperator.invokeAndUnwrap(operator, "foo", null, IOException.class));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            BinaryOperator<String> operator = String::concat;

            assertEquals("foobar", CheckedBinaryOperator.invokeAndUnwrap(operator, "foo", "bar", IOException.class));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                BinaryOperator<String> operator = (s1, s2) -> {
                    throw new UncheckedException(new IOException(s1 + s2));
                };

                IOException thrown = assertThrows(IOException.class,
                        () -> CheckedBinaryOperator.invokeAndUnwrap(operator, "foo", "bar", IOException.class));
                assertEquals("foobar", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                BinaryOperator<String> operator = (s1, s2) -> {
                    throw new UncheckedException(new FileNotFoundException(s1 + s2));
                };

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class,
                        () -> CheckedBinaryOperator.invokeAndUnwrap(operator, "foo", "bar", IOException.class));
                assertEquals("foobar", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                BinaryOperator<String> operator = (s1, s2) -> {
                    throw new UncheckedException(new ParseException(s1 + s2, 0));
                };

                UncheckedException thrown = assertThrows(UncheckedException.class,
                        () -> CheckedBinaryOperator.invokeAndUnwrap(operator, "foo", "bar", IOException.class));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("foobar", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            BinaryOperator<String> operator = (s1, s2) -> {
                throw new IllegalStateException(s1 + s2);
            };

            IllegalStateException thrown = assertThrows(IllegalStateException.class,
                    () -> CheckedBinaryOperator.invokeAndUnwrap(operator, "foo", "bar", IOException.class));
            assertEquals("foobar", thrown.getMessage());
        }
    }
}
