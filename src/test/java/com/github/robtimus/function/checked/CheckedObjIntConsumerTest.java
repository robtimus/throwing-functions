/*
 * CheckedObjIntConsumerTest.java
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
import java.util.function.ObjIntConsumer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("nls")
class CheckedObjIntConsumerTest {

    @Nested
    class OnErrorThrowAsChecked {

        @Test
        void testNullArgument() {
            CheckedObjIntConsumer<String, IOException> consumer = Spied.checkedObjIntConsumer(CheckedObjIntConsumerTest::ignore);

            assertThrows(NullPointerException.class, () -> consumer.onErrorThrowAsChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedObjIntConsumer<String, IOException> consumer = Spied.checkedObjIntConsumer(CheckedObjIntConsumerTest::ignore);

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedObjIntConsumer<String, ExecutionException> throwing = consumer.onErrorThrowAsChecked(errorMapper);

            throwing.accept("foo", 1);

            verify(consumer).accept("foo", 1);
            verify(consumer).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(consumer, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedObjIntConsumer<String, IOException> consumer = Spied.checkedObjIntConsumer((s, i) -> {
                throw new IOException(s + i);
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedObjIntConsumer<String, ExecutionException> throwing = consumer.onErrorThrowAsChecked(errorMapper);

            ExecutionException thrown = assertThrows(ExecutionException.class, () -> throwing.accept("foo", 1));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo1", cause.getMessage());

            verify(consumer).accept("foo", 1);
            verify(consumer).onErrorThrowAsChecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(consumer, errorMapper);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedObjIntConsumer<String, IOException> consumer = Spied.checkedObjIntConsumer((s, i) -> {
                throw new IllegalStateException(s + i);
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedObjIntConsumer<String, ExecutionException> throwing = consumer.onErrorThrowAsChecked(errorMapper);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> throwing.accept("foo", 1));
            assertEquals("foo1", thrown.getMessage());

            verify(consumer).accept("foo", 1);
            verify(consumer).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(consumer, errorMapper);
        }
    }

    @Nested
    class OnErrorThrowAsUnchecked {

        @Test
        void testNullArgument() {
            CheckedObjIntConsumer<String, IOException> consumer = Spied.checkedObjIntConsumer(CheckedObjIntConsumerTest::ignore);

            assertThrows(NullPointerException.class, () -> consumer.onErrorThrowAsUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedObjIntConsumer<String, IOException> consumer = Spied.checkedObjIntConsumer(CheckedObjIntConsumerTest::ignore);

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            ObjIntConsumer<String> throwing = consumer.onErrorThrowAsUnchecked(errorMapper);

            throwing.accept("foo", 1);

            verify(consumer).accept("foo", 1);
            verify(consumer).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(consumer, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedObjIntConsumer<String, IOException> consumer = Spied.checkedObjIntConsumer((s, i) -> {
                throw new IOException(s + i);
            });

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            ObjIntConsumer<String> throwing = consumer.onErrorThrowAsUnchecked(errorMapper);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> throwing.accept("foo", 1));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo1", cause.getMessage());

            verify(consumer).accept("foo", 1);
            verify(consumer).onErrorThrowAsUnchecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(consumer, errorMapper);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedObjIntConsumer<String, IOException> consumer = Spied.checkedObjIntConsumer((s, i) -> {
                throw new IllegalArgumentException(s + i);
            });

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            ObjIntConsumer<String> throwing = consumer.onErrorThrowAsUnchecked(errorMapper);

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> throwing.accept("foo", 1));
            assertEquals("foo1", thrown.getMessage());

            verify(consumer).accept("foo", 1);
            verify(consumer).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(consumer, errorMapper);
        }
    }

    @Nested
    class OnErrorHandleChecked {

        @Test
        void testNullArgument() {
            CheckedObjIntConsumer<String, IOException> consumer = Spied.checkedObjIntConsumer(CheckedObjIntConsumerTest::ignore);

            assertThrows(NullPointerException.class, () -> consumer.onErrorHandleChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedObjIntConsumer<String, IOException> consumer = Spied.checkedObjIntConsumer(CheckedObjIntConsumerTest::ignore);

            CheckedConsumer<IOException, ExecutionException> errorHandler = Spied.checkedConsumer(Exception::getMessage);

            CheckedObjIntConsumer<String, ExecutionException> handling = consumer.onErrorHandleChecked(errorHandler);

            handling.accept("foo", 1);

            verify(consumer).accept("foo", 1);
            verify(consumer).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(consumer, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException, ExecutionException {
                CheckedObjIntConsumer<String, IOException> consumer = Spied.checkedObjIntConsumer((s, i) -> {
                    throw new IOException(s + i);
                });

                CheckedConsumer<IOException, ExecutionException> errorHandler = Spied.checkedConsumer(Exception::getMessage);

                CheckedObjIntConsumer<String, ExecutionException> handling = consumer.onErrorHandleChecked(errorHandler);

                handling.accept("foo", 1);

                verify(consumer).accept("foo", 1);
                verify(consumer).onErrorHandleChecked(errorHandler);
                verify(errorHandler).accept(any());
                verifyNoMoreInteractions(consumer, errorHandler);
            }

            @Test
            void testHandlerThrowsChecked() throws IOException, ExecutionException {
                CheckedObjIntConsumer<String, IOException> consumer = Spied.checkedObjIntConsumer((s, i) -> {
                    throw new IOException(s + i);
                });

                CheckedConsumer<IOException, ExecutionException> errorHandler = Spied.checkedConsumer(e -> {
                    throw new ExecutionException(e);
                });

                CheckedObjIntConsumer<String, ExecutionException> handling = consumer.onErrorHandleChecked(errorHandler);

                ExecutionException thrown = assertThrows(ExecutionException.class, () -> handling.accept("foo", 1));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foo1", cause.getMessage());

                verify(consumer).accept("foo", 1);
                verify(consumer).onErrorHandleChecked(errorHandler);
                verify(errorHandler).accept(any());
                verifyNoMoreInteractions(consumer, errorHandler);
            }

            @Test
            void testHandlerThrowsUnchecked() throws IOException, ExecutionException {
                CheckedObjIntConsumer<String, IOException> consumer = Spied.checkedObjIntConsumer((s, i) -> {
                    throw new IOException(s + i);
                });

                CheckedConsumer<IOException, ExecutionException> errorHandler = Spied.checkedConsumer(e -> {
                    throw new IllegalStateException(e);
                });

                CheckedObjIntConsumer<String, ExecutionException> handling = consumer.onErrorHandleChecked(errorHandler);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.accept("foo", 1));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foo1", cause.getMessage());

                verify(consumer).accept("foo", 1);
                verify(consumer).onErrorHandleChecked(errorHandler);
                verify(errorHandler).accept(any());
                verifyNoMoreInteractions(consumer, errorHandler);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedObjIntConsumer<String, IOException> consumer = Spied.checkedObjIntConsumer((s, i) -> {
                throw new IllegalStateException(s + i);
            });

            CheckedConsumer<IOException, ExecutionException> errorHandler = Spied.checkedConsumer(Exception::getMessage);

            CheckedObjIntConsumer<String, ExecutionException> handling = consumer.onErrorHandleChecked(errorHandler);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.accept("foo", 1));
            assertEquals("foo1", thrown.getMessage());

            verify(consumer).accept("foo", 1);
            verify(consumer).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(consumer, errorHandler);
        }
    }

    @Nested
    class OnErrorHandleUnchecked {

        @Test
        void testNullArgument() {
            CheckedObjIntConsumer<String, IOException> consumer = Spied.checkedObjIntConsumer(CheckedObjIntConsumerTest::ignore);

            assertThrows(NullPointerException.class, () -> consumer.onErrorHandleUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedObjIntConsumer<String, IOException> consumer = Spied.checkedObjIntConsumer(CheckedObjIntConsumerTest::ignore);

            Consumer<IOException> errorHandler = Spied.consumer(Exception::getMessage);

            ObjIntConsumer<String> handling = consumer.onErrorHandleUnchecked(errorHandler);

            handling.accept("foo", 1);

            verify(consumer).accept("foo", 1);
            verify(consumer).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(consumer, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException {
                CheckedObjIntConsumer<String, IOException> consumer = Spied.checkedObjIntConsumer((s, i) -> {
                    throw new IOException(s + i);
                });

                Consumer<IOException> errorHandler = Spied.consumer(Exception::getMessage);

                ObjIntConsumer<String> handling = consumer.onErrorHandleUnchecked(errorHandler);

                handling.accept("foo", 1);

                verify(consumer).accept("foo", 1);
                verify(consumer).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).accept(any());
                verifyNoMoreInteractions(consumer, errorHandler);
            }

            @Test
            void testHandlerThrowsUnchecked() throws IOException {
                CheckedObjIntConsumer<String, IOException> consumer = Spied.checkedObjIntConsumer((s, i) -> {
                    throw new IOException(s + i);
                });

                Consumer<IOException> errorHandler = Spied.consumer(e -> {
                    throw new IllegalStateException(e);
                });

                ObjIntConsumer<String> handling = consumer.onErrorHandleUnchecked(errorHandler);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.accept("foo", 1));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foo1", cause.getMessage());

                verify(consumer).accept("foo", 1);
                verify(consumer).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).accept(any());
                verifyNoMoreInteractions(consumer, errorHandler);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedObjIntConsumer<String, IOException> consumer = Spied.checkedObjIntConsumer((s, i) -> {
                throw new IllegalStateException(s + i);
            });

            Consumer<IOException> errorHandler = Spied.consumer(Exception::getMessage);

            ObjIntConsumer<String> handling = consumer.onErrorHandleUnchecked(errorHandler);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.accept("foo", 1));
            assertEquals("foo1", thrown.getMessage());

            verify(consumer).accept("foo", 1);
            verify(consumer).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(consumer, errorHandler);
        }
    }

    @Nested
    class OnErrorAcceptChecked {

        @Test
        void testNullArgument() {
            CheckedObjIntConsumer<String, IOException> consumer = Spied.checkedObjIntConsumer(CheckedObjIntConsumerTest::ignore);

            assertThrows(NullPointerException.class, () -> consumer.onErrorAcceptChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedObjIntConsumer<String, IOException> consumer = Spied.checkedObjIntConsumer(CheckedObjIntConsumerTest::ignore);

            CheckedObjIntConsumer<String, ExecutionException> fallback = Spied.checkedObjIntConsumer(CheckedObjIntConsumerTest::ignore);

            CheckedObjIntConsumer<String, ExecutionException> applying = consumer.onErrorAcceptChecked(fallback);

            applying.accept("foo", 1);

            verify(consumer).accept("foo", 1);
            verify(consumer).onErrorAcceptChecked(fallback);
            verifyNoMoreInteractions(consumer, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                CheckedObjIntConsumer<String, IOException> consumer = Spied.checkedObjIntConsumer((s, i) -> {
                    throw new IOException(s + i);
                });

                CheckedObjIntConsumer<String, ParseException> fallback = Spied.checkedObjIntConsumer(CheckedObjIntConsumerTest::ignore);

                CheckedObjIntConsumer<String, ParseException> applying = consumer.onErrorAcceptChecked(fallback);

                applying.accept("foo", 1);

                verify(consumer).accept("foo", 1);
                verify(consumer).onErrorAcceptChecked(fallback);
                verify(fallback).accept("foo", 1);
                verifyNoMoreInteractions(consumer, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                CheckedObjIntConsumer<String, IOException> consumer = Spied.checkedObjIntConsumer((s, i) -> {
                    throw new IOException(s + i);
                });

                CheckedObjIntConsumer<String, ParseException> fallback = Spied.checkedObjIntConsumer((s, i) -> {
                    throw new ParseException(s + i, 0);
                });

                CheckedObjIntConsumer<String, ParseException> applying = consumer.onErrorAcceptChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> applying.accept("foo", 1));
                assertEquals("foo1", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(consumer).accept("foo", 1);
                verify(consumer).onErrorAcceptChecked(fallback);
                verify(fallback).accept("foo", 1);
                verifyNoMoreInteractions(consumer, fallback);
            }

            @Test
            void testFallbackThrowsUnchecked() throws IOException, ParseException {
                CheckedObjIntConsumer<String, IOException> consumer = Spied.checkedObjIntConsumer((s, i) -> {
                    throw new IOException(s + i);
                });

                CheckedObjIntConsumer<String, ParseException> fallback = Spied.checkedObjIntConsumer((s, i) -> {
                    throw new IllegalStateException(s + i);
                });

                CheckedObjIntConsumer<String, ParseException> applying = consumer.onErrorAcceptChecked(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> applying.accept("foo", 1));
                assertEquals("foo1", thrown.getMessage());

                verify(consumer).accept("foo", 1);
                verify(consumer).onErrorAcceptChecked(fallback);
                verify(fallback).accept("foo", 1);
                verifyNoMoreInteractions(consumer, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedObjIntConsumer<String, IOException> consumer = Spied.checkedObjIntConsumer((s, i) -> {
                throw new IllegalStateException(s + i);
            });

            CheckedObjIntConsumer<String, ParseException> fallback = Spied.checkedObjIntConsumer(CheckedObjIntConsumerTest::ignore);

            CheckedObjIntConsumer<String, ParseException> applying = consumer.onErrorAcceptChecked(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> applying.accept("foo", 1));
            assertEquals("foo1", thrown.getMessage());

            verify(consumer).accept("foo", 1);
            verify(consumer).onErrorAcceptChecked(fallback);
            verifyNoMoreInteractions(consumer, fallback);
        }
    }

    @Nested
    class OnErrorAcceptUnchecked {

        @Test
        void testNullArgument() {
            CheckedObjIntConsumer<String, IOException> consumer = Spied.checkedObjIntConsumer(CheckedObjIntConsumerTest::ignore);

            assertThrows(NullPointerException.class, () -> consumer.onErrorAcceptUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedObjIntConsumer<String, IOException> consumer = Spied.checkedObjIntConsumer(CheckedObjIntConsumerTest::ignore);

            ObjIntConsumer<String> fallback = Spied.objIntConsumer(CheckedObjIntConsumerTest::ignore);

            ObjIntConsumer<String> applying = consumer.onErrorAcceptUnchecked(fallback);

            applying.accept("foo", 1);

            verify(consumer).accept("foo", 1);
            verify(consumer).onErrorAcceptUnchecked(fallback);
            verifyNoMoreInteractions(consumer, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                CheckedObjIntConsumer<String, IOException> consumer = Spied.checkedObjIntConsumer((s, i) -> {
                    throw new IOException(s + i);
                });

                ObjIntConsumer<String> fallback = Spied.objIntConsumer(CheckedObjIntConsumerTest::ignore);

                ObjIntConsumer<String> applying = consumer.onErrorAcceptUnchecked(fallback);

                applying.accept("foo", 1);

                verify(consumer).accept("foo", 1);
                verify(consumer).onErrorAcceptUnchecked(fallback);
                verify(fallback).accept("foo", 1);
                verifyNoMoreInteractions(consumer, fallback);
            }

            @Test
            void testFallbackThrowsUnchecked() throws IOException {
                CheckedObjIntConsumer<String, IOException> consumer = Spied.checkedObjIntConsumer((s, i) -> {
                    throw new IOException(s + i);
                });

                ObjIntConsumer<String> fallback = Spied.objIntConsumer((s, i) -> {
                    throw new IllegalStateException(s + i);
                });

                ObjIntConsumer<String> applying = consumer.onErrorAcceptUnchecked(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> applying.accept("foo", 1));
                assertEquals("foo1", thrown.getMessage());

                verify(consumer).accept("foo", 1);
                verify(consumer).onErrorAcceptUnchecked(fallback);
                verify(fallback).accept("foo", 1);
                verifyNoMoreInteractions(consumer, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedObjIntConsumer<String, IOException> consumer = Spied.checkedObjIntConsumer((s, i) -> {
                throw new IllegalStateException(s + i);
            });

            ObjIntConsumer<String> fallback = Spied.objIntConsumer(CheckedObjIntConsumerTest::ignore);

            ObjIntConsumer<String> applying = consumer.onErrorAcceptUnchecked(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> applying.accept("foo", 1));
            assertEquals("foo1", thrown.getMessage());

            verify(consumer).accept("foo", 1);
            verify(consumer).onErrorAcceptUnchecked(fallback);
            verifyNoMoreInteractions(consumer, fallback);
        }
    }

    @Nested
    class OnErrorDiscard {

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedObjIntConsumer<String, IOException> consumer = Spied.checkedObjIntConsumer(CheckedObjIntConsumerTest::ignore);

            ObjIntConsumer<String> discarding = consumer.onErrorDiscard();

            discarding.accept("foo", 1);

            verify(consumer).accept("foo", 1);
            verify(consumer).onErrorDiscard();
            verifyNoMoreInteractions(consumer);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedObjIntConsumer<String, IOException> consumer = Spied.checkedObjIntConsumer((s, i) -> {
                throw new IOException(s + i);
            });

            ObjIntConsumer<String> discarding = consumer.onErrorDiscard();

            discarding.accept("foo", 1);

            verify(consumer).accept("foo", 1);
            verify(consumer).onErrorDiscard();
            verifyNoMoreInteractions(consumer);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedObjIntConsumer<String, IOException> consumer = Spied.checkedObjIntConsumer((s, i) -> {
                throw new IllegalStateException(s + i);
            });

            ObjIntConsumer<String> discarding = consumer.onErrorDiscard();

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> discarding.accept("foo", 1));
            assertEquals("foo1", thrown.getMessage());

            verify(consumer).accept("foo", 1);
            verify(consumer).onErrorDiscard();
            verifyNoMoreInteractions(consumer);
        }
    }

    @Nested
    class Unchecked {

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedObjIntConsumer<String, IOException> consumer = Spied.checkedObjIntConsumer(CheckedObjIntConsumerTest::ignore);

            ObjIntConsumer<String> unchecked = consumer.unchecked();

            unchecked.accept("foo", 1);

            verify(consumer).accept("foo", 1);
            verify(consumer).unchecked();
            verify(consumer).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(consumer);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedObjIntConsumer<String, IOException> consumer = Spied.checkedObjIntConsumer((s, i) -> {
                throw new IOException(s + i);
            });

            ObjIntConsumer<String> unchecked = consumer.unchecked();

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.accept("foo", 1));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo1", cause.getMessage());

            verify(consumer).accept("foo", 1);
            verify(consumer).unchecked();
            verify(consumer).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(consumer);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedObjIntConsumer<String, IOException> consumer = Spied.checkedObjIntConsumer((s, i) -> {
                throw new IllegalArgumentException(s + i);
            });

            ObjIntConsumer<String> unchecked = consumer.unchecked();

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> unchecked.accept("foo", 1));
            assertEquals("foo1", thrown.getMessage());

            verify(consumer).accept("foo", 1);
            verify(consumer).unchecked();
            verify(consumer).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(consumer);
        }
    }

    @Nested
    class Of {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> CheckedObjIntConsumer.of(null));
        }

        @Test
        void testNonNullArgument() throws IOException {
            CheckedObjIntConsumer<String, IOException> delegate = Spied.checkedObjIntConsumer(CheckedObjIntConsumerTest::ignore);

            CheckedObjIntConsumer<String, IOException> consumer = CheckedObjIntConsumer.of(delegate);

            consumer.accept("foo", 1);

            verify(delegate).accept("foo", 1);
            verifyNoMoreInteractions(delegate);
        }
    }

    @Nested
    class UncheckedFactory {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> CheckedObjIntConsumer.unchecked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedObjIntConsumer<String, IOException> consumer = Spied.checkedObjIntConsumer(CheckedObjIntConsumerTest::ignore);

            ObjIntConsumer<String> unchecked = CheckedObjIntConsumer.unchecked(consumer);

            unchecked.accept("foo", 1);

            verify(consumer).unchecked();
            verify(consumer).onErrorThrowAsUnchecked(any());
            verify(consumer).accept("foo", 1);
            verifyNoMoreInteractions(consumer);
        }

        @Test
        void testArgumentThrowsChecked() throws IOException {
            CheckedObjIntConsumer<String, IOException> consumer = Spied.checkedObjIntConsumer((s, i) -> {
                throw new IOException(s + i);
            });

            ObjIntConsumer<String> unchecked = CheckedObjIntConsumer.unchecked(consumer);

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.accept("foo", 1));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo1", cause.getMessage());

            verify(consumer).unchecked();
            verify(consumer).onErrorThrowAsUnchecked(any());
            verify(consumer).accept("foo", 1);
            verifyNoMoreInteractions(consumer);
        }

        @Test
        void testArgumentThrowsUnchecked() throws IOException {
            CheckedObjIntConsumer<String, IOException> consumer = Spied.checkedObjIntConsumer((s, i) -> {
                throw new IllegalArgumentException(s + i);
            });

            ObjIntConsumer<String> unchecked = CheckedObjIntConsumer.unchecked(consumer);

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> unchecked.accept("foo", 1));
            assertEquals("foo1", thrown.getMessage());

            verify(consumer).unchecked();
            verify(consumer).onErrorThrowAsUnchecked(any());
            verify(consumer).accept("foo", 1);
            verifyNoMoreInteractions(consumer);
        }
    }

    @Nested
    class CheckedWrapper {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> CheckedObjIntConsumer.checked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ObjIntConsumer<String> delegate = Spied.objIntConsumer(CheckedObjIntConsumerTest::ignore);

            CheckedObjIntConsumer<String, IOException> consumer = CheckedObjIntConsumer.checked(delegate);

            consumer.accept("foo", 1);

            verify(delegate).accept("foo", 1);
            verifyNoMoreInteractions(delegate);
        }

        @Test
        void testArgumentThrowsUnchecked() {
            CheckedObjIntConsumer<String, IOException> consumer = CheckedObjIntConsumer.checked((s, i) -> {
                throw new UncheckedException(s + i, new IOException());
            });

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> consumer.accept("foo", 1));
            assertEquals("foo1", thrown.getMessage());
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertNull(cause.getMessage());
        }
    }

    @Nested
    class CheckedUnwrapper {

        @Test
        void testNullArguments() {
            ObjIntConsumer<String> consumer = CheckedObjIntConsumerTest::ignore;

            assertThrows(NullPointerException.class, () -> CheckedObjIntConsumer.checked(null, IOException.class));
            assertThrows(NullPointerException.class, () -> CheckedObjIntConsumer.checked(consumer, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ObjIntConsumer<String> delegate = Spied.objIntConsumer(CheckedObjIntConsumerTest::ignore);

            CheckedObjIntConsumer<String, IOException> consumer = CheckedObjIntConsumer.checked(delegate, IOException.class);

            consumer.accept("foo", 1);

            verify(delegate).accept("foo", 1);
            verifyNoMoreInteractions(delegate);
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                CheckedObjIntConsumer<String, IOException> consumer = CheckedObjIntConsumer.checked((s, i) -> {
                    throw new UncheckedException(new IOException(s + i));
                }, IOException.class);

                IOException thrown = assertThrows(IOException.class, () -> consumer.accept("foo", 1));
                assertEquals("foo1", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                CheckedObjIntConsumer<String, IOException> consumer = CheckedObjIntConsumer.checked((s, i) -> {
                    throw new UncheckedException(new FileNotFoundException(s + i));
                }, IOException.class);

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class, () -> consumer.accept("foo", 1));
                assertEquals("foo1", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                CheckedObjIntConsumer<String, IOException> consumer = CheckedObjIntConsumer.checked((s, i) -> {
                    throw new UncheckedException(new ParseException(s + i, 0));
                }, IOException.class);

                UncheckedException thrown = assertThrows(UncheckedException.class, () -> consumer.accept("foo", 1));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("foo1", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            CheckedObjIntConsumer<String, IOException> consumer = CheckedObjIntConsumer.checked((s, i) -> {
                throw new IllegalStateException(s + i);
            }, IOException.class);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> consumer.accept("foo", 1));
            assertEquals("foo1", thrown.getMessage());
        }
    }

    @Nested
    class InvokeAndUnwrap {

        @Test
        void testNullArguments() throws IOException {
            ObjIntConsumer<String> consumer = Spied.objIntConsumer(CheckedObjIntConsumerTest::ignore);

            assertThrows(NullPointerException.class, () -> CheckedObjIntConsumer.invokeAndUnwrap(null, "foo", 1, IOException.class));
            assertThrows(NullPointerException.class, () -> CheckedObjIntConsumer.invokeAndUnwrap(consumer, "foo", 1, null));

            CheckedObjIntConsumer.invokeAndUnwrap(consumer, null, 1, IOException.class);

            verify(consumer).accept(null, 1);
            verifyNoMoreInteractions(consumer);
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ObjIntConsumer<String> consumer = Spied.objIntConsumer(CheckedObjIntConsumerTest::ignore);

            CheckedObjIntConsumer.invokeAndUnwrap(consumer, "foo", 1, IOException.class);

            verify(consumer).accept("foo", 1);
            verifyNoMoreInteractions(consumer);
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                ObjIntConsumer<String> consumer = (s, i) -> {
                    throw new UncheckedException(new IOException(s + i));
                };

                IOException thrown = assertThrows(IOException.class,
                        () -> CheckedObjIntConsumer.invokeAndUnwrap(consumer, "foo", 1, IOException.class));
                assertEquals("foo1", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                ObjIntConsumer<String> consumer = (s, i) -> {
                    throw new UncheckedException(new FileNotFoundException(s + i));
                };

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class,
                        () -> CheckedObjIntConsumer.invokeAndUnwrap(consumer, "foo", 1, IOException.class));
                assertEquals("foo1", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                ObjIntConsumer<String> consumer = (s, i) -> {
                    throw new UncheckedException(new ParseException(s + i, 0));
                };

                UncheckedException thrown = assertThrows(UncheckedException.class,
                        () -> CheckedObjIntConsumer.invokeAndUnwrap(consumer, "foo", 1, IOException.class));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("foo1", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            ObjIntConsumer<String> consumer = (s, i) -> {
                throw new IllegalStateException(s + i);
            };

            IllegalStateException thrown = assertThrows(IllegalStateException.class,
                    () -> CheckedObjIntConsumer.invokeAndUnwrap(consumer, "foo", 1, IOException.class));
            assertEquals("foo1", thrown.getMessage());
        }
    }

    @SuppressWarnings("unused")
    private static void ignore(String s, int i) {
        // do nothing
    }
}
