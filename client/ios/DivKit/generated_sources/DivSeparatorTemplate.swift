// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivSeparatorTemplate: TemplateValue {
  public final class DelimiterStyleTemplate: TemplateValue {
    public typealias Orientation = DivSeparator.DelimiterStyle.Orientation

    public let color: Field<Expression<Color>>? // default value: #14000000
    public let orientation: Field<Expression<Orientation>>? // default value: horizontal

    public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
      self.init(
        color: dictionary.getOptionalExpressionField("color", transform: Color.color(withHexString:)),
        orientation: dictionary.getOptionalExpressionField("orientation")
      )
    }

    init(
      color: Field<Expression<Color>>? = nil,
      orientation: Field<Expression<Orientation>>? = nil
    ) {
      self.color = color
      self.orientation = orientation
    }

    private static func resolveOnlyLinks(context: TemplatesContext, parent: DelimiterStyleTemplate?) -> DeserializationResult<DivSeparator.DelimiterStyle> {
      let colorValue = { parent?.color?.resolveOptionalValue(context: context, transform: Color.color(withHexString:)) ?? .noValue }()
      let orientationValue = { parent?.orientation?.resolveOptionalValue(context: context) ?? .noValue }()
      let errors = mergeErrors(
        colorValue.errorsOrWarnings?.map { .nestedObjectError(field: "color", error: $0) },
        orientationValue.errorsOrWarnings?.map { .nestedObjectError(field: "orientation", error: $0) }
      )
      let result = DivSeparator.DelimiterStyle(
        color: { colorValue.value }(),
        orientation: { orientationValue.value }()
      )
      return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
    }

    public static func resolveValue(context: TemplatesContext, parent: DelimiterStyleTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivSeparator.DelimiterStyle> {
      if useOnlyLinks {
        return resolveOnlyLinks(context: context, parent: parent)
      }
      var colorValue: DeserializationResult<Expression<Color>> = { parent?.color?.value() ?? .noValue }()
      var orientationValue: DeserializationResult<Expression<DivSeparator.DelimiterStyle.Orientation>> = { parent?.orientation?.value() ?? .noValue }()
      _ = {
        // Each field is parsed in its own lambda to keep the stack size managable
        // Otherwise the compiler will allocate stack for each intermediate variable
        // upfront even when we don't actually visit a relevant branch
        for (key, __dictValue) in context.templateData {
          _ = {
            if key == "color" {
             colorValue = deserialize(__dictValue, transform: Color.color(withHexString:)).merged(with: colorValue)
            }
          }()
          _ = {
            if key == "orientation" {
             orientationValue = deserialize(__dictValue).merged(with: orientationValue)
            }
          }()
          _ = {
           if key == parent?.color?.link {
             colorValue = colorValue.merged(with: { deserialize(__dictValue, transform: Color.color(withHexString:)) })
            }
          }()
          _ = {
           if key == parent?.orientation?.link {
             orientationValue = orientationValue.merged(with: { deserialize(__dictValue) })
            }
          }()
        }
      }()
      let errors = mergeErrors(
        colorValue.errorsOrWarnings?.map { .nestedObjectError(field: "color", error: $0) },
        orientationValue.errorsOrWarnings?.map { .nestedObjectError(field: "orientation", error: $0) }
      )
      let result = DivSeparator.DelimiterStyle(
        color: { colorValue.value }(),
        orientation: { orientationValue.value }()
      )
      return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
    }

    private func mergedWithParent(templates: [TemplateName: Any]) throws -> DelimiterStyleTemplate {
      return self
    }

    public func resolveParent(templates: [TemplateName: Any]) throws -> DelimiterStyleTemplate {
      return try mergedWithParent(templates: templates)
    }
  }

  public static let type: String = "separator"
  public let parent: String?
  public let accessibility: Field<DivAccessibilityTemplate>?
  public let action: Field<DivActionTemplate>?
  public let actionAnimation: Field<DivAnimationTemplate>? // default value: DivAnimation(duration: .value(100), endValue: .value(0.6), name: .value(.fade), startValue: .value(1))
  public let actions: Field<[DivActionTemplate]>?
  public let alignmentHorizontal: Field<Expression<DivAlignmentHorizontal>>?
  public let alignmentVertical: Field<Expression<DivAlignmentVertical>>?
  public let alpha: Field<Expression<Double>>? // constraint: number >= 0.0 && number <= 1.0; default value: 1.0
  public let animators: Field<[DivAnimatorTemplate]>?
  public let background: Field<[DivBackgroundTemplate]>?
  public let border: Field<DivBorderTemplate>?
  public let columnSpan: Field<Expression<Int>>? // constraint: number >= 0
  public let delimiterStyle: Field<DelimiterStyleTemplate>?
  public let disappearActions: Field<[DivDisappearActionTemplate]>?
  public let doubletapActions: Field<[DivActionTemplate]>?
  public let extensions: Field<[DivExtensionTemplate]>?
  public let focus: Field<DivFocusTemplate>?
  public let functions: Field<[DivFunctionTemplate]>?
  public let height: Field<DivSizeTemplate>? // default value: .divWrapContentSize(DivWrapContentSize())
  public let hoverEndActions: Field<[DivActionTemplate]>?
  public let hoverStartActions: Field<[DivActionTemplate]>?
  public let id: Field<String>?
  public let layoutProvider: Field<DivLayoutProviderTemplate>?
  public let longtapActions: Field<[DivActionTemplate]>?
  public let margins: Field<DivEdgeInsetsTemplate>?
  public let paddings: Field<DivEdgeInsetsTemplate>?
  public let pressEndActions: Field<[DivActionTemplate]>?
  public let pressStartActions: Field<[DivActionTemplate]>?
  public let reuseId: Field<Expression<String>>?
  public let rowSpan: Field<Expression<Int>>? // constraint: number >= 0
  public let selectedActions: Field<[DivActionTemplate]>?
  public let tooltips: Field<[DivTooltipTemplate]>?
  public let transform: Field<DivTransformTemplate>?
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
      background: dictionary.getOptionalArray("background", templateToType: templateToType),
      border: dictionary.getOptionalField("border", templateToType: templateToType),
      columnSpan: dictionary.getOptionalExpressionField("column_span"),
      delimiterStyle: dictionary.getOptionalField("delimiter_style", templateToType: templateToType),
      disappearActions: dictionary.getOptionalArray("disappear_actions", templateToType: templateToType),
      doubletapActions: dictionary.getOptionalArray("doubletap_actions", templateToType: templateToType),
      extensions: dictionary.getOptionalArray("extensions", templateToType: templateToType),
      focus: dictionary.getOptionalField("focus", templateToType: templateToType),
      functions: dictionary.getOptionalArray("functions", templateToType: templateToType),
      height: dictionary.getOptionalField("height", templateToType: templateToType),
      hoverEndActions: dictionary.getOptionalArray("hover_end_actions", templateToType: templateToType),
      hoverStartActions: dictionary.getOptionalArray("hover_start_actions", templateToType: templateToType),
      id: dictionary.getOptionalField("id"),
      layoutProvider: dictionary.getOptionalField("layout_provider", templateToType: templateToType),
      longtapActions: dictionary.getOptionalArray("longtap_actions", templateToType: templateToType),
      margins: dictionary.getOptionalField("margins", templateToType: templateToType),
      paddings: dictionary.getOptionalField("paddings", templateToType: templateToType),
      pressEndActions: dictionary.getOptionalArray("press_end_actions", templateToType: templateToType),
      pressStartActions: dictionary.getOptionalArray("press_start_actions", templateToType: templateToType),
      reuseId: dictionary.getOptionalExpressionField("reuse_id"),
      rowSpan: dictionary.getOptionalExpressionField("row_span"),
      selectedActions: dictionary.getOptionalArray("selected_actions", templateToType: templateToType),
      tooltips: dictionary.getOptionalArray("tooltips", templateToType: templateToType),
      transform: dictionary.getOptionalField("transform", templateToType: templateToType),
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
    background: Field<[DivBackgroundTemplate]>? = nil,
    border: Field<DivBorderTemplate>? = nil,
    columnSpan: Field<Expression<Int>>? = nil,
    delimiterStyle: Field<DelimiterStyleTemplate>? = nil,
    disappearActions: Field<[DivDisappearActionTemplate]>? = nil,
    doubletapActions: Field<[DivActionTemplate]>? = nil,
    extensions: Field<[DivExtensionTemplate]>? = nil,
    focus: Field<DivFocusTemplate>? = nil,
    functions: Field<[DivFunctionTemplate]>? = nil,
    height: Field<DivSizeTemplate>? = nil,
    hoverEndActions: Field<[DivActionTemplate]>? = nil,
    hoverStartActions: Field<[DivActionTemplate]>? = nil,
    id: Field<String>? = nil,
    layoutProvider: Field<DivLayoutProviderTemplate>? = nil,
    longtapActions: Field<[DivActionTemplate]>? = nil,
    margins: Field<DivEdgeInsetsTemplate>? = nil,
    paddings: Field<DivEdgeInsetsTemplate>? = nil,
    pressEndActions: Field<[DivActionTemplate]>? = nil,
    pressStartActions: Field<[DivActionTemplate]>? = nil,
    reuseId: Field<Expression<String>>? = nil,
    rowSpan: Field<Expression<Int>>? = nil,
    selectedActions: Field<[DivActionTemplate]>? = nil,
    tooltips: Field<[DivTooltipTemplate]>? = nil,
    transform: Field<DivTransformTemplate>? = nil,
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
    self.background = background
    self.border = border
    self.columnSpan = columnSpan
    self.delimiterStyle = delimiterStyle
    self.disappearActions = disappearActions
    self.doubletapActions = doubletapActions
    self.extensions = extensions
    self.focus = focus
    self.functions = functions
    self.height = height
    self.hoverEndActions = hoverEndActions
    self.hoverStartActions = hoverStartActions
    self.id = id
    self.layoutProvider = layoutProvider
    self.longtapActions = longtapActions
    self.margins = margins
    self.paddings = paddings
    self.pressEndActions = pressEndActions
    self.pressStartActions = pressStartActions
    self.reuseId = reuseId
    self.rowSpan = rowSpan
    self.selectedActions = selectedActions
    self.tooltips = tooltips
    self.transform = transform
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

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivSeparatorTemplate?) -> DeserializationResult<DivSeparator> {
    let accessibilityValue = { parent?.accessibility?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let actionValue = { parent?.action?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let actionAnimationValue = { parent?.actionAnimation?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let actionsValue = { parent?.actions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let alignmentHorizontalValue = { parent?.alignmentHorizontal?.resolveOptionalValue(context: context) ?? .noValue }()
    let alignmentVerticalValue = { parent?.alignmentVertical?.resolveOptionalValue(context: context) ?? .noValue }()
    let alphaValue = { parent?.alpha?.resolveOptionalValue(context: context, validator: ResolvedValue.alphaValidator) ?? .noValue }()
    let animatorsValue = { parent?.animators?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let backgroundValue = { parent?.background?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let borderValue = { parent?.border?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let columnSpanValue = { parent?.columnSpan?.resolveOptionalValue(context: context, validator: ResolvedValue.columnSpanValidator) ?? .noValue }()
    let delimiterStyleValue = { parent?.delimiterStyle?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let disappearActionsValue = { parent?.disappearActions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let doubletapActionsValue = { parent?.doubletapActions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let extensionsValue = { parent?.extensions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let focusValue = { parent?.focus?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let functionsValue = { parent?.functions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let heightValue = { parent?.height?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let hoverEndActionsValue = { parent?.hoverEndActions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let hoverStartActionsValue = { parent?.hoverStartActions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let idValue = { parent?.id?.resolveOptionalValue(context: context) ?? .noValue }()
    let layoutProviderValue = { parent?.layoutProvider?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let longtapActionsValue = { parent?.longtapActions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let marginsValue = { parent?.margins?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let paddingsValue = { parent?.paddings?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let pressEndActionsValue = { parent?.pressEndActions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let pressStartActionsValue = { parent?.pressStartActions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let reuseIdValue = { parent?.reuseId?.resolveOptionalValue(context: context) ?? .noValue }()
    let rowSpanValue = { parent?.rowSpan?.resolveOptionalValue(context: context, validator: ResolvedValue.rowSpanValidator) ?? .noValue }()
    let selectedActionsValue = { parent?.selectedActions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let tooltipsValue = { parent?.tooltips?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let transformValue = { parent?.transform?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
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
      backgroundValue.errorsOrWarnings?.map { .nestedObjectError(field: "background", error: $0) },
      borderValue.errorsOrWarnings?.map { .nestedObjectError(field: "border", error: $0) },
      columnSpanValue.errorsOrWarnings?.map { .nestedObjectError(field: "column_span", error: $0) },
      delimiterStyleValue.errorsOrWarnings?.map { .nestedObjectError(field: "delimiter_style", error: $0) },
      disappearActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "disappear_actions", error: $0) },
      doubletapActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "doubletap_actions", error: $0) },
      extensionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "extensions", error: $0) },
      focusValue.errorsOrWarnings?.map { .nestedObjectError(field: "focus", error: $0) },
      functionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "functions", error: $0) },
      heightValue.errorsOrWarnings?.map { .nestedObjectError(field: "height", error: $0) },
      hoverEndActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "hover_end_actions", error: $0) },
      hoverStartActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "hover_start_actions", error: $0) },
      idValue.errorsOrWarnings?.map { .nestedObjectError(field: "id", error: $0) },
      layoutProviderValue.errorsOrWarnings?.map { .nestedObjectError(field: "layout_provider", error: $0) },
      longtapActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "longtap_actions", error: $0) },
      marginsValue.errorsOrWarnings?.map { .nestedObjectError(field: "margins", error: $0) },
      paddingsValue.errorsOrWarnings?.map { .nestedObjectError(field: "paddings", error: $0) },
      pressEndActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "press_end_actions", error: $0) },
      pressStartActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "press_start_actions", error: $0) },
      reuseIdValue.errorsOrWarnings?.map { .nestedObjectError(field: "reuse_id", error: $0) },
      rowSpanValue.errorsOrWarnings?.map { .nestedObjectError(field: "row_span", error: $0) },
      selectedActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "selected_actions", error: $0) },
      tooltipsValue.errorsOrWarnings?.map { .nestedObjectError(field: "tooltips", error: $0) },
      transformValue.errorsOrWarnings?.map { .nestedObjectError(field: "transform", error: $0) },
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
    let result = DivSeparator(
      accessibility: { accessibilityValue.value }(),
      action: { actionValue.value }(),
      actionAnimation: { actionAnimationValue.value }(),
      actions: { actionsValue.value }(),
      alignmentHorizontal: { alignmentHorizontalValue.value }(),
      alignmentVertical: { alignmentVerticalValue.value }(),
      alpha: { alphaValue.value }(),
      animators: { animatorsValue.value }(),
      background: { backgroundValue.value }(),
      border: { borderValue.value }(),
      columnSpan: { columnSpanValue.value }(),
      delimiterStyle: { delimiterStyleValue.value }(),
      disappearActions: { disappearActionsValue.value }(),
      doubletapActions: { doubletapActionsValue.value }(),
      extensions: { extensionsValue.value }(),
      focus: { focusValue.value }(),
      functions: { functionsValue.value }(),
      height: { heightValue.value }(),
      hoverEndActions: { hoverEndActionsValue.value }(),
      hoverStartActions: { hoverStartActionsValue.value }(),
      id: { idValue.value }(),
      layoutProvider: { layoutProviderValue.value }(),
      longtapActions: { longtapActionsValue.value }(),
      margins: { marginsValue.value }(),
      paddings: { paddingsValue.value }(),
      pressEndActions: { pressEndActionsValue.value }(),
      pressStartActions: { pressStartActionsValue.value }(),
      reuseId: { reuseIdValue.value }(),
      rowSpan: { rowSpanValue.value }(),
      selectedActions: { selectedActionsValue.value }(),
      tooltips: { tooltipsValue.value }(),
      transform: { transformValue.value }(),
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

  public static func resolveValue(context: TemplatesContext, parent: DivSeparatorTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivSeparator> {
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
    var backgroundValue: DeserializationResult<[DivBackground]> = .noValue
    var borderValue: DeserializationResult<DivBorder> = .noValue
    var columnSpanValue: DeserializationResult<Expression<Int>> = { parent?.columnSpan?.value() ?? .noValue }()
    var delimiterStyleValue: DeserializationResult<DivSeparator.DelimiterStyle> = .noValue
    var disappearActionsValue: DeserializationResult<[DivDisappearAction]> = .noValue
    var doubletapActionsValue: DeserializationResult<[DivAction]> = .noValue
    var extensionsValue: DeserializationResult<[DivExtension]> = .noValue
    var focusValue: DeserializationResult<DivFocus> = .noValue
    var functionsValue: DeserializationResult<[DivFunction]> = .noValue
    var heightValue: DeserializationResult<DivSize> = .noValue
    var hoverEndActionsValue: DeserializationResult<[DivAction]> = .noValue
    var hoverStartActionsValue: DeserializationResult<[DivAction]> = .noValue
    var idValue: DeserializationResult<String> = { parent?.id?.value() ?? .noValue }()
    var layoutProviderValue: DeserializationResult<DivLayoutProvider> = .noValue
    var longtapActionsValue: DeserializationResult<[DivAction]> = .noValue
    var marginsValue: DeserializationResult<DivEdgeInsets> = .noValue
    var paddingsValue: DeserializationResult<DivEdgeInsets> = .noValue
    var pressEndActionsValue: DeserializationResult<[DivAction]> = .noValue
    var pressStartActionsValue: DeserializationResult<[DivAction]> = .noValue
    var reuseIdValue: DeserializationResult<Expression<String>> = { parent?.reuseId?.value() ?? .noValue }()
    var rowSpanValue: DeserializationResult<Expression<Int>> = { parent?.rowSpan?.value() ?? .noValue }()
    var selectedActionsValue: DeserializationResult<[DivAction]> = .noValue
    var tooltipsValue: DeserializationResult<[DivTooltip]> = .noValue
    var transformValue: DeserializationResult<DivTransform> = .noValue
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
          if key == "column_span" {
           columnSpanValue = deserialize(__dictValue, validator: ResolvedValue.columnSpanValidator).merged(with: columnSpanValue)
          }
        }()
        _ = {
          if key == "delimiter_style" {
           delimiterStyleValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivSeparatorTemplate.DelimiterStyleTemplate.self).merged(with: delimiterStyleValue)
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
          if key == "selected_actions" {
           selectedActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self).merged(with: selectedActionsValue)
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
         if key == parent?.accessibility?.link {
           accessibilityValue = accessibilityValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivAccessibilityTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.action?.link {
           actionValue = actionValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.actionAnimation?.link {
           actionAnimationValue = actionAnimationValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivAnimationTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.actions?.link {
           actionsValue = actionsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.alignmentHorizontal?.link {
           alignmentHorizontalValue = alignmentHorizontalValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.alignmentVertical?.link {
           alignmentVerticalValue = alignmentVerticalValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.alpha?.link {
           alphaValue = alphaValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.alphaValidator) })
          }
        }()
        _ = {
         if key == parent?.animators?.link {
           animatorsValue = animatorsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivAnimatorTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.background?.link {
           backgroundValue = backgroundValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivBackgroundTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.border?.link {
           borderValue = borderValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivBorderTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.columnSpan?.link {
           columnSpanValue = columnSpanValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.columnSpanValidator) })
          }
        }()
        _ = {
         if key == parent?.delimiterStyle?.link {
           delimiterStyleValue = delimiterStyleValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivSeparatorTemplate.DelimiterStyleTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.disappearActions?.link {
           disappearActionsValue = disappearActionsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivDisappearActionTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.doubletapActions?.link {
           doubletapActionsValue = doubletapActionsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.extensions?.link {
           extensionsValue = extensionsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivExtensionTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.focus?.link {
           focusValue = focusValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivFocusTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.functions?.link {
           functionsValue = functionsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivFunctionTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.height?.link {
           heightValue = heightValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivSizeTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.hoverEndActions?.link {
           hoverEndActionsValue = hoverEndActionsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.hoverStartActions?.link {
           hoverStartActionsValue = hoverStartActionsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.id?.link {
           idValue = idValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.layoutProvider?.link {
           layoutProviderValue = layoutProviderValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivLayoutProviderTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.longtapActions?.link {
           longtapActionsValue = longtapActionsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.margins?.link {
           marginsValue = marginsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivEdgeInsetsTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.paddings?.link {
           paddingsValue = paddingsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivEdgeInsetsTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.pressEndActions?.link {
           pressEndActionsValue = pressEndActionsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.pressStartActions?.link {
           pressStartActionsValue = pressStartActionsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.reuseId?.link {
           reuseIdValue = reuseIdValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.rowSpan?.link {
           rowSpanValue = rowSpanValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.rowSpanValidator) })
          }
        }()
        _ = {
         if key == parent?.selectedActions?.link {
           selectedActionsValue = selectedActionsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.tooltips?.link {
           tooltipsValue = tooltipsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTooltipTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.transform?.link {
           transformValue = transformValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTransformTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.transitionChange?.link {
           transitionChangeValue = transitionChangeValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivChangeTransitionTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.transitionIn?.link {
           transitionInValue = transitionInValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivAppearanceTransitionTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.transitionOut?.link {
           transitionOutValue = transitionOutValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivAppearanceTransitionTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.transitionTriggers?.link {
           transitionTriggersValue = transitionTriggersValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.transitionTriggersValidator) })
          }
        }()
        _ = {
         if key == parent?.variableTriggers?.link {
           variableTriggersValue = variableTriggersValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTriggerTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.variables?.link {
           variablesValue = variablesValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivVariableTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.visibility?.link {
           visibilityValue = visibilityValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.visibilityAction?.link {
           visibilityActionValue = visibilityActionValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivVisibilityActionTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.visibilityActions?.link {
           visibilityActionsValue = visibilityActionsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivVisibilityActionTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.width?.link {
           widthValue = widthValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivSizeTemplate.self) })
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
      _ = { backgroundValue = backgroundValue.merged(with: { parent.background?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { borderValue = borderValue.merged(with: { parent.border?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { delimiterStyleValue = delimiterStyleValue.merged(with: { parent.delimiterStyle?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { disappearActionsValue = disappearActionsValue.merged(with: { parent.disappearActions?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { doubletapActionsValue = doubletapActionsValue.merged(with: { parent.doubletapActions?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { extensionsValue = extensionsValue.merged(with: { parent.extensions?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
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
      backgroundValue.errorsOrWarnings?.map { .nestedObjectError(field: "background", error: $0) },
      borderValue.errorsOrWarnings?.map { .nestedObjectError(field: "border", error: $0) },
      columnSpanValue.errorsOrWarnings?.map { .nestedObjectError(field: "column_span", error: $0) },
      delimiterStyleValue.errorsOrWarnings?.map { .nestedObjectError(field: "delimiter_style", error: $0) },
      disappearActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "disappear_actions", error: $0) },
      doubletapActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "doubletap_actions", error: $0) },
      extensionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "extensions", error: $0) },
      focusValue.errorsOrWarnings?.map { .nestedObjectError(field: "focus", error: $0) },
      functionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "functions", error: $0) },
      heightValue.errorsOrWarnings?.map { .nestedObjectError(field: "height", error: $0) },
      hoverEndActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "hover_end_actions", error: $0) },
      hoverStartActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "hover_start_actions", error: $0) },
      idValue.errorsOrWarnings?.map { .nestedObjectError(field: "id", error: $0) },
      layoutProviderValue.errorsOrWarnings?.map { .nestedObjectError(field: "layout_provider", error: $0) },
      longtapActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "longtap_actions", error: $0) },
      marginsValue.errorsOrWarnings?.map { .nestedObjectError(field: "margins", error: $0) },
      paddingsValue.errorsOrWarnings?.map { .nestedObjectError(field: "paddings", error: $0) },
      pressEndActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "press_end_actions", error: $0) },
      pressStartActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "press_start_actions", error: $0) },
      reuseIdValue.errorsOrWarnings?.map { .nestedObjectError(field: "reuse_id", error: $0) },
      rowSpanValue.errorsOrWarnings?.map { .nestedObjectError(field: "row_span", error: $0) },
      selectedActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "selected_actions", error: $0) },
      tooltipsValue.errorsOrWarnings?.map { .nestedObjectError(field: "tooltips", error: $0) },
      transformValue.errorsOrWarnings?.map { .nestedObjectError(field: "transform", error: $0) },
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
    let result = DivSeparator(
      accessibility: { accessibilityValue.value }(),
      action: { actionValue.value }(),
      actionAnimation: { actionAnimationValue.value }(),
      actions: { actionsValue.value }(),
      alignmentHorizontal: { alignmentHorizontalValue.value }(),
      alignmentVertical: { alignmentVerticalValue.value }(),
      alpha: { alphaValue.value }(),
      animators: { animatorsValue.value }(),
      background: { backgroundValue.value }(),
      border: { borderValue.value }(),
      columnSpan: { columnSpanValue.value }(),
      delimiterStyle: { delimiterStyleValue.value }(),
      disappearActions: { disappearActionsValue.value }(),
      doubletapActions: { doubletapActionsValue.value }(),
      extensions: { extensionsValue.value }(),
      focus: { focusValue.value }(),
      functions: { functionsValue.value }(),
      height: { heightValue.value }(),
      hoverEndActions: { hoverEndActionsValue.value }(),
      hoverStartActions: { hoverStartActionsValue.value }(),
      id: { idValue.value }(),
      layoutProvider: { layoutProviderValue.value }(),
      longtapActions: { longtapActionsValue.value }(),
      margins: { marginsValue.value }(),
      paddings: { paddingsValue.value }(),
      pressEndActions: { pressEndActionsValue.value }(),
      pressStartActions: { pressStartActionsValue.value }(),
      reuseId: { reuseIdValue.value }(),
      rowSpan: { rowSpanValue.value }(),
      selectedActions: { selectedActionsValue.value }(),
      tooltips: { tooltipsValue.value }(),
      transform: { transformValue.value }(),
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

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivSeparatorTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? DivSeparatorTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return DivSeparatorTemplate(
      parent: nil,
      accessibility: accessibility ?? mergedParent.accessibility,
      action: action ?? mergedParent.action,
      actionAnimation: actionAnimation ?? mergedParent.actionAnimation,
      actions: actions ?? mergedParent.actions,
      alignmentHorizontal: alignmentHorizontal ?? mergedParent.alignmentHorizontal,
      alignmentVertical: alignmentVertical ?? mergedParent.alignmentVertical,
      alpha: alpha ?? mergedParent.alpha,
      animators: animators ?? mergedParent.animators,
      background: background ?? mergedParent.background,
      border: border ?? mergedParent.border,
      columnSpan: columnSpan ?? mergedParent.columnSpan,
      delimiterStyle: delimiterStyle ?? mergedParent.delimiterStyle,
      disappearActions: disappearActions ?? mergedParent.disappearActions,
      doubletapActions: doubletapActions ?? mergedParent.doubletapActions,
      extensions: extensions ?? mergedParent.extensions,
      focus: focus ?? mergedParent.focus,
      functions: functions ?? mergedParent.functions,
      height: height ?? mergedParent.height,
      hoverEndActions: hoverEndActions ?? mergedParent.hoverEndActions,
      hoverStartActions: hoverStartActions ?? mergedParent.hoverStartActions,
      id: id ?? mergedParent.id,
      layoutProvider: layoutProvider ?? mergedParent.layoutProvider,
      longtapActions: longtapActions ?? mergedParent.longtapActions,
      margins: margins ?? mergedParent.margins,
      paddings: paddings ?? mergedParent.paddings,
      pressEndActions: pressEndActions ?? mergedParent.pressEndActions,
      pressStartActions: pressStartActions ?? mergedParent.pressStartActions,
      reuseId: reuseId ?? mergedParent.reuseId,
      rowSpan: rowSpan ?? mergedParent.rowSpan,
      selectedActions: selectedActions ?? mergedParent.selectedActions,
      tooltips: tooltips ?? mergedParent.tooltips,
      transform: transform ?? mergedParent.transform,
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

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivSeparatorTemplate {
    let merged = try mergedWithParent(templates: templates)

    return DivSeparatorTemplate(
      parent: nil,
      accessibility: merged.accessibility?.tryResolveParent(templates: templates),
      action: merged.action?.tryResolveParent(templates: templates),
      actionAnimation: merged.actionAnimation?.tryResolveParent(templates: templates),
      actions: merged.actions?.tryResolveParent(templates: templates),
      alignmentHorizontal: merged.alignmentHorizontal,
      alignmentVertical: merged.alignmentVertical,
      alpha: merged.alpha,
      animators: merged.animators?.tryResolveParent(templates: templates),
      background: merged.background?.tryResolveParent(templates: templates),
      border: merged.border?.tryResolveParent(templates: templates),
      columnSpan: merged.columnSpan,
      delimiterStyle: merged.delimiterStyle?.tryResolveParent(templates: templates),
      disappearActions: merged.disappearActions?.tryResolveParent(templates: templates),
      doubletapActions: merged.doubletapActions?.tryResolveParent(templates: templates),
      extensions: merged.extensions?.tryResolveParent(templates: templates),
      focus: merged.focus?.tryResolveParent(templates: templates),
      functions: merged.functions?.tryResolveParent(templates: templates),
      height: merged.height?.tryResolveParent(templates: templates),
      hoverEndActions: merged.hoverEndActions?.tryResolveParent(templates: templates),
      hoverStartActions: merged.hoverStartActions?.tryResolveParent(templates: templates),
      id: merged.id,
      layoutProvider: merged.layoutProvider?.tryResolveParent(templates: templates),
      longtapActions: merged.longtapActions?.tryResolveParent(templates: templates),
      margins: merged.margins?.tryResolveParent(templates: templates),
      paddings: merged.paddings?.tryResolveParent(templates: templates),
      pressEndActions: merged.pressEndActions?.tryResolveParent(templates: templates),
      pressStartActions: merged.pressStartActions?.tryResolveParent(templates: templates),
      reuseId: merged.reuseId,
      rowSpan: merged.rowSpan,
      selectedActions: merged.selectedActions?.tryResolveParent(templates: templates),
      tooltips: merged.tooltips?.tryResolveParent(templates: templates),
      transform: merged.transform?.tryResolveParent(templates: templates),
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
