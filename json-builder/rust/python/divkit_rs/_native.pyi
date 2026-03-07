# Generated code. Do not modify.

from __future__ import annotations

import enum
from typing import Any, ClassVar, TypeAlias


class PyDivEntity:
    template_name: ClassVar[str]

    def __init__(self, **kwargs: Any) -> None: ...
    def dict(self) -> dict[str, Any]: ...
    def build(self) -> dict[str, Any]: ...
    def related_templates(self) -> set[type[PyDivEntity]]: ...
    def schema(self, exclude_fields: list[str] | None = None) -> dict[str, Any]: ...
    @classmethod
    def template(cls) -> dict[str, Any]: ...
    @classmethod
    def update_forward_refs(cls) -> None: ...


class PyDivData:
    def __init__(self, log_id: str, states: list[PyDivDataState]) -> None: ...
    def dict(self) -> dict[str, Any]: ...
    def build(self) -> dict[str, Any]: ...


class PyDivDataState:
    def __init__(self, state_id: int, div: PyDivEntity) -> None: ...


def make_div(div: PyDivEntity) -> dict[str, Any]: ...
def make_card(log_id: str, div: PyDivEntity) -> dict[str, Any]: ...
def compat_dump(value: Any) -> Any: ...
def normalize_pydivkit_json(value: Any) -> Any: ...
def register_type_meta(
    class_name: str,
    type_name: str | None,
    field_names: list[str],
    required_fields: list[str],
) -> None: ...


class AccessibilityType(str, enum.Enum):
    NONE = 'none'
    BUTTON = 'button'
    IMAGE = 'image'
    TEXT = 'text'
    AUTO = 'auto'

class DelimiterStyleOrientation(str, enum.Enum):
    VERTICAL = 'vertical'
    HORIZONTAL = 'horizontal'

class DivAccessibilityMode(str, enum.Enum):
    DEFAULT = 'default'
    MERGE = 'merge'
    EXCLUDE = 'exclude'

class DivAccessibilityType(str, enum.Enum):
    NONE = 'none'
    BUTTON = 'button'
    IMAGE = 'image'
    TEXT = 'text'
    EDIT_TEXT = 'edit_text'
    HEADER = 'header'
    TAB_BAR = 'tab_bar'
    LIST = 'list'
    SELECT = 'select'
    CHECKBOX = 'checkbox'
    RADIO = 'radio'
    AUTO = 'auto'

class DivActionScrollByOverflow(str, enum.Enum):
    CLAMP = 'clamp'
    RING = 'ring'

class DivActionTarget(str, enum.Enum):
    SELF = '_self'
    BLANK = '_blank'

class DivActionTimerAction(str, enum.Enum):
    START = 'start'
    STOP = 'stop'
    PAUSE = 'pause'
    RESUME = 'resume'
    CANCEL = 'cancel'
    RESET = 'reset'

class DivActionVideoAction(str, enum.Enum):
    START = 'start'
    PAUSE = 'pause'

class DivAlignmentHorizontal(str, enum.Enum):
    LEFT = 'left'
    CENTER = 'center'
    RIGHT = 'right'
    START = 'start'
    END = 'end'

class DivAlignmentVertical(str, enum.Enum):
    TOP = 'top'
    CENTER = 'center'
    BOTTOM = 'bottom'
    BASELINE = 'baseline'

class DivAnimationDirection(str, enum.Enum):
    NORMAL = 'normal'
    REVERSE = 'reverse'
    ALTERNATE = 'alternate'
    ALTERNATE_REVERSE = 'alternate_reverse'

class DivAnimationInterpolator(str, enum.Enum):
    LINEAR = 'linear'
    EASE = 'ease'
    EASE_IN = 'ease_in'
    EASE_OUT = 'ease_out'
    EASE_IN_OUT = 'ease_in_out'
    SPRING = 'spring'

class DivAnimationName(str, enum.Enum):
    FADE = 'fade'
    TRANSLATE = 'translate'
    SCALE = 'scale'
    NATIVE = 'native'
    SET = 'set'
    NO_ANIMATION = 'no_animation'

class DivBlendMode(str, enum.Enum):
    SOURCE_IN = 'source_in'
    SOURCE_ATOP = 'source_atop'
    DARKEN = 'darken'
    LIGHTEN = 'lighten'
    MULTIPLY = 'multiply'
    SCREEN = 'screen'

class DivContainerLayoutMode(str, enum.Enum):
    NO_WRAP = 'no_wrap'
    WRAP = 'wrap'

class DivContainerOrientation(str, enum.Enum):
    VERTICAL = 'vertical'
    HORIZONTAL = 'horizontal'
    OVERLAP = 'overlap'

class DivContentAlignmentHorizontal(str, enum.Enum):
    LEFT = 'left'
    CENTER = 'center'
    RIGHT = 'right'
    START = 'start'
    END = 'end'
    SPACE_BETWEEN = 'space-between'
    SPACE_AROUND = 'space-around'
    SPACE_EVENLY = 'space-evenly'

class DivContentAlignmentVertical(str, enum.Enum):
    TOP = 'top'
    CENTER = 'center'
    BOTTOM = 'bottom'
    BASELINE = 'baseline'
    SPACE_BETWEEN = 'space-between'
    SPACE_AROUND = 'space-around'
    SPACE_EVENLY = 'space-evenly'

class DivEvaluableType(str, enum.Enum):
    STRING = 'string'
    INTEGER = 'integer'
    NUMBER = 'number'
    BOOLEAN = 'boolean'
    DATETIME = 'datetime'
    COLOR = 'color'
    URL = 'url'
    DICT = 'dict'
    ARRAY = 'array'

class DivFontWeight(str, enum.Enum):
    LIGHT = 'light'
    MEDIUM = 'medium'
    REGULAR = 'regular'
    BOLD = 'bold'

class DivGalleryCrossContentAlignment(str, enum.Enum):
    START = 'start'
    CENTER = 'center'
    END = 'end'

class DivGalleryOrientation(str, enum.Enum):
    HORIZONTAL = 'horizontal'
    VERTICAL = 'vertical'

class DivGalleryScrollMode(str, enum.Enum):
    PAGING = 'paging'
    DEFAULT = 'default'

class DivGalleryScrollbar(str, enum.Enum):
    NONE = 'none'
    AUTO = 'auto'

class DivImageScale(str, enum.Enum):
    FILL = 'fill'
    NO_SCALE = 'no_scale'
    FIT = 'fit'
    STRETCH = 'stretch'

class DivIndicatorAnimation(str, enum.Enum):
    SCALE = 'scale'
    WORM = 'worm'
    SLIDER = 'slider'

class DivInputAutocapitalization(str, enum.Enum):
    AUTO = 'auto'
    NONE = 'none'
    WORDS = 'words'
    SENTENCES = 'sentences'
    ALL_CHARACTERS = 'all_characters'

class DivInputEnterKeyType(str, enum.Enum):
    DEFAULT = 'default'
    GO = 'go'
    SEARCH = 'search'
    SEND = 'send'
    DONE = 'done'

class DivInputKeyboardType(str, enum.Enum):
    SINGLE_LINE_TEXT = 'single_line_text'
    MULTI_LINE_TEXT = 'multi_line_text'
    PHONE = 'phone'
    NUMBER = 'number'
    EMAIL = 'email'
    URI = 'uri'
    PASSWORD = 'password'

class DivLineStyle(str, enum.Enum):
    NONE = 'none'
    SINGLE = 'single'

class DivPagerItemAlignment(str, enum.Enum):
    START = 'start'
    CENTER = 'center'
    END = 'end'

class DivPagerOrientation(str, enum.Enum):
    HORIZONTAL = 'horizontal'
    VERTICAL = 'vertical'

class DivPatchMode(str, enum.Enum):
    TRANSACTIONAL = 'transactional'
    PARTIAL = 'partial'

class DivRadialGradientRelativeRadiusValue(str, enum.Enum):
    NEAREST_CORNER = 'nearest_corner'
    FARTHEST_CORNER = 'farthest_corner'
    NEAREST_SIDE = 'nearest_side'
    FARTHEST_SIDE = 'farthest_side'

class DivSizeUnit(str, enum.Enum):
    DP = 'dp'
    SP = 'sp'
    PX = 'px'

class DivSlideTransitionEdge(str, enum.Enum):
    LEFT = 'left'
    TOP = 'top'
    RIGHT = 'right'
    BOTTOM = 'bottom'

class DivTextAlignmentVertical(str, enum.Enum):
    TOP = 'top'
    CENTER = 'center'
    BOTTOM = 'bottom'
    BASELINE = 'baseline'

class DivTextTruncate(str, enum.Enum):
    NONE = 'none'
    START = 'start'
    END = 'end'
    MIDDLE = 'middle'

class DivTooltipPosition(str, enum.Enum):
    LEFT = 'left'
    TOP_LEFT = 'top-left'
    TOP = 'top'
    TOP_RIGHT = 'top-right'
    RIGHT = 'right'
    BOTTOM_RIGHT = 'bottom-right'
    BOTTOM = 'bottom'
    BOTTOM_LEFT = 'bottom-left'
    CENTER = 'center'

class DivTransitionSelector(str, enum.Enum):
    NONE = 'none'
    DATA_CHANGE = 'data_change'
    STATE_CHANGE = 'state_change'
    ANY_CHANGE = 'any_change'

class DivTransitionTrigger(str, enum.Enum):
    DATA_CHANGE = 'data_change'
    STATE_CHANGE = 'state_change'
    VISIBILITY_CHANGE = 'visibility_change'

class DivTriggerMode(str, enum.Enum):
    ON_CONDITION = 'on_condition'
    ON_VARIABLE = 'on_variable'

class DivVideoScale(str, enum.Enum):
    FILL = 'fill'
    NO_SCALE = 'no_scale'
    FIT = 'fit'

class DivVisibility(str, enum.Enum):
    VISIBLE = 'visible'
    INVISIBLE = 'invisible'
    GONE = 'gone'

class ImageIndexingDirection(str, enum.Enum):
    NORMAL = 'normal'
    REVERSED = 'reversed'

class RequestMethod(str, enum.Enum):
    GET = 'get'
    POST = 'post'
    PUT = 'put'
    PATCH = 'patch'
    DELETE = 'delete'
    HEAD = 'head'
    OPTIONS = 'options'

class TabTitleStyleAnimationType(str, enum.Enum):
    SLIDE = 'slide'
    FADE = 'fade'
    NONE = 'none'


class ArrayValue(PyDivEntity):
    def __init__(
        self,
        *,
        value: list[Any] | str,
    ) -> None: ...

class ArrayVariable(PyDivEntity):
    def __init__(
        self,
        *,
        name: str,
        value: list[Any] | str,
    ) -> None: ...

class BooleanValue(PyDivEntity):
    def __init__(
        self,
        *,
        value: bool | str,
    ) -> None: ...

class BooleanVariable(PyDivEntity):
    def __init__(
        self,
        *,
        name: str,
        value: bool | int | str,
    ) -> None: ...

class ColorValue(PyDivEntity):
    def __init__(
        self,
        *,
        value: str,
    ) -> None: ...

class ColorVariable(PyDivEntity):
    def __init__(
        self,
        *,
        name: str,
        value: str,
    ) -> None: ...

class ContentText(PyDivEntity):
    def __init__(
        self,
        *,
        value: str,
    ) -> None: ...

class ContentUrl(PyDivEntity):
    def __init__(
        self,
        *,
        value: str,
    ) -> None: ...

class DictValue(PyDivEntity):
    def __init__(
        self,
        *,
        value: dict[str, Any] | str,
    ) -> None: ...

