package com.yandex.div.compose.di

import com.yandex.div.compose.actions.DivActionHandler
import com.yandex.div.compose.animation.AnimatorStorage
import com.yandex.div.compose.collection.CollectionItemScopeStorage
import com.yandex.div.compose.context.CompositionKeyStorage
import com.yandex.div.compose.context.DivViewCoroutineScopeProvider
import com.yandex.div.compose.focus.FocusBindingStorage
import com.yandex.div.compose.images.ImageStateStorage
import com.yandex.div.compose.layoutprovider.LayoutProviderStorage
import com.yandex.div.compose.scroll.ScrollTargetStorage
import com.yandex.div.compose.state.DivStateStorage
import com.yandex.div.compose.storedvalues.DivStoredValuesStorage
import com.yandex.div.compose.video.VideoPlayerStorage
import com.yandex.div.compose.visibility.VisibilityActionTracker

internal interface DivViewDependencies : DivContextDependencies {

    val actionHandler: DivActionHandler
    val animatorStorage: AnimatorStorage
    val collectionItemScopeStorage: CollectionItemScopeStorage
    val compositionKeyStorage: CompositionKeyStorage
    val coroutineScopeProvider: DivViewCoroutineScopeProvider
    val focusBindingStorage: FocusBindingStorage
    val imageStateStorage: ImageStateStorage
    val layoutProviderStorage: LayoutProviderStorage
    val scrollTargetStorage: ScrollTargetStorage
    val stateStorage: DivStateStorage
    val storedValuesStorage: DivStoredValuesStorage
    val videoPlayerStorage: VideoPlayerStorage
    val visibilityActionTracker: VisibilityActionTracker
}
