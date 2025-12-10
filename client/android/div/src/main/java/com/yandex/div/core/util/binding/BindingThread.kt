package com.yandex.div.core.util.binding

/**
 * Annotated object should be accessed only from [com.yandex.div.core.util.binding.BindingDispatcher]'s synchronization lock,
 * which guarantees that it will not be accessed concurrently from multiple threads.
 */
internal annotation class BindingThread
