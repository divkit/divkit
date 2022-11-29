package com.yandex.div.internal.util;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.collection.ArrayMap;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Contains utility methods to check and request permissions.
 */
public class PermissionUtils {

    private static final String PREFS_NAME = "PermissionUtils.Prefs";
    private static final String PREFS_KEY_ANSWERED_PERMISSION_SET = "prefs_key_answered_permission_set";

    private PermissionUtils() {
    }

    /**
     * Checks if passed permission is granted
     */
    public static boolean hasPermission(@NonNull Context context, @NonNull String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Requests permissions to the application.
     */
    public static void requestPermissions(@NonNull Activity activity,
                                          int requestCode,
                                          @NonNull String[] permissions) {
        ActivityCompat.requestPermissions(activity, permissions, requestCode);
    }

    /**
     * Returns easy readable grant results.
     *
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link PackageManager#PERMISSION_GRANTED}
     *                     or {@link PackageManager#PERMISSION_DENIED}. Never null.
     * @return true if granted else false
     */
    @NonNull
    public static GrantResults parseGrantResults(@NonNull String[] permissions, @NonNull int[] grantResults) {
        Map<String, Boolean> resultMap = new ArrayMap<>();

        // From docs: It is possible that the permissions request interaction with the user is interrupted.
        // In this case you will receive empty permissions and results arrays which should be treated as a cancellation.
        if (grantResults.length == 0 || permissions.length != grantResults.length) {
            return new GrantResults(resultMap);
        }

        for (int i = 0; i < permissions.length; i++) {
            String permission = permissions[i];
            int grantResult = grantResults[i];

            boolean isGranted = (grantResult == PackageManager.PERMISSION_GRANTED);
            resultMap.put(permission, isGranted);
        }
        return new GrantResults(resultMap);
    }

    /**
     * Note that {@link #markUserAnswered} must be called in order to get correct results.
     *
     * @see #markUserAnswered
     */
    public static boolean isDeniedWithDontAsk(@NonNull Activity activity, @NonNull String permission) {
        return !hasPermission(activity, permission) &&
                !shouldShowRequestPermissionRationale(activity, Collections.singleton(permission)) &&
                userEverAnswered(activity, permission);
    }

    /**
     * Must be called from {@link Activity#onRequestPermissionsResult}/{@link Fragment#onRequestPermissionsResult}.
     *
     * This class must know whether the user has answered for the permission ask or not.
     * this knowledge is required for checking 'Don't ask' state.
     * When no permission granted, {@link #shouldShowRequestPermissionRationale} gives:
     * 1) False for the first dialog launch (until the user presses any button on a dialog),
     * 2) True for the sequential asks,
     * 3) False after the dialog blocked with 'Don't ask'.
     * Thus to distinguish 'first launch' and 'Don't ask' blocked state, we need to store
     * an additional state by ourselves.
     *
     * Note also that changing a permission in app settings manually does NOT resets
     * {@link #shouldShowRequestPermissionRationale} method behavior to the 'first launch' state
     * thus this settings must outlive manual permission change.
     *
     * @see #isDeniedWithDontAsk
     */
    public static void markUserAnswered(@NonNull Context context, @NonNull String[] permissions) {
        Set<String> answeredPermissions = new HashSet<>(getAnsweredPermissions(context));
        for (String permission : permissions) {
            if (answeredPermissions.contains(permission)) {
                continue;
            }
            answeredPermissions.add(permission);
        }
        context.getSharedPreferences(PREFS_NAME, 0).edit()
                .putStringSet(PREFS_KEY_ANSWERED_PERMISSION_SET, answeredPermissions)
                .apply();
    }

    public static boolean userEverAnswered(@NonNull Context context, @NonNull String permission) {
        return getAnsweredPermissions(context).contains(permission);
    }

    private static Set<String> getAnsweredPermissions(@NonNull Context context) {
        return context.getSharedPreferences(PREFS_NAME, 0)
                .getStringSet(PREFS_KEY_ANSWERED_PERMISSION_SET, Collections.EMPTY_SET);
    }

    public static class GrantResults {
        @NonNull
        private final Map<String, Boolean> mResultMap;

        GrantResults(@NonNull Map<String, Boolean> resultMap) {
            mResultMap = resultMap;
        }

        public boolean contains(@NonNull String permission) {
            return mResultMap.containsKey(permission);
        }

        public boolean isPermissionGranted(@NonNull String permission) {
            if (!mResultMap.containsKey(permission)) {
                return false;
            }
            return mResultMap.get(permission);
        }

        public boolean isDeniedWithDontAsk(@NonNull Activity activity, @NonNull String permission) {
            return !isPermissionGranted(permission) &&
                    !shouldShowRequestPermissionRationale(activity, Collections.singleton(permission)) &&
                    userEverAnswered(activity, permission);
        }

        public boolean areAllPermissionsGranted() {
            if (mResultMap.isEmpty()) {
                return false;
            }

            for (boolean isGranted : mResultMap.values()) {
                if (!isGranted) return false;
            }
            return true;
        }

        public boolean isAnyPermissionDeniedWithDontAsk(@NonNull Activity activity) {
            for (Map.Entry<String, Boolean> entry : mResultMap.entrySet()) {
                Boolean isGranted = entry.getValue();
                if (!isGranted &&
                        !shouldShowRequestPermissionRationale(activity, Collections.singleton(entry.getKey()))) {
                    return true;
                }
            }
            return false;
        }

        public Collection<String> allPermissions() {
            return mResultMap.keySet();
        }
    }

    /**
     * @return True if at least for one permission {@link ActivityCompat#shouldShowRequestPermissionRationale}
     * returns True.
     */
    public static boolean shouldShowRequestPermissionRationale(@NonNull Activity activity,
                                                               @NonNull Iterable<String> permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Call it when permission is blocked
     */
    public static void openSettings(@NonNull Context context) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                .setData(Uri.fromParts("package", context.getPackageName(), null))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
