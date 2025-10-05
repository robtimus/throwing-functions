/*
 * ThrowingLongConsumerTest.java
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
import java.util.function.LongConsumer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

@SuppressWarnings("nls")
class ThrowingLongConsumerTest {

    @Nested
    class AndThen {

        @Test
        void testNullArgument() {
            ThrowingLongConsumer<IOException> consumer = Spied.throwingLongConsumer(Long::toString);

            assertThrows(NullPointerException.class, () -> consumer.andThen(null));
        }

        @Test
        void testNeitherThrows() throws IOException {
            ThrowingLongConsumer<IOException> consumer = Spied.throwingLongConsumer(Long::toString);
            ThrowingLongConsumer<IOException> after = Spied.throwingLongConsumer(Long::reverse);

            ThrowingLongConsumer<IOException> composed = consumer.andThen(after);

            composed.accept(1L);

            verify(consumer).accept(1L);
            verify(consumer).andThen(after);
            verify(after).accept(1L);
            verifyNoMoreInteractions(consumer, after);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingLongConsumer<IOException> consumer = Spied.throwingLongConsumer(l -> {
                throw new IOException(Long.toString(l));
            });
            ThrowingLongConsumer<IOException> after = Spied.throwingLongConsumer(Long::reverse);

            ThrowingLongConsumer<IOException> composed = consumer.andThen(after);

            IOException thrown = assertThrows(IOException.class, () -> composed.accept(1L));
            assertEquals("1", thrown.getMessage());

            verify(consumer).accept(1L);
            verify(consumer).andThen(after);
            verifyNoMoreInteractions(consumer, after);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingLongConsumer<IOException> consumer = Spied.throwingLongConsumer(throwable::throwUnchecked);
            ThrowingLongConsumer<IOException> after = Spied.throwingLongConsumer(Long::reverse);

            ThrowingLongConsumer<IOException> composed = consumer.andThen(after);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> composed.accept(1L));
            assertEquals("1", thrown.getMessage());

            verify(consumer).accept(1L);
            verify(consumer).andThen(after);
            verifyNoMoreInteractions(consumer, after);
        }

        @Test
        void testAfterThrowsChecked() throws IOException {
            ThrowingLongConsumer<IOException> consumer = Spied.throwingLongConsumer(Long::toString);
            ThrowingLongConsumer<IOException> after = Spied.throwingLongConsumer(l -> {
                throw new IOException(Long.toString(l));
            });

            ThrowingLongConsumer<IOException> composed = consumer.andThen(after);

            IOException thrown = assertThrows(IOException.class, () -> composed.accept(1L));
            assertEquals("1", thrown.getMessage());

            verify(consumer).accept(1L);
            verify(consumer).andThen(after);
            verify(after).accept(1L);
            verifyNoMoreInteractions(consumer, after);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testAfterThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingLongConsumer<IOException> consumer = Spied.throwingLongConsumer(Long::toString);
            ThrowingLongConsumer<IOException> after = Spied.throwingLongConsumer(throwable::throwUnchecked);

            ThrowingLongConsumer<IOException> composed = consumer.andThen(after);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> composed.accept(1L));
            assertEquals("1", thrown.getMessage());

            verify(consumer).accept(1L);
            verify(consumer).andThen(after);
            verify(after).accept(1L);
            verifyNoMoreInteractions(consumer, after);
        }
    }

    @Nested
    class OnErrorThrowAsChecked {

        @Test
        void testNullArgument() {
            ThrowingLongConsumer<IOException> consumer = Spied.throwingLongConsumer(Long::toString);

            assertThrows(NullPointerException.class, () -> consumer.onErrorThrowAsChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingLongConsumer<IOException> consumer = Spied.throwingLongConsumer(Long::toString);

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingLongConsumer<ExecutionException> throwing = consumer.onErrorThrowAsChecked(errorMapper);

            throwing.accept(1L);

            verify(consumer).accept(1L);
            verify(consumer).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(consumer, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingLongConsumer<IOException> consumer = Spied.throwingLongConsumer(l -> {
                throw new IOException(Long.toString(l));
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingLongConsumer<ExecutionException> throwing = consumer.onErrorThrowAsChecked(errorMapper);

            ExecutionException thrown = assertThrows(ExecutionException.class, () -> throwing.accept(1L));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1", cause.getMessage());

            verify(consumer).accept(1L);
            verify(consumer).onErrorThrowAsChecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(consumer, errorMapper);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingLongConsumer<IOException> consumer = Spied.throwingLongConsumer(throwable::throwUnchecked);

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingLongConsumer<ExecutionException> throwing = consumer.onErrorThrowAsChecked(errorMapper);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> throwing.accept(1L));
            assertEquals("1", thrown.getMessage());

            verify(consumer).accept(1L);
            verify(consumer).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(consumer, errorMapper);
        }
    }

    @Nested
    class OnErrorThrowAsUnchecked {

        @Test
        void testNullArgument() {
            ThrowingLongConsumer<IOException> consumer = Spied.throwingLongConsumer(Long::toString);

            assertThrows(NullPointerException.class, () -> consumer.onErrorThrowAsUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingLongConsumer<IOException> consumer = Spied.throwingLongConsumer(Long::toString);

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            LongConsumer throwing = consumer.onErrorThrowAsUnchecked(errorMapper);

            throwing.accept(1L);

            verify(consumer).accept(1L);
            verify(consumer).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(consumer, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingLongConsumer<IOException> consumer = Spied.throwingLongConsumer(l -> {
                throw new IOException(Long.toString(l));
            });

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            LongConsumer throwing = consumer.onErrorThrowAsUnchecked(errorMapper);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> throwing.accept(1L));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1", cause.getMessage());

            verify(consumer).accept(1L);
            verify(consumer).onErrorThrowAsUnchecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(consumer, errorMapper);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingLongConsumer<IOException> consumer = Spied.throwingLongConsumer(throwable::throwUnchecked);

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            LongConsumer throwing = consumer.onErrorThrowAsUnchecked(errorMapper);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> throwing.accept(1L));
            assertEquals("1", thrown.getMessage());

            verify(consumer).accept(1L);
            verify(consumer).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(consumer, errorMapper);
        }
    }

    @Nested
    class OnErrorHandleChecked {

        @Test
        void testNullArgument() {
            ThrowingLongConsumer<IOException> consumer = Spied.throwingLongConsumer(Long::toString);

            assertThrows(NullPointerException.class, () -> consumer.onErrorHandleChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingLongConsumer<IOException> consumer = Spied.throwingLongConsumer(Long::toString);

            ThrowingConsumer<IOException, ExecutionException> errorHandler = Spied.throwingConsumer(Exception::getMessage);

            ThrowingLongConsumer<ExecutionException> handling = consumer.onErrorHandleChecked(errorHandler);

            handling.accept(1L);

            verify(consumer).accept(1L);
            verify(consumer).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(consumer, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException, ExecutionException {
                ThrowingLongConsumer<IOException> consumer = Spied.throwingLongConsumer(l -> {
                    throw new IOException(Long.toString(l));
                });

                ThrowingConsumer<IOException, ExecutionException> errorHandler = Spied.throwingConsumer(Exception::getMessage);

                ThrowingLongConsumer<ExecutionException> handling = consumer.onErrorHandleChecked(errorHandler);

                handling.accept(1L);

                verify(consumer).accept(1L);
                verify(consumer).onErrorHandleChecked(errorHandler);
                verify(errorHandler).accept(any());
                verifyNoMoreInteractions(consumer, errorHandler);
            }

            @Test
            void testHandlerThrowsChecked() throws IOException, ExecutionException {
                ThrowingLongConsumer<IOException> consumer = Spied.throwingLongConsumer(l -> {
                    throw new IOException(Long.toString(l));
                });

                ThrowingConsumer<IOException, ExecutionException> errorHandler = Spied.throwingConsumer(e -> {
                    throw new ExecutionException(e);
                });

                ThrowingLongConsumer<ExecutionException> handling = consumer.onErrorHandleChecked(errorHandler);

                ExecutionException thrown = assertThrows(ExecutionException.class, () -> handling.accept(1L));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("1", cause.getMessage());

                verify(consumer).accept(1L);
                verify(consumer).onErrorHandleChecked(errorHandler);
                verify(errorHandler).accept(any());
                verifyNoMoreInteractions(consumer, errorHandler);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testHandlerThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ExecutionException {
                ThrowingLongConsumer<IOException> consumer = Spied.throwingLongConsumer(l -> {
                    throw new IOException(Long.toString(l));
                });

                ThrowingConsumer<IOException, ExecutionException> errorHandler = Spied.throwingConsumer(throwable::throwUnchecked);

                ThrowingLongConsumer<ExecutionException> handling = consumer.onErrorHandleChecked(errorHandler);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.accept(1L));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("1", cause.getMessage());

                verify(consumer).accept(1L);
                verify(consumer).onErrorHandleChecked(errorHandler);
                verify(errorHandler).accept(any());
                verifyNoMoreInteractions(consumer, errorHandler);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingLongConsumer<IOException> consumer = Spied.throwingLongConsumer(throwable::throwUnchecked);

            ThrowingConsumer<IOException, ExecutionException> errorHandler = Spied.throwingConsumer(Exception::getMessage);

            ThrowingLongConsumer<ExecutionException> handling = consumer.onErrorHandleChecked(errorHandler);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.accept(1L));
            assertEquals("1", thrown.getMessage());

            verify(consumer).accept(1L);
            verify(consumer).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(consumer, errorHandler);
        }
    }

    @Nested
    class OnErrorHandleUnchecked {

        @Test
        void testNullArgument() {
            ThrowingLongConsumer<IOException> consumer = Spied.throwingLongConsumer(Long::toString);

            assertThrows(NullPointerException.class, () -> consumer.onErrorHandleUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingLongConsumer<IOException> consumer = Spied.throwingLongConsumer(Long::toString);

            Consumer<IOException> errorHandler = Spied.consumer(Exception::getMessage);

            LongConsumer handling = consumer.onErrorHandleUnchecked(errorHandler);

            handling.accept(1L);

            verify(consumer).accept(1L);
            verify(consumer).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(consumer, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException {
                ThrowingLongConsumer<IOException> consumer = Spied.throwingLongConsumer(l -> {
                    throw new IOException(Long.toString(l));
                });

                Consumer<IOException> errorHandler = Spied.consumer(Exception::getMessage);

                LongConsumer handling = consumer.onErrorHandleUnchecked(errorHandler);

                handling.accept(1L);

                verify(consumer).accept(1L);
                verify(consumer).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).accept(any());
                verifyNoMoreInteractions(consumer, errorHandler);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testHandlerThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingLongConsumer<IOException> consumer = Spied.throwingLongConsumer(l -> {
                    throw new IOException(Long.toString(l));
                });

                Consumer<IOException> errorHandler = Spied.consumer(throwable::throwUnchecked);

                LongConsumer handling = consumer.onErrorHandleUnchecked(errorHandler);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.accept(1L));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("1", cause.getMessage());

                verify(consumer).accept(1L);
                verify(consumer).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).accept(any());
                verifyNoMoreInteractions(consumer, errorHandler);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingLongConsumer<IOException> consumer = Spied.throwingLongConsumer(throwable::throwUnchecked);

            Consumer<IOException> errorHandler = Spied.consumer(Exception::getMessage);

            LongConsumer handling = consumer.onErrorHandleUnchecked(errorHandler);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.accept(1L));
            assertEquals("1", thrown.getMessage());

            verify(consumer).accept(1L);
            verify(consumer).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(consumer, errorHandler);
        }
    }

    @Nested
    class OnErrorAcceptChecked {

        @Test
        void testNullArgument() {
            ThrowingLongConsumer<IOException> consumer = Spied.throwingLongConsumer(Long::toString);

            assertThrows(NullPointerException.class, () -> consumer.onErrorAcceptChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingLongConsumer<IOException> consumer = Spied.throwingLongConsumer(Long::toString);

            ThrowingLongConsumer<ExecutionException> fallback = Spied.throwingLongConsumer(Long::reverse);

            ThrowingLongConsumer<ExecutionException> applying = consumer.onErrorAcceptChecked(fallback);

            applying.accept(1L);

            verify(consumer).accept(1L);
            verify(consumer).onErrorAcceptChecked(fallback);
            verifyNoMoreInteractions(consumer, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                ThrowingLongConsumer<IOException> consumer = Spied.throwingLongConsumer(l -> {
                    throw new IOException(Long.toString(l));
                });

                ThrowingLongConsumer<ParseException> fallback = Spied.throwingLongConsumer(Long::reverse);

                ThrowingLongConsumer<ParseException> applying = consumer.onErrorAcceptChecked(fallback);

                applying.accept(1L);

                verify(consumer).accept(1L);
                verify(consumer).onErrorAcceptChecked(fallback);
                verify(fallback).accept(1L);
                verifyNoMoreInteractions(consumer, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                ThrowingLongConsumer<IOException> consumer = Spied.throwingLongConsumer(l -> {
                    throw new IOException(Long.toString(l));
                });

                ThrowingLongConsumer<ParseException> fallback = Spied.throwingLongConsumer(l -> {
                    throw new ParseException(Long.toString(l), 0);
                });

                ThrowingLongConsumer<ParseException> applying = consumer.onErrorAcceptChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> applying.accept(1L));
                assertEquals("1", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(consumer).accept(1L);
                verify(consumer).onErrorAcceptChecked(fallback);
                verify(fallback).accept(1L);
                verifyNoMoreInteractions(consumer, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ParseException {
                ThrowingLongConsumer<IOException> consumer = Spied.throwingLongConsumer(l -> {
                    throw new IOException(Long.toString(l));
                });

                ThrowingLongConsumer<ParseException> fallback = Spied.throwingLongConsumer(throwable::throwUnchecked);

                ThrowingLongConsumer<ParseException> applying = consumer.onErrorAcceptChecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.accept(1L));
                assertEquals("1", thrown.getMessage());

                verify(consumer).accept(1L);
                verify(consumer).onErrorAcceptChecked(fallback);
                verify(fallback).accept(1L);
                verifyNoMoreInteractions(consumer, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingLongConsumer<IOException> consumer = Spied.throwingLongConsumer(throwable::throwUnchecked);

            ThrowingLongConsumer<ParseException> fallback = Spied.throwingLongConsumer(Long::reverse);

            ThrowingLongConsumer<ParseException> applying = consumer.onErrorAcceptChecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.accept(1L));
            assertEquals("1", thrown.getMessage());

            verify(consumer).accept(1L);
            verify(consumer).onErrorAcceptChecked(fallback);
            verifyNoMoreInteractions(consumer, fallback);
        }
    }

    @Nested
    class OnErrorAcceptUnchecked {

        @Test
        void testNullArgument() {
            ThrowingLongConsumer<IOException> consumer = Spied.throwingLongConsumer(Long::toString);

            assertThrows(NullPointerException.class, () -> consumer.onErrorAcceptUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingLongConsumer<IOException> consumer = Spied.throwingLongConsumer(Long::toString);

            LongConsumer fallback = Spied.longConsumer(Long::reverse);

            LongConsumer applying = consumer.onErrorAcceptUnchecked(fallback);

            applying.accept(1L);

            verify(consumer).accept(1L);
            verify(consumer).onErrorAcceptUnchecked(fallback);
            verifyNoMoreInteractions(consumer, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                ThrowingLongConsumer<IOException> consumer = Spied.throwingLongConsumer(l -> {
                    throw new IOException(Long.toString(l));
                });

                LongConsumer fallback = Spied.longConsumer(Long::reverse);

                LongConsumer applying = consumer.onErrorAcceptUnchecked(fallback);

                applying.accept(1L);

                verify(consumer).accept(1L);
                verify(consumer).onErrorAcceptUnchecked(fallback);
                verify(fallback).accept(1L);
                verifyNoMoreInteractions(consumer, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingLongConsumer<IOException> consumer = Spied.throwingLongConsumer(l -> {
                    throw new IOException(Long.toString(l));
                });

                LongConsumer fallback = Spied.longConsumer(throwable::throwUnchecked);

                LongConsumer applying = consumer.onErrorAcceptUnchecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.accept(1L));
                assertEquals("1", thrown.getMessage());

                verify(consumer).accept(1L);
                verify(consumer).onErrorAcceptUnchecked(fallback);
                verify(fallback).accept(1L);
                verifyNoMoreInteractions(consumer, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingLongConsumer<IOException> consumer = Spied.throwingLongConsumer(throwable::throwUnchecked);

            LongConsumer fallback = Spied.longConsumer(Long::reverse);

            LongConsumer applying = consumer.onErrorAcceptUnchecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.accept(1L));
            assertEquals("1", thrown.getMessage());

            verify(consumer).accept(1L);
            verify(consumer).onErrorAcceptUnchecked(fallback);
            verifyNoMoreInteractions(consumer, fallback);
        }
    }

    @Nested
    class OnErrorDiscard {

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingLongConsumer<IOException> consumer = Spied.throwingLongConsumer(Long::toString);

            LongConsumer discarding = consumer.onErrorDiscard();

            discarding.accept(1L);

            verify(consumer).accept(1L);
            verify(consumer).onErrorDiscard();
            verifyNoMoreInteractions(consumer);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingLongConsumer<IOException> consumer = Spied.throwingLongConsumer(l -> {
                throw new IOException(Long.toString(l));
            });

            LongConsumer discarding = consumer.onErrorDiscard();

            discarding.accept(1L);

            verify(consumer).accept(1L);
            verify(consumer).onErrorDiscard();
            verifyNoMoreInteractions(consumer);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingLongConsumer<IOException> consumer = Spied.throwingLongConsumer(throwable::throwUnchecked);

            LongConsumer discarding = consumer.onErrorDiscard();

            Throwable thrown = assertThrows(throwable.throwableType(), () -> discarding.accept(1L));
            assertEquals("1", thrown.getMessage());

            verify(consumer).accept(1L);
            verify(consumer).onErrorDiscard();
            verifyNoMoreInteractions(consumer);
        }
    }

    @Nested
    class Unchecked {

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingLongConsumer<IOException> consumer = Spied.throwingLongConsumer(Long::toString);

            LongConsumer unchecked = consumer.unchecked();

            unchecked.accept(1L);

            verify(consumer).accept(1L);
            verify(consumer).unchecked();
            verify(consumer).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(consumer);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingLongConsumer<IOException> consumer = Spied.throwingLongConsumer(l -> {
                throw new IOException(Long.toString(l));
            });

            LongConsumer unchecked = consumer.unchecked();

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.accept(1L));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1", cause.getMessage());

            verify(consumer).accept(1L);
            verify(consumer).unchecked();
            verify(consumer).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(consumer);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingLongConsumer<IOException> consumer = Spied.throwingLongConsumer(throwable::throwUnchecked);

            LongConsumer unchecked = consumer.unchecked();

            Throwable thrown = assertThrows(throwable.throwableType(), () -> unchecked.accept(1L));
            assertEquals("1", thrown.getMessage());

            verify(consumer).accept(1L);
            verify(consumer).unchecked();
            verify(consumer).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(consumer);
        }
    }

    @Nested
    class Of {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> ThrowingLongConsumer.of(null));
        }

        @Test
        void testNonNullArgument() throws IOException {
            ThrowingLongConsumer<IOException> delegate = Spied.throwingLongConsumer(Long::toString);

            ThrowingLongConsumer<IOException> consumer = ThrowingLongConsumer.of(delegate);

            consumer.accept(1L);

            verify(delegate).accept(1L);
            verifyNoMoreInteractions(delegate);
        }
    }

    @Nested
    class UncheckedFactory {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> ThrowingLongConsumer.unchecked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ThrowingLongConsumer<IOException> consumer = Spied.throwingLongConsumer(Long::toString);

            LongConsumer unchecked = ThrowingLongConsumer.unchecked(consumer);

            unchecked.accept(1L);

            verify(consumer).unchecked();
            verify(consumer).onErrorThrowAsUnchecked(any());
            verify(consumer).accept(1L);
            verifyNoMoreInteractions(consumer);
        }

        @Test
        void testArgumentThrowsChecked() throws IOException {
            ThrowingLongConsumer<IOException> consumer = Spied.throwingLongConsumer(l -> {
                throw new IOException(Long.toString(l));
            });

            LongConsumer unchecked = ThrowingLongConsumer.unchecked(consumer);

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.accept(1L));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1", cause.getMessage());

            verify(consumer).unchecked();
            verify(consumer).onErrorThrowAsUnchecked(any());
            verify(consumer).accept(1L);
            verifyNoMoreInteractions(consumer);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testArgumentThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingLongConsumer<IOException> consumer = Spied.throwingLongConsumer(throwable::throwUnchecked);

            LongConsumer unchecked = ThrowingLongConsumer.unchecked(consumer);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> unchecked.accept(1L));
            assertEquals("1", thrown.getMessage());

            verify(consumer).unchecked();
            verify(consumer).onErrorThrowAsUnchecked(any());
            verify(consumer).accept(1L);
            verifyNoMoreInteractions(consumer);
        }
    }

    @Nested
    class CheckedWrapper {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> ThrowingLongConsumer.checked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            LongConsumer delegate = Spied.longConsumer(Long::toString);

            ThrowingLongConsumer<IOException> consumer = ThrowingLongConsumer.checked(delegate);

            consumer.accept(1L);

            verify(delegate).accept(1L);
            verifyNoMoreInteractions(delegate);
        }

        @Test
        void testArgumentThrowsUnchecked() {
            ThrowingLongConsumer<IOException> consumer = ThrowingLongConsumer.checked(l -> {
                throw UncheckedException.withoutStackTrace(Long.toString(l), new IOException());
            });

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> consumer.accept(1L));
            assertEquals("1", thrown.getMessage());
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertNull(cause.getMessage());
        }
    }

    @Nested
    class CheckedUnwrapper {

        @Test
        void testNullArguments() {
            LongConsumer consumer = Long::toString;

            assertThrows(NullPointerException.class, () -> ThrowingLongConsumer.checked(null, IOException.class));
            assertThrows(NullPointerException.class, () -> ThrowingLongConsumer.checked(consumer, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            LongConsumer delegate = Spied.longConsumer(Long::toString);

            ThrowingLongConsumer<IOException> consumer = ThrowingLongConsumer.checked(delegate, IOException.class);

            consumer.accept(1L);

            verify(delegate).accept(1L);
            verifyNoMoreInteractions(delegate);
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                ThrowingLongConsumer<IOException> consumer = ThrowingLongConsumer.checked(l -> {
                    throw UncheckedException.withoutStackTrace(new IOException(Long.toString(l)));
                }, IOException.class);

                IOException thrown = assertThrows(IOException.class, () -> consumer.accept(1L));
                assertEquals("1", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                ThrowingLongConsumer<IOException> consumer = ThrowingLongConsumer.checked(l -> {
                    throw UncheckedException.withoutStackTrace(new FileNotFoundException(Long.toString(l)));
                }, IOException.class);

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class, () -> consumer.accept(1L));
                assertEquals("1", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                ThrowingLongConsumer<IOException> consumer = ThrowingLongConsumer.checked(l -> {
                    throw UncheckedException.withoutStackTrace(new ParseException(Long.toString(l), 0));
                }, IOException.class);

                UncheckedException thrown = assertThrows(UncheckedException.class, () -> consumer.accept(1L));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("1", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            ThrowingLongConsumer<IOException> consumer = ThrowingLongConsumer.checked(l -> {
                throw new IllegalStateException(Long.toString(l));
            }, IOException.class);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> consumer.accept(1L));
            assertEquals("1", thrown.getMessage());
        }
    }

    @Nested
    class InvokeAndUnwrap {

        @Test
        void testNullArguments() {
            LongConsumer consumer = Long::toString;

            assertThrows(NullPointerException.class, () -> ThrowingLongConsumer.invokeAndUnwrap(null, 1L, IOException.class));
            assertThrows(NullPointerException.class, () -> ThrowingLongConsumer.invokeAndUnwrap(consumer, 1L, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            LongConsumer consumer = Spied.longConsumer(Long::toString);

            ThrowingLongConsumer.invokeAndUnwrap(consumer, 1L, IOException.class);

            verify(consumer).accept(1L);
            verifyNoMoreInteractions(consumer);
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                LongConsumer consumer = l -> {
                    throw UncheckedException.withoutStackTrace(new IOException(Long.toString(l)));
                };

                IOException thrown = assertThrows(IOException.class, () -> ThrowingLongConsumer.invokeAndUnwrap(consumer, 1L, IOException.class));
                assertEquals("1", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                LongConsumer consumer = l -> {
                    throw UncheckedException.withoutStackTrace(new FileNotFoundException(Long.toString(l)));
                };

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class,
                        () -> ThrowingLongConsumer.invokeAndUnwrap(consumer, 1L, IOException.class));
                assertEquals("1", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                LongConsumer consumer = l -> {
                    throw UncheckedException.withoutStackTrace(new ParseException(Long.toString(l), 0));
                };

                UncheckedException thrown = assertThrows(UncheckedException.class,
                        () -> ThrowingLongConsumer.invokeAndUnwrap(consumer, 1L, IOException.class));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("1", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            LongConsumer consumer = l -> {
                throw new IllegalStateException(Long.toString(l));
            };

            IllegalStateException thrown = assertThrows(IllegalStateException.class,
                    () -> ThrowingLongConsumer.invokeAndUnwrap(consumer, 1L, IOException.class));
            assertEquals("1", thrown.getMessage());
        }
    }
}
