// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivWrapContentSizeTemplate: TemplateValue, Sendable {
  public static let type: String = "wrap_content"
  public let parent: String?
  public let constrained: Field<Expression<Bool>>?
  public let maxSize: Field<DivSizeUnitValueTemplate>?
  public let minSize: Field<DivSizeUnitValueTemplate>?

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      parent: dictionary["type"] as? String,
      constrained: dictionary.getOptionalExpressionField("constrained"),
      maxSize: dictionary.getOptionalField("max_size", templateToType: templateToType),
      minSize: dictionary.getOptionalField("min_size", templateToType: templateToType)
    )
  }

  init(
    parent: String?,
    constrained: Field<Expression<Bool>>? = nil,
    maxSize: Field<DivSizeUnitValueTemplate>? = nil,
    minSize: Field<DivSizeUnitValueTemplate>? = nil
  ) {
    self.parent = parent
    self.constrained = constrained
    self.maxSize = maxSize
    self.minSize = minSize
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivWrapContentSizeTemplate?) -> DeserializationResult<DivWrapContentSize> {
    let constrainedValue = parent?.constrained?.resolveOptionalValue(context: context) ?? .noValue
    let maxSizeValue = parent?.maxSize?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let minSizeValue = parent?.minSize?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let errors = mergeErrors(
      constrainedValue.errorsOrWarnings?.map { .nestedObjectError(field: "constrained", error: $0) },
      maxSizeValue.errorsOrWarnings?.map { .nestedObjectError(field: "max_size", error: $0) },
      minSizeValue.errorsOrWarnings?.map { .nestedObjectError(field: "min_size", error: $0) }
    )
    let result = DivWrapContentSize(
      constrained: constrainedValue.value,
      maxSize: maxSizeValue.value,
      minSize: minSizeValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivWrapContentSizeTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivWrapContentSize> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var constrainedValue: DeserializationResult<Expression<Bool>> = parent?.constrained?.value() ?? .noValue
    var maxSizeValue: DeserializationResult<DivSizeUnitValue> = .noValue
    var minSizeValue: DeserializationResult<DivSizeUnitValue> = .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "constrained":
        constrainedValue = deserialize(__dictValue).merged(with: constrainedValue)
      case "max_size":
        maxSizeValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivSizeUnitValueTemplate.self).merged(with: maxSizeValue)
      case "min_size":
        minSizeValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivSizeUnitValueTemplate.self).merged(with: minSizeValue)
      case parent?.constrained?.link:
        constrainedValue = constrainedValue.merged(with: { deserialize(__dictValue) })
      case parent?.maxSize?.link:
        maxSizeValue = maxSizeValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivSizeUnitValueTemplate.self) })
      case parent?.minSize?.link:
        minSizeValue = minSizeValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivSizeUnitValueTemplate.self) })
      default: break
      }
    }
    if let parent = parent {
      _ = maxSizeValue = maxSizeValue.merged(with: { parent.maxSize?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      _ = minSizeValue = minSizeValue.merged(with: { parent.minSize?.resolveOptionalValue(context: context, useOnlyLinks: true) })
    }
    let errors = mergeErrors(
      constrainedValue.errorsOrWarnings?.map { .nestedObjectError(field: "constrained", error: $0) },
      maxSizeValue.errorsOrWarnings?.map { .nestedObjectError(field: "max_size", error: $0) },
      minSizeValue.errorsOrWarnings?.map { .nestedObjectError(field: "min_size", error: $0) }
    )
    let result = DivWrapContentSize(
      constrained: constrainedValue.value,
      maxSize: maxSizeValue.value,
      minSize: minSizeValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivWrapContentSizeTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? DivWrapContentSizeTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return DivWrapContentSizeTemplate(
      parent: nil,
      constrained: constrained ?? mergedParent.constrained,
      maxSize: maxSize ?? mergedParent.maxSize,
      minSize: minSize ?? mergedParent.minSize
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivWrapContentSizeTemplate {
    let merged = try mergedWithParent(templates: templates)

    return DivWrapContentSizeTemplate(
      parent: nil,
      constrained: merged.constrained,
      maxSize: merged.maxSize?.tryResolveParent(templates: templates),
      minSize: merged.minSize?.tryResolveParent(templates: templates)
    )
  }
}
