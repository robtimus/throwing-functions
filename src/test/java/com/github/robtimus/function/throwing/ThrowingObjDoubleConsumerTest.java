/*
 * ThrowingObjDoubleConsumerTest.java
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
import java.util.function.ObjDoubleConsumer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

@SuppressWarnings("nls")
class ThrowingObjDoubleConsumerTest {

    @Nested
    class OnErrorThrowAsChecked {

        @Test
        void testNullArgument() {
            ThrowingObjDoubleConsumer<String, IOException> consumer = Spied.throwingObjDoubleConsumer(ThrowingObjDoubleConsumerTest::ignore);

            assertThrows(NullPointerException.class, () -> consumer.onErrorThrowAsChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingObjDoubleConsumer<String, IOException> consumer = Spied.throwingObjDoubleConsumer(ThrowingObjDoubleConsumerTest::ignore);

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingObjDoubleConsumer<String, ExecutionException> throwing = consumer.onErrorThrowAsChecked(errorMapper);

            throwing.accept("foo", 1D);

            verify(consumer).accept("foo", 1D);
            verify(consumer).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(consumer, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingObjDoubleConsumer<String, IOException> consumer = Spied.throwingObjDoubleConsumer((s, d) -> {
                throw new IOException(s + d);
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingObjDoubleConsumer<String, ExecutionException> throwing = consumer.onErrorThrowAsChecked(errorMapper);

            ExecutionException thrown = assertThrows(ExecutionException.class, () -> throwing.accept("foo", 1D));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo1.0", cause.getMessage());

            verify(consumer).accept("foo", 1D);
            verify(consumer).onErrorThrowAsChecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(consumer, errorMapper);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingObjDoubleConsumer<String, IOException> consumer = Spied.throwingObjDoubleConsumer((s, d) -> throwable.throwUnchecked(s + d));

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingObjDoubleConsumer<String, ExecutionException> throwing = consumer.onErrorThrowAsChecked(errorMapper);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> throwing.accept("foo", 1D));
            assertEquals("foo1.0", thrown.getMessage());

            verify(consumer).accept("foo", 1D);
            verify(consumer).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(consumer, errorMapper);
        }
    }

    @Nested
    class OnErrorThrowAsUnchecked {

        @Test
        void testNullArgument() {
            ThrowingObjDoubleConsumer<String, IOException> consumer = Spied.throwingObjDoubleConsumer(ThrowingObjDoubleConsumerTest::ignore);

            assertThrows(NullPointerException.class, () -> consumer.onErrorThrowAsUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingObjDoubleConsumer<String, IOException> consumer = Spied.throwingObjDoubleConsumer(ThrowingObjDoubleConsumerTest::ignore);

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            ObjDoubleConsumer<String> throwing = consumer.onErrorThrowAsUnchecked(errorMapper);

            throwing.accept("foo", 1D);

            verify(consumer).accept("foo", 1D);
            verify(consumer).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(consumer, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingObjDoubleConsumer<String, IOException> consumer = Spied.throwingObjDoubleConsumer((s, d) -> {
                throw new IOException(s + d);
            });

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            ObjDoubleConsumer<String> throwing = consumer.onErrorThrowAsUnchecked(errorMapper);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> throwing.accept("foo", 1D));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo1.0", cause.getMessage());

            verify(consumer).accept("foo", 1D);
            verify(consumer).onErrorThrowAsUnchecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(consumer, errorMapper);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingObjDoubleConsumer<String, IOException> consumer = Spied.throwingObjDoubleConsumer((s, d) -> throwable.throwUnchecked(s + d));

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            ObjDoubleConsumer<String> throwing = consumer.onErrorThrowAsUnchecked(errorMapper);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> throwing.accept("foo", 1D));
            assertEquals("foo1.0", thrown.getMessage());

            verify(consumer).accept("foo", 1D);
            verify(consumer).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(consumer, errorMapper);
        }
    }

    @Nested
    class OnErrorHandleChecked {

        @Test
        void testNullArgument() {
            ThrowingObjDoubleConsumer<String, IOException> consumer = Spied.throwingObjDoubleConsumer(ThrowingObjDoubleConsumerTest::ignore);

            assertThrows(NullPointerException.class, () -> consumer.onErrorHandleChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingObjDoubleConsumer<String, IOException> consumer = Spied.throwingObjDoubleConsumer(ThrowingObjDoubleConsumerTest::ignore);

            ThrowingConsumer<IOException, ExecutionException> errorHandler = Spied.throwingConsumer(Exception::getMessage);

            ThrowingObjDoubleConsumer<String, ExecutionException> handling = consumer.onErrorHandleChecked(errorHandler);

            handling.accept("foo", 1D);

            verify(consumer).accept("foo", 1D);
            verify(consumer).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(consumer, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException, ExecutionException {
                ThrowingObjDoubleConsumer<String, IOException> consumer = Spied.throwingObjDoubleConsumer((s, d) -> {
                    throw new IOException(s + d);
                });

                ThrowingConsumer<IOException, ExecutionException> errorHandler = Spied.throwingConsumer(Exception::getMessage);

                ThrowingObjDoubleConsumer<String, ExecutionException> handling = consumer.onErrorHandleChecked(errorHandler);

                handling.accept("foo", 1D);

                verify(consumer).accept("foo", 1D);
                verify(consumer).onErrorHandleChecked(errorHandler);
                verify(errorHandler).accept(any());
                verifyNoMoreInteractions(consumer, errorHandler);
            }

            @Test
            void testHandlerThrowsChecked() throws IOException, ExecutionException {
                ThrowingObjDoubleConsumer<String, IOException> consumer = Spied.throwingObjDoubleConsumer((s, d) -> {
                    throw new IOException(s + d);
                });

                ThrowingConsumer<IOException, ExecutionException> errorHandler = Spied.throwingConsumer(e -> {
                    throw new ExecutionException(e);
                });

                ThrowingObjDoubleConsumer<String, ExecutionException> handling = consumer.onErrorHandleChecked(errorHandler);

                ExecutionException thrown = assertThrows(ExecutionException.class, () -> handling.accept("foo", 1D));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foo1.0", cause.getMessage());

                verify(consumer).accept("foo", 1D);
                verify(consumer).onErrorHandleChecked(errorHandler);
                verify(errorHandler).accept(any());
                verifyNoMoreInteractions(consumer, errorHandler);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testHandlerThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ExecutionException {
                ThrowingObjDoubleConsumer<String, IOException> consumer = Spied.throwingObjDoubleConsumer((s, d) -> {
                    throw new IOException(s + d);
                });

                ThrowingConsumer<IOException, ExecutionException> errorHandler = Spied.throwingConsumer(throwable::throwUnchecked);

                ThrowingObjDoubleConsumer<String, ExecutionException> handling = consumer.onErrorHandleChecked(errorHandler);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.accept("foo", 1D));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foo1.0", cause.getMessage());

                verify(consumer).accept("foo", 1D);
                verify(consumer).onErrorHandleChecked(errorHandler);
                verify(errorHandler).accept(any());
                verifyNoMoreInteractions(consumer, errorHandler);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingObjDoubleConsumer<String, IOException> consumer = Spied.throwingObjDoubleConsumer((s, d) -> throwable.throwUnchecked(s + d));

            ThrowingConsumer<IOException, ExecutionException> errorHandler = Spied.throwingConsumer(Exception::getMessage);

            ThrowingObjDoubleConsumer<String, ExecutionException> handling = consumer.onErrorHandleChecked(errorHandler);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.accept("foo", 1D));
            assertEquals("foo1.0", thrown.getMessage());

            verify(consumer).accept("foo", 1D);
            verify(consumer).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(consumer, errorHandler);
        }
    }

    @Nested
    class OnErrorHandleUnchecked {

        @Test
        void testNullArgument() {
            ThrowingObjDoubleConsumer<String, IOException> consumer = Spied.throwingObjDoubleConsumer(ThrowingObjDoubleConsumerTest::ignore);

            assertThrows(NullPointerException.class, () -> consumer.onErrorHandleUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingObjDoubleConsumer<String, IOException> consumer = Spied.throwingObjDoubleConsumer(ThrowingObjDoubleConsumerTest::ignore);

            Consumer<IOException> errorHandler = Spied.consumer(Exception::getMessage);

            ObjDoubleConsumer<String> handling = consumer.onErrorHandleUnchecked(errorHandler);

            handling.accept("foo", 1D);

            verify(consumer).accept("foo", 1D);
            verify(consumer).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(consumer, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException {
                ThrowingObjDoubleConsumer<String, IOException> consumer = Spied.throwingObjDoubleConsumer((s, d) -> {
                    throw new IOException(s + d);
                });

                Consumer<IOException> errorHandler = Spied.consumer(Exception::getMessage);

                ObjDoubleConsumer<String> handling = consumer.onErrorHandleUnchecked(errorHandler);

                handling.accept("foo", 1D);

                verify(consumer).accept("foo", 1D);
                verify(consumer).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).accept(any());
                verifyNoMoreInteractions(consumer, errorHandler);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testHandlerThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingObjDoubleConsumer<String, IOException> consumer = Spied.throwingObjDoubleConsumer((s, d) -> {
                    throw new IOException(s + d);
                });

                Consumer<IOException> errorHandler = Spied.consumer(throwable::throwUnchecked);

                ObjDoubleConsumer<String> handling = consumer.onErrorHandleUnchecked(errorHandler);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.accept("foo", 1D));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foo1.0", cause.getMessage());

                verify(consumer).accept("foo", 1D);
                verify(consumer).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).accept(any());
                verifyNoMoreInteractions(consumer, errorHandler);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingObjDoubleConsumer<String, IOException> consumer = Spied.throwingObjDoubleConsumer((s, d) -> throwable.throwUnchecked(s + d));

            Consumer<IOException> errorHandler = Spied.consumer(Exception::getMessage);

            ObjDoubleConsumer<String> handling = consumer.onErrorHandleUnchecked(errorHandler);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.accept("foo", 1D));
            assertEquals("foo1.0", thrown.getMessage());

            verify(consumer).accept("foo", 1D);
            verify(consumer).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(consumer, errorHandler);
        }
    }

    @Nested
    class OnErrorAcceptChecked {

        @Test
        void testNullArgument() {
            ThrowingObjDoubleConsumer<String, IOException> consumer = Spied.throwingObjDoubleConsumer(ThrowingObjDoubleConsumerTest::ignore);

            assertThrows(NullPointerException.class, () -> consumer.onErrorAcceptChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingObjDoubleConsumer<String, IOException> consumer = Spied.throwingObjDoubleConsumer(ThrowingObjDoubleConsumerTest::ignore);

            ThrowingObjDoubleConsumer<String, ExecutionException> fallback = Spied.throwingObjDoubleConsumer(ThrowingObjDoubleConsumerTest::ignore);

            ThrowingObjDoubleConsumer<String, ExecutionException> applying = consumer.onErrorAcceptChecked(fallback);

            applying.accept("foo", 1D);

            verify(consumer).accept("foo", 1D);
            verify(consumer).onErrorAcceptChecked(fallback);
            verifyNoMoreInteractions(consumer, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                ThrowingObjDoubleConsumer<String, IOException> consumer = Spied.throwingObjDoubleConsumer((s, d) -> {
                    throw new IOException(s + d);
                });

                ThrowingObjDoubleConsumer<String, ParseException> fallback = Spied.throwingObjDoubleConsumer(ThrowingObjDoubleConsumerTest::ignore);

                ThrowingObjDoubleConsumer<String, ParseException> applying = consumer.onErrorAcceptChecked(fallback);

                applying.accept("foo", 1D);

                verify(consumer).accept("foo", 1D);
                verify(consumer).onErrorAcceptChecked(fallback);
                verify(fallback).accept("foo", 1D);
                verifyNoMoreInteractions(consumer, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                ThrowingObjDoubleConsumer<String, IOException> consumer = Spied.throwingObjDoubleConsumer((s, d) -> {
                    throw new IOException(s + d);
                });

                ThrowingObjDoubleConsumer<String, ParseException> fallback = Spied.throwingObjDoubleConsumer((s, d) -> {
                    throw new ParseException(s + d, 0);
                });

                ThrowingObjDoubleConsumer<String, ParseException> applying = consumer.onErrorAcceptChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> applying.accept("foo", 1D));
                assertEquals("foo1.0", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(consumer).accept("foo", 1D);
                verify(consumer).onErrorAcceptChecked(fallback);
                verify(fallback).accept("foo", 1D);
                verifyNoMoreInteractions(consumer, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ParseException {
                ThrowingObjDoubleConsumer<String, IOException> consumer = Spied.throwingObjDoubleConsumer((s, d) -> {
                    throw new IOException(s + d);
                });

                ThrowingObjDoubleConsumer<String, ParseException> fallback = Spied
                        .throwingObjDoubleConsumer((s, d) -> throwable.throwUnchecked(s + d));

                ThrowingObjDoubleConsumer<String, ParseException> applying = consumer.onErrorAcceptChecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.accept("foo", 1D));
                assertEquals("foo1.0", thrown.getMessage());

                verify(consumer).accept("foo", 1D);
                verify(consumer).onErrorAcceptChecked(fallback);
                verify(fallback).accept("foo", 1D);
                verifyNoMoreInteractions(consumer, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingObjDoubleConsumer<String, IOException> consumer = Spied.throwingObjDoubleConsumer((s, d) -> throwable.throwUnchecked(s + d));

            ThrowingObjDoubleConsumer<String, ParseException> fallback = Spied.throwingObjDoubleConsumer(ThrowingObjDoubleConsumerTest::ignore);

            ThrowingObjDoubleConsumer<String, ParseException> applying = consumer.onErrorAcceptChecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.accept("foo", 1D));
            assertEquals("foo1.0", thrown.getMessage());

            verify(consumer).accept("foo", 1D);
            verify(consumer).onErrorAcceptChecked(fallback);
            verifyNoMoreInteractions(consumer, fallback);
        }
    }

    @Nested
    class OnErrorAcceptUnchecked {

        @Test
        void testNullArgument() {
            ThrowingObjDoubleConsumer<String, IOException> consumer = Spied.throwingObjDoubleConsumer(ThrowingObjDoubleConsumerTest::ignore);

            assertThrows(NullPointerException.class, () -> consumer.onErrorAcceptUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingObjDoubleConsumer<String, IOException> consumer = Spied.throwingObjDoubleConsumer(ThrowingObjDoubleConsumerTest::ignore);

            ObjDoubleConsumer<String> fallback = Spied.objDoubleConsumer(ThrowingObjDoubleConsumerTest::ignore);

            ObjDoubleConsumer<String> applying = consumer.onErrorAcceptUnchecked(fallback);

            applying.accept("foo", 1D);

            verify(consumer).accept("foo", 1D);
            verify(consumer).onErrorAcceptUnchecked(fallback);
            verifyNoMoreInteractions(consumer, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                ThrowingObjDoubleConsumer<String, IOException> consumer = Spied.throwingObjDoubleConsumer((s, d) -> {
                    throw new IOException(s + d);
                });

                ObjDoubleConsumer<String> fallback = Spied.objDoubleConsumer(ThrowingObjDoubleConsumerTest::ignore);

                ObjDoubleConsumer<String> applying = consumer.onErrorAcceptUnchecked(fallback);

                applying.accept("foo", 1D);

                verify(consumer).accept("foo", 1D);
                verify(consumer).onErrorAcceptUnchecked(fallback);
                verify(fallback).accept("foo", 1D);
                verifyNoMoreInteractions(consumer, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingObjDoubleConsumer<String, IOException> consumer = Spied.throwingObjDoubleConsumer((s, d) -> {
                    throw new IOException(s + d);
                });

                ObjDoubleConsumer<String> fallback = Spied.objDoubleConsumer((s, d) -> throwable.throwUnchecked(s + d));

                ObjDoubleConsumer<String> applying = consumer.onErrorAcceptUnchecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.accept("foo", 1D));
                assertEquals("foo1.0", thrown.getMessage());

                verify(consumer).accept("foo", 1D);
                verify(consumer).onErrorAcceptUnchecked(fallback);
                verify(fallback).accept("foo", 1D);
                verifyNoMoreInteractions(consumer, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingObjDoubleConsumer<String, IOException> consumer = Spied.throwingObjDoubleConsumer((s, d) -> throwable.throwUnchecked(s + d));

            ObjDoubleConsumer<String> fallback = Spied.objDoubleConsumer(ThrowingObjDoubleConsumerTest::ignore);

            ObjDoubleConsumer<String> applying = consumer.onErrorAcceptUnchecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.accept("foo", 1D));
            assertEquals("foo1.0", thrown.getMessage());

            verify(consumer).accept("foo", 1D);
            verify(consumer).onErrorAcceptUnchecked(fallback);
            verifyNoMoreInteractions(consumer, fallback);
        }
    }

    @Nested
    class OnErrorDiscard {

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingObjDoubleConsumer<String, IOException> consumer = Spied.throwingObjDoubleConsumer(ThrowingObjDoubleConsumerTest::ignore);

            ObjDoubleConsumer<String> discarding = consumer.onErrorDiscard();

            discarding.accept("foo", 1D);

            verify(consumer).accept("foo", 1D);
            verify(consumer).onErrorDiscard();
            verifyNoMoreInteractions(consumer);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingObjDoubleConsumer<String, IOException> consumer = Spied.throwingObjDoubleConsumer((s, d) -> {
                throw new IOException(s + d);
            });

            ObjDoubleConsumer<String> discarding = consumer.onErrorDiscard();

            discarding.accept("foo", 1D);

            verify(consumer).accept("foo", 1D);
            verify(consumer).onErrorDiscard();
            verifyNoMoreInteractions(consumer);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingObjDoubleConsumer<String, IOException> consumer = Spied.throwingObjDoubleConsumer((s, d) -> throwable.throwUnchecked(s + d));

            ObjDoubleConsumer<String> discarding = consumer.onErrorDiscard();

            Throwable thrown = assertThrows(throwable.throwableType(), () -> discarding.accept("foo", 1D));
            assertEquals("foo1.0", thrown.getMessage());

            verify(consumer).accept("foo", 1D);
            verify(consumer).onErrorDiscard();
            verifyNoMoreInteractions(consumer);
        }
    }

    @Nested
    class Unchecked {

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingObjDoubleConsumer<String, IOException> consumer = Spied.throwingObjDoubleConsumer(ThrowingObjDoubleConsumerTest::ignore);

            ObjDoubleConsumer<String> unchecked = consumer.unchecked();

            unchecked.accept("foo", 1D);

            verify(consumer).accept("foo", 1D);
            verify(consumer).unchecked();
            verify(consumer).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(consumer);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingObjDoubleConsumer<String, IOException> consumer = Spied.throwingObjDoubleConsumer((s, d) -> {
                throw new IOException(s + d);
            });

            ObjDoubleConsumer<String> unchecked = consumer.unchecked();

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.accept("foo", 1D));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo1.0", cause.getMessage());

            verify(consumer).accept("foo", 1D);
            verify(consumer).unchecked();
            verify(consumer).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(consumer);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingObjDoubleConsumer<String, IOException> consumer = Spied.throwingObjDoubleConsumer((s, d) -> throwable.throwUnchecked(s + d));

            ObjDoubleConsumer<String> unchecked = consumer.unchecked();

            Throwable thrown = assertThrows(throwable.throwableType(), () -> unchecked.accept("foo", 1D));
            assertEquals("foo1.0", thrown.getMessage());

            verify(consumer).accept("foo", 1D);
            verify(consumer).unchecked();
            verify(consumer).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(consumer);
        }
    }

    @Nested
    class Of {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> ThrowingObjDoubleConsumer.of(null));
        }

        @Test
        void testNonNullArgument() throws IOException {
            ThrowingObjDoubleConsumer<String, IOException> delegate = Spied.throwingObjDoubleConsumer(ThrowingObjDoubleConsumerTest::ignore);

            ThrowingObjDoubleConsumer<String, IOException> consumer = ThrowingObjDoubleConsumer.of(delegate);

            consumer.accept("foo", 1D);

            verify(delegate).accept("foo", 1D);
            verifyNoMoreInteractions(delegate);
        }
    }

    @Nested
    class UncheckedFactory {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> ThrowingObjDoubleConsumer.unchecked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ThrowingObjDoubleConsumer<String, IOException> consumer = Spied.throwingObjDoubleConsumer(ThrowingObjDoubleConsumerTest::ignore);

            ObjDoubleConsumer<String> unchecked = ThrowingObjDoubleConsumer.unchecked(consumer);

            unchecked.accept("foo", 1D);

            verify(consumer).unchecked();
            verify(consumer).onErrorThrowAsUnchecked(any());
            verify(consumer).accept("foo", 1D);
            verifyNoMoreInteractions(consumer);
        }

        @Test
        void testArgumentThrowsChecked() throws IOException {
            ThrowingObjDoubleConsumer<String, IOException> consumer = Spied.throwingObjDoubleConsumer((s, d) -> {
                throw new IOException(s + d);
            });

            ObjDoubleConsumer<String> unchecked = ThrowingObjDoubleConsumer.unchecked(consumer);

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.accept("foo", 1D));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo1.0", cause.getMessage());

            verify(consumer).unchecked();
            verify(consumer).onErrorThrowAsUnchecked(any());
            verify(consumer).accept("foo", 1D);
            verifyNoMoreInteractions(consumer);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testArgumentThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingObjDoubleConsumer<String, IOException> consumer = Spied.throwingObjDoubleConsumer((s, d) -> throwable.throwUnchecked(s + d));

            ObjDoubleConsumer<String> unchecked = ThrowingObjDoubleConsumer.unchecked(consumer);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> unchecked.accept("foo", 1D));
            assertEquals("foo1.0", thrown.getMessage());

            verify(consumer).unchecked();
            verify(consumer).onErrorThrowAsUnchecked(any());
            verify(consumer).accept("foo", 1D);
            verifyNoMoreInteractions(consumer);
        }
    }

    @Nested
    class CheckedWrapper {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> ThrowingObjDoubleConsumer.checked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ObjDoubleConsumer<String> delegate = Spied.objDoubleConsumer(ThrowingObjDoubleConsumerTest::ignore);

            ThrowingObjDoubleConsumer<String, IOException> consumer = ThrowingObjDoubleConsumer.checked(delegate);

            consumer.accept("foo", 1D);

            verify(delegate).accept("foo", 1D);
            verifyNoMoreInteractions(delegate);
        }

        @Test
        void testArgumentThrowsUnchecked() {
            ThrowingObjDoubleConsumer<String, IOException> consumer = ThrowingObjDoubleConsumer.checked((s, d) -> {
                throw UncheckedException.withoutStackTrace(s + d, new IOException());
            });

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> consumer.accept("foo", 1D));
            assertEquals("foo1.0", thrown.getMessage());
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertNull(cause.getMessage());
        }
    }

    @Nested
    class CheckedUnwrapper {

        @Test
        void testNullArguments() {
            ObjDoubleConsumer<String> consumer = ThrowingObjDoubleConsumerTest::ignore;

            assertThrows(NullPointerException.class, () -> ThrowingObjDoubleConsumer.checked(null, IOException.class));
            assertThrows(NullPointerException.class, () -> ThrowingObjDoubleConsumer.checked(consumer, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ObjDoubleConsumer<String> delegate = Spied.objDoubleConsumer(ThrowingObjDoubleConsumerTest::ignore);

            ThrowingObjDoubleConsumer<String, IOException> consumer = ThrowingObjDoubleConsumer.checked(delegate, IOException.class);

            consumer.accept("foo", 1D);

            verify(delegate).accept("foo", 1D);
            verifyNoMoreInteractions(delegate);
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                ThrowingObjDoubleConsumer<String, IOException> consumer = ThrowingObjDoubleConsumer.checked((s, d) -> {
                    throw UncheckedException.withoutStackTrace(new IOException(s + d));
                }, IOException.class);

                IOException thrown = assertThrows(IOException.class, () -> consumer.accept("foo", 1D));
                assertEquals("foo1.0", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                ThrowingObjDoubleConsumer<String, IOException> consumer = ThrowingObjDoubleConsumer.checked((s, d) -> {
                    throw UncheckedException.withoutStackTrace(new FileNotFoundException(s + d));
                }, IOException.class);

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class, () -> consumer.accept("foo", 1D));
                assertEquals("foo1.0", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                ThrowingObjDoubleConsumer<String, IOException> consumer = ThrowingObjDoubleConsumer.checked((s, d) -> {
                    throw UncheckedException.withoutStackTrace(new ParseException(s + d, 0));
                }, IOException.class);

                UncheckedException thrown = assertThrows(UncheckedException.class, () -> consumer.accept("foo", 1D));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("foo1.0", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            ThrowingObjDoubleConsumer<String, IOException> consumer = ThrowingObjDoubleConsumer.checked((s, d) -> {
                throw new IllegalStateException(s + d);
            }, IOException.class);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> consumer.accept("foo", 1D));
            assertEquals("foo1.0", thrown.getMessage());
        }
    }

    @Nested
    class InvokeAndUnwrap {

        @Test
        void testNullArguments() throws IOException {
            ObjDoubleConsumer<String> consumer = Spied.objDoubleConsumer(ThrowingObjDoubleConsumerTest::ignore);

            assertThrows(NullPointerException.class, () -> ThrowingObjDoubleConsumer.invokeAndUnwrap(null, "foo", 1D, IOException.class));
            assertThrows(NullPointerException.class, () -> ThrowingObjDoubleConsumer.invokeAndUnwrap(consumer, "foo", 1D, null));

            ThrowingObjDoubleConsumer.invokeAndUnwrap(consumer, null, 1D, IOException.class);

            verify(consumer).accept(null, 1D);
            verifyNoMoreInteractions(consumer);
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ObjDoubleConsumer<String> consumer = Spied.objDoubleConsumer(ThrowingObjDoubleConsumerTest::ignore);

            ThrowingObjDoubleConsumer.invokeAndUnwrap(consumer, "foo", 1D, IOException.class);

            verify(consumer).accept("foo", 1D);
            verifyNoMoreInteractions(consumer);
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                ObjDoubleConsumer<String> consumer = (s, d) -> {
                    throw UncheckedException.withoutStackTrace(new IOException(s + d));
                };

                IOException thrown = assertThrows(IOException.class,
                        () -> ThrowingObjDoubleConsumer.invokeAndUnwrap(consumer, "foo", 1D, IOException.class));
                assertEquals("foo1.0", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                ObjDoubleConsumer<String> consumer = (s, d) -> {
                    throw UncheckedException.withoutStackTrace(new FileNotFoundException(s + d));
                };

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class,
                        () -> ThrowingObjDoubleConsumer.invokeAndUnwrap(consumer, "foo", 1D, IOException.class));
                assertEquals("foo1.0", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                ObjDoubleConsumer<String> consumer = (s, d) -> {
                    throw UncheckedException.withoutStackTrace(new ParseException(s + d, 0));
                };

                UncheckedException thrown = assertThrows(UncheckedException.class,
                        () -> ThrowingObjDoubleConsumer.invokeAndUnwrap(consumer, "foo", 1D, IOException.class));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("foo1.0", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            ObjDoubleConsumer<String> consumer = (s, d) -> {
                throw new IllegalStateException(s + d);
            };

            IllegalStateException thrown = assertThrows(IllegalStateException.class,
                    () -> ThrowingObjDoubleConsumer.invokeAndUnwrap(consumer, "foo", 1D, IOException.class));
            assertEquals("foo1.0", thrown.getMessage());
        }
    }

    @SuppressWarnings("unused")
    private static void ignore(String s, double d) {
        // do nothing
    }
}
