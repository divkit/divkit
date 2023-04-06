// Generated code. Do not modify.

import CommonCore
import Foundation
import Serialization

public final class EntityWithSimplePropertiesTemplate: TemplateValue, EntityProtocol, TemplateDeserializable {
  public static let type: String = "entity_with_simple_properties"
  public let parent: String? // at least 1 char
  public let boolean: Field<Expression<Bool>>?
  public let booleanInt: Field<Expression<Bool>>?
  public let color: Field<Expression<Color>>?
  public let double: Field<Expression<Double>>?
  public let id: Field<Int>?
  public let integer: Field<Expression<Int>>?
  public let positiveInteger: Field<Expression<Int>>? // constraint: number > 0
  public let string: Field<Expression<String>>? // at least 1 char
  public let url: Field<Expression<URL>>?

  static let parentValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  public convenience init(dictionary: [String: Any], templateToType: TemplateToType) throws {
    self.init(
      parent: try dictionary.getOptionalField("type", validator: Self.parentValidator),
      boolean: try dictionary.getOptionalExpressionField("boolean"),
      booleanInt: try dictionary.getOptionalExpressionField("boolean_int"),
      color: try dictionary.getOptionalExpressionField("color", transform: Color.color(withHexString:)),
      double: try dictionary.getOptionalExpressionField("double"),
      id: try dictionary.getOptionalField("id"),
      integer: try dictionary.getOptionalExpressionField("integer"),
      positiveInteger: try dictionary.getOptionalExpressionField("positive_integer"),
      string: try dictionary.getOptionalExpressionField("string"),
      url: try dictionary.getOptionalExpressionField("url", transform: URL.init(string:))
    )
  }

