import Foundation
import Serialization
import VGSL

struct UntypedDivTemplateResolver {
  private enum Origin {
    case instance
    case template
  }

  private struct OriginValue {
    let value: Any
    let origin: Origin
  }

  private let templates: [TemplateName: Any]
  private let templateToType: [TemplateName: String]
  private var resolvedTemplateCache: [TemplateName: [String: Any]] = [:]
  private var currentlyResolvingTemplates = Set<TemplateName>()

  init(templates: [String: Any]?) {
    let templates = templates ?? [:]
    self.templates = templates
    templateToType = calculateTemplateToType(in: templates)
  }

  mutating func resolve(card: [String: Any]) -> DeserializationResult<[String: Any]> {
    resolveDictionary(
      card,
      linkSource: card,
      origin: .instance
    )
  }

  private mutating func resolveDictionary(
    _ dictionary: [String: Any],
    linkSource: [String: Any],
    origin: Origin
  ) -> DeserializationResult<[String: Any]> {
    var values: [String: OriginValue] = [:]

    if let templateName = dictionary["type"] as? String, templates[templateName] != nil {
      let resolvedTemplate = resolveTemplate(named: templateName)
      guard let templateValue = resolvedTemplate.value else {
        return .failure(normalizedErrors(from: resolvedTemplate.errorsOrWarnings))
      }

      values = templateValue.mapValues { OriginValue(value: $0, origin: .template) }
      for (key, value) in dictionary {
        values[key] = OriginValue(value: value, origin: .instance)
      }
      values["type"] = OriginValue(
        value: templateToType[templateName] ?? templateName,
        origin: .template
      )
    } else {
      values = dictionary.mapValues { OriginValue(value: $0, origin: .instance) }
    }

    let currentLinkSource = origin == .instance ? dictionary : linkSource
    substituteLinks(values: &values, linkSource: currentLinkSource)

    var resolved: [String: Any] = [:]
    var errors: [DeserializationError] = []

    for (key, value) in values {
      guard !key.hasPrefix("$") else { continue }

      let childResult = resolveAny(
        value.value,
        linkSource: currentLinkSource,
        origin: value.origin
      )
      if let resolvedChild = childResult.value {
        resolved[key] = resolvedChild
      }
      if let childErrors = childResult.errorsOrWarnings {
        errors.append(contentsOf: childErrors.map { .nestedObjectError(field: key, error: $0) })
      }
    }

    if let warnings = NonEmptyArray(errors) {
      return .partialSuccess(resolved, warnings: warnings)
    }
    return .success(resolved)
  }

  private mutating func resolveArray(
    _ array: [Any],
    linkSource: [String: Any],
    origin: Origin
  ) -> DeserializationResult<[Any]> {
    var result: [Any] = []
    var errors: [DeserializationError] = []
    result.reserveCapacity(array.count)

    for index in array.indices {
      let child = array[index]
      let childResult: DeserializationResult<Any>
      if let dictionary = child as? [String: Any] {
        let childLinkSource = origin == .instance ? dictionary : linkSource
        childResult = resolveDictionary(
          dictionary,
          linkSource: childLinkSource,
          origin: origin == .instance ? .instance : .template
        ).map { $0 as Any }
      } else if let nestedArray = child as? [Any] {
        childResult = resolveArray(
          nestedArray,
          linkSource: linkSource,
          origin: origin
        ).map { $0 as Any }
      } else {
        childResult = .success(child)
      }

      if let value = childResult.value {
        result.append(value)
      }
      if let childErrors = childResult.errorsOrWarnings {
        errors
          .append(contentsOf: childErrors.map { .nestedObjectError(field: "\(index)", error: $0) })
      }
    }

    if let warnings = NonEmptyArray(errors) {
      return .partialSuccess(result, warnings: warnings)
    }
    return .success(result)
  }

  private mutating func resolveAny(
    _ value: Any,
    linkSource: [String: Any],
    origin: Origin
  ) -> DeserializationResult<Any> {
    if let dictionary = value as? [String: Any] {
      let childLinkSource = origin == .instance ? dictionary : linkSource
      return resolveDictionary(
        dictionary,
        linkSource: childLinkSource,
        origin: origin == .instance ? .instance : .template
      ).map { $0 as Any }
    }
    if let array = value as? [Any] {
      return resolveArray(array, linkSource: linkSource, origin: origin).map { $0 as Any }
    }
    return .success(value)
  }

  private mutating func resolveTemplate(named templateName: TemplateName)
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

  private func substituteLinks(values: inout [String: OriginValue], linkSource: [String: Any]) {
    let linkValues = values.filter { $0.key.hasPrefix("$") }
    for (linkKey, linkValue) in linkValues {
      let key = String(linkKey.dropFirst())
      guard values[key] == nil else { continue }
      guard let linkName = linkValue.value as? String else { continue }
      guard let value = linkSource[linkName] else { continue }
      values[key] = OriginValue(value: value, origin: .instance)
    }
  }
}

private func normalizedErrors(
  from errors: NonEmptyArray<DeserializationError>?
) -> NonEmptyArray<DeserializationError> {
  errors ?? NonEmptyArray(.generic)
}

extension DeserializationResult {
  fileprivate func map<U>(_ transform: (T) -> U) -> DeserializationResult<U> {
    switch self {
    case let .success(value):
      .success(transform(value))
    case let .partialSuccess(value, warnings):
      .partialSuccess(transform(value), warnings: warnings)
    case let .failure(errors):
      .failure(errors)
    case .noValue:
      .noValue
    }
  }
}
