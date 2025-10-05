/*
 * ThrowingPredicateTest.java
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
import java.util.function.Predicate;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

@SuppressWarnings("nls")
class ThrowingPredicateTest {

    @Nested
    class And {

        @Test
        void testNullArgument() {
            ThrowingPredicate<String, IOException> predicate = Spied.throwingPredicate(String::isEmpty);

            assertThrows(NullPointerException.class, () -> predicate.and(null));
        }

        @Nested
        class NeitherThrows {

            @Test
            void testThisMatches() throws IOException {
                ThrowingPredicate<String, IOException> predicate = Spied.throwingPredicate(String::isBlank);
                ThrowingPredicate<String, IOException> other = Spied.throwingPredicate(String::isEmpty);

                ThrowingPredicate<String, IOException> composed = predicate.and(other);

                assertFalse(composed.test(" "));

                verify(predicate).test(" ");
                verify(predicate).and(other);
                verify(other).test(" ");
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testOtherMatches() throws IOException {
                ThrowingPredicate<String, IOException> predicate = Spied.throwingPredicate(String::isEmpty);
                ThrowingPredicate<String, IOException> other = Spied.throwingPredicate(String::isBlank);

                ThrowingPredicate<String, IOException> composed = predicate.and(other);

                assertFalse(composed.test(" "));

                verify(predicate).test(" ");
                verify(predicate).and(other);
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testBothMatch() throws IOException {
                ThrowingPredicate<String, IOException> predicate = Spied.throwingPredicate(String::isEmpty);
                ThrowingPredicate<String, IOException> other = Spied.throwingPredicate(String::isBlank);

                ThrowingPredicate<String, IOException> composed = predicate.and(other);

                assertTrue(composed.test(""));

                verify(predicate).test("");
                verify(predicate).and(other);
                verify(other).test("");
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testNeitherMatches() throws IOException {
                ThrowingPredicate<String, IOException> predicate = Spied.throwingPredicate(String::isEmpty);
                ThrowingPredicate<String, IOException> other = Spied.throwingPredicate(String::isBlank);

                ThrowingPredicate<String, IOException> composed = predicate.and(other);

                assertFalse(composed.test("foo"));

                verify(predicate).test("foo");
                verify(predicate).and(other);
                verifyNoMoreInteractions(predicate, other);
            }
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testOtherMatches() throws IOException {
                ThrowingPredicate<String, IOException> predicate = Spied.throwingPredicate(s -> {
                    throw new IOException(s);
                });
                ThrowingPredicate<String, IOException> other = Spied.throwingPredicate(String::isBlank);

                ThrowingPredicate<String, IOException> composed = predicate.and(other);

                IOException thrown = assertThrows(IOException.class, () -> composed.test(" "));
                assertEquals(" ", thrown.getMessage());

                verify(predicate).test(" ");
                verify(predicate).and(other);
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testOtherDoesNotMatch() throws IOException {
                ThrowingPredicate<String, IOException> predicate = Spied.throwingPredicate(s -> {
                    throw new IOException(s);
                });
                ThrowingPredicate<String, IOException> other = Spied.throwingPredicate(String::isBlank);

                ThrowingPredicate<String, IOException> composed = predicate.and(other);

                IOException thrown = assertThrows(IOException.class, () -> composed.test("foo"));
                assertEquals("foo", thrown.getMessage());

                verify(predicate).test("foo");
                verify(predicate).and(other);
                verifyNoMoreInteractions(predicate, other);
            }
        }

        @Nested
        class ThisThrowsUnchecked {

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testOtherMatches(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingPredicate<String, IOException> predicate = Spied.throwingPredicate(throwable::throwUnchecked);
                ThrowingPredicate<String, IOException> other = Spied.throwingPredicate(String::isBlank);

                ThrowingPredicate<String, IOException> composed = predicate.and(other);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> composed.test(" "));
                assertEquals(" ", thrown.getMessage());

                verify(predicate).test(" ");
                verify(predicate).and(other);
                verifyNoMoreInteractions(predicate, other);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testOtherDoesNotMatch(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingPredicate<String, IOException> predicate = Spied.throwingPredicate(throwable::throwUnchecked);
                ThrowingPredicate<String, IOException> other = Spied.throwingPredicate(String::isBlank);

                ThrowingPredicate<String, IOException> composed = predicate.and(other);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> composed.test("foo"));
                assertEquals("foo", thrown.getMessage());

                verify(predicate).test("foo");
                verify(predicate).and(other);
                verifyNoMoreInteractions(predicate, other);
            }
        }

        @Nested
        class OtherThrowsChecked {

            @Test
            void testThisMatches() throws IOException {
                ThrowingPredicate<String, IOException> predicate = Spied.throwingPredicate(String::isBlank);
                ThrowingPredicate<String, IOException> other = Spied.throwingPredicate(s -> {
                    throw new IOException(s);
                });

                ThrowingPredicate<String, IOException> composed = predicate.and(other);

                IOException thrown = assertThrows(IOException.class, () -> composed.test(" "));
                assertEquals(" ", thrown.getMessage());

                verify(predicate).test(" ");
                verify(predicate).and(other);
                verify(other).test(" ");
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testThisDoesNotMatch() throws IOException {
                ThrowingPredicate<String, IOException> predicate = Spied.throwingPredicate(String::isBlank);
                ThrowingPredicate<String, IOException> other = Spied.throwingPredicate(s -> {
                    throw new IOException(s);
                });

                ThrowingPredicate<String, IOException> composed = predicate.and(other);

                assertFalse(composed.test("foo"));

                verify(predicate).test("foo");
                verify(predicate).and(other);
                verifyNoMoreInteractions(predicate, other);
            }
        }

        @Nested
        class OtherThrowsUnchecked {

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testThisMatches(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingPredicate<String, IOException> predicate = Spied.throwingPredicate(String::isBlank);
                ThrowingPredicate<String, IOException> other = Spied.throwingPredicate(throwable::throwUnchecked);

                ThrowingPredicate<String, IOException> composed = predicate.and(other);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> composed.test(" "));
                assertEquals(" ", thrown.getMessage());

                verify(predicate).test(" ");
                verify(predicate).and(other);
                verify(other).test(" ");
                verifyNoMoreInteractions(predicate, other);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testThisDoesNotMatch(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingPredicate<String, IOException> predicate = Spied.throwingPredicate(String::isBlank);
                ThrowingPredicate<String, IOException> other = Spied.throwingPredicate(throwable::throwUnchecked);

                ThrowingPredicate<String, IOException> composed = predicate.and(other);

                assertFalse(composed.test("foo"));

                verify(predicate).test("foo");
                verify(predicate).and(other);
                verifyNoMoreInteractions(predicate, other);
            }
        }
    }

    @Nested
    class Negate {

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingPredicate<String, IOException> predicate = Spied.throwingPredicate(String::isBlank);

            ThrowingPredicate<String, IOException> negated = predicate.negate();

            assertFalse(negated.test(" "));
            assertTrue(negated.test("foo"));

            verify(predicate).test(" ");
            verify(predicate).test("foo");
            verify(predicate).negate();
            verifyNoMoreInteractions(predicate);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingPredicate<String, IOException> predicate = Spied.throwingPredicate(s -> {
                throw new IOException(s);
            });

            ThrowingPredicate<String, IOException> negated = predicate.negate();

            IOException thrown = assertThrows(IOException.class, () -> negated.test("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(predicate).test("foo");
            verify(predicate).negate();
            verifyNoMoreInteractions(predicate);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingPredicate<String, IOException> predicate = Spied.throwingPredicate(throwable::throwUnchecked);

            ThrowingPredicate<String, IOException> negated = predicate.negate();

            Throwable thrown = assertThrows(throwable.throwableType(), () -> negated.test("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(predicate).test("foo");
            verify(predicate).negate();
            verifyNoMoreInteractions(predicate);
        }
    }

    @Nested
    class Or {

        @Test
        void testNullArgument() {
            ThrowingPredicate<String, IOException> predicate = Spied.throwingPredicate(String::isEmpty);

            assertThrows(NullPointerException.class, () -> predicate.or(null));
        }

        @Nested
        class NeitherThrows {

            @Test
            void testThisMatches() throws IOException {
                ThrowingPredicate<String, IOException> predicate = Spied.throwingPredicate(String::isBlank);
                ThrowingPredicate<String, IOException> other = Spied.throwingPredicate(String::isEmpty);

                ThrowingPredicate<String, IOException> composed = predicate.or(other);

                assertTrue(composed.test(" "));

                verify(predicate).test(" ");
                verify(predicate).or(other);
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testOtherMatches() throws IOException {
                ThrowingPredicate<String, IOException> predicate = Spied.throwingPredicate(String::isEmpty);
                ThrowingPredicate<String, IOException> other = Spied.throwingPredicate(String::isBlank);

                ThrowingPredicate<String, IOException> composed = predicate.or(other);

                assertTrue(composed.test(" "));

                verify(predicate).test(" ");
                verify(predicate).or(other);
                verify(other).test(" ");
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testBothMatch() throws IOException {
                ThrowingPredicate<String, IOException> predicate = Spied.throwingPredicate(String::isEmpty);
                ThrowingPredicate<String, IOException> other = Spied.throwingPredicate(String::isBlank);

                ThrowingPredicate<String, IOException> composed = predicate.or(other);

                assertTrue(composed.test(""));

                verify(predicate).test("");
                verify(predicate).or(other);
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testNeitherMatches() throws IOException {
                ThrowingPredicate<String, IOException> predicate = Spied.throwingPredicate(String::isEmpty);
                ThrowingPredicate<String, IOException> other = Spied.throwingPredicate(String::isBlank);

                ThrowingPredicate<String, IOException> composed = predicate.or(other);

                assertFalse(composed.test("foo"));

                verify(predicate).test("foo");
                verify(predicate).or(other);
                verify(other).test("foo");
                verifyNoMoreInteractions(predicate, other);
            }
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testOtherMatches() throws IOException {
                ThrowingPredicate<String, IOException> predicate = Spied.throwingPredicate(s -> {
                    throw new IOException(s);
                });
                ThrowingPredicate<String, IOException> other = Spied.throwingPredicate(String::isBlank);

                ThrowingPredicate<String, IOException> composed = predicate.or(other);

                IOException thrown = assertThrows(IOException.class, () -> composed.test(" "));
                assertEquals(" ", thrown.getMessage());

                verify(predicate).test(" ");
                verify(predicate).or(other);
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testOtherDoesNotMatch() throws IOException {
                ThrowingPredicate<String, IOException> predicate = Spied.throwingPredicate(s -> {
                    throw new IOException(s);
                });
                ThrowingPredicate<String, IOException> other = Spied.throwingPredicate(String::isBlank);

                ThrowingPredicate<String, IOException> composed = predicate.or(other);

                IOException thrown = assertThrows(IOException.class, () -> composed.test("foo"));
                assertEquals("foo", thrown.getMessage());

                verify(predicate).test("foo");
                verify(predicate).or(other);
                verifyNoMoreInteractions(predicate, other);
            }
        }

        @Nested
        class ThisThrowsUnchecked {

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testOtherMatches(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingPredicate<String, IOException> predicate = Spied.throwingPredicate(throwable::throwUnchecked);
                ThrowingPredicate<String, IOException> other = Spied.throwingPredicate(String::isBlank);

                ThrowingPredicate<String, IOException> composed = predicate.or(other);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> composed.test(" "));
                assertEquals(" ", thrown.getMessage());

                verify(predicate).test(" ");
                verify(predicate).or(other);
                verifyNoMoreInteractions(predicate, other);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testOtherDoesNotMatch(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingPredicate<String, IOException> predicate = Spied.throwingPredicate(throwable::throwUnchecked);
                ThrowingPredicate<String, IOException> other = Spied.throwingPredicate(String::isBlank);

                ThrowingPredicate<String, IOException> composed = predicate.or(other);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> composed.test("foo"));
                assertEquals("foo", thrown.getMessage());

                verify(predicate).test("foo");
                verify(predicate).or(other);
                verifyNoMoreInteractions(predicate, other);
            }
        }

        @Nested
        class OtherThrowsChecked {

            @Test
            void testThisMatches() throws IOException {
                ThrowingPredicate<String, IOException> predicate = Spied.throwingPredicate(String::isBlank);
                ThrowingPredicate<String, IOException> other = Spied.throwingPredicate(s -> {
                    throw new IOException(s);
                });

                ThrowingPredicate<String, IOException> composed = predicate.or(other);

                assertTrue(composed.test(" "));

                verify(predicate).test(" ");
                verify(predicate).or(other);
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testThisDoesNotMatch() throws IOException {
                ThrowingPredicate<String, IOException> predicate = Spied.throwingPredicate(String::isBlank);
                ThrowingPredicate<String, IOException> other = Spied.throwingPredicate(s -> {
                    throw new IOException(s);
                });

                ThrowingPredicate<String, IOException> composed = predicate.or(other);

                IOException thrown = assertThrows(IOException.class, () -> composed.test("foo"));
                assertEquals("foo", thrown.getMessage());

                verify(predicate).test("foo");
                verify(predicate).or(other);
                verify(other).test("foo");
                verifyNoMoreInteractions(predicate, other);
            }
        }

        @Nested
        class OtherThrowsUnchecked {

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testThisMatches(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingPredicate<String, IOException> predicate = Spied.throwingPredicate(String::isBlank);
                ThrowingPredicate<String, IOException> other = Spied.throwingPredicate(throwable::throwUnchecked);

                ThrowingPredicate<String, IOException> composed = predicate.or(other);

                assertTrue(composed.test(" "));

                verify(predicate).test(" ");
                verify(predicate).or(other);
                verifyNoMoreInteractions(predicate, other);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testThisDoesNotMatch(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingPredicate<String, IOException> predicate = Spied.throwingPredicate(String::isBlank);
                ThrowingPredicate<String, IOException> other = Spied.throwingPredicate(throwable::throwUnchecked);

                ThrowingPredicate<String, IOException> composed = predicate.or(other);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> composed.test("foo"));
                assertEquals("foo", thrown.getMessage());

                verify(predicate).test("foo");
                verify(predicate).or(other);
                verify(other).test("foo");
                verifyNoMoreInteractions(predicate, other);
            }
        }
    }

    @Nested
    class OnErrorThrowAsChecked {

        @Test
        void testNullArgument() {
            ThrowingPredicate<String, IOException> predicate = Spied.throwingPredicate(String::isBlank);

            assertThrows(NullPointerException.class, () -> predicate.onErrorThrowAsChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingPredicate<String, IOException> predicate = Spied.throwingPredicate(String::isBlank);

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingPredicate<String, ExecutionException> throwing = predicate.onErrorThrowAsChecked(errorMapper);

            assertTrue(throwing.test(" "));

            verify(predicate).test(" ");
            verify(predicate).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(predicate, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingPredicate<String, IOException> predicate = Spied.throwingPredicate(s -> {
                throw new IOException(s);
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingPredicate<String, ExecutionException> throwing = predicate.onErrorThrowAsChecked(errorMapper);

            ExecutionException thrown = assertThrows(ExecutionException.class, () -> throwing.test("foo"));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo", cause.getMessage());

            verify(predicate).test("foo");
            verify(predicate).onErrorThrowAsChecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(predicate, errorMapper);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingPredicate<String, IOException> predicate = Spied.throwingPredicate(throwable::throwUnchecked);

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            ThrowingPredicate<String, ExecutionException> throwing = predicate.onErrorThrowAsChecked(errorMapper);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> throwing.test("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(predicate).test("foo");
            verify(predicate).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(predicate, errorMapper);
        }
    }

    @Nested
    class OnErrorThrowAsUnchecked {

        @Test
        void testNullArgument() {
            ThrowingPredicate<String, IOException> predicate = Spied.throwingPredicate(String::isBlank);

            assertThrows(NullPointerException.class, () -> predicate.onErrorThrowAsUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingPredicate<String, IOException> predicate = Spied.throwingPredicate(String::isBlank);

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            Predicate<String> throwing = predicate.onErrorThrowAsUnchecked(errorMapper);

            assertTrue(throwing.test(" "));

            verify(predicate).test(" ");
            verify(predicate).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(predicate, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingPredicate<String, IOException> predicate = Spied.throwingPredicate(s -> {
                throw new IOException(s);
            });

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            Predicate<String> throwing = predicate.onErrorThrowAsUnchecked(errorMapper);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> throwing.test("foo"));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo", cause.getMessage());

            verify(predicate).test("foo");
            verify(predicate).onErrorThrowAsUnchecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(predicate, errorMapper);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingPredicate<String, IOException> predicate = Spied.throwingPredicate(throwable::throwUnchecked);

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            Predicate<String> throwing = predicate.onErrorThrowAsUnchecked(errorMapper);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> throwing.test("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(predicate).test("foo");
            verify(predicate).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(predicate, errorMapper);
        }
    }

    @Nested
    class OnErrorHandleChecked {

        @Test
        void testNullArgument() {
            ThrowingPredicate<String, IOException> predicate = Spied.throwingPredicate(String::isBlank);

            assertThrows(NullPointerException.class, () -> predicate.onErrorHandleChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingPredicate<String, IOException> predicate = Spied.throwingPredicate(String::isBlank);

            ThrowingPredicate<IOException, ExecutionException> errorHandler = Spied.throwingPredicate(e -> e.getMessage() == null);

            ThrowingPredicate<String, ExecutionException> handling = predicate.onErrorHandleChecked(errorHandler);

            assertTrue(handling.test(" "));

            verify(predicate).test(" ");
            verify(predicate).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(predicate, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException, ExecutionException {
                ThrowingPredicate<String, IOException> predicate = Spied.throwingPredicate(s -> {
                    throw new IOException(s);
                });

                ThrowingPredicate<IOException, ExecutionException> errorHandler = Spied.throwingPredicate(e -> e.getMessage() == null);

                ThrowingPredicate<String, ExecutionException> handling = predicate.onErrorHandleChecked(errorHandler);

                assertFalse(handling.test(" "));

                verify(predicate).test(" ");
                verify(predicate).onErrorHandleChecked(errorHandler);
                verify(errorHandler).test(any());
                verifyNoMoreInteractions(predicate, errorHandler);
            }

            @Test
            void testHandlerThrowsChecked() throws IOException, ExecutionException {
                ThrowingPredicate<String, IOException> predicate = Spied.throwingPredicate(s -> {
                    throw new IOException(s);
                });

                ThrowingPredicate<IOException, ExecutionException> errorHandler = Spied.throwingPredicate(e -> {
                    throw new ExecutionException(e);
                });

                ThrowingPredicate<String, ExecutionException> handling = predicate.onErrorHandleChecked(errorHandler);

                ExecutionException thrown = assertThrows(ExecutionException.class, () -> handling.test("foo"));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());

                verify(predicate).test("foo");
                verify(predicate).onErrorHandleChecked(errorHandler);
                verify(errorHandler).test(any());
                verifyNoMoreInteractions(predicate, errorHandler);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testHandlerThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ExecutionException {
                ThrowingPredicate<String, IOException> predicate = Spied.throwingPredicate(s -> {
                    throw new IOException(s);
                });

                ThrowingPredicate<IOException, ExecutionException> errorHandler = Spied.throwingPredicate(throwable::throwUnchecked);

                ThrowingPredicate<String, ExecutionException> handling = predicate.onErrorHandleChecked(errorHandler);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.test("foo"));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());

                verify(predicate).test("foo");
                verify(predicate).onErrorHandleChecked(errorHandler);
                verify(errorHandler).test(any());
                verifyNoMoreInteractions(predicate, errorHandler);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingPredicate<String, IOException> predicate = Spied.throwingPredicate(throwable::throwUnchecked);

            ThrowingPredicate<IOException, ExecutionException> errorHandler = Spied.throwingPredicate(e -> e.getMessage() == null);

            ThrowingPredicate<String, ExecutionException> handling = predicate.onErrorHandleChecked(errorHandler);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.test("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(predicate).test("foo");
            verify(predicate).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(predicate, errorHandler);
        }
    }

    @Nested
    class OnErrorHandleUnchecked {

        @Test
        void testNullArgument() {
            ThrowingPredicate<String, IOException> predicate = Spied.throwingPredicate(String::isBlank);

            assertThrows(NullPointerException.class, () -> predicate.onErrorHandleUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingPredicate<String, IOException> predicate = Spied.throwingPredicate(String::isBlank);

            Predicate<IOException> errorHandler = Spied.predicate(e -> e.getMessage() == null);

            Predicate<String> handling = predicate.onErrorHandleUnchecked(errorHandler);

            assertTrue(handling.test(" "));

            verify(predicate).test(" ");
            verify(predicate).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(predicate, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException {
                ThrowingPredicate<String, IOException> predicate = Spied.throwingPredicate(s -> {
                    throw new IOException(s);
                });

                Predicate<IOException> errorHandler = Spied.predicate(e -> e.getMessage() == null);

                Predicate<String> handling = predicate.onErrorHandleUnchecked(errorHandler);

                assertFalse(handling.test("foo"));

                verify(predicate).test("foo");
                verify(predicate).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).test(any());
                verifyNoMoreInteractions(predicate, errorHandler);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testHandlerThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingPredicate<String, IOException> predicate = Spied.throwingPredicate(s -> {
                    throw new IOException(s);
                });

                Predicate<IOException> errorHandler = Spied.predicate(throwable::throwUnchecked);

                Predicate<String> handling = predicate.onErrorHandleUnchecked(errorHandler);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.test("foo"));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());

                verify(predicate).test("foo");
                verify(predicate).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).test(any());
                verifyNoMoreInteractions(predicate, errorHandler);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingPredicate<String, IOException> predicate = Spied.throwingPredicate(throwable::throwUnchecked);

            Predicate<IOException> errorHandler = Spied.predicate(e -> e.getMessage() == null);

            Predicate<String> handling = predicate.onErrorHandleUnchecked(errorHandler);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.test("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(predicate).test("foo");
            verify(predicate).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(predicate, errorHandler);
        }
    }

    @Nested
    class OnErrorTestChecked {

        @Test
        void testNullArgument() {
            ThrowingPredicate<String, IOException> predicate = Spied.throwingPredicate(String::isBlank);

            assertThrows(NullPointerException.class, () -> predicate.onErrorTestChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingPredicate<String, IOException> predicate = Spied.throwingPredicate(String::isBlank);

            ThrowingPredicate<String, ExecutionException> fallback = Spied.throwingPredicate(String::isEmpty);

            ThrowingPredicate<String, ExecutionException> testing = predicate.onErrorTestChecked(fallback);

            assertTrue(testing.test(" "));

            verify(predicate).test(" ");
            verify(predicate).onErrorTestChecked(fallback);
            verifyNoMoreInteractions(predicate, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                ThrowingPredicate<String, IOException> predicate = Spied.throwingPredicate(s -> {
                    throw new IOException(s);
                });

                ThrowingPredicate<String, ParseException> fallback = Spied.throwingPredicate(String::isEmpty);

                ThrowingPredicate<String, ParseException> testing = predicate.onErrorTestChecked(fallback);

                assertFalse(testing.test(" "));

                verify(predicate).test(" ");
                verify(predicate).onErrorTestChecked(fallback);
                verify(fallback).test(" ");
                verifyNoMoreInteractions(predicate, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                ThrowingPredicate<String, IOException> predicate = Spied.throwingPredicate(s -> {
                    throw new IOException(s);
                });

                ThrowingPredicate<String, ParseException> fallback = Spied.throwingPredicate(s -> {
                    throw new ParseException(s, 0);
                });

                ThrowingPredicate<String, ParseException> testing = predicate.onErrorTestChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> testing.test("foo"));
                assertEquals("foo", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(predicate).test("foo");
                verify(predicate).onErrorTestChecked(fallback);
                verify(fallback).test("foo");
                verifyNoMoreInteractions(predicate, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ParseException {
                ThrowingPredicate<String, IOException> predicate = Spied.throwingPredicate(s -> {
                    throw new IOException(s);
                });

                ThrowingPredicate<String, ParseException> fallback = Spied.throwingPredicate(throwable::throwUnchecked);

                ThrowingPredicate<String, ParseException> testing = predicate.onErrorTestChecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> testing.test("foo"));
                assertEquals("foo", thrown.getMessage());

                verify(predicate).test("foo");
                verify(predicate).onErrorTestChecked(fallback);
                verify(fallback).test("foo");
                verifyNoMoreInteractions(predicate, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingPredicate<String, IOException> predicate = Spied.throwingPredicate(throwable::throwUnchecked);

            ThrowingPredicate<String, ParseException> fallback = Spied.throwingPredicate(String::isEmpty);

            ThrowingPredicate<String, ParseException> testing = predicate.onErrorTestChecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> testing.test("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(predicate).test("foo");
            verify(predicate).onErrorTestChecked(fallback);
            verifyNoMoreInteractions(predicate, fallback);
        }
    }

    @Nested
    class OnErrorTestUnchecked {

        @Test
        void testNullArgument() {
            ThrowingPredicate<String, IOException> predicate = Spied.throwingPredicate(String::isBlank);

            assertThrows(NullPointerException.class, () -> predicate.onErrorTestUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingPredicate<String, IOException> predicate = Spied.throwingPredicate(String::isBlank);

            Predicate<String> fallback = Spied.predicate(String::isEmpty);

            Predicate<String> testing = predicate.onErrorTestUnchecked(fallback);

            assertTrue(testing.test(" "));

            verify(predicate).test(" ");
            verify(predicate).onErrorTestUnchecked(fallback);
            verifyNoMoreInteractions(predicate, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                ThrowingPredicate<String, IOException> predicate = Spied.throwingPredicate(s -> {
                    throw new IOException(s);
                });

                Predicate<String> fallback = Spied.predicate(String::isEmpty);

                Predicate<String> testing = predicate.onErrorTestUnchecked(fallback);

                assertFalse(testing.test(" "));

                verify(predicate).test(" ");
                verify(predicate).onErrorTestUnchecked(fallback);
                verify(fallback).test(" ");
                verifyNoMoreInteractions(predicate, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingPredicate<String, IOException> predicate = Spied.throwingPredicate(s -> {
                    throw new IOException(s);
                });

                Predicate<String> fallback = Spied.predicate(throwable::throwUnchecked);

                Predicate<String> testing = predicate.onErrorTestUnchecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> testing.test("foo"));
                assertEquals("foo", thrown.getMessage());

                verify(predicate).test("foo");
                verify(predicate).onErrorTestUnchecked(fallback);
                verify(fallback).test("foo");
                verifyNoMoreInteractions(predicate, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingPredicate<String, IOException> predicate = Spied.throwingPredicate(throwable::throwUnchecked);

            Predicate<String> fallback = Spied.predicate(String::isEmpty);

            Predicate<String> testing = predicate.onErrorTestUnchecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> testing.test("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(predicate).test("foo");
            verify(predicate).onErrorTestUnchecked(fallback);
            verifyNoMoreInteractions(predicate, fallback);
        }
    }

    @Nested
    class OnErrorGetCheckedAsBoolean {

        @Test
        void testNullArgument() {
            ThrowingPredicate<String, IOException> predicate = Spied.throwingPredicate(String::isBlank);

            assertThrows(NullPointerException.class, () -> predicate.onErrorGetCheckedAsBoolean(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            ThrowingPredicate<String, IOException> predicate = Spied.throwingPredicate(String::isBlank);

            ThrowingBooleanSupplier<ExecutionException> fallback = Spied.throwingBooleanSupplier(() -> false);

            ThrowingPredicate<String, ExecutionException> getting = predicate.onErrorGetCheckedAsBoolean(fallback);

            assertTrue(getting.test(" "));

            verify(predicate).test(" ");
            verify(predicate).onErrorGetCheckedAsBoolean(fallback);
            verifyNoMoreInteractions(predicate, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                ThrowingPredicate<String, IOException> predicate = Spied.throwingPredicate(s -> {
                    throw new IOException(s);
                });

                ThrowingBooleanSupplier<ParseException> fallback = Spied.throwingBooleanSupplier(() -> false);

                ThrowingPredicate<String, ParseException> getting = predicate.onErrorGetCheckedAsBoolean(fallback);

                assertFalse(getting.test("foo"));

                verify(predicate).test("foo");
                verify(predicate).onErrorGetCheckedAsBoolean(fallback);
                verify(fallback).getAsBoolean();
                verifyNoMoreInteractions(predicate, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                ThrowingPredicate<String, IOException> predicate = Spied.throwingPredicate(s -> {
                    throw new IOException(s);
                });

                ThrowingBooleanSupplier<ParseException> fallback = Spied.throwingBooleanSupplier(() -> {
                    throw new ParseException("bar", 0);
                });

                ThrowingPredicate<String, ParseException> getting = predicate.onErrorGetCheckedAsBoolean(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> getting.test("foo"));
                assertEquals("bar", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(predicate).test("foo");
                verify(predicate).onErrorGetCheckedAsBoolean(fallback);
                verify(fallback).getAsBoolean();
                verifyNoMoreInteractions(predicate, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ParseException {
                ThrowingPredicate<String, IOException> predicate = Spied.throwingPredicate(s -> {
                    throw new IOException(s);
                });

                ThrowingBooleanSupplier<ParseException> fallback = Spied.throwingBooleanSupplier(() -> throwable.throwUnchecked("bar"));

                ThrowingPredicate<String, ParseException> getting = predicate.onErrorGetCheckedAsBoolean(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.test("foo"));
                assertEquals("bar", thrown.getMessage());

                verify(predicate).test("foo");
                verify(predicate).onErrorGetCheckedAsBoolean(fallback);
                verify(fallback).getAsBoolean();
                verifyNoMoreInteractions(predicate, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingPredicate<String, IOException> predicate = Spied.throwingPredicate(throwable::throwUnchecked);

            ThrowingBooleanSupplier<ParseException> fallback = Spied.throwingBooleanSupplier(() -> false);

            ThrowingPredicate<String, ParseException> getting = predicate.onErrorGetCheckedAsBoolean(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.test("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(predicate).test("foo");
            verify(predicate).onErrorGetCheckedAsBoolean(fallback);
            verifyNoMoreInteractions(predicate, fallback);
        }
    }

    @Nested
    class OnErrorGetUncheckedAsBoolean {

        @Test
        void testNullArgument() {
            ThrowingPredicate<String, IOException> predicate = Spied.throwingPredicate(String::isBlank);

            assertThrows(NullPointerException.class, () -> predicate.onErrorGetUncheckedAsBoolean(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingPredicate<String, IOException> predicate = Spied.throwingPredicate(String::isBlank);

            BooleanSupplier fallback = Spied.booleanSupplier(() -> false);

            Predicate<String> getting = predicate.onErrorGetUncheckedAsBoolean(fallback);

            assertTrue(getting.test(" "));

            verify(predicate).test(" ");
            verify(predicate).onErrorGetUncheckedAsBoolean(fallback);
            verifyNoMoreInteractions(predicate, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                ThrowingPredicate<String, IOException> predicate = Spied.throwingPredicate(s -> {
                    throw new IOException(s);
                });

                BooleanSupplier fallback = Spied.booleanSupplier(() -> false);

                Predicate<String> getting = predicate.onErrorGetUncheckedAsBoolean(fallback);

                assertFalse(getting.test(" "));

                verify(predicate).test(" ");
                verify(predicate).onErrorGetUncheckedAsBoolean(fallback);
                verify(fallback).getAsBoolean();
                verifyNoMoreInteractions(predicate, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                ThrowingPredicate<String, IOException> predicate = Spied.throwingPredicate(s -> {
                    throw new IOException(s);
                });

                BooleanSupplier fallback = Spied.booleanSupplier(() -> throwable.throwUnchecked("bar"));

                Predicate<String> getting = predicate.onErrorGetUncheckedAsBoolean(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.test("foo"));
                assertEquals("bar", thrown.getMessage());

                verify(predicate).test("foo");
                verify(predicate).onErrorGetUncheckedAsBoolean(fallback);
                verify(fallback).getAsBoolean();
                verifyNoMoreInteractions(predicate, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingPredicate<String, IOException> predicate = Spied.throwingPredicate(throwable::throwUnchecked);

            BooleanSupplier fallback = Spied.booleanSupplier(() -> false);

            Predicate<String> getting = predicate.onErrorGetUncheckedAsBoolean(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.test("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(predicate).test("foo");
            verify(predicate).onErrorGetUncheckedAsBoolean(fallback);
            verifyNoMoreInteractions(predicate, fallback);
        }
    }

    @Nested
    class OnErrorReturn {

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingPredicate<String, IOException> predicate = Spied.throwingPredicate(String::isBlank);

            Predicate<String> returning = predicate.onErrorReturn(false);

            assertTrue(returning.test(" "));

            verify(predicate).test(" ");
            verify(predicate).onErrorReturn(false);
            verifyNoMoreInteractions(predicate);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingPredicate<String, IOException> predicate = Spied.throwingPredicate(s -> {
                throw new IOException(s);
            });

            Predicate<String> returning = predicate.onErrorReturn(false);

            assertFalse(returning.test(" "));

            verify(predicate).test(" ");
            verify(predicate).onErrorReturn(false);
            verifyNoMoreInteractions(predicate);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingPredicate<String, IOException> predicate = Spied.throwingPredicate(throwable::throwUnchecked);

            Predicate<String> returning = predicate.onErrorReturn(false);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> returning.test("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(predicate).test("foo");
            verify(predicate).onErrorReturn(false);
            verifyNoMoreInteractions(predicate);
        }
    }

    @Nested
    class Unchecked {

        @Test
        void testThisThrowsNothing() throws IOException {
            ThrowingPredicate<String, IOException> predicate = Spied.throwingPredicate(String::isBlank);

            Predicate<String> unchecked = predicate.unchecked();

            assertTrue(unchecked.test(" "));

            verify(predicate).test(" ");
            verify(predicate).unchecked();
            verify(predicate).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(predicate);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            ThrowingPredicate<String, IOException> predicate = Spied.throwingPredicate(s -> {
                throw new IOException(s);
            });

            Predicate<String> unchecked = predicate.unchecked();

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.test("foo"));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo", cause.getMessage());

            verify(predicate).test("foo");
            verify(predicate).unchecked();
            verify(predicate).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(predicate);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingPredicate<String, IOException> predicate = Spied.throwingPredicate(throwable::throwUnchecked);

            Predicate<String> unchecked = predicate.unchecked();

            Throwable thrown = assertThrows(throwable.throwableType(), () -> unchecked.test("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(predicate).test("foo");
            verify(predicate).unchecked();
            verify(predicate).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(predicate);
        }
    }

    @Nested
    class Of {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> ThrowingPredicate.of(null));
        }

        @Test
        void testNonNullArgument() throws IOException {
            ThrowingPredicate<String, IOException> predicate = ThrowingPredicate.of(String::isBlank);

            assertTrue(predicate.test(" "));
        }
    }

    @Nested
    class Not {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> ThrowingPredicate.not(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ThrowingPredicate<String, IOException> predicate = Spied.throwingPredicate(String::isBlank);

            ThrowingPredicate<String, IOException> negated = ThrowingPredicate.not(predicate);

            assertFalse(negated.test(" "));
            assertTrue(negated.test("foo"));

            verify(predicate).negate();
            verify(predicate).test(" ");
            verify(predicate).test("foo");
            verifyNoMoreInteractions(predicate);
        }

        @Test
        void testArgumentThrowsChecked() throws IOException {
            ThrowingPredicate<String, IOException> predicate = Spied.throwingPredicate(s -> {
                throw new IOException(s);
            });

            ThrowingPredicate<String, IOException> negated = ThrowingPredicate.not(predicate);

            IOException thrown = assertThrows(IOException.class, () -> negated.test("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(predicate).negate();
            verify(predicate).test("foo");
            verifyNoMoreInteractions(predicate);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testArgumentThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingPredicate<String, IOException> predicate = Spied.throwingPredicate(throwable::throwUnchecked);

            ThrowingPredicate<String, IOException> negated = ThrowingPredicate.not(predicate);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> negated.test("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(predicate).negate();
            verify(predicate).test("foo");
            verifyNoMoreInteractions(predicate);
        }
    }

    @Nested
    class UncheckedFactory {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> ThrowingPredicate.unchecked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ThrowingPredicate<String, IOException> predicate = Spied.throwingPredicate(String::isBlank);

            Predicate<String> unchecked = ThrowingPredicate.unchecked(predicate);

            assertTrue(unchecked.test(" "));

            verify(predicate).unchecked();
            verify(predicate).onErrorThrowAsUnchecked(any());
            verify(predicate).test(" ");
            verifyNoMoreInteractions(predicate);
        }

        @Test
        void testArgumentThrowsChecked() throws IOException {
            ThrowingPredicate<String, IOException> predicate = Spied.throwingPredicate(s -> {
                throw new IOException(s);
            });

            Predicate<String> unchecked = ThrowingPredicate.unchecked(predicate);

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.test("foo"));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo", cause.getMessage());

            verify(predicate).unchecked();
            verify(predicate).onErrorThrowAsUnchecked(any());
            verify(predicate).test("foo");
            verifyNoMoreInteractions(predicate);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testArgumentThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            ThrowingPredicate<String, IOException> predicate = Spied.throwingPredicate(throwable::throwUnchecked);

            Predicate<String> unchecked = ThrowingPredicate.unchecked(predicate);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> unchecked.test("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(predicate).unchecked();
            verify(predicate).onErrorThrowAsUnchecked(any());
            verify(predicate).test("foo");
            verifyNoMoreInteractions(predicate);
        }
    }

    @Nested
    class CheckedWrapper {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> ThrowingPredicate.checked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ThrowingPredicate<String, IOException> predicate = ThrowingPredicate.checked(String::isBlank);

            assertTrue(predicate.test(" "));
        }

        @Test
        void testArgumentThrowsUnchecked() {
            ThrowingPredicate<String, IOException> predicate = ThrowingPredicate.checked(s -> {
                throw UncheckedException.withoutStackTrace(s, new IOException());
            });

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> predicate.test("foo"));
            assertEquals("foo", thrown.getMessage());
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertNull(cause.getMessage());
        }
    }

    @Nested
    class CheckedUnwrapper {

        @Test
        void testNullArguments() {
            Predicate<String> predicate = String::isBlank;

            assertThrows(NullPointerException.class, () -> ThrowingPredicate.checked(null, IOException.class));
            assertThrows(NullPointerException.class, () -> ThrowingPredicate.checked(predicate, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            ThrowingPredicate<String, IOException> predicate = ThrowingPredicate.checked(String::isBlank, IOException.class);

            assertTrue(predicate.test(" "));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                ThrowingPredicate<String, IOException> predicate = ThrowingPredicate.checked(s -> {
                    throw UncheckedException.withoutStackTrace(new IOException(s));
                }, IOException.class);

                IOException thrown = assertThrows(IOException.class, () -> predicate.test("foo"));
                assertEquals("foo", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                ThrowingPredicate<String, IOException> predicate = ThrowingPredicate.checked(s -> {
                    throw UncheckedException.withoutStackTrace(new FileNotFoundException(s));
                }, IOException.class);

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class, () -> predicate.test("foo"));
                assertEquals("foo", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                ThrowingPredicate<String, IOException> predicate = ThrowingPredicate.checked(s -> {
                    throw UncheckedException.withoutStackTrace(new ParseException(s, 0));
                }, IOException.class);

                UncheckedException thrown = assertThrows(UncheckedException.class, () -> predicate.test("foo"));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            ThrowingPredicate<String, IOException> predicate = ThrowingPredicate.checked(s -> {
                throw new IllegalStateException(s);
            }, IOException.class);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> predicate.test("foo"));
            assertEquals("foo", thrown.getMessage());
        }
    }

    @Nested
    class InvokeAndUnwrap {

        @Test
        void testNullArguments() throws IOException {
            Predicate<String> predicate = Objects::nonNull;

            assertThrows(NullPointerException.class, () -> ThrowingPredicate.invokeAndUnwrap(null, "foo", IOException.class));
            assertThrows(NullPointerException.class, () -> ThrowingPredicate.invokeAndUnwrap(predicate, "foo", null));

            assertFalse(ThrowingPredicate.invokeAndUnwrap(predicate, null, IOException.class));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            Predicate<String> predicate = String::isBlank;

            assertTrue(ThrowingPredicate.invokeAndUnwrap(predicate, " ", IOException.class));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                Predicate<String> predicate = s -> {
                    throw UncheckedException.withoutStackTrace(new IOException(s));
                };

                IOException thrown = assertThrows(IOException.class, () -> ThrowingPredicate.invokeAndUnwrap(predicate, "foo", IOException.class));
                assertEquals("foo", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                Predicate<String> predicate = s -> {
                    throw UncheckedException.withoutStackTrace(new FileNotFoundException(s));
                };

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class,
                        () -> ThrowingPredicate.invokeAndUnwrap(predicate, "foo", IOException.class));
                assertEquals("foo", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                Predicate<String> predicate = s -> {
                    throw UncheckedException.withoutStackTrace(new ParseException(s, 0));
                };

                UncheckedException thrown = assertThrows(UncheckedException.class,
                        () -> ThrowingPredicate.invokeAndUnwrap(predicate, "foo", IOException.class));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            Predicate<String> predicate = s -> {
                throw new IllegalStateException(s);
            };

            IllegalStateException thrown = assertThrows(IllegalStateException.class,
                    () -> ThrowingPredicate.invokeAndUnwrap(predicate, "foo", IOException.class));
            assertEquals("foo", thrown.getMessage());
        }
    }
}