class DictVariable(PyDivEntity):
    def __init__(
        self,
        *,
        name: str,
        value: dict[str, Any] | str,
    ) -> None: ...

class DivAbsoluteEdgeInsets(PyDivEntity):
    def __init__(
        self,
        *,
        bottom: int | str | None = None,
        left: int | str | None = None,
        right: int | str | None = None,
        top: int | str | None = None,
    ) -> None: ...

class DivAccessibility(PyDivEntity):
    def __init__(
        self,
        *,
        description: str | None = None,
        hint: str | None = None,
        is_checked: bool | int | str | None = None,
        mode: DivAccessibilityMode | str | None = None,
        mute_after_action: bool | int | str | None = None,
        state_description: str | None = None,
        type: DivAccessibilityType | str | None = None,
    ) -> None: ...

class DivAction(PyDivEntity):
    def __init__(
        self,
        *,
        log_id: str,
        download_callbacks: DivDownloadCallbacks | None = None,
        is_enabled: bool | int | str | None = None,
        log_url: str | None = None,
        menu_items: list[DivActionMenuItem] | None = None,
        payload: dict[str, Any] | None = None,
        referer: str | None = None,
        scope_id: str | None = None,
        target: DivActionTarget | str | None = None,
        typed: DivActionTyped | None = None,
        url: str | None = None,
    ) -> None: ...

class DivActionAnimatorStart(PyDivEntity):
    def __init__(
        self,
        *,
        animator_id: str,
        direction: DivAnimationDirection | str | None = None,
        duration: int | str | None = None,
        end_value: DivTypedValue | None = None,
        interpolator: DivAnimationInterpolator | str | None = None,
        repeat_count: DivCount | None = None,
        start_delay: int | str | None = None,
        start_value: DivTypedValue | None = None,
    ) -> None: ...

class DivActionAnimatorStop(PyDivEntity):
    def __init__(
        self,
        *,
        animator_id: str,
    ) -> None: ...

class DivActionArrayInsertValue(PyDivEntity):
    def __init__(
        self,
        *,
        value: DivTypedValue,
        variable_name: str,
        index: int | str | None = None,
    ) -> None: ...

class DivActionArrayRemoveValue(PyDivEntity):
    def __init__(
        self,
        *,
        index: int | str,
        variable_name: str,
    ) -> None: ...

class DivActionArraySetValue(PyDivEntity):
    def __init__(
        self,
        *,
        index: int | str,
        value: DivTypedValue,
        variable_name: str,
    ) -> None: ...

class DivActionClearFocus(PyDivEntity):
    def __init__(
        self,
    ) -> None: ...

class DivActionCopyToClipboard(PyDivEntity):
    def __init__(
        self,
        *,
        content: DivActionCopyToClipboardContent,
    ) -> None: ...

class DivActionCustom(PyDivEntity):
    def __init__(
        self,
    ) -> None: ...

class DivActionDictSetValue(PyDivEntity):
    def __init__(
        self,
        *,
        key: str,
        variable_name: str,
        value: DivTypedValue | None = None,
    ) -> None: ...

class DivActionDownload(PyDivEntity):
    def __init__(
        self,
        *,
        url: str,
        on_fail_actions: list[DivAction] | None = None,
        on_success_actions: list[DivAction] | None = None,
    ) -> None: ...

class DivActionFocusElement(PyDivEntity):
    def __init__(
        self,
        *,
        element_id: str,
    ) -> None: ...

class DivActionHideTooltip(PyDivEntity):
    def __init__(
        self,
        *,
        id: str,
    ) -> None: ...

class DivActionMenuItem(PyDivEntity):
    def __init__(
        self,
        *,
        text: str,
        action: DivAction | None = None,
        actions: list[DivAction] | None = None,
    ) -> None: ...

class DivActionScrollBy(PyDivEntity):
    def __init__(
        self,
        *,
        id: str,
        animated: bool | str | None = None,
        item_count: int | str | None = None,
        offset: int | str | None = None,
        overflow: DivActionScrollByOverflow | str | None = None,
    ) -> None: ...

class DivActionScrollTo(PyDivEntity):
    def __init__(
        self,
        *,
        destination: DivActionScrollDestination,
        id: str,
        animated: bool | str | None = None,
    ) -> None: ...

class DivActionSetState(PyDivEntity):
    def __init__(
        self,
        *,
        state_id: str,
        temporary: bool | str | None = None,
    ) -> None: ...

class DivActionSetStoredValue(PyDivEntity):
    def __init__(
        self,
        *,
        lifetime: int | str,
        name: str,
        value: DivTypedValue,
    ) -> None: ...

class DivActionSetVariable(PyDivEntity):
    def __init__(
        self,
        *,
        value: DivTypedValue,
        variable_name: str,
    ) -> None: ...

class DivActionShowTooltip(PyDivEntity):
    def __init__(
        self,
        *,
        id: str,
        multiple: bool | str | None = None,
    ) -> None: ...

class DivActionSubmit(PyDivEntity):
    def __init__(
        self,
        *,
        container_id: str,
        request: DivActionSubmitRequest,
        on_fail_actions: list[DivAction] | None = None,
        on_success_actions: list[DivAction] | None = None,
    ) -> None: ...

class DivActionSubmitRequest(PyDivEntity):
    def __init__(
        self,
        *,
        url: str,
        headers: list[RequestHeader] | None = None,
        method: RequestMethod | str | None = None,
    ) -> None: ...

class DivActionTimer(PyDivEntity):
    def __init__(
        self,
        *,
        action: DivActionTimerAction | str,
        id: str,
    ) -> None: ...

class DivActionUpdateStructure(PyDivEntity):
    def __init__(
        self,
        *,
        path: str,
        value: DivTypedValue,
        variable_name: str,
    ) -> None: ...

class DivActionVideo(PyDivEntity):
    def __init__(
        self,
        *,
        action: DivActionVideoAction | str,
        id: str,
    ) -> None: ...

class DivAnimation(PyDivEntity):
    def __init__(
        self,
        *,
        name: DivAnimationName | str,
        duration: int | str | None = None,
        end_value: float | str | None = None,
        interpolator: DivAnimationInterpolator | str | None = None,
        items: list[DivAnimation] | None = None,
        repeat: DivCount | None = None,
        start_delay: int | str | None = None,
        start_value: float | str | None = None,
    ) -> None: ...

class DivAnimatorBase(PyDivEntity):
    def __init__(
        self,
        *,
        duration: int | str,
        id: str,
        variable_name: str,
        cancel_actions: list[DivAction] | None = None,
        direction: DivAnimationDirection | str | None = None,
        end_actions: list[DivAction] | None = None,
        interpolator: DivAnimationInterpolator | str | None = None,
        repeat_count: DivCount | None = None,
        start_delay: int | str | None = None,
    ) -> None: ...

class DivAppearanceSetTransition(PyDivEntity):
    def __init__(
        self,
        *,
        items: list[DivAppearanceTransition],
    ) -> None: ...

class DivAspect(PyDivEntity):
    def __init__(
        self,
        *,
        ratio: float | str,
    ) -> None: ...

class DivBase(PyDivEntity):
    def __init__(
        self,
        *,
        accessibility: DivAccessibility | None = None,
        alignment_horizontal: DivAlignmentHorizontal | str | None = None,
        alignment_vertical: DivAlignmentVertical | str | None = None,
        alpha: float | str | None = None,
        animators: list[DivAnimator] | None = None,
        background: list[DivBackground] | None = None,
        border: DivBorder | None = None,
        column_span: int | str | None = None,
        disappear_actions: list[DivDisappearAction] | None = None,
        extensions: list[DivExtension] | None = None,
        focus: DivFocus | None = None,
        functions: list[DivFunction] | None = None,
        height: DivSize | None = None,
        id: str | None = None,
        layout_provider: DivLayoutProvider | None = None,
        margins: DivEdgeInsets | None = None,
        paddings: DivEdgeInsets | None = None,
        reuse_id: str | None = None,
        row_span: int | str | None = None,
        selected_actions: list[DivAction] | None = None,
        tooltips: list[DivTooltip] | None = None,
        transform: DivTransform | None = None,
        transformations: list[DivTransformation] | None = None,
        transition_change: DivChangeTransition | None = None,
        transition_in: DivAppearanceTransition | None = None,
        transition_out: DivAppearanceTransition | None = None,
        transition_triggers: list[DivTransitionTrigger | str] | None = None,
        variable_triggers: list[DivTrigger] | None = None,
        variables: list[DivVariable] | None = None,
        visibility: DivVisibility | str | None = None,
        visibility_action: DivVisibilityAction | None = None,
        visibility_actions: list[DivVisibilityAction] | None = None,
        width: DivSize | None = None,
    ) -> None: ...

class DivBlur(PyDivEntity):
    def __init__(
        self,
        *,
        radius: int | str,
    ) -> None: ...

class DivBorder(PyDivEntity):
    def __init__(
        self,
        *,
        corner_radius: int | str | None = None,
        corners_radius: DivCornersRadius | None = None,
        has_shadow: bool | int | str | None = None,
        shadow: DivShadow | None = None,
        stroke: DivStroke | None = None,
    ) -> None: ...

class DivChangeBoundsTransition(PyDivEntity):
    def __init__(
        self,
        *,
        duration: int | str | None = None,
        interpolator: DivAnimationInterpolator | str | None = None,
        start_delay: int | str | None = None,
    ) -> None: ...

class DivChangeSetTransition(PyDivEntity):
    def __init__(
        self,
        *,
        items: list[DivChangeTransition],
    ) -> None: ...

class DivCircleShape(PyDivEntity):
    def __init__(
        self,
        *,
        background_color: str | None = None,
        radius: DivFixedSize | None = None,
        stroke: DivStroke | None = None,
    ) -> None: ...

class DivCloudBackground(PyDivEntity):
    def __init__(
        self,
        *,
        color: str,
        corner_radius: int | str,
        paddings: DivEdgeInsets | None = None,
    ) -> None: ...

class DivCollectionItemBuilder(PyDivEntity):
    def __init__(
        self,
        *,
        data: list[Any] | str,
        prototypes: list[DivCollectionItemBuilderPrototype],
        data_element_name: str | None = None,
    ) -> None: ...

class DivCollectionItemBuilderPrototype(PyDivEntity):
    def __init__(
        self,
        *,
        div: Div,
        id: str | None = None,
        selector: bool | int | str | None = None,
    ) -> None: ...

class DivColorAnimator(PyDivEntity):
    def __init__(
        self,
        *,
        duration: int | str,
        end_value: str,
        id: str,
        variable_name: str,
        cancel_actions: list[DivAction] | None = None,
        direction: DivAnimationDirection | str | None = None,
        end_actions: list[DivAction] | None = None,
        interpolator: DivAnimationInterpolator | str | None = None,
        repeat_count: DivCount | None = None,
        start_delay: int | str | None = None,
        start_value: str | None = None,
    ) -> None: ...

