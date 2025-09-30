/*
 * ThrowingBiFunctionTest.java
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
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

@SuppressWarnings("nls")
class ThrowingBiFunctionTest {

    @Nested
    class AndThen {

        @Test
        void testNullArgument() {
            ThrowingBiFunction<String, String, String, IOException> function = Spied.throwingBiFunction(String::concat);

            assertThrows(NullPointerException.class, () -> function.andThen(null));
        }

        @Test
        void testNeitherThrows() throws IOException {
            ThrowingBiFunction<Integer, String, String, IOException> function = Spied.throwingBiFunction((i, s) -> i + s);
            ThrowingFunction<String, String, IOException> after = Spied.throwingFunction(s -> s + s);

            ThrowingBiFunction<Integer, String, String, IOException> composed = function.andThen(after);

            assertEquals("1s1s", composed.apply(1, "s"));

            verify(function).apply(1, "s");
            verify(function).andThen(after);
            verify(after).apply("1s");
            verifyNoMoreInteractions(function, after);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingBiFunction<Integer, String, String, IOException> function = Spied.throwingBiFunction((i, s) -> {
                throw new IOException(i + s);
            });
            ThrowingFunction<String, String, IOException> after = Spied.throwingFunction(Object::toString);

            ThrowingBiFunction<Integer, String, String, IOException> composed = function.andThen(after);

            IOException thrown = assertThrows(IOException.class, () -> composed.apply(1, "s"));
            assertEquals("1s", thrown.getMessage());

            verify(function).apply(1, "s");
            verify(function).andThen(after);
            verifyNoMoreInteractions(function, after);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingBiFunction<Integer, String, String, IOException> function = Spied.throwingBiFunction((i, s) -> throwable.throwUnchecked(i + s));
            ThrowingFunction<String, String, IOException> after = Spied.throwingFunction(Object::toString);

            ThrowingBiFunction<Integer, String, String, IOException> composed = function.andThen(after);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> composed.apply(1, "s"));
            assertEquals("1s", thrown.getMessage());

            verify(function).apply(1, "s");
            verify(function).andThen(after);
            verifyNoMoreInteractions(function, after);
        }

        @Test
        void testAfterThrowsChecked() throws IOException {
            ThrowingBiFunction<Integer, String, String, IOException> function = Spied.throwingBiFunction((i, s) -> i + s);
            ThrowingFunction<String, String, IOException> after = Spied.throwingFunction(s -> {
                throw new IOException(s);
            });

            ThrowingBiFunction<Integer, String, String, IOException> composed = function.andThen(after);

            IOException thrown = assertThrows(IOException.class, () -> composed.apply(1, "s"));
            assertEquals("1s", thrown.getMessage());

            verify(function).apply(1, "s");
            verify(function).andThen(after);
            verify(after).apply("1s");
            verifyNoMoreInteractions(function, after);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testAfterThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingBiFunction<Integer, String, String, IOException> function = Spied.throwingBiFunction((i, s) -> i + s);
            ThrowingFunction<String, String, IOException> after = Spied.throwingFunction(throwable::throwUnchecked);

            ThrowingBiFunction<Integer, String, String, IOException> composed = function.andThen(after);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> composed.apply(1, "s"));
            assertEquals("1s", thrown.getMessage());

            verify(function).apply(1, "s");
            verify(function).andThen(after);
            verify(after).apply("1s");
            verifyNoMoreInteractions(function, after);
        }
    }

    @Nested
    class OnErrorThrowAsChecked {

        @Test
        void testNullArgument() {
            ThrowingBiFunction<String, String, String, IOException> function = Spied.throwingBiFunction(String::concat);

            assertThrows(NullPointerException.class, () -> function.onErrorThrowAsChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingBiFunction<String, String, String, IOException> function = Spied.throwingBiFunction(String::concat);

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingBiFunction<String, String, String, ExecutionException> throwing = function.onErrorThrowAsChecked(errorMapper);

            assertEquals("foobar", throwing.apply("foo", "bar"));

            verify(function).apply("foo", "bar");
            verify(function).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingBiFunction<String, String, String, IOException> function = Spied.throwingBiFunction((s1, s2) -> {
                throw new IOException(s1 + s2);
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingBiFunction<String, String, String, ExecutionException> throwing = function.onErrorThrowAsChecked(errorMapper);

            ExecutionException thrown = assertThrows(ExecutionException.class, () -> throwing.apply("foo", "bar"));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foobar", cause.getMessage());

            verify(function).apply("foo", "bar");
            verify(function).onErrorThrowAsChecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(function, errorMapper);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingBiFunction<String, String, String, IOException> function = Spied
                    .throwingBiFunction((s1, s2) -> throwable.throwUnchecked(s1 + s2));

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingBiFunction<String, String, String, ExecutionException> throwing = function.onErrorThrowAsChecked(errorMapper);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> throwing.apply("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());

            verify(function).apply("foo", "bar");
            verify(function).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }
    }

    @Nested
    class OnErrorThrowAsUnchecked {

        @Test
        void testNullArgument() {
            ThrowingBiFunction<String, String, String, IOException> function = Spied.throwingBiFunction(String::concat);

            assertThrows(NullPointerException.class, () -> function.onErrorThrowAsUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingBiFunction<String, String, String, IOException> function = Spied.throwingBiFunction(String::concat);

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            BiFunction<String, String, String> throwing = function.onErrorThrowAsUnchecked(errorMapper);

            assertEquals("foobar", throwing.apply("foo", "bar"));

            verify(function).apply("foo", "bar");
            verify(function).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingBiFunction<String, String, String, IOException> function = Spied.throwingBiFunction((s1, s2) -> {
                throw new IOException(s1 + s2);
            });

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            BiFunction<String, String, String> throwing = function.onErrorThrowAsUnchecked(errorMapper);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> throwing.apply("foo", "bar"));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foobar", cause.getMessage());

            verify(function).apply("foo", "bar");
            verify(function).onErrorThrowAsUnchecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(function, errorMapper);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingBiFunction<String, String, String, IOException> function = Spied
                    .throwingBiFunction((s1, s2) -> throwable.throwUnchecked(s1 + s2));

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            BiFunction<String, String, String> throwing = function.onErrorThrowAsUnchecked(errorMapper);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> throwing.apply("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());

            verify(function).apply("foo", "bar");
            verify(function).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }
    }

    @Nested
    class OnErrorHandleChecked {

        @Test
        void testNullArgument() {
            ThrowingBiFunction<String, String, String, IOException> function = Spied.throwingBiFunction(String::concat);

            assertThrows(NullPointerException.class, () -> function.onErrorHandleChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingBiFunction<String, String, String, IOException> function = Spied.throwingBiFunction(String::concat);

            ThrowingFunction<IOException, String, ExecutionException> errorHandler = Spied.throwingFunction(Exception::getMessage);

            ThrowingBiFunction<String, String, String, ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

            assertEquals("foobar", handling.apply("foo", "bar"));

            verify(function).apply("foo", "bar");
            verify(function).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException, ExecutionException {
                ThrowingBiFunction<String, String, String, IOException> function = Spied.throwingBiFunction((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                ThrowingFunction<IOException, String, ExecutionException> errorHandler = Spied.throwingFunction(Exception::getMessage);

                ThrowingBiFunction<String, String, String, ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

                assertEquals("foobar", handling.apply("foo", "bar"));

                verify(function).apply("foo", "bar");
                verify(function).onErrorHandleChecked(errorHandler);
                verify(errorHandler).apply(any());
                verifyNoMoreInteractions(function, errorHandler);
            }

            @Test
            void testHandlerThrowsChecked() throws IOException, ExecutionException {
                ThrowingBiFunction<String, String, String, IOException> function = Spied.throwingBiFunction((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                ThrowingFunction<IOException, String, ExecutionException> errorHandler = Spied.throwingFunction(e -> {
                    throw new ExecutionException(e);
                });

                ThrowingBiFunction<String, String, String, ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

                ExecutionException thrown = assertThrows(ExecutionException.class, () -> handling.apply("foo", "bar"));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foobar", cause.getMessage());

                verify(function).apply("foo", "bar");
                verify(function).onErrorHandleChecked(errorHandler);
                verify(errorHandler).apply(any());
                verifyNoMoreInteractions(function, errorHandler);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testHandlerThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ExecutionException {
                ThrowingBiFunction<String, String, String, IOException> function = Spied.throwingBiFunction((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                ThrowingFunction<IOException, String, ExecutionException> errorHandler = Spied.throwingFunction(throwable::throwUnchecked);

                ThrowingBiFunction<String, String, String, ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.apply("foo", "bar"));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foobar", cause.getMessage());

                verify(function).apply("foo", "bar");
                verify(function).onErrorHandleChecked(errorHandler);
                verify(errorHandler).apply(any());
                verifyNoMoreInteractions(function, errorHandler);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingBiFunction<String, String, String, IOException> function = Spied
                    .throwingBiFunction((s1, s2) -> throwable.throwUnchecked(s1 + s2));

            ThrowingFunction<IOException, String, ExecutionException> errorHandler = Spied.throwingFunction(Exception::getMessage);

            ThrowingBiFunction<String, String, String, ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.apply("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());

            verify(function).apply("foo", "bar");
            verify(function).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }
    }

    @Nested
    class OnErrorHandleUnchecked {

        @Test
        void testNullArgument() {
            ThrowingBiFunction<String, String, String, IOException> function = Spied.throwingBiFunction(String::concat);

            assertThrows(NullPointerException.class, () -> function.onErrorHandleUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingBiFunction<String, String, String, IOException> function = Spied.throwingBiFunction(String::concat);

            Function<IOException, String> errorHandler = Spied.function(Exception::getMessage);

            BiFunction<String, String, String> handling = function.onErrorHandleUnchecked(errorHandler);

            assertEquals("foobar", handling.apply("foo", "bar"));

            verify(function).apply("foo", "bar");
            verify(function).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException {
                ThrowingBiFunction<String, String, String, IOException> function = Spied.throwingBiFunction((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                Function<IOException, String> errorHandler = Spied.function(Exception::getMessage);

                BiFunction<String, String, String> handling = function.onErrorHandleUnchecked(errorHandler);

                assertEquals("foobar", handling.apply("foo", "bar"));

                verify(function).apply("foo", "bar");
                verify(function).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).apply(any());
                verifyNoMoreInteractions(function, errorHandler);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testHandlerThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingBiFunction<String, String, String, IOException> function = Spied.throwingBiFunction((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                Function<IOException, String> errorHandler = Spied.function(throwable::throwUnchecked);

                BiFunction<String, String, String> handling = function.onErrorHandleUnchecked(errorHandler);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.apply("foo", "bar"));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foobar", cause.getMessage());

                verify(function).apply("foo", "bar");
                verify(function).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).apply(any());
                verifyNoMoreInteractions(function, errorHandler);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingBiFunction<String, String, String, IOException> function = Spied
                    .throwingBiFunction((s1, s2) -> throwable.throwUnchecked(s1 + s2));

            Function<IOException, String> errorHandler = Spied.function(Exception::getMessage);

            BiFunction<String, String, String> handling = function.onErrorHandleUnchecked(errorHandler);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.apply("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());

            verify(function).apply("foo", "bar");
            verify(function).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }
    }

    @Nested
    class OnErrorApplyChecked {

        @Test
        void testNullArgument() {
            ThrowingBiFunction<String, String, String, IOException> function = Spied.throwingBiFunction(String::concat);

            assertThrows(NullPointerException.class, () -> function.onErrorApplyChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingBiFunction<String, String, String, IOException> function = Spied.throwingBiFunction(String::concat);

            ThrowingBiFunction<String, String, String, ExecutionException> fallback = Spied.throwingBiFunction((s1, s2) -> s1 + s2 + s1 + s2);

            ThrowingBiFunction<String, String, String, ExecutionException> applying = function.onErrorApplyChecked(fallback);

            assertEquals("foobar", applying.apply("foo", "bar"));

            verify(function).apply("foo", "bar");
            verify(function).onErrorApplyChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                ThrowingBiFunction<String, String, String, IOException> function = Spied.throwingBiFunction((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                ThrowingBiFunction<String, String, String, ParseException> fallback = Spied.throwingBiFunction((s1, s2) -> s1 + s2 + s1 + s2);

                ThrowingBiFunction<String, String, String, ParseException> applying = function.onErrorApplyChecked(fallback);

                assertEquals("foobarfoobar", applying.apply("foo", "bar"));

                verify(function).apply("foo", "bar");
                verify(function).onErrorApplyChecked(fallback);
                verify(fallback).apply("foo", "bar");
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                ThrowingBiFunction<String, String, String, IOException> function = Spied.throwingBiFunction((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                ThrowingBiFunction<String, String, String, ParseException> fallback = Spied.throwingBiFunction((s1, s2) -> {
                    throw new ParseException(s1 + s2, 0);
                });

                ThrowingBiFunction<String, String, String, ParseException> applying = function.onErrorApplyChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> applying.apply("foo", "bar"));
                assertEquals("foobar", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(function).apply("foo", "bar");
                verify(function).onErrorApplyChecked(fallback);
                verify(fallback).apply("foo", "bar");
                verifyNoMoreInteractions(function, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ParseException {
                ThrowingBiFunction<String, String, String, IOException> function = Spied.throwingBiFunction((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                ThrowingBiFunction<String, String, String, ParseException> fallback = Spied
                        .throwingBiFunction((s1, s2) -> throwable.throwUnchecked(s1 + s2));

                ThrowingBiFunction<String, String, String, ParseException> applying = function.onErrorApplyChecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.apply("foo", "bar"));
                assertEquals("foobar", thrown.getMessage());

                verify(function).apply("foo", "bar");
                verify(function).onErrorApplyChecked(fallback);
                verify(fallback).apply("foo", "bar");
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingBiFunction<String, String, String, IOException> function = Spied
                    .throwingBiFunction((s1, s2) -> throwable.throwUnchecked(s1 + s2));

            ThrowingBiFunction<String, String, String, ParseException> fallback = Spied.throwingBiFunction((s1, s2) -> s1 + s2 + s1 + s2);

            ThrowingBiFunction<String, String, String, ParseException> applying = function.onErrorApplyChecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.apply("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());

            verify(function).apply("foo", "bar");
            verify(function).onErrorApplyChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorApplyUnchecked {

        @Test
        void testNullArgument() {
            ThrowingBiFunction<String, String, String, IOException> function = Spied.throwingBiFunction(String::concat);

            assertThrows(NullPointerException.class, () -> function.onErrorApplyUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingBiFunction<String, String, String, IOException> function = Spied.throwingBiFunction(String::concat);

            BiFunction<String, String, String> fallback = Spied.biFunction((s1, s2) -> s1 + s2 + s1 + s2);

            BiFunction<String, String, String> applying = function.onErrorApplyUnchecked(fallback);

            assertEquals("foobar", applying.apply("foo", "bar"));

            verify(function).apply("foo", "bar");
            verify(function).onErrorApplyUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                ThrowingBiFunction<String, String, String, IOException> function = Spied.throwingBiFunction((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                BiFunction<String, String, String> fallback = Spied.biFunction((s1, s2) -> s1 + s2 + s1 + s2);

                BiFunction<String, String, String> applying = function.onErrorApplyUnchecked(fallback);

                assertEquals("foobarfoobar", applying.apply("foo", "bar"));

                verify(function).apply("foo", "bar");
                verify(function).onErrorApplyUnchecked(fallback);
                verify(fallback).apply("foo", "bar");
                verifyNoMoreInteractions(function, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingBiFunction<String, String, String, IOException> function = Spied.throwingBiFunction((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                BiFunction<String, String, String> fallback = Spied.biFunction((s1, s2) -> throwable.throwUnchecked(s1 + s2));

                BiFunction<String, String, String> applying = function.onErrorApplyUnchecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.apply("foo", "bar"));
                assertEquals("foobar", thrown.getMessage());

                verify(function).apply("foo", "bar");
                verify(function).onErrorApplyUnchecked(fallback);
                verify(fallback).apply("foo", "bar");
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingBiFunction<String, String, String, IOException> function = Spied
                    .throwingBiFunction((s1, s2) -> throwable.throwUnchecked(s1 + s2));

            BiFunction<String, String, String> fallback = Spied.biFunction((s1, s2) -> s1 + s2 + s1 + s2);

            BiFunction<String, String, String> applying = function.onErrorApplyUnchecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.apply("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());

            verify(function).apply("foo", "bar");
            verify(function).onErrorApplyUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorGetChecked {

        @Test
        void testNullArgument() {
            ThrowingBiFunction<String, String, String, IOException> function = Spied.throwingBiFunction(String::concat);

            assertThrows(NullPointerException.class, () -> function.onErrorGetChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingBiFunction<String, String, String, IOException> function = Spied.throwingBiFunction(String::concat);

            ThrowingSupplier<String, ExecutionException> fallback = Spied.throwingSupplier(() -> "bar");

            ThrowingBiFunction<String, String, String, ExecutionException> getting = function.onErrorGetChecked(fallback);

            assertEquals("foobar", getting.apply("foo", "bar"));

            verify(function).apply("foo", "bar");
            verify(function).onErrorGetChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                ThrowingBiFunction<String, String, String, IOException> function = Spied.throwingBiFunction((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                ThrowingSupplier<String, ParseException> fallback = Spied.throwingSupplier(() -> "bar");

                ThrowingBiFunction<String, String, String, ParseException> getting = function.onErrorGetChecked(fallback);

                assertEquals("bar", getting.apply("foo", "bar"));

                verify(function).apply("foo", "bar");
                verify(function).onErrorGetChecked(fallback);
                verify(fallback).get();
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                ThrowingBiFunction<String, String, String, IOException> function = Spied.throwingBiFunction((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                ThrowingSupplier<String, ParseException> fallback = Spied.throwingSupplier(() -> {
                    throw new ParseException("bar", 0);
                });

                ThrowingBiFunction<String, String, String, ParseException> getting = function.onErrorGetChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> getting.apply("foo", "bar"));
                assertEquals("bar", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(function).apply("foo", "bar");
                verify(function).onErrorGetChecked(fallback);
                verify(fallback).get();
                verifyNoMoreInteractions(function, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ParseException {
                ThrowingBiFunction<String, String, String, IOException> function = Spied.throwingBiFunction((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                ThrowingSupplier<String, ParseException> fallback = Spied.throwingSupplier(() -> throwable.throwUnchecked("bar"));

                ThrowingBiFunction<String, String, String, ParseException> getting = function.onErrorGetChecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.apply("foo", "bar"));
                assertEquals("bar", thrown.getMessage());

                verify(function).apply("foo", "bar");
                verify(function).onErrorGetChecked(fallback);
                verify(fallback).get();
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingBiFunction<String, String, String, IOException> function = Spied
                    .throwingBiFunction((s1, s2) -> throwable.throwUnchecked(s1 + s2));

            ThrowingSupplier<String, ParseException> fallback = Spied.throwingSupplier(() -> "bar");

            ThrowingBiFunction<String, String, String, ParseException> getting = function.onErrorGetChecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.apply("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());

            verify(function).apply("foo", "bar");
            verify(function).onErrorGetChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorGetUnchecked {

        @Test
        void testNullArgument() {
            ThrowingBiFunction<String, String, String, IOException> function = Spied.throwingBiFunction(String::concat);

            assertThrows(NullPointerException.class, () -> function.onErrorGetUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingBiFunction<String, String, String, IOException> function = Spied.throwingBiFunction(String::concat);

            Supplier<String> fallback = Spied.supplier(() -> "bar");

            BiFunction<String, String, String> getting = function.onErrorGetUnchecked(fallback);

            assertEquals("foobar", getting.apply("foo", "bar"));

            verify(function).apply("foo", "bar");
            verify(function).onErrorGetUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                ThrowingBiFunction<String, String, String, IOException> function = Spied.throwingBiFunction((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                Supplier<String> fallback = Spied.supplier(() -> "bar");

                BiFunction<String, String, String> getting = function.onErrorGetUnchecked(fallback);

                assertEquals("bar", getting.apply("foo", "bar"));

                verify(function).apply("foo", "bar");
                verify(function).onErrorGetUnchecked(fallback);
                verify(fallback).get();
                verifyNoMoreInteractions(function, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingBiFunction<String, String, String, IOException> function = Spied.throwingBiFunction((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                Supplier<String> fallback = Spied.supplier(() -> throwable.throwUnchecked("bar"));

                BiFunction<String, String, String> getting = function.onErrorGetUnchecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.apply("foo", "bar"));
                assertEquals("bar", thrown.getMessage());

                verify(function).apply("foo", "bar");
                verify(function).onErrorGetUnchecked(fallback);
                verify(fallback).get();
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingBiFunction<String, String, String, IOException> function = Spied
                    .throwingBiFunction((s1, s2) -> throwable.throwUnchecked(s1 + s2));

            Supplier<String> fallback = Spied.supplier(() -> "bar");

            BiFunction<String, String, String> getting = function.onErrorGetUnchecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.apply("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());

            verify(function).apply("foo", "bar");
            verify(function).onErrorGetUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorReturn {

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingBiFunction<String, String, String, IOException> function = Spied.throwingBiFunction(String::concat);

            BiFunction<String, String, String> returning = function.onErrorReturn("bar");

            assertEquals("foobar", returning.apply("foo", "bar"));

            verify(function).apply("foo", "bar");
            verify(function).onErrorReturn("bar");
            verifyNoMoreInteractions(function);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingBiFunction<String, String, String, IOException> function = Spied.throwingBiFunction((s1, s2) -> {
                throw new IOException(s1 + s2);
            });

            BiFunction<String, String, String> returning = function.onErrorReturn("bar");

            assertEquals("bar", returning.apply("foo", "bar"));

            verify(function).apply("foo", "bar");
            verify(function).onErrorReturn("bar");
            verifyNoMoreInteractions(function);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingBiFunction<String, String, String, IOException> function = Spied
                    .throwingBiFunction((s1, s2) -> throwable.throwUnchecked(s1 + s2));

            BiFunction<String, String, String> returning = function.onErrorReturn("bar");

            Throwable thrown = assertThrows(throwable.throwableType(), () -> returning.apply("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());

            verify(function).apply("foo", "bar");
            verify(function).onErrorReturn("bar");
            verifyNoMoreInteractions(function);
        }
    }

    @Nested
    class Unchecked {

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingBiFunction<String, String, String, IOException> function = Spied.throwingBiFunction(String::concat);

            BiFunction<String, String, String> unchecked = function.unchecked();

            assertEquals("foobar", unchecked.apply("foo", "bar"));

            verify(function).apply("foo", "bar");
            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(function);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingBiFunction<String, String, String, IOException> function = Spied.throwingBiFunction((s1, s2) -> {
                throw new IOException(s1 + s2);
            });

            BiFunction<String, String, String> unchecked = function.unchecked();

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.apply("foo", "bar"));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foobar", cause.getMessage());

            verify(function).apply("foo", "bar");
            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(function);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingBiFunction<String, String, String, IOException> function = Spied
                    .throwingBiFunction((s1, s2) -> throwable.throwUnchecked(s1 + s2));

            BiFunction<String, String, String> unchecked = function.unchecked();

            Throwable thrown = assertThrows(throwable.throwableType(), () -> unchecked.apply("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());

            verify(function).apply("foo", "bar");
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
            ThrowingBiFunction<String, String, String, IOException> function = ThrowingBiFunction.of(String::concat);

            assertEquals("foobar", function.apply("foo", "bar"));
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
            ThrowingBiFunction<String, String, String, IOException> function = Spied.throwingBiFunction(String::concat);

            BiFunction<String, String, String> unchecked = ThrowingBiFunction.unchecked(function);

            assertEquals("foobar", unchecked.apply("foo", "bar"));

            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verify(function).apply("foo", "bar");
            verifyNoMoreInteractions(function);
        }

        @Test
        void testArgumentThrowsChecked() throws IOException {
            ThrowingBiFunction<String, String, String, IOException> function = Spied.throwingBiFunction((s1, s2) -> {
                throw new IOException(s1 + s2);
            });

            BiFunction<String, String, String> unchecked = ThrowingBiFunction.unchecked(function);

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.apply("foo", "bar"));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foobar", cause.getMessage());

            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verify(function).apply("foo", "bar");
            verifyNoMoreInteractions(function);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testArgumentThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingBiFunction<String, String, String, IOException> function = Spied
                    .throwingBiFunction((s1, s2) -> throwable.throwUnchecked(s1 + s2));

            BiFunction<String, String, String> unchecked = ThrowingBiFunction.unchecked(function);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> unchecked.apply("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());

            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verify(function).apply("foo", "bar");
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
            ThrowingBiFunction<String, String, String, IOException> function = ThrowingBiFunction.checked(String::concat);

            assertEquals("foobar", function.apply("foo", "bar"));
        }

        @Test
        void testArgumentThrowsUnchecked() {
            ThrowingBiFunction<String, String, String, IOException> function = ThrowingBiFunction.checked((s1, s2) -> {
                throw new UncheckedException(s1 + s2, new IOException());
            });

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> function.apply("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertNull(cause.getMessage());
        }
    }

    @Nested
    class CheckedUnwrapper {

        @Test
        void testNullArguments() {
            BiFunction<String, String, String> function = String::concat;

            assertThrows(NullPointerException.class, () -> ThrowingBiFunction.checked(null, IOException.class));
            assertThrows(NullPointerException.class, () -> ThrowingBiFunction.checked(function, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ThrowingBiFunction<String, String, String, IOException> function = ThrowingBiFunction.checked(String::concat, IOException.class);

            assertEquals("foobar", function.apply("foo", "bar"));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                ThrowingBiFunction<String, String, String, IOException> function = ThrowingBiFunction.checked((s1, s2) -> {
                    throw new UncheckedException(new IOException(s1 + s2));
                }, IOException.class);

                IOException thrown = assertThrows(IOException.class, () -> function.apply("foo", "bar"));
                assertEquals("foobar", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                ThrowingBiFunction<String, String, String, IOException> function = ThrowingBiFunction.checked((s1, s2) -> {
                    throw new UncheckedException(new FileNotFoundException(s1 + s2));
                }, IOException.class);

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class, () -> function.apply("foo", "bar"));
                assertEquals("foobar", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                ThrowingBiFunction<String, String, String, IOException> function = ThrowingBiFunction.checked((s1, s2) -> {
                    throw new UncheckedException(new ParseException(s1 + s2, 0));
                }, IOException.class);

                UncheckedException thrown = assertThrows(UncheckedException.class, () -> function.apply("foo", "bar"));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("foobar", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            ThrowingBiFunction<String, String, String, IOException> function = ThrowingBiFunction.checked((s1, s2) -> {
                throw new IllegalStateException(s1 + s2);
            }, IOException.class);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> function.apply("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());
        }
    }

    @Nested
    class InvokeAndUnwrap {

        @Test
        void testNullArguments() throws IOException {
            BiFunction<String, String, String> function = (s1, s2) -> s1 + s2;

            assertThrows(NullPointerException.class, () -> ThrowingBiFunction.invokeAndUnwrap(null, "foo", "bar", IOException.class));
            assertThrows(NullPointerException.class, () -> ThrowingBiFunction.invokeAndUnwrap(function, "foo", "bar", null));

            assertEquals("nullbar", ThrowingBiFunction.invokeAndUnwrap(function, null, "bar", IOException.class));
            assertEquals("foonull", ThrowingBiFunction.invokeAndUnwrap(function, "foo", null, IOException.class));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            BiFunction<String, String, String> function = String::concat;

            assertEquals("foobar", ThrowingBiFunction.invokeAndUnwrap(function, "foo", "bar", IOException.class));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                BiFunction<String, String, String> function = (s1, s2) -> {
                    throw new UncheckedException(new IOException(s1 + s2));
                };

                IOException thrown = assertThrows(IOException.class,
                        () -> ThrowingBiFunction.invokeAndUnwrap(function, "foo", "bar", IOException.class));
                assertEquals("foobar", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                BiFunction<String, String, String> function = (s1, s2) -> {
                    throw new UncheckedException(new FileNotFoundException(s1 + s2));
                };

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class,
                        () -> ThrowingBiFunction.invokeAndUnwrap(function, "foo", "bar", IOException.class));
                assertEquals("foobar", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                BiFunction<String, String, String> function = (s1, s2) -> {
                    throw new UncheckedException(new ParseException(s1 + s2, 0));
                };

                UncheckedException thrown = assertThrows(UncheckedException.class,
                        () -> ThrowingBiFunction.invokeAndUnwrap(function, "foo", "bar", IOException.class));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("foobar", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            BiFunction<String, String, String> function = (s1, s2) -> {
                throw new IllegalStateException(s1 + s2);
            };

            IllegalStateException thrown = assertThrows(IllegalStateException.class,
                    () -> ThrowingBiFunction.invokeAndUnwrap(function, "foo", "bar", IOException.class));
            assertEquals("foobar", thrown.getMessage());
        }
    }
}
