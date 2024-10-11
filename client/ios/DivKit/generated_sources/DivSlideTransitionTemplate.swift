// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivSlideTransitionTemplate: TemplateValue {
  public typealias Edge = DivSlideTransition.Edge

  public static let type: String = "slide"
  public let parent: String?
  public let distance: Field<DivDimensionTemplate>?
  public let duration: Field<Expression<Int>>? // constraint: number >= 0; default value: 200
  public let edge: Field<Expression<Edge>>? // default value: bottom
  public let interpolator: Field<Expression<DivAnimationInterpolator>>? // default value: ease_in_out
  public let startDelay: Field<Expression<Int>>? // constraint: number >= 0; default value: 0

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      parent: dictionary["type"] as? String,
      distance: dictionary.getOptionalField("distance", templateToType: templateToType),
      duration: dictionary.getOptionalExpressionField("duration"),
      edge: dictionary.getOptionalExpressionField("edge"),
      interpolator: dictionary.getOptionalExpressionField("interpolator"),
      startDelay: dictionary.getOptionalExpressionField("start_delay")
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
    let distanceValue = { parent?.distance?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let durationValue = { parent?.duration?.resolveOptionalValue(context: context, validator: ResolvedValue.durationValidator) ?? .noValue }()
    let edgeValue = { parent?.edge?.resolveOptionalValue(context: context) ?? .noValue }()
    let interpolatorValue = { parent?.interpolator?.resolveOptionalValue(context: context) ?? .noValue }()
    let startDelayValue = { parent?.startDelay?.resolveOptionalValue(context: context, validator: ResolvedValue.startDelayValidator) ?? .noValue }()
    let errors = mergeErrors(
      distanceValue.errorsOrWarnings?.map { .nestedObjectError(field: "distance", error: $0) },
      durationValue.errorsOrWarnings?.map { .nestedObjectError(field: "duration", error: $0) },
      edgeValue.errorsOrWarnings?.map { .nestedObjectError(field: "edge", error: $0) },
      interpolatorValue.errorsOrWarnings?.map { .nestedObjectError(field: "interpolator", error: $0) },
      startDelayValue.errorsOrWarnings?.map { .nestedObjectError(field: "start_delay", error: $0) }
    )
    let result = DivSlideTransition(
      distance: { distanceValue.value }(),
      duration: { durationValue.value }(),
      edge: { edgeValue.value }(),
      interpolator: { interpolatorValue.value }(),
      startDelay: { startDelayValue.value }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivSlideTransitionTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivSlideTransition> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var distanceValue: DeserializationResult<DivDimension> = .noValue
    var durationValue: DeserializationResult<Expression<Int>> = { parent?.duration?.value() ?? .noValue }()
    var edgeValue: DeserializationResult<Expression<DivSlideTransition.Edge>> = { parent?.edge?.value() ?? .noValue }()
    var interpolatorValue: DeserializationResult<Expression<DivAnimationInterpolator>> = { parent?.interpolator?.value() ?? .noValue }()
    var startDelayValue: DeserializationResult<Expression<Int>> = { parent?.startDelay?.value() ?? .noValue }()
    _ = {
      // Each field is parsed in its own lambda to keep the stack size managable
      // Otherwise the compiler will allocate stack for each intermediate variable
      // upfront even when we don't actually visit a relevant branch
      for (key, __dictValue) in context.templateData {
        _ = {
          if key == "distance" {
           distanceValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivDimensionTemplate.self).merged(with: distanceValue)
          }
        }()
        _ = {
          if key == "duration" {
           durationValue = deserialize(__dictValue, validator: ResolvedValue.durationValidator).merged(with: durationValue)
          }
        }()
        _ = {
          if key == "edge" {
           edgeValue = deserialize(__dictValue).merged(with: edgeValue)
          }
        }()
        _ = {
          if key == "interpolator" {
           interpolatorValue = deserialize(__dictValue).merged(with: interpolatorValue)
          }
        }()
        _ = {
          if key == "start_delay" {
           startDelayValue = deserialize(__dictValue, validator: ResolvedValue.startDelayValidator).merged(with: startDelayValue)
          }
        }()
        _ = {
         if key == parent?.distance?.link {
           distanceValue = distanceValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivDimensionTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.duration?.link {
           durationValue = durationValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.durationValidator) })
          }
        }()
        _ = {
         if key == parent?.edge?.link {
           edgeValue = edgeValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.interpolator?.link {
           interpolatorValue = interpolatorValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.startDelay?.link {
           startDelayValue = startDelayValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.startDelayValidator) })
          }
        }()
      }
    }()
    if let parent = parent {
      _ = { distanceValue = distanceValue.merged(with: { parent.distance?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
    }
    let errors = mergeErrors(
      distanceValue.errorsOrWarnings?.map { .nestedObjectError(field: "distance", error: $0) },
      durationValue.errorsOrWarnings?.map { .nestedObjectError(field: "duration", error: $0) },
      edgeValue.errorsOrWarnings?.map { .nestedObjectError(field: "edge", error: $0) },
      interpolatorValue.errorsOrWarnings?.map { .nestedObjectError(field: "interpolator", error: $0) },
      startDelayValue.errorsOrWarnings?.map { .nestedObjectError(field: "start_delay", error: $0) }
    )
    let result = DivSlideTransition(
      distance: { distanceValue.value }(),
      duration: { durationValue.value }(),
      edge: { edgeValue.value }(),
      interpolator: { interpolatorValue.value }(),
      startDelay: { startDelayValue.value }()
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
