package com.yandex.div.lint

import com.android.tools.lint.checks.infrastructure.LintDetectorTest
import com.android.tools.lint.checks.infrastructure.TestFile
import com.android.tools.lint.checks.infrastructure.TestMode
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Issue
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class OnPreDrawListenerDetectorTest : LintDetectorTest() {

    private val declarationFile: TestFile = kotlin(
        """
        package android.view

        class ViewTreeObserver {
            fun interface OnPreDrawListener {
                override fun onPreDraw(): Boolean
            }
        }
        """
    ).indented()

    override fun getDetector(): Detector = OnPreDrawListenerDetector()

    override fun getIssues(): List<Issue> = listOf(OnPreDrawListenerIssue.get())

    @Test
    fun `issue detected in implementation`() {
        val testFile = kotlin(
            """
            package test
    
            import android.view.ViewTreeObserver
            
            class OnPreDrawListenerImpl : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean = true
            }
    
            val onPreDrawListener = OnPreDrawListenerImpl()
            """
        ).indented()

        lint()
            .files(declarationFile, testFile)
            .allowMissingSdk()
            .run()
            .expect(
                """
                src/test/OnPreDrawListenerImpl.kt:5: Error: ViewTreeObserver.OnPreDrawListener is potentially unsafe
                Return of false from ViewTreeObserver.OnPreDrawListener.onPreDraw() may break the drawing of the entire screen.
                Replace it with com.yandex.div.core.view.OverridableOnPreDrawListener or com.yandex.div.core.view.OnPreDrawListeners.onPreDrawListener(...) function. [OnPreDrawListenerIssue]
                class OnPreDrawListenerImpl : ViewTreeObserver.OnPreDrawListener {
                                              ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                1 errors, 0 warnings
                """.trimIndent()
            )
    }

    @Test
    fun `issue detected in anonymous implementation`() {
        val testFile = kotlin(
            """
            package test
    
            import android.view.ViewTreeObserver
            
            val onPreDrawListener = object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean = true
            }
            """
        ).indented()

        lint()
            .files(declarationFile, testFile)
            .allowMissingSdk()
            .run()
            .expect(
                """
                src/test/test.kt:5: Error: ViewTreeObserver.OnPreDrawListener is potentially unsafe
                Return of false from ViewTreeObserver.OnPreDrawListener.onPreDraw() may break the drawing of the entire screen.
                Replace it with com.yandex.div.core.view.OverridableOnPreDrawListener or com.yandex.div.core.view.OnPreDrawListeners.onPreDrawListener(...) function. [OnPreDrawListenerIssue]
                val onPreDrawListener = object : ViewTreeObserver.OnPreDrawListener {
                                                 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                1 errors, 0 warnings
                """.trimIndent()
            )
    }

    @Test
    fun `issue does not detected in implicit implementation`() {
        val testFile = kotlin("""
            package test
    
            import android.view.ViewTreeObserver
            
            @Suppress("OnPreDrawListenerIssue")
            class OnPreDrawListenerImpl : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean = true
            }
    
            fun onPreDrawListener(): ViewTreeObserver.OnPreDrawListener {
                return OnPreDrawListenerImpl()
            }
            """
        ).indented()

        lint()
            .files(declarationFile, testFile)
            .allowMissingSdk()
            .skipTestModes(TestMode.SUPPRESSIBLE)
            .run()
            .expectClean()
    }

    @Test
    fun `issue detected in SAM constructor`() {
        val testFile = kotlin(
            """
            package test
    
            import android.view.ViewTreeObserver.OnPreDrawListener
            
            val onPreDrawListener = OnPreDrawListener { true }
            """
        ).indented()

        lint()
            .files(declarationFile, testFile)
            .allowMissingSdk()
            .skipTestModes(TestMode.SUPPRESSIBLE)
            .run()
            .expect(
                """
                src/test/test.kt:5: Error: ViewTreeObserver.OnPreDrawListener is potentially unsafe
                Return of false from ViewTreeObserver.OnPreDrawListener.onPreDraw() may break the drawing of the entire screen.
                Replace it with com.yandex.div.core.view.OverridableOnPreDrawListener or com.yandex.div.core.view.OnPreDrawListeners.onPreDrawListener(...) function. [OnPreDrawListenerIssue]
                val onPreDrawListener = OnPreDrawListener { true }
                                        ~~~~~~~~~~~~~~~~~
                1 errors, 0 warnings
                """.trimIndent()
            )
    }

    @Test
    fun `issue detected in qualified SAM constructor`() {
        val testFile = kotlin(
            """
            package test
    
            import android.view.ViewTreeObserver
            
            val onPreDrawListener = ViewTreeObserver.OnPreDrawListener { true }
            """
        ).indented()

        lint()
            .files(declarationFile, testFile)
            .allowMissingSdk()
            .skipTestModes(TestMode.SUPPRESSIBLE)
            .run()
            .expect(
                """
                src/test/test.kt:5: Error: ViewTreeObserver.OnPreDrawListener is potentially unsafe
                Return of false from ViewTreeObserver.OnPreDrawListener.onPreDraw() may break the drawing of the entire screen.
                Replace it with com.yandex.div.core.view.OverridableOnPreDrawListener or com.yandex.div.core.view.OnPreDrawListeners.onPreDrawListener(...) function. [OnPreDrawListenerIssue]
                val onPreDrawListener = ViewTreeObserver.OnPreDrawListener { true }
                                        ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                1 errors, 0 warnings
                """.trimIndent()
            )
    }

    @Test
    fun `issue detected in lambda SAM conversion`() {
        val testFile = kotlin(
            """
            package test
    
            import android.view.ViewTreeObserver
    
            class Wrapper(
                private val count: Int,
                private val listener: ViewTreeObserver.OnPreDrawListener
            )
            
            fun wrap(listener: ViewTreeObserver.OnPreDrawListener): Wrapper {
                return Wrapper(0, listener)
            }
    
            fun doWrapConstructorCall() {
                Wrapper(0, listener = { true })
            }
    
            fun doWrapMethodCall() {
                wrap(listener = { true })
            }
            """
        ).indented()

        lint()
            .files(declarationFile, testFile)
            .allowMissingSdk()
            .skipTestModes(TestMode.SUPPRESSIBLE, TestMode.SOURCE_TRANSFORMATION_GROUP)
            .run()
            .expect(
                """
                src/test/Wrapper.kt:15: Error: ViewTreeObserver.OnPreDrawListener is potentially unsafe
                Return of false from ViewTreeObserver.OnPreDrawListener.onPreDraw() may break the drawing of the entire screen.
                Replace it with com.yandex.div.core.view.OverridableOnPreDrawListener or com.yandex.div.core.view.OnPreDrawListeners.onPreDrawListener(...) function. [OnPreDrawListenerIssue]
                    Wrapper(0, listener = { true })
                                          ~~~~~~~~
                src/test/Wrapper.kt:19: Error: ViewTreeObserver.OnPreDrawListener is potentially unsafe
                Return of false from ViewTreeObserver.OnPreDrawListener.onPreDraw() may break the drawing of the entire screen.
                Replace it with com.yandex.div.core.view.OverridableOnPreDrawListener or com.yandex.div.core.view.OnPreDrawListeners.onPreDrawListener(...) function. [OnPreDrawListenerIssue]
                    wrap(listener = { true })
                                    ~~~~~~~~
                2 errors, 0 warnings
                """.trimIndent()
            )
    }

    @Test
    fun `issue detected in method reference SAM conversion`() {
        val testFile = kotlin(
            """
            package test
    
            import android.view.ViewTreeObserver
    
            class Wrapper(private val listener: ViewTreeObserver.OnPreDrawListener)
            
            fun wrap(listener: ViewTreeObserver.OnPreDrawListener): Wrapper {
                return Wrapper(listener)
            }
    
            fun onPreDraw(): Boolean = true
    
            fun doWrapConstructorCall() {
                Wrapper(listener = ::onPreDraw)
            }
    
            fun doWrapMethodCall() {
                wrap(listener = ::onPreDraw)
            }
            """
        ).indented()

        lint()
            .files(declarationFile, testFile)
            .allowMissingSdk()
            .skipTestModes(TestMode.SUPPRESSIBLE, TestMode.SOURCE_TRANSFORMATION_GROUP)
            .run()
            .expect(
                """
                src/test/Wrapper.kt:14: Error: ViewTreeObserver.OnPreDrawListener is potentially unsafe
                Return of false from ViewTreeObserver.OnPreDrawListener.onPreDraw() may break the drawing of the entire screen.
                Replace it with com.yandex.div.core.view.OverridableOnPreDrawListener or com.yandex.div.core.view.OnPreDrawListeners.onPreDrawListener(...) function. [OnPreDrawListenerIssue]
                    Wrapper(listener = ::onPreDraw)
                                       ~~~~~~~~~~~
                src/test/Wrapper.kt:18: Error: ViewTreeObserver.OnPreDrawListener is potentially unsafe
                Return of false from ViewTreeObserver.OnPreDrawListener.onPreDraw() may break the drawing of the entire screen.
                Replace it with com.yandex.div.core.view.OverridableOnPreDrawListener or com.yandex.div.core.view.OnPreDrawListeners.onPreDrawListener(...) function. [OnPreDrawListenerIssue]
                    wrap(listener = ::onPreDraw)
                                    ~~~~~~~~~~~
                2 errors, 0 warnings
                """.trimIndent()
            )
    }
}
