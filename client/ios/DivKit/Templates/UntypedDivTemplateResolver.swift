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

    return resolveLinks(in: merged, linkSource: dictionary)
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
    linkSource: [String: Any]
  ) -> [String: Any] {
    var dict = dictionary
    let linkKeys = dict.keys.filter { $0.hasPrefix("$") }
    for linkKey in linkKeys {
      let key = String(linkKey.dropFirst())
      guard dict[key] == nil else { continue }
      guard let linkName = dict[linkKey] as? String else { continue }
      guard let value = linkSource[linkName] else { continue }
      dict[key] = value
    }

    var result: [String: Any] = [:]
    for (key, value) in dict {
      guard !key.hasPrefix("$") else { continue }
      result[key] = resolveLinksInValue(value, linkSource: linkSource)
    }
    return result
  }

  private func resolveLinksInValue(
    _ value: Any,
    linkSource: [String: Any]
  ) -> Any {
    if let dict = value as? [String: Any] {
      if let type = dict["type"] as? String,
         let resolvedTemplate = resolveTemplate(named: type).value {
        let parameterNames = collectParameterNames(from: resolvedTemplate)
        return resolveInstanceLinks(
          in: dict,
          linkSource: linkSource,
          parameterNames: parameterNames
        )
      }
      return resolveLinks(in: dict, linkSource: linkSource)
    }
    if let array = value as? [Any] {
      return array.map { resolveLinksInValue($0, linkSource: linkSource) }
    }
    return value
  }

  private func resolveInstanceLinks(
    in dictionary: [String: Any],
    linkSource: [String: Any],
    parameterNames: Set<String>
  ) -> [String: Any] {
    var dict = dictionary
    let linkKeys = dict.keys.filter { $0.hasPrefix("$") }
    for linkKey in linkKeys {
      let key = String(linkKey.dropFirst())
      guard dict[key] == nil else { continue }
      guard !parameterNames.contains(key) else { continue }
      guard let linkName = dict[linkKey] as? String else { continue }
      guard let value = linkSource[linkName] else { continue }
      dict[key] = value
    }

    var result: [String: Any] = [:]
    for (key, value) in dict {
      guard !key.hasPrefix("$") else { continue }
      result[key] = value
    }

    for paramName in parameterNames {
      if result[paramName] == nil, let value = linkSource[paramName] {
        result[paramName] = value
      }
    }

    return result
  }

  private func collectParameterNames(from dict: [String: Any]) -> Set<String> {
    var names = Set<String>()
    for (key, value) in dict {
      if key.hasPrefix("$"), let name = value as? String {
        names.insert(name)
      }
      if let nestedDict = value as? [String: Any] {
        names.formUnion(collectParameterNames(from: nestedDict))
      }
      if let array = value as? [Any] {
        for element in array {
          if let elementDict = element as? [String: Any] {
            names.formUnion(collectParameterNames(from: elementDict))
          }
        }
      }
    }
    return names
  }
}

private func normalizedErrors(
  from errors: NonEmptyArray<DeserializationError>?
) -> NonEmptyArray<DeserializationError> {
  errors ?? NonEmptyArray(.generic)
}
