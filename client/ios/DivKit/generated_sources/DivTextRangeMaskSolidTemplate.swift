// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivTextRangeMaskSolidTemplate: TemplateValue, Sendable {
  public static let type: String = "solid"
  public let parent: String?
  public let color: Field<Expression<Color>>?
  public let isEnabled: Field<Expression<Bool>>? // default value: true

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      parent: dictionary["type"] as? String,
      color: dictionary.getOptionalExpressionField("color", transform: Color.color(withHexString:)),
      isEnabled: dictionary.getOptionalExpressionField("is_enabled")
    )
  }

  init(
    parent: String?,
    color: Field<Expression<Color>>? = nil,
    isEnabled: Field<Expression<Bool>>? = nil
  ) {
    self.parent = parent
    self.color = color
    self.isEnabled = isEnabled
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivTextRangeMaskSolidTemplate?) -> DeserializationResult<DivTextRangeMaskSolid> {
    let colorValue = { parent?.color?.resolveValue(context: context, transform: Color.color(withHexString:)) ?? .noValue }()
    let isEnabledValue = { parent?.isEnabled?.resolveOptionalValue(context: context) ?? .noValue }()
    var errors = mergeErrors(
      colorValue.errorsOrWarnings?.map { .nestedObjectError(field: "color", error: $0) },
      isEnabledValue.errorsOrWarnings?.map { .nestedObjectError(field: "is_enabled", error: $0) }
    )
    if case .noValue = colorValue {
      errors.append(.requiredFieldIsMissing(field: "color"))
    }
    guard
      let colorNonNil = colorValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivTextRangeMaskSolid(
      color: { colorNonNil }(),
      isEnabled: { isEnabledValue.value }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivTextRangeMaskSolidTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivTextRangeMaskSolid> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var colorValue: DeserializationResult<Expression<Color>> = { parent?.color?.value() ?? .noValue }()
    var isEnabledValue: DeserializationResult<Expression<Bool>> = { parent?.isEnabled?.value() ?? .noValue }()
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
          if key == "is_enabled" {
           isEnabledValue = deserialize(__dictValue).merged(with: isEnabledValue)
          }
        }()
        _ = {
         if key == parent?.color?.link {
           colorValue = colorValue.merged(with: { deserialize(__dictValue, transform: Color.color(withHexString:)) })
          }
        }()
        _ = {
         if key == parent?.isEnabled?.link {
           isEnabledValue = isEnabledValue.merged(with: { deserialize(__dictValue) })
          }
        }()
      }
    }()
    var errors = mergeErrors(
      colorValue.errorsOrWarnings?.map { .nestedObjectError(field: "color", error: $0) },
      isEnabledValue.errorsOrWarnings?.map { .nestedObjectError(field: "is_enabled", error: $0) }
    )
    if case .noValue = colorValue {
      errors.append(.requiredFieldIsMissing(field: "color"))
    }
    guard
      let colorNonNil = colorValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivTextRangeMaskSolid(
      color: { colorNonNil }(),
      isEnabled: { isEnabledValue.value }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivTextRangeMaskSolidTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? DivTextRangeMaskSolidTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return DivTextRangeMaskSolidTemplate(
      parent: nil,
      color: color ?? mergedParent.color,
      isEnabled: isEnabled ?? mergedParent.isEnabled
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivTextRangeMaskSolidTemplate {
    return try mergedWithParent(templates: templates)
  }
}
