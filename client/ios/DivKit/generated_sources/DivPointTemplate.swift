// Generated code. Do not modify.

import CommonCore
import Foundation
import Serialization
import TemplatesSupport

public final class DivPointTemplate: TemplateValue, TemplateDeserializable {
  public let x: Field<DivDimensionTemplate>?
  public let y: Field<DivDimensionTemplate>?

  public convenience init(dictionary: [String: Any], templateToType: TemplateToType) throws {
    do {
      self.init(
        x: try dictionary.getOptionalField("x", templateToType: templateToType),
        y: try dictionary.getOptionalField("y", templateToType: templateToType)
      )
    } catch let DeserializationError.invalidFieldRepresentation(fieldName: field, representation: representation) {
      throw DeserializationError.invalidFieldRepresentation(fieldName: "div-point_template." + field, representation: representation)
    }
  }

  init(
    x: Field<DivDimensionTemplate>? = nil,
    y: Field<DivDimensionTemplate>? = nil
  ) {
    self.x = x
    self.y = y
  }

  private static func resolveOnlyLinks(context: Context, parent: DivPointTemplate?) -> DeserializationResult<DivPoint> {
    let xValue = parent?.x?.resolveValue(context: context, useOnlyLinks: true) ?? .noValue
    let yValue = parent?.y?.resolveValue(context: context, useOnlyLinks: true) ?? .noValue
    var errors = mergeErrors(
      xValue.errorsOrWarnings?.map { .nestedObjectError(fieldName: "x", error: $0) },
      yValue.errorsOrWarnings?.map { .nestedObjectError(fieldName: "y", error: $0) }
    )
    if case .noValue = xValue {
      errors.append(.requiredFieldIsMissing(fieldName: "x"))
    }
    if case .noValue = yValue {
      errors.append(.requiredFieldIsMissing(fieldName: "y"))
    }
    guard
      let xNonNil = xValue.value,
      let yNonNil = yValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivPoint(
      x: xNonNil,
      y: yNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: Context, parent: DivPointTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivPoint> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var xValue: DeserializationResult<DivDimension> = .noValue
    var yValue: DeserializationResult<DivDimension> = .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "x":
        xValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivDimensionTemplate.self).merged(with: xValue)
      case "y":
        yValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivDimensionTemplate.self).merged(with: yValue)
      case parent?.x?.link:
        xValue = xValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivDimensionTemplate.self))
      case parent?.y?.link:
        yValue = yValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivDimensionTemplate.self))
      default: break
      }
    }
    if let parent = parent {
      xValue = xValue.merged(with: parent.x?.resolveValue(context: context, useOnlyLinks: true))
      yValue = yValue.merged(with: parent.y?.resolveValue(context: context, useOnlyLinks: true))
    }
    var errors = mergeErrors(
      xValue.errorsOrWarnings?.map { .nestedObjectError(fieldName: "x", error: $0) },
      yValue.errorsOrWarnings?.map { .nestedObjectError(fieldName: "y", error: $0) }
    )
    if case .noValue = xValue {
      errors.append(.requiredFieldIsMissing(fieldName: "x"))
    }
    if case .noValue = yValue {
      errors.append(.requiredFieldIsMissing(fieldName: "y"))
    }
    guard
      let xNonNil = xValue.value,
      let yNonNil = yValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivPoint(
      x: xNonNil,
      y: yNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: Templates) throws -> DivPointTemplate {
    return self
  }

  public func resolveParent(templates: Templates) throws -> DivPointTemplate {
    let merged = try mergedWithParent(templates: templates)

    return DivPointTemplate(
      x: try merged.x?.resolveParent(templates: templates),
      y: try merged.y?.resolveParent(templates: templates)
    )
  }
}
