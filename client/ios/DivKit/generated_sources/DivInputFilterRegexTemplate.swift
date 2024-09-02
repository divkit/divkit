// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivInputFilterRegexTemplate: TemplateValue {
  public static let type: String = "regex"
  public let parent: String?
  public let pattern: Field<Expression<String>>?

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      parent: dictionary["type"] as? String,
      pattern: dictionary.getOptionalExpressionField("pattern")
    )
  }

  init(
    parent: String?,
    pattern: Field<Expression<String>>? = nil
  ) {
    self.parent = parent
    self.pattern = pattern
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivInputFilterRegexTemplate?) -> DeserializationResult<DivInputFilterRegex> {
    let patternValue = parent?.pattern?.resolveValue(context: context) ?? .noValue
    var errors = mergeErrors(
      patternValue.errorsOrWarnings?.map { .nestedObjectError(field: "pattern", error: $0) }
    )
    if case .noValue = patternValue {
      errors.append(.requiredFieldIsMissing(field: "pattern"))
    }
    guard
      let patternNonNil = patternValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivInputFilterRegex(
      pattern: patternNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivInputFilterRegexTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivInputFilterRegex> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var patternValue: DeserializationResult<Expression<String>> = parent?.pattern?.value() ?? .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "pattern":
        patternValue = deserialize(__dictValue).merged(with: patternValue)
      case parent?.pattern?.link:
        patternValue = patternValue.merged(with: { deserialize(__dictValue) })
      default: break
      }
    }
    var errors = mergeErrors(
      patternValue.errorsOrWarnings?.map { .nestedObjectError(field: "pattern", error: $0) }
    )
    if case .noValue = patternValue {
      errors.append(.requiredFieldIsMissing(field: "pattern"))
    }
    guard
      let patternNonNil = patternValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivInputFilterRegex(
      pattern: patternNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivInputFilterRegexTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? DivInputFilterRegexTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return DivInputFilterRegexTemplate(
      parent: nil,
      pattern: pattern ?? mergedParent.pattern
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivInputFilterRegexTemplate {
    return try mergedWithParent(templates: templates)
  }
}
