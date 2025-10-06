/*
 * ThrowingBinaryOperatorTest.java
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
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

@SuppressWarnings("nls")
class ThrowingBinaryOperatorTest {

    @Nested
    class OnErrorThrowAsChecked {

        @Test
        void testNullArgument() {
            ThrowingBinaryOperator<String, IOException> operator = Spied.throwingBinaryOperator(String::concat);

            assertThrows(NullPointerException.class, () -> operator.onErrorThrowAsChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingBinaryOperator<String, IOException> operator = Spied.throwingBinaryOperator(String::concat);

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingBinaryOperator<String, ExecutionException> throwing = operator.onErrorThrowAsChecked(errorMapper);

            assertEquals("foobar", throwing.apply("foo", "bar"));

            verify(operator).apply("foo", "bar");
            verify(operator).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(operator, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingBinaryOperator<String, IOException> operator = Spied.throwingBinaryOperator((s1, s2) -> {
                throw new IOException(s1 + s2);
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingBinaryOperator<String, ExecutionException> throwing = operator.onErrorThrowAsChecked(errorMapper);

            ExecutionException thrown = assertThrows(ExecutionException.class, () -> throwing.apply("foo", "bar"));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foobar", cause.getMessage());

            verify(operator).apply("foo", "bar");
            verify(operator).onErrorThrowAsChecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(operator, errorMapper);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingBinaryOperator<String, IOException> operator = Spied.throwingBinaryOperator((s1, s2) -> throwable.throwUnchecked(s1 + s2));

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingBinaryOperator<String, ExecutionException> throwing = operator.onErrorThrowAsChecked(errorMapper);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> throwing.apply("foo", "bar"));
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
            ThrowingBinaryOperator<String, IOException> operator = Spied.throwingBinaryOperator(String::concat);

            assertThrows(NullPointerException.class, () -> operator.onErrorThrowAsUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingBinaryOperator<String, IOException> operator = Spied.throwingBinaryOperator(String::concat);

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            BinaryOperator<String> throwing = operator.onErrorThrowAsUnchecked(errorMapper);

            assertEquals("foobar", throwing.apply("foo", "bar"));

            verify(operator).apply("foo", "bar");
            verify(operator).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(operator, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingBinaryOperator<String, IOException> operator = Spied.throwingBinaryOperator((s1, s2) -> {
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

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingBinaryOperator<String, IOException> operator = Spied.throwingBinaryOperator((s1, s2) -> throwable.throwUnchecked(s1 + s2));

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            BinaryOperator<String> throwing = operator.onErrorThrowAsUnchecked(errorMapper);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> throwing.apply("foo", "bar"));
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
            ThrowingBinaryOperator<String, IOException> operator = Spied.throwingBinaryOperator(String::concat);

            assertThrows(NullPointerException.class, () -> operator.onErrorHandleChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingBinaryOperator<String, IOException> operator = Spied.throwingBinaryOperator(String::concat);

            ThrowingFunction<IOException, String, ExecutionException> errorHandler = Spied.throwingFunction(Exception::getMessage);

            ThrowingBinaryOperator<String, ExecutionException> handling = operator.onErrorHandleChecked(errorHandler);

            assertEquals("foobar", handling.apply("foo", "bar"));

            verify(operator).apply("foo", "bar");
            verify(operator).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(operator, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException, ExecutionException {
                ThrowingBinaryOperator<String, IOException> operator = Spied.throwingBinaryOperator((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                ThrowingFunction<IOException, String, ExecutionException> errorHandler = Spied.throwingFunction(Exception::getMessage);

                ThrowingBinaryOperator<String, ExecutionException> handling = operator.onErrorHandleChecked(errorHandler);

                assertEquals("foobar", handling.apply("foo", "bar"));

                verify(operator).apply("foo", "bar");
                verify(operator).onErrorHandleChecked(errorHandler);
                verify(errorHandler).apply(any());
                verifyNoMoreInteractions(operator, errorHandler);
            }

            @Test
            void testHandlerThrowsChecked() throws IOException, ExecutionException {
                ThrowingBinaryOperator<String, IOException> operator = Spied.throwingBinaryOperator((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                ThrowingFunction<IOException, String, ExecutionException> errorHandler = Spied.throwingFunction(e -> {
                    throw new ExecutionException(e);
                });

                ThrowingBinaryOperator<String, ExecutionException> handling = operator.onErrorHandleChecked(errorHandler);

                ExecutionException thrown = assertThrows(ExecutionException.class, () -> handling.apply("foo", "bar"));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foobar", cause.getMessage());

                verify(operator).apply("foo", "bar");
                verify(operator).onErrorHandleChecked(errorHandler);
                verify(errorHandler).apply(any());
                verifyNoMoreInteractions(operator, errorHandler);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testHandlerThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ExecutionException {
                ThrowingBinaryOperator<String, IOException> operator = Spied.throwingBinaryOperator((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                ThrowingFunction<IOException, String, ExecutionException> errorHandler = Spied.throwingFunction(throwable::throwUnchecked);

                ThrowingBinaryOperator<String, ExecutionException> handling = operator.onErrorHandleChecked(errorHandler);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.apply("foo", "bar"));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foobar", cause.getMessage());

                verify(operator).apply("foo", "bar");
                verify(operator).onErrorHandleChecked(errorHandler);
                verify(errorHandler).apply(any());
                verifyNoMoreInteractions(operator, errorHandler);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingBinaryOperator<String, IOException> operator = Spied.throwingBinaryOperator((s1, s2) -> throwable.throwUnchecked(s1 + s2));

            ThrowingFunction<IOException, String, ExecutionException> errorHandler = Spied.throwingFunction(Exception::getMessage);

            ThrowingBinaryOperator<String, ExecutionException> handling = operator.onErrorHandleChecked(errorHandler);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.apply("foo", "bar"));
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
            ThrowingBinaryOperator<String, IOException> operator = Spied.throwingBinaryOperator(String::concat);

            assertThrows(NullPointerException.class, () -> operator.onErrorHandleUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingBinaryOperator<String, IOException> operator = Spied.throwingBinaryOperator(String::concat);

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
                ThrowingBinaryOperator<String, IOException> operator = Spied.throwingBinaryOperator((s1, s2) -> {
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

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testHandlerThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingBinaryOperator<String, IOException> operator = Spied.throwingBinaryOperator((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                Function<IOException, String> errorHandler = Spied.function(throwable::throwUnchecked);

                BinaryOperator<String> handling = operator.onErrorHandleUnchecked(errorHandler);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.apply("foo", "bar"));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foobar", cause.getMessage());

                verify(operator).apply("foo", "bar");
                verify(operator).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).apply(any());
                verifyNoMoreInteractions(operator, errorHandler);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingBinaryOperator<String, IOException> operator = Spied.throwingBinaryOperator((s1, s2) -> throwable.throwUnchecked(s1 + s2));

            Function<IOException, String> errorHandler = Spied.function(Exception::getMessage);

            BinaryOperator<String> handling = operator.onErrorHandleUnchecked(errorHandler);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.apply("foo", "bar"));
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
            ThrowingBinaryOperator<String, IOException> operator = Spied.throwingBinaryOperator(String::concat);

            assertThrows(NullPointerException.class, () -> operator.onErrorApplyChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingBinaryOperator<String, IOException> operator = Spied.throwingBinaryOperator(String::concat);

            ThrowingBinaryOperator<String, ExecutionException> fallback = Spied.throwingBinaryOperator((s1, s2) -> s1 + s2 + s1 + s2);

            ThrowingBinaryOperator<String, ExecutionException> applying = operator.onErrorApplyChecked(fallback);

            assertEquals("foobar", applying.apply("foo", "bar"));

            verify(operator).apply("foo", "bar");
            verify(operator).onErrorApplyChecked(fallback);
            verifyNoMoreInteractions(operator, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                ThrowingBinaryOperator<String, IOException> operator = Spied.throwingBinaryOperator((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                ThrowingBinaryOperator<String, ParseException> fallback = Spied.throwingBinaryOperator((s1, s2) -> s1 + s2 + s1 + s2);

                ThrowingBinaryOperator<String, ParseException> applying = operator.onErrorApplyChecked(fallback);

                assertEquals("foobarfoobar", applying.apply("foo", "bar"));

                verify(operator).apply("foo", "bar");
                verify(operator).onErrorApplyChecked(fallback);
                verify(fallback).apply("foo", "bar");
                verifyNoMoreInteractions(operator, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                ThrowingBinaryOperator<String, IOException> operator = Spied.throwingBinaryOperator((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                ThrowingBinaryOperator<String, ParseException> fallback = Spied.throwingBinaryOperator((s1, s2) -> {
                    throw new ParseException(s1 + s2, 0);
                });

                ThrowingBinaryOperator<String, ParseException> applying = operator.onErrorApplyChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> applying.apply("foo", "bar"));
                assertEquals("foobar", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(operator).apply("foo", "bar");
                verify(operator).onErrorApplyChecked(fallback);
                verify(fallback).apply("foo", "bar");
                verifyNoMoreInteractions(operator, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ParseException {
                ThrowingBinaryOperator<String, IOException> operator = Spied.throwingBinaryOperator((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                ThrowingBinaryOperator<String, ParseException> fallback = Spied.throwingBinaryOperator((s1, s2) -> throwable.throwUnchecked(s1 + s2));

                ThrowingBinaryOperator<String, ParseException> applying = operator.onErrorApplyChecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.apply("foo", "bar"));
                assertEquals("foobar", thrown.getMessage());

                verify(operator).apply("foo", "bar");
                verify(operator).onErrorApplyChecked(fallback);
                verify(fallback).apply("foo", "bar");
                verifyNoMoreInteractions(operator, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingBinaryOperator<String, IOException> operator = Spied.throwingBinaryOperator((s1, s2) -> throwable.throwUnchecked(s1 + s2));

            ThrowingBinaryOperator<String, ParseException> fallback = Spied.throwingBinaryOperator((s1, s2) -> s1 + s2 + s1 + s2);

            ThrowingBinaryOperator<String, ParseException> applying = operator.onErrorApplyChecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.apply("foo", "bar"));
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
            ThrowingBinaryOperator<String, IOException> operator = Spied.throwingBinaryOperator(String::concat);

            assertThrows(NullPointerException.class, () -> operator.onErrorApplyUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingBinaryOperator<String, IOException> operator = Spied.throwingBinaryOperator(String::concat);

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
                ThrowingBinaryOperator<String, IOException> operator = Spied.throwingBinaryOperator((s1, s2) -> {
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

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingBinaryOperator<String, IOException> operator = Spied.throwingBinaryOperator((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                BinaryOperator<String> fallback = Spied.binaryOperator((s1, s2) -> throwable.throwUnchecked(s1 + s2));

                BinaryOperator<String> applying = operator.onErrorApplyUnchecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.apply("foo", "bar"));
                assertEquals("foobar", thrown.getMessage());

                verify(operator).apply("foo", "bar");
                verify(operator).onErrorApplyUnchecked(fallback);
                verify(fallback).apply("foo", "bar");
                verifyNoMoreInteractions(operator, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingBinaryOperator<String, IOException> operator = Spied.throwingBinaryOperator((s1, s2) -> throwable.throwUnchecked(s1 + s2));

            BinaryOperator<String> fallback = Spied.binaryOperator((s1, s2) -> s1 + s2 + s1 + s2);

            BinaryOperator<String> applying = operator.onErrorApplyUnchecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.apply("foo", "bar"));
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
            ThrowingBinaryOperator<String, IOException> operator = Spied.throwingBinaryOperator(String::concat);

            assertThrows(NullPointerException.class, () -> operator.onErrorGetChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingBinaryOperator<String, IOException> operator = Spied.throwingBinaryOperator(String::concat);

            ThrowingSupplier<String, ExecutionException> fallback = Spied.throwingSupplier(() -> "bar");

            ThrowingBinaryOperator<String, ExecutionException> getting = operator.onErrorGetChecked(fallback);

            assertEquals("foobar", getting.apply("foo", "bar"));

            verify(operator).apply("foo", "bar");
            verify(operator).onErrorGetChecked(fallback);
            verifyNoMoreInteractions(operator, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                ThrowingBinaryOperator<String, IOException> operator = Spied.throwingBinaryOperator((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                ThrowingSupplier<String, ParseException> fallback = Spied.throwingSupplier(() -> "bar");

                ThrowingBinaryOperator<String, ParseException> getting = operator.onErrorGetChecked(fallback);

                assertEquals("bar", getting.apply("foo", "bar"));

                verify(operator).apply("foo", "bar");
                verify(operator).onErrorGetChecked(fallback);
                verify(fallback).get();
                verifyNoMoreInteractions(operator, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                ThrowingBinaryOperator<String, IOException> operator = Spied.throwingBinaryOperator((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                ThrowingSupplier<String, ParseException> fallback = Spied.throwingSupplier(() -> {
                    throw new ParseException("bar", 0);
                });

                ThrowingBinaryOperator<String, ParseException> getting = operator.onErrorGetChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> getting.apply("foo", "bar"));
                assertEquals("bar", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(operator).apply("foo", "bar");
                verify(operator).onErrorGetChecked(fallback);
                verify(fallback).get();
                verifyNoMoreInteractions(operator, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ParseException {
                ThrowingBinaryOperator<String, IOException> operator = Spied.throwingBinaryOperator((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                ThrowingSupplier<String, ParseException> fallback = Spied.throwingSupplier(() -> throwable.throwUnchecked("bar"));

                ThrowingBinaryOperator<String, ParseException> getting = operator.onErrorGetChecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.apply("foo", "bar"));
                assertEquals("bar", thrown.getMessage());

                verify(operator).apply("foo", "bar");
                verify(operator).onErrorGetChecked(fallback);
                verify(fallback).get();
                verifyNoMoreInteractions(operator, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingBinaryOperator<String, IOException> operator = Spied.throwingBinaryOperator((s1, s2) -> throwable.throwUnchecked(s1 + s2));

            ThrowingSupplier<String, ParseException> fallback = Spied.throwingSupplier(() -> "bar");

            ThrowingBinaryOperator<String, ParseException> getting = operator.onErrorGetChecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.apply("foo", "bar"));
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
            ThrowingBinaryOperator<String, IOException> operator = Spied.throwingBinaryOperator(String::concat);

            assertThrows(NullPointerException.class, () -> operator.onErrorGetUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingBinaryOperator<String, IOException> operator = Spied.throwingBinaryOperator(String::concat);

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
                ThrowingBinaryOperator<String, IOException> operator = Spied.throwingBinaryOperator((s1, s2) -> {
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

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingBinaryOperator<String, IOException> operator = Spied.throwingBinaryOperator((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                Supplier<String> fallback = Spied.supplier(() -> throwable.throwUnchecked("bar"));

                BinaryOperator<String> getting = operator.onErrorGetUnchecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.apply("foo", "bar"));
                assertEquals("bar", thrown.getMessage());

                verify(operator).apply("foo", "bar");
                verify(operator).onErrorGetUnchecked(fallback);
                verify(fallback).get();
                verifyNoMoreInteractions(operator, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingBinaryOperator<String, IOException> operator = Spied.throwingBinaryOperator((s1, s2) -> throwable.throwUnchecked(s1 + s2));

            Supplier<String> fallback = Spied.supplier(() -> "bar");

            BinaryOperator<String> getting = operator.onErrorGetUnchecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.apply("foo", "bar"));
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
            ThrowingBinaryOperator<String, IOException> operator = Spied.throwingBinaryOperator(String::concat);

            BinaryOperator<String> returning = operator.onErrorReturn("bar");

            assertEquals("foobar", returning.apply("foo", "bar"));

            verify(operator).apply("foo", "bar");
            verify(operator).onErrorReturn("bar");
            verifyNoMoreInteractions(operator);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingBinaryOperator<String, IOException> operator = Spied.throwingBinaryOperator((s1, s2) -> {
                throw new IOException(s1 + s2);
            });

            BinaryOperator<String> returning = operator.onErrorReturn("bar");

            assertEquals("bar", returning.apply("foo", "bar"));

            verify(operator).apply("foo", "bar");
            verify(operator).onErrorReturn("bar");
            verifyNoMoreInteractions(operator);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingBinaryOperator<String, IOException> operator = Spied.throwingBinaryOperator((s1, s2) -> throwable.throwUnchecked(s1 + s2));

            BinaryOperator<String> returning = operator.onErrorReturn("bar");

            Throwable thrown = assertThrows(throwable.throwableType(), () -> returning.apply("foo", "bar"));
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
            ThrowingBinaryOperator<String, IOException> operator = Spied.throwingBinaryOperator(String::concat);

            BinaryOperator<String> unchecked = operator.unchecked();

            assertEquals("foobar", unchecked.apply("foo", "bar"));

            verify(operator).apply("foo", "bar");
            verify(operator).unchecked();
            verify(operator).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(operator);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingBinaryOperator<String, IOException> operator = Spied.throwingBinaryOperator((s1, s2) -> {
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

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingBinaryOperator<String, IOException> operator = Spied.throwingBinaryOperator((s1, s2) -> throwable.throwUnchecked(s1 + s2));

            BinaryOperator<String> unchecked = operator.unchecked();

            Throwable thrown = assertThrows(throwable.throwableType(), () -> unchecked.apply("foo", "bar"));
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
            assertThrows(NullPointerException.class, () -> ThrowingFunction.of(null));
        }

        @Test
        void testNonNullArgument() throws IOException {
            ThrowingBinaryOperator<String, IOException> operator = ThrowingBinaryOperator.of(String::concat);

            assertEquals("foobar", operator.apply("foo", "bar"));
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
            ThrowingBinaryOperator<String, IOException> operator = Spied.throwingBinaryOperator(String::concat);

            BinaryOperator<String> unchecked = ThrowingBinaryOperator.unchecked(operator);

            assertEquals("foobar", unchecked.apply("foo", "bar"));

            verify(operator).unchecked();
            verify(operator).onErrorThrowAsUnchecked(any());
            verify(operator).apply("foo", "bar");
            verifyNoMoreInteractions(operator);
        }

        @Test
        void testArgumentThrowsChecked() throws IOException {
            ThrowingBinaryOperator<String, IOException> operator = Spied.throwingBinaryOperator((s1, s2) -> {
                throw new IOException(s1 + s2);
            });

            BinaryOperator<String> unchecked = ThrowingBinaryOperator.unchecked(operator);

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.apply("foo", "bar"));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foobar", cause.getMessage());

            verify(operator).unchecked();
            verify(operator).onErrorThrowAsUnchecked(any());
            verify(operator).apply("foo", "bar");
            verifyNoMoreInteractions(operator);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testArgumentThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingBinaryOperator<String, IOException> operator = Spied.throwingBinaryOperator((s1, s2) -> throwable.throwUnchecked(s1 + s2));

            BinaryOperator<String> unchecked = ThrowingBinaryOperator.unchecked(operator);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> unchecked.apply("foo", "bar"));
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
            assertThrows(NullPointerException.class, () -> ThrowingFunction.checked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ThrowingBinaryOperator<String, IOException> operator = ThrowingBinaryOperator.checked(String::concat);

            assertEquals("foobar", operator.apply("foo", "bar"));
        }

        @Test
        void testArgumentThrowsUnchecked() {
            ThrowingBinaryOperator<String, IOException> operator = ThrowingBinaryOperator.checked((s1, s2) -> {
                throw UncheckedException.withoutStackTrace(s1 + s2, new IOException());
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

            assertThrows(NullPointerException.class, () -> ThrowingBinaryOperator.checked(null, IOException.class));
            assertThrows(NullPointerException.class, () -> ThrowingBinaryOperator.checked(operator, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ThrowingBinaryOperator<String, IOException> operator = ThrowingBinaryOperator.checked(String::concat, IOException.class);

            assertEquals("foobar", operator.apply("foo", "bar"));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                ThrowingBinaryOperator<String, IOException> operator = ThrowingBinaryOperator.checked((s1, s2) -> {
                    throw UncheckedException.withoutStackTrace(new IOException(s1 + s2));
                }, IOException.class);

                IOException thrown = assertThrows(IOException.class, () -> operator.apply("foo", "bar"));
                assertEquals("foobar", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                ThrowingBinaryOperator<String, IOException> operator = ThrowingBinaryOperator.checked((s1, s2) -> {
                    throw UncheckedException.withoutStackTrace(new FileNotFoundException(s1 + s2));
                }, IOException.class);

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class, () -> operator.apply("foo", "bar"));
                assertEquals("foobar", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                ThrowingBinaryOperator<String, IOException> operator = ThrowingBinaryOperator.checked((s1, s2) -> {
                    throw UncheckedException.withoutStackTrace(new ParseException(s1 + s2, 0));
                }, IOException.class);

                UncheckedException thrown = assertThrows(UncheckedException.class, () -> operator.apply("foo", "bar"));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("foobar", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            ThrowingBinaryOperator<String, IOException> operator = ThrowingBinaryOperator.checked((s1, s2) -> {
                throw new IllegalStateException(s1 + s2);
            }, IOException.class);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> operator.apply("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());
        }
    }
}
