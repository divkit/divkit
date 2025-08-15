// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class ColorValueTemplate: TemplateValue, Sendable {
  public static let type: String = "color"
  public let parent: String?
  public let value: Field<Expression<Color>>?

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      parent: dictionary["type"] as? String,
      value: dictionary.getOptionalExpressionField("value", transform: Color.color(withHexString:))
    )
  }

  init(
    parent: String?,
    value: Field<Expression<Color>>? = nil
  ) {
    self.parent = parent
    self.value = value
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: ColorValueTemplate?) -> DeserializationResult<ColorValue> {
    let valueValue = { parent?.value?.resolveValue(context: context, transform: Color.color(withHexString:)) ?? .noValue }()
    var errors = mergeErrors(
      valueValue.errorsOrWarnings?.map { .nestedObjectError(field: "value", error: $0) }
    )
    if case .noValue = valueValue {
      errors.append(.requiredFieldIsMissing(field: "value"))
    }
    guard
      let valueNonNil = valueValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = ColorValue(
      value: { valueNonNil }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: ColorValueTemplate?, useOnlyLinks: Bool) -> DeserializationResult<ColorValue> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var valueValue: DeserializationResult<Expression<Color>> = { parent?.value?.value() ?? .noValue }()
    _ = {
      // Each field is parsed in its own lambda to keep the stack size managable
      // Otherwise the compiler will allocate stack for each intermediate variable
      // upfront even when we don't actually visit a relevant branch
      for (key, __dictValue) in context.templateData {
        _ = {
          if key == "value" {
           valueValue = deserialize(__dictValue, transform: Color.color(withHexString:)).merged(with: valueValue)
          }
        }()
        _ = {
         if key == parent?.value?.link {
           valueValue = valueValue.merged(with: { deserialize(__dictValue, transform: Color.color(withHexString:)) })
          }
        }()
      }
    }()
    var errors = mergeErrors(
      valueValue.errorsOrWarnings?.map { .nestedObjectError(field: "value", error: $0) }
    )
    if case .noValue = valueValue {
      errors.append(.requiredFieldIsMissing(field: "value"))
    }
    guard
      let valueNonNil = valueValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = ColorValue(
      value: { valueNonNil }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> ColorValueTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? ColorValueTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return ColorValueTemplate(
      parent: nil,
      value: value ?? mergedParent.value
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> ColorValueTemplate {
    return try mergedWithParent(templates: templates)
  }
}
