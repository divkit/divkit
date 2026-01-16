// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivMatchParentSizeTemplate: TemplateValue, Sendable {
  public static let type: String = "match_parent"
  public let parent: String?
  public let maxSize: Field<DivSizeUnitValueTemplate>?
  public let minSize: Field<DivSizeUnitValueTemplate>?
  public let weight: Field<Expression<Double>>? // constraint: number > 0

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      parent: dictionary["type"] as? String,
      maxSize: dictionary.getOptionalField("max_size", templateToType: templateToType),
      minSize: dictionary.getOptionalField("min_size", templateToType: templateToType),
      weight: dictionary.getOptionalExpressionField("weight")
    )
  }

  init(
    parent: String?,
    maxSize: Field<DivSizeUnitValueTemplate>? = nil,
    minSize: Field<DivSizeUnitValueTemplate>? = nil,
    weight: Field<Expression<Double>>? = nil
  ) {
    self.parent = parent
    self.maxSize = maxSize
    self.minSize = minSize
    self.weight = weight
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivMatchParentSizeTemplate?) -> DeserializationResult<DivMatchParentSize> {
    let maxSizeValue = { parent?.maxSize?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let minSizeValue = { parent?.minSize?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let weightValue = { parent?.weight?.resolveOptionalValue(context: context, validator: ResolvedValue.weightValidator) ?? .noValue }()
    let errors = mergeErrors(
      maxSizeValue.errorsOrWarnings?.map { .nestedObjectError(field: "max_size", error: $0) },
      minSizeValue.errorsOrWarnings?.map { .nestedObjectError(field: "min_size", error: $0) },
      weightValue.errorsOrWarnings?.map { .nestedObjectError(field: "weight", error: $0) }
    )
    let result = DivMatchParentSize(
      maxSize: { maxSizeValue.value }(),
      minSize: { minSizeValue.value }(),
      weight: { weightValue.value }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivMatchParentSizeTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivMatchParentSize> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var maxSizeValue: DeserializationResult<DivSizeUnitValue> = .noValue
    var minSizeValue: DeserializationResult<DivSizeUnitValue> = .noValue
    var weightValue: DeserializationResult<Expression<Double>> = { parent?.weight?.value() ?? .noValue }()
    _ = {
      // Each field is parsed in its own lambda to keep the stack size managable
      // Otherwise the compiler will allocate stack for each intermediate variable
      // upfront even when we don't actually visit a relevant branch
      for (key, __dictValue) in context.templateData {
        _ = {
          if key == "max_size" {
           maxSizeValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivSizeUnitValueTemplate.self).merged(with: maxSizeValue)
          }
        }()
        _ = {
          if key == "min_size" {
           minSizeValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivSizeUnitValueTemplate.self).merged(with: minSizeValue)
          }
        }()
        _ = {
          if key == "weight" {
           weightValue = deserialize(__dictValue, validator: ResolvedValue.weightValidator).merged(with: weightValue)
          }
        }()
        _ = {
         if key == parent?.maxSize?.link {
           maxSizeValue = maxSizeValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivSizeUnitValueTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.minSize?.link {
           minSizeValue = minSizeValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivSizeUnitValueTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.weight?.link {
           weightValue = weightValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.weightValidator) })
          }
        }()
      }
    }()
    if let parent = parent {
      _ = { maxSizeValue = maxSizeValue.merged(with: { parent.maxSize?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { minSizeValue = minSizeValue.merged(with: { parent.minSize?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
    }
    let errors = mergeErrors(
      maxSizeValue.errorsOrWarnings?.map { .nestedObjectError(field: "max_size", error: $0) },
      minSizeValue.errorsOrWarnings?.map { .nestedObjectError(field: "min_size", error: $0) },
      weightValue.errorsOrWarnings?.map { .nestedObjectError(field: "weight", error: $0) }
    )
    let result = DivMatchParentSize(
      maxSize: { maxSizeValue.value }(),
      minSize: { minSizeValue.value }(),
      weight: { weightValue.value }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivMatchParentSizeTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? DivMatchParentSizeTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return DivMatchParentSizeTemplate(
      parent: nil,
      maxSize: maxSize ?? mergedParent.maxSize,
      minSize: minSize ?? mergedParent.minSize,
      weight: weight ?? mergedParent.weight
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivMatchParentSizeTemplate {
    let merged = try mergedWithParent(templates: templates)

    return DivMatchParentSizeTemplate(
      parent: nil,
      maxSize: merged.maxSize?.tryResolveParent(templates: templates),
      minSize: merged.minSize?.tryResolveParent(templates: templates),
      weight: merged.weight
    )
  }
}
