/**
 * [Toast] helpers.
 */

@file:Suppress("NOTHING_TO_INLINE", "unused")
package com.yandex.divkit.demo.utils

import android.content.Context
import android.widget.Toast

/**
 * Display the simple Toast message with the [Toast.LENGTH_SHORT] duration.
 *
 * @param message the message text resource.
 */
inline fun Context.showToast(message: Int) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

/**
 * Display the simple Toast message with the [Toast.LENGTH_SHORT] duration.
 *
 * @param message the message text.
 */
inline fun Context.showToast(message: CharSequence) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

/**
 * Display the simple Toast message with the [Toast.LENGTH_LONG] duration.
 *
 * @param message the message text resource.
 */
inline fun Context.longToast(message: Int) = Toast.makeText(this, message, Toast.LENGTH_LONG).show()

/**
 * Display the simple Toast message with the [Toast.LENGTH_LONG] duration.
 *
 * @param message the message text.
 */
inline fun Context.longToast(message: CharSequence) = Toast.makeText(this, message, Toast.LENGTH_LONG).show()
