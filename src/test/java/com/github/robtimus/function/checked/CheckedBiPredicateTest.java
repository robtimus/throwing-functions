/*
 * CheckedBiPredicateTest.java
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
import java.util.function.BiPredicate;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Predicate;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

@SuppressWarnings("nls")
class CheckedBiPredicateTest {

    @Nested
    class And {

        @Test
        void testNullArgument() {
            CheckedBiPredicate<String, String, IOException> predicate = Spied.checkedBiPredicate(String::equalsIgnoreCase);

            assertThrows(NullPointerException.class, () -> predicate.and(null));
        }

        @Nested
        class NeitherThrows {

            @Test
            void testThisMatches() throws IOException {
                CheckedBiPredicate<String, String, IOException> predicate = Spied.checkedBiPredicate(String::equalsIgnoreCase);
                CheckedBiPredicate<String, String, IOException> other = Spied.checkedBiPredicate(String::equals);

                CheckedBiPredicate<String, String, IOException> composed = predicate.and(other);

                assertFalse(composed.test("foo", "FOO"));

                verify(predicate).test("foo", "FOO");
                verify(predicate).and(other);
                verify(other).test("foo", "FOO");
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testOtherMatches() throws IOException {
                CheckedBiPredicate<String, String, IOException> predicate = Spied.checkedBiPredicate(String::equals);
                CheckedBiPredicate<String, String, IOException> other = Spied.checkedBiPredicate(String::equalsIgnoreCase);

                CheckedBiPredicate<String, String, IOException> composed = predicate.and(other);

                assertFalse(composed.test("foo", "FOO"));

                verify(predicate).test("foo", "FOO");
                verify(predicate).and(other);
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testBothMatch() throws IOException {
                CheckedBiPredicate<String, String, IOException> predicate = Spied.checkedBiPredicate(String::equals);
                CheckedBiPredicate<String, String, IOException> other = Spied.checkedBiPredicate(String::equalsIgnoreCase);

                CheckedBiPredicate<String, String, IOException> composed = predicate.and(other);

                assertTrue(composed.test("foo", "foo"));

                verify(predicate).test("foo", "foo");
                verify(predicate).and(other);
                verify(other).test("foo", "foo");
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testNeitherMatches() throws IOException {
                CheckedBiPredicate<String, String, IOException> predicate = Spied.checkedBiPredicate(String::equals);
                CheckedBiPredicate<String, String, IOException> other = Spied.checkedBiPredicate(String::equalsIgnoreCase);

                CheckedBiPredicate<String, String, IOException> composed = predicate.and(other);

                assertFalse(composed.test("foo", "bar"));

                verify(predicate).test("foo", "bar");
                verify(predicate).and(other);
                verifyNoMoreInteractions(predicate, other);
            }
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testOtherMatches() throws IOException {
                CheckedBiPredicate<String, String, IOException> predicate = Spied.checkedBiPredicate((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });
                CheckedBiPredicate<String, String, IOException> other = Spied.checkedBiPredicate(String::equalsIgnoreCase);

                CheckedBiPredicate<String, String, IOException> composed = predicate.and(other);

                IOException thrown = assertThrows(IOException.class, () -> composed.test("foo", "FOO"));
                assertEquals("fooFOO", thrown.getMessage());

                verify(predicate).test("foo", "FOO");
                verify(predicate).and(other);
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testOtherDoesNotMatch() throws IOException {
                CheckedBiPredicate<String, String, IOException> predicate = Spied.checkedBiPredicate((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });
                CheckedBiPredicate<String, String, IOException> other = Spied.checkedBiPredicate(String::equalsIgnoreCase);

                CheckedBiPredicate<String, String, IOException> composed = predicate.and(other);

                IOException thrown = assertThrows(IOException.class, () -> composed.test("foo", "bar"));
                assertEquals("foobar", thrown.getMessage());

                verify(predicate).test("foo", "bar");
                verify(predicate).and(other);
                verifyNoMoreInteractions(predicate, other);
            }
        }

        @Nested
        class ThisThrowsUnchecked {

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testOtherMatches(UncheckedThrowable<?> throwable) throws IOException {
                CheckedBiPredicate<String, String, IOException> predicate = Spied.checkedBiPredicate((s1, s2) -> throwable.throwUnchecked(s1 + s2));
                CheckedBiPredicate<String, String, IOException> other = Spied.checkedBiPredicate(String::equalsIgnoreCase);

                CheckedBiPredicate<String, String, IOException> composed = predicate.and(other);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> composed.test("foo", "FOO"));
                assertEquals("fooFOO", thrown.getMessage());

                verify(predicate).test("foo", "FOO");
                verify(predicate).and(other);
                verifyNoMoreInteractions(predicate, other);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testOtherDoesNotMatch(UncheckedThrowable<?> throwable) throws IOException {
                CheckedBiPredicate<String, String, IOException> predicate = Spied.checkedBiPredicate((s1, s2) -> throwable.throwUnchecked(s1 + s2));
                CheckedBiPredicate<String, String, IOException> other = Spied.checkedBiPredicate(String::equalsIgnoreCase);

                CheckedBiPredicate<String, String, IOException> composed = predicate.and(other);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> composed.test("foo", "bar"));
                assertEquals("foobar", thrown.getMessage());

                verify(predicate).test("foo", "bar");
                verify(predicate).and(other);
                verifyNoMoreInteractions(predicate, other);
            }
        }

        @Nested
        class OtherThrowsChecked {

            @Test
            void testThisMatches() throws IOException {
                CheckedBiPredicate<String, String, IOException> predicate = Spied.checkedBiPredicate(String::equalsIgnoreCase);
                CheckedBiPredicate<String, String, IOException> other = Spied.checkedBiPredicate((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                CheckedBiPredicate<String, String, IOException> composed = predicate.and(other);

                IOException thrown = assertThrows(IOException.class, () -> composed.test("foo", "FOO"));
                assertEquals("fooFOO", thrown.getMessage());

                verify(predicate).test("foo", "FOO");
                verify(predicate).and(other);
                verify(other).test("foo", "FOO");
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testThisDoesNotMatch() throws IOException {
                CheckedBiPredicate<String, String, IOException> predicate = Spied.checkedBiPredicate(String::equalsIgnoreCase);
                CheckedBiPredicate<String, String, IOException> other = Spied.checkedBiPredicate((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                CheckedBiPredicate<String, String, IOException> composed = predicate.and(other);

                assertFalse(composed.test("foo", "bar"));

                verify(predicate).test("foo", "bar");
                verify(predicate).and(other);
                verifyNoMoreInteractions(predicate, other);
            }
        }

        @Nested
        class OtherThrowsUnchecked {

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testThisMatches(UncheckedThrowable<?> throwable) throws IOException {
                CheckedBiPredicate<String, String, IOException> predicate = Spied.checkedBiPredicate(String::equalsIgnoreCase);
                CheckedBiPredicate<String, String, IOException> other = Spied.checkedBiPredicate((s1, s2) -> throwable.throwUnchecked(s1 + s2));

                CheckedBiPredicate<String, String, IOException> composed = predicate.and(other);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> composed.test("foo", "FOO"));
                assertEquals("fooFOO", thrown.getMessage());

                verify(predicate).test("foo", "FOO");
                verify(predicate).and(other);
                verify(other).test("foo", "FOO");
                verifyNoMoreInteractions(predicate, other);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testThisDoesNotMatch(UncheckedThrowable<?> throwable) throws IOException {
                CheckedBiPredicate<String, String, IOException> predicate = Spied.checkedBiPredicate(String::equalsIgnoreCase);
                CheckedBiPredicate<String, String, IOException> other = Spied.checkedBiPredicate((s1, s2) -> throwable.throwUnchecked(s1 + s2));

                CheckedBiPredicate<String, String, IOException> composed = predicate.and(other);

                assertFalse(composed.test("foo", "bar"));

                verify(predicate).test("foo", "bar");
                verify(predicate).and(other);
                verifyNoMoreInteractions(predicate, other);
            }
        }
    }

    @Nested
    class Negate {

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedBiPredicate<String, String, IOException> predicate = Spied.checkedBiPredicate(String::equalsIgnoreCase);

            CheckedBiPredicate<String, String, IOException> negated = predicate.negate();

            assertFalse(negated.test("foo", "FOO"));
            assertTrue(negated.test("foo", "bar"));

            verify(predicate).test("foo", "FOO");
            verify(predicate).test("foo", "bar");
            verify(predicate).negate();
            verifyNoMoreInteractions(predicate);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedBiPredicate<String, String, IOException> predicate = Spied.checkedBiPredicate((s1, s2) -> {
                throw new IOException(s1 + s2);
            });

            CheckedBiPredicate<String, String, IOException> negated = predicate.negate();

            IOException thrown = assertThrows(IOException.class, () -> negated.test("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());

            verify(predicate).test("foo", "bar");
            verify(predicate).negate();
            verifyNoMoreInteractions(predicate);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            CheckedBiPredicate<String, String, IOException> predicate = Spied.checkedBiPredicate((s1, s2) -> throwable.throwUnchecked(s1 + s2));

            CheckedBiPredicate<String, String, IOException> negated = predicate.negate();

            Throwable thrown = assertThrows(throwable.throwableType(), () -> negated.test("foo", "bar"));
            assertEquals("foobar", thrown.getMessage());

            verify(predicate).test("foo", "bar");
            verify(predicate).negate();
            verifyNoMoreInteractions(predicate);
        }
    }

    @Nested
    class Or {

        @Test
        void testNullArgument() {
            CheckedBiPredicate<String, String, IOException> predicate = Spied.checkedBiPredicate(String::equalsIgnoreCase);

            assertThrows(NullPointerException.class, () -> predicate.or(null));
        }

        @Nested
        class NeitherThrows {

            @Test
            void testThisMatches() throws IOException {
                CheckedBiPredicate<String, String, IOException> predicate = Spied.checkedBiPredicate(String::equalsIgnoreCase);
                CheckedBiPredicate<String, String, IOException> other = Spied.checkedBiPredicate(String::equals);

                CheckedBiPredicate<String, String, IOException> composed = predicate.or(other);

                assertTrue(composed.test("foo", "FOO"));

                verify(predicate).test("foo", "FOO");
                verify(predicate).or(other);
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testOtherMatches() throws IOException {
                CheckedBiPredicate<String, String, IOException> predicate = Spied.checkedBiPredicate(String::equals);
                CheckedBiPredicate<String, String, IOException> other = Spied.checkedBiPredicate(String::equalsIgnoreCase);

                CheckedBiPredicate<String, String, IOException> composed = predicate.or(other);

                assertTrue(composed.test("foo", "FOO"));

                verify(predicate).test("foo", "FOO");
                verify(predicate).or(other);
                verify(other).test("foo", "FOO");
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testBothMatch() throws IOException {
                CheckedBiPredicate<String, String, IOException> predicate = Spied.checkedBiPredicate(String::equals);
                CheckedBiPredicate<String, String, IOException> other = Spied.checkedBiPredicate(String::equalsIgnoreCase);

                CheckedBiPredicate<String, String, IOException> composed = predicate.or(other);

                assertTrue(composed.test("foo", "foo"));

                verify(predicate).test("foo", "foo");
                verify(predicate).or(other);
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testNeitherMatches() throws IOException {
                CheckedBiPredicate<String, String, IOException> predicate = Spied.checkedBiPredicate(String::equals);
                CheckedBiPredicate<String, String, IOException> other = Spied.checkedBiPredicate(String::equalsIgnoreCase);

                CheckedBiPredicate<String, String, IOException> composed = predicate.or(other);

                assertFalse(composed.test("foo", "bar"));

                verify(predicate).test("foo", "bar");
                verify(predicate).or(other);
                verify(other).test("foo", "bar");
                verifyNoMoreInteractions(predicate, other);
            }
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testOtherMatches() throws IOException {
                CheckedBiPredicate<String, String, IOException> predicate = Spied.checkedBiPredicate((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });
                CheckedBiPredicate<String, String, IOException> other = Spied.checkedBiPredicate(String::equalsIgnoreCase);

                CheckedBiPredicate<String, String, IOException> composed = predicate.or(other);

                IOException thrown = assertThrows(IOException.class, () -> composed.test("foo", "FOO"));
                assertEquals("fooFOO", thrown.getMessage());

                verify(predicate).test("foo", "FOO");
                verify(predicate).or(other);
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testOtherDoesNotMatch() throws IOException {
                CheckedBiPredicate<String, String, IOException> predicate = Spied.checkedBiPredicate((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });
                CheckedBiPredicate<String, String, IOException> other = Spied.checkedBiPredicate(String::equalsIgnoreCase);

                CheckedBiPredicate<String, String, IOException> composed = predicate.or(other);

                IOException thrown = assertThrows(IOException.class, () -> composed.test("foo", "bar"));
                assertEquals("foobar", thrown.getMessage());

                verify(predicate).test("foo", "bar");
                verify(predicate).or(other);
                verifyNoMoreInteractions(predicate, other);
            }
        }

        @Nested
        class ThisThrowsUnchecked {

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testOtherMatches(UncheckedThrowable<?> throwable) throws IOException {
                CheckedBiPredicate<String, String, IOException> predicate = Spied.checkedBiPredicate((s1, s2) -> throwable.throwUnchecked(s1 + s2));
                CheckedBiPredicate<String, String, IOException> other = Spied.checkedBiPredicate(String::equalsIgnoreCase);

                CheckedBiPredicate<String, String, IOException> composed = predicate.or(other);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> composed.test("foo", "FOO"));
                assertEquals("fooFOO", thrown.getMessage());

                verify(predicate).test("foo", "FOO");
                verify(predicate).or(other);
                verifyNoMoreInteractions(predicate, other);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testOtherDoesNotMatch(UncheckedThrowable<?> throwable) throws IOException {
                CheckedBiPredicate<String, String, IOException> predicate = Spied.checkedBiPredicate((s1, s2) -> throwable.throwUnchecked(s1 + s2));
                CheckedBiPredicate<String, String, IOException> other = Spied.checkedBiPredicate(String::equalsIgnoreCase);

                CheckedBiPredicate<String, String, IOException> composed = predicate.or(other);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> composed.test("foo", "bar"));
                assertEquals("foobar", thrown.getMessage());

                verify(predicate).test("foo", "bar");
                verify(predicate).or(other);
                verifyNoMoreInteractions(predicate, other);
            }
        }

        @Nested
        class OtherThrowsChecked {

            @Test
            void testThisMatches() throws IOException {
                CheckedBiPredicate<String, String, IOException> predicate = Spied.checkedBiPredicate(String::equalsIgnoreCase);
                CheckedBiPredicate<String, String, IOException> other = Spied.checkedBiPredicate((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                CheckedBiPredicate<String, String, IOException> composed = predicate.or(other);

                assertTrue(composed.test("foo", "FOO"));

                verify(predicate).test("foo", "FOO");
                verify(predicate).or(other);
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testThisDoesNotMatch() throws IOException {
                CheckedBiPredicate<String, String, IOException> predicate = Spied.checkedBiPredicate(String::equalsIgnoreCase);
                CheckedBiPredicate<String, String, IOException> other = Spied.checkedBiPredicate((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                CheckedBiPredicate<String, String, IOException> composed = predicate.or(other);

                IOException thrown = assertThrows(IOException.class, () -> composed.test("foo", "bar"));
                assertEquals("foobar", thrown.getMessage());

                verify(predicate).test("foo", "bar");
                verify(predicate).or(other);
                verify(other).test("foo", "bar");
                verifyNoMoreInteractions(predicate, other);
            }
        }

        @Nested
        class OtherThrowsUnchecked {

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testThisMatches(UncheckedThrowable<?> throwable) throws IOException {
                CheckedBiPredicate<String, String, IOException> predicate = Spied.checkedBiPredicate(String::equalsIgnoreCase);
                CheckedBiPredicate<String, String, IOException> other = Spied.checkedBiPredicate((s1, s2) -> throwable.throwUnchecked(s1 + s2));

                CheckedBiPredicate<String, String, IOException> composed = predicate.or(other);

                assertTrue(composed.test("foo", "FOO"));

                verify(predicate).test("foo", "FOO");
                verify(predicate).or(other);
                verifyNoMoreInteractions(predicate, other);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testThisDoesNotMatch(UncheckedThrowable<?> throwable) throws IOException {
                CheckedBiPredicate<String, String, IOException> predicate = Spied.checkedBiPredicate(String::equalsIgnoreCase);
                CheckedBiPredicate<String, String, IOException> other = Spied.checkedBiPredicate((s1, s2) -> throwable.throwUnchecked(s1 + s2));

                CheckedBiPredicate<String, String, IOException> composed = predicate.or(other);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> composed.test("foo", "bar"));
                assertEquals("foobar", thrown.getMessage());

                verify(predicate).test("foo", "bar");
                verify(predicate).or(other);
                verify(other).test("foo", "bar");
                verifyNoMoreInteractions(predicate, other);
            }
        }
    }

    @Nested
    class OnErrorThrowAsChecked {

        @Test
        void testNullArgument() {
            CheckedBiPredicate<String, String, IOException> predicate = Spied.checkedBiPredicate(String::equalsIgnoreCase);

            assertThrows(NullPointerException.class, () -> predicate.onErrorThrowAsChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedBiPredicate<String, String, IOException> predicate = Spied.checkedBiPredicate(String::equalsIgnoreCase);

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedBiPredicate<String, String, ExecutionException> throwing = predicate.onErrorThrowAsChecked(errorMapper);

            assertTrue(throwing.test("foo", "FOO"));

            verify(predicate).test("foo", "FOO");
            verify(predicate).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(predicate, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedBiPredicate<String, String, IOException> predicate = Spied.checkedBiPredicate((s1, s2) -> {
                throw new IOException(s1 + s2);
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedBiPredicate<String, String, ExecutionException> throwing = predicate.onErrorThrowAsChecked(errorMapper);

            ExecutionException thrown = assertThrows(ExecutionException.class, () -> throwing.test("foo", "FOO"));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("fooFOO", cause.getMessage());

            verify(predicate).test("foo", "FOO");
            verify(predicate).onErrorThrowAsChecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(predicate, errorMapper);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            CheckedBiPredicate<String, String, IOException> predicate = Spied.checkedBiPredicate((s1, s2) -> throwable.throwUnchecked(s1 + s2));

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedBiPredicate<String, String, ExecutionException> throwing = predicate.onErrorThrowAsChecked(errorMapper);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> throwing.test("foo", "FOO"));
            assertEquals("fooFOO", thrown.getMessage());

            verify(predicate).test("foo", "FOO");
            verify(predicate).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(predicate, errorMapper);
        }
    }

    @Nested
    class OnErrorThrowAsUnchecked {

        @Test
        void testNullArgument() {
            CheckedBiPredicate<String, String, IOException> predicate = Spied.checkedBiPredicate(String::equalsIgnoreCase);

            assertThrows(NullPointerException.class, () -> predicate.onErrorThrowAsUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedBiPredicate<String, String, IOException> predicate = Spied.checkedBiPredicate(String::equalsIgnoreCase);

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            BiPredicate<String, String> throwing = predicate.onErrorThrowAsUnchecked(errorMapper);

            assertTrue(throwing.test("foo", "FOO"));

            verify(predicate).test("foo", "FOO");
            verify(predicate).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(predicate, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedBiPredicate<String, String, IOException> predicate = Spied.checkedBiPredicate((s1, s2) -> {
                throw new IOException(s1 + s2);
            });

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            BiPredicate<String, String> throwing = predicate.onErrorThrowAsUnchecked(errorMapper);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> throwing.test("foo", "FOO"));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("fooFOO", cause.getMessage());

            verify(predicate).test("foo", "FOO");
            verify(predicate).onErrorThrowAsUnchecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(predicate, errorMapper);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            CheckedBiPredicate<String, String, IOException> predicate = Spied.checkedBiPredicate((s1, s2) -> throwable.throwUnchecked(s1 + s2));

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            BiPredicate<String, String> throwing = predicate.onErrorThrowAsUnchecked(errorMapper);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> throwing.test("foo", "FOO"));
            assertEquals("fooFOO", thrown.getMessage());

            verify(predicate).test("foo", "FOO");
            verify(predicate).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(predicate, errorMapper);
        }
    }

    @Nested
    class OnErrorHandleChecked {

        @Test
        void testNullArgument() {
            CheckedBiPredicate<String, String, IOException> predicate = Spied.checkedBiPredicate(String::equalsIgnoreCase);

            assertThrows(NullPointerException.class, () -> predicate.onErrorHandleChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedBiPredicate<String, String, IOException> predicate = Spied.checkedBiPredicate(String::equalsIgnoreCase);

            CheckedPredicate<IOException, ExecutionException> errorHandler = Spied.checkedPredicate(e -> e.getMessage() == null);

            CheckedBiPredicate<String, String, ExecutionException> handling = predicate.onErrorHandleChecked(errorHandler);

            assertTrue(handling.test("foo", "FOO"));

            verify(predicate).test("foo", "FOO");
            verify(predicate).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(predicate, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException, ExecutionException {
                CheckedBiPredicate<String, String, IOException> predicate = Spied.checkedBiPredicate((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                CheckedPredicate<IOException, ExecutionException> errorHandler = Spied.checkedPredicate(e -> e.getMessage() == null);

                CheckedBiPredicate<String, String, ExecutionException> handling = predicate.onErrorHandleChecked(errorHandler);

                assertFalse(handling.test("foo", "FOO"));

                verify(predicate).test("foo", "FOO");
                verify(predicate).onErrorHandleChecked(errorHandler);
                verify(errorHandler).test(any());
                verifyNoMoreInteractions(predicate, errorHandler);
            }

            @Test
            void testHandlerThrowsChecked() throws IOException, ExecutionException {
                CheckedBiPredicate<String, String, IOException> predicate = Spied.checkedBiPredicate((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                CheckedPredicate<IOException, ExecutionException> errorHandler = Spied.checkedPredicate(e -> {
                    throw new ExecutionException(e);
                });

                CheckedBiPredicate<String, String, ExecutionException> handling = predicate.onErrorHandleChecked(errorHandler);

                ExecutionException thrown = assertThrows(ExecutionException.class, () -> handling.test("foo", "FOO"));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("fooFOO", cause.getMessage());

                verify(predicate).test("foo", "FOO");
                verify(predicate).onErrorHandleChecked(errorHandler);
                verify(errorHandler).test(any());
                verifyNoMoreInteractions(predicate, errorHandler);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testHandlerThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ExecutionException {
                CheckedBiPredicate<String, String, IOException> predicate = Spied.checkedBiPredicate((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                CheckedPredicate<IOException, ExecutionException> errorHandler = Spied.checkedPredicate(throwable::throwUnchecked);

                CheckedBiPredicate<String, String, ExecutionException> handling = predicate.onErrorHandleChecked(errorHandler);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.test("foo", "FOO"));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("fooFOO", cause.getMessage());

                verify(predicate).test("foo", "FOO");
                verify(predicate).onErrorHandleChecked(errorHandler);
                verify(errorHandler).test(any());
                verifyNoMoreInteractions(predicate, errorHandler);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            CheckedBiPredicate<String, String, IOException> predicate = Spied.checkedBiPredicate((s1, s2) -> throwable.throwUnchecked(s1 + s2));

            CheckedPredicate<IOException, ExecutionException> errorHandler = Spied.checkedPredicate(e -> e.getMessage() == null);

            CheckedBiPredicate<String, String, ExecutionException> handling = predicate.onErrorHandleChecked(errorHandler);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.test("foo", "FOO"));
            assertEquals("fooFOO", thrown.getMessage());

            verify(predicate).test("foo", "FOO");
            verify(predicate).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(predicate, errorHandler);
        }
    }

    @Nested
    class OnErrorHandleUnchecked {

        @Test
        void testNullArgument() {
            CheckedBiPredicate<String, String, IOException> predicate = Spied.checkedBiPredicate(String::equalsIgnoreCase);

            assertThrows(NullPointerException.class, () -> predicate.onErrorHandleUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedBiPredicate<String, String, IOException> predicate = Spied.checkedBiPredicate(String::equalsIgnoreCase);

            Predicate<IOException> errorHandler = Spied.predicate(e -> e.getMessage() == null);

            BiPredicate<String, String> handling = predicate.onErrorHandleUnchecked(errorHandler);

            assertTrue(handling.test("foo", "FOO"));

            verify(predicate).test("foo", "FOO");
            verify(predicate).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(predicate, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException {
                CheckedBiPredicate<String, String, IOException> predicate = Spied.checkedBiPredicate((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                Predicate<IOException> errorHandler = Spied.predicate(e -> e.getMessage() == null);

                BiPredicate<String, String> handling = predicate.onErrorHandleUnchecked(errorHandler);

                assertFalse(handling.test("foo", "FOO"));

                verify(predicate).test("foo", "FOO");
                verify(predicate).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).test(any());
                verifyNoMoreInteractions(predicate, errorHandler);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testHandlerThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                CheckedBiPredicate<String, String, IOException> predicate = Spied.checkedBiPredicate((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                Predicate<IOException> errorHandler = Spied.predicate(throwable::throwUnchecked);

                BiPredicate<String, String> handling = predicate.onErrorHandleUnchecked(errorHandler);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.test("foo", "FOO"));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("fooFOO", cause.getMessage());

                verify(predicate).test("foo", "FOO");
                verify(predicate).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).test(any());
                verifyNoMoreInteractions(predicate, errorHandler);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            CheckedBiPredicate<String, String, IOException> predicate = Spied.checkedBiPredicate((s1, s2) -> throwable.throwUnchecked(s1 + s2));

            Predicate<IOException> errorHandler = Spied.predicate(e -> e.getMessage() == null);

            BiPredicate<String, String> handling = predicate.onErrorHandleUnchecked(errorHandler);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> handling.test("foo", "FOO"));
            assertEquals("fooFOO", thrown.getMessage());

            verify(predicate).test("foo", "FOO");
            verify(predicate).onErrorHandleUnchecked(errorHandler);
            verifyNoMoreInteractions(predicate, errorHandler);
        }
    }

    @Nested
    class OnErrorTestChecked {

        @Test
        void testNullArgument() {
            CheckedBiPredicate<String, String, IOException> predicate = Spied.checkedBiPredicate(String::equalsIgnoreCase);

            assertThrows(NullPointerException.class, () -> predicate.onErrorTestChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedBiPredicate<String, String, IOException> predicate = Spied.checkedBiPredicate(String::equalsIgnoreCase);

            CheckedBiPredicate<String, String, ExecutionException> fallback = Spied.checkedBiPredicate(String::equals);

            CheckedBiPredicate<String, String, ExecutionException> testing = predicate.onErrorTestChecked(fallback);

            assertTrue(testing.test("foo", "FOO"));

            verify(predicate).test("foo", "FOO");
            verify(predicate).onErrorTestChecked(fallback);
            verifyNoMoreInteractions(predicate, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                CheckedBiPredicate<String, String, IOException> predicate = Spied.checkedBiPredicate((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                CheckedBiPredicate<String, String, ParseException> fallback = Spied.checkedBiPredicate(String::equals);

                CheckedBiPredicate<String, String, ParseException> testing = predicate.onErrorTestChecked(fallback);

                assertFalse(testing.test("foo", "FOO"));

                verify(predicate).test("foo", "FOO");
                verify(predicate).onErrorTestChecked(fallback);
                verify(fallback).test("foo", "FOO");
                verifyNoMoreInteractions(predicate, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                CheckedBiPredicate<String, String, IOException> predicate = Spied.checkedBiPredicate((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                CheckedBiPredicate<String, String, ParseException> fallback = Spied.checkedBiPredicate((s1, s2) -> {
                    throw new ParseException(s1 + s2, 0);
                });

                CheckedBiPredicate<String, String, ParseException> testing = predicate.onErrorTestChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> testing.test("foo", "FOO"));
                assertEquals("fooFOO", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(predicate).test("foo", "FOO");
                verify(predicate).onErrorTestChecked(fallback);
                verify(fallback).test("foo", "FOO");
                verifyNoMoreInteractions(predicate, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ParseException {
                CheckedBiPredicate<String, String, IOException> predicate = Spied.checkedBiPredicate((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                CheckedBiPredicate<String, String, ParseException> fallback = Spied.checkedBiPredicate((s1, s2) -> throwable.throwUnchecked(s1 + s2));

                CheckedBiPredicate<String, String, ParseException> testing = predicate.onErrorTestChecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> testing.test("foo", "FOO"));
                assertEquals("fooFOO", thrown.getMessage());

                verify(predicate).test("foo", "FOO");
                verify(predicate).onErrorTestChecked(fallback);
                verify(fallback).test("foo", "FOO");
                verifyNoMoreInteractions(predicate, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            CheckedBiPredicate<String, String, IOException> predicate = Spied.checkedBiPredicate((s1, s2) -> throwable.throwUnchecked(s1 + s2));

            CheckedBiPredicate<String, String, ParseException> fallback = Spied.checkedBiPredicate(String::equalsIgnoreCase);

            CheckedBiPredicate<String, String, ParseException> testing = predicate.onErrorTestChecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> testing.test("foo", "FOO"));
            assertEquals("fooFOO", thrown.getMessage());

            verify(predicate).test("foo", "FOO");
            verify(predicate).onErrorTestChecked(fallback);
            verifyNoMoreInteractions(predicate, fallback);
        }
    }

    @Nested
    class OnErrorTestUnchecked {

        @Test
        void testNullArgument() {
            CheckedBiPredicate<String, String, IOException> predicate = Spied.checkedBiPredicate(String::equalsIgnoreCase);

            assertThrows(NullPointerException.class, () -> predicate.onErrorTestUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedBiPredicate<String, String, IOException> predicate = Spied.checkedBiPredicate(String::equalsIgnoreCase);

            BiPredicate<String, String> fallback = Spied.biPredicate(String::equals);

            BiPredicate<String, String> testing = predicate.onErrorTestUnchecked(fallback);

            assertTrue(testing.test("foo", "FOO"));

            verify(predicate).test("foo", "FOO");
            verify(predicate).onErrorTestUnchecked(fallback);
            verifyNoMoreInteractions(predicate, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                CheckedBiPredicate<String, String, IOException> predicate = Spied.checkedBiPredicate((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                BiPredicate<String, String> fallback = Spied.biPredicate(String::equals);

                BiPredicate<String, String> testing = predicate.onErrorTestUnchecked(fallback);

                assertFalse(testing.test("foo", "FOO"));

                verify(predicate).test("foo", "FOO");
                verify(predicate).onErrorTestUnchecked(fallback);
                verify(fallback).test("foo", "FOO");
                verifyNoMoreInteractions(predicate, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                CheckedBiPredicate<String, String, IOException> predicate = Spied.checkedBiPredicate((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                BiPredicate<String, String> fallback = Spied.biPredicate((s1, s2) -> throwable.throwUnchecked(s1 + s2));

                BiPredicate<String, String> testing = predicate.onErrorTestUnchecked(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> testing.test("foo", "FOO"));
                assertEquals("fooFOO", thrown.getMessage());

                verify(predicate).test("foo", "FOO");
                verify(predicate).onErrorTestUnchecked(fallback);
                verify(fallback).test("foo", "FOO");
                verifyNoMoreInteractions(predicate, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            CheckedBiPredicate<String, String, IOException> predicate = Spied.checkedBiPredicate((s1, s2) -> throwable.throwUnchecked(s1 + s2));

            BiPredicate<String, String> fallback = Spied.biPredicate(String::equalsIgnoreCase);

            BiPredicate<String, String> testing = predicate.onErrorTestUnchecked(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> testing.test("foo", "FOO"));
            assertEquals("fooFOO", thrown.getMessage());

            verify(predicate).test("foo", "FOO");
            verify(predicate).onErrorTestUnchecked(fallback);
            verifyNoMoreInteractions(predicate, fallback);
        }
    }

    @Nested
    class OnErrorGetCheckedAsBoolean {

        @Test
        void testNullArgument() {
            CheckedBiPredicate<String, String, IOException> predicate = Spied.checkedBiPredicate(String::equalsIgnoreCase);

            assertThrows(NullPointerException.class, () -> predicate.onErrorGetCheckedAsBoolean(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedBiPredicate<String, String, IOException> predicate = Spied.checkedBiPredicate(String::equalsIgnoreCase);

            CheckedBooleanSupplier<ExecutionException> fallback = Spied.checkedBooleanSupplier(() -> false);

            CheckedBiPredicate<String, String, ExecutionException> getting = predicate.onErrorGetCheckedAsBoolean(fallback);

            assertTrue(getting.test("foo", "FOO"));

            verify(predicate).test("foo", "FOO");
            verify(predicate).onErrorGetCheckedAsBoolean(fallback);
            verifyNoMoreInteractions(predicate, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                CheckedBiPredicate<String, String, IOException> predicate = Spied.checkedBiPredicate((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                CheckedBooleanSupplier<ParseException> fallback = Spied.checkedBooleanSupplier(() -> false);

                CheckedBiPredicate<String, String, ParseException> getting = predicate.onErrorGetCheckedAsBoolean(fallback);

                assertFalse(getting.test("foo", "FOO"));

                verify(predicate).test("foo", "FOO");
                verify(predicate).onErrorGetCheckedAsBoolean(fallback);
                verify(fallback).getAsBoolean();
                verifyNoMoreInteractions(predicate, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                CheckedBiPredicate<String, String, IOException> predicate = Spied.checkedBiPredicate((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                CheckedBooleanSupplier<ParseException> fallback = Spied.checkedBooleanSupplier(() -> {
                    throw new ParseException("bar", 0);
                });

                CheckedBiPredicate<String, String, ParseException> getting = predicate.onErrorGetCheckedAsBoolean(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> getting.test("foo", "FOO"));
                assertEquals("bar", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(predicate).test("foo", "FOO");
                verify(predicate).onErrorGetCheckedAsBoolean(fallback);
                verify(fallback).getAsBoolean();
                verifyNoMoreInteractions(predicate, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException, ParseException {
                CheckedBiPredicate<String, String, IOException> predicate = Spied.checkedBiPredicate((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                CheckedBooleanSupplier<ParseException> fallback = Spied.checkedBooleanSupplier(() -> throwable.throwUnchecked("bar"));

                CheckedBiPredicate<String, String, ParseException> getting = predicate.onErrorGetCheckedAsBoolean(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.test("foo", "FOO"));
                assertEquals("bar", thrown.getMessage());

                verify(predicate).test("foo", "FOO");
                verify(predicate).onErrorGetCheckedAsBoolean(fallback);
                verify(fallback).getAsBoolean();
                verifyNoMoreInteractions(predicate, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            CheckedBiPredicate<String, String, IOException> predicate = Spied.checkedBiPredicate((s1, s2) -> throwable.throwUnchecked(s1 + s2));

            CheckedBooleanSupplier<ParseException> fallback = Spied.checkedBooleanSupplier(() -> false);

            CheckedBiPredicate<String, String, ParseException> getting = predicate.onErrorGetCheckedAsBoolean(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.test("foo", "FOO"));
            assertEquals("fooFOO", thrown.getMessage());

            verify(predicate).test("foo", "FOO");
            verify(predicate).onErrorGetCheckedAsBoolean(fallback);
            verifyNoMoreInteractions(predicate, fallback);
        }
    }

    @Nested
    class OnErrorGetUncheckedAsBoolean {

        @Test
        void testNullArgument() {
            CheckedBiPredicate<String, String, IOException> predicate = Spied.checkedBiPredicate(String::equalsIgnoreCase);

            assertThrows(NullPointerException.class, () -> predicate.onErrorGetUncheckedAsBoolean(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedBiPredicate<String, String, IOException> predicate = Spied.checkedBiPredicate(String::equalsIgnoreCase);

            BooleanSupplier fallback = Spied.booleanSupplier(() -> false);

            BiPredicate<String, String> getting = predicate.onErrorGetUncheckedAsBoolean(fallback);

            assertTrue(getting.test("foo", "FOO"));

            verify(predicate).test("foo", "FOO");
            verify(predicate).onErrorGetUncheckedAsBoolean(fallback);
            verifyNoMoreInteractions(predicate, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException {
                CheckedBiPredicate<String, String, IOException> predicate = Spied.checkedBiPredicate((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                BooleanSupplier fallback = Spied.booleanSupplier(() -> false);

                BiPredicate<String, String> getting = predicate.onErrorGetUncheckedAsBoolean(fallback);

                assertFalse(getting.test("foo", "FOO"));

                verify(predicate).test("foo", "FOO");
                verify(predicate).onErrorGetUncheckedAsBoolean(fallback);
                verify(fallback).getAsBoolean();
                verifyNoMoreInteractions(predicate, fallback);
            }

            @ParameterizedTest
            @ArgumentsSource(UncheckedThrowable.Provider.class)
            void testFallbackThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
                CheckedBiPredicate<String, String, IOException> predicate = Spied.checkedBiPredicate((s1, s2) -> {
                    throw new IOException(s1 + s2);
                });

                BooleanSupplier fallback = Spied.booleanSupplier(() -> throwable.throwUnchecked("bar"));

                BiPredicate<String, String> getting = predicate.onErrorGetUncheckedAsBoolean(fallback);

                Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.test("foo", "FOO"));
                assertEquals("bar", thrown.getMessage());

                verify(predicate).test("foo", "FOO");
                verify(predicate).onErrorGetUncheckedAsBoolean(fallback);
                verify(fallback).getAsBoolean();
                verifyNoMoreInteractions(predicate, fallback);
            }
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            CheckedBiPredicate<String, String, IOException> predicate = Spied.checkedBiPredicate((s1, s2) -> throwable.throwUnchecked(s1 + s2));

            BooleanSupplier fallback = Spied.booleanSupplier(() -> false);

            BiPredicate<String, String> getting = predicate.onErrorGetUncheckedAsBoolean(fallback);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> getting.test("foo", "FOO"));
            assertEquals("fooFOO", thrown.getMessage());

            verify(predicate).test("foo", "FOO");
            verify(predicate).onErrorGetUncheckedAsBoolean(fallback);
            verifyNoMoreInteractions(predicate, fallback);
        }
    }

    @Nested
    class OnErrorReturn {

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedBiPredicate<String, String, IOException> predicate = Spied.checkedBiPredicate(String::equalsIgnoreCase);

            BiPredicate<String, String> returning = predicate.onErrorReturn(false);

            assertTrue(returning.test("foo", "FOO"));

            verify(predicate).test("foo", "FOO");
            verify(predicate).onErrorReturn(false);
            verifyNoMoreInteractions(predicate);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedBiPredicate<String, String, IOException> predicate = Spied.checkedBiPredicate((s1, s2) -> {
                throw new IOException(s1 + s2);
            });

            BiPredicate<String, String> returning = predicate.onErrorReturn(false);

            assertFalse(returning.test("foo", "FOO"));

            verify(predicate).test("foo", "FOO");
            verify(predicate).onErrorReturn(false);
            verifyNoMoreInteractions(predicate);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            CheckedBiPredicate<String, String, IOException> predicate = Spied.checkedBiPredicate((s1, s2) -> throwable.throwUnchecked(s1 + s2));

            BiPredicate<String, String> returning = predicate.onErrorReturn(false);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> returning.test("foo", "FOO"));
            assertEquals("fooFOO", thrown.getMessage());

            verify(predicate).test("foo", "FOO");
            verify(predicate).onErrorReturn(false);
            verifyNoMoreInteractions(predicate);
        }
    }

    @Nested
    class Unchecked {

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedBiPredicate<String, String, IOException> predicate = Spied.checkedBiPredicate(String::equalsIgnoreCase);

            BiPredicate<String, String> unchecked = predicate.unchecked();

            assertTrue(unchecked.test("foo", "FOO"));

            verify(predicate).test("foo", "FOO");
            verify(predicate).unchecked();
            verify(predicate).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(predicate);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedBiPredicate<String, String, IOException> predicate = Spied.checkedBiPredicate((s1, s2) -> {
                throw new IOException(s1 + s2);
            });

            BiPredicate<String, String> unchecked = predicate.unchecked();

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.test("foo", "FOO"));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("fooFOO", cause.getMessage());

            verify(predicate).test("foo", "FOO");
            verify(predicate).unchecked();
            verify(predicate).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(predicate);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testThisThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            CheckedBiPredicate<String, String, IOException> predicate = Spied.checkedBiPredicate((s1, s2) -> throwable.throwUnchecked(s1 + s2));

            BiPredicate<String, String> unchecked = predicate.unchecked();

            Throwable thrown = assertThrows(throwable.throwableType(), () -> unchecked.test("foo", "FOO"));
            assertEquals("fooFOO", thrown.getMessage());

            verify(predicate).test("foo", "FOO");
            verify(predicate).unchecked();
            verify(predicate).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(predicate);
        }
    }

    @Nested
    class Of {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> CheckedBiPredicate.of(null));
        }

        @Test
        void testNonNullArgument() throws IOException {
            CheckedBiPredicate<String, String, IOException> predicate = CheckedBiPredicate.of(String::equalsIgnoreCase);

            assertTrue(predicate.test("foo", "FOO"));
        }
    }

    @Nested
    class Not {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> CheckedBiPredicate.not(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedBiPredicate<String, String, IOException> predicate = Spied.checkedBiPredicate(String::equalsIgnoreCase);

            CheckedBiPredicate<String, String, IOException> negated = CheckedBiPredicate.not(predicate);

            assertFalse(negated.test("foo", "FOO"));
            assertTrue(negated.test("foo", "bar"));

            verify(predicate).negate();
            verify(predicate).test("foo", "FOO");
            verify(predicate).test("foo", "bar");
            verifyNoMoreInteractions(predicate);
        }

        @Test
        void testArgumentThrowsChecked() throws IOException {
            CheckedBiPredicate<String, String, IOException> predicate = Spied.checkedBiPredicate((s1, s2) -> {
                throw new IOException(s1 + s2);
            });

            CheckedBiPredicate<String, String, IOException> negated = CheckedBiPredicate.not(predicate);

            IOException thrown = assertThrows(IOException.class, () -> negated.test("foo", "foo"));
            assertEquals("foofoo", thrown.getMessage());

            verify(predicate).negate();
            verify(predicate).test("foo", "foo");
            verifyNoMoreInteractions(predicate);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testArgumentThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            CheckedBiPredicate<String, String, IOException> predicate = Spied.checkedBiPredicate((s1, s2) -> throwable.throwUnchecked(s1 + s2));

            CheckedBiPredicate<String, String, IOException> negated = CheckedBiPredicate.not(predicate);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> negated.test("foo", "foo"));
            assertEquals("foofoo", thrown.getMessage());

            verify(predicate).negate();
            verify(predicate).test("foo", "foo");
            verifyNoMoreInteractions(predicate);
        }
    }

    @Nested
    class UncheckedFactory {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> CheckedBiPredicate.unchecked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedBiPredicate<String, String, IOException> predicate = Spied.checkedBiPredicate(String::equalsIgnoreCase);

            BiPredicate<String, String> unchecked = CheckedBiPredicate.unchecked(predicate);

            assertTrue(unchecked.test("foo", "FOO"));

            verify(predicate).unchecked();
            verify(predicate).onErrorThrowAsUnchecked(any());
            verify(predicate).test("foo", "FOO");
            verifyNoMoreInteractions(predicate);
        }

        @Test
        void testArgumentThrowsChecked() throws IOException {
            CheckedBiPredicate<String, String, IOException> predicate = Spied.checkedBiPredicate((s1, s2) -> {
                throw new IOException(s1 + s2);
            });

            BiPredicate<String, String> unchecked = CheckedBiPredicate.unchecked(predicate);

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.test("foo", "FOO"));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("fooFOO", cause.getMessage());

            verify(predicate).unchecked();
            verify(predicate).onErrorThrowAsUnchecked(any());
            verify(predicate).test("foo", "FOO");
            verifyNoMoreInteractions(predicate);
        }

        @ParameterizedTest
        @ArgumentsSource(UncheckedThrowable.Provider.class)
        void testArgumentThrowsUnchecked(UncheckedThrowable<?> throwable) throws IOException {
            CheckedBiPredicate<String, String, IOException> predicate = Spied.checkedBiPredicate((s1, s2) -> throwable.throwUnchecked(s1 + s2));

            BiPredicate<String, String> unchecked = CheckedBiPredicate.unchecked(predicate);

            Throwable thrown = assertThrows(throwable.throwableType(), () -> unchecked.test("foo", "FOO"));
            assertEquals("fooFOO", thrown.getMessage());

            verify(predicate).unchecked();
            verify(predicate).onErrorThrowAsUnchecked(any());
            verify(predicate).test("foo", "FOO");
            verifyNoMoreInteractions(predicate);
        }
    }

    @Nested
    class CheckedWrapper {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> CheckedBiPredicate.checked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedBiPredicate<String, String, IOException> predicate = CheckedBiPredicate.checked(String::equalsIgnoreCase);

            assertTrue(predicate.test("foo", "FOO"));
        }

        @Test
        void testArgumentThrowsUnchecked() {
            CheckedBiPredicate<String, String, IOException> predicate = CheckedBiPredicate.checked((s1, s2) -> {
                throw new UncheckedException(s1 + s2, new IOException());
            });

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> predicate.test("foo", "FOO"));
            assertEquals("fooFOO", thrown.getMessage());
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertNull(cause.getMessage());
        }
    }

    @Nested
    class CheckedUnwrapper {

        @Test
        void testNullArguments() {
            BiPredicate<String, String> predicate = String::equalsIgnoreCase;

            assertThrows(NullPointerException.class, () -> CheckedBiPredicate.checked(null, IOException.class));
            assertThrows(NullPointerException.class, () -> CheckedBiPredicate.checked(predicate, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedBiPredicate<String, String, IOException> predicate = CheckedBiPredicate.checked(String::equalsIgnoreCase, IOException.class);

            assertTrue(predicate.test("foo", "FOO"));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                CheckedBiPredicate<String, String, IOException> predicate = CheckedBiPredicate.checked((s1, s2) -> {
                    throw new UncheckedException(new IOException(s1 + s2));
                }, IOException.class);

                IOException thrown = assertThrows(IOException.class, () -> predicate.test("foo", "FOO"));
                assertEquals("fooFOO", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                CheckedBiPredicate<String, String, IOException> predicate = CheckedBiPredicate.checked((s1, s2) -> {
                    throw new UncheckedException(new FileNotFoundException(s1 + s2));
                }, IOException.class);

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class, () -> predicate.test("foo", "FOO"));
                assertEquals("fooFOO", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                CheckedBiPredicate<String, String, IOException> predicate = CheckedBiPredicate.checked((s1, s2) -> {
                    throw new UncheckedException(new ParseException(s1 + s2, 0));
                }, IOException.class);

                UncheckedException thrown = assertThrows(UncheckedException.class, () -> predicate.test("foo", "FOO"));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("fooFOO", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            CheckedBiPredicate<String, String, IOException> predicate = CheckedBiPredicate.checked((s1, s2) -> {
                throw new IllegalStateException(s1 + s2);
            }, IOException.class);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> predicate.test("foo", "FOO"));
            assertEquals("fooFOO", thrown.getMessage());
        }
    }

    @Nested
    class InvokeAndUnwrap {

        @Test
        void testNullArguments() throws IOException {
            BiPredicate<String, String> predicate = Objects::equals;

            assertThrows(NullPointerException.class, () -> CheckedBiPredicate.invokeAndUnwrap(null, "foo", "bar", IOException.class));
            assertThrows(NullPointerException.class, () -> CheckedBiPredicate.invokeAndUnwrap(predicate, "foo", "bar", null));

            assertFalse(CheckedBiPredicate.invokeAndUnwrap(predicate, null, "bar", IOException.class));
            assertFalse(CheckedBiPredicate.invokeAndUnwrap(predicate, "foo", null, IOException.class));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            BiPredicate<String, String> predicate = String::equalsIgnoreCase;

            assertTrue(CheckedBiPredicate.invokeAndUnwrap(predicate, "foo", "FOO", IOException.class));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                BiPredicate<String, String> predicate = (s1, s2) -> {
                    throw new UncheckedException(new IOException(s1 + s2));
                };

                IOException thrown = assertThrows(IOException.class,
                        () -> CheckedBiPredicate.invokeAndUnwrap(predicate, "foo", "bar", IOException.class));
                assertEquals("foobar", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                BiPredicate<String, String> predicate = (s1, s2) -> {
                    throw new UncheckedException(new FileNotFoundException(s1 + s2));
                };

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class,
                        () -> CheckedBiPredicate.invokeAndUnwrap(predicate, "foo", "bar", IOException.class));
                assertEquals("foobar", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                BiPredicate<String, String> predicate = (s1, s2) -> {
                    throw new UncheckedException(new ParseException(s1 + s2, 0));
                };

                UncheckedException thrown = assertThrows(UncheckedException.class,
                        () -> CheckedBiPredicate.invokeAndUnwrap(predicate, "foo", "bar", IOException.class));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("foobar", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            BiPredicate<String, String> predicate = (s1, s2) -> {
                throw new IllegalStateException(s1 + s2);
            };

            IllegalStateException thrown = assertThrows(IllegalStateException.class,
                    () -> CheckedBiPredicate.invokeAndUnwrap(predicate, "foo", "bar", IOException.class));
            assertEquals("foobar", thrown.getMessage());
        }
    }
}
