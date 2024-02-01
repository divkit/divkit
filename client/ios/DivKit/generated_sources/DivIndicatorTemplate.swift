// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivIndicatorTemplate: TemplateValue {
  public typealias Animation = DivIndicator.Animation

  public static let type: String = "indicator"
  public let parent: String?
  public let accessibility: Field<DivAccessibilityTemplate>?
  public let activeItemColor: Field<Expression<Color>>? // default value: #ffdc60
  public let activeItemSize: Field<Expression<Double>>? // constraint: number > 0; default value: 1.3
  public let activeShape: Field<DivRoundedRectangleShapeTemplate>?
  public let alignmentHorizontal: Field<Expression<DivAlignmentHorizontal>>?
  public let alignmentVertical: Field<Expression<DivAlignmentVertical>>?
  public let alpha: Field<Expression<Double>>? // constraint: number >= 0.0 && number <= 1.0; default value: 1.0
  public let animation: Field<Expression<Animation>>? // default value: scale
  public let background: Field<[DivBackgroundTemplate]>?
  public let border: Field<DivBorderTemplate>?
  public let columnSpan: Field<Expression<Int>>? // constraint: number >= 0
  public let disappearActions: Field<[DivDisappearActionTemplate]>?
  public let extensions: Field<[DivExtensionTemplate]>?
  public let focus: Field<DivFocusTemplate>?
  public let height: Field<DivSizeTemplate>? // default value: .divWrapContentSize(DivWrapContentSize())
  public let id: Field<String>?
  public let inactiveItemColor: Field<Expression<Color>>? // default value: #33919cb5
  public let inactiveMinimumShape: Field<DivRoundedRectangleShapeTemplate>?
  public let inactiveShape: Field<DivRoundedRectangleShapeTemplate>?
  public let itemsPlacement: Field<DivIndicatorItemPlacementTemplate>?
  public let margins: Field<DivEdgeInsetsTemplate>?
  public let minimumItemSize: Field<Expression<Double>>? // constraint: number > 0; default value: 0.5
  public let paddings: Field<DivEdgeInsetsTemplate>?
  public let pagerId: Field<String>?
  public let rowSpan: Field<Expression<Int>>? // constraint: number >= 0
  public let selectedActions: Field<[DivActionTemplate]>?
  public let shape: Field<DivShapeTemplate>? // default value: .divRoundedRectangleShape(DivRoundedRectangleShape())
  public let spaceBetweenCenters: Field<DivFixedSizeTemplate>? // default value: DivFixedSize(value: .value(15))
  public let tooltips: Field<[DivTooltipTemplate]>?
  public let transform: Field<DivTransformTemplate>?
  public let transitionChange: Field<DivChangeTransitionTemplate>?
  public let transitionIn: Field<DivAppearanceTransitionTemplate>?
  public let transitionOut: Field<DivAppearanceTransitionTemplate>?
  public let transitionTriggers: Field<[DivTransitionTrigger]>? // at least 1 elements
  public let visibility: Field<Expression<DivVisibility>>? // default value: visible
  public let visibilityAction: Field<DivVisibilityActionTemplate>?
  public let visibilityActions: Field<[DivVisibilityActionTemplate]>?
  public let width: Field<DivSizeTemplate>? // default value: .divMatchParentSize(DivMatchParentSize())

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      parent: dictionary["type"] as? String,
      accessibility: dictionary.getOptionalField("accessibility", templateToType: templateToType),
      activeItemColor: dictionary.getOptionalExpressionField("active_item_color", transform: Color.color(withHexString:)),
      activeItemSize: dictionary.getOptionalExpressionField("active_item_size"),
      activeShape: dictionary.getOptionalField("active_shape", templateToType: templateToType),
      alignmentHorizontal: dictionary.getOptionalExpressionField("alignment_horizontal"),
      alignmentVertical: dictionary.getOptionalExpressionField("alignment_vertical"),
      alpha: dictionary.getOptionalExpressionField("alpha"),
      animation: dictionary.getOptionalExpressionField("animation"),
      background: dictionary.getOptionalArray("background", templateToType: templateToType),
      border: dictionary.getOptionalField("border", templateToType: templateToType),
      columnSpan: dictionary.getOptionalExpressionField("column_span"),
      disappearActions: dictionary.getOptionalArray("disappear_actions", templateToType: templateToType),
      extensions: dictionary.getOptionalArray("extensions", templateToType: templateToType),
      focus: dictionary.getOptionalField("focus", templateToType: templateToType),
      height: dictionary.getOptionalField("height", templateToType: templateToType),
      id: dictionary.getOptionalField("id"),
      inactiveItemColor: dictionary.getOptionalExpressionField("inactive_item_color", transform: Color.color(withHexString:)),
      inactiveMinimumShape: dictionary.getOptionalField("inactive_minimum_shape", templateToType: templateToType),
      inactiveShape: dictionary.getOptionalField("inactive_shape", templateToType: templateToType),
      itemsPlacement: dictionary.getOptionalField("items_placement", templateToType: templateToType),
      margins: dictionary.getOptionalField("margins", templateToType: templateToType),
      minimumItemSize: dictionary.getOptionalExpressionField("minimum_item_size"),
      paddings: dictionary.getOptionalField("paddings", templateToType: templateToType),
      pagerId: dictionary.getOptionalField("pager_id"),
      rowSpan: dictionary.getOptionalExpressionField("row_span"),
      selectedActions: dictionary.getOptionalArray("selected_actions", templateToType: templateToType),
      shape: dictionary.getOptionalField("shape", templateToType: templateToType),
      spaceBetweenCenters: dictionary.getOptionalField("space_between_centers", templateToType: templateToType),
      tooltips: dictionary.getOptionalArray("tooltips", templateToType: templateToType),
      transform: dictionary.getOptionalField("transform", templateToType: templateToType),
      transitionChange: dictionary.getOptionalField("transition_change", templateToType: templateToType),
      transitionIn: dictionary.getOptionalField("transition_in", templateToType: templateToType),
      transitionOut: dictionary.getOptionalField("transition_out", templateToType: templateToType),
      transitionTriggers: dictionary.getOptionalArray("transition_triggers"),
      visibility: dictionary.getOptionalExpressionField("visibility"),
      visibilityAction: dictionary.getOptionalField("visibility_action", templateToType: templateToType),
      visibilityActions: dictionary.getOptionalArray("visibility_actions", templateToType: templateToType),
      width: dictionary.getOptionalField("width", templateToType: templateToType)
    )
  }

  init(
    parent: String?,
    accessibility: Field<DivAccessibilityTemplate>? = nil,
    activeItemColor: Field<Expression<Color>>? = nil,
    activeItemSize: Field<Expression<Double>>? = nil,
    activeShape: Field<DivRoundedRectangleShapeTemplate>? = nil,
    alignmentHorizontal: Field<Expression<DivAlignmentHorizontal>>? = nil,
    alignmentVertical: Field<Expression<DivAlignmentVertical>>? = nil,
    alpha: Field<Expression<Double>>? = nil,
    animation: Field<Expression<Animation>>? = nil,
    background: Field<[DivBackgroundTemplate]>? = nil,
    border: Field<DivBorderTemplate>? = nil,
    columnSpan: Field<Expression<Int>>? = nil,
    disappearActions: Field<[DivDisappearActionTemplate]>? = nil,
    extensions: Field<[DivExtensionTemplate]>? = nil,
    focus: Field<DivFocusTemplate>? = nil,
    height: Field<DivSizeTemplate>? = nil,
    id: Field<String>? = nil,
    inactiveItemColor: Field<Expression<Color>>? = nil,
    inactiveMinimumShape: Field<DivRoundedRectangleShapeTemplate>? = nil,
    inactiveShape: Field<DivRoundedRectangleShapeTemplate>? = nil,
    itemsPlacement: Field<DivIndicatorItemPlacementTemplate>? = nil,
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
    self.activeShape = activeShape
    self.alignmentHorizontal = alignmentHorizontal
    self.alignmentVertical = alignmentVertical
    self.alpha = alpha
    self.animation = animation
    self.background = background
    self.border = border
    self.columnSpan = columnSpan
    self.disappearActions = disappearActions
    self.extensions = extensions
    self.focus = focus
    self.height = height
    self.id = id
    self.inactiveItemColor = inactiveItemColor
    self.inactiveMinimumShape = inactiveMinimumShape
    self.inactiveShape = inactiveShape
    self.itemsPlacement = itemsPlacement
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

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivIndicatorTemplate?) -> DeserializationResult<DivIndicator> {
    let accessibilityValue = parent?.accessibility?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let activeItemColorValue = parent?.activeItemColor?.resolveOptionalValue(context: context, transform: Color.color(withHexString:)) ?? .noValue
    let activeItemSizeValue = parent?.activeItemSize?.resolveOptionalValue(context: context, validator: ResolvedValue.activeItemSizeValidator) ?? .noValue
    let activeShapeValue = parent?.activeShape?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let alignmentHorizontalValue = parent?.alignmentHorizontal?.resolveOptionalValue(context: context) ?? .noValue
    let alignmentVerticalValue = parent?.alignmentVertical?.resolveOptionalValue(context: context) ?? .noValue
    let alphaValue = parent?.alpha?.resolveOptionalValue(context: context, validator: ResolvedValue.alphaValidator) ?? .noValue
    let animationValue = parent?.animation?.resolveOptionalValue(context: context) ?? .noValue
    let backgroundValue = parent?.background?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let borderValue = parent?.border?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let columnSpanValue = parent?.columnSpan?.resolveOptionalValue(context: context, validator: ResolvedValue.columnSpanValidator) ?? .noValue
    let disappearActionsValue = parent?.disappearActions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let extensionsValue = parent?.extensions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let focusValue = parent?.focus?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let heightValue = parent?.height?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let idValue = parent?.id?.resolveOptionalValue(context: context) ?? .noValue
    let inactiveItemColorValue = parent?.inactiveItemColor?.resolveOptionalValue(context: context, transform: Color.color(withHexString:)) ?? .noValue
    let inactiveMinimumShapeValue = parent?.inactiveMinimumShape?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let inactiveShapeValue = parent?.inactiveShape?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let itemsPlacementValue = parent?.itemsPlacement?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let marginsValue = parent?.margins?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let minimumItemSizeValue = parent?.minimumItemSize?.resolveOptionalValue(context: context, validator: ResolvedValue.minimumItemSizeValidator) ?? .noValue
    let paddingsValue = parent?.paddings?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let pagerIdValue = parent?.pagerId?.resolveOptionalValue(context: context) ?? .noValue
    let rowSpanValue = parent?.rowSpan?.resolveOptionalValue(context: context, validator: ResolvedValue.rowSpanValidator) ?? .noValue
    let selectedActionsValue = parent?.selectedActions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let shapeValue = parent?.shape?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let spaceBetweenCentersValue = parent?.spaceBetweenCenters?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let tooltipsValue = parent?.tooltips?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let transformValue = parent?.transform?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let transitionChangeValue = parent?.transitionChange?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let transitionInValue = parent?.transitionIn?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let transitionOutValue = parent?.transitionOut?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let transitionTriggersValue = parent?.transitionTriggers?.resolveOptionalValue(context: context, validator: ResolvedValue.transitionTriggersValidator) ?? .noValue
    let visibilityValue = parent?.visibility?.resolveOptionalValue(context: context) ?? .noValue
    let visibilityActionValue = parent?.visibilityAction?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let visibilityActionsValue = parent?.visibilityActions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let widthValue = parent?.width?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let errors = mergeErrors(
      accessibilityValue.errorsOrWarnings?.map { .nestedObjectError(field: "accessibility", error: $0) },
      activeItemColorValue.errorsOrWarnings?.map { .nestedObjectError(field: "active_item_color", error: $0) },
      activeItemSizeValue.errorsOrWarnings?.map { .nestedObjectError(field: "active_item_size", error: $0) },
      activeShapeValue.errorsOrWarnings?.map { .nestedObjectError(field: "active_shape", error: $0) },
      alignmentHorizontalValue.errorsOrWarnings?.map { .nestedObjectError(field: "alignment_horizontal", error: $0) },
      alignmentVerticalValue.errorsOrWarnings?.map { .nestedObjectError(field: "alignment_vertical", error: $0) },
      alphaValue.errorsOrWarnings?.map { .nestedObjectError(field: "alpha", error: $0) },
      animationValue.errorsOrWarnings?.map { .nestedObjectError(field: "animation", error: $0) },
      backgroundValue.errorsOrWarnings?.map { .nestedObjectError(field: "background", error: $0) },
      borderValue.errorsOrWarnings?.map { .nestedObjectError(field: "border", error: $0) },
      columnSpanValue.errorsOrWarnings?.map { .nestedObjectError(field: "column_span", error: $0) },
      disappearActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "disappear_actions", error: $0) },
      extensionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "extensions", error: $0) },
      focusValue.errorsOrWarnings?.map { .nestedObjectError(field: "focus", error: $0) },
      heightValue.errorsOrWarnings?.map { .nestedObjectError(field: "height", error: $0) },
      idValue.errorsOrWarnings?.map { .nestedObjectError(field: "id", error: $0) },
      inactiveItemColorValue.errorsOrWarnings?.map { .nestedObjectError(field: "inactive_item_color", error: $0) },
      inactiveMinimumShapeValue.errorsOrWarnings?.map { .nestedObjectError(field: "inactive_minimum_shape", error: $0) },
      inactiveShapeValue.errorsOrWarnings?.map { .nestedObjectError(field: "inactive_shape", error: $0) },
      itemsPlacementValue.errorsOrWarnings?.map { .nestedObjectError(field: "items_placement", error: $0) },
      marginsValue.errorsOrWarnings?.map { .nestedObjectError(field: "margins", error: $0) },
      minimumItemSizeValue.errorsOrWarnings?.map { .nestedObjectError(field: "minimum_item_size", error: $0) },
      paddingsValue.errorsOrWarnings?.map { .nestedObjectError(field: "paddings", error: $0) },
      pagerIdValue.errorsOrWarnings?.map { .nestedObjectError(field: "pager_id", error: $0) },
      rowSpanValue.errorsOrWarnings?.map { .nestedObjectError(field: "row_span", error: $0) },
      selectedActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "selected_actions", error: $0) },
      shapeValue.errorsOrWarnings?.map { .nestedObjectError(field: "shape", error: $0) },
      spaceBetweenCentersValue.errorsOrWarnings?.map { .nestedObjectError(field: "space_between_centers", error: $0) },
      tooltipsValue.errorsOrWarnings?.map { .nestedObjectError(field: "tooltips", error: $0) },
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
    let result = DivIndicator(
      accessibility: accessibilityValue.value,
      activeItemColor: activeItemColorValue.value,
      activeItemSize: activeItemSizeValue.value,
      activeShape: activeShapeValue.value,
      alignmentHorizontal: alignmentHorizontalValue.value,
      alignmentVertical: alignmentVerticalValue.value,
      alpha: alphaValue.value,
      animation: animationValue.value,
      background: backgroundValue.value,
      border: borderValue.value,
      columnSpan: columnSpanValue.value,
      disappearActions: disappearActionsValue.value,
      extensions: extensionsValue.value,
      focus: focusValue.value,
      height: heightValue.value,
      id: idValue.value,
      inactiveItemColor: inactiveItemColorValue.value,
      inactiveMinimumShape: inactiveMinimumShapeValue.value,
      inactiveShape: inactiveShapeValue.value,
      itemsPlacement: itemsPlacementValue.value,
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
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivIndicatorTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivIndicator> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var accessibilityValue: DeserializationResult<DivAccessibility> = .noValue
    var activeItemColorValue: DeserializationResult<Expression<Color>> = parent?.activeItemColor?.value() ?? .noValue
    var activeItemSizeValue: DeserializationResult<Expression<Double>> = parent?.activeItemSize?.value() ?? .noValue
    var activeShapeValue: DeserializationResult<DivRoundedRectangleShape> = .noValue
    var alignmentHorizontalValue: DeserializationResult<Expression<DivAlignmentHorizontal>> = parent?.alignmentHorizontal?.value() ?? .noValue
    var alignmentVerticalValue: DeserializationResult<Expression<DivAlignmentVertical>> = parent?.alignmentVertical?.value() ?? .noValue
    var alphaValue: DeserializationResult<Expression<Double>> = parent?.alpha?.value() ?? .noValue
    var animationValue: DeserializationResult<Expression<DivIndicator.Animation>> = parent?.animation?.value() ?? .noValue
    var backgroundValue: DeserializationResult<[DivBackground]> = .noValue
    var borderValue: DeserializationResult<DivBorder> = .noValue
    var columnSpanValue: DeserializationResult<Expression<Int>> = parent?.columnSpan?.value() ?? .noValue
    var disappearActionsValue: DeserializationResult<[DivDisappearAction]> = .noValue
    var extensionsValue: DeserializationResult<[DivExtension]> = .noValue
    var focusValue: DeserializationResult<DivFocus> = .noValue
    var heightValue: DeserializationResult<DivSize> = .noValue
    var idValue: DeserializationResult<String> = parent?.id?.value() ?? .noValue
    var inactiveItemColorValue: DeserializationResult<Expression<Color>> = parent?.inactiveItemColor?.value() ?? .noValue
    var inactiveMinimumShapeValue: DeserializationResult<DivRoundedRectangleShape> = .noValue
    var inactiveShapeValue: DeserializationResult<DivRoundedRectangleShape> = .noValue
    var itemsPlacementValue: DeserializationResult<DivIndicatorItemPlacement> = .noValue
    var marginsValue: DeserializationResult<DivEdgeInsets> = .noValue
    var minimumItemSizeValue: DeserializationResult<Expression<Double>> = parent?.minimumItemSize?.value() ?? .noValue
    var paddingsValue: DeserializationResult<DivEdgeInsets> = .noValue
    var pagerIdValue: DeserializationResult<String> = parent?.pagerId?.value() ?? .noValue
    var rowSpanValue: DeserializationResult<Expression<Int>> = parent?.rowSpan?.value() ?? .noValue
    var selectedActionsValue: DeserializationResult<[DivAction]> = .noValue
    var shapeValue: DeserializationResult<DivShape> = .noValue
    var spaceBetweenCentersValue: DeserializationResult<DivFixedSize> = .noValue
    var tooltipsValue: DeserializationResult<[DivTooltip]> = .noValue
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
        accessibilityValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivAccessibilityTemplate.self).merged(with: accessibilityValue)
      case "active_item_color":
        activeItemColorValue = deserialize(__dictValue, transform: Color.color(withHexString:)).merged(with: activeItemColorValue)
      case "active_item_size":
        activeItemSizeValue = deserialize(__dictValue, validator: ResolvedValue.activeItemSizeValidator).merged(with: activeItemSizeValue)
      case "active_shape":
        activeShapeValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivRoundedRectangleShapeTemplate.self).merged(with: activeShapeValue)
      case "alignment_horizontal":
        alignmentHorizontalValue = deserialize(__dictValue).merged(with: alignmentHorizontalValue)
      case "alignment_vertical":
        alignmentVerticalValue = deserialize(__dictValue).merged(with: alignmentVerticalValue)
      case "alpha":
        alphaValue = deserialize(__dictValue, validator: ResolvedValue.alphaValidator).merged(with: alphaValue)
      case "animation":
        animationValue = deserialize(__dictValue).merged(with: animationValue)
      case "background":
        backgroundValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivBackgroundTemplate.self).merged(with: backgroundValue)
      case "border":
        borderValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivBorderTemplate.self).merged(with: borderValue)
      case "column_span":
        columnSpanValue = deserialize(__dictValue, validator: ResolvedValue.columnSpanValidator).merged(with: columnSpanValue)
      case "disappear_actions":
        disappearActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivDisappearActionTemplate.self).merged(with: disappearActionsValue)
      case "extensions":
        extensionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivExtensionTemplate.self).merged(with: extensionsValue)
      case "focus":
        focusValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivFocusTemplate.self).merged(with: focusValue)
      case "height":
        heightValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivSizeTemplate.self).merged(with: heightValue)
      case "id":
        idValue = deserialize(__dictValue).merged(with: idValue)
      case "inactive_item_color":
        inactiveItemColorValue = deserialize(__dictValue, transform: Color.color(withHexString:)).merged(with: inactiveItemColorValue)
      case "inactive_minimum_shape":
        inactiveMinimumShapeValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivRoundedRectangleShapeTemplate.self).merged(with: inactiveMinimumShapeValue)
      case "inactive_shape":
        inactiveShapeValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivRoundedRectangleShapeTemplate.self).merged(with: inactiveShapeValue)
      case "items_placement":
        itemsPlacementValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivIndicatorItemPlacementTemplate.self).merged(with: itemsPlacementValue)
      case "margins":
        marginsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivEdgeInsetsTemplate.self).merged(with: marginsValue)
      case "minimum_item_size":
        minimumItemSizeValue = deserialize(__dictValue, validator: ResolvedValue.minimumItemSizeValidator).merged(with: minimumItemSizeValue)
      case "paddings":
        paddingsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivEdgeInsetsTemplate.self).merged(with: paddingsValue)
      case "pager_id":
        pagerIdValue = deserialize(__dictValue).merged(with: pagerIdValue)
      case "row_span":
        rowSpanValue = deserialize(__dictValue, validator: ResolvedValue.rowSpanValidator).merged(with: rowSpanValue)
      case "selected_actions":
        selectedActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self).merged(with: selectedActionsValue)
      case "shape":
        shapeValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivShapeTemplate.self).merged(with: shapeValue)
      case "space_between_centers":
        spaceBetweenCentersValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivFixedSizeTemplate.self).merged(with: spaceBetweenCentersValue)
      case "tooltips":
        tooltipsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTooltipTemplate.self).merged(with: tooltipsValue)
      case "transform":
        transformValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTransformTemplate.self).merged(with: transformValue)
      case "transition_change":
        transitionChangeValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivChangeTransitionTemplate.self).merged(with: transitionChangeValue)
      case "transition_in":
        transitionInValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivAppearanceTransitionTemplate.self).merged(with: transitionInValue)
      case "transition_out":
        transitionOutValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivAppearanceTransitionTemplate.self).merged(with: transitionOutValue)
      case "transition_triggers":
        transitionTriggersValue = deserialize(__dictValue, validator: ResolvedValue.transitionTriggersValidator).merged(with: transitionTriggersValue)
      case "visibility":
        visibilityValue = deserialize(__dictValue).merged(with: visibilityValue)
      case "visibility_action":
        visibilityActionValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivVisibilityActionTemplate.self).merged(with: visibilityActionValue)
      case "visibility_actions":
        visibilityActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivVisibilityActionTemplate.self).merged(with: visibilityActionsValue)
      case "width":
        widthValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivSizeTemplate.self).merged(with: widthValue)
      case parent?.accessibility?.link:
        accessibilityValue = accessibilityValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivAccessibilityTemplate.self) })
      case parent?.activeItemColor?.link:
        activeItemColorValue = activeItemColorValue.merged(with: { deserialize(__dictValue, transform: Color.color(withHexString:)) })
      case parent?.activeItemSize?.link:
        activeItemSizeValue = activeItemSizeValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.activeItemSizeValidator) })
      case parent?.activeShape?.link:
        activeShapeValue = activeShapeValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivRoundedRectangleShapeTemplate.self) })
      case parent?.alignmentHorizontal?.link:
        alignmentHorizontalValue = alignmentHorizontalValue.merged(with: { deserialize(__dictValue) })
      case parent?.alignmentVertical?.link:
        alignmentVerticalValue = alignmentVerticalValue.merged(with: { deserialize(__dictValue) })
      case parent?.alpha?.link:
        alphaValue = alphaValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.alphaValidator) })
      case parent?.animation?.link:
        animationValue = animationValue.merged(with: { deserialize(__dictValue) })
      case parent?.background?.link:
        backgroundValue = backgroundValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivBackgroundTemplate.self) })
      case parent?.border?.link:
        borderValue = borderValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivBorderTemplate.self) })
      case parent?.columnSpan?.link:
        columnSpanValue = columnSpanValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.columnSpanValidator) })
      case parent?.disappearActions?.link:
        disappearActionsValue = disappearActionsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivDisappearActionTemplate.self) })
      case parent?.extensions?.link:
        extensionsValue = extensionsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivExtensionTemplate.self) })
      case parent?.focus?.link:
        focusValue = focusValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivFocusTemplate.self) })
      case parent?.height?.link:
        heightValue = heightValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivSizeTemplate.self) })
      case parent?.id?.link:
        idValue = idValue.merged(with: { deserialize(__dictValue) })
      case parent?.inactiveItemColor?.link:
        inactiveItemColorValue = inactiveItemColorValue.merged(with: { deserialize(__dictValue, transform: Color.color(withHexString:)) })
      case parent?.inactiveMinimumShape?.link:
        inactiveMinimumShapeValue = inactiveMinimumShapeValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivRoundedRectangleShapeTemplate.self) })
      case parent?.inactiveShape?.link:
        inactiveShapeValue = inactiveShapeValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivRoundedRectangleShapeTemplate.self) })
      case parent?.itemsPlacement?.link:
        itemsPlacementValue = itemsPlacementValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivIndicatorItemPlacementTemplate.self) })
      case parent?.margins?.link:
        marginsValue = marginsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivEdgeInsetsTemplate.self) })
      case parent?.minimumItemSize?.link:
        minimumItemSizeValue = minimumItemSizeValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.minimumItemSizeValidator) })
      case parent?.paddings?.link:
        paddingsValue = paddingsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivEdgeInsetsTemplate.self) })
      case parent?.pagerId?.link:
        pagerIdValue = pagerIdValue.merged(with: { deserialize(__dictValue) })
      case parent?.rowSpan?.link:
        rowSpanValue = rowSpanValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.rowSpanValidator) })
      case parent?.selectedActions?.link:
        selectedActionsValue = selectedActionsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self) })
      case parent?.shape?.link:
        shapeValue = shapeValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivShapeTemplate.self) })
      case parent?.spaceBetweenCenters?.link:
        spaceBetweenCentersValue = spaceBetweenCentersValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivFixedSizeTemplate.self) })
      case parent?.tooltips?.link:
        tooltipsValue = tooltipsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTooltipTemplate.self) })
      case parent?.transform?.link:
        transformValue = transformValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTransformTemplate.self) })
      case parent?.transitionChange?.link:
        transitionChangeValue = transitionChangeValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivChangeTransitionTemplate.self) })
      case parent?.transitionIn?.link:
        transitionInValue = transitionInValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivAppearanceTransitionTemplate.self) })
      case parent?.transitionOut?.link:
        transitionOutValue = transitionOutValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivAppearanceTransitionTemplate.self) })
      case parent?.transitionTriggers?.link:
        transitionTriggersValue = transitionTriggersValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.transitionTriggersValidator) })
      case parent?.visibility?.link:
        visibilityValue = visibilityValue.merged(with: { deserialize(__dictValue) })
      case parent?.visibilityAction?.link:
        visibilityActionValue = visibilityActionValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivVisibilityActionTemplate.self) })
      case parent?.visibilityActions?.link:
        visibilityActionsValue = visibilityActionsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivVisibilityActionTemplate.self) })
      case parent?.width?.link:
        widthValue = widthValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivSizeTemplate.self) })
      default: break
      }
    }
    if let parent = parent {
      accessibilityValue = accessibilityValue.merged(with: { parent.accessibility?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      activeShapeValue = activeShapeValue.merged(with: { parent.activeShape?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      backgroundValue = backgroundValue.merged(with: { parent.background?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      borderValue = borderValue.merged(with: { parent.border?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      disappearActionsValue = disappearActionsValue.merged(with: { parent.disappearActions?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      extensionsValue = extensionsValue.merged(with: { parent.extensions?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      focusValue = focusValue.merged(with: { parent.focus?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      heightValue = heightValue.merged(with: { parent.height?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      inactiveMinimumShapeValue = inactiveMinimumShapeValue.merged(with: { parent.inactiveMinimumShape?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      inactiveShapeValue = inactiveShapeValue.merged(with: { parent.inactiveShape?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      itemsPlacementValue = itemsPlacementValue.merged(with: { parent.itemsPlacement?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      marginsValue = marginsValue.merged(with: { parent.margins?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      paddingsValue = paddingsValue.merged(with: { parent.paddings?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      selectedActionsValue = selectedActionsValue.merged(with: { parent.selectedActions?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      shapeValue = shapeValue.merged(with: { parent.shape?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      spaceBetweenCentersValue = spaceBetweenCentersValue.merged(with: { parent.spaceBetweenCenters?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      tooltipsValue = tooltipsValue.merged(with: { parent.tooltips?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      transformValue = transformValue.merged(with: { parent.transform?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      transitionChangeValue = transitionChangeValue.merged(with: { parent.transitionChange?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      transitionInValue = transitionInValue.merged(with: { parent.transitionIn?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      transitionOutValue = transitionOutValue.merged(with: { parent.transitionOut?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      visibilityActionValue = visibilityActionValue.merged(with: { parent.visibilityAction?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      visibilityActionsValue = visibilityActionsValue.merged(with: { parent.visibilityActions?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      widthValue = widthValue.merged(with: { parent.width?.resolveOptionalValue(context: context, useOnlyLinks: true) })
    }
    let errors = mergeErrors(
      accessibilityValue.errorsOrWarnings?.map { .nestedObjectError(field: "accessibility", error: $0) },
      activeItemColorValue.errorsOrWarnings?.map { .nestedObjectError(field: "active_item_color", error: $0) },
      activeItemSizeValue.errorsOrWarnings?.map { .nestedObjectError(field: "active_item_size", error: $0) },
      activeShapeValue.errorsOrWarnings?.map { .nestedObjectError(field: "active_shape", error: $0) },
      alignmentHorizontalValue.errorsOrWarnings?.map { .nestedObjectError(field: "alignment_horizontal", error: $0) },
      alignmentVerticalValue.errorsOrWarnings?.map { .nestedObjectError(field: "alignment_vertical", error: $0) },
      alphaValue.errorsOrWarnings?.map { .nestedObjectError(field: "alpha", error: $0) },
      animationValue.errorsOrWarnings?.map { .nestedObjectError(field: "animation", error: $0) },
      backgroundValue.errorsOrWarnings?.map { .nestedObjectError(field: "background", error: $0) },
      borderValue.errorsOrWarnings?.map { .nestedObjectError(field: "border", error: $0) },
      columnSpanValue.errorsOrWarnings?.map { .nestedObjectError(field: "column_span", error: $0) },
      disappearActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "disappear_actions", error: $0) },
      extensionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "extensions", error: $0) },
      focusValue.errorsOrWarnings?.map { .nestedObjectError(field: "focus", error: $0) },
      heightValue.errorsOrWarnings?.map { .nestedObjectError(field: "height", error: $0) },
      idValue.errorsOrWarnings?.map { .nestedObjectError(field: "id", error: $0) },
      inactiveItemColorValue.errorsOrWarnings?.map { .nestedObjectError(field: "inactive_item_color", error: $0) },
      inactiveMinimumShapeValue.errorsOrWarnings?.map { .nestedObjectError(field: "inactive_minimum_shape", error: $0) },
      inactiveShapeValue.errorsOrWarnings?.map { .nestedObjectError(field: "inactive_shape", error: $0) },
      itemsPlacementValue.errorsOrWarnings?.map { .nestedObjectError(field: "items_placement", error: $0) },
      marginsValue.errorsOrWarnings?.map { .nestedObjectError(field: "margins", error: $0) },
      minimumItemSizeValue.errorsOrWarnings?.map { .nestedObjectError(field: "minimum_item_size", error: $0) },
      paddingsValue.errorsOrWarnings?.map { .nestedObjectError(field: "paddings", error: $0) },
      pagerIdValue.errorsOrWarnings?.map { .nestedObjectError(field: "pager_id", error: $0) },
      rowSpanValue.errorsOrWarnings?.map { .nestedObjectError(field: "row_span", error: $0) },
      selectedActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "selected_actions", error: $0) },
      shapeValue.errorsOrWarnings?.map { .nestedObjectError(field: "shape", error: $0) },
      spaceBetweenCentersValue.errorsOrWarnings?.map { .nestedObjectError(field: "space_between_centers", error: $0) },
      tooltipsValue.errorsOrWarnings?.map { .nestedObjectError(field: "tooltips", error: $0) },
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
    let result = DivIndicator(
      accessibility: accessibilityValue.value,
      activeItemColor: activeItemColorValue.value,
      activeItemSize: activeItemSizeValue.value,
      activeShape: activeShapeValue.value,
      alignmentHorizontal: alignmentHorizontalValue.value,
      alignmentVertical: alignmentVerticalValue.value,
      alpha: alphaValue.value,
      animation: animationValue.value,
      background: backgroundValue.value,
      border: borderValue.value,
      columnSpan: columnSpanValue.value,
      disappearActions: disappearActionsValue.value,
      extensions: extensionsValue.value,
      focus: focusValue.value,
      height: heightValue.value,
      id: idValue.value,
      inactiveItemColor: inactiveItemColorValue.value,
      inactiveMinimumShape: inactiveMinimumShapeValue.value,
      inactiveShape: inactiveShapeValue.value,
      itemsPlacement: itemsPlacementValue.value,
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
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivIndicatorTemplate {
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
      activeShape: activeShape ?? mergedParent.activeShape,
      alignmentHorizontal: alignmentHorizontal ?? mergedParent.alignmentHorizontal,
      alignmentVertical: alignmentVertical ?? mergedParent.alignmentVertical,
      alpha: alpha ?? mergedParent.alpha,
      animation: animation ?? mergedParent.animation,
      background: background ?? mergedParent.background,
      border: border ?? mergedParent.border,
      columnSpan: columnSpan ?? mergedParent.columnSpan,
      disappearActions: disappearActions ?? mergedParent.disappearActions,
      extensions: extensions ?? mergedParent.extensions,
      focus: focus ?? mergedParent.focus,
      height: height ?? mergedParent.height,
      id: id ?? mergedParent.id,
      inactiveItemColor: inactiveItemColor ?? mergedParent.inactiveItemColor,
      inactiveMinimumShape: inactiveMinimumShape ?? mergedParent.inactiveMinimumShape,
      inactiveShape: inactiveShape ?? mergedParent.inactiveShape,
      itemsPlacement: itemsPlacement ?? mergedParent.itemsPlacement,
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

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivIndicatorTemplate {
    let merged = try mergedWithParent(templates: templates)

    return DivIndicatorTemplate(
      parent: nil,
      accessibility: merged.accessibility?.tryResolveParent(templates: templates),
      activeItemColor: merged.activeItemColor,
      activeItemSize: merged.activeItemSize,
      activeShape: merged.activeShape?.tryResolveParent(templates: templates),
      alignmentHorizontal: merged.alignmentHorizontal,
      alignmentVertical: merged.alignmentVertical,
      alpha: merged.alpha,
      animation: merged.animation,
      background: merged.background?.tryResolveParent(templates: templates),
      border: merged.border?.tryResolveParent(templates: templates),
      columnSpan: merged.columnSpan,
      disappearActions: merged.disappearActions?.tryResolveParent(templates: templates),
      extensions: merged.extensions?.tryResolveParent(templates: templates),
      focus: merged.focus?.tryResolveParent(templates: templates),
      height: merged.height?.tryResolveParent(templates: templates),
      id: merged.id,
      inactiveItemColor: merged.inactiveItemColor,
      inactiveMinimumShape: merged.inactiveMinimumShape?.tryResolveParent(templates: templates),
      inactiveShape: merged.inactiveShape?.tryResolveParent(templates: templates),
      itemsPlacement: merged.itemsPlacement?.tryResolveParent(templates: templates),
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
