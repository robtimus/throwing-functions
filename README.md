# throwing-functions
[![Maven Central](https://img.shields.io/maven-central/v/com.github.robtimus/throwing-functions)](https://search.maven.org/artifact/com.github.robtimus/throwing-functions)
[![Build Status](https://github.com/robtimus/throwing-functions/actions/workflows/build.yml/badge.svg)](https://github.com/robtimus/throwing-functions/actions/workflows/build.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=com.github.robtimus%3Athrowing-functions&metric=alert_status)](https://sonarcloud.io/summary/overall?id=com.github.robtimus%3Athrowing-functions)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=com.github.robtimus%3Athrowing-functions&metric=coverage)](https://sonarcloud.io/summary/overall?id=com.github.robtimus%3Athrowing-functions)
[![Known Vulnerabilities](https://snyk.io/test/github/robtimus/throwing-functions/badge.svg)](https://snyk.io/test/github/robtimus/throwing-functions)

The `throwing-functions` library provides copies of the functional interfaces in [java.util.functions](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/function/package-summary.html) that allow their functional interface methods to throw checked exceptions.

Each of these interfaces also contains static methods `unchecked` and `checked` to convert them to and from their matching equivalents in `java.util.functions`. For example, to delete all files in a directory that match a filter, you can use [TheckedConsumer.unchecked](https://robtimus.github.io/throwing-functions/apidocs/com.github.robtimus.function.throwing/com/github/robtimus/function/throwing/ThrowingConsumer.html#unchecked\(com.github.robtimus.function.throwing.ThrowingConsumer\)):

```java
try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, filter)) {
    stream.forEach(unchecked(Files::delete));
} catch (UncheckedException e) {
    e.throwCauseAs(IOException.class);
}
```

## Handling checked exceptions

Each interface has a set of default methods that allow any thrown checked exception to be handled. These come in the following variants:

* `onErrorThrowAsChecked` and `onErrorThrowAsUnchecked` transform the caught checked exception into another exception.
* `onErrorHandleChecked` and `onErrorHandleUnchecked` invoke a function or action on the caught checked exception.
* `onError<Operation>Checked` and `onError<Operation>Unchecked` discard the caught checked exception and invoke an instance of the same interface or its unchecked variant.
* `onErrorGetChecked` and `onErrorGetUnchecked` discard the caught checked exception and return the result of a supplier (only for interfaces that don't return `void`).
* `onErrorReturn` discards the caught checked exception and returns a fixed value (only for interfaces that don't return `void`).
* `onErrorDiscard` discards the caught checked exception without doing anything else (only for interfaces that return `void`).
* `unchecked` wraps the caught checked exception in an instance of [UncheckedException](https://robtimus.github.io/throwing-functions/apidocs/com.github.robtimus.function.throwing/com/github/robtimus/function/throwing/UncheckedException.html). This is similar to calling the static `unchecked` method with the instance as argument.

The variants ending with `Checked` return an instance of the same interface but with a possibly different checked exception that can be thrown.
The variants ending with `Unchecked` return an instance of the matching unchecked variant.

Using these methods, the above example can be changed to throw an `UncheckedIOException` instead:

```java
try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, filter)) {
    stream.forEach(ThrowingConsumer.of(Files::delete).onErrorThrowAsUnchecked(UncheckedIOException::new));
} catch (UncheckedIOException e) {
    throw e.getCause();
}
```

It also becomes easy to log exceptions instead of letting them be relayed to the caller:

```java
try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, filter)) {
    stream.forEach(ThrowingConsumer.of(Files::delete).onErrorHandleUnchecked(e -> logger.info("Failed to delete a file", e)));
}
```

## Handling unchecked exceptions

Like the functional interfaces in `java.util.functions`, any thrown instance of `Error`, `RuntimeException` or one of their sub classes is relayed to the caller.
