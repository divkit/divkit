// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivWrapContentSizeTemplate: TemplateValue {
  public final class ConstraintSizeTemplate: TemplateValue {
    public let unit: Field<Expression<DivSizeUnit>>? // default value: dp
    public let value: Field<Expression<Int>>? // constraint: number >= 0

    public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
      do {
        self.init(
          unit: try dictionary.getOptionalExpressionField("unit"),
          value: try dictionary.getOptionalExpressionField("value")
        )
      } catch let DeserializationError.invalidFieldRepresentation(field: field, representation: representation) {
        throw DeserializationError.invalidFieldRepresentation(field: "constraint_size_template." + field, representation: representation)
      }
    }

    init(
      unit: Field<Expression<DivSizeUnit>>? = nil,
      value: Field<Expression<Int>>? = nil
    ) {
      self.unit = unit
      self.value = value
    }

    private static func resolveOnlyLinks(context: TemplatesContext, parent: ConstraintSizeTemplate?) -> DeserializationResult<DivWrapContentSize.ConstraintSize> {
      let unitValue = parent?.unit?.resolveOptionalValue(context: context, validator: ResolvedValue.unitValidator) ?? .noValue
      let valueValue = parent?.value?.resolveValue(context: context, validator: ResolvedValue.valueValidator) ?? .noValue
      var errors = mergeErrors(
        unitValue.errorsOrWarnings?.map { .nestedObjectError(field: "unit", error: $0) },
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
      let result = DivWrapContentSize.ConstraintSize(
        unit: unitValue.value,
        value: valueNonNil
      )
      return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
    }

    public static func resolveValue(context: TemplatesContext, parent: ConstraintSizeTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivWrapContentSize.ConstraintSize> {
      if useOnlyLinks {
        return resolveOnlyLinks(context: context, parent: parent)
      }
      var unitValue: DeserializationResult<Expression<DivSizeUnit>> = parent?.unit?.value() ?? .noValue
      var valueValue: DeserializationResult<Expression<Int>> = parent?.value?.value() ?? .noValue
      context.templateData.forEach { key, __dictValue in
        switch key {
        case "unit":
          unitValue = deserialize(__dictValue, validator: ResolvedValue.unitValidator).merged(with: unitValue)
        case "value":
          valueValue = deserialize(__dictValue, validator: ResolvedValue.valueValidator).merged(with: valueValue)
        case parent?.unit?.link:
          unitValue = unitValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.unitValidator))
        case parent?.value?.link:
          valueValue = valueValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.valueValidator))
        default: break
        }
      }
      var errors = mergeErrors(
        unitValue.errorsOrWarnings?.map { .nestedObjectError(field: "unit", error: $0) },
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
      let result = DivWrapContentSize.ConstraintSize(
        unit: unitValue.value,
        value: valueNonNil
      )
      return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
    }

    private func mergedWithParent(templates: [TemplateName: Any]) throws -> ConstraintSizeTemplate {
      return self
    }

    public func resolveParent(templates: [TemplateName: Any]) throws -> ConstraintSizeTemplate {
      return try mergedWithParent(templates: templates)
    }
  }

  public static let type: String = "wrap_content"
  public let parent: String? // at least 1 char
  public let constrained: Field<Expression<Bool>>?
  public let maxSize: Field<ConstraintSizeTemplate>?
  public let minSize: Field<ConstraintSizeTemplate>?

  static let parentValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      parent: try dictionary.getOptionalField("type", validator: Self.parentValidator),
      constrained: try dictionary.getOptionalExpressionField("constrained"),
      maxSize: try dictionary.getOptionalField("max_size", templateToType: templateToType),
      minSize: try dictionary.getOptionalField("min_size", templateToType: templateToType)
    )
  }

  init(
    parent: String?,
    constrained: Field<Expression<Bool>>? = nil,
    maxSize: Field<ConstraintSizeTemplate>? = nil,
    minSize: Field<ConstraintSizeTemplate>? = nil
  ) {
    self.parent = parent
    self.constrained = constrained
    self.maxSize = maxSize
    self.minSize = minSize
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivWrapContentSizeTemplate?) -> DeserializationResult<DivWrapContentSize> {
    let constrainedValue = parent?.constrained?.resolveOptionalValue(context: context, validator: ResolvedValue.constrainedValidator) ?? .noValue
    let maxSizeValue = parent?.maxSize?.resolveOptionalValue(context: context, validator: ResolvedValue.maxSizeValidator, useOnlyLinks: true) ?? .noValue
    let minSizeValue = parent?.minSize?.resolveOptionalValue(context: context, validator: ResolvedValue.minSizeValidator, useOnlyLinks: true) ?? .noValue
    let errors = mergeErrors(
      constrainedValue.errorsOrWarnings?.map { .nestedObjectError(field: "constrained", error: $0) },
      maxSizeValue.errorsOrWarnings?.map { .nestedObjectError(field: "max_size", error: $0) },
      minSizeValue.errorsOrWarnings?.map { .nestedObjectError(field: "min_size", error: $0) }
    )
    let result = DivWrapContentSize(
      constrained: constrainedValue.value,
      maxSize: maxSizeValue.value,
      minSize: minSizeValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivWrapContentSizeTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivWrapContentSize> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var constrainedValue: DeserializationResult<Expression<Bool>> = parent?.constrained?.value() ?? .noValue
    var maxSizeValue: DeserializationResult<DivWrapContentSize.ConstraintSize> = .noValue
    var minSizeValue: DeserializationResult<DivWrapContentSize.ConstraintSize> = .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "constrained":
        constrainedValue = deserialize(__dictValue, validator: ResolvedValue.constrainedValidator).merged(with: constrainedValue)
      case "max_size":
        maxSizeValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.maxSizeValidator, type: DivWrapContentSizeTemplate.ConstraintSizeTemplate.self).merged(with: maxSizeValue)
      case "min_size":
        minSizeValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.minSizeValidator, type: DivWrapContentSizeTemplate.ConstraintSizeTemplate.self).merged(with: minSizeValue)
      case parent?.constrained?.link:
        constrainedValue = constrainedValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.constrainedValidator))
      case parent?.maxSize?.link:
        maxSizeValue = maxSizeValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.maxSizeValidator, type: DivWrapContentSizeTemplate.ConstraintSizeTemplate.self))
      case parent?.minSize?.link:
        minSizeValue = minSizeValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.minSizeValidator, type: DivWrapContentSizeTemplate.ConstraintSizeTemplate.self))
      default: break
      }
    }
    if let parent = parent {
      maxSizeValue = maxSizeValue.merged(with: parent.maxSize?.resolveOptionalValue(context: context, validator: ResolvedValue.maxSizeValidator, useOnlyLinks: true))
      minSizeValue = minSizeValue.merged(with: parent.minSize?.resolveOptionalValue(context: context, validator: ResolvedValue.minSizeValidator, useOnlyLinks: true))
    }
    let errors = mergeErrors(
      constrainedValue.errorsOrWarnings?.map { .nestedObjectError(field: "constrained", error: $0) },
      maxSizeValue.errorsOrWarnings?.map { .nestedObjectError(field: "max_size", error: $0) },
      minSizeValue.errorsOrWarnings?.map { .nestedObjectError(field: "min_size", error: $0) }
    )
    let result = DivWrapContentSize(
      constrained: constrainedValue.value,
      maxSize: maxSizeValue.value,
      minSize: minSizeValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivWrapContentSizeTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? DivWrapContentSizeTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return DivWrapContentSizeTemplate(
      parent: nil,
      constrained: constrained ?? mergedParent.constrained,
      maxSize: maxSize ?? mergedParent.maxSize,
      minSize: minSize ?? mergedParent.minSize
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivWrapContentSizeTemplate {
    let merged = try mergedWithParent(templates: templates)

    return DivWrapContentSizeTemplate(
      parent: nil,
      constrained: merged.constrained,
      maxSize: merged.maxSize?.tryResolveParent(templates: templates),
      minSize: merged.minSize?.tryResolveParent(templates: templates)
    )
  }
}
