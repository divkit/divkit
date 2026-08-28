import Foundation
import Serialization
import VGSL

final class UntypedDivTemplateResolver {
  private struct SubtreeInfo {
    static let empty = SubtreeInfo(hasLinkKeys: false, hasTemplateTypes: false)

    let hasLinkKeys: Bool
    let hasTemplateTypes: Bool
  }

  private let templates: [TemplateName: Any]
  private let templateToType: [TemplateName: String]
  private var resolvedTemplateCache: [TemplateName: [String: Any]] = [:]
  private var templateParameterNamesCache: [TemplateName: Set<String>] = [:]
  private var subtreeInfoCache: [ObjectIdentifier: (container: AnyObject, info: SubtreeInfo)] = [:]
  private var currentlyResolvingTemplates = Set<TemplateName>()

  init(templates: [String: Any]?) {
    let templates = templates ?? [:]
    self.templates = templates
    templateToType = calculateTemplateToType(in: templates)
  }

  func resolveFlat(_ dictionary: [String: Any]) -> [String: Any] {
    guard !templates.isEmpty,
          let templateName = dictionary["type"] as? String,
          templates[templateName] != nil else {
      return dictionary
    }

    let resolvedTemplate = resolveTemplate(named: templateName)
    guard let templateValue = resolvedTemplate.value else {
      return dictionary
    }

    var merged = templateValue
    merged.reserveCapacity(templateValue.count + dictionary.count)
    for (key, value) in dictionary {
      merged[key] = value
    }
    merged["type"] = templateToType[templateName] ?? templateName

    return resolveLinks(
      in: merged,
      linkSource: dictionary,
      cascadeAllowed: true,
      instanceProvidedKeys: Set(dictionary.keys)
    ) ?? merged
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
      if !key.hasPrefix("$"), templateDict["$" + key] == nil {
        result["$" + key] = nil
      }
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
    instanceProvidedKeys: Set<String> = []
  ) -> [String: Any]? {
    var hasLinkKeys = false
    var linkFieldNames = Set<String>()
    var resolvedLinkValues: [(key: String, value: Any)] = []
    for (linkKey, linkValue) in dictionary {
      guard linkKey.hasPrefix("$") else { continue }
      hasLinkKeys = true
      let key = String(linkKey.dropFirst())
      linkFieldNames.insert(key)
      guard let linkName = linkValue as? String else { continue }
      guard !instanceProvidedKeys.contains(key) else { continue }
      guard let value = linkSource?[linkName] else { continue }
      resolvedLinkValues.append((key, value))
    }

    var changedValues: [(key: String, value: Any)] = []
    for (key, value) in dictionary {
      guard !key.hasPrefix("$") else { continue }
      let isLinked = linkFieldNames.contains(key)
      let isInstanceProvided = instanceProvidedKeys.contains(key)
      let childLinkSource: [String: Any]? = isLinked ? nil : linkSource
      let childCascadeAllowed = cascadeAllowed && !isLinked && !isInstanceProvided
      if let resolvedValue = resolveLinksInValue(
        value,
        linkSource: childLinkSource,
        cascadeAllowed: childCascadeAllowed
      ) {
        changedValues.append((key, resolvedValue))
      }
    }

    guard hasLinkKeys || !changedValues.isEmpty else {
      return nil
    }

    var result = dictionary
    if hasLinkKeys {
      for key in dictionary.keys where key.hasPrefix("$") {
        result.removeValue(forKey: key)
      }
    }
    for (key, value) in changedValues {
      result[key] = value
    }
    for (key, value) in resolvedLinkValues {
      result[key] = resolveLinksInValue(value, linkSource: nil, cascadeAllowed: false) ?? value
    }
    return result
  }