  init(
    parent: String?,
    boolean: Field<Expression<Bool>>? = nil,
    booleanInt: Field<Expression<Bool>>? = nil,
    color: Field<Expression<Color>>? = nil,
    double: Field<Expression<Double>>? = nil,
    id: Field<Int>? = nil,
    integer: Field<Expression<Int>>? = nil,
    positiveInteger: Field<Expression<Int>>? = nil,
    string: Field<Expression<String>>? = nil,
    url: Field<Expression<URL>>? = nil
  ) {
    self.parent = parent
    self.boolean = boolean
    self.booleanInt = booleanInt
    self.color = color
    self.double = double
    self.id = id
    self.integer = integer
    self.positiveInteger = positiveInteger
    self.string = string
    self.url = url
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: EntityWithSimplePropertiesTemplate?) -> DeserializationResult<EntityWithSimpleProperties> {
    let booleanValue = parent?.boolean?.resolveOptionalValue(context: context, validator: ResolvedValue.booleanValidator) ?? .noValue
    let booleanIntValue = parent?.booleanInt?.resolveOptionalValue(context: context, validator: ResolvedValue.booleanIntValidator) ?? .noValue
    let colorValue = parent?.color?.resolveOptionalValue(context: context, transform: Color.color(withHexString:), validator: ResolvedValue.colorValidator) ?? .noValue
    let doubleValue = parent?.double?.resolveOptionalValue(context: context) ?? .noValue
    let idValue = parent?.id?.resolveOptionalValue(context: context) ?? .noValue
    let integerValue = parent?.integer?.resolveOptionalValue(context: context) ?? .noValue
    let positiveIntegerValue = parent?.positiveInteger?.resolveOptionalValue(context: context, validator: ResolvedValue.positiveIntegerValidator) ?? .noValue
    let stringValue = parent?.string?.resolveOptionalValue(context: context, validator: ResolvedValue.stringValidator) ?? .noValue
    let urlValue = parent?.url?.resolveOptionalValue(context: context, transform: URL.init(string:), validator: ResolvedValue.urlValidator) ?? .noValue
    let errors = mergeErrors(
      booleanValue.errorsOrWarnings?.map { .nestedObjectError(field: "boolean", error: $0) },
      booleanIntValue.errorsOrWarnings?.map { .nestedObjectError(field: "boolean_int", error: $0) },
      colorValue.errorsOrWarnings?.map { .nestedObjectError(field: "color", error: $0) },
      doubleValue.errorsOrWarnings?.map { .nestedObjectError(field: "double", error: $0) },
      idValue.errorsOrWarnings?.map { .nestedObjectError(field: "id", error: $0) },
      integerValue.errorsOrWarnings?.map { .nestedObjectError(field: "integer", error: $0) },
      positiveIntegerValue.errorsOrWarnings?.map { .nestedObjectError(field: "positive_integer", error: $0) },
      stringValue.errorsOrWarnings?.map { .nestedObjectError(field: "string", error: $0) },
      urlValue.errorsOrWarnings?.map { .nestedObjectError(field: "url", error: $0) }
    )
    let result = EntityWithSimpleProperties(
      boolean: booleanValue.value,
      booleanInt: booleanIntValue.value,
      color: colorValue.value,
      double: doubleValue.value,
      id: idValue.value,
      integer: integerValue.value,
      positiveInteger: positiveIntegerValue.value,
      string: stringValue.value,
      url: urlValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: EntityWithSimplePropertiesTemplate?, useOnlyLinks: Bool) -> DeserializationResult<EntityWithSimpleProperties> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var booleanValue: DeserializationResult<Expression<Bool>> = parent?.boolean?.value() ?? .noValue
    var booleanIntValue: DeserializationResult<Expression<Bool>> = parent?.booleanInt?.value() ?? .noValue
    var colorValue: DeserializationResult<Expression<Color>> = parent?.color?.value() ?? .noValue
    var doubleValue: DeserializationResult<Expression<Double>> = parent?.double?.value() ?? .noValue
    var idValue: DeserializationResult<Int> = parent?.id?.value() ?? .noValue
    var integerValue: DeserializationResult<Expression<Int>> = parent?.integer?.value() ?? .noValue
    var positiveIntegerValue: DeserializationResult<Expression<Int>> = parent?.positiveInteger?.value() ?? .noValue
    var stringValue: DeserializationResult<Expression<String>> = parent?.string?.value() ?? .noValue
    var urlValue: DeserializationResult<Expression<URL>> = parent?.url?.value() ?? .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "boolean":
        booleanValue = deserialize(__dictValue, validator: ResolvedValue.booleanValidator).merged(with: booleanValue)
      case "boolean_int":
        booleanIntValue = deserialize(__dictValue, validator: ResolvedValue.booleanIntValidator).merged(with: booleanIntValue)
      case "color":
        colorValue = deserialize(__dictValue, transform: Color.color(withHexString:), validator: ResolvedValue.colorValidator).merged(with: colorValue)
      case "double":
        doubleValue = deserialize(__dictValue).merged(with: doubleValue)
      case "id":
        idValue = deserialize(__dictValue).merged(with: idValue)
      case "integer":
        integerValue = deserialize(__dictValue).merged(with: integerValue)
      case "positive_integer":
        positiveIntegerValue = deserialize(__dictValue, validator: ResolvedValue.positiveIntegerValidator).merged(with: positiveIntegerValue)
      case "string":
        stringValue = deserialize(__dictValue, validator: ResolvedValue.stringValidator).merged(with: stringValue)
      case "url":
        urlValue = deserialize(__dictValue, transform: URL.init(string:), validator: ResolvedValue.urlValidator).merged(with: urlValue)
      case parent?.boolean?.link:
        booleanValue = booleanValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.booleanValidator))
      case parent?.booleanInt?.link:
        booleanIntValue = booleanIntValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.booleanIntValidator))
      case parent?.color?.link:
        colorValue = colorValue.merged(with: deserialize(__dictValue, transform: Color.color(withHexString:), validator: ResolvedValue.colorValidator))
      case parent?.double?.link:
        doubleValue = doubleValue.merged(with: deserialize(__dictValue))
      case parent?.id?.link:
        idValue = idValue.merged(with: deserialize(__dictValue))
      case parent?.integer?.link:
        integerValue = integerValue.merged(with: deserialize(__dictValue))
      case parent?.positiveInteger?.link:
        positiveIntegerValue = positiveIntegerValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.positiveIntegerValidator))
      case parent?.string?.link:
        stringValue = stringValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.stringValidator))
      case parent?.url?.link:
        urlValue = urlValue.merged(with: deserialize(__dictValue, transform: URL.init(string:), validator: ResolvedValue.urlValidator))
      default: break
      }
    }
    let errors = mergeErrors(
      booleanValue.errorsOrWarnings?.map { .nestedObjectError(field: "boolean", error: $0) },
      booleanIntValue.errorsOrWarnings?.map { .nestedObjectError(field: "boolean_int", error: $0) },
      colorValue.errorsOrWarnings?.map { .nestedObjectError(field: "color", error: $0) },
      doubleValue.errorsOrWarnings?.map { .nestedObjectError(field: "double", error: $0) },
      idValue.errorsOrWarnings?.map { .nestedObjectError(field: "id", error: $0) },
      integerValue.errorsOrWarnings?.map { .nestedObjectError(field: "integer", error: $0) },
      positiveIntegerValue.errorsOrWarnings?.map { .nestedObjectError(field: "positive_integer", error: $0) },
      stringValue.errorsOrWarnings?.map { .nestedObjectError(field: "string", error: $0) },
      urlValue.errorsOrWarnings?.map { .nestedObjectError(field: "url", error: $0) }
    )
    let result = EntityWithSimpleProperties(
      boolean: booleanValue.value,
      booleanInt: booleanIntValue.value,
      color: colorValue.value,
      double: doubleValue.value,
      id: idValue.value,
      integer: integerValue.value,
      positiveInteger: positiveIntegerValue.value,
      string: stringValue.value,
      url: urlValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
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
      booleanInt: booleanInt ?? mergedParent.booleanInt,
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
    return try mergedWithParent(templates: templates)
  }
}
