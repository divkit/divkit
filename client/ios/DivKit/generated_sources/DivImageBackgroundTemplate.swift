// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivImageBackgroundTemplate: TemplateValue {
  public static let type: String = "image"
  public let parent: String?
  public let alpha: Field<Expression<Double>>? // constraint: number >= 0.0 && number <= 1.0; default value: 1.0
  public let contentAlignmentHorizontal: Field<Expression<DivAlignmentHorizontal>>? // default value: center
  public let contentAlignmentVertical: Field<Expression<DivAlignmentVertical>>? // default value: center
  public let filters: Field<[DivFilterTemplate]>?
  public let imageUrl: Field<Expression<URL>>?
  public let preloadRequired: Field<Expression<Bool>>? // default value: false
  public let scale: Field<Expression<DivImageScale>>? // default value: fill

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    do {
      self.init(
        parent: try dictionary.getOptionalField("type"),
        alpha: try dictionary.getOptionalExpressionField("alpha"),
        contentAlignmentHorizontal: try dictionary.getOptionalExpressionField("content_alignment_horizontal"),
        contentAlignmentVertical: try dictionary.getOptionalExpressionField("content_alignment_vertical"),
        filters: try dictionary.getOptionalArray("filters", templateToType: templateToType),
        imageUrl: try dictionary.getOptionalExpressionField("image_url", transform: URL.init(string:)),
        preloadRequired: try dictionary.getOptionalExpressionField("preload_required"),
        scale: try dictionary.getOptionalExpressionField("scale")
      )
    } catch let DeserializationError.invalidFieldRepresentation(field: field, representation: representation) {
      throw DeserializationError.invalidFieldRepresentation(field: "div-image-background_template." + field, representation: representation)
    }
  }

  init(
    parent: String?,
    alpha: Field<Expression<Double>>? = nil,
    contentAlignmentHorizontal: Field<Expression<DivAlignmentHorizontal>>? = nil,
    contentAlignmentVertical: Field<Expression<DivAlignmentVertical>>? = nil,
    filters: Field<[DivFilterTemplate]>? = nil,
    imageUrl: Field<Expression<URL>>? = nil,
    preloadRequired: Field<Expression<Bool>>? = nil,
    scale: Field<Expression<DivImageScale>>? = nil
  ) {
    self.parent = parent
    self.alpha = alpha
    self.contentAlignmentHorizontal = contentAlignmentHorizontal
    self.contentAlignmentVertical = contentAlignmentVertical
    self.filters = filters
    self.imageUrl = imageUrl
    self.preloadRequired = preloadRequired
    self.scale = scale
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivImageBackgroundTemplate?) -> DeserializationResult<DivImageBackground> {
    let alphaValue = parent?.alpha?.resolveOptionalValue(context: context, validator: ResolvedValue.alphaValidator) ?? .noValue
    let contentAlignmentHorizontalValue = parent?.contentAlignmentHorizontal?.resolveOptionalValue(context: context) ?? .noValue
    let contentAlignmentVerticalValue = parent?.contentAlignmentVertical?.resolveOptionalValue(context: context) ?? .noValue
    let filtersValue = parent?.filters?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let imageUrlValue = parent?.imageUrl?.resolveValue(context: context, transform: URL.init(string:)) ?? .noValue
    let preloadRequiredValue = parent?.preloadRequired?.resolveOptionalValue(context: context) ?? .noValue
    let scaleValue = parent?.scale?.resolveOptionalValue(context: context) ?? .noValue
    var errors = mergeErrors(
      alphaValue.errorsOrWarnings?.map { .nestedObjectError(field: "alpha", error: $0) },
      contentAlignmentHorizontalValue.errorsOrWarnings?.map { .nestedObjectError(field: "content_alignment_horizontal", error: $0) },
      contentAlignmentVerticalValue.errorsOrWarnings?.map { .nestedObjectError(field: "content_alignment_vertical", error: $0) },
      filtersValue.errorsOrWarnings?.map { .nestedObjectError(field: "filters", error: $0) },
      imageUrlValue.errorsOrWarnings?.map { .nestedObjectError(field: "image_url", error: $0) },
      preloadRequiredValue.errorsOrWarnings?.map { .nestedObjectError(field: "preload_required", error: $0) },
      scaleValue.errorsOrWarnings?.map { .nestedObjectError(field: "scale", error: $0) }
    )
    if case .noValue = imageUrlValue {
      errors.append(.requiredFieldIsMissing(field: "image_url"))
    }
    guard
      let imageUrlNonNil = imageUrlValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivImageBackground(
      alpha: alphaValue.value,
      contentAlignmentHorizontal: contentAlignmentHorizontalValue.value,
      contentAlignmentVertical: contentAlignmentVerticalValue.value,
      filters: filtersValue.value,
      imageUrl: imageUrlNonNil,
      preloadRequired: preloadRequiredValue.value,
      scale: scaleValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivImageBackgroundTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivImageBackground> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var alphaValue: DeserializationResult<Expression<Double>> = parent?.alpha?.value() ?? .noValue
    var contentAlignmentHorizontalValue: DeserializationResult<Expression<DivAlignmentHorizontal>> = parent?.contentAlignmentHorizontal?.value() ?? .noValue
    var contentAlignmentVerticalValue: DeserializationResult<Expression<DivAlignmentVertical>> = parent?.contentAlignmentVertical?.value() ?? .noValue
    var filtersValue: DeserializationResult<[DivFilter]> = .noValue
    var imageUrlValue: DeserializationResult<Expression<URL>> = parent?.imageUrl?.value() ?? .noValue
    var preloadRequiredValue: DeserializationResult<Expression<Bool>> = parent?.preloadRequired?.value() ?? .noValue
    var scaleValue: DeserializationResult<Expression<DivImageScale>> = parent?.scale?.value() ?? .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "alpha":
        alphaValue = deserialize(__dictValue, validator: ResolvedValue.alphaValidator).merged(with: alphaValue)
      case "content_alignment_horizontal":
        contentAlignmentHorizontalValue = deserialize(__dictValue).merged(with: contentAlignmentHorizontalValue)
      case "content_alignment_vertical":
        contentAlignmentVerticalValue = deserialize(__dictValue).merged(with: contentAlignmentVerticalValue)
      case "filters":
        filtersValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivFilterTemplate.self).merged(with: filtersValue)
      case "image_url":
        imageUrlValue = deserialize(__dictValue, transform: URL.init(string:)).merged(with: imageUrlValue)
      case "preload_required":
        preloadRequiredValue = deserialize(__dictValue).merged(with: preloadRequiredValue)
      case "scale":
        scaleValue = deserialize(__dictValue).merged(with: scaleValue)
      case parent?.alpha?.link:
        alphaValue = alphaValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.alphaValidator))
      case parent?.contentAlignmentHorizontal?.link:
        contentAlignmentHorizontalValue = contentAlignmentHorizontalValue.merged(with: deserialize(__dictValue))
      case parent?.contentAlignmentVertical?.link:
        contentAlignmentVerticalValue = contentAlignmentVerticalValue.merged(with: deserialize(__dictValue))
      case parent?.filters?.link:
        filtersValue = filtersValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivFilterTemplate.self))
      case parent?.imageUrl?.link:
        imageUrlValue = imageUrlValue.merged(with: deserialize(__dictValue, transform: URL.init(string:)))
      case parent?.preloadRequired?.link:
        preloadRequiredValue = preloadRequiredValue.merged(with: deserialize(__dictValue))
      case parent?.scale?.link:
        scaleValue = scaleValue.merged(with: deserialize(__dictValue))
      default: break
      }
    }
    if let parent = parent {
      filtersValue = filtersValue.merged(with: parent.filters?.resolveOptionalValue(context: context, useOnlyLinks: true))
    }
    var errors = mergeErrors(
      alphaValue.errorsOrWarnings?.map { .nestedObjectError(field: "alpha", error: $0) },
      contentAlignmentHorizontalValue.errorsOrWarnings?.map { .nestedObjectError(field: "content_alignment_horizontal", error: $0) },
      contentAlignmentVerticalValue.errorsOrWarnings?.map { .nestedObjectError(field: "content_alignment_vertical", error: $0) },
      filtersValue.errorsOrWarnings?.map { .nestedObjectError(field: "filters", error: $0) },
      imageUrlValue.errorsOrWarnings?.map { .nestedObjectError(field: "image_url", error: $0) },
      preloadRequiredValue.errorsOrWarnings?.map { .nestedObjectError(field: "preload_required", error: $0) },
      scaleValue.errorsOrWarnings?.map { .nestedObjectError(field: "scale", error: $0) }
    )
    if case .noValue = imageUrlValue {
      errors.append(.requiredFieldIsMissing(field: "image_url"))
    }
    guard
      let imageUrlNonNil = imageUrlValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivImageBackground(
      alpha: alphaValue.value,
      contentAlignmentHorizontal: contentAlignmentHorizontalValue.value,
      contentAlignmentVertical: contentAlignmentVerticalValue.value,
      filters: filtersValue.value,
      imageUrl: imageUrlNonNil,
      preloadRequired: preloadRequiredValue.value,
      scale: scaleValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivImageBackgroundTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? DivImageBackgroundTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return DivImageBackgroundTemplate(
      parent: nil,
      alpha: alpha ?? mergedParent.alpha,
      contentAlignmentHorizontal: contentAlignmentHorizontal ?? mergedParent.contentAlignmentHorizontal,
      contentAlignmentVertical: contentAlignmentVertical ?? mergedParent.contentAlignmentVertical,
      filters: filters ?? mergedParent.filters,
      imageUrl: imageUrl ?? mergedParent.imageUrl,
      preloadRequired: preloadRequired ?? mergedParent.preloadRequired,
      scale: scale ?? mergedParent.scale
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivImageBackgroundTemplate {
    let merged = try mergedWithParent(templates: templates)

    return DivImageBackgroundTemplate(
      parent: nil,
      alpha: merged.alpha,
      contentAlignmentHorizontal: merged.contentAlignmentHorizontal,
      contentAlignmentVertical: merged.contentAlignmentVertical,
      filters: merged.filters?.tryResolveParent(templates: templates),
      imageUrl: merged.imageUrl,
      preloadRequired: merged.preloadRequired,
      scale: merged.scale
    )
  }
}
