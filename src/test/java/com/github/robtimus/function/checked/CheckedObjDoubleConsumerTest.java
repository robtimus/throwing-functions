/*
 * CheckedObjDoubleConsumerTest.java
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
import java.util.function.ObjDoubleConsumer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

@SuppressWarnings("nls")
class CheckedObjDoubleConsumerTest {

    @Nested
    class OnErrorThrowAsChecked {

        @Test
        void testNullArgument() {
            CheckedObjDoubleConsumer<String, IOException> consumer = Spied.checkedObjDoubleConsumer(CheckedObjDoubleConsumerTest::ignore);

            assertThrows(NullPointerException.class, () -> consumer.onErrorThrowAsChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedObjDoubleConsumer<String, IOException> consumer = Spied.checkedObjDoubleConsumer(CheckedObjDoubleConsumerTest::ignore);

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedObjDoubleConsumer<String, ExecutionException> throwing = consumer.onErrorThrowAsChecked(errorMapper);

            throwing.accept("foo", 1D);

            verify(consumer).accept("foo", 1D);
            verify(consumer).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(consumer, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedObjDoubleConsumer<String, IOException> consumer = Spied.checkedObjDoubleConsumer((s, d) -> {
                throw new IOException(s + d);
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedObjDoubleConsumer<String, ExecutionException> throwing = consumer.onErrorThrowAsChecked(errorMapper);

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
            CheckedObjDoubleConsumer<String, IOException> consumer = Spied.checkedObjDoubleConsumer((s, d) -> throwable.throwUnchecked(s + d));

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedObjDoubleConsumer<String, ExecutionException> throwing = consumer.onErrorThrowAsChecked(errorMapper);

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
            CheckedObjDoubleConsumer<String, IOException> consumer = Spied.checkedObjDoubleConsumer(CheckedObjDoubleConsumerTest::ignore);

            assertThrows(NullPointerException.class, () -> consumer.onErrorThrowAsUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedObjDoubleConsumer<String, IOException> consumer = Spied.checkedObjDoubleConsumer(CheckedObjDoubleConsumerTest::ignore);

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            ObjDoubleConsumer<String> throwing = consumer.onErrorThrowAsUnchecked(errorMapper);

            throwing.accept("foo", 1D);

            verify(consumer).accept("foo", 1D);
            verify(consumer).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(consumer, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedObjDoubleConsumer<String, IOException> consumer = Spied.checkedObjDoubleConsumer((s, d) -> {
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
            CheckedObjDoubleConsumer<String, IOException> consumer = Spied.checkedObjDoubleConsumer((s, d) -> throwable.throwUnchecked(s + d));

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
            CheckedObjDoubleConsumer<String, IOException> consumer = Spied.checkedObjDoubleConsumer(CheckedObjDoubleConsumerTest::ignore);

            assertThrows(NullPointerException.class, () -> consumer.onErrorHandleChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedObjDoubleConsumer<String, IOException> consumer = Spied.checkedObjDoubleConsumer(CheckedObjDoubleConsumerTest::ignore);

            CheckedConsumer<IOException, ExecutionException> errorHandler = Spied.checkedConsumer(Exception::getMessage);

            CheckedObjDoubleConsumer<String, ExecutionException> handling = consumer.onErrorHandleChecked(errorHandler);

            handling.accept("foo", 1D);

            verify(consumer).accept("foo", 1D);
            verify(consumer).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(consumer, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException, ExecutionException {
                CheckedObjDoubleConsumer<String, IOException> consumer = Spied.checkedObjDoubleConsumer((s, d) -> {
                    throw new IOException(s + d);
                });

                CheckedConsumer<IOException, ExecutionException> errorHandler = Spied.checkedConsumer(Exception::getMessage);

                CheckedObjDoubleConsumer<String, ExecutionException> handling = consumer.onErrorHandleChecked(errorHandler);

                handling.accept("foo", 1D);

                verify(consumer).accept("foo", 1D);
                verify(consumer).onErrorHandleChecked(errorHandler);
                verify(errorHandler).accept(any());
                verifyNoMoreInteractions(consumer, errorHandler);
            }

            @Test
            void testHandlerThrowsChecked() throws IOException, ExecutionException {
                CheckedObjDoubleConsumer<String, IOException> consumer = Spied.checkedObjDoubleConsumer((s, d) -> {
                    throw new IOException(s + d);
                });

                CheckedConsumer<IOException, ExecutionException> errorHandler = Spied.checkedConsumer(e -> {
                    throw new ExecutionException(e);
                });

                CheckedObjDoubleConsumer<String, ExecutionException> handling = consumer.onErrorHandleChecked(errorHandler);

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
                CheckedObjDoubleConsumer<String, IOException> consumer = Spied.checkedObjDoubleConsumer((s, d) -> {
                    throw new IOException(s + d);
                });

                CheckedConsumer<IOException, ExecutionException> errorHandler = Spied.checkedConsumer(throwable::throwUnchecked);

                CheckedObjDoubleConsumer<String, ExecutionException> handling = consumer.onErrorHandleChecked(errorHandler);

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
            CheckedObjDoubleConsumer<String, IOException> consumer = Spied.checkedObjDoubleConsumer((s, d) -> throwable.throwUnchecked(s + d));

            CheckedConsumer<IOException, ExecutionException> errorHandler = Spied.checkedConsumer(Exception::getMessage);

            CheckedObjDoubleConsumer<String, ExecutionException> handling = consumer.onErrorHandleChecked(errorHandler);

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
            CheckedObjDoubleConsumer<String, IOException> consumer = Spied.checkedObjDoubleConsumer(CheckedObjDoubleConsumerTest::ignore);

            assertThrows(NullPointerException.class, () -> consumer.onErrorHandleUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedObjDoubleConsumer<String, IOException> consumer = Spied.checkedObjDoubleConsumer(CheckedObjDoubleConsumerTest::ignore);

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
                CheckedObjDoubleConsumer<String, IOException> consumer = Spied.checkedObjDoubleConsumer((s, d) -> {
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
                CheckedObjDoubleConsumer<String, IOException> consumer = Spied.checkedObjDoubleConsumer((s, d) -> {
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
            CheckedObjDoubleConsumer<String, IOException> consumer = Spied.checkedObjDoubleConsumer((s, d) -> throwable.throwUnchecked(s + d));

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
            CheckedObjDoubleConsumer<String, IOException> consumer = Spied.checkedObjDoubleConsumer(CheckedObjDoubleConsumerTest::ignore);

            assertThrows(NullPointerException.class, () -> consumer.onErrorAcceptChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedObjDoubleConsumer<String, IOException> consumer = Spied.checkedObjDoubleConsumer(CheckedObjDoubleConsumerTest::ignore);

            CheckedObjDoubleConsumer<String, ExecutionException> fallback = Spied.checkedObjDoubleConsumer(CheckedObjDoubleConsumerTest::ignore);

            CheckedObjDoubleConsumer<String, ExecutionException> applying = consumer.onErrorAcceptChecked(fallback);

            applying.accept("foo", 1D);

            verify(consumer).accept("foo", 1D);
            verify(consumer).onErrorAcceptChecked(fallback);
            verifyNoMoreInteractions(consumer, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                CheckedObjDoubleConsumer<String, IOException> consumer = Spied.checkedObjDoubleConsumer((s, d) -> {
                    throw new IOException(s + d);
                });

                CheckedObjDoubleConsumer<String, ParseException> fallback = Spied.checkedObjDoubleConsumer(CheckedObjDoubleConsumerTest::ignore);

                CheckedObjDoubleConsumer<String, ParseException> applying = consumer.onErrorAcceptChecked(fallback);

                applying.accept("foo", 1D);

                verify(consumer).accept("foo", 1D);
                verify(consumer).onErrorAcceptChecked(fallback);
                verify(fallback).accept("foo", 1D);
                verifyNoMoreInteractions(consumer, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                CheckedObjDoubleConsumer<String, IOException> consumer = Spied.checkedObjDoubleConsumer((s, d) -> {
                    throw new IOException(s + d);
                });

                CheckedObjDoubleConsumer<String, ParseException> fallback = Spied.checkedObjDoubleConsumer((s, d) -> {
                    throw new ParseException(s + d, 0);
                });

                CheckedObjDoubleConsumer<String, ParseException> applying = consumer.onErrorAcceptChecked(fallback);

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
                CheckedObjDoubleConsumer<String, IOException> consumer = Spied.checkedObjDoubleConsumer((s, d) -> {
                    throw new IOException(s + d);
                });

                CheckedObjDoubleConsumer<String, ParseException> fallback = Spied.checkedObjDoubleConsumer((s, d) -> throwable.throwUnchecked(s + d));

                CheckedObjDoubleConsumer<String, ParseException> applying = consumer.onErrorAcceptChecked(fallback);

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
            CheckedObjDoubleConsumer<String, IOException> consumer = Spied.checkedObjDoubleConsumer((s, d) -> throwable.throwUnchecked(s + d));

            CheckedObjDoubleConsumer<String, ParseException> fallback = Spied.checkedObjDoubleConsumer(CheckedObjDoubleConsumerTest::ignore);

            CheckedObjDoubleConsumer<String, ParseException> applying = consumer.onErrorAcceptChecked(fallback);

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
            CheckedObjDoubleConsumer<String, IOException> consumer = Spied.checkedObjDoubleConsumer(CheckedObjDoubleConsumerTest::ignore);

            assertThrows(NullPointerException.class, () -> consumer.onErrorAcceptUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedObjDoubleConsumer<String, IOException> consumer = Spied.checkedObjDoubleConsumer(CheckedObjDoubleConsumerTest::ignore);

            ObjDoubleConsumer<String> fallback = Spied.objDoubleConsumer(CheckedObjDoubleConsumerTest::ignore);

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
                CheckedObjDoubleConsumer<String, IOException> consumer = Spied.checkedObjDoubleConsumer((s, d) -> {
                    throw new IOException(s + d);
                });

                ObjDoubleConsumer<String> fallback = Spied.objDoubleConsumer(CheckedObjDoubleConsumerTest::ignore);

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
                CheckedObjDoubleConsumer<String, IOException> consumer = Spied.checkedObjDoubleConsumer((s, d) -> {
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
            CheckedObjDoubleConsumer<String, IOException> consumer = Spied.checkedObjDoubleConsumer((s, d) -> throwable.throwUnchecked(s + d));

            ObjDoubleConsumer<String> fallback = Spied.objDoubleConsumer(CheckedObjDoubleConsumerTest::ignore);

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
            CheckedObjDoubleConsumer<String, IOException> consumer = Spied.checkedObjDoubleConsumer(CheckedObjDoubleConsumerTest::ignore);

            ObjDoubleConsumer<String> discarding = consumer.onErrorDiscard();

            discarding.accept("foo", 1D);

            verify(consumer).accept("foo", 1D);
            verify(consumer).onErrorDiscard();
            verifyNoMoreInteractions(consumer);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedObjDoubleConsumer<String, IOException> consumer = Spied.checkedObjDoubleConsumer((s, d) -> {
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
            CheckedObjDoubleConsumer<String, IOException> consumer = Spied.checkedObjDoubleConsumer((s, d) -> throwable.throwUnchecked(s + d));

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
            CheckedObjDoubleConsumer<String, IOException> consumer = Spied.checkedObjDoubleConsumer(CheckedObjDoubleConsumerTest::ignore);

            ObjDoubleConsumer<String> unchecked = consumer.unchecked();

            unchecked.accept("foo", 1D);

            verify(consumer).accept("foo", 1D);
            verify(consumer).unchecked();
            verify(consumer).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(consumer);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedObjDoubleConsumer<String, IOException> consumer = Spied.checkedObjDoubleConsumer((s, d) -> {
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
            CheckedObjDoubleConsumer<String, IOException> consumer = Spied.checkedObjDoubleConsumer((s, d) -> throwable.throwUnchecked(s + d));

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
            assertThrows(NullPointerException.class, () -> CheckedObjDoubleConsumer.of(null));
        }

        @Test
        void testNonNullArgument() throws IOException {
            CheckedObjDoubleConsumer<String, IOException> delegate = Spied.checkedObjDoubleConsumer(CheckedObjDoubleConsumerTest::ignore);

            CheckedObjDoubleConsumer<String, IOException> consumer = CheckedObjDoubleConsumer.of(delegate);

            consumer.accept("foo", 1D);

            verify(delegate).accept("foo", 1D);
            verifyNoMoreInteractions(delegate);
        }
    }

    @Nested
    class UncheckedFactory {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> CheckedObjDoubleConsumer.unchecked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedObjDoubleConsumer<String, IOException> consumer = Spied.checkedObjDoubleConsumer(CheckedObjDoubleConsumerTest::ignore);

            ObjDoubleConsumer<String> unchecked = CheckedObjDoubleConsumer.unchecked(consumer);

            unchecked.accept("foo", 1D);

            verify(consumer).unchecked();
            verify(consumer).onErrorThrowAsUnchecked(any());
            verify(consumer).accept("foo", 1D);
            verifyNoMoreInteractions(consumer);
        }

        @Test
        void testArgumentThrowsChecked() throws IOException {
            CheckedObjDoubleConsumer<String, IOException> consumer = Spied.checkedObjDoubleConsumer((s, d) -> {
                throw new IOException(s + d);
            });

            ObjDoubleConsumer<String> unchecked = CheckedObjDoubleConsumer.unchecked(consumer);

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
            CheckedObjDoubleConsumer<String, IOException> consumer = Spied.checkedObjDoubleConsumer((s, d) -> throwable.throwUnchecked(s + d));

            ObjDoubleConsumer<String> unchecked = CheckedObjDoubleConsumer.unchecked(consumer);

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
            assertThrows(NullPointerException.class, () -> CheckedObjDoubleConsumer.checked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ObjDoubleConsumer<String> delegate = Spied.objDoubleConsumer(CheckedObjDoubleConsumerTest::ignore);

            CheckedObjDoubleConsumer<String, IOException> consumer = CheckedObjDoubleConsumer.checked(delegate);

            consumer.accept("foo", 1D);

            verify(delegate).accept("foo", 1D);
            verifyNoMoreInteractions(delegate);
        }

        @Test
        void testArgumentThrowsUnchecked() {
            CheckedObjDoubleConsumer<String, IOException> consumer = CheckedObjDoubleConsumer.checked((s, d) -> {
                throw new UncheckedException(s + d, new IOException());
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
            ObjDoubleConsumer<String> consumer = CheckedObjDoubleConsumerTest::ignore;

            assertThrows(NullPointerException.class, () -> CheckedObjDoubleConsumer.checked(null, IOException.class));
            assertThrows(NullPointerException.class, () -> CheckedObjDoubleConsumer.checked(consumer, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ObjDoubleConsumer<String> delegate = Spied.objDoubleConsumer(CheckedObjDoubleConsumerTest::ignore);

            CheckedObjDoubleConsumer<String, IOException> consumer = CheckedObjDoubleConsumer.checked(delegate, IOException.class);

            consumer.accept("foo", 1D);

            verify(delegate).accept("foo", 1D);
            verifyNoMoreInteractions(delegate);
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                CheckedObjDoubleConsumer<String, IOException> consumer = CheckedObjDoubleConsumer.checked((s, d) -> {
                    throw new UncheckedException(new IOException(s + d));
                }, IOException.class);

                IOException thrown = assertThrows(IOException.class, () -> consumer.accept("foo", 1D));
                assertEquals("foo1.0", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                CheckedObjDoubleConsumer<String, IOException> consumer = CheckedObjDoubleConsumer.checked((s, d) -> {
                    throw new UncheckedException(new FileNotFoundException(s + d));
                }, IOException.class);

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class, () -> consumer.accept("foo", 1D));
                assertEquals("foo1.0", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                CheckedObjDoubleConsumer<String, IOException> consumer = CheckedObjDoubleConsumer.checked((s, d) -> {
                    throw new UncheckedException(new ParseException(s + d, 0));
                }, IOException.class);

                UncheckedException thrown = assertThrows(UncheckedException.class, () -> consumer.accept("foo", 1D));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("foo1.0", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            CheckedObjDoubleConsumer<String, IOException> consumer = CheckedObjDoubleConsumer.checked((s, d) -> {
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
            ObjDoubleConsumer<String> consumer = Spied.objDoubleConsumer(CheckedObjDoubleConsumerTest::ignore);

            assertThrows(NullPointerException.class, () -> CheckedObjDoubleConsumer.invokeAndUnwrap(null, "foo", 1D, IOException.class));
            assertThrows(NullPointerException.class, () -> CheckedObjDoubleConsumer.invokeAndUnwrap(consumer, "foo", 1D, null));

            CheckedObjDoubleConsumer.invokeAndUnwrap(consumer, null, 1D, IOException.class);

            verify(consumer).accept(null, 1D);
            verifyNoMoreInteractions(consumer);
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ObjDoubleConsumer<String> consumer = Spied.objDoubleConsumer(CheckedObjDoubleConsumerTest::ignore);

            CheckedObjDoubleConsumer.invokeAndUnwrap(consumer, "foo", 1D, IOException.class);

            verify(consumer).accept("foo", 1D);
            verifyNoMoreInteractions(consumer);
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                ObjDoubleConsumer<String> consumer = (s, d) -> {
                    throw new UncheckedException(new IOException(s + d));
                };

                IOException thrown = assertThrows(IOException.class,
                        () -> CheckedObjDoubleConsumer.invokeAndUnwrap(consumer, "foo", 1D, IOException.class));
                assertEquals("foo1.0", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                ObjDoubleConsumer<String> consumer = (s, d) -> {
                    throw new UncheckedException(new FileNotFoundException(s + d));
                };

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class,
                        () -> CheckedObjDoubleConsumer.invokeAndUnwrap(consumer, "foo", 1D, IOException.class));
                assertEquals("foo1.0", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                ObjDoubleConsumer<String> consumer = (s, d) -> {
                    throw new UncheckedException(new ParseException(s + d, 0));
                };

                UncheckedException thrown = assertThrows(UncheckedException.class,
                        () -> CheckedObjDoubleConsumer.invokeAndUnwrap(consumer, "foo", 1D, IOException.class));
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
                    () -> CheckedObjDoubleConsumer.invokeAndUnwrap(consumer, "foo", 1D, IOException.class));
            assertEquals("foo1.0", thrown.getMessage());
        }
    }

    @SuppressWarnings("unused")
    private static void ignore(String s, double d) {
        // do nothing
    }
}
