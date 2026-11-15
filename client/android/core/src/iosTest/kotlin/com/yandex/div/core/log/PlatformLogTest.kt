package com.yandex.div.core.log

import kotlinx.cinterop.BetaInteropApi
import platform.Foundation.NSPipe
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.closeFile
import platform.Foundation.create
import platform.Foundation.fileDescriptor
import platform.Foundation.readDataToEndOfFile
import platform.posix.STDERR_FILENO
import platform.posix.close
import platform.posix.dup
import platform.posix.dup2
import kotlin.test.Test
import kotlin.test.assertContains

@OptIn(BetaInteropApi::class)
class PlatformLogTest {

    @Test
    fun `logging preserves format placeholders and Unicode in user text`() {
        val pipe = NSPipe()
        val originalStderr = dup(STDERR_FILENO)
        try {
            dup2(pipe.fileHandleForWriting.fileDescriptor, STDERR_FILENO)

            PlatformLog.e("tag %@", "100% %@ %s Привет", IllegalStateException("cause %@"))
        } finally {
            dup2(originalStderr, STDERR_FILENO)
            close(originalStderr)
            pipe.fileHandleForWriting.closeFile()
        }
        val output = NSString.create(
            data = pipe.fileHandleForReading.readDataToEndOfFile(),
            encoding = NSUTF8StringEncoding,
        ).toString()
        pipe.fileHandleForReading.closeFile()

        assertContains(output, "E/tag %@: 100% %@ %s Привет")
        assertContains(output, "IllegalStateException: cause %@")
    }
}
