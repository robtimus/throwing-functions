/*
 * CheckedDoubleConsumerTest.java
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
import java.util.function.DoubleConsumer;
import java.util.function.Function;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("nls")
class CheckedDoubleConsumerTest {

    @Nested
    class AndThen {

        @Test
        void testNullArgument() {
            CheckedDoubleConsumer<IOException> consumer = Spied.checkedDoubleConsumer(Double::toString);

            assertThrows(NullPointerException.class, () -> consumer.andThen(null));
        }

        @Test
        void testNeitherThrows() throws IOException {
            CheckedDoubleConsumer<IOException> consumer = Spied.checkedDoubleConsumer(Double::toString);
            CheckedDoubleConsumer<IOException> after = Spied.checkedDoubleConsumer(Double::isNaN);

            CheckedDoubleConsumer<IOException> composed = consumer.andThen(after);

            composed.accept(1D);

            verify(consumer).accept(1D);
            verify(consumer).andThen(after);
            verify(after).accept(1D);
            verifyNoMoreInteractions(consumer, after);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedDoubleConsumer<IOException> consumer = Spied.checkedDoubleConsumer(d -> {
                throw new IOException(Double.toString(d));
            });
            CheckedDoubleConsumer<IOException> after = Spied.checkedDoubleConsumer(Double::isNaN);

            CheckedDoubleConsumer<IOException> composed = consumer.andThen(after);

            IOException thrown = assertThrows(IOException.class, () -> composed.accept(1D));
            assertEquals("1.0", thrown.getMessage());

            verify(consumer).accept(1D);
            verify(consumer).andThen(after);
            verifyNoMoreInteractions(consumer, after);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedDoubleConsumer<IOException> consumer = Spied.checkedDoubleConsumer(d -> {
                throw new IllegalStateException(Double.toString(d));
            });
            CheckedDoubleConsumer<IOException> after = Spied.checkedDoubleConsumer(Double::isNaN);

            CheckedDoubleConsumer<IOException> composed = consumer.andThen(after);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> composed.accept(1D));
            assertEquals("1.0", thrown.getMessage());

            verify(consumer).accept(1D);
            verify(consumer).andThen(after);
            verifyNoMoreInteractions(consumer, after);
        }

        @Test
        void testAfterThrowsChecked() throws IOException {
            CheckedDoubleConsumer<IOException> consumer = Spied.checkedDoubleConsumer(Double::toString);
            CheckedDoubleConsumer<IOException> after = Spied.checkedDoubleConsumer(d -> {
                throw new IOException(Double.toString(d));
            });

            CheckedDoubleConsumer<IOException> composed = consumer.andThen(after);

            IOException thrown = assertThrows(IOException.class, () -> composed.accept(1D));
            assertEquals("1.0", thrown.getMessage());

            verify(consumer).accept(1D);
            verify(consumer).andThen(after);
            verify(after).accept(1D);
            verifyNoMoreInteractions(consumer, after);
        }

        @Test
        void testAfterThrowsUnchecked() throws IOException {
            CheckedDoubleConsumer<IOException> consumer = Spied.checkedDoubleConsumer(Double::toString);
            CheckedDoubleConsumer<IOException> after = Spied.checkedDoubleConsumer(d -> {
                throw new IllegalStateException(Double.toString(d));
            });

            CheckedDoubleConsumer<IOException> composed = consumer.andThen(after);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> composed.accept(1D));
            assertEquals("1.0", thrown.getMessage());

            verify(consumer).accept(1D);
            verify(consumer).andThen(after);
            verify(after).accept(1D);
            verifyNoMoreInteractions(consumer, after);
        }
    }

    @Nested
    class OnErrorThrowAsChecked {

        @Test
        void testNullArgument() {
            CheckedDoubleConsumer<IOException> consumer = Spied.checkedDoubleConsumer(Double::toString);

            assertThrows(NullPointerException.class, () -> consumer.onErrorThrowAsChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedDoubleConsumer<IOException> consumer = Spied.checkedDoubleConsumer(Double::toString);

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedDoubleConsumer<ExecutionException> throwing = consumer.onErrorThrowAsChecked(errorMapper);

            throwing.accept(1D);

            verify(consumer).accept(1D);
            verify(consumer).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(consumer, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedDoubleConsumer<IOException> consumer = Spied.checkedDoubleConsumer(d -> {
                throw new IOException(Double.toString(d));
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedDoubleConsumer<ExecutionException> throwing = consumer.onErrorThrowAsChecked(errorMapper);

            ExecutionException thrown = assertThrows(ExecutionException.class, () -> throwing.accept(1D));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1.0", cause.getMessage());

            verify(consumer).accept(1D);
            verify(consumer).onErrorThrowAsChecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(consumer, errorMapper);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedDoubleConsumer<IOException> consumer = Spied.checkedDoubleConsumer(d -> {
                throw new IllegalStateException(Double.toString(d));
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedDoubleConsumer<ExecutionException> throwing = consumer.onErrorThrowAsChecked(errorMapper);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> throwing.accept(1D));
            assertEquals("1.0", thrown.getMessage());

            verify(consumer).accept(1D);
            verify(consumer).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(consumer, errorMapper);
        }
    }

    @Nested
    class OnErrorThrowAsUnchecked {

        @Test
        void testNullArgument() {
            CheckedDoubleConsumer<IOException> consumer = Spied.checkedDoubleConsumer(Double::toString);

            assertThrows(NullPointerException.class, () -> consumer.onErrorThrowAsUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedDoubleConsumer<IOException> consumer = Spied.checkedDoubleConsumer(Double::toString);

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            DoubleConsumer throwing = consumer.onErrorThrowAsUnchecked(errorMapper);

            throwing.accept(1D);

            verify(consumer).accept(1D);
            verify(consumer).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(consumer, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedDoubleConsumer<IOException> consumer = Spied.checkedDoubleConsumer(d -> {
                throw new IOException(Double.toString(d));
            });

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            DoubleConsumer throwing = consumer.onErrorThrowAsUnchecked(errorMapper);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> throwing.accept(1D));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1.0", cause.getMessage());

            verify(consumer).accept(1D);
            verify(consumer).onErrorThrowAsUnchecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(consumer, errorMapper);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedDoubleConsumer<IOException> consumer = Spied.checkedDoubleConsumer(d -> {
                throw new IllegalArgumentException(Double.toString(d));
            });

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            DoubleConsumer throwing = consumer.onErrorThrowAsUnchecked(errorMapper);

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> throwing.accept(1D));
            assertEquals("1.0", thrown.getMessage());

            verify(consumer).accept(1D);
            verify(consumer).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(consumer, errorMapper);
        }
    }

    @Nested
    class OnErrorHandleChecked {

        @Test
        void testNullArgument() {
            CheckedDoubleConsumer<IOException> consumer = Spied.checkedDoubleConsumer(Double::toString);

            assertThrows(NullPointerException.class, () -> consumer.onErrorHandleChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedDoubleConsumer<IOException> consumer = Spied.checkedDoubleConsumer(Double::toString);

            CheckedConsumer<IOException, ExecutionException> errorHandler = Spied.checkedConsumer(Exception::getMessage);

            CheckedDoubleConsumer<ExecutionException> handling = consumer.onErrorHandleChecked(errorHandler);

            handling.accept(1D);

            verify(consumer).accept(1D);
            verify(consumer).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(consumer, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException, ExecutionException {
                CheckedDoubleConsumer<IOException> consumer = Spied.checkedDoubleConsumer(d -> {
                    throw new IOException(Double.toString(d));
                });

                CheckedConsumer<IOException, ExecutionException> errorHandler = Spied.checkedConsumer(Exception::getMessage);

                CheckedDoubleConsumer<ExecutionException> handling = consumer.onErrorHandleChecked(errorHandler);

                handling.accept(1D);

                verify(consumer).accept(1D);
                verify(consumer).onErrorHandleChecked(errorHandler);
                verify(errorHandler).accept(any());
                verifyNoMoreInteractions(consumer, errorHandler);
            }

            @Test
            void testHandlerThrowsChecked() throws IOException, ExecutionException {
                CheckedDoubleConsumer<IOException> consumer = Spied.checkedDoubleConsumer(d -> {
                    throw new IOException(Double.toString(d));
                });

                CheckedConsumer<IOException, ExecutionException> errorHandler = Spied.checkedConsumer(e -> {
                    throw new ExecutionException(e);
                });

                CheckedDoubleConsumer<ExecutionException> handling = consumer.onErrorHandleChecked(errorHandler);

                ExecutionException thrown = assertThrows(ExecutionException.class, () -> handling.accept(1D));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("1.0", cause.getMessage());

                verify(consumer).accept(1D);
                verify(consumer).onErrorHandleChecked(errorHandler);
                verify(errorHandler).accept(any());
                verifyNoMoreInteractions(consumer, errorHandler);
            }

            @Test
            void testHandlerThrowsUnchecked() throws IOException, ExecutionException {
                CheckedDoubleConsumer<IOException> consumer = Spied.checkedDoubleConsumer(d -> {
                    throw new IOException(Double.toString(d));
                });

                CheckedConsumer<IOException, ExecutionException> errorHandler = Spied.checkedConsumer(e -> {
                    throw new IllegalStateException(e);
                });

                CheckedDoubleConsumer<ExecutionException> handling = consumer.onErrorHandleChecked(errorHandler);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.accept(1D));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("1.0", cause.getMessage());

                verify(consumer).accept(1D);
                verify(consumer).onErrorHandleChecked(errorHandler);
                verify(errorHandler).accept(any());
                verifyNoMoreInteractions(consumer, errorHandler);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedDoubleConsumer<IOException> consumer = Spied.checkedDoubleConsumer(d -> {
                throw new IllegalStateException(Double.toString(d));
            });

            CheckedConsumer<IOException, ExecutionException> errorHandler = Spied.checkedConsumer(Exception::getMessage);

            CheckedDoubleConsumer<ExecutionException> handling = consumer.onErrorHandleChecked(errorHandler);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.accept(1D));
            assertEquals("1.0", thrown.getMessage());

            verify(consumer).accept(1D);
            verify(consumer).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(consumer, errorHandler);
        }
    }

    @Nested
    class OnErrorHandleUnchecked {

        @Test
        void testNullArgument() {
            CheckedDoubleConsumer<IOException> consumer = Spied.checkedDoubleConsumer(Double::toString);

            assertThrows(NullPointerException.class, () -> consumer.onErrorHandleUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedDoubleConsumer<IOException> consumer = Spied.checkedDoubleConsumer(Double::toString);

            Consumer<IOException> errorHandler = Spied.consumer(Exception::getMessage);

            DoubleConsumer handling = consumer.onErrorHandleUnchecked(errorHandler);

            handling.accept(1D);

            verify(consumer).accept(1D);
            verify(consumer).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(consumer, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException {
                CheckedDoubleConsumer<IOException> consumer = Spied.checkedDoubleConsumer(d -> {
                    throw new IOException(Double.toString(d));
                });

                Consumer<IOException> errorHandler = Spied.consumer(Exception::getMessage);

                DoubleConsumer handling = consumer.onErrorHandleUnchecked(errorHandler);

                handling.accept(1D);

                verify(consumer).accept(1D);
                verify(consumer).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).accept(any());
                verifyNoMoreInteractions(consumer, errorHandler);
            }

            @Test
            void testHandlerThrowsUnchecked() throws IOException {
                CheckedDoubleConsumer<IOException> consumer = Spied.checkedDoubleConsumer(d -> {
                    throw new IOException(Double.toString(d));
                });

                Consumer<IOException> errorHandler = Spied.consumer(e -> {
                    throw new IllegalStateException(e);
                });

                DoubleConsumer handling = consumer.onErrorHandleUnchecked(errorHandler);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.accept(1D));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("1.0", cause.getMessage());

                verify(consumer).accept(1D);
                verify(consumer).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).accept(any());
                verifyNoMoreInteractions(consumer, errorHandler);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedDoubleConsumer<IOException> consumer = Spied.checkedDoubleConsumer(d -> {
                throw new IllegalStateException(Double.toString(d));
            });

            Consumer<IOException> errorHandler = Spied.consumer(Exception::getMessage);

            DoubleConsumer handling = consumer.onErrorHandleUnchecked(errorHandler);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.accept(1D));
            assertEquals("1.0", thrown.getMessage());

            verify(consumer).accept(1D);
            verify(consumer).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(consumer, errorHandler);
        }
    }

    @Nested
    class OnErrorAcceptChecked {

        @Test
        void testNullArgument() {
            CheckedDoubleConsumer<IOException> consumer = Spied.checkedDoubleConsumer(Double::toString);

            assertThrows(NullPointerException.class, () -> consumer.onErrorAcceptChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedDoubleConsumer<IOException> consumer = Spied.checkedDoubleConsumer(Double::toString);

            CheckedDoubleConsumer<ExecutionException> fallback = Spied.checkedDoubleConsumer(Double::isNaN);

            CheckedDoubleConsumer<ExecutionException> applying = consumer.onErrorAcceptChecked(fallback);

            applying.accept(1D);

            verify(consumer).accept(1D);
            verify(consumer).onErrorAcceptChecked(fallback);
            verifyNoMoreInteractions(consumer, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                CheckedDoubleConsumer<IOException> consumer = Spied.checkedDoubleConsumer(d -> {
                    throw new IOException(Double.toString(d));
                });

                CheckedDoubleConsumer<ParseException> fallback = Spied.checkedDoubleConsumer(Double::isNaN);

                CheckedDoubleConsumer<ParseException> applying = consumer.onErrorAcceptChecked(fallback);

                applying.accept(1D);

                verify(consumer).accept(1D);
                verify(consumer).onErrorAcceptChecked(fallback);
                verify(fallback).accept(1D);
                verifyNoMoreInteractions(consumer, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                CheckedDoubleConsumer<IOException> consumer = Spied.checkedDoubleConsumer(d -> {
                    throw new IOException(Double.toString(d));
                });

                CheckedDoubleConsumer<ParseException> fallback = Spied.checkedDoubleConsumer(d -> {
                    throw new ParseException(Double.toString(d), 0);
                });

                CheckedDoubleConsumer<ParseException> applying = consumer.onErrorAcceptChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> applying.accept(1D));
                assertEquals("1.0", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(consumer).accept(1D);
                verify(consumer).onErrorAcceptChecked(fallback);
                verify(fallback).accept(1D);
                verifyNoMoreInteractions(consumer, fallback);
            }

            @Test
            void testFallbackThrowsUnchecked() throws IOException, ParseException {
                CheckedDoubleConsumer<IOException> consumer = Spied.checkedDoubleConsumer(d -> {
                    throw new IOException(Double.toString(d));
                });

                CheckedDoubleConsumer<ParseException> fallback = Spied.checkedDoubleConsumer(d -> {
                    throw new IllegalStateException(Double.toString(d));
                });

                CheckedDoubleConsumer<ParseException> applying = consumer.onErrorAcceptChecked(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> applying.accept(1D));
                assertEquals("1.0", thrown.getMessage());

                verify(consumer).accept(1D);
                verify(consumer).onErrorAcceptChecked(fallback);
                verify(fallback).accept(1D);
                verifyNoMoreInteractions(consumer, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedDoubleConsumer<IOException> consumer = Spied.checkedDoubleConsumer(d -> {
                throw new IllegalStateException(Double.toString(d));
            });

            CheckedDoubleConsumer<ParseException> fallback = Spied.checkedDoubleConsumer(Double::isNaN);

            CheckedDoubleConsumer<ParseException> applying = consumer.onErrorAcceptChecked(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> applying.accept(1D));
            assertEquals("1.0", thrown.getMessage());

            verify(consumer).accept(1D);
            verify(consumer).onErrorAcceptChecked(fallback);
            verifyNoMoreInteractions(consumer, fallback);
        }
    }

    @Nested
    class OnErrorAcceptUnchecked {

        @Test
        void testNullArgument() {
            CheckedDoubleConsumer<IOException> consumer = Spied.checkedDoubleConsumer(Double::toString);

            assertThrows(NullPointerException.class, () -> consumer.onErrorAcceptUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedDoubleConsumer<IOException> consumer = Spied.checkedDoubleConsumer(Double::toString);

            DoubleConsumer fallback = Spied.doubleConsumer(Double::isNaN);

            DoubleConsumer applying = consumer.onErrorAcceptUnchecked(fallback);

            applying.accept(1D);

            verify(consumer).accept(1D);
            verify(consumer).onErrorAcceptUnchecked(fallback);
            verifyNoMoreInteractions(consumer, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                CheckedDoubleConsumer<IOException> consumer = Spied.checkedDoubleConsumer(d -> {
                    throw new IOException(Double.toString(d));
                });

                DoubleConsumer fallback = Spied.doubleConsumer(Double::isNaN);

                DoubleConsumer applying = consumer.onErrorAcceptUnchecked(fallback);

                applying.accept(1D);

                verify(consumer).accept(1D);
                verify(consumer).onErrorAcceptUnchecked(fallback);
                verify(fallback).accept(1D);
                verifyNoMoreInteractions(consumer, fallback);
            }

            @Test
            void testFallbackThrowsUnchecked() throws IOException {
                CheckedDoubleConsumer<IOException> consumer = Spied.checkedDoubleConsumer(d -> {
                    throw new IOException(Double.toString(d));
                });

                DoubleConsumer fallback = Spied.doubleConsumer(d -> {
                    throw new IllegalStateException(Double.toString(d));
                });

                DoubleConsumer applying = consumer.onErrorAcceptUnchecked(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> applying.accept(1D));
                assertEquals("1.0", thrown.getMessage());

                verify(consumer).accept(1D);
                verify(consumer).onErrorAcceptUnchecked(fallback);
                verify(fallback).accept(1D);
                verifyNoMoreInteractions(consumer, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedDoubleConsumer<IOException> consumer = Spied.checkedDoubleConsumer(d -> {
                throw new IllegalStateException(Double.toString(d));
            });

            DoubleConsumer fallback = Spied.doubleConsumer(Double::isNaN);

            DoubleConsumer applying = consumer.onErrorAcceptUnchecked(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> applying.accept(1D));
            assertEquals("1.0", thrown.getMessage());

            verify(consumer).accept(1D);
            verify(consumer).onErrorAcceptUnchecked(fallback);
            verifyNoMoreInteractions(consumer, fallback);
        }
    }

    @Nested
    class OnErrorDiscard {

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedDoubleConsumer<IOException> consumer = Spied.checkedDoubleConsumer(Double::toString);

            DoubleConsumer discarding = consumer.onErrorDiscard();

            discarding.accept(1D);

            verify(consumer).accept(1D);
            verify(consumer).onErrorDiscard();
            verifyNoMoreInteractions(consumer);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedDoubleConsumer<IOException> consumer = Spied.checkedDoubleConsumer(d -> {
                throw new IOException(Double.toString(d));
            });

            DoubleConsumer discarding = consumer.onErrorDiscard();

            discarding.accept(1D);

            verify(consumer).accept(1D);
            verify(consumer).onErrorDiscard();
            verifyNoMoreInteractions(consumer);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedDoubleConsumer<IOException> consumer = Spied.checkedDoubleConsumer(d -> {
                throw new IllegalStateException(Double.toString(d));
            });

            DoubleConsumer discarding = consumer.onErrorDiscard();

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> discarding.accept(1D));
            assertEquals("1.0", thrown.getMessage());

            verify(consumer).accept(1D);
            verify(consumer).onErrorDiscard();
            verifyNoMoreInteractions(consumer);
        }
    }

    @Nested
    class Unchecked {

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedDoubleConsumer<IOException> consumer = Spied.checkedDoubleConsumer(Double::toString);

            DoubleConsumer unchecked = consumer.unchecked();

            unchecked.accept(1D);

            verify(consumer).accept(1D);
            verify(consumer).unchecked();
            verify(consumer).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(consumer);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedDoubleConsumer<IOException> consumer = Spied.checkedDoubleConsumer(d -> {
                throw new IOException(Double.toString(d));
            });

            DoubleConsumer unchecked = consumer.unchecked();

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.accept(1D));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1.0", cause.getMessage());

            verify(consumer).accept(1D);
            verify(consumer).unchecked();
            verify(consumer).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(consumer);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedDoubleConsumer<IOException> consumer = Spied.checkedDoubleConsumer(d -> {
                throw new IllegalArgumentException(Double.toString(d));
            });

            DoubleConsumer unchecked = consumer.unchecked();

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> unchecked.accept(1D));
            assertEquals("1.0", thrown.getMessage());

            verify(consumer).accept(1D);
            verify(consumer).unchecked();
            verify(consumer).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(consumer);
        }
    }

    @Nested
    class Of {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> CheckedDoubleConsumer.of(null));
        }

        @Test
        void testNonNullArgument() throws IOException {
            CheckedDoubleConsumer<IOException> delegate = Spied.checkedDoubleConsumer(Double::toString);

            CheckedDoubleConsumer<IOException> consumer = CheckedDoubleConsumer.of(delegate);

            consumer.accept(1D);

            verify(delegate).accept(1D);
            verifyNoMoreInteractions(delegate);
        }
    }

    @Nested
    class UncheckedFactory {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> CheckedDoubleConsumer.unchecked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedDoubleConsumer<IOException> consumer = Spied.checkedDoubleConsumer(Double::toString);

            DoubleConsumer unchecked = CheckedDoubleConsumer.unchecked(consumer);

            unchecked.accept(1D);

            verify(consumer).unchecked();
            verify(consumer).onErrorThrowAsUnchecked(any());
            verify(consumer).accept(1D);
            verifyNoMoreInteractions(consumer);
        }

        @Test
        void testArgumentThrowsChecked() throws IOException {
            CheckedDoubleConsumer<IOException> consumer = Spied.checkedDoubleConsumer(d -> {
                throw new IOException(Double.toString(d));
            });

            DoubleConsumer unchecked = CheckedDoubleConsumer.unchecked(consumer);

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.accept(1D));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1.0", cause.getMessage());

            verify(consumer).unchecked();
            verify(consumer).onErrorThrowAsUnchecked(any());
            verify(consumer).accept(1D);
            verifyNoMoreInteractions(consumer);
        }

        @Test
        void testArgumentThrowsUnchecked() throws IOException {
            CheckedDoubleConsumer<IOException> consumer = Spied.checkedDoubleConsumer(d -> {
                throw new IllegalArgumentException(Double.toString(d));
            });

            DoubleConsumer unchecked = CheckedDoubleConsumer.unchecked(consumer);

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> unchecked.accept(1D));
            assertEquals("1.0", thrown.getMessage());

            verify(consumer).unchecked();
            verify(consumer).onErrorThrowAsUnchecked(any());
            verify(consumer).accept(1D);
            verifyNoMoreInteractions(consumer);
        }
    }

    @Nested
    class CheckedWrapper {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> CheckedDoubleConsumer.checked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            DoubleConsumer delegate = Spied.doubleConsumer(Double::toString);

            CheckedDoubleConsumer<IOException> consumer = CheckedDoubleConsumer.checked(delegate);

            consumer.accept(1D);

            verify(delegate).accept(1D);
            verifyNoMoreInteractions(delegate);
        }

        @Test
        void testArgumentThrowsUnchecked() {
            CheckedDoubleConsumer<IOException> consumer = CheckedDoubleConsumer.checked(d -> {
                throw new UncheckedException(Double.toString(d), new IOException());
            });

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> consumer.accept(1D));
            assertEquals("1.0", thrown.getMessage());
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertNull(cause.getMessage());
        }
    }

    @Nested
    class CheckedUnwrapper {

        @Test
        void testNullArguments() {
            DoubleConsumer consumer = Double::toString;

            assertThrows(NullPointerException.class, () -> CheckedDoubleConsumer.checked(null, IOException.class));
            assertThrows(NullPointerException.class, () -> CheckedDoubleConsumer.checked(consumer, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            DoubleConsumer delegate = Spied.doubleConsumer(Double::toString);

            CheckedDoubleConsumer<IOException> consumer = CheckedDoubleConsumer.checked(delegate, IOException.class);

            consumer.accept(1D);

            verify(delegate).accept(1D);
            verifyNoMoreInteractions(delegate);
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                CheckedDoubleConsumer<IOException> consumer = CheckedDoubleConsumer.checked(d -> {
                    throw new UncheckedException(new IOException(Double.toString(d)));
                }, IOException.class);

                IOException thrown = assertThrows(IOException.class, () -> consumer.accept(1D));
                assertEquals("1.0", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                CheckedDoubleConsumer<IOException> consumer = CheckedDoubleConsumer.checked(d -> {
                    throw new UncheckedException(new FileNotFoundException(Double.toString(d)));
                }, IOException.class);

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class, () -> consumer.accept(1D));
                assertEquals("1.0", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                CheckedDoubleConsumer<IOException> consumer = CheckedDoubleConsumer.checked(d -> {
                    throw new UncheckedException(new ParseException(Double.toString(d), 0));
                }, IOException.class);

                UncheckedException thrown = assertThrows(UncheckedException.class, () -> consumer.accept(1D));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("1.0", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            CheckedDoubleConsumer<IOException> consumer = CheckedDoubleConsumer.checked(d -> {
                throw new IllegalStateException(Double.toString(d));
            }, IOException.class);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> consumer.accept(1D));
            assertEquals("1.0", thrown.getMessage());
        }
    }

    @Nested
    class InvokeAndUnwrap {

        @Test
        void testNullArguments() {
            DoubleConsumer consumer = Double::toString;

            assertThrows(NullPointerException.class, () -> CheckedDoubleConsumer.invokeAndUnwrap(null, 1D, IOException.class));
            assertThrows(NullPointerException.class, () -> CheckedDoubleConsumer.invokeAndUnwrap(consumer, 1D, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            DoubleConsumer consumer = Spied.doubleConsumer(Double::toString);

            CheckedDoubleConsumer.invokeAndUnwrap(consumer, 1D, IOException.class);

            verify(consumer).accept(1D);
            verifyNoMoreInteractions(consumer);
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                DoubleConsumer consumer = d -> {
                    throw new UncheckedException(new IOException(Double.toString(d)));
                };

                IOException thrown = assertThrows(IOException.class, () -> CheckedDoubleConsumer.invokeAndUnwrap(consumer, 1D, IOException.class));
                assertEquals("1.0", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                DoubleConsumer consumer = d -> {
                    throw new UncheckedException(new FileNotFoundException(Double.toString(d)));
                };

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class,
                        () -> CheckedDoubleConsumer.invokeAndUnwrap(consumer, 1D, IOException.class));
                assertEquals("1.0", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                DoubleConsumer consumer = d -> {
                    throw new UncheckedException(new ParseException(Double.toString(d), 0));
                };

                UncheckedException thrown = assertThrows(UncheckedException.class,
                        () -> CheckedDoubleConsumer.invokeAndUnwrap(consumer, 1D, IOException.class));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("1.0", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            DoubleConsumer consumer = d -> {
                throw new IllegalStateException(Double.toString(d));
            };

            IllegalStateException thrown = assertThrows(IllegalStateException.class,
                    () -> CheckedDoubleConsumer.invokeAndUnwrap(consumer, 1D, IOException.class));
            assertEquals("1.0", thrown.getMessage());
        }
    }
}
