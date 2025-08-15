// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivSolidBackgroundTemplate: TemplateValue, Sendable {
  public static let type: String = "solid"
  public let parent: String?
  public let color: Field<Expression<Color>>?

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      parent: dictionary["type"] as? String,
      color: dictionary.getOptionalExpressionField("color", transform: Color.color(withHexString:))
    )
  }

  init(
    parent: String?,
    color: Field<Expression<Color>>? = nil
  ) {
    self.parent = parent
    self.color = color
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivSolidBackgroundTemplate?) -> DeserializationResult<DivSolidBackground> {
    let colorValue = { parent?.color?.resolveValue(context: context, transform: Color.color(withHexString:)) ?? .noValue }()
    var errors = mergeErrors(
      colorValue.errorsOrWarnings?.map { .nestedObjectError(field: "color", error: $0) }
    )
    if case .noValue = colorValue {
      errors.append(.requiredFieldIsMissing(field: "color"))
    }
    guard
      let colorNonNil = colorValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivSolidBackground(
      color: { colorNonNil }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivSolidBackgroundTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivSolidBackground> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var colorValue: DeserializationResult<Expression<Color>> = { parent?.color?.value() ?? .noValue }()
    _ = {
      // Each field is parsed in its own lambda to keep the stack size managable
      // Otherwise the compiler will allocate stack for each intermediate variable
      // upfront even when we don't actually visit a relevant branch
      for (key, __dictValue) in context.templateData {
        _ = {
          if key == "color" {
           colorValue = deserialize(__dictValue, transform: Color.color(withHexString:)).merged(with: colorValue)
          }
        }()
        _ = {
         if key == parent?.color?.link {
           colorValue = colorValue.merged(with: { deserialize(__dictValue, transform: Color.color(withHexString:)) })
          }
        }()
      }
    }()
    var errors = mergeErrors(
      colorValue.errorsOrWarnings?.map { .nestedObjectError(field: "color", error: $0) }
    )
    if case .noValue = colorValue {
      errors.append(.requiredFieldIsMissing(field: "color"))
    }
    guard
      let colorNonNil = colorValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivSolidBackground(
      color: { colorNonNil }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivSolidBackgroundTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? DivSolidBackgroundTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return DivSolidBackgroundTemplate(
      parent: nil,
      color: color ?? mergedParent.color
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivSolidBackgroundTemplate {
    return try mergedWithParent(templates: templates)
  }
}
