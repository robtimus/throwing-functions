/*
 * ThrowingLongPredicateTest.java
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
import java.util.function.LongPredicate;
import java.util.function.Predicate;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

@SuppressWarnings("nls")
class ThrowingLongPredicateTest {

    @Nested
    class And {

        @Test
        void testNullArgument() {
            ThrowingLongPredicate<IOException> predicate = Spied.throwingLongPredicate(l -> l % 2 != 0);

            assertThrows(NullPointerException.class, () -> predicate.and(null));
        }

        @Nested
        class NeitherThrows {

            @Test
            void testThisMatches() throws IOException {
                ThrowingLongPredicate<IOException> predicate = Spied.throwingLongPredicate(l -> l % 2 != 0);
                ThrowingLongPredicate<IOException> other = Spied.throwingLongPredicate(l -> l % 2 == 0);

                ThrowingLongPredicate<IOException> composed = predicate.and(other);

                assertFalse(composed.test(1L));

                verify(predicate).test(1L);
                verify(predicate).and(other);
                verify(other).test(1L);
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testOtherMatches() throws IOException {
                ThrowingLongPredicate<IOException> predicate = Spied.throwingLongPredicate(l -> l % 2 == 0);
                ThrowingLongPredicate<IOException> other = Spied.throwingLongPredicate(l -> l % 2 != 0);

                ThrowingLongPredicate<IOException> composed = predicate.and(other);

                assertFalse(composed.test(1L));

                verify(predicate).test(1L);
                verify(predicate).and(other);
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testBothMatch() throws IOException {
                ThrowingLongPredicate<IOException> predicate = Spied.throwingLongPredicate(l -> l % 2 != 0);
                ThrowingLongPredicate<IOException> other = Spied.throwingLongPredicate(l -> l > 0);

                ThrowingLongPredicate<IOException> composed = predicate.and(other);

                assertTrue(composed.test(1L));

                verify(predicate).test(1L);
                verify(predicate).and(other);
                verify(other).test(1L);
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testNeitherMatches() throws IOException {
                ThrowingLongPredicate<IOException> predicate = Spied.throwingLongPredicate(l -> l % 2 != 0);
                ThrowingLongPredicate<IOException> other = Spied.throwingLongPredicate(l -> l > 0);

                ThrowingLongPredicate<IOException> composed = predicate.and(other);

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
                ThrowingLongPredicate<IOException> predicate = Spied.throwingLongPredicate(l -> {
                    throw new IOException(Long.toString(l));
                });
                ThrowingLongPredicate<IOException> other = Spied.throwingLongPredicate(l -> l % 2 != 0);

                ThrowingLongPredicate<IOException> composed = predicate.and(other);

                IOException thrown = assertThrows(IOException.class, () -> composed.test(1L));
                assertEquals("1", thrown.getMessage());

                verify(predicate).test(1L);
                verify(predicate).and(other);
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testOtherDoesNotMatch() throws IOException {
                ThrowingLongPredicate<IOException> predicate = Spied.throwingLongPredicate(l -> {
                    throw new IOException(Long.toString(l));
                });
                ThrowingLongPredicate<IOException> other = Spied.throwingLongPredicate(l -> l % 2 == 0);

                ThrowingLongPredicate<IOException> composed = predicate.and(other);

                IOException thrown = assertThrows(IOException.class, () -> composed.test(1L));
                assertEquals("1", thrown.getMessage());

                verify(predicate).test(1L);
                verify(predicate).and(other);
                verifyNoMoreInteractions(predicate, other);
            }
        }

        @Nested
        class ThisThrowsUnchecked {

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testOtherMatches(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingLongPredicate<IOException> predicate = Spied.throwingLongPredicate(throwable::throwUnchecked);
                ThrowingLongPredicate<IOException> other = Spied.throwingLongPredicate(l -> l % 2 != 0);

                ThrowingLongPredicate<IOException> composed = predicate.and(other);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> composed.test(1L));
                assertEquals("1", thrown.getMessage());

                verify(predicate).test(1L);
                verify(predicate).and(other);
                verifyNoMoreInteractions(predicate, other);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testOtherDoesNotMatch(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingLongPredicate<IOException> predicate = Spied.throwingLongPredicate(throwable::throwUnchecked);
                ThrowingLongPredicate<IOException> other = Spied.throwingLongPredicate(l -> l % 2 == 0);

                ThrowingLongPredicate<IOException> composed = predicate.and(other);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> composed.test(1L));
                assertEquals("1", thrown.getMessage());

                verify(predicate).test(1L);
                verify(predicate).and(other);
                verifyNoMoreInteractions(predicate, other);
            }
        }

        @Nested
        class OtherThrowsChecked {

            @Test
            void testThisMatches() throws IOException {
                ThrowingLongPredicate<IOException> predicate = Spied.throwingLongPredicate(l -> l % 2 != 0);
                ThrowingLongPredicate<IOException> other = Spied.throwingLongPredicate(l -> {
                    throw new IOException(Long.toString(l));
                });

                ThrowingLongPredicate<IOException> composed = predicate.and(other);

                IOException thrown = assertThrows(IOException.class, () -> composed.test(1L));
                assertEquals("1", thrown.getMessage());

                verify(predicate).test(1L);
                verify(predicate).and(other);
                verify(other).test(1L);
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testThisDoesNotMatch() throws IOException {
                ThrowingLongPredicate<IOException> predicate = Spied.throwingLongPredicate(l -> l % 2 == 0);
                ThrowingLongPredicate<IOException> other = Spied.throwingLongPredicate(l -> {
                    throw new IOException(Long.toString(l));
                });

                ThrowingLongPredicate<IOException> composed = predicate.and(other);

                assertFalse(composed.test(1L));

                verify(predicate).test(1L);
                verify(predicate).and(other);
                verifyNoMoreInteractions(predicate, other);
            }
        }

        @Nested
        class OtherThrowsUnchecked {

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testThisMatches(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingLongPredicate<IOException> predicate = Spied.throwingLongPredicate(l -> l % 2 != 0);
                ThrowingLongPredicate<IOException> other = Spied.throwingLongPredicate(throwable::throwUnchecked);

                ThrowingLongPredicate<IOException> composed = predicate.and(other);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> composed.test(1L));
                assertEquals("1", thrown.getMessage());

                verify(predicate).test(1L);
                verify(predicate).and(other);
                verify(other).test(1L);
                verifyNoMoreInteractions(predicate, other);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testThisDoesNotMatch(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingLongPredicate<IOException> predicate = Spied.throwingLongPredicate(l -> l % 2 == 0);
                ThrowingLongPredicate<IOException> other = Spied.throwingLongPredicate(throwable::throwUnchecked);

                ThrowingLongPredicate<IOException> composed = predicate.and(other);

                assertFalse(composed.test(1L));

                verify(predicate).test(1L);
                verify(predicate).and(other);
                verifyNoMoreInteractions(predicate, other);
            }
        }
    }

    @Nested
    class Negate {

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingLongPredicate<IOException> predicate = Spied.throwingLongPredicate(l -> l % 2 != 0);

            ThrowingLongPredicate<IOException> negated = predicate.negate();

            assertFalse(negated.test(1L));
            assertTrue(negated.test(0));

            verify(predicate).test(1L);
            verify(predicate).test(0);
            verify(predicate).negate();
            verifyNoMoreInteractions(predicate);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingLongPredicate<IOException> predicate = Spied.throwingLongPredicate(l -> {
                throw new IOException(Long.toString(l));
            });

            ThrowingLongPredicate<IOException> negated = predicate.negate();

            IOException thrown = assertThrows(IOException.class, () -> negated.test(1L));
            assertEquals("1", thrown.getMessage());

            verify(predicate).test(1L);
            verify(predicate).negate();
            verifyNoMoreInteractions(predicate);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingLongPredicate<IOException> predicate = Spied.throwingLongPredicate(throwable::throwUnchecked);

            ThrowingLongPredicate<IOException> negated = predicate.negate();

            Throwable thrown = assertThrows(throwable.throwableType(), () -> negated.test(1L));
            assertEquals("1", thrown.getMessage());

            verify(predicate).test(1L);
            verify(predicate).negate();
            verifyNoMoreInteractions(predicate);
        }
    }

    @Nested
    class Or {

        @Test
        void testNullArgument() {
            ThrowingLongPredicate<IOException> predicate = Spied.throwingLongPredicate(l -> l % 2 != 0);

            assertThrows(NullPointerException.class, () -> predicate.or(null));
        }

        @Nested
        class NeitherThrows {

            @Test
            void testThisMatches() throws IOException {
                ThrowingLongPredicate<IOException> predicate = Spied.throwingLongPredicate(l -> l % 2 != 0);
                ThrowingLongPredicate<IOException> other = Spied.throwingLongPredicate(l -> l % 2 == 0);

                ThrowingLongPredicate<IOException> composed = predicate.or(other);

                assertTrue(composed.test(1L));

                verify(predicate).test(1L);
                verify(predicate).or(other);
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testOtherMatches() throws IOException {
                ThrowingLongPredicate<IOException> predicate = Spied.throwingLongPredicate(l -> l % 2 == 0);
                ThrowingLongPredicate<IOException> other = Spied.throwingLongPredicate(l -> l % 2 != 0);

                ThrowingLongPredicate<IOException> composed = predicate.or(other);

                assertTrue(composed.test(1L));

                verify(predicate).test(1L);
                verify(predicate).or(other);
                verify(other).test(1L);
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testBothMatch() throws IOException {
                ThrowingLongPredicate<IOException> predicate = Spied.throwingLongPredicate(l -> l % 2 != 0);
                ThrowingLongPredicate<IOException> other = Spied.throwingLongPredicate(l -> l > 0);

                ThrowingLongPredicate<IOException> composed = predicate.or(other);

                assertTrue(composed.test(1L));

                verify(predicate).test(1L);
                verify(predicate).or(other);
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testNeitherMatches() throws IOException {
                ThrowingLongPredicate<IOException> predicate = Spied.throwingLongPredicate(l -> l % 2 != 0);
                ThrowingLongPredicate<IOException> other = Spied.throwingLongPredicate(l -> l > 0);

                ThrowingLongPredicate<IOException> composed = predicate.or(other);

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
                ThrowingLongPredicate<IOException> predicate = Spied.throwingLongPredicate(l -> {
                    throw new IOException(Long.toString(l));
                });
                ThrowingLongPredicate<IOException> other = Spied.throwingLongPredicate(l -> l % 2 != 0);

                ThrowingLongPredicate<IOException> composed = predicate.or(other);

                IOException thrown = assertThrows(IOException.class, () -> composed.test(1L));
                assertEquals("1", thrown.getMessage());

                verify(predicate).test(1L);
                verify(predicate).or(other);
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testOtherDoesNotMatch() throws IOException {
                ThrowingLongPredicate<IOException> predicate = Spied.throwingLongPredicate(l -> {
                    throw new IOException(Long.toString(l));
                });
                ThrowingLongPredicate<IOException> other = Spied.throwingLongPredicate(l -> l % 2 == 0);

                ThrowingLongPredicate<IOException> composed = predicate.or(other);

                IOException thrown = assertThrows(IOException.class, () -> composed.test(1L));
                assertEquals("1", thrown.getMessage());

                verify(predicate).test(1L);
                verify(predicate).or(other);
                verifyNoMoreInteractions(predicate, other);
            }
        }

        @Nested
        class ThisThrowsUnchecked {

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testOtherMatches(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingLongPredicate<IOException> predicate = Spied.throwingLongPredicate(throwable::throwUnchecked);
                ThrowingLongPredicate<IOException> other = Spied.throwingLongPredicate(l -> l % 2 != 0);

                ThrowingLongPredicate<IOException> composed = predicate.or(other);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> composed.test(1L));
                assertEquals("1", thrown.getMessage());

                verify(predicate).test(1L);
                verify(predicate).or(other);
                verifyNoMoreInteractions(predicate, other);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testOtherDoesNotMatch(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingLongPredicate<IOException> predicate = Spied.throwingLongPredicate(throwable::throwUnchecked);
                ThrowingLongPredicate<IOException> other = Spied.throwingLongPredicate(l -> l % 2 == 0);

                ThrowingLongPredicate<IOException> composed = predicate.or(other);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> composed.test(1L));
                assertEquals("1", thrown.getMessage());

                verify(predicate).test(1L);
                verify(predicate).or(other);
                verifyNoMoreInteractions(predicate, other);
            }
        }

        @Nested
        class OtherThrowsChecked {

            @Test
            void testThisMatches() throws IOException {
                ThrowingLongPredicate<IOException> predicate = Spied.throwingLongPredicate(l -> l % 2 != 0);
                ThrowingLongPredicate<IOException> other = Spied.throwingLongPredicate(l -> {
                    throw new IOException(Long.toString(l));
                });

                ThrowingLongPredicate<IOException> composed = predicate.or(other);

                assertTrue(composed.test(1L));

                verify(predicate).test(1L);
                verify(predicate).or(other);
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testThisDoesNotMatch() throws IOException {
                ThrowingLongPredicate<IOException> predicate = Spied.throwingLongPredicate(l -> l % 2 == 0);
                ThrowingLongPredicate<IOException> other = Spied.throwingLongPredicate(l -> {
                    throw new IOException(Long.toString(l));
                });

                ThrowingLongPredicate<IOException> composed = predicate.or(other);

                IOException thrown = assertThrows(IOException.class, () -> composed.test(1L));
                assertEquals("1", thrown.getMessage());

                verify(predicate).test(1L);
                verify(predicate).or(other);
                verify(other).test(1L);
                verifyNoMoreInteractions(predicate, other);
            }
        }

        @Nested
        class OtherThrowsUnchecked {

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testThisMatches(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingLongPredicate<IOException> predicate = Spied.throwingLongPredicate(l -> l % 2 != 0);
                ThrowingLongPredicate<IOException> other = Spied.throwingLongPredicate(throwable::throwUnchecked);

                ThrowingLongPredicate<IOException> composed = predicate.or(other);

                assertTrue(composed.test(1L));

                verify(predicate).test(1L);
                verify(predicate).or(other);
                verifyNoMoreInteractions(predicate, other);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testThisDoesNotMatch(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingLongPredicate<IOException> predicate = Spied.throwingLongPredicate(l -> l % 2 == 0);
                ThrowingLongPredicate<IOException> other = Spied.throwingLongPredicate(throwable::throwUnchecked);

                ThrowingLongPredicate<IOException> composed = predicate.or(other);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> composed.test(1L));
                assertEquals("1", thrown.getMessage());

                verify(predicate).test(1L);
                verify(predicate).or(other);
                verify(other).test(1L);
                verifyNoMoreInteractions(predicate, other);
            }
        }
    }

    @Nested
    class OnErrorThrowAsChecked {

        @Test
        void testNullArgument() {
            ThrowingLongPredicate<IOException> predicate = Spied.throwingLongPredicate(l -> l % 2 != 0);

            assertThrows(NullPointerException.class, () -> predicate.onErrorThrowAsChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingLongPredicate<IOException> predicate = Spied.throwingLongPredicate(l -> l % 2 != 0);

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingLongPredicate<ExecutionException> throwing = predicate.onErrorThrowAsChecked(errorMapper);

            assertTrue(throwing.test(1L));

            verify(predicate).test(1L);
            verify(predicate).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(predicate, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingLongPredicate<IOException> predicate = Spied.throwingLongPredicate(l -> {
                throw new IOException(Long.toString(l));
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingLongPredicate<ExecutionException> throwing = predicate.onErrorThrowAsChecked(errorMapper);

            ExecutionException thrown = assertThrows(ExecutionException.class, () -> throwing.test(1L));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1", cause.getMessage());

            verify(predicate).test(1L);
            verify(predicate).onErrorThrowAsChecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(predicate, errorMapper);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingLongPredicate<IOException> predicate = Spied.throwingLongPredicate(throwable::throwUnchecked);

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingLongPredicate<ExecutionException> throwing = predicate.onErrorThrowAsChecked(errorMapper);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> throwing.test(1L));
            assertEquals("1", thrown.getMessage());

            verify(predicate).test(1L);
            verify(predicate).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(predicate, errorMapper);
        }
    }

    @Nested
    class OnErrorThrowAsUnchecked {

        @Test
        void testNullArgument() {
            ThrowingLongPredicate<IOException> predicate = Spied.throwingLongPredicate(l -> l % 2 != 0);

            assertThrows(NullPointerException.class, () -> predicate.onErrorThrowAsUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingLongPredicate<IOException> predicate = Spied.throwingLongPredicate(l -> l % 2 != 0);

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            LongPredicate throwing = predicate.onErrorThrowAsUnchecked(errorMapper);

            assertTrue(throwing.test(1L));

            verify(predicate).test(1L);
            verify(predicate).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(predicate, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingLongPredicate<IOException> predicate = Spied.throwingLongPredicate(l -> {
                throw new IOException(Long.toString(l));
            });

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            LongPredicate throwing = predicate.onErrorThrowAsUnchecked(errorMapper);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> throwing.test(1L));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1", cause.getMessage());

            verify(predicate).test(1L);
            verify(predicate).onErrorThrowAsUnchecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(predicate, errorMapper);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingLongPredicate<IOException> predicate = Spied.throwingLongPredicate(throwable::throwUnchecked);

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            LongPredicate throwing = predicate.onErrorThrowAsUnchecked(errorMapper);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> throwing.test(1L));
            assertEquals("1", thrown.getMessage());

            verify(predicate).test(1L);
            verify(predicate).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(predicate, errorMapper);
        }
    }

    @Nested
    class OnErrorHandleChecked {

        @Test
        void testNullArgument() {
            ThrowingLongPredicate<IOException> predicate = Spied.throwingLongPredicate(l -> l % 2 != 0);

            assertThrows(NullPointerException.class, () -> predicate.onErrorHandleChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingLongPredicate<IOException> predicate = Spied.throwingLongPredicate(l -> l % 2 != 0);

            ThrowingPredicate<IOException, ExecutionException> errorHandler = Spied.throwingPredicate(e -> e.getMessage() == null);

            ThrowingLongPredicate<ExecutionException> handling = predicate.onErrorHandleChecked(errorHandler);

            assertTrue(handling.test(1L));

            verify(predicate).test(1L);
            verify(predicate).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(predicate, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException, ExecutionException {
                ThrowingLongPredicate<IOException> predicate = Spied.throwingLongPredicate(l -> {
                    throw new IOException(Long.toString(l));
                });

                ThrowingPredicate<IOException, ExecutionException> errorHandler = Spied.throwingPredicate(e -> e.getMessage() == null);

                ThrowingLongPredicate<ExecutionException> handling = predicate.onErrorHandleChecked(errorHandler);

                assertFalse(handling.test(1L));

                verify(predicate).test(1L);
                verify(predicate).onErrorHandleChecked(errorHandler);
                verify(errorHandler).test(any());
                verifyNoMoreInteractions(predicate, errorHandler);
            }

            @Test
            void testHandlerThrowsChecked() throws IOException, ExecutionException {
                ThrowingLongPredicate<IOException> predicate = Spied.throwingLongPredicate(l -> {
                    throw new IOException(Long.toString(l));
                });

                ThrowingPredicate<IOException, ExecutionException> errorHandler = Spied.throwingPredicate(e -> {
                    throw new ExecutionException(e);
                });

                ThrowingLongPredicate<ExecutionException> handling = predicate.onErrorHandleChecked(errorHandler);

                ExecutionException thrown = assertThrows(ExecutionException.class, () -> handling.test(1L));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("1", cause.getMessage());

                verify(predicate).test(1L);
                verify(predicate).onErrorHandleChecked(errorHandler);
                verify(errorHandler).test(any());
                verifyNoMoreInteractions(predicate, errorHandler);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testHandlerThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ExecutionException {
                ThrowingLongPredicate<IOException> predicate = Spied.throwingLongPredicate(l -> {
                    throw new IOException(Long.toString(l));
                });

                ThrowingPredicate<IOException, ExecutionException> errorHandler = Spied.throwingPredicate(throwable::throwUnchecked);

                ThrowingLongPredicate<ExecutionException> handling = predicate.onErrorHandleChecked(errorHandler);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.test(1L));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("1", cause.getMessage());

                verify(predicate).test(1L);
                verify(predicate).onErrorHandleChecked(errorHandler);
                verify(errorHandler).test(any());
                verifyNoMoreInteractions(predicate, errorHandler);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingLongPredicate<IOException> predicate = Spied.throwingLongPredicate(throwable::throwUnchecked);

            ThrowingPredicate<IOException, ExecutionException> errorHandler = Spied.throwingPredicate(e -> e.getMessage() == null);

            ThrowingLongPredicate<ExecutionException> handling = predicate.onErrorHandleChecked(errorHandler);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.test(1L));
            assertEquals("1", thrown.getMessage());

            verify(predicate).test(1L);
            verify(predicate).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(predicate, errorHandler);
        }
    }

    @Nested
    class OnErrorHandleUnchecked {

        @Test
        void testNullArgument() {
            ThrowingLongPredicate<IOException> predicate = Spied.throwingLongPredicate(l -> l % 2 != 0);

            assertThrows(NullPointerException.class, () -> predicate.onErrorHandleUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingLongPredicate<IOException> predicate = Spied.throwingLongPredicate(l -> l % 2 != 0);

            Predicate<IOException> errorHandler = Spied.predicate(e -> e.getMessage() == null);

            LongPredicate handling = predicate.onErrorHandleUnchecked(errorHandler);

            assertTrue(handling.test(1L));

            verify(predicate).test(1L);
            verify(predicate).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(predicate, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException {
                ThrowingLongPredicate<IOException> predicate = Spied.throwingLongPredicate(l -> {
                    throw new IOException(Long.toString(l));
                });

                Predicate<IOException> errorHandler = Spied.predicate(e -> e.getMessage() == null);

                LongPredicate handling = predicate.onErrorHandleUnchecked(errorHandler);

                assertFalse(handling.test(1L));

                verify(predicate).test(1L);
                verify(predicate).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).test(any());
                verifyNoMoreInteractions(predicate, errorHandler);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testHandlerThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingLongPredicate<IOException> predicate = Spied.throwingLongPredicate(l -> {
                    throw new IOException(Long.toString(l));
                });

                Predicate<IOException> errorHandler = Spied.predicate(throwable::throwUnchecked);

                LongPredicate handling = predicate.onErrorHandleUnchecked(errorHandler);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.test(1L));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("1", cause.getMessage());

                verify(predicate).test(1L);
                verify(predicate).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).test(any());
                verifyNoMoreInteractions(predicate, errorHandler);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingLongPredicate<IOException> predicate = Spied.throwingLongPredicate(throwable::throwUnchecked);

            Predicate<IOException> errorHandler = Spied.predicate(e -> e.getMessage() == null);

            LongPredicate handling = predicate.onErrorHandleUnchecked(errorHandler);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.test(1L));
            assertEquals("1", thrown.getMessage());

            verify(predicate).test(1L);
            verify(predicate).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(predicate, errorHandler);
        }
    }

    @Nested
    class OnErrorTestChecked {

        @Test
        void testNullArgument() {
            ThrowingLongPredicate<IOException> predicate = Spied.throwingLongPredicate(l -> l % 2 != 0);

            assertThrows(NullPointerException.class, () -> predicate.onErrorTestChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingLongPredicate<IOException> predicate = Spied.throwingLongPredicate(l -> l % 2 != 0);

            ThrowingLongPredicate<ExecutionException> fallback = Spied.throwingLongPredicate(l -> l % 2 == 0);

            ThrowingLongPredicate<ExecutionException> testing = predicate.onErrorTestChecked(fallback);

            assertTrue(testing.test(1L));

            verify(predicate).test(1L);
            verify(predicate).onErrorTestChecked(fallback);
            verifyNoMoreInteractions(predicate, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                ThrowingLongPredicate<IOException> predicate = Spied.throwingLongPredicate(l -> {
                    throw new IOException(Long.toString(l));
                });

                ThrowingLongPredicate<ParseException> fallback = Spied.throwingLongPredicate(l -> l % 2 == 0);

                ThrowingLongPredicate<ParseException> testing = predicate.onErrorTestChecked(fallback);

                assertFalse(testing.test(1L));

                verify(predicate).test(1L);
                verify(predicate).onErrorTestChecked(fallback);
                verify(fallback).test(1L);
                verifyNoMoreInteractions(predicate, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                ThrowingLongPredicate<IOException> predicate = Spied.throwingLongPredicate(l -> {
                    throw new IOException(Long.toString(l));
                });

                ThrowingLongPredicate<ParseException> fallback = Spied.throwingLongPredicate(l -> {
                    throw new ParseException(Long.toString(l), 0);
                });

                ThrowingLongPredicate<ParseException> testing = predicate.onErrorTestChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> testing.test(1L));
                assertEquals("1", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(predicate).test(1L);
                verify(predicate).onErrorTestChecked(fallback);
                verify(fallback).test(1L);
                verifyNoMoreInteractions(predicate, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ParseException {
                ThrowingLongPredicate<IOException> predicate = Spied.throwingLongPredicate(l -> {
                    throw new IOException(Long.toString(l));
                });

                ThrowingLongPredicate<ParseException> fallback = Spied.throwingLongPredicate(throwable::throwUnchecked);

                ThrowingLongPredicate<ParseException> testing = predicate.onErrorTestChecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> testing.test(1L));
                assertEquals("1", thrown.getMessage());

                verify(predicate).test(1L);
                verify(predicate).onErrorTestChecked(fallback);
                verify(fallback).test(1L);
                verifyNoMoreInteractions(predicate, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingLongPredicate<IOException> predicate = Spied.throwingLongPredicate(throwable::throwUnchecked);

            ThrowingLongPredicate<ParseException> fallback = Spied.throwingLongPredicate(l -> l % 2 == 0);

            ThrowingLongPredicate<ParseException> testing = predicate.onErrorTestChecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> testing.test(1L));
            assertEquals("1", thrown.getMessage());

            verify(predicate).test(1L);
            verify(predicate).onErrorTestChecked(fallback);
            verifyNoMoreInteractions(predicate, fallback);
        }
    }

    @Nested
    class OnErrorTestUnchecked {

        @Test
        void testNullArgument() {
            ThrowingLongPredicate<IOException> predicate = Spied.throwingLongPredicate(l -> l % 2 != 0);

            assertThrows(NullPointerException.class, () -> predicate.onErrorTestUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingLongPredicate<IOException> predicate = Spied.throwingLongPredicate(l -> l % 2 != 0);

            LongPredicate fallback = Spied.longPredicate(l -> l % 2 == 0);

            LongPredicate testing = predicate.onErrorTestUnchecked(fallback);

            assertTrue(testing.test(1L));

            verify(predicate).test(1L);
            verify(predicate).onErrorTestUnchecked(fallback);
            verifyNoMoreInteractions(predicate, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                ThrowingLongPredicate<IOException> predicate = Spied.throwingLongPredicate(l -> {
                    throw new IOException(Long.toString(l));
                });

                LongPredicate fallback = Spied.longPredicate(l -> l % 2 == 0);

                LongPredicate testing = predicate.onErrorTestUnchecked(fallback);

                assertFalse(testing.test(1L));

                verify(predicate).test(1L);
                verify(predicate).onErrorTestUnchecked(fallback);
                verify(fallback).test(1L);
                verifyNoMoreInteractions(predicate, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingLongPredicate<IOException> predicate = Spied.throwingLongPredicate(l -> {
                    throw new IOException(Long.toString(l));
                });

                LongPredicate fallback = Spied.longPredicate(throwable::throwUnchecked);

                LongPredicate testing = predicate.onErrorTestUnchecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> testing.test(1L));
                assertEquals("1", thrown.getMessage());

                verify(predicate).test(1L);
                verify(predicate).onErrorTestUnchecked(fallback);
                verify(fallback).test(1L);
                verifyNoMoreInteractions(predicate, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingLongPredicate<IOException> predicate = Spied.throwingLongPredicate(throwable::throwUnchecked);

            LongPredicate fallback = Spied.longPredicate(l -> l % 2 == 0);

            LongPredicate testing = predicate.onErrorTestUnchecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> testing.test(1L));
            assertEquals("1", thrown.getMessage());

            verify(predicate).test(1L);
            verify(predicate).onErrorTestUnchecked(fallback);
            verifyNoMoreInteractions(predicate, fallback);
        }
    }

    @Nested
    class OnErrorGetCheckedAsBoolean {

        @Test
        void testNullArgument() {
            ThrowingLongPredicate<IOException> predicate = Spied.throwingLongPredicate(l -> l % 2 != 0);

            assertThrows(NullPointerException.class, () -> predicate.onErrorGetCheckedAsBoolean(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingLongPredicate<IOException> predicate = Spied.throwingLongPredicate(l -> l % 2 != 0);

            ThrowingBooleanSupplier<ExecutionException> fallback = Spied.throwingBooleanSupplier(() -> false);

            ThrowingLongPredicate<ExecutionException> getting = predicate.onErrorGetCheckedAsBoolean(fallback);

            assertTrue(getting.test(1L));

            verify(predicate).test(1L);
            verify(predicate).onErrorGetCheckedAsBoolean(fallback);
            verifyNoMoreInteractions(predicate, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                ThrowingLongPredicate<IOException> predicate = Spied.throwingLongPredicate(l -> {
                    throw new IOException(Long.toString(l));
                });

                ThrowingBooleanSupplier<ParseException> fallback = Spied.throwingBooleanSupplier(() -> false);

                ThrowingLongPredicate<ParseException> getting = predicate.onErrorGetCheckedAsBoolean(fallback);

                assertFalse(getting.test(1L));

                verify(predicate).test(1L);
                verify(predicate).onErrorGetCheckedAsBoolean(fallback);
                verify(fallback).getAsBoolean();
                verifyNoMoreInteractions(predicate, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                ThrowingLongPredicate<IOException> predicate = Spied.throwingLongPredicate(l -> {
                    throw new IOException(Long.toString(l));
                });

                ThrowingBooleanSupplier<ParseException> fallback = Spied.throwingBooleanSupplier(() -> {
                    throw new ParseException("bar", 0);
                });

                ThrowingLongPredicate<ParseException> getting = predicate.onErrorGetCheckedAsBoolean(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> getting.test(1L));
                assertEquals("bar", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(predicate).test(1L);
                verify(predicate).onErrorGetCheckedAsBoolean(fallback);
                verify(fallback).getAsBoolean();
                verifyNoMoreInteractions(predicate, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ParseException {
                ThrowingLongPredicate<IOException> predicate = Spied.throwingLongPredicate(l -> {
                    throw new IOException(Long.toString(l));
                });

                ThrowingBooleanSupplier<ParseException> fallback = Spied.throwingBooleanSupplier(() -> throwable.throwUnchecked("bar"));

                ThrowingLongPredicate<ParseException> getting = predicate.onErrorGetCheckedAsBoolean(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.test(1L));
                assertEquals("bar", thrown.getMessage());

                verify(predicate).test(1L);
                verify(predicate).onErrorGetCheckedAsBoolean(fallback);
                verify(fallback).getAsBoolean();
                verifyNoMoreInteractions(predicate, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingLongPredicate<IOException> predicate = Spied.throwingLongPredicate(throwable::throwUnchecked);

            ThrowingBooleanSupplier<ParseException> fallback = Spied.throwingBooleanSupplier(() -> false);

            ThrowingLongPredicate<ParseException> getting = predicate.onErrorGetCheckedAsBoolean(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.test(1L));
            assertEquals("1", thrown.getMessage());

            verify(predicate).test(1L);
            verify(predicate).onErrorGetCheckedAsBoolean(fallback);
            verifyNoMoreInteractions(predicate, fallback);
        }
    }

    @Nested
    class OnErrorGetUncheckedAsBoolean {

        @Test
        void testNullArgument() {
            ThrowingLongPredicate<IOException> predicate = Spied.throwingLongPredicate(l -> l % 2 != 0);

            assertThrows(NullPointerException.class, () -> predicate.onErrorGetUncheckedAsBoolean(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingLongPredicate<IOException> predicate = Spied.throwingLongPredicate(l -> l % 2 != 0);

            BooleanSupplier fallback = Spied.booleanSupplier(() -> false);

            LongPredicate getting = predicate.onErrorGetUncheckedAsBoolean(fallback);

            assertTrue(getting.test(1L));

            verify(predicate).test(1L);
            verify(predicate).onErrorGetUncheckedAsBoolean(fallback);
            verifyNoMoreInteractions(predicate, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                ThrowingLongPredicate<IOException> predicate = Spied.throwingLongPredicate(l -> {
                    throw new IOException(Long.toString(l));
                });

                BooleanSupplier fallback = Spied.booleanSupplier(() -> false);

                LongPredicate getting = predicate.onErrorGetUncheckedAsBoolean(fallback);

                assertFalse(getting.test(1L));

                verify(predicate).test(1L);
                verify(predicate).onErrorGetUncheckedAsBoolean(fallback);
                verify(fallback).getAsBoolean();
                verifyNoMoreInteractions(predicate, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingLongPredicate<IOException> predicate = Spied.throwingLongPredicate(l -> {
                    throw new IOException(Long.toString(l));
                });

                BooleanSupplier fallback = Spied.booleanSupplier(() -> throwable.throwUnchecked("bar"));

                LongPredicate getting = predicate.onErrorGetUncheckedAsBoolean(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.test(1L));
                assertEquals("bar", thrown.getMessage());

                verify(predicate).test(1L);
                verify(predicate).onErrorGetUncheckedAsBoolean(fallback);
                verify(fallback).getAsBoolean();
                verifyNoMoreInteractions(predicate, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingLongPredicate<IOException> predicate = Spied.throwingLongPredicate(throwable::throwUnchecked);

            BooleanSupplier fallback = Spied.booleanSupplier(() -> false);

            LongPredicate getting = predicate.onErrorGetUncheckedAsBoolean(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.test(1L));
            assertEquals("1", thrown.getMessage());

            verify(predicate).test(1L);
            verify(predicate).onErrorGetUncheckedAsBoolean(fallback);
            verifyNoMoreInteractions(predicate, fallback);
        }
    }

    @Nested
    class OnErrorReturn {

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingLongPredicate<IOException> predicate = Spied.throwingLongPredicate(l -> l % 2 != 0);

            LongPredicate returning = predicate.onErrorReturn(false);

            assertTrue(returning.test(1L));

            verify(predicate).test(1L);
            verify(predicate).onErrorReturn(false);
            verifyNoMoreInteractions(predicate);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingLongPredicate<IOException> predicate = Spied.throwingLongPredicate(l -> {
                throw new IOException(Long.toString(l));
            });

            LongPredicate returning = predicate.onErrorReturn(false);

            assertFalse(returning.test(1L));

            verify(predicate).test(1L);
            verify(predicate).onErrorReturn(false);
            verifyNoMoreInteractions(predicate);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingLongPredicate<IOException> predicate = Spied.throwingLongPredicate(throwable::throwUnchecked);

            LongPredicate returning = predicate.onErrorReturn(false);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> returning.test(1L));
            assertEquals("1", thrown.getMessage());

            verify(predicate).test(1L);
            verify(predicate).onErrorReturn(false);
            verifyNoMoreInteractions(predicate);
        }
    }

    @Nested
    class Unchecked {

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingLongPredicate<IOException> predicate = Spied.throwingLongPredicate(l -> l % 2 != 0);

            LongPredicate unchecked = predicate.unchecked();

            assertTrue(unchecked.test(1L));

            verify(predicate).test(1L);
            verify(predicate).unchecked();
            verify(predicate).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(predicate);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingLongPredicate<IOException> predicate = Spied.throwingLongPredicate(l -> {
                throw new IOException(Long.toString(l));
            });

            LongPredicate unchecked = predicate.unchecked();

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.test(1L));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1", cause.getMessage());

            verify(predicate).test(1L);
            verify(predicate).unchecked();
            verify(predicate).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(predicate);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingLongPredicate<IOException> predicate = Spied.throwingLongPredicate(throwable::throwUnchecked);

            LongPredicate unchecked = predicate.unchecked();

            Throwable thrown = assertThrows(throwable.throwableType(), () -> unchecked.test(1L));
            assertEquals("1", thrown.getMessage());

            verify(predicate).test(1L);
            verify(predicate).unchecked();
            verify(predicate).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(predicate);
        }
    }

    @Nested
    class Of {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> ThrowingLongPredicate.of(null));
        }

        @Test
        void testNonNullArgument() throws IOException {
            ThrowingLongPredicate<IOException> predicate = ThrowingLongPredicate.of(l -> l % 2 != 0);

            assertTrue(predicate.test(1L));
        }
    }

    @Nested
    class Not {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> ThrowingLongPredicate.not(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ThrowingLongPredicate<IOException> predicate = Spied.throwingLongPredicate(l -> l % 2 != 0);

            ThrowingLongPredicate<IOException> negated = ThrowingLongPredicate.not(predicate);

            assertFalse(negated.test(1L));
            assertTrue(negated.test(0));

            verify(predicate).negate();
            verify(predicate).test(1L);
            verify(predicate).test(0);
            verifyNoMoreInteractions(predicate);
        }

        @Test
        void testArgumentThrowsChecked() throws IOException {
            ThrowingLongPredicate<IOException> predicate = Spied.throwingLongPredicate(l -> {
                throw new IOException(Long.toString(l));
            });

            ThrowingLongPredicate<IOException> negated = ThrowingLongPredicate.not(predicate);

            IOException thrown = assertThrows(IOException.class, () -> negated.test(1L));
            assertEquals("1", thrown.getMessage());

            verify(predicate).negate();
            verify(predicate).test(1L);
            verifyNoMoreInteractions(predicate);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testArgumentThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingLongPredicate<IOException> predicate = Spied.throwingLongPredicate(throwable::throwUnchecked);

            ThrowingLongPredicate<IOException> negated = ThrowingLongPredicate.not(predicate);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> negated.test(1L));
            assertEquals("1", thrown.getMessage());

            verify(predicate).negate();
            verify(predicate).test(1L);
            verifyNoMoreInteractions(predicate);
        }
    }

    @Nested
    class UncheckedFactory {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> ThrowingLongPredicate.unchecked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ThrowingLongPredicate<IOException> predicate = Spied.throwingLongPredicate(l -> l % 2 != 0);

            LongPredicate unchecked = ThrowingLongPredicate.unchecked(predicate);

            assertTrue(unchecked.test(1L));

            verify(predicate).unchecked();
            verify(predicate).onErrorThrowAsUnchecked(any());
            verify(predicate).test(1L);
            verifyNoMoreInteractions(predicate);
        }

        @Test
        void testArgumentThrowsChecked() throws IOException {
            ThrowingLongPredicate<IOException> predicate = Spied.throwingLongPredicate(l -> {
                throw new IOException(Long.toString(l));
            });

            LongPredicate unchecked = ThrowingLongPredicate.unchecked(predicate);

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.test(1L));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1", cause.getMessage());

            verify(predicate).unchecked();
            verify(predicate).onErrorThrowAsUnchecked(any());
            verify(predicate).test(1L);
            verifyNoMoreInteractions(predicate);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testArgumentThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingLongPredicate<IOException> predicate = Spied.throwingLongPredicate(throwable::throwUnchecked);

            LongPredicate unchecked = ThrowingLongPredicate.unchecked(predicate);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> unchecked.test(1L));
            assertEquals("1", thrown.getMessage());

            verify(predicate).unchecked();
            verify(predicate).onErrorThrowAsUnchecked(any());
            verify(predicate).test(1L);
            verifyNoMoreInteractions(predicate);
        }
    }

    @Nested
    class CheckedWrapper {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> ThrowingLongPredicate.checked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ThrowingLongPredicate<IOException> predicate = ThrowingLongPredicate.checked(l -> l % 2 != 0);

            assertTrue(predicate.test(1L));
        }

        @Test
        void testArgumentThrowsUnchecked() {
            ThrowingLongPredicate<IOException> predicate = ThrowingLongPredicate.checked(l -> {
                throw UncheckedException.withoutStackTrace(Long.toString(l), new IOException());
            });

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> predicate.test(1L));
            assertEquals("1", thrown.getMessage());
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertNull(cause.getMessage());
        }
    }

    @Nested
    class CheckedUnwrapper {

        @Test
        void testNullArguments() {
            LongPredicate predicate = l -> l % 2 != 0;

            assertThrows(NullPointerException.class, () -> ThrowingLongPredicate.checked(null, IOException.class));
            assertThrows(NullPointerException.class, () -> ThrowingLongPredicate.checked(predicate, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ThrowingLongPredicate<IOException> predicate = ThrowingLongPredicate.checked(l -> l % 2 != 0, IOException.class);

            assertTrue(predicate.test(1L));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                ThrowingLongPredicate<IOException> predicate = ThrowingLongPredicate.checked(l -> {
                    throw UncheckedException.withoutStackTrace(new IOException(Long.toString(l)));
                }, IOException.class);

                IOException thrown = assertThrows(IOException.class, () -> predicate.test(1L));
                assertEquals("1", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                ThrowingLongPredicate<IOException> predicate = ThrowingLongPredicate.checked(l -> {
                    throw UncheckedException.withoutStackTrace(new FileNotFoundException(Long.toString(l)));
                }, IOException.class);

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class, () -> predicate.test(1L));
                assertEquals("1", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                ThrowingLongPredicate<IOException> predicate = ThrowingLongPredicate.checked(l -> {
                    throw UncheckedException.withoutStackTrace(new ParseException(Long.toString(l), 0));
                }, IOException.class);

                UncheckedException thrown = assertThrows(UncheckedException.class, () -> predicate.test(1L));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("1", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            ThrowingLongPredicate<IOException> predicate = ThrowingLongPredicate.checked(l -> {
                throw new IllegalStateException(Long.toString(l));
            }, IOException.class);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> predicate.test(1L));
            assertEquals("1", thrown.getMessage());
        }
    }

    @Nested
    class InvokeAndUnwrap {

        @Test
        void testNullArguments() {
            LongPredicate predicate = Objects::nonNull;

            assertThrows(NullPointerException.class, () -> ThrowingLongPredicate.invokeAndUnwrap(null, 1L, IOException.class));
            assertThrows(NullPointerException.class, () -> ThrowingLongPredicate.invokeAndUnwrap(predicate, 1L, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            LongPredicate predicate = l -> l % 2 != 0;

            assertTrue(ThrowingLongPredicate.invokeAndUnwrap(predicate, 1L, IOException.class));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                LongPredicate predicate = l -> {
                    throw UncheckedException.withoutStackTrace(new IOException(Long.toString(l)));
                };

                IOException thrown = assertThrows(IOException.class, () -> ThrowingLongPredicate.invokeAndUnwrap(predicate, 1L, IOException.class));
                assertEquals("1", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                LongPredicate predicate = l -> {
                    throw UncheckedException.withoutStackTrace(new FileNotFoundException(Long.toString(l)));
                };

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class,
                        () -> ThrowingLongPredicate.invokeAndUnwrap(predicate, 1L, IOException.class));
                assertEquals("1", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                LongPredicate predicate = l -> {
                    throw UncheckedException.withoutStackTrace(new ParseException(Long.toString(l), 0));
                };

                UncheckedException thrown = assertThrows(UncheckedException.class,
                        () -> ThrowingLongPredicate.invokeAndUnwrap(predicate, 1L, IOException.class));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("1", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            LongPredicate predicate = l -> {
                throw new IllegalStateException(Long.toString(l));
            };

            IllegalStateException thrown = assertThrows(IllegalStateException.class,
                    () -> ThrowingLongPredicate.invokeAndUnwrap(predicate, 1L, IOException.class));
            assertEquals("1", thrown.getMessage());
        }
    }
}