  private func resolveLinksInValue(
    _ value: Any,
    linkSource: [String: Any]?,
    cascadeAllowed: Bool
  ) -> Any? {
    if let dict = value as? [String: Any] {
      guard mayNeedLinkResolution(
        value, dict: dict,
        linkSource: linkSource,
        cascadeAllowed: cascadeAllowed
      ) else {
        return nil
      }
      if cascadeAllowed,
         let linkSource,
         let type = dict["type"] as? String,
         templates[type] != nil,
         let resolvedTemplate = resolveTemplate(named: type).value {
        var parameterNames = parameterNames(
          ofTemplateNamed: type,
          resolvedTemplate: resolvedTemplate
        )
        parameterNames.formUnion(
          collectParameterNames(from: dict, excludingKeys: parameterNames)
        )
        return resolveInstanceLinks(
          in: dict,
          linkSource: linkSource,
          parameterNames: parameterNames
        )
      }
      return resolveLinks(in: dict, linkSource: linkSource, cascadeAllowed: cascadeAllowed)
    }
    if let array = value as? [Any] {
      guard mayNeedLinkResolution(
        value, array: array,
        linkSource: linkSource,
        cascadeAllowed: cascadeAllowed
      ) else {
        return nil
      }
      var changedElements: [(index: Int, element: Any)] = []
      for (index, element) in array.enumerated() {
        if let resolvedElement = resolveLinksInValue(
          element,
          linkSource: linkSource,
          cascadeAllowed: cascadeAllowed
        ) {
          changedElements.append((index, resolvedElement))
        }
      }
      guard !changedElements.isEmpty else {
        return nil
      }
      var result = array
      for (index, element) in changedElements {
        result[index] = element
      }
      return result
    }
    return nil
  }

  private func mayNeedLinkResolution(
    _ value: Any,
    dict: [String: Any],
    linkSource: [String: Any]?,
    cascadeAllowed: Bool
  ) -> Bool {
    let info = subtreeInfo(of: value, dict: dict)
    return info.hasLinkKeys ||
      (cascadeAllowed && linkSource != nil && info.hasTemplateTypes)
  }

  private func mayNeedLinkResolution(
    _ value: Any,
    array: [Any],
    linkSource: [String: Any]?,
    cascadeAllowed: Bool
  ) -> Bool {
    let info = subtreeInfo(of: value, array: array)
    return info.hasLinkKeys ||
      (cascadeAllowed && linkSource != nil && info.hasTemplateTypes)
  }

  private func subtreeInfo(of value: Any) -> SubtreeInfo {
    if let dict = value as? [String: Any] {
      return subtreeInfo(of: value, dict: dict)
    }
    if let array = value as? [Any] {
      return subtreeInfo(of: value, array: array)
    }
    return .empty
  }

  private func subtreeInfo(of value: Any, dict: [String: Any]) -> SubtreeInfo {
    let objectID = referenceID(of: value)
    if let objectID, let cached = subtreeInfoCache[objectID] {
      return cached.info
    }

    var hasLinkKeys = false
    var hasTemplateTypes = false
    for (key, nestedValue) in dict {
      if !hasLinkKeys, key.hasPrefix("$") {
        hasLinkKeys = true
      }
      if !hasTemplateTypes, key == "type",
         let type = nestedValue as? String, templates[type] != nil {
        hasTemplateTypes = true
      }
      if hasLinkKeys, hasTemplateTypes {
        break
      }
      let nestedInfo = subtreeInfo(of: nestedValue)
      hasLinkKeys = hasLinkKeys || nestedInfo.hasLinkKeys
      hasTemplateTypes = hasTemplateTypes || nestedInfo.hasTemplateTypes
    }

    let info = SubtreeInfo(hasLinkKeys: hasLinkKeys, hasTemplateTypes: hasTemplateTypes)
    if let objectID {
      subtreeInfoCache[objectID] = (value as AnyObject, info)
    }
    return info
  }

  private func subtreeInfo(of value: Any, array: [Any]) -> SubtreeInfo {
    let objectID = referenceID(of: value)
    if let objectID, let cached = subtreeInfoCache[objectID] {
      return cached.info
    }

    var hasLinkKeys = false
    var hasTemplateTypes = false
    for element in array {
      let elementInfo = subtreeInfo(of: element)
      hasLinkKeys = hasLinkKeys || elementInfo.hasLinkKeys
      hasTemplateTypes = hasTemplateTypes || elementInfo.hasTemplateTypes
      if hasLinkKeys, hasTemplateTypes {
        break
      }
    }

    let info = SubtreeInfo(hasLinkKeys: hasLinkKeys, hasTemplateTypes: hasTemplateTypes)
    if let objectID {
      subtreeInfoCache[objectID] = (value as AnyObject, info)
    }
    return info
  }

