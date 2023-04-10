// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivNinePatchBackgroundTemplate: TemplateValue {
  public static let type: String = "nine_patch_image"
  public let parent: String? // at least 1 char
  public let imageUrl: Field<Expression<URL>>?
  public let insets: Field<DivAbsoluteEdgeInsetsTemplate>?

  static let parentValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    do {
      self.init(
        parent: try dictionary.getOptionalField("type", validator: Self.parentValidator),
        imageUrl: try dictionary.getOptionalExpressionField("image_url", transform: URL.init(string:)),
        insets: try dictionary.getOptionalField("insets", templateToType: templateToType)
      )
    } catch let DeserializationError.invalidFieldRepresentation(field: field, representation: representation) {
      throw DeserializationError.invalidFieldRepresentation(field: "div-nine-patch-background_template." + field, representation: representation)
    }
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
    let imageUrlValue = parent?.imageUrl?.resolveValue(context: context, transform: URL.init(string:)) ?? .noValue
    let insetsValue = parent?.insets?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    var errors = mergeErrors(
      imageUrlValue.errorsOrWarnings?.map { .nestedObjectError(field: "image_url", error: $0) },
      insetsValue.errorsOrWarnings?.map { .nestedObjectError(field: "insets", error: $0) }
    )
    if case .noValue = imageUrlValue {
      errors.append(.requiredFieldIsMissing(field: "image_url"))
    }
    guard
      let imageUrlNonNil = imageUrlValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivNinePatchBackground(
      imageUrl: imageUrlNonNil,
      insets: insetsValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivNinePatchBackgroundTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivNinePatchBackground> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var imageUrlValue: DeserializationResult<Expression<URL>> = parent?.imageUrl?.value() ?? .noValue
    var insetsValue: DeserializationResult<DivAbsoluteEdgeInsets> = .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "image_url":
        imageUrlValue = deserialize(__dictValue, transform: URL.init(string:)).merged(with: imageUrlValue)
      case "insets":
        insetsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivAbsoluteEdgeInsetsTemplate.self).merged(with: insetsValue)
      case parent?.imageUrl?.link:
        imageUrlValue = imageUrlValue.merged(with: deserialize(__dictValue, transform: URL.init(string:)))
      case parent?.insets?.link:
        insetsValue = insetsValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivAbsoluteEdgeInsetsTemplate.self))
      default: break
      }
    }
    if let parent = parent {
      insetsValue = insetsValue.merged(with: parent.insets?.resolveOptionalValue(context: context, useOnlyLinks: true))
    }
    var errors = mergeErrors(
      imageUrlValue.errorsOrWarnings?.map { .nestedObjectError(field: "image_url", error: $0) },
      insetsValue.errorsOrWarnings?.map { .nestedObjectError(field: "insets", error: $0) }
    )
    if case .noValue = imageUrlValue {
      errors.append(.requiredFieldIsMissing(field: "image_url"))
    }
    guard
      let imageUrlNonNil = imageUrlValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivNinePatchBackground(
      imageUrl: imageUrlNonNil,
      insets: insetsValue.value
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
      insets: merged.insets?.tryResolveParent(templates: templates)
    )
  }
}
