package com.yandex.div.core.view2

import android.app.Activity
import com.yandex.div.DivDataTag
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivConfiguration
import com.yandex.div.core.images.DivImageLoader
import com.yandex.div.core.util.Assert
import com.yandex.div.core.view2.divs.UnitTestData
import com.yandex.div.core.viewEquals
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
        val oldData = UnitTestData("$PATCH_DIR/container", "container-initial.json").dataWithTemplates
        val referenceData = UnitTestData("$PATCH_DIR/container", "container-success-transactional.json").dataWithTemplates
        div2View.setData(oldData, tag)
        div2ReferenceView.setData(referenceData, tag)
        val patch = UnitTestData("$PATCH_DIR/container", "container-patch-transactional.json").patchWithTemplates
        div2View.applyPatch(patch)

        Assert.assertTrue(div2View.viewEquals(div2ReferenceView))
    }

    @Test
    fun `patch container transactional - failed`() {
        val oldData = UnitTestData("$PATCH_DIR/container", "container-initial.json").dataWithTemplates
        // reference is the same
        val referenceData = UnitTestData("$PATCH_DIR/container", "container-initial.json").dataWithTemplates
        div2View.setData(oldData, tag)
        div2ReferenceView.setData(referenceData, tag)
        val patch = UnitTestData("$PATCH_DIR/container", "container-broken-patch-transactional.json").patchWithTemplates
        div2View.applyPatch(patch)

        Assert.assertTrue(div2View.viewEquals(div2ReferenceView))
    }

    @Test
    fun `patch container partial - success`() {
        val oldData = UnitTestData("$PATCH_DIR/container", "container-initial.json").dataWithTemplates
        val referenceData = UnitTestData("$PATCH_DIR/container", "container-success-partial.json").dataWithTemplates
        div2View.setData(oldData, tag)
        div2ReferenceView.setData(referenceData, tag)
        val patch = UnitTestData("$PATCH_DIR/container", "container-patch-partial.json").patchWithTemplates
        div2View.applyPatch(patch)

        Assert.assertTrue(div2View.viewEquals(div2ReferenceView))
    }

    @Test
    fun `patch grid transactional - success`() {
        val oldData = UnitTestData("$PATCH_DIR/grid", "grid-initial.json").dataWithTemplates
        val referenceData = UnitTestData("$PATCH_DIR/grid", "grid-success-transactional.json").dataWithTemplates
        div2View.setData(oldData, tag)
        div2ReferenceView.setData(referenceData, tag)
        val patch = UnitTestData("$PATCH_DIR/grid", "grid-patch-transactional.json").patchWithTemplates
        div2View.applyPatch(patch)

        Assert.assertTrue(div2View.viewEquals(div2ReferenceView))
    }

    @Test
    fun `patch grid transactional - failed`() {
        val oldData = UnitTestData("$PATCH_DIR/grid", "grid-initial.json").dataWithTemplates
        // reference is the same
        val referenceData = UnitTestData("$PATCH_DIR/grid", "grid-initial.json").dataWithTemplates
        div2View.setData(oldData, tag)
        div2ReferenceView.setData(referenceData, tag)
        val patch = UnitTestData("$PATCH_DIR/grid", "grid-patch-transactional-broken.json").patchWithTemplates
        div2View.applyPatch(patch)

        Assert.assertTrue(div2View.viewEquals(div2ReferenceView))
    }

    @Test
    fun `patch grid partial - success`() {
        val oldData = UnitTestData("$PATCH_DIR/grid", "grid-initial.json").dataWithTemplates
        val referenceData = UnitTestData("$PATCH_DIR/grid", "grid-success-partial.json").dataWithTemplates
        div2View.setData(oldData, tag)
        div2ReferenceView.setData(referenceData, tag)
        val patch = UnitTestData("$PATCH_DIR/grid", "grid-patch-partial.json").patchWithTemplates
        div2View.applyPatch(patch)

        Assert.assertTrue(div2View.viewEquals(div2ReferenceView))
    }

    @Test
    fun `patch gallery transactional - success`() {
        val oldData = UnitTestData("$PATCH_DIR/gallery", "gallery-initial.json").dataWithTemplates
        val referenceData = UnitTestData("$PATCH_DIR/gallery", "gallery-success-transactional.json").dataWithTemplates
        div2View.setData(oldData, tag)
        div2ReferenceView.setData(referenceData, tag)
        val patch = UnitTestData("$PATCH_DIR/gallery", "gallery-patch-transactional.json").patchWithTemplates
        div2View.applyPatch(patch)

        Assert.assertTrue(div2View.viewEquals(div2ReferenceView))
    }

    @Test
    fun `patch gallery transactional - failed`() {
        val oldData = UnitTestData("$PATCH_DIR/gallery", "gallery-initial.json").dataWithTemplates
        // reference is the same
        val referenceData = UnitTestData("$PATCH_DIR/gallery", "gallery-initial.json").dataWithTemplates
        div2View.setData(oldData, tag)
        div2ReferenceView.setData(referenceData, tag)
        val patch = UnitTestData("$PATCH_DIR/gallery", "gallery-patch-transactional-broken.json").patchWithTemplates
        div2View.applyPatch(patch)

        Assert.assertTrue(div2View.viewEquals(div2ReferenceView))
    }

    @Test
    fun `patch gallery partial - success`() {
        val oldData = UnitTestData("$PATCH_DIR/gallery", "gallery-initial.json").dataWithTemplates
        val referenceData = UnitTestData("$PATCH_DIR/gallery", "gallery-success-partial.json").dataWithTemplates
        div2View.setData(oldData, tag)
        div2ReferenceView.setData(referenceData, tag)
        val patch = UnitTestData("$PATCH_DIR/gallery", "gallery-patch-partial.json").patchWithTemplates
        div2View.applyPatch(patch)

        Assert.assertTrue(div2View.viewEquals(div2ReferenceView))
    }

    @Test
    fun `patch pager transactional - success`() {
        val oldData = UnitTestData("$PATCH_DIR/pager", "pager-initial.json").dataWithTemplates
        val referenceData = UnitTestData("$PATCH_DIR/pager", "pager-success-transactional.json").dataWithTemplates
        div2View.setData(oldData, tag)
        div2ReferenceView.setData(referenceData, tag)
        val patch = UnitTestData("$PATCH_DIR/pager", "pager-patch-transactional.json").patchWithTemplates
        div2View.applyPatch(patch)

        Assert.assertTrue(div2View.viewEquals(div2ReferenceView))
    }

    @Test
    fun `patch pager transactional - failed`() {
        val oldData = UnitTestData("$PATCH_DIR/pager", "pager-initial.json").dataWithTemplates
        // reference is the same
        val referenceData = UnitTestData("$PATCH_DIR/pager", "pager-initial.json").dataWithTemplates
        div2View.setData(oldData, tag)
        div2ReferenceView.setData(referenceData, tag)
        val patch = UnitTestData("$PATCH_DIR/pager", "pager-patch-transactional-broken.json").patchWithTemplates
        div2View.applyPatch(patch)

        Assert.assertTrue(div2View.viewEquals(div2ReferenceView))
    }

    @Test
    fun `patch pager partial - success`() {
        val oldData = UnitTestData("$PATCH_DIR/pager", "pager-initial.json").dataWithTemplates
        val referenceData = UnitTestData("$PATCH_DIR/pager", "pager-success-partial.json").dataWithTemplates
        div2View.setData(oldData, tag)
        div2ReferenceView.setData(referenceData, tag)
        val patch = UnitTestData("$PATCH_DIR/pager", "pager-patch-partial.json").patchWithTemplates
        div2View.applyPatch(patch)

        Assert.assertTrue(div2View.viewEquals(div2ReferenceView))
    }

}
