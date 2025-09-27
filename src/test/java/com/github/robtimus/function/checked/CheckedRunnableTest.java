/*
 * CheckedRunnableTest.java
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

@SuppressWarnings("nls")
class CheckedRunnableTest {

    @Nested
    class OnErrorThrowAsChecked {

        @Test
        void testNullArgument() {
            CheckedRunnable<IOException> runnable = Spied.checkedRunnable("foo"::toUpperCase);

            assertThrows(NullPointerException.class, () -> runnable.onErrorThrowAsChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedRunnable<IOException> runnable = Spied.checkedRunnable("foo"::toUpperCase);

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedRunnable<ExecutionException> throwing = runnable.onErrorThrowAsChecked(errorMapper);

            throwing.run();

            verify(runnable).run();
            verify(runnable).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(runnable, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedRunnable<IOException> runnable = Spied.checkedRunnable(() -> {
                throw new IOException("foo");
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedRunnable<ExecutionException> throwing = runnable.onErrorThrowAsChecked(errorMapper);

            ExecutionException thrown = assertThrows(ExecutionException.class, throwing::run);
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo", cause.getMessage());

            verify(runnable).run();
            verify(runnable).onErrorThrowAsChecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(runnable, errorMapper);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedRunnable<IOException> runnable = Spied.checkedRunnable(() -> {
                throw new IllegalStateException("foo");
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedRunnable<ExecutionException> throwing = runnable.onErrorThrowAsChecked(errorMapper);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, throwing::run);
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
            CheckedRunnable<IOException> runnable = Spied.checkedRunnable("foo"::toUpperCase);

            assertThrows(NullPointerException.class, () -> runnable.onErrorThrowAsUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedRunnable<IOException> runnable = Spied.checkedRunnable("foo"::toUpperCase);

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            Runnable throwing = runnable.onErrorThrowAsUnchecked(errorMapper);

            throwing.run();

            verify(runnable).run();
            verify(runnable).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(runnable, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedRunnable<IOException> runnable = Spied.checkedRunnable(() -> {
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

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedRunnable<IOException> runnable = Spied.checkedRunnable(() -> {
                throw new IllegalArgumentException("foo");
            });

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            Runnable throwing = runnable.onErrorThrowAsUnchecked(errorMapper);

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, throwing::run);
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
            CheckedRunnable<IOException> runnable = Spied.checkedRunnable("foo"::toUpperCase);

            assertThrows(NullPointerException.class, () -> runnable.onErrorHandleChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedRunnable<IOException> runnable = Spied.checkedRunnable("foo"::toUpperCase);

            CheckedConsumer<IOException, ExecutionException> errorHandler = Spied.checkedConsumer(Exception::getMessage);

            CheckedRunnable<ExecutionException> handling = runnable.onErrorHandleChecked(errorHandler);

            handling.run();

            verify(runnable).run();
            verify(runnable).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(runnable, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException, ExecutionException {
                CheckedRunnable<IOException> runnable = Spied.checkedRunnable(() -> {
                    throw new IOException("foo");
                });

                CheckedConsumer<IOException, ExecutionException> errorHandler = Spied.checkedConsumer(Exception::getMessage);

                CheckedRunnable<ExecutionException> handling = runnable.onErrorHandleChecked(errorHandler);

                handling.run();

                verify(runnable).run();
                verify(runnable).onErrorHandleChecked(errorHandler);
                verify(errorHandler).accept(any());
                verifyNoMoreInteractions(runnable, errorHandler);
            }

            @Test
            void testHandlerThrowsChecked() throws IOException, ExecutionException {
                CheckedRunnable<IOException> runnable = Spied.checkedRunnable(() -> {
                    throw new IOException("foo");
                });

                CheckedConsumer<IOException, ExecutionException> errorHandler = Spied.checkedConsumer(e -> {
                    throw new ExecutionException(e);
                });

                CheckedRunnable<ExecutionException> handling = runnable.onErrorHandleChecked(errorHandler);

                ExecutionException thrown = assertThrows(ExecutionException.class, handling::run);
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());

                verify(runnable).run();
                verify(runnable).onErrorHandleChecked(errorHandler);
                verify(errorHandler).accept(any());
                verifyNoMoreInteractions(runnable, errorHandler);
            }

            @Test
            void testHandlerThrowsUnchecked() throws IOException, ExecutionException {
                CheckedRunnable<IOException> runnable = Spied.checkedRunnable(() -> {
                    throw new IOException("foo");
                });

                CheckedConsumer<IOException, ExecutionException> errorHandler = Spied.checkedConsumer(e -> {
                    throw new IllegalStateException(e);
                });

                CheckedRunnable<ExecutionException> handling = runnable.onErrorHandleChecked(errorHandler);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, handling::run);
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());

                verify(runnable).run();
                verify(runnable).onErrorHandleChecked(errorHandler);
                verify(errorHandler).accept(any());
                verifyNoMoreInteractions(runnable, errorHandler);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedRunnable<IOException> runnable = Spied.checkedRunnable(() -> {
                throw new IllegalStateException("foo");
            });

            CheckedConsumer<IOException, ExecutionException> errorHandler = Spied.checkedConsumer(Exception::getMessage);

            CheckedRunnable<ExecutionException> handling = runnable.onErrorHandleChecked(errorHandler);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, handling::run);
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
            CheckedRunnable<IOException> runnable = Spied.checkedRunnable("foo"::toUpperCase);

            assertThrows(NullPointerException.class, () -> runnable.onErrorHandleUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedRunnable<IOException> runnable = Spied.checkedRunnable("foo"::toUpperCase);

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
                CheckedRunnable<IOException> runnable = Spied.checkedRunnable(() -> {
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

            @Test
            void testHandlerThrowsUnchecked() throws IOException {
                CheckedRunnable<IOException> runnable = Spied.checkedRunnable(() -> {
                    throw new IOException("foo");
                });

                Consumer<IOException> errorHandler = Spied.consumer(e -> {
                    throw new IllegalStateException(e);
                });

                Runnable handling = runnable.onErrorHandleUnchecked(errorHandler);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, handling::run);
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());

                verify(runnable).run();
                verify(runnable).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).accept(any());
                verifyNoMoreInteractions(runnable, errorHandler);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedRunnable<IOException> runnable = Spied.checkedRunnable(() -> {
                throw new IllegalStateException("foo");
            });

            Consumer<IOException> errorHandler = Spied.consumer(Exception::getMessage);

            Runnable handling = runnable.onErrorHandleUnchecked(errorHandler);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, handling::run);
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
            CheckedRunnable<IOException> runnable = Spied.checkedRunnable("foo"::toUpperCase);

            assertThrows(NullPointerException.class, () -> runnable.onErrorRunChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedRunnable<IOException> runnable = Spied.checkedRunnable("foo"::toUpperCase);

            CheckedRunnable<ExecutionException> fallback = Spied.checkedRunnable("foo"::toLowerCase);

            CheckedRunnable<ExecutionException> running = runnable.onErrorRunChecked(fallback);

            running.run();

            verify(runnable).run();
            verify(runnable).onErrorRunChecked(fallback);
            verifyNoMoreInteractions(runnable, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                CheckedRunnable<IOException> runnable = Spied.checkedRunnable(() -> {
                    throw new IOException("foo");
                });

                CheckedRunnable<ParseException> fallback = Spied.checkedRunnable("foo"::toLowerCase);

                CheckedRunnable<ParseException> running = runnable.onErrorRunChecked(fallback);

                running.run();

                verify(runnable).run();
                verify(runnable).onErrorRunChecked(fallback);
                verify(fallback).run();
                verifyNoMoreInteractions(runnable, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                CheckedRunnable<IOException> runnable = Spied.checkedRunnable(() -> {
                    throw new IOException("foo");
                });

                CheckedRunnable<ParseException> fallback = Spied.checkedRunnable(() -> {
                    throw new ParseException("foo", 0);
                });

                CheckedRunnable<ParseException> running = runnable.onErrorRunChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, running::run);
                assertEquals("foo", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(runnable).run();
                verify(runnable).onErrorRunChecked(fallback);
                verify(fallback).run();
                verifyNoMoreInteractions(runnable, fallback);
            }

            @Test
            void testFallbackThrowsUnchecked() throws IOException, ParseException {
                CheckedRunnable<IOException> runnable = Spied.checkedRunnable(() -> {
                    throw new IOException("foo");
                });

                CheckedRunnable<ParseException> fallback = Spied.checkedRunnable(() -> {
                    throw new IllegalStateException("foo");
                });

                CheckedRunnable<ParseException> running = runnable.onErrorRunChecked(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, running::run);
                assertEquals("foo", thrown.getMessage());

                verify(runnable).run();
                verify(runnable).onErrorRunChecked(fallback);
                verify(fallback).run();
                verifyNoMoreInteractions(runnable, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedRunnable<IOException> runnable = Spied.checkedRunnable(() -> {
                throw new IllegalStateException("foo");
            });

            CheckedRunnable<ParseException> fallback = Spied.checkedRunnable("foo"::toLowerCase);

            CheckedRunnable<ParseException> running = runnable.onErrorRunChecked(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, running::run);
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
            CheckedRunnable<IOException> runnable = Spied.checkedRunnable("foo"::toUpperCase);

            assertThrows(NullPointerException.class, () -> runnable.onErrorRunUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedRunnable<IOException> runnable = Spied.checkedRunnable("foo"::toUpperCase);

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
                CheckedRunnable<IOException> runnable = Spied.checkedRunnable(() -> {
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

            @Test
            void testFallbackThrowsUnchecked() throws IOException {
                CheckedRunnable<IOException> runnable = Spied.checkedRunnable(() -> {
                    throw new IOException("foo");
                });

                Runnable fallback = Spied.runnable(() -> {
                    throw new IllegalStateException("foo");
                });

                Runnable running = runnable.onErrorRunUnchecked(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, running::run);
                assertEquals("foo", thrown.getMessage());

                verify(runnable).run();
                verify(runnable).onErrorRunUnchecked(fallback);
                verify(fallback).run();
                verifyNoMoreInteractions(runnable, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedRunnable<IOException> runnable = Spied.checkedRunnable(() -> {
                throw new IllegalStateException("foo");
            });

            Runnable fallback = Spied.runnable("foo"::toLowerCase);

            Runnable running = runnable.onErrorRunUnchecked(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, running::run);
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
            CheckedRunnable<IOException> runnable = Spied.checkedRunnable("foo"::toUpperCase);

            Runnable discarding = runnable.onErrorDiscard();

            discarding.run();

            verify(runnable).run();
            verify(runnable).onErrorDiscard();
            verifyNoMoreInteractions(runnable);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedRunnable<IOException> runnable = Spied.checkedRunnable(() -> {
                throw new IOException("foo");
            });

            Runnable discarding = runnable.onErrorDiscard();

            discarding.run();

            verify(runnable).run();
            verify(runnable).onErrorDiscard();
            verifyNoMoreInteractions(runnable);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedRunnable<IOException> runnable = Spied.checkedRunnable(() -> {
                throw new IllegalStateException("foo");
            });

            Runnable discarding = runnable.onErrorDiscard();

            IllegalStateException thrown = assertThrows(IllegalStateException.class, discarding::run);
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
            CheckedRunnable<IOException> runnable = Spied.checkedRunnable("foo"::toUpperCase);

            Runnable unchecked = runnable.unchecked();

            unchecked.run();

            verify(runnable).run();
            verify(runnable).unchecked();
            verify(runnable).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(runnable);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedRunnable<IOException> runnable = Spied.checkedRunnable(() -> {
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

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedRunnable<IOException> runnable = Spied.checkedRunnable(() -> {
                throw new IllegalArgumentException("foo");
            });

            Runnable unchecked = runnable.unchecked();

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, unchecked::run);
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
            assertThrows(NullPointerException.class, () -> CheckedRunnable.of(null));
        }

        @Test
        void testNonNullArgument() throws IOException {
            CheckedRunnable<IOException> delegate = Spied.checkedRunnable("foo"::toUpperCase);

            CheckedRunnable<IOException> runnable = CheckedRunnable.of(delegate);

            runnable.run();

            verify(delegate).run();
            verifyNoMoreInteractions(delegate);
        }
    }

    @Nested
    class UncheckedFactory {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> CheckedRunnable.unchecked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedRunnable<IOException> runnable = Spied.checkedRunnable("foo"::toUpperCase);

            Runnable unchecked = CheckedRunnable.unchecked(runnable);

            unchecked.run();

            verify(runnable).unchecked();
            verify(runnable).onErrorThrowAsUnchecked(any());
            verify(runnable).run();
            verifyNoMoreInteractions(runnable);
        }

        @Test
        void testArgumentThrowsChecked() throws IOException {
            CheckedRunnable<IOException> runnable = Spied.checkedRunnable(() -> {
                throw new IOException("foo");
            });

            Runnable unchecked = CheckedRunnable.unchecked(runnable);

            UncheckedException thrown = assertThrows(UncheckedException.class, unchecked::run);
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo", cause.getMessage());

            verify(runnable).unchecked();
            verify(runnable).onErrorThrowAsUnchecked(any());
            verify(runnable).run();
            verifyNoMoreInteractions(runnable);
        }

        @Test
        void testArgumentThrowsUnchecked() throws IOException {
            CheckedRunnable<IOException> runnable = Spied.checkedRunnable(() -> {
                throw new IllegalArgumentException("foo");
            });

            Runnable unchecked = CheckedRunnable.unchecked(runnable);

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, unchecked::run);
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
            assertThrows(NullPointerException.class, () -> CheckedRunnable.checked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            Runnable delegate = Spied.runnable("foo"::toUpperCase);

            CheckedRunnable<IOException> runnable = CheckedRunnable.checked(delegate);

            runnable.run();

            verify(delegate).run();
            verifyNoMoreInteractions(delegate);
        }

        @Test
        void testargumentThrowsUnchecked() {
            CheckedRunnable<IOException> runnable = CheckedRunnable.checked(() -> {
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

            assertThrows(NullPointerException.class, () -> CheckedRunnable.checked(null, IOException.class));
            assertThrows(NullPointerException.class, () -> CheckedRunnable.checked(runnable, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            Runnable delegate = Spied.runnable("foo"::toUpperCase);

            CheckedRunnable<IOException> runnable = CheckedRunnable.checked(delegate, IOException.class);

            runnable.run();

            verify(delegate).run();
            verifyNoMoreInteractions(delegate);
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                CheckedRunnable<IOException> runnable = CheckedRunnable.checked(() -> {
                    throw new UncheckedException(new IOException("foo"));
                }, IOException.class);

                IOException thrown = assertThrows(IOException.class, runnable::run);
                assertEquals("foo", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                CheckedRunnable<IOException> runnable = CheckedRunnable.checked(() -> {
                    throw new UncheckedException(new FileNotFoundException("foo"));
                }, IOException.class);

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class, runnable::run);
                assertEquals("foo", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                CheckedRunnable<IOException> runnable = CheckedRunnable.checked(() -> {
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
            CheckedRunnable<IOException> runnable = CheckedRunnable.checked(() -> {
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

            assertThrows(NullPointerException.class, () -> CheckedRunnable.invokeAndUnwrap(null, IOException.class));
            assertThrows(NullPointerException.class, () -> CheckedRunnable.invokeAndUnwrap(runnable, null));

            CheckedRunnable.invokeAndUnwrap(runnable, IOException.class);

            verify(runnable).run();
            verifyNoMoreInteractions(runnable);
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            Runnable runnable = Spied.runnable("foo"::toUpperCase);

            CheckedRunnable.invokeAndUnwrap(runnable, IOException.class);

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

                IOException thrown = assertThrows(IOException.class, () -> CheckedRunnable.invokeAndUnwrap(runnable, IOException.class));
                assertEquals("foo", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                Runnable runnable = () -> {
                    throw new UncheckedException(new FileNotFoundException("foo"));
                };

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class,
                        () -> CheckedRunnable.invokeAndUnwrap(runnable, IOException.class));
                assertEquals("foo", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                Runnable runnable = () -> {
                    throw new UncheckedException(new ParseException("foo", 0));
                };

                UncheckedException thrown = assertThrows(UncheckedException.class,
                        () -> CheckedRunnable.invokeAndUnwrap(runnable, IOException.class));
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
                    () -> CheckedRunnable.invokeAndUnwrap(runnable, IOException.class));
            assertEquals("foo", thrown.getMessage());
        }
    }
}
