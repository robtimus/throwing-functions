/*
 * CheckedFunctionTest.java
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
import java.util.function.Function;
import java.util.function.Supplier;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("nls")
class CheckedFunctionTest {

    @Nested
    class Compose {

        @Test
        void testNullArgument() {
            CheckedFunction<String, String, IOException> function = Spied.checkedFunction(String::toUpperCase);

            assertThrows(NullPointerException.class, () -> function.compose(null));
        }

        @Test
        void testNeitherThrows() throws IOException {
            CheckedFunction<String, String, IOException> function = Spied.checkedFunction(s -> s + s);
            CheckedFunction<Integer, String, IOException> before = Spied.checkedFunction(Object::toString);

            CheckedFunction<Integer, String, IOException> composed = function.compose(before);

            assertEquals("11", composed.apply(1));

            verify(before).apply(1);
            verify(function).apply("1");
            verify(function).compose(before);
            verifyNoMoreInteractions(function, before);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedFunction<String, String, IOException> function = Spied.checkedFunction(s -> {
                throw new IOException(s);
            });
            CheckedFunction<Integer, String, IOException> before = Spied.checkedFunction(Object::toString);

            CheckedFunction<Integer, String, IOException> composed = function.compose(before);

            IOException thrown = assertThrows(IOException.class, () -> composed.apply(1));
            assertEquals("1", thrown.getMessage());

            verify(before).apply(1);
            verify(function).apply("1");
            verify(function).compose(before);
            verifyNoMoreInteractions(function, before);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedFunction<String, String, IOException> function = Spied.checkedFunction(s -> {
                throw new IllegalStateException(s);
            });
            CheckedFunction<Integer, String, IOException> before = Spied.checkedFunction(Object::toString);

            CheckedFunction<Integer, String, IOException> composed = function.compose(before);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> composed.apply(1));
            assertEquals("1", thrown.getMessage());

            verify(before).apply(1);
            verify(function).apply("1");
            verify(function).compose(before);
            verifyNoMoreInteractions(function, before);
        }

        @Test
        void testBeforeThrowsChecked() throws IOException {
            CheckedFunction<String, String, IOException> function = Spied.checkedFunction(String::toUpperCase);
            CheckedFunction<Integer, String, IOException> before = Spied.checkedFunction(i -> {
                throw new IOException(i.toString());
            });

            CheckedFunction<Integer, String, IOException> composed = function.compose(before);

            IOException thrown = assertThrows(IOException.class, () -> composed.apply(1));
            assertEquals("1", thrown.getMessage());

            verify(before).apply(1);
            verify(function).compose(before);
            verifyNoMoreInteractions(function, before);
        }

        @Test
        void testBeforeThrowsUnchecked() throws IOException {
            CheckedFunction<String, String, IOException> function = Spied.checkedFunction(String::toUpperCase);
            CheckedFunction<Integer, String, IOException> before = Spied.checkedFunction(i -> {
                throw new IllegalStateException(i.toString());
            });

            CheckedFunction<Integer, String, IOException> composed = function.compose(before);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> composed.apply(1));
            assertEquals("1", thrown.getMessage());

            verify(before).apply(1);
            verify(function).compose(before);
            verifyNoMoreInteractions(function, before);
        }
    }

    @Nested
    class AndThen {

        @Test
        void testNullArgument() {
            CheckedFunction<String, String, IOException> function = Spied.checkedFunction(String::toUpperCase);

            assertThrows(NullPointerException.class, () -> function.andThen(null));
        }

        @Test
        void testNeitherThrows() throws IOException {
            CheckedFunction<Integer, String, IOException> function = Spied.checkedFunction(Object::toString);
            CheckedFunction<String, String, IOException> after = Spied.checkedFunction(s -> s + s);

            CheckedFunction<Integer, String, IOException> composed = function.andThen(after);

            assertEquals("11", composed.apply(1));

            verify(function).apply(1);
            verify(function).andThen(after);
            verify(after).apply("1");
            verifyNoMoreInteractions(function, after);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedFunction<Integer, String, IOException> function = Spied.checkedFunction(i -> {
                throw new IOException(i.toString());
            });
            CheckedFunction<String, String, IOException> after = Spied.checkedFunction(Object::toString);

            CheckedFunction<Integer, String, IOException> composed = function.andThen(after);

            IOException thrown = assertThrows(IOException.class, () -> composed.apply(1));
            assertEquals("1", thrown.getMessage());

            verify(function).apply(1);
            verify(function).andThen(after);
            verifyNoMoreInteractions(function, after);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedFunction<Integer, String, IOException> function = Spied.checkedFunction(i -> {
                throw new IllegalStateException(i.toString());
            });
            CheckedFunction<String, String, IOException> after = Spied.checkedFunction(Object::toString);

            CheckedFunction<Integer, String, IOException> composed = function.andThen(after);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> composed.apply(1));
            assertEquals("1", thrown.getMessage());

            verify(function).apply(1);
            verify(function).andThen(after);
            verifyNoMoreInteractions(function, after);
        }

        @Test
        void testAfterThrowsChecked() throws IOException {
            CheckedFunction<Integer, String, IOException> function = Spied.checkedFunction(Object::toString);
            CheckedFunction<String, String, IOException> after = Spied.checkedFunction(s -> {
                throw new IOException(s);
            });

            CheckedFunction<Integer, String, IOException> composed = function.andThen(after);

            IOException thrown = assertThrows(IOException.class, () -> composed.apply(1));
            assertEquals("1", thrown.getMessage());

            verify(function).apply(1);
            verify(function).andThen(after);
            verify(after).apply("1");
            verifyNoMoreInteractions(function, after);
        }

        @Test
        void testAfterThrowsUnchecked() throws IOException {
            CheckedFunction<Integer, String, IOException> function = Spied.checkedFunction(Object::toString);
            CheckedFunction<String, String, IOException> after = Spied.checkedFunction(s -> {
                throw new IllegalStateException(s);
            });

            CheckedFunction<Integer, String, IOException> composed = function.andThen(after);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> composed.apply(1));
            assertEquals("1", thrown.getMessage());

            verify(function).apply(1);
            verify(function).andThen(after);
            verify(after).apply("1");
            verifyNoMoreInteractions(function, after);
        }
    }

    @Nested
    class OnErrorThrowAsChecked {

        @Test
        void testNullArgument() {
            CheckedFunction<String, String, IOException> function = Spied.checkedFunction(String::toUpperCase);

            assertThrows(NullPointerException.class, () -> function.onErrorThrowAsChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedFunction<String, String, IOException> function = Spied.checkedFunction(String::toUpperCase);

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedFunction<String, String, ExecutionException> throwing = function.onErrorThrowAsChecked(errorMapper);

            assertEquals("FOO", throwing.apply("foo"));

            verify(function).apply("foo");
            verify(function).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedFunction<String, String, IOException> function = Spied.checkedFunction(s -> {
                throw new IOException(s);
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedFunction<String, String, ExecutionException> throwing = function.onErrorThrowAsChecked(errorMapper);

            ExecutionException thrown = assertThrows(ExecutionException.class, () -> throwing.apply("foo"));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo", cause.getMessage());

            verify(function).apply("foo");
            verify(function).onErrorThrowAsChecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(function, errorMapper);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedFunction<String, String, IOException> function = Spied.checkedFunction(s -> {
                throw new IllegalStateException(s);
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedFunction<String, String, ExecutionException> throwing = function.onErrorThrowAsChecked(errorMapper);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> throwing.apply("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(function).apply("foo");
            verify(function).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }
    }

    @Nested
    class OnErrorThrowAsUnchecked {

        @Test
        void testNullArgument() {
            CheckedFunction<String, String, IOException> function = Spied.checkedFunction(String::toUpperCase);

            assertThrows(NullPointerException.class, () -> function.onErrorThrowAsUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedFunction<String, String, IOException> function = Spied.checkedFunction(String::toUpperCase);

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            Function<String, String> throwing = function.onErrorThrowAsUnchecked(errorMapper);

            assertEquals("FOO", throwing.apply("foo"));

            verify(function).apply("foo");
            verify(function).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedFunction<String, String, IOException> function = Spied.checkedFunction(s -> {
                throw new IOException(s);
            });

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            Function<String, String> throwing = function.onErrorThrowAsUnchecked(errorMapper);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> throwing.apply("foo"));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo", cause.getMessage());

            verify(function).apply("foo");
            verify(function).onErrorThrowAsUnchecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(function, errorMapper);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedFunction<String, String, IOException> function = Spied.checkedFunction(s -> {
                throw new IllegalArgumentException(s);
            });

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            Function<String, String> throwing = function.onErrorThrowAsUnchecked(errorMapper);

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> throwing.apply("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(function).apply("foo");
            verify(function).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }
    }

    @Nested
    class OnErrorHandleChecked {

        @Test
        void testNullArgument() {
            CheckedFunction<String, String, IOException> function = Spied.checkedFunction(String::toUpperCase);

            assertThrows(NullPointerException.class, () -> function.onErrorHandleChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedFunction<String, String, IOException> function = Spied.checkedFunction(String::toUpperCase);

            CheckedFunction<IOException, String, ExecutionException> errorHandler = Spied.checkedFunction(Exception::getMessage);

            CheckedFunction<String, String, ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

            assertEquals("FOO", handling.apply("foo"));

            verify(function).apply("foo");
            verify(function).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException, ExecutionException {
                CheckedFunction<String, String, IOException> function = Spied.checkedFunction(s -> {
                    throw new IOException(s);
                });

                CheckedFunction<IOException, String, ExecutionException> errorHandler = Spied.checkedFunction(Exception::getMessage);

                CheckedFunction<String, String, ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

                assertEquals("foo", handling.apply("foo"));

                verify(function).apply("foo");
                verify(function).onErrorHandleChecked(errorHandler);
                verify(errorHandler).apply(any());
                verifyNoMoreInteractions(function, errorHandler);
            }

            @Test
            void testHandlerThrowsChecked() throws IOException, ExecutionException {
                CheckedFunction<String, String, IOException> function = Spied.checkedFunction(s -> {
                    throw new IOException(s);
                });

                CheckedFunction<IOException, String, ExecutionException> errorHandler = Spied.checkedFunction(e -> {
                    throw new ExecutionException(e);
                });

                CheckedFunction<String, String, ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

                ExecutionException thrown = assertThrows(ExecutionException.class, () -> handling.apply("foo"));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());

                verify(function).apply("foo");
                verify(function).onErrorHandleChecked(errorHandler);
                verify(errorHandler).apply(any());
                verifyNoMoreInteractions(function, errorHandler);
            }

            @Test
            void testHandlerThrowsUnchecked() throws IOException, ExecutionException {
                CheckedFunction<String, String, IOException> function = Spied.checkedFunction(s -> {
                    throw new IOException(s);
                });

                CheckedFunction<IOException, String, ExecutionException> errorHandler = Spied.checkedFunction(e -> {
                    throw new IllegalStateException(e);
                });

                CheckedFunction<String, String, ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.apply("foo"));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());

                verify(function).apply("foo");
                verify(function).onErrorHandleChecked(errorHandler);
                verify(errorHandler).apply(any());
                verifyNoMoreInteractions(function, errorHandler);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedFunction<String, String, IOException> function = Spied.checkedFunction(s -> {
                throw new IllegalStateException(s);
            });

            CheckedFunction<IOException, String, ExecutionException> errorHandler = Spied.checkedFunction(Exception::getMessage);

            CheckedFunction<String, String, ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.apply("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(function).apply("foo");
            verify(function).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }
    }

    @Nested
    class OnErrorHandleUnchecked {

        @Test
        void testNullArgument() {
            CheckedFunction<String, String, IOException> function = Spied.checkedFunction(String::toUpperCase);

            assertThrows(NullPointerException.class, () -> function.onErrorHandleUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedFunction<String, String, IOException> function = Spied.checkedFunction(String::toUpperCase);

            Function<IOException, String> errorHandler = Spied.function(Exception::getMessage);

            Function<String, String> handling = function.onErrorHandleUnchecked(errorHandler);

            assertEquals("FOO", handling.apply("foo"));

            verify(function).apply("foo");
            verify(function).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException {
                CheckedFunction<String, String, IOException> function = Spied.checkedFunction(s -> {
                    throw new IOException(s);
                });

                Function<IOException, String> errorHandler = Spied.function(Exception::getMessage);

                Function<String, String> handling = function.onErrorHandleUnchecked(errorHandler);

                assertEquals("foo", handling.apply("foo"));

                verify(function).apply("foo");
                verify(function).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).apply(any());
                verifyNoMoreInteractions(function, errorHandler);
            }

            @Test
            void testHandlerThrowsUnchecked() throws IOException {
                CheckedFunction<String, String, IOException> function = Spied.checkedFunction(s -> {
                    throw new IOException(s);
                });

                Function<IOException, String> errorHandler = Spied.function(e -> {
                    throw new IllegalStateException(e);
                });

                Function<String, String> handling = function.onErrorHandleUnchecked(errorHandler);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.apply("foo"));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());

                verify(function).apply("foo");
                verify(function).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).apply(any());
                verifyNoMoreInteractions(function, errorHandler);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedFunction<String, String, IOException> function = Spied.checkedFunction(s -> {
                throw new IllegalStateException(s);
            });

            Function<IOException, String> errorHandler = Spied.function(Exception::getMessage);

            Function<String, String> handling = function.onErrorHandleUnchecked(errorHandler);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.apply("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(function).apply("foo");
            verify(function).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }
    }

    @Nested
    class OnErrorApplyChecked {

        @Test
        void testNullArgument() {
            CheckedFunction<String, String, IOException> function = Spied.checkedFunction(String::toUpperCase);

            assertThrows(NullPointerException.class, () -> function.onErrorApplyChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedFunction<String, String, IOException> function = Spied.checkedFunction(String::toUpperCase);

            CheckedFunction<String, String, ExecutionException> fallback = Spied.checkedFunction(s -> s + s);

            CheckedFunction<String, String, ExecutionException> applying = function.onErrorApplyChecked(fallback);

            assertEquals("FOO", applying.apply("foo"));

            verify(function).apply("foo");
            verify(function).onErrorApplyChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                CheckedFunction<String, String, IOException> function = Spied.checkedFunction(s -> {
                    throw new IOException(s);
                });

                CheckedFunction<String, String, ParseException> fallback = Spied.checkedFunction(s -> s + s);

                CheckedFunction<String, String, ParseException> applying = function.onErrorApplyChecked(fallback);

                assertEquals("foofoo", applying.apply("foo"));

                verify(function).apply("foo");
                verify(function).onErrorApplyChecked(fallback);
                verify(fallback).apply("foo");
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                CheckedFunction<String, String, IOException> function = Spied.checkedFunction(s -> {
                    throw new IOException(s);
                });

                CheckedFunction<String, String, ParseException> fallback = Spied.checkedFunction(s -> {
                    throw new ParseException(s, 0);
                });

                CheckedFunction<String, String, ParseException> applying = function.onErrorApplyChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> applying.apply("foo"));
                assertEquals("foo", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(function).apply("foo");
                verify(function).onErrorApplyChecked(fallback);
                verify(fallback).apply("foo");
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsUnchecked() throws IOException, ParseException {
                CheckedFunction<String, String, IOException> function = Spied.checkedFunction(s -> {
                    throw new IOException(s);
                });

                CheckedFunction<String, String, ParseException> fallback = Spied.checkedFunction(s -> {
                    throw new IllegalStateException(s);
                });

                CheckedFunction<String, String, ParseException> applying = function.onErrorApplyChecked(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> applying.apply("foo"));
                assertEquals("foo", thrown.getMessage());

                verify(function).apply("foo");
                verify(function).onErrorApplyChecked(fallback);
                verify(fallback).apply("foo");
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedFunction<String, String, IOException> function = Spied.checkedFunction(s -> {
                throw new IllegalStateException(s);
            });

            CheckedFunction<String, String, ParseException> fallback = Spied.checkedFunction(s -> s + s);

            CheckedFunction<String, String, ParseException> applying = function.onErrorApplyChecked(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> applying.apply("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(function).apply("foo");
            verify(function).onErrorApplyChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorApplyUnchecked {

        @Test
        void testNullArgument() {
            CheckedFunction<String, String, IOException> function = Spied.checkedFunction(String::toUpperCase);

            assertThrows(NullPointerException.class, () -> function.onErrorApplyUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedFunction<String, String, IOException> function = Spied.checkedFunction(String::toUpperCase);

            Function<String, String> fallback = Spied.function(s -> s + s);

            Function<String, String> applying = function.onErrorApplyUnchecked(fallback);

            assertEquals("FOO", applying.apply("foo"));

            verify(function).apply("foo");
            verify(function).onErrorApplyUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                CheckedFunction<String, String, IOException> function = Spied.checkedFunction(s -> {
                    throw new IOException(s);
                });

                Function<String, String> fallback = Spied.function(s -> s + s);

                Function<String, String> applying = function.onErrorApplyUnchecked(fallback);

                assertEquals("foofoo", applying.apply("foo"));

                verify(function).apply("foo");
                verify(function).onErrorApplyUnchecked(fallback);
                verify(fallback).apply("foo");
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsUnchecked() throws IOException {
                CheckedFunction<String, String, IOException> function = Spied.checkedFunction(s -> {
                    throw new IOException(s);
                });

                Function<String, String> fallback = Spied.function(s -> {
                    throw new IllegalStateException(s);
                });

                Function<String, String> applying = function.onErrorApplyUnchecked(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> applying.apply("foo"));
                assertEquals("foo", thrown.getMessage());

                verify(function).apply("foo");
                verify(function).onErrorApplyUnchecked(fallback);
                verify(fallback).apply("foo");
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedFunction<String, String, IOException> function = Spied.checkedFunction(s -> {
                throw new IllegalStateException(s);
            });

            Function<String, String> fallback = Spied.function(s -> s + s);

            Function<String, String> applying = function.onErrorApplyUnchecked(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> applying.apply("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(function).apply("foo");
            verify(function).onErrorApplyUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorGetChecked {

        @Test
        void testNullArgument() {
            CheckedFunction<String, String, IOException> function = Spied.checkedFunction(String::toUpperCase);

            assertThrows(NullPointerException.class, () -> function.onErrorGetChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedFunction<String, String, IOException> function = Spied.checkedFunction(String::toUpperCase);

            CheckedSupplier<String, ExecutionException> fallback = Spied.checkedSupplier(() -> "bar");

            CheckedFunction<String, String, ExecutionException> getting = function.onErrorGetChecked(fallback);

            assertEquals("FOO", getting.apply("foo"));

            verify(function).apply("foo");
            verify(function).onErrorGetChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                CheckedFunction<String, String, IOException> function = Spied.checkedFunction(s -> {
                    throw new IOException(s);
                });

                CheckedSupplier<String, ParseException> fallback = Spied.checkedSupplier(() -> "bar");

                CheckedFunction<String, String, ParseException> getting = function.onErrorGetChecked(fallback);

                assertEquals("bar", getting.apply("foo"));

                verify(function).apply("foo");
                verify(function).onErrorGetChecked(fallback);
                verify(fallback).get();
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                CheckedFunction<String, String, IOException> function = Spied.checkedFunction(s -> {
                    throw new IOException(s);
                });

                CheckedSupplier<String, ParseException> fallback = Spied.checkedSupplier(() -> {
                    throw new ParseException("bar", 0);
                });

                CheckedFunction<String, String, ParseException> getting = function.onErrorGetChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> getting.apply("foo"));
                assertEquals("bar", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(function).apply("foo");
                verify(function).onErrorGetChecked(fallback);
                verify(fallback).get();
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsUnchecked() throws IOException, ParseException {
                CheckedFunction<String, String, IOException> function = Spied.checkedFunction(s -> {
                    throw new IOException(s);
                });

                CheckedSupplier<String, ParseException> fallback = Spied.checkedSupplier(() -> {
                    throw new IllegalStateException("bar");
                });

                CheckedFunction<String, String, ParseException> getting = function.onErrorGetChecked(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> getting.apply("foo"));
                assertEquals("bar", thrown.getMessage());

                verify(function).apply("foo");
                verify(function).onErrorGetChecked(fallback);
                verify(fallback).get();
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedFunction<String, String, IOException> function = Spied.checkedFunction(s -> {
                throw new IllegalStateException(s);
            });

            CheckedSupplier<String, ParseException> fallback = Spied.checkedSupplier(() -> "bar");

            CheckedFunction<String, String, ParseException> getting = function.onErrorGetChecked(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> getting.apply("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(function).apply("foo");
            verify(function).onErrorGetChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorGetUnchecked {

        @Test
        void testNullArgument() {
            CheckedFunction<String, String, IOException> function = Spied.checkedFunction(String::toUpperCase);

            assertThrows(NullPointerException.class, () -> function.onErrorGetUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedFunction<String, String, IOException> function = Spied.checkedFunction(String::toUpperCase);

            Supplier<String> fallback = Spied.supplier(() -> "bar");

            Function<String, String> getting = function.onErrorGetUnchecked(fallback);

            assertEquals("FOO", getting.apply("foo"));

            verify(function).apply("foo");
            verify(function).onErrorGetUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                CheckedFunction<String, String, IOException> function = Spied.checkedFunction(s -> {
                    throw new IOException(s);
                });

                Supplier<String> fallback = Spied.supplier(() -> "bar");

                Function<String, String> getting = function.onErrorGetUnchecked(fallback);

                assertEquals("bar", getting.apply("foo"));

                verify(function).apply("foo");
                verify(function).onErrorGetUnchecked(fallback);
                verify(fallback).get();
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsUnchecked() throws IOException {
                CheckedFunction<String, String, IOException> function = Spied.checkedFunction(s -> {
                    throw new IOException(s);
                });

                Supplier<String> fallback = Spied.supplier(() -> {
                    throw new IllegalStateException("bar");
                });

                Function<String, String> getting = function.onErrorGetUnchecked(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> getting.apply("foo"));
                assertEquals("bar", thrown.getMessage());

                verify(function).apply("foo");
                verify(function).onErrorGetUnchecked(fallback);
                verify(fallback).get();
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedFunction<String, String, IOException> function = Spied.checkedFunction(s -> {
                throw new IllegalStateException(s);
            });

            Supplier<String> fallback = Spied.supplier(() -> "bar");

            Function<String, String> getting = function.onErrorGetUnchecked(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> getting.apply("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(function).apply("foo");
            verify(function).onErrorGetUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorReturn {

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedFunction<String, String, IOException> function = Spied.checkedFunction(String::toUpperCase);

            Function<String, String> returning = function.onErrorReturn("bar");

            assertEquals("FOO", returning.apply("foo"));

            verify(function).apply("foo");
            verify(function).onErrorReturn("bar");
            verifyNoMoreInteractions(function);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedFunction<String, String, IOException> function = Spied.checkedFunction(s -> {
                throw new IOException(s);
            });

            Function<String, String> returning = function.onErrorReturn("bar");

            assertEquals("bar", returning.apply("foo"));

            verify(function).apply("foo");
            verify(function).onErrorReturn("bar");
            verifyNoMoreInteractions(function);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedFunction<String, String, IOException> function = Spied.checkedFunction(s -> {
                throw new IllegalStateException(s);
            });

            Function<String, String> returning = function.onErrorReturn("bar");

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> returning.apply("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(function).apply("foo");
            verify(function).onErrorReturn("bar");
            verifyNoMoreInteractions(function);
        }
    }

    @Nested
    class Unchecked {

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedFunction<String, String, IOException> function = Spied.checkedFunction(String::toUpperCase);

            Function<String, String> unchecked = function.unchecked();

            assertEquals("FOO", unchecked.apply("foo"));

            verify(function).apply("foo");
            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(function);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedFunction<String, String, IOException> function = Spied.checkedFunction(s -> {
                throw new IOException(s);
            });

            Function<String, String> unchecked = function.unchecked();

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.apply("foo"));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo", cause.getMessage());

            verify(function).apply("foo");
            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(function);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedFunction<String, String, IOException> function = Spied.checkedFunction(s -> {
                throw new IllegalArgumentException(s);
            });

            Function<String, String> unchecked = function.unchecked();

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> unchecked.apply("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(function).apply("foo");
            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(function);
        }
    }

    @Nested
    class Of {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> CheckedFunction.of(null));
        }

        @Test
        void testNonNullArgument() throws IOException {
            CheckedFunction<String, String, IOException> function = CheckedFunction.of(String::toUpperCase);

            assertEquals("FOO", function.apply("foo"));
        }
    }

    @Nested
    class Identity {

        @Test
        void testNullArgument() throws IOException {
            CheckedFunction<String, String, IOException> function = CheckedFunction.identity();

            assertNull(function.apply(null));
        }

        @Test
        void testNonNullArgument() throws IOException {
            CheckedFunction<String, String, IOException> function = CheckedFunction.identity();

            assertEquals("foo", function.apply("foo"));
        }
    }

    @Nested
    class UncheckedFactory {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> CheckedFunction.unchecked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedFunction<String, String, IOException> function = Spied.checkedFunction(String::toUpperCase);

            Function<String, String> unchecked = CheckedFunction.unchecked(function);

            assertEquals("FOO", unchecked.apply("foo"));

            verify(function).apply("foo");
            verifyNoMoreInteractions(function);
        }

        @Test
        void testArgumentThrowsChecked() throws IOException {
            CheckedFunction<String, String, IOException> function = Spied.checkedFunction(s -> {
                throw new IOException(s);
            });

            Function<String, String> unchecked = CheckedFunction.unchecked(function);

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.apply("foo"));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo", cause.getMessage());

            verify(function).apply("foo");
            verifyNoMoreInteractions(function);
        }

        @Test
        void testArgumentThrowsUnchecked() throws IOException {
            CheckedFunction<String, String, IOException> function = Spied.checkedFunction(s -> {
                throw new IllegalArgumentException(s);
            });

            Function<String, String> unchecked = CheckedFunction.unchecked(function);

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> unchecked.apply("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(function).apply("foo");
            verifyNoMoreInteractions(function);
        }
    }

    @Nested
    class CheckedWrapper {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> CheckedFunction.checked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedFunction<String, String, IOException> function = CheckedFunction.checked(String::toUpperCase);

            assertEquals("FOO", function.apply("foo"));
        }

        @Test
        void testargumentThrowsUnchecked() {
            CheckedFunction<String, String, IOException> function = CheckedFunction.checked(s -> {
                throw new UncheckedException(s, new IOException());
            });

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> function.apply("foo"));
            assertEquals("foo", thrown.getMessage());
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertNull(cause.getMessage());
        }
    }

    @Nested
    class CheckedUnwrapper {

        @Test
        void testNullArguments() {
            Function<String, String> function = String::toUpperCase;

            assertThrows(NullPointerException.class, () -> CheckedFunction.checked(null, IOException.class));
            assertThrows(NullPointerException.class, () -> CheckedFunction.checked(function, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedFunction<String, String, IOException> function = CheckedFunction.checked(String::toUpperCase, IOException.class);

            assertEquals("FOO", function.apply("foo"));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                CheckedFunction<String, String, IOException> function = CheckedFunction.checked(s -> {
                    throw new UncheckedException(new IOException(s));
                }, IOException.class);

                IOException thrown = assertThrows(IOException.class, () -> function.apply("foo"));
                assertEquals("foo", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                CheckedFunction<String, String, IOException> function = CheckedFunction.checked(s -> {
                    throw new UncheckedException(new FileNotFoundException(s));
                }, IOException.class);

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class, () -> function.apply("foo"));
                assertEquals("foo", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                CheckedFunction<String, String, IOException> function = CheckedFunction.checked(s -> {
                    throw new UncheckedException(new ParseException(s, 0));
                }, IOException.class);

                UncheckedException thrown = assertThrows(UncheckedException.class, () -> function.apply("foo"));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            CheckedFunction<String, String, IOException> function = CheckedFunction.checked(s -> {
                throw new IllegalStateException(s);
            }, IOException.class);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> function.apply("foo"));
            assertEquals("foo", thrown.getMessage());
        }
    }

    @Nested
    class InvokeAndUnwrap {

        @Test
        void testNullArguments() throws IOException {
            Function<String, String> function = s -> String.valueOf(s).toUpperCase();

            assertThrows(NullPointerException.class, () -> CheckedFunction.invokeAndUnwrap(null, "foo", IOException.class));
            assertThrows(NullPointerException.class, () -> CheckedFunction.invokeAndUnwrap(function, "foo", null));

            assertEquals("NULL", CheckedFunction.invokeAndUnwrap(function, null, IOException.class));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            Function<String, String> function = String::toUpperCase;

            assertEquals("FOO", CheckedFunction.invokeAndUnwrap(function, "foo", IOException.class));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                Function<String, String> function = s -> {
                    throw new UncheckedException(new IOException(s));
                };

                IOException thrown = assertThrows(IOException.class, () -> CheckedFunction.invokeAndUnwrap(function, "foo", IOException.class));
                assertEquals("foo", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                Function<String, String> function = s -> {
                    throw new UncheckedException(new FileNotFoundException(s));
                };

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class,
                        () -> CheckedFunction.invokeAndUnwrap(function, "foo", IOException.class));
                assertEquals("foo", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                Function<String, String> function = s -> {
                    throw new UncheckedException(new ParseException(s, 0));
                };

                UncheckedException thrown = assertThrows(UncheckedException.class,
                        () -> CheckedFunction.invokeAndUnwrap(function, "foo", IOException.class));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            Function<String, String> function = s -> {
                throw new IllegalStateException(s);
            };

            IllegalStateException thrown = assertThrows(IllegalStateException.class,
                    () -> CheckedFunction.invokeAndUnwrap(function, "foo", IOException.class));
            assertEquals("foo", thrown.getMessage());
        }
    }
}
