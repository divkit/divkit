// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivFocusTemplate: TemplateValue {
  public final class NextFocusIdsTemplate: TemplateValue {
    public let down: Field<Expression<String>>? // at least 1 char
    public let forward: Field<Expression<String>>? // at least 1 char
    public let left: Field<Expression<String>>? // at least 1 char
    public let right: Field<Expression<String>>? // at least 1 char
    public let up: Field<Expression<String>>? // at least 1 char

    public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
      self.init(
        down: try dictionary.getOptionalExpressionField("down"),
        forward: try dictionary.getOptionalExpressionField("forward"),
        left: try dictionary.getOptionalExpressionField("left"),
        right: try dictionary.getOptionalExpressionField("right"),
        up: try dictionary.getOptionalExpressionField("up")
      )
    }

    init(
      down: Field<Expression<String>>? = nil,
      forward: Field<Expression<String>>? = nil,
      left: Field<Expression<String>>? = nil,
      right: Field<Expression<String>>? = nil,
      up: Field<Expression<String>>? = nil
    ) {
      self.down = down
      self.forward = forward
      self.left = left
      self.right = right
      self.up = up
    }

    private static func resolveOnlyLinks(context: TemplatesContext, parent: NextFocusIdsTemplate?) -> DeserializationResult<DivFocus.NextFocusIds> {
      let downValue = parent?.down?.resolveOptionalValue(context: context, validator: ResolvedValue.downValidator) ?? .noValue
      let forwardValue = parent?.forward?.resolveOptionalValue(context: context, validator: ResolvedValue.forwardValidator) ?? .noValue
      let leftValue = parent?.left?.resolveOptionalValue(context: context, validator: ResolvedValue.leftValidator) ?? .noValue
      let rightValue = parent?.right?.resolveOptionalValue(context: context, validator: ResolvedValue.rightValidator) ?? .noValue
      let upValue = parent?.up?.resolveOptionalValue(context: context, validator: ResolvedValue.upValidator) ?? .noValue
      let errors = mergeErrors(
        downValue.errorsOrWarnings?.map { .nestedObjectError(field: "down", error: $0) },
        forwardValue.errorsOrWarnings?.map { .nestedObjectError(field: "forward", error: $0) },
        leftValue.errorsOrWarnings?.map { .nestedObjectError(field: "left", error: $0) },
        rightValue.errorsOrWarnings?.map { .nestedObjectError(field: "right", error: $0) },
        upValue.errorsOrWarnings?.map { .nestedObjectError(field: "up", error: $0) }
      )
      let result = DivFocus.NextFocusIds(
        down: downValue.value,
        forward: forwardValue.value,
        left: leftValue.value,
        right: rightValue.value,
        up: upValue.value
      )
      return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
    }

    public static func resolveValue(context: TemplatesContext, parent: NextFocusIdsTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivFocus.NextFocusIds> {
      if useOnlyLinks {
        return resolveOnlyLinks(context: context, parent: parent)
      }
      var downValue: DeserializationResult<Expression<String>> = parent?.down?.value() ?? .noValue
      var forwardValue: DeserializationResult<Expression<String>> = parent?.forward?.value() ?? .noValue
      var leftValue: DeserializationResult<Expression<String>> = parent?.left?.value() ?? .noValue
      var rightValue: DeserializationResult<Expression<String>> = parent?.right?.value() ?? .noValue
      var upValue: DeserializationResult<Expression<String>> = parent?.up?.value() ?? .noValue
      context.templateData.forEach { key, __dictValue in
        switch key {
        case "down":
          downValue = deserialize(__dictValue, validator: ResolvedValue.downValidator).merged(with: downValue)
        case "forward":
          forwardValue = deserialize(__dictValue, validator: ResolvedValue.forwardValidator).merged(with: forwardValue)
        case "left":
          leftValue = deserialize(__dictValue, validator: ResolvedValue.leftValidator).merged(with: leftValue)
        case "right":
          rightValue = deserialize(__dictValue, validator: ResolvedValue.rightValidator).merged(with: rightValue)
        case "up":
          upValue = deserialize(__dictValue, validator: ResolvedValue.upValidator).merged(with: upValue)
        case parent?.down?.link:
          downValue = downValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.downValidator))
        case parent?.forward?.link:
          forwardValue = forwardValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.forwardValidator))
        case parent?.left?.link:
          leftValue = leftValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.leftValidator))
        case parent?.right?.link:
          rightValue = rightValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.rightValidator))
        case parent?.up?.link:
          upValue = upValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.upValidator))
        default: break
        }
      }
      let errors = mergeErrors(
        downValue.errorsOrWarnings?.map { .nestedObjectError(field: "down", error: $0) },
        forwardValue.errorsOrWarnings?.map { .nestedObjectError(field: "forward", error: $0) },
        leftValue.errorsOrWarnings?.map { .nestedObjectError(field: "left", error: $0) },
        rightValue.errorsOrWarnings?.map { .nestedObjectError(field: "right", error: $0) },
        upValue.errorsOrWarnings?.map { .nestedObjectError(field: "up", error: $0) }
      )
      let result = DivFocus.NextFocusIds(
        down: downValue.value,
        forward: forwardValue.value,
        left: leftValue.value,
        right: rightValue.value,
        up: upValue.value
      )
      return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
    }

    private func mergedWithParent(templates: [TemplateName: Any]) throws -> NextFocusIdsTemplate {
      return self
    }

    public func resolveParent(templates: [TemplateName: Any]) throws -> NextFocusIdsTemplate {
      return try mergedWithParent(templates: templates)
    }
  }

  public let background: Field<[DivBackgroundTemplate]>? // at least 1 elements
  public let border: Field<DivBorderTemplate>?
  public let nextFocusIds: Field<NextFocusIdsTemplate>?
  public let onBlur: Field<[DivActionTemplate]>? // at least 1 elements
  public let onFocus: Field<[DivActionTemplate]>? // at least 1 elements

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      background: try dictionary.getOptionalArray("background", templateToType: templateToType),
      border: try dictionary.getOptionalField("border", templateToType: templateToType),
      nextFocusIds: try dictionary.getOptionalField("next_focus_ids", templateToType: templateToType),
      onBlur: try dictionary.getOptionalArray("on_blur", templateToType: templateToType),
      onFocus: try dictionary.getOptionalArray("on_focus", templateToType: templateToType)
    )
  }

  init(
    background: Field<[DivBackgroundTemplate]>? = nil,
    border: Field<DivBorderTemplate>? = nil,
    nextFocusIds: Field<NextFocusIdsTemplate>? = nil,
    onBlur: Field<[DivActionTemplate]>? = nil,
    onFocus: Field<[DivActionTemplate]>? = nil
  ) {
    self.background = background
    self.border = border
    self.nextFocusIds = nextFocusIds
    self.onBlur = onBlur
    self.onFocus = onFocus
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivFocusTemplate?) -> DeserializationResult<DivFocus> {
    let backgroundValue = parent?.background?.resolveOptionalValue(context: context, validator: ResolvedValue.backgroundValidator, useOnlyLinks: true) ?? .noValue
    let borderValue = parent?.border?.resolveOptionalValue(context: context, validator: ResolvedValue.borderValidator, useOnlyLinks: true) ?? .noValue
    let nextFocusIdsValue = parent?.nextFocusIds?.resolveOptionalValue(context: context, validator: ResolvedValue.nextFocusIdsValidator, useOnlyLinks: true) ?? .noValue
    let onBlurValue = parent?.onBlur?.resolveOptionalValue(context: context, validator: ResolvedValue.onBlurValidator, useOnlyLinks: true) ?? .noValue
    let onFocusValue = parent?.onFocus?.resolveOptionalValue(context: context, validator: ResolvedValue.onFocusValidator, useOnlyLinks: true) ?? .noValue
    let errors = mergeErrors(
      backgroundValue.errorsOrWarnings?.map { .nestedObjectError(field: "background", error: $0) },
      borderValue.errorsOrWarnings?.map { .nestedObjectError(field: "border", error: $0) },
      nextFocusIdsValue.errorsOrWarnings?.map { .nestedObjectError(field: "next_focus_ids", error: $0) },
      onBlurValue.errorsOrWarnings?.map { .nestedObjectError(field: "on_blur", error: $0) },
      onFocusValue.errorsOrWarnings?.map { .nestedObjectError(field: "on_focus", error: $0) }
    )
    let result = DivFocus(
      background: backgroundValue.value,
      border: borderValue.value,
      nextFocusIds: nextFocusIdsValue.value,
      onBlur: onBlurValue.value,
      onFocus: onFocusValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivFocusTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivFocus> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var backgroundValue: DeserializationResult<[DivBackground]> = .noValue
    var borderValue: DeserializationResult<DivBorder> = .noValue
    var nextFocusIdsValue: DeserializationResult<DivFocus.NextFocusIds> = .noValue
    var onBlurValue: DeserializationResult<[DivAction]> = .noValue
    var onFocusValue: DeserializationResult<[DivAction]> = .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "background":
        backgroundValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.backgroundValidator, type: DivBackgroundTemplate.self).merged(with: backgroundValue)
      case "border":
        borderValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.borderValidator, type: DivBorderTemplate.self).merged(with: borderValue)
      case "next_focus_ids":
        nextFocusIdsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.nextFocusIdsValidator, type: DivFocusTemplate.NextFocusIdsTemplate.self).merged(with: nextFocusIdsValue)
      case "on_blur":
        onBlurValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.onBlurValidator, type: DivActionTemplate.self).merged(with: onBlurValue)
      case "on_focus":
        onFocusValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.onFocusValidator, type: DivActionTemplate.self).merged(with: onFocusValue)
      case parent?.background?.link:
        backgroundValue = backgroundValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.backgroundValidator, type: DivBackgroundTemplate.self))
      case parent?.border?.link:
        borderValue = borderValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.borderValidator, type: DivBorderTemplate.self))
      case parent?.nextFocusIds?.link:
        nextFocusIdsValue = nextFocusIdsValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.nextFocusIdsValidator, type: DivFocusTemplate.NextFocusIdsTemplate.self))
      case parent?.onBlur?.link:
        onBlurValue = onBlurValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.onBlurValidator, type: DivActionTemplate.self))
      case parent?.onFocus?.link:
        onFocusValue = onFocusValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.onFocusValidator, type: DivActionTemplate.self))
      default: break
      }
    }
    if let parent = parent {
      backgroundValue = backgroundValue.merged(with: parent.background?.resolveOptionalValue(context: context, validator: ResolvedValue.backgroundValidator, useOnlyLinks: true))
      borderValue = borderValue.merged(with: parent.border?.resolveOptionalValue(context: context, validator: ResolvedValue.borderValidator, useOnlyLinks: true))
      nextFocusIdsValue = nextFocusIdsValue.merged(with: parent.nextFocusIds?.resolveOptionalValue(context: context, validator: ResolvedValue.nextFocusIdsValidator, useOnlyLinks: true))
      onBlurValue = onBlurValue.merged(with: parent.onBlur?.resolveOptionalValue(context: context, validator: ResolvedValue.onBlurValidator, useOnlyLinks: true))
      onFocusValue = onFocusValue.merged(with: parent.onFocus?.resolveOptionalValue(context: context, validator: ResolvedValue.onFocusValidator, useOnlyLinks: true))
    }
    let errors = mergeErrors(
      backgroundValue.errorsOrWarnings?.map { .nestedObjectError(field: "background", error: $0) },
      borderValue.errorsOrWarnings?.map { .nestedObjectError(field: "border", error: $0) },
      nextFocusIdsValue.errorsOrWarnings?.map { .nestedObjectError(field: "next_focus_ids", error: $0) },
      onBlurValue.errorsOrWarnings?.map { .nestedObjectError(field: "on_blur", error: $0) },
      onFocusValue.errorsOrWarnings?.map { .nestedObjectError(field: "on_focus", error: $0) }
    )
    let result = DivFocus(
      background: backgroundValue.value,
      border: borderValue.value,
      nextFocusIds: nextFocusIdsValue.value,
      onBlur: onBlurValue.value,
      onFocus: onFocusValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivFocusTemplate {
    return self
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivFocusTemplate {
    let merged = try mergedWithParent(templates: templates)

    return DivFocusTemplate(
      background: merged.background?.tryResolveParent(templates: templates),
      border: merged.border?.tryResolveParent(templates: templates),
      nextFocusIds: merged.nextFocusIds?.tryResolveParent(templates: templates),
      onBlur: merged.onBlur?.tryResolveParent(templates: templates),
      onFocus: merged.onFocus?.tryResolveParent(templates: templates)
    )
  }
}
