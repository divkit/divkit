// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivSliderTemplate: TemplateValue {
  public final class TextStyleTemplate: TemplateValue {
    public let fontSize: Field<Expression<Int>>? // constraint: number >= 0
    public let fontSizeUnit: Field<Expression<DivSizeUnit>>? // default value: sp
    public let fontWeight: Field<Expression<DivFontWeight>>? // default value: regular
    public let offset: Field<DivPointTemplate>?
    public let textColor: Field<Expression<Color>>? // default value: #FF000000

    public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
      do {
        self.init(
          fontSize: try dictionary.getOptionalExpressionField("font_size"),
          fontSizeUnit: try dictionary.getOptionalExpressionField("font_size_unit"),
          fontWeight: try dictionary.getOptionalExpressionField("font_weight"),
          offset: try dictionary.getOptionalField("offset", templateToType: templateToType),
          textColor: try dictionary.getOptionalExpressionField("text_color", transform: Color.color(withHexString:))
        )
      } catch let DeserializationError.invalidFieldRepresentation(field: field, representation: representation) {
        throw DeserializationError.invalidFieldRepresentation(field: "text_style_template." + field, representation: representation)
      }
    }

    init(
      fontSize: Field<Expression<Int>>? = nil,
      fontSizeUnit: Field<Expression<DivSizeUnit>>? = nil,
      fontWeight: Field<Expression<DivFontWeight>>? = nil,
      offset: Field<DivPointTemplate>? = nil,
      textColor: Field<Expression<Color>>? = nil
    ) {
      self.fontSize = fontSize
      self.fontSizeUnit = fontSizeUnit
      self.fontWeight = fontWeight
      self.offset = offset
      self.textColor = textColor
    }

    private static func resolveOnlyLinks(context: TemplatesContext, parent: TextStyleTemplate?) -> DeserializationResult<DivSlider.TextStyle> {
      let fontSizeValue = parent?.fontSize?.resolveValue(context: context, validator: ResolvedValue.fontSizeValidator) ?? .noValue
      let fontSizeUnitValue = parent?.fontSizeUnit?.resolveOptionalValue(context: context, validator: ResolvedValue.fontSizeUnitValidator) ?? .noValue
      let fontWeightValue = parent?.fontWeight?.resolveOptionalValue(context: context, validator: ResolvedValue.fontWeightValidator) ?? .noValue
      let offsetValue = parent?.offset?.resolveOptionalValue(context: context, validator: ResolvedValue.offsetValidator, useOnlyLinks: true) ?? .noValue
      let textColorValue = parent?.textColor?.resolveOptionalValue(context: context, transform: Color.color(withHexString:), validator: ResolvedValue.textColorValidator) ?? .noValue
      var errors = mergeErrors(
        fontSizeValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_size", error: $0) },
        fontSizeUnitValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_size_unit", error: $0) },
        fontWeightValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_weight", error: $0) },
        offsetValue.errorsOrWarnings?.map { .nestedObjectError(field: "offset", error: $0) },
        textColorValue.errorsOrWarnings?.map { .nestedObjectError(field: "text_color", error: $0) }
      )
      if case .noValue = fontSizeValue {
        errors.append(.requiredFieldIsMissing(field: "font_size"))
      }
      guard
        let fontSizeNonNil = fontSizeValue.value
      else {
        return .failure(NonEmptyArray(errors)!)
      }
      let result = DivSlider.TextStyle(
        fontSize: fontSizeNonNil,
        fontSizeUnit: fontSizeUnitValue.value,
        fontWeight: fontWeightValue.value,
        offset: offsetValue.value,
        textColor: textColorValue.value
      )
      return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
    }

    public static func resolveValue(context: TemplatesContext, parent: TextStyleTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivSlider.TextStyle> {
      if useOnlyLinks {
        return resolveOnlyLinks(context: context, parent: parent)
      }
      var fontSizeValue: DeserializationResult<Expression<Int>> = parent?.fontSize?.value() ?? .noValue
      var fontSizeUnitValue: DeserializationResult<Expression<DivSizeUnit>> = parent?.fontSizeUnit?.value() ?? .noValue
      var fontWeightValue: DeserializationResult<Expression<DivFontWeight>> = parent?.fontWeight?.value() ?? .noValue
      var offsetValue: DeserializationResult<DivPoint> = .noValue
      var textColorValue: DeserializationResult<Expression<Color>> = parent?.textColor?.value() ?? .noValue
      context.templateData.forEach { key, __dictValue in
        switch key {
        case "font_size":
          fontSizeValue = deserialize(__dictValue, validator: ResolvedValue.fontSizeValidator).merged(with: fontSizeValue)
        case "font_size_unit":
          fontSizeUnitValue = deserialize(__dictValue, validator: ResolvedValue.fontSizeUnitValidator).merged(with: fontSizeUnitValue)
        case "font_weight":
          fontWeightValue = deserialize(__dictValue, validator: ResolvedValue.fontWeightValidator).merged(with: fontWeightValue)
        case "offset":
          offsetValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.offsetValidator, type: DivPointTemplate.self).merged(with: offsetValue)
        case "text_color":
          textColorValue = deserialize(__dictValue, transform: Color.color(withHexString:), validator: ResolvedValue.textColorValidator).merged(with: textColorValue)
        case parent?.fontSize?.link:
          fontSizeValue = fontSizeValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.fontSizeValidator))
        case parent?.fontSizeUnit?.link:
          fontSizeUnitValue = fontSizeUnitValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.fontSizeUnitValidator))
        case parent?.fontWeight?.link:
          fontWeightValue = fontWeightValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.fontWeightValidator))
        case parent?.offset?.link:
          offsetValue = offsetValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.offsetValidator, type: DivPointTemplate.self))
        case parent?.textColor?.link:
          textColorValue = textColorValue.merged(with: deserialize(__dictValue, transform: Color.color(withHexString:), validator: ResolvedValue.textColorValidator))
        default: break
        }
      }
      if let parent = parent {
        offsetValue = offsetValue.merged(with: parent.offset?.resolveOptionalValue(context: context, validator: ResolvedValue.offsetValidator, useOnlyLinks: true))
      }
      var errors = mergeErrors(
        fontSizeValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_size", error: $0) },
        fontSizeUnitValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_size_unit", error: $0) },
        fontWeightValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_weight", error: $0) },
        offsetValue.errorsOrWarnings?.map { .nestedObjectError(field: "offset", error: $0) },
        textColorValue.errorsOrWarnings?.map { .nestedObjectError(field: "text_color", error: $0) }
      )
      if case .noValue = fontSizeValue {
        errors.append(.requiredFieldIsMissing(field: "font_size"))
      }
      guard
        let fontSizeNonNil = fontSizeValue.value
      else {
        return .failure(NonEmptyArray(errors)!)
      }
      let result = DivSlider.TextStyle(
        fontSize: fontSizeNonNil,
        fontSizeUnit: fontSizeUnitValue.value,
        fontWeight: fontWeightValue.value,
        offset: offsetValue.value,
        textColor: textColorValue.value
      )
      return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
    }

    private func mergedWithParent(templates: [TemplateName: Any]) throws -> TextStyleTemplate {
      return self
    }

    public func resolveParent(templates: [TemplateName: Any]) throws -> TextStyleTemplate {
      let merged = try mergedWithParent(templates: templates)

      return TextStyleTemplate(
        fontSize: merged.fontSize,
        fontSizeUnit: merged.fontSizeUnit,
        fontWeight: merged.fontWeight,
        offset: merged.offset?.tryResolveParent(templates: templates),
        textColor: merged.textColor
      )
    }
  }

  public static let type: String = "slider"
  public let parent: String? // at least 1 char
  public let accessibility: Field<DivAccessibilityTemplate>?
  public let alignmentHorizontal: Field<Expression<DivAlignmentHorizontal>>?
  public let alignmentVertical: Field<Expression<DivAlignmentVertical>>?
  public let alpha: Field<Expression<Double>>? // constraint: number >= 0.0 && number <= 1.0; default value: 1.0
  public let background: Field<[DivBackgroundTemplate]>? // at least 1 elements
  public let border: Field<DivBorderTemplate>?
  public let columnSpan: Field<Expression<Int>>? // constraint: number >= 0
  public let extensions: Field<[DivExtensionTemplate]>? // at least 1 elements
  public let focus: Field<DivFocusTemplate>?
  public let height: Field<DivSizeTemplate>? // default value: .divWrapContentSize(DivWrapContentSize())
  public let id: Field<String>? // at least 1 char
  public let margins: Field<DivEdgeInsetsTemplate>?
  public let maxValue: Field<Expression<Int>>? // default value: 100
  public let minValue: Field<Expression<Int>>? // default value: 0
  public let paddings: Field<DivEdgeInsetsTemplate>?
  public let rowSpan: Field<Expression<Int>>? // constraint: number >= 0
  public let secondaryValueAccessibility: Field<DivAccessibilityTemplate>?
  public let selectedActions: Field<[DivActionTemplate]>? // at least 1 elements
  public let thumbSecondaryStyle: Field<DivDrawableTemplate>?
  public let thumbSecondaryTextStyle: Field<TextStyleTemplate>?
  public let thumbSecondaryValueVariable: Field<String>? // at least 1 char
  public let thumbStyle: Field<DivDrawableTemplate>?
  public let thumbTextStyle: Field<TextStyleTemplate>?
  public let thumbValueVariable: Field<String>? // at least 1 char
  public let tickMarkActiveStyle: Field<DivDrawableTemplate>?
  public let tickMarkInactiveStyle: Field<DivDrawableTemplate>?
  public let tooltips: Field<[DivTooltipTemplate]>? // at least 1 elements
  public let trackActiveStyle: Field<DivDrawableTemplate>?
  public let trackInactiveStyle: Field<DivDrawableTemplate>?
  public let transform: Field<DivTransformTemplate>?
  public let transitionChange: Field<DivChangeTransitionTemplate>?
  public let transitionIn: Field<DivAppearanceTransitionTemplate>?
  public let transitionOut: Field<DivAppearanceTransitionTemplate>?
  public let transitionTriggers: Field<[DivTransitionTrigger]>? // at least 1 elements
  public let visibility: Field<Expression<DivVisibility>>? // default value: visible
  public let visibilityAction: Field<DivVisibilityActionTemplate>?
  public let visibilityActions: Field<[DivVisibilityActionTemplate]>? // at least 1 elements
  public let width: Field<DivSizeTemplate>? // default value: .divMatchParentSize(DivMatchParentSize())

  static let parentValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    do {
      self.init(
        parent: try dictionary.getOptionalField("type", validator: Self.parentValidator),
        accessibility: try dictionary.getOptionalField("accessibility", templateToType: templateToType),
        alignmentHorizontal: try dictionary.getOptionalExpressionField("alignment_horizontal"),
        alignmentVertical: try dictionary.getOptionalExpressionField("alignment_vertical"),
        alpha: try dictionary.getOptionalExpressionField("alpha"),
        background: try dictionary.getOptionalArray("background", templateToType: templateToType),
        border: try dictionary.getOptionalField("border", templateToType: templateToType),
        columnSpan: try dictionary.getOptionalExpressionField("column_span"),
        extensions: try dictionary.getOptionalArray("extensions", templateToType: templateToType),
        focus: try dictionary.getOptionalField("focus", templateToType: templateToType),
        height: try dictionary.getOptionalField("height", templateToType: templateToType),
        id: try dictionary.getOptionalField("id"),
        margins: try dictionary.getOptionalField("margins", templateToType: templateToType),
        maxValue: try dictionary.getOptionalExpressionField("max_value"),
        minValue: try dictionary.getOptionalExpressionField("min_value"),
        paddings: try dictionary.getOptionalField("paddings", templateToType: templateToType),
        rowSpan: try dictionary.getOptionalExpressionField("row_span"),
        secondaryValueAccessibility: try dictionary.getOptionalField("secondary_value_accessibility", templateToType: templateToType),
        selectedActions: try dictionary.getOptionalArray("selected_actions", templateToType: templateToType),
        thumbSecondaryStyle: try dictionary.getOptionalField("thumb_secondary_style", templateToType: templateToType),
        thumbSecondaryTextStyle: try dictionary.getOptionalField("thumb_secondary_text_style", templateToType: templateToType),
        thumbSecondaryValueVariable: try dictionary.getOptionalField("thumb_secondary_value_variable"),
        thumbStyle: try dictionary.getOptionalField("thumb_style", templateToType: templateToType),
        thumbTextStyle: try dictionary.getOptionalField("thumb_text_style", templateToType: templateToType),
        thumbValueVariable: try dictionary.getOptionalField("thumb_value_variable"),
        tickMarkActiveStyle: try dictionary.getOptionalField("tick_mark_active_style", templateToType: templateToType),
        tickMarkInactiveStyle: try dictionary.getOptionalField("tick_mark_inactive_style", templateToType: templateToType),
        tooltips: try dictionary.getOptionalArray("tooltips", templateToType: templateToType),
        trackActiveStyle: try dictionary.getOptionalField("track_active_style", templateToType: templateToType),
        trackInactiveStyle: try dictionary.getOptionalField("track_inactive_style", templateToType: templateToType),
        transform: try dictionary.getOptionalField("transform", templateToType: templateToType),
        transitionChange: try dictionary.getOptionalField("transition_change", templateToType: templateToType),
        transitionIn: try dictionary.getOptionalField("transition_in", templateToType: templateToType),
        transitionOut: try dictionary.getOptionalField("transition_out", templateToType: templateToType),
        transitionTriggers: try dictionary.getOptionalArray("transition_triggers"),
        visibility: try dictionary.getOptionalExpressionField("visibility"),
        visibilityAction: try dictionary.getOptionalField("visibility_action", templateToType: templateToType),
        visibilityActions: try dictionary.getOptionalArray("visibility_actions", templateToType: templateToType),
        width: try dictionary.getOptionalField("width", templateToType: templateToType)
      )
    } catch let DeserializationError.invalidFieldRepresentation(field: field, representation: representation) {
      throw DeserializationError.invalidFieldRepresentation(field: "div-slider_template." + field, representation: representation)
    }
  }

  init(
    parent: String?,
    accessibility: Field<DivAccessibilityTemplate>? = nil,
    alignmentHorizontal: Field<Expression<DivAlignmentHorizontal>>? = nil,
    alignmentVertical: Field<Expression<DivAlignmentVertical>>? = nil,
    alpha: Field<Expression<Double>>? = nil,
    background: Field<[DivBackgroundTemplate]>? = nil,
    border: Field<DivBorderTemplate>? = nil,
    columnSpan: Field<Expression<Int>>? = nil,
    extensions: Field<[DivExtensionTemplate]>? = nil,
    focus: Field<DivFocusTemplate>? = nil,
    height: Field<DivSizeTemplate>? = nil,
    id: Field<String>? = nil,
    margins: Field<DivEdgeInsetsTemplate>? = nil,
    maxValue: Field<Expression<Int>>? = nil,
    minValue: Field<Expression<Int>>? = nil,
    paddings: Field<DivEdgeInsetsTemplate>? = nil,
    rowSpan: Field<Expression<Int>>? = nil,
    secondaryValueAccessibility: Field<DivAccessibilityTemplate>? = nil,
    selectedActions: Field<[DivActionTemplate]>? = nil,
    thumbSecondaryStyle: Field<DivDrawableTemplate>? = nil,
    thumbSecondaryTextStyle: Field<TextStyleTemplate>? = nil,
    thumbSecondaryValueVariable: Field<String>? = nil,
    thumbStyle: Field<DivDrawableTemplate>? = nil,
    thumbTextStyle: Field<TextStyleTemplate>? = nil,
    thumbValueVariable: Field<String>? = nil,
    tickMarkActiveStyle: Field<DivDrawableTemplate>? = nil,
    tickMarkInactiveStyle: Field<DivDrawableTemplate>? = nil,
    tooltips: Field<[DivTooltipTemplate]>? = nil,
    trackActiveStyle: Field<DivDrawableTemplate>? = nil,
    trackInactiveStyle: Field<DivDrawableTemplate>? = nil,
    transform: Field<DivTransformTemplate>? = nil,
    transitionChange: Field<DivChangeTransitionTemplate>? = nil,
    transitionIn: Field<DivAppearanceTransitionTemplate>? = nil,
    transitionOut: Field<DivAppearanceTransitionTemplate>? = nil,
    transitionTriggers: Field<[DivTransitionTrigger]>? = nil,
    visibility: Field<Expression<DivVisibility>>? = nil,
    visibilityAction: Field<DivVisibilityActionTemplate>? = nil,
    visibilityActions: Field<[DivVisibilityActionTemplate]>? = nil,
    width: Field<DivSizeTemplate>? = nil
  ) {
    self.parent = parent
    self.accessibility = accessibility
    self.alignmentHorizontal = alignmentHorizontal
    self.alignmentVertical = alignmentVertical
    self.alpha = alpha
    self.background = background
    self.border = border
    self.columnSpan = columnSpan
    self.extensions = extensions
    self.focus = focus
    self.height = height
    self.id = id
    self.margins = margins
    self.maxValue = maxValue
    self.minValue = minValue
    self.paddings = paddings
    self.rowSpan = rowSpan
    self.secondaryValueAccessibility = secondaryValueAccessibility
    self.selectedActions = selectedActions
    self.thumbSecondaryStyle = thumbSecondaryStyle
    self.thumbSecondaryTextStyle = thumbSecondaryTextStyle
    self.thumbSecondaryValueVariable = thumbSecondaryValueVariable
    self.thumbStyle = thumbStyle
    self.thumbTextStyle = thumbTextStyle
    self.thumbValueVariable = thumbValueVariable
    self.tickMarkActiveStyle = tickMarkActiveStyle
    self.tickMarkInactiveStyle = tickMarkInactiveStyle
    self.tooltips = tooltips
    self.trackActiveStyle = trackActiveStyle
    self.trackInactiveStyle = trackInactiveStyle
    self.transform = transform
    self.transitionChange = transitionChange
    self.transitionIn = transitionIn
    self.transitionOut = transitionOut
    self.transitionTriggers = transitionTriggers
    self.visibility = visibility
    self.visibilityAction = visibilityAction
    self.visibilityActions = visibilityActions
    self.width = width
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivSliderTemplate?) -> DeserializationResult<DivSlider> {
    let accessibilityValue = parent?.accessibility?.resolveOptionalValue(context: context, validator: ResolvedValue.accessibilityValidator, useOnlyLinks: true) ?? .noValue
    let alignmentHorizontalValue = parent?.alignmentHorizontal?.resolveOptionalValue(context: context, validator: ResolvedValue.alignmentHorizontalValidator) ?? .noValue
    let alignmentVerticalValue = parent?.alignmentVertical?.resolveOptionalValue(context: context, validator: ResolvedValue.alignmentVerticalValidator) ?? .noValue
    let alphaValue = parent?.alpha?.resolveOptionalValue(context: context, validator: ResolvedValue.alphaValidator) ?? .noValue
    let backgroundValue = parent?.background?.resolveOptionalValue(context: context, validator: ResolvedValue.backgroundValidator, useOnlyLinks: true) ?? .noValue
    let borderValue = parent?.border?.resolveOptionalValue(context: context, validator: ResolvedValue.borderValidator, useOnlyLinks: true) ?? .noValue
    let columnSpanValue = parent?.columnSpan?.resolveOptionalValue(context: context, validator: ResolvedValue.columnSpanValidator) ?? .noValue
    let extensionsValue = parent?.extensions?.resolveOptionalValue(context: context, validator: ResolvedValue.extensionsValidator, useOnlyLinks: true) ?? .noValue
    let focusValue = parent?.focus?.resolveOptionalValue(context: context, validator: ResolvedValue.focusValidator, useOnlyLinks: true) ?? .noValue
    let heightValue = parent?.height?.resolveOptionalValue(context: context, validator: ResolvedValue.heightValidator, useOnlyLinks: true) ?? .noValue
    let idValue = parent?.id?.resolveOptionalValue(context: context, validator: ResolvedValue.idValidator) ?? .noValue
    let marginsValue = parent?.margins?.resolveOptionalValue(context: context, validator: ResolvedValue.marginsValidator, useOnlyLinks: true) ?? .noValue
    let maxValueValue = parent?.maxValue?.resolveOptionalValue(context: context) ?? .noValue
    let minValueValue = parent?.minValue?.resolveOptionalValue(context: context) ?? .noValue
    let paddingsValue = parent?.paddings?.resolveOptionalValue(context: context, validator: ResolvedValue.paddingsValidator, useOnlyLinks: true) ?? .noValue
    let rowSpanValue = parent?.rowSpan?.resolveOptionalValue(context: context, validator: ResolvedValue.rowSpanValidator) ?? .noValue
    let secondaryValueAccessibilityValue = parent?.secondaryValueAccessibility?.resolveOptionalValue(context: context, validator: ResolvedValue.secondaryValueAccessibilityValidator, useOnlyLinks: true) ?? .noValue
    let selectedActionsValue = parent?.selectedActions?.resolveOptionalValue(context: context, validator: ResolvedValue.selectedActionsValidator, useOnlyLinks: true) ?? .noValue
    let thumbSecondaryStyleValue = parent?.thumbSecondaryStyle?.resolveOptionalValue(context: context, validator: ResolvedValue.thumbSecondaryStyleValidator, useOnlyLinks: true) ?? .noValue
    let thumbSecondaryTextStyleValue = parent?.thumbSecondaryTextStyle?.resolveOptionalValue(context: context, validator: ResolvedValue.thumbSecondaryTextStyleValidator, useOnlyLinks: true) ?? .noValue
    let thumbSecondaryValueVariableValue = parent?.thumbSecondaryValueVariable?.resolveOptionalValue(context: context, validator: ResolvedValue.thumbSecondaryValueVariableValidator) ?? .noValue
    let thumbStyleValue = parent?.thumbStyle?.resolveValue(context: context, useOnlyLinks: true) ?? .noValue
    let thumbTextStyleValue = parent?.thumbTextStyle?.resolveOptionalValue(context: context, validator: ResolvedValue.thumbTextStyleValidator, useOnlyLinks: true) ?? .noValue
    let thumbValueVariableValue = parent?.thumbValueVariable?.resolveOptionalValue(context: context, validator: ResolvedValue.thumbValueVariableValidator) ?? .noValue
    let tickMarkActiveStyleValue = parent?.tickMarkActiveStyle?.resolveOptionalValue(context: context, validator: ResolvedValue.tickMarkActiveStyleValidator, useOnlyLinks: true) ?? .noValue
    let tickMarkInactiveStyleValue = parent?.tickMarkInactiveStyle?.resolveOptionalValue(context: context, validator: ResolvedValue.tickMarkInactiveStyleValidator, useOnlyLinks: true) ?? .noValue
    let tooltipsValue = parent?.tooltips?.resolveOptionalValue(context: context, validator: ResolvedValue.tooltipsValidator, useOnlyLinks: true) ?? .noValue
    let trackActiveStyleValue = parent?.trackActiveStyle?.resolveValue(context: context, useOnlyLinks: true) ?? .noValue
    let trackInactiveStyleValue = parent?.trackInactiveStyle?.resolveValue(context: context, useOnlyLinks: true) ?? .noValue
    let transformValue = parent?.transform?.resolveOptionalValue(context: context, validator: ResolvedValue.transformValidator, useOnlyLinks: true) ?? .noValue
    let transitionChangeValue = parent?.transitionChange?.resolveOptionalValue(context: context, validator: ResolvedValue.transitionChangeValidator, useOnlyLinks: true) ?? .noValue
    let transitionInValue = parent?.transitionIn?.resolveOptionalValue(context: context, validator: ResolvedValue.transitionInValidator, useOnlyLinks: true) ?? .noValue
    let transitionOutValue = parent?.transitionOut?.resolveOptionalValue(context: context, validator: ResolvedValue.transitionOutValidator, useOnlyLinks: true) ?? .noValue
    let transitionTriggersValue = parent?.transitionTriggers?.resolveOptionalValue(context: context, validator: ResolvedValue.transitionTriggersValidator) ?? .noValue
    let visibilityValue = parent?.visibility?.resolveOptionalValue(context: context, validator: ResolvedValue.visibilityValidator) ?? .noValue
    let visibilityActionValue = parent?.visibilityAction?.resolveOptionalValue(context: context, validator: ResolvedValue.visibilityActionValidator, useOnlyLinks: true) ?? .noValue
    let visibilityActionsValue = parent?.visibilityActions?.resolveOptionalValue(context: context, validator: ResolvedValue.visibilityActionsValidator, useOnlyLinks: true) ?? .noValue
    let widthValue = parent?.width?.resolveOptionalValue(context: context, validator: ResolvedValue.widthValidator, useOnlyLinks: true) ?? .noValue
    var errors = mergeErrors(
      accessibilityValue.errorsOrWarnings?.map { .nestedObjectError(field: "accessibility", error: $0) },
      alignmentHorizontalValue.errorsOrWarnings?.map { .nestedObjectError(field: "alignment_horizontal", error: $0) },
      alignmentVerticalValue.errorsOrWarnings?.map { .nestedObjectError(field: "alignment_vertical", error: $0) },
      alphaValue.errorsOrWarnings?.map { .nestedObjectError(field: "alpha", error: $0) },
      backgroundValue.errorsOrWarnings?.map { .nestedObjectError(field: "background", error: $0) },
      borderValue.errorsOrWarnings?.map { .nestedObjectError(field: "border", error: $0) },
      columnSpanValue.errorsOrWarnings?.map { .nestedObjectError(field: "column_span", error: $0) },
      extensionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "extensions", error: $0) },
      focusValue.errorsOrWarnings?.map { .nestedObjectError(field: "focus", error: $0) },
      heightValue.errorsOrWarnings?.map { .nestedObjectError(field: "height", error: $0) },
      idValue.errorsOrWarnings?.map { .nestedObjectError(field: "id", error: $0) },
      marginsValue.errorsOrWarnings?.map { .nestedObjectError(field: "margins", error: $0) },
      maxValueValue.errorsOrWarnings?.map { .nestedObjectError(field: "max_value", error: $0) },
      minValueValue.errorsOrWarnings?.map { .nestedObjectError(field: "min_value", error: $0) },
      paddingsValue.errorsOrWarnings?.map { .nestedObjectError(field: "paddings", error: $0) },
      rowSpanValue.errorsOrWarnings?.map { .nestedObjectError(field: "row_span", error: $0) },
      secondaryValueAccessibilityValue.errorsOrWarnings?.map { .nestedObjectError(field: "secondary_value_accessibility", error: $0) },
      selectedActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "selected_actions", error: $0) },
      thumbSecondaryStyleValue.errorsOrWarnings?.map { .nestedObjectError(field: "thumb_secondary_style", error: $0) },
      thumbSecondaryTextStyleValue.errorsOrWarnings?.map { .nestedObjectError(field: "thumb_secondary_text_style", error: $0) },
      thumbSecondaryValueVariableValue.errorsOrWarnings?.map { .nestedObjectError(field: "thumb_secondary_value_variable", error: $0) },
      thumbStyleValue.errorsOrWarnings?.map { .nestedObjectError(field: "thumb_style", error: $0) },
      thumbTextStyleValue.errorsOrWarnings?.map { .nestedObjectError(field: "thumb_text_style", error: $0) },
      thumbValueVariableValue.errorsOrWarnings?.map { .nestedObjectError(field: "thumb_value_variable", error: $0) },
      tickMarkActiveStyleValue.errorsOrWarnings?.map { .nestedObjectError(field: "tick_mark_active_style", error: $0) },
      tickMarkInactiveStyleValue.errorsOrWarnings?.map { .nestedObjectError(field: "tick_mark_inactive_style", error: $0) },
      tooltipsValue.errorsOrWarnings?.map { .nestedObjectError(field: "tooltips", error: $0) },
      trackActiveStyleValue.errorsOrWarnings?.map { .nestedObjectError(field: "track_active_style", error: $0) },
      trackInactiveStyleValue.errorsOrWarnings?.map { .nestedObjectError(field: "track_inactive_style", error: $0) },
      transformValue.errorsOrWarnings?.map { .nestedObjectError(field: "transform", error: $0) },
      transitionChangeValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_change", error: $0) },
      transitionInValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_in", error: $0) },
      transitionOutValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_out", error: $0) },
      transitionTriggersValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_triggers", error: $0) },
      visibilityValue.errorsOrWarnings?.map { .nestedObjectError(field: "visibility", error: $0) },
      visibilityActionValue.errorsOrWarnings?.map { .nestedObjectError(field: "visibility_action", error: $0) },
      visibilityActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "visibility_actions", error: $0) },
      widthValue.errorsOrWarnings?.map { .nestedObjectError(field: "width", error: $0) }
    )
    if case .noValue = thumbStyleValue {
      errors.append(.requiredFieldIsMissing(field: "thumb_style"))
    }
    if case .noValue = trackActiveStyleValue {
      errors.append(.requiredFieldIsMissing(field: "track_active_style"))
    }
    if case .noValue = trackInactiveStyleValue {
      errors.append(.requiredFieldIsMissing(field: "track_inactive_style"))
    }
    guard
      let thumbStyleNonNil = thumbStyleValue.value,
      let trackActiveStyleNonNil = trackActiveStyleValue.value,
      let trackInactiveStyleNonNil = trackInactiveStyleValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivSlider(
      accessibility: accessibilityValue.value,
      alignmentHorizontal: alignmentHorizontalValue.value,
      alignmentVertical: alignmentVerticalValue.value,
      alpha: alphaValue.value,
      background: backgroundValue.value,
      border: borderValue.value,
      columnSpan: columnSpanValue.value,
      extensions: extensionsValue.value,
      focus: focusValue.value,
      height: heightValue.value,
      id: idValue.value,
      margins: marginsValue.value,
      maxValue: maxValueValue.value,
      minValue: minValueValue.value,
      paddings: paddingsValue.value,
      rowSpan: rowSpanValue.value,
      secondaryValueAccessibility: secondaryValueAccessibilityValue.value,
      selectedActions: selectedActionsValue.value,
      thumbSecondaryStyle: thumbSecondaryStyleValue.value,
      thumbSecondaryTextStyle: thumbSecondaryTextStyleValue.value,
      thumbSecondaryValueVariable: thumbSecondaryValueVariableValue.value,
      thumbStyle: thumbStyleNonNil,
      thumbTextStyle: thumbTextStyleValue.value,
      thumbValueVariable: thumbValueVariableValue.value,
      tickMarkActiveStyle: tickMarkActiveStyleValue.value,
      tickMarkInactiveStyle: tickMarkInactiveStyleValue.value,
      tooltips: tooltipsValue.value,
      trackActiveStyle: trackActiveStyleNonNil,
      trackInactiveStyle: trackInactiveStyleNonNil,
      transform: transformValue.value,
      transitionChange: transitionChangeValue.value,
      transitionIn: transitionInValue.value,
      transitionOut: transitionOutValue.value,
      transitionTriggers: transitionTriggersValue.value,
      visibility: visibilityValue.value,
      visibilityAction: visibilityActionValue.value,
      visibilityActions: visibilityActionsValue.value,
      width: widthValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivSliderTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivSlider> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var accessibilityValue: DeserializationResult<DivAccessibility> = .noValue
    var alignmentHorizontalValue: DeserializationResult<Expression<DivAlignmentHorizontal>> = parent?.alignmentHorizontal?.value() ?? .noValue
    var alignmentVerticalValue: DeserializationResult<Expression<DivAlignmentVertical>> = parent?.alignmentVertical?.value() ?? .noValue
    var alphaValue: DeserializationResult<Expression<Double>> = parent?.alpha?.value() ?? .noValue
    var backgroundValue: DeserializationResult<[DivBackground]> = .noValue
    var borderValue: DeserializationResult<DivBorder> = .noValue
    var columnSpanValue: DeserializationResult<Expression<Int>> = parent?.columnSpan?.value() ?? .noValue
    var extensionsValue: DeserializationResult<[DivExtension]> = .noValue
    var focusValue: DeserializationResult<DivFocus> = .noValue
    var heightValue: DeserializationResult<DivSize> = .noValue
    var idValue: DeserializationResult<String> = parent?.id?.value(validatedBy: ResolvedValue.idValidator) ?? .noValue
    var marginsValue: DeserializationResult<DivEdgeInsets> = .noValue
    var maxValueValue: DeserializationResult<Expression<Int>> = parent?.maxValue?.value() ?? .noValue
    var minValueValue: DeserializationResult<Expression<Int>> = parent?.minValue?.value() ?? .noValue
    var paddingsValue: DeserializationResult<DivEdgeInsets> = .noValue
    var rowSpanValue: DeserializationResult<Expression<Int>> = parent?.rowSpan?.value() ?? .noValue
    var secondaryValueAccessibilityValue: DeserializationResult<DivAccessibility> = .noValue
    var selectedActionsValue: DeserializationResult<[DivAction]> = .noValue
    var thumbSecondaryStyleValue: DeserializationResult<DivDrawable> = .noValue
    var thumbSecondaryTextStyleValue: DeserializationResult<DivSlider.TextStyle> = .noValue
    var thumbSecondaryValueVariableValue: DeserializationResult<String> = parent?.thumbSecondaryValueVariable?.value(validatedBy: ResolvedValue.thumbSecondaryValueVariableValidator) ?? .noValue
    var thumbStyleValue: DeserializationResult<DivDrawable> = .noValue
    var thumbTextStyleValue: DeserializationResult<DivSlider.TextStyle> = .noValue
    var thumbValueVariableValue: DeserializationResult<String> = parent?.thumbValueVariable?.value(validatedBy: ResolvedValue.thumbValueVariableValidator) ?? .noValue
    var tickMarkActiveStyleValue: DeserializationResult<DivDrawable> = .noValue
    var tickMarkInactiveStyleValue: DeserializationResult<DivDrawable> = .noValue
    var tooltipsValue: DeserializationResult<[DivTooltip]> = .noValue
    var trackActiveStyleValue: DeserializationResult<DivDrawable> = .noValue
    var trackInactiveStyleValue: DeserializationResult<DivDrawable> = .noValue
    var transformValue: DeserializationResult<DivTransform> = .noValue
    var transitionChangeValue: DeserializationResult<DivChangeTransition> = .noValue
    var transitionInValue: DeserializationResult<DivAppearanceTransition> = .noValue
    var transitionOutValue: DeserializationResult<DivAppearanceTransition> = .noValue
    var transitionTriggersValue: DeserializationResult<[DivTransitionTrigger]> = parent?.transitionTriggers?.value(validatedBy: ResolvedValue.transitionTriggersValidator) ?? .noValue
    var visibilityValue: DeserializationResult<Expression<DivVisibility>> = parent?.visibility?.value() ?? .noValue
    var visibilityActionValue: DeserializationResult<DivVisibilityAction> = .noValue
    var visibilityActionsValue: DeserializationResult<[DivVisibilityAction]> = .noValue
    var widthValue: DeserializationResult<DivSize> = .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "accessibility":
        accessibilityValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.accessibilityValidator, type: DivAccessibilityTemplate.self).merged(with: accessibilityValue)
      case "alignment_horizontal":
        alignmentHorizontalValue = deserialize(__dictValue, validator: ResolvedValue.alignmentHorizontalValidator).merged(with: alignmentHorizontalValue)
      case "alignment_vertical":
        alignmentVerticalValue = deserialize(__dictValue, validator: ResolvedValue.alignmentVerticalValidator).merged(with: alignmentVerticalValue)
      case "alpha":
        alphaValue = deserialize(__dictValue, validator: ResolvedValue.alphaValidator).merged(with: alphaValue)
      case "background":
        backgroundValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.backgroundValidator, type: DivBackgroundTemplate.self).merged(with: backgroundValue)
      case "border":
        borderValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.borderValidator, type: DivBorderTemplate.self).merged(with: borderValue)
      case "column_span":
        columnSpanValue = deserialize(__dictValue, validator: ResolvedValue.columnSpanValidator).merged(with: columnSpanValue)
      case "extensions":
        extensionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.extensionsValidator, type: DivExtensionTemplate.self).merged(with: extensionsValue)
      case "focus":
        focusValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.focusValidator, type: DivFocusTemplate.self).merged(with: focusValue)
      case "height":
        heightValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.heightValidator, type: DivSizeTemplate.self).merged(with: heightValue)
      case "id":
        idValue = deserialize(__dictValue, validator: ResolvedValue.idValidator).merged(with: idValue)
      case "margins":
        marginsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.marginsValidator, type: DivEdgeInsetsTemplate.self).merged(with: marginsValue)
      case "max_value":
        maxValueValue = deserialize(__dictValue).merged(with: maxValueValue)
      case "min_value":
        minValueValue = deserialize(__dictValue).merged(with: minValueValue)
      case "paddings":
        paddingsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.paddingsValidator, type: DivEdgeInsetsTemplate.self).merged(with: paddingsValue)
      case "row_span":
        rowSpanValue = deserialize(__dictValue, validator: ResolvedValue.rowSpanValidator).merged(with: rowSpanValue)
      case "secondary_value_accessibility":
        secondaryValueAccessibilityValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.secondaryValueAccessibilityValidator, type: DivAccessibilityTemplate.self).merged(with: secondaryValueAccessibilityValue)
      case "selected_actions":
        selectedActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.selectedActionsValidator, type: DivActionTemplate.self).merged(with: selectedActionsValue)
      case "thumb_secondary_style":
        thumbSecondaryStyleValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.thumbSecondaryStyleValidator, type: DivDrawableTemplate.self).merged(with: thumbSecondaryStyleValue)
      case "thumb_secondary_text_style":
        thumbSecondaryTextStyleValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.thumbSecondaryTextStyleValidator, type: DivSliderTemplate.TextStyleTemplate.self).merged(with: thumbSecondaryTextStyleValue)
      case "thumb_secondary_value_variable":
        thumbSecondaryValueVariableValue = deserialize(__dictValue, validator: ResolvedValue.thumbSecondaryValueVariableValidator).merged(with: thumbSecondaryValueVariableValue)
      case "thumb_style":
        thumbStyleValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivDrawableTemplate.self).merged(with: thumbStyleValue)
      case "thumb_text_style":
        thumbTextStyleValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.thumbTextStyleValidator, type: DivSliderTemplate.TextStyleTemplate.self).merged(with: thumbTextStyleValue)
      case "thumb_value_variable":
        thumbValueVariableValue = deserialize(__dictValue, validator: ResolvedValue.thumbValueVariableValidator).merged(with: thumbValueVariableValue)
      case "tick_mark_active_style":
        tickMarkActiveStyleValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.tickMarkActiveStyleValidator, type: DivDrawableTemplate.self).merged(with: tickMarkActiveStyleValue)
      case "tick_mark_inactive_style":
        tickMarkInactiveStyleValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.tickMarkInactiveStyleValidator, type: DivDrawableTemplate.self).merged(with: tickMarkInactiveStyleValue)
      case "tooltips":
        tooltipsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.tooltipsValidator, type: DivTooltipTemplate.self).merged(with: tooltipsValue)
      case "track_active_style":
        trackActiveStyleValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivDrawableTemplate.self).merged(with: trackActiveStyleValue)
      case "track_inactive_style":
        trackInactiveStyleValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivDrawableTemplate.self).merged(with: trackInactiveStyleValue)
      case "transform":
        transformValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.transformValidator, type: DivTransformTemplate.self).merged(with: transformValue)
      case "transition_change":
        transitionChangeValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.transitionChangeValidator, type: DivChangeTransitionTemplate.self).merged(with: transitionChangeValue)
      case "transition_in":
        transitionInValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.transitionInValidator, type: DivAppearanceTransitionTemplate.self).merged(with: transitionInValue)
      case "transition_out":
        transitionOutValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.transitionOutValidator, type: DivAppearanceTransitionTemplate.self).merged(with: transitionOutValue)
      case "transition_triggers":
        transitionTriggersValue = deserialize(__dictValue, validator: ResolvedValue.transitionTriggersValidator).merged(with: transitionTriggersValue)
      case "visibility":
        visibilityValue = deserialize(__dictValue, validator: ResolvedValue.visibilityValidator).merged(with: visibilityValue)
      case "visibility_action":
        visibilityActionValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.visibilityActionValidator, type: DivVisibilityActionTemplate.self).merged(with: visibilityActionValue)
      case "visibility_actions":
        visibilityActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.visibilityActionsValidator, type: DivVisibilityActionTemplate.self).merged(with: visibilityActionsValue)
      case "width":
        widthValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.widthValidator, type: DivSizeTemplate.self).merged(with: widthValue)
      case parent?.accessibility?.link:
        accessibilityValue = accessibilityValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.accessibilityValidator, type: DivAccessibilityTemplate.self))
      case parent?.alignmentHorizontal?.link:
        alignmentHorizontalValue = alignmentHorizontalValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.alignmentHorizontalValidator))
      case parent?.alignmentVertical?.link:
        alignmentVerticalValue = alignmentVerticalValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.alignmentVerticalValidator))
      case parent?.alpha?.link:
        alphaValue = alphaValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.alphaValidator))
      case parent?.background?.link:
        backgroundValue = backgroundValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.backgroundValidator, type: DivBackgroundTemplate.self))
      case parent?.border?.link:
        borderValue = borderValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.borderValidator, type: DivBorderTemplate.self))
      case parent?.columnSpan?.link:
        columnSpanValue = columnSpanValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.columnSpanValidator))
      case parent?.extensions?.link:
        extensionsValue = extensionsValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.extensionsValidator, type: DivExtensionTemplate.self))
      case parent?.focus?.link:
        focusValue = focusValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.focusValidator, type: DivFocusTemplate.self))
      case parent?.height?.link:
        heightValue = heightValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.heightValidator, type: DivSizeTemplate.self))
      case parent?.id?.link:
        idValue = idValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.idValidator))
      case parent?.margins?.link:
        marginsValue = marginsValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.marginsValidator, type: DivEdgeInsetsTemplate.self))
      case parent?.maxValue?.link:
        maxValueValue = maxValueValue.merged(with: deserialize(__dictValue))
      case parent?.minValue?.link:
        minValueValue = minValueValue.merged(with: deserialize(__dictValue))
      case parent?.paddings?.link:
        paddingsValue = paddingsValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.paddingsValidator, type: DivEdgeInsetsTemplate.self))
      case parent?.rowSpan?.link:
        rowSpanValue = rowSpanValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.rowSpanValidator))
      case parent?.secondaryValueAccessibility?.link:
        secondaryValueAccessibilityValue = secondaryValueAccessibilityValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.secondaryValueAccessibilityValidator, type: DivAccessibilityTemplate.self))
      case parent?.selectedActions?.link:
        selectedActionsValue = selectedActionsValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.selectedActionsValidator, type: DivActionTemplate.self))
      case parent?.thumbSecondaryStyle?.link:
        thumbSecondaryStyleValue = thumbSecondaryStyleValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.thumbSecondaryStyleValidator, type: DivDrawableTemplate.self))
      case parent?.thumbSecondaryTextStyle?.link:
        thumbSecondaryTextStyleValue = thumbSecondaryTextStyleValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.thumbSecondaryTextStyleValidator, type: DivSliderTemplate.TextStyleTemplate.self))
      case parent?.thumbSecondaryValueVariable?.link:
        thumbSecondaryValueVariableValue = thumbSecondaryValueVariableValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.thumbSecondaryValueVariableValidator))
      case parent?.thumbStyle?.link:
        thumbStyleValue = thumbStyleValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivDrawableTemplate.self))
      case parent?.thumbTextStyle?.link:
        thumbTextStyleValue = thumbTextStyleValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.thumbTextStyleValidator, type: DivSliderTemplate.TextStyleTemplate.self))
      case parent?.thumbValueVariable?.link:
        thumbValueVariableValue = thumbValueVariableValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.thumbValueVariableValidator))
      case parent?.tickMarkActiveStyle?.link:
        tickMarkActiveStyleValue = tickMarkActiveStyleValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.tickMarkActiveStyleValidator, type: DivDrawableTemplate.self))
      case parent?.tickMarkInactiveStyle?.link:
        tickMarkInactiveStyleValue = tickMarkInactiveStyleValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.tickMarkInactiveStyleValidator, type: DivDrawableTemplate.self))
      case parent?.tooltips?.link:
        tooltipsValue = tooltipsValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.tooltipsValidator, type: DivTooltipTemplate.self))
      case parent?.trackActiveStyle?.link:
        trackActiveStyleValue = trackActiveStyleValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivDrawableTemplate.self))
      case parent?.trackInactiveStyle?.link:
        trackInactiveStyleValue = trackInactiveStyleValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivDrawableTemplate.self))
      case parent?.transform?.link:
        transformValue = transformValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.transformValidator, type: DivTransformTemplate.self))
      case parent?.transitionChange?.link:
        transitionChangeValue = transitionChangeValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.transitionChangeValidator, type: DivChangeTransitionTemplate.self))
      case parent?.transitionIn?.link:
        transitionInValue = transitionInValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.transitionInValidator, type: DivAppearanceTransitionTemplate.self))
      case parent?.transitionOut?.link:
        transitionOutValue = transitionOutValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.transitionOutValidator, type: DivAppearanceTransitionTemplate.self))
      case parent?.transitionTriggers?.link:
        transitionTriggersValue = transitionTriggersValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.transitionTriggersValidator))
      case parent?.visibility?.link:
        visibilityValue = visibilityValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.visibilityValidator))
      case parent?.visibilityAction?.link:
        visibilityActionValue = visibilityActionValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.visibilityActionValidator, type: DivVisibilityActionTemplate.self))
      case parent?.visibilityActions?.link:
        visibilityActionsValue = visibilityActionsValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.visibilityActionsValidator, type: DivVisibilityActionTemplate.self))
      case parent?.width?.link:
        widthValue = widthValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.widthValidator, type: DivSizeTemplate.self))
      default: break
      }
    }
    if let parent = parent {
      accessibilityValue = accessibilityValue.merged(with: parent.accessibility?.resolveOptionalValue(context: context, validator: ResolvedValue.accessibilityValidator, useOnlyLinks: true))
      backgroundValue = backgroundValue.merged(with: parent.background?.resolveOptionalValue(context: context, validator: ResolvedValue.backgroundValidator, useOnlyLinks: true))
      borderValue = borderValue.merged(with: parent.border?.resolveOptionalValue(context: context, validator: ResolvedValue.borderValidator, useOnlyLinks: true))
      extensionsValue = extensionsValue.merged(with: parent.extensions?.resolveOptionalValue(context: context, validator: ResolvedValue.extensionsValidator, useOnlyLinks: true))
      focusValue = focusValue.merged(with: parent.focus?.resolveOptionalValue(context: context, validator: ResolvedValue.focusValidator, useOnlyLinks: true))
      heightValue = heightValue.merged(with: parent.height?.resolveOptionalValue(context: context, validator: ResolvedValue.heightValidator, useOnlyLinks: true))
      marginsValue = marginsValue.merged(with: parent.margins?.resolveOptionalValue(context: context, validator: ResolvedValue.marginsValidator, useOnlyLinks: true))
      paddingsValue = paddingsValue.merged(with: parent.paddings?.resolveOptionalValue(context: context, validator: ResolvedValue.paddingsValidator, useOnlyLinks: true))
      secondaryValueAccessibilityValue = secondaryValueAccessibilityValue.merged(with: parent.secondaryValueAccessibility?.resolveOptionalValue(context: context, validator: ResolvedValue.secondaryValueAccessibilityValidator, useOnlyLinks: true))
      selectedActionsValue = selectedActionsValue.merged(with: parent.selectedActions?.resolveOptionalValue(context: context, validator: ResolvedValue.selectedActionsValidator, useOnlyLinks: true))
      thumbSecondaryStyleValue = thumbSecondaryStyleValue.merged(with: parent.thumbSecondaryStyle?.resolveOptionalValue(context: context, validator: ResolvedValue.thumbSecondaryStyleValidator, useOnlyLinks: true))
      thumbSecondaryTextStyleValue = thumbSecondaryTextStyleValue.merged(with: parent.thumbSecondaryTextStyle?.resolveOptionalValue(context: context, validator: ResolvedValue.thumbSecondaryTextStyleValidator, useOnlyLinks: true))
      thumbStyleValue = thumbStyleValue.merged(with: parent.thumbStyle?.resolveValue(context: context, useOnlyLinks: true))
      thumbTextStyleValue = thumbTextStyleValue.merged(with: parent.thumbTextStyle?.resolveOptionalValue(context: context, validator: ResolvedValue.thumbTextStyleValidator, useOnlyLinks: true))
      tickMarkActiveStyleValue = tickMarkActiveStyleValue.merged(with: parent.tickMarkActiveStyle?.resolveOptionalValue(context: context, validator: ResolvedValue.tickMarkActiveStyleValidator, useOnlyLinks: true))
      tickMarkInactiveStyleValue = tickMarkInactiveStyleValue.merged(with: parent.tickMarkInactiveStyle?.resolveOptionalValue(context: context, validator: ResolvedValue.tickMarkInactiveStyleValidator, useOnlyLinks: true))
      tooltipsValue = tooltipsValue.merged(with: parent.tooltips?.resolveOptionalValue(context: context, validator: ResolvedValue.tooltipsValidator, useOnlyLinks: true))
      trackActiveStyleValue = trackActiveStyleValue.merged(with: parent.trackActiveStyle?.resolveValue(context: context, useOnlyLinks: true))
      trackInactiveStyleValue = trackInactiveStyleValue.merged(with: parent.trackInactiveStyle?.resolveValue(context: context, useOnlyLinks: true))
      transformValue = transformValue.merged(with: parent.transform?.resolveOptionalValue(context: context, validator: ResolvedValue.transformValidator, useOnlyLinks: true))
      transitionChangeValue = transitionChangeValue.merged(with: parent.transitionChange?.resolveOptionalValue(context: context, validator: ResolvedValue.transitionChangeValidator, useOnlyLinks: true))
      transitionInValue = transitionInValue.merged(with: parent.transitionIn?.resolveOptionalValue(context: context, validator: ResolvedValue.transitionInValidator, useOnlyLinks: true))
      transitionOutValue = transitionOutValue.merged(with: parent.transitionOut?.resolveOptionalValue(context: context, validator: ResolvedValue.transitionOutValidator, useOnlyLinks: true))
      visibilityActionValue = visibilityActionValue.merged(with: parent.visibilityAction?.resolveOptionalValue(context: context, validator: ResolvedValue.visibilityActionValidator, useOnlyLinks: true))
      visibilityActionsValue = visibilityActionsValue.merged(with: parent.visibilityActions?.resolveOptionalValue(context: context, validator: ResolvedValue.visibilityActionsValidator, useOnlyLinks: true))
      widthValue = widthValue.merged(with: parent.width?.resolveOptionalValue(context: context, validator: ResolvedValue.widthValidator, useOnlyLinks: true))
    }
    var errors = mergeErrors(
      accessibilityValue.errorsOrWarnings?.map { .nestedObjectError(field: "accessibility", error: $0) },
      alignmentHorizontalValue.errorsOrWarnings?.map { .nestedObjectError(field: "alignment_horizontal", error: $0) },
      alignmentVerticalValue.errorsOrWarnings?.map { .nestedObjectError(field: "alignment_vertical", error: $0) },
      alphaValue.errorsOrWarnings?.map { .nestedObjectError(field: "alpha", error: $0) },
      backgroundValue.errorsOrWarnings?.map { .nestedObjectError(field: "background", error: $0) },
      borderValue.errorsOrWarnings?.map { .nestedObjectError(field: "border", error: $0) },
      columnSpanValue.errorsOrWarnings?.map { .nestedObjectError(field: "column_span", error: $0) },
      extensionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "extensions", error: $0) },
      focusValue.errorsOrWarnings?.map { .nestedObjectError(field: "focus", error: $0) },
      heightValue.errorsOrWarnings?.map { .nestedObjectError(field: "height", error: $0) },
      idValue.errorsOrWarnings?.map { .nestedObjectError(field: "id", error: $0) },
      marginsValue.errorsOrWarnings?.map { .nestedObjectError(field: "margins", error: $0) },
      maxValueValue.errorsOrWarnings?.map { .nestedObjectError(field: "max_value", error: $0) },
      minValueValue.errorsOrWarnings?.map { .nestedObjectError(field: "min_value", error: $0) },
      paddingsValue.errorsOrWarnings?.map { .nestedObjectError(field: "paddings", error: $0) },
      rowSpanValue.errorsOrWarnings?.map { .nestedObjectError(field: "row_span", error: $0) },
      secondaryValueAccessibilityValue.errorsOrWarnings?.map { .nestedObjectError(field: "secondary_value_accessibility", error: $0) },
      selectedActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "selected_actions", error: $0) },
      thumbSecondaryStyleValue.errorsOrWarnings?.map { .nestedObjectError(field: "thumb_secondary_style", error: $0) },
      thumbSecondaryTextStyleValue.errorsOrWarnings?.map { .nestedObjectError(field: "thumb_secondary_text_style", error: $0) },
      thumbSecondaryValueVariableValue.errorsOrWarnings?.map { .nestedObjectError(field: "thumb_secondary_value_variable", error: $0) },
      thumbStyleValue.errorsOrWarnings?.map { .nestedObjectError(field: "thumb_style", error: $0) },
      thumbTextStyleValue.errorsOrWarnings?.map { .nestedObjectError(field: "thumb_text_style", error: $0) },
      thumbValueVariableValue.errorsOrWarnings?.map { .nestedObjectError(field: "thumb_value_variable", error: $0) },
      tickMarkActiveStyleValue.errorsOrWarnings?.map { .nestedObjectError(field: "tick_mark_active_style", error: $0) },
      tickMarkInactiveStyleValue.errorsOrWarnings?.map { .nestedObjectError(field: "tick_mark_inactive_style", error: $0) },
      tooltipsValue.errorsOrWarnings?.map { .nestedObjectError(field: "tooltips", error: $0) },
      trackActiveStyleValue.errorsOrWarnings?.map { .nestedObjectError(field: "track_active_style", error: $0) },
      trackInactiveStyleValue.errorsOrWarnings?.map { .nestedObjectError(field: "track_inactive_style", error: $0) },
      transformValue.errorsOrWarnings?.map { .nestedObjectError(field: "transform", error: $0) },
      transitionChangeValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_change", error: $0) },
      transitionInValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_in", error: $0) },
      transitionOutValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_out", error: $0) },
      transitionTriggersValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_triggers", error: $0) },
      visibilityValue.errorsOrWarnings?.map { .nestedObjectError(field: "visibility", error: $0) },
      visibilityActionValue.errorsOrWarnings?.map { .nestedObjectError(field: "visibility_action", error: $0) },
      visibilityActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "visibility_actions", error: $0) },
      widthValue.errorsOrWarnings?.map { .nestedObjectError(field: "width", error: $0) }
    )
    if case .noValue = thumbStyleValue {
      errors.append(.requiredFieldIsMissing(field: "thumb_style"))
    }
    if case .noValue = trackActiveStyleValue {
      errors.append(.requiredFieldIsMissing(field: "track_active_style"))
    }
    if case .noValue = trackInactiveStyleValue {
      errors.append(.requiredFieldIsMissing(field: "track_inactive_style"))
    }
    guard
      let thumbStyleNonNil = thumbStyleValue.value,
      let trackActiveStyleNonNil = trackActiveStyleValue.value,
      let trackInactiveStyleNonNil = trackInactiveStyleValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivSlider(
      accessibility: accessibilityValue.value,
      alignmentHorizontal: alignmentHorizontalValue.value,
      alignmentVertical: alignmentVerticalValue.value,
      alpha: alphaValue.value,
      background: backgroundValue.value,
      border: borderValue.value,
      columnSpan: columnSpanValue.value,
      extensions: extensionsValue.value,
      focus: focusValue.value,
      height: heightValue.value,
      id: idValue.value,
      margins: marginsValue.value,
      maxValue: maxValueValue.value,
      minValue: minValueValue.value,
      paddings: paddingsValue.value,
      rowSpan: rowSpanValue.value,
      secondaryValueAccessibility: secondaryValueAccessibilityValue.value,
      selectedActions: selectedActionsValue.value,
      thumbSecondaryStyle: thumbSecondaryStyleValue.value,
      thumbSecondaryTextStyle: thumbSecondaryTextStyleValue.value,
      thumbSecondaryValueVariable: thumbSecondaryValueVariableValue.value,
      thumbStyle: thumbStyleNonNil,
      thumbTextStyle: thumbTextStyleValue.value,
      thumbValueVariable: thumbValueVariableValue.value,
      tickMarkActiveStyle: tickMarkActiveStyleValue.value,
      tickMarkInactiveStyle: tickMarkInactiveStyleValue.value,
      tooltips: tooltipsValue.value,
      trackActiveStyle: trackActiveStyleNonNil,
      trackInactiveStyle: trackInactiveStyleNonNil,
      transform: transformValue.value,
      transitionChange: transitionChangeValue.value,
      transitionIn: transitionInValue.value,
      transitionOut: transitionOutValue.value,
      transitionTriggers: transitionTriggersValue.value,
      visibility: visibilityValue.value,
      visibilityAction: visibilityActionValue.value,
      visibilityActions: visibilityActionsValue.value,
      width: widthValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivSliderTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? DivSliderTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return DivSliderTemplate(
      parent: nil,
      accessibility: accessibility ?? mergedParent.accessibility,
      alignmentHorizontal: alignmentHorizontal ?? mergedParent.alignmentHorizontal,
      alignmentVertical: alignmentVertical ?? mergedParent.alignmentVertical,
      alpha: alpha ?? mergedParent.alpha,
      background: background ?? mergedParent.background,
      border: border ?? mergedParent.border,
      columnSpan: columnSpan ?? mergedParent.columnSpan,
      extensions: extensions ?? mergedParent.extensions,
      focus: focus ?? mergedParent.focus,
      height: height ?? mergedParent.height,
      id: id ?? mergedParent.id,
      margins: margins ?? mergedParent.margins,
      maxValue: maxValue ?? mergedParent.maxValue,
      minValue: minValue ?? mergedParent.minValue,
      paddings: paddings ?? mergedParent.paddings,
      rowSpan: rowSpan ?? mergedParent.rowSpan,
      secondaryValueAccessibility: secondaryValueAccessibility ?? mergedParent.secondaryValueAccessibility,
      selectedActions: selectedActions ?? mergedParent.selectedActions,
      thumbSecondaryStyle: thumbSecondaryStyle ?? mergedParent.thumbSecondaryStyle,
      thumbSecondaryTextStyle: thumbSecondaryTextStyle ?? mergedParent.thumbSecondaryTextStyle,
      thumbSecondaryValueVariable: thumbSecondaryValueVariable ?? mergedParent.thumbSecondaryValueVariable,
      thumbStyle: thumbStyle ?? mergedParent.thumbStyle,
      thumbTextStyle: thumbTextStyle ?? mergedParent.thumbTextStyle,
      thumbValueVariable: thumbValueVariable ?? mergedParent.thumbValueVariable,
      tickMarkActiveStyle: tickMarkActiveStyle ?? mergedParent.tickMarkActiveStyle,
      tickMarkInactiveStyle: tickMarkInactiveStyle ?? mergedParent.tickMarkInactiveStyle,
      tooltips: tooltips ?? mergedParent.tooltips,
      trackActiveStyle: trackActiveStyle ?? mergedParent.trackActiveStyle,
      trackInactiveStyle: trackInactiveStyle ?? mergedParent.trackInactiveStyle,
      transform: transform ?? mergedParent.transform,
      transitionChange: transitionChange ?? mergedParent.transitionChange,
      transitionIn: transitionIn ?? mergedParent.transitionIn,
      transitionOut: transitionOut ?? mergedParent.transitionOut,
      transitionTriggers: transitionTriggers ?? mergedParent.transitionTriggers,
      visibility: visibility ?? mergedParent.visibility,
      visibilityAction: visibilityAction ?? mergedParent.visibilityAction,
      visibilityActions: visibilityActions ?? mergedParent.visibilityActions,
      width: width ?? mergedParent.width
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivSliderTemplate {
    let merged = try mergedWithParent(templates: templates)

    return DivSliderTemplate(
      parent: nil,
      accessibility: merged.accessibility?.tryResolveParent(templates: templates),
      alignmentHorizontal: merged.alignmentHorizontal,
      alignmentVertical: merged.alignmentVertical,
      alpha: merged.alpha,
      background: merged.background?.tryResolveParent(templates: templates),
      border: merged.border?.tryResolveParent(templates: templates),
      columnSpan: merged.columnSpan,
      extensions: merged.extensions?.tryResolveParent(templates: templates),
      focus: merged.focus?.tryResolveParent(templates: templates),
      height: merged.height?.tryResolveParent(templates: templates),
      id: merged.id,
      margins: merged.margins?.tryResolveParent(templates: templates),
      maxValue: merged.maxValue,
      minValue: merged.minValue,
      paddings: merged.paddings?.tryResolveParent(templates: templates),
      rowSpan: merged.rowSpan,
      secondaryValueAccessibility: merged.secondaryValueAccessibility?.tryResolveParent(templates: templates),
      selectedActions: merged.selectedActions?.tryResolveParent(templates: templates),
      thumbSecondaryStyle: merged.thumbSecondaryStyle?.tryResolveParent(templates: templates),
      thumbSecondaryTextStyle: merged.thumbSecondaryTextStyle?.tryResolveParent(templates: templates),
      thumbSecondaryValueVariable: merged.thumbSecondaryValueVariable,
      thumbStyle: try merged.thumbStyle?.resolveParent(templates: templates),
      thumbTextStyle: merged.thumbTextStyle?.tryResolveParent(templates: templates),
      thumbValueVariable: merged.thumbValueVariable,
      tickMarkActiveStyle: merged.tickMarkActiveStyle?.tryResolveParent(templates: templates),
      tickMarkInactiveStyle: merged.tickMarkInactiveStyle?.tryResolveParent(templates: templates),
      tooltips: merged.tooltips?.tryResolveParent(templates: templates),
      trackActiveStyle: try merged.trackActiveStyle?.resolveParent(templates: templates),
      trackInactiveStyle: try merged.trackInactiveStyle?.resolveParent(templates: templates),
      transform: merged.transform?.tryResolveParent(templates: templates),
      transitionChange: merged.transitionChange?.tryResolveParent(templates: templates),
      transitionIn: merged.transitionIn?.tryResolveParent(templates: templates),
      transitionOut: merged.transitionOut?.tryResolveParent(templates: templates),
      transitionTriggers: merged.transitionTriggers,
      visibility: merged.visibility,
      visibilityAction: merged.visibilityAction?.tryResolveParent(templates: templates),
      visibilityActions: merged.visibilityActions?.tryResolveParent(templates: templates),
      width: merged.width?.tryResolveParent(templates: templates)
    )
  }
}
