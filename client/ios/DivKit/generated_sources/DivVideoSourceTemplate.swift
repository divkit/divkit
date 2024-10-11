// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivVideoSourceTemplate: TemplateValue {
  public final class ResolutionTemplate: TemplateValue {
    public static let type: String = "resolution"
    public let parent: String?
    public let height: Field<Expression<Int>>? // constraint: number > 0
    public let width: Field<Expression<Int>>? // constraint: number > 0

    public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
      self.init(
        parent: dictionary["type"] as? String,
        height: dictionary.getOptionalExpressionField("height"),
        width: dictionary.getOptionalExpressionField("width")
      )
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
      let heightValue = { parent?.height?.resolveValue(context: context, validator: ResolvedValue.heightValidator) ?? .noValue }()
      let widthValue = { parent?.width?.resolveValue(context: context, validator: ResolvedValue.widthValidator) ?? .noValue }()
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
        height: { heightNonNil }(),
        width: { widthNonNil }()
      )
      return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
    }

    public static func resolveValue(context: TemplatesContext, parent: ResolutionTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivVideoSource.Resolution> {
      if useOnlyLinks {
        return resolveOnlyLinks(context: context, parent: parent)
      }
      var heightValue: DeserializationResult<Expression<Int>> = { parent?.height?.value() ?? .noValue }()
      var widthValue: DeserializationResult<Expression<Int>> = { parent?.width?.value() ?? .noValue }()
      _ = {
        // Each field is parsed in its own lambda to keep the stack size managable
        // Otherwise the compiler will allocate stack for each intermediate variable
        // upfront even when we don't actually visit a relevant branch
        for (key, __dictValue) in context.templateData {
          _ = {
            if key == "height" {
             heightValue = deserialize(__dictValue, validator: ResolvedValue.heightValidator).merged(with: heightValue)
            }
          }()
          _ = {
            if key == "width" {
             widthValue = deserialize(__dictValue, validator: ResolvedValue.widthValidator).merged(with: widthValue)
            }
          }()
          _ = {
           if key == parent?.height?.link {
             heightValue = heightValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.heightValidator) })
            }
          }()
          _ = {
           if key == parent?.width?.link {
             widthValue = widthValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.widthValidator) })
            }
          }()
        }
      }()
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
        height: { heightNonNil }(),
        width: { widthNonNil }()
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
  public let parent: String?
  public let bitrate: Field<Expression<Int>>?
  public let mimeType: Field<Expression<String>>?
  public let resolution: Field<ResolutionTemplate>?
  public let url: Field<Expression<URL>>?

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      parent: dictionary["type"] as? String,
      bitrate: dictionary.getOptionalExpressionField("bitrate"),
      mimeType: dictionary.getOptionalExpressionField("mime_type"),
      resolution: dictionary.getOptionalField("resolution", templateToType: templateToType),
      url: dictionary.getOptionalExpressionField("url", transform: URL.init(string:))
    )
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
    let bitrateValue = { parent?.bitrate?.resolveOptionalValue(context: context) ?? .noValue }()
    let mimeTypeValue = { parent?.mimeType?.resolveValue(context: context) ?? .noValue }()
    let resolutionValue = { parent?.resolution?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let urlValue = { parent?.url?.resolveValue(context: context, transform: URL.init(string:)) ?? .noValue }()
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
      bitrate: { bitrateValue.value }(),
      mimeType: { mimeTypeNonNil }(),
      resolution: { resolutionValue.value }(),
      url: { urlNonNil }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivVideoSourceTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivVideoSource> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var bitrateValue: DeserializationResult<Expression<Int>> = { parent?.bitrate?.value() ?? .noValue }()
    var mimeTypeValue: DeserializationResult<Expression<String>> = { parent?.mimeType?.value() ?? .noValue }()
    var resolutionValue: DeserializationResult<DivVideoSource.Resolution> = .noValue
    var urlValue: DeserializationResult<Expression<URL>> = { parent?.url?.value() ?? .noValue }()
    _ = {
      // Each field is parsed in its own lambda to keep the stack size managable
      // Otherwise the compiler will allocate stack for each intermediate variable
      // upfront even when we don't actually visit a relevant branch
      for (key, __dictValue) in context.templateData {
        _ = {
          if key == "bitrate" {
           bitrateValue = deserialize(__dictValue).merged(with: bitrateValue)
          }
        }()
        _ = {
          if key == "mime_type" {
           mimeTypeValue = deserialize(__dictValue).merged(with: mimeTypeValue)
          }
        }()
        _ = {
          if key == "resolution" {
           resolutionValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivVideoSourceTemplate.ResolutionTemplate.self).merged(with: resolutionValue)
          }
        }()
        _ = {
          if key == "url" {
           urlValue = deserialize(__dictValue, transform: URL.init(string:)).merged(with: urlValue)
          }
        }()
        _ = {
         if key == parent?.bitrate?.link {
           bitrateValue = bitrateValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.mimeType?.link {
           mimeTypeValue = mimeTypeValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.resolution?.link {
           resolutionValue = resolutionValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivVideoSourceTemplate.ResolutionTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.url?.link {
           urlValue = urlValue.merged(with: { deserialize(__dictValue, transform: URL.init(string:)) })
          }
        }()
      }
    }()
    if let parent = parent {
      _ = { resolutionValue = resolutionValue.merged(with: { parent.resolution?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
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
      bitrate: { bitrateValue.value }(),
      mimeType: { mimeTypeNonNil }(),
      resolution: { resolutionValue.value }(),
      url: { urlNonNil }()
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
