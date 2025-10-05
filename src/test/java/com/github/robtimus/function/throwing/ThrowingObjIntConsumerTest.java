/*
 * ThrowingObjIntConsumerTest.java
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
import java.util.function.ObjIntConsumer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

@SuppressWarnings("nls")
class ThrowingObjIntConsumerTest {

    @Nested
    class OnErrorThrowAsChecked {

        @Test
        void testNullArgument() {
            ThrowingObjIntConsumer<String, IOException> consumer = Spied.throwingObjIntConsumer(ThrowingObjIntConsumerTest::ignore);

            assertThrows(NullPointerException.class, () -> consumer.onErrorThrowAsChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingObjIntConsumer<String, IOException> consumer = Spied.throwingObjIntConsumer(ThrowingObjIntConsumerTest::ignore);

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingObjIntConsumer<String, ExecutionException> throwing = consumer.onErrorThrowAsChecked(errorMapper);

            throwing.accept("foo", 1);

            verify(consumer).accept("foo", 1);
            verify(consumer).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(consumer, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingObjIntConsumer<String, IOException> consumer = Spied.throwingObjIntConsumer((s, i) -> {
                throw new IOException(s + i);
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingObjIntConsumer<String, ExecutionException> throwing = consumer.onErrorThrowAsChecked(errorMapper);

            ExecutionException thrown = assertThrows(ExecutionException.class, () -> throwing.accept("foo", 1));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo1", cause.getMessage());

            verify(consumer).accept("foo", 1);
            verify(consumer).onErrorThrowAsChecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(consumer, errorMapper);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingObjIntConsumer<String, IOException> consumer = Spied.throwingObjIntConsumer((s, i) -> throwable.throwUnchecked(s + i));

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingObjIntConsumer<String, ExecutionException> throwing = consumer.onErrorThrowAsChecked(errorMapper);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> throwing.accept("foo", 1));
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
            ThrowingObjIntConsumer<String, IOException> consumer = Spied.throwingObjIntConsumer(ThrowingObjIntConsumerTest::ignore);

            assertThrows(NullPointerException.class, () -> consumer.onErrorThrowAsUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingObjIntConsumer<String, IOException> consumer = Spied.throwingObjIntConsumer(ThrowingObjIntConsumerTest::ignore);

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            ObjIntConsumer<String> throwing = consumer.onErrorThrowAsUnchecked(errorMapper);

            throwing.accept("foo", 1);

            verify(consumer).accept("foo", 1);
            verify(consumer).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(consumer, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingObjIntConsumer<String, IOException> consumer = Spied.throwingObjIntConsumer((s, i) -> {
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

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingObjIntConsumer<String, IOException> consumer = Spied.throwingObjIntConsumer((s, i) -> throwable.throwUnchecked(s + i));

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            ObjIntConsumer<String> throwing = consumer.onErrorThrowAsUnchecked(errorMapper);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> throwing.accept("foo", 1));
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
            ThrowingObjIntConsumer<String, IOException> consumer = Spied.throwingObjIntConsumer(ThrowingObjIntConsumerTest::ignore);

            assertThrows(NullPointerException.class, () -> consumer.onErrorHandleChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingObjIntConsumer<String, IOException> consumer = Spied.throwingObjIntConsumer(ThrowingObjIntConsumerTest::ignore);

            ThrowingConsumer<IOException, ExecutionException> errorHandler = Spied.throwingConsumer(Exception::getMessage);

            ThrowingObjIntConsumer<String, ExecutionException> handling = consumer.onErrorHandleChecked(errorHandler);

            handling.accept("foo", 1);

            verify(consumer).accept("foo", 1);
            verify(consumer).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(consumer, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException, ExecutionException {
                ThrowingObjIntConsumer<String, IOException> consumer = Spied.throwingObjIntConsumer((s, i) -> {
                    throw new IOException(s + i);
                });

                ThrowingConsumer<IOException, ExecutionException> errorHandler = Spied.throwingConsumer(Exception::getMessage);

                ThrowingObjIntConsumer<String, ExecutionException> handling = consumer.onErrorHandleChecked(errorHandler);

                handling.accept("foo", 1);

                verify(consumer).accept("foo", 1);
                verify(consumer).onErrorHandleChecked(errorHandler);
                verify(errorHandler).accept(any());
                verifyNoMoreInteractions(consumer, errorHandler);
            }

            @Test
            void testHandlerThrowsChecked() throws IOException, ExecutionException {
                ThrowingObjIntConsumer<String, IOException> consumer = Spied.throwingObjIntConsumer((s, i) -> {
                    throw new IOException(s + i);
                });

                ThrowingConsumer<IOException, ExecutionException> errorHandler = Spied.throwingConsumer(e -> {
                    throw new ExecutionException(e);
                });

                ThrowingObjIntConsumer<String, ExecutionException> handling = consumer.onErrorHandleChecked(errorHandler);

                ExecutionException thrown = assertThrows(ExecutionException.class, () -> handling.accept("foo", 1));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foo1", cause.getMessage());

                verify(consumer).accept("foo", 1);
                verify(consumer).onErrorHandleChecked(errorHandler);
                verify(errorHandler).accept(any());
                verifyNoMoreInteractions(consumer, errorHandler);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testHandlerThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ExecutionException {
                ThrowingObjIntConsumer<String, IOException> consumer = Spied.throwingObjIntConsumer((s, i) -> {
                    throw new IOException(s + i);
                });

                ThrowingConsumer<IOException, ExecutionException> errorHandler = Spied.throwingConsumer(throwable::throwUnchecked);

                ThrowingObjIntConsumer<String, ExecutionException> handling = consumer.onErrorHandleChecked(errorHandler);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.accept("foo", 1));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foo1", cause.getMessage());

                verify(consumer).accept("foo", 1);
                verify(consumer).onErrorHandleChecked(errorHandler);
                verify(errorHandler).accept(any());
                verifyNoMoreInteractions(consumer, errorHandler);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingObjIntConsumer<String, IOException> consumer = Spied.throwingObjIntConsumer((s, i) -> throwable.throwUnchecked(s + i));

            ThrowingConsumer<IOException, ExecutionException> errorHandler = Spied.throwingConsumer(Exception::getMessage);

            ThrowingObjIntConsumer<String, ExecutionException> handling = consumer.onErrorHandleChecked(errorHandler);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.accept("foo", 1));
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
            ThrowingObjIntConsumer<String, IOException> consumer = Spied.throwingObjIntConsumer(ThrowingObjIntConsumerTest::ignore);

            assertThrows(NullPointerException.class, () -> consumer.onErrorHandleUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingObjIntConsumer<String, IOException> consumer = Spied.throwingObjIntConsumer(ThrowingObjIntConsumerTest::ignore);

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
                ThrowingObjIntConsumer<String, IOException> consumer = Spied.throwingObjIntConsumer((s, i) -> {
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

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testHandlerThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingObjIntConsumer<String, IOException> consumer = Spied.throwingObjIntConsumer((s, i) -> {
                    throw new IOException(s + i);
                });

                Consumer<IOException> errorHandler = Spied.consumer(throwable::throwUnchecked);

                ObjIntConsumer<String> handling = consumer.onErrorHandleUnchecked(errorHandler);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.accept("foo", 1));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foo1", cause.getMessage());

                verify(consumer).accept("foo", 1);
                verify(consumer).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).accept(any());
                verifyNoMoreInteractions(consumer, errorHandler);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingObjIntConsumer<String, IOException> consumer = Spied.throwingObjIntConsumer((s, i) -> throwable.throwUnchecked(s + i));

            Consumer<IOException> errorHandler = Spied.consumer(Exception::getMessage);

            ObjIntConsumer<String> handling = consumer.onErrorHandleUnchecked(errorHandler);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.accept("foo", 1));
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
            ThrowingObjIntConsumer<String, IOException> consumer = Spied.throwingObjIntConsumer(ThrowingObjIntConsumerTest::ignore);

            assertThrows(NullPointerException.class, () -> consumer.onErrorAcceptChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingObjIntConsumer<String, IOException> consumer = Spied.throwingObjIntConsumer(ThrowingObjIntConsumerTest::ignore);

            ThrowingObjIntConsumer<String, ExecutionException> fallback = Spied.throwingObjIntConsumer(ThrowingObjIntConsumerTest::ignore);

            ThrowingObjIntConsumer<String, ExecutionException> applying = consumer.onErrorAcceptChecked(fallback);

            applying.accept("foo", 1);

            verify(consumer).accept("foo", 1);
            verify(consumer).onErrorAcceptChecked(fallback);
            verifyNoMoreInteractions(consumer, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                ThrowingObjIntConsumer<String, IOException> consumer = Spied.throwingObjIntConsumer((s, i) -> {
                    throw new IOException(s + i);
                });

                ThrowingObjIntConsumer<String, ParseException> fallback = Spied.throwingObjIntConsumer(ThrowingObjIntConsumerTest::ignore);

                ThrowingObjIntConsumer<String, ParseException> applying = consumer.onErrorAcceptChecked(fallback);

                applying.accept("foo", 1);

                verify(consumer).accept("foo", 1);
                verify(consumer).onErrorAcceptChecked(fallback);
                verify(fallback).accept("foo", 1);
                verifyNoMoreInteractions(consumer, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                ThrowingObjIntConsumer<String, IOException> consumer = Spied.throwingObjIntConsumer((s, i) -> {
                    throw new IOException(s + i);
                });

                ThrowingObjIntConsumer<String, ParseException> fallback = Spied.throwingObjIntConsumer((s, i) -> {
                    throw new ParseException(s + i, 0);
                });

                ThrowingObjIntConsumer<String, ParseException> applying = consumer.onErrorAcceptChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> applying.accept("foo", 1));
                assertEquals("foo1", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(consumer).accept("foo", 1);
                verify(consumer).onErrorAcceptChecked(fallback);
                verify(fallback).accept("foo", 1);
                verifyNoMoreInteractions(consumer, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ParseException {
                ThrowingObjIntConsumer<String, IOException> consumer = Spied.throwingObjIntConsumer((s, i) -> {
                    throw new IOException(s + i);
                });

                ThrowingObjIntConsumer<String, ParseException> fallback = Spied.throwingObjIntConsumer((s, i) -> throwable.throwUnchecked(s + i));

                ThrowingObjIntConsumer<String, ParseException> applying = consumer.onErrorAcceptChecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.accept("foo", 1));
                assertEquals("foo1", thrown.getMessage());

                verify(consumer).accept("foo", 1);
                verify(consumer).onErrorAcceptChecked(fallback);
                verify(fallback).accept("foo", 1);
                verifyNoMoreInteractions(consumer, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingObjIntConsumer<String, IOException> consumer = Spied.throwingObjIntConsumer((s, i) -> throwable.throwUnchecked(s + i));

            ThrowingObjIntConsumer<String, ParseException> fallback = Spied.throwingObjIntConsumer(ThrowingObjIntConsumerTest::ignore);

            ThrowingObjIntConsumer<String, ParseException> applying = consumer.onErrorAcceptChecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.accept("foo", 1));
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
            ThrowingObjIntConsumer<String, IOException> consumer = Spied.throwingObjIntConsumer(ThrowingObjIntConsumerTest::ignore);

            assertThrows(NullPointerException.class, () -> consumer.onErrorAcceptUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingObjIntConsumer<String, IOException> consumer = Spied.throwingObjIntConsumer(ThrowingObjIntConsumerTest::ignore);

            ObjIntConsumer<String> fallback = Spied.objIntConsumer(ThrowingObjIntConsumerTest::ignore);

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
                ThrowingObjIntConsumer<String, IOException> consumer = Spied.throwingObjIntConsumer((s, i) -> {
                    throw new IOException(s + i);
                });

                ObjIntConsumer<String> fallback = Spied.objIntConsumer(ThrowingObjIntConsumerTest::ignore);

                ObjIntConsumer<String> applying = consumer.onErrorAcceptUnchecked(fallback);

                applying.accept("foo", 1);

                verify(consumer).accept("foo", 1);
                verify(consumer).onErrorAcceptUnchecked(fallback);
                verify(fallback).accept("foo", 1);
                verifyNoMoreInteractions(consumer, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingObjIntConsumer<String, IOException> consumer = Spied.throwingObjIntConsumer((s, i) -> {
                    throw new IOException(s + i);
                });

                ObjIntConsumer<String> fallback = Spied.objIntConsumer((s, i) -> throwable.throwUnchecked(s + i));

                ObjIntConsumer<String> applying = consumer.onErrorAcceptUnchecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.accept("foo", 1));
                assertEquals("foo1", thrown.getMessage());

                verify(consumer).accept("foo", 1);
                verify(consumer).onErrorAcceptUnchecked(fallback);
                verify(fallback).accept("foo", 1);
                verifyNoMoreInteractions(consumer, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingObjIntConsumer<String, IOException> consumer = Spied.throwingObjIntConsumer((s, i) -> throwable.throwUnchecked(s + i));

            ObjIntConsumer<String> fallback = Spied.objIntConsumer(ThrowingObjIntConsumerTest::ignore);

            ObjIntConsumer<String> applying = consumer.onErrorAcceptUnchecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.accept("foo", 1));
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
            ThrowingObjIntConsumer<String, IOException> consumer = Spied.throwingObjIntConsumer(ThrowingObjIntConsumerTest::ignore);

            ObjIntConsumer<String> discarding = consumer.onErrorDiscard();

            discarding.accept("foo", 1);

            verify(consumer).accept("foo", 1);
            verify(consumer).onErrorDiscard();
            verifyNoMoreInteractions(consumer);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingObjIntConsumer<String, IOException> consumer = Spied.throwingObjIntConsumer((s, i) -> {
                throw new IOException(s + i);
            });

            ObjIntConsumer<String> discarding = consumer.onErrorDiscard();

            discarding.accept("foo", 1);

            verify(consumer).accept("foo", 1);
            verify(consumer).onErrorDiscard();
            verifyNoMoreInteractions(consumer);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingObjIntConsumer<String, IOException> consumer = Spied.throwingObjIntConsumer((s, i) -> throwable.throwUnchecked(s + i));

            ObjIntConsumer<String> discarding = consumer.onErrorDiscard();

            Throwable thrown = assertThrows(throwable.throwableType(), () -> discarding.accept("foo", 1));
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
            ThrowingObjIntConsumer<String, IOException> consumer = Spied.throwingObjIntConsumer(ThrowingObjIntConsumerTest::ignore);

            ObjIntConsumer<String> unchecked = consumer.unchecked();

            unchecked.accept("foo", 1);

            verify(consumer).accept("foo", 1);
            verify(consumer).unchecked();
            verify(consumer).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(consumer);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingObjIntConsumer<String, IOException> consumer = Spied.throwingObjIntConsumer((s, i) -> {
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

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingObjIntConsumer<String, IOException> consumer = Spied.throwingObjIntConsumer((s, i) -> throwable.throwUnchecked(s + i));

            ObjIntConsumer<String> unchecked = consumer.unchecked();

            Throwable thrown = assertThrows(throwable.throwableType(), () -> unchecked.accept("foo", 1));
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
            assertThrows(NullPointerException.class, () -> ThrowingObjIntConsumer.of(null));
        }

        @Test
        void testNonNullArgument() throws IOException {
            ThrowingObjIntConsumer<String, IOException> delegate = Spied.throwingObjIntConsumer(ThrowingObjIntConsumerTest::ignore);

            ThrowingObjIntConsumer<String, IOException> consumer = ThrowingObjIntConsumer.of(delegate);

            consumer.accept("foo", 1);

            verify(delegate).accept("foo", 1);
            verifyNoMoreInteractions(delegate);
        }
    }

    @Nested
    class UncheckedFactory {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> ThrowingObjIntConsumer.unchecked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ThrowingObjIntConsumer<String, IOException> consumer = Spied.throwingObjIntConsumer(ThrowingObjIntConsumerTest::ignore);

            ObjIntConsumer<String> unchecked = ThrowingObjIntConsumer.unchecked(consumer);

            unchecked.accept("foo", 1);

            verify(consumer).unchecked();
            verify(consumer).onErrorThrowAsUnchecked(any());
            verify(consumer).accept("foo", 1);
            verifyNoMoreInteractions(consumer);
        }

        @Test
        void testArgumentThrowsChecked() throws IOException {
            ThrowingObjIntConsumer<String, IOException> consumer = Spied.throwingObjIntConsumer((s, i) -> {
                throw new IOException(s + i);
            });

            ObjIntConsumer<String> unchecked = ThrowingObjIntConsumer.unchecked(consumer);

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.accept("foo", 1));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo1", cause.getMessage());

            verify(consumer).unchecked();
            verify(consumer).onErrorThrowAsUnchecked(any());
            verify(consumer).accept("foo", 1);
            verifyNoMoreInteractions(consumer);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testArgumentThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingObjIntConsumer<String, IOException> consumer = Spied.throwingObjIntConsumer((s, i) -> throwable.throwUnchecked(s + i));

            ObjIntConsumer<String> unchecked = ThrowingObjIntConsumer.unchecked(consumer);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> unchecked.accept("foo", 1));
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
            assertThrows(NullPointerException.class, () -> ThrowingObjIntConsumer.checked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ObjIntConsumer<String> delegate = Spied.objIntConsumer(ThrowingObjIntConsumerTest::ignore);

            ThrowingObjIntConsumer<String, IOException> consumer = ThrowingObjIntConsumer.checked(delegate);

            consumer.accept("foo", 1);

            verify(delegate).accept("foo", 1);
            verifyNoMoreInteractions(delegate);
        }

        @Test
        void testArgumentThrowsUnchecked() {
            ThrowingObjIntConsumer<String, IOException> consumer = ThrowingObjIntConsumer.checked((s, i) -> {
                throw UncheckedException.withoutStackTrace(s + i, new IOException());
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
            ObjIntConsumer<String> consumer = ThrowingObjIntConsumerTest::ignore;

            assertThrows(NullPointerException.class, () -> ThrowingObjIntConsumer.checked(null, IOException.class));
            assertThrows(NullPointerException.class, () -> ThrowingObjIntConsumer.checked(consumer, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ObjIntConsumer<String> delegate = Spied.objIntConsumer(ThrowingObjIntConsumerTest::ignore);

            ThrowingObjIntConsumer<String, IOException> consumer = ThrowingObjIntConsumer.checked(delegate, IOException.class);

            consumer.accept("foo", 1);

            verify(delegate).accept("foo", 1);
            verifyNoMoreInteractions(delegate);
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                ThrowingObjIntConsumer<String, IOException> consumer = ThrowingObjIntConsumer.checked((s, i) -> {
                    throw UncheckedException.withoutStackTrace(new IOException(s + i));
                }, IOException.class);

                IOException thrown = assertThrows(IOException.class, () -> consumer.accept("foo", 1));
                assertEquals("foo1", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                ThrowingObjIntConsumer<String, IOException> consumer = ThrowingObjIntConsumer.checked((s, i) -> {
                    throw UncheckedException.withoutStackTrace(new FileNotFoundException(s + i));
                }, IOException.class);

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class, () -> consumer.accept("foo", 1));
                assertEquals("foo1", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                ThrowingObjIntConsumer<String, IOException> consumer = ThrowingObjIntConsumer.checked((s, i) -> {
                    throw UncheckedException.withoutStackTrace(new ParseException(s + i, 0));
                }, IOException.class);

                UncheckedException thrown = assertThrows(UncheckedException.class, () -> consumer.accept("foo", 1));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("foo1", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            ThrowingObjIntConsumer<String, IOException> consumer = ThrowingObjIntConsumer.checked((s, i) -> {
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
            ObjIntConsumer<String> consumer = Spied.objIntConsumer(ThrowingObjIntConsumerTest::ignore);

            assertThrows(NullPointerException.class, () -> ThrowingObjIntConsumer.invokeAndUnwrap(null, "foo", 1, IOException.class));
            assertThrows(NullPointerException.class, () -> ThrowingObjIntConsumer.invokeAndUnwrap(consumer, "foo", 1, null));

            ThrowingObjIntConsumer.invokeAndUnwrap(consumer, null, 1, IOException.class);

            verify(consumer).accept(null, 1);
            verifyNoMoreInteractions(consumer);
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ObjIntConsumer<String> consumer = Spied.objIntConsumer(ThrowingObjIntConsumerTest::ignore);

            ThrowingObjIntConsumer.invokeAndUnwrap(consumer, "foo", 1, IOException.class);

            verify(consumer).accept("foo", 1);
            verifyNoMoreInteractions(consumer);
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                ObjIntConsumer<String> consumer = (s, i) -> {
                    throw UncheckedException.withoutStackTrace(new IOException(s + i));
                };

                IOException thrown = assertThrows(IOException.class,
                        () -> ThrowingObjIntConsumer.invokeAndUnwrap(consumer, "foo", 1, IOException.class));
                assertEquals("foo1", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                ObjIntConsumer<String> consumer = (s, i) -> {
                    throw UncheckedException.withoutStackTrace(new FileNotFoundException(s + i));
                };

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class,
                        () -> ThrowingObjIntConsumer.invokeAndUnwrap(consumer, "foo", 1, IOException.class));
                assertEquals("foo1", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                ObjIntConsumer<String> consumer = (s, i) -> {
                    throw UncheckedException.withoutStackTrace(new ParseException(s + i, 0));
                };

                UncheckedException thrown = assertThrows(UncheckedException.class,
                        () -> ThrowingObjIntConsumer.invokeAndUnwrap(consumer, "foo", 1, IOException.class));
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
                    () -> ThrowingObjIntConsumer.invokeAndUnwrap(consumer, "foo", 1, IOException.class));
            assertEquals("foo1", thrown.getMessage());
        }
    }

    @SuppressWarnings("unused")
    private static void ignore(String s, int i) {
        // do nothing
    }
}
