/*
 * UncheckedExceptionTest.java
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.ParseException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("nls")
class UncheckedExceptionTest {

    @Test
    void testWithStackTrace() {
        IOException cause = new IOException();
        UncheckedException exception = UncheckedException.withStackTrace(cause);

        assertEquals(cause.toString(), exception.getMessage());

        List<String> lines = getStackTraceLines(exception);

        assertThat(lines, hasSize(greaterThan(5)));

        assertEquals(exception.toString(), lines.get(0));

        /*
         * First three stack trace elements in reverse order:
         * - this method
         * - UncheckedException.withStackTrace without a message
         * - UncheckedException.withStackTrace with a message
         */

        assertThat(lines.get(1), startsWith("\tat "));
        assertThat(lines.get(1), containsString("UncheckedException.withStackTrace"));

        assertThat(lines.get(2), startsWith("\tat "));
        assertThat(lines.get(2), containsString("UncheckedException.withStackTrace"));

        assertThat(lines.get(3), startsWith("\tat "));
        assertThat(lines.get(3), containsString("UncheckedExceptionTest.testWithStackTrace"));

        int indexOfCause = indexOfCause(lines);
        assertThat(indexOfCause, greaterThanOrEqualTo(2));

        assertEquals("Caused by: " + cause.toString(), lines.get(indexOfCause));
    }

    @Test
    void testWithoutStackTrace() {
        IOException cause = new IOException();
        UncheckedException exception = UncheckedException.withoutStackTrace(cause);

        assertEquals(cause.toString(), exception.getMessage());

        List<String> lines = getStackTraceLines(exception);

        assertThat(lines, hasSize(greaterThan(2)));

        assertEquals(exception.toString(), lines.get(0));

        int indexOfCause = indexOfCause(lines);
        assertEquals(1, indexOfCause);

        assertEquals("Caused by: " + cause.toString(), lines.get(indexOfCause));

        assertThat(lines.get(indexOfCause + 1), startsWith("\tat "));
        assertThat(lines.get(indexOfCause + 1), containsString("UncheckedExceptionTest.testWithoutStackTrace"));
    }

    private List<String> getStackTraceLines(UncheckedException exception) {
        StringWriter writer = new StringWriter();
        try (PrintWriter printWriter = new PrintWriter(writer)) {
            exception.printStackTrace(printWriter);
        }
        return writer.toString()
                .lines()
                .collect(Collectors.toList());
    }

    private int indexOfCause(List<String> stackTraceLines) {
        int lineNumber = 0;
        for (String line : stackTraceLines) {
            if (line.startsWith("Caused by: ")) {
                return lineNumber;
            }
            lineNumber++;
        }
        return fail("No line starting with 'Caused by: ' found");
    }

    @Nested
    class ThrowCauseAs {

        @Test
        void testCauseInstanceOfErrorType() {
            IOException cause = new IOException();
            UncheckedException exception = UncheckedException.withoutStackTrace(cause);

            IOException thrown = assertThrows(IOException.class, () -> exception.throwCauseAs(IOException.class));
            assertSame(cause, thrown);
        }

        @Test
        void testCauseNotInstanceOfErrorType() {
            ParseException cause = new ParseException("", 0);
            UncheckedException exception = UncheckedException.withoutStackTrace(cause);

            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> exception.throwCauseAs(IOException.class));
            assertEquals("Unexpected exception thrown: " + cause, thrown.getMessage());
            assertSame(cause, thrown.getCause());
        }
    }

    @Nested
    class ThrowCauseAsOneOf {

        @Nested
        class OneOfTwo {

            @Test
            void testCauseInstanceOfFirstErrorType() {
                IOException cause = new IOException();
                UncheckedException exception = UncheckedException.withoutStackTrace(cause);

                IOException thrown = assertThrows(IOException.class, () -> exception.throwCauseAsOneOf(IOException.class, ParseException.class));
                assertSame(cause, thrown);
            }

            @Test
            void testCauseInstanceOfSecondErrorType() {
                ParseException cause = new ParseException("", 0);
                UncheckedException exception = UncheckedException.withoutStackTrace(cause);

                ParseException thrown = assertThrows(ParseException.class,
                        () -> exception.throwCauseAsOneOf(IOException.class, ParseException.class));
                assertSame(cause, thrown);
            }

            @Test
            void testCauseNotInstanceOfEitherErrorType() {
                IllegalArgumentException cause = new IllegalArgumentException();
                UncheckedException exception = UncheckedException.withoutStackTrace(cause);

                IllegalStateException thrown = assertThrows(IllegalStateException.class,
                        () -> exception.throwCauseAsOneOf(IOException.class, ParseException.class));
                assertEquals("Unexpected exception thrown: " + cause, thrown.getMessage());
                assertSame(cause, thrown.getCause());
            }
        }

        @Nested
        class OneOfThree {

            @Test
            void testCauseInstanceOfFirstErrorType() {
                IOException cause = new IOException();
                UncheckedException exception = UncheckedException.withoutStackTrace(cause);

                IOException thrown = assertThrows(IOException.class,
                        () -> exception.throwCauseAsOneOf(IOException.class, ParseException.class, IllegalArgumentException.class));
                assertSame(cause, thrown);
            }

            @Test
            void testCauseInstanceOfSecondErrorType() {
                ParseException cause = new ParseException("", 0);
                UncheckedException exception = UncheckedException.withoutStackTrace(cause);

                ParseException thrown = assertThrows(ParseException.class,
                        () -> exception.throwCauseAsOneOf(IOException.class, ParseException.class, IllegalArgumentException.class));
                assertSame(cause, thrown);
            }

            @Test
            void testCauseInstanceOfThirdErrorType() {
                IllegalArgumentException cause = new IllegalArgumentException();
                UncheckedException exception = UncheckedException.withoutStackTrace(cause);

                IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                        () -> exception.throwCauseAsOneOf(IOException.class, ParseException.class, IllegalArgumentException.class));
                assertSame(cause, thrown);
            }

            @Test
            void testCauseNotInstanceOfAnyErrorType() {
                NoSuchElementException cause = new NoSuchElementException();
                UncheckedException exception = UncheckedException.withoutStackTrace(cause);

                IllegalStateException thrown = assertThrows(IllegalStateException.class,
                        () -> exception.throwCauseAsOneOf(IOException.class, ParseException.class, IllegalArgumentException.class));
                assertEquals("Unexpected exception thrown: " + cause, thrown.getMessage());
                assertSame(cause, thrown.getCause());
            }
        }
    }
}
