// Generated code. Do not modify.

import CoreFoundation
import Foundation

import CommonCore
import Serialization
import TemplatesSupport

public final class DivIndicatorTemplate: TemplateValue, TemplateDeserializable {
  public typealias Animation = DivIndicator.Animation

  public static let type: String = "indicator"
  public let parent: String? // at least 1 char
  public let accessibility: Field<DivAccessibilityTemplate>?
  public let activeItemColor: Field<Expression<Color>>? // default value: #ffdc60
  public let activeItemSize: Field<Expression<Double>>? // constraint: number > 0; default value: 1.3
  public let alignmentHorizontal: Field<Expression<DivAlignmentHorizontal>>?
  public let alignmentVertical: Field<Expression<DivAlignmentVertical>>?
  public let alpha: Field<Expression<Double>>? // constraint: number >= 0.0 && number <= 1.0; default value: 1.0
  public let animation: Field<Expression<Animation>>? // default value: scale
  public let background: Field<[DivBackgroundTemplate]>? // at least 1 elements
  public let border: Field<DivBorderTemplate>?
  public let columnSpan: Field<Expression<Int>>? // constraint: number >= 0
  public let extensions: Field<[DivExtensionTemplate]>? // at least 1 elements
  public let focus: Field<DivFocusTemplate>?
  public let height: Field<DivSizeTemplate>? // default value: .divWrapContentSize(DivWrapContentSize())
  public let id: Field<String>? // at least 1 char
  public let inactiveItemColor: Field<Expression<Color>>? // default value: #33919cb5
  public let margins: Field<DivEdgeInsetsTemplate>?
  public let minimumItemSize: Field<Expression<Double>>? // constraint: number > 0; default value: 0.5
  public let paddings: Field<DivEdgeInsetsTemplate>?
  public let pagerId: Field<String>?
  public let rowSpan: Field<Expression<Int>>? // constraint: number >= 0
  public let selectedActions: Field<[DivActionTemplate]>? // at least 1 elements
  public let shape: Field<DivShapeTemplate>? // default value: .divRoundedRectangleShape(DivRoundedRectangleShape())
  public let spaceBetweenCenters: Field<DivFixedSizeTemplate>? // default value: DivFixedSize(value: .value(15))
  public let tooltips: Field<[DivTooltipTemplate]>? // at least 1 elements
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
    self.init(
      parent: try dictionary.getOptionalField("type", validator: Self.parentValidator),
      accessibility: try dictionary.getOptionalField(
        "accessibility",
        templateToType: templateToType
      ),
      activeItemColor: try dictionary.getOptionalField(
        "active_item_color",
        transform: Color.color(withHexString:)
      ),
      activeItemSize: try dictionary.getOptionalField("active_item_size"),
      alignmentHorizontal: try dictionary.getOptionalField("alignment_horizontal"),
      alignmentVertical: try dictionary.getOptionalField("alignment_vertical"),
      alpha: try dictionary.getOptionalField("alpha"),
      animation: try dictionary.getOptionalField("animation"),
      background: try dictionary.getOptionalArray("background", templateToType: templateToType),
      border: try dictionary.getOptionalField("border", templateToType: templateToType),
      columnSpan: try dictionary.getOptionalField("column_span"),
      extensions: try dictionary.getOptionalArray("extensions", templateToType: templateToType),
      focus: try dictionary.getOptionalField("focus", templateToType: templateToType),
      height: try dictionary.getOptionalField("height", templateToType: templateToType),
      id: try dictionary.getOptionalField("id"),
      inactiveItemColor: try dictionary.getOptionalField(
        "inactive_item_color",
        transform: Color.color(withHexString:)
      ),
      margins: try dictionary.getOptionalField("margins", templateToType: templateToType),
      minimumItemSize: try dictionary.getOptionalField("minimum_item_size"),
      paddings: try dictionary.getOptionalField("paddings", templateToType: templateToType),
      pagerId: try dictionary.getOptionalField("pager_id"),
      rowSpan: try dictionary.getOptionalField("row_span"),
      selectedActions: try dictionary.getOptionalArray(
        "selected_actions",
        templateToType: templateToType
      ),
      shape: try dictionary.getOptionalField("shape", templateToType: templateToType),
      spaceBetweenCenters: try dictionary.getOptionalField(
        "space_between_centers",
        templateToType: templateToType
      ),
      tooltips: try dictionary.getOptionalArray("tooltips", templateToType: templateToType),
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
  }

