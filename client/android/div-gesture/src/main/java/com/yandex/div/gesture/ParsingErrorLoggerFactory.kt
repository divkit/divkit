package com.yandex.div.gesture

import com.yandex.div.json.ParsingErrorLogger

/**
 * Creates [ParsingErrorLogger]'s instance that may provide more detailed information
 * about parsing errors that may occur during reading [DivAction]s list.
 */
public interface ParsingErrorLoggerFactory {
    /**
     * Creates [ParsingErrorLogger]'s instance using given [key].
     */
    public fun create(key: String): ParsingErrorLogger
}
