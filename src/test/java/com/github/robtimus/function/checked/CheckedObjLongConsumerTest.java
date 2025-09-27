/*
 * CheckedObjLongConsumerTest.java
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
import java.util.function.ObjLongConsumer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("nls")
class CheckedObjLongConsumerTest {

    @Nested
    class OnErrorThrowAsChecked {

        @Test
        void testNullArgument() {
            CheckedObjLongConsumer<String, IOException> consumer = Spied.checkedObjLongConsumer(CheckedObjLongConsumerTest::ignore);

            assertThrows(NullPointerException.class, () -> consumer.onErrorThrowAsChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedObjLongConsumer<String, IOException> consumer = Spied.checkedObjLongConsumer(CheckedObjLongConsumerTest::ignore);

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedObjLongConsumer<String, ExecutionException> throwing = consumer.onErrorThrowAsChecked(errorMapper);

            throwing.accept("foo", 1L);

            verify(consumer).accept("foo", 1L);
            verify(consumer).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(consumer, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedObjLongConsumer<String, IOException> consumer = Spied.checkedObjLongConsumer((s, l) -> {
                throw new IOException(s + l);
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedObjLongConsumer<String, ExecutionException> throwing = consumer.onErrorThrowAsChecked(errorMapper);

            ExecutionException thrown = assertThrows(ExecutionException.class, () -> throwing.accept("foo", 1L));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo1", cause.getMessage());

            verify(consumer).accept("foo", 1L);
            verify(consumer).onErrorThrowAsChecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(consumer, errorMapper);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedObjLongConsumer<String, IOException> consumer = Spied.checkedObjLongConsumer((s, l) -> {
                throw new IllegalStateException(s + l);
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedObjLongConsumer<String, ExecutionException> throwing = consumer.onErrorThrowAsChecked(errorMapper);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> throwing.accept("foo", 1L));
            assertEquals("foo1", thrown.getMessage());

            verify(consumer).accept("foo", 1L);
            verify(consumer).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(consumer, errorMapper);
        }
    }

    @Nested
    class OnErrorThrowAsUnchecked {

        @Test
        void testNullArgument() {
            CheckedObjLongConsumer<String, IOException> consumer = Spied.checkedObjLongConsumer(CheckedObjLongConsumerTest::ignore);

            assertThrows(NullPointerException.class, () -> consumer.onErrorThrowAsUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedObjLongConsumer<String, IOException> consumer = Spied.checkedObjLongConsumer(CheckedObjLongConsumerTest::ignore);

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            ObjLongConsumer<String> throwing = consumer.onErrorThrowAsUnchecked(errorMapper);

            throwing.accept("foo", 1L);

            verify(consumer).accept("foo", 1L);
            verify(consumer).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(consumer, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedObjLongConsumer<String, IOException> consumer = Spied.checkedObjLongConsumer((s, l) -> {
                throw new IOException(s + l);
            });

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            ObjLongConsumer<String> throwing = consumer.onErrorThrowAsUnchecked(errorMapper);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> throwing.accept("foo", 1L));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo1", cause.getMessage());

            verify(consumer).accept("foo", 1L);
            verify(consumer).onErrorThrowAsUnchecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(consumer, errorMapper);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedObjLongConsumer<String, IOException> consumer = Spied.checkedObjLongConsumer((s, l) -> {
                throw new IllegalArgumentException(s + l);
            });

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            ObjLongConsumer<String> throwing = consumer.onErrorThrowAsUnchecked(errorMapper);

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> throwing.accept("foo", 1L));
            assertEquals("foo1", thrown.getMessage());

            verify(consumer).accept("foo", 1L);
            verify(consumer).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(consumer, errorMapper);
        }
    }

    @Nested
    class OnErrorHandleChecked {

        @Test
        void testNullArgument() {
            CheckedObjLongConsumer<String, IOException> consumer = Spied.checkedObjLongConsumer(CheckedObjLongConsumerTest::ignore);

            assertThrows(NullPointerException.class, () -> consumer.onErrorHandleChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedObjLongConsumer<String, IOException> consumer = Spied.checkedObjLongConsumer(CheckedObjLongConsumerTest::ignore);

            CheckedConsumer<IOException, ExecutionException> errorHandler = Spied.checkedConsumer(Exception::getMessage);

            CheckedObjLongConsumer<String, ExecutionException> handling = consumer.onErrorHandleChecked(errorHandler);

            handling.accept("foo", 1L);

            verify(consumer).accept("foo", 1L);
            verify(consumer).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(consumer, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException, ExecutionException {
                CheckedObjLongConsumer<String, IOException> consumer = Spied.checkedObjLongConsumer((s, l) -> {
                    throw new IOException(s + l);
                });

                CheckedConsumer<IOException, ExecutionException> errorHandler = Spied.checkedConsumer(Exception::getMessage);

                CheckedObjLongConsumer<String, ExecutionException> handling = consumer.onErrorHandleChecked(errorHandler);

                handling.accept("foo", 1L);

                verify(consumer).accept("foo", 1L);
                verify(consumer).onErrorHandleChecked(errorHandler);
                verify(errorHandler).accept(any());
                verifyNoMoreInteractions(consumer, errorHandler);
            }

            @Test
            void testHandlerThrowsChecked() throws IOException, ExecutionException {
                CheckedObjLongConsumer<String, IOException> consumer = Spied.checkedObjLongConsumer((s, l) -> {
                    throw new IOException(s + l);
                });

                CheckedConsumer<IOException, ExecutionException> errorHandler = Spied.checkedConsumer(e -> {
                    throw new ExecutionException(e);
                });

                CheckedObjLongConsumer<String, ExecutionException> handling = consumer.onErrorHandleChecked(errorHandler);

                ExecutionException thrown = assertThrows(ExecutionException.class, () -> handling.accept("foo", 1L));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foo1", cause.getMessage());

                verify(consumer).accept("foo", 1L);
                verify(consumer).onErrorHandleChecked(errorHandler);
                verify(errorHandler).accept(any());
                verifyNoMoreInteractions(consumer, errorHandler);
            }

            @Test
            void testHandlerThrowsUnchecked() throws IOException, ExecutionException {
                CheckedObjLongConsumer<String, IOException> consumer = Spied.checkedObjLongConsumer((s, l) -> {
                    throw new IOException(s + l);
                });

                CheckedConsumer<IOException, ExecutionException> errorHandler = Spied.checkedConsumer(e -> {
                    throw new IllegalStateException(e);
                });

                CheckedObjLongConsumer<String, ExecutionException> handling = consumer.onErrorHandleChecked(errorHandler);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.accept("foo", 1L));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foo1", cause.getMessage());

                verify(consumer).accept("foo", 1L);
                verify(consumer).onErrorHandleChecked(errorHandler);
                verify(errorHandler).accept(any());
                verifyNoMoreInteractions(consumer, errorHandler);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedObjLongConsumer<String, IOException> consumer = Spied.checkedObjLongConsumer((s, l) -> {
                throw new IllegalStateException(s + l);
            });

            CheckedConsumer<IOException, ExecutionException> errorHandler = Spied.checkedConsumer(Exception::getMessage);

            CheckedObjLongConsumer<String, ExecutionException> handling = consumer.onErrorHandleChecked(errorHandler);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.accept("foo", 1L));
            assertEquals("foo1", thrown.getMessage());

            verify(consumer).accept("foo", 1L);
            verify(consumer).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(consumer, errorHandler);
        }
    }

    @Nested
    class OnErrorHandleUnchecked {

        @Test
        void testNullArgument() {
            CheckedObjLongConsumer<String, IOException> consumer = Spied.checkedObjLongConsumer(CheckedObjLongConsumerTest::ignore);

            assertThrows(NullPointerException.class, () -> consumer.onErrorHandleUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedObjLongConsumer<String, IOException> consumer = Spied.checkedObjLongConsumer(CheckedObjLongConsumerTest::ignore);

            Consumer<IOException> errorHandler = Spied.consumer(Exception::getMessage);

            ObjLongConsumer<String> handling = consumer.onErrorHandleUnchecked(errorHandler);

            handling.accept("foo", 1L);

            verify(consumer).accept("foo", 1L);
            verify(consumer).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(consumer, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException {
                CheckedObjLongConsumer<String, IOException> consumer = Spied.checkedObjLongConsumer((s, l) -> {
                    throw new IOException(s + l);
                });

                Consumer<IOException> errorHandler = Spied.consumer(Exception::getMessage);

                ObjLongConsumer<String> handling = consumer.onErrorHandleUnchecked(errorHandler);

                handling.accept("foo", 1L);

                verify(consumer).accept("foo", 1L);
                verify(consumer).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).accept(any());
                verifyNoMoreInteractions(consumer, errorHandler);
            }

            @Test
            void testHandlerThrowsUnchecked() throws IOException {
                CheckedObjLongConsumer<String, IOException> consumer = Spied.checkedObjLongConsumer((s, l) -> {
                    throw new IOException(s + l);
                });

                Consumer<IOException> errorHandler = Spied.consumer(e -> {
                    throw new IllegalStateException(e);
                });

                ObjLongConsumer<String> handling = consumer.onErrorHandleUnchecked(errorHandler);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.accept("foo", 1L));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foo1", cause.getMessage());

                verify(consumer).accept("foo", 1L);
                verify(consumer).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).accept(any());
                verifyNoMoreInteractions(consumer, errorHandler);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedObjLongConsumer<String, IOException> consumer = Spied.checkedObjLongConsumer((s, l) -> {
                throw new IllegalStateException(s + l);
            });

            Consumer<IOException> errorHandler = Spied.consumer(Exception::getMessage);

            ObjLongConsumer<String> handling = consumer.onErrorHandleUnchecked(errorHandler);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.accept("foo", 1L));
            assertEquals("foo1", thrown.getMessage());

            verify(consumer).accept("foo", 1L);
            verify(consumer).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(consumer, errorHandler);
        }
    }

    @Nested
    class OnErrorAcceptChecked {

        @Test
        void testNullArgument() {
            CheckedObjLongConsumer<String, IOException> consumer = Spied.checkedObjLongConsumer(CheckedObjLongConsumerTest::ignore);

            assertThrows(NullPointerException.class, () -> consumer.onErrorAcceptChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedObjLongConsumer<String, IOException> consumer = Spied.checkedObjLongConsumer(CheckedObjLongConsumerTest::ignore);

            CheckedObjLongConsumer<String, ExecutionException> fallback = Spied.checkedObjLongConsumer(CheckedObjLongConsumerTest::ignore);

            CheckedObjLongConsumer<String, ExecutionException> applying = consumer.onErrorAcceptChecked(fallback);

            applying.accept("foo", 1L);

            verify(consumer).accept("foo", 1L);
            verify(consumer).onErrorAcceptChecked(fallback);
            verifyNoMoreInteractions(consumer, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                CheckedObjLongConsumer<String, IOException> consumer = Spied.checkedObjLongConsumer((s, l) -> {
                    throw new IOException(s + l);
                });

                CheckedObjLongConsumer<String, ParseException> fallback = Spied.checkedObjLongConsumer(CheckedObjLongConsumerTest::ignore);

                CheckedObjLongConsumer<String, ParseException> applying = consumer.onErrorAcceptChecked(fallback);

                applying.accept("foo", 1L);

                verify(consumer).accept("foo", 1L);
                verify(consumer).onErrorAcceptChecked(fallback);
                verify(fallback).accept("foo", 1L);
                verifyNoMoreInteractions(consumer, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                CheckedObjLongConsumer<String, IOException> consumer = Spied.checkedObjLongConsumer((s, l) -> {
                    throw new IOException(s + l);
                });

                CheckedObjLongConsumer<String, ParseException> fallback = Spied.checkedObjLongConsumer((s, l) -> {
                    throw new ParseException(s + l, 0);
                });

                CheckedObjLongConsumer<String, ParseException> applying = consumer.onErrorAcceptChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> applying.accept("foo", 1L));
                assertEquals("foo1", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(consumer).accept("foo", 1L);
                verify(consumer).onErrorAcceptChecked(fallback);
                verify(fallback).accept("foo", 1L);
                verifyNoMoreInteractions(consumer, fallback);
            }

            @Test
            void testFallbackThrowsUnchecked() throws IOException, ParseException {
                CheckedObjLongConsumer<String, IOException> consumer = Spied.checkedObjLongConsumer((s, l) -> {
                    throw new IOException(s + l);
                });

                CheckedObjLongConsumer<String, ParseException> fallback = Spied.checkedObjLongConsumer((s, l) -> {
                    throw new IllegalStateException(s + l);
                });

                CheckedObjLongConsumer<String, ParseException> applying = consumer.onErrorAcceptChecked(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> applying.accept("foo", 1L));
                assertEquals("foo1", thrown.getMessage());

                verify(consumer).accept("foo", 1L);
                verify(consumer).onErrorAcceptChecked(fallback);
                verify(fallback).accept("foo", 1L);
                verifyNoMoreInteractions(consumer, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedObjLongConsumer<String, IOException> consumer = Spied.checkedObjLongConsumer((s, l) -> {
                throw new IllegalStateException(s + l);
            });

            CheckedObjLongConsumer<String, ParseException> fallback = Spied.checkedObjLongConsumer(CheckedObjLongConsumerTest::ignore);

            CheckedObjLongConsumer<String, ParseException> applying = consumer.onErrorAcceptChecked(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> applying.accept("foo", 1L));
            assertEquals("foo1", thrown.getMessage());

            verify(consumer).accept("foo", 1L);
            verify(consumer).onErrorAcceptChecked(fallback);
            verifyNoMoreInteractions(consumer, fallback);
        }
    }

    @Nested
    class OnErrorAcceptUnchecked {

        @Test
        void testNullArgument() {
            CheckedObjLongConsumer<String, IOException> consumer = Spied.checkedObjLongConsumer(CheckedObjLongConsumerTest::ignore);

            assertThrows(NullPointerException.class, () -> consumer.onErrorAcceptUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedObjLongConsumer<String, IOException> consumer = Spied.checkedObjLongConsumer(CheckedObjLongConsumerTest::ignore);

            ObjLongConsumer<String> fallback = Spied.objLongConsumer(CheckedObjLongConsumerTest::ignore);

            ObjLongConsumer<String> applying = consumer.onErrorAcceptUnchecked(fallback);

            applying.accept("foo", 1L);

            verify(consumer).accept("foo", 1L);
            verify(consumer).onErrorAcceptUnchecked(fallback);
            verifyNoMoreInteractions(consumer, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                CheckedObjLongConsumer<String, IOException> consumer = Spied.checkedObjLongConsumer((s, l) -> {
                    throw new IOException(s + l);
                });

                ObjLongConsumer<String> fallback = Spied.objLongConsumer(CheckedObjLongConsumerTest::ignore);

                ObjLongConsumer<String> applying = consumer.onErrorAcceptUnchecked(fallback);

                applying.accept("foo", 1L);

                verify(consumer).accept("foo", 1L);
                verify(consumer).onErrorAcceptUnchecked(fallback);
                verify(fallback).accept("foo", 1L);
                verifyNoMoreInteractions(consumer, fallback);
            }

            @Test
            void testFallbackThrowsUnchecked() throws IOException {
                CheckedObjLongConsumer<String, IOException> consumer = Spied.checkedObjLongConsumer((s, l) -> {
                    throw new IOException(s + l);
                });

                ObjLongConsumer<String> fallback = Spied.objLongConsumer((s, l) -> {
                    throw new IllegalStateException(s + l);
                });

                ObjLongConsumer<String> applying = consumer.onErrorAcceptUnchecked(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> applying.accept("foo", 1L));
                assertEquals("foo1", thrown.getMessage());

                verify(consumer).accept("foo", 1L);
                verify(consumer).onErrorAcceptUnchecked(fallback);
                verify(fallback).accept("foo", 1L);
                verifyNoMoreInteractions(consumer, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedObjLongConsumer<String, IOException> consumer = Spied.checkedObjLongConsumer((s, l) -> {
                throw new IllegalStateException(s + l);
            });

            ObjLongConsumer<String> fallback = Spied.objLongConsumer(CheckedObjLongConsumerTest::ignore);

            ObjLongConsumer<String> applying = consumer.onErrorAcceptUnchecked(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> applying.accept("foo", 1L));
            assertEquals("foo1", thrown.getMessage());

            verify(consumer).accept("foo", 1L);
            verify(consumer).onErrorAcceptUnchecked(fallback);
            verifyNoMoreInteractions(consumer, fallback);
        }
    }

    @Nested
    class OnErrorDiscard {

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedObjLongConsumer<String, IOException> consumer = Spied.checkedObjLongConsumer(CheckedObjLongConsumerTest::ignore);

            ObjLongConsumer<String> discarding = consumer.onErrorDiscard();

            discarding.accept("foo", 1L);

            verify(consumer).accept("foo", 1L);
            verify(consumer).onErrorDiscard();
            verifyNoMoreInteractions(consumer);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedObjLongConsumer<String, IOException> consumer = Spied.checkedObjLongConsumer((s, l) -> {
                throw new IOException(s + l);
            });

            ObjLongConsumer<String> discarding = consumer.onErrorDiscard();

            discarding.accept("foo", 1L);

            verify(consumer).accept("foo", 1L);
            verify(consumer).onErrorDiscard();
            verifyNoMoreInteractions(consumer);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedObjLongConsumer<String, IOException> consumer = Spied.checkedObjLongConsumer((s, l) -> {
                throw new IllegalStateException(s + l);
            });

            ObjLongConsumer<String> discarding = consumer.onErrorDiscard();

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> discarding.accept("foo", 1L));
            assertEquals("foo1", thrown.getMessage());

            verify(consumer).accept("foo", 1L);
            verify(consumer).onErrorDiscard();
            verifyNoMoreInteractions(consumer);
        }
    }

    @Nested
    class Unchecked {

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedObjLongConsumer<String, IOException> consumer = Spied.checkedObjLongConsumer(CheckedObjLongConsumerTest::ignore);

            ObjLongConsumer<String> unchecked = consumer.unchecked();

            unchecked.accept("foo", 1L);

            verify(consumer).accept("foo", 1L);
            verify(consumer).unchecked();
            verify(consumer).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(consumer);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedObjLongConsumer<String, IOException> consumer = Spied.checkedObjLongConsumer((s, l) -> {
                throw new IOException(s + l);
            });

            ObjLongConsumer<String> unchecked = consumer.unchecked();

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.accept("foo", 1L));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo1", cause.getMessage());

            verify(consumer).accept("foo", 1L);
            verify(consumer).unchecked();
            verify(consumer).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(consumer);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedObjLongConsumer<String, IOException> consumer = Spied.checkedObjLongConsumer((s, l) -> {
                throw new IllegalArgumentException(s + l);
            });

            ObjLongConsumer<String> unchecked = consumer.unchecked();

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> unchecked.accept("foo", 1L));
            assertEquals("foo1", thrown.getMessage());

            verify(consumer).accept("foo", 1L);
            verify(consumer).unchecked();
            verify(consumer).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(consumer);
        }
    }

    @Nested
    class Of {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> CheckedObjLongConsumer.of(null));
        }

        @Test
        void testNonNullArgument() throws IOException {
            CheckedObjLongConsumer<String, IOException> delegate = Spied.checkedObjLongConsumer(CheckedObjLongConsumerTest::ignore);

            CheckedObjLongConsumer<String, IOException> consumer = CheckedObjLongConsumer.of(delegate);

            consumer.accept("foo", 1L);

            verify(delegate).accept("foo", 1L);
            verifyNoMoreInteractions(delegate);
        }
    }

    @Nested
    class UncheckedFactory {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> CheckedObjLongConsumer.unchecked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedObjLongConsumer<String, IOException> consumer = Spied.checkedObjLongConsumer(CheckedObjLongConsumerTest::ignore);

            ObjLongConsumer<String> unchecked = CheckedObjLongConsumer.unchecked(consumer);

            unchecked.accept("foo", 1L);

            verify(consumer).unchecked();
            verify(consumer).onErrorThrowAsUnchecked(any());
            verify(consumer).accept("foo", 1L);
            verifyNoMoreInteractions(consumer);
        }

        @Test
        void testArgumentThrowsChecked() throws IOException {
            CheckedObjLongConsumer<String, IOException> consumer = Spied.checkedObjLongConsumer((s, l) -> {
                throw new IOException(s + l);
            });

            ObjLongConsumer<String> unchecked = CheckedObjLongConsumer.unchecked(consumer);

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.accept("foo", 1L));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo1", cause.getMessage());

            verify(consumer).unchecked();
            verify(consumer).onErrorThrowAsUnchecked(any());
            verify(consumer).accept("foo", 1L);
            verifyNoMoreInteractions(consumer);
        }

        @Test
        void testArgumentThrowsUnchecked() throws IOException {
            CheckedObjLongConsumer<String, IOException> consumer = Spied.checkedObjLongConsumer((s, l) -> {
                throw new IllegalArgumentException(s + l);
            });

            ObjLongConsumer<String> unchecked = CheckedObjLongConsumer.unchecked(consumer);

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> unchecked.accept("foo", 1L));
            assertEquals("foo1", thrown.getMessage());

            verify(consumer).unchecked();
            verify(consumer).onErrorThrowAsUnchecked(any());
            verify(consumer).accept("foo", 1L);
            verifyNoMoreInteractions(consumer);
        }
    }

    @Nested
    class CheckedWrapper {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> CheckedObjLongConsumer.checked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ObjLongConsumer<String> delegate = Spied.objLongConsumer(CheckedObjLongConsumerTest::ignore);

            CheckedObjLongConsumer<String, IOException> consumer = CheckedObjLongConsumer.checked(delegate);

            consumer.accept("foo", 1L);

            verify(delegate).accept("foo", 1L);
            verifyNoMoreInteractions(delegate);
        }

        @Test
        void testArgumentThrowsUnchecked() {
            CheckedObjLongConsumer<String, IOException> consumer = CheckedObjLongConsumer.checked((s, l) -> {
                throw new UncheckedException(s + l, new IOException());
            });

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> consumer.accept("foo", 1L));
            assertEquals("foo1", thrown.getMessage());
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertNull(cause.getMessage());
        }
    }

    @Nested
    class CheckedUnwrapper {

        @Test
        void testNullArguments() {
            ObjLongConsumer<String> consumer = CheckedObjLongConsumerTest::ignore;

            assertThrows(NullPointerException.class, () -> CheckedObjLongConsumer.checked(null, IOException.class));
            assertThrows(NullPointerException.class, () -> CheckedObjLongConsumer.checked(consumer, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ObjLongConsumer<String> delegate = Spied.objLongConsumer(CheckedObjLongConsumerTest::ignore);

            CheckedObjLongConsumer<String, IOException> consumer = CheckedObjLongConsumer.checked(delegate, IOException.class);

            consumer.accept("foo", 1L);

            verify(delegate).accept("foo", 1L);
            verifyNoMoreInteractions(delegate);
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                CheckedObjLongConsumer<String, IOException> consumer = CheckedObjLongConsumer.checked((s, l) -> {
                    throw new UncheckedException(new IOException(s + l));
                }, IOException.class);

                IOException thrown = assertThrows(IOException.class, () -> consumer.accept("foo", 1L));
                assertEquals("foo1", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                CheckedObjLongConsumer<String, IOException> consumer = CheckedObjLongConsumer.checked((s, l) -> {
                    throw new UncheckedException(new FileNotFoundException(s + l));
                }, IOException.class);

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class, () -> consumer.accept("foo", 1L));
                assertEquals("foo1", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                CheckedObjLongConsumer<String, IOException> consumer = CheckedObjLongConsumer.checked((s, l) -> {
                    throw new UncheckedException(new ParseException(s + l, 0));
                }, IOException.class);

                UncheckedException thrown = assertThrows(UncheckedException.class, () -> consumer.accept("foo", 1L));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("foo1", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            CheckedObjLongConsumer<String, IOException> consumer = CheckedObjLongConsumer.checked((s, l) -> {
                throw new IllegalStateException(s + l);
            }, IOException.class);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> consumer.accept("foo", 1L));
            assertEquals("foo1", thrown.getMessage());
        }
    }

    @Nested
    class InvokeAndUnwrap {

        @Test
        void testNullArguments() throws IOException {
            ObjLongConsumer<String> consumer = Spied.objLongConsumer(CheckedObjLongConsumerTest::ignore);

            assertThrows(NullPointerException.class, () -> CheckedObjLongConsumer.invokeAndUnwrap(null, "foo", 1L, IOException.class));
            assertThrows(NullPointerException.class, () -> CheckedObjLongConsumer.invokeAndUnwrap(consumer, "foo", 1L, null));

            CheckedObjLongConsumer.invokeAndUnwrap(consumer, null, 1L, IOException.class);

            verify(consumer).accept(null, 1L);
            verifyNoMoreInteractions(consumer);
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ObjLongConsumer<String> consumer = Spied.objLongConsumer(CheckedObjLongConsumerTest::ignore);

            CheckedObjLongConsumer.invokeAndUnwrap(consumer, "foo", 1L, IOException.class);

            verify(consumer).accept("foo", 1L);
            verifyNoMoreInteractions(consumer);
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                ObjLongConsumer<String> consumer = (s, l) -> {
                    throw new UncheckedException(new IOException(s + l));
                };

                IOException thrown = assertThrows(IOException.class,
                        () -> CheckedObjLongConsumer.invokeAndUnwrap(consumer, "foo", 1L, IOException.class));
                assertEquals("foo1", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                ObjLongConsumer<String> consumer = (s, l) -> {
                    throw new UncheckedException(new FileNotFoundException(s + l));
                };

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class,
                        () -> CheckedObjLongConsumer.invokeAndUnwrap(consumer, "foo", 1L, IOException.class));
                assertEquals("foo1", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                ObjLongConsumer<String> consumer = (s, l) -> {
                    throw new UncheckedException(new ParseException(s + l, 0));
                };

                UncheckedException thrown = assertThrows(UncheckedException.class,
                        () -> CheckedObjLongConsumer.invokeAndUnwrap(consumer, "foo", 1L, IOException.class));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("foo1", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            ObjLongConsumer<String> consumer = (s, l) -> {
                throw new IllegalStateException(s + l);
            };

            IllegalStateException thrown = assertThrows(IllegalStateException.class,
                    () -> CheckedObjLongConsumer.invokeAndUnwrap(consumer, "foo", 1L, IOException.class));
            assertEquals("foo1", thrown.getMessage());
        }
    }

    @SuppressWarnings("unused")
    private static void ignore(String s, long l) {
        // do nothing
    }
}
