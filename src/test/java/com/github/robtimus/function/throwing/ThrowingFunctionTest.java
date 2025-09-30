/*
 * ThrowingFunctionTest.java
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
import java.util.function.Supplier;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

@SuppressWarnings("nls")
class ThrowingFunctionTest {

    @Nested
    class Compose {

        @Test
        void testNullArgument() {
            ThrowingFunction<String, String, IOException> function = Spied.throwingFunction(String::toUpperCase);

            assertThrows(NullPointerException.class, () -> function.compose(null));
        }

        @Test
        void testNeitherThrows() throws IOException {
            ThrowingFunction<String, String, IOException> function = Spied.throwingFunction(s -> s + s);
            ThrowingFunction<Integer, String, IOException> before = Spied.throwingFunction(Object::toString);

            ThrowingFunction<Integer, String, IOException> composed = function.compose(before);

            assertEquals("11", composed.apply(1));

            verify(before).apply(1);
            verify(function).apply("1");
            verify(function).compose(before);
            verifyNoMoreInteractions(function, before);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingFunction<String, String, IOException> function = Spied.throwingFunction(s -> {
                throw new IOException(s);
            });
            ThrowingFunction<Integer, String, IOException> before = Spied.throwingFunction(Object::toString);

            ThrowingFunction<Integer, String, IOException> composed = function.compose(before);

            IOException thrown = assertThrows(IOException.class, () -> composed.apply(1));
            assertEquals("1", thrown.getMessage());

            verify(before).apply(1);
            verify(function).apply("1");
            verify(function).compose(before);
            verifyNoMoreInteractions(function, before);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingFunction<String, String, IOException> function = Spied.throwingFunction(throwable::throwUnchecked);
            ThrowingFunction<Integer, String, IOException> before = Spied.throwingFunction(Object::toString);

            ThrowingFunction<Integer, String, IOException> composed = function.compose(before);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> composed.apply(1));
            assertEquals("1", thrown.getMessage());

            verify(before).apply(1);
            verify(function).apply("1");
            verify(function).compose(before);
            verifyNoMoreInteractions(function, before);
        }

        @Test
        void testBeforeThrowsChecked() throws IOException {
            ThrowingFunction<String, String, IOException> function = Spied.throwingFunction(String::toUpperCase);
            ThrowingFunction<Integer, String, IOException> before = Spied.throwingFunction(i -> {
                throw new IOException(i.toString());
            });

            ThrowingFunction<Integer, String, IOException> composed = function.compose(before);

            IOException thrown = assertThrows(IOException.class, () -> composed.apply(1));
            assertEquals("1", thrown.getMessage());

            verify(before).apply(1);
            verify(function).compose(before);
            verifyNoMoreInteractions(function, before);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testBeforeThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingFunction<String, String, IOException> function = Spied.throwingFunction(String::toUpperCase);
            ThrowingFunction<Integer, String, IOException> before = Spied.throwingFunction(throwable::throwUnchecked);

            ThrowingFunction<Integer, String, IOException> composed = function.compose(before);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> composed.apply(1));
            assertEquals("1", thrown.getMessage());

            verify(before).apply(1);
            verify(function).compose(before);
            verifyNoMoreInteractions(function, before);
        }
    }

    @Nested
    class AndThen {

        @Test
        void testNullArgument() {
            ThrowingFunction<String, String, IOException> function = Spied.throwingFunction(String::toUpperCase);

            assertThrows(NullPointerException.class, () -> function.andThen(null));
        }

        @Test
        void testNeitherThrows() throws IOException {
            ThrowingFunction<Integer, String, IOException> function = Spied.throwingFunction(Object::toString);
            ThrowingFunction<String, String, IOException> after = Spied.throwingFunction(s -> s + s);

            ThrowingFunction<Integer, String, IOException> composed = function.andThen(after);

            assertEquals("11", composed.apply(1));

            verify(function).apply(1);
            verify(function).andThen(after);
            verify(after).apply("1");
            verifyNoMoreInteractions(function, after);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingFunction<Integer, String, IOException> function = Spied.throwingFunction(i -> {
                throw new IOException(i.toString());
            });
            ThrowingFunction<String, String, IOException> after = Spied.throwingFunction(Object::toString);

            ThrowingFunction<Integer, String, IOException> composed = function.andThen(after);

            IOException thrown = assertThrows(IOException.class, () -> composed.apply(1));
            assertEquals("1", thrown.getMessage());

            verify(function).apply(1);
            verify(function).andThen(after);
            verifyNoMoreInteractions(function, after);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingFunction<Integer, String, IOException> function = Spied.throwingFunction(throwable::throwUnchecked);
            ThrowingFunction<String, String, IOException> after = Spied.throwingFunction(Object::toString);

            ThrowingFunction<Integer, String, IOException> composed = function.andThen(after);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> composed.apply(1));
            assertEquals("1", thrown.getMessage());

            verify(function).apply(1);
            verify(function).andThen(after);
            verifyNoMoreInteractions(function, after);
        }

        @Test
        void testAfterThrowsChecked() throws IOException {
            ThrowingFunction<Integer, String, IOException> function = Spied.throwingFunction(Object::toString);
            ThrowingFunction<String, String, IOException> after = Spied.throwingFunction(s -> {
                throw new IOException(s);
            });

            ThrowingFunction<Integer, String, IOException> composed = function.andThen(after);

            IOException thrown = assertThrows(IOException.class, () -> composed.apply(1));
            assertEquals("1", thrown.getMessage());

            verify(function).apply(1);
            verify(function).andThen(after);
            verify(after).apply("1");
            verifyNoMoreInteractions(function, after);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testAfterThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingFunction<Integer, String, IOException> function = Spied.throwingFunction(Object::toString);
            ThrowingFunction<String, String, IOException> after = Spied.throwingFunction(throwable::throwUnchecked);

            ThrowingFunction<Integer, String, IOException> composed = function.andThen(after);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> composed.apply(1));
            assertEquals("1", thrown.getMessage());

            verify(function).apply(1);
            verify(function).andThen(after);
            verify(after).apply("1");
            verifyNoMoreInteractions(function, after);
        }
    }

    @Nested
    class OnErrorThrowAsChecked {

        @Test
        void testNullArgument() {
            ThrowingFunction<String, String, IOException> function = Spied.throwingFunction(String::toUpperCase);

            assertThrows(NullPointerException.class, () -> function.onErrorThrowAsChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingFunction<String, String, IOException> function = Spied.throwingFunction(String::toUpperCase);

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingFunction<String, String, ExecutionException> throwing = function.onErrorThrowAsChecked(errorMapper);

            assertEquals("FOO", throwing.apply("foo"));

            verify(function).apply("foo");
            verify(function).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingFunction<String, String, IOException> function = Spied.throwingFunction(s -> {
                throw new IOException(s);
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingFunction<String, String, ExecutionException> throwing = function.onErrorThrowAsChecked(errorMapper);

            ExecutionException thrown = assertThrows(ExecutionException.class, () -> throwing.apply("foo"));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo", cause.getMessage());

            verify(function).apply("foo");
            verify(function).onErrorThrowAsChecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(function, errorMapper);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingFunction<String, String, IOException> function = Spied.throwingFunction(throwable::throwUnchecked);

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingFunction<String, String, ExecutionException> throwing = function.onErrorThrowAsChecked(errorMapper);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> throwing.apply("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(function).apply("foo");
            verify(function).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }
    }

    @Nested
    class OnErrorThrowAsUnchecked {

        @Test
        void testNullArgument() {
            ThrowingFunction<String, String, IOException> function = Spied.throwingFunction(String::toUpperCase);

            assertThrows(NullPointerException.class, () -> function.onErrorThrowAsUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingFunction<String, String, IOException> function = Spied.throwingFunction(String::toUpperCase);

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            Function<String, String> throwing = function.onErrorThrowAsUnchecked(errorMapper);

            assertEquals("FOO", throwing.apply("foo"));

            verify(function).apply("foo");
            verify(function).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingFunction<String, String, IOException> function = Spied.throwingFunction(s -> {
                throw new IOException(s);
            });

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            Function<String, String> throwing = function.onErrorThrowAsUnchecked(errorMapper);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> throwing.apply("foo"));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo", cause.getMessage());

            verify(function).apply("foo");
            verify(function).onErrorThrowAsUnchecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(function, errorMapper);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingFunction<String, String, IOException> function = Spied.throwingFunction(throwable::throwUnchecked);

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            Function<String, String> throwing = function.onErrorThrowAsUnchecked(errorMapper);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> throwing.apply("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(function).apply("foo");
            verify(function).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }
    }

    @Nested
    class OnErrorHandleChecked {

        @Test
        void testNullArgument() {
            ThrowingFunction<String, String, IOException> function = Spied.throwingFunction(String::toUpperCase);

            assertThrows(NullPointerException.class, () -> function.onErrorHandleChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingFunction<String, String, IOException> function = Spied.throwingFunction(String::toUpperCase);

            ThrowingFunction<IOException, String, ExecutionException> errorHandler = Spied.throwingFunction(Exception::getMessage);

            ThrowingFunction<String, String, ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

            assertEquals("FOO", handling.apply("foo"));

            verify(function).apply("foo");
            verify(function).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException, ExecutionException {
                ThrowingFunction<String, String, IOException> function = Spied.throwingFunction(s -> {
                    throw new IOException(s);
                });

                ThrowingFunction<IOException, String, ExecutionException> errorHandler = Spied.throwingFunction(Exception::getMessage);

                ThrowingFunction<String, String, ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

                assertEquals("foo", handling.apply("foo"));

                verify(function).apply("foo");
                verify(function).onErrorHandleChecked(errorHandler);
                verify(errorHandler).apply(any());
                verifyNoMoreInteractions(function, errorHandler);
            }

            @Test
            void testHandlerThrowsChecked() throws IOException, ExecutionException {
                ThrowingFunction<String, String, IOException> function = Spied.throwingFunction(s -> {
                    throw new IOException(s);
                });

                ThrowingFunction<IOException, String, ExecutionException> errorHandler = Spied.throwingFunction(e -> {
                    throw new ExecutionException(e);
                });

                ThrowingFunction<String, String, ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

                ExecutionException thrown = assertThrows(ExecutionException.class, () -> handling.apply("foo"));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());

                verify(function).apply("foo");
                verify(function).onErrorHandleChecked(errorHandler);
                verify(errorHandler).apply(any());
                verifyNoMoreInteractions(function, errorHandler);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testHandlerThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ExecutionException {
                ThrowingFunction<String, String, IOException> function = Spied.throwingFunction(s -> {
                    throw new IOException(s);
                });

                ThrowingFunction<IOException, String, ExecutionException> errorHandler = Spied.throwingFunction(throwable::throwUnchecked);

                ThrowingFunction<String, String, ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.apply("foo"));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());

                verify(function).apply("foo");
                verify(function).onErrorHandleChecked(errorHandler);
                verify(errorHandler).apply(any());
                verifyNoMoreInteractions(function, errorHandler);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingFunction<String, String, IOException> function = Spied.throwingFunction(throwable::throwUnchecked);

            ThrowingFunction<IOException, String, ExecutionException> errorHandler = Spied.throwingFunction(Exception::getMessage);

            ThrowingFunction<String, String, ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.apply("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(function).apply("foo");
            verify(function).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }
    }

    @Nested
    class OnErrorHandleUnchecked {

        @Test
        void testNullArgument() {
            ThrowingFunction<String, String, IOException> function = Spied.throwingFunction(String::toUpperCase);

            assertThrows(NullPointerException.class, () -> function.onErrorHandleUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingFunction<String, String, IOException> function = Spied.throwingFunction(String::toUpperCase);

            Function<IOException, String> errorHandler = Spied.function(Exception::getMessage);

            Function<String, String> handling = function.onErrorHandleUnchecked(errorHandler);

            assertEquals("FOO", handling.apply("foo"));

            verify(function).apply("foo");
            verify(function).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException {
                ThrowingFunction<String, String, IOException> function = Spied.throwingFunction(s -> {
                    throw new IOException(s);
                });

                Function<IOException, String> errorHandler = Spied.function(Exception::getMessage);

                Function<String, String> handling = function.onErrorHandleUnchecked(errorHandler);

                assertEquals("foo", handling.apply("foo"));

                verify(function).apply("foo");
                verify(function).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).apply(any());
                verifyNoMoreInteractions(function, errorHandler);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testHandlerThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingFunction<String, String, IOException> function = Spied.throwingFunction(s -> {
                    throw new IOException(s);
                });

                Function<IOException, String> errorHandler = Spied.function(throwable::throwUnchecked);

                Function<String, String> handling = function.onErrorHandleUnchecked(errorHandler);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.apply("foo"));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());

                verify(function).apply("foo");
                verify(function).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).apply(any());
                verifyNoMoreInteractions(function, errorHandler);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingFunction<String, String, IOException> function = Spied.throwingFunction(throwable::throwUnchecked);

            Function<IOException, String> errorHandler = Spied.function(Exception::getMessage);

            Function<String, String> handling = function.onErrorHandleUnchecked(errorHandler);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.apply("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(function).apply("foo");
            verify(function).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }
    }

    @Nested
    class OnErrorApplyChecked {

        @Test
        void testNullArgument() {
            ThrowingFunction<String, String, IOException> function = Spied.throwingFunction(String::toUpperCase);

            assertThrows(NullPointerException.class, () -> function.onErrorApplyChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingFunction<String, String, IOException> function = Spied.throwingFunction(String::toUpperCase);

            ThrowingFunction<String, String, ExecutionException> fallback = Spied.throwingFunction(s -> s + s);

            ThrowingFunction<String, String, ExecutionException> applying = function.onErrorApplyChecked(fallback);

            assertEquals("FOO", applying.apply("foo"));

            verify(function).apply("foo");
            verify(function).onErrorApplyChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                ThrowingFunction<String, String, IOException> function = Spied.throwingFunction(s -> {
                    throw new IOException(s);
                });

                ThrowingFunction<String, String, ParseException> fallback = Spied.throwingFunction(s -> s + s);

                ThrowingFunction<String, String, ParseException> applying = function.onErrorApplyChecked(fallback);

                assertEquals("foofoo", applying.apply("foo"));

                verify(function).apply("foo");
                verify(function).onErrorApplyChecked(fallback);
                verify(fallback).apply("foo");
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                ThrowingFunction<String, String, IOException> function = Spied.throwingFunction(s -> {
                    throw new IOException(s);
                });

                ThrowingFunction<String, String, ParseException> fallback = Spied.throwingFunction(s -> {
                    throw new ParseException(s, 0);
                });

                ThrowingFunction<String, String, ParseException> applying = function.onErrorApplyChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> applying.apply("foo"));
                assertEquals("foo", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(function).apply("foo");
                verify(function).onErrorApplyChecked(fallback);
                verify(fallback).apply("foo");
                verifyNoMoreInteractions(function, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ParseException {
                ThrowingFunction<String, String, IOException> function = Spied.throwingFunction(s -> {
                    throw new IOException(s);
                });

                ThrowingFunction<String, String, ParseException> fallback = Spied.throwingFunction(throwable::throwUnchecked);

                ThrowingFunction<String, String, ParseException> applying = function.onErrorApplyChecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.apply("foo"));
                assertEquals("foo", thrown.getMessage());

                verify(function).apply("foo");
                verify(function).onErrorApplyChecked(fallback);
                verify(fallback).apply("foo");
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingFunction<String, String, IOException> function = Spied.throwingFunction(throwable::throwUnchecked);

            ThrowingFunction<String, String, ParseException> fallback = Spied.throwingFunction(s -> s + s);

            ThrowingFunction<String, String, ParseException> applying = function.onErrorApplyChecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.apply("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(function).apply("foo");
            verify(function).onErrorApplyChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorApplyUnchecked {

        @Test
        void testNullArgument() {
            ThrowingFunction<String, String, IOException> function = Spied.throwingFunction(String::toUpperCase);

            assertThrows(NullPointerException.class, () -> function.onErrorApplyUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingFunction<String, String, IOException> function = Spied.throwingFunction(String::toUpperCase);

            Function<String, String> fallback = Spied.function(s -> s + s);

            Function<String, String> applying = function.onErrorApplyUnchecked(fallback);

            assertEquals("FOO", applying.apply("foo"));

            verify(function).apply("foo");
            verify(function).onErrorApplyUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                ThrowingFunction<String, String, IOException> function = Spied.throwingFunction(s -> {
                    throw new IOException(s);
                });

                Function<String, String> fallback = Spied.function(s -> s + s);

                Function<String, String> applying = function.onErrorApplyUnchecked(fallback);

                assertEquals("foofoo", applying.apply("foo"));

                verify(function).apply("foo");
                verify(function).onErrorApplyUnchecked(fallback);
                verify(fallback).apply("foo");
                verifyNoMoreInteractions(function, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingFunction<String, String, IOException> function = Spied.throwingFunction(s -> {
                    throw new IOException(s);
                });

                Function<String, String> fallback = Spied.function(throwable::throwUnchecked);

                Function<String, String> applying = function.onErrorApplyUnchecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.apply("foo"));
                assertEquals("foo", thrown.getMessage());

                verify(function).apply("foo");
                verify(function).onErrorApplyUnchecked(fallback);
                verify(fallback).apply("foo");
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingFunction<String, String, IOException> function = Spied.throwingFunction(throwable::throwUnchecked);

            Function<String, String> fallback = Spied.function(s -> s + s);

            Function<String, String> applying = function.onErrorApplyUnchecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.apply("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(function).apply("foo");
            verify(function).onErrorApplyUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorGetChecked {

        @Test
        void testNullArgument() {
            ThrowingFunction<String, String, IOException> function = Spied.throwingFunction(String::toUpperCase);

            assertThrows(NullPointerException.class, () -> function.onErrorGetChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingFunction<String, String, IOException> function = Spied.throwingFunction(String::toUpperCase);

            ThrowingSupplier<String, ExecutionException> fallback = Spied.throwingSupplier(() -> "bar");

            ThrowingFunction<String, String, ExecutionException> getting = function.onErrorGetChecked(fallback);

            assertEquals("FOO", getting.apply("foo"));

            verify(function).apply("foo");
            verify(function).onErrorGetChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                ThrowingFunction<String, String, IOException> function = Spied.throwingFunction(s -> {
                    throw new IOException(s);
                });

                ThrowingSupplier<String, ParseException> fallback = Spied.throwingSupplier(() -> "bar");

                ThrowingFunction<String, String, ParseException> getting = function.onErrorGetChecked(fallback);

                assertEquals("bar", getting.apply("foo"));

                verify(function).apply("foo");
                verify(function).onErrorGetChecked(fallback);
                verify(fallback).get();
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                ThrowingFunction<String, String, IOException> function = Spied.throwingFunction(s -> {
                    throw new IOException(s);
                });

                ThrowingSupplier<String, ParseException> fallback = Spied.throwingSupplier(() -> {
                    throw new ParseException("bar", 0);
                });

                ThrowingFunction<String, String, ParseException> getting = function.onErrorGetChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> getting.apply("foo"));
                assertEquals("bar", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(function).apply("foo");
                verify(function).onErrorGetChecked(fallback);
                verify(fallback).get();
                verifyNoMoreInteractions(function, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ParseException {
                ThrowingFunction<String, String, IOException> function = Spied.throwingFunction(s -> {
                    throw new IOException(s);
                });

                ThrowingSupplier<String, ParseException> fallback = Spied.throwingSupplier(() -> throwable.throwUnchecked("bar"));

                ThrowingFunction<String, String, ParseException> getting = function.onErrorGetChecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.apply("foo"));
                assertEquals("bar", thrown.getMessage());

                verify(function).apply("foo");
                verify(function).onErrorGetChecked(fallback);
                verify(fallback).get();
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingFunction<String, String, IOException> function = Spied.throwingFunction(throwable::throwUnchecked);

            ThrowingSupplier<String, ParseException> fallback = Spied.throwingSupplier(() -> "bar");

            ThrowingFunction<String, String, ParseException> getting = function.onErrorGetChecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.apply("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(function).apply("foo");
            verify(function).onErrorGetChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorGetUnchecked {

        @Test
        void testNullArgument() {
            ThrowingFunction<String, String, IOException> function = Spied.throwingFunction(String::toUpperCase);

            assertThrows(NullPointerException.class, () -> function.onErrorGetUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingFunction<String, String, IOException> function = Spied.throwingFunction(String::toUpperCase);

            Supplier<String> fallback = Spied.supplier(() -> "bar");

            Function<String, String> getting = function.onErrorGetUnchecked(fallback);

            assertEquals("FOO", getting.apply("foo"));

            verify(function).apply("foo");
            verify(function).onErrorGetUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                ThrowingFunction<String, String, IOException> function = Spied.throwingFunction(s -> {
                    throw new IOException(s);
                });

                Supplier<String> fallback = Spied.supplier(() -> "bar");

                Function<String, String> getting = function.onErrorGetUnchecked(fallback);

                assertEquals("bar", getting.apply("foo"));

                verify(function).apply("foo");
                verify(function).onErrorGetUnchecked(fallback);
                verify(fallback).get();
                verifyNoMoreInteractions(function, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingFunction<String, String, IOException> function = Spied.throwingFunction(s -> {
                    throw new IOException(s);
                });

                Supplier<String> fallback = Spied.supplier(() -> throwable.throwUnchecked("bar"));

                Function<String, String> getting = function.onErrorGetUnchecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.apply("foo"));
                assertEquals("bar", thrown.getMessage());

                verify(function).apply("foo");
                verify(function).onErrorGetUnchecked(fallback);
                verify(fallback).get();
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingFunction<String, String, IOException> function = Spied.throwingFunction(throwable::throwUnchecked);

            Supplier<String> fallback = Spied.supplier(() -> "bar");

            Function<String, String> getting = function.onErrorGetUnchecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.apply("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(function).apply("foo");
            verify(function).onErrorGetUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorReturn {

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingFunction<String, String, IOException> function = Spied.throwingFunction(String::toUpperCase);

            Function<String, String> returning = function.onErrorReturn("bar");

            assertEquals("FOO", returning.apply("foo"));

            verify(function).apply("foo");
            verify(function).onErrorReturn("bar");
            verifyNoMoreInteractions(function);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingFunction<String, String, IOException> function = Spied.throwingFunction(s -> {
                throw new IOException(s);
            });

            Function<String, String> returning = function.onErrorReturn("bar");

            assertEquals("bar", returning.apply("foo"));

            verify(function).apply("foo");
            verify(function).onErrorReturn("bar");
            verifyNoMoreInteractions(function);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingFunction<String, String, IOException> function = Spied.throwingFunction(throwable::throwUnchecked);

            Function<String, String> returning = function.onErrorReturn("bar");

            Throwable thrown = assertThrows(throwable.throwableType(), () -> returning.apply("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(function).apply("foo");
            verify(function).onErrorReturn("bar");
            verifyNoMoreInteractions(function);
        }
    }

    @Nested
    class Unchecked {

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingFunction<String, String, IOException> function = Spied.throwingFunction(String::toUpperCase);

            Function<String, String> unchecked = function.unchecked();

            assertEquals("FOO", unchecked.apply("foo"));

            verify(function).apply("foo");
            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(function);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingFunction<String, String, IOException> function = Spied.throwingFunction(s -> {
                throw new IOException(s);
            });

            Function<String, String> unchecked = function.unchecked();

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.apply("foo"));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo", cause.getMessage());

            verify(function).apply("foo");
            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(function);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingFunction<String, String, IOException> function = Spied.throwingFunction(throwable::throwUnchecked);

            Function<String, String> unchecked = function.unchecked();

            Throwable thrown = assertThrows(throwable.throwableType(), () -> unchecked.apply("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(function).apply("foo");
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
            ThrowingFunction<String, String, IOException> function = ThrowingFunction.of(String::toUpperCase);

            assertEquals("FOO", function.apply("foo"));
        }
    }

    @Nested
    class Identity {

        @Test
        void testNullArgument() throws IOException {
            ThrowingFunction<String, String, IOException> function = ThrowingFunction.identity();

            assertNull(function.apply(null));
        }

        @Test
        void testNonNullArgument() throws IOException {
            ThrowingFunction<String, String, IOException> function = ThrowingFunction.identity();

            assertEquals("foo", function.apply("foo"));
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
            ThrowingFunction<String, String, IOException> function = Spied.throwingFunction(String::toUpperCase);

            Function<String, String> unchecked = ThrowingFunction.unchecked(function);

            assertEquals("FOO", unchecked.apply("foo"));

            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verify(function).apply("foo");
            verifyNoMoreInteractions(function);
        }

        @Test
        void testArgumentThrowsChecked() throws IOException {
            ThrowingFunction<String, String, IOException> function = Spied.throwingFunction(s -> {
                throw new IOException(s);
            });

            Function<String, String> unchecked = ThrowingFunction.unchecked(function);

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.apply("foo"));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo", cause.getMessage());

            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verify(function).apply("foo");
            verifyNoMoreInteractions(function);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testArgumentThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingFunction<String, String, IOException> function = Spied.throwingFunction(throwable::throwUnchecked);

            Function<String, String> unchecked = ThrowingFunction.unchecked(function);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> unchecked.apply("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verify(function).apply("foo");
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
            ThrowingFunction<String, String, IOException> function = ThrowingFunction.checked(String::toUpperCase);

            assertEquals("FOO", function.apply("foo"));
        }

        @Test
        void testArgumentThrowsUnchecked() {
            ThrowingFunction<String, String, IOException> function = ThrowingFunction.checked(s -> {
                throw new UncheckedException(s, new IOException());
            });

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> function.apply("foo"));
            assertEquals("foo", thrown.getMessage());
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertNull(cause.getMessage());
        }
    }

    @Nested
    class CheckedUnwrapper {

        @Test
        void testNullArguments() {
            Function<String, String> function = String::toUpperCase;

            assertThrows(NullPointerException.class, () -> ThrowingFunction.checked(null, IOException.class));
            assertThrows(NullPointerException.class, () -> ThrowingFunction.checked(function, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ThrowingFunction<String, String, IOException> function = ThrowingFunction.checked(String::toUpperCase, IOException.class);

            assertEquals("FOO", function.apply("foo"));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                ThrowingFunction<String, String, IOException> function = ThrowingFunction.checked(s -> {
                    throw new UncheckedException(new IOException(s));
                }, IOException.class);

                IOException thrown = assertThrows(IOException.class, () -> function.apply("foo"));
                assertEquals("foo", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                ThrowingFunction<String, String, IOException> function = ThrowingFunction.checked(s -> {
                    throw new UncheckedException(new FileNotFoundException(s));
                }, IOException.class);

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class, () -> function.apply("foo"));
                assertEquals("foo", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                ThrowingFunction<String, String, IOException> function = ThrowingFunction.checked(s -> {
                    throw new UncheckedException(new ParseException(s, 0));
                }, IOException.class);

                UncheckedException thrown = assertThrows(UncheckedException.class, () -> function.apply("foo"));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            ThrowingFunction<String, String, IOException> function = ThrowingFunction.checked(s -> {
                throw new IllegalStateException(s);
            }, IOException.class);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> function.apply("foo"));
            assertEquals("foo", thrown.getMessage());
        }
    }

    @Nested
    class InvokeAndUnwrap {

        @Test
        void testNullArguments() throws IOException {
            Function<String, String> function = s -> String.valueOf(s).toUpperCase();

            assertThrows(NullPointerException.class, () -> ThrowingFunction.invokeAndUnwrap(null, "foo", IOException.class));
            assertThrows(NullPointerException.class, () -> ThrowingFunction.invokeAndUnwrap(function, "foo", null));

            assertEquals("NULL", ThrowingFunction.invokeAndUnwrap(function, null, IOException.class));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            Function<String, String> function = String::toUpperCase;

            assertEquals("FOO", ThrowingFunction.invokeAndUnwrap(function, "foo", IOException.class));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                Function<String, String> function = s -> {
                    throw new UncheckedException(new IOException(s));
                };

                IOException thrown = assertThrows(IOException.class, () -> ThrowingFunction.invokeAndUnwrap(function, "foo", IOException.class));
                assertEquals("foo", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                Function<String, String> function = s -> {
                    throw new UncheckedException(new FileNotFoundException(s));
                };

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class,
                        () -> ThrowingFunction.invokeAndUnwrap(function, "foo", IOException.class));
                assertEquals("foo", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                Function<String, String> function = s -> {
                    throw new UncheckedException(new ParseException(s, 0));
                };

                UncheckedException thrown = assertThrows(UncheckedException.class,
                        () -> ThrowingFunction.invokeAndUnwrap(function, "foo", IOException.class));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            Function<String, String> function = s -> {
                throw new IllegalStateException(s);
            };

            IllegalStateException thrown = assertThrows(IllegalStateException.class,
                    () -> ThrowingFunction.invokeAndUnwrap(function, "foo", IOException.class));
            assertEquals("foo", thrown.getMessage());
        }
    }
}