class DivContainer(PyDivEntity):
    def __init__(
        self,
        *,
        accessibility: DivAccessibility | None = None,
        action: DivAction | None = None,
        action_animation: DivAnimation | None = None,
        actions: list[DivAction] | None = None,
        alignment_horizontal: DivAlignmentHorizontal | str | None = None,
        alignment_vertical: DivAlignmentVertical | str | None = None,
        alpha: float | str | None = None,
        animators: list[DivAnimator] | None = None,
        aspect: DivAspect | None = None,
        background: list[DivBackground] | None = None,
        border: DivBorder | None = None,
        capture_focus_on_action: bool | str | None = None,
        clip_to_bounds: bool | int | str | None = None,
        column_span: int | str | None = None,
        content_alignment_horizontal: DivContentAlignmentHorizontal | str | None = None,
        content_alignment_vertical: DivContentAlignmentVertical | str | None = None,
        disappear_actions: list[DivDisappearAction] | None = None,
        doubletap_actions: list[DivAction] | None = None,
        extensions: list[DivExtension] | None = None,
        focus: DivFocus | None = None,
        functions: list[DivFunction] | None = None,
        height: DivSize | None = None,
        hover_end_actions: list[DivAction] | None = None,
        hover_start_actions: list[DivAction] | None = None,
        id: str | None = None,
        item_builder: DivCollectionItemBuilder | None = None,
        item_spacing: int | str | None = None,
        items: list[Div] | None = None,
        layout_mode: DivContainerLayoutMode | str | None = None,
        layout_provider: DivLayoutProvider | None = None,
        line_separator: DivContainerSeparator | None = None,
        line_spacing: int | str | None = None,
        longtap_actions: list[DivAction] | None = None,
        margins: DivEdgeInsets | None = None,
        orientation: DivContainerOrientation | str | None = None,
        paddings: DivEdgeInsets | None = None,
        press_end_actions: list[DivAction] | None = None,
        press_start_actions: list[DivAction] | None = None,
        reuse_id: str | None = None,
        row_span: int | str | None = None,
        selected_actions: list[DivAction] | None = None,
        separator: DivContainerSeparator | None = None,
        tooltips: list[DivTooltip] | None = None,
        transform: DivTransform | None = None,
        transformations: list[DivTransformation] | None = None,
        transition_change: DivChangeTransition | None = None,
        transition_in: DivAppearanceTransition | None = None,
        transition_out: DivAppearanceTransition | None = None,
        transition_triggers: list[DivTransitionTrigger | str] | None = None,
        variable_triggers: list[DivTrigger] | None = None,
        variables: list[DivVariable] | None = None,
        visibility: DivVisibility | str | None = None,
        visibility_action: DivVisibilityAction | None = None,
        visibility_actions: list[DivVisibilityAction] | None = None,
        width: DivSize | None = None,
    ) -> None: ...

class DivContainerSeparator(PyDivEntity):
    def __init__(
        self,
        *,
        style: DivDrawable,
        margins: DivEdgeInsets | None = None,
        show_at_end: bool | int | str | None = None,
        show_at_start: bool | int | str | None = None,
        show_between: bool | int | str | None = None,
    ) -> None: ...

class DivCornersRadius(PyDivEntity):
    def __init__(
        self,
        *,
        bottom_left: int | str | None = None,
        bottom_right: int | str | None = None,
        top_left: int | str | None = None,
        top_right: int | str | None = None,
    ) -> None: ...

class DivCurrencyInputMask(PyDivEntity):
    def __init__(
        self,
        *,
        raw_text_variable: str,
        locale: str | None = None,
    ) -> None: ...

class DivCustom(PyDivEntity):
    def __init__(
        self,
        *,
        custom_type: str,
        accessibility: DivAccessibility | None = None,
        alignment_horizontal: DivAlignmentHorizontal | str | None = None,
        alignment_vertical: DivAlignmentVertical | str | None = None,
        alpha: float | str | None = None,
        animators: list[DivAnimator] | None = None,
        background: list[DivBackground] | None = None,
        border: DivBorder | None = None,
        column_span: int | str | None = None,
        custom_props: dict[str, Any] | None = None,
        disappear_actions: list[DivDisappearAction] | None = None,
        extensions: list[DivExtension] | None = None,
        focus: DivFocus | None = None,
        functions: list[DivFunction] | None = None,
        height: DivSize | None = None,
        id: str | None = None,
        items: list[Div] | None = None,
        layout_provider: DivLayoutProvider | None = None,
        margins: DivEdgeInsets | None = None,
        paddings: DivEdgeInsets | None = None,
        reuse_id: str | None = None,
        row_span: int | str | None = None,
        selected_actions: list[DivAction] | None = None,
        tooltips: list[DivTooltip] | None = None,
        transform: DivTransform | None = None,
        transformations: list[DivTransformation] | None = None,
        transition_change: DivChangeTransition | None = None,
        transition_in: DivAppearanceTransition | None = None,
        transition_out: DivAppearanceTransition | None = None,
        transition_triggers: list[DivTransitionTrigger | str] | None = None,
        variable_triggers: list[DivTrigger] | None = None,
        variables: list[DivVariable] | None = None,
        visibility: DivVisibility | str | None = None,
        visibility_action: DivVisibilityAction | None = None,
        visibility_actions: list[DivVisibilityAction] | None = None,
        width: DivSize | None = None,
    ) -> None: ...

class DivData(PyDivEntity):
    def __init__(
        self,
        *,
        log_id: str,
        states: list[DivDataState],
        functions: list[DivFunction] | None = None,
        timers: list[DivTimer] | None = None,
        transition_animation_selector: DivTransitionSelector | str | None = None,
        variable_triggers: list[DivTrigger] | None = None,
        variables: list[DivVariable] | None = None,
    ) -> None: ...

class DivDataState(PyDivEntity):
    def __init__(
        self,
        *,
        div: Div,
        state_id: int,
    ) -> None: ...

class DivDefaultIndicatorItemPlacement(PyDivEntity):
    def __init__(
        self,
        *,
        space_between_centers: DivFixedSize | None = None,
    ) -> None: ...

class DivDimension(PyDivEntity):
    def __init__(
        self,
        *,
        value: float | str,
        unit: DivSizeUnit | str | None = None,
    ) -> None: ...

class DivDisappearAction(PyDivEntity):
    def __init__(
        self,
        *,
        log_id: str,
        disappear_duration: int | str | None = None,
        download_callbacks: DivDownloadCallbacks | None = None,
        is_enabled: bool | int | str | None = None,
        log_limit: int | str | None = None,
        payload: dict[str, Any] | None = None,
        referer: str | None = None,
        scope_id: str | None = None,
        typed: DivActionTyped | None = None,
        url: str | None = None,
        visibility_percentage: int | str | None = None,
    ) -> None: ...

class DivDownloadCallbacks(PyDivEntity):
    def __init__(
        self,
        *,
        on_fail_actions: list[DivAction] | None = None,
        on_success_actions: list[DivAction] | None = None,
    ) -> None: ...

class DivEdgeInsets(PyDivEntity):
    def __init__(
        self,
        *,
        bottom: int | str | None = None,
        end: int | str | None = None,
        left: int | str | None = None,
        right: int | str | None = None,
        start: int | str | None = None,
        top: int | str | None = None,
        unit: DivSizeUnit | str | None = None,
    ) -> None: ...

class DivExtension(PyDivEntity):
    def __init__(
        self,
        *,
        id: str,
        params: dict[str, Any] | None = None,
    ) -> None: ...

class DivFadeTransition(PyDivEntity):
    def __init__(
        self,
        *,
        alpha: float | str | None = None,
        duration: int | str | None = None,
        interpolator: DivAnimationInterpolator | str | None = None,
        start_delay: int | str | None = None,
    ) -> None: ...

class DivFilterRtlMirror(PyDivEntity):
    def __init__(
        self,
    ) -> None: ...

class DivFixedCount(PyDivEntity):
    def __init__(
        self,
        *,
        value: int | str,
    ) -> None: ...

class DivFixedLengthInputMask(PyDivEntity):
    def __init__(
        self,
        *,
        pattern: str,
        pattern_elements: list[DivFixedLengthInputMaskPatternElement],
        raw_text_variable: str,
        always_visible: bool | int | str | None = None,
    ) -> None: ...

class DivFixedLengthInputMaskPatternElement(PyDivEntity):
    def __init__(
        self,
        *,
        key: str,
        placeholder: str | None = None,
        regex: str | None = None,
    ) -> None: ...

class DivFixedSize(PyDivEntity):
    def __init__(
        self,
        *,
        value: int | str,
        unit: DivSizeUnit | str | None = None,
    ) -> None: ...

class DivFixedTranslation(PyDivEntity):
    def __init__(
        self,
        *,
        value: int | str,
        unit: DivSizeUnit | str | None = None,
    ) -> None: ...

class DivFocus(PyDivEntity):
    def __init__(
        self,
        *,
        background: list[DivBackground] | None = None,
        border: DivBorder | None = None,
        next_focus_ids: DivFocusNextFocusIds | None = None,
        on_blur: list[DivAction] | None = None,
        on_focus: list[DivAction] | None = None,
    ) -> None: ...

class DivFocusNextFocusIds(PyDivEntity):
    def __init__(
        self,
        *,
        down: str | None = None,
        forward: str | None = None,
        left: str | None = None,
        right: str | None = None,
        up: str | None = None,
    ) -> None: ...

class DivFunction(PyDivEntity):
    def __init__(
        self,
        *,
        arguments: list[DivFunctionArgument],
        body: str,
        name: str,
        return_type: DivEvaluableType | str,
    ) -> None: ...

class DivFunctionArgument(PyDivEntity):
    def __init__(
        self,
        *,
        name: str,
        type: DivEvaluableType | str,
    ) -> None: ...

class DivGallery(PyDivEntity):
    def __init__(
        self,
        *,
        accessibility: DivAccessibility | None = None,
        alignment_horizontal: DivAlignmentHorizontal | str | None = None,
        alignment_vertical: DivAlignmentVertical | str | None = None,
        alpha: float | str | None = None,
        animators: list[DivAnimator] | None = None,
        background: list[DivBackground] | None = None,
        border: DivBorder | None = None,
        column_count: int | str | None = None,
        column_span: int | str | None = None,
        cross_content_alignment: DivGalleryCrossContentAlignment | str | None = None,
        cross_spacing: int | str | None = None,
        default_item: int | str | None = None,
        disappear_actions: list[DivDisappearAction] | None = None,
        extensions: list[DivExtension] | None = None,
        focus: DivFocus | None = None,
        functions: list[DivFunction] | None = None,
        height: DivSize | None = None,
        id: str | None = None,
        item_builder: DivCollectionItemBuilder | None = None,
        item_spacing: int | str | None = None,
        items: list[Div] | None = None,
        layout_provider: DivLayoutProvider | None = None,
        margins: DivEdgeInsets | None = None,
        orientation: DivGalleryOrientation | str | None = None,
        paddings: DivEdgeInsets | None = None,
        restrict_parent_scroll: bool | int | str | None = None,
        reuse_id: str | None = None,
        row_span: int | str | None = None,
        scroll_mode: DivGalleryScrollMode | str | None = None,
        scrollbar: DivGalleryScrollbar | str | None = None,
        selected_actions: list[DivAction] | None = None,
        tooltips: list[DivTooltip] | None = None,
        transform: DivTransform | None = None,
        transformations: list[DivTransformation] | None = None,
        transition_change: DivChangeTransition | None = None,
        transition_in: DivAppearanceTransition | None = None,
        transition_out: DivAppearanceTransition | None = None,
        transition_triggers: list[DivTransitionTrigger | str] | None = None,
        variable_triggers: list[DivTrigger] | None = None,
        variables: list[DivVariable] | None = None,
        visibility: DivVisibility | str | None = None,
        visibility_action: DivVisibilityAction | None = None,
        visibility_actions: list[DivVisibilityAction] | None = None,
        width: DivSize | None = None,
    ) -> None: ...

