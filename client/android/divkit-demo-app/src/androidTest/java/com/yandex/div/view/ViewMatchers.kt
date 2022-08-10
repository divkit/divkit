package com.yandex.div.view

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Root
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.Visibility
import androidx.test.espresso.matcher.ViewMatchers.isClickable
import androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isDisplayingAtLeast
import androidx.test.espresso.matcher.ViewMatchers.withClassName
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withHint
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.yandex.test.matcher.TextViewMatchers.withIcon
import com.yandex.test.util.Drawable.checkBitmap
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.anyOf
import org.hamcrest.Matchers.`is`
import org.hamcrest.TypeSafeMatcher
import java.util.Stack

/**
 * Overrides espresso's default requirement for the view to be displayed at 90%
 */
object ViewMatchers {

    fun withIdAndText(@IdRes id: Int, @StringRes text: Int): Matcher<View> = allOf(withId(id), withText(text))

    fun withIdAndText(@IdRes id: Int, text: String): Matcher<View> = allOf(withId(id), withText(text))

    fun withIdAndTextAndIcon(@IdRes id: Int, @StringRes text: Int, @DrawableRes icon: Int): Matcher<View> =
        allOf(withId(id), withText(text), withIcon(icon))

    fun withIdAndTextAndVisible(@IdRes id: Int, @StringRes text: Int): Matcher<View> =
        allOf(withId(id), withText(text), withEffectiveVisibility(Visibility.VISIBLE))

    fun withIdAndTextAndVisible(@IdRes id: Int, text: String): Matcher<View> =
        allOf(withId(id), withText(text), withEffectiveVisibility(Visibility.VISIBLE))

    fun withIdAndDescAndVisible(@IdRes id: Int, @StringRes desc: Int): Matcher<View> =
        allOf(withId(id), withContentDescription(desc), withEffectiveVisibility(Visibility.VISIBLE))

    fun withIdAndDrawable(@IdRes id: Int, @DrawableRes drawable: Int, position: DrawablePosition): Matcher<View> =
        allOf(withId(id), withImageViewDrawable(drawable, position))

    fun withIdAndDrawableWithTint(
        @IdRes id: Int,
        @DrawableRes drawable: Int,
        @ColorRes tint: Int,
        position: DrawablePosition
    ): Matcher<View> =
        allOf(withId(id), withTintImageDrawable(drawable, tint, position))

    fun withIdAndClickable(@IdRes id: Int): Matcher<View> = allOf(withId(id), isClickable())

    fun withIdAndHintAndClickable(@IdRes id: Int, @StringRes hint: Int): Matcher<View> =
        allOf(withId(id), withHint(hint), isClickable())

    fun withIdAndVisible(@IdRes id: Int): Matcher<View> = allOf(withId(id), withEffectiveVisibility(Visibility.VISIBLE))

    fun withIdAndGone(@IdRes id: Int): Matcher<View> = allOf(withId(id), withEffectiveVisibility(Visibility.GONE))

    fun withIdAndCompletelyVisible(@IdRes id: Int) = allOf(withId(id), isCompletelyDisplayed())

    fun isDisplayedForClicking(): Matcher<View> = isDisplayingAtLeast(50)

    // resourceId == 0 means any drawable
    fun withDrawable(@DrawableRes resourceId: Int = 0): Matcher<View> = drawableIsCorrect(resourceId)

    fun noDrawable(): Matcher<View> = drawableIsCorrect(-1)

    private fun drawableIsCorrect(@DrawableRes expectedId: Int): Matcher<View> {
        return object : TypeSafeMatcher<View>() {

            private var resourceName: String? = null

            override fun describeTo(description: Description) {
                description.appendText("Matches with drawable from resource id: ")
                description.appendValue(expectedId)
                description.appendText("[$resourceName]")
            }

            @SuppressLint("ResourceType")
            override fun matchesSafely(target: View?): Boolean {
                if (target !is ImageView) {
                    return false
                }
                if (expectedId == 0) {
                    return target.drawable != null
                }
                if (expectedId < 0) {
                    return target.drawable == null
                }

                val expectedDrawable = ContextCompat.getDrawable(target.context, expectedId)
                    ?: return false
                resourceName = target.context.resources.getResourceEntryName(expectedId)

                return if (expectedDrawable.constantState != null) {
                    expectedDrawable.constantState == target.drawable.constantState
                } else {
                    false
                }
            }
        }
    }

    fun withDrawable(matcher: Matcher<Drawable>): Matcher<View> {
        return object : TypeSafeMatcher<View>() {

            override fun describeTo(description: Description) {
                description.appendText("Matches with drawable: ")
                matcher.describeTo(description)
            }

            @SuppressLint("ResourceType")
            override fun matchesSafely(target: View?): Boolean {
                if (target !is ImageView) {
                    return false
                }
                return matcher.matches(target.drawable)
            }
        }
    }

