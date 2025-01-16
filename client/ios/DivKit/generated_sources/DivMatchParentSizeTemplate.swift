// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivMatchParentSizeTemplate: TemplateValue, Sendable {
  public static let type: String = "match_parent"
  public let parent: String?
  public let weight: Field<Expression<Double>>? // constraint: number > 0

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      parent: dictionary["type"] as? String,
      weight: dictionary.getOptionalExpressionField("weight")
    )
  }

  init(
    parent: String?,
    weight: Field<Expression<Double>>? = nil
  ) {
    self.parent = parent
    self.weight = weight
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivMatchParentSizeTemplate?) -> DeserializationResult<DivMatchParentSize> {
    let weightValue = { parent?.weight?.resolveOptionalValue(context: context, validator: ResolvedValue.weightValidator) ?? .noValue }()
    let errors = mergeErrors(
      weightValue.errorsOrWarnings?.map { .nestedObjectError(field: "weight", error: $0) }
    )
    let result = DivMatchParentSize(
      weight: { weightValue.value }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivMatchParentSizeTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivMatchParentSize> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var weightValue: DeserializationResult<Expression<Double>> = { parent?.weight?.value() ?? .noValue }()
    _ = {
      // Each field is parsed in its own lambda to keep the stack size managable
      // Otherwise the compiler will allocate stack for each intermediate variable
      // upfront even when we don't actually visit a relevant branch
      for (key, __dictValue) in context.templateData {
        _ = {
          if key == "weight" {
           weightValue = deserialize(__dictValue, validator: ResolvedValue.weightValidator).merged(with: weightValue)
          }
        }()
        _ = {
         if key == parent?.weight?.link {
           weightValue = weightValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.weightValidator) })
          }
        }()
      }
    }()
    let errors = mergeErrors(
      weightValue.errorsOrWarnings?.map { .nestedObjectError(field: "weight", error: $0) }
    )
    let result = DivMatchParentSize(
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
      weight: weight ?? mergedParent.weight
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivMatchParentSizeTemplate {
    return try mergedWithParent(templates: templates)
  }
}
