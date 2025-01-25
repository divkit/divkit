// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivAnimationTemplate: TemplateValue {
  public typealias Name = DivAnimation.Name

  public let duration: Field<Expression<Int>>? // constraint: number >= 0; default value: 300
  public let endValue: Field<Expression<Double>>?
  public let interpolator: Field<Expression<DivAnimationInterpolator>>? // default value: spring
  public let items: Field<[DivAnimationTemplate]>?
  public let name: Field<Expression<Name>>?
  public let repeatCount: Field<DivCountTemplate>? // default value: .divInfinityCount(DivInfinityCount())
  public let startDelay: Field<Expression<Int>>? // constraint: number >= 0; default value: 0
  public let startValue: Field<Expression<Double>>?

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      duration: dictionary.getOptionalExpressionField("duration"),
      endValue: dictionary.getOptionalExpressionField("end_value"),
      interpolator: dictionary.getOptionalExpressionField("interpolator"),
      items: dictionary.getOptionalArray("items", templateToType: templateToType),
      name: dictionary.getOptionalExpressionField("name"),
      repeatCount: dictionary.getOptionalField("repeat", templateToType: templateToType),
      startDelay: dictionary.getOptionalExpressionField("start_delay"),
      startValue: dictionary.getOptionalExpressionField("start_value")
    )
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
    let durationValue = { parent?.duration?.resolveOptionalValue(context: context, validator: ResolvedValue.durationValidator) ?? .noValue }()
    let endValueValue = { parent?.endValue?.resolveOptionalValue(context: context) ?? .noValue }()
    let interpolatorValue = { parent?.interpolator?.resolveOptionalValue(context: context) ?? .noValue }()
    let itemsValue = { parent?.items?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let nameValue = { parent?.name?.resolveValue(context: context) ?? .noValue }()
    let repeatCountValue = { parent?.repeatCount?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let startDelayValue = { parent?.startDelay?.resolveOptionalValue(context: context, validator: ResolvedValue.startDelayValidator) ?? .noValue }()
    let startValueValue = { parent?.startValue?.resolveOptionalValue(context: context) ?? .noValue }()
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
      duration: { durationValue.value }(),
      endValue: { endValueValue.value }(),
      interpolator: { interpolatorValue.value }(),
      items: { itemsValue.value }(),
      name: { nameNonNil }(),
      repeatCount: { repeatCountValue.value }(),
      startDelay: { startDelayValue.value }(),
      startValue: { startValueValue.value }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivAnimationTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivAnimation> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var durationValue: DeserializationResult<Expression<Int>> = { parent?.duration?.value() ?? .noValue }()
    var endValueValue: DeserializationResult<Expression<Double>> = { parent?.endValue?.value() ?? .noValue }()
    var interpolatorValue: DeserializationResult<Expression<DivAnimationInterpolator>> = { parent?.interpolator?.value() ?? .noValue }()
    var itemsValue: DeserializationResult<[DivAnimation]> = .noValue
    var nameValue: DeserializationResult<Expression<DivAnimation.Name>> = { parent?.name?.value() ?? .noValue }()
    var repeatCountValue: DeserializationResult<DivCount> = .noValue
    var startDelayValue: DeserializationResult<Expression<Int>> = { parent?.startDelay?.value() ?? .noValue }()
    var startValueValue: DeserializationResult<Expression<Double>> = { parent?.startValue?.value() ?? .noValue }()
    _ = {
      // Each field is parsed in its own lambda to keep the stack size managable
      // Otherwise the compiler will allocate stack for each intermediate variable
      // upfront even when we don't actually visit a relevant branch
      for (key, __dictValue) in context.templateData {
        _ = {
          if key == "duration" {
           durationValue = deserialize(__dictValue, validator: ResolvedValue.durationValidator).merged(with: durationValue)
          }
        }()
        _ = {
          if key == "end_value" {
           endValueValue = deserialize(__dictValue).merged(with: endValueValue)
          }
        }()
        _ = {
          if key == "interpolator" {
           interpolatorValue = deserialize(__dictValue).merged(with: interpolatorValue)
          }
        }()
        _ = {
          if key == "items" {
           itemsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivAnimationTemplate.self).merged(with: itemsValue)
          }
        }()
        _ = {
          if key == "name" {
           nameValue = deserialize(__dictValue).merged(with: nameValue)
          }
        }()
        _ = {
          if key == "repeat" {
           repeatCountValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivCountTemplate.self).merged(with: repeatCountValue)
          }
        }()
        _ = {
          if key == "start_delay" {
           startDelayValue = deserialize(__dictValue, validator: ResolvedValue.startDelayValidator).merged(with: startDelayValue)
          }
        }()
        _ = {
          if key == "start_value" {
           startValueValue = deserialize(__dictValue).merged(with: startValueValue)
          }
        }()
        _ = {
         if key == parent?.duration?.link {
           durationValue = durationValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.durationValidator) })
          }
        }()
        _ = {
         if key == parent?.endValue?.link {
           endValueValue = endValueValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.interpolator?.link {
           interpolatorValue = interpolatorValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.items?.link {
           itemsValue = itemsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivAnimationTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.name?.link {
           nameValue = nameValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.repeatCount?.link {
           repeatCountValue = repeatCountValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivCountTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.startDelay?.link {
           startDelayValue = startDelayValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.startDelayValidator) })
          }
        }()
        _ = {
         if key == parent?.startValue?.link {
           startValueValue = startValueValue.merged(with: { deserialize(__dictValue) })
          }
        }()
      }
    }()
    if let parent = parent {
      _ = { itemsValue = itemsValue.merged(with: { parent.items?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { repeatCountValue = repeatCountValue.merged(with: { parent.repeatCount?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
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
      duration: { durationValue.value }(),
      endValue: { endValueValue.value }(),
      interpolator: { interpolatorValue.value }(),
      items: { itemsValue.value }(),
      name: { nameNonNil }(),
      repeatCount: { repeatCountValue.value }(),
      startDelay: { startDelayValue.value }(),
      startValue: { startValueValue.value }()
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
