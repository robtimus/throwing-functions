/*
 * CheckedBiConsumerTest.java
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
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("nls")
class CheckedBiConsumerTest {

    @Nested
    class AndThen {

        @Test
        void testNullArgument() {
            CheckedBiConsumer<String, String, IOException> consumer = Spied.checkedBiConsumer(String::concat);

            assertThrows(NullPointerException.class, () -> consumer.andThen(null));
        }

        @Test
        void testNeitherThrows() throws IOException {
            CheckedBiConsumer<String, String, IOException> consumer = Spied.checkedBiConsumer(String::concat);
            CheckedBiConsumer<String, String, IOException> after = Spied.checkedBiConsumer(Objects::equals);

            CheckedBiConsumer<String, String, IOException> composed = consumer.andThen(after);

            composed.accept("foo", "bar");

            verify(consumer).accept("foo", "bar");
            verify(consumer).andThen(after);
            verify(after).accept("foo", "bar");
            verifyNoMoreInteractions(consumer, after);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedBiConsumer<String, String, IOException> consumer = Spied.checkedBiConsumer((s1, s2) -> {
                throw new IOException(s1 + s2);
            });
            CheckedBiConsumer<String, String, IOException> after = Spied.checkedBiConsumer(Objects::equals);

            CheckedBiConsumer<String, String, IOException> composed = consumer.andThen(after);

            IOException thrown = assertThrows(IOException.class, () -> composed.accept("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());

            verify(consumer).accept("foo", "bar");
            verify(consumer).andThen(after);
            verifyNoMoreInteractions(consumer, after);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedBiConsumer<String, String, IOException> consumer = Spied.checkedBiConsumer((s1, s2) -> {
                throw new IllegalStateException(s1 + s2);
            });
            CheckedBiConsumer<String, String, IOException> after = Spied.checkedBiConsumer(Objects::equals);

            CheckedBiConsumer<String, String, IOException> composed = consumer.andThen(after);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> composed.accept("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());

            verify(consumer).accept("foo", "bar");
            verify(consumer).andThen(after);
            verifyNoMoreInteractions(consumer, after);
        }

        @Test
        void testAfterThrowsChecked() throws IOException {
            CheckedBiConsumer<String, String, IOException> consumer = Spied.checkedBiConsumer(String::concat);
            CheckedBiConsumer<String, String, IOException> after = Spied.checkedBiConsumer((s1, s2) -> {
                throw new IOException(s1 + s2);
            });

            CheckedBiConsumer<String, String, IOException> composed = consumer.andThen(after);

            IOException thrown = assertThrows(IOException.class, () -> composed.accept("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());

            verify(consumer).accept("foo", "bar");
            verify(consumer).andThen(after);
            verify(after).accept("foo", "bar");
            verifyNoMoreInteractions(consumer, after);
        }

        @Test
        void testAfterThrowsUnchecked() throws IOException {
            CheckedBiConsumer<String, String, IOException> consumer = Spied.checkedBiConsumer(String::concat);
            CheckedBiConsumer<String, String, IOException> after = Spied.checkedBiConsumer((s1, s2) -> {
                throw new IllegalStateException(s1 + s2);
            });

            CheckedBiConsumer<String, String, IOException> composed = consumer.andThen(after);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> composed.accept("foo", "bar"));
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
            CheckedBiConsumer<String, String, IOException> consumer = Spied.checkedBiConsumer(String::concat);

            assertThrows(NullPointerException.class, () -> consumer.onErrorThrowAsChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedBiConsumer<String, String, IOException> consumer = Spied.checkedBiConsumer(String::concat);

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedBiConsumer<String, String, ExecutionException> throwing = consumer.onErrorThrowAsChecked(errorMapper);

            throwing.accept("foo", "bar");

            verify(consumer).accept("foo", "bar");
            verify(consumer).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(consumer, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedBiConsumer<String, String, IOException> consumer = Spied.checkedBiConsumer((s1, s2) -> {
                throw new IOException(s1 + s2);
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedBiConsumer<String, String, ExecutionException> throwing = consumer.onErrorThrowAsChecked(errorMapper);

            ExecutionException thrown = assertThrows(ExecutionException.class, () -> throwing.accept("foo", "bar"));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foobar", cause.getMessage());

            verify(consumer).accept("foo", "bar");
            verify(consumer).onErrorThrowAsChecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(consumer, errorMapper);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedBiConsumer<String, String, IOException> consumer = Spied.checkedBiConsumer((s1, s2) -> {
                throw new IllegalStateException(s1 + s2);
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedBiConsumer<String, String, ExecutionException> throwing = consumer.onErrorThrowAsChecked(errorMapper);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> throwing.accept("foo", "bar"));
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
            CheckedBiConsumer<String, String, IOException> consumer = Spied.checkedBiConsumer(String::concat);

            assertThrows(NullPointerException.class, () -> consumer.onErrorThrowAsUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedBiConsumer<String, String, IOException> consumer = Spied.checkedBiConsumer(String::concat);

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            BiConsumer<String, String> throwing = consumer.onErrorThrowAsUnchecked(errorMapper);

            throwing.accept("foo", "bar");

            verify(consumer).accept("foo", "bar");
            verify(consumer).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(consumer, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedBiConsumer<String, String, IOException> consumer = Spied.checkedBiConsumer((s1, s2) -> {
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

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedBiConsumer<String, String, IOException> consumer = Spied.checkedBiConsumer((s1, s2) -> {
                throw new IllegalArgumentException(s1 + s2);
            });

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            BiConsumer<String, String> throwing = consumer.onErrorThrowAsUnchecked(errorMapper);

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> throwing.accept("foo", "bar"));
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
            CheckedBiConsumer<String, String, IOException> consumer = Spied.checkedBiConsumer(String::concat);

            assertThrows(NullPointerException.class, () -> consumer.onErrorHandleChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedBiConsumer<String, String, IOException> consumer = Spied.checkedBiConsumer(String::concat);

            CheckedConsumer<IOException, ExecutionException> errorHandler = Spied.checkedConsumer(Exception::getMessage);

            CheckedBiConsumer<String, String, ExecutionException> handling = consumer.onErrorHandleChecked(errorHandler);

            handling.accept("foo", "bar");

            verify(consumer).accept("foo", "bar");
            verify(consumer).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(consumer, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException, ExecutionException {
                CheckedBiConsumer<String, String, IOException> consumer = Spied.checkedBiConsumer((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                CheckedConsumer<IOException, ExecutionException> errorHandler = Spied.checkedConsumer(Exception::getMessage);

                CheckedBiConsumer<String, String, ExecutionException> handling = consumer.onErrorHandleChecked(errorHandler);

                handling.accept("foo", "bar");

                verify(consumer).accept("foo", "bar");
                verify(consumer).onErrorHandleChecked(errorHandler);
                verify(errorHandler).accept(any());
                verifyNoMoreInteractions(consumer, errorHandler);
            }

            @Test
            void testHandlerThrowsChecked() throws IOException, ExecutionException {
                CheckedBiConsumer<String, String, IOException> consumer = Spied.checkedBiConsumer((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                CheckedConsumer<IOException, ExecutionException> errorHandler = Spied.checkedConsumer(e -> {
                    throw new ExecutionException(e);
                });

                CheckedBiConsumer<String, String, ExecutionException> handling = consumer.onErrorHandleChecked(errorHandler);

                ExecutionException thrown = assertThrows(ExecutionException.class, () -> handling.accept("foo", "bar"));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foobar", cause.getMessage());

                verify(consumer).accept("foo", "bar");
                verify(consumer).onErrorHandleChecked(errorHandler);
                verify(errorHandler).accept(any());
                verifyNoMoreInteractions(consumer, errorHandler);
            }

            @Test
            void testHandlerThrowsUnchecked() throws IOException, ExecutionException {
                CheckedBiConsumer<String, String, IOException> consumer = Spied.checkedBiConsumer((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                CheckedConsumer<IOException, ExecutionException> errorHandler = Spied.checkedConsumer(e -> {
                    throw new IllegalStateException(e);
                });

                CheckedBiConsumer<String, String, ExecutionException> handling = consumer.onErrorHandleChecked(errorHandler);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.accept("foo", "bar"));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foobar", cause.getMessage());

                verify(consumer).accept("foo", "bar");
                verify(consumer).onErrorHandleChecked(errorHandler);
                verify(errorHandler).accept(any());
                verifyNoMoreInteractions(consumer, errorHandler);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedBiConsumer<String, String, IOException> consumer = Spied.checkedBiConsumer((s1, s2) -> {
                throw new IllegalStateException(s1 + s2);
            });

            CheckedConsumer<IOException, ExecutionException> errorHandler = Spied.checkedConsumer(Exception::getMessage);

            CheckedBiConsumer<String, String, ExecutionException> handling = consumer.onErrorHandleChecked(errorHandler);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.accept("foo", "bar"));
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
            CheckedBiConsumer<String, String, IOException> consumer = Spied.checkedBiConsumer(String::concat);

            assertThrows(NullPointerException.class, () -> consumer.onErrorHandleUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedBiConsumer<String, String, IOException> consumer = Spied.checkedBiConsumer(String::concat);

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
                CheckedBiConsumer<String, String, IOException> consumer = Spied.checkedBiConsumer((s1, s2) -> {
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

            @Test
            void testHandlerThrowsUnchecked() throws IOException {
                CheckedBiConsumer<String, String, IOException> consumer = Spied.checkedBiConsumer((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                Consumer<IOException> errorHandler = Spied.consumer(e -> {
                    throw new IllegalStateException(e);
                });

                BiConsumer<String, String> handling = consumer.onErrorHandleUnchecked(errorHandler);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.accept("foo", "bar"));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foobar", cause.getMessage());

                verify(consumer).accept("foo", "bar");
                verify(consumer).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).accept(any());
                verifyNoMoreInteractions(consumer, errorHandler);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedBiConsumer<String, String, IOException> consumer = Spied.checkedBiConsumer((s1, s2) -> {
                throw new IllegalStateException(s1 + s2);
            });

            Consumer<IOException> errorHandler = Spied.consumer(Exception::getMessage);

            BiConsumer<String, String> handling = consumer.onErrorHandleUnchecked(errorHandler);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.accept("foo", "bar"));
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
            CheckedBiConsumer<String, String, IOException> consumer = Spied.checkedBiConsumer(String::concat);

            assertThrows(NullPointerException.class, () -> consumer.onErrorAcceptChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedBiConsumer<String, String, IOException> consumer = Spied.checkedBiConsumer(String::concat);

            CheckedBiConsumer<String, String, ExecutionException> fallback = Spied.checkedBiConsumer(Objects::equals);

            CheckedBiConsumer<String, String, ExecutionException> applying = consumer.onErrorAcceptChecked(fallback);

            applying.accept("foo", "bar");

            verify(consumer).accept("foo", "bar");
            verify(consumer).onErrorAcceptChecked(fallback);
            verifyNoMoreInteractions(consumer, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                CheckedBiConsumer<String, String, IOException> consumer = Spied.checkedBiConsumer((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                CheckedBiConsumer<String, String, ParseException> fallback = Spied.checkedBiConsumer(Objects::equals);

                CheckedBiConsumer<String, String, ParseException> applying = consumer.onErrorAcceptChecked(fallback);

                applying.accept("foo", "bar");

                verify(consumer).accept("foo", "bar");
                verify(consumer).onErrorAcceptChecked(fallback);
                verify(fallback).accept("foo", "bar");
                verifyNoMoreInteractions(consumer, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                CheckedBiConsumer<String, String, IOException> consumer = Spied.checkedBiConsumer((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                CheckedBiConsumer<String, String, ParseException> fallback = Spied.checkedBiConsumer((s1, s2) -> {
                    throw new ParseException(s1 + s2, 0);
                });

                CheckedBiConsumer<String, String, ParseException> applying = consumer.onErrorAcceptChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> applying.accept("foo", "bar"));
                assertEquals("foobar", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(consumer).accept("foo", "bar");
                verify(consumer).onErrorAcceptChecked(fallback);
                verify(fallback).accept("foo", "bar");
                verifyNoMoreInteractions(consumer, fallback);
            }

            @Test
            void testFallbackThrowsUnchecked() throws IOException, ParseException {
                CheckedBiConsumer<String, String, IOException> consumer = Spied.checkedBiConsumer((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                CheckedBiConsumer<String, String, ParseException> fallback = Spied.checkedBiConsumer((s1, s2) -> {
                    throw new IllegalStateException(s1 + s2);
                });

                CheckedBiConsumer<String, String, ParseException> applying = consumer.onErrorAcceptChecked(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> applying.accept("foo", "bar"));
                assertEquals("foobar", thrown.getMessage());

                verify(consumer).accept("foo", "bar");
                verify(consumer).onErrorAcceptChecked(fallback);
                verify(fallback).accept("foo", "bar");
                verifyNoMoreInteractions(consumer, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedBiConsumer<String, String, IOException> consumer = Spied.checkedBiConsumer((s1, s2) -> {
                throw new IllegalStateException(s1 + s2);
            });

            CheckedBiConsumer<String, String, ParseException> fallback = Spied.checkedBiConsumer(Objects::equals);

            CheckedBiConsumer<String, String, ParseException> applying = consumer.onErrorAcceptChecked(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> applying.accept("foo", "bar"));
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
            CheckedBiConsumer<String, String, IOException> consumer = Spied.checkedBiConsumer(String::concat);

            assertThrows(NullPointerException.class, () -> consumer.onErrorAcceptUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedBiConsumer<String, String, IOException> consumer = Spied.checkedBiConsumer(String::concat);

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
                CheckedBiConsumer<String, String, IOException> consumer = Spied.checkedBiConsumer((s1, s2) -> {
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

            @Test
            void testFallbackThrowsUnchecked() throws IOException {
                CheckedBiConsumer<String, String, IOException> consumer = Spied.checkedBiConsumer((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                BiConsumer<String, String> fallback = Spied.biConsumer((s1, s2) -> {
                    throw new IllegalStateException(s1 + s2);
                });

                BiConsumer<String, String> applying = consumer.onErrorAcceptUnchecked(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> applying.accept("foo", "bar"));
                assertEquals("foobar", thrown.getMessage());

                verify(consumer).accept("foo", "bar");
                verify(consumer).onErrorAcceptUnchecked(fallback);
                verify(fallback).accept("foo", "bar");
                verifyNoMoreInteractions(consumer, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedBiConsumer<String, String, IOException> consumer = Spied.checkedBiConsumer((s1, s2) -> {
                throw new IllegalStateException(s1 + s2);
            });

            BiConsumer<String, String> fallback = Spied.biConsumer(Objects::equals);

            BiConsumer<String, String> applying = consumer.onErrorAcceptUnchecked(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> applying.accept("foo", "bar"));
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
            CheckedBiConsumer<String, String, IOException> consumer = Spied.checkedBiConsumer(String::concat);

            BiConsumer<String, String> discarding = consumer.onErrorDiscard();

            discarding.accept("foo", "bar");

            verify(consumer).accept("foo", "bar");
            verify(consumer).onErrorDiscard();
            verifyNoMoreInteractions(consumer);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedBiConsumer<String, String, IOException> consumer = Spied.checkedBiConsumer((s1, s2) -> {
                throw new IOException(s1 + s2);
            });

            BiConsumer<String, String> discarding = consumer.onErrorDiscard();

            discarding.accept("foo", "bar");

            verify(consumer).accept("foo", "bar");
            verify(consumer).onErrorDiscard();
            verifyNoMoreInteractions(consumer);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedBiConsumer<String, String, IOException> consumer = Spied.checkedBiConsumer((s1, s2) -> {
                throw new IllegalStateException(s1 + s2);
            });

            BiConsumer<String, String> discarding = consumer.onErrorDiscard();

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> discarding.accept("foo", "bar"));
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
            CheckedBiConsumer<String, String, IOException> consumer = Spied.checkedBiConsumer(String::concat);

            BiConsumer<String, String> unchecked = consumer.unchecked();

            unchecked.accept("foo", "bar");

            verify(consumer).accept("foo", "bar");
            verify(consumer).unchecked();
            verify(consumer).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(consumer);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedBiConsumer<String, String, IOException> consumer = Spied.checkedBiConsumer((s1, s2) -> {
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

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedBiConsumer<String, String, IOException> consumer = Spied.checkedBiConsumer((s1, s2) -> {
                throw new IllegalArgumentException(s1 + s2);
            });

            BiConsumer<String, String> unchecked = consumer.unchecked();

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> unchecked.accept("foo", "bar"));
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
            assertThrows(NullPointerException.class, () -> CheckedBiConsumer.of(null));
        }

        @Test
        void testNonNullArgument() throws IOException {
            CheckedBiConsumer<String, String, IOException> delegate = Spied.checkedBiConsumer(String::concat);

            CheckedBiConsumer<String, String, IOException> consumer = CheckedBiConsumer.of(delegate);

            consumer.accept("foo", "bar");

            verify(delegate).accept("foo", "bar");
            verifyNoMoreInteractions(delegate);
        }
    }

    @Nested
    class UncheckedFactory {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> CheckedBiConsumer.unchecked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedBiConsumer<String, String, IOException> consumer = Spied.checkedBiConsumer(String::concat);

            BiConsumer<String, String> unchecked = CheckedBiConsumer.unchecked(consumer);

            unchecked.accept("foo", "bar");

            verify(consumer).accept("foo", "bar");
            verifyNoMoreInteractions(consumer);
        }

        @Test
        void testArgumentThrowsChecked() throws IOException {
            CheckedBiConsumer<String, String, IOException> consumer = Spied.checkedBiConsumer((s1, s2) -> {
                throw new IOException(s1 + s2);
            });

            BiConsumer<String, String> unchecked = CheckedBiConsumer.unchecked(consumer);

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.accept("foo", "bar"));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foobar", cause.getMessage());

            verify(consumer).accept("foo", "bar");
            verifyNoMoreInteractions(consumer);
        }

        @Test
        void testArgumentThrowsUnchecked() throws IOException {
            CheckedBiConsumer<String, String, IOException> consumer = Spied.checkedBiConsumer((s1, s2) -> {
                throw new IllegalArgumentException(s1 + s2);
            });

            BiConsumer<String, String> unchecked = CheckedBiConsumer.unchecked(consumer);

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> unchecked.accept("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());

            verify(consumer).accept("foo", "bar");
            verifyNoMoreInteractions(consumer);
        }
    }

    @Nested
    class CheckedWrapper {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> CheckedBiConsumer.checked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            BiConsumer<String, String> delegate = Spied.biConsumer(String::concat);

            CheckedBiConsumer<String, String, IOException> consumer = CheckedBiConsumer.checked(delegate);

            consumer.accept("foo", "bar");

            verify(delegate).accept("foo", "bar");
            verifyNoMoreInteractions(delegate);
        }

        @Test
        void testargumentThrowsUnchecked() {
            CheckedBiConsumer<String, String, IOException> consumer = CheckedBiConsumer.checked((s1, s2) -> {
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

            assertThrows(NullPointerException.class, () -> CheckedBiConsumer.checked(null, IOException.class));
            assertThrows(NullPointerException.class, () -> CheckedBiConsumer.checked(consumer, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            BiConsumer<String, String> delegate = Spied.biConsumer(String::concat);

            CheckedBiConsumer<String, String, IOException> consumer = CheckedBiConsumer.checked(delegate, IOException.class);

            consumer.accept("foo", "bar");

            verify(delegate).accept("foo", "bar");
            verifyNoMoreInteractions(delegate);
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                CheckedBiConsumer<String, String, IOException> consumer = CheckedBiConsumer.checked((s1, s2) -> {
                    throw new UncheckedException(new IOException(s1 + s2));
                }, IOException.class);

                IOException thrown = assertThrows(IOException.class, () -> consumer.accept("foo", "bar"));
                assertEquals("foobar", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                CheckedBiConsumer<String, String, IOException> consumer = CheckedBiConsumer.checked((s1, s2) -> {
                    throw new UncheckedException(new FileNotFoundException(s1 + s2));
                }, IOException.class);

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class, () -> consumer.accept("foo", "bar"));
                assertEquals("foobar", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                CheckedBiConsumer<String, String, IOException> consumer = CheckedBiConsumer.checked((s1, s2) -> {
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
            CheckedBiConsumer<String, String, IOException> consumer = CheckedBiConsumer.checked((s1, s2) -> {
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

            assertThrows(NullPointerException.class, () -> CheckedBiConsumer.invokeAndUnwrap(null, "foo", "bar", IOException.class));
            assertThrows(NullPointerException.class, () -> CheckedBiConsumer.invokeAndUnwrap(consumer, "foo", "bar", null));

            CheckedBiConsumer.invokeAndUnwrap(consumer, null, "bar", IOException.class);
            CheckedBiConsumer.invokeAndUnwrap(consumer, "foo", null, IOException.class);

            verify(consumer).accept(null, "bar");
            verify(consumer).accept("foo", null);
            verifyNoMoreInteractions(consumer);
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            BiConsumer<String, String> consumer = Spied.biConsumer(String::concat);

            CheckedBiConsumer.invokeAndUnwrap(consumer, "foo", "bar", IOException.class);

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
                        () -> CheckedBiConsumer.invokeAndUnwrap(consumer, "foo", "bar", IOException.class));
                assertEquals("foobar", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                BiConsumer<String, String> consumer = (s1, s2) -> {
                    throw new UncheckedException(new FileNotFoundException(s1 + s2));
                };

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class,
                        () -> CheckedBiConsumer.invokeAndUnwrap(consumer, "foo", "bar", IOException.class));
                assertEquals("foobar", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                BiConsumer<String, String> consumer = (s1, s2) -> {
                    throw new UncheckedException(new ParseException(s1 + s2, 0));
                };

                UncheckedException thrown = assertThrows(UncheckedException.class,
                        () -> CheckedBiConsumer.invokeAndUnwrap(consumer, "foo", "bar", IOException.class));
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
                    () -> CheckedBiConsumer.invokeAndUnwrap(consumer, "foo", "bar", IOException.class));
            assertEquals("foobar", thrown.getMessage());
        }
    }
}
