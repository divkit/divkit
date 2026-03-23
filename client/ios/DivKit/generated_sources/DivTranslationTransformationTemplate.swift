// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivTranslationTransformationTemplate: TemplateValue, Sendable {
  public static let type: String = "translation"
  public let parent: String?
  public let x: Field<DivTranslationTemplate>?
  public let y: Field<DivTranslationTemplate>?

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      parent: dictionary["type"] as? String,
      x: dictionary.getOptionalField("x", templateToType: templateToType),
      y: dictionary.getOptionalField("y", templateToType: templateToType)
    )
  }

  init(
    parent: String?,
    x: Field<DivTranslationTemplate>? = nil,
    y: Field<DivTranslationTemplate>? = nil
  ) {
    self.parent = parent
    self.x = x
    self.y = y
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivTranslationTransformationTemplate?) -> DeserializationResult<DivTranslationTransformation> {
    let xValue = { parent?.x?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let yValue = { parent?.y?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let errors = mergeErrors(
      xValue.errorsOrWarnings?.map { .nestedObjectError(field: "x", error: $0) },
      yValue.errorsOrWarnings?.map { .nestedObjectError(field: "y", error: $0) }
    )
    let result = DivTranslationTransformation(
      x: { xValue.value }(),
      y: { yValue.value }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivTranslationTransformationTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivTranslationTransformation> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var xValue: DeserializationResult<DivTranslation> = .noValue
    var yValue: DeserializationResult<DivTranslation> = .noValue
    _ = {
      // Each field is parsed in its own lambda to keep the stack size managable
      // Otherwise the compiler will allocate stack for each intermediate variable
      // upfront even when we don't actually visit a relevant branch
      for (key, __dictValue) in context.templateData {
        _ = {
          if key == "x" {
           xValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTranslationTemplate.self).merged(with: xValue)
          }
        }()
        _ = {
          if key == "y" {
           yValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTranslationTemplate.self).merged(with: yValue)
          }
        }()
        _ = {
         if key == parent?.x?.link {
           xValue = xValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTranslationTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.y?.link {
           yValue = yValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTranslationTemplate.self) })
          }
        }()
      }
    }()
    if let parent = parent {
      _ = { xValue = xValue.merged(with: { parent.x?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { yValue = yValue.merged(with: { parent.y?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
    }
    let errors = mergeErrors(
      xValue.errorsOrWarnings?.map { .nestedObjectError(field: "x", error: $0) },
      yValue.errorsOrWarnings?.map { .nestedObjectError(field: "y", error: $0) }
    )
    let result = DivTranslationTransformation(
      x: { xValue.value }(),
      y: { yValue.value }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivTranslationTransformationTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? DivTranslationTransformationTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return DivTranslationTransformationTemplate(
      parent: nil,
      x: x ?? mergedParent.x,
      y: y ?? mergedParent.y
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivTranslationTransformationTemplate {
    let merged = try mergedWithParent(templates: templates)

    return DivTranslationTransformationTemplate(
      parent: nil,
      x: merged.x?.tryResolveParent(templates: templates),
      y: merged.y?.tryResolveParent(templates: templates)
    )
  }
}
