// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivImageTemplate: TemplateValue, Sendable {
  public static let type: String = "image"
  public let parent: String?
  public let accessibility: Field<DivAccessibilityTemplate>?
  public let action: Field<DivActionTemplate>?
  public let actionAnimation: Field<DivAnimationTemplate>? // default value: DivAnimation(duration: .value(100), endValue: .value(0.6), name: .value(.fade), startValue: .value(1))
  public let actions: Field<[DivActionTemplate]>?
  public let alignmentHorizontal: Field<Expression<DivAlignmentHorizontal>>?
  public let alignmentVertical: Field<Expression<DivAlignmentVertical>>?
  public let alpha: Field<Expression<Double>>? // constraint: number >= 0.0 && number <= 1.0; default value: 1.0
  public let animators: Field<[DivAnimatorTemplate]>?
  public let appearanceAnimation: Field<DivFadeTransitionTemplate>?
  public let aspect: Field<DivAspectTemplate>?
  public let background: Field<[DivBackgroundTemplate]>?
  public let border: Field<DivBorderTemplate>?
  public let captureFocusOnAction: Field<Expression<Bool>>? // default value: true
  public let columnSpan: Field<Expression<Int>>? // constraint: number >= 0
  public let contentAlignmentHorizontal: Field<Expression<DivAlignmentHorizontal>>? // default value: center
  public let contentAlignmentVertical: Field<Expression<DivAlignmentVertical>>? // default value: center
  public let disappearActions: Field<[DivDisappearActionTemplate]>?
  public let doubletapActions: Field<[DivActionTemplate]>?
  public let extensions: Field<[DivExtensionTemplate]>?
  public let filters: Field<[DivFilterTemplate]>?
  public let focus: Field<DivFocusTemplate>?
  public let functions: Field<[DivFunctionTemplate]>?
  public let height: Field<DivSizeTemplate>? // default value: .divWrapContentSize(DivWrapContentSize())
  public let highPriorityPreviewShow: Field<Expression<Bool>>? // default value: false
  public let hoverEndActions: Field<[DivActionTemplate]>?
  public let hoverStartActions: Field<[DivActionTemplate]>?
  public let id: Field<String>?
  public let imageUrl: Field<Expression<URL>>?
  public let layoutProvider: Field<DivLayoutProviderTemplate>?
  public let longtapActions: Field<[DivActionTemplate]>?
  public let margins: Field<DivEdgeInsetsTemplate>?
  public let paddings: Field<DivEdgeInsetsTemplate>?
  public let placeholderColor: Field<Expression<Color>>? // default value: #14000000
  public let preloadRequired: Field<Expression<Bool>>? // default value: false
  public let pressEndActions: Field<[DivActionTemplate]>?
  public let pressStartActions: Field<[DivActionTemplate]>?
  public let preview: Field<Expression<String>>?
  public let reuseId: Field<Expression<String>>?
  public let rowSpan: Field<Expression<Int>>? // constraint: number >= 0
  public let scale: Field<Expression<DivImageScale>>? // default value: fill
  public let selectedActions: Field<[DivActionTemplate]>?
  public let tintColor: Field<Expression<Color>>?
  public let tintMode: Field<Expression<DivBlendMode>>? // default value: source_in
  public let tooltips: Field<[DivTooltipTemplate]>?
  public let transform: Field<DivTransformTemplate>?
  public let transformations: Field<[DivTransformationTemplate]>?
  public let transitionChange: Field<DivChangeTransitionTemplate>?
  public let transitionIn: Field<DivAppearanceTransitionTemplate>?
  public let transitionOut: Field<DivAppearanceTransitionTemplate>?
  public let transitionTriggers: Field<[DivTransitionTrigger]>? // at least 1 elements
  public let variableTriggers: Field<[DivTriggerTemplate]>?
  public let variables: Field<[DivVariableTemplate]>?
  public let visibility: Field<Expression<DivVisibility>>? // default value: visible
  public let visibilityAction: Field<DivVisibilityActionTemplate>?
  public let visibilityActions: Field<[DivVisibilityActionTemplate]>?
  public let width: Field<DivSizeTemplate>? // default value: .divMatchParentSize(DivMatchParentSize())

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      parent: dictionary["type"] as? String,
      accessibility: dictionary.getOptionalField("accessibility", templateToType: templateToType),
      action: dictionary.getOptionalField("action", templateToType: templateToType),
      actionAnimation: dictionary.getOptionalField("action_animation", templateToType: templateToType),
      actions: dictionary.getOptionalArray("actions", templateToType: templateToType),
      alignmentHorizontal: dictionary.getOptionalExpressionField("alignment_horizontal"),
      alignmentVertical: dictionary.getOptionalExpressionField("alignment_vertical"),
      alpha: dictionary.getOptionalExpressionField("alpha"),
      animators: dictionary.getOptionalArray("animators", templateToType: templateToType),
      appearanceAnimation: dictionary.getOptionalField("appearance_animation", templateToType: templateToType),
      aspect: dictionary.getOptionalField("aspect", templateToType: templateToType),
      background: dictionary.getOptionalArray("background", templateToType: templateToType),
      border: dictionary.getOptionalField("border", templateToType: templateToType),
      captureFocusOnAction: dictionary.getOptionalExpressionField("capture_focus_on_action"),
      columnSpan: dictionary.getOptionalExpressionField("column_span"),
      contentAlignmentHorizontal: dictionary.getOptionalExpressionField("content_alignment_horizontal"),
      contentAlignmentVertical: dictionary.getOptionalExpressionField("content_alignment_vertical"),
      disappearActions: dictionary.getOptionalArray("disappear_actions", templateToType: templateToType),
      doubletapActions: dictionary.getOptionalArray("doubletap_actions", templateToType: templateToType),
      extensions: dictionary.getOptionalArray("extensions", templateToType: templateToType),
      filters: dictionary.getOptionalArray("filters", templateToType: templateToType),
      focus: dictionary.getOptionalField("focus", templateToType: templateToType),
      functions: dictionary.getOptionalArray("functions", templateToType: templateToType),
      height: dictionary.getOptionalField("height", templateToType: templateToType),
      highPriorityPreviewShow: dictionary.getOptionalExpressionField("high_priority_preview_show"),
      hoverEndActions: dictionary.getOptionalArray("hover_end_actions", templateToType: templateToType),
      hoverStartActions: dictionary.getOptionalArray("hover_start_actions", templateToType: templateToType),
      id: dictionary.getOptionalField("id"),
      imageUrl: dictionary.getOptionalExpressionField("image_url", transform: URL.makeFromNonEncodedString),
      layoutProvider: dictionary.getOptionalField("layout_provider", templateToType: templateToType),
      longtapActions: dictionary.getOptionalArray("longtap_actions", templateToType: templateToType),
      margins: dictionary.getOptionalField("margins", templateToType: templateToType),
      paddings: dictionary.getOptionalField("paddings", templateToType: templateToType),
      placeholderColor: dictionary.getOptionalExpressionField("placeholder_color", transform: Color.color(withHexString:)),
      preloadRequired: dictionary.getOptionalExpressionField("preload_required"),
      pressEndActions: dictionary.getOptionalArray("press_end_actions", templateToType: templateToType),
      pressStartActions: dictionary.getOptionalArray("press_start_actions", templateToType: templateToType),
      preview: dictionary.getOptionalExpressionField("preview"),
      reuseId: dictionary.getOptionalExpressionField("reuse_id"),
      rowSpan: dictionary.getOptionalExpressionField("row_span"),
      scale: dictionary.getOptionalExpressionField("scale"),
      selectedActions: dictionary.getOptionalArray("selected_actions", templateToType: templateToType),
      tintColor: dictionary.getOptionalExpressionField("tint_color", transform: Color.color(withHexString:)),
      tintMode: dictionary.getOptionalExpressionField("tint_mode"),
      tooltips: dictionary.getOptionalArray("tooltips", templateToType: templateToType),
      transform: dictionary.getOptionalField("transform", templateToType: templateToType),
      transformations: dictionary.getOptionalArray("transformations", templateToType: templateToType),
      transitionChange: dictionary.getOptionalField("transition_change", templateToType: templateToType),
      transitionIn: dictionary.getOptionalField("transition_in", templateToType: templateToType),
      transitionOut: dictionary.getOptionalField("transition_out", templateToType: templateToType),
      transitionTriggers: dictionary.getOptionalArray("transition_triggers"),
      variableTriggers: dictionary.getOptionalArray("variable_triggers", templateToType: templateToType),
      variables: dictionary.getOptionalArray("variables", templateToType: templateToType),
      visibility: dictionary.getOptionalExpressionField("visibility"),
      visibilityAction: dictionary.getOptionalField("visibility_action", templateToType: templateToType),
      visibilityActions: dictionary.getOptionalArray("visibility_actions", templateToType: templateToType),
      width: dictionary.getOptionalField("width", templateToType: templateToType)
    )
  }

  init(
    parent: String?,
    accessibility: Field<DivAccessibilityTemplate>? = nil,
    action: Field<DivActionTemplate>? = nil,
    actionAnimation: Field<DivAnimationTemplate>? = nil,
    actions: Field<[DivActionTemplate]>? = nil,
    alignmentHorizontal: Field<Expression<DivAlignmentHorizontal>>? = nil,
    alignmentVertical: Field<Expression<DivAlignmentVertical>>? = nil,
    alpha: Field<Expression<Double>>? = nil,
    animators: Field<[DivAnimatorTemplate]>? = nil,
    appearanceAnimation: Field<DivFadeTransitionTemplate>? = nil,
    aspect: Field<DivAspectTemplate>? = nil,
    background: Field<[DivBackgroundTemplate]>? = nil,
    border: Field<DivBorderTemplate>? = nil,
    captureFocusOnAction: Field<Expression<Bool>>? = nil,
    columnSpan: Field<Expression<Int>>? = nil,
    contentAlignmentHorizontal: Field<Expression<DivAlignmentHorizontal>>? = nil,
    contentAlignmentVertical: Field<Expression<DivAlignmentVertical>>? = nil,
    disappearActions: Field<[DivDisappearActionTemplate]>? = nil,
    doubletapActions: Field<[DivActionTemplate]>? = nil,
    extensions: Field<[DivExtensionTemplate]>? = nil,
    filters: Field<[DivFilterTemplate]>? = nil,
    focus: Field<DivFocusTemplate>? = nil,
    functions: Field<[DivFunctionTemplate]>? = nil,
    height: Field<DivSizeTemplate>? = nil,
    highPriorityPreviewShow: Field<Expression<Bool>>? = nil,
    hoverEndActions: Field<[DivActionTemplate]>? = nil,
    hoverStartActions: Field<[DivActionTemplate]>? = nil,
    id: Field<String>? = nil,
    imageUrl: Field<Expression<URL>>? = nil,
    layoutProvider: Field<DivLayoutProviderTemplate>? = nil,
    longtapActions: Field<[DivActionTemplate]>? = nil,
    margins: Field<DivEdgeInsetsTemplate>? = nil,
    paddings: Field<DivEdgeInsetsTemplate>? = nil,
    placeholderColor: Field<Expression<Color>>? = nil,
    preloadRequired: Field<Expression<Bool>>? = nil,
    pressEndActions: Field<[DivActionTemplate]>? = nil,
    pressStartActions: Field<[DivActionTemplate]>? = nil,
    preview: Field<Expression<String>>? = nil,
    reuseId: Field<Expression<String>>? = nil,
    rowSpan: Field<Expression<Int>>? = nil,
    scale: Field<Expression<DivImageScale>>? = nil,
    selectedActions: Field<[DivActionTemplate]>? = nil,
    tintColor: Field<Expression<Color>>? = nil,
    tintMode: Field<Expression<DivBlendMode>>? = nil,
    tooltips: Field<[DivTooltipTemplate]>? = nil,
    transform: Field<DivTransformTemplate>? = nil,
    transformations: Field<[DivTransformationTemplate]>? = nil,
    transitionChange: Field<DivChangeTransitionTemplate>? = nil,
    transitionIn: Field<DivAppearanceTransitionTemplate>? = nil,
    transitionOut: Field<DivAppearanceTransitionTemplate>? = nil,
    transitionTriggers: Field<[DivTransitionTrigger]>? = nil,
    variableTriggers: Field<[DivTriggerTemplate]>? = nil,
    variables: Field<[DivVariableTemplate]>? = nil,
    visibility: Field<Expression<DivVisibility>>? = nil,
    visibilityAction: Field<DivVisibilityActionTemplate>? = nil,
    visibilityActions: Field<[DivVisibilityActionTemplate]>? = nil,
    width: Field<DivSizeTemplate>? = nil
  ) {
    self.parent = parent
    self.accessibility = accessibility
    self.action = action
    self.actionAnimation = actionAnimation
    self.actions = actions
    self.alignmentHorizontal = alignmentHorizontal
    self.alignmentVertical = alignmentVertical
    self.alpha = alpha
    self.animators = animators
    self.appearanceAnimation = appearanceAnimation
    self.aspect = aspect
    self.background = background
    self.border = border
    self.captureFocusOnAction = captureFocusOnAction
    self.columnSpan = columnSpan
    self.contentAlignmentHorizontal = contentAlignmentHorizontal
    self.contentAlignmentVertical = contentAlignmentVertical
    self.disappearActions = disappearActions
    self.doubletapActions = doubletapActions
    self.extensions = extensions
    self.filters = filters
    self.focus = focus
    self.functions = functions
    self.height = height
    self.highPriorityPreviewShow = highPriorityPreviewShow
    self.hoverEndActions = hoverEndActions
    self.hoverStartActions = hoverStartActions
    self.id = id
    self.imageUrl = imageUrl
    self.layoutProvider = layoutProvider
    self.longtapActions = longtapActions
    self.margins = margins
    self.paddings = paddings
    self.placeholderColor = placeholderColor
    self.preloadRequired = preloadRequired
    self.pressEndActions = pressEndActions
    self.pressStartActions = pressStartActions
    self.preview = preview
    self.reuseId = reuseId
    self.rowSpan = rowSpan
    self.scale = scale
    self.selectedActions = selectedActions
    self.tintColor = tintColor
    self.tintMode = tintMode
    self.tooltips = tooltips
    self.transform = transform
    self.transformations = transformations
    self.transitionChange = transitionChange
    self.transitionIn = transitionIn
    self.transitionOut = transitionOut
    self.transitionTriggers = transitionTriggers
    self.variableTriggers = variableTriggers
    self.variables = variables
    self.visibility = visibility
    self.visibilityAction = visibilityAction
    self.visibilityActions = visibilityActions
    self.width = width
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivImageTemplate?) -> DeserializationResult<DivImage> {
    let accessibilityValue = { parent?.accessibility?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let actionValue = { parent?.action?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let actionAnimationValue = { parent?.actionAnimation?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let actionsValue = { parent?.actions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let alignmentHorizontalValue = { parent?.alignmentHorizontal?.resolveOptionalValue(context: context) ?? .noValue }()
    let alignmentVerticalValue = { parent?.alignmentVertical?.resolveOptionalValue(context: context) ?? .noValue }()
    let alphaValue = { parent?.alpha?.resolveOptionalValue(context: context, validator: ResolvedValue.alphaValidator) ?? .noValue }()
    let animatorsValue = { parent?.animators?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let appearanceAnimationValue = { parent?.appearanceAnimation?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let aspectValue = { parent?.aspect?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let backgroundValue = { parent?.background?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let borderValue = { parent?.border?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let captureFocusOnActionValue = { parent?.captureFocusOnAction?.resolveOptionalValue(context: context) ?? .noValue }()
    let columnSpanValue = { parent?.columnSpan?.resolveOptionalValue(context: context, validator: ResolvedValue.columnSpanValidator) ?? .noValue }()
    let contentAlignmentHorizontalValue = { parent?.contentAlignmentHorizontal?.resolveOptionalValue(context: context) ?? .noValue }()
    let contentAlignmentVerticalValue = { parent?.contentAlignmentVertical?.resolveOptionalValue(context: context) ?? .noValue }()
    let disappearActionsValue = { parent?.disappearActions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let doubletapActionsValue = { parent?.doubletapActions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let extensionsValue = { parent?.extensions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let filtersValue = { parent?.filters?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let focusValue = { parent?.focus?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let functionsValue = { parent?.functions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let heightValue = { parent?.height?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let highPriorityPreviewShowValue = { parent?.highPriorityPreviewShow?.resolveOptionalValue(context: context) ?? .noValue }()
    let hoverEndActionsValue = { parent?.hoverEndActions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let hoverStartActionsValue = { parent?.hoverStartActions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let idValue = { parent?.id?.resolveOptionalValue(context: context) ?? .noValue }()
    let imageUrlValue = { parent?.imageUrl?.resolveOptionalValue(context: context, transform: URL.makeFromNonEncodedString) ?? .noValue }()
    let layoutProviderValue = { parent?.layoutProvider?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let longtapActionsValue = { parent?.longtapActions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let marginsValue = { parent?.margins?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let paddingsValue = { parent?.paddings?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let placeholderColorValue = { parent?.placeholderColor?.resolveOptionalValue(context: context, transform: Color.color(withHexString:)) ?? .noValue }()
    let preloadRequiredValue = { parent?.preloadRequired?.resolveOptionalValue(context: context) ?? .noValue }()
    let pressEndActionsValue = { parent?.pressEndActions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let pressStartActionsValue = { parent?.pressStartActions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let previewValue = { parent?.preview?.resolveOptionalValue(context: context) ?? .noValue }()
    let reuseIdValue = { parent?.reuseId?.resolveOptionalValue(context: context) ?? .noValue }()
    let rowSpanValue = { parent?.rowSpan?.resolveOptionalValue(context: context, validator: ResolvedValue.rowSpanValidator) ?? .noValue }()
    let scaleValue = { parent?.scale?.resolveOptionalValue(context: context) ?? .noValue }()
    let selectedActionsValue = { parent?.selectedActions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let tintColorValue = { parent?.tintColor?.resolveOptionalValue(context: context, transform: Color.color(withHexString:)) ?? .noValue }()
    let tintModeValue = { parent?.tintMode?.resolveOptionalValue(context: context) ?? .noValue }()
    let tooltipsValue = { parent?.tooltips?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let transformValue = { parent?.transform?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let transformationsValue = { parent?.transformations?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let transitionChangeValue = { parent?.transitionChange?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let transitionInValue = { parent?.transitionIn?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let transitionOutValue = { parent?.transitionOut?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let transitionTriggersValue = { parent?.transitionTriggers?.resolveOptionalValue(context: context, validator: ResolvedValue.transitionTriggersValidator) ?? .noValue }()
    let variableTriggersValue = { parent?.variableTriggers?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let variablesValue = { parent?.variables?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let visibilityValue = { parent?.visibility?.resolveOptionalValue(context: context) ?? .noValue }()
    let visibilityActionValue = { parent?.visibilityAction?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let visibilityActionsValue = { parent?.visibilityActions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let widthValue = { parent?.width?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let errors = mergeErrors(
      accessibilityValue.errorsOrWarnings?.map { .nestedObjectError(field: "accessibility", error: $0) },
      actionValue.errorsOrWarnings?.map { .nestedObjectError(field: "action", error: $0) },
      actionAnimationValue.errorsOrWarnings?.map { .nestedObjectError(field: "action_animation", error: $0) },
      actionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "actions", error: $0) },
      alignmentHorizontalValue.errorsOrWarnings?.map { .nestedObjectError(field: "alignment_horizontal", error: $0) },
      alignmentVerticalValue.errorsOrWarnings?.map { .nestedObjectError(field: "alignment_vertical", error: $0) },
      alphaValue.errorsOrWarnings?.map { .nestedObjectError(field: "alpha", error: $0) },
      animatorsValue.errorsOrWarnings?.map { .nestedObjectError(field: "animators", error: $0) },
      appearanceAnimationValue.errorsOrWarnings?.map { .nestedObjectError(field: "appearance_animation", error: $0) },
      aspectValue.errorsOrWarnings?.map { .nestedObjectError(field: "aspect", error: $0) },
      backgroundValue.errorsOrWarnings?.map { .nestedObjectError(field: "background", error: $0) },
      borderValue.errorsOrWarnings?.map { .nestedObjectError(field: "border", error: $0) },
      captureFocusOnActionValue.errorsOrWarnings?.map { .nestedObjectError(field: "capture_focus_on_action", error: $0) },
      columnSpanValue.errorsOrWarnings?.map { .nestedObjectError(field: "column_span", error: $0) },
      contentAlignmentHorizontalValue.errorsOrWarnings?.map { .nestedObjectError(field: "content_alignment_horizontal", error: $0) },
      contentAlignmentVerticalValue.errorsOrWarnings?.map { .nestedObjectError(field: "content_alignment_vertical", error: $0) },
      disappearActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "disappear_actions", error: $0) },
      doubletapActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "doubletap_actions", error: $0) },
      extensionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "extensions", error: $0) },
      filtersValue.errorsOrWarnings?.map { .nestedObjectError(field: "filters", error: $0) },
      focusValue.errorsOrWarnings?.map { .nestedObjectError(field: "focus", error: $0) },
      functionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "functions", error: $0) },
      heightValue.errorsOrWarnings?.map { .nestedObjectError(field: "height", error: $0) },
      highPriorityPreviewShowValue.errorsOrWarnings?.map { .nestedObjectError(field: "high_priority_preview_show", error: $0) },
      hoverEndActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "hover_end_actions", error: $0) },
      hoverStartActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "hover_start_actions", error: $0) },
      idValue.errorsOrWarnings?.map { .nestedObjectError(field: "id", error: $0) },
      imageUrlValue.errorsOrWarnings?.map { .nestedObjectError(field: "image_url", error: $0) },
      layoutProviderValue.errorsOrWarnings?.map { .nestedObjectError(field: "layout_provider", error: $0) },
      longtapActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "longtap_actions", error: $0) },
      marginsValue.errorsOrWarnings?.map { .nestedObjectError(field: "margins", error: $0) },
      paddingsValue.errorsOrWarnings?.map { .nestedObjectError(field: "paddings", error: $0) },
      placeholderColorValue.errorsOrWarnings?.map { .nestedObjectError(field: "placeholder_color", error: $0) },
      preloadRequiredValue.errorsOrWarnings?.map { .nestedObjectError(field: "preload_required", error: $0) },
      pressEndActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "press_end_actions", error: $0) },
      pressStartActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "press_start_actions", error: $0) },
      previewValue.errorsOrWarnings?.map { .nestedObjectError(field: "preview", error: $0) },
      reuseIdValue.errorsOrWarnings?.map { .nestedObjectError(field: "reuse_id", error: $0) },
      rowSpanValue.errorsOrWarnings?.map { .nestedObjectError(field: "row_span", error: $0) },
      scaleValue.errorsOrWarnings?.map { .nestedObjectError(field: "scale", error: $0) },
      selectedActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "selected_actions", error: $0) },
      tintColorValue.errorsOrWarnings?.map { .nestedObjectError(field: "tint_color", error: $0) },
      tintModeValue.errorsOrWarnings?.map { .nestedObjectError(field: "tint_mode", error: $0) },
      tooltipsValue.errorsOrWarnings?.map { .nestedObjectError(field: "tooltips", error: $0) },
      transformValue.errorsOrWarnings?.map { .nestedObjectError(field: "transform", error: $0) },
      transformationsValue.errorsOrWarnings?.map { .nestedObjectError(field: "transformations", error: $0) },
      transitionChangeValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_change", error: $0) },
      transitionInValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_in", error: $0) },
      transitionOutValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_out", error: $0) },
      transitionTriggersValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_triggers", error: $0) },
      variableTriggersValue.errorsOrWarnings?.map { .nestedObjectError(field: "variable_triggers", error: $0) },
      variablesValue.errorsOrWarnings?.map { .nestedObjectError(field: "variables", error: $0) },
      visibilityValue.errorsOrWarnings?.map { .nestedObjectError(field: "visibility", error: $0) },
      visibilityActionValue.errorsOrWarnings?.map { .nestedObjectError(field: "visibility_action", error: $0) },
      visibilityActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "visibility_actions", error: $0) },
      widthValue.errorsOrWarnings?.map { .nestedObjectError(field: "width", error: $0) }
    )
    let result = DivImage(
      accessibility: { accessibilityValue.value }(),
      action: { actionValue.value }(),
      actionAnimation: { actionAnimationValue.value }(),
      actions: { actionsValue.value }(),
      alignmentHorizontal: { alignmentHorizontalValue.value }(),
      alignmentVertical: { alignmentVerticalValue.value }(),
      alpha: { alphaValue.value }(),
      animators: { animatorsValue.value }(),
      appearanceAnimation: { appearanceAnimationValue.value }(),
      aspect: { aspectValue.value }(),
      background: { backgroundValue.value }(),
      border: { borderValue.value }(),
      captureFocusOnAction: { captureFocusOnActionValue.value }(),
      columnSpan: { columnSpanValue.value }(),
      contentAlignmentHorizontal: { contentAlignmentHorizontalValue.value }(),
      contentAlignmentVertical: { contentAlignmentVerticalValue.value }(),
      disappearActions: { disappearActionsValue.value }(),
      doubletapActions: { doubletapActionsValue.value }(),
      extensions: { extensionsValue.value }(),
      filters: { filtersValue.value }(),
      focus: { focusValue.value }(),
      functions: { functionsValue.value }(),
      height: { heightValue.value }(),
      highPriorityPreviewShow: { highPriorityPreviewShowValue.value }(),
      hoverEndActions: { hoverEndActionsValue.value }(),
      hoverStartActions: { hoverStartActionsValue.value }(),
      id: { idValue.value }(),
      imageUrl: { imageUrlValue.value }(),
      layoutProvider: { layoutProviderValue.value }(),
      longtapActions: { longtapActionsValue.value }(),
      margins: { marginsValue.value }(),
      paddings: { paddingsValue.value }(),
      placeholderColor: { placeholderColorValue.value }(),
      preloadRequired: { preloadRequiredValue.value }(),
      pressEndActions: { pressEndActionsValue.value }(),
      pressStartActions: { pressStartActionsValue.value }(),
      preview: { previewValue.value }(),
      reuseId: { reuseIdValue.value }(),
      rowSpan: { rowSpanValue.value }(),
      scale: { scaleValue.value }(),
      selectedActions: { selectedActionsValue.value }(),
      tintColor: { tintColorValue.value }(),
      tintMode: { tintModeValue.value }(),
      tooltips: { tooltipsValue.value }(),
      transform: { transformValue.value }(),
      transformations: { transformationsValue.value }(),
      transitionChange: { transitionChangeValue.value }(),
      transitionIn: { transitionInValue.value }(),
      transitionOut: { transitionOutValue.value }(),
      transitionTriggers: { transitionTriggersValue.value }(),
      variableTriggers: { variableTriggersValue.value }(),
      variables: { variablesValue.value }(),
      visibility: { visibilityValue.value }(),
      visibilityAction: { visibilityActionValue.value }(),
      visibilityActions: { visibilityActionsValue.value }(),
      width: { widthValue.value }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivImageTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivImage> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var accessibilityValue: DeserializationResult<DivAccessibility> = .noValue
    var actionValue: DeserializationResult<DivAction> = .noValue
    var actionAnimationValue: DeserializationResult<DivAnimation> = .noValue
    var actionsValue: DeserializationResult<[DivAction]> = .noValue
    var alignmentHorizontalValue: DeserializationResult<Expression<DivAlignmentHorizontal>> = { parent?.alignmentHorizontal?.value() ?? .noValue }()
    var alignmentVerticalValue: DeserializationResult<Expression<DivAlignmentVertical>> = { parent?.alignmentVertical?.value() ?? .noValue }()
    var alphaValue: DeserializationResult<Expression<Double>> = { parent?.alpha?.value() ?? .noValue }()
    var animatorsValue: DeserializationResult<[DivAnimator]> = .noValue
    var appearanceAnimationValue: DeserializationResult<DivFadeTransition> = .noValue
    var aspectValue: DeserializationResult<DivAspect> = .noValue
    var backgroundValue: DeserializationResult<[DivBackground]> = .noValue
    var borderValue: DeserializationResult<DivBorder> = .noValue
    var captureFocusOnActionValue: DeserializationResult<Expression<Bool>> = { parent?.captureFocusOnAction?.value() ?? .noValue }()
    var columnSpanValue: DeserializationResult<Expression<Int>> = { parent?.columnSpan?.value() ?? .noValue }()
    var contentAlignmentHorizontalValue: DeserializationResult<Expression<DivAlignmentHorizontal>> = { parent?.contentAlignmentHorizontal?.value() ?? .noValue }()
    var contentAlignmentVerticalValue: DeserializationResult<Expression<DivAlignmentVertical>> = { parent?.contentAlignmentVertical?.value() ?? .noValue }()
    var disappearActionsValue: DeserializationResult<[DivDisappearAction]> = .noValue
    var doubletapActionsValue: DeserializationResult<[DivAction]> = .noValue
    var extensionsValue: DeserializationResult<[DivExtension]> = .noValue
    var filtersValue: DeserializationResult<[DivFilter]> = .noValue
    var focusValue: DeserializationResult<DivFocus> = .noValue
    var functionsValue: DeserializationResult<[DivFunction]> = .noValue
    var heightValue: DeserializationResult<DivSize> = .noValue
    var highPriorityPreviewShowValue: DeserializationResult<Expression<Bool>> = { parent?.highPriorityPreviewShow?.value() ?? .noValue }()
    var hoverEndActionsValue: DeserializationResult<[DivAction]> = .noValue
    var hoverStartActionsValue: DeserializationResult<[DivAction]> = .noValue
    var idValue: DeserializationResult<String> = { parent?.id?.value() ?? .noValue }()
    var imageUrlValue: DeserializationResult<Expression<URL>> = { parent?.imageUrl?.value() ?? .noValue }()
    var layoutProviderValue: DeserializationResult<DivLayoutProvider> = .noValue
    var longtapActionsValue: DeserializationResult<[DivAction]> = .noValue
    var marginsValue: DeserializationResult<DivEdgeInsets> = .noValue
    var paddingsValue: DeserializationResult<DivEdgeInsets> = .noValue
    var placeholderColorValue: DeserializationResult<Expression<Color>> = { parent?.placeholderColor?.value() ?? .noValue }()
    var preloadRequiredValue: DeserializationResult<Expression<Bool>> = { parent?.preloadRequired?.value() ?? .noValue }()
    var pressEndActionsValue: DeserializationResult<[DivAction]> = .noValue
    var pressStartActionsValue: DeserializationResult<[DivAction]> = .noValue
    var previewValue: DeserializationResult<Expression<String>> = { parent?.preview?.value() ?? .noValue }()
    var reuseIdValue: DeserializationResult<Expression<String>> = { parent?.reuseId?.value() ?? .noValue }()
    var rowSpanValue: DeserializationResult<Expression<Int>> = { parent?.rowSpan?.value() ?? .noValue }()
    var scaleValue: DeserializationResult<Expression<DivImageScale>> = { parent?.scale?.value() ?? .noValue }()
    var selectedActionsValue: DeserializationResult<[DivAction]> = .noValue
    var tintColorValue: DeserializationResult<Expression<Color>> = { parent?.tintColor?.value() ?? .noValue }()
    var tintModeValue: DeserializationResult<Expression<DivBlendMode>> = { parent?.tintMode?.value() ?? .noValue }()
    var tooltipsValue: DeserializationResult<[DivTooltip]> = .noValue
    var transformValue: DeserializationResult<DivTransform> = .noValue
    var transformationsValue: DeserializationResult<[DivTransformation]> = .noValue
    var transitionChangeValue: DeserializationResult<DivChangeTransition> = .noValue
    var transitionInValue: DeserializationResult<DivAppearanceTransition> = .noValue
    var transitionOutValue: DeserializationResult<DivAppearanceTransition> = .noValue
    var transitionTriggersValue: DeserializationResult<[DivTransitionTrigger]> = { parent?.transitionTriggers?.value(validatedBy: ResolvedValue.transitionTriggersValidator) ?? .noValue }()
    var variableTriggersValue: DeserializationResult<[DivTrigger]> = .noValue
    var variablesValue: DeserializationResult<[DivVariable]> = .noValue
    var visibilityValue: DeserializationResult<Expression<DivVisibility>> = { parent?.visibility?.value() ?? .noValue }()
    var visibilityActionValue: DeserializationResult<DivVisibilityAction> = .noValue
    var visibilityActionsValue: DeserializationResult<[DivVisibilityAction]> = .noValue
    var widthValue: DeserializationResult<DivSize> = .noValue
    _ = {
      // Each field is parsed in its own lambda to keep the stack size managable
      // Otherwise the compiler will allocate stack for each intermediate variable
      // upfront even when we don't actually visit a relevant branch
      for (key, __dictValue) in context.templateData {
        _ = {
          if key == "accessibility" {
           accessibilityValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivAccessibilityTemplate.self).merged(with: accessibilityValue)
          }
        }()
        _ = {
          if key == "action" {
           actionValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self).merged(with: actionValue)
          }
        }()
        _ = {
          if key == "action_animation" {
           actionAnimationValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivAnimationTemplate.self).merged(with: actionAnimationValue)
          }
        }()
        _ = {
          if key == "actions" {
           actionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self).merged(with: actionsValue)
          }
        }()
        _ = {
          if key == "alignment_horizontal" {
           alignmentHorizontalValue = deserialize(__dictValue).merged(with: alignmentHorizontalValue)
          }
        }()
        _ = {
          if key == "alignment_vertical" {
           alignmentVerticalValue = deserialize(__dictValue).merged(with: alignmentVerticalValue)
          }
        }()
        _ = {
          if key == "alpha" {
           alphaValue = deserialize(__dictValue, validator: ResolvedValue.alphaValidator).merged(with: alphaValue)
          }
        }()
        _ = {
          if key == "animators" {
           animatorsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivAnimatorTemplate.self).merged(with: animatorsValue)
          }
        }()
        _ = {
          if key == "appearance_animation" {
           appearanceAnimationValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivFadeTransitionTemplate.self).merged(with: appearanceAnimationValue)
          }
        }()
        _ = {
          if key == "aspect" {
           aspectValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivAspectTemplate.self).merged(with: aspectValue)
          }
        }()
        _ = {
          if key == "background" {
           backgroundValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivBackgroundTemplate.self).merged(with: backgroundValue)
          }
        }()
        _ = {
          if key == "border" {
           borderValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivBorderTemplate.self).merged(with: borderValue)
          }
        }()
        _ = {
          if key == "capture_focus_on_action" {
           captureFocusOnActionValue = deserialize(__dictValue).merged(with: captureFocusOnActionValue)
          }
        }()
        _ = {
          if key == "column_span" {
           columnSpanValue = deserialize(__dictValue, validator: ResolvedValue.columnSpanValidator).merged(with: columnSpanValue)
          }
        }()
        _ = {
          if key == "content_alignment_horizontal" {
           contentAlignmentHorizontalValue = deserialize(__dictValue).merged(with: contentAlignmentHorizontalValue)
          }
        }()
        _ = {
          if key == "content_alignment_vertical" {
           contentAlignmentVerticalValue = deserialize(__dictValue).merged(with: contentAlignmentVerticalValue)
          }
        }()
        _ = {
          if key == "disappear_actions" {
           disappearActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivDisappearActionTemplate.self).merged(with: disappearActionsValue)
          }
        }()
        _ = {
          if key == "doubletap_actions" {
           doubletapActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self).merged(with: doubletapActionsValue)
          }
        }()
        _ = {
          if key == "extensions" {
           extensionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivExtensionTemplate.self).merged(with: extensionsValue)
          }
        }()
        _ = {
          if key == "filters" {
           filtersValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivFilterTemplate.self).merged(with: filtersValue)
          }
        }()
        _ = {
          if key == "focus" {
           focusValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivFocusTemplate.self).merged(with: focusValue)
          }
        }()
        _ = {
          if key == "functions" {
           functionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivFunctionTemplate.self).merged(with: functionsValue)
          }
        }()
        _ = {
          if key == "height" {
           heightValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivSizeTemplate.self).merged(with: heightValue)
          }
        }()
        _ = {
          if key == "high_priority_preview_show" {
           highPriorityPreviewShowValue = deserialize(__dictValue).merged(with: highPriorityPreviewShowValue)
          }
        }()
        _ = {
          if key == "hover_end_actions" {
           hoverEndActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self).merged(with: hoverEndActionsValue)
          }
        }()
        _ = {
          if key == "hover_start_actions" {
           hoverStartActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self).merged(with: hoverStartActionsValue)
          }
        }()
        _ = {
          if key == "id" {
           idValue = deserialize(__dictValue).merged(with: idValue)
          }
        }()
        _ = {
          if key == "image_url" {
           imageUrlValue = deserialize(__dictValue, transform: URL.makeFromNonEncodedString).merged(with: imageUrlValue)
          }
        }()
        _ = {
          if key == "layout_provider" {
           layoutProviderValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivLayoutProviderTemplate.self).merged(with: layoutProviderValue)
          }
        }()
        _ = {
          if key == "longtap_actions" {
           longtapActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self).merged(with: longtapActionsValue)
          }
        }()
        _ = {
          if key == "margins" {
           marginsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivEdgeInsetsTemplate.self).merged(with: marginsValue)
          }
        }()
        _ = {
          if key == "paddings" {
           paddingsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivEdgeInsetsTemplate.self).merged(with: paddingsValue)
          }
        }()
        _ = {
          if key == "placeholder_color" {
           placeholderColorValue = deserialize(__dictValue, transform: Color.color(withHexString:)).merged(with: placeholderColorValue)
          }
        }()
        _ = {
          if key == "preload_required" {
           preloadRequiredValue = deserialize(__dictValue).merged(with: preloadRequiredValue)
          }
        }()
        _ = {
          if key == "press_end_actions" {
           pressEndActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self).merged(with: pressEndActionsValue)
          }
        }()
        _ = {
          if key == "press_start_actions" {
           pressStartActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self).merged(with: pressStartActionsValue)
          }
        }()
        _ = {
          if key == "preview" {
           previewValue = deserialize(__dictValue).merged(with: previewValue)
          }
        }()
        _ = {
          if key == "reuse_id" {
           reuseIdValue = deserialize(__dictValue).merged(with: reuseIdValue)
          }
        }()
        _ = {
          if key == "row_span" {
           rowSpanValue = deserialize(__dictValue, validator: ResolvedValue.rowSpanValidator).merged(with: rowSpanValue)
          }
        }()
        _ = {
          if key == "scale" {
           scaleValue = deserialize(__dictValue).merged(with: scaleValue)
          }
        }()
        _ = {
          if key == "selected_actions" {
           selectedActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self).merged(with: selectedActionsValue)
          }
        }()
        _ = {
          if key == "tint_color" {
           tintColorValue = deserialize(__dictValue, transform: Color.color(withHexString:)).merged(with: tintColorValue)
          }
        }()
        _ = {
          if key == "tint_mode" {
           tintModeValue = deserialize(__dictValue).merged(with: tintModeValue)
          }
        }()
        _ = {
          if key == "tooltips" {
           tooltipsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTooltipTemplate.self).merged(with: tooltipsValue)
          }
        }()
        _ = {
          if key == "transform" {
           transformValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTransformTemplate.self).merged(with: transformValue)
          }
        }()
        _ = {
          if key == "transformations" {
           transformationsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTransformationTemplate.self).merged(with: transformationsValue)
          }
        }()
        _ = {
          if key == "transition_change" {
           transitionChangeValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivChangeTransitionTemplate.self).merged(with: transitionChangeValue)
          }
        }()
        _ = {
          if key == "transition_in" {
           transitionInValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivAppearanceTransitionTemplate.self).merged(with: transitionInValue)
          }
        }()
        _ = {
          if key == "transition_out" {
           transitionOutValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivAppearanceTransitionTemplate.self).merged(with: transitionOutValue)
          }
        }()
        _ = {
          if key == "transition_triggers" {
           transitionTriggersValue = deserialize(__dictValue, validator: ResolvedValue.transitionTriggersValidator).merged(with: transitionTriggersValue)
          }
        }()
        _ = {
          if key == "variable_triggers" {
           variableTriggersValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTriggerTemplate.self).merged(with: variableTriggersValue)
          }
        }()
        _ = {
          if key == "variables" {
           variablesValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivVariableTemplate.self).merged(with: variablesValue)
          }
        }()
        _ = {
          if key == "visibility" {
           visibilityValue = deserialize(__dictValue).merged(with: visibilityValue)
          }
        }()
        _ = {
          if key == "visibility_action" {
           visibilityActionValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivVisibilityActionTemplate.self).merged(with: visibilityActionValue)
          }
        }()
        _ = {
          if key == "visibility_actions" {
           visibilityActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivVisibilityActionTemplate.self).merged(with: visibilityActionsValue)
          }
        }()
        _ = {
          if key == "width" {
           widthValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivSizeTemplate.self).merged(with: widthValue)
          }
        }()
        _ = {
         if key == parent?.accessibility?.link, context.templateData["accessibility"] == nil {
           accessibilityValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivAccessibilityTemplate.self).orFallback(accessibilityValue)
          }
        }()
        _ = {
         if key == parent?.action?.link, context.templateData["action"] == nil {
           actionValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self).orFallback(actionValue)
          }
        }()
        _ = {
         if key == parent?.actionAnimation?.link, context.templateData["action_animation"] == nil {
           actionAnimationValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivAnimationTemplate.self).orFallback(actionAnimationValue)
          }
        }()
        _ = {
         if key == parent?.actions?.link, context.templateData["actions"] == nil {
           actionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self).orFallback(actionsValue)
          }
        }()
        _ = {
         if key == parent?.alignmentHorizontal?.link, context.templateData["alignment_horizontal"] == nil {
           alignmentHorizontalValue = deserialize(__dictValue).orFallback(alignmentHorizontalValue)
          }
        }()
        _ = {
         if key == parent?.alignmentVertical?.link, context.templateData["alignment_vertical"] == nil {
           alignmentVerticalValue = deserialize(__dictValue).orFallback(alignmentVerticalValue)
          }
        }()
        _ = {
         if key == parent?.alpha?.link, context.templateData["alpha"] == nil {
           alphaValue = deserialize(__dictValue, validator: ResolvedValue.alphaValidator).orFallback(alphaValue)
          }
        }()
        _ = {
         if key == parent?.animators?.link, context.templateData["animators"] == nil {
           animatorsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivAnimatorTemplate.self).orFallback(animatorsValue)
          }
        }()
        _ = {
         if key == parent?.appearanceAnimation?.link, context.templateData["appearance_animation"] == nil {
           appearanceAnimationValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivFadeTransitionTemplate.self).orFallback(appearanceAnimationValue)
          }
        }()
        _ = {
         if key == parent?.aspect?.link, context.templateData["aspect"] == nil {
           aspectValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivAspectTemplate.self).orFallback(aspectValue)
          }
        }()
        _ = {
         if key == parent?.background?.link, context.templateData["background"] == nil {
           backgroundValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivBackgroundTemplate.self).orFallback(backgroundValue)
          }
        }()
        _ = {
         if key == parent?.border?.link, context.templateData["border"] == nil {
           borderValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivBorderTemplate.self).orFallback(borderValue)
          }
        }()
        _ = {
         if key == parent?.captureFocusOnAction?.link, context.templateData["capture_focus_on_action"] == nil {
           captureFocusOnActionValue = deserialize(__dictValue).orFallback(captureFocusOnActionValue)
          }
        }()
        _ = {
         if key == parent?.columnSpan?.link, context.templateData["column_span"] == nil {
           columnSpanValue = deserialize(__dictValue, validator: ResolvedValue.columnSpanValidator).orFallback(columnSpanValue)
          }
        }()
        _ = {
         if key == parent?.contentAlignmentHorizontal?.link, context.templateData["content_alignment_horizontal"] == nil {
           contentAlignmentHorizontalValue = deserialize(__dictValue).orFallback(contentAlignmentHorizontalValue)
          }
        }()
        _ = {
         if key == parent?.contentAlignmentVertical?.link, context.templateData["content_alignment_vertical"] == nil {
           contentAlignmentVerticalValue = deserialize(__dictValue).orFallback(contentAlignmentVerticalValue)
          }
        }()
        _ = {
         if key == parent?.disappearActions?.link, context.templateData["disappear_actions"] == nil {
           disappearActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivDisappearActionTemplate.self).orFallback(disappearActionsValue)
          }
        }()
        _ = {
         if key == parent?.doubletapActions?.link, context.templateData["doubletap_actions"] == nil {
           doubletapActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self).orFallback(doubletapActionsValue)
          }
        }()
        _ = {
         if key == parent?.extensions?.link, context.templateData["extensions"] == nil {
           extensionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivExtensionTemplate.self).orFallback(extensionsValue)
          }
        }()
        _ = {
         if key == parent?.filters?.link, context.templateData["filters"] == nil {
           filtersValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivFilterTemplate.self).orFallback(filtersValue)
          }
        }()
        _ = {
         if key == parent?.focus?.link, context.templateData["focus"] == nil {
           focusValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivFocusTemplate.self).orFallback(focusValue)
          }
        }()
        _ = {
         if key == parent?.functions?.link, context.templateData["functions"] == nil {
           functionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivFunctionTemplate.self).orFallback(functionsValue)
          }
        }()
        _ = {
         if key == parent?.height?.link, context.templateData["height"] == nil {
           heightValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivSizeTemplate.self).orFallback(heightValue)
          }
        }()
        _ = {
         if key == parent?.highPriorityPreviewShow?.link, context.templateData["high_priority_preview_show"] == nil {
           highPriorityPreviewShowValue = deserialize(__dictValue).orFallback(highPriorityPreviewShowValue)
          }
        }()
        _ = {
         if key == parent?.hoverEndActions?.link, context.templateData["hover_end_actions"] == nil {
           hoverEndActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self).orFallback(hoverEndActionsValue)
          }
        }()
        _ = {
         if key == parent?.hoverStartActions?.link, context.templateData["hover_start_actions"] == nil {
           hoverStartActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self).orFallback(hoverStartActionsValue)
          }
        }()
        _ = {
         if key == parent?.id?.link, context.templateData["id"] == nil {
           idValue = deserialize(__dictValue).orFallback(idValue)
          }
        }()
        _ = {
         if key == parent?.imageUrl?.link, context.templateData["image_url"] == nil {
           imageUrlValue = deserialize(__dictValue, transform: URL.makeFromNonEncodedString).orFallback(imageUrlValue)
          }
        }()
        _ = {
         if key == parent?.layoutProvider?.link, context.templateData["layout_provider"] == nil {
           layoutProviderValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivLayoutProviderTemplate.self).orFallback(layoutProviderValue)
          }
        }()
        _ = {
         if key == parent?.longtapActions?.link, context.templateData["longtap_actions"] == nil {
           longtapActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self).orFallback(longtapActionsValue)
          }
        }()
        _ = {
         if key == parent?.margins?.link, context.templateData["margins"] == nil {
           marginsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivEdgeInsetsTemplate.self).orFallback(marginsValue)
          }
        }()
        _ = {
         if key == parent?.paddings?.link, context.templateData["paddings"] == nil {
           paddingsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivEdgeInsetsTemplate.self).orFallback(paddingsValue)
          }
        }()
        _ = {
         if key == parent?.placeholderColor?.link, context.templateData["placeholder_color"] == nil {
           placeholderColorValue = deserialize(__dictValue, transform: Color.color(withHexString:)).orFallback(placeholderColorValue)
          }
        }()
        _ = {
         if key == parent?.preloadRequired?.link, context.templateData["preload_required"] == nil {
           preloadRequiredValue = deserialize(__dictValue).orFallback(preloadRequiredValue)
          }
        }()
        _ = {
         if key == parent?.pressEndActions?.link, context.templateData["press_end_actions"] == nil {
           pressEndActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self).orFallback(pressEndActionsValue)
          }
        }()
        _ = {
         if key == parent?.pressStartActions?.link, context.templateData["press_start_actions"] == nil {
           pressStartActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self).orFallback(pressStartActionsValue)
          }
        }()
        _ = {
         if key == parent?.preview?.link, context.templateData["preview"] == nil {
           previewValue = deserialize(__dictValue).orFallback(previewValue)
          }
        }()
        _ = {
         if key == parent?.reuseId?.link, context.templateData["reuse_id"] == nil {
           reuseIdValue = deserialize(__dictValue).orFallback(reuseIdValue)
          }
        }()
        _ = {
         if key == parent?.rowSpan?.link, context.templateData["row_span"] == nil {
           rowSpanValue = deserialize(__dictValue, validator: ResolvedValue.rowSpanValidator).orFallback(rowSpanValue)
          }
        }()
        _ = {
         if key == parent?.scale?.link, context.templateData["scale"] == nil {
           scaleValue = deserialize(__dictValue).orFallback(scaleValue)
          }
        }()
        _ = {
         if key == parent?.selectedActions?.link, context.templateData["selected_actions"] == nil {
           selectedActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self).orFallback(selectedActionsValue)
          }
        }()
        _ = {
         if key == parent?.tintColor?.link, context.templateData["tint_color"] == nil {
           tintColorValue = deserialize(__dictValue, transform: Color.color(withHexString:)).orFallback(tintColorValue)
          }
        }()
        _ = {
         if key == parent?.tintMode?.link, context.templateData["tint_mode"] == nil {
           tintModeValue = deserialize(__dictValue).orFallback(tintModeValue)
          }
        }()
        _ = {
         if key == parent?.tooltips?.link, context.templateData["tooltips"] == nil {
           tooltipsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTooltipTemplate.self).orFallback(tooltipsValue)
          }
        }()
        _ = {
         if key == parent?.transform?.link, context.templateData["transform"] == nil {
           transformValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTransformTemplate.self).orFallback(transformValue)
          }
        }()
        _ = {
         if key == parent?.transformations?.link, context.templateData["transformations"] == nil {
           transformationsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTransformationTemplate.self).orFallback(transformationsValue)
          }
        }()
        _ = {
         if key == parent?.transitionChange?.link, context.templateData["transition_change"] == nil {
           transitionChangeValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivChangeTransitionTemplate.self).orFallback(transitionChangeValue)
          }
        }()
        _ = {
         if key == parent?.transitionIn?.link, context.templateData["transition_in"] == nil {
           transitionInValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivAppearanceTransitionTemplate.self).orFallback(transitionInValue)
          }
        }()
        _ = {
         if key == parent?.transitionOut?.link, context.templateData["transition_out"] == nil {
           transitionOutValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivAppearanceTransitionTemplate.self).orFallback(transitionOutValue)
          }
        }()
        _ = {
         if key == parent?.transitionTriggers?.link, context.templateData["transition_triggers"] == nil {
           transitionTriggersValue = deserialize(__dictValue, validator: ResolvedValue.transitionTriggersValidator).orFallback(transitionTriggersValue)
          }
        }()
        _ = {
         if key == parent?.variableTriggers?.link, context.templateData["variable_triggers"] == nil {
           variableTriggersValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTriggerTemplate.self).orFallback(variableTriggersValue)
          }
        }()
        _ = {
         if key == parent?.variables?.link, context.templateData["variables"] == nil {
           variablesValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivVariableTemplate.self).orFallback(variablesValue)
          }
        }()
        _ = {
         if key == parent?.visibility?.link, context.templateData["visibility"] == nil {
           visibilityValue = deserialize(__dictValue).orFallback(visibilityValue)
          }
        }()
        _ = {
         if key == parent?.visibilityAction?.link, context.templateData["visibility_action"] == nil {
           visibilityActionValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivVisibilityActionTemplate.self).orFallback(visibilityActionValue)
          }
        }()
        _ = {
         if key == parent?.visibilityActions?.link, context.templateData["visibility_actions"] == nil {
           visibilityActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivVisibilityActionTemplate.self).orFallback(visibilityActionsValue)
          }
        }()
        _ = {
         if key == parent?.width?.link, context.templateData["width"] == nil {
           widthValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivSizeTemplate.self).orFallback(widthValue)
          }
        }()
      }
    }()
    if let parent = parent {
      _ = { accessibilityValue = accessibilityValue.merged(with: { parent.accessibility?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { actionValue = actionValue.merged(with: { parent.action?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { actionAnimationValue = actionAnimationValue.merged(with: { parent.actionAnimation?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { actionsValue = actionsValue.merged(with: { parent.actions?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { animatorsValue = animatorsValue.merged(with: { parent.animators?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { appearanceAnimationValue = appearanceAnimationValue.merged(with: { parent.appearanceAnimation?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { aspectValue = aspectValue.merged(with: { parent.aspect?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { backgroundValue = backgroundValue.merged(with: { parent.background?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { borderValue = borderValue.merged(with: { parent.border?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { disappearActionsValue = disappearActionsValue.merged(with: { parent.disappearActions?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { doubletapActionsValue = doubletapActionsValue.merged(with: { parent.doubletapActions?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { extensionsValue = extensionsValue.merged(with: { parent.extensions?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { filtersValue = filtersValue.merged(with: { parent.filters?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { focusValue = focusValue.merged(with: { parent.focus?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { functionsValue = functionsValue.merged(with: { parent.functions?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { heightValue = heightValue.merged(with: { parent.height?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { hoverEndActionsValue = hoverEndActionsValue.merged(with: { parent.hoverEndActions?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { hoverStartActionsValue = hoverStartActionsValue.merged(with: { parent.hoverStartActions?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { layoutProviderValue = layoutProviderValue.merged(with: { parent.layoutProvider?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { longtapActionsValue = longtapActionsValue.merged(with: { parent.longtapActions?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { marginsValue = marginsValue.merged(with: { parent.margins?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { paddingsValue = paddingsValue.merged(with: { parent.paddings?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { pressEndActionsValue = pressEndActionsValue.merged(with: { parent.pressEndActions?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { pressStartActionsValue = pressStartActionsValue.merged(with: { parent.pressStartActions?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { selectedActionsValue = selectedActionsValue.merged(with: { parent.selectedActions?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { tooltipsValue = tooltipsValue.merged(with: { parent.tooltips?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { transformValue = transformValue.merged(with: { parent.transform?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { transformationsValue = transformationsValue.merged(with: { parent.transformations?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { transitionChangeValue = transitionChangeValue.merged(with: { parent.transitionChange?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { transitionInValue = transitionInValue.merged(with: { parent.transitionIn?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { transitionOutValue = transitionOutValue.merged(with: { parent.transitionOut?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { variableTriggersValue = variableTriggersValue.merged(with: { parent.variableTriggers?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { variablesValue = variablesValue.merged(with: { parent.variables?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { visibilityActionValue = visibilityActionValue.merged(with: { parent.visibilityAction?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { visibilityActionsValue = visibilityActionsValue.merged(with: { parent.visibilityActions?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { widthValue = widthValue.merged(with: { parent.width?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
    }
    let errors = mergeErrors(
      accessibilityValue.errorsOrWarnings?.map { .nestedObjectError(field: "accessibility", error: $0) },
      actionValue.errorsOrWarnings?.map { .nestedObjectError(field: "action", error: $0) },
      actionAnimationValue.errorsOrWarnings?.map { .nestedObjectError(field: "action_animation", error: $0) },
      actionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "actions", error: $0) },
      alignmentHorizontalValue.errorsOrWarnings?.map { .nestedObjectError(field: "alignment_horizontal", error: $0) },
      alignmentVerticalValue.errorsOrWarnings?.map { .nestedObjectError(field: "alignment_vertical", error: $0) },
      alphaValue.errorsOrWarnings?.map { .nestedObjectError(field: "alpha", error: $0) },
      animatorsValue.errorsOrWarnings?.map { .nestedObjectError(field: "animators", error: $0) },
      appearanceAnimationValue.errorsOrWarnings?.map { .nestedObjectError(field: "appearance_animation", error: $0) },
      aspectValue.errorsOrWarnings?.map { .nestedObjectError(field: "aspect", error: $0) },
      backgroundValue.errorsOrWarnings?.map { .nestedObjectError(field: "background", error: $0) },
      borderValue.errorsOrWarnings?.map { .nestedObjectError(field: "border", error: $0) },
      captureFocusOnActionValue.errorsOrWarnings?.map { .nestedObjectError(field: "capture_focus_on_action", error: $0) },
      columnSpanValue.errorsOrWarnings?.map { .nestedObjectError(field: "column_span", error: $0) },
      contentAlignmentHorizontalValue.errorsOrWarnings?.map { .nestedObjectError(field: "content_alignment_horizontal", error: $0) },
      contentAlignmentVerticalValue.errorsOrWarnings?.map { .nestedObjectError(field: "content_alignment_vertical", error: $0) },
      disappearActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "disappear_actions", error: $0) },
      doubletapActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "doubletap_actions", error: $0) },
      extensionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "extensions", error: $0) },
      filtersValue.errorsOrWarnings?.map { .nestedObjectError(field: "filters", error: $0) },
      focusValue.errorsOrWarnings?.map { .nestedObjectError(field: "focus", error: $0) },
      functionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "functions", error: $0) },
      heightValue.errorsOrWarnings?.map { .nestedObjectError(field: "height", error: $0) },
      highPriorityPreviewShowValue.errorsOrWarnings?.map { .nestedObjectError(field: "high_priority_preview_show", error: $0) },
      hoverEndActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "hover_end_actions", error: $0) },
      hoverStartActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "hover_start_actions", error: $0) },
      idValue.errorsOrWarnings?.map { .nestedObjectError(field: "id", error: $0) },
      imageUrlValue.errorsOrWarnings?.map { .nestedObjectError(field: "image_url", error: $0) },
      layoutProviderValue.errorsOrWarnings?.map { .nestedObjectError(field: "layout_provider", error: $0) },
      longtapActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "longtap_actions", error: $0) },
      marginsValue.errorsOrWarnings?.map { .nestedObjectError(field: "margins", error: $0) },
      paddingsValue.errorsOrWarnings?.map { .nestedObjectError(field: "paddings", error: $0) },
      placeholderColorValue.errorsOrWarnings?.map { .nestedObjectError(field: "placeholder_color", error: $0) },
      preloadRequiredValue.errorsOrWarnings?.map { .nestedObjectError(field: "preload_required", error: $0) },
      pressEndActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "press_end_actions", error: $0) },
      pressStartActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "press_start_actions", error: $0) },
      previewValue.errorsOrWarnings?.map { .nestedObjectError(field: "preview", error: $0) },
      reuseIdValue.errorsOrWarnings?.map { .nestedObjectError(field: "reuse_id", error: $0) },
      rowSpanValue.errorsOrWarnings?.map { .nestedObjectError(field: "row_span", error: $0) },
      scaleValue.errorsOrWarnings?.map { .nestedObjectError(field: "scale", error: $0) },
      selectedActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "selected_actions", error: $0) },
      tintColorValue.errorsOrWarnings?.map { .nestedObjectError(field: "tint_color", error: $0) },
      tintModeValue.errorsOrWarnings?.map { .nestedObjectError(field: "tint_mode", error: $0) },
      tooltipsValue.errorsOrWarnings?.map { .nestedObjectError(field: "tooltips", error: $0) },
      transformValue.errorsOrWarnings?.map { .nestedObjectError(field: "transform", error: $0) },
      transformationsValue.errorsOrWarnings?.map { .nestedObjectError(field: "transformations", error: $0) },
      transitionChangeValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_change", error: $0) },
      transitionInValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_in", error: $0) },
      transitionOutValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_out", error: $0) },
      transitionTriggersValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_triggers", error: $0) },
      variableTriggersValue.errorsOrWarnings?.map { .nestedObjectError(field: "variable_triggers", error: $0) },
      variablesValue.errorsOrWarnings?.map { .nestedObjectError(field: "variables", error: $0) },
      visibilityValue.errorsOrWarnings?.map { .nestedObjectError(field: "visibility", error: $0) },
      visibilityActionValue.errorsOrWarnings?.map { .nestedObjectError(field: "visibility_action", error: $0) },
      visibilityActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "visibility_actions", error: $0) },
      widthValue.errorsOrWarnings?.map { .nestedObjectError(field: "width", error: $0) }
    )
    let result = DivImage(
      accessibility: { accessibilityValue.value }(),
      action: { actionValue.value }(),
      actionAnimation: { actionAnimationValue.value }(),
      actions: { actionsValue.value }(),
      alignmentHorizontal: { alignmentHorizontalValue.value }(),
      alignmentVertical: { alignmentVerticalValue.value }(),
      alpha: { alphaValue.value }(),
      animators: { animatorsValue.value }(),
      appearanceAnimation: { appearanceAnimationValue.value }(),
      aspect: { aspectValue.value }(),
      background: { backgroundValue.value }(),
      border: { borderValue.value }(),
      captureFocusOnAction: { captureFocusOnActionValue.value }(),
      columnSpan: { columnSpanValue.value }(),
      contentAlignmentHorizontal: { contentAlignmentHorizontalValue.value }(),
      contentAlignmentVertical: { contentAlignmentVerticalValue.value }(),
      disappearActions: { disappearActionsValue.value }(),
      doubletapActions: { doubletapActionsValue.value }(),
      extensions: { extensionsValue.value }(),
      filters: { filtersValue.value }(),
      focus: { focusValue.value }(),
      functions: { functionsValue.value }(),
      height: { heightValue.value }(),
      highPriorityPreviewShow: { highPriorityPreviewShowValue.value }(),
      hoverEndActions: { hoverEndActionsValue.value }(),
      hoverStartActions: { hoverStartActionsValue.value }(),
      id: { idValue.value }(),
      imageUrl: { imageUrlValue.value }(),
      layoutProvider: { layoutProviderValue.value }(),
      longtapActions: { longtapActionsValue.value }(),
      margins: { marginsValue.value }(),
      paddings: { paddingsValue.value }(),
      placeholderColor: { placeholderColorValue.value }(),
      preloadRequired: { preloadRequiredValue.value }(),
      pressEndActions: { pressEndActionsValue.value }(),
      pressStartActions: { pressStartActionsValue.value }(),
      preview: { previewValue.value }(),
      reuseId: { reuseIdValue.value }(),
      rowSpan: { rowSpanValue.value }(),
      scale: { scaleValue.value }(),
      selectedActions: { selectedActionsValue.value }(),
      tintColor: { tintColorValue.value }(),
      tintMode: { tintModeValue.value }(),
      tooltips: { tooltipsValue.value }(),
      transform: { transformValue.value }(),
      transformations: { transformationsValue.value }(),
      transitionChange: { transitionChangeValue.value }(),
      transitionIn: { transitionInValue.value }(),
      transitionOut: { transitionOutValue.value }(),
      transitionTriggers: { transitionTriggersValue.value }(),
      variableTriggers: { variableTriggersValue.value }(),
      variables: { variablesValue.value }(),
      visibility: { visibilityValue.value }(),
      visibilityAction: { visibilityActionValue.value }(),
      visibilityActions: { visibilityActionsValue.value }(),
      width: { widthValue.value }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivImageTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? DivImageTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return DivImageTemplate(
      parent: nil,
      accessibility: accessibility ?? mergedParent.accessibility,
      action: action ?? mergedParent.action,
      actionAnimation: actionAnimation ?? mergedParent.actionAnimation,
      actions: actions ?? mergedParent.actions,
      alignmentHorizontal: alignmentHorizontal ?? mergedParent.alignmentHorizontal,
      alignmentVertical: alignmentVertical ?? mergedParent.alignmentVertical,
      alpha: alpha ?? mergedParent.alpha,
      animators: animators ?? mergedParent.animators,
      appearanceAnimation: appearanceAnimation ?? mergedParent.appearanceAnimation,
      aspect: aspect ?? mergedParent.aspect,
      background: background ?? mergedParent.background,
      border: border ?? mergedParent.border,
      captureFocusOnAction: captureFocusOnAction ?? mergedParent.captureFocusOnAction,
      columnSpan: columnSpan ?? mergedParent.columnSpan,
      contentAlignmentHorizontal: contentAlignmentHorizontal ?? mergedParent.contentAlignmentHorizontal,
      contentAlignmentVertical: contentAlignmentVertical ?? mergedParent.contentAlignmentVertical,
      disappearActions: disappearActions ?? mergedParent.disappearActions,
      doubletapActions: doubletapActions ?? mergedParent.doubletapActions,
      extensions: extensions ?? mergedParent.extensions,
      filters: filters ?? mergedParent.filters,
      focus: focus ?? mergedParent.focus,
      functions: functions ?? mergedParent.functions,
      height: height ?? mergedParent.height,
      highPriorityPreviewShow: highPriorityPreviewShow ?? mergedParent.highPriorityPreviewShow,
      hoverEndActions: hoverEndActions ?? mergedParent.hoverEndActions,
      hoverStartActions: hoverStartActions ?? mergedParent.hoverStartActions,
      id: id ?? mergedParent.id,
      imageUrl: imageUrl ?? mergedParent.imageUrl,
      layoutProvider: layoutProvider ?? mergedParent.layoutProvider,
      longtapActions: longtapActions ?? mergedParent.longtapActions,
      margins: margins ?? mergedParent.margins,
      paddings: paddings ?? mergedParent.paddings,
      placeholderColor: placeholderColor ?? mergedParent.placeholderColor,
      preloadRequired: preloadRequired ?? mergedParent.preloadRequired,
      pressEndActions: pressEndActions ?? mergedParent.pressEndActions,
      pressStartActions: pressStartActions ?? mergedParent.pressStartActions,
      preview: preview ?? mergedParent.preview,
      reuseId: reuseId ?? mergedParent.reuseId,
      rowSpan: rowSpan ?? mergedParent.rowSpan,
      scale: scale ?? mergedParent.scale,
      selectedActions: selectedActions ?? mergedParent.selectedActions,
      tintColor: tintColor ?? mergedParent.tintColor,
      tintMode: tintMode ?? mergedParent.tintMode,
      tooltips: tooltips ?? mergedParent.tooltips,
      transform: transform ?? mergedParent.transform,
      transformations: transformations ?? mergedParent.transformations,
      transitionChange: transitionChange ?? mergedParent.transitionChange,
      transitionIn: transitionIn ?? mergedParent.transitionIn,
      transitionOut: transitionOut ?? mergedParent.transitionOut,
      transitionTriggers: transitionTriggers ?? mergedParent.transitionTriggers,
      variableTriggers: variableTriggers ?? mergedParent.variableTriggers,
      variables: variables ?? mergedParent.variables,
      visibility: visibility ?? mergedParent.visibility,
      visibilityAction: visibilityAction ?? mergedParent.visibilityAction,
      visibilityActions: visibilityActions ?? mergedParent.visibilityActions,
      width: width ?? mergedParent.width
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivImageTemplate {
    let merged = try mergedWithParent(templates: templates)

    return DivImageTemplate(
      parent: nil,
      accessibility: merged.accessibility?.tryResolveParent(templates: templates),
      action: merged.action?.tryResolveParent(templates: templates),
      actionAnimation: merged.actionAnimation?.tryResolveParent(templates: templates),
      actions: merged.actions?.tryResolveParent(templates: templates),
      alignmentHorizontal: merged.alignmentHorizontal,
      alignmentVertical: merged.alignmentVertical,
      alpha: merged.alpha,
      animators: merged.animators?.tryResolveParent(templates: templates),
      appearanceAnimation: merged.appearanceAnimation?.tryResolveParent(templates: templates),
      aspect: merged.aspect?.tryResolveParent(templates: templates),
      background: merged.background?.tryResolveParent(templates: templates),
      border: merged.border?.tryResolveParent(templates: templates),
      captureFocusOnAction: merged.captureFocusOnAction,
      columnSpan: merged.columnSpan,
      contentAlignmentHorizontal: merged.contentAlignmentHorizontal,
      contentAlignmentVertical: merged.contentAlignmentVertical,
      disappearActions: merged.disappearActions?.tryResolveParent(templates: templates),
      doubletapActions: merged.doubletapActions?.tryResolveParent(templates: templates),
      extensions: merged.extensions?.tryResolveParent(templates: templates),
      filters: merged.filters?.tryResolveParent(templates: templates),
      focus: merged.focus?.tryResolveParent(templates: templates),
      functions: merged.functions?.tryResolveParent(templates: templates),
      height: merged.height?.tryResolveParent(templates: templates),
      highPriorityPreviewShow: merged.highPriorityPreviewShow,
      hoverEndActions: merged.hoverEndActions?.tryResolveParent(templates: templates),
      hoverStartActions: merged.hoverStartActions?.tryResolveParent(templates: templates),
      id: merged.id,
      imageUrl: merged.imageUrl,
      layoutProvider: merged.layoutProvider?.tryResolveParent(templates: templates),
      longtapActions: merged.longtapActions?.tryResolveParent(templates: templates),
      margins: merged.margins?.tryResolveParent(templates: templates),
      paddings: merged.paddings?.tryResolveParent(templates: templates),
      placeholderColor: merged.placeholderColor,
      preloadRequired: merged.preloadRequired,
      pressEndActions: merged.pressEndActions?.tryResolveParent(templates: templates),
      pressStartActions: merged.pressStartActions?.tryResolveParent(templates: templates),
      preview: merged.preview,
      reuseId: merged.reuseId,
      rowSpan: merged.rowSpan,
      scale: merged.scale,
      selectedActions: merged.selectedActions?.tryResolveParent(templates: templates),
      tintColor: merged.tintColor,
      tintMode: merged.tintMode,
      tooltips: merged.tooltips?.tryResolveParent(templates: templates),
      transform: merged.transform?.tryResolveParent(templates: templates),
      transformations: merged.transformations?.tryResolveParent(templates: templates),
      transitionChange: merged.transitionChange?.tryResolveParent(templates: templates),
      transitionIn: merged.transitionIn?.tryResolveParent(templates: templates),
      transitionOut: merged.transitionOut?.tryResolveParent(templates: templates),
      transitionTriggers: merged.transitionTriggers,
      variableTriggers: merged.variableTriggers?.tryResolveParent(templates: templates),
      variables: merged.variables?.tryResolveParent(templates: templates),
      visibility: merged.visibility,
      visibilityAction: merged.visibilityAction?.tryResolveParent(templates: templates),
      visibilityActions: merged.visibilityActions?.tryResolveParent(templates: templates),
      width: merged.width?.tryResolveParent(templates: templates)
    )
  }
}
