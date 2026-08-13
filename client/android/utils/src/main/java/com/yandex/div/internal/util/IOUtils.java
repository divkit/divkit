package com.yandex.div.internal.util;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yandex.div.core.annotations.InternalApi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

@InternalApi
public final class IOUtils {

    private static final int BUFFER_SIZE = 2048;

    private IOUtils() {
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
