/*
 * ThrowingConsumerTest.java
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
import java.util.function.Consumer;
import java.util.function.Function;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

@SuppressWarnings("nls")
class ThrowingConsumerTest {

    @Nested
    class AndThen {

        @Test
        void testNullArgument() {
            ThrowingConsumer<String, IOException> consumer = Spied.throwingConsumer(String::toUpperCase);

            assertThrows(NullPointerException.class, () -> consumer.andThen(null));
        }

        @Test
        void testNeitherThrows() throws IOException {
            ThrowingConsumer<String, IOException> consumer = Spied.throwingConsumer(String::toUpperCase);
            ThrowingConsumer<String, IOException> after = Spied.throwingConsumer(String::toLowerCase);

            ThrowingConsumer<String, IOException> composed = consumer.andThen(after);

            composed.accept("foo");

            verify(consumer).accept("foo");
            verify(consumer).andThen(after);
            verify(after).accept("foo");
            verifyNoMoreInteractions(consumer, after);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingConsumer<String, IOException> consumer = Spied.throwingConsumer(s -> {
                throw new IOException(s);
            });
            ThrowingConsumer<String, IOException> after = Spied.throwingConsumer(String::toLowerCase);

            ThrowingConsumer<String, IOException> composed = consumer.andThen(after);

            IOException thrown = assertThrows(IOException.class, () -> composed.accept("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(consumer).accept("foo");
            verify(consumer).andThen(after);
            verifyNoMoreInteractions(consumer, after);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingConsumer<String, IOException> consumer = Spied.throwingConsumer(throwable::throwUnchecked);
            ThrowingConsumer<String, IOException> after = Spied.throwingConsumer(String::toLowerCase);

            ThrowingConsumer<String, IOException> composed = consumer.andThen(after);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> composed.accept("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(consumer).accept("foo");
            verify(consumer).andThen(after);
            verifyNoMoreInteractions(consumer, after);
        }

        @Test
        void testAfterThrowsChecked() throws IOException {
            ThrowingConsumer<String, IOException> consumer = Spied.throwingConsumer(String::toUpperCase);
            ThrowingConsumer<String, IOException> after = Spied.throwingConsumer(s -> {
                throw new IOException(s);
            });

            ThrowingConsumer<String, IOException> composed = consumer.andThen(after);

            IOException thrown = assertThrows(IOException.class, () -> composed.accept("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(consumer).accept("foo");
            verify(consumer).andThen(after);
            verify(after).accept("foo");
            verifyNoMoreInteractions(consumer, after);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testAfterThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingConsumer<String, IOException> consumer = Spied.throwingConsumer(String::toUpperCase);
            ThrowingConsumer<String, IOException> after = Spied.throwingConsumer(throwable::throwUnchecked);

            ThrowingConsumer<String, IOException> composed = consumer.andThen(after);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> composed.accept("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(consumer).accept("foo");
            verify(consumer).andThen(after);
            verify(after).accept("foo");
            verifyNoMoreInteractions(consumer, after);
        }
    }

    @Nested
    class OnErrorThrowAsChecked {

        @Test
        void testNullArgument() {
            ThrowingConsumer<String, IOException> consumer = Spied.throwingConsumer(String::toUpperCase);

            assertThrows(NullPointerException.class, () -> consumer.onErrorThrowAsChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingConsumer<String, IOException> consumer = Spied.throwingConsumer(String::toUpperCase);

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingConsumer<String, ExecutionException> throwing = consumer.onErrorThrowAsChecked(errorMapper);

            throwing.accept("foo");

            verify(consumer).accept("foo");
            verify(consumer).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(consumer, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingConsumer<String, IOException> consumer = Spied.throwingConsumer(s -> {
                throw new IOException(s);
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingConsumer<String, ExecutionException> throwing = consumer.onErrorThrowAsChecked(errorMapper);

            ExecutionException thrown = assertThrows(ExecutionException.class, () -> throwing.accept("foo"));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo", cause.getMessage());

            verify(consumer).accept("foo");
            verify(consumer).onErrorThrowAsChecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(consumer, errorMapper);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingConsumer<String, IOException> consumer = Spied.throwingConsumer(throwable::throwUnchecked);

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingConsumer<String, ExecutionException> throwing = consumer.onErrorThrowAsChecked(errorMapper);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> throwing.accept("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(consumer).accept("foo");
            verify(consumer).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(consumer, errorMapper);
        }
    }

    @Nested
    class OnErrorThrowAsUnchecked {

        @Test
        void testNullArgument() {
            ThrowingConsumer<String, IOException> consumer = Spied.throwingConsumer(String::toUpperCase);

            assertThrows(NullPointerException.class, () -> consumer.onErrorThrowAsUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingConsumer<String, IOException> consumer = Spied.throwingConsumer(String::toUpperCase);

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            Consumer<String> throwing = consumer.onErrorThrowAsUnchecked(errorMapper);

            throwing.accept("foo");

            verify(consumer).accept("foo");
            verify(consumer).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(consumer, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingConsumer<String, IOException> consumer = Spied.throwingConsumer(s -> {
                throw new IOException(s);
            });

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            Consumer<String> throwing = consumer.onErrorThrowAsUnchecked(errorMapper);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> throwing.accept("foo"));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo", cause.getMessage());

            verify(consumer).accept("foo");
            verify(consumer).onErrorThrowAsUnchecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(consumer, errorMapper);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingConsumer<String, IOException> consumer = Spied.throwingConsumer(throwable::throwUnchecked);

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            Consumer<String> throwing = consumer.onErrorThrowAsUnchecked(errorMapper);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> throwing.accept("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(consumer).accept("foo");
            verify(consumer).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(consumer, errorMapper);
        }
    }

    @Nested
    class OnErrorHandleChecked {

        @Test
        void testNullArgument() {
            ThrowingConsumer<String, IOException> consumer = Spied.throwingConsumer(String::toUpperCase);

            assertThrows(NullPointerException.class, () -> consumer.onErrorHandleChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingConsumer<String, IOException> consumer = Spied.throwingConsumer(String::toUpperCase);

            ThrowingConsumer<IOException, ExecutionException> errorHandler = Spied.throwingConsumer(Exception::getMessage);

            ThrowingConsumer<String, ExecutionException> handling = consumer.onErrorHandleChecked(errorHandler);

            handling.accept("foo");

            verify(consumer).accept("foo");
            verify(consumer).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(consumer, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException, ExecutionException {
                ThrowingConsumer<String, IOException> consumer = Spied.throwingConsumer(s -> {
                    throw new IOException(s);
                });

                ThrowingConsumer<IOException, ExecutionException> errorHandler = Spied.throwingConsumer(Exception::getMessage);

                ThrowingConsumer<String, ExecutionException> handling = consumer.onErrorHandleChecked(errorHandler);

                handling.accept("foo");

                verify(consumer).accept("foo");
                verify(consumer).onErrorHandleChecked(errorHandler);
                verify(errorHandler).accept(any());
                verifyNoMoreInteractions(consumer, errorHandler);
            }

            @Test
            void testHandlerThrowsChecked() throws IOException, ExecutionException {
                ThrowingConsumer<String, IOException> consumer = Spied.throwingConsumer(s -> {
                    throw new IOException(s);
                });

                ThrowingConsumer<IOException, ExecutionException> errorHandler = Spied.throwingConsumer(e -> {
                    throw new ExecutionException(e);
                });

                ThrowingConsumer<String, ExecutionException> handling = consumer.onErrorHandleChecked(errorHandler);

                ExecutionException thrown = assertThrows(ExecutionException.class, () -> handling.accept("foo"));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());

                verify(consumer).accept("foo");
                verify(consumer).onErrorHandleChecked(errorHandler);
                verify(errorHandler).accept(any());
                verifyNoMoreInteractions(consumer, errorHandler);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testHandlerThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ExecutionException {
                ThrowingConsumer<String, IOException> consumer = Spied.throwingConsumer(s -> {
                    throw new IOException(s);
                });

                ThrowingConsumer<IOException, ExecutionException> errorHandler = Spied.throwingConsumer(throwable::throwUnchecked);

                ThrowingConsumer<String, ExecutionException> handling = consumer.onErrorHandleChecked(errorHandler);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.accept("foo"));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());

                verify(consumer).accept("foo");
                verify(consumer).onErrorHandleChecked(errorHandler);
                verify(errorHandler).accept(any());
                verifyNoMoreInteractions(consumer, errorHandler);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingConsumer<String, IOException> consumer = Spied.throwingConsumer(throwable::throwUnchecked);

            ThrowingConsumer<IOException, ExecutionException> errorHandler = Spied.throwingConsumer(Exception::getMessage);

            ThrowingConsumer<String, ExecutionException> handling = consumer.onErrorHandleChecked(errorHandler);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.accept("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(consumer).accept("foo");
            verify(consumer).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(consumer, errorHandler);
        }
    }

    @Nested
    class OnErrorHandleUnchecked {

        @Test
        void testNullArgument() {
            ThrowingConsumer<String, IOException> consumer = Spied.throwingConsumer(String::toUpperCase);

            assertThrows(NullPointerException.class, () -> consumer.onErrorHandleUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingConsumer<String, IOException> consumer = Spied.throwingConsumer(String::toUpperCase);

            Consumer<IOException> errorHandler = Spied.consumer(Exception::getMessage);

            Consumer<String> handling = consumer.onErrorHandleUnchecked(errorHandler);

            handling.accept("foo");

            verify(consumer).accept("foo");
            verify(consumer).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(consumer, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException {
                ThrowingConsumer<String, IOException> consumer = Spied.throwingConsumer(s -> {
                    throw new IOException(s);
                });

                Consumer<IOException> errorHandler = Spied.consumer(Exception::getMessage);

                Consumer<String> handling = consumer.onErrorHandleUnchecked(errorHandler);

                handling.accept("foo");

                verify(consumer).accept("foo");
                verify(consumer).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).accept(any());
                verifyNoMoreInteractions(consumer, errorHandler);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testHandlerThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingConsumer<String, IOException> consumer = Spied.throwingConsumer(s -> {
                    throw new IOException(s);
                });

                Consumer<IOException> errorHandler = Spied.consumer(throwable::throwUnchecked);

                Consumer<String> handling = consumer.onErrorHandleUnchecked(errorHandler);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.accept("foo"));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());

                verify(consumer).accept("foo");
                verify(consumer).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).accept(any());
                verifyNoMoreInteractions(consumer, errorHandler);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingConsumer<String, IOException> consumer = Spied.throwingConsumer(throwable::throwUnchecked);

            Consumer<IOException> errorHandler = Spied.consumer(Exception::getMessage);

            Consumer<String> handling = consumer.onErrorHandleUnchecked(errorHandler);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.accept("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(consumer).accept("foo");
            verify(consumer).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(consumer, errorHandler);
        }
    }

    @Nested
    class OnErrorAcceptChecked {

        @Test
        void testNullArgument() {
            ThrowingConsumer<String, IOException> consumer = Spied.throwingConsumer(String::toUpperCase);

            assertThrows(NullPointerException.class, () -> consumer.onErrorAcceptChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingConsumer<String, IOException> consumer = Spied.throwingConsumer(String::toUpperCase);

            ThrowingConsumer<String, ExecutionException> fallback = Spied.throwingConsumer(String::toLowerCase);

            ThrowingConsumer<String, ExecutionException> applying = consumer.onErrorAcceptChecked(fallback);

            applying.accept("foo");

            verify(consumer).accept("foo");
            verify(consumer).onErrorAcceptChecked(fallback);
            verifyNoMoreInteractions(consumer, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                ThrowingConsumer<String, IOException> consumer = Spied.throwingConsumer(s -> {
                    throw new IOException(s);
                });

                ThrowingConsumer<String, ParseException> fallback = Spied.throwingConsumer(String::toLowerCase);

                ThrowingConsumer<String, ParseException> applying = consumer.onErrorAcceptChecked(fallback);

                applying.accept("foo");

                verify(consumer).accept("foo");
                verify(consumer).onErrorAcceptChecked(fallback);
                verify(fallback).accept("foo");
                verifyNoMoreInteractions(consumer, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                ThrowingConsumer<String, IOException> consumer = Spied.throwingConsumer(s -> {
                    throw new IOException(s);
                });

                ThrowingConsumer<String, ParseException> fallback = Spied.throwingConsumer(s -> {
                    throw new ParseException(s, 0);
                });

                ThrowingConsumer<String, ParseException> applying = consumer.onErrorAcceptChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> applying.accept("foo"));
                assertEquals("foo", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(consumer).accept("foo");
                verify(consumer).onErrorAcceptChecked(fallback);
                verify(fallback).accept("foo");
                verifyNoMoreInteractions(consumer, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ParseException {
                ThrowingConsumer<String, IOException> consumer = Spied.throwingConsumer(s -> {
                    throw new IOException(s);
                });

                ThrowingConsumer<String, ParseException> fallback = Spied.throwingConsumer(throwable::throwUnchecked);

                ThrowingConsumer<String, ParseException> applying = consumer.onErrorAcceptChecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.accept("foo"));
                assertEquals("foo", thrown.getMessage());

                verify(consumer).accept("foo");
                verify(consumer).onErrorAcceptChecked(fallback);
                verify(fallback).accept("foo");
                verifyNoMoreInteractions(consumer, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingConsumer<String, IOException> consumer = Spied.throwingConsumer(throwable::throwUnchecked);

            ThrowingConsumer<String, ParseException> fallback = Spied.throwingConsumer(String::toLowerCase);

            ThrowingConsumer<String, ParseException> applying = consumer.onErrorAcceptChecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.accept("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(consumer).accept("foo");
            verify(consumer).onErrorAcceptChecked(fallback);
            verifyNoMoreInteractions(consumer, fallback);
        }
    }

    @Nested
    class OnErrorAcceptUnchecked {

        @Test
        void testNullArgument() {
            ThrowingConsumer<String, IOException> consumer = Spied.throwingConsumer(String::toUpperCase);

            assertThrows(NullPointerException.class, () -> consumer.onErrorAcceptUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingConsumer<String, IOException> consumer = Spied.throwingConsumer(String::toUpperCase);

            Consumer<String> fallback = Spied.consumer(String::toLowerCase);

            Consumer<String> applying = consumer.onErrorAcceptUnchecked(fallback);

            applying.accept("foo");

            verify(consumer).accept("foo");
            verify(consumer).onErrorAcceptUnchecked(fallback);
            verifyNoMoreInteractions(consumer, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                ThrowingConsumer<String, IOException> consumer = Spied.throwingConsumer(s -> {
                    throw new IOException(s);
                });

                Consumer<String> fallback = Spied.consumer(String::toLowerCase);

                Consumer<String> applying = consumer.onErrorAcceptUnchecked(fallback);

                applying.accept("foo");

                verify(consumer).accept("foo");
                verify(consumer).onErrorAcceptUnchecked(fallback);
                verify(fallback).accept("foo");
                verifyNoMoreInteractions(consumer, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingConsumer<String, IOException> consumer = Spied.throwingConsumer(s -> {
                    throw new IOException(s);
                });

                Consumer<String> fallback = Spied.consumer(throwable::throwUnchecked);

                Consumer<String> applying = consumer.onErrorAcceptUnchecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.accept("foo"));
                assertEquals("foo", thrown.getMessage());

                verify(consumer).accept("foo");
                verify(consumer).onErrorAcceptUnchecked(fallback);
                verify(fallback).accept("foo");
                verifyNoMoreInteractions(consumer, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingConsumer<String, IOException> consumer = Spied.throwingConsumer(throwable::throwUnchecked);

            Consumer<String> fallback = Spied.consumer(String::toLowerCase);

            Consumer<String> applying = consumer.onErrorAcceptUnchecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.accept("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(consumer).accept("foo");
            verify(consumer).onErrorAcceptUnchecked(fallback);
            verifyNoMoreInteractions(consumer, fallback);
        }
    }

    @Nested
    class OnErrorDiscard {

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingConsumer<String, IOException> consumer = Spied.throwingConsumer(String::toUpperCase);

            Consumer<String> discarding = consumer.onErrorDiscard();

            discarding.accept("foo");

            verify(consumer).accept("foo");
            verify(consumer).onErrorDiscard();
            verifyNoMoreInteractions(consumer);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingConsumer<String, IOException> consumer = Spied.throwingConsumer(s -> {
                throw new IOException(s);
            });

            Consumer<String> discarding = consumer.onErrorDiscard();

            discarding.accept("foo");

            verify(consumer).accept("foo");
            verify(consumer).onErrorDiscard();
            verifyNoMoreInteractions(consumer);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingConsumer<String, IOException> consumer = Spied.throwingConsumer(throwable::throwUnchecked);

            Consumer<String> discarding = consumer.onErrorDiscard();

            Throwable thrown = assertThrows(throwable.throwableType(), () -> discarding.accept("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(consumer).accept("foo");
            verify(consumer).onErrorDiscard();
            verifyNoMoreInteractions(consumer);
        }
    }

    @Nested
    class Unchecked {

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingConsumer<String, IOException> consumer = Spied.throwingConsumer(String::toUpperCase);

            Consumer<String> unchecked = consumer.unchecked();

            unchecked.accept("foo");

            verify(consumer).accept("foo");
            verify(consumer).unchecked();
            verify(consumer).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(consumer);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingConsumer<String, IOException> consumer = Spied.throwingConsumer(s -> {
                throw new IOException(s);
            });

            Consumer<String> unchecked = consumer.unchecked();

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.accept("foo"));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo", cause.getMessage());

            verify(consumer).accept("foo");
            verify(consumer).unchecked();
            verify(consumer).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(consumer);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingConsumer<String, IOException> consumer = Spied.throwingConsumer(throwable::throwUnchecked);

            Consumer<String> unchecked = consumer.unchecked();

            Throwable thrown = assertThrows(throwable.throwableType(), () -> unchecked.accept("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(consumer).accept("foo");
            verify(consumer).unchecked();
            verify(consumer).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(consumer);
        }
    }

    @Nested
    class Of {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> ThrowingConsumer.of(null));
        }

        @Test
        void testNonNullArgument() throws IOException {
            ThrowingConsumer<String, IOException> delegate = Spied.throwingConsumer(String::toUpperCase);

            ThrowingConsumer<String, IOException> consumer = ThrowingConsumer.of(delegate);

            consumer.accept("foo");

            verify(delegate).accept("foo");
            verifyNoMoreInteractions(delegate);
        }
    }

    @Nested
    class UncheckedFactory {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> ThrowingConsumer.unchecked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ThrowingConsumer<String, IOException> consumer = Spied.throwingConsumer(String::toUpperCase);

            Consumer<String> unchecked = ThrowingConsumer.unchecked(consumer);

            unchecked.accept("foo");

            verify(consumer).unchecked();
            verify(consumer).onErrorThrowAsUnchecked(any());
            verify(consumer).accept("foo");
            verifyNoMoreInteractions(consumer);
        }

        @Test
        void testArgumentThrowsChecked() throws IOException {
            ThrowingConsumer<String, IOException> consumer = Spied.throwingConsumer(s -> {
                throw new IOException(s);
            });

            Consumer<String> unchecked = ThrowingConsumer.unchecked(consumer);

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.accept("foo"));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo", cause.getMessage());

            verify(consumer).unchecked();
            verify(consumer).onErrorThrowAsUnchecked(any());
            verify(consumer).accept("foo");
            verifyNoMoreInteractions(consumer);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testArgumentThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingConsumer<String, IOException> consumer = Spied.throwingConsumer(throwable::throwUnchecked);

            Consumer<String> unchecked = ThrowingConsumer.unchecked(consumer);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> unchecked.accept("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(consumer).unchecked();
            verify(consumer).onErrorThrowAsUnchecked(any());
            verify(consumer).accept("foo");
            verifyNoMoreInteractions(consumer);
        }
    }

    @Nested
    class CheckedWrapper {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> ThrowingConsumer.checked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            Consumer<String> delegate = Spied.consumer(String::toUpperCase);

            ThrowingConsumer<String, IOException> consumer = ThrowingConsumer.checked(delegate);

            consumer.accept("foo");

            verify(delegate).accept("foo");
            verifyNoMoreInteractions(delegate);
        }

        @Test
        void testArgumentThrowsUnchecked() {
            ThrowingConsumer<String, IOException> consumer = ThrowingConsumer.checked(s -> {
                throw UncheckedException.withoutStackTrace(s, new IOException());
            });

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> consumer.accept("foo"));
            assertEquals("foo", thrown.getMessage());
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertNull(cause.getMessage());
        }
    }

    @Nested
    class CheckedUnwrapper {

        @Test
        void testNullArguments() {
            Consumer<String> consumer = String::toUpperCase;

            assertThrows(NullPointerException.class, () -> ThrowingConsumer.checked(null, IOException.class));
            assertThrows(NullPointerException.class, () -> ThrowingConsumer.checked(consumer, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            Consumer<String> delegate = Spied.consumer(String::toUpperCase);

            ThrowingConsumer<String, IOException> consumer = ThrowingConsumer.checked(delegate, IOException.class);

            consumer.accept("foo");

            verify(delegate).accept("foo");
            verifyNoMoreInteractions(delegate);
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                ThrowingConsumer<String, IOException> consumer = ThrowingConsumer.checked(s -> {
                    throw UncheckedException.withoutStackTrace(new IOException(s));
                }, IOException.class);

                IOException thrown = assertThrows(IOException.class, () -> consumer.accept("foo"));
                assertEquals("foo", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                ThrowingConsumer<String, IOException> consumer = ThrowingConsumer.checked(s -> {
                    throw UncheckedException.withoutStackTrace(new FileNotFoundException(s));
                }, IOException.class);

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class, () -> consumer.accept("foo"));
                assertEquals("foo", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                ThrowingConsumer<String, IOException> consumer = ThrowingConsumer.checked(s -> {
                    throw UncheckedException.withoutStackTrace(new ParseException(s, 0));
                }, IOException.class);

                UncheckedException thrown = assertThrows(UncheckedException.class, () -> consumer.accept("foo"));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            ThrowingConsumer<String, IOException> consumer = ThrowingConsumer.checked(s -> {
                throw new IllegalStateException(s);
            }, IOException.class);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> consumer.accept("foo"));
            assertEquals("foo", thrown.getMessage());
        }
    }

    @Nested
    class InvokeAndUnwrap {

        @Test
        void testNullArguments() throws IOException {
            Consumer<String> consumer = Spied.consumer(String::valueOf);

            assertThrows(NullPointerException.class, () -> ThrowingConsumer.invokeAndUnwrap(null, "foo", IOException.class));
            assertThrows(NullPointerException.class, () -> ThrowingConsumer.invokeAndUnwrap(consumer, "foo", null));

            ThrowingConsumer.invokeAndUnwrap(consumer, null, IOException.class);

            verify(consumer).accept(null);
            verifyNoMoreInteractions(consumer);
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            Consumer<String> consumer = Spied.consumer(String::toUpperCase);

            ThrowingConsumer.invokeAndUnwrap(consumer, "foo", IOException.class);

            verify(consumer).accept("foo");
            verifyNoMoreInteractions(consumer);
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                Consumer<String> consumer = s -> {
                    throw UncheckedException.withoutStackTrace(new IOException(s));
                };

                IOException thrown = assertThrows(IOException.class, () -> ThrowingConsumer.invokeAndUnwrap(consumer, "foo", IOException.class));
                assertEquals("foo", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                Consumer<String> consumer = s -> {
                    throw UncheckedException.withoutStackTrace(new FileNotFoundException(s));
                };

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class,
                        () -> ThrowingConsumer.invokeAndUnwrap(consumer, "foo", IOException.class));
                assertEquals("foo", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                Consumer<String> consumer = s -> {
                    throw UncheckedException.withoutStackTrace(new ParseException(s, 0));
                };

                UncheckedException thrown = assertThrows(UncheckedException.class,
                        () -> ThrowingConsumer.invokeAndUnwrap(consumer, "foo", IOException.class));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            Consumer<String> consumer = s -> {
                throw new IllegalStateException(s);
            };

            IllegalStateException thrown = assertThrows(IllegalStateException.class,
                    () -> ThrowingConsumer.invokeAndUnwrap(consumer, "foo", IOException.class));
            assertEquals("foo", thrown.getMessage());
        }
    }
}
