// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivVideoSourceTemplate: TemplateValue {
  public final class ResolutionTemplate: TemplateValue {
    public static let type: String = "resolution"
    public let parent: String? // at least 1 char
    public let height: Field<Expression<Int>>? // constraint: number > 0
    public let width: Field<Expression<Int>>? // constraint: number > 0

    static let parentValidator: AnyValueValidator<String> =
      makeStringValidator(minLength: 1)

    public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
      do {
        self.init(
          parent: try dictionary.getOptionalField("type", validator: Self.parentValidator),
          height: try dictionary.getOptionalExpressionField("height"),
          width: try dictionary.getOptionalExpressionField("width")
        )
      } catch let DeserializationError.invalidFieldRepresentation(field: field, representation: representation) {
        throw DeserializationError.invalidFieldRepresentation(field: "resolution_template." + field, representation: representation)
      }
    }

    init(
      parent: String?,
      height: Field<Expression<Int>>? = nil,
      width: Field<Expression<Int>>? = nil
    ) {
      self.parent = parent
      self.height = height
      self.width = width
    }

    private static func resolveOnlyLinks(context: TemplatesContext, parent: ResolutionTemplate?) -> DeserializationResult<DivVideoSource.Resolution> {
      let heightValue = parent?.height?.resolveValue(context: context, validator: ResolvedValue.heightValidator) ?? .noValue
      let widthValue = parent?.width?.resolveValue(context: context, validator: ResolvedValue.widthValidator) ?? .noValue
      var errors = mergeErrors(
        heightValue.errorsOrWarnings?.map { .nestedObjectError(field: "height", error: $0) },
        widthValue.errorsOrWarnings?.map { .nestedObjectError(field: "width", error: $0) }
      )
      if case .noValue = heightValue {
        errors.append(.requiredFieldIsMissing(field: "height"))
      }
      if case .noValue = widthValue {
        errors.append(.requiredFieldIsMissing(field: "width"))
      }
      guard
        let heightNonNil = heightValue.value,
        let widthNonNil = widthValue.value
      else {
        return .failure(NonEmptyArray(errors)!)
      }
      let result = DivVideoSource.Resolution(
        height: heightNonNil,
        width: widthNonNil
      )
      return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
    }

    public static func resolveValue(context: TemplatesContext, parent: ResolutionTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivVideoSource.Resolution> {
      if useOnlyLinks {
        return resolveOnlyLinks(context: context, parent: parent)
      }
      var heightValue: DeserializationResult<Expression<Int>> = parent?.height?.value() ?? .noValue
      var widthValue: DeserializationResult<Expression<Int>> = parent?.width?.value() ?? .noValue
      context.templateData.forEach { key, __dictValue in
        switch key {
        case "height":
          heightValue = deserialize(__dictValue, validator: ResolvedValue.heightValidator).merged(with: heightValue)
        case "width":
          widthValue = deserialize(__dictValue, validator: ResolvedValue.widthValidator).merged(with: widthValue)
        case parent?.height?.link:
          heightValue = heightValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.heightValidator))
        case parent?.width?.link:
          widthValue = widthValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.widthValidator))
        default: break
        }
      }
      var errors = mergeErrors(
        heightValue.errorsOrWarnings?.map { .nestedObjectError(field: "height", error: $0) },
        widthValue.errorsOrWarnings?.map { .nestedObjectError(field: "width", error: $0) }
      )
      if case .noValue = heightValue {
        errors.append(.requiredFieldIsMissing(field: "height"))
      }
      if case .noValue = widthValue {
        errors.append(.requiredFieldIsMissing(field: "width"))
      }
      guard
        let heightNonNil = heightValue.value,
        let widthNonNil = widthValue.value
      else {
        return .failure(NonEmptyArray(errors)!)
      }
      let result = DivVideoSource.Resolution(
        height: heightNonNil,
        width: widthNonNil
      )
      return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
    }

    private func mergedWithParent(templates: [TemplateName: Any]) throws -> ResolutionTemplate {
      guard let parent = parent, parent != Self.type else { return self }
      guard let parentTemplate = templates[parent] as? ResolutionTemplate else {
        throw DeserializationError.unknownType(type: parent)
      }
      let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

      return ResolutionTemplate(
        parent: nil,
        height: height ?? mergedParent.height,
        width: width ?? mergedParent.width
      )
    }

    public func resolveParent(templates: [TemplateName: Any]) throws -> ResolutionTemplate {
      return try mergedWithParent(templates: templates)
    }
  }

  public static let type: String = "video_source"
  public let parent: String? // at least 1 char
  public let bitrate: Field<Expression<Int>>?
  public let mimeType: Field<Expression<String>>?
  public let resolution: Field<ResolutionTemplate>?
  public let url: Field<Expression<URL>>?

  static let parentValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    do {
      self.init(
        parent: try dictionary.getOptionalField("type", validator: Self.parentValidator),
        bitrate: try dictionary.getOptionalExpressionField("bitrate"),
        mimeType: try dictionary.getOptionalExpressionField("mime_type"),
        resolution: try dictionary.getOptionalField("resolution", templateToType: templateToType),
        url: try dictionary.getOptionalExpressionField("url", transform: URL.init(string:))
      )
    } catch let DeserializationError.invalidFieldRepresentation(field: field, representation: representation) {
      throw DeserializationError.invalidFieldRepresentation(field: "div-video-source_template." + field, representation: representation)
    }
  }

  init(
    parent: String?,
    bitrate: Field<Expression<Int>>? = nil,
    mimeType: Field<Expression<String>>? = nil,
    resolution: Field<ResolutionTemplate>? = nil,
    url: Field<Expression<URL>>? = nil
  ) {
    self.parent = parent
    self.bitrate = bitrate
    self.mimeType = mimeType
    self.resolution = resolution
    self.url = url
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivVideoSourceTemplate?) -> DeserializationResult<DivVideoSource> {
    let bitrateValue = parent?.bitrate?.resolveOptionalValue(context: context) ?? .noValue
    let mimeTypeValue = parent?.mimeType?.resolveValue(context: context) ?? .noValue
    let resolutionValue = parent?.resolution?.resolveOptionalValue(context: context, validator: ResolvedValue.resolutionValidator, useOnlyLinks: true) ?? .noValue
    let urlValue = parent?.url?.resolveValue(context: context, transform: URL.init(string:)) ?? .noValue
    var errors = mergeErrors(
      bitrateValue.errorsOrWarnings?.map { .nestedObjectError(field: "bitrate", error: $0) },
      mimeTypeValue.errorsOrWarnings?.map { .nestedObjectError(field: "mime_type", error: $0) },
      resolutionValue.errorsOrWarnings?.map { .nestedObjectError(field: "resolution", error: $0) },
      urlValue.errorsOrWarnings?.map { .nestedObjectError(field: "url", error: $0) }
    )
    if case .noValue = mimeTypeValue {
      errors.append(.requiredFieldIsMissing(field: "mime_type"))
    }
    if case .noValue = urlValue {
      errors.append(.requiredFieldIsMissing(field: "url"))
    }
    guard
      let mimeTypeNonNil = mimeTypeValue.value,
      let urlNonNil = urlValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivVideoSource(
      bitrate: bitrateValue.value,
      mimeType: mimeTypeNonNil,
      resolution: resolutionValue.value,
      url: urlNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivVideoSourceTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivVideoSource> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var bitrateValue: DeserializationResult<Expression<Int>> = parent?.bitrate?.value() ?? .noValue
    var mimeTypeValue: DeserializationResult<Expression<String>> = parent?.mimeType?.value() ?? .noValue
    var resolutionValue: DeserializationResult<DivVideoSource.Resolution> = .noValue
    var urlValue: DeserializationResult<Expression<URL>> = parent?.url?.value() ?? .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "bitrate":
        bitrateValue = deserialize(__dictValue).merged(with: bitrateValue)
      case "mime_type":
        mimeTypeValue = deserialize(__dictValue).merged(with: mimeTypeValue)
      case "resolution":
        resolutionValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.resolutionValidator, type: DivVideoSourceTemplate.ResolutionTemplate.self).merged(with: resolutionValue)
      case "url":
        urlValue = deserialize(__dictValue, transform: URL.init(string:)).merged(with: urlValue)
      case parent?.bitrate?.link:
        bitrateValue = bitrateValue.merged(with: deserialize(__dictValue))
      case parent?.mimeType?.link:
        mimeTypeValue = mimeTypeValue.merged(with: deserialize(__dictValue))
      case parent?.resolution?.link:
        resolutionValue = resolutionValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.resolutionValidator, type: DivVideoSourceTemplate.ResolutionTemplate.self))
      case parent?.url?.link:
        urlValue = urlValue.merged(with: deserialize(__dictValue, transform: URL.init(string:)))
      default: break
      }
    }
    if let parent = parent {
      resolutionValue = resolutionValue.merged(with: parent.resolution?.resolveOptionalValue(context: context, validator: ResolvedValue.resolutionValidator, useOnlyLinks: true))
    }
    var errors = mergeErrors(
      bitrateValue.errorsOrWarnings?.map { .nestedObjectError(field: "bitrate", error: $0) },
      mimeTypeValue.errorsOrWarnings?.map { .nestedObjectError(field: "mime_type", error: $0) },
      resolutionValue.errorsOrWarnings?.map { .nestedObjectError(field: "resolution", error: $0) },
      urlValue.errorsOrWarnings?.map { .nestedObjectError(field: "url", error: $0) }
    )
    if case .noValue = mimeTypeValue {
      errors.append(.requiredFieldIsMissing(field: "mime_type"))
    }
    if case .noValue = urlValue {
      errors.append(.requiredFieldIsMissing(field: "url"))
    }
    guard
      let mimeTypeNonNil = mimeTypeValue.value,
      let urlNonNil = urlValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivVideoSource(
      bitrate: bitrateValue.value,
      mimeType: mimeTypeNonNil,
      resolution: resolutionValue.value,
      url: urlNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivVideoSourceTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? DivVideoSourceTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return DivVideoSourceTemplate(
      parent: nil,
      bitrate: bitrate ?? mergedParent.bitrate,
      mimeType: mimeType ?? mergedParent.mimeType,
      resolution: resolution ?? mergedParent.resolution,
      url: url ?? mergedParent.url
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivVideoSourceTemplate {
    let merged = try mergedWithParent(templates: templates)

    return DivVideoSourceTemplate(
      parent: nil,
      bitrate: merged.bitrate,
      mimeType: merged.mimeType,
      resolution: merged.resolution?.tryResolveParent(templates: templates),
      url: merged.url
    )
  }
}
