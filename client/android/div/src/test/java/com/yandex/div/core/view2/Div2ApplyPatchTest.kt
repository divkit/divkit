package com.yandex.div.core.view2

import android.app.Activity
import com.yandex.div.DivDataTag
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivConfiguration
import com.yandex.div.core.images.DivImageLoader
import com.yandex.div.core.view2.divs.UnitTestData
import com.yandex.div.core.viewEquals
import com.yandex.div.internal.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

const val PATCH_DIR = "patches"

@RunWith(RobolectricTestRunner::class)
class Div2ApplyPatchTest {
    private val divImageLoader = mock<DivImageLoader>()
    private val activity = Robolectric.buildActivity(Activity::class.java).get()
    private val div2Context = Div2Context(
        activity,
        DivConfiguration.Builder(divImageLoader).build()
    )

    private val div2View = Div2View(div2Context)
    private val div2ReferenceView = Div2View(div2Context)
    private val tag = DivDataTag("tag")

    @Test
    fun `patch container transactional - success`() {
        testPatch(
            initial = UnitTestData("$PATCH_DIR/container", "container-initial.json"),
            expected = UnitTestData("$PATCH_DIR/container", "container-success-transactional.json"),
            patch = UnitTestData("$PATCH_DIR/container", "container-patch-transactional.json")
        )
    }

    @Test
    fun `patch container transactional - failed`() {
        testPatch(
            initial = UnitTestData("$PATCH_DIR/container", "container-initial.json"),
            expected = UnitTestData("$PATCH_DIR/container", "container-initial.json"),
            patch = UnitTestData("$PATCH_DIR/container", "container-broken-patch-transactional.json")
        )
    }

    @Test
    fun `patch container partial - success`() {
        testPatch(
            initial = UnitTestData("$PATCH_DIR/container", "container-initial.json"),
            expected = UnitTestData("$PATCH_DIR/container", "container-success-partial.json"),
            patch = UnitTestData("$PATCH_DIR/container", "container-patch-partial.json")
        )
    }

    @Test
    fun `patch grid transactional - success`() {
        testPatch(
            initial = UnitTestData("$PATCH_DIR/grid", "grid-initial.json"),
            expected = UnitTestData("$PATCH_DIR/grid", "grid-success-transactional.json"),
            patch = UnitTestData("$PATCH_DIR/grid", "grid-patch-transactional.json")
        )
    }

    @Test
    fun `patch grid transactional - failed`() {
        testPatch(
            initial = UnitTestData("$PATCH_DIR/grid", "grid-initial.json"),
            expected = UnitTestData("$PATCH_DIR/grid", "grid-initial.json"),
            patch = UnitTestData("$PATCH_DIR/grid", "grid-patch-transactional-broken.json")
        )
    }

    @Test
    fun `patch grid partial - success`() {
        testPatch(
            initial = UnitTestData("$PATCH_DIR/grid", "grid-initial.json"),
            expected = UnitTestData("$PATCH_DIR/grid", "grid-success-partial.json"),
            patch = UnitTestData("$PATCH_DIR/grid", "grid-patch-partial.json")
        )
    }

    @Test
    fun `patch gallery transactional - success`() {
        testPatch(
            initial = UnitTestData("$PATCH_DIR/gallery", "gallery-initial.json"),
            expected = UnitTestData("$PATCH_DIR/gallery", "gallery-success-transactional.json"),
            patch = UnitTestData("$PATCH_DIR/gallery", "gallery-patch-transactional.json")
        )
    }

    @Test
    fun `patch gallery transactional - failed`() {
        testPatch(
            initial = UnitTestData("$PATCH_DIR/gallery", "gallery-initial.json"),
            expected = UnitTestData("$PATCH_DIR/gallery", "gallery-initial.json"),
            patch = UnitTestData("$PATCH_DIR/gallery", "gallery-patch-transactional-broken.json")
        )
    }

    @Test
    fun `patch gallery partial - success`() {
        testPatch(
            initial = UnitTestData("$PATCH_DIR/gallery", "gallery-initial.json"),
            expected = UnitTestData("$PATCH_DIR/gallery", "gallery-success-partial.json"),
            patch = UnitTestData("$PATCH_DIR/gallery", "gallery-patch-partial.json")
        )
    }

