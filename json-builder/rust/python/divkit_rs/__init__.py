"""divkit_rs — Fast Rust-backed DivKit JSON builder."""

from types import SimpleNamespace

from divkit_rs._native import (
    # ----------------------------------------------------------------
    # Enums
    # ----------------------------------------------------------------
    AccessibilityType,
    # ----------------------------------------------------------------
    # Typed value types
    # ----------------------------------------------------------------
    ArrayValue,
    # ----------------------------------------------------------------
    # Variable types
    # ----------------------------------------------------------------
    ArrayVariable,
    BooleanValue,
    BooleanVariable,
    ColorValue,
    ColorVariable,
    ContentText,
    ContentUrl,
    DelimiterStyleOrientation,
    DictValue,
    DictVariable,
    # ----------------------------------------------------------------
    # Type aliases (Union types)
    # ----------------------------------------------------------------
    Div,
    # ----------------------------------------------------------------
    # Basic / structural types
    # ----------------------------------------------------------------
    DivAbsoluteEdgeInsets,
    DivAccessibility,
    DivAccessibilityMode,
    DivAccessibilityType,
    # ----------------------------------------------------------------
    # Actions
    # ----------------------------------------------------------------
    DivAction,
    DivActionAnimatorStart,
    DivActionAnimatorStop,
    DivActionArrayInsertValue,
    DivActionArrayRemoveValue,
    DivActionArraySetValue,
    DivActionClearFocus,
    DivActionCopyToClipboard,
    DivActionCopyToClipboardContent,
    DivActionCustom,
    DivActionDictSetValue,
    DivActionDownload,
    DivActionFocusElement,
    DivActionHideTooltip,
    DivActionMenuItem,
    DivActionScrollBy,
    DivActionScrollByOverflow,
    DivActionScrollDestination,
    DivActionScrollTo,
    DivActionSetState,
    DivActionSetStoredValue,
    DivActionSetVariable,
    DivActionShowTooltip,
    DivActionSubmit,
    DivActionSubmitRequest,
    DivActionTarget,
    DivActionTimer,
    DivActionTimerAction,
    DivActionTyped,
    DivActionUpdateStructure,
    DivActionVideo,
    DivActionVideoAction,
    DivAlignmentHorizontal,
    DivAlignmentVertical,
    # ----------------------------------------------------------------
    # Animation / animator types
    # ----------------------------------------------------------------
    DivAnimation,
    DivAnimationDirection,
    DivAnimationInterpolator,
    DivAnimationName,
    DivAnimator,
    DivAnimatorBase,
    # ----------------------------------------------------------------
    # Transition types
    # ----------------------------------------------------------------
    DivAppearanceSetTransition,
    DivAppearanceTransition,
    DivAspect,
    DivBackground,
    DivBase,
    DivBlendMode,
    DivBlur,
    DivBorder,
    DivChangeBoundsTransition,
    DivChangeSetTransition,
    DivChangeTransition,
    # ----------------------------------------------------------------
    # Shape types
    # ----------------------------------------------------------------
    DivCircleShape,
    # ----------------------------------------------------------------
    # Background types
    # ----------------------------------------------------------------
    DivCloudBackground,
    # ----------------------------------------------------------------
    # Collection item builder
    # ----------------------------------------------------------------
    DivCollectionItemBuilder,
    DivCollectionItemBuilderPrototype,
    DivColorAnimator,
    # ----------------------------------------------------------------
    # Main div components
    # ----------------------------------------------------------------
    DivContainer,
    DivContainerLayoutMode,
    DivContainerOrientation,
    DivContainerSeparator,
    DivContentAlignmentHorizontal,
    DivContentAlignmentVertical,
    DivCornersRadius,
    DivCount,
    # ----------------------------------------------------------------
    # Input mask / filter / validator types
    # ----------------------------------------------------------------
    DivCurrencyInputMask,
    DivCustom,
    # ----------------------------------------------------------------
    # Indicator / pager layout types
    # ----------------------------------------------------------------
    DivDefaultIndicatorItemPlacement,
    DivDimension,
    # ----------------------------------------------------------------
    # Sight / visibility / disappear actions
    # ----------------------------------------------------------------
    DivDisappearAction,
    DivDownloadCallbacks,
    DivDrawable,
    DivEdgeInsets,
    DivEvaluableType,
    DivExtension,
    DivFadeTransition,
    DivFilter,
    # ----------------------------------------------------------------
    # Filter types
    # ----------------------------------------------------------------
    DivFilterRtlMirror,
    # ----------------------------------------------------------------
    # Count types
    # ----------------------------------------------------------------
    DivFixedCount,
    DivFixedLengthInputMask,
    DivFixedLengthInputMaskPatternElement,
    # ----------------------------------------------------------------
    # Size types
    # ----------------------------------------------------------------
    DivFixedSize,
    # ----------------------------------------------------------------
    # Transformation types
    # ----------------------------------------------------------------
    DivFixedTranslation,
    DivFocus,
    DivFocusNextFocusIds,
    DivFontWeight,
    DivFunction,
    DivFunctionArgument,
    DivGallery,
    DivGalleryCrossContentAlignment,
    DivGalleryOrientation,
    DivGalleryScrollbar,
    DivGalleryScrollMode,
    DivGifImage,
    DivGrid,
    DivImage,
    DivImageBackground,
    DivImageScale,
    DivIndicator,
    DivIndicatorAnimation,
    DivIndicatorItemPlacement,
    DivInfinityCount,
    DivInput,
    DivInputAutocapitalization,
    DivInputEnterKeyType,
    DivInputFilter,
    DivInputFilterExpression,
    DivInputFilterRegex,
    DivInputKeyboardType,
    DivInputMask,
    DivInputMaskBase,
    DivInputNativeInterface,
    DivInputValidator,
    DivInputValidatorBase,
    DivInputValidatorExpression,
    DivInputValidatorRegex,
    DivLayoutProvider,
    DivLinearGradient,
    DivLinearGradientColorPoint,
    DivLineStyle,
    DivMatchParentSize,
    DivNeighbourPageSize,
    DivNinePatchBackground,
    DivNumberAnimator,
    DivPageContentSize,
    DivPager,
    DivPagerItemAlignment,
    DivPagerLayoutMode,
    DivPagerOrientation,
    DivPageSize,
    DivPageTransformation,
    DivPageTransformationOverlap,
    DivPageTransformationSlide,
    # ----------------------------------------------------------------
    # Patch types
    # ----------------------------------------------------------------
    DivPatch,
    DivPatchChange,
    DivPatchMode,
    DivPercentageSize,
    DivPercentageTranslation,
    DivPhoneInputMask,
    DivPivot,
    DivPivotFixed,
    DivPivotPercentage,
    DivPoint,
    # NOTE: DivGradientBackground was renamed to DivLinearGradient in the schema.
    # The alias below preserves backward compatibility with pydivkit code.
    DivRadialGradient,
    DivRadialGradientCenter,
    DivRadialGradientColorPoint,
    DivRadialGradientFixedCenter,
    DivRadialGradientRadius,
    DivRadialGradientRelativeCenter,
    DivRadialGradientRelativeRadius,
    DivRadialGradientRelativeRadiusValue,
    DivRotationTransformation,
    DivRoundedRectangleShape,
    DivScaleTransition,
    DivSelect,
    DivSelectOption,
    DivSeparator,
    DivSeparatorDelimiterStyle,
    DivShadow,
    DivShape,
    DivShapeDrawable,
    DivSightAction,
    DivSize,
    DivSizeUnit,
    DivSizeUnitValue,
    DivSlider,
    DivSliderRange,
    DivSliderTextStyle,
    DivSlideTransition,
    DivSlideTransitionEdge,
    DivSolidBackground,
    DivState,
    DivStateState,
    DivStretchIndicatorItemPlacement,
    DivStroke,
    DivStrokeStyle,
    DivStrokeStyleDashed,
    DivStrokeStyleSolid,
    DivSwitch,
    DivTabs,
    DivTabsItem,
    DivTabsTabTitleDelimiter,
    DivTabsTabTitleStyle,
    DivText,
    DivTextAlignmentVertical,
    # ----------------------------------------------------------------
    # Text sub-types
    # ----------------------------------------------------------------
    DivTextEllipsis,
    DivTextGradient,
    DivTextImage,
    DivTextRange,
    DivTextRangeBackground,
    DivTextRangeBorder,
    DivTextRangeMask,
    DivTextRangeMaskBase,
    DivTextRangeMaskParticles,
    DivTextRangeMaskSolid,
    DivTextTruncate,
    DivTimer,
    # ----------------------------------------------------------------
    # Tooltip types
    # ----------------------------------------------------------------
    DivTooltip,
    DivTooltipMode,
    DivTooltipModeModal,
    DivTooltipModeNonModal,
    DivTooltipPosition,
    DivTransform,
    DivTransformation,
    DivTransitionBase,
    DivTransitionSelector,
    DivTransitionTrigger,
    DivTranslation,
    DivTranslationTransformation,
    # ----------------------------------------------------------------
    # Trigger / timer types
    # ----------------------------------------------------------------
    DivTrigger,
    DivTriggerMode,
    DivTypedValue,
    DivVariable,
    DivVideo,
    DivVideoScale,
    DivVideoSource,
    DivVideoSourceResolution,
    DivVisibility,
    DivVisibilityAction,
    DivWrapContentSize,
    # ----------------------------------------------------------------
    # Scroll destination types
    # ----------------------------------------------------------------
    EndDestination,
    ImageAccessibility,
    ImageIndexingDirection,
    IndexDestination,
    IntegerValue,
    IntegerVariable,
    NumberValue,
    NumberVariable,
    OffsetDestination,
    PropertyVariable,
    # Base class
    PyDivEntity,
    RequestHeader,
    RequestMethod,
    StartDestination,
    StringValue,
    StringVariable,
    TabTitleStyleAnimationType,
    UrlValue,
    UrlVariable,
)
from divkit_rs._native import (
    # Top-level helpers
    PyDivData as DivData,
)
from divkit_rs._native import (
    PyDivDataState as DivDataState,
)
from divkit_rs._native import (
    make_card as _native_make_card,
)
from divkit_rs._native import (
    make_div as _native_make_div,
)
from divkit_rs._pydivkit_compat import (
    install_pydivkit_compat,
)
from divkit_rs._pydivkit_compat import (
    make_card as _compat_make_card,
)
from divkit_rs._pydivkit_compat import (
    make_div as _compat_make_div,
)
from divkit_rs.core import BaseDiv, BaseEntity, Expr, Field, Ref

