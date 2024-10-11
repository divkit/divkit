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
    let patternValue = { parent?.pattern?.resolveValue(context: context) ?? .noValue }()
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
      pattern: { patternNonNil }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivInputFilterRegexTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivInputFilterRegex> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var patternValue: DeserializationResult<Expression<String>> = { parent?.pattern?.value() ?? .noValue }()
    _ = {
      // Each field is parsed in its own lambda to keep the stack size managable
      // Otherwise the compiler will allocate stack for each intermediate variable
      // upfront even when we don't actually visit a relevant branch
      for (key, __dictValue) in context.templateData {
        _ = {
          if key == "pattern" {
           patternValue = deserialize(__dictValue).merged(with: patternValue)
          }
        }()
        _ = {
         if key == parent?.pattern?.link {
           patternValue = patternValue.merged(with: { deserialize(__dictValue) })
          }
        }()
      }
    }()
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
      pattern: { patternNonNil }()
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
