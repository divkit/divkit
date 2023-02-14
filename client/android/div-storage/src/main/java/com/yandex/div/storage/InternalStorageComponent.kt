package com.yandex.div.storage

/**
 * Internal version of [DivStorageComponent] that is used for integration testing.
 */
internal class InternalStorageComponent(
        override val repository: DivDataRepository,
        val storage: DivStorage,
) : DivStorageComponent
