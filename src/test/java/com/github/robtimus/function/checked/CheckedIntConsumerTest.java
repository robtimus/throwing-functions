/*
 * CheckedIntConsumerTest.java
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
import java.util.function.IntConsumer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

@SuppressWarnings("nls")
class CheckedIntConsumerTest {

    @Nested
    class AndThen {

        @Test
        void testNullArgument() {
            CheckedIntConsumer<IOException> consumer = Spied.checkedIntConsumer(Integer::toString);

            assertThrows(NullPointerException.class, () -> consumer.andThen(null));
        }

        @Test
        void testNeitherThrows() throws IOException {
            CheckedIntConsumer<IOException> consumer = Spied.checkedIntConsumer(Integer::toString);
            CheckedIntConsumer<IOException> after = Spied.checkedIntConsumer(Integer::reverse);

            CheckedIntConsumer<IOException> composed = consumer.andThen(after);

            composed.accept(1);

            verify(consumer).accept(1);
            verify(consumer).andThen(after);
            verify(after).accept(1);
            verifyNoMoreInteractions(consumer, after);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedIntConsumer<IOException> consumer = Spied.checkedIntConsumer(i -> {
                throw new IOException(Integer.toString(i));
            });
            CheckedIntConsumer<IOException> after = Spied.checkedIntConsumer(Integer::reverse);

            CheckedIntConsumer<IOException> composed = consumer.andThen(after);

            IOException thrown = assertThrows(IOException.class, () -> composed.accept(1));
            assertEquals("1", thrown.getMessage());

            verify(consumer).accept(1);
            verify(consumer).andThen(after);
            verifyNoMoreInteractions(consumer, after);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            CheckedIntConsumer<IOException> consumer = Spied.checkedIntConsumer(throwable::throwUnchecked);
            CheckedIntConsumer<IOException> after = Spied.checkedIntConsumer(Integer::reverse);

            CheckedIntConsumer<IOException> composed = consumer.andThen(after);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> composed.accept(1));
            assertEquals("1", thrown.getMessage());

            verify(consumer).accept(1);
            verify(consumer).andThen(after);
            verifyNoMoreInteractions(consumer, after);
        }

        @Test
        void testAfterThrowsChecked() throws IOException {
            CheckedIntConsumer<IOException> consumer = Spied.checkedIntConsumer(Integer::toString);
            CheckedIntConsumer<IOException> after = Spied.checkedIntConsumer(i -> {
                throw new IOException(Integer.toString(i));
            });

            CheckedIntConsumer<IOException> composed = consumer.andThen(after);

            IOException thrown = assertThrows(IOException.class, () -> composed.accept(1));
            assertEquals("1", thrown.getMessage());

            verify(consumer).accept(1);
            verify(consumer).andThen(after);
            verify(after).accept(1);
            verifyNoMoreInteractions(consumer, after);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testAfterThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            CheckedIntConsumer<IOException> consumer = Spied.checkedIntConsumer(Integer::toString);
            CheckedIntConsumer<IOException> after = Spied.checkedIntConsumer(throwable::throwUnchecked);

            CheckedIntConsumer<IOException> composed = consumer.andThen(after);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> composed.accept(1));
            assertEquals("1", thrown.getMessage());

            verify(consumer).accept(1);
            verify(consumer).andThen(after);
            verify(after).accept(1);
            verifyNoMoreInteractions(consumer, after);
        }
    }

    @Nested
    class OnErrorThrowAsChecked {

        @Test
        void testNullArgument() {
            CheckedIntConsumer<IOException> consumer = Spied.checkedIntConsumer(Integer::toString);

            assertThrows(NullPointerException.class, () -> consumer.onErrorThrowAsChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedIntConsumer<IOException> consumer = Spied.checkedIntConsumer(Integer::toString);

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedIntConsumer<ExecutionException> throwing = consumer.onErrorThrowAsChecked(errorMapper);

            throwing.accept(1);

            verify(consumer).accept(1);
            verify(consumer).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(consumer, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedIntConsumer<IOException> consumer = Spied.checkedIntConsumer(i -> {
                throw new IOException(Integer.toString(i));
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedIntConsumer<ExecutionException> throwing = consumer.onErrorThrowAsChecked(errorMapper);

            ExecutionException thrown = assertThrows(ExecutionException.class, () -> throwing.accept(1));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1", cause.getMessage());

            verify(consumer).accept(1);
            verify(consumer).onErrorThrowAsChecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(consumer, errorMapper);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            CheckedIntConsumer<IOException> consumer = Spied.checkedIntConsumer(throwable::throwUnchecked);

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedIntConsumer<ExecutionException> throwing = consumer.onErrorThrowAsChecked(errorMapper);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> throwing.accept(1));
            assertEquals("1", thrown.getMessage());

            verify(consumer).accept(1);
            verify(consumer).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(consumer, errorMapper);
        }
    }

    @Nested
    class OnErrorThrowAsUnchecked {

        @Test
        void testNullArgument() {
            CheckedIntConsumer<IOException> consumer = Spied.checkedIntConsumer(Integer::toString);

            assertThrows(NullPointerException.class, () -> consumer.onErrorThrowAsUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedIntConsumer<IOException> consumer = Spied.checkedIntConsumer(Integer::toString);

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            IntConsumer throwing = consumer.onErrorThrowAsUnchecked(errorMapper);

            throwing.accept(1);

            verify(consumer).accept(1);
            verify(consumer).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(consumer, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedIntConsumer<IOException> consumer = Spied.checkedIntConsumer(i -> {
                throw new IOException(Integer.toString(i));
            });

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            IntConsumer throwing = consumer.onErrorThrowAsUnchecked(errorMapper);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> throwing.accept(1));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1", cause.getMessage());

            verify(consumer).accept(1);
            verify(consumer).onErrorThrowAsUnchecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(consumer, errorMapper);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            CheckedIntConsumer<IOException> consumer = Spied.checkedIntConsumer(throwable::throwUnchecked);

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            IntConsumer throwing = consumer.onErrorThrowAsUnchecked(errorMapper);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> throwing.accept(1));
            assertEquals("1", thrown.getMessage());

            verify(consumer).accept(1);
            verify(consumer).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(consumer, errorMapper);
        }
    }

    @Nested
    class OnErrorHandleChecked {

        @Test
        void testNullArgument() {
            CheckedIntConsumer<IOException> consumer = Spied.checkedIntConsumer(Integer::toString);

            assertThrows(NullPointerException.class, () -> consumer.onErrorHandleChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedIntConsumer<IOException> consumer = Spied.checkedIntConsumer(Integer::toString);

            CheckedConsumer<IOException, ExecutionException> errorHandler = Spied.checkedConsumer(Exception::getMessage);

            CheckedIntConsumer<ExecutionException> handling = consumer.onErrorHandleChecked(errorHandler);

            handling.accept(1);

            verify(consumer).accept(1);
            verify(consumer).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(consumer, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException, ExecutionException {
                CheckedIntConsumer<IOException> consumer = Spied.checkedIntConsumer(i -> {
                    throw new IOException(Integer.toString(i));
                });

                CheckedConsumer<IOException, ExecutionException> errorHandler = Spied.checkedConsumer(Exception::getMessage);

                CheckedIntConsumer<ExecutionException> handling = consumer.onErrorHandleChecked(errorHandler);

                handling.accept(1);

                verify(consumer).accept(1);
                verify(consumer).onErrorHandleChecked(errorHandler);
                verify(errorHandler).accept(any());
                verifyNoMoreInteractions(consumer, errorHandler);
            }

            @Test
            void testHandlerThrowsChecked() throws IOException, ExecutionException {
                CheckedIntConsumer<IOException> consumer = Spied.checkedIntConsumer(i -> {
                    throw new IOException(Integer.toString(i));
                });

                CheckedConsumer<IOException, ExecutionException> errorHandler = Spied.checkedConsumer(e -> {
                    throw new ExecutionException(e);
                });

                CheckedIntConsumer<ExecutionException> handling = consumer.onErrorHandleChecked(errorHandler);

                ExecutionException thrown = assertThrows(ExecutionException.class, () -> handling.accept(1));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("1", cause.getMessage());

                verify(consumer).accept(1);
                verify(consumer).onErrorHandleChecked(errorHandler);
                verify(errorHandler).accept(any());
                verifyNoMoreInteractions(consumer, errorHandler);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testHandlerThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ExecutionException {
                CheckedIntConsumer<IOException> consumer = Spied.checkedIntConsumer(i -> {
                    throw new IOException(Integer.toString(i));
                });

                CheckedConsumer<IOException, ExecutionException> errorHandler = Spied.checkedConsumer(throwable::throwUnchecked);

                CheckedIntConsumer<ExecutionException> handling = consumer.onErrorHandleChecked(errorHandler);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.accept(1));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("1", cause.getMessage());

                verify(consumer).accept(1);
                verify(consumer).onErrorHandleChecked(errorHandler);
                verify(errorHandler).accept(any());
                verifyNoMoreInteractions(consumer, errorHandler);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            CheckedIntConsumer<IOException> consumer = Spied.checkedIntConsumer(throwable::throwUnchecked);

            CheckedConsumer<IOException, ExecutionException> errorHandler = Spied.checkedConsumer(Exception::getMessage);

            CheckedIntConsumer<ExecutionException> handling = consumer.onErrorHandleChecked(errorHandler);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.accept(1));
            assertEquals("1", thrown.getMessage());

            verify(consumer).accept(1);
            verify(consumer).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(consumer, errorHandler);
        }
    }

    @Nested
    class OnErrorHandleUnchecked {

        @Test
        void testNullArgument() {
            CheckedIntConsumer<IOException> consumer = Spied.checkedIntConsumer(Integer::toString);

            assertThrows(NullPointerException.class, () -> consumer.onErrorHandleUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedIntConsumer<IOException> consumer = Spied.checkedIntConsumer(Integer::toString);

            Consumer<IOException> errorHandler = Spied.consumer(Exception::getMessage);

            IntConsumer handling = consumer.onErrorHandleUnchecked(errorHandler);

            handling.accept(1);

            verify(consumer).accept(1);
            verify(consumer).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(consumer, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException {
                CheckedIntConsumer<IOException> consumer = Spied.checkedIntConsumer(i -> {
                    throw new IOException(Integer.toString(i));
                });

                Consumer<IOException> errorHandler = Spied.consumer(Exception::getMessage);

                IntConsumer handling = consumer.onErrorHandleUnchecked(errorHandler);

                handling.accept(1);

                verify(consumer).accept(1);
                verify(consumer).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).accept(any());
                verifyNoMoreInteractions(consumer, errorHandler);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testHandlerThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                CheckedIntConsumer<IOException> consumer = Spied.checkedIntConsumer(i -> {
                    throw new IOException(Integer.toString(i));
                });

                Consumer<IOException> errorHandler = Spied.consumer(throwable::throwUnchecked);

                IntConsumer handling = consumer.onErrorHandleUnchecked(errorHandler);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.accept(1));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("1", cause.getMessage());

                verify(consumer).accept(1);
                verify(consumer).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).accept(any());
                verifyNoMoreInteractions(consumer, errorHandler);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            CheckedIntConsumer<IOException> consumer = Spied.checkedIntConsumer(throwable::throwUnchecked);

            Consumer<IOException> errorHandler = Spied.consumer(Exception::getMessage);

            IntConsumer handling = consumer.onErrorHandleUnchecked(errorHandler);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.accept(1));
            assertEquals("1", thrown.getMessage());

            verify(consumer).accept(1);
            verify(consumer).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(consumer, errorHandler);
        }
    }

    @Nested
    class OnErrorAcceptChecked {

        @Test
        void testNullArgument() {
            CheckedIntConsumer<IOException> consumer = Spied.checkedIntConsumer(Integer::toString);

            assertThrows(NullPointerException.class, () -> consumer.onErrorAcceptChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedIntConsumer<IOException> consumer = Spied.checkedIntConsumer(Integer::toString);

            CheckedIntConsumer<ExecutionException> fallback = Spied.checkedIntConsumer(Integer::reverse);

            CheckedIntConsumer<ExecutionException> applying = consumer.onErrorAcceptChecked(fallback);

            applying.accept(1);

            verify(consumer).accept(1);
            verify(consumer).onErrorAcceptChecked(fallback);
            verifyNoMoreInteractions(consumer, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                CheckedIntConsumer<IOException> consumer = Spied.checkedIntConsumer(i -> {
                    throw new IOException(Integer.toString(i));
                });

                CheckedIntConsumer<ParseException> fallback = Spied.checkedIntConsumer(Integer::reverse);

                CheckedIntConsumer<ParseException> applying = consumer.onErrorAcceptChecked(fallback);

                applying.accept(1);

                verify(consumer).accept(1);
                verify(consumer).onErrorAcceptChecked(fallback);
                verify(fallback).accept(1);
                verifyNoMoreInteractions(consumer, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                CheckedIntConsumer<IOException> consumer = Spied.checkedIntConsumer(i -> {
                    throw new IOException(Integer.toString(i));
                });

                CheckedIntConsumer<ParseException> fallback = Spied.checkedIntConsumer(i -> {
                    throw new ParseException(Integer.toString(i), 0);
                });

                CheckedIntConsumer<ParseException> applying = consumer.onErrorAcceptChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> applying.accept(1));
                assertEquals("1", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(consumer).accept(1);
                verify(consumer).onErrorAcceptChecked(fallback);
                verify(fallback).accept(1);
                verifyNoMoreInteractions(consumer, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ParseException {
                CheckedIntConsumer<IOException> consumer = Spied.checkedIntConsumer(i -> {
                    throw new IOException(Integer.toString(i));
                });

                CheckedIntConsumer<ParseException> fallback = Spied.checkedIntConsumer(throwable::throwUnchecked);

                CheckedIntConsumer<ParseException> applying = consumer.onErrorAcceptChecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.accept(1));
                assertEquals("1", thrown.getMessage());

                verify(consumer).accept(1);
                verify(consumer).onErrorAcceptChecked(fallback);
                verify(fallback).accept(1);
                verifyNoMoreInteractions(consumer, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            CheckedIntConsumer<IOException> consumer = Spied.checkedIntConsumer(throwable::throwUnchecked);

            CheckedIntConsumer<ParseException> fallback = Spied.checkedIntConsumer(Integer::reverse);

            CheckedIntConsumer<ParseException> applying = consumer.onErrorAcceptChecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.accept(1));
            assertEquals("1", thrown.getMessage());

            verify(consumer).accept(1);
            verify(consumer).onErrorAcceptChecked(fallback);
            verifyNoMoreInteractions(consumer, fallback);
        }
    }

    @Nested
    class OnErrorAcceptUnchecked {

        @Test
        void testNullArgument() {
            CheckedIntConsumer<IOException> consumer = Spied.checkedIntConsumer(Integer::toString);

            assertThrows(NullPointerException.class, () -> consumer.onErrorAcceptUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedIntConsumer<IOException> consumer = Spied.checkedIntConsumer(Integer::toString);

            IntConsumer fallback = Spied.intConsumer(Integer::reverse);

            IntConsumer applying = consumer.onErrorAcceptUnchecked(fallback);

            applying.accept(1);

            verify(consumer).accept(1);
            verify(consumer).onErrorAcceptUnchecked(fallback);
            verifyNoMoreInteractions(consumer, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                CheckedIntConsumer<IOException> consumer = Spied.checkedIntConsumer(i -> {
                    throw new IOException(Integer.toString(i));
                });

                IntConsumer fallback = Spied.intConsumer(Integer::reverse);

                IntConsumer applying = consumer.onErrorAcceptUnchecked(fallback);

                applying.accept(1);

                verify(consumer).accept(1);
                verify(consumer).onErrorAcceptUnchecked(fallback);
                verify(fallback).accept(1);
                verifyNoMoreInteractions(consumer, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                CheckedIntConsumer<IOException> consumer = Spied.checkedIntConsumer(i -> {
                    throw new IOException(Integer.toString(i));
                });

                IntConsumer fallback = Spied.intConsumer(throwable::throwUnchecked);

                IntConsumer applying = consumer.onErrorAcceptUnchecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.accept(1));
                assertEquals("1", thrown.getMessage());

                verify(consumer).accept(1);
                verify(consumer).onErrorAcceptUnchecked(fallback);
                verify(fallback).accept(1);
                verifyNoMoreInteractions(consumer, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            CheckedIntConsumer<IOException> consumer = Spied.checkedIntConsumer(throwable::throwUnchecked);

            IntConsumer fallback = Spied.intConsumer(Integer::reverse);

            IntConsumer applying = consumer.onErrorAcceptUnchecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.accept(1));
            assertEquals("1", thrown.getMessage());

            verify(consumer).accept(1);
            verify(consumer).onErrorAcceptUnchecked(fallback);
            verifyNoMoreInteractions(consumer, fallback);
        }
    }

    @Nested
    class OnErrorDiscard {

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedIntConsumer<IOException> consumer = Spied.checkedIntConsumer(Integer::toString);

            IntConsumer discarding = consumer.onErrorDiscard();

            discarding.accept(1);

            verify(consumer).accept(1);
            verify(consumer).onErrorDiscard();
            verifyNoMoreInteractions(consumer);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedIntConsumer<IOException> consumer = Spied.checkedIntConsumer(i -> {
                throw new IOException(Integer.toString(i));
            });

            IntConsumer discarding = consumer.onErrorDiscard();

            discarding.accept(1);

            verify(consumer).accept(1);
            verify(consumer).onErrorDiscard();
            verifyNoMoreInteractions(consumer);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            CheckedIntConsumer<IOException> consumer = Spied.checkedIntConsumer(throwable::throwUnchecked);

            IntConsumer discarding = consumer.onErrorDiscard();

            Throwable thrown = assertThrows(throwable.throwableType(), () -> discarding.accept(1));
            assertEquals("1", thrown.getMessage());

            verify(consumer).accept(1);
            verify(consumer).onErrorDiscard();
            verifyNoMoreInteractions(consumer);
        }
    }

    @Nested
    class Unchecked {

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedIntConsumer<IOException> consumer = Spied.checkedIntConsumer(Integer::toString);

            IntConsumer unchecked = consumer.unchecked();

            unchecked.accept(1);

            verify(consumer).accept(1);
            verify(consumer).unchecked();
            verify(consumer).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(consumer);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedIntConsumer<IOException> consumer = Spied.checkedIntConsumer(i -> {
                throw new IOException(Integer.toString(i));
            });

            IntConsumer unchecked = consumer.unchecked();

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.accept(1));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1", cause.getMessage());

            verify(consumer).accept(1);
            verify(consumer).unchecked();
            verify(consumer).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(consumer);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            CheckedIntConsumer<IOException> consumer = Spied.checkedIntConsumer(throwable::throwUnchecked);

            IntConsumer unchecked = consumer.unchecked();

            Throwable thrown = assertThrows(throwable.throwableType(), () -> unchecked.accept(1));
            assertEquals("1", thrown.getMessage());

            verify(consumer).accept(1);
            verify(consumer).unchecked();
            verify(consumer).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(consumer);
        }
    }

    @Nested
    class Of {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> CheckedIntConsumer.of(null));
        }

        @Test
        void testNonNullArgument() throws IOException {
            CheckedIntConsumer<IOException> delegate = Spied.checkedIntConsumer(Integer::toString);

            CheckedIntConsumer<IOException> consumer = CheckedIntConsumer.of(delegate);

            consumer.accept(1);

            verify(delegate).accept(1);
            verifyNoMoreInteractions(delegate);
        }
    }

    @Nested
    class UncheckedFactory {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> CheckedIntConsumer.unchecked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedIntConsumer<IOException> consumer = Spied.checkedIntConsumer(Integer::toString);

            IntConsumer unchecked = CheckedIntConsumer.unchecked(consumer);

            unchecked.accept(1);

            verify(consumer).unchecked();
            verify(consumer).onErrorThrowAsUnchecked(any());
            verify(consumer).accept(1);
            verifyNoMoreInteractions(consumer);
        }

        @Test
        void testArgumentThrowsChecked() throws IOException {
            CheckedIntConsumer<IOException> consumer = Spied.checkedIntConsumer(i -> {
                throw new IOException(Integer.toString(i));
            });

            IntConsumer unchecked = CheckedIntConsumer.unchecked(consumer);

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.accept(1));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1", cause.getMessage());

            verify(consumer).unchecked();
            verify(consumer).onErrorThrowAsUnchecked(any());
            verify(consumer).accept(1);
            verifyNoMoreInteractions(consumer);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testArgumentThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            CheckedIntConsumer<IOException> consumer = Spied.checkedIntConsumer(throwable::throwUnchecked);

            IntConsumer unchecked = CheckedIntConsumer.unchecked(consumer);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> unchecked.accept(1));
            assertEquals("1", thrown.getMessage());

            verify(consumer).unchecked();
            verify(consumer).onErrorThrowAsUnchecked(any());
            verify(consumer).accept(1);
            verifyNoMoreInteractions(consumer);
        }
    }

    @Nested
    class CheckedWrapper {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> CheckedIntConsumer.checked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            IntConsumer delegate = Spied.intConsumer(Integer::toString);

            CheckedIntConsumer<IOException> consumer = CheckedIntConsumer.checked(delegate);

            consumer.accept(1);

            verify(delegate).accept(1);
            verifyNoMoreInteractions(delegate);
        }

        @Test
        void testArgumentThrowsUnchecked() {
            CheckedIntConsumer<IOException> consumer = CheckedIntConsumer.checked(i -> {
                throw new UncheckedException(Integer.toString(i), new IOException());
            });

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> consumer.accept(1));
            assertEquals("1", thrown.getMessage());
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertNull(cause.getMessage());
        }
    }

    @Nested
    class CheckedUnwrapper {

        @Test
        void testNullArguments() {
            IntConsumer consumer = Integer::toString;

            assertThrows(NullPointerException.class, () -> CheckedIntConsumer.checked(null, IOException.class));
            assertThrows(NullPointerException.class, () -> CheckedIntConsumer.checked(consumer, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            IntConsumer delegate = Spied.intConsumer(Integer::toString);

            CheckedIntConsumer<IOException> consumer = CheckedIntConsumer.checked(delegate, IOException.class);

            consumer.accept(1);

            verify(delegate).accept(1);
            verifyNoMoreInteractions(delegate);
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                CheckedIntConsumer<IOException> consumer = CheckedIntConsumer.checked(i -> {
                    throw new UncheckedException(new IOException(Integer.toString(i)));
                }, IOException.class);

                IOException thrown = assertThrows(IOException.class, () -> consumer.accept(1));
                assertEquals("1", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                CheckedIntConsumer<IOException> consumer = CheckedIntConsumer.checked(i -> {
                    throw new UncheckedException(new FileNotFoundException(Integer.toString(i)));
                }, IOException.class);

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class, () -> consumer.accept(1));
                assertEquals("1", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                CheckedIntConsumer<IOException> consumer = CheckedIntConsumer.checked(i -> {
                    throw new UncheckedException(new ParseException(Integer.toString(i), 0));
                }, IOException.class);

                UncheckedException thrown = assertThrows(UncheckedException.class, () -> consumer.accept(1));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("1", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            CheckedIntConsumer<IOException> consumer = CheckedIntConsumer.checked(i -> {
                throw new IllegalStateException(Integer.toString(i));
            }, IOException.class);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> consumer.accept(1));
            assertEquals("1", thrown.getMessage());
        }
    }

    @Nested
    class InvokeAndUnwrap {

        @Test
        void testNullArguments() {
            IntConsumer consumer = Integer::toString;

            assertThrows(NullPointerException.class, () -> CheckedIntConsumer.invokeAndUnwrap(null, 1, IOException.class));
            assertThrows(NullPointerException.class, () -> CheckedIntConsumer.invokeAndUnwrap(consumer, 1, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            IntConsumer consumer = Spied.intConsumer(Integer::toString);

            CheckedIntConsumer.invokeAndUnwrap(consumer, 1, IOException.class);

            verify(consumer).accept(1);
            verifyNoMoreInteractions(consumer);
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                IntConsumer consumer = i -> {
                    throw new UncheckedException(new IOException(Integer.toString(i)));
                };

                IOException thrown = assertThrows(IOException.class, () -> CheckedIntConsumer.invokeAndUnwrap(consumer, 1, IOException.class));
                assertEquals("1", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                IntConsumer consumer = i -> {
                    throw new UncheckedException(new FileNotFoundException(Integer.toString(i)));
                };

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class,
                        () -> CheckedIntConsumer.invokeAndUnwrap(consumer, 1, IOException.class));
                assertEquals("1", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                IntConsumer consumer = i -> {
                    throw new UncheckedException(new ParseException(Integer.toString(i), 0));
                };

                UncheckedException thrown = assertThrows(UncheckedException.class,
                        () -> CheckedIntConsumer.invokeAndUnwrap(consumer, 1, IOException.class));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("1", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            IntConsumer consumer = i -> {
                throw new IllegalStateException(Integer.toString(i));
            };

            IllegalStateException thrown = assertThrows(IllegalStateException.class,
                    () -> CheckedIntConsumer.invokeAndUnwrap(consumer, 1, IOException.class));
            assertEquals("1", thrown.getMessage());
        }
    }
}
