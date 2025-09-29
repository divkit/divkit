package com.yandex.div.core.view2.divs.widgets

import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.dagger.DivViewScope
import javax.inject.Inject

@DivViewScope
@Mockable
internal class MediaReleaseViewVisitor @Inject constructor() : DivViewVisitor() {
    override fun visit(view: DivVideoView) {
        view.releaseMedia()
    }

    override fun visit(view: DivImageView) {
        view.releaseMedia()
    }

    override fun visit(view: DivGifImageView) {
        view.releaseMedia()
    }
}
