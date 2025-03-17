// Copyright 2024 Yandex LLC. All rights reserved.

package com.yandex.div.shine

import android.database.Observable
import android.view.View
import com.yandex.div.core.extension.DivExtensionHandler
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.widget.LoadableImageView
import com.yandex.div.internal.Assert
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivAction
import com.yandex.div2.DivBase
import com.yandex.div2.DivImage
import org.json.JSONObject
import java.lang.ref.WeakReference

internal const val EXTENSION_ID = "shine"

class DivShineExtensionHandler @JvmOverloads constructor(
    private val logger: DivShineLogger = DivShineLogger.STUB,
    private val pauseShineObservable : Observable<PauseShineObserver>? = null,
) : DivExtensionHandler {

    override fun matches(div: DivBase): Boolean {
        if (div !is DivImage) {
            return false
        }

        val res = div.extensions?.any { extension ->
            extension.id == EXTENSION_ID
        } ?: false

        return res
    }

    override fun bindView(
        divView: Div2View,
        expressionResolver: ExpressionResolver,
        view: View,
        div: DivBase
    ) {
        val imageView = view as? LoadableImageView ?: run {
            logger.logError(
                IllegalArgumentException(
                    "$EXTENSION_ID extension's view is not instance of LoadableImageView"
                )
            )
            return
        }

        val params: JSONObject? = div.extensions?.find { extension ->
            return@find extension.id == EXTENSION_ID
        }?.params

        val tintColorExpression = (div as? DivImage)?.tintColor

        val data = ShineData.fromJson(
            logger,
            params,
            tintColorExpression = tintColorExpression,
        )

        val actionPerformer = createActionPerformer(
            data.onCycleStartActions,
            divView,
            expressionResolver
        )

        val transformer = ShineImageTransformer(
            data,
            actionPerformer,
            expressionResolver,
            logger,
        )

        val animationController = ShineAnimationController(
            transformer,
            WeakReference(view)
        )

        view.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        imageView.imageTransformer = transformer
        imageView.delegate = animationController
        pauseShineObservable?.registerObserver(animationController)
        view.setTag(R.id.div_shine_image_transformer, transformer)
        view.setTag(R.id.div_shine_animation_controller, animationController)
    }

    override fun unbindView(
        divView: Div2View,
        expressionResolver: ExpressionResolver,
        view: View,
        div: DivBase
    ) {
        val imageView = view as? LoadableImageView
        imageView?.imageTransformer = null
        imageView?.delegate = null

        val animationController =
            (view.getTag(R.id.div_shine_animation_controller) as? ShineAnimationController)
        pauseShineObservable?.unregisterObserver(animationController)
        Assert.assertNotNull(
            "Animation controller in $EXTENSION_ID extension wasn't properly released",
            animationController
        )

        val transformer = (view.getTag(R.id.div_shine_image_transformer) as? ShineImageTransformer)
        transformer?.clear()
        Assert.assertNotNull( "Transformer in $EXTENSION_ID extension wasn't properly released", transformer )
    }

    private fun createActionPerformer(
        divActions: List<DivAction>?,
        divView: Div2View,
        expressionResolver: ExpressionResolver,
    ): () -> Unit {
        return {
            divActions?.let { actions ->
                actions
                    .filter { it.isEnabled.evaluate(expressionResolver) }
                    .forEach { action -> divView.handleAction(action) }
            }
        }
    }
}

