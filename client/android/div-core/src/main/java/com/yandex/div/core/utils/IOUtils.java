package com.yandex.div.core.utils;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;

public final class IOUtils {

    private static final int BUFFER_SIZE = 2048;

    private IOUtils() {
    }

    /**
     * Closes given closable without any side effects
     *
     * NOTE: Don't pass {@link Cursor} to this method, because some
     * devices may throw {@link IncompatibleClassChangeError}
     *
     * @param closeable closeable for close
     */
    public static void closeSilently(@Nullable Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException ignore) {
        }
    }

    /**
     * Don't use closeSilently(Closable) because of ClasCastException on some devices
     */
    public static void closeCursorSilently(@Nullable Cursor cursor) {
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
    }

    public static void flushSilently(@Nullable OutputStream outputStream) {
        try {
            if (outputStream != null) {
                outputStream.flush();
            }
        } catch (IOException ignore) {
        }
    }

    @Nullable
    public static byte[] toByteArray(@Nullable InputStream inputStream) throws IOException {
        if (inputStream == null) {
            return null;
        }

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final byte[] buffer = new byte[BUFFER_SIZE];
        int read;
        while ((read = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, read);
        }

        return outputStream.toByteArray();
    }

    @NonNull
    public static String toString(@NonNull InputStream inputStream) throws IOException {
        char[] buffer = new char[BUFFER_SIZE];
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder builder = new StringBuilder(inputStream.available());
        int read;
        while ((read = reader.read(buffer)) != -1) {
            builder.append(buffer, 0, read);
        }
        return builder.toString();
    }

    @NonNull
    public static String toString(@NonNull InputStream inputStream, @NonNull Charset charset) throws IOException {
        char[] buffer = new char[BUFFER_SIZE];
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, charset));
        StringBuilder builder = new StringBuilder(inputStream.available());
        int read;
        while ((read = reader.read(buffer)) != -1) {
            builder.append(buffer, 0, read);
        }
        return builder.toString();
    }
}
