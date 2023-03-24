@file:Suppress(
    "unused",
    "UNUSED_PARAMETER",
)

package divkit.dsl

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonValue
import divkit.dsl.annotation.*
import divkit.dsl.core.*
import divkit.dsl.scope.*
import kotlin.Any
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map

@Generated
sealed class EnumValue(
    @JsonValue
    val serialized: String,
)

@Generated
object DefaultEnumValue : EnumValue("default"),
    Accessibility.Mode,
    Gallery.ScrollMode

@Generated
object MergeEnumValue : EnumValue("merge"),
    Accessibility.Mode

@Generated
object ExcludeEnumValue : EnumValue("exclude"),
    Accessibility.Mode

@Generated
object NoneEnumValue : EnumValue("none"),
    Accessibility.Type,
    LineStyle,
    Tabs.TabTitleStyle.AnimationType,
    Text.Truncate,
    TransitionSelector

@Generated
object ButtonEnumValue : EnumValue("button"),
    Accessibility.Type

@Generated
object ImageEnumValue : EnumValue("image"),
    Accessibility.Type

@Generated
object TextEnumValue : EnumValue("text"),
    Accessibility.Type,
    FontFamily

@Generated
object EditTextEnumValue : EnumValue("edit_text"),
    Accessibility.Type

@Generated
object HeaderEnumValue : EnumValue("header"),
    Accessibility.Type

@Generated
object TabBarEnumValue : EnumValue("tab_bar"),
    Accessibility.Type

@Generated
object ListEnumValue : EnumValue("list"),
    Accessibility.Type

@Generated
object SelfEnumValue : EnumValue("_self"),
    Action.Target

@Generated
object BlankEnumValue : EnumValue("_blank"),
    Action.Target

@Generated
object LeftEnumValue : EnumValue("left"),
    AlignmentHorizontal,
    SlideTransition.Edge,
    Tooltip.Position

@Generated
object CenterEnumValue : EnumValue("center"),
    AlignmentHorizontal,
    AlignmentVertical,
    Gallery.CrossContentAlignment

@Generated
object RightEnumValue : EnumValue("right"),
    AlignmentHorizontal,
    SlideTransition.Edge,
    Tooltip.Position

@Generated
object TopEnumValue : EnumValue("top"),
    AlignmentVertical,
    SlideTransition.Edge,
    Tooltip.Position

@Generated
object BottomEnumValue : EnumValue("bottom"),
    AlignmentVertical,
    SlideTransition.Edge,
    Tooltip.Position

@Generated
object BaselineEnumValue : EnumValue("baseline"),
    AlignmentVertical

@Generated
object FadeEnumValue : EnumValue("fade"),
    Animation.Name,
    Tabs.TabTitleStyle.AnimationType

@Generated
object TranslateEnumValue : EnumValue("translate"),
    Animation.Name

@Generated
object ScaleEnumValue : EnumValue("scale"),
    Animation.Name,
    Indicator.Animation

@Generated
object NativeEnumValue : EnumValue("native"),
    Animation.Name

@Generated
object SetEnumValue : EnumValue("set"),
    Animation.Name

@Generated
object NoAnimationEnumValue : EnumValue("no_animation"),
    Animation.Name

@Generated
object LinearEnumValue : EnumValue("linear"),
    AnimationInterpolator

@Generated
object EaseEnumValue : EnumValue("ease"),
    AnimationInterpolator

@Generated
object EaseInEnumValue : EnumValue("ease_in"),
    AnimationInterpolator

@Generated
object EaseOutEnumValue : EnumValue("ease_out"),
    AnimationInterpolator

@Generated
object EaseInOutEnumValue : EnumValue("ease_in_out"),
    AnimationInterpolator

@Generated
object SpringEnumValue : EnumValue("spring"),
    AnimationInterpolator

@Generated
object SourceInEnumValue : EnumValue("source_in"),
    BlendMode

@Generated
object SourceAtopEnumValue : EnumValue("source_atop"),
    BlendMode

@Generated
object DarkenEnumValue : EnumValue("darken"),
    BlendMode

@Generated
object LightenEnumValue : EnumValue("lighten"),
    BlendMode

@Generated
object MultiplyEnumValue : EnumValue("multiply"),
    BlendMode

@Generated
object ScreenEnumValue : EnumValue("screen"),
    BlendMode

@Generated
object NoWrapEnumValue : EnumValue("no_wrap"),
    Container.LayoutMode

@Generated
object WrapEnumValue : EnumValue("wrap"),
    Container.LayoutMode

