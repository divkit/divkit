// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization
import TemplatesSupport

public final class DivKeyboardInputTemplate: TemplateValue, TemplateDeserializable {
  public typealias KeyboardType = DivKeyboardInput.KeyboardType

  public static let type: String = "keyboard"
  public let parent: String? // at least 1 char
  public let keyboardType: Field<Expression<KeyboardType>>? // default value: multi_line_text

  static let parentValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  public convenience init(dictionary: [String: Any], templateToType: TemplateToType) throws {
    self.init(
      parent: try dictionary.getOptionalField("type", validator: Self.parentValidator),
      keyboardType: try dictionary.getOptionalExpressionField("keyboard_type")
    )
  }

  init(
    parent: String?,
    keyboardType: Field<Expression<KeyboardType>>? = nil
  ) {
    self.parent = parent
    self.keyboardType = keyboardType
  }

  private static func resolveOnlyLinks(context: Context, parent: DivKeyboardInputTemplate?) -> DeserializationResult<DivKeyboardInput> {
    let keyboardTypeValue = parent?.keyboardType?.resolveOptionalValue(context: context, validator: ResolvedValue.keyboardTypeValidator) ?? .noValue
    let errors = mergeErrors(
      keyboardTypeValue.errorsOrWarnings?.map { .nestedObjectError(field: "keyboard_type", error: $0) }
    )
    let result = DivKeyboardInput(
      keyboardType: keyboardTypeValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: Context, parent: DivKeyboardInputTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivKeyboardInput> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var keyboardTypeValue: DeserializationResult<Expression<DivKeyboardInput.KeyboardType>> = parent?.keyboardType?.value() ?? .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "keyboard_type":
        keyboardTypeValue = deserialize(__dictValue, validator: ResolvedValue.keyboardTypeValidator).merged(with: keyboardTypeValue)
      case parent?.keyboardType?.link:
        keyboardTypeValue = keyboardTypeValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.keyboardTypeValidator))
      default: break
      }
    }
    let errors = mergeErrors(
      keyboardTypeValue.errorsOrWarnings?.map { .nestedObjectError(field: "keyboard_type", error: $0) }
    )
    let result = DivKeyboardInput(
      keyboardType: keyboardTypeValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: Templates) throws -> DivKeyboardInputTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? DivKeyboardInputTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return DivKeyboardInputTemplate(
      parent: nil,
      keyboardType: keyboardType ?? mergedParent.keyboardType
    )
  }

  public func resolveParent(templates: Templates) throws -> DivKeyboardInputTemplate {
    return try mergedWithParent(templates: templates)
  }
}
