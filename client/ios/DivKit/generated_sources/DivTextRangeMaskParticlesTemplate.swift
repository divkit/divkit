// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivTextRangeMaskParticlesTemplate: TemplateValue, Sendable {
  public static let type: String = "particles"
  public let parent: String?
  public let color: Field<Expression<Color>>?
  public let density: Field<Expression<Double>>? // default value: 0.8
  public let isAnimated: Field<Expression<Bool>>? // default value: false
  public let isEnabled: Field<Expression<Bool>>? // default value: true
  public let particleSize: Field<DivFixedSizeTemplate>? // default value: DivFixedSize(value: .value(1))

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      parent: dictionary["type"] as? String,
      color: dictionary.getOptionalExpressionField("color", transform: Color.color(withHexString:)),
      density: dictionary.getOptionalExpressionField("density"),
      isAnimated: dictionary.getOptionalExpressionField("is_animated"),
      isEnabled: dictionary.getOptionalExpressionField("is_enabled"),
      particleSize: dictionary.getOptionalField("particle_size", templateToType: templateToType)
    )
  }

  init(
    parent: String?,
    color: Field<Expression<Color>>? = nil,
    density: Field<Expression<Double>>? = nil,
    isAnimated: Field<Expression<Bool>>? = nil,
    isEnabled: Field<Expression<Bool>>? = nil,
    particleSize: Field<DivFixedSizeTemplate>? = nil
  ) {
    self.parent = parent
    self.color = color
    self.density = density
    self.isAnimated = isAnimated
    self.isEnabled = isEnabled
    self.particleSize = particleSize
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivTextRangeMaskParticlesTemplate?) -> DeserializationResult<DivTextRangeMaskParticles> {
    let colorValue = { parent?.color?.resolveValue(context: context, transform: Color.color(withHexString:)) ?? .noValue }()
    let densityValue = { parent?.density?.resolveOptionalValue(context: context) ?? .noValue }()
    let isAnimatedValue = { parent?.isAnimated?.resolveOptionalValue(context: context) ?? .noValue }()
    let isEnabledValue = { parent?.isEnabled?.resolveOptionalValue(context: context) ?? .noValue }()
    let particleSizeValue = { parent?.particleSize?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    var errors = mergeErrors(
      colorValue.errorsOrWarnings?.map { .nestedObjectError(field: "color", error: $0) },
      densityValue.errorsOrWarnings?.map { .nestedObjectError(field: "density", error: $0) },
      isAnimatedValue.errorsOrWarnings?.map { .nestedObjectError(field: "is_animated", error: $0) },
      isEnabledValue.errorsOrWarnings?.map { .nestedObjectError(field: "is_enabled", error: $0) },
      particleSizeValue.errorsOrWarnings?.map { .nestedObjectError(field: "particle_size", error: $0) }
    )
    if case .noValue = colorValue {
      errors.append(.requiredFieldIsMissing(field: "color"))
    }
    guard
      let colorNonNil = colorValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivTextRangeMaskParticles(
      color: { colorNonNil }(),
      density: { densityValue.value }(),
      isAnimated: { isAnimatedValue.value }(),
      isEnabled: { isEnabledValue.value }(),
      particleSize: { particleSizeValue.value }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivTextRangeMaskParticlesTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivTextRangeMaskParticles> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var colorValue: DeserializationResult<Expression<Color>> = { parent?.color?.value() ?? .noValue }()
    var densityValue: DeserializationResult<Expression<Double>> = { parent?.density?.value() ?? .noValue }()
    var isAnimatedValue: DeserializationResult<Expression<Bool>> = { parent?.isAnimated?.value() ?? .noValue }()
    var isEnabledValue: DeserializationResult<Expression<Bool>> = { parent?.isEnabled?.value() ?? .noValue }()
    var particleSizeValue: DeserializationResult<DivFixedSize> = .noValue
    _ = {
      // Each field is parsed in its own lambda to keep the stack size managable
      // Otherwise the compiler will allocate stack for each intermediate variable
      // upfront even when we don't actually visit a relevant branch
      for (key, __dictValue) in context.templateData {
        _ = {
          if key == "color" {
           colorValue = deserialize(__dictValue, transform: Color.color(withHexString:)).merged(with: colorValue)
          }
        }()
        _ = {
          if key == "density" {
           densityValue = deserialize(__dictValue).merged(with: densityValue)
          }
        }()
        _ = {
          if key == "is_animated" {
           isAnimatedValue = deserialize(__dictValue).merged(with: isAnimatedValue)
          }
        }()
        _ = {
          if key == "is_enabled" {
           isEnabledValue = deserialize(__dictValue).merged(with: isEnabledValue)
          }
        }()
        _ = {
          if key == "particle_size" {
           particleSizeValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivFixedSizeTemplate.self).merged(with: particleSizeValue)
          }
        }()
        _ = {
         if key == parent?.color?.link {
           colorValue = colorValue.merged(with: { deserialize(__dictValue, transform: Color.color(withHexString:)) })
          }
        }()
        _ = {
         if key == parent?.density?.link {
           densityValue = densityValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.isAnimated?.link {
           isAnimatedValue = isAnimatedValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.isEnabled?.link {
           isEnabledValue = isEnabledValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.particleSize?.link {
           particleSizeValue = particleSizeValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivFixedSizeTemplate.self) })
          }
        }()
      }
    }()
    if let parent = parent {
      _ = { particleSizeValue = particleSizeValue.merged(with: { parent.particleSize?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
    }
    var errors = mergeErrors(
      colorValue.errorsOrWarnings?.map { .nestedObjectError(field: "color", error: $0) },
      densityValue.errorsOrWarnings?.map { .nestedObjectError(field: "density", error: $0) },
      isAnimatedValue.errorsOrWarnings?.map { .nestedObjectError(field: "is_animated", error: $0) },
      isEnabledValue.errorsOrWarnings?.map { .nestedObjectError(field: "is_enabled", error: $0) },
      particleSizeValue.errorsOrWarnings?.map { .nestedObjectError(field: "particle_size", error: $0) }
    )
    if case .noValue = colorValue {
      errors.append(.requiredFieldIsMissing(field: "color"))
    }
    guard
      let colorNonNil = colorValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivTextRangeMaskParticles(
      color: { colorNonNil }(),
      density: { densityValue.value }(),
      isAnimated: { isAnimatedValue.value }(),
      isEnabled: { isEnabledValue.value }(),
      particleSize: { particleSizeValue.value }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivTextRangeMaskParticlesTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? DivTextRangeMaskParticlesTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return DivTextRangeMaskParticlesTemplate(
      parent: nil,
      color: color ?? mergedParent.color,
      density: density ?? mergedParent.density,
      isAnimated: isAnimated ?? mergedParent.isAnimated,
      isEnabled: isEnabled ?? mergedParent.isEnabled,
      particleSize: particleSize ?? mergedParent.particleSize
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivTextRangeMaskParticlesTemplate {
    let merged = try mergedWithParent(templates: templates)

    return DivTextRangeMaskParticlesTemplate(
      parent: nil,
      color: merged.color,
      density: merged.density,
      isAnimated: merged.isAnimated,
      isEnabled: merged.isEnabled,
      particleSize: merged.particleSize?.tryResolveParent(templates: templates)
    )
  }
}
