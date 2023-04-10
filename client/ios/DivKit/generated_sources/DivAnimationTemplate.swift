// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivAnimationTemplate: TemplateValue {
  public typealias Name = DivAnimation.Name

  public let duration: Field<Expression<Int>>? // constraint: number >= 0; default value: 300
  public let endValue: Field<Expression<Double>>?
  public let interpolator: Field<Expression<DivAnimationInterpolator>>? // default value: spring
  public let items: Field<[DivAnimationTemplate]>? // at least 1 elements
  public let name: Field<Expression<Name>>?
  public let repeatCount: Field<DivCountTemplate>? // default value: .divInfinityCount(DivInfinityCount())
  public let startDelay: Field<Expression<Int>>? // constraint: number >= 0; default value: 0
  public let startValue: Field<Expression<Double>>?

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    do {
      self.init(
        duration: try dictionary.getOptionalExpressionField("duration"),
        endValue: try dictionary.getOptionalExpressionField("end_value"),
        interpolator: try dictionary.getOptionalExpressionField("interpolator"),
        items: try dictionary.getOptionalArray("items", templateToType: templateToType),
        name: try dictionary.getOptionalExpressionField("name"),
        repeatCount: try dictionary.getOptionalField("repeat", templateToType: templateToType),
        startDelay: try dictionary.getOptionalExpressionField("start_delay"),
        startValue: try dictionary.getOptionalExpressionField("start_value")
      )
    } catch let DeserializationError.invalidFieldRepresentation(field: field, representation: representation) {
      throw DeserializationError.invalidFieldRepresentation(field: "div-animation_template." + field, representation: representation)
    }
  }

  init(
    duration: Field<Expression<Int>>? = nil,
    endValue: Field<Expression<Double>>? = nil,
    interpolator: Field<Expression<DivAnimationInterpolator>>? = nil,
    items: Field<[DivAnimationTemplate]>? = nil,
    name: Field<Expression<Name>>? = nil,
    repeatCount: Field<DivCountTemplate>? = nil,
    startDelay: Field<Expression<Int>>? = nil,
    startValue: Field<Expression<Double>>? = nil
  ) {
    self.duration = duration
    self.endValue = endValue
    self.interpolator = interpolator
    self.items = items
    self.name = name
    self.repeatCount = repeatCount
    self.startDelay = startDelay
    self.startValue = startValue
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivAnimationTemplate?) -> DeserializationResult<DivAnimation> {
    let durationValue = parent?.duration?.resolveOptionalValue(context: context, validator: ResolvedValue.durationValidator) ?? .noValue
    let endValueValue = parent?.endValue?.resolveOptionalValue(context: context) ?? .noValue
    let interpolatorValue = parent?.interpolator?.resolveOptionalValue(context: context, validator: ResolvedValue.interpolatorValidator) ?? .noValue
    let itemsValue = parent?.items?.resolveOptionalValue(context: context, validator: ResolvedValue.itemsValidator, useOnlyLinks: true) ?? .noValue
    let nameValue = parent?.name?.resolveValue(context: context) ?? .noValue
    let repeatCountValue = parent?.repeatCount?.resolveOptionalValue(context: context, validator: ResolvedValue.repeatCountValidator, useOnlyLinks: true) ?? .noValue
    let startDelayValue = parent?.startDelay?.resolveOptionalValue(context: context, validator: ResolvedValue.startDelayValidator) ?? .noValue
    let startValueValue = parent?.startValue?.resolveOptionalValue(context: context) ?? .noValue
    var errors = mergeErrors(
      durationValue.errorsOrWarnings?.map { .nestedObjectError(field: "duration", error: $0) },
      endValueValue.errorsOrWarnings?.map { .nestedObjectError(field: "end_value", error: $0) },
      interpolatorValue.errorsOrWarnings?.map { .nestedObjectError(field: "interpolator", error: $0) },
      itemsValue.errorsOrWarnings?.map { .nestedObjectError(field: "items", error: $0) },
      nameValue.errorsOrWarnings?.map { .nestedObjectError(field: "name", error: $0) },
      repeatCountValue.errorsOrWarnings?.map { .nestedObjectError(field: "repeat", error: $0) },
      startDelayValue.errorsOrWarnings?.map { .nestedObjectError(field: "start_delay", error: $0) },
      startValueValue.errorsOrWarnings?.map { .nestedObjectError(field: "start_value", error: $0) }
    )
    if case .noValue = nameValue {
      errors.append(.requiredFieldIsMissing(field: "name"))
    }
    guard
      let nameNonNil = nameValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivAnimation(
      duration: durationValue.value,
      endValue: endValueValue.value,
      interpolator: interpolatorValue.value,
      items: itemsValue.value,
      name: nameNonNil,
      repeatCount: repeatCountValue.value,
      startDelay: startDelayValue.value,
      startValue: startValueValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivAnimationTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivAnimation> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var durationValue: DeserializationResult<Expression<Int>> = parent?.duration?.value() ?? .noValue
    var endValueValue: DeserializationResult<Expression<Double>> = parent?.endValue?.value() ?? .noValue
    var interpolatorValue: DeserializationResult<Expression<DivAnimationInterpolator>> = parent?.interpolator?.value() ?? .noValue
    var itemsValue: DeserializationResult<[DivAnimation]> = .noValue
    var nameValue: DeserializationResult<Expression<DivAnimation.Name>> = parent?.name?.value() ?? .noValue
    var repeatCountValue: DeserializationResult<DivCount> = .noValue
    var startDelayValue: DeserializationResult<Expression<Int>> = parent?.startDelay?.value() ?? .noValue
    var startValueValue: DeserializationResult<Expression<Double>> = parent?.startValue?.value() ?? .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "duration":
        durationValue = deserialize(__dictValue, validator: ResolvedValue.durationValidator).merged(with: durationValue)
      case "end_value":
        endValueValue = deserialize(__dictValue).merged(with: endValueValue)
      case "interpolator":
        interpolatorValue = deserialize(__dictValue, validator: ResolvedValue.interpolatorValidator).merged(with: interpolatorValue)
      case "items":
        itemsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.itemsValidator, type: DivAnimationTemplate.self).merged(with: itemsValue)
      case "name":
        nameValue = deserialize(__dictValue).merged(with: nameValue)
      case "repeat":
        repeatCountValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.repeatCountValidator, type: DivCountTemplate.self).merged(with: repeatCountValue)
      case "start_delay":
        startDelayValue = deserialize(__dictValue, validator: ResolvedValue.startDelayValidator).merged(with: startDelayValue)
      case "start_value":
        startValueValue = deserialize(__dictValue).merged(with: startValueValue)
      case parent?.duration?.link:
        durationValue = durationValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.durationValidator))
      case parent?.endValue?.link:
        endValueValue = endValueValue.merged(with: deserialize(__dictValue))
      case parent?.interpolator?.link:
        interpolatorValue = interpolatorValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.interpolatorValidator))
      case parent?.items?.link:
        itemsValue = itemsValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.itemsValidator, type: DivAnimationTemplate.self))
      case parent?.name?.link:
        nameValue = nameValue.merged(with: deserialize(__dictValue))
      case parent?.repeatCount?.link:
        repeatCountValue = repeatCountValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.repeatCountValidator, type: DivCountTemplate.self))
      case parent?.startDelay?.link:
        startDelayValue = startDelayValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.startDelayValidator))
      case parent?.startValue?.link:
        startValueValue = startValueValue.merged(with: deserialize(__dictValue))
      default: break
      }
    }
    if let parent = parent {
      itemsValue = itemsValue.merged(with: parent.items?.resolveOptionalValue(context: context, validator: ResolvedValue.itemsValidator, useOnlyLinks: true))
      repeatCountValue = repeatCountValue.merged(with: parent.repeatCount?.resolveOptionalValue(context: context, validator: ResolvedValue.repeatCountValidator, useOnlyLinks: true))
    }
    var errors = mergeErrors(
      durationValue.errorsOrWarnings?.map { .nestedObjectError(field: "duration", error: $0) },
      endValueValue.errorsOrWarnings?.map { .nestedObjectError(field: "end_value", error: $0) },
      interpolatorValue.errorsOrWarnings?.map { .nestedObjectError(field: "interpolator", error: $0) },
      itemsValue.errorsOrWarnings?.map { .nestedObjectError(field: "items", error: $0) },
      nameValue.errorsOrWarnings?.map { .nestedObjectError(field: "name", error: $0) },
      repeatCountValue.errorsOrWarnings?.map { .nestedObjectError(field: "repeat", error: $0) },
      startDelayValue.errorsOrWarnings?.map { .nestedObjectError(field: "start_delay", error: $0) },
      startValueValue.errorsOrWarnings?.map { .nestedObjectError(field: "start_value", error: $0) }
    )
    if case .noValue = nameValue {
      errors.append(.requiredFieldIsMissing(field: "name"))
    }
    guard
      let nameNonNil = nameValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivAnimation(
      duration: durationValue.value,
      endValue: endValueValue.value,
      interpolator: interpolatorValue.value,
      items: itemsValue.value,
      name: nameNonNil,
      repeatCount: repeatCountValue.value,
      startDelay: startDelayValue.value,
      startValue: startValueValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivAnimationTemplate {
    return self
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivAnimationTemplate {
    let merged = try mergedWithParent(templates: templates)

    return DivAnimationTemplate(
      duration: merged.duration,
      endValue: merged.endValue,
      interpolator: merged.interpolator,
      items: merged.items?.tryResolveParent(templates: templates),
      name: merged.name,
      repeatCount: merged.repeatCount?.tryResolveParent(templates: templates),
      startDelay: merged.startDelay,
      startValue: merged.startValue
    )
  }
}
