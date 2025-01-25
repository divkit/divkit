// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivInputFilterExpressionTemplate: TemplateValue {
  public static let type: String = "expression"
  public let parent: String?
  public let condition: Field<Expression<Bool>>?

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      parent: dictionary["type"] as? String,
      condition: dictionary.getOptionalExpressionField("condition")
    )
  }

  init(
    parent: String?,
    condition: Field<Expression<Bool>>? = nil
  ) {
    self.parent = parent
    self.condition = condition
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivInputFilterExpressionTemplate?) -> DeserializationResult<DivInputFilterExpression> {
    let conditionValue = { parent?.condition?.resolveValue(context: context) ?? .noValue }()
    var errors = mergeErrors(
      conditionValue.errorsOrWarnings?.map { .nestedObjectError(field: "condition", error: $0) }
    )
    if case .noValue = conditionValue {
      errors.append(.requiredFieldIsMissing(field: "condition"))
    }
    guard
      let conditionNonNil = conditionValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivInputFilterExpression(
      condition: { conditionNonNil }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivInputFilterExpressionTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivInputFilterExpression> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var conditionValue: DeserializationResult<Expression<Bool>> = { parent?.condition?.value() ?? .noValue }()
    _ = {
      // Each field is parsed in its own lambda to keep the stack size managable
      // Otherwise the compiler will allocate stack for each intermediate variable
      // upfront even when we don't actually visit a relevant branch
      for (key, __dictValue) in context.templateData {
        _ = {
          if key == "condition" {
           conditionValue = deserialize(__dictValue).merged(with: conditionValue)
          }
        }()
        _ = {
         if key == parent?.condition?.link {
           conditionValue = conditionValue.merged(with: { deserialize(__dictValue) })
          }
        }()
      }
    }()
    var errors = mergeErrors(
      conditionValue.errorsOrWarnings?.map { .nestedObjectError(field: "condition", error: $0) }
    )
    if case .noValue = conditionValue {
      errors.append(.requiredFieldIsMissing(field: "condition"))
    }
    guard
      let conditionNonNil = conditionValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivInputFilterExpression(
      condition: { conditionNonNil }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivInputFilterExpressionTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? DivInputFilterExpressionTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return DivInputFilterExpressionTemplate(
      parent: nil,
      condition: condition ?? mergedParent.condition
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivInputFilterExpressionTemplate {
    return try mergedWithParent(templates: templates)
  }
}
