/*
 * CheckedToIntBiFunctionTest.java
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
import java.util.function.IntSupplier;
import java.util.function.ToIntBiFunction;
import java.util.function.ToIntFunction;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

@SuppressWarnings("nls")
class CheckedToIntBiFunctionTest {

    @Nested
    class OnErrorThrowAsChecked {

        @Test
        void testNullArgument() {
            CheckedToIntBiFunction<String, String, IOException> function = Spied.checkedToIntBiFunction((s1, s2) -> s1.concat(s2).length());

            assertThrows(NullPointerException.class, () -> function.onErrorThrowAsChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedToIntBiFunction<String, String, IOException> function = Spied.checkedToIntBiFunction((s1, s2) -> s1.concat(s2).length());

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedToIntBiFunction<String, String, ExecutionException> throwing = function.onErrorThrowAsChecked(errorMapper);

            assertEquals(6, throwing.applyAsInt("foo", "bar"));

            verify(function).applyAsInt("foo", "bar");
            verify(function).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedToIntBiFunction<String, String, IOException> function = Spied.checkedToIntBiFunction((s1, s2) -> {
                throw new IOException(s1 + s2);
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedToIntBiFunction<String, String, ExecutionException> throwing = function.onErrorThrowAsChecked(errorMapper);

            ExecutionException thrown = assertThrows(ExecutionException.class, () -> throwing.applyAsInt("foo", "bar"));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foobar", cause.getMessage());

            verify(function).applyAsInt("foo", "bar");
            verify(function).onErrorThrowAsChecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(function, errorMapper);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            CheckedToIntBiFunction<String, String, IOException> function = Spied
                    .checkedToIntBiFunction((s1, s2) -> throwable.throwUnchecked(s1 + s2));

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedToIntBiFunction<String, String, ExecutionException> throwing = function.onErrorThrowAsChecked(errorMapper);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> throwing.applyAsInt("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());

            verify(function).applyAsInt("foo", "bar");
            verify(function).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }
    }

    @Nested
    class OnErrorThrowAsUnchecked {

        @Test
        void testNullArgument() {
            CheckedToIntBiFunction<String, String, IOException> function = Spied.checkedToIntBiFunction((s1, s2) -> s1.concat(s2).length());

            assertThrows(NullPointerException.class, () -> function.onErrorThrowAsUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedToIntBiFunction<String, String, IOException> function = Spied.checkedToIntBiFunction((s1, s2) -> s1.concat(s2).length());

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            ToIntBiFunction<String, String> throwing = function.onErrorThrowAsUnchecked(errorMapper);

            assertEquals(6, throwing.applyAsInt("foo", "bar"));

            verify(function).applyAsInt("foo", "bar");
            verify(function).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedToIntBiFunction<String, String, IOException> function = Spied.checkedToIntBiFunction((s1, s2) -> {
                throw new IOException(s1 + s2);
            });

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            ToIntBiFunction<String, String> throwing = function.onErrorThrowAsUnchecked(errorMapper);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> throwing.applyAsInt("foo", "bar"));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foobar", cause.getMessage());

            verify(function).applyAsInt("foo", "bar");
            verify(function).onErrorThrowAsUnchecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(function, errorMapper);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            CheckedToIntBiFunction<String, String, IOException> function = Spied
                    .checkedToIntBiFunction((s1, s2) -> throwable.throwUnchecked(s1 + s2));

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            ToIntBiFunction<String, String> throwing = function.onErrorThrowAsUnchecked(errorMapper);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> throwing.applyAsInt("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());

            verify(function).applyAsInt("foo", "bar");
            verify(function).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }
    }

    @Nested
    class OnErrorHandleChecked {

        @Test
        void testNullArgument() {
            CheckedToIntBiFunction<String, String, IOException> function = Spied.checkedToIntBiFunction((s1, s2) -> s1.concat(s2).length());

            assertThrows(NullPointerException.class, () -> function.onErrorHandleChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedToIntBiFunction<String, String, IOException> function = Spied.checkedToIntBiFunction((s1, s2) -> s1.concat(s2).length());

            CheckedToIntFunction<IOException, ExecutionException> errorHandler = Spied.checkedToIntFunction(e -> 0);

            CheckedToIntBiFunction<String, String, ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

            assertEquals(6, handling.applyAsInt("foo", "bar"));

            verify(function).applyAsInt("foo", "bar");
            verify(function).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException, ExecutionException {
                CheckedToIntBiFunction<String, String, IOException> function = Spied.checkedToIntBiFunction((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                CheckedToIntFunction<IOException, ExecutionException> errorHandler = Spied.checkedToIntFunction(e -> 0);

                CheckedToIntBiFunction<String, String, ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

                assertEquals(0, handling.applyAsInt("foo", "bar"));

                verify(function).applyAsInt("foo", "bar");
                verify(function).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsInt(any());
                verifyNoMoreInteractions(function, errorHandler);
            }

            @Test
            void testHandlerThrowsChecked() throws IOException, ExecutionException {
                CheckedToIntBiFunction<String, String, IOException> function = Spied.checkedToIntBiFunction((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                CheckedToIntFunction<IOException, ExecutionException> errorHandler = Spied.checkedToIntFunction(e -> {
                    throw new ExecutionException(e);
                });

                CheckedToIntBiFunction<String, String, ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

                ExecutionException thrown = assertThrows(ExecutionException.class, () -> handling.applyAsInt("foo", "bar"));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foobar", cause.getMessage());

                verify(function).applyAsInt("foo", "bar");
                verify(function).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsInt(any());
                verifyNoMoreInteractions(function, errorHandler);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testHandlerThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ExecutionException {
                CheckedToIntBiFunction<String, String, IOException> function = Spied.checkedToIntBiFunction((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                CheckedToIntFunction<IOException, ExecutionException> errorHandler = Spied.checkedToIntFunction(throwable::throwUnchecked);

                CheckedToIntBiFunction<String, String, ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.applyAsInt("foo", "bar"));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foobar", cause.getMessage());

                verify(function).applyAsInt("foo", "bar");
                verify(function).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsInt(any());
                verifyNoMoreInteractions(function, errorHandler);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            CheckedToIntBiFunction<String, String, IOException> function = Spied
                    .checkedToIntBiFunction((s1, s2) -> throwable.throwUnchecked(s1 + s2));

            CheckedToIntFunction<IOException, ExecutionException> errorHandler = Spied.checkedToIntFunction(e -> 0);

            CheckedToIntBiFunction<String, String, ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.applyAsInt("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());

            verify(function).applyAsInt("foo", "bar");
            verify(function).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }
    }

    @Nested
    class OnErrorHandleUnchecked {

        @Test
        void testNullArgument() {
            CheckedToIntBiFunction<String, String, IOException> function = Spied.checkedToIntBiFunction((s1, s2) -> s1.concat(s2).length());

            assertThrows(NullPointerException.class, () -> function.onErrorHandleUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedToIntBiFunction<String, String, IOException> function = Spied.checkedToIntBiFunction((s1, s2) -> s1.concat(s2).length());

            ToIntFunction<IOException> errorHandler = Spied.toIntFunction(e -> 0);

            ToIntBiFunction<String, String> handling = function.onErrorHandleUnchecked(errorHandler);

            assertEquals(6, handling.applyAsInt("foo", "bar"));

            verify(function).applyAsInt("foo", "bar");
            verify(function).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException {
                CheckedToIntBiFunction<String, String, IOException> function = Spied.checkedToIntBiFunction((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                ToIntFunction<IOException> errorHandler = Spied.toIntFunction(e -> 0);

                ToIntBiFunction<String, String> handling = function.onErrorHandleUnchecked(errorHandler);

                assertEquals(0, handling.applyAsInt("foo", "bar"));

                verify(function).applyAsInt("foo", "bar");
                verify(function).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).applyAsInt(any());
                verifyNoMoreInteractions(function, errorHandler);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testHandlerThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                CheckedToIntBiFunction<String, String, IOException> function = Spied.checkedToIntBiFunction((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                ToIntFunction<IOException> errorHandler = Spied.toIntFunction(throwable::throwUnchecked);

                ToIntBiFunction<String, String> handling = function.onErrorHandleUnchecked(errorHandler);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.applyAsInt("foo", "bar"));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foobar", cause.getMessage());

                verify(function).applyAsInt("foo", "bar");
                verify(function).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).applyAsInt(any());
                verifyNoMoreInteractions(function, errorHandler);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            CheckedToIntBiFunction<String, String, IOException> function = Spied
                    .checkedToIntBiFunction((s1, s2) -> throwable.throwUnchecked(s1 + s2));

            ToIntFunction<IOException> errorHandler = Spied.toIntFunction(e -> 0);

            ToIntBiFunction<String, String> handling = function.onErrorHandleUnchecked(errorHandler);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.applyAsInt("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());

            verify(function).applyAsInt("foo", "bar");
            verify(function).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }
    }

    @Nested
    class OnErrorApplyChecked {

        @Test
        void testNullArgument() {
            CheckedToIntBiFunction<String, String, IOException> function = Spied.checkedToIntBiFunction((s1, s2) -> s1.concat(s2).length());

            assertThrows(NullPointerException.class, () -> function.onErrorApplyChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedToIntBiFunction<String, String, IOException> function = Spied.checkedToIntBiFunction((s1, s2) -> s1.concat(s2).length());

            CheckedToIntBiFunction<String, String, ExecutionException> fallback = Spied
                    .checkedToIntBiFunction((s1, s2) -> 2 * s1.concat(s2).length());

            CheckedToIntBiFunction<String, String, ExecutionException> applying = function.onErrorApplyChecked(fallback);

            assertEquals(6, applying.applyAsInt("foo", "bar"));

            verify(function).applyAsInt("foo", "bar");
            verify(function).onErrorApplyChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                CheckedToIntBiFunction<String, String, IOException> function = Spied.checkedToIntBiFunction((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                CheckedToIntBiFunction<String, String, ParseException> fallback = Spied
                        .checkedToIntBiFunction((s1, s2) -> 2 * s1.concat(s2).length());

                CheckedToIntBiFunction<String, String, ParseException> applying = function.onErrorApplyChecked(fallback);

                assertEquals(12, applying.applyAsInt("foo", "bar"));

                verify(function).applyAsInt("foo", "bar");
                verify(function).onErrorApplyChecked(fallback);
                verify(fallback).applyAsInt("foo", "bar");
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                CheckedToIntBiFunction<String, String, IOException> function = Spied.checkedToIntBiFunction((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                CheckedToIntBiFunction<String, String, ParseException> fallback = Spied.checkedToIntBiFunction((s1, s2) -> {
                    throw new ParseException(s1 + s2, 0);
                });

                CheckedToIntBiFunction<String, String, ParseException> applying = function.onErrorApplyChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> applying.applyAsInt("foo", "bar"));
                assertEquals("foobar", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(function).applyAsInt("foo", "bar");
                verify(function).onErrorApplyChecked(fallback);
                verify(fallback).applyAsInt("foo", "bar");
                verifyNoMoreInteractions(function, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ParseException {
                CheckedToIntBiFunction<String, String, IOException> function = Spied.checkedToIntBiFunction((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                CheckedToIntBiFunction<String, String, ParseException> fallback = Spied
                        .checkedToIntBiFunction((s1, s2) -> throwable.throwUnchecked(s1 + s2));

                CheckedToIntBiFunction<String, String, ParseException> applying = function.onErrorApplyChecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.applyAsInt("foo", "bar"));
                assertEquals("foobar", thrown.getMessage());

                verify(function).applyAsInt("foo", "bar");
                verify(function).onErrorApplyChecked(fallback);
                verify(fallback).applyAsInt("foo", "bar");
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            CheckedToIntBiFunction<String, String, IOException> function = Spied
                    .checkedToIntBiFunction((s1, s2) -> throwable.throwUnchecked(s1 + s2));

            CheckedToIntBiFunction<String, String, ParseException> fallback = Spied.checkedToIntBiFunction((s1, s2) -> 2 * s1.concat(s2).length());

            CheckedToIntBiFunction<String, String, ParseException> applying = function.onErrorApplyChecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.applyAsInt("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());

            verify(function).applyAsInt("foo", "bar");
            verify(function).onErrorApplyChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorApplyUnchecked {

        @Test
        void testNullArgument() {
            CheckedToIntBiFunction<String, String, IOException> function = Spied.checkedToIntBiFunction((s1, s2) -> s1.concat(s2).length());

            assertThrows(NullPointerException.class, () -> function.onErrorApplyUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedToIntBiFunction<String, String, IOException> function = Spied.checkedToIntBiFunction((s1, s2) -> s1.concat(s2).length());

            ToIntBiFunction<String, String> fallback = Spied.toIntBiFunction((s1, s2) -> 2 * s1.concat(s2).length());

            ToIntBiFunction<String, String> applying = function.onErrorApplyUnchecked(fallback);

            assertEquals(6, applying.applyAsInt("foo", "bar"));

            verify(function).applyAsInt("foo", "bar");
            verify(function).onErrorApplyUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                CheckedToIntBiFunction<String, String, IOException> function = Spied.checkedToIntBiFunction((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                ToIntBiFunction<String, String> fallback = Spied.toIntBiFunction((s1, s2) -> 2 * s1.concat(s2).length());

                ToIntBiFunction<String, String> applying = function.onErrorApplyUnchecked(fallback);

                assertEquals(12, applying.applyAsInt("foo", "bar"));

                verify(function).applyAsInt("foo", "bar");
                verify(function).onErrorApplyUnchecked(fallback);
                verify(fallback).applyAsInt("foo", "bar");
                verifyNoMoreInteractions(function, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                CheckedToIntBiFunction<String, String, IOException> function = Spied.checkedToIntBiFunction((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                ToIntBiFunction<String, String> fallback = Spied.toIntBiFunction((s1, s2) -> throwable.throwUnchecked(s1 + s2));

                ToIntBiFunction<String, String> applying = function.onErrorApplyUnchecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.applyAsInt("foo", "bar"));
                assertEquals("foobar", thrown.getMessage());

                verify(function).applyAsInt("foo", "bar");
                verify(function).onErrorApplyUnchecked(fallback);
                verify(fallback).applyAsInt("foo", "bar");
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            CheckedToIntBiFunction<String, String, IOException> function = Spied
                    .checkedToIntBiFunction((s1, s2) -> throwable.throwUnchecked(s1 + s2));

            ToIntBiFunction<String, String> fallback = Spied.toIntBiFunction((s1, s2) -> 2 * s1.concat(s2).length());

            ToIntBiFunction<String, String> applying = function.onErrorApplyUnchecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.applyAsInt("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());

            verify(function).applyAsInt("foo", "bar");
            verify(function).onErrorApplyUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorGetChecked {

        @Test
        void testNullArgument() {
            CheckedToIntBiFunction<String, String, IOException> function = Spied.checkedToIntBiFunction((s1, s2) -> s1.concat(s2).length());

            assertThrows(NullPointerException.class, () -> function.onErrorGetChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedToIntBiFunction<String, String, IOException> function = Spied.checkedToIntBiFunction((s1, s2) -> s1.concat(s2).length());

            CheckedIntSupplier<ExecutionException> fallback = Spied.checkedIntSupplier(() -> 0);

            CheckedToIntBiFunction<String, String, ExecutionException> getting = function.onErrorGetChecked(fallback);

            assertEquals(6, getting.applyAsInt("foo", "bar"));

            verify(function).applyAsInt("foo", "bar");
            verify(function).onErrorGetChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                CheckedToIntBiFunction<String, String, IOException> function = Spied.checkedToIntBiFunction((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                CheckedIntSupplier<ParseException> fallback = Spied.checkedIntSupplier(() -> 0);

                CheckedToIntBiFunction<String, String, ParseException> getting = function.onErrorGetChecked(fallback);

                assertEquals(0, getting.applyAsInt("foo", "bar"));

                verify(function).applyAsInt("foo", "bar");
                verify(function).onErrorGetChecked(fallback);
                verify(fallback).getAsInt();
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                CheckedToIntBiFunction<String, String, IOException> function = Spied.checkedToIntBiFunction((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                CheckedIntSupplier<ParseException> fallback = Spied.checkedIntSupplier(() -> {
                    throw new ParseException("bar", 0);
                });

                CheckedToIntBiFunction<String, String, ParseException> getting = function.onErrorGetChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> getting.applyAsInt("foo", "bar"));
                assertEquals("bar", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(function).applyAsInt("foo", "bar");
                verify(function).onErrorGetChecked(fallback);
                verify(fallback).getAsInt();
                verifyNoMoreInteractions(function, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ParseException {
                CheckedToIntBiFunction<String, String, IOException> function = Spied.checkedToIntBiFunction((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                CheckedIntSupplier<ParseException> fallback = Spied.checkedIntSupplier(() -> throwable.throwUnchecked("bar"));

                CheckedToIntBiFunction<String, String, ParseException> getting = function.onErrorGetChecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.applyAsInt("foo", "bar"));
                assertEquals("bar", thrown.getMessage());

                verify(function).applyAsInt("foo", "bar");
                verify(function).onErrorGetChecked(fallback);
                verify(fallback).getAsInt();
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            CheckedToIntBiFunction<String, String, IOException> function = Spied
                    .checkedToIntBiFunction((s1, s2) -> throwable.throwUnchecked(s1 + s2));

            CheckedIntSupplier<ParseException> fallback = Spied.checkedIntSupplier(() -> 0);

            CheckedToIntBiFunction<String, String, ParseException> getting = function.onErrorGetChecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.applyAsInt("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());

            verify(function).applyAsInt("foo", "bar");
            verify(function).onErrorGetChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorGetUnchecked {

        @Test
        void testNullArgument() {
            CheckedToIntBiFunction<String, String, IOException> function = Spied.checkedToIntBiFunction((s1, s2) -> s1.concat(s2).length());

            assertThrows(NullPointerException.class, () -> function.onErrorGetUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedToIntBiFunction<String, String, IOException> function = Spied.checkedToIntBiFunction((s1, s2) -> s1.concat(s2).length());

            IntSupplier fallback = Spied.intSupplier(() -> 0);

            ToIntBiFunction<String, String> getting = function.onErrorGetUnchecked(fallback);

            assertEquals(6, getting.applyAsInt("foo", "bar"));

            verify(function).applyAsInt("foo", "bar");
            verify(function).onErrorGetUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                CheckedToIntBiFunction<String, String, IOException> function = Spied.checkedToIntBiFunction((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                IntSupplier fallback = Spied.intSupplier(() -> 0);

                ToIntBiFunction<String, String> getting = function.onErrorGetUnchecked(fallback);

                assertEquals(0, getting.applyAsInt("foo", "bar"));

                verify(function).applyAsInt("foo", "bar");
                verify(function).onErrorGetUnchecked(fallback);
                verify(fallback).getAsInt();
                verifyNoMoreInteractions(function, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                CheckedToIntBiFunction<String, String, IOException> function = Spied.checkedToIntBiFunction((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                IntSupplier fallback = Spied.intSupplier(() -> throwable.throwUnchecked("bar"));

                ToIntBiFunction<String, String> getting = function.onErrorGetUnchecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.applyAsInt("foo", "bar"));
                assertEquals("bar", thrown.getMessage());

                verify(function).applyAsInt("foo", "bar");
                verify(function).onErrorGetUnchecked(fallback);
                verify(fallback).getAsInt();
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            CheckedToIntBiFunction<String, String, IOException> function = Spied
                    .checkedToIntBiFunction((s1, s2) -> throwable.throwUnchecked(s1 + s2));

            IntSupplier fallback = Spied.intSupplier(() -> 0);

            ToIntBiFunction<String, String> getting = function.onErrorGetUnchecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.applyAsInt("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());

            verify(function).applyAsInt("foo", "bar");
            verify(function).onErrorGetUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorReturn {

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedToIntBiFunction<String, String, IOException> function = Spied.checkedToIntBiFunction((s1, s2) -> s1.concat(s2).length());

            ToIntBiFunction<String, String> returning = function.onErrorReturn(0);

            assertEquals(6, returning.applyAsInt("foo", "bar"));

            verify(function).applyAsInt("foo", "bar");
            verify(function).onErrorReturn(0);
            verifyNoMoreInteractions(function);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedToIntBiFunction<String, String, IOException> function = Spied.checkedToIntBiFunction((s1, s2) -> {
                throw new IOException(s1 + s2);
            });

            ToIntBiFunction<String, String> returning = function.onErrorReturn(0);

            assertEquals(0, returning.applyAsInt("foo", "bar"));

            verify(function).applyAsInt("foo", "bar");
            verify(function).onErrorReturn(0);
            verifyNoMoreInteractions(function);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            CheckedToIntBiFunction<String, String, IOException> function = Spied
                    .checkedToIntBiFunction((s1, s2) -> throwable.throwUnchecked(s1 + s2));

            ToIntBiFunction<String, String> returning = function.onErrorReturn(0);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> returning.applyAsInt("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());

            verify(function).applyAsInt("foo", "bar");
            verify(function).onErrorReturn(0);
            verifyNoMoreInteractions(function);
        }
    }

    @Nested
    class Unchecked {

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedToIntBiFunction<String, String, IOException> function = Spied.checkedToIntBiFunction((s1, s2) -> s1.concat(s2).length());

            ToIntBiFunction<String, String> unchecked = function.unchecked();

            assertEquals(6, unchecked.applyAsInt("foo", "bar"));

            verify(function).applyAsInt("foo", "bar");
            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(function);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedToIntBiFunction<String, String, IOException> function = Spied.checkedToIntBiFunction((s1, s2) -> {
                throw new IOException(s1 + s2);
            });

            ToIntBiFunction<String, String> unchecked = function.unchecked();

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.applyAsInt("foo", "bar"));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foobar", cause.getMessage());

            verify(function).applyAsInt("foo", "bar");
            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(function);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            CheckedToIntBiFunction<String, String, IOException> function = Spied
                    .checkedToIntBiFunction((s1, s2) -> throwable.throwUnchecked(s1 + s2));

            ToIntBiFunction<String, String> unchecked = function.unchecked();

            Throwable thrown = assertThrows(throwable.throwableType(), () -> unchecked.applyAsInt("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());

            verify(function).applyAsInt("foo", "bar");
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
            CheckedToIntBiFunction<String, String, IOException> function = CheckedToIntBiFunction.of((s1, s2) -> s1.concat(s2).length());

            assertEquals(6, function.applyAsInt("foo", "bar"));
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
            CheckedToIntBiFunction<String, String, IOException> function = Spied.checkedToIntBiFunction((s1, s2) -> s1.concat(s2).length());

            ToIntBiFunction<String, String> unchecked = CheckedToIntBiFunction.unchecked(function);

            assertEquals(6, unchecked.applyAsInt("foo", "bar"));

            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verify(function).applyAsInt("foo", "bar");
            verifyNoMoreInteractions(function);
        }

        @Test
        void testArgumentThrowsChecked() throws IOException {
            CheckedToIntBiFunction<String, String, IOException> function = Spied.checkedToIntBiFunction((s1, s2) -> {
                throw new IOException(s1 + s2);
            });

            ToIntBiFunction<String, String> unchecked = CheckedToIntBiFunction.unchecked(function);

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.applyAsInt("foo", "bar"));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foobar", cause.getMessage());

            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verify(function).applyAsInt("foo", "bar");
            verifyNoMoreInteractions(function);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testArgumentThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            CheckedToIntBiFunction<String, String, IOException> function = Spied
                    .checkedToIntBiFunction((s1, s2) -> throwable.throwUnchecked(s1 + s2));

            ToIntBiFunction<String, String> unchecked = CheckedToIntBiFunction.unchecked(function);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> unchecked.applyAsInt("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());

            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verify(function).applyAsInt("foo", "bar");
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
            CheckedToIntBiFunction<String, String, IOException> function = CheckedToIntBiFunction.checked((s1, s2) -> s1.concat(s2).length());

            assertEquals(6, function.applyAsInt("foo", "bar"));
        }

        @Test
        void testArgumentThrowsUnchecked() {
            CheckedToIntBiFunction<String, String, IOException> function = CheckedToIntBiFunction.checked((s1, s2) -> {
                throw new UncheckedException(s1 + s2, new IOException());
            });

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> function.applyAsInt("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertNull(cause.getMessage());
        }
    }

    @Nested
    class CheckedUnwrapper {

        @Test
        void testNullArguments() {
            ToIntBiFunction<String, String> function = (s1, s2) -> s1.concat(s2).length();

            assertThrows(NullPointerException.class, () -> CheckedToIntBiFunction.checked(null, IOException.class));
            assertThrows(NullPointerException.class, () -> CheckedToIntBiFunction.checked(function, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedToIntBiFunction<String, String, IOException> function = CheckedToIntBiFunction
                    .checked((s1, s2) -> s1.concat(s2).length(), IOException.class);

            assertEquals(6, function.applyAsInt("foo", "bar"));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                CheckedToIntBiFunction<String, String, IOException> function = CheckedToIntBiFunction.checked((s1, s2) -> {
                    throw new UncheckedException(new IOException(s1 + s2));
                }, IOException.class);

                IOException thrown = assertThrows(IOException.class, () -> function.applyAsInt("foo", "bar"));
                assertEquals("foobar", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                CheckedToIntBiFunction<String, String, IOException> function = CheckedToIntBiFunction.checked((s1, s2) -> {
                    throw new UncheckedException(new FileNotFoundException(s1 + s2));
                }, IOException.class);

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class, () -> function.applyAsInt("foo", "bar"));
                assertEquals("foobar", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                CheckedToIntBiFunction<String, String, IOException> function = CheckedToIntBiFunction.checked((s1, s2) -> {
                    throw new UncheckedException(new ParseException(s1 + s2, 0));
                }, IOException.class);

                UncheckedException thrown = assertThrows(UncheckedException.class, () -> function.applyAsInt("foo", "bar"));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("foobar", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            CheckedToIntBiFunction<String, String, IOException> function = CheckedToIntBiFunction.checked((s1, s2) -> {
                throw new IllegalStateException(s1 + s2);
            }, IOException.class);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> function.applyAsInt("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());
        }
    }

    @Nested
    class InvokeAndUnwrap {

        @Test
        void testNullArguments() throws IOException {
            ToIntBiFunction<String, String> function = (s1, s2) -> (s1 + s2).length();

            assertThrows(NullPointerException.class, () -> CheckedToIntBiFunction.invokeAndUnwrap(null, "foo", "bar", IOException.class));
            assertThrows(NullPointerException.class, () -> CheckedToIntBiFunction.invokeAndUnwrap(function, "foo", "bar", null));

            assertEquals(7, CheckedToIntBiFunction.invokeAndUnwrap(function, null, "bar", IOException.class));
            assertEquals(7, CheckedToIntBiFunction.invokeAndUnwrap(function, "foo", null, IOException.class));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ToIntBiFunction<String, String> function = (s1, s2) -> s1.concat(s2).length();

            assertEquals(6, CheckedToIntBiFunction.invokeAndUnwrap(function, "foo", "bar", IOException.class));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                ToIntBiFunction<String, String> function = (s1, s2) -> {
                    throw new UncheckedException(new IOException(s1 + s2));
                };

                IOException thrown = assertThrows(IOException.class,
                        () -> CheckedToIntBiFunction.invokeAndUnwrap(function, "foo", "bar", IOException.class));
                assertEquals("foobar", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                ToIntBiFunction<String, String> function = (s1, s2) -> {
                    throw new UncheckedException(new FileNotFoundException(s1 + s2));
                };

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class,
                        () -> CheckedToIntBiFunction.invokeAndUnwrap(function, "foo", "bar", IOException.class));
                assertEquals("foobar", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                ToIntBiFunction<String, String> function = (s1, s2) -> {
                    throw new UncheckedException(new ParseException(s1 + s2, 0));
                };

                UncheckedException thrown = assertThrows(UncheckedException.class,
                        () -> CheckedToIntBiFunction.invokeAndUnwrap(function, "foo", "bar", IOException.class));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("foobar", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            ToIntBiFunction<String, String> function = (s1, s2) -> {
                throw new IllegalStateException(s1 + s2);
            };

            IllegalStateException thrown = assertThrows(IllegalStateException.class,
                    () -> CheckedToIntBiFunction.invokeAndUnwrap(function, "foo", "bar", IOException.class));
            assertEquals("foobar", thrown.getMessage());
        }
    }
}
