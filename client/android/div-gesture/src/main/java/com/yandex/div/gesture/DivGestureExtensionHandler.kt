package com.yandex.div.gesture

import android.annotation.SuppressLint
import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import com.yandex.div.core.extension.DivExtensionHandler
import com.yandex.div.core.view2.Div2View
import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div.internal.parser.JsonParser
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivAction
import com.yandex.div2.DivBase
import org.json.JSONObject

private const val EXTENSION_ID = "gesture"
private const val SWIPE_UP_PARAM = "swipe_up"
private const val SWIPE_DOWN_PARAM = "swipe_down"
private const val SWIPE_LEFT_PARAM = "swipe_left"
private const val SWIPE_RIGHT_PARAM = "swipe_right"

/**
 * DivExtension that listens to user gestures.
 *
 * Example of usage:
 * ```json
 * "extensions": [
 *   {
 *     "id": "gesture",
 *     "params": {
 *       (optional) "swipe_up": [<DivAction>],
 *       (optional) "swipe_down": [<DivAction>],
 *       (optional) "swipe_left": [<DivAction>],
 *       (optional) "swipe_right": [<DivAction>]
 *     }
 *   }
 * ]
 * ```
 */
public class DivGestureExtensionHandler (
    private val parsingErrorLoggerFactory: ParsingErrorLoggerFactory,
) : DivExtensionHandler {

    override fun matches(div: DivBase): Boolean {
        return div.extensions?.any { extension ->
            extension.id == EXTENSION_ID
        } ?: false
    }

    override fun bindView(divView: Div2View, expressionResolver: ExpressionResolver,
                          view: View, div: DivBase) {
        val params = div.extensions?.first { it.id == EXTENSION_ID }?.params ?: return

        val swipeUpActions = readDivActions(params, SWIPE_UP_PARAM)
        val swipeDownActions = readDivActions(params, SWIPE_DOWN_PARAM)
        val swipeLeftActions = readDivActions(params, SWIPE_LEFT_PARAM)
        val swipeRightActions = readDivActions(params, SWIPE_RIGHT_PARAM)

        val swipeTouchListener = OnSwipeTouchListener(
                context = view.context,
                onSwipeUp = createSwipeActionPerformer(swipeUpActions, divView, expressionResolver),
                onSwipeDown = createSwipeActionPerformer(swipeDownActions, divView, expressionResolver),
                onSwipeLeft = createSwipeActionPerformer(swipeLeftActions, divView, expressionResolver),
                onSwipeRight = createSwipeActionPerformer(swipeRightActions, divView, expressionResolver),
        )

        view.setOnTouchListener(swipeTouchListener)
    }

    override fun unbindView(divView: Div2View, expressionResolver: ExpressionResolver,
                            view: View, div: DivBase) {
        view.setOnTouchListener(null)
    }

    private fun readDivActions(json: JSONObject, key: String): List<DivAction>? {
        val env = DivParsingEnvironment(parsingErrorLoggerFactory.create(key))
        return JsonParser.readOptionalList(json, key, DivAction.CREATOR, env.logger, env)
    }

    private fun createSwipeActionPerformer(
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

    private inner class OnSwipeTouchListener(
        context: Context,
        private val onSwipeUp: () -> Unit,
        private val onSwipeDown: () -> Unit,
        private val onSwipeLeft: () -> Unit,
        private val onSwipeRight: () -> Unit,
    ) : View.OnTouchListener {

        private val gestureConsumer = object : SwipeGestureConsumer {
            override fun onTouchStart(info: SwipeGestureInfo): Boolean = false
            override fun onSwipe(info: SwipeGestureInfo): Boolean = info.isTouchSlopBreached
            override fun onTouchEnd(info: SwipeGestureInfo): Boolean {
                return when {
                    info.isSwipingUp -> {
                        onSwipeUp()
                        true
                    }
                    info.isSwipingDown -> {
                        onSwipeDown()
                        true
                    }
                    info.isSwipingLeft -> {
                        onSwipeLeft()
                        true
                    }
                    info.isSwipingRight -> {
                        onSwipeRight()
                        true
                    }
                    else -> false
                }
            }
        }

        private val gestureInterpreter = SwipeGestureInterpreter(
            ViewConfiguration.get(context).scaledTouchSlop,
            gestureConsumer,
        )

        @SuppressLint("ClickableViewAccessibility")
        override fun onTouch(v: View, event: MotionEvent): Boolean {
            return gestureInterpreter.consumeTouchEvent(event)
        }
    }
}
