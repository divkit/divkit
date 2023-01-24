package com.yandex.div.core.timer

internal class InstantTestTicker(
    name: String,
    onStart: (Long) -> Unit,
    onPause: (Long) -> Unit,
    onEnd: (Long) -> Unit,
    onTick: (Long) -> Unit
): Ticker(name, onPause, onStart, onEnd, onTick, null) {
    private var isWorking = false

    private var mockedTime: Long = 0

    override val currentTime: Long
        get() = mockedTime

    override fun cleanTicker() {
        isWorking = false
    }

    override fun setupTimer(period: Long, initialDelay: Long, onTick: () -> Unit) {
        isWorking = true

        startedAt = mockedTime

        var isFirst = true

        while (isWorking) {
            mockedTime += if (isFirst) {
                isFirst = false

                initialDelay
            } else period

            onTick()
        }
    }
}
