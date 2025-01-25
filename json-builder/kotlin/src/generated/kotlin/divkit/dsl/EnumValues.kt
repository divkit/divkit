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
    Gallery.ScrollMode,
    Input.EnterKeyType

@Generated
object MergeEnumValue : EnumValue("merge"),
    Accessibility.Mode

@Generated
object ExcludeEnumValue : EnumValue("exclude"),
    Accessibility.Mode

@Generated
object NoneEnumValue : EnumValue("none"),
    Accessibility.Type,
    Gallery.Scrollbar,
    Input.Autocapitalization,
    LineStyle,
    Tabs.TabTitleStyle.AnimationType,
    Text.Image.Accessibility.Type,
    Text.Truncate,
    TransitionSelector

@Generated
object ButtonEnumValue : EnumValue("button"),
    Accessibility.Type,
    Text.Image.Accessibility.Type

@Generated
object ImageEnumValue : EnumValue("image"),
    Accessibility.Type,
    Text.Image.Accessibility.Type

@Generated
object TextEnumValue : EnumValue("text"),
    Accessibility.Type,
    Text.Image.Accessibility.Type

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
object SelectEnumValue : EnumValue("select"),
    Accessibility.Type

@Generated
object CheckboxEnumValue : EnumValue("checkbox"),
    Accessibility.Type

@Generated
object RadioEnumValue : EnumValue("radio"),
    Accessibility.Type

@Generated
object AutoEnumValue : EnumValue("auto"),
    Accessibility.Type,
    Gallery.Scrollbar,
    Input.Autocapitalization,
    Text.Image.Accessibility.Type

@Generated
object SelfEnumValue : EnumValue("_self"),
    Action.Target

@Generated
object BlankEnumValue : EnumValue("_blank"),
    Action.Target

@Generated
object ClampEnumValue : EnumValue("clamp"),
    ActionScrollBy.Overflow

@Generated
object RingEnumValue : EnumValue("ring"),
    ActionScrollBy.Overflow

@Generated
object GetEnumValue : EnumValue("get"),
    ActionSubmit.Request.Method

@Generated
object PostEnumValue : EnumValue("post"),
    ActionSubmit.Request.Method

@Generated
object PutEnumValue : EnumValue("put"),
    ActionSubmit.Request.Method

@Generated
object PatchEnumValue : EnumValue("patch"),
    ActionSubmit.Request.Method

@Generated
object DeleteEnumValue : EnumValue("delete"),
    ActionSubmit.Request.Method

@Generated
object HeadEnumValue : EnumValue("head"),
    ActionSubmit.Request.Method

@Generated
object OptionsEnumValue : EnumValue("options"),
    ActionSubmit.Request.Method

@Generated
object StartEnumValue : EnumValue("start"),
    ActionTimer.Action,
    ActionVideo.Action,
    AlignmentHorizontal,
    ContentAlignmentHorizontal,
    Gallery.CrossContentAlignment,
    Pager.ScrollAxisAlignment,
    Text.Truncate

@Generated
object StopEnumValue : EnumValue("stop"),
    ActionTimer.Action

@Generated
object PauseEnumValue : EnumValue("pause"),
    ActionTimer.Action,
    ActionVideo.Action

@Generated
object ResumeEnumValue : EnumValue("resume"),
    ActionTimer.Action

@Generated
object CancelEnumValue : EnumValue("cancel"),
    ActionTimer.Action

@Generated
object ResetEnumValue : EnumValue("reset"),
    ActionTimer.Action

@Generated
object LeftEnumValue : EnumValue("left"),
    AlignmentHorizontal,
    ContentAlignmentHorizontal,
    SlideTransition.Edge,
    Tooltip.Position

@Generated
object CenterEnumValue : EnumValue("center"),
    AlignmentHorizontal,
    AlignmentVertical,
    ContentAlignmentHorizontal,
    ContentAlignmentVertical,
    Gallery.CrossContentAlignment,
    Pager.ScrollAxisAlignment,
    TextAlignmentVertical,
    Tooltip.Position

@Generated
object RightEnumValue : EnumValue("right"),
    AlignmentHorizontal,
    ContentAlignmentHorizontal,
    SlideTransition.Edge,
    Tooltip.Position

@Generated
object EndEnumValue : EnumValue("end"),
    AlignmentHorizontal,
    ContentAlignmentHorizontal,
    Gallery.CrossContentAlignment,
    Pager.ScrollAxisAlignment,
    Text.Truncate

