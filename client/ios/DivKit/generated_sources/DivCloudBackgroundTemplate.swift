// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivCloudBackgroundTemplate: TemplateValue, Sendable {
  public static let type: String = "cloud"
  public let parent: String?
  public let color: Field<Expression<Color>>?
  public let cornerRadius: Field<Expression<Int>>? // constraint: number >= 0
  public let paddings: Field<DivEdgeInsetsTemplate>?

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      parent: dictionary["type"] as? String,
      color: dictionary.getOptionalExpressionField("color", transform: Color.color(withHexString:)),
      cornerRadius: dictionary.getOptionalExpressionField("corner_radius"),
      paddings: dictionary.getOptionalField("paddings", templateToType: templateToType)
    )
  }

  init(
    parent: String?,
    color: Field<Expression<Color>>? = nil,
    cornerRadius: Field<Expression<Int>>? = nil,
    paddings: Field<DivEdgeInsetsTemplate>? = nil
  ) {
    self.parent = parent
    self.color = color
    self.cornerRadius = cornerRadius
    self.paddings = paddings
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivCloudBackgroundTemplate?) -> DeserializationResult<DivCloudBackground> {
    let colorValue = { parent?.color?.resolveValue(context: context, transform: Color.color(withHexString:)) ?? .noValue }()
    let cornerRadiusValue = { parent?.cornerRadius?.resolveValue(context: context, validator: ResolvedValue.cornerRadiusValidator) ?? .noValue }()
    let paddingsValue = { parent?.paddings?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    var errors = mergeErrors(
      colorValue.errorsOrWarnings?.map { .nestedObjectError(field: "color", error: $0) },
      cornerRadiusValue.errorsOrWarnings?.map { .nestedObjectError(field: "corner_radius", error: $0) },
      paddingsValue.errorsOrWarnings?.map { .nestedObjectError(field: "paddings", error: $0) }
    )
    if case .noValue = colorValue {
      errors.append(.requiredFieldIsMissing(field: "color"))
    }
    if case .noValue = cornerRadiusValue {
      errors.append(.requiredFieldIsMissing(field: "corner_radius"))
    }
    guard
      let colorNonNil = colorValue.value,
      let cornerRadiusNonNil = cornerRadiusValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivCloudBackground(
      color: { colorNonNil }(),
      cornerRadius: { cornerRadiusNonNil }(),
      paddings: { paddingsValue.value }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivCloudBackgroundTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivCloudBackground> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var colorValue: DeserializationResult<Expression<Color>> = { parent?.color?.value() ?? .noValue }()
    var cornerRadiusValue: DeserializationResult<Expression<Int>> = { parent?.cornerRadius?.value() ?? .noValue }()
    var paddingsValue: DeserializationResult<DivEdgeInsets> = .noValue
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
          if key == "corner_radius" {
           cornerRadiusValue = deserialize(__dictValue, validator: ResolvedValue.cornerRadiusValidator).merged(with: cornerRadiusValue)
          }
        }()
        _ = {
          if key == "paddings" {
           paddingsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivEdgeInsetsTemplate.self).merged(with: paddingsValue)
          }
        }()
        _ = {
         if key == parent?.color?.link {
           colorValue = colorValue.merged(with: { deserialize(__dictValue, transform: Color.color(withHexString:)) })
          }
        }()
        _ = {
         if key == parent?.cornerRadius?.link {
           cornerRadiusValue = cornerRadiusValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.cornerRadiusValidator) })
          }
        }()
        _ = {
         if key == parent?.paddings?.link {
           paddingsValue = paddingsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivEdgeInsetsTemplate.self) })
          }
        }()
      }
    }()
    if let parent = parent {
      _ = { paddingsValue = paddingsValue.merged(with: { parent.paddings?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
    }
    var errors = mergeErrors(
      colorValue.errorsOrWarnings?.map { .nestedObjectError(field: "color", error: $0) },
      cornerRadiusValue.errorsOrWarnings?.map { .nestedObjectError(field: "corner_radius", error: $0) },
      paddingsValue.errorsOrWarnings?.map { .nestedObjectError(field: "paddings", error: $0) }
    )
    if case .noValue = colorValue {
      errors.append(.requiredFieldIsMissing(field: "color"))
    }
    if case .noValue = cornerRadiusValue {
      errors.append(.requiredFieldIsMissing(field: "corner_radius"))
    }
    guard
      let colorNonNil = colorValue.value,
      let cornerRadiusNonNil = cornerRadiusValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivCloudBackground(
      color: { colorNonNil }(),
      cornerRadius: { cornerRadiusNonNil }(),
      paddings: { paddingsValue.value }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivCloudBackgroundTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? DivCloudBackgroundTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return DivCloudBackgroundTemplate(
      parent: nil,
      color: color ?? mergedParent.color,
      cornerRadius: cornerRadius ?? mergedParent.cornerRadius,
      paddings: paddings ?? mergedParent.paddings
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivCloudBackgroundTemplate {
    let merged = try mergedWithParent(templates: templates)

    return DivCloudBackgroundTemplate(
      parent: nil,
      color: merged.color,
      cornerRadius: merged.cornerRadius,
      paddings: merged.paddings?.tryResolveParent(templates: templates)
    )
  }
}
