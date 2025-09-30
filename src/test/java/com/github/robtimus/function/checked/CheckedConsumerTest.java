/*
 * CheckedConsumerTest.java
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
import java.util.function.Consumer;
import java.util.function.Function;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

@SuppressWarnings("nls")
class CheckedConsumerTest {

    @Nested
    class AndThen {

        @Test
        void testNullArgument() {
            CheckedConsumer<String, IOException> consumer = Spied.checkedConsumer(String::toUpperCase);

            assertThrows(NullPointerException.class, () -> consumer.andThen(null));
        }

        @Test
        void testNeitherThrows() throws IOException {
            CheckedConsumer<String, IOException> consumer = Spied.checkedConsumer(String::toUpperCase);
            CheckedConsumer<String, IOException> after = Spied.checkedConsumer(String::toLowerCase);

            CheckedConsumer<String, IOException> composed = consumer.andThen(after);

            composed.accept("foo");

            verify(consumer).accept("foo");
            verify(consumer).andThen(after);
            verify(after).accept("foo");
            verifyNoMoreInteractions(consumer, after);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedConsumer<String, IOException> consumer = Spied.checkedConsumer(s -> {
                throw new IOException(s);
            });
            CheckedConsumer<String, IOException> after = Spied.checkedConsumer(String::toLowerCase);

            CheckedConsumer<String, IOException> composed = consumer.andThen(after);

            IOException thrown = assertThrows(IOException.class, () -> composed.accept("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(consumer).accept("foo");
            verify(consumer).andThen(after);
            verifyNoMoreInteractions(consumer, after);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            CheckedConsumer<String, IOException> consumer = Spied.checkedConsumer(throwable::throwUnchecked);
            CheckedConsumer<String, IOException> after = Spied.checkedConsumer(String::toLowerCase);

            CheckedConsumer<String, IOException> composed = consumer.andThen(after);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> composed.accept("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(consumer).accept("foo");
            verify(consumer).andThen(after);
            verifyNoMoreInteractions(consumer, after);
        }

        @Test
        void testAfterThrowsChecked() throws IOException {
            CheckedConsumer<String, IOException> consumer = Spied.checkedConsumer(String::toUpperCase);
            CheckedConsumer<String, IOException> after = Spied.checkedConsumer(s -> {
                throw new IOException(s);
            });

            CheckedConsumer<String, IOException> composed = consumer.andThen(after);

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
            CheckedConsumer<String, IOException> consumer = Spied.checkedConsumer(String::toUpperCase);
            CheckedConsumer<String, IOException> after = Spied.checkedConsumer(throwable::throwUnchecked);

            CheckedConsumer<String, IOException> composed = consumer.andThen(after);

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
            CheckedConsumer<String, IOException> consumer = Spied.checkedConsumer(String::toUpperCase);

            assertThrows(NullPointerException.class, () -> consumer.onErrorThrowAsChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedConsumer<String, IOException> consumer = Spied.checkedConsumer(String::toUpperCase);

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedConsumer<String, ExecutionException> throwing = consumer.onErrorThrowAsChecked(errorMapper);

            throwing.accept("foo");

            verify(consumer).accept("foo");
            verify(consumer).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(consumer, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedConsumer<String, IOException> consumer = Spied.checkedConsumer(s -> {
                throw new IOException(s);
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedConsumer<String, ExecutionException> throwing = consumer.onErrorThrowAsChecked(errorMapper);

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
            CheckedConsumer<String, IOException> consumer = Spied.checkedConsumer(throwable::throwUnchecked);

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedConsumer<String, ExecutionException> throwing = consumer.onErrorThrowAsChecked(errorMapper);

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
            CheckedConsumer<String, IOException> consumer = Spied.checkedConsumer(String::toUpperCase);

            assertThrows(NullPointerException.class, () -> consumer.onErrorThrowAsUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedConsumer<String, IOException> consumer = Spied.checkedConsumer(String::toUpperCase);

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            Consumer<String> throwing = consumer.onErrorThrowAsUnchecked(errorMapper);

            throwing.accept("foo");

            verify(consumer).accept("foo");
            verify(consumer).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(consumer, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedConsumer<String, IOException> consumer = Spied.checkedConsumer(s -> {
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
            CheckedConsumer<String, IOException> consumer = Spied.checkedConsumer(throwable::throwUnchecked);

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
            CheckedConsumer<String, IOException> consumer = Spied.checkedConsumer(String::toUpperCase);

            assertThrows(NullPointerException.class, () -> consumer.onErrorHandleChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedConsumer<String, IOException> consumer = Spied.checkedConsumer(String::toUpperCase);

            CheckedConsumer<IOException, ExecutionException> errorHandler = Spied.checkedConsumer(Exception::getMessage);

            CheckedConsumer<String, ExecutionException> handling = consumer.onErrorHandleChecked(errorHandler);

            handling.accept("foo");

            verify(consumer).accept("foo");
            verify(consumer).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(consumer, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException, ExecutionException {
                CheckedConsumer<String, IOException> consumer = Spied.checkedConsumer(s -> {
                    throw new IOException(s);
                });

                CheckedConsumer<IOException, ExecutionException> errorHandler = Spied.checkedConsumer(Exception::getMessage);

                CheckedConsumer<String, ExecutionException> handling = consumer.onErrorHandleChecked(errorHandler);

                handling.accept("foo");

                verify(consumer).accept("foo");
                verify(consumer).onErrorHandleChecked(errorHandler);
                verify(errorHandler).accept(any());
                verifyNoMoreInteractions(consumer, errorHandler);
            }

            @Test
            void testHandlerThrowsChecked() throws IOException, ExecutionException {
                CheckedConsumer<String, IOException> consumer = Spied.checkedConsumer(s -> {
                    throw new IOException(s);
                });

                CheckedConsumer<IOException, ExecutionException> errorHandler = Spied.checkedConsumer(e -> {
                    throw new ExecutionException(e);
                });

                CheckedConsumer<String, ExecutionException> handling = consumer.onErrorHandleChecked(errorHandler);

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
                CheckedConsumer<String, IOException> consumer = Spied.checkedConsumer(s -> {
                    throw new IOException(s);
                });

                CheckedConsumer<IOException, ExecutionException> errorHandler = Spied.checkedConsumer(throwable::throwUnchecked);

                CheckedConsumer<String, ExecutionException> handling = consumer.onErrorHandleChecked(errorHandler);

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
            CheckedConsumer<String, IOException> consumer = Spied.checkedConsumer(throwable::throwUnchecked);

            CheckedConsumer<IOException, ExecutionException> errorHandler = Spied.checkedConsumer(Exception::getMessage);

            CheckedConsumer<String, ExecutionException> handling = consumer.onErrorHandleChecked(errorHandler);

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
            CheckedConsumer<String, IOException> consumer = Spied.checkedConsumer(String::toUpperCase);

            assertThrows(NullPointerException.class, () -> consumer.onErrorHandleUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedConsumer<String, IOException> consumer = Spied.checkedConsumer(String::toUpperCase);

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
                CheckedConsumer<String, IOException> consumer = Spied.checkedConsumer(s -> {
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
                CheckedConsumer<String, IOException> consumer = Spied.checkedConsumer(s -> {
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
            CheckedConsumer<String, IOException> consumer = Spied.checkedConsumer(throwable::throwUnchecked);

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
            CheckedConsumer<String, IOException> consumer = Spied.checkedConsumer(String::toUpperCase);

            assertThrows(NullPointerException.class, () -> consumer.onErrorAcceptChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedConsumer<String, IOException> consumer = Spied.checkedConsumer(String::toUpperCase);

            CheckedConsumer<String, ExecutionException> fallback = Spied.checkedConsumer(String::toLowerCase);

            CheckedConsumer<String, ExecutionException> applying = consumer.onErrorAcceptChecked(fallback);

            applying.accept("foo");

            verify(consumer).accept("foo");
            verify(consumer).onErrorAcceptChecked(fallback);
            verifyNoMoreInteractions(consumer, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                CheckedConsumer<String, IOException> consumer = Spied.checkedConsumer(s -> {
                    throw new IOException(s);
                });

                CheckedConsumer<String, ParseException> fallback = Spied.checkedConsumer(String::toLowerCase);

                CheckedConsumer<String, ParseException> applying = consumer.onErrorAcceptChecked(fallback);

                applying.accept("foo");

                verify(consumer).accept("foo");
                verify(consumer).onErrorAcceptChecked(fallback);
                verify(fallback).accept("foo");
                verifyNoMoreInteractions(consumer, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                CheckedConsumer<String, IOException> consumer = Spied.checkedConsumer(s -> {
                    throw new IOException(s);
                });

                CheckedConsumer<String, ParseException> fallback = Spied.checkedConsumer(s -> {
                    throw new ParseException(s, 0);
                });

                CheckedConsumer<String, ParseException> applying = consumer.onErrorAcceptChecked(fallback);

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
                CheckedConsumer<String, IOException> consumer = Spied.checkedConsumer(s -> {
                    throw new IOException(s);
                });

                CheckedConsumer<String, ParseException> fallback = Spied.checkedConsumer(throwable::throwUnchecked);

                CheckedConsumer<String, ParseException> applying = consumer.onErrorAcceptChecked(fallback);

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
            CheckedConsumer<String, IOException> consumer = Spied.checkedConsumer(throwable::throwUnchecked);

            CheckedConsumer<String, ParseException> fallback = Spied.checkedConsumer(String::toLowerCase);

            CheckedConsumer<String, ParseException> applying = consumer.onErrorAcceptChecked(fallback);

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
            CheckedConsumer<String, IOException> consumer = Spied.checkedConsumer(String::toUpperCase);

            assertThrows(NullPointerException.class, () -> consumer.onErrorAcceptUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedConsumer<String, IOException> consumer = Spied.checkedConsumer(String::toUpperCase);

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
                CheckedConsumer<String, IOException> consumer = Spied.checkedConsumer(s -> {
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
                CheckedConsumer<String, IOException> consumer = Spied.checkedConsumer(s -> {
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
            CheckedConsumer<String, IOException> consumer = Spied.checkedConsumer(throwable::throwUnchecked);

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
            CheckedConsumer<String, IOException> consumer = Spied.checkedConsumer(String::toUpperCase);

            Consumer<String> discarding = consumer.onErrorDiscard();

            discarding.accept("foo");

            verify(consumer).accept("foo");
            verify(consumer).onErrorDiscard();
            verifyNoMoreInteractions(consumer);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedConsumer<String, IOException> consumer = Spied.checkedConsumer(s -> {
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
            CheckedConsumer<String, IOException> consumer = Spied.checkedConsumer(throwable::throwUnchecked);

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
            CheckedConsumer<String, IOException> consumer = Spied.checkedConsumer(String::toUpperCase);

            Consumer<String> unchecked = consumer.unchecked();

            unchecked.accept("foo");

            verify(consumer).accept("foo");
            verify(consumer).unchecked();
            verify(consumer).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(consumer);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedConsumer<String, IOException> consumer = Spied.checkedConsumer(s -> {
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
            CheckedConsumer<String, IOException> consumer = Spied.checkedConsumer(throwable::throwUnchecked);

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
            assertThrows(NullPointerException.class, () -> CheckedConsumer.of(null));
        }

        @Test
        void testNonNullArgument() throws IOException {
            CheckedConsumer<String, IOException> delegate = Spied.checkedConsumer(String::toUpperCase);

            CheckedConsumer<String, IOException> consumer = CheckedConsumer.of(delegate);

            consumer.accept("foo");

            verify(delegate).accept("foo");
            verifyNoMoreInteractions(delegate);
        }
    }

    @Nested
    class UncheckedFactory {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> CheckedConsumer.unchecked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedConsumer<String, IOException> consumer = Spied.checkedConsumer(String::toUpperCase);

            Consumer<String> unchecked = CheckedConsumer.unchecked(consumer);

            unchecked.accept("foo");

            verify(consumer).unchecked();
            verify(consumer).onErrorThrowAsUnchecked(any());
            verify(consumer).accept("foo");
            verifyNoMoreInteractions(consumer);
        }

        @Test
        void testArgumentThrowsChecked() throws IOException {
            CheckedConsumer<String, IOException> consumer = Spied.checkedConsumer(s -> {
                throw new IOException(s);
            });

            Consumer<String> unchecked = CheckedConsumer.unchecked(consumer);

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
            CheckedConsumer<String, IOException> consumer = Spied.checkedConsumer(throwable::throwUnchecked);

            Consumer<String> unchecked = CheckedConsumer.unchecked(consumer);

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
            assertThrows(NullPointerException.class, () -> CheckedConsumer.checked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            Consumer<String> delegate = Spied.consumer(String::toUpperCase);

            CheckedConsumer<String, IOException> consumer = CheckedConsumer.checked(delegate);

            consumer.accept("foo");

            verify(delegate).accept("foo");
            verifyNoMoreInteractions(delegate);
        }

        @Test
        void testArgumentThrowsUnchecked() {
            CheckedConsumer<String, IOException> consumer = CheckedConsumer.checked(s -> {
                throw new UncheckedException(s, new IOException());
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

            assertThrows(NullPointerException.class, () -> CheckedConsumer.checked(null, IOException.class));
            assertThrows(NullPointerException.class, () -> CheckedConsumer.checked(consumer, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            Consumer<String> delegate = Spied.consumer(String::toUpperCase);

            CheckedConsumer<String, IOException> consumer = CheckedConsumer.checked(delegate, IOException.class);

            consumer.accept("foo");

            verify(delegate).accept("foo");
            verifyNoMoreInteractions(delegate);
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                CheckedConsumer<String, IOException> consumer = CheckedConsumer.checked(s -> {
                    throw new UncheckedException(new IOException(s));
                }, IOException.class);

                IOException thrown = assertThrows(IOException.class, () -> consumer.accept("foo"));
                assertEquals("foo", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                CheckedConsumer<String, IOException> consumer = CheckedConsumer.checked(s -> {
                    throw new UncheckedException(new FileNotFoundException(s));
                }, IOException.class);

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class, () -> consumer.accept("foo"));
                assertEquals("foo", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                CheckedConsumer<String, IOException> consumer = CheckedConsumer.checked(s -> {
                    throw new UncheckedException(new ParseException(s, 0));
                }, IOException.class);

                UncheckedException thrown = assertThrows(UncheckedException.class, () -> consumer.accept("foo"));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            CheckedConsumer<String, IOException> consumer = CheckedConsumer.checked(s -> {
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

            assertThrows(NullPointerException.class, () -> CheckedConsumer.invokeAndUnwrap(null, "foo", IOException.class));
            assertThrows(NullPointerException.class, () -> CheckedConsumer.invokeAndUnwrap(consumer, "foo", null));

            CheckedConsumer.invokeAndUnwrap(consumer, null, IOException.class);

            verify(consumer).accept(null);
            verifyNoMoreInteractions(consumer);
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            Consumer<String> consumer = Spied.consumer(String::toUpperCase);

            CheckedConsumer.invokeAndUnwrap(consumer, "foo", IOException.class);

            verify(consumer).accept("foo");
            verifyNoMoreInteractions(consumer);
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                Consumer<String> consumer = s -> {
                    throw new UncheckedException(new IOException(s));
                };

                IOException thrown = assertThrows(IOException.class, () -> CheckedConsumer.invokeAndUnwrap(consumer, "foo", IOException.class));
                assertEquals("foo", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                Consumer<String> consumer = s -> {
                    throw new UncheckedException(new FileNotFoundException(s));
                };

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class,
                        () -> CheckedConsumer.invokeAndUnwrap(consumer, "foo", IOException.class));
                assertEquals("foo", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                Consumer<String> consumer = s -> {
                    throw new UncheckedException(new ParseException(s, 0));
                };

                UncheckedException thrown = assertThrows(UncheckedException.class,
                        () -> CheckedConsumer.invokeAndUnwrap(consumer, "foo", IOException.class));
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
                    () -> CheckedConsumer.invokeAndUnwrap(consumer, "foo", IOException.class));
            assertEquals("foo", thrown.getMessage());
        }
    }
}
