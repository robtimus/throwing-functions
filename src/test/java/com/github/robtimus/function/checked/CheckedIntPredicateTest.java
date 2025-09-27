/*
 * CheckedIntPredicateTest.java
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

@SuppressWarnings("nls")
class CheckedIntPredicateTest {

    @Nested
    class And {

        @Test
        void testNullArgument() {
            CheckedIntPredicate<IOException> predicate = Spied.checkedIntPredicate(d -> d % 2 != 0);

            assertThrows(NullPointerException.class, () -> predicate.and(null));
        }

        @Nested
        class NeitherThrows {

            @Test
            void testThisMatches() throws IOException {
                CheckedIntPredicate<IOException> predicate = Spied.checkedIntPredicate(d -> d % 2 != 0);
                CheckedIntPredicate<IOException> other = Spied.checkedIntPredicate(d -> d % 2 == 0);

                CheckedIntPredicate<IOException> composed = predicate.and(other);

                assertFalse(composed.test(1));

                verify(predicate).test(1);
                verify(predicate).and(other);
                verify(other).test(1);
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testOtherMatches() throws IOException {
                CheckedIntPredicate<IOException> predicate = Spied.checkedIntPredicate(d -> d % 2 == 0);
                CheckedIntPredicate<IOException> other = Spied.checkedIntPredicate(d -> d % 2 != 0);

                CheckedIntPredicate<IOException> composed = predicate.and(other);

                assertFalse(composed.test(1));

                verify(predicate).test(1);
                verify(predicate).and(other);
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testBothMatch() throws IOException {
                CheckedIntPredicate<IOException> predicate = Spied.checkedIntPredicate(d -> d % 2 != 0);
                CheckedIntPredicate<IOException> other = Spied.checkedIntPredicate(d -> d > 0);

                CheckedIntPredicate<IOException> composed = predicate.and(other);

                assertTrue(composed.test(1));

                verify(predicate).test(1);
                verify(predicate).and(other);
                verify(other).test(1);
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testNeitherMatches() throws IOException {
                CheckedIntPredicate<IOException> predicate = Spied.checkedIntPredicate(d -> d % 2 != 0);
                CheckedIntPredicate<IOException> other = Spied.checkedIntPredicate(d -> d > 0);

                CheckedIntPredicate<IOException> composed = predicate.and(other);

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
                CheckedIntPredicate<IOException> predicate = Spied.checkedIntPredicate(d -> {
                    throw new IOException(Integer.toString(d));
                });
                CheckedIntPredicate<IOException> other = Spied.checkedIntPredicate(d -> d % 2 != 0);

                CheckedIntPredicate<IOException> composed = predicate.and(other);

                IOException thrown = assertThrows(IOException.class, () -> composed.test(1));
                assertEquals("1", thrown.getMessage());

                verify(predicate).test(1);
                verify(predicate).and(other);
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testOtherDoesNotMatch() throws IOException {
                CheckedIntPredicate<IOException> predicate = Spied.checkedIntPredicate(d -> {
                    throw new IOException(Integer.toString(d));
                });
                CheckedIntPredicate<IOException> other = Spied.checkedIntPredicate(d -> d % 2 == 0);

                CheckedIntPredicate<IOException> composed = predicate.and(other);

                IOException thrown = assertThrows(IOException.class, () -> composed.test(1));
                assertEquals("1", thrown.getMessage());

                verify(predicate).test(1);
                verify(predicate).and(other);
                verifyNoMoreInteractions(predicate, other);
            }
        }

        @Nested
        class ThisThrowsUnchecked {

            @Test
            void testOtherMatches() throws IOException {
                CheckedIntPredicate<IOException> predicate = Spied.checkedIntPredicate(d -> {
                    throw new IllegalStateException(Integer.toString(d));
                });
                CheckedIntPredicate<IOException> other = Spied.checkedIntPredicate(d -> d % 2 != 0);

                CheckedIntPredicate<IOException> composed = predicate.and(other);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> composed.test(1));
                assertEquals("1", thrown.getMessage());

                verify(predicate).test(1);
                verify(predicate).and(other);
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testOtherDoesNotMatch() throws IOException {
                CheckedIntPredicate<IOException> predicate = Spied.checkedIntPredicate(d -> {
                    throw new IllegalStateException(Integer.toString(d));
                });
                CheckedIntPredicate<IOException> other = Spied.checkedIntPredicate(d -> d % 2 == 0);

                CheckedIntPredicate<IOException> composed = predicate.and(other);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> composed.test(1));
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
                CheckedIntPredicate<IOException> predicate = Spied.checkedIntPredicate(d -> d % 2 != 0);
                CheckedIntPredicate<IOException> other = Spied.checkedIntPredicate(d -> {
                    throw new IOException(Integer.toString(d));
                });

                CheckedIntPredicate<IOException> composed = predicate.and(other);

                IOException thrown = assertThrows(IOException.class, () -> composed.test(1));
                assertEquals("1", thrown.getMessage());

                verify(predicate).test(1);
                verify(predicate).and(other);
                verify(other).test(1);
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testThisDoesNotMatch() throws IOException {
                CheckedIntPredicate<IOException> predicate = Spied.checkedIntPredicate(d -> d % 2 == 0);
                CheckedIntPredicate<IOException> other = Spied.checkedIntPredicate(d -> {
                    throw new IOException(Integer.toString(d));
                });

                CheckedIntPredicate<IOException> composed = predicate.and(other);

                assertFalse(composed.test(1));

                verify(predicate).test(1);
                verify(predicate).and(other);
                verifyNoMoreInteractions(predicate, other);
            }
        }

        @Nested
        class OtherThrowsUnchecked {

            @Test
            void testThisMatches() throws IOException {
                CheckedIntPredicate<IOException> predicate = Spied.checkedIntPredicate(d -> d % 2 != 0);
                CheckedIntPredicate<IOException> other = Spied.checkedIntPredicate(d -> {
                    throw new IllegalStateException(Integer.toString(d));
                });

                CheckedIntPredicate<IOException> composed = predicate.and(other);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> composed.test(1));
                assertEquals("1", thrown.getMessage());

                verify(predicate).test(1);
                verify(predicate).and(other);
                verify(other).test(1);
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testThisDoesNotMatch() throws IOException {
                CheckedIntPredicate<IOException> predicate = Spied.checkedIntPredicate(d -> d % 2 == 0);
                CheckedIntPredicate<IOException> other = Spied.checkedIntPredicate(d -> {
                    throw new IOException(Integer.toString(d));
                });

                CheckedIntPredicate<IOException> composed = predicate.and(other);

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
            CheckedIntPredicate<IOException> predicate = Spied.checkedIntPredicate(d -> d % 2 != 0);

            CheckedIntPredicate<IOException> negated = predicate.negate();

            assertFalse(negated.test(1));
            assertTrue(negated.test(0));

            verify(predicate).test(1);
            verify(predicate).test(0);
            verify(predicate).negate();
            verifyNoMoreInteractions(predicate);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedIntPredicate<IOException> predicate = Spied.checkedIntPredicate(d -> {
                throw new IOException(Integer.toString(d));
            });

            CheckedIntPredicate<IOException> negated = predicate.negate();

            IOException thrown = assertThrows(IOException.class, () -> negated.test(1));
            assertEquals("1", thrown.getMessage());

            verify(predicate).test(1);
            verify(predicate).negate();
            verifyNoMoreInteractions(predicate);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedIntPredicate<IOException> predicate = Spied.checkedIntPredicate(d -> {
                throw new IllegalStateException(Integer.toString(d));
            });

            CheckedIntPredicate<IOException> negated = predicate.negate();

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> negated.test(1));
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
            CheckedIntPredicate<IOException> predicate = Spied.checkedIntPredicate(d -> d % 2 != 0);

            assertThrows(NullPointerException.class, () -> predicate.or(null));
        }

        @Nested
        class NeitherThrows {

            @Test
            void testThisMatches() throws IOException {
                CheckedIntPredicate<IOException> predicate = Spied.checkedIntPredicate(d -> d % 2 != 0);
                CheckedIntPredicate<IOException> other = Spied.checkedIntPredicate(d -> d % 2 == 0);

                CheckedIntPredicate<IOException> composed = predicate.or(other);

                assertTrue(composed.test(1));

                verify(predicate).test(1);
                verify(predicate).or(other);
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testOtherMatches() throws IOException {
                CheckedIntPredicate<IOException> predicate = Spied.checkedIntPredicate(d -> d % 2 == 0);
                CheckedIntPredicate<IOException> other = Spied.checkedIntPredicate(d -> d % 2 != 0);

                CheckedIntPredicate<IOException> composed = predicate.or(other);

                assertTrue(composed.test(1));

                verify(predicate).test(1);
                verify(predicate).or(other);
                verify(other).test(1);
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testBothMatch() throws IOException {
                CheckedIntPredicate<IOException> predicate = Spied.checkedIntPredicate(d -> d % 2 != 0);
                CheckedIntPredicate<IOException> other = Spied.checkedIntPredicate(d -> d > 0);

                CheckedIntPredicate<IOException> composed = predicate.or(other);

                assertTrue(composed.test(1));

                verify(predicate).test(1);
                verify(predicate).or(other);
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testNeitherMatches() throws IOException {
                CheckedIntPredicate<IOException> predicate = Spied.checkedIntPredicate(d -> d % 2 != 0);
                CheckedIntPredicate<IOException> other = Spied.checkedIntPredicate(d -> d > 0);

                CheckedIntPredicate<IOException> composed = predicate.or(other);

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
                CheckedIntPredicate<IOException> predicate = Spied.checkedIntPredicate(d -> {
                    throw new IOException(Integer.toString(d));
                });
                CheckedIntPredicate<IOException> other = Spied.checkedIntPredicate(d -> d % 2 != 0);

                CheckedIntPredicate<IOException> composed = predicate.or(other);

                IOException thrown = assertThrows(IOException.class, () -> composed.test(1));
                assertEquals("1", thrown.getMessage());

                verify(predicate).test(1);
                verify(predicate).or(other);
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testOtherDoesNotMatch() throws IOException {
                CheckedIntPredicate<IOException> predicate = Spied.checkedIntPredicate(d -> {
                    throw new IOException(Integer.toString(d));
                });
                CheckedIntPredicate<IOException> other = Spied.checkedIntPredicate(d -> d % 2 == 0);

                CheckedIntPredicate<IOException> composed = predicate.or(other);

                IOException thrown = assertThrows(IOException.class, () -> composed.test(1));
                assertEquals("1", thrown.getMessage());

                verify(predicate).test(1);
                verify(predicate).or(other);
                verifyNoMoreInteractions(predicate, other);
            }
        }

        @Nested
        class ThisThrowsUnchecked {

            @Test
            void testOtherMatches() throws IOException {
                CheckedIntPredicate<IOException> predicate = Spied.checkedIntPredicate(d -> {
                    throw new IllegalStateException(Integer.toString(d));
                });
                CheckedIntPredicate<IOException> other = Spied.checkedIntPredicate(d -> d % 2 != 0);

                CheckedIntPredicate<IOException> composed = predicate.or(other);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> composed.test(1));
                assertEquals("1", thrown.getMessage());

                verify(predicate).test(1);
                verify(predicate).or(other);
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testOtherDoesNotMatch() throws IOException {
                CheckedIntPredicate<IOException> predicate = Spied.checkedIntPredicate(d -> {
                    throw new IllegalStateException(Integer.toString(d));
                });
                CheckedIntPredicate<IOException> other = Spied.checkedIntPredicate(d -> d % 2 == 0);

                CheckedIntPredicate<IOException> composed = predicate.or(other);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> composed.test(1));
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
                CheckedIntPredicate<IOException> predicate = Spied.checkedIntPredicate(d -> d % 2 != 0);
                CheckedIntPredicate<IOException> other = Spied.checkedIntPredicate(d -> {
                    throw new IOException(Integer.toString(d));
                });

                CheckedIntPredicate<IOException> composed = predicate.or(other);

                assertTrue(composed.test(1));

                verify(predicate).test(1);
                verify(predicate).or(other);
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testThisDoesNotMatch() throws IOException {
                CheckedIntPredicate<IOException> predicate = Spied.checkedIntPredicate(d -> d % 2 == 0);
                CheckedIntPredicate<IOException> other = Spied.checkedIntPredicate(d -> {
                    throw new IOException(Integer.toString(d));
                });

                CheckedIntPredicate<IOException> composed = predicate.or(other);

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

            @Test
            void testThisMatches() throws IOException {
                CheckedIntPredicate<IOException> predicate = Spied.checkedIntPredicate(d -> d % 2 != 0);
                CheckedIntPredicate<IOException> other = Spied.checkedIntPredicate(d -> {
                    throw new IllegalStateException(Integer.toString(d));
                });

                CheckedIntPredicate<IOException> composed = predicate.or(other);

                assertTrue(composed.test(1));

                verify(predicate).test(1);
                verify(predicate).or(other);
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testThisDoesNotMatch() throws IOException {
                CheckedIntPredicate<IOException> predicate = Spied.checkedIntPredicate(d -> d % 2 == 0);
                CheckedIntPredicate<IOException> other = Spied.checkedIntPredicate(d -> {
                    throw new IllegalStateException(Integer.toString(d));
                });

                CheckedIntPredicate<IOException> composed = predicate.or(other);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> composed.test(1));
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
            CheckedIntPredicate<IOException> predicate = Spied.checkedIntPredicate(d -> d % 2 != 0);

            assertThrows(NullPointerException.class, () -> predicate.onErrorThrowAsChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedIntPredicate<IOException> predicate = Spied.checkedIntPredicate(d -> d % 2 != 0);

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedIntPredicate<ExecutionException> throwing = predicate.onErrorThrowAsChecked(errorMapper);

            assertTrue(throwing.test(1));

            verify(predicate).test(1);
            verify(predicate).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(predicate, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedIntPredicate<IOException> predicate = Spied.checkedIntPredicate(d -> {
                throw new IOException(Integer.toString(d));
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedIntPredicate<ExecutionException> throwing = predicate.onErrorThrowAsChecked(errorMapper);

            ExecutionException thrown = assertThrows(ExecutionException.class, () -> throwing.test(1));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1", cause.getMessage());

            verify(predicate).test(1);
            verify(predicate).onErrorThrowAsChecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(predicate, errorMapper);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedIntPredicate<IOException> predicate = Spied.checkedIntPredicate(d -> {
                throw new IllegalStateException(Integer.toString(d));
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedIntPredicate<ExecutionException> throwing = predicate.onErrorThrowAsChecked(errorMapper);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> throwing.test(1));
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
            CheckedIntPredicate<IOException> predicate = Spied.checkedIntPredicate(d -> d % 2 != 0);

            assertThrows(NullPointerException.class, () -> predicate.onErrorThrowAsUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedIntPredicate<IOException> predicate = Spied.checkedIntPredicate(d -> d % 2 != 0);

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            IntPredicate throwing = predicate.onErrorThrowAsUnchecked(errorMapper);

            assertTrue(throwing.test(1));

            verify(predicate).test(1);
            verify(predicate).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(predicate, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedIntPredicate<IOException> predicate = Spied.checkedIntPredicate(d -> {
                throw new IOException(Integer.toString(d));
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

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedIntPredicate<IOException> predicate = Spied.checkedIntPredicate(d -> {
                throw new IllegalArgumentException(Integer.toString(d));
            });

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            IntPredicate throwing = predicate.onErrorThrowAsUnchecked(errorMapper);

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> throwing.test(1));
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
            CheckedIntPredicate<IOException> predicate = Spied.checkedIntPredicate(d -> d % 2 != 0);

            assertThrows(NullPointerException.class, () -> predicate.onErrorHandleChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedIntPredicate<IOException> predicate = Spied.checkedIntPredicate(d -> d % 2 != 0);

            CheckedPredicate<IOException, ExecutionException> errorHandler = Spied.checkedPredicate(e -> e.getMessage() == null);

            CheckedIntPredicate<ExecutionException> handling = predicate.onErrorHandleChecked(errorHandler);

            assertTrue(handling.test(1));

            verify(predicate).test(1);
            verify(predicate).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(predicate, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException, ExecutionException {
                CheckedIntPredicate<IOException> predicate = Spied.checkedIntPredicate(d -> {
                    throw new IOException(Integer.toString(d));
                });

                CheckedPredicate<IOException, ExecutionException> errorHandler = Spied.checkedPredicate(e -> e.getMessage() == null);

                CheckedIntPredicate<ExecutionException> handling = predicate.onErrorHandleChecked(errorHandler);

                assertFalse(handling.test(1));

                verify(predicate).test(1);
                verify(predicate).onErrorHandleChecked(errorHandler);
                verify(errorHandler).test(any());
                verifyNoMoreInteractions(predicate, errorHandler);
            }

            @Test
            void testHandlerThrowsChecked() throws IOException, ExecutionException {
                CheckedIntPredicate<IOException> predicate = Spied.checkedIntPredicate(d -> {
                    throw new IOException(Integer.toString(d));
                });

                CheckedPredicate<IOException, ExecutionException> errorHandler = Spied.checkedPredicate(e -> {
                    throw new ExecutionException(e);
                });

                CheckedIntPredicate<ExecutionException> handling = predicate.onErrorHandleChecked(errorHandler);

                ExecutionException thrown = assertThrows(ExecutionException.class, () -> handling.test(1));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("1", cause.getMessage());

                verify(predicate).test(1);
                verify(predicate).onErrorHandleChecked(errorHandler);
                verify(errorHandler).test(any());
                verifyNoMoreInteractions(predicate, errorHandler);
            }

            @Test
            void testHandlerThrowsUnchecked() throws IOException, ExecutionException {
                CheckedIntPredicate<IOException> predicate = Spied.checkedIntPredicate(d -> {
                    throw new IOException(Integer.toString(d));
                });

                CheckedPredicate<IOException, ExecutionException> errorHandler = Spied.checkedPredicate(e -> {
                    throw new IllegalStateException(e);
                });

                CheckedIntPredicate<ExecutionException> handling = predicate.onErrorHandleChecked(errorHandler);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.test(1));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("1", cause.getMessage());

                verify(predicate).test(1);
                verify(predicate).onErrorHandleChecked(errorHandler);
                verify(errorHandler).test(any());
                verifyNoMoreInteractions(predicate, errorHandler);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedIntPredicate<IOException> predicate = Spied.checkedIntPredicate(d -> {
                throw new IllegalStateException(Integer.toString(d));
            });

            CheckedPredicate<IOException, ExecutionException> errorHandler = Spied.checkedPredicate(e -> e.getMessage() == null);

            CheckedIntPredicate<ExecutionException> handling = predicate.onErrorHandleChecked(errorHandler);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.test(1));
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
            CheckedIntPredicate<IOException> predicate = Spied.checkedIntPredicate(d -> d % 2 != 0);

            assertThrows(NullPointerException.class, () -> predicate.onErrorHandleUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedIntPredicate<IOException> predicate = Spied.checkedIntPredicate(d -> d % 2 != 0);

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
                CheckedIntPredicate<IOException> predicate = Spied.checkedIntPredicate(d -> {
                    throw new IOException(Integer.toString(d));
                });

                Predicate<IOException> errorHandler = Spied.predicate(e -> e.getMessage() == null);

                IntPredicate handling = predicate.onErrorHandleUnchecked(errorHandler);

                assertFalse(handling.test(1));

                verify(predicate).test(1);
                verify(predicate).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).test(any());
                verifyNoMoreInteractions(predicate, errorHandler);
            }

            @Test
            void testHandlerThrowsUnchecked() throws IOException {
                CheckedIntPredicate<IOException> predicate = Spied.checkedIntPredicate(d -> {
                    throw new IOException(Integer.toString(d));
                });

                Predicate<IOException> errorHandler = Spied.predicate(e -> {
                    throw new IllegalStateException(e);
                });

                IntPredicate handling = predicate.onErrorHandleUnchecked(errorHandler);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.test(1));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("1", cause.getMessage());

                verify(predicate).test(1);
                verify(predicate).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).test(any());
                verifyNoMoreInteractions(predicate, errorHandler);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedIntPredicate<IOException> predicate = Spied.checkedIntPredicate(d -> {
                throw new IllegalStateException(Integer.toString(d));
            });

            Predicate<IOException> errorHandler = Spied.predicate(e -> e.getMessage() == null);

            IntPredicate handling = predicate.onErrorHandleUnchecked(errorHandler);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.test(1));
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
            CheckedIntPredicate<IOException> predicate = Spied.checkedIntPredicate(d -> d % 2 != 0);

            assertThrows(NullPointerException.class, () -> predicate.onErrorTestChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedIntPredicate<IOException> predicate = Spied.checkedIntPredicate(d -> d % 2 != 0);

            CheckedIntPredicate<ExecutionException> fallback = Spied.checkedIntPredicate(d -> d % 2 == 0);

            CheckedIntPredicate<ExecutionException> testing = predicate.onErrorTestChecked(fallback);

            assertTrue(testing.test(1));

            verify(predicate).test(1);
            verify(predicate).onErrorTestChecked(fallback);
            verifyNoMoreInteractions(predicate, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                CheckedIntPredicate<IOException> predicate = Spied.checkedIntPredicate(d -> {
                    throw new IOException(Integer.toString(d));
                });

                CheckedIntPredicate<ParseException> fallback = Spied.checkedIntPredicate(d -> d % 2 == 0);

                CheckedIntPredicate<ParseException> testing = predicate.onErrorTestChecked(fallback);

                assertFalse(testing.test(1));

                verify(predicate).test(1);
                verify(predicate).onErrorTestChecked(fallback);
                verify(fallback).test(1);
                verifyNoMoreInteractions(predicate, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                CheckedIntPredicate<IOException> predicate = Spied.checkedIntPredicate(d -> {
                    throw new IOException(Integer.toString(d));
                });

                CheckedIntPredicate<ParseException> fallback = Spied.checkedIntPredicate(d -> {
                    throw new ParseException(Integer.toString(d), 0);
                });

                CheckedIntPredicate<ParseException> testing = predicate.onErrorTestChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> testing.test(1));
                assertEquals("1", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(predicate).test(1);
                verify(predicate).onErrorTestChecked(fallback);
                verify(fallback).test(1);
                verifyNoMoreInteractions(predicate, fallback);
            }

            @Test
            void testFallbackThrowsUnchecked() throws IOException, ParseException {
                CheckedIntPredicate<IOException> predicate = Spied.checkedIntPredicate(d -> {
                    throw new IOException(Integer.toString(d));
                });

                CheckedIntPredicate<ParseException> fallback = Spied.checkedIntPredicate(d -> {
                    throw new IllegalStateException(Integer.toString(d));
                });

                CheckedIntPredicate<ParseException> testing = predicate.onErrorTestChecked(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> testing.test(1));
                assertEquals("1", thrown.getMessage());

                verify(predicate).test(1);
                verify(predicate).onErrorTestChecked(fallback);
                verify(fallback).test(1);
                verifyNoMoreInteractions(predicate, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedIntPredicate<IOException> predicate = Spied.checkedIntPredicate(d -> {
                throw new IllegalStateException(Integer.toString(d));
            });

            CheckedIntPredicate<ParseException> fallback = Spied.checkedIntPredicate(d -> d % 2 == 0);

            CheckedIntPredicate<ParseException> testing = predicate.onErrorTestChecked(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> testing.test(1));
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
            CheckedIntPredicate<IOException> predicate = Spied.checkedIntPredicate(d -> d % 2 != 0);

            assertThrows(NullPointerException.class, () -> predicate.onErrorTestUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedIntPredicate<IOException> predicate = Spied.checkedIntPredicate(d -> d % 2 != 0);

            IntPredicate fallback = Spied.intPredicate(d -> d % 2 == 0);

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
                CheckedIntPredicate<IOException> predicate = Spied.checkedIntPredicate(d -> {
                    throw new IOException(Integer.toString(d));
                });

                IntPredicate fallback = Spied.intPredicate(d -> d % 2 == 0);

                IntPredicate testing = predicate.onErrorTestUnchecked(fallback);

                assertFalse(testing.test(1));

                verify(predicate).test(1);
                verify(predicate).onErrorTestUnchecked(fallback);
                verify(fallback).test(1);
                verifyNoMoreInteractions(predicate, fallback);
            }

            @Test
            void testFallbackThrowsUnchecked() throws IOException {
                CheckedIntPredicate<IOException> predicate = Spied.checkedIntPredicate(d -> {
                    throw new IOException(Integer.toString(d));
                });

                IntPredicate fallback = Spied.intPredicate(d -> {
                    throw new IllegalStateException(Integer.toString(d));
                });

                IntPredicate testing = predicate.onErrorTestUnchecked(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> testing.test(1));
                assertEquals("1", thrown.getMessage());

                verify(predicate).test(1);
                verify(predicate).onErrorTestUnchecked(fallback);
                verify(fallback).test(1);
                verifyNoMoreInteractions(predicate, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedIntPredicate<IOException> predicate = Spied.checkedIntPredicate(d -> {
                throw new IllegalStateException(Integer.toString(d));
            });

            IntPredicate fallback = Spied.intPredicate(d -> d % 2 == 0);

            IntPredicate testing = predicate.onErrorTestUnchecked(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> testing.test(1));
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
            CheckedIntPredicate<IOException> predicate = Spied.checkedIntPredicate(d -> d % 2 != 0);

            assertThrows(NullPointerException.class, () -> predicate.onErrorGetCheckedAsBoolean(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedIntPredicate<IOException> predicate = Spied.checkedIntPredicate(d -> d % 2 != 0);

            CheckedBooleanSupplier<ExecutionException> fallback = Spied.checkedBooleanSupplier(() -> false);

            CheckedIntPredicate<ExecutionException> getting = predicate.onErrorGetCheckedAsBoolean(fallback);

            assertTrue(getting.test(1));

            verify(predicate).test(1);
            verify(predicate).onErrorGetCheckedAsBoolean(fallback);
            verifyNoMoreInteractions(predicate, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                CheckedIntPredicate<IOException> predicate = Spied.checkedIntPredicate(d -> {
                    throw new IOException(Integer.toString(d));
                });

                CheckedBooleanSupplier<ParseException> fallback = Spied.checkedBooleanSupplier(() -> false);

                CheckedIntPredicate<ParseException> getting = predicate.onErrorGetCheckedAsBoolean(fallback);

                assertFalse(getting.test(1));

                verify(predicate).test(1);
                verify(predicate).onErrorGetCheckedAsBoolean(fallback);
                verify(fallback).getAsBoolean();
                verifyNoMoreInteractions(predicate, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                CheckedIntPredicate<IOException> predicate = Spied.checkedIntPredicate(d -> {
                    throw new IOException(Integer.toString(d));
                });

                CheckedBooleanSupplier<ParseException> fallback = Spied.checkedBooleanSupplier(() -> {
                    throw new ParseException("bar", 0);
                });

                CheckedIntPredicate<ParseException> getting = predicate.onErrorGetCheckedAsBoolean(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> getting.test(1));
                assertEquals("bar", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(predicate).test(1);
                verify(predicate).onErrorGetCheckedAsBoolean(fallback);
                verify(fallback).getAsBoolean();
                verifyNoMoreInteractions(predicate, fallback);
            }

            @Test
            void testFallbackThrowsUnchecked() throws IOException, ParseException {
                CheckedIntPredicate<IOException> predicate = Spied.checkedIntPredicate(d -> {
                    throw new IOException(Integer.toString(d));
                });

                CheckedBooleanSupplier<ParseException> fallback = Spied.checkedBooleanSupplier(() -> {
                    throw new IllegalStateException("bar");
                });

                CheckedIntPredicate<ParseException> getting = predicate.onErrorGetCheckedAsBoolean(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> getting.test(1));
                assertEquals("bar", thrown.getMessage());

                verify(predicate).test(1);
                verify(predicate).onErrorGetCheckedAsBoolean(fallback);
                verify(fallback).getAsBoolean();
                verifyNoMoreInteractions(predicate, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedIntPredicate<IOException> predicate = Spied.checkedIntPredicate(d -> {
                throw new IllegalStateException(Integer.toString(d));
            });

            CheckedBooleanSupplier<ParseException> fallback = Spied.checkedBooleanSupplier(() -> false);

            CheckedIntPredicate<ParseException> getting = predicate.onErrorGetCheckedAsBoolean(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> getting.test(1));
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
            CheckedIntPredicate<IOException> predicate = Spied.checkedIntPredicate(d -> d % 2 != 0);

            assertThrows(NullPointerException.class, () -> predicate.onErrorGetUncheckedAsBoolean(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedIntPredicate<IOException> predicate = Spied.checkedIntPredicate(d -> d % 2 != 0);

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
                CheckedIntPredicate<IOException> predicate = Spied.checkedIntPredicate(d -> {
                    throw new IOException(Integer.toString(d));
                });

                BooleanSupplier fallback = Spied.booleanSupplier(() -> false);

                IntPredicate getting = predicate.onErrorGetUncheckedAsBoolean(fallback);

                assertFalse(getting.test(1));

                verify(predicate).test(1);
                verify(predicate).onErrorGetUncheckedAsBoolean(fallback);
                verify(fallback).getAsBoolean();
                verifyNoMoreInteractions(predicate, fallback);
            }

            @Test
            void testFallbackThrowsUnchecked() throws IOException {
                CheckedIntPredicate<IOException> predicate = Spied.checkedIntPredicate(d -> {
                    throw new IOException(Integer.toString(d));
                });

                BooleanSupplier fallback = Spied.booleanSupplier(() -> {
                    throw new IllegalStateException("bar");
                });

                IntPredicate getting = predicate.onErrorGetUncheckedAsBoolean(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> getting.test(1));
                assertEquals("bar", thrown.getMessage());

                verify(predicate).test(1);
                verify(predicate).onErrorGetUncheckedAsBoolean(fallback);
                verify(fallback).getAsBoolean();
                verifyNoMoreInteractions(predicate, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedIntPredicate<IOException> predicate = Spied.checkedIntPredicate(d -> {
                throw new IllegalStateException(Integer.toString(d));
            });

            BooleanSupplier fallback = Spied.booleanSupplier(() -> false);

            IntPredicate getting = predicate.onErrorGetUncheckedAsBoolean(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> getting.test(1));
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
            CheckedIntPredicate<IOException> predicate = Spied.checkedIntPredicate(d -> d % 2 != 0);

            IntPredicate returning = predicate.onErrorReturn(false);

            assertTrue(returning.test(1));

            verify(predicate).test(1);
            verify(predicate).onErrorReturn(false);
            verifyNoMoreInteractions(predicate);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedIntPredicate<IOException> predicate = Spied.checkedIntPredicate(d -> {
                throw new IOException(Integer.toString(d));
            });

            IntPredicate returning = predicate.onErrorReturn(false);

            assertFalse(returning.test(1));

            verify(predicate).test(1);
            verify(predicate).onErrorReturn(false);
            verifyNoMoreInteractions(predicate);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedIntPredicate<IOException> predicate = Spied.checkedIntPredicate(d -> {
                throw new IllegalStateException(Integer.toString(d));
            });

            IntPredicate returning = predicate.onErrorReturn(false);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> returning.test(1));
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
            CheckedIntPredicate<IOException> predicate = Spied.checkedIntPredicate(d -> d % 2 != 0);

            IntPredicate unchecked = predicate.unchecked();

            assertTrue(unchecked.test(1));

            verify(predicate).test(1);
            verify(predicate).unchecked();
            verify(predicate).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(predicate);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedIntPredicate<IOException> predicate = Spied.checkedIntPredicate(d -> {
                throw new IOException(Integer.toString(d));
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

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedIntPredicate<IOException> predicate = Spied.checkedIntPredicate(d -> {
                throw new IllegalArgumentException(Integer.toString(d));
            });

            IntPredicate unchecked = predicate.unchecked();

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> unchecked.test(1));
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
            assertThrows(NullPointerException.class, () -> CheckedIntPredicate.of(null));
        }

        @Test
        void testNonNullArgument() throws IOException {
            CheckedIntPredicate<IOException> predicate = CheckedIntPredicate.of(d -> d % 2 != 0);

            assertTrue(predicate.test(1));
        }
    }

    @Nested
    class Not {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> CheckedIntPredicate.not(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedIntPredicate<IOException> predicate = Spied.checkedIntPredicate(d -> d % 2 != 0);

            CheckedIntPredicate<IOException> negated = CheckedIntPredicate.not(predicate);

            assertFalse(negated.test(1));
            assertTrue(negated.test(0));

            verify(predicate).negate();
            verify(predicate).test(1);
            verify(predicate).test(0);
            verifyNoMoreInteractions(predicate);
        }

        @Test
        void testArgumentThrowsChecked() throws IOException {
            CheckedIntPredicate<IOException> predicate = Spied.checkedIntPredicate(d -> {
                throw new IOException(Integer.toString(d));
            });

            CheckedIntPredicate<IOException> negated = CheckedIntPredicate.not(predicate);

            IOException thrown = assertThrows(IOException.class, () -> negated.test(1));
            assertEquals("1", thrown.getMessage());

            verify(predicate).negate();
            verify(predicate).test(1);
            verifyNoMoreInteractions(predicate);
        }

        @Test
        void testArgumentThrowsUnchecked() throws IOException {
            CheckedIntPredicate<IOException> predicate = Spied.checkedIntPredicate(d -> {
                throw new IllegalStateException(Integer.toString(d));
            });

            CheckedIntPredicate<IOException> negated = CheckedIntPredicate.not(predicate);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> negated.test(1));
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
            assertThrows(NullPointerException.class, () -> CheckedIntPredicate.unchecked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedIntPredicate<IOException> predicate = Spied.checkedIntPredicate(d -> d % 2 != 0);

            IntPredicate unchecked = CheckedIntPredicate.unchecked(predicate);

            assertTrue(unchecked.test(1));

            verify(predicate).unchecked();
            verify(predicate).onErrorThrowAsUnchecked(any());
            verify(predicate).test(1);
            verifyNoMoreInteractions(predicate);
        }

        @Test
        void testArgumentThrowsChecked() throws IOException {
            CheckedIntPredicate<IOException> predicate = Spied.checkedIntPredicate(d -> {
                throw new IOException(Integer.toString(d));
            });

            IntPredicate unchecked = CheckedIntPredicate.unchecked(predicate);

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.test(1));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1", cause.getMessage());

            verify(predicate).unchecked();
            verify(predicate).onErrorThrowAsUnchecked(any());
            verify(predicate).test(1);
            verifyNoMoreInteractions(predicate);
        }

        @Test
        void testArgumentThrowsUnchecked() throws IOException {
            CheckedIntPredicate<IOException> predicate = Spied.checkedIntPredicate(d -> {
                throw new IllegalArgumentException(Integer.toString(d));
            });

            IntPredicate unchecked = CheckedIntPredicate.unchecked(predicate);

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> unchecked.test(1));
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
            assertThrows(NullPointerException.class, () -> CheckedIntPredicate.checked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedIntPredicate<IOException> predicate = CheckedIntPredicate.checked(d -> d % 2 != 0);

            assertTrue(predicate.test(1));
        }

        @Test
        void testArgumentThrowsUnchecked() {
            CheckedIntPredicate<IOException> predicate = CheckedIntPredicate.checked(d -> {
                throw new UncheckedException(Integer.toString(d), new IOException());
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
            IntPredicate predicate = d -> d % 2 != 0;

            assertThrows(NullPointerException.class, () -> CheckedIntPredicate.checked(null, IOException.class));
            assertThrows(NullPointerException.class, () -> CheckedIntPredicate.checked(predicate, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedIntPredicate<IOException> predicate = CheckedIntPredicate.checked(d -> d % 2 != 0, IOException.class);

            assertTrue(predicate.test(1));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                CheckedIntPredicate<IOException> predicate = CheckedIntPredicate.checked(d -> {
                    throw new UncheckedException(new IOException(Integer.toString(d)));
                }, IOException.class);

                IOException thrown = assertThrows(IOException.class, () -> predicate.test(1));
                assertEquals("1", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                CheckedIntPredicate<IOException> predicate = CheckedIntPredicate.checked(d -> {
                    throw new UncheckedException(new FileNotFoundException(Integer.toString(d)));
                }, IOException.class);

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class, () -> predicate.test(1));
                assertEquals("1", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                CheckedIntPredicate<IOException> predicate = CheckedIntPredicate.checked(d -> {
                    throw new UncheckedException(new ParseException(Integer.toString(d), 0));
                }, IOException.class);

                UncheckedException thrown = assertThrows(UncheckedException.class, () -> predicate.test(1));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("1", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            CheckedIntPredicate<IOException> predicate = CheckedIntPredicate.checked(d -> {
                throw new IllegalStateException(Integer.toString(d));
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

            assertThrows(NullPointerException.class, () -> CheckedIntPredicate.invokeAndUnwrap(null, 1, IOException.class));
            assertThrows(NullPointerException.class, () -> CheckedIntPredicate.invokeAndUnwrap(predicate, 1, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            IntPredicate predicate = d -> d % 2 != 0;

            assertTrue(CheckedIntPredicate.invokeAndUnwrap(predicate, 1, IOException.class));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                IntPredicate predicate = d -> {
                    throw new UncheckedException(new IOException(Integer.toString(d)));
                };

                IOException thrown = assertThrows(IOException.class, () -> CheckedIntPredicate.invokeAndUnwrap(predicate, 1, IOException.class));
                assertEquals("1", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                IntPredicate predicate = d -> {
                    throw new UncheckedException(new FileNotFoundException(Integer.toString(d)));
                };

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class,
                        () -> CheckedIntPredicate.invokeAndUnwrap(predicate, 1, IOException.class));
                assertEquals("1", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                IntPredicate predicate = d -> {
                    throw new UncheckedException(new ParseException(Integer.toString(d), 0));
                };

                UncheckedException thrown = assertThrows(UncheckedException.class,
                        () -> CheckedIntPredicate.invokeAndUnwrap(predicate, 1, IOException.class));
                ParseException cause = assertInstanceOf(ParseException.class, thrown.getCause());
                assertEquals("1", cause.getMessage());
                assertEquals(0, cause.getErrorOffset());
            }
        }

        @Test
        void testArgumentThrowsOther() {
            IntPredicate predicate = d -> {
                throw new IllegalStateException(Integer.toString(d));
            };

            IllegalStateException thrown = assertThrows(IllegalStateException.class,
                    () -> CheckedIntPredicate.invokeAndUnwrap(predicate, 1, IOException.class));
            assertEquals("1", thrown.getMessage());
        }
    }
}
