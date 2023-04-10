// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivSlideTransitionTemplate: TemplateValue {
  public typealias Edge = DivSlideTransition.Edge

  public static let type: String = "slide"
  public let parent: String? // at least 1 char
  public let distance: Field<DivDimensionTemplate>?
  public let duration: Field<Expression<Int>>? // constraint: number >= 0; default value: 200
  public let edge: Field<Expression<Edge>>? // default value: bottom
  public let interpolator: Field<Expression<DivAnimationInterpolator>>? // default value: ease_in_out
  public let startDelay: Field<Expression<Int>>? // constraint: number >= 0; default value: 0

  static let parentValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      parent: try dictionary.getOptionalField("type", validator: Self.parentValidator),
      distance: try dictionary.getOptionalField("distance", templateToType: templateToType),
      duration: try dictionary.getOptionalExpressionField("duration"),
      edge: try dictionary.getOptionalExpressionField("edge"),
      interpolator: try dictionary.getOptionalExpressionField("interpolator"),
      startDelay: try dictionary.getOptionalExpressionField("start_delay")
    )
  }

  init(
    parent: String?,
    distance: Field<DivDimensionTemplate>? = nil,
    duration: Field<Expression<Int>>? = nil,
    edge: Field<Expression<Edge>>? = nil,
    interpolator: Field<Expression<DivAnimationInterpolator>>? = nil,
    startDelay: Field<Expression<Int>>? = nil
  ) {
    self.parent = parent
    self.distance = distance
    self.duration = duration
    self.edge = edge
    self.interpolator = interpolator
    self.startDelay = startDelay
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivSlideTransitionTemplate?) -> DeserializationResult<DivSlideTransition> {
    let distanceValue = parent?.distance?.resolveOptionalValue(context: context, validator: ResolvedValue.distanceValidator, useOnlyLinks: true) ?? .noValue
    let durationValue = parent?.duration?.resolveOptionalValue(context: context, validator: ResolvedValue.durationValidator) ?? .noValue
    let edgeValue = parent?.edge?.resolveOptionalValue(context: context, validator: ResolvedValue.edgeValidator) ?? .noValue
    let interpolatorValue = parent?.interpolator?.resolveOptionalValue(context: context, validator: ResolvedValue.interpolatorValidator) ?? .noValue
    let startDelayValue = parent?.startDelay?.resolveOptionalValue(context: context, validator: ResolvedValue.startDelayValidator) ?? .noValue
    let errors = mergeErrors(
      distanceValue.errorsOrWarnings?.map { .nestedObjectError(field: "distance", error: $0) },
      durationValue.errorsOrWarnings?.map { .nestedObjectError(field: "duration", error: $0) },
      edgeValue.errorsOrWarnings?.map { .nestedObjectError(field: "edge", error: $0) },
      interpolatorValue.errorsOrWarnings?.map { .nestedObjectError(field: "interpolator", error: $0) },
      startDelayValue.errorsOrWarnings?.map { .nestedObjectError(field: "start_delay", error: $0) }
    )
    let result = DivSlideTransition(
      distance: distanceValue.value,
      duration: durationValue.value,
      edge: edgeValue.value,
      interpolator: interpolatorValue.value,
      startDelay: startDelayValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivSlideTransitionTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivSlideTransition> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var distanceValue: DeserializationResult<DivDimension> = .noValue
    var durationValue: DeserializationResult<Expression<Int>> = parent?.duration?.value() ?? .noValue
    var edgeValue: DeserializationResult<Expression<DivSlideTransition.Edge>> = parent?.edge?.value() ?? .noValue
    var interpolatorValue: DeserializationResult<Expression<DivAnimationInterpolator>> = parent?.interpolator?.value() ?? .noValue
    var startDelayValue: DeserializationResult<Expression<Int>> = parent?.startDelay?.value() ?? .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "distance":
        distanceValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.distanceValidator, type: DivDimensionTemplate.self).merged(with: distanceValue)
      case "duration":
        durationValue = deserialize(__dictValue, validator: ResolvedValue.durationValidator).merged(with: durationValue)
      case "edge":
        edgeValue = deserialize(__dictValue, validator: ResolvedValue.edgeValidator).merged(with: edgeValue)
      case "interpolator":
        interpolatorValue = deserialize(__dictValue, validator: ResolvedValue.interpolatorValidator).merged(with: interpolatorValue)
      case "start_delay":
        startDelayValue = deserialize(__dictValue, validator: ResolvedValue.startDelayValidator).merged(with: startDelayValue)
      case parent?.distance?.link:
        distanceValue = distanceValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.distanceValidator, type: DivDimensionTemplate.self))
      case parent?.duration?.link:
        durationValue = durationValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.durationValidator))
      case parent?.edge?.link:
        edgeValue = edgeValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.edgeValidator))
      case parent?.interpolator?.link:
        interpolatorValue = interpolatorValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.interpolatorValidator))
      case parent?.startDelay?.link:
        startDelayValue = startDelayValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.startDelayValidator))
      default: break
      }
    }
    if let parent = parent {
      distanceValue = distanceValue.merged(with: parent.distance?.resolveOptionalValue(context: context, validator: ResolvedValue.distanceValidator, useOnlyLinks: true))
    }
    let errors = mergeErrors(
      distanceValue.errorsOrWarnings?.map { .nestedObjectError(field: "distance", error: $0) },
      durationValue.errorsOrWarnings?.map { .nestedObjectError(field: "duration", error: $0) },
      edgeValue.errorsOrWarnings?.map { .nestedObjectError(field: "edge", error: $0) },
      interpolatorValue.errorsOrWarnings?.map { .nestedObjectError(field: "interpolator", error: $0) },
      startDelayValue.errorsOrWarnings?.map { .nestedObjectError(field: "start_delay", error: $0) }
    )
    let result = DivSlideTransition(
      distance: distanceValue.value,
      duration: durationValue.value,
      edge: edgeValue.value,
      interpolator: interpolatorValue.value,
      startDelay: startDelayValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivSlideTransitionTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? DivSlideTransitionTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return DivSlideTransitionTemplate(
      parent: nil,
      distance: distance ?? mergedParent.distance,
      duration: duration ?? mergedParent.duration,
      edge: edge ?? mergedParent.edge,
      interpolator: interpolator ?? mergedParent.interpolator,
      startDelay: startDelay ?? mergedParent.startDelay
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivSlideTransitionTemplate {
    let merged = try mergedWithParent(templates: templates)

    return DivSlideTransitionTemplate(
      parent: nil,
      distance: merged.distance?.tryResolveParent(templates: templates),
      duration: merged.duration,
      edge: merged.edge,
      interpolator: merged.interpolator,
      startDelay: merged.startDelay
    )
  }
}