class DivGifImage(PyDivEntity):
    def __init__(
        self,
        *,
        gif_url: str,
        accessibility: DivAccessibility | None = None,
        action: DivAction | None = None,
        action_animation: DivAnimation | None = None,
        actions: list[DivAction] | None = None,
        alignment_horizontal: DivAlignmentHorizontal | str | None = None,
        alignment_vertical: DivAlignmentVertical | str | None = None,
        alpha: float | str | None = None,
        animators: list[DivAnimator] | None = None,
        aspect: DivAspect | None = None,
        background: list[DivBackground] | None = None,
        border: DivBorder | None = None,
        capture_focus_on_action: bool | str | None = None,
        column_span: int | str | None = None,
        content_alignment_horizontal: DivAlignmentHorizontal | str | None = None,
        content_alignment_vertical: DivAlignmentVertical | str | None = None,
        disappear_actions: list[DivDisappearAction] | None = None,
        doubletap_actions: list[DivAction] | None = None,
        extensions: list[DivExtension] | None = None,
        focus: DivFocus | None = None,
        functions: list[DivFunction] | None = None,
        height: DivSize | None = None,
        hover_end_actions: list[DivAction] | None = None,
        hover_start_actions: list[DivAction] | None = None,
        id: str | None = None,
        layout_provider: DivLayoutProvider | None = None,
        longtap_actions: list[DivAction] | None = None,
        margins: DivEdgeInsets | None = None,
        paddings: DivEdgeInsets | None = None,
        placeholder_color: str | None = None,
        preload_required: bool | int | str | None = None,
        press_end_actions: list[DivAction] | None = None,
        press_start_actions: list[DivAction] | None = None,
        preview: str | None = None,
        preview_url: str | None = None,
        reuse_id: str | None = None,
        row_span: int | str | None = None,
        scale: DivImageScale | str | None = None,
        selected_actions: list[DivAction] | None = None,
        tooltips: list[DivTooltip] | None = None,
        transform: DivTransform | None = None,
        transformations: list[DivTransformation] | None = None,
        transition_change: DivChangeTransition | None = None,
        transition_in: DivAppearanceTransition | None = None,
        transition_out: DivAppearanceTransition | None = None,
        transition_triggers: list[DivTransitionTrigger | str] | None = None,
        variable_triggers: list[DivTrigger] | None = None,
        variables: list[DivVariable] | None = None,
        visibility: DivVisibility | str | None = None,
        visibility_action: DivVisibilityAction | None = None,
        visibility_actions: list[DivVisibilityAction] | None = None,
        width: DivSize | None = None,
    ) -> None: ...

class DivGrid(PyDivEntity):
    def __init__(
        self,
        *,
        column_count: int | str,
        accessibility: DivAccessibility | None = None,
        action: DivAction | None = None,
        action_animation: DivAnimation | None = None,
        actions: list[DivAction] | None = None,
        alignment_horizontal: DivAlignmentHorizontal | str | None = None,
        alignment_vertical: DivAlignmentVertical | str | None = None,
        alpha: float | str | None = None,
        animators: list[DivAnimator] | None = None,
        background: list[DivBackground] | None = None,
        border: DivBorder | None = None,
        capture_focus_on_action: bool | str | None = None,
        column_span: int | str | None = None,
        content_alignment_horizontal: DivAlignmentHorizontal | str | None = None,
        content_alignment_vertical: DivAlignmentVertical | str | None = None,
        disappear_actions: list[DivDisappearAction] | None = None,
        doubletap_actions: list[DivAction] | None = None,
        extensions: list[DivExtension] | None = None,
        focus: DivFocus | None = None,
        functions: list[DivFunction] | None = None,
        height: DivSize | None = None,
        hover_end_actions: list[DivAction] | None = None,
        hover_start_actions: list[DivAction] | None = None,
        id: str | None = None,
        items: list[Div] | None = None,
        layout_provider: DivLayoutProvider | None = None,
        longtap_actions: list[DivAction] | None = None,
        margins: DivEdgeInsets | None = None,
        paddings: DivEdgeInsets | None = None,
        press_end_actions: list[DivAction] | None = None,
        press_start_actions: list[DivAction] | None = None,
        reuse_id: str | None = None,
        row_span: int | str | None = None,
        selected_actions: list[DivAction] | None = None,
        tooltips: list[DivTooltip] | None = None,
        transform: DivTransform | None = None,
        transformations: list[DivTransformation] | None = None,
        transition_change: DivChangeTransition | None = None,
        transition_in: DivAppearanceTransition | None = None,
        transition_out: DivAppearanceTransition | None = None,
        transition_triggers: list[DivTransitionTrigger | str] | None = None,
        variable_triggers: list[DivTrigger] | None = None,
        variables: list[DivVariable] | None = None,
        visibility: DivVisibility | str | None = None,
        visibility_action: DivVisibilityAction | None = None,
        visibility_actions: list[DivVisibilityAction] | None = None,
        width: DivSize | None = None,
    ) -> None: ...

class DivImage(PyDivEntity):
    def __init__(
        self,
        *,
        image_url: str,
        accessibility: DivAccessibility | None = None,
        action: DivAction | None = None,
        action_animation: DivAnimation | None = None,
        actions: list[DivAction] | None = None,
        alignment_horizontal: DivAlignmentHorizontal | str | None = None,
        alignment_vertical: DivAlignmentVertical | str | None = None,
        alpha: float | str | None = None,
        animators: list[DivAnimator] | None = None,
        appearance_animation: DivFadeTransition | None = None,
        aspect: DivAspect | None = None,
        background: list[DivBackground] | None = None,
        border: DivBorder | None = None,
        capture_focus_on_action: bool | str | None = None,
        column_span: int | str | None = None,
        content_alignment_horizontal: DivAlignmentHorizontal | str | None = None,
        content_alignment_vertical: DivAlignmentVertical | str | None = None,
        disappear_actions: list[DivDisappearAction] | None = None,
        doubletap_actions: list[DivAction] | None = None,
        extensions: list[DivExtension] | None = None,
        filters: list[DivFilter] | None = None,
        focus: DivFocus | None = None,
        functions: list[DivFunction] | None = None,
        height: DivSize | None = None,
        high_priority_preview_show: bool | int | str | None = None,
        hover_end_actions: list[DivAction] | None = None,
        hover_start_actions: list[DivAction] | None = None,
        id: str | None = None,
        layout_provider: DivLayoutProvider | None = None,
        longtap_actions: list[DivAction] | None = None,
        margins: DivEdgeInsets | None = None,
        paddings: DivEdgeInsets | None = None,
        placeholder_color: str | None = None,
        preload_required: bool | int | str | None = None,
        press_end_actions: list[DivAction] | None = None,
        press_start_actions: list[DivAction] | None = None,
        preview: str | None = None,
        reuse_id: str | None = None,
        row_span: int | str | None = None,
        scale: DivImageScale | str | None = None,
        selected_actions: list[DivAction] | None = None,
        tint_color: str | None = None,
        tint_mode: DivBlendMode | str | None = None,
        tooltips: list[DivTooltip] | None = None,
        transform: DivTransform | None = None,
        transformations: list[DivTransformation] | None = None,
        transition_change: DivChangeTransition | None = None,
        transition_in: DivAppearanceTransition | None = None,
        transition_out: DivAppearanceTransition | None = None,
        transition_triggers: list[DivTransitionTrigger | str] | None = None,
        variable_triggers: list[DivTrigger] | None = None,
        variables: list[DivVariable] | None = None,
        visibility: DivVisibility | str | None = None,
        visibility_action: DivVisibilityAction | None = None,
        visibility_actions: list[DivVisibilityAction] | None = None,
        width: DivSize | None = None,
    ) -> None: ...

class DivImageBackground(PyDivEntity):
    def __init__(
        self,
        *,
        image_url: str,
        alpha: float | str | None = None,
        content_alignment_horizontal: DivAlignmentHorizontal | str | None = None,
        content_alignment_vertical: DivAlignmentVertical | str | None = None,
        filters: list[DivFilter] | None = None,
        preload_required: bool | int | str | None = None,
        scale: DivImageScale | str | None = None,
    ) -> None: ...

class DivIndicator(PyDivEntity):
    def __init__(
        self,
        *,
        accessibility: DivAccessibility | None = None,
        active_item_color: str | None = None,
        active_item_size: float | str | None = None,
        active_shape: DivRoundedRectangleShape | None = None,
        alignment_horizontal: DivAlignmentHorizontal | str | None = None,
        alignment_vertical: DivAlignmentVertical | str | None = None,
        alpha: float | str | None = None,
        animation: DivIndicatorAnimation | str | None = None,
        animators: list[DivAnimator] | None = None,
        background: list[DivBackground] | None = None,
        border: DivBorder | None = None,
        column_span: int | str | None = None,
        disappear_actions: list[DivDisappearAction] | None = None,
        extensions: list[DivExtension] | None = None,
        focus: DivFocus | None = None,
        functions: list[DivFunction] | None = None,
        height: DivSize | None = None,
        id: str | None = None,
        inactive_item_color: str | None = None,
        inactive_minimum_shape: DivRoundedRectangleShape | None = None,
        inactive_shape: DivRoundedRectangleShape | None = None,
        items_placement: DivIndicatorItemPlacement | None = None,
        layout_provider: DivLayoutProvider | None = None,
        margins: DivEdgeInsets | None = None,
        minimum_item_size: float | str | None = None,
        paddings: DivEdgeInsets | None = None,
        pager_id: str | None = None,
        reuse_id: str | None = None,
        row_span: int | str | None = None,
        selected_actions: list[DivAction] | None = None,
        shape: DivShape | None = None,
        space_between_centers: DivFixedSize | None = None,
        tooltips: list[DivTooltip] | None = None,
        transform: DivTransform | None = None,
        transformations: list[DivTransformation] | None = None,
        transition_change: DivChangeTransition | None = None,
        transition_in: DivAppearanceTransition | None = None,
        transition_out: DivAppearanceTransition | None = None,
        transition_triggers: list[DivTransitionTrigger | str] | None = None,
        variable_triggers: list[DivTrigger] | None = None,
        variables: list[DivVariable] | None = None,
        visibility: DivVisibility | str | None = None,
        visibility_action: DivVisibilityAction | None = None,
        visibility_actions: list[DivVisibilityAction] | None = None,
        width: DivSize | None = None,
    ) -> None: ...

class DivInfinityCount(PyDivEntity):
    def __init__(
        self,
    ) -> None: ...

class DivInput(PyDivEntity):
    def __init__(
        self,
        *,
        text_variable: str,
        accessibility: DivAccessibility | None = None,
        alignment_horizontal: DivAlignmentHorizontal | str | None = None,
        alignment_vertical: DivAlignmentVertical | str | None = None,
        alpha: float | str | None = None,
        animators: list[DivAnimator] | None = None,
        autocapitalization: DivInputAutocapitalization | str | None = None,
        background: list[DivBackground] | None = None,
        border: DivBorder | None = None,
        column_span: int | str | None = None,
        disappear_actions: list[DivDisappearAction] | None = None,
        enter_key_actions: list[DivAction] | None = None,
        enter_key_type: DivInputEnterKeyType | str | None = None,
        extensions: list[DivExtension] | None = None,
        filters: list[DivInputFilter] | None = None,
        focus: DivFocus | None = None,
        font_family: str | None = None,
        font_size: int | str | None = None,
        font_size_unit: DivSizeUnit | str | None = None,
        font_variation_settings: dict[str, Any] | str | None = None,
        font_weight: DivFontWeight | str | None = None,
        font_weight_value: int | str | None = None,
        functions: list[DivFunction] | None = None,
        height: DivSize | None = None,
        highlight_color: str | None = None,
        hint_color: str | None = None,
        hint_text: str | None = None,
        id: str | None = None,
        is_enabled: bool | int | str | None = None,
        keyboard_type: DivInputKeyboardType | str | None = None,
        layout_provider: DivLayoutProvider | None = None,
        letter_spacing: float | str | None = None,
        line_height: int | str | None = None,
        margins: DivEdgeInsets | None = None,
        mask: DivInputMask | None = None,
        max_length: int | str | None = None,
        max_visible_lines: int | str | None = None,
        native_interface: DivInputNativeInterface | None = None,
        paddings: DivEdgeInsets | None = None,
        reuse_id: str | None = None,
        row_span: int | str | None = None,
        select_all_on_focus: bool | int | str | None = None,
        selected_actions: list[DivAction] | None = None,
        text_alignment_horizontal: DivAlignmentHorizontal | str | None = None,
        text_alignment_vertical: DivAlignmentVertical | str | None = None,
        text_color: str | None = None,
        tooltips: list[DivTooltip] | None = None,
        transform: DivTransform | None = None,
        transformations: list[DivTransformation] | None = None,
        transition_change: DivChangeTransition | None = None,
        transition_in: DivAppearanceTransition | None = None,
        transition_out: DivAppearanceTransition | None = None,
        transition_triggers: list[DivTransitionTrigger | str] | None = None,
        validators: list[DivInputValidator] | None = None,
        variable_triggers: list[DivTrigger] | None = None,
        variables: list[DivVariable] | None = None,
        visibility: DivVisibility | str | None = None,
        visibility_action: DivVisibilityAction | None = None,
        visibility_actions: list[DivVisibilityAction] | None = None,
        width: DivSize | None = None,
    ) -> None: ...

