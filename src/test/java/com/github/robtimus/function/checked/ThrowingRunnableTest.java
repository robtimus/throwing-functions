/*
 * ThrowingRunnableTest.java
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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

@SuppressWarnings("nls")
class ThrowingRunnableTest {

    @Nested
    class OnErrorThrowAsChecked {

        @Test
        void testNullArgument() {
            ThrowingRunnable<IOException> runnable = Spied.checkedRunnable("foo"::toUpperCase);

            assertThrows(NullPointerException.class, () -> runnable.onErrorThrowAsChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingRunnable<IOException> runnable = Spied.checkedRunnable("foo"::toUpperCase);

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingRunnable<ExecutionException> throwing = runnable.onErrorThrowAsChecked(errorMapper);

            throwing.run();

            verify(runnable).run();
            verify(runnable).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(runnable, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingRunnable<IOException> runnable = Spied.checkedRunnable(() -> {
                throw new IOException("foo");
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingRunnable<ExecutionException> throwing = runnable.onErrorThrowAsChecked(errorMapper);

            ExecutionException thrown = assertThrows(ExecutionException.class, throwing::run);
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo", cause.getMessage());

            verify(runnable).run();
            verify(runnable).onErrorThrowAsChecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(runnable, errorMapper);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingRunnable<IOException> runnable = Spied.checkedRunnable(() -> throwable.throwUnchecked("foo"));

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingRunnable<ExecutionException> throwing = runnable.onErrorThrowAsChecked(errorMapper);

            Throwable thrown = assertThrows(throwable.throwableType(), throwing::run);
            assertEquals("foo", thrown.getMessage());

            verify(runnable).run();
            verify(runnable).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(runnable, errorMapper);
        }
    }

    @Nested
    class OnErrorThrowAsUnchecked {

        @Test
        void testNullArgument() {
            ThrowingRunnable<IOException> runnable = Spied.checkedRunnable("foo"::toUpperCase);

            assertThrows(NullPointerException.class, () -> runnable.onErrorThrowAsUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingRunnable<IOException> runnable = Spied.checkedRunnable("foo"::toUpperCase);

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            Runnable throwing = runnable.onErrorThrowAsUnchecked(errorMapper);

            throwing.run();

            verify(runnable).run();
            verify(runnable).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(runnable, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingRunnable<IOException> runnable = Spied.checkedRunnable(() -> {
                throw new IOException("foo");
            });

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            Runnable throwing = runnable.onErrorThrowAsUnchecked(errorMapper);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, throwing::run);
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo", cause.getMessage());

            verify(runnable).run();
            verify(runnable).onErrorThrowAsUnchecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(runnable, errorMapper);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingRunnable<IOException> runnable = Spied.checkedRunnable(() -> throwable.throwUnchecked("foo"));

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            Runnable throwing = runnable.onErrorThrowAsUnchecked(errorMapper);

            Throwable thrown = assertThrows(throwable.throwableType(), throwing::run);
            assertEquals("foo", thrown.getMessage());

            verify(runnable).run();
            verify(runnable).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(runnable, errorMapper);
        }
    }

    @Nested
    class OnErrorHandleChecked {

        @Test
        void testNullArgument() {
            ThrowingRunnable<IOException> runnable = Spied.checkedRunnable("foo"::toUpperCase);

            assertThrows(NullPointerException.class, () -> runnable.onErrorHandleChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingRunnable<IOException> runnable = Spied.checkedRunnable("foo"::toUpperCase);

            ThrowingConsumer<IOException, ExecutionException> errorHandler = Spied.checkedConsumer(Exception::getMessage);

            ThrowingRunnable<ExecutionException> handling = runnable.onErrorHandleChecked(errorHandler);

            handling.run();

            verify(runnable).run();
            verify(runnable).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(runnable, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException, ExecutionException {
                ThrowingRunnable<IOException> runnable = Spied.checkedRunnable(() -> {
                    throw new IOException("foo");
                });

                ThrowingConsumer<IOException, ExecutionException> errorHandler = Spied.checkedConsumer(Exception::getMessage);

                ThrowingRunnable<ExecutionException> handling = runnable.onErrorHandleChecked(errorHandler);

                handling.run();

                verify(runnable).run();
                verify(runnable).onErrorHandleChecked(errorHandler);
                verify(errorHandler).accept(any());
                verifyNoMoreInteractions(runnable, errorHandler);
            }

            @Test
            void testHandlerThrowsChecked() throws IOException, ExecutionException {
                ThrowingRunnable<IOException> runnable = Spied.checkedRunnable(() -> {
                    throw new IOException("foo");
                });

                ThrowingConsumer<IOException, ExecutionException> errorHandler = Spied.checkedConsumer(e -> {
                    throw new ExecutionException(e);
                });

                ThrowingRunnable<ExecutionException> handling = runnable.onErrorHandleChecked(errorHandler);

                ExecutionException thrown = assertThrows(ExecutionException.class, handling::run);
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());

                verify(runnable).run();
                verify(runnable).onErrorHandleChecked(errorHandler);
                verify(errorHandler).accept(any());
                verifyNoMoreInteractions(runnable, errorHandler);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testHandlerThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ExecutionException {
                ThrowingRunnable<IOException> runnable = Spied.checkedRunnable(() -> {
                    throw new IOException("foo");
                });

                ThrowingConsumer<IOException, ExecutionException> errorHandler = Spied.checkedConsumer(throwable::throwUnchecked);

                ThrowingRunnable<ExecutionException> handling = runnable.onErrorHandleChecked(errorHandler);

                Throwable thrown = assertThrows(throwable.throwableType(), handling::run);
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());

                verify(runnable).run();
                verify(runnable).onErrorHandleChecked(errorHandler);
                verify(errorHandler).accept(any());
                verifyNoMoreInteractions(runnable, errorHandler);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingRunnable<IOException> runnable = Spied.checkedRunnable(() -> throwable.throwUnchecked("foo"));

            ThrowingConsumer<IOException, ExecutionException> errorHandler = Spied.checkedConsumer(Exception::getMessage);

            ThrowingRunnable<ExecutionException> handling = runnable.onErrorHandleChecked(errorHandler);

            Throwable thrown = assertThrows(throwable.throwableType(), handling::run);
            assertEquals("foo", thrown.getMessage());

            verify(runnable).run();
            verify(runnable).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(runnable, errorHandler);
        }
    }

    @Nested
    class OnErrorHandleUnchecked {

        @Test
        void testNullArgument() {
            ThrowingRunnable<IOException> runnable = Spied.checkedRunnable("foo"::toUpperCase);

            assertThrows(NullPointerException.class, () -> runnable.onErrorHandleUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingRunnable<IOException> runnable = Spied.checkedRunnable("foo"::toUpperCase);

            Consumer<IOException> errorHandler = Spied.consumer(Exception::getMessage);

            Runnable handling = runnable.onErrorHandleUnchecked(errorHandler);

            handling.run();

            verify(runnable).run();
            verify(runnable).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(runnable, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException {
                ThrowingRunnable<IOException> runnable = Spied.checkedRunnable(() -> {
                    throw new IOException("foo");
                });

                Consumer<IOException> errorHandler = Spied.consumer(Exception::getMessage);

                Runnable handling = runnable.onErrorHandleUnchecked(errorHandler);

                handling.run();

                verify(runnable).run();
                verify(runnable).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).accept(any());
                verifyNoMoreInteractions(runnable, errorHandler);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testHandlerThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingRunnable<IOException> runnable = Spied.checkedRunnable(() -> {
                    throw new IOException("foo");
                });

                Consumer<IOException> errorHandler = Spied.consumer(throwable::throwUnchecked);

                Runnable handling = runnable.onErrorHandleUnchecked(errorHandler);

                Throwable thrown = assertThrows(throwable.throwableType(), handling::run);
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());

                verify(runnable).run();
                verify(runnable).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).accept(any());
                verifyNoMoreInteractions(runnable, errorHandler);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingRunnable<IOException> runnable = Spied.checkedRunnable(() -> throwable.throwUnchecked("foo"));

            Consumer<IOException> errorHandler = Spied.consumer(Exception::getMessage);

            Runnable handling = runnable.onErrorHandleUnchecked(errorHandler);

            Throwable thrown = assertThrows(throwable.throwableType(), handling::run);
            assertEquals("foo", thrown.getMessage());

            verify(runnable).run();
            verify(runnable).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(runnable, errorHandler);
        }
    }

    @Nested
    class OnErrorRunChecked {

        @Test
        void testNullArgument() {
            ThrowingRunnable<IOException> runnable = Spied.checkedRunnable("foo"::toUpperCase);

            assertThrows(NullPointerException.class, () -> runnable.onErrorRunChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingRunnable<IOException> runnable = Spied.checkedRunnable("foo"::toUpperCase);

            ThrowingRunnable<ExecutionException> fallback = Spied.checkedRunnable("foo"::toLowerCase);

            ThrowingRunnable<ExecutionException> running = runnable.onErrorRunChecked(fallback);

            running.run();

            verify(runnable).run();
            verify(runnable).onErrorRunChecked(fallback);
            verifyNoMoreInteractions(runnable, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                ThrowingRunnable<IOException> runnable = Spied.checkedRunnable(() -> {
                    throw new IOException("foo");
                });

                ThrowingRunnable<ParseException> fallback = Spied.checkedRunnable("foo"::toLowerCase);

                ThrowingRunnable<ParseException> running = runnable.onErrorRunChecked(fallback);

                running.run();

                verify(runnable).run();
                verify(runnable).onErrorRunChecked(fallback);
                verify(fallback).run();
                verifyNoMoreInteractions(runnable, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                ThrowingRunnable<IOException> runnable = Spied.checkedRunnable(() -> {
                    throw new IOException("foo");
                });

                ThrowingRunnable<ParseException> fallback = Spied.checkedRunnable(() -> {
                    throw new ParseException("foo", 0);
                });

                ThrowingRunnable<ParseException> running = runnable.onErrorRunChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, running::run);
                assertEquals("foo", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(runnable).run();
                verify(runnable).onErrorRunChecked(fallback);
                verify(fallback).run();
                verifyNoMoreInteractions(runnable, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ParseException {
                ThrowingRunnable<IOException> runnable = Spied.checkedRunnable(() -> {
                    throw new IOException("foo");
                });

                ThrowingRunnable<ParseException> fallback = Spied.checkedRunnable(() -> throwable.throwUnchecked("foo"));

                ThrowingRunnable<ParseException> running = runnable.onErrorRunChecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), running::run);
                assertEquals("foo", thrown.getMessage());

                verify(runnable).run();
                verify(runnable).onErrorRunChecked(fallback);
                verify(fallback).run();
                verifyNoMoreInteractions(runnable, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingRunnable<IOException> runnable = Spied.checkedRunnable(() -> throwable.throwUnchecked("foo"));

            ThrowingRunnable<ParseException> fallback = Spied.checkedRunnable("foo"::toLowerCase);

            ThrowingRunnable<ParseException> running = runnable.onErrorRunChecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), running::run);
            assertEquals("foo", thrown.getMessage());

            verify(runnable).run();
            verify(runnable).onErrorRunChecked(fallback);
            verifyNoMoreInteractions(runnable, fallback);
        }
    }

    @Nested
    class OnErrorRunUnchecked {

        @Test
        void testNullArgument() {
            ThrowingRunnable<IOException> runnable = Spied.checkedRunnable("foo"::toUpperCase);

            assertThrows(NullPointerException.class, () -> runnable.onErrorRunUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingRunnable<IOException> runnable = Spied.checkedRunnable("foo"::toUpperCase);

            Runnable fallback = Spied.runnable("foo"::toLowerCase);

            Runnable running = runnable.onErrorRunUnchecked(fallback);

            running.run();

            verify(runnable).run();
            verify(runnable).onErrorRunUnchecked(fallback);
            verifyNoMoreInteractions(runnable, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                ThrowingRunnable<IOException> runnable = Spied.checkedRunnable(() -> {
                    throw new IOException("foo");
                });

                Runnable fallback = Spied.runnable("foo"::toLowerCase);

                Runnable running = runnable.onErrorRunUnchecked(fallback);

                running.run();

                verify(runnable).run();
                verify(runnable).onErrorRunUnchecked(fallback);
                verify(fallback).run();
                verifyNoMoreInteractions(runnable, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingRunnable<IOException> runnable = Spied.checkedRunnable(() -> {
                    throw new IOException("foo");
                });

                Runnable fallback = Spied.runnable(() -> throwable.throwUnchecked("foo"));

                Runnable running = runnable.onErrorRunUnchecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), running::run);
                assertEquals("foo", thrown.getMessage());

                verify(runnable).run();
                verify(runnable).onErrorRunUnchecked(fallback);
                verify(fallback).run();
                verifyNoMoreInteractions(runnable, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingRunnable<IOException> runnable = Spied.checkedRunnable(() -> throwable.throwUnchecked("foo"));

            Runnable fallback = Spied.runnable("foo"::toLowerCase);

            Runnable running = runnable.onErrorRunUnchecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), running::run);
            assertEquals("foo", thrown.getMessage());

            verify(runnable).run();
            verify(runnable).onErrorRunUnchecked(fallback);
            verifyNoMoreInteractions(runnable, fallback);
        }
    }

    @Nested
    class OnErrorDiscard {

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingRunnable<IOException> runnable = Spied.checkedRunnable("foo"::toUpperCase);

            Runnable discarding = runnable.onErrorDiscard();

            discarding.run();

            verify(runnable).run();
            verify(runnable).onErrorDiscard();
            verifyNoMoreInteractions(runnable);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingRunnable<IOException> runnable = Spied.checkedRunnable(() -> {
                throw new IOException("foo");
            });

            Runnable discarding = runnable.onErrorDiscard();

            discarding.run();

            verify(runnable).run();
            verify(runnable).onErrorDiscard();
            verifyNoMoreInteractions(runnable);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingRunnable<IOException> runnable = Spied.checkedRunnable(() -> throwable.throwUnchecked("foo"));

            Runnable discarding = runnable.onErrorDiscard();

            Throwable thrown = assertThrows(throwable.throwableType(), discarding::run);
            assertEquals("foo", thrown.getMessage());

            verify(runnable).run();
            verify(runnable).onErrorDiscard();
            verifyNoMoreInteractions(runnable);
        }
    }

    @Nested
    class Unchecked {

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingRunnable<IOException> runnable = Spied.checkedRunnable("foo"::toUpperCase);

            Runnable unchecked = runnable.unchecked();

            unchecked.run();

            verify(runnable).run();
            verify(runnable).unchecked();
            verify(runnable).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(runnable);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingRunnable<IOException> runnable = Spied.checkedRunnable(() -> {
                throw new IOException("foo");
            });

            Runnable unchecked = runnable.unchecked();

            UncheckedException thrown = assertThrows(UncheckedException.class, unchecked::run);
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo", cause.getMessage());

            verify(runnable).run();
            verify(runnable).unchecked();
            verify(runnable).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(runnable);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingRunnable<IOException> runnable = Spied.checkedRunnable(() -> throwable.throwUnchecked("foo"));

            Runnable unchecked = runnable.unchecked();

            Throwable thrown = assertThrows(throwable.throwableType(), unchecked::run);
            assertEquals("foo", thrown.getMessage());

            verify(runnable).run();
            verify(runnable).unchecked();
            verify(runnable).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(runnable);
        }
    }

    @Nested
    class Of {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> ThrowingRunnable.of(null));
        }

        @Test
        void testNonNullArgument() throws IOException {
            ThrowingRunnable<IOException> delegate = Spied.checkedRunnable("foo"::toUpperCase);

            ThrowingRunnable<IOException> runnable = ThrowingRunnable.of(delegate);

            runnable.run();

            verify(delegate).run();
            verifyNoMoreInteractions(delegate);
        }
    }

    @Nested
    class UncheckedFactory {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> ThrowingRunnable.unchecked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ThrowingRunnable<IOException> runnable = Spied.checkedRunnable("foo"::toUpperCase);

            Runnable unchecked = ThrowingRunnable.unchecked(runnable);

            unchecked.run();

            verify(runnable).unchecked();
            verify(runnable).onErrorThrowAsUnchecked(any());
            verify(runnable).run();
            verifyNoMoreInteractions(runnable);
        }

        @Test
        void testArgumentThrowsChecked() throws IOException {
            ThrowingRunnable<IOException> runnable = Spied.checkedRunnable(() -> {
                throw new IOException("foo");
            });

            Runnable unchecked = ThrowingRunnable.unchecked(runnable);

            UncheckedException thrown = assertThrows(UncheckedException.class, unchecked::run);
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo", cause.getMessage());

            verify(runnable).unchecked();
            verify(runnable).onErrorThrowAsUnchecked(any());
            verify(runnable).run();
            verifyNoMoreInteractions(runnable);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testArgumentThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingRunnable<IOException> runnable = Spied.checkedRunnable(() -> throwable.throwUnchecked("foo"));

            Runnable unchecked = ThrowingRunnable.unchecked(runnable);

            Throwable thrown = assertThrows(throwable.throwableType(), unchecked::run);
            assertEquals("foo", thrown.getMessage());

            verify(runnable).unchecked();
            verify(runnable).onErrorThrowAsUnchecked(any());
            verify(runnable).run();
            verifyNoMoreInteractions(runnable);
        }
    }

    @Nested
    class CheckedWrapper {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> ThrowingRunnable.checked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            Runnable delegate = Spied.runnable("foo"::toUpperCase);

            ThrowingRunnable<IOException> runnable = ThrowingRunnable.checked(delegate);

            runnable.run();

            verify(delegate).run();
            verifyNoMoreInteractions(delegate);
        }

        @Test
        void testArgumentThrowsUnchecked() {
            ThrowingRunnable<IOException> runnable = ThrowingRunnable.checked(() -> {
                throw new UncheckedException("foo", new IOException());
            });

            UncheckedException thrown = assertThrows(UncheckedException.class, runnable::run);
            assertEquals("foo", thrown.getMessage());
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertNull(cause.getMessage());
        }
    }

    @Nested
    class CheckedUnwrapper {

        @Test
        void testNullArguments() {
            Runnable runnable = "foo"::toUpperCase;

            assertThrows(NullPointerException.class, () -> ThrowingRunnable.checked(null, IOException.class));
            assertThrows(NullPointerException.class, () -> ThrowingRunnable.checked(runnable, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            Runnable delegate = Spied.runnable("foo"::toUpperCase);

            ThrowingRunnable<IOException> runnable = ThrowingRunnable.checked(delegate, IOException.class);

            runnable.run();

            verify(delegate).run();
            verifyNoMoreInteractions(delegate);
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                ThrowingRunnable<IOException> runnable = ThrowingRunnable.checked(() -> {
                    throw new UncheckedException(new IOException("foo"));
                }, IOException.class);

                IOException thrown = assertThrows(IOException.class, runnable::run);
                assertEquals("foo", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                ThrowingRunnable<IOException> runnable = ThrowingRunnable.checked(() -> {
                    throw new UncheckedException(new FileNotFoundException("foo"));
                }, IOException.class);

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class, runnable::run);
                assertEquals("foo", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                ThrowingRunnable<IOException> runnable = ThrowingRunnable.checked(() -> {
                    throw new UncheckedException(new ParseException("foo", 0));
                }, IOException.class);

                UncheckedException thrown = assertThrows(UncheckedException.class, runnable::run);
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            ThrowingRunnable<IOException> runnable = ThrowingRunnable.checked(() -> {
                throw new IllegalStateException("foo");
            }, IOException.class);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, runnable::run);
            assertEquals("foo", thrown.getMessage());
        }
    }

    @Nested
    class InvokeAndUnwrap {

        @Test
        void testNullArguments() throws IOException {
            Runnable runnable = Spied.runnable("foo"::toLowerCase);

            assertThrows(NullPointerException.class, () -> ThrowingRunnable.invokeAndUnwrap(null, IOException.class));
            assertThrows(NullPointerException.class, () -> ThrowingRunnable.invokeAndUnwrap(runnable, null));

            ThrowingRunnable.invokeAndUnwrap(runnable, IOException.class);

            verify(runnable).run();
            verifyNoMoreInteractions(runnable);
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            Runnable runnable = Spied.runnable("foo"::toUpperCase);

            ThrowingRunnable.invokeAndUnwrap(runnable, IOException.class);

            verify(runnable).run();
            verifyNoMoreInteractions(runnable);
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                Runnable runnable = () -> {
                    throw new UncheckedException(new IOException("foo"));
                };

                IOException thrown = assertThrows(IOException.class, () -> ThrowingRunnable.invokeAndUnwrap(runnable, IOException.class));
                assertEquals("foo", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                Runnable runnable = () -> {
                    throw new UncheckedException(new FileNotFoundException("foo"));
                };

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class,
                        () -> ThrowingRunnable.invokeAndUnwrap(runnable, IOException.class));
                assertEquals("foo", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                Runnable runnable = () -> {
                    throw new UncheckedException(new ParseException("foo", 0));
                };

                UncheckedException thrown = assertThrows(UncheckedException.class,
                        () -> ThrowingRunnable.invokeAndUnwrap(runnable, IOException.class));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            Runnable runnable = () -> {
                throw new IllegalStateException("foo");
            };

            IllegalStateException thrown = assertThrows(IllegalStateException.class,
                    () -> ThrowingRunnable.invokeAndUnwrap(runnable, IOException.class));
            assertEquals("foo", thrown.getMessage());
        }
    }
}
