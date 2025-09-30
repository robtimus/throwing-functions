/*
 * ThrowingToDoubleBiFunctionTest.java
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
import java.util.function.Function;
import java.util.function.ToDoubleBiFunction;
import java.util.function.ToDoubleFunction;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

@SuppressWarnings("nls")
class ThrowingToDoubleBiFunctionTest {

    @Nested
    class OnErrorThrowAsChecked {

        @Test
        void testNullArgument() {
            ThrowingToDoubleBiFunction<String, String, IOException> function = Spied.throwingToDoubleBiFunction((s1, s2) -> s1.concat(s2).length());

            assertThrows(NullPointerException.class, () -> function.onErrorThrowAsChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingToDoubleBiFunction<String, String, IOException> function = Spied.throwingToDoubleBiFunction((s1, s2) -> s1.concat(s2).length());

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingToDoubleBiFunction<String, String, ExecutionException> throwing = function.onErrorThrowAsChecked(errorMapper);

            assertEquals(6D, throwing.applyAsDouble("foo", "bar"));

            verify(function).applyAsDouble("foo", "bar");
            verify(function).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingToDoubleBiFunction<String, String, IOException> function = Spied.throwingToDoubleBiFunction((s1, s2) -> {
                throw new IOException(s1 + s2);
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingToDoubleBiFunction<String, String, ExecutionException> throwing = function.onErrorThrowAsChecked(errorMapper);

            ExecutionException thrown = assertThrows(ExecutionException.class, () -> throwing.applyAsDouble("foo", "bar"));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foobar", cause.getMessage());

            verify(function).applyAsDouble("foo", "bar");
            verify(function).onErrorThrowAsChecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(function, errorMapper);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingToDoubleBiFunction<String, String, IOException> function = Spied
                    .throwingToDoubleBiFunction((s1, s2) -> throwable.throwUnchecked(s1 + s2));

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingToDoubleBiFunction<String, String, ExecutionException> throwing = function.onErrorThrowAsChecked(errorMapper);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> throwing.applyAsDouble("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());

            verify(function).applyAsDouble("foo", "bar");
            verify(function).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }
    }

    @Nested
    class OnErrorThrowAsUnchecked {

        @Test
        void testNullArgument() {
            ThrowingToDoubleBiFunction<String, String, IOException> function = Spied.throwingToDoubleBiFunction((s1, s2) -> s1.concat(s2).length());

            assertThrows(NullPointerException.class, () -> function.onErrorThrowAsUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingToDoubleBiFunction<String, String, IOException> function = Spied.throwingToDoubleBiFunction((s1, s2) -> s1.concat(s2).length());

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            ToDoubleBiFunction<String, String> throwing = function.onErrorThrowAsUnchecked(errorMapper);

            assertEquals(6D, throwing.applyAsDouble("foo", "bar"));

            verify(function).applyAsDouble("foo", "bar");
            verify(function).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingToDoubleBiFunction<String, String, IOException> function = Spied.throwingToDoubleBiFunction((s1, s2) -> {
                throw new IOException(s1 + s2);
            });

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            ToDoubleBiFunction<String, String> throwing = function.onErrorThrowAsUnchecked(errorMapper);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> throwing.applyAsDouble("foo", "bar"));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foobar", cause.getMessage());

            verify(function).applyAsDouble("foo", "bar");
            verify(function).onErrorThrowAsUnchecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(function, errorMapper);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingToDoubleBiFunction<String, String, IOException> function = Spied
                    .throwingToDoubleBiFunction((s1, s2) -> throwable.throwUnchecked(s1 + s2));

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            ToDoubleBiFunction<String, String> throwing = function.onErrorThrowAsUnchecked(errorMapper);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> throwing.applyAsDouble("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());

            verify(function).applyAsDouble("foo", "bar");
            verify(function).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }
    }

    @Nested
    class OnErrorHandleChecked {

        @Test
        void testNullArgument() {
            ThrowingToDoubleBiFunction<String, String, IOException> function = Spied.throwingToDoubleBiFunction((s1, s2) -> s1.concat(s2).length());

            assertThrows(NullPointerException.class, () -> function.onErrorHandleChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingToDoubleBiFunction<String, String, IOException> function = Spied.throwingToDoubleBiFunction((s1, s2) -> s1.concat(s2).length());

            ThrowingToDoubleFunction<IOException, ExecutionException> errorHandler = Spied.throwingToDoubleFunction(e -> 0D);

            ThrowingToDoubleBiFunction<String, String, ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

            assertEquals(6D, handling.applyAsDouble("foo", "bar"));

            verify(function).applyAsDouble("foo", "bar");
            verify(function).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException, ExecutionException {
                ThrowingToDoubleBiFunction<String, String, IOException> function = Spied.throwingToDoubleBiFunction((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                ThrowingToDoubleFunction<IOException, ExecutionException> errorHandler = Spied.throwingToDoubleFunction(e -> 0D);

                ThrowingToDoubleBiFunction<String, String, ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

                assertEquals(0D, handling.applyAsDouble("foo", "bar"));

                verify(function).applyAsDouble("foo", "bar");
                verify(function).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsDouble(any());
                verifyNoMoreInteractions(function, errorHandler);
            }

            @Test
            void testHandlerThrowsChecked() throws IOException, ExecutionException {
                ThrowingToDoubleBiFunction<String, String, IOException> function = Spied.throwingToDoubleBiFunction((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                ThrowingToDoubleFunction<IOException, ExecutionException> errorHandler = Spied.throwingToDoubleFunction(e -> {
                    throw new ExecutionException(e);
                });

                ThrowingToDoubleBiFunction<String, String, ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

                ExecutionException thrown = assertThrows(ExecutionException.class, () -> handling.applyAsDouble("foo", "bar"));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foobar", cause.getMessage());

                verify(function).applyAsDouble("foo", "bar");
                verify(function).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsDouble(any());
                verifyNoMoreInteractions(function, errorHandler);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testHandlerThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ExecutionException {
                ThrowingToDoubleBiFunction<String, String, IOException> function = Spied.throwingToDoubleBiFunction((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                ThrowingToDoubleFunction<IOException, ExecutionException> errorHandler = Spied.throwingToDoubleFunction(throwable::throwUnchecked);

                ThrowingToDoubleBiFunction<String, String, ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.applyAsDouble("foo", "bar"));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foobar", cause.getMessage());

                verify(function).applyAsDouble("foo", "bar");
                verify(function).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsDouble(any());
                verifyNoMoreInteractions(function, errorHandler);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingToDoubleBiFunction<String, String, IOException> function = Spied
                    .throwingToDoubleBiFunction((s1, s2) -> throwable.throwUnchecked(s1 + s2));

            ThrowingToDoubleFunction<IOException, ExecutionException> errorHandler = Spied.throwingToDoubleFunction(e -> 0D);

            ThrowingToDoubleBiFunction<String, String, ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.applyAsDouble("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());

            verify(function).applyAsDouble("foo", "bar");
            verify(function).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }
    }

    @Nested
    class OnErrorHandleUnchecked {

        @Test
        void testNullArgument() {
            ThrowingToDoubleBiFunction<String, String, IOException> function = Spied.throwingToDoubleBiFunction((s1, s2) -> s1.concat(s2).length());

            assertThrows(NullPointerException.class, () -> function.onErrorHandleUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingToDoubleBiFunction<String, String, IOException> function = Spied.throwingToDoubleBiFunction((s1, s2) -> s1.concat(s2).length());

            ToDoubleFunction<IOException> errorHandler = Spied.toDoubleFunction(e -> 0D);

            ToDoubleBiFunction<String, String> handling = function.onErrorHandleUnchecked(errorHandler);

            assertEquals(6D, handling.applyAsDouble("foo", "bar"));

            verify(function).applyAsDouble("foo", "bar");
            verify(function).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException {
                ThrowingToDoubleBiFunction<String, String, IOException> function = Spied.throwingToDoubleBiFunction((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                ToDoubleFunction<IOException> errorHandler = Spied.toDoubleFunction(e -> 0D);

                ToDoubleBiFunction<String, String> handling = function.onErrorHandleUnchecked(errorHandler);

                assertEquals(0D, handling.applyAsDouble("foo", "bar"));

                verify(function).applyAsDouble("foo", "bar");
                verify(function).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).applyAsDouble(any());
                verifyNoMoreInteractions(function, errorHandler);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testHandlerThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingToDoubleBiFunction<String, String, IOException> function = Spied.throwingToDoubleBiFunction((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                ToDoubleFunction<IOException> errorHandler = Spied.toDoubleFunction(throwable::throwUnchecked);

                ToDoubleBiFunction<String, String> handling = function.onErrorHandleUnchecked(errorHandler);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.applyAsDouble("foo", "bar"));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foobar", cause.getMessage());

                verify(function).applyAsDouble("foo", "bar");
                verify(function).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).applyAsDouble(any());
                verifyNoMoreInteractions(function, errorHandler);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingToDoubleBiFunction<String, String, IOException> function = Spied
                    .throwingToDoubleBiFunction((s1, s2) -> throwable.throwUnchecked(s1 + s2));

            ToDoubleFunction<IOException> errorHandler = Spied.toDoubleFunction(e -> 0D);

            ToDoubleBiFunction<String, String> handling = function.onErrorHandleUnchecked(errorHandler);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.applyAsDouble("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());

            verify(function).applyAsDouble("foo", "bar");
            verify(function).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }
    }

    @Nested
    class OnErrorApplyChecked {

        @Test
        void testNullArgument() {
            ThrowingToDoubleBiFunction<String, String, IOException> function = Spied.throwingToDoubleBiFunction((s1, s2) -> s1.concat(s2).length());

            assertThrows(NullPointerException.class, () -> function.onErrorApplyChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingToDoubleBiFunction<String, String, IOException> function = Spied.throwingToDoubleBiFunction((s1, s2) -> s1.concat(s2).length());

            ThrowingToDoubleBiFunction<String, String, ExecutionException> fallback = Spied
                    .throwingToDoubleBiFunction((s1, s2) -> 2 * s1.concat(s2).length());

            ThrowingToDoubleBiFunction<String, String, ExecutionException> applying = function.onErrorApplyChecked(fallback);

            assertEquals(6D, applying.applyAsDouble("foo", "bar"));

            verify(function).applyAsDouble("foo", "bar");
            verify(function).onErrorApplyChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                ThrowingToDoubleBiFunction<String, String, IOException> function = Spied.throwingToDoubleBiFunction((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                ThrowingToDoubleBiFunction<String, String, ParseException> fallback = Spied
                        .throwingToDoubleBiFunction((s1, s2) -> 2 * s1.concat(s2).length());

                ThrowingToDoubleBiFunction<String, String, ParseException> applying = function.onErrorApplyChecked(fallback);

                assertEquals(12D, applying.applyAsDouble("foo", "bar"));

                verify(function).applyAsDouble("foo", "bar");
                verify(function).onErrorApplyChecked(fallback);
                verify(fallback).applyAsDouble("foo", "bar");
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                ThrowingToDoubleBiFunction<String, String, IOException> function = Spied.throwingToDoubleBiFunction((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                ThrowingToDoubleBiFunction<String, String, ParseException> fallback = Spied.throwingToDoubleBiFunction((s1, s2) -> {
                    throw new ParseException(s1 + s2, 0);
                });

                ThrowingToDoubleBiFunction<String, String, ParseException> applying = function.onErrorApplyChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> applying.applyAsDouble("foo", "bar"));
                assertEquals("foobar", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(function).applyAsDouble("foo", "bar");
                verify(function).onErrorApplyChecked(fallback);
                verify(fallback).applyAsDouble("foo", "bar");
                verifyNoMoreInteractions(function, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ParseException {
                ThrowingToDoubleBiFunction<String, String, IOException> function = Spied.throwingToDoubleBiFunction((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                ThrowingToDoubleBiFunction<String, String, ParseException> fallback = Spied
                        .throwingToDoubleBiFunction((s1, s2) -> throwable.throwUnchecked(s1 + s2));

                ThrowingToDoubleBiFunction<String, String, ParseException> applying = function.onErrorApplyChecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.applyAsDouble("foo", "bar"));
                assertEquals("foobar", thrown.getMessage());

                verify(function).applyAsDouble("foo", "bar");
                verify(function).onErrorApplyChecked(fallback);
                verify(fallback).applyAsDouble("foo", "bar");
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingToDoubleBiFunction<String, String, IOException> function = Spied
                    .throwingToDoubleBiFunction((s1, s2) -> throwable.throwUnchecked(s1 + s2));

            ThrowingToDoubleBiFunction<String, String, ParseException> fallback = Spied
                    .throwingToDoubleBiFunction((s1, s2) -> 2 * s1.concat(s2).length());

            ThrowingToDoubleBiFunction<String, String, ParseException> applying = function.onErrorApplyChecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.applyAsDouble("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());

            verify(function).applyAsDouble("foo", "bar");
            verify(function).onErrorApplyChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorApplyUnchecked {

        @Test
        void testNullArgument() {
            ThrowingToDoubleBiFunction<String, String, IOException> function = Spied.throwingToDoubleBiFunction((s1, s2) -> s1.concat(s2).length());

            assertThrows(NullPointerException.class, () -> function.onErrorApplyUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingToDoubleBiFunction<String, String, IOException> function = Spied.throwingToDoubleBiFunction((s1, s2) -> s1.concat(s2).length());

            ToDoubleBiFunction<String, String> fallback = Spied.toDoubleBiFunction((s1, s2) -> 2 * s1.concat(s2).length());

            ToDoubleBiFunction<String, String> applying = function.onErrorApplyUnchecked(fallback);

            assertEquals(6D, applying.applyAsDouble("foo", "bar"));

            verify(function).applyAsDouble("foo", "bar");
            verify(function).onErrorApplyUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                ThrowingToDoubleBiFunction<String, String, IOException> function = Spied.throwingToDoubleBiFunction((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                ToDoubleBiFunction<String, String> fallback = Spied.toDoubleBiFunction((s1, s2) -> 2 * s1.concat(s2).length());

                ToDoubleBiFunction<String, String> applying = function.onErrorApplyUnchecked(fallback);

                assertEquals(12D, applying.applyAsDouble("foo", "bar"));

                verify(function).applyAsDouble("foo", "bar");
                verify(function).onErrorApplyUnchecked(fallback);
                verify(fallback).applyAsDouble("foo", "bar");
                verifyNoMoreInteractions(function, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingToDoubleBiFunction<String, String, IOException> function = Spied.throwingToDoubleBiFunction((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                ToDoubleBiFunction<String, String> fallback = Spied.toDoubleBiFunction((s1, s2) -> throwable.throwUnchecked(s1 + s2));

                ToDoubleBiFunction<String, String> applying = function.onErrorApplyUnchecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.applyAsDouble("foo", "bar"));
                assertEquals("foobar", thrown.getMessage());

                verify(function).applyAsDouble("foo", "bar");
                verify(function).onErrorApplyUnchecked(fallback);
                verify(fallback).applyAsDouble("foo", "bar");
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingToDoubleBiFunction<String, String, IOException> function = Spied
                    .throwingToDoubleBiFunction((s1, s2) -> throwable.throwUnchecked(s1 + s2));

            ToDoubleBiFunction<String, String> fallback = Spied.toDoubleBiFunction((s1, s2) -> 2 * s1.concat(s2).length());

            ToDoubleBiFunction<String, String> applying = function.onErrorApplyUnchecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.applyAsDouble("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());

            verify(function).applyAsDouble("foo", "bar");
            verify(function).onErrorApplyUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorGetChecked {

        @Test
        void testNullArgument() {
            ThrowingToDoubleBiFunction<String, String, IOException> function = Spied.throwingToDoubleBiFunction((s1, s2) -> s1.concat(s2).length());

            assertThrows(NullPointerException.class, () -> function.onErrorGetChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingToDoubleBiFunction<String, String, IOException> function = Spied.throwingToDoubleBiFunction((s1, s2) -> s1.concat(s2).length());

            ThrowingDoubleSupplier<ExecutionException> fallback = Spied.throwingDoubleSupplier(() -> 0D);

            ThrowingToDoubleBiFunction<String, String, ExecutionException> getting = function.onErrorGetChecked(fallback);

            assertEquals(6D, getting.applyAsDouble("foo", "bar"));

            verify(function).applyAsDouble("foo", "bar");
            verify(function).onErrorGetChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                ThrowingToDoubleBiFunction<String, String, IOException> function = Spied.throwingToDoubleBiFunction((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                ThrowingDoubleSupplier<ParseException> fallback = Spied.throwingDoubleSupplier(() -> 0D);

                ThrowingToDoubleBiFunction<String, String, ParseException> getting = function.onErrorGetChecked(fallback);

                assertEquals(0D, getting.applyAsDouble("foo", "bar"));

                verify(function).applyAsDouble("foo", "bar");
                verify(function).onErrorGetChecked(fallback);
                verify(fallback).getAsDouble();
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                ThrowingToDoubleBiFunction<String, String, IOException> function = Spied.throwingToDoubleBiFunction((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                ThrowingDoubleSupplier<ParseException> fallback = Spied.throwingDoubleSupplier(() -> {
                    throw new ParseException("bar", 0);
                });

                ThrowingToDoubleBiFunction<String, String, ParseException> getting = function.onErrorGetChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> getting.applyAsDouble("foo", "bar"));
                assertEquals("bar", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(function).applyAsDouble("foo", "bar");
                verify(function).onErrorGetChecked(fallback);
                verify(fallback).getAsDouble();
                verifyNoMoreInteractions(function, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ParseException {
                ThrowingToDoubleBiFunction<String, String, IOException> function = Spied.throwingToDoubleBiFunction((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                ThrowingDoubleSupplier<ParseException> fallback = Spied.throwingDoubleSupplier(() -> throwable.throwUnchecked("bar"));

                ThrowingToDoubleBiFunction<String, String, ParseException> getting = function.onErrorGetChecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.applyAsDouble("foo", "bar"));
                assertEquals("bar", thrown.getMessage());

                verify(function).applyAsDouble("foo", "bar");
                verify(function).onErrorGetChecked(fallback);
                verify(fallback).getAsDouble();
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingToDoubleBiFunction<String, String, IOException> function = Spied
                    .throwingToDoubleBiFunction((s1, s2) -> throwable.throwUnchecked(s1 + s2));

            ThrowingDoubleSupplier<ParseException> fallback = Spied.throwingDoubleSupplier(() -> 0D);

            ThrowingToDoubleBiFunction<String, String, ParseException> getting = function.onErrorGetChecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.applyAsDouble("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());

            verify(function).applyAsDouble("foo", "bar");
            verify(function).onErrorGetChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorGetUnchecked {

        @Test
        void testNullArgument() {
            ThrowingToDoubleBiFunction<String, String, IOException> function = Spied.throwingToDoubleBiFunction((s1, s2) -> s1.concat(s2).length());

            assertThrows(NullPointerException.class, () -> function.onErrorGetUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingToDoubleBiFunction<String, String, IOException> function = Spied.throwingToDoubleBiFunction((s1, s2) -> s1.concat(s2).length());

            DoubleSupplier fallback = Spied.doubleSupplier(() -> 0D);

            ToDoubleBiFunction<String, String> getting = function.onErrorGetUnchecked(fallback);

            assertEquals(6D, getting.applyAsDouble("foo", "bar"));

            verify(function).applyAsDouble("foo", "bar");
            verify(function).onErrorGetUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                ThrowingToDoubleBiFunction<String, String, IOException> function = Spied.throwingToDoubleBiFunction((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                DoubleSupplier fallback = Spied.doubleSupplier(() -> 0D);

                ToDoubleBiFunction<String, String> getting = function.onErrorGetUnchecked(fallback);

                assertEquals(0D, getting.applyAsDouble("foo", "bar"));

                verify(function).applyAsDouble("foo", "bar");
                verify(function).onErrorGetUnchecked(fallback);
                verify(fallback).getAsDouble();
                verifyNoMoreInteractions(function, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingToDoubleBiFunction<String, String, IOException> function = Spied.throwingToDoubleBiFunction((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                DoubleSupplier fallback = Spied.doubleSupplier(() -> throwable.throwUnchecked("bar"));

                ToDoubleBiFunction<String, String> getting = function.onErrorGetUnchecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.applyAsDouble("foo", "bar"));
                assertEquals("bar", thrown.getMessage());

                verify(function).applyAsDouble("foo", "bar");
                verify(function).onErrorGetUnchecked(fallback);
                verify(fallback).getAsDouble();
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingToDoubleBiFunction<String, String, IOException> function = Spied
                    .throwingToDoubleBiFunction((s1, s2) -> throwable.throwUnchecked(s1 + s2));

            DoubleSupplier fallback = Spied.doubleSupplier(() -> 0D);

            ToDoubleBiFunction<String, String> getting = function.onErrorGetUnchecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.applyAsDouble("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());

            verify(function).applyAsDouble("foo", "bar");
            verify(function).onErrorGetUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorReturn {

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingToDoubleBiFunction<String, String, IOException> function = Spied.throwingToDoubleBiFunction((s1, s2) -> s1.concat(s2).length());

            ToDoubleBiFunction<String, String> returning = function.onErrorReturn(0D);

            assertEquals(6D, returning.applyAsDouble("foo", "bar"));

            verify(function).applyAsDouble("foo", "bar");
            verify(function).onErrorReturn(0D);
            verifyNoMoreInteractions(function);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingToDoubleBiFunction<String, String, IOException> function = Spied.throwingToDoubleBiFunction((s1, s2) -> {
                throw new IOException(s1 + s2);
            });

            ToDoubleBiFunction<String, String> returning = function.onErrorReturn(0D);

            assertEquals(0D, returning.applyAsDouble("foo", "bar"));

            verify(function).applyAsDouble("foo", "bar");
            verify(function).onErrorReturn(0D);
            verifyNoMoreInteractions(function);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingToDoubleBiFunction<String, String, IOException> function = Spied
                    .throwingToDoubleBiFunction((s1, s2) -> throwable.throwUnchecked(s1 + s2));

            ToDoubleBiFunction<String, String> returning = function.onErrorReturn(0D);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> returning.applyAsDouble("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());

            verify(function).applyAsDouble("foo", "bar");
            verify(function).onErrorReturn(0D);
            verifyNoMoreInteractions(function);
        }
    }

    @Nested
    class Unchecked {

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingToDoubleBiFunction<String, String, IOException> function = Spied.throwingToDoubleBiFunction((s1, s2) -> s1.concat(s2).length());

            ToDoubleBiFunction<String, String> unchecked = function.unchecked();

            assertEquals(6D, unchecked.applyAsDouble("foo", "bar"));

            verify(function).applyAsDouble("foo", "bar");
            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(function);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingToDoubleBiFunction<String, String, IOException> function = Spied.throwingToDoubleBiFunction((s1, s2) -> {
                throw new IOException(s1 + s2);
            });

            ToDoubleBiFunction<String, String> unchecked = function.unchecked();

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.applyAsDouble("foo", "bar"));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foobar", cause.getMessage());

            verify(function).applyAsDouble("foo", "bar");
            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(function);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingToDoubleBiFunction<String, String, IOException> function = Spied
                    .throwingToDoubleBiFunction((s1, s2) -> throwable.throwUnchecked(s1 + s2));

            ToDoubleBiFunction<String, String> unchecked = function.unchecked();

            Throwable thrown = assertThrows(throwable.throwableType(), () -> unchecked.applyAsDouble("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());

            verify(function).applyAsDouble("foo", "bar");
            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(function);
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
            ThrowingToDoubleBiFunction<String, String, IOException> function = ThrowingToDoubleBiFunction.of((s1, s2) -> s1.concat(s2).length());

            assertEquals(6D, function.applyAsDouble("foo", "bar"));
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
            ThrowingToDoubleBiFunction<String, String, IOException> function = Spied.throwingToDoubleBiFunction((s1, s2) -> s1.concat(s2).length());

            ToDoubleBiFunction<String, String> unchecked = ThrowingToDoubleBiFunction.unchecked(function);

            assertEquals(6D, unchecked.applyAsDouble("foo", "bar"));

            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verify(function).applyAsDouble("foo", "bar");
            verifyNoMoreInteractions(function);
        }

        @Test
        void testArgumentThrowsChecked() throws IOException {
            ThrowingToDoubleBiFunction<String, String, IOException> function = Spied.throwingToDoubleBiFunction((s1, s2) -> {
                throw new IOException(s1 + s2);
            });

            ToDoubleBiFunction<String, String> unchecked = ThrowingToDoubleBiFunction.unchecked(function);

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.applyAsDouble("foo", "bar"));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foobar", cause.getMessage());

            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verify(function).applyAsDouble("foo", "bar");
            verifyNoMoreInteractions(function);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testArgumentThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingToDoubleBiFunction<String, String, IOException> function = Spied
                    .throwingToDoubleBiFunction((s1, s2) -> throwable.throwUnchecked(s1 + s2));

            ToDoubleBiFunction<String, String> unchecked = ThrowingToDoubleBiFunction.unchecked(function);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> unchecked.applyAsDouble("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());

            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verify(function).applyAsDouble("foo", "bar");
            verifyNoMoreInteractions(function);
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
            ThrowingToDoubleBiFunction<String, String, IOException> function = ThrowingToDoubleBiFunction.checked((s1, s2) -> s1.concat(s2).length());

            assertEquals(6D, function.applyAsDouble("foo", "bar"));
        }

        @Test
        void testArgumentThrowsUnchecked() {
            ThrowingToDoubleBiFunction<String, String, IOException> function = ThrowingToDoubleBiFunction.checked((s1, s2) -> {
                throw new UncheckedException(s1 + s2, new IOException());
            });

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> function.applyAsDouble("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertNull(cause.getMessage());
        }
    }

    @Nested
    class CheckedUnwrapper {

        @Test
        void testNullArguments() {
            ToDoubleBiFunction<String, String> function = (s1, s2) -> s1.concat(s2).length();

            assertThrows(NullPointerException.class, () -> ThrowingToDoubleBiFunction.checked(null, IOException.class));
            assertThrows(NullPointerException.class, () -> ThrowingToDoubleBiFunction.checked(function, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ThrowingToDoubleBiFunction<String, String, IOException> function = ThrowingToDoubleBiFunction
                    .checked((s1, s2) -> s1.concat(s2).length(), IOException.class);

            assertEquals(6D, function.applyAsDouble("foo", "bar"));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                ThrowingToDoubleBiFunction<String, String, IOException> function = ThrowingToDoubleBiFunction.checked((s1, s2) -> {
                    throw new UncheckedException(new IOException(s1 + s2));
                }, IOException.class);

                IOException thrown = assertThrows(IOException.class, () -> function.applyAsDouble("foo", "bar"));
                assertEquals("foobar", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                ThrowingToDoubleBiFunction<String, String, IOException> function = ThrowingToDoubleBiFunction.checked((s1, s2) -> {
                    throw new UncheckedException(new FileNotFoundException(s1 + s2));
                }, IOException.class);

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class, () -> function.applyAsDouble("foo", "bar"));
                assertEquals("foobar", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                ThrowingToDoubleBiFunction<String, String, IOException> function = ThrowingToDoubleBiFunction.checked((s1, s2) -> {
                    throw new UncheckedException(new ParseException(s1 + s2, 0));
                }, IOException.class);

                UncheckedException thrown = assertThrows(UncheckedException.class, () -> function.applyAsDouble("foo", "bar"));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("foobar", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            ThrowingToDoubleBiFunction<String, String, IOException> function = ThrowingToDoubleBiFunction.checked((s1, s2) -> {
                throw new IllegalStateException(s1 + s2);
            }, IOException.class);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> function.applyAsDouble("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());
        }
    }

    @Nested
    class InvokeAndUnwrap {

        @Test
        void testNullArguments() throws IOException {
            ToDoubleBiFunction<String, String> function = (s1, s2) -> (s1 + s2).length();

            assertThrows(NullPointerException.class, () -> ThrowingToDoubleBiFunction.invokeAndUnwrap(null, "foo", "bar", IOException.class));
            assertThrows(NullPointerException.class, () -> ThrowingToDoubleBiFunction.invokeAndUnwrap(function, "foo", "bar", null));

            assertEquals(7D, ThrowingToDoubleBiFunction.invokeAndUnwrap(function, null, "bar", IOException.class));
            assertEquals(7D, ThrowingToDoubleBiFunction.invokeAndUnwrap(function, "foo", null, IOException.class));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ToDoubleBiFunction<String, String> function = (s1, s2) -> s1.concat(s2).length();

            assertEquals(6D, ThrowingToDoubleBiFunction.invokeAndUnwrap(function, "foo", "bar", IOException.class));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                ToDoubleBiFunction<String, String> function = (s1, s2) -> {
                    throw new UncheckedException(new IOException(s1 + s2));
                };

                IOException thrown = assertThrows(IOException.class,
                        () -> ThrowingToDoubleBiFunction.invokeAndUnwrap(function, "foo", "bar", IOException.class));
                assertEquals("foobar", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                ToDoubleBiFunction<String, String> function = (s1, s2) -> {
                    throw new UncheckedException(new FileNotFoundException(s1 + s2));
                };

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class,
                        () -> ThrowingToDoubleBiFunction.invokeAndUnwrap(function, "foo", "bar", IOException.class));
                assertEquals("foobar", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                ToDoubleBiFunction<String, String> function = (s1, s2) -> {
                    throw new UncheckedException(new ParseException(s1 + s2, 0));
                };

                UncheckedException thrown = assertThrows(UncheckedException.class,
                        () -> ThrowingToDoubleBiFunction.invokeAndUnwrap(function, "foo", "bar", IOException.class));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("foobar", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            ToDoubleBiFunction<String, String> function = (s1, s2) -> {
                throw new IllegalStateException(s1 + s2);
            };

            IllegalStateException thrown = assertThrows(IllegalStateException.class,
                    () -> ThrowingToDoubleBiFunction.invokeAndUnwrap(function, "foo", "bar", IOException.class));
            assertEquals("foobar", thrown.getMessage());
        }
    }
}
