package com.yandex.div.backdrop.model

import org.json.JSONException

/**
 * Defines how far the backdrop lookup reaches.
 */
internal enum class BackdropScope {

    /**
     * The backdrop is looked up inside the card the decorated element belongs to. The default.
     */
    CARD,

    /**
     * The backdrop is looked up in the whole window the decorated element is drawn in.
     */
    WINDOW;

    companion object {

        /**
         * Anything but a known scope is a parsing error rather than a silent fallback to [CARD],
         * matching how the extension treats an unknown `rim_highlight.type`. The trade-off is that
         * a scope added in a future version drops the whole effect on clients that predate it.
         */
        @JvmStatic
        @Throws(JSONException::class)
        fun deserialize(value: String): BackdropScope {
            return when (value) {
                CARD_SCOPE -> CARD
                WINDOW_SCOPE -> WINDOW
                else -> throw JSONException("Unknown value for key 'backdrop_scope': $value")
            }
        }

        internal const val CARD_SCOPE = "card"
        private const val WINDOW_SCOPE = "window"
    }
}
