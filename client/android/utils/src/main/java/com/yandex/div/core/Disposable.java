package com.yandex.div.core;

import java.io.Closeable;

/**
 * It supposed to be a {@link AutoCloseable} but, we have to support up to API 16.
 *
 * In comparison with {@link Closeable} IOException is dropped from the {@link #close()} method.
 */
public interface Disposable extends Closeable {

    Disposable NULL = () -> { };

    /**
     * Closes this resource, relinquishing any underlying resources.
     * <p>
     * Implementation of this method must be idempotent.
     */
    void close();
}
