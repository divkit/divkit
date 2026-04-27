import Foundation
import Serialization
import VGSL

final class UntypedDivTemplateResolver {
  private let templates: [TemplateName: Any]
  private let templateToType: [TemplateName: String]
  private var resolvedTemplateCache: [TemplateName: [String: Any]] = [:]
  private var currentlyResolvingTemplates = Set<TemplateName>()

  init(templates: [String: Any]?) {
    let templates = templates ?? [:]
    self.templates = templates
    templateToType = calculateTemplateToType(in: templates)
  }

  func resolveFlat(_ dictionary: [String: Any]) -> [String: Any] {
    guard let templateName = dictionary["type"] as? String,
          templates[templateName] != nil else {
      return dictionary
    }

    let resolvedTemplate = resolveTemplate(named: templateName)
    guard let templateValue = resolvedTemplate.value else {
      return dictionary
    }

    var merged = templateValue
    for (key, value) in dictionary {
      merged[key] = value
    }
    merged["type"] = templateToType[templateName] ?? templateName

    return resolveLinks(
      in: merged,
      linkSource: dictionary,
      cascadeAllowed: true,
      instanceProvidedKeys: Set(dictionary.keys),
      expanding: [templateName]
    )
  }

  private func resolveTemplate(named templateName: TemplateName)
    -> DeserializationResult<[String: Any]> {
    if let cached = resolvedTemplateCache[templateName] {
      return .success(cached)
    }

    guard !currentlyResolvingTemplates.contains(templateName) else {
      return .failure(NonEmptyArray(.unknownType(type: templateName)))
    }

    guard let templateDict = templates[templateName] as? [String: Any] else {
      return .failure(NonEmptyArray(.unknownType(type: templateName)))
    }

    currentlyResolvingTemplates.insert(templateName)
    defer { currentlyResolvingTemplates.remove(templateName) }

    var result: [String: Any] = [:]

    if let parentName = templateDict["type"] as? String, templates[parentName] != nil {
      let parentResult = resolveTemplate(named: parentName)
      guard let parentTemplate = parentResult.value else {
        return .failure(normalizedErrors(from: parentResult.errorsOrWarnings))
      }
      result = parentTemplate
    }

    for (key, value) in templateDict {
      result[key] = value
    }
    result["type"] = templateToType[templateName] ??
      (templateDict["type"] as? String ?? templateName)

    resolvedTemplateCache[templateName] = result
    return .success(result)
  }

  private func resolveLinks(
    in dictionary: [String: Any],
    linkSource: [String: Any]?,
    cascadeAllowed: Bool,
    instanceProvidedKeys: Set<String> = [],
    expanding: Set<String> = []
  ) -> [String: Any] {
    var dict = dictionary
    var linkFieldNames = Set<String>()
    let linkKeys = dict.keys.filter { $0.hasPrefix("$") }
    for linkKey in linkKeys {
      let key = String(linkKey.dropFirst())
      linkFieldNames.insert(key)
      guard let linkName = dict[linkKey] as? String else { continue }
      guard dict[key] == nil else { continue }
      guard let value = linkSource?[linkName] else { continue }
      dict[key] = value
    }

    var result: [String: Any] = [:]
    for (key, value) in dict {
      guard !key.hasPrefix("$") else { continue }
      let isLinked = linkFieldNames.contains(key)
      let isInstanceProvided = instanceProvidedKeys.contains(key)
      let childLinkSource: [String: Any]? = isLinked ? nil : linkSource
      let childCascadeAllowed = cascadeAllowed && !isLinked && !isInstanceProvided
      result[key] = resolveLinksInValue(
        value,
        linkSource: childLinkSource,
        cascadeAllowed: childCascadeAllowed,
        expanding: expanding
      )
    }
    return result
  }

  private func resolveLinksInValue(
    _ value: Any,
    linkSource: [String: Any]?,
    cascadeAllowed: Bool,
    expanding: Set<String>
  ) -> Any {
    if let dict = value as? [String: Any] {
      if cascadeAllowed,
         let linkSource,
         let type = dict["type"] as? String,
         !expanding.contains(type),
         let resolvedTemplate = resolveTemplate(named: type).value {
        var merged = resolvedTemplate
        for (key, value) in dict {
          merged[key] = value
        }
        merged["type"] = templateToType[type] ?? type
        return resolveLinks(
          in: merged,
          linkSource: linkSource,
          cascadeAllowed: true,
          instanceProvidedKeys: Set(dict.keys),
          expanding: expanding.union([type])
        )
      }
      return resolveLinks(
        in: dict,
        linkSource: linkSource,
        cascadeAllowed: cascadeAllowed,
        expanding: expanding
      )
    }
    if let array = value as? [Any] {
      return array.map {
        resolveLinksInValue(
          $0,
          linkSource: linkSource,
          cascadeAllowed: cascadeAllowed,
          expanding: expanding
        )
      }
    }
    return value
  }
}

private func normalizedErrors(
  from errors: NonEmptyArray<DeserializationError>?
) -> NonEmptyArray<DeserializationError> {
  errors ?? NonEmptyArray(.generic)
}