@Generated
object VerticalEnumValue : EnumValue("vertical"),
    Container.Orientation,
    Gallery.Orientation,
    Pager.Orientation,
    Separator.DelimiterStyle.Orientation

@Generated
object HorizontalEnumValue : EnumValue("horizontal"),
    Container.Orientation,
    Gallery.Orientation,
    Pager.Orientation,
    Separator.DelimiterStyle.Orientation

@Generated
object OverlapEnumValue : EnumValue("overlap"),
    Container.Orientation

@Generated
object DisplayEnumValue : EnumValue("display"),
    FontFamily

@Generated
object LightEnumValue : EnumValue("light"),
    FontWeight

@Generated
object MediumEnumValue : EnumValue("medium"),
    FontWeight

@Generated
object RegularEnumValue : EnumValue("regular"),
    FontWeight

@Generated
object BoldEnumValue : EnumValue("bold"),
    FontWeight

@Generated
object StartEnumValue : EnumValue("start"),
    Gallery.CrossContentAlignment,
    Text.Truncate

@Generated
object EndEnumValue : EnumValue("end"),
    Gallery.CrossContentAlignment,
    Text.Truncate

@Generated
object PagingEnumValue : EnumValue("paging"),
    Gallery.ScrollMode

@Generated
object FillEnumValue : EnumValue("fill"),
    ImageScale

@Generated
object NoScaleEnumValue : EnumValue("no_scale"),
    ImageScale

@Generated
object FitEnumValue : EnumValue("fit"),
    ImageScale

@Generated
object WormEnumValue : EnumValue("worm"),
    Indicator.Animation

@Generated
object SliderEnumValue : EnumValue("slider"),
    Indicator.Animation

@Generated
object SingleLineTextEnumValue : EnumValue("single_line_text"),
    Input.KeyboardType,
    KeyboardInput.KeyboardType

@Generated
object MultiLineTextEnumValue : EnumValue("multi_line_text"),
    Input.KeyboardType,
    KeyboardInput.KeyboardType

@Generated
object PhoneEnumValue : EnumValue("phone"),
    Input.KeyboardType,
    KeyboardInput.KeyboardType

@Generated
object NumberEnumValue : EnumValue("number"),
    Input.KeyboardType,
    KeyboardInput.KeyboardType

@Generated
object EmailEnumValue : EnumValue("email"),
    Input.KeyboardType,
    KeyboardInput.KeyboardType

@Generated
object UriEnumValue : EnumValue("uri"),
    Input.KeyboardType,
    KeyboardInput.KeyboardType

@Generated
object DecimalEnumValue : EnumValue("decimal"),
    Input.KeyboardType,
    KeyboardInput.KeyboardType

@Generated
object SingleEnumValue : EnumValue("single"),
    LineStyle

@Generated
object TransactionalEnumValue : EnumValue("transactional"),
    Patch.Mode

@Generated
object PartialEnumValue : EnumValue("partial"),
    Patch.Mode

@Generated
object NearestCornerEnumValue : EnumValue("nearest_corner"),
    RadialGradientRelativeRadius.Value

@Generated
object FarthestCornerEnumValue : EnumValue("farthest_corner"),
    RadialGradientRelativeRadius.Value

@Generated
object NearestSideEnumValue : EnumValue("nearest_side"),
    RadialGradientRelativeRadius.Value

@Generated
object FarthestSideEnumValue : EnumValue("farthest_side"),
    RadialGradientRelativeRadius.Value

@Generated
object DpEnumValue : EnumValue("dp"),
    SizeUnit

@Generated
object SpEnumValue : EnumValue("sp"),
    SizeUnit

@Generated
object PxEnumValue : EnumValue("px"),
    SizeUnit

@Generated
object SlideEnumValue : EnumValue("slide"),
    Tabs.TabTitleStyle.AnimationType

@Generated
object MiddleEnumValue : EnumValue("middle"),
    Text.Truncate

@Generated
object TopLeftEnumValue : EnumValue("top-left"),
    Tooltip.Position

@Generated
object TopRightEnumValue : EnumValue("top-right"),
    Tooltip.Position

@Generated
object BottomRightEnumValue : EnumValue("bottom-right"),
    Tooltip.Position

@Generated
object BottomLeftEnumValue : EnumValue("bottom-left"),
    Tooltip.Position

@Generated
object DataChangeEnumValue : EnumValue("data_change"),
    TransitionSelector,
    TransitionTrigger

@Generated
object StateChangeEnumValue : EnumValue("state_change"),
    TransitionSelector,
    TransitionTrigger

