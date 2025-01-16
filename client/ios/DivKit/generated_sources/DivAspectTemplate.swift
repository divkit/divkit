// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivAspectTemplate: TemplateValue, Sendable {
  public let ratio: Field<Expression<Double>>? // constraint: number > 0

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      ratio: dictionary.getOptionalExpressionField("ratio")
    )
  }

  init(
    ratio: Field<Expression<Double>>? = nil
  ) {
    self.ratio = ratio
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivAspectTemplate?) -> DeserializationResult<DivAspect> {
    let ratioValue = { parent?.ratio?.resolveValue(context: context, validator: ResolvedValue.ratioValidator) ?? .noValue }()
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
      ratio: { ratioNonNil }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivAspectTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivAspect> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var ratioValue: DeserializationResult<Expression<Double>> = { parent?.ratio?.value() ?? .noValue }()
    _ = {
      // Each field is parsed in its own lambda to keep the stack size managable
      // Otherwise the compiler will allocate stack for each intermediate variable
      // upfront even when we don't actually visit a relevant branch
      for (key, __dictValue) in context.templateData {
        _ = {
          if key == "ratio" {
           ratioValue = deserialize(__dictValue, validator: ResolvedValue.ratioValidator).merged(with: ratioValue)
          }
        }()
        _ = {
         if key == parent?.ratio?.link {
           ratioValue = ratioValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.ratioValidator) })
          }
        }()
      }
    }()
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
      ratio: { ratioNonNil }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivAspectTemplate {
    return self
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivAspectTemplate {
    return try mergedWithParent(templates: templates)
  }
}