@Generated
object TopEnumValue : EnumValue("top"),
    AlignmentVertical,
    ContentAlignmentVertical,
    SlideTransition.Edge,
    TextAlignmentVertical,
    Tooltip.Position

@Generated
object BottomEnumValue : EnumValue("bottom"),
    AlignmentVertical,
    ContentAlignmentVertical,
    SlideTransition.Edge,
    TextAlignmentVertical,
    Tooltip.Position

@Generated
object BaselineEnumValue : EnumValue("baseline"),
    AlignmentVertical,
    ContentAlignmentVertical,
    TextAlignmentVertical

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
object NormalEnumValue : EnumValue("normal"),
    AnimationDirection,
    Text.Image.IndexingDirection

@Generated
object ReverseEnumValue : EnumValue("reverse"),
    AnimationDirection

@Generated
object AlternateEnumValue : EnumValue("alternate"),
    AnimationDirection

@Generated
object AlternateReverseEnumValue : EnumValue("alternate_reverse"),
    AnimationDirection

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
object SpaceBetweenEnumValue : EnumValue("space-between"),
    ContentAlignmentHorizontal,
    ContentAlignmentVertical

@Generated
object SpaceAroundEnumValue : EnumValue("space-around"),
    ContentAlignmentHorizontal,
    ContentAlignmentVertical

@Generated
object SpaceEvenlyEnumValue : EnumValue("space-evenly"),
    ContentAlignmentHorizontal,
    ContentAlignmentVertical

@Generated
object StringEnumValue : EnumValue("string"),
    EvaluableType

@Generated
object IntegerEnumValue : EnumValue("integer"),
    EvaluableType

@Generated
object NumberEnumValue : EnumValue("number"),
    EvaluableType,
    Input.KeyboardType

@Generated
object BooleanEnumValue : EnumValue("boolean"),
    EvaluableType

@Generated
object DatetimeEnumValue : EnumValue("datetime"),
    EvaluableType

@Generated
object ColorEnumValue : EnumValue("color"),
    EvaluableType

@Generated
object UrlEnumValue : EnumValue("url"),
    EvaluableType

@Generated
object DictEnumValue : EnumValue("dict"),
    EvaluableType

@Generated
object ArrayEnumValue : EnumValue("array"),
    EvaluableType

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
object PagingEnumValue : EnumValue("paging"),
    Gallery.ScrollMode

@Generated
object FillEnumValue : EnumValue("fill"),
    ImageScale,
    VideoScale

@Generated
object NoScaleEnumValue : EnumValue("no_scale"),
    ImageScale,
    VideoScale

@Generated
object FitEnumValue : EnumValue("fit"),
    ImageScale,
    VideoScale

@Generated
object StretchEnumValue : EnumValue("stretch"),
    ImageScale

@Generated
object WormEnumValue : EnumValue("worm"),
    Indicator.Animation

@Generated
object SliderEnumValue : EnumValue("slider"),
    Indicator.Animation

@Generated
object WordsEnumValue : EnumValue("words"),
    Input.Autocapitalization

@Generated
object SentencesEnumValue : EnumValue("sentences"),
    Input.Autocapitalization

@Generated
object AllCharactersEnumValue : EnumValue("all_characters"),
    Input.Autocapitalization

@Generated
object GoEnumValue : EnumValue("go"),
    Input.EnterKeyType

@Generated
object SearchEnumValue : EnumValue("search"),
    Input.EnterKeyType

@Generated
object SendEnumValue : EnumValue("send"),
    Input.EnterKeyType

@Generated
object DoneEnumValue : EnumValue("done"),
    Input.EnterKeyType

@Generated
object SingleLineTextEnumValue : EnumValue("single_line_text"),
    Input.KeyboardType

@Generated
object MultiLineTextEnumValue : EnumValue("multi_line_text"),
    Input.KeyboardType

@Generated
object PhoneEnumValue : EnumValue("phone"),
    Input.KeyboardType

@Generated
object EmailEnumValue : EnumValue("email"),
    Input.KeyboardType

@Generated
object UriEnumValue : EnumValue("uri"),
    Input.KeyboardType

@Generated
object PasswordEnumValue : EnumValue("password"),
    Input.KeyboardType

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
object ReversedEnumValue : EnumValue("reversed"),
    Text.Image.IndexingDirection

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
val DivScope.select: SelectEnumValue
    get() = SelectEnumValue

@Generated
val DivScope.checkbox: CheckboxEnumValue
    get() = CheckboxEnumValue

@Generated
val DivScope.radio: RadioEnumValue
    get() = RadioEnumValue

