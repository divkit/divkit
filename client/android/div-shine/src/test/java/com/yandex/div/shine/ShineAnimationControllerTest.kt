package com.yandex.div.shine

import android.view.View
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import java.lang.ref.WeakReference
import kotlin.test.assertEquals

class ShineAnimationControllerTest {

    private var isAnimationPaused = false
    private val transformer = mock<ShineImageTransformer> {
        on { pauseAnimation() } doAnswer { isAnimationPaused = true }
        on { resumeAnimation() } doAnswer { isAnimationPaused = false }
    }

    // Use custom setters to automatically call the appropriate methods when these properties change
    private var _isViewVisible = true
    private var isViewVisible: Boolean
        get() = _isViewVisible
        set(value) {
            if (_isViewVisible != value) {
                _isViewVisible = value
                val visibility = if (value) View.VISIBLE else View.INVISIBLE

                assertEquals(view.isShown, _isViewVisible)

                shineAnimationController.onVisibilityChanged(view, visibility)
            }
        }
    
    private var _isViewAttachedToWindow = true
    private var isViewAttachedToWindow: Boolean
        get() = _isViewAttachedToWindow
        set(value) {
            if (_isViewAttachedToWindow != value) {
                _isViewAttachedToWindow = value
                if (value) {
                    shineAnimationController.onAttachedToWindow()
                } else {
                    shineAnimationController.onDetachedFromWindow()
                }
            }
        }
    
    private val view = mock<View> {
        on { isShown } doAnswer  { _isViewVisible }
        on { isAttachedToWindow } doAnswer  { _isViewAttachedToWindow }
    }

    private val shineAnimationController = ShineAnimationController(
        transformer = transformer,
        viewReference = WeakReference(view)
    )
    
    @Before
    fun setup() {
        // Reset state before each test
        isAnimationPaused = false

        // Reset view state without triggering the setters
        _isViewVisible = true
        _isViewAttachedToWindow = true
    }

    // Initialization Tests

    @Test
    fun `initial state is not paused`() {
        assert(!shineAnimationController.isPaused)
    }

    // Pause State Tests

    @Test
    fun `isPaused returns true when paused by observer`() {
        shineAnimationController.onPause()
        assert(shineAnimationController.isPaused)
    }

    @Test
    fun `isPaused returns true when paused by delegate`() {
        isViewVisible = false
        assert(shineAnimationController.isPaused)
    }

    @Test
    fun `isPaused returns false when not paused by either source`() {
        isViewVisible = true
        isViewAttachedToWindow = true
        shineAnimationController.onResume()
        assert(!shineAnimationController.isPaused)
    }

    // Observer Pause/Resume Tests

    @Test
    fun `onPause sets isPausedByObserver to true and pauses animation`() {
        shineAnimationController.onPause()
        verify(transformer).pauseAnimation()
    }

    @Test
    fun `onResume sets isPausedByObserver to false and resumes animation when not paused by delegate`() {
        isViewVisible = true
        isViewAttachedToWindow = true
        
        shineAnimationController.onPause()
        verify(transformer).pauseAnimation()
        
        shineAnimationController.onResume()
        verify(transformer).resumeAnimation()
    }

    @Test
    fun `onResume does not resume animation when still paused by delegate`() {
        isViewVisible = false
        
        shineAnimationController.onPause()
        shineAnimationController.onResume()
        verify(transformer, never()).resumeAnimation()
    }

    // Delegate Pause Tests

    @Test
    fun `invalidateAnimationPauseByDelegate sets isPausedByDelegate to true when view is not shown`() {
        isViewVisible = false
        verify(transformer).pauseAnimation()
    }

    @Test
    fun `invalidateAnimationPauseByDelegate sets isPausedByDelegate to true when view is not attached`() {
        isViewAttachedToWindow = false
        verify(transformer).pauseAnimation()
    }

    @Test
    fun `invalidateAnimationPauseByDelegate sets isPausedByDelegate to false when view is shown and attached`() {
        // Both are true by default, so we'll toggle them and then set them back
        isViewVisible = false
        verify(transformer).pauseAnimation()
        
        isViewVisible = true
        verify(transformer).resumeAnimation()
    }

    // View Lifecycle Tests

    @Test
    fun `onVisibilityChanged calls invalidateAnimationPauseByDelegate`() {
        // This is now tested implicitly by the property setter
        isViewVisible = false
        verify(transformer).pauseAnimation()
        
        isViewVisible = true
        verify(transformer).resumeAnimation()
    }

    @Test
    fun `onAttachedToWindow calls invalidateAnimationPauseByDelegate`() {
        // This is now tested implicitly by the property setter
        isViewAttachedToWindow = false
        verify(transformer).pauseAnimation()
        
        isViewAttachedToWindow = true
        verify(transformer).resumeAnimation()
    }

    @Test
    fun `onDetachedFromWindow calls invalidateAnimationPauseByDelegate`() {
        isViewAttachedToWindow = false
        verify(transformer).pauseAnimation()
    }

    // Resource Cleanup Tests

    @Test
    fun `transformer clear is called when view reference is null`() {
        val controllerWithNullView = ShineAnimationController(transformer, WeakReference(null))
        
        // This should trigger the null check and call clear
        controllerWithNullView.onAttachedToWindow()
        
        verify(transformer).clear()
    }

    // Edge Cases

    @Test
    fun `pause from both sources requires both to be resumed to resume animation`() {
        isViewVisible = false
        verify(transformer).pauseAnimation()
        
        // Pause from observer
        shineAnimationController.onPause()
        
        // Resume from observer only - should not resume since still paused by delegate
        shineAnimationController.onResume()
        verify(transformer, never()).resumeAnimation()
        assert(isAnimationPaused) // Should still be paused
        
        // Resume from delegate too - now should resume
        isViewVisible = true
        verify(transformer).resumeAnimation()
        assert(!isAnimationPaused) // Should be updated by resumeAnimation()
    }
    
}