    fun atPosition(position: Int, itemMatcher: Matcher<View>): Matcher<View> {
        return object : BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {

            override fun describeTo(description: Description) {
                description.appendText("has item at position $position: ")
                itemMatcher.describeTo(description)
            }

            override fun matchesSafely(view: RecyclerView): Boolean {
                val viewHolder = view.findViewHolderForAdapterPosition(position) ?: return false
                return itemMatcher.matches(viewHolder.itemView)
            }
        }
    }

    fun canScrollUp(): Matcher<View> = canScrollVertically(-1)
    fun canScrollDown(): Matcher<View> = canScrollVertically(1)
    fun canScrollVertically(): Matcher<View> = anyOf(canScrollUp(), canScrollDown())

    fun canScrollVertically(direction: Int): Matcher<View> {
        return object : BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {

            override fun describeTo(description: Description) {
                description.appendText("scrollable with direction $direction: ")
            }

            override fun matchesSafely(view: RecyclerView): Boolean {
                return view.canScrollVertically(direction)
            }
        }
    }

    /**
     * Matches view with #index if multiple views in hierarchy found
     */
    fun withIndex(matcher: Matcher<View>, index: Int): Matcher<View> {
        return object : TypeSafeMatcher<View>() {

            var currentIndex = 0
            var viewObjHash = 0

            override fun describeTo(description: Description) {
                description.appendText(" with view index: $index, ")
                matcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                if (matcher.matches(view) && currentIndex++ == index) {
                    viewObjHash = view.hashCode()
                }
                return view.hashCode() == viewObjHash
            }
        }
    }

    /**
     * Matches view with #index with parent matcher
     */
    fun withChildAt(matcher: Matcher<View>, index: Int): Matcher<View> {
        return object : BoundedMatcher<View, View>(View::class.java) {

            override fun describeTo(description: Description) {
                description.appendText(" with child at index: $index, ")
                matcher.describeTo(description)
            }

            override fun matchesSafely(view: View): Boolean {
                val parentView = view.parent
                return if (parentView is ViewGroup) {
                    matcher.matches(view.parent) && parentView.getChildAt(index) == view
                } else false
            }
        }
    }

    fun withChildDeep(childMatcher: Matcher<View>) = object : TypeSafeMatcher<View>() {

        override fun describeTo(description: Description) {
            description.appendText("has child: ")
            childMatcher.describeTo(description)
        }

        public override fun matchesSafely(view: View?): Boolean {
            if (view !is ViewGroup) {
                return false
            }
            return findChildDeep(view, childMatcher) != null
        }
    }

    fun findChildDeep(parent: View, childMatcher: Matcher<View>): View? {
        val children = Stack<View>()
        children.push(parent)
        while (children.isNotEmpty()) {
            val child = children.pop()
            if (childMatcher.matches(child)) {
                return child
            }
            if (child is ViewGroup) {
                child.children.forEach { children.push(it) }
            }
        }
        return null
    }

    enum class DrawablePosition {
        LEFT,
        TOP,
        RIGHT,
        BOTTOM
    }

    /**
     * Matcher for drawable in ImageView, TextView(left) or View
     * */
    fun withImageViewDrawable(
        @DrawableRes drawableId: Int,
        position: DrawablePosition
    ): Matcher<View> {
        return object : TypeSafeMatcher<View>() {

            override fun describeTo(description: Description) {
                description.appendText("has image drawable resource $drawableId")
            }

            override fun matchesSafely(view: View): Boolean {
                return when (view) {
                    is ImageView -> checkBitmap(view.context, view.drawable, drawableId)
                    is TextView -> checkBitmap(view.context, view.compoundDrawables[position.ordinal], drawableId)
                    else -> checkBitmap(view.context, view.background, drawableId)
                }
            }
        }
    }

    /**
     * Matcher for drawable in ImageView, TextView(left) or View with applied tint
     * */
    fun withTintImageDrawable(
        @DrawableRes drawableId: Int,
        @ColorRes tint: Int,
        position: DrawablePosition
    ): Matcher<View> {
        return object : TypeSafeMatcher<View>() {

            override fun describeTo(description: Description) {
                description.appendText("has image drawable resource $drawableId with tint $tint")
            }

            override fun matchesSafely(view: View): Boolean {
                return when (view) {
                    is ImageView -> checkBitmap(view.context, view.drawable, drawableId, tint)
                    is TextView -> checkBitmap(view.context, view.compoundDrawables[position.ordinal], drawableId, tint)
                    else -> checkBitmap(view.context, view.background, drawableId, tint)
                }
            }
        }
    }
}

class IsPlatformPopupApi21 : TypeSafeMatcher<Root>() {

    override fun describeTo(description: Description) {
        description.appendText("with decor view of type PopupWindow\$FrameLayout")
    }

    override fun matchesSafely(item: Root): Boolean {
        val popupClassName = "android.widget.FrameLayout"
        return withDecorView(withClassName(`is`(popupClassName))).matches(item)
    }
}
