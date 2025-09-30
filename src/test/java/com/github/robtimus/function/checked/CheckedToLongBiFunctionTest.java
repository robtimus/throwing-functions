/*
 * CheckedToLongBiFunctionTest.java
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
import java.util.function.LongSupplier;
import java.util.function.ToLongBiFunction;
import java.util.function.ToLongFunction;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

@SuppressWarnings("nls")
class CheckedToLongBiFunctionTest {

    @Nested
    class OnErrorThrowAsChecked {

        @Test
        void testNullArgument() {
            CheckedToLongBiFunction<String, String, IOException> function = Spied.checkedToLongBiFunction((s1, s2) -> s1.concat(s2).length());

            assertThrows(NullPointerException.class, () -> function.onErrorThrowAsChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedToLongBiFunction<String, String, IOException> function = Spied.checkedToLongBiFunction((s1, s2) -> s1.concat(s2).length());

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedToLongBiFunction<String, String, ExecutionException> throwing = function.onErrorThrowAsChecked(errorMapper);

            assertEquals(6L, throwing.applyAsLong("foo", "bar"));

            verify(function).applyAsLong("foo", "bar");
            verify(function).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedToLongBiFunction<String, String, IOException> function = Spied.checkedToLongBiFunction((s1, s2) -> {
                throw new IOException(s1 + s2);
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedToLongBiFunction<String, String, ExecutionException> throwing = function.onErrorThrowAsChecked(errorMapper);

            ExecutionException thrown = assertThrows(ExecutionException.class, () -> throwing.applyAsLong("foo", "bar"));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foobar", cause.getMessage());

            verify(function).applyAsLong("foo", "bar");
            verify(function).onErrorThrowAsChecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(function, errorMapper);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            CheckedToLongBiFunction<String, String, IOException> function = Spied
                    .checkedToLongBiFunction((s1, s2) -> throwable.throwUnchecked(s1 + s2));

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedToLongBiFunction<String, String, ExecutionException> throwing = function.onErrorThrowAsChecked(errorMapper);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> throwing.applyAsLong("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());

            verify(function).applyAsLong("foo", "bar");
            verify(function).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }
    }

    @Nested
    class OnErrorThrowAsUnchecked {

        @Test
        void testNullArgument() {
            CheckedToLongBiFunction<String, String, IOException> function = Spied.checkedToLongBiFunction((s1, s2) -> s1.concat(s2).length());

            assertThrows(NullPointerException.class, () -> function.onErrorThrowAsUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedToLongBiFunction<String, String, IOException> function = Spied.checkedToLongBiFunction((s1, s2) -> s1.concat(s2).length());

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            ToLongBiFunction<String, String> throwing = function.onErrorThrowAsUnchecked(errorMapper);

            assertEquals(6L, throwing.applyAsLong("foo", "bar"));

            verify(function).applyAsLong("foo", "bar");
            verify(function).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedToLongBiFunction<String, String, IOException> function = Spied.checkedToLongBiFunction((s1, s2) -> {
                throw new IOException(s1 + s2);
            });

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            ToLongBiFunction<String, String> throwing = function.onErrorThrowAsUnchecked(errorMapper);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> throwing.applyAsLong("foo", "bar"));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foobar", cause.getMessage());

            verify(function).applyAsLong("foo", "bar");
            verify(function).onErrorThrowAsUnchecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(function, errorMapper);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            CheckedToLongBiFunction<String, String, IOException> function = Spied
                    .checkedToLongBiFunction((s1, s2) -> throwable.throwUnchecked(s1 + s2));

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            ToLongBiFunction<String, String> throwing = function.onErrorThrowAsUnchecked(errorMapper);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> throwing.applyAsLong("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());

            verify(function).applyAsLong("foo", "bar");
            verify(function).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }
    }

    @Nested
    class OnErrorHandleChecked {

        @Test
        void testNullArgument() {
            CheckedToLongBiFunction<String, String, IOException> function = Spied.checkedToLongBiFunction((s1, s2) -> s1.concat(s2).length());

            assertThrows(NullPointerException.class, () -> function.onErrorHandleChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedToLongBiFunction<String, String, IOException> function = Spied.checkedToLongBiFunction((s1, s2) -> s1.concat(s2).length());

            CheckedToLongFunction<IOException, ExecutionException> errorHandler = Spied.checkedToLongFunction(e -> 0L);

            CheckedToLongBiFunction<String, String, ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

            assertEquals(6L, handling.applyAsLong("foo", "bar"));

            verify(function).applyAsLong("foo", "bar");
            verify(function).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException, ExecutionException {
                CheckedToLongBiFunction<String, String, IOException> function = Spied.checkedToLongBiFunction((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                CheckedToLongFunction<IOException, ExecutionException> errorHandler = Spied.checkedToLongFunction(e -> 0L);

                CheckedToLongBiFunction<String, String, ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

                assertEquals(0L, handling.applyAsLong("foo", "bar"));

                verify(function).applyAsLong("foo", "bar");
                verify(function).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsLong(any());
                verifyNoMoreInteractions(function, errorHandler);
            }

            @Test
            void testHandlerThrowsChecked() throws IOException, ExecutionException {
                CheckedToLongBiFunction<String, String, IOException> function = Spied.checkedToLongBiFunction((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                CheckedToLongFunction<IOException, ExecutionException> errorHandler = Spied.checkedToLongFunction(e -> {
                    throw new ExecutionException(e);
                });

                CheckedToLongBiFunction<String, String, ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

                ExecutionException thrown = assertThrows(ExecutionException.class, () -> handling.applyAsLong("foo", "bar"));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foobar", cause.getMessage());

                verify(function).applyAsLong("foo", "bar");
                verify(function).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsLong(any());
                verifyNoMoreInteractions(function, errorHandler);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testHandlerThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ExecutionException {
                CheckedToLongBiFunction<String, String, IOException> function = Spied.checkedToLongBiFunction((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                CheckedToLongFunction<IOException, ExecutionException> errorHandler = Spied.checkedToLongFunction(throwable::throwUnchecked);

                CheckedToLongBiFunction<String, String, ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.applyAsLong("foo", "bar"));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foobar", cause.getMessage());

                verify(function).applyAsLong("foo", "bar");
                verify(function).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsLong(any());
                verifyNoMoreInteractions(function, errorHandler);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            CheckedToLongBiFunction<String, String, IOException> function = Spied
                    .checkedToLongBiFunction((s1, s2) -> throwable.throwUnchecked(s1 + s2));

            CheckedToLongFunction<IOException, ExecutionException> errorHandler = Spied.checkedToLongFunction(e -> 0L);

            CheckedToLongBiFunction<String, String, ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.applyAsLong("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());

            verify(function).applyAsLong("foo", "bar");
            verify(function).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }
    }

    @Nested
    class OnErrorHandleUnchecked {

        @Test
        void testNullArgument() {
            CheckedToLongBiFunction<String, String, IOException> function = Spied.checkedToLongBiFunction((s1, s2) -> s1.concat(s2).length());

            assertThrows(NullPointerException.class, () -> function.onErrorHandleUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedToLongBiFunction<String, String, IOException> function = Spied.checkedToLongBiFunction((s1, s2) -> s1.concat(s2).length());

            ToLongFunction<IOException> errorHandler = Spied.toLongFunction(e -> 0L);

            ToLongBiFunction<String, String> handling = function.onErrorHandleUnchecked(errorHandler);

            assertEquals(6L, handling.applyAsLong("foo", "bar"));

            verify(function).applyAsLong("foo", "bar");
            verify(function).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException {
                CheckedToLongBiFunction<String, String, IOException> function = Spied.checkedToLongBiFunction((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                ToLongFunction<IOException> errorHandler = Spied.toLongFunction(e -> 0L);

                ToLongBiFunction<String, String> handling = function.onErrorHandleUnchecked(errorHandler);

                assertEquals(0L, handling.applyAsLong("foo", "bar"));

                verify(function).applyAsLong("foo", "bar");
                verify(function).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).applyAsLong(any());
                verifyNoMoreInteractions(function, errorHandler);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testHandlerThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                CheckedToLongBiFunction<String, String, IOException> function = Spied.checkedToLongBiFunction((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                ToLongFunction<IOException> errorHandler = Spied.toLongFunction(throwable::throwUnchecked);

                ToLongBiFunction<String, String> handling = function.onErrorHandleUnchecked(errorHandler);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.applyAsLong("foo", "bar"));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foobar", cause.getMessage());

                verify(function).applyAsLong("foo", "bar");
                verify(function).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).applyAsLong(any());
                verifyNoMoreInteractions(function, errorHandler);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            CheckedToLongBiFunction<String, String, IOException> function = Spied
                    .checkedToLongBiFunction((s1, s2) -> throwable.throwUnchecked(s1 + s2));

            ToLongFunction<IOException> errorHandler = Spied.toLongFunction(e -> 0L);

            ToLongBiFunction<String, String> handling = function.onErrorHandleUnchecked(errorHandler);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.applyAsLong("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());

            verify(function).applyAsLong("foo", "bar");
            verify(function).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }
    }

    @Nested
    class OnErrorApplyChecked {

        @Test
        void testNullArgument() {
            CheckedToLongBiFunction<String, String, IOException> function = Spied.checkedToLongBiFunction((s1, s2) -> s1.concat(s2).length());

            assertThrows(NullPointerException.class, () -> function.onErrorApplyChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedToLongBiFunction<String, String, IOException> function = Spied.checkedToLongBiFunction((s1, s2) -> s1.concat(s2).length());

            CheckedToLongBiFunction<String, String, ExecutionException> fallback = Spied
                    .checkedToLongBiFunction((s1, s2) -> 2 * s1.concat(s2).length());

            CheckedToLongBiFunction<String, String, ExecutionException> applying = function.onErrorApplyChecked(fallback);

            assertEquals(6L, applying.applyAsLong("foo", "bar"));

            verify(function).applyAsLong("foo", "bar");
            verify(function).onErrorApplyChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                CheckedToLongBiFunction<String, String, IOException> function = Spied.checkedToLongBiFunction((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                CheckedToLongBiFunction<String, String, ParseException> fallback = Spied
                        .checkedToLongBiFunction((s1, s2) -> 2 * s1.concat(s2).length());

                CheckedToLongBiFunction<String, String, ParseException> applying = function.onErrorApplyChecked(fallback);

                assertEquals(12L, applying.applyAsLong("foo", "bar"));

                verify(function).applyAsLong("foo", "bar");
                verify(function).onErrorApplyChecked(fallback);
                verify(fallback).applyAsLong("foo", "bar");
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                CheckedToLongBiFunction<String, String, IOException> function = Spied.checkedToLongBiFunction((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                CheckedToLongBiFunction<String, String, ParseException> fallback = Spied.checkedToLongBiFunction((s1, s2) -> {
                    throw new ParseException(s1 + s2, 0);
                });

                CheckedToLongBiFunction<String, String, ParseException> applying = function.onErrorApplyChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> applying.applyAsLong("foo", "bar"));
                assertEquals("foobar", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(function).applyAsLong("foo", "bar");
                verify(function).onErrorApplyChecked(fallback);
                verify(fallback).applyAsLong("foo", "bar");
                verifyNoMoreInteractions(function, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ParseException {
                CheckedToLongBiFunction<String, String, IOException> function = Spied.checkedToLongBiFunction((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                CheckedToLongBiFunction<String, String, ParseException> fallback = Spied
                        .checkedToLongBiFunction((s1, s2) -> throwable.throwUnchecked(s1 + s2));

                CheckedToLongBiFunction<String, String, ParseException> applying = function.onErrorApplyChecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.applyAsLong("foo", "bar"));
                assertEquals("foobar", thrown.getMessage());

                verify(function).applyAsLong("foo", "bar");
                verify(function).onErrorApplyChecked(fallback);
                verify(fallback).applyAsLong("foo", "bar");
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            CheckedToLongBiFunction<String, String, IOException> function = Spied
                    .checkedToLongBiFunction((s1, s2) -> throwable.throwUnchecked(s1 + s2));

            CheckedToLongBiFunction<String, String, ParseException> fallback = Spied.checkedToLongBiFunction((s1, s2) -> 2 * s1.concat(s2).length());

            CheckedToLongBiFunction<String, String, ParseException> applying = function.onErrorApplyChecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.applyAsLong("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());

            verify(function).applyAsLong("foo", "bar");
            verify(function).onErrorApplyChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorApplyUnchecked {

        @Test
        void testNullArgument() {
            CheckedToLongBiFunction<String, String, IOException> function = Spied.checkedToLongBiFunction((s1, s2) -> s1.concat(s2).length());

            assertThrows(NullPointerException.class, () -> function.onErrorApplyUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedToLongBiFunction<String, String, IOException> function = Spied.checkedToLongBiFunction((s1, s2) -> s1.concat(s2).length());

            ToLongBiFunction<String, String> fallback = Spied.toLongBiFunction((s1, s2) -> 2 * s1.concat(s2).length());

            ToLongBiFunction<String, String> applying = function.onErrorApplyUnchecked(fallback);

            assertEquals(6L, applying.applyAsLong("foo", "bar"));

            verify(function).applyAsLong("foo", "bar");
            verify(function).onErrorApplyUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                CheckedToLongBiFunction<String, String, IOException> function = Spied.checkedToLongBiFunction((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                ToLongBiFunction<String, String> fallback = Spied.toLongBiFunction((s1, s2) -> 2 * s1.concat(s2).length());

                ToLongBiFunction<String, String> applying = function.onErrorApplyUnchecked(fallback);

                assertEquals(12L, applying.applyAsLong("foo", "bar"));

                verify(function).applyAsLong("foo", "bar");
                verify(function).onErrorApplyUnchecked(fallback);
                verify(fallback).applyAsLong("foo", "bar");
                verifyNoMoreInteractions(function, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                CheckedToLongBiFunction<String, String, IOException> function = Spied.checkedToLongBiFunction((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                ToLongBiFunction<String, String> fallback = Spied.toLongBiFunction((s1, s2) -> throwable.throwUnchecked(s1 + s2));

                ToLongBiFunction<String, String> applying = function.onErrorApplyUnchecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.applyAsLong("foo", "bar"));
                assertEquals("foobar", thrown.getMessage());

                verify(function).applyAsLong("foo", "bar");
                verify(function).onErrorApplyUnchecked(fallback);
                verify(fallback).applyAsLong("foo", "bar");
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            CheckedToLongBiFunction<String, String, IOException> function = Spied
                    .checkedToLongBiFunction((s1, s2) -> throwable.throwUnchecked(s1 + s2));

            ToLongBiFunction<String, String> fallback = Spied.toLongBiFunction((s1, s2) -> 2 * s1.concat(s2).length());

            ToLongBiFunction<String, String> applying = function.onErrorApplyUnchecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.applyAsLong("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());

            verify(function).applyAsLong("foo", "bar");
            verify(function).onErrorApplyUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorGetChecked {

        @Test
        void testNullArgument() {
            CheckedToLongBiFunction<String, String, IOException> function = Spied.checkedToLongBiFunction((s1, s2) -> s1.concat(s2).length());

            assertThrows(NullPointerException.class, () -> function.onErrorGetChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedToLongBiFunction<String, String, IOException> function = Spied.checkedToLongBiFunction((s1, s2) -> s1.concat(s2).length());

            CheckedLongSupplier<ExecutionException> fallback = Spied.checkedLongSupplier(() -> 0L);

            CheckedToLongBiFunction<String, String, ExecutionException> getting = function.onErrorGetChecked(fallback);

            assertEquals(6L, getting.applyAsLong("foo", "bar"));

            verify(function).applyAsLong("foo", "bar");
            verify(function).onErrorGetChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                CheckedToLongBiFunction<String, String, IOException> function = Spied.checkedToLongBiFunction((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                CheckedLongSupplier<ParseException> fallback = Spied.checkedLongSupplier(() -> 0L);

                CheckedToLongBiFunction<String, String, ParseException> getting = function.onErrorGetChecked(fallback);

                assertEquals(0L, getting.applyAsLong("foo", "bar"));

                verify(function).applyAsLong("foo", "bar");
                verify(function).onErrorGetChecked(fallback);
                verify(fallback).getAsLong();
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                CheckedToLongBiFunction<String, String, IOException> function = Spied.checkedToLongBiFunction((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                CheckedLongSupplier<ParseException> fallback = Spied.checkedLongSupplier(() -> {
                    throw new ParseException("bar", 0);
                });

                CheckedToLongBiFunction<String, String, ParseException> getting = function.onErrorGetChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> getting.applyAsLong("foo", "bar"));
                assertEquals("bar", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(function).applyAsLong("foo", "bar");
                verify(function).onErrorGetChecked(fallback);
                verify(fallback).getAsLong();
                verifyNoMoreInteractions(function, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ParseException {
                CheckedToLongBiFunction<String, String, IOException> function = Spied.checkedToLongBiFunction((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                CheckedLongSupplier<ParseException> fallback = Spied.checkedLongSupplier(() -> throwable.throwUnchecked("bar"));

                CheckedToLongBiFunction<String, String, ParseException> getting = function.onErrorGetChecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.applyAsLong("foo", "bar"));
                assertEquals("bar", thrown.getMessage());

                verify(function).applyAsLong("foo", "bar");
                verify(function).onErrorGetChecked(fallback);
                verify(fallback).getAsLong();
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            CheckedToLongBiFunction<String, String, IOException> function = Spied
                    .checkedToLongBiFunction((s1, s2) -> throwable.throwUnchecked(s1 + s2));

            CheckedLongSupplier<ParseException> fallback = Spied.checkedLongSupplier(() -> 0L);

            CheckedToLongBiFunction<String, String, ParseException> getting = function.onErrorGetChecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.applyAsLong("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());

            verify(function).applyAsLong("foo", "bar");
            verify(function).onErrorGetChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorGetUnchecked {

        @Test
        void testNullArgument() {
            CheckedToLongBiFunction<String, String, IOException> function = Spied.checkedToLongBiFunction((s1, s2) -> s1.concat(s2).length());

            assertThrows(NullPointerException.class, () -> function.onErrorGetUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedToLongBiFunction<String, String, IOException> function = Spied.checkedToLongBiFunction((s1, s2) -> s1.concat(s2).length());

            LongSupplier fallback = Spied.longSupplier(() -> 0L);

            ToLongBiFunction<String, String> getting = function.onErrorGetUnchecked(fallback);

            assertEquals(6L, getting.applyAsLong("foo", "bar"));

            verify(function).applyAsLong("foo", "bar");
            verify(function).onErrorGetUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                CheckedToLongBiFunction<String, String, IOException> function = Spied.checkedToLongBiFunction((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                LongSupplier fallback = Spied.longSupplier(() -> 0L);

                ToLongBiFunction<String, String> getting = function.onErrorGetUnchecked(fallback);

                assertEquals(0L, getting.applyAsLong("foo", "bar"));

                verify(function).applyAsLong("foo", "bar");
                verify(function).onErrorGetUnchecked(fallback);
                verify(fallback).getAsLong();
                verifyNoMoreInteractions(function, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                CheckedToLongBiFunction<String, String, IOException> function = Spied.checkedToLongBiFunction((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                LongSupplier fallback = Spied.longSupplier(() -> throwable.throwUnchecked("bar"));

                ToLongBiFunction<String, String> getting = function.onErrorGetUnchecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.applyAsLong("foo", "bar"));
                assertEquals("bar", thrown.getMessage());

                verify(function).applyAsLong("foo", "bar");
                verify(function).onErrorGetUnchecked(fallback);
                verify(fallback).getAsLong();
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            CheckedToLongBiFunction<String, String, IOException> function = Spied
                    .checkedToLongBiFunction((s1, s2) -> throwable.throwUnchecked(s1 + s2));

            LongSupplier fallback = Spied.longSupplier(() -> 0L);

            ToLongBiFunction<String, String> getting = function.onErrorGetUnchecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.applyAsLong("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());

            verify(function).applyAsLong("foo", "bar");
            verify(function).onErrorGetUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorReturn {

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedToLongBiFunction<String, String, IOException> function = Spied.checkedToLongBiFunction((s1, s2) -> s1.concat(s2).length());

            ToLongBiFunction<String, String> returning = function.onErrorReturn(0L);

            assertEquals(6L, returning.applyAsLong("foo", "bar"));

            verify(function).applyAsLong("foo", "bar");
            verify(function).onErrorReturn(0L);
            verifyNoMoreInteractions(function);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedToLongBiFunction<String, String, IOException> function = Spied.checkedToLongBiFunction((s1, s2) -> {
                throw new IOException(s1 + s2);
            });

            ToLongBiFunction<String, String> returning = function.onErrorReturn(0L);

            assertEquals(0L, returning.applyAsLong("foo", "bar"));

            verify(function).applyAsLong("foo", "bar");
            verify(function).onErrorReturn(0L);
            verifyNoMoreInteractions(function);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            CheckedToLongBiFunction<String, String, IOException> function = Spied
                    .checkedToLongBiFunction((s1, s2) -> throwable.throwUnchecked(s1 + s2));

            ToLongBiFunction<String, String> returning = function.onErrorReturn(0L);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> returning.applyAsLong("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());

            verify(function).applyAsLong("foo", "bar");
            verify(function).onErrorReturn(0L);
            verifyNoMoreInteractions(function);
        }
    }

    @Nested
    class Unchecked {

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedToLongBiFunction<String, String, IOException> function = Spied.checkedToLongBiFunction((s1, s2) -> s1.concat(s2).length());

            ToLongBiFunction<String, String> unchecked = function.unchecked();

            assertEquals(6L, unchecked.applyAsLong("foo", "bar"));

            verify(function).applyAsLong("foo", "bar");
            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(function);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedToLongBiFunction<String, String, IOException> function = Spied.checkedToLongBiFunction((s1, s2) -> {
                throw new IOException(s1 + s2);
            });

            ToLongBiFunction<String, String> unchecked = function.unchecked();

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.applyAsLong("foo", "bar"));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foobar", cause.getMessage());

            verify(function).applyAsLong("foo", "bar");
            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(function);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            CheckedToLongBiFunction<String, String, IOException> function = Spied
                    .checkedToLongBiFunction((s1, s2) -> throwable.throwUnchecked(s1 + s2));

            ToLongBiFunction<String, String> unchecked = function.unchecked();

            Throwable thrown = assertThrows(throwable.throwableType(), () -> unchecked.applyAsLong("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());

            verify(function).applyAsLong("foo", "bar");
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
            CheckedToLongBiFunction<String, String, IOException> function = CheckedToLongBiFunction.of((s1, s2) -> s1.concat(s2).length());

            assertEquals(6L, function.applyAsLong("foo", "bar"));
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
            CheckedToLongBiFunction<String, String, IOException> function = Spied.checkedToLongBiFunction((s1, s2) -> s1.concat(s2).length());

            ToLongBiFunction<String, String> unchecked = CheckedToLongBiFunction.unchecked(function);

            assertEquals(6L, unchecked.applyAsLong("foo", "bar"));

            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verify(function).applyAsLong("foo", "bar");
            verifyNoMoreInteractions(function);
        }

        @Test
        void testArgumentThrowsChecked() throws IOException {
            CheckedToLongBiFunction<String, String, IOException> function = Spied.checkedToLongBiFunction((s1, s2) -> {
                throw new IOException(s1 + s2);
            });

            ToLongBiFunction<String, String> unchecked = CheckedToLongBiFunction.unchecked(function);

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.applyAsLong("foo", "bar"));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foobar", cause.getMessage());

            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verify(function).applyAsLong("foo", "bar");
            verifyNoMoreInteractions(function);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testArgumentThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            CheckedToLongBiFunction<String, String, IOException> function = Spied
                    .checkedToLongBiFunction((s1, s2) -> throwable.throwUnchecked(s1 + s2));

            ToLongBiFunction<String, String> unchecked = CheckedToLongBiFunction.unchecked(function);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> unchecked.applyAsLong("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());

            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verify(function).applyAsLong("foo", "bar");
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
            CheckedToLongBiFunction<String, String, IOException> function = CheckedToLongBiFunction.checked((s1, s2) -> s1.concat(s2).length());

            assertEquals(6L, function.applyAsLong("foo", "bar"));
        }

        @Test
        void testArgumentThrowsUnchecked() {
            CheckedToLongBiFunction<String, String, IOException> function = CheckedToLongBiFunction.checked((s1, s2) -> {
                throw new UncheckedException(s1 + s2, new IOException());
            });

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> function.applyAsLong("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertNull(cause.getMessage());
        }
    }

    @Nested
    class CheckedUnwrapper {

        @Test
        void testNullArguments() {
            ToLongBiFunction<String, String> function = (s1, s2) -> s1.concat(s2).length();

            assertThrows(NullPointerException.class, () -> CheckedToLongBiFunction.checked(null, IOException.class));
            assertThrows(NullPointerException.class, () -> CheckedToLongBiFunction.checked(function, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedToLongBiFunction<String, String, IOException> function = CheckedToLongBiFunction
                    .checked((s1, s2) -> s1.concat(s2).length(), IOException.class);

            assertEquals(6L, function.applyAsLong("foo", "bar"));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                CheckedToLongBiFunction<String, String, IOException> function = CheckedToLongBiFunction.checked((s1, s2) -> {
                    throw new UncheckedException(new IOException(s1 + s2));
                }, IOException.class);

                IOException thrown = assertThrows(IOException.class, () -> function.applyAsLong("foo", "bar"));
                assertEquals("foobar", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                CheckedToLongBiFunction<String, String, IOException> function = CheckedToLongBiFunction.checked((s1, s2) -> {
                    throw new UncheckedException(new FileNotFoundException(s1 + s2));
                }, IOException.class);

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class, () -> function.applyAsLong("foo", "bar"));
                assertEquals("foobar", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                CheckedToLongBiFunction<String, String, IOException> function = CheckedToLongBiFunction.checked((s1, s2) -> {
                    throw new UncheckedException(new ParseException(s1 + s2, 0));
                }, IOException.class);

                UncheckedException thrown = assertThrows(UncheckedException.class, () -> function.applyAsLong("foo", "bar"));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("foobar", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            CheckedToLongBiFunction<String, String, IOException> function = CheckedToLongBiFunction.checked((s1, s2) -> {
                throw new IllegalStateException(s1 + s2);
            }, IOException.class);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> function.applyAsLong("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());
        }
    }

    @Nested
    class InvokeAndUnwrap {

        @Test
        void testNullArguments() throws IOException {
            ToLongBiFunction<String, String> function = (s1, s2) -> (s1 + s2).length();

            assertThrows(NullPointerException.class, () -> CheckedToLongBiFunction.invokeAndUnwrap(null, "foo", "bar", IOException.class));
            assertThrows(NullPointerException.class, () -> CheckedToLongBiFunction.invokeAndUnwrap(function, "foo", "bar", null));

            assertEquals(7L, CheckedToLongBiFunction.invokeAndUnwrap(function, null, "bar", IOException.class));
            assertEquals(7L, CheckedToLongBiFunction.invokeAndUnwrap(function, "foo", null, IOException.class));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ToLongBiFunction<String, String> function = (s1, s2) -> s1.concat(s2).length();

            assertEquals(6L, CheckedToLongBiFunction.invokeAndUnwrap(function, "foo", "bar", IOException.class));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                ToLongBiFunction<String, String> function = (s1, s2) -> {
                    throw new UncheckedException(new IOException(s1 + s2));
                };

                IOException thrown = assertThrows(IOException.class,
                        () -> CheckedToLongBiFunction.invokeAndUnwrap(function, "foo", "bar", IOException.class));
                assertEquals("foobar", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                ToLongBiFunction<String, String> function = (s1, s2) -> {
                    throw new UncheckedException(new FileNotFoundException(s1 + s2));
                };

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class,
                        () -> CheckedToLongBiFunction.invokeAndUnwrap(function, "foo", "bar", IOException.class));
                assertEquals("foobar", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                ToLongBiFunction<String, String> function = (s1, s2) -> {
                    throw new UncheckedException(new ParseException(s1 + s2, 0));
                };

                UncheckedException thrown = assertThrows(UncheckedException.class,
                        () -> CheckedToLongBiFunction.invokeAndUnwrap(function, "foo", "bar", IOException.class));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("foobar", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            ToLongBiFunction<String, String> function = (s1, s2) -> {
                throw new IllegalStateException(s1 + s2);
            };

            IllegalStateException thrown = assertThrows(IllegalStateException.class,
                    () -> CheckedToLongBiFunction.invokeAndUnwrap(function, "foo", "bar", IOException.class));
            assertEquals("foobar", thrown.getMessage());
        }
    }
}