class DivInputFilterExpression(PyDivEntity):
    def __init__(
        self,
        *,
        condition: bool | int | str,
    ) -> None: ...

class DivInputFilterRegex(PyDivEntity):
    def __init__(
        self,
        *,
        pattern: str,
    ) -> None: ...

class DivInputMaskBase(PyDivEntity):
    def __init__(
        self,
        *,
        raw_text_variable: str,
    ) -> None: ...

class DivInputNativeInterface(PyDivEntity):
    def __init__(
        self,
        *,
        color: str,
    ) -> None: ...

class DivInputValidatorBase(PyDivEntity):
    def __init__(
        self,
        *,
        allow_empty: bool | int | str | None = None,
        label_id: str | None = None,
        variable: str | None = None,
    ) -> None: ...

class DivInputValidatorExpression(PyDivEntity):
    def __init__(
        self,
        *,
        condition: bool | int | str,
        label_id: str,
        variable: str,
        allow_empty: bool | int | str | None = None,
    ) -> None: ...

class DivInputValidatorRegex(PyDivEntity):
    def __init__(
        self,
        *,
        label_id: str,
        pattern: str,
        variable: str,
        allow_empty: bool | int | str | None = None,
    ) -> None: ...

class DivLayoutProvider(PyDivEntity):
    def __init__(
        self,
        *,
        height_variable_name: str | None = None,
        width_variable_name: str | None = None,
    ) -> None: ...

class DivLinearGradient(PyDivEntity):
    def __init__(
        self,
        *,
        angle: int | str | None = None,
        color_map: list[DivLinearGradientColorPoint] | None = None,
        colors: list[str] | str | None = None,
    ) -> None: ...

class DivLinearGradientColorPoint(PyDivEntity):
    def __init__(
        self,
        *,
        color: str,
        position: float | str,
    ) -> None: ...

class DivMatchParentSize(PyDivEntity):
    def __init__(
        self,
        *,
        max_size: DivSizeUnitValue | None = None,
        min_size: DivSizeUnitValue | None = None,
        weight: float | str | None = None,
    ) -> None: ...

class DivNeighbourPageSize(PyDivEntity):
    def __init__(
        self,
        *,
        neighbour_page_width: DivFixedSize,
    ) -> None: ...

class DivNinePatchBackground(PyDivEntity):
    def __init__(
        self,
        *,
        image_url: str,
        insets: DivAbsoluteEdgeInsets,
    ) -> None: ...

class DivNumberAnimator(PyDivEntity):
    def __init__(
        self,
        *,
        duration: int | str,
        end_value: float | str,
        id: str,
        variable_name: str,
        cancel_actions: list[DivAction] | None = None,
        direction: DivAnimationDirection | str | None = None,
        end_actions: list[DivAction] | None = None,
        interpolator: DivAnimationInterpolator | str | None = None,
        repeat_count: DivCount | None = None,
        start_delay: int | str | None = None,
        start_value: float | str | None = None,
    ) -> None: ...

class DivPageContentSize(PyDivEntity):
    def __init__(
        self,
    ) -> None: ...

class DivPageSize(PyDivEntity):
    def __init__(
        self,
        *,
        page_width: DivPercentageSize,
    ) -> None: ...

class DivPageTransformationOverlap(PyDivEntity):
    def __init__(
        self,
        *,
        interpolator: DivAnimationInterpolator | str | None = None,
        next_page_alpha: float | str | None = None,
        next_page_scale: float | str | None = None,
        previous_page_alpha: float | str | None = None,
        previous_page_scale: float | str | None = None,
        reversed_stacking_order: bool | int | str | None = None,
    ) -> None: ...

class DivPageTransformationSlide(PyDivEntity):
    def __init__(
        self,
        *,
        interpolator: DivAnimationInterpolator | str | None = None,
        next_page_alpha: float | str | None = None,
        next_page_scale: float | str | None = None,
        previous_page_alpha: float | str | None = None,
        previous_page_scale: float | str | None = None,
    ) -> None: ...

class DivPager(PyDivEntity):
    def __init__(
        self,
        *,
        layout_mode: DivPagerLayoutMode,
        accessibility: DivAccessibility | None = None,
        alignment_horizontal: DivAlignmentHorizontal | str | None = None,
        alignment_vertical: DivAlignmentVertical | str | None = None,
        alpha: float | str | None = None,
        animators: list[DivAnimator] | None = None,
        background: list[DivBackground] | None = None,
        border: DivBorder | None = None,
        column_span: int | str | None = None,
        cross_axis_alignment: DivPagerItemAlignment | str | None = None,
        default_item: int | str | None = None,
        disappear_actions: list[DivDisappearAction] | None = None,
        extensions: list[DivExtension] | None = None,
        focus: DivFocus | None = None,
        functions: list[DivFunction] | None = None,
        height: DivSize | None = None,
        id: str | None = None,
        infinite_scroll: bool | int | str | None = None,
        item_builder: DivCollectionItemBuilder | None = None,
        item_spacing: DivFixedSize | None = None,
        items: list[Div] | None = None,
        layout_provider: DivLayoutProvider | None = None,
        margins: DivEdgeInsets | None = None,
        orientation: DivPagerOrientation | str | None = None,
        paddings: DivEdgeInsets | None = None,
        page_transformation: DivPageTransformation | None = None,
        restrict_parent_scroll: bool | int | str | None = None,
        reuse_id: str | None = None,
        row_span: int | str | None = None,
        scroll_axis_alignment: DivPagerItemAlignment | str | None = None,
        selected_actions: list[DivAction] | None = None,
        tooltips: list[DivTooltip] | None = None,
        transform: DivTransform | None = None,
        transformations: list[DivTransformation] | None = None,
        transition_change: DivChangeTransition | None = None,
        transition_in: DivAppearanceTransition | None = None,
        transition_out: DivAppearanceTransition | None = None,
        transition_triggers: list[DivTransitionTrigger | str] | None = None,
        variable_triggers: list[DivTrigger] | None = None,
        variables: list[DivVariable] | None = None,
        visibility: DivVisibility | str | None = None,
        visibility_action: DivVisibilityAction | None = None,
        visibility_actions: list[DivVisibilityAction] | None = None,
        width: DivSize | None = None,
    ) -> None: ...

class DivPatch(PyDivEntity):
    def __init__(
        self,
        *,
        changes: list[DivPatchChange],
        mode: DivPatchMode | str | None = None,
        on_applied_actions: list[DivAction] | None = None,
        on_failed_actions: list[DivAction] | None = None,
    ) -> None: ...

class DivPatchChange(PyDivEntity):
    def __init__(
        self,
        *,
        id: str,
        items: list[Div] | None = None,
    ) -> None: ...

class DivPercentageSize(PyDivEntity):
    def __init__(
        self,
        *,
        value: float | str,
    ) -> None: ...

class DivPercentageTranslation(PyDivEntity):
    def __init__(
        self,
        *,
        value: float | str,
    ) -> None: ...

class DivPhoneInputMask(PyDivEntity):
    def __init__(
        self,
        *,
        raw_text_variable: str,
    ) -> None: ...

class DivPivotFixed(PyDivEntity):
    def __init__(
        self,
        *,
        unit: DivSizeUnit | str | None = None,
        value: int | str | None = None,
    ) -> None: ...

class DivPivotPercentage(PyDivEntity):
    def __init__(
        self,
        *,
        value: float | str,
    ) -> None: ...

class DivPoint(PyDivEntity):
    def __init__(
        self,
        *,
        x: DivDimension,
        y: DivDimension,
    ) -> None: ...

class DivRadialGradient(PyDivEntity):
    def __init__(
        self,
        *,
        center_x: DivRadialGradientCenter | None = None,
        center_y: DivRadialGradientCenter | None = None,
        color_map: list[DivRadialGradientColorPoint] | None = None,
        colors: list[str] | str | None = None,
        radius: DivRadialGradientRadius | None = None,
    ) -> None: ...

class DivRadialGradientColorPoint(PyDivEntity):
    def __init__(
        self,
        *,
        color: str,
        position: float | str,
    ) -> None: ...

class DivRadialGradientFixedCenter(PyDivEntity):
    def __init__(
        self,
        *,
        value: int | str,
        unit: DivSizeUnit | str | None = None,
    ) -> None: ...

class DivRadialGradientRelativeCenter(PyDivEntity):
    def __init__(
        self,
        *,
        value: float | str,
    ) -> None: ...

class DivRadialGradientRelativeRadius(PyDivEntity):
    def __init__(
        self,
        *,
        value: DivRadialGradientRelativeRadiusValue | str,
    ) -> None: ...

class DivRotationTransformation(PyDivEntity):
    def __init__(
        self,
        *,
        angle: float | str,
        pivot_x: DivPivot | None = None,
        pivot_y: DivPivot | None = None,
    ) -> None: ...

class DivRoundedRectangleShape(PyDivEntity):
    def __init__(
        self,
        *,
        background_color: str | None = None,
        corner_radius: DivFixedSize | None = None,
        item_height: DivFixedSize | None = None,
        item_width: DivFixedSize | None = None,
        stroke: DivStroke | None = None,
    ) -> None: ...

class DivScaleTransition(PyDivEntity):
    def __init__(
        self,
        *,
        duration: int | str | None = None,
        interpolator: DivAnimationInterpolator | str | None = None,
        pivot_x: float | str | None = None,
        pivot_y: float | str | None = None,
        scale: float | str | None = None,
        start_delay: int | str | None = None,
    ) -> None: ...