  private func referenceID(of value: Any) -> ObjectIdentifier? {
    guard type(of: value) is AnyClass else {
      return nil
    }
    return ObjectIdentifier(value as AnyObject)
  }

  private func resolveInstanceLinks(
    in dictionary: [String: Any],
    linkSource: [String: Any],
    parameterNames: Set<String>
  ) -> [String: Any]? {
    var hasLinkKeys = false
    var resolvedLinkValues: [(key: String, value: Any)] = []
    for (linkKey, linkValue) in dictionary {
      guard linkKey.hasPrefix("$") else { continue }
      hasLinkKeys = true
      let key = String(linkKey.dropFirst())
      guard dictionary[key] == nil else { continue }
      guard !parameterNames.contains(key) else { continue }
      guard let linkName = linkValue as? String else { continue }
      guard let value = linkSource[linkName] else { continue }
      resolvedLinkValues.append((key, value))
    }

    var injectedParameters: [(key: String, value: Any)] = []
    for paramName in parameterNames {
      if let value = linkSource[paramName] {
        injectedParameters.append((paramName, value))
      }
    }

    guard hasLinkKeys || !injectedParameters.isEmpty else {
      return nil
    }

    var result = dictionary
    if hasLinkKeys {
      for key in dictionary.keys where key.hasPrefix("$") {
        result.removeValue(forKey: key)
      }
    }
    for (key, value) in resolvedLinkValues {
      result[key] = value
    }
    for (key, value) in injectedParameters {
      result[key] = value
    }
    return result
  }

  private func parameterNames(
    ofTemplateNamed templateName: TemplateName,
    resolvedTemplate: [String: Any]
  ) -> Set<String> {
    if let cachedNames = templateParameterNamesCache[templateName] {
      return cachedNames
    }
    let names = collectParameterNames(from: resolvedTemplate)
    templateParameterNamesCache[templateName] = names
    return names
  }

  private func collectParameterNames(from dict: [String: Any]) -> Set<String> {
    var visited = Set<TemplateName>()
    return collectParameterNames(from: dict, excludingKeys: [], visited: &visited)
  }

  private func collectParameterNames(
    from dict: [String: Any],
    excludingKeys: Set<String>
  ) -> Set<String> {
    var visited = Set<TemplateName>()
    return collectParameterNames(from: dict, excludingKeys: excludingKeys, visited: &visited)
  }

  private func collectParameterNames(
    from dict: [String: Any],
    excludingKeys: Set<String> = [],
    visited: inout Set<TemplateName>
  ) -> Set<String> {
    var names = Set<String>()
    for (key, value) in dict {
      if key.hasPrefix("$"), let name = value as? String {
        names.insert(name)
      }
      guard !excludingKeys.contains(key) else { continue }
      if let nestedDict = value as? [String: Any] {
        let info = subtreeInfo(of: value, dict: nestedDict)
        guard info.hasLinkKeys || info.hasTemplateTypes else { continue }
        names.formUnion(collectParameterNamesResolvingTemplates(
          from: nestedDict, visited: &visited
        ))
      }
      if let array = value as? [Any] {
        let info = subtreeInfo(of: value, array: array)
        guard info.hasLinkKeys || info.hasTemplateTypes else { continue }
        for element in array {
          if let elementDict = element as? [String: Any] {
            names.formUnion(collectParameterNamesResolvingTemplates(
              from: elementDict, visited: &visited
            ))
          }
        }
      }
    }
    return names
  }

  private func collectParameterNamesResolvingTemplates(
    from dict: [String: Any],
    visited: inout Set<TemplateName>
  ) -> Set<String> {
    if let type = dict["type"] as? String,
       !visited.contains(type),
       templates[type] != nil,
       let resolvedTemplate = resolveTemplate(named: type).value {
      visited.insert(type)
      var names = collectParameterNames(from: dict, visited: &visited)
      names.formUnion(collectParameterNames(from: resolvedTemplate, visited: &visited))
      return names
    }
    return collectParameterNames(from: dict, visited: &visited)
  }
}

private func normalizedErrors(
  from errors: NonEmptyArray<DeserializationError>?
) -> NonEmptyArray<DeserializationError> {
  errors ?? NonEmptyArray(.generic)
}
