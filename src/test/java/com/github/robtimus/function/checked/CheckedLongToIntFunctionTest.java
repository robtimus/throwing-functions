/*
 * CheckedLongToIntFunctionTest.java
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
import java.util.function.LongToIntFunction;
import java.util.function.ToIntFunction;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

@SuppressWarnings("nls")
class CheckedLongToIntFunctionTest {

    @Nested
    class OnErrorThrowAsChecked {

        @Test
        void testNullArgument() {
            CheckedLongToIntFunction<IOException> function = Spied.checkedLongToIntFunction(l -> (int) l + 1);

            assertThrows(NullPointerException.class, () -> function.onErrorThrowAsChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedLongToIntFunction<IOException> function = Spied.checkedLongToIntFunction(l -> (int) l + 1);

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedLongToIntFunction<ExecutionException> throwing = function.onErrorThrowAsChecked(errorMapper);

            assertEquals(2, throwing.applyAsInt(1L));

            verify(function).applyAsInt(1L);
            verify(function).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedLongToIntFunction<IOException> function = Spied.checkedLongToIntFunction(l -> {
                throw new IOException(Long.toString(l));
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedLongToIntFunction<ExecutionException> throwing = function.onErrorThrowAsChecked(errorMapper);

            ExecutionException thrown = assertThrows(ExecutionException.class, () -> throwing.applyAsInt(1L));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1", cause.getMessage());

            verify(function).applyAsInt(1L);
            verify(function).onErrorThrowAsChecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(function, errorMapper);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            CheckedLongToIntFunction<IOException> function = Spied.checkedLongToIntFunction(throwable::throwUnchecked);

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedLongToIntFunction<ExecutionException> throwing = function.onErrorThrowAsChecked(errorMapper);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> throwing.applyAsInt(1L));
            assertEquals("1", thrown.getMessage());

            verify(function).applyAsInt(1L);
            verify(function).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }
    }

    @Nested
    class OnErrorThrowAsUnchecked {

        @Test
        void testNullArgument() {
            CheckedLongToIntFunction<IOException> function = Spied.checkedLongToIntFunction(l -> (int) l + 1);

            assertThrows(NullPointerException.class, () -> function.onErrorThrowAsUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedLongToIntFunction<IOException> function = Spied.checkedLongToIntFunction(l -> (int) l + 1);

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            LongToIntFunction throwing = function.onErrorThrowAsUnchecked(errorMapper);

            assertEquals(2, throwing.applyAsInt(1L));

            verify(function).applyAsInt(1L);
            verify(function).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedLongToIntFunction<IOException> function = Spied.checkedLongToIntFunction(l -> {
                throw new IOException(Long.toString(l));
            });

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            LongToIntFunction throwing = function.onErrorThrowAsUnchecked(errorMapper);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> throwing.applyAsInt(1L));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1", cause.getMessage());

            verify(function).applyAsInt(1L);
            verify(function).onErrorThrowAsUnchecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(function, errorMapper);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            CheckedLongToIntFunction<IOException> function = Spied.checkedLongToIntFunction(throwable::throwUnchecked);

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            LongToIntFunction throwing = function.onErrorThrowAsUnchecked(errorMapper);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> throwing.applyAsInt(1L));
            assertEquals("1", thrown.getMessage());

            verify(function).applyAsInt(1L);
            verify(function).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(function, errorMapper);
        }
    }

    @Nested
    class OnErrorHandleChecked {

        @Test
        void testNullArgument() {
            CheckedLongToIntFunction<IOException> function = Spied.checkedLongToIntFunction(l -> (int) l + 1);

            assertThrows(NullPointerException.class, () -> function.onErrorHandleChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedLongToIntFunction<IOException> function = Spied.checkedLongToIntFunction(l -> (int) l + 1);

            CheckedToIntFunction<IOException, ExecutionException> errorHandler = Spied.checkedToIntFunction(e -> 0);

            CheckedLongToIntFunction<ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

            assertEquals(2, handling.applyAsInt(1L));

            verify(function).applyAsInt(1L);
            verify(function).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException, ExecutionException {
                CheckedLongToIntFunction<IOException> function = Spied.checkedLongToIntFunction(l -> {
                    throw new IOException(Long.toString(l));
                });

                CheckedToIntFunction<IOException, ExecutionException> errorHandler = Spied.checkedToIntFunction(e -> 0);

                CheckedLongToIntFunction<ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

                assertEquals(0, handling.applyAsInt(1L));

                verify(function).applyAsInt(1L);
                verify(function).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsInt(any());
                verifyNoMoreInteractions(function, errorHandler);
            }

            @Test
            void testHandlerThrowsChecked() throws IOException, ExecutionException {
                CheckedLongToIntFunction<IOException> function = Spied.checkedLongToIntFunction(l -> {
                    throw new IOException(Long.toString(l));
                });

                CheckedToIntFunction<IOException, ExecutionException> errorHandler = Spied.checkedToIntFunction(e -> {
                    throw new ExecutionException(e);
                });

                CheckedLongToIntFunction<ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

                ExecutionException thrown = assertThrows(ExecutionException.class, () -> handling.applyAsInt(1L));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("1", cause.getMessage());

                verify(function).applyAsInt(1L);
                verify(function).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsInt(any());
                verifyNoMoreInteractions(function, errorHandler);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testHandlerThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ExecutionException {
                CheckedLongToIntFunction<IOException> function = Spied.checkedLongToIntFunction(l -> {
                    throw new IOException(Long.toString(l));
                });

                CheckedToIntFunction<IOException, ExecutionException> errorHandler = Spied.checkedToIntFunction(throwable::throwUnchecked);

                CheckedLongToIntFunction<ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.applyAsInt(1L));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("1", cause.getMessage());

                verify(function).applyAsInt(1L);
                verify(function).onErrorHandleChecked(errorHandler);
                verify(errorHandler).applyAsInt(any());
                verifyNoMoreInteractions(function, errorHandler);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            CheckedLongToIntFunction<IOException> function = Spied.checkedLongToIntFunction(throwable::throwUnchecked);

            CheckedToIntFunction<IOException, ExecutionException> errorHandler = Spied.checkedToIntFunction(e -> 0);

            CheckedLongToIntFunction<ExecutionException> handling = function.onErrorHandleChecked(errorHandler);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.applyAsInt(1L));
            assertEquals("1", thrown.getMessage());

            verify(function).applyAsInt(1L);
            verify(function).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }
    }

    @Nested
    class OnErrorHandleUnchecked {

        @Test
        void testNullArgument() {
            CheckedLongToIntFunction<IOException> function = Spied.checkedLongToIntFunction(l -> (int) l + 1);

            assertThrows(NullPointerException.class, () -> function.onErrorHandleUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedLongToIntFunction<IOException> function = Spied.checkedLongToIntFunction(l -> (int) l + 1);

            ToIntFunction<IOException> errorHandler = Spied.toIntFunction(e -> 0);

            LongToIntFunction handling = function.onErrorHandleUnchecked(errorHandler);

            assertEquals(2, handling.applyAsInt(1L));

            verify(function).applyAsInt(1L);
            verify(function).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException {
                CheckedLongToIntFunction<IOException> function = Spied.checkedLongToIntFunction(l -> {
                    throw new IOException(Long.toString(l));
                });

                ToIntFunction<IOException> errorHandler = Spied.toIntFunction(e -> 0);

                LongToIntFunction handling = function.onErrorHandleUnchecked(errorHandler);

                assertEquals(0, handling.applyAsInt(1L));

                verify(function).applyAsInt(1L);
                verify(function).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).applyAsInt(any());
                verifyNoMoreInteractions(function, errorHandler);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testHandlerThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                CheckedLongToIntFunction<IOException> function = Spied.checkedLongToIntFunction(l -> {
                    throw new IOException(Long.toString(l));
                });

                ToIntFunction<IOException> errorHandler = Spied.toIntFunction(throwable::throwUnchecked);

                LongToIntFunction handling = function.onErrorHandleUnchecked(errorHandler);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.applyAsInt(1L));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("1", cause.getMessage());

                verify(function).applyAsInt(1L);
                verify(function).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).applyAsInt(any());
                verifyNoMoreInteractions(function, errorHandler);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            CheckedLongToIntFunction<IOException> function = Spied.checkedLongToIntFunction(throwable::throwUnchecked);

            ToIntFunction<IOException> errorHandler = Spied.toIntFunction(e -> 0);

            LongToIntFunction handling = function.onErrorHandleUnchecked(errorHandler);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.applyAsInt(1L));
            assertEquals("1", thrown.getMessage());

            verify(function).applyAsInt(1L);
            verify(function).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(function, errorHandler);
        }
    }

    @Nested
    class OnErrorApplyChecked {

        @Test
        void testNullArgument() {
            CheckedLongToIntFunction<IOException> function = Spied.checkedLongToIntFunction(l -> (int) l + 1);

            assertThrows(NullPointerException.class, () -> function.onErrorApplyChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedLongToIntFunction<IOException> function = Spied.checkedLongToIntFunction(l -> (int) l + 1);

            CheckedLongToIntFunction<ExecutionException> fallback = Spied.checkedLongToIntFunction(l -> (int) l * 3);

            CheckedLongToIntFunction<ExecutionException> applying = function.onErrorApplyChecked(fallback);

            assertEquals(2, applying.applyAsInt(1L));

            verify(function).applyAsInt(1L);
            verify(function).onErrorApplyChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                CheckedLongToIntFunction<IOException> function = Spied.checkedLongToIntFunction(l -> {
                    throw new IOException(Long.toString(l));
                });

                CheckedLongToIntFunction<ParseException> fallback = Spied.checkedLongToIntFunction(l -> (int) l * 3);

                CheckedLongToIntFunction<ParseException> applying = function.onErrorApplyChecked(fallback);

                assertEquals(3, applying.applyAsInt(1L));

                verify(function).applyAsInt(1L);
                verify(function).onErrorApplyChecked(fallback);
                verify(fallback).applyAsInt(1L);
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                CheckedLongToIntFunction<IOException> function = Spied.checkedLongToIntFunction(l -> {
                    throw new IOException(Long.toString(l));
                });

                CheckedLongToIntFunction<ParseException> fallback = Spied.checkedLongToIntFunction(l -> {
                    throw new ParseException(Long.toString(l), 0);
                });

                CheckedLongToIntFunction<ParseException> applying = function.onErrorApplyChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> applying.applyAsInt(1L));
                assertEquals("1", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(function).applyAsInt(1L);
                verify(function).onErrorApplyChecked(fallback);
                verify(fallback).applyAsInt(1L);
                verifyNoMoreInteractions(function, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ParseException {
                CheckedLongToIntFunction<IOException> function = Spied.checkedLongToIntFunction(l -> {
                    throw new IOException(Long.toString(l));
                });

                CheckedLongToIntFunction<ParseException> fallback = Spied.checkedLongToIntFunction(throwable::throwUnchecked);

                CheckedLongToIntFunction<ParseException> applying = function.onErrorApplyChecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.applyAsInt(1L));
                assertEquals("1", thrown.getMessage());

                verify(function).applyAsInt(1L);
                verify(function).onErrorApplyChecked(fallback);
                verify(fallback).applyAsInt(1L);
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            CheckedLongToIntFunction<IOException> function = Spied.checkedLongToIntFunction(throwable::throwUnchecked);

            CheckedLongToIntFunction<ParseException> fallback = Spied.checkedLongToIntFunction(l -> (int) l * 3);

            CheckedLongToIntFunction<ParseException> applying = function.onErrorApplyChecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.applyAsInt(1L));
            assertEquals("1", thrown.getMessage());

            verify(function).applyAsInt(1L);
            verify(function).onErrorApplyChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorApplyUnchecked {

        @Test
        void testNullArgument() {
            CheckedLongToIntFunction<IOException> function = Spied.checkedLongToIntFunction(l -> (int) l + 1);

            assertThrows(NullPointerException.class, () -> function.onErrorApplyUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedLongToIntFunction<IOException> function = Spied.checkedLongToIntFunction(l -> (int) l + 1);

            LongToIntFunction fallback = Spied.longToIntFunction(l -> (int) l * 3);

            LongToIntFunction applying = function.onErrorApplyUnchecked(fallback);

            assertEquals(2, applying.applyAsInt(1L));

            verify(function).applyAsInt(1L);
            verify(function).onErrorApplyUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                CheckedLongToIntFunction<IOException> function = Spied.checkedLongToIntFunction(l -> {
                    throw new IOException(Long.toString(l));
                });

                LongToIntFunction fallback = Spied.longToIntFunction(l -> (int) l * 3);

                LongToIntFunction applying = function.onErrorApplyUnchecked(fallback);

                assertEquals(3, applying.applyAsInt(1L));

                verify(function).applyAsInt(1L);
                verify(function).onErrorApplyUnchecked(fallback);
                verify(fallback).applyAsInt(1L);
                verifyNoMoreInteractions(function, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                CheckedLongToIntFunction<IOException> function = Spied.checkedLongToIntFunction(l -> {
                    throw new IOException(Long.toString(l));
                });

                LongToIntFunction fallback = Spied.longToIntFunction(throwable::throwUnchecked);

                LongToIntFunction applying = function.onErrorApplyUnchecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.applyAsInt(1L));
                assertEquals("1", thrown.getMessage());

                verify(function).applyAsInt(1L);
                verify(function).onErrorApplyUnchecked(fallback);
                verify(fallback).applyAsInt(1L);
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            CheckedLongToIntFunction<IOException> function = Spied.checkedLongToIntFunction(throwable::throwUnchecked);

            LongToIntFunction fallback = Spied.longToIntFunction(l -> (int) l * 3);

            LongToIntFunction applying = function.onErrorApplyUnchecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> applying.applyAsInt(1L));
            assertEquals("1", thrown.getMessage());

            verify(function).applyAsInt(1L);
            verify(function).onErrorApplyUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorGetChecked {

        @Test
        void testNullArgument() {
            CheckedLongToIntFunction<IOException> function = Spied.checkedLongToIntFunction(l -> (int) l + 1);

            assertThrows(NullPointerException.class, () -> function.onErrorGetChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedLongToIntFunction<IOException> function = Spied.checkedLongToIntFunction(l -> (int) l + 1);

            CheckedIntSupplier<ExecutionException> fallback = Spied.checkedIntSupplier(() -> 0);

            CheckedLongToIntFunction<ExecutionException> getting = function.onErrorGetChecked(fallback);

            assertEquals(2, getting.applyAsInt(1L));

            verify(function).applyAsInt(1L);
            verify(function).onErrorGetChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                CheckedLongToIntFunction<IOException> function = Spied.checkedLongToIntFunction(l -> {
                    throw new IOException(Long.toString(l));
                });

                CheckedIntSupplier<ParseException> fallback = Spied.checkedIntSupplier(() -> 0);

                CheckedLongToIntFunction<ParseException> getting = function.onErrorGetChecked(fallback);

                assertEquals(0, getting.applyAsInt(1L));

                verify(function).applyAsInt(1L);
                verify(function).onErrorGetChecked(fallback);
                verify(fallback).getAsInt();
                verifyNoMoreInteractions(function, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                CheckedLongToIntFunction<IOException> function = Spied.checkedLongToIntFunction(l -> {
                    throw new IOException(Long.toString(l));
                });

                CheckedIntSupplier<ParseException> fallback = Spied.checkedIntSupplier(() -> {
                    throw new ParseException("bar", 0);
                });

                CheckedLongToIntFunction<ParseException> getting = function.onErrorGetChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> getting.applyAsInt(1L));
                assertEquals("bar", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(function).applyAsInt(1L);
                verify(function).onErrorGetChecked(fallback);
                verify(fallback).getAsInt();
                verifyNoMoreInteractions(function, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ParseException {
                CheckedLongToIntFunction<IOException> function = Spied.checkedLongToIntFunction(l -> {
                    throw new IOException(Long.toString(l));
                });

                CheckedIntSupplier<ParseException> fallback = Spied.checkedIntSupplier(() -> throwable.throwUnchecked("bar"));

                CheckedLongToIntFunction<ParseException> getting = function.onErrorGetChecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.applyAsInt(1L));
                assertEquals("bar", thrown.getMessage());

                verify(function).applyAsInt(1L);
                verify(function).onErrorGetChecked(fallback);
                verify(fallback).getAsInt();
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            CheckedLongToIntFunction<IOException> function = Spied.checkedLongToIntFunction(throwable::throwUnchecked);

            CheckedIntSupplier<ParseException> fallback = Spied.checkedIntSupplier(() -> 0);

            CheckedLongToIntFunction<ParseException> getting = function.onErrorGetChecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.applyAsInt(1L));
            assertEquals("1", thrown.getMessage());

            verify(function).applyAsInt(1L);
            verify(function).onErrorGetChecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorGetUnchecked {

        @Test
        void testNullArgument() {
            CheckedLongToIntFunction<IOException> function = Spied.checkedLongToIntFunction(l -> (int) l + 1);

            assertThrows(NullPointerException.class, () -> function.onErrorGetUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedLongToIntFunction<IOException> function = Spied.checkedLongToIntFunction(l -> (int) l + 1);

            IntSupplier fallback = Spied.intSupplier(() -> 0);

            LongToIntFunction getting = function.onErrorGetUnchecked(fallback);

            assertEquals(2, getting.applyAsInt(1L));

            verify(function).applyAsInt(1L);
            verify(function).onErrorGetUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                CheckedLongToIntFunction<IOException> function = Spied.checkedLongToIntFunction(l -> {
                    throw new IOException(Long.toString(l));
                });

                IntSupplier fallback = Spied.intSupplier(() -> 0);

                LongToIntFunction getting = function.onErrorGetUnchecked(fallback);

                assertEquals(0, getting.applyAsInt(1L));

                verify(function).applyAsInt(1L);
                verify(function).onErrorGetUnchecked(fallback);
                verify(fallback).getAsInt();
                verifyNoMoreInteractions(function, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                CheckedLongToIntFunction<IOException> function = Spied.checkedLongToIntFunction(l -> {
                    throw new IOException(Long.toString(l));
                });

                IntSupplier fallback = Spied.intSupplier(() -> throwable.throwUnchecked("bar"));

                LongToIntFunction getting = function.onErrorGetUnchecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.applyAsInt(1L));
                assertEquals("bar", thrown.getMessage());

                verify(function).applyAsInt(1L);
                verify(function).onErrorGetUnchecked(fallback);
                verify(fallback).getAsInt();
                verifyNoMoreInteractions(function, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            CheckedLongToIntFunction<IOException> function = Spied.checkedLongToIntFunction(throwable::throwUnchecked);

            IntSupplier fallback = Spied.intSupplier(() -> 0);

            LongToIntFunction getting = function.onErrorGetUnchecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.applyAsInt(1L));
            assertEquals("1", thrown.getMessage());

            verify(function).applyAsInt(1L);
            verify(function).onErrorGetUnchecked(fallback);
            verifyNoMoreInteractions(function, fallback);
        }
    }

    @Nested
    class OnErrorReturn {

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedLongToIntFunction<IOException> function = Spied.checkedLongToIntFunction(l -> (int) l + 1);

            LongToIntFunction returning = function.onErrorReturn(0);

            assertEquals(2, returning.applyAsInt(1L));

            verify(function).applyAsInt(1L);
            verify(function).onErrorReturn(0);
            verifyNoMoreInteractions(function);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedLongToIntFunction<IOException> function = Spied.checkedLongToIntFunction(l -> {
                throw new IOException(Long.toString(l));
            });

            LongToIntFunction returning = function.onErrorReturn(0);

            assertEquals(0, returning.applyAsInt(1L));

            verify(function).applyAsInt(1L);
            verify(function).onErrorReturn(0);
            verifyNoMoreInteractions(function);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            CheckedLongToIntFunction<IOException> function = Spied.checkedLongToIntFunction(throwable::throwUnchecked);

            LongToIntFunction returning = function.onErrorReturn(0);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> returning.applyAsInt(1L));
            assertEquals("1", thrown.getMessage());

            verify(function).applyAsInt(1L);
            verify(function).onErrorReturn(0);
            verifyNoMoreInteractions(function);
        }
    }

    @Nested
    class Unchecked {

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedLongToIntFunction<IOException> function = Spied.checkedLongToIntFunction(l -> (int) l + 1);

            LongToIntFunction unchecked = function.unchecked();

            assertEquals(2, unchecked.applyAsInt(1L));

            verify(function).applyAsInt(1L);
            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(function);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedLongToIntFunction<IOException> function = Spied.checkedLongToIntFunction(l -> {
                throw new IOException(Long.toString(l));
            });

            LongToIntFunction unchecked = function.unchecked();

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.applyAsInt(1L));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1", cause.getMessage());

            verify(function).applyAsInt(1L);
            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(function);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            CheckedLongToIntFunction<IOException> function = Spied.checkedLongToIntFunction(throwable::throwUnchecked);

            LongToIntFunction unchecked = function.unchecked();

            Throwable thrown = assertThrows(throwable.throwableType(), () -> unchecked.applyAsInt(1L));
            assertEquals("1", thrown.getMessage());

            verify(function).applyAsInt(1L);
            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(function);
        }
    }

    @Nested
    class Of {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> CheckedLongToIntFunction.of(null));
        }

        @Test
        void testNonNullArgument() throws IOException {
            CheckedLongToIntFunction<IOException> function = CheckedLongToIntFunction.of(l -> (int) l + 1);

            assertEquals(2, function.applyAsInt(1L));
        }
    }

    @Nested
    class UncheckedFactory {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> CheckedLongToIntFunction.unchecked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedLongToIntFunction<IOException> function = Spied.checkedLongToIntFunction(l -> (int) l + 1);

            LongToIntFunction unchecked = CheckedLongToIntFunction.unchecked(function);

            assertEquals(2, unchecked.applyAsInt(1L));

            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verify(function).applyAsInt(1L);
            verifyNoMoreInteractions(function);
        }

        @Test
        void testArgumentThrowsChecked() throws IOException {
            CheckedLongToIntFunction<IOException> function = Spied.checkedLongToIntFunction(l -> {
                throw new IOException(Long.toString(l));
            });

            LongToIntFunction unchecked = CheckedLongToIntFunction.unchecked(function);

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.applyAsInt(1L));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1", cause.getMessage());

            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verify(function).applyAsInt(1L);
            verifyNoMoreInteractions(function);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testArgumentThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            CheckedLongToIntFunction<IOException> function = Spied.checkedLongToIntFunction(throwable::throwUnchecked);

            LongToIntFunction unchecked = CheckedLongToIntFunction.unchecked(function);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> unchecked.applyAsInt(1L));
            assertEquals("1", thrown.getMessage());

            verify(function).unchecked();
            verify(function).onErrorThrowAsUnchecked(any());
            verify(function).applyAsInt(1L);
            verifyNoMoreInteractions(function);
        }
    }

    @Nested
    class CheckedWrapper {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> CheckedLongToIntFunction.checked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedLongToIntFunction<IOException> function = CheckedLongToIntFunction.checked(l -> (int) l + 1);

            assertEquals(2, function.applyAsInt(1L));
        }

        @Test
        void testArgumentThrowsUnchecked() {
            CheckedLongToIntFunction<IOException> function = CheckedLongToIntFunction.checked(l -> {
                throw new UncheckedException(Long.toString(l), new IOException());
            });

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> function.applyAsInt(1L));
            assertEquals("1", thrown.getMessage());
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertNull(cause.getMessage());
        }
    }

    @Nested
    class CheckedUnwrapper {

        @Test
        void testNullArguments() {
            LongToIntFunction function = l -> (int) l + 1;

            assertThrows(NullPointerException.class, () -> CheckedLongToIntFunction.checked(null, IOException.class));
            assertThrows(NullPointerException.class, () -> CheckedLongToIntFunction.checked(function, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedLongToIntFunction<IOException> function = CheckedLongToIntFunction.checked(l -> (int) l + 1, IOException.class);

            assertEquals(2, function.applyAsInt(1L));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                CheckedLongToIntFunction<IOException> function = CheckedLongToIntFunction.checked(l -> {
                    throw new UncheckedException(new IOException(Long.toString(l)));
                }, IOException.class);

                IOException thrown = assertThrows(IOException.class, () -> function.applyAsInt(1L));
                assertEquals("1", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                CheckedLongToIntFunction<IOException> function = CheckedLongToIntFunction.checked(l -> {
                    throw new UncheckedException(new FileNotFoundException(Long.toString(l)));
                }, IOException.class);

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class, () -> function.applyAsInt(1L));
                assertEquals("1", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                CheckedLongToIntFunction<IOException> function = CheckedLongToIntFunction.checked(l -> {
                    throw new UncheckedException(new ParseException(Long.toString(l), 0));
                }, IOException.class);

                UncheckedException thrown = assertThrows(UncheckedException.class, () -> function.applyAsInt(1L));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("1", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            CheckedLongToIntFunction<IOException> function = CheckedLongToIntFunction.checked(l -> {
                throw new IllegalStateException(Long.toString(l));
            }, IOException.class);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> function.applyAsInt(1L));
            assertEquals("1", thrown.getMessage());
        }
    }

    @Nested
    class InvokeAndUnwrap {

        @Test
        void testNullArguments() {
            LongToIntFunction function = l -> String.valueOf(Long.toString(l)).length();

            assertThrows(NullPointerException.class, () -> CheckedLongToIntFunction.invokeAndUnwrap(null, 1L, IOException.class));
            assertThrows(NullPointerException.class, () -> CheckedLongToIntFunction.invokeAndUnwrap(function, 1L, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            LongToIntFunction function = l -> (int) l + 1;

            assertEquals(2, CheckedLongToIntFunction.invokeAndUnwrap(function, 1L, IOException.class));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                LongToIntFunction function = l -> {
                    throw new UncheckedException(new IOException(Long.toString(l)));
                };

                IOException thrown = assertThrows(IOException.class, () -> CheckedLongToIntFunction.invokeAndUnwrap(function, 1L, IOException.class));
                assertEquals("1", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                LongToIntFunction function = l -> {
                    throw new UncheckedException(new FileNotFoundException(Long.toString(l)));
                };

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class,
                        () -> CheckedLongToIntFunction.invokeAndUnwrap(function, 1L, IOException.class));
                assertEquals("1", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                LongToIntFunction function = l -> {
                    throw new UncheckedException(new ParseException(Long.toString(l), 0));
                };

                UncheckedException thrown = assertThrows(UncheckedException.class,
                        () -> CheckedLongToIntFunction.invokeAndUnwrap(function, 1L, IOException.class));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("1", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            LongToIntFunction function = l -> {
                throw new IllegalStateException(Long.toString(l));
            };

            IllegalStateException thrown = assertThrows(IllegalStateException.class,
                    () -> CheckedLongToIntFunction.invokeAndUnwrap(function, 1L, IOException.class));
            assertEquals("1", thrown.getMessage());
        }
    }
}
