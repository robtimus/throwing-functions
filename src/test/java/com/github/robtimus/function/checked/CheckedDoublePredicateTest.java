/*
 * CheckedDoublePredicateTest.java
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
import java.util.function.DoublePredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("nls")
class CheckedDoublePredicateTest {

    @Nested
    class And {

        @Test
        void testNullArgument() {
            CheckedDoublePredicate<IOException> predicate = Spied.checkedDoublePredicate(d -> d % 2 != 0);

            assertThrows(NullPointerException.class, () -> predicate.and(null));
        }

        @Nested
        class NeitherThrows {

            @Test
            void testThisMatches() throws IOException {
                CheckedDoublePredicate<IOException> predicate = Spied.checkedDoublePredicate(d -> d % 2 != 0);
                CheckedDoublePredicate<IOException> other = Spied.checkedDoublePredicate(d -> d % 2 == 0);

                CheckedDoublePredicate<IOException> composed = predicate.and(other);

                assertFalse(composed.test(1D));

                verify(predicate).test(1D);
                verify(predicate).and(other);
                verify(other).test(1D);
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testOtherMatches() throws IOException {
                CheckedDoublePredicate<IOException> predicate = Spied.checkedDoublePredicate(d -> d % 2 == 0);
                CheckedDoublePredicate<IOException> other = Spied.checkedDoublePredicate(d -> d % 2 != 0);

                CheckedDoublePredicate<IOException> composed = predicate.and(other);

                assertFalse(composed.test(1D));

                verify(predicate).test(1D);
                verify(predicate).and(other);
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testBothMatch() throws IOException {
                CheckedDoublePredicate<IOException> predicate = Spied.checkedDoublePredicate(d -> d % 2 != 0);
                CheckedDoublePredicate<IOException> other = Spied.checkedDoublePredicate(d -> d > 0);

                CheckedDoublePredicate<IOException> composed = predicate.and(other);

                assertTrue(composed.test(1D));

                verify(predicate).test(1D);
                verify(predicate).and(other);
                verify(other).test(1D);
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testNeitherMatches() throws IOException {
                CheckedDoublePredicate<IOException> predicate = Spied.checkedDoublePredicate(d -> d % 2 != 0);
                CheckedDoublePredicate<IOException> other = Spied.checkedDoublePredicate(d -> d > 0);

                CheckedDoublePredicate<IOException> composed = predicate.and(other);

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
                CheckedDoublePredicate<IOException> predicate = Spied.checkedDoublePredicate(d -> {
                    throw new IOException(Double.toString(d));
                });
                CheckedDoublePredicate<IOException> other = Spied.checkedDoublePredicate(d -> d % 2 != 0);

                CheckedDoublePredicate<IOException> composed = predicate.and(other);

                IOException thrown = assertThrows(IOException.class, () -> composed.test(1D));
                assertEquals("1.0", thrown.getMessage());

                verify(predicate).test(1D);
                verify(predicate).and(other);
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testOtherDoesNotMatch() throws IOException {
                CheckedDoublePredicate<IOException> predicate = Spied.checkedDoublePredicate(d -> {
                    throw new IOException(Double.toString(d));
                });
                CheckedDoublePredicate<IOException> other = Spied.checkedDoublePredicate(d -> d % 2 == 0);

                CheckedDoublePredicate<IOException> composed = predicate.and(other);

                IOException thrown = assertThrows(IOException.class, () -> composed.test(1D));
                assertEquals("1.0", thrown.getMessage());

                verify(predicate).test(1D);
                verify(predicate).and(other);
                verifyNoMoreInteractions(predicate, other);
            }
        }

        @Nested
        class ThisThrowsUnchecked {

            @Test
            void testOtherMatches() throws IOException {
                CheckedDoublePredicate<IOException> predicate = Spied.checkedDoublePredicate(d -> {
                    throw new IllegalStateException(Double.toString(d));
                });
                CheckedDoublePredicate<IOException> other = Spied.checkedDoublePredicate(d -> d % 2 != 0);

                CheckedDoublePredicate<IOException> composed = predicate.and(other);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> composed.test(1D));
                assertEquals("1.0", thrown.getMessage());

                verify(predicate).test(1D);
                verify(predicate).and(other);
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testOtherDoesNotMatch() throws IOException {
                CheckedDoublePredicate<IOException> predicate = Spied.checkedDoublePredicate(d -> {
                    throw new IllegalStateException(Double.toString(d));
                });
                CheckedDoublePredicate<IOException> other = Spied.checkedDoublePredicate(d -> d % 2 == 0);

                CheckedDoublePredicate<IOException> composed = predicate.and(other);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> composed.test(1D));
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
                CheckedDoublePredicate<IOException> predicate = Spied.checkedDoublePredicate(d -> d % 2 != 0);
                CheckedDoublePredicate<IOException> other = Spied.checkedDoublePredicate(d -> {
                    throw new IOException(Double.toString(d));
                });

                CheckedDoublePredicate<IOException> composed = predicate.and(other);

                IOException thrown = assertThrows(IOException.class, () -> composed.test(1D));
                assertEquals("1.0", thrown.getMessage());

                verify(predicate).test(1D);
                verify(predicate).and(other);
                verify(other).test(1D);
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testThisDoesNotMatch() throws IOException {
                CheckedDoublePredicate<IOException> predicate = Spied.checkedDoublePredicate(d -> d % 2 == 0);
                CheckedDoublePredicate<IOException> other = Spied.checkedDoublePredicate(d -> {
                    throw new IOException(Double.toString(d));
                });

                CheckedDoublePredicate<IOException> composed = predicate.and(other);

                assertFalse(composed.test(1D));

                verify(predicate).test(1D);
                verify(predicate).and(other);
                verifyNoMoreInteractions(predicate, other);
            }
        }

        @Nested
        class OtherThrowsUnchecked {

            @Test
            void testThisMatches() throws IOException {
                CheckedDoublePredicate<IOException> predicate = Spied.checkedDoublePredicate(d -> d % 2 != 0);
                CheckedDoublePredicate<IOException> other = Spied.checkedDoublePredicate(d -> {
                    throw new IllegalStateException(Double.toString(d));
                });

                CheckedDoublePredicate<IOException> composed = predicate.and(other);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> composed.test(1D));
                assertEquals("1.0", thrown.getMessage());

                verify(predicate).test(1D);
                verify(predicate).and(other);
                verify(other).test(1D);
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testThisDoesNotMatch() throws IOException {
                CheckedDoublePredicate<IOException> predicate = Spied.checkedDoublePredicate(d -> d % 2 == 0);
                CheckedDoublePredicate<IOException> other = Spied.checkedDoublePredicate(d -> {
                    throw new IOException(Double.toString(d));
                });

                CheckedDoublePredicate<IOException> composed = predicate.and(other);

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
            CheckedDoublePredicate<IOException> predicate = Spied.checkedDoublePredicate(d -> d % 2 != 0);

            CheckedDoublePredicate<IOException> negated = predicate.negate();

            assertFalse(negated.test(1D));
            assertTrue(negated.test(0D));

            verify(predicate).test(1D);
            verify(predicate).test(0D);
            verify(predicate).negate();
            verifyNoMoreInteractions(predicate);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedDoublePredicate<IOException> predicate = Spied.checkedDoublePredicate(d -> {
                throw new IOException(Double.toString(d));
            });

            CheckedDoublePredicate<IOException> negated = predicate.negate();

            IOException thrown = assertThrows(IOException.class, () -> negated.test(1D));
            assertEquals("1.0", thrown.getMessage());

            verify(predicate).test(1D);
            verify(predicate).negate();
            verifyNoMoreInteractions(predicate);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedDoublePredicate<IOException> predicate = Spied.checkedDoublePredicate(d -> {
                throw new IllegalStateException(Double.toString(d));
            });

            CheckedDoublePredicate<IOException> negated = predicate.negate();

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> negated.test(1D));
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
            CheckedDoublePredicate<IOException> predicate = Spied.checkedDoublePredicate(d -> d % 2 != 0);

            assertThrows(NullPointerException.class, () -> predicate.or(null));
        }

        @Nested
        class NeitherThrows {

            @Test
            void testThisMatches() throws IOException {
                CheckedDoublePredicate<IOException> predicate = Spied.checkedDoublePredicate(d -> d % 2 != 0);
                CheckedDoublePredicate<IOException> other = Spied.checkedDoublePredicate(d -> d % 2 == 0);

                CheckedDoublePredicate<IOException> composed = predicate.or(other);

                assertTrue(composed.test(1D));

                verify(predicate).test(1D);
                verify(predicate).or(other);
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testOtherMatches() throws IOException {
                CheckedDoublePredicate<IOException> predicate = Spied.checkedDoublePredicate(d -> d % 2 == 0);
                CheckedDoublePredicate<IOException> other = Spied.checkedDoublePredicate(d -> d % 2 != 0);

                CheckedDoublePredicate<IOException> composed = predicate.or(other);

                assertTrue(composed.test(1D));

                verify(predicate).test(1D);
                verify(predicate).or(other);
                verify(other).test(1D);
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testBothMatch() throws IOException {
                CheckedDoublePredicate<IOException> predicate = Spied.checkedDoublePredicate(d -> d % 2 != 0);
                CheckedDoublePredicate<IOException> other = Spied.checkedDoublePredicate(d -> d > 0);

                CheckedDoublePredicate<IOException> composed = predicate.or(other);

                assertTrue(composed.test(1D));

                verify(predicate).test(1D);
                verify(predicate).or(other);
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testNeitherMatches() throws IOException {
                CheckedDoublePredicate<IOException> predicate = Spied.checkedDoublePredicate(d -> d % 2 != 0);
                CheckedDoublePredicate<IOException> other = Spied.checkedDoublePredicate(d -> d > 0);

                CheckedDoublePredicate<IOException> composed = predicate.or(other);

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
                CheckedDoublePredicate<IOException> predicate = Spied.checkedDoublePredicate(d -> {
                    throw new IOException(Double.toString(d));
                });
                CheckedDoublePredicate<IOException> other = Spied.checkedDoublePredicate(d -> d % 2 != 0);

                CheckedDoublePredicate<IOException> composed = predicate.or(other);

                IOException thrown = assertThrows(IOException.class, () -> composed.test(1D));
                assertEquals("1.0", thrown.getMessage());

                verify(predicate).test(1D);
                verify(predicate).or(other);
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testOtherDoesNotMatch() throws IOException {
                CheckedDoublePredicate<IOException> predicate = Spied.checkedDoublePredicate(d -> {
                    throw new IOException(Double.toString(d));
                });
                CheckedDoublePredicate<IOException> other = Spied.checkedDoublePredicate(d -> d % 2 == 0);

                CheckedDoublePredicate<IOException> composed = predicate.or(other);

                IOException thrown = assertThrows(IOException.class, () -> composed.test(1D));
                assertEquals("1.0", thrown.getMessage());

                verify(predicate).test(1D);
                verify(predicate).or(other);
                verifyNoMoreInteractions(predicate, other);
            }
        }

        @Nested
        class ThisThrowsUnchecked {

            @Test
            void testOtherMatches() throws IOException {
                CheckedDoublePredicate<IOException> predicate = Spied.checkedDoublePredicate(d -> {
                    throw new IllegalStateException(Double.toString(d));
                });
                CheckedDoublePredicate<IOException> other = Spied.checkedDoublePredicate(d -> d % 2 != 0);

                CheckedDoublePredicate<IOException> composed = predicate.or(other);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> composed.test(1D));
                assertEquals("1.0", thrown.getMessage());

                verify(predicate).test(1D);
                verify(predicate).or(other);
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testOtherDoesNotMatch() throws IOException {
                CheckedDoublePredicate<IOException> predicate = Spied.checkedDoublePredicate(d -> {
                    throw new IllegalStateException(Double.toString(d));
                });
                CheckedDoublePredicate<IOException> other = Spied.checkedDoublePredicate(d -> d % 2 == 0);

                CheckedDoublePredicate<IOException> composed = predicate.or(other);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> composed.test(1D));
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
                CheckedDoublePredicate<IOException> predicate = Spied.checkedDoublePredicate(d -> d % 2 != 0);
                CheckedDoublePredicate<IOException> other = Spied.checkedDoublePredicate(d -> {
                    throw new IOException(Double.toString(d));
                });

                CheckedDoublePredicate<IOException> composed = predicate.or(other);

                assertTrue(composed.test(1D));

                verify(predicate).test(1D);
                verify(predicate).or(other);
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testThisDoesNotMatch() throws IOException {
                CheckedDoublePredicate<IOException> predicate = Spied.checkedDoublePredicate(d -> d % 2 == 0);
                CheckedDoublePredicate<IOException> other = Spied.checkedDoublePredicate(d -> {
                    throw new IOException(Double.toString(d));
                });

                CheckedDoublePredicate<IOException> composed = predicate.or(other);

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

            @Test
            void testThisMatches() throws IOException {
                CheckedDoublePredicate<IOException> predicate = Spied.checkedDoublePredicate(d -> d % 2 != 0);
                CheckedDoublePredicate<IOException> other = Spied.checkedDoublePredicate(d -> {
                    throw new IllegalStateException(Double.toString(d));
                });

                CheckedDoublePredicate<IOException> composed = predicate.or(other);

                assertTrue(composed.test(1D));

                verify(predicate).test(1D);
                verify(predicate).or(other);
                verifyNoMoreInteractions(predicate, other);
            }

            @Test
            void testThisDoesNotMatch() throws IOException {
                CheckedDoublePredicate<IOException> predicate = Spied.checkedDoublePredicate(d -> d % 2 == 0);
                CheckedDoublePredicate<IOException> other = Spied.checkedDoublePredicate(d -> {
                    throw new IllegalStateException(Double.toString(d));
                });

                CheckedDoublePredicate<IOException> composed = predicate.or(other);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> composed.test(1D));
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
            CheckedDoublePredicate<IOException> predicate = Spied.checkedDoublePredicate(d -> d % 2 != 0);

            assertThrows(NullPointerException.class, () -> predicate.onErrorThrowAsChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedDoublePredicate<IOException> predicate = Spied.checkedDoublePredicate(d -> d % 2 != 0);

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedDoublePredicate<ExecutionException> throwing = predicate.onErrorThrowAsChecked(errorMapper);

            assertTrue(throwing.test(1D));

            verify(predicate).test(1D);
            verify(predicate).onErrorThrowAsChecked(errorMapper);
            verifyNoMoreInteractions(predicate, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedDoublePredicate<IOException> predicate = Spied.checkedDoublePredicate(d -> {
                throw new IOException(Double.toString(d));
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedDoublePredicate<ExecutionException> throwing = predicate.onErrorThrowAsChecked(errorMapper);

            ExecutionException thrown = assertThrows(ExecutionException.class, () -> throwing.test(1D));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1.0", cause.getMessage());

            verify(predicate).test(1D);
            verify(predicate).onErrorThrowAsChecked(errorMapper);
            verify(errorMapper).apply(any());
            verifyNoMoreInteractions(predicate, errorMapper);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedDoublePredicate<IOException> predicate = Spied.checkedDoublePredicate(d -> {
                throw new IllegalStateException(Double.toString(d));
            });

            Function<IOException, ExecutionException> errorMapper = Spied.function(ExecutionException::new);

            CheckedDoublePredicate<ExecutionException> throwing = predicate.onErrorThrowAsChecked(errorMapper);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> throwing.test(1D));
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
            CheckedDoublePredicate<IOException> predicate = Spied.checkedDoublePredicate(d -> d % 2 != 0);

            assertThrows(NullPointerException.class, () -> predicate.onErrorThrowAsUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedDoublePredicate<IOException> predicate = Spied.checkedDoublePredicate(d -> d % 2 != 0);

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            DoublePredicate throwing = predicate.onErrorThrowAsUnchecked(errorMapper);

            assertTrue(throwing.test(1D));

            verify(predicate).test(1D);
            verify(predicate).onErrorThrowAsUnchecked(errorMapper);
            verifyNoMoreInteractions(predicate, errorMapper);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedDoublePredicate<IOException> predicate = Spied.checkedDoublePredicate(d -> {
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

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedDoublePredicate<IOException> predicate = Spied.checkedDoublePredicate(d -> {
                throw new IllegalArgumentException(Double.toString(d));
            });

            Function<IOException, IllegalStateException> errorMapper = Spied.function(IllegalStateException::new);

            DoublePredicate throwing = predicate.onErrorThrowAsUnchecked(errorMapper);

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> throwing.test(1D));
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
            CheckedDoublePredicate<IOException> predicate = Spied.checkedDoublePredicate(d -> d % 2 != 0);

            assertThrows(NullPointerException.class, () -> predicate.onErrorHandleChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedDoublePredicate<IOException> predicate = Spied.checkedDoublePredicate(d -> d % 2 != 0);

            CheckedPredicate<IOException, ExecutionException> errorHandler = Spied.checkedPredicate(e -> e.getMessage() == null);

            CheckedDoublePredicate<ExecutionException> handling = predicate.onErrorHandleChecked(errorHandler);

            assertTrue(handling.test(1D));

            verify(predicate).test(1D);
            verify(predicate).onErrorHandleChecked(errorHandler);
            verifyNoMoreInteractions(predicate, errorHandler);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testHandlerThrowsNothing() throws IOException, ExecutionException {
                CheckedDoublePredicate<IOException> predicate = Spied.checkedDoublePredicate(d -> {
                    throw new IOException(Double.toString(d));
                });

                CheckedPredicate<IOException, ExecutionException> errorHandler = Spied.checkedPredicate(e -> e.getMessage() == null);

                CheckedDoublePredicate<ExecutionException> handling = predicate.onErrorHandleChecked(errorHandler);

                assertFalse(handling.test(1D));

                verify(predicate).test(1D);
                verify(predicate).onErrorHandleChecked(errorHandler);
                verify(errorHandler).test(any());
                verifyNoMoreInteractions(predicate, errorHandler);
            }

            @Test
            void testHandlerThrowsChecked() throws IOException, ExecutionException {
                CheckedDoublePredicate<IOException> predicate = Spied.checkedDoublePredicate(d -> {
                    throw new IOException(Double.toString(d));
                });

                CheckedPredicate<IOException, ExecutionException> errorHandler = Spied.checkedPredicate(e -> {
                    throw new ExecutionException(e);
                });

                CheckedDoublePredicate<ExecutionException> handling = predicate.onErrorHandleChecked(errorHandler);

                ExecutionException thrown = assertThrows(ExecutionException.class, () -> handling.test(1D));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("1.0", cause.getMessage());

                verify(predicate).test(1D);
                verify(predicate).onErrorHandleChecked(errorHandler);
                verify(errorHandler).test(any());
                verifyNoMoreInteractions(predicate, errorHandler);
            }

            @Test
            void testHandlerThrowsUnchecked() throws IOException, ExecutionException {
                CheckedDoublePredicate<IOException> predicate = Spied.checkedDoublePredicate(d -> {
                    throw new IOException(Double.toString(d));
                });

                CheckedPredicate<IOException, ExecutionException> errorHandler = Spied.checkedPredicate(e -> {
                    throw new IllegalStateException(e);
                });

                CheckedDoublePredicate<ExecutionException> handling = predicate.onErrorHandleChecked(errorHandler);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.test(1D));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("1.0", cause.getMessage());

                verify(predicate).test(1D);
                verify(predicate).onErrorHandleChecked(errorHandler);
                verify(errorHandler).test(any());
                verifyNoMoreInteractions(predicate, errorHandler);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedDoublePredicate<IOException> predicate = Spied.checkedDoublePredicate(d -> {
                throw new IllegalStateException(Double.toString(d));
            });

            CheckedPredicate<IOException, ExecutionException> errorHandler = Spied.checkedPredicate(e -> e.getMessage() == null);

            CheckedDoublePredicate<ExecutionException> handling = predicate.onErrorHandleChecked(errorHandler);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.test(1D));
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
            CheckedDoublePredicate<IOException> predicate = Spied.checkedDoublePredicate(d -> d % 2 != 0);

            assertThrows(NullPointerException.class, () -> predicate.onErrorHandleUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedDoublePredicate<IOException> predicate = Spied.checkedDoublePredicate(d -> d % 2 != 0);

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
                CheckedDoublePredicate<IOException> predicate = Spied.checkedDoublePredicate(d -> {
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

            @Test
            void testHandlerThrowsUnchecked() throws IOException {
                CheckedDoublePredicate<IOException> predicate = Spied.checkedDoublePredicate(d -> {
                    throw new IOException(Double.toString(d));
                });

                Predicate<IOException> errorHandler = Spied.predicate(e -> {
                    throw new IllegalStateException(e);
                });

                DoublePredicate handling = predicate.onErrorHandleUnchecked(errorHandler);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.test(1D));
                IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
                assertEquals("1.0", cause.getMessage());

                verify(predicate).test(1D);
                verify(predicate).onErrorHandleUnchecked(errorHandler);
                verify(errorHandler).test(any());
                verifyNoMoreInteractions(predicate, errorHandler);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedDoublePredicate<IOException> predicate = Spied.checkedDoublePredicate(d -> {
                throw new IllegalStateException(Double.toString(d));
            });

            Predicate<IOException> errorHandler = Spied.predicate(e -> e.getMessage() == null);

            DoublePredicate handling = predicate.onErrorHandleUnchecked(errorHandler);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handling.test(1D));
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
            CheckedDoublePredicate<IOException> predicate = Spied.checkedDoublePredicate(d -> d % 2 != 0);

            assertThrows(NullPointerException.class, () -> predicate.onErrorTestChecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedDoublePredicate<IOException> predicate = Spied.checkedDoublePredicate(d -> d % 2 != 0);

            CheckedDoublePredicate<ExecutionException> fallback = Spied.checkedDoublePredicate(d -> d % 2 == 0);

            CheckedDoublePredicate<ExecutionException> testing = predicate.onErrorTestChecked(fallback);

            assertTrue(testing.test(1D));

            verify(predicate).test(1D);
            verify(predicate).onErrorTestChecked(fallback);
            verifyNoMoreInteractions(predicate, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                CheckedDoublePredicate<IOException> predicate = Spied.checkedDoublePredicate(d -> {
                    throw new IOException(Double.toString(d));
                });

                CheckedDoublePredicate<ParseException> fallback = Spied.checkedDoublePredicate(d -> d % 2 == 0);

                CheckedDoublePredicate<ParseException> testing = predicate.onErrorTestChecked(fallback);

                assertFalse(testing.test(1D));

                verify(predicate).test(1D);
                verify(predicate).onErrorTestChecked(fallback);
                verify(fallback).test(1D);
                verifyNoMoreInteractions(predicate, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                CheckedDoublePredicate<IOException> predicate = Spied.checkedDoublePredicate(d -> {
                    throw new IOException(Double.toString(d));
                });

                CheckedDoublePredicate<ParseException> fallback = Spied.checkedDoublePredicate(d -> {
                    throw new ParseException(Double.toString(d), 0);
                });

                CheckedDoublePredicate<ParseException> testing = predicate.onErrorTestChecked(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> testing.test(1D));
                assertEquals("1.0", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(predicate).test(1D);
                verify(predicate).onErrorTestChecked(fallback);
                verify(fallback).test(1D);
                verifyNoMoreInteractions(predicate, fallback);
            }

            @Test
            void testFallbackThrowsUnchecked() throws IOException, ParseException {
                CheckedDoublePredicate<IOException> predicate = Spied.checkedDoublePredicate(d -> {
                    throw new IOException(Double.toString(d));
                });

                CheckedDoublePredicate<ParseException> fallback = Spied.checkedDoublePredicate(d -> {
                    throw new IllegalStateException(Double.toString(d));
                });

                CheckedDoublePredicate<ParseException> testing = predicate.onErrorTestChecked(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> testing.test(1D));
                assertEquals("1.0", thrown.getMessage());

                verify(predicate).test(1D);
                verify(predicate).onErrorTestChecked(fallback);
                verify(fallback).test(1D);
                verifyNoMoreInteractions(predicate, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedDoublePredicate<IOException> predicate = Spied.checkedDoublePredicate(d -> {
                throw new IllegalStateException(Double.toString(d));
            });

            CheckedDoublePredicate<ParseException> fallback = Spied.checkedDoublePredicate(d -> d % 2 == 0);

            CheckedDoublePredicate<ParseException> testing = predicate.onErrorTestChecked(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> testing.test(1D));
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
            CheckedDoublePredicate<IOException> predicate = Spied.checkedDoublePredicate(d -> d % 2 != 0);

            assertThrows(NullPointerException.class, () -> predicate.onErrorTestUnchecked(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedDoublePredicate<IOException> predicate = Spied.checkedDoublePredicate(d -> d % 2 != 0);

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
                CheckedDoublePredicate<IOException> predicate = Spied.checkedDoublePredicate(d -> {
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

            @Test
            void testFallbackThrowsUnchecked() throws IOException {
                CheckedDoublePredicate<IOException> predicate = Spied.checkedDoublePredicate(d -> {
                    throw new IOException(Double.toString(d));
                });

                DoublePredicate fallback = Spied.doublePredicate(d -> {
                    throw new IllegalStateException(Double.toString(d));
                });

                DoublePredicate testing = predicate.onErrorTestUnchecked(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> testing.test(1D));
                assertEquals("1.0", thrown.getMessage());

                verify(predicate).test(1.0);
                verify(predicate).onErrorTestUnchecked(fallback);
                verify(fallback).test(1.0);
                verifyNoMoreInteractions(predicate, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedDoublePredicate<IOException> predicate = Spied.checkedDoublePredicate(d -> {
                throw new IllegalStateException(Double.toString(d));
            });

            DoublePredicate fallback = Spied.doublePredicate(d -> d % 2 == 0);

            DoublePredicate testing = predicate.onErrorTestUnchecked(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> testing.test(1D));
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
            CheckedDoublePredicate<IOException> predicate = Spied.checkedDoublePredicate(d -> d % 2 != 0);

            assertThrows(NullPointerException.class, () -> predicate.onErrorGetCheckedAsBoolean(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException, ExecutionException {
            CheckedDoublePredicate<IOException> predicate = Spied.checkedDoublePredicate(d -> d % 2 != 0);

            CheckedBooleanSupplier<ExecutionException> fallback = Spied.checkedBooleanSupplier(() -> false);

            CheckedDoublePredicate<ExecutionException> getting = predicate.onErrorGetCheckedAsBoolean(fallback);

            assertTrue(getting.test(1D));

            verify(predicate).test(1D);
            verify(predicate).onErrorGetCheckedAsBoolean(fallback);
            verifyNoMoreInteractions(predicate, fallback);
        }

        @Nested
        class ThisThrowsChecked {

            @Test
            void testFallbackThrowsNothing() throws IOException, ParseException {
                CheckedDoublePredicate<IOException> predicate = Spied.checkedDoublePredicate(d -> {
                    throw new IOException(Double.toString(d));
                });

                CheckedBooleanSupplier<ParseException> fallback = Spied.checkedBooleanSupplier(() -> false);

                CheckedDoublePredicate<ParseException> getting = predicate.onErrorGetCheckedAsBoolean(fallback);

                assertFalse(getting.test(1D));

                verify(predicate).test(1D);
                verify(predicate).onErrorGetCheckedAsBoolean(fallback);
                verify(fallback).getAsBoolean();
                verifyNoMoreInteractions(predicate, fallback);
            }

            @Test
            void testFallbackThrowsChecked() throws IOException, ParseException {
                CheckedDoublePredicate<IOException> predicate = Spied.checkedDoublePredicate(d -> {
                    throw new IOException(Double.toString(d));
                });

                CheckedBooleanSupplier<ParseException> fallback = Spied.checkedBooleanSupplier(() -> {
                    throw new ParseException("bar", 0);
                });

                CheckedDoublePredicate<ParseException> getting = predicate.onErrorGetCheckedAsBoolean(fallback);

                ParseException thrown = assertThrows(ParseException.class, () -> getting.test(1D));
                assertEquals("bar", thrown.getMessage());
                assertEquals(0, thrown.getErrorOffset());

                verify(predicate).test(1D);
                verify(predicate).onErrorGetCheckedAsBoolean(fallback);
                verify(fallback).getAsBoolean();
                verifyNoMoreInteractions(predicate, fallback);
            }

            @Test
            void testFallbackThrowsUnchecked() throws IOException, ParseException {
                CheckedDoublePredicate<IOException> predicate = Spied.checkedDoublePredicate(d -> {
                    throw new IOException(Double.toString(d));
                });

                CheckedBooleanSupplier<ParseException> fallback = Spied.checkedBooleanSupplier(() -> {
                    throw new IllegalStateException("bar");
                });

                CheckedDoublePredicate<ParseException> getting = predicate.onErrorGetCheckedAsBoolean(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> getting.test(1D));
                assertEquals("bar", thrown.getMessage());

                verify(predicate).test(1D);
                verify(predicate).onErrorGetCheckedAsBoolean(fallback);
                verify(fallback).getAsBoolean();
                verifyNoMoreInteractions(predicate, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedDoublePredicate<IOException> predicate = Spied.checkedDoublePredicate(d -> {
                throw new IllegalStateException(Double.toString(d));
            });

            CheckedBooleanSupplier<ParseException> fallback = Spied.checkedBooleanSupplier(() -> false);

            CheckedDoublePredicate<ParseException> getting = predicate.onErrorGetCheckedAsBoolean(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> getting.test(1D));
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
            CheckedDoublePredicate<IOException> predicate = Spied.checkedDoublePredicate(d -> d % 2 != 0);

            assertThrows(NullPointerException.class, () -> predicate.onErrorGetUncheckedAsBoolean(null));
        }

        @Test
        void testThisThrowsNothing() throws IOException {
            CheckedDoublePredicate<IOException> predicate = Spied.checkedDoublePredicate(d -> d % 2 != 0);

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
                CheckedDoublePredicate<IOException> predicate = Spied.checkedDoublePredicate(d -> {
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

            @Test
            void testFallbackThrowsUnchecked() throws IOException {
                CheckedDoublePredicate<IOException> predicate = Spied.checkedDoublePredicate(d -> {
                    throw new IOException(Double.toString(d));
                });

                BooleanSupplier fallback = Spied.booleanSupplier(() -> {
                    throw new IllegalStateException("bar");
                });

                DoublePredicate getting = predicate.onErrorGetUncheckedAsBoolean(fallback);

                IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> getting.test(1D));
                assertEquals("bar", thrown.getMessage());

                verify(predicate).test(1D);
                verify(predicate).onErrorGetUncheckedAsBoolean(fallback);
                verify(fallback).getAsBoolean();
                verifyNoMoreInteractions(predicate, fallback);
            }
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedDoublePredicate<IOException> predicate = Spied.checkedDoublePredicate(d -> {
                throw new IllegalStateException(Double.toString(d));
            });

            BooleanSupplier fallback = Spied.booleanSupplier(() -> false);

            DoublePredicate getting = predicate.onErrorGetUncheckedAsBoolean(fallback);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> getting.test(1D));
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
            CheckedDoublePredicate<IOException> predicate = Spied.checkedDoublePredicate(d -> d % 2 != 0);

            DoublePredicate returning = predicate.onErrorReturn(false);

            assertTrue(returning.test(1D));

            verify(predicate).test(1D);
            verify(predicate).onErrorReturn(false);
            verifyNoMoreInteractions(predicate);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedDoublePredicate<IOException> predicate = Spied.checkedDoublePredicate(d -> {
                throw new IOException(Double.toString(d));
            });

            DoublePredicate returning = predicate.onErrorReturn(false);

            assertFalse(returning.test(1D));

            verify(predicate).test(1D);
            verify(predicate).onErrorReturn(false);
            verifyNoMoreInteractions(predicate);
        }

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedDoublePredicate<IOException> predicate = Spied.checkedDoublePredicate(d -> {
                throw new IllegalStateException(Double.toString(d));
            });

            DoublePredicate returning = predicate.onErrorReturn(false);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> returning.test(1D));
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
            CheckedDoublePredicate<IOException> predicate = Spied.checkedDoublePredicate(d -> d % 2 != 0);

            DoublePredicate unchecked = predicate.unchecked();

            assertTrue(unchecked.test(1D));

            verify(predicate).test(1D);
            verify(predicate).unchecked();
            verify(predicate).onErrorThrowAsUnchecked(any());
            verifyNoMoreInteractions(predicate);
        }

        @Test
        void testThisThrowsChecked() throws IOException {
            CheckedDoublePredicate<IOException> predicate = Spied.checkedDoublePredicate(d -> {
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

        @Test
        void testThisThrowsUnchecked() throws IOException {
            CheckedDoublePredicate<IOException> predicate = Spied.checkedDoublePredicate(d -> {
                throw new IllegalArgumentException(Double.toString(d));
            });

            DoublePredicate unchecked = predicate.unchecked();

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> unchecked.test(1D));
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
            assertThrows(NullPointerException.class, () -> CheckedDoublePredicate.of(null));
        }

        @Test
        void testNonNullArgument() throws IOException {
            CheckedDoublePredicate<IOException> predicate = CheckedDoublePredicate.of(d -> d % 2 != 0);

            assertTrue(predicate.test(1D));
        }
    }

    @Nested
    class Not {

        @Test
        void testNullArgument() {
            assertThrows(NullPointerException.class, () -> CheckedDoublePredicate.not(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedDoublePredicate<IOException> predicate = Spied.checkedDoublePredicate(d -> d % 2 != 0);

            CheckedDoublePredicate<IOException> negated = CheckedDoublePredicate.not(predicate);

            assertFalse(negated.test(1D));
            assertTrue(negated.test(0D));

            verify(predicate).negate();
            verify(predicate).test(1D);
            verify(predicate).test(0D);
            verifyNoMoreInteractions(predicate);
        }

        @Test
        void testArgumentThrowsChecked() throws IOException {
            CheckedDoublePredicate<IOException> predicate = Spied.checkedDoublePredicate(d -> {
                throw new IOException(Double.toString(d));
            });

            CheckedDoublePredicate<IOException> negated = CheckedDoublePredicate.not(predicate);

            IOException thrown = assertThrows(IOException.class, () -> negated.test(1D));
            assertEquals("1.0", thrown.getMessage());

            verify(predicate).negate();
            verify(predicate).test(1D);
            verifyNoMoreInteractions(predicate);
        }

        @Test
        void testArgumentThrowsUnchecked() throws IOException {
            CheckedDoublePredicate<IOException> predicate = Spied.checkedDoublePredicate(d -> {
                throw new IllegalStateException(Double.toString(d));
            });

            CheckedDoublePredicate<IOException> negated = CheckedDoublePredicate.not(predicate);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> negated.test(1D));
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
            assertThrows(NullPointerException.class, () -> CheckedDoublePredicate.unchecked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedDoublePredicate<IOException> predicate = Spied.checkedDoublePredicate(d -> d % 2 != 0);

            DoublePredicate unchecked = CheckedDoublePredicate.unchecked(predicate);

            assertTrue(unchecked.test(1D));

            verify(predicate).unchecked();
            verify(predicate).onErrorThrowAsUnchecked(any());
            verify(predicate).test(1D);
            verifyNoMoreInteractions(predicate);
        }

        @Test
        void testArgumentThrowsChecked() throws IOException {
            CheckedDoublePredicate<IOException> predicate = Spied.checkedDoublePredicate(d -> {
                throw new IOException(Double.toString(d));
            });

            DoublePredicate unchecked = CheckedDoublePredicate.unchecked(predicate);

            UncheckedException thrown = assertThrows(UncheckedException.class, () -> unchecked.test(1D));
            IOException cause = assertInstanceOf(IOException.class, thrown.getCause());
            assertEquals("1.0", cause.getMessage());

            verify(predicate).unchecked();
            verify(predicate).onErrorThrowAsUnchecked(any());
            verify(predicate).test(1D);
            verifyNoMoreInteractions(predicate);
        }

        @Test
        void testArgumentThrowsUnchecked() throws IOException {
            CheckedDoublePredicate<IOException> predicate = Spied.checkedDoublePredicate(d -> {
                throw new IllegalArgumentException(Double.toString(d));
            });

            DoublePredicate unchecked = CheckedDoublePredicate.unchecked(predicate);

            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> unchecked.test(1D));
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
            assertThrows(NullPointerException.class, () -> CheckedDoublePredicate.checked(null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedDoublePredicate<IOException> predicate = CheckedDoublePredicate.checked(d -> d % 2 != 0);

            assertTrue(predicate.test(1D));
        }

        @Test
        void testArgumentThrowsUnchecked() {
            CheckedDoublePredicate<IOException> predicate = CheckedDoublePredicate.checked(d -> {
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

            assertThrows(NullPointerException.class, () -> CheckedDoublePredicate.checked(null, IOException.class));
            assertThrows(NullPointerException.class, () -> CheckedDoublePredicate.checked(predicate, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            CheckedDoublePredicate<IOException> predicate = CheckedDoublePredicate.checked(d -> d % 2 != 0, IOException.class);

            assertTrue(predicate.test(1D));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                CheckedDoublePredicate<IOException> predicate = CheckedDoublePredicate.checked(d -> {
                    throw new UncheckedException(new IOException(Double.toString(d)));
                }, IOException.class);

                IOException thrown = assertThrows(IOException.class, () -> predicate.test(1D));
                assertEquals("1.0", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                CheckedDoublePredicate<IOException> predicate = CheckedDoublePredicate.checked(d -> {
                    throw new UncheckedException(new FileNotFoundException(Double.toString(d)));
                }, IOException.class);

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class, () -> predicate.test(1D));
                assertEquals("1.0", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                CheckedDoublePredicate<IOException> predicate = CheckedDoublePredicate.checked(d -> {
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
            CheckedDoublePredicate<IOException> predicate = CheckedDoublePredicate.checked(d -> {
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

            assertThrows(NullPointerException.class, () -> CheckedDoublePredicate.invokeAndUnwrap(null, 1D, IOException.class));
            assertThrows(NullPointerException.class, () -> CheckedDoublePredicate.invokeAndUnwrap(predicate, 1D, null));
        }

        @Test
        void testArgumentThrowsNothing() throws IOException {
            DoublePredicate predicate = d -> d % 2 != 0;

            assertTrue(CheckedDoublePredicate.invokeAndUnwrap(predicate, 1D, IOException.class));
        }

        @Nested
        class ArgumentThrowsUncheckedException {

            @Test
            void testWrappingExactType() {
                DoublePredicate predicate = d -> {
                    throw new UncheckedException(new IOException(Double.toString(d)));
                };

                IOException thrown = assertThrows(IOException.class, () -> CheckedDoublePredicate.invokeAndUnwrap(predicate, 1D, IOException.class));
                assertEquals("1.0", thrown.getMessage());
            }

            @Test
            void testWrappingSubType() {
                DoublePredicate predicate = d -> {
                    throw new UncheckedException(new FileNotFoundException(Double.toString(d)));
                };

                FileNotFoundException thrown = assertThrows(FileNotFoundException.class,
                        () -> CheckedDoublePredicate.invokeAndUnwrap(predicate, 1D, IOException.class));
                assertEquals("1.0", thrown.getMessage());
            }

            @Test
            void testWrappingOther() {
                DoublePredicate predicate = d -> {
                    throw new UncheckedException(new ParseException(Double.toString(d), 0));
                };

                UncheckedException thrown = assertThrows(UncheckedException.class,
                        () -> CheckedDoublePredicate.invokeAndUnwrap(predicate, 1D, IOException.class));
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
                    () -> CheckedDoublePredicate.invokeAndUnwrap(predicate, 1D, IOException.class));
            assertEquals("1.0", thrown.getMessage());
        }
    }
}