@Generated
val DivScope.auto: AutoEnumValue
    get() = AutoEnumValue

@Generated
val DivScope.self: SelfEnumValue
    get() = SelfEnumValue

@Generated
val DivScope.blank: BlankEnumValue
    get() = BlankEnumValue

@Generated
val DivScope.clamp: ClampEnumValue
    get() = ClampEnumValue

@Generated
val DivScope.ring: RingEnumValue
    get() = RingEnumValue

@Generated
val DivScope.get: GetEnumValue
    get() = GetEnumValue

@Generated
val DivScope.post: PostEnumValue
    get() = PostEnumValue

@Generated
val DivScope.put: PutEnumValue
    get() = PutEnumValue

@Generated
val DivScope.patch: PatchEnumValue
    get() = PatchEnumValue

@Generated
val DivScope.delete: DeleteEnumValue
    get() = DeleteEnumValue

@Generated
val DivScope.head: HeadEnumValue
    get() = HeadEnumValue

@Generated
val DivScope.options: OptionsEnumValue
    get() = OptionsEnumValue

@Generated
val DivScope.start: StartEnumValue
    get() = StartEnumValue

@Generated
val DivScope.stop: StopEnumValue
    get() = StopEnumValue

@Generated
val DivScope.pause: PauseEnumValue
    get() = PauseEnumValue

@Generated
val DivScope.resume: ResumeEnumValue
    get() = ResumeEnumValue

@Generated
val DivScope.cancel: CancelEnumValue
    get() = CancelEnumValue

@Generated
val DivScope.reset: ResetEnumValue
    get() = ResetEnumValue

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
val DivScope.end: EndEnumValue
    get() = EndEnumValue

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
val DivScope.normal: NormalEnumValue
    get() = NormalEnumValue

@Generated
val DivScope.reverse: ReverseEnumValue
    get() = ReverseEnumValue

@Generated
val DivScope.alternate: AlternateEnumValue
    get() = AlternateEnumValue

@Generated
val DivScope.alternate_reverse: AlternateReverseEnumValue
    get() = AlternateReverseEnumValue

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
val DivScope.space_between: SpaceBetweenEnumValue
    get() = SpaceBetweenEnumValue

@Generated
val DivScope.space_around: SpaceAroundEnumValue
    get() = SpaceAroundEnumValue

@Generated
val DivScope.space_evenly: SpaceEvenlyEnumValue
    get() = SpaceEvenlyEnumValue

@Generated
val DivScope.string: StringEnumValue
    get() = StringEnumValue

@Generated
val DivScope.integer: IntegerEnumValue
    get() = IntegerEnumValue

@Generated
val DivScope.number: NumberEnumValue
    get() = NumberEnumValue

@Generated
val DivScope.boolean: BooleanEnumValue
    get() = BooleanEnumValue

@Generated
val DivScope.datetime: DatetimeEnumValue
    get() = DatetimeEnumValue

@Generated
val DivScope.color: ColorEnumValue
    get() = ColorEnumValue

@Generated
val DivScope.url: UrlEnumValue
    get() = UrlEnumValue

@Generated
val DivScope.dict: DictEnumValue
    get() = DictEnumValue

@Generated
val DivScope.array: ArrayEnumValue
    get() = ArrayEnumValue

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
val DivScope.stretch: StretchEnumValue
    get() = StretchEnumValue

@Generated
val DivScope.worm: WormEnumValue
    get() = WormEnumValue

@Generated
val DivScope.slider: SliderEnumValue
    get() = SliderEnumValue

@Generated
val DivScope.words: WordsEnumValue
    get() = WordsEnumValue

@Generated
val DivScope.sentences: SentencesEnumValue
    get() = SentencesEnumValue

@Generated
val DivScope.all_characters: AllCharactersEnumValue
    get() = AllCharactersEnumValue

@Generated
val DivScope.go: GoEnumValue
    get() = GoEnumValue

@Generated
val DivScope.search: SearchEnumValue
    get() = SearchEnumValue

@Generated
val DivScope.send: SendEnumValue
    get() = SendEnumValue

@Generated
val DivScope.done: DoneEnumValue
    get() = DoneEnumValue

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
val DivScope.email: EmailEnumValue
    get() = EmailEnumValue

@Generated
val DivScope.uri: UriEnumValue
    get() = UriEnumValue

@Generated
val DivScope.password: PasswordEnumValue
    get() = PasswordEnumValue

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
val DivScope.reversed: ReversedEnumValue
    get() = ReversedEnumValue

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
