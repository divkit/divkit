// Generated code. Do not modify.

import CommonCore
import Foundation
import Serialization
import TemplatesSupport

public final class DivAspectTemplate: TemplateValue, TemplateDeserializable {
  public let ratio: Field<Expression<Double>>? // constraint: number > 0

  public convenience init(dictionary: [String: Any], templateToType: TemplateToType) throws {
    do {
      self.init(
        ratio: try dictionary.getOptionalExpressionField("ratio")
      )
    } catch let DeserializationError.invalidFieldRepresentation(field: field, representation: representation) {
      throw DeserializationError.invalidFieldRepresentation(field: "div-aspect_template." + field, representation: representation)
    }
  }

  init(
    ratio: Field<Expression<Double>>? = nil
  ) {
    self.ratio = ratio
  }

  private static func resolveOnlyLinks(context: Context, parent: DivAspectTemplate?) -> DeserializationResult<DivAspect> {
    let ratioValue = parent?.ratio?.resolveValue(context: context, validator: ResolvedValue.ratioValidator) ?? .noValue
    var errors = mergeErrors(
      ratioValue.errorsOrWarnings?.map { .nestedObjectError(field: "ratio", error: $0) }
    )
    if case .noValue = ratioValue {
      errors.append(.requiredFieldIsMissing(field: "ratio"))
    }
    guard
      let ratioNonNil = ratioValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivAspect(
      ratio: ratioNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: Context, parent: DivAspectTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivAspect> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var ratioValue: DeserializationResult<Expression<Double>> = parent?.ratio?.value() ?? .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "ratio":
        ratioValue = deserialize(__dictValue, validator: ResolvedValue.ratioValidator).merged(with: ratioValue)
      case parent?.ratio?.link:
        ratioValue = ratioValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.ratioValidator))
      default: break
      }
    }
    var errors = mergeErrors(
      ratioValue.errorsOrWarnings?.map { .nestedObjectError(field: "ratio", error: $0) }
    )
    if case .noValue = ratioValue {
      errors.append(.requiredFieldIsMissing(field: "ratio"))
    }
    guard
      let ratioNonNil = ratioValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivAspect(
      ratio: ratioNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: Templates) throws -> DivAspectTemplate {
    return self
  }

  public func resolveParent(templates: Templates) throws -> DivAspectTemplate {
    return try mergedWithParent(templates: templates)
  }
}
