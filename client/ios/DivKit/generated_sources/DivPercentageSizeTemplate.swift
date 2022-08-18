// Generated code. Do not modify.

import CommonCore
import Foundation
import Serialization
import TemplatesSupport

public final class DivPercentageSizeTemplate: TemplateValue, TemplateDeserializable {
  public static let type: String = "percentage"
  public let parent: String? // at least 1 char
  public let value: Field<Expression<Double>>? // constraint: number > 0

  static let parentValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  public convenience init(dictionary: [String: Any], templateToType: TemplateToType) throws {
    do {
      self.init(
        parent: try dictionary.getOptionalField("type", validator: Self.parentValidator),
        value: try dictionary.getOptionalField("value")
      )
    } catch let DeserializationError.invalidFieldRepresentation(field: field, representation: representation) {
      throw DeserializationError.invalidFieldRepresentation(field: "div-percentage-size_template." + field, representation: representation)
    }
  }

  init(
    parent: String?,
    value: Field<Expression<Double>>? = nil
  ) {
    self.parent = parent
    self.value = value
  }

  private static func resolveOnlyLinks(context: Context, parent: DivPercentageSizeTemplate?) -> DeserializationResult<DivPercentageSize> {
    let valueValue = parent?.value?.resolveValue(context: context, validator: ResolvedValue.valueValidator) ?? .noValue
    var errors = mergeErrors(
      valueValue.errorsOrWarnings?.map { .right($0.asError(deserializing: "value", level: .error)) }
    )
    if case .noValue = valueValue {
      errors.append(.right(FieldError(fieldName: "value", level: .error, error: .requiredFieldIsMissing)))
    }
    guard
      let valueNonNil = valueValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivPercentageSize(
      value: valueNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: Context, parent: DivPercentageSizeTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivPercentageSize> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var valueValue: DeserializationResult<Expression<Double>> = parent?.value?.value() ?? .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "value":
        valueValue = deserialize(__dictValue, validator: ResolvedValue.valueValidator).merged(with: valueValue)
      case parent?.value?.link:
        valueValue = valueValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.valueValidator))
      default: break
      }
    }
    var errors = mergeErrors(
      valueValue.errorsOrWarnings?.map { Either.right($0.asError(deserializing: "value", level: .error)) }
    )
    if case .noValue = valueValue {
      errors.append(.right(FieldError(fieldName: "value", level: .error, error: .requiredFieldIsMissing)))
    }
    guard
      let valueNonNil = valueValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivPercentageSize(
      value: valueNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: Templates) throws -> DivPercentageSizeTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? DivPercentageSizeTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return DivPercentageSizeTemplate(
      parent: nil,
      value: value ?? mergedParent.value
    )
  }

  public func resolveParent(templates: Templates) throws -> DivPercentageSizeTemplate {
    return try mergedWithParent(templates: templates)
  }
}
