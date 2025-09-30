/*
 * ThrowingIntPredicateTest.java
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

@SuppressWarnings("nls")
class ThrowingIntPredicateTest {

    @Nested
    class And {

        @Test
        void testNullArgument() {
            ThrowingIntPredicate<IOException> predicate = Spied.checkedIntPredicate(i -> i % 2 != 0);

            assertThrows(NullPointerException.class, () -> predicate.and(null));
        }

        @Nested
        class NeitherThrows {

            @Test
            void testThisMatches() throws IOException {
                ThrowingIntPredicate<IOException> predicate = Spied.checkedIntPredicate(i -> i % 2 != 0);
                ThrowingIntPredicate<IOException> other = Spied.checkedIntPredicate(i -> i % 2 == 0);

                ThrowingIntPredicate<IOException> composed = predicate.and(other);

                assertFalse(composed.test(1));

                verify(predicate).test(1);
                verify(predicate).and(other);
                verify(other).test(1);
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testOtherMatches() throws IOException {
                ThrowingIntPredicate<IOException> predicate = Spied.checkedIntPredicate(i -> i % 2 == 0);
                ThrowingIntPredicate<IOException> other = Spied.checkedIntPredicate(i -> i % 2 != 0);

                ThrowingIntPredicate<IOException> composed = predicate.and(other);

                assertFalse(composed.test(1));

                verify(predicate).test(1);
                verify(predicate).and(other);
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testBothMatch() throws IOException {
                ThrowingIntPredicate<IOException> predicate = Spied.checkedIntPredicate(i -> i % 2 != 0);
                ThrowingIntPredicate<IOException> other = Spied.checkedIntPredicate(i -> i > 0);

                ThrowingIntPredicate<IOException> composed = predicate.and(other);

                assertTrue(composed.test(1));

                verify(predicate).test(1);
                verify(predicate).and(other);
                verify(other).test(1);
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testNeitherMatches() throws IOException {
                ThrowingIntPredicate<IOException> predicate = Spied.checkedIntPredicate(i -> i % 2 != 0);
                ThrowingIntPredicate<IOException> other = Spied.checkedIntPredicate(i -> i > 0);

                ThrowingIntPredicate<IOException> composed = predicate.and(other);

                assertFalse(composed.test(0));

                verify(predicate).test(0);
                verify(predicate).and(other);
                verifyNoMoreInteractions(predicate, other);
            }
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testOtherMatches() throws IOException {
                ThrowingIntPredicate<IOException> predicate = Spied.checkedIntPredicate(i -> {
                    throw new IOException(Integer.toString(i));
                });
                ThrowingIntPredicate<IOException> other = Spied.checkedIntPredicate(i -> i % 2 != 0);

                ThrowingIntPredicate<IOException> composed = predicate.and(other);

                IOException thrown = assertThrows(IOException.class, () -> composed.test(1));
                assertEquals("1", thrown.getMessage());

                verify(predicate).test(1);
                verify(predicate).and(other);
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testOtherDoesNotMatch() throws IOException {
                ThrowingIntPredicate<IOException> predicate = Spied.checkedIntPredicate(i -> {
                    throw new IOException(Integer.toString(i));
                });
                ThrowingIntPredicate<IOException> other = Spied.checkedIntPredicate(i -> i % 2 == 0);

                ThrowingIntPredicate<IOException> composed = predicate.and(other);

                IOException thrown = assertThrows(IOException.class, () -> composed.test(1));
                assertEquals("1", thrown.getMessage());

                verify(predicate).test(1);
                verify(predicate).and(other);
                verifyNoMoreInteractions(predicate, other);
            }
        }

        @Nested
        class ThisThrowsUnchecked {

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testOtherMatches(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingIntPredicate<IOException> predicate = Spied.checkedIntPredicate(throwable::throwUnchecked);
                ThrowingIntPredicate<IOException> other = Spied.checkedIntPredicate(i -> i % 2 != 0);

                ThrowingIntPredicate<IOException> composed = predicate.and(other);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> composed.test(1));
                assertEquals("1", thrown.getMessage());

                verify(predicate).test(1);
                verify(predicate).and(other);
                verifyNoMoreInteractions(predicate, other);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testOtherDoesNotMatch(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingIntPredicate<IOException> predicate = Spied.checkedIntPredicate(throwable::throwUnchecked);
                ThrowingIntPredicate<IOException> other = Spied.checkedIntPredicate(i -> i % 2 == 0);

                ThrowingIntPredicate<IOException> composed = predicate.and(other);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> composed.test(1));
                assertEquals("1", thrown.getMessage());

                verify(predicate).test(1);
                verify(predicate).and(other);
                verifyNoMoreInteractions(predicate, other);
            }
        }

        @Nested
        class OtherThrowsChecked {

            @Test
            void testThisMatches() throws IOException {
                ThrowingIntPredicate<IOException> predicate = Spied.checkedIntPredicate(i -> i % 2 != 0);
                ThrowingIntPredicate<IOException> other = Spied.checkedIntPredicate(i -> {
                    throw new IOException(Integer.toString(i));
                });

                ThrowingIntPredicate<IOException> composed = predicate.and(other);

                IOException thrown = assertThrows(IOException.class, () -> composed.test(1));
                assertEquals("1", thrown.getMessage());

                verify(predicate).test(1);
                verify(predicate).and(other);
                verify(other).test(1);
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testThisDoesNotMatch() throws IOException {
                ThrowingIntPredicate<IOException> predicate = Spied.checkedIntPredicate(i -> i % 2 == 0);
                ThrowingIntPredicate<IOException> other = Spied.checkedIntPredicate(i -> {
                    throw new IOException(Integer.toString(i));
                });

                ThrowingIntPredicate<IOException> composed = predicate.and(other);

                assertFalse(composed.test(1));

                verify(predicate).test(1);
                verify(predicate).and(other);
                verifyNoMoreInteractions(predicate, other);
            }
        }

        @Nested
        class OtherThrowsUnchecked {

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testThisMatches(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingIntPredicate<IOException> predicate = Spied.checkedIntPredicate(i -> i % 2 != 0);
                ThrowingIntPredicate<IOException> other = Spied.checkedIntPredicate(throwable::throwUnchecked);

                ThrowingIntPredicate<IOException> composed = predicate.and(other);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> composed.test(1));
                assertEquals("1", thrown.getMessage());

                verify(predicate).test(1);
                verify(predicate).and(other);
                verify(other).test(1);
                verifyNoMoreInteractions(predicate, other);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testThisDoesNotMatch(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingIntPredicate<IOException> predicate = Spied.checkedIntPredicate(i -> i % 2 == 0);
                ThrowingIntPredicate<IOException> other = Spied.checkedIntPredicate(throwable::throwUnchecked);

                ThrowingIntPredicate<IOException> composed = predicate.and(other);

                assertFalse(composed.test(1));

                verify(predicate).test(1);
                verify(predicate).and(other);
                verifyNoMoreInteractions(predicate, other);
            }
        }
    }

    @Nested
    class Negate {

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingIntPredicate<IOException> predicate = Spied.checkedIntPredicate(i -> i % 2 != 0);

            ThrowingIntPredicate<IOException> negated = predicate.negate();

            assertFalse(negated.test(1));
            assertTrue(negated.test(0));

            verify(predicate).test(1);
            verify(predicate).test(0);
            verify(predicate).negate();
            verifyNoMoreInteractions(predicate);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingIntPredicate<IOException> predicate = Spied.checkedIntPredicate(i -> {
                throw new IOException(Integer.toString(i));
            });

            ThrowingIntPredicate<IOException> negated = predicate.negate();

            IOException thrown = assertThrows(IOException.class, () -> negated.test(1));
            assertEquals("1", thrown.getMessage());

            verify(predicate).test(1);
            verify(predicate).negate();
            verifyNoMoreInteractions(predicate);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingIntPredicate<IOException> predicate = Spied.checkedIntPredicate(throwable::throwUnchecked);

            ThrowingIntPredicate<IOException> negated = predicate.negate();

            Throwable thrown = assertThrows(throwable.throwableType(), () -> negated.test(1));
            assertEquals("1", thrown.getMessage());

            verify(predicate).test(1);
            verify(predicate).negate();
            verifyNoMoreInteractions(predicate);
        }
    }

    @Nested
    class Or {

        @Test
        void testNullArgument() {
            ThrowingIntPredicate<IOException> predicate = Spied.checkedIntPredicate(i -> i % 2 != 0);

            assertThrows(NullPointerException.class, () -> predicate.or(null));
        }

        @Nested
        class NeitherThrows {

            @Test
            void testThisMatches() throws IOException {
                ThrowingIntPredicate<IOException> predicate = Spied.checkedIntPredicate(i -> i % 2 != 0);
                ThrowingIntPredicate<IOException> other = Spied.checkedIntPredicate(i -> i % 2 == 0);

                ThrowingIntPredicate<IOException> composed = predicate.or(other);

                assertTrue(composed.test(1));

                verify(predicate).test(1);
                verify(predicate).or(other);
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testOtherMatches() throws IOException {
                ThrowingIntPredicate<IOException> predicate = Spied.checkedIntPredicate(i -> i % 2 == 0);
                ThrowingIntPredicate<IOException> other = Spied.checkedIntPredicate(i -> i % 2 != 0);

                ThrowingIntPredicate<IOException> composed = predicate.or(other);

                assertTrue(composed.test(1));

                verify(predicate).test(1);
                verify(predicate).or(other);
                verify(other).test(1);
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testBothMatch() throws IOException {
                ThrowingIntPredicate<IOException> predicate = Spied.checkedIntPredicate(i -> i % 2 != 0);
                ThrowingIntPredicate<IOException> other = Spied.checkedIntPredicate(i -> i > 0);

                ThrowingIntPredicate<IOException> composed = predicate.or(other);

                assertTrue(composed.test(1));

                verify(predicate).test(1);
                verify(predicate).or(other);
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testNeitherMatches() throws IOException {
                ThrowingIntPredicate<IOException> predicate = Spied.checkedIntPredicate(i -> i % 2 != 0);
                ThrowingIntPredicate<IOException> other = Spied.checkedIntPredicate(i -> i > 0);

                ThrowingIntPredicate<IOException> composed = predicate.or(other);

                assertFalse(composed.test(0));

                verify(predicate).test(0);
                verify(predicate).or(other);
                verify(other).test(0);
                verifyNoMoreInteractions(predicate, other);
            }
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testOtherMatches() throws IOException {
                ThrowingIntPredicate<IOException> predicate = Spied.checkedIntPredicate(i -> {
                    throw new IOException(Integer.toString(i));
                });
                ThrowingIntPredicate<IOException> other = Spied.checkedIntPredicate(i -> i % 2 != 0);

                ThrowingIntPredicate<IOException> composed = predicate.or(other);

                IOException thrown = assertThrows(IOException.class, () -> composed.test(1));
                assertEquals("1", thrown.getMessage());

                verify(predicate).test(1);
                verify(predicate).or(other);
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testOtherDoesNotMatch() throws IOException {
                ThrowingIntPredicate<IOException> predicate = Spied.checkedIntPredicate(i -> {
                    throw new IOException(Integer.toString(i));
                });
                ThrowingIntPredicate<IOException> other = Spied.checkedIntPredicate(i -> i % 2 == 0);

                ThrowingIntPredicate<IOException> composed = predicate.or(other);

                IOException thrown = assertThrows(IOException.class, () -> composed.test(1));
                assertEquals("1", thrown.getMessage());

                verify(predicate).test(1);
                verify(predicate).or(other);
                verifyNoMoreInteractions(predicate, other);
            }
        }

        @Nested
        class ThisThrowsUnchecked {

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testOtherMatches(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingIntPredicate<IOException> predicate = Spied.checkedIntPredicate(throwable::throwUnchecked);
                ThrowingIntPredicate<IOException> other = Spied.checkedIntPredicate(i -> i % 2 != 0);

                ThrowingIntPredicate<IOException> composed = predicate.or(other);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> composed.test(1));
                assertEquals("1", thrown.getMessage());

                verify(predicate).test(1);
                verify(predicate).or(other);
                verifyNoMoreInteractions(predicate, other);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testOtherDoesNotMatch(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingIntPredicate<IOException> predicate = Spied.checkedIntPredicate(throwable::throwUnchecked);
                ThrowingIntPredicate<IOException> other = Spied.checkedIntPredicate(i -> i % 2 == 0);

                ThrowingIntPredicate<IOException> composed = predicate.or(other);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> composed.test(1));
                assertEquals("1", thrown.getMessage());

                verify(predicate).test(1);
                verify(predicate).or(other);
                verifyNoMoreInteractions(predicate, other);
            }
        }

        @Nested
        class OtherThrowsChecked {

            @Test
            void testThisMatches() throws IOException {
                ThrowingIntPredicate<IOException> predicate = Spied.checkedIntPredicate(i -> i % 2 != 0);
                ThrowingIntPredicate<IOException> other = Spied.checkedIntPredicate(i -> {
                    throw new IOException(Integer.toString(i));
                });

                ThrowingIntPredicate<IOException> composed = predicate.or(other);

                assertTrue(composed.test(1));

                verify(predicate).test(1);
                verify(predicate).or(other);
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testThisDoesNotMatch() throws IOException {
                ThrowingIntPredicate<IOException> predicate = Spied.checkedIntPredicate(i -> i % 2 == 0);
                ThrowingIntPredicate<IOException> other = Spied.checkedIntPredicate(i -> {
                    throw new IOException(Integer.toString(i));
                });

                ThrowingIntPredicate<IOException> composed = predicate.or(other);

                IOException thrown = assertThrows(IOException.class, () -> composed.test(1));
                assertEquals("1", thrown.getMessage());

                verify(predicate).test(1);
                verify(predicate).or(other);
                verify(other).test(1);
                verifyNoMoreInteractions(predicate, other);
            }
        }

        @Nested
        class OtherThrowsUnchecked {

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testThisMatches(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingIntPredicate<IOException> predicate = Spied.checkedIntPredicate(i -> i % 2 != 0);
                ThrowingIntPredicate<IOException> other = Spied.checkedIntPredicate(throwable::throwUnchecked);

                ThrowingIntPredicate<IOException> composed = predicate.or(other);

                assertTrue(composed.test(1));

                verify(predicate).test(1);
                verify(predicate).or(other);
                verifyNoMoreInteractions(predicate, other);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testThisDoesNotMatch(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingIntPredicate<IOException> predicate = Spied.checkedIntPredicate(i -> i % 2 == 0);
                ThrowingIntPredicate<IOException> other = Spied.checkedIntPredicate(throwable::throwUnchecked);

                ThrowingIntPredicate<IOException> composed = predicate.or(other);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> composed.test(1));
                assertEquals("1", thrown.getMessage());

                verify(predicate).test(1);
                verify(predicate).or(other);
                verify(other).test(1);
                verifyNoMoreInteractions(predicate, other);
            }
        }
    }

    @Nested
    class OnErrorThrowAsChecked {

        @Test
        void testNullArgument() {
            ThrowingIntPredicate<IOException> predicate = Spied.checkedIntPredicate(i -> i % 2 != 0);

            assertThrows(NullPointerException.class, () -> predicate.onErrorThrowAsChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingIntPredicate<IOException> predicate = Spied.checkedIntPredicate(i -> i % 2 != 0);

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingIntPredicate<ExecutionException> throwing = predicate.onErrorThrowAsChecked(errorMapper);

            assertTrue(throwing.test(1));

            verify(predicate).test(1);
            verify(predicate).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(predicate, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingIntPredicate<IOException> predicate = Spied.checkedIntPredicate(i -> {
                throw new IOException(Integer.toString(i));
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingIntPredicate<ExecutionException> throwing = predicate.onErrorThrowAsChecked(errorMapper);

            ExecutionException thrown = assertThrows(ExecutionException.class, () -> throwing.test(1));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1", cause.getMessage());

            verify(predicate).test(1);
            verify(predicate).onErrorThrowAsChecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(predicate, errorMapper);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingIntPredicate<IOException> predicate = Spied.checkedIntPredicate(throwable::throwUnchecked);

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingIntPredicate<ExecutionException> throwing = predicate.onErrorThrowAsChecked(errorMapper);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> throwing.test(1));
            assertEquals("1", thrown.getMessage());

            verify(predicate).test(1);
            verify(predicate).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(predicate, errorMapper);
        }
    }

    @Nested
    class OnErrorThrowAsUnchecked {

        @Test
        void testNullArgument() {
            ThrowingIntPredicate<IOException> predicate = Spied.checkedIntPredicate(i -> i % 2 != 0);

            assertThrows(NullPointerException.class, () -> predicate.onErrorThrowAsUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingIntPredicate<IOException> predicate = Spied.checkedIntPredicate(i -> i % 2 != 0);

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            IntPredicate throwing = predicate.onErrorThrowAsUnchecked(errorMapper);

            assertTrue(throwing.test(1));

            verify(predicate).test(1);
            verify(predicate).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(predicate, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingIntPredicate<IOException> predicate = Spied.checkedIntPredicate(i -> {
                throw new IOException(Integer.toString(i));
            });

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            IntPredicate throwing = predicate.onErrorThrowAsUnchecked(errorMapper);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> throwing.test(1));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1", cause.getMessage());

            verify(predicate).test(1);
            verify(predicate).onErrorThrowAsUnchecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(predicate, errorMapper);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingIntPredicate<IOException> predicate = Spied.checkedIntPredicate(throwable::throwUnchecked);

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            IntPredicate throwing = predicate.onErrorThrowAsUnchecked(errorMapper);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> throwing.test(1));
            assertEquals("1", thrown.getMessage());

            verify(predicate).test(1);
            verify(predicate).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(predicate, errorMapper);
        }
    }

    @Nested
    class OnErrorHandleChecked {

        @Test
        void testNullArgument() {
            ThrowingIntPredicate<IOException> predicate = Spied.checkedIntPredicate(i -> i % 2 != 0);

            assertThrows(NullPointerException.class, () -> predicate.onErrorHandleChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingIntPredicate<IOException> predicate = Spied.checkedIntPredicate(i -> i % 2 != 0);

            ThrowingPredicate<IOException, ExecutionException> errorHandler = Spied.checkedPredicate(e -> e.getMessage() == null);

            ThrowingIntPredicate<ExecutionException> handling = predicate.onErrorHandleChecked(errorHandler);

            assertTrue(handling.test(1));

            verify(predicate).test(1);
            verify(predicate).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(predicate, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException, ExecutionException {
                ThrowingIntPredicate<IOException> predicate = Spied.checkedIntPredicate(i -> {
                    throw new IOException(Integer.toString(i));
                });

                ThrowingPredicate<IOException, ExecutionException> errorHandler = Spied.checkedPredicate(e -> e.getMessage() == null);

                ThrowingIntPredicate<ExecutionException> handling = predicate.onErrorHandleChecked(errorHandler);

                assertFalse(handling.test(1));

                verify(predicate).test(1);
                verify(predicate).onErrorHandleChecked(errorHandler);
                verify(errorHandler).test(any());
                verifyNoMoreInteractions(predicate, errorHandler);
            }

            @Test
            void testHandlerThrowsChecked() throws IOException, ExecutionException {
                ThrowingIntPredicate<IOException> predicate = Spied.checkedIntPredicate(i -> {
                    throw new IOException(Integer.toString(i));
                });

                ThrowingPredicate<IOException, ExecutionException> errorHandler = Spied.checkedPredicate(e -> {
                    throw new ExecutionException(e);
                });

                ThrowingIntPredicate<ExecutionException> handling = predicate.onErrorHandleChecked(errorHandler);

                ExecutionException thrown = assertThrows(ExecutionException.class, () -> handling.test(1));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("1", cause.getMessage());

                verify(predicate).test(1);
                verify(predicate).onErrorHandleChecked(errorHandler);
                verify(errorHandler).test(any());
                verifyNoMoreInteractions(predicate, errorHandler);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testHandlerThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ExecutionException {
                ThrowingIntPredicate<IOException> predicate = Spied.checkedIntPredicate(i -> {
                    throw new IOException(Integer.toString(i));
                });

                ThrowingPredicate<IOException, ExecutionException> errorHandler = Spied.checkedPredicate(throwable::throwUnchecked);

                ThrowingIntPredicate<ExecutionException> handling = predicate.onErrorHandleChecked(errorHandler);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.test(1));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("1", cause.getMessage());

                verify(predicate).test(1);
                verify(predicate).onErrorHandleChecked(errorHandler);
                verify(errorHandler).test(any());
                verifyNoMoreInteractions(predicate, errorHandler);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingIntPredicate<IOException> predicate = Spied.checkedIntPredicate(throwable::throwUnchecked);

            ThrowingPredicate<IOException, ExecutionException> errorHandler = Spied.checkedPredicate(e -> e.getMessage() == null);

            ThrowingIntPredicate<ExecutionException> handling = predicate.onErrorHandleChecked(errorHandler);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.test(1));
            assertEquals("1", thrown.getMessage());

            verify(predicate).test(1);
            verify(predicate).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(predicate, errorHandler);
        }
    }

    @Nested
    class OnErrorHandleUnchecked {

        @Test
        void testNullArgument() {
            ThrowingIntPredicate<IOException> predicate = Spied.checkedIntPredicate(i -> i % 2 != 0);

            assertThrows(NullPointerException.class, () -> predicate.onErrorHandleUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingIntPredicate<IOException> predicate = Spied.checkedIntPredicate(i -> i % 2 != 0);

            Predicate<IOException> errorHandler = Spied.predicate(e -> e.getMessage() == null);

            IntPredicate handling = predicate.onErrorHandleUnchecked(errorHandler);

            assertTrue(handling.test(1));

            verify(predicate).test(1);
            verify(predicate).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(predicate, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException {
                ThrowingIntPredicate<IOException> predicate = Spied.checkedIntPredicate(i -> {
                    throw new IOException(Integer.toString(i));
                });

                Predicate<IOException> errorHandler = Spied.predicate(e -> e.getMessage() == null);

                IntPredicate handling = predicate.onErrorHandleUnchecked(errorHandler);

                assertFalse(handling.test(1));

                verify(predicate).test(1);
                verify(predicate).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).test(any());
                verifyNoMoreInteractions(predicate, errorHandler);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testHandlerThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingIntPredicate<IOException> predicate = Spied.checkedIntPredicate(i -> {
                    throw new IOException(Integer.toString(i));
                });

                Predicate<IOException> errorHandler = Spied.predicate(throwable::throwUnchecked);

                IntPredicate handling = predicate.onErrorHandleUnchecked(errorHandler);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.test(1));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("1", cause.getMessage());

                verify(predicate).test(1);
                verify(predicate).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).test(any());
                verifyNoMoreInteractions(predicate, errorHandler);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingIntPredicate<IOException> predicate = Spied.checkedIntPredicate(throwable::throwUnchecked);

            Predicate<IOException> errorHandler = Spied.predicate(e -> e.getMessage() == null);

            IntPredicate handling = predicate.onErrorHandleUnchecked(errorHandler);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.test(1));
            assertEquals("1", thrown.getMessage());

            verify(predicate).test(1);
            verify(predicate).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(predicate, errorHandler);
        }
    }

    @Nested
    class OnErrorTestChecked {

        @Test
        void testNullArgument() {
            ThrowingIntPredicate<IOException> predicate = Spied.checkedIntPredicate(i -> i % 2 != 0);

            assertThrows(NullPointerException.class, () -> predicate.onErrorTestChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingIntPredicate<IOException> predicate = Spied.checkedIntPredicate(i -> i % 2 != 0);

            ThrowingIntPredicate<ExecutionException> fallback = Spied.checkedIntPredicate(i -> i % 2 == 0);

            ThrowingIntPredicate<ExecutionException> testing = predicate.onErrorTestChecked(fallback);

            assertTrue(testing.test(1));

            verify(predicate).test(1);
            verify(predicate).onErrorTestChecked(fallback);
            verifyNoMoreInteractions(predicate, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                ThrowingIntPredicate<IOException> predicate = Spied.checkedIntPredicate(i -> {
                    throw new IOException(Integer.toString(i));
                });

                ThrowingIntPredicate<ParseException> fallback = Spied.checkedIntPredicate(i -> i % 2 == 0);

                ThrowingIntPredicate<ParseException> testing = predicate.onErrorTestChecked(fallback);

                assertFalse(testing.test(1));

                verify(predicate).test(1);
                verify(predicate).onErrorTestChecked(fallback);
                verify(fallback).test(1);
                verifyNoMoreInteractions(predicate, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                ThrowingIntPredicate<IOException> predicate = Spied.checkedIntPredicate(i -> {
                    throw new IOException(Integer.toString(i));
                });

                ThrowingIntPredicate<ParseException> fallback = Spied.checkedIntPredicate(i -> {
                    throw new ParseException(Integer.toString(i), 0);
                });

                ThrowingIntPredicate<ParseException> testing = predicate.onErrorTestChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> testing.test(1));
                assertEquals("1", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(predicate).test(1);
                verify(predicate).onErrorTestChecked(fallback);
                verify(fallback).test(1);
                verifyNoMoreInteractions(predicate, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ParseException {
                ThrowingIntPredicate<IOException> predicate = Spied.checkedIntPredicate(i -> {
                    throw new IOException(Integer.toString(i));
                });

                ThrowingIntPredicate<ParseException> fallback = Spied.checkedIntPredicate(throwable::throwUnchecked);

                ThrowingIntPredicate<ParseException> testing = predicate.onErrorTestChecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> testing.test(1));
                assertEquals("1", thrown.getMessage());

                verify(predicate).test(1);
                verify(predicate).onErrorTestChecked(fallback);
                verify(fallback).test(1);
                verifyNoMoreInteractions(predicate, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingIntPredicate<IOException> predicate = Spied.checkedIntPredicate(throwable::throwUnchecked);

            ThrowingIntPredicate<ParseException> fallback = Spied.checkedIntPredicate(i -> i % 2 == 0);

            ThrowingIntPredicate<ParseException> testing = predicate.onErrorTestChecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> testing.test(1));
            assertEquals("1", thrown.getMessage());

            verify(predicate).test(1);
            verify(predicate).onErrorTestChecked(fallback);
            verifyNoMoreInteractions(predicate, fallback);
        }
    }

    @Nested
    class OnErrorTestUnchecked {

        @Test
        void testNullArgument() {
            ThrowingIntPredicate<IOException> predicate = Spied.checkedIntPredicate(i -> i % 2 != 0);

            assertThrows(NullPointerException.class, () -> predicate.onErrorTestUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingIntPredicate<IOException> predicate = Spied.checkedIntPredicate(i -> i % 2 != 0);

            IntPredicate fallback = Spied.intPredicate(i -> i % 2 == 0);

            IntPredicate testing = predicate.onErrorTestUnchecked(fallback);

            assertTrue(testing.test(1));

            verify(predicate).test(1);
            verify(predicate).onErrorTestUnchecked(fallback);
            verifyNoMoreInteractions(predicate, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                ThrowingIntPredicate<IOException> predicate = Spied.checkedIntPredicate(i -> {
                    throw new IOException(Integer.toString(i));
                });

                IntPredicate fallback = Spied.intPredicate(i -> i % 2 == 0);

                IntPredicate testing = predicate.onErrorTestUnchecked(fallback);

                assertFalse(testing.test(1));

                verify(predicate).test(1);
                verify(predicate).onErrorTestUnchecked(fallback);
                verify(fallback).test(1);
                verifyNoMoreInteractions(predicate, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingIntPredicate<IOException> predicate = Spied.checkedIntPredicate(i -> {
                    throw new IOException(Integer.toString(i));
                });

                IntPredicate fallback = Spied.intPredicate(throwable::throwUnchecked);

                IntPredicate testing = predicate.onErrorTestUnchecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> testing.test(1));
                assertEquals("1", thrown.getMessage());

                verify(predicate).test(1);
                verify(predicate).onErrorTestUnchecked(fallback);
                verify(fallback).test(1);
                verifyNoMoreInteractions(predicate, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingIntPredicate<IOException> predicate = Spied.checkedIntPredicate(throwable::throwUnchecked);

            IntPredicate fallback = Spied.intPredicate(i -> i % 2 == 0);

            IntPredicate testing = predicate.onErrorTestUnchecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> testing.test(1));
            assertEquals("1", thrown.getMessage());

            verify(predicate).test(1);
            verify(predicate).onErrorTestUnchecked(fallback);
            verifyNoMoreInteractions(predicate, fallback);
        }
    }

    @Nested
    class OnErrorGetCheckedAsBoolean {

        @Test
        void testNullArgument() {
            ThrowingIntPredicate<IOException> predicate = Spied.checkedIntPredicate(i -> i % 2 != 0);

            assertThrows(NullPointerException.class, () -> predicate.onErrorGetCheckedAsBoolean(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingIntPredicate<IOException> predicate = Spied.checkedIntPredicate(i -> i % 2 != 0);

            ThrowingBooleanSupplier<ExecutionException> fallback = Spied.checkedBooleanSupplier(() -> false);

            ThrowingIntPredicate<ExecutionException> getting = predicate.onErrorGetCheckedAsBoolean(fallback);

            assertTrue(getting.test(1));

            verify(predicate).test(1);
            verify(predicate).onErrorGetCheckedAsBoolean(fallback);
            verifyNoMoreInteractions(predicate, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                ThrowingIntPredicate<IOException> predicate = Spied.checkedIntPredicate(i -> {
                    throw new IOException(Integer.toString(i));
                });

                ThrowingBooleanSupplier<ParseException> fallback = Spied.checkedBooleanSupplier(() -> false);

                ThrowingIntPredicate<ParseException> getting = predicate.onErrorGetCheckedAsBoolean(fallback);

                assertFalse(getting.test(1));

                verify(predicate).test(1);
                verify(predicate).onErrorGetCheckedAsBoolean(fallback);
                verify(fallback).getAsBoolean();
                verifyNoMoreInteractions(predicate, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                ThrowingIntPredicate<IOException> predicate = Spied.checkedIntPredicate(i -> {
                    throw new IOException(Integer.toString(i));
                });

                ThrowingBooleanSupplier<ParseException> fallback = Spied.checkedBooleanSupplier(() -> {
                    throw new ParseException("bar", 0);
                });

                ThrowingIntPredicate<ParseException> getting = predicate.onErrorGetCheckedAsBoolean(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> getting.test(1));
                assertEquals("bar", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(predicate).test(1);
                verify(predicate).onErrorGetCheckedAsBoolean(fallback);
                verify(fallback).getAsBoolean();
                verifyNoMoreInteractions(predicate, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ParseException {
                ThrowingIntPredicate<IOException> predicate = Spied.checkedIntPredicate(i -> {
                    throw new IOException(Integer.toString(i));
                });

                ThrowingBooleanSupplier<ParseException> fallback = Spied.checkedBooleanSupplier(() -> throwable.throwUnchecked("bar"));

                ThrowingIntPredicate<ParseException> getting = predicate.onErrorGetCheckedAsBoolean(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.test(1));
                assertEquals("bar", thrown.getMessage());

                verify(predicate).test(1);
                verify(predicate).onErrorGetCheckedAsBoolean(fallback);
                verify(fallback).getAsBoolean();
                verifyNoMoreInteractions(predicate, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingIntPredicate<IOException> predicate = Spied.checkedIntPredicate(throwable::throwUnchecked);

            ThrowingBooleanSupplier<ParseException> fallback = Spied.checkedBooleanSupplier(() -> false);

            ThrowingIntPredicate<ParseException> getting = predicate.onErrorGetCheckedAsBoolean(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.test(1));
            assertEquals("1", thrown.getMessage());

            verify(predicate).test(1);
            verify(predicate).onErrorGetCheckedAsBoolean(fallback);
            verifyNoMoreInteractions(predicate, fallback);
        }
    }

    @Nested
    class OnErrorGetUncheckedAsBoolean {

        @Test
        void testNullArgument() {
            ThrowingIntPredicate<IOException> predicate = Spied.checkedIntPredicate(i -> i % 2 != 0);

            assertThrows(NullPointerException.class, () -> predicate.onErrorGetUncheckedAsBoolean(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingIntPredicate<IOException> predicate = Spied.checkedIntPredicate(i -> i % 2 != 0);

            BooleanSupplier fallback = Spied.booleanSupplier(() -> false);

            IntPredicate getting = predicate.onErrorGetUncheckedAsBoolean(fallback);

            assertTrue(getting.test(1));

            verify(predicate).test(1);
            verify(predicate).onErrorGetUncheckedAsBoolean(fallback);
            verifyNoMoreInteractions(predicate, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                ThrowingIntPredicate<IOException> predicate = Spied.checkedIntPredicate(i -> {
                    throw new IOException(Integer.toString(i));
                });

                BooleanSupplier fallback = Spied.booleanSupplier(() -> false);

                IntPredicate getting = predicate.onErrorGetUncheckedAsBoolean(fallback);

                assertFalse(getting.test(1));

                verify(predicate).test(1);
                verify(predicate).onErrorGetUncheckedAsBoolean(fallback);
                verify(fallback).getAsBoolean();
                verifyNoMoreInteractions(predicate, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingIntPredicate<IOException> predicate = Spied.checkedIntPredicate(i -> {
                    throw new IOException(Integer.toString(i));
                });

                BooleanSupplier fallback = Spied.booleanSupplier(() -> throwable.throwUnchecked("bar"));

                IntPredicate getting = predicate.onErrorGetUncheckedAsBoolean(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.test(1));
                assertEquals("bar", thrown.getMessage());

                verify(predicate).test(1);
                verify(predicate).onErrorGetUncheckedAsBoolean(fallback);
                verify(fallback).getAsBoolean();
                verifyNoMoreInteractions(predicate, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingIntPredicate<IOException> predicate = Spied.checkedIntPredicate(throwable::throwUnchecked);

            BooleanSupplier fallback = Spied.booleanSupplier(() -> false);

            IntPredicate getting = predicate.onErrorGetUncheckedAsBoolean(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.test(1));
            assertEquals("1", thrown.getMessage());

            verify(predicate).test(1);
            verify(predicate).onErrorGetUncheckedAsBoolean(fallback);
            verifyNoMoreInteractions(predicate, fallback);
        }
    }

    @Nested
    class OnErrorReturn {

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingIntPredicate<IOException> predicate = Spied.checkedIntPredicate(i -> i % 2 != 0);

            IntPredicate returning = predicate.onErrorReturn(false);

            assertTrue(returning.test(1));

            verify(predicate).test(1);
            verify(predicate).onErrorReturn(false);
            verifyNoMoreInteractions(predicate);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingIntPredicate<IOException> predicate = Spied.checkedIntPredicate(i -> {
                throw new IOException(Integer.toString(i));
            });

            IntPredicate returning = predicate.onErrorReturn(false);

            assertFalse(returning.test(1));

            verify(predicate).test(1);
            verify(predicate).onErrorReturn(false);
            verifyNoMoreInteractions(predicate);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingIntPredicate<IOException> predicate = Spied.checkedIntPredicate(throwable::throwUnchecked);

            IntPredicate returning = predicate.onErrorReturn(false);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> returning.test(1));
            assertEquals("1", thrown.getMessage());

            verify(predicate).test(1);
            verify(predicate).onErrorReturn(false);
            verifyNoMoreInteractions(predicate);
        }
    }

    @Nested
    class Unchecked {

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingIntPredicate<IOException> predicate = Spied.checkedIntPredicate(i -> i % 2 != 0);

            IntPredicate unchecked = predicate.unchecked();

            assertTrue(unchecked.test(1));

            verify(predicate).test(1);
            verify(predicate).unchecked();
            verify(predicate).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(predicate);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingIntPredicate<IOException> predicate = Spied.checkedIntPredicate(i -> {
                throw new IOException(Integer.toString(i));
            });

            IntPredicate unchecked = predicate.unchecked();

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.test(1));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1", cause.getMessage());

            verify(predicate).test(1);
            verify(predicate).unchecked();
            verify(predicate).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(predicate);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingIntPredicate<IOException> predicate = Spied.checkedIntPredicate(throwable::throwUnchecked);

            IntPredicate unchecked = predicate.unchecked();

            Throwable thrown = assertThrows(throwable.throwableType(), () -> unchecked.test(1));
            assertEquals("1", thrown.getMessage());

            verify(predicate).test(1);
            verify(predicate).unchecked();
            verify(predicate).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(predicate);
        }
    }

    @Nested
    class Of {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> ThrowingIntPredicate.of(null));
        }

        @Test
        void testNonNullArgument() throws IOException {
            ThrowingIntPredicate<IOException> predicate = ThrowingIntPredicate.of(i -> i % 2 != 0);

            assertTrue(predicate.test(1));
        }
    }

    @Nested
    class Not {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> ThrowingIntPredicate.not(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ThrowingIntPredicate<IOException> predicate = Spied.checkedIntPredicate(i -> i % 2 != 0);

            ThrowingIntPredicate<IOException> negated = ThrowingIntPredicate.not(predicate);

            assertFalse(negated.test(1));
            assertTrue(negated.test(0));

            verify(predicate).negate();
            verify(predicate).test(1);
            verify(predicate).test(0);
            verifyNoMoreInteractions(predicate);
        }

        @Test
        void testArgumentThrowsChecked() throws IOException {
            ThrowingIntPredicate<IOException> predicate = Spied.checkedIntPredicate(i -> {
                throw new IOException(Integer.toString(i));
            });

            ThrowingIntPredicate<IOException> negated = ThrowingIntPredicate.not(predicate);

            IOException thrown = assertThrows(IOException.class, () -> negated.test(1));
            assertEquals("1", thrown.getMessage());

            verify(predicate).negate();
            verify(predicate).test(1);
            verifyNoMoreInteractions(predicate);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testArgumentThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingIntPredicate<IOException> predicate = Spied.checkedIntPredicate(throwable::throwUnchecked);

            ThrowingIntPredicate<IOException> negated = ThrowingIntPredicate.not(predicate);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> negated.test(1));
            assertEquals("1", thrown.getMessage());

            verify(predicate).negate();
            verify(predicate).test(1);
            verifyNoMoreInteractions(predicate);
        }
    }

    @Nested
    class UncheckedFactory {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> ThrowingIntPredicate.unchecked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ThrowingIntPredicate<IOException> predicate = Spied.checkedIntPredicate(i -> i % 2 != 0);

            IntPredicate unchecked = ThrowingIntPredicate.unchecked(predicate);

            assertTrue(unchecked.test(1));

            verify(predicate).unchecked();
            verify(predicate).onErrorThrowAsUnchecked(any());
            verify(predicate).test(1);
            verifyNoMoreInteractions(predicate);
        }

        @Test
        void testArgumentThrowsChecked() throws IOException {
            ThrowingIntPredicate<IOException> predicate = Spied.checkedIntPredicate(i -> {
                throw new IOException(Integer.toString(i));
            });

            IntPredicate unchecked = ThrowingIntPredicate.unchecked(predicate);

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.test(1));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1", cause.getMessage());

            verify(predicate).unchecked();
            verify(predicate).onErrorThrowAsUnchecked(any());
            verify(predicate).test(1);
            verifyNoMoreInteractions(predicate);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testArgumentThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingIntPredicate<IOException> predicate = Spied.checkedIntPredicate(throwable::throwUnchecked);

            IntPredicate unchecked = ThrowingIntPredicate.unchecked(predicate);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> unchecked.test(1));
            assertEquals("1", thrown.getMessage());

            verify(predicate).unchecked();
            verify(predicate).onErrorThrowAsUnchecked(any());
            verify(predicate).test(1);
            verifyNoMoreInteractions(predicate);
        }
    }

    @Nested
    class CheckedWrapper {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> ThrowingIntPredicate.checked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ThrowingIntPredicate<IOException> predicate = ThrowingIntPredicate.checked(i -> i % 2 != 0);

            assertTrue(predicate.test(1));
        }

        @Test
        void testArgumentThrowsUnchecked() {
            ThrowingIntPredicate<IOException> predicate = ThrowingIntPredicate.checked(i -> {
                throw new UncheckedException(Integer.toString(i), new IOException());
            });

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> predicate.test(1));
            assertEquals("1", thrown.getMessage());
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertNull(cause.getMessage());
        }
    }

    @Nested
    class CheckedUnwrapper {

        @Test
        void testNullArguments() {
            IntPredicate predicate = i -> i % 2 != 0;

            assertThrows(NullPointerException.class, () -> ThrowingIntPredicate.checked(null, IOException.class));
            assertThrows(NullPointerException.class, () -> ThrowingIntPredicate.checked(predicate, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ThrowingIntPredicate<IOException> predicate = ThrowingIntPredicate.checked(i -> i % 2 != 0, IOException.class);

            assertTrue(predicate.test(1));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                ThrowingIntPredicate<IOException> predicate = ThrowingIntPredicate.checked(i -> {
                    throw new UncheckedException(new IOException(Integer.toString(i)));
                }, IOException.class);

                IOException thrown = assertThrows(IOException.class, () -> predicate.test(1));
                assertEquals("1", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                ThrowingIntPredicate<IOException> predicate = ThrowingIntPredicate.checked(i -> {
                    throw new UncheckedException(new FileNotFoundException(Integer.toString(i)));
                }, IOException.class);

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class, () -> predicate.test(1));
                assertEquals("1", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                ThrowingIntPredicate<IOException> predicate = ThrowingIntPredicate.checked(i -> {
                    throw new UncheckedException(new ParseException(Integer.toString(i), 0));
                }, IOException.class);

                UncheckedException thrown = assertThrows(UncheckedException.class, () -> predicate.test(1));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("1", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            ThrowingIntPredicate<IOException> predicate = ThrowingIntPredicate.checked(i -> {
                throw new IllegalStateException(Integer.toString(i));
            }, IOException.class);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> predicate.test(1));
            assertEquals("1", thrown.getMessage());
        }
    }

    @Nested
    class InvokeAndUnwrap {

        @Test
        void testNullArguments() {
            IntPredicate predicate = Objects::nonNull;

            assertThrows(NullPointerException.class, () -> ThrowingIntPredicate.invokeAndUnwrap(null, 1, IOException.class));
            assertThrows(NullPointerException.class, () -> ThrowingIntPredicate.invokeAndUnwrap(predicate, 1, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            IntPredicate predicate = i -> i % 2 != 0;

            assertTrue(ThrowingIntPredicate.invokeAndUnwrap(predicate, 1, IOException.class));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                IntPredicate predicate = i -> {
                    throw new UncheckedException(new IOException(Integer.toString(i)));
                };

                IOException thrown = assertThrows(IOException.class, () -> ThrowingIntPredicate.invokeAndUnwrap(predicate, 1, IOException.class));
                assertEquals("1", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                IntPredicate predicate = i -> {
                    throw new UncheckedException(new FileNotFoundException(Integer.toString(i)));
                };

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class,
                        () -> ThrowingIntPredicate.invokeAndUnwrap(predicate, 1, IOException.class));
                assertEquals("1", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                IntPredicate predicate = i -> {
                    throw new UncheckedException(new ParseException(Integer.toString(i), 0));
                };

                UncheckedException thrown = assertThrows(UncheckedException.class,
                        () -> ThrowingIntPredicate.invokeAndUnwrap(predicate, 1, IOException.class));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("1", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            IntPredicate predicate = i -> {
                throw new IllegalStateException(Integer.toString(i));
            };

            IllegalStateException thrown = assertThrows(IllegalStateException.class,
                    () -> ThrowingIntPredicate.invokeAndUnwrap(predicate, 1, IOException.class));
            assertEquals("1", thrown.getMessage());
        }
    }
}
