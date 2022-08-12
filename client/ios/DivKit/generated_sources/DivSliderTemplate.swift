// Generated code. Do not modify.

import CoreFoundation
import Foundation

import CommonCore
import Serialization
import TemplatesSupport

public final class DivSliderTemplate: TemplateValue, TemplateDeserializable {
  public final class TextStyleTemplate: TemplateValue, TemplateDeserializable {
    public let fontSize: Field<Expression<Int>>? // constraint: number >= 0
    public let fontSizeUnit: Field<Expression<DivSizeUnit>>? // default value: sp
    public let fontWeight: Field<Expression<DivFontWeight>>? // default value: regular
    public let offset: Field<DivPointTemplate>?
    public let textColor: Field<Expression<Color>>? // default value: #FF000000

    public convenience init(dictionary: [String: Any], templateToType: TemplateToType) throws {
      do {
        self.init(
          fontSize: try dictionary.getOptionalField("font_size"),
          fontSizeUnit: try dictionary.getOptionalField("font_size_unit"),
          fontWeight: try dictionary.getOptionalField("font_weight"),
          offset: try dictionary.getOptionalField("offset", templateToType: templateToType),
          textColor: try dictionary.getOptionalField(
            "text_color",
            transform: Color.color(withHexString:)
          )
        )
      } catch let DeserializationError.invalidFieldRepresentation(
        field: field,
        representation: representation
      ) {
        throw DeserializationError.invalidFieldRepresentation(
          field: "text_style_template." + field,
          representation: representation
        )
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

    private static func resolveOnlyLinks(
      context: Context,
      parent: TextStyleTemplate?
    ) -> DeserializationResult<DivSlider.TextStyle> {
      let fontSizeValue = parent?.fontSize?.resolveValue(
        context: context,
        validator: ResolvedValue.fontSizeValidator
      ) ?? .noValue
      let fontSizeUnitValue = parent?.fontSizeUnit?.resolveOptionalValue(
        context: context,
        validator: ResolvedValue.fontSizeUnitValidator
      ) ?? .noValue
      let fontWeightValue = parent?.fontWeight?.resolveOptionalValue(
        context: context,
        validator: ResolvedValue.fontWeightValidator
      ) ?? .noValue
      let offsetValue = parent?.offset?.resolveOptionalValue(
        context: context,
        validator: ResolvedValue.offsetValidator,
        useOnlyLinks: true
      ) ?? .noValue
      let textColorValue = parent?.textColor?.resolveOptionalValue(
        context: context,
        transform: Color.color(withHexString:),
        validator: ResolvedValue.textColorValidator
      ) ?? .noValue
      var errors = mergeErrors(
        fontSizeValue.errorsOrWarnings?
          .map { .right($0.asError(deserializing: "font_size", level: .error)) },
        fontSizeUnitValue.errorsOrWarnings?
          .map { .right($0.asError(deserializing: "font_size_unit", level: .warning)) },
        fontWeightValue.errorsOrWarnings?
          .map { .right($0.asError(deserializing: "font_weight", level: .warning)) },
        offsetValue.errorsOrWarnings?
          .map { .right($0.asError(deserializing: "offset", level: .warning)) },
        textColorValue.errorsOrWarnings?
          .map { .right($0.asError(deserializing: "text_color", level: .warning)) }
      )
      if case .noValue = fontSizeValue {
        errors
          .append(.right(FieldError(
            fieldName: "font_size",
            level: .error,
            error: .requiredFieldIsMissing
          )))
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
      return errors
        .isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
    }

    public static func resolveValue(
      context: Context,
      parent: TextStyleTemplate?,
      useOnlyLinks: Bool
    ) -> DeserializationResult<DivSlider.TextStyle> {
      if useOnlyLinks {
        return resolveOnlyLinks(context: context, parent: parent)
      }
      var fontSizeValue: DeserializationResult<Expression<Int>> = parent?.fontSize?
        .value() ?? .noValue
      var fontSizeUnitValue: DeserializationResult<Expression<DivSizeUnit>> = parent?.fontSizeUnit?
        .value() ?? .noValue
      var fontWeightValue: DeserializationResult<Expression<DivFontWeight>> = parent?.fontWeight?
        .value() ?? .noValue
      var offsetValue: DeserializationResult<DivPoint> = .noValue
      var textColorValue: DeserializationResult<Expression<Color>> = parent?.textColor?
        .value() ?? .noValue
      context.templateData.forEach { key, __dictValue in
        switch key {
        case "font_size":
          fontSizeValue = deserialize(__dictValue, validator: ResolvedValue.fontSizeValidator)
            .merged(with: fontSizeValue)
        case "font_size_unit":
          fontSizeUnitValue = deserialize(
            __dictValue,
            validator: ResolvedValue.fontSizeUnitValidator
          ).merged(with: fontSizeUnitValue)
        case "font_weight":
          fontWeightValue = deserialize(__dictValue, validator: ResolvedValue.fontWeightValidator)
            .merged(with: fontWeightValue)
        case "offset":
          offsetValue = deserialize(
            __dictValue,
            templates: context.templates,
            templateToType: context.templateToType,
            validator: ResolvedValue.offsetValidator,
            type: DivPointTemplate.self
          ).merged(with: offsetValue)
        case "text_color":
          textColorValue = deserialize(
            __dictValue,
            transform: Color.color(withHexString:),
            validator: ResolvedValue.textColorValidator
          ).merged(with: textColorValue)
        case parent?.fontSize?.link:
          fontSizeValue = fontSizeValue
            .merged(with: deserialize(__dictValue, validator: ResolvedValue.fontSizeValidator))
        case parent?.fontSizeUnit?.link:
          fontSizeUnitValue = fontSizeUnitValue
            .merged(with: deserialize(__dictValue, validator: ResolvedValue.fontSizeUnitValidator))
        case parent?.fontWeight?.link:
          fontWeightValue = fontWeightValue
            .merged(with: deserialize(__dictValue, validator: ResolvedValue.fontWeightValidator))
        case parent?.offset?.link:
          offsetValue = offsetValue.merged(with: deserialize(
            __dictValue,
            templates: context.templates,
            templateToType: context.templateToType,
            validator: ResolvedValue.offsetValidator,
            type: DivPointTemplate.self
          ))
        case parent?.textColor?.link:
          textColorValue = textColorValue.merged(with: deserialize(
            __dictValue,
            transform: Color.color(withHexString:),
            validator: ResolvedValue.textColorValidator
          ))
        default: break
        }
      }
      if let parent = parent {
        offsetValue = offsetValue.merged(with: parent.offset?.resolveOptionalValue(
          context: context,
          validator: ResolvedValue.offsetValidator,
          useOnlyLinks: true
        ))
      }
      var errors = mergeErrors(
        fontSizeValue.errorsOrWarnings?
          .map { Either.right($0.asError(deserializing: "font_size", level: .error)) },
        fontSizeUnitValue.errorsOrWarnings?
          .map { Either.right($0.asError(deserializing: "font_size_unit", level: .warning)) },
        fontWeightValue.errorsOrWarnings?
          .map { Either.right($0.asError(deserializing: "font_weight", level: .warning)) },
        offsetValue.errorsOrWarnings?
          .map { Either.right($0.asError(deserializing: "offset", level: .warning)) },
        textColorValue.errorsOrWarnings?
          .map { Either.right($0.asError(deserializing: "text_color", level: .warning)) }
      )
      if case .noValue = fontSizeValue {
        errors
          .append(.right(FieldError(
            fieldName: "font_size",
            level: .error,
            error: .requiredFieldIsMissing
          )))
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
      return errors
        .isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
    }

    private func mergedWithParent(templates _: Templates) throws -> TextStyleTemplate {
      self
    }

    public func resolveParent(templates: Templates) throws -> TextStyleTemplate {
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

  public convenience init(dictionary: [String: Any], templateToType: TemplateToType) throws {
    do {
      self.init(
        parent: try dictionary.getOptionalField("type", validator: Self.parentValidator),
        accessibility: try dictionary.getOptionalField(
          "accessibility",
          templateToType: templateToType
        ),
        alignmentHorizontal: try dictionary.getOptionalField("alignment_horizontal"),
        alignmentVertical: try dictionary.getOptionalField("alignment_vertical"),
        alpha: try dictionary.getOptionalField("alpha"),
        background: try dictionary.getOptionalArray("background", templateToType: templateToType),
        border: try dictionary.getOptionalField("border", templateToType: templateToType),
        columnSpan: try dictionary.getOptionalField("column_span"),
        extensions: try dictionary.getOptionalArray("extensions", templateToType: templateToType),
        focus: try dictionary.getOptionalField("focus", templateToType: templateToType),
        height: try dictionary.getOptionalField("height", templateToType: templateToType),
        id: try dictionary.getOptionalField("id"),
        margins: try dictionary.getOptionalField("margins", templateToType: templateToType),
        maxValue: try dictionary.getOptionalField("max_value"),
        minValue: try dictionary.getOptionalField("min_value"),
        paddings: try dictionary.getOptionalField("paddings", templateToType: templateToType),
        rowSpan: try dictionary.getOptionalField("row_span"),
        secondaryValueAccessibility: try dictionary.getOptionalField(
          "secondary_value_accessibility",
          templateToType: templateToType
        ),
        selectedActions: try dictionary.getOptionalArray(
          "selected_actions",
          templateToType: templateToType
        ),
        thumbSecondaryStyle: try dictionary.getOptionalField(
          "thumb_secondary_style",
          templateToType: templateToType
        ),
        thumbSecondaryTextStyle: try dictionary.getOptionalField(
          "thumb_secondary_text_style",
          templateToType: templateToType
        ),
        thumbSecondaryValueVariable: try dictionary
          .getOptionalField("thumb_secondary_value_variable"),
        thumbStyle: try dictionary.getOptionalField("thumb_style", templateToType: templateToType),
        thumbTextStyle: try dictionary.getOptionalField(
          "thumb_text_style",
          templateToType: templateToType
        ),
        thumbValueVariable: try dictionary.getOptionalField("thumb_value_variable"),
        tickMarkActiveStyle: try dictionary.getOptionalField(
          "tick_mark_active_style",
          templateToType: templateToType
        ),
        tickMarkInactiveStyle: try dictionary.getOptionalField(
          "tick_mark_inactive_style",
          templateToType: templateToType
        ),
        tooltips: try dictionary.getOptionalArray("tooltips", templateToType: templateToType),
        trackActiveStyle: try dictionary.getOptionalField(
          "track_active_style",
          templateToType: templateToType
        ),
        trackInactiveStyle: try dictionary.getOptionalField(
          "track_inactive_style",
          templateToType: templateToType
        ),
        transform: try dictionary.getOptionalField("transform", templateToType: templateToType),
        transitionChange: try dictionary.getOptionalField(
          "transition_change",
          templateToType: templateToType
        ),
        transitionIn: try dictionary.getOptionalField(
          "transition_in",
          templateToType: templateToType
        ),
        transitionOut: try dictionary.getOptionalField(
          "transition_out",
          templateToType: templateToType
        ),
        transitionTriggers: try dictionary.getOptionalArray("transition_triggers"),
        visibility: try dictionary.getOptionalField("visibility"),
        visibilityAction: try dictionary.getOptionalField(
          "visibility_action",
          templateToType: templateToType
        ),
        visibilityActions: try dictionary.getOptionalArray(
          "visibility_actions",
          templateToType: templateToType
        ),
        width: try dictionary.getOptionalField("width", templateToType: templateToType)
      )
    } catch let DeserializationError.invalidFieldRepresentation(
      field: field,
      representation: representation
    ) {
      throw DeserializationError.invalidFieldRepresentation(
        field: "div-slider_template." + field,
        representation: representation
      )
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

  private static func resolveOnlyLinks(
    context: Context,
    parent: DivSliderTemplate?
  ) -> DeserializationResult<DivSlider> {
    let accessibilityValue = parent?.accessibility?.resolveOptionalValue(
      context: context,
      validator: ResolvedValue.accessibilityValidator,
      useOnlyLinks: true
    ) ?? .noValue
    let alignmentHorizontalValue = parent?.alignmentHorizontal?.resolveOptionalValue(
      context: context,
      validator: ResolvedValue.alignmentHorizontalValidator
    ) ?? .noValue
    let alignmentVerticalValue = parent?.alignmentVertical?.resolveOptionalValue(
      context: context,
      validator: ResolvedValue.alignmentVerticalValidator
    ) ?? .noValue
    let alphaValue = parent?.alpha?.resolveOptionalValue(
      context: context,
      validator: ResolvedValue.alphaValidator
    ) ?? .noValue
    let backgroundValue = parent?.background?.resolveOptionalValue(
      context: context,
      validator: ResolvedValue.backgroundValidator,
      useOnlyLinks: true
    ) ?? .noValue
    let borderValue = parent?.border?.resolveOptionalValue(
      context: context,
      validator: ResolvedValue.borderValidator,
      useOnlyLinks: true
    ) ?? .noValue
    let columnSpanValue = parent?.columnSpan?.resolveOptionalValue(
      context: context,
      validator: ResolvedValue.columnSpanValidator
    ) ?? .noValue
    let extensionsValue = parent?.extensions?.resolveOptionalValue(
      context: context,
      validator: ResolvedValue.extensionsValidator,
      useOnlyLinks: true
    ) ?? .noValue
    let focusValue = parent?.focus?.resolveOptionalValue(
      context: context,
      validator: ResolvedValue.focusValidator,
      useOnlyLinks: true
    ) ?? .noValue
    let heightValue = parent?.height?.resolveOptionalValue(
      context: context,
      validator: ResolvedValue.heightValidator,
      useOnlyLinks: true
    ) ?? .noValue
    let idValue = parent?.id?.resolveOptionalValue(
      context: context,
      validator: ResolvedValue.idValidator
    ) ?? .noValue
    let marginsValue = parent?.margins?.resolveOptionalValue(
      context: context,
      validator: ResolvedValue.marginsValidator,
      useOnlyLinks: true
    ) ?? .noValue
    let maxValueValue = parent?.maxValue?.resolveOptionalValue(context: context) ?? .noValue
    let minValueValue = parent?.minValue?.resolveOptionalValue(context: context) ?? .noValue
    let paddingsValue = parent?.paddings?.resolveOptionalValue(
      context: context,
      validator: ResolvedValue.paddingsValidator,
      useOnlyLinks: true
    ) ?? .noValue
    let rowSpanValue = parent?.rowSpan?.resolveOptionalValue(
      context: context,
      validator: ResolvedValue.rowSpanValidator
    ) ?? .noValue
    let secondaryValueAccessibilityValue = parent?.secondaryValueAccessibility?
      .resolveOptionalValue(
        context: context,
        validator: ResolvedValue.secondaryValueAccessibilityValidator,
        useOnlyLinks: true
      ) ?? .noValue
    let selectedActionsValue = parent?.selectedActions?.resolveOptionalValue(
      context: context,
      validator: ResolvedValue.selectedActionsValidator,
      useOnlyLinks: true
    ) ?? .noValue
    let thumbSecondaryStyleValue = parent?.thumbSecondaryStyle?.resolveOptionalValue(
      context: context,
      validator: ResolvedValue.thumbSecondaryStyleValidator,
      useOnlyLinks: true
    ) ?? .noValue
    let thumbSecondaryTextStyleValue = parent?.thumbSecondaryTextStyle?.resolveOptionalValue(
      context: context,
      validator: ResolvedValue.thumbSecondaryTextStyleValidator,
      useOnlyLinks: true
    ) ?? .noValue
    let thumbSecondaryValueVariableValue = parent?.thumbSecondaryValueVariable?
      .resolveOptionalValue(
        context: context,
        validator: ResolvedValue.thumbSecondaryValueVariableValidator
      ) ?? .noValue
    let thumbStyleValue = parent?.thumbStyle?
      .resolveValue(context: context, useOnlyLinks: true) ?? .noValue
    let thumbTextStyleValue = parent?.thumbTextStyle?.resolveOptionalValue(
      context: context,
      validator: ResolvedValue.thumbTextStyleValidator,
      useOnlyLinks: true
    ) ?? .noValue
    let thumbValueVariableValue = parent?.thumbValueVariable?.resolveOptionalValue(
      context: context,
      validator: ResolvedValue.thumbValueVariableValidator
    ) ?? .noValue
    let tickMarkActiveStyleValue = parent?.tickMarkActiveStyle?.resolveOptionalValue(
      context: context,
      validator: ResolvedValue.tickMarkActiveStyleValidator,
      useOnlyLinks: true
    ) ?? .noValue
    let tickMarkInactiveStyleValue = parent?.tickMarkInactiveStyle?.resolveOptionalValue(
      context: context,
      validator: ResolvedValue.tickMarkInactiveStyleValidator,
      useOnlyLinks: true
    ) ?? .noValue
    let tooltipsValue = parent?.tooltips?.resolveOptionalValue(
      context: context,
      validator: ResolvedValue.tooltipsValidator,
      useOnlyLinks: true
    ) ?? .noValue
    let trackActiveStyleValue = parent?.trackActiveStyle?.resolveValue(
      context: context,
      useOnlyLinks: true
    ) ?? .noValue
    let trackInactiveStyleValue = parent?.trackInactiveStyle?.resolveValue(
      context: context,
      useOnlyLinks: true
    ) ?? .noValue
    let transformValue = parent?.transform?.resolveOptionalValue(
      context: context,
      validator: ResolvedValue.transformValidator,
      useOnlyLinks: true
    ) ?? .noValue
    let transitionChangeValue = parent?.transitionChange?.resolveOptionalValue(
      context: context,
      validator: ResolvedValue.transitionChangeValidator,
      useOnlyLinks: true
    ) ?? .noValue
    let transitionInValue = parent?.transitionIn?.resolveOptionalValue(
      context: context,
      validator: ResolvedValue.transitionInValidator,
      useOnlyLinks: true
    ) ?? .noValue
    let transitionOutValue = parent?.transitionOut?.resolveOptionalValue(
      context: context,
      validator: ResolvedValue.transitionOutValidator,
      useOnlyLinks: true
    ) ?? .noValue
    let transitionTriggersValue = parent?.transitionTriggers?.resolveOptionalValue(
      context: context,
      validator: ResolvedValue.transitionTriggersValidator
    ) ?? .noValue
    let visibilityValue = parent?.visibility?.resolveOptionalValue(
      context: context,
      validator: ResolvedValue.visibilityValidator
    ) ?? .noValue
    let visibilityActionValue = parent?.visibilityAction?.resolveOptionalValue(
      context: context,
      validator: ResolvedValue.visibilityActionValidator,
      useOnlyLinks: true
    ) ?? .noValue
    let visibilityActionsValue = parent?.visibilityActions?.resolveOptionalValue(
      context: context,
      validator: ResolvedValue.visibilityActionsValidator,
      useOnlyLinks: true
    ) ?? .noValue
    let widthValue = parent?.width?.resolveOptionalValue(
      context: context,
      validator: ResolvedValue.widthValidator,
      useOnlyLinks: true
    ) ?? .noValue
    var errors = mergeErrors(
      accessibilityValue.errorsOrWarnings?
        .map { .right($0.asError(deserializing: "accessibility", level: .warning)) },
      alignmentHorizontalValue.errorsOrWarnings?
        .map { .right($0.asError(deserializing: "alignment_horizontal", level: .warning)) },
      alignmentVerticalValue.errorsOrWarnings?
        .map { .right($0.asError(deserializing: "alignment_vertical", level: .warning)) },
      alphaValue.errorsOrWarnings?
        .map { .right($0.asError(deserializing: "alpha", level: .warning)) },
      backgroundValue.errorsOrWarnings?
        .map { .right($0.asError(deserializing: "background", level: .warning)) },
      borderValue.errorsOrWarnings?
        .map { .right($0.asError(deserializing: "border", level: .warning)) },
      columnSpanValue.errorsOrWarnings?
        .map { .right($0.asError(deserializing: "column_span", level: .warning)) },
      extensionsValue.errorsOrWarnings?
        .map { .right($0.asError(deserializing: "extensions", level: .warning)) },
      focusValue.errorsOrWarnings?
        .map { .right($0.asError(deserializing: "focus", level: .warning)) },
      heightValue.errorsOrWarnings?
        .map { .right($0.asError(deserializing: "height", level: .warning)) },
      idValue.errorsOrWarnings?.map { .right($0.asError(deserializing: "id", level: .warning)) },
      marginsValue.errorsOrWarnings?
        .map { .right($0.asError(deserializing: "margins", level: .warning)) },
      maxValueValue.errorsOrWarnings?
        .map { .right($0.asError(deserializing: "max_value", level: .warning)) },
      minValueValue.errorsOrWarnings?
        .map { .right($0.asError(deserializing: "min_value", level: .warning)) },
      paddingsValue.errorsOrWarnings?
        .map { .right($0.asError(deserializing: "paddings", level: .warning)) },
      rowSpanValue.errorsOrWarnings?
        .map { .right($0.asError(deserializing: "row_span", level: .warning)) },
      secondaryValueAccessibilityValue.errorsOrWarnings?
        .map { .right($0.asError(deserializing: "secondary_value_accessibility", level: .warning))
        },
      selectedActionsValue.errorsOrWarnings?
        .map { .right($0.asError(deserializing: "selected_actions", level: .warning)) },
      thumbSecondaryStyleValue.errorsOrWarnings?
        .map { .right($0.asError(deserializing: "thumb_secondary_style", level: .warning)) },
      thumbSecondaryTextStyleValue.errorsOrWarnings?
        .map { .right($0.asError(deserializing: "thumb_secondary_text_style", level: .warning)) },
      thumbSecondaryValueVariableValue.errorsOrWarnings?
        .map { .right($0.asError(deserializing: "thumb_secondary_value_variable", level: .warning))
        },
      thumbStyleValue.errorsOrWarnings?
        .map { .right($0.asError(deserializing: "thumb_style", level: .error)) },
      thumbTextStyleValue.errorsOrWarnings?
        .map { .right($0.asError(deserializing: "thumb_text_style", level: .warning)) },
      thumbValueVariableValue.errorsOrWarnings?
        .map { .right($0.asError(deserializing: "thumb_value_variable", level: .warning)) },
      tickMarkActiveStyleValue.errorsOrWarnings?
        .map { .right($0.asError(deserializing: "tick_mark_active_style", level: .warning)) },
      tickMarkInactiveStyleValue.errorsOrWarnings?
        .map { .right($0.asError(deserializing: "tick_mark_inactive_style", level: .warning)) },
      tooltipsValue.errorsOrWarnings?
        .map { .right($0.asError(deserializing: "tooltips", level: .warning)) },
      trackActiveStyleValue.errorsOrWarnings?
        .map { .right($0.asError(deserializing: "track_active_style", level: .error)) },
      trackInactiveStyleValue.errorsOrWarnings?
        .map { .right($0.asError(deserializing: "track_inactive_style", level: .error)) },
      transformValue.errorsOrWarnings?
        .map { .right($0.asError(deserializing: "transform", level: .warning)) },
      transitionChangeValue.errorsOrWarnings?
        .map { .right($0.asError(deserializing: "transition_change", level: .warning)) },
      transitionInValue.errorsOrWarnings?
        .map { .right($0.asError(deserializing: "transition_in", level: .warning)) },
      transitionOutValue.errorsOrWarnings?
        .map { .right($0.asError(deserializing: "transition_out", level: .warning)) },
      transitionTriggersValue.errorsOrWarnings?
        .map { .right($0.asError(deserializing: "transition_triggers", level: .warning)) },
      visibilityValue.errorsOrWarnings?
        .map { .right($0.asError(deserializing: "visibility", level: .warning)) },
      visibilityActionValue.errorsOrWarnings?
        .map { .right($0.asError(deserializing: "visibility_action", level: .warning)) },
      visibilityActionsValue.errorsOrWarnings?
        .map { .right($0.asError(deserializing: "visibility_actions", level: .warning)) },
      widthValue.errorsOrWarnings?
        .map { .right($0.asError(deserializing: "width", level: .warning)) }
    )
    if case .noValue = thumbStyleValue {
      errors
        .append(.right(FieldError(
          fieldName: "thumb_style",
          level: .error,
          error: .requiredFieldIsMissing
        )))
    }
    if case .noValue = trackActiveStyleValue {
      errors
        .append(.right(FieldError(
          fieldName: "track_active_style",
          level: .error,
          error: .requiredFieldIsMissing
        )))
    }
    if case .noValue = trackInactiveStyleValue {
      errors
        .append(.right(FieldError(
          fieldName: "track_inactive_style",
          level: .error,
          error: .requiredFieldIsMissing
        )))
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
    return errors
      .isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(
    context: Context,
    parent: DivSliderTemplate?,
    useOnlyLinks: Bool
  ) -> DeserializationResult<DivSlider> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var accessibilityValue: DeserializationResult<DivAccessibility> = .noValue
    var alignmentHorizontalValue: DeserializationResult<Expression<DivAlignmentHorizontal>> =
      parent?.alignmentHorizontal?.value() ?? .noValue
    var alignmentVerticalValue: DeserializationResult<Expression<DivAlignmentVertical>> = parent?
      .alignmentVertical?.value() ?? .noValue
    var alphaValue: DeserializationResult<Expression<Double>> = parent?.alpha?.value() ?? .noValue
    var backgroundValue: DeserializationResult<[DivBackground]> = .noValue
    var borderValue: DeserializationResult<DivBorder> = .noValue
    var columnSpanValue: DeserializationResult<Expression<Int>> = parent?.columnSpan?
      .value() ?? .noValue
    var extensionsValue: DeserializationResult<[DivExtension]> = .noValue
    var focusValue: DeserializationResult<DivFocus> = .noValue
    var heightValue: DeserializationResult<DivSize> = .noValue
    var idValue: DeserializationResult<String> = parent?.id?
      .value(validatedBy: ResolvedValue.idValidator) ?? .noValue
    var marginsValue: DeserializationResult<DivEdgeInsets> = .noValue
    var maxValueValue: DeserializationResult<Expression<Int>> = parent?.maxValue?
      .value() ?? .noValue
    var minValueValue: DeserializationResult<Expression<Int>> = parent?.minValue?
      .value() ?? .noValue
    var paddingsValue: DeserializationResult<DivEdgeInsets> = .noValue
    var rowSpanValue: DeserializationResult<Expression<Int>> = parent?.rowSpan?.value() ?? .noValue
    var secondaryValueAccessibilityValue: DeserializationResult<DivAccessibility> = .noValue
    var selectedActionsValue: DeserializationResult<[DivAction]> = .noValue
    var thumbSecondaryStyleValue: DeserializationResult<DivDrawable> = .noValue
    var thumbSecondaryTextStyleValue: DeserializationResult<DivSlider.TextStyle> = .noValue
    var thumbSecondaryValueVariableValue: DeserializationResult<String> = parent?
      .thumbSecondaryValueVariable?
      .value(validatedBy: ResolvedValue.thumbSecondaryValueVariableValidator) ?? .noValue
    var thumbStyleValue: DeserializationResult<DivDrawable> = .noValue
    var thumbTextStyleValue: DeserializationResult<DivSlider.TextStyle> = .noValue
    var thumbValueVariableValue: DeserializationResult<String> = parent?.thumbValueVariable?
      .value(validatedBy: ResolvedValue.thumbValueVariableValidator) ?? .noValue
    var tickMarkActiveStyleValue: DeserializationResult<DivDrawable> = .noValue
    var tickMarkInactiveStyleValue: DeserializationResult<DivDrawable> = .noValue
    var tooltipsValue: DeserializationResult<[DivTooltip]> = .noValue
    var trackActiveStyleValue: DeserializationResult<DivDrawable> = .noValue
    var trackInactiveStyleValue: DeserializationResult<DivDrawable> = .noValue
    var transformValue: DeserializationResult<DivTransform> = .noValue
    var transitionChangeValue: DeserializationResult<DivChangeTransition> = .noValue
    var transitionInValue: DeserializationResult<DivAppearanceTransition> = .noValue
    var transitionOutValue: DeserializationResult<DivAppearanceTransition> = .noValue
    var transitionTriggersValue: DeserializationResult<[DivTransitionTrigger]> = parent?
      .transitionTriggers?.value(validatedBy: ResolvedValue.transitionTriggersValidator) ?? .noValue
    var visibilityValue: DeserializationResult<Expression<DivVisibility>> = parent?.visibility?
      .value() ?? .noValue
    var visibilityActionValue: DeserializationResult<DivVisibilityAction> = .noValue
    var visibilityActionsValue: DeserializationResult<[DivVisibilityAction]> = .noValue
    var widthValue: DeserializationResult<DivSize> = .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "accessibility":
        accessibilityValue = deserialize(
          __dictValue,
          templates: context.templates,
          templateToType: context.templateToType,
          validator: ResolvedValue.accessibilityValidator,
          type: DivAccessibilityTemplate.self
        ).merged(with: accessibilityValue)
      case "alignment_horizontal":
        alignmentHorizontalValue = deserialize(
          __dictValue,
          validator: ResolvedValue.alignmentHorizontalValidator
        ).merged(with: alignmentHorizontalValue)
      case "alignment_vertical":
        alignmentVerticalValue = deserialize(
          __dictValue,
          validator: ResolvedValue.alignmentVerticalValidator
        ).merged(with: alignmentVerticalValue)
      case "alpha":
        alphaValue = deserialize(__dictValue, validator: ResolvedValue.alphaValidator)
          .merged(with: alphaValue)
      case "background":
        backgroundValue = deserialize(
          __dictValue,
          templates: context.templates,
          templateToType: context.templateToType,
          validator: ResolvedValue.backgroundValidator,
          type: DivBackgroundTemplate.self
        ).merged(with: backgroundValue)
      case "border":
        borderValue = deserialize(
          __dictValue,
          templates: context.templates,
          templateToType: context.templateToType,
          validator: ResolvedValue.borderValidator,
          type: DivBorderTemplate.self
        ).merged(with: borderValue)
      case "column_span":
        columnSpanValue = deserialize(__dictValue, validator: ResolvedValue.columnSpanValidator)
          .merged(with: columnSpanValue)
      case "extensions":
        extensionsValue = deserialize(
          __dictValue,
          templates: context.templates,
          templateToType: context.templateToType,
          validator: ResolvedValue.extensionsValidator,
          type: DivExtensionTemplate.self
        ).merged(with: extensionsValue)
      case "focus":
        focusValue = deserialize(
          __dictValue,
          templates: context.templates,
          templateToType: context.templateToType,
          validator: ResolvedValue.focusValidator,
          type: DivFocusTemplate.self
        ).merged(with: focusValue)
      case "height":
        heightValue = deserialize(
          __dictValue,
          templates: context.templates,
          templateToType: context.templateToType,
          validator: ResolvedValue.heightValidator,
          type: DivSizeTemplate.self
        ).merged(with: heightValue)
      case "id":
        idValue = deserialize(__dictValue, validator: ResolvedValue.idValidator)
          .merged(with: idValue)
      case "margins":
        marginsValue = deserialize(
          __dictValue,
          templates: context.templates,
          templateToType: context.templateToType,
          validator: ResolvedValue.marginsValidator,
          type: DivEdgeInsetsTemplate.self
        ).merged(with: marginsValue)
      case "max_value":
        maxValueValue = deserialize(__dictValue).merged(with: maxValueValue)
      case "min_value":
        minValueValue = deserialize(__dictValue).merged(with: minValueValue)
      case "paddings":
        paddingsValue = deserialize(
          __dictValue,
          templates: context.templates,
          templateToType: context.templateToType,
          validator: ResolvedValue.paddingsValidator,
          type: DivEdgeInsetsTemplate.self
        ).merged(with: paddingsValue)
      case "row_span":
        rowSpanValue = deserialize(__dictValue, validator: ResolvedValue.rowSpanValidator)
          .merged(with: rowSpanValue)
      case "secondary_value_accessibility":
        secondaryValueAccessibilityValue = deserialize(
          __dictValue,
          templates: context.templates,
          templateToType: context.templateToType,
          validator: ResolvedValue.secondaryValueAccessibilityValidator,
          type: DivAccessibilityTemplate.self
        ).merged(with: secondaryValueAccessibilityValue)
      case "selected_actions":
        selectedActionsValue = deserialize(
          __dictValue,
          templates: context.templates,
          templateToType: context.templateToType,
          validator: ResolvedValue.selectedActionsValidator,
          type: DivActionTemplate.self
        ).merged(with: selectedActionsValue)
      case "thumb_secondary_style":
        thumbSecondaryStyleValue = deserialize(
          __dictValue,
          templates: context.templates,
          templateToType: context.templateToType,
          validator: ResolvedValue.thumbSecondaryStyleValidator,
          type: DivDrawableTemplate.self
        ).merged(with: thumbSecondaryStyleValue)
      case "thumb_secondary_text_style":
        thumbSecondaryTextStyleValue = deserialize(
          __dictValue,
          templates: context.templates,
          templateToType: context.templateToType,
          validator: ResolvedValue.thumbSecondaryTextStyleValidator,
          type: DivSliderTemplate.TextStyleTemplate.self
        ).merged(with: thumbSecondaryTextStyleValue)
      case "thumb_secondary_value_variable":
        thumbSecondaryValueVariableValue = deserialize(
          __dictValue,
          validator: ResolvedValue.thumbSecondaryValueVariableValidator
        ).merged(with: thumbSecondaryValueVariableValue)
      case "thumb_style":
        thumbStyleValue = deserialize(
          __dictValue,
          templates: context.templates,
          templateToType: context.templateToType,
          type: DivDrawableTemplate.self
        ).merged(with: thumbStyleValue)
      case "thumb_text_style":
        thumbTextStyleValue = deserialize(
          __dictValue,
          templates: context.templates,
          templateToType: context.templateToType,
          validator: ResolvedValue.thumbTextStyleValidator,
          type: DivSliderTemplate.TextStyleTemplate.self
        ).merged(with: thumbTextStyleValue)
      case "thumb_value_variable":
        thumbValueVariableValue = deserialize(
          __dictValue,
          validator: ResolvedValue.thumbValueVariableValidator
        ).merged(with: thumbValueVariableValue)
      case "tick_mark_active_style":
        tickMarkActiveStyleValue = deserialize(
          __dictValue,
          templates: context.templates,
          templateToType: context.templateToType,
          validator: ResolvedValue.tickMarkActiveStyleValidator,
          type: DivDrawableTemplate.self
        ).merged(with: tickMarkActiveStyleValue)
      case "tick_mark_inactive_style":
        tickMarkInactiveStyleValue = deserialize(
          __dictValue,
          templates: context.templates,
          templateToType: context.templateToType,
          validator: ResolvedValue.tickMarkInactiveStyleValidator,
          type: DivDrawableTemplate.self
        ).merged(with: tickMarkInactiveStyleValue)
      case "tooltips":
        tooltipsValue = deserialize(
          __dictValue,
          templates: context.templates,
          templateToType: context.templateToType,
          validator: ResolvedValue.tooltipsValidator,
          type: DivTooltipTemplate.self
        ).merged(with: tooltipsValue)
      case "track_active_style":
        trackActiveStyleValue = deserialize(
          __dictValue,
          templates: context.templates,
          templateToType: context.templateToType,
          type: DivDrawableTemplate.self
        ).merged(with: trackActiveStyleValue)
      case "track_inactive_style":
        trackInactiveStyleValue = deserialize(
          __dictValue,
          templates: context.templates,
          templateToType: context.templateToType,
          type: DivDrawableTemplate.self
        ).merged(with: trackInactiveStyleValue)
      case "transform":
        transformValue = deserialize(
          __dictValue,
          templates: context.templates,
          templateToType: context.templateToType,
          validator: ResolvedValue.transformValidator,
          type: DivTransformTemplate.self
        ).merged(with: transformValue)
      case "transition_change":
        transitionChangeValue = deserialize(
          __dictValue,
          templates: context.templates,
          templateToType: context.templateToType,
          validator: ResolvedValue.transitionChangeValidator,
          type: DivChangeTransitionTemplate.self
        ).merged(with: transitionChangeValue)
      case "transition_in":
        transitionInValue = deserialize(
          __dictValue,
          templates: context.templates,
          templateToType: context.templateToType,
          validator: ResolvedValue.transitionInValidator,
          type: DivAppearanceTransitionTemplate.self
        ).merged(with: transitionInValue)
      case "transition_out":
        transitionOutValue = deserialize(
          __dictValue,
          templates: context.templates,
          templateToType: context.templateToType,
          validator: ResolvedValue.transitionOutValidator,
          type: DivAppearanceTransitionTemplate.self
        ).merged(with: transitionOutValue)
      case "transition_triggers":
        transitionTriggersValue = deserialize(
          __dictValue,
          validator: ResolvedValue.transitionTriggersValidator
        ).merged(with: transitionTriggersValue)
      case "visibility":
        visibilityValue = deserialize(__dictValue, validator: ResolvedValue.visibilityValidator)
          .merged(with: visibilityValue)
      case "visibility_action":
        visibilityActionValue = deserialize(
          __dictValue,
          templates: context.templates,
          templateToType: context.templateToType,
          validator: ResolvedValue.visibilityActionValidator,
          type: DivVisibilityActionTemplate.self
        ).merged(with: visibilityActionValue)
      case "visibility_actions":
        visibilityActionsValue = deserialize(
          __dictValue,
          templates: context.templates,
          templateToType: context.templateToType,
          validator: ResolvedValue.visibilityActionsValidator,
          type: DivVisibilityActionTemplate.self
        ).merged(with: visibilityActionsValue)
      case "width":
        widthValue = deserialize(
          __dictValue,
          templates: context.templates,
          templateToType: context.templateToType,
          validator: ResolvedValue.widthValidator,
          type: DivSizeTemplate.self
        ).merged(with: widthValue)
      case parent?.accessibility?.link:
        accessibilityValue = accessibilityValue.merged(with: deserialize(
          __dictValue,
          templates: context.templates,
          templateToType: context.templateToType,
          validator: ResolvedValue.accessibilityValidator,
          type: DivAccessibilityTemplate.self
        ))
      case parent?.alignmentHorizontal?.link:
        alignmentHorizontalValue = alignmentHorizontalValue
          .merged(with: deserialize(
            __dictValue,
            validator: ResolvedValue.alignmentHorizontalValidator
          ))
      case parent?.alignmentVertical?.link:
        alignmentVerticalValue = alignmentVerticalValue
          .merged(with: deserialize(
            __dictValue,
            validator: ResolvedValue.alignmentVerticalValidator
          ))
      case parent?.alpha?.link:
        alphaValue = alphaValue
          .merged(with: deserialize(__dictValue, validator: ResolvedValue.alphaValidator))
      case parent?.background?.link:
        backgroundValue = backgroundValue.merged(with: deserialize(
          __dictValue,
          templates: context.templates,
          templateToType: context.templateToType,
          validator: ResolvedValue.backgroundValidator,
          type: DivBackgroundTemplate.self
        ))
      case parent?.border?.link:
        borderValue = borderValue.merged(with: deserialize(
          __dictValue,
          templates: context.templates,
          templateToType: context.templateToType,
          validator: ResolvedValue.borderValidator,
          type: DivBorderTemplate.self
        ))
      case parent?.columnSpan?.link:
        columnSpanValue = columnSpanValue
          .merged(with: deserialize(__dictValue, validator: ResolvedValue.columnSpanValidator))
      case parent?.extensions?.link:
        extensionsValue = extensionsValue.merged(with: deserialize(
          __dictValue,
          templates: context.templates,
          templateToType: context.templateToType,
          validator: ResolvedValue.extensionsValidator,
          type: DivExtensionTemplate.self
        ))
      case parent?.focus?.link:
        focusValue = focusValue.merged(with: deserialize(
          __dictValue,
          templates: context.templates,
          templateToType: context.templateToType,
          validator: ResolvedValue.focusValidator,
          type: DivFocusTemplate.self
        ))
      case parent?.height?.link:
        heightValue = heightValue.merged(with: deserialize(
          __dictValue,
          templates: context.templates,
          templateToType: context.templateToType,
          validator: ResolvedValue.heightValidator,
          type: DivSizeTemplate.self
        ))
      case parent?.id?.link:
        idValue = idValue
          .merged(with: deserialize(__dictValue, validator: ResolvedValue.idValidator))
      case parent?.margins?.link:
        marginsValue = marginsValue.merged(with: deserialize(
          __dictValue,
          templates: context.templates,
          templateToType: context.templateToType,
          validator: ResolvedValue.marginsValidator,
          type: DivEdgeInsetsTemplate.self
        ))
      case parent?.maxValue?.link:
        maxValueValue = maxValueValue.merged(with: deserialize(__dictValue))
      case parent?.minValue?.link:
        minValueValue = minValueValue.merged(with: deserialize(__dictValue))
      case parent?.paddings?.link:
        paddingsValue = paddingsValue.merged(with: deserialize(
          __dictValue,
          templates: context.templates,
          templateToType: context.templateToType,
          validator: ResolvedValue.paddingsValidator,
          type: DivEdgeInsetsTemplate.self
        ))
      case parent?.rowSpan?.link:
        rowSpanValue = rowSpanValue
          .merged(with: deserialize(__dictValue, validator: ResolvedValue.rowSpanValidator))
      case parent?.secondaryValueAccessibility?.link:
        secondaryValueAccessibilityValue = secondaryValueAccessibilityValue
          .merged(with: deserialize(
            __dictValue,
            templates: context.templates,
            templateToType: context.templateToType,
            validator: ResolvedValue.secondaryValueAccessibilityValidator,
            type: DivAccessibilityTemplate.self
          ))
      case parent?.selectedActions?.link:
        selectedActionsValue = selectedActionsValue.merged(with: deserialize(
          __dictValue,
          templates: context.templates,
          templateToType: context.templateToType,
          validator: ResolvedValue.selectedActionsValidator,
          type: DivActionTemplate.self
        ))
      case parent?.thumbSecondaryStyle?.link:
        thumbSecondaryStyleValue = thumbSecondaryStyleValue.merged(with: deserialize(
          __dictValue,
          templates: context.templates,
          templateToType: context.templateToType,
          validator: ResolvedValue.thumbSecondaryStyleValidator,
          type: DivDrawableTemplate.self
        ))
      case parent?.thumbSecondaryTextStyle?.link:
        thumbSecondaryTextStyleValue = thumbSecondaryTextStyleValue.merged(with: deserialize(
          __dictValue,
          templates: context.templates,
          templateToType: context.templateToType,
          validator: ResolvedValue.thumbSecondaryTextStyleValidator,
          type: DivSliderTemplate.TextStyleTemplate.self
        ))
      case parent?.thumbSecondaryValueVariable?.link:
        thumbSecondaryValueVariableValue = thumbSecondaryValueVariableValue
          .merged(with: deserialize(
            __dictValue,
            validator: ResolvedValue.thumbSecondaryValueVariableValidator
          ))
      case parent?.thumbStyle?.link:
        thumbStyleValue = thumbStyleValue.merged(with: deserialize(
          __dictValue,
          templates: context.templates,
          templateToType: context.templateToType,
          type: DivDrawableTemplate.self
        ))
      case parent?.thumbTextStyle?.link:
        thumbTextStyleValue = thumbTextStyleValue.merged(with: deserialize(
          __dictValue,
          templates: context.templates,
          templateToType: context.templateToType,
          validator: ResolvedValue.thumbTextStyleValidator,
          type: DivSliderTemplate.TextStyleTemplate.self
        ))
      case parent?.thumbValueVariable?.link:
        thumbValueVariableValue = thumbValueVariableValue
          .merged(with: deserialize(
            __dictValue,
            validator: ResolvedValue.thumbValueVariableValidator
          ))
      case parent?.tickMarkActiveStyle?.link:
        tickMarkActiveStyleValue = tickMarkActiveStyleValue.merged(with: deserialize(
          __dictValue,
          templates: context.templates,
          templateToType: context.templateToType,
          validator: ResolvedValue.tickMarkActiveStyleValidator,
          type: DivDrawableTemplate.self
        ))
      case parent?.tickMarkInactiveStyle?.link:
        tickMarkInactiveStyleValue = tickMarkInactiveStyleValue.merged(with: deserialize(
          __dictValue,
          templates: context.templates,
          templateToType: context.templateToType,
          validator: ResolvedValue.tickMarkInactiveStyleValidator,
          type: DivDrawableTemplate.self
        ))
      case parent?.tooltips?.link:
        tooltipsValue = tooltipsValue.merged(with: deserialize(
          __dictValue,
          templates: context.templates,
          templateToType: context.templateToType,
          validator: ResolvedValue.tooltipsValidator,
          type: DivTooltipTemplate.self
        ))
      case parent?.trackActiveStyle?.link:
        trackActiveStyleValue = trackActiveStyleValue.merged(with: deserialize(
          __dictValue,
          templates: context.templates,
          templateToType: context.templateToType,
          type: DivDrawableTemplate.self
        ))
      case parent?.trackInactiveStyle?.link:
        trackInactiveStyleValue = trackInactiveStyleValue.merged(with: deserialize(
          __dictValue,
          templates: context.templates,
          templateToType: context.templateToType,
          type: DivDrawableTemplate.self
        ))
      case parent?.transform?.link:
        transformValue = transformValue.merged(with: deserialize(
          __dictValue,
          templates: context.templates,
          templateToType: context.templateToType,
          validator: ResolvedValue.transformValidator,
          type: DivTransformTemplate.self
        ))
      case parent?.transitionChange?.link:
        transitionChangeValue = transitionChangeValue.merged(with: deserialize(
          __dictValue,
          templates: context.templates,
          templateToType: context.templateToType,
          validator: ResolvedValue.transitionChangeValidator,
          type: DivChangeTransitionTemplate.self
        ))
      case parent?.transitionIn?.link:
        transitionInValue = transitionInValue.merged(with: deserialize(
          __dictValue,
          templates: context.templates,
          templateToType: context.templateToType,
          validator: ResolvedValue.transitionInValidator,
          type: DivAppearanceTransitionTemplate.self
        ))
      case parent?.transitionOut?.link:
        transitionOutValue = transitionOutValue.merged(with: deserialize(
          __dictValue,
          templates: context.templates,
          templateToType: context.templateToType,
          validator: ResolvedValue.transitionOutValidator,
          type: DivAppearanceTransitionTemplate.self
        ))
      case parent?.transitionTriggers?.link:
        transitionTriggersValue = transitionTriggersValue
          .merged(with: deserialize(
            __dictValue,
            validator: ResolvedValue.transitionTriggersValidator
          ))
      case parent?.visibility?.link:
        visibilityValue = visibilityValue
          .merged(with: deserialize(__dictValue, validator: ResolvedValue.visibilityValidator))
      case parent?.visibilityAction?.link:
        visibilityActionValue = visibilityActionValue.merged(with: deserialize(
          __dictValue,
          templates: context.templates,
          templateToType: context.templateToType,
          validator: ResolvedValue.visibilityActionValidator,
          type: DivVisibilityActionTemplate.self
        ))
      case parent?.visibilityActions?.link:
        visibilityActionsValue = visibilityActionsValue.merged(with: deserialize(
          __dictValue,
          templates: context.templates,
          templateToType: context.templateToType,
          validator: ResolvedValue.visibilityActionsValidator,
          type: DivVisibilityActionTemplate.self
        ))
      case parent?.width?.link:
        widthValue = widthValue.merged(with: deserialize(
          __dictValue,
          templates: context.templates,
          templateToType: context.templateToType,
          validator: ResolvedValue.widthValidator,
          type: DivSizeTemplate.self
        ))
      default: break
      }
    }
    if let parent = parent {
      accessibilityValue = accessibilityValue
        .merged(with: parent.accessibility?.resolveOptionalValue(
          context: context,
          validator: ResolvedValue.accessibilityValidator,
          useOnlyLinks: true
        ))
      backgroundValue = backgroundValue.merged(with: parent.background?.resolveOptionalValue(
        context: context,
        validator: ResolvedValue.backgroundValidator,
        useOnlyLinks: true
      ))
      borderValue = borderValue.merged(with: parent.border?.resolveOptionalValue(
        context: context,
        validator: ResolvedValue.borderValidator,
        useOnlyLinks: true
      ))
      extensionsValue = extensionsValue.merged(with: parent.extensions?.resolveOptionalValue(
        context: context,
        validator: ResolvedValue.extensionsValidator,
        useOnlyLinks: true
      ))
      focusValue = focusValue.merged(with: parent.focus?.resolveOptionalValue(
        context: context,
        validator: ResolvedValue.focusValidator,
        useOnlyLinks: true
      ))
      heightValue = heightValue.merged(with: parent.height?.resolveOptionalValue(
        context: context,
        validator: ResolvedValue.heightValidator,
        useOnlyLinks: true
      ))
      marginsValue = marginsValue.merged(with: parent.margins?.resolveOptionalValue(
        context: context,
        validator: ResolvedValue.marginsValidator,
        useOnlyLinks: true
      ))
      paddingsValue = paddingsValue.merged(with: parent.paddings?.resolveOptionalValue(
        context: context,
        validator: ResolvedValue.paddingsValidator,
        useOnlyLinks: true
      ))
      secondaryValueAccessibilityValue = secondaryValueAccessibilityValue
        .merged(with: parent.secondaryValueAccessibility?.resolveOptionalValue(
          context: context,
          validator: ResolvedValue.secondaryValueAccessibilityValidator,
          useOnlyLinks: true
        ))
      selectedActionsValue = selectedActionsValue
        .merged(with: parent.selectedActions?.resolveOptionalValue(
          context: context,
          validator: ResolvedValue.selectedActionsValidator,
          useOnlyLinks: true
        ))
      thumbSecondaryStyleValue = thumbSecondaryStyleValue
        .merged(with: parent.thumbSecondaryStyle?.resolveOptionalValue(
          context: context,
          validator: ResolvedValue.thumbSecondaryStyleValidator,
          useOnlyLinks: true
        ))
      thumbSecondaryTextStyleValue = thumbSecondaryTextStyleValue
        .merged(with: parent.thumbSecondaryTextStyle?.resolveOptionalValue(
          context: context,
          validator: ResolvedValue.thumbSecondaryTextStyleValidator,
          useOnlyLinks: true
        ))
      thumbStyleValue = thumbStyleValue
        .merged(with: parent.thumbStyle?.resolveValue(context: context, useOnlyLinks: true))
      thumbTextStyleValue = thumbTextStyleValue
        .merged(with: parent.thumbTextStyle?.resolveOptionalValue(
          context: context,
          validator: ResolvedValue.thumbTextStyleValidator,
          useOnlyLinks: true
        ))
      tickMarkActiveStyleValue = tickMarkActiveStyleValue
        .merged(with: parent.tickMarkActiveStyle?.resolveOptionalValue(
          context: context,
          validator: ResolvedValue.tickMarkActiveStyleValidator,
          useOnlyLinks: true
        ))
      tickMarkInactiveStyleValue = tickMarkInactiveStyleValue
        .merged(with: parent.tickMarkInactiveStyle?.resolveOptionalValue(
          context: context,
          validator: ResolvedValue.tickMarkInactiveStyleValidator,
          useOnlyLinks: true
        ))
      tooltipsValue = tooltipsValue.merged(with: parent.tooltips?.resolveOptionalValue(
        context: context,
        validator: ResolvedValue.tooltipsValidator,
        useOnlyLinks: true
      ))
      trackActiveStyleValue = trackActiveStyleValue
        .merged(with: parent.trackActiveStyle?.resolveValue(context: context, useOnlyLinks: true))
      trackInactiveStyleValue = trackInactiveStyleValue
        .merged(with: parent.trackInactiveStyle?.resolveValue(context: context, useOnlyLinks: true))
      transformValue = transformValue.merged(with: parent.transform?.resolveOptionalValue(
        context: context,
        validator: ResolvedValue.transformValidator,
        useOnlyLinks: true
      ))
      transitionChangeValue = transitionChangeValue
        .merged(with: parent.transitionChange?.resolveOptionalValue(
          context: context,
          validator: ResolvedValue.transitionChangeValidator,
          useOnlyLinks: true
        ))
      transitionInValue = transitionInValue.merged(with: parent.transitionIn?.resolveOptionalValue(
        context: context,
        validator: ResolvedValue.transitionInValidator,
        useOnlyLinks: true
      ))
      transitionOutValue = transitionOutValue
        .merged(with: parent.transitionOut?.resolveOptionalValue(
          context: context,
          validator: ResolvedValue.transitionOutValidator,
          useOnlyLinks: true
        ))
      visibilityActionValue = visibilityActionValue
        .merged(with: parent.visibilityAction?.resolveOptionalValue(
          context: context,
          validator: ResolvedValue.visibilityActionValidator,
          useOnlyLinks: true
        ))
      visibilityActionsValue = visibilityActionsValue
        .merged(with: parent.visibilityActions?.resolveOptionalValue(
          context: context,
          validator: ResolvedValue.visibilityActionsValidator,
          useOnlyLinks: true
        ))
      widthValue = widthValue.merged(with: parent.width?.resolveOptionalValue(
        context: context,
        validator: ResolvedValue.widthValidator,
        useOnlyLinks: true
      ))
    }
    var errors = mergeErrors(
      accessibilityValue.errorsOrWarnings?
        .map { Either.right($0.asError(deserializing: "accessibility", level: .warning)) },
      alignmentHorizontalValue.errorsOrWarnings?
        .map { Either.right($0.asError(deserializing: "alignment_horizontal", level: .warning)) },
      alignmentVerticalValue.errorsOrWarnings?
        .map { Either.right($0.asError(deserializing: "alignment_vertical", level: .warning)) },
      alphaValue.errorsOrWarnings?
        .map { Either.right($0.asError(deserializing: "alpha", level: .warning)) },
      backgroundValue.errorsOrWarnings?
        .map { Either.right($0.asError(deserializing: "background", level: .warning)) },
      borderValue.errorsOrWarnings?
        .map { Either.right($0.asError(deserializing: "border", level: .warning)) },
      columnSpanValue.errorsOrWarnings?
        .map { Either.right($0.asError(deserializing: "column_span", level: .warning)) },
      extensionsValue.errorsOrWarnings?
        .map { Either.right($0.asError(deserializing: "extensions", level: .warning)) },
      focusValue.errorsOrWarnings?
        .map { Either.right($0.asError(deserializing: "focus", level: .warning)) },
      heightValue.errorsOrWarnings?
        .map { Either.right($0.asError(deserializing: "height", level: .warning)) },
      idValue.errorsOrWarnings?
        .map { Either.right($0.asError(deserializing: "id", level: .warning)) },
      marginsValue.errorsOrWarnings?
        .map { Either.right($0.asError(deserializing: "margins", level: .warning)) },
      maxValueValue.errorsOrWarnings?
        .map { Either.right($0.asError(deserializing: "max_value", level: .warning)) },
      minValueValue.errorsOrWarnings?
        .map { Either.right($0.asError(deserializing: "min_value", level: .warning)) },
      paddingsValue.errorsOrWarnings?
        .map { Either.right($0.asError(deserializing: "paddings", level: .warning)) },
      rowSpanValue.errorsOrWarnings?
        .map { Either.right($0.asError(deserializing: "row_span", level: .warning)) },
      secondaryValueAccessibilityValue.errorsOrWarnings?
        .map {
          Either.right($0.asError(deserializing: "secondary_value_accessibility", level: .warning))
        },
      selectedActionsValue.errorsOrWarnings?
        .map { Either.right($0.asError(deserializing: "selected_actions", level: .warning)) },
      thumbSecondaryStyleValue.errorsOrWarnings?
        .map { Either.right($0.asError(deserializing: "thumb_secondary_style", level: .warning)) },
      thumbSecondaryTextStyleValue.errorsOrWarnings?
        .map {
          Either.right($0.asError(deserializing: "thumb_secondary_text_style", level: .warning))
        },
      thumbSecondaryValueVariableValue.errorsOrWarnings?
        .map {
          Either.right($0.asError(deserializing: "thumb_secondary_value_variable", level: .warning))
        },
      thumbStyleValue.errorsOrWarnings?
        .map { Either.right($0.asError(deserializing: "thumb_style", level: .error)) },
      thumbTextStyleValue.errorsOrWarnings?
        .map { Either.right($0.asError(deserializing: "thumb_text_style", level: .warning)) },
      thumbValueVariableValue.errorsOrWarnings?
        .map { Either.right($0.asError(deserializing: "thumb_value_variable", level: .warning)) },
      tickMarkActiveStyleValue.errorsOrWarnings?
        .map { Either.right($0.asError(deserializing: "tick_mark_active_style", level: .warning)) },
      tickMarkInactiveStyleValue.errorsOrWarnings?
        .map { Either.right($0.asError(deserializing: "tick_mark_inactive_style", level: .warning))
        },
      tooltipsValue.errorsOrWarnings?
        .map { Either.right($0.asError(deserializing: "tooltips", level: .warning)) },
      trackActiveStyleValue.errorsOrWarnings?
        .map { Either.right($0.asError(deserializing: "track_active_style", level: .error)) },
      trackInactiveStyleValue.errorsOrWarnings?
        .map { Either.right($0.asError(deserializing: "track_inactive_style", level: .error)) },
      transformValue.errorsOrWarnings?
        .map { Either.right($0.asError(deserializing: "transform", level: .warning)) },
      transitionChangeValue.errorsOrWarnings?
        .map { Either.right($0.asError(deserializing: "transition_change", level: .warning)) },
      transitionInValue.errorsOrWarnings?
        .map { Either.right($0.asError(deserializing: "transition_in", level: .warning)) },
      transitionOutValue.errorsOrWarnings?
        .map { Either.right($0.asError(deserializing: "transition_out", level: .warning)) },
      transitionTriggersValue.errorsOrWarnings?
        .map { Either.right($0.asError(deserializing: "transition_triggers", level: .warning)) },
      visibilityValue.errorsOrWarnings?
        .map { Either.right($0.asError(deserializing: "visibility", level: .warning)) },
      visibilityActionValue.errorsOrWarnings?
        .map { Either.right($0.asError(deserializing: "visibility_action", level: .warning)) },
      visibilityActionsValue.errorsOrWarnings?
        .map { Either.right($0.asError(deserializing: "visibility_actions", level: .warning)) },
      widthValue.errorsOrWarnings?
        .map { Either.right($0.asError(deserializing: "width", level: .warning)) }
    )
    if case .noValue = thumbStyleValue {
      errors
        .append(.right(FieldError(
          fieldName: "thumb_style",
          level: .error,
          error: .requiredFieldIsMissing
        )))
    }
    if case .noValue = trackActiveStyleValue {
      errors
        .append(.right(FieldError(
          fieldName: "track_active_style",
          level: .error,
          error: .requiredFieldIsMissing
        )))
    }
    if case .noValue = trackInactiveStyleValue {
      errors
        .append(.right(FieldError(
          fieldName: "track_inactive_style",
          level: .error,
          error: .requiredFieldIsMissing
        )))
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
    return errors
      .isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: Templates) throws -> DivSliderTemplate {
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
      secondaryValueAccessibility: secondaryValueAccessibility ?? mergedParent
        .secondaryValueAccessibility,
      selectedActions: selectedActions ?? mergedParent.selectedActions,
      thumbSecondaryStyle: thumbSecondaryStyle ?? mergedParent.thumbSecondaryStyle,
      thumbSecondaryTextStyle: thumbSecondaryTextStyle ?? mergedParent.thumbSecondaryTextStyle,
      thumbSecondaryValueVariable: thumbSecondaryValueVariable ?? mergedParent
        .thumbSecondaryValueVariable,
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

  public func resolveParent(templates: Templates) throws -> DivSliderTemplate {
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
      secondaryValueAccessibility: merged.secondaryValueAccessibility?
        .tryResolveParent(templates: templates),
      selectedActions: merged.selectedActions?.tryResolveParent(templates: templates),
      thumbSecondaryStyle: merged.thumbSecondaryStyle?.tryResolveParent(templates: templates),
      thumbSecondaryTextStyle: merged.thumbSecondaryTextStyle?
        .tryResolveParent(templates: templates),
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
