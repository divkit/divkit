// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivPointTemplate: TemplateValue, Sendable {
  public let x: Field<DivDimensionTemplate>?
  public let y: Field<DivDimensionTemplate>?

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      x: dictionary.getOptionalField("x", templateToType: templateToType),
      y: dictionary.getOptionalField("y", templateToType: templateToType)
    )
  }

  init(
    x: Field<DivDimensionTemplate>? = nil,
    y: Field<DivDimensionTemplate>? = nil
  ) {
    self.x = x
    self.y = y
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivPointTemplate?) -> DeserializationResult<DivPoint> {
    let xValue = { parent?.x?.resolveValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let yValue = { parent?.y?.resolveValue(context: context, useOnlyLinks: true) ?? .noValue }()
    var errors = mergeErrors(
      xValue.errorsOrWarnings?.map { .nestedObjectError(field: "x", error: $0) },
      yValue.errorsOrWarnings?.map { .nestedObjectError(field: "y", error: $0) }
    )
    if case .noValue = xValue {
      errors.append(.requiredFieldIsMissing(field: "x"))
    }
    if case .noValue = yValue {
      errors.append(.requiredFieldIsMissing(field: "y"))
    }
    guard
      let xNonNil = xValue.value,
      let yNonNil = yValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivPoint(
      x: { xNonNil }(),
      y: { yNonNil }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivPointTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivPoint> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var xValue: DeserializationResult<DivDimension> = .noValue
    var yValue: DeserializationResult<DivDimension> = .noValue
    _ = {
      // Each field is parsed in its own lambda to keep the stack size managable
      // Otherwise the compiler will allocate stack for each intermediate variable
      // upfront even when we don't actually visit a relevant branch
      for (key, __dictValue) in context.templateData {
        _ = {
          if key == "x" {
           xValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivDimensionTemplate.self).merged(with: xValue)
          }
        }()
        _ = {
          if key == "y" {
           yValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivDimensionTemplate.self).merged(with: yValue)
          }
        }()
        _ = {
         if key == parent?.x?.link {
           xValue = xValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivDimensionTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.y?.link {
           yValue = yValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivDimensionTemplate.self) })
          }
        }()
      }
    }()
    if let parent = parent {
      _ = { xValue = xValue.merged(with: { parent.x?.resolveValue(context: context, useOnlyLinks: true) }) }()
      _ = { yValue = yValue.merged(with: { parent.y?.resolveValue(context: context, useOnlyLinks: true) }) }()
    }
    var errors = mergeErrors(
      xValue.errorsOrWarnings?.map { .nestedObjectError(field: "x", error: $0) },
      yValue.errorsOrWarnings?.map { .nestedObjectError(field: "y", error: $0) }
    )
    if case .noValue = xValue {
      errors.append(.requiredFieldIsMissing(field: "x"))
    }
    if case .noValue = yValue {
      errors.append(.requiredFieldIsMissing(field: "y"))
    }
    guard
      let xNonNil = xValue.value,
      let yNonNil = yValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivPoint(
      x: { xNonNil }(),
      y: { yNonNil }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivPointTemplate {
    return self
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivPointTemplate {
    let merged = try mergedWithParent(templates: templates)

    return DivPointTemplate(
      x: try merged.x?.resolveParent(templates: templates),
      y: try merged.y?.resolveParent(templates: templates)
    )
  }
}
