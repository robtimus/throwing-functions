/*
 * ThrowingDoublePredicateTest.java
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
import java.util.function.DoublePredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

@SuppressWarnings("nls")
class ThrowingDoublePredicateTest {

    @Nested
    class And {

        @Test
        void testNullArgument() {
            ThrowingDoublePredicate<IOException> predicate = Spied.throwingDoublePredicate(d -> d % 2 != 0);

            assertThrows(NullPointerException.class, () -> predicate.and(null));
        }

        @Nested
        class NeitherThrows {

            @Test
            void testThisMatches() throws IOException {
                ThrowingDoublePredicate<IOException> predicate = Spied.throwingDoublePredicate(d -> d % 2 != 0);
                ThrowingDoublePredicate<IOException> other = Spied.throwingDoublePredicate(d -> d % 2 == 0);

                ThrowingDoublePredicate<IOException> composed = predicate.and(other);

                assertFalse(composed.test(1D));

                verify(predicate).test(1D);
                verify(predicate).and(other);
                verify(other).test(1D);
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testOtherMatches() throws IOException {
                ThrowingDoublePredicate<IOException> predicate = Spied.throwingDoublePredicate(d -> d % 2 == 0);
                ThrowingDoublePredicate<IOException> other = Spied.throwingDoublePredicate(d -> d % 2 != 0);

                ThrowingDoublePredicate<IOException> composed = predicate.and(other);

                assertFalse(composed.test(1D));

                verify(predicate).test(1D);
                verify(predicate).and(other);
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testBothMatch() throws IOException {
                ThrowingDoublePredicate<IOException> predicate = Spied.throwingDoublePredicate(d -> d % 2 != 0);
                ThrowingDoublePredicate<IOException> other = Spied.throwingDoublePredicate(d -> d > 0);

                ThrowingDoublePredicate<IOException> composed = predicate.and(other);

                assertTrue(composed.test(1D));

                verify(predicate).test(1D);
                verify(predicate).and(other);
                verify(other).test(1D);
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testNeitherMatches() throws IOException {
                ThrowingDoublePredicate<IOException> predicate = Spied.throwingDoublePredicate(d -> d % 2 != 0);
                ThrowingDoublePredicate<IOException> other = Spied.throwingDoublePredicate(d -> d > 0);

                ThrowingDoublePredicate<IOException> composed = predicate.and(other);

                assertFalse(composed.test(0D));

                verify(predicate).test(0D);
                verify(predicate).and(other);
                verifyNoMoreInteractions(predicate, other);
            }
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testOtherMatches() throws IOException {
                ThrowingDoublePredicate<IOException> predicate = Spied.throwingDoublePredicate(d -> {
                    throw new IOException(Double.toString(d));
                });
                ThrowingDoublePredicate<IOException> other = Spied.throwingDoublePredicate(d -> d % 2 != 0);

                ThrowingDoublePredicate<IOException> composed = predicate.and(other);

                IOException thrown = assertThrows(IOException.class, () -> composed.test(1D));
                assertEquals("1.0", thrown.getMessage());

                verify(predicate).test(1D);
                verify(predicate).and(other);
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testOtherDoesNotMatch() throws IOException {
                ThrowingDoublePredicate<IOException> predicate = Spied.throwingDoublePredicate(d -> {
                    throw new IOException(Double.toString(d));
                });
                ThrowingDoublePredicate<IOException> other = Spied.throwingDoublePredicate(d -> d % 2 == 0);

                ThrowingDoublePredicate<IOException> composed = predicate.and(other);

                IOException thrown = assertThrows(IOException.class, () -> composed.test(1D));
                assertEquals("1.0", thrown.getMessage());

                verify(predicate).test(1D);
                verify(predicate).and(other);
                verifyNoMoreInteractions(predicate, other);
            }
        }

        @Nested
        class ThisThrowsUnchecked {

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testOtherMatches(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingDoublePredicate<IOException> predicate = Spied.throwingDoublePredicate(throwable::throwUnchecked);
                ThrowingDoublePredicate<IOException> other = Spied.throwingDoublePredicate(d -> d % 2 != 0);

                ThrowingDoublePredicate<IOException> composed = predicate.and(other);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> composed.test(1D));
                assertEquals("1.0", thrown.getMessage());

                verify(predicate).test(1D);
                verify(predicate).and(other);
                verifyNoMoreInteractions(predicate, other);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testOtherDoesNotMatch(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingDoublePredicate<IOException> predicate = Spied.throwingDoublePredicate(throwable::throwUnchecked);
                ThrowingDoublePredicate<IOException> other = Spied.throwingDoublePredicate(d -> d % 2 == 0);

                ThrowingDoublePredicate<IOException> composed = predicate.and(other);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> composed.test(1D));
                assertEquals("1.0", thrown.getMessage());

                verify(predicate).test(1D);
                verify(predicate).and(other);
                verifyNoMoreInteractions(predicate, other);
            }
        }

        @Nested
        class OtherThrowsChecked {

            @Test
            void testThisMatches() throws IOException {
                ThrowingDoublePredicate<IOException> predicate = Spied.throwingDoublePredicate(d -> d % 2 != 0);
                ThrowingDoublePredicate<IOException> other = Spied.throwingDoublePredicate(d -> {
                    throw new IOException(Double.toString(d));
                });

                ThrowingDoublePredicate<IOException> composed = predicate.and(other);

                IOException thrown = assertThrows(IOException.class, () -> composed.test(1D));
                assertEquals("1.0", thrown.getMessage());

                verify(predicate).test(1D);
                verify(predicate).and(other);
                verify(other).test(1D);
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testThisDoesNotMatch() throws IOException {
                ThrowingDoublePredicate<IOException> predicate = Spied.throwingDoublePredicate(d -> d % 2 == 0);
                ThrowingDoublePredicate<IOException> other = Spied.throwingDoublePredicate(d -> {
                    throw new IOException(Double.toString(d));
                });

                ThrowingDoublePredicate<IOException> composed = predicate.and(other);

                assertFalse(composed.test(1D));

                verify(predicate).test(1D);
                verify(predicate).and(other);
                verifyNoMoreInteractions(predicate, other);
            }
        }

        @Nested
        class OtherThrowsUnchecked {

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testThisMatches(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingDoublePredicate<IOException> predicate = Spied.throwingDoublePredicate(d -> d % 2 != 0);
                ThrowingDoublePredicate<IOException> other = Spied.throwingDoublePredicate(throwable::throwUnchecked);

                ThrowingDoublePredicate<IOException> composed = predicate.and(other);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> composed.test(1D));
                assertEquals("1.0", thrown.getMessage());

                verify(predicate).test(1D);
                verify(predicate).and(other);
                verify(other).test(1D);
                verifyNoMoreInteractions(predicate, other);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testThisDoesNotMatch(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingDoublePredicate<IOException> predicate = Spied.throwingDoublePredicate(d -> d % 2 == 0);
                ThrowingDoublePredicate<IOException> other = Spied.throwingDoublePredicate(throwable::throwUnchecked);

                ThrowingDoublePredicate<IOException> composed = predicate.and(other);

                assertFalse(composed.test(1D));

                verify(predicate).test(1D);
                verify(predicate).and(other);
                verifyNoMoreInteractions(predicate, other);
            }
        }
    }

    @Nested
    class Negate {

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingDoublePredicate<IOException> predicate = Spied.throwingDoublePredicate(d -> d % 2 != 0);

            ThrowingDoublePredicate<IOException> negated = predicate.negate();

            assertFalse(negated.test(1D));
            assertTrue(negated.test(0D));

            verify(predicate).test(1D);
            verify(predicate).test(0D);
            verify(predicate).negate();
            verifyNoMoreInteractions(predicate);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingDoublePredicate<IOException> predicate = Spied.throwingDoublePredicate(d -> {
                throw new IOException(Double.toString(d));
            });

            ThrowingDoublePredicate<IOException> negated = predicate.negate();

            IOException thrown = assertThrows(IOException.class, () -> negated.test(1D));
            assertEquals("1.0", thrown.getMessage());

            verify(predicate).test(1D);
            verify(predicate).negate();
            verifyNoMoreInteractions(predicate);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingDoublePredicate<IOException> predicate = Spied.throwingDoublePredicate(throwable::throwUnchecked);

            ThrowingDoublePredicate<IOException> negated = predicate.negate();

            Throwable thrown = assertThrows(throwable.throwableType(), () -> negated.test(1D));
            assertEquals("1.0", thrown.getMessage());

            verify(predicate).test(1D);
            verify(predicate).negate();
            verifyNoMoreInteractions(predicate);
        }
    }

    @Nested
    class Or {

        @Test
        void testNullArgument() {
            ThrowingDoublePredicate<IOException> predicate = Spied.throwingDoublePredicate(d -> d % 2 != 0);

            assertThrows(NullPointerException.class, () -> predicate.or(null));
        }

        @Nested
        class NeitherThrows {

            @Test
            void testThisMatches() throws IOException {
                ThrowingDoublePredicate<IOException> predicate = Spied.throwingDoublePredicate(d -> d % 2 != 0);
                ThrowingDoublePredicate<IOException> other = Spied.throwingDoublePredicate(d -> d % 2 == 0);

                ThrowingDoublePredicate<IOException> composed = predicate.or(other);

                assertTrue(composed.test(1D));

                verify(predicate).test(1D);
                verify(predicate).or(other);
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testOtherMatches() throws IOException {
                ThrowingDoublePredicate<IOException> predicate = Spied.throwingDoublePredicate(d -> d % 2 == 0);
                ThrowingDoublePredicate<IOException> other = Spied.throwingDoublePredicate(d -> d % 2 != 0);

                ThrowingDoublePredicate<IOException> composed = predicate.or(other);

                assertTrue(composed.test(1D));

                verify(predicate).test(1D);
                verify(predicate).or(other);
                verify(other).test(1D);
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testBothMatch() throws IOException {
                ThrowingDoublePredicate<IOException> predicate = Spied.throwingDoublePredicate(d -> d % 2 != 0);
                ThrowingDoublePredicate<IOException> other = Spied.throwingDoublePredicate(d -> d > 0);

                ThrowingDoublePredicate<IOException> composed = predicate.or(other);

                assertTrue(composed.test(1D));

                verify(predicate).test(1D);
                verify(predicate).or(other);
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testNeitherMatches() throws IOException {
                ThrowingDoublePredicate<IOException> predicate = Spied.throwingDoublePredicate(d -> d % 2 != 0);
                ThrowingDoublePredicate<IOException> other = Spied.throwingDoublePredicate(d -> d > 0);

                ThrowingDoublePredicate<IOException> composed = predicate.or(other);

                assertFalse(composed.test(0D));

                verify(predicate).test(0D);
                verify(predicate).or(other);
                verify(other).test(0D);
                verifyNoMoreInteractions(predicate, other);
            }
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testOtherMatches() throws IOException {
                ThrowingDoublePredicate<IOException> predicate = Spied.throwingDoublePredicate(d -> {
                    throw new IOException(Double.toString(d));
                });
                ThrowingDoublePredicate<IOException> other = Spied.throwingDoublePredicate(d -> d % 2 != 0);

                ThrowingDoublePredicate<IOException> composed = predicate.or(other);

                IOException thrown = assertThrows(IOException.class, () -> composed.test(1D));
                assertEquals("1.0", thrown.getMessage());

                verify(predicate).test(1D);
                verify(predicate).or(other);
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testOtherDoesNotMatch() throws IOException {
                ThrowingDoublePredicate<IOException> predicate = Spied.throwingDoublePredicate(d -> {
                    throw new IOException(Double.toString(d));
                });
                ThrowingDoublePredicate<IOException> other = Spied.throwingDoublePredicate(d -> d % 2 == 0);

                ThrowingDoublePredicate<IOException> composed = predicate.or(other);

                IOException thrown = assertThrows(IOException.class, () -> composed.test(1D));
                assertEquals("1.0", thrown.getMessage());

                verify(predicate).test(1D);
                verify(predicate).or(other);
                verifyNoMoreInteractions(predicate, other);
            }
        }

        @Nested
        class ThisThrowsUnchecked {

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testOtherMatches(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingDoublePredicate<IOException> predicate = Spied.throwingDoublePredicate(throwable::throwUnchecked);
                ThrowingDoublePredicate<IOException> other = Spied.throwingDoublePredicate(d -> d % 2 != 0);

                ThrowingDoublePredicate<IOException> composed = predicate.or(other);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> composed.test(1D));
                assertEquals("1.0", thrown.getMessage());

                verify(predicate).test(1D);
                verify(predicate).or(other);
                verifyNoMoreInteractions(predicate, other);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testOtherDoesNotMatch(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingDoublePredicate<IOException> predicate = Spied.throwingDoublePredicate(throwable::throwUnchecked);
                ThrowingDoublePredicate<IOException> other = Spied.throwingDoublePredicate(d -> d % 2 == 0);

                ThrowingDoublePredicate<IOException> composed = predicate.or(other);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> composed.test(1D));
                assertEquals("1.0", thrown.getMessage());

                verify(predicate).test(1D);
                verify(predicate).or(other);
                verifyNoMoreInteractions(predicate, other);
            }
        }

        @Nested
        class OtherThrowsChecked {

            @Test
            void testThisMatches() throws IOException {
                ThrowingDoublePredicate<IOException> predicate = Spied.throwingDoublePredicate(d -> d % 2 != 0);
                ThrowingDoublePredicate<IOException> other = Spied.throwingDoublePredicate(d -> {
                    throw new IOException(Double.toString(d));
                });

                ThrowingDoublePredicate<IOException> composed = predicate.or(other);

                assertTrue(composed.test(1D));

                verify(predicate).test(1D);
                verify(predicate).or(other);
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testThisDoesNotMatch() throws IOException {
                ThrowingDoublePredicate<IOException> predicate = Spied.throwingDoublePredicate(d -> d % 2 == 0);
                ThrowingDoublePredicate<IOException> other = Spied.throwingDoublePredicate(d -> {
                    throw new IOException(Double.toString(d));
                });

                ThrowingDoublePredicate<IOException> composed = predicate.or(other);

                IOException thrown = assertThrows(IOException.class, () -> composed.test(1D));
                assertEquals("1.0", thrown.getMessage());

                verify(predicate).test(1D);
                verify(predicate).or(other);
                verify(other).test(1D);
                verifyNoMoreInteractions(predicate, other);
            }
        }

        @Nested
        class OtherThrowsUnchecked {

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testThisMatches(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingDoublePredicate<IOException> predicate = Spied.throwingDoublePredicate(d -> d % 2 != 0);
                ThrowingDoublePredicate<IOException> other = Spied.throwingDoublePredicate(throwable::throwUnchecked);

                ThrowingDoublePredicate<IOException> composed = predicate.or(other);

                assertTrue(composed.test(1D));

                verify(predicate).test(1D);
                verify(predicate).or(other);
                verifyNoMoreInteractions(predicate, other);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testThisDoesNotMatch(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingDoublePredicate<IOException> predicate = Spied.throwingDoublePredicate(d -> d % 2 == 0);
                ThrowingDoublePredicate<IOException> other = Spied.throwingDoublePredicate(throwable::throwUnchecked);

                ThrowingDoublePredicate<IOException> composed = predicate.or(other);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> composed.test(1D));
                assertEquals("1.0", thrown.getMessage());

                verify(predicate).test(1D);
                verify(predicate).or(other);
                verify(other).test(1D);
                verifyNoMoreInteractions(predicate, other);
            }
        }
    }

    @Nested
    class OnErrorThrowAsChecked {

        @Test
        void testNullArgument() {
            ThrowingDoublePredicate<IOException> predicate = Spied.throwingDoublePredicate(d -> d % 2 != 0);

            assertThrows(NullPointerException.class, () -> predicate.onErrorThrowAsChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingDoublePredicate<IOException> predicate = Spied.throwingDoublePredicate(d -> d % 2 != 0);

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingDoublePredicate<ExecutionException> throwing = predicate.onErrorThrowAsChecked(errorMapper);

            assertTrue(throwing.test(1D));

            verify(predicate).test(1D);
            verify(predicate).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(predicate, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingDoublePredicate<IOException> predicate = Spied.throwingDoublePredicate(d -> {
                throw new IOException(Double.toString(d));
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingDoublePredicate<ExecutionException> throwing = predicate.onErrorThrowAsChecked(errorMapper);

            ExecutionException thrown = assertThrows(ExecutionException.class, () -> throwing.test(1D));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1.0", cause.getMessage());

            verify(predicate).test(1D);
            verify(predicate).onErrorThrowAsChecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(predicate, errorMapper);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingDoublePredicate<IOException> predicate = Spied.throwingDoublePredicate(throwable::throwUnchecked);

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingDoublePredicate<ExecutionException> throwing = predicate.onErrorThrowAsChecked(errorMapper);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> throwing.test(1D));
            assertEquals("1.0", thrown.getMessage());

            verify(predicate).test(1D);
            verify(predicate).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(predicate, errorMapper);
        }
    }

    @Nested
    class OnErrorThrowAsUnchecked {

        @Test
        void testNullArgument() {
            ThrowingDoublePredicate<IOException> predicate = Spied.throwingDoublePredicate(d -> d % 2 != 0);

            assertThrows(NullPointerException.class, () -> predicate.onErrorThrowAsUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingDoublePredicate<IOException> predicate = Spied.throwingDoublePredicate(d -> d % 2 != 0);

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            DoublePredicate throwing = predicate.onErrorThrowAsUnchecked(errorMapper);

            assertTrue(throwing.test(1D));

            verify(predicate).test(1D);
            verify(predicate).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(predicate, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingDoublePredicate<IOException> predicate = Spied.throwingDoublePredicate(d -> {
                throw new IOException(Double.toString(d));
            });

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            DoublePredicate throwing = predicate.onErrorThrowAsUnchecked(errorMapper);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> throwing.test(1D));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1.0", cause.getMessage());

            verify(predicate).test(1D);
            verify(predicate).onErrorThrowAsUnchecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(predicate, errorMapper);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingDoublePredicate<IOException> predicate = Spied.throwingDoublePredicate(throwable::throwUnchecked);

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            DoublePredicate throwing = predicate.onErrorThrowAsUnchecked(errorMapper);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> throwing.test(1D));
            assertEquals("1.0", thrown.getMessage());

            verify(predicate).test(1D);
            verify(predicate).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(predicate, errorMapper);
        }
    }

    @Nested
    class OnErrorHandleChecked {

        @Test
        void testNullArgument() {
            ThrowingDoublePredicate<IOException> predicate = Spied.throwingDoublePredicate(d -> d % 2 != 0);

            assertThrows(NullPointerException.class, () -> predicate.onErrorHandleChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingDoublePredicate<IOException> predicate = Spied.throwingDoublePredicate(d -> d % 2 != 0);

            ThrowingPredicate<IOException, ExecutionException> errorHandler = Spied.throwingPredicate(e -> e.getMessage() == null);

            ThrowingDoublePredicate<ExecutionException> handling = predicate.onErrorHandleChecked(errorHandler);

            assertTrue(handling.test(1D));

            verify(predicate).test(1D);
            verify(predicate).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(predicate, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException, ExecutionException {
                ThrowingDoublePredicate<IOException> predicate = Spied.throwingDoublePredicate(d -> {
                    throw new IOException(Double.toString(d));
                });

                ThrowingPredicate<IOException, ExecutionException> errorHandler = Spied.throwingPredicate(e -> e.getMessage() == null);

                ThrowingDoublePredicate<ExecutionException> handling = predicate.onErrorHandleChecked(errorHandler);

                assertFalse(handling.test(1D));

                verify(predicate).test(1D);
                verify(predicate).onErrorHandleChecked(errorHandler);
                verify(errorHandler).test(any());
                verifyNoMoreInteractions(predicate, errorHandler);
            }

            @Test
            void testHandlerThrowsChecked() throws IOException, ExecutionException {
                ThrowingDoublePredicate<IOException> predicate = Spied.throwingDoublePredicate(d -> {
                    throw new IOException(Double.toString(d));
                });

                ThrowingPredicate<IOException, ExecutionException> errorHandler = Spied.throwingPredicate(e -> {
                    throw new ExecutionException(e);
                });

                ThrowingDoublePredicate<ExecutionException> handling = predicate.onErrorHandleChecked(errorHandler);

                ExecutionException thrown = assertThrows(ExecutionException.class, () -> handling.test(1D));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("1.0", cause.getMessage());

                verify(predicate).test(1D);
                verify(predicate).onErrorHandleChecked(errorHandler);
                verify(errorHandler).test(any());
                verifyNoMoreInteractions(predicate, errorHandler);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testHandlerThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ExecutionException {
                ThrowingDoublePredicate<IOException> predicate = Spied.throwingDoublePredicate(d -> {
                    throw new IOException(Double.toString(d));
                });

                ThrowingPredicate<IOException, ExecutionException> errorHandler = Spied.throwingPredicate(throwable::throwUnchecked);

                ThrowingDoublePredicate<ExecutionException> handling = predicate.onErrorHandleChecked(errorHandler);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.test(1D));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("1.0", cause.getMessage());

                verify(predicate).test(1D);
                verify(predicate).onErrorHandleChecked(errorHandler);
                verify(errorHandler).test(any());
                verifyNoMoreInteractions(predicate, errorHandler);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingDoublePredicate<IOException> predicate = Spied.throwingDoublePredicate(throwable::throwUnchecked);

            ThrowingPredicate<IOException, ExecutionException> errorHandler = Spied.throwingPredicate(e -> e.getMessage() == null);

            ThrowingDoublePredicate<ExecutionException> handling = predicate.onErrorHandleChecked(errorHandler);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.test(1D));
            assertEquals("1.0", thrown.getMessage());

            verify(predicate).test(1D);
            verify(predicate).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(predicate, errorHandler);
        }
    }

    @Nested
    class OnErrorHandleUnchecked {

        @Test
        void testNullArgument() {
            ThrowingDoublePredicate<IOException> predicate = Spied.throwingDoublePredicate(d -> d % 2 != 0);

            assertThrows(NullPointerException.class, () -> predicate.onErrorHandleUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingDoublePredicate<IOException> predicate = Spied.throwingDoublePredicate(d -> d % 2 != 0);

            Predicate<IOException> errorHandler = Spied.predicate(e -> e.getMessage() == null);

            DoublePredicate handling = predicate.onErrorHandleUnchecked(errorHandler);

            assertTrue(handling.test(1D));

            verify(predicate).test(1D);
            verify(predicate).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(predicate, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException {
                ThrowingDoublePredicate<IOException> predicate = Spied.throwingDoublePredicate(d -> {
                    throw new IOException(Double.toString(d));
                });

                Predicate<IOException> errorHandler = Spied.predicate(e -> e.getMessage() == null);

                DoublePredicate handling = predicate.onErrorHandleUnchecked(errorHandler);

                assertFalse(handling.test(1D));

                verify(predicate).test(1D);
                verify(predicate).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).test(any());
                verifyNoMoreInteractions(predicate, errorHandler);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testHandlerThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingDoublePredicate<IOException> predicate = Spied.throwingDoublePredicate(d -> {
                    throw new IOException(Double.toString(d));
                });

                Predicate<IOException> errorHandler = Spied.predicate(throwable::throwUnchecked);

                DoublePredicate handling = predicate.onErrorHandleUnchecked(errorHandler);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.test(1D));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("1.0", cause.getMessage());

                verify(predicate).test(1D);
                verify(predicate).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).test(any());
                verifyNoMoreInteractions(predicate, errorHandler);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingDoublePredicate<IOException> predicate = Spied.throwingDoublePredicate(throwable::throwUnchecked);

            Predicate<IOException> errorHandler = Spied.predicate(e -> e.getMessage() == null);

            DoublePredicate handling = predicate.onErrorHandleUnchecked(errorHandler);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.test(1D));
            assertEquals("1.0", thrown.getMessage());

            verify(predicate).test(1D);
            verify(predicate).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(predicate, errorHandler);
        }
    }

    @Nested
    class OnErrorTestChecked {

        @Test
        void testNullArgument() {
            ThrowingDoublePredicate<IOException> predicate = Spied.throwingDoublePredicate(d -> d % 2 != 0);

            assertThrows(NullPointerException.class, () -> predicate.onErrorTestChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingDoublePredicate<IOException> predicate = Spied.throwingDoublePredicate(d -> d % 2 != 0);

            ThrowingDoublePredicate<ExecutionException> fallback = Spied.throwingDoublePredicate(d -> d % 2 == 0);

            ThrowingDoublePredicate<ExecutionException> testing = predicate.onErrorTestChecked(fallback);

            assertTrue(testing.test(1D));

            verify(predicate).test(1D);
            verify(predicate).onErrorTestChecked(fallback);
            verifyNoMoreInteractions(predicate, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                ThrowingDoublePredicate<IOException> predicate = Spied.throwingDoublePredicate(d -> {
                    throw new IOException(Double.toString(d));
                });

                ThrowingDoublePredicate<ParseException> fallback = Spied.throwingDoublePredicate(d -> d % 2 == 0);

                ThrowingDoublePredicate<ParseException> testing = predicate.onErrorTestChecked(fallback);

                assertFalse(testing.test(1D));

                verify(predicate).test(1D);
                verify(predicate).onErrorTestChecked(fallback);
                verify(fallback).test(1D);
                verifyNoMoreInteractions(predicate, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                ThrowingDoublePredicate<IOException> predicate = Spied.throwingDoublePredicate(d -> {
                    throw new IOException(Double.toString(d));
                });

                ThrowingDoublePredicate<ParseException> fallback = Spied.throwingDoublePredicate(d -> {
                    throw new ParseException(Double.toString(d), 0);
                });

                ThrowingDoublePredicate<ParseException> testing = predicate.onErrorTestChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> testing.test(1D));
                assertEquals("1.0", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(predicate).test(1D);
                verify(predicate).onErrorTestChecked(fallback);
                verify(fallback).test(1D);
                verifyNoMoreInteractions(predicate, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ParseException {
                ThrowingDoublePredicate<IOException> predicate = Spied.throwingDoublePredicate(d -> {
                    throw new IOException(Double.toString(d));
                });

                ThrowingDoublePredicate<ParseException> fallback = Spied.throwingDoublePredicate(throwable::throwUnchecked);

                ThrowingDoublePredicate<ParseException> testing = predicate.onErrorTestChecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> testing.test(1D));
                assertEquals("1.0", thrown.getMessage());

                verify(predicate).test(1D);
                verify(predicate).onErrorTestChecked(fallback);
                verify(fallback).test(1D);
                verifyNoMoreInteractions(predicate, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingDoublePredicate<IOException> predicate = Spied.throwingDoublePredicate(throwable::throwUnchecked);

            ThrowingDoublePredicate<ParseException> fallback = Spied.throwingDoublePredicate(d -> d % 2 == 0);

            ThrowingDoublePredicate<ParseException> testing = predicate.onErrorTestChecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> testing.test(1D));
            assertEquals("1.0", thrown.getMessage());

            verify(predicate).test(1D);
            verify(predicate).onErrorTestChecked(fallback);
            verifyNoMoreInteractions(predicate, fallback);
        }
    }

    @Nested
    class OnErrorTestUnchecked {

        @Test
        void testNullArgument() {
            ThrowingDoublePredicate<IOException> predicate = Spied.throwingDoublePredicate(d -> d % 2 != 0);

            assertThrows(NullPointerException.class, () -> predicate.onErrorTestUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingDoublePredicate<IOException> predicate = Spied.throwingDoublePredicate(d -> d % 2 != 0);

            DoublePredicate fallback = Spied.doublePredicate(d -> d % 2 == 0);

            DoublePredicate testing = predicate.onErrorTestUnchecked(fallback);

            assertTrue(testing.test(1D));

            verify(predicate).test(1D);
            verify(predicate).onErrorTestUnchecked(fallback);
            verifyNoMoreInteractions(predicate, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                ThrowingDoublePredicate<IOException> predicate = Spied.throwingDoublePredicate(d -> {
                    throw new IOException(Double.toString(d));
                });

                DoublePredicate fallback = Spied.doublePredicate(d -> d % 2 == 0);

                DoublePredicate testing = predicate.onErrorTestUnchecked(fallback);

                assertFalse(testing.test(1D));

                verify(predicate).test(1D);
                verify(predicate).onErrorTestUnchecked(fallback);
                verify(fallback).test(1D);
                verifyNoMoreInteractions(predicate, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingDoublePredicate<IOException> predicate = Spied.throwingDoublePredicate(d -> {
                    throw new IOException(Double.toString(d));
                });

                DoublePredicate fallback = Spied.doublePredicate(throwable::throwUnchecked);

                DoublePredicate testing = predicate.onErrorTestUnchecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> testing.test(1D));
                assertEquals("1.0", thrown.getMessage());

                verify(predicate).test(1.0);
                verify(predicate).onErrorTestUnchecked(fallback);
                verify(fallback).test(1.0);
                verifyNoMoreInteractions(predicate, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingDoublePredicate<IOException> predicate = Spied.throwingDoublePredicate(throwable::throwUnchecked);

            DoublePredicate fallback = Spied.doublePredicate(d -> d % 2 == 0);

            DoublePredicate testing = predicate.onErrorTestUnchecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> testing.test(1D));
            assertEquals("1.0", thrown.getMessage());

            verify(predicate).test(1D);
            verify(predicate).onErrorTestUnchecked(fallback);
            verifyNoMoreInteractions(predicate, fallback);
        }
    }

    @Nested
    class OnErrorGetCheckedAsBoolean {

        @Test
        void testNullArgument() {
            ThrowingDoublePredicate<IOException> predicate = Spied.throwingDoublePredicate(d -> d % 2 != 0);

            assertThrows(NullPointerException.class, () -> predicate.onErrorGetCheckedAsBoolean(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingDoublePredicate<IOException> predicate = Spied.throwingDoublePredicate(d -> d % 2 != 0);

            ThrowingBooleanSupplier<ExecutionException> fallback = Spied.throwingBooleanSupplier(() -> false);

            ThrowingDoublePredicate<ExecutionException> getting = predicate.onErrorGetCheckedAsBoolean(fallback);

            assertTrue(getting.test(1D));

            verify(predicate).test(1D);
            verify(predicate).onErrorGetCheckedAsBoolean(fallback);
            verifyNoMoreInteractions(predicate, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                ThrowingDoublePredicate<IOException> predicate = Spied.throwingDoublePredicate(d -> {
                    throw new IOException(Double.toString(d));
                });

                ThrowingBooleanSupplier<ParseException> fallback = Spied.throwingBooleanSupplier(() -> false);

                ThrowingDoublePredicate<ParseException> getting = predicate.onErrorGetCheckedAsBoolean(fallback);

                assertFalse(getting.test(1D));

                verify(predicate).test(1D);
                verify(predicate).onErrorGetCheckedAsBoolean(fallback);
                verify(fallback).getAsBoolean();
                verifyNoMoreInteractions(predicate, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                ThrowingDoublePredicate<IOException> predicate = Spied.throwingDoublePredicate(d -> {
                    throw new IOException(Double.toString(d));
                });

                ThrowingBooleanSupplier<ParseException> fallback = Spied.throwingBooleanSupplier(() -> {
                    throw new ParseException("bar", 0);
                });

                ThrowingDoublePredicate<ParseException> getting = predicate.onErrorGetCheckedAsBoolean(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> getting.test(1D));
                assertEquals("bar", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(predicate).test(1D);
                verify(predicate).onErrorGetCheckedAsBoolean(fallback);
                verify(fallback).getAsBoolean();
                verifyNoMoreInteractions(predicate, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ParseException {
                ThrowingDoublePredicate<IOException> predicate = Spied.throwingDoublePredicate(d -> {
                    throw new IOException(Double.toString(d));
                });

                ThrowingBooleanSupplier<ParseException> fallback = Spied.throwingBooleanSupplier(() -> throwable.throwUnchecked("bar"));

                ThrowingDoublePredicate<ParseException> getting = predicate.onErrorGetCheckedAsBoolean(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.test(1D));
                assertEquals("bar", thrown.getMessage());

                verify(predicate).test(1D);
                verify(predicate).onErrorGetCheckedAsBoolean(fallback);
                verify(fallback).getAsBoolean();
                verifyNoMoreInteractions(predicate, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingDoublePredicate<IOException> predicate = Spied.throwingDoublePredicate(throwable::throwUnchecked);

            ThrowingBooleanSupplier<ParseException> fallback = Spied.throwingBooleanSupplier(() -> false);

            ThrowingDoublePredicate<ParseException> getting = predicate.onErrorGetCheckedAsBoolean(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.test(1D));
            assertEquals("1.0", thrown.getMessage());

            verify(predicate).test(1D);
            verify(predicate).onErrorGetCheckedAsBoolean(fallback);
            verifyNoMoreInteractions(predicate, fallback);
        }
    }

    @Nested
    class OnErrorGetUncheckedAsBoolean {

        @Test
        void testNullArgument() {
            ThrowingDoublePredicate<IOException> predicate = Spied.throwingDoublePredicate(d -> d % 2 != 0);

            assertThrows(NullPointerException.class, () -> predicate.onErrorGetUncheckedAsBoolean(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingDoublePredicate<IOException> predicate = Spied.throwingDoublePredicate(d -> d % 2 != 0);

            BooleanSupplier fallback = Spied.booleanSupplier(() -> false);

            DoublePredicate getting = predicate.onErrorGetUncheckedAsBoolean(fallback);

            assertTrue(getting.test(1D));

            verify(predicate).test(1D);
            verify(predicate).onErrorGetUncheckedAsBoolean(fallback);
            verifyNoMoreInteractions(predicate, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                ThrowingDoublePredicate<IOException> predicate = Spied.throwingDoublePredicate(d -> {
                    throw new IOException(Double.toString(d));
                });

                BooleanSupplier fallback = Spied.booleanSupplier(() -> false);

                DoublePredicate getting = predicate.onErrorGetUncheckedAsBoolean(fallback);

                assertFalse(getting.test(1D));

                verify(predicate).test(1D);
                verify(predicate).onErrorGetUncheckedAsBoolean(fallback);
                verify(fallback).getAsBoolean();
                verifyNoMoreInteractions(predicate, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingDoublePredicate<IOException> predicate = Spied.throwingDoublePredicate(d -> {
                    throw new IOException(Double.toString(d));
                });

                BooleanSupplier fallback = Spied.booleanSupplier(() -> throwable.throwUnchecked("bar"));

                DoublePredicate getting = predicate.onErrorGetUncheckedAsBoolean(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.test(1D));
                assertEquals("bar", thrown.getMessage());

                verify(predicate).test(1D);
                verify(predicate).onErrorGetUncheckedAsBoolean(fallback);
                verify(fallback).getAsBoolean();
                verifyNoMoreInteractions(predicate, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingDoublePredicate<IOException> predicate = Spied.throwingDoublePredicate(throwable::throwUnchecked);

            BooleanSupplier fallback = Spied.booleanSupplier(() -> false);

            DoublePredicate getting = predicate.onErrorGetUncheckedAsBoolean(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.test(1D));
            assertEquals("1.0", thrown.getMessage());

            verify(predicate).test(1D);
            verify(predicate).onErrorGetUncheckedAsBoolean(fallback);
            verifyNoMoreInteractions(predicate, fallback);
        }
    }

    @Nested
    class OnErrorReturn {

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingDoublePredicate<IOException> predicate = Spied.throwingDoublePredicate(d -> d % 2 != 0);

            DoublePredicate returning = predicate.onErrorReturn(false);

            assertTrue(returning.test(1D));

            verify(predicate).test(1D);
            verify(predicate).onErrorReturn(false);
            verifyNoMoreInteractions(predicate);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingDoublePredicate<IOException> predicate = Spied.throwingDoublePredicate(d -> {
                throw new IOException(Double.toString(d));
            });

            DoublePredicate returning = predicate.onErrorReturn(false);

            assertFalse(returning.test(1D));

            verify(predicate).test(1D);
            verify(predicate).onErrorReturn(false);
            verifyNoMoreInteractions(predicate);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingDoublePredicate<IOException> predicate = Spied.throwingDoublePredicate(throwable::throwUnchecked);

            DoublePredicate returning = predicate.onErrorReturn(false);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> returning.test(1D));
            assertEquals("1.0", thrown.getMessage());

            verify(predicate).test(1D);
            verify(predicate).onErrorReturn(false);
            verifyNoMoreInteractions(predicate);
        }
    }

    @Nested
    class Unchecked {

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingDoublePredicate<IOException> predicate = Spied.throwingDoublePredicate(d -> d % 2 != 0);

            DoublePredicate unchecked = predicate.unchecked();

            assertTrue(unchecked.test(1D));

            verify(predicate).test(1D);
            verify(predicate).unchecked();
            verify(predicate).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(predicate);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingDoublePredicate<IOException> predicate = Spied.throwingDoublePredicate(d -> {
                throw new IOException(Double.toString(d));
            });

            DoublePredicate unchecked = predicate.unchecked();

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.test(1D));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1.0", cause.getMessage());

            verify(predicate).test(1D);
            verify(predicate).unchecked();
            verify(predicate).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(predicate);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingDoublePredicate<IOException> predicate = Spied.throwingDoublePredicate(throwable::throwUnchecked);

            DoublePredicate unchecked = predicate.unchecked();

            Throwable thrown = assertThrows(throwable.throwableType(), () -> unchecked.test(1D));
            assertEquals("1.0", thrown.getMessage());

            verify(predicate).test(1D);
            verify(predicate).unchecked();
            verify(predicate).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(predicate);
        }
    }

    @Nested
    class Of {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> ThrowingDoublePredicate.of(null));
        }

        @Test
        void testNonNullArgument() throws IOException {
            ThrowingDoublePredicate<IOException> predicate = ThrowingDoublePredicate.of(d -> d % 2 != 0);

            assertTrue(predicate.test(1D));
        }
    }

    @Nested
    class Not {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> ThrowingDoublePredicate.not(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ThrowingDoublePredicate<IOException> predicate = Spied.throwingDoublePredicate(d -> d % 2 != 0);

            ThrowingDoublePredicate<IOException> negated = ThrowingDoublePredicate.not(predicate);

            assertFalse(negated.test(1D));
            assertTrue(negated.test(0D));

            verify(predicate).negate();
            verify(predicate).test(1D);
            verify(predicate).test(0D);
            verifyNoMoreInteractions(predicate);
        }

        @Test
        void testArgumentThrowsChecked() throws IOException {
            ThrowingDoublePredicate<IOException> predicate = Spied.throwingDoublePredicate(d -> {
                throw new IOException(Double.toString(d));
            });

            ThrowingDoublePredicate<IOException> negated = ThrowingDoublePredicate.not(predicate);

            IOException thrown = assertThrows(IOException.class, () -> negated.test(1D));
            assertEquals("1.0", thrown.getMessage());

            verify(predicate).negate();
            verify(predicate).test(1D);
            verifyNoMoreInteractions(predicate);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testArgumentThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingDoublePredicate<IOException> predicate = Spied.throwingDoublePredicate(throwable::throwUnchecked);

            ThrowingDoublePredicate<IOException> negated = ThrowingDoublePredicate.not(predicate);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> negated.test(1D));
            assertEquals("1.0", thrown.getMessage());

            verify(predicate).negate();
            verify(predicate).test(1D);
            verifyNoMoreInteractions(predicate);
        }
    }

    @Nested
    class UncheckedFactory {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> ThrowingDoublePredicate.unchecked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ThrowingDoublePredicate<IOException> predicate = Spied.throwingDoublePredicate(d -> d % 2 != 0);

            DoublePredicate unchecked = ThrowingDoublePredicate.unchecked(predicate);

            assertTrue(unchecked.test(1D));

            verify(predicate).unchecked();
            verify(predicate).onErrorThrowAsUnchecked(any());
            verify(predicate).test(1D);
            verifyNoMoreInteractions(predicate);
        }

        @Test
        void testArgumentThrowsChecked() throws IOException {
            ThrowingDoublePredicate<IOException> predicate = Spied.throwingDoublePredicate(d -> {
                throw new IOException(Double.toString(d));
            });

            DoublePredicate unchecked = ThrowingDoublePredicate.unchecked(predicate);

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.test(1D));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1.0", cause.getMessage());

            verify(predicate).unchecked();
            verify(predicate).onErrorThrowAsUnchecked(any());
            verify(predicate).test(1D);
            verifyNoMoreInteractions(predicate);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testArgumentThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingDoublePredicate<IOException> predicate = Spied.throwingDoublePredicate(throwable::throwUnchecked);

            DoublePredicate unchecked = ThrowingDoublePredicate.unchecked(predicate);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> unchecked.test(1D));
            assertEquals("1.0", thrown.getMessage());

            verify(predicate).unchecked();
            verify(predicate).onErrorThrowAsUnchecked(any());
            verify(predicate).test(1D);
            verifyNoMoreInteractions(predicate);
        }
    }

    @Nested
    class CheckedWrapper {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> ThrowingDoublePredicate.checked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ThrowingDoublePredicate<IOException> predicate = ThrowingDoublePredicate.checked(d -> d % 2 != 0);

            assertTrue(predicate.test(1D));
        }

        @Test
        void testArgumentThrowsUnchecked() {
            ThrowingDoublePredicate<IOException> predicate = ThrowingDoublePredicate.checked(d -> {
                throw new UncheckedException(Double.toString(d), new IOException());
            });

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> predicate.test(1D));
            assertEquals("1.0", thrown.getMessage());
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertNull(cause.getMessage());
        }
    }

    @Nested
    class CheckedUnwrapper {

        @Test
        void testNullArguments() {
            DoublePredicate predicate = d -> d % 2 != 0;

            assertThrows(NullPointerException.class, () -> ThrowingDoublePredicate.checked(null, IOException.class));
            assertThrows(NullPointerException.class, () -> ThrowingDoublePredicate.checked(predicate, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ThrowingDoublePredicate<IOException> predicate = ThrowingDoublePredicate.checked(d -> d % 2 != 0, IOException.class);

            assertTrue(predicate.test(1D));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                ThrowingDoublePredicate<IOException> predicate = ThrowingDoublePredicate.checked(d -> {
                    throw new UncheckedException(new IOException(Double.toString(d)));
                }, IOException.class);

                IOException thrown = assertThrows(IOException.class, () -> predicate.test(1D));
                assertEquals("1.0", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                ThrowingDoublePredicate<IOException> predicate = ThrowingDoublePredicate.checked(d -> {
                    throw new UncheckedException(new FileNotFoundException(Double.toString(d)));
                }, IOException.class);

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class, () -> predicate.test(1D));
                assertEquals("1.0", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                ThrowingDoublePredicate<IOException> predicate = ThrowingDoublePredicate.checked(d -> {
                    throw new UncheckedException(new ParseException(Double.toString(d), 0));
                }, IOException.class);

                UncheckedException thrown = assertThrows(UncheckedException.class, () -> predicate.test(1D));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("1.0", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            ThrowingDoublePredicate<IOException> predicate = ThrowingDoublePredicate.checked(d -> {
                throw new IllegalStateException(Double.toString(d));
            }, IOException.class);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> predicate.test(1D));
            assertEquals("1.0", thrown.getMessage());
        }
    }

    @Nested
    class InvokeAndUnwrap {

        @Test
        void testNullArguments() {
            DoublePredicate predicate = Objects::nonNull;

            assertThrows(NullPointerException.class, () -> ThrowingDoublePredicate.invokeAndUnwrap(null, 1D, IOException.class));
            assertThrows(NullPointerException.class, () -> ThrowingDoublePredicate.invokeAndUnwrap(predicate, 1D, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            DoublePredicate predicate = d -> d % 2 != 0;

            assertTrue(ThrowingDoublePredicate.invokeAndUnwrap(predicate, 1D, IOException.class));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                DoublePredicate predicate = d -> {
                    throw new UncheckedException(new IOException(Double.toString(d)));
                };

                IOException thrown = assertThrows(IOException.class, () -> ThrowingDoublePredicate.invokeAndUnwrap(predicate, 1D, IOException.class));
                assertEquals("1.0", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                DoublePredicate predicate = d -> {
                    throw new UncheckedException(new FileNotFoundException(Double.toString(d)));
                };

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class,
                        () -> ThrowingDoublePredicate.invokeAndUnwrap(predicate, 1D, IOException.class));
                assertEquals("1.0", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                DoublePredicate predicate = d -> {
                    throw new UncheckedException(new ParseException(Double.toString(d), 0));
                };

                UncheckedException thrown = assertThrows(UncheckedException.class,
                        () -> ThrowingDoublePredicate.invokeAndUnwrap(predicate, 1D, IOException.class));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("1.0", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            DoublePredicate predicate = d -> {
                throw new IllegalStateException(Double.toString(d));
            };

            IllegalStateException thrown = assertThrows(IllegalStateException.class,
                    () -> ThrowingDoublePredicate.invokeAndUnwrap(predicate, 1D, IOException.class));
            assertEquals("1.0", thrown.getMessage());
        }
    }
}