  init(
    parent: String?,
    accessibility: Field<DivAccessibilityTemplate>? = nil,
    activeItemColor: Field<Expression<Color>>? = nil,
    activeItemSize: Field<Expression<Double>>? = nil,
    alignmentHorizontal: Field<Expression<DivAlignmentHorizontal>>? = nil,
    alignmentVertical: Field<Expression<DivAlignmentVertical>>? = nil,
    alpha: Field<Expression<Double>>? = nil,
    animation: Field<Expression<Animation>>? = nil,
    background: Field<[DivBackgroundTemplate]>? = nil,
    border: Field<DivBorderTemplate>? = nil,
    columnSpan: Field<Expression<Int>>? = nil,
    extensions: Field<[DivExtensionTemplate]>? = nil,
    focus: Field<DivFocusTemplate>? = nil,
    height: Field<DivSizeTemplate>? = nil,
    id: Field<String>? = nil,
    inactiveItemColor: Field<Expression<Color>>? = nil,
    margins: Field<DivEdgeInsetsTemplate>? = nil,
    minimumItemSize: Field<Expression<Double>>? = nil,
    paddings: Field<DivEdgeInsetsTemplate>? = nil,
    pagerId: Field<String>? = nil,
    rowSpan: Field<Expression<Int>>? = nil,
    selectedActions: Field<[DivActionTemplate]>? = nil,
    shape: Field<DivShapeTemplate>? = nil,
    spaceBetweenCenters: Field<DivFixedSizeTemplate>? = nil,
    tooltips: Field<[DivTooltipTemplate]>? = nil,
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
    self.activeItemColor = activeItemColor
    self.activeItemSize = activeItemSize
    self.alignmentHorizontal = alignmentHorizontal
    self.alignmentVertical = alignmentVertical
    self.alpha = alpha
    self.animation = animation
    self.background = background
    self.border = border
    self.columnSpan = columnSpan
    self.extensions = extensions
    self.focus = focus
    self.height = height
    self.id = id
    self.inactiveItemColor = inactiveItemColor
    self.margins = margins
    self.minimumItemSize = minimumItemSize
    self.paddings = paddings
    self.pagerId = pagerId
    self.rowSpan = rowSpan
    self.selectedActions = selectedActions
    self.shape = shape
    self.spaceBetweenCenters = spaceBetweenCenters
    self.tooltips = tooltips
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
    parent: DivIndicatorTemplate?
  ) -> DeserializationResult<DivIndicator> {
    let accessibilityValue = parent?.accessibility?.resolveOptionalValue(
      context: context,
      validator: ResolvedValue.accessibilityValidator,
      useOnlyLinks: true
    ) ?? .noValue
    let activeItemColorValue = parent?.activeItemColor?.resolveOptionalValue(
      context: context,
      transform: Color.color(withHexString:),
      validator: ResolvedValue.activeItemColorValidator
    ) ?? .noValue
    let activeItemSizeValue = parent?.activeItemSize?.resolveOptionalValue(
      context: context,
      validator: ResolvedValue.activeItemSizeValidator
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
    let animationValue = parent?.animation?.resolveOptionalValue(
      context: context,
      validator: ResolvedValue.animationValidator
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
    let inactiveItemColorValue = parent?.inactiveItemColor?.resolveOptionalValue(
      context: context,
      transform: Color.color(withHexString:),
      validator: ResolvedValue.inactiveItemColorValidator
    ) ?? .noValue
    let marginsValue = parent?.margins?.resolveOptionalValue(
      context: context,
      validator: ResolvedValue.marginsValidator,
      useOnlyLinks: true
    ) ?? .noValue
    let minimumItemSizeValue = parent?.minimumItemSize?.resolveOptionalValue(
      context: context,
      validator: ResolvedValue.minimumItemSizeValidator
    ) ?? .noValue
    let paddingsValue = parent?.paddings?.resolveOptionalValue(
      context: context,
      validator: ResolvedValue.paddingsValidator,
      useOnlyLinks: true
    ) ?? .noValue
    let pagerIdValue = parent?.pagerId?.resolveOptionalValue(
      context: context,
      validator: ResolvedValue.pagerIdValidator
    ) ?? .noValue
    let rowSpanValue = parent?.rowSpan?.resolveOptionalValue(
      context: context,
      validator: ResolvedValue.rowSpanValidator
    ) ?? .noValue
    let selectedActionsValue = parent?.selectedActions?.resolveOptionalValue(
      context: context,
      validator: ResolvedValue.selectedActionsValidator,
      useOnlyLinks: true
    ) ?? .noValue
    let shapeValue = parent?.shape?.resolveOptionalValue(
      context: context,
      validator: ResolvedValue.shapeValidator,
      useOnlyLinks: true
    ) ?? .noValue
    let spaceBetweenCentersValue = parent?.spaceBetweenCenters?.resolveOptionalValue(
      context: context,
      validator: ResolvedValue.spaceBetweenCentersValidator,
      useOnlyLinks: true
    ) ?? .noValue
    let tooltipsValue = parent?.tooltips?.resolveOptionalValue(
      context: context,
      validator: ResolvedValue.tooltipsValidator,
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
    let errors = mergeErrors(
      accessibilityValue.errorsOrWarnings?
        .map { .right($0.asError(deserializing: "accessibility", level: .warning)) },
      activeItemColorValue.errorsOrWarnings?
        .map { .right($0.asError(deserializing: "active_item_color", level: .warning)) },
      activeItemSizeValue.errorsOrWarnings?
        .map { .right($0.asError(deserializing: "active_item_size", level: .warning)) },
      alignmentHorizontalValue.errorsOrWarnings?
        .map { .right($0.asError(deserializing: "alignment_horizontal", level: .warning)) },
      alignmentVerticalValue.errorsOrWarnings?
        .map { .right($0.asError(deserializing: "alignment_vertical", level: .warning)) },
      alphaValue.errorsOrWarnings?
        .map { .right($0.asError(deserializing: "alpha", level: .warning)) },
      animationValue.errorsOrWarnings?
        .map { .right($0.asError(deserializing: "animation", level: .warning)) },
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
      inactiveItemColorValue.errorsOrWarnings?
        .map { .right($0.asError(deserializing: "inactive_item_color", level: .warning)) },
      marginsValue.errorsOrWarnings?
        .map { .right($0.asError(deserializing: "margins", level: .warning)) },
      minimumItemSizeValue.errorsOrWarnings?
        .map { .right($0.asError(deserializing: "minimum_item_size", level: .warning)) },
      paddingsValue.errorsOrWarnings?
        .map { .right($0.asError(deserializing: "paddings", level: .warning)) },
      pagerIdValue.errorsOrWarnings?
        .map { .right($0.asError(deserializing: "pager_id", level: .warning)) },
      rowSpanValue.errorsOrWarnings?
        .map { .right($0.asError(deserializing: "row_span", level: .warning)) },
      selectedActionsValue.errorsOrWarnings?
        .map { .right($0.asError(deserializing: "selected_actions", level: .warning)) },
      shapeValue.errorsOrWarnings?
        .map { .right($0.asError(deserializing: "shape", level: .warning)) },
      spaceBetweenCentersValue.errorsOrWarnings?
        .map { .right($0.asError(deserializing: "space_between_centers", level: .warning)) },
      tooltipsValue.errorsOrWarnings?
        .map { .right($0.asError(deserializing: "tooltips", level: .warning)) },
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
    let result = DivIndicator(
      accessibility: accessibilityValue.value,
      activeItemColor: activeItemColorValue.value,
      activeItemSize: activeItemSizeValue.value,
      alignmentHorizontal: alignmentHorizontalValue.value,
      alignmentVertical: alignmentVerticalValue.value,
      alpha: alphaValue.value,
      animation: animationValue.value,
      background: backgroundValue.value,
      border: borderValue.value,
      columnSpan: columnSpanValue.value,
      extensions: extensionsValue.value,
      focus: focusValue.value,
      height: heightValue.value,
      id: idValue.value,
      inactiveItemColor: inactiveItemColorValue.value,
      margins: marginsValue.value,
      minimumItemSize: minimumItemSizeValue.value,
      paddings: paddingsValue.value,
      pagerId: pagerIdValue.value,
      rowSpan: rowSpanValue.value,
      selectedActions: selectedActionsValue.value,
      shape: shapeValue.value,
      spaceBetweenCenters: spaceBetweenCentersValue.value,
      tooltips: tooltipsValue.value,
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
    parent: DivIndicatorTemplate?,
    useOnlyLinks: Bool
  ) -> DeserializationResult<DivIndicator> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var accessibilityValue: DeserializationResult<DivAccessibility> = .noValue
    var activeItemColorValue: DeserializationResult<Expression<Color>> = parent?.activeItemColor?
      .value() ?? .noValue
    var activeItemSizeValue: DeserializationResult<Expression<Double>> = parent?.activeItemSize?
      .value() ?? .noValue
    var alignmentHorizontalValue: DeserializationResult<Expression<DivAlignmentHorizontal>> =
      parent?.alignmentHorizontal?.value() ?? .noValue
    var alignmentVerticalValue: DeserializationResult<Expression<DivAlignmentVertical>> = parent?
      .alignmentVertical?.value() ?? .noValue
    var alphaValue: DeserializationResult<Expression<Double>> = parent?.alpha?.value() ?? .noValue
    var animationValue: DeserializationResult<Expression<DivIndicator.Animation>> = parent?
      .animation?.value() ?? .noValue
    var backgroundValue: DeserializationResult<[DivBackground]> = .noValue
    var borderValue: DeserializationResult<DivBorder> = .noValue
    var columnSpanValue: DeserializationResult<Expression<Int>> = parent?.columnSpan?
      .value() ?? .noValue
    var extensionsValue: DeserializationResult<[DivExtension]> = .noValue
    var focusValue: DeserializationResult<DivFocus> = .noValue
    var heightValue: DeserializationResult<DivSize> = .noValue
    var idValue: DeserializationResult<String> = parent?.id?
      .value(validatedBy: ResolvedValue.idValidator) ?? .noValue
    var inactiveItemColorValue: DeserializationResult<Expression<Color>> = parent?
      .inactiveItemColor?.value() ?? .noValue
    var marginsValue: DeserializationResult<DivEdgeInsets> = .noValue
    var minimumItemSizeValue: DeserializationResult<Expression<Double>> = parent?.minimumItemSize?
      .value() ?? .noValue
    var paddingsValue: DeserializationResult<DivEdgeInsets> = .noValue
    var pagerIdValue: DeserializationResult<String> = parent?.pagerId?
      .value(validatedBy: ResolvedValue.pagerIdValidator) ?? .noValue
    var rowSpanValue: DeserializationResult<Expression<Int>> = parent?.rowSpan?.value() ?? .noValue
    var selectedActionsValue: DeserializationResult<[DivAction]> = .noValue
    var shapeValue: DeserializationResult<DivShape> = .noValue
    var spaceBetweenCentersValue: DeserializationResult<DivFixedSize> = .noValue
    var tooltipsValue: DeserializationResult<[DivTooltip]> = .noValue
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
      case "active_item_color":
        activeItemColorValue = deserialize(
          __dictValue,
          transform: Color.color(withHexString:),
          validator: ResolvedValue.activeItemColorValidator
        ).merged(with: activeItemColorValue)
      case "active_item_size":
        activeItemSizeValue = deserialize(
          __dictValue,
          validator: ResolvedValue.activeItemSizeValidator
        ).merged(with: activeItemSizeValue)
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
      case "animation":
        animationValue = deserialize(__dictValue, validator: ResolvedValue.animationValidator)
          .merged(with: animationValue)
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
      case "inactive_item_color":
        inactiveItemColorValue = deserialize(
          __dictValue,
          transform: Color.color(withHexString:),
          validator: ResolvedValue.inactiveItemColorValidator
        ).merged(with: inactiveItemColorValue)
      case "margins":
        marginsValue = deserialize(
          __dictValue,
          templates: context.templates,
          templateToType: context.templateToType,
          validator: ResolvedValue.marginsValidator,
          type: DivEdgeInsetsTemplate.self
        ).merged(with: marginsValue)
      case "minimum_item_size":
        minimumItemSizeValue = deserialize(
          __dictValue,
          validator: ResolvedValue.minimumItemSizeValidator
        ).merged(with: minimumItemSizeValue)
      case "paddings":
        paddingsValue = deserialize(
          __dictValue,
          templates: context.templates,
          templateToType: context.templateToType,
          validator: ResolvedValue.paddingsValidator,
          type: DivEdgeInsetsTemplate.self
        ).merged(with: paddingsValue)
      case "pager_id":
        pagerIdValue = deserialize(__dictValue, validator: ResolvedValue.pagerIdValidator)
          .merged(with: pagerIdValue)
      case "row_span":
        rowSpanValue = deserialize(__dictValue, validator: ResolvedValue.rowSpanValidator)
          .merged(with: rowSpanValue)
      case "selected_actions":
        selectedActionsValue = deserialize(
          __dictValue,
          templates: context.templates,
          templateToType: context.templateToType,
          validator: ResolvedValue.selectedActionsValidator,
          type: DivActionTemplate.self
        ).merged(with: selectedActionsValue)
      case "shape":
        shapeValue = deserialize(
          __dictValue,
          templates: context.templates,
          templateToType: context.templateToType,
          validator: ResolvedValue.shapeValidator,
          type: DivShapeTemplate.self
        ).merged(with: shapeValue)
      case "space_between_centers":
        spaceBetweenCentersValue = deserialize(
          __dictValue,
          templates: context.templates,
          templateToType: context.templateToType,
          validator: ResolvedValue.spaceBetweenCentersValidator,
          type: DivFixedSizeTemplate.self
        ).merged(with: spaceBetweenCentersValue)
      case "tooltips":
        tooltipsValue = deserialize(
          __dictValue,
          templates: context.templates,
          templateToType: context.templateToType,
          validator: ResolvedValue.tooltipsValidator,
          type: DivTooltipTemplate.self
        ).merged(with: tooltipsValue)
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
      case parent?.activeItemColor?.link:
        activeItemColorValue = activeItemColorValue.merged(with: deserialize(
          __dictValue,
          transform: Color.color(withHexString:),
          validator: ResolvedValue.activeItemColorValidator
        ))
      case parent?.activeItemSize?.link:
        activeItemSizeValue = activeItemSizeValue
          .merged(with: deserialize(__dictValue, validator: ResolvedValue.activeItemSizeValidator))
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
      case parent?.animation?.link:
        animationValue = animationValue
          .merged(with: deserialize(__dictValue, validator: ResolvedValue.animationValidator))
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
      case parent?.inactiveItemColor?.link:
        inactiveItemColorValue = inactiveItemColorValue.merged(with: deserialize(
          __dictValue,
          transform: Color.color(withHexString:),
          validator: ResolvedValue.inactiveItemColorValidator
        ))
      case parent?.margins?.link:
        marginsValue = marginsValue.merged(with: deserialize(
          __dictValue,
          templates: context.templates,
          templateToType: context.templateToType,
          validator: ResolvedValue.marginsValidator,
          type: DivEdgeInsetsTemplate.self
        ))
      case parent?.minimumItemSize?.link:
        minimumItemSizeValue = minimumItemSizeValue
          .merged(with: deserialize(__dictValue, validator: ResolvedValue.minimumItemSizeValidator))
      case parent?.paddings?.link:
        paddingsValue = paddingsValue.merged(with: deserialize(
          __dictValue,
          templates: context.templates,
          templateToType: context.templateToType,
          validator: ResolvedValue.paddingsValidator,
          type: DivEdgeInsetsTemplate.self
        ))
      case parent?.pagerId?.link:
        pagerIdValue = pagerIdValue
          .merged(with: deserialize(__dictValue, validator: ResolvedValue.pagerIdValidator))
      case parent?.rowSpan?.link:
        rowSpanValue = rowSpanValue
          .merged(with: deserialize(__dictValue, validator: ResolvedValue.rowSpanValidator))
      case parent?.selectedActions?.link:
        selectedActionsValue = selectedActionsValue.merged(with: deserialize(
          __dictValue,
          templates: context.templates,
          templateToType: context.templateToType,
          validator: ResolvedValue.selectedActionsValidator,
          type: DivActionTemplate.self
        ))
      case parent?.shape?.link:
        shapeValue = shapeValue.merged(with: deserialize(
          __dictValue,
          templates: context.templates,
          templateToType: context.templateToType,
          validator: ResolvedValue.shapeValidator,
          type: DivShapeTemplate.self
        ))
      case parent?.spaceBetweenCenters?.link:
        spaceBetweenCentersValue = spaceBetweenCentersValue.merged(with: deserialize(
          __dictValue,
          templates: context.templates,
          templateToType: context.templateToType,
          validator: ResolvedValue.spaceBetweenCentersValidator,
          type: DivFixedSizeTemplate.self
        ))
      case parent?.tooltips?.link:
        tooltipsValue = tooltipsValue.merged(with: deserialize(
          __dictValue,
          templates: context.templates,
          templateToType: context.templateToType,
          validator: ResolvedValue.tooltipsValidator,
          type: DivTooltipTemplate.self
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
      selectedActionsValue = selectedActionsValue
        .merged(with: parent.selectedActions?.resolveOptionalValue(
          context: context,
          validator: ResolvedValue.selectedActionsValidator,
          useOnlyLinks: true
        ))
      shapeValue = shapeValue.merged(with: parent.shape?.resolveOptionalValue(
        context: context,
        validator: ResolvedValue.shapeValidator,
        useOnlyLinks: true
      ))
      spaceBetweenCentersValue = spaceBetweenCentersValue
        .merged(with: parent.spaceBetweenCenters?.resolveOptionalValue(
          context: context,
          validator: ResolvedValue.spaceBetweenCentersValidator,
          useOnlyLinks: true
        ))
      tooltipsValue = tooltipsValue.merged(with: parent.tooltips?.resolveOptionalValue(
        context: context,
        validator: ResolvedValue.tooltipsValidator,
        useOnlyLinks: true
      ))
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
    let errors = mergeErrors(
      accessibilityValue.errorsOrWarnings?
        .map { Either.right($0.asError(deserializing: "accessibility", level: .warning)) },
      activeItemColorValue.errorsOrWarnings?
        .map { Either.right($0.asError(deserializing: "active_item_color", level: .warning)) },
      activeItemSizeValue.errorsOrWarnings?
        .map { Either.right($0.asError(deserializing: "active_item_size", level: .warning)) },
      alignmentHorizontalValue.errorsOrWarnings?
        .map { Either.right($0.asError(deserializing: "alignment_horizontal", level: .warning)) },
      alignmentVerticalValue.errorsOrWarnings?
        .map { Either.right($0.asError(deserializing: "alignment_vertical", level: .warning)) },
      alphaValue.errorsOrWarnings?
        .map { Either.right($0.asError(deserializing: "alpha", level: .warning)) },
      animationValue.errorsOrWarnings?
        .map { Either.right($0.asError(deserializing: "animation", level: .warning)) },
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
      inactiveItemColorValue.errorsOrWarnings?
        .map { Either.right($0.asError(deserializing: "inactive_item_color", level: .warning)) },
      marginsValue.errorsOrWarnings?
        .map { Either.right($0.asError(deserializing: "margins", level: .warning)) },
      minimumItemSizeValue.errorsOrWarnings?
        .map { Either.right($0.asError(deserializing: "minimum_item_size", level: .warning)) },
      paddingsValue.errorsOrWarnings?
        .map { Either.right($0.asError(deserializing: "paddings", level: .warning)) },
      pagerIdValue.errorsOrWarnings?
        .map { Either.right($0.asError(deserializing: "pager_id", level: .warning)) },
      rowSpanValue.errorsOrWarnings?
        .map { Either.right($0.asError(deserializing: "row_span", level: .warning)) },
      selectedActionsValue.errorsOrWarnings?
        .map { Either.right($0.asError(deserializing: "selected_actions", level: .warning)) },
      shapeValue.errorsOrWarnings?
        .map { Either.right($0.asError(deserializing: "shape", level: .warning)) },
      spaceBetweenCentersValue.errorsOrWarnings?
        .map { Either.right($0.asError(deserializing: "space_between_centers", level: .warning)) },
      tooltipsValue.errorsOrWarnings?
        .map { Either.right($0.asError(deserializing: "tooltips", level: .warning)) },
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
    let result = DivIndicator(
      accessibility: accessibilityValue.value,
      activeItemColor: activeItemColorValue.value,
      activeItemSize: activeItemSizeValue.value,
      alignmentHorizontal: alignmentHorizontalValue.value,
      alignmentVertical: alignmentVerticalValue.value,
      alpha: alphaValue.value,
      animation: animationValue.value,
      background: backgroundValue.value,
      border: borderValue.value,
      columnSpan: columnSpanValue.value,
      extensions: extensionsValue.value,
      focus: focusValue.value,
      height: heightValue.value,
      id: idValue.value,
      inactiveItemColor: inactiveItemColorValue.value,
      margins: marginsValue.value,
      minimumItemSize: minimumItemSizeValue.value,
      paddings: paddingsValue.value,
      pagerId: pagerIdValue.value,
      rowSpan: rowSpanValue.value,
      selectedActions: selectedActionsValue.value,
      shape: shapeValue.value,
      spaceBetweenCenters: spaceBetweenCentersValue.value,
      tooltips: tooltipsValue.value,
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

  private func mergedWithParent(templates: Templates) throws -> DivIndicatorTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? DivIndicatorTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return DivIndicatorTemplate(
      parent: nil,
      accessibility: accessibility ?? mergedParent.accessibility,
      activeItemColor: activeItemColor ?? mergedParent.activeItemColor,
      activeItemSize: activeItemSize ?? mergedParent.activeItemSize,
      alignmentHorizontal: alignmentHorizontal ?? mergedParent.alignmentHorizontal,
      alignmentVertical: alignmentVertical ?? mergedParent.alignmentVertical,
      alpha: alpha ?? mergedParent.alpha,
      animation: animation ?? mergedParent.animation,
      background: background ?? mergedParent.background,
      border: border ?? mergedParent.border,
      columnSpan: columnSpan ?? mergedParent.columnSpan,
      extensions: extensions ?? mergedParent.extensions,
      focus: focus ?? mergedParent.focus,
      height: height ?? mergedParent.height,
      id: id ?? mergedParent.id,
      inactiveItemColor: inactiveItemColor ?? mergedParent.inactiveItemColor,
      margins: margins ?? mergedParent.margins,
      minimumItemSize: minimumItemSize ?? mergedParent.minimumItemSize,
      paddings: paddings ?? mergedParent.paddings,
      pagerId: pagerId ?? mergedParent.pagerId,
      rowSpan: rowSpan ?? mergedParent.rowSpan,
      selectedActions: selectedActions ?? mergedParent.selectedActions,
      shape: shape ?? mergedParent.shape,
      spaceBetweenCenters: spaceBetweenCenters ?? mergedParent.spaceBetweenCenters,
      tooltips: tooltips ?? mergedParent.tooltips,
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

  public func resolveParent(templates: Templates) throws -> DivIndicatorTemplate {
    let merged = try mergedWithParent(templates: templates)

    return DivIndicatorTemplate(
      parent: nil,
      accessibility: merged.accessibility?.tryResolveParent(templates: templates),
      activeItemColor: merged.activeItemColor,
      activeItemSize: merged.activeItemSize,
      alignmentHorizontal: merged.alignmentHorizontal,
      alignmentVertical: merged.alignmentVertical,
      alpha: merged.alpha,
      animation: merged.animation,
      background: merged.background?.tryResolveParent(templates: templates),
      border: merged.border?.tryResolveParent(templates: templates),
      columnSpan: merged.columnSpan,
      extensions: merged.extensions?.tryResolveParent(templates: templates),
      focus: merged.focus?.tryResolveParent(templates: templates),
      height: merged.height?.tryResolveParent(templates: templates),
      id: merged.id,
      inactiveItemColor: merged.inactiveItemColor,
      margins: merged.margins?.tryResolveParent(templates: templates),
      minimumItemSize: merged.minimumItemSize,
      paddings: merged.paddings?.tryResolveParent(templates: templates),
      pagerId: merged.pagerId,
      rowSpan: merged.rowSpan,
      selectedActions: merged.selectedActions?.tryResolveParent(templates: templates),
      shape: merged.shape?.tryResolveParent(templates: templates),
      spaceBetweenCenters: merged.spaceBetweenCenters?.tryResolveParent(templates: templates),
      tooltips: merged.tooltips?.tryResolveParent(templates: templates),
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
