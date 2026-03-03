package com.yandex.div.core.util

import com.yandex.div.core.view2.PATCH_DIR
import com.yandex.div.core.view2.divs.UnitTestData
import com.yandex.div.internal.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DivDataApplyPatchTest {

    @Test
    fun `divdata patch container transactional - success`() {
        testPatch(
            initial = UnitTestData("$PATCH_DIR/container", "container-initial.json"),
            expected = UnitTestData("$PATCH_DIR/container", "container-success-transactional.json"),
            patch = UnitTestData("$PATCH_DIR/container", "container-patch-transactional.json")
        )
    }

    @Test
    fun `divdata patch container transactional - failed`() {
        testPatch(
            initial = UnitTestData("$PATCH_DIR/container", "container-initial.json"),
            expected = null,
            patch = UnitTestData("$PATCH_DIR/container", "container-broken-patch-transactional.json")
        )
    }

    @Test
    fun `divdata patch container partial - success`() {
        testPatch(
            initial = UnitTestData("$PATCH_DIR/container", "container-initial.json"),
            expected = UnitTestData("$PATCH_DIR/container", "container-success-partial.json"),
            patch = UnitTestData("$PATCH_DIR/container", "container-patch-partial.json")
        )
    }

    @Test
    fun `divdata patch grid transactional - success`() {
        testPatch(
            initial = UnitTestData("$PATCH_DIR/grid", "grid-initial.json"),
            expected = UnitTestData("$PATCH_DIR/grid", "grid-success-transactional.json"),
            patch = UnitTestData("$PATCH_DIR/grid", "grid-patch-transactional.json")
        )
    }

    @Test
    fun `divdata patch grid transactional - failed`() {
        testPatch(
            initial = UnitTestData("$PATCH_DIR/grid", "grid-initial.json"),
            expected = null,
            patch = UnitTestData("$PATCH_DIR/grid", "grid-patch-transactional-broken.json")
        )
    }

    @Test
    fun `divdata patch grid partial - success`() {
        testPatch(
            initial = UnitTestData("$PATCH_DIR/grid", "grid-initial.json"),
            expected = UnitTestData("$PATCH_DIR/grid", "grid-success-partial.json"),
            patch = UnitTestData("$PATCH_DIR/grid", "grid-patch-partial.json")
        )
    }

    @Test
    fun `divdata patch gallery transactional - success`() {
        testPatch(
            initial = UnitTestData("$PATCH_DIR/gallery", "gallery-initial.json"),
            expected = UnitTestData("$PATCH_DIR/gallery", "gallery-success-transactional.json"),
            patch = UnitTestData("$PATCH_DIR/gallery", "gallery-patch-transactional.json")
        )
    }

    @Test
    fun `divdata patch gallery transactional - failed`() {
        testPatch(
            initial = UnitTestData("$PATCH_DIR/gallery", "gallery-initial.json"),
            expected = null,
            patch = UnitTestData("$PATCH_DIR/gallery", "gallery-patch-transactional-broken.json")
        )
    }

    @Test
    fun `divdata patch gallery partial - success`() {
        testPatch(
            initial = UnitTestData("$PATCH_DIR/gallery", "gallery-initial.json"),
            expected = UnitTestData("$PATCH_DIR/gallery", "gallery-success-partial.json"),
            patch = UnitTestData("$PATCH_DIR/gallery", "gallery-patch-partial.json")
        )
    }

    @Test
    fun `divdata patch pager transactional - success`() {
        testPatch(
            initial = UnitTestData("$PATCH_DIR/pager", "pager-initial.json"),
            expected = UnitTestData("$PATCH_DIR/pager", "pager-success-transactional.json"),
            patch = UnitTestData("$PATCH_DIR/pager", "pager-patch-transactional.json")
        )
    }

    @Test
    fun `divdata patch pager transactional - failed`() {
        testPatch(
            initial = UnitTestData("$PATCH_DIR/pager", "pager-initial.json"),
            expected = null,
            patch = UnitTestData("$PATCH_DIR/pager", "pager-patch-transactional-broken.json")
        )
    }

    @Test
    fun `divdata patch pager partial - success`() {
        testPatch(
            initial = UnitTestData("$PATCH_DIR/pager", "pager-initial.json"),
            expected = UnitTestData("$PATCH_DIR/pager", "pager-success-partial.json"),
            patch = UnitTestData("$PATCH_DIR/pager", "pager-patch-partial.json")
        )
    }

    @Test
    fun `divdata patch state transactional - success`() {
        testPatch(
            initial = UnitTestData("$PATCH_DIR/state", "state-initial.json"),
            expected = UnitTestData("$PATCH_DIR/state", "state-success-transactional.json"),
            patch = UnitTestData("$PATCH_DIR/state", "state-patch-transactional-success.json")
        )
    }

    @Test
    fun `divdata patch state transactional - failed`() {
        testPatch(
            initial = UnitTestData("$PATCH_DIR/state", "state-initial.json"),
            expected = UnitTestData("$PATCH_DIR/state", "state-initial.json"),
            patch = UnitTestData("$PATCH_DIR/state", "state-patch-transactional-broken.json")
        )
    }

    @Test
    fun `divdata patch state partial - success`() {
        testPatch(
            initial = UnitTestData("$PATCH_DIR/state", "state-initial.json"),
            expected = UnitTestData("$PATCH_DIR/state", "state-success-partial.json"),
            patch = UnitTestData("$PATCH_DIR/state", "state-patch-partial.json")
        )
    }

    private fun testPatch(
        initial: UnitTestData,
        expected: UnitTestData?,
        patch: UnitTestData,
    ) {
        val referenceDivData = expected?.dataWithTemplates
        val divData = initial.dataWithTemplates
        val divPatch = patch.patchWithTemplates
        val patchedDivData = divData.applyPatch(divPatch)

        Assert.assertEquals(
            patchedDivData?.writeToJSON()?.toString(),
            referenceDivData?.writeToJSON()?.toString()
        )
    }
}
