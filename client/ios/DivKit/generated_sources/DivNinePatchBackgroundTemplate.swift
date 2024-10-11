// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivNinePatchBackgroundTemplate: TemplateValue {
  public static let type: String = "nine_patch_image"
  public let parent: String?
  public let imageUrl: Field<Expression<URL>>?
  public let insets: Field<DivAbsoluteEdgeInsetsTemplate>?

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      parent: dictionary["type"] as? String,
      imageUrl: dictionary.getOptionalExpressionField("image_url", transform: URL.init(string:)),
      insets: dictionary.getOptionalField("insets", templateToType: templateToType)
    )
  }

  init(
    parent: String?,
    imageUrl: Field<Expression<URL>>? = nil,
    insets: Field<DivAbsoluteEdgeInsetsTemplate>? = nil
  ) {
    self.parent = parent
    self.imageUrl = imageUrl
    self.insets = insets
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivNinePatchBackgroundTemplate?) -> DeserializationResult<DivNinePatchBackground> {
    let imageUrlValue = { parent?.imageUrl?.resolveValue(context: context, transform: URL.init(string:)) ?? .noValue }()
    let insetsValue = { parent?.insets?.resolveValue(context: context, useOnlyLinks: true) ?? .noValue }()
    var errors = mergeErrors(
      imageUrlValue.errorsOrWarnings?.map { .nestedObjectError(field: "image_url", error: $0) },
      insetsValue.errorsOrWarnings?.map { .nestedObjectError(field: "insets", error: $0) }
    )
    if case .noValue = imageUrlValue {
      errors.append(.requiredFieldIsMissing(field: "image_url"))
    }
    if case .noValue = insetsValue {
      errors.append(.requiredFieldIsMissing(field: "insets"))
    }
    guard
      let imageUrlNonNil = imageUrlValue.value,
      let insetsNonNil = insetsValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivNinePatchBackground(
      imageUrl: { imageUrlNonNil }(),
      insets: { insetsNonNil }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivNinePatchBackgroundTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivNinePatchBackground> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var imageUrlValue: DeserializationResult<Expression<URL>> = { parent?.imageUrl?.value() ?? .noValue }()
    var insetsValue: DeserializationResult<DivAbsoluteEdgeInsets> = .noValue
    _ = {
      // Each field is parsed in its own lambda to keep the stack size managable
      // Otherwise the compiler will allocate stack for each intermediate variable
      // upfront even when we don't actually visit a relevant branch
      for (key, __dictValue) in context.templateData {
        _ = {
          if key == "image_url" {
           imageUrlValue = deserialize(__dictValue, transform: URL.init(string:)).merged(with: imageUrlValue)
          }
        }()
        _ = {
          if key == "insets" {
           insetsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivAbsoluteEdgeInsetsTemplate.self).merged(with: insetsValue)
          }
        }()
        _ = {
         if key == parent?.imageUrl?.link {
           imageUrlValue = imageUrlValue.merged(with: { deserialize(__dictValue, transform: URL.init(string:)) })
          }
        }()
        _ = {
         if key == parent?.insets?.link {
           insetsValue = insetsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivAbsoluteEdgeInsetsTemplate.self) })
          }
        }()
      }
    }()
    if let parent = parent {
      _ = { insetsValue = insetsValue.merged(with: { parent.insets?.resolveValue(context: context, useOnlyLinks: true) }) }()
    }
    var errors = mergeErrors(
      imageUrlValue.errorsOrWarnings?.map { .nestedObjectError(field: "image_url", error: $0) },
      insetsValue.errorsOrWarnings?.map { .nestedObjectError(field: "insets", error: $0) }
    )
    if case .noValue = imageUrlValue {
      errors.append(.requiredFieldIsMissing(field: "image_url"))
    }
    if case .noValue = insetsValue {
      errors.append(.requiredFieldIsMissing(field: "insets"))
    }
    guard
      let imageUrlNonNil = imageUrlValue.value,
      let insetsNonNil = insetsValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivNinePatchBackground(
      imageUrl: { imageUrlNonNil }(),
      insets: { insetsNonNil }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivNinePatchBackgroundTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? DivNinePatchBackgroundTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return DivNinePatchBackgroundTemplate(
      parent: nil,
      imageUrl: imageUrl ?? mergedParent.imageUrl,
      insets: insets ?? mergedParent.insets
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivNinePatchBackgroundTemplate {
    let merged = try mergedWithParent(templates: templates)

    return DivNinePatchBackgroundTemplate(
      parent: nil,
      imageUrl: merged.imageUrl,
      insets: try merged.insets?.resolveParent(templates: templates)
    )
  }
}
