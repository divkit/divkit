// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivActionScrollToTemplate: TemplateValue, Sendable {
  public static let type: String = "scroll_to"
  public let parent: String?
  public let animated: Field<Expression<Bool>>? // default value: true
  public let destination: Field<DivActionScrollDestinationTemplate>?
  public let id: Field<Expression<String>>?

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      parent: dictionary["type"] as? String,
      animated: dictionary.getOptionalExpressionField("animated"),
      destination: dictionary.getOptionalField("destination", templateToType: templateToType),
      id: dictionary.getOptionalExpressionField("id")
    )
  }

  init(
    parent: String?,
    animated: Field<Expression<Bool>>? = nil,
    destination: Field<DivActionScrollDestinationTemplate>? = nil,
    id: Field<Expression<String>>? = nil
  ) {
    self.parent = parent
    self.animated = animated
    self.destination = destination
    self.id = id
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivActionScrollToTemplate?) -> DeserializationResult<DivActionScrollTo> {
    let animatedValue = { parent?.animated?.resolveOptionalValue(context: context) ?? .noValue }()
    let destinationValue = { parent?.destination?.resolveValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let idValue = { parent?.id?.resolveValue(context: context) ?? .noValue }()
    var errors = mergeErrors(
      animatedValue.errorsOrWarnings?.map { .nestedObjectError(field: "animated", error: $0) },
      destinationValue.errorsOrWarnings?.map { .nestedObjectError(field: "destination", error: $0) },
      idValue.errorsOrWarnings?.map { .nestedObjectError(field: "id", error: $0) }
    )
    if case .noValue = destinationValue {
      errors.append(.requiredFieldIsMissing(field: "destination"))
    }
    if case .noValue = idValue {
      errors.append(.requiredFieldIsMissing(field: "id"))
    }
    guard
      let destinationNonNil = destinationValue.value,
      let idNonNil = idValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivActionScrollTo(
      animated: { animatedValue.value }(),
      destination: { destinationNonNil }(),
      id: { idNonNil }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivActionScrollToTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivActionScrollTo> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var animatedValue: DeserializationResult<Expression<Bool>> = { parent?.animated?.value() ?? .noValue }()
    var destinationValue: DeserializationResult<DivActionScrollDestination> = .noValue
    var idValue: DeserializationResult<Expression<String>> = { parent?.id?.value() ?? .noValue }()
    _ = {
      // Each field is parsed in its own lambda to keep the stack size managable
      // Otherwise the compiler will allocate stack for each intermediate variable
      // upfront even when we don't actually visit a relevant branch
      for (key, __dictValue) in context.templateData {
        _ = {
          if key == "animated" {
           animatedValue = deserialize(__dictValue).merged(with: animatedValue)
          }
        }()
        _ = {
          if key == "destination" {
           destinationValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionScrollDestinationTemplate.self).merged(with: destinationValue)
          }
        }()
        _ = {
          if key == "id" {
           idValue = deserialize(__dictValue).merged(with: idValue)
          }
        }()
        _ = {
         if key == parent?.animated?.link {
           animatedValue = animatedValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.destination?.link {
           destinationValue = destinationValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionScrollDestinationTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.id?.link {
           idValue = idValue.merged(with: { deserialize(__dictValue) })
          }
        }()
      }
    }()
    if let parent = parent {
      _ = { destinationValue = destinationValue.merged(with: { parent.destination?.resolveValue(context: context, useOnlyLinks: true) }) }()
    }
    var errors = mergeErrors(
      animatedValue.errorsOrWarnings?.map { .nestedObjectError(field: "animated", error: $0) },
      destinationValue.errorsOrWarnings?.map { .nestedObjectError(field: "destination", error: $0) },
      idValue.errorsOrWarnings?.map { .nestedObjectError(field: "id", error: $0) }
    )
    if case .noValue = destinationValue {
      errors.append(.requiredFieldIsMissing(field: "destination"))
    }
    if case .noValue = idValue {
      errors.append(.requiredFieldIsMissing(field: "id"))
    }
    guard
      let destinationNonNil = destinationValue.value,
      let idNonNil = idValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivActionScrollTo(
      animated: { animatedValue.value }(),
      destination: { destinationNonNil }(),
      id: { idNonNil }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivActionScrollToTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? DivActionScrollToTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return DivActionScrollToTemplate(
      parent: nil,
      animated: animated ?? mergedParent.animated,
      destination: destination ?? mergedParent.destination,
      id: id ?? mergedParent.id
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivActionScrollToTemplate {
    let merged = try mergedWithParent(templates: templates)

    return DivActionScrollToTemplate(
      parent: nil,
      animated: merged.animated,
      destination: try merged.destination?.resolveParent(templates: templates),
      id: merged.id
    )
  }
}