class DivSelect(PyDivEntity):
    def __init__(
        self,
        *,
        options: list[DivSelectOption],
        value_variable: str,
        accessibility: DivAccessibility | None = None,
        alignment_horizontal: DivAlignmentHorizontal | str | None = None,
        alignment_vertical: DivAlignmentVertical | str | None = None,
        alpha: float | str | None = None,
        animators: list[DivAnimator] | None = None,
        background: list[DivBackground] | None = None,
        border: DivBorder | None = None,
        column_span: int | str | None = None,
        disappear_actions: list[DivDisappearAction] | None = None,
        extensions: list[DivExtension] | None = None,
        focus: DivFocus | None = None,
        font_family: str | None = None,
        font_size: int | str | None = None,
        font_size_unit: DivSizeUnit | str | None = None,
        font_variation_settings: dict[str, Any] | str | None = None,
        font_weight: DivFontWeight | str | None = None,
        font_weight_value: int | str | None = None,
        functions: list[DivFunction] | None = None,
        height: DivSize | None = None,
        hint_color: str | None = None,
        hint_text: str | None = None,
        id: str | None = None,
        layout_provider: DivLayoutProvider | None = None,
        letter_spacing: float | str | None = None,
        line_height: int | str | None = None,
        margins: DivEdgeInsets | None = None,
        paddings: DivEdgeInsets | None = None,
        reuse_id: str | None = None,
        row_span: int | str | None = None,
        selected_actions: list[DivAction] | None = None,
        text_color: str | None = None,
        tooltips: list[DivTooltip] | None = None,
        transform: DivTransform | None = None,
        transformations: list[DivTransformation] | None = None,
        transition_change: DivChangeTransition | None = None,
        transition_in: DivAppearanceTransition | None = None,
        transition_out: DivAppearanceTransition | None = None,
        transition_triggers: list[DivTransitionTrigger | str] | None = None,
        variable_triggers: list[DivTrigger] | None = None,
        variables: list[DivVariable] | None = None,
        visibility: DivVisibility | str | None = None,
        visibility_action: DivVisibilityAction | None = None,
        visibility_actions: list[DivVisibilityAction] | None = None,
        width: DivSize | None = None,
    ) -> None: ...

class DivSelectOption(PyDivEntity):
    def __init__(
        self,
        *,
        value: str,
        text: str | None = None,
    ) -> None: ...

class DivSeparator(PyDivEntity):
    def __init__(
        self,
        *,
        accessibility: DivAccessibility | None = None,
        action: DivAction | None = None,
        action_animation: DivAnimation | None = None,
        actions: list[DivAction] | None = None,
        alignment_horizontal: DivAlignmentHorizontal | str | None = None,
        alignment_vertical: DivAlignmentVertical | str | None = None,
        alpha: float | str | None = None,
        animators: list[DivAnimator] | None = None,
        background: list[DivBackground] | None = None,
        border: DivBorder | None = None,
        capture_focus_on_action: bool | str | None = None,
        column_span: int | str | None = None,
        delimiter_style: DivSeparatorDelimiterStyle | None = None,
        disappear_actions: list[DivDisappearAction] | None = None,
        doubletap_actions: list[DivAction] | None = None,
        extensions: list[DivExtension] | None = None,
        focus: DivFocus | None = None,
        functions: list[DivFunction] | None = None,
        height: DivSize | None = None,
        hover_end_actions: list[DivAction] | None = None,
        hover_start_actions: list[DivAction] | None = None,
        id: str | None = None,
        layout_provider: DivLayoutProvider | None = None,
        longtap_actions: list[DivAction] | None = None,
        margins: DivEdgeInsets | None = None,
        paddings: DivEdgeInsets | None = None,
        press_end_actions: list[DivAction] | None = None,
        press_start_actions: list[DivAction] | None = None,
        reuse_id: str | None = None,
        row_span: int | str | None = None,
        selected_actions: list[DivAction] | None = None,
        tooltips: list[DivTooltip] | None = None,
        transform: DivTransform | None = None,
        transformations: list[DivTransformation] | None = None,
        transition_change: DivChangeTransition | None = None,
        transition_in: DivAppearanceTransition | None = None,
        transition_out: DivAppearanceTransition | None = None,
        transition_triggers: list[DivTransitionTrigger | str] | None = None,
        variable_triggers: list[DivTrigger] | None = None,
        variables: list[DivVariable] | None = None,
        visibility: DivVisibility | str | None = None,
        visibility_action: DivVisibilityAction | None = None,
        visibility_actions: list[DivVisibilityAction] | None = None,
        width: DivSize | None = None,
    ) -> None: ...

class DivSeparatorDelimiterStyle(PyDivEntity):
    def __init__(
        self,
        *,
        color: str | None = None,
        orientation: DelimiterStyleOrientation | str | None = None,
    ) -> None: ...

class DivShadow(PyDivEntity):
    def __init__(
        self,
        *,
        offset: DivPoint,
        alpha: float | str | None = None,
        blur: int | str | None = None,
        color: str | None = None,
    ) -> None: ...

class DivShapeDrawable(PyDivEntity):
    def __init__(
        self,
        *,
        color: str,
        shape: DivShape,
        stroke: DivStroke | None = None,
    ) -> None: ...

class DivSightAction(PyDivEntity):
    def __init__(
        self,
        *,
        log_id: str,
        download_callbacks: DivDownloadCallbacks | None = None,
        is_enabled: bool | int | str | None = None,
        log_limit: int | str | None = None,
        payload: dict[str, Any] | None = None,
        referer: str | None = None,
        scope_id: str | None = None,
        typed: DivActionTyped | None = None,
        url: str | None = None,
    ) -> None: ...

class DivSizeUnitValue(PyDivEntity):
    def __init__(
        self,
        *,
        value: int | str,
        unit: DivSizeUnit | str | None = None,
    ) -> None: ...

class DivSlideTransition(PyDivEntity):
    def __init__(
        self,
        *,
        distance: DivDimension | None = None,
        duration: int | str | None = None,
        edge: DivSlideTransitionEdge | str | None = None,
        interpolator: DivAnimationInterpolator | str | None = None,
        start_delay: int | str | None = None,
    ) -> None: ...

class DivSlider(PyDivEntity):
    def __init__(
        self,
        *,
        thumb_style: DivDrawable,
        track_active_style: DivDrawable,
        track_inactive_style: DivDrawable,
        accessibility: DivAccessibility | None = None,
        alignment_horizontal: DivAlignmentHorizontal | str | None = None,
        alignment_vertical: DivAlignmentVertical | str | None = None,
        alpha: float | str | None = None,
        animators: list[DivAnimator] | None = None,
        background: list[DivBackground] | None = None,
        border: DivBorder | None = None,
        column_span: int | str | None = None,
        disappear_actions: list[DivDisappearAction] | None = None,
        extensions: list[DivExtension] | None = None,
        focus: DivFocus | None = None,
        functions: list[DivFunction] | None = None,
        height: DivSize | None = None,
        id: str | None = None,
        is_enabled: bool | int | str | None = None,
        layout_provider: DivLayoutProvider | None = None,
        margins: DivEdgeInsets | None = None,
        max_value: int | str | None = None,
        min_value: int | str | None = None,
        paddings: DivEdgeInsets | None = None,
        ranges: list[DivSliderRange] | None = None,
        reuse_id: str | None = None,
        row_span: int | str | None = None,
        secondary_value_accessibility: DivAccessibility | None = None,
        selected_actions: list[DivAction] | None = None,
        thumb_secondary_style: DivDrawable | None = None,
        thumb_secondary_text_style: DivSliderTextStyle | None = None,
        thumb_secondary_value_variable: str | None = None,
        thumb_text_style: DivSliderTextStyle | None = None,
        thumb_value_variable: str | None = None,
        tick_mark_active_style: DivDrawable | None = None,
        tick_mark_inactive_style: DivDrawable | None = None,
        tooltips: list[DivTooltip] | None = None,
        transform: DivTransform | None = None,
        transformations: list[DivTransformation] | None = None,
        transition_change: DivChangeTransition | None = None,
        transition_in: DivAppearanceTransition | None = None,
        transition_out: DivAppearanceTransition | None = None,
        transition_triggers: list[DivTransitionTrigger | str] | None = None,
        variable_triggers: list[DivTrigger] | None = None,
        variables: list[DivVariable] | None = None,
        visibility: DivVisibility | str | None = None,
        visibility_action: DivVisibilityAction | None = None,
        visibility_actions: list[DivVisibilityAction] | None = None,
        width: DivSize | None = None,
    ) -> None: ...

class DivSliderRange(PyDivEntity):
    def __init__(
        self,
        *,
        end: int | str | None = None,
        margins: DivEdgeInsets | None = None,
        start: int | str | None = None,
        track_active_style: DivDrawable | None = None,
        track_inactive_style: DivDrawable | None = None,
    ) -> None: ...

class DivSliderTextStyle(PyDivEntity):
    def __init__(
        self,
        *,
        font_size: int | str,
        font_family: str | None = None,
        font_size_unit: DivSizeUnit | str | None = None,
        font_variation_settings: dict[str, Any] | str | None = None,
        font_weight: DivFontWeight | str | None = None,
        font_weight_value: int | str | None = None,
        letter_spacing: float | str | None = None,
        offset: DivPoint | None = None,
        text_color: str | None = None,
    ) -> None: ...

class DivSolidBackground(PyDivEntity):
    def __init__(
        self,
        *,
        color: str,
    ) -> None: ...

class DivState(PyDivEntity):
    def __init__(
        self,
        *,
        states: list[DivStateState],
        accessibility: DivAccessibility | None = None,
        action: DivAction | None = None,
        action_animation: DivAnimation | None = None,
        actions: list[DivAction] | None = None,
        alignment_horizontal: DivAlignmentHorizontal | str | None = None,
        alignment_vertical: DivAlignmentVertical | str | None = None,
        alpha: float | str | None = None,
        animators: list[DivAnimator] | None = None,
        background: list[DivBackground] | None = None,
        border: DivBorder | None = None,
        capture_focus_on_action: bool | str | None = None,
        clip_to_bounds: bool | int | str | None = None,
        column_span: int | str | None = None,
        default_state_id: str | None = None,
        disappear_actions: list[DivDisappearAction] | None = None,
        div_id: str | None = None,
        doubletap_actions: list[DivAction] | None = None,
        extensions: list[DivExtension] | None = None,
        focus: DivFocus | None = None,
        functions: list[DivFunction] | None = None,
        height: DivSize | None = None,
        hover_end_actions: list[DivAction] | None = None,
        hover_start_actions: list[DivAction] | None = None,
        id: str | None = None,
        layout_provider: DivLayoutProvider | None = None,
        longtap_actions: list[DivAction] | None = None,
        margins: DivEdgeInsets | None = None,
        paddings: DivEdgeInsets | None = None,
        press_end_actions: list[DivAction] | None = None,
        press_start_actions: list[DivAction] | None = None,
        reuse_id: str | None = None,
        row_span: int | str | None = None,
        selected_actions: list[DivAction] | None = None,
        state_id_variable: str | None = None,
        tooltips: list[DivTooltip] | None = None,
        transform: DivTransform | None = None,
        transformations: list[DivTransformation] | None = None,
        transition_animation_selector: DivTransitionSelector | str | None = None,
        transition_change: DivChangeTransition | None = None,
        transition_in: DivAppearanceTransition | None = None,
        transition_out: DivAppearanceTransition | None = None,
        transition_triggers: list[DivTransitionTrigger | str] | None = None,
        variable_triggers: list[DivTrigger] | None = None,
        variables: list[DivVariable] | None = None,
        visibility: DivVisibility | str | None = None,
        visibility_action: DivVisibilityAction | None = None,
        visibility_actions: list[DivVisibilityAction] | None = None,
        width: DivSize | None = None,
    ) -> None: ...

class DivStateState(PyDivEntity):
    def __init__(
        self,
        *,
        state_id: str,
        animation_in: DivAnimation | None = None,
        animation_out: DivAnimation | None = None,
        div: Div | None = None,
        swipe_out_actions: list[DivAction] | None = None,
    ) -> None: ...

class DivStretchIndicatorItemPlacement(PyDivEntity):
    def __init__(
        self,
        *,
        item_spacing: DivFixedSize | None = None,
        max_visible_items: int | str | None = None,
    ) -> None: ...

class DivStroke(PyDivEntity):
    def __init__(
        self,
        *,
        color: str,
        style: DivStrokeStyle | None = None,
        unit: DivSizeUnit | str | None = None,
        width: float | str | None = None,
    ) -> None: ...

