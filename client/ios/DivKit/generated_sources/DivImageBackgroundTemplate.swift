// Generated code. Do not modify.

import CommonCore
import Foundation
import Serialization
import TemplatesSupport

public final class DivImageBackgroundTemplate: TemplateValue, TemplateDeserializable {
  public static let type: String = "image"
  public let parent: String? // at least 1 char
  public let alpha: Field<Expression<Double>>? // constraint: number >= 0.0 && number <= 1.0; default value: 1.0
  public let contentAlignmentHorizontal: Field<Expression<DivAlignmentHorizontal>>? // default value: center
  public let contentAlignmentVertical: Field<Expression<DivAlignmentVertical>>? // default value: center
  public let imageUrl: Field<Expression<URL>>?
  public let preloadRequired: Field<Expression<Bool>>? // default value: false
  public let scale: Field<Expression<DivImageScale>>? // default value: fill

  static let parentValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  public convenience init(dictionary: [String: Any], templateToType: TemplateToType) throws {
    do {
      self.init(
        parent: try dictionary.getOptionalField("type", validator: Self.parentValidator),
        alpha: try dictionary.getOptionalField("alpha"),
        contentAlignmentHorizontal: try dictionary.getOptionalField("content_alignment_horizontal"),
        contentAlignmentVertical: try dictionary.getOptionalField("content_alignment_vertical"),
        imageUrl: try dictionary.getOptionalField("image_url", transform: URL.init(string:)),
        preloadRequired: try dictionary.getOptionalField("preload_required"),
        scale: try dictionary.getOptionalField("scale")
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
    imageUrl: Field<Expression<URL>>? = nil,
    preloadRequired: Field<Expression<Bool>>? = nil,
    scale: Field<Expression<DivImageScale>>? = nil
  ) {
    self.parent = parent
    self.alpha = alpha
    self.contentAlignmentHorizontal = contentAlignmentHorizontal
    self.contentAlignmentVertical = contentAlignmentVertical
    self.imageUrl = imageUrl
    self.preloadRequired = preloadRequired
    self.scale = scale
  }

  private static func resolveOnlyLinks(context: Context, parent: DivImageBackgroundTemplate?) -> DeserializationResult<DivImageBackground> {
    let alphaValue = parent?.alpha?.resolveOptionalValue(context: context, validator: ResolvedValue.alphaValidator) ?? .noValue
    let contentAlignmentHorizontalValue = parent?.contentAlignmentHorizontal?.resolveOptionalValue(context: context, validator: ResolvedValue.contentAlignmentHorizontalValidator) ?? .noValue
    let contentAlignmentVerticalValue = parent?.contentAlignmentVertical?.resolveOptionalValue(context: context, validator: ResolvedValue.contentAlignmentVerticalValidator) ?? .noValue
    let imageUrlValue = parent?.imageUrl?.resolveValue(context: context, transform: URL.init(string:)) ?? .noValue
    let preloadRequiredValue = parent?.preloadRequired?.resolveOptionalValue(context: context, validator: ResolvedValue.preloadRequiredValidator) ?? .noValue
    let scaleValue = parent?.scale?.resolveOptionalValue(context: context, validator: ResolvedValue.scaleValidator) ?? .noValue
    var errors = mergeErrors(
      alphaValue.errorsOrWarnings?.map { .right($0.asError(deserializing: "alpha", level: .warning)) },
      contentAlignmentHorizontalValue.errorsOrWarnings?.map { .right($0.asError(deserializing: "content_alignment_horizontal", level: .warning)) },
      contentAlignmentVerticalValue.errorsOrWarnings?.map { .right($0.asError(deserializing: "content_alignment_vertical", level: .warning)) },
      imageUrlValue.errorsOrWarnings?.map { .right($0.asError(deserializing: "image_url", level: .error)) },
      preloadRequiredValue.errorsOrWarnings?.map { .right($0.asError(deserializing: "preload_required", level: .warning)) },
      scaleValue.errorsOrWarnings?.map { .right($0.asError(deserializing: "scale", level: .warning)) }
    )
    if case .noValue = imageUrlValue {
      errors.append(.right(FieldError(fieldName: "image_url", level: .error, error: .requiredFieldIsMissing)))
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
      imageUrl: imageUrlNonNil,
      preloadRequired: preloadRequiredValue.value,
      scale: scaleValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: Context, parent: DivImageBackgroundTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivImageBackground> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var alphaValue: DeserializationResult<Expression<Double>> = parent?.alpha?.value() ?? .noValue
    var contentAlignmentHorizontalValue: DeserializationResult<Expression<DivAlignmentHorizontal>> = parent?.contentAlignmentHorizontal?.value() ?? .noValue
    var contentAlignmentVerticalValue: DeserializationResult<Expression<DivAlignmentVertical>> = parent?.contentAlignmentVertical?.value() ?? .noValue
    var imageUrlValue: DeserializationResult<Expression<URL>> = parent?.imageUrl?.value() ?? .noValue
    var preloadRequiredValue: DeserializationResult<Expression<Bool>> = parent?.preloadRequired?.value() ?? .noValue
    var scaleValue: DeserializationResult<Expression<DivImageScale>> = parent?.scale?.value() ?? .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "alpha":
        alphaValue = deserialize(__dictValue, validator: ResolvedValue.alphaValidator).merged(with: alphaValue)
      case "content_alignment_horizontal":
        contentAlignmentHorizontalValue = deserialize(__dictValue, validator: ResolvedValue.contentAlignmentHorizontalValidator).merged(with: contentAlignmentHorizontalValue)
      case "content_alignment_vertical":
        contentAlignmentVerticalValue = deserialize(__dictValue, validator: ResolvedValue.contentAlignmentVerticalValidator).merged(with: contentAlignmentVerticalValue)
      case "image_url":
        imageUrlValue = deserialize(__dictValue, transform: URL.init(string:)).merged(with: imageUrlValue)
      case "preload_required":
        preloadRequiredValue = deserialize(__dictValue, validator: ResolvedValue.preloadRequiredValidator).merged(with: preloadRequiredValue)
      case "scale":
        scaleValue = deserialize(__dictValue, validator: ResolvedValue.scaleValidator).merged(with: scaleValue)
      case parent?.alpha?.link:
        alphaValue = alphaValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.alphaValidator))
      case parent?.contentAlignmentHorizontal?.link:
        contentAlignmentHorizontalValue = contentAlignmentHorizontalValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.contentAlignmentHorizontalValidator))
      case parent?.contentAlignmentVertical?.link:
        contentAlignmentVerticalValue = contentAlignmentVerticalValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.contentAlignmentVerticalValidator))
      case parent?.imageUrl?.link:
        imageUrlValue = imageUrlValue.merged(with: deserialize(__dictValue, transform: URL.init(string:)))
      case parent?.preloadRequired?.link:
        preloadRequiredValue = preloadRequiredValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.preloadRequiredValidator))
      case parent?.scale?.link:
        scaleValue = scaleValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.scaleValidator))
      default: break
      }
    }
    var errors = mergeErrors(
      alphaValue.errorsOrWarnings?.map { Either.right($0.asError(deserializing: "alpha", level: .warning)) },
      contentAlignmentHorizontalValue.errorsOrWarnings?.map { Either.right($0.asError(deserializing: "content_alignment_horizontal", level: .warning)) },
      contentAlignmentVerticalValue.errorsOrWarnings?.map { Either.right($0.asError(deserializing: "content_alignment_vertical", level: .warning)) },
      imageUrlValue.errorsOrWarnings?.map { Either.right($0.asError(deserializing: "image_url", level: .error)) },
      preloadRequiredValue.errorsOrWarnings?.map { Either.right($0.asError(deserializing: "preload_required", level: .warning)) },
      scaleValue.errorsOrWarnings?.map { Either.right($0.asError(deserializing: "scale", level: .warning)) }
    )
    if case .noValue = imageUrlValue {
      errors.append(.right(FieldError(fieldName: "image_url", level: .error, error: .requiredFieldIsMissing)))
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
      imageUrl: imageUrlNonNil,
      preloadRequired: preloadRequiredValue.value,
      scale: scaleValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: Templates) throws -> DivImageBackgroundTemplate {
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
      imageUrl: imageUrl ?? mergedParent.imageUrl,
      preloadRequired: preloadRequired ?? mergedParent.preloadRequired,
      scale: scale ?? mergedParent.scale
    )
  }

  public func resolveParent(templates: Templates) throws -> DivImageBackgroundTemplate {
    return try mergedWithParent(templates: templates)
  }
}