@Generated
object AnyChangeEnumValue : EnumValue("any_change"),
    TransitionSelector

@Generated
object VisibilityChangeEnumValue : EnumValue("visibility_change"),
    TransitionTrigger

@Generated
object OnConditionEnumValue : EnumValue("on_condition"),
    Trigger.Mode

@Generated
object OnVariableEnumValue : EnumValue("on_variable"),
    Trigger.Mode

@Generated
object VisibleEnumValue : EnumValue("visible"),
    Visibility

@Generated
object InvisibleEnumValue : EnumValue("invisible"),
    Visibility

@Generated
object GoneEnumValue : EnumValue("gone"),
    Visibility

@Generated
val DivScope.default: DefaultEnumValue
    get() = DefaultEnumValue

@Generated
val DivScope.merge: MergeEnumValue
    get() = MergeEnumValue

@Generated
val DivScope.exclude: ExcludeEnumValue
    get() = ExcludeEnumValue

@Generated
val DivScope.none: NoneEnumValue
    get() = NoneEnumValue

@Generated
val DivScope.button: ButtonEnumValue
    get() = ButtonEnumValue

@Generated
val DivScope.image: ImageEnumValue
    get() = ImageEnumValue

@Generated
val DivScope.text: TextEnumValue
    get() = TextEnumValue

@Generated
val DivScope.edit_text: EditTextEnumValue
    get() = EditTextEnumValue

@Generated
val DivScope.header: HeaderEnumValue
    get() = HeaderEnumValue

@Generated
val DivScope.tab_bar: TabBarEnumValue
    get() = TabBarEnumValue

@Generated
val DivScope.list: ListEnumValue
    get() = ListEnumValue

@Generated
val DivScope.self: SelfEnumValue
    get() = SelfEnumValue

@Generated
val DivScope.blank: BlankEnumValue
    get() = BlankEnumValue

@Generated
val DivScope.left: LeftEnumValue
    get() = LeftEnumValue

@Generated
val DivScope.center: CenterEnumValue
    get() = CenterEnumValue

@Generated
val DivScope.right: RightEnumValue
    get() = RightEnumValue

@Generated
val DivScope.top: TopEnumValue
    get() = TopEnumValue

@Generated
val DivScope.bottom: BottomEnumValue
    get() = BottomEnumValue

@Generated
val DivScope.baseline: BaselineEnumValue
    get() = BaselineEnumValue

@Generated
val DivScope.fade: FadeEnumValue
    get() = FadeEnumValue

@Generated
val DivScope.translate: TranslateEnumValue
    get() = TranslateEnumValue

@Generated
val DivScope.scale: ScaleEnumValue
    get() = ScaleEnumValue

@Generated
val DivScope.native: NativeEnumValue
    get() = NativeEnumValue

@Generated
val DivScope.set: SetEnumValue
    get() = SetEnumValue

@Generated
val DivScope.no_animation: NoAnimationEnumValue
    get() = NoAnimationEnumValue

@Generated
val DivScope.linear: LinearEnumValue
    get() = LinearEnumValue

@Generated
val DivScope.ease: EaseEnumValue
    get() = EaseEnumValue

@Generated
val DivScope.ease_in: EaseInEnumValue
    get() = EaseInEnumValue

@Generated
val DivScope.ease_out: EaseOutEnumValue
    get() = EaseOutEnumValue

@Generated
val DivScope.ease_in_out: EaseInOutEnumValue
    get() = EaseInOutEnumValue

@Generated
val DivScope.spring: SpringEnumValue
    get() = SpringEnumValue

@Generated
val DivScope.source_in: SourceInEnumValue
    get() = SourceInEnumValue

@Generated
val DivScope.source_atop: SourceAtopEnumValue
    get() = SourceAtopEnumValue

@Generated
val DivScope.darken: DarkenEnumValue
    get() = DarkenEnumValue

@Generated
val DivScope.lighten: LightenEnumValue
    get() = LightenEnumValue

@Generated
val DivScope.multiply: MultiplyEnumValue
    get() = MultiplyEnumValue

@Generated
val DivScope.screen: ScreenEnumValue
    get() = ScreenEnumValue

@Generated
val DivScope.no_wrap: NoWrapEnumValue
    get() = NoWrapEnumValue

@Generated
val DivScope.wrap: WrapEnumValue
    get() = WrapEnumValue

@Generated
val DivScope.vertical: VerticalEnumValue
    get() = VerticalEnumValue

@Generated
val DivScope.horizontal: HorizontalEnumValue
    get() = HorizontalEnumValue