class DivStrokeStyleDashed(PyDivEntity):
    def __init__(
        self,
    ) -> None: ...

class DivStrokeStyleSolid(PyDivEntity):
    def __init__(
        self,
    ) -> None: ...

class DivSwitch(PyDivEntity):
    def __init__(
        self,
        *,
        is_on_variable: str,
        accessibility: DivAccessibility | None = None,
        alignment_horizontal: DivAlignmentHorizontal | str | None = None,
        alignment_vertical: DivAlignmentVertical | str | None = None,
        alpha: float | str | None = None,
        animators: list[DivAnimator] | None = None,
        background: list[DivBackground] | None = None,
        border: DivBorder | None = None,
        column_span: int | str | None = None,
        disappear_actions: list[DivDisappearAction] | None = None,
        extensions: list[DivExtension] | None = None,
        focus: DivFocus | None = None,
        functions: list[DivFunction] | None = None,
        height: DivSize | None = None,
        id: str | None = None,
        is_enabled: bool | int | str | None = None,
        layout_provider: DivLayoutProvider | None = None,
        margins: DivEdgeInsets | None = None,
        on_color: str | None = None,
        paddings: DivEdgeInsets | None = None,
        reuse_id: str | None = None,
        row_span: int | str | None = None,
        selected_actions: list[DivAction] | None = None,
        tooltips: list[DivTooltip] | None = None,
        transform: DivTransform | None = None,
        transformations: list[DivTransformation] | None = None,
        transition_change: DivChangeTransition | None = None,
        transition_in: DivAppearanceTransition | None = None,
        transition_out: DivAppearanceTransition | None = None,
        transition_triggers: list[DivTransitionTrigger | str] | None = None,
        variable_triggers: list[DivTrigger] | None = None,
        variables: list[DivVariable] | None = None,
        visibility: DivVisibility | str | None = None,
        visibility_action: DivVisibilityAction | None = None,
        visibility_actions: list[DivVisibilityAction] | None = None,
        width: DivSize | None = None,
    ) -> None: ...

class DivTabs(PyDivEntity):
    def __init__(
        self,
        *,
        items: list[DivTabsItem],
        accessibility: DivAccessibility | None = None,
        alignment_horizontal: DivAlignmentHorizontal | str | None = None,
        alignment_vertical: DivAlignmentVertical | str | None = None,
        alpha: float | str | None = None,
        animators: list[DivAnimator] | None = None,
        background: list[DivBackground] | None = None,
        border: DivBorder | None = None,
        column_span: int | str | None = None,
        disappear_actions: list[DivDisappearAction] | None = None,
        dynamic_height: bool | int | str | None = None,
        extensions: list[DivExtension] | None = None,
        focus: DivFocus | None = None,
        functions: list[DivFunction] | None = None,
        has_separator: bool | int | str | None = None,
        height: DivSize | None = None,
        id: str | None = None,
        layout_provider: DivLayoutProvider | None = None,
        margins: DivEdgeInsets | None = None,
        paddings: DivEdgeInsets | None = None,
        restrict_parent_scroll: bool | int | str | None = None,
        reuse_id: str | None = None,
        row_span: int | str | None = None,
        selected_actions: list[DivAction] | None = None,
        selected_tab: int | str | None = None,
        separator_color: str | None = None,
        separator_paddings: DivEdgeInsets | None = None,
        switch_tabs_by_content_swipe_enabled: bool | int | str | None = None,
        tab_title_delimiter: DivTabsTabTitleDelimiter | None = None,
        tab_title_style: DivTabsTabTitleStyle | None = None,
        title_paddings: DivEdgeInsets | None = None,
        tooltips: list[DivTooltip] | None = None,
        transform: DivTransform | None = None,
        transformations: list[DivTransformation] | None = None,
        transition_change: DivChangeTransition | None = None,
        transition_in: DivAppearanceTransition | None = None,
        transition_out: DivAppearanceTransition | None = None,
        transition_triggers: list[DivTransitionTrigger | str] | None = None,
        variable_triggers: list[DivTrigger] | None = None,
        variables: list[DivVariable] | None = None,
        visibility: DivVisibility | str | None = None,
        visibility_action: DivVisibilityAction | None = None,
        visibility_actions: list[DivVisibilityAction] | None = None,
        width: DivSize | None = None,
    ) -> None: ...

class DivTabsItem(PyDivEntity):
    def __init__(
        self,
        *,
        div: Div,
        title: str,
        title_click_action: DivAction | None = None,
    ) -> None: ...

class DivTabsTabTitleDelimiter(PyDivEntity):
    def __init__(
        self,
        *,
        image_url: str,
        height: DivFixedSize | None = None,
        width: DivFixedSize | None = None,
    ) -> None: ...

class DivTabsTabTitleStyle(PyDivEntity):
    def __init__(
        self,
        *,
        active_background_color: str | None = None,
        active_font_variation_settings: dict[str, Any] | str | None = None,
        active_font_weight: DivFontWeight | str | None = None,
        active_text_color: str | None = None,
        animation_duration: int | str | None = None,
        animation_type: TabTitleStyleAnimationType | str | None = None,
        corner_radius: int | str | None = None,
        corners_radius: DivCornersRadius | None = None,
        font_family: str | None = None,
        font_size: int | str | None = None,
        font_size_unit: DivSizeUnit | str | None = None,
        font_weight: DivFontWeight | str | None = None,
        inactive_background_color: str | None = None,
        inactive_font_variation_settings: dict[str, Any] | str | None = None,
        inactive_font_weight: DivFontWeight | str | None = None,
        inactive_text_color: str | None = None,
        item_spacing: int | str | None = None,
        letter_spacing: float | str | None = None,
        line_height: int | str | None = None,
        paddings: DivEdgeInsets | None = None,
    ) -> None: ...

class DivText(PyDivEntity):
    def __init__(
        self,
        *,
        text: str,
        accessibility: DivAccessibility | None = None,
        action: DivAction | None = None,
        action_animation: DivAnimation | None = None,
        actions: list[DivAction] | None = None,
        alignment_horizontal: DivAlignmentHorizontal | str | None = None,
        alignment_vertical: DivAlignmentVertical | str | None = None,
        alpha: float | str | None = None,
        animators: list[DivAnimator] | None = None,
        auto_ellipsize: bool | int | str | None = None,
        background: list[DivBackground] | None = None,
        border: DivBorder | None = None,
        capture_focus_on_action: bool | str | None = None,
        column_span: int | str | None = None,
        disappear_actions: list[DivDisappearAction] | None = None,
        doubletap_actions: list[DivAction] | None = None,
        ellipsis: DivTextEllipsis | None = None,
        extensions: list[DivExtension] | None = None,
        focus: DivFocus | None = None,
        focused_text_color: str | None = None,
        font_family: str | None = None,
        font_feature_settings: str | None = None,
        font_size: int | str | None = None,
        font_size_unit: DivSizeUnit | str | None = None,
        font_variation_settings: dict[str, Any] | str | None = None,
        font_weight: DivFontWeight | str | None = None,
        font_weight_value: int | str | None = None,
        functions: list[DivFunction] | None = None,
        height: DivSize | None = None,
        hover_end_actions: list[DivAction] | None = None,
        hover_start_actions: list[DivAction] | None = None,
        id: str | None = None,
        images: list[DivTextImage] | None = None,
        layout_provider: DivLayoutProvider | None = None,
        letter_spacing: float | str | None = None,
        line_height: int | str | None = None,
        longtap_actions: list[DivAction] | None = None,
        margins: DivEdgeInsets | None = None,
        max_lines: int | str | None = None,
        min_hidden_lines: int | str | None = None,
        paddings: DivEdgeInsets | None = None,
        press_end_actions: list[DivAction] | None = None,
        press_start_actions: list[DivAction] | None = None,
        ranges: list[DivTextRange] | None = None,
        reuse_id: str | None = None,
        row_span: int | str | None = None,
        selectable: bool | int | str | None = None,
        selected_actions: list[DivAction] | None = None,
        strike: DivLineStyle | str | None = None,
        text_alignment_horizontal: DivAlignmentHorizontal | str | None = None,
        text_alignment_vertical: DivAlignmentVertical | str | None = None,
        text_color: str | None = None,
        text_gradient: DivTextGradient | None = None,
        text_shadow: DivShadow | None = None,
        tighten_width: bool | int | str | None = None,
        tooltips: list[DivTooltip] | None = None,
        transform: DivTransform | None = None,
        transformations: list[DivTransformation] | None = None,
        transition_change: DivChangeTransition | None = None,
        transition_in: DivAppearanceTransition | None = None,
        transition_out: DivAppearanceTransition | None = None,
        transition_triggers: list[DivTransitionTrigger | str] | None = None,
        truncate: DivTextTruncate | str | None = None,
        underline: DivLineStyle | str | None = None,
        variable_triggers: list[DivTrigger] | None = None,
        variables: list[DivVariable] | None = None,
        visibility: DivVisibility | str | None = None,
        visibility_action: DivVisibilityAction | None = None,
        visibility_actions: list[DivVisibilityAction] | None = None,
        width: DivSize | None = None,
    ) -> None: ...

class DivTextEllipsis(PyDivEntity):
    def __init__(
        self,
        *,
        text: str,
        actions: list[DivAction] | None = None,
        images: list[DivTextImage] | None = None,
        ranges: list[DivTextRange] | None = None,
    ) -> None: ...

class DivTextImage(PyDivEntity):
    def __init__(
        self,
        *,
        start: int | str,
        url: str,
        accessibility: ImageAccessibility | None = None,
        alignment_vertical: DivTextAlignmentVertical | str | None = None,
        height: DivFixedSize | None = None,
        indexing_direction: ImageIndexingDirection | str | None = None,
        preload_required: bool | int | str | None = None,
        tint_color: str | None = None,
        tint_mode: DivBlendMode | str | None = None,
        width: DivFixedSize | None = None,
    ) -> None: ...

class DivTextRange(PyDivEntity):
    def __init__(
        self,
        *,
        actions: list[DivAction] | None = None,
        alignment_vertical: DivTextAlignmentVertical | str | None = None,
        background: DivTextRangeBackground | None = None,
        baseline_offset: float | str | None = None,
        border: DivTextRangeBorder | None = None,
        end: int | str | None = None,
        font_family: str | None = None,
        font_feature_settings: str | None = None,
        font_size: int | str | None = None,
        font_size_unit: DivSizeUnit | str | None = None,
        font_variation_settings: dict[str, Any] | str | None = None,
        font_weight: DivFontWeight | str | None = None,
        font_weight_value: int | str | None = None,
        letter_spacing: float | str | None = None,
        line_height: int | str | None = None,
        mask: DivTextRangeMask | None = None,
        start: int | str | None = None,
        strike: DivLineStyle | str | None = None,
        text_color: str | None = None,
        text_shadow: DivShadow | None = None,
        top_offset: int | str | None = None,
        underline: DivLineStyle | str | None = None,
    ) -> None: ...

class DivTextRangeBorder(PyDivEntity):
    def __init__(
        self,
        *,
        corner_radius: int | str | None = None,
        stroke: DivStroke | None = None,
    ) -> None: ...

class DivTextRangeMaskBase(PyDivEntity):
    def __init__(
        self,
        *,
        is_enabled: bool | str | None = None,
    ) -> None: ...

class DivTextRangeMaskParticles(PyDivEntity):
    def __init__(
        self,
        *,
        color: str,
        density: float | str | None = None,
        is_animated: bool | str | None = None,
        is_enabled: bool | str | None = None,
        particle_size: DivFixedSize | None = None,
    ) -> None: ...

