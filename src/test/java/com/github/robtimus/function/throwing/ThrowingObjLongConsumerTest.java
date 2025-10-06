/*
 * ThrowingObjLongConsumerTest.java
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
import java.util.function.ObjLongConsumer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

@SuppressWarnings("nls")
class ThrowingObjLongConsumerTest {

    @Nested
    class OnErrorThrowAsChecked {

        @Test
        void testNullArgument() {
            ThrowingObjLongConsumer<String, IOException> consumer = Spied.throwingObjLongConsumer(ThrowingObjLongConsumerTest::ignore);

            assertThrows(NullPointerException.class, () -> consumer.onErrorThrowAsChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingObjLongConsumer<String, IOException> consumer = Spied.throwingObjLongConsumer(ThrowingObjLongConsumerTest::ignore);

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingObjLongConsumer<String, ExecutionException> throwing = consumer.onErrorThrowAsChecked(errorMapper);

            throwing.accept("foo", 1L);

            verify(consumer).accept("foo", 1L);
            verify(consumer).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(consumer, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingObjLongConsumer<String, IOException> consumer = Spied.throwingObjLongConsumer((s, l) -> {
                throw new IOException(s + l);
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingObjLongConsumer<String, ExecutionException> throwing = consumer.onErrorThrowAsChecked(errorMapper);

            ExecutionException thrown = assertThrows(ExecutionException.class, () -> throwing.accept("foo", 1L));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo1", cause.getMessage());

            verify(consumer).accept("foo", 1L);
            verify(consumer).onErrorThrowAsChecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(consumer, errorMapper);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingObjLongConsumer<String, IOException> consumer = Spied.throwingObjLongConsumer((s, l) -> throwable.throwUnchecked(s + l));

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingObjLongConsumer<String, ExecutionException> throwing = consumer.onErrorThrowAsChecked(errorMapper);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> throwing.accept("foo", 1L));
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
            ThrowingObjLongConsumer<String, IOException> consumer = Spied.throwingObjLongConsumer(ThrowingObjLongConsumerTest::ignore);

            assertThrows(NullPointerException.class, () -> consumer.onErrorThrowAsUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingObjLongConsumer<String, IOException> consumer = Spied.throwingObjLongConsumer(ThrowingObjLongConsumerTest::ignore);

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            ObjLongConsumer<String> throwing = consumer.onErrorThrowAsUnchecked(errorMapper);

            throwing.accept("foo", 1L);

            verify(consumer).accept("foo", 1L);
            verify(consumer).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(consumer, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingObjLongConsumer<String, IOException> consumer = Spied.throwingObjLongConsumer((s, l) -> {
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

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingObjLongConsumer<String, IOException> consumer = Spied.throwingObjLongConsumer((s, l) -> throwable.throwUnchecked(s + l));

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            ObjLongConsumer<String> throwing = consumer.onErrorThrowAsUnchecked(errorMapper);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> throwing.accept("foo", 1L));
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
            ThrowingObjLongConsumer<String, IOException> consumer = Spied.throwingObjLongConsumer(ThrowingObjLongConsumerTest::ignore);

            assertThrows(NullPointerException.class, () -> consumer.onErrorHandleChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingObjLongConsumer<String, IOException> consumer = Spied.throwingObjLongConsumer(ThrowingObjLongConsumerTest::ignore);

            ThrowingConsumer<IOException, ExecutionException> errorHandler = Spied.throwingConsumer(Exception::getMessage);

            ThrowingObjLongConsumer<String, ExecutionException> handling = consumer.onErrorHandleChecked(errorHandler);

            handling.accept("foo", 1L);

            verify(consumer).accept("foo", 1L);
            verify(consumer).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(consumer, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException, ExecutionException {
                ThrowingObjLongConsumer<String, IOException> consumer = Spied.throwingObjLongConsumer((s, l) -> {
                    throw new IOException(s + l);
                });

                ThrowingConsumer<IOException, ExecutionException> errorHandler = Spied.throwingConsumer(Exception::getMessage);

                ThrowingObjLongConsumer<String, ExecutionException> handling = consumer.onErrorHandleChecked(errorHandler);

                handling.accept("foo", 1L);

                verify(consumer).accept("foo", 1L);
                verify(consumer).onErrorHandleChecked(errorHandler);
                verify(errorHandler).accept(any());
                verifyNoMoreInteractions(consumer, errorHandler);
            }

            @Test
            void testHandlerThrowsChecked() throws IOException, ExecutionException {
                ThrowingObjLongConsumer<String, IOException> consumer = Spied.throwingObjLongConsumer((s, l) -> {
                    throw new IOException(s + l);
                });

                ThrowingConsumer<IOException, ExecutionException> errorHandler = Spied.throwingConsumer(e -> {
                    throw new ExecutionException(e);
                });

                ThrowingObjLongConsumer<String, ExecutionException> handling = consumer.onErrorHandleChecked(errorHandler);

                ExecutionException thrown = assertThrows(ExecutionException.class, () -> handling.accept("foo", 1L));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foo1", cause.getMessage());

                verify(consumer).accept("foo", 1L);
                verify(consumer).onErrorHandleChecked(errorHandler);
                verify(errorHandler).accept(any());
                verifyNoMoreInteractions(consumer, errorHandler);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testHandlerThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ExecutionException {
                ThrowingObjLongConsumer<String, IOException> consumer = Spied.throwingObjLongConsumer((s, l) -> {
                    throw new IOException(s + l);
                });

                ThrowingConsumer<IOException, ExecutionException> errorHandler = Spied.throwingConsumer(throwable::throwUnchecked);

                ThrowingObjLongConsumer<String, ExecutionException> handling = consumer.onErrorHandleChecked(errorHandler);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.accept("foo", 1L));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foo1", cause.getMessage());

                verify(consumer).accept("foo", 1L);
                verify(consumer).onErrorHandleChecked(errorHandler);
                verify(errorHandler).accept(any());
                verifyNoMoreInteractions(consumer, errorHandler);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingObjLongConsumer<String, IOException> consumer = Spied.throwingObjLongConsumer((s, l) -> throwable.throwUnchecked(s + l));

            ThrowingConsumer<IOException, ExecutionException> errorHandler = Spied.throwingConsumer(Exception::getMessage);

            ThrowingObjLongConsumer<String, ExecutionException> handling = consumer.onErrorHandleChecked(errorHandler);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.accept("foo", 1L));
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
            ThrowingObjLongConsumer<String, IOException> consumer = Spied.throwingObjLongConsumer(ThrowingObjLongConsumerTest::ignore);

            assertThrows(NullPointerException.class, () -> consumer.onErrorHandleUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingObjLongConsumer<String, IOException> consumer = Spied.throwingObjLongConsumer(ThrowingObjLongConsumerTest::ignore);

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
                ThrowingObjLongConsumer<String, IOException> consumer = Spied.throwingObjLongConsumer((s, l) -> {
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

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testHandlerThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingObjLongConsumer<String, IOException> consumer = Spied.throwingObjLongConsumer((s, l) -> {
                    throw new IOException(s + l);
                });

                Consumer<IOException> errorHandler = Spied.consumer(throwable::throwUnchecked);

                ObjLongConsumer<String> handling = consumer.onErrorHandleUnchecked(errorHandler);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.accept("foo", 1L));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foo1", cause.getMessage());

                verify(consumer).accept("foo", 1L);
                verify(consumer).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).accept(any());
                verifyNoMoreInteractions(consumer, errorHandler);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingObjLongConsumer<String, IOException> consumer = Spied.throwingObjLongConsumer((s, l) -> throwable.throwUnchecked(s + l));

            Consumer<IOException> errorHandler = Spied.consumer(Exception::getMessage);

            ObjLongConsumer<String> handling = consumer.onErrorHandleUnchecked(errorHandler);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.accept("foo", 1L));
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
            ThrowingObjLongConsumer<String, IOException> consumer = Spied.throwingObjLongConsumer(ThrowingObjLongConsumerTest::ignore);

            assertThrows(NullPointerException.class, () -> consumer.onErrorAcceptChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingObjLongConsumer<String, IOException> consumer = Spied.throwingObjLongConsumer(ThrowingObjLongConsumerTest::ignore);

            ThrowingObjLongConsumer<String, ExecutionException> fallback = Spied.throwingObjLongConsumer(ThrowingObjLongConsumerTest::ignore);

            ThrowingObjLongConsumer<String, ExecutionException> applying = consumer.onErrorAcceptChecked(fallback);

            applying.accept("foo", 1L);

            verify(consumer).accept("foo", 1L);
            verify(consumer).onErrorAcceptChecked(fallback);
            verifyNoMoreInteractions(consumer, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                ThrowingObjLongConsumer<String, IOException> consumer = Spied.throwingObjLongConsumer((s, l) -> {
                    throw new IOException(s + l);
                });

                ThrowingObjLongConsumer<String, ParseException> fallback = Spied.throwingObjLongConsumer(ThrowingObjLongConsumerTest::ignore);

                ThrowingObjLongConsumer<String, ParseException> applying = consumer.onErrorAcceptChecked(fallback);

                applying.accept("foo", 1L);

                verify(consumer).accept("foo", 1L);
                verify(consumer).onErrorAcceptChecked(fallback);
                verify(fallback).accept("foo", 1L);
                verifyNoMoreInteractions(consumer, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                ThrowingObjLongConsumer<String, IOException> consumer = Spied.throwingObjLongConsumer((s, l) -> {
                    throw new IOException(s + l);
                });

                ThrowingObjLongConsumer<String, ParseException> fallback = Spied.throwingObjLongConsumer((s, l) -> {
                    throw new ParseException(s + l, 0);
                });

                ThrowingObjLongConsumer<String, ParseException> applying = consumer.onErrorAcceptChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> applying.accept("foo", 1L));
                assertEquals("foo1", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(consumer).accept("foo", 1L);
                verify(consumer).onErrorAcceptChecked(fallback);
                verify(fallback).accept("foo", 1L);
                verifyNoMoreInteractions(consumer, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ParseException {
                ThrowingObjLongConsumer<String, IOException> consumer = Spied.throwingObjLongConsumer((s, l) -> {
                    throw new IOException(s + l);
                });

                ThrowingObjLongConsumer<String, ParseException> fallback = Spied.throwingObjLongConsumer((s, l) -> throwable.throwUnchecked(s + l));

                ThrowingObjLongConsumer<String, ParseException> applying = consumer.onErrorAcceptChecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.accept("foo", 1L));
                assertEquals("foo1", thrown.getMessage());

                verify(consumer).accept("foo", 1L);
                verify(consumer).onErrorAcceptChecked(fallback);
                verify(fallback).accept("foo", 1L);
                verifyNoMoreInteractions(consumer, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingObjLongConsumer<String, IOException> consumer = Spied.throwingObjLongConsumer((s, l) -> throwable.throwUnchecked(s + l));

            ThrowingObjLongConsumer<String, ParseException> fallback = Spied.throwingObjLongConsumer(ThrowingObjLongConsumerTest::ignore);

            ThrowingObjLongConsumer<String, ParseException> applying = consumer.onErrorAcceptChecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.accept("foo", 1L));
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
            ThrowingObjLongConsumer<String, IOException> consumer = Spied.throwingObjLongConsumer(ThrowingObjLongConsumerTest::ignore);

            assertThrows(NullPointerException.class, () -> consumer.onErrorAcceptUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingObjLongConsumer<String, IOException> consumer = Spied.throwingObjLongConsumer(ThrowingObjLongConsumerTest::ignore);

            ObjLongConsumer<String> fallback = Spied.objLongConsumer(ThrowingObjLongConsumerTest::ignore);

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
                ThrowingObjLongConsumer<String, IOException> consumer = Spied.throwingObjLongConsumer((s, l) -> {
                    throw new IOException(s + l);
                });

                ObjLongConsumer<String> fallback = Spied.objLongConsumer(ThrowingObjLongConsumerTest::ignore);

                ObjLongConsumer<String> applying = consumer.onErrorAcceptUnchecked(fallback);

                applying.accept("foo", 1L);

                verify(consumer).accept("foo", 1L);
                verify(consumer).onErrorAcceptUnchecked(fallback);
                verify(fallback).accept("foo", 1L);
                verifyNoMoreInteractions(consumer, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingObjLongConsumer<String, IOException> consumer = Spied.throwingObjLongConsumer((s, l) -> {
                    throw new IOException(s + l);
                });

                ObjLongConsumer<String> fallback = Spied.objLongConsumer((s, l) -> throwable.throwUnchecked(s + l));

                ObjLongConsumer<String> applying = consumer.onErrorAcceptUnchecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.accept("foo", 1L));
                assertEquals("foo1", thrown.getMessage());

                verify(consumer).accept("foo", 1L);
                verify(consumer).onErrorAcceptUnchecked(fallback);
                verify(fallback).accept("foo", 1L);
                verifyNoMoreInteractions(consumer, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingObjLongConsumer<String, IOException> consumer = Spied.throwingObjLongConsumer((s, l) -> throwable.throwUnchecked(s + l));

            ObjLongConsumer<String> fallback = Spied.objLongConsumer(ThrowingObjLongConsumerTest::ignore);

            ObjLongConsumer<String> applying = consumer.onErrorAcceptUnchecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.accept("foo", 1L));
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
            ThrowingObjLongConsumer<String, IOException> consumer = Spied.throwingObjLongConsumer(ThrowingObjLongConsumerTest::ignore);

            ObjLongConsumer<String> discarding = consumer.onErrorDiscard();

            discarding.accept("foo", 1L);

            verify(consumer).accept("foo", 1L);
            verify(consumer).onErrorDiscard();
            verifyNoMoreInteractions(consumer);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingObjLongConsumer<String, IOException> consumer = Spied.throwingObjLongConsumer((s, l) -> {
                throw new IOException(s + l);
            });

            ObjLongConsumer<String> discarding = consumer.onErrorDiscard();

            discarding.accept("foo", 1L);

            verify(consumer).accept("foo", 1L);
            verify(consumer).onErrorDiscard();
            verifyNoMoreInteractions(consumer);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingObjLongConsumer<String, IOException> consumer = Spied.throwingObjLongConsumer((s, l) -> throwable.throwUnchecked(s + l));

            ObjLongConsumer<String> discarding = consumer.onErrorDiscard();

            Throwable thrown = assertThrows(throwable.throwableType(), () -> discarding.accept("foo", 1L));
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
            ThrowingObjLongConsumer<String, IOException> consumer = Spied.throwingObjLongConsumer(ThrowingObjLongConsumerTest::ignore);

            ObjLongConsumer<String> unchecked = consumer.unchecked();

            unchecked.accept("foo", 1L);

            verify(consumer).accept("foo", 1L);
            verify(consumer).unchecked();
            verify(consumer).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(consumer);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingObjLongConsumer<String, IOException> consumer = Spied.throwingObjLongConsumer((s, l) -> {
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

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingObjLongConsumer<String, IOException> consumer = Spied.throwingObjLongConsumer((s, l) -> throwable.throwUnchecked(s + l));

            ObjLongConsumer<String> unchecked = consumer.unchecked();

            Throwable thrown = assertThrows(throwable.throwableType(), () -> unchecked.accept("foo", 1L));
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
            assertThrows(NullPointerException.class, () -> ThrowingObjLongConsumer.of(null));
        }

        @Test
        void testNonNullArgument() throws IOException {
            ThrowingObjLongConsumer<String, IOException> delegate = Spied.throwingObjLongConsumer(ThrowingObjLongConsumerTest::ignore);

            ThrowingObjLongConsumer<String, IOException> consumer = ThrowingObjLongConsumer.of(delegate);

            consumer.accept("foo", 1L);

            verify(delegate).accept("foo", 1L);
            verifyNoMoreInteractions(delegate);
        }
    }

    @Nested
    class UncheckedFactory {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> ThrowingObjLongConsumer.unchecked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ThrowingObjLongConsumer<String, IOException> consumer = Spied.throwingObjLongConsumer(ThrowingObjLongConsumerTest::ignore);

            ObjLongConsumer<String> unchecked = ThrowingObjLongConsumer.unchecked(consumer);

            unchecked.accept("foo", 1L);

            verify(consumer).unchecked();
            verify(consumer).onErrorThrowAsUnchecked(any());
            verify(consumer).accept("foo", 1L);
            verifyNoMoreInteractions(consumer);
        }

        @Test
        void testArgumentThrowsChecked() throws IOException {
            ThrowingObjLongConsumer<String, IOException> consumer = Spied.throwingObjLongConsumer((s, l) -> {
                throw new IOException(s + l);
            });

            ObjLongConsumer<String> unchecked = ThrowingObjLongConsumer.unchecked(consumer);

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.accept("foo", 1L));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo1", cause.getMessage());

            verify(consumer).unchecked();
            verify(consumer).onErrorThrowAsUnchecked(any());
            verify(consumer).accept("foo", 1L);
            verifyNoMoreInteractions(consumer);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testArgumentThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingObjLongConsumer<String, IOException> consumer = Spied.throwingObjLongConsumer((s, l) -> throwable.throwUnchecked(s + l));

            ObjLongConsumer<String> unchecked = ThrowingObjLongConsumer.unchecked(consumer);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> unchecked.accept("foo", 1L));
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
            assertThrows(NullPointerException.class, () -> ThrowingObjLongConsumer.checked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ObjLongConsumer<String> delegate = Spied.objLongConsumer(ThrowingObjLongConsumerTest::ignore);

            ThrowingObjLongConsumer<String, IOException> consumer = ThrowingObjLongConsumer.checked(delegate);

            consumer.accept("foo", 1L);

            verify(delegate).accept("foo", 1L);
            verifyNoMoreInteractions(delegate);
        }

        @Test
        void testArgumentThrowsUnchecked() {
            ThrowingObjLongConsumer<String, IOException> consumer = ThrowingObjLongConsumer.checked((s, l) -> {
                throw UncheckedException.withoutStackTrace(s + l, new IOException());
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
            ObjLongConsumer<String> consumer = ThrowingObjLongConsumerTest::ignore;

            assertThrows(NullPointerException.class, () -> ThrowingObjLongConsumer.checked(null, IOException.class));
            assertThrows(NullPointerException.class, () -> ThrowingObjLongConsumer.checked(consumer, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ObjLongConsumer<String> delegate = Spied.objLongConsumer(ThrowingObjLongConsumerTest::ignore);

            ThrowingObjLongConsumer<String, IOException> consumer = ThrowingObjLongConsumer.checked(delegate, IOException.class);

            consumer.accept("foo", 1L);

            verify(delegate).accept("foo", 1L);
            verifyNoMoreInteractions(delegate);
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                ThrowingObjLongConsumer<String, IOException> consumer = ThrowingObjLongConsumer.checked((s, l) -> {
                    throw UncheckedException.withoutStackTrace(new IOException(s + l));
                }, IOException.class);

                IOException thrown = assertThrows(IOException.class, () -> consumer.accept("foo", 1L));
                assertEquals("foo1", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                ThrowingObjLongConsumer<String, IOException> consumer = ThrowingObjLongConsumer.checked((s, l) -> {
                    throw UncheckedException.withoutStackTrace(new FileNotFoundException(s + l));
                }, IOException.class);

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class, () -> consumer.accept("foo", 1L));
                assertEquals("foo1", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                ThrowingObjLongConsumer<String, IOException> consumer = ThrowingObjLongConsumer.checked((s, l) -> {
                    throw UncheckedException.withoutStackTrace(new ParseException(s + l, 0));
                }, IOException.class);

                UncheckedException thrown = assertThrows(UncheckedException.class, () -> consumer.accept("foo", 1L));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("foo1", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            ThrowingObjLongConsumer<String, IOException> consumer = ThrowingObjLongConsumer.checked((s, l) -> {
                throw new IllegalStateException(s + l);
            }, IOException.class);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> consumer.accept("foo", 1L));
            assertEquals("foo1", thrown.getMessage());
        }
    }

    @SuppressWarnings("unused")
    private static void ignore(String s, long l) {
        // do nothing
    }
}
