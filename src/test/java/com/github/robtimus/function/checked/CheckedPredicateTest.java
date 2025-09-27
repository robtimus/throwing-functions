/*
 * CheckedPredicateTest.java
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
import java.util.function.Predicate;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("nls")
class CheckedPredicateTest {

    @Nested
    class And {

        @Test
        void testNullArgument() {
            CheckedPredicate<String, IOException> predicate = Spied.checkedPredicate(String::isEmpty);

            assertThrows(NullPointerException.class, () -> predicate.and(null));
        }

        @Nested
        class NeitherThrows {

            @Test
            void testThisMatches() throws IOException {
                CheckedPredicate<String, IOException> predicate = Spied.checkedPredicate(String::isBlank);
                CheckedPredicate<String, IOException> other = Spied.checkedPredicate(String::isEmpty);

                CheckedPredicate<String, IOException> composed = predicate.and(other);

                assertFalse(composed.test(" "));

                verify(predicate).test(" ");
                verify(predicate).and(other);
                verify(other).test(" ");
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testOtherMatches() throws IOException {
                CheckedPredicate<String, IOException> predicate = Spied.checkedPredicate(String::isEmpty);
                CheckedPredicate<String, IOException> other = Spied.checkedPredicate(String::isBlank);

                CheckedPredicate<String, IOException> composed = predicate.and(other);

                assertFalse(composed.test(" "));

                verify(predicate).test(" ");
                verify(predicate).and(other);
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testBothMatch() throws IOException {
                CheckedPredicate<String, IOException> predicate = Spied.checkedPredicate(String::isEmpty);
                CheckedPredicate<String, IOException> other = Spied.checkedPredicate(String::isBlank);

                CheckedPredicate<String, IOException> composed = predicate.and(other);

                assertTrue(composed.test(""));

                verify(predicate).test("");
                verify(predicate).and(other);
                verify(other).test("");
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testNeitherMatches() throws IOException {
                CheckedPredicate<String, IOException> predicate = Spied.checkedPredicate(String::isEmpty);
                CheckedPredicate<String, IOException> other = Spied.checkedPredicate(String::isBlank);

                CheckedPredicate<String, IOException> composed = predicate.and(other);

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
                CheckedPredicate<String, IOException> predicate = Spied.checkedPredicate(s -> {
                    throw new IOException(s);
                });
                CheckedPredicate<String, IOException> other = Spied.checkedPredicate(String::isBlank);

                CheckedPredicate<String, IOException> composed = predicate.and(other);

                IOException thrown = assertThrows(IOException.class, () -> composed.test(" "));
                assertEquals(" ", thrown.getMessage());

                verify(predicate).test(" ");
                verify(predicate).and(other);
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testOtherDoesNotMatch() throws IOException {
                CheckedPredicate<String, IOException> predicate = Spied.checkedPredicate(s -> {
                    throw new IOException(s);
                });
                CheckedPredicate<String, IOException> other = Spied.checkedPredicate(String::isBlank);

                CheckedPredicate<String, IOException> composed = predicate.and(other);

                IOException thrown = assertThrows(IOException.class, () -> composed.test("foo"));
                assertEquals("foo", thrown.getMessage());

                verify(predicate).test("foo");
                verify(predicate).and(other);
                verifyNoMoreInteractions(predicate, other);
            }
        }

        @Nested
        class ThisThrowsUnchecked {

            @Test
            void testOtherMatches() throws IOException {
                CheckedPredicate<String, IOException> predicate = Spied.checkedPredicate(s -> {
                    throw new IllegalStateException(s);
                });
                CheckedPredicate<String, IOException> other = Spied.checkedPredicate(String::isBlank);

                CheckedPredicate<String, IOException> composed = predicate.and(other);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> composed.test(" "));
                assertEquals(" ", thrown.getMessage());

                verify(predicate).test(" ");
                verify(predicate).and(other);
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testOtherDoesNotMatch() throws IOException {
                CheckedPredicate<String, IOException> predicate = Spied.checkedPredicate(s -> {
                    throw new IllegalStateException(s);
                });
                CheckedPredicate<String, IOException> other = Spied.checkedPredicate(String::isBlank);

                CheckedPredicate<String, IOException> composed = predicate.and(other);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> composed.test("foo"));
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
                CheckedPredicate<String, IOException> predicate = Spied.checkedPredicate(String::isBlank);
                CheckedPredicate<String, IOException> other = Spied.checkedPredicate(s -> {
                    throw new IOException(s);
                });

                CheckedPredicate<String, IOException> composed = predicate.and(other);

                IOException thrown = assertThrows(IOException.class, () -> composed.test(" "));
                assertEquals(" ", thrown.getMessage());

                verify(predicate).test(" ");
                verify(predicate).and(other);
                verify(other).test(" ");
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testThisDoesNotMatch() throws IOException {
                CheckedPredicate<String, IOException> predicate = Spied.checkedPredicate(String::isBlank);
                CheckedPredicate<String, IOException> other = Spied.checkedPredicate(s -> {
                    throw new IOException(s);
                });

                CheckedPredicate<String, IOException> composed = predicate.and(other);

                assertFalse(composed.test("foo"));

                verify(predicate).test("foo");
                verify(predicate).and(other);
                verifyNoMoreInteractions(predicate, other);
            }
        }

        @Nested
        class OtherThrowsUnchecked {

            @Test
            void testThisMatches() throws IOException {
                CheckedPredicate<String, IOException> predicate = Spied.checkedPredicate(String::isBlank);
                CheckedPredicate<String, IOException> other = Spied.checkedPredicate(s -> {
                    throw new IllegalStateException(s);
                });

                CheckedPredicate<String, IOException> composed = predicate.and(other);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> composed.test(" "));
                assertEquals(" ", thrown.getMessage());

                verify(predicate).test(" ");
                verify(predicate).and(other);
                verify(other).test(" ");
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testThisDoesNotMatch() throws IOException {
                CheckedPredicate<String, IOException> predicate = Spied.checkedPredicate(String::isBlank);
                CheckedPredicate<String, IOException> other = Spied.checkedPredicate(s -> {
                    throw new IOException(s);
                });

                CheckedPredicate<String, IOException> composed = predicate.and(other);

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
            CheckedPredicate<String, IOException> predicate = Spied.checkedPredicate(String::isBlank);

            CheckedPredicate<String, IOException> negated = predicate.negate();

            assertFalse(negated.test(" "));
            assertTrue(negated.test("foo"));

            verify(predicate).test(" ");
            verify(predicate).test("foo");
            verify(predicate).negate();
            verifyNoMoreInteractions(predicate);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedPredicate<String, IOException> predicate = Spied.checkedPredicate(s -> {
                throw new IOException(s);
            });

            CheckedPredicate<String, IOException> negated = predicate.negate();

            IOException thrown = assertThrows(IOException.class, () -> negated.test("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(predicate).test("foo");
            verify(predicate).negate();
            verifyNoMoreInteractions(predicate);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedPredicate<String, IOException> predicate = Spied.checkedPredicate(s -> {
                throw new IllegalStateException(s);
            });

            CheckedPredicate<String, IOException> negated = predicate.negate();

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> negated.test("foo"));
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
            CheckedPredicate<String, IOException> predicate = Spied.checkedPredicate(String::isEmpty);

            assertThrows(NullPointerException.class, () -> predicate.or(null));
        }

        @Nested
        class NeitherThrows {

            @Test
            void testThisMatches() throws IOException {
                CheckedPredicate<String, IOException> predicate = Spied.checkedPredicate(String::isBlank);
                CheckedPredicate<String, IOException> other = Spied.checkedPredicate(String::isEmpty);

                CheckedPredicate<String, IOException> composed = predicate.or(other);

                assertTrue(composed.test(" "));

                verify(predicate).test(" ");
                verify(predicate).or(other);
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testOtherMatches() throws IOException {
                CheckedPredicate<String, IOException> predicate = Spied.checkedPredicate(String::isEmpty);
                CheckedPredicate<String, IOException> other = Spied.checkedPredicate(String::isBlank);

                CheckedPredicate<String, IOException> composed = predicate.or(other);

                assertTrue(composed.test(" "));

                verify(predicate).test(" ");
                verify(predicate).or(other);
                verify(other).test(" ");
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testBothMatch() throws IOException {
                CheckedPredicate<String, IOException> predicate = Spied.checkedPredicate(String::isEmpty);
                CheckedPredicate<String, IOException> other = Spied.checkedPredicate(String::isBlank);

                CheckedPredicate<String, IOException> composed = predicate.or(other);

                assertTrue(composed.test(""));

                verify(predicate).test("");
                verify(predicate).or(other);
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testNeitherMatches() throws IOException {
                CheckedPredicate<String, IOException> predicate = Spied.checkedPredicate(String::isEmpty);
                CheckedPredicate<String, IOException> other = Spied.checkedPredicate(String::isBlank);

                CheckedPredicate<String, IOException> composed = predicate.or(other);

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
                CheckedPredicate<String, IOException> predicate = Spied.checkedPredicate(s -> {
                    throw new IOException(s);
                });
                CheckedPredicate<String, IOException> other = Spied.checkedPredicate(String::isBlank);

                CheckedPredicate<String, IOException> composed = predicate.or(other);

                IOException thrown = assertThrows(IOException.class, () -> composed.test(" "));
                assertEquals(" ", thrown.getMessage());

                verify(predicate).test(" ");
                verify(predicate).or(other);
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testOtherDoesNotMatch() throws IOException {
                CheckedPredicate<String, IOException> predicate = Spied.checkedPredicate(s -> {
                    throw new IOException(s);
                });
                CheckedPredicate<String, IOException> other = Spied.checkedPredicate(String::isBlank);

                CheckedPredicate<String, IOException> composed = predicate.or(other);

                IOException thrown = assertThrows(IOException.class, () -> composed.test("foo"));
                assertEquals("foo", thrown.getMessage());

                verify(predicate).test("foo");
                verify(predicate).or(other);
                verifyNoMoreInteractions(predicate, other);
            }
        }

        @Nested
        class ThisThrowsUnchecked {

            @Test
            void testOtherMatches() throws IOException {
                CheckedPredicate<String, IOException> predicate = Spied.checkedPredicate(s -> {
                    throw new IllegalStateException(s);
                });
                CheckedPredicate<String, IOException> other = Spied.checkedPredicate(String::isBlank);

                CheckedPredicate<String, IOException> composed = predicate.or(other);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> composed.test(" "));
                assertEquals(" ", thrown.getMessage());

                verify(predicate).test(" ");
                verify(predicate).or(other);
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testOtherDoesNotMatch() throws IOException {
                CheckedPredicate<String, IOException> predicate = Spied.checkedPredicate(s -> {
                    throw new IllegalStateException(s);
                });
                CheckedPredicate<String, IOException> other = Spied.checkedPredicate(String::isBlank);

                CheckedPredicate<String, IOException> composed = predicate.or(other);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> composed.test("foo"));
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
                CheckedPredicate<String, IOException> predicate = Spied.checkedPredicate(String::isBlank);
                CheckedPredicate<String, IOException> other = Spied.checkedPredicate(s -> {
                    throw new IOException(s);
                });

                CheckedPredicate<String, IOException> composed = predicate.or(other);

                assertTrue(composed.test(" "));

                verify(predicate).test(" ");
                verify(predicate).or(other);
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testThisDoesNotMatch() throws IOException {
                CheckedPredicate<String, IOException> predicate = Spied.checkedPredicate(String::isBlank);
                CheckedPredicate<String, IOException> other = Spied.checkedPredicate(s -> {
                    throw new IOException(s);
                });

                CheckedPredicate<String, IOException> composed = predicate.or(other);

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

            @Test
            void testThisMatches() throws IOException {
                CheckedPredicate<String, IOException> predicate = Spied.checkedPredicate(String::isBlank);
                CheckedPredicate<String, IOException> other = Spied.checkedPredicate(s -> {
                    throw new IllegalStateException(s);
                });

                CheckedPredicate<String, IOException> composed = predicate.or(other);

                assertTrue(composed.test(" "));

                verify(predicate).test(" ");
                verify(predicate).or(other);
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testThisDoesNotMatch() throws IOException {
                CheckedPredicate<String, IOException> predicate = Spied.checkedPredicate(String::isBlank);
                CheckedPredicate<String, IOException> other = Spied.checkedPredicate(s -> {
                    throw new IllegalStateException(s);
                });

                CheckedPredicate<String, IOException> composed = predicate.or(other);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> composed.test("foo"));
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
            CheckedPredicate<String, IOException> predicate = Spied.checkedPredicate(String::isBlank);

            assertThrows(NullPointerException.class, () -> predicate.onErrorThrowAsChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedPredicate<String, IOException> predicate = Spied.checkedPredicate(String::isBlank);

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedPredicate<String, ExecutionException> throwing = predicate.onErrorThrowAsChecked(errorMapper);

            assertTrue(throwing.test(" "));

            verify(predicate).test(" ");
            verify(predicate).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(predicate, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedPredicate<String, IOException> predicate = Spied.checkedPredicate(s -> {
                throw new IOException(s);
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedPredicate<String, ExecutionException> throwing = predicate.onErrorThrowAsChecked(errorMapper);

            ExecutionException thrown = assertThrows(ExecutionException.class, () -> throwing.test("foo"));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo", cause.getMessage());

            verify(predicate).test("foo");
            verify(predicate).onErrorThrowAsChecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(predicate, errorMapper);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedPredicate<String, IOException> predicate = Spied.checkedPredicate(s -> {
                throw new IllegalStateException(s);
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedPredicate<String, ExecutionException> throwing = predicate.onErrorThrowAsChecked(errorMapper);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> throwing.test("foo"));
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
            CheckedPredicate<String, IOException> predicate = Spied.checkedPredicate(String::isBlank);

            assertThrows(NullPointerException.class, () -> predicate.onErrorThrowAsUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedPredicate<String, IOException> predicate = Spied.checkedPredicate(String::isBlank);

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            Predicate<String> throwing = predicate.onErrorThrowAsUnchecked(errorMapper);

            assertTrue(throwing.test(" "));

            verify(predicate).test(" ");
            verify(predicate).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(predicate, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedPredicate<String, IOException> predicate = Spied.checkedPredicate(s -> {
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

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedPredicate<String, IOException> predicate = Spied.checkedPredicate(s -> {
                throw new IllegalArgumentException(s);
            });

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            Predicate<String> throwing = predicate.onErrorThrowAsUnchecked(errorMapper);

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> throwing.test("foo"));
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
            CheckedPredicate<String, IOException> predicate = Spied.checkedPredicate(String::isBlank);

            assertThrows(NullPointerException.class, () -> predicate.onErrorHandleChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedPredicate<String, IOException> predicate = Spied.checkedPredicate(String::isBlank);

            CheckedPredicate<IOException, ExecutionException> errorHandler = Spied.checkedPredicate(e -> e.getMessage() == null);

            CheckedPredicate<String, ExecutionException> handling = predicate.onErrorHandleChecked(errorHandler);

            assertTrue(handling.test(" "));

            verify(predicate).test(" ");
            verify(predicate).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(predicate, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException, ExecutionException {
                CheckedPredicate<String, IOException> predicate = Spied.checkedPredicate(s -> {
                    throw new IOException(s);
                });

                CheckedPredicate<IOException, ExecutionException> errorHandler = Spied.checkedPredicate(e -> e.getMessage() == null);

                CheckedPredicate<String, ExecutionException> handling = predicate.onErrorHandleChecked(errorHandler);

                assertFalse(handling.test(" "));

                verify(predicate).test(" ");
                verify(predicate).onErrorHandleChecked(errorHandler);
                verify(errorHandler).test(any());
                verifyNoMoreInteractions(predicate, errorHandler);
            }

            @Test
            void testHandlerThrowsChecked() throws IOException, ExecutionException {
                CheckedPredicate<String, IOException> predicate = Spied.checkedPredicate(s -> {
                    throw new IOException(s);
                });

                CheckedPredicate<IOException, ExecutionException> errorHandler = Spied.checkedPredicate(e -> {
                    throw new ExecutionException(e);
                });

                CheckedPredicate<String, ExecutionException> handling = predicate.onErrorHandleChecked(errorHandler);

                ExecutionException thrown = assertThrows(ExecutionException.class, () -> handling.test("foo"));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());

                verify(predicate).test("foo");
                verify(predicate).onErrorHandleChecked(errorHandler);
                verify(errorHandler).test(any());
                verifyNoMoreInteractions(predicate, errorHandler);
            }

            @Test
            void testHandlerThrowsUnchecked() throws IOException, ExecutionException {
                CheckedPredicate<String, IOException> predicate = Spied.checkedPredicate(s -> {
                    throw new IOException(s);
                });

                CheckedPredicate<IOException, ExecutionException> errorHandler = Spied.checkedPredicate(e -> {
                    throw new IllegalStateException(e);
                });

                CheckedPredicate<String, ExecutionException> handling = predicate.onErrorHandleChecked(errorHandler);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.test("foo"));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());

                verify(predicate).test("foo");
                verify(predicate).onErrorHandleChecked(errorHandler);
                verify(errorHandler).test(any());
                verifyNoMoreInteractions(predicate, errorHandler);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedPredicate<String, IOException> predicate = Spied.checkedPredicate(s -> {
                throw new IllegalStateException(s);
            });

            CheckedPredicate<IOException, ExecutionException> errorHandler = Spied.checkedPredicate(e -> e.getMessage() == null);

            CheckedPredicate<String, ExecutionException> handling = predicate.onErrorHandleChecked(errorHandler);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.test("foo"));
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
            CheckedPredicate<String, IOException> predicate = Spied.checkedPredicate(String::isBlank);

            assertThrows(NullPointerException.class, () -> predicate.onErrorHandleUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedPredicate<String, IOException> predicate = Spied.checkedPredicate(String::isBlank);

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
                CheckedPredicate<String, IOException> predicate = Spied.checkedPredicate(s -> {
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

            @Test
            void testHandlerThrowsUnchecked() throws IOException {
                CheckedPredicate<String, IOException> predicate = Spied.checkedPredicate(s -> {
                    throw new IOException(s);
                });

                Predicate<IOException> errorHandler = Spied.predicate(e -> {
                    throw new IllegalStateException(e);
                });

                Predicate<String> handling = predicate.onErrorHandleUnchecked(errorHandler);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.test("foo"));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());

                verify(predicate).test("foo");
                verify(predicate).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).test(any());
                verifyNoMoreInteractions(predicate, errorHandler);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedPredicate<String, IOException> predicate = Spied.checkedPredicate(s -> {
                throw new IllegalStateException(s);
            });

            Predicate<IOException> errorHandler = Spied.predicate(e -> e.getMessage() == null);

            Predicate<String> handling = predicate.onErrorHandleUnchecked(errorHandler);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.test("foo"));
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
            CheckedPredicate<String, IOException> predicate = Spied.checkedPredicate(String::isBlank);

            assertThrows(NullPointerException.class, () -> predicate.onErrorTestChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedPredicate<String, IOException> predicate = Spied.checkedPredicate(String::isBlank);

            CheckedPredicate<String, ExecutionException> fallback = Spied.checkedPredicate(String::isEmpty);

            CheckedPredicate<String, ExecutionException> testing = predicate.onErrorTestChecked(fallback);

            assertTrue(testing.test(" "));

            verify(predicate).test(" ");
            verify(predicate).onErrorTestChecked(fallback);
            verifyNoMoreInteractions(predicate, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                CheckedPredicate<String, IOException> predicate = Spied.checkedPredicate(s -> {
                    throw new IOException(s);
                });

                CheckedPredicate<String, ParseException> fallback = Spied.checkedPredicate(String::isEmpty);

                CheckedPredicate<String, ParseException> testing = predicate.onErrorTestChecked(fallback);

                assertFalse(testing.test(" "));

                verify(predicate).test(" ");
                verify(predicate).onErrorTestChecked(fallback);
                verify(fallback).test(" ");
                verifyNoMoreInteractions(predicate, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                CheckedPredicate<String, IOException> predicate = Spied.checkedPredicate(s -> {
                    throw new IOException(s);
                });

                CheckedPredicate<String, ParseException> fallback = Spied.checkedPredicate(s -> {
                    throw new ParseException(s, 0);
                });

                CheckedPredicate<String, ParseException> testing = predicate.onErrorTestChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> testing.test("foo"));
                assertEquals("foo", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(predicate).test("foo");
                verify(predicate).onErrorTestChecked(fallback);
                verify(fallback).test("foo");
                verifyNoMoreInteractions(predicate, fallback);
            }

            @Test
            void testFallbackThrowsUnchecked() throws IOException, ParseException {
                CheckedPredicate<String, IOException> predicate = Spied.checkedPredicate(s -> {
                    throw new IOException(s);
                });

                CheckedPredicate<String, ParseException> fallback = Spied.checkedPredicate(s -> {
                    throw new IllegalStateException(s);
                });

                CheckedPredicate<String, ParseException> testing = predicate.onErrorTestChecked(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> testing.test("foo"));
                assertEquals("foo", thrown.getMessage());

                verify(predicate).test("foo");
                verify(predicate).onErrorTestChecked(fallback);
                verify(fallback).test("foo");
                verifyNoMoreInteractions(predicate, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedPredicate<String, IOException> predicate = Spied.checkedPredicate(s -> {
                throw new IllegalStateException(s);
            });

            CheckedPredicate<String, ParseException> fallback = Spied.checkedPredicate(String::isEmpty);

            CheckedPredicate<String, ParseException> testing = predicate.onErrorTestChecked(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> testing.test("foo"));
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
            CheckedPredicate<String, IOException> predicate = Spied.checkedPredicate(String::isBlank);

            assertThrows(NullPointerException.class, () -> predicate.onErrorTestUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedPredicate<String, IOException> predicate = Spied.checkedPredicate(String::isBlank);

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
                CheckedPredicate<String, IOException> predicate = Spied.checkedPredicate(s -> {
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

            @Test
            void testFallbackThrowsUnchecked() throws IOException {
                CheckedPredicate<String, IOException> predicate = Spied.checkedPredicate(s -> {
                    throw new IOException(s);
                });

                Predicate<String> fallback = Spied.predicate(s -> {
                    throw new IllegalStateException(s);
                });

                Predicate<String> testing = predicate.onErrorTestUnchecked(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> testing.test("foo"));
                assertEquals("foo", thrown.getMessage());

                verify(predicate).test("foo");
                verify(predicate).onErrorTestUnchecked(fallback);
                verify(fallback).test("foo");
                verifyNoMoreInteractions(predicate, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedPredicate<String, IOException> predicate = Spied.checkedPredicate(s -> {
                throw new IllegalStateException(s);
            });

            Predicate<String> fallback = Spied.predicate(String::isEmpty);

            Predicate<String> testing = predicate.onErrorTestUnchecked(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> testing.test("foo"));
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
            CheckedPredicate<String, IOException> predicate = Spied.checkedPredicate(String::isBlank);

            assertThrows(NullPointerException.class, () -> predicate.onErrorGetCheckedAsBoolean(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedPredicate<String, IOException> predicate = Spied.checkedPredicate(String::isBlank);

            CheckedBooleanSupplier<ExecutionException> fallback = Spied.checkedBooleanSupplier(() -> false);

            CheckedPredicate<String, ExecutionException> getting = predicate.onErrorGetCheckedAsBoolean(fallback);

            assertTrue(getting.test(" "));

            verify(predicate).test(" ");
            verify(predicate).onErrorGetCheckedAsBoolean(fallback);
            verifyNoMoreInteractions(predicate, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                CheckedPredicate<String, IOException> predicate = Spied.checkedPredicate(s -> {
                    throw new IOException(s);
                });

                CheckedBooleanSupplier<ParseException> fallback = Spied.checkedBooleanSupplier(() -> false);

                CheckedPredicate<String, ParseException> getting = predicate.onErrorGetCheckedAsBoolean(fallback);

                assertFalse(getting.test("foo"));

                verify(predicate).test("foo");
                verify(predicate).onErrorGetCheckedAsBoolean(fallback);
                verify(fallback).getAsBoolean();
                verifyNoMoreInteractions(predicate, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                CheckedPredicate<String, IOException> predicate = Spied.checkedPredicate(s -> {
                    throw new IOException(s);
                });

                CheckedBooleanSupplier<ParseException> fallback = Spied.checkedBooleanSupplier(() -> {
                    throw new ParseException("bar", 0);
                });

                CheckedPredicate<String, ParseException> getting = predicate.onErrorGetCheckedAsBoolean(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> getting.test("foo"));
                assertEquals("bar", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(predicate).test("foo");
                verify(predicate).onErrorGetCheckedAsBoolean(fallback);
                verify(fallback).getAsBoolean();
                verifyNoMoreInteractions(predicate, fallback);
            }

            @Test
            void testFallbackThrowsUnchecked() throws IOException, ParseException {
                CheckedPredicate<String, IOException> predicate = Spied.checkedPredicate(s -> {
                    throw new IOException(s);
                });

                CheckedBooleanSupplier<ParseException> fallback = Spied.checkedBooleanSupplier(() -> {
                    throw new IllegalStateException("bar");
                });

                CheckedPredicate<String, ParseException> getting = predicate.onErrorGetCheckedAsBoolean(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> getting.test("foo"));
                assertEquals("bar", thrown.getMessage());

                verify(predicate).test("foo");
                verify(predicate).onErrorGetCheckedAsBoolean(fallback);
                verify(fallback).getAsBoolean();
                verifyNoMoreInteractions(predicate, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedPredicate<String, IOException> predicate = Spied.checkedPredicate(s -> {
                throw new IllegalStateException(s);
            });

            CheckedBooleanSupplier<ParseException> fallback = Spied.checkedBooleanSupplier(() -> false);

            CheckedPredicate<String, ParseException> getting = predicate.onErrorGetCheckedAsBoolean(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> getting.test("foo"));
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
            CheckedPredicate<String, IOException> predicate = Spied.checkedPredicate(String::isBlank);

            assertThrows(NullPointerException.class, () -> predicate.onErrorGetUncheckedAsBoolean(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedPredicate<String, IOException> predicate = Spied.checkedPredicate(String::isBlank);

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
                CheckedPredicate<String, IOException> predicate = Spied.checkedPredicate(s -> {
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

            @Test
            void testFallbackThrowsUnchecked() throws IOException {
                CheckedPredicate<String, IOException> predicate = Spied.checkedPredicate(s -> {
                    throw new IOException(s);
                });

                BooleanSupplier fallback = Spied.booleanSupplier(() -> {
                    throw new IllegalStateException("bar");
                });

                Predicate<String> getting = predicate.onErrorGetUncheckedAsBoolean(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> getting.test("foo"));
                assertEquals("bar", thrown.getMessage());

                verify(predicate).test("foo");
                verify(predicate).onErrorGetUncheckedAsBoolean(fallback);
                verify(fallback).getAsBoolean();
                verifyNoMoreInteractions(predicate, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedPredicate<String, IOException> predicate = Spied.checkedPredicate(s -> {
                throw new IllegalStateException(s);
            });

            BooleanSupplier fallback = Spied.booleanSupplier(() -> false);

            Predicate<String> getting = predicate.onErrorGetUncheckedAsBoolean(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> getting.test("foo"));
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
            CheckedPredicate<String, IOException> predicate = Spied.checkedPredicate(String::isBlank);

            Predicate<String> returning = predicate.onErrorReturn(false);

            assertTrue(returning.test(" "));

            verify(predicate).test(" ");
            verify(predicate).onErrorReturn(false);
            verifyNoMoreInteractions(predicate);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedPredicate<String, IOException> predicate = Spied.checkedPredicate(s -> {
                throw new IOException(s);
            });

            Predicate<String> returning = predicate.onErrorReturn(false);

            assertFalse(returning.test(" "));

            verify(predicate).test(" ");
            verify(predicate).onErrorReturn(false);
            verifyNoMoreInteractions(predicate);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedPredicate<String, IOException> predicate = Spied.checkedPredicate(s -> {
                throw new IllegalStateException(s);
            });

            Predicate<String> returning = predicate.onErrorReturn(false);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> returning.test("foo"));
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
            CheckedPredicate<String, IOException> predicate = Spied.checkedPredicate(String::isBlank);

            Predicate<String> unchecked = predicate.unchecked();

            assertTrue(unchecked.test(" "));

            verify(predicate).test(" ");
            verify(predicate).unchecked();
            verify(predicate).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(predicate);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedPredicate<String, IOException> predicate = Spied.checkedPredicate(s -> {
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

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedPredicate<String, IOException> predicate = Spied.checkedPredicate(s -> {
                throw new IllegalArgumentException(s);
            });

            Predicate<String> unchecked = predicate.unchecked();

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> unchecked.test("foo"));
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
            assertThrows(NullPointerException.class, () -> CheckedPredicate.of(null));
        }

        @Test
        void testNonNullArgument() throws IOException {
            CheckedPredicate<String, IOException> predicate = CheckedPredicate.of(String::isBlank);

            assertTrue(predicate.test(" "));
        }
    }

    @Nested
    class Not {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> CheckedPredicate.not(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedPredicate<String, IOException> predicate = Spied.checkedPredicate(String::isBlank);

            CheckedPredicate<String, IOException> negated = CheckedPredicate.not(predicate);

            assertFalse(negated.test(" "));
            assertTrue(negated.test("foo"));

            verify(predicate).negate();
            verify(predicate).test(" ");
            verify(predicate).test("foo");
            verifyNoMoreInteractions(predicate);
        }

        @Test
        void testArgumentThrowsChecked() throws IOException {
            CheckedPredicate<String, IOException> predicate = Spied.checkedPredicate(s -> {
                throw new IOException(s);
            });

            CheckedPredicate<String, IOException> negated = CheckedPredicate.not(predicate);

            IOException thrown = assertThrows(IOException.class, () -> negated.test("foo"));
            assertEquals("foo", thrown.getMessage());

            verify(predicate).negate();
            verify(predicate).test("foo");
            verifyNoMoreInteractions(predicate);
        }

        @Test
        void testArgumentThrowsUnchecked() throws IOException {
            CheckedPredicate<String, IOException> predicate = Spied.checkedPredicate(s -> {
                throw new IllegalStateException(s);
            });

            CheckedPredicate<String, IOException> negated = CheckedPredicate.not(predicate);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> negated.test("foo"));
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
            assertThrows(NullPointerException.class, () -> CheckedPredicate.unchecked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedPredicate<String, IOException> predicate = Spied.checkedPredicate(String::isBlank);

            Predicate<String> unchecked = CheckedPredicate.unchecked(predicate);

            assertTrue(unchecked.test(" "));

            verify(predicate).unchecked();
            verify(predicate).onErrorThrowAsUnchecked(any());
            verify(predicate).test(" ");
            verifyNoMoreInteractions(predicate);
        }

        @Test
        void testArgumentThrowsChecked() throws IOException {
            CheckedPredicate<String, IOException> predicate = Spied.checkedPredicate(s -> {
                throw new IOException(s);
            });

            Predicate<String> unchecked = CheckedPredicate.unchecked(predicate);

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.test("foo"));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("foo", cause.getMessage());

            verify(predicate).unchecked();
            verify(predicate).onErrorThrowAsUnchecked(any());
            verify(predicate).test("foo");
            verifyNoMoreInteractions(predicate);
        }

        @Test
        void testArgumentThrowsUnchecked() throws IOException {
            CheckedPredicate<String, IOException> predicate = Spied.checkedPredicate(s -> {
                throw new IllegalArgumentException(s);
            });

            Predicate<String> unchecked = CheckedPredicate.unchecked(predicate);

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> unchecked.test("foo"));
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
            assertThrows(NullPointerException.class, () -> CheckedPredicate.checked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedPredicate<String, IOException> predicate = CheckedPredicate.checked(String::isBlank);

            assertTrue(predicate.test(" "));
        }

        @Test
        void testargumentThrowsUnchecked() {
            CheckedPredicate<String, IOException> predicate = CheckedPredicate.checked(s -> {
                throw new UncheckedException(s, new IOException());
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

            assertThrows(NullPointerException.class, () -> CheckedPredicate.checked(null, IOException.class));
            assertThrows(NullPointerException.class, () -> CheckedPredicate.checked(predicate, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedPredicate<String, IOException> predicate = CheckedPredicate.checked(String::isBlank, IOException.class);

            assertTrue(predicate.test(" "));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                CheckedPredicate<String, IOException> predicate = CheckedPredicate.checked(s -> {
                    throw new UncheckedException(new IOException(s));
                }, IOException.class);

                IOException thrown = assertThrows(IOException.class, () -> predicate.test("foo"));
                assertEquals("foo", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                CheckedPredicate<String, IOException> predicate = CheckedPredicate.checked(s -> {
                    throw new UncheckedException(new FileNotFoundException(s));
                }, IOException.class);

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class, () -> predicate.test("foo"));
                assertEquals("foo", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                CheckedPredicate<String, IOException> predicate = CheckedPredicate.checked(s -> {
                    throw new UncheckedException(new ParseException(s, 0));
                }, IOException.class);

                UncheckedException thrown = assertThrows(UncheckedException.class, () -> predicate.test("foo"));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("foo", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            CheckedPredicate<String, IOException> predicate = CheckedPredicate.checked(s -> {
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

            assertThrows(NullPointerException.class, () -> CheckedPredicate.invokeAndUnwrap(null, "foo", IOException.class));
            assertThrows(NullPointerException.class, () -> CheckedPredicate.invokeAndUnwrap(predicate, "foo", null));

            assertFalse(CheckedPredicate.invokeAndUnwrap(predicate, null, IOException.class));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            Predicate<String> predicate = String::isBlank;

            assertTrue(CheckedPredicate.invokeAndUnwrap(predicate, " ", IOException.class));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                Predicate<String> predicate = s -> {
                    throw new UncheckedException(new IOException(s));
                };

                IOException thrown = assertThrows(IOException.class, () -> CheckedPredicate.invokeAndUnwrap(predicate, "foo", IOException.class));
                assertEquals("foo", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                Predicate<String> predicate = s -> {
                    throw new UncheckedException(new FileNotFoundException(s));
                };

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class,
                        () -> CheckedPredicate.invokeAndUnwrap(predicate, "foo", IOException.class));
                assertEquals("foo", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                Predicate<String> predicate = s -> {
                    throw new UncheckedException(new ParseException(s, 0));
                };

                UncheckedException thrown = assertThrows(UncheckedException.class,
                        () -> CheckedPredicate.invokeAndUnwrap(predicate, "foo", IOException.class));
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
                    () -> CheckedPredicate.invokeAndUnwrap(predicate, "foo", IOException.class));
            assertEquals("foo", thrown.getMessage());
        }
    }
}
