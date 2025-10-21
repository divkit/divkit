@file:Suppress("unused")

package divkit.dsl.expression

import divkit.dsl.EnumValue
import divkit.dsl.scope.DivScope

sealed class ActionEnumValue(
    val value: String,
)

sealed interface OverflowActionEnum

object ClampOverflowActionEnumValue : ActionEnumValue("clamp"),
    OverflowActionEnum

object RingOverflowActionEnumValue : ActionEnumValue("ring"),
    OverflowActionEnum

val DivScope.clamp: ClampOverflowActionEnumValue
    get() = ClampOverflowActionEnumValue

val DivScope.ring: RingOverflowActionEnumValue
    get() = RingOverflowActionEnumValue

sealed interface VideoActionEnum

object PauseVideoActionEnum : ActionEnumValue("pause"),
    VideoActionEnum

object StartVideoActionEnum : ActionEnumValue("start"),
    VideoActionEnum

val DivScope.pause: PauseVideoActionEnum
    get() = PauseVideoActionEnum

val DivScope.start: StartVideoActionEnum
    get() = StartVideoActionEnum

sealed interface OpenModeActionEnum

object PopupOpenModeActionEnum : ActionEnumValue("popup"),
    OpenModeActionEnum

object FullscreenOpenModeActionEnum : ActionEnumValue("fullscreen"),
    OpenModeActionEnum

val DivScope.popup: PopupOpenModeActionEnum
    get() = PopupOpenModeActionEnum

val DivScope.fullscreen: FullscreenOpenModeActionEnum
    get() = FullscreenOpenModeActionEnum

fun EnumValue.string(): Expression<String> = this.serialized.string()
fun ActionEnumValue.string(): Expression<String> = this.value.string()