@Generated
val DivScope.overlap: OverlapEnumValue
    get() = OverlapEnumValue

@Generated
val DivScope.display: DisplayEnumValue
    get() = DisplayEnumValue

@Generated
val DivScope.light: LightEnumValue
    get() = LightEnumValue

@Generated
val DivScope.medium: MediumEnumValue
    get() = MediumEnumValue

@Generated
val DivScope.regular: RegularEnumValue
    get() = RegularEnumValue

@Generated
val DivScope.bold: BoldEnumValue
    get() = BoldEnumValue

@Generated
val DivScope.start: StartEnumValue
    get() = StartEnumValue

@Generated
val DivScope.end: EndEnumValue
    get() = EndEnumValue

@Generated
val DivScope.paging: PagingEnumValue
    get() = PagingEnumValue

@Generated
val DivScope.fill: FillEnumValue
    get() = FillEnumValue

@Generated
val DivScope.no_scale: NoScaleEnumValue
    get() = NoScaleEnumValue

@Generated
val DivScope.fit: FitEnumValue
    get() = FitEnumValue

@Generated
val DivScope.worm: WormEnumValue
    get() = WormEnumValue

@Generated
val DivScope.slider: SliderEnumValue
    get() = SliderEnumValue

@Generated
val DivScope.single_line_text: SingleLineTextEnumValue
    get() = SingleLineTextEnumValue

@Generated
val DivScope.multi_line_text: MultiLineTextEnumValue
    get() = MultiLineTextEnumValue

@Generated
val DivScope.phone: PhoneEnumValue
    get() = PhoneEnumValue

@Generated
val DivScope.number: NumberEnumValue
    get() = NumberEnumValue

@Generated
val DivScope.email: EmailEnumValue
    get() = EmailEnumValue

@Generated
val DivScope.uri: UriEnumValue
    get() = UriEnumValue

@Generated
val DivScope.decimal: DecimalEnumValue
    get() = DecimalEnumValue

@Generated
val DivScope.single: SingleEnumValue
    get() = SingleEnumValue

@Generated
val DivScope.transactional: TransactionalEnumValue
    get() = TransactionalEnumValue

@Generated
val DivScope.partial: PartialEnumValue
    get() = PartialEnumValue

@Generated
val DivScope.nearest_corner: NearestCornerEnumValue
    get() = NearestCornerEnumValue

@Generated
val DivScope.farthest_corner: FarthestCornerEnumValue
    get() = FarthestCornerEnumValue

@Generated
val DivScope.nearest_side: NearestSideEnumValue
    get() = NearestSideEnumValue

@Generated
val DivScope.farthest_side: FarthestSideEnumValue
    get() = FarthestSideEnumValue

@Generated
val DivScope.dp: DpEnumValue
    get() = DpEnumValue

@Generated
val DivScope.sp: SpEnumValue
    get() = SpEnumValue

@Generated
val DivScope.px: PxEnumValue
    get() = PxEnumValue

@Generated
val DivScope.slide: SlideEnumValue
    get() = SlideEnumValue

@Generated
val DivScope.middle: MiddleEnumValue
    get() = MiddleEnumValue

@Generated
val DivScope.top_left: TopLeftEnumValue
    get() = TopLeftEnumValue

@Generated
val DivScope.top_right: TopRightEnumValue
    get() = TopRightEnumValue

@Generated
val DivScope.bottom_right: BottomRightEnumValue
    get() = BottomRightEnumValue

@Generated
val DivScope.bottom_left: BottomLeftEnumValue
    get() = BottomLeftEnumValue

@Generated
val DivScope.data_change: DataChangeEnumValue
    get() = DataChangeEnumValue

@Generated
val DivScope.state_change: StateChangeEnumValue
    get() = StateChangeEnumValue

@Generated
val DivScope.any_change: AnyChangeEnumValue
    get() = AnyChangeEnumValue

@Generated
val DivScope.visibility_change: VisibilityChangeEnumValue
    get() = VisibilityChangeEnumValue

@Generated
val DivScope.on_condition: OnConditionEnumValue
    get() = OnConditionEnumValue

@Generated
val DivScope.on_variable: OnVariableEnumValue
    get() = OnVariableEnumValue

@Generated
val DivScope.visible: VisibleEnumValue
    get() = VisibleEnumValue

@Generated
val DivScope.invisible: InvisibleEnumValue
    get() = InvisibleEnumValue

@Generated
val DivScope.gone: GoneEnumValue
    get() = GoneEnumValue
