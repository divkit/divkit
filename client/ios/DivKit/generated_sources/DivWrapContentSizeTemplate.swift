// Generated code. Do not modify.

import CoreFoundation
import Foundation

import CommonCore
import Serialization
import TemplatesSupport

public final class DivWrapContentSizeTemplate: TemplateValue, TemplateDeserializable {
  public static let type: String = "wrap_content"
  public let parent: String? // at least 1 char
  public let constrained: Field<Expression<Bool>>?

  static let parentValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  public convenience init(dictionary: [String: Any], templateToType _: TemplateToType) throws {
    self.init(
      parent: try dictionary.getOptionalField("type", validator: Self.parentValidator),
      constrained: try dictionary.getOptionalField("constrained")
    )
  }

  init(
    parent: String?,
    constrained: Field<Expression<Bool>>? = nil
  ) {
    self.parent = parent
    self.constrained = constrained
  }

  private static func resolveOnlyLinks(
    context: Context,
    parent: DivWrapContentSizeTemplate?
  ) -> DeserializationResult<DivWrapContentSize> {
    let constrainedValue = parent?.constrained?.resolveOptionalValue(
      context: context,
      validator: ResolvedValue.constrainedValidator
    ) ?? .noValue
    let errors = mergeErrors(
      constrainedValue.errorsOrWarnings?
        .map { .right($0.asError(deserializing: "constrained", level: .warning)) }
    )
    let result = DivWrapContentSize(
      constrained: constrainedValue.value
    )
    return errors
      .isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(
    context: Context,
    parent: DivWrapContentSizeTemplate?,
    useOnlyLinks: Bool
  ) -> DeserializationResult<DivWrapContentSize> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var constrainedValue: DeserializationResult<Expression<Bool>> = parent?.constrained?
      .value() ?? .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "constrained":
        constrainedValue = deserialize(__dictValue, validator: ResolvedValue.constrainedValidator)
          .merged(with: constrainedValue)
      case parent?.constrained?.link:
        constrainedValue = constrainedValue
          .merged(with: deserialize(__dictValue, validator: ResolvedValue.constrainedValidator))
      default: break
      }
    }
    let errors = mergeErrors(
      constrainedValue.errorsOrWarnings?
        .map { Either.right($0.asError(deserializing: "constrained", level: .warning)) }
    )
    let result = DivWrapContentSize(
      constrained: constrainedValue.value
    )
    return errors
      .isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: Templates) throws -> DivWrapContentSizeTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? DivWrapContentSizeTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return DivWrapContentSizeTemplate(
      parent: nil,
      constrained: constrained ?? mergedParent.constrained
    )
  }

  public func resolveParent(templates: Templates) throws -> DivWrapContentSizeTemplate {
    try mergedWithParent(templates: templates)
  }
}
