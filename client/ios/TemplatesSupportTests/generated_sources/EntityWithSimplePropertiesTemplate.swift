// Generated code. Do not modify.

import CoreFoundation
import Foundation

import CommonCore
import Serialization
import TemplatesSupport

public final class EntityWithSimplePropertiesTemplate: TemplateValue, EntityProtocol,
  TemplateDeserializable {
  public static let type: String = "entity_with_simple_properties"
  public let parent: String? // at least 1 char
  public let boolean: Field<Bool>?
  public let color: Field<Color>?
  public let double: Field<Double>?
  public let id: Field<Int>?
  public let integer: Field<Int>?
  public let positiveInteger: Field<Int>? // constraint: number > 0
  public let string: Field<String>? // at least 1 char
  public let url: Field<URL>?

  static let parentValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  public convenience init(dictionary: [String: Any], templateToType _: TemplateToType) throws {
    self.init(
      parent: try dictionary.getOptionalField("type", validator: Self.parentValidator),
      boolean: try dictionary.getOptionalField("boolean"),
      color: try dictionary.getOptionalField("color", transform: Color.color(withHexString:)),
      double: try dictionary.getOptionalField("double"),
      id: try dictionary.getOptionalField("id"),
      integer: try dictionary.getOptionalField("integer"),
      positiveInteger: try dictionary.getOptionalField("positive_integer"),
      string: try dictionary.getOptionalField("string"),
      url: try dictionary.getOptionalField("url", transform: URL.init(string:))
    )
  }

  init(
    parent: String?,
    boolean: Field<Bool>? = nil,
    color: Field<Color>? = nil,
    double: Field<Double>? = nil,
    id: Field<Int>? = nil,
    integer: Field<Int>? = nil,
    positiveInteger: Field<Int>? = nil,
    string: Field<String>? = nil,
    url: Field<URL>? = nil
  ) {
    self.parent = parent
    self.boolean = boolean
    self.color = color
    self.double = double
    self.id = id
    self.integer = integer
    self.positiveInteger = positiveInteger
    self.string = string
    self.url = url
  }

  private static func resolveOnlyLinks(
    context: Context,
    parent: EntityWithSimplePropertiesTemplate?
  )
    -> DeserializationResult<EntityWithSimpleProperties> {
    let booleanValue = parent?.boolean?.resolveOptionalValue(
      context: context,
      validator: ResolvedValue.booleanValidator
    ) ?? .noValue
    let colorValue = parent?.color?.resolveOptionalValue(
      context: context,
      transform: Color.color(withHexString:),
      validator: ResolvedValue.colorValidator
    ) ?? .noValue
    let doubleValue = parent?.double?.resolveOptionalValue(context: context) ?? .noValue
    let idValue = parent?.id?.resolveOptionalValue(context: context) ?? .noValue
    let integerValue = parent?.integer?.resolveOptionalValue(context: context) ?? .noValue
    let positiveIntegerValue = parent?.positiveInteger?.resolveOptionalValue(
      context: context,
      validator: ResolvedValue.positiveIntegerValidator
    ) ?? .noValue
    let stringValue = parent?.string?.resolveOptionalValue(
      context: context,
      validator: ResolvedValue.stringValidator
    ) ?? .noValue
    let urlValue = parent?.url?.resolveOptionalValue(
      context: context,
      transform: URL.init(string:),
      validator: ResolvedValue.urlValidator
    ) ?? .noValue
    let errors = mergeErrors(
      booleanValue.errorsOrWarnings?
        .map { .right($0.asError(deserializing: "boolean", level: .warning)) },
      colorValue.errorsOrWarnings?
        .map { .right($0.asError(deserializing: "color", level: .warning)) },
      doubleValue.errorsOrWarnings?
        .map { .right($0.asError(deserializing: "double", level: .warning)) },
      idValue.errorsOrWarnings?.map { .right($0.asError(deserializing: "id", level: .warning)) },
      integerValue.errorsOrWarnings?
        .map { .right($0.asError(deserializing: "integer", level: .warning)) },
      positiveIntegerValue.errorsOrWarnings?
        .map { .right($0.asError(deserializing: "positive_integer", level: .warning)) },
      stringValue.errorsOrWarnings?
        .map { .right($0.asError(deserializing: "string", level: .warning)) },
      urlValue.errorsOrWarnings?.map { .right($0.asError(deserializing: "url", level: .warning)) }
    )
    let result = EntityWithSimpleProperties(
      boolean: booleanValue.value,
      color: colorValue.value,
      double: doubleValue.value,
      id: idValue.value,
      integer: integerValue.value,
      positiveInteger: positiveIntegerValue.value,
      string: stringValue.value,
      url: urlValue.value
    )
    return errors
      .isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(
    context: Context,
    parent: EntityWithSimplePropertiesTemplate?,
    useOnlyLinks: Bool
  ) -> DeserializationResult<EntityWithSimpleProperties> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var booleanValue: DeserializationResult<Bool> = parent?.boolean?
      .value(validatedBy: ResolvedValue.booleanValidator) ?? .noValue
    var colorValue: DeserializationResult<Color> = parent?.color?
      .value(validatedBy: ResolvedValue.colorValidator) ?? .noValue
    var doubleValue: DeserializationResult<Double> = parent?.double?.value() ?? .noValue
    var idValue: DeserializationResult<Int> = parent?.id?.value() ?? .noValue
    var integerValue: DeserializationResult<Int> = parent?.integer?.value() ?? .noValue
    var positiveIntegerValue: DeserializationResult<Int> = parent?.positiveInteger?
      .value(validatedBy: ResolvedValue.positiveIntegerValidator) ?? .noValue
    var stringValue: DeserializationResult<String> = parent?.string?
      .value(validatedBy: ResolvedValue.stringValidator) ?? .noValue
    var urlValue: DeserializationResult<URL> = parent?.url?
      .value(validatedBy: ResolvedValue.urlValidator) ?? .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "boolean":
        booleanValue = deserialize(__dictValue, validator: ResolvedValue.booleanValidator)
          .merged(with: booleanValue)
      case "color":
        colorValue = deserialize(
          __dictValue,
          transform: Color.color(withHexString:),
          validator: ResolvedValue.colorValidator
        ).merged(with: colorValue)
      case "double":
        doubleValue = deserialize(__dictValue).merged(with: doubleValue)
      case "id":
        idValue = deserialize(__dictValue).merged(with: idValue)
      case "integer":
        integerValue = deserialize(__dictValue).merged(with: integerValue)
      case "positive_integer":
        positiveIntegerValue = deserialize(
          __dictValue,
          validator: ResolvedValue.positiveIntegerValidator
        ).merged(with: positiveIntegerValue)
      case "string":
        stringValue = deserialize(__dictValue, validator: ResolvedValue.stringValidator)
          .merged(with: stringValue)
      case "url":
        urlValue = deserialize(
          __dictValue,
          transform: URL.init(string:),
          validator: ResolvedValue.urlValidator
        ).merged(with: urlValue)
      case parent?.boolean?.link:
        booleanValue = booleanValue
          .merged(with: deserialize(__dictValue, validator: ResolvedValue.booleanValidator))
      case parent?.color?.link:
        colorValue = colorValue.merged(with: deserialize(
          __dictValue,
          transform: Color.color(withHexString:),
          validator: ResolvedValue.colorValidator
        ))
      case parent?.double?.link:
        doubleValue = doubleValue.merged(with: deserialize(__dictValue))
      case parent?.id?.link:
        idValue = idValue.merged(with: deserialize(__dictValue))
      case parent?.integer?.link:
        integerValue = integerValue.merged(with: deserialize(__dictValue))
      case parent?.positiveInteger?.link:
        positiveIntegerValue = positiveIntegerValue
          .merged(with: deserialize(__dictValue, validator: ResolvedValue.positiveIntegerValidator))
      case parent?.string?.link:
        stringValue = stringValue
          .merged(with: deserialize(__dictValue, validator: ResolvedValue.stringValidator))
      case parent?.url?.link:
        urlValue = urlValue.merged(with: deserialize(
          __dictValue,
          transform: URL.init(string:),
          validator: ResolvedValue.urlValidator
        ))
      default: break
      }
    }
    let errors = mergeErrors(
      booleanValue.errorsOrWarnings?
        .map { Either.right($0.asError(deserializing: "boolean", level: .warning)) },
      colorValue.errorsOrWarnings?
        .map { Either.right($0.asError(deserializing: "color", level: .warning)) },
      doubleValue.errorsOrWarnings?
        .map { Either.right($0.asError(deserializing: "double", level: .warning)) },
      idValue.errorsOrWarnings?
        .map { Either.right($0.asError(deserializing: "id", level: .warning)) },
      integerValue.errorsOrWarnings?
        .map { Either.right($0.asError(deserializing: "integer", level: .warning)) },
      positiveIntegerValue.errorsOrWarnings?
        .map { Either.right($0.asError(deserializing: "positive_integer", level: .warning)) },
      stringValue.errorsOrWarnings?
        .map { Either.right($0.asError(deserializing: "string", level: .warning)) },
      urlValue.errorsOrWarnings?
        .map { Either.right($0.asError(deserializing: "url", level: .warning)) }
    )
    let result = EntityWithSimpleProperties(
      boolean: booleanValue.value,
      color: colorValue.value,
      double: doubleValue.value,
      id: idValue.value,
      integer: integerValue.value,
      positiveInteger: positiveIntegerValue.value,
      string: stringValue.value,
      url: urlValue.value
    )
    return errors
      .isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: Templates) throws -> EntityWithSimplePropertiesTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? EntityWithSimplePropertiesTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return EntityWithSimplePropertiesTemplate(
      parent: nil,
      boolean: boolean ?? mergedParent.boolean,
      color: color ?? mergedParent.color,
      double: double ?? mergedParent.double,
      id: id ?? mergedParent.id,
      integer: integer ?? mergedParent.integer,
      positiveInteger: positiveInteger ?? mergedParent.positiveInteger,
      string: string ?? mergedParent.string,
      url: url ?? mergedParent.url
    )
  }

  public func resolveParent(templates: Templates) throws -> EntityWithSimplePropertiesTemplate {
    try mergedWithParent(templates: templates)
  }
}
