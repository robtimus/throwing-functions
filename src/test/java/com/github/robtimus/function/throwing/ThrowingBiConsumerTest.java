/*
 * ThrowingBiConsumerTest.java
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
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

@SuppressWarnings("nls")
class ThrowingBiConsumerTest {

    @Nested
    class AndThen {

        @Test
        void testNullArgument() {
            ThrowingBiConsumer<String, String, IOException> consumer = Spied.throwingBiConsumer(String::concat);

            assertThrows(NullPointerException.class, () -> consumer.andThen(null));
        }

        @Test
        void testNeitherThrows() throws IOException {
            ThrowingBiConsumer<String, String, IOException> consumer = Spied.throwingBiConsumer(String::concat);
            ThrowingBiConsumer<String, String, IOException> after = Spied.throwingBiConsumer(Objects::equals);

            ThrowingBiConsumer<String, String, IOException> composed = consumer.andThen(after);

            composed.accept("foo", "bar");

            verify(consumer).accept("foo", "bar");
            verify(consumer).andThen(after);
            verify(after).accept("foo", "bar");
            verifyNoMoreInteractions(consumer, after);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingBiConsumer<String, String, IOException> consumer = Spied.throwingBiConsumer((s1, s2) -> {
                throw new IOException(s1 + s2);
            });
            ThrowingBiConsumer<String, String, IOException> after = Spied.throwingBiConsumer(Objects::equals);

            ThrowingBiConsumer<String, String, IOException> composed = consumer.andThen(after);

            IOException thrown = assertThrows(IOException.class, () -> composed.accept("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());

            verify(consumer).accept("foo", "bar");
            verify(consumer).andThen(after);
            verifyNoMoreInteractions(consumer, after);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingBiConsumer<String, String, IOException> consumer = Spied.throwingBiConsumer((s1, s2) -> throwable.throwUnchecked(s1 + s2));
            ThrowingBiConsumer<String, String, IOException> after = Spied.throwingBiConsumer(Objects::equals);

            ThrowingBiConsumer<String, String, IOException> composed = consumer.andThen(after);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> composed.accept("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());

            verify(consumer).accept("foo", "bar");
            verify(consumer).andThen(after);
            verifyNoMoreInteractions(consumer, after);
        }

        @Test
        void testAfterThrowsChecked() throws IOException {
            ThrowingBiConsumer<String, String, IOException> consumer = Spied.throwingBiConsumer(String::concat);
            ThrowingBiConsumer<String, String, IOException> after = Spied.throwingBiConsumer((s1, s2) -> {
                throw new IOException(s1 + s2);
            });

            ThrowingBiConsumer<String, String, IOException> composed = consumer.andThen(after);

            IOException thrown = assertThrows(IOException.class, () -> composed.accept("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());

            verify(consumer).accept("foo", "bar");
            verify(consumer).andThen(after);
            verify(after).accept("foo", "bar");
            verifyNoMoreInteractions(consumer, after);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testAfterThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingBiConsumer<String, String, IOException> consumer = Spied.throwingBiConsumer(String::concat);
            ThrowingBiConsumer<String, String, IOException> after = Spied.throwingBiConsumer((s1, s2) -> throwable.throwUnchecked(s1 + s2));

            ThrowingBiConsumer<String, String, IOException> composed = consumer.andThen(after);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> composed.accept("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());

            verify(consumer).accept("foo", "bar");
            verify(consumer).andThen(after);
            verify(after).accept("foo", "bar");
            verifyNoMoreInteractions(consumer, after);
        }
    }

    @Nested
    class OnErrorThrowAsChecked {

        @Test
        void testNullArgument() {
            ThrowingBiConsumer<String, String, IOException> consumer = Spied.throwingBiConsumer(String::concat);

            assertThrows(NullPointerException.class, () -> consumer.onErrorThrowAsChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingBiConsumer<String, String, IOException> consumer = Spied.throwingBiConsumer(String::concat);

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingBiConsumer<String, String, ExecutionException> throwing = consumer.onErrorThrowAsChecked(errorMapper);

            throwing.accept("foo", "bar");

            verify(consumer).accept("foo", "bar");
            verify(consumer).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(consumer, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingBiConsumer<String, String, IOException> consumer = Spied.throwingBiConsumer((s1, s2) -> {
                throw new IOException(s1 + s2);
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingBiConsumer<String, String, ExecutionException> throwing = consumer.onErrorThrowAsChecked(errorMapper);

            ExecutionException thrown = assertThrows(ExecutionException.class, () -> throwing.accept("foo", "bar"));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foobar", cause.getMessage());

            verify(consumer).accept("foo", "bar");
            verify(consumer).onErrorThrowAsChecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(consumer, errorMapper);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingBiConsumer<String, String, IOException> consumer = Spied.throwingBiConsumer((s1, s2) -> throwable.throwUnchecked(s1 + s2));

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingBiConsumer<String, String, ExecutionException> throwing = consumer.onErrorThrowAsChecked(errorMapper);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> throwing.accept("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());

            verify(consumer).accept("foo", "bar");
            verify(consumer).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(consumer, errorMapper);
        }
    }

    @Nested
    class OnErrorThrowAsUnchecked {

        @Test
        void testNullArgument() {
            ThrowingBiConsumer<String, String, IOException> consumer = Spied.throwingBiConsumer(String::concat);

            assertThrows(NullPointerException.class, () -> consumer.onErrorThrowAsUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingBiConsumer<String, String, IOException> consumer = Spied.throwingBiConsumer(String::concat);

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            BiConsumer<String, String> throwing = consumer.onErrorThrowAsUnchecked(errorMapper);

            throwing.accept("foo", "bar");

            verify(consumer).accept("foo", "bar");
            verify(consumer).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(consumer, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingBiConsumer<String, String, IOException> consumer = Spied.throwingBiConsumer((s1, s2) -> {
                throw new IOException(s1 + s2);
            });

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            BiConsumer<String, String> throwing = consumer.onErrorThrowAsUnchecked(errorMapper);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> throwing.accept("foo", "bar"));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foobar", cause.getMessage());

            verify(consumer).accept("foo", "bar");
            verify(consumer).onErrorThrowAsUnchecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(consumer, errorMapper);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingBiConsumer<String, String, IOException> consumer = Spied.throwingBiConsumer((s1, s2) -> throwable.throwUnchecked(s1 + s2));

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            BiConsumer<String, String> throwing = consumer.onErrorThrowAsUnchecked(errorMapper);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> throwing.accept("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());

            verify(consumer).accept("foo", "bar");
            verify(consumer).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(consumer, errorMapper);
        }
    }

    @Nested
    class OnErrorHandleChecked {

        @Test
        void testNullArgument() {
            ThrowingBiConsumer<String, String, IOException> consumer = Spied.throwingBiConsumer(String::concat);

            assertThrows(NullPointerException.class, () -> consumer.onErrorHandleChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingBiConsumer<String, String, IOException> consumer = Spied.throwingBiConsumer(String::concat);

            ThrowingConsumer<IOException, ExecutionException> errorHandler = Spied.throwingConsumer(Exception::getMessage);

            ThrowingBiConsumer<String, String, ExecutionException> handling = consumer.onErrorHandleChecked(errorHandler);

            handling.accept("foo", "bar");

            verify(consumer).accept("foo", "bar");
            verify(consumer).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(consumer, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException, ExecutionException {
                ThrowingBiConsumer<String, String, IOException> consumer = Spied.throwingBiConsumer((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                ThrowingConsumer<IOException, ExecutionException> errorHandler = Spied.throwingConsumer(Exception::getMessage);

                ThrowingBiConsumer<String, String, ExecutionException> handling = consumer.onErrorHandleChecked(errorHandler);

                handling.accept("foo", "bar");

                verify(consumer).accept("foo", "bar");
                verify(consumer).onErrorHandleChecked(errorHandler);
                verify(errorHandler).accept(any());
                verifyNoMoreInteractions(consumer, errorHandler);
            }

            @Test
            void testHandlerThrowsChecked() throws IOException, ExecutionException {
                ThrowingBiConsumer<String, String, IOException> consumer = Spied.throwingBiConsumer((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                ThrowingConsumer<IOException, ExecutionException> errorHandler = Spied.throwingConsumer(e -> {
                    throw new ExecutionException(e);
                });

                ThrowingBiConsumer<String, String, ExecutionException> handling = consumer.onErrorHandleChecked(errorHandler);

                ExecutionException thrown = assertThrows(ExecutionException.class, () -> handling.accept("foo", "bar"));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foobar", cause.getMessage());

                verify(consumer).accept("foo", "bar");
                verify(consumer).onErrorHandleChecked(errorHandler);
                verify(errorHandler).accept(any());
                verifyNoMoreInteractions(consumer, errorHandler);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testHandlerThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ExecutionException {
                ThrowingBiConsumer<String, String, IOException> consumer = Spied.throwingBiConsumer((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                ThrowingConsumer<IOException, ExecutionException> errorHandler = Spied.throwingConsumer(throwable::throwUnchecked);

                ThrowingBiConsumer<String, String, ExecutionException> handling = consumer.onErrorHandleChecked(errorHandler);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.accept("foo", "bar"));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foobar", cause.getMessage());

                verify(consumer).accept("foo", "bar");
                verify(consumer).onErrorHandleChecked(errorHandler);
                verify(errorHandler).accept(any());
                verifyNoMoreInteractions(consumer, errorHandler);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingBiConsumer<String, String, IOException> consumer = Spied.throwingBiConsumer((s1, s2) -> throwable.throwUnchecked(s1 + s2));

            ThrowingConsumer<IOException, ExecutionException> errorHandler = Spied.throwingConsumer(Exception::getMessage);

            ThrowingBiConsumer<String, String, ExecutionException> handling = consumer.onErrorHandleChecked(errorHandler);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.accept("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());

            verify(consumer).accept("foo", "bar");
            verify(consumer).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(consumer, errorHandler);
        }
    }

    @Nested
    class OnErrorHandleUnchecked {

        @Test
        void testNullArgument() {
            ThrowingBiConsumer<String, String, IOException> consumer = Spied.throwingBiConsumer(String::concat);

            assertThrows(NullPointerException.class, () -> consumer.onErrorHandleUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingBiConsumer<String, String, IOException> consumer = Spied.throwingBiConsumer(String::concat);

            Consumer<IOException> errorHandler = Spied.consumer(Exception::getMessage);

            BiConsumer<String, String> handling = consumer.onErrorHandleUnchecked(errorHandler);

            handling.accept("foo", "bar");

            verify(consumer).accept("foo", "bar");
            verify(consumer).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(consumer, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException {
                ThrowingBiConsumer<String, String, IOException> consumer = Spied.throwingBiConsumer((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                Consumer<IOException> errorHandler = Spied.consumer(Exception::getMessage);

                BiConsumer<String, String> handling = consumer.onErrorHandleUnchecked(errorHandler);

                handling.accept("foo", "bar");

                verify(consumer).accept("foo", "bar");
                verify(consumer).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).accept(any());
                verifyNoMoreInteractions(consumer, errorHandler);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testHandlerThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingBiConsumer<String, String, IOException> consumer = Spied.throwingBiConsumer((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                Consumer<IOException> errorHandler = Spied.consumer(throwable::throwUnchecked);

                BiConsumer<String, String> handling = consumer.onErrorHandleUnchecked(errorHandler);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.accept("foo", "bar"));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foobar", cause.getMessage());

                verify(consumer).accept("foo", "bar");
                verify(consumer).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).accept(any());
                verifyNoMoreInteractions(consumer, errorHandler);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingBiConsumer<String, String, IOException> consumer = Spied.throwingBiConsumer((s1, s2) -> throwable.throwUnchecked(s1 + s2));

            Consumer<IOException> errorHandler = Spied.consumer(Exception::getMessage);

            BiConsumer<String, String> handling = consumer.onErrorHandleUnchecked(errorHandler);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.accept("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());

            verify(consumer).accept("foo", "bar");
            verify(consumer).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(consumer, errorHandler);
        }
    }

    @Nested
    class OnErrorAcceptChecked {

        @Test
        void testNullArgument() {
            ThrowingBiConsumer<String, String, IOException> consumer = Spied.throwingBiConsumer(String::concat);

            assertThrows(NullPointerException.class, () -> consumer.onErrorAcceptChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingBiConsumer<String, String, IOException> consumer = Spied.throwingBiConsumer(String::concat);

            ThrowingBiConsumer<String, String, ExecutionException> fallback = Spied.throwingBiConsumer(Objects::equals);

            ThrowingBiConsumer<String, String, ExecutionException> applying = consumer.onErrorAcceptChecked(fallback);

            applying.accept("foo", "bar");

            verify(consumer).accept("foo", "bar");
            verify(consumer).onErrorAcceptChecked(fallback);
            verifyNoMoreInteractions(consumer, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                ThrowingBiConsumer<String, String, IOException> consumer = Spied.throwingBiConsumer((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                ThrowingBiConsumer<String, String, ParseException> fallback = Spied.throwingBiConsumer(Objects::equals);

                ThrowingBiConsumer<String, String, ParseException> applying = consumer.onErrorAcceptChecked(fallback);

                applying.accept("foo", "bar");

                verify(consumer).accept("foo", "bar");
                verify(consumer).onErrorAcceptChecked(fallback);
                verify(fallback).accept("foo", "bar");
                verifyNoMoreInteractions(consumer, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                ThrowingBiConsumer<String, String, IOException> consumer = Spied.throwingBiConsumer((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                ThrowingBiConsumer<String, String, ParseException> fallback = Spied.throwingBiConsumer((s1, s2) -> {
                    throw new ParseException(s1 + s2, 0);
                });

                ThrowingBiConsumer<String, String, ParseException> applying = consumer.onErrorAcceptChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> applying.accept("foo", "bar"));
                assertEquals("foobar", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(consumer).accept("foo", "bar");
                verify(consumer).onErrorAcceptChecked(fallback);
                verify(fallback).accept("foo", "bar");
                verifyNoMoreInteractions(consumer, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ParseException {
                ThrowingBiConsumer<String, String, IOException> consumer = Spied.throwingBiConsumer((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                ThrowingBiConsumer<String, String, ParseException> fallback = Spied.throwingBiConsumer((s1, s2) -> throwable.throwUnchecked(s1 + s2));

                ThrowingBiConsumer<String, String, ParseException> applying = consumer.onErrorAcceptChecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.accept("foo", "bar"));
                assertEquals("foobar", thrown.getMessage());

                verify(consumer).accept("foo", "bar");
                verify(consumer).onErrorAcceptChecked(fallback);
                verify(fallback).accept("foo", "bar");
                verifyNoMoreInteractions(consumer, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingBiConsumer<String, String, IOException> consumer = Spied.throwingBiConsumer((s1, s2) -> throwable.throwUnchecked(s1 + s2));

            ThrowingBiConsumer<String, String, ParseException> fallback = Spied.throwingBiConsumer(Objects::equals);

            ThrowingBiConsumer<String, String, ParseException> applying = consumer.onErrorAcceptChecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.accept("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());

            verify(consumer).accept("foo", "bar");
            verify(consumer).onErrorAcceptChecked(fallback);
            verifyNoMoreInteractions(consumer, fallback);
        }
    }

    @Nested
    class OnErrorAcceptUnchecked {

        @Test
        void testNullArgument() {
            ThrowingBiConsumer<String, String, IOException> consumer = Spied.throwingBiConsumer(String::concat);

            assertThrows(NullPointerException.class, () -> consumer.onErrorAcceptUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingBiConsumer<String, String, IOException> consumer = Spied.throwingBiConsumer(String::concat);

            BiConsumer<String, String> fallback = Spied.biConsumer(Objects::equals);

            BiConsumer<String, String> applying = consumer.onErrorAcceptUnchecked(fallback);

            applying.accept("foo", "bar");

            verify(consumer).accept("foo", "bar");
            verify(consumer).onErrorAcceptUnchecked(fallback);
            verifyNoMoreInteractions(consumer, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                ThrowingBiConsumer<String, String, IOException> consumer = Spied.throwingBiConsumer((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                BiConsumer<String, String> fallback = Spied.biConsumer(Objects::equals);

                BiConsumer<String, String> applying = consumer.onErrorAcceptUnchecked(fallback);

                applying.accept("foo", "bar");

                verify(consumer).accept("foo", "bar");
                verify(consumer).onErrorAcceptUnchecked(fallback);
                verify(fallback).accept("foo", "bar");
                verifyNoMoreInteractions(consumer, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingBiConsumer<String, String, IOException> consumer = Spied.throwingBiConsumer((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                BiConsumer<String, String> fallback = Spied.biConsumer((s1, s2) -> throwable.throwUnchecked(s1 + s2));

                BiConsumer<String, String> applying = consumer.onErrorAcceptUnchecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.accept("foo", "bar"));
                assertEquals("foobar", thrown.getMessage());

                verify(consumer).accept("foo", "bar");
                verify(consumer).onErrorAcceptUnchecked(fallback);
                verify(fallback).accept("foo", "bar");
                verifyNoMoreInteractions(consumer, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingBiConsumer<String, String, IOException> consumer = Spied.throwingBiConsumer((s1, s2) -> throwable.throwUnchecked(s1 + s2));

            BiConsumer<String, String> fallback = Spied.biConsumer(Objects::equals);

            BiConsumer<String, String> applying = consumer.onErrorAcceptUnchecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.accept("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());

            verify(consumer).accept("foo", "bar");
            verify(consumer).onErrorAcceptUnchecked(fallback);
            verifyNoMoreInteractions(consumer, fallback);
        }
    }

    @Nested
    class OnErrorDiscard {

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingBiConsumer<String, String, IOException> consumer = Spied.throwingBiConsumer(String::concat);

            BiConsumer<String, String> discarding = consumer.onErrorDiscard();

            discarding.accept("foo", "bar");

            verify(consumer).accept("foo", "bar");
            verify(consumer).onErrorDiscard();
            verifyNoMoreInteractions(consumer);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingBiConsumer<String, String, IOException> consumer = Spied.throwingBiConsumer((s1, s2) -> {
                throw new IOException(s1 + s2);
            });

            BiConsumer<String, String> discarding = consumer.onErrorDiscard();

            discarding.accept("foo", "bar");

            verify(consumer).accept("foo", "bar");
            verify(consumer).onErrorDiscard();
            verifyNoMoreInteractions(consumer);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingBiConsumer<String, String, IOException> consumer = Spied.throwingBiConsumer((s1, s2) -> throwable.throwUnchecked(s1 + s2));

            BiConsumer<String, String> discarding = consumer.onErrorDiscard();

            Throwable thrown = assertThrows(throwable.throwableType(), () -> discarding.accept("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());

            verify(consumer).accept("foo", "bar");
            verify(consumer).onErrorDiscard();
            verifyNoMoreInteractions(consumer);
        }
    }

    @Nested
    class Unchecked {

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingBiConsumer<String, String, IOException> consumer = Spied.throwingBiConsumer(String::concat);

            BiConsumer<String, String> unchecked = consumer.unchecked();

            unchecked.accept("foo", "bar");

            verify(consumer).accept("foo", "bar");
            verify(consumer).unchecked();
            verify(consumer).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(consumer);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingBiConsumer<String, String, IOException> consumer = Spied.throwingBiConsumer((s1, s2) -> {
                throw new IOException(s1 + s2);
            });

            BiConsumer<String, String> unchecked = consumer.unchecked();

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.accept("foo", "bar"));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foobar", cause.getMessage());

            verify(consumer).accept("foo", "bar");
            verify(consumer).unchecked();
            verify(consumer).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(consumer);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingBiConsumer<String, String, IOException> consumer = Spied.throwingBiConsumer((s1, s2) -> throwable.throwUnchecked(s1 + s2));

            BiConsumer<String, String> unchecked = consumer.unchecked();

            Throwable thrown = assertThrows(throwable.throwableType(), () -> unchecked.accept("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());

            verify(consumer).accept("foo", "bar");
            verify(consumer).unchecked();
            verify(consumer).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(consumer);
        }
    }

    @Nested
    class Of {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> ThrowingBiConsumer.of(null));
        }

        @Test
        void testNonNullArgument() throws IOException {
            ThrowingBiConsumer<String, String, IOException> delegate = Spied.throwingBiConsumer(String::concat);

            ThrowingBiConsumer<String, String, IOException> consumer = ThrowingBiConsumer.of(delegate);

            consumer.accept("foo", "bar");

            verify(delegate).accept("foo", "bar");
            verifyNoMoreInteractions(delegate);
        }
    }

    @Nested
    class UncheckedFactory {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> ThrowingBiConsumer.unchecked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ThrowingBiConsumer<String, String, IOException> consumer = Spied.throwingBiConsumer(String::concat);

            BiConsumer<String, String> unchecked = ThrowingBiConsumer.unchecked(consumer);

            unchecked.accept("foo", "bar");

            verify(consumer).unchecked();
            verify(consumer).onErrorThrowAsUnchecked(any());
            verify(consumer).accept("foo", "bar");
            verifyNoMoreInteractions(consumer);
        }

        @Test
        void testArgumentThrowsChecked() throws IOException {
            ThrowingBiConsumer<String, String, IOException> consumer = Spied.throwingBiConsumer((s1, s2) -> {
                throw new IOException(s1 + s2);
            });

            BiConsumer<String, String> unchecked = ThrowingBiConsumer.unchecked(consumer);

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.accept("foo", "bar"));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foobar", cause.getMessage());

            verify(consumer).unchecked();
            verify(consumer).onErrorThrowAsUnchecked(any());
            verify(consumer).accept("foo", "bar");
            verifyNoMoreInteractions(consumer);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testArgumentThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingBiConsumer<String, String, IOException> consumer = Spied.throwingBiConsumer((s1, s2) -> throwable.throwUnchecked(s1 + s2));

            BiConsumer<String, String> unchecked = ThrowingBiConsumer.unchecked(consumer);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> unchecked.accept("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());

            verify(consumer).unchecked();
            verify(consumer).onErrorThrowAsUnchecked(any());
            verify(consumer).accept("foo", "bar");
            verifyNoMoreInteractions(consumer);
        }
    }

    @Nested
    class CheckedWrapper {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> ThrowingBiConsumer.checked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            BiConsumer<String, String> delegate = Spied.biConsumer(String::concat);

            ThrowingBiConsumer<String, String, IOException> consumer = ThrowingBiConsumer.checked(delegate);

            consumer.accept("foo", "bar");

            verify(delegate).accept("foo", "bar");
            verifyNoMoreInteractions(delegate);
        }

        @Test
        void testArgumentThrowsUnchecked() {
            ThrowingBiConsumer<String, String, IOException> consumer = ThrowingBiConsumer.checked((s1, s2) -> {
                throw new UncheckedException(s1 + s2, new IOException());
            });

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> consumer.accept("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertNull(cause.getMessage());
        }
    }

    @Nested
    class CheckedUnwrapper {

        @Test
        void testNullArguments() {
            BiConsumer<String, String> consumer = String::concat;

            assertThrows(NullPointerException.class, () -> ThrowingBiConsumer.checked(null, IOException.class));
            assertThrows(NullPointerException.class, () -> ThrowingBiConsumer.checked(consumer, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            BiConsumer<String, String> delegate = Spied.biConsumer(String::concat);

            ThrowingBiConsumer<String, String, IOException> consumer = ThrowingBiConsumer.checked(delegate, IOException.class);

            consumer.accept("foo", "bar");

            verify(delegate).accept("foo", "bar");
            verifyNoMoreInteractions(delegate);
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                ThrowingBiConsumer<String, String, IOException> consumer = ThrowingBiConsumer.checked((s1, s2) -> {
                    throw new UncheckedException(new IOException(s1 + s2));
                }, IOException.class);

                IOException thrown = assertThrows(IOException.class, () -> consumer.accept("foo", "bar"));
                assertEquals("foobar", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                ThrowingBiConsumer<String, String, IOException> consumer = ThrowingBiConsumer.checked((s1, s2) -> {
                    throw new UncheckedException(new FileNotFoundException(s1 + s2));
                }, IOException.class);

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class, () -> consumer.accept("foo", "bar"));
                assertEquals("foobar", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                ThrowingBiConsumer<String, String, IOException> consumer = ThrowingBiConsumer.checked((s1, s2) -> {
                    throw new UncheckedException(new ParseException(s1 + s2, 0));
                }, IOException.class);

                UncheckedException thrown = assertThrows(UncheckedException.class, () -> consumer.accept("foo", "bar"));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("foobar", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            ThrowingBiConsumer<String, String, IOException> consumer = ThrowingBiConsumer.checked((s1, s2) -> {
                throw new IllegalStateException(s1 + s2);
            }, IOException.class);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> consumer.accept("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());
        }
    }

    @Nested
    class InvokeAndUnwrap {

        @Test
        void testNullArguments() throws IOException {
            BiConsumer<String, String> consumer = Spied.biConsumer(Objects::equals);

            assertThrows(NullPointerException.class, () -> ThrowingBiConsumer.invokeAndUnwrap(null, "foo", "bar", IOException.class));
            assertThrows(NullPointerException.class, () -> ThrowingBiConsumer.invokeAndUnwrap(consumer, "foo", "bar", null));

            ThrowingBiConsumer.invokeAndUnwrap(consumer, null, "bar", IOException.class);
            ThrowingBiConsumer.invokeAndUnwrap(consumer, "foo", null, IOException.class);

            verify(consumer).accept(null, "bar");
            verify(consumer).accept("foo", null);
            verifyNoMoreInteractions(consumer);
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            BiConsumer<String, String> consumer = Spied.biConsumer(String::concat);

            ThrowingBiConsumer.invokeAndUnwrap(consumer, "foo", "bar", IOException.class);

            verify(consumer).accept("foo", "bar");
            verifyNoMoreInteractions(consumer);
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                BiConsumer<String, String> consumer = (s1, s2) -> {
                    throw new UncheckedException(new IOException(s1 + s2));
                };

                IOException thrown = assertThrows(IOException.class,
                        () -> ThrowingBiConsumer.invokeAndUnwrap(consumer, "foo", "bar", IOException.class));
                assertEquals("foobar", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                BiConsumer<String, String> consumer = (s1, s2) -> {
                    throw new UncheckedException(new FileNotFoundException(s1 + s2));
                };

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class,
                        () -> ThrowingBiConsumer.invokeAndUnwrap(consumer, "foo", "bar", IOException.class));
                assertEquals("foobar", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                BiConsumer<String, String> consumer = (s1, s2) -> {
                    throw new UncheckedException(new ParseException(s1 + s2, 0));
                };

                UncheckedException thrown = assertThrows(UncheckedException.class,
                        () -> ThrowingBiConsumer.invokeAndUnwrap(consumer, "foo", "bar", IOException.class));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("foobar", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            BiConsumer<String, String> consumer = (s1, s2) -> {
                throw new IllegalStateException(s1 + s2);
            };

            IllegalStateException thrown = assertThrows(IllegalStateException.class,
                    () -> ThrowingBiConsumer.invokeAndUnwrap(consumer, "foo", "bar", IOException.class));
            assertEquals("foobar", thrown.getMessage());
        }
    }
}
