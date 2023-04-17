// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivVideoDataVideoTemplate: TemplateValue {
  public static let type: String = "video"
  public let parent: String? // at least 1 char
  public let videoSources: Field<[DivVideoDataVideoSourceTemplate]>? // at least 1 elements

  static let parentValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    do {
      self.init(
        parent: try dictionary.getOptionalField("type", validator: Self.parentValidator),
        videoSources: try dictionary.getOptionalArray("video_sources", templateToType: templateToType)
      )
    } catch let DeserializationError.invalidFieldRepresentation(field: field, representation: representation) {
      throw DeserializationError.invalidFieldRepresentation(field: "div-video-data-video_template." + field, representation: representation)
    }
  }

  init(
    parent: String?,
    videoSources: Field<[DivVideoDataVideoSourceTemplate]>? = nil
  ) {
    self.parent = parent
    self.videoSources = videoSources
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivVideoDataVideoTemplate?) -> DeserializationResult<DivVideoDataVideo> {
    let videoSourcesValue = parent?.videoSources?.resolveValue(context: context, validator: ResolvedValue.videoSourcesValidator, useOnlyLinks: true) ?? .noValue
    var errors = mergeErrors(
      videoSourcesValue.errorsOrWarnings?.map { .nestedObjectError(field: "video_sources", error: $0) }
    )
    if case .noValue = videoSourcesValue {
      errors.append(.requiredFieldIsMissing(field: "video_sources"))
    }
    guard
      let videoSourcesNonNil = videoSourcesValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivVideoDataVideo(
      videoSources: videoSourcesNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivVideoDataVideoTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivVideoDataVideo> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var videoSourcesValue: DeserializationResult<[DivVideoDataVideoSource]> = .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "video_sources":
        videoSourcesValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.videoSourcesValidator, type: DivVideoDataVideoSourceTemplate.self).merged(with: videoSourcesValue)
      case parent?.videoSources?.link:
        videoSourcesValue = videoSourcesValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.videoSourcesValidator, type: DivVideoDataVideoSourceTemplate.self))
      default: break
      }
    }
    if let parent = parent {
      videoSourcesValue = videoSourcesValue.merged(with: parent.videoSources?.resolveValue(context: context, validator: ResolvedValue.videoSourcesValidator, useOnlyLinks: true))
    }
    var errors = mergeErrors(
      videoSourcesValue.errorsOrWarnings?.map { .nestedObjectError(field: "video_sources", error: $0) }
    )
    if case .noValue = videoSourcesValue {
      errors.append(.requiredFieldIsMissing(field: "video_sources"))
    }
    guard
      let videoSourcesNonNil = videoSourcesValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivVideoDataVideo(
      videoSources: videoSourcesNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivVideoDataVideoTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? DivVideoDataVideoTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return DivVideoDataVideoTemplate(
      parent: nil,
      videoSources: videoSources ?? mergedParent.videoSources
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivVideoDataVideoTemplate {
    let merged = try mergedWithParent(templates: templates)

    return DivVideoDataVideoTemplate(
      parent: nil,
      videoSources: try merged.videoSources?.resolveParent(templates: templates)
    )
  }
}
