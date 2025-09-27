/*
 * CheckedLongConsumerTest.java
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
import java.util.function.LongConsumer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("nls")
class CheckedLongConsumerTest {

    @Nested
    class AndThen {

        @Test
        void testNullArgument() {
            CheckedLongConsumer<IOException> consumer = Spied.checkedLongConsumer(Long::toString);

            assertThrows(NullPointerException.class, () -> consumer.andThen(null));
        }

        @Test
        void testNeitherThrows() throws IOException {
            CheckedLongConsumer<IOException> consumer = Spied.checkedLongConsumer(Long::toString);
            CheckedLongConsumer<IOException> after = Spied.checkedLongConsumer(Long::reverse);

            CheckedLongConsumer<IOException> composed = consumer.andThen(after);

            composed.accept(1L);

            verify(consumer).accept(1L);
            verify(consumer).andThen(after);
            verify(after).accept(1L);
            verifyNoMoreInteractions(consumer, after);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedLongConsumer<IOException> consumer = Spied.checkedLongConsumer(l -> {
                throw new IOException(Long.toString(l));
            });
            CheckedLongConsumer<IOException> after = Spied.checkedLongConsumer(Long::reverse);

            CheckedLongConsumer<IOException> composed = consumer.andThen(after);

            IOException thrown = assertThrows(IOException.class, () -> composed.accept(1L));
            assertEquals("1", thrown.getMessage());

            verify(consumer).accept(1L);
            verify(consumer).andThen(after);
            verifyNoMoreInteractions(consumer, after);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedLongConsumer<IOException> consumer = Spied.checkedLongConsumer(l -> {
                throw new IllegalStateException(Long.toString(l));
            });
            CheckedLongConsumer<IOException> after = Spied.checkedLongConsumer(Long::reverse);

            CheckedLongConsumer<IOException> composed = consumer.andThen(after);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> composed.accept(1L));
            assertEquals("1", thrown.getMessage());

            verify(consumer).accept(1L);
            verify(consumer).andThen(after);
            verifyNoMoreInteractions(consumer, after);
        }

        @Test
        void testAfterThrowsChecked() throws IOException {
            CheckedLongConsumer<IOException> consumer = Spied.checkedLongConsumer(Long::toString);
            CheckedLongConsumer<IOException> after = Spied.checkedLongConsumer(l -> {
                throw new IOException(Long.toString(l));
            });

            CheckedLongConsumer<IOException> composed = consumer.andThen(after);

            IOException thrown = assertThrows(IOException.class, () -> composed.accept(1L));
            assertEquals("1", thrown.getMessage());

            verify(consumer).accept(1L);
            verify(consumer).andThen(after);
            verify(after).accept(1L);
            verifyNoMoreInteractions(consumer, after);
        }

        @Test
        void testAfterThrowsUnchecked() throws IOException {
            CheckedLongConsumer<IOException> consumer = Spied.checkedLongConsumer(Long::toString);
            CheckedLongConsumer<IOException> after = Spied.checkedLongConsumer(l -> {
                throw new IllegalStateException(Long.toString(l));
            });

            CheckedLongConsumer<IOException> composed = consumer.andThen(after);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> composed.accept(1L));
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
            CheckedLongConsumer<IOException> consumer = Spied.checkedLongConsumer(Long::toString);

            assertThrows(NullPointerException.class, () -> consumer.onErrorThrowAsChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedLongConsumer<IOException> consumer = Spied.checkedLongConsumer(Long::toString);

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedLongConsumer<ExecutionException> throwing = consumer.onErrorThrowAsChecked(errorMapper);

            throwing.accept(1L);

            verify(consumer).accept(1L);
            verify(consumer).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(consumer, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedLongConsumer<IOException> consumer = Spied.checkedLongConsumer(l -> {
                throw new IOException(Long.toString(l));
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedLongConsumer<ExecutionException> throwing = consumer.onErrorThrowAsChecked(errorMapper);

            ExecutionException thrown = assertThrows(ExecutionException.class, () -> throwing.accept(1L));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1", cause.getMessage());

            verify(consumer).accept(1L);
            verify(consumer).onErrorThrowAsChecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(consumer, errorMapper);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedLongConsumer<IOException> consumer = Spied.checkedLongConsumer(l -> {
                throw new IllegalStateException(Long.toString(l));
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedLongConsumer<ExecutionException> throwing = consumer.onErrorThrowAsChecked(errorMapper);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> throwing.accept(1L));
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
            CheckedLongConsumer<IOException> consumer = Spied.checkedLongConsumer(Long::toString);

            assertThrows(NullPointerException.class, () -> consumer.onErrorThrowAsUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedLongConsumer<IOException> consumer = Spied.checkedLongConsumer(Long::toString);

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            LongConsumer throwing = consumer.onErrorThrowAsUnchecked(errorMapper);

            throwing.accept(1L);

            verify(consumer).accept(1L);
            verify(consumer).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(consumer, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedLongConsumer<IOException> consumer = Spied.checkedLongConsumer(l -> {
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

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedLongConsumer<IOException> consumer = Spied.checkedLongConsumer(l -> {
                throw new IllegalArgumentException(Long.toString(l));
            });

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            LongConsumer throwing = consumer.onErrorThrowAsUnchecked(errorMapper);

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> throwing.accept(1L));
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
            CheckedLongConsumer<IOException> consumer = Spied.checkedLongConsumer(Long::toString);

            assertThrows(NullPointerException.class, () -> consumer.onErrorHandleChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedLongConsumer<IOException> consumer = Spied.checkedLongConsumer(Long::toString);

            CheckedConsumer<IOException, ExecutionException> errorHandler = Spied.checkedConsumer(Exception::getMessage);

            CheckedLongConsumer<ExecutionException> handling = consumer.onErrorHandleChecked(errorHandler);

            handling.accept(1L);

            verify(consumer).accept(1L);
            verify(consumer).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(consumer, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException, ExecutionException {
                CheckedLongConsumer<IOException> consumer = Spied.checkedLongConsumer(l -> {
                    throw new IOException(Long.toString(l));
                });

                CheckedConsumer<IOException, ExecutionException> errorHandler = Spied.checkedConsumer(Exception::getMessage);

                CheckedLongConsumer<ExecutionException> handling = consumer.onErrorHandleChecked(errorHandler);

                handling.accept(1L);

                verify(consumer).accept(1L);
                verify(consumer).onErrorHandleChecked(errorHandler);
                verify(errorHandler).accept(any());
                verifyNoMoreInteractions(consumer, errorHandler);
            }

            @Test
            void testHandlerThrowsChecked() throws IOException, ExecutionException {
                CheckedLongConsumer<IOException> consumer = Spied.checkedLongConsumer(l -> {
                    throw new IOException(Long.toString(l));
                });

                CheckedConsumer<IOException, ExecutionException> errorHandler = Spied.checkedConsumer(e -> {
                    throw new ExecutionException(e);
                });

                CheckedLongConsumer<ExecutionException> handling = consumer.onErrorHandleChecked(errorHandler);

                ExecutionException thrown = assertThrows(ExecutionException.class, () -> handling.accept(1L));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("1", cause.getMessage());

                verify(consumer).accept(1L);
                verify(consumer).onErrorHandleChecked(errorHandler);
                verify(errorHandler).accept(any());
                verifyNoMoreInteractions(consumer, errorHandler);
            }

            @Test
            void testHandlerThrowsUnchecked() throws IOException, ExecutionException {
                CheckedLongConsumer<IOException> consumer = Spied.checkedLongConsumer(l -> {
                    throw new IOException(Long.toString(l));
                });

                CheckedConsumer<IOException, ExecutionException> errorHandler = Spied.checkedConsumer(e -> {
                    throw new IllegalStateException(e);
                });

                CheckedLongConsumer<ExecutionException> handling = consumer.onErrorHandleChecked(errorHandler);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.accept(1L));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("1", cause.getMessage());

                verify(consumer).accept(1L);
                verify(consumer).onErrorHandleChecked(errorHandler);
                verify(errorHandler).accept(any());
                verifyNoMoreInteractions(consumer, errorHandler);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedLongConsumer<IOException> consumer = Spied.checkedLongConsumer(l -> {
                throw new IllegalStateException(Long.toString(l));
            });

            CheckedConsumer<IOException, ExecutionException> errorHandler = Spied.checkedConsumer(Exception::getMessage);

            CheckedLongConsumer<ExecutionException> handling = consumer.onErrorHandleChecked(errorHandler);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.accept(1L));
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
            CheckedLongConsumer<IOException> consumer = Spied.checkedLongConsumer(Long::toString);

            assertThrows(NullPointerException.class, () -> consumer.onErrorHandleUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedLongConsumer<IOException> consumer = Spied.checkedLongConsumer(Long::toString);

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
                CheckedLongConsumer<IOException> consumer = Spied.checkedLongConsumer(l -> {
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

            @Test
            void testHandlerThrowsUnchecked() throws IOException {
                CheckedLongConsumer<IOException> consumer = Spied.checkedLongConsumer(l -> {
                    throw new IOException(Long.toString(l));
                });

                Consumer<IOException> errorHandler = Spied.consumer(e -> {
                    throw new IllegalStateException(e);
                });

                LongConsumer handling = consumer.onErrorHandleUnchecked(errorHandler);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.accept(1L));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("1", cause.getMessage());

                verify(consumer).accept(1L);
                verify(consumer).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).accept(any());
                verifyNoMoreInteractions(consumer, errorHandler);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedLongConsumer<IOException> consumer = Spied.checkedLongConsumer(l -> {
                throw new IllegalStateException(Long.toString(l));
            });

            Consumer<IOException> errorHandler = Spied.consumer(Exception::getMessage);

            LongConsumer handling = consumer.onErrorHandleUnchecked(errorHandler);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.accept(1L));
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
            CheckedLongConsumer<IOException> consumer = Spied.checkedLongConsumer(Long::toString);

            assertThrows(NullPointerException.class, () -> consumer.onErrorAcceptChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedLongConsumer<IOException> consumer = Spied.checkedLongConsumer(Long::toString);

            CheckedLongConsumer<ExecutionException> fallback = Spied.checkedLongConsumer(Long::reverse);

            CheckedLongConsumer<ExecutionException> applying = consumer.onErrorAcceptChecked(fallback);

            applying.accept(1L);

            verify(consumer).accept(1L);
            verify(consumer).onErrorAcceptChecked(fallback);
            verifyNoMoreInteractions(consumer, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                CheckedLongConsumer<IOException> consumer = Spied.checkedLongConsumer(l -> {
                    throw new IOException(Long.toString(l));
                });

                CheckedLongConsumer<ParseException> fallback = Spied.checkedLongConsumer(Long::reverse);

                CheckedLongConsumer<ParseException> applying = consumer.onErrorAcceptChecked(fallback);

                applying.accept(1L);

                verify(consumer).accept(1L);
                verify(consumer).onErrorAcceptChecked(fallback);
                verify(fallback).accept(1L);
                verifyNoMoreInteractions(consumer, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                CheckedLongConsumer<IOException> consumer = Spied.checkedLongConsumer(l -> {
                    throw new IOException(Long.toString(l));
                });

                CheckedLongConsumer<ParseException> fallback = Spied.checkedLongConsumer(l -> {
                    throw new ParseException(Long.toString(l), 0);
                });

                CheckedLongConsumer<ParseException> applying = consumer.onErrorAcceptChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> applying.accept(1L));
                assertEquals("1", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(consumer).accept(1L);
                verify(consumer).onErrorAcceptChecked(fallback);
                verify(fallback).accept(1L);
                verifyNoMoreInteractions(consumer, fallback);
            }

            @Test
            void testFallbackThrowsUnchecked() throws IOException, ParseException {
                CheckedLongConsumer<IOException> consumer = Spied.checkedLongConsumer(l -> {
                    throw new IOException(Long.toString(l));
                });

                CheckedLongConsumer<ParseException> fallback = Spied.checkedLongConsumer(l -> {
                    throw new IllegalStateException(Long.toString(l));
                });

                CheckedLongConsumer<ParseException> applying = consumer.onErrorAcceptChecked(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> applying.accept(1L));
                assertEquals("1", thrown.getMessage());

                verify(consumer).accept(1L);
                verify(consumer).onErrorAcceptChecked(fallback);
                verify(fallback).accept(1L);
                verifyNoMoreInteractions(consumer, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedLongConsumer<IOException> consumer = Spied.checkedLongConsumer(l -> {
                throw new IllegalStateException(Long.toString(l));
            });

            CheckedLongConsumer<ParseException> fallback = Spied.checkedLongConsumer(Long::reverse);

            CheckedLongConsumer<ParseException> applying = consumer.onErrorAcceptChecked(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> applying.accept(1L));
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
            CheckedLongConsumer<IOException> consumer = Spied.checkedLongConsumer(Long::toString);

            assertThrows(NullPointerException.class, () -> consumer.onErrorAcceptUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedLongConsumer<IOException> consumer = Spied.checkedLongConsumer(Long::toString);

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
                CheckedLongConsumer<IOException> consumer = Spied.checkedLongConsumer(l -> {
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

            @Test
            void testFallbackThrowsUnchecked() throws IOException {
                CheckedLongConsumer<IOException> consumer = Spied.checkedLongConsumer(l -> {
                    throw new IOException(Long.toString(l));
                });

                LongConsumer fallback = Spied.longConsumer(l -> {
                    throw new IllegalStateException(Long.toString(l));
                });

                LongConsumer applying = consumer.onErrorAcceptUnchecked(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> applying.accept(1L));
                assertEquals("1", thrown.getMessage());

                verify(consumer).accept(1L);
                verify(consumer).onErrorAcceptUnchecked(fallback);
                verify(fallback).accept(1L);
                verifyNoMoreInteractions(consumer, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedLongConsumer<IOException> consumer = Spied.checkedLongConsumer(l -> {
                throw new IllegalStateException(Long.toString(l));
            });

            LongConsumer fallback = Spied.longConsumer(Long::reverse);

            LongConsumer applying = consumer.onErrorAcceptUnchecked(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> applying.accept(1L));
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
            CheckedLongConsumer<IOException> consumer = Spied.checkedLongConsumer(Long::toString);

            LongConsumer discarding = consumer.onErrorDiscard();

            discarding.accept(1L);

            verify(consumer).accept(1L);
            verify(consumer).onErrorDiscard();
            verifyNoMoreInteractions(consumer);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedLongConsumer<IOException> consumer = Spied.checkedLongConsumer(l -> {
                throw new IOException(Long.toString(l));
            });

            LongConsumer discarding = consumer.onErrorDiscard();

            discarding.accept(1L);

            verify(consumer).accept(1L);
            verify(consumer).onErrorDiscard();
            verifyNoMoreInteractions(consumer);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedLongConsumer<IOException> consumer = Spied.checkedLongConsumer(l -> {
                throw new IllegalStateException(Long.toString(l));
            });

            LongConsumer discarding = consumer.onErrorDiscard();

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> discarding.accept(1L));
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
            CheckedLongConsumer<IOException> consumer = Spied.checkedLongConsumer(Long::toString);

            LongConsumer unchecked = consumer.unchecked();

            unchecked.accept(1L);

            verify(consumer).accept(1L);
            verify(consumer).unchecked();
            verify(consumer).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(consumer);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedLongConsumer<IOException> consumer = Spied.checkedLongConsumer(l -> {
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

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedLongConsumer<IOException> consumer = Spied.checkedLongConsumer(l -> {
                throw new IllegalArgumentException(Long.toString(l));
            });

            LongConsumer unchecked = consumer.unchecked();

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> unchecked.accept(1L));
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
            assertThrows(NullPointerException.class, () -> CheckedLongConsumer.of(null));
        }

        @Test
        void testNonNullArgument() throws IOException {
            CheckedLongConsumer<IOException> delegate = Spied.checkedLongConsumer(Long::toString);

            CheckedLongConsumer<IOException> consumer = CheckedLongConsumer.of(delegate);

            consumer.accept(1L);

            verify(delegate).accept(1L);
            verifyNoMoreInteractions(delegate);
        }
    }

    @Nested
    class UncheckedFactory {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> CheckedLongConsumer.unchecked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedLongConsumer<IOException> consumer = Spied.checkedLongConsumer(Long::toString);

            LongConsumer unchecked = CheckedLongConsumer.unchecked(consumer);

            unchecked.accept(1L);

            verify(consumer).unchecked();
            verify(consumer).onErrorThrowAsUnchecked(any());
            verify(consumer).accept(1L);
            verifyNoMoreInteractions(consumer);
        }

        @Test
        void testArgumentThrowsChecked() throws IOException {
            CheckedLongConsumer<IOException> consumer = Spied.checkedLongConsumer(l -> {
                throw new IOException(Long.toString(l));
            });

            LongConsumer unchecked = CheckedLongConsumer.unchecked(consumer);

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.accept(1L));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1", cause.getMessage());

            verify(consumer).unchecked();
            verify(consumer).onErrorThrowAsUnchecked(any());
            verify(consumer).accept(1L);
            verifyNoMoreInteractions(consumer);
        }

        @Test
        void testArgumentThrowsUnchecked() throws IOException {
            CheckedLongConsumer<IOException> consumer = Spied.checkedLongConsumer(l -> {
                throw new IllegalArgumentException(Long.toString(l));
            });

            LongConsumer unchecked = CheckedLongConsumer.unchecked(consumer);

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> unchecked.accept(1L));
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
            assertThrows(NullPointerException.class, () -> CheckedLongConsumer.checked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            LongConsumer delegate = Spied.longConsumer(Long::toString);

            CheckedLongConsumer<IOException> consumer = CheckedLongConsumer.checked(delegate);

            consumer.accept(1L);

            verify(delegate).accept(1L);
            verifyNoMoreInteractions(delegate);
        }

        @Test
        void testArgumentThrowsUnchecked() {
            CheckedLongConsumer<IOException> consumer = CheckedLongConsumer.checked(l -> {
                throw new UncheckedException(Long.toString(l), new IOException());
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

            assertThrows(NullPointerException.class, () -> CheckedLongConsumer.checked(null, IOException.class));
            assertThrows(NullPointerException.class, () -> CheckedLongConsumer.checked(consumer, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            LongConsumer delegate = Spied.longConsumer(Long::toString);

            CheckedLongConsumer<IOException> consumer = CheckedLongConsumer.checked(delegate, IOException.class);

            consumer.accept(1L);

            verify(delegate).accept(1L);
            verifyNoMoreInteractions(delegate);
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                CheckedLongConsumer<IOException> consumer = CheckedLongConsumer.checked(l -> {
                    throw new UncheckedException(new IOException(Long.toString(l)));
                }, IOException.class);

                IOException thrown = assertThrows(IOException.class, () -> consumer.accept(1L));
                assertEquals("1", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                CheckedLongConsumer<IOException> consumer = CheckedLongConsumer.checked(l -> {
                    throw new UncheckedException(new FileNotFoundException(Long.toString(l)));
                }, IOException.class);

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class, () -> consumer.accept(1L));
                assertEquals("1", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                CheckedLongConsumer<IOException> consumer = CheckedLongConsumer.checked(l -> {
                    throw new UncheckedException(new ParseException(Long.toString(l), 0));
                }, IOException.class);

                UncheckedException thrown = assertThrows(UncheckedException.class, () -> consumer.accept(1L));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("1", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            CheckedLongConsumer<IOException> consumer = CheckedLongConsumer.checked(l -> {
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

            assertThrows(NullPointerException.class, () -> CheckedLongConsumer.invokeAndUnwrap(null, 1L, IOException.class));
            assertThrows(NullPointerException.class, () -> CheckedLongConsumer.invokeAndUnwrap(consumer, 1L, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            LongConsumer consumer = Spied.longConsumer(Long::toString);

            CheckedLongConsumer.invokeAndUnwrap(consumer, 1L, IOException.class);

            verify(consumer).accept(1L);
            verifyNoMoreInteractions(consumer);
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                LongConsumer consumer = l -> {
                    throw new UncheckedException(new IOException(Long.toString(l)));
                };

                IOException thrown = assertThrows(IOException.class, () -> CheckedLongConsumer.invokeAndUnwrap(consumer, 1L, IOException.class));
                assertEquals("1", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                LongConsumer consumer = l -> {
                    throw new UncheckedException(new FileNotFoundException(Long.toString(l)));
                };

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class,
                        () -> CheckedLongConsumer.invokeAndUnwrap(consumer, 1L, IOException.class));
                assertEquals("1", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                LongConsumer consumer = l -> {
                    throw new UncheckedException(new ParseException(Long.toString(l), 0));
                };

                UncheckedException thrown = assertThrows(UncheckedException.class,
                        () -> CheckedLongConsumer.invokeAndUnwrap(consumer, 1L, IOException.class));
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
                    () -> CheckedLongConsumer.invokeAndUnwrap(consumer, 1L, IOException.class));
            assertEquals("1", thrown.getMessage());
        }
    }
}