class DivTextRangeMaskSolid(PyDivEntity):
    def __init__(
        self,
        *,
        color: str,
        is_enabled: bool | str | None = None,
    ) -> None: ...

class DivTimer(PyDivEntity):
    def __init__(
        self,
        *,
        id: str,
        duration: int | str | None = None,
        end_actions: list[DivAction] | None = None,
        tick_actions: list[DivAction] | None = None,
        tick_interval: int | str | None = None,
        value_variable: str | None = None,
    ) -> None: ...

class DivTooltip(PyDivEntity):
    def __init__(
        self,
        *,
        div: Div,
        id: str,
        position: DivTooltipPosition | str,
        animation_in: DivAnimation | None = None,
        animation_out: DivAnimation | None = None,
        background_accessibility_description: str | None = None,
        bring_to_top_id: str | None = None,
        close_by_tap_outside: bool | str | None = None,
        duration: int | str | None = None,
        mode: DivTooltipMode | None = None,
        offset: DivPoint | None = None,
        substrate_div: Div | None = None,
        tap_outside_actions: list[DivAction] | None = None,
    ) -> None: ...

class DivTooltipModeModal(PyDivEntity):
    def __init__(
        self,
    ) -> None: ...

class DivTooltipModeNonModal(PyDivEntity):
    def __init__(
        self,
    ) -> None: ...

class DivTransform(PyDivEntity):
    def __init__(
        self,
        *,
        pivot_x: DivPivot | None = None,
        pivot_y: DivPivot | None = None,
        rotation: float | str | None = None,
    ) -> None: ...

class DivTransitionBase(PyDivEntity):
    def __init__(
        self,
        *,
        duration: int | str | None = None,
        interpolator: DivAnimationInterpolator | str | None = None,
        start_delay: int | str | None = None,
    ) -> None: ...

class DivTranslationTransformation(PyDivEntity):
    def __init__(
        self,
        *,
        x: DivTranslation | None = None,
        y: DivTranslation | None = None,
    ) -> None: ...

class DivTrigger(PyDivEntity):
    def __init__(
        self,
        *,
        actions: list[DivAction],
        condition: bool | int | str,
        mode: DivTriggerMode | str | None = None,
    ) -> None: ...

class DivVideo(PyDivEntity):
    def __init__(
        self,
        *,
        video_sources: list[DivVideoSource],
        accessibility: DivAccessibility | None = None,
        alignment_horizontal: DivAlignmentHorizontal | str | None = None,
        alignment_vertical: DivAlignmentVertical | str | None = None,
        alpha: float | str | None = None,
        animators: list[DivAnimator] | None = None,
        aspect: DivAspect | None = None,
        autostart: bool | int | str | None = None,
        background: list[DivBackground] | None = None,
        border: DivBorder | None = None,
        buffering_actions: list[DivAction] | None = None,
        column_span: int | str | None = None,
        disappear_actions: list[DivDisappearAction] | None = None,
        elapsed_time_variable: str | None = None,
        end_actions: list[DivAction] | None = None,
        extensions: list[DivExtension] | None = None,
        fatal_actions: list[DivAction] | None = None,
        focus: DivFocus | None = None,
        functions: list[DivFunction] | None = None,
        height: DivSize | None = None,
        id: str | None = None,
        layout_provider: DivLayoutProvider | None = None,
        margins: DivEdgeInsets | None = None,
        muted: bool | int | str | None = None,
        paddings: DivEdgeInsets | None = None,
        pause_actions: list[DivAction] | None = None,
        player_settings_payload: dict[str, Any] | None = None,
        preload_required: bool | int | str | None = None,
        preview: str | None = None,
        repeatable: bool | int | str | None = None,
        resume_actions: list[DivAction] | None = None,
        reuse_id: str | None = None,
        row_span: int | str | None = None,
        scale: DivVideoScale | str | None = None,
        selected_actions: list[DivAction] | None = None,
        tooltips: list[DivTooltip] | None = None,
        transform: DivTransform | None = None,
        transformations: list[DivTransformation] | None = None,
        transition_change: DivChangeTransition | None = None,
        transition_in: DivAppearanceTransition | None = None,
        transition_out: DivAppearanceTransition | None = None,
        transition_triggers: list[DivTransitionTrigger | str] | None = None,
        variable_triggers: list[DivTrigger] | None = None,
        variables: list[DivVariable] | None = None,
        visibility: DivVisibility | str | None = None,
        visibility_action: DivVisibilityAction | None = None,
        visibility_actions: list[DivVisibilityAction] | None = None,
        width: DivSize | None = None,
    ) -> None: ...

class DivVideoSource(PyDivEntity):
    def __init__(
        self,
        *,
        mime_type: str,
        url: str,
        bitrate: int | str | None = None,
        resolution: DivVideoSourceResolution | None = None,
    ) -> None: ...

class DivVideoSourceResolution(PyDivEntity):
    def __init__(
        self,
        *,
        height: int | str,
        width: int | str,
    ) -> None: ...

class DivVisibilityAction(PyDivEntity):
    def __init__(
        self,
        *,
        log_id: str,
        download_callbacks: DivDownloadCallbacks | None = None,
        is_enabled: bool | int | str | None = None,
        log_limit: int | str | None = None,
        payload: dict[str, Any] | None = None,
        referer: str | None = None,
        scope_id: str | None = None,
        typed: DivActionTyped | None = None,
        url: str | None = None,
        visibility_duration: int | str | None = None,
        visibility_percentage: int | str | None = None,
    ) -> None: ...

class DivWrapContentSize(PyDivEntity):
    def __init__(
        self,
        *,
        constrained: bool | int | str | None = None,
        max_size: DivSizeUnitValue | None = None,
        min_size: DivSizeUnitValue | None = None,
    ) -> None: ...

class EndDestination(PyDivEntity):
    def __init__(
        self,
    ) -> None: ...

class ImageAccessibility(PyDivEntity):
    def __init__(
        self,
        *,
        description: str | None = None,
        type: AccessibilityType | str | None = None,
    ) -> None: ...

class IndexDestination(PyDivEntity):
    def __init__(
        self,
        *,
        value: int | str,
    ) -> None: ...

class IntegerValue(PyDivEntity):
    def __init__(
        self,
        *,
        value: int | str,
    ) -> None: ...

class IntegerVariable(PyDivEntity):
    def __init__(
        self,
        *,
        name: str,
        value: int | str,
    ) -> None: ...

class NumberValue(PyDivEntity):
    def __init__(
        self,
        *,
        value: float | str,
    ) -> None: ...

class NumberVariable(PyDivEntity):
    def __init__(
        self,
        *,
        name: str,
        value: float | str,
    ) -> None: ...

class OffsetDestination(PyDivEntity):
    def __init__(
        self,
        *,
        value: int | str,
    ) -> None: ...

class PropertyVariable(PyDivEntity):
    def __init__(
        self,
        *,
        get: str,
        name: str,
        value_type: DivEvaluableType | str,
        new_value_variable_name: str | None = None,
        set: list[DivAction] | None = None,
    ) -> None: ...

class RequestHeader(PyDivEntity):
    def __init__(
        self,
        *,
        name: str,
        value: str,
    ) -> None: ...

class StartDestination(PyDivEntity):
    def __init__(
        self,
    ) -> None: ...

class StringValue(PyDivEntity):
    def __init__(
        self,
        *,
        value: str,
    ) -> None: ...

class StringVariable(PyDivEntity):
    def __init__(
        self,
        *,
        name: str,
        value: str,
    ) -> None: ...

class UrlValue(PyDivEntity):
    def __init__(
        self,
        *,
        value: str,
    ) -> None: ...

class UrlVariable(PyDivEntity):
    def __init__(
        self,
        *,
        name: str,
        value: str,
    ) -> None: ...


Div: TypeAlias = DivImage | DivGifImage | DivText | DivSeparator | DivContainer | DivGrid | DivGallery | DivPager | DivTabs | DivState | DivCustom | DivIndicator | DivSlider | DivSwitch | DivInput | DivSelect | DivVideo
DivActionCopyToClipboardContent: TypeAlias = ContentText | ContentUrl
DivActionScrollDestination: TypeAlias = OffsetDestination | IndexDestination | StartDestination | EndDestination
DivActionTyped: TypeAlias = DivActionAnimatorStart | DivActionAnimatorStop | DivActionArrayInsertValue | DivActionArrayRemoveValue | DivActionArraySetValue | DivActionClearFocus | DivActionCopyToClipboard | DivActionDictSetValue | DivActionDownload | DivActionFocusElement | DivActionHideTooltip | DivActionScrollBy | DivActionScrollTo | DivActionSetState | DivActionSetStoredValue | DivActionSetVariable | DivActionShowTooltip | DivActionSubmit | DivActionTimer | DivActionUpdateStructure | DivActionVideo | DivActionCustom
DivAnimator: TypeAlias = DivColorAnimator | DivNumberAnimator
DivAppearanceTransition: TypeAlias = DivAppearanceSetTransition | DivFadeTransition | DivScaleTransition | DivSlideTransition
DivBackground: TypeAlias = DivLinearGradient | DivRadialGradient | DivImageBackground | DivSolidBackground | DivNinePatchBackground
DivChangeTransition: TypeAlias = DivChangeSetTransition | DivChangeBoundsTransition
DivCount: TypeAlias = DivInfinityCount | DivFixedCount
DivDrawable: TypeAlias = DivShapeDrawable
DivFilter: TypeAlias = DivBlur | DivFilterRtlMirror
DivIndicatorItemPlacement: TypeAlias = DivDefaultIndicatorItemPlacement | DivStretchIndicatorItemPlacement
DivInputFilter: TypeAlias = DivInputFilterRegex | DivInputFilterExpression
DivInputMask: TypeAlias = DivFixedLengthInputMask | DivCurrencyInputMask | DivPhoneInputMask
DivInputValidator: TypeAlias = DivInputValidatorRegex | DivInputValidatorExpression
DivPageTransformation: TypeAlias = DivPageTransformationSlide | DivPageTransformationOverlap
DivPagerLayoutMode: TypeAlias = DivPageSize | DivNeighbourPageSize | DivPageContentSize
DivPivot: TypeAlias = DivPivotFixed | DivPivotPercentage
DivRadialGradientCenter: TypeAlias = DivRadialGradientFixedCenter | DivRadialGradientRelativeCenter
DivRadialGradientRadius: TypeAlias = DivFixedSize | DivRadialGradientRelativeRadius
DivShape: TypeAlias = DivRoundedRectangleShape | DivCircleShape
DivSize: TypeAlias = DivFixedSize | DivMatchParentSize | DivWrapContentSize
DivStrokeStyle: TypeAlias = DivStrokeStyleSolid | DivStrokeStyleDashed
DivTextGradient: TypeAlias = DivLinearGradient | DivRadialGradient
DivTextRangeBackground: TypeAlias = DivSolidBackground | DivCloudBackground
DivTextRangeMask: TypeAlias = DivTextRangeMaskParticles | DivTextRangeMaskSolid
DivTooltipMode: TypeAlias = DivTooltipModeNonModal | DivTooltipModeModal
DivTransformation: TypeAlias = DivRotationTransformation | DivTranslationTransformation
DivTranslation: TypeAlias = DivFixedTranslation | DivPercentageTranslation
DivTypedValue: TypeAlias = StringValue | IntegerValue | NumberValue | ColorValue | BooleanValue | UrlValue | DictValue | ArrayValue
DivVariable: TypeAlias = StringVariable | NumberVariable | IntegerVariable | BooleanVariable | ColorVariable | UrlVariable | DictVariable | ArrayVariable | PropertyVariable