# Backward-compatible alias: pydivkit used DivGradientBackground,
# the schema now calls it DivLinearGradient.
DivGradientBackground = DivLinearGradient

install_pydivkit_compat()

# pydivkit-compatible helpers (Python-side, with template support).
make_div = _compat_make_div
make_card = _compat_make_card

# Keep native helpers available for explicit low-level usage.
native_make_div = _native_make_div
native_make_card = _native_make_card

# Compatibility namespaces for imports like `from pydivkit.div import div_timer`.
div_timer = SimpleNamespace(DivTimer=DivTimer)
div_trigger = SimpleNamespace(DivTrigger=DivTrigger)
div_variable = SimpleNamespace(
    DivVariable=DivVariable,
    ArrayVariable=ArrayVariable,
    BooleanVariable=BooleanVariable,
    ColorVariable=ColorVariable,
    DictVariable=DictVariable,
    IntegerVariable=IntegerVariable,
    NumberVariable=NumberVariable,
    PropertyVariable=PropertyVariable,
    StringVariable=StringVariable,
    UrlVariable=UrlVariable,
)

__all__ = [
    "PyDivEntity",
    "BaseEntity",
    "BaseDiv",
    "Field",
    "Ref",
    "Expr",
    "DivData",
    "DivDataState",
    "make_div",
    "make_card",
    "native_make_div",
    "native_make_card",
    "div_timer",
    "div_trigger",
    "div_variable",
    # Size types
    "DivFixedSize",
    "DivMatchParentSize",
    "DivWrapContentSize",
    "DivPercentageSize",
    # Basic / structural types
    "DivAbsoluteEdgeInsets",
    "DivAccessibility",
    "DivAspect",
    "DivBase",
    "DivBlur",
    "DivBorder",
    "DivCornersRadius",
    "DivDimension",
    "DivEdgeInsets",
    "DivExtension",
    "DivFocus",
    "DivFocusNextFocusIds",
    "DivFunction",
    "DivFunctionArgument",
    "DivLayoutProvider",
    "DivPoint",
    "DivShadow",
    "DivSizeUnitValue",
    "DivStroke",
    "DivStrokeStyleDashed",
    "DivStrokeStyleSolid",
    "DivTransform",
    "DivTransitionBase",
    # Actions
    "DivAction",
    "DivActionAnimatorStart",
    "DivActionAnimatorStop",
    "DivActionArrayInsertValue",
    "DivActionArrayRemoveValue",
    "DivActionArraySetValue",
    "DivActionClearFocus",
    "DivActionCopyToClipboard",
    "DivActionCustom",
    "DivActionDictSetValue",
    "DivActionDownload",
    "DivActionFocusElement",
    "DivActionHideTooltip",
    "DivActionMenuItem",
    "DivActionScrollBy",
    "DivActionScrollTo",
    "DivActionSetState",
    "DivActionSetStoredValue",
    "DivActionSetVariable",
    "DivActionShowTooltip",
    "DivActionSubmit",
    "DivActionSubmitRequest",
    "DivActionTimer",
    "DivActionUpdateStructure",
    "DivActionVideo",
    "RequestHeader",
    # Sight / visibility / disappear actions
    "DivDisappearAction",
    "DivDownloadCallbacks",
    "DivSightAction",
    "DivVisibilityAction",
    # Background types
    "DivCloudBackground",
    "DivImageBackground",
    "DivGradientBackground",  # compat alias for DivLinearGradient
    "DivLinearGradient",
    "DivLinearGradientColorPoint",
    "DivNinePatchBackground",
    "DivRadialGradient",
    "DivRadialGradientColorPoint",
    "DivRadialGradientFixedCenter",
    "DivRadialGradientRelativeCenter",
    "DivRadialGradientRelativeRadius",
    "DivSolidBackground",
    # Shape types
    "DivCircleShape",
    "DivRoundedRectangleShape",
    "DivShapeDrawable",
    # Transition types
    "DivAppearanceSetTransition",
    "DivChangeBoundsTransition",
    "DivChangeSetTransition",
    "DivFadeTransition",
    "DivScaleTransition",
    "DivSlideTransition",
    # Transformation types
    "DivFixedTranslation",
    "DivPercentageTranslation",
    "DivPivotFixed",
    "DivPivotPercentage",
    "DivRotationTransformation",
    "DivTranslationTransformation",
    # Animation / animator types
    "DivAnimation",
    "DivAnimatorBase",
    "DivColorAnimator",
    "DivNumberAnimator",
    # Main div components
    "DivContainer",
    "DivContainerSeparator",
    "DivCustom",
    "DivGallery",
    "DivGifImage",
    "DivGrid",
    "DivImage",
    "DivIndicator",
    "DivInput",
    "DivInputNativeInterface",
    "DivPager",
    "DivSelect",
    "DivSelectOption",
    "DivSeparator",
    "DivSeparatorDelimiterStyle",
    "DivSlider",
    "DivSliderRange",
    "DivSliderTextStyle",
    "DivState",
    "DivStateState",
    "DivSwitch",
    "DivTabs",
    "DivTabsItem",
    "DivTabsTabTitleDelimiter",
    "DivTabsTabTitleStyle",
    "DivText",
    "DivVideo",
    "DivVideoSource",
    "DivVideoSourceResolution",
    # Text sub-types
    "DivTextEllipsis",
    "DivTextImage",
    "DivTextRange",
    "DivTextRangeBorder",
    "DivTextRangeMaskBase",
    "DivTextRangeMaskParticles",
    "DivTextRangeMaskSolid",
    "ImageAccessibility",
    # Input mask / filter / validator types
    "DivCurrencyInputMask",
    "DivFixedLengthInputMask",
    "DivFixedLengthInputMaskPatternElement",
    "DivInputFilterExpression",
    "DivInputFilterRegex",
    "DivInputMaskBase",
    "DivInputValidatorBase",
    "DivInputValidatorExpression",
    "DivInputValidatorRegex",
    "DivPhoneInputMask",
    # Indicator / pager layout types
    "DivDefaultIndicatorItemPlacement",
    "DivStretchIndicatorItemPlacement",
    "DivNeighbourPageSize",
    "DivPageContentSize",
    "DivPageSize",
    "DivPageTransformationOverlap",
    "DivPageTransformationSlide",
    # Filter types
    "DivFilterRtlMirror",
    # Tooltip types
    "DivTooltip",
    "DivTooltipModeModal",
    "DivTooltipModeNonModal",
    # Trigger / timer types
    "DivTrigger",
    "DivTimer",
    # Patch types
    "DivPatch",
    "DivPatchChange",
    # Collection item builder
    "DivCollectionItemBuilder",
    "DivCollectionItemBuilderPrototype",
    # Count types
    "DivFixedCount",
    "DivInfinityCount",
    # Scroll destination types
    "EndDestination",
    "IndexDestination",
    "OffsetDestination",
    "StartDestination",
    # Variable types
    "ArrayVariable",
    "BooleanVariable",
    "ColorVariable",
    "DictVariable",
    "IntegerVariable",
    "NumberVariable",
    "PropertyVariable",
    "StringVariable",
    "UrlVariable",
    # Typed value types
    "ArrayValue",
    "BooleanValue",
    "ColorValue",
    "ContentText",
    "ContentUrl",
    "DictValue",
    "IntegerValue",
    "NumberValue",
    "StringValue",
    "UrlValue",
    # Enums
    "AccessibilityType",
    "DelimiterStyleOrientation",
    "DivAccessibilityMode",
    "DivAccessibilityType",
    "DivActionScrollByOverflow",
    "DivActionTarget",
    "DivActionTimerAction",
    "DivActionVideoAction",
    "DivAlignmentHorizontal",
    "DivAlignmentVertical",
    "DivAnimationDirection",
    "DivAnimationInterpolator",
    "DivAnimationName",
    "DivBlendMode",
    "DivContainerLayoutMode",
    "DivContainerOrientation",
    "DivContentAlignmentHorizontal",
    "DivContentAlignmentVertical",
    "DivEvaluableType",
    "DivFontWeight",
    "DivGalleryCrossContentAlignment",
    "DivGalleryOrientation",
    "DivGalleryScrollbar",
    "DivGalleryScrollMode",
    "DivImageScale",
    "DivIndicatorAnimation",
    "DivInputAutocapitalization",
    "DivInputEnterKeyType",
    "DivInputKeyboardType",
    "DivLineStyle",
    "DivPagerItemAlignment",
    "DivPagerOrientation",
    "DivPatchMode",
    "DivRadialGradientRelativeRadiusValue",
    "DivSizeUnit",
    "DivSlideTransitionEdge",
    "DivTextAlignmentVertical",
    "DivTextTruncate",
    "DivTooltipPosition",
    "DivTransitionSelector",
    "DivTransitionTrigger",
    "DivTriggerMode",
    "DivVideoScale",
    "DivVisibility",
    "ImageIndexingDirection",
    "RequestMethod",
    "TabTitleStyleAnimationType",
    # Type aliases (Union types)
    "Div",
    "DivActionCopyToClipboardContent",
    "DivActionScrollDestination",
    "DivActionTyped",
    "DivAnimator",
    "DivAppearanceTransition",
    "DivBackground",
    "DivChangeTransition",
    "DivCount",
    "DivDrawable",
    "DivFilter",
    "DivIndicatorItemPlacement",
    "DivInputFilter",
    "DivInputMask",
    "DivInputValidator",
    "DivPageTransformation",
    "DivPagerLayoutMode",
    "DivPivot",
    "DivRadialGradientCenter",
    "DivRadialGradientRadius",
    "DivShape",
    "DivSize",
    "DivStrokeStyle",
    "DivTextGradient",
    "DivTextRangeBackground",
    "DivTextRangeMask",
    "DivTooltipMode",
    "DivTransformation",
    "DivTranslation",
    "DivTypedValue",
    "DivVariable",
]