    @Test
    fun `patch pager transactional - success`() {
        testPatch(
            initial = UnitTestData("$PATCH_DIR/pager", "pager-initial.json"),
            expected = UnitTestData("$PATCH_DIR/pager", "pager-success-transactional.json"),
            patch = UnitTestData("$PATCH_DIR/pager", "pager-patch-transactional.json")
        )
    }

    @Test
    fun `patch pager transactional - failed`() {
        testPatch(
            initial = UnitTestData("$PATCH_DIR/pager", "pager-initial.json"),
            expected = UnitTestData("$PATCH_DIR/pager", "pager-initial.json"),
            patch = UnitTestData("$PATCH_DIR/pager", "pager-patch-transactional-broken.json")
        )
    }

    @Test
    fun `patch pager partial - success`() {
        testPatch(
            initial = UnitTestData("$PATCH_DIR/pager", "pager-initial.json"),
            expected = UnitTestData("$PATCH_DIR/pager", "pager-success-partial.json"),
            patch = UnitTestData("$PATCH_DIR/pager", "pager-patch-partial.json")
        )
    }

    @Test
    fun `patch state transactional - success`() {
        testPatch(
            initial = UnitTestData("$PATCH_DIR/state", "state-initial.json"),
            expected = UnitTestData("$PATCH_DIR/state", "state-success-transactional.json"),
            patch = UnitTestData("$PATCH_DIR/state", "state-patch-transactional-success.json")
        )
    }

    @Test
    fun `patch state transactional - failed`() {
        testPatch(
            initial = UnitTestData("$PATCH_DIR/state", "state-initial.json"),
            expected = UnitTestData("$PATCH_DIR/state", "state-initial.json"),
            patch = UnitTestData("$PATCH_DIR/state", "state-patch-transactional-broken.json")
        )
    }

    @Test
    fun `patch state partial - success`() {
        testPatch(
            initial = UnitTestData("$PATCH_DIR/state", "state-initial.json"),
            expected = UnitTestData("$PATCH_DIR/state", "state-success-partial.json"),
            patch = UnitTestData("$PATCH_DIR/state", "state-patch-partial.json")
        )
    }

    @Test
    fun `patch tabs transactional - success`() {
        testPatch(
            initial = UnitTestData("$PATCH_DIR/tabs", "tabs-initial.json"),
            expected = UnitTestData("$PATCH_DIR/tabs", "tabs-success-transactional.json"),
            patch = UnitTestData("$PATCH_DIR/tabs", "tabs-patch-transactional-success.json")
        )
    }

    @Test
    fun `patch tabs transactional - failed`() {
        testPatch(
            initial = UnitTestData("$PATCH_DIR/tabs", "tabs-initial.json"),
            expected = UnitTestData("$PATCH_DIR/tabs", "tabs-initial.json"),
            patch = UnitTestData("$PATCH_DIR/tabs", "tabs-patch-transactional-broken.json")
        )
    }

    @Test
    fun `patch tabs partial - success`() {
        testPatch(
            initial = UnitTestData("$PATCH_DIR/tabs", "tabs-initial.json"),
            expected = UnitTestData("$PATCH_DIR/tabs", "tabs-success-partial.json"),
            patch = UnitTestData("$PATCH_DIR/tabs", "tabs-patch-partial.json")
        )
    }

    private fun testPatch(
        initial: UnitTestData,
        expected: UnitTestData,
        patch: UnitTestData
    ) {
        val oldData = initial.dataWithTemplates
        val referenceData = expected.dataWithTemplates
        div2View.setData(oldData, tag)
        div2ReferenceView.setData(referenceData, tag)
        val patchData = patch.patchWithTemplates
        div2View.applyPatch(patchData)

        div2View.bindOnAttachRunnable?.onAttach()
        div2ReferenceView.bindOnAttachRunnable?.onAttach()

        Assert.assertTrue(div2View.viewEquals(div2ReferenceView))
    }
}
