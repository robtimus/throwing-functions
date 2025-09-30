/*
 * CheckedUnaryOperatorTest.java
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
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

@SuppressWarnings("nls")
class CheckedUnaryOperatorTest {

    @Nested
    class OnErrorThrowAsChecked {

        @Test
        void testNullArgument() {
            CheckedUnaryOperator<String, IOException> operator = Spied.checkedUnaryOperator(String::toUpperCase);

            assertThrows(NullPointerException.class, () -> operator.onErrorThrowAsChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedUnaryOperator<String, IOException> operator = Spied.checkedUnaryOperator(String::toUpperCase);

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedUnaryOperator<String, ExecutionException> throwing = operator.onErrorThrowAsChecked(errorMapper);

            assertEquals("FOO", throwing.apply("foo"));

            verify(operator).apply("foo");
            verify(operator).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(operator, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedUnaryOperator<String, IOException> operator = Spied.checkedUnaryOperator(s -> {
                throw new IOException(s);
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedUnaryOperator<String, ExecutionException> throwing = operator.onErrorThrowAsChecked(errorMapper);

            ExecutionException thrown = assertThrows(ExecutionException.class, () -> throwing.apply("foo"));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo", cause.getMessage());

            verify(operator).apply("foo");
            verify(operator).onErrorThrowAsChecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(operator, errorMapper);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            CheckedUnaryOperator<String, IOException> operator = Spied.checkedUnaryOperator(throwable::throwUnchecked);

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedUnaryOperator<String, ExecutionException> throwing = operator.onErrorThrowAsChecked(errorMapper);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> throwing.apply("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(operator).apply("foo");
            verify(operator).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(operator, errorMapper);
        }
    }

    @Nested
    class OnErrorThrowAsUnchecked {

        @Test
        void testNullArgument() {
            CheckedUnaryOperator<String, IOException> operator = Spied.checkedUnaryOperator(String::toUpperCase);

            assertThrows(NullPointerException.class, () -> operator.onErrorThrowAsUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedUnaryOperator<String, IOException> operator = Spied.checkedUnaryOperator(String::toUpperCase);

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            UnaryOperator<String> throwing = operator.onErrorThrowAsUnchecked(errorMapper);

            assertEquals("FOO", throwing.apply("foo"));

            verify(operator).apply("foo");
            verify(operator).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(operator, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedUnaryOperator<String, IOException> operator = Spied.checkedUnaryOperator(s -> {
                throw new IOException(s);
            });

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            UnaryOperator<String> throwing = operator.onErrorThrowAsUnchecked(errorMapper);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> throwing.apply("foo"));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo", cause.getMessage());

            verify(operator).apply("foo");
            verify(operator).onErrorThrowAsUnchecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(operator, errorMapper);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            CheckedUnaryOperator<String, IOException> operator = Spied.checkedUnaryOperator(throwable::throwUnchecked);

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            UnaryOperator<String> throwing = operator.onErrorThrowAsUnchecked(errorMapper);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> throwing.apply("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(operator).apply("foo");
            verify(operator).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(operator, errorMapper);
        }
    }

    @Nested
    class OnErrorHandleChecked {

        @Test
        void testNullArgument() {
            CheckedUnaryOperator<String, IOException> operator = Spied.checkedUnaryOperator(String::toUpperCase);

            assertThrows(NullPointerException.class, () -> operator.onErrorHandleChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedUnaryOperator<String, IOException> operator = Spied.checkedUnaryOperator(String::toUpperCase);

            CheckedFunction<IOException, String, ExecutionException> errorHandler = Spied.checkedFunction(Exception::getMessage);

            CheckedUnaryOperator<String, ExecutionException> handling = operator.onErrorHandleChecked(errorHandler);

            assertEquals("FOO", handling.apply("foo"));

            verify(operator).apply("foo");
            verify(operator).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(operator, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException, ExecutionException {
                CheckedUnaryOperator<String, IOException> operator = Spied.checkedUnaryOperator(s -> {
                    throw new IOException(s);
                });

                CheckedFunction<IOException, String, ExecutionException> errorHandler = Spied.checkedFunction(Exception::getMessage);

                CheckedUnaryOperator<String, ExecutionException> handling = operator.onErrorHandleChecked(errorHandler);

                assertEquals("foo", handling.apply("foo"));

                verify(operator).apply("foo");
                verify(operator).onErrorHandleChecked(errorHandler);
                verify(errorHandler).apply(any());
                verifyNoMoreInteractions(operator, errorHandler);
            }

            @Test
            void testHandlerThrowsChecked() throws IOException, ExecutionException {
                CheckedUnaryOperator<String, IOException> operator = Spied.checkedUnaryOperator(s -> {
                    throw new IOException(s);
                });

                CheckedFunction<IOException, String, ExecutionException> errorHandler = Spied.checkedFunction(e -> {
                    throw new ExecutionException(e);
                });

                CheckedUnaryOperator<String, ExecutionException> handling = operator.onErrorHandleChecked(errorHandler);

                ExecutionException thrown = assertThrows(ExecutionException.class, () -> handling.apply("foo"));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());

                verify(operator).apply("foo");
                verify(operator).onErrorHandleChecked(errorHandler);
                verify(errorHandler).apply(any());
                verifyNoMoreInteractions(operator, errorHandler);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testHandlerThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ExecutionException {
                CheckedUnaryOperator<String, IOException> operator = Spied.checkedUnaryOperator(s -> {
                    throw new IOException(s);
                });

                CheckedFunction<IOException, String, ExecutionException> errorHandler = Spied.checkedFunction(throwable::throwUnchecked);

                CheckedUnaryOperator<String, ExecutionException> handling = operator.onErrorHandleChecked(errorHandler);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.apply("foo"));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());

                verify(operator).apply("foo");
                verify(operator).onErrorHandleChecked(errorHandler);
                verify(errorHandler).apply(any());
                verifyNoMoreInteractions(operator, errorHandler);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            CheckedUnaryOperator<String, IOException> operator = Spied.checkedUnaryOperator(throwable::throwUnchecked);

            CheckedFunction<IOException, String, ExecutionException> errorHandler = Spied.checkedFunction(Exception::getMessage);

            CheckedUnaryOperator<String, ExecutionException> handling = operator.onErrorHandleChecked(errorHandler);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.apply("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(operator).apply("foo");
            verify(operator).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(operator, errorHandler);
        }
    }

    @Nested
    class OnErrorHandleUnchecked {

        @Test
        void testNullArgument() {
            CheckedUnaryOperator<String, IOException> operator = Spied.checkedUnaryOperator(String::toUpperCase);

            assertThrows(NullPointerException.class, () -> operator.onErrorHandleUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedUnaryOperator<String, IOException> operator = Spied.checkedUnaryOperator(String::toUpperCase);

            Function<IOException, String> errorHandler = Spied.function(Exception::getMessage);

            UnaryOperator<String> handling = operator.onErrorHandleUnchecked(errorHandler);

            assertEquals("FOO", handling.apply("foo"));

            verify(operator).apply("foo");
            verify(operator).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(operator, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException {
                CheckedUnaryOperator<String, IOException> operator = Spied.checkedUnaryOperator(s -> {
                    throw new IOException(s);
                });

                Function<IOException, String> errorHandler = Spied.function(Exception::getMessage);

                UnaryOperator<String> handling = operator.onErrorHandleUnchecked(errorHandler);

                assertEquals("foo", handling.apply("foo"));

                verify(operator).apply("foo");
                verify(operator).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).apply(any());
                verifyNoMoreInteractions(operator, errorHandler);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testHandlerThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                CheckedUnaryOperator<String, IOException> operator = Spied.checkedUnaryOperator(s -> {
                    throw new IOException(s);
                });

                Function<IOException, String> errorHandler = Spied.function(throwable::throwUnchecked);

                UnaryOperator<String> handling = operator.onErrorHandleUnchecked(errorHandler);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.apply("foo"));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());

                verify(operator).apply("foo");
                verify(operator).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).apply(any());
                verifyNoMoreInteractions(operator, errorHandler);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            CheckedUnaryOperator<String, IOException> operator = Spied.checkedUnaryOperator(throwable::throwUnchecked);

            Function<IOException, String> errorHandler = Spied.function(Exception::getMessage);

            UnaryOperator<String> handling = operator.onErrorHandleUnchecked(errorHandler);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.apply("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(operator).apply("foo");
            verify(operator).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(operator, errorHandler);
        }
    }

    @Nested
    class OnErrorApplyChecked {

        @Test
        void testNullArgument() {
            CheckedUnaryOperator<String, IOException> operator = Spied.checkedUnaryOperator(String::toUpperCase);

            assertThrows(NullPointerException.class, () -> operator.onErrorApplyChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedUnaryOperator<String, IOException> operator = Spied.checkedUnaryOperator(String::toUpperCase);

            CheckedUnaryOperator<String, ExecutionException> fallback = Spied.checkedUnaryOperator(s -> s + s);

            CheckedUnaryOperator<String, ExecutionException> applying = operator.onErrorApplyChecked(fallback);

            assertEquals("FOO", applying.apply("foo"));

            verify(operator).apply("foo");
            verify(operator).onErrorApplyChecked(fallback);
            verifyNoMoreInteractions(operator, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                CheckedUnaryOperator<String, IOException> operator = Spied.checkedUnaryOperator(s -> {
                    throw new IOException(s);
                });

                CheckedUnaryOperator<String, ParseException> fallback = Spied.checkedUnaryOperator(s -> s + s);

                CheckedUnaryOperator<String, ParseException> applying = operator.onErrorApplyChecked(fallback);

                assertEquals("foofoo", applying.apply("foo"));

                verify(operator).apply("foo");
                verify(operator).onErrorApplyChecked(fallback);
                verify(fallback).apply("foo");
                verifyNoMoreInteractions(operator, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                CheckedUnaryOperator<String, IOException> operator = Spied.checkedUnaryOperator(s -> {
                    throw new IOException(s);
                });

                CheckedUnaryOperator<String, ParseException> fallback = Spied.checkedUnaryOperator(s -> {
                    throw new ParseException(s, 0);
                });

                CheckedUnaryOperator<String, ParseException> applying = operator.onErrorApplyChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> applying.apply("foo"));
                assertEquals("foo", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(operator).apply("foo");
                verify(operator).onErrorApplyChecked(fallback);
                verify(fallback).apply("foo");
                verifyNoMoreInteractions(operator, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ParseException {
                CheckedUnaryOperator<String, IOException> operator = Spied.checkedUnaryOperator(s -> {
                    throw new IOException(s);
                });

                CheckedUnaryOperator<String, ParseException> fallback = Spied.checkedUnaryOperator(throwable::throwUnchecked);

                CheckedUnaryOperator<String, ParseException> applying = operator.onErrorApplyChecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.apply("foo"));
                assertEquals("foo", thrown.getMessage());

                verify(operator).apply("foo");
                verify(operator).onErrorApplyChecked(fallback);
                verify(fallback).apply("foo");
                verifyNoMoreInteractions(operator, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            CheckedUnaryOperator<String, IOException> operator = Spied.checkedUnaryOperator(throwable::throwUnchecked);

            CheckedUnaryOperator<String, ParseException> fallback = Spied.checkedUnaryOperator(s -> s + s);

            CheckedUnaryOperator<String, ParseException> applying = operator.onErrorApplyChecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.apply("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(operator).apply("foo");
            verify(operator).onErrorApplyChecked(fallback);
            verifyNoMoreInteractions(operator, fallback);
        }
    }

    @Nested
    class OnErrorApplyUnchecked {

        @Test
        void testNullArgument() {
            CheckedUnaryOperator<String, IOException> operator = Spied.checkedUnaryOperator(String::toUpperCase);

            assertThrows(NullPointerException.class, () -> operator.onErrorApplyUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedUnaryOperator<String, IOException> operator = Spied.checkedUnaryOperator(String::toUpperCase);

            UnaryOperator<String> fallback = Spied.unaryOperator(s -> s + s);

            UnaryOperator<String> applying = operator.onErrorApplyUnchecked(fallback);

            assertEquals("FOO", applying.apply("foo"));

            verify(operator).apply("foo");
            verify(operator).onErrorApplyUnchecked(fallback);
            verifyNoMoreInteractions(operator, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                CheckedUnaryOperator<String, IOException> operator = Spied.checkedUnaryOperator(s -> {
                    throw new IOException(s);
                });

                UnaryOperator<String> fallback = Spied.unaryOperator(s -> s + s);

                UnaryOperator<String> applying = operator.onErrorApplyUnchecked(fallback);

                assertEquals("foofoo", applying.apply("foo"));

                verify(operator).apply("foo");
                verify(operator).onErrorApplyUnchecked(fallback);
                verify(fallback).apply("foo");
                verifyNoMoreInteractions(operator, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                CheckedUnaryOperator<String, IOException> operator = Spied.checkedUnaryOperator(s -> {
                    throw new IOException(s);
                });

                UnaryOperator<String> fallback = Spied.unaryOperator(throwable::throwUnchecked);

                UnaryOperator<String> applying = operator.onErrorApplyUnchecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.apply("foo"));
                assertEquals("foo", thrown.getMessage());

                verify(operator).apply("foo");
                verify(operator).onErrorApplyUnchecked(fallback);
                verify(fallback).apply("foo");
                verifyNoMoreInteractions(operator, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            CheckedUnaryOperator<String, IOException> operator = Spied.checkedUnaryOperator(throwable::throwUnchecked);

            UnaryOperator<String> fallback = Spied.unaryOperator(s -> s + s);

            UnaryOperator<String> applying = operator.onErrorApplyUnchecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.apply("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(operator).apply("foo");
            verify(operator).onErrorApplyUnchecked(fallback);
            verifyNoMoreInteractions(operator, fallback);
        }
    }

    @Nested
    class OnErrorGetChecked {

        @Test
        void testNullArgument() {
            CheckedUnaryOperator<String, IOException> operator = Spied.checkedUnaryOperator(String::toUpperCase);

            assertThrows(NullPointerException.class, () -> operator.onErrorGetChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedUnaryOperator<String, IOException> operator = Spied.checkedUnaryOperator(String::toUpperCase);

            CheckedSupplier<String, ExecutionException> fallback = Spied.checkedSupplier(() -> "bar");

            CheckedUnaryOperator<String, ExecutionException> getting = operator.onErrorGetChecked(fallback);

            assertEquals("FOO", getting.apply("foo"));

            verify(operator).apply("foo");
            verify(operator).onErrorGetChecked(fallback);
            verifyNoMoreInteractions(operator, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                CheckedUnaryOperator<String, IOException> operator = Spied.checkedUnaryOperator(s -> {
                    throw new IOException(s);
                });

                CheckedSupplier<String, ParseException> fallback = Spied.checkedSupplier(() -> "bar");

                CheckedUnaryOperator<String, ParseException> getting = operator.onErrorGetChecked(fallback);

                assertEquals("bar", getting.apply("foo"));

                verify(operator).apply("foo");
                verify(operator).onErrorGetChecked(fallback);
                verify(fallback).get();
                verifyNoMoreInteractions(operator, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                CheckedUnaryOperator<String, IOException> operator = Spied.checkedUnaryOperator(s -> {
                    throw new IOException(s);
                });

                CheckedSupplier<String, ParseException> fallback = Spied.checkedSupplier(() -> {
                    throw new ParseException("bar", 0);
                });

                CheckedUnaryOperator<String, ParseException> getting = operator.onErrorGetChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> getting.apply("foo"));
                assertEquals("bar", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(operator).apply("foo");
                verify(operator).onErrorGetChecked(fallback);
                verify(fallback).get();
                verifyNoMoreInteractions(operator, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ParseException {
                CheckedUnaryOperator<String, IOException> operator = Spied.checkedUnaryOperator(s -> {
                    throw new IOException(s);
                });

                CheckedSupplier<String, ParseException> fallback = Spied.checkedSupplier(() -> throwable.throwUnchecked("bar"));

                CheckedUnaryOperator<String, ParseException> getting = operator.onErrorGetChecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.apply("foo"));
                assertEquals("bar", thrown.getMessage());

                verify(operator).apply("foo");
                verify(operator).onErrorGetChecked(fallback);
                verify(fallback).get();
                verifyNoMoreInteractions(operator, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            CheckedUnaryOperator<String, IOException> operator = Spied.checkedUnaryOperator(throwable::throwUnchecked);

            CheckedSupplier<String, ParseException> fallback = Spied.checkedSupplier(() -> "bar");

            CheckedUnaryOperator<String, ParseException> getting = operator.onErrorGetChecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.apply("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(operator).apply("foo");
            verify(operator).onErrorGetChecked(fallback);
            verifyNoMoreInteractions(operator, fallback);
        }
    }

    @Nested
    class OnErrorGetUnchecked {

        @Test
        void testNullArgument() {
            CheckedUnaryOperator<String, IOException> operator = Spied.checkedUnaryOperator(String::toUpperCase);

            assertThrows(NullPointerException.class, () -> operator.onErrorGetUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedUnaryOperator<String, IOException> operator = Spied.checkedUnaryOperator(String::toUpperCase);

            Supplier<String> fallback = Spied.supplier(() -> "bar");

            UnaryOperator<String> getting = operator.onErrorGetUnchecked(fallback);

            assertEquals("FOO", getting.apply("foo"));

            verify(operator).apply("foo");
            verify(operator).onErrorGetUnchecked(fallback);
            verifyNoMoreInteractions(operator, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                CheckedUnaryOperator<String, IOException> operator = Spied.checkedUnaryOperator(s -> {
                    throw new IOException(s);
                });

                Supplier<String> fallback = Spied.supplier(() -> "bar");

                UnaryOperator<String> getting = operator.onErrorGetUnchecked(fallback);

                assertEquals("bar", getting.apply("foo"));

                verify(operator).apply("foo");
                verify(operator).onErrorGetUnchecked(fallback);
                verify(fallback).get();
                verifyNoMoreInteractions(operator, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                CheckedUnaryOperator<String, IOException> operator = Spied.checkedUnaryOperator(s -> {
                    throw new IOException(s);
                });

                Supplier<String> fallback = Spied.supplier(() -> throwable.throwUnchecked("bar"));

                UnaryOperator<String> getting = operator.onErrorGetUnchecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.apply("foo"));
                assertEquals("bar", thrown.getMessage());

                verify(operator).apply("foo");
                verify(operator).onErrorGetUnchecked(fallback);
                verify(fallback).get();
                verifyNoMoreInteractions(operator, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            CheckedUnaryOperator<String, IOException> operator = Spied.checkedUnaryOperator(throwable::throwUnchecked);

            Supplier<String> fallback = Spied.supplier(() -> "bar");

            UnaryOperator<String> getting = operator.onErrorGetUnchecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.apply("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(operator).apply("foo");
            verify(operator).onErrorGetUnchecked(fallback);
            verifyNoMoreInteractions(operator, fallback);
        }
    }

    @Nested
    class OnErrorReturn {

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedUnaryOperator<String, IOException> operator = Spied.checkedUnaryOperator(String::toUpperCase);

            UnaryOperator<String> returning = operator.onErrorReturn("bar");

            assertEquals("FOO", returning.apply("foo"));

            verify(operator).apply("foo");
            verify(operator).onErrorReturn("bar");
            verifyNoMoreInteractions(operator);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedUnaryOperator<String, IOException> operator = Spied.checkedUnaryOperator(s -> {
                throw new IOException(s);
            });

            UnaryOperator<String> returning = operator.onErrorReturn("bar");

            assertEquals("bar", returning.apply("foo"));

            verify(operator).apply("foo");
            verify(operator).onErrorReturn("bar");
            verifyNoMoreInteractions(operator);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            CheckedUnaryOperator<String, IOException> operator = Spied.checkedUnaryOperator(throwable::throwUnchecked);

            UnaryOperator<String> returning = operator.onErrorReturn("bar");

            Throwable thrown = assertThrows(throwable.throwableType(), () -> returning.apply("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(operator).apply("foo");
            verify(operator).onErrorReturn("bar");
            verifyNoMoreInteractions(operator);
        }
    }

    @Nested
    class Unchecked {

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedUnaryOperator<String, IOException> operator = Spied.checkedUnaryOperator(String::toUpperCase);

            UnaryOperator<String> unchecked = operator.unchecked();

            assertEquals("FOO", unchecked.apply("foo"));

            verify(operator).apply("foo");
            verify(operator).unchecked();
            verify(operator).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(operator);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedUnaryOperator<String, IOException> operator = Spied.checkedUnaryOperator(s -> {
                throw new IOException(s);
            });

            UnaryOperator<String> unchecked = operator.unchecked();

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.apply("foo"));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo", cause.getMessage());

            verify(operator).apply("foo");
            verify(operator).unchecked();
            verify(operator).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(operator);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            CheckedUnaryOperator<String, IOException> operator = Spied.checkedUnaryOperator(throwable::throwUnchecked);

            UnaryOperator<String> unchecked = operator.unchecked();

            Throwable thrown = assertThrows(throwable.throwableType(), () -> unchecked.apply("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(operator).apply("foo");
            verify(operator).unchecked();
            verify(operator).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(operator);
        }
    }

    @Nested
    class Of {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> CheckedUnaryOperator.of(null));
        }

        @Test
        void testNonNullArgument() throws IOException {
            CheckedUnaryOperator<String, IOException> operator = CheckedUnaryOperator.of(String::toUpperCase);

            assertEquals("FOO", operator.apply("foo"));
        }
    }

    @Nested
    class Identity {

        @Test
        void testNullArgument() throws IOException {
            CheckedUnaryOperator<String, IOException> operator = CheckedUnaryOperator.identity();

            assertNull(operator.apply(null));
        }

        @Test
        void testNonNullArgument() throws IOException {
            CheckedUnaryOperator<String, IOException> operator = CheckedUnaryOperator.identity();

            assertEquals("foo", operator.apply("foo"));
        }
    }

    @Nested
    class UncheckedFactory {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> CheckedUnaryOperator.unchecked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedUnaryOperator<String, IOException> operator = Spied.checkedUnaryOperator(String::toUpperCase);

            UnaryOperator<String> unchecked = CheckedUnaryOperator.unchecked(operator);

            assertEquals("FOO", unchecked.apply("foo"));

            verify(operator).unchecked();
            verify(operator).onErrorThrowAsUnchecked(any());
            verify(operator).apply("foo");
            verifyNoMoreInteractions(operator);
        }

        @Test
        void testArgumentThrowsChecked() throws IOException {
            CheckedUnaryOperator<String, IOException> operator = Spied.checkedUnaryOperator(s -> {
                throw new IOException(s);
            });

            UnaryOperator<String> unchecked = CheckedUnaryOperator.unchecked(operator);

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.apply("foo"));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo", cause.getMessage());

            verify(operator).unchecked();
            verify(operator).onErrorThrowAsUnchecked(any());
            verify(operator).apply("foo");
            verifyNoMoreInteractions(operator);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testArgumentThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            CheckedUnaryOperator<String, IOException> operator = Spied.checkedUnaryOperator(throwable::throwUnchecked);

            UnaryOperator<String> unchecked = CheckedUnaryOperator.unchecked(operator);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> unchecked.apply("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(operator).unchecked();
            verify(operator).onErrorThrowAsUnchecked(any());
            verify(operator).apply("foo");
            verifyNoMoreInteractions(operator);
        }
    }

    @Nested
    class CheckedWrapper {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> CheckedUnaryOperator.checked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedUnaryOperator<String, IOException> operator = CheckedUnaryOperator.checked(String::toUpperCase);

            assertEquals("FOO", operator.apply("foo"));
        }

        @Test
        void testArgumentThrowsUnchecked() {
            CheckedUnaryOperator<String, IOException> operator = CheckedUnaryOperator.checked(s -> {
                throw new UncheckedException(s, new IOException());
            });

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> operator.apply("foo"));
            assertEquals("foo", thrown.getMessage());
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertNull(cause.getMessage());
        }
    }

    @Nested
    class CheckedUnwrapper {

        @Test
        void testNullArguments() {
            UnaryOperator<String> operator = String::toUpperCase;

            assertThrows(NullPointerException.class, () -> CheckedUnaryOperator.checked(null, IOException.class));
            assertThrows(NullPointerException.class, () -> CheckedUnaryOperator.checked(operator, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedUnaryOperator<String, IOException> operator = CheckedUnaryOperator.checked(String::toUpperCase, IOException.class);

            assertEquals("FOO", operator.apply("foo"));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                CheckedUnaryOperator<String, IOException> operator = CheckedUnaryOperator.checked(s -> {
                    throw new UncheckedException(new IOException(s));
                }, IOException.class);

                IOException thrown = assertThrows(IOException.class, () -> operator.apply("foo"));
                assertEquals("foo", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                CheckedUnaryOperator<String, IOException> operator = CheckedUnaryOperator.checked(s -> {
                    throw new UncheckedException(new FileNotFoundException(s));
                }, IOException.class);

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class, () -> operator.apply("foo"));
                assertEquals("foo", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                CheckedUnaryOperator<String, IOException> operator = CheckedUnaryOperator.checked(s -> {
                    throw new UncheckedException(new ParseException(s, 0));
                }, IOException.class);

                UncheckedException thrown = assertThrows(UncheckedException.class, () -> operator.apply("foo"));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            CheckedUnaryOperator<String, IOException> operator = CheckedUnaryOperator.checked(s -> {
                throw new IllegalStateException(s);
            }, IOException.class);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> operator.apply("foo"));
            assertEquals("foo", thrown.getMessage());
        }
    }

    @Nested
    class InvokeAndUnwrap {

        @Test
        void testNullArguments() throws IOException {
            UnaryOperator<String> operator = s -> String.valueOf(s).toUpperCase();

            assertThrows(NullPointerException.class, () -> CheckedUnaryOperator.invokeAndUnwrap(null, "foo", IOException.class));
            assertThrows(NullPointerException.class, () -> CheckedUnaryOperator.invokeAndUnwrap(operator, "foo", null));

            assertEquals("NULL", CheckedUnaryOperator.invokeAndUnwrap(operator, null, IOException.class));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            UnaryOperator<String> operator = String::toUpperCase;

            assertEquals("FOO", CheckedUnaryOperator.invokeAndUnwrap(operator, "foo", IOException.class));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                UnaryOperator<String> operator = s -> {
                    throw new UncheckedException(new IOException(s));
                };

                IOException thrown = assertThrows(IOException.class, () -> CheckedUnaryOperator.invokeAndUnwrap(operator, "foo", IOException.class));
                assertEquals("foo", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                UnaryOperator<String> operator = s -> {
                    throw new UncheckedException(new FileNotFoundException(s));
                };

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class,
                        () -> CheckedUnaryOperator.invokeAndUnwrap(operator, "foo", IOException.class));
                assertEquals("foo", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                UnaryOperator<String> operator = s -> {
                    throw new UncheckedException(new ParseException(s, 0));
                };

                UncheckedException thrown = assertThrows(UncheckedException.class,
                        () -> CheckedUnaryOperator.invokeAndUnwrap(operator, "foo", IOException.class));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            UnaryOperator<String> operator = s -> {
                throw new IllegalStateException(s);
            };

            IllegalStateException thrown = assertThrows(IllegalStateException.class,
                    () -> CheckedUnaryOperator.invokeAndUnwrap(operator, "foo", IOException.class));
            assertEquals("foo", thrown.getMessage());
        }
    }
}
